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

import com.questhelper.questjournal.TooltipRenderer.SemanticHit;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * Lays out and renders selected-quest objectives and detail sections.
 */
final class DetailRenderer
{
	private static final int DETAIL_LINE_HEIGHT = 16;
	private static final int OBJECTIVE_GLYPH_SIZE = 14;

	private final JournalOverlay overlay;
	private final ChromeRenderer chromeRenderer;

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	static final class LayoutContext
	{
		private final Graphics2D graphics;
		private final Rectangle content;
		private final int scroll;
		private final boolean draw;
		private final List<SemanticHit> semanticHits;
		private final Rectangle semanticClip;
		private final boolean wikiLinksEnabled;

		static LayoutContext measure(Graphics2D graphics, Rectangle content)
		{
			return new LayoutContext(
				graphics,
				content,
				0,
				false,
				null,
				null,
				false);
		}

		static LayoutContext render(
			Graphics2D graphics,
			Rectangle content,
			int scroll,
			List<SemanticHit> semanticHits,
			Rectangle semanticClip,
			boolean wikiLinksEnabled)
		{
			return new LayoutContext(
				graphics,
				content,
				scroll,
				true,
				semanticHits,
				semanticClip,
				wikiLinksEnabled);
		}

		private LayoutContext withContent(Rectangle content)
		{
			return new LayoutContext(
				graphics,
				content,
				scroll,
				draw,
				semanticHits,
				semanticClip,
				wikiLinksEnabled);
		}
	}

	DetailRenderer(
		JournalOverlay overlay,
		ChromeRenderer chromeRenderer)
	{
		this.overlay = Objects.requireNonNull(overlay, "overlay");
		this.chromeRenderer = Objects.requireNonNull(
			chromeRenderer,
			"chromeRenderer");
	}

	int layoutDetailContent(
		LayoutContext context,
		JournalSnapshot.SelectedQuest quest)
	{
		int cursor = 4;
		boolean hasRequirements = hasRequirementSections(quest);
		boolean hasSupplemental = hasSupplementalSections(quest);
		if (hasRequirements)
		{
			cursor = layoutRequirements(
				context,
				quest,
				cursor);
		}
		if (!hasSupplemental)
		{
			return cursor + 6;
		}
		cursor = JournalOverlay.sectionStartCursor(cursor, hasRequirements);
		return cursor + layoutSupplementalDetailContent(
			context.withContent(new Rectangle(
				context.content.x,
				context.content.y + cursor,
				context.content.width,
				context.content.height)),
			quest);
	}

	static boolean hasRequirementSections(JournalSnapshot.SelectedQuest quest)
	{
		return !quest.getEnemies().isEmpty() || !quest.getRequirements().isEmpty();
	}

	static boolean hasSupplementalSections(JournalSnapshot.SelectedQuest quest)
	{
		return !quest.getRecommendations().isEmpty()
			|| !quest.getRewards().isEmpty()
			|| !quest.getNotes().isEmpty();
	}

	int layoutRequirements(
		LayoutContext context,
		JournalSnapshot.SelectedQuest quest,
		int cursor)
	{
		boolean hasPreviousSection = false;
		if (!quest.getEnemies().isEmpty())
		{
			cursor = drawSectionHeading(
				context,
				cursor,
				"ENEMIES TO DEFEAT");
			cursor = layoutEnemyList(
				context,
				cursor,
				quest.getEnemies());
			hasPreviousSection = true;
		}
		if (!quest.getRequirements().isEmpty())
		{
			cursor = JournalOverlay.sectionStartCursor(
				cursor,
				hasPreviousSection);
			cursor = drawSectionHeading(
				context,
				cursor,
				"REQUIREMENTS");
			cursor = layoutRequirementList(
				context,
				cursor,
				orderedRequirements(quest.getRequirements()));
		}
		return cursor;
	}

	int layoutSupplementalDetailContent(
		LayoutContext context,
		JournalSnapshot.SelectedQuest quest)
	{
		int cursor = 0;
		boolean hasPreviousSection = false;

		if (!quest.getRecommendations().isEmpty())
		{
			cursor = JournalOverlay.sectionStartCursor(
				cursor,
				hasPreviousSection);
			cursor = layoutRecommendations(
				context,
				quest,
				cursor);
			hasPreviousSection = true;
		}

		if (!quest.getRewards().isEmpty())
		{
			cursor = JournalOverlay.sectionStartCursor(
				cursor,
				hasPreviousSection);
			cursor = layoutRewards(
				context,
				quest,
				cursor);
			hasPreviousSection = true;
		}

		if (!quest.getNotes().isEmpty())
		{
			cursor = JournalOverlay.sectionStartCursor(
				cursor,
				hasPreviousSection);
			cursor = layoutNotes(
				context,
				quest,
				cursor);
		}
		return cursor + 6;
	}

	int layoutRecommendations(
		LayoutContext context,
		JournalSnapshot.SelectedQuest quest,
		int cursor)
	{
		cursor = drawSectionHeading(
			context,
			cursor,
			"RECOMMENDED");
		return layoutRequirementList(
			context,
			cursor,
			quest.getRecommendations());
	}

	int layoutRewards(
		LayoutContext context,
		JournalSnapshot.SelectedQuest quest,
		int cursor)
	{
		cursor = drawSectionHeading(
			context,
			cursor,
			"REWARDS");
		return layoutRewardList(
			context,
			cursor,
			quest.getRewards());
	}

	int layoutNotes(
		LayoutContext context,
		JournalSnapshot.SelectedQuest quest,
		int cursor)
	{
		cursor = drawSectionHeading(
			context,
			cursor,
			"NOTES");
		for (int index = 0; index < quest.getNotes().size(); index++)
		{
			if (index > 0)
			{
				cursor += JournalOverlay.NOTE_PARAGRAPH_GAP;
			}
			cursor = drawPlainText(
				context,
				cursor,
				quest.getNotes().get(index),
				JournalOverlay.TEXT);
		}
		return cursor;
	}

	int drawSectionHeading(
		LayoutContext context,
		int cursor,
		String title)
	{
		return drawSectionHeading(
			context,
			cursor,
			title,
			0);
	}

	int drawSectionHeading(
		LayoutContext context,
		int cursor,
		String title,
		int trailingReserve)
	{
		Graphics2D graphics = context.graphics;
		Rectangle content = context.content;
		Font font = JournalOverlay.sectionHeadingFont();
		graphics.setFont(font);
		int y = content.y + cursor - context.scroll;
		if (context.draw)
		{
			int headingX = content.x + JournalOverlay.PANEL_CONTENT_PADDING;
			int end = content.x + content.width
				- JournalOverlay.PANEL_CONTENT_PADDING
				- Math.max(0, trailingReserve);
			String visibleTitle = overlay.fitText(
				graphics.getFontMetrics(),
				title,
				Math.max(0, end - headingX));
			graphics.setColor(JournalOverlay.ACCENT);
			JournalOverlay.drawShadowedString(
				graphics,
				visibleTitle,
				headingX,
				y + JournalOverlay.SECTION_HEADING_BASELINE);
		}
		return cursor + JournalOverlay.SECTION_HEADING_HEIGHT;
	}

	int layoutObjectiveSection(
		LayoutContext context,
		int cursor,
		List<JournalSnapshot.Objective> objectives,
		Set<String> expandedChecklistIds,
		boolean alwaysExpandChecklists)
	{
		Rectangle content = context.content;
		JournalSnapshot.Objective objective = objectives.get(0);
		List<JournalSnapshot.Requirement> requirements =
			objective.getSectionRequirements();
		String checklistId = JournalOverlay.sectionChecklistId(
			objective.getSectionId());
		boolean hasRequirements =
			!requirements.isEmpty() && !objective.getSectionId().isEmpty();
		boolean expanded = hasRequirements
			&& (alwaysExpandChecklists
				|| expandedChecklistIds.contains(checklistId));
		boolean showChecklistToggle = hasRequirements && !alwaysExpandChecklists;
		int y = content.y + cursor - context.scroll;
		Rectangle checklistToggleBounds = showChecklistToggle
			? new Rectangle(
				content.x + content.width
					- JournalOverlay.PANEL_CONTENT_PADDING
					- JournalOverlay.CHECKLIST_TOGGLE_WIDTH,
				y + Math.max(
					0,
					(JournalOverlay.SECTION_HEADING_HEIGHT
						- JournalOverlay.CHECKLIST_TOGGLE_HEIGHT) / 2),
				JournalOverlay.CHECKLIST_TOGGLE_WIDTH,
				JournalOverlay.CHECKLIST_TOGGLE_HEIGHT)
			: new Rectangle();
		if (showChecklistToggle
			&& context.semanticHits != null
			&& context.semanticClip != null)
		{
			Rectangle clippedToggle = JournalGeometry.clip(
				checklistToggleBounds,
				context.semanticClip);
			if (!clippedToggle.isEmpty())
			{
				context.semanticHits.add(SemanticHit.checklistToggle(
					clippedToggle,
					checklistId));
			}
		}
		cursor = drawSectionHeading(
			context,
			cursor,
			objective.getSection().toUpperCase(Locale.ROOT),
			showChecklistToggle
				? JournalOverlay.CHECKLIST_TOGGLE_WIDTH
					+ JournalOverlay.CHECKLIST_TOGGLE_GAP
				: 0);
		if (context.draw && showChecklistToggle)
		{
			drawChecklistToggle(
				context.graphics,
				checklistToggleBounds,
				expanded);
		}
		if (expanded)
		{
			int indent = Math.min(
				JournalOverlay.CHECKLIST_CONTENT_INDENT,
				Math.max(0, content.width - 1));
			Rectangle requirementContent = new Rectangle(
				content.x + indent,
				content.y,
				Math.max(1, content.width - indent),
				content.height);
			cursor = layoutRequirementList(
				context.withContent(requirementContent),
				cursor,
				requirements);
		}
		return cursor;
	}

	int layoutObjectiveList(
		LayoutContext context,
		int cursor,
		List<JournalSnapshot.Objective> objectives,
		boolean questComplete)
	{
		int next = cursor;
		for (JournalSnapshot.Objective objective : objectives)
		{
			next = drawObjective(
				context,
				next,
				objective,
				questComplete);
		}
		return next;
	}

	int layoutRequirementList(
		LayoutContext context,
		int cursor,
		List<JournalSnapshot.Requirement> requirements)
	{
		if (requirements.isEmpty())
		{
			return cursor;
		}
		int next = cursor;
		for (JournalSnapshot.Requirement requirement : requirements)
		{
			next = drawRequirement(
				context,
				next,
				requirement);
		}
		return next;
	}

	private int drawRequirement(
		LayoutContext context,
		int cursor,
		JournalSnapshot.Requirement requirement)
	{
		Graphics2D graphics = context.graphics;
		Rectangle content = context.content;
		graphics.setFont(JournalOverlay.contentFont());
		FontMetrics metrics = graphics.getFontMetrics();
		boolean hasIcon = JournalOverlay.hasSemanticIcon(requirement.getIcon());
		int cardX = content.x;
		int cardWidth = Math.max(1, content.width);
		int iconX = cardX + JournalOverlay.PANEL_CONTENT_PADDING;
		int textX = hasIcon
			? iconX + JournalOverlay.DETAIL_ICON_SIZE
				+ JournalOverlay.CONTENT_ICON_TEXT_GAP
			: cardX + JournalOverlay.PANEL_CONTENT_PADDING;
		List<String> lines = overlay.wrapText(
			metrics,
			requirement.getText(),
			Math.max(
				1,
				cardX + cardWidth - JournalOverlay.PANEL_CONTENT_PADDING
					- textX));
		int height = JournalOverlay.detailCardHeight(
			lines.size(),
			DETAIL_LINE_HEIGHT,
			metrics.getHeight(),
			hasIcon ? JournalOverlay.DETAIL_ICON_SIZE : 0);
		int y = content.y + cursor - context.scroll;
		int textTop = JournalOverlay.centeredLinesTop(
			y,
			height,
			lines.size(),
			DETAIL_LINE_HEIGHT,
			metrics.getHeight());
		if (context.draw
			&& context.semanticHits != null
			&& context.semanticClip != null)
		{
			Rectangle clippedHit = semanticRequirementBounds(
				content,
				y,
				height,
				context.semanticClip);
			boolean wikiLink = JournalOverlay.isMissingItemWikiLink(
				requirement,
				context.wikiLinksEnabled);
			List<String> tooltipBlocks =
				JournalOverlay.requirementTooltipBlocks(
					requirement,
					wikiLink);
			if (!clippedHit.isEmpty()
				&& (requirement.hasLinkedQuest()
					|| wikiLink
					|| !tooltipBlocks.isEmpty()))
			{
				context.semanticHits.add(new SemanticHit(
					clippedHit,
					requirement.getLinkedQuestId(),
					wikiLink ? requirement.getWikiUrl() : "",
					tooltipBlocks,
					requirement.hasLinkedQuest() || wikiLink
						? underlineBounds(
							metrics,
							lines,
							textX,
							textTop,
							DETAIL_LINE_HEIGHT,
							context.semanticClip)
						: Collections.emptyList(),
					JournalOverlay.requirementUnderlineColor(requirement)));
			}
		}
		if (context.draw)
		{
			Color color = JournalOverlay.requirementColor(requirement);
			if (hasIcon)
			{
				overlay.drawIdentityIcon(
					graphics,
					requirement.getIcon(),
					JournalOverlay.detailIconBounds(iconX, y, height));
			}
			overlay.drawLinesAt(
				graphics,
				lines,
				textX,
				textTop,
				DETAIL_LINE_HEIGHT,
				color);
		}
		return cursor + height;
	}

	private int drawObjective(
		LayoutContext context,
		int cursor,
		JournalSnapshot.Objective objective,
		boolean questComplete)
	{
		Graphics2D graphics = context.graphics;
		Rectangle content = context.content;
		Font font = JournalOverlay.contentFont();
		graphics.setFont(font);
		int iconX = content.x + JournalOverlay.PANEL_CONTENT_PADDING;
		int textX = iconX + OBJECTIVE_GLYPH_SIZE
			+ JournalOverlay.CONTENT_ICON_TEXT_GAP;
		int textWidth = Math.max(
			1,
			content.x + content.width
				- JournalOverlay.PANEL_CONTENT_PADDING
				- textX);
		List<String> lines = overlay.wrapText(
			graphics.getFontMetrics(),
			objective.getText(),
			textWidth);
		int height = JournalOverlay.detailCardHeight(
			lines.size(),
			DETAIL_LINE_HEIGHT,
			graphics.getFontMetrics().getHeight(),
			OBJECTIVE_GLYPH_SIZE);
		int y = content.y + cursor - context.scroll;
		FontMetrics metrics = graphics.getFontMetrics();
		int textTop = JournalOverlay.centeredLinesTop(
			y,
			height,
			lines.size(),
			DETAIL_LINE_HEIGHT,
			metrics.getHeight());
		if (context.draw)
		{
			boolean complete = questComplete
				|| objective.getState() == JournalSnapshot.ObjectiveState.COMPLETE;
			Rectangle glyphBounds = JournalOverlay.objectiveGlyphBounds(
				iconX,
				y,
				height,
				textTop,
				DETAIL_LINE_HEIGHT,
				complete);
			overlay.drawObjectiveGlyph(
				graphics,
				glyphBounds,
				objective,
				questComplete);
			overlay.drawLinesAt(
				graphics,
				lines,
				textX,
				textTop,
				DETAIL_LINE_HEIGHT,
				JournalOverlay.objectiveTextColor(
					objective,
					questComplete));
		}
		return cursor + height;
	}

	private void drawChecklistToggle(
		Graphics2D graphics,
		Rectangle bounds,
		boolean expanded)
	{
		if (bounds.isEmpty())
		{
			return;
		}
		chromeRenderer.drawHeaderControlSkin(graphics, bounds, false);
		Font oldFont = graphics.getFont();
		graphics.setFont(JournalOverlay.compactSmallFont());
		FontMetrics metrics = graphics.getFontMetrics();
		graphics.setColor(Color.WHITE);
		JournalOverlay.drawShadowedString(
			graphics,
			"Checklist",
			bounds.x + 6,
			overlay.centeredTextBaseline(metrics, bounds));
		graphics.setFont(oldFont);

		Rectangle arrowBounds = new Rectangle(
			bounds.x + bounds.width - 12,
			bounds.y + bounds.height / 2 - 4,
			9,
			9);
		JournalOverlay.drawDisclosureArrow(
			graphics,
			arrowBounds,
			expanded,
			JournalOverlay.checklistToggleIconColor());
	}

	private int drawEnemy(
		LayoutContext context,
		int cursor,
		String text)
	{
		Graphics2D graphics = context.graphics;
		Rectangle content = context.content;
		graphics.setFont(JournalOverlay.contentFont());
		FontMetrics metrics = graphics.getFontMetrics();
		int iconSize = JournalOverlay.DETAIL_ICON_SIZE;
		int iconX = content.x + JournalOverlay.PANEL_CONTENT_PADDING;
		int textX = iconX + iconSize + JournalOverlay.CONTENT_ICON_TEXT_GAP;
		List<String> lines = overlay.wrapText(
			metrics,
			text,
			Math.max(
				1,
				content.x + content.width
					- JournalOverlay.PANEL_CONTENT_PADDING
					- textX));
		int lineBlockHeight = lines.isEmpty()
			? 0
			: metrics.getHeight() + (lines.size() - 1) * DETAIL_LINE_HEIGHT;
		int height = Math.max(iconSize, lineBlockHeight) + 4;
		int y = content.y + cursor - context.scroll;
		if (context.draw)
		{
			overlay.drawEnemyIcon(
				graphics,
				JournalOverlay.detailIconBounds(iconX, y, height));
			overlay.drawLinesAt(
				graphics,
				lines,
				textX,
				JournalOverlay.centeredLinesTop(
					y,
					height,
					lines.size(),
					DETAIL_LINE_HEIGHT,
					metrics.getHeight()),
				DETAIL_LINE_HEIGHT,
				JournalOverlay.TEXT);
		}
		return cursor + height;
	}

	private int layoutEnemyList(
		LayoutContext context,
		int cursor,
		List<String> enemies)
	{
		int next = cursor;
		for (String enemy : enemies)
		{
			next = drawEnemy(
				context,
				next,
				enemy);
		}
		return next;
	}

	private int drawPlainText(
		LayoutContext context,
		int cursor,
		String text,
		Color color)
	{
		Graphics2D graphics = context.graphics;
		Rectangle content = context.content;
		graphics.setFont(JournalOverlay.contentFont());
		List<String> lines = overlay.wrapText(
			graphics.getFontMetrics(),
			text,
			Math.max(
				1,
				content.width - JournalOverlay.PANEL_CONTENT_PADDING * 2));
		int height = Math.max(17, lines.size() * DETAIL_LINE_HEIGHT + 1);
		int y = content.y + cursor - context.scroll;
		if (context.draw)
		{
			overlay.drawLinesAt(
				graphics,
				lines,
				content.x + JournalOverlay.PANEL_CONTENT_PADDING,
				y,
				DETAIL_LINE_HEIGHT,
				color);
		}
		return cursor + height + 1;
	}

	private int drawReward(
		LayoutContext context,
		int cursor,
		JournalSnapshot.Reward reward)
	{
		Graphics2D graphics = context.graphics;
		Rectangle content = context.content;
		Font detailFont = JournalOverlay.contentFont();
		graphics.setFont(detailFont);
		boolean hasIcon = JournalOverlay.hasSemanticIcon(reward.getIcon());
		int iconX = content.x + JournalOverlay.PANEL_CONTENT_PADDING;
		int textX = hasIcon
			? iconX + JournalOverlay.DETAIL_ICON_SIZE
				+ JournalOverlay.CONTENT_ICON_TEXT_GAP
			: content.x + JournalOverlay.PANEL_CONTENT_PADDING;
		List<String> detailLines = overlay.wrapText(
			graphics.getFontMetrics(),
			reward.getDetail(),
			Math.max(
				1,
				content.x + content.width
					- JournalOverlay.PANEL_CONTENT_PADDING
					- textX));
		int height = Math.max(
			hasIcon ? JournalOverlay.DETAIL_ICON_SIZE + 6 : 0,
			detailLines.size() * DETAIL_LINE_HEIGHT + 7);
		int y = content.y + cursor - context.scroll;
		if (context.draw)
		{
			if (hasIcon)
			{
				overlay.drawIdentityIcon(
					graphics,
					reward.getIcon(),
					JournalOverlay.detailIconBounds(iconX, y, height));
			}
			graphics.setFont(detailFont);
			overlay.drawLinesAt(
				graphics,
				detailLines,
				textX,
				y + 3,
				DETAIL_LINE_HEIGHT,
				rewardTextColor());
		}
		return cursor + height;
	}

	private int layoutRewardList(
		LayoutContext context,
		int cursor,
		List<JournalSnapshot.Reward> rewards)
	{
		int next = cursor;
		for (JournalSnapshot.Reward reward : rewards)
		{
			next = drawReward(
				context,
				next,
				reward);
		}
		return next;
	}

	static Rectangle semanticRequirementBounds(
		Rectangle content,
		int y,
		int height,
		Rectangle clip)
	{
		Rectangle hitBounds = new Rectangle(
			content.x,
			y,
			Math.max(0, content.width),
			Math.max(0, height));
		return JournalGeometry.clip(hitBounds, clip);
	}

	static List<JournalSnapshot.Requirement> orderedRequirements(
		List<JournalSnapshot.Requirement> requirements)
	{
		List<JournalSnapshot.Requirement> ordered =
			new ArrayList<>(requirements.size());
		for (JournalSnapshot.Requirement requirement : requirements)
		{
			if (requirement.hasLinkedQuest())
			{
				ordered.add(requirement);
			}
		}
		for (JournalSnapshot.Requirement requirement : requirements)
		{
			if (!requirement.hasLinkedQuest())
			{
				ordered.add(requirement);
			}
		}
		return Collections.unmodifiableList(ordered);
	}

	static Color rewardTextColor()
	{
		return JournalOverlay.TEXT;
	}

	private static List<Rectangle> underlineBounds(
		FontMetrics metrics,
		List<String> lines,
		int x,
		int top,
		int lineHeight,
		Rectangle clip)
	{
		List<Rectangle> underlines = new ArrayList<>(lines.size());
		for (int index = 0; index < lines.size(); index++)
		{
			String line = lines.get(index);
			if (line.isEmpty())
			{
				continue;
			}
			int baseline = top + metrics.getAscent() + index * lineHeight;
			Rectangle underline = JournalGeometry.clip(
				new Rectangle(
					x,
					baseline + 1,
					Math.max(0, metrics.stringWidth(line)),
					1),
				clip);
			if (!underline.isEmpty())
			{
				underlines.add(underline);
			}
		}
		return Collections.unmodifiableList(underlines);
	}
}
