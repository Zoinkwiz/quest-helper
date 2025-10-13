/*
 * Copyright (c) 2025, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.tools;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Development plugin for copying QuestStep instantiation code to clipboard.
 */
@PluginDescriptor(
	name = "Quest Helper - Step Copy",
	description = "Development tool for copying QuestStep code to clipboard",
	tags = { "quest", "helper", "development", "copy" }
)
@Slf4j
public class StepCopyPlugin extends Plugin
{
	@Inject
	private Client client;
	private static final String COPY_OBJECT_STEP = "Copy ObjectStep";
	private static final String COPY_NPC_STEP = "Copy NpcStep";
	private static final String COPY_ITEM_STEP = "Copy ItemStep";

	private static final Map<Integer, String> objectIdToConstant = new HashMap<>();
	private static final Map<Integer, String> npcIdToConstant = new HashMap<>();
	private static final Map<Integer, String> itemIdToConstant = new HashMap<>();

	private static boolean mappingsInitialized = false;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Quest Helper Step Copy plugin started");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Quest Helper Step Copy plugin stopped");
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (!client.isKeyPressed(KeyCode.KC_SHIFT))
		{
			return;
		}

		MenuEntry menuEntry = event.getMenuEntry();
		MenuAction menuAction = menuEntry.getType();

		if (menuAction == MenuAction.EXAMINE_OBJECT)
		{
			handleObjectMenuEntry(event);
		}
		else if (menuAction == MenuAction.EXAMINE_NPC)
		{
			handleNpcMenuEntry(event);
		}
		else if (menuAction == MenuAction.EXAMINE_ITEM_GROUND)
		{
			handleItemMenuEntry(event);
		}
	}

	private void handleObjectMenuEntry(MenuEntryAdded event)
	{
		MenuEntry menuEntry = event.getMenuEntry();
		// For objects, we'll use the identifier to get the object ID
		if (menuEntry.getIdentifier() > 0)
		{
			addCopyStepMenuEntry(menuEntry, COPY_OBJECT_STEP, () -> {
				handleObjectStepCopy(menuEntry);
			});
		}
	}

	private void handleNpcMenuEntry(MenuEntryAdded event)
	{
		MenuEntry menuEntry = event.getMenuEntry();
		NPC npc = menuEntry.getNpc();

		if (npc != null)
		{
			addCopyStepMenuEntry(menuEntry, COPY_NPC_STEP, () -> {
				handleNpcStepCopy(npc.getId(), npc.getWorldLocation());
			});
		}
	}

	private void handleItemMenuEntry(MenuEntryAdded event)
	{
		MenuEntry menuEntry = event.getMenuEntry();
		// For items, we'll use the identifier to get the item ID
		if (menuEntry.getIdentifier() > 0)
		{
			addCopyStepMenuEntry(menuEntry, COPY_ITEM_STEP, () -> {
				handleItemStepCopy(menuEntry);
			});
		}
	}

	private void addCopyStepMenuEntry(MenuEntry originalEntry, String copyOption, Runnable onClickHandler)
	{
		net.runelite.api.Menu menu = client.getMenu();
		MenuEntry[] menuEntries = menu.getMenuEntries();

		menu.createMenuEntry(menuEntries.length - 1)
			.setIdentifier(originalEntry.getIdentifier())
			.setOption(copyOption)
			.setTarget(originalEntry.getTarget())
			.onClick((menuEntry) -> onClickHandler.run())
			.setType(MenuAction.RUNELITE)
			.setParam0(originalEntry.getParam0())
			.setParam1(originalEntry.getParam1());
	}

	private void handleObjectStepCopy(MenuEntry menuEntry)
	{
		int objectId = menuEntry.getIdentifier();

		// Get the world point from the menu entry parameters
		WorldPoint worldPoint = getWorldPointFromMenuEntry(menuEntry);

		// Get the constant name for the object ID
		String objectIdConstant = getObjectIdConstant(objectId);

		String stepCode = String.format(
			"new ObjectStep(this, %s, new WorldPoint(%d, %d, %d), \"placeholder text\");",
			objectIdConstant,
			worldPoint.getX(),
			worldPoint.getY(),
			worldPoint.getPlane()
		);

		copyToClipboard(stepCode);
		log.info("Copied ObjectStep code: {}", stepCode);
	}

	private void handleNpcStepCopy(int npcID, WorldPoint worldPoint)
	{
		// Get the constant name for the NPC ID
		String npcIdConstant = getNpcIdConstant(npcID);

		String stepCode = String.format(
			"new NpcStep(this, %s, new WorldPoint(%d, %d, %d), \"placeholder text\");",
			npcIdConstant,
			worldPoint.getX(),
			worldPoint.getY(),
			worldPoint.getPlane()
		);

		copyToClipboard(stepCode);
		log.info("Copied NpcStep code: {}", stepCode);
	}

	private void handleItemStepCopy(MenuEntry menuEntry)
	{
		int itemId = menuEntry.getIdentifier();

		String itemIdConstant = getItemIdConstant(itemId);

		String stepCode = String.format(
			"new ItemStep(this, %s, \"placeholder text\");",
			itemIdConstant
		);

		copyToClipboard(stepCode);
		log.info("Copied ItemStep code: {}", stepCode);
	}

	private void copyToClipboard(String text)
	{
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection stringSelection = new StringSelection(text);
		clipboard.setContents(stringSelection, null);
	}

	private WorldPoint getWorldPointFromMenuEntry(MenuEntry menuEntry)
	{
		final int SCENE_TO_LOCAL = 128;
		int x = menuEntry.getParam0() * SCENE_TO_LOCAL;
		int y = menuEntry.getParam1() * SCENE_TO_LOCAL;
		int wv = menuEntry.getWorldViewId();
		LocalPoint itemLp = new LocalPoint(x, y, wv);

		return WorldPoint.fromLocal(client, itemLp);
	}

	private static synchronized void initializeMappings()
	{
		if (mappingsInitialized)
		{
			return;
		}

		try
		{
			initializeIdMappings(ObjectID.class, objectIdToConstant);
			initializeIdMappings(NpcID.class, npcIdToConstant);
			initializeIdMappings(ItemID.class, itemIdToConstant);

			mappingsInitialized = true;
			log.debug("Initialized ID mappings: {} objects, {} NPCs, {} items",
				objectIdToConstant.size(), npcIdToConstant.size(), itemIdToConstant.size());
		}
		catch (Exception e)
		{
			log.error("Failed to initialize ID mappings", e);
		}
	}

	private static void initializeIdMappings(Class<?> idClass, Map<Integer, String> idMap)
	{
		Field[] fields = idClass.getDeclaredFields();

		for (Field field : fields)
		{
			try
			{
				if (field.getType() == int.class && java.lang.reflect.Modifier.isStatic(field.getModifiers()))
				{
					int value = field.getInt(null);
					String constantName = field.getName();
					idMap.put(value, constantName);
				}
			}
			catch (IllegalAccessException ignored) {}
		}
	}

	private String getObjectIdConstant(int objectId)
	{
		initializeMappings();
		String constantName = objectIdToConstant.get(objectId);
		return constantName != null ? "ObjectID." + constantName : String.valueOf(objectId);
	}

	private String getNpcIdConstant(int npcId)
	{
		initializeMappings();
		String constantName = npcIdToConstant.get(npcId);
		return constantName != null ? "NpcID." + constantName : String.valueOf(npcId);
	}

	private String getItemIdConstant(int itemId)
	{
		initializeMappings();
		String constantName = itemIdToConstant.get(itemId);
		return constantName != null ? "ItemID." + constantName : String.valueOf(itemId);
	}
}
