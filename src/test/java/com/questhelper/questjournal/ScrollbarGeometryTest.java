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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScrollbarGeometryTest
{
	@Test
	public void thumbTracksTheScrollRangeFromTopToBottom()
	{
		Rectangle content = new Rectangle(20, 30, 200, 100);

		ScrollbarGeometry top = ScrollbarGeometry.create(content, 400, 0, 300);
		ScrollbarGeometry middle = ScrollbarGeometry.create(content, 400, 150, 300);
		ScrollbarGeometry bottom = ScrollbarGeometry.create(content, 400, 300, 300);

		assertEquals(new Rectangle(204, 30, 16, 100), top.trackBounds());
		assertEquals(new Rectangle(204, 30, 16, 25), top.thumbBounds());
		assertEquals(new Rectangle(206, 30, 14, 100), top.visualTrackBounds());
		assertEquals(new Rectangle(206, 30, 14, 25), top.visualThumbBounds());
		assertEquals(67, middle.thumbBounds().y);
		assertEquals(105, bottom.thumbBounds().y);
	}

	@Test
	public void draggingPreservesThePointerOffsetAndClampsAtBothEnds()
	{
		ScrollbarGeometry scrollbar = ScrollbarGeometry.create(
			new Rectangle(20, 30, 200, 100),
			400,
			150,
			300);
		Point press = new Point(215, scrollbar.thumbBounds().y + 7);
		int pointerOffset = scrollbar.pointerOffset(press);

		assertEquals(7, pointerOffset);
		assertEquals(0, scrollbar.offsetForPointer(Integer.MIN_VALUE, pointerOffset));
		assertEquals(152, scrollbar.offsetForPointer(75, pointerOffset));
		assertEquals(300, scrollbar.offsetForPointer(Integer.MAX_VALUE, pointerOffset));
	}

	@Test
	public void trackPressUsesTheThumbCenterAsItsDragAnchor()
	{
		ScrollbarGeometry scrollbar = ScrollbarGeometry.create(
			new Rectangle(20, 30, 200, 100),
			400,
			0,
			300);
		Point belowThumb = new Point(215, 90);

		assertTrue(scrollbar.contains(belowThumb));
		assertFalse(scrollbar.thumbContains(belowThumb));
		assertEquals(scrollbar.thumbBounds().height / 2, scrollbar.pointerOffset(belowThumb));
		assertEquals(192, scrollbar.offsetForPointer(belowThumb.y, scrollbar.pointerOffset(belowThumb)));
	}

	@Test
	public void noOverflowHidesTheScrollbarAndItsHitArea()
	{
		ScrollbarGeometry scrollbar = ScrollbarGeometry.create(
			new Rectangle(20, 30, 200, 100),
			100,
			0,
			0);

		assertFalse(scrollbar.isVisible());
		assertFalse(scrollbar.contains(new Point(219, 50)));
		assertFalse(scrollbar.thumbContains(new Point(219, 50)));
		assertEquals(new Rectangle(), scrollbar.trackBounds());
		assertEquals(new Rectangle(), scrollbar.thumbBounds());
		assertEquals(new Rectangle(), scrollbar.visualTrackBounds());
		assertEquals(new Rectangle(), scrollbar.visualThumbBounds());
		assertEquals(0, scrollbar.offsetForPointer(Integer.MIN_VALUE, 0));
		assertEquals(0, scrollbar.offsetForPointer(Integer.MAX_VALUE, 99));
	}

	@Test
	public void emptyContentDoesNotPublishScrollbarGeometry()
	{
		ScrollbarGeometry scrollbar = ScrollbarGeometry.create(
			new Rectangle(20, 30, 200, 100),
			0,
			27,
			0);

		assertFalse(scrollbar.isVisible());
		assertEquals(new Rectangle(), scrollbar.trackBounds());
		assertEquals(new Rectangle(), scrollbar.thumbBounds());
		assertEquals(0, scrollbar.offsetForPointer(80, 20));
	}

	@Test
	public void contentHeightStillGuardsAgainstAnInconsistentPositiveRange()
	{
		ScrollbarGeometry scrollbar = ScrollbarGeometry.create(
			new Rectangle(20, 30, 200, 100),
			100,
			0,
			25);

		assertFalse(scrollbar.isVisible());
		assertFalse(scrollbar.contains(new Point(219, 50)));
	}
}
