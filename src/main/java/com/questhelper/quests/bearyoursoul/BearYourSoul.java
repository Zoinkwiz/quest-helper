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
package com.questhelper.quests.bearyoursoul;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.DigStep;
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
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
quest = QuestHelperQuest.BEAR_YOUR_SOUL
)
public class BearYourSoul extends BasicQuestHelper
{
	//Items Required
	ItemRequirement spade, dustyKeyOr70AgilOrKeyMasterTeleport, damagedSoulBearer;

	Requirement inTaverleyDungeon, inKeyMaster;

	QuestStep findSoulJourneyAndRead, talkToAretha, arceuusChurchDig, goToTaverleyDungeon, enterCaveToKeyMaster, speakKeyMaster;

	//Zones
	Zone inTaverleyDungeonZone, inKeyMasterZone;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, findSoulJourneyAndRead);
		steps.put(1, talkToAretha);

		ConditionalStep repairSoulBearer = new ConditionalStep(this, arceuusChurchDig);
		repairSoulBearer.addStep(inKeyMaster, speakKeyMaster);
		repairSoulBearer.addStep(inTaverleyDungeon, enterCaveToKeyMaster);
		repairSoulBearer.addStep(damagedSoulBearer, goToTaverleyDungeon);

		steps.put(2, repairSoulBearer);

		return steps;
	}

	public void setupItemRequirements()
	{
		dustyKeyOr70AgilOrKeyMasterTeleport = new ItemRequirement("Dusty key, or another way to get into the deep Taverley Dungeon", ItemID.DUSTY_KEY);
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		damagedSoulBearer = new ItemRequirement("Damaged soul bearer", ItemID.DAMAGED_SOUL_BEARER);
	}

	public void loadZones()
	{
		inTaverleyDungeonZone = new Zone(new WorldPoint(2816, 9668, 0), new WorldPoint(2973, 9855, 0));
		inKeyMasterZone = new Zone(new WorldPoint(1289, 1236, 0), new WorldPoint(1333, 1274, 0));
	}

	public void setupConditions()
	{
		inTaverleyDungeon = new ZoneRequirement(inTaverleyDungeonZone);
		inKeyMaster = new ZoneRequirement(inKeyMasterZone);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("A Soul Bearer", ItemID.SOUL_BEARER, 1));
	}

	public void setupSteps()
	{
		findSoulJourneyAndRead = new DetailedQuestStep(this, new WorldPoint(1632, 3808, 0), "Go to the Arceuus library and find The Soul journey book in one of the bookcases, then read it. You can ask Biblia for help locating it, or make use of the Runelite Kourend Library plugin.");

		talkToAretha = new NpcStep(this, NpcID.ARETHA, new WorldPoint(1814, 3851, 0),
			"Talk to Aretha at the Soul Altar.");
		talkToAretha.addDialogStep("I've been reading your book...");
		talkToAretha.addDialogStep("Yes please.");

		arceuusChurchDig = new DigStep(this, new WorldPoint(1699, 3794, 0), "Go to the Arceuus church and dig for the Damaged soul bearer.");

		goToTaverleyDungeon = new ObjectStep(this, ObjectID.LADDER_16680, new WorldPoint(2884, 3397, 0), "Go to Taverley Dungeon, or teleport to the Key Master directly.", damagedSoulBearer, dustyKeyOr70AgilOrKeyMasterTeleport);

		enterCaveToKeyMaster = new ObjectStep(this, ObjectID.CAVE_26567, new WorldPoint(2874, 9846, 0), "Enter the cave to the Key Master.", damagedSoulBearer, dustyKeyOr70AgilOrKeyMasterTeleport);

		speakKeyMaster = new NpcStep(this, NpcID.KEY_MASTER, new WorldPoint(1310, 1251, 0),
			"Talk to Key Master in the Cerberus' Lair.", damagedSoulBearer);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		List<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(spade);
		reqs.add(dustyKeyOr70AgilOrKeyMasterTeleport);
		return reqs;
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Find the Soul journey book", Collections.singletonList(findSoulJourneyAndRead)));
		allSteps.add(new PanelDetails("Talk to Aretha", Collections.singletonList(talkToAretha)));
		allSteps.add(new PanelDetails("Dig up the Soul Bearer", Collections.singletonList(arceuusChurchDig), spade));
		allSteps.add(new PanelDetails("Have the Soul Bearer repaired", Arrays.asList(goToTaverleyDungeon, enterCaveToKeyMaster, speakKeyMaster), dustyKeyOr70AgilOrKeyMasterTeleport));
		return allSteps;
	}
}
