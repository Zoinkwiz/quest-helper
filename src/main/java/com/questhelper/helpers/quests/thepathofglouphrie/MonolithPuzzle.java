/*
 * Copyright (c) 2023, pajlada <https://github.com/pajlada>
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
package com.questhelper.helpers.quests.thepathofglouphrie;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.ItemContainer;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;


public class MonolithPuzzle extends DetailedOwnerStep
{
	private static final int STOREROOM_REGION = 11074;
	private static final int BIG_MONOLITH = ObjectID.POG_2X2_BLOCKING;
	private static final int SMALL_MONOLITH = ObjectID.POG_1X1_BLOCKING;
	private ObjectStep getFirstShapes;
	private HashMap<Integer, ItemRequirement> shapes;
	private NpcStep pushSouthernMonolithUp;
	private NpcStep pushSWMonolithNorth;
	private ZoneRequirement inStartZone;
	private ObjectStep getSecondShapes;
	private ObjectStep picklockChestForFirstKey;
	private NpcStep pushNWMonolithEast;
	private ObjectStep getThirdShapes;
	private NpcStep pushSmallMonolithSouth;
	private ObjectStep inspectSingingBowl;
	private ObjectStep openChestForCrystalChimeSeed;
	private NpcStep pushNWMonolithWest;
	private ItemRequirement crystalChime;
	private NpcStep pushSEMonolithWest;
	private ObjectStep unlockTheGate;

	public MonolithPuzzle(QuestHelper questHelper)
	{
		super(questHelper, "Unlock Yewnock's machine room in the Tree Gnome Village dungeon.");
	}

	@Override
	public void startUp()
	{
		updateSteps();
	}

	private WorldPoint regionPoint(int regionX, int regionY)
	{
		return WorldPoint.fromRegion(STOREROOM_REGION, regionX, regionY, 0);
	}

	@Override
	protected void setupSteps()
	{
		shapes = new HashMap<>();
		shapes.put(ItemID.EYEGLO_RED_CIRCLE, new ItemRequirement("Red circle", ItemID.EYEGLO_RED_CIRCLE));
		shapes.put(ItemID.EYEGLO_ORANGE_CIRCLE, new ItemRequirement("Orange circle", ItemID.EYEGLO_ORANGE_CIRCLE));
		shapes.put(ItemID.EYEGLO_YELLOW_CIRCLE, new ItemRequirement("Yellow circle", ItemID.EYEGLO_YELLOW_CIRCLE));
		shapes.put(ItemID.EYEGLO_GREEN_CIRCLE, new ItemRequirement("Green circle", ItemID.EYEGLO_GREEN_CIRCLE));
		shapes.put(ItemID.EYEGLO_BLUE_CIRCLE, new ItemRequirement("Blue circle", ItemID.EYEGLO_BLUE_CIRCLE));
		shapes.put(ItemID.EYEGLO_INDIGO_CIRCLE, new ItemRequirement("Indigo circle", ItemID.EYEGLO_INDIGO_CIRCLE));
		shapes.put(ItemID.EYEGLO_VIOLET_CIRCLE, new ItemRequirement("Violet circle", ItemID.EYEGLO_VIOLET_CIRCLE));

		shapes.put(ItemID.EYEGLO_RED_TRIANGLE, new ItemRequirement("Red triangle", ItemID.EYEGLO_RED_TRIANGLE));
		shapes.put(ItemID.EYEGLO_ORANGE_TRIANGLE, new ItemRequirement("Orange triangle", ItemID.EYEGLO_ORANGE_TRIANGLE));
		shapes.put(ItemID.EYEGLO_YELLOW_TRIANGLE, new ItemRequirement("Yellow triangle", ItemID.EYEGLO_YELLOW_TRIANGLE));
		shapes.put(ItemID.EYEGLO_GREEN_TRIANGLE, new ItemRequirement("Green triangle", ItemID.EYEGLO_GREEN_TRIANGLE));
		shapes.put(ItemID.EYEGLO_BLUE_TRIANGLE, new ItemRequirement("Blue triangle", ItemID.EYEGLO_BLUE_TRIANGLE));
		shapes.put(ItemID.EYEGLO_INDIGO_TRIANGLE, new ItemRequirement("Indigo triangle", ItemID.EYEGLO_INDIGO_TRIANGLE));
		shapes.put(ItemID.EYEGLO_VIOLET_TRIANGLE, new ItemRequirement("Violet triangle", ItemID.EYEGLO_VIOLET_TRIANGLE));

		shapes.put(ItemID.EYEGLO_RED_SQUARE, new ItemRequirement("Red square", ItemID.EYEGLO_RED_SQUARE));
		shapes.put(ItemID.EYEGLO_ORANGE_SQUARE, new ItemRequirement("Orange square", ItemID.EYEGLO_ORANGE_SQUARE));
		shapes.put(ItemID.EYEGLO_YELLOW_SQUARE, new ItemRequirement("Yellow square", ItemID.EYEGLO_YELLOW_SQUARE));
		shapes.put(ItemID.EYEGLO_GREEN_SQUARE, new ItemRequirement("Green square", ItemID.EYEGLO_GREEN_SQUARE));
		shapes.put(ItemID.EYEGLO_BLUE_SQUARE, new ItemRequirement("Blue square", ItemID.EYEGLO_BLUE_SQUARE));
		shapes.put(ItemID.EYEGLO_INDIGO_SQUARE, new ItemRequirement("Indigo square", ItemID.EYEGLO_INDIGO_SQUARE));
		shapes.put(ItemID.EYEGLO_VIOLET_SQUARE, new ItemRequirement("Violet square", ItemID.EYEGLO_VIOLET_SQUARE));

		shapes.put(ItemID.EYEGLO_RED_PENTAGON, new ItemRequirement("Red pentagon", ItemID.EYEGLO_RED_PENTAGON));
		shapes.put(ItemID.EYEGLO_ORANGE_PENTAGON, new ItemRequirement("Orange pentagon", ItemID.EYEGLO_ORANGE_PENTAGON));
		shapes.put(ItemID.EYEGLO_YELLOW_PENTAGON, new ItemRequirement("Yellow pentagon", ItemID.EYEGLO_YELLOW_PENTAGON));
		shapes.put(ItemID.EYEGLO_GREEN_PENTAGON, new ItemRequirement("Green pentagon", ItemID.EYEGLO_GREEN_PENTAGON));
		shapes.put(ItemID.EYEGLO_BLUE_PENTAGON, new ItemRequirement("Blue pentagon", ItemID.EYEGLO_BLUE_PENTAGON));
		shapes.put(ItemID.EYEGLO_INDIGO_PENTAGON, new ItemRequirement("Indigo pentagon", ItemID.EYEGLO_INDIGO_PENTAGON));
		shapes.put(ItemID.EYEGLO_VIOLET_PENTAGON, new ItemRequirement("Violet pentagon", ItemID.EYEGLO_VIOLET_PENTAGON));

		inStartZone = new ZoneRequirement(new Zone(
			regionPoint(34, 17),
			regionPoint(39, 25)
		));

		pushSouthernMonolithUp = new NpcStep(getQuestHelper(), NpcID.POG_GOLRIE_CRATE_2X2, regionPoint(37, 21),
			"Push the monolith north once.");
		pushSouthernMonolithUp.setMaxRoamRange(3);

		getFirstShapes = new ObjectStep(getQuestHelper(), ObjectID.POG_CHEST_CLOSED, regionPoint(39, 25),
			"Open the chest for some shapes.");
		getFirstShapes.addSubSteps(pushSouthernMonolithUp);

		pushSWMonolithNorth = new NpcStep(getQuestHelper(), NpcID.POG_GOLRIE_CRATE_2X2, regionPoint(35, 29),
			"Push the south-west monolith north once.");
		pushSWMonolithNorth.setMaxRoamRange(2);

		pushNWMonolithEast = new NpcStep(getQuestHelper(), NpcID.POG_GOLRIE_CRATE_2X2, regionPoint(35, 33),
			"Push the north-west monolith east once.");
		pushNWMonolithEast.setMaxRoamRange(2);

		getSecondShapes = new ObjectStep(getQuestHelper(), ObjectID.POG_CHEST_CLOSED, regionPoint(36, 38),
			"Open the chest for some more shapes. If the chest doesn't give you any shapes, drop the shapes in your inventory first, then click the chest, then pick up the shapes from the ground.");
		getSecondShapes.addSubSteps(pushSWMonolithNorth, pushNWMonolithEast);

		picklockChestForFirstKey = new ObjectStep(getQuestHelper(), ObjectID.POG_GOLRIE_SMALL_CHEST_PICKLOCK, regionPoint(33, 37),
			"Picklock the chest for a key.");
		picklockChestForFirstKey.addAlternateObjects(ObjectID.POG_GOLRIE_SMALL_CHEST_NORMAL);
		picklockChestForFirstKey.addSubSteps(pushSWMonolithNorth, pushNWMonolithEast);

		pushSmallMonolithSouth = new NpcStep(getQuestHelper(), NpcID.POG_GOLRIE_CRATE_1X1, regionPoint(39, 33),
			"Push the small monolith south once.");
		pushSmallMonolithSouth.setMaxRoamRange(2);

		// TODO: For this step, make sure it's actually possible to push the monolith west.
		// If the user has messed up, they might need to push the SW monolith south first.
		// The user might also need to push the monolith west twice in case they've pushed it too far - we don't handle that case.
		pushNWMonolithWest = new NpcStep(getQuestHelper(), NpcID.POG_GOLRIE_CRATE_2X2, regionPoint(36, 33),
			"Push the north-west monolith west once.");
		pushNWMonolithWest.setMaxRoamRange(2);

		openChestForCrystalChimeSeed = new ObjectStep(getQuestHelper(), ObjectID.POG_GOLRIE_BIG_CHEST, regionPoint(37, 34),
			"Search the chest for the strongroom key and crystal chime seed.");
		openChestForCrystalChimeSeed.addSubSteps(pushSmallMonolithSouth, pushNWMonolithWest);

		getThirdShapes = new ObjectStep(getQuestHelper(), ObjectID.POG_CHEST_CLOSED, regionPoint(41, 29),
			"Open the chest for some more shapes. If the chest doesn't give you any shapes, drop the shapes in your inventory first, then click the chest, then pick up the shapes from the ground.");
		// getThirdShapes.addSubSteps(pushSmallMonolithSouth);

		var crystalChimeSeed = new ItemRequirement("Crystal chime seed", ItemID.CRYSTAL_CHIME_SEED, 1);
		crystalChimeSeed.setTooltip("You can get another one from the golden chest in Yewnock's storeroom");

		crystalChime = new ItemRequirement("Crystal chime", ItemID.CRYSTAL_CHIME, 1);

		var machineRoomKey = new ItemRequirement("Strongroom key", ItemID.POG_STRONGROOM_KEY, 1);

		inspectSingingBowl = new ObjectStep(getQuestHelper(), ObjectID.POG_SINGING_BOWL_INSPECT, regionPoint(41, 32),
			"Inspect the Singing bowl, then click it again to create the Crystal chime.", crystalChimeSeed);
		inspectSingingBowl.addAlternateObjects(ObjectID.POG_SINGING_BOWL_SING);
		inspectSingingBowl.addDialogStep("Yes.");

		pushSEMonolithWest = new NpcStep(getQuestHelper(), NpcID.POG_GOLRIE_CRATE_2X2, regionPoint(39, 29),
			"Push the south-east monolith west once.");
		pushSEMonolithWest.setMaxRoamRange(2);

		unlockTheGate = new ObjectStep(getQuestHelper(), ObjectID.POG_GATE_DOOR, regionPoint(30, 31),
			"Unlock the gate to Yewnock's machine room.", crystalChime, machineRoomKey);
	}

	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		updateSteps();
	}

	@Subscribe
	public void onGameTick(final GameTick event)
	{
		// TODO: optimize
		updateSteps();
	}

	private int countShapes()
	{
		ItemContainer itemContainer = client.getItemContainer(InventoryID.INV);
		if (itemContainer == null)
		{
			return 0;
		}

		int count = 0;

		for (var item : itemContainer.getItems())
		{
			var shape = shapes.get(item.getId());
			if (shape != null)
			{
				count += item.getQuantity();
			}
		}

		return count;
	}

	private boolean hasFirstChestKey()
	{
		ItemContainer itemContainer = client.getItemContainer(InventoryID.INV);
		if (itemContainer == null)
		{
			return false;
		}

		return itemContainer.contains(ItemID.POG_BIG_CHEST_KEY);
	}

	private boolean hasMachineRoomKey()
	{
		ItemContainer itemContainer = client.getItemContainer(InventoryID.INV);
		if (itemContainer == null)
		{
			return false;
		}

		return itemContainer.contains(ItemID.POG_STRONGROOM_KEY);
	}

	private boolean tileHasBigMonolith(Tile[][] tiles, int sceneX, int sceneY)
	{
		var tile = tiles[sceneX][sceneY];
		if (tile == null) {
			// Tiles normally aren't null, but in tests they can be.
			return false;
		}
		for (var gameObject : tile.getGameObjects())
		{
			if (gameObject != null && gameObject.getId() == BIG_MONOLITH)
			{
				return true;
			}
		}

		return false;
	}

	private boolean tileHasSmallMonolith(Tile[][] tiles, int sceneX, int sceneY)
	{
		var tile = tiles[sceneX][sceneY];
		for (var gameObject : tile.getGameObjects())
		{
			if (gameObject != null && gameObject.getId() == SMALL_MONOLITH)
			{
				return true;
			}
		}

		return false;
	}

	protected void updateSteps()
	{
		Tile[][] tiles = client.getScene().getTiles()[client.getPlane()];

		if (tileHasBigMonolith(tiles, 53, 53))
		{
			if (inStartZone.check(client))
			{
				startUpStep(pushSouthernMonolithUp);
				return;
			}
		}

		int shapeCount = countShapes();

		if (shapeCount < 3)
		{
			// Get the first shapes
			startUpStep(getFirstShapes);
			return;
		}

		if (hasFirstChestKey())
		{
			if (tileHasBigMonolith(tiles, 51, 61))
			{
				startUpStep(pushSWMonolithNorth);
				return;
			}

			if (tileHasSmallMonolith(tiles, 55, 65))
			{
				if (tileHasBigMonolith(tiles, 51, 64))
				{
					startUpStep(pushNWMonolithEast);
					return;
				}
				startUpStep(pushSmallMonolithSouth);
				return;
			}
			if (tileHasBigMonolith(tiles, 53, 65))
			{
				startUpStep(pushNWMonolithWest);
				return;
			}

			startUpStep(openChestForCrystalChimeSeed);
			return;
		}

		if (hasMachineRoomKey())
		{
			if (shapeCount < 9)
			{
				// Get the third set of shapes
				startUpStep(getThirdShapes);
				return;
			}

			if (!crystalChime.check(client))
			{
				startUpStep(inspectSingingBowl);
				return;
			}

			if (tileHasBigMonolith(tiles, 55, 61))
			{
				startUpStep(pushSEMonolithWest);
				return;
			}

			if (crystalChime.check(client))
			{
				startUpStep(unlockTheGate);
				return;
			}
		}

		if (tileHasBigMonolith(tiles, 51, 61))
		{
			startUpStep(pushSWMonolithNorth);
			return;
		}

		if (tileHasBigMonolith(tiles, 51, 64))
		{
			startUpStep(pushNWMonolithEast);
			return;
		}

		if (shapeCount < 6)
		{
			// Get the second set of shapes
			startUpStep(getSecondShapes);
			return;
		}

		startUpStep(picklockChestForFirstKey);
	}

	@Override
	public List<QuestStep> getSteps()
	{
		return List.of(
			pushSouthernMonolithUp,
			getFirstShapes,
			pushSWMonolithNorth,
			pushNWMonolithEast,
			getSecondShapes,
			picklockChestForFirstKey,
			pushSmallMonolithSouth,
			pushNWMonolithWest,
			openChestForCrystalChimeSeed,
			getThirdShapes,
			inspectSingingBowl,
			pushSEMonolithWest,
			unlockTheGate
		);
	}
}
