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
package com.questhelper.achievementdiaries.fremennik;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.PrayerRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpecialAttackRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.SpecialAttack;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;

@QuestDescriptor(
	quest = QuestHelperQuest.FREMENNIK_ELITE
)

public class FremennikElite extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement pureEss, dragonstone, goldBar, amuletMould, combatGear, thrownaxe, climbingBoots, rope, petRock,
		crossbow, hammer, mithGrap;

	// Recommended
	ItemRequirement food, prayerPot, stamPot;

	Requirement notGodwarsGenerals, notDragonAmulet, notDagKings, notAstralRunes, notRellRooftop, notSpiritualMage;

	// Quest requirements
	Requirement fremIsles, trollStronghold, lunarDiplomacy;


	// Steps
	QuestStep dragonAmulet, astralRunes, rellRooftop, claimReward, moveToNeit, moveToDagKings, moveToGodWars,
		moveToDagCave1, moveToDagCave2, moveToDagCave3, moveToDagCave4, moveToDagCave5, moveToDagCave6,
		moveToDagCave7, moveToDagCave8, moveToDagCave9, moveToDagCave10, moveToDagCave11, moveToDagCave12,
		moveToDagCave13, moveToWaterbirth, moveToDagCave, moveToAxeSpot, throwAxe, moveToLunarIsle, moveToPirates,
		moveToCaptain, moveToCaptain2, moveToAltar1, moveToAltar2;

	ObjectStep dropPetRock;

	NpcStep dagKings, godwarsGenerals, spiritualMage;

	Zone godWars, waterbirthIsland, dagCave, dagCave_2, dagCave_3, dagCave_4, dagCave2, dagCave1, dagCave3, dagCave4,
		dagCave5, dagCave6, dagCave7, dagCave8, dagCave9, dagCave10, dagCave11, dagCave12, dagCaveKings, neitiznot,
		pirates, lunarIsle, godWars2, godWars3, pirates2, pirates3, lunarIsle2, lunarIsle3;

	ZoneRequirement inGodwars, inWaterbirthIsland, inDagCave, inDagCave_2, inDagCave_3, inDagCave_4, inDagCave2,
		inDagCave1, inDagCave3, inDagCave4, inDagCave5, inDagCave6, inDagCave7, inDagCave8, inDagCave9, inDagCave10,
		inDagCave11, inDagCave12, inDagCaveKings, inNeitiznot, inPirates, inLunarIsle, inGodwars2, inGodwars3,
		inPirates2, inPirates3, inLunarIsle2, inLunarIsle3;

	Requirement protectMelee, protectMissiles, protectMagic, specialAttackEnabled;


	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doElite = new ConditionalStep(this, claimReward);
		doElite.addStep(new Conditions(notDagKings, inDagCaveKings), dagKings);
		doElite.addStep(new Conditions(notDagKings, inDagCave12), moveToDagKings);
		doElite.addStep(new Conditions(notDagKings, inDagCave11), moveToDagCave12);
		doElite.addStep(new Conditions(notDagKings, inDagCave10), moveToDagCave11);
		doElite.addStep(new Conditions(notDagKings, inDagCave9), moveToDagCave10);
		doElite.addStep(new Conditions(notDagKings, inDagCave8), moveToDagCave9);
		doElite.addStep(new Conditions(notDagKings, inDagCave7), moveToDagCave8);
		doElite.addStep(new Conditions(notDagKings, inDagCave6), moveToDagCave7);
		doElite.addStep(new Conditions(notDagKings, inDagCave5), moveToDagCave6);
		doElite.addStep(new Conditions(notDagKings, inDagCave4), moveToDagCave5);
		doElite.addStep(new Conditions(notDagKings, inDagCave3), moveToDagCave4);
		doElite.addStep(new Conditions(notDagKings, inDagCave2), moveToDagCave3);
		doElite.addStep(new Conditions(notDagKings, inDagCave1), moveToDagCave2);
		doElite.addStep(new Conditions(notDagKings, inDagCave_4), moveToDagCave1);
		doElite.addStep(new Conditions(notDagKings, inDagCave_3), throwAxe);
		doElite.addStep(new Conditions(notDagKings, inDagCave_2), moveToAxeSpot);
		doElite.addStep(new Conditions(notDagKings, inDagCave), dropPetRock);
		doElite.addStep(new Conditions(notDagKings, inWaterbirthIsland), moveToDagCave);
		doElite.addStep(notDagKings, moveToWaterbirth);
		doElite.addStep(new Conditions(notDragonAmulet, inNeitiznot), dragonAmulet);
		doElite.addStep(notDragonAmulet, moveToNeit);
		doElite.addStep(new Conditions(notAstralRunes, inLunarIsle), astralRunes);
		doElite.addStep(new Conditions(notAstralRunes, inLunarIsle2), moveToAltar2);
		doElite.addStep(new Conditions(notAstralRunes, inLunarIsle3), moveToAltar1);
		doElite.addStep(new Conditions(notAstralRunes, inPirates3), moveToLunarIsle);
		doElite.addStep(new Conditions(notAstralRunes, inPirates2), moveToCaptain2);
		doElite.addStep(new Conditions(notAstralRunes, inPirates), moveToCaptain);
		doElite.addStep(notAstralRunes, moveToPirates);
		doElite.addStep(notRellRooftop, rellRooftop);
		doElite.addStep(new Conditions(notSpiritualMage, inGodwars), spiritualMage);
		doElite.addStep(notSpiritualMage, moveToGodWars);
		doElite.addStep(new Conditions(notGodwarsGenerals, inGodwars), godwarsGenerals);
		doElite.addStep(notGodwarsGenerals, moveToGodWars);

		return doElite;
	}

	public void setupRequirements()
	{
		notDagKings = new VarplayerRequirement(1184, false, 31);
		notAstralRunes = new VarplayerRequirement(1185, false, 0);
		notDragonAmulet = new VarplayerRequirement(1185, false, 1);
		notRellRooftop = new VarplayerRequirement(1185, false, 2);
		notGodwarsGenerals = new VarplayerRequirement(1185, false, 3);
		notSpiritualMage = new VarplayerRequirement(1185, false, 4);

		// TODO add varb req for generals and army kills (my combat lvl is too low to ensure this works properly)
		// notKillArma = new VarbitRequirement(3973, true, 1);
		// notKillSara = new VarbitRequirement(3973, true, 1);
		// notKillBand = new VarbitRequirement(3973, true, 1);
		// notKillZam = new VarbitRequirement(3973, true, 1);

		specialAttackEnabled = new SpecialAttackRequirement(SpecialAttack.ON);
		protectMelee = new PrayerRequirement("Protect from Melee", Prayer.PROTECT_FROM_MELEE);
		protectMissiles = new PrayerRequirement("Protect from Missiles", Prayer.PROTECT_FROM_MISSILES);
		protectMagic = new PrayerRequirement("Protect from Magic", Prayer.PROTECT_FROM_MAGIC);

		petRock = new ItemRequirement("Pet Rock", ItemID.PET_ROCK).showConditioned(notDagKings);
		pureEss = new ItemRequirement("Pure essence", ItemID.PURE_ESSENCE).showConditioned(notAstralRunes);
		dragonstone = new ItemRequirement("Cut dragonstone", ItemID.DRAGONSTONE).showConditioned(notDragonAmulet);
		goldBar = new ItemRequirement("Gold bar", ItemID.GOLD_BAR).showConditioned(notDragonAmulet);
		amuletMould = new ItemRequirement("Amulet mould", ItemID.AMULET_MOULD).showConditioned(notDragonAmulet);
		thrownaxe = new ItemRequirement("Rune thrownaxe", ItemID.RUNE_THROWNAXE).showConditioned(notDagKings);
		climbingBoots = new ItemRequirement("Climbing boots", ItemID.CLIMBING_BOOTS).showConditioned(new Conditions(LogicType.OR, notGodwarsGenerals, notSpiritualMage));
		rope = new ItemRequirement("Rope", ItemID.ROPE).showConditioned(new Conditions(LogicType.OR, notGodwarsGenerals, notSpiritualMage));
		crossbow = new ItemRequirement("Any crossbow", ItemCollections.getCrossbows()).showConditioned(new Conditions(LogicType.OR, notGodwarsGenerals, notSpiritualMage));
		mithGrap = new ItemRequirement("Mith grapple", ItemID.MITH_GRAPPLE_9419).showConditioned(new Conditions(LogicType.OR, notGodwarsGenerals, notSpiritualMage));
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(new Conditions(LogicType.OR, notGodwarsGenerals, notSpiritualMage));

		combatGear = new ItemRequirement("High tier combat gear", -1, -1).showConditioned(new Conditions(LogicType.OR, notDagKings, notGodwarsGenerals, notSpiritualMage));
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);
		prayerPot = new ItemRequirement("Prayer Potions", ItemCollections.getPrayerPotions(), -1);
		stamPot = new ItemRequirement("Stamina Potions", ItemCollections.getStaminaPotions(), -1);

		fremIsles = new QuestRequirement(QuestHelperQuest.THE_FREMENNIK_ISLES, QuestState.FINISHED);
		trollStronghold = new QuestRequirement(QuestHelperQuest.TROLL_STRONGHOLD, QuestState.FINISHED);
		lunarDiplomacy = new QuestRequirement(QuestHelperQuest.LUNAR_DIPLOMACY, QuestState.FINISHED);

		inGodwars = new ZoneRequirement(godWars);
		inGodwars2 = new ZoneRequirement(godWars2);
		inGodwars3 = new ZoneRequirement(godWars3);
		inWaterbirthIsland = new ZoneRequirement(waterbirthIsland);
		inDagCave = new ZoneRequirement(dagCave);
		inDagCave_2 = new ZoneRequirement(dagCave_2);
		inDagCave_3 = new ZoneRequirement(dagCave_3);
		inDagCave_4 = new ZoneRequirement(dagCave_4);
		inDagCave1 = new ZoneRequirement(dagCave1);
		inDagCave2 = new ZoneRequirement(dagCave2);
		inDagCave3 = new ZoneRequirement(dagCave3);
		inDagCave4 = new ZoneRequirement(dagCave4);
		inDagCave5 = new ZoneRequirement(dagCave5);
		inDagCave6 = new ZoneRequirement(dagCave6);
		inDagCave7 = new ZoneRequirement(dagCave7);
		inDagCave8 = new ZoneRequirement(dagCave8);
		inDagCave9 = new ZoneRequirement(dagCave9);
		inDagCave10 = new ZoneRequirement(dagCave10);
		inDagCave11 = new ZoneRequirement(dagCave11);
		inDagCave12 = new ZoneRequirement(dagCave12);
		inDagCaveKings = new ZoneRequirement(dagCaveKings);
		inNeitiznot = new ZoneRequirement(neitiznot);
		inPirates = new ZoneRequirement(pirates);
		inLunarIsle = new ZoneRequirement(lunarIsle);
		inLunarIsle2 = new ZoneRequirement(lunarIsle2);
		inLunarIsle3 = new ZoneRequirement(lunarIsle3);
		inPirates2 = new ZoneRequirement(pirates2);
		inPirates3 = new ZoneRequirement(pirates3);
	}

	public void loadZones()
	{
		godWars = new Zone(new WorldPoint(2819, 5375, 2), new WorldPoint(2943, 5252, 2));
		godWars2 = new Zone(new WorldPoint(2819, 5375, 1), new WorldPoint(2943, 5252, 1));
		godWars3 = new Zone(new WorldPoint(2819, 5375, 0), new WorldPoint(2943, 5252, 0));
		waterbirthIsland = new Zone(new WorldPoint(2499, 3770, 0), new WorldPoint(2557, 3713, 0));
		dagCave = new Zone(new WorldPoint(2434, 10174, 0), new WorldPoint(2491, 10118, 0));
		dagCave_2 = new Zone(new WorldPoint(2492, 10174, 0), new WorldPoint(2558, 10149, 0));
		dagCave_3 = new Zone(new WorldPoint(2544, 10148, 0), new WorldPoint(2546, 10146, 0));
		dagCave_4 = new Zone(new WorldPoint(2542, 10145, 0), new WorldPoint(2547, 10141, 0));
		dagCave1 = new Zone(new WorldPoint(1792, 4414, 3), new WorldPoint(1809, 4397, 3));
		dagCave2 = new Zone(new WorldPoint(1808, 4411, 2), new WorldPoint(1824, 4400, 2));
		dagCave3 = new Zone(new WorldPoint(1824, 4412, 3), new WorldPoint(1853, 4389, 3));
		dagCave4 = new Zone(new WorldPoint(1807, 4397, 2), new WorldPoint(1835, 4380, 2));
		dagCave5 = new Zone(new WorldPoint(1794, 4398, 1), new WorldPoint(1815, 4387, 1));
		dagCave6 = new Zone(new WorldPoint(1793, 4387, 2), new WorldPoint(1805, 4378, 2));
		dagCave7 = new Zone(new WorldPoint(1793, 4385, 1), new WorldPoint(1807, 4365, 1));
		dagCave8 = new Zone(new WorldPoint(1796, 4374, 2), new WorldPoint(1877, 4354, 2));
		dagCave9 = new Zone(new WorldPoint(1824, 4374, 1), new WorldPoint(1872, 4353, 1));
		dagCave10 = new Zone(new WorldPoint(1856, 4389, 2), new WorldPoint(1871, 4371, 2));
		dagCave11 = new Zone(new WorldPoint(1858, 4415, 1), new WorldPoint(1896, 4387, 1));
		dagCave12 = new Zone(new WorldPoint(1874, 4415, 0), new WorldPoint(1968, 4350, 0));
		dagCaveKings = new Zone(new WorldPoint(2891, 4472, 0), new WorldPoint(2936, 4362, 0));
		neitiznot = new Zone(new WorldPoint(2306, 3825, 0), new WorldPoint(2367, 3779, 0));
		pirates = new Zone(new WorldPoint(2175, 3840, 0), new WorldPoint(2239, 3781, 0));
		pirates2 = new Zone(new WorldPoint(2175, 3840, 1), new WorldPoint(2239, 3781, 1));
		pirates3 = new Zone(new WorldPoint(2175, 3840, 2), new WorldPoint(2239, 3781, 2));
		lunarIsle = new Zone(new WorldPoint(2052, 3963, 0), new WorldPoint(2174, 3841, 0));
		lunarIsle2 = new Zone(new WorldPoint(2052, 3963, 0), new WorldPoint(2174, 3841, 1));
		lunarIsle3 = new Zone(new WorldPoint(2052, 3963, 0), new WorldPoint(2174, 3841, 2));
	}

	public void setupSteps()
	{
		rellRooftop = new ObjectStep(this, 14946, new WorldPoint(2625, 3677, 0),
			"Complete a lap of the Rellekka Rooftop course.");
		dragonAmulet = new ObjectStep(this, 21303, new WorldPoint(2344, 3811, 0),
			"Smelt a dragonstone amulet on the clay forge.");
		dragonAmulet.addIcon(ItemID.DRAGONSTONE_AMULET_U);
		moveToPirates = new NpcStep(this, NpcID.LOKAR_SEARUNNER, new WorldPoint(2620, 3693, 0),
			"Speak to Lokar.");
		moveToLunarIsle = new NpcStep(this, NpcID.CAPTAIN_BENTLEY_6650, new WorldPoint(2223, 3799, 2),
			"Speak to Captain Bentley to travel to Lunar Isle.");
		moveToCaptain = new ObjectStep(this, ObjectID.LADDER_16960, new WorldPoint(2213, 3795, 0),
			"Go up the ladder.");
		moveToCaptain2 = new ObjectStep(this, ObjectID.LADDER_16959, new WorldPoint(2214, 3801, 1),
			"Go up the ladder.");
		moveToAltar1 = new ObjectStep(this, ObjectID.LADDER_16961, new WorldPoint(2127, 3893, 2),
			"Go down the ladder.");
		moveToAltar2 = new ObjectStep(this, ObjectID.LADDER_16962, new WorldPoint(2118, 3894, 1),
			"Go down the ladder.");
		astralRunes = new ObjectStep(this, ObjectID.ALTAR_34771, new WorldPoint(2158, 3864, 0),
			"Craft 56 astral runes");
		moveToNeit = new NpcStep(this, NpcID.MARIA_GUNNARS_1883, new WorldPoint(2644, 3710, 0),
			"Speak with Maria Gunnars to travel to Neitiznot.");


		moveToGodWars = new ObjectStep(this, 26419, new WorldPoint(2919, 3747, 0),
			"Go down the hole. Bring a rope if this is your first time entering.");
		godwarsGenerals = new NpcStep(this, NpcID.KREEARRA, new WorldPoint(2832, 5301, 2),
			"Get kills for a faction then kill it's respective general", true);
		godwarsGenerals.addAlternateNpcs(NpcID.KRIL_TSUTSAROTH);
		godwarsGenerals.addAlternateNpcs(NpcID.GENERAL_GRAARDOR);
		godwarsGenerals.addAlternateNpcs(NpcID.COMMANDER_ZILYANA);
		// TODO count kills with each army and generals
		// killArma = new NpcStep(this, NpcID.AVIANSIE_3172, new WorldPoint(2880, 5298,  2), "Kill armadyl guys");
		// npc id       kree 3162 kril 3129 graar 2215 zil 2205
		// varb count   arma 3973 zam 3976 bandos 3975 sara 3972
		// need varbs for generals kills

		spiritualMage = new NpcStep(this, NpcID.SPIRITUAL_MAGE, new WorldPoint(2832, 5301, 2),
			"Kill a spiritual mage", true);
		spiritualMage.addAlternateNpcs(NpcID.SPIRITUAL_MAGE_2244);
		spiritualMage.addAlternateNpcs(NpcID.SPIRITUAL_MAGE_3161);
		spiritualMage.addAlternateNpcs(NpcID.SPIRITUAL_MAGE_3168);

		moveToWaterbirth = new NpcStep(this, NpcID.JARVALD, new WorldPoint(2620, 3686, 0),
			"Speak with Jarvald to travel to Waterbirth Island");
		moveToWaterbirth.addDialogStep("What Jarvald is doing.");
		moveToWaterbirth.addDialogStep("Can I come?");
		moveToWaterbirth.addDialogStep("YES");
		moveToDagCave = new ObjectStep(this, 8929, new WorldPoint(2521, 3740, 0),
			"Enter cave and pray melee. Make sure you are full stam and prayer before entering.", protectMelee);
		dropPetRock = new ObjectStep(this, 8965, new WorldPoint(2490, 10162, 0),
			"Drop your pet rock on one pressure pad then stand on the other pad to open the gate.", petRock);// item on tile req?
		dropPetRock.addIcon(ItemID.PET_ROCK);
		dropPetRock.addTileMarker(new WorldPoint(2490, 10164, 0), SpriteID.SKILL_AGILITY);
		moveToAxeSpot = new ObjectStep(this, 8945, new WorldPoint(2545, 10146, 0),
			"Continue onwards until you reach the barrier.");
		throwAxe = new NpcStep(this, 2253, new WorldPoint(2543, 10143, 0),
			"Attack the Door-Support with a rune thrownaxe special attack. If done correctly the axe should ricochet and lower all 3 barriers.", thrownaxe.equipped(), specialAttackEnabled);
		moveToDagCave1 = new ObjectStep(this, 10177, new WorldPoint(2546, 10143, 0),
			"Enable magic protection then climb down the ladder.", protectMagic);
		moveToDagCave1.addDialogSteps("Climb Down.");
		moveToDagCave2 = new ObjectStep(this, ObjectID.LADDER_10195, new WorldPoint(1808, 4405, 3),
			"Enable melee protection and continue through the cave.", protectMelee);
		moveToDagCave3 = new ObjectStep(this, ObjectID.LADDER_10198, new WorldPoint(1823, 4404, 2),
			"Continue through the cave.", protectMelee);
		moveToDagCave4 = new ObjectStep(this, ObjectID.LADDER_10199, new WorldPoint(1834, 4389, 3),
			"Enable missile protection and continue through the cave.", protectMissiles);
		moveToDagCave5 = new ObjectStep(this, ObjectID.LADDER_10201, new WorldPoint(1811, 4394, 2),
			"Enable magic protection and continue through the cave.", protectMagic);
		moveToDagCave6 = new ObjectStep(this, ObjectID.LADDER_10203, new WorldPoint(1799, 4388, 1),
			"Continue through the cave.", protectMagic);
		moveToDagCave7 = new ObjectStep(this, ObjectID.LADDER_10205, new WorldPoint(1797, 4382, 2),
			"Continue through the cave.", protectMagic);
		moveToDagCave8 = new ObjectStep(this, ObjectID.LADDER_10207, new WorldPoint(1802, 4369, 1),
			"Enable melee protection and continue through the cave.", protectMelee);
		moveToDagCave9 = new ObjectStep(this, ObjectID.LADDER_10209, new WorldPoint(1826, 4362, 2),
			"Continue through the cave.", protectMelee);
		moveToDagCave10 = new ObjectStep(this, ObjectID.LADDER_10211, new WorldPoint(1863, 4371, 1),
			"Continue through the cave.", protectMelee);
		moveToDagCave11 = new ObjectStep(this, ObjectID.LADDER_10213, new WorldPoint(1864, 4388, 2),
			"Continue through the cave.", protectMelee);
		moveToDagCave12 = new ObjectStep(this, ObjectID.LADDER_10215, new WorldPoint(1890, 4407, 1),
			"Continue through the cave.", protectMelee);
		moveToDagCave13 = new ObjectStep(this, ObjectID.LADDER_10217, new WorldPoint(1957, 4371, 0),
			"Continue through the cave.", protectMelee);
		moveToDagKings = new ObjectStep(this, 3831, new WorldPoint(1911, 4367, 0),
			"Enter the Kings' lair.", protectMelee);
		dagKings = new NpcStep(this, NpcID.DAGANNOTH_REX, new WorldPoint(2913, 4449, 0),
			"Kill each of the Dagannoth Kings.", true, combatGear);// check slayer and non slayer versions of area and npc ids
		dagKings.addAlternateNpcs(NpcID.DAGANNOTH_PRIME);
		dagKings.addAlternateNpcs(NpcID.DAGANNOTH_SUPREME);
		dagKings.addAlternateNpcs(NpcID.DAGANNOTH_SUPREME_6496);
		dagKings.addAlternateNpcs(NpcID.DAGANNOTH_PRIME_6497);
		dagKings.addAlternateNpcs(NpcID.DAGANNOTH_REX_6498);

		claimReward = new NpcStep(this, NpcID.THORODIN_5526, new WorldPoint(2658, 3627, 0),
			"Talk to Thorodin south of Rellekka to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(pureEss.quantity(28), dragonstone, goldBar, amuletMould, combatGear, rope.quantity(3), climbingBoots, petRock, crossbow, mithGrap, hammer);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, prayerPot, stamPot);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("God Wars Generals (level ~600), 3 Dagannoth Kings (level 303), and a Spiritual mage (level 120)");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new SkillRequirement(Skill.AGILITY, 80));
		req.add(new SkillRequirement(Skill.CRAFTING, 80));
		req.add(new SkillRequirement(Skill.HITPOINTS, 70, false));
		req.add(new SkillRequirement(Skill.RANGED, 70, false));
		req.add(new SkillRequirement(Skill.RUNECRAFT, 82));
		req.add(new SkillRequirement(Skill.SLAYER, 83, true));
		req.add(new SkillRequirement(Skill.STRENGTH, 70, false));
		req.add(new SkillRequirement(Skill.PRAYER, 43, false,
			"At least 43 Prayer for protection prayers"));
		return req;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("Fremennik Sea Boots (4)", ItemID.FREMENNIK_SEA_BOOTS_4, 1),
				new ItemReward("50,000 Exp. Lamp (Any skill over 70)", ItemID.ANTIQUE_LAMP, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Dagganoth bones will be dropped in noted form"),
				new UnlockReward("Enchanted lyre can now teleport to Jatizso and Neitiznot"),
				new UnlockReward("Even faster approval gain in Miscellania"),
				new UnlockReward("Seal of passage is no longer needed to interact with anyone on Lunar Isle"),
				new UnlockReward("Access to the Return Orb inside the bank on Lunar Isle"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails dagannothKingsSteps = new PanelDetails("Kill Dagannoth Kings", Arrays.asList(moveToWaterbirth, moveToDagCave,
			dropPetRock, moveToAxeSpot, throwAxe, moveToDagCave1, moveToDagCave2, moveToDagCave3, moveToDagCave4,
			moveToDagCave5, moveToDagCave6, moveToDagCave7, moveToDagCave8, moveToDagCave9, moveToDagCave10,
			moveToDagCave12, moveToDagCave13, moveToDagKings, dagKings), combatGear, thrownaxe, petRock);
		dagannothKingsSteps.setDisplayCondition(notDagKings);
		allSteps.add(dagannothKingsSteps);

		PanelDetails dragonstoneAmuletSteps = new PanelDetails("Dragonstone Amulet", Arrays.asList(moveToNeit, dragonAmulet),
			fremIsles, new SkillRequirement(Skill.CRAFTING, 80), dragonstone, goldBar, amuletMould);
		dragonstoneAmuletSteps.setDisplayCondition(notDragonAmulet);
		allSteps.add(dragonstoneAmuletSteps);

		PanelDetails astralRunesSteps = new PanelDetails("Astral Runes", Arrays.asList(moveToPirates, moveToCaptain,
			moveToCaptain2, moveToLunarIsle, moveToAltar1, moveToAltar2), lunarDiplomacy,
			new SkillRequirement(Skill.RUNECRAFT, 82), pureEss.quantity(28));
		astralRunesSteps.setDisplayCondition(notAstralRunes);
		allSteps.add(astralRunesSteps);

		PanelDetails rellekkaRooftopsSteps = new PanelDetails("Rellekka Rooftops", Collections.singletonList(rellRooftop),
			new SkillRequirement(Skill.AGILITY, 80));
		rellekkaRooftopsSteps.setDisplayCondition(notRellRooftop);
		allSteps.add(rellekkaRooftopsSteps);

		PanelDetails spiritualMageSteps = new PanelDetails("Slay Spiritual Mage", Arrays.asList(moveToGodWars,
			spiritualMage), trollStronghold, new SkillRequirement(Skill.SLAYER, 83, true), combatGear,
			rope.quantity(1), climbingBoots);
		spiritualMageSteps.setDisplayCondition(notSpiritualMage);
		allSteps.add(spiritualMageSteps);

		PanelDetails godWarsSteps = new PanelDetails("God Wars Generals", Arrays.asList(moveToGodWars,
			godwarsGenerals), trollStronghold, new SkillRequirement(Skill.AGILITY, 70),
			new SkillRequirement(Skill.STRENGTH, 70, false), new SkillRequirement(Skill.HITPOINTS, 70, false),
			new SkillRequirement(Skill.RANGED, 70, false), combatGear, rope.quantity(2), climbingBoots, hammer,
			mithGrap, crossbow);
		godWarsSteps.setDisplayCondition(notGodwarsGenerals);
		allSteps.add(godWarsSteps);

		PanelDetails finishOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishOffSteps);

		return allSteps;
	}
}
