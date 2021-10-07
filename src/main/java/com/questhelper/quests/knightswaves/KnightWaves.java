/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.quests.knightswaves;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.ObjectStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.QuestDescriptor;

@QuestDescriptor(
	quest = QuestHelperQuest.KNIGHT_WAVES_TRAINING_GROUNDS
)
public class KnightWaves extends BasicQuestHelper
{
	//Items Required
	ItemRequirement combatGear, poisonedWeapon, food, potions;

	QuestStep talkToSquire, enterGrounds, killKnights, goToFloor1, goToFloor2;

	ConditionalStep talkToSquireSteps, killKnightsSteps;

	Zone floor1, floor2, room;

	Requirement onFloor2, onFloor1, inRoom, talkedToSquire;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();

		setupRequirements();
		setupSteps();

		ConditionalStep doQuest = new ConditionalStep(this, talkToSquireSteps);
		doQuest.addStep(talkedToSquire, killKnightsSteps);

		steps.put(0, doQuest);
		steps.put(1, doQuest);
		steps.put(2, doQuest);
		steps.put(3, doQuest);
		steps.put(4, doQuest);
		steps.put(5, doQuest);
		steps.put(6, doQuest);
		steps.put(7, doQuest);

		return steps;
	}

	private void setupRequirements()
	{
		floor1 = new Zone(new WorldPoint(2740, 3480, 1), new WorldPoint(2770, 3518, 1));
		floor2 = new Zone(new WorldPoint(2740, 3480, 2), new WorldPoint(2770, 3518, 2));
		room = new Zone(new WorldPoint(2752, 3502, 2), new WorldPoint(2764, 3513, 2));
		onFloor1 = new ZoneRequirement(floor1);
		onFloor2 = new ZoneRequirement(floor2);
		inRoom = new ZoneRequirement(room);

		combatGear = new ItemRequirement("Melee combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getMeleeCombatGear());
		poisonedWeapon = new ItemRequirement("Poisoned weapon such as Dragon dagger (p++)", ItemID.DRAGON_DAGGERP_5698);
		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), 25);
		potions = new ItemRequirement("Attack and strength potions for boost", -1, -1);

		talkedToSquire = new VarbitRequirement(3908, 1);
	}

	public void setupSteps()
	{

		goToFloor1 = new ObjectStep(this, ObjectID.LADDER_26107, new WorldPoint(2747, 3493, 0),
			"Climb the south west ladder of Camelot.");
		goToFloor2 = new ObjectStep(this, ObjectID.LADDER_26107, new WorldPoint(2747, 3493, 0),
			"Climb up to the roof.");
		talkToSquire = new NpcStep(this, NpcID.SQUIRE_4353, new WorldPoint(2750, 3507, 2),
			"");

		talkToSquireSteps = new ConditionalStep(this, goToFloor1, "Talk to the Squire on the roof of Camelot.",
			combatGear, poisonedWeapon, food, potions);
		talkToSquireSteps.addStep(onFloor2, talkToSquire);
		talkToSquireSteps.addStep(onFloor1, goToFloor2);

		enterGrounds = new ObjectStep(this, ObjectID.LARGE_DOOR_25595, new WorldPoint(2751, 3508, 2),
			"Enter the room to fight.");

		killKnights = new NpcStep(this, NpcID.SIR_GAWAIN_4356, new WorldPoint(2757, 3507, 2),
			"", true);
		((NpcStep) killKnights).addAlternateNpcs(NpcID.SIR_GAWAIN_4348,
			NpcID.SIR_BEDIVERE_4345, NpcID.SIR_BEDIVERE_4361,
			NpcID.SIR_PELLEAS_4347, NpcID.SIR_PELLEAS_4350, NpcID.SIR_PELLEAS_4360,
			NpcID.SIR_TRISTRAM_4346, NpcID.SIR_TRISTRAM_4359,
			NpcID.SIR_PALOMEDES_4343, NpcID.SIR_PALOMEDES_4358,
			NpcID.SIR_LUCAN_4342, NpcID.SIR_LUCAN_4357,
			NpcID.SIR_KAY_4349, NpcID.SIR_KAY_4352, NpcID.SIR_KAY_4355,
			NpcID.SIR_LANCELOT_4344, NpcID.SIR_LANCELOT_4354);
		((NpcStep) killKnights).addSafeSpots(new WorldPoint(2752, 3511, 2));
		((NpcStep) killKnights).addTileMarker(new WorldPoint(2753, 3510, 2), SpriteID.MAP_ICON_HELMET_SHOP);

		killKnightsSteps = new ConditionalStep(this, goToFloor1, "Defeat the 8 Knights of the Round Table in the room" +
			" on top of Camelot. It's recommended to flinch the knights on one of the dummies around the room, and " +
			"use a poisoned weapon to make the process even easier.",combatGear, poisonedWeapon, food, potions);
		killKnightsSteps.addStep(inRoom, killKnights);
		killKnightsSteps.addStep(onFloor2, enterGrounds);
		killKnightsSteps.addStep(onFloor1, goToFloor2);

	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.KINGS_RANSOM, QuestState.FINISHED));
		return req;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear, poisonedWeapon, food, potions);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.ATTACK, 20000),
				new ExperienceReward(Skill.STRENGTH, 20000),
				new ExperienceReward(Skill.DEFENCE, 20000),
				new ExperienceReward(Skill.HITPOINTS, 20000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Access to Chivalry Prayer (60 Prayer & 65 Defence)"),
				new UnlockReward("Access to Piety (70 Prayer & 70 Defence)"),
				new UnlockReward("Ability to change your spawn point to Camelot."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Complete the Knight Waves", Arrays.asList(talkToSquireSteps, killKnightsSteps),
			combatGear, poisonedWeapon, food, potions));
		return allSteps;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("8 Knights of the Round Table (levels 110-127)");
	}
}
