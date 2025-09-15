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
package com.questhelper.helpers.quests.holygrail;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.DialogRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

public class HolyGrail extends BasicQuestHelper
{
	// Required items
	ItemRequirement excalibur;

	// Recommended items
	ItemRequirement antipoison;
	ItemRequirement combatGear;
	ItemRequirement food;
	ItemRequirement threeCamelotTele;
	ItemRequirement ardyTele;
	ItemRequirement faladorTele;
	ItemRequirement sixtyCoins;
	ItemRequirement thirtyCoins;
	ItemRequirement draynorTele;

	// Mid-quest item requirements
	ItemRequirement holyTableNapkin;
	ItemRequirement twoMagicWhistles;
	ItemRequirement highlightMagicWhistle1;
	ItemRequirement goldFeather;
	ItemRequirement grailBell;
	ItemRequirement highlightGrailBell;
	ItemRequirement emptyInvSpot;
	ItemRequirement oneMagicWhistle;
	ItemRequirement highlightMagicWhistle2;
	ItemRequirement grail;

	// Zones
	Zone camelotGround;
	Zone camelotUpstairsZone1;
	Zone camelotUpstairsZone2;
	Zone merlinRoom;
	Zone entranaBoat;
	Zone entranaIsland;
	Zone draynorManorFront;
	Zone draynorManorBottomFloor;
	Zone draynorManorSecondFloor;
	Zone draynorManorTopFloor;
	Zone magicWhistleRoom;
	Zone teleportLocation;
	Zone fisherKingRealmAfterTitan1;
	Zone fisherKingRealmAfterTitan2;
	Zone fisherKingRealmAfterTitan3;
	Zone grailBellRingLocation;
	Zone fisherKingRealmCastle1BottomFloor;
	Zone fisherKingRealmCastle1SecondFloor;
	Zone fisherKingRealm;
	Zone fisherKingRealmCastle2BottomFloor;
	Zone fisherKingRealmCastle2SecondFloor;
	Zone fisherKingRealmCastle2ThirdFloor;

	// Miscellaneous requirements
	ZoneRequirement inCamelot;
	ZoneRequirement inCamelotUpstairs;
	ZoneRequirement inMerlinRoom;
	NpcCondition merlinNearby;
	ZoneRequirement onEntrana;
	ZoneRequirement inDraynorFrontManor;
	ZoneRequirement inDraynorManorBottomFloor;
	ZoneRequirement inDraynorManorSecondFloor;
	ZoneRequirement inDraynorManorTopFloor;
	ZoneRequirement inMagicWhistleRoom;
	ZoneRequirement inTeleportLocation;
	NpcCondition titanNearby;
	ZoneRequirement inFisherKingRealmAfterTitan;
	Conditions talkedToFisherman;
	ZoneRequirement inGrailBellRingLocation;
	ZoneRequirement inFisherKingCastle1BottomFloor;
	ZoneRequirement inFisherKingCastle1SecondFloor;
	ZoneRequirement inFisherKingRealm;
	ZoneRequirement inFisherKingCastle2BottomFloor;
	ZoneRequirement inFisherKingCastle2SecondFloor;
	ZoneRequirement inFisherKingCastle2ThirdFloor;

	// Steps
	NpcStep startQuest;

	ObjectStep goUpStairsCamelot;
	ObjectStep openMerlinDoor;
	NpcStep talkToMerlin;

	NpcStep goToEntrana;
	NpcStep talkToHighPriest;
	NpcStep talkToGalahad;
	DetailedQuestStep goToDraynorManor;
	ObjectStep enterDraynorManor;
	ObjectStep goUpStairsDraynor1;
	ObjectStep goUpStairsDraynor2;
	ObjectStep openWhistleDoor;
	DetailedQuestStep takeWhistles;
	ItemStep goGetExcalibur;
	DetailedQuestStep goToTeleportLocation1;
	ItemStep blowWhistle1;
	NpcStep attackTitan;
	NpcStep talkToFisherman;
	DetailedQuestStep pickupBell;
	DetailedQuestStep ringBell;
	ObjectStep goUpStairsBrokenCastle;
	NpcStep talkToFisherKing;
	DetailedQuestStep goToCamelot;
	NpcStep talkToKingArthur2;
	ObjectStep openSack;
	DetailedQuestStep goToTeleportLocation2;
	ItemStep blowWhistle2;
	ObjectStep openFisherKingCastleDoor;
	ObjectStep goUpNewCastleStairs;
	ObjectStep goUpNewCastleLadder;
	DetailedQuestStep takeGrail;
	NpcStep finishQuest;

	ConditionalStep findFisherKing;

	@Override
	protected void setupZones()
	{
		camelotGround = new Zone(new WorldPoint(2744, 3517, 0), new WorldPoint(2733, 3483, 0));
		camelotUpstairsZone1 = new Zone(new WorldPoint(2768, 3517, 1), new WorldPoint(2757, 3506, 1));
		camelotUpstairsZone2 = new Zone(new WorldPoint(2764, 3517, 1), new WorldPoint(2748, 3496, 1));
		merlinRoom = new Zone(new WorldPoint(2768, 3505, 1), new WorldPoint(2765, 3496, 1));
		entranaBoat = new Zone(new WorldPoint(2841, 3332, 0), new WorldPoint(2823, 3328, 2));
		entranaIsland = new Zone(new WorldPoint(2871, 3393, 0), new WorldPoint(2800, 3329, 2));
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

	@Override
	protected void setupRequirements()
	{
		excalibur = new ItemRequirement("Excalibur", ItemID.EXCALIBUR).isNotConsumed();
		holyTableNapkin = new ItemRequirement("Holy Table Napkin", ItemID.HOLY_TABLE_NAPKIN);
		twoMagicWhistles = new ItemRequirement("Magic Whistles", ItemID.MAGIC_WHISTLE, 2);
		threeCamelotTele = new ItemRequirement("Camelot Teleports", ItemID.POH_TABLET_CAMELOTTELEPORT, 3);
		draynorTele = new ItemRequirement("Draynor Teleport Tablet", ItemID.TELETAB_DRAYNOR, 1);
		draynorTele.addAlternates(ItemCollections.AMULET_OF_GLORIES);
		ardyTele = new ItemRequirement("Ardougne Teleport", ItemID.POH_TABLET_ARDOUGNETELEPORT);
		faladorTele = new ItemRequirement("Falador Teleport", ItemID.POH_TABLET_FALADORTELEPORT);
		sixtyCoins = new ItemRequirement("Coins", ItemCollections.COINS, 60);
		sixtyCoins.appendToTooltip("For travel from Ardougne to Brimhaven twice");
		thirtyCoins = new ItemRequirement("Coins", ItemCollections.COINS, 30);
		thirtyCoins.appendToTooltip("For travel from Ardougne to Brimhaven");
		antipoison = new ItemRequirement("Antipoison", ItemID._4DOSEANTIPOISON);
		antipoison.setTooltip("If you're below Combat level 41, the poisonous scorpions on Karamja are aggressive");
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		combatGear = new ItemRequirement("A weapon and armour (melee recommended)", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		emptyInvSpot = new ItemRequirement("Empty Inventory Spot", -1, 1);
		goldFeather = new ItemRequirement("Magic gold feather", ItemID.MAGIC_GOLDEN_FEATHER);
		grailBell = new ItemRequirement("Grail Bell", ItemID.GRAIL_BELL);
		oneMagicWhistle = new ItemRequirement("Magic Whistle", ItemID.MAGIC_WHISTLE);
		grail = new ItemRequirement("Holy Grail", ItemID.HOLY_GRAIL);

		inCamelot = new ZoneRequirement(camelotGround);
		inCamelotUpstairs = new ZoneRequirement(camelotUpstairsZone1, camelotUpstairsZone2);
		inMerlinRoom = new ZoneRequirement(merlinRoom);
		merlinNearby = new NpcCondition(NpcID.MERLIN2);
		onEntrana = new ZoneRequirement(entranaBoat, entranaIsland);
		inDraynorFrontManor = new ZoneRequirement(draynorManorFront);
		inDraynorManorBottomFloor = new ZoneRequirement(draynorManorBottomFloor);
		inDraynorManorSecondFloor = new ZoneRequirement(draynorManorSecondFloor);
		inDraynorManorTopFloor = new ZoneRequirement(draynorManorTopFloor);
		inMagicWhistleRoom = new ZoneRequirement(magicWhistleRoom);
		inTeleportLocation = new ZoneRequirement(teleportLocation);
		titanNearby = new NpcCondition(NpcID.BLACK_KNIGHT_TITAN);
		inFisherKingRealmAfterTitan = new ZoneRequirement(fisherKingRealmAfterTitan1, fisherKingRealmAfterTitan2, fisherKingRealmAfterTitan3);
		talkedToFisherman = new Conditions(true, new DialogRequirement("You must be blind then. There's ALWAYS bells there when I go to the castle."));
		inGrailBellRingLocation = new ZoneRequirement(grailBellRingLocation);
		inFisherKingCastle1BottomFloor = new ZoneRequirement(fisherKingRealmCastle1BottomFloor);
		inFisherKingCastle1SecondFloor = new ZoneRequirement(fisherKingRealmCastle1SecondFloor);
		inFisherKingRealm = new ZoneRequirement(fisherKingRealm);
		inFisherKingCastle2BottomFloor = new ZoneRequirement(fisherKingRealmCastle2BottomFloor);
		inFisherKingCastle2SecondFloor = new ZoneRequirement(fisherKingRealmCastle2SecondFloor);
		inFisherKingCastle2ThirdFloor = new ZoneRequirement(fisherKingRealmCastle2ThirdFloor);

		highlightMagicWhistle1 = new ItemRequirement("Magic Whistle", ItemID.MAGIC_WHISTLE, 2);
		highlightMagicWhistle1.setHighlightInInventory(true);

		highlightMagicWhistle2 = new ItemRequirement("Magic Whistle", ItemID.MAGIC_WHISTLE);
		highlightMagicWhistle2.setHighlightInInventory(true);

		highlightGrailBell = new ItemRequirement("Grail Bell", ItemID.GRAIL_BELL);
		highlightGrailBell.setHighlightInInventory(true);
	}

	public void setupSteps()
	{
		var kingArthurWorldPoint = new WorldPoint(2763, 3513, 0);

		startQuest = new NpcStep(this, NpcID.KING_ARTHUR, kingArthurWorldPoint, "Talk to King Arthur in Camelot Castle to start the quest.");
		startQuest.addDialogStep("Tell me of this quest.");
		startQuest.addDialogStep("Yes.");

		goUpStairsCamelot = new ObjectStep(this, ObjectID.KR_CAM_WOODENSTAIRS, new WorldPoint(2751, 3511, 0), "Go upstairs and talk to Merlin.");
		openMerlinDoor = new ObjectStep(this, ObjectID.MERLINWORKSHOP, "Go upstairs and talk to Merlin.");
		talkToMerlin = new NpcStep(this, NpcID.MERLIN2, new WorldPoint(2763, 3513, 1), "Go upstairs and talk to Merlin.");
		talkToMerlin.addSubSteps(goUpStairsCamelot, openMerlinDoor);

		goToEntrana = new NpcStep(this, NpcID.SHIPMONK1_C, new WorldPoint(3048, 3235, 0), "Head to Port Sarim and talk to a monk of Entrana for passage to Entrana. Bank all combat gear.", true);
		talkToHighPriest = new NpcStep(this, NpcID.HIGH_PRIEST_OF_ENTRANA, new WorldPoint(2851, 3348, 0), "Talk to the High Priest on Entrana.");
		talkToHighPriest.addDialogSteps("Ask about the Holy Grail Quest", "Ok, I will go searching.");

		talkToGalahad = new NpcStep(this, NpcID.BROTHER_GALAHAD, new WorldPoint(2612, 3475, 0), "Talk to Galahad in his house west of McGrubor's Woods.");
		talkToGalahad.addDialogStep("I seek an item from the realm of the Fisher King.");

		goToDraynorManor = new DetailedQuestStep(this, new WorldPoint(3108, 3350, 0), "Travel to Draynor Manor.", holyTableNapkin);
		goToDraynorManor.addTeleport(draynorTele);
		enterDraynorManor = new ObjectStep(this, ObjectID.HAUNTEDDOORR, "Enter Draynor Manor.", holyTableNapkin);
		goUpStairsDraynor1 = new ObjectStep(this, ObjectID.DRAYNOR_MANOR_STAIRS_UP, new WorldPoint(3109, 3364, 0), "Go up the stairs in Draynor Manor.", holyTableNapkin);
		goUpStairsDraynor2 = new ObjectStep(this, ObjectID.DRAYNOR_SPIRALSTAIRS, new WorldPoint(3105, 3363, 1), "Go up the second set of stairs in Draynor Manor.", holyTableNapkin);
		openWhistleDoor = new ObjectStep(this, ObjectID.WHISTLEDOOR, "Open the door to the Magic Whistles.", holyTableNapkin);
		takeWhistles = new DetailedQuestStep(this, new WorldPoint(3107, 3359, 2), "Pickup 2 Magic Whistles.", holyTableNapkin);

		goGetExcalibur = new ItemStep(this, "Go retrieve Excalibur from your bank. If you do not own Excalibur, you can retrieve it from the Lady of the Lake in Taverley for 500 coins.", twoMagicWhistles, excalibur);
		WorldPoint teleportLocationPoint = new WorldPoint(2742, 3236, 0);
		goToTeleportLocation1 = new DetailedQuestStep(this, teleportLocationPoint, "Go to the tower on Karamja near gold mine west of Brimhaven.", twoMagicWhistles, excalibur);
		goToTeleportLocation1.addDialogStep("I'd like to go to Brimhaven.");
		goToTeleportLocation1.addRecommended(thirtyCoins);
		goToTeleportLocation1.addRecommended(antipoison);
		goToTeleportLocation1.addSubSteps(goGetExcalibur);
		blowWhistle1 = new ItemStep(this, "Blow the whistle once you are underneath of the tower.", highlightMagicWhistle1, excalibur);

		attackTitan = new NpcStep(this, NpcID.BLACK_KNIGHT_TITAN, "Kill the Black Knight Titan with Excalibur. Melee is recommended as it has high Ranged and Magic defence. (You only need to deal the killing blow with excalibur!)", twoMagicWhistles, excalibur);
		talkToFisherman = new NpcStep(this, NpcID.GRAIL_FISHERMAN, new WorldPoint(2798, 4706, 0), "Talk to the fisherman by the river. After talking to him walk West to the castle.");
		talkToFisherman.addDialogStep("Any idea how to get into the castle?");
		pickupBell = new DetailedQuestStep(this, new WorldPoint(2762, 4694, 0), "Pickup the bell outside of the castle.");
		ringBell = new DetailedQuestStep(this, new WorldPoint(2762, 4694, 0), "Ring the grail bell directly north of the broken castle wall (Where you picked up the bell)", highlightGrailBell);
		ringBell.addIcon(ItemID.GRAIL_BELL);
		goUpStairsBrokenCastle = new ObjectStep(this, ObjectID.SPIRALSTAIRS, new WorldPoint(2762, 4681, 0), "Go up the stairs inside of the castle.");
		talkToFisherKing = new NpcStep(this, NpcID.FISHER_KING, "Talk to The Fisher King.");
		talkToFisherKing.addDialogStep("You don't look too well.");

		findFisherKing = new ConditionalStep(this, talkToGalahad);
		findFisherKing.addStep(inFisherKingCastle1SecondFloor, talkToFisherKing);
		findFisherKing.addStep(inFisherKingCastle1BottomFloor, goUpStairsBrokenCastle);
		findFisherKing.addStep(and(grailBell, inFisherKingRealmAfterTitan), ringBell);
		findFisherKing.addStep(talkedToFisherman, pickupBell);
		findFisherKing.addStep(inFisherKingRealmAfterTitan, talkToFisherman);
		findFisherKing.addStep(and(excalibur, titanNearby), attackTitan);
		findFisherKing.addStep(and(twoMagicWhistles, inTeleportLocation, excalibur), blowWhistle1);
		findFisherKing.addStep(and(twoMagicWhistles, excalibur), goToTeleportLocation1);
		findFisherKing.addStep(twoMagicWhistles, goGetExcalibur);
		findFisherKing.addStep(and(inMagicWhistleRoom, holyTableNapkin), takeWhistles);
		findFisherKing.addStep(and(holyTableNapkin, inDraynorManorTopFloor), openWhistleDoor);
		findFisherKing.addStep(and(holyTableNapkin, inDraynorManorSecondFloor), goUpStairsDraynor2);
		findFisherKing.addStep(and(holyTableNapkin, inDraynorManorBottomFloor), goUpStairsDraynor1);
		findFisherKing.addStep(and(holyTableNapkin, inDraynorFrontManor), enterDraynorManor);
		findFisherKing.addStep(holyTableNapkin, goToDraynorManor);
		findFisherKing.setLockingCondition(twoMagicWhistles);

		goToCamelot = new DetailedQuestStep(this, new WorldPoint(2758, 3486, 0), "Go back to Camelot.");
		talkToKingArthur2 = new NpcStep(this, NpcID.KING_ARTHUR, kingArthurWorldPoint, "Return to Camelot and talk to King Arthur.", emptyInvSpot);

		openSack = new ObjectStep(this, ObjectID.PERCY_SACKS, new WorldPoint(2962, 3506, 0), "Travel to the Goblin Village North of Falador. Right click and open the sacks.", twoMagicWhistles);
		openSack.addDialogStep("Come with me, I shall make you a king.");

		goToTeleportLocation2 = new DetailedQuestStep(this, teleportLocationPoint, "Go to the tower on Karamja near gold mine west of Brimhaven.", oneMagicWhistle, goldFeather);
		blowWhistle2 = new ItemStep(this, "Blow the whistle once you are underneath of the tower.", highlightMagicWhistle2, goldFeather);

		openFisherKingCastleDoor = new ObjectStep(this, ObjectID.CASTLEDOUBLEDOORR, "Open the door to the castle and enter.", goldFeather);
		goUpNewCastleStairs = new ObjectStep(this, ObjectID.SPIRALSTAIRS, new WorldPoint(2649, 4684, 0), "Go up the stairs to the east.", goldFeather);
		goUpNewCastleLadder = new ObjectStep(this, ObjectID.LADDER, "Climb the ladder on the second floor.", goldFeather);
		takeGrail = new DetailedQuestStep(this, new WorldPoint(2649, 4684, 2), "Pickup the Grail.", goldFeather);

		finishQuest = new NpcStep(this, NpcID.KING_ARTHUR, kingArthurWorldPoint, "Return to Camelot and talk to King Arthur", grail);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, startQuest);

		var findingMerlin = new ConditionalStep(this, goUpStairsCamelot);
		findingMerlin.addStep(and(inMerlinRoom, merlinNearby), talkToMerlin);
		findingMerlin.addStep(inCamelotUpstairs, openMerlinDoor);

		steps.put(2, findingMerlin);

		var findHighPriest = new ConditionalStep(this, goToEntrana);
		findHighPriest.addStep(onEntrana, talkToHighPriest);

		steps.put(3, findHighPriest);

		steps.put(4, findFisherKing);
		steps.put(7, findFisherKing);

		var findPercival = new ConditionalStep(this, talkToKingArthur2);
		findPercival.addStep(goldFeather, openSack);

		steps.put(8, findPercival);

		var finishQuest = new ConditionalStep(this, goToTeleportLocation2);
		finishQuest.addStep(grail, this.finishQuest);
		finishQuest.addStep(inFisherKingCastle2ThirdFloor, takeGrail);
		finishQuest.addStep(inFisherKingCastle2SecondFloor, goUpNewCastleLadder);
		finishQuest.addStep(inFisherKingCastle2BottomFloor, goUpNewCastleStairs);
		finishQuest.addStep(inFisherKingRealm, openFisherKingCastleDoor);
		finishQuest.addStep(inTeleportLocation, blowWhistle2);

		steps.put(9, finishQuest);

		return steps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.MERLINS_CRYSTAL, QuestState.FINISHED),
			new SkillRequirement(Skill.ATTACK, 20)
		);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			excalibur
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			threeCamelotTele,
			ardyTele.quantity(2),
			draynorTele,
			faladorTele,
			sixtyCoins,
			antipoison,
			food,
			combatGear
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Black Knight Titan (level 120)"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.PRAYER, 11000),
			new ExperienceReward(Skill.DEFENCE, 15300)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to the Fisher Realm."),
			new UnlockReward("Ability to put King Arthur picture on the wall in the POH.")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			startQuest,
			talkToMerlin
		)));

		sections.add(new PanelDetails("Getting the Napkin", List.of(
			goToEntrana,
			talkToHighPriest,
			talkToGalahad
		)));

		sections.add(new PanelDetails("Getting the Magic Whistles", List.of(
			goToDraynorManor,
			enterDraynorManor,
			goUpStairsDraynor1,
			goUpStairsDraynor2,
			openWhistleDoor,
			takeWhistles
		), List.of(
			holyTableNapkin
		), List.of(
			draynorTele
		)));

		sections.add(new PanelDetails("Fisher King Realm Pt.1", List.of(
			goToTeleportLocation1,
			blowWhistle1,
			attackTitan,
			talkToFisherman,
			pickupBell,
			ringBell,
			goUpStairsBrokenCastle,
			talkToFisherKing
		), List.of(
			twoMagicWhistles,
			excalibur
		), List.of(
			thirtyCoins,
			antipoison
		)));

		sections.add(new PanelDetails("Finding Percival", List.of(
			talkToKingArthur2,
			openSack
		), List.of(
			emptyInvSpot,
			twoMagicWhistles
		)));

		sections.add(new PanelDetails("Fisher King Realm Pt.2", List.of(
			goToTeleportLocation2,
			blowWhistle2,
			openFisherKingCastleDoor,
			goUpNewCastleStairs,
			goUpNewCastleLadder,
			takeGrail
		), List.of(
			oneMagicWhistle,
			goldFeather
		)));

		sections.add(new PanelDetails("Finishing up", List.of(
			finishQuest
		), List.of(
			grail
		)));

		return sections;
	}
}
