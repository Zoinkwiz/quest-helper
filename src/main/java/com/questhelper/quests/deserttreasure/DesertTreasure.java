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
package com.questhelper.quests.deserttreasure;

import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.NpcCondition;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullItemID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;

@QuestDescriptor(
	quest = QuestHelperQuest.DESERT_TREASURE
)
public class DesertTreasure extends BasicQuestHelper
{
	ItemRequirement coins650, magicLogs12, steelBars6, moltenGlass6, ashes, charcoal, bloodRune, bones, silverBar, garlicPowder, spice, cake, spikedBoots,
		climbingBoots, faceMask, tinderbox, manyLockpicks, etchings, translation, warmKey, smokeDiamond, shadowDiamond, iceDiamond, bloodDiamond, iceGloves,
		waterSpellOrMelee, cross, ringOfVisibility, antipoison, silverPot, silverPot2, potOfBlood, potWithGarlic, potWithSpice, potComplete, fireSpells,
		spikedBootsEquipped, combatGear, food, prayerPotions, restorePotions, energyOrStaminas;

	ConditionForStep hasWarmKey, gotBloodDiamond, hadSmokeDiamond, gotIceDiamond, killedDamis, inSmokeDungeon, inFareedRoom, litTorch1, litTorch2, litTorch3,
		litTorch4, unlockedFareedDoor, killedFareed, talkedToRasolo, unlockedCrossChest, gotRing, inShadowDungeon, damis1Nearby, damis2Nearby, talkedToMalak,
		askedAboutKillingDessous, hasSilverPot, hasSilverPot2, inDraynorSewer, hasSilverPotBlood, hasSilverPotGarlic, hasSilverPotComplete, hasSilverPotSpice,
		dessousNearby, killedDessous, gaveCake, talkedToTrollChild, killedTrolls, inTrollArea, inPath, killedKamil, onIcePath, onIceBridge, smashedIce1,
		freedTrolls, placedBlood, placedIce, placedSmoke, placedShadow, inFloor1, inFloor2, inFloor3, inFloor4, inAzzRoom;

	QuestStep talkToArchaeologist, talkToExpert, talkToExpertAgain, bringTranslationToArchaeologist, talkToArchaeologistAgainAfterTranslation,
		buyDrink, talkToBartender, talkToEblis, bringItemsToEblis, talkToEblisAtMirrors, enterSmokeDungeon, lightTorch1, lightTorch2, lightTorch3,
		lightTorch4, openChest, useWarmKey, enterFareedRoom, killFareed, talkToRasolo, giveCakeToTroll, talkToMalak, askAboutKillingDessous,
		talkToRuantun, blessPot, talkToMalakWithPot, addSpice, addPowder, addPowderToFinish, usePotOnGrave, killDessous, talkToMalakForDiamond,
		getCross, returnCross, enterShadowDungeon, waitForDamis, killDamis1, killDamis2, talkToChildTroll, enterIceGate, enterTrollCave,
		killKamil, climbOnToLedge, goThroughPathGate, breakIce1, breakIce2, talkToTrolls, talkToChildTrollAfterFreeing, placeBlood, placeShadow,
		placeSmoke, placeIce, enterPyramid, goDownFromFirstFloor, goDownFromSecondFloor, goDownFromThirdFloor, enterMiddleOfPyramid, talkToAzz;

	ObjectStep enterSewer;

	NpcStep killIceTrolls;

	ConditionalStep getSmokeDiamond, getBloodDiamond, getIceDiamond, getShadowDiamond, getDiamonds;

	Zone smokeDungeon, fareedRoom, shadowDungeon, draynorSewer, trollArea, path1, path2, icePath, iceBridge, floor1, floor2, floor3, floor4, azzRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToArchaeologist);
		steps.put(1, talkToExpert);
		steps.put(2, talkToExpertAgain);
		steps.put(3, talkToExpertAgain);
		steps.put(4, bringTranslationToArchaeologist);
		steps.put(5, talkToArchaeologistAgainAfterTranslation);
		steps.put(6, buyDrink);
		steps.put(7, talkToBartender);
		steps.put(8, talkToEblis);
		steps.put(9, bringItemsToEblis);
		steps.put(10, talkToEblisAtMirrors);

		getSmokeDiamond = new ConditionalStep(this, enterSmokeDungeon);
		getSmokeDiamond.addStep(new Conditions(inFareedRoom), killFareed);
		getSmokeDiamond.addStep(new Conditions(inSmokeDungeon, unlockedFareedDoor), enterFareedRoom);
		getSmokeDiamond.addStep(new Conditions(inSmokeDungeon, hasWarmKey), useWarmKey);
		getSmokeDiamond.addStep(new Conditions(inSmokeDungeon, litTorch1, litTorch2, litTorch3, litTorch4), openChest);
		getSmokeDiamond.addStep(new Conditions(inSmokeDungeon, litTorch1, litTorch2, litTorch3), lightTorch4);
		getSmokeDiamond.addStep(new Conditions(inSmokeDungeon, litTorch1, litTorch2), lightTorch3);
		getSmokeDiamond.addStep(new Conditions(inSmokeDungeon, litTorch1), lightTorch2);
		getSmokeDiamond.addStep(inSmokeDungeon, lightTorch1);

		getSmokeDiamond.setLockingCondition(killedFareed);

		getShadowDiamond = new ConditionalStep(this, talkToRasolo);
		getShadowDiamond.addStep(damis2Nearby, killDamis2);
		getShadowDiamond.addStep(damis1Nearby, killDamis1);
		getShadowDiamond.addStep(inShadowDungeon, waitForDamis);
		getShadowDiamond.addStep(gotRing, enterShadowDungeon);
		getShadowDiamond.addStep(unlockedCrossChest, returnCross);
		getShadowDiamond.addStep(talkedToRasolo, getCross);
		getShadowDiamond.setLockingCondition(killedDamis);

		getBloodDiamond = new ConditionalStep(this, talkToMalak);
		getBloodDiamond.addStep(killedDessous, talkToMalakForDiamond);
		getBloodDiamond.addStep(dessousNearby, killDessous);
		getBloodDiamond.addStep(hasSilverPotComplete, usePotOnGrave);
		getBloodDiamond.addStep(hasSilverPotGarlic, addSpice);
		getBloodDiamond.addStep(hasSilverPotSpice, addPowderToFinish);
		getBloodDiamond.addStep(hasSilverPotBlood, addPowder);
		getBloodDiamond.addStep(hasSilverPot2, talkToMalakWithPot);
		getBloodDiamond.addStep(hasSilverPot, blessPot);
		getBloodDiamond.addStep(new Conditions(askedAboutKillingDessous, inDraynorSewer), talkToRuantun);
		getBloodDiamond.addStep(askedAboutKillingDessous, enterSewer);
		getBloodDiamond.addStep(talkedToMalak, askAboutKillingDessous);
		getBloodDiamond.setLockingCondition(gotBloodDiamond);

		getIceDiamond = new ConditionalStep(this, giveCakeToTroll);
		getIceDiamond.addStep(new Conditions(onIceBridge, freedTrolls), talkToTrolls);
		getIceDiamond.addStep(new Conditions(freedTrolls), talkToChildTrollAfterFreeing);
		getIceDiamond.addStep(new Conditions(onIceBridge, killedKamil, smashedIce1), breakIce2);
		getIceDiamond.addStep(new Conditions(onIceBridge, killedKamil), breakIce1);
		getIceDiamond.addStep(new Conditions(onIcePath, killedKamil), goThroughPathGate);
		getIceDiamond.addStep(new Conditions(inPath, killedKamil), climbOnToLedge);
		getIceDiamond.addStep(inPath, killKamil);
		getIceDiamond.addStep(new Conditions(killedTrolls, inTrollArea), enterTrollCave);
		getIceDiamond.addStep(inTrollArea, killIceTrolls);
		getIceDiamond.addStep(talkedToTrollChild, enterIceGate);
		getIceDiamond.addStep(gaveCake, talkToChildTroll);
		getIceDiamond.setLockingCondition(gotIceDiamond);

		getDiamonds = new ConditionalStep(this, getSmokeDiamond);
		getDiamonds.addStep(new Conditions(hadSmokeDiamond, killedDamis, gotBloodDiamond), getIceDiamond);
		getDiamonds.addStep(new Conditions(hadSmokeDiamond, killedDamis), getBloodDiamond);
		getDiamonds.addStep(killedFareed, getShadowDiamond);

		steps.put(11, getDiamonds);

		ConditionalStep placeDiamonds = new ConditionalStep(this, placeBlood);
		placeDiamonds.addStep(new Conditions(placedBlood, placedSmoke, placedIce), placeShadow);
		placeDiamonds.addStep(new Conditions(placedBlood, placedSmoke), placeIce);
		placeDiamonds.addStep(placedBlood, placeSmoke);

		steps.put(12, placeDiamonds);

		ConditionalStep finishQuest = new ConditionalStep(this, enterPyramid);
		finishQuest.addStep(inAzzRoom, talkToAzz);
		finishQuest.addStep(inFloor4, enterMiddleOfPyramid);
		finishQuest.addStep(inFloor3, goDownFromThirdFloor);
		finishQuest.addStep(inFloor2, goDownFromSecondFloor);
		finishQuest.addStep(inFloor1, goDownFromFirstFloor);

		steps.put(13, finishQuest);
		steps.put(14, finishQuest);

		return steps;
	}

	public void setupItemRequirements()
	{
		coins650 = new ItemRequirement("Coins", ItemID.COINS_995, 650);
		magicLogs12 = new ItemRequirement("Magic logs", ItemID.MAGIC_LOGS, 12);
		magicLogs12.addAlternates(NullItemID.NULL_1514);
		steelBars6 = new ItemRequirement("Steel bar", ItemID.STEEL_BAR,  6);
		steelBars6.addAlternates(NullItemID.NULL_2354);
		moltenGlass6 = new ItemRequirement("Molten glass", ItemID.MOLTEN_GLASS, 6);
		moltenGlass6.addAlternates(NullItemID.NULL_1776);
		ashes = new ItemRequirement("Ashes", ItemID.ASHES);
		charcoal = new ItemRequirement("Charcoal", ItemID.CHARCOAL);
		bloodRune = new ItemRequirement("Blood rune", ItemID.BLOOD_RUNE);
		bones = new ItemRequirement("Bones", ItemID.BONES);
		silverBar = new ItemRequirement("Silver bar", ItemID.SILVER_BAR);
		garlicPowder = new ItemRequirement("Garlic powder", ItemID.GARLIC_POWDER);
		garlicPowder.setHighlightInInventory(true);
		garlicPowder.setTip("Use a pestle and mortar on a garlic to make powder");
		spice = new ItemRequirement("Spice", ItemID.SPICE);
		spice.setHighlightInInventory(true);

		cake = new ItemRequirement("Cake", ItemID.CAKE);
		cake.setHighlightInInventory(true);
		cake.addAlternates(ItemID.CHOCOLATE_CAKE);
		spikedBoots = new ItemRequirement("Spiked boots", ItemID.SPIKED_BOOTS);
		spikedBoots.setTip("Bring Dunstan in Burthorpe climbing boots and an iron bar to make these");

		spikedBootsEquipped = new ItemRequirement("Spiked boots", ItemID.SPIKED_BOOTS, 1, true);
		spikedBootsEquipped.setTip("Bring Dunstan in Burthorpe climbing boots and an iron bar to make these");

		climbingBoots = new ItemRequirement("Climbing boots", ItemID.CLIMBING_BOOTS);
		faceMask = new ItemRequirement("Facemask, slayer helmet or gas mask", ItemID.FACEMASK, 1, true);
		faceMask.addAlternates(ItemID.SLAYER_HELMET, ItemID.GAS_MASK);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		manyLockpicks = new ItemRequirement("Many lockpicks", ItemID.LOCKPICK, -1);
		etchings = new ItemRequirement("Etchings", ItemID.ETCHINGS);
		etchings.setTip("You can get another from the Archaeologist in the Bedabin Camp");
		translation = new ItemRequirement("Translation", ItemID.TRANSLATION);
		translation.setTip("You can get another from the Archaeological expert in the Exam Centre");

		warmKey = new ItemRequirement("Warm key", ItemID.WARM_KEY);
		warmKey.setHighlightInInventory(true);

		smokeDiamond = new ItemRequirement("Smoke diamond", ItemID.SMOKE_DIAMOND);
		smokeDiamond.setTip("You can get another from the room you killed Fareed in inside the Smoke Dungeon");
		shadowDiamond = new ItemRequirement("Shadow diamond", ItemID.SHADOW_DIAMOND);
		shadowDiamond.setTip("You can get another from the east room of the Shadow Dungeon");

		iceDiamond = new ItemRequirement("Ice diamond", ItemID.ICE_DIAMOND);
		iceDiamond.setTip("You can get another from the Troll Child north of Trollheim");
		bloodDiamond = new ItemRequirement("Blood diamond", ItemID.BLOOD_DIAMOND);
		bloodDiamond.setTip("You can get another from Malak in Canifis");

		iceGloves = new ItemRequirement("Ice gloves", ItemID.ICE_GLOVES, 1, true);
		iceGloves.setTip("You can kill the Ice Queen under White Wolf Mountain for these");

		waterSpellOrMelee = new ItemRequirement("Water spells or melee gear", -1, -1);

		cross = new ItemRequirement("Gilded cross", ItemID.GILDED_CROSS);
		cross.setTip("You can get another from the chest in the south of the Bandit Camp");

		ringOfVisibility = new ItemRequirement("Ring of visibility", ItemID.RING_OF_VISIBILITY, 1, true);
		ringOfVisibility.setTip("You can get another from Rasolo south of Baxtorian Falls");

		antipoison = new ItemRequirement("Antipoisons", ItemID.ANTIPOISON3);

		silverPot = new ItemRequirement("Silver pot", ItemID.SILVER_POT_4658);
		silverPot2 = new ItemRequirement("Blessed pot", ItemID.BLESSED_POT);
		potOfBlood = new ItemRequirement("Blessed pot", ItemID.BLESSED_POT_4661);
		potOfBlood.setHighlightInInventory(true);
		potWithGarlic = new ItemRequirement("Blessed pot", ItemID.BLESSED_POT_4663);
		potWithGarlic.setHighlightInInventory(true);
		potWithSpice = new ItemRequirement("Blessed pot", ItemID.BLESSED_POT_4667);
		potWithSpice.setHighlightInInventory(true);

		potComplete = new ItemRequirement("Blessed pot", ItemID.BLESSED_POT_4665);
		potComplete.setHighlightInInventory(true);

		fireSpells = new ItemRequirement("Fire spells", -1, -1);

		combatGear = new ItemRequirement("Decent combat gear",-1, -1);
		food = new ItemRequirement("Food",-1, -1);
		prayerPotions = new ItemRequirement("Prayer potions",-1, -1);
		restorePotions = new ItemRequirement("Restore potions",-1, -1);
		energyOrStaminas = new ItemRequirement("Energy/Stamina potions",-1, -1);
	}

	public void loadZones()
	{
		smokeDungeon = new Zone(new WorldPoint(3199, 9345, 0), new WorldPoint(3328, 9412, 0));
		fareedRoom = new Zone(new WorldPoint(3305, 9360, 0), new WorldPoint(3326, 9393, 0));
		shadowDungeon = new Zone(new WorldPoint(2624, 5051, 0), new WorldPoint(2757, 5125, 0));
		draynorSewer = new Zone(new WorldPoint(3078, 9641, 0), new WorldPoint(3129, 9699, 0));
		trollArea = new Zone(new WorldPoint(2839, 3716, 0), new WorldPoint(2868, 3741, 0));
		path1 = new Zone(new WorldPoint(2872, 3714, 0), new WorldPoint(2903, 3771, 0));
		path2 = new Zone(new WorldPoint(2817, 3748, 0), new WorldPoint( 2892, 3869, 0));
		icePath = new Zone(new WorldPoint(2830, 3785, 1), new WorldPoint(2870, 3825, 1));
		iceBridge = new Zone(new WorldPoint(2823, 3807, 2), new WorldPoint(2855, 3812, 2));
		floor1 = new Zone(new WorldPoint(2893, 4944, 3), new WorldPoint(2931, 4973, 3));
		floor2 = new Zone(new WorldPoint(2823, 4936, 2), new WorldPoint(2874, 4977, 2));

		floor3 = new Zone(new WorldPoint(2758, 4935, 1), new WorldPoint(2811, 4980, 1));
		floor4 = new Zone(new WorldPoint(3186, 9269, 0), new WorldPoint(3266, 9339, 0));
		azzRoom = new Zone(new WorldPoint(3227, 9310, 0), new WorldPoint(3239, 9323, 0));
	}

	public void setupConditions()
	{
		// Given all items, 392 = 1;
		hasWarmKey = new ItemRequirementCondition(warmKey);
		killedDamis = new VarbitCondition(383, 5);
		hadSmokeDiamond = new Conditions(true, LogicType.OR, new ItemRequirementCondition(smokeDiamond));
		gotIceDiamond = new Conditions(true, LogicType.OR, new ItemRequirementCondition(iceDiamond));
		gotBloodDiamond = new VarbitCondition(373, 4);
		inSmokeDungeon = new ZoneCondition(smokeDungeon);
		inFareedRoom = new ZoneCondition(fareedRoom);
		litTorch1 = new VarbitCondition(360, 1);
		litTorch2 = new VarbitCondition(361, 1);
		litTorch3 = new VarbitCondition(363, 1);
		litTorch4 = new VarbitCondition(362, 1);
		unlockedFareedDoor = new VarbitCondition(386, 1);
		killedFareed = new VarbitCondition(376, 1);
		talkedToRasolo = new VarbitCondition(383, 2);
		gotRing = new VarbitCondition(383, 3, Operation.GREATER_EQUAL);

		unlockedCrossChest = new VarbitCondition(384, 1);

		inShadowDungeon = new ZoneCondition(shadowDungeon);

		damis1Nearby = new NpcCondition(NpcID.DAMIS);
		damis2Nearby = new NpcCondition(NpcID.DAMIS_683);

		talkedToMalak = new VarbitCondition(373, 1);
		askedAboutKillingDessous = new VarbitCondition(373, 2);

		hasSilverPot = new ItemRequirementCondition(silverPot);
		hasSilverPot2 = new ItemRequirementCondition(silverPot2);

		hasSilverPotBlood = new ItemRequirementCondition(potOfBlood);
		hasSilverPotGarlic = new ItemRequirementCondition(potWithGarlic);
		hasSilverPotSpice = new ItemRequirementCondition(potWithSpice);
		hasSilverPotComplete = new ItemRequirementCondition(potComplete);

		inDraynorSewer = new ZoneCondition(draynorSewer);

		dessousNearby = new NpcCondition(NpcID.DESSOUS);

		killedDessous = new VarbitCondition(373, 3);

		gaveCake = new VarbitCondition(382, 1);
		talkedToTrollChild = new VarbitCondition(382, 2, Operation.GREATER_EQUAL);
		// Killed kamil also results in 377 0->1
		killedKamil = new VarbitCondition(382, 3, Operation.GREATER_EQUAL);
		freedTrolls = new VarbitCondition(382, 4);
		gotIceDiamond = new VarbitCondition(382, 5);

		killedTrolls = new VarbitCondition(378, 5);

		inTrollArea = new ZoneCondition(trollArea);
		inPath = new ZoneCondition(path1, path2);
		onIcePath = new ZoneCondition(icePath);
		onIceBridge = new ZoneCondition(iceBridge);

		smashedIce1 = new VarbitCondition(380, 1);

		placedSmoke = new VarbitCondition(387, 1);
		placedShadow = new VarbitCondition(388, 1);
		placedIce = new VarbitCondition(389, 1);
		placedBlood = new VarbitCondition(390, 1);

		inFloor1 = new ZoneCondition(floor1);
		inFloor2 = new ZoneCondition(floor2);
		inFloor3 = new ZoneCondition(floor3);
		inFloor4 = new ZoneCondition(floor4);
		inAzzRoom = new ZoneCondition(azzRoom);
	}

	public void setupSteps()
	{
		talkToArchaeologist = new NpcStep(this, NpcID.ARCHAEOLOGIST, new WorldPoint(3177, 3043, 0), "Talk to the Archaeologist in the Bedabin Camp.");
		talkToArchaeologist.addDialogStep("Do you have any quests?");
		talkToArchaeologist.addDialogStep("Yes, I'll help you.");

		talkToExpert = new NpcStep(this, NpcID.ARCHAEOLOGICAL_EXPERT, new WorldPoint(3359, 3334, 0), "Talk to the Archaeological Expert in the Exam Centre east of Varrock.", etchings);
		talkToExpert.addDialogStep("Ask about the Desert Treasure quest.");

		talkToExpertAgain = new NpcStep(this, NpcID.ARCHAEOLOGICAL_EXPERT, new WorldPoint(3359, 3334, 0), "Talk to the Archaeological Expert again.");
		talkToExpertAgain.addDialogStep("Ask about the Desert Treasure quest.");

		bringTranslationToArchaeologist = new NpcStep(this, NpcID.ARCHAEOLOGIST, new WorldPoint(3177, 3043, 0),
			"Bring the translation to the Archaeologist in the Bedabin Camp.", translation);
		talkToArchaeologistAgainAfterTranslation = new NpcStep(this, NpcID.ARCHAEOLOGIST, new WorldPoint(3177, 3043, 0), "Talk to the Archaeologist again.");
		talkToArchaeologistAgainAfterTranslation.addDialogStep("Help him");
		buyDrink = new NpcStep(this, NpcID.BARTENDER, new WorldPoint(3159, 2978, 0), "Buy a drink from the pub in the Bandit Camp, then talk to the Bartender again.", coins650);
		buyDrink.addDialogStep("Buy a drink");
		buyDrink.addDialogStep("Buy a beer");
		talkToBartender = new NpcStep(this, NpcID.BARTENDER, new WorldPoint(3159, 2978, 0), "Talk to the bartender in the Bandit Camp again.");
		talkToBartender.addDialogStep("I heard about four diamonds...");

		talkToEblis = new NpcStep(this, NpcID.EBLIS, new WorldPoint(3184, 2989, 0), "Talk to Eblis in the east of the Bandit Camp.");
		talkToEblis.addDialogStep("Tell me of The four diamonds of Azzanadra");
		talkToEblis.addDialogStep("Yes");

		bringItemsToEblis = new GiveItems(this, NpcID.EBLIS, new WorldPoint(3184, 2989, 0), "Bring the required items to Eblis in the east of the Bandit Camp.", ashes, bloodRune, bones, charcoal, moltenGlass6, magicLogs12, steelBars6);

		talkToEblisAtMirrors = new NpcStep(this, NpcID.EBLIS_689, new WorldPoint(3214, 2954, 0), "Talk to Eblis at the mirrors south east of the Bandit Camp.");

		enterSmokeDungeon = new ObjectStep(this, ObjectID.SMOKEY_WELL, new WorldPoint(3310, 2962, 0),
			"Enter the smokey well west of Pollnivneach. You'll need to run a lot, so bring energy/stamina potions if you can.", tinderbox, faceMask, iceGloves, waterSpellOrMelee);
		lightTorch1 = new ObjectStep(this, NullObjectID.NULL_6405, new WorldPoint(3323, 9398, 0),
			"Light all the torches in the corners of the dungeon. This is timed, so try to do it as fast as possible. Start with the north east torch, and work your way to the south west.", tinderbox);
		lightTorch1.addIcon(ItemID.TINDERBOX);

		lightTorch2 = new ObjectStep(this, NullObjectID.NULL_6407, new WorldPoint(3321, 9355, 0), "Light all the torches in the corners of the dungeon.", tinderbox);
		lightTorch2.addIcon(ItemID.TINDERBOX);

		lightTorch3 = new ObjectStep(this, NullObjectID.NULL_6411, new WorldPoint(3207, 9395, 0), "Light all the torches in the corners of the dungeon.", tinderbox);
		lightTorch3.addIcon(ItemID.TINDERBOX);

		lightTorch4 = new ObjectStep(this, NullObjectID.NULL_6409, new WorldPoint(3204, 9350, 0), "Light all the torches in the corners of the dungeon.", tinderbox);
		lightTorch4.addIcon(ItemID.TINDERBOX);

		lightTorch1.addSubSteps(lightTorch2, lightTorch3, lightTorch4);

		openChest = new ObjectStep(this, ObjectID.BURNT_CHEST, new WorldPoint(3248, 9364, 0), "Open the chest in the middle of the dungeon.");

		useWarmKey = new ObjectStep(this, ObjectID.GATE_6452, new WorldPoint(3305, 9376, 0),
			"Use the warm key on the gate in the east of the dungeon. Be prepared to fight Fareed. If you aren't wearing ice gloves he'll unequip your weapon.", warmKey, iceGloves, waterSpellOrMelee);
		useWarmKey.addIcon(ItemID.WARM_KEY);

		enterFareedRoom = new ObjectStep(this, ObjectID.GATE_6452, new WorldPoint(3305, 9376, 0),
			"Enter the gate in the east of the dungeon. Be prepared to fight Fareed. If you aren't wearing ice gloves he'll unequip your weapon.", iceGloves, waterSpellOrMelee);
		killFareed = new NpcStep(this, NpcID.FAREED, new WorldPoint(3315, 9375, 0), "Kill Fareed. Either use melee with ice gloves, or water spells.", iceGloves, waterSpellOrMelee);

		talkToRasolo = new NpcStep(this, NpcID.RASOLO, new WorldPoint(2531, 3420, 0), "Talk to Rasolo south of Baxtorian Falls.");
		talkToRasolo.addDialogStep("Ask about the Diamonds of Azzanadra");
		talkToRasolo.addDialogStep("Yes");

		getCross = new ObjectStep(this, ObjectID.SECURE_CHEST, new WorldPoint(3169, 2967, 0), "Bring antipoison, food, and as many picklocks as you can to the Bandit Camp, and try opening the chest in the south of the Bandit Camp. Keep trying until you succeed.", manyLockpicks, antipoison);

		talkToMalak = new NpcStep(this, NpcID.MALAK, new WorldPoint(3496, 3479, 0), "Talk to Malak in the pub in Canifis.");
		talkToMalak.addDialogStep("I am looking for a special Diamond...");
		talkToMalak.addDialogStep("Yes");

		askAboutKillingDessous = new NpcStep(this, NpcID.MALAK, new WorldPoint(3496, 3479, 0), "Ask Malek in the pub in Canifis how to kill Dessous.");
		askAboutKillingDessous.addDialogStep("How can I kill Dessous?");

		returnCross = new NpcStep(this, NpcID.RASOLO, new WorldPoint(2531, 3420, 0), "Return the cross to Rasolo south of Baxtorian Falls.", cross);

		enterShadowDungeon = new ObjectStep(this, NullObjectID.NULL_6560, new WorldPoint(2547, 3421, 0),
			"Equip the Ring of Visibility, then go down the ladder in the area east of Rasolo. It's recommended you bring combat gear to safe spot Damis.", ringOfVisibility);

		waitForDamis = new DetailedQuestStep(this, new WorldPoint(2745, 5115, 0), "Go to the far eastern room of the dungeon, and wait for Damis to spawn.");

		killDamis1 = new NpcStep(this, NpcID.DAMIS, new WorldPoint(2745, 5115, 0), "Kill both phases of Damis. He can be safe spotted.");
		killDamis2 = new NpcStep(this, NpcID.DAMIS_683, new WorldPoint(2745, 5115, 0), "Kill both phases of Damis. He can be safe spotted.");
		killDamis1.addSubSteps(killDamis2);

		enterSewer = new ObjectStep(this, ObjectID.TRAPDOOR_6434, new WorldPoint(3084, 3272, 0), "Bring a silver bar to Ruantun in Draynor Sewer.", silverBar);
		enterSewer.addAlternateObjects(ObjectID.TRAPDOOR_6435);
		enterSewer.addDialogStep("Actually, I don't need to know anything.");
		talkToRuantun = new NpcStep(this, NpcID.RUANTUN, new WorldPoint(3112, 9690, 0), "Bring a silver bar to Ruantun in Draynor Sewer.", silverBar);
		talkToRuantun.addSubSteps(enterSewer);

		blessPot = new NpcStep(this, NpcID.HIGH_PRIEST, new WorldPoint(2851, 3350, 0), "Travel to Entrana with the silver pot and have the High Priest enchant it.", silverPot);
		talkToMalakWithPot = new NpcStep(this, NpcID.MALAK, new WorldPoint(3496, 3479, 0), "Bring the blessed pot to Malak in the pub in Canifis. He'll bite you for a small bit of damage.", silverPot2);

		addSpice = new DetailedQuestStep(this, "Use the spice on the blessed pot.", potWithGarlic, spice);
		addPowder = new DetailedQuestStep(this, "Add the garlic powder to the blessed pot.", potOfBlood, garlicPowder);
		addPowderToFinish = new DetailedQuestStep(this, "Add the garlic powder to the blessed pot.", potWithSpice, garlicPowder);
		addPowder.addSubSteps(addPowderToFinish);

		usePotOnGrave = new ObjectStep(this, ObjectID.VAMPYRE_TOMB, new WorldPoint(3570, 3402, 0),
			"Use the blessed pot on the vampyre tomb in the graveyard south east of Canifis. Be prepared to fight Dessous.", potComplete);
		usePotOnGrave.addIcon(ItemID.BLESSED_POT_4665);
		killDessous = new NpcStep(this, NpcID.DESSOUS, new WorldPoint(3570, 3403, 0), "Kill Dessous.");

		talkToMalakForDiamond = new NpcStep(this, NpcID.MALAK, new WorldPoint(3496, 3479, 0), "Return to Malak in Canifis to get the Blood Diamond.");

		giveCakeToTroll = new NpcStep(this, NpcID.TROLL_CHILD_697, new WorldPoint(2835, 3740, 0), "Use a cake on the Troll Child north of Trollheim.", cake, spikedBoots);
		giveCakeToTroll.addIcon(ItemID.CAKE);
		talkToChildTroll = new NpcStep(this, NpcID.TROLL_CHILD, new WorldPoint(2835, 3740, 0), "Talk to the Troll Child north of Trollheim.");
		talkToChildTroll.addDialogStep("Yes");

		enterIceGate = new ObjectStep(this, ObjectID.ICE_GATE, new WorldPoint(2838, 3740, 0), "Enter the ice gate east of the troll child. Make sure you're prepared for combat, and your stats to be continually drained.", fireSpells, spikedBoots);
		killIceTrolls = new NpcStep(this, NpcID.ICE_TROLL_699, new WorldPoint(2854, 3733, 0), "Kill 5 ice trolls.", true);
		killIceTrolls.addAlternateNpcs(NpcID.ICE_TROLL_700, NpcID.ICE_TROLL_701, NpcID.ICE_TROLL_702, NpcID.ICE_TROLL_703, NpcID.ICE_TROLL_704, NpcID.ICE_TROLL_705);
		enterTrollCave = new ObjectStep(this, NullObjectID.NULL_6440, new WorldPoint(2869, 3719, 0), "Continue along the path through the cave to the east.");

		killKamil = new NpcStep(this, NpcID.KAMIL, new WorldPoint(2863, 3757, 0),
			"Continue along the path until you find Kamil. Kill him, preferably with fire spells. Get into melee distance and protect from melee.", fireSpells);
		climbOnToLedge = new ObjectStep(this, ObjectID.ICE_LEDGE, new WorldPoint(2837, 3804, 0), "Equip the spiked boots, then continue along the path until you reach an ice ledge. Climb up it.", spikedBootsEquipped);
		goThroughPathGate = new ObjectStep(this, ObjectID.ICE_GATE_6462, new WorldPoint(2854, 3811, 1), "Follow the Ice Path up to the top and enter the gate there.");
		breakIce1 = new NpcStep(this, NpcID.ICE_BLOCK, new WorldPoint(2826, 3808, 2), "Break the ice surrounding the trolls at the end of the path. Fire spells are effective for this.", fireSpells);
		breakIce2 = new NpcStep(this, NpcID.ICE_BLOCK_707, new WorldPoint(2826, 3812, 2), "Break the ice surrounding the trolls at the end of the path. Fire spells are effective for this.", fireSpells);
		talkToTrolls = new NpcStep(this, NpcID.TROLL_MOTHER, new WorldPoint(2826, 3812, 2), "Talk to the troll parents at the end of the Ice Path.");
		talkToChildTrollAfterFreeing = new NpcStep(this, NpcID.TROLL_CHILD, new WorldPoint(2835, 3740, 0), "Talk to the Troll Child north of Trollheim to get the ice diamond.");

		placeBlood = new ObjectStep(this, NullObjectID.NULL_6482, new WorldPoint(3221, 2910, 0), "Place all the diamonds in the obelisks around the pyrimid south east of the Bandit Camp.", bloodDiamond, smokeDiamond, iceDiamond, shadowDiamond);
		placeBlood.addIcon(ItemID.BLOOD_DIAMOND);

		placeSmoke = new ObjectStep(this, NullObjectID.NULL_6485, new WorldPoint(3245, 2910, 0), "Place all the diamonds in the obelisks around the pyrimid south east of the Bandit Camp.", smokeDiamond, iceDiamond, shadowDiamond);
		placeSmoke.addIcon(ItemID.SMOKE_DIAMOND);

		placeIce = new ObjectStep(this, NullObjectID.NULL_6488, new WorldPoint(3245, 2886, 0), "Place all the diamonds in the obelisks around the pyrimid south east of the Bandit Camp.", iceDiamond, shadowDiamond);
		placeIce.addIcon(ItemID.ICE_DIAMOND);

		placeShadow = new ObjectStep(this, NullObjectID.NULL_6491, new WorldPoint(3221, 2886, 0), "Place all the diamonds in the obelisks around the pyrimid south east of the Bandit Camp.", shadowDiamond);
		placeShadow.addIcon(ItemID.SHADOW_DIAMOND);

		placeBlood.addSubSteps(placeSmoke, placeShadow, placeIce);

		enterPyramid = new ObjectStep(this, ObjectID.LADDER_6497, new WorldPoint(3233, 2897, 0), "Bring any energy/stamina potions you have, some food, and enter the pyramid south east of the Bandit Camp.");

		goDownFromFirstFloor = new ObjectStep(this, ObjectID.LADDER_6498, new WorldPoint(2909, 4964, 3), "Go down to the bottom of the pyramid.");
		goDownFromSecondFloor = new ObjectStep(this, ObjectID.LADDER_6499, new WorldPoint(2846, 4973, 2), "Go down to the bottom of the pyramid.");
		goDownFromThirdFloor = new ObjectStep(this, ObjectID.LADDER_6500, new WorldPoint(2784, 4941, 1), "Go down to the bottom of the pyramid.");

		goDownFromFirstFloor.addSubSteps(goDownFromSecondFloor, goDownFromThirdFloor);

		enterMiddleOfPyramid = new ObjectStep(this, ObjectID.DOORWAY_6553, new WorldPoint(3234, 9324, 0), "Enter the central room of the bottom floor.");

		talkToAzz = new NpcStep(this, NpcID.AZZANADRA, new WorldPoint(3232, 9317, 0), "Talk to Azzanadra to finish the quest!");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(coins650, magicLogs12, steelBars6, moltenGlass6, ashes, charcoal,
			bloodRune, bones, silverBar, garlicPowder, spice, cake, spikedBoots, climbingBoots, faceMask, tinderbox, manyLockpicks));
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Arrays.asList(combatGear, food, prayerPotions, energyOrStaminas, restorePotions));
	}


	@Override
	public ArrayList<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Dessous (level 139)");
		reqs.add("Kamil (level 154)");
		reqs.add("Fareed (level 167)");
		reqs.add("Damis (level 103, then level 174 in second phase)");
		reqs.add("5 ice trolls (level 120-124)");
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off",
			new ArrayList<>(Arrays.asList(talkToArchaeologist, talkToExpert, talkToExpertAgain,
				bringTranslationToArchaeologist, talkToArchaeologistAgainAfterTranslation, buyDrink, talkToBartender, talkToEblis, bringItemsToEblis)), coins650, ashes, bloodRune, bones, charcoal, moltenGlass6, magicLogs12, steelBars6));

		PanelDetails smokeDiamondPanel = new PanelDetails("Smoke diamond",
			new ArrayList<>(Arrays.asList(enterSmokeDungeon, lightTorch1, openChest, useWarmKey, killFareed)), faceMask, tinderbox, iceGloves, waterSpellOrMelee, energyOrStaminas);
		smokeDiamondPanel.setLockingStep(getSmokeDiamond);

		PanelDetails shadowDiamondPanel = new PanelDetails("Shadow diamond",
			new ArrayList<>(Arrays.asList(talkToRasolo, getCross, returnCross, enterShadowDungeon, waitForDamis, killDamis1)), manyLockpicks, antipoison, combatGear, food);
		shadowDiamondPanel.setLockingStep(getShadowDiamond);

		PanelDetails bloodDiamondPanel = new PanelDetails("Blood diamond",
			new ArrayList<>(Arrays.asList(talkToMalak, askAboutKillingDessous, talkToRuantun, blessPot, talkToMalakWithPot, addPowder, addSpice, usePotOnGrave, killDessous, talkToMalakForDiamond)), silverBar, spice, garlicPowder, combatGear, food);
		bloodDiamondPanel.setLockingStep(getBloodDiamond);

		PanelDetails iceDiamondPanel = new PanelDetails("Ice diamond",
			new ArrayList<>(Arrays.asList(giveCakeToTroll, enterIceGate, killIceTrolls, enterTrollCave, killKamil, climbOnToLedge, goThroughPathGate, breakIce1, breakIce2, talkToTrolls, talkToChildTrollAfterFreeing)), cake, spikedBoots, combatGear, food, restorePotions, prayerPotions, energyOrStaminas);
		iceDiamondPanel.setLockingStep(getIceDiamond);

		PanelDetails finishingPanel = new PanelDetails("Freeing Azzanadra",
			new ArrayList<>(Arrays.asList(placeBlood, enterPyramid, goDownFromFirstFloor, enterMiddleOfPyramid, talkToAzz)), energyOrStaminas, food, prayerPotions);

		allSteps.add(smokeDiamondPanel);
		allSteps.add(shadowDiamondPanel);
		allSteps.add(bloodDiamondPanel);
		allSteps.add(iceDiamondPanel);
		allSteps.add(finishingPanel);
		return allSteps;
	}
}
