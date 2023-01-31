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
package com.questhelper.quests.monkeymadnessi;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.rewards.ItemReward;
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
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;

@QuestDescriptor(
	quest = QuestHelperQuest.MONKEY_MADNESS_I
)
public class MonkeyMadnessI extends BasicQuestHelper
{
	//Items Required
	ItemRequirement monkeyBonesOrCorpse, ballOfWool, goldBar, royalSeal, narnodesOrders, monkeyDentures, mould, monkeyDenturesHighlight, mouldHighlight, barHighlight, enchantedBar,
		enchantedBarHighlight, ballOfWoolHighlight, unstrungAmuletHighlight, amulet, banana5, amuletWorn, talisman, talismanHighlight, karamjanGreegree, monkeyBonesOrCorpseHighlight,
		monkey, karamjanGreegreeEquipped, sigilEquipped, bananaReq;

	//Items Recommended
	ItemRequirement combatGear, antipoison;

	Requirement inStronghold, inFloor1, inFloor2, inFloor3, inKaramja, talkedToCaranock, reportedBackToNarnode, inHangar, startedPuzzle, solvedPuzzle,
		talkedToDaeroAfterPuzzle, onCrashIsland, talkedToLumdo, talkedToWaydar, onApeAtollSouth, inPrison, onApeAtollNorth, talkedToGarkor, inDentureBuilding,
		inMouldRoom, hadDenturesAndMould, inZooknockDungeon, talkedToZooknock, givenDentures, givenBar, givenMould, hadEnchantedBar,
		inTempleDungeon, givenTalisman, givenBones, inMonkeyPen, talkedToGarkorWithGreeGree, talkedToGuard, talkedToKruk, onApeAtollNorthBridge,
		onApeAtollOverBridge, inThroneRoom, givenMonkey, gotSigil, inJungleDemonRoom, hasTalisman;

	DetailedQuestStep talkToNarnode, goUpF0ToF1, goUpF1ToF2, goUpF2ToF3, flyGandius, enterShipyard, talkToCaranock, talkToNarnodeAfterShipyard, goUpToDaero,
		clickPuzzle, enterValley, leavePrison, talkToGarkor, enterDentureBuilding, searchForDentures, goDownFromDentures, leaveToPrepareForAmulet, enterValleyForAmuletMake,
		searchForMould, leaveToPrepareForBar, goUpToDaeroForAmuletRun, enterDungeonForAmuletRun, talkToZooknock, useDentures, useMould, useBar, goUpToDaeroForAmuletMake,
		useBarOnFlame, leaveTempleDungeon, useWool, talkToMonkeyChild, talkToMonkeyChild2, talkToMonkeyChild3, giveChildBananas, talkToChildForTalisman,
		leaveToPrepareForTalismanRun, enterDungeonForTalismanRun, goUpToDaeroForTalismanRun, useTalisman, useBones, leaveDungeonWithGreeGree, enterGate, talkToMinder,
		talkToMonkeyAtZoo, talkToMinderAgain, talkToGuard, goUpToBridge, goDownFromBridge, talkToKruk, talkToAwow, talkToGarkorForSigil, prepareForBattle, killDemon,
		talkToNarnodeToFinish, goUpToDaeroForTalkingToAwow, talkToGarkorWithMonkey;

	ConditionalStep getAmuletParts, makeBar, makeAmulet, getTalisman, makeKaramjanGreeGree;

	NpcStep talkToDaero, talkToDaeroInHangar, talkToDaeroAfterPuzzle, talkToWaydarAfterPuzzle, talkToWaydarOnCrash, talkToDaeroTravel,
		talkToDaeroForAmuletRun, talkToWaydarForAmuletRun, talkToLumdoForAmuletRun, talkToLumdo, talkToLumdoToReturn, talkToDaeroForAmuletMake,
		talkToWaydarForAmuletMake, talkToLumdoForAmuletMake, talkToZooknockForTalisman, talkToLumdoForTalismanRun, talkToWaydarForTalismanRun,
		talkToDaeroForTalismanRun, talkToLumdoForTalkingToAwow, talkToWaydarForTalkingToAwow, talkToDaeroForTalkingToAwow;

	ObjectStep enterTemple;

	//Zones
	Zone stronghold, floor1, floor2, floor3, karamja, hangar, hangar2, crashIsland, apeAtollSouth1, apeAtollSouth2, apeAtollSouth3, prison, apeAtollNorth1,
		apeAtollNorth2, apeAtollNorth3, apeAtollNorth4, apeAtollNorthBridge, apeAtollOverBridge, dentureBuilding, mouldRoom, zooknockDungeon, templeDungeon,
		monkeyPen1, monkeyPen2, monkeyPen3, throne1, throne2, throne3, throne4, jungleDemonRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToNarnode);

		ConditionalStep gettingToApeAtoll = new ConditionalStep(this, talkToCaranock);
		gettingToApeAtoll.addStep(new Conditions(onCrashIsland, talkedToLumdo), talkToWaydarOnCrash);
		gettingToApeAtoll.addStep(new Conditions(onCrashIsland), talkToLumdo);
		gettingToApeAtoll.addStep(new Conditions(inHangar, talkedToDaeroAfterPuzzle), talkToWaydarAfterPuzzle);
		gettingToApeAtoll.addStep(new Conditions(inHangar, solvedPuzzle), talkToDaeroAfterPuzzle);
		gettingToApeAtoll.addStep(new Conditions(inHangar, startedPuzzle), clickPuzzle);
		gettingToApeAtoll.addStep(new Conditions(startedPuzzle, inFloor1), talkToDaeroTravel);
		gettingToApeAtoll.addStep(inHangar, talkToDaeroInHangar);
		gettingToApeAtoll.addStep(new Conditions(reportedBackToNarnode, inFloor1), talkToDaero);
		gettingToApeAtoll.addStep(reportedBackToNarnode, goUpToDaero);
		gettingToApeAtoll.addStep(talkedToCaranock, talkToNarnodeAfterShipyard);
		gettingToApeAtoll.addStep(inFloor3, flyGandius);
		gettingToApeAtoll.addStep(inFloor2, goUpF2ToF3);
		gettingToApeAtoll.addStep(inFloor1, goUpF1ToF2);
		gettingToApeAtoll.addStep(inStronghold, goUpF0ToF1);

		steps.put(1, gettingToApeAtoll);
		steps.put(2, gettingToApeAtoll);

		ConditionalStep gettingToGarkor = new ConditionalStep(this, goUpToDaero);
		gettingToGarkor.addStep(new Conditions(talkedToGarkor, inMouldRoom, monkeyDentures), searchForMould);
		gettingToGarkor.addStep(new Conditions(talkedToGarkor, inDentureBuilding, monkeyDentures), goDownFromDentures);
		gettingToGarkor.addStep(new Conditions(talkedToGarkor, inDentureBuilding), searchForDentures);
		gettingToGarkor.addStep(inPrison, leavePrison);
		gettingToGarkor.addStep(new Conditions(onApeAtollNorth, talkedToGarkor), enterDentureBuilding);
		gettingToGarkor.addStep(onApeAtollNorth, talkToGarkor);
		gettingToGarkor.addStep(onApeAtollSouth, enterValley);
		gettingToGarkor.addStep(onCrashIsland, talkToLumdoToReturn);
		gettingToGarkor.addStep(inHangar, talkToWaydarAfterPuzzle);
		gettingToGarkor.addStep(inFloor1, talkToDaeroTravel);

		getAmuletParts = new ConditionalStep(this, gettingToGarkor);
		getAmuletParts.setLockingCondition(hadDenturesAndMould);

		makeBar = new ConditionalStep(this, goUpToDaeroForAmuletRun);
		makeBar.addStep(new Conditions(inZooknockDungeon, givenDentures, givenMould, givenBar), talkToZooknock);
		makeBar.addStep(new Conditions(inZooknockDungeon, givenDentures, givenMould), useBar);
		makeBar.addStep(new Conditions(inZooknockDungeon, givenDentures), useMould);
		makeBar.addStep(new Conditions(inZooknockDungeon, talkedToZooknock), useDentures);
		makeBar.addStep(inZooknockDungeon, talkToZooknock);
		makeBar.addStep(onApeAtollSouth, enterDungeonForAmuletRun);
		makeBar.addStep(onCrashIsland, talkToLumdoForAmuletRun);
		makeBar.addStep(inHangar, talkToWaydarForAmuletRun);
		makeBar.addStep(inFloor1, talkToDaeroForAmuletRun);
		makeBar.addStep(inMouldRoom, leaveToPrepareForBar);
		makeBar.addStep(onApeAtollNorth, leaveToPrepareForBar);
		makeBar.setLockingCondition(hadEnchantedBar);

		makeAmulet = new ConditionalStep(this, goUpToDaeroForAmuletMake);
		makeAmulet.addStep(unstrungAmuletHighlight, useWool);
		makeAmulet.addStep(inTempleDungeon, useBarOnFlame);
		makeAmulet.addStep(onApeAtollNorth, enterTemple);
		makeAmulet.addStep(onApeAtollSouth, enterValleyForAmuletMake);
		makeAmulet.addStep(onCrashIsland, talkToLumdoForAmuletMake);
		makeAmulet.addStep(inHangar, talkToWaydarForAmuletMake);
		makeAmulet.addStep(inFloor1, talkToDaeroForAmuletMake);
		makeAmulet.addStep(inZooknockDungeon, leaveToPrepareForAmulet);
		makeAmulet.setLockingCondition(amulet);

		getTalisman = new ConditionalStep(this, leaveTempleDungeon);
		getTalisman.addStep(onApeAtollNorth, talkToMonkeyChild);
		getTalisman.setLockingCondition(hasTalisman);

		makeKaramjanGreeGree = new ConditionalStep(this, goUpToDaeroForTalismanRun);
		makeKaramjanGreeGree.addStep(new Conditions(inZooknockDungeon, givenTalisman, givenBones), talkToZooknockForTalisman);
		makeKaramjanGreeGree.addStep(new Conditions(inZooknockDungeon, givenTalisman), useBones);
		makeKaramjanGreeGree.addStep(new Conditions(inZooknockDungeon), useTalisman);
		makeKaramjanGreeGree.addStep(onApeAtollSouth, enterDungeonForTalismanRun);
		makeKaramjanGreeGree.addStep(onCrashIsland, talkToLumdoForTalismanRun);
		makeKaramjanGreeGree.addStep(inHangar, talkToWaydarForTalismanRun);
		makeKaramjanGreeGree.addStep(inFloor1, talkToDaeroForTalismanRun);
		makeKaramjanGreeGree.addStep(inTempleDungeon, leaveToPrepareForTalismanRun);
		makeKaramjanGreeGree.addStep(inMouldRoom, leaveToPrepareForTalismanRun);
		makeKaramjanGreeGree.addStep(onApeAtollNorth, leaveToPrepareForTalismanRun);
		makeKaramjanGreeGree.setLockingCondition(karamjanGreegree.alsoCheckBank(questBank));

		ConditionalStep infiltratingTheMonkeys = new ConditionalStep(this, getAmuletParts);
		infiltratingTheMonkeys.addStep(new Conditions(talkedToGarkor, hasTalisman), makeKaramjanGreeGree);
		infiltratingTheMonkeys.addStep(new Conditions(talkedToGarkor, amulet.alsoCheckBank(questBank)), getTalisman);
		infiltratingTheMonkeys.addStep(new Conditions(talkedToGarkor, hadEnchantedBar), makeAmulet);
		infiltratingTheMonkeys.addStep(new Conditions(talkedToGarkor, hadDenturesAndMould), makeBar);

		steps.put(3, infiltratingTheMonkeys);

		// 127 5->6 for step 4 start

		ConditionalStep bringMonkey = new ConditionalStep(this, goUpToDaeroForTalkingToAwow);
		bringMonkey.addStep(new Conditions(inThroneRoom), talkToAwow);
		bringMonkey.addStep(new Conditions(onApeAtollNorth, talkedToKruk), talkToAwow);
		bringMonkey.addStep(new Conditions(onApeAtollOverBridge, talkedToGuard), talkToKruk);
		bringMonkey.addStep(new Conditions(onApeAtollNorthBridge, talkedToGuard), goDownFromBridge);
		bringMonkey.addStep(new Conditions(onApeAtollNorth, talkedToGuard), goUpToBridge);
		bringMonkey.addStep(new Conditions(onApeAtollNorth, talkedToGarkorWithGreeGree), talkToGuard);
		bringMonkey.addStep(onApeAtollNorth, talkToGarkorWithMonkey);
		bringMonkey.addStep(onApeAtollSouth, enterGate);
		bringMonkey.addStep(onCrashIsland, talkToLumdoForTalkingToAwow);
		bringMonkey.addStep(inHangar, talkToWaydarForTalkingToAwow);
		bringMonkey.addStep(inFloor1, talkToDaeroForTalkingToAwow);
		bringMonkey.addStep(inMonkeyPen, talkToMinderAgain);

		ConditionalStep bringingAMonkeyToAwow = new ConditionalStep(this, talkToMinder);
		bringingAMonkeyToAwow.addStep(givenMonkey, talkToGarkorForSigil);
		bringingAMonkeyToAwow.addStep(new Conditions(true, monkey), bringMonkey);
		bringingAMonkeyToAwow.addStep(inMonkeyPen, talkToMonkeyAtZoo);
		bringingAMonkeyToAwow.addStep(inZooknockDungeon, leaveDungeonWithGreeGree);

		steps.put(4, bringingAMonkeyToAwow);

		ConditionalStep getSigil = new ConditionalStep(this, talkToGarkorForSigil);
		getSigil.addStep(inJungleDemonRoom, killDemon);
		getSigil.addStep(gotSigil, prepareForBattle);

		steps.put(5, getSigil);

		steps.put(6, talkToNarnodeToFinish);
		steps.put(7, talkToNarnodeToFinish);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		royalSeal = new ItemRequirement("Gnome royal seal", ItemID.GNOME_ROYAL_SEAL);
		royalSeal.setTooltip("You can get another from King Narnode");

		goldBar = new ItemRequirement("Gold bar", ItemID.GOLD_BAR);

		barHighlight = new ItemRequirement("Gold bar", ItemID.GOLD_BAR);
		barHighlight.setHighlightInInventory(true);

		ballOfWool = new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL);
		ballOfWoolHighlight = new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL);
		ballOfWoolHighlight.setHighlightInInventory(true);

		bananaReq = new ItemRequirement("Banana", ItemID.BANANA, 5);
		bananaReq.canBeObtainedDuringQuest();

		monkeyBonesOrCorpse = new ItemRequirement("Monkey bones or corpse", ItemID.MONKEY_BONES);
		monkeyBonesOrCorpse.addAlternates(ItemID.MONKEY_CORPSE);
		monkeyBonesOrCorpse.setTooltip("Obtainable during quest");

		monkeyBonesOrCorpseHighlight = new ItemRequirement("Monkey bones or corpse", ItemID.MONKEY_BONES);
		monkeyBonesOrCorpseHighlight.setHighlightInInventory(true);
		monkeyBonesOrCorpseHighlight.addAlternates(ItemID.MONKEY_CORPSE);

		narnodesOrders = new ItemRequirement("Narnode's orders", ItemID.NARNODES_ORDERS);
		narnodesOrders.setTooltip("You can get another from King Narnode");

		monkeyDentures = new ItemRequirement("Monkey dentures", ItemID.MONKEY_DENTURES);
		monkeyDenturesHighlight = new ItemRequirement("Monkey dentures", ItemID.MONKEY_DENTURES);
		monkeyDenturesHighlight.setHighlightInInventory(true);
		mould = new ItemRequirement("M'amulet mould", ItemID.MAMULET_MOULD);

		mouldHighlight = new ItemRequirement("M'amulet mould", ItemID.MAMULET_MOULD);
		mouldHighlight.setHighlightInInventory(true);

		enchantedBar = new ItemRequirement("Enchanted bar", ItemID.ENCHANTED_BAR);
		enchantedBar.setTooltip("If you've lost this you'll need to make another by bringing Zooknock some monkey dentures, an m'amulet mould and a gold bar");

		enchantedBarHighlight = new ItemRequirement("Enchanted bar", ItemID.ENCHANTED_BAR);
		enchantedBarHighlight.setHighlightInInventory(true);
		enchantedBarHighlight.setTooltip("If you've lost this you'll need to make another by bringing Zooknock some monkey dentures, an m'amulet mould and a gold bar");

		unstrungAmuletHighlight = new ItemRequirement("M'speak amulet", ItemID.MSPEAK_AMULET_4022);
		unstrungAmuletHighlight.setHighlightInInventory(true);

		amulet = new ItemRequirement("M'speak amulet", ItemID.MSPEAK_AMULET);
		amuletWorn = new ItemRequirement("M'speak amulet", ItemID.MSPEAK_AMULET, 1, true);

		banana5 = new ItemRequirement("Banana", ItemID.BANANA, 5);
		banana5.setTooltip("You can pick some from the trees near the monkey child");

		talisman = new ItemRequirement("Monkey talisman", ItemID.MONKEY_TALISMAN);
		talisman.setTooltip("You can get another from the monkey child");
		talismanHighlight = new ItemRequirement("Monkey talisman", ItemID.MONKEY_TALISMAN);
		talismanHighlight.setHighlightInInventory(true);
		talismanHighlight.setTooltip("You can get another from the monkey child");

		karamjanGreegree = new ItemRequirement("Karamjan monkey greegree", ItemID.KARAMJAN_MONKEY_GREEGREE);
		karamjanGreegreeEquipped = new ItemRequirement("Karamjan monkey greegree", ItemID.KARAMJAN_MONKEY_GREEGREE, 1, true);

		monkey = new ItemRequirement("Monkey", ItemID.MONKEY);

		sigilEquipped = new ItemRequirement("10th squad sigil", ItemID._10TH_SQUAD_SIGIL, 1, true);
		sigilEquipped.setTooltip("You can get another from Waymottin next to Zooknock");

		combatGear = new ItemRequirement("Combat gear, food and potions", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		antipoison = new ItemRequirement("Antipoison", ItemCollections.ANTIPOISONS);
	}

	public void loadZones()
	{
		karamja = new Zone(new WorldPoint(2688, 2881, 0), new WorldPoint(3008, 3252, 2));
		floor1 = new Zone(new WorldPoint(2437, 3474, 1), new WorldPoint(2493, 3511, 1));
		floor2 = new Zone(new WorldPoint(2437, 3474, 2), new WorldPoint(2493, 3511, 2));
		floor3 = new Zone(new WorldPoint(2437, 3474, 3), new WorldPoint(2493, 3511, 3));

		stronghold = new Zone(new WorldPoint(2346, 3336, 0), new WorldPoint(2511, 3528, 3));

		hangar = new Zone(new WorldPoint(2380, 9869, 0), new WorldPoint(2406, 9913, 0));
		hangar2 = new Zone(new WorldPoint(2635, 4492, 0), new WorldPoint(2662, 4533, 0));

		crashIsland = new Zone(new WorldPoint(2883, 2693, 0), new WorldPoint(2941, 2747, 0));

		apeAtollSouth1 = new Zone(new WorldPoint(2687, 2687, 0), new WorldPoint(2820, 2737, 0));
		apeAtollSouth2 = new Zone(new WorldPoint(2713, 2738, 0), new WorldPoint(2737, 2743, 0));
		apeAtollSouth3 = new Zone(new WorldPoint(2718, 2744, 0), new WorldPoint(2726, 2765, 0));

		prison = new Zone(new WorldPoint(2764, 2793, 0), new WorldPoint(2776, 2802, 0));

		apeAtollNorth1 = new Zone(new WorldPoint(2682, 2766, 0), new WorldPoint(2816, 2817, 3));
		apeAtollNorth2 = new Zone(new WorldPoint(2687, 2738, 0), new WorldPoint(2712, 2765, 3));
		apeAtollNorth3 = new Zone(new WorldPoint(2713, 2744, 0), new WorldPoint(2716, 2765, 3));

		apeAtollNorth4 = new Zone(new WorldPoint(2735, 2730, 0), new WorldPoint(2815, 2765, 3));
		apeAtollNorthBridge = new Zone(new WorldPoint(2712, 2765, 2), new WorldPoint(2730, 2767, 2));
		apeAtollOverBridge = new Zone(new WorldPoint(2726, 2751, 0), new WorldPoint(2733, 2769, 0));
		dentureBuilding = new Zone(new WorldPoint(2759, 2764, 0), new WorldPoint(2770, 2772, 0));
		mouldRoom = new Zone(new WorldPoint(2752, 9156, 0), new WorldPoint(2806, 9183, 0));
		zooknockDungeon = new Zone(new WorldPoint(2690, 9088, 0), new WorldPoint(2813, 9149, 0));

		templeDungeon = new Zone(new WorldPoint(2777, 9185, 0), new WorldPoint(2818, 9219, 0));
		monkeyPen1 = new Zone(new WorldPoint(2598, 3274, 0), new WorldPoint(2600, 3278, 0));
		monkeyPen2 = new Zone(new WorldPoint(2605, 3277, 0), new WorldPoint(2606, 3281, 0));
		monkeyPen3 = new Zone(new WorldPoint(2600, 3276, 0), new WorldPoint(2604, 3282, 0));

		throne1 = new Zone(new WorldPoint(2796, 2763, 0), new WorldPoint(2798, 2765, 0));
		throne2 = new Zone(new WorldPoint(2798, 2762, 0), new WorldPoint(2799, 2763, 0));
		throne3 = new Zone(new WorldPoint(2800, 2759, 0), new WorldPoint(2804, 2766, 0));
		throne4 = new Zone(new WorldPoint(2805, 2764, 0), new WorldPoint(2805, 2766, 0));

		jungleDemonRoom = new Zone(new WorldPoint(2671, 9151, 0), new WorldPoint(2749, 9214, 1));
	}

	public void setupConditions()
	{
		inKaramja = new ZoneRequirement(karamja);
		inFloor1 = new ZoneRequirement(floor1);
		inFloor2 = new ZoneRequirement(floor2);
		inFloor3 = new ZoneRequirement(floor3);
		inStronghold = new ZoneRequirement(stronghold);
		inHangar = new ZoneRequirement(hangar, hangar2);
		onCrashIsland = new ZoneRequirement(crashIsland);
		inPrison = new ZoneRequirement(prison);
		onApeAtollNorth = new ZoneRequirement(apeAtollNorth1, apeAtollNorth2, apeAtollNorth3, apeAtollNorth4, apeAtollNorthBridge, apeAtollOverBridge);
		inDentureBuilding = new ZoneRequirement(dentureBuilding);
		inMouldRoom = new ZoneRequirement(mouldRoom);
		inZooknockDungeon = new ZoneRequirement(zooknockDungeon);
		inTempleDungeon = new ZoneRequirement(templeDungeon);
		inMonkeyPen = new ZoneRequirement(monkeyPen1, monkeyPen2, monkeyPen3);
		onApeAtollNorthBridge = new ZoneRequirement(apeAtollNorthBridge);
		onApeAtollOverBridge = new ZoneRequirement(apeAtollOverBridge);
		inThroneRoom = new ZoneRequirement(throne1, throne2, throne3, throne4);
		inJungleDemonRoom = new ZoneRequirement(jungleDemonRoom);

		talkedToCaranock = new VarbitRequirement(122, 3);

		reportedBackToNarnode = new VarbitRequirement(121, 7);

		startedPuzzle = new VarbitRequirement(123, 5, Operation.GREATER_EQUAL);

		solvedPuzzle = new VarbitRequirement(123, 6, Operation.GREATER_EQUAL);
		talkedToDaeroAfterPuzzle = new VarbitRequirement(123, 7, Operation.GREATER_EQUAL);

		talkedToLumdo = new VarbitRequirement(125, 2, Operation.GREATER_EQUAL);
		talkedToWaydar = new VarbitRequirement(125, 3, Operation.GREATER_EQUAL);

		// 128 0->1 talked to Karam

		onApeAtollSouth = new ZoneRequirement(apeAtollSouth1, apeAtollSouth2, apeAtollSouth3);

		talkedToGarkor = new VarbitRequirement(126, 2, Operation.GREATER_EQUAL);

		talkedToZooknock = new VarbitRequirement(127, 5, Operation.GREATER_EQUAL);

		givenDentures = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(WidgetInfo.DIALOG_SPRITE_TEXT, "You hand Zooknock the magical monkey dentures."),
			new WidgetTextRequirement(119, 3, true, "<str> - Something to do with monkey speech."));
		givenBar = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(WidgetInfo.DIALOG_SPRITE_TEXT, "You hand Zooknock the gold bar."),
			new WidgetTextRequirement(119, 3, true, "<str> - A gold bar."));
		givenMould = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(WidgetInfo.DIALOG_SPRITE_TEXT, "You hand Zooknock the monkey amulet mould."),
			new WidgetTextRequirement(119, 3, true, "<str> - A monkey amulet mould."));

		hasTalisman = new Conditions(LogicType.OR, karamjanGreegree, talisman);

		hadEnchantedBar = new Conditions(LogicType.OR, talisman, unstrungAmuletHighlight, amulet, enchantedBar);
		hadDenturesAndMould = new Conditions(LogicType.OR, hadEnchantedBar, new Conditions(monkeyDentures, mould));

		givenTalisman = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(WidgetInfo.DIALOG_SPRITE_TEXT, "You hand Zooknock the monkey talisman."),
			new WidgetTextRequirement(119, 3, true, "<str> - An authentic magical monkey talisman.")
		);
		givenBones = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(WidgetInfo.DIALOG_SPRITE_TEXT, "You hand Zooknock the monkey remains."),
			new WidgetTextRequirement(119, 3, true, "<str> - Some kind of monkey remains.")
		);

		talkedToGarkorWithGreeGree = new VarbitRequirement(126, 3, Operation.GREATER_EQUAL);
		talkedToGuard = new Conditions(true, new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "He goes by the name of Kruk."));
		talkedToKruk = new Conditions(true,
			new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "As you wish.", "I see. Very well, you look genuine enough. Follow me.")
		);

		givenMonkey = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "We are still pondering your proposition", "You have shown yourself to be very resourceful."),
			new WidgetTextRequirement(119, 3, true, "appear to have earnt Awowogei's favour.")
		);

		gotSigil = new VarbitRequirement(126, 6, Operation.GREATER_EQUAL);
	}

	public void setupSteps()
	{
		talkToNarnode = new NpcStep(this, NpcID.KING_NARNODE_SHAREEN, new WorldPoint(2465, 3496, 0), "Talk to King Narnode Shareen in the Tree Gnome Stronghold.");
		talkToNarnode.addDialogStep("Yes");
		enterShipyard = new ObjectStep(this, ObjectID.GATE_2438, new WorldPoint(2945, 3041, 0), "Enter the shipyard on Karamja.", royalSeal);

		goUpF0ToF1 = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2466, 3495, 0), "Travel to the Shipyard on Karamja.", royalSeal);
		goUpF1ToF2 = new ObjectStep(this, ObjectID.LADDER_16684, new WorldPoint(2466, 3495, 1), "Travel to the Shipyard on Karamja.", royalSeal);
		goUpF2ToF3 = new ObjectStep(this, ObjectID.LADDER_2884, new WorldPoint(2466, 3495, 2), "Travel to the Shipyard on Karamja.", royalSeal);

		flyGandius = new NpcStep(this, NpcID.CAPTAIN_ERRDO_10467, new WorldPoint(2464, 3501, 3), "Fly with Captain Errdo to Gandius.");
		flyGandius.addSubSteps(goUpF0ToF1, goUpF1ToF2, goUpF2ToF3);

		talkToCaranock = new NpcStep(this, NpcID.GLO_CARANOCK, new WorldPoint(2955, 3025, 0), "Talk to G.L.O. Caranock in the shipyard.");

		talkToNarnodeAfterShipyard = new NpcStep(this, NpcID.KING_NARNODE_SHAREEN, new WorldPoint(2465, 3496, 0), "Return to King Narnode Shareen in the Tree Gnome Stronghold.");

		goUpToDaero = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2466, 3495, 0), "Talk to Daero on the 1st floor of the Tree Gnome Stronghold.");
		talkToDaero = new NpcStep(this, NpcID.DAERO, new WorldPoint(2482, 3486, 1), "Talk to Daero on the 1st floor of the Tree Gnome Stronghold. Make sure to go through all of the Chat Options to be teleported to a new location.", narnodesOrders, royalSeal);
		talkToDaero.addDialogSteps("Leave...", "Who is it?", "Yes");
		talkToDaero.addAlternateNpcs(NpcID.DAERO_1445);
		talkToDaero.addSubSteps(goUpToDaero);

		talkToDaeroInHangar = new NpcStep(this, NpcID.DAERO, new WorldPoint(2392, 9889, 0), "Talk to Daero in the hangar.");
		talkToDaeroInHangar.addAlternateNpcs(NpcID.DAERO_1445);

		clickPuzzle = new ObjectStep(this, ObjectID.REINITIALISATION_PANEL, new WorldPoint(2394, 9883, 0), "Operate the reinitialisation panel and solve the puzzle.");

		talkToDaeroAfterPuzzle = new NpcStep(this, NpcID.DAERO, new WorldPoint(2648, 4513, 0), "Talk to Daero in the hangar again.");
		talkToDaeroAfterPuzzle.addAlternateNpcs(NpcID.DAERO_1445);

		talkToWaydarAfterPuzzle = new NpcStep(this, NpcID.WAYDAR_6675, new WorldPoint(2648, 4518, 0), "Talk to Waydar.");
		talkToWaydarAfterPuzzle.addAlternateNpcs(NpcID.WAYDAR);
		talkToWaydarAfterPuzzle.addDialogStep("Yes");

		talkToLumdo = new NpcStep(this, NpcID.LUMDO_1453, new WorldPoint(2891, 2724, 0), "Talk to Lumdo.", royalSeal);
		talkToLumdo.addAlternateNpcs(NpcID.LUMDO, NpcID.LUMDO_1454);
		talkToWaydarOnCrash = new NpcStep(this, NpcID.WAYDAR_6675, new WorldPoint(2898, 2726, 0), "Talk to Waydar on Crash Island.");
		talkToWaydarOnCrash.addAlternateNpcs(NpcID.WAYDAR);
		talkToWaydarOnCrash.addDialogStep("I cannot convince Lumdo to take us to the island...");

		talkToLumdoToReturn = new NpcStep(this, NpcID.LUMDO_1453, new WorldPoint(2891, 2724, 0), "Talk to Lumdo again.");
		talkToLumdoToReturn.addAlternateNpcs(NpcID.LUMDO, NpcID.LUMDO_1454);

		talkToDaeroTravel = new NpcStep(this, NpcID.DAERO, new WorldPoint(2482, 3486, 1), "Talk to Daero on the 1st floor of the Tree Gnome Stronghold.");
		talkToDaeroTravel.addDialogSteps("Yes");
		talkToDaeroTravel.addAlternateNpcs(NpcID.DAERO_1445);

		enterValley = new DetailedQuestStep(this, new WorldPoint(2721, 2750, 0), "Head west and enter the valley going north WITH PROTECT FROM RANGED ON. Be wary of poison and taking damage.");
		enterValley.addSubSteps(talkToLumdoToReturn, talkToDaeroTravel);
		leavePrison = new DetailedQuestStep(this, new WorldPoint(2779, 2802, 0), "Wait for the gorilla guard to start" +
			" going away from the prison cell, then sneak out and go to the north side of the prison.");

		talkToGarkor = new NpcStep(this, NpcID.GARKOR_7158, new WorldPoint(2807, 2762, 0), "Stick to the east edge of the town, and make your way to Garkor to the south east.");
		talkToGarkor.setLinePoints(Arrays.asList(
			new WorldPoint(2762, 2806, 0),
			new WorldPoint(2784, 2806, 0),
			new WorldPoint(2784, 2770, 0),
			new WorldPoint(2807, 2770, 0),
			new WorldPoint(2807, 2762, 0)));

		enterDentureBuilding = new ObjectStep(this, ObjectID.DOORWAY_4710, new WorldPoint(2764, 2764, 0), "Head west and enter the large open building via the south door. DO NOT STAND ON THE LIGHT FLOOR IN THE BUILDING.");
		enterDentureBuilding.setLinePoints(Arrays.asList(
			new WorldPoint(2807, 2764, 0),
			new WorldPoint(2807, 2768, 0),
			new WorldPoint(2786, 2768, 0),
			new WorldPoint(2780, 2763, 0),
			new WorldPoint(2764, 2763, 0)
		));

		searchForDentures = new ObjectStep(this, ObjectID.CRATE_4715, new WorldPoint(2767, 2769, 0), "DO NOT WALK ON THE LIGHT FLOOR. Search the stacked crates for monkey dentures.");
		searchForDentures.addDialogStep("Yes");

		goDownFromDentures = new ObjectStep(this, ObjectID.CRATE_4714, new WorldPoint(2769, 2765, 0), "Search the crate in the south east corner and go down into the cavern.");
		goDownFromDentures.addDialogStep("Yes, I'm sure.");

		searchForMould = new ObjectStep(this, ObjectID.CRATE_4724, new WorldPoint(2782, 9172, 0), "Search the crate in the north west of the room for a M'amulet mould.");
		searchForMould.addDialogStep("Yes");

		leaveToPrepareForBar = new DetailedQuestStep(this, "Teleport out to prepare for a dangerous portion. You'll want energy/stamina potions, food and prayer potions.");

		goUpToDaeroForAmuletRun = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2466, 3495, 0), "Get food, antipoison, energy / stamina / prayer potions, and return to Ape Atoll.", goldBar, monkeyDentures,
			mould);

		talkToDaeroForAmuletRun = new NpcStep(this, NpcID.DAERO, new WorldPoint(2482, 3486, 1), "Travel with Daero on the 1st floor of the Tree Gnome Stronghold.", goldBar, monkeyDentures, mould);
		talkToDaeroForAmuletRun.addDialogSteps("Yes");
		talkToDaeroForAmuletRun.addAlternateNpcs(NpcID.DAERO_1445);

		talkToWaydarForAmuletRun = new NpcStep(this, NpcID.WAYDAR_6675, new WorldPoint(2648, 4518, 0), "Travel with Waydar.");
		talkToWaydarForAmuletRun.addAlternateNpcs(NpcID.WAYDAR);
		talkToWaydarForAmuletRun.addDialogStep("Yes");

		talkToLumdoForAmuletRun = new NpcStep(this, NpcID.LUMDO_1453, new WorldPoint(2891, 2724, 0), "Travel with Lumdo.");
		talkToLumdoForAmuletRun.addAlternateNpcs(NpcID.LUMDO, NpcID.LUMDO_1454);

		goUpToDaeroForAmuletRun.addSubSteps(talkToDaeroForAmuletRun, talkToWaydarForAmuletRun, talkToLumdoForAmuletRun);

		enterDungeonForAmuletRun = new ObjectStep(this, ObjectID.BAMBOO_LADDER_4780, new WorldPoint(2763, 2703, 0), "Enter the dungeon in south Ape Atoll.", goldBar, monkeyDentures, mould);

		List<WorldPoint> zooknockDungeonPath = Arrays.asList(
			new WorldPoint(2768, 9101, 0),
			new WorldPoint(2788, 9102, 0),
			new WorldPoint(2788, 9109, 0),
			new WorldPoint(2766, 9111, 0),
			new WorldPoint(2764, 9121, 0),
			new WorldPoint(2800, 9116, 0),
			new WorldPoint(2802, 9094, 0),
			new WorldPoint(2809, 9096, 0),
			new WorldPoint(2807, 9130, 0),
			new WorldPoint(2787, 9128, 0),
			new WorldPoint(2770, 9133, 0),
			new WorldPoint(2747, 9134, 0),
			new WorldPoint(2734, 9138, 0),
			new WorldPoint(2716, 9139, 0),
			new WorldPoint(2717, 9131, 0),
			new WorldPoint(2738, 9129, 0),
			new WorldPoint(2738, 9121, 0),
			new WorldPoint(2709, 9118, 0),
			new WorldPoint(2711, 9108, 0),
			new WorldPoint(2741, 9106, 0),
			new WorldPoint(2741, 9095, 0),
			new WorldPoint(2721, 9099, 0),
			new WorldPoint(2713, 9094, 0),
			new WorldPoint(2695, 9096, 0),
			new WorldPoint(2693, 9113, 0),
			new WorldPoint(2697, 9128, 0),
			new WorldPoint(2696, 9144, 0),
			new WorldPoint(2722, 9147, 0),
			new WorldPoint(2750, 9142, 0),
			new WorldPoint(2773, 9144, 0),
			new WorldPoint(2799, 9138, 0)
		);

		talkToZooknock = new NpcStep(this, NpcID.ZOOKNOCK_7170, new WorldPoint(2805, 9143, 0), "Talk to Zooknock in the north east of the dungeon.", goldBar, monkeyDentures, mould);
		talkToZooknock.addDialogSteps("What do we need for the monkey amulet?", "I'll be back later.");
		talkToZooknock.setLinePoints(zooknockDungeonPath);

		useDentures = new NpcStep(this, NpcID.ZOOKNOCK_7170, new WorldPoint(2805, 9143, 0), "Use the monkey dentures on Zooknock in the north east of the dungeon. If you've already done so, open the quest journal to re-sync your current state.", monkeyDenturesHighlight);
		useDentures.addIcon(ItemID.MONKEY_DENTURES);

		useMould = new NpcStep(this, NpcID.ZOOKNOCK_7170, new WorldPoint(2805, 9143, 0), "Use the m'amulet mould on Zooknock in the north east of the dungeon. If you've already done so, open the quest journal to re-sync your current state.", mouldHighlight);
		useMould.addIcon(ItemID.MAMULET_MOULD);

		useBar = new NpcStep(this, NpcID.ZOOKNOCK_7170, new WorldPoint(2805, 9143, 0), "Use the gold bar on Zooknock in the north east of the dungeon. If you've already done so, open the quest journal to re-sync your current state.", barHighlight);
		useBar.addIcon(ItemID.GOLD_BAR);

		leaveToPrepareForAmulet = new DetailedQuestStep(this, "Teleport out to prepare to make the amulet. You'll want some food, prayer potions, antipoisons, the mould and the enchanted bar.");

		goUpToDaeroForAmuletMake = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2466, 3495, 0), "Get food, antipoison, prayer potions, and return to Ape Atoll.", enchantedBar, mould, ballOfWool);

		talkToDaeroForAmuletMake = new NpcStep(this, NpcID.DAERO, new WorldPoint(2482, 3486, 1), "Travel with Daero on the 1st floor of the Tree Gnome Stronghold.", enchantedBar, mould, ballOfWool);
		talkToDaeroForAmuletMake.addDialogSteps("Yes");
		talkToDaeroForAmuletMake.addAlternateNpcs(NpcID.DAERO_1445);

		talkToWaydarForAmuletMake = new NpcStep(this, NpcID.WAYDAR_6675, new WorldPoint(2648, 4518, 0), "Travel with Waydar.");
		talkToWaydarForAmuletMake.addAlternateNpcs(NpcID.WAYDAR);
		talkToWaydarForAmuletMake.addDialogStep("Yes");

		talkToLumdoForAmuletMake = new NpcStep(this, NpcID.LUMDO_1453, new WorldPoint(2891, 2724, 0), "Travel with Lumdo.");
		talkToLumdoForAmuletMake.addAlternateNpcs(NpcID.LUMDO, NpcID.LUMDO_1454);

		goUpToDaeroForAmuletMake.addSubSteps(talkToDaeroForAmuletMake, talkToWaydarForAmuletMake, talkToLumdoForAmuletMake);

		enterValleyForAmuletMake = new DetailedQuestStep(this, new WorldPoint(2721, 2750, 0), "Head west and enter the valley going north WITH PROTECT FROM RANGED ON. Be wary of poison and taking damage.", enchantedBar, mould, ballOfWool);
		enterValleyForAmuletMake.addSubSteps(talkToLumdoToReturn, talkToDaeroTravel);

		enterTemple = new ObjectStep(this, ObjectID.TRAPDOOR_4879, new WorldPoint(2807, 2785, 0), "Wait for the " +
			"gorilla guard to start going away from the prison cell, then sneak out and go to the north side of the prison. Afterwards, head into the temple's basement.", enchantedBar, mould, ballOfWool);
		enterTemple.addAlternateObjects(ObjectID.TRAPDOOR_4880);
		enterTemple.setLinePoints(Arrays.asList(
			new WorldPoint(2764, 2806, 0),
			new WorldPoint(2784, 2806, 0),
			new WorldPoint(2784, 2787, 0),
			new WorldPoint(2806, 2785, 0)
		));

		useBarOnFlame = new ObjectStep(this, ObjectID.WALL_OF_FLAME_4766, new WorldPoint(2810, 9209, 0), "Use the enchanted bar on the wall of flame.", enchantedBarHighlight);
		useBarOnFlame.addIcon(ItemID.ENCHANTED_BAR);

		leaveTempleDungeon = new ObjectStep(this, ObjectID.CLIMBING_ROPE_4881, new WorldPoint(2808, 9201, 0), "Climb the rope out. If you plan on doing Recipe for Disaster, also kill a Zombie Monkey for its bones whilst here, and a gorilla upstairs.");

		useWool = new DetailedQuestStep(this, "Use the ball of wool on the unstrung amulet.", ballOfWoolHighlight, unstrungAmuletHighlight);

		talkToMonkeyChild = new NpcStep(this, NpcID.MONKEY_CHILD, new WorldPoint(2743, 2794, 0), "Go to the north west of the island and talk to the monkey child there a few times. Eventually you'll give him 5 bananas, and he'll give you a monkey talisman.", amuletWorn, banana5);
		talkToMonkeyChild.addText("Make sure to avoid the monkey's aunt near him otherwise she'll call the guards on you.");
		talkToMonkeyChild.addDialogSteps("Well I'll be a monkey's uncle!", "How many bananas did Aunty want?", "Ok, I promise!", "I've lost that toy you gave me...", "Wow - can I borrow it?");
		talkToMonkeyChild.setLinePoints(Arrays.asList(
			new WorldPoint(2806, 2785, 0),
			new WorldPoint(2784, 2787, 0),
			new WorldPoint(2784, 2806, 0),
			new WorldPoint(2764, 2806, 0),
			new WorldPoint(2749, 2804, 0),
			new WorldPoint(2749, 2802, 0),
			new WorldPoint(2746, 2802, 0),
			new WorldPoint(2746, 2797, 0)
		));

		talkToMonkeyChild2 = new NpcStep(this, NpcID.MONKEY_CHILD, new WorldPoint(2743, 2794, 0), "Talk to the monkey child again.", amuletWorn);
		talkToMonkeyChild3 = new NpcStep(this, NpcID.MONKEY_CHILD, new WorldPoint(2743, 2794, 0), "Talk to the monkey child again.", amuletWorn);

		giveChildBananas = new NpcStep(this, NpcID.MONKEY_CHILD, new WorldPoint(2743, 2794, 0), "Give the monkey child 5 bananas.", amuletWorn, banana5);
		talkToChildForTalisman = new NpcStep(this, NpcID.MONKEY_CHILD, new WorldPoint(2743, 2794, 0), "Wait for the aunt to make another round, then talk to the monkey child for the talisman.", amuletWorn);

		talkToMonkeyChild.addSubSteps(talkToMonkeyChild2, talkToMonkeyChild3, giveChildBananas, talkToChildForTalisman);

		leaveToPrepareForTalismanRun = new DetailedQuestStep(this, "Teleport out to prepare to make the talisman. You'll want some food, prayer potions, and antipoisons.");
		leaveToPrepareForTalismanRun.addText("If you want to make talismans for Recipe for Disaster, talk to the monkey child a few more times for 4 talismans, and make sure you have bones of a zombie / ninja / gorilla.");

		goUpToDaeroForTalismanRun = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2466, 3495, 0), "Get food, antipoison, prayer potions, and return to Ape Atoll.", talisman, monkeyBonesOrCorpse);

		talkToDaeroForTalismanRun = new NpcStep(this, NpcID.DAERO, new WorldPoint(2482, 3486, 1), "Travel with Daero on the 1st floor of the Tree Gnome Stronghold.", talisman, monkeyBonesOrCorpse);
		talkToDaeroForTalismanRun.addDialogSteps("Yes");
		talkToDaeroForTalismanRun.addAlternateNpcs(NpcID.DAERO_1445);

		talkToWaydarForTalismanRun = new NpcStep(this, NpcID.WAYDAR_6675, new WorldPoint(2648, 4518, 0), "Travel with Waydar.");
		talkToWaydarForTalismanRun.addAlternateNpcs(NpcID.WAYDAR);
		talkToWaydarForTalismanRun.addDialogStep("Yes");

		talkToLumdoForTalismanRun = new NpcStep(this, NpcID.LUMDO_1453, new WorldPoint(2891, 2724, 0), "Travel with Lumdo.");
		talkToLumdoForTalismanRun.addAlternateNpcs(NpcID.LUMDO, NpcID.LUMDO_1454);

		goUpToDaeroForTalismanRun.addSubSteps(talkToDaeroForTalismanRun, talkToWaydarForTalismanRun, talkToLumdoForTalismanRun);

		enterDungeonForTalismanRun = new ObjectStep(this, ObjectID.BAMBOO_LADDER_4780, new WorldPoint(2763, 2703, 0), "Enter the dungeon in south Ape Atoll.", talisman, monkeyBonesOrCorpse);

		useTalisman = new NpcStep(this, NpcID.ZOOKNOCK_7170, new WorldPoint(2805, 9143, 0), "Use the monkey talisman on Zooknock in the north east of the dungeon. If you've already done so, open the quest journal to re-sync your current state.", talismanHighlight);
		useTalisman.addIcon(ItemID.MONKEY_TALISMAN);
		useTalisman.setLinePoints(zooknockDungeonPath);

		useBones = new NpcStep(this, NpcID.ZOOKNOCK_7170, new WorldPoint(2805, 9143, 0), "Use the monkey bones on Zooknock in the north east of the dungeon. If you've already done so, open the quest journal to re-sync your current state.", monkeyBonesOrCorpseHighlight);
		useBones.addIcon(ItemID.MONKEY_BONES);

		talkToZooknockForTalisman = new NpcStep(this, NpcID.ZOOKNOCK_7170, new WorldPoint(2805, 9143, 0), "Talk to Zooknock in the north east of the dungeon for the talisman.");
		talkToZooknockForTalisman.addDialogStep("What do we need for the monkey talisman?");

		leaveDungeonWithGreeGree = new DetailedQuestStep(this, "If you want to make a zombie, ninja and gorilla greegree talk to and give Zooknock the bones/talismans. Once you're done, teleport away for the next step.");
		leaveDungeonWithGreeGree.addDialogSteps("Can you make another monkey talisman?", "Yes");

		talkToMinder = new NpcStep(this, NpcID.MONKEY_MINDER, new WorldPoint(2608, 3278, 0), "Talk to the Monkey " +
			"Minder in Ardougne Zoo whilst wielding the Karamjan monkey greegree.", karamjanGreegreeEquipped, amuletWorn);
		talkToMonkeyAtZoo = new NpcStep(this, NpcID.MONKEY_5279, new WorldPoint(2603, 3278, 0), "Talk to a monkey in the pen.", true);
		talkToMinderAgain = new NpcStep(this, NpcID.MONKEY_MINDER, new WorldPoint(2608, 3278, 0), "UNEQUIP the greegree, then talk to the Monkey Minder again to leave.");

		goUpToDaeroForTalkingToAwow = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2466, 3495, 0), "WALK/RUN to Daero to return to Ape Atoll. If you teleport, you'll have to start again.", karamjanGreegree, amuletWorn, monkey);

		talkToDaeroForTalkingToAwow = new NpcStep(this, NpcID.DAERO, new WorldPoint(2482, 3486, 1), "Travel with Daero on the 1st floor of the Tree Gnome Stronghold.", karamjanGreegree, amulet, monkey);
		talkToDaeroForTalkingToAwow.addDialogSteps("Yes");
		talkToDaeroForTalkingToAwow.addAlternateNpcs(NpcID.DAERO_1445);

		talkToWaydarForTalkingToAwow = new NpcStep(this, NpcID.WAYDAR_6675, new WorldPoint(2648, 4518, 0), "Travel with Waydar.", karamjanGreegree, amulet, monkey);
		talkToWaydarForTalkingToAwow.addAlternateNpcs(NpcID.WAYDAR);
		talkToWaydarForTalkingToAwow.addDialogStep("Yes");

		talkToLumdoForTalkingToAwow = new NpcStep(this, NpcID.LUMDO_1453, new WorldPoint(2891, 2724, 0), "Travel with Lumdo.", karamjanGreegree, amulet, monkey);
		talkToLumdoForTalkingToAwow.addAlternateNpcs(NpcID.LUMDO, NpcID.LUMDO_1454);

		goUpToDaeroForTalismanRun.addSubSteps(talkToDaeroForTalismanRun, talkToWaydarForTalismanRun, talkToLumdoForTalismanRun);

		talkToGuard = new NpcStep(this, NpcID.ELDER_GUARD_5278, new WorldPoint(2802, 2758, 0), "Talk to a guard outside the building in the south east of Marim. If you already have, open the quest guide to resync.");

		enterGate = new ObjectStep(this, ObjectID.BAMBOO_GATE_4788, new WorldPoint(2722, 2766, 0), "EQUIP THE GREEGREE and enter the gate to Marim. The monkeys will not attack you if you're holding it.", karamjanGreegreeEquipped, amuletWorn, monkey);

		goUpToBridge = new ObjectStep(this, ObjectID.BAMBOO_LADDER_4775, new WorldPoint(2713, 2766, 0), "Talk to Kruk in the north west of the island.", karamjanGreegreeEquipped, amuletWorn, monkey);
		goDownFromBridge = new ObjectStep(this, ObjectID.BAMBOO_LADDER_4776, new WorldPoint(2729, 2766, 2), "Talk to Kruk in the north west of the island.", karamjanGreegreeEquipped, amuletWorn, monkey);
		talkToKruk = new NpcStep(this, NpcID.KRUK, new WorldPoint(2729, 2764, 0), "Talk to Kruk in the north west of the island.", karamjanGreegreeEquipped, amuletWorn, monkey);
		talkToKruk.addSubSteps(goUpToBridge, goDownFromBridge);

		talkToGarkorWithMonkey = new NpcStep(this, NpcID.GARKOR_7158, new WorldPoint(2807, 2762, 0), "Talk to Garkor.", karamjanGreegreeEquipped, amuletWorn, monkey);
		talkToGarkorForSigil = new NpcStep(this, NpcID.GARKOR_7158, new WorldPoint(2807, 2762, 0), "Talk to Garkor.", karamjanGreegreeEquipped, amuletWorn);

		talkToAwow = new ObjectStep(this, ObjectID.AWOWOGEI, new WorldPoint(2803, 2765, 0), "Talk to Awowogei twice.", karamjanGreegreeEquipped, amuletWorn, monkey);

		prepareForBattle = new DetailedQuestStep(this, "Prepare to fight the Jungle Demon. Once you're ready, equip the 10th squad sigil.", sigilEquipped);
		killDemon = new DetailedQuestStep(this, "Kill the Jungle Demon.");

		talkToNarnodeToFinish = new NpcStep(this, NpcID.KING_NARNODE_SHAREEN, new WorldPoint(2465, 3496, 0), "Return to King Narnode Shareen in the Tree Gnome Stronghold.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(goldBar, ballOfWool, bananaReq, monkeyBonesOrCorpse);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(combatGear, antipoison);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Jungle Demon (level 195)");
		return reqs;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.THE_GRAND_TREE, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.TREE_GNOME_VILLAGE, QuestState.FINISHED));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(3);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("55,000 Experience Combat Lamp (Over multiple Skills)", ItemID.ANTIQUE_LAMP, 1), //4447 is placeholder for filter
				new ItemReward("10,000 Coins", ItemID.COINS_995, 10000),
				new ItemReward("3 Diamonds", ItemID.DIAMOND, 3));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Ability to purchase and wield the Dragon Scimitar."),
				new UnlockReward("Full access to Ape Atoll."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Collections.singletonList(talkToNarnode)));
		allSteps.add(new PanelDetails("Investigate the shipyard", Arrays.asList(flyGandius, talkToCaranock, talkToNarnodeAfterShipyard)));
		allSteps.add(new PanelDetails("Traveling to Ape Atoll", Arrays.asList(talkToDaero, talkToDaeroInHangar, clickPuzzle, talkToDaeroAfterPuzzle, talkToWaydarAfterPuzzle,
			talkToLumdo, talkToWaydarOnCrash)));
		allSteps.add(new PanelDetails("Finding Garkor", Arrays.asList(enterValley, leavePrison, talkToGarkor)));

		PanelDetails getAmuletItemsPanel = new PanelDetails("Getting amulet parts", Arrays.asList(enterDentureBuilding, searchForDentures, goDownFromDentures, searchForMould, leaveToPrepareForBar));
		getAmuletItemsPanel.setLockingStep(getAmuletParts);
		allSteps.add(getAmuletItemsPanel);

		PanelDetails makeBarPanel = new PanelDetails("Making an Enchanted Bar", Arrays.asList(goUpToDaeroForAmuletRun, enterDungeonForAmuletRun, talkToZooknock, useDentures, useMould, useBar), goldBar, monkeyDentures, mould);
		makeBarPanel.setLockingStep(makeBar);
		allSteps.add(makeBarPanel);

		PanelDetails makeAmuletPanel = new PanelDetails("Making an amulet", Arrays.asList(leaveToPrepareForAmulet, goUpToDaeroForAmuletMake, enterValleyForAmuletMake, enterTemple, useBarOnFlame), enchantedBar, mould, ballOfWool);
		makeAmuletPanel.setLockingStep(makeAmulet);
		allSteps.add(makeAmuletPanel);

		PanelDetails getTalismanPanel = new PanelDetails("Getting the talisman", Arrays.asList(leaveTempleDungeon, talkToMonkeyChild, talkToMonkeyChild2, talkToMonkeyChild3, giveChildBananas, talkToChildForTalisman), amulet, banana5);
		getTalismanPanel.setLockingStep(getTalisman);
		allSteps.add(getTalismanPanel);

		allSteps.add(new PanelDetails("Making a greegree", Arrays.asList(leaveToPrepareForTalismanRun, goUpToDaeroForTalismanRun, enterDungeonForTalismanRun, useTalisman, useBones), talisman, monkeyBonesOrCorpse));
		allSteps.add(new PanelDetails("Save a monkey", Arrays.asList(talkToMinder, talkToMonkeyAtZoo, talkToMinderAgain, goUpToDaeroForTalkingToAwow,
			talkToGarkorWithMonkey, talkToGuard, talkToKruk, talkToAwow, talkToGarkorForSigil), karamjanGreegree, amulet));
		allSteps.add(new PanelDetails("Defeat the demon", Arrays.asList(prepareForBattle, killDemon, talkToNarnodeToFinish), combatGear));

		return allSteps;
	}
}

