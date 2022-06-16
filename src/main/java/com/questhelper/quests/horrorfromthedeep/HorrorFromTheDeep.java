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
package com.questhelper.quests.horrorfromthedeep;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.PrayerRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.npc.NpcHintArrowRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Prayer;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.HORROR_FROM_THE_DEEP
)
public class HorrorFromTheDeep extends BasicQuestHelper
{
	ItemRequirement fireRune, airRune, waterRune, earthRune, sword, arrow, moltenGlass, tinderbox, hammer,
	steelNails, plank2, plank, swampTar1, combatRunes;

	ItemRequirement magicCombat, food, prayerPotions, gamesNecklace;

	Requirement protectFromMissiles;

	ItemRequirement lighthouseKey;

	Zone lighthouseF0, lighthouseF1, lighthouseF2, lighthouseF2V2, basement, dagCave;

	Requirement inLighthouseF0, inLighthouseF1, inLighthouseF2, inBasement, inDagCave;

	Requirement repairedBridge1, repairedBridge2, gotKey, usedTar, usedTinderbox, usedGlass;

	Requirement notUsedAirRune, notUsedWaterRune, notUsedEarthRune, notUsedFireRune, notUsedArrow, notUsedSword,
		doorUnlocked, dagannothNearby, motherNearby;

	QuestStep talkToLarrissa, usePlankOnBridge, useSecondPlank, talkToGunnjorn, openLighthouse,
		enterLighthouse, goToF1, goToF2, useTar, useTinderbox, useGlass, goDownToF2, goDownToF1,
		goDownToF0, goDownToBasement;

	QuestStep useAirRune, useWaterRune, useEarthRune, useFireRune, useSword, useArrow, goThroughDoor, talkToJossik,
		killDagannoth, killMother;


	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToLarrissa);

		ConditionalStep repairBridgeAndGetKey = new ConditionalStep(this, usePlankOnBridge);
		repairBridgeAndGetKey.addStep(new Conditions(repairedBridge1, repairedBridge2, gotKey), openLighthouse);
		repairBridgeAndGetKey.addStep(new Conditions(repairedBridge1, repairedBridge2), talkToGunnjorn);
		repairBridgeAndGetKey.addStep(repairedBridge1, useSecondPlank);
		steps.put(1, repairBridgeAndGetKey);

		ConditionalStep repairLightingMechanism = new ConditionalStep(this, enterLighthouse);
		repairLightingMechanism.addStep(new Conditions(inLighthouseF2, usedTar, usedTinderbox), useGlass);
		repairLightingMechanism.addStep(new Conditions(inLighthouseF2, usedTar), useTinderbox);
		repairLightingMechanism.addStep(inLighthouseF2, useTar);
		repairLightingMechanism.addStep(inLighthouseF1, goToF2);
		repairLightingMechanism.addStep(inLighthouseF0, goToF1);
		steps.put(2, repairLightingMechanism);
		steps.put(3, repairLightingMechanism);

		ConditionalStep goOpenStrangeDoor = new ConditionalStep(this, enterLighthouse);
		goOpenStrangeDoor.addStep(dagannothNearby, killDagannoth);
		goOpenStrangeDoor.addStep(new Conditions(inDagCave), talkToJossik);
		goOpenStrangeDoor.addStep(new Conditions(inBasement, doorUnlocked), goThroughDoor);
		goOpenStrangeDoor.addStep(new Conditions(inBasement, notUsedAirRune), useAirRune);
		goOpenStrangeDoor.addStep(new Conditions(inBasement, notUsedWaterRune), useWaterRune);
		goOpenStrangeDoor.addStep(new Conditions(inBasement, notUsedEarthRune), useEarthRune);
		goOpenStrangeDoor.addStep(new Conditions(inBasement, notUsedFireRune), useFireRune);
		goOpenStrangeDoor.addStep(new Conditions(inBasement, notUsedArrow), useArrow);
		goOpenStrangeDoor.addStep(new Conditions(inBasement, notUsedSword), useSword);
		goOpenStrangeDoor.addStep(inLighthouseF0, goDownToBasement);
		goOpenStrangeDoor.addStep(inLighthouseF1, goDownToF0);
		goOpenStrangeDoor.addStep(inLighthouseF2, goDownToF1);
		goOpenStrangeDoor.addDialogStep("Yes");
		steps.put(4, goOpenStrangeDoor);

		ConditionalStep goDefeatMother = new ConditionalStep(this, enterLighthouse);
		goDefeatMother.addStep(motherNearby, killMother);
		goDefeatMother.addStep(inDagCave, talkToJossik);
		goDefeatMother.addStep(inBasement, goThroughDoor);
		goDefeatMother.addStep(inLighthouseF0, goDownToBasement);
		steps.put(5, goDefeatMother);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		fireRune = new ItemRequirement("Fire rune", ItemID.FIRE_RUNE);
		airRune = new ItemRequirement("Air rune", ItemID.AIR_RUNE);
		waterRune = new ItemRequirement("Water rune", ItemID.WATER_RUNE);
		earthRune = new ItemRequirement("Earth rune", ItemID.EARTH_RUNE);
		sword = new ItemRequirement("Any sword you're willing to lose", ItemCollections.SWORDS);
		arrow = new ItemRequirement("Any arrow", ItemCollections.METAL_ARROWS);
		moltenGlass = new ItemRequirement("Molten glass", ItemID.MOLTEN_GLASS);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).isNotConsumed();
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed();
		steelNails = new ItemRequirement("Steel nails", ItemID.STEEL_NAILS);
		plank2 = new ItemRequirement("Plank", ItemID.PLANK, 2);
		plank = new ItemRequirement("Plank", ItemID.PLANK);
		swampTar1 = new ItemRequirement("Swamp tar", ItemID.SWAMP_TAR);


		magicCombat = new ItemRequirement("Magic combat gear", -1, -1).isNotConsumed();
		magicCombat.setDisplayItemId(BankSlotIcons.getMagicCombatGear());
		combatRunes = new ItemRequirement("20+ casts of each element spell", -1, -1);
		combatRunes.setDisplayItemId(ItemID.DEATH_RUNE);
		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS, -1);
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		gamesNecklace = new ItemRequirement("Games necklace", ItemCollections.GAMES_NECKLACES);

		protectFromMissiles = new PrayerRequirement("Protect from Missiles", Prayer.PROTECT_FROM_MISSILES);

		lighthouseKey = new ItemRequirement("Lighthouse key", ItemID.LIGHTHOUSE_KEY);
		lighthouseKey.setTooltip("You can get another from Gunnjorn in the Barbarian Agility Course");
	}

	public void setupZones()
	{
		lighthouseF0 = new Zone(new WorldPoint(2440, 4596, 0), new WorldPoint(2449, 4605, 0));
		lighthouseF1 = new Zone(new WorldPoint(2440, 4596, 1), new WorldPoint(2449, 4605, 1));
		lighthouseF2 = new Zone(new WorldPoint(2440, 4596, 2), new WorldPoint(2449, 4605, 2));
		lighthouseF2V2 = new Zone(new WorldPoint(2504, 3636, 2), new WorldPoint(2513, 3645, 2));
		basement = new Zone(new WorldPoint(2506, 4610, 0), new WorldPoint(2522, 4626, 0));
		dagCave = new Zone(new WorldPoint(2506, 4627, 0), new WorldPoint(2539, 4670, 0));
	}

	public void setupConditions()
	{
		inLighthouseF0 = new ZoneRequirement(lighthouseF0);
		inLighthouseF1 = new ZoneRequirement(lighthouseF1);
		inLighthouseF2 = new ZoneRequirement(lighthouseF2, lighthouseF2V2);
		inBasement = new ZoneRequirement(basement);
		inDagCave = new ZoneRequirement(dagCave);

		repairedBridge1 = new VarbitRequirement(36, 1);
		repairedBridge2 = new VarbitRequirement(37, 1);
		gotKey = new VarbitRequirement(38, 1);

		usedTar = new VarbitRequirement(46, 1);
		usedTinderbox = new VarbitRequirement(48, 1);
		usedGlass = new VarbitRequirement(47, 1);

		notUsedAirRune = new Conditions(LogicType.NOR, new VarbitRequirement(43, 1));
		notUsedWaterRune = new Conditions(LogicType.NOR, new VarbitRequirement(41, 1));
		notUsedEarthRune = new Conditions(LogicType.NOR, new VarbitRequirement(42, 1));
		notUsedFireRune = new Conditions(LogicType.NOR, new VarbitRequirement(40, 1));
		notUsedSword = new Conditions(LogicType.NOR, new VarbitRequirement(44, 1));
		notUsedArrow = new Conditions(LogicType.NOR, new VarbitRequirement(45, 1));

		doorUnlocked = new VarbitRequirement(35, 1);
		dagannothNearby = new NpcHintArrowRequirement(NpcID.DAGANNOTH_979);
		motherNearby = new NpcHintArrowRequirement(NpcID.DAGANNOTH_MOTHER, NpcID.DAGANNOTH_MOTHER_981,
			NpcID.DAGANNOTH_MOTHER_982, NpcID.DAGANNOTH_MOTHER_983, NpcID.DAGANNOTH_MOTHER_984,
			NpcID.DAGANNOTH_MOTHER_985, NpcID.DAGANNOTH_MOTHER_986, NpcID.DAGANNOTH_MOTHER_987,
			NpcID.DAGANNOTH_MOTHER_988);
		// Opened door, 39 =1
	}

	public void setupSteps()
	{
		talkToLarrissa = new NpcStep(this, NpcID.LARRISSA, new WorldPoint(2507, 3634, 0), "Talk to Larrissa outside " +
			"the Lighthouse north of the Barbarian Outpost. You can get here by Fairy Ring (alp), by jumping over " +
			"the bassalt rocks from the Barbarian Outpost, or from the Fremennik Province.");
		talkToLarrissa.addDialogSteps("With what?", "But how can I help?", "Okay, I'll help!");

		usePlankOnBridge = new ObjectStep(this, ObjectID.BROKEN_BRIDGE, new WorldPoint(2596, 3608, 0), "Use a plank " +
			"on the bridge east of the Lighthouse.", plank.highlighted(), steelNails.quantity(30), hammer);
		usePlankOnBridge.addIcon(ItemID.PLANK);

		useSecondPlank = new ObjectStep(this, ObjectID.BROKEN_BRIDGE_4616, new WorldPoint(2598, 3608, 0),
			"Use a plank on the other side of the bridge east of the Lighthouse.", plank.highlighted(), steelNails.quantity(30),
			hammer);
		useSecondPlank.addIcon(ItemID.PLANK);

		talkToGunnjorn = new NpcStep(this, NpcID.GUNNJORN, new WorldPoint(2547, 3553, 0),
			"Talk to Gunnjorn in the Barbarian Agility Course south of the lighthouse.");

		openLighthouse = new ObjectStep(this, ObjectID.DOORWAY_4577, new WorldPoint(2509, 3636, 0), "Unlock the " +
			"Lighthouse.", lighthouseKey);

		enterLighthouse = new ObjectStep(this, ObjectID.DOORWAY_4577, new WorldPoint(2509, 3636, 0), "Enter the " +
			"Lighthouse.");

		goToF1 = new ObjectStep(this, ObjectID.STAIRCASE_4568, new WorldPoint(2443, 4601, 0),
			"Go to the top of the lighthouse.");
		goToF2 = new ObjectStep(this, ObjectID.STAIRCASE_4569, new WorldPoint(2443, 4601, 1),
			"Go to the top of the lighthouse.");
		goToF2.addDialogStep("Climb up");
		goToF2.addSubSteps(goToF1);

		useTar = new ObjectStep(this, ObjectID.LIGHTING_MECHANISM_4588, new WorldPoint(2445, 4601, 2),
			"Use swamp tar on the lighting mechanism.", swampTar1.highlighted());
		useTar.addIcon(ItemID.SWAMP_TAR);

		useTinderbox = new ObjectStep(this, ObjectID.LIGHTING_MECHANISM_4588, new WorldPoint(2445, 4601, 2),
			"Use a tinderbox on the lighting mechanism.", tinderbox.highlighted());
		useTinderbox.addIcon(ItemID.TINDERBOX);

		useGlass = new ObjectStep(this, ObjectID.LIGHTING_MECHANISM_4588, new WorldPoint(2445, 4601, 2),
			"Use molten glass on the lighting mechanism.", moltenGlass.highlighted());
		useGlass.addIcon(ItemID.MOLTEN_GLASS);

		goDownToF1 = new ObjectStep(this, ObjectID.STAIRCASE_4570, new WorldPoint(2443, 4601, 1),
			"Go to the basement of the lighthouse.");

		goDownToF0 = new ObjectStep(this, ObjectID.STAIRCASE_4569, new WorldPoint(2443, 4601, 1),
			"Go to the basement of the lighthouse.");
		goDownToF0.addDialogStep("Climb down");

		goDownToBasement = new ObjectStep(this, ObjectID.IRON_LADDER_4383, new WorldPoint(2445, 4604, 0),
			"Go to the basement of the lighthouse.");
		goDownToBasement.addSubSteps(goDownToF1, goDownToF2);

		useAirRune = new ObjectStep(this, ObjectID.STRANGE_WALL_4544, new WorldPoint(2515, 4627, 0),
			"Use an air rune on the strange wall.", airRune.highlighted());
		useAirRune.addIcon(ItemID.AIR_RUNE);
		useWaterRune = new ObjectStep(this, ObjectID.STRANGE_WALL_4544, new WorldPoint(2515, 4627, 0),
			"Use a water rune on the strange wall.", waterRune.highlighted());
		useWaterRune.addIcon(ItemID.WATER_RUNE);
		useEarthRune = new ObjectStep(this, ObjectID.STRANGE_WALL_4544, new WorldPoint(2515, 4627, 0),
			"Use an earth rune on the strange wall.", earthRune.highlighted());
		useEarthRune.addIcon(ItemID.EARTH_RUNE);
		useFireRune = new ObjectStep(this, ObjectID.STRANGE_WALL_4544, new WorldPoint(2515, 4627, 0),
			"Use a fire rune on the strange wall.", fireRune.highlighted());
		useFireRune.addIcon(ItemID.FIRE_RUNE);
		useArrow = new ObjectStep(this, ObjectID.STRANGE_WALL_4544, new WorldPoint(2515, 4627, 0),
			"Use an arrow on the strange wall.", arrow.highlighted());
		useArrow.addIcon(ItemID.BRONZE_ARROW);
		useSword = new ObjectStep(this, ObjectID.STRANGE_WALL_4544, new WorldPoint(2515, 4627, 0),
			"Use a sword on the strange wall. You will lose it.", sword.highlighted());
		useSword.addIcon(ItemID.BRONZE_SWORD);

		goThroughDoor = new ObjectStep(this, ObjectID.STRANGE_WALL_4545, new WorldPoint(2516, 4627, 0), "Go through " +
			"the strange wall, ready for fighting.");
		talkToJossik = new NpcStep(this, NpcID.JOSSIK_4424, new WorldPoint(2518, 4634, 0), "Talk to Jossik.");
		killDagannoth = new NpcStep(this, NpcID.DAGANNOTH_979, new WorldPoint(2518, 4640, 0), "Defeat the dagannoth.");
		killMother = new NpcStep(this, NpcID.DAGANNOTH_MOTHER, new WorldPoint(2518, 4640, 0), "Defeat the Dagannoth " +
			"mother. She can only be hurt by air spells when white, water spells when blue, earth spells when brown, " +
			"fire spells when red, melee when orange and ranged when green.", protectFromMissiles, magicCombat, combatRunes);
		((NpcStep) killMother).addAlternateNpcs(NpcID.DAGANNOTH_MOTHER_981,
			NpcID.DAGANNOTH_MOTHER_982, NpcID.DAGANNOTH_MOTHER_983, NpcID.DAGANNOTH_MOTHER_984,
			NpcID.DAGANNOTH_MOTHER_985, NpcID.DAGANNOTH_MOTHER_986, NpcID.DAGANNOTH_MOTHER_987,
			NpcID.DAGANNOTH_MOTHER_988);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(fireRune, airRune, waterRune, earthRune, sword, arrow, moltenGlass, tinderbox, hammer,
			steelNails.quantity(60), plank2, swampTar1, combatRunes);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(gamesNecklace, magicCombat, food, prayerPotions);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.AGILITY, 35, true));
		reqs.add(new QuestRequirement(QuestHelperQuest.ALFRED_GRIMHANDS_BARCRAWL, QuestState.FINISHED));
		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Dagannoth (level 100)", "Dagannoth Mother (level 100)");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.MAGIC, 4662),
				new ExperienceReward(Skill.STRENGTH, 4662),
				new ExperienceReward(Skill.RANGED, 4662));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("A damaged God Book of your choice."),
				new UnlockReward("Access to The Lighthouse"),
				new UnlockReward("Ability to receive Dagannoth as a Slayer task."));
	}


	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Saving Jossik",
			Arrays.asList(talkToLarrissa, usePlankOnBridge, useSecondPlank, talkToGunnjorn, openLighthouse,
				enterLighthouse, goToF1, useTar, useTinderbox, useGlass, goDownToBasement, useAirRune, useWaterRune,
				useEarthRune, useFireRune, useArrow, useSword), hammer, steelNails.quantity(60), plank2, moltenGlass, tinderbox,
			swampTar1, fireRune, airRune, waterRune, earthRune, sword, arrow, combatRunes));

		allSteps.add(new PanelDetails("Defeating the dagannoths",
			Arrays.asList(goThroughDoor, talkToJossik, killDagannoth,
				killMother), combatRunes));

		return allSteps;
	}
}
