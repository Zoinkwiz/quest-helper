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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/** Renders the selected quest and caches its layout and viewport surfaces. */
final class QuestViewRenderer
{
	private final JournalOverlay overlay;
	private final QuestJournalManager manager;
	private final DetailRenderer detailRenderer;
	private final ChromeRenderer chromeRenderer;

	private volatile ChecklistExpansionState checklistExpansionState =
		ChecklistExpansionState.empty();
	private volatile SelectedQuestLayoutCache layoutCache;
	private volatile SelectedQuestLayoutCache secondaryLayoutCache;
	private volatile SelectedQuestSurfaceCache surfaceCache;
	private volatile SelectedQuestSurfaceCache secondarySurfaceCache;

	QuestViewRenderer(
		JournalOverlay overlay,
		QuestJournalManager manager,
		DetailRenderer detailRenderer,
		ChromeRenderer chromeRenderer)
	{
		this.overlay = Objects.requireNonNull(overlay, "overlay");
		this.manager = manager;
		this.detailRenderer = Objects.requireNonNull(
			detailRenderer,
			"detailRenderer");
		this.chromeRenderer = Objects.requireNonNull(
			chromeRenderer,
			"chromeRenderer");
	}

	RenderResult draw(
		Graphics2D graphics,
		JournalGeometry geometry,
		JournalSnapshot.SelectedQuest quest,
		int requestedMainScroll,
		int requestedDetailScroll)
	{
		Rectangle fullMainContent = ControlsRenderer.selectedQuestMainContentBounds(
			geometry.mainContentBounds(),
			manager == null || manager.isManualActiveQuestSelection());
		Rectangle mainContent = new Rectangle(fullMainContent);
		Rectangle detailContent = geometry.detailContentBounds();
		boolean compact = !geometry.hasDetailPane();
		boolean alwaysExpandChecklists = manager != null
			&& manager.shouldAlwaysExpandChecklists();
		ChecklistExpansionState checklistExpansion = checklistExpansionState;
		Rectangle fullMainBody = JournalOverlay.scrollBodyBounds(mainContent, false);
		Rectangle fullDetailBody = JournalOverlay.scrollBodyBounds(detailContent, false);
		SelectedQuestLayoutCache fullWidthLayout = cachedSelectedQuestLayout(
			graphics,
			quest,
			Math.max(0, fullMainBody.width),
			Math.max(0, fullDetailBody.width),
			compact,
			alwaysExpandChecklists,
			checklistExpansion);
		boolean mainScrollbarVisible = fullWidthLayout.mainHeight > mainContent.height;
		boolean detailScrollbarVisible = geometry.hasDetailPane()
			&& fullWidthLayout.detailHeight > detailContent.height;
		Rectangle mainBody = JournalOverlay.scrollBodyBounds(
			mainContent,
			mainScrollbarVisible);
		Rectangle detailBody = JournalOverlay.scrollBodyBounds(
			detailContent,
			detailScrollbarVisible);
		SelectedQuestLayoutCache layout = mainBody.width == fullMainBody.width
			&& detailBody.width == fullDetailBody.width
			? fullWidthLayout
			: cachedSelectedQuestLayout(
				graphics,
				quest,
				Math.max(0, mainBody.width),
				Math.max(0, detailBody.width),
				compact,
				alwaysExpandChecklists,
				checklistExpansion);

		int mainHeight = layout.mainHeight;
		int mainMaximumScroll = Math.max(0, mainHeight - mainContent.height);
		int mainScroll = JournalOverlay.clamp(
			requestedMainScroll,
			0,
			mainMaximumScroll);
		int detailHeight = geometry.hasDetailPane() ? layout.detailHeight : 0;
		int detailMaximumScroll = Math.max(0, detailHeight - detailContent.height);
		int detailScroll = JournalOverlay.clamp(
			requestedDetailScroll,
			0,
			detailMaximumScroll);

		SelectedQuestSurfaceCache surfaces = cachedSelectedQuestSurfaces(
			graphics,
			layout,
			mainBody.height,
			detailBody.height,
			mainScroll,
			detailScroll,
			manager == null || manager.shouldOpenMissingItemWikiLinks());

		Graphics2D main = overlay.clippedGraphics(graphics, mainContent);
		try
		{
			blitViewportSurface(main, mainBody, surfaces.mainSurface);
			chromeRenderer.drawScrollBar(
				main,
				mainContent,
				mainHeight,
				mainScroll,
				mainMaximumScroll);
		}
		finally
		{
			main.dispose();
		}

		if (geometry.hasDetailPane())
		{
			Graphics2D detail = overlay.clippedGraphics(graphics, detailContent);
			try
			{
				blitViewportSurface(detail, detailBody, surfaces.detailSurface);
				chromeRenderer.drawScrollBar(
					detail,
					detailContent,
					detailHeight,
					detailScroll,
					detailMaximumScroll);
			}
			finally
			{
				detail.dispose();
			}
		}
		else
		{
			detailMaximumScroll = 0;
			detailScroll = 0;
		}

		return new RenderResult(
			surfaces.positionedSemanticHits(mainBody, detailBody),
			mainScroll,
			detailScroll,
			mainMaximumScroll,
			detailMaximumScroll);
	}

	private SelectedQuestLayoutCache cachedSelectedQuestLayout(
		Graphics2D graphics,
		JournalSnapshot.SelectedQuest quest,
		int mainWidth,
		int detailWidth,
		boolean compact,
		boolean alwaysExpandChecklists,
		ChecklistExpansionState checklistExpansion)
	{
		FontRenderContext fontRenderContext = graphics.getFontRenderContext();
		SelectedQuestLayoutCache cached = layoutCache;
		if (cached != null && cached.matches(
			quest,
			mainWidth,
			detailWidth,
			compact,
			alwaysExpandChecklists,
			checklistExpansion,
			fontRenderContext))
		{
			return cached;
		}
		SelectedQuestLayoutCache secondary = secondaryLayoutCache;
		if (secondary != null && secondary.matches(
			quest,
			mainWidth,
			detailWidth,
			compact,
			alwaysExpandChecklists,
			checklistExpansion,
			fontRenderContext))
		{
			layoutCache = secondary;
			secondaryLayoutCache = cached;
			return secondary;
		}

		int mainHeight = mainWidth <= 0
			? 0
			: Math.max(
				0,
				layoutMainContent(
					DetailRenderer.LayoutContext.measure(
						graphics,
						new Rectangle(0, 0, mainWidth, 0)),
					quest,
					compact,
					checklistExpansion.expandedChecklistIds,
					alwaysExpandChecklists));
		int detailHeight = compact || detailWidth <= 0
			? 0
			: Math.max(
				0,
				detailRenderer.layoutDetailContent(
					DetailRenderer.LayoutContext.measure(
						graphics,
						new Rectangle(0, 0, detailWidth, 0)),
					quest));
		SelectedQuestLayoutCache replacement = new SelectedQuestLayoutCache(
			quest,
			mainWidth,
			detailWidth,
			compact,
			alwaysExpandChecklists,
			checklistExpansion,
			fontRenderContext,
			mainHeight,
			detailHeight);
		secondaryLayoutCache = cached;
		layoutCache = replacement;
		return replacement;
	}

	private synchronized SelectedQuestSurfaceCache cachedSelectedQuestSurfaces(
		Graphics2D graphics,
		SelectedQuestLayoutCache layout,
		int mainViewportHeight,
		int detailViewportHeight,
		int mainScroll,
		int detailScroll,
		boolean wikiLinksEnabled)
	{
		SelectedQuestSurfaceCache cached = surfaceCache;
		if (cached != null && cached.matches(
			layout,
			mainViewportHeight,
			detailViewportHeight,
			mainScroll,
			detailScroll,
			wikiLinksEnabled))
		{
			return cached;
		}
		SelectedQuestSurfaceCache secondary = secondarySurfaceCache;
		if (secondary != null && secondary.matches(
			layout,
			mainViewportHeight,
			detailViewportHeight,
			mainScroll,
			detailScroll,
			wikiLinksEnabled))
		{
			surfaceCache = secondary;
			secondarySurfaceCache = cached;
			return secondary;
		}
		if (secondary != null && secondary != cached)
		{
			secondarySurfaceCache = null;
			secondary.release();
		}

		RenderedSelectedSurface mainSurface = renderMainSurface(
			graphics,
			layout,
			mainViewportHeight,
			mainScroll,
			wikiLinksEnabled);
		RenderedSelectedSurface detailSurface = renderDetailSurface(
			graphics,
			layout,
			detailViewportHeight,
			detailScroll,
			wikiLinksEnabled);
		SelectedQuestSurfaceCache replacement = new SelectedQuestSurfaceCache(
			layout,
			mainViewportHeight,
			detailViewportHeight,
			mainScroll,
			detailScroll,
			wikiLinksEnabled,
			mainSurface.image,
			detailSurface.image,
			mainSurface.semanticHits,
			detailSurface.semanticHits);
		secondarySurfaceCache = cached;
		surfaceCache = replacement;
		return replacement;
	}

	private RenderedSelectedSurface renderMainSurface(
		Graphics2D source,
		SelectedQuestLayoutCache layout,
		int viewportHeight,
		int scroll,
		boolean wikiLinksEnabled)
	{
		Dimension size = boundedViewportSurfaceSize(
			layout.mainWidth,
			viewportHeight,
			layout.mainHeight,
			scroll);
		if (size.width <= 0 || size.height <= 0)
		{
			return RenderedSelectedSurface.empty();
		}

		BufferedImage surface = new BufferedImage(
			size.width,
			size.height,
			BufferedImage.TYPE_INT_ARGB);
		List<SemanticHit> semanticHits = new ArrayList<>();
		Graphics2D canvas = surface.createGraphics();
		try
		{
			canvas.setRenderingHints(source.getRenderingHints());
			Rectangle surfaceBounds = new Rectangle(0, 0, size.width, size.height);
			layoutMainContent(
				DetailRenderer.LayoutContext.render(
					canvas,
					surfaceBounds,
					scroll,
					semanticHits,
					surfaceBounds,
					wikiLinksEnabled),
				layout.quest,
				layout.compact,
				layout.checklistExpansion.expandedChecklistIds,
				layout.alwaysExpandChecklists);
		}
		finally
		{
			canvas.dispose();
		}
		return new RenderedSelectedSurface(surface, semanticHits);
	}

	private RenderedSelectedSurface renderDetailSurface(
		Graphics2D source,
		SelectedQuestLayoutCache layout,
		int viewportHeight,
		int scroll,
		boolean wikiLinksEnabled)
	{
		Dimension size = boundedViewportSurfaceSize(
			layout.detailWidth,
			viewportHeight,
			layout.detailHeight,
			scroll);
		if (layout.compact || size.width <= 0 || size.height <= 0)
		{
			return RenderedSelectedSurface.empty();
		}

		BufferedImage surface = new BufferedImage(
			size.width,
			size.height,
			BufferedImage.TYPE_INT_ARGB);
		List<SemanticHit> semanticHits = new ArrayList<>();
		Graphics2D canvas = surface.createGraphics();
		try
		{
			canvas.setRenderingHints(source.getRenderingHints());
			Rectangle surfaceBounds = new Rectangle(0, 0, size.width, size.height);
			detailRenderer.layoutDetailContent(
				DetailRenderer.LayoutContext.render(
					canvas,
					surfaceBounds,
					scroll,
					semanticHits,
					surfaceBounds,
					wikiLinksEnabled),
				layout.quest);
		}
		finally
		{
			canvas.dispose();
		}
		return new RenderedSelectedSurface(surface, semanticHits);
	}

	static Dimension boundedViewportSurfaceSize(
		int width,
		int viewportHeight,
		int totalHeight,
		int scroll)
	{
		int safeWidth = Math.max(0, width);
		int safeViewportHeight = Math.max(0, viewportHeight);
		int safeTotalHeight = Math.max(0, totalHeight);
		int safeScroll = JournalOverlay.clamp(scroll, 0, safeTotalHeight);
		return new Dimension(
			safeWidth,
			Math.min(safeViewportHeight, safeTotalHeight - safeScroll));
	}

	private static void blitViewportSurface(
		Graphics2D graphics,
		Rectangle content,
		BufferedImage surface)
	{
		if (surface == null || content.width <= 0 || content.height <= 0)
		{
			return;
		}

		int width = Math.min(content.width, surface.getWidth());
		int height = Math.min(content.height, surface.getHeight());
		if (width > 0 && height > 0)
		{
			graphics.drawImage(surface, content.x, content.y, null);
		}
	}

	int layoutMainContent(
		DetailRenderer.LayoutContext context,
		JournalSnapshot.SelectedQuest quest,
		boolean includeDetails,
		Set<String> expandedChecklistIds,
		boolean alwaysExpandChecklists)
	{
		JournalSnapshot.QuestOverview overview = quest.getOverview();
		int cursor = 4;

		boolean hasPreviousSection = false;
		if (includeDetails
			&& DetailRenderer.hasRequirementSections(quest))
		{
			cursor = detailRenderer.layoutRequirements(
				context,
				quest,
				cursor);
			hasPreviousSection = true;
		}

		if (includeDetails && !quest.getRecommendations().isEmpty())
		{
			cursor = JournalOverlay.sectionStartCursor(cursor, hasPreviousSection);
			cursor = detailRenderer.layoutRecommendations(
				context,
				quest,
				cursor);
			hasPreviousSection = true;
		}

		if (includeDetails && !quest.getNotes().isEmpty())
		{
			cursor = JournalOverlay.sectionStartCursor(cursor, hasPreviousSection);
			cursor = detailRenderer.layoutNotes(
				context,
				quest,
				cursor);
			hasPreviousSection = true;
		}

		boolean questComplete =
			overview.getState() == JournalSnapshot.QuestState.COMPLETE;
		List<JournalSnapshot.Objective> objectives = quest.getObjectives();
		for (int first = 0; first < objectives.size();)
		{
			JournalSnapshot.Objective firstObjective = objectives.get(first);
			String section = firstObjective.getSection();
			String sectionGroupId = JournalOverlay.objectiveSectionGroupId(
				firstObjective);
			int last = first + 1;
			while (last < objectives.size()
				&& Objects.equals(
					sectionGroupId,
					JournalOverlay.objectiveSectionGroupId(objectives.get(last))))
			{
				last++;
			}
			List<JournalSnapshot.Objective> sectionObjectives =
				objectives.subList(first, last);
			cursor = JournalOverlay.sectionStartCursor(cursor, hasPreviousSection);
			if (!section.isEmpty())
			{
				cursor = detailRenderer.layoutObjectiveSection(
					context,
					cursor,
					sectionObjectives,
					expandedChecklistIds,
					alwaysExpandChecklists);
			}
			cursor = detailRenderer.layoutObjectiveList(
				context,
				cursor,
				sectionObjectives,
				questComplete);
			hasPreviousSection = true;
			first = last;
		}

		if (includeDetails && !quest.getRewards().isEmpty())
		{
			cursor = JournalOverlay.sectionStartCursor(cursor, hasPreviousSection);
			cursor = detailRenderer.layoutRewards(
				context,
				quest,
				cursor);
		}
		return cursor + 5 + (includeDetails
			&& DetailRenderer.hasSupplementalSections(quest) ? 6 : 0);
	}

	synchronized boolean toggleChecklist(String checklistId)
	{
		if (checklistId == null || checklistId.trim().isEmpty())
		{
			return false;
		}
		LinkedHashSet<String> expanded = new LinkedHashSet<>(
			checklistExpansionState.expandedChecklistIds);
		boolean nowExpanded;
		if (expanded.remove(checklistId))
		{
			nowExpanded = false;
		}
		else
		{
			expanded.add(checklistId);
			nowExpanded = true;
		}
		checklistExpansionState = new ChecklistExpansionState(expanded);
		clearCaches();
		return nowExpanded;
	}

	Set<String> expandedChecklistIds()
	{
		return checklistExpansionState.expandedChecklistIds;
	}

	void restoreExpandedChecklistIds(Set<String> expandedChecklistIds)
	{
		checklistExpansionState = new ChecklistExpansionState(
			expandedChecklistIds == null
				? Collections.emptySet()
				: expandedChecklistIds);
		clearCaches();
	}

	void resetChecklistExpansion()
	{
		checklistExpansionState = ChecklistExpansionState.empty();
		clearCaches();
	}

	void clearCaches()
	{
		layoutCache = null;
		secondaryLayoutCache = null;
		clearSurfaceCaches();
	}

	synchronized void clearSurfaceCaches()
	{
		SelectedQuestSurfaceCache primary = surfaceCache;
		SelectedQuestSurfaceCache secondary = secondarySurfaceCache;
		surfaceCache = null;
		secondarySurfaceCache = null;
		if (primary != null)
		{
			primary.release();
		}
		if (secondary != null && secondary != primary)
		{
			secondary.release();
		}
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	static final class RenderResult
	{
		final List<SemanticHit> semanticHits;
		final int mainScroll;
		final int detailScroll;
		final int mainMaximumScroll;
		final int detailMaximumScroll;
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	private static final class SelectedQuestLayoutCache
	{
		private final JournalSnapshot.SelectedQuest quest;
		private final int mainWidth;
		private final int detailWidth;
		private final boolean compact;
		private final boolean alwaysExpandChecklists;
		private final ChecklistExpansionState checklistExpansion;
		private final FontRenderContext fontRenderContext;
		private final int mainHeight;
		private final int detailHeight;

		private boolean matches(
			JournalSnapshot.SelectedQuest requestedQuest,
			int requestedMainWidth,
			int requestedDetailWidth,
			boolean requestedCompact,
			boolean requestedAlwaysExpandChecklists,
			ChecklistExpansionState requestedChecklistExpansion,
			FontRenderContext requestedFontRenderContext)
		{
			return quest == requestedQuest
				&& mainWidth == requestedMainWidth
				&& detailWidth == requestedDetailWidth
				&& compact == requestedCompact
				&& alwaysExpandChecklists == requestedAlwaysExpandChecklists
				&& checklistExpansion == requestedChecklistExpansion
				&& fontRenderContext.equals(requestedFontRenderContext);
		}
	}

	private static final class ChecklistExpansionState
	{
		private static final ChecklistExpansionState EMPTY =
			new ChecklistExpansionState(Collections.emptySet());

		private final Set<String> expandedChecklistIds;

		private ChecklistExpansionState(Set<String> expandedChecklistIds)
		{
			this.expandedChecklistIds = expandedChecklistIds.isEmpty()
				? Collections.emptySet()
				: Collections.unmodifiableSet(
					new LinkedHashSet<>(expandedChecklistIds));
		}

		private static ChecklistExpansionState empty()
		{
			return EMPTY;
		}
	}

	private static final class RenderedSelectedSurface
	{
		private static final RenderedSelectedSurface EMPTY =
			new RenderedSelectedSurface(null, Collections.emptyList());

		private final BufferedImage image;
		private final List<SemanticHit> semanticHits;

		private RenderedSelectedSurface(
			BufferedImage image,
			List<SemanticHit> semanticHits)
		{
			this.image = image;
			this.semanticHits = semanticHits.isEmpty()
				? Collections.emptyList()
				: Collections.unmodifiableList(new ArrayList<>(semanticHits));
		}

		private static RenderedSelectedSurface empty()
		{
			return EMPTY;
		}
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	private static final class SelectedQuestSurfaceCache
	{
		private final SelectedQuestLayoutCache layout;
		private final int mainViewportHeight;
		private final int detailViewportHeight;
		private final int mainScroll;
		private final int detailScroll;
		private final boolean wikiLinksEnabled;
		private final BufferedImage mainSurface;
		private final BufferedImage detailSurface;
		private final List<SemanticHit> mainSemanticHits;
		private final List<SemanticHit> detailSemanticHits;
		private int positionedMainX = Integer.MIN_VALUE;
		private int positionedMainY = Integer.MIN_VALUE;
		private int positionedDetailX = Integer.MIN_VALUE;
		private int positionedDetailY = Integer.MIN_VALUE;
		private List<SemanticHit> positionedSemanticHits = Collections.emptyList();

		private boolean matches(
			SelectedQuestLayoutCache requestedLayout,
			int requestedMainViewportHeight,
			int requestedDetailViewportHeight,
			int requestedMainScroll,
			int requestedDetailScroll,
			boolean requestedWikiLinksEnabled)
		{
			return layout == requestedLayout
				&& mainViewportHeight == requestedMainViewportHeight
				&& detailViewportHeight == requestedDetailViewportHeight
				&& mainScroll == requestedMainScroll
				&& detailScroll == requestedDetailScroll
				&& wikiLinksEnabled == requestedWikiLinksEnabled;
		}

		private synchronized List<SemanticHit> positionedSemanticHits(
			Rectangle mainBody,
			Rectangle detailBody)
		{
			if (mainSemanticHits.isEmpty() && detailSemanticHits.isEmpty())
			{
				return Collections.emptyList();
			}
			if (positionedMainX == mainBody.x
				&& positionedMainY == mainBody.y
				&& positionedDetailX == detailBody.x
				&& positionedDetailY == detailBody.y)
			{
				return positionedSemanticHits;
			}

			List<SemanticHit> translated = new ArrayList<>(
				mainSemanticHits.size() + detailSemanticHits.size());
			for (SemanticHit hit : mainSemanticHits)
			{
				translated.add(hit.translated(mainBody.x, mainBody.y));
			}
			if (!layout.compact)
			{
				for (SemanticHit hit : detailSemanticHits)
				{
					translated.add(hit.translated(detailBody.x, detailBody.y));
				}
			}
			positionedMainX = mainBody.x;
			positionedMainY = mainBody.y;
			positionedDetailX = detailBody.x;
			positionedDetailY = detailBody.y;
			positionedSemanticHits = Collections.unmodifiableList(translated);
			return positionedSemanticHits;
		}

		private synchronized void release()
		{
			positionedSemanticHits = Collections.emptyList();
			if (mainSurface != null)
			{
				mainSurface.flush();
			}
			if (detailSurface != null)
			{
				detailSurface.flush();
			}
		}
	}
}
