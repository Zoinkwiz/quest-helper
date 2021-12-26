/*
 * Copyright (c) 2020, Zoinkwiz
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
package com.questhelper.quests.curseoftheemptylord;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;

@QuestDescriptor(
	quest = QuestHelperQuest.CURSE_OF_THE_EMPTY_LORD
)
public class CurseOfTheEmptyLord extends BasicQuestHelper
{
	private final int PATH_VARBIT = 815;
	private int currentPath = 0;

	//Items Required
	ItemRequirement ringOfVis, ghostspeakItems, ghostspeak, knife;

	Requirement talkedToValdez, talkedToRennard, talkedToKharrim, talkedToLennissa, talkedToDhalak, talkedToViggora, inRoguesCastle, inEdgevilleDungeon, inSlayerTower,
		inEdgevilleMonastery, inPartyRoom, onPath1, onPath2, onPath3;

	DetailedQuestStep talkToValdez, talkToRennard, talkToKharrim, talkToLennissa, talkToDhalak, talkToViggora,
		goUpstairsRoguesCastle, goUpstairsSlayerTower, goUpstairsMonastery, goUpstairsPartyRoom;

	ObjectStep goDownIntoEdgevilleDungeon;

	//Zones
	Zone roguesCastleFirstFloor, edgevilleDungeon, slayerTowerFirstFloor, edgevilleMonastery, partyRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep dhalakSteps = new ConditionalStep(this, talkToDhalak);
		dhalakSteps.addStep(new Conditions(onPath3, inPartyRoom), talkToDhalak);
		dhalakSteps.addStep(onPath3, goUpstairsPartyRoom);
		dhalakSteps.addStep(new Conditions(onPath2, inEdgevilleMonastery), talkToDhalak);
		dhalakSteps.addStep(onPath2, goUpstairsMonastery);

		ConditionalStep viggoraSteps = new ConditionalStep(this, talkToViggora);
		viggoraSteps.addStep(new Conditions(onPath3, inEdgevilleDungeon), talkToViggora);
		viggoraSteps.addStep(onPath3, goDownIntoEdgevilleDungeon);
		viggoraSteps.addStep(new Conditions(onPath2, inSlayerTower), talkToViggora);
		viggoraSteps.addStep(onPath2, goUpstairsSlayerTower);
		viggoraSteps.addStep(new Conditions(onPath1, inRoguesCastle), talkToViggora);
		viggoraSteps.addStep(onPath1, goUpstairsRoguesCastle);

		ConditionalStep questSteps = new ConditionalStep(this, talkToValdez);
		questSteps.addStep(talkedToDhalak, viggoraSteps);
		questSteps.addStep(talkedToLennissa, dhalakSteps);
		questSteps.addStep(talkedToKharrim, talkToLennissa);
		questSteps.addStep(talkedToRennard, talkToKharrim);
		questSteps.addStep(talkedToValdez, talkToRennard);

		steps.put(0, questSteps);

		return steps;
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		if (currentPath == 0)
		{
			int newPath = client.getVarbitValue(PATH_VARBIT);
			if (newPath != 0)
			{
				currentPath = newPath;
				updateSteps(newPath);
			}
		}
	}

	public void setupItemRequirements()
	{
		ringOfVis = new ItemRequirement("Ring of visibility", ItemID.RING_OF_VISIBILITY, 1, true);
		ghostspeak = new ItemRequirement("Ghostspeak amulet", ItemID.GHOSTSPEAK_AMULET, 1, true);
		ghostspeakItems = new ItemRequirement("Ghostspeak amulet or any pair of morytania legs", ItemID.GHOSTSPEAK_AMULET, 1, true);
		ghostspeakItems.addAlternates(ItemID.MORYTANIA_LEGS, ItemID.MORYTANIA_LEGS_1, ItemID.MORYTANIA_LEGS_2, ItemID.MORYTANIA_LEGS_3, ItemID.MORYTANIA_LEGS_4);
		knife = new ItemRequirement("Knife", ItemID.KNIFE).showConditioned(new VarbitRequirement(PATH_VARBIT, 3));
	}

	public void setupConditions()
	{
		talkedToValdez = new VarbitRequirement(816, 1);
		talkedToRennard = new VarbitRequirement(817, 1);
		talkedToKharrim = new VarbitRequirement(818, 1);
		talkedToLennissa = new VarbitRequirement(819, 1);
		talkedToDhalak = new VarbitRequirement(820, 1);
		talkedToViggora = new VarbitRequirement(821, 1);
		inEdgevilleDungeon = new ZoneRequirement(edgevilleDungeon);
		inRoguesCastle = new ZoneRequirement(roguesCastleFirstFloor);
		inSlayerTower = new ZoneRequirement(slayerTowerFirstFloor);
		inEdgevilleMonastery = new ZoneRequirement(edgevilleMonastery);
		inPartyRoom = new ZoneRequirement(partyRoom);

		onPath1 = new VarbitRequirement(PATH_VARBIT, 1);
		onPath2 = new VarbitRequirement(PATH_VARBIT, 2);
		onPath3 = new VarbitRequirement(PATH_VARBIT, 3);
	}

	public void loadZones()
	{
		edgevilleDungeon = new Zone(new WorldPoint(3086, 9821, 0), new WorldPoint(3829, 10001, 0));
		roguesCastleFirstFloor = new Zone(new WorldPoint(3274, 3924, 1), new WorldPoint(3297, 3942, 1));
		slayerTowerFirstFloor = new Zone(new WorldPoint(3403, 3529, 1), new WorldPoint(3453, 3581, 1));
		edgevilleMonastery = new Zone(new WorldPoint(3043, 3481, 1), new WorldPoint(3060, 3499, 1));
		partyRoom = new Zone(new WorldPoint(3035, 3370, 1), new WorldPoint(3057, 3387, 1));
	}

	public void setupSteps()
	{
		int pathID = client.getVarbitValue(PATH_VARBIT);

		talkToValdez = new NpcStep(this, NpcID.MYSTERIOUS_GHOST_3452, new WorldPoint(2556, 3445, 0), "Talk to the Mysterious Ghost outside Glarial's Tomb.", ghostspeakItems, ringOfVis);
		talkToValdez.addDialogStep("Tell me your story");

		talkToRennard = new NpcStep(this, NpcID.MYSTERIOUS_GHOST, new WorldPoint(2556, 3445, 0), "Talk to the Mysterious Ghost Rennard.", ghostspeakItems, ringOfVis);
		talkToRennard.addDialogStep("Tell me your story");

		talkToKharrim = new NpcStep(this, NpcID.MYSTERIOUS_GHOST_3453, new WorldPoint(2556, 3445, 0), "Talk to the Mysterious Ghost Kharrim.", ghostspeakItems, ringOfVis);
		talkToKharrim.addDialogStep("Tell me your story");

		talkToLennissa = new NpcStep(this, NpcID.MYSTERIOUS_GHOST_3454, new WorldPoint(2556, 3445, 0), "Talk to the Mysterious Ghost Lennissa.", ghostspeakItems, ringOfVis);
		talkToLennissa.addDialogStep("Tell me your story");

		talkToDhalak = new NpcStep(this, NpcID.MYSTERIOUS_GHOST_3451, new WorldPoint(2556, 3445, 0), "Talk to the Mysterious Ghost Dhalak.", ghostspeakItems, ringOfVis);
		talkToDhalak.addDialogStep("Tell me your story");

		talkToViggora = new NpcStep(this, NpcID.MYSTERIOUS_GHOST_3455, new WorldPoint(2556, 3445, 0), "Talk to the Mysterious Ghost Viggora.", ghostspeakItems, ringOfVis);
		talkToViggora.addDialogStep("Tell me your story");

		goUpstairsMonastery = new ObjectStep(this, ObjectID.LADDER_2641, new WorldPoint(3057, 3483, 0), "Talk to the Mysterious Ghost upstairs in the Edgeville Monastery.");

		goUpstairsSlayerTower = new ObjectStep(this, ObjectID.STAIRCASE, new WorldPoint(3436, 3538, 0), "Talk to the Mysterious Ghost upstairs in the Slayer Tower, near the Infernal Mages.");

		goDownIntoEdgevilleDungeon = new ObjectStep(this, ObjectID.TRAPDOOR_1579, new WorldPoint(3097, 3468, 0), "Talk to the Mysterious Ghost in the Edgeville Wilderness Dungeon, near the Earth Warriors.");
		goDownIntoEdgevilleDungeon.addAlternateObjects(ObjectID.TRAPDOOR_1581);

		goUpstairsRoguesCastle = new ObjectStep(this, ObjectID.STAIRCASE_14735, new WorldPoint(3281, 3937, 0), "Talk to the Mysterious Ghost Viggora upstairs in the Rogues' Castle in 54 Wilderness.");

		goUpstairsPartyRoom = new ObjectStep(this, ObjectID.STAIRCASE_24249, new WorldPoint(3054, 3384, 0), "Talk to the Mysterious Ghost upstairs in the Falador Party Room.");

		updateSteps(pathID);
	}

	public void updateSteps(int pathID)
	{
		if (pathID == 1)
		{
			talkToRennard.setText("Talk to the Mysterious Ghost near the shipwrecked ship near the Wilderness Agility Course in 52 Wilderness.");
			talkToRennard.setWorldPoint(3019, 3946, 0);

			talkToKharrim.setText("Talk to the Mysterious Ghost at the Chaos Temple in 38 Wilderness.");
			talkToKharrim.setWorldPoint(2954, 3821, 0);

			talkToLennissa.setText("Talk to the Mysterious Ghost in the church on Entrana.");
			talkToLennissa.setRequirements(Arrays.asList(ghostspeak, ringOfVis));
			talkToLennissa.setWorldPoint(2846, 3349, 0);

			talkToDhalak.setText("Talk to the Mysterious Ghost in the Wizard's Tower ground floor.");
			talkToDhalak.setWorldPoint(3109, 3163, 0);

			talkToViggora.setText("Talk to the Mysterious Ghost Viggora upstairs in the Rogues' Castle in 54 Wilderness.");
			talkToViggora.setWorldPoint(3295, 3934, 1);
			talkToViggora.addSubSteps(goUpstairsRoguesCastle);
		}
		else if (pathID == 2)
		{
			talkToRennard.setText("Talk to the Mysterious Ghost in the Bandit Camp in the Wilderness.");
			talkToRennard.setWorldPoint(3031, 3703, 0);

			talkToKharrim.setText("Talk to the Mysterious Ghost in the Graveyard of Shadows in the Wilderness.");
			talkToKharrim.setWorldPoint(3160, 3670, 0);

			talkToLennissa.setText("Talk to the Mysterious Ghost on the south of the Port Sarim dock.");
			talkToLennissa.setWorldPoint(3041, 3203, 0);

			talkToDhalak.setText("Talk to the Mysterious Ghost upstairs in the Edgeville Monastery.");
			talkToDhalak.setWorldPoint(3052, 3497, 1);
			talkToDhalak.addSubSteps(goUpstairsMonastery);

			talkToViggora.setText("Talk to the Mysterious Ghost upstairs in the Slayer Tower, near the Infernal Mages.");
			talkToViggora.setWorldPoint(3447, 3547, 1);
			talkToViggora.addSubSteps(goUpstairsSlayerTower);

		}
		else if (pathID == 3)
		{
			talkToRennard.setText("Talk to the Mysterious Ghost in the Bandit Camp in the desert.");
			talkToRennard.setWorldPoint(3163, 2981, 0);

			talkToKharrim.setText("Talk to the Mysterious Ghost in the centre of the Lava Maze.");
			talkToKharrim.addItemRequirements(Collections.singletonList(knife));
			talkToKharrim.setWorldPoint(3076, 3861, 0);

			talkToLennissa.setText("Talk to the Mysterious Ghost in the west of the Tree Gnome Stronghold.");
			talkToLennissa.setWorldPoint(2396, 3476, 0);

			talkToDhalak.setText("Talk to the Mysterious Ghost upstairs in the Falador Party Room.");
			talkToDhalak.setWorldPoint(3052, 3378, 1);
			talkToDhalak.addSubSteps(goUpstairsPartyRoom);

			talkToViggora.setText("Talk to the Mysterious Ghost in the Edgeville Wilderness Dungeon, near the Earth Warriors.");
			talkToViggora.setWorldPoint(3121, 9995, 0);
			talkToViggora.addSubSteps(goDownIntoEdgevilleDungeon);
		}
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(ringOfVis, ghostspeakItems, knife);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("10,000 Experience Lamp (Any skill over level 50).", ItemID.ANTIQUE_LAMP, 1)); //4447 is used as placeholder
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("A Set of Ghostly Robes"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Learn about the Empty Lord",
			Arrays.asList(talkToValdez, talkToRennard, talkToKharrim, talkToLennissa, talkToDhalak, talkToViggora),
			ghostspeakItems, ringOfVis, knife));

		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.DESERT_TREASURE, QuestState.IN_PROGRESS));
		req.add(new QuestRequirement(QuestHelperQuest.THE_RESTLESS_GHOST, QuestState.IN_PROGRESS));
		return req;
	}
}
