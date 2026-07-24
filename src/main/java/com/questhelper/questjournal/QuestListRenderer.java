/*
 * Copyright (c) 2026, nanopink
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.questhelper.questjournal;

import com.questhelper.questjournal.TooltipRenderer.MarkerTooltipHit;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Renders quest-list rows and caches width-dependent layouts. */
final class QuestListRenderer
{
	private static final Color ACTIVE_ROW_BACKGROUND = new Color(0xFF, 0x98, 0x1F, 0x24);
	private static final Color TRANSPARENT = new Color(0, 0, 0, 0);
	private static final Color DIFFICULTY_NOVICE = new Color(0x15A605);
	private static final Color DIFFICULTY_INTERMEDIATE = new Color(0xE1DA11);
	private static final Color DIFFICULTY_EXPERIENCED = new Color(0xF78B01);
	private static final Color DIFFICULTY_MASTER = new Color(0xA80000);
	private static final Color DIFFICULTY_GRANDMASTER = new Color(0x8100A1);
	private static final Color DIFFICULTY_SPECIAL = new Color(0x091EA9);
	private static final Color DIFFICULTY_DOT_OUTLINE = Color.BLACK;

	private final JournalOverlay overlay;
	private final QuestJournalManager manager;
	private final ChromeRenderer chromeRenderer;
	private QuestListLayoutCache layoutCache;

	QuestListRenderer(
		JournalOverlay overlay,
		QuestJournalManager manager,
		ChromeRenderer chromeRenderer)
	{
		this.overlay = Objects.requireNonNull(overlay, "overlay");
		this.manager = manager;
		this.chromeRenderer = Objects.requireNonNull(
			chromeRenderer,
			"chromeRenderer");
	}

	RenderResult draw(
		Graphics2D graphics,
		JournalGeometry geometry,
		JournalSnapshot snapshot,
		Point pointer,
		JournalPanelAssets assets,
		List<MarkerTooltipHit> markerTooltipHits,
		Rectangle popupOcclusion,
		int requestedScrollOffset)
	{
		Rectangle content = geometry.questListContentBounds();
		List<JournalSnapshot.QuestListItem> quests = snapshot.getQuests();
		JournalSnapshot.SelectedQuest selectedQuest = snapshot.getSelectedQuest();
		String selectedId = selectedQuest == null ? "" : selectedQuest.getOverview().getId();
		JournalSnapshot.ActiveQuest activeQuest = snapshot.getActiveQuest();
		String activeId = activeQuest == null ? "" : activeQuest.getId();
		Set<String> starredQuestIds = manager == null
			? Collections.emptySet()
			: manager.getStarredQuestIds();
		Rectangle scrollContent = questListScrollBounds(content);
		Font titleFont = JournalOverlay.questListFont();
		graphics.setFont(titleFont);
		FontMetrics titleMetrics = graphics.getFontMetrics();

		Rectangle rowContent = JournalOverlay.scrollBodyBounds(scrollContent, false);
		QuestListRowsLayout rowsLayout = questListRowsLayout(
			titleMetrics,
			quests,
			rowContent.width,
			assets,
			activeId,
			starredQuestIds);
		int totalHeight = rowsLayout.totalHeight;
		boolean scrollbarVisible = totalHeight > rowContent.height;
		if (scrollbarVisible)
		{
			rowContent = JournalOverlay.scrollBodyBounds(scrollContent, true);
			rowsLayout = questListRowsLayout(
				titleMetrics,
				quests,
				rowContent.width,
				assets,
				activeId,
				starredQuestIds);
			totalHeight = rowsLayout.totalHeight;
		}
		int maximumScroll = Math.max(0, totalHeight - Math.max(0, rowContent.height));
		int scrollOffset = JournalOverlay.clamp(
			requestedScrollOffset,
			0,
			maximumScroll);
		Rectangle progressFooter = questProgressFooterBounds(content);
		if (scrollbarVisible)
		{
			progressFooter.width = Math.min(progressFooter.width, rowContent.width);
		}
		drawQuestProgressFooter(graphics, progressFooter, snapshot.getQuestProgress());

		List<RowHit> rowHits = new ArrayList<>();
		Graphics2D pane = overlay.clippedGraphics(graphics, rowContent);
		try
		{
			pane.setFont(titleFont);
			if (quests.isEmpty())
			{
				overlay.drawCenteredEmptyMessage(
					pane,
					rowContent,
					JournalOverlay.emptyQuestListMessage());
			}
			int rowOffset = 0;
			for (int index = 0; index < quests.size(); index++)
			{
				QuestListRowLayout rowLayout = rowsLayout.rows.get(index);
				int rowHeight = rowLayout.height;
				int y = rowContent.y + rowOffset - scrollOffset;
				rowOffset += rowHeight;
				if ((long) y + rowHeight <= rowContent.y)
				{
					continue;
				}
				if (y >= (long) rowContent.y + rowContent.height)
				{
					break;
				}

				JournalSnapshot.QuestListItem quest = quests.get(index);
				boolean active = rowLayout.active;
				int titleX = questListTitleTextX(rowContent, rowLayout.typeMarker);
				List<String> titleLines = rowLayout.titleLines;
				Rectangle row = new Rectangle(rowContent.x, y, rowContent.width, rowHeight);
				Rectangle hit = JournalGeometry.clip(row, rowContent);
				if (hit.isEmpty())
				{
					continue;
				}
				rowHits.add(new RowHit(quest.getId(), hit));

				boolean selected = selectedId.equals(quest.getId());
				boolean hovered = questRowHovered(hit, pointer, popupOcclusion);
				JournalOverlay.drawListElementSurface(
					pane,
					row,
					questRowBackground(selected, hovered));

				Color stateColor = questRowTextColor(quest.getState(), hovered);
				if (rowLayout.typeMarker)
				{
					Rectangle typeMarker = questListTypeMarkerBounds(row);
					drawQuestListTypeMarker(pane, typeMarker, quest, assets);
					markerTooltipHits.add(new MarkerTooltipHit(
						JournalGeometry.clip(typeMarker, rowContent),
						questListMarkerTooltip(quest),
						false));
				}
				int titleTop = JournalOverlay.centeredLinesTop(
					row.y,
					row.height,
					titleLines.size(),
					titleMetrics.getHeight(),
					titleMetrics.getHeight());
				overlay.drawQuestTitleLinesAt(
					pane,
					titleLines,
					titleX,
					titleTop,
					titleMetrics.getHeight(),
					stateColor);
				if (rowLayout.starred)
				{
					Rectangle starMarker = questListStarMarkerBounds(row, active);
					ControlsRenderer.drawStarGlyph(
						pane,
						starMarker,
						FilterRenderer.filterActiveOptionMarkerColor());
					markerTooltipHits.add(new MarkerTooltipHit(
						JournalGeometry.clip(starMarker, rowContent),
						"Starred quest",
						false));
				}
				if (active)
				{
					Rectangle activeMarker = questListActiveMarkerBounds(row);
					drawActiveQuestListMarker(pane, activeMarker, assets);
					markerTooltipHits.add(new MarkerTooltipHit(
						JournalGeometry.clip(activeMarker, rowContent),
						"Active quest",
						false));
				}
			}
		}
		finally
		{
			pane.dispose();
		}
		chromeRenderer.drawScrollBar(
			graphics,
			scrollContent,
			totalHeight,
			scrollOffset,
			maximumScroll);

		return new RenderResult(
			rowHits,
			scrollContent,
			scrollOffset,
			maximumScroll);
	}

	static boolean questRowHovered(
		Rectangle row,
		Point pointer,
		Rectangle popupOcclusion)
	{
		return row != null
			&& pointer != null
			&& (popupOcclusion == null || !popupOcclusion.contains(pointer))
			&& row.contains(pointer);
	}

	private QuestListRowsLayout questListRowsLayout(
		FontMetrics metrics,
		List<JournalSnapshot.QuestListItem> quests,
		int rowWidth,
		JournalPanelAssets assets,
		String activeId,
		Set<String> starredQuestIds)
	{
		QuestListLayoutCache cache = layoutCache;
		if (cache == null
			|| !cache.adoptEquivalentQuests(quests)
			|| cache.assets != assets
			|| !cache.activeId.equals(activeId)
			|| !cache.starredQuestIds.equals(starredQuestIds)
			|| !cache.font.equals(metrics.getFont()))
		{
			cache = new QuestListLayoutCache(
				quests,
				assets,
				metrics.getFont(),
				activeId,
				starredQuestIds);
			layoutCache = cache;
		}
		QuestListRowsLayout cached = cache.rowsByWidth.get(rowWidth);
		if (cached != null)
		{
			return cached;
		}

		long height = 0L;
		List<QuestListRowLayout> rows = new ArrayList<>(quests.size());
		Rectangle row = new Rectangle(
			0,
			0,
			Math.max(0, rowWidth),
			JournalOverlay.QUEST_ROW_HEIGHT);
		for (JournalSnapshot.QuestListItem quest : quests)
		{
			boolean active = activeId.equals(quest.getId());
			boolean typeMarker = questListMarkerAvailable(quest, assets);
			boolean starred = starredQuestIds.contains(quest.getId());
			int titleX = questListTitleTextX(row, typeMarker);
			int titleWidth = Math.max(
				1,
				questListTitleRight(row, starred, active) - titleX);
			List<String> titleLines = Collections.unmodifiableList(
				overlay.wrapText(metrics, quest.getTitle(), titleWidth));
			int rowHeight = questRowHeight(metrics, titleLines.size());
			rows.add(new QuestListRowLayout(
				active,
				typeMarker,
				starred,
				titleLines,
				rowHeight));
			height += rowHeight;
			if (height >= Integer.MAX_VALUE)
			{
				height = Integer.MAX_VALUE;
			}
		}
		QuestListRowsLayout layout = new QuestListRowsLayout(rows, (int) height);
		if (cache.rowsByWidth.size() >= 4)
		{
			cache.rowsByWidth.clear();
		}
		cache.rowsByWidth.put(rowWidth, layout);
		return layout;
	}

	static int questRowHeight(FontMetrics metrics, int lineCount)
	{
		int lines = Math.max(1, lineCount);
		int textHeight = metrics == null ? 0 : Math.max(0, metrics.getHeight()) * lines;
		return Math.max(JournalOverlay.QUEST_ROW_HEIGHT, textHeight + 4);
	}

	static Rectangle questListScrollBounds(Rectangle content)
	{
		Rectangle bounds = new Rectangle(content);
		int footerHeight = Math.min(
			JournalOverlay.QUEST_PROGRESS_FOOTER_HEIGHT,
			bounds.height);
		int gap = Math.min(
			JournalOverlay.QUEST_PROGRESS_FOOTER_GAP,
			Math.max(0, bounds.height - footerHeight));
		bounds.height = Math.max(0, bounds.height - footerHeight - gap);
		return bounds;
	}

	static Rectangle questProgressFooterBounds(Rectangle content)
	{
		int footerHeight = Math.min(
			JournalOverlay.QUEST_PROGRESS_FOOTER_HEIGHT,
			Math.max(0, content.height));
		return new Rectangle(
			content.x,
			content.y + Math.max(0, content.height - footerHeight),
			Math.max(0, content.width),
			footerHeight);
	}

	private void drawQuestProgressFooter(
		Graphics2D graphics,
		Rectangle row,
		JournalSnapshot.QuestProgress progress)
	{
		if (row.isEmpty())
		{
			return;
		}
		Graphics2D footer = overlay.clippedGraphics(graphics, row);
		try
		{
			ControlsRenderer.drawQuestProgress(
				overlay,
				footer,
				row,
				progress);
		}
		finally
		{
			footer.dispose();
		}
	}

	static int questListTitleTextX(Rectangle row, boolean showTypeMarker)
	{
		return row.x + JournalOverlay.QUEST_LIST_CONTENT_PADDING + (showTypeMarker
			? JournalOverlay.QUEST_TYPE_ICON_SIZE
				+ JournalOverlay.QUEST_ROW_INLINE_ICON_GAP
			: 0);
	}

	static Rectangle questListTypeMarkerBounds(Rectangle row)
	{
		return new Rectangle(
			row.x + JournalOverlay.QUEST_LIST_CONTENT_PADDING,
			row.y + (row.height - JournalOverlay.QUEST_TYPE_ICON_SIZE) / 2,
			JournalOverlay.QUEST_TYPE_ICON_SIZE,
			JournalOverlay.QUEST_TYPE_ICON_SIZE);
	}

	static Rectangle questListStarMarkerBounds(Rectangle row, boolean active)
	{
		if (!active)
		{
			return new Rectangle(
				row.x + Math.max(
					0,
					row.width
						- JournalOverlay.QUEST_LIST_CONTENT_PADDING
						- JournalOverlay.QUEST_ROW_TRAILING_ICON_SIZE),
				row.y + (row.height - JournalOverlay.QUEST_ROW_TRAILING_ICON_SIZE) / 2,
				JournalOverlay.QUEST_ROW_TRAILING_ICON_SIZE,
				JournalOverlay.QUEST_ROW_TRAILING_ICON_SIZE);
		}
		Rectangle activeMarker = questListActiveMarkerBounds(row);
		return new Rectangle(
			activeMarker.x
				- JournalOverlay.QUEST_ROW_INLINE_ICON_GAP
				- JournalOverlay.QUEST_ROW_TRAILING_ICON_SIZE,
			row.y + (row.height - JournalOverlay.QUEST_ROW_TRAILING_ICON_SIZE) / 2,
			JournalOverlay.QUEST_ROW_TRAILING_ICON_SIZE,
			JournalOverlay.QUEST_ROW_TRAILING_ICON_SIZE);
	}

	static Rectangle questListActiveMarkerBounds(Rectangle row)
	{
		return new Rectangle(
			row.x + Math.max(
				0,
				row.width
					- JournalOverlay.QUEST_LIST_CONTENT_PADDING
					- JournalOverlay.ACTIVE_QUEST_LIST_ICON_SIZE),
			row.y + (row.height - JournalOverlay.ACTIVE_QUEST_LIST_ICON_SIZE) / 2,
			JournalOverlay.ACTIVE_QUEST_LIST_ICON_SIZE,
			JournalOverlay.ACTIVE_QUEST_LIST_ICON_SIZE);
	}

	static int questListTitleRight(Rectangle row, boolean starred, boolean active)
	{
		if (starred)
		{
			return questListStarMarkerBounds(row, active).x
				- JournalOverlay.QUEST_ROW_INLINE_ICON_GAP;
		}
		if (active)
		{
			return questListActiveMarkerBounds(row).x
				- JournalOverlay.QUEST_ROW_INLINE_ICON_GAP;
		}
		return row.x + Math.max(
			0,
			row.width - JournalOverlay.QUEST_LIST_CONTENT_PADDING);
	}

	static boolean questListMarkerAvailable(
		JournalSnapshot.QuestListItem quest,
		JournalPanelAssets assets)
	{
		return quest.getType() != JournalSnapshot.QuestType.QUEST
			&& FilterRenderer.questTypeMarkerImage(
				quest.getType(),
				assets) != null;
	}

	private void drawQuestListTypeMarker(
		Graphics2D graphics,
		Rectangle bounds,
		JournalSnapshot.QuestListItem quest,
		JournalPanelAssets assets)
	{
		BufferedImage image = FilterRenderer.questTypeMarkerImage(
			quest.getType(),
			assets);
		if (image != null)
		{
			if (quest.getType() == JournalSnapshot.QuestType.SKILL)
			{
				overlay.drawPixelArtCenteredImage(graphics, image, bounds);
			}
			else
			{
				overlay.drawOutlinedPixelArtCenteredImage(graphics, image, bounds);
			}
		}
	}

	private void drawActiveQuestListMarker(
		Graphics2D graphics,
		Rectangle bounds,
		JournalPanelAssets assets)
	{
		BufferedImage combatIcon = assets == null ? null : assets.combatIcon;
		if (combatIcon != null && overlay.hasVisiblePixels(combatIcon))
		{
			overlay.drawPixelArtRightAlignedImage(graphics, combatIcon, bounds);
		}
	}

	static void drawActiveQuestIcon(
		JournalOverlay overlay,
		Graphics2D graphics,
		Rectangle bounds,
		JournalPanelAssets assets)
	{
		BufferedImage combatIcon = assets == null ? null : assets.combatIcon;
		if (combatIcon != null && overlay.hasVisiblePixels(combatIcon))
		{
			overlay.drawPixelArtCenteredImage(graphics, combatIcon, bounds);
		}
	}

	static Rectangle difficultyDotBounds(int x, int baseline, int ascent, int descent)
	{
		int lineTop = baseline - ascent;
		int lineHeight = ascent + descent;
		return new Rectangle(
			x,
			lineTop + (lineHeight - JournalOverlay.DIFFICULTY_DOT_SIZE) / 2,
			JournalOverlay.DIFFICULTY_DOT_SIZE,
			JournalOverlay.DIFFICULTY_DOT_SIZE);
	}

	static Color difficultyDotColor(JournalSnapshot.QuestDifficulty difficulty)
	{
		if (difficulty == null)
		{
			return null;
		}
		switch (difficulty)
		{
			case NOVICE:
				return DIFFICULTY_NOVICE;
			case INTERMEDIATE:
				return DIFFICULTY_INTERMEDIATE;
			case EXPERIENCED:
				return DIFFICULTY_EXPERIENCED;
			case MASTER:
				return DIFFICULTY_MASTER;
			case GRANDMASTER:
				return DIFFICULTY_GRANDMASTER;
			case SPECIAL:
				return DIFFICULTY_SPECIAL;
			default:
				return null;
		}
	}

	static boolean isDiaryDifficulty(JournalSnapshot.QuestDifficulty difficulty)
	{
		return difficulty == JournalSnapshot.QuestDifficulty.EASY
			|| difficulty == JournalSnapshot.QuestDifficulty.MEDIUM
			|| difficulty == JournalSnapshot.QuestDifficulty.HARD
			|| difficulty == JournalSnapshot.QuestDifficulty.ELITE;
	}

	static String difficultyMarkerTooltip(JournalSnapshot.QuestDifficulty difficulty)
	{
		String label = FilterRenderer.difficultyLabel(difficulty);
		return isDiaryDifficulty(difficulty)
			? label + " achievement diary"
			: label + " difficulty";
	}

	static String questListMarkerTooltip(JournalSnapshot.QuestListItem quest)
	{
		if (quest.getType() != JournalSnapshot.QuestType.QUEST
			&& quest.getType() != JournalSnapshot.QuestType.ACHIEVEMENT_DIARY)
		{
			return FilterRenderer.typeLabel(quest.getType());
		}
		if (quest.getType() == JournalSnapshot.QuestType.ACHIEVEMENT_DIARY)
		{
			return isDiaryDifficulty(quest.getDifficulty())
				? difficultyMarkerTooltip(quest.getDifficulty())
				: "Achievement diary";
		}
		return difficultyMarkerTooltip(quest.getDifficulty());
	}

	static boolean selectedQuestShowsDifficultyDot(
		JournalSnapshot.QuestType type,
		JournalSnapshot.QuestDifficulty difficulty)
	{
		return type == JournalSnapshot.QuestType.QUEST
			&& difficultyDotColor(difficulty) != null;
	}

	static Color difficultyDotTopColor(JournalSnapshot.QuestDifficulty difficulty)
	{
		return blendColor(difficultyDotColor(difficulty), Color.WHITE, 12);
	}

	static Color difficultyDotBottomColor(JournalSnapshot.QuestDifficulty difficulty)
	{
		return blendColor(difficultyDotColor(difficulty), Color.BLACK, 18);
	}

	private static Color blendColor(Color base, Color overlay, int overlayPercent)
	{
		if (base == null)
		{
			return null;
		}
		int basePercent = 100 - overlayPercent;
		return new Color(
			(base.getRed() * basePercent + overlay.getRed() * overlayPercent + 50) / 100,
			(base.getGreen() * basePercent + overlay.getGreen() * overlayPercent + 50) / 100,
			(base.getBlue() * basePercent + overlay.getBlue() * overlayPercent + 50) / 100);
	}

	static void drawDifficultyDot(
		Graphics2D graphics,
		Rectangle bounds,
		JournalSnapshot.QuestDifficulty difficulty)
	{
		Color top = difficultyDotTopColor(difficulty);
		Color bottom = difficultyDotBottomColor(difficulty);
		if (graphics == null
			|| bounds == null
			|| bounds.isEmpty()
			|| top == null
			|| bottom == null)
		{
			return;
		}

		Paint oldPaint = graphics.getPaint();
		Stroke oldStroke = graphics.getStroke();
		Object oldAntialiasing = graphics.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		graphics.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setPaint(new GradientPaint(
			bounds.x,
			bounds.y,
			top,
			bounds.x,
			bounds.y + Math.max(1, bounds.height - 1),
			bottom));
		graphics.fillOval(bounds.x, bounds.y, bounds.width, bounds.height);
		graphics.setPaint(DIFFICULTY_DOT_OUTLINE);
		graphics.setStroke(new BasicStroke(1f));
		graphics.drawOval(
			bounds.x,
			bounds.y,
			Math.max(1, bounds.width - 1),
			Math.max(1, bounds.height - 1));
		graphics.setStroke(oldStroke);
		graphics.setPaint(oldPaint);
		graphics.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING,
			oldAntialiasing == null
				? RenderingHints.VALUE_ANTIALIAS_DEFAULT
				: oldAntialiasing);
	}

	static Color questRowBackground(boolean selected, boolean hovered)
	{
		return selected ? ACTIVE_ROW_BACKGROUND : TRANSPARENT;
	}

	static Color questRowTextColor(JournalSnapshot.QuestState state, boolean hovered)
	{
		return hovered ? Color.WHITE : JournalOverlay.questStateColor(state);
	}

	void clear()
	{
		layoutCache = null;
	}

	static final class RowHit
	{
		final String questId;
		private final Rectangle bounds;

		private RowHit(String questId, Rectangle bounds)
		{
			this.questId = questId;
			this.bounds = new Rectangle(bounds);
		}

		boolean contains(Point point)
		{
			return point != null && bounds.contains(point);
		}
	}

	static final class RenderResult
	{
		final List<RowHit> rows;
		final Rectangle scrollBounds;
		final int scrollOffset;
		final int maximumScroll;

		private RenderResult(
			List<RowHit> rows,
			Rectangle scrollBounds,
			int scrollOffset,
			int maximumScroll)
		{
			this.rows = Collections.unmodifiableList(new ArrayList<>(rows));
			this.scrollBounds = new Rectangle(scrollBounds);
			this.scrollOffset = Math.max(0, scrollOffset);
			this.maximumScroll = Math.max(0, maximumScroll);
		}
	}

	private static final class QuestListLayoutCache
	{
		private List<JournalSnapshot.QuestListItem> quests;
		private final JournalPanelAssets assets;
		private final Font font;
		private final String activeId;
		private final Set<String> starredQuestIds;
		private final Map<Integer, QuestListRowsLayout> rowsByWidth = new HashMap<>();

		private QuestListLayoutCache(
			List<JournalSnapshot.QuestListItem> quests,
			JournalPanelAssets assets,
			Font font,
			String activeId,
			Set<String> starredQuestIds)
		{
			this.quests = quests;
			this.assets = assets;
			this.font = font;
			this.activeId = activeId;
			this.starredQuestIds = Collections.unmodifiableSet(
				new java.util.LinkedHashSet<>(starredQuestIds));
		}

		private boolean adoptEquivalentQuests(
			List<JournalSnapshot.QuestListItem> candidateQuests)
		{
			if (quests == candidateQuests)
			{
				return true;
			}
			if (!quests.equals(candidateQuests))
			{
				return false;
			}
			quests = candidateQuests;
			return true;
		}
	}

	private static final class QuestListRowsLayout
	{
		private final List<QuestListRowLayout> rows;
		private final int totalHeight;

		private QuestListRowsLayout(
			List<QuestListRowLayout> rows,
			int totalHeight)
		{
			this.rows = Collections.unmodifiableList(new ArrayList<>(rows));
			this.totalHeight = Math.max(0, totalHeight);
		}
	}

	private static final class QuestListRowLayout
	{
		private final boolean active;
		private final boolean typeMarker;
		private final boolean starred;
		private final List<String> titleLines;
		private final int height;

		private QuestListRowLayout(
			boolean active,
			boolean typeMarker,
			boolean starred,
			List<String> titleLines,
			int height)
		{
			this.active = active;
			this.typeMarker = typeMarker;
			this.starred = starred;
			this.titleLines = titleLines;
			this.height = Math.max(1, height);
		}
	}
}
