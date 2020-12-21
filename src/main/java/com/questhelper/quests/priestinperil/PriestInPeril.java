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
package com.questhelper.quests.priestinperil;

import com.questhelper.BankSlotIcons;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.requirements.ItemRequirements;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.PRIEST_IN_PERIL
)
public class PriestInPeril extends BasicQuestHelper
{
	ItemRequirement runeEssence, lotsOfRuneEssence, bucket, runePouches, varrockTeleport, weaponAndArmour, goldenKey, rangedMagedGear,
		murkyWater, ironKey, blessedWaterHighlighted, bucketHighlighted, goldenKeyHighlighted;

	ConditionForStep inUnderground, hasGoldenOrIronKey, inTempleGroundFloor, inTemple, inTempleFirstFloor, inTempleSecondFloor, hasBlessedOrMurkyWater, hasIronKey,
		hasBlessedWater;

	QuestStep talkToRoald, goToTemple, goDownToDog, killTheDog, climbUpAfterKillingDog, returnToKingRoald, returnToTemple, killMonk, talkToDrezel,
		goUpToFloorTwoTemple, goUpToFloorOneTemple, goDownToFloorOneTemple, goDownToGroundFloorTemple, enterUnderground, fillBucket, useKeyForKey,
		openDoor, useBlessedWater, blessWater, goUpWithWaterToSurface, goUpWithWaterToFirstFloor, goUpWithWaterToSecondFloor, talkToDrezelAfterFreeing,
		goDownToFloorOneAfterFreeing, goDownToGroundFloorAfterFreeing, enterUndergroundAfterFreeing, talkToDrezelUnderground, bringDrezelEssence;

	Zone underground, temple1, temple2, temple3, temple4, temple5, temple6, templeFloorOne, templeFloorTwo;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToRoald);
		steps.put(1, goToTemple);

		ConditionalStep goDownAndKillDog = new ConditionalStep(this, goDownToDog);
		goDownAndKillDog.addStep(inUnderground, killTheDog);

		steps.put(2, goDownAndKillDog);

		ConditionalStep reportKillingDog = new ConditionalStep(this, returnToKingRoald);
		reportKillingDog.addStep(inUnderground, climbUpAfterKillingDog);
		steps.put(3, reportKillingDog);

		ConditionalStep goTalkToDrezel = new ConditionalStep(this, returnToTemple);
		goTalkToDrezel.addStep(new Conditions(hasGoldenOrIronKey, inTempleSecondFloor), talkToDrezel);
		goTalkToDrezel.addStep(new Conditions(hasGoldenOrIronKey, inTempleFirstFloor), goUpToFloorTwoTemple);
		goTalkToDrezel.addStep(new Conditions(hasGoldenOrIronKey, inTempleGroundFloor), goUpToFloorOneTemple);
		goTalkToDrezel.addStep(inTemple, killMonk);
		steps.put(4, goTalkToDrezel);

		ConditionalStep goGetKey = new ConditionalStep(this, returnToTemple);
		goGetKey.addStep(new Conditions(hasIronKey, hasBlessedOrMurkyWater, inTempleSecondFloor), openDoor);
		goGetKey.addStep(new Conditions(hasIronKey, hasBlessedOrMurkyWater, inTempleFirstFloor), goUpWithWaterToSecondFloor);
		goGetKey.addStep(new Conditions(hasIronKey, hasBlessedOrMurkyWater, inUnderground), goUpWithWaterToSurface);
		goGetKey.addStep(new Conditions(hasIronKey, hasBlessedOrMurkyWater), goUpWithWaterToFirstFloor);
		goGetKey.addStep(new Conditions(hasIronKey, inUnderground), fillBucket);
		goGetKey.addStep(new Conditions(hasGoldenOrIronKey, inUnderground), useKeyForKey);
		goGetKey.addStep(new Conditions(hasGoldenOrIronKey, inTempleSecondFloor), goDownToFloorOneTemple);
		goGetKey.addStep(new Conditions(hasGoldenOrIronKey, inTempleFirstFloor), goDownToGroundFloorTemple);
		goGetKey.addStep(hasGoldenOrIronKey, enterUnderground);
		goGetKey.addStep(inTemple, killMonk);
		steps.put(5, goGetKey);

		ConditionalStep goGetWater = new ConditionalStep(this, enterUnderground);
		goGetWater.addStep(new Conditions(hasBlessedWater, inTempleSecondFloor), useBlessedWater);
		goGetWater.addStep(new Conditions(hasBlessedOrMurkyWater, inTempleSecondFloor), blessWater);
		goGetWater.addStep(new Conditions(hasBlessedOrMurkyWater, inTempleFirstFloor), goUpWithWaterToSecondFloor);
		goGetWater.addStep(new Conditions(hasBlessedOrMurkyWater, inUnderground), goUpWithWaterToSurface);
		goGetWater.addStep(hasBlessedOrMurkyWater, goUpWithWaterToFirstFloor);
		goGetWater.addStep(inUnderground, fillBucket);
		goGetWater.addStep(inTempleSecondFloor, goDownToFloorOneTemple);
		goGetWater.addStep(inTempleFirstFloor, goDownToGroundFloorTemple);
		steps.put(6, goGetWater);

		ConditionalStep goTalkToDrezelAfterFreeing = new ConditionalStep(this, goUpWithWaterToFirstFloor);
		goTalkToDrezelAfterFreeing.addStep(inTempleSecondFloor, talkToDrezelAfterFreeing);
		goTalkToDrezelAfterFreeing.addStep(inTempleFirstFloor, goUpWithWaterToSecondFloor);
		steps.put(7, goTalkToDrezelAfterFreeing);

		ConditionalStep goDownToDrezel = new ConditionalStep(this, enterUndergroundAfterFreeing);
		goDownToDrezel.addStep(inUnderground, talkToDrezelUnderground);
		goDownToDrezel.addStep(inTempleFirstFloor, goDownToGroundFloorAfterFreeing);
		goDownToDrezel.addStep(inTempleSecondFloor, goDownToFloorOneAfterFreeing);
		steps.put(8, goDownToDrezel);
		steps.put(9, goDownToDrezel);

		steps.put(10, bringDrezelEssence);
		steps.put(11, bringDrezelEssence);
		steps.put(12, bringDrezelEssence);
		steps.put(13, bringDrezelEssence);
		steps.put(14, bringDrezelEssence);
		steps.put(15, bringDrezelEssence);
		steps.put(16, bringDrezelEssence);
		steps.put(17, bringDrezelEssence);
		steps.put(18, bringDrezelEssence);
		steps.put(19, bringDrezelEssence);
		steps.put(20, bringDrezelEssence);
		steps.put(21, bringDrezelEssence);
		steps.put(22, bringDrezelEssence);
		steps.put(23, bringDrezelEssence);
		steps.put(24, bringDrezelEssence);
		steps.put(25, bringDrezelEssence);
		steps.put(26, bringDrezelEssence);
		steps.put(27, bringDrezelEssence);
		steps.put(28, bringDrezelEssence);
		steps.put(29, bringDrezelEssence);
		steps.put(30, bringDrezelEssence);
		steps.put(31, bringDrezelEssence);
		steps.put(32, bringDrezelEssence);
		steps.put(33, bringDrezelEssence);
		steps.put(34, bringDrezelEssence);
		steps.put(35, bringDrezelEssence);
		steps.put(36, bringDrezelEssence);
		steps.put(37, bringDrezelEssence);
		steps.put(38, bringDrezelEssence);
		steps.put(39, bringDrezelEssence);
		steps.put(40, bringDrezelEssence);
		steps.put(41, bringDrezelEssence);
		steps.put(42, bringDrezelEssence);
		steps.put(43, bringDrezelEssence);
		steps.put(44, bringDrezelEssence);
		steps.put(45, bringDrezelEssence);
		steps.put(46, bringDrezelEssence);
		steps.put(47, bringDrezelEssence);
		steps.put(48, bringDrezelEssence);
		steps.put(49, bringDrezelEssence);
		steps.put(50, bringDrezelEssence);
		steps.put(51, bringDrezelEssence);
		steps.put(52, bringDrezelEssence);
		steps.put(53, bringDrezelEssence);
		steps.put(54, bringDrezelEssence);
		steps.put(55, bringDrezelEssence);
		steps.put(56, bringDrezelEssence);
		steps.put(57, bringDrezelEssence);
		steps.put(58, bringDrezelEssence);
		steps.put(59, bringDrezelEssence);
		// There is a 60th step before the final 61, but the 'Quest Completed!' message pops up prior to it

		return steps;
	}

	public void setupItemRequirements()
	{
		runeEssence = new ItemRequirement("Rune or Pure Essence", ItemID.RUNE_ESSENCE, 50);
		runeEssence.addAlternates(ItemID.PURE_ESSENCE);
		bucket = new ItemRequirement("Bucket", ItemID.BUCKET);
		bucketHighlighted = new ItemRequirement("Bucket", ItemID.BUCKET);
		bucketHighlighted.setHighlightInInventory(true);
		runePouches = new ItemRequirements(LogicType.AND, "Rune pouches for carrying essence",
			new ItemRequirement("Small pouch", ItemID.SMALL_POUCH),
			new ItemRequirement("Medium pouch", ItemID.MEDIUM_POUCH),
			new ItemRequirement("Large pouch", ItemID.LARGE_POUCH),
			new ItemRequirement("Giant pouch", ItemID.GIANT_POUCH)
			);

		varrockTeleport = new ItemRequirement("Varrock teleports", ItemID.VARROCK_TELEPORT, 3);
		weaponAndArmour = new ItemRequirement("Ranged or melee weapon + armour", -1, -1);
		weaponAndArmour.setDisplayItemId(BankSlotIcons.getCombatGear());
		goldenKey = new ItemRequirement("Golden key", ItemID.GOLDEN_KEY);
		goldenKeyHighlighted = new ItemRequirement("Golden key", ItemID.GOLDEN_KEY);
		goldenKeyHighlighted.setHighlightInInventory(true);
		rangedMagedGear = new ItemRequirement("Combat gear, ranged or mage to safespot", -1, -1);
		rangedMagedGear.setDisplayItemId(BankSlotIcons.getRangedCombatGear());
		lotsOfRuneEssence = new ItemRequirement("As much essence as you can carry, you'll need to bring 50 UNNOTED in total", ItemID.PURE_ESSENCE, -1);
		murkyWater = new ItemRequirement("Murky water", ItemID.MURKY_WATER);
		ironKey = new ItemRequirement("Iron key", ItemID.IRON_KEY);
		blessedWaterHighlighted = new ItemRequirement("Blessed water", ItemID.BLESSED_WATER);
		blessedWaterHighlighted.setHighlightInInventory(true);
	}

	public void loadZones()
	{
		underground = new Zone(new WorldPoint(3402, 9880, 0), new WorldPoint(3443, 9907, 0));
		temple1 = new Zone(new WorldPoint(3409, 3483, 0), new WorldPoint(3411, 3494, 0));
		temple2 = new Zone(new WorldPoint(3408, 3485, 0), new WorldPoint(3408, 3486, 0));
		temple3 = new Zone(new WorldPoint(3408, 3491, 0), new WorldPoint(3408, 3492, 0));
		temple4 = new Zone(new WorldPoint(3412, 3484, 0), new WorldPoint(3415, 3493, 0));
		temple5 = new Zone(new WorldPoint(3416, 3483, 0), new WorldPoint(3417, 3494, 0));
		temple6 = new Zone(new WorldPoint(3418, 3484, 0), new WorldPoint(3418, 3493, 0));
		templeFloorOne = new Zone(new WorldPoint(3408, 3483, 1), new WorldPoint(3419, 3494, 1));
		templeFloorTwo = new Zone(new WorldPoint(3408, 3483, 2), new WorldPoint(3419, 3494, 2));
	}

	public void setupConditions()
	{
		inUnderground = new ZoneCondition(underground);
		inTempleGroundFloor = new ZoneCondition(temple1, temple2, temple3, temple4, temple5, temple6);
		inTempleFirstFloor = new ZoneCondition(templeFloorOne);
		inTempleSecondFloor = new ZoneCondition(templeFloorTwo);
		inTemple = new ZoneCondition(temple1, temple2, temple3, temple4, temple5, temple6, templeFloorOne, templeFloorTwo);

		hasIronKey = new ItemRequirementCondition(ironKey);
		hasGoldenOrIronKey = new Conditions(LogicType.OR, new ItemRequirementCondition(goldenKey), hasIronKey);
		hasBlessedOrMurkyWater = new ItemRequirementCondition(murkyWater);
		hasBlessedWater = new ItemRequirementCondition(blessedWaterHighlighted);
	}

	public void setupSteps()
	{
		talkToRoald = new NpcStep(this, NpcID.KING_ROALD_5215, new WorldPoint(3222, 3473, 0), "Speak to King Roald in Varrock Castle.");
		talkToRoald.addDialogStep("I'm looking for a quest!");
		talkToRoald.addDialogStep("Yes.");
		goToTemple = new ObjectStep(this, ObjectID.LARGE_DOOR_3490, new WorldPoint(3408, 3488, 0),
			"Go to the temple east of Varrock by the river and click on the large door.", weaponAndArmour);
		goToTemple.addDialogSteps("I'll get going.", "Roald sent me to check on Drezel.", "Sure. I'm a helpful person!");
		goDownToDog = new ObjectStep(this, ObjectID.TRAPDOOR_1579, new WorldPoint(3405, 3507, 0), "Go down the ladder north of the temple.");
		goDownToDog.addDialogStep("Yes.");
		((ObjectStep)(goDownToDog)).addAlternateObjects(ObjectID.TRAPDOOR_1581);
		killTheDog = new NpcStep(this, NpcID.TEMPLE_GUARDIAN, new WorldPoint(3405, 9901, 0),
			"Kill the Temple Guardian (level 30). It is immune to magic so you will need to use either ranged or melee.");
		climbUpAfterKillingDog = new ObjectStep(this, ObjectID.LADDER_17385, new WorldPoint(3405, 9907, 0),
			"Climb back up the ladder and return to King Roald.");
		returnToKingRoald = new NpcStep(this, NpcID.KING_ROALD_5215, new WorldPoint(3222, 3473, 0),
			"Return to King Roald.");
		returnToKingRoald.addSubSteps(climbUpAfterKillingDog);

		returnToTemple = new ObjectStep(this, ObjectID.LARGE_DOOR_3490, new WorldPoint(3408, 3488, 0),
			"Return to the temple.", bucket, lotsOfRuneEssence, rangedMagedGear);
		killMonk = new NpcStep(this, NpcID.MONK_OF_ZAMORAK_3486, new WorldPoint(3412, 3488, 0), "Kill a Monk of Zamorak (level 30) for a golden key. You can safespot using the pews.", true, goldenKey);

		goUpToFloorOneTemple = new ObjectStep(this, ObjectID.STAIRCASE_16671, new WorldPoint(3418, 3493, 0), "Go upstairs.");
		goUpToFloorTwoTemple = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(3410, 3485, 1), "Climb up the ladder.");
		talkToDrezel = new NpcStep(this, NpcID.DREZEL, new WorldPoint(3418, 3489, 2), "Talk to Drezel on the top floor of the temple.");
		talkToDrezel.addDialogSteps("So, what now?", "Yes, of course.");
		talkToDrezel.addSubSteps(goUpToFloorOneTemple, goUpToFloorTwoTemple);

		fillBucket = new ObjectStep(this, ObjectID.WELL_3485, new WorldPoint(3423, 9890, 0), "Use the bucket on the well in the central room.", bucketHighlighted);
		fillBucket.addIcon(ItemID.BUCKET);

		useKeyForKey = new DetailedQuestStep(this,  "Got to the central room, and study the monuments to find which has a key on it. Use the Golden Key on it.", goldenKeyHighlighted);
		useKeyForKey.addIcon(ItemID.GOLDEN_KEY);

		goDownToFloorOneTemple = new ObjectStep(this, ObjectID.LADDER_16679, new WorldPoint(3410, 3485, 2), "Go down to the underground of the temple.", bucket);
		goDownToGroundFloorTemple = new ObjectStep(this, ObjectID.STAIRCASE_16673, new WorldPoint(3417, 3485, 0), "Go down to the underground of the temple.", bucket);
		enterUnderground = new ObjectStep(this, ObjectID.TRAPDOOR_1579, new WorldPoint(3405, 3507, 0), "Go down to the underground of the temple.", bucket);
		enterUnderground.addSubSteps(goDownToFloorOneTemple, goDownToGroundFloorTemple);
		((ObjectStep)(enterUnderground)).addAlternateObjects(ObjectID.TRAPDOOR_1581);

		goUpWithWaterToSurface = new ObjectStep(this, ObjectID.LADDER_17385, new WorldPoint(3405, 9907, 0),
			"Go back up to the top floor of the temple.");
		goUpWithWaterToFirstFloor = new ObjectStep(this, ObjectID.STAIRCASE_16671, new WorldPoint(3418, 3493, 0),
			"Go back up to the top floor of the temple.");
		goUpWithWaterToSecondFloor = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(3410, 3485, 1),
			"Go back up to the top floor of the temple.");
		goUpWithWaterToSecondFloor.addSubSteps(goUpWithWaterToSurface, goUpWithWaterToFirstFloor);

		openDoor = new ObjectStep(this, ObjectID.CELL_DOOR, new WorldPoint(3415, 3489, 2), "Open the cell door.", ironKey);
		blessWater = new NpcStep(this, NpcID.DREZEL, new WorldPoint(3418, 3489, 2), "Talk to Drezel to bless the water.", murkyWater);
		useBlessedWater = new ObjectStep(this, ObjectID.COFFIN_3480, new WorldPoint(3413, 3487, 2), "Use the blessed water on the coffin.", blessedWaterHighlighted);
		useBlessedWater.addIcon(ItemID.BLESSED_WATER);

		talkToDrezelAfterFreeing = new NpcStep(this, NpcID.DREZEL, new WorldPoint(3418, 3489, 2), "Talk to Drezel again.");

		goDownToFloorOneAfterFreeing = new ObjectStep(this, ObjectID.LADDER_16679, new WorldPoint(3410, 3485, 2), "Go down to the underground of the temple.", lotsOfRuneEssence);
		goDownToGroundFloorAfterFreeing = new ObjectStep(this, ObjectID.STAIRCASE_16673, new WorldPoint(3417, 3485, 0), "Go down to the underground of the temple.", lotsOfRuneEssence);
		enterUndergroundAfterFreeing = new ObjectStep(this, ObjectID.TRAPDOOR_1579, new WorldPoint(3405, 3507, 0), "Go down to the underground of the temple.", lotsOfRuneEssence);
		((ObjectStep)(enterUndergroundAfterFreeing)).addAlternateObjects(ObjectID.TRAPDOOR_1581);
		talkToDrezelUnderground = new NpcStep(this, NpcID.DREZEL, new WorldPoint(3439, 9896, 0), "Talk to Drezel in the east of the underground temple area.", lotsOfRuneEssence);
		talkToDrezelUnderground.addSubSteps(goDownToFloorOneAfterFreeing, goDownToGroundFloorAfterFreeing, enterUndergroundAfterFreeing);

		bringDrezelEssence = new BringDrezelPureEssenceStep(this);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(varrockTeleport);
		reqs.add(runePouches);

		return reqs;
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(runeEssence);
		reqs.add(bucket);
		return reqs;
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Collections.singletonList("Temple guardian (level 30). Can only be hurt by ranged or melee."));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Start the quest", new ArrayList<>(Collections.singletonList(talkToRoald))));
		allSteps.add(new PanelDetails("Go to the temple", new ArrayList<>(Arrays.asList(goToTemple, killTheDog, returnToKingRoald)), weaponAndArmour));
		allSteps.add(new PanelDetails("Return to the temple", new ArrayList<>(Arrays.asList(returnToTemple, killMonk, talkToDrezel)), weaponAndArmour, bucket, lotsOfRuneEssence));
		allSteps.add(new PanelDetails("Freeing Drezel", new ArrayList<>(Arrays.asList(enterUnderground, useKeyForKey, fillBucket, goUpWithWaterToSecondFloor, openDoor, blessWater, useBlessedWater, talkToDrezelAfterFreeing)), weaponAndArmour, bucket, lotsOfRuneEssence));
		allSteps.add(new PanelDetails("Curing the Salve", new ArrayList<>(Arrays.asList(talkToDrezelUnderground, bringDrezelEssence)), runeEssence));

		return allSteps;
	}
}
