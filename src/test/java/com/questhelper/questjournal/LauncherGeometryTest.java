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
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LauncherGeometryTest
{
	@Test
	public void nudgesAutomaticLauncherUpAndLeftToMatchMinimapControls()
	{
		Rectangle minimap = new Rectangle(900, 20, 160, 160);

		Point location = LauncherGeometry.automaticLocation(
			minimap,
			new Rectangle(0, 0, 1100, 700),
			Collections.emptyList());

		Assertions.assertEquals(new Point(924, 177), location);
	}

	@Test
	public void automaticPlacementWorksForRepresentativeGameLayouts()
	{
		Rectangle[] viewports = {
			new Rectangle(0, 0, 765, 503),
			new Rectangle(0, 0, 1100, 700),
			new Rectangle(0, 0, 1400, 900),
			new Rectangle(12, 18, 1024, 768)
		};
		Rectangle[] minimaps = {
			new Rectangle(548, 4, 200, 160),
			new Rectangle(920, 10, 160, 160),
			new Rectangle(1210, 12, 170, 170),
			new Rectangle(856, 28, 160, 160)
		};
		Point[] expectedLocations = {
			new Point(592, 161),
			new Point(944, 167),
			new Point(1239, 179),
			new Point(880, 185)
		};

		for (int i = 0; i < viewports.length; i++)
		{
			Point location = LauncherGeometry.automaticLocation(
				minimaps[i],
				viewports[i],
				Collections.emptyList());
			Rectangle button = new Rectangle(
				location.x,
				location.y,
				LauncherGeometry.BUTTON_SIZE,
				LauncherGeometry.BUTTON_SIZE);

			Assertions.assertEquals(expectedLocations[i], location);
			Assertions.assertEquals(minimaps[i].y + minimaps[i].height - 3, location.y);
			Assertions.assertEquals(
				minimaps[i].x + (minimaps[i].width - LauncherGeometry.BUTTON_SIZE) / 2
					- LauncherGeometry.MINIMAP_HORIZONTAL_NUDGE
					- LauncherGeometry.FINAL_LEFT_CALIBRATION,
				location.x);
			Assertions.assertTrue(LauncherGeometry.safeViewport(viewports[i]).contains(button));
		}
	}

	@Test
	public void searchesLeftThenRightAroundAutomaticBlockers()
	{
		Rectangle minimap = new Rectangle(600, 20, 160, 160);
		Point centered = new Point(
			minimap.x + (minimap.width - LauncherGeometry.BUTTON_SIZE) / 2
				- LauncherGeometry.MINIMAP_HORIZONTAL_NUDGE,
			177);
		Rectangle centerBlocker = buttonAt(centered);

		Point shiftedLeft = LauncherGeometry.automaticLocation(
			minimap,
			new Rectangle(0, 0, 900, 600),
			Collections.singletonList(centerBlocker));
		Assertions.assertEquals(
			new Point(
				centered.x - LauncherGeometry.BUTTON_SIZE
					- LauncherGeometry.BLOCKER_HORIZONTAL_CLEARANCE
					- LauncherGeometry.FINAL_LEFT_CALIBRATION,
				177),
			shiftedLeft);

		Point shiftedRight = LauncherGeometry.automaticLocation(
			minimap,
			new Rectangle(0, 0, 900, 600),
			Arrays.asList(centerBlocker, buttonAt(shiftedLeft)));
		Assertions.assertEquals(
			new Point(
				centered.x + LauncherGeometry.BUTTON_SIZE
					+ LauncherGeometry.BLOCKER_HORIZONTAL_CLEARANCE,
				177),
			shiftedRight);
	}

	@Test
	public void adjacentWidgetPaddingDoesNotCancelTheFinalVisualNudge()
	{
		Assertions.assertEquals(16, LauncherGeometry.BLOCKER_HORIZONTAL_CLEARANCE);
		Assertions.assertEquals(5, LauncherGeometry.FINAL_LEFT_CALIBRATION);
		Assertions.assertEquals(7, LauncherGeometry.MINIMAP_VERTICAL_NUDGE);

		Rectangle minimap = new Rectangle(600, 20, 160, 160);
		Rectangle blockerEndingAtBase = new Rectangle(620, 177, 9, 34);

		Point location = LauncherGeometry.automaticLocation(
			minimap,
			new Rectangle(0, 0, 900, 600),
			Collections.singletonList(blockerEndingAtBase));

		Assertions.assertEquals(new Point(624, 177), location);
	}

	@Test
	public void fixedLayoutTabRowMovesAutomaticLauncherClearAlongSameRow()
	{
		Rectangle viewport = new Rectangle(0, 0, 765, 503);
		Rectangle minimap = new Rectangle(548, 4, 200, 160);
		Rectangle fixedTabs = new Rectangle(522, 168, 243, 36);

		Point location = LauncherGeometry.automaticLocation(
			minimap,
			viewport,
			Collections.singletonList(fixedTabs));
		Rectangle button = buttonAt(location);

		Point preCorrectionLocation = new Point(472, 161);
		Assertions.assertEquals(new Point(467, 161), location);
		Assertions.assertEquals(
			preCorrectionLocation.x - LauncherGeometry.FINAL_LEFT_CALIBRATION,
			location.x);
		Assertions.assertEquals(161, location.y);
		Assertions.assertFalse(button.intersects(fixedTabs));
		Assertions.assertTrue(LauncherGeometry.safeViewport(viewport).contains(button));
	}

	@Test
	public void automaticLauncherHidesWhenTheOnlyRowIsBlocked()
	{
		Rectangle viewport = new Rectangle(0, 0, 500, 300);
		Rectangle minimap = new Rectangle(300, 10, 160, 160);
		Rectangle blockedRow = new Rectangle(0, 174, 500, 34);

		Assertions.assertNull(LauncherGeometry.automaticLocation(
			minimap,
			viewport,
			Collections.singletonList(blockedRow)));
	}

	@Test
	public void automaticLauncherRequiresAMinimapAndEnoughViewportSpace()
	{
		Assertions.assertNull(LauncherGeometry.automaticLocation(
			null,
			new Rectangle(0, 0, 800, 600),
			Collections.emptyList()));
		Assertions.assertNull(LauncherGeometry.automaticLocation(
			new Rectangle(10, 10, 20, 20),
			new Rectangle(10, 20, 30, 30),
			Collections.emptyList()));
	}

	@Test
	public void manualLocationClampsFullyInsideEveryViewportEdge()
	{
		Rectangle viewport = new Rectangle(10, 20, 300, 200);

		Assertions.assertEquals(
			new Point(12, 22),
			LauncherGeometry.clampLocation(new Point(-500, -500), viewport));
		Assertions.assertEquals(
			new Point(274, 184),
			LauncherGeometry.clampLocation(new Point(500, 500), viewport));
	}

	@Test
	public void transientClampDoesNotMutateRequestedLocation()
	{
		Point requested = new Point(900, 500);

		Point narrowed = LauncherGeometry.clampLocation(
			requested,
			new Rectangle(0, 0, 800, 450));
		Point restored = LauncherGeometry.clampLocation(
			requested,
			new Rectangle(0, 0, 1200, 700));

		Assertions.assertEquals(new Point(764, 414), narrowed);
		Assertions.assertEquals(new Point(900, 500), restored);
		Assertions.assertEquals(new Point(900, 500), requested);
	}

	@Test
	public void circularHitAreaExcludesTransparentCorners()
	{
		Point location = new Point(100, 100);

		Assertions.assertTrue(LauncherGeometry.contains(location, new Point(117, 117)));
		Assertions.assertFalse(LauncherGeometry.contains(location, new Point(100, 100)));
		Assertions.assertFalse(LauncherGeometry.contains(location, new Point(133, 100)));
	}

	private static Rectangle buttonAt(Point location)
	{
		return new Rectangle(
			location.x,
			location.y,
			LauncherGeometry.BUTTON_SIZE,
			LauncherGeometry.BUTTON_SIZE);
	}
}
