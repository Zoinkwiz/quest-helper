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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LauncherOverlayPlacementTest
{
	@Test
	public void adjacentWidgetBoundsCannotCancelDefaultOrResetCorrection()
	{
		Dimension viewport = new Dimension(900, 600);
		Rectangle minimap = new Rectangle(600, 20, 160, 160);
		Map<Integer, Widget> widgets = new HashMap<>();
		widgets.put(InterfaceID.Toplevel.MINIMAP, widget(minimap));
		// occupiedBounds grows widgets by three pixels. The grown edge touches the
		// uncorrected launcher x=629 and previously caused the horizontal correction
		// to be discarded.
		widgets.put(
			InterfaceID.Orbs.ORB_WORLDMAP,
			widget(new Rectangle(620, 179, 6, 28)));
		SpriteManager sprites = spriteManager();
		LauncherOverlay overlay = new LauncherOverlay(
			client(viewport, widgets),
			sprites,
			mock(TooltipManager.class),
			null,
			manager());

		render(overlay, viewport);
		verify(sprites).getSprite(SpriteID.SideIcons.QUEST, 0);
		verify(sprites).getSprite(SpriteID.OrbFiller.RUN, 0);
		verify(sprites, never()).getSprite(SpriteID.AchievementDiaryIcons.BLUE_QUESTS, 0);
		assertEquals(new Point(624, 177), overlay.getBounds().getLocation());
		assertEquals(
			new Dimension(LauncherGeometry.BUTTON_SIZE, LauncherGeometry.BUTTON_SIZE),
			overlay.getBounds().getSize());

		overlay.setPreferredLocation(new Point(700, 300));
		overlay.getBounds().setLocation(700, 300);
		render(overlay, viewport);
		assertEquals(new Point(700, 300), overlay.getBounds().getLocation());

		applyRuneLiteResetState(overlay);
		render(overlay, viewport);
		assertEquals(new Point(624, 177), overlay.getBounds().getLocation());
	}

	@Test
	public void defaultsAndRuneLiteAltResetUseTheCalibratedFixedLayoutPlacement()
	{
		Dimension viewport = new Dimension(765, 503);
		Rectangle minimap = new Rectangle(548, 4, 200, 160);
		Rectangle fixedTabs = new Rectangle(522, 168, 243, 36);
		Map<Integer, Widget> widgets = new HashMap<>();
		widgets.put(InterfaceID.Toplevel.MINIMAP, widget(minimap));
		widgets.put(InterfaceID.Toplevel.SIDE_TOP, widget(fixedTabs));
		LauncherOverlay overlay = new LauncherOverlay(
			client(viewport, widgets),
			spriteManager(),
			mock(TooltipManager.class),
			null,
			manager());

		render(overlay, viewport);
		Point preCorrectionDefault = new Point(469, 161);
		Point calibratedDefault = new Point(
			preCorrectionDefault.x - LauncherGeometry.FINAL_LEFT_CALIBRATION,
			preCorrectionDefault.y);
		assertEquals(new Point(464, 161), calibratedDefault);
		assertEquals(calibratedDefault, overlay.getBounds().getLocation());

		Point manuallyMoved = new Point(600, 250);
		overlay.setPreferredLocation(manuallyMoved);
		overlay.getBounds().setLocation(manuallyMoved);
		render(overlay, viewport);
		assertEquals(manuallyMoved, overlay.getBounds().getLocation());

		applyRuneLiteResetState(overlay);
		assertNull(overlay.getPreferredLocation());
		render(overlay, viewport);
		assertEquals(calibratedDefault, overlay.getBounds().getLocation());
	}

	@Test
	public void automaticPlacementScansWidgetsOnlyOncePerClientTick()
	{
		Dimension viewport = new Dimension(765, 503);
		Map<Integer, Widget> widgets = new HashMap<>();
		widgets.put(
			InterfaceID.Toplevel.MINIMAP,
			widget(new Rectangle(548, 4, 200, 160)));
		Client client = client(viewport, widgets);
		when(client.getTickCount()).thenReturn(10, 10, 11);
		LauncherOverlay overlay = new LauncherOverlay(
			client,
			spriteManager(),
			mock(TooltipManager.class),
			null,
			manager());

		render(overlay, viewport);
		render(overlay, viewport);
		render(overlay, viewport);

		verify(client, times(2)).getWidget(InterfaceID.Toplevel.MINIMAP);
	}

	@Test
	public void publishedLauncherHitStateDoesNotReadClientState()
	{
		Dimension viewport = new Dimension(900, 600);
		Map<Integer, Widget> widgets = new HashMap<>();
		widgets.put(
			InterfaceID.Toplevel.MINIMAP,
			widget(new Rectangle(600, 20, 160, 160)));
		Client client = client(viewport, widgets);
		LauncherOverlay overlay = new LauncherOverlay(
			client,
			spriteManager(),
			mock(TooltipManager.class),
			null,
			manager());
		clearInvocations(client);
		render(overlay, viewport);
		verify(client, never()).isMenuOpen();
		Point center = new Point(
			overlay.getBounds().x + overlay.getBounds().width / 2,
			overlay.getBounds().y + overlay.getBounds().height / 2);
		clearInvocations(client);

		assertTrue(overlay.isRendered());
		assertTrue(overlay.contains(center));
		verify(client, never()).getGameState();
		verify(client, never()).isMenuOpen();
	}

	private static void render(LauncherOverlay overlay, Dimension viewport)
	{
		BufferedImage canvas = new BufferedImage(
			viewport.width,
			viewport.height,
			BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = canvas.createGraphics();
		try
		{
			// Mirror OverlayRenderer.safeRender(): RuneLite first translates to the
			// location from the previous bounds/preference, then lets render update
			// the absolute bounds, and finally applies the returned size.
			Point rendererLocation = overlay.getPreferredLocation() == null
				? overlay.getBounds().getLocation()
				: new Point(overlay.getPreferredLocation());
			graphics.translate(rendererLocation.x, rendererLocation.y);
			overlay.getBounds().setLocation(rendererLocation);
			Dimension renderedSize = overlay.render(graphics);
			if (renderedSize == null)
			{
				overlay.getBounds().setSize(0, 0);
			}
			else
			{
				overlay.getBounds().setSize(renderedSize);
			}
		}
		finally
		{
			graphics.dispose();
		}
	}

	private static void applyRuneLiteResetState(LauncherOverlay overlay)
	{
		overlay.setPreferredPosition(null);
		overlay.setPreferredSize(null);
		overlay.setPreferredLocation(null);
	}

	private static SpriteManager spriteManager()
	{
		SpriteManager spriteManager = mock(SpriteManager.class);
		when(spriteManager.getSprite(SpriteID.TliButton01Orb01_34x34._0, 0))
			.thenReturn(new BufferedImage(34, 34, BufferedImage.TYPE_INT_ARGB));
		when(spriteManager.getSprite(SpriteID.SideIcons.QUEST, 0))
			.thenReturn(new BufferedImage(18, 18, BufferedImage.TYPE_INT_ARGB));
		when(spriteManager.getSprite(SpriteID.OrbFiller.RUN, 0))
			.thenReturn(new BufferedImage(26, 26, BufferedImage.TYPE_INT_ARGB));
		return spriteManager;
	}

	private static QuestJournalManager manager()
	{
		QuestJournalManager manager = mock(QuestJournalManager.class);
		when(manager.getPointerCanvasPoint()).thenReturn(new Point(-1, -1));
		return manager;
	}

	private static Client client(Dimension viewport, Map<Integer, Widget> widgets)
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		when(client.getRealDimensions()).thenReturn(new Dimension(viewport));
		when(client.getWidget(anyInt())).thenAnswer(
			invocation -> widgets.get(invocation.getArgument(0)));
		return client;
	}

	private static Widget widget(Rectangle bounds)
	{
		Widget widget = mock(Widget.class);
		when(widget.getBounds()).thenReturn(new Rectangle(bounds));
		return widget;
	}
}
