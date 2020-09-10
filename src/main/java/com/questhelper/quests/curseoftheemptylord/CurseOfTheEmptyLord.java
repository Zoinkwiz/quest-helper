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

import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;

@QuestDescriptor(
	quest = QuestHelperQuest.CURSE_OF_THE_EMPTY_LORD
)
public class CurseOfTheEmptyLord extends BasicQuestHelper
{
	private final int PATH_VARBIT = 815;
	private int currentPath = 0;

	ItemRequirement ringOfVis, ghostspeak;

	ConditionForStep talkedToValdez, talkedToRennard, talkedToKharrim, talkedToLennissa, talkedToDhalak, talkedToViggora, inRoguesCastle, inEdgevilleDungeon, inSlayerTower,
		inEdgevilleMonastery, inPartyRoom, onPath1, onPath2, onPath3;

	DetailedQuestStep talkToValdez, talkToRennard, talkToKharrim, talkToLennissa, talkToDhalak, talkToViggora,
		goUpstairsRoguesCastle, goUpstairsSlayerTower, goUpstairsMonastery, goUpstairsPartyRoom;

	ObjectStep goDownIntoEdgevilleDungeon;

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
		// TODO: Check for mory legs
		ghostspeak = new ItemRequirement("Ghostspeak amulet", ItemID.GHOSTSPEAK_AMULET, 1, true);
	}

	public void setupConditions()
	{
		talkedToValdez = new VarbitCondition(816, 1);
		talkedToRennard = new VarbitCondition(817, 1);
		talkedToKharrim = new VarbitCondition(818, 1);
		talkedToLennissa = new VarbitCondition(819, 1);
		talkedToDhalak = new VarbitCondition(820, 1);
		talkedToViggora = new VarbitCondition(821, 1);
		inEdgevilleDungeon = new ZoneCondition(edgevilleDungeon);
		inRoguesCastle = new ZoneCondition(roguesCastleFirstFloor);
		inSlayerTower = new ZoneCondition(slayerTowerFirstFloor);
		inEdgevilleMonastery = new ZoneCondition(edgevilleMonastery);
		inPartyRoom = new ZoneCondition(partyRoom);

		onPath1 = new VarbitCondition(PATH_VARBIT, 1);
		onPath2 = new VarbitCondition(PATH_VARBIT, 2);
		onPath3 = new VarbitCondition(PATH_VARBIT, 3);
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

		talkToValdez = new NpcStep(this, NpcID.MYSTERIOUS_GHOST_3452, new WorldPoint(2556, 3445, 0), "Talk to the Mysterious Ghost outside Glarial's Tomb.", ghostspeak, ringOfVis);
		talkToValdez.addDialogStep("Tell me your story");

		talkToRennard = new NpcStep(this, NpcID.MYSTERIOUS_GHOST, new WorldPoint(2556, 3445, 0), "Talk to the Mysterious Ghost Rennard.", ghostspeak, ringOfVis);
		talkToRennard.addDialogStep("Tell me your story");

		talkToKharrim = new NpcStep(this, NpcID.MYSTERIOUS_GHOST_3453, new WorldPoint(2556, 3445, 0), "Talk to the Mysterious Ghost Kharrim.", ghostspeak, ringOfVis);
		talkToKharrim.addDialogStep("Tell me your story");

		talkToLennissa = new NpcStep(this, NpcID.MYSTERIOUS_GHOST_3454, new WorldPoint(2556, 3445, 0), "Talk to the Mysterious Ghost Lennissa.", ghostspeak, ringOfVis);
		talkToLennissa.addDialogStep("Tell me your story");

		talkToDhalak = new NpcStep(this, NpcID.MYSTERIOUS_GHOST_3451, new WorldPoint(2556, 3445, 0), "Talk to the Mysterious Ghost Dhalak.", ghostspeak, ringOfVis);
		talkToDhalak.addDialogStep("Tell me your story");

		talkToViggora = new NpcStep(this, NpcID.MYSTERIOUS_GHOST_3455, new WorldPoint(2556, 3445, 0), "Talk to the Mysterious Ghost Viggora.", ghostspeak, ringOfVis);
		talkToViggora.addDialogStep("Tell me your story");

		updateSteps(pathID);

		goUpstairsMonastery = new ObjectStep(this, ObjectID.LADDER_2641, new WorldPoint(3057, 3483, 0), "Talk to the Mysterious Ghost upstairs in the Edgeville Monastery.");

		goDownIntoEdgevilleDungeon = new ObjectStep(this, ObjectID.TRAPDOOR_1579, new WorldPoint(3097, 3468, 0), "Talk to the Mysterious Ghost in the Edgeville Wilderness Dungeon, near the Earth Warriors.");
		goDownIntoEdgevilleDungeon.addAlternateObjects(ObjectID.TRAPDOOR_1581);

		goUpstairsSlayerTower = new ObjectStep(this, ObjectID.STAIRCASE, new WorldPoint(3436, 3538, 0), "Talk to the Mysterious Ghost upstairs in the Slayer Tower, near the Infernal Mages.");

		goUpstairsRoguesCastle = new ObjectStep(this, ObjectID.STAIRCASE_14735, new WorldPoint(3281, 3937, 0), "Talk to the Mysterious Ghost Viggora upstairs in the Rogues' Castle in 54 Wilderness.");

		goUpstairsPartyRoom = new ObjectStep(this, ObjectID.STAIRCASE_24249, new WorldPoint(3054, 3384, 0), "Talk to the Mysterious Ghost upstairs in the Falador Party Room.");
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
			talkToLennissa.setWorldPoint(2846, 3349, 0);

			talkToDhalak.setText("Talk to the Mysterious Ghost in the Wizard's Tower ground floor.");
			talkToDhalak.setWorldPoint(3109, 3163, 1);

			talkToViggora.setText("Talk to the Mysterious Ghost Viggora upstairs in the Rogues' Castle in 54 Wilderness.");
			talkToViggora.setWorldPoint(3295, 3934, 1);
		} else if (pathID == 2)
		{
			talkToRennard.setText("Talk to the Mysterious Ghost in the Bandit Camp in the Wilderness.");
			talkToRennard.setWorldPoint(3031, 3703, 0);

			talkToKharrim.setText("Talk to the Mysterious Ghost in the Graveyard of Shadows in the Wilderness.");
			talkToKharrim.setWorldPoint(3160, 3670, 0);

			talkToLennissa.setText("Talk to the Mysterious Ghost on the south of the Port Sarim dock.");
			talkToLennissa.setWorldPoint(3041, 3203, 0);

			talkToDhalak.setText("Talk to the Mysterious Ghost upstairs in the Edgeville Monastery.");
			talkToDhalak.setWorldPoint(3052, 3497, 1);

			talkToViggora.setText("Talk to the Mysterious Ghost upstairs in the Slayer Tower, near the Infernal Mages.");
			talkToViggora.setWorldPoint(3447, 3547, 1);

		} else if (pathID == 3)
		{
			talkToRennard.setText("Talk to the Mysterious Ghost in the Bandit Camp in the desert.");
			talkToRennard.setWorldPoint(3163, 2981, 0);

			talkToKharrim.setText("Talk to the Mysterious Ghost in the centre of the Lava Maze.");
			talkToKharrim.setWorldPoint(3076, 3861, 0);

			talkToLennissa.setText("Talk to the Mysterious Ghost in the west of the Tree Gnome Stronghold.");
			talkToLennissa.setWorldPoint(2396, 3476, 0);

			talkToDhalak.setText("Talk to the Mysterious Ghost upstairs in the Falador Party Room.");
			talkToDhalak.setWorldPoint(3052, 3378, 1);

			talkToViggora.setText("Talk to the Mysterious Ghost in the Edgeville Wilderness Dungeon, near the Earth Warriors.");
			talkToViggora.setWorldPoint(3121, 9995, 0);
		}
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(ringOfVis, ghostspeak));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Learn about the Empty Lord",
			new ArrayList<>(Arrays.asList(talkToValdez, talkToRennard, talkToKharrim, talkToLennissa, talkToDhalak, talkToViggora)), ghostspeak, ringOfVis));

		return allSteps;
	}
}
