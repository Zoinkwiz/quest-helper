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

import com.questhelper.questjournal.JournalOverlay.FilterControl;
import com.questhelper.questjournal.JournalOverlay.FilterSelection;
import com.questhelper.questjournal.JournalOverlay.FilterSelectionAction;
import com.questhelper.questjournal.TooltipRenderer.MarkerTooltipHit;
import com.questhelper.questjournal.JournalOverlay.TypeFilterOption;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/** Renders and hit-tests quest-list filters. */
final class FilterRenderer
{
	private static final Color ACCENT = new Color(0xE9, 0xBF, 0x6F);
	private static final Color JOURNAL_ORANGE = new Color(0xFF, 0x99, 0x33);
	private static final Color FILTER_TRIGGER_BACKGROUND = new Color(0, 0, 0, 38);
	private static final Color FILTER_TRIGGER_INNER_BORDER = new Color(0x44, 0x44, 0x42);
	private static final Color FILTER_TRIGGER_OUTER_BORDER = Color.BLACK;
	private static final Color FILTER_TRIGGER_ARROW = Color.WHITE;
	private static final Color FILTER_POPUP_BORDER = new Color(0x00, 0x00, 0x01);
	private static final Color FILTER_POPUP_BACKGROUND = new Color(0x3C, 0x35, 0x2B);
	private static final Color FILTER_POPUP_EVEN_ROW_OVERLAY =
		new Color(0xFF, 0xFF, 0xFF, 13);
	private static final Color TRANSPARENT = new Color(0, 0, 0, 0);

	static final int FILTER_CONTROL_HEIGHT = 22;
	static final int FILTER_LABEL_WIDTH = 70;
	static final int FILTER_LABEL_GAP = 4;
	static final int FILTER_ARROW_WIDTH = 9;
	static final int FILTER_ARROW_HEIGHT = 7;
	static final int FILTER_ARROW_RIGHT_INSET = 6;
	static final int FILTER_ROW_GAP = 2;
	static final int FILTER_SECTION_GAP = 4;

	private static final int FILTER_DIFFICULTY_ICON_GAP = 4;
	private static final int FILTER_POPUP_ROW_HEIGHT = 19;
	private static final int FILTER_POPUP_BORDER_WIDTH = 1;
	private static final int FILTER_POPUP_MINIMUM_WIDTH = 150;
	private static final int FILTER_POPUP_PADDING = FILTER_POPUP_BORDER_WIDTH;
	private static final int FILTER_POPUP_ACTION_VERTICAL_MARGIN = 2;
	private static final int FILTER_POPUP_ACTION_BUTTON_HEIGHT = 22;
	private static final int FILTER_POPUP_ACTION_ROW_HEIGHT =
		FILTER_POPUP_ACTION_BUTTON_HEIGHT + FILTER_POPUP_ACTION_VERTICAL_MARGIN * 2;

	private final JournalOverlay overlay;
	private final QuestJournalManager manager;
	private final ChromeRenderer chromeRenderer;

	private volatile FilterControl openControl;
	private volatile FilterHitState hitState = FilterHitState.empty();

	FilterRenderer(
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

	Rectangle listHeaderActionRowBounds(JournalGeometry geometry)
	{
		Rectangle header = ControlsRenderer.paneHeader(
			geometry.questListPaneBounds(),
			geometry.questListContentBounds());
		Rectangle activeControl =
			ControlsRenderer.activeQuestControlBounds(
				geometry,
				manager == null || manager.isManualActiveQuestSelection());
		int inset = Math.min(
			JournalOverlay.LIST_CONTROL_HORIZONTAL_INSET,
			header.width / 2);
		int y = activeControl.isEmpty()
			? header.y + Math.min(2, header.height)
			: Math.min(
				header.y + header.height,
				activeControl.y + activeControl.height
					+ JournalOverlay.ACTIVE_QUEST_CONTROL_GAP);
		return new Rectangle(
			header.x + inset,
			y,
			Math.max(0, header.width - inset * 2),
			Math.min(
				JournalOverlay.LIST_HEADER_ACTION_HEIGHT,
				Math.max(0, header.y + header.height - y)));
	}

	Rectangle typeControlBounds(JournalGeometry geometry)
	{
		Rectangle header = ControlsRenderer.paneHeader(
			geometry.questListPaneBounds(),
			geometry.questListContentBounds());
		Rectangle actionRow = listHeaderActionRowBounds(geometry);
		int inset = Math.min(
			JournalOverlay.LIST_CONTROL_HORIZONTAL_INSET,
			header.width / 2);
		int y = Math.min(
			header.y + header.height,
			actionRow.y + actionRow.height + Math.min(
				FILTER_SECTION_GAP,
				Math.max(
					0,
					header.y + header.height
						- actionRow.y - actionRow.height)));
		Rectangle row = new Rectangle(
			header.x + inset,
			y,
			Math.max(0, header.width - inset * 2),
			Math.min(
				FILTER_CONTROL_HEIGHT,
				Math.max(0, header.y + header.height - y)));
		return ControlsRenderer.filterTriggerBounds(row);
	}

	Rectangle difficultyControlBounds(JournalGeometry geometry)
	{
		return filterControlBelow(typeControlBounds(geometry), geometry);
	}

	Rectangle membershipControlBounds(JournalGeometry geometry)
	{
		return filterControlBelow(difficultyControlBounds(geometry), geometry);
	}

	Rectangle orderControlBounds(JournalGeometry geometry)
	{
		return filterControlBelow(statusControlBounds(geometry), geometry);
	}

	Rectangle statusControlBounds(JournalGeometry geometry)
	{
		return filterControlBelow(membershipControlBounds(geometry), geometry);
	}

	private Rectangle filterControlBelow(
		Rectangle previous,
		JournalGeometry geometry)
	{
		Rectangle header = ControlsRenderer.paneHeader(
			geometry.questListPaneBounds(),
			geometry.questListContentBounds());
		int y = Math.min(
			header.y + header.height,
			previous.y + previous.height + FILTER_ROW_GAP);
		return new Rectangle(
			previous.x,
			y,
			previous.width,
			Math.min(
				FILTER_CONTROL_HEIGHT,
				Math.max(0, header.y + header.height - y)));
	}

	void drawFilterControl(
		Graphics2D graphics,
		Rectangle captionBounds,
		Rectangle bounds,
		String caption,
		String value,
		boolean hovered,
		boolean open)
	{
		graphics.setFont(JournalOverlay.compactSmallFont());
		FontMetrics metrics = graphics.getFontMetrics();
		if (!captionBounds.isEmpty())
		{
			String label = overlay.fitText(
				metrics,
				caption + ":",
				Math.max(0, captionBounds.width - 1));
			graphics.setColor(filterTriggerTextColor());
			JournalOverlay.drawShadowedString(
				graphics,
				label,
				captionBounds.x,
				overlay.centeredTextBaseline(metrics, captionBounds));
		}
		if (bounds.isEmpty())
		{
			return;
		}
		drawFilterTriggerSkin(graphics, bounds);

		Rectangle arrow = ControlsRenderer.filterArrowBounds(bounds);
		drawFilterArrowGlyph(graphics, arrow, open);

		int left = bounds.x + 5;
		int textRight = arrow.isEmpty()
			? bounds.x + bounds.width - 5
			: arrow.x - 4;
		int baseline = overlay.centeredTextBaseline(metrics, bounds);
		graphics.setColor(filterTriggerTextColor(hovered));
		JournalOverlay.drawShadowedString(
			graphics,
			overlay.fitText(metrics, value, Math.max(0, textRight - left)),
			left,
			baseline);
	}

	private void drawFilterTriggerSkin(Graphics2D graphics, Rectangle bounds)
	{
		Rectangle face = ChromeRenderer.insetRectangle(bounds, 2);
		if (!face.isEmpty())
		{
			graphics.setColor(filterTriggerBackgroundColor());
			graphics.fillRect(face.x, face.y, face.width, face.height);
		}
		Rectangle innerBorder = ChromeRenderer.insetRectangle(
			bounds,
			1);
		if (!innerBorder.isEmpty())
		{
			graphics.setColor(filterTriggerInnerBorderColor());
			ChromeRenderer.drawPixelFrame(
				graphics,
				innerBorder.x,
				innerBorder.y,
				innerBorder.width,
				innerBorder.height,
				1);
		}
		graphics.setColor(filterTriggerOuterBorderColor());
		ChromeRenderer.drawPixelFrame(
			graphics,
			bounds.x,
			bounds.y,
			bounds.width,
			bounds.height,
			1);
	}

	private void drawFilterArrowGlyph(Graphics2D graphics, Rectangle bounds, boolean open)
	{
		if (bounds.width < 5 || bounds.height < 3)
		{
			return;
		}
		int centerX = bounds.x + bounds.width / 2;
		int centerY = bounds.y + bounds.height / 2;
		graphics.setColor(filterTriggerArrowColor());
		for (int step = 0; step < 4; step++)
		{
			int width = 7 - step * 2;
			int y = open
				? centerY + 2 - step
				: centerY - 2 + step;
			graphics.fillRect(centerX - width / 2, y, width, 1);
		}
	}

	static String typeLabel(JournalSnapshot.QuestType type)
	{
		if (type == null)
		{
			return "All";
		}
		switch (type)
		{
			case QUEST:
				return "Quests";
			case MINIQUEST:
				return "Miniquests";
			case ACHIEVEMENT_DIARY:
				return "Achievement Diaries";
			case GENERIC:
				return "Generic Helpers";
			case SKILL:
				return "Skill Helpers";
			case PLAYER_QUEST:
				return "Player-made Quests";
			default:
				return JournalOverlay.prettyEnumName(type);
		}
	}

	static <T> String checklistControlSummary(
		FontMetrics metrics,
		Rectangle bounds,
		String caption,
		Set<T> selected,
		List<T> available,
		Function<T, String> labeler)
	{
		return checklistSummary(
			selected,
			available,
			labeler,
			filterControlValueWidth(metrics, bounds, caption),
			metrics::stringWidth);
	}

	static String typeChecklistControlSummary(
		FontMetrics metrics,
		Rectangle bounds,
		JournalSnapshot.QuestFilter filter,
		List<JournalSnapshot.QuestType> availableTypes)
	{
		List<Object> available = new ArrayList<>();
		available.add(TypeFilterOption.STARRED);
		available.addAll(availableTypes);
		Set<Object> selected = new LinkedHashSet<>();
		if (filter.isStarredSelected())
		{
			selected.add(TypeFilterOption.STARRED);
		}
		selected.addAll(filter.getTypes());
		return checklistControlSummary(
			metrics,
			bounds,
			"Type",
			selected,
			available,
			value -> value == TypeFilterOption.STARRED
				? "Starred"
				: typeLabel((JournalSnapshot.QuestType) value));
	}

	static <T> String checklistSummary(
		Set<T> selected,
		List<T> available,
		Function<T, String> labeler,
		int maxWidth,
		ToIntFunction<String> textWidth)
	{
		Objects.requireNonNull(selected, "selected");
		Objects.requireNonNull(available, "available");
		Objects.requireNonNull(labeler, "labeler");
		Objects.requireNonNull(textWidth, "textWidth");

		List<String> labels = new ArrayList<>();
		for (T value : available)
		{
			if (selected.contains(value))
			{
				labels.add(labeler.apply(value));
			}
		}
		if (!available.isEmpty() && labels.size() == available.size())
		{
			return "All";
		}
		if (labels.isEmpty())
		{
			return "None";
		}
		if (labels.size() == 1)
		{
			return labels.get(0);
		}

		String expanded = String.join(" & ", labels);
		return textWidth.applyAsInt(expanded) <= Math.max(0, maxWidth)
			? expanded
			: labels.size() + " selected";
	}

	static int filterControlValueWidth(
		FontMetrics metrics,
		Rectangle bounds,
		String caption)
	{
		Objects.requireNonNull(metrics, "metrics");
		Objects.requireNonNull(bounds, "bounds");
		Objects.requireNonNull(caption, "caption");
		int left = bounds.x + 5;
		Rectangle arrow = ControlsRenderer.filterArrowBounds(bounds);
		int textRight = arrow.isEmpty()
			? bounds.x + bounds.width - 5
			: arrow.x - 4;
		return Math.max(0, textRight - left);
	}

	static String questClassificationLabel(
		JournalSnapshot.QuestType type,
		JournalSnapshot.QuestDifficulty difficulty)
	{
		return type == null || type == JournalSnapshot.QuestType.QUEST
			? difficultyLabel(difficulty)
			: typeLabel(type);
	}

	static String difficultyLabel(JournalSnapshot.QuestDifficulty difficulty)
	{
		return difficulty == null
			? "All"
			: JournalOverlay.prettyEnumName(difficulty);
	}

	static String membershipFilterLabel(JournalSnapshot.QuestMembership membership)
	{
		if (membership == null)
		{
			return "All";
		}
		return membership == JournalSnapshot.QuestMembership.MEMBERS
			? "Members"
			: "Free to play";
	}

	static String statusLabel(JournalSnapshot.QuestState state)
	{
		if (state == null)
		{
			return "All";
		}
		return state == JournalSnapshot.QuestState.COMPLETE
			? "Completed"
			: JournalOverlay.prettyEnumName(state);
	}

	static String orderLabel(JournalSnapshot.QuestOrder order)
	{
		switch (order)
		{
			case A_TO_Z:
				return "A to Z";
			case Z_TO_A:
				return "Z to A";
			case OPTIMAL_IRONMAN:
				return "Optimal Ironman";
			case RELEASE_DATE:
				return "Release Date";
			case QUEST_POINTS_ASC:
				return "Quest Points, Low First";
			case QUEST_POINTS_DESC:
				return "Quest Points, High First";
			default:
				return "Optimal";
		}
	}

	RenderResult drawOpenDropdown(
		Graphics2D graphics,
		JournalGeometry geometry,
		JournalSnapshot snapshot,
		Point pointer,
		Rectangle viewport,
		JournalPanelAssets assets,
		List<MarkerTooltipHit> markerTooltipHits)
	{
		FilterControl control = openControl;
		if (control == null)
		{
			return RenderResult.empty();
		}

		List<FilterSelection> options = filterSelections(snapshot, control);
		if (options.isEmpty())
		{
			return RenderResult.empty();
		}

		Rectangle popup = filterPopupBounds(
			filterControlBounds(geometry, control),
			geometry.questListPaneBounds(),
			control,
			options.size());
		Rectangle clippedPopup = JournalGeometry.clip(popup, viewport);
		if (clippedPopup.isEmpty())
		{
			return RenderResult.empty();
		}

		List<FilterOptionHit> hits = new ArrayList<>(
			options.size() + (control.isChecklist() ? 2 : 0));
		Graphics2D dropdown = overlay.clippedGraphics(graphics, clippedPopup);
		try
		{
			dropdown.setColor(FILTER_POPUP_BACKGROUND);
			dropdown.fillRect(popup.x, popup.y, popup.width, popup.height);
			dropdown.setColor(FILTER_POPUP_BORDER);
			ChromeRenderer.drawPixelFrame(
				dropdown,
				popup.x,
				popup.y,
				popup.width,
				popup.height,
				FILTER_POPUP_BORDER_WIDTH);
			dropdown.setFont(JournalOverlay.compactSmallFont());
			FontMetrics metrics = dropdown.getFontMetrics();
			if (control.isChecklist())
			{
				Rectangle actionRow = filterPopupActionRowBounds(popup);
				drawChecklistActionButton(
					dropdown,
					clippedPopup,
					pointer,
					actionRow,
					FilterSelection.selectAll(control),
					metrics,
					hits);
				drawChecklistActionButton(
					dropdown,
					clippedPopup,
					pointer,
					actionRow,
					FilterSelection.selectNone(control),
					metrics,
					hits);
			}
			boolean useSelectedOptionArrow = control.isSingleSelect();
			boolean reserveMarkerGutter = control.isChecklist()
				|| control.isSingleSelect();
			for (int index = 0; index < options.size(); index++)
			{
				FilterSelection option = options.get(index);
				Rectangle row = filterPopupOptionRowBounds(popup, control, index);
				drawFilterPopupRowBackground(dropdown, row, index);
				Rectangle hit = JournalGeometry.clip(row, clippedPopup);
				if (!hit.isEmpty())
				{
					hits.add(new FilterOptionHit(hit, option));
				}
				boolean selected = filterOptionChecked(option);
				boolean hovered = hit.contains(pointer);
				int textX = row.x + 6;
				if (reserveMarkerGutter)
				{
					Rectangle marker = filterOptionMarkerBounds(row);
					if (useSelectedOptionArrow && selected)
					{
						JournalOverlay.drawDisclosureArrow(
							dropdown,
							marker,
							false,
							filterActiveOptionMarkerColor());
					}
					else if (control.isChecklist())
					{
						drawChecklistMarker(dropdown, marker, selected);
					}
					textX = filterOptionLabelX(marker);
				}
				if (control == FilterControl.TYPE
					&& (option.value() == TypeFilterOption.STARRED
						|| option.value() instanceof JournalSnapshot.QuestType))
				{
					Rectangle iconSlot = filterTypeIconSlotBounds(row, textX);
					if (option.value() == TypeFilterOption.STARRED)
					{
						ControlsRenderer.drawStarGlyph(dropdown, iconSlot, ACCENT);
					}
					else
					{
						drawQuestTypeIcon(
							dropdown,
							iconSlot,
							(JournalSnapshot.QuestType) option.value(),
							assets);
					}
					textX = filterTypeLabelX(iconSlot);
				}
				else if (control == FilterControl.DIFFICULTY
					&& option.value() instanceof JournalSnapshot.QuestDifficulty)
				{
					Rectangle iconBounds = new Rectangle(
						textX,
						row.y + (row.height - JournalOverlay.QUEST_TYPE_ICON_SIZE) / 2,
						JournalOverlay.QUEST_TYPE_ICON_SIZE,
						JournalOverlay.QUEST_TYPE_ICON_SIZE);
					drawDifficultyFilterIcon(
						dropdown,
						iconBounds,
						(JournalSnapshot.QuestDifficulty) option.value(),
						assets);
					markerTooltipHits.add(new MarkerTooltipHit(
						JournalGeometry.clip(iconBounds, clippedPopup),
						QuestListRenderer.difficultyMarkerTooltip(
							(JournalSnapshot.QuestDifficulty) option.value()),
						true));
					textX = iconBounds.x + iconBounds.width + FILTER_DIFFICULTY_ICON_GAP;
				}
				else if (control == FilterControl.MEMBERSHIP
					&& option.value() instanceof JournalSnapshot.QuestMembership)
				{
					Rectangle iconBounds = new Rectangle(
						textX,
						row.y + (row.height - JournalOverlay.QUEST_TYPE_ICON_SIZE) / 2,
						JournalOverlay.QUEST_TYPE_ICON_SIZE,
						JournalOverlay.QUEST_TYPE_ICON_SIZE);
					overlay.drawMembershipEmblem(
						dropdown,
						iconBounds,
						option.value() == JournalSnapshot.QuestMembership.MEMBERS);
					textX = iconBounds.x + iconBounds.width + FILTER_DIFFICULTY_ICON_GAP;
				}
				dropdown.setColor(filterPopupTextColor(hovered));
				JournalOverlay.drawShadowedString(
					dropdown,
					overlay.fitText(
						metrics,
						option.label(),
						Math.max(0, row.x + row.width - textX - 6)),
					textX,
					overlay.centeredTextBaseline(metrics, row));
			}
		}
		finally
		{
			dropdown.dispose();
		}
		return new RenderResult(clippedPopup, hits);
	}

	static Rectangle filterTypeIconSlotBounds(Rectangle row, int x)
	{
		return new Rectangle(
			x,
			row.y + (row.height - JournalOverlay.QUEST_TYPE_ICON_SIZE) / 2,
			JournalOverlay.QUEST_TYPE_ICON_SIZE,
			JournalOverlay.QUEST_TYPE_ICON_SIZE);
	}

	static int filterTypeLabelX(Rectangle iconSlot)
	{
		return iconSlot.x + iconSlot.width + FILTER_DIFFICULTY_ICON_GAP;
	}

	Rectangle openPopupBounds(
		JournalGeometry geometry,
		JournalSnapshot snapshot,
		Rectangle viewport)
	{
		FilterControl control = openControl;
		if (control == null)
		{
			return new Rectangle();
		}
		List<FilterSelection> options = filterSelections(snapshot, control);
		if (options.isEmpty())
		{
			return new Rectangle();
		}
		return JournalGeometry.clip(
			filterPopupBounds(
				filterControlBounds(geometry, control),
				geometry.questListPaneBounds(),
				control,
				options.size()),
			viewport);
	}

	private static void drawFilterPopupRowBackground(
		Graphics2D graphics,
		Rectangle row,
		int index)
	{
		Color rowOverlay = filterPopupRowOverlayColor(index);
		if (rowOverlay.getAlpha() > 0)
		{
			graphics.setColor(rowOverlay);
			graphics.fillRect(row.x, row.y, row.width, row.height);
		}
	}

	private void drawDifficultyFilterIcon(
		Graphics2D graphics,
		Rectangle bounds,
		JournalSnapshot.QuestDifficulty difficulty,
		JournalPanelAssets assets)
	{
		if (QuestListRenderer.isDiaryDifficulty(difficulty))
		{
			if (assets != null && assets.diaryIcon != null)
			{
				overlay.drawOutlinedPixelArtCenteredImage(graphics, assets.diaryIcon, bounds);
			}
			return;
		}

		Color color = QuestListRenderer.difficultyDotColor(difficulty);
		if (color != null)
		{
			int inset = Math.max(
				0,
				(bounds.width - JournalOverlay.DIFFICULTY_DOT_SIZE) / 2);
			QuestListRenderer.drawDifficultyDot(
				graphics,
				new Rectangle(
					bounds.x + inset,
					bounds.y + Math.max(
						0,
						(bounds.height - JournalOverlay.DIFFICULTY_DOT_SIZE) / 2),
					JournalOverlay.DIFFICULTY_DOT_SIZE,
					JournalOverlay.DIFFICULTY_DOT_SIZE),
				difficulty);
		}
	}

	private void drawQuestTypeIcon(
		Graphics2D graphics,
		Rectangle bounds,
		JournalSnapshot.QuestType type,
		JournalPanelAssets assets)
	{
		BufferedImage image = questTypeMarkerImage(type, assets);
		if (image != null)
		{
			if (type == JournalSnapshot.QuestType.SKILL)
			{
				overlay.drawPixelArtCenteredImage(graphics, image, bounds);
			}
			else
			{
				overlay.drawOutlinedPixelArtCenteredImage(graphics, image, bounds);
			}
		}
	}

	static BufferedImage questTypeMarkerImage(
		JournalSnapshot.QuestType type,
		JournalPanelAssets assets)
	{
		if (type == null || assets == null)
		{
			return null;
		}
		switch (type)
		{
			case ACHIEVEMENT_DIARY:
				return assets.diaryIcon;
			case SKILL:
				return assets.skillIcon;
			case QUEST:
			case MINIQUEST:
			case GENERIC:
			case PLAYER_QUEST:
			default:
				return assets.questIcon;
		}
	}

	private void drawChecklistActionButton(
		Graphics2D graphics,
		Rectangle clippedPopup,
		Point pointer,
		Rectangle actionRow,
		FilterSelection selection,
		FontMetrics metrics,
		List<FilterOptionHit> hits)
	{
		Rectangle button = checklistActionButtonBounds(actionRow, selection.action());
		Rectangle hit = JournalGeometry.clip(button, clippedPopup);
		if (!hit.isEmpty())
		{
			hits.add(new FilterOptionHit(hit, selection));
		}
		boolean hovered = hit.contains(pointer);
		chromeRenderer.drawHeaderControlSkin(graphics, button, hovered);
		graphics.setColor(filterPopupTextColor());
		String label = overlay.fitText(
			metrics,
			selection.label(),
			Math.max(0, button.width - 8));
		int textX = button.x + Math.max(
			0,
			(button.width - metrics.stringWidth(label)) / 2);
		JournalOverlay.drawShadowedString(
			graphics,
			label,
			textX,
			overlay.centeredTextBaseline(metrics, button));
	}

	static Rectangle filterPopupActionRowBounds(Rectangle popup)
	{
		return new Rectangle(
			popup.x + Math.min(FILTER_POPUP_PADDING, Math.max(0, popup.width / 2)),
			popup.y + Math.max(
				FILTER_POPUP_PADDING,
				popup.height - FILTER_POPUP_PADDING - FILTER_POPUP_ACTION_ROW_HEIGHT),
			Math.max(0, popup.width - FILTER_POPUP_PADDING * 2),
			FILTER_POPUP_ACTION_ROW_HEIGHT);
	}

	static Rectangle filterPopupOptionRowBounds(
		Rectangle popup,
		FilterControl control,
		int optionIndex)
	{
		return new Rectangle(
			popup.x + Math.min(FILTER_POPUP_PADDING, Math.max(0, popup.width / 2)),
			popup.y + FILTER_POPUP_PADDING
				+ Math.max(0, optionIndex) * FILTER_POPUP_ROW_HEIGHT,
			Math.max(0, popup.width - FILTER_POPUP_PADDING * 2),
			FILTER_POPUP_ROW_HEIGHT);
	}

	static Rectangle filterOptionMarkerBounds(Rectangle row)
	{
		return new Rectangle(
			row.x + 6,
			row.y + (row.height - 9) / 2,
			9,
			9);
	}

	static int filterOptionLabelX(Rectangle marker)
	{
		return marker.x + marker.width + 5;
	}

	private static List<FilterSelection> filterSelections(
		JournalSnapshot snapshot,
		FilterControl control)
	{
		List<FilterSelection> options = new ArrayList<>();
		JournalSnapshot.QuestListOptions listOptions = snapshot.getListOptions();
		switch (control)
		{
			case TYPE:
				options.add(new FilterSelection(
					control,
					TypeFilterOption.STARRED,
					"Starred"));
				for (JournalSnapshot.QuestType type : listOptions.getTypes())
				{
					options.add(new FilterSelection(control, type, typeLabel(type)));
				}
				break;
			case DIFFICULTY:
				for (JournalSnapshot.QuestDifficulty difficulty : listOptions.getDifficulties())
				{
					options.add(new FilterSelection(control, difficulty, difficultyLabel(difficulty)));
				}
				break;
			case MEMBERSHIP:
				for (JournalSnapshot.QuestMembership membership : listOptions.getMemberships())
				{
					options.add(new FilterSelection(
						control,
						membership,
						membershipFilterLabel(membership)));
				}
				break;
			case STATUS:
				for (JournalSnapshot.QuestState state : JournalSnapshot.QuestState.values())
				{
					options.add(new FilterSelection(control, state, statusLabel(state)));
				}
				break;
			case ORDER:
				for (JournalSnapshot.QuestOrder order : listOptions.getOrders())
				{
					options.add(new FilterSelection(control, order, orderLabel(order)));
				}
				break;
			default:
				break;
		}
		return options;
	}

	private boolean filterOptionChecked(FilterSelection selection)
	{
		if (selection.action() != FilterSelectionAction.VALUE)
		{
			return false;
		}
		JournalSnapshot.QuestFilter filter = manager.getQuestFilter();
		FilterControl control = selection.control();
		if (control == FilterControl.ORDER)
		{
			return filter.getOrder() == selection.value();
		}
		if (control == FilterControl.TYPE
			&& selection.value() == TypeFilterOption.STARRED)
		{
			return filter.isStarredSelected();
		}

		Set<?> selected;
		switch (control)
		{
			case TYPE:
				selected = filter.getTypes();
				break;
			case DIFFICULTY:
				selected = filter.getDifficulties();
				break;
			case MEMBERSHIP:
				selected = filter.getMemberships();
				break;
			case STATUS:
				selected = filter.getStates();
				break;
			default:
				return false;
		}
		return selected.contains(selection.value());
	}

	private static void drawChecklistMarker(
		Graphics2D graphics,
		Rectangle bounds,
		boolean checked)
	{
		if (!checked)
		{
			return;
		}
		Stroke oldStroke = graphics.getStroke();
		graphics.setColor(ACCENT);
		graphics.setStroke(new BasicStroke(
			2f,
			BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_ROUND));
		graphics.drawLine(bounds.x, bounds.y + 5, bounds.x + 3, bounds.y + 8);
		graphics.drawLine(bounds.x + 3, bounds.y + 8, bounds.x + 8, bounds.y + 1);
		graphics.setStroke(oldStroke);
	}

	private Rectangle filterControlBounds(JournalGeometry geometry, FilterControl control)
	{
		switch (control)
		{
			case TYPE:
				return typeControlBounds(geometry);
			case DIFFICULTY:
				return difficultyControlBounds(geometry);
			case MEMBERSHIP:
				return membershipControlBounds(geometry);
			case ORDER:
				return orderControlBounds(geometry);
			case STATUS:
				return statusControlBounds(geometry);
			default:
				return new Rectangle();
		}
	}

	static Color filterTriggerBackgroundColor()
	{
		return FILTER_TRIGGER_BACKGROUND;
	}

	static Color filterTriggerOuterBorderColor()
	{
		return FILTER_TRIGGER_OUTER_BORDER;
	}

	static Color filterTriggerInnerBorderColor()
	{
		return FILTER_TRIGGER_INNER_BORDER;
	}

	static Color filterTriggerTextColor()
	{
		return Color.WHITE;
	}

	static Color filterTriggerTextColor(boolean hovered)
	{
		return hovered ? JOURNAL_ORANGE : filterTriggerTextColor();
	}

	static Color filterTriggerArrowColor()
	{
		return FILTER_TRIGGER_ARROW;
	}

	static Color filterPopupTextColor()
	{
		return Color.WHITE;
	}

	static Color filterPopupTextColor(boolean hovered)
	{
		return hovered ? JOURNAL_ORANGE : filterPopupTextColor();
	}

	static Color filterPopupRowOverlayColor(int optionIndex)
	{
		return (optionIndex & 1) == 1
			? FILTER_POPUP_EVEN_ROW_OVERLAY
			: TRANSPARENT;
	}

	static Color filterActiveOptionMarkerColor()
	{
		return ACCENT;
	}

	static Rectangle checklistActionButtonBounds(
		Rectangle actionRow,
		FilterSelectionAction action)
	{
		Objects.requireNonNull(actionRow, "actionRow");
		if (action != FilterSelectionAction.SELECT_ALL
			&& action != FilterSelectionAction.SELECT_NONE)
		{
			throw new IllegalArgumentException("Checklist action must select all or none");
		}

		int horizontalInset = Math.min(3, Math.max(0, actionRow.width / 2));
		int gap = Math.min(3, Math.max(0, actionRow.width - horizontalInset * 2));
		int innerWidth = Math.max(0, actionRow.width - horizontalInset * 2);
		int availableWidth = Math.max(0, innerWidth - gap);
		int buttonHeight = Math.min(
			FILTER_POPUP_ACTION_BUTTON_HEIGHT,
			Math.max(0, actionRow.height));
		int buttonY = actionRow.y + Math.max(
			0,
			(actionRow.height - buttonHeight) / 2);
		int allWidth = availableWidth / 2;
		int noneWidth = availableWidth - allWidth;
		int allX = actionRow.x + horizontalInset;
		int noneX = allX + allWidth + gap;
		return action == FilterSelectionAction.SELECT_ALL
			? new Rectangle(allX, buttonY, allWidth, buttonHeight)
			: new Rectangle(noneX, buttonY, noneWidth, buttonHeight);
	}

	static Rectangle filterPopupBounds(
		Rectangle anchor,
		Rectangle pane,
		FilterControl control,
		int optionCount)
	{
		int actionHeight = control != null && control.isChecklist()
			? FILTER_POPUP_ACTION_ROW_HEIGHT
			: 0;
		int height = Math.max(0, optionCount) * FILTER_POPUP_ROW_HEIGHT
			+ actionHeight
			+ FILTER_POPUP_PADDING * 2;
		return filterPopupBoundsForHeight(anchor, pane, height);
	}

	private static Rectangle filterPopupBoundsForHeight(
		Rectangle anchor,
		Rectangle pane,
		int height)
	{
		int paneInset = Math.min(
			JournalOverlay.LIST_CONTROL_HORIZONTAL_INSET,
			Math.max(0, pane.width / 2));
		int innerX = pane.x + paneInset;
		int innerWidth = Math.max(0, pane.width - paneInset * 2);
		int width = Math.min(
			innerWidth,
			Math.max(Math.max(0, anchor.width), FILTER_POPUP_MINIMUM_WIDTH));
		int preferredX = anchor.x + anchor.width - width;
		int maximumX = Math.max(innerX, innerX + innerWidth - width);
		int x = JournalOverlay.clamp(preferredX, innerX, maximumX);
		int maximumY = Math.max(pane.y, pane.y + pane.height - height);
		int preferredY = anchor.y + anchor.height + FILTER_ROW_GAP;
		int y = JournalOverlay.clamp(preferredY, pane.y, maximumY);
		return new Rectangle(x, y, width, Math.min(height, pane.height));
	}

	boolean isOpen(FilterControl control)
	{
		return openControl == control;
	}

	void toggle(FilterControl control)
	{
		if (control != null)
		{
			openControl = openControl == control ? null : control;
		}
	}

	void close()
	{
		openControl = null;
	}

	boolean isOpen()
	{
		return openControl != null;
	}

	FilterControl controlAt(Point point)
	{
		return hitState.controlAt(point);
	}

	FilterSelection selectionAt(Point point)
	{
		return hitState.selectionAt(point);
	}

	boolean popupContains(Point point)
	{
		return hitState.popupContains(point);
	}

	void publishBounds(
		JournalGeometry geometry,
		Rectangle viewport,
		boolean showFilters,
		RenderResult render)
	{
		if (!showFilters)
		{
			hitState = FilterHitState.empty();
			return;
		}
		hitState = new FilterHitState(
			JournalGeometry.clip(typeControlBounds(geometry), viewport),
			JournalGeometry.clip(difficultyControlBounds(geometry), viewport),
			JournalGeometry.clip(membershipControlBounds(geometry), viewport),
			JournalGeometry.clip(orderControlBounds(geometry), viewport),
			JournalGeometry.clip(statusControlBounds(geometry), viewport),
			JournalGeometry.clip(render.popupBounds, viewport),
			render.optionHits);
	}

	void clear()
	{
		openControl = null;
		hitState = FilterHitState.empty();
	}

	static final class RenderResult
	{
		private static final RenderResult EMPTY = new RenderResult(
			new Rectangle(),
			Collections.emptyList());

		private final Rectangle popupBounds;
		private final List<FilterOptionHit> optionHits;

		private RenderResult(
			Rectangle popupBounds,
			List<FilterOptionHit> optionHits)
		{
			this.popupBounds = new Rectangle(popupBounds);
			this.optionHits = Collections.unmodifiableList(new ArrayList<>(optionHits));
		}

		static RenderResult empty()
		{
			return EMPTY;
		}
	}

	private static final class FilterOptionHit
	{
		private final Rectangle bounds;
		private final FilterSelection selection;

		private FilterOptionHit(Rectangle bounds, FilterSelection selection)
		{
			this.bounds = new Rectangle(bounds);
			this.selection = Objects.requireNonNull(selection, "selection");
		}

		private boolean contains(Point point)
		{
			return point != null && bounds.contains(point);
		}
	}

	private static final class FilterHitState
	{
		private static final FilterHitState EMPTY = new FilterHitState(
			new Rectangle(),
			new Rectangle(),
			new Rectangle(),
			new Rectangle(),
			new Rectangle(),
			new Rectangle(),
			Collections.emptyList());

		private final Rectangle typeBounds;
		private final Rectangle difficultyBounds;
		private final Rectangle membershipBounds;
		private final Rectangle orderBounds;
		private final Rectangle statusBounds;
		private final Rectangle popupBounds;
		private final List<FilterOptionHit> optionHits;

		private FilterHitState(
			Rectangle typeBounds,
			Rectangle difficultyBounds,
			Rectangle membershipBounds,
			Rectangle orderBounds,
			Rectangle statusBounds,
			Rectangle popupBounds,
			List<FilterOptionHit> optionHits)
		{
			this.typeBounds = new Rectangle(typeBounds);
			this.difficultyBounds = new Rectangle(difficultyBounds);
			this.membershipBounds = new Rectangle(membershipBounds);
			this.orderBounds = new Rectangle(orderBounds);
			this.statusBounds = new Rectangle(statusBounds);
			this.popupBounds = new Rectangle(popupBounds);
			this.optionHits = Collections.unmodifiableList(new ArrayList<>(optionHits));
		}

		private static FilterHitState empty()
		{
			return EMPTY;
		}

		private FilterControl controlAt(Point point)
		{
			if (point == null)
			{
				return null;
			}
			if (typeBounds.contains(point))
			{
				return FilterControl.TYPE;
			}
			if (difficultyBounds.contains(point))
			{
				return FilterControl.DIFFICULTY;
			}
			if (membershipBounds.contains(point))
			{
				return FilterControl.MEMBERSHIP;
			}
			if (orderBounds.contains(point))
			{
				return FilterControl.ORDER;
			}
			return statusBounds.contains(point) ? FilterControl.STATUS : null;
		}

		private FilterSelection selectionAt(Point point)
		{
			for (FilterOptionHit hit : optionHits)
			{
				if (hit.contains(point))
				{
					return hit.selection;
				}
			}
			return null;
		}

		private boolean popupContains(Point point)
		{
			return point != null && popupBounds.contains(point);
		}
	}
}
