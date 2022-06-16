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
package com.questhelper.quests.insearchofthemyreque;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
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
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.IN_SEARCH_OF_THE_MYREQUE
)
public class InSearchOfTheMyreque extends BasicQuestHelper
{
	//Items Required
	ItemRequirement combatGear, steelLong, steelSword2, steelMace, steelWarhammer, steeldagger, steelNails225, druidPouch5, hammer, plank6,
		coins10OrCharos, plank3, plank2, plank1, steelNails75, steelNails150;

	//Items Recommended
	ItemRequirement morttonTeleport;

	Requirement hasEnoughPouch, repairedBridge1, repairedBridge2, repairedBridge3, onBridge, onEntranceIsland, onQuestion1, onQuestion2,
		onQuestion3, onQuestion4, onQuestion5, onQuestion6, inCaves, inMyrequeCave, talkedToHarold, talkedToRadigad, talkedToSani, talkedToPolmafi,
		talkedToIvan;

	QuestStep talkToVanstrom, fillDruidPouch, talkToCyreg, boardBoat, climbTree, climbTree2, climbTree3, climbTree4, repairBridge1, repairBridge2, repairBridge3,
		talkToCurpile, talkToCurpile1, talkToCurpile2, talkToCurpile3, talkToCurpile4, talkToCurpile5, talkToCurpile6, enterDoors, enterCave, talkToMembers,
		talkToVeliaf, talkToHarold, talkToRadigad, talkToSani, talkToPolmafi, talkToIvan, talkToVeliafAgain, talkToVeliafForCutscene, killHellhound, talkToVeliafToLeave, leaveCave, goUpToCanifis,
		talkToStranger, climbTreeHellhound, enterCaveHellhound, enterDoorsHellhound, climbTreeLeave, enterCaveLeave, enterDoorsLeave;

	//Zones
	Zone entranceIsland, bridge, caves, myrequeCave;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToVanstrom);

		ConditionalStep goTalkToCyreg = new ConditionalStep(this, fillDruidPouch);
		goTalkToCyreg.addStep(hasEnoughPouch, talkToCyreg);
		steps.put(5, goTalkToCyreg);
		steps.put(10, goTalkToCyreg);
		steps.put(15, goTalkToCyreg);

		steps.put(20, boardBoat);

		ConditionalStep goTalkToCurpile = new ConditionalStep(this, climbTree);
		goTalkToCurpile.addStep(new Conditions(onEntranceIsland, onQuestion6), talkToCurpile6);
		goTalkToCurpile.addStep(new Conditions(onEntranceIsland, onQuestion5), talkToCurpile5);
		goTalkToCurpile.addStep(new Conditions(onEntranceIsland, onQuestion4), talkToCurpile4);
		goTalkToCurpile.addStep(new Conditions(onEntranceIsland, onQuestion3), talkToCurpile3);
		goTalkToCurpile.addStep(new Conditions(onEntranceIsland, onQuestion2), talkToCurpile2);
		goTalkToCurpile.addStep(new Conditions(onEntranceIsland, onQuestion1), talkToCurpile1);
		goTalkToCurpile.addStep(new Conditions(onEntranceIsland), talkToCurpile);
		goTalkToCurpile.addStep(new Conditions(onBridge, repairedBridge2), repairBridge3);
		goTalkToCurpile.addStep(new Conditions(onBridge, repairedBridge1), repairBridge2);
		goTalkToCurpile.addStep(onBridge, repairBridge1);
		goTalkToCurpile.addStep(repairedBridge3, climbTree4);
		goTalkToCurpile.addStep(repairedBridge2, climbTree3);
		goTalkToCurpile.addStep(repairedBridge1, climbTree2);
		steps.put(25, goTalkToCurpile);
		steps.put(52, goTalkToCurpile);

		ConditionalStep goTalkToVeliaf = new ConditionalStep(this, climbTree4);
		goTalkToVeliaf.addStep(inMyrequeCave, talkToVeliaf);
		goTalkToVeliaf.addStep(inCaves, enterCave);
		goTalkToVeliaf.addStep(onEntranceIsland, enterDoors);
		steps.put(55, goTalkToVeliaf);
		steps.put(60, goTalkToVeliaf);

		ConditionalStep talkToCrew = new ConditionalStep(this, climbTree4);
		talkToCrew.addStep(new Conditions(inMyrequeCave, talkedToHarold, talkedToRadigad, talkedToSani, talkedToPolmafi, talkedToIvan), talkToVeliafAgain);
		talkToCrew.addStep(new Conditions(inMyrequeCave, talkedToHarold, talkedToRadigad, talkedToSani, talkedToPolmafi), talkToIvan);
		talkToCrew.addStep(new Conditions(inMyrequeCave, talkedToHarold, talkedToRadigad, talkedToSani), talkToPolmafi);
		talkToCrew.addStep(new Conditions(inMyrequeCave, talkedToHarold, talkedToRadigad), talkToSani);
		talkToCrew.addStep(new Conditions(inMyrequeCave, talkedToHarold), talkToRadigad);
		talkToCrew.addStep(inMyrequeCave, talkToHarold);
		talkToCrew.addStep(inCaves, enterCave);
		talkToCrew.addStep(onEntranceIsland, enterDoors);
		steps.put(65, talkToCrew);

		ConditionalStep goTalkToVeliafAgain = new ConditionalStep(this, climbTree4);
		goTalkToVeliafAgain.addStep(inMyrequeCave, talkToVeliafForCutscene);
		goTalkToVeliafAgain.addStep(inCaves, enterCave);
		goTalkToVeliafAgain.addStep(onEntranceIsland, enterDoors);
		steps.put(67, goTalkToVeliafAgain);
		steps.put(70, goTalkToVeliafAgain);

		ConditionalStep goKillSkeletonHellhound = new ConditionalStep(this, climbTreeHellhound);
		goKillSkeletonHellhound.addStep(inMyrequeCave, killHellhound);
		goKillSkeletonHellhound.addStep(inCaves, enterCaveHellhound);
		goKillSkeletonHellhound.addStep(onEntranceIsland, enterDoorsHellhound);
		steps.put(71, goKillSkeletonHellhound);
		steps.put(72, goKillSkeletonHellhound);
		steps.put(73, goKillSkeletonHellhound);
		steps.put(74, goKillSkeletonHellhound);
		steps.put(75, goKillSkeletonHellhound);
		steps.put(76, goKillSkeletonHellhound);
		steps.put(77, goKillSkeletonHellhound);
		steps.put(78, goKillSkeletonHellhound);
		steps.put(79, goKillSkeletonHellhound);
		steps.put(80, goKillSkeletonHellhound);

		ConditionalStep goLearnExit = new ConditionalStep(this, climbTreeLeave);
		goLearnExit.addStep(inMyrequeCave, talkToVeliafToLeave);
		goLearnExit.addStep(inCaves, enterCaveLeave);
		goLearnExit.addStep(onEntranceIsland, enterDoorsLeave);
		steps.put(85, goLearnExit);

		ConditionalStep tryLeavingExit = new ConditionalStep(this, climbTreeLeave);
		tryLeavingExit.addStep(inMyrequeCave, leaveCave);
		tryLeavingExit.addStep(inCaves, goUpToCanifis);
		tryLeavingExit.addStep(onEntranceIsland, enterDoorsLeave);
		steps.put(90, tryLeavingExit);
		steps.put(95, tryLeavingExit);

		ConditionalStep finishingOff = new ConditionalStep(this, talkToStranger);
		finishingOff.addStep(inMyrequeCave, leaveCave);
		finishingOff.addStep(inCaves, goUpToCanifis);
		steps.put(97, finishingOff);
		steps.put(100, finishingOff);
		steps.put(105, finishingOff);
		return steps;
	}

	@Override
	public void setupRequirements()
	{
		steelLong = new ItemRequirement("Steel longsword", ItemID.STEEL_LONGSWORD);
		steelSword2 = new ItemRequirement("Steel sword", ItemID.STEEL_SWORD, 2);
		steelMace = new ItemRequirement("Steel mace", ItemID.STEEL_MACE);
		steelWarhammer = new ItemRequirement("Steel warhammer", ItemID.STEEL_WARHAMMER);
		steeldagger = new ItemRequirement("Steel dagger", ItemID.STEEL_DAGGER);
		steelNails225 = new ItemRequirement("Steel nails", ItemID.STEEL_NAILS, 225);
		druidPouch5 = new ItemRequirement("Charges in a druid pouch", ItemID.DRUID_POUCH_2958, 5);
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed();
		plank6 = new ItemRequirement("Plank", ItemID.PLANK, 6);
		plank3 = new ItemRequirement("Plank", ItemID.PLANK, 3);
		plank2 = new ItemRequirement("Plank", ItemID.PLANK, 2);
		plank1 = new ItemRequirement("Plank", ItemID.PLANK);
		steelNails150 = new ItemRequirement("Steel nails", ItemID.STEEL_NAILS, 150);
		steelNails75 = new ItemRequirement("Steel nails", ItemID.STEEL_NAILS, 75);
		coins10OrCharos = new ItemRequirements(LogicType.OR, "10 coins or a Ring of Charos (a)",
			new ItemRequirement("Ring of Charos (a)", ItemID.RING_OF_CHAROSA),
			new ItemRequirement("Coins", ItemCollections.COINS, 10));
		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		morttonTeleport = new ItemRequirement("Teleport to Mort'ton, such as minigame teleport or Barrows Teleport", ItemID.MORTTON_TELEPORT);
		morttonTeleport.addAlternates(ItemID.BARROWS_TELEPORT);
	}

	public void loadZones()
	{
		bridge = new Zone(new WorldPoint(3503, 3426, 0), new WorldPoint(3503, 3429, 0));
		entranceIsland = new Zone(new WorldPoint(3480, 3430, 0), new WorldPoint(3513, 3464, 0));
		caves = new Zone(new WorldPoint(3450, 9792, 0), new WorldPoint(3504, 9847, 2));
		myrequeCave = new Zone(new WorldPoint(3502, 9829, 0), new WorldPoint(3515, 9846, 0));
	}

	public void setupConditions()
	{
		hasEnoughPouch = druidPouch5;
		repairedBridge1 = new VarbitRequirement(176, 1);
		repairedBridge2 = new VarbitRequirement(177, 1);
		repairedBridge3 = new VarbitRequirement(178, 1);

		onBridge = new ZoneRequirement(bridge);
		onEntranceIsland = new ZoneRequirement(entranceIsland);
		inCaves = new ZoneRequirement(caves);
		inMyrequeCave = new ZoneRequirement(myrequeCave);

		onQuestion1 = new WidgetTextRequirement(219, 1, 0, "female");
		onQuestion2 = new WidgetTextRequirement(219, 1, 0, "youngest");
		onQuestion3 = new WidgetTextRequirement(219, 1, 0, "leader");
		onQuestion4 = new WidgetTextRequirement(219, 1, 0, "boatman");
		onQuestion5 = new WidgetTextRequirement(219, 1, 0, "vampyre");
		onQuestion6 = new WidgetTextRequirement(219, 1, 0, "scholar");

		talkedToSani = new VarbitRequirement(2496, true, 0);
		talkedToHarold = new VarbitRequirement(2496, true, 1);
		talkedToRadigad = new VarbitRequirement(2496, true, 2);
		talkedToPolmafi = new VarbitRequirement(2496, true, 3);
		talkedToIvan = new VarbitRequirement(2496, true, 4);
	}

	public void setupSteps()
	{
		talkToVanstrom = new NpcStep(this, NpcID.VANSTROM_KLAUSE_5056, new WorldPoint(3503, 3477, 0),
			"Talk to Vanstrom Klause in the Canifis pub.");
		talkToVanstrom.addDialogStep("Yes.");
		fillDruidPouch = new DetailedQuestStep(this, "Fill a druid pouch with at least 5 mort myre items. Try to have more in case a ghast hits you.", druidPouch5);
		fillDruidPouch.addDialogStep("I'd better be off.");
		talkToCyreg = new NpcStep(this, NpcID.CYREG_PADDLEHORN, new WorldPoint(3522, 3284, 0), "Talk to Cyreg in Mort'ton.",
			druidPouch5, steelLong, steelSword2, steeldagger, steelMace, steelNails225, steelWarhammer, plank6, hammer, coins10OrCharos);
		talkToCyreg.addDialogSteps("I'd better be off.",
			"Well, I guess they'll just die without weapons.", "Resourceful enough to get their own steel weapons?",
			"If you don't tell me, their deaths are on your head!", "What kind of a man are you to say that you don't care?",
			"Here are some planks for you.");
		boardBoat = new ObjectStep(this, ObjectID.SWAMP_BOATY_6969, new WorldPoint(3524, 3285, 0), "Board the swamp boaty in Mort'ton.",
			steelLong, steelSword2, steeldagger, steelMace, steelNails225, steelWarhammer, plank3, hammer, coins10OrCharos);
		boardBoat.addDialogStep("Yes. I'll pay the ten gold.");
		climbTree = new ObjectStep(this, NullObjectID.NULL_5003, new WorldPoint(3502, 3426, 0),
			"Climb the tree to the north of the boat.", steelLong, steelSword2, steeldagger, steelMace, steelNails225, steelWarhammer, plank3, hammer);
		climbTree2 = new ObjectStep(this, NullObjectID.NULL_5003, new WorldPoint(3502, 3426, 0),
			"Climb the tree to the north of the boat.", steelLong, steelSword2, steeldagger, steelMace, steelNails150, steelWarhammer, plank2, hammer);
		climbTree3 = new ObjectStep(this, NullObjectID.NULL_5003, new WorldPoint(3502, 3426, 0),
			"Climb the tree to the north of the boat.", steelLong, steelSword2, steeldagger, steelMace, steelNails75, steelWarhammer, plank1, hammer);
		climbTree4 = new ObjectStep(this, NullObjectID.NULL_5003, new WorldPoint(3502, 3426, 0),
			"Climb the tree to the north of the boat.", steelLong, steelSword2, steeldagger, steelMace, steelWarhammer);
		climbTree.addSubSteps(climbTree2, climbTree3, climbTree4);

		repairBridge1 = new ObjectStep(this, NullObjectID.NULL_26245, new WorldPoint(3502, 3428, 0), "Repair the bridge.", steelNails225, plank3, hammer);
		repairBridge2 = new ObjectStep(this, NullObjectID.NULL_26246, new WorldPoint(3502, 3429, 0), "Repair the bridge.", steelNails150, plank2, hammer);
		repairBridge3 = new ObjectStep(this, NullObjectID.NULL_26247, new WorldPoint(3502, 3430, 0), "Repair the bridge.", steelNails75, plank1, hammer);
		repairBridge1.addSubSteps(repairBridge2, repairBridge3);
		talkToCurpile = new NpcStep(this, NpcID.CURPILE_FYOD, new WorldPoint(3508, 3440, 0), "Talk to Curpile Fyod.");
		talkToCurpile.addDialogStep("I've come to help the Myreque. I've brought weapons.");
		talkToCurpile1 = new NpcStep(this, NpcID.CURPILE_FYOD, new WorldPoint(3508, 3440, 0), "Talk to Curpile Fyod.");
		talkToCurpile1.addDialogStep("Sani Piliu.");
		talkToCurpile2 = new NpcStep(this, NpcID.CURPILE_FYOD, new WorldPoint(3508, 3440, 0), "Talk to Curpile Fyod.");
		talkToCurpile2.addDialogStep("Ivan Strom.");
		talkToCurpile3 = new NpcStep(this, NpcID.CURPILE_FYOD, new WorldPoint(3508, 3440, 0), "Talk to Curpile Fyod.");
		talkToCurpile3.addDialogStep("Veliaf Hurtz.");
		talkToCurpile4 = new NpcStep(this, NpcID.CURPILE_FYOD, new WorldPoint(3508, 3440, 0), "Talk to Curpile Fyod.");
		talkToCurpile4.addDialogStep("Cyreg Paddlehorn.");
		talkToCurpile5 = new NpcStep(this, NpcID.CURPILE_FYOD, new WorldPoint(3508, 3440, 0), "Talk to Curpile Fyod.");
		talkToCurpile5.addDialogStep("Drakan.");
		talkToCurpile6 = new NpcStep(this, NpcID.CURPILE_FYOD, new WorldPoint(3508, 3440, 0), "Talk to Curpile Fyod.");
		talkToCurpile6.addDialogStep("Polmafi Ferdygris.");
		talkToCurpile.addSubSteps(talkToCurpile1, talkToCurpile3, talkToCurpile4, talkToCurpile5, talkToCurpile6);
		enterDoors = new ObjectStep(this, ObjectID.WOODEN_DOORS_5061, new WorldPoint(3509, 3448, 0), "Enter the wooden doors north of Curpile.",
			steelLong, steelSword2, steeldagger, steelMace, steelWarhammer);
		enterCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_5046, new WorldPoint(3492, 9823, 0), "Enter the cave to the north on the east side.");
		talkToHarold = new NpcStep(this, NpcID.HAROLD_EVANS, new WorldPoint(3504, 9833, 0), "Talk to Harold Evans.");
		talkToRadigad = new NpcStep(this, NpcID.RADIGAD_PONFIT, new WorldPoint(3510, 9833, 0), "Talk to Radigad Ponfit.");
		talkToSani = new NpcStep(this, NpcID.SANI_PILIU, new WorldPoint(3511, 9838, 0), "Talk to Sani Piliu.");
		talkToPolmafi = new NpcStep(this, NpcID.POLMAFI_FERDYGRIS, new WorldPoint(3512, 9839, 0), "Talk to Polmafi Ferdygris.");
		talkToIvan = new NpcStep(this, NpcID.IVAN_STROM_5053, new WorldPoint(3512, 9843, 0), "Talk to Ivan Strom.");
		talkToMembers = new DetailedQuestStep(this, "Talk to each of the members of the Myreque.");
		talkToMembers.addSubSteps(talkToHarold, talkToRadigad, talkToSani, talkToPolmafi, talkToIvan);

		talkToVeliaf = new NpcStep(this, NpcID.VELIAF_HURTZ_5048, new WorldPoint(3506, 9837, 0), "Talk to Veliaf Hurtz.");
		talkToVeliafForCutscene = new NpcStep(this, NpcID.VELIAF_HURTZ_5048, new WorldPoint(3506, 9837, 0), "Talk to Veliaf Hurtz.");
		talkToVeliafAgain = new NpcStep(this, NpcID.VELIAF_HURTZ_5048, new WorldPoint(3506, 9837, 0), "Talk to Veliaf Hurtz again and give him the steel weapons. Be ready to fight the Skeleton Hellhound", steelLong, steelSword2, steelWarhammer, steelMace, steeldagger);
		talkToVeliafAgain.addDialogStep("Let's talk about the weapons.");
		talkToVeliafAgain.addSubSteps(talkToVeliafForCutscene);

		climbTreeHellhound = new ObjectStep(this, NullObjectID.NULL_5003, new WorldPoint(3502, 3426, 0),
			"Climb the tree to the north of the boat.", combatGear);
		enterDoorsHellhound = new ObjectStep(this, ObjectID.WOODEN_DOORS_5061, new WorldPoint(3509, 3448, 0), "Enter the wooden doors north of Curpile.", combatGear);
		enterCaveHellhound = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_5046, new WorldPoint(3492, 9823, 0), "Enter the cave to the north on the east side.");
		killHellhound = new NpcStep(this, NpcID.SKELETON_HELLHOUND, new WorldPoint(3506, 9837, 0), "Kill the Skeleton Hellhound.");
		killHellhound.addSubSteps(climbTreeHellhound, enterCaveHellhound, enterDoorsHellhound);

		climbTreeLeave = new ObjectStep(this, NullObjectID.NULL_5003, new WorldPoint(3502, 3426, 0),
			"Climb the tree to the north of the boat.", steelLong, steelSword2, steeldagger, steelMace, steelWarhammer);
		enterDoorsLeave = new ObjectStep(this, ObjectID.WOODEN_DOORS_5061, new WorldPoint(3509, 3448, 0), "Enter the wooden doors north of Curpile.");
		enterCaveLeave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_5046, new WorldPoint(3492, 9823, 0), "Enter the cave to the north on the east side.");
		talkToVeliafToLeave = new NpcStep(this, NpcID.VELIAF_HURTZ_5048, new WorldPoint(3506, 9837, 0), "Talk to Veliaf Hurtz to learn the way out.");
		talkToVeliafToLeave.addSubSteps(climbTreeLeave, enterDoorsLeave, enterCaveLeave);

		leaveCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_5046, new WorldPoint(3505, 9831, 0), "Leave the cave.");
		leaveCave.addDialogStep("Okay, thanks.");
		goUpToCanifis = new ObjectStep(this, ObjectID.LADDER_5054, new WorldPoint(3477, 9846, 0), "Leave up the ladder in the north of the cave.");
		talkToStranger = new NpcStep(this, NpcID.STRANGER_5055, new WorldPoint(3503, 3477, 0),
			"Talk to the Stranger in the Canifis pub to finish the quest!");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(steelLong, steelSword2, steelMace, steelWarhammer, steeldagger, steelNails225, coins10OrCharos, druidPouch5, hammer, plank6);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(combatGear, morttonTeleport);
	}

	@Override
	public List<String> getNotes()
	{
		return Collections.singletonList("Whilst in Mort Myre, the Ghasts will occasionally rot the food in your inventory and steal charges from your Druid Pouch.");
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Skeleton hellhound (level 97)");
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
				new ExperienceReward(Skill.ATTACK, 600),
				new ExperienceReward(Skill.DEFENCE, 600),
				new ExperienceReward(Skill.STRENGTH, 600),
				new ExperienceReward(Skill.HITPOINTS, 600),
				new ExperienceReward(Skill.CRAFTING, 600));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("A quick route to Mor'ton"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Helping the Myreque",
			Arrays.asList(talkToVanstrom, fillDruidPouch, talkToCyreg, boardBoat, climbTree, repairBridge1, talkToCurpile, enterDoors,
				enterCave, talkToVeliaf, talkToMembers, talkToVeliafAgain, killHellhound, talkToVeliafToLeave, leaveCave, goUpToCanifis, talkToStranger),
			steelLong, steelSword2, steelMace, steelWarhammer, steeldagger, steelNails225, coins10OrCharos, druidPouch5, hammer, plank6));

		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return Arrays.asList(
			new QuestRequirement(QuestHelperQuest.NATURE_SPIRIT, QuestState.FINISHED),
			new SkillRequirement(Skill.AGILITY, 25, true)
		);
	}
}
