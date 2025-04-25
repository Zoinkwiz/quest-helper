/*
 * Copyright (c) 2022, Obasill
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
package com.questhelper.helpers.achievementdiaries.fremennik;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.PrayerRequirement;
import com.questhelper.requirements.player.SpecialAttackRequirement;
import com.questhelper.requirements.util.SpecialAttack;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.steps.*;
import net.runelite.api.Prayer;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.List;

public class DagRoute extends ConditionalStep
{
	//Requirements
	ItemRequirement combatGear, food, prayerPot, petRock, thrownaxe, stamPot;

	QuestStep moveToWaterbirth;

	DetailedQuestStep moveToDagCave, moveToAxeSpot, throwAxe, moveToDagCave1, moveToDagCave2, moveToDagCave3, moveToDagCave4,
		moveToDagCave5, moveToDagCave6, moveToDagCave7, moveToDagCave8, moveToDagCave9, moveToDagCave10, moveToDagCave11,
		moveToDagCave12, activateSpecial, dropPetRock, moveToDagKings;

	Requirement protectMelee, protectMissiles, protectMagic, specialAttackEnabled;

	Zone waterbirthIsland, dagCave, dagCave1, dagCave_2, dagCave_3, dagCave_4, dagCave2, dagCave3, dagCave4, dagCave5,
		dagCave6, dagCave7, dagCave8, dagCave9, dagCave10, dagCave11, dagCave12;

	Requirement inWaterbirthIsland, inDagCave, inDagCave1, inDagCave_2, inDagCave_3, inDagCave_4, inDagCave2, inDagCave3,
		inDagCave4, inDagCave5, inDagCave6, inDagCave7, inDagCave8, inDagCave9, inDagCave10, inDagCave11, inDagCave12;


	public DagRoute(QuestHelper questHelper)
	{
		super(questHelper, new NpcStep(questHelper, NpcID.VIKING_DAGGANOTH_CAVE_FERRYMAN_ISLAND, new WorldPoint(2620, 3686, 0),
			"Speak with Jarvald to travel to Waterbirth Island."));
		super.addDialogSteps("What Jarvald is doing.", "Can I come?", "YES");
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		addStep(inDagCave12, moveToDagKings);
		addStep(inDagCave11, moveToDagCave12);
		addStep(inDagCave10, moveToDagCave11);
		addStep(inDagCave9, moveToDagCave10);
		addStep(inDagCave8, moveToDagCave9);
		addStep(inDagCave7, moveToDagCave8);
		addStep(inDagCave6, moveToDagCave7);
		addStep(inDagCave5, moveToDagCave6);
		addStep(inDagCave4, moveToDagCave5);
		addStep(inDagCave3, moveToDagCave4);
		addStep(inDagCave2, moveToDagCave3);
		addStep(inDagCave1, moveToDagCave2);
		addStep(inDagCave_4, moveToDagCave1);
		addStep(new Conditions(inDagCave_3, specialAttackEnabled), throwAxe);
		addStep(inDagCave_3, activateSpecial);
		addStep(inDagCave_2, moveToAxeSpot);
		addStep(inDagCave, dropPetRock);
		addStep(inWaterbirthIsland, moveToDagCave);
	}

	public void setupItemRequirements()
	{
		thrownaxe = new ItemRequirement("Rune thrownaxe", ItemID.RUNE_THROWNAXE);
		petRock = new ItemRequirement("Pet rock", ItemID.VT_USELESS_ROCK);
		petRock.setTooltip("Obtained from Askeladden in Rellekka");

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		prayerPot = new ItemRequirement("Prayer Potions", ItemCollections.PRAYER_POTIONS, -1);
		stamPot = new ItemRequirement("Stamina Potions", ItemCollections.STAMINA_POTIONS, -1);

		protectMelee = new PrayerRequirement("Protect from Melee", Prayer.PROTECT_FROM_MELEE);
		protectMissiles = new PrayerRequirement("Protect from Missiles", Prayer.PROTECT_FROM_MISSILES);
		protectMagic = new PrayerRequirement("Protect from Magic", Prayer.PROTECT_FROM_MAGIC);
	}

	public void setupZones()
	{
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
	}

	public void setupConditions()
	{
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

		specialAttackEnabled = new SpecialAttackRequirement(SpecialAttack.ON);
	}

	public void setupSteps()
	{
		moveToWaterbirth = steps.get(null);
		moveToDagCave = new ObjectStep(questHelper, 8929, new WorldPoint(2521, 3740, 0),
			"Enter the cave and pray melee. Make sure you are full stamina and prayer before entering.", protectMelee,
			thrownaxe, petRock, food, stamPot, prayerPot);
		dropPetRock = new ObjectStep(questHelper, 8965, new WorldPoint(2490, 10162, 0),
			"Drop your pet rock on one pressure pad then stand on the other pad to open the gate.", petRock);// item on tile req?
		dropPetRock.addIcon(ItemID.VT_USELESS_ROCK);
		dropPetRock.addTileMarker(new WorldPoint(2490, 10164, 0), SpriteID.SKILL_AGILITY);
		moveToAxeSpot = new ObjectStep(questHelper, 8945, new WorldPoint(2545, 10146, 0),
			"Continue onwards until you reach the barrier.", thrownaxe);
		activateSpecial = new DetailedQuestStep(questHelper, "Activate special attack with the rune thrownaxes equipped.",
			thrownaxe.equipped(), specialAttackEnabled);
		throwAxe = new NpcStep(questHelper, 2253, new WorldPoint(2543, 10143, 0),
			"Attack the Door-Support with a rune thrownaxe special attack. If done correctly the axe should ricochet" +
				" and lower all 3 barriers.", thrownaxe.equipped(), specialAttackEnabled);
		moveToDagCave1 = new ObjectStep(questHelper, 10177, new WorldPoint(2546, 10143, 0),
			"Enable magic protection then climb down the ladder.", protectMagic);
		moveToDagCave1.addDialogSteps("Climb Down.");
		moveToDagCave2 = new ObjectStep(questHelper, ObjectID.DAGEXP_LADDER1, new WorldPoint(1808, 4405, 3),
			"Enable melee protection then continue through the cave.", protectMelee);
		moveToDagCave3 = new ObjectStep(questHelper, ObjectID.DAGEXP_LADDER4, new WorldPoint(1823, 4404, 2),
			"Keep current protection and continue through the cave.", protectMelee);
		moveToDagCave4 = new ObjectStep(questHelper, ObjectID.DAGEXP_LADDER5, new WorldPoint(1834, 4389, 3),
			"Enable missile protection then continue through the cave.", protectMissiles);
		moveToDagCave5 = new ObjectStep(questHelper, ObjectID.DAGEXP_LADDER7, new WorldPoint(1811, 4394, 2),
			"Enable magic protection and continue through the cave.", protectMagic);
		moveToDagCave6 = new ObjectStep(questHelper, ObjectID.DAGEXP_LADDER9, new WorldPoint(1799, 4388, 1),
			"Keep current protection and continue through the cave.", protectMagic);
		moveToDagCave7 = new ObjectStep(questHelper, ObjectID.DAGEXP_LADDER11, new WorldPoint(1797, 4382, 2),
			"Keep current protection and continue through the cave.", protectMagic);
		moveToDagCave8 = new ObjectStep(questHelper, ObjectID.DAGEXP_LADDER13, new WorldPoint(1802, 4369, 1),
			"Enable melee protection and continue through the cave.", protectMelee);
		moveToDagCave9 = new ObjectStep(questHelper, ObjectID.DAGEXP_LADDER15, new WorldPoint(1826, 4362, 2),
			"Keep current protection and continue through the cave.", protectMelee);
		moveToDagCave10 = new ObjectStep(questHelper, ObjectID.DAGEXP_LADDER17, new WorldPoint(1863, 4371, 1),
			"Keep current protection and continue through the cave.", protectMelee);
		moveToDagCave11 = new ObjectStep(questHelper, ObjectID.DAGEXP_LADDER19, new WorldPoint(1864, 4388, 2),
			"Keep current protection and continue through the cave.", protectMelee);
		moveToDagCave12 = new ObjectStep(questHelper, ObjectID.DAGEXP_LADDER21, new WorldPoint(1890, 4407, 1),
			"Keep current protection and continue through the cave.", protectMelee);
		moveToDagKings = new ObjectStep(questHelper, 3831, new WorldPoint(1911, 4367, 0),
			"Enter the Kings' lair.", protectMelee);
	}

	public List<QuestStep> getDisplaySteps()
	{
		return QuestUtil.toArrayList(
			moveToWaterbirth, moveToDagCave, dropPetRock, moveToAxeSpot, throwAxe, moveToDagCave1, moveToDagCave2,
			moveToDagCave3, moveToDagCave4, moveToDagCave5, moveToDagCave6, moveToDagCave7, moveToDagCave8, moveToDagCave9,
			moveToDagCave10, moveToDagCave11, moveToDagCave12, moveToDagKings);
	}
}
