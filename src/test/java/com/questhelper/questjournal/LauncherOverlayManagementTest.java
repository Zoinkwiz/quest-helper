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
import java.awt.geom.Area;
import java.util.Collections;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.OverlayLayer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LauncherOverlayManagementTest
{
	@Test
	public void launcherParticipatesInRuneLiteOverlayManagementWithoutResizing()
	{
		LauncherOverlay overlay = new LauncherOverlay(
			null,
			null,
			null,
			null,
			null);

		assertTrue(overlay.isMovable());
		assertFalse(overlay.isResizable());
		assertTrue(overlay.isResettable());
		assertFalse(overlay.isSnappable());
	}

	@Test
	public void launcherStaysAboveWidgetsForStableNativeInputOwnership()
	{
		LauncherOverlay overlay = new LauncherOverlay(
			null,
			null,
			null,
			null,
			null);

		assertEquals(OverlayLayer.ABOVE_WIDGETS, overlay.getLayer());
		assertTrue(overlay.getDrawHooks().isEmpty());
	}

	@Test
	public void nativeWorldMapTooltipOverlapIsExcludedOnlyFromLauncherPaint()
	{
		Area paint = LauncherOverlay.launcherPaintArea(
			new Rectangle(100, 100, 34, 34),
			Collections.singletonList(new Rectangle(120, 110, 30, 20)));

		assertTrue(paint.contains(5, 5));
		assertFalse(paint.contains(21, 11));
		assertTrue(paint.contains(19, 11));
	}

	@Test
	public void nativeWorldMapTooltipMustHaveItsRenderedDynamicChildren()
	{
		Widget tooltip = mock(Widget.class);
		Rectangle viewport = new Rectangle(0, 0, 800, 600);
		when(tooltip.getBounds()).thenReturn(new Rectangle(100, 100, 80, 40));
		when(tooltip.getDynamicChildren()).thenReturn(new Widget[]{
			mock(Widget.class),
			mock(Widget.class),
			mock(Widget.class)
		});

		assertTrue(LauncherOverlay.isActiveNativeTooltip(tooltip, viewport));

		when(tooltip.getDynamicChildren()).thenReturn(new Widget[0]);
		assertFalse(LauncherOverlay.isActiveNativeTooltip(tooltip, viewport));
	}

	@Test
	public void launcherOwnsEveryPixelInItsRenderedBounds()
	{
		Rectangle bounds = new Rectangle(100, 100, 34, 34);

		assertTrue(LauncherOverlay.ownsInput(bounds, new Point(117, 100)));
		assertTrue(LauncherOverlay.ownsInput(bounds, new Point(100, 117)));
		assertTrue(LauncherOverlay.ownsInput(bounds, new Point(133, 117)));
		assertTrue(LauncherOverlay.ownsInput(bounds, new Point(117, 133)));
		assertFalse(LauncherOverlay.ownsInput(bounds, new Point(134, 117)));
	}
}
