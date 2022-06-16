/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.quests.monkeymadnessii;

import com.questhelper.Zone;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.RuneliteRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.PrayerRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import net.runelite.api.ChatMessageType;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.Player;
import net.runelite.api.Prayer;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

public class AgilityDungeonSteps extends DetailedOwnerStep
{
	final int MAX_DEPTH = 20;

	WorldPoint lastPosition = new WorldPoint(0, 0, 0);

	DetailedQuestStep traverseDungeonFirstSection, getKey, openBronzeDoor, traverseDungeonThirdSection, openShortcut, goToKruk, enterShortcut, fightKruk;

	DetailedQuestStep leaveFallArea1, leaveFallArea2, leaveFallArea3, leaveFallArea4;

	ItemRequirement bronzeKey;

	Requirement protectFromRanged, protectFromMelee;

	List<WorldPoint> path1V1, path1V2, pathConnectingPath1V2ToV1, pathConnectingPath1V1ToV2, path2V1, path2V2, pathMaze, path3V1, path3V2, pathToChest,
		pathFromChest, pathToDoor, path4V1, path4V2, path5V1, path5V2, path5V3, firstHalfSection5Path, pathToShortcutV1, pathToShortcutV2, pathToKrukV1, pathToKrukV2;

	Zone fallArea1, fallArea2, fallArea3, fallArea4, fallArea4P2, cavesSection2, cavesSection2P2, cavesSection3, cavesSection3P2, cavesSection4P1, cavesSection4P2,
		cavesSection4P3, krukRoom;

	Requirement inCavesSection2, inCavesSection3, inCavesSection4, hasBronzeKey, inFallArea1, inFallArea2, inFallArea3, inFallArea4, inKrukRoom, openedShortcut;

	ChatMessageRequirement path1SouthIsWrongChat, path2NorthIsWrongChat, path2NorthIsWrongChat2, path3SouthIsWrongChat, path4NorthIsWrongChat, path5WestIsWrongChat,
		path5MiddleIsWrongChat, path5EastIsWrongChat, path5WestToMiddleWrongChat, path5MiddleToWestWrongChat, path5MiddleToEastWrongChat, path5EastToMiddleWrongChat,
		path6WestIsWrongChat;
	Conditions path1SouthIsWrong, path2NorthIsWrong, path3SouthIsWrong, path4NorthIsWrong, path5WestIsWrong, path5MiddleIsWrong, path5EastIsWrong, path6WestIsWrong;

	HashMap<MM2Route, RuneliteRequirement> routeSavedValues;
	MM2AgilityNodes[] fifthSectionMap;
	int[] fifthSectionRightPaths;

	RuneliteRequirement shouldUsePath1V2, shouldUsePath2V2, shouldUsePath3V2, shouldUsePath4V2, shouldntUsePath5V1,
		shouldntUsePath5V2, shouldntUsePath5V3, shouldUsePath6V2;

	public AgilityDungeonSteps(QuestHelper questHelper)
	{
		super(questHelper);
		updateSection1Route();
		updateSection2Route();
		updateSection3Route();
	}

	public void setupRequirements()
	{
		bronzeKey = new ItemRequirement("Bronze key", ItemID.BRONZE_KEY_19566);
		protectFromMelee = new PrayerRequirement("Protect from Melee", Prayer.PROTECT_FROM_MELEE);
		protectFromRanged = new PrayerRequirement("Protect from Missiles", Prayer.PROTECT_FROM_MISSILES);
	}

	public void setupZones()
	{
		fallArea1 = new Zone(new WorldPoint(2306, 9126, 1), new WorldPoint(2376, 9188, 1));
		fallArea2 = new Zone(new WorldPoint(2378, 9153, 1), new WorldPoint(2454, 9187, 1));
		fallArea3 = new Zone(new WorldPoint(2375, 9188, 1), new WorldPoint(2423, 9264, 1));
		fallArea4 = new Zone(new WorldPoint(2334, 9223, 1), new WorldPoint(2374, 9277, 1));
		fallArea4P2 = new Zone(new WorldPoint(2375, 9265, 1), new WorldPoint(2425, 9279, 1));

		cavesSection2 = new Zone(new WorldPoint(2621, 9153, 1), new WorldPoint(2653, 9189, 1));
		cavesSection2P2 = new Zone(new WorldPoint(2611, 9187, 1), new WorldPoint(2620, 9196, 1));
		cavesSection3 = new Zone(new WorldPoint(2565, 9188, 1), new WorldPoint(2610, 9265, 1));
		cavesSection3P2 = new Zone(new WorldPoint(2611, 9243, 1), new WorldPoint(2616, 9254, 1));
		cavesSection4P1 = new Zone(new WorldPoint(2556, 9266, 1), new WorldPoint(2640, 9278, 1));
		cavesSection4P2 = new Zone(new WorldPoint(2528, 9228, 1), new WorldPoint(2565, 9265, 1));
		cavesSection4P3 = new Zone(new WorldPoint(2542, 9226, 1), new WorldPoint(2550, 9227, 1));
		krukRoom = new Zone(new WorldPoint(2518, 9202, 1), new WorldPoint(2548, 9227, 1));
	}

	public void setupConditions()
	{
		inFallArea1 = new ZoneRequirement(fallArea1);
		inFallArea2 = new ZoneRequirement(fallArea2);
		inFallArea3 = new ZoneRequirement(fallArea3);
		inFallArea4 = new ZoneRequirement(fallArea4, fallArea4P2);
		inCavesSection2 = new ZoneRequirement(cavesSection2, cavesSection2P2);
		inCavesSection3 = new ZoneRequirement(cavesSection3, cavesSection3P2);
		inCavesSection4 = new ZoneRequirement(cavesSection4P1, cavesSection4P2, cavesSection4P3);
		inKrukRoom = new ZoneRequirement(krukRoom);

		hasBronzeKey = bronzeKey;
		openedShortcut = new VarbitRequirement(5029, 1);

		path1SouthIsWrongChat = new ChatMessageRequirement(
			new ZoneRequirement(new Zone(new WorldPoint(2512, 9141, 1), new WorldPoint(2515, 9135, 1))),
			"Something about this route feels wrong.");
		path1SouthIsWrong = new Conditions(true, LogicType.OR, path1SouthIsWrongChat,
			new ZoneRequirement(new Zone(new WorldPoint(2511, 9147, 1), new WorldPoint(2527, 9159, 1))));

		path2NorthIsWrongChat = new ChatMessageRequirement(
			new ZoneRequirement(new Zone(new WorldPoint(2527, 9169, 1), new WorldPoint(2529, 9171, 1))),
			"Something about this tunnel feels wrong.");
		path2NorthIsWrongChat2 = new ChatMessageRequirement(
			new ZoneRequirement(new Zone(new WorldPoint(2548, 9154, 1), new WorldPoint(2550, 9156, 1))),
			"Something about this route feels wrong.");
		path2NorthIsWrong = new Conditions(true, LogicType.OR, path2NorthIsWrongChat, path2NorthIsWrongChat2,
			new ZoneRequirement(new Zone(new WorldPoint(2566, 9151, 1), new WorldPoint(2574, 9164, 1))));

		path3SouthIsWrongChat = new ChatMessageRequirement(
			new ZoneRequirement(new Zone(new WorldPoint(2589, 9160, 1), new WorldPoint(2592, 9164, 1))),
			"Something about this route feels wrong.");
		path3SouthIsWrong = new Conditions(true, LogicType.OR, path3SouthIsWrongChat,
			new ZoneRequirement(new Zone(new WorldPoint(2590, 9173, 1), new WorldPoint(2617, 9180, 1))));

		path4NorthIsWrongChat = new ChatMessageRequirement(
			new ZoneRequirement(new Zone(new WorldPoint(2600, 9196, 1), new WorldPoint(2600, 9201, 1))),
			"Something about this route feels wrong.");

		path5WestIsWrongChat = new ChatMessageRequirement(
			new ZoneRequirement(new Zone(new WorldPoint(2575, 9223, 1), new WorldPoint(2577, 9225, 1))),
			"Something about this route feels wrong.");

		path5MiddleIsWrongChat = new ChatMessageRequirement(
			new ZoneRequirement(new Zone(new WorldPoint(2586, 9226, 1), new WorldPoint(2588, 9228, 1))),
			"Something about this route feels wrong.");

		path5EastIsWrongChat = new ChatMessageRequirement(
			new ZoneRequirement(new Zone(new WorldPoint(2601, 9228, 1), new WorldPoint(2603, 9230, 1))),
			"Something about this route feels wrong.");

		path5WestToMiddleWrongChat = new ChatMessageRequirement(
			new ZoneRequirement(new Zone(new WorldPoint(2581, 9224, 1), new WorldPoint(2583, 9226, 1))),
			"Something about this route feels wrong.");
		path5MiddleToWestWrongChat = new ChatMessageRequirement(
			new ZoneRequirement(new Zone(new WorldPoint(2584, 9223, 1), new WorldPoint(2586, 9224, 1))),
			"Something about this route feels wrong.");
		path5MiddleToEastWrongChat = new ChatMessageRequirement(
			new ZoneRequirement(new Zone(new WorldPoint(2588, 9221, 1), new WorldPoint(2592, 9222, 1))),
			"Something about this route feels wrong.");
		path5EastToMiddleWrongChat = new ChatMessageRequirement(
			new ZoneRequirement(new Zone(new WorldPoint(2595, 9221, 1), new WorldPoint(2598, 9221, 1))),
			"Something about this route feels wrong.");

		path4NorthIsWrong = new Conditions(true, LogicType.OR, path4NorthIsWrongChat, path5WestToMiddleWrongChat,
			path5MiddleToEastWrongChat,
			new ZoneRequirement(new Zone(new WorldPoint(2565, 9189, 1), new WorldPoint(2596, 9213, 1))));
		path5WestIsWrong = new Conditions(LogicType.OR, path5WestIsWrongChat, path5MiddleToWestWrongChat, path5EastToMiddleWrongChat);
		path5MiddleIsWrong = new Conditions(LogicType.OR, path5MiddleIsWrongChat, path5WestToMiddleWrongChat, path5EastToMiddleWrongChat);
		path5EastIsWrong = new Conditions(LogicType.OR, path5EastIsWrongChat, path5MiddleToEastWrongChat, path5WestToMiddleWrongChat);

		path6WestIsWrongChat = new ChatMessageRequirement(
			new ZoneRequirement(new Zone(new WorldPoint(2550, 9258, 1), new WorldPoint(2553, 9259, 1))),
			"Something about this route feels wrong.");
		path6WestIsWrong = new Conditions(true, LogicType.OR, path6WestIsWrongChat,
			new ZoneRequirement(new Zone(new WorldPoint(2546, 9226, 1), new WorldPoint(2554, 9255, 1))));


		shouldUsePath1V2 = new RuneliteRequirement(getQuestHelper().getConfigManager(), "mm2path1v2", "true");
		shouldUsePath2V2 = new RuneliteRequirement(getQuestHelper().getConfigManager(), "mm2path2v2", "true");
		shouldUsePath3V2 = new RuneliteRequirement(getQuestHelper().getConfigManager(), "mm2path3v2", "true");
		shouldUsePath4V2 = new RuneliteRequirement(getQuestHelper().getConfigManager(), "mm2path4v2", "true");
		shouldntUsePath5V1 = new RuneliteRequirement(getQuestHelper().getConfigManager(), "mm2path5v1", "false");
		shouldntUsePath5V2 = new RuneliteRequirement(getQuestHelper().getConfigManager(), "mm2path5v2", "false");
		shouldntUsePath5V3 = new RuneliteRequirement(getQuestHelper().getConfigManager(), "mm2path5v3", "false");
		shouldUsePath6V2 = new RuneliteRequirement(getQuestHelper().getConfigManager(), "mm2path6v2", "true");

		shouldUsePath1V2.initWithValue("false");
		shouldUsePath2V2.initWithValue("false");
		shouldUsePath3V2.initWithValue("false");
		shouldUsePath4V2.initWithValue("false");
		shouldntUsePath5V1.initWithValue("false");
		shouldntUsePath5V2.initWithValue("false");
		shouldntUsePath5V3.initWithValue("false");
		shouldUsePath6V2.initWithValue("false");
	}

	@Override
	public void setupSteps()
	{
		routeSavedValues = new HashMap<>();
		fifthSectionMap = MM2AgilityNodes.values();
		for (MM2AgilityNodes mm2AgilityNodes : fifthSectionMap)
		{
			for (MM2Route path : mm2AgilityNodes.getPaths())
			{
				if (path == null)
				{
					continue;
				}
				routeSavedValues.putIfAbsent(path,
					new RuneliteRequirement(questHelper.getConfigManager(), "mm2agilitywrongway" + path.name(), "false"));
				routeSavedValues.get(path).initWithValue("false");
			}
		}

		fifthSectionRightPaths = new int[fifthSectionMap.length];
		Arrays.fill(fifthSectionRightPaths, -1);

		setupRequirements();
		setupZones();
		setupConditions();
		setupPaths();

		leaveFallArea1 = new ObjectStep(getQuestHelper(), ObjectID.ROPE_28775, new WorldPoint(2317, 9159, 1),
			"Go up the rope to the west.", protectFromMelee);
		leaveFallArea2 = new ObjectStep(getQuestHelper(), ObjectID.ROPE_28775, new WorldPoint(2379, 9168, 1),
			"Go up the rope to the west.", protectFromMelee);
		leaveFallArea3 = new ObjectStep(getQuestHelper(), ObjectID.ROPE_28775, new WorldPoint(2414, 9189, 1),
			"Climb up the rope to the south east and use Protect from Ranged.", protectFromRanged);
		leaveFallArea4 = new ObjectStep(getQuestHelper(), ObjectID.ROPE_28775, new WorldPoint(2364, 9264, 1),
			"Climb up the rope to the north west and use Protect from Melee.", protectFromMelee);

		traverseDungeonFirstSection = new DetailedQuestStep(getQuestHelper(), "Traverse the next section of the dungeon. Protect from Melee if you fall.");
		traverseDungeonThirdSection = new ObjectStep(getQuestHelper(), ObjectID.HOLE_28764, new WorldPoint(2595, 9266, 1),
			"Traverse the next section of the dungeon. PROTECT FROM MISSILE if you fall.");
		getKey = new ObjectStep(getQuestHelper(), ObjectID.CHEST_28792, new WorldPoint(2653, 9163, 1),
			"Right-click unlock the chest in the cavern to the east for a bronze key.");
		((ObjectStep)(getKey)).addAlternateObjects(ObjectID.CHEST_28793);
		getKey.setLinePoints(pathToChest);

		openBronzeDoor = new ObjectStep(getQuestHelper(), ObjectID.BRONZE_DOOR, new WorldPoint(2610, 9195, 1),
			"Make your way north then through the bronze door.", bronzeKey);
		openBronzeDoor.setLinePoints(pathToDoor);

		openShortcut = new ObjectStep(getQuestHelper(), NullObjectID.NULL_28814, new WorldPoint(2544, 9232, 1),
			"Continue through the dungeon until you reach a shortcut to open. PROTECT FROM MELEE if you fall.");

		enterShortcut = new ObjectStep(getQuestHelper(), NullObjectID.NULL_28814, new WorldPoint(2515, 9173, 1),
			"Enter the shortcut near the entrance.");
		goToKruk = new ObjectStep(getQuestHelper(), ObjectID.CAVERN_ENTRANCE, new WorldPoint(2531, 9227, 1),
			"Prepare to fight Kruk, then enter the hole to the south.");
		goToKruk.addSubSteps(enterShortcut);

		fightKruk = new NpcStep(getQuestHelper(), NpcID.KRUK_6805, new WorldPoint(2535, 9213, 1),
			"Kill Kruk. He can be flinched on a corner in the room.");
	}

	private void updateSection1Route()
	{
		ArrayList<WorldPoint> newRoute = new ArrayList<>();
		if(shouldUsePath1V2.check(client))
		{
			newRoute.addAll(path1V2);
		}
		else
		{
			newRoute.addAll(path1V1);
		}

		if(shouldUsePath2V2.check(client))
		{
			if (shouldUsePath1V2.check(client))
			{
				newRoute.addAll(pathConnectingPath1V1ToV2);
			}
			newRoute.addAll(path2V2);
		}
		else
		{
			if (!shouldUsePath1V2.check(client))
			{
				newRoute.addAll(pathConnectingPath1V2ToV1);
			}
			newRoute.addAll(path2V1);
		}
		newRoute.addAll(pathMaze);

		if(shouldUsePath3V2.check(client))
		{
			newRoute.addAll(path3V2);
		}
		else
		{
			newRoute.addAll(path3V1);
		}

		traverseDungeonFirstSection.setLinePoints(newRoute);
	}

	private void updateSection2Route()
	{
		ArrayList<WorldPoint> newRoute = new ArrayList<>();
		if(shouldUsePath4V2.check(client))
		{
			newRoute.addAll(path4V2);
			newRoute.addAll(workOutFifthSection(0, new ArrayList<>(), 0));
		}
		else
		{
			newRoute.addAll(path4V1);
			newRoute.addAll(workOutFifthSection(0, new ArrayList<>(), 2));
		}
		traverseDungeonThirdSection.setLinePoints(newRoute);
	}

	private void updateSection3Route()
	{
		if(shouldUsePath6V2.check(client))
		{
			goToKruk.setLinePoints(pathToKrukV2);
			goToKruk.setWorldPoint(new WorldPoint(2548, 9225, 1));
			openShortcut.setLinePoints(pathToShortcutV2);
			openShortcut.setWorldPoint(new WorldPoint(2544, 9232, 1));
		}
		else
		{
			goToKruk.setLinePoints(pathToKrukV1);
			goToKruk.setWorldPoint(new WorldPoint(2531, 9227, 1));
			openShortcut.setLinePoints(pathToShortcutV1);
			openShortcut.setWorldPoint(new WorldPoint(2534, 9240, 1));
		}
	}

	public ArrayList<WorldPoint> workOutFifthSection(int currentDepth, ArrayList<Integer> previousIds, int id)
	{
		previousIds.add(id);
		if (currentDepth > MAX_DEPTH)
		{
			return new ArrayList<>();
		}

		ArrayList<WorldPoint> newPoints = new ArrayList<>();
		MM2AgilityNodes currentNode = fifthSectionMap[id];
		int currentCorrectRoute = fifthSectionRightPaths[id];

		int nextNodeId;

		// If we've tried the un-indicated path and it was correct...
		if (currentCorrectRoute != -1)
		{
			// Check if we've just gone backwards/looped, thus it's not the 'correct' next step
			if (previousIds.contains(currentNode.getPaths()[currentCorrectRoute].getIdEnd()))
			{
				// If we're going back onto ourself, we've done something wrong. Set back to unknown
				fifthSectionRightPaths[id] = -1;
			}
			else
			{
				nextNodeId = currentNode.getPaths()[currentCorrectRoute].getIdEnd();

				// If we know this path is correct, all other paths are invalid. Save them as thus
				for (MM2Route path : currentNode.getPaths())
				{
					if (path != null && path.getIdEnd() != nextNodeId)
					{
						routeSavedValues.get(path).setConfigValue("true");
					}
				}

				newPoints.addAll(currentNode.getPaths()[currentCorrectRoute].getPath());
				newPoints.addAll(workOutFifthSection(currentDepth + 1, previousIds, nextNodeId));
				return newPoints;
			}
		}
		for (int i = 0; i < currentNode.getPaths().length; i++)
		{
			if (currentNode.getPaths()[i] != null)
			{
				if (currentNode.getPaths()[i].getWrongWay().check(client)) routeSavedValues.get(currentNode.getPaths()[i]).setConfigValue("true");
				if (routeSavedValues.get(currentNode.getPaths()[i]).check(client) && !previousIds.contains(currentNode.getPaths()[i].getIdEnd()))
				{
					nextNodeId = currentNode.getPaths()[i].getIdEnd();
					newPoints.addAll(currentNode.getPaths()[i].getPath());
					newPoints.addAll(workOutFifthSection(currentDepth + 1, previousIds, nextNodeId));
					return newPoints;
				}
			}
		}

		return new ArrayList<>();
	}

	public void checkSection5Successes()
	{
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return;
		}
		WorldPoint currentPosition = player.getWorldLocation();
		for (int i = 0; i < fifthSectionMap.length; i++)
		{
			MM2Route[] pathsFromNode = fifthSectionMap[i].getPaths();
			for (int dir = 0; dir < pathsFromNode.length; dir++)
			{
				if (pathsFromNode[dir] != null)
				{
					if (pathsFromNode[dir].getStartWp().contains(lastPosition) && pathsFromNode[dir].getEndWp().contains(currentPosition))
					{
						if (fifthSectionRightPaths[i] != dir)
						{
							fifthSectionRightPaths[i] = dir;
							updateSection2Route();
						}
						break;
					}
				}
			}
		}
		lastPosition = currentPosition;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (!shouldUsePath1V2.check(client))
		{
			if (path1SouthIsWrong.check(client)) shouldUsePath1V2.setConfigValue("true");
			if (shouldUsePath1V2.check(client))
			{
				updateSection1Route();
			}
		}
		if (!shouldUsePath2V2.check(client))
		{
			if (path2NorthIsWrong.check(client)) shouldUsePath2V2.setConfigValue("true");
			if (shouldUsePath2V2.check(client))
			{
				updateSection1Route();
			}
		}
		if (!shouldUsePath3V2.check(client))
		{
			if (path3SouthIsWrong.check(client)) shouldUsePath3V2.setConfigValue("true");
			if (shouldUsePath3V2.check(client))
			{
				updateSection1Route();
			}
		}

		if (!shouldUsePath4V2.check(client))
		{
			if (path4NorthIsWrong.check(client)) shouldUsePath4V2.setConfigValue("true");
			if (shouldUsePath4V2.check(client))
			{
				updateSection2Route();
			}
		}

		if (!shouldntUsePath5V1.check(client))
		{
			if (path5EastIsWrong.check(client)) shouldntUsePath5V1.setConfigValue("true");
			if (shouldntUsePath5V1.check(client))
			{
				updateSection2Route();
			}
		}

		if (!shouldntUsePath5V2.check(client))
		{
			if (path5MiddleIsWrong.check(client)) shouldntUsePath5V2.setConfigValue("true");
			if (shouldntUsePath5V2.check(client))
			{
				updateSection2Route();
			}
		}

		if (!shouldntUsePath5V3.check(client))
		{
			if (path5WestIsWrong.check(client)) shouldntUsePath5V3.setConfigValue("true");
			if (shouldntUsePath5V1.check(client))
			{
				updateSection2Route();
			}
		}

		if (!shouldUsePath6V2.check(client))
		{
			if (path6WestIsWrong.check(client)) shouldUsePath6V2.setConfigValue("true");
			if (shouldUsePath6V2.check(client))
			{
				updateSection3Route();
			}
		}

		checkSection5Successes();
		updateSteps();
	}

	@Override
	protected void updateSteps()
	{
		if (inCavesSection4.check(client))
		{
			if (openedShortcut.check(client))
			{
				startUpStep(goToKruk);
			}
			else
			{
				startUpStep(openShortcut);
			}
		}
		else if (inKrukRoom.check(client))
		{
			startUpStep(fightKruk);
		}
		else if (openedShortcut.check(client))
		{
			startUpStep(enterShortcut);
		}
		else if (inCavesSection3.check(client))
		{
			startUpStep(traverseDungeonThirdSection);
		}
		else if (inCavesSection2.check(client))
		{
			if (hasBronzeKey.check(client))
			{
				startUpStep(openBronzeDoor);
			}
			else
			{
				startUpStep(getKey);
			}
		}
		else if (inFallArea1.check(client))
		{
			startUpStep(leaveFallArea1);
		}
		else if (inFallArea2.check(client))
		{
			startUpStep(leaveFallArea2);
		}
		else if (inFallArea3.check(client))
		{
			startUpStep(leaveFallArea3);
		}
		else if (inFallArea4.check(client))
		{
			startUpStep(leaveFallArea4);
		}
		else
		{
			startUpStep(traverseDungeonFirstSection);
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE)
		{
			path1SouthIsWrongChat.validateCondition(client, chatMessage.getMessage());
			path2NorthIsWrongChat.validateCondition(client, chatMessage.getMessage());
			path2NorthIsWrongChat2.validateCondition(client, chatMessage.getMessage());
			path3SouthIsWrongChat.validateCondition(client, chatMessage.getMessage());
			path4NorthIsWrongChat.validateCondition(client, chatMessage.getMessage());
			path5WestIsWrongChat.validateCondition(client, chatMessage.getMessage());
			path5MiddleIsWrongChat.validateCondition(client, chatMessage.getMessage());
			path5EastIsWrongChat.validateCondition(client, chatMessage.getMessage());
			path5MiddleToEastWrongChat.validateCondition(client, chatMessage.getMessage());
			path5MiddleToWestWrongChat.validateCondition(client, chatMessage.getMessage());
			path5EastToMiddleWrongChat.validateCondition(client, chatMessage.getMessage());
			path5WestToMiddleWrongChat.validateCondition(client, chatMessage.getMessage());
			path6WestIsWrongChat.validateCondition(client, chatMessage.getMessage());
			for (MM2AgilityNodes mm2AgilityNode : fifthSectionMap)
			{
				for (int i = 0; i < mm2AgilityNode.getPaths().length; i++)
				{
					if (mm2AgilityNode.getPaths()[i] != null)
					{
						mm2AgilityNode.getPaths()[i].getWrongWay().validateCondition(client, chatMessage.getMessage());
					}
				}
			}
			updateSection2Route();
		}
	}

	private void setupPaths()
	{
		path1V1 = Arrays.asList(
			new WorldPoint(2509, 9169, 1),
			new WorldPoint(2509, 9167, 1),
			new WorldPoint(2507, 9164, 1),
			new WorldPoint(2507, 9156, 1),
			new WorldPoint(2504, 9153, 1),
			new WorldPoint(2504, 9146, 1),
			new WorldPoint(2509, 9145, 1),
			new WorldPoint(2509, 9144, 1),
			new WorldPoint(2509, 9143, 1),
			new WorldPoint(2513, 9143, 1),
			new WorldPoint(2513, 9136, 1),
			new WorldPoint(2515, 9135, 1),
			new WorldPoint(2521, 9135, 1),
			new WorldPoint(2529, 9140, 1),
			new WorldPoint(2531, 9140, 1),
			new WorldPoint(2531, 9139, 1),
			new WorldPoint(2533, 9139, 1),
			new WorldPoint(2533, 9145, 1),
			new WorldPoint(2535, 9145, 1),
			new WorldPoint(2535, 9143, 1),
			new WorldPoint(2538, 9143, 1),
			new WorldPoint(2543, 9148, 1),
			new WorldPoint(2549, 9148, 1)
		);

		path1V2 = Arrays.asList(
			new WorldPoint(2509, 9169, 1),
			new WorldPoint(2509, 9167, 1),
			new WorldPoint(2507, 9164, 1),
			new WorldPoint(2507, 9156, 1),
			new WorldPoint(2504, 9153, 1),
			new WorldPoint(2504, 9146, 1),
			new WorldPoint(2509, 9145, 1),
			new WorldPoint(2509, 9144, 1),
			new WorldPoint(2514, 9144, 1),
			new WorldPoint(2514, 9146, 1),
			new WorldPoint(2511, 9146, 1),
			new WorldPoint(2511, 9148, 1),
			new WorldPoint(2519, 9148, 1),
			new WorldPoint(2519, 9155, 1),
			new WorldPoint(2525, 9155, 1),
			new WorldPoint(2525, 9158, 1),
			new WorldPoint(2528, 9165, 1)
		);

		pathConnectingPath1V2ToV1 = Arrays.asList(
			new WorldPoint(2549, 9152, 1),
			new WorldPoint(2549, 9162, 1),
			new WorldPoint(2539, 9166, 1),
			new WorldPoint(2528, 9165, 1)
		);

		pathConnectingPath1V1ToV2 = Arrays.asList(
			new WorldPoint(2528, 9165, 1),
			new WorldPoint(2539, 9166, 1),
			new WorldPoint(2549, 9162, 1),
			new WorldPoint(2549, 9152, 1)
		);

		// This goes north from the first bonfire
		path2V1 = Arrays.asList(
			new WorldPoint(2528, 9173, 1),
			new WorldPoint(2534, 9173, 1),
			new WorldPoint(2538, 9180, 1),
			new WorldPoint(2548, 9180, 1),
			new WorldPoint(2552, 9184, 1),
			new WorldPoint(2567, 9184, 1),
			new WorldPoint(2567, 9178, 1)
		);

		path2V2 = Arrays.asList(
			new WorldPoint(2557, 9152, 1),
			new WorldPoint(2560, 9156, 1),
			new WorldPoint(2567, 9156, 1),
			new WorldPoint(2574, 9164, 1)
		);


		pathMaze = Arrays.asList(
			new WorldPoint(2575, 9170, 1),
			new WorldPoint(2577, 9170, 1),
			new WorldPoint(2577, 9172, 1),
			new WorldPoint(2581, 9172, 1),
			new WorldPoint(2581, 9169, 1),
			new WorldPoint(2584, 9169, 1)
		);

		path3V1 = Arrays.asList(
			new WorldPoint(2589, 9162, 1),
			new WorldPoint(2597, 9162, 1),
			new WorldPoint(2602, 9162, 1),
			new WorldPoint(2602, 9168, 1),
			new WorldPoint(2603, 9169, 1),
			new WorldPoint(2603, 9171, 1),
			new WorldPoint(2608, 9171, 1),
			new WorldPoint(2608, 9165, 1),
			new WorldPoint(2611, 9162, 1),
			new WorldPoint(2611, 9159, 1),
			new WorldPoint(2617, 9160, 1),
			new WorldPoint(2622, 9160, 1),
			new WorldPoint(2629, 9167, 1)
		);

		path3V2 = Arrays.asList(
			new WorldPoint(2588, 9177, 1),
			new WorldPoint(2593, 9177, 1),
			new WorldPoint(2597, 9176, 1),
			new WorldPoint(2603, 9176, 1),
			new WorldPoint(2604, 9179, 1),
			new WorldPoint(2612, 9179, 1),
			new WorldPoint(2616, 9173, 1),
			new WorldPoint(2621, 9173, 1),
			new WorldPoint(2625, 9172, 1),
			new WorldPoint(2629, 9172, 1),
			new WorldPoint(2629, 9167, 1)
		);

		pathToChest = Arrays.asList(
			new WorldPoint(2629, 9167, 1),
			new WorldPoint(2639, 9167, 1),
			new WorldPoint(2639, 9163, 1),
			new WorldPoint(2643, 9163, 1),
			new WorldPoint(2643, 9165, 1),
			new WorldPoint(2645, 9165, 1),
			new WorldPoint(2645, 9163, 1),
			new WorldPoint(2652, 9163, 1)
		);

		pathFromChest = Arrays.asList(
			new WorldPoint(2652, 9163, 1),
			new WorldPoint(2639, 9163, 1),
			new WorldPoint(2639, 9167, 1),
			new WorldPoint(2629, 9167, 1)
		);

		pathToDoor = Arrays.asList(
			new WorldPoint(2652, 9163, 1),
			new WorldPoint(2645, 9163, 1),
			new WorldPoint(2645, 9165, 1),
			new WorldPoint(2643, 9165, 1),
			new WorldPoint(2643, 9163, 1),
			new WorldPoint(2639, 9163, 1),
			new WorldPoint(2639, 9167, 1),
			new WorldPoint(2629, 9167, 1),

			new WorldPoint(2630, 9171, 1),
			new WorldPoint(2633, 9171, 1),
			new WorldPoint(2633, 9169, 1),
			new WorldPoint(2635, 9169, 1),
			new WorldPoint(2635, 9171, 1),
			new WorldPoint(2641, 9171, 1),
			new WorldPoint(2641, 9173, 1),
			new WorldPoint(2636, 9173, 1),
			new WorldPoint(2636, 9175, 1),
			new WorldPoint(2635, 9175, 1),
			new WorldPoint(2635, 9183, 1),
			new WorldPoint(2628, 9183, 1),
			new WorldPoint(2628, 9186, 1),
			new WorldPoint(2627, 9186, 1),
			new WorldPoint(2627, 9187, 1),
			new WorldPoint(2624, 9187, 1),
			new WorldPoint(2623, 9188, 1),
			new WorldPoint(2615, 9188, 1),
			new WorldPoint(2615, 9195, 1),
			new WorldPoint(2611, 9195, 1)
		);

		path4V1 = Arrays.asList(
			new WorldPoint(2607, 9195, 1),
			new WorldPoint(2600, 9195, 1),
			new WorldPoint(2600, 9205, 1),
			new WorldPoint(2601, 9207, 1),
			new WorldPoint(2601, 9208, 1),
			new WorldPoint(2598, 9208, 1),
			new WorldPoint(2598, 9210, 1),
			new WorldPoint(2600, 9210, 1),
			new WorldPoint(2600, 9212, 1),
			new WorldPoint(2598, 9212, 1),
			new WorldPoint(2598, 9215, 1),
			new WorldPoint(2599, 9216, 1),
			new WorldPoint(2599, 9221, 1)
		);

		path4V2 = Arrays.asList(
			new WorldPoint(2607, 9195, 1),
			new WorldPoint(2600, 9195, 1),
			new WorldPoint(2595, 9195, 1),
			new WorldPoint(2592, 9196, 1),
			new WorldPoint(2584, 9196, 1),
			new WorldPoint(2584, 9192, 1),
			new WorldPoint(2579, 9192, 1),
			new WorldPoint(2579, 9194, 1),
			new WorldPoint(2578, 9196, 1),
			new WorldPoint(2578, 9203, 1),
			new WorldPoint(2579, 9207, 1),
			new WorldPoint(2574, 9207, 1),
			new WorldPoint(2571, 9211, 1),
			new WorldPoint(2571, 9220, 1),
			new WorldPoint(2576, 9222, 1)
		);

		path5V1 = Arrays.asList(
			new WorldPoint(2599, 9221, 1),
			new WorldPoint(2604, 9228, 1),
			new WorldPoint(2604, 9230, 1),
			new WorldPoint(2602, 9230, 1),
			new WorldPoint(2602, 9232, 1)
		);

		path5V2 = Arrays.asList(
			new WorldPoint(2587, 9223, 1),
			new WorldPoint(2587, 9231, 1),
			new WorldPoint(2591, 9235, 1),
			new WorldPoint(2591, 9237, 1)
		);

		path5V3 = Arrays.asList(
			new WorldPoint(2576, 9222, 1),
			new WorldPoint(2576, 9225, 1),
			new WorldPoint(2577, 9225, 1),
			new WorldPoint(2577, 9227, 1),
			new WorldPoint(2575, 9227, 1),
			new WorldPoint(2575, 9229, 1),
			new WorldPoint(2572, 9229, 1),
			new WorldPoint(2572, 9230, 1),
			new WorldPoint(2570, 9230, 1),
			new WorldPoint(2570, 9232, 1),
			new WorldPoint(2569, 9232, 1),
			new WorldPoint(2569, 9234, 1),
			new WorldPoint(2571, 9234, 1),
			new WorldPoint(2571, 9239, 1)
		);

		firstHalfSection5Path = Arrays.asList(
			new WorldPoint(2593, 9273, 1),
			new WorldPoint(2570, 9273, 1),
			new WorldPoint(2554, 9258, 1)
		);

		pathToShortcutV1 = new ArrayList<>();
		pathToShortcutV1.addAll(firstHalfSection5Path);
		pathToShortcutV1.addAll(Arrays.asList(
			new WorldPoint(2545, 9258, 1),
			new WorldPoint(2544, 9257, 1),
			new WorldPoint(2544, 9252, 1),
			new WorldPoint(2541, 9253, 1),
			new WorldPoint(2532, 9253, 1),
			new WorldPoint(2529, 9247, 1),
			new WorldPoint(2529, 9240, 1),
			new WorldPoint(2533, 9240, 1)
		));

		pathToKrukV1 = new ArrayList<>();
		pathToKrukV1.addAll(firstHalfSection5Path);
		pathToKrukV1.addAll(Arrays.asList(
			new WorldPoint(2545, 9258, 1),
			new WorldPoint(2544, 9257, 1),
			new WorldPoint(2544, 9252, 1),
			new WorldPoint(2541, 9253, 1),
			new WorldPoint(2532, 9253, 1),
			new WorldPoint(2529, 9247, 1),
			new WorldPoint(2529, 9240, 1),
			new WorldPoint(2533, 9240, 1),
			new WorldPoint(2533, 9236, 1),
			new WorldPoint(2530, 9236, 1),
			new WorldPoint(2530, 9233, 1),
			new WorldPoint(2532, 9233, 1),
			new WorldPoint(2532, 9230, 1),
			new WorldPoint(2531, 9230, 1),
			new WorldPoint(2531, 9228, 1)
		));

		pathToShortcutV2 = new ArrayList<>();
		pathToShortcutV2.addAll(firstHalfSection5Path);
		pathToShortcutV2.addAll(Arrays.asList(
			new WorldPoint(2554, 9253, 1),
			new WorldPoint(2552, 9253, 1),
			new WorldPoint(2552, 9254, 1),
			new WorldPoint(2551, 9254, 1),
			new WorldPoint(2551, 9255, 1),
			new WorldPoint(2548, 9255, 1),
			new WorldPoint(2548, 9252, 1),
			new WorldPoint(2550, 9252, 1),
			new WorldPoint(2550, 9251, 1),
			new WorldPoint(2552, 9251, 1),
			new WorldPoint(2552, 9249, 1),
			new WorldPoint(2551, 9249, 1),
			new WorldPoint(2550, 9244, 1),
			new WorldPoint(2550, 9239, 1),
			new WorldPoint(2547, 9237, 1),
			new WorldPoint(2547, 9232, 1),
			new WorldPoint(2545, 9232, 1)
		));

		pathToKrukV2 = new ArrayList<>();
		pathToKrukV2.addAll(firstHalfSection5Path);
		pathToKrukV2.addAll(Arrays.asList(
			new WorldPoint(2554, 9253, 1),
			new WorldPoint(2552, 9253, 1),
			new WorldPoint(2552, 9254, 1),
			new WorldPoint(2551, 9254, 1),
			new WorldPoint(2551, 9255, 1),
			new WorldPoint(2548, 9255, 1),
			new WorldPoint(2548, 9252, 1),
			new WorldPoint(2550, 9252, 1),
			new WorldPoint(2550, 9251, 1),
			new WorldPoint(2552, 9251, 1),
			new WorldPoint(2552, 9249, 1),
			new WorldPoint(2551, 9249, 1),
			new WorldPoint(2550, 9244, 1),
			new WorldPoint(2550, 9239, 1),
			new WorldPoint(2547, 9237, 1),
			new WorldPoint(2547, 9232, 1),
			new WorldPoint(2548, 9231, 1),
			new WorldPoint(2548, 9229, 1),
			new WorldPoint(2546, 9229, 1),
			new WorldPoint(2546, 9227, 1),
			new WorldPoint(2548, 9227, 1),
			new WorldPoint(2548, 9226, 1)
		));
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(leaveFallArea1, leaveFallArea2, leaveFallArea3, leaveFallArea4, traverseDungeonFirstSection, getKey, openBronzeDoor, traverseDungeonThirdSection,
			openShortcut, goToKruk, enterShortcut, fightKruk);
	}

	public List<QuestStep> getDisplaySteps()
	{
		return Arrays.asList(traverseDungeonFirstSection, getKey, openBronzeDoor, traverseDungeonThirdSection, openShortcut, goToKruk, fightKruk);
	}
}
