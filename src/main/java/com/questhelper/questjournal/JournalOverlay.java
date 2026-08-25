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

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questjournal.TooltipRenderer.MarkerTooltipHit;
import com.questhelper.questjournal.TooltipRenderer.SemanticHit;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.gameval.SpriteID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.Text;

final class JournalOverlay extends Overlay
{
	static final Color BACKGROUND = new Color(0x46, 0x3B, 0x2E, 0xF2);
	static final Color ACCENT = new Color(0xE9, 0xBF, 0x6F);
	static final Color TITLE_ORANGE = new Color(0xFF, 0x98, 0x1F);
	private static final Color JOURNAL_ORANGE = new Color(0xFF, 0x99, 0x33);
	static final Color BORDER_DARK = new Color(0x08, 0x06, 0x04, 0xEE);
	static final Color BORDER_LIGHT = new Color(0x71, 0x64, 0x51, 0xB8);
	static final Color MUTED = new Color(0xB8, 0xA0, 0x74);
	static final Color TEXT = Color.WHITE;
	static final Color CELL_CONTOUR = new Color(0x71, 0x64, 0x51, 0x0F);
	private static final Color OBJECTIVE_COMPLETE_COLOR = MUTED;
	private static final Color REQUIREMENT_GRAY = new Color(0x99, 0x99, 0x99);
	private static final Color REQUIREMENT_MET_COLOR = new Color(0x1F, 0xFF, 0x1F, 0x70);
	private static final Color ACTIVE_COLOR = JOURNAL_ORANGE;
	private static final Color QUEST_COMPLETE_COLOR = new Color(0x1F, 0xFF, 0x1F);
	private static final Color QUEST_IN_PROGRESS_COLOR = new Color(0xFF, 0xFF, 0x1F);
	private static final Color QUEST_NOT_STARTED_COLOR = new Color(0xFF, 0x1F, 0x1F);
	private static final Color LOCKED_COLOR = MUTED;
	private static final Color FADED_COLOR = new Color(0x78, 0x70, 0x62);
	private static final Color UNMET_COLOR = new Color(0xFF, 0x66, 0x66);
	static final Color CONTROL_BEVEL_LIGHT = new Color(0xAA9368);
	static final Color CONTROL_BEVEL_MIDDLE = new Color(0x60523D);
	static final Color CONTROL_BEVEL_DARK = new Color(0x2B2B29);
	static final Color CONTROL_FACE_LIGHT = new Color(0x8A785B);
	static final Color CONTROL_FACE_MIDDLE = new Color(0x6A5C44);
	static final Color CONTROL_FACE_DARK = new Color(0x443B2C);
	static final Color CONTROL_BORDER = new Color(0x00, 0x00, 0x01);
	static final Color CONTROL_ICON = new Color(0x21, 0x1C, 0x15);
	static final Color CONTROL_ICON_LIGHT = new Color(0x2B251B);
	static final Color CONTROL_ICON_DARK = new Color(0x0A0906);
	static final Color SCROLL_TRACK = new Color(0x25, 0x20, 0x19);
	static final Color SCROLL_TRACK_EDGE = new Color(0x3D, 0x34, 0x26);
	static final Color SCROLL_THUMB_LEFT = new Color(0x81, 0x70, 0x51);
	static final Color SCROLL_THUMB_RIGHT = new Color(0x4B, 0x41, 0x31);
	static final Color SCROLL_BEZEL_DARK = Color.BLACK;
	static final Color SCROLL_BEZEL_LIGHT = new Color(0x9B, 0x88, 0x64);
	private static final Color TEXT_SHADOW = Color.BLACK;
	private static final Color FREE_TO_PLAY_STAR = new Color(0xC4, 0xC4, 0xC4);
	private static final String DEFAULT_HEADER_TITLE = "Quest Journal";
	private static final String EASTER_EGG_HEADER_TITLE = "Quest Helper by Ruined Heir";
	private static final int EASTER_EGG_CLICK_TARGET = 10;
	static final Color TITLE_SEPARATOR_EDGE = new Color(0x49, 0x49, 0x46);
	static final Color TITLE_SEPARATOR_TOP = new Color(0x25, 0x25, 0x22);
	static final Color TITLE_SEPARATOR_BOTTOM = new Color(0x35, 0x35, 0x32);
	private static final JournalSnapshot.IconIdentity ENEMY_ICON_IDENTITY =
		JournalSnapshot.IconIdentity.skill("Slayer");
	private static final Font JOURNAL_FONT = reducedFont(reducedFont(FontManager.getRunescapeFont()));
	private static final Font JOURNAL_OVERVIEW_TITLE_FONT =
		reducedFont(FontManager.getRunescapeFont());
	private static final Font JOURNAL_SMALL_FONT = reducedFont(FontManager.getRunescapeSmallFont());
	private static final Font JOURNAL_COMPACT_SMALL_FONT = reducedFont(JOURNAL_SMALL_FONT);
	private static final Font SET_ACTIVE_QUEST_FONT = JOURNAL_COMPACT_SMALL_FONT.deriveFont(
		JOURNAL_COMPACT_SMALL_FONT.getSize2D() + 2f);
	private static final Font JOURNAL_QUEST_LIST_FONT =
		reducedFont(FontManager.getRunescapeFont());

	static final int QUEST_ROW_HEIGHT = 22;
	private static final int QUEST_ROW_GAP = 1;
	static final int QUEST_PROGRESS_FOOTER_HEIGHT = 24;
	static final int QUEST_PROGRESS_FOOTER_GAP = QUEST_ROW_GAP;
	static final int QUEST_PROGRESS_VISUAL_OFFSET_Y = 2;
	static final int LIST_HEADER_ACTION_HEIGHT = 26;
	static final int ACTIVE_QUEST_CONTROL_HEIGHT = LIST_HEADER_ACTION_HEIGHT;
	static final int ACTIVE_QUEST_CONTROL_GAP = 4;
	static final int PANEL_CONTENT_PADDING = JournalGeometry.contentPadding();
	static final int QUEST_LIST_CONTENT_PADDING = Math.max(0, PANEL_CONTENT_PADDING - 1);
	static final int CONTENT_ICON_TEXT_GAP = 4;
	private static final int SECTION_GROUP_GAP = 8;
	static final int PANE_SEPARATOR_INSET = 5;
	static final int SELECTED_HEADER_HORIZONTAL_PADDING = 10;
	static final int SELECTED_HEADER_ROW_GAP = 2;
	static final int DIFFICULTY_DOT_SIZE = 8;
	static final int DIFFICULTY_DOT_TEXT_GAP = 4;
	static final int QUEST_TYPE_ICON_SIZE = 12;
	private static final int SCROLL_STEP = QUEST_ROW_HEIGHT + QUEST_ROW_GAP;
	private static final int SCROLLBAR_GUTTER_GAP = PANEL_CONTENT_PADDING;
	private static final int SCROLLBAR_VISUAL_WIDTH = 14;
	static final int SCROLLBAR_THUMB_ARC = 6;
	static final int LIST_HEADER_CONTROL_GAP = 3;
	static final int LIST_HEADER_ACTION_HORIZONTAL_INSET = 7;
	static final int LIST_HEADER_ACTION_ICON_SIZE = 14;
	static final int LIST_HEADER_ACTION_LABEL_GAP = 3;
	static final int LIST_CONTROL_HORIZONTAL_INSET = 6;
	static final int DETAIL_ICON_SIZE = 18;
	static final int TITLE_ICON_SIZE = 13;
	static final int SECTION_HEADING_HEIGHT = 18;
	static final int SECTION_HEADING_BASELINE = 12;
	static final int NOTE_PARAGRAPH_GAP = 7;
	static final int TITLE_SEPARATOR_HEIGHT = JournalGeometry.titleSeparatorHeight();
	static final int PANEL_ASSET_RETRY_FRAMES = 30;
	static final int CHECKLIST_TOGGLE_GAP = 4;
	static final int CHECKLIST_CONTENT_INDENT = 16;
	static final int CHECKLIST_TOGGLE_WIDTH = 66;
	static final int CHECKLIST_TOGGLE_HEIGHT = 18;
	static final int CLEAR_ACTIVE_QUEST_CONTROL_SIZE = ACTIVE_QUEST_CONTROL_HEIGHT;
	static final int CLEAR_ACTIVE_QUEST_CONTROL_INSET = 2;
	static final int CLEAR_ACTIVE_QUEST_CONTROL_GAP = 3;
	static final int ACTIVE_QUEST_ICON_SIZE = 16;
	static final int ACTIVE_QUEST_ICON_GAP = 4;
	static final int ACTIVE_QUEST_CONTENT_HORIZONTAL_INSET = 5;
	static final int SET_ACTIVE_QUEST_CONTROL_WIDTH = 116;
	static final int SET_ACTIVE_QUEST_CONTROL_HEIGHT = LIST_HEADER_ACTION_HEIGHT;
	static final int SET_ACTIVE_QUEST_CONTENT_INSET =
		Math.max(0, SET_ACTIVE_QUEST_CONTROL_HEIGHT / 2 - PANE_SEPARATOR_INSET + 1);
	static final int QUEST_ROW_TRAILING_ICON_SIZE = 16;
	static final int ACTIVE_QUEST_LIST_ICON_SIZE = QUEST_ROW_TRAILING_ICON_SIZE;
	static final int QUEST_ROW_INLINE_ICON_GAP = 3;

	private final Client client;
	private final QuestJournalManager manager;
	private final SpriteManager spriteManager;
	private final ItemManager itemManager;
	private final SkillIconManager skillIconManager;
	private final FilterRenderer filterRenderer;
	private final QuestListRenderer questListRenderer;
	private final ChromeRenderer chromeRenderer;
	private final ControlsRenderer controlsRenderer;
	private final DetailRenderer detailRenderer;
	private final QuestViewRenderer selectedRenderer;
	private final TooltipRenderer tooltipRenderer;
	private final Set<Long> watchedItemIcons = Collections.newSetFromMap(
		new ConcurrentHashMap<>());
	private final Map<JournalSnapshot.IconIdentity, Boolean> visibleSemanticIconPixels =
		new ConcurrentHashMap<>();
	private final Map<BufferedImage, Boolean> visibleStaticImagePixels =
		new ConcurrentHashMap<>();
	private final Map<BufferedImage, Rectangle> visibleImageBoundsCache =
		new ConcurrentHashMap<>();
	private final Map<BufferedImage, BufferedImage> blackOutlinedIconCache =
		new ConcurrentHashMap<>();
	private final Object scrollStateLock = new Object();

	private volatile HitState hitState = HitState.empty();

	private volatile int listScrollOffset;
	private volatile int mainScrollOffset;
	private volatile int detailScrollOffset;
	private long scrollStateRevision;
	private volatile boolean filtersVisible;
	private volatile int titleIconClickCount;
	private int listMaximumScroll;
	private int mainMaximumScroll;
	private int detailMaximumScroll;
	private String renderedQuestId;
	private JournalSnapshot.SelectedQuest renderedSelectedQuest;

	@Inject
	JournalOverlay(
		Client client,
		QuestHelperPlugin owner,
		QuestJournalManager manager,
		SpriteManager spriteManager,
		ItemManager itemManager,
		SkillIconManager skillIconManager)
	{
		super(owner);
		this.client = client;
		this.manager = manager;
		this.spriteManager = spriteManager;
		this.itemManager = itemManager;
		this.skillIconManager = skillIconManager;
		this.chromeRenderer = new ChromeRenderer(this, spriteManager);
		this.filterRenderer = new FilterRenderer(
			this,
			manager,
			chromeRenderer);
		this.questListRenderer = new QuestListRenderer(
			this,
			manager,
			chromeRenderer);
		this.controlsRenderer = new ControlsRenderer(
			this,
			manager,
			filterRenderer,
			chromeRenderer);
		this.detailRenderer = new DetailRenderer(
			this,
			chromeRenderer);
		this.selectedRenderer = new QuestViewRenderer(
			this,
			manager,
			detailRenderer,
			chromeRenderer);
		this.tooltipRenderer = new TooltipRenderer(
			this,
			client,
			chromeRenderer);

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(Overlay.PRIORITY_HIGHEST);
		setMovable(true);
		setResizable(true);
		setMinimumSize(Math.min(JournalGeometry.minimumWidth(), JournalGeometry.minimumHeight()));
		setSnappable(false);
		setResettable(true);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!manager.isJournalOpen() || client.getGameState() != GameState.LOGGED_IN)
		{
			clearBounds();
			return null;
		}

		Dimension canvasSize = client.getRealDimensions();
		if (canvasSize == null || canvasSize.width <= 0 || canvasSize.height <= 0)
		{
			clearBounds();
			return null;
		}
		Rectangle canvasBounds = new Rectangle(
			0,
			0,
			Math.max(0, canvasSize.width),
			Math.max(0, canvasSize.height));
		Rectangle viewport = manager.getJournalContentBounds(canvasBounds);
		QuestJournalManager.JournalPanelRenderState panelState =
			manager.getJournalRenderState(new Rectangle(viewport));
		Rectangle requested = panelState.bounds();
		boolean maximized = panelState.maximized();
		long panelStateRevision = panelState.revision();
		JournalSnapshot snapshot = manager.getJournalSnapshot();
		if (requested == null || snapshot == null || viewport.isEmpty())
		{
			clearBounds();
			return null;
		}
		if (!manager.isJournalRenderStateCurrent(panelStateRevision))
		{
			return currentOverlayBounds().getSize();
		}
		if (!maximized)
		{
			requested = managedRequestedBounds(requested);
		}

		boolean showFilters = filtersVisible;
		boolean manualActiveControls =
			controlsRenderer.isManualActiveQuestSelection();
		JournalGeometry geometry = JournalGeometry.create(
			requested,
			viewport,
			showFilters,
			manualActiveControls);
		Rectangle panel = geometry.panelBounds();
		if (panel.isEmpty())
		{
			clearBounds();
			return null;
		}
		JournalSnapshot.SelectedQuest selectedQuest = snapshot.getSelectedQuest();
		String selectedQuestId = selectedQuest == null ? null : selectedQuest.getOverview().getId();
		boolean selectedQuestChanged = !Objects.equals(renderedQuestId, selectedQuestId);
		boolean selectedQuestSnapshotChanged = renderedSelectedQuest != selectedQuest;
		ScrollRenderState scrollRenderState = captureScrollRenderState();
		int nextListScrollOffset = scrollRenderState.listScrollOffset;
		int nextMainScrollOffset = selectedQuest == null || selectedQuestChanged
			? 0
			: scrollRenderState.mainScrollOffset;
		int nextDetailScrollOffset = selectedQuest == null || selectedQuestChanged
			? 0
			: scrollRenderState.detailScrollOffset;
		int nextMainMaximumScroll = 0;
		int nextDetailMaximumScroll = 0;

		Point pointer = manager.getPointerCanvasPoint();
		if (pointer == null)
		{
			pointer = new Point(-1, -1);
		}

		Rectangle host = currentOverlayBounds();
		JournalPanelAssets assets = chromeRenderer.currentPanelAssets();
		Graphics2D output = (Graphics2D) graphics.create();
		QuestListRenderer.RenderResult questListRender;
		JournalSnapshot.QuestOverview viewedOverview = selectedQuest == null
			? null
			: selectedQuest.getOverview();
		Rectangle setActiveQuestBounds = controlsRenderer.setActiveQuestBounds(
			geometry,
			viewedOverview,
			snapshot.getActiveQuest());
		Rectangle clearActiveQuestBounds =
			ControlsRenderer.clearActiveQuestBounds(
			controlsRenderer.activeQuestControlBounds(geometry),
			snapshot.getActiveQuest());
		Rectangle returnToActiveQuestBounds =
			controlsRenderer.returnToActiveQuestBounds(
				geometry,
				selectedQuestId,
				snapshot.getActiveQuest());
		Rectangle mainScrollBounds = selectedQuest == null
			? geometry.mainContentBounds()
			: ControlsRenderer.selectedQuestMainContentBounds(
				geometry.mainContentBounds(),
				manualActiveControls);
		Rectangle renderedTitleIconBounds;
		List<SemanticHit> semanticHits = Collections.emptyList();
		FilterRenderer.RenderResult filterDropdownRender =
			FilterRenderer.RenderResult.empty();
		List<MarkerTooltipHit> markerTooltipHits = new ArrayList<>();
		Rectangle filterPopupOcclusion = showFilters
			? filterRenderer.openPopupBounds(geometry, snapshot, viewport)
			: new Rectangle();
		try
		{
			output.translate(-host.x, -host.y);
			output.clip(viewport);
			ChromeRenderer.applyQualityHints(output);
			output.drawImage(
				chromeRenderer.cachedBackground(
					panel.getSize(),
					geometry.hasDetailPane(),
					showFilters,
					manualActiveControls,
					assets),
				panel.x,
				panel.y,
				null);
			renderedTitleIconBounds = chromeRenderer.drawHeader(
				output,
				geometry,
				pointer,
				maximized,
				assets);
			controlsRenderer.drawPaneLabels(
				output,
				geometry,
				snapshot,
				pointer,
				assets,
				markerTooltipHits,
				showFilters,
				clearActiveQuestBounds,
				returnToActiveQuestBounds);
			questListRender = questListRenderer.draw(
				output,
				geometry,
				snapshot,
				pointer,
				assets,
				markerTooltipHits,
				filterPopupOcclusion,
				nextListScrollOffset);
			nextListScrollOffset = questListRender.scrollOffset;
			if (selectedQuest == null)
			{
				drawEmptyState(output, geometry);
			}
			else
			{
				QuestViewRenderer.RenderResult selectedRender =
					selectedRenderer.draw(
					output,
					geometry,
					selectedQuest,
					nextMainScrollOffset,
					nextDetailScrollOffset);
				semanticHits = selectedRender.semanticHits;
				nextMainScrollOffset = selectedRender.mainScroll;
				nextDetailScrollOffset = selectedRender.detailScroll;
				nextMainMaximumScroll = selectedRender.mainMaximumScroll;
				nextDetailMaximumScroll = selectedRender.detailMaximumScroll;
			}
			controlsRenderer.drawSetActiveQuestControl(
				output,
				setActiveQuestBounds,
				pointer);
			chromeRenderer.drawResizeHandle(output, geometry, maximized);
			tooltipRenderer.drawSemanticFeedback(
				output,
				viewport,
				pointer,
				semanticHits);
			tooltipRenderer.drawSettingsTooltip(
				output,
				settingsButtonBounds(geometry.maximizeButtonBounds()),
				viewport,
				pointer);
			if (showFilters)
			{
				filterDropdownRender = filterRenderer.drawOpenDropdown(
					output,
					geometry,
					snapshot,
					pointer,
					viewport,
					assets,
					markerTooltipHits);
			}
			tooltipRenderer.drawMarkerTooltip(
				output,
				viewport,
				pointer,
				markerTooltipHits,
				filterRenderer.isOpen());
		}
		finally
		{
			output.dispose();
		}

		QuestListRenderer.RenderResult committedQuestListRender =
			questListRender;
		Rectangle committedSetActiveQuestBounds = setActiveQuestBounds;
		Rectangle committedClearActiveQuestBounds = clearActiveQuestBounds;
		Rectangle committedReturnToActiveQuestBounds =
			returnToActiveQuestBounds;
		Rectangle committedMainScrollBounds = mainScrollBounds;
		Rectangle committedTitleIconBounds = renderedTitleIconBounds;
		List<SemanticHit> committedSemanticHits = semanticHits;
		FilterRenderer.RenderResult committedFilterDropdownRender =
			filterDropdownRender;
		int committedListScrollOffset = nextListScrollOffset;
		int committedMainScrollOffset = nextMainScrollOffset;
		int committedDetailScrollOffset = nextDetailScrollOffset;
		int committedMainMaximumScroll = nextMainMaximumScroll;
		int committedDetailMaximumScroll = nextDetailMaximumScroll;
		boolean committed = manager.commitJournalRender(panelStateRevision, () ->
		{
			if (!commitScrollOffsets(
				scrollRenderState.revision,
				committedListScrollOffset,
				committedMainScrollOffset,
				committedDetailScrollOffset))
			{
				return;
			}
			if (selectedQuestSnapshotChanged)
			{
				visibleSemanticIconPixels.clear();
				visibleImageBoundsCache.clear();
				blackOutlinedIconCache.clear();
			}
			renderedQuestId = selectedQuestId;
			renderedSelectedQuest = selectedQuest;
			listMaximumScroll = committedQuestListRender.maximumScroll;
			mainMaximumScroll = committedMainMaximumScroll;
			detailMaximumScroll = committedDetailMaximumScroll;
			if (!maximized)
			{
				normalizeManagedPreferredSize(panel);
			}
			publishBounds(
				geometry,
				viewport,
				committedQuestListRender.rows,
				committedSemanticHits,
				committedSetActiveQuestBounds,
				committedClearActiveQuestBounds,
				committedReturnToActiveQuestBounds,
				committedTitleIconBounds,
				committedQuestListRender.scrollBounds,
				committedMainScrollBounds,
				selectedQuestId,
				snapshot.getActiveQuest() == null
					? null
					: snapshot.getActiveQuest().getId(),
					maximized);
			filterRenderer.publishBounds(
				geometry,
				viewport,
				showFilters,
				committedFilterDropdownRender);
			getBounds().setBounds(panel);
			if (getPreferredLocation() == null && !manager.isDirectlyManagingJournal())
			{
				setPreferredLocation(panel.getLocation());
				setPreferredSize(panel.getSize());
			}
		});
		if (!committed)
		{
			discardUncommittedRenderCaches();
		}
		return committed ? panel.getSize() : currentOverlayBounds().getSize();
	}

	private void discardUncommittedRenderCaches()
	{
		selectedRenderer.clearCaches();
		questListRenderer.clear();
		chromeRenderer.clearBackgroundCache();
		clearIconCaches(false);
	}

	private Rectangle managedRequestedBounds(Rectangle requested)
	{
		Point managedLocation = getPreferredLocation();
		Dimension managedSize = getPreferredSize();
		Rectangle host = currentOverlayBounds();
		if (host.isEmpty() || managedLocation == null && managedSize == null)
		{
			return requested;
		}

		Rectangle managed = new Rectangle(requested);
		if (managedLocation != null)
		{
			managed.setLocation(host.getLocation());
		}
		if (managedSize != null)
		{
			managed.setSize(
				Math.max(JournalGeometry.minimumWidth(), host.width),
				Math.max(JournalGeometry.minimumHeight(), host.height));
		}
		return managed;
	}

	@Override
	public Rectangle getParentBounds()
	{
		Rectangle published = hitState.viewportBounds();
		if (!published.isEmpty())
		{
			return published;
		}
		Dimension canvasSize = client.getRealDimensions();
		return canvasSize == null
			? new Rectangle()
			: manager.getJournalContentBounds(new Rectangle(canvasSize));
	}

	@Override
	public void revalidate()
	{
		if (manager != null)
		{
			manager.onJournalOverlayReset();
		}
	}

	void normalizeManagedPreferredSize(Rectangle normalizedBounds)
	{
		Dimension preferredSize = getPreferredSize();
		if (preferredSize == null || normalizedBounds == null || normalizedBounds.isEmpty())
		{
			return;
		}

		Dimension normalizedSize = normalizedBounds.getSize();
		if (!preferredSize.equals(normalizedSize))
		{
			setPreferredSize(normalizedSize);
		}
	}

	void panelAssetsChanged()
	{
		clearIconCaches(true);
		selectedRenderer.clearSurfaceCaches();
	}

	static Rectangle titleSeparatorBounds(int width, int height, int headerHeight)
	{
		return ChromeRenderer.titleSeparatorBounds(
			width,
			height,
			headerHeight);
	}

	String headerTitleText()
	{
		return titleIconClickCount >= EASTER_EGG_CLICK_TARGET
			? EASTER_EGG_HEADER_TITLE
			: DEFAULT_HEADER_TITLE;
	}

	static Color journalTitleColor()
	{
		return JOURNAL_ORANGE;
	}

	void recordTitleIconClick()
	{
		if (titleIconClickCount < EASTER_EGG_CLICK_TARGET)
		{
			titleIconClickCount++;
		}
	}

	static Rectangle settingsButtonBounds(Rectangle maximizeBounds)
	{
		if (maximizeBounds == null || maximizeBounds.isEmpty())
		{
			return new Rectangle();
		}
		return new Rectangle(
			maximizeBounds.x - maximizeBounds.width - 4,
			maximizeBounds.y,
			maximizeBounds.width,
			maximizeBounds.height);
	}

	static int centeredHeaderGroupX(Rectangle header, int groupWidth)
	{
		Objects.requireNonNull(header, "header");
		int fittedWidth = Math.min(Math.max(0, groupWidth), Math.max(0, header.width));
		return header.x + (header.width - fittedWidth) / 2;
	}

	Rectangle visibleImageBounds(BufferedImage image)
	{
		return new Rectangle(visibleImageBoundsCache.computeIfAbsent(
			image,
			JournalGeometry::visibleImageBounds));
	}

	static Rectangle headerControlShadowBounds(Rectangle bounds)
	{
		return ChromeRenderer.headerControlShadowBounds(bounds);
	}

	static Rectangle headerControlFaceBounds(Rectangle bounds)
	{
		return ChromeRenderer.headerControlFaceBounds(bounds);
	}

	static Rectangle closeIconBounds(Rectangle bounds)
	{
		return ChromeRenderer.closeIconBounds(bounds);
	}

	static void drawListElementSurface(Graphics2D graphics, Rectangle bounds, Color color)
	{
		if (graphics == null || bounds == null || bounds.isEmpty() || color == null)
		{
			return;
		}
		graphics.setColor(color);
		graphics.fillRect(
			bounds.x,
			bounds.y,
			bounds.width,
			Math.max(0, bounds.height - 1));
	}

	static int scrollbarTrackArc()
	{
		return SCROLLBAR_THUMB_ARC;
	}

	static int scrollbarThumbArc()
	{
		return SCROLLBAR_THUMB_ARC;
	}

	private void drawEmptyState(
		Graphics2D graphics,
		JournalGeometry geometry)
	{
		Rectangle content = geometry.mainContentBounds();
		if (content.isEmpty())
		{
			return;
		}

		Graphics2D pane = clippedGraphics(graphics, content);
		try
		{
			drawCenteredEmptyMessage(pane, content, emptyStateMessage());
		}
		finally
		{
			pane.dispose();
		}
	}

	static String emptyStateMessage()
	{
		return "Select a quest to view its steps";
	}

	static String emptyQuestListMessage()
	{
		return "No quests match your filters";
	}

	static boolean canActivateQuest(
		JournalSnapshot.QuestOverview overview,
		JournalSnapshot.ActiveQuest activeQuest)
	{
		return overview != null
			&& overview.getState() != JournalSnapshot.QuestState.COMPLETE
			&& (activeQuest == null || !overview.getId().equals(activeQuest.getId()));
	}

	static Rectangle scrollBodyBounds(Rectangle content, boolean scrollbarVisible)
	{
		int reserved = scrollbarVisible
			? Math.min(
				Math.max(0, content.width),
				SCROLLBAR_VISUAL_WIDTH + SCROLLBAR_GUTTER_GAP)
			: 0;
		return new Rectangle(
			content.x,
			content.y,
			Math.max(0, content.width - reserved),
			content.height);
	}

	static int sectionStartCursor(int cursor, boolean hasPreviousSection)
	{
		return hasPreviousSection ? cursor + SECTION_GROUP_GAP : cursor;
	}

	static String objectiveSectionGroupId(JournalSnapshot.Objective objective)
	{
		if (objective == null)
		{
			return "";
		}
		return objective.getSectionId().isEmpty()
			? "title:" + objective.getSection()
			: "id:" + objective.getSectionId();
	}

	static String sectionChecklistId(String sectionId)
	{
		return "section:" + (sectionId == null ? "" : sectionId);
	}

	static String membershipLabel(boolean members)
	{
		return members ? "Members" : "Free to play";
	}

	void drawMembershipEmblem(Graphics2D graphics, Rectangle bounds, boolean members)
	{
		BufferedImage nativeStar = spriteManager == null
			? null
			: spriteManager.getSprite(membershipEmblemSpriteId(members), 0);
		if (nativeStar != null && hasVisiblePixels(nativeStar))
		{
			drawPixelArtCenteredImage(graphics, nativeStar, bounds);
			return;
		}

		int centerX = bounds.x + bounds.width / 2;
		int centerY = bounds.y + bounds.height / 2;
		int outerRadius = Math.max(2, (Math.min(bounds.width, bounds.height) - 1) / 2);
		double innerRadius = outerRadius * 0.55;
		Polygon star = new Polygon();
		for (int point = 0; point < 16; point++)
		{
			double angle = -Math.PI / 2.0 + point * Math.PI / 8.0;
			double radius = point % 2 == 0 ? outerRadius : innerRadius;
			star.addPoint(
				(int) Math.round(centerX + Math.cos(angle) * radius),
				(int) Math.round(centerY + Math.sin(angle) * radius));
		}
		graphics.setColor(members ? ACCENT : FREE_TO_PLAY_STAR);
		graphics.fillPolygon(star);
		graphics.setColor(Color.BLACK);
		graphics.drawPolygon(star);
	}

	static int membershipEmblemSpriteId(boolean members)
	{
		return members
			? SpriteID.WorldswitcherStars.MEMBERS
			: SpriteID.WorldswitcherStars.FREE;
	}

	static void drawDisclosureArrow(
		Graphics2D graphics,
		Rectangle bounds,
		boolean expanded,
		Color color)
	{
		int centerX = bounds.x + bounds.width / 2;
		int centerY = bounds.y + bounds.height / 2;
		Polygon arrow = new Polygon();
		if (expanded)
		{
			arrow.addPoint(centerX - 3, centerY - 2);
			arrow.addPoint(centerX + 3, centerY - 2);
			arrow.addPoint(centerX, centerY + 2);
		}
		else
		{
			arrow.addPoint(centerX - 2, centerY - 3);
			arrow.addPoint(centerX - 2, centerY + 3);
			arrow.addPoint(centerX + 2, centerY);
		}
		graphics.setColor(color);
		graphics.fillPolygon(arrow);
	}

	static Color checklistToggleIconColor()
	{
		return CONTROL_ICON_DARK;
	}

	static int detailCardHeight(
		int lineCount,
		int lineHeight,
		int fontHeight,
		int iconHeight)
	{
		int count = Math.max(0, lineCount);
		int lineBlockHeight = count == 0
			? 0
			: Math.max(0, fontHeight) + (count - 1) * Math.max(0, lineHeight);
		return Math.max(Math.max(0, iconHeight), lineBlockHeight) + 6;
	}

	static int centeredLinesTop(
		int rowY,
		int rowHeight,
		int lineCount,
		int lineHeight,
		int fontHeight)
	{
		int count = Math.max(0, lineCount);
		int visualHeight = count == 0
			? 0
			: Math.max(0, fontHeight) + (count - 1) * Math.max(0, lineHeight);
		return rowY + Math.max(0, (Math.max(0, rowHeight) - visualHeight) / 2);
	}

	static Font sectionHeadingFont()
	{
		return JOURNAL_SMALL_FONT;
	}

	static Font overviewQuestTitleFont()
	{
		return JOURNAL_OVERVIEW_TITLE_FONT;
	}

	static Font questListFont()
	{
		return JOURNAL_QUEST_LIST_FONT;
	}

	static Font overviewMetadataFont()
	{
		return JOURNAL_SMALL_FONT;
	}

	static Font progressFont()
	{
		return JOURNAL_SMALL_FONT;
	}

	static Font compactSmallFont()
	{
		return JOURNAL_COMPACT_SMALL_FONT;
	}

	static Font setActiveQuestFont()
	{
		return SET_ACTIVE_QUEST_FONT;
	}

	void drawEnemyIcon(Graphics2D graphics, Rectangle bounds)
	{
		JournalSnapshot.IconIdentity identity = enemyIconIdentity();
		BufferedImage slayerIcon = identityImage(identity);
		if (slayerIcon != null && hasVisiblePixels(identity, slayerIcon))
		{
			drawPixelArtCenteredImage(graphics, slayerIcon, bounds);
			return;
		}

		drawEnemyIconFallback(graphics, bounds);
	}

	static JournalSnapshot.IconIdentity enemyIconIdentity()
	{
		return ENEMY_ICON_IDENTITY;
	}

	static int activeQuestIconSpriteId()
	{
		return SpriteID.AccountIcons._0;
	}

	private void drawEnemyIconFallback(Graphics2D graphics, Rectangle bounds)
	{
		Stroke oldStroke = graphics.getStroke();
		Object oldAntialiasing = graphics.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		graphics.setStroke(new BasicStroke(2f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
		graphics.setColor(MUTED);
		int inset = Math.max(3, bounds.width / 5);
		int bladeStartX = bounds.x + inset;
		int bladeStartY = bounds.y + inset;
		int bladeEndX = bounds.x + bounds.width - inset - 2;
		int bladeEndY = bounds.y + bounds.height - inset - 2;
		graphics.drawLine(
			bladeStartX,
			bladeStartY,
			bladeEndX,
			bladeEndY);
		graphics.drawLine(bladeStartX, bladeStartY, bladeStartX + 4, bladeStartY);
		graphics.drawLine(bladeStartX, bladeStartY, bladeStartX, bladeStartY + 4);
		graphics.drawLine(
			bladeEndX - 3,
			bladeEndY + 1,
			bladeEndX + 1,
			bladeEndY - 3);
		graphics.drawLine(bladeEndX + 1, bladeEndY + 1, bladeEndX + 3, bladeEndY + 3);
		graphics.setStroke(oldStroke);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntialiasing);
	}

	void drawIdentityIcon(
		Graphics2D graphics,
		JournalSnapshot.IconIdentity identity,
		Rectangle bounds)
	{
		if (!hasSemanticIcon(identity))
		{
			return;
		}
		BufferedImage image = identityImage(identity);
		if (image != null && hasVisiblePixels(identity, image))
		{
			if (identity.getType() == JournalSnapshot.IconType.SKILL)
			{
				drawPixelArtCenteredImage(graphics, image, bounds);
			}
			else
			{
				drawOutlinedPixelArtCenteredImage(graphics, image, bounds);
			}
		}
	}

	void drawOutlinedPixelArtCenteredImage(
		Graphics2D graphics,
		BufferedImage image,
		Rectangle bounds)
	{
		drawPixelArtCenteredImage(graphics, blackOutlinedIcon(image), bounds);
	}

	BufferedImage blackOutlinedIcon(BufferedImage image)
	{
		return blackOutlinedIconCache.computeIfAbsent(
			image,
			JournalOverlay::createBlackOutlinedIcon);
	}

	static BufferedImage createBlackOutlinedIcon(BufferedImage image)
	{
		BufferedImage padded = new BufferedImage(
			Math.max(1, image.getWidth() + 2),
			Math.max(1, image.getHeight() + 2),
			BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = padded.createGraphics();
		try
		{
			graphics.drawImage(image, 1, 1, null);
		}
		finally
		{
			graphics.dispose();
		}
		return LauncherOverlay.createSolidOutlinedIcon(
			padded,
			Color.BLACK);
	}

	void drawPixelArtCenteredImage(
		Graphics2D graphics,
		BufferedImage image,
		Rectangle bounds)
	{
		Rectangle visible = visibleImageBounds(image);
		Rectangle destination = pixelArtDestinationBounds(visible, bounds);
		drawPixelArtImage(graphics, image, visible, destination);
	}

	void drawPixelArtRightAlignedImage(
		Graphics2D graphics,
		BufferedImage image,
		Rectangle bounds)
	{
		Rectangle visible = visibleImageBounds(image);
		Rectangle destination = pixelArtRightAlignedDestinationBounds(visible, bounds);
		drawPixelArtImage(graphics, image, visible, destination);
	}

	private void drawPixelArtImage(
		Graphics2D graphics,
		BufferedImage image,
		Rectangle visible,
		Rectangle destination)
	{
		if (destination.isEmpty())
		{
			return;
		}

		Graphics2D pixelGraphics = (Graphics2D) graphics.create();
		try
		{
			pixelGraphics.setRenderingHint(
				RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			pixelGraphics.drawImage(
				image,
				destination.x,
				destination.y,
				destination.x + destination.width,
				destination.y + destination.height,
				visible.x,
				visible.y,
				visible.x + visible.width,
				visible.y + visible.height,
				null);
		}
		finally
		{
			pixelGraphics.dispose();
		}
	}

	static Rectangle pixelArtRightAlignedDestinationBounds(Rectangle visible, Rectangle bounds)
	{
		Rectangle destination = pixelArtDestinationBounds(visible, bounds);
		if (!destination.isEmpty())
		{
			destination.x = bounds.x + bounds.width - destination.width;
		}
		return destination;
	}

	static Rectangle pixelArtDestinationBounds(Rectangle visible, Rectangle bounds)
	{
		Objects.requireNonNull(visible, "visible");
		Objects.requireNonNull(bounds, "bounds");
		if (visible.isEmpty() || bounds.isEmpty())
		{
			return new Rectangle();
		}
		double scale = Math.min(
			1.0,
			Math.min(
				bounds.width / (double) Math.max(1, visible.width),
				bounds.height / (double) Math.max(1, visible.height)));
		int width = Math.max(1, (int) Math.round(visible.width * scale));
		int height = Math.max(1, (int) Math.round(visible.height * scale));
		int x = bounds.x + (bounds.width - width) / 2;
		int y = bounds.y + (bounds.height - height) / 2;
		return new Rectangle(x, y, width, height);
	}

	static boolean hasSemanticIcon(JournalSnapshot.IconIdentity identity)
	{
		return identity != null && identity.getType() != JournalSnapshot.IconType.NONE;
	}

	static Rectangle detailIconBounds(int x, int rowY, int rowHeight)
	{
		return new Rectangle(
			x,
			rowY + (Math.max(0, rowHeight) - DETAIL_ICON_SIZE) / 2,
			DETAIL_ICON_SIZE,
			DETAIL_ICON_SIZE);
	}

	private BufferedImage identityImage(JournalSnapshot.IconIdentity identity)
	{
		switch (identity.getType())
		{
			case QUEST:
			case QUEST_POINTS:
				JournalPanelAssets assets = chromeRenderer.panelAssets();
				return assets == null ? null : assets.questIcon;
			case ITEM:
				if (identity.getItemId() == null)
				{
					return null;
				}
				return itemImage(identity.getItemId(), identity.getQuantity());
			case SKILL:
				Skill skill = semanticSkill(identity);
				if (skillIconManager == null || skill == null)
				{
					return null;
				}
				return skillIconManager.getSkillImage(skill, usesSmallSkillIcon(skill));
			default:
				return null;
		}
	}

	static boolean usesSmallSkillIcon(Skill skill)
	{
		return skill != Skill.CONSTRUCTION;
	}

	private BufferedImage itemImage(int itemId, int quantity)
	{
		if (itemManager == null)
		{
			return null;
		}
		AsyncBufferedImage image = itemManager.getImage(itemId, quantity, false);
		watchItemIcon(itemId, quantity, image);
		return image;
	}

	static Skill semanticSkill(JournalSnapshot.IconIdentity identity)
	{
		if (identity == null || identity.getType() != JournalSnapshot.IconType.SKILL)
		{
			return null;
		}
		try
		{
			return Skill.valueOf(
				identity.getSkill().trim().toUpperCase(Locale.ROOT).replace(' ', '_'));
		}
		catch (IllegalArgumentException ignored)
		{
			return null;
		}
	}

	private void watchItemIcon(int itemId, int quantity, AsyncBufferedImage image)
	{
		long imageKey = ((long) itemId << 32) | (quantity & 0xFFFFFFFFL);
		if (image != null && watchedItemIcons.add(imageKey))
		{
			image.onLoaded(() ->
			{
				selectedRenderer.clearSurfaceCaches();
				clearIconCaches(true);
			});
		}
	}

	private void clearIconCaches(boolean includeStaticImages)
	{
		visibleSemanticIconPixels.clear();
		if (includeStaticImages)
		{
			visibleStaticImagePixels.clear();
		}
		visibleImageBoundsCache.clear();
		blackOutlinedIconCache.clear();
	}

	static Font contentFont()
	{
		return JOURNAL_FONT;
	}

	private static Font reducedFont(Font font)
	{
		return font.deriveFont(Math.max(1f, font.getSize2D() - 1f));
	}

	boolean hasVisiblePixels(BufferedImage image)
	{
		return visibleStaticImagePixels.computeIfAbsent(
			image,
			JournalOverlay::scanVisiblePixels);
	}

	private static boolean scanVisiblePixels(BufferedImage image)
	{
		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				if ((image.getRGB(x, y) >>> 24) != 0)
				{
					return true;
				}
			}
		}
		return false;
	}

	private boolean hasVisiblePixels(
		JournalSnapshot.IconIdentity identity,
		BufferedImage image)
	{
		return visibleSemanticIconPixels.computeIfAbsent(
			identity,
			ignored -> scanVisiblePixels(image));
	}

	void drawLinesAt(
		Graphics2D graphics,
		List<String> lines,
		int x,
		int top,
		int lineHeight,
		Color color)
	{
		FontMetrics metrics = graphics.getFontMetrics();
		graphics.setColor(color);
		for (int index = 0; index < lines.size(); index++)
		{
			drawShadowedString(
				graphics,
				lines.get(index),
				x,
				top + metrics.getAscent() + index * lineHeight);
		}
	}

	void drawQuestTitleLinesAt(
		Graphics2D graphics,
		List<String> lines,
		int x,
		int top,
		int lineHeight,
		Color color)
	{
		FontMetrics metrics = graphics.getFontMetrics();
		for (int index = 0; index < lines.size(); index++)
		{
			drawQuestTitleString(
				graphics,
				lines.get(index),
				x,
				top + metrics.getAscent() + index * lineHeight,
				color);
		}
	}

	static void drawQuestTitleString(
		Graphics2D graphics,
		String text,
		int x,
		int baseline,
		Color color)
	{
		Graphics2D crisp = (Graphics2D) graphics.create();
		try
		{
			crisp.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			crisp.setColor(color);
			drawShadowedString(crisp, text, x, baseline);
		}
		finally
		{
			crisp.dispose();
		}
	}

	private void drawCenteredLinesAt(
		Graphics2D graphics,
		List<String> lines,
		Rectangle bounds,
		int top,
		int lineHeight,
		Color color)
	{
		FontMetrics metrics = graphics.getFontMetrics();
		graphics.setColor(color);
		for (int index = 0; index < lines.size(); index++)
		{
			String line = lines.get(index);
			int x = bounds.x + (bounds.width - metrics.stringWidth(line)) / 2;
			drawShadowedString(
				graphics,
				line,
				x,
				top + metrics.getAscent() + index * lineHeight);
		}
	}

	void drawCenteredEmptyMessage(
		Graphics2D graphics,
		Rectangle bounds,
		String message)
	{
		graphics.setFont(JOURNAL_FONT);
		drawCenteredLinesAt(
			graphics,
			Collections.singletonList(message),
			bounds,
			bounds.y + Math.max(8, (bounds.height - 20) / 2),
			20,
			TEXT);
	}

	void drawObjectiveGlyph(
		Graphics2D graphics,
		Rectangle bounds,
		JournalSnapshot.Objective objective,
		boolean questComplete)
	{
		ObjectiveMarker marker = objectiveMarker(objective, questComplete);
		Color color = marker == ObjectiveMarker.COMPLETE
			? OBJECTIVE_COMPLETE_COLOR
			: marker == ObjectiveMarker.CURRENT
				? ACTIVE_COLOR
				: marker == ObjectiveMarker.FADED
					? FADED_COLOR
					: LOCKED_COLOR;
		Stroke oldStroke = graphics.getStroke();
		graphics.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		graphics.setColor(color);
		if (marker == ObjectiveMarker.COMPLETE)
		{
			graphics.drawLine(bounds.x + 2, bounds.y + 7, bounds.x + 5, bounds.y + 10);
			graphics.drawLine(bounds.x + 5, bounds.y + 10, bounds.x + 12, bounds.y + 3);
		}
		else if (marker == ObjectiveMarker.CURRENT)
		{
			graphics.fillPolygon(
				new int[]{bounds.x + 2, bounds.x + 12, bounds.x + 2},
				new int[]{bounds.y + 2, bounds.y + 7, bounds.y + 12},
				3);
		}
		else if (marker == ObjectiveMarker.FADED)
		{
			graphics.fillRect(bounds.x + 5, bounds.y + 5, 4, 4);
		}
		else
		{
			Rectangle dot = pendingObjectiveDotBounds(bounds);
			graphics.fillOval(dot.x, dot.y, dot.width, dot.height);
		}
		graphics.setStroke(oldStroke);
	}

	static Rectangle pendingObjectiveDotBounds(Rectangle bounds)
	{
		Objects.requireNonNull(bounds, "bounds");
		int size = Math.min(4, Math.max(0, Math.min(bounds.width, bounds.height)));
		return new Rectangle(
			bounds.x + (bounds.width - size) / 2,
			bounds.y + (bounds.height - size) / 2,
			size,
			size);
	}

	static Rectangle objectiveGlyphBounds(
		int iconX,
		int rowY,
		int rowHeight,
		int firstLineTop,
		int lineHeight,
		boolean complete)
	{
		return new Rectangle(
			iconX,
			complete
				? rowY + Math.max(0, (Math.max(0, rowHeight) - 14) / 2)
				: firstLineTop + Math.max(0, (Math.max(0, lineHeight) - 14) / 2),
			14,
			14);
	}

	static ObjectiveMarker objectiveMarker(
		JournalSnapshot.Objective objective,
		boolean questComplete)
	{
		if (questComplete || objective.getState() == JournalSnapshot.ObjectiveState.COMPLETE)
		{
			return ObjectiveMarker.COMPLETE;
		}
		if (objective.isCurrent())
		{
			return ObjectiveMarker.CURRENT;
		}
		return objective.getState() == JournalSnapshot.ObjectiveState.FADED
			? ObjectiveMarker.FADED
			: ObjectiveMarker.NEUTRAL;
	}

	static Color objectiveTextColor(
		JournalSnapshot.Objective objective,
		boolean questComplete)
	{
		if (questComplete || objective.getState() == JournalSnapshot.ObjectiveState.COMPLETE)
		{
			return OBJECTIVE_COMPLETE_COLOR;
		}
		if (objective.isCurrent())
		{
			return JOURNAL_ORANGE;
		}
		switch (objective.getState())
		{
			case COMPLETE:
				return OBJECTIVE_COMPLETE_COLOR;
			case FADED:
				return FADED_COLOR;
			case LOCKED:
				return MUTED;
			default:
				return TEXT;
		}
	}

	Graphics2D clippedGraphics(Graphics2D graphics, Rectangle clip)
	{
		Graphics2D copy = (Graphics2D) graphics.create();
		copy.clip(clip);
		return copy;
	}

	List<String> wrapText(FontMetrics metrics, String text, int maximumWidth)
	{
		if (maximumWidth <= 0)
		{
			return Collections.emptyList();
		}

		List<String> lines = new ArrayList<>();
		String[] paragraphs = text.split("\\r?\\n", -1);
		for (String paragraph : paragraphs)
		{
			String trimmed = paragraph.trim();
			if (trimmed.isEmpty())
			{
				lines.add("");
				continue;
			}

			String current = "";
			for (String word : trimmed.split("\\s+"))
			{
				String candidate = current.isEmpty() ? word : current + " " + word;
				if (metrics.stringWidth(candidate) <= maximumWidth)
				{
					current = candidate;
					continue;
				}
				if (!current.isEmpty())
				{
					lines.add(current);
					current = "";
				}
				while (metrics.stringWidth(word) > maximumWidth && !word.isEmpty())
				{
					int split = fittingPrefix(metrics, word, maximumWidth);
					lines.add(word.substring(0, split));
					word = word.substring(split);
				}
				current = word;
			}
			if (!current.isEmpty())
			{
				lines.add(current);
			}
		}
		return lines;
	}

	private int fittingPrefix(FontMetrics metrics, String text, int maximumWidth)
	{
		for (int end = Math.min(text.length(), Math.max(1, maximumWidth)); end > 1; end--)
		{
			if (metrics.stringWidth(text.substring(0, end)) <= maximumWidth)
			{
				return end;
			}
		}
		return 1;
	}

	String fitText(FontMetrics metrics, String text, int maximumWidth)
	{
		if (maximumWidth <= 0)
		{
			return "";
		}
		if (metrics.stringWidth(text) <= maximumWidth)
		{
			return text;
		}
		String ellipsis = "...";
		for (int end = text.length(); end > 0; end--)
		{
			String candidate = text.substring(0, end) + ellipsis;
			if (metrics.stringWidth(candidate) <= maximumWidth)
			{
				return candidate;
			}
		}
		return "";
	}

	static void drawShadowedString(Graphics2D graphics, String text, int x, int baseline)
	{
		Color textColor = graphics.getColor();
		graphics.setColor(TEXT_SHADOW);
		graphics.drawString(text, x + 1, baseline + 1);
		graphics.setColor(textColor);
		graphics.drawString(text, x, baseline);
	}

	int centeredTextBaseline(FontMetrics metrics, Rectangle bounds)
	{
		return (int) Math.round(
			bounds.getCenterY() + (metrics.getAscent() - metrics.getDescent()) / 2.0);
	}

	static Color questStateColor(JournalSnapshot.QuestState state)
	{
		switch (state)
		{
			case COMPLETE:
				return QUEST_COMPLETE_COLOR;
			case IN_PROGRESS:
				return QUEST_IN_PROGRESS_COLOR;
			default:
				return QUEST_NOT_STARTED_COLOR;
		}
	}

	static Color requirementColor(JournalSnapshot.Requirement requirement)
	{
		Color color;
		switch (requirement.getState())
		{
			case BANKED:
			case GROUP_BANKED:
			case PARTIAL:
			case BOOSTABLE:
				color = TEXT;
				break;
			default:
				color = configuredRequirementColor(
					requirement,
					requirement.getState() == JournalSnapshot.RequirementState.MET
						? REQUIREMENT_MET_COLOR
						: requirement.getState() == JournalSnapshot.RequirementState.UNMET
							? UNMET_COLOR
							: LOCKED_COLOR);
				break;
		}
		return Color.GRAY.equals(color)
			? REQUIREMENT_GRAY
			: color;
	}

	static List<String> requirementTooltipBlocks(
		JournalSnapshot.Requirement requirement,
		boolean wikiLink)
	{
		String help = TooltipRenderer.tooltipLineBreaks(
			requirement.getHelpText());
		String location = requirement.getLocationHint() == null
			? ""
			: requirement.getLocationHint().trim();
		boolean helpIncludesContainerLocation = !help.isEmpty()
			&& (requirement.getState() == JournalSnapshot.RequirementState.BANKED
				|| requirement.getState() == JournalSnapshot.RequirementState.GROUP_BANKED);
		String primary = help;
		if (!location.isEmpty() && !helpIncludesContainerLocation)
		{
			primary = primary.isEmpty() ? location : primary + "<br>" + location;
		}
		String action = "";
		if (requirement.hasLinkedQuest())
		{
			String title = requirement.getLinkedQuestTitle() == null
				? ""
				: requirement.getLinkedQuestTitle().trim();
			action = title.isEmpty()
				? "Click to open the required quest."
				: "Click to open " + title + ".";
		}
		else if (wikiLink)
		{
			action = "<col=ffffff>Click to open Wiki</col>";
		}
		if (requirement.hasLinkedQuest())
		{
			String combined = primary.isEmpty() ? action : primary + "<br>" + action;
			return combined.isEmpty()
				? Collections.emptyList()
				: Collections.singletonList(combined);
		}
		List<String> blocks = new ArrayList<>(2);
		if (!primary.isEmpty())
		{
			blocks.add(primary);
		}
		if (!action.isEmpty())
		{
			blocks.add(action);
		}
		return Collections.unmodifiableList(blocks);
	}

	static boolean isMissingItemWikiLink(
		JournalSnapshot.Requirement requirement,
		boolean enabled)
	{
		return enabled
			&& requirement != null
			&& requirement.getState() == JournalSnapshot.RequirementState.UNMET
			&& requirement.hasWikiUrl();
	}

	static Color requirementUnderlineColor(JournalSnapshot.Requirement requirement)
	{
		return requirement == null ? TEXT : requirementColor(requirement);
	}

	private static Color configuredRequirementColor(JournalSnapshot.Requirement requirement, Color fallback)
	{
		Integer configured = requirement.getDisplayColor();
		return configured == null ? fallback : new Color(configured, true);
	}

	static String prettyEnumName(Enum<?> value)
	{
		return Text.titleCase(value);
	}

	private Rectangle currentOverlayBounds()
	{
		return new Rectangle(getBounds());
	}

	private void publishBounds(
		JournalGeometry geometry,
		Rectangle viewport,
		List<QuestListRenderer.RowHit> rows,
		List<SemanticHit> semanticHits,
		Rectangle setActiveQuestBounds,
		Rectangle clearActiveQuestBounds,
		Rectangle returnToActiveQuestBounds,
		Rectangle titleIconBounds,
		Rectangle questListScrollBounds,
		Rectangle mainScrollBounds,
		String viewedQuestId,
		String activeQuestId,
		boolean maximized)
	{
		hitState = new HitState(
			maximized,
			JournalGeometry.clip(geometry.panelBounds(), viewport),
			viewport,
			JournalGeometry.clip(
				geometry.draggableHeaderBounds(),
				viewport),
			JournalGeometry.clip(titleIconBounds, viewport),
			JournalGeometry.clip(geometry.closeButtonBounds(), viewport),
			JournalGeometry.clip(geometry.maximizeButtonBounds(), viewport),
			maximized
				? new Rectangle()
				: JournalGeometry.clip(geometry.resizeHandleHitBounds(), viewport),
			JournalGeometry.clip(
				controlsRenderer.searchControlBounds(geometry),
				viewport),
			JournalGeometry.clip(
				controlsRenderer.filterVisibilityControlBounds(geometry),
				viewport),
			JournalGeometry.clip(
				controlsRenderer.starQuestControlBounds(geometry),
				viewport),
			JournalGeometry.clip(questListScrollBounds, viewport),
			JournalGeometry.clip(mainScrollBounds, viewport),
			JournalGeometry.clip(geometry.detailContentBounds(), viewport),
			JournalGeometry.clip(setActiveQuestBounds, viewport),
			JournalGeometry.clip(clearActiveQuestBounds, viewport),
			JournalGeometry.clip(returnToActiveQuestBounds, viewport),
			viewedQuestId,
			activeQuestId,
			geometry.hasDetailPane(),
			rows,
			semanticHits,
			listMaximumScroll,
			mainMaximumScroll,
			detailMaximumScroll);
	}

	private void clearBounds()
	{
		hitState = HitState.empty();
		filterRenderer.clear();
		listMaximumScroll = 0;
		mainMaximumScroll = 0;
		detailMaximumScroll = 0;
	}

	boolean isRenderedForMaximizedState(boolean maximized)
	{
		HitState state = hitState;
		return state.rendered && state.maximized == maximized;
	}

	void beginPanelStateTransition(Rectangle targetBounds)
	{
		if (targetBounds != null && !targetBounds.isEmpty())
		{
			getBounds().setBounds(targetBounds);
		}
		hitState = HitState.empty();
	}

	boolean scrollAt(Point point, int wheelRotation)
	{
		HitState state = hitState;
		if (!state.rendered || point == null)
		{
			return false;
		}
		if (filterRenderer.popupContains(point))
		{
			return false;
		}

		if (state.questListScrollBounds.contains(point))
		{
			synchronized (scrollStateLock)
			{
				int next = scrolledOffset(
					listScrollOffset,
					state.listMaximumScroll,
					wheelRotation);
				if (next == listScrollOffset)
				{
					return false;
				}
				listScrollOffset = next;
				scrollStateRevision++;
				return true;
			}
		}
		if (state.detailPaneVisible && state.detailContentBounds.contains(point))
		{
			synchronized (scrollStateLock)
			{
				int next = scrolledOffset(
					detailScrollOffset,
					state.detailMaximumScroll,
					wheelRotation);
				if (next == detailScrollOffset)
				{
					return false;
				}
				detailScrollOffset = next;
				scrollStateRevision++;
				return true;
			}
		}
		if (state.mainContentBounds.contains(point))
		{
			synchronized (scrollStateLock)
			{
				int next = scrolledOffset(
					mainScrollOffset,
					state.mainMaximumScroll,
					wheelRotation);
				if (next == mainScrollOffset)
				{
					return false;
				}
				mainScrollOffset = next;
				scrollStateRevision++;
				return true;
			}
		}
		return false;
	}

	ScrollbarInteraction scrollbarInteractionAt(Point point)
	{
		HitState state = hitState;
		if (!state.rendered || point == null)
		{
			return null;
		}
		ScrollRenderState scrollState = captureScrollRenderState();

		ScrollbarInteraction list = scrollbarInteraction(
			ScrollRegion.QUEST_LIST,
			state.questListScrollBounds,
			scrollState.listScrollOffset,
			state.listMaximumScroll,
			point);
		if (list != null)
		{
			return list;
		}

		if (state.detailPaneVisible)
		{
			ScrollbarInteraction detail = scrollbarInteraction(
				ScrollRegion.DETAIL,
				state.detailContentBounds,
				scrollState.detailScrollOffset,
				state.detailMaximumScroll,
				point);
			if (detail != null)
			{
				return detail;
			}
		}

		return scrollbarInteraction(
			ScrollRegion.MAIN,
			state.mainContentBounds,
			scrollState.mainScrollOffset,
			state.mainMaximumScroll,
			point);
	}

	private ScrollbarInteraction scrollbarInteraction(
		ScrollRegion region,
		Rectangle contentBounds,
		int offset,
		int maximum,
		Point point)
	{
		ScrollbarGeometry scrollbar = scrollbarGeometry(contentBounds, offset, maximum);
		return scrollbar.contains(point)
			? new ScrollbarInteraction(region, scrollbar)
			: null;
	}

	void dragScrollbar(ScrollRegion region, int pointerY, int pointerOffset)
	{
		HitState state = hitState;
		if (!state.rendered || region == null)
		{
			return;
		}

		synchronized (scrollStateLock)
		{
			int next;
			switch (region)
			{
				case QUEST_LIST:
					next = scrollbarGeometry(
						state.questListScrollBounds,
						listScrollOffset,
						state.listMaximumScroll).offsetForPointer(pointerY, pointerOffset);
					if (next != listScrollOffset)
					{
						listScrollOffset = next;
						scrollStateRevision++;
					}
					break;
				case DETAIL:
					if (state.detailPaneVisible)
					{
						next = scrollbarGeometry(
							state.detailContentBounds,
							detailScrollOffset,
							state.detailMaximumScroll).offsetForPointer(pointerY, pointerOffset);
						if (next != detailScrollOffset)
						{
							detailScrollOffset = next;
							scrollStateRevision++;
						}
					}
					break;
				case MAIN:
					next = scrollbarGeometry(
						state.mainContentBounds,
						mainScrollOffset,
						state.mainMaximumScroll).offsetForPointer(pointerY, pointerOffset);
					if (next != mainScrollOffset)
					{
						mainScrollOffset = next;
						scrollStateRevision++;
					}
					break;
				default:
					break;
			}
		}
	}

	private ScrollRenderState captureScrollRenderState()
	{
		synchronized (scrollStateLock)
		{
			return new ScrollRenderState(
				scrollStateRevision,
				listScrollOffset,
				mainScrollOffset,
				detailScrollOffset);
		}
	}

	private boolean commitScrollOffsets(
		long revision,
		int listOffset,
		int mainOffset,
		int detailOffset)
	{
		synchronized (scrollStateLock)
		{
			if (scrollStateRevision != revision)
			{
				return false;
			}
			listScrollOffset = listOffset;
			mainScrollOffset = mainOffset;
			detailScrollOffset = detailOffset;
			scrollStateRevision++;
			return true;
		}
	}

	private void replaceScrollOffsets(int listOffset, int mainOffset, int detailOffset)
	{
		synchronized (scrollStateLock)
		{
			listScrollOffset = listOffset;
			mainScrollOffset = mainOffset;
			detailScrollOffset = detailOffset;
			scrollStateRevision++;
		}
	}

	private ScrollbarGeometry scrollbarGeometry(Rectangle contentBounds, int offset, int maximum)
	{
		int totalHeight = (int) Math.min(
			Integer.MAX_VALUE,
			Math.max(0L, (long) contentBounds.height) + Math.max(0L, (long) maximum));
		return ScrollbarGeometry.create(contentBounds, totalHeight, offset, maximum);
	}

	void resetSelectedQuestScroll()
	{
		synchronized (scrollStateLock)
		{
			mainScrollOffset = 0;
			detailScrollOffset = 0;
			scrollStateRevision++;
		}
	}

	ViewState captureViewState(
		String selectedQuestId,
		JournalSnapshot.QuestFilter filter)
	{
		Objects.requireNonNull(filter, "filter");
		ScrollRenderState scrollState = captureScrollRenderState();
		int persistedListScroll = filter.getSearchText().isEmpty()
			? scrollState.listScrollOffset
			: 0;
		return ViewState.fromFilter(
			selectedQuestId,
			persistedListScroll,
			scrollState.mainScrollOffset,
			scrollState.detailScrollOffset,
			filter,
			filtersVisible,
			selectedRenderer.expandedChecklistIds());
	}

	synchronized void restoreViewState(ViewState state)
	{
		if (state == null || !state.isPresent())
		{
			return;
		}
		replaceScrollOffsets(
			state.getListScrollOffset(),
			state.getOverviewScrollOffset(),
			state.getDetailsScrollOffset());
		renderedQuestId = state.getSelectedQuestId();
		filtersVisible = state.isFiltersVisible();
		filterRenderer.close();
		selectedRenderer.restoreExpandedChecklistIds(
			state.getExpandedChecklistIds());
	}

	synchronized void clearPersistentViewState()
	{
		resetViewState();
		filtersVisible = false;
	}

	void release()
	{
		resetViewState();
		chromeRenderer.release();
		filtersVisible = false;
		watchedItemIcons.clear();
		clearIconCaches(true);
		tooltipRenderer.clear();
		questListRenderer.clear();
	}

	private void resetViewState()
	{
		clearBounds();
		titleIconClickCount = 0;
		replaceScrollOffsets(0, 0, 0);
		renderedQuestId = null;
		renderedSelectedQuest = null;
		selectedRenderer.resetChecklistExpansion();
		filterRenderer.clear();
	}

	void resetQuestListScroll()
	{
		synchronized (scrollStateLock)
		{
			listScrollOffset = 0;
			scrollStateRevision++;
		}
	}

	private int scrolledOffset(int current, int maximum, int wheelRotation)
	{
		long requested = (long) current + (long) wheelRotation * SCROLL_STEP;
		return (int) Math.max(0, Math.min(maximum, requested));
	}

	boolean isRendered()
	{
		return hitState.rendered;
	}

	boolean contains(Point point)
	{
		HitState state = hitState;
		return state.rendered && point != null && state.panelBounds.contains(point);
	}

	boolean isHeader(Point point)
	{
		HitState state = hitState;
		return state.rendered && point != null && state.headerBounds.contains(point);
	}

	boolean isTitleIcon(Point point)
	{
		HitState state = hitState;
		return state.rendered
			&& point != null
			&& state.titleIconBounds.contains(point);
	}

	boolean isCloseButton(Point point)
	{
		HitState state = hitState;
		return state.rendered && point != null && state.closeButtonBounds.contains(point);
	}

	boolean isMaximizeButton(Point point)
	{
		HitState state = hitState;
		return state.rendered && point != null && state.maximizeButtonBounds.contains(point);
	}

	boolean isSettingsButton(Point point)
	{
		HitState state = hitState;
		return state.rendered
			&& point != null
			&& settingsButtonBounds(state.maximizeButtonBounds).contains(point);
	}

	boolean isResizeHandle(Point point)
	{
		HitState state = hitState;
		return state.rendered && point != null && state.resizeHandleBounds.contains(point);
	}

	boolean isSearchControl(Point point)
	{
		HitState state = hitState;
		return state.rendered && point != null && state.searchControlBounds.contains(point);
	}

	boolean isFilterVisibilityControl(Point point)
	{
		HitState state = hitState;
		return state.rendered
			&& point != null
			&& state.filterVisibilityControlBounds.contains(point);
	}

	String starControlQuestIdAt(Point point)
	{
		HitState state = hitState;
		return state.rendered
			&& point != null
			&& state.starQuestControlBounds.contains(point)
			&& !state.viewedQuestId.isEmpty()
			? state.viewedQuestId
			: null;
	}

	boolean areFiltersVisible()
	{
		return filtersVisible;
	}

	void toggleFilterVisibility()
	{
		filtersVisible = !filtersVisible;
		if (!filtersVisible)
		{
			filterRenderer.close();
		}
	}

	FilterControl filterControlAt(Point point)
	{
		return filterRenderer.controlAt(point);
	}

	FilterSelection filterSelectionAt(Point point)
	{
		return filterRenderer.selectionAt(point);
	}

	boolean isFilterPopupSurface(Point point)
	{
		return filterRenderer.popupContains(point);
	}

	void toggleFilterDropdown(FilterControl control)
	{
		filterRenderer.toggle(control);
	}

	void closeFilterDropdown()
	{
		filterRenderer.close();
	}

	boolean isFilterDropdownOpen()
	{
		return filterRenderer.isOpen();
	}

	String questIdAt(Point point)
	{
		HitState state = hitState;
		if (!state.rendered || point == null)
		{
			return null;
		}
		if (filterRenderer.popupContains(point))
		{
			return null;
		}
		for (QuestListRenderer.RowHit row : state.questRowHits)
		{
			if (row.contains(point))
			{
				return row.questId;
			}
		}
		return null;
	}

	String setActiveQuestIdAt(Point point)
	{
		if (!controlsRenderer.isManualActiveQuestSelection())
		{
			return null;
		}
		HitState state = hitState;
		return state.rendered
			&& point != null
			&& state.setActiveQuestBounds.contains(point)
			? state.viewedQuestId
			: null;
	}

	String clearActiveQuestIdAt(Point point)
	{
		if (!controlsRenderer.isManualActiveQuestSelection())
		{
			return null;
		}
		HitState state = hitState;
		return state.rendered
			&& point != null
			&& state.clearActiveQuestBounds.contains(point)
			? state.activeQuestId
			: null;
	}

	String returnToActiveQuestIdAt(Point point)
	{
		if (!controlsRenderer.isManualActiveQuestSelection())
		{
			return null;
		}
		HitState state = hitState;
		return state.rendered
			&& point != null
			&& state.returnToActiveQuestBounds.contains(point)
			? state.activeQuestId
			: null;
	}

	String linkedQuestIdAt(Point point)
	{
		SemanticHit hit = semanticHitAt(point, hitState.semanticHits);
		return hit == null || hit.linkedQuestId.isEmpty() ? null : hit.linkedQuestId;
	}

	String checklistToggleIdAt(Point point)
	{
		SemanticHit hit = semanticHitAt(point, hitState.semanticHits);
		return hit == null || hit.checklistId.isEmpty()
			? null
			: hit.checklistId;
	}

	synchronized boolean toggleChecklist(String checklistId)
	{
		return selectedRenderer.toggleChecklist(checklistId);
	}

	String missingItemWikiUrlAt(Point point)
	{
		SemanticHit hit = semanticHitAt(point, hitState.semanticHits);
		return hit == null || hit.wikiUrl.isEmpty() ? null : hit.wikiUrl;
	}

	private SemanticHit semanticHitAt(Point point, List<SemanticHit> semanticHits)
	{
		return TooltipRenderer.semanticHitAt(point, semanticHits);
	}

	Rectangle getPanelBounds()
	{
		return hitState.panelBounds();
	}

	Rectangle getViewportBounds()
	{
		return hitState.viewportBounds();
	}

	void clearManagedPlacement()
	{
		setPreferredLocation(null);
		setPreferredSize(null);
	}

	static int clamp(int value, int minimum, int maximum)
	{
		return Math.max(minimum, Math.min(maximum, value));
	}

	enum FilterControl
	{
		TYPE,
		DIFFICULTY,
		MEMBERSHIP,
		ORDER,
		STATUS;

		boolean isChecklist()
		{
			return this == TYPE
				|| this == DIFFICULTY
				|| this == MEMBERSHIP
				|| this == STATUS;
		}

		boolean isSingleSelect()
		{
			return !isChecklist();
		}
	}

	enum TypeFilterOption
	{
		STARRED
	}

	enum FilterSelectionAction
	{
		VALUE,
		SELECT_ALL,
		SELECT_NONE
	}

	@EqualsAndHashCode(of = {"control", "action", "value"})
	static final class FilterSelection
	{
		private final FilterControl control;
		private final FilterSelectionAction action;
		private final Object value;
		private final String label;

		FilterSelection(FilterControl control, Object value, String label)
		{
			this(
				control,
				FilterSelectionAction.VALUE,
				Objects.requireNonNull(value, "value"),
				label);
		}

		private FilterSelection(
			FilterControl control,
			FilterSelectionAction action,
			Object value,
			String label)
		{
			this.control = Objects.requireNonNull(control, "control");
			this.action = Objects.requireNonNull(action, "action");
			this.value = value;
			this.label = Objects.requireNonNull(label, "label");
			if (action != FilterSelectionAction.VALUE && !control.isChecklist())
			{
				throw new IllegalArgumentException("Bulk actions require a checklist filter");
			}
			if ((action == FilterSelectionAction.VALUE) != (value != null))
			{
				throw new IllegalArgumentException("Only value selections can contain a value");
			}
		}

		static FilterSelection selectAll(FilterControl control)
		{
			return new FilterSelection(
				control,
				FilterSelectionAction.SELECT_ALL,
				null,
				"All");
		}

		static FilterSelection selectNone(FilterControl control)
		{
			return new FilterSelection(
				control,
				FilterSelectionAction.SELECT_NONE,
				null,
				"None");
		}

		FilterControl control()
		{
			return control;
		}

		FilterSelectionAction action()
		{
			return action;
		}

		Object value()
		{
			return value;
		}

		String label()
		{
			return label;
		}

	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	private static final class ScrollRenderState
	{
		private final long revision;
		private final int listScrollOffset;
		private final int mainScrollOffset;
		private final int detailScrollOffset;
	}

	enum ScrollRegion
	{
		QUEST_LIST,
		MAIN,
		DETAIL
	}

	enum ObjectiveMarker
	{
		COMPLETE,
		CURRENT,
		NEUTRAL,
		FADED
	}

	enum HeaderControlIcon
	{
		CLOSE,
		MAXIMIZE,
		RESTORE,
		SETTINGS
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	static final class ScrollbarInteraction
	{
		private final ScrollRegion region;
		private final ScrollbarGeometry geometry;

		ScrollRegion region()
		{
			return region;
		}

		int pointerOffset(Point point)
		{
			return geometry.pointerOffset(point);
		}
	}

	private static final class HitState
	{
		private static final HitState EMPTY = new HitState(
			false,
			false,
			new Rectangle(),
			new Rectangle(),
			new Rectangle(),
			new Rectangle(),
			new Rectangle(),
			new Rectangle(),
			new Rectangle(),
			new Rectangle(),
			new Rectangle(),
			new Rectangle(),
			new Rectangle(),
			new Rectangle(),
			new Rectangle(),
			new Rectangle(),
			new Rectangle(),
			new Rectangle(),
			null,
			null,
			false,
			Collections.emptyList(),
			Collections.emptyList(),
			0,
			0,
			0);

		private final boolean rendered;
		private final boolean maximized;
		private final Rectangle panelBounds;
		private final Rectangle viewportBounds;
		private final Rectangle headerBounds;
		private final Rectangle titleIconBounds;
		private final Rectangle closeButtonBounds;
		private final Rectangle maximizeButtonBounds;
		private final Rectangle resizeHandleBounds;
		private final Rectangle searchControlBounds;
		private final Rectangle filterVisibilityControlBounds;
	private final Rectangle starQuestControlBounds;
		private final Rectangle questListScrollBounds;
		private final Rectangle mainContentBounds;
		private final Rectangle detailContentBounds;
		private final Rectangle setActiveQuestBounds;
		private final Rectangle clearActiveQuestBounds;
		private final Rectangle returnToActiveQuestBounds;
		private final String viewedQuestId;
		private final String activeQuestId;
		private final boolean detailPaneVisible;
		private final List<QuestListRenderer.RowHit> questRowHits;
		private final List<SemanticHit> semanticHits;
		private final int listMaximumScroll;
		private final int mainMaximumScroll;
		private final int detailMaximumScroll;

		private HitState(
			boolean maximized,
			Rectangle panelBounds,
			Rectangle viewportBounds,
			Rectangle headerBounds,
			Rectangle titleIconBounds,
			Rectangle closeButtonBounds,
			Rectangle maximizeButtonBounds,
			Rectangle resizeHandleBounds,
			Rectangle searchControlBounds,
			Rectangle filterVisibilityControlBounds,
			Rectangle starQuestControlBounds,
			Rectangle questListScrollBounds,
			Rectangle mainContentBounds,
			Rectangle detailContentBounds,
			Rectangle setActiveQuestBounds,
			Rectangle clearActiveQuestBounds,
			Rectangle returnToActiveQuestBounds,
			String viewedQuestId,
			String activeQuestId,
			boolean detailPaneVisible,
			List<QuestListRenderer.RowHit> questRowHits,
			List<SemanticHit> semanticHits,
			int listMaximumScroll,
			int mainMaximumScroll,
			int detailMaximumScroll)
		{
			this(
				true,
				maximized,
				panelBounds,
				viewportBounds,
				headerBounds,
				titleIconBounds,
				closeButtonBounds,
				maximizeButtonBounds,
				resizeHandleBounds,
				searchControlBounds,
				filterVisibilityControlBounds,
				starQuestControlBounds,
				questListScrollBounds,
				mainContentBounds,
				detailContentBounds,
				setActiveQuestBounds,
				clearActiveQuestBounds,
				returnToActiveQuestBounds,
				viewedQuestId,
				activeQuestId,
				detailPaneVisible,
				questRowHits,
				semanticHits,
				listMaximumScroll,
				mainMaximumScroll,
				detailMaximumScroll);
		}

		private HitState(
			boolean rendered,
			boolean maximized,
			Rectangle panelBounds,
			Rectangle viewportBounds,
			Rectangle headerBounds,
			Rectangle titleIconBounds,
			Rectangle closeButtonBounds,
			Rectangle maximizeButtonBounds,
			Rectangle resizeHandleBounds,
			Rectangle searchControlBounds,
			Rectangle filterVisibilityControlBounds,
			Rectangle starQuestControlBounds,
			Rectangle questListScrollBounds,
			Rectangle mainContentBounds,
			Rectangle detailContentBounds,
			Rectangle setActiveQuestBounds,
			Rectangle clearActiveQuestBounds,
			Rectangle returnToActiveQuestBounds,
			String viewedQuestId,
			String activeQuestId,
			boolean detailPaneVisible,
			List<QuestListRenderer.RowHit> questRowHits,
			List<SemanticHit> semanticHits,
			int listMaximumScroll,
			int mainMaximumScroll,
			int detailMaximumScroll)
		{
			this.rendered = rendered;
			this.maximized = maximized;
			this.panelBounds = new Rectangle(panelBounds);
			this.viewportBounds = new Rectangle(viewportBounds);
			this.headerBounds = new Rectangle(headerBounds);
			this.titleIconBounds = new Rectangle(titleIconBounds);
			this.closeButtonBounds = new Rectangle(closeButtonBounds);
			this.maximizeButtonBounds = new Rectangle(maximizeButtonBounds);
			this.resizeHandleBounds = new Rectangle(resizeHandleBounds);
			this.searchControlBounds = new Rectangle(searchControlBounds);
			this.filterVisibilityControlBounds =
				new Rectangle(filterVisibilityControlBounds);
			this.starQuestControlBounds = new Rectangle(starQuestControlBounds);
			this.questListScrollBounds = new Rectangle(questListScrollBounds);
			this.mainContentBounds = new Rectangle(mainContentBounds);
			this.detailContentBounds = new Rectangle(detailContentBounds);
			this.setActiveQuestBounds = new Rectangle(setActiveQuestBounds);
			this.clearActiveQuestBounds = new Rectangle(clearActiveQuestBounds);
			this.returnToActiveQuestBounds =
				new Rectangle(returnToActiveQuestBounds);
			this.viewedQuestId = viewedQuestId == null ? "" : viewedQuestId;
			this.activeQuestId = activeQuestId == null ? "" : activeQuestId;
			this.detailPaneVisible = detailPaneVisible;
		// Hit lists are immutable and may be reused.
			this.questRowHits = Objects.requireNonNull(questRowHits, "questRowHits");
			this.semanticHits = Objects.requireNonNull(semanticHits, "semanticHits");
			this.listMaximumScroll = listMaximumScroll;
			this.mainMaximumScroll = mainMaximumScroll;
			this.detailMaximumScroll = detailMaximumScroll;
		}

		private static HitState empty()
		{
			return EMPTY;
		}

		private Rectangle panelBounds()
		{
			return new Rectangle(panelBounds);
		}

		private Rectangle viewportBounds()
		{
			return new Rectangle(viewportBounds);
		}
	}
}
