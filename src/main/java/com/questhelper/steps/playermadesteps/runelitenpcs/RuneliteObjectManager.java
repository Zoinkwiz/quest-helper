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
package com.questhelper.steps.playermadesteps.runelitenpcs;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.questhelper.questhelpers.QuestHelper;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
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
public class RuneliteObjectManager
{
	protected final Client client;
	protected final EventBus eventBus;
	protected final ChatboxPanelManager chatboxPanelManager;
	protected final ClientThread clientThread;
	protected final ChatMessageManager chatMessageManager;
	protected final SpriteManager spriteManager;

	protected final List<RuneliteNpcs> npcGroups = new ArrayList<>();

	// Red Click
	Point clickPos;
	int clickAnimationFrame = 0;
	int bufferAnimation = 0;
	int[] redClick = new int[]{SpriteID.RED_CLICK_ANIMATION_1, SpriteID.RED_CLICK_ANIMATION_2, SpriteID.RED_CLICK_ANIMATION_3, SpriteID.RED_CLICK_ANIMATION_4};
	final int ANIMATION_PERIOD = 5;

	// Hiding NPCs
	// TODO: Need to be per object
	boolean playerBeenOnTile = false;
	boolean npcBeenOnTile = false;

	RuneliteNpc lastInteractedWithNpc;

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

	List<RuneliteNpc> rotatingObjectsToPlayer = new ArrayList<>();

	@Inject
	public RuneliteObjectManager(Client client, EventBus eventBus, ChatboxPanelManager chatboxPanelManager, ClientThread clientThread, ChatMessageManager chatMessageManager, SpriteManager spriteManager)
	{
		this.client = client;
		this.eventBus = eventBus;
		this.chatboxPanelManager = chatboxPanelManager;
		this.clientThread = clientThread;
		this.chatMessageManager = chatMessageManager;
		this.spriteManager = spriteManager;
	}

	public void startUp()
	{

	}

	public void shutDown()
	{
		// Need to close dialogs
		if (chatboxPanelManager.getCurrentInput() instanceof ChatBox) chatboxPanelManager.close();

		clientThread.invokeLater(this::removeRuneliteNpcs);
	}

	// Need distinction between REMOVE and DISABLE

	private void removeRuneliteNpcs()
	{
		for (RuneliteNpcs npcGroup : npcGroups)
		{
			npcGroup.removeAll(this);
		}
		npcGroups.clear();
	}

	private void disableRuneliteNpcs()
	{
		for (RuneliteNpcs npcGroup : npcGroups)
		{
			npcGroup.disableAll(this);
		}
	}

	public RuneliteNpc createRuneliteNpc(String group, int[] model, WorldPoint wp, int animation)
	{
		RuneliteNpc runeliteNpc = new RuneliteNpc(client, clientThread, wp, model, animation);
		// Should this be here or a separate 'activate' step?
		runeliteNpc.activate();

		boolean foundGroup = false;
		for (RuneliteNpcs npcGroup : npcGroups)
		{
			if (npcGroup.getGroupName().equals(group))
			{
				foundGroup = true;
				npcGroup.addNpc(runeliteNpc);
			}
		}
		if(!foundGroup)
		{
			npcGroups.add(new RuneliteNpcs(group, runeliteNpc));
		}

		return runeliteNpc;
	}

	public RuneliteNpc createRuneliteNpc(RuneliteNpcs group, int[] model, WorldPoint wp, int animation)
	{
		RuneliteNpc runeliteNpc = new RuneliteNpc(client, clientThread, wp, model, animation);
		// Should this be here or a separate 'activate' step?
		runeliteNpc.activate();

		group.addNpc(runeliteNpc);
		return runeliteNpc;
	}

	public RuneliteNpc createRuneliteNpc(QuestHelper questHelper, int[] model, WorldPoint wp, int animation)
	{
		String groupID = questHelper.toString();
		return createRuneliteNpc(groupID, model, wp, animation);
	}

	public void removeRuneliteNpc(String groupID, RuneliteNpc npc)
	{
		if (npc == null)
		{
			throw new IllegalStateException("Attempted to remove null RuneliteObject from Manager");
		}
		npc.remove();

		RuneliteNpcs groupNpcs = npcGroups.stream().filter(s -> s.getGroupName().equals(groupID)).findAny().orElse(null);

		if (groupNpcs == null)
		{
			throw new IllegalStateException("Attempted to remove non-added RuneliteNpcs " + groupID + "from Manager");
		}

		groupNpcs.remove(npc);
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		for (RuneliteNpcs npcGroup : npcGroups)
		{
			for (RuneliteNpc npc : npcGroup.npcs)
			{
				setupMenuOptions(npc, event);
			}
		}
	}

	private void setupMenuOptions(RuneliteNpc npc, MenuEntryAdded event)
	{
		LocalPoint lp = LocalPoint.fromWorld(client, npc.getWorldPoint());

		int widgetIndex = event.getActionParam0();
		int widgetID = event.getActionParam1();
		MenuEntry[] menuEntries = client.getMenuEntries();

		if (npc.getRuneliteObject() != null && npc.getRuneliteObject().getModel() != null)
		{
			if (!isMouseOverNpc(npc)) return;
			if (event.getOption().equals("Walk here") && isMouseOverNpc(npc))
			{
				addExamine(npc, menuEntries, widgetIndex, widgetID);
				addTalkOption(npc, widgetIndex, widgetID);
			}

			if (lp == null)
			{
				return;
			}

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

	public void setActive(RuneliteNpc npc)
	{
		LocalPoint lp = LocalPoint.fromWorld(client, npc.getWorldPoint());
		if (lp == null) return;

		npc.getRuneliteObject().setLocation(lp, client.getPlane());
		npc.activate();
	}

	public boolean isActive(RuneliteNpc runeliteNpc)
	{
		return runeliteNpc.getRuneliteObject().isActive();
	}

	public void remove(RuneliteNpc runeliteNpc)
	{
		runeliteNpc.remove();
	}

	public void removeGroup(String groupID)
	{
		RuneliteNpcs matchedGroup = null;
		for (RuneliteNpcs npcGroup : npcGroups)
		{
			if (npcGroup.getGroupName().equals(groupID))
			{
				matchedGroup = npcGroup;
				npcGroup.removeAll(this);
			}
		}
		if (matchedGroup != null) npcGroups.remove(matchedGroup);
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

	public void addExamine(RuneliteNpc npc, MenuEntry[] menuEntries, int widgetIndex, int widgetID)
	{
		// This is important for getting talk to be left-clickable, but don't fully understand why
		menuEntries = Arrays.copyOf(menuEntries, menuEntries.length + 1);

		client.createMenuEntry(menuEntries.length - 1)
			.setOption("Examine")
			.setTarget("<col=ffff00>" + npc.getName() + "</col>")
			.onClick(menuEntry -> {
				clickPos = client.getMouseCanvasPosition();
				clickAnimationFrame = 0;
				bufferAnimation = 0;

				String chatMessage = new ChatMessageBuilder()
					.append(ChatColorType.NORMAL)
					.append(npc.getExamine())
					.build();

				chatMessageManager.queue(QueuedMessage.builder()
					.type(ChatMessageType.NPC_EXAMINE)
					.runeLiteFormattedMessage(chatMessage)
					.timestamp((int) (System.currentTimeMillis() / 1000))
					.build());
			})
			.setType(MenuAction.RUNELITE)
			.setParam0(widgetIndex)
			.setParam1(widgetID);
	}

	public void addTalkOption(RuneliteNpc runeliteNpc, int widgetIndex, int widgetID)
	{
		MenuEntry talkEntry = client.createMenuEntry(-2)
			.setOption("Talk")
			.setTarget("<col=ffff00>" + runeliteNpc.getName() + "</col>")
			.setType(MenuAction.RUNELITE_HIGH_PRIORITY)
			.setDeprioritized(false)
			.onClick((menuEntry -> {
				clickPos = client.getMouseCanvasPosition();

				WorldPoint wp = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
				if (wp.distanceTo(runeliteNpc.getWorldPoint()) > 1)
				{
					String chatMessage = new ChatMessageBuilder()
						.append(ChatColorType.NORMAL)
						.append("You'll need to move closer to them to talk!")
						.build();

					chatMessageManager.queue(QueuedMessage.builder()
						.type(ChatMessageType.NPC_EXAMINE)
						.runeLiteFormattedMessage(chatMessage)
						.timestamp((int) (System.currentTimeMillis() / 1000))
						.build());
					return;
				}
				lastInteractedWithNpc = runeliteNpc;

				// Set to rotate towards player
				rotatingObjectsToPlayer.add(runeliteNpc);
				runeliteNpc.setupChatBox(chatboxPanelManager);
			}))
			.setParam0(widgetIndex)
			.setParam1(widgetID);
	}

	private boolean isMouseOverNpc(RuneliteNpc runeliteNpc)
	{
		LocalPoint lp = LocalPoint.fromWorld(client, runeliteNpc.getWorldPoint());

		if (lp == null) return false;
		Shape clickbox = Perspective.getClickbox(client, runeliteNpc.getRuneliteObject().getModel(), runeliteNpc.getRuneliteObject().getOrientation(), lp.getX(), lp.getY(),
			Perspective.getTileHeight(client, lp, runeliteNpc.getWorldPoint().getPlane()));

		if (clickbox == null) return false;

		Point p = client.getMouseCanvasPosition();

		return clickbox.contains(p.getX(), p.getY());
	}

	public void makeWorldOverlayHint(Graphics2D graphics)
	{
		renderRedClick(graphics);

		WorldPoint playerPosition = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
		if (lastInteractedWithNpc != null && playerPosition.distanceTo(lastInteractedWithNpc.getWorldPoint()) > 1 && chatboxPanelManager.getCurrentInput() instanceof ChatBox) chatboxPanelManager.close();

		for (RuneliteNpcs npcGroup : npcGroups)
		{
			for (RuneliteNpc npc : npcGroup.npcs)
			{
				hideIfNpcOnTile(npc);
				hideIfPlayerOnTile(npc, playerPosition);
			}
		}
	}

	private void renderRedClick(Graphics2D graphics)
	{
		if (clickPos != null && clickAnimationFrame < 4)
		{
			BufferedImage img = spriteManager.getSprite(redClick[clickAnimationFrame], 0);
			if (img == null) return;
			Point point = new Point(clickPos.getX() - (img.getWidth() / 2), clickPos.getY() - (img.getHeight() / 2));
			OverlayUtil.renderImageLocation(graphics, point, img);
			bufferAnimation = Math.floorMod(bufferAnimation + 1, ANIMATION_PERIOD);
			if (bufferAnimation == 0)
			{
				clickAnimationFrame++;
			}
		}
		else
		{
			clickPos = null;
			clickAnimationFrame = 0;
		}
	}

	private void hideIfNpcOnTile(RuneliteNpc runeliteNpc)
	{
		boolean npcCurrentlyOnTile = false;
		for (NPC npc : client.getNpcs())
		{
			WorldPoint wpNpc = WorldPoint.fromLocalInstance(client, npc.getLocalLocation());
			if (wpNpc != null && wpNpc.distanceTo2D(runeliteNpc.getWorldPoint()) == 0)
			{
				npcCurrentlyOnTile = true;
				break;
			}
		}
		if (npcCurrentlyOnTile && !npcBeenOnTile)
		{
			npcBeenOnTile = true;
			remove(runeliteNpc);
		}
		else if (!npcCurrentlyOnTile && npcBeenOnTile)
		{
			npcBeenOnTile = false;

			if (playerBeenOnTile) return;

			LocalPoint lp = LocalPoint.fromWorld(client, runeliteNpc.getWorldPoint());
			if (lp == null) return;
			setActive(runeliteNpc);
		}
	}

	private void hideIfPlayerOnTile(RuneliteNpc runeliteNpc, WorldPoint playerPosition)
	{
		if (playerPosition.distanceTo(runeliteNpc.getWorldPoint()) == 0 && !playerBeenOnTile)
		{
			playerBeenOnTile = true;
			remove(runeliteNpc);
//			runeliteNpc.remove();
		}
		else if (playerBeenOnTile && !runeliteNpc.getRuneliteObject().isActive() && playerPosition.distanceTo(runeliteNpc.getWorldPoint()) != 0)
		{
			playerBeenOnTile = false;

			if (npcBeenOnTile) return;

			LocalPoint lp = LocalPoint.fromWorld(client, runeliteNpc.getWorldPoint());
			if (lp == null) return;
			setActive(runeliteNpc);
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (lastInteractedWithNpc == null) return;
		lastInteractedWithNpc.progressDialog();
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		rotatingObjectsToPlayer.removeIf((runeliteNpc) -> runeliteNpc.partiallyRotateToPlayer(client));
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			for (RuneliteNpcs npcGroup : npcGroups)
			{
				for (RuneliteNpc npc : npcGroup.npcs)
				{
					LocalPoint lp = LocalPoint.fromWorld(client, npc.getWorldPoint());
					if (lp == null) return;
					setActive(npc);
				}
			}
		}
		else if (event.getGameState() == GameState.LOADING)
		{
			for (RuneliteNpcs npcGroup : npcGroups)
			{
				for (RuneliteNpc npc : npcGroup.npcs)
				{
					remove(npc);
				}
			}
		}
	}

}
