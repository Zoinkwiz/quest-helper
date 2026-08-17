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
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.questjournal;

import com.questhelper.QuestHelperConfig;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayManager;

/** Manages panel placement, persisted bounds, and allowed movement area. */
@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
final class PanelController
{
	private static final String PANEL_INITIALIZED_KEY = "journalPanelInitialized";
	private static final String PANEL_X_KEY = "journalPanelX";
	private static final String PANEL_Y_KEY = "journalPanelY";
	private static final String PANEL_WIDTH_KEY = "journalPanelWidth";
	private static final String PANEL_HEIGHT_KEY = "journalPanelHeight";
	private static final String PANEL_MAXIMIZED_KEY = "journalPanelMaximized";
	private static final int[] MAIN_MODAL_WIDGET_IDS = {
		InterfaceID.Toplevel.MAINMODAL,
		InterfaceID.ToplevelOsrsStretch.MAINMODAL,
		InterfaceID.ToplevelPreEoc.MAINMODAL,
		InterfaceID.ToplevelOsm.MAINMODAL
	};

	@NonNull
	private final Client client;
	@NonNull
	private final ConfigManager configManager;
	@NonNull
	private final QuestHelperConfig config;
	@NonNull
	private final OverlayManager overlayManager;

	private final Object stateLock = new Object();
	private volatile Rectangle requestedBounds = new Rectangle();
	private volatile Rectangle restoredBounds = new Rectangle();
	private volatile long stateRevision;
	private volatile boolean initialized;
	private volatile boolean maximized;
	private Point resizeGrabOffset = new Point(1, 1);

	private boolean suppressOverlayResetCallbacks;
	private Widget cachedMovementRoot;
	private List<Widget> cachedMovementParentPath = Collections.emptyList();
	private Rectangle lastContentBounds = new Rectangle();
	private int contentBoundsGameCycle = Integer.MIN_VALUE;
	private int contentBoundsTopLevel = Integer.MIN_VALUE;
	private boolean contentBoundsSafeArea;
	private Rectangle contentBoundsCanvas = new Rectangle();
	private Rectangle cachedContentBounds = new Rectangle();

	void loadConfiguration()
	{
		synchronized (stateLock)
		{
			initialized = configuration(PANEL_INITIALIZED_KEY, Boolean.class, false);
			requestedBounds = new Rectangle(
				configuration(PANEL_X_KEY, Integer.class, 0),
				configuration(PANEL_Y_KEY, Integer.class, 0),
				Math.max(
					JournalGeometry.minimumWidth(),
					configuration(
						PANEL_WIDTH_KEY,
						Integer.class,
						JournalGeometry.defaultWidth())),
				Math.max(
					JournalGeometry.minimumHeight(),
					configuration(
						PANEL_HEIGHT_KEY,
						Integer.class,
						JournalGeometry.defaultHeight())));
			restoredBounds = new Rectangle(requestedBounds);
			maximized = configuration(PANEL_MAXIMIZED_KEY, Boolean.class, false);
		}
	}

	void resetTransientState()
	{
		suppressOverlayResetCallbacks = false;
		cachedMovementRoot = null;
		cachedMovementParentPath = Collections.emptyList();
		lastContentBounds = new Rectangle();
		resizeGrabOffset = new Point(1, 1);
		invalidateContentBoundsCache();
	}

	void invalidateContentBoundsCache()
	{
		contentBoundsGameCycle = Integer.MIN_VALUE;
		contentBoundsTopLevel = Integer.MIN_VALUE;
		contentBoundsCanvas = new Rectangle();
		cachedContentBounds = new Rectangle();
	}

	void invalidateMovementConstraint()
	{
		lastContentBounds = new Rectangle();
		invalidateContentBoundsCache();
	}

	boolean isMaximized()
	{
		return maximized;
	}

	QuestJournalManager.JournalPanelRenderState getRenderState(Rectangle viewportBounds)
	{
		synchronized (stateLock)
		{
			Rectangle usableViewport = new Rectangle(viewportBounds);
			if (!initialized)
			{
				requestedBounds = JournalGeometry.defaultPanelBounds(usableViewport);
				restoredBounds = new Rectangle(requestedBounds);
				initialized = true;
				saveBounds();
			}
			Rectangle bounds = maximized
				? new Rectangle(usableViewport)
				: JournalGeometry.clampPanelBounds(requestedBounds, usableViewport);
			return new QuestJournalManager.JournalPanelRenderState(
				bounds,
				maximized,
				stateRevision);
		}
	}

	boolean isRenderStateCurrent(long revision)
	{
		return stateRevision == revision;
	}

	boolean commitRender(long revision, Runnable commit)
	{
		synchronized (stateLock)
		{
			if (stateRevision != revision)
			{
				return false;
			}
			commit.run();
			return true;
		}
	}

	Rectangle getContentBounds(Rectangle canvasBounds)
	{
		boolean keepWithinGameArea = config.journalKeepWithinGameArea();
		int gameCycle = client.getGameCycle();
		int activeTopLevel = client.getTopLevelInterfaceId();
		if (contentBoundsGameCycle == gameCycle
			&& contentBoundsTopLevel == activeTopLevel
			&& contentBoundsSafeArea == keepWithinGameArea
			&& contentBoundsCanvas.equals(canvasBounds))
		{
			return new Rectangle(cachedContentBounds);
		}

		Rectangle contentBounds;
		if (!keepWithinGameArea)
		{
			contentBounds = JournalGeometry.contentBounds(
				canvasBounds,
				null,
				new Rectangle());
		}
		else
		{
			List<Rectangle> movementBounds = new ArrayList<>(MAIN_MODAL_WIDGET_IDS.length);
			for (int widgetId : MAIN_MODAL_WIDGET_IDS)
			{
				if (widgetId >>> 16 == activeTopLevel)
				{
					addMovementBounds(movementBounds, client.getWidget(widgetId), false);
					break;
				}
			}
			if (movementBounds.isEmpty() || movementBounds.get(0).isEmpty())
			{
				for (int widgetId : MAIN_MODAL_WIDGET_IDS)
				{
					if (widgetId >>> 16 != activeTopLevel)
					{
						addMovementBounds(movementBounds, client.getWidget(widgetId), true);
					}
				}
			}
			contentBounds = JournalGeometry.contentBounds(
				canvasBounds,
				movementBounds,
				new Rectangle());
		}

		contentBoundsGameCycle = gameCycle;
		contentBoundsTopLevel = activeTopLevel;
		contentBoundsSafeArea = keepWithinGameArea;
		contentBoundsCanvas = new Rectangle(canvasBounds);
		cachedContentBounds = new Rectangle(contentBounds);
		if (!contentBounds.equals(lastContentBounds))
		{
			lastContentBounds = new Rectangle(contentBounds);
			log.debug("Quest Journal movement bounds updated: {}", contentBounds);
		}
		return new Rectangle(contentBounds);
	}

	void saveBounds()
	{
		Rectangle bounds;
		synchronized (stateLock)
		{
			bounds = new Rectangle(requestedBounds);
		}
		setConfiguration(PANEL_X_KEY, bounds.x);
		setConfiguration(PANEL_Y_KEY, bounds.y);
		setConfiguration(PANEL_WIDTH_KEY, bounds.width);
		setConfiguration(PANEL_HEIGHT_KEY, bounds.height);
		setConfiguration(PANEL_INITIALIZED_KEY, true);
	}

	void toggleMaximized(JournalOverlay overlay)
	{
		if (overlay == null)
		{
			return;
		}
		boolean nextMaximized;
		synchronized (stateLock)
		{
			Rectangle viewportBounds = overlay.getViewportBounds();
			Rectangle transitionBounds;
			if (!maximized)
			{
				adoptManagedBoundsLocked(overlay, null);
				restoredBounds = new Rectangle(requestedBounds);
				saveBounds();
				maximized = true;
				transitionBounds = viewportBounds;
			}
			else
			{
				Rectangle restoreTarget = !restoredBounds.isEmpty()
					? new Rectangle(restoredBounds)
					: new Rectangle(requestedBounds);
				releaseManagedPlacement(overlay);
				maximized = false;
				if (!restoreTarget.isEmpty())
				{
					requestedBounds = restoreTarget;
					restoredBounds = new Rectangle(restoreTarget);
					initialized = true;
					saveBounds();
				}
				else
				{
					initialized = false;
				}
				transitionBounds = viewportBounds.isEmpty()
					? new Rectangle(requestedBounds)
					: JournalGeometry.clampPanelBounds(requestedBounds, viewportBounds);
			}
			stateRevision++;
			overlay.beginPanelStateTransition(transitionBounds);
			nextMaximized = maximized;
		}
		setConfiguration(PANEL_MAXIMIZED_KEY, nextMaximized);
	}

	void adoptManagedBounds(JournalOverlay overlay, JournalGeometry currentGeometry)
	{
		synchronized (stateLock)
		{
			if (maximized || overlay == null)
			{
				return;
			}
			adoptManagedBoundsLocked(overlay, currentGeometry);
			stateRevision++;
		}
	}

	boolean beginResize(
		JournalOverlay overlay,
		JournalGeometry currentGeometry,
		Point pointer)
	{
		synchronized (stateLock)
		{
			if (maximized || overlay == null || pointer == null)
			{
				return false;
			}
			Rectangle renderedBounds = currentGeometry == null
				? overlay.getPanelBounds()
				: currentGeometry.panelBounds();
			if (renderedBounds.isEmpty())
			{
				return false;
			}
			adoptManagedBoundsLocked(overlay, currentGeometry);
			resizeGrabOffset = JournalGeometry.resizeGrabOffset(
				renderedBounds,
				pointer);
			stateRevision++;
			return true;
		}
	}

	private void adoptManagedBoundsLocked(
		JournalOverlay overlay,
		JournalGeometry currentGeometry)
	{
		Rectangle renderedBounds = currentGeometry == null
			? overlay.getPanelBounds()
			: currentGeometry.panelBounds();
		if (!renderedBounds.isEmpty())
		{
			requestedBounds = new Rectangle(renderedBounds);
			restoredBounds = new Rectangle(renderedBounds);
			initialized = true;
		}
		releaseManagedPlacement(overlay);
	}

	void dragTo(Point point, Point dragOffset, Rectangle viewportBounds)
	{
		synchronized (stateLock)
		{
			Rectangle nextBounds = JournalGeometry.dragBounds(
				requestedBounds,
				point,
				dragOffset,
				viewportBounds);
			if (!nextBounds.equals(requestedBounds))
			{
				requestedBounds = nextBounds;
				stateRevision++;
			}
		}
	}

	void resizeTo(Point point, Rectangle viewportBounds)
	{
		synchronized (stateLock)
		{
			Rectangle nextBounds = JournalGeometry.resizeBoundsFromBottomRight(
				requestedBounds,
				point,
				resizeGrabOffset,
				viewportBounds);
			if (!nextBounds.equals(requestedBounds))
			{
				requestedBounds = nextBounds;
				stateRevision++;
			}
		}
	}

	void onOverlayReset(JournalOverlay overlay)
	{
		synchronized (stateLock)
		{
			if (suppressOverlayResetCallbacks || maximized)
			{
				return;
			}
			clearPersistedBounds(overlay);
		}
	}

	void resetConfiguration(JournalOverlay overlay)
	{
		synchronized (stateLock)
		{
			releaseManagedPlacement(overlay);
			clearPersistedBounds(overlay);
		}
	}

	private void clearPersistedBounds(JournalOverlay overlay)
	{
		initialized = false;
		maximized = false;
		requestedBounds = new Rectangle();
		restoredBounds = new Rectangle();
		stateRevision++;
		if (overlay != null)
		{
			overlay.beginPanelStateTransition(new Rectangle());
		}
		unsetConfiguration();
	}

	private void addMovementBounds(
		List<Rectangle> bounds,
		Widget widget,
		boolean requireVisible)
	{
		if (widget != null && (!requireVisible || !widget.isHidden()))
		{
			bounds.add(effectiveParentBounds(widget));
		}
	}

	private Rectangle effectiveParentBounds(Widget movementRoot)
	{
		if (cachedMovementRoot != movementRoot)
		{
			cachedMovementRoot = movementRoot;
			cachedMovementParentPath = findMovementParentPath(movementRoot);
		}
		Rectangle nestedParentBounds = intersectWidgetBounds(cachedMovementParentPath);
		if (nestedParentBounds != null)
		{
			return nestedParentBounds;
		}
		Rectangle effective = null;
		for (Widget current = movementRoot.getParent(); current != null; current = current.getParent())
		{
			Rectangle bounds = current.getBounds();
			if (bounds.width <= 0 || bounds.height <= 0)
			{
				continue;
			}
			effective = effective == null ? new Rectangle(bounds) : effective.intersection(bounds);
			if (effective.isEmpty())
			{
				return new Rectangle();
			}
		}
		return effective == null ? new Rectangle() : effective;
	}

	private List<Widget> findMovementParentPath(Widget movementRoot)
	{
		Widget[] roots = client.getWidgetRoots();
		if (roots == null)
		{
			return Collections.emptyList();
		}
		for (Widget root : roots)
		{
			List<Widget> path = new ArrayList<>();
			Set<Widget> visited = Collections.newSetFromMap(new IdentityHashMap<>());
			if (findMovementParentPath(root, movementRoot, path, visited))
			{
				return Collections.unmodifiableList(new ArrayList<>(path));
			}
		}
		return Collections.emptyList();
	}

	private boolean findMovementParentPath(
		Widget current,
		Widget target,
		List<Widget> path,
		Set<Widget> visited)
	{
		if (current == null || !visited.add(current))
		{
			return false;
		}
		if (sameWidget(current, target))
		{
			return !path.isEmpty();
		}
		path.add(current);
		Widget[][] childGroups = {
			current.getDynamicChildren(),
			current.getStaticChildren(),
			current.getNestedChildren()
		};
		for (Widget[] children : childGroups)
		{
			if (children == null)
			{
				continue;
			}
			for (Widget child : children)
			{
				if (findMovementParentPath(child, target, path, visited))
				{
					return true;
				}
			}
		}
		path.remove(path.size() - 1);
		return false;
	}

	private static Rectangle intersectWidgetBounds(List<Widget> widgets)
	{
		Rectangle effective = null;
		for (Widget widget : widgets)
		{
			Rectangle bounds = widget.getBounds();
			if (bounds.width <= 0 || bounds.height <= 0)
			{
				continue;
			}
			effective = effective == null ? new Rectangle(bounds) : effective.intersection(bounds);
			if (effective.isEmpty())
			{
				return effective;
			}
		}
		return effective;
	}

	private static boolean sameWidget(Widget first, Widget second)
	{
		return first == second
			|| first.getId() == second.getId() && first.getIndex() == second.getIndex();
	}

	private <T> T configuration(String key, Class<T> type, T fallback)
	{
		T value = configManager.getConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			key,
			type);
		return value == null ? fallback : value;
	}

	private void setConfiguration(String key, Object value)
	{
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, key, value);
	}

	private void unsetConfiguration()
	{
		String[] keys = {
			PANEL_INITIALIZED_KEY,
			PANEL_X_KEY,
			PANEL_Y_KEY,
			PANEL_WIDTH_KEY,
			PANEL_HEIGHT_KEY,
			PANEL_MAXIMIZED_KEY
		};
		for (String key : keys)
		{
			configManager.unsetConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, key);
		}
	}

	private void releaseManagedPlacement(JournalOverlay overlay)
	{
		if (overlay != null)
		{
			resetOverlayManagerState(overlay);
			overlay.clearManagedPlacement();
		}
	}

	private void resetOverlayManagerState(Overlay overlay)
	{
		suppressOverlayResetCallbacks = true;
		try
		{
			overlayManager.resetOverlay(overlay);
		}
		finally
		{
			suppressOverlayResetCallbacks = false;
		}
	}

}
