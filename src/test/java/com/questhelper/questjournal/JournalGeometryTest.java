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

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JournalGeometryTest
{
	@Test
	public void smallViewportClampsThePanelAndItsInteractiveBounds()
	{
		Rectangle requested = new Rectangle(-100, -80, 800, 600);
		Rectangle viewport = new Rectangle(40, 50, 180, 140);

		JournalGeometry geometry = JournalGeometry.create(requested, viewport);

		assertEquals(viewport, geometry.panelBounds());
		assertContained(geometry.panelBounds(), geometry.headerBounds());
		assertContained(geometry.panelBounds(), geometry.closeButtonBounds());
		assertContained(geometry.panelBounds(), geometry.maximizeButtonBounds());
		assertContained(geometry.panelBounds(), geometry.resizeHandleBounds());
		assertContained(geometry.panelBounds(), geometry.resizeHandleHitBounds());
		assertContained(geometry.panelBounds(), geometry.questListPaneBounds());
		assertContained(geometry.panelBounds(), geometry.mainPaneBounds());
		assertContained(geometry.panelBounds(), geometry.compactHeaderBounds());
	}

	@Test
	public void resizeHandleHasALargerHitTargetWithoutChangingItsAppearance()
	{
		JournalGeometry geometry = JournalGeometry.create(
			new Rectangle(20, 30, 620, 480),
			new Rectangle(0, 0, 1000, 700));

		Rectangle visualBounds = geometry.resizeHandleBounds();
		Rectangle hitBounds = geometry.resizeHandleHitBounds();

		assertEquals(new Rectangle(624, 494, 16, 16), visualBounds);
		assertEquals(new Rectangle(620, 490, 20, 20), hitBounds);
		assertContained(hitBounds, visualBounds);
		assertTrue(hitBounds.contains(new Point(621, 491)));
		assertFalse(visualBounds.contains(new Point(621, 491)));
		assertFalse(hitBounds.contains(new Point(619, 489)));
	}

	@Test
	public void viewportClampingIsTransientAndDoesNotChangeRequestedState()
	{
		Rectangle requested = new Rectangle(620, 75, 760, 520);
		Rectangle originalRequest = new Rectangle(requested);
		Rectangle narrowViewport = new Rectangle(0, 0, 900, 600);
		Rectangle restoredViewport = new Rectangle(0, 0, 1500, 800);

		JournalGeometry temporarilyClamped = JournalGeometry.create(requested, narrowViewport);
		JournalGeometry restored = JournalGeometry.create(requested, restoredViewport);

		assertEquals(new Rectangle(140, 75, 760, 520), temporarilyClamped.panelBounds());
		assertEquals(originalRequest, temporarilyClamped.requestedBounds());
		assertEquals(originalRequest, requested);
		assertEquals(originalRequest, restored.panelBounds());
	}

	@Test
	public void draggingPreservesTheOriginalPointerOffset()
	{
		Rectangle requested = new Rectangle(100, 90, 700, 480);
		Point pointerOffset = new Point(37, 16);

		Rectangle dragged = JournalGeometry.dragRequestedBounds(
			requested,
			new Point(412, 266),
			pointerOffset);

		assertEquals(new Rectangle(375, 250, 700, 480), dragged);
		assertEquals(new Rectangle(100, 90, 700, 480), requested);
	}

	@Test
	public void clampedDraggingDoesNotAccumulateDistanceAtAnEdge()
	{
		Rectangle panel = new Rectangle(100, 90, 500, 350);
		Rectangle viewport = new Rectangle(0, 0, 800, 600);
		Point pointerOffset = new Point(20, 10);

		assertEquals(
			new Rectangle(300, 90, 500, 350),
			JournalGeometry.dragBounds(panel, new Point(900, 100), pointerOffset, viewport));
		assertEquals(
			new Rectangle(299, 90, 500, 350),
			JournalGeometry.dragBounds(panel, new Point(319, 100), pointerOffset, viewport));
	}

	@Test
	public void bottomRightResizeStopsAtTheViewportWithoutMovingItsOrigin()
	{
		Rectangle panel = new Rectangle(140, 40, 1000, 600);
		Rectangle viewport = new Rectangle(0, 0, 1308, 659);

		Rectangle resized = JournalGeometry.resizeBoundsFromBottomRight(
			panel,
			new Point(1600, 900),
			viewport);

		assertEquals(new Rectangle(140, 40, 1168, 619), resized);
	}

	@Test
	public void bottomRightResizePreservesTheGrabOffsetInsideTheHandle()
	{
		Rectangle panel = new Rectangle(100, 80, 680, 420);
		Rectangle viewport = new Rectangle(0, 0, 1200, 800);
		Point pressed = new Point(772, 494);
		Point grabOffset = JournalGeometry.resizeGrabOffset(panel, pressed);

		Rectangle resized = JournalGeometry.resizeBoundsFromBottomRight(
			panel,
			new Point(pressed.x + 1, pressed.y + 1),
			grabOffset,
			viewport);

		assertEquals(new Point(8, 6), grabOffset);
		assertEquals(new Rectangle(100, 80, 681, 421), resized);
	}

	@Test
	public void bottomRightResizeKeepsAMaximumPanelStationary()
	{
		Rectangle viewport = new Rectangle(40, 30, 1000, 700);
		Rectangle panel = new Rectangle(viewport);

		Rectangle resized = JournalGeometry.resizeBoundsFromBottomRight(
			panel,
			new Point(1400, 1000),
			viewport);

		assertEquals(panel, resized);
	}

	@Test
	public void bottomRightResizeDoesNotShiftAnOffsetPanelAtItsMaximumSize()
	{
		Rectangle viewport = new Rectangle(0, 0, 1308, 659);
		Rectangle panel = new Rectangle(140, 40, 1168, 619);

		Rectangle resized = JournalGeometry.resizeBoundsFromBottomRight(
			panel,
			new Point(1600, 900),
			viewport);

		assertEquals(panel, resized);
	}

	@Test
	public void wideLayoutUsesASeparateDetailPane()
	{
		JournalGeometry geometry = JournalGeometry.create(
			new Rectangle(20, 30, 900, 600),
			new Rectangle(0, 0, 1200, 800));

		assertTrue(geometry.hasDetailPane());
		assertFalse(geometry.detailPaneBounds().isEmpty());
		assertTrue(geometry.compactHeaderBounds().isEmpty());
		assertNotEquals(geometry.mainContentBounds(), geometry.detailContentBounds());
		assertTrue(geometry.questListPaneBounds().x < geometry.mainPaneBounds().x);
		assertTrue(geometry.mainPaneBounds().x < geometry.detailPaneBounds().x);
		assertEquals(212, geometry.questListPaneBounds().width);
		assertEquals(220, geometry.detailPaneBounds().width);
	}

	@Test
	public void detailPaneAppearsAtTheWideLayoutThreshold()
	{
		Rectangle viewport = new Rectangle(0, 0, 1200, 800);

		assertFalse(JournalGeometry.create(
			new Rectangle(20, 30, 759, 600),
			viewport).hasDetailPane());
		assertTrue(JournalGeometry.create(
			new Rectangle(20, 30, 760, 600),
			viewport).hasDetailPane());
	}

	@Test
	public void defaultPanelUsesTheConfiguredSizeAndCentersInsideGameContent()
	{
		assertEquals(
			new Rectangle(145, 135, 790, 490),
			JournalGeometry.defaultPanelBounds(new Rectangle(40, 30, 1000, 700)));
		assertEquals(
			new Rectangle(12, 18, 500, 320),
			JournalGeometry.defaultPanelBounds(new Rectangle(12, 18, 500, 320)));
	}

	@Test
	public void gameContentPrefersMainModalThenSceneViewportThenCanvas()
	{
		Rectangle canvas = new Rectangle(0, 0, 1200, 800);
		Rectangle scene = new Rectangle(20, 25, 900, 600);

		assertEquals(
			new Rectangle(30, 40, 980, 650),
			JournalGeometry.contentBounds(
				canvas,
				Arrays.asList(new Rectangle(), new Rectangle(30, 40, 980, 650)),
				scene));
		assertEquals(
			scene,
			JournalGeometry.contentBounds(canvas, Arrays.asList(new Rectangle()), scene));
		assertEquals(
			canvas,
			JournalGeometry.contentBounds(canvas, null, new Rectangle()));
	}

	@Test
	public void gameContentIsClippedToTheCanvasWithoutAddingAMargin()
	{
		assertEquals(
			new Rectangle(0, 15, 900, 685),
			JournalGeometry.contentBounds(
				new Rectangle(0, 0, 900, 700),
				Arrays.asList(new Rectangle(-20, 15, 1000, 800)),
				new Rectangle()));
	}

	@Test
	public void headerUsesBankStyleChromeDimensions()
	{
		JournalGeometry geometry = JournalGeometry.create(
			new Rectangle(20, 30, 620, 480),
			new Rectangle(0, 0, 1000, 700));

		Rectangle header = geometry.headerBounds();
		Rectangle maximize = geometry.maximizeButtonBounds();
		Rectangle close = geometry.closeButtonBounds();
		assertEquals(30, header.height);
		assertEquals(new Rectangle(609, 37, 21, 21), close);
		assertEquals(new Rectangle(584, 37, 21, 21), maximize);
		assertEquals(4, close.x - maximize.x - maximize.width);
		assertEquals(new Rectangle(20, 37, 620, 21), JournalGeometry.headerContent(header));
	}

	@Test
	public void draggableHeaderIncludesTheFullTexturedSeparatorBelowTheHeader()
	{
		JournalGeometry geometry = JournalGeometry.create(
			new Rectangle(20, 30, 620, 480),
			new Rectangle(0, 0, 1000, 700));

		Rectangle header = geometry.headerBounds();
		Rectangle draggable = geometry.draggableHeaderBounds();
		assertEquals(6, JournalGeometry.titleSeparatorHeight());
		assertEquals(new Rectangle(20, 30, 620, 36), draggable);
		assertTrue(draggable.contains(new Point(330, header.y + header.height)));
		assertTrue(draggable.contains(new Point(330, header.y + header.height + 5)));
		assertFalse(draggable.contains(new Point(330, header.y + header.height + 6)));
	}

	@Test
	public void questHeaderStartsCollapsedAndCanReserveFiveFilters()
	{
		Rectangle panel = new Rectangle(20, 30, 900, 600);
		Rectangle viewport = new Rectangle(0, 0, 1200, 800);
		JournalGeometry geometry = JournalGeometry.create(panel, viewport);
		JournalGeometry expanded = JournalGeometry.create(panel, viewport, true);

		assertEquals(
			63,
			geometry.questListContentBounds().y - geometry.questListPaneBounds().y);
		assertEquals(
			185,
			expanded.questListContentBounds().y - expanded.questListPaneBounds().y);
		assertEquals(
			41,
			geometry.mainContentBounds().y - geometry.mainPaneBounds().y);
		assertEquals(
			5,
			geometry.detailContentBounds().y - geometry.detailPaneBounds().y);
	}

	@Test
	public void automaticActiveQuestModeReclaimsTheManualControlRow()
	{
		Rectangle panel = new Rectangle(20, 30, 900, 600);
		Rectangle viewport = new Rectangle(0, 0, 1200, 800);
		JournalGeometry compact = JournalGeometry.create(panel, viewport, false, false);
		JournalGeometry expanded = JournalGeometry.create(panel, viewport, true, false);

		assertEquals(
			33,
			compact.questListContentBounds().y - compact.questListPaneBounds().y);
		assertEquals(
			155,
			expanded.questListContentBounds().y - expanded.questListPaneBounds().y);
	}

	@Test
	public void compactLayoutCombinesQuestAndDetailContent()
	{
		JournalGeometry geometry = JournalGeometry.create(
			new Rectangle(20, 30, 620, 480),
			new Rectangle(0, 0, 1000, 700));

		assertFalse(geometry.hasDetailPane());
		assertTrue(geometry.detailPaneBounds().isEmpty());
		assertFalse(geometry.compactHeaderBounds().isEmpty());
		assertEquals(geometry.mainContentBounds(), geometry.detailContentBounds());
		assertContained(geometry.mainPaneBounds(), geometry.compactHeaderBounds());
		assertContained(geometry.mainPaneBounds(), geometry.mainContentBounds());
	}

	@Test
	public void clippingReturnsOnlyTheSharedArea()
	{
		Rectangle content = new Rectangle(80, 60, 100, 90);
		Rectangle clippingBounds = new Rectangle(100, 100, 50, 40);

		assertEquals(
			new Rectangle(100, 100, 50, 40),
			JournalGeometry.clip(content, clippingBounds));
		assertEquals(
			new Rectangle(100, 100, 0, 0),
			JournalGeometry.clip(new Rectangle(0, 0, 10, 10), clippingBounds));
	}

	@Test
	public void returnedBoundsAreDefensiveCopies()
	{
		JournalGeometry geometry = JournalGeometry.create(
			new Rectangle(20, 30, 620, 480),
			new Rectangle(0, 0, 1000, 700));
		Rectangle panel = geometry.panelBounds();

		panel.setBounds(0, 0, 1, 1);

		assertEquals(new Rectangle(20, 30, 620, 480), geometry.panelBounds());
	}

	private static void assertContained(Rectangle outer, Rectangle inner)
	{
		if (inner.isEmpty())
		{
			assertTrue(inner.x >= outer.x);
			assertTrue(inner.y >= outer.y);
			assertTrue(inner.x <= outer.x + outer.width);
			assertTrue(inner.y <= outer.y + outer.height);
			return;
		}
		assertEquals(inner, inner.intersection(outer));
	}
}
