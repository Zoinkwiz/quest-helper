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
package com.questhelper.helpers.quests.sheepherder;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitBuilder;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.tools.QuestTile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

public class SheepHerder extends BasicQuestHelper
{
	// Required items
	ItemRequirement coins100;

	// Recommended items
	ItemRequirement energyRestore;

	// Mid-quest item requirements
	ItemRequirement sheepFeed;
	ItemRequirement plagueJacket;
	ItemRequirement plagueTrousers;
	ItemRequirement cattleprod;
	ItemRequirement bones1;
	ItemRequirement bones2;
	ItemRequirement bones3;
	ItemRequirement bones4;

	// Zones
	Zone enclosure;

	// Miscellaneous requirements
	ZoneRequirement inEnclosure;
	Conditions sheep1InEnclosure;
	Conditions sheep2InEnclosure;
	Conditions sheep3InEnclosure;
	Conditions sheep4InEnclosure;
	Conditions sheep1HasBones;
	Conditions sheep2HasBones;
	Conditions sheep3HasBones;
	Conditions sheep4HasBones;
	VarbitRequirement sheep1Burned;
	VarbitRequirement sheep2Burned;
	VarbitRequirement sheep3Burned;
	VarbitRequirement sheep4Burned;
	Conditions allSheepBonesObtained;
	Conditions allSheepBurned;
	Conditions bonesNearby;

	// Steps
	NpcStep talkToHalgrive;
	NpcStep talkToOrbon;
	ObjectStep enterEnclosure;
	DetailedQuestStep pickupCattleprod;
	NpcStep prodSheep1;
	NpcStep prodSheep2;
	NpcStep prodSheep3;
	NpcStep prodSheep4;
	NpcStep feedSheep;
	ItemStep pickupBones;
	ObjectStep useBonesOnIncinerator;
	NpcStep talkToHalgriveToFinish;


	@Override
	protected void setupZones()
	{
		enclosure = new Zone(new WorldPoint(2595, 3351, 0), new WorldPoint(2609, 3364, 0));
	}

	@Override
	protected void setupRequirements()
	{
		var sheep1State = new VarbitBuilder(VarbitID.SHEEPHERDER_SHEEP_C);
		var sheep2State = new VarbitBuilder(VarbitID.SHEEPHERDER_SHEEP_D);
		var sheep3State = new VarbitBuilder(VarbitID.SHEEPHERDER_SHEEP_B);
		var sheep4State = new VarbitBuilder(VarbitID.SHEEPHERDER_SHEEP_A);

		coins100 = new ItemRequirement("Coins", ItemCollections.COINS, 100);

		energyRestore = new ItemRequirement("Energy restoring items", ItemCollections.RUN_RESTORE_ITEMS);

		sheepFeed = new ItemRequirement("Sheep feed", ItemID.POISONED_FEED);
		sheepFeed.setTooltip("You can get more from Halgrive");
		plagueJacket = new ItemRequirement("Plague jacket", ItemID.PLAGUE_JACKET);
		plagueJacket.setTooltip("You can buy another from Doctor Orbon for 100 coins");
		plagueTrousers = new ItemRequirement("Plague trousers", ItemID.PLAGUE_TROUSERS);
		plagueTrousers.setTooltip("You can buy another from Doctor Orbon for 100 coins");
		cattleprod = new ItemRequirement("Cattle prod", ItemID.CATTLEPROD);
		bones1 = new ItemRequirement("Sheep bones 1", ItemID.SHEEPBONESA);
		bones2 = new ItemRequirement("Sheep bones 2", ItemID.SHEEPBONESB);
		bones3 = new ItemRequirement("Sheep bones 3", ItemID.SHEEPBONESC);
		bones4 = new ItemRequirement("Sheep bones 4", ItemID.SHEEPBONESD);
		inEnclosure = new ZoneRequirement(enclosure);

		sheep1Burned = sheep1State.eq(6);
		sheep1HasBones = new Conditions(LogicType.OR,
			bones3,
			sheep1Burned
		);
		sheep1InEnclosure = new Conditions(LogicType.OR,
			sheep1State.eq(1),
			sheep1HasBones
		);

		sheep2Burned = sheep2State.eq(6);
		sheep2HasBones = new Conditions(LogicType.OR,
			bones4,
			sheep2Burned
		);
		sheep2InEnclosure = new Conditions(LogicType.OR,
			sheep2State.eq(1),
			sheep2HasBones
		);

		sheep3Burned = sheep3State.eq(6);
		sheep3HasBones = new Conditions(LogicType.OR,
			bones2,
			sheep3Burned
		);
		sheep3InEnclosure = new Conditions(LogicType.OR,
			sheep3State.eq(1),
			sheep3HasBones
		);

		sheep4Burned = sheep4State.eq(6);
		sheep4HasBones = new Conditions(LogicType.OR,
			bones1,
			sheep4Burned
		);
		sheep4InEnclosure = new Conditions(LogicType.OR,
			sheep4State.eq(1),
			sheep4HasBones
		);

		allSheepBonesObtained = new Conditions(sheep1HasBones, sheep2HasBones, sheep3HasBones, sheep4HasBones);
		allSheepBurned = new Conditions(sheep1Burned, sheep2Burned, sheep3Burned, sheep4Burned);

		bonesNearby = new Conditions(LogicType.OR,
			new ItemOnTileRequirement(bones1),
			new ItemOnTileRequirement(bones2),
			new ItemOnTileRequirement(bones3),
			new ItemOnTileRequirement(bones4)
		);
	}

	public void setupSteps()
	{
		talkToHalgrive = new NpcStep(this, NpcID.COUNCILLOR_HALGRIVE_VIS, new WorldPoint(2615, 3298, 0),
			"Talk to Councillor Halgrive outside the East Ardougne church.");
		talkToHalgrive.addDialogSteps("What's wrong?", "Yes.");

		talkToOrbon = new NpcStep(this, NpcID.DOCTOR_ORBON, new WorldPoint(2616, 3306, 0),
			"Talk to Doctor Orbon in the East Ardougne Church.", coins100);
		talkToOrbon.addDialogStep("Okay, I'll take it.");

		enterEnclosure = new ObjectStep(this, ObjectID.PLAGUESHEEP_GATEL, new WorldPoint(2594, 3362, 0),
			"Enter the enclosure north of Ardougne wearing the plague jacket and trousers.", plagueJacket.equipped(), plagueTrousers.equipped());

		pickupCattleprod = new DetailedQuestStep(this, new WorldPoint(2604, 3357, 0), "Pickup the nearby cattleprod.",
			cattleprod);
		prodSheep1 = new NpcStep(this, NpcID.HERDER_PLAGUESHEEP_3, new WorldPoint(2562, 3389, 0),
			"Prod one of the blue sheep north west of the enclosure to the enclosure gate.",
			true, cattleprod.equipped(), plagueJacket.equipped(), plagueTrousers.equipped());
		prodSheep1.addTileMarker(new QuestTile(new WorldPoint(2594, 3362, 0)));
		prodSheep2 = new NpcStep(this, NpcID.HERDER_PLAGUESHEEP_4, new WorldPoint(2610, 3389, 0),
			"Prod one of the yellow sheep north of the enclosure to the enclosure gate.",
			true, cattleprod.equipped(), plagueJacket.equipped(), plagueTrousers.equipped());
		prodSheep2.addTileMarker(new QuestTile(new WorldPoint(2594, 3362, 0)));
		prodSheep3 = new NpcStep(this, NpcID.HERDER_PLAGUESHEEP_2, new WorldPoint(2621, 3367, 0),
			"Prod one of the green sheep east of the enclosure to the enclosure gate.",
			true, cattleprod.equipped(), plagueJacket.equipped(), plagueTrousers.equipped());
		prodSheep3.addTileMarker(new QuestTile(new WorldPoint(2594, 3362, 0)));
		prodSheep4 = new NpcStep(this, NpcID.HERDER_PLAGUESHEEP_1, new WorldPoint(2609, 3347, 0),
			"Prod one of the red sheep south east of the enclosure to the enclosure gate.",
			true, cattleprod.equipped(), plagueJacket.equipped(), plagueTrousers.equipped());
		prodSheep4.addTileMarker(new QuestTile(new WorldPoint(2594, 3362, 0)));

		pickupBones = new ItemStep(this, "Pickup the bones.", bones1.hideConditioned(sheep4HasBones),
			bones2.hideConditioned(sheep3HasBones), bones3.hideConditioned(sheep1HasBones),
			bones4.hideConditioned(sheep2HasBones));
		feedSheep = new NpcStep(this, NpcID.HERDER_PLAGUESHEEP_3, new WorldPoint(2597, 3361, 0),
			"Feed the sheep the sheep feed.", true, sheepFeed.highlighted());
		feedSheep.addIcon(ItemID.POISONED_FEED);
		feedSheep.setMaxRoamRange(3);
		feedSheep.addAlternateNpcs(NpcID.HERDER_PLAGUESHEEP_2, NpcID.HERDER_PLAGUESHEEP_1, NpcID.HERDER_PLAGUESHEEP_4);

		useBonesOnIncinerator = new ObjectStep(this, ObjectID.PLAGUESHEEP_FURNACE, new WorldPoint(2607, 3361, 0),
			"Pickup the bones and incinerate them.", bones1.highlighted().hideConditioned(sheep4Burned),
			bones2.highlighted().hideConditioned(sheep3Burned), bones3.highlighted().hideConditioned(sheep1Burned),
			bones4.highlighted().hideConditioned(sheep2Burned));
		useBonesOnIncinerator.addIcon(ItemID.SHEEPBONESA);
		talkToHalgriveToFinish = new NpcStep(this, NpcID.COUNCILLOR_HALGRIVE_VIS, new WorldPoint(2615, 3298, 0),
			"Return to Councillor Halgrive.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToHalgrive);

		steps.put(1, talkToOrbon);

		var goBurnSheep = new ConditionalStep(this, enterEnclosure);
		goBurnSheep.addStep(allSheepBurned, talkToHalgriveToFinish);
		goBurnSheep.addStep(allSheepBonesObtained, useBonesOnIncinerator);
		goBurnSheep.addStep(bonesNearby, pickupBones);
		goBurnSheep.addStep(and(sheep1InEnclosure, sheep2InEnclosure, sheep3InEnclosure, sheep4InEnclosure), feedSheep);
		goBurnSheep.addStep(and(cattleprod, sheep1InEnclosure, sheep2InEnclosure, sheep3InEnclosure), prodSheep4);
		goBurnSheep.addStep(and(cattleprod, sheep1InEnclosure, sheep2InEnclosure), prodSheep3);
		goBurnSheep.addStep(and(cattleprod, sheep1InEnclosure), prodSheep2);
		goBurnSheep.addStep(cattleprod, prodSheep1);
		goBurnSheep.addStep(inEnclosure, pickupCattleprod);
		steps.put(2, goBurnSheep);

		return steps;
	}


	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			coins100
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			energyRestore
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(4);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Coins", ItemID.COINS, 3100)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting out", List.of(
			talkToHalgrive,
			talkToOrbon
		), List.of(
			coins100
		)));

		sections.add(new PanelDetails("Killing sheep", List.of(
			enterEnclosure,
			pickupCattleprod,
			prodSheep1,
			prodSheep2,
			prodSheep3,
			prodSheep4,
			feedSheep,
			pickupBones,
			useBonesOnIncinerator,
			talkToHalgriveToFinish
		), List.of(
			plagueJacket,
			plagueTrousers
		)));

		return sections;
	}
}
