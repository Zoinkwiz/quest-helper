package com.questhelper.quests.eaglespeak;

import com.questhelper.QuestHelperQuest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.ObjectCondition;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;

@QuestDescriptor(
	quest = QuestHelperQuest.EAGLES_PEAK
)
public class EaglesPeak extends BasicQuestHelper
{
	ItemRequirement yellowDye, coins, tar, birdBook, metalFeather, tenEagleFeathers, fakeBeak, eagleCape, bronzeFeather, silverFeather, goldFeather, birdFeed, ferret;

	ConditionForStep hasBirdBook, hasMetalFeather, inMainCavern, spokenToNickolaus, hasTenFeathers, spokenOnceToAsyff, spokenTwiceToAsyff, inBronzeRoom,
		bronzeRoomPedestalUp, bronzeRoomPedestalLowered, winch1NotDone, winch2NotDone, winch3NotDone, winch4NotDone, hasSolvedBronze, hasBronzeFeather, hasSilverFeather,
		hasGoldFeather, hasInspectedSilverPedestal, inSilverRoom, hasInspectedRocks1, hasInspectedRocks2, hasInspectedOpening, threatenedKebbit, inGoldRoom, hasBirdFeed,
		lever1OriginalPosition, lever1Pulled, lever2Pulled, lever3Pulled, lever4Pulled, bird1Moved, bird2Moved, bird3Moved, bird4Moved, bird5Moved, hasInsertedBronzeFeather,
		hasInsertedSilverFeather, hasInsertedGoldFeather;

	QuestStep speakToCharlie, inspectBooks, clickBook, inspectBooksForFeather, useFeatherOnDoor, enterPeak, shoutAtNickolaus, pickupFeathers, enterEastCave,
		goToFancyStore, speakAsyffAgain, returnToEaglesPeak, enterBronzeRoom, attemptToTakeBronzeFeather, winch1, winch2, winch3, winch4, grabBronzeFeather,
		enterMainCavernFromBronze, enterSilverRoom, inspectSilverPedestal, enterMainCavernFromSilver, enterGoldRoom, inspectRocks1, inspectRocks2, inspectOpening,
		threatenKebbit, pickupSilverFeather, collectFeed, pullLever1Down, pushLever1Up, pullLever2Down, pullLever3Down, pullLever4Down, fillFeeder1, fillFeeder2,
		fillFeeder3, fillFeeder4, fillFeeder5, fillFeeder6, fillFeeder4Again, fillFeeder7, grabGoldFeather, enterMainCavernFromGold, useFeathersOnStoneDoor,
		useSilverFeathersOnStoneDoor, useBronzeFeathersOnStoneDoor, useGoldFeathersOnStoneDoor, useGoldBronzeFeathersOnStoneDoor, useGoldSilverFeathersOnStoneDoor,
		useBronzeSilverFeathersOnStoneDoor, sneakPastEagle, speakToNickolaus, speakToNickolausInTheCamp, speakToCharlieAgain;

	Zone inMainCave, inSilverRoomZone, inGoldRoomZone1, inGoldRoomZone2, inNest;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, speakToCharlie);

		ConditionalStep getFeatherKey = new ConditionalStep(this, inspectBooks);
		getFeatherKey.addStep(hasBirdBook, clickBook);

		steps.put(5, getFeatherKey);

		ConditionalStep enterEaglesPeak = new ConditionalStep(this, inspectBooksForFeather);
		enterEaglesPeak.addStep(hasMetalFeather, useFeatherOnDoor);

		steps.put(10, enterEaglesPeak);

		Conditions hasGoldFeatherOrUsed = new Conditions(LogicType.OR, hasGoldFeather, hasInsertedGoldFeather);
		Conditions hasSilverFeatherOrUsed = new Conditions(LogicType.OR, hasSilverFeather, hasInsertedSilverFeather);
		Conditions hasBronzeFeatherOrUsed = new Conditions(LogicType.OR, hasBronzeFeather, hasInsertedBronzeFeather);

		ConditionalStep createDisguises = new ConditionalStep(this, enterEaglesPeak);
		createDisguises.addStep(new Conditions(inMainCavern, hasInsertedGoldFeather, hasInsertedBronzeFeather, hasSilverFeatherOrUsed), useSilverFeathersOnStoneDoor);
		createDisguises.addStep(new Conditions(inMainCavern, hasInsertedGoldFeather, hasBronzeFeatherOrUsed, hasInsertedSilverFeather), useBronzeFeathersOnStoneDoor);
		createDisguises.addStep(new Conditions(inMainCavern, hasGoldFeatherOrUsed, hasInsertedBronzeFeather, hasInsertedSilverFeather), useGoldFeathersOnStoneDoor);
		createDisguises.addStep(new Conditions(inMainCavern, hasGoldFeatherOrUsed, hasBronzeFeatherOrUsed, hasInsertedSilverFeather), useGoldBronzeFeathersOnStoneDoor);
		createDisguises.addStep(new Conditions(inMainCavern, hasGoldFeatherOrUsed, hasInsertedBronzeFeather, hasSilverFeatherOrUsed), useGoldSilverFeathersOnStoneDoor);
		createDisguises.addStep(new Conditions(inMainCavern, hasInsertedGoldFeather, hasBronzeFeatherOrUsed, hasSilverFeatherOrUsed), useBronzeSilverFeathersOnStoneDoor);
		createDisguises.addStep(new Conditions(inMainCavern, hasGoldFeatherOrUsed, hasBronzeFeatherOrUsed, hasSilverFeatherOrUsed), useFeathersOnStoneDoor);
		createDisguises.addStep(new Conditions(inGoldRoom, hasGoldFeatherOrUsed), enterMainCavernFromGold);
		createDisguises.addStep(new Conditions(inGoldRoom, bird5Moved), grabGoldFeather);
		createDisguises.addStep(new Conditions(inGoldRoom, lever3Pulled, lever4Pulled, bird3Moved), fillFeeder6);
		createDisguises.addStep(new Conditions(inGoldRoom, lever3Pulled, lever4Pulled), fillFeeder4Again);
		createDisguises.addStep(new Conditions(inGoldRoom, lever4Pulled), pullLever3Down);
		createDisguises.addStep(new Conditions(inGoldRoom, lever1OriginalPosition, lever2Pulled, lever3Pulled, bird4Moved), pullLever4Down);
		createDisguises.addStep(new Conditions(inGoldRoom, lever1OriginalPosition, lever2Pulled, lever3Pulled), fillFeeder5);
		createDisguises.addStep(new Conditions(inGoldRoom, lever1OriginalPosition, lever2Pulled, bird3Moved), pullLever3Down);
		createDisguises.addStep(new Conditions(inGoldRoom, lever1OriginalPosition, lever2Pulled), fillFeeder4);
		createDisguises.addStep(new Conditions(inGoldRoom, lever1Pulled, lever2Pulled, bird4Moved), fillFeeder7); // If you've blocked lever 1
		createDisguises.addStep(new Conditions(inGoldRoom, lever1Pulled, lever2Pulled), pushLever1Up);
		createDisguises.addStep(new Conditions(inGoldRoom, lever1Pulled, bird1Moved, bird2Moved), pullLever2Down);
		createDisguises.addStep(new Conditions(inGoldRoom, lever1Pulled, bird2Moved), fillFeeder3);
		createDisguises.addStep(new Conditions(inGoldRoom, lever1Pulled, bird1Moved), fillFeeder2);
		createDisguises.addStep(new Conditions(inGoldRoom, lever1Pulled), fillFeeder1);
		createDisguises.addStep(new Conditions(inGoldRoom, hasBirdFeed), pullLever1Down);
		createDisguises.addStep(new Conditions(inGoldRoom), collectFeed);
		createDisguises.addStep(new Conditions(inMainCavern, hasSilverFeatherOrUsed, hasBronzeFeatherOrUsed), enterGoldRoom);
		createDisguises.addStep(new Conditions(inSilverRoom, hasSilverFeatherOrUsed), enterMainCavernFromSilver);
		createDisguises.addStep(new Conditions(inSilverRoom, threatenedKebbit), pickupSilverFeather);
		createDisguises.addStep(new Conditions(inSilverRoom, hasInspectedOpening), threatenKebbit);
		createDisguises.addStep(new Conditions(inSilverRoom, hasInspectedRocks2), inspectOpening);
		createDisguises.addStep(new Conditions(inSilverRoom, hasInspectedRocks1), inspectRocks2);
		createDisguises.addStep(new Conditions(inSilverRoom, hasInspectedSilverPedestal), inspectRocks1);
		createDisguises.addStep(new Conditions(inSilverRoom), inspectSilverPedestal);
		createDisguises.addStep(new Conditions(inMainCavern, hasBronzeFeatherOrUsed), enterSilverRoom);
		createDisguises.addStep(new Conditions(bronzeRoomPedestalLowered, hasBronzeFeatherOrUsed), enterMainCavernFromBronze);
		createDisguises.addStep(new Conditions(hasSolvedBronze, bronzeRoomPedestalLowered), grabBronzeFeather);
		createDisguises.addStep(new Conditions(bronzeRoomPedestalUp, winch4NotDone), winch4);
		createDisguises.addStep(new Conditions(bronzeRoomPedestalUp, winch3NotDone), winch3);
		createDisguises.addStep(new Conditions(bronzeRoomPedestalUp, winch2NotDone), winch2);
		createDisguises.addStep(new Conditions(bronzeRoomPedestalUp, winch1NotDone), winch1);
		createDisguises.addStep(new Conditions(inBronzeRoom, spokenTwiceToAsyff), attemptToTakeBronzeFeather);
		createDisguises.addStep(new Conditions(new ZoneCondition(inMainCave), spokenTwiceToAsyff), enterBronzeRoom);
		createDisguises.addStep(new Conditions(spokenTwiceToAsyff), returnToEaglesPeak);
		createDisguises.addStep(new Conditions(spokenOnceToAsyff, hasTenFeathers), speakAsyffAgain);
		createDisguises.addStep(new Conditions(spokenToNickolaus, hasTenFeathers), goToFancyStore);
		createDisguises.addStep(new Conditions(spokenToNickolaus, inMainCavern), pickupFeathers);
		createDisguises.addStep(inMainCavern, shoutAtNickolaus);
		steps.put(15, createDisguises);

		ConditionalStep freeNickolaus = new ConditionalStep(this, enterEaglesPeak);
		freeNickolaus.addStep(new Conditions(new ZoneCondition(inNest)), speakToNickolaus);
		freeNickolaus.addStep(new Conditions(inMainCavern), sneakPastEagle);
		steps.put(20, freeNickolaus);

		steps.put(25, speakToNickolausInTheCamp);

		steps.put(30, speakToNickolausInTheCamp);

		steps.put(35, speakToCharlieAgain);

		return steps;
	}

	public void setupItemRequirements() {
		yellowDye = new ItemRequirement("Yellow dye", ItemID.YELLOW_DYE);
		coins = new ItemRequirement("Coins", ItemID.COINS_995, 50);
		tar = new ItemRequirement("Swamp tar", ItemID.SWAMP_TAR);
		birdBook = new ItemRequirement("Bird book", ItemID.BIRD_BOOK);
		metalFeather = new ItemRequirement("Metal feather", ItemID.METAL_FEATHER);
		metalFeather.setTip("You can get another Metal Feather by searching the books in the camp north of Eagles' Peak");
		tenEagleFeathers = new ItemRequirement("Eagle feather", ItemID.EAGLE_FEATHER, 10);
		fakeBeak = new ItemRequirement("Fake beak", ItemID.FAKE_BEAK, 2);
		fakeBeak.setTip("If you lose one of your beaks you'll need to have Azyff make you a new one.");
		eagleCape = new ItemRequirement("Eagle cape", ItemID.EAGLE_CAPE, 2);
		eagleCape.setTip("If you lose one of your capes you'll need to have Azyff make you a new one.");
		bronzeFeather = new ItemRequirement("Bronze feather", ItemID.BRONZE_FEATHER);
		silverFeather = new ItemRequirement("Silver feather", ItemID.SILVER_FEATHER);
		goldFeather = new ItemRequirement("Golden feather", ItemID.GOLDEN_FEATHER_10175);
		birdFeed = new ItemRequirement("Odd bird seed", ItemID.ODD_BIRD_SEED, 6);
		ferret = new ItemRequirement("Ferret", ItemID.FERRET);
		ferret.setTip("If you lose your ferret you'll need to catch a new one with a box trap north of Eagles' Peak.");
	}

	public void loadZones() {
		inMainCave = new Zone(new WorldPoint(1983,4940,3), new WorldPoint(2035,4987,3));
		inSilverRoomZone = new Zone(new WorldPoint(1925, 4863, 2), new WorldPoint(1976, 4884, 2));
		inGoldRoomZone1 = new Zone(new WorldPoint(1924, 4890, 2),  new WorldPoint(1959, 4921, 2));
		inGoldRoomZone2 = new Zone(new WorldPoint(1959, 4890, 2),  new WorldPoint(1959, 4901, 2));
		inNest = new Zone(new WorldPoint(2002, 4956, 3),  new WorldPoint(2012, 4962, 3));
	}

	public void setupConditions() {
		hasBirdBook = new ItemRequirementCondition(birdBook);
		hasMetalFeather = new ItemRequirementCondition(metalFeather);
		inBronzeRoom = new ObjectCondition(ObjectID.PEDESTAL_19980);
		bronzeRoomPedestalUp = new ObjectCondition(ObjectID.PEDESTAL_19981);
		bronzeRoomPedestalLowered = new ObjectCondition(ObjectID.STONE_PEDESTAL_19984);
		inMainCavern = new ZoneCondition(inMainCave);
		spokenToNickolaus = new VarbitCondition(3110, 3);
		hasTenFeathers = new ItemRequirementCondition(tenEagleFeathers);
		spokenOnceToAsyff = new VarbitCondition(3110, 4);
		spokenTwiceToAsyff = new VarbitCondition(3110, 5);
		winch1NotDone = new VarbitCondition(3101, 0);
		winch2NotDone = new VarbitCondition(3102, 0);
		winch3NotDone = new VarbitCondition(3103, 0);
		winch4NotDone = new VarbitCondition(3104, 0);
		hasSolvedBronze = new VarbitCondition( 3105, 0);
		hasBronzeFeather = new ItemRequirementCondition(bronzeFeather);
		hasSilverFeather = new ItemRequirementCondition(silverFeather);
		hasGoldFeather = new ItemRequirementCondition(goldFeather);
		hasInspectedSilverPedestal = new VarbitCondition(3099, 1);
		hasInspectedRocks1 = new VarbitCondition(3099, 2);
		hasInspectedRocks2 = new VarbitCondition(3099, 3);
		hasInspectedOpening = new VarbitCondition(3099, 4);
		threatenedKebbit = new VarbitCondition(3099, 5);
		inSilverRoom = new ZoneCondition(inSilverRoomZone);
		inGoldRoom = new ZoneCondition(inGoldRoomZone1, inGoldRoomZone2);
		lever1OriginalPosition = new VarbitCondition(3092, 0);
		lever1Pulled = new VarbitCondition(3092, 1);
		lever2Pulled = new VarbitCondition(3093, 1);
		lever3Pulled = new VarbitCondition(3090, 1);
		lever4Pulled = new VarbitCondition(3091, 1);
		bird1Moved = new VarbitCondition(3098, 1);
		bird2Moved = new VarbitCondition(3097, 1);
		bird3Moved = new VarbitCondition(3095, 1);
		bird4Moved = new VarbitCondition(3094, 1);
		bird5Moved = new VarbitCondition(3096, 1);
		hasBirdFeed = new ItemRequirementCondition(birdFeed);
		hasInsertedBronzeFeather = new VarbitCondition(3108, 1);
		hasInsertedSilverFeather = new VarbitCondition(3099, 6);
		hasInsertedGoldFeather = new VarbitCondition(3107, 1);
	}

	public void setupSteps() {
		speakToCharlie = new NpcStep(this, NpcID.CHARLIE_1495, new WorldPoint(2607, 3264, 0),
			"Speak to Charlie in the Ardougne Zoo.");
		speakToCharlie.addDialogStep("Ah, you sound like someone who needs a quest doing!");
		speakToCharlie.addDialogStep("Sure.  Any idea where I should start looking?");

		inspectBooks = new ObjectStep(this, ObjectID.BOOKS_19886, new WorldPoint(2319, 3506, 0),
			"Go to the camp north of Eagles' Peak and search the pile of books for a Bird Book.");

		clickBook = new DetailedQuestStep(this, "Click the Bird Book for a Metal Feather.");

		inspectBooksForFeather = new ObjectStep(this, ObjectID.BOOKS_19886, new WorldPoint(2319, 3506, 0),
			"Go to the camp north of Eagles' Peak and search the pile of books to get the Metal Feather back.");

		useFeatherOnDoor = new ObjectStep(this, ObjectID.ROCKY_OUTCROP_19925, new WorldPoint(2328, 3494, 0),
			"Use the Metal Feather on the Rocky Outcrop on Eagles' Peak.", metalFeather);
		useFeatherOnDoor.addIcon(ItemID.METAL_FEATHER);

		enterPeak = new ObjectStep(this, NullObjectID.NULL_19790, new WorldPoint(2328, 3494, 0),
			"Enter Eagles' Peak through the Rocky Outcrop.");

		shoutAtNickolaus = new NpcStep(this, NpcID.NICKOLAUS_1484, new WorldPoint(2006, 4960, 3),
			"Shout to Nickolaus from across the chasm.");
		shoutAtNickolaus.addDialogStep("The Ardougne zookeeper sent me to find you.");
		shoutAtNickolaus.addDialogStep("Well if you gave me a ferret I could take it back for you.");
		shoutAtNickolaus.addDialogStep("Could I help at all?");

		pickupFeathers = new ObjectStep(this, ObjectID.GIANT_FEATHERS, "Pick up 10 Eagle feathers from the piles in the main cavern.", tenEagleFeathers);

		goToFancyStore = new NpcStep(this, NpcID.ASYFF, new WorldPoint(3281, 3398,0), "Go speak to Asyff in south-east Varrock to have a disguise made.",
			yellowDye, coins, tar, tenEagleFeathers);
		goToFancyStore.addDialogStep("Well, specifically I'm after a couple of bird costumes.");

		speakAsyffAgain = new NpcStep(this, NpcID.ASYFF, new WorldPoint(3281, 3398,0), "Speak to Asyff again.",
			yellowDye, coins, tar, tenEagleFeathers);
		speakAsyffAgain.addDialogStep("I've got the feathers and materials you requested.");
		speakAsyffAgain.addDialogStep("Okay, here are the materials. Eagle me up.");

		returnToEaglesPeak = new ObjectStep(this, NullObjectID.NULL_19790, new WorldPoint(2328, 3494, 0),
			"Enter Eagles' Peak through the Rocky Outcrop.", fakeBeak, eagleCape);

		enterEastCave = new ObjectStep(this, ObjectID.TUNNEL_19897, new WorldPoint(2023, 4982, 3), "Enter the eastern cavern of Eagles' Peak.");

		enterBronzeRoom = new ObjectStep(this, ObjectID.TUNNEL_19909, new WorldPoint(1986, 4949, 3), "Enter the south-western cavern of Eagles' Peak.");

		attemptToTakeBronzeFeather = new ObjectStep(this, ObjectID.PEDESTAL_19980, new WorldPoint(1974, 4915, 2), "Try to take the feather from the pedestal.");
		winch1 = new ObjectStep(this, ObjectID.WINCH_19976, new WorldPoint(1970, 4919, 2), "Use the winches in the corners of the room.");
		winch2 = new ObjectStep(this, ObjectID.WINCH_19977, new WorldPoint(1978, 4919, 2), "Use the winches in the corners of the room.");
		winch3 = new ObjectStep(this, ObjectID.WINCH_19978, new WorldPoint(1970, 4910, 2), "Use the winches in the corners of the room.");
		winch4 = new ObjectStep(this, ObjectID.WINCH_19979, new WorldPoint(1978, 4910, 2), "Use the winches in the corners of the room.");

		grabBronzeFeather = new ObjectStep(this, ObjectID.STONE_PEDESTAL_19984, new WorldPoint(1974, 4915, 2), "Take the feather from the pedestal.");

		enterMainCavernFromBronze = new ObjectStep(this, ObjectID.TUNNEL_19906, new WorldPoint(1974, 4907, 2), "Return the main cavern.");

		enterSilverRoom = new ObjectStep(this, ObjectID.TUNNEL_19903, new WorldPoint(1986, 4972, 3), "Enter the north-western cavern of Eagles' Peak.");

		inspectSilverPedestal = new ObjectStep(this, ObjectID.STONE_PEDESTAL_19974, new  WorldPoint(1947, 4873, 2),"Inspect the Stone Pedestal here.");

		enterMainCavernFromSilver = new ObjectStep(this, ObjectID.TUNNEL_19900, new WorldPoint(1947, 4867, 2), "Return to the main cavern.");

		inspectRocks1 = new ObjectStep(this, ObjectID.ROCKS_19969, new WorldPoint(1961, 4875, 2), "Inspect the rocks east of the pedestal.");

		inspectRocks2 = new ObjectStep(this, ObjectID.ROCKS_19970, new WorldPoint(1967, 4879, 2), "Inspect the rocks north east of the last rock.");

		inspectOpening = new ObjectStep(this, ObjectID.OPENING, new WorldPoint(1971, 4886, 2), "Inspect the opening north of the second rock.");

		threatenKebbit = new NpcStep(this, NpcID.KEBBIT, new WorldPoint(1971, 4880, 2), "Threaten the Kebbit that appears. If the kebbit's gone, re-inspect the opening.");
		threatenKebbit.addDialogStep("Taunt the kebbit.");

		pickupSilverFeather = new ObjectStep(this, ObjectID.OPENING, new WorldPoint(1971, 4886, 2), "Pick up the silver feather. If it's despawned, inspect the opening to get it.");

		enterGoldRoom = new ObjectStep(this, ObjectID.TUNNEL_19897, new WorldPoint(2023, 4982, 3), "Enter the tunnel in the north east of the main cavern.");

		collectFeed = new ObjectStep(this, ObjectID.BIRDSEED_HOLDER, new WorldPoint(1958, 4906, 2), "Collect 6 birdseed from the Birdseed holder.");

		pullLever1Down = new ObjectStep(this, NullObjectID.NULL_19948, new WorldPoint(1943, 4911, 2), "Pull the lever west of the entrance down.");

		pushLever1Up = new ObjectStep(this, NullObjectID.NULL_19948, new WorldPoint(1943, 4911, 2), "Push the lever west of the entrance up.");

		pullLever2Down = new ObjectStep(this, NullObjectID.NULL_19949, new WorldPoint(1978, 4891, 2), "Pull the lever in the south east corner down.");

		pullLever3Down = new ObjectStep(this, NullObjectID.NULL_19946, new WorldPoint(1935, 4902, 2), "Pull the lever in the south west corner down.");

		pullLever4Down = new ObjectStep(this, NullObjectID.NULL_19947, new WorldPoint(1925, 4915, 2), "Pull the lever in the north west corner down.");

		fillFeeder1 = new ObjectStep(this, ObjectID.BIRD_FEEDER_19939, new WorldPoint(1966, 4890, 2),
			"Use the odd bird seed on the Bird feeder in the far south eastern corner.");
		fillFeeder1.addIcon(ItemID.ODD_BIRD_SEED);

		fillFeeder2 = new ObjectStep(this, ObjectID.BIRD_FEEDER_19938, new WorldPoint(1962, 4894, 2),
			"Use the odd bird seed on the marked Bird feeder.");
		fillFeeder2.addIcon(ItemID.ODD_BIRD_SEED);

		/* Only needed if the player's messed up */
		fillFeeder3 = new ObjectStep(this, ObjectID.BIRD_FEEDER_19943, new WorldPoint(1962, 4901, 2),
			"Use the odd bird seed on the marked Bird feeder, as you've moved the wrong bird.");
		fillFeeder3.addIcon(ItemID.ODD_BIRD_SEED);

		fillFeeder4 = new ObjectStep(this, ObjectID.BIRD_FEEDER_19937, new WorldPoint(1947, 4898, 2), "Put odd bird feed into the feeder in the north east of the room.");
		fillFeeder4.addIcon(ItemID.ODD_BIRD_SEED);

		fillFeeder5 = new ObjectStep(this, ObjectID.BIRD_FEEDER_19936, new WorldPoint(1945, 4915, 2), "Put odd bird feed into the feeder in the south of the room.");
		fillFeeder5.addIcon(ItemID.ODD_BIRD_SEED);

		fillFeeder6 = new ObjectStep(this, ObjectID.BIRD_FEEDER_19941, new WorldPoint(1935, 4897, 2), "Put odd bird feed into the feeder in the south west of the room");
		fillFeeder6.addIcon(ItemID.ODD_BIRD_SEED);

		fillFeeder4Again = new ObjectStep(this, ObjectID.BIRD_FEEDER_19937, new WorldPoint(1947, 4898, 2), "Put odd bird feed into the feeder in the south of the room.");
		fillFeeder4Again.addIcon(ItemID.ODD_BIRD_SEED);

		fillFeeder7 = new ObjectStep(this, ObjectID.BIRD_FEEDER_19940, new WorldPoint(1931, 4916, 2), "Put odd bird feed in the feeder in the north west of the room.");
		fillFeeder7.addIcon(ItemID.ODD_BIRD_SEED);

		grabGoldFeather = new ObjectStep(this, ObjectID.STONE_PEDESTAL_19950, new WorldPoint(1928, 4907, 2), "Grab the Golden feather from the pedestal.");

		enterMainCavernFromGold = new ObjectStep(this, ObjectID.TUNNEL_19894, new WorldPoint(1957, 4909, 2), "Return to the main cavern.");

		useFeathersOnStoneDoor = new ObjectStep(this, ObjectID.STONE_DOOR_19916, new WorldPoint(2003, 4948, 3), "Use all three feathers on the door.",
			goldFeather, silverFeather, bronzeFeather);
		useFeathersOnStoneDoor.addIcon(ItemID.GOLDEN_FEATHER);
		useBronzeFeathersOnStoneDoor = new ObjectStep(this, ObjectID.STONE_DOOR_19916, new WorldPoint(2003, 4948, 3), "Use the bronze feather on the door.",
			bronzeFeather);
		useBronzeFeathersOnStoneDoor.addIcon(ItemID.BRONZE_FEATHER);
		useSilverFeathersOnStoneDoor = new ObjectStep(this, ObjectID.STONE_DOOR_19918, new WorldPoint(2003, 4948, 3), "Use the silver feather on the door.",
			silverFeather);
		useSilverFeathersOnStoneDoor.addIcon(ItemID.SILVER_FEATHER);
		useGoldFeathersOnStoneDoor = new ObjectStep(this, ObjectID.STONE_DOOR_19917, new WorldPoint(2003, 4948, 3), "Use the golden feather on the door.",
			goldFeather);
		useGoldFeathersOnStoneDoor.addIcon(ItemID.GOLDEN_FEATHER);
		useBronzeSilverFeathersOnStoneDoor = new ObjectStep(this, ObjectID.STONE_DOOR_19916, new WorldPoint(2003, 4948, 3), "Use the bronze and silver feathers on the door.",
			silverFeather, bronzeFeather);
		useBronzeSilverFeathersOnStoneDoor.addIcon(ItemID.SILVER_FEATHER);
		useGoldBronzeFeathersOnStoneDoor = new ObjectStep(this, ObjectID.STONE_DOOR_19916, new WorldPoint(2003, 4948, 3), "Use the bronze and golden feathers on the door.",
			goldFeather, bronzeFeather);
		useGoldBronzeFeathersOnStoneDoor.addIcon(ItemID.GOLDEN_FEATHER);
		useGoldSilverFeathersOnStoneDoor = new ObjectStep(this, ObjectID.STONE_DOOR_19917, new WorldPoint(2003, 4948, 3), "Use the silver and golden feathers on the door.",
			goldFeather, silverFeather, bronzeFeather);
		useGoldSilverFeathersOnStoneDoor.addIcon(ItemID.GOLDEN_FEATHER);

		sneakPastEagle = new NpcStep(this, NpcID.EAGLE, new WorldPoint(2008, 4955, 3),
			"Go through the feather door and sneak past the Eagle whilst wearing your eagle disguise.",
			fakeBeak, eagleCape);

		speakToNickolaus = new NpcStep(this, NpcID.NICKOLAUS_1485, new WorldPoint(2006, 4960, 3),
			"Speak to Nickolaus.",
			fakeBeak, eagleCape);

		speakToNickolausInTheCamp = new NpcStep(this, NpcID.NICKOLAUS_1485, new WorldPoint(2317, 3504, 0),
			"Speak to Nickolaus in his camp north of Eagles' Peak.");
		speakToNickolausInTheCamp.addDialogStep("Well I was originally sent to find you because of a ferret.");
		speakToNickolausInTheCamp.addDialogStep("That sounds good to me.");

		speakToCharlieAgain = new NpcStep(this, NpcID.CHARLIE_1495, new WorldPoint(2607, 3264, 0),
			"Bring the ferret back to Charlie in Ardougne Zoo.", ferret);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(yellowDye);
		reqs.add(coins);
		reqs.add(tar);
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Start the quest", new ArrayList<>(Arrays.asList(speakToCharlie))));
		allSteps.add(new PanelDetails("Go to Eagles' Peak", new ArrayList<>(Arrays.asList(inspectBooks, clickBook, useFeatherOnDoor))));
		allSteps.add(new PanelDetails("In Eagles' Peak", new ArrayList<>(Arrays.asList(enterPeak, shoutAtNickolaus, pickupFeathers))));
		allSteps.add(new PanelDetails("Make a disguise", new ArrayList<>(Arrays.asList(goToFancyStore, speakAsyffAgain)), yellowDye, coins, tar, tenEagleFeathers));
		allSteps.add(new PanelDetails("Return to Eagles' Peak", new ArrayList<>(Arrays.asList(returnToEaglesPeak)), fakeBeak, eagleCape));
		allSteps.add(new PanelDetails("Get the bronze feather", new ArrayList<>(Arrays.asList(enterBronzeRoom, attemptToTakeBronzeFeather, winch1, grabBronzeFeather))));
		allSteps.add(new PanelDetails("Get the silver feather", new ArrayList<>(Arrays.asList(enterSilverRoom, inspectSilverPedestal, inspectRocks1, inspectRocks2, inspectOpening, threatenKebbit, pickupSilverFeather))));
		allSteps.add(new PanelDetails("Get the golden feather", new ArrayList<>(Arrays.asList(enterGoldRoom, collectFeed, pullLever1Down, fillFeeder1, fillFeeder2, pullLever2Down, pushLever1Up, fillFeeder4, pullLever3Down, fillFeeder5,
			pullLever4Down, fillFeeder6, fillFeeder4Again, grabGoldFeather))));
		allSteps.add(new PanelDetails("Free Nickolaus", new ArrayList<>(Arrays.asList(useFeathersOnStoneDoor, sneakPastEagle, speakToNickolaus))));
		allSteps.add(new PanelDetails("Learn how to catch ferrets", new ArrayList<>(Arrays.asList(speakToNickolausInTheCamp, speakToCharlieAgain))));
		return allSteps;
	}
}
