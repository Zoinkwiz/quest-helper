package com.questhelper.quests.undergroundpass;

import com.google.common.collect.ImmutableMap;
import com.questhelper.*;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.steps.*;
import com.questhelper.steps.conditional.*;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@QuestDescriptor(
        quest = QuestHelperQuest.UNDERGROUND_PASS
)
public class UndergroundPass extends BasicQuestHelper
{

	private static int OBJECTID_ORB_OF_LIGHT1 = 37326;
	private static int OBJECTID_ORB_OF_LIGHT2 = 37325;
	private static int OBJECTID_ORB_OF_LIGHT3 = 37324;
	private static int OBJECTID_ORB_OF_LIGHT4 = 35944;
	private static int OBJECTID_BOULDER = 4543;

	ItemRequirement rope1, rope2, ropeHighlight, bow, arrows, arrowsHighlight, spade, spadeHighlight, plank,
		plankHighlight, bucket, tinderbox, tinderboxHighlight, combatEquipment, food, staminaPotions, coins,
		agilityPotions, telegrabRunes, oilyCloth, oilyClothHighlight, fireArrow, litArrow, litArrowEquipped,
		orb1, orb2, orb3, orb4, railing, railingHighlight, unicornHorn, badgeJerro, badgeCarl, badgeHarry,
		badgeJerroHighlight, badgeCarlHighlight, badgeHarryHighlight, unicornHornHighlight, klanksGauntlets,
		witchsCat, amuletHolthion, amuletHolthionHighlight, amuletDoomion, amuletDoomionHighlight, amuletOthanian,
		amuletOthanianHighlight, dollOfIban, bucketHighlight, brew, klanksGauntletsEquipped, robeTop, robeBottom;

	Zone castleFloor2, westArdougne, beforeRockslide1, beforeRockslide2, beforeRockslide3, beforeBridge,
		northEastOfBridge, westOfBridge, beforeThePit, afterThePit, beforeTheGrid, atTheGrid, afterTheGrid,
		beforeTrap1, beforeTrap2, beforeTrap3, beforeTrap4, beforeTrap5, wellArea, beforePlank2, beforePlank3,
		atOrb1, beforeCellPicklock, insideCell, beforeLedge, afterMaze, afterMazeShortcut, inUnicornArea,
		inUnicornArea2, inKnightsArea, beforeIbansDoor, inDwarfCavern, catArea, inWitchsHouse, beforeTemple,
		inTemple;

	ConditionForStep inCastleFloor2, inWestArdougne, isBeforeRockslide1, isBeforeRockslide2, isBeforeRockslide3,
		isBeforeBridge, isNorthEastOfBridge, haveOilyCloth, haveFireArrow, haveLitArrow, haveLitArrowEquipped,
		havePlank, isBeforeThePit, isAfterThePit, isBeforeTheGrid, isAtTheGrid, isAfterTheGrid, isBeforeTrap1,
		isBeforeTrap2, isBeforeTrap3, isBeforeTrap4, isBeforeTrap5, isInWellArea, isBeforePlank2, isBeforePlank3,
		isAtOrb1, haveOrb1, haveOrb2, haveOrb3, haveOrb4, isBeforeCellPicklock, isInsideCell, isBeforeLedge,
		isAfterMaze, hasRailing, isInUnicornArea, isInUnicornArea2, haveUnicornHorn, isInKnightsArea, haveBadgeJerro,
		haveBadgeCarl, haveBadgeHarry, isBeforeIbansDoor, isInDwarfCavern, haveKlanksGauntlets, isInCatArea,
		haveWitchsCat, isInWitchsHouse, haveAmuletHolthion, haveAmuletDoomion, haveAmuletOthanian, dollImbued,
		haveBrew, pouredBrew, dollAshed, kalragKilled, doveSmeared, isBeforeTemple, haveRobeTop, haveRobeBottom,
		spokeToKoftikAfterTemple;

	QuestStep goToArdougneCastleFloor2, talkToKingLathas, goDownCastleStairs, enterWestArdougne, talkToKoftik,
		enterTheDungeon, climbOverRockslide1, climbOverRockslide2, climbOverRockslide3, talkToKoftikAtBridge,
		useClothOnArrow, lightArrow, walkNorthEastOfBridge, shootBridgeRope, collectPlank,
		crossThePit, climbOverRockslide4, climbOverRockslide5, crossTheGrid, pullLeverAfterGrid, passTrap1,
		passTrap2, passTrap3, passTrap4, passTrap5, plankRock1, plankRock2, plankRock3, collectOrb1, collectOrb2,
		collectOrb3, collectOrb4, orbsToFurnace, climbDownWell, pickCellLock, digMud, crossLedge, navigateMaze,
		goThroughPipe, searchUnicornCage, useRailingOnBoulder, searchUnicornCageAgain, leaveUnicornArea,
		walkToKnights, killJerro, killCarl, killHarry, useBadgeJerroOnWell, useBadgeHarryOnWell, useBadgeCarlOnWell,
		useUnicornHornOnWell, openIbansDoor, descendCave, talkToNiloof, talkToKlank, goBackUpToIbansCavern,
		searchWitchsWindow, pickUpWitchsCat, useCatOnDoor, searchWitchsChest, killHolthion, killDoomion, killOthanian,
		searchDoomionsChest, returnToDwarfs, useBucketOnBrew, useBrewOnTomb, useTinderboxOnTomb, killKalrag,
		ascendToHalfSouless, searchCage, killDisciple, enterTemple, useDollOnWell, talkToKoftikAfterTemple,
		talkToKingLathasAfterTemple;

	private void setupItemReqs()
	{
		rope1 = new ItemRequirement("Rope", ItemID.ROPE);
		rope2 = new ItemRequirement("Rope", ItemID.ROPE, 2);
		ropeHighlight = new ItemRequirement("Rope", ItemID.ROPE);
		ropeHighlight.setHighlightInInventory(true);
		bow = new ItemRequirement("Bow (not crossbow)", ItemCollections.getBows());
		arrows = new ItemRequirement("Arrows (metal, unpoisoned)", ItemCollections.getMetalArrows());
		arrowsHighlight = new ItemRequirement("Arrows (metal, unpoisoned)", ItemCollections.getMetalArrows());
		arrowsHighlight.setHighlightInInventory(true);
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		spadeHighlight = new ItemRequirement("Spade", ItemID.SPADE);
		spadeHighlight.setHighlightInInventory(true);
		plank = new ItemRequirement("Plank", ItemID.PLANK);
		plankHighlight = new ItemRequirement("Plank", ItemID.PLANK);
		plankHighlight.setHighlightInInventory(true);
		bucket = new ItemRequirement("Bucket", ItemID.BUCKET);
		bucketHighlight = new ItemRequirement("Bucket", ItemID.BUCKET);
		bucketHighlight.setHighlightInInventory(true);
		bucketHighlight.setTip("You can grab a bucket from the southwest corner of the large dwarf encampment building.");
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		tinderboxHighlight = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		tinderboxHighlight.setHighlightInInventory(true);
		combatEquipment = new ItemRequirement("Combat Equipment", -1, -1);
		food = new ItemRequirement("Food", -1, -1);
		staminaPotions = new ItemRequirement("Stamina Potions", -1, -1);
		coins = new ItemRequirement("Coins (to buy food, 75 ea)", -1, -1);
		agilityPotions = new ItemRequirement("Agility Potions", -1, -1);
		telegrabRunes = new ItemRequirement("Telegrab Runes", -1, -1);
		oilyCloth = new ItemRequirement("Oily Cloth", ItemID.OILY_CLOTH);
		oilyCloth.setTip("You can get another by searching the equipment by the fireplace beside Koftik.");
		oilyClothHighlight = new ItemRequirement("Oily Cloth", ItemID.OILY_CLOTH);
		oilyClothHighlight.setHighlightInInventory(true);
		fireArrow = new ItemRequirement("Fire Arrow", ItemID.BRONZE_FIRE_ARROW);
		fireArrow.setHighlightInInventory(true);
		fireArrow.addAlternates(ItemID.IRON_FIRE_ARROW, ItemID.STEEL_FIRE_ARROW, ItemID.MITHRIL_FIRE_ARROW, ItemID.ADAMANT_FIRE_ARROW, ItemID.RUNE_FIRE_ARROW);
		litArrow = new ItemRequirement("Lit Arrow", ItemID.BRONZE_FIRE_ARROW_LIT);
		litArrow.setHighlightInInventory(true);
		litArrow.addAlternates(ItemID.IRON_FIRE_ARROW_LIT, ItemID.STEEL_FIRE_ARROW_LIT, ItemID.MITHRIL_FIRE_ARROW_LIT, ItemID.ADAMANT_FIRE_ARROW_LIT, ItemID.RUNE_FIRE_ARROW_LIT);
		litArrowEquipped = new ItemRequirement("Lit Arrow", ItemID.BRONZE_FIRE_ARROW_LIT, 1, true);
		litArrowEquipped.addAlternates(ItemID.IRON_FIRE_ARROW_LIT, ItemID.STEEL_FIRE_ARROW_LIT, ItemID.MITHRIL_FIRE_ARROW_LIT, ItemID.ADAMANT_FIRE_ARROW_LIT, ItemID.RUNE_FIRE_ARROW_LIT);
		orb1 = new ItemRequirement("Orb of light", ItemID.ORB_OF_LIGHT_1484);
		orb2 = new ItemRequirement("Orb of light", ItemID.ORB_OF_LIGHT_1483);
		orb3 = new ItemRequirement("Orb of light", ItemID.ORB_OF_LIGHT_1482);
		orb4 = new ItemRequirement("Orb of light", ItemID.ORB_OF_LIGHT);
		railing = new ItemRequirement("Railing", ItemID.PIECE_OF_RAILING);
		railingHighlight = new ItemRequirement("Railing", ItemID.PIECE_OF_RAILING);
		railingHighlight.setHighlightInInventory(true);
		unicornHorn = new ItemRequirement("Unicorn Horn", ItemID.UNICORN_HORN_1487);
		unicornHornHighlight = new ItemRequirement("Unicorn Horn", ItemID.UNICORN_HORN_1487);
		unicornHornHighlight.setHighlightInInventory(true);
		badgeJerro = new ItemRequirement("Badge (Sir Jerro)", ItemID.PALADINS_BADGE);
		badgeJerroHighlight = new ItemRequirement("Badge (Sir Jerro)", ItemID.PALADINS_BADGE);
		badgeJerroHighlight.setHighlightInInventory(true);
		badgeCarl = new ItemRequirement("Badge (Sir Carl)", ItemID.PALADINS_BADGE_1489);
		badgeCarlHighlight = new ItemRequirement("Badge (Sir Carl)", ItemID.PALADINS_BADGE_1489);
		badgeCarlHighlight.setHighlightInInventory(true);
		badgeHarry = new ItemRequirement("Badge (Sir Harry)", ItemID.PALADINS_BADGE_1490);
		badgeHarryHighlight = new ItemRequirement("Badge (Sir Harry)", ItemID.PALADINS_BADGE_1490);
		badgeHarryHighlight.setHighlightInInventory(true);
		klanksGauntlets = new ItemRequirement("Klank's gauntlets", ItemID.KLANKS_GAUNTLETS);
		klanksGauntletsEquipped = new ItemRequirement("Klank's gauntlets", ItemID.KLANKS_GAUNTLETS, 1, true);
		witchsCat = new ItemRequirement("Witch's Cat", ItemID.WITCHS_CAT);
		witchsCat.setHighlightInInventory(true);
		amuletHolthion = new ItemRequirement("Holthion's Amulet", ItemID.AMULET_OF_HOLTHION);
		amuletHolthionHighlight = new ItemRequirement("Holthion's Amulet", ItemID.AMULET_OF_HOLTHION);
		amuletHolthionHighlight.setHighlightInInventory(true);
		amuletDoomion = new ItemRequirement("Doomion's Amulet", ItemID.AMULET_OF_DOOMION);
		amuletDoomionHighlight = new ItemRequirement("Doomion's Amulet", ItemID.AMULET_OF_DOOMION);
		amuletDoomionHighlight.setHighlightInInventory(true);
		amuletOthanian = new ItemRequirement("Othanian's Amulet", ItemID.AMULET_OF_OTHANIAN);
		amuletOthanianHighlight = new ItemRequirement("Othanian's Amulet", ItemID.AMULET_OF_OTHANIAN);
		amuletOthanianHighlight.setHighlightInInventory(true);
		dollOfIban = new ItemRequirement("Doll of Iban", ItemID.DOLL_OF_IBAN);
		brew = new ItemRequirement("Dwarf brew", ItemID.DWARF_BREW);
		brew.setHighlightInInventory(true);
		robeTop = new ItemRequirement("Zamorak monk top", ItemID.ZAMORAK_MONK_TOP, 1, true);
		robeBottom = new ItemRequirement("Zamorak monk bottom", ItemID.ZAMORAK_MONK_TOP, 1, true);
	}

	private void setupZones()
	{
		castleFloor2 = new Zone(new WorldPoint(2568, 3283, 1), new WorldPoint(2591, 3310, 1));
		westArdougne = new Zone(new WorldPoint(2433, 3264, 0), new WorldPoint(2557, 3337, 2));
		beforeRockslide1 = new Zone(new WorldPoint(2473, 9713, 0), new WorldPoint(2500, 9720, 0));
		beforeRockslide2 = new Zone(new WorldPoint(2471, 9705, 0), new WorldPoint(2481, 9712, 0));
		beforeRockslide3 = new Zone(new WorldPoint(2457, 9704, 0), new WorldPoint(2470, 9711, 0));
		beforeBridge = new Zone(new WorldPoint(2445, 9713, 0), new WorldPoint(2461, 9719, 0));
		northEastOfBridge = new Zone(new WorldPoint(2445, 9719, 0), new WorldPoint(2456, 9727, 0));
		westOfBridge = new Zone(new WorldPoint(2431, 9684, 0), new WorldPoint(2443, 9728, 0));
		beforeThePit = new Zone(new WorldPoint(2439, 9691, 0), new WorldPoint(2463, 9703, 0));
		afterThePit = new Zone(new WorldPoint(2463, 9691, 0), new WorldPoint(2493, 9709, 0));
		beforeTheGrid = new Zone(new WorldPoint(2482, 9668, 0), new WorldPoint(2494, 9690, 0));
		atTheGrid = new Zone(new WorldPoint(2467, 9672, 0), new WorldPoint(2482, 9684, 0));
		afterTheGrid = new Zone(new WorldPoint(2466, 9672, 0), new WorldPoint(2466, 9683, 0));
		beforeTrap1 = new Zone(new WorldPoint(2444, 9668, 0), new WorldPoint(2465, 9690, 0));
		beforeTrap2 = new Zone(new WorldPoint(2441, 9676, 0), new WorldPoint(2442, 9678, 0));
		beforeTrap3 = new Zone(new WorldPoint(2436, 9675, 0), new WorldPoint(2439, 9683, 0));
		beforeTrap4 = new Zone(new WorldPoint(2433, 9675, 0), new WorldPoint(2434, 9677, 0));
		beforeTrap5 = new Zone(new WorldPoint(2431, 9675, 0), new WorldPoint(2431, 9677, 0));
		wellArea = new Zone(new WorldPoint(2409, 9663, 0), new WorldPoint(2430, 9680, 0));
		beforePlank2 = new Zone(new WorldPoint(2416, 9682, 0), new WorldPoint(2420, 9684, 0));
		beforePlank3 = new Zone(new WorldPoint(2414, 9686, 0), new WorldPoint(2420, 9688, 0));
		atOrb1 = new Zone(new WorldPoint(2412, 9690, 0), new WorldPoint(2422, 9698, 0));
		beforeCellPicklock = new Zone(new WorldPoint(2391, 9648, 0), new WorldPoint(2433, 9663, 0));
		insideCell = new Zone(new WorldPoint(2392, 9650, 0), new WorldPoint(2394, 9654, 0));
		beforeLedge = new Zone(new WorldPoint(2374, 9642, 0), new WorldPoint(2395, 9647, 0));
		afterMaze = new Zone(new WorldPoint(2416, 9597, 0), new WorldPoint(2432, 9640, 0));
		afterMazeShortcut = new Zone(new WorldPoint(2404, 9615, 0), new WorldPoint(2420, 9624, 0));
		inUnicornArea = new Zone(new WorldPoint(2391, 9586, 0), new WorldPoint(2415, 9612, 0));
		inUnicornArea2 = new Zone(new WorldPoint(2366, 9586, 0), new WorldPoint(2390, 9612, 0));
		inKnightsArea = new Zone(new WorldPoint(2420, 9714, 0), new WorldPoint(2429, 9728, 0));
		beforeIbansDoor = new Zone(new WorldPoint(2367, 9709, 0), new WorldPoint(2386, 9727, 0));
		inDwarfCavern = new Zone(new WorldPoint(2304, 9789, 0), new WorldPoint(2365, 9921, 0));
		catArea = new Zone(new WorldPoint(2121, 4581, 1), new WorldPoint(2147, 4606, 1));
		inWitchsHouse = new Zone(new WorldPoint(2157, 4565, 1), new WorldPoint(2157, 4566, 1));
		beforeTemple = new Zone(new WorldPoint(2142, 4629, 1), new WorldPoint(2164, 4662, 1));
		inTemple = new Zone(new WorldPoint(2131, 4641, 1), new WorldPoint(2143, 4654, 1));
	}

	private void setupConditions()
	{
		// Started quest:
		// 9139, 9140, 9141, 9142, 9143 = 1
		// Talked to koftik
		// 9141 = 0
		inCastleFloor2 = new ZoneCondition(castleFloor2);
		inWestArdougne = new ZoneCondition(westArdougne);
		isBeforeRockslide1 = new ZoneCondition(beforeRockslide1);
		isBeforeRockslide2 = new ZoneCondition(beforeRockslide2);
		isBeforeRockslide3 = new ZoneCondition(beforeRockslide3);
		isBeforeBridge = new ZoneCondition(beforeBridge);
		isNorthEastOfBridge = new ZoneCondition(northEastOfBridge);
		haveOilyCloth = new ItemRequirementCondition(oilyCloth);
		haveFireArrow = new ItemRequirementCondition(fireArrow);
		haveLitArrow = new ItemRequirementCondition(litArrow);
		haveLitArrowEquipped = new ItemRequirementCondition(litArrowEquipped);
		havePlank = new ItemRequirementCondition(plank);
		isBeforeThePit = new ZoneCondition(westOfBridge, beforeThePit);
		isAfterThePit = new ZoneCondition(afterThePit);
		isBeforeTheGrid = new ZoneCondition(beforeTheGrid);
		isAtTheGrid = new ZoneCondition(atTheGrid);
		isAfterTheGrid = new ZoneCondition(afterTheGrid);
		isBeforeTrap1 = new ZoneCondition(beforeTrap1);
		isBeforeTrap2 = new ZoneCondition(beforeTrap2);
		isBeforeTrap3 = new ZoneCondition(beforeTrap3);
		isBeforeTrap4 = new ZoneCondition(beforeTrap4);
		isBeforeTrap5 = new ZoneCondition(beforeTrap5);
		haveOrb1 = new ItemRequirementCondition(orb1);
		haveOrb2 = new ItemRequirementCondition(orb2);
		haveOrb3 = new ItemRequirementCondition(orb3);
		haveOrb4 = new ItemRequirementCondition(orb4);
		isInWellArea = new ZoneCondition(wellArea);
		isBeforePlank2 = new ZoneCondition(beforePlank2);
		isBeforePlank3 = new ZoneCondition(beforePlank3);
		isAtOrb1 = new ZoneCondition(atOrb1);
		isBeforeCellPicklock = new ZoneCondition(beforeCellPicklock);
		isInsideCell = new ZoneCondition(insideCell);
		isBeforeLedge = new ZoneCondition(beforeLedge);
		isAfterMaze = new ZoneCondition(afterMaze, afterMazeShortcut);
		hasRailing = new ItemRequirementCondition(railing);
		isInUnicornArea = new ZoneCondition(inUnicornArea);
		isInUnicornArea2 = new ZoneCondition(inUnicornArea2);
		haveUnicornHorn = new ItemRequirementCondition(unicornHorn);
		isInKnightsArea = new ZoneCondition(inKnightsArea);
		haveBadgeCarl = new ItemRequirementCondition(badgeCarl);
		haveBadgeHarry = new ItemRequirementCondition(badgeHarry);
		haveBadgeJerro = new ItemRequirementCondition(badgeJerro);
		isBeforeIbansDoor = new ZoneCondition(beforeIbansDoor);
		isInDwarfCavern = new ZoneCondition(inDwarfCavern);
		haveKlanksGauntlets = new ItemRequirementCondition(klanksGauntlets);
		isInCatArea = new ZoneCondition(catArea);
		haveWitchsCat = new ItemRequirementCondition(witchsCat);
		isInWitchsHouse = new ZoneCondition(inWitchsHouse);
		haveAmuletHolthion = new ItemRequirementCondition(amuletHolthion);
		haveAmuletDoomion = new ItemRequirementCondition(amuletDoomion);
		haveAmuletOthanian = new ItemRequirementCondition(amuletOthanian);
		dollImbued = new VarbitCondition(9118, 1); // todo confirm this varbit is set when liquid is poured on doll
		haveBrew = new ItemRequirementCondition(brew);
		pouredBrew = new VarbitCondition(9134, 1); // todo confirm this varbit is set when pouring brew on tomb
		dollAshed = new VarbitCondition(9117, 1); // todo confirm this varbit is set when ashes are rubbed on the doll after lighting the tomb
		kalragKilled = new VarbitCondition(9115, 1); // todo confirm this varbit is set when kalrag the spider is killed
		doveSmeared = new VarbitCondition(9116, 1); // todo confirm this varbit is set when kalrag the spider is killed
		isBeforeTemple = new ZoneCondition(beforeTemple);
		haveRobeTop = new ItemRequirementCondition(robeTop);
		haveRobeBottom = new ItemRequirementCondition(robeBottom);
		spokeToKoftikAfterTemple = new VarbitCondition(9143, 1);
	}

	public void setupSteps()
	{
		goToArdougneCastleFloor2 = new ObjectStep(this, ObjectID.STAIRCASE_15645, new WorldPoint(2572, 3296, 0), "Go to the second floor of Ardougne Castle.");
		talkToKingLathas = new NpcStep(this, NpcID.KING_LATHAS_9005, new WorldPoint(2578, 3293, 1), "Talk to King Lathas.");
		talkToKingLathas.addDialogSteps("I am. What's our plan?", "I'm ready.", "Yes.");
		talkToKingLathas.addSubSteps(goToArdougneCastleFloor2);

		goDownCastleStairs = new ObjectStep(this, ObjectID.STAIRCASE_15648, new WorldPoint(2572, 3296, 1), "Talk to Koftik in West Ardougne.");
		enterWestArdougne = new ObjectStep(this, ObjectID.ARDOUGNE_WALL_DOOR_8739, new WorldPoint(2558, 3300, 0), "Talk to Koftik in West Ardougne.");
		talkToKoftik = new NpcStep(this, NpcID.KOFTIK_8976, new WorldPoint(2435, 3314, 0), "Talk to Koftik outside the West Ardougne cave.");
		talkToKoftik.addDialogStep("I'll take my chances.");
		talkToKoftik.addSubSteps(goDownCastleStairs, enterWestArdougne);

		enterTheDungeon = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_3213, new WorldPoint(2434, 3315, 0), "Enter the dungeon.");
		climbOverRockslide1 = new ObjectStep(this, ObjectID.ROCKSLIDE, new WorldPoint(2480, 9713, 0), "Climb-over rockslide.");
		climbOverRockslide2 = new ObjectStep(this, ObjectID.ROCKSLIDE, new WorldPoint(2471, 9706, 0), "Climb-over rockslide.");
		climbOverRockslide3 = new ObjectStep(this, ObjectID.ROCKSLIDE, new WorldPoint(2458, 9712, 0), "Climb-over rockslide.");
		talkToKoftikAtBridge = new NpcStep(this, NpcID.KOFTIK_8976, "Talk to Koftik inside the cave.");
		talkToKoftikAtBridge.addSubSteps(enterTheDungeon, climbOverRockslide1, climbOverRockslide2, climbOverRockslide3);

		useClothOnArrow = new DetailedQuestStep(this, "Use the oily cloth on an arrow.", oilyClothHighlight, arrowsHighlight);
		lightArrow = new DetailedQuestStep(this, "Use the tinderbox on the fire arrow.", fireArrow, tinderboxHighlight);
		walkNorthEastOfBridge = new TileStep(this, new WorldPoint(2447, 9722, 0), "Walk to the room north of Koftik.", litArrowEquipped);
		shootBridgeRope = new ObjectStep(this, ObjectID.GUIDE_ROPE, "Wield a lit arrow and shoot the guide-rope.", litArrowEquipped);
		shootBridgeRope.addSubSteps(useClothOnArrow, lightArrow, walkNorthEastOfBridge);

		collectPlank = new ItemStep(this, "Pick up the plank in the north room.");
		crossThePit = new ObjectStep(this, ObjectID.ROCK_23125, "Swing across the pit with a rope.", ropeHighlight);
		crossThePit.addSubSteps(collectPlank);
		climbOverRockslide4 = new ObjectStep(this, ObjectID.ROCKSLIDE, new WorldPoint(2491, 9691, 0), "Climb-over rockslide");
		climbOverRockslide5 = new ObjectStep(this, ObjectID.ROCKSLIDE, new WorldPoint(2482, 9679, 0), "Climb-over rockslide");
		crossTheGrid = new DetailedQuestStep(this, "Cross the grid by trail and error. Keep health above 15, and remember the path for Regicide.");
		pullLeverAfterGrid = new ObjectStep(this, ObjectID.LEVER_3337, "Pull the lever to raise the gate.");
		crossTheGrid.addSubSteps(climbOverRockslide4, climbOverRockslide5, pullLeverAfterGrid);

		passTrap1 = new ObjectStep(this, ObjectID.ODD_MARKINGS, new WorldPoint(2443, 9678, 0), "Either disable or walk past the traps.");
		passTrap2 = new ObjectStep(this, ObjectID.ODD_MARKINGS, new WorldPoint(2440, 9678, 0), "Either disable or walk past the traps.");
		passTrap3 = new ObjectStep(this, ObjectID.ODD_MARKINGS, new WorldPoint(2435, 9675, 0), "Either disable or walk past the traps.");
		passTrap4 = new ObjectStep(this, ObjectID.ODD_MARKINGS, new WorldPoint(2432, 9675, 0), "Either disable or walk past the traps.");
		passTrap5 = new ObjectStep(this, ObjectID.ODD_MARKINGS, new WorldPoint(2430, 9675, 0), "Either disable or walk past the traps.");
		passTrap1.addSubSteps(passTrap2, passTrap3, passTrap4, passTrap5);

		collectOrb1 = new ObjectStep(this, OBJECTID_ORB_OF_LIGHT1, "Take the first orb of light.");
		plankRock1 = new ObjectStep(this, ObjectID.FLAT_ROCK, new WorldPoint(2418, 9681, 0), "Use a plank on the flat rock to cross it.");
		plankRock2 = new ObjectStep(this, ObjectID.FLAT_ROCK, new WorldPoint(2418, 9685, 0), "Use a plank on the flat rock to cross it.");
		plankRock3 = new ObjectStep(this, ObjectID.FLAT_ROCK, new WorldPoint(2416, 9689, 0), "Use a plank on the flat rock to cross it.");
		collectOrb1.addSubSteps(plankRock1, plankRock2, plankRock3);

		collectOrb2 = new ObjectStep(this, OBJECTID_ORB_OF_LIGHT2, "Take the second orb of light.");
		collectOrb3 = new ObjectStep(this, OBJECTID_ORB_OF_LIGHT3, "Take the third orb of light. Remember to use a plank on the flat rocks to cross them.");
		collectOrb4 = new ObjectStep(this, ObjectID.FLAT_ROCK_3339, new WorldPoint(2382, 9668, 0), "Search the flat rock to remove the trap and take the fourth orb.");
		orbsToFurnace = new ObjectStep(this, ObjectID.FURNACE_3294, new WorldPoint(2455, 9683, 0), "Return to the furnace to the east, and use the orbs on it.");
		climbDownWell = new ObjectStep(this, ObjectID.WELL_3264, new WorldPoint(2416, 9675, 0), "Climb down the well.");

		navigateMaze = new DetailedQuestStep(this, "Navigate the maze, or use the shortcut to the south with 50 Thieving.");
		pickCellLock = new ObjectStep(this, ObjectID.GATE_3266, new WorldPoint(2393, 9655, 0), "Pick the lock to enter the cell.");
		digMud = new ObjectStep(this, ObjectID.LOOSE_MUD, new WorldPoint(2393, 9650, 0), "Dig the loose mud.", spadeHighlight);
		crossLedge = new ObjectStep(this, ObjectID.LEDGE_3238, new WorldPoint(2374, 9644, 0), "Cross the ledge.");
		goThroughPipe = new ObjectStep(this, ObjectID.PIPE_3237, new WorldPoint(2418, 9605, 0), "Enter the pipe to the next section.");
		navigateMaze.addSubSteps(pickCellLock, digMud, crossLedge, goThroughPipe);

		searchUnicornCage = new ObjectStep(this, ObjectID.CAGE, new WorldPoint(2398, 9605, 0), "Search the unicorn cage.");
		useRailingOnBoulder = new ObjectStep(this, OBJECTID_BOULDER, new WorldPoint(2397, 9595, 0), "Use the railing on the boulder.", railingHighlight);

		searchUnicornCageAgain = new ObjectStep(this, ObjectID.SMASHED_CAGE, "Search the cage again.");
		leaveUnicornArea = new ObjectStep(this, ObjectID.TUNNEL_3219, "Enter the tunnel.");
		walkToKnights = new TileStep(this, new WorldPoint(2424, 9715, 0), "Travel through the tunnel to find some knights.");
		walkToKnights.addSubSteps(leaveUnicornArea);

		killJerro = new NpcStep(this, NpcID.SIR_JERRO, "Kill Sir Jerro and take his badge. Optionally: talk to one of the knights first to receive food and potions.");
		killCarl = new NpcStep(this, NpcID.SIR_CARL, "Kill Sir Carl and take his badge.");
		killHarry = new NpcStep(this, NpcID.SIR_HARRY, "Kill Sir Harry and take his badge.");

		useBadgeJerroOnWell = new ObjectStep(this, ObjectID.WELL_3305, new WorldPoint(2373, 9718, 0), "Use the badges and unicorn horn on the well.", badgeJerroHighlight);
		useBadgeHarryOnWell = new ObjectStep(this, ObjectID.WELL_3305, new WorldPoint(2373, 9718, 0), "Use the badges and unicorn horn on the well.", badgeHarryHighlight);
		useBadgeCarlOnWell = new ObjectStep(this, ObjectID.WELL_3305, new WorldPoint(2373, 9718, 0), "Use the badges and unicorn horn on the well.", badgeCarlHighlight);
		useUnicornHornOnWell = new ObjectStep(this, ObjectID.WELL_3305, new WorldPoint(2373, 9718, 0), "Use the badges and unicorn horn on the well.", unicornHornHighlight);
		useBadgeJerroOnWell.addSubSteps(useBadgeHarryOnWell, useBadgeCarlOnWell, useUnicornHornOnWell);
		openIbansDoor = new ObjectStep(this, ObjectID.DOOR_3221, "Open the door.");

		descendCave = new ObjectStep(this, ObjectID.CAVE_3222, new WorldPoint(2150, 4545, 1), "Follow the edge of the cavern down to enter a cave on the south wall.");
		talkToNiloof = new NpcStep(this, NpcID.NILOOF, new WorldPoint(2313, 9806, 0), "Talk to Niloof.");
		talkToNiloof.addSubSteps(descendCave);

		talkToKlank = new NpcStep(this, NpcID.KLANK, "Talk to Klank.");
		talkToKlank.addDialogStep("What happened to them?");

		goBackUpToIbansCavern = new ObjectStep(this, ObjectID.CAVE_3223, new WorldPoint(2336, 9793, 0), "Return to Iban's Cavern.");
		searchWitchsWindow = new ObjectStep(this, ObjectID.WINDOW_3362, "Search the witch's window from OUTSIDE the house.");
		searchWitchsWindow.addSubSteps(goBackUpToIbansCavern);
		pickUpWitchsCat = new NpcStep(this, NpcID.WITCHS_CAT, new WorldPoint(2131, 4602, 1), "Go northwest along the bridges and retrieve the witch's cat.");
		useCatOnDoor = new ObjectStep(this, ObjectID.DOOR_3270, new WorldPoint(2158, 4566, 1), "Use the cat on the witch's door to distract her.", witchsCat);
		searchWitchsChest = new ObjectStep(this, ObjectID.CHEST, new WorldPoint(2157, 4564, 1), "Search the chest in the witch's house. You'll need 4 empty inventory slots.");

		killHolthion = new NpcStep(this, NpcID.HOLTHION, new WorldPoint(2133, 4555, 1), "Kill Holthion and pick up his amulet.");
		killDoomion = new NpcStep(this, NpcID.DOOMION, new WorldPoint(2135, 4566, 1), "Kill Doomion and pick up his amulet.");
		killOthanian = new NpcStep(this, NpcID.OTHAINIAN, new WorldPoint(2123, 4563, 1), "Kill Othanian and pick up his amulet.");
		searchDoomionsChest = new ObjectStep(this, ObjectID.CHEST_3274, new WorldPoint(2136, 4578, 1), "Search the chest north of Doomion.");
		returnToDwarfs = new ObjectStep(this, ObjectID.CAVE_3222, new WorldPoint(2150, 4545, 1), "Return to the dwarf encampment on the lower level.");
		useBucketOnBrew = new ObjectStep(this, ObjectID.BREW_BARREL, new WorldPoint(2327, 9799, 0), "Use a bucket on the brew barrel.", bucketHighlight);
		useBrewOnTomb = new ObjectStep(this, ObjectID.TOMB, new WorldPoint(2357, 9803, 0), "Use the bucket of brew on the tomb.", brew);
		useTinderboxOnTomb = new ObjectStep(this, ObjectID.TOMB, new WorldPoint(2357, 9803, 0), "Light the tomb with a tinderbox.", tinderboxHighlight);
		killKalrag = new NpcStep(this, NpcID.KALRAG, new WorldPoint(2356, 9913, 0), "Kill Kalrag the spider. Protect From Melee can keep you safe in this fight.");
		ascendToHalfSouless = new ObjectStep(this, ObjectID.CAVE_3223, new WorldPoint(2304, 9915, 0), "Ascend to the upper level of the cave again.");
		searchCage = new ObjectStep(this, ObjectID.CAGE_3351, new WorldPoint(2135, 4702, 1), "Search the marked cage while wearing Klank's gauntlets.");
		searchCage.addSubSteps(ascendToHalfSouless);

		killDisciple = new NpcStep(this, NpcID.DISCIPLE_OF_IBAN, new WorldPoint(2163, 4648, 1), "Kill a disciple of Iban and take their robe.");
		enterTemple = new ObjectStep(this, ObjectID.DOOR_3333, new WorldPoint(2144, 4648, 1), "Enter the Temple of Iban wearing monk robes. Prepare for the following steps before entering, as Iban can deal lots of damage quickly.", robeTop, robeBottom);
		useDollOnWell = new ObjectStep(this, ObjectID.WELL_3359, new WorldPoint(2137, 4648, 1), "Right-click USE the doll on the well. Standing north of the well can prevent Iban from hitting.");

		talkToKoftikAfterTemple = new NpcStep(this, NpcID.KOFTIK_8976, new WorldPoint(2444, 9607, 0), "Talk to Koftik.");
		talkToKingLathasAfterTemple = new NpcStep(this, NpcID.KING_LATHAS_9005, new WorldPoint(2578, 3293, 1), "Return to King Lathas to finish the quest.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemReqs();
		setupZones();
		setupConditions();
		setupSteps();

		ConditionalStep beginWithLathas = new ConditionalStep(this, goToArdougneCastleFloor2);
		beginWithLathas.addStep(inCastleFloor2, talkToKingLathas);

		ConditionalStep startDungeon = new ConditionalStep(this, enterWestArdougne);
		startDungeon.addStep(inCastleFloor2, goDownCastleStairs);
		startDungeon.addStep(inWestArdougne, talkToKoftik);

		ConditionalStep crossTheBridge = new ConditionalStep(this, enterTheDungeon);
		crossTheBridge.addStep(isBeforeRockslide1, climbOverRockslide1);
		crossTheBridge.addStep(isBeforeRockslide2, climbOverRockslide2);
		crossTheBridge.addStep(isBeforeRockslide3, climbOverRockslide3);
		crossTheBridge.addStep(new Conditions(isNorthEastOfBridge, haveLitArrowEquipped), shootBridgeRope);
		crossTheBridge.addStep(new Conditions(new Conditions(LogicType.OR, isBeforeBridge, isNorthEastOfBridge), haveLitArrow), walkNorthEastOfBridge);
		crossTheBridge.addStep(new Conditions(new Conditions(LogicType.OR, isBeforeBridge, isNorthEastOfBridge), haveFireArrow), lightArrow);
		crossTheBridge.addStep(new Conditions(new Conditions(LogicType.OR, isBeforeBridge, isNorthEastOfBridge), haveOilyCloth), useClothOnArrow);

		ConditionalStep theUndergroundPass = new ConditionalStep(this, climbDownWell);
		theUndergroundPass.addStep(new Conditions(LogicType.NOR, havePlank), collectPlank);
		theUndergroundPass.addStep(isBeforeThePit, crossThePit);
		theUndergroundPass.addStep(isAfterThePit, climbOverRockslide4);
		theUndergroundPass.addStep(isBeforeTheGrid, climbOverRockslide5);
		theUndergroundPass.addStep(isAtTheGrid, crossTheGrid);
		theUndergroundPass.addStep(isAfterTheGrid, pullLeverAfterGrid);
		theUndergroundPass.addStep(new Conditions(haveOrb1, haveOrb2, haveOrb3, haveOrb4), orbsToFurnace);
		theUndergroundPass.addStep(new Conditions(new Conditions(LogicType.NOR, haveOrb1), isInWellArea), plankRock1);
		theUndergroundPass.addStep(new Conditions(new Conditions(LogicType.NOR, haveOrb1), isBeforePlank2), plankRock2);
		theUndergroundPass.addStep(new Conditions(new Conditions(LogicType.NOR, haveOrb1), isBeforePlank3), plankRock3);
		theUndergroundPass.addStep(new Conditions(new Conditions(LogicType.NOR, haveOrb1), isAtOrb1), collectOrb1);
		theUndergroundPass.addStep(haveOrb1, collectOrb2);
		theUndergroundPass.addStep(haveOrb2, collectOrb3);
		theUndergroundPass.addStep(haveOrb3, collectOrb4);
		theUndergroundPass.addStep(isBeforeTrap1, passTrap1);
		theUndergroundPass.addStep(isBeforeTrap2, passTrap2);
		theUndergroundPass.addStep(isBeforeTrap3, passTrap3);
		theUndergroundPass.addStep(isBeforeTrap4, passTrap4);
		theUndergroundPass.addStep(isBeforeTrap5, passTrap5);

		ConditionalStep descendingDeeper = new ConditionalStep(this, navigateMaze);
		descendingDeeper.addStep(isBeforeCellPicklock, pickCellLock);
		descendingDeeper.addStep(isInsideCell, digMud);
		descendingDeeper.addStep(isBeforeLedge, crossLedge);
		descendingDeeper.addStep(isAfterMaze, goThroughPipe);
		descendingDeeper.addStep(new Conditions(new Conditions(LogicType.NOR, hasRailing), isInUnicornArea), searchUnicornCage);
		descendingDeeper.addStep(new Conditions(isInUnicornArea, hasRailing), useRailingOnBoulder);

		ConditionalStep killedAUnicorn = new ConditionalStep(this, walkToKnights);
		descendingDeeper.addStep(new Conditions(new Conditions(LogicType.NOR, haveUnicornHorn), isInUnicornArea2), searchUnicornCageAgain);
		descendingDeeper.addStep(new Conditions(haveUnicornHorn, isInUnicornArea2), leaveUnicornArea);
		descendingDeeper.addStep(new Conditions(isInKnightsArea, new Conditions(LogicType.NOR, haveBadgeJerro)), killJerro);
		descendingDeeper.addStep(new Conditions(isInKnightsArea, new Conditions(LogicType.NOR, haveBadgeCarl)), killCarl);
		descendingDeeper.addStep(new Conditions(isInKnightsArea, new Conditions(LogicType.NOR, haveBadgeHarry)), killHarry);
		descendingDeeper.addStep(new Conditions(isBeforeIbansDoor, haveBadgeJerro), useBadgeJerroOnWell);
		descendingDeeper.addStep(new Conditions(isBeforeIbansDoor, haveBadgeHarry), useBadgeHarryOnWell);
		descendingDeeper.addStep(new Conditions(isBeforeIbansDoor, haveBadgeCarl), useBadgeCarlOnWell);
		descendingDeeper.addStep(new Conditions(isBeforeIbansDoor, haveUnicornHorn), useUnicornHornOnWell);
		descendingDeeper.addStep(isBeforeIbansDoor, openIbansDoor);
		descendingDeeper.addStep(new Conditions(haveBadgeJerro, haveBadgeHarry, haveBadgeCarl, new Conditions(LogicType.NOR, isBeforeIbansDoor)), useBadgeJerroOnWell);

		ConditionalStep talkingToNiloof = new ConditionalStep(this, descendCave);
		talkingToNiloof.addStep(isInDwarfCavern, talkToNiloof);

		ConditionalStep theWitch = new ConditionalStep(this, searchWitchsWindow);
		theWitch.addStep(new Conditions(isInDwarfCavern, haveKlanksGauntlets), goBackUpToIbansCavern);
		theWitch.addStep(new Conditions(isInDwarfCavern, new Conditions(LogicType.NOR, haveKlanksGauntlets)), talkToKlank);
		theWitch.addStep(isInCatArea, pickUpWitchsCat); // todo find a way to detect that we've searched the window
		theWitch.addStep(haveWitchsCat, useCatOnDoor);
		theWitch.addStep(isInWitchsHouse, searchWitchsChest); // todo find a way to detect we've used cat on door

		ConditionalStep imbuingTheDoll = new ConditionalStep(this, searchCage);
		imbuingTheDoll.addStep(new Conditions(isBeforeTemple, doveSmeared), enterTemple);
		imbuingTheDoll.addStep(doveSmeared, killDisciple);
		imbuingTheDoll.addStep(new Conditions(kalragKilled, isInDwarfCavern), ascendToHalfSouless);
		imbuingTheDoll.addStep(kalragKilled, searchCage);
		imbuingTheDoll.addStep(dollAshed, killKalrag);
		imbuingTheDoll.addStep(pouredBrew, useTinderboxOnTomb);
		imbuingTheDoll.addStep(haveBrew, useBrewOnTomb);
		imbuingTheDoll.addStep(new Conditions(dollImbued, isInDwarfCavern), useBucketOnBrew);
		imbuingTheDoll.addStep(dollImbued, returnToDwarfs);
		imbuingTheDoll.addStep(new Conditions(LogicType.NOR, haveAmuletHolthion), killHolthion);
		imbuingTheDoll.addStep(new Conditions(LogicType.NOR, haveAmuletDoomion), killDoomion);
		imbuingTheDoll.addStep(new Conditions(LogicType.NOR, haveAmuletOthanian), killOthanian);
		imbuingTheDoll.addStep(new Conditions(haveAmuletHolthion, haveAmuletDoomion, haveAmuletOthanian), searchDoomionsChest);

		ConditionalStep wrappingUp = new ConditionalStep(this, talkToKoftikAfterTemple);
		wrappingUp.addStep(spokeToKoftikAfterTemple, talkToKingLathasAfterTemple);

		return new ImmutableMap.Builder<Integer, QuestStep>()
			.put(0, beginWithLathas)
			.put(1, startDungeon)
			.put(2, crossTheBridge)
			.put(3, theUndergroundPass)
			.put(4, descendingDeeper)
			.put(5, killedAUnicorn)
			.put(6, talkingToNiloof)
			.put(7, theWitch)
			.put(8, imbuingTheDoll)
			.put(9, useDollOnWell)
			.put(10, wrappingUp)
			.build();
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(rope1);
		reqs.add(bow);
		reqs.add(arrows);
		reqs.add(spade);
		reqs.add(plank);
		reqs.add(bucket);
		reqs.add(tinderbox);
		reqs.add(combatEquipment);
		return reqs;
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(rope2);
		reqs.add(food);
		reqs.add(staminaPotions);
		reqs.add(coins);
		reqs.add(agilityPotions);
		reqs.add(telegrabRunes);
		return reqs;
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("3 Demons (level 91, safespottable)");
		reqs.add("3 Paladins (level 62, safespottable)");
		reqs.add("Kalrag (level 89)");
		reqs.add("Disciple of Iban (level 13)");
		return reqs;
	}

	@Override
	public ArrayList<String> getNotes()
	{
		ArrayList<String> notes = new ArrayList<>();
		notes.add("Kalrag attacks with melee only, so Protect From Melee can keep you safe in that fight.");
		notes.add("Using the bow provided for combat can save inventory space.");
		notes.add("Bring all items with you to start the quest, as leaving the dungeon will require you to restart from the beginning.");
		return notes;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting out", new ArrayList<>(Arrays.asList(talkToKingLathas, talkToKoftik))));
		allSteps.add(new PanelDetails("The Underground Pass", new ArrayList<>(Arrays.asList(talkToKoftikAtBridge, shootBridgeRope, crossThePit, crossTheGrid, passTrap1, collectOrb1, collectOrb2, collectOrb3, collectOrb4, orbsToFurnace, climbDownWell))));
		allSteps.add(new PanelDetails("Descending Deeper", new ArrayList<>(Arrays.asList(navigateMaze, searchUnicornCage, useRailingOnBoulder))));
		allSteps.add(new PanelDetails("Cold-blooded Killing", new ArrayList<>(Arrays.asList(searchUnicornCageAgain, walkToKnights, killJerro, killHarry, killCarl, useBadgeJerroOnWell, openIbansDoor))));
		allSteps.add(new PanelDetails("The Witch Kardia", new ArrayList<>(Arrays.asList(talkToNiloof, searchWitchsWindow, pickUpWitchsCat, useCatOnDoor, searchWitchsChest))));
		allSteps.add(new PanelDetails("Imbuing the Doll", new ArrayList<>(Arrays.asList(killHolthion, killDoomion, killOthanian, searchDoomionsChest, returnToDwarfs, useBucketOnBrew, useBrewOnTomb, useTinderboxOnTomb, killKalrag, searchCage))));
		allSteps.add(new PanelDetails("Entering the Temple", new ArrayList<>(Arrays.asList(killDisciple, enterTemple, useDollOnWell))));
		allSteps.add(new PanelDetails("Foggy Memories", new ArrayList<>(Arrays.asList(talkToKoftikAfterTemple, talkToKingLathasAfterTemple))));
		return allSteps;
	}
}
