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
import java.util.concurrent.atomic.AtomicBoolean;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.overlay.OverlayManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PanelControllerTest
{
	private static final Rectangle VIEWPORT = new Rectangle(0, 0, 1_400, 1_000);

	@Test
	void firstRenderUsesThe790By490DefaultInsteadOfTheViewportSize()
	{
		PanelController controller = new PanelController(
			mock(Client.class),
			mock(ConfigManager.class),
			mock(QuestHelperConfig.class),
			mock(OverlayManager.class));

		QuestJournalManager.JournalPanelRenderState state =
			controller.getRenderState(VIEWPORT);

		assertEquals(new Rectangle(305, 255, 790, 490), state.bounds());
		assertTrue(JournalGeometry.create(state.bounds(), VIEWPORT).hasDetailPane());
	}

	@Test
	void resizingPreservesWhereTheHandleWasGrabbed()
	{
		PanelController controller = new PanelController(
			mock(Client.class),
			mock(ConfigManager.class),
			mock(QuestHelperConfig.class),
			mock(OverlayManager.class));
		Rectangle panel = new Rectangle(100, 80, 680, 420);
		JournalGeometry geometry = JournalGeometry.create(panel, VIEWPORT);
		Point pressed = new Point(772, 494);
		JournalOverlay overlay = mock(JournalOverlay.class);

		controller.beginResize(overlay, geometry, pressed);
		controller.resizeTo(
			new Point(pressed.x + 1, pressed.y + 1),
			VIEWPORT);

		assertEquals(
			new Rectangle(100, 80, 681, 421),
			controller.getRenderState(VIEWPORT).bounds());
	}

	@Test
	void boundsMutationsRejectEveryOlderRenderState()
	{
		PanelController controller = new PanelController(
			mock(Client.class),
			mock(ConfigManager.class),
			mock(QuestHelperConfig.class),
			mock(OverlayManager.class));
		QuestJournalManager.JournalPanelRenderState initial =
			controller.getRenderState(VIEWPORT);

		controller.dragTo(new Point(140, 120), new Point(10, 10), VIEWPORT);
		QuestJournalManager.JournalPanelRenderState dragged =
			controller.getRenderState(VIEWPORT);
		assertNotEquals(initial.bounds(), dragged.bounds());
		assertRejected(controller, initial.revision());

		Rectangle draggedBounds = dragged.bounds();
		controller.resizeTo(
			new Point(
				draggedBounds.x + draggedBounds.width + 20,
				draggedBounds.y + draggedBounds.height + 20),
			VIEWPORT);
		QuestJournalManager.JournalPanelRenderState resized =
			controller.getRenderState(VIEWPORT);
		assertNotEquals(dragged.bounds(), resized.bounds());
		assertRejected(controller, dragged.revision());

		JournalOverlay overlay = mock(JournalOverlay.class);
		Rectangle adoptedBounds = new Rectangle(180, 140, 700, 440);
		when(overlay.getPanelBounds()).thenReturn(adoptedBounds);
		controller.adoptManagedBounds(overlay, null);
		QuestJournalManager.JournalPanelRenderState adopted =
			controller.getRenderState(VIEWPORT);
		assertRejected(controller, resized.revision());
		assertTrue(controller.commitRender(adopted.revision(), () -> { }));
	}

	private static void assertRejected(
		PanelController controller,
		long revision)
	{
		AtomicBoolean ran = new AtomicBoolean();
		assertFalse(controller.commitRender(revision, () -> ran.set(true)));
		assertFalse(ran.get());
	}
}
