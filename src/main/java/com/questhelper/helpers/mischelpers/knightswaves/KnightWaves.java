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
package com.questhelper.helpers.mischelpers.knightswaves;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.SpriteID;

import java.util.*;

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

		initializeRequirements();
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

	@Override
	protected void setupRequirements()
	{
		floor1 = new Zone(new WorldPoint(2740, 3480, 1), new WorldPoint(2770, 3518, 1));
		floor2 = new Zone(new WorldPoint(2740, 3480, 2), new WorldPoint(2770, 3518, 2));
		room = new Zone(new WorldPoint(2752, 3502, 2), new WorldPoint(2764, 3513, 2));
		onFloor1 = new ZoneRequirement(floor1);
		onFloor2 = new ZoneRequirement(floor2);
		inRoom = new ZoneRequirement(room);

		combatGear = new ItemRequirement("Melee combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getMeleeCombatGear());
		poisonedWeapon = new ItemRequirement("Poisoned weapon such as Dragon dagger (p++)", ItemID.DRAGON_DAGGER_P__);
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, 25);
		potions = new ItemRequirement("Attack and strength potions for boost", -1, -1);

		talkedToSquire = new VarbitRequirement(3908, 1);
	}

	public void setupSteps()
	{

		goToFloor1 = new ObjectStep(this, ObjectID.KR_CAM_LADDER, new WorldPoint(2747, 3493, 0),
			"Climb the south west ladder of Camelot.");
		goToFloor2 = new ObjectStep(this, ObjectID.KR_CAM_LADDER, new WorldPoint(2749, 3491, 1),
			"Climb up to the roof.");
		talkToSquire = new NpcStep(this, NpcID.KR_SQUIRE, new WorldPoint(2750, 3507, 2),
			"");

		talkToSquireSteps = new ConditionalStep(this, goToFloor1, "Talk to the Squire on the roof of Camelot.",
			combatGear, poisonedWeapon, food, potions);
		talkToSquireSteps.addStep(onFloor2, talkToSquire);
		talkToSquireSteps.addStep(onFloor1, goToFloor2);

		enterGrounds = new ObjectStep(this, ObjectID.KR_CAM_WAVE_DOUBLEDOORL, new WorldPoint(2751, 3508, 2),
			"Enter the room to fight.");

		killKnights = new NpcStep(this, NpcID.KR_KNIGHT6, new WorldPoint(2757, 3507, 2),
			"", true);
		((NpcStep) killKnights).addAlternateNpcs(NpcID.KR_CAM_GAWAIN,
			NpcID.KR_CAM_BEDIVERE, NpcID.KR_KNIGHT1,
			NpcID.KR_CAM_PELLEAS, NpcID.KR_CAM_PELLEAS_JAIL, NpcID.KR_KNIGHT2,
			NpcID.KR_CAM_TRISTRAM, NpcID.KR_KNIGHT3,
			NpcID.KR_CAM_PALOMEDES, NpcID.KR_KNIGHT4,
			NpcID.KR_CAM_LUCAN, NpcID.KR_KNIGHT5,
			NpcID.KR_CAM_KAY, NpcID.KR_CAM_KAY_JAIL, NpcID.KR_KNIGHT7,
			NpcID.KR_CAM_LANCELOT, NpcID.KR_KNIGHT8);
		((NpcStep) killKnights).addSafeSpots(new WorldPoint(2752, 3511, 2));
		((NpcStep) killKnights).addTileMarker(new WorldPoint(2753, 3510, 2), SpriteID.Mapfunction.HELMET_SHOP);

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
