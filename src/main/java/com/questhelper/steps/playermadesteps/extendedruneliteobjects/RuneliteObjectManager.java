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
package com.questhelper.steps.playermadesteps.extendedruneliteobjects;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
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

	protected final Map<String, ExtendedRuneliteObjects> runeliteObjectGroups = new HashMap<>();

	// Red Click
	Point clickPos;
	int redClickAnimationFrame = 0;
	int bufferRedClickAnimation = 0;
	int[] redClick = new int[]{SpriteID.RED_CLICK_ANIMATION_1, SpriteID.RED_CLICK_ANIMATION_2, SpriteID.RED_CLICK_ANIMATION_3, SpriteID.RED_CLICK_ANIMATION_4};
	final int ANIMATION_PERIOD = 10;

	ExtendedRuneliteObject lastInteractedWithRuneliteObject;

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

	List<ExtendedRuneliteObject> rotatingObjectsToPlayer = new ArrayList<>();

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

		clientThread.invokeLater(this::removeRuneliteObjects);
	}

	private void removeRuneliteObjects()
	{
		disableRuneliteObjects();
		runeliteObjectGroups.clear();
	}

	private void disableRuneliteObjects()
	{
		runeliteObjectGroups.forEach((groupID, extendedRuneliteObjectGroup) -> {
			extendedRuneliteObjectGroup.disableAll(this);
		});
	}

	public FakeNpc createFakeNpc(String groupID, int[] model, WorldPoint wp, int animation)
	{
		FakeNpc extendedRuneliteObject = new FakeNpc(client, clientThread, wp, model, animation);
		// Should this be here or a separate 'activate' step?
		extendedRuneliteObject.activate();

		runeliteObjectGroups.computeIfAbsent(groupID, (existingVal) -> new ExtendedRuneliteObjects(groupID));
		runeliteObjectGroups.get(groupID).addExtendedRuneliteObject(extendedRuneliteObject);

		return extendedRuneliteObject;
	}

	public FakeItem createFakeItem(String groupID, int[] model, WorldPoint wp, int animation)
	{
		FakeItem extendedRuneliteObject = new FakeItem(client, clientThread, wp, model, animation);
		// Should this be here or a separate 'activate' step?
		extendedRuneliteObject.activate();

		runeliteObjectGroups.computeIfAbsent(groupID, (existingVal) -> new ExtendedRuneliteObjects(groupID));
		runeliteObjectGroups.get(groupID).addExtendedRuneliteObject(extendedRuneliteObject);

		return extendedRuneliteObject;
	}

	public FakeObject createFakeObject(String groupID, int[] model, WorldPoint wp, int animation)
	{
		FakeObject extendedRuneliteObject = new FakeObject(client, clientThread, wp, model, animation);
		// Should this be here or a separate 'activate' step?
		extendedRuneliteObject.activate();

		runeliteObjectGroups.computeIfAbsent(groupID, (existingVal) -> new ExtendedRuneliteObjects(groupID));
		runeliteObjectGroups.get(groupID).addExtendedRuneliteObject(extendedRuneliteObject);

		return extendedRuneliteObject;
	}

//	public ExtendedRuneliteObject createRuneliteObject(String groupID, int[] model, WorldPoint wp, int animation)
//	{
//		ExtendedRuneliteObject extendedRuneliteObject = new ExtendedRuneliteObject(client, clientThread, wp, model, animation);
//		// Should this be here or a separate 'activate' step?
//		extendedRuneliteObject.activate();
//
//		runeliteObjectGroups.computeIfAbsent(groupID, (existingVal) -> new ExtendedRuneliteObjects(groupID));
//		runeliteObjectGroups.get(groupID).addExtendedRuneliteObject(extendedRuneliteObject);
//
//		return extendedRuneliteObject;
//	}

//	public ExtendedRuneliteObject createRuneliteObject(QuestHelper questHelper, int[] model, WorldPoint wp, int animation)
//	{
//		String groupID = questHelper.toString();
//		return createRuneliteObject(groupID, model, wp, animation);
//	}

	public void removeGroupAndSubgroups(String groupID)
	{
		if (runeliteObjectGroups.get(groupID) == null) return;
		clientThread.invokeLater(() -> {
			runeliteObjectGroups.get(groupID).removeAllIncludingSubgroups(this);
		});
	}

	public void removeRuneliteObject(String groupID, ExtendedRuneliteObject eRuneliteObject)
	{
		if (eRuneliteObject == null)
		{
			throw new IllegalStateException("Attempted to remove null RuneliteObject from Manager");
		}
		eRuneliteObject.disable();

		ExtendedRuneliteObjects groupERuneliteObjects = runeliteObjectGroups.get(groupID);

		if (groupERuneliteObjects == null)
		{
			throw new IllegalStateException("Attempted to remove non-added ExtendedRuneliteObjects " + groupID + "from Manager");
		}

		groupERuneliteObjects.remove(eRuneliteObject);
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		runeliteObjectGroups.forEach((groupID, runeliteObjectGroup) -> {
			for (ExtendedRuneliteObject runeliteObject : runeliteObjectGroup.extendedRuneliteObjects)
			{
				setupMenuOptions(runeliteObject, event);
			}
		});
	}

	private void setupMenuOptions(ExtendedRuneliteObject extendedRuneliteObject
		, MenuEntryAdded event)
	{
		LocalPoint lp = LocalPoint.fromWorld(client, extendedRuneliteObject.getWorldPoint());

		int widgetIndex = event.getActionParam0();
		int widgetID = event.getActionParam1();
		MenuEntry[] menuEntries = client.getMenuEntries();

		if (!extendedRuneliteObject.isHiddenNoOptions() && extendedRuneliteObject.getRuneliteObject() != null && extendedRuneliteObject.getRuneliteObject().getModel() != null)
		{
			if (!isMouseOverObject(extendedRuneliteObject)) return;
			if (event.getOption().equals("Walk here") && isMouseOverObject(extendedRuneliteObject))
			{
				extendedRuneliteObject.getActions().forEach((name, action) -> {
					addAction(extendedRuneliteObject, widgetIndex, widgetID, action, name);
				});
				extendedRuneliteObject.getPriorityActions().forEach((name, action) -> {
					addPriorityAction(extendedRuneliteObject, widgetIndex, widgetID, action, name);
				});

				if (extendedRuneliteObject.getReplaceWalkAction() != null && extendedRuneliteObject.getReplaceWalkActionText() != null)
				{
					addReplaceWalkAction(event.getMenuEntry(), extendedRuneliteObject);
				}
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

	public void setActive(ExtendedRuneliteObject extendedRuneliteObject)
	{
		LocalPoint lp = LocalPoint.fromWorld(client, extendedRuneliteObject.getWorldPoint());
		if (lp == null) return;

		extendedRuneliteObject.getRuneliteObject().setLocation(lp, client.getPlane());
		extendedRuneliteObject.activate();
	}

	public void disableObject(ExtendedRuneliteObject extendedRuneliteObject)
	{
		extendedRuneliteObject.disable();
	}

	public void removeGroup(String groupID)
	{
		runeliteObjectGroups.get(groupID).removeAll(this);
		runeliteObjectGroups.remove(groupID);
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

	public Consumer<MenuEntry> getExamineAction(ExtendedRuneliteObject extendedRuneliteObject)
	{
		return menuEntry -> {
			createChatboxMessage(extendedRuneliteObject.getExamine());
		};
	}
	
	public Consumer<MenuEntry> getTalkAction(ExtendedRuneliteObject extendedRuneliteObject)
	{
		return menuEntry -> {
			WorldPoint wp = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
			if (wp.distanceTo(extendedRuneliteObject.getWorldPoint()) > 1)
			{
				createChatboxMessage("You'll need to move closer to them to talk!");
				return;
			}

			// Set to rotate towards player
			rotatingObjectsToPlayer.add(extendedRuneliteObject);
			extendedRuneliteObject.setupChatBox(chatboxPanelManager);
		};
	}

	private void addReplaceWalkAction(MenuEntry menuEntry, ExtendedRuneliteObject extendedRuneliteObject)
	{
		menuEntry.setOption(extendedRuneliteObject.getReplaceWalkActionText());
		menuEntry.setTarget("<col=" + extendedRuneliteObject.getNameColor() + ">" + extendedRuneliteObject.getName() + "</col>");
		menuEntry.onClick(menuEnt -> {
			resetRedClick();
			lastInteractedWithRuneliteObject = extendedRuneliteObject;
			extendedRuneliteObject.getReplaceWalkAction().accept(menuEnt);
		});
	}

	private void addPriorityAction(ExtendedRuneliteObject extendedRuneliteObject, int widgetIndex, int widgetID, Consumer<MenuEntry> action, String actionWord)
	{
		MenuEntry[] menuEntries = client.getMenuEntries();
		menuEntries = Arrays.copyOf(menuEntries, menuEntries.length + 1);

		client.createMenuEntry(-2)
			.setOption(actionWord)
			.setTarget("<col=" + extendedRuneliteObject.getNameColor() + ">" + extendedRuneliteObject.getName() + "</col>")
			.setType(MenuAction.RUNELITE_HIGH_PRIORITY)
			.setDeprioritized(false)
			.onClick(menuEntry -> {;
				resetRedClick();
				lastInteractedWithRuneliteObject = extendedRuneliteObject;
				action.accept(menuEntry);
			})
			.setParam0(widgetIndex)
			.setParam1(widgetID);
	}

	private void addAction(ExtendedRuneliteObject extendedRuneliteObject, int widgetIndex, int widgetID, Consumer<MenuEntry> action, String actionWord)
	{
		MenuEntry[] menuEntries = client.getMenuEntries();
		menuEntries = Arrays.copyOf(menuEntries, menuEntries.length + 1);

		client.createMenuEntry(menuEntries.length - 1)
			.setOption(actionWord)
			.setTarget("<col=" + extendedRuneliteObject.getNameColor() + ">" + extendedRuneliteObject.getName() + "</col>")
			.setType(MenuAction.RUNELITE)
			.onClick(menuEntry -> {;
				resetRedClick();
				action.accept(menuEntry);
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

	private boolean isMouseOverObject(ExtendedRuneliteObject extendedRuneliteObject)
	{
		LocalPoint lp = LocalPoint.fromWorld(client, extendedRuneliteObject.getWorldPoint());

		if (lp == null) return false;
		Shape clickbox = Perspective.getClickbox(client, extendedRuneliteObject.getRuneliteObject().getModel(), extendedRuneliteObject.getRuneliteObject().getOrientation(), lp.getX(), lp.getY(),
			Perspective.getTileHeight(client, lp, extendedRuneliteObject.getWorldPoint().getPlane()));

		if (clickbox == null) return false;

		Point p = client.getMouseCanvasPosition();

		return clickbox.contains(p.getX(), p.getY());
	}

	public void makeWidgetOverlayHint(Graphics2D graphics)
	{
		renderRedClick(graphics);

		WorldPoint playerPosition = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
		if (lastInteractedWithRuneliteObject != null && playerPosition.distanceTo(lastInteractedWithRuneliteObject.getWorldPoint()) > 1 && chatboxPanelManager.getCurrentInput() instanceof ChatBox) chatboxPanelManager.close();
	}

	private void renderRedClick(Graphics2D graphics)
	{
		if (clickPos != null && redClickAnimationFrame < 4)
		{
			BufferedImage img = spriteManager.getSprite(redClick[redClickAnimationFrame], 0);
			if (img == null) return;
			Point point = new Point(clickPos.getX() - (img.getWidth() / 2), clickPos.getY() - (img.getHeight() / 2));
			OverlayUtil.renderImageLocation(graphics, point, img);
			bufferRedClickAnimation = Math.floorMod(bufferRedClickAnimation + 1, ANIMATION_PERIOD);
			if (bufferRedClickAnimation == 0)
			{
				redClickAnimationFrame++;
			}
		}
		else
		{
			clickPos = null;
			redClickAnimationFrame = 0;
		}
	}

	private boolean isNpcOnTile(ExtendedRuneliteObject extendedRuneliteObject)
	{
		for (NPC npc : client.getNpcs())
		{
			WorldPoint wpNpc = npc.getWorldLocation();
			if (wpNpc != null && wpNpc.distanceTo(extendedRuneliteObject.getWorldPoint()) == 0)
			{
				return true;
			}
		}

		return false;
	}

	private boolean isPlayerOnTile(ExtendedRuneliteObject extendedRuneliteObject, WorldPoint playerPosition)
	{
		return playerPosition.distanceTo(extendedRuneliteObject.getWorldPoint()) == 0;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		runeliteObjectGroups.forEach((groupID, extendedRuneliteObjectsGroup) -> {
			for (ExtendedRuneliteObject extendedRuneliteObject : extendedRuneliteObjectsGroup.extendedRuneliteObjects)
			{
				if (extendedRuneliteObject.isActive())
				{
					if (client.getPlane() != extendedRuneliteObject.getWorldPoint().getPlane())
					{
						disableObject(extendedRuneliteObject);
					}
					if (extendedRuneliteObject.isHiddenNoOptions())
					{
						disableObject(extendedRuneliteObject);
					}
				}
			}
		});

		// TODO: This needs to be more generic, for checking what action is 'pending'. Currently this is used just to keep to a tick system for dialog loading
		if (lastInteractedWithRuneliteObject != null)
		{
			lastInteractedWithRuneliteObject.checkPendingAction();
			if (lastInteractedWithRuneliteObject.getActions().get("Talk") != null ||
				lastInteractedWithRuneliteObject.getPriorityActions().get("Talk") != null)
			{
				lastInteractedWithRuneliteObject.progressDialog();
			}
		}
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		rotatingObjectsToPlayer.removeIf((extendedRuneliteObject) -> extendedRuneliteObject.partiallyRotateToPlayer(client));

		WorldPoint playerPosition = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
		runeliteObjectGroups.forEach((groupID, extendedRuneliteObjectGroup) -> {
			for (ExtendedRuneliteObject extendedRuneliteObject : extendedRuneliteObjectGroup.extendedRuneliteObjects)
			{
				boolean isVisible = extendedRuneliteObject.isActive();
				if (isNpcOnTile(extendedRuneliteObject) || isPlayerOnTile(extendedRuneliteObject, playerPosition))
				{
					if (isVisible) disableObject(extendedRuneliteObject);
				}
				else if (!isVisible)
				{
					setActive(extendedRuneliteObject);
				}
			}
		});
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			runeliteObjectGroups.forEach((groupID, extendedRuneliteObjectGroup) -> {
				for (ExtendedRuneliteObject extendedRuneliteObject : extendedRuneliteObjectGroup.extendedRuneliteObjects)
				{
					LocalPoint lp = LocalPoint.fromWorld(client, extendedRuneliteObject.getWorldPoint());
					if (lp == null) return;
					setActive(extendedRuneliteObject);
				}
			});
		}
		else if (event.getGameState() == GameState.LOADING)
		{
			runeliteObjectGroups.forEach((groupID, extendedRuneliteObjectGroup) -> {
				for (ExtendedRuneliteObject extendedRuneliteObject : extendedRuneliteObjectGroup.extendedRuneliteObjects)
				{
					disableObject(extendedRuneliteObject);
				}
			});
		}
	}
}
