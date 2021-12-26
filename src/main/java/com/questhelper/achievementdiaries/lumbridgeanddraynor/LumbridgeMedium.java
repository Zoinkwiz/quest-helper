/*
 * Copyright (c) 2021, Obasill <https://github.com/Obasill>
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
package com.questhelper.achievementdiaries.lumbridgeanddraynor;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarbits;
import com.questhelper.Zone;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.TileStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.LUMBRIDGE_MEDIUM
)
public class LumbridgeMedium extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement crossbow, mithGrap, steelArrows, avasAttractor, coins, fairyAccess, earthRune,
		airRune, lawRune, earthTali, fireAccess, flyFishingRod, feathers, leather, needle, thread, axe, butterflyNet,
		implingJar, ess, bindingNeck;

	ItemRequirements avasAccumulator;
	// magic imbue

	// Quests required
	Requirement animalMagnetism, fairyTaleII, lostCity;

	Requirement notAlKaridRooftop, notGrappleLum, notUpgradeDevice, notWizardFairy, notTPlumb, notCatchSalmon,
		notCraftCoif, notChopWillow, notPickGardener, notChaeldarTask, notPuroImp, notCraftLava;

	QuestStep claimReward, moveToCowPen, moveToZanarisChaeldar, moveToZanarisPuro, moveToPuro, moveToLavaAltar, alKaridRooftop,
		grappleLum, upgradeDevice, wizardFairy, tpLumb, catchSalmon, craftCoif, chopWillow, pickGardener, chaeldarTask, craftLava;

	NpcStep puroImp;

	Zone puroPuro, cowPen, zanaris, lavaAltar;

	ZoneRequirement inPuroPuro, inCowPen, inZanaris, inLavaAltar;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doMedium = new ConditionalStep(this, claimReward);
		doMedium.addStep(new Conditions(notChaeldarTask, inZanaris), chaeldarTask);
		doMedium.addStep(notChaeldarTask, moveToZanarisChaeldar);
		doMedium.addStep(new Conditions(notPuroImp, inPuroPuro), puroImp);
		doMedium.addStep(new Conditions(notPuroImp, inZanaris), moveToPuro);
		doMedium.addStep(notPuroImp, moveToZanarisPuro);
		doMedium.addStep(notWizardFairy, wizardFairy);
		doMedium.addStep(notChopWillow, chopWillow);
		doMedium.addStep(notPickGardener, pickGardener);
		doMedium.addStep(notUpgradeDevice, upgradeDevice);
		doMedium.addStep(notTPlumb, tpLumb);
		doMedium.addStep(notCatchSalmon, catchSalmon);
		doMedium.addStep(new Conditions(notCraftCoif, inCowPen), craftCoif);
		doMedium.addStep(notCraftCoif, moveToCowPen);
		doMedium.addStep(notAlKaridRooftop, alKaridRooftop);
		doMedium.addStep(notGrappleLum, grappleLum);
		doMedium.addStep(new Conditions(notCraftLava, inLavaAltar), craftLava);
		doMedium.addStep(notCraftLava, moveToLavaAltar);

		return doMedium;
	}

	public void setupRequirements()
	{
		notAlKaridRooftop = new VarplayerRequirement(1194, false, 13);
		notGrappleLum = new VarplayerRequirement(1194, false, 14);
		notUpgradeDevice = new VarplayerRequirement(1194, false, 15);
		notWizardFairy = new VarplayerRequirement(1194, false, 16);
		notTPlumb = new VarplayerRequirement(1194, false, 17);
		notCatchSalmon = new VarplayerRequirement(1194, false, 18);
		notCraftCoif = new VarplayerRequirement(1194, false, 19);
		notChopWillow = new VarplayerRequirement(1194, false, 20);
		notPickGardener = new VarplayerRequirement(1194, false, 21);
		notChaeldarTask = new VarplayerRequirement(1194, false, 22);
		notPuroImp = new VarplayerRequirement(1194, false, 23);
		notCraftLava = new VarplayerRequirement(1194, false, 24);

		crossbow = new ItemRequirement("A crossbow", ItemCollections.getCrossbows()).showConditioned(notGrappleLum);
		mithGrap = new ItemRequirement("Mith grapple", ItemID.MITH_GRAPPLE_9419).showConditioned(notGrappleLum);
		earthTali = new ItemRequirement("Earth talisman", ItemID.EARTH_TALISMAN).showConditioned(notCraftLava);
		fireAccess = new ItemRequirement("Access to fire altar", ItemCollections.getFireAltar()).showConditioned(notCraftLava);
		earthRune = new ItemRequirement("Earth rune", ItemID.EARTH_RUNE)
			.showConditioned(new Conditions(LogicType.OR, notCraftLava, notTPlumb));
		ess = new ItemRequirement("Essence", ItemCollections.getEssenceLow()).showConditioned(notCraftLava);
		bindingNeck = new ItemRequirement("Binding necklace", ItemID.BINDING_NECKLACE).showConditioned(notCraftLava);
		feathers = new ItemRequirement("Feathers", ItemID.FEATHER).showConditioned(notCatchSalmon);
		flyFishingRod = new ItemRequirement("Fly fishing rod", ItemID.FLY_FISHING_ROD).showConditioned(notCatchSalmon);
		needle = new ItemRequirement("Needle", ItemID.NEEDLE).showConditioned(notCraftCoif);
		thread = new ItemRequirement("Thread", ItemID.THREAD).showConditioned(notCraftCoif);
		leather = new ItemRequirement("Leather", ItemID.LEATHER).showConditioned(notCraftCoif);
		lawRune = new ItemRequirement("Law rune", ItemID.LAW_RUNE).showConditioned(notTPlumb);
		airRune = new ItemRequirement("Air rune", ItemID.AIR_RUNE).showConditioned(notTPlumb);
		steelArrows = new ItemRequirement("Steel arrows", ItemID.STEEL_ARROW).showConditioned(notUpgradeDevice);
		coins = new ItemRequirement("999 Coins", ItemCollections.getCoins()).showConditioned(notUpgradeDevice);
		avasAttractor = new ItemRequirement("Ava's Attractor", ItemID.AVAS_ATTRACTOR).showConditioned(notUpgradeDevice);
		avasAccumulator = new ItemRequirements(LogicType.OR, "999 Coins or Ava's Attractor", coins.quantity(999),
			avasAttractor);
		axe = new ItemRequirement("Any axe", ItemCollections.getAxes()).showConditioned(notChopWillow);
		fairyAccess = new ItemRequirement("Lunar or Dramen staff", ItemCollections.getFairyStaff())
			.showConditioned(new Conditions(LogicType.OR, notChaeldarTask, notPuroImp, notWizardFairy));
		butterflyNet = new ItemRequirement("Butterfly net", ItemID.BUTTERFLY_NET).showConditioned(notPuroImp);
		implingJar = new ItemRequirement("Impling jar", ItemID.IMPLING_JAR).showConditioned(notPuroImp);


		inLavaAltar = new ZoneRequirement(lavaAltar);
		inPuroPuro = new ZoneRequirement(puroPuro);
		inCowPen = new ZoneRequirement(cowPen);
		inZanaris = new ZoneRequirement(zanaris);

		fairyTaleII = new VarbitRequirement(QuestVarbits.QUEST_FAIRYTALE_II_CURE_A_QUEEN.getId(), Operation.GREATER_EQUAL, 40, "Partial completion of Fairytale II for access to fairy rings");
		animalMagnetism = new QuestRequirement(QuestHelperQuest.ANIMAL_MAGNETISM, QuestState.FINISHED);
		lostCity = new QuestRequirement(QuestHelperQuest.LOST_CITY, QuestState.FINISHED);
	}

	public void loadZones()
	{
		lavaAltar = new Zone(new WorldPoint(2553, 4863, 0), new WorldPoint(2623, 4802, 0));
		puroPuro = new Zone(new WorldPoint(2561, 4350, 0), new WorldPoint(2623, 4289, 0));
		cowPen = new Zone(new WorldPoint(3253, 3298, 0), new WorldPoint(3265, 3255, 0));
		zanaris = new Zone(new WorldPoint(2369, 4481, 0), new WorldPoint(2497, 4352, 0));
	}

	public void setupSteps()
	{
		alKaridRooftop = new ObjectStep(this, ObjectID.ROUGH_WALL_11633, new WorldPoint(3273, 3195, 0),
			"Complete the Al Karid Rooftop Course.");

		grappleLum = new ObjectStep(this, ObjectID.BROKEN_RAFT, new WorldPoint(3252, 3179, 0),
			"Grapple across the River Lum.", mithGrap.equipped(), crossbow.equipped());

		moveToLavaAltar = new ObjectStep(this, 34817, new WorldPoint(3313, 3255, 0),
			"Enter the fire altar north of Al Kharid.", fireAccess);
		craftLava = new ObjectStep(this, ObjectID.ALTAR_34764, new WorldPoint(2585, 4838, 0),
			"Use an earth talisman on the fire altar.", earthTali.highlighted(), ess, earthRune);
		craftLava.addIcon(ItemID.EARTH_TALISMAN);

		catchSalmon = new NpcStep(this, NpcID.ROD_FISHING_SPOT_1527, new WorldPoint(3241, 3248, 0),
			"Catch a salmon in the River Lum.", feathers.quantity(10), flyFishingRod);

		moveToCowPen = new TileStep(this, new WorldPoint(3257, 3267, 0),
			"Enter the cow pen in Lumbridge.", thread, needle, leather);
		craftCoif = new ItemStep(this, "Craft a coif.", thread, needle.highlighted(), leather.highlighted());

		tpLumb = new DetailedQuestStep(this, "Cast the Teleport to Lumbridge spell.", airRune.quantity(3),
			earthRune.quantity(1), lawRune.quantity(1));

		upgradeDevice = new NpcStep(this, NpcID.AVA, new WorldPoint(3093, 3357, 0),
			"Buy an Ava's Accumulator from Ava in the Draynor Manor.", avasAccumulator, steelArrows.quantity(75));

		pickGardener = new NpcStep(this, NpcID.MARTIN_THE_MASTER_GARDENER, new WorldPoint(3077, 3263, 0),
			"Pickpocket Martin the Master Gardener in Draynor Village.");

		chopWillow = new ObjectStep(this, ObjectID.WILLOW, new WorldPoint(3089, 3235, 0),
			"Chop some Willow logs in Draynor Village.", axe);

		moveToZanarisChaeldar = new ObjectStep(this, ObjectID.DOOR_2406, new WorldPoint(3202, 3169, 0),
			"Go to Zanaris through the shed in Lumbridge swamp " +
				"or any fairy ring in the world if you've partially completed Fairytale II.", fairyAccess.equipped());
		moveToZanarisPuro = new ObjectStep(this, ObjectID.DOOR_2406, new WorldPoint(3202, 3169, 0),
			"Go to Zanaris through the shed in Lumbridge swamp " +
				"or any fairy ring in the world if you've partially completed Fairytale II.", fairyAccess.equipped(),
			butterflyNet, implingJar);

		moveToPuro = new ObjectStep(this, ObjectID.CENTRE_OF_CROP_CIRCLE_24991, new WorldPoint(2427, 4446, 0),
			"Enter the centre of the crop circle in Zanaris.");
		puroImp = new NpcStep(this, NpcID.ESSENCE_IMPLING, new WorldPoint(2592, 4322, 0),
			"Catch an essence or eclectic impling in Puro-Puro.", true, butterflyNet, implingJar);
		puroImp.addAlternateNpcs(NpcID.ESSENCE_IMPLING_1649, NpcID.ECLECTIC_IMPLING, NpcID.ECLECTIC_IMPLING_1650);
		chaeldarTask = new NpcStep(this, NpcID.CHAELDAR, new WorldPoint(2446, 4430, 0),
			"Get a slayer task from Chaeldar in Zanaris.");

		wizardFairy = new ObjectStep(this, 29560, new WorldPoint(2412, 4434, 0),
			"Take the nearest fairy ring and travel to the Wizards' Tower (DIS).", fairyAccess.equipped());

		claimReward = new NpcStep(this, NpcID.HATIUS_COSAINTUS, new WorldPoint(3235, 3213, 0),
			"Talk to Hatius Cosaintus in Lumbridge to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(crossbow, mithGrap, earthTali, fireAccess, earthRune.quantity(2), ess, feathers.quantity(10), flyFishingRod, needle,
			thread, leather, lawRune.quantity(1), airRune.quantity(3), steelArrows.quantity(75), avasAccumulator, axe,
			fairyAccess, butterflyNet, implingJar);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Collections.singletonList(bindingNeck);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new CombatLevelRequirement(70));
		reqs.add(new SkillRequirement(Skill.AGILITY, 20));
		reqs.add(new SkillRequirement(Skill.CRAFTING, 38));
		reqs.add(new SkillRequirement(Skill.FISHING, 30));
		reqs.add(new SkillRequirement(Skill.HUNTER, 42));
		reqs.add(new SkillRequirement(Skill.MAGIC, 31));
		reqs.add(new SkillRequirement(Skill.RANGED, 50));
		reqs.add(new SkillRequirement(Skill.RUNECRAFT, 23));
		reqs.add(new SkillRequirement(Skill.STRENGTH, 19));
		reqs.add(new SkillRequirement(Skill.THIEVING, 38));
		reqs.add(new SkillRequirement(Skill.WOODCUTTING, 36));

		reqs.add(fairyTaleII);
		reqs.add(animalMagnetism);

		return reqs;
	}


	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails chaeldarSteps = new PanelDetails("Slayer Task from Chaeldar", Arrays.asList(moveToZanarisChaeldar,
			chaeldarTask), new CombatLevelRequirement(70), fairyAccess);
		chaeldarSteps.setDisplayCondition(notChaeldarTask);
		allSteps.add(chaeldarSteps);

		PanelDetails puroImpSteps = new PanelDetails("Catch Essence or Eclectic Imp", Arrays.asList(moveToZanarisPuro,
			moveToPuro, puroImp), new SkillRequirement(Skill.HUNTER, 42), lostCity, fairyAccess, butterflyNet, implingJar);
		puroImpSteps.setDisplayCondition(notPuroImp);
		allSteps.add(puroImpSteps);

		PanelDetails wizardsTowerSteps = new PanelDetails("Travel to Wizards' Tower by Fairy Ring",
			Collections.singletonList(wizardFairy), fairyTaleII, fairyAccess);
		wizardsTowerSteps.setDisplayCondition(notWizardFairy);
		allSteps.add(wizardsTowerSteps);

		PanelDetails chopWillowSteps = new PanelDetails("Chop Willow", Collections.singletonList(chopWillow),
			new SkillRequirement(Skill.WOODCUTTING, 30), axe);
		chopWillowSteps.setDisplayCondition(notChopWillow);
		allSteps.add(chopWillowSteps);

		PanelDetails pickpocketMasterGardenerSteps = new PanelDetails("Pickpocket Master Gardener",
			Collections.singletonList(pickGardener),new SkillRequirement(Skill.THIEVING, 38));
		pickpocketMasterGardenerSteps.setDisplayCondition(notPickGardener);
		allSteps.add(pickpocketMasterGardenerSteps);

		PanelDetails upgradeSteps = new PanelDetails("Ava's Accumulator", Collections.singletonList(upgradeDevice),
			new SkillRequirement(Skill.RANGED, 50), animalMagnetism, avasAccumulator, steelArrows.quantity(75));
		upgradeSteps.setDisplayCondition(notUpgradeDevice);
		allSteps.add(upgradeSteps);

		PanelDetails tpLumbSteps = new PanelDetails("Teleport to Lumbridge", Collections.singletonList(tpLumb));
		tpLumbSteps.setDisplayCondition(notTPlumb);
		allSteps.add(tpLumbSteps);

		PanelDetails catchSalmonSteps = new PanelDetails("Catch Salmon", Collections.singletonList(catchSalmon),
			new SkillRequirement(Skill.FISHING, 30), feathers, flyFishingRod);
		catchSalmonSteps.setDisplayCondition(notCatchSalmon);
		allSteps.add(catchSalmonSteps);

		PanelDetails craftACoifSteps = new PanelDetails("Craft a coif", Arrays.asList(moveToCowPen, craftCoif),
			new SkillRequirement(Skill.CRAFTING, 38), leather, needle, thread);
		craftACoifSteps.setDisplayCondition(notCraftCoif);
		allSteps.add(craftACoifSteps);

		PanelDetails alKaridRooftopCourseSteps = new PanelDetails("Al Karid Rooftop Course",
			Collections.singletonList(alKaridRooftop), new SkillRequirement(Skill.AGILITY, 20));
		alKaridRooftopCourseSteps.setDisplayCondition(notAlKaridRooftop);
		allSteps.add(alKaridRooftopCourseSteps);

		PanelDetails grappleRiverLumSteps = new PanelDetails("Grapple River Lum",
			Collections.singletonList(grappleLum), new SkillRequirement(Skill.AGILITY, 8),
			new SkillRequirement(Skill.STRENGTH, 19), new SkillRequirement(Skill.RANGED, 37),
			crossbow, mithGrap);
		grappleRiverLumSteps.setDisplayCondition(notGrappleLum);
		allSteps.add(grappleRiverLumSteps);

		PanelDetails lavaRunesSteps = new PanelDetails("Craft Lava Runes", Arrays.asList(moveToLavaAltar, craftLava),
			new SkillRequirement(Skill.RUNECRAFT, 23), fireAccess, earthTali, earthRune, ess);
		lavaRunesSteps.setDisplayCondition(notCraftLava);
		allSteps.add(lavaRunesSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
