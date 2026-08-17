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
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

final class LauncherGeometry
{
	static final int BUTTON_SIZE = 34;
	static final int BUTTON_GAP = 4;
	static final int BLOCKER_HORIZONTAL_CLEARANCE = 16;
	static final int MINIMAP_HORIZONTAL_NUDGE = 34;
	static final int FINAL_LEFT_CALIBRATION = 5;
	static final int MINIMAP_VERTICAL_NUDGE = 7;
	static final int VIEWPORT_MARGIN = 2;
	private LauncherGeometry()
	{
	}

	static Rectangle safeViewport(Rectangle viewport)
	{
		Objects.requireNonNull(viewport, "viewport");
		int width = Math.max(0, viewport.width);
		int height = Math.max(0, viewport.height);
		int marginX = Math.min(VIEWPORT_MARGIN, width / 2);
		int marginY = Math.min(VIEWPORT_MARGIN, height / 2);
		return new Rectangle(
			viewport.x + marginX,
			viewport.y + marginY,
			Math.max(0, width - marginX * 2),
			Math.max(0, height - marginY * 2));
	}

	static Point automaticLocation(
		Rectangle minimap,
		Rectangle viewport,
		List<Rectangle> blockers)
	{
		Objects.requireNonNull(viewport, "viewport");
		if (minimap == null || minimap.isEmpty())
		{
			return null;
		}

		Rectangle safeViewport = safeViewport(viewport);
		if (!canContainButton(safeViewport))
		{
			return null;
		}

		long requestedY = (long) minimap.y + minimap.height
			+ BUTTON_GAP - MINIMAP_VERTICAL_NUDGE;
		int minimumY = safeViewport.y;
		int maximumY = safeViewport.y + safeViewport.height - BUTTON_SIZE;
		if (requestedY < minimumY || requestedY > maximumY)
		{
			return null;
		}

		int minimumX = safeViewport.x;
		int maximumX = safeViewport.x + safeViewport.width - BUTTON_SIZE;
		long centeredX = (long) minimap.x + (minimap.width - BUTTON_SIZE) / 2L
			- MINIMAP_HORIZONTAL_NUDGE;
		int baseX = clamp(centeredX, minimumX, maximumX);
		int y = (int) requestedY;
		List<Rectangle> safeBlockers = blockers == null ? Collections.emptyList() : blockers;

		List<Integer> candidates = new ArrayList<>();
		addCandidate(candidates, baseX, minimumX, maximumX);
		for (Rectangle blocker : safeBlockers)
		{
			if (blocker == null)
			{
				continue;
			}
			addCandidate(
				candidates,
				(long) blocker.x - BUTTON_SIZE - BLOCKER_HORIZONTAL_CLEARANCE,
				minimumX,
				maximumX);
			addCandidate(
				candidates,
				(long) blocker.x + blocker.width + BLOCKER_HORIZONTAL_CLEARANCE
					+ FINAL_LEFT_CALIBRATION,
				minimumX,
				maximumX);
		}
		addCandidate(candidates, minimumX, minimumX, maximumX);
		addCandidate(candidates, maximumX, minimumX, maximumX);

		candidates.sort((left, right) ->
		{
			long leftDistance = Math.abs((long) left - baseX);
			long rightDistance = Math.abs((long) right - baseX);
			int distanceOrder = Long.compare(leftDistance, rightDistance);
			if (distanceOrder != 0)
			{
				return distanceOrder;
			}
			return Integer.compare(left, right);
		});

		for (int candidateX : candidates)
		{
			Point candidate = availableCandidate(candidateX, y, safeViewport, safeBlockers);
			if (candidate != null)
			{
				return applyFinalLeftCalibration(candidate, safeViewport);
			}
		}

		return null;
	}

	private static Point applyFinalLeftCalibration(
		Point selectedCandidate,
		Rectangle safeViewport)
	{
		int minimumX = safeViewport.x;
		int maximumX = safeViewport.x + safeViewport.width - BUTTON_SIZE;
		int nudgedX = clamp(
			(long) selectedCandidate.x - FINAL_LEFT_CALIBRATION,
			minimumX,
			maximumX);
		// Blocker bounds may include transparent padding, so apply the visual offset
		// after side selection and clamp it to the viewport.
		return new Point(nudgedX, selectedCandidate.y);
	}

	private static void addCandidate(
		List<Integer> candidates,
		long candidate,
		int minimumX,
		int maximumX)
	{
		if (candidate < minimumX || candidate > maximumX)
		{
			return;
		}
		int x = (int) candidate;
		if (!candidates.contains(x))
		{
			candidates.add(x);
		}
	}

	static Point clampLocation(Point requestedLocation, Rectangle viewport)
	{
		Objects.requireNonNull(requestedLocation, "requestedLocation");
		Objects.requireNonNull(viewport, "viewport");
		Rectangle safeViewport = safeViewport(viewport);
		if (!canContainButton(safeViewport))
		{
			return null;
		}

		int maximumX = safeViewport.x + safeViewport.width - BUTTON_SIZE;
		int maximumY = safeViewport.y + safeViewport.height - BUTTON_SIZE;
		return new Point(
			clamp(requestedLocation.x, safeViewport.x, maximumX),
			clamp(requestedLocation.y, safeViewport.y, maximumY));
	}

	static boolean contains(Point location, Point pointer)
	{
		Objects.requireNonNull(location, "location");
		Objects.requireNonNull(pointer, "pointer");
		return new Ellipse2D.Double(
			location.x,
			location.y,
			BUTTON_SIZE,
			BUTTON_SIZE).contains(pointer);
	}

	private static Point availableCandidate(
		int x,
		int y,
		Rectangle safeViewport,
		List<Rectangle> blockers)
	{
		Rectangle candidate = new Rectangle(x, y, BUTTON_SIZE, BUTTON_SIZE);
		if (!safeViewport.contains(candidate) || intersectsAny(candidate, blockers))
		{
			return null;
		}
		return candidate.getLocation();
	}

	private static boolean intersectsAny(Rectangle candidate, List<Rectangle> blockers)
	{
		for (Rectangle blocker : blockers)
		{
			if (blocker != null && candidate.intersects(blocker))
			{
				return true;
			}
		}
		return false;
	}

	private static boolean canContainButton(Rectangle viewport)
	{
		return viewport.width >= BUTTON_SIZE && viewport.height >= BUTTON_SIZE;
	}

	private static int clamp(long value, int minimum, int maximum)
	{
		return (int) Math.max(minimum, Math.min((long) maximum, value));
	}

}
