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
package com.questhelper.quests.heroesquest;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemCondition;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.ObjectCondition;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.SkillCondition;
import com.questhelper.steps.conditional.VarplayerCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.HEROES_QUEST
)
public class HeroesQuest extends BasicQuestHelper
{
	ItemRequirement iceGloves, equippedIceGloves, fishingRod, fishingBait, dustyKey, dustyKeyHint, harralanderUnf, pickaxe, blamishSlime, blamishOil,
		oilRod, jailKey, rawLavaEel, lavaEel, thievesArmband, rangedMage, miscKey, blackFullHelm, blackPlatebody, blackPlatelegs, idPapers, candlestick,
		gripsKey, fireFeather, combatGear, antifireShield, varrockTeleport;

	ConditionForStep inTaverleyDungeon, hasOil, hasSlime, hasDustyKey, has70Agility, inDeepTaverleyDungeon, hasOilRod, hasRawLavaEel, hasLavaEel, hasThievesArmband,
		talkedToKatrine, talkedToStraven, hasJailKey, inJailCell, inBlackArmGang, inPhoenixBase, talkedToAlfonse, hasMiscKey, blackArmGangDoorUnlocked, gottenPapers,
		enteredMansion, talkedToGrip, inGarden, inSecretRoom, chestOpen, hasCandlestick, hasGripsKey, gripsKeyOnFloor, inTreasureRoom, talkedToCharlie,
		unlockedCandlestickBlackArm, unlockedCandlestickPhoenix, finishedBlackArm, finishedPhoenix, inIceEntrance, inIceUndergroundRoom1, inIceUndergroundRoom2,
		inIceAboveGround1, inIceAboveGround2, hasIceGloves, iceGlovesNearby, inThroneRoom, fireFeatherNearby, hasFireFeather, onEntrana;

	QuestStep talkToAchietties, talkToGerrant, makeBlamishOil, useOilOnRod, enterTaverleyDungeon, goThroughPipe, killJailerForKey, getDustyFromAdventurer,
		enterDeeperTaverley, fishLavaEel, cookLavaEel, talkToKatrine, tryToEnterTrobertHouse, talkToTrobert, enterMansion, talkToGrip, getKeyFromGrip, pickupKey,
		enterTreasureRoom, searchChest, returnToKatrine, enterPhoenixBase, talkToStraven, talkToAlfonse, getKeyFromPartner, talkToCharlie, useKeyOnDoor, pushWall,
		killGrip, enterPhoenixBaseAgain, bringCandlestickToStraven, mineEntranceRocks, takeLadder1Down, takeLadder2Up, takeLadder3Down, takeLadder4Up, takeLadder5Down,
		killIceQueen, pickupIceGloves, goToEntrana, getCandlestick, killFireBird, pickupFireFeather, finishQuest;

	ConditionalStep getThievesArmband, getIceGloves, getLavaEel, getFireFeather;

	Zone taverleyDungeon, deepTaverleyDungeon1, deepTaverleyDungeon2, deepTaverleyDungeon3, deepTaverleyDungeon4, jailCell, phoenixBase, phoenixEntry,
		garden1, garden2, secretRoom, treasureRoom, iceEntrance, iceRoom1P1, iceRoom1P2, iceRoom1P3, iceRoom2P1, iceRoom2P2, iceRoom2P3, iceRoom2P4,
		iceUp1P1, iceUp1P2, iceUp1P3, iceUp1P4, iceUp2, iceThrone1, iceThrone2, iceThrone3, entrana;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToAchietties);

		getLavaEel = new ConditionalStep(this, talkToGerrant);
		getLavaEel.addStep(hasRawLavaEel, cookLavaEel);
		getLavaEel.addStep(new Conditions(hasOilRod, inDeepTaverleyDungeon), fishLavaEel);
		getLavaEel.addStep(new Conditions(hasOilRod, inTaverleyDungeon, has70Agility), goThroughPipe);
		getLavaEel.addStep(new Conditions(hasOilRod, inTaverleyDungeon, hasDustyKey), enterDeeperTaverley);
		getLavaEel.addStep(new Conditions(hasOilRod, inTaverleyDungeon, new Conditions(LogicType.OR, inJailCell, hasJailKey)), getDustyFromAdventurer);
		getLavaEel.addStep(new Conditions(hasOilRod, inTaverleyDungeon), killJailerForKey);
		getLavaEel.addStep(new Conditions(hasOilRod), enterTaverleyDungeon);
		getLavaEel.addStep(hasOil, useOilOnRod);
		getLavaEel.addStep(hasSlime, makeBlamishOil);
		getLavaEel.setLockingCondition(hasLavaEel);

		if (inBlackArmGang.checkCondition(client))
		{
			thievesArmband.setTip("You can get another from Katrine in the Black Arm Gang base.");
			getThievesArmband = new ConditionalStep(this, talkToKatrine);
			getThievesArmband.addStep(hasCandlestick, returnToKatrine);
			getThievesArmband.addStep(new Conditions(inTreasureRoom, chestOpen), searchChest);
			getThievesArmband.addStep(inTreasureRoom, searchChest);
			getThievesArmband.addStep(hasGripsKey, enterTreasureRoom);
			getThievesArmband.addStep(gripsKeyOnFloor, pickupKey);
			getThievesArmband.addStep(talkedToGrip, getKeyFromGrip);
			getThievesArmband.addStep(enteredMansion, talkToGrip);
			getThievesArmband.addStep(gottenPapers, enterMansion);
			getThievesArmband.addStep(blackArmGangDoorUnlocked, talkToTrobert);
			getThievesArmband.addStep(talkedToKatrine, tryToEnterTrobertHouse);
			getThievesArmband.setLockingCondition(finishedBlackArm);
		}
		else
		{
			thievesArmband.setTip("You can get another from Straven in the Phoenix Gang base.");
			getThievesArmband = new ConditionalStep(this, enterPhoenixBase);
			getThievesArmband.addStep(new Conditions(hasCandlestick, inPhoenixBase), bringCandlestickToStraven);
			getThievesArmband.addStep(hasCandlestick, enterPhoenixBaseAgain);
			getThievesArmband.addStep(unlockedCandlestickPhoenix, getCandlestick);
			getThievesArmband.addStep(inSecretRoom, killGrip);
			getThievesArmband.addStep(new Conditions(inGarden, hasMiscKey), useKeyOnDoor);
			getThievesArmband.addStep(new Conditions(talkedToCharlie, hasMiscKey), pushWall);
			getThievesArmband.addStep(hasMiscKey, talkToCharlie);
			getThievesArmband.addStep(talkedToAlfonse, getKeyFromPartner);
			getThievesArmband.addStep(talkedToStraven, talkToAlfonse);
			getThievesArmband.addStep(inPhoenixBase, talkToStraven);
			getThievesArmband.setLockingCondition(finishedPhoenix);
		}

		getIceGloves = new ConditionalStep(this, mineEntranceRocks);
		getIceGloves.addStep(iceGlovesNearby, pickupIceGloves);
		getIceGloves.addStep(inThroneRoom, killIceQueen);
		getIceGloves.addStep(inIceAboveGround2, takeLadder5Down);
		getIceGloves.addStep(inIceUndergroundRoom2, takeLadder4Up);
		getIceGloves.addStep(inIceAboveGround1, takeLadder3Down);
		getIceGloves.addStep(inIceUndergroundRoom1, takeLadder2Up);
		getIceGloves.addStep(inIceEntrance, takeLadder1Down);
		getIceGloves.setLockingCondition(hasIceGloves);

		getFireFeather = new ConditionalStep(this, goToEntrana);
		getFireFeather.addStep(fireFeatherNearby, pickupFireFeather);
		getFireFeather.addStep(onEntrana, killFireBird);
		getFireFeather.setLockingCondition(hasFireFeather);

		ConditionalStep wholeQuest = new ConditionalStep(this, getLavaEel);
		wholeQuest.addStep(new Conditions(hasLavaEel, hasThievesArmband, hasIceGloves, hasFireFeather), finishQuest);
		wholeQuest.addStep(new Conditions(hasLavaEel, hasThievesArmband, hasIceGloves), getFireFeather);
		wholeQuest.addStep(new Conditions(hasLavaEel, hasThievesArmband), getIceGloves);
		wholeQuest.addStep(hasLavaEel, getThievesArmband);

		steps.put(1, wholeQuest);
		steps.put(2, wholeQuest);
		steps.put(3, wholeQuest);
		steps.put(4, wholeQuest);
		steps.put(5, wholeQuest);
		steps.put(6, wholeQuest);
		steps.put(7, wholeQuest);
		steps.put(8, wholeQuest);
		steps.put(9, wholeQuest);
		steps.put(10, wholeQuest);
		steps.put(11, wholeQuest);
		steps.put(12, wholeQuest);
		steps.put(13, wholeQuest);

		return steps;
	}

	public void setupItemRequirements()
	{
		iceGloves = new ItemRequirement("Ice gloves (obtainable in quest)", ItemID.ICE_GLOVES);
		equippedIceGloves = new ItemRequirement("Ice gloves", ItemID.ICE_GLOVES, 1, true);
		fishingRod = new ItemRequirement("Fishing rod", ItemID.FISHING_ROD);
		fishingBait = new ItemRequirement("Fishing bait", ItemID.FISHING_BAIT);
		jailKey = new ItemRequirement("Jail key", ItemID.JAIL_KEY);
		dustyKey = new ItemRequirement("Dusty key", ItemID.DUSTY_KEY);
		dustyKeyHint = new ItemRequirement("Dusty key (obtainable in quest)", ItemID.DUSTY_KEY);
		harralanderUnf = new ItemRequirement("Harralander potion (unf)", ItemID.HARRALANDER_POTION_UNF);
		pickaxe = new ItemRequirement("Any pickaxe", ItemID.BRONZE_PICKAXE);
		pickaxe.addAlternates(ItemCollections.getPickaxes());
		blamishSlime = new ItemRequirement("Blamish snail slime", ItemID.BLAMISH_SNAIL_SLIME);
		blamishOil = new ItemRequirement("Blamish oil", ItemID.BLAMISH_OIL);
		oilRod = new ItemRequirement("Oily fishing rod", ItemID.OILY_FISHING_ROD);
		lavaEel = new ItemRequirement("Lava eel", ItemID.LAVA_EEL);
		rawLavaEel = new ItemRequirement("Raw lava eel", ItemID.RAW_LAVA_EEL);

		thievesArmband = new ItemRequirement("Thieves' armband", ItemID.THIEVES_ARMBAND);

		rangedMage = new ItemRequirement("A ranged or magic attack method", -1, -1);
		miscKey = new ItemRequirement("Miscellaneous key", ItemID.MISCELLANEOUS_KEY);
		blackFullHelm = new ItemRequirement("Black full helm", ItemID.BLACK_FULL_HELM, 1, true);
		blackPlatebody = new ItemRequirement("Black platebody", ItemID.BLACK_PLATEBODY, 1, true);
		blackPlatelegs = new ItemRequirement("Black platelegs", ItemID.BLACK_PLATELEGS, 1, true);
		idPapers = new ItemRequirement("Id papers", ItemID.ID_PAPERS);
		idPapers.setTip("You can get another from Trobert in the building in east Brimhaven.");
		candlestick = new ItemRequirement("Pete's candlestick", ItemID.PETES_CANDLESTICK);
		gripsKey = new ItemRequirement("Grip's keyring", ItemID.GRIPS_KEYRING);
		fireFeather = new ItemRequirement("Fire feather", ItemID.FIRE_FEATHER);

		combatGear = new ItemRequirement("Weapons, armour and food for the Ice Queen", -1, -1);
		varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.VARROCK_TELEPORT);
		antifireShield  = new ItemRequirement("Anti-dragon shield", ItemID.ANTIDRAGON_SHIELD);
	}

	public void loadZones()
	{
		taverleyDungeon = new Zone(new WorldPoint(2816, 9668, 0), new WorldPoint(2973, 9855, 0));
		deepTaverleyDungeon1 = new Zone(new WorldPoint(2816, 9856, 0), new WorldPoint(2880, 9760, 0));
		deepTaverleyDungeon2 = new Zone(new WorldPoint(2880, 9760, 0), new WorldPoint(2907, 9793, 0));
		deepTaverleyDungeon3 = new Zone(new WorldPoint(2889, 9793, 0), new WorldPoint(2923, 9815, 0));
		deepTaverleyDungeon4 = new Zone(new WorldPoint(2907, 9772, 0), new WorldPoint(2928, 9793, 0));
		jailCell = new Zone(new WorldPoint(2928, 9683, 0), new WorldPoint(2934, 9689, 0));
		phoenixEntry = new Zone(new WorldPoint(3239, 9780, 0), new WorldPoint(3249, 9786, 0));
		phoenixBase = new Zone(new WorldPoint(3232, 9761, 0), new WorldPoint(3254, 9785, 0));
		garden1 = new Zone(new WorldPoint(2783, 3194, 0), new WorldPoint(2797, 3199, 1));
		garden2 = new Zone(new WorldPoint(2780, 3188, 0), new WorldPoint(2786, 3196, 0));
		secretRoom = new Zone(new WorldPoint(2780, 3197, 0), new WorldPoint(2782, 3198, 0));
		treasureRoom = new Zone(new WorldPoint(2764, 3196, 0), new WorldPoint(2769, 3199, 0));
		iceEntrance = new Zone(new WorldPoint(2840, 3512, 0), new WorldPoint(2851, 3522, 0));

		iceRoom1P1 = new Zone(new WorldPoint(2811, 9897, 0), new WorldPoint(2841, 9908, 0));
		iceRoom1P2 = new Zone(new WorldPoint(2832, 9907, 0), new WorldPoint(2853, 9965, 0));
		iceRoom1P3 = new Zone(new WorldPoint(2811, 9907, 0), new WorldPoint(2824, 9942, 0));

		iceRoom2P1 = new Zone(new WorldPoint(2825, 9909, 0), new WorldPoint(2829, 9970, 0));
		iceRoom2P2 = new Zone(new WorldPoint(2854, 9908, 0), new WorldPoint(2878, 9918, 0));
		iceRoom2P3 = new Zone(new WorldPoint(2878, 9920, 0), new WorldPoint(2899, 9977, 0));
		iceRoom2P4 = new Zone(new WorldPoint(2826, 9965, 0), new WorldPoint(2888, 9977, 0));

		iceUp1P1 = new Zone(new WorldPoint(2799, 3500, 0), new WorldPoint(2811, 3515, 0));
		iceUp1P2 = new Zone(new WorldPoint(2811, 3503, 0), new WorldPoint(2828, 3514, 0));
		iceUp1P3 = new Zone(new WorldPoint(2811, 3514, 0), new WorldPoint(2823, 3518, 0));
		iceUp1P4 = new Zone(new WorldPoint(2823, 3514, 0), new WorldPoint(2825, 3516, 0));

		iceUp2 = new Zone(new WorldPoint(2852, 3515, 0), new WorldPoint(2862, 3522, 0));

		iceThrone1 = new Zone(new WorldPoint(2857, 9919, 0), new WorldPoint(2873, 9965, 0));
		iceThrone2 = new Zone(new WorldPoint(2874, 9937, 0), new WorldPoint(2879, 9965, 0));
		iceThrone3 = new Zone(new WorldPoint(2860, 9917, 0), new WorldPoint(2866, 9918, 0));

		entrana = new Zone(new WorldPoint(2798, 3327,0), new WorldPoint(2878, 3394,1));

	}

	public void setupConditions()
	{
		inTaverleyDungeon = new ZoneCondition(taverleyDungeon);
		hasSlime = new ItemRequirementCondition(blamishSlime);
		hasOil = new ItemRequirementCondition(blamishOil);
		hasOilRod = new ItemRequirementCondition(oilRod);
		hasLavaEel = new ItemRequirementCondition(lavaEel);
		hasRawLavaEel = new ItemRequirementCondition(rawLavaEel);
		hasJailKey = new ItemRequirementCondition(jailKey);
		hasDustyKey = new ItemRequirementCondition(dustyKey);
		inDeepTaverleyDungeon = new ZoneCondition(deepTaverleyDungeon1, deepTaverleyDungeon2, deepTaverleyDungeon3, deepTaverleyDungeon4);
		inJailCell = new ZoneCondition(jailCell);
		has70Agility = new SkillCondition(Skill.AGILITY, 70, Operation.GREATER_EQUAL);

		hasThievesArmband = new ItemRequirementCondition(thievesArmband);
		talkedToKatrine = new VarplayerCondition(188, 7, Operation.GREATER_EQUAL);
		blackArmGangDoorUnlocked = new VarplayerCondition(188, 8, Operation.GREATER_EQUAL);
		gottenPapers = new VarplayerCondition(188, 9, Operation.GREATER_EQUAL);
		enteredMansion = new VarplayerCondition(188, 10, Operation.GREATER_EQUAL);
		talkedToGrip = new VarplayerCondition(188, 11, Operation.GREATER_EQUAL);
		unlockedCandlestickBlackArm = new VarplayerCondition(188, 12);
		finishedBlackArm = new VarplayerCondition(188, 13, Operation.GREATER_EQUAL);
		talkedToStraven = new VarplayerCondition(188, 2, Operation.GREATER_EQUAL);
		talkedToAlfonse = new VarplayerCondition(188, 3, Operation.GREATER_EQUAL);
		talkedToCharlie = new VarplayerCondition(188, 4, Operation.GREATER_EQUAL);
		unlockedCandlestickPhoenix = new VarplayerCondition(188, 5, Operation.GREATER_EQUAL);
		finishedPhoenix = new VarplayerCondition(188, 6, Operation.GREATER_EQUAL);
		hasMiscKey = new ItemRequirementCondition(miscKey);
		inSecretRoom = new ZoneCondition(secretRoom);
		inGarden = new ZoneCondition(garden1, garden2);
		hasCandlestick = new ItemRequirementCondition(candlestick);
		gripsKeyOnFloor = new ItemCondition(gripsKey);
		hasGripsKey = new ItemRequirementCondition(gripsKey);
		inTreasureRoom = new ZoneCondition(treasureRoom);
		chestOpen = new ObjectCondition(ObjectID.CHEST_2633);

		inBlackArmGang = new VarplayerCondition(146, 4, Operation.GREATER_EQUAL);
		inPhoenixBase = new ZoneCondition(phoenixBase, phoenixEntry);

		inIceEntrance = new ZoneCondition(iceEntrance);
		inIceUndergroundRoom1 = new ZoneCondition(iceRoom1P1, iceRoom1P2, iceRoom1P3);
		inIceUndergroundRoom2 = new ZoneCondition(iceRoom2P1, iceRoom2P2, iceRoom2P3, iceRoom2P4);
		inIceAboveGround1 = new ZoneCondition(iceUp1P1, iceUp1P2, iceUp1P3, iceUp1P4);
		inIceAboveGround2 = new ZoneCondition(iceUp2);

		inThroneRoom = new ZoneCondition(iceThrone1, iceThrone2, iceThrone3);

		iceGlovesNearby = new ItemCondition(ItemID.ICE_GLOVES);
		hasIceGloves = new ItemRequirementCondition(iceGloves);

		fireFeatherNearby = new ItemCondition(ItemID.FIRE_FEATHER);

		hasFireFeather = new ItemRequirementCondition(fireFeather);

		onEntrana = new ZoneCondition(entrana);
	}

	public void setupSteps()
	{
		talkToAchietties = new NpcStep(this, NpcID.ACHIETTIES, new WorldPoint(2904, 3511, 0), "Talk to Achietties outside the Heroes' Guild, south of Burthorpe.");
		talkToAchietties.addDialogStep("I'm a hero, may I apply to join?");
		talkToAchietties.addDialogStep("I'll start looking for all those things then.");
		talkToGerrant = new NpcStep(this, NpcID.GERRANT_2891, new WorldPoint(3013, 3224, 0), "You need to get an oily rod. Talk to Gerrant in Port Sarim to get some slime.");
		talkToGerrant.addDialogStep("I want to find out how to catch a lava eel.");
		makeBlamishOil = new DetailedQuestStep(this, "Combine the harralander potion (unf) with the blamish snail slime.", harralanderUnf, blamishSlime);
		useOilOnRod = new DetailedQuestStep(this, "Use the Blamish oil on your fishing rod.", blamishOil, fishingRod);

		if (client.getRealSkillLevel(Skill.AGILITY) >= 70)
		{
			enterTaverleyDungeon = new ObjectStep(this, ObjectID.LADDER_16680, new WorldPoint(2884, 3397, 0),
				"Go to Taverley Dungeon. As you're 70 Agility, you don't need a dusty key.", oilRod, fishingBait);
		}
		else
		{
			enterTaverleyDungeon = new ObjectStep(this, ObjectID.LADDER_16680, new WorldPoint(2884, 3397, 0),
				"Go to Taverley Dungeon. Bring a dusty key if you have one, otherwise you can get one in the dungeon.", oilRod, fishingBait, dustyKey);
		}

		goThroughPipe = new ObjectStep(this, ObjectID.OBSTACLE_PIPE_16509, new WorldPoint(2888, 9799, 0), "Squeeze through the obstacle pipe.");

		killJailerForKey = new NpcStep(this, NpcID.JAILER, new WorldPoint(2930, 9692, 0), "Travel through Taverley Dungeon until you reach the Black Knights' Base. Kill the Jailer in the east side of the base for a jail key.");
		getDustyFromAdventurer = new NpcStep(this, NpcID.VELRAK_THE_EXPLORER, new WorldPoint(2930, 9685, 0), "Use the jail key on the south door and talk to Velrak for a dusty key.", jailKey);
		getDustyFromAdventurer.addDialogStep("So... do you know anywhere good to explore?");
		enterDeeperTaverley = new ObjectStep(this, ObjectID.GATE_2623, new WorldPoint(2924, 9803, 0), "Enter the gate to the deeper Taverley dungeon.", dustyKey);
		fishLavaEel = new NpcStep(this, NpcID.FISHING_SPOT_4928, new WorldPoint(2898, 9764, 0), "Fish a lava eel from the lava south of the blue dragons.", oilRod, fishingBait);
		cookLavaEel = new DetailedQuestStep(this, "Cook the Lava Eel at a range. You cannot fail this.", rawLavaEel);

		enterTaverleyDungeon.addSubSteps(goThroughPipe, killJailerForKey, getDustyFromAdventurer, enterDeeperTaverley);

		/* Black Arm Gang steps */
		talkToKatrine = new NpcStep(this, NpcID.KATRINE, new WorldPoint(3185, 3385, 0), "Talk to Katrine at the Black Gang base in South west Varrock.");
		talkToKatrine.addDialogStep("Is there any way I can get the rank of master thief?");

		tryToEnterTrobertHouse = new ObjectStep(this, ObjectID.DOOR_2626, new WorldPoint(2811, 3170, 0), "Try to enter the house in east Brimhaven.");
		tryToEnterTrobertHouse.addDialogStep("Four leaved clover.");
		talkToTrobert = new NpcStep(this, NpcID.TROBERT, new WorldPoint(2807, 3174, 0), "Talk to Trobert inside.");
		talkToTrobert.addDialogStep("So can you help me get Scarface Pete's candlesticks?");
		talkToTrobert.addDialogStep("I volunteer to undertake that mission!");
		enterMansion = new ObjectStep(this, ObjectID.DOOR_2627, new WorldPoint(2774, 3187, 0), "Equip your black armour and enter the mansion in the north west of Brimhaven.", idPapers, blackFullHelm, blackPlatebody, blackPlatelegs);
		talkToGrip = new NpcStep(this, NpcID.GRIP, new WorldPoint(2775, 3192, 0), "Talk to Grip.", idPapers);
		getKeyFromGrip = new ObjectStep(this, ObjectID.CUPBOARD_2635, new WorldPoint(2776, 3196, 0),
			"Get a miscellaneous key from Grip then give it to your partner. Once you've done that, wait for your partner to enter the north east room of the mansion." +
				"After that, search the cupboard in the north room to lure Grip there. Your partner should kill them.");
		getKeyFromGrip.addDialogStep("So what do my duties involve?");
		getKeyFromGrip.addDialogStep("Anything I can do now?");
		getKeyFromGrip.addDialogStep("He won't notice me having a quick look.");

		pickupKey = new DetailedQuestStep(this, "Pick up Grip's keyring", gripsKey);
		enterTreasureRoom = new ObjectStep(this, ObjectID.DOOR_2621, new WorldPoint(2764, 3197, 0), "Use Grip's keyring on the treasure room door.", gripsKey);
		enterTreasureRoom.addIcon(ItemID.GRIPS_KEYRING);
		searchChest = new ObjectStep(this, ObjectID.CHEST_2632, new WorldPoint(2766, 3199, 0), "Search the chest in the treasure room for two candlesticks.");
		((ObjectStep)(searchChest)).addAlternateObjects(ObjectID.CHEST_2633);

		returnToKatrine = new NpcStep(this, NpcID.KATRINE, new WorldPoint(3185, 3385, 0), "Give one of Pete's candlesticks to your partner. Afterwards, return to Katrine with Pete's candlestick", candlestick);
		returnToKatrine.addDialogStep("I have a candlestick now.");

		/* Phoenix Gang steps */
		enterPhoenixBase = new ObjectStep(this, ObjectID.LADDER_11803, new WorldPoint(3244, 3383, 0), "Head into the Phoenix Gang's base in south Varrock.");
		talkToStraven = new NpcStep(this, NpcID.STRAVEN, new WorldPoint(3247, 9781, 0), "Talk to Straven.");
		talkToAlfonse = new NpcStep(this, NpcID.ALFONSE_THE_WAITER, new WorldPoint(2792, 3186, 0), "Talk to Alfonse the Waiter in the restaurant in Brimhaven.", rangedMage);
		talkToAlfonse.addDialogStep("Do you sell Gherkins?");
		getKeyFromPartner = new DetailedQuestStep(this, "You'll need your partner to give you a miscellaneous key.");
		talkToCharlie = new NpcStep(this, NpcID.CHARLIE_THE_COOK, new WorldPoint(2790, 3191, 0), "Talk to Charlie the Cook in the back of the restaurant.");
		talkToCharlie.addDialogStep("I'm looking for a gherkin...");
		talkToCharlie.addDialogStep("I want to steal Scarface Pete's candlesticks.");
		pushWall = new ObjectStep(this, ObjectID.WALL_2629, new WorldPoint(2787, 3190, 0), "Push the wall to enter Pete's garden.");
		useKeyOnDoor = new ObjectStep(this, ObjectID.DOOR_2622, new WorldPoint(2781, 3197, 0), "Use the misc key on the door to the north west.", miscKey);
		useKeyOnDoor.addIcon(ItemID.MISCELLANEOUS_KEY);
		killGrip = new NpcStep(this, NpcID.GRIP, new WorldPoint(2775, 3192, 0), "Wait for your partner to lure Grip into the room next to yours, and kill him with magic/ranged. Afterwards, trade your partner for a candlestick.");
		getCandlestick = new DetailedQuestStep(this, "Get your candlestick from your partner.");
		killGrip.addSubSteps(getCandlestick);
		enterPhoenixBaseAgain = new ObjectStep(this, ObjectID.LADDER_11803, new WorldPoint(3244, 3383, 0), "Bring the candlestick back to Straven.");
		bringCandlestickToStraven = new NpcStep(this, NpcID.STRAVEN, new WorldPoint(3247, 9781, 0), "Bring the candlestick back to Straven.");
		bringCandlestickToStraven.addSubSteps(enterPhoenixBaseAgain);

		mineEntranceRocks = new ObjectStep(this, ObjectID.ROCK_SLIDE, new WorldPoint(2839, 3518, 0), "Head to White Wolf Mountain, and mine a rockslide in the northern part.", pickaxe);
		takeLadder1Down = new ObjectStep(this, ObjectID.LADDER_16680, new WorldPoint(2848, 3513, 0), "Take the south east ladder down.");
		takeLadder2Up = new ObjectStep(this, ObjectID.LADDER_17385, new WorldPoint(2824, 9907, 0), "Follow the tunnel south, then go up the ladder there.");
		takeLadder3Down = new ObjectStep(this, ObjectID.LADDER_16680, new WorldPoint(2827, 3510, 0), "Take the east ladder down.");
		takeLadder4Up = new ObjectStep(this, ObjectID.LADDER_17385, new WorldPoint(2857, 9917, 0), "Follow the path around until you reach a ladder, then climb it.");
		takeLadder5Down = new ObjectStep(this, ObjectID.LADDER_16680, new WorldPoint(2859, 3519, 0), "Take the south ladder down.");
		killIceQueen = new NpcStep(this, NpcID.ICE_QUEEN, new WorldPoint(2865, 9948, 0), "Kill the Ice Queen for ice gloves.");
		pickupIceGloves = new DetailedQuestStep(this, "Pick up the ice gloves.", iceGloves);
		killIceQueen.addSubSteps(pickupIceGloves);

		goToEntrana = new NpcStep(this, NpcID.MONK_OF_ENTRANA_1167, new WorldPoint(3047, 3236, 0), "Travel to Entrana with the monks in Port Sarim.", equippedIceGloves);
		killFireBird = new NpcStep(this, NpcID.ENTRANA_FIREBIRD, new WorldPoint(2840, 3374, 0), "Kill the entrana firebird on the north of Entrana.", equippedIceGloves);
		pickupFireFeather = new ItemStep(this, "Pick up the fire feather.", fireFeather);

		finishQuest =  new NpcStep(this, NpcID.ACHIETTIES, new WorldPoint(2904, 3511, 0), "Bring Achietties all the items to finish.", fireFeather, thievesArmband, lavaEel);
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Ice Queen (level 111) for ice gloves");
		if (!inBlackArmGang.checkCondition(client))
		{
			reqs.add("Grip (level 26)");
		}
		return reqs;
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(fishingRod);
		reqs.add(fishingBait);
		reqs.add(harralanderUnf);
		reqs.add(pickaxe);
		reqs.add(iceGloves);
		if (inBlackArmGang.checkCondition(client))
		{
			reqs.add(blackFullHelm);
			reqs.add(blackPlatebody);
			reqs.add(blackPlatelegs);
		}
		else
		{
			reqs.add(rangedMage);
		}
		if (client.getRealSkillLevel(Skill.AGILITY) < 70)
		{
			reqs.add(dustyKeyHint);
		}
		return reqs;
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(combatGear);
		reqs.add(varrockTeleport);
		reqs.add(antifireShield);

		return reqs;
	}

	@Override
	public ArrayList<String> getNotes()
	{
		ArrayList<String> reqs = new ArrayList<>();
		if (inBlackArmGang.checkCondition(client))
		{
			reqs.add("You will need to find another player who joined the Phoenix Gang during the Shield of Arrav quest to assist you. If one of you is an Ironman, you can use the necessary items on one another to trade them.");
		}
		else
		{
			reqs.add("You will need to find another player who joined the Black Arm Gang during the Shield of Arrav quest to assist you. If one of you is an Ironman, you can use the necessary items on one another to trade them.");
		}
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails firstPanel = new PanelDetails("Start the quest", new ArrayList<>(Collections.singletonList(talkToAchietties)));

		PanelDetails secondPanel = new PanelDetails("Get the Lava Eel", new ArrayList<>(Arrays.asList(talkToGerrant, makeBlamishOil, useOilOnRod, enterTaverleyDungeon, fishLavaEel, cookLavaEel)), harralanderUnf, fishingRod, fishingBait, dustyKey);
		secondPanel.setLockingStep(getLavaEel);
		PanelDetails thirdPanel;

		if (inBlackArmGang.checkCondition(client))
		{
			thirdPanel = new PanelDetails("Get thieves' armband",
				new ArrayList<>(Arrays.asList(talkToKatrine, talkToTrobert, enterMansion, talkToGrip, getKeyFromGrip, pickupKey, enterTreasureRoom, searchChest, returnToKatrine)),
				blackFullHelm, blackPlatebody, blackPlatelegs);
		}
		else
		{
			thirdPanel = new PanelDetails("Get thieves' armband", new ArrayList<>(
				Arrays.asList(talkToStraven, talkToAlfonse, getKeyFromPartner, talkToCharlie, pushWall, useKeyOnDoor, killGrip, bringCandlestickToStraven)));
		}

		thirdPanel.setLockingStep(getThievesArmband);

		PanelDetails fourthPanel = new PanelDetails("Get ice gloves", new ArrayList<>(Arrays.asList(mineEntranceRocks, takeLadder1Down, takeLadder2Up, takeLadder3Down, takeLadder4Up, takeLadder5Down, killIceQueen)));
		fourthPanel.setLockingStep(getIceGloves);

		PanelDetails fifthPanel = new PanelDetails("Get fire feather", new ArrayList<>(Arrays.asList(goToEntrana, killFireBird)));
		fifthPanel.setLockingStep(getFireFeather);

		PanelDetails sixthPanel = new PanelDetails("Finish off", new ArrayList<>(Arrays.asList(finishQuest)), fireFeather, thievesArmband, lavaEel);

		allSteps.add(firstPanel);
		allSteps.add(secondPanel);
		allSteps.add(thirdPanel);
		allSteps.add(fourthPanel);
		allSteps.add(fifthPanel);
		allSteps.add(sixthPanel);
		return allSteps;
	}
}
