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
import com.questhelper.steps.widget.WidgetDetails;
import java.awt.*;
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
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Model;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.Renderable;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.Hooks;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.JagexColors;
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

	@Inject
	private Hooks hooks;
	private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

	// Red Click
	Point clickPos;
	int redClickAnimationFrame = 0;
	int bufferRedClickAnimation = 0;
	int[] redClick = new int[]{SpriteID.RED_CLICK_ANIMATION_1, SpriteID.RED_CLICK_ANIMATION_2, SpriteID.RED_CLICK_ANIMATION_3, SpriteID.RED_CLICK_ANIMATION_4};
	final int ANIMATION_PERIOD = 5;

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
		hooks.registerRenderableDrawListener(drawListener);
	}

	public void shutDown()
	{
		hooks.unregisterRenderableDrawListener(drawListener);
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
		FakeNpc extendedRuneliteObject = new FakeNpc(client, clientThread, wp, model, animation, animation);
		// Should this be here or a separate 'activate' step?
		extendedRuneliteObject.activate();

		runeliteObjectGroups.computeIfAbsent(groupID, (existingVal) -> new ExtendedRuneliteObjects(groupID));
		runeliteObjectGroups.get(groupID).addExtendedRuneliteObject(extendedRuneliteObject);

		return extendedRuneliteObject;
	}

	public FakeNpc createFakeNpc(String groupID, Model model, WorldPoint wp, int animation)
	{
		FakeNpc extendedRuneliteObject = new FakeNpc(client, clientThread, wp, model, animation);
		// Should this be here or a separate 'activate' step?
		extendedRuneliteObject.activate();

		runeliteObjectGroups.computeIfAbsent(groupID, (existingVal) -> new ExtendedRuneliteObjects(groupID));
		runeliteObjectGroups.get(groupID).addExtendedRuneliteObject(extendedRuneliteObject);

		return extendedRuneliteObject;
	}

	public ReplacedNpc createReplacedNpc(int[] model, WorldPoint wp, int npcIDToReplace)
	{
		String groupID = "global";
		ReplacedNpc extendedRuneliteObject = new ReplacedNpc(client, clientThread, wp, model, npcIDToReplace);
		// Should this be here or a separate 'activate' step?
		for (NPC clientNpc : client.getNpcs())
		{
			if (clientNpc.getId() == npcIDToReplace)
			{
				extendedRuneliteObject.setNpc(clientNpc);
				break;
			}
		}

		runeliteObjectGroups.computeIfAbsent(groupID, (existingVal) -> new ExtendedRuneliteObjects(groupID));
		runeliteObjectGroups.get(groupID).addExtendedRuneliteObject(extendedRuneliteObject);

		return extendedRuneliteObject;
	}

	public ReplacedNpc createReplacedNpc(String groupID, int[] model, WorldPoint wp, int npcIDToReplace)
	{
		ReplacedNpc extendedRuneliteObject = new ReplacedNpc(client, clientThread, wp, model, npcIDToReplace);
		// Should this be here or a separate 'activate' step?
		for (NPC clientNpc : client.getNpcs())
		{
			if (clientNpc.getId() == npcIDToReplace)
			{
				extendedRuneliteObject.setNpc(clientNpc);
				break;
			}
		}

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

	public FakeGraphicsObject createGraphicsFakeObject(String groupID, int[] model, WorldPoint wp, int animation, ExtendedRuneliteObject obj)
	{
		FakeGraphicsObject extendedRuneliteObject = new FakeGraphicsObject(client, clientThread, wp, model, animation, obj);
		// Should this be here or a separate 'activate' step?
		extendedRuneliteObject.activate();

		runeliteObjectGroups.computeIfAbsent(groupID, (existingVal) -> new ExtendedRuneliteObjects(groupID));
		runeliteObjectGroups.get(groupID).addExtendedRuneliteObject(extendedRuneliteObject);

		return extendedRuneliteObject;
	}

	public FakeGraphicsObject createGraphicsFakeObject(String groupID, int[] model, WorldPoint wp, int animation)
	{
		FakeGraphicsObject extendedRuneliteObject = new FakeGraphicsObject(client, clientThread, wp, model, animation);
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

	boolean shouldDraw(Renderable renderable, boolean drawingUI)
	{
		if (renderable instanceof NPC)
		{
			NPC npc = (NPC) renderable;
			for (String groupID : runeliteObjectGroups.keySet())
			{
				ExtendedRuneliteObjects group = runeliteObjectGroups.get(groupID);
				for (ExtendedRuneliteObject extendedRuneliteObject : group.extendedRuneliteObjects)
				{
					if (extendedRuneliteObject instanceof ReplacedNpc)
					{
						ReplacedNpc replacedNpc = (ReplacedNpc) extendedRuneliteObject;
						if (replacedNpc.getNpc() == npc)
						{
							Point p = client.getMouseCanvasPosition();
							boolean passesRequirementToShowReplacement = replacedNpc.getDisplayReq() == null || replacedNpc.getDisplayReq().check(client);
							// is hovered
							if (!passesRequirementToShowReplacement) return true;
							if (!replacedNpc.getEntries().isEmpty())
							{
								return false;
							}
							else
							{
								return replacedNpc.getClickbox()  == null || replacedNpc.getClickbox().contains(p.getX(), p.getY());
							}
						}
					}
				}
			}
		}
		// TODO: Maybe one day this will work for GameObjects

		return true;
	}

	public ExtendedRuneliteObjects addSubGroup(String groupID, String subGroupID)
	{
		runeliteObjectGroups.computeIfAbsent(groupID, (existingVal) -> new ExtendedRuneliteObjects(groupID));
		runeliteObjectGroups.computeIfAbsent(subGroupID, (existingVal) -> new ExtendedRuneliteObjects(subGroupID));
		ExtendedRuneliteObjects subgroup = runeliteObjectGroups.get(subGroupID);
		runeliteObjectGroups.get(groupID).addSubGroup(subgroup);
		return subgroup;
	}

	public void removeGroupAndSubgroups(String groupID)
	{
		if (runeliteObjectGroups.get(groupID) == null) return;
		clientThread.invokeLater(() -> {
			if (runeliteObjectGroups.get(groupID) == null) return;
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

	// Get Menu Entry, wait for it to be the target NPC
	// Once it is, save the options, and be ready to add them to RuneliteNpc

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		NPC npc = event.getMenuEntry().getNpc();
		if (npc != null)
		{
			for (String groupID : runeliteObjectGroups.keySet())
			{
				ExtendedRuneliteObjects group = runeliteObjectGroups.get(groupID);
				for (ExtendedRuneliteObject extendedRuneliteObject : group.extendedRuneliteObjects)
				{
					copyMenuEntry(extendedRuneliteObject, event, npc);
				}
			}
		}

		List<ExtendedRuneliteObject> objs = new ArrayList<>();
		runeliteObjectGroups.forEach((groupID, runeliteObjectGroup) -> {
			objs.addAll(runeliteObjectGroup.extendedRuneliteObjects);
		});

		for (ExtendedRuneliteObject obj : objs)
		{
			setupMenuOptions(obj, event);
		}
	}

	private void copyMenuEntry(ExtendedRuneliteObject extendedRuneliteObject, MenuEntryAdded event, NPC npc)
	{
		if (extendedRuneliteObject instanceof ReplacedNpc)
		{
			ReplacedNpc replacedNpc = (ReplacedNpc) extendedRuneliteObject;
			if (replacedNpc.getNpc() == npc)
			{
				boolean shouldSkip = false;
				for (MenuEntryWrapper entry : replacedNpc.getEntries())
				{
					// If seen option before, skip it
					if (entry.option.equals(event.getOption()))
					{
						shouldSkip = true;
						break;
					}
					if (event.getOption().equals("Examine"))
					{
						shouldSkip = true;
						break;
					}
				}
				if (shouldSkip) return;
				replacedNpc.addMenuEntry(new MenuEntryWrapper(event.getOption(), event.getMenuEntry().getType(), event.getTarget(), event.getIdentifier(), 0, 0));
			}
		}
	}

	private void setupMenuOptions(ExtendedRuneliteObject extendedRuneliteObject, MenuEntryAdded event)
	{
		if (!extendedRuneliteObject.isRuneliteObjectActive()) return;
		if (extendedRuneliteObject.getMenuActions().isEmpty() && !(extendedRuneliteObject instanceof ReplacedNpc))
		{
			return;
		}
		LocalPoint lp = extendedRuneliteObject.getRuneliteObject().getLocation();

		int widgetIndex = event.getActionParam0();
		int widgetID = event.getActionParam1();
		Menu menu = client.getMenu();
		MenuEntry[] menuEntries = menu.getMenuEntries();

		if (!extendedRuneliteObject.isHiddenNoOptions()
			&& extendedRuneliteObject.getRuneliteObject() != null
			&& extendedRuneliteObject.getRuneliteObject().getModel() != null)
		{
			if (!isMouseOverObject(extendedRuneliteObject)) return;
			if (event.getOption().equals("Walk here"))
			{
				if (extendedRuneliteObject instanceof ReplacedNpc)
				{
					addReplacedNpcOptions(extendedRuneliteObject, event);
				}

				extendedRuneliteObject.getMenuActions().forEach((name, action) -> {
					addAction(extendedRuneliteObject, widgetIndex, widgetID, name);
				});

				extendedRuneliteObject.getPriorityMenuActions().forEach((name, action) -> {
					addPriorityAction(extendedRuneliteObject, widgetIndex, widgetID, name);
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

	private void addReplacedNpcOptions(ExtendedRuneliteObject extendedRuneliteObject, MenuEntryAdded event)
	{
		ReplacedNpc replacedNpc = (ReplacedNpc) extendedRuneliteObject;
		if (!replacedNpc.getEntries().isEmpty())
		{
			for (MenuEntryWrapper entry : replacedNpc.getEntries())
			{
				Menu menu = client.getMenu();
				menu.createMenuEntry(-1)
					.setOption(entry.getOption())
					.setType(entry.getType())
					.setTarget("<col=" + replacedNpc.getNameColor() + ">" + replacedNpc.getName() + "</col>")
					.setIdentifier(entry.getIdentifier())
					.setParam0(0)
					.setParam1(0);
			}
		}
	}

	public void disableObject(ExtendedRuneliteObject extendedRuneliteObject)
	{
		extendedRuneliteObject.disable();
	}

	public void removeGroup(String groupID)
	{
		if (runeliteObjectGroups.get(groupID) == null) return;
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

			if (extendedRuneliteObject.isNeedToBeCloseToTalk() && wp.distanceTo(extendedRuneliteObject.getWorldPoint()) > ExtendedRuneliteObject.MAX_TALK_DISTANCE)
			{
				createChatboxMessage("You'll need to move closer to them to talk!");
				return;
			}

			locationOfPlayerInteraction = client.getLocalPlayer().getLocalLocation();

			// Set to rotate towards player
			extendedRuneliteObject.setOrientationGoalAsPlayer(client);
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

	private void addPriorityAction(ExtendedRuneliteObject extendedRuneliteObject, int widgetIndex, int widgetID, String actionWord)
	{
		Menu menu = client.getMenu();
		MenuEntry[] menuEntries = menu.getMenuEntries();
		menuEntries = Arrays.copyOf(menuEntries, menuEntries.length + 1);

		menu.createMenuEntry(-2)
			.setOption(actionWord)
			.setTarget("<col=" + extendedRuneliteObject.getNameColor() + ">" + extendedRuneliteObject.getName() + "</col>")
			.setType(MenuAction.RUNELITE_HIGH_PRIORITY)
			.setDeprioritized(false)
			.onClick(menuEntry -> {;
				resetRedClick();
				lastInteractedWithRuneliteObject = extendedRuneliteObject;
				extendedRuneliteObject.activatePriorityAction(actionWord, menuEntry);
			})
			.setParam0(widgetIndex)
			.setParam1(widgetID);
	}

	private void addAction(ExtendedRuneliteObject extendedRuneliteObject, int widgetIndex, int widgetID, String actionWord)
	{
		Menu menu = client.getMenu();
		MenuEntry[] menuEntries = menu.getMenuEntries();
		menuEntries = Arrays.copyOf(menuEntries, menuEntries.length + 1);

		menu.createMenuEntry(menuEntries.length - 1)
			.setOption(actionWord)
			.setTarget("<col=" + extendedRuneliteObject.getNameColor() + ">" + extendedRuneliteObject.getName() + "</col>")
			.setType(MenuAction.RUNELITE)
			.onClick(menuEntry -> {
				resetRedClick();
				extendedRuneliteObject.activateAction(actionWord, menuEntry);
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
		Shape clickbox = extendedRuneliteObject.getClickbox();

		if (clickbox == null) return false;
		Point p = client.getMouseCanvasPosition();

		return clickbox.contains(p.getX(), p.getY());
	}

	private LocalPoint locationOfPlayerInteraction;

	public void makeWidgetOverlayHint(Graphics2D graphics)
	{
		renderRedClick(graphics);

		if (lastInteractedWithRuneliteObject != null
			&& locationOfPlayerInteraction != null
			&& client.getLocalPlayer().getLocalLocation().distanceTo(locationOfPlayerInteraction) > 0
			&& chatboxPanelManager.getCurrentInput() instanceof ChatBox)
		{
			chatboxPanelManager.close();
		}
	}

	public void makeWorldOverlayHint(Graphics2D graphics)
	{
		runeliteObjectGroups.forEach((groupID, group) -> {
			for (ExtendedRuneliteObject extendedRuneliteObject : group.extendedRuneliteObjects)
			{
				if (!extendedRuneliteObject.isRuneliteObjectActive()) continue;
				if (extendedRuneliteObject.getOverheadText() != null)
				{
					checkToAddOverheadText(extendedRuneliteObject, graphics);
				}
			}
		});
	}

	private void checkToAddOverheadText(ExtendedRuneliteObject extendedRuneliteObject, Graphics2D graphics)
	{
		if (extendedRuneliteObject.getTickToRemoveOverheadText() <= client.getTickCount())
		{
			extendedRuneliteObject.clearOverheadText();
			return;
		}

		addOverheadText(extendedRuneliteObject, graphics);
	}

	private void addOverheadText(ExtendedRuneliteObject rlObj, Graphics2D graphics)
	{
		LocalPoint lp = LocalPoint.fromWorld(client, rlObj.getWorldPoint());
		if (lp != null)
		{
			Point p = Perspective.localToCanvas(client, lp, client.getPlane(),
				rlObj.getRuneliteObject().getModelHeight());
			if (p != null)
			{
				Font overheadFont = FontManager.getRunescapeBoldFont();
				FontMetrics metrics = graphics.getFontMetrics(overheadFont);
				Point shiftedP = new Point(p.getX() - (metrics.stringWidth(rlObj.getOverheadText()) / 2), p.getY());

				graphics.setFont(overheadFont);
				OverlayUtil.renderTextLocation(graphics, shiftedP, rlObj.getOverheadText(),
					JagexColors.YELLOW_INTERFACE_TEXT);
			}
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

	private boolean isNpcOnTile(ExtendedRuneliteObject extendedRuneliteObject)
	{
		for (NPC npc : client.getNpcs())
		{
			WorldPoint wpNpc = npc.getWorldLocation();
			WorldPoint rlObjWp = WorldPoint.fromLocalInstance(client, extendedRuneliteObject.getRuneliteObject().getLocation());
			if (wpNpc != null && wpNpc.distanceTo(rlObjWp) == 0)
			{
				if (extendedRuneliteObject instanceof ReplacedNpc)
				{
					if (npc == ((ReplacedNpc) extendedRuneliteObject).getNpc())
					{
						continue;
					}
				}
				return true;
			}
		}

		return false;
	}

	private boolean isPlayerOnTile(ExtendedRuneliteObject extendedRuneliteObject, WorldPoint playerPosition)
	{
		WorldPoint rlObjWp = WorldPoint.fromLocalInstance(client, extendedRuneliteObject.getRuneliteObject().getLocation());
		return playerPosition.distanceTo(rlObjWp) == 0;
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		runeliteObjectGroups.forEach((groupID, group) -> {
			for (ExtendedRuneliteObject extendedRuneliteObject : group.extendedRuneliteObjects)
			{
				if (extendedRuneliteObject instanceof ReplacedNpc)
				{
					ReplacedNpc replacedNpc = ((ReplacedNpc) extendedRuneliteObject);
					if (event.getNpc().getId() == replacedNpc.getNpcIDToReplace())
					{
						replacedNpc.setNpc(event.getNpc());
					}
				}
			}
		});
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		runeliteObjectGroups.forEach((groupID, group) -> {
			for (ExtendedRuneliteObject extendedRuneliteObject : group.extendedRuneliteObjects)
			{
				if (extendedRuneliteObject instanceof ReplacedNpc)
				{
					ReplacedNpc replacedNpc = ((ReplacedNpc) extendedRuneliteObject);
					if (event.getNpc().getId() == replacedNpc.getNpcIDToReplace())
					{
						replacedNpc.setNpc(null);
					}
				}
			}
		});
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		List<ExtendedRuneliteObjects> groups = new ArrayList<>();
		// To avoid edit whilst looping error, we get groups first
		runeliteObjectGroups.forEach((groupID, extendedRuneliteObjectsGroup) -> groups.add(extendedRuneliteObjectsGroup));
		for (ExtendedRuneliteObjects group : groups)
		{
			for (ExtendedRuneliteObject extendedRuneliteObject : group.extendedRuneliteObjects)
			{
				if (extendedRuneliteObject.getDisableAfterTick() != -1 &&
					extendedRuneliteObject.getDisableAfterTick() <= client.getTickCount())
				{
					disableObject(extendedRuneliteObject);
					extendedRuneliteObject.setDisableAfterTick(-1);
				}

				if (extendedRuneliteObject.isRuneliteObjectActive())
				{
					extendedRuneliteObject.actionOnGameTick();
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
		}

		// TODO: This needs to be more generic, for checking what action is 'pending'. Currently this is used just to keep to a tick system for dialog loading
		if (lastInteractedWithRuneliteObject != null)
		{
			lastInteractedWithRuneliteObject.checkPendingAction();
			if (lastInteractedWithRuneliteObject.getMenuActions().get("Talk-to") != null ||
				lastInteractedWithRuneliteObject.getPriorityMenuActions().get("Talk-to") != null)
			{
				lastInteractedWithRuneliteObject.progressDialog();
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
		WorldPoint playerPosition = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
		runeliteObjectGroups.forEach((groupID, extendedRuneliteObjectGroup) -> {
			for (ExtendedRuneliteObject extendedRuneliteObject : extendedRuneliteObjectGroup.extendedRuneliteObjects)
			{
				extendedRuneliteObject.actionOnClientTick();

				// If replaced NPC and active,
				if (extendedRuneliteObject instanceof ReplacedNpc)
				{
					ReplacedNpc replacedNpc = ((ReplacedNpc) extendedRuneliteObject);
					NPC npc = replacedNpc.getNpc();
					if (npc != null)
					{
						replacedNpc.updateNpcSync(client);
					}
				}

				if (extendedRuneliteObject.getOrientationGoal() != extendedRuneliteObject.getRuneliteObject().getOrientation())
				{
					extendedRuneliteObject.partiallyRotateToGoal();
				}

				boolean isVisible = extendedRuneliteObject.isVisible();
				boolean shouldDisplayReqPassed = extendedRuneliteObject.getDisplayReq() == null || extendedRuneliteObject.getDisplayReq().check(client);

				if (extendedRuneliteObject.objectType == RuneliteObjectTypes.NPC &&
					(!shouldDisplayReqPassed || isNpcOnTile(extendedRuneliteObject) || isPlayerOnTile(extendedRuneliteObject, playerPosition)))
				{
					if (isVisible) extendedRuneliteObject.setVisible(false);
				}
				else
				{
					extendedRuneliteObject.setVisible(true);
				}
			}
		});
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		runeliteObjectGroups.forEach((groupID, group) -> {
			for (ExtendedRuneliteObject extendedRuneliteObject : group.extendedRuneliteObjects)
			{
				if (extendedRuneliteObject instanceof ReplacedNpc)
				{
					clientThread.invokeLater(() -> replaceWidgetsForReplacedNpcs((ReplacedNpc) extendedRuneliteObject, event));
				}
			}
		});
	}

	public void replaceWidgetsForReplacedNpcs(ReplacedNpc object, WidgetLoaded event)
	{
		if (object.getDisplayReq() != null && !object.getDisplayReq().check(client)) return;

		for (WidgetReplacement widgetReplacement : object.getWidgetReplacements())
		{
			WidgetDetails widgetDetails = widgetReplacement.getWidgetDetails();
			if (event.getGroupId() == widgetDetails.getGroupID())
			{
				Widget widget = client.getWidget(widgetDetails.getGroupID(), widgetDetails.getChildID());
				if (widget == null) continue;

				if (widgetDetails.getChildChildID() != -1)
				{
					widget = widget.getChild(widgetDetails.getChildChildID());
					if (widget == null) continue;
				}
				widget.setText(widget.getText().replace(widgetReplacement.getTextToReplace(), widgetReplacement.getReplacementText()));
				widget.revalidate();
			}
		}

		if (event.getGroupId() == InterfaceID.DIALOG_NPC)
		{
			Widget npcChatName = client.getWidget(ComponentID.DIALOG_NPC_NAME);
			Widget npcChatHead = client.getWidget(ComponentID.DIALOG_NPC_HEAD_MODEL);

			clientThread.invokeLater(() -> {
				if (npcChatHead == null || npcChatName == null)
				{
					return;
				}
				NPCComposition comp = client.getNpcDefinition(object.getNpcIDToReplace());

				if (npcChatName.getText().equals(comp.getName()))
				{
					npcChatName.setText(object.getName());
				}

				if (npcChatHead.getModelId() == object.getNpcIDToReplace())
				{
					if (object.getFace() != -1)
					{
						npcChatHead.setModelId(object.getFace());
					}
				}
			});
		}
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			runeliteObjectGroups.forEach((groupID, extendedRuneliteObjectGroup) -> {
				for (ExtendedRuneliteObject extendedRuneliteObject : extendedRuneliteObjectGroup.extendedRuneliteObjects)
				{
					extendedRuneliteObject.render();
				}
			});
		}
		else if (event.getGameState() == GameState.LOADING)
		{
			runeliteObjectGroups.forEach((groupID, extendedRuneliteObjectGroup) -> {
				for (ExtendedRuneliteObject extendedRuneliteObject : extendedRuneliteObjectGroup.extendedRuneliteObjects)
				{
					extendedRuneliteObject.render();
				}
			});
		}
	}
}
