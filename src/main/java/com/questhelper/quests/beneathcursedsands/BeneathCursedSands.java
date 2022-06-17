package com.questhelper.quests.beneathcursedsands;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.widget.WidgetModelRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.DigStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.WidgetStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(quest = QuestHelperQuest.BENEATH_CURSED_SANDS)
@Slf4j
public class BeneathCursedSands extends BasicQuestHelper
{
	// Items Required
	ItemRequirement coal, tinderbox, ironBar, spade, meat, prayerPotions, fiveCoins;

	// Items Recommended
	ItemRequirement waterskins, antipoison, accessToFairyRings, pharaosSceptre, food, meleeCombatGear,
		rangedCombatGear, staminaPotions, nardahTeleport;

	// Quest Items
	ItemRequirement messageFromJamila, stoneTablet, chest, scarabMould, scarabEmblem, rustyKey, lilyOfTheElid,
		cureCrate;

	// Primary Quest Line Steps
	QuestStep talkToJamilaToStart, receiveSpecialItemFromJamila, readMessage, talkToMaisaStartInvestigation,
		inspectBlockedPyramidEntry, talkToCitizenOrGuard, fightHeadMenaphiteGuard, talkToMaisaExploreCliffs,
		goFromCampsiteToRuinsOfUllek, inspectFurnace, useCoalOnFurnace, useTinderboxOnFurnace, searchWell,
		readStoneTablet, leaveRuinsOfUllek, digForChest, openChest, craftEmblem, useEmblemOnPillar, rotateScarabLeft,
		rotateScarabRight, confirmScarabRotation, enterDungeonToFightScarabMages, fightScarabMages,
		climbDownStairsAgain, pullLever, pullSecondLever, enterRiddleDoor, solveTombRiddle, enterTombDoor,
		talkToSpirit, takeRustyKey, unlockBossDoor, fightChampionOfScabaras, talkToScabarasHighPriest,
		talkToMaisaInNardah, attemptSteppingStones, pickLilyOfElid, takeLilyToZahur, talkToZahur,
		warmUpChemistryEquipment, bringCureToPriest, prepareFightMenaphiteAkh, talkToSophanemHighPriest,
		defeatMenaphiteAkh, defeatMenaphiteShadow, finishQuest;

	// Optional & Supportive Quest Steps
	QuestStep talkToMaisaPostFightCutsceneInterruption, obtainTinderbox, obtainSpade, goToRuinsOfUllek, enterDungeon,
		leaveTombDoor, leaveRiddleDoor, climbUpstairs, openBossDoor, fightScarabSwarm, destroyShadowRift,
		goToScabarasHighPriestDoorOne, goToScabarasHighPriestDoorTwo, leaveHighPriestDoorOne, leaveHighPriestDoorTwo,
		leaveDungeon, purchaseBeef, chemistryValveDecreaseLeft, chemistryValveIncreaseMiddle,
		chemistryValveDecreaseMiddle, chemistryValveIncreaseRight, chemistryValveDecreaseRight, talkToOsman;

	Requirement inRuinsOfUllek, inScarabMageArea, inLeverMazeArea, inRiddleArea, inTombArea, inBossArea,
		inBossTransitionArea;

	VarbitRequirement investigatedPyramid, hasReadStoneTablet, scarabRotatedDownwards, scarabRotationQuickestRight,
		chemistryValveLeftStepZero, chemistryValveLeftStepOne, chemistryValveLeftStepTwo, chemistryValveLeftStepThree,
		chemistryValveMiddleNearMax, chemistryValveMiddleAtMaximum, chemistryValveRightAtMaximum,
		chemistryValveRightNearMax;

	ObjectCondition firstLeverPulled;

	NpcCondition shouldFightScarabSwarm, shouldDestroyShadowRift, shouldFightMenaphiteShadow;

	WidgetModelRequirement isRotatingScarab, inChemistryPuzzle;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupZones();
		setupRequirements();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();
		steps.put(0, talkToJamilaToStart);
		steps.put(2, receiveSpecialItemFromJamila);
		steps.put(4, readMessage);

		steps.put(6, talkToMaisaStartInvestigation);
		steps.put(8, talkToMaisaStartInvestigation);
		steps.put(10, talkToMaisaStartInvestigation);
		steps.put(12, talkToMaisaStartInvestigation);

		ConditionalStep investigatePyramid = new ConditionalStep(this, inspectBlockedPyramidEntry);
		investigatePyramid.addStep(new Conditions(investigatedPyramid), talkToCitizenOrGuard);
		steps.put(14, investigatePyramid);

		steps.put(16, talkToCitizenOrGuard);
		steps.put(18, fightHeadMenaphiteGuard);
		steps.put(20, talkToMaisaPostFightCutsceneInterruption);
		steps.put(22, talkToMaisaExploreCliffs);
		steps.put(24, talkToMaisaExploreCliffs);

		ConditionalStep goToAndInspectFurnace = new ConditionalStep(this, goFromCampsiteToRuinsOfUllek);
		goToAndInspectFurnace.addStep(new Conditions(LogicType.NOR, tinderbox), obtainTinderbox);
		goToAndInspectFurnace.addStep(new Conditions(LogicType.NOR, spade), obtainSpade);
		goToAndInspectFurnace.addStep(new Conditions(inRuinsOfUllek), inspectFurnace);
		steps.put(26, goToAndInspectFurnace);

		steps.put(28, useCoalOnFurnace);
		steps.put(30, useTinderboxOnFurnace);

		ConditionalStep obtainEmblem = new ConditionalStep(this, goToRuinsOfUllek);
		obtainEmblem.addStep(new Conditions(new Conditions(LogicType.NOR, hasReadStoneTablet, stoneTablet), inRuinsOfUllek), searchWell);
		obtainEmblem.addStep(new Conditions(new Conditions(LogicType.NOR, hasReadStoneTablet), stoneTablet), readStoneTablet);
		obtainEmblem.addStep(new Conditions(new Conditions(LogicType.NOR, chest, scarabMould, scarabEmblem), inRuinsOfUllek), leaveRuinsOfUllek);
		obtainEmblem.addStep(new Conditions(LogicType.NOR, chest, scarabMould, scarabEmblem), digForChest);
		obtainEmblem.addStep(new Conditions(chest), openChest);
		obtainEmblem.addStep(new Conditions(new Conditions(LogicType.NOR, scarabEmblem), scarabMould, inRuinsOfUllek), craftEmblem);
		obtainEmblem.addStep(new Conditions(scarabEmblem, inRuinsOfUllek), useEmblemOnPillar);
		steps.put(32, obtainEmblem);
		steps.put(34, obtainEmblem);

		ConditionalStep rotateScarab = new ConditionalStep(this, goToRuinsOfUllek);
		rotateScarab.addStep(new Conditions(new Conditions(LogicType.NOR, isRotatingScarab), inRuinsOfUllek), useEmblemOnPillar);
		rotateScarab.addStep(new Conditions(isRotatingScarab, scarabRotatedDownwards), confirmScarabRotation);
		rotateScarab.addStep(new Conditions(isRotatingScarab, scarabRotationQuickestRight), rotateScarabRight);
		rotateScarab.addStep(new Conditions(isRotatingScarab), rotateScarabLeft);
		steps.put(36, rotateScarab);

		ConditionalStep defeatScarabMages = new ConditionalStep(this, goToRuinsOfUllek);
		defeatScarabMages.addStep(new Conditions(inRuinsOfUllek), enterDungeonToFightScarabMages);
		defeatScarabMages.addStep(new Conditions(inScarabMageArea), fightScarabMages);
		steps.put(38, defeatScarabMages);
		steps.put(40, defeatScarabMages);

		ConditionalStep climbDownSecondSteps = new ConditionalStep(this, goToRuinsOfUllek);
		climbDownSecondSteps.addStep(new Conditions(inRuinsOfUllek), enterDungeon);
		climbDownSecondSteps.addStep(new Conditions(inScarabMageArea), climbDownStairsAgain);
		steps.put(42, climbDownSecondSteps);

		ConditionalStep pullFirstLever = new ConditionalStep(this, goToRuinsOfUllek);
		pullFirstLever.addStep(new Conditions(inRuinsOfUllek), enterDungeon);
		pullFirstLever.addStep(new Conditions(inScarabMageArea), climbDownStairsAgain);
		pullFirstLever.addStep(new Conditions(new Conditions(LogicType.NOR, firstLeverPulled), inLeverMazeArea), pullLever);
		pullFirstLever.addStep(new Conditions(inLeverMazeArea), pullSecondLever);
		steps.put(44, pullFirstLever);

		ConditionalStep solveRiddle = new ConditionalStep(this, goToRuinsOfUllek);
		solveRiddle.addStep(new Conditions(inRuinsOfUllek), enterDungeon);
		solveRiddle.addStep(new Conditions(inScarabMageArea), climbDownStairsAgain);
		solveRiddle.addStep(new Conditions(inLeverMazeArea), enterRiddleDoor);
		solveRiddle.addStep(new Conditions(inRiddleArea), solveTombRiddle);
		steps.put(46, solveRiddle);

		ConditionalStep enterTomb = new ConditionalStep(this, goToRuinsOfUllek);
		enterTomb.addStep(new Conditions(inRuinsOfUllek), enterDungeon);
		enterTomb.addStep(new Conditions(inScarabMageArea), climbDownStairsAgain);
		enterTomb.addStep(new Conditions(inLeverMazeArea), enterRiddleDoor);
		enterTomb.addStep(new Conditions(inRiddleArea), enterTombDoor);
		enterTomb.addStep(new Conditions(inTombArea), talkToSpirit);
		steps.put(48, enterTomb);
		steps.put(50, enterTomb);
		steps.put(52, enterTomb);

		ConditionalStep obtainKey = new ConditionalStep(this, goToRuinsOfUllek);
		obtainKey.addStep(new Conditions(inRuinsOfUllek), enterDungeon);
		obtainKey.addStep(new Conditions(inScarabMageArea), climbDownStairsAgain);
		obtainKey.addStep(new Conditions(inLeverMazeArea), enterRiddleDoor);
		obtainKey.addStep(new Conditions(inRiddleArea), enterTombDoor);
		obtainKey.addStep(new Conditions(inTombArea), takeRustyKey);
		steps.put(54, obtainKey);

		ConditionalStep beginChampionOfScabarasFight = new ConditionalStep(this, goToRuinsOfUllek);
		beginChampionOfScabarasFight.addStep(new Conditions(inRuinsOfUllek), enterDungeon);
		beginChampionOfScabarasFight.addStep(new Conditions(inScarabMageArea, rustyKey), unlockBossDoor);
		beginChampionOfScabarasFight.addStep(new Conditions(inScarabMageArea), openBossDoor);
		beginChampionOfScabarasFight.addStep(new Conditions(inLeverMazeArea), climbUpstairs);
		beginChampionOfScabarasFight.addStep(new Conditions(inRiddleArea), leaveRiddleDoor);
		beginChampionOfScabarasFight.addStep(new Conditions(inTombArea), leaveTombDoor);
		steps.put(56, beginChampionOfScabarasFight);

		ConditionalStep championOfScabarasFight = new ConditionalStep(this, goToRuinsOfUllek);
		championOfScabarasFight.addStep(new Conditions(inRuinsOfUllek), enterDungeon);
		championOfScabarasFight.addStep(new Conditions(inScarabMageArea), openBossDoor);
//		championOfScabarasFight.addStep(new Conditions(shouldFightScarabSwarm), fightScarabSwarm);
//		championOfScabarasFight.addStep(new Conditions(shouldDestroyShadowRift), destroyShadowRift);
		championOfScabarasFight.addStep(new Conditions(inBossArea), fightChampionOfScabaras);
		steps.put(58, championOfScabarasFight);
		steps.put(60, championOfScabarasFight);

		ConditionalStep highPriest = new ConditionalStep(this, goToRuinsOfUllek);
		highPriest.addStep(new Conditions(inRuinsOfUllek), enterDungeon);
		highPriest.addStep(new Conditions(inScarabMageArea), goToScabarasHighPriestDoorOne);
		highPriest.addStep(new Conditions(inBossTransitionArea), goToScabarasHighPriestDoorTwo);
		highPriest.addStep(new Conditions(inBossArea), talkToScabarasHighPriest);
		steps.put(62, highPriest);
		steps.put(64, highPriest);
		steps.put(66, highPriest);

		ConditionalStep goToNardah = new ConditionalStep(this, talkToMaisaInNardah);
		goToNardah.addStep(new Conditions(inRuinsOfUllek), leaveRuinsOfUllek);
		goToNardah.addStep(new Conditions(inScarabMageArea), leaveDungeon);
		goToNardah.addStep(new Conditions(inBossTransitionArea), leaveHighPriestDoorTwo);
		goToNardah.addStep(new Conditions(inBossArea), leaveHighPriestDoorOne);
		steps.put(68, goToNardah);
		steps.put(70, goToNardah);

		ConditionalStep buyMeat = new ConditionalStep(this, talkToMaisaInNardah);
		buyMeat.addStep(new Conditions(LogicType.NOR, meat), purchaseBeef);
		buyMeat.addStep(new Conditions(meat), attemptSteppingStones);
		steps.put(72, buyMeat);
		steps.put(74, buyMeat);

		ConditionalStep returnToZahur = new ConditionalStep(this, pickLilyOfElid);
		returnToZahur.addStep(new Conditions(lilyOfTheElid), takeLilyToZahur);
		steps.put(76, returnToZahur);
		steps.put(78, returnToZahur);
		steps.put(80, talkToZahur);

		ConditionalStep chemistryPuzzle = new ConditionalStep(this, warmUpChemistryEquipment);
		chemistryPuzzle.addStep(new Conditions(inChemistryPuzzle, chemistryValveLeftStepZero, new Conditions(LogicType.NAND, chemistryValveMiddleAtMaximum)), chemistryValveIncreaseMiddle);
		chemistryPuzzle.addStep(new Conditions(inChemistryPuzzle, chemistryValveLeftStepZero, chemistryValveMiddleAtMaximum, new Conditions(LogicType.NAND, chemistryValveRightAtMaximum)), chemistryValveIncreaseRight);
		chemistryPuzzle.addStep(new Conditions(inChemistryPuzzle, chemistryValveLeftStepZero, chemistryValveMiddleAtMaximum, chemistryValveRightAtMaximum), chemistryValveDecreaseRight);
		chemistryPuzzle.addStep(new Conditions(inChemistryPuzzle, chemistryValveLeftStepOne, chemistryValveMiddleAtMaximum, chemistryValveRightNearMax), chemistryValveDecreaseMiddle);
		chemistryPuzzle.addStep(new Conditions(inChemistryPuzzle, chemistryValveLeftStepTwo, chemistryValveMiddleNearMax, chemistryValveRightAtMaximum), chemistryValveDecreaseRight);
		chemistryPuzzle.addStep(new Conditions(inChemistryPuzzle, chemistryValveLeftStepThree, chemistryValveMiddleNearMax, chemistryValveRightNearMax), chemistryValveDecreaseMiddle);
		chemistryPuzzle.addStep(new Conditions(inChemistryPuzzle), chemistryValveDecreaseLeft);
		steps.put(82, chemistryPuzzle);
		steps.put(84, talkToZahur);
		steps.put(86, talkToZahur);

		steps.put(88, bringCureToPriest);
		steps.put(90, talkToSophanemHighPriest);
		steps.put(92, prepareFightMenaphiteAkh);
		steps.put(94, prepareFightMenaphiteAkh);
		steps.put(96, prepareFightMenaphiteAkh);

		ConditionalStep menaphiteAkhFight = new ConditionalStep(this, defeatMenaphiteAkh);
//		menaphiteAkhFight.addStep(shouldFightMenaphiteShadow, defeatMenaphiteShadow);
		steps.put(98, menaphiteAkhFight);
		steps.put(100, talkToOsman);
		steps.put(102, finishQuest);
		steps.put(104, finishQuest);
		steps.put(106, finishQuest);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		coal = new ItemRequirement("Coal", ItemID.COAL);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).isNotConsumed();
		tinderbox.canBeObtainedDuringQuest();
		ironBar = new ItemRequirement("Iron bar", ItemID.IRON_BAR);
		spade = new ItemRequirement("Spade", ItemID.SPADE).isNotConsumed();
		spade.canBeObtainedDuringQuest();
		meat = new ItemRequirement("Any cooked or raw meat", ItemID.COOKED_MEAT);
		meat.addAlternates(ItemID.RAW_BEEF, ItemID.RAW_BEAR_MEAT, ItemID.RAW_BOAR_MEAT, ItemID.RAW_RAT_MEAT);
		meat.setTooltip("Purchasable from a shop during the quest. Fish will NOT work");
		waterskins = new ItemRequirement("Waterskin(s)", ItemID.WATERSKIN4).isNotConsumed();
		waterskins.addAlternates(ItemID.WATERSKIN3, ItemID.WATERSKIN2, ItemID.WATERSKIN1);
		waterskins.setTooltip("Used for protection against the desert heat");
		antipoison = new ItemRequirement("Antipoison", ItemCollections.ANTIPOISONS);
		accessToFairyRings = new ItemRequirement("Access to Fairy Rings", ItemID.DRAMEN_STAFF).isNotConsumed();
		accessToFairyRings.addAlternates(ItemID.LUNAR_STAFF);
		pharaosSceptre = new ItemRequirement("Pharaoh's sceptre", ItemCollections.PHAROAH_SCEPTRE).isNotConsumed();
		pharaosSceptre.setTooltip("When visiting Necropolis during the quest, you can unlock the direct teleport by using 'Commune' on the Obelisk.");
		food = new ItemRequirement("Food", -1, -1);
		food.setDisplayItemId(BankSlotIcons.getFood());
		meleeCombatGear = new ItemRequirement("Melee combat gear", -1, -1).isNotConsumed();
		meleeCombatGear.setDisplayItemId(BankSlotIcons.getMeleeCombatGear());
		rangedCombatGear = new ItemRequirement("Ranged combat gear", -1, -1).isNotConsumed();
		rangedCombatGear.setDisplayItemId(BankSlotIcons.getRangedCombatGear());
		staminaPotions = new ItemRequirement("Stamina Potions", ItemCollections.STAMINA_POTIONS, -1);
		nardahTeleport = new ItemRequirement("Nardah Teleport", ItemID.DESERT_AMULET_4);
		nardahTeleport.addAlternates(ItemID.NARDAH_TELEPORT, ItemID.DESERT_AMULET_3, ItemID.DESERT_AMULET_2);
		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS, -1);
		prayerPotions.addAlternates(ItemCollections.SUPER_RESTORE_POTIONS);

		messageFromJamila = new ItemRequirement("Message", ItemID.MESSAGE_26942);
		messageFromJamila.setHighlightInInventory(true);
		stoneTablet = new ItemRequirement("Stone tablet", ItemID.STONE_TABLET_26954);
		stoneTablet.setHighlightInInventory(true);
		chest = new ItemRequirement("Chest", ItemID.CHEST_26955);
		chest.setHighlightInInventory(true);
		chest.alsoCheckBank(questBank);
		scarabMould = new ItemRequirement("Scarab mould", ItemID.SCARAB_MOULD);
		scarabEmblem = new ItemRequirement("Scarab emblem", ItemID.SCARAB_EMBLEM);
		scarabEmblem.setHighlightInInventory(true);
		rustyKey = new ItemRequirement("Rusty key", ItemID.RUSTY_KEY);
		rustyKey.setHighlightInInventory(true);
		fiveCoins = new ItemRequirement("Coins", ItemCollections.COINS, 5);
		lilyOfTheElid = new ItemRequirement("Lily of the Elid", ItemID.LILY_OF_THE_ELID);
		cureCrate = new ItemRequirement("Cure crate", ItemID.CURE_CRATE);
	}

	public void setupZones()
	{
		Zone ruinsOfUllekArea1 = new Zone(new WorldPoint(3388, 2821, 0), new WorldPoint(3416, 2803, 0));
		Zone ruinsOfUllekArea2 = new Zone(new WorldPoint(3388, 2821, 0), new WorldPoint(3445, 2858, 0));
		Zone ruinsOfUllekArea3 = new Zone(new WorldPoint(3393, 2803, 0), new WorldPoint(3404, 2793, 0));
		Zone ruinsOfUllekArea4 = new Zone(new WorldPoint(3412, 2799, 0), new WorldPoint(3400, 2802, 0));
		inRuinsOfUllek = new ZoneRequirement(ruinsOfUllekArea1, ruinsOfUllekArea2, ruinsOfUllekArea3, ruinsOfUllekArea4);

		Zone scarabMageArea = new Zone(new WorldPoint(3446, 9240, 2), new WorldPoint(3433, 9256, 2));
		inScarabMageArea = new ZoneRequirement(scarabMageArea);

		Zone leverMazeArea = new Zone(new WorldPoint(3440, 9217, 0), new WorldPoint(3406, 9277, 0));
		inLeverMazeArea = new ZoneRequirement(leverMazeArea);

		Zone riddleArea = new Zone(new WorldPoint(3390, 9240, 0), new WorldPoint(3405, 9256, 0));
		inRiddleArea = new ZoneRequirement(riddleArea);

		Zone tombArea = new Zone(new WorldPoint(3389, 9240, 0), new WorldPoint(3365, 9254, 0));
		inTombArea = new ZoneRequirement(tombArea);

		Zone bossArea = new Zone(new WorldPoint(3432, 9241, 2), new WorldPoint(3404, 9255, 2));
		inBossArea = new ZoneRequirement(bossArea);

		Zone bossTransitionArea = new Zone(new WorldPoint(3421, 9241, 2), new WorldPoint(3434, 9255, 2));
		inBossTransitionArea = new ZoneRequirement(bossTransitionArea);
	}

	public void setupConditions()
	{
		investigatedPyramid = new VarbitRequirement(13844, 1, Operation.EQUAL);

		// Has found stone tablet, =1
		// Has read stone tablet, =2
		hasReadStoneTablet = new VarbitRequirement(13847, 2, Operation.GREATER_EQUAL);

		isRotatingScarab = new WidgetModelRequirement(750, 3, -1);
		scarabRotatedDownwards = new VarbitRequirement(13849, 15);
		scarabRotationQuickestRight = new VarbitRequirement(13849, 15, Operation.GREATER_EQUAL);

		firstLeverPulled = new ObjectCondition(ObjectID.LEVER_43968, new WorldPoint(3439, 9225, 0));

		shouldFightScarabSwarm = new NpcCondition(NpcID.SCARAB_SWARM_11484);
		shouldDestroyShadowRift = new NpcCondition(NpcID.SHADOW_RIFT);

		inChemistryPuzzle = new WidgetModelRequirement(751, 3, -1);
		chemistryValveLeftStepZero = new VarbitRequirement(13863, 0);
		chemistryValveLeftStepOne = new VarbitRequirement(13863, 3);
		chemistryValveLeftStepTwo = new VarbitRequirement(13863, 6);
		chemistryValveLeftStepThree = new VarbitRequirement(13863, 9);
		chemistryValveMiddleAtMaximum = new VarbitRequirement(13864, 45);
		chemistryValveMiddleNearMax = new VarbitRequirement(13864, 42);
		chemistryValveRightAtMaximum = new VarbitRequirement(13865, 45);
		chemistryValveRightNearMax = new VarbitRequirement(13865, 42);

		shouldFightMenaphiteShadow = new NpcCondition(NpcID.MENAPHITE_SHADOW);
	}

	public void setupSteps()
	{
		// Starting off
		talkToJamilaToStart = new NpcStep(this, NpcID.JAMILA, new WorldPoint(3311, 2779, 0), "Talk to Jamila in Sophanem to start the quest.");
		talkToJamilaToStart.addDialogSteps("What's this special item?", "Yes.");

		receiveSpecialItemFromJamila = new NpcStep(this, NpcID.JAMILA, new WorldPoint(3311, 2779, 0), "Talk to Jamila to obtain a special item");
		receiveSpecialItemFromJamila.addDialogStep("About that special item...");

		readMessage = new DetailedQuestStep(this, "Read the message.", messageFromJamila);

		// Entranced Menaphites
		talkToMaisaStartInvestigation = new NpcStep(this, NpcID.MAISA_11474, new WorldPoint(3378, 2792, 0), "Talk to Maisa at the campsite east of Sophanem, and investigate the excavation.");
		talkToMaisaStartInvestigation.addDialogStep("Let's go.");

		inspectBlockedPyramidEntry = new ObjectStep(this, NullObjectID.NULL_44596, new WorldPoint(3358, 2712, 0), "Inspect the blocked entry at the Jaltevas Pyramid.");

		talkToCitizenOrGuard = new NpcStep(this, NpcID.CITIZEN_11537, new WorldPoint(3347, 2718, 0), "Prepare to fight the Head Menaphite Guard, and talk to either a citizen or Menaphite Guard to start the fight.", true);
		((NpcStep) talkToCitizenOrGuard).addAlternateNpcs(NpcID.CITIZEN_11536, NpcID.CITIZEN_11534, NpcID.MENAPHITE_GUARD_11515, NpcID.MENAPHITE_GUARD_11516, NpcID.MENAPHITE_GUARD_11518);

		fightHeadMenaphiteGuard = new NpcStep(this, NpcID.HEAD_MENAPHITE_GUARD_11529, "Fight the Head Menaphite Guard. This boss uses melee, and can hit up to 16. DO NOT USE ANY OVERHEAD PROTECTION PRAYERS, OR YOU WILL GET HIT FOR 1/3RD OF YOUR HITPOINTS", meleeCombatGear, food);

		talkToMaisaPostFightCutsceneInterruption = new NpcStep(this, NpcID.MAISA_11474, new WorldPoint(3327, 2740, 0), "Talk to Maisa to continue the quest.");

		// The Ruins of Ullek
		talkToMaisaExploreCliffs = new NpcStep(this, NpcID.MAISA_11474, new WorldPoint(3378, 2792, 0), "Go back to Maisa's camp and talk to her.");

		inspectFurnace = new ObjectStep(this, NullObjectID.NULL_2883, new WorldPoint(3404, 2824, 0), "Inspect the furnace.");
		obtainTinderbox = new ObjectStep(this, ObjectID.CAMPING_EQUIPMENT_43889, new WorldPoint(3379, 2791, 0), "Search the camping equipment by the tent for a tinderbox.");
		obtainTinderbox.addIcon(ItemID.TINDERBOX);
		obtainSpade = new DetailedQuestStep(this, new WorldPoint(3355, 2758, 0), "Pick up a spade south of the campsite.");
		obtainSpade.addIcon(ItemID.SPADE);

		goFromCampsiteToRuinsOfUllek = new ObjectStep(this, ObjectID.STEPS_44113, new WorldPoint(3419, 2803, 0), "From the campsite, head south and then to the east, around the cliffs, where you will find a set of stairs to the Ruins of Ullek.");
		((DetailedQuestStep) goFromCampsiteToRuinsOfUllek).setLinePoints(Arrays.asList(new WorldPoint(3370, 2795, 0), new WorldPoint(3370, 2773, 0), new WorldPoint(3387, 2758, 0), new WorldPoint(3402, 2758, 0), new WorldPoint(3402, 2758, 0), new WorldPoint(3419, 2778, 0), new WorldPoint(3419, 2802, 0)));

		useCoalOnFurnace = new ObjectStep(this, NullObjectID.NULL_2883, new WorldPoint(3404, 2824, 0), "Use coal on the furnace to refuel it.", tinderbox, coal.highlighted());
		useCoalOnFurnace.addDialogStep("Yes.");
		useCoalOnFurnace.addIcon(ItemID.COAL);

		useTinderboxOnFurnace = new ObjectStep(this, NullObjectID.NULL_2883, new WorldPoint(3404, 2824, 0), "Use your tinderbox on the furnace to light it.", tinderbox.highlighted());
		useTinderboxOnFurnace.addDialogStep("Yes.");
		useTinderboxOnFurnace.addIcon(ItemID.TINDERBOX);

		searchWell = new ObjectStep(this, NullObjectID.NULL_6630, new WorldPoint(3400, 2828, 0), "Search the well to receive a stone tablet.");
		readStoneTablet = new DetailedQuestStep(this, "Read the stone tablet.", stoneTablet);
		leaveRuinsOfUllek = new ObjectStep(this, ObjectID.STEPS_44111, new WorldPoint(3417, 2805, 0), "Leave the Ruins of Ullek using the steps in the south-east.");
		digForChest = new DigStep(this, new WorldPoint(3411, 2786, 0), "Dig in front of the southernmost ritual pillar");
		openChest = new DetailedQuestStep(this, "Open the chest and input the passcode \"1118513\".", chest);
		goToRuinsOfUllek = new ObjectStep(this, ObjectID.STEPS_44113, new WorldPoint(3419, 2803, 0), "Return to the Ruins of Ullek.");

		craftEmblem = new ObjectStep(this, NullObjectID.NULL_2883, new WorldPoint(3404, 2824, 0), "Craft a scarab emblem using the furnace.", scarabMould, ironBar.highlighted());
		craftEmblem.addDialogStep("Yes.");
		craftEmblem.addIcon(ItemID.IRON_BAR);

		useEmblemOnPillar = new ObjectStep(this, NullObjectID.NULL_6579, new WorldPoint(3418, 2848, 0), "Inspect the " +
			"pillar to the north to insert the emblem.", scarabEmblem);
		useEmblemOnPillar.addIcon(ItemID.SCARAB_EMBLEM);
		useEmblemOnPillar.addDialogStep("Yes.");

		rotateScarabLeft = new WidgetStep(this, "Rotate the scarab on the emblem so it faces downwards.", 750, 7);
		rotateScarabRight = new WidgetStep(this, "Rotate the scarab on the emblem so it faces downwards.", 750, 8);
		confirmScarabRotation = new WidgetStep(this, "Rotate the scarab on the emblem so it faces downwards.", 750, 9);

		enterDungeonToFightScarabMages = new ObjectStep(this, NullObjectID.NULL_6643, new WorldPoint(3409, 2848, 0),
			"Enter the dungeon.", meleeCombatGear, food);
		enterDungeon = new ObjectStep(this, NullObjectID.NULL_6643, new WorldPoint(3409, 2848, 0), "Enter the dungeon.");
		fightScarabMages = new NpcStep(this, NpcID.SCARAB_MAGE_11508, "Use Protect from Magic and defeat the two Scarab Mages.", meleeCombatGear, food, antipoison);

		climbDownStairsAgain = new ObjectStep(this, ObjectID.STAIRS_43959, "Climb down the stairs.");
		((ObjectStep) climbDownStairsAgain).addAlternateObjects(ObjectID.STAIRS_43958);

		pullLever = new DetailedQuestStep(this, new WorldPoint(3439, 9225, 0), "Avoid the projectiles, run west to the junction, run south, follow the corridor, and pull the lever at the end.", food, staminaPotions);
		((DetailedQuestStep) pullLever).setLinePoints(Arrays.asList(new WorldPoint(3436, 9239, 0), new WorldPoint(3430, 9239, 0), new WorldPoint(3430, 9246, 0), new WorldPoint(3429, 9247, 0), new WorldPoint(3415, 9247, 0), new WorldPoint(3412, 9244, 0), new WorldPoint(3411, 9243, 0), new WorldPoint(3411, 9239, 0), new WorldPoint(3410, 9239, 0), new WorldPoint(3411, 9238, 0), new WorldPoint(3411, 9234, 0), new WorldPoint(3412, 9234, 0), new WorldPoint(3411, 9233, 0), new WorldPoint(3411, 9233, 0), new WorldPoint(3411, 9230, 0), new WorldPoint(3415, 9226, 0), new WorldPoint(3419, 9226, 0), new WorldPoint(3420, 9227, 0), new WorldPoint(3421, 9226, 0), new WorldPoint(3430, 9226, 0), new WorldPoint(3431, 9227, 0), new WorldPoint(3435, 9227, 0), new WorldPoint(3437, 9225, 0)));

		pullSecondLever = new DetailedQuestStep(this, new WorldPoint(3439, 9271, 0), "As fast as possible, run back to the juncture, then run north and pull the second lever.", food, staminaPotions);
		((DetailedQuestStep) pullSecondLever).setLinePoints(Arrays.asList(new WorldPoint(3437, 9225, 0), new WorldPoint(3435, 9222, 0), new WorldPoint(3432, 9222, 0), new WorldPoint(3430, 9224, 0), new WorldPoint(3426, 9224, 0), new WorldPoint(3425, 9223, 0), new WorldPoint(3420, 9227, 0), new WorldPoint(3419, 9226, 0), new WorldPoint(3415, 9226, 0), new WorldPoint(3411, 9230, 0), new WorldPoint(3411, 9237, 0), new WorldPoint(3410, 9237, 0), new WorldPoint(3410, 9244, 0), new WorldPoint(3408, 9246, 0), new WorldPoint(3408, 9249, 0), new WorldPoint(3411, 9252, 0), new WorldPoint(3411, 9257, 0), new WorldPoint(3410, 9257, 0), new WorldPoint(3411, 9258, 0), new WorldPoint(3411, 9262, 0), new WorldPoint(3412, 9262, 0), new WorldPoint(3412, 9267, 0), new WorldPoint(3415, 9270, 0), new WorldPoint(3419, 9270, 0), new WorldPoint(3420, 9269, 0), new WorldPoint(3421, 9270, 0), new WorldPoint(3429, 9270, 0), new WorldPoint(3430, 9269, 0), new WorldPoint(3433, 9268, 0), new WorldPoint(3436, 9268, 0), new WorldPoint(3438, 9271, 0)));

		enterRiddleDoor = new ObjectStep(this, ObjectID.DOOR_43961, new WorldPoint(3405, 9248, 0), "With the traps disabled, go through the door west of the juncture.");

		// Riddle of the Tomb
		// TODO: This step needs to be tested.
		solveTombRiddle = new TombRiddle(this);

		enterTombDoor = new ObjectStep(this, ObjectID.DOOR_43962, new WorldPoint(3389, 9248, 0), "Enter the tomb.");

		talkToSpirit = new NpcStep(this, NpcID.SPIRIT, new WorldPoint(3377, 9248, 0), "Speak to the Spirit.");
		((NpcStep) talkToSpirit).addAlternateNpcs(NpcID.MEHHAR);

		takeRustyKey = new ObjectStep(this, NullObjectID.NULL_44591, new WorldPoint(3368, 9248, 0), "Take the rusty key from the urn in the back of the room.");

		// The Champion of Scabaras
		leaveTombDoor = new ObjectStep(this, ObjectID.DOOR_43962, new WorldPoint(3389, 9248, 0), "Leave the tomb.");

		leaveRiddleDoor = new ObjectStep(this, ObjectID.DOOR_43961, new WorldPoint(3405, 9248, 0), "Leave the riddle area.");

		climbUpstairs = new ObjectStep(this, ObjectID.STAIRS_43956, "Go back upstairs.");
		((ObjectStep) climbUpstairs).addAlternateObjects(ObjectID.STAIRS_43957);
		((DetailedQuestStep) climbUpstairs).setLinePoints(Arrays.asList(new WorldPoint(3408, 9248, 0), new WorldPoint(3411, 9244, 0), new WorldPoint(3415, 9248, 0), new WorldPoint(3431, 9248, 0), new WorldPoint(3431, 9239, 0), new WorldPoint(3438, 9239, 0), new WorldPoint(3438, 9243, 0)));

		unlockBossDoor = new ObjectStep(this, ObjectID.DOOR_43960, "Prepare to fight the Champion of Scabaras, and unlock the door to start the fight.", rangedCombatGear, food, antipoison, staminaPotions, prayerPotions, rustyKey);
		unlockBossDoor.addIcon(ItemID.RUSTY_KEY);
		unlockBossDoor.addSubSteps(leaveTombDoor, leaveRiddleDoor, climbUpstairs);

		openBossDoor = new ObjectStep(this, ObjectID.DOOR_43960, "Prepare to fight the Champion of Scabaras, and open the door to start the fight.", rangedCombatGear, food, antipoison, staminaPotions, prayerPotions);

		fightChampionOfScabaras = new NpcStep(this, NpcID.CHAMPION_OF_SCABARAS_11483, "Fight the Champion of Scabaras.", rangedCombatGear, food, antipoison, staminaPotions, prayerPotions);
		fightChampionOfScabaras.addText("Protect from Magic, and keep 4+ tiles distance at all times.");
		fightChampionOfScabaras.addText("Whenever a rift or swarm appears, kill it.");
		fightChampionOfScabaras.addText("He is weak to ranged attacks, so bring your best ranged gear.");
		fightScarabSwarm = new NpcStep(this, NpcID.SCARAB_SWARM_11484, "Defeat the Swarm as soon as possible, to prevent shadow flames from hindering your movement.");
		destroyShadowRift = new NpcStep(this, NpcID.SHADOW_RIFT, "Destroy the Shadow Rift as soon as possible, to prevent 35+ damage from occurring once it explodes.");

		goToScabarasHighPriestDoorOne = new ObjectStep(this, ObjectID.DOOR_43960, "Go back to the Champion of Scabaras boss area, and talk to the High Priest of Scabaras.");
		goToScabarasHighPriestDoorTwo = new ObjectStep(this, ObjectID.DOOR_43963, "Go back to the Champion of Scabaras boss area, and talk to the High Priest of Scabaras.");
		talkToScabarasHighPriest = new NpcStep(this, NpcID.HIGH_PRIEST_OF_SCABARAS, "Talk to the High Priest of Scabaras.");

		leaveHighPriestDoorOne = new ObjectStep(this, ObjectID.DOOR_43963, "Leave the dungeon.");
		leaveHighPriestDoorTwo = new ObjectStep(this, ObjectID.DOOR_43960, "Leave the dungeon.");
		leaveDungeon = new ObjectStep(this, ObjectID.STAIRS_43955, "Leave the dungeon.");

		// Cure for the Pox
		talkToMaisaInNardah = new NpcStep(this, NpcID.MAISA_11474, new WorldPoint(3425, 2909, 0), "Talk to Maisa or Zahur in Nardah.");
		((NpcStep) talkToMaisaInNardah).addAlternateNpcs(NpcID.ZAHUR);
		talkToMaisaInNardah.addSubSteps(leaveHighPriestDoorOne, leaveHighPriestDoorTwo, leaveDungeon);

		purchaseBeef = new NpcStep(this, NpcID.KAZEMDE, new WorldPoint(3415, 2908, 0), "Purchase some meat from the General Store.", fiveCoins);

		attemptSteppingStones = new ObjectStep(this, ObjectID.STEPPING_STONE_43988, new WorldPoint(3353, 2923, 0), "Head west and attempt to jump to the stepping stones over the River Elid.", meat);
		attemptSteppingStones.addDialogStep("Yes.");

		pickLilyOfElid = new ObjectStep(this, NullObjectID.NULL_44593, new WorldPoint(3353, 2927, 0), "Attempt to cross the stepping stones again, and pick the Lily of the Elid.");
		takeLilyToZahur = new NpcStep(this, NpcID.ZAHUR, new WorldPoint(3425, 2909, 0), "Go back to Zahur in Nardah.", lilyOfTheElid);
		talkToZahur = new NpcStep(this, NpcID.ZAHUR, new WorldPoint(3425, 2909, 0), "Talk to Zahur in Nardah.");

		warmUpChemistryEquipment = new ObjectStep(this, NullObjectID.NULL_44594, new WorldPoint(3424, 2905, 0), "Warm up Zahur's Chemistry Equipment.");
		chemistryValveDecreaseLeft = new WidgetStep(this, "Warm up the Chemistry Equipment. Decrease the temperature of the first valve.", 751, 24);
		chemistryValveIncreaseMiddle = new WidgetStep(this, "Warm up the Chemistry Equipment. Increase the temperature of the second valve.", 751, 25);
		chemistryValveDecreaseMiddle = new WidgetStep(this, "Warm up the Chemistry Equipment. Decrease the temperature of the second valve.", 751, 26);
		chemistryValveIncreaseRight = new WidgetStep(this, "Warm up the Chemistry Equipment. Increase the temperature of the third valve.", 751, 27);
		chemistryValveDecreaseRight = new WidgetStep(this, "Warm up the Chemistry Equipment. Decrease the temperature of the third valve.", 751, 28);
		warmUpChemistryEquipment.addSubSteps(chemistryValveDecreaseLeft, chemistryValveIncreaseMiddle,
			chemistryValveDecreaseMiddle, chemistryValveIncreaseRight, chemistryValveDecreaseRight);

		bringCureToPriest = new NpcStep(this, NpcID.HIGH_PRIEST_4206, new WorldPoint(3281, 2772, 0), "Bring the cure crate to the High Priest in Sophanem.", cureCrate);
		talkToSophanemHighPriest = new NpcStep(this, NpcID.HIGH_PRIEST_4206, new WorldPoint(3281, 2772, 0), "Talk to the High Priest in Sophanem.");

		// Fight with the Menaphite Akh
		prepareFightMenaphiteAkh = new NpcStep(this, NpcID.MAISA_11474, new WorldPoint(3327, 2740, 0), "Prepare to fight the Menaphite Akh (lvl 351), and talk to Maisa in Necropolis when ready.", meleeCombatGear, waterskins);

		defeatMenaphiteAkh = new NpcStep(this, NpcID.MENAPHITE_AKH_11492, "Defeat the Menaphite Akh. This boss uses melee, and will occasionally cast lightning in front of her, " +
			"so be prepared to walk behind her to avoid this attack. Whenever a shadow version of them appears, kill " +
			"them quickly as they'll deal a lot of damage.", meleeCombatGear,waterskins);
//		defeatMenaphiteShadow = new NpcStep(this, NpcID.MENAPHITE_SHADOW, "Quickly defeat the Menaphite Shadow, which attack using Ranged and Magic and can deal large damage.");

		talkToOsman = new NpcStep(this, NpcID.OSMAN_11486, new WorldPoint(3369, 2799, 0), "Talk to Osman");

		finishQuest = new NpcStep(this, NpcID.MAISA_11474, new WorldPoint(3281, 2772, 0), "Talk to Maisa or the High Priest in Sophanem to finish the quest.");
		((NpcStep) finishQuest).addAlternateNpcs(NpcID.HIGH_PRIEST_4206);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(waterskins, antipoison, accessToFairyRings, pharaosSceptre, meleeCombatGear, food, staminaPotions, nardahTeleport);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(coal, tinderbox, ironBar, spade, meat);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Head Menaphite Guard (lvl 174) without protection prayers", "Two Scabarite Mages (lvl 119)", "Champion of Scabaras (lvl 379)", "Menaphite Akh (lvl 351)");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.AGILITY, 20000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(new UnlockReward("Access to the Tombs of Amascut."), new UnlockReward("Ability to unlock the Jaltevas teleport option on the Pharaoh's sceptre"), new UnlockReward("Access to fairy ring code A.K.P (a small island south-west of Necropolis)"));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(new ItemReward("A Keris Partisan", ItemID.KERIS_PARTISAN, 1), new ItemReward("A Circlet of Water", ItemID.CIRCLET_OF_WATER, 1));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToJamilaToStart, readMessage), Collections.emptyList(), Collections.singletonList(waterskins)));
		allSteps.add(new PanelDetails("Entranced Menaphites", Arrays.asList(talkToMaisaStartInvestigation, inspectBlockedPyramidEntry, talkToCitizenOrGuard, fightHeadMenaphiteGuard, talkToMaisaPostFightCutsceneInterruption),
			Collections.singletonList(meleeCombatGear), Arrays.asList(waterskins, food)));
		allSteps.add(new PanelDetails("The Ruins of Ullek", Arrays.asList(talkToMaisaExploreCliffs, goFromCampsiteToRuinsOfUllek, inspectFurnace, useCoalOnFurnace, useTinderboxOnFurnace, searchWell, readStoneTablet, digForChest, openChest, craftEmblem, useEmblemOnPillar, confirmScarabRotation, enterDungeonToFightScarabMages, fightScarabMages, climbDownStairsAgain, pullLever, pullSecondLever, enterRiddleDoor), Arrays.asList(meleeCombatGear, coal, tinderbox, spade, ironBar), Arrays.asList(food, antipoison, waterskins)));
		allSteps.add(new PanelDetails("Riddle of the Tomb", Arrays.asList(solveTombRiddle, enterTombDoor, talkToSpirit, takeRustyKey)));
		allSteps.add(new PanelDetails("The Champion of Scabaras", Arrays.asList(unlockBossDoor, fightChampionOfScabaras, talkToScabarasHighPriest), Arrays.asList(rangedCombatGear, food, rustyKey)));
		allSteps.add(new PanelDetails("Cure for the Pox", Arrays.asList(talkToMaisaInNardah, purchaseBeef, attemptSteppingStones, pickLilyOfElid, takeLilyToZahur, talkToZahur, warmUpChemistryEquipment, bringCureToPriest), Arrays.asList(meat, waterskins)));
		allSteps.add(new PanelDetails("Fight with the Menaphite Akh", Arrays.asList(prepareFightMenaphiteAkh, defeatMenaphiteAkh, finishQuest), Arrays.asList(meleeCombatGear, waterskins)));

		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.CONTACT, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.AGILITY, 62));
		req.add(new SkillRequirement(Skill.CRAFTING, 55));
		req.add(new SkillRequirement(Skill.FIREMAKING, 55));
		return req;
	}
}
