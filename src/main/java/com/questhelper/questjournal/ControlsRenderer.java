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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Renders journal controls and computes their hit areas. */
final class ControlsRenderer
{
	private final JournalOverlay overlay;
	private final QuestJournalManager manager;
	private final FilterRenderer filterRenderer;
	private final ChromeRenderer chromeRenderer;

	ControlsRenderer(
		JournalOverlay overlay,
		QuestJournalManager manager,
		FilterRenderer filterRenderer,
		ChromeRenderer chromeRenderer)
	{
		this.overlay = Objects.requireNonNull(overlay, "overlay");
		this.manager = manager;
		this.filterRenderer = Objects.requireNonNull(
			filterRenderer,
			"filterRenderer");
		this.chromeRenderer = Objects.requireNonNull(
			chromeRenderer,
			"chromeRenderer");
	}

	void drawPaneLabels(
		Graphics2D graphics,
		JournalGeometry geometry,
		JournalSnapshot snapshot,
		Point pointer,
		JournalPanelAssets assets,
		List<MarkerTooltipHit> markerTooltipHits,
		boolean showFilters,
		Rectangle clearActiveQuestBounds,
		Rectangle returnToActiveQuestBounds)
	{
		Font oldFont = graphics.getFont();
		JournalSnapshot.QuestFilter filter = manager.getQuestFilter();
		JournalSnapshot.QuestListOptions options = snapshot.getListOptions();
		JournalSnapshot.SelectedQuest selectedQuest = snapshot.getSelectedQuest();
		Rectangle activeQuestControl = activeQuestControlBounds(geometry);
		Rectangle starQuestControl = starQuestControlBounds(geometry);
		Rectangle searchControl = searchControlBounds(geometry);
		Rectangle filterVisibilityControl =
			filterVisibilityControlBounds(geometry);
		Rectangle typeControl = filterRenderer.typeControlBounds(geometry);
		Rectangle difficultyControl =
			filterRenderer.difficultyControlBounds(geometry);
		Rectangle membershipControl =
			filterRenderer.membershipControlBounds(geometry);
		Rectangle orderControl = filterRenderer.orderControlBounds(geometry);
		Rectangle statusControl =
			filterRenderer.statusControlBounds(geometry);
		FontMetrics filterMetrics =
			graphics.getFontMetrics(JournalOverlay.compactSmallFont());
		drawActiveQuestControl(
			graphics,
			activeQuestControl,
			snapshot.getActiveQuest(),
			pointer,
			assets,
			clearActiveQuestBounds,
			returnToActiveQuestBounds);
		addActiveQuestControlTooltips(
			markerTooltipHits,
			clearActiveQuestBounds,
			returnToActiveQuestBounds);
		String viewedQuestId = selectedQuest == null
			? null
			: selectedQuest.getOverview().getId();
		boolean starredQuest = viewedQuestId != null
			&& manager != null
			&& manager.isQuestStarred(viewedQuestId);
		drawStarQuestButton(
			graphics,
			starQuestControl,
			viewedQuestId != null,
			starredQuest,
			starQuestControl.contains(pointer));
		drawQuestSearchButton(
			graphics,
			searchControl,
			assets == null ? null : assets.questSearchIcon,
			searchControl.contains(pointer));
		drawFilterVisibilityButton(
			graphics,
			filterVisibilityControl,
			filterVisibilityControl.contains(pointer));
		if (showFilters)
		{
			filterRenderer.drawFilterControl(
				graphics,
				filterCaptionBounds(geometry, typeControl),
				typeControl,
				"Type",
				FilterRenderer.typeChecklistControlSummary(
					filterMetrics,
					typeControl,
					filter,
					options.getTypes()),
				typeControl.contains(pointer),
				filterRenderer.isOpen(JournalOverlay.FilterControl.TYPE));
			filterRenderer.drawFilterControl(
				graphics,
				filterCaptionBounds(geometry, difficultyControl),
				difficultyControl,
				"Difficulty",
				FilterRenderer.checklistControlSummary(
					filterMetrics,
					difficultyControl,
					"Difficulty",
					filter.getDifficulties(),
					options.getDifficulties(),
					FilterRenderer::difficultyLabel),
				difficultyControl.contains(pointer),
				filterRenderer.isOpen(
					JournalOverlay.FilterControl.DIFFICULTY));
			filterRenderer.drawFilterControl(
				graphics,
				filterCaptionBounds(geometry, membershipControl),
				membershipControl,
				"Membership",
				FilterRenderer.checklistControlSummary(
					filterMetrics,
					membershipControl,
					"Membership",
					filter.getMemberships(),
					options.getMemberships(),
					FilterRenderer::membershipFilterLabel),
				membershipControl.contains(pointer),
				filterRenderer.isOpen(
					JournalOverlay.FilterControl.MEMBERSHIP));
			filterRenderer.drawFilterControl(
				graphics,
				filterCaptionBounds(geometry, statusControl),
				statusControl,
				"Status",
				FilterRenderer.checklistControlSummary(
					filterMetrics,
					statusControl,
					"Status",
					filter.getStates(),
					Arrays.asList(JournalSnapshot.QuestState.values()),
					FilterRenderer::statusLabel),
				statusControl.contains(pointer),
				filterRenderer.isOpen(JournalOverlay.FilterControl.STATUS));
			filterRenderer.drawFilterControl(
				graphics,
				filterCaptionBounds(geometry, orderControl),
				orderControl,
				"Order",
				FilterRenderer.orderLabel(filter.getOrder()),
				orderControl.contains(pointer),
				filterRenderer.isOpen(JournalOverlay.FilterControl.ORDER));
		}
		if (selectedQuest != null)
		{
			drawSelectedQuestPaneHeader(
				graphics,
				selectedPaneHeaderBounds(geometry),
				selectedQuest.getOverview(),
				markerTooltipHits);
		}
		graphics.setFont(oldFont);
	}

	void drawSetActiveQuestControl(
		Graphics2D graphics,
		Rectangle bounds,
		Point pointer)
	{
		if (bounds.isEmpty())
		{
			return;
		}
		boolean hovered = pointer != null && bounds.contains(pointer);
		chromeRenderer.drawHeaderControlSkin(graphics, bounds, hovered);
		graphics.setColor(JournalOverlay.TITLE_ORANGE);
		graphics.drawRect(
			bounds.x,
			bounds.y,
			Math.max(0, bounds.width - 1),
			Math.max(0, bounds.height - 1));
		Font oldFont = graphics.getFont();
		graphics.setFont(JournalOverlay.setActiveQuestFont());
		drawCenteredControlLabel(
			graphics,
			bounds,
			"Set Active Quest",
			graphics.getFontMetrics(),
			JournalOverlay.TEXT);
		graphics.setFont(oldFont);
	}

	static void drawQuestProgress(
		JournalOverlay overlay,
		Graphics2D graphics,
		Rectangle bounds,
		JournalSnapshot.QuestProgress progress)
	{
		if (bounds.isEmpty()
			|| progress == null
			|| progress.getTotalQuestCount() <= 0
				&& progress.getTotalQuestPoints() <= 0)
		{
			return;
		}

		Font oldFont = graphics.getFont();
		try
		{
			graphics.setFont(JournalOverlay.progressFont());
			FontMetrics metrics = graphics.getFontMetrics();
			String completedLabel = "Completed";
			String completedValue = " "
				+ progress.getCompletedQuestCount()
				+ "/"
				+ progress.getTotalQuestCount();
			String pointsLabel = "Points";
			String pointsValue = " "
				+ progress.getCurrentQuestPoints()
				+ "/"
				+ progress.getTotalQuestPoints();
			int questsWidth =
				metrics.stringWidth(completedLabel)
					+ metrics.stringWidth(completedValue);
			int pointsWidth =
				metrics.stringWidth(pointsLabel)
					+ metrics.stringWidth(pointsValue);
			int[] groupX = progressGroupPositions(
				bounds,
				questsWidth,
				pointsWidth);
			int questsX = groupX[0];
			int pointsX = groupX[1];
			int baseline = progressTextBaseline(metrics, bounds);
			Graphics2D clipped = overlay.clippedGraphics(graphics, bounds);
			try
			{
				clipped.setFont(graphics.getFont());
				clipped.setColor(JournalOverlay.ACCENT);
				JournalOverlay.drawShadowedString(
					clipped,
					completedLabel,
					questsX,
					baseline);
				questsX += metrics.stringWidth(completedLabel);
				clipped.setColor(JournalOverlay.TEXT);
				JournalOverlay.drawShadowedString(
					clipped,
					completedValue,
					questsX,
					baseline);
				clipped.setColor(JournalOverlay.ACCENT);
				JournalOverlay.drawShadowedString(
					clipped,
					pointsLabel,
					pointsX,
					baseline);
				pointsX += metrics.stringWidth(pointsLabel);
				clipped.setColor(JournalOverlay.TEXT);
				JournalOverlay.drawShadowedString(
					clipped,
					pointsValue,
					pointsX,
					baseline);
			}
			finally
			{
				clipped.dispose();
			}
		}
		finally
		{
			graphics.setFont(oldFont);
		}
	}

	void drawClearActiveQuestControl(
		Graphics2D graphics,
		Rectangle bounds,
		Point pointer,
		JournalPanelAssets assets)
	{
		chromeRenderer.drawHeaderControl(
			graphics,
			bounds,
			pointer,
			JournalOverlay.HeaderControlIcon.CLOSE,
			assets);
	}

	Rectangle searchControlBounds(JournalGeometry geometry)
	{
		return listHeaderActionControlBounds(
			filterRenderer.listHeaderActionRowBounds(geometry),
			2);
	}

	Rectangle filterVisibilityControlBounds(JournalGeometry geometry)
	{
		return listHeaderActionControlBounds(
			filterRenderer.listHeaderActionRowBounds(geometry),
			1);
	}

	Rectangle starQuestControlBounds(JournalGeometry geometry)
	{
		return listHeaderActionControlBounds(
			filterRenderer.listHeaderActionRowBounds(geometry),
			0);
	}

	boolean isManualActiveQuestSelection()
	{
		return manager == null || manager.isManualActiveQuestSelection();
	}

	Rectangle activeQuestControlBounds(JournalGeometry geometry)
	{
		return activeQuestControlBounds(
			geometry,
			isManualActiveQuestSelection());
	}

	static Rectangle activeQuestControlBounds(
		JournalGeometry geometry,
		boolean manualActiveQuestSelection)
	{
		if (!manualActiveQuestSelection)
		{
			return new Rectangle();
		}
		Rectangle header = paneHeader(
			geometry.questListPaneBounds(),
			geometry.questListContentBounds());
		int inset = Math.min(
			JournalOverlay.LIST_CONTROL_HORIZONTAL_INSET,
			header.width / 2);
		int y = header.y + Math.min(2, header.height);
		return new Rectangle(
			header.x + inset,
			y,
			Math.max(0, header.width - inset * 2),
			Math.min(
				JournalOverlay.ACTIVE_QUEST_CONTROL_HEIGHT,
				Math.max(0, header.y + header.height - y)));
	}

	Rectangle setActiveQuestControlBounds(JournalGeometry geometry)
	{
		return setActiveQuestControlBounds(
			selectedPaneHeaderBounds(geometry));
	}

	Rectangle setActiveQuestBounds(
		JournalGeometry geometry,
		JournalSnapshot.QuestOverview overview,
		JournalSnapshot.ActiveQuest activeQuest)
	{
		if (!isManualActiveQuestSelection()
			|| !JournalOverlay.canActivateQuest(overview, activeQuest))
		{
			return new Rectangle();
		}
		return setActiveQuestControlBounds(geometry);
	}

	Rectangle returnToActiveQuestBounds(
		JournalGeometry geometry,
		String viewedQuestId,
		JournalSnapshot.ActiveQuest activeQuest)
	{
		if (!isManualActiveQuestSelection()
			|| activeQuest == null
			|| activeQuest.getId().equals(viewedQuestId))
		{
			return new Rectangle();
		}
		return activeQuestStatusBounds(activeQuestControlBounds(geometry), true);
	}

	static Rectangle filterCaptionBounds(
		JournalGeometry geometry,
		Rectangle trigger)
	{
		Rectangle header = paneHeader(
			geometry.questListPaneBounds(),
			geometry.questListContentBounds());
		int inset = Math.min(
			JournalOverlay.LIST_CONTROL_HORIZONTAL_INSET,
			header.width / 2);
		int x = header.x + inset;
		int width = Math.max(
			0,
			trigger.x - FilterRenderer.FILTER_LABEL_GAP - x);
		return new Rectangle(
			x,
			trigger.y,
			width,
			Math.max(0, trigger.height));
	}

	static Rectangle[] selectedQuestHeaderRowBounds(
		Rectangle header,
		int titleLineHeight,
		int metadataLineHeight)
	{
		int titleHeight = Math.min(
			Math.max(0, titleLineHeight),
			Math.max(0, header.height));
		int remainingHeight = Math.max(0, header.height - titleHeight);
		int gap = Math.min(
			JournalOverlay.SELECTED_HEADER_ROW_GAP,
			remainingHeight);
		int metadataHeight = Math.min(
			Math.max(0, metadataLineHeight),
			Math.max(0, remainingHeight - gap));
		int blockHeight = titleHeight + gap + metadataHeight;
		int top = header.y + Math.max(0, (header.height - blockHeight) / 2);
		Rectangle title = new Rectangle(
			header.x,
			top,
			Math.max(0, header.width),
			titleHeight);
		Rectangle metadata = new Rectangle(
			header.x,
			top + titleHeight + gap,
			Math.max(0, header.width),
			metadataHeight);
		return new Rectangle[]{title, metadata};
	}

	static Rectangle selectedQuestMainContentBounds(
		Rectangle content,
		boolean reserveSetActiveQuestControl)
	{
		Rectangle adjusted = new Rectangle(content);
		if (!reserveSetActiveQuestControl)
		{
			return adjusted;
		}
		int inset = Math.min(
			JournalOverlay.SET_ACTIVE_QUEST_CONTENT_INSET,
			Math.max(0, adjusted.height));
		adjusted.y += inset;
		adjusted.height -= inset;
		return adjusted;
	}

	static Rectangle setActiveQuestControlBounds(Rectangle header)
	{
		int width = Math.min(
			JournalOverlay.SET_ACTIVE_QUEST_CONTROL_WIDTH,
			Math.max(
				0,
				header.width
					- JournalOverlay.SELECTED_HEADER_HORIZONTAL_PADDING * 2));
		int height = Math.min(
			JournalOverlay.SET_ACTIVE_QUEST_CONTROL_HEIGHT,
			Math.max(0, header.height));
		return new Rectangle(
			header.x + Math.max(0, (header.width - width) / 2),
			header.y + Math.max(0, header.height - height / 2),
			width,
			height);
	}

	static Rectangle activeQuestStatusBounds(
		Rectangle control,
		boolean hasActiveQuest)
	{
		Rectangle status = new Rectangle(control);
		if (!hasActiveQuest || status.isEmpty())
		{
			return status;
		}
		int clearControlWidth = Math.min(
			JournalOverlay.CLEAR_ACTIVE_QUEST_CONTROL_SIZE,
			Math.max(
				0,
				status.width
					- JournalOverlay.CLEAR_ACTIVE_QUEST_CONTROL_INSET * 2));
		status.width = Math.max(
			0,
			status.width
				- JournalOverlay.CLEAR_ACTIVE_QUEST_CONTROL_INSET
				- clearControlWidth
				- JournalOverlay.CLEAR_ACTIVE_QUEST_CONTROL_GAP);
		return status;
	}

	static Rectangle clearActiveQuestBounds(
		Rectangle control,
		JournalSnapshot.ActiveQuest activeQuest)
	{
		if (activeQuest == null || control.isEmpty())
		{
			return new Rectangle();
		}
		int size = Math.min(
			JournalOverlay.CLEAR_ACTIVE_QUEST_CONTROL_SIZE,
			Math.max(
				0,
				Math.min(
					control.height,
					control.width
						- JournalOverlay.CLEAR_ACTIVE_QUEST_CONTROL_INSET * 2)));
		return new Rectangle(
			control.x + Math.max(
				0,
				control.width
					- JournalOverlay.CLEAR_ACTIVE_QUEST_CONTROL_INSET
					- size),
			control.y + Math.max(0, (control.height - size) / 2),
			size,
			size);
	}

	static Rectangle filterTriggerBounds(Rectangle row)
	{
		int labelWidth = Math.min(
			FilterRenderer.FILTER_LABEL_WIDTH,
			Math.max(
				0,
				(row.width - FilterRenderer.FILTER_LABEL_GAP) / 2));
		int gap = Math.min(
			FilterRenderer.FILTER_LABEL_GAP,
			Math.max(0, row.width - labelWidth));
		return new Rectangle(
			row.x + labelWidth + gap,
			row.y,
			Math.max(0, row.width - labelWidth - gap),
			Math.max(0, row.height));
	}

	static Rectangle filterArrowBounds(Rectangle trigger)
	{
		int rightInset = Math.min(
			FilterRenderer.FILTER_ARROW_RIGHT_INSET,
			Math.max(0, trigger.width));
		int width = Math.min(
			FilterRenderer.FILTER_ARROW_WIDTH,
			Math.max(0, trigger.width - rightInset));
		int height = Math.min(
			FilterRenderer.FILTER_ARROW_HEIGHT,
			Math.max(0, trigger.height));
		return new Rectangle(
			trigger.x + Math.max(0, trigger.width - rightInset - width),
			trigger.y + Math.max(0, (trigger.height - height) / 2),
			width,
			height);
	}

	static Rectangle listHeaderActionIconBounds(Rectangle bounds)
	{
		int horizontalInset = Math.min(
			JournalOverlay.LIST_HEADER_ACTION_HORIZONTAL_INSET,
			Math.max(0, bounds.width / 2));
		int size = Math.min(
			JournalOverlay.LIST_HEADER_ACTION_ICON_SIZE,
			Math.max(
				0,
				Math.min(
					bounds.height - 4,
					bounds.width - horizontalInset * 2)));
		return new Rectangle(
			bounds.x + Math.max(
				0,
				bounds.width - horizontalInset - size),
			bounds.y + Math.max(0, (bounds.height - size) / 2),
			size,
			size);
	}

	static int[] progressGroupPositions(
		Rectangle bounds,
		int questsWidth,
		int pointsWidth)
	{
		int safeQuestWidth = Math.max(0, questsWidth);
		int safePointsWidth = Math.max(0, pointsWidth);
		int freeSpace = Math.max(
			0,
			bounds.width - safeQuestWidth - safePointsWidth);
		int gap = freeSpace / 3;
		int left = bounds.x + gap;
		int right = left + safeQuestWidth + gap;
		if ((long) left + safeQuestWidth > right
			|| (long) right + safePointsWidth
				> (long) bounds.x + bounds.width)
		{
			int totalWidth = safeQuestWidth + safePointsWidth;
			left = bounds.x + Math.max(
				0,
				(bounds.width - totalWidth) / 2);
			right = left + safeQuestWidth;
		}
		return new int[]{left, right};
	}

	static int progressTextBaseline(FontMetrics metrics, Rectangle bounds)
	{
		return (int) Math.round(
			bounds.getCenterY()
				+ (metrics.getAscent() - metrics.getDescent()) / 2.0)
			- 1 + JournalOverlay.QUEST_PROGRESS_VISUAL_OFFSET_Y;
	}

	static String starQuestButtonLabel(boolean starred)
	{
		return starred ? "Unstar" : "Star";
	}

	static void drawStarGlyph(
		Graphics2D graphics,
		Rectangle bounds,
		Color color)
	{
		int centerX = bounds.x + bounds.width / 2;
		int centerY = bounds.y + bounds.height / 2;
		int outerRadius = Math.max(
			2,
			(Math.min(bounds.width, bounds.height) - 2) / 2);
		double innerRadius = Math.max(1.0, outerRadius * 0.45);
		Polygon star = new Polygon();
		for (int point = 0; point < 10; point++)
		{
			double angle = -Math.PI / 2.0 + point * Math.PI / 5.0;
			double radius = point % 2 == 0 ? outerRadius : innerRadius;
			star.addPoint(
				(int) Math.round(centerX + Math.cos(angle) * radius),
				(int) Math.round(centerY + Math.sin(angle) * radius));
		}
		graphics.setColor(color);
		graphics.fillPolygon(star);
		graphics.setColor(Color.BLACK);
		graphics.drawPolygon(star);
	}

	static Color activeQuestTitleColor(JournalSnapshot.QuestState state)
	{
		return JournalOverlay.questStateColor(state);
	}

	static Color noActiveQuestTextColor()
	{
		return JournalOverlay.ACCENT;
	}

	static String clearActiveQuestTooltipText()
	{
		return "Clear active quest";
	}

	static String overviewClassificationLabel(
		JournalSnapshot.QuestType type,
		JournalSnapshot.QuestDifficulty difficulty)
	{
		String label = FilterRenderer.questClassificationLabel(
			type,
			difficulty).toLowerCase(Locale.ROOT);
		return label.isEmpty()
			? label
			: Character.toUpperCase(label.charAt(0)) + label.substring(1);
	}

	private void drawSelectedQuestPaneHeader(
		Graphics2D graphics,
		Rectangle header,
		JournalSnapshot.QuestOverview overview,
		List<MarkerTooltipHit> markerTooltipHits)
	{
		if (header.isEmpty())
		{
			return;
		}

		Font titleFont = JournalOverlay.overviewQuestTitleFont();
		Font detailFont = JournalOverlay.overviewMetadataFont();
		graphics.setFont(titleFont);
		FontMetrics titleMetrics = graphics.getFontMetrics();
		graphics.setFont(detailFont);
		FontMetrics detailMetrics = graphics.getFontMetrics();
		Rectangle[] headerRows = selectedQuestHeaderRowBounds(
			header,
			titleMetrics.getHeight(),
			detailMetrics.getHeight());
		Rectangle titleRow = headerRows[0];
		Rectangle metadataRow = headerRows[1];
		int titleBaseline = overlay.centeredTextBaseline(titleMetrics, titleRow);
		int membershipBaseline =
			overlay.centeredTextBaseline(detailMetrics, metadataRow);
		int difficultyBaseline = membershipBaseline;
		String classification = overviewClassificationLabel(
			overview.getType(),
			overview.getDifficulty());
		String membership =
			JournalOverlay.membershipLabel(overview.isMembers());
		boolean showDifficultyDot =
			QuestListRenderer.selectedQuestShowsDifficultyDot(
				overview.getType(),
				overview.getDifficulty());
		int difficultyMarkerWidth = showDifficultyDot
			? JournalOverlay.DIFFICULTY_DOT_SIZE
				+ JournalOverlay.DIFFICULTY_DOT_TEXT_GAP
			: 0;
		Rectangle centeredControl = isManualActiveQuestSelection()
			? setActiveQuestControlBounds(header)
			: new Rectangle(
				header.x + header.width / 2,
				header.y,
				0,
				header.height);
		int difficultyLeft =
			header.x + JournalOverlay.SELECTED_HEADER_HORIZONTAL_PADDING;
		int difficultyTextX = difficultyLeft + difficultyMarkerWidth;
		int maximumDifficultyTextWidth = Math.max(
			0,
			centeredControl.x - 5 - difficultyTextX);
		String visibleClassification = overlay.fitText(
			detailMetrics,
			classification,
			maximumDifficultyTextWidth);
		int titleX =
			header.x + JournalOverlay.SELECTED_HEADER_HORIZONTAL_PADDING;
		int titleRight = Math.max(
			titleX,
			header.x + header.width
				- JournalOverlay.SELECTED_HEADER_HORIZONTAL_PADDING);
		graphics.setFont(titleFont);
		JournalOverlay.drawQuestTitleString(
			graphics,
			overlay.fitText(
				titleMetrics,
				overview.getTitle(),
				Math.max(0, titleRight - titleX)),
			titleX,
			titleBaseline,
			JournalOverlay.questStateColor(overview.getState()));

		graphics.setFont(detailFont);
		if (showDifficultyDot)
		{
			Rectangle dotBounds = QuestListRenderer.difficultyDotBounds(
				difficultyLeft,
				difficultyBaseline,
				detailMetrics.getAscent(),
				detailMetrics.getDescent());
			QuestListRenderer.drawDifficultyDot(
				graphics,
				dotBounds,
				overview.getDifficulty());
			markerTooltipHits.add(new MarkerTooltipHit(
				dotBounds,
				QuestListRenderer.difficultyMarkerTooltip(
					overview.getDifficulty()),
				false));
		}
		graphics.setColor(JournalOverlay.ACCENT);
		JournalOverlay.drawShadowedString(
			graphics,
			visibleClassification,
			difficultyTextX,
			difficultyBaseline);

		int starSize = Math.min(
			10,
			Math.max(8, detailMetrics.getHeight() - 2));
		int maximumMembershipTextWidth = Math.max(
			0,
			header.x + header.width
				- JournalOverlay.SELECTED_HEADER_HORIZONTAL_PADDING
				- centeredControl.x - centeredControl.width - 5
				- starSize - 4);
		String visibleMembership = overlay.fitText(
			detailMetrics,
			membership,
			maximumMembershipTextWidth);
		int membershipWidth =
			starSize + 4 + detailMetrics.stringWidth(visibleMembership);
		int rightX = Math.max(
			centeredControl.x + centeredControl.width + 5,
			header.x + header.width - membershipWidth
				- JournalOverlay.SELECTED_HEADER_HORIZONTAL_PADDING);
		Rectangle star = new Rectangle(
			rightX,
			membershipBaseline - detailMetrics.getAscent()
				+ Math.max(0, (detailMetrics.getHeight() - starSize) / 2),
			starSize,
			starSize);
		overlay.drawMembershipEmblem(
			graphics,
			star,
			overview.isMembers());
		graphics.setColor(JournalOverlay.ACCENT);
		JournalOverlay.drawShadowedString(
			graphics,
			visibleMembership,
			rightX + starSize + 4,
			membershipBaseline);
	}

	private static Rectangle listHeaderActionControlBounds(
		Rectangle row,
		int index)
	{
		int controlIndex = JournalOverlay.clamp(index, 0, 2);
		int gap = Math.min(
			JournalOverlay.LIST_HEADER_CONTROL_GAP,
			Math.max(0, row.width / 2));
		int availableWidth = Math.max(0, row.width - gap * 2);
		int baseWidth = availableWidth / 3;
		int remainder = availableWidth % 3;
		int x = row.x;
		for (int current = 0; current < controlIndex; current++)
		{
			x += baseWidth + (current < remainder ? 1 : 0) + gap;
		}
		int width = baseWidth + (controlIndex < remainder ? 1 : 0);
		return new Rectangle(x, row.y, width, row.height);
	}

	private Rectangle selectedPaneHeaderBounds(JournalGeometry geometry)
	{
		if (geometry.hasDetailPane())
		{
			Rectangle mainPane = geometry.mainPaneBounds();
			Rectangle mainContent = geometry.mainContentBounds();
			return new Rectangle(
				mainPane.x,
				mainPane.y,
				mainPane.width,
				Math.max(
					0,
					mainContent.y - mainPane.y
						- JournalOverlay.PANE_SEPARATOR_INSET));
		}
		return geometry.compactHeaderBounds();
	}

	private void drawActiveQuestControl(
		Graphics2D graphics,
		Rectangle bounds,
		JournalSnapshot.ActiveQuest activeQuest,
		Point pointer,
		JournalPanelAssets assets,
		Rectangle clearBounds,
		Rectangle returnToActiveBounds)
	{
		if (bounds.isEmpty())
		{
			return;
		}
		Rectangle statusBounds = activeQuestStatusBounds(
			bounds,
			activeQuest != null);
		if (activeQuest != null && !returnToActiveBounds.isEmpty())
		{
			boolean hovered =
				returnToActiveBounds.contains(pointer);
			chromeRenderer.drawHeaderControlSkin(graphics, statusBounds, hovered);
		}
		Graphics2D control = overlay.clippedGraphics(graphics, bounds);
		try
		{
			control.setFont(JournalOverlay.questListFont());
			FontMetrics statusMetrics = control.getFontMetrics();
			if (activeQuest == null)
			{
				drawCenteredControlLabel(
					control,
					statusBounds,
					"No active quest",
					statusMetrics,
					noActiveQuestTextColor());
			}
			else
			{
				drawActiveQuestStatus(
					control,
					statusBounds,
					statusMetrics,
					activeQuest,
					assets);
				drawClearActiveQuestControl(
					control,
					clearBounds,
					pointer,
					assets);
			}
		}
		finally
		{
			control.dispose();
		}
	}

	private void drawActiveQuestStatus(
		Graphics2D graphics,
		Rectangle bounds,
		FontMetrics metrics,
		JournalSnapshot.ActiveQuest activeQuest,
		JournalPanelAssets assets)
	{
		String prefix = "Active: ";
		int iconSize = Math.min(
			JournalOverlay.ACTIVE_QUEST_ICON_SIZE,
			Math.max(0, Math.min(bounds.width, bounds.height)));
		int iconGap = iconSize > 0
			? JournalOverlay.ACTIVE_QUEST_ICON_GAP
			: 0;
		int availableWidth = Math.max(
			0,
			bounds.width
				- JournalOverlay.ACTIVE_QUEST_CONTENT_HORIZONTAL_INSET * 2
				- iconSize - iconGap);
		String title = overlay.fitText(
			metrics,
			activeQuest.getTitle(),
			Math.max(0, availableWidth - metrics.stringWidth(prefix)));
		int totalWidth = iconSize + iconGap
			+ metrics.stringWidth(prefix)
			+ metrics.stringWidth(title);
		int x = bounds.x + Math.max(0, (bounds.width - totalWidth) / 2);
		Rectangle iconBounds = new Rectangle(
			x,
			bounds.y + Math.max(0, (bounds.height - iconSize) / 2),
			iconSize,
			iconSize);
		QuestListRenderer.drawActiveQuestIcon(
			overlay,
			graphics,
			iconBounds,
			assets);
		int textX = x + iconSize + iconGap;
		int baseline = overlay.centeredTextBaseline(metrics, bounds);
		graphics.setColor(JournalOverlay.TEXT);
		JournalOverlay.drawShadowedString(
			graphics,
			prefix,
			textX,
			baseline);
		graphics.setColor(activeQuestTitleColor(activeQuest.getState()));
		JournalOverlay.drawShadowedString(
			graphics,
			title,
			textX + metrics.stringWidth(prefix),
			baseline);
	}

	private void drawCenteredControlLabel(
		Graphics2D graphics,
		Rectangle bounds,
		String label,
		FontMetrics metrics,
		Color color)
	{
		String fitted = overlay.fitText(
			metrics,
			label,
			Math.max(0, bounds.width - 8));
		graphics.setColor(color);
		JournalOverlay.drawShadowedString(
			graphics,
			fitted,
			bounds.x + Math.max(
				0,
				(bounds.width - metrics.stringWidth(fitted)) / 2),
			overlay.centeredTextBaseline(metrics, bounds));
	}

	private void addActiveQuestControlTooltips(
		List<MarkerTooltipHit> tooltips,
		Rectangle clearBounds,
		Rectangle returnToActiveBounds)
	{
		if (!clearBounds.isEmpty())
		{
			tooltips.add(new MarkerTooltipHit(
				clearBounds,
				clearActiveQuestTooltipText(),
				false));
		}
		if (!returnToActiveBounds.isEmpty())
		{
			tooltips.add(new MarkerTooltipHit(
				returnToActiveBounds,
				"Return to active quest",
				false));
		}
	}

	private void drawStarQuestButton(
		Graphics2D graphics,
		Rectangle bounds,
		boolean enabled,
		boolean starred,
		boolean hovered)
	{
		if (bounds.isEmpty())
		{
			return;
		}
		chromeRenderer.drawHeaderControlSkin(
			graphics,
			bounds,
			enabled && hovered);
		Rectangle iconBounds = drawListHeaderActionLabel(
			graphics,
			bounds,
			starQuestButtonLabel(starred),
			JournalOverlay.TEXT);
		drawStarGlyph(
			graphics,
			iconBounds,
			JournalOverlay.ACCENT);
	}

	private void drawQuestSearchButton(
		Graphics2D graphics,
		Rectangle bounds,
		BufferedImage searchIcon,
		boolean hovered)
	{
		if (bounds.isEmpty())
		{
			return;
		}
		chromeRenderer.drawHeaderControlSkin(graphics, bounds, hovered);
		Rectangle iconBounds = drawListHeaderActionLabel(
			graphics,
			bounds,
			"Search",
			JournalOverlay.TEXT);
		if (searchIcon != null)
		{
			overlay.drawPixelArtCenteredImage(
				graphics,
				searchIcon,
				iconBounds);
			return;
		}
		drawSearchIconFallback(graphics, iconBounds);
	}

	private void drawFilterVisibilityButton(
		Graphics2D graphics,
		Rectangle bounds,
		boolean hovered)
	{
		if (bounds.isEmpty())
		{
			return;
		}
		chromeRenderer.drawHeaderControlSkin(graphics, bounds, hovered);
		Rectangle iconBounds = drawListHeaderActionLabel(
			graphics,
			bounds,
			"Filters",
			JournalOverlay.TEXT);
		Graphics2D icon = (Graphics2D) graphics.create();
		try
		{
			icon.clip(iconBounds);
			icon.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
			icon.setColor(JournalOverlay.CONTROL_ICON_DARK);
			int centerX = iconBounds.x + iconBounds.width / 2;
			int top = iconBounds.y + Math.max(
				1,
				(iconBounds.height - 8) / 2);
			icon.fillRect(centerX - 4, top, 8, 2);
			icon.fillRect(centerX - 3, top + 2, 6, 1);
			icon.fillRect(centerX - 2, top + 3, 4, 1);
			icon.fillRect(centerX - 1, top + 4, 2, 4);
		}
		finally
		{
			icon.dispose();
		}
	}

	private Rectangle drawListHeaderActionLabel(
		Graphics2D graphics,
		Rectangle bounds,
		String label,
		Color color)
	{
		Font oldFont = graphics.getFont();
		try
		{
			graphics.setFont(JournalOverlay.compactSmallFont());
			FontMetrics metrics = graphics.getFontMetrics();
			Rectangle iconBounds = listHeaderActionIconBounds(bounds);
			int labelX = bounds.x + Math.min(
				JournalOverlay.LIST_HEADER_ACTION_HORIZONTAL_INSET,
				Math.max(0, bounds.width));
			int maximumLabelWidth = Math.max(
				0,
				iconBounds.x
					- JournalOverlay.LIST_HEADER_ACTION_LABEL_GAP
					- labelX);
			String fitted = overlay.fitText(
				metrics,
				label,
				maximumLabelWidth);
			graphics.setColor(color);
			JournalOverlay.drawShadowedString(
				graphics,
				fitted,
				labelX,
				overlay.centeredTextBaseline(metrics, bounds));
			return iconBounds;
		}
		finally
		{
			graphics.setFont(oldFont);
		}
	}

	private void drawSearchIconFallback(
		Graphics2D graphics,
		Rectangle bounds)
	{
		if (bounds.width < 6 || bounds.height < 6)
		{
			return;
		}
		Graphics2D icon = (Graphics2D) graphics.create();
		try
		{
			icon.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
			icon.setColor(JournalOverlay.MUTED);
			icon.setStroke(new BasicStroke(
				2f,
				BasicStroke.CAP_SQUARE,
				BasicStroke.JOIN_MITER));
			int inset = 3;
			int lens = Math.max(
				5,
				Math.min(bounds.width, bounds.height) - inset * 2 - 3);
			int x = bounds.x + inset;
			int y = bounds.y + inset;
			icon.drawOval(x, y, lens, lens);
			icon.drawLine(
				x + lens - 1,
				y + lens - 1,
				bounds.x + bounds.width - inset,
				bounds.y + bounds.height - inset);
		}
		finally
		{
			icon.dispose();
		}
	}

	static Rectangle paneHeader(
		Rectangle pane,
		Rectangle content)
	{
		return new Rectangle(
			pane.x,
			pane.y,
			pane.width,
			Math.max(0, content.y - pane.y));
	}
}
