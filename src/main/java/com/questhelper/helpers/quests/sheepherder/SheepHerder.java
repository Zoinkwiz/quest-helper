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
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.var.VarbitBuilder;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
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
	ItemRequirement redSheepBones;
	ItemRequirement greenSheepBones;
	ItemRequirement blueSheepBones;
	ItemRequirement yellowSheepBones;

	// Zones
	Zone enclosure;

	// Miscellaneous requirements
	ZoneRequirement inEnclosure;

	Conditions blueSheepInEnclosure;
	Conditions blueSheepHasBones;
	VarbitRequirement blueSheepBurned;

	Conditions yellowSheepInEnclosure;
	Conditions yellowSheepHasBones;
	VarbitRequirement yellowSheepBurned;

	Conditions greenSheepInEnclosure;
	Conditions greenSheepHasBones;
	VarbitRequirement greenSheepBurned;

	Conditions redSheepInEnclosure;
	Conditions redSheepHasBones;
	VarbitRequirement redSheepBurned;

	Conditions allSheepBonesObtained;
	Conditions allSheepBurned;
	Conditions bonesNearby;

	// Steps
	NpcStep talkToHalgrive;
	NpcStep talkToOrbon;
	ObjectStep enterEnclosure;
	ItemStep pickupCattleprod;
	NpcStep prodBlueSheep;
	NpcStep prodYellowSheep;
	NpcStep prodGreenSheep;
	NpcStep prodRedSheep;
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
		// Sheep states:
		// 0 = Sheep is loose
		// 1 = Sheep has been prodded into the enclosure
		// 2 = Sheep has been killed by the poisoned sheep food
		// 6 = The bones of the sheep has been incinerated
		var blueSheepState = new VarbitBuilder(VarbitID.SHEEPHERDER_SHEEP_C);
		var yellowSheepState = new VarbitBuilder(VarbitID.SHEEPHERDER_SHEEP_D);
		var greenSheepState = new VarbitBuilder(VarbitID.SHEEPHERDER_SHEEP_B);
		var redSheepState = new VarbitBuilder(VarbitID.SHEEPHERDER_SHEEP_A);

		coins100 = new ItemRequirement("Coins", ItemCollections.COINS, 100);

		energyRestore = new ItemRequirement("Energy restoring items", ItemCollections.RUN_RESTORE_ITEMS);

		sheepFeed = new ItemRequirement("Sheep feed", ItemID.POISONED_FEED);
		sheepFeed.setTooltip("You can get more from Halgrive");
		plagueJacket = new ItemRequirement("Plague jacket", ItemID.PLAGUE_JACKET);
		plagueJacket.setTooltip("You can buy another from Doctor Orbon for 100 coins");
		plagueTrousers = new ItemRequirement("Plague trousers", ItemID.PLAGUE_TROUSERS);
		plagueTrousers.setTooltip("You can buy another from Doctor Orbon for 100 coins");
		cattleprod = new ItemRequirement("Cattle prod", ItemID.CATTLEPROD);
		redSheepBones = new ItemRequirement("Sheep bones 1", ItemID.SHEEPBONESA);
		greenSheepBones = new ItemRequirement("Sheep bones 2", ItemID.SHEEPBONESB);
		blueSheepBones = new ItemRequirement("Sheep bones 3", ItemID.SHEEPBONESC);
		yellowSheepBones = new ItemRequirement("Sheep bones 4", ItemID.SHEEPBONESD);
		inEnclosure = new ZoneRequirement(enclosure);

		blueSheepBurned = blueSheepState.eq(6);
		blueSheepHasBones = or(blueSheepBones, blueSheepBurned);
		blueSheepInEnclosure = or(blueSheepState.eq(1), blueSheepHasBones);

		yellowSheepBurned = yellowSheepState.eq(6);
		yellowSheepHasBones = or(yellowSheepBones, yellowSheepBurned);
		yellowSheepInEnclosure = or(yellowSheepState.eq(1), yellowSheepHasBones);

		greenSheepBurned = greenSheepState.eq(6);
		greenSheepHasBones = or(greenSheepBones, greenSheepBurned);
		greenSheepInEnclosure = or(greenSheepState.eq(1), greenSheepHasBones);

		redSheepBurned = redSheepState.eq(6);
		redSheepHasBones = or(redSheepBones, redSheepBurned);
		redSheepInEnclosure = or(redSheepState.eq(1), redSheepHasBones);

		allSheepBonesObtained = and(blueSheepHasBones, yellowSheepHasBones, greenSheepHasBones, redSheepHasBones);
		allSheepBurned = and(blueSheepBurned, yellowSheepBurned, greenSheepBurned, redSheepBurned);

		bonesNearby = or(
			new ItemOnTileRequirement(redSheepBones),
			new ItemOnTileRequirement(greenSheepBones),
			new ItemOnTileRequirement(blueSheepBones),
			new ItemOnTileRequirement(yellowSheepBones)
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

		pickupCattleprod = new ItemStep(this, new WorldPoint(2604, 3357, 0), "Pick up the nearby cattleprod.",
			cattleprod);
		prodBlueSheep = new NpcStep(this, NpcID.HERDER_PLAGUESHEEP_3, new WorldPoint(2562, 3389, 0),
			"Prod one of the blue sheep north west of the enclosure to the enclosure gate.",
			true, cattleprod.equipped(), plagueJacket.equipped(), plagueTrousers.equipped());
		prodBlueSheep.addTileMarker(new QuestTile(new WorldPoint(2594, 3362, 0)));
		prodYellowSheep = new NpcStep(this, NpcID.HERDER_PLAGUESHEEP_4, new WorldPoint(2610, 3389, 0),
			"Prod one of the yellow sheep north of the enclosure to the enclosure gate.",
			true, cattleprod.equipped(), plagueJacket.equipped(), plagueTrousers.equipped());
		prodYellowSheep.addTileMarker(new QuestTile(new WorldPoint(2594, 3362, 0)));
		prodGreenSheep = new NpcStep(this, NpcID.HERDER_PLAGUESHEEP_2, new WorldPoint(2621, 3367, 0),
			"Prod one of the green sheep east of the enclosure to the enclosure gate.",
			true, cattleprod.equipped(), plagueJacket.equipped(), plagueTrousers.equipped());
		prodGreenSheep.addTileMarker(new QuestTile(new WorldPoint(2594, 3362, 0)));
		prodRedSheep = new NpcStep(this, NpcID.HERDER_PLAGUESHEEP_1, new WorldPoint(2609, 3347, 0),
			"Prod one of the red sheep south east of the enclosure to the enclosure gate.",
			true, cattleprod.equipped(), plagueJacket.equipped(), plagueTrousers.equipped());
		prodRedSheep.addTileMarker(new QuestTile(new WorldPoint(2594, 3362, 0)));

		pickupBones = new ItemStep(this, "Pick up the bones.", redSheepBones.hideConditioned(redSheepHasBones),
			greenSheepBones.hideConditioned(greenSheepHasBones), blueSheepBones.hideConditioned(blueSheepHasBones),
			yellowSheepBones.hideConditioned(yellowSheepHasBones));
		feedSheep = new NpcStep(this, NpcID.HERDER_PLAGUESHEEP_3, new WorldPoint(2597, 3361, 0),
			"Feed the sheep the sheep feed.", true, sheepFeed.highlighted());
		feedSheep.addIcon(ItemID.POISONED_FEED);
		feedSheep.setMaxRoamRange(3);
		feedSheep.addAlternateNpcs(NpcID.HERDER_PLAGUESHEEP_2, NpcID.HERDER_PLAGUESHEEP_1, NpcID.HERDER_PLAGUESHEEP_4);

		useBonesOnIncinerator = new ObjectStep(this, ObjectID.PLAGUESHEEP_FURNACE, new WorldPoint(2607, 3361, 0),
			"Pick up the bones and incinerate them.", redSheepBones.highlighted().hideConditioned(redSheepBurned),
			greenSheepBones.highlighted().hideConditioned(greenSheepBurned), blueSheepBones.highlighted().hideConditioned(blueSheepBurned),
			yellowSheepBones.highlighted().hideConditioned(yellowSheepBurned));
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
		goBurnSheep.addStep(and(blueSheepInEnclosure, yellowSheepInEnclosure, greenSheepInEnclosure, redSheepInEnclosure), feedSheep);
		goBurnSheep.addStep(and(cattleprod, blueSheepInEnclosure, yellowSheepInEnclosure, greenSheepInEnclosure), prodRedSheep);
		goBurnSheep.addStep(and(cattleprod, blueSheepInEnclosure, yellowSheepInEnclosure), prodGreenSheep);
		goBurnSheep.addStep(and(cattleprod, blueSheepInEnclosure), prodYellowSheep);
		goBurnSheep.addStep(cattleprod, prodBlueSheep);
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
			prodBlueSheep,
			prodYellowSheep,
			prodGreenSheep,
			prodRedSheep,
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
