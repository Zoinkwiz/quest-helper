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
package com.questhelper.quests.sheepherder;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestTile;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
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
	quest = QuestHelperQuest.SHEEP_HERDER
)
public class SheepHerder extends BasicQuestHelper
{
	//Items Required
	ItemRequirement coins;

	//Items Recommended
	ItemRequirement energyRestore;

	ItemRequirement plagueJacket, plagueTrousers, cattleprod, sheepFeed, bones1, bones2, bones3, bones4;

	Requirement inEnclosure, sheep1InEnclosure, sheep2InEnclosure, sheep3InEnclosure, sheep4InEnclosure,
		sheep1HasBones, sheep2HasBones, sheep3HasBones, sheep4HasBones, sheep1Burned, sheep2Burned, sheep3Burned,
		sheep4Burned, allSheepBonesObtained, allSheepBurned, bonesNearby;

	DetailedQuestStep talkToHalgrive, talkToOrbon, enterEnclosure, pickupCattleprod, prodSheep1, prodSheep2,
	prodSheep3, prodSheep4, feedSheep, pickupBones, useBonesOnIncinerator, talkToHalgriveToFinish;

	Zone enclosure;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();
		steps.put(0, talkToHalgrive);
		steps.put(1, talkToOrbon);

		ConditionalStep goBurnSheep = new ConditionalStep(this, enterEnclosure);
		goBurnSheep.addStep(new Conditions(allSheepBurned), talkToHalgriveToFinish);
		goBurnSheep.addStep(new Conditions(allSheepBonesObtained), useBonesOnIncinerator);
		goBurnSheep.addStep(new Conditions(bonesNearby), pickupBones);
		goBurnSheep.addStep(new Conditions(sheep1InEnclosure, sheep2InEnclosure, sheep3InEnclosure,
				sheep4InEnclosure), feedSheep);
		goBurnSheep.addStep(new Conditions(cattleprod, sheep1InEnclosure, sheep2InEnclosure, sheep3InEnclosure),
			prodSheep4);
		goBurnSheep.addStep(new Conditions(cattleprod, sheep1InEnclosure, sheep2InEnclosure), prodSheep3);
		goBurnSheep.addStep(new Conditions(cattleprod, sheep1InEnclosure), prodSheep2);
		goBurnSheep.addStep(cattleprod, prodSheep1);
		goBurnSheep.addStep(inEnclosure, pickupCattleprod);
		steps.put(2, goBurnSheep);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		coins = new ItemRequirement("Coins", ItemCollections.COINS);

		energyRestore = new ItemRequirement("Energy restoring items", ItemCollections.RUN_RESTORE_ITEMS);

		plagueJacket = new ItemRequirement("Plague jacket", ItemID.PLAGUE_JACKET);
		plagueJacket.setTooltip("You can buy another from Doctor Orbon for 100 coins");
		plagueTrousers = new ItemRequirement("Plague trousers", ItemID.PLAGUE_TROUSERS);
		plagueTrousers.setTooltip("You can buy another from Doctor Orbon for 100 coins");
		cattleprod = new ItemRequirement("Cattle prod", ItemID.CATTLEPROD);
		sheepFeed = new ItemRequirement("Sheep feed", ItemID.SHEEP_FEED);
		sheepFeed.setTooltip("You can get more from Halgrive");
		bones1 = new ItemRequirement("Sheep bones 1", ItemID.SHEEP_BONES_1);
		bones2 = new ItemRequirement("Sheep bones 2", ItemID.SHEEP_BONES_2);
		bones3 = new ItemRequirement("Sheep bones 3", ItemID.SHEEP_BONES_3);
		bones4 = new ItemRequirement("Sheep bones 4", ItemID.SHEEP_BONES_4);
	}

	private void setupZones()
	{
		enclosure = new Zone(new WorldPoint(2595, 3351, 0), new WorldPoint(2609, 3364, 0));
	}

	private void setupConditions()
	{
		inEnclosure = new ZoneRequirement(enclosure);

		sheep1Burned = new VarbitRequirement(2233, 6);
		sheep1HasBones = new Conditions(LogicType.OR,
			bones3,
			sheep1Burned
		);
		sheep1InEnclosure = new Conditions(LogicType.OR,
			new VarbitRequirement(2233, 1),
			sheep1HasBones
		);

		sheep2Burned = new VarbitRequirement(2234, 6);
		sheep2HasBones = new Conditions(LogicType.OR,
			bones4,
			sheep2Burned
		);
		sheep2InEnclosure = new Conditions(LogicType.OR,
			new VarbitRequirement(2234, 1),
			sheep2HasBones
		);

		sheep3Burned = new VarbitRequirement(2232, 6);
		sheep3HasBones = new Conditions(LogicType.OR,
			bones2,
			sheep3Burned
		);
		sheep3InEnclosure = new Conditions(LogicType.OR,
			new VarbitRequirement(2232, 1),
			sheep3HasBones
		);

		sheep4Burned = new VarbitRequirement(2231, 6);
		sheep4HasBones = new Conditions(LogicType.OR,
			bones1,
			sheep4Burned
		);
		sheep4InEnclosure = new Conditions(LogicType.OR,
			new VarbitRequirement(2231, 1),
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
		talkToHalgrive = new NpcStep(this, NpcID.COUNCILLOR_HALGRIVE_8765, new WorldPoint(2615, 3298, 0),
			"Talk to Councillor Halgrive outside the East Ardougne church.");
		talkToHalgrive.addDialogSteps("What's wrong?", "I can do that for you.");
		talkToOrbon = new NpcStep(this, NpcID.DOCTOR_ORBON, new WorldPoint(2616, 3306, 0),
			"Talk to Doctor Orbon in the East Ardougne Church.", coins.quantity(100));
		talkToOrbon.addDialogStep("Okay, I'll take it.");
		enterEnclosure = new ObjectStep(this, ObjectID.GATE_166, new WorldPoint(2594, 3362, 0),
			"Enter the enclosure north of Ardougne.", plagueJacket.equipped(), plagueTrousers.equipped());
		pickupCattleprod = new DetailedQuestStep(this, new WorldPoint(2604, 3357, 0), "Pickup the nearby cattleprod.",
			cattleprod);
		prodSheep1 = new NpcStep(this, NpcID.BLUE_SHEEP, new WorldPoint(2562, 3389, 0),
			"Prod one of the blue sheep north west of the enclosure to the enclosure gate.",
			true, cattleprod.equipped(), plagueJacket.equipped(), plagueTrousers.equipped());
		prodSheep1.addTileMarker(new QuestTile(new WorldPoint(2594, 3362, 0)));
		prodSheep2 = new NpcStep(this, NpcID.YELLOW_SHEEP, new WorldPoint(2610, 3389, 0),
			"Prod one of the yellow sheep north of the enclosure to the enclosure gate.",
			true, cattleprod.equipped(), plagueJacket.equipped(), plagueTrousers.equipped());
		prodSheep2.addTileMarker(new QuestTile(new WorldPoint(2594, 3362, 0)));
		prodSheep3 = new NpcStep(this, NpcID.GREEN_SHEEP, new WorldPoint(2621, 3367, 0),
		"Prod one of the green sheep east of the enclosure to the enclosure gate.",
			true, cattleprod.equipped(), plagueJacket.equipped(), plagueTrousers.equipped());
		prodSheep3.addTileMarker(new QuestTile(new WorldPoint(2594, 3362, 0)));
		prodSheep4 = new NpcStep(this, NpcID.RED_SHEEP, new WorldPoint(2609, 3347, 0),
		"Prod one of the red sheep south east of the enclosure to the enclosure gate.",
			true, cattleprod.equipped(), plagueJacket.equipped(), plagueTrousers.equipped());
		prodSheep4.addTileMarker(new QuestTile(new WorldPoint(2594, 3362, 0)));

		pickupBones = new ItemStep(this, "Pickup the bones.", bones1.hideConditioned(sheep4HasBones),
			bones2.hideConditioned(sheep3HasBones), bones3.hideConditioned(sheep1HasBones),
			bones4.hideConditioned(sheep2HasBones));
		feedSheep = new NpcStep(this, NpcID.BLUE_SHEEP, new WorldPoint(2597, 3361, 0),
			"Feed the sheep the sheep feed.", true, sheepFeed.highlighted());
		feedSheep.addIcon(ItemID.SHEEP_FEED);
		((NpcStep) feedSheep).setMaxRoamRange(3);
		((NpcStep) feedSheep).addAlternateNpcs(NpcID.GREEN_SHEEP, NpcID.RED_SHEEP, NpcID.YELLOW_SHEEP);

		useBonesOnIncinerator = new ObjectStep(this, ObjectID.INCINERATOR, new WorldPoint(2607, 3361, 0),
			"Pickup the bones and incinerate them.", bones1.highlighted().hideConditioned(sheep4Burned),
			bones2.highlighted().hideConditioned(sheep3Burned), bones3.highlighted().hideConditioned(sheep1Burned),
			bones4.highlighted().hideConditioned(sheep2Burned));
		useBonesOnIncinerator.addIcon(ItemID.SHEEP_BONES_1);
		talkToHalgriveToFinish = new NpcStep(this, NpcID.COUNCILLOR_HALGRIVE_8765, new WorldPoint(2615, 3298, 0),
			"Return to Councillor Halgrive.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Collections.singletonList(coins.quantity(100));
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Collections.singletonList(energyRestore);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(4);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("3,100 Coins", ItemID.COINS_995, 3100));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting out", Arrays.asList(talkToHalgrive, talkToOrbon), coins.quantity(100)));
		allSteps.add(new PanelDetails("Killing sheep", Arrays.asList(enterEnclosure, pickupCattleprod, prodSheep1,
			prodSheep2, prodSheep3, prodSheep4, feedSheep, pickupBones, useBonesOnIncinerator, talkToHalgriveToFinish),
			plagueJacket, plagueTrousers));

		return allSteps;
	}
}
