/*
 * Copyright (c) 2023, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.runeliteobjects.extendedruneliteobjects;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Model;
import net.runelite.api.Point;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.ui.overlay.OverlayUtil;

// This will hold all RuneliteObjects
// Can group up to helpers to have specific helpers remove all things themselves
// Can also when closing quest helper remove all
@Slf4j
@Singleton
public class RLObjectManager
{
	protected final Client client;
	protected final EventBus eventBus;
	protected final ChatboxPanelManager chatboxPanelManager;
	protected final ClientThread clientThread;
	protected final ChatMessageManager chatMessageManager;
	protected final SpriteManager spriteManager;

	// Red Click
	Point clickPos;
	int redClickAnimationFrame = 0;
	int bufferRedClickAnimation = 0;
	int[] redClick = new int[]{SpriteID.RED_CLICK_ANIMATION_1, SpriteID.RED_CLICK_ANIMATION_2, SpriteID.RED_CLICK_ANIMATION_3, SpriteID.RED_CLICK_ANIMATION_4};
	final int ANIMATION_PERIOD = 5;

	NewFakeNpc runeliteNpc;

	private static final List<MenuAction> OBJECT_MENU_TYPES = ImmutableList.of(
		MenuAction.GAME_OBJECT_FIRST_OPTION,
		MenuAction.GAME_OBJECT_SECOND_OPTION,
		MenuAction.GAME_OBJECT_THIRD_OPTION,
		MenuAction.GAME_OBJECT_FOURTH_OPTION,
		MenuAction.GAME_OBJECT_FIFTH_OPTION,
		MenuAction.WIDGET_TARGET_ON_GAME_OBJECT
	);

	private static final List<MenuAction> GROUP_ITEM_MENU_TYPES = ImmutableList.of(
		MenuAction.GROUND_ITEM_FIRST_OPTION,
		MenuAction.GROUND_ITEM_SECOND_OPTION,
		MenuAction.GROUND_ITEM_THIRD_OPTION,
		MenuAction.GROUND_ITEM_FOURTH_OPTION,
		MenuAction.GROUND_ITEM_FIFTH_OPTION
	);

	@Inject
	public RLObjectManager(Client client, EventBus eventBus, ChatboxPanelManager chatboxPanelManager, ClientThread clientThread, ChatMessageManager chatMessageManager, SpriteManager spriteManager)
	{
		this.client = client;
		this.eventBus = eventBus;
		this.chatboxPanelManager = chatboxPanelManager;
		this.clientThread = clientThread;
		this.chatMessageManager = chatMessageManager;
		this.spriteManager = spriteManager;
	}

	public void startUp(Model model, WorldPoint wp, int animation)
	{
		createFakeNpc(model, wp, animation);
	}

	public void shutDown()
	{
		// Need to close dialogs
		if (chatboxPanelManager.getCurrentInput() instanceof ChatBox) chatboxPanelManager.close();

		clientThread.invokeLater(this::removeRuneliteObject);
	}

	private void removeRuneliteObject()
	{
		runeliteNpc.setVisible(false);
	}

	public void createFakeNpc(Model model, WorldPoint wp, int animation)
	{
		runeliteNpc = new NewFakeNpc(client, clientThread, wp, model, animation);
		// Should this be here or a separate 'activate' step?
		runeliteNpc.setVisible(true);
	}

	// Get Menu Entry, wait for it to be the target NPC
	// Once it is, save the options, and be ready to add them to RuneliteNpc
	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		setupMenuOptions(runeliteNpc, event);
	}

	private void setupMenuOptions(NewFakeNpc extendedRuneliteObject, MenuEntryAdded event)
	{
		if (!extendedRuneliteObject.isRuneliteObjectActive()) return;

		LocalPoint lp = extendedRuneliteObject.getRuneliteObject().getLocation();

		int widgetIndex = event.getActionParam0();
		int widgetID = event.getActionParam1();
		MenuEntry[] menuEntries = client.getMenuEntries();

		if (extendedRuneliteObject.isRuneliteObjectActive()
			&& extendedRuneliteObject.getRuneliteObject() != null
			&& extendedRuneliteObject.getRuneliteObject().getModel() != null)
		{
			if (!isMouseOverObject(extendedRuneliteObject)) return;
			if (event.getOption().equals("Walk here") && isMouseOverObject(extendedRuneliteObject))
			{
				addTalkAction(extendedRuneliteObject, widgetIndex, widgetID);
			}

			if (lp == null)
			{
				return;
			}

			boolean isHighPriorityOnTile = false;
			for (MenuEntry menuEntry : menuEntries)
			{
				if (menuEntry.getType() == MenuAction.RUNELITE_HIGH_PRIORITY)
				{
					isHighPriorityOnTile = true;
				}
			}
			if (!isHighPriorityOnTile) return;

			if (OBJECT_MENU_TYPES.contains(event.getMenuEntry().getType()))
			{
				updatePriorities(event, event.getActionParam0(), event.getActionParam1(), menuEntries, lp, false);
			}

			if (GROUP_ITEM_MENU_TYPES.contains(event.getMenuEntry().getType()))
			{
				updatePriorities(event, event.getActionParam0(), event.getActionParam1(), menuEntries, lp, true);
			}

			Actor actor = event.getMenuEntry().getActor();
			if (actor != null)
			{
				LocalPoint actorLp = actor.getLocalLocation();
				updatePriorities(event, actorLp.getSceneX(), actorLp.getSceneY(), menuEntries, lp, false);
			}
		}
	}

	private void updatePriorities(MenuEntryAdded event, int currentItemX, int currentItemY, MenuEntry[] menuEntries, LocalPoint runeliteObjectLp, boolean shouldPrioritizeObject)
	{
		int cameraX = client.getCameraX();
		int cameraY = client.getCameraY();
		int playerX = client.getLocalPlayer().getLocalLocation().getX();
		int playerY = client.getLocalPlayer().getLocalLocation().getY();

		int shiftToBeEqualOr = 0;
		if (shouldPrioritizeObject) shiftToBeEqualOr = 1;

		// If on tile replace, want to include
		if ((cameraX - playerX + 15 > 0 && currentItemX < runeliteObjectLp.getSceneX() + shiftToBeEqualOr) // Facing west
			|| (cameraX - playerX + 15 < 0 && currentItemX > runeliteObjectLp.getSceneX() - shiftToBeEqualOr) // Facing east
			|| (cameraY - playerY - 15 > 0 && currentItemY < runeliteObjectLp.getSceneY() + shiftToBeEqualOr) // Facing south
			|| (cameraY - playerY - 15 < 0 && currentItemY > runeliteObjectLp.getSceneY() - shiftToBeEqualOr) // Facing north
		)
		{
			event.getMenuEntry().setDeprioritized(true);
			for (MenuEntry menuEntry : menuEntries)
			{
				if (menuEntry.getType() == MenuAction.RUNELITE_HIGH_PRIORITY)
				{
					menuEntry.setDeprioritized(false);
				}
			}
		}
	}

	public void createChatboxMessage(String text)
	{
		String chatMessage = new ChatMessageBuilder()
			.append(ChatColorType.NORMAL)
			.append(text)
			.build();

		chatMessageManager.queue(QueuedMessage.builder()
			.type(ChatMessageType.NPC_EXAMINE)
			.runeLiteFormattedMessage(chatMessage)
			.timestamp((int) (System.currentTimeMillis() / 1000))
			.build());
	}

	public Consumer<MenuEntry> getExamineAction(NewFakeNpc fakeNpc)
	{
		return menuEntry -> {
			createChatboxMessage(fakeNpc.getExamine());
		};
	}

	public Consumer<MenuEntry> getTalkAction(NewFakeNpc fakeNpc)
	{
		return menuEntry -> {
			WorldPoint wp = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());

			if (fakeNpc.isNeedToBeCloseToTalk() && wp.distanceTo(fakeNpc.getWorldPoint()) > ExtendedRuneliteObject.MAX_TALK_DISTANCE)
			{
				createChatboxMessage("You'll need to move closer to them to talk!");
				return;
			}

			locationOfPlayerInteraction = client.getLocalPlayer().getLocalLocation();

			// Set to rotate towards player
			fakeNpc.setOrientationGoalAsPlayer(client);
			fakeNpc.setupChatBox(chatboxPanelManager);
		};
	}

	private void addTalkAction(NewFakeNpc extendedRuneliteObject, int widgetIndex, int widgetID)
	{
		MenuEntry[] menuEntries = client.getMenuEntries();
		menuEntries = Arrays.copyOf(menuEntries, menuEntries.length + 1);

		client.createMenuEntry(-2)
			.setOption("Talk-to")
			.setTarget("<col=" + extendedRuneliteObject.getNameColor() + ">" + extendedRuneliteObject.getName() + "</col>")
			.setType(MenuAction.RUNELITE_HIGH_PRIORITY)
			.setDeprioritized(false)
			.onClick(menuEntry -> {

				resetRedClick();
				extendedRuneliteObject.getTalkAction().activate();
			})
			.setParam0(widgetIndex)
			.setParam1(widgetID);
	}

	private void resetRedClick()
	{
		clickPos = client.getMouseCanvasPosition();
		redClickAnimationFrame = 0;
		bufferRedClickAnimation = 0;
	}

	private boolean isMouseOverObject(NewFakeNpc extendedRuneliteObject)
	{
		Shape clickbox = extendedRuneliteObject.getClickbox();

		if (clickbox == null) return false;
		Point p = client.getMouseCanvasPosition();

		return clickbox.contains(p.getX(), p.getY());
	}

	private LocalPoint locationOfPlayerInteraction;

	public void makeWidgetOverlayHint(Graphics2D graphics)
	{
		renderRedClick(graphics);

		if (runeliteNpc != null
			&& locationOfPlayerInteraction != null
			&& client.getLocalPlayer().getLocalLocation().distanceTo(locationOfPlayerInteraction) > 0
			&& chatboxPanelManager.getCurrentInput() instanceof ChatBox)
		{
			chatboxPanelManager.close();
		}
	}

	private void renderRedClick(Graphics2D graphics)
	{
		if (clickPos != null && redClickAnimationFrame < 4)
		{
			BufferedImage img = spriteManager.getSprite(redClick[redClickAnimationFrame], 0);
			if (img == null) return;
			Point point = new Point(clickPos.getX() - (img.getWidth() / 2), clickPos.getY() - (img.getHeight() / 2));
			OverlayUtil.renderImageLocation(graphics, point, img);
		}
		else
		{
			clickPos = null;
			redClickAnimationFrame = 0;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		// TODO: This needs to be more generic, for checking what action is 'pending'. Currently this is used just to keep to a tick system for dialog loading
		if (runeliteNpc != null)
		{
			if (runeliteNpc.getTalkAction() != null)
			{
				runeliteNpc.progressDialog();
			}
		}
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		bufferRedClickAnimation = Math.floorMod(bufferRedClickAnimation + 1, ANIMATION_PERIOD);
		if (bufferRedClickAnimation == 0)
		{
			redClickAnimationFrame++;
		}


		runeliteNpc.actionOnClientTick();

		if (runeliteNpc.getOrientationGoal() != runeliteNpc.getRuneliteObject().getOrientation())
		{
			runeliteNpc.partiallyRotateToGoal();
		}
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged event)
	{
		if (Arrays.stream(new GameState[] { GameState.LOGGED_IN, GameState.LOADING })
			.anyMatch((state) -> state == event.getGameState()))
		{
			runeliteNpc.render();
		}
	}
}
