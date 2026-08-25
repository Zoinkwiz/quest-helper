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
import java.util.Objects;

final class ScrollbarGeometry
{
	private static final int VISUAL_WIDTH = 14;
	private static final int HIT_WIDTH = 16;
	private static final int MINIMUM_THUMB_HEIGHT = 18;
	private static final ScrollbarGeometry EMPTY = new ScrollbarGeometry(
		new Rectangle(),
		new Rectangle(),
		new Rectangle(),
		new Rectangle(),
		0);

	private final Rectangle trackBounds;
	private final Rectangle thumbBounds;
	private final Rectangle visualTrackBounds;
	private final Rectangle visualThumbBounds;
	private final int maximumOffset;

	private ScrollbarGeometry(
		Rectangle trackBounds,
		Rectangle thumbBounds,
		Rectangle visualTrackBounds,
		Rectangle visualThumbBounds,
		int maximumOffset)
	{
		this.trackBounds = new Rectangle(trackBounds);
		this.thumbBounds = new Rectangle(thumbBounds);
		this.visualTrackBounds = new Rectangle(visualTrackBounds);
		this.visualThumbBounds = new Rectangle(visualThumbBounds);
		this.maximumOffset = maximumOffset;
	}

	static ScrollbarGeometry create(
		Rectangle contentBounds,
		int totalHeight,
		int offset,
		int maximumOffset)
	{
		Objects.requireNonNull(contentBounds, "contentBounds");
		int clampedMaximumOffset = Math.max(0, maximumOffset);
		if (contentBounds.width <= 0
			|| contentBounds.height <= 0
			|| totalHeight <= contentBounds.height
			|| clampedMaximumOffset == 0)
		{
			return EMPTY;
		}

		int visualWidth = Math.min(VISUAL_WIDTH, contentBounds.width);
		int hitWidth = Math.min(HIT_WIDTH, contentBounds.width);
		int visualX = contentBounds.x + contentBounds.width - visualWidth;
		int hitX = contentBounds.x + contentBounds.width - hitWidth;
		int thumbHeight = (int) Math.min(
			contentBounds.height,
			Math.max(
				MINIMUM_THUMB_HEIGHT,
				(long) contentBounds.height * contentBounds.height
					/ Math.max(contentBounds.height, totalHeight)));
		int travel = Math.max(0, contentBounds.height - thumbHeight);
		int clampedOffset = clamp(offset, 0, clampedMaximumOffset);
		int thumbY = contentBounds.y
			+ (int) ((long) travel * clampedOffset / clampedMaximumOffset);

		return new ScrollbarGeometry(
			new Rectangle(hitX, contentBounds.y, hitWidth, contentBounds.height),
			new Rectangle(hitX, thumbY, hitWidth, thumbHeight),
			new Rectangle(visualX, contentBounds.y, visualWidth, contentBounds.height),
			new Rectangle(visualX, thumbY, visualWidth, thumbHeight),
			clampedMaximumOffset);
	}

	boolean isVisible()
	{
		return !trackBounds.isEmpty() && !thumbBounds.isEmpty();
	}

	boolean contains(Point point)
	{
		return point != null && isVisible() && trackBounds.contains(point);
	}

	boolean thumbContains(Point point)
	{
		return point != null && isVisible() && thumbBounds.contains(point);
	}

	int pointerOffset(Point point)
	{
		if (thumbContains(point))
		{
			return point.y - thumbBounds.y;
		}
		return thumbBounds.height / 2;
	}

	int offsetForPointer(int pointerY, int pointerOffset)
	{
		if (!isVisible() || maximumOffset <= 0)
		{
			return 0;
		}

		int travel = trackBounds.height - thumbBounds.height;
		if (travel <= 0)
		{
			return 0;
		}
		int requestedThumbY = saturatingSubtract(pointerY, pointerOffset);
		int thumbY = clamp(
			requestedThumbY,
			trackBounds.y,
			trackBounds.y + travel);
		return (int) Math.min(
			maximumOffset,
			Math.max(0L, (long) (thumbY - trackBounds.y) * maximumOffset / travel));
	}

	Rectangle trackBounds()
	{
		return new Rectangle(trackBounds);
	}

	Rectangle thumbBounds()
	{
		return new Rectangle(thumbBounds);
	}

	Rectangle visualTrackBounds()
	{
		return new Rectangle(visualTrackBounds);
	}

	Rectangle visualThumbBounds()
	{
		return new Rectangle(visualThumbBounds);
	}

	private static int clamp(int value, int minimum, int maximum)
	{
		return Math.max(minimum, Math.min(maximum, value));
	}

	private static int saturatingSubtract(int left, int right)
	{
		long value = (long) left - right;
		return (int) Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, value));
	}
}
