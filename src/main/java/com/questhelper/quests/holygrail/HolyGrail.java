/*
 * Copyright (c) 2020, Kijjuy
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
package com.questhelper.quests.holygrail;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.widget.WidgetTextRequirement;
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
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.widgets.WidgetInfo;

@QuestDescriptor(
	quest = QuestHelperQuest.HOLY_GRAIL
)
public class HolyGrail extends BasicQuestHelper
{
	//Items Recommended
	ItemRequirement antipoison, combatGear, food, threeCamelotTele, ardyTele, faladorTele, sixtyCoins;

	//Items Required
	ItemRequirement excalibur, holyTableNapkin, twoMagicWhistles, highlightMagicWhistle1, goldFeather, grailBell, highlightGrailBell, emptyInvSpot, oneMagicWhistle, highlightMagicWhistle2, grail;

	Requirement inCamelot, inCamelotUpstairs, inMerlinRoom, merlinNearby, onEntrana, inGalahadHouse, inDraynorFrontManor, inDraynorManorBottomFloor, inDraynorManorSecondFloor,
		inDraynorManorTopFloor, inMagicWhistleRoom, inTeleportLocation, inFisherKingRealmEntrance, titanNearby, inFisherKingRealmAfterTitan, talkedToFisherman,
		inGrailBellRingLocation, inFisherKingCastle1BottomFloor, inFisherKingCastle1SecondFloor, inFisherKingRealm, inFisherKingCastle2BottomFloor,
		inFisherKingCastle2SecondFloor, inFisherKingCastle2ThirdFloor;

	QuestStep talkToKingArthur1, talkToMerlin, goUpStairsCamelot, openMerlinDoor, goToEntrana, talkToHighPriest, goToGalahad, talkToGalahad, goToDraynorManor, enterDraynorManor, goUpStairsDraynor1,
		goUpStairsDraynor2, openWhistleDoor, takeWhistles, goGetExcalibur, goToTeleportLocation1, blowWhistle1, attackTitan, talkToFisherman, pickupBell, ringBell, goUpStairsBrokenCastle, talkToFisherKing, goToCamelot,
		talkToKingArthur2, openSack, goToTeleportLocation2, blowWhistle2, openFisherKingCastleDoor, goUpNewCastleStairs, goUpNewCastleLadder, takeGrail, talkToKingArthur3;

	ConditionalStep findFisherKing;

	//Zones
	Zone camelotGround, camelotUpstairsZone1, camelotUpstairsZone2, merlinRoom, entranaBoat, entranaIsland, galahadHouse, draynorManorFront, draynorManorBottomFloor, draynorManorSecondFloor,
		draynorManorTopFloor, magicWhistleRoom, teleportLocation, fisherKingRealmEntrance, fisherKingRealmAfterTitan1, fisherKingRealmAfterTitan2, fisherKingRealmAfterTitan3, grailBellRingLocation,
		fisherKingRealmCastle1BottomFloor, fisherKingRealmCastle1SecondFloor, fisherKingRealm, fisherKingRealmCastle2BottomFloor, fisherKingRealmCastle2SecondFloor, fisherKingRealmCastle2ThirdFloor;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToKingArthur1);

		ConditionalStep findingMerlin = new ConditionalStep(this, goUpStairsCamelot);
		findingMerlin.addStep(new Conditions(inMerlinRoom, merlinNearby), talkToMerlin);
		findingMerlin.addStep(inCamelotUpstairs, openMerlinDoor);

		steps.put(2, findingMerlin);

		ConditionalStep findHighPriest = new ConditionalStep(this, goToEntrana);
		findHighPriest.addStep(onEntrana, talkToHighPriest);

		steps.put(3, findHighPriest);

		findFisherKing = new ConditionalStep(this, goToGalahad);
		findFisherKing.addStep(inFisherKingCastle1SecondFloor, talkToFisherKing);
		findFisherKing.addStep(inFisherKingCastle1BottomFloor, goUpStairsBrokenCastle);
		findFisherKing.addStep(new Conditions(grailBell, inFisherKingRealmAfterTitan), ringBell);
		findFisherKing.addStep(talkedToFisherman, pickupBell);
		findFisherKing.addStep(inFisherKingRealmAfterTitan, talkToFisherman);
		findFisherKing.addStep(new Conditions(excalibur, titanNearby), attackTitan);
		findFisherKing.addStep(new Conditions(twoMagicWhistles, inTeleportLocation, excalibur), blowWhistle1);
		findFisherKing.addStep(new Conditions(twoMagicWhistles, excalibur), goToTeleportLocation1);
		findFisherKing.addStep(twoMagicWhistles, goGetExcalibur);
		findFisherKing.addStep(inMagicWhistleRoom, takeWhistles);
		findFisherKing.addStep(inDraynorManorTopFloor, openWhistleDoor);
		findFisherKing.addStep(inDraynorManorSecondFloor, goUpStairsDraynor2);
		findFisherKing.addStep(inDraynorManorBottomFloor, goUpStairsDraynor1);
		findFisherKing.addStep(inDraynorFrontManor, enterDraynorManor);
		findFisherKing.addStep(holyTableNapkin, goToDraynorManor);
		findFisherKing.addStep(inGalahadHouse, talkToGalahad);
		findFisherKing.setLockingCondition(twoMagicWhistles);

		steps.put(4, findFisherKing);
		steps.put(7, findFisherKing);

		ConditionalStep findPercival = new ConditionalStep(this, talkToKingArthur2);
		findPercival.addStep(goldFeather, openSack);

		steps.put(8, findPercival);

		ConditionalStep finishQuest = new ConditionalStep(this, goToTeleportLocation2);
		finishQuest.addStep(grail, talkToKingArthur3);
		finishQuest.addStep(inFisherKingCastle2ThirdFloor, takeGrail);
		finishQuest.addStep(inFisherKingCastle2SecondFloor, goUpNewCastleLadder);
		finishQuest.addStep(inFisherKingCastle2BottomFloor, goUpNewCastleStairs);
		finishQuest.addStep(inFisherKingRealm, openFisherKingCastleDoor);
		finishQuest.addStep(inTeleportLocation, blowWhistle2);

		steps.put(9, finishQuest);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		excalibur = new ItemRequirement("Excalibur", ItemID.EXCALIBUR).isNotConsumed();
		holyTableNapkin = new ItemRequirement("Holy Table Napkin", ItemID.HOLY_TABLE_NAPKIN);
		twoMagicWhistles = new ItemRequirement("Magic Whistles", ItemID.MAGIC_WHISTLE, 2);
		threeCamelotTele = new ItemRequirement("Camelot Teleports", ItemID.CAMELOT_TELEPORT, 3);
		ardyTele = new ItemRequirement("Ardougne Teleport", ItemID.ARDOUGNE_TELEPORT);
		faladorTele = new ItemRequirement("Falador Teleport", ItemID.FALADOR_TELEPORT);
		sixtyCoins = new ItemRequirement("Coins", ItemCollections.COINS, 60);
		antipoison = new ItemRequirement("Antipoison", ItemID.ANTIPOISON4);
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		combatGear = new ItemRequirement("A weapon and armour", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		emptyInvSpot = new ItemRequirement("Empty Inventory Spot", -1, 1);
		goldFeather = new ItemRequirement("Magic gold feather", ItemID.MAGIC_GOLD_FEATHER);
		grailBell = new ItemRequirement("Grail Bell", ItemID.GRAIL_BELL);
		oneMagicWhistle = new ItemRequirement("Magic Whistle", ItemID.MAGIC_WHISTLE);
		grail = new ItemRequirement("Holy Grail", ItemID.HOLY_GRAIL);

		highlightMagicWhistle1 = new ItemRequirement("Magic Whistle", ItemID.MAGIC_WHISTLE, 2);
		highlightMagicWhistle1.setHighlightInInventory(true);

		highlightMagicWhistle2 = new ItemRequirement("Magic Whistle", ItemID.MAGIC_WHISTLE);
		highlightMagicWhistle2.setHighlightInInventory(true);

		highlightGrailBell = new ItemRequirement("Grail Bell", ItemID.GRAIL_BELL);
		highlightGrailBell.setHighlightInInventory(true);
	}

	public void loadZones()
	{
		camelotGround = new Zone(new WorldPoint(2744, 3517, 0), new WorldPoint(2733, 3483, 0));
		camelotUpstairsZone1 = new Zone(new WorldPoint(2768, 3517, 1), new WorldPoint(2757, 3506, 1));
		camelotUpstairsZone2 = new Zone(new WorldPoint(2764, 3517, 1), new WorldPoint(2748, 3496, 1));
		merlinRoom = new Zone(new WorldPoint(2768, 3505, 1), new WorldPoint(2765, 3496, 1));
		entranaBoat = new Zone(new WorldPoint(2841, 3332, 0), new WorldPoint(2823, 3328, 2));
		entranaIsland = new Zone(new WorldPoint(2871, 3393, 0), new WorldPoint(2800, 3329, 2));
		galahadHouse = new Zone(new WorldPoint(2616, 3480, 0), new WorldPoint(2609, 3473, 0));
		draynorManorFront = new Zone(new WorldPoint(3116, 3353, 0), new WorldPoint(3100, 3347, 0));
		draynorManorBottomFloor = new Zone(new WorldPoint(3119, 3373, 0), new WorldPoint(3097, 3354, 0));
		draynorManorSecondFloor = new Zone(new WorldPoint(3118, 3373, 1), new WorldPoint(3098, 3354, 1));
		draynorManorTopFloor = new Zone(new WorldPoint(3112, 3370, 2), new WorldPoint(3104, 3362, 2));
		magicWhistleRoom = new Zone(new WorldPoint(3112, 3361, 2), new WorldPoint(3104, 3357, 2));
		teleportLocation = new Zone(new WorldPoint(2743, 3237, 0), new WorldPoint(2740, 3234, 0));
		fisherKingRealmAfterTitan1 = new Zone(new WorldPoint(2791, 4734, 0), new WorldPoint(2752, 4671, 0));
		fisherKingRealmAfterTitan2 = new Zone(new WorldPoint(2808, 4707, 0), new WorldPoint(2791, 4688, 0));
		fisherKingRealmAfterTitan3 = new Zone(new WorldPoint(2798, 4710, 0), new WorldPoint(2781, 4707, 0));
		grailBellRingLocation = new Zone(new WorldPoint(2762, 4694, 0), new WorldPoint(2761, 4694, 0));
		fisherKingRealmCastle1BottomFloor = new Zone(new WorldPoint(2780, 4692, 0), new WorldPoint(2756, 4675, 0));
		fisherKingRealmCastle1SecondFloor = new Zone(new WorldPoint(2771, 4692, 1), new WorldPoint(2756, 4675, 1));
		fisherKingRealm = new Zone(new WorldPoint(2683, 4733, 0), new WorldPoint(2625, 4672, 0));
		fisherKingRealmCastle2BottomFloor = new Zone(new WorldPoint(2652, 4692, 0), new WorldPoint(2628, 4675, 0));
		fisherKingRealmCastle2SecondFloor = new Zone(new WorldPoint(2652, 4687, 1), new WorldPoint(2646, 4681, 1));
		fisherKingRealmCastle2ThirdFloor = new Zone(new WorldPoint(2651, 4686, 2), new WorldPoint(2647, 4682, 2));
	}

	public void setupConditions()
	{
		inCamelot = new ZoneRequirement(camelotGround);
		inCamelotUpstairs = new Conditions(LogicType.OR,
			new ZoneRequirement(camelotUpstairsZone1),
			new ZoneRequirement(camelotUpstairsZone2));
		inMerlinRoom = new ZoneRequirement(merlinRoom);
		merlinNearby = new NpcCondition(NpcID.MERLIN_4059);
		onEntrana = new Conditions(LogicType.OR,
			new ZoneRequirement(entranaBoat),
			new ZoneRequirement(entranaIsland));
		inGalahadHouse = new ZoneRequirement(galahadHouse);
		inDraynorFrontManor = new ZoneRequirement(draynorManorFront);
		inDraynorManorBottomFloor = new ZoneRequirement(draynorManorBottomFloor);
		inDraynorManorSecondFloor = new ZoneRequirement(draynorManorSecondFloor);
		inDraynorManorTopFloor = new ZoneRequirement(draynorManorTopFloor);
		inMagicWhistleRoom = new ZoneRequirement(magicWhistleRoom);
		inTeleportLocation = new ZoneRequirement(teleportLocation);
		inFisherKingRealmEntrance = new ZoneRequirement(fisherKingRealmEntrance);
		titanNearby = new NpcCondition(NpcID.BLACK_KNIGHT_TITAN);
		inFisherKingRealmAfterTitan = new Conditions(LogicType.OR,
			new ZoneRequirement(fisherKingRealmAfterTitan1),
			new ZoneRequirement(fisherKingRealmAfterTitan2),
			new ZoneRequirement(fisherKingRealmAfterTitan3));
		talkedToFisherman = new Conditions(true, new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "You must be blind then. There's ALWAYS bells there<br>when I go to the castle."));
		inGrailBellRingLocation = new ZoneRequirement(grailBellRingLocation);
		inFisherKingCastle1BottomFloor = new ZoneRequirement(fisherKingRealmCastle1BottomFloor);
		inFisherKingCastle1SecondFloor = new ZoneRequirement(fisherKingRealmCastle1SecondFloor);
		inFisherKingRealm = new ZoneRequirement(fisherKingRealm);
		inFisherKingCastle2BottomFloor = new ZoneRequirement(fisherKingRealmCastle2BottomFloor);
		inFisherKingCastle2SecondFloor = new ZoneRequirement(fisherKingRealmCastle2SecondFloor);
		inFisherKingCastle2ThirdFloor = new ZoneRequirement(fisherKingRealmCastle2ThirdFloor);
	}

	public void setupSteps()
	{
		WorldPoint kingArthurWorldPoint = new WorldPoint(2763, 3513, 0);
		talkToKingArthur1 = new NpcStep(this, NpcID.KING_ARTHUR, kingArthurWorldPoint, "Talk to King Arthur in Camelot Castle to start.");
		talkToKingArthur1.addDialogStep("Tell me of this quest.");
		talkToKingArthur1.addDialogStep("I'd enjoy trying that.");
		goUpStairsCamelot = new ObjectStep(this, ObjectID.STAIRCASE_26106, new WorldPoint(2751, 3511, 0), "Go upstairs to talk to Merlin.");
		openMerlinDoor = new ObjectStep(this, ObjectID.DOOR_24, "Open the door to go to Merlin's room.");
		talkToMerlin = new NpcStep(this, NpcID.MERLIN_4059, new WorldPoint(2763, 3513, 1), "Talk to Merlin");
		talkToMerlin.addDialogStep("Where can I find Sir Galahad?");

		goToEntrana = new NpcStep(this, NpcID.MONK_OF_ENTRANA_1167, new WorldPoint(3048, 3235, 0), "Talk to a monk of Entrana. Bank all combat gear.", true);
		talkToHighPriest = new NpcStep(this, NpcID.HIGH_PRIEST, new WorldPoint(2851, 3348, 0), "Talk to the High Priest.");
		talkToHighPriest.addDialogSteps("Ask about the Holy Grail Quest", "Ok, I will go searching.");

		goToGalahad = new DetailedQuestStep(this, new WorldPoint(2612, 3475, 0), "Travel to Galahad's House. His house is west of McGrubor's Woods.");
		talkToGalahad = new NpcStep(this, NpcID.GALAHAD, new WorldPoint(2612, 3475, 0), "Talk to Galahad.");
		talkToGalahad.addDialogStep("I seek an item from the realm of the Fisher King.");

		goToDraynorManor = new DetailedQuestStep(this, new WorldPoint(3108, 3350, 0), "Travel to Draynor Manor.", holyTableNapkin);
		enterDraynorManor = new ObjectStep(this, ObjectID.LARGE_DOOR_135, "Enter Draynor Manor.", holyTableNapkin);
		goUpStairsDraynor1 = new ObjectStep(this, ObjectID.STAIRCASE_11498, new WorldPoint(3109, 3364, 0), "Go up the stairs in Draynor Manor.", holyTableNapkin);
		goUpStairsDraynor2 = new ObjectStep(this, ObjectID.STAIRCASE_11511, new WorldPoint(3105, 3363, 1), "Go up the second set of stairs in Draynor Manor.", holyTableNapkin);
		openWhistleDoor = new ObjectStep(this, ObjectID.DOOR_22, "Open the door to the Magic Whistles.", holyTableNapkin);
		takeWhistles = new DetailedQuestStep(this, new WorldPoint(3107, 3359, 2), "Pickup 2 Magic Whistles.", holyTableNapkin);

		goGetExcalibur = new ItemStep(this, "Go retrieve Excalibur from your bank. If you do not own Excalibur, you can retrieve it from the Lady of the Lake in Taverly for 500 coins.", twoMagicWhistles, excalibur);
		WorldPoint teleportLocationPoint = new WorldPoint(2742, 3236, 0);
		goToTeleportLocation1 = new DetailedQuestStep(this, teleportLocationPoint, "Go to the tower on Karamja near gold mine west of Brimhaven.", twoMagicWhistles, excalibur);
		blowWhistle1 = new ItemStep(this, "Blow the whistle once you are underneath of the tower.", highlightMagicWhistle1, excalibur);

		attackTitan = new NpcStep(this, NpcID.BLACK_KNIGHT_TITAN, "Kill the Black Knight Titan with Excalibur. (You only need to deal the killing blow with excalibur!)", twoMagicWhistles, excalibur);
		talkToFisherman = new NpcStep(this, NpcID.FISHERMAN_4065, new WorldPoint(2798, 4706, 0), "Talk to the fisherman by the river. After talking to him walk West to the castle.");
		talkToFisherman.addDialogStep("Any idea how to get into the castle?");
		pickupBell = new DetailedQuestStep(this, new WorldPoint(2762, 4694, 0), "Pickup the bell outside of the castle.");
		ringBell = new DetailedQuestStep(this, new WorldPoint(2762, 4694, 0), "Ring the grail bell directly north of the broken castle wall (Where you picked up the bell)", highlightGrailBell);
		ringBell.addIcon(ItemID.GRAIL_BELL);
		goUpStairsBrokenCastle = new ObjectStep(this, ObjectID.STAIRCASE_16671, new WorldPoint(2762, 4681, 0), "Go up the stairs inside of the castle.");
		talkToFisherKing = new NpcStep(this, NpcID.THE_FISHER_KING, "Talk to The Fisher King.");
		talkToFisherKing.addDialogStep("You don't look too well.");

		goToCamelot = new DetailedQuestStep(this, new WorldPoint(2758, 3486, 0), "Go back to Camelot.");
		talkToKingArthur2 = new NpcStep(this, NpcID.KING_ARTHUR, kingArthurWorldPoint, "Return to Camelot and talk to King Arthur.", emptyInvSpot);

		openSack = new ObjectStep(this, ObjectID.SACKS, new WorldPoint(2962, 3506, 0), "Travel to the Goblin Village North of Falador. Right click and open the sacks.", twoMagicWhistles);
		openSack.addDialogStep("Come with me, I shall make you a king.");

		goToTeleportLocation2 = new DetailedQuestStep(this, teleportLocationPoint, "Go to the tower on Karamja near gold mine west of Brimhaven.", oneMagicWhistle, goldFeather);
		blowWhistle2 = new ItemStep(this, "Blow the whistle once you are underneath of the tower.", highlightMagicWhistle2, goldFeather);

		openFisherKingCastleDoor = new ObjectStep(this, ObjectID.LARGE_DOOR_1524, "Open the door to the castle and enter.", goldFeather);
		goUpNewCastleStairs = new ObjectStep(this, ObjectID.STAIRCASE_16671, new WorldPoint(2649, 4684, 0), "Go up the stairs to the east.", goldFeather);
		goUpNewCastleLadder = new ObjectStep(this, ObjectID.LADDER_16683, "Climb the ladder on the second floor.", goldFeather);
		takeGrail = new DetailedQuestStep(this, new WorldPoint(2649, 4684, 2), "Pickup the Grail.", goldFeather);

		talkToKingArthur3 = new NpcStep(this, NpcID.KING_ARTHUR, kingArthurWorldPoint, "Return to Camelot and talk to King Arthur", grail);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Black Knight Titan (level 120)");
		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(excalibur);
		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(threeCamelotTele);
		reqs.add(ardyTele.quantity(2));
		reqs.add(faladorTele);
		reqs.add(sixtyCoins);
		reqs.add(antipoison);
		reqs.add(food);
		reqs.add(combatGear);
		return reqs;
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
				new ExperienceReward(Skill.PRAYER, 11000),
				new ExperienceReward(Skill.DEFENCE, 15300));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Access to the Fisher Realm."),
				new UnlockReward("Ability to put King Arthur picture on the wall in the POH."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting Off", Arrays.asList(talkToKingArthur1, goUpStairsCamelot, openMerlinDoor, talkToMerlin)));
		allSteps.add(new PanelDetails("Getting the Napkin", Arrays.asList(goToEntrana, talkToHighPriest, goToGalahad, talkToGalahad)));
		allSteps.add(new PanelDetails("Getting the Magic Whistles", Arrays.asList(goToDraynorManor, enterDraynorManor, goUpStairsDraynor1, goUpStairsDraynor2, openWhistleDoor, takeWhistles), holyTableNapkin));
		allSteps.add(new PanelDetails("Fisher King Realm Pt.1", Arrays.asList(goToTeleportLocation1, blowWhistle1, attackTitan, talkToFisherman, pickupBell, ringBell, goUpStairsBrokenCastle, talkToFisherKing), twoMagicWhistles, excalibur));
		allSteps.add(new PanelDetails("Finding Percival", Arrays.asList(talkToKingArthur2, openSack), emptyInvSpot, twoMagicWhistles));
		allSteps.add(new PanelDetails("Fisher King Realm Pt.2", Arrays.asList(goToTeleportLocation2, blowWhistle2, openFisherKingCastleDoor, goUpNewCastleStairs, goUpNewCastleLadder, takeGrail), oneMagicWhistle, goldFeather));
		allSteps.add(new PanelDetails("Finishing Up", Collections.singletonList(talkToKingArthur3), grail));

		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.MERLINS_CRYSTAL, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.ATTACK, 20));
		return req;
	}
}
