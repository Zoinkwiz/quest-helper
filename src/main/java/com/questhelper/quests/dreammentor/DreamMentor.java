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
package com.questhelper.quests.dreammentor;

import com.questhelper.BankSlotIcons;
import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.NpcCondition;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.WidgetTextCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;

@QuestDescriptor(
	quest = QuestHelperQuest.DREAM_MENTOR
)
public class DreamMentor extends BasicQuestHelper
{
	ItemRequirement sealOfPassage, dreamVial, astralRune, astralRuneShards, groundAstralRune, dreamVialWater, dreamVialWithGoutweed,
		pestleAndMortar, dreamPotion, foodAll, food4, food6, goutweed, tinderbox, hammer, combatGear, food14, chest;

	ConditionForStep inLunarMine, inCyrisusRoom, at40Health, at70Health, lookingAtBank, gotItems, cyrisusDressed, at100Health, hasVialWater, hasVialGout,
		hasAstralShard, hasAstralPowder, hasDreamPotion, litBrazier, inArena, unlockedDream, inadaquacyNearby, everlastingNearby,
		untouchableNearby, illusiveNearby;

	QuestStep goDownToCyrisus, enterCyrisusCave, talkToCyrisus, feed4Food, talkToCyrisus2, feed4Food2, talkToCyrisus3, feed6Food, talkToCyrisus4,
		leaveCave, goUpToSurface, talkToJack, selectEquipment, goBackDownToCyrisus, enterCyrisusCaveAgain, giveCyrisusGear, useFood3, goBackDownAfterGearing,
		talkAfterHelping, supportCyrisusToRecovery, talkToOneiromancer, fillVialWithWater, addGoutweed, useHammerOnAstralRune, usePestleOnShards,
		useGroundAstralOnVial, lightBrazier, talkToCyrisusForDream, killInadaquacy, killEverlasting, killUntouchable, killIllusive, returnToOneiromancer;

	Zone lunarMine, cyrisusRoom, arena;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep startQuest = new ConditionalStep(this, goDownToCyrisus);
		startQuest.addStep(inCyrisusRoom, talkToCyrisus);
		startQuest.addStep(inLunarMine, enterCyrisusCave);
		steps.put(0, startQuest);
		steps.put(2, startQuest);

		ConditionalStep firstFeeding = new ConditionalStep(this, goDownToCyrisus);
		firstFeeding.addStep(inCyrisusRoom, feed4Food);
		firstFeeding.addStep(inLunarMine, enterCyrisusCave);
		steps.put(4, firstFeeding);

		ConditionalStep talkToCyrisusSteps = new ConditionalStep(this, goDownToCyrisus);
		talkToCyrisusSteps.addStep(inCyrisusRoom, talkToCyrisus2);
		talkToCyrisusSteps.addStep(inLunarMine, enterCyrisusCave);
		steps.put(6, talkToCyrisusSteps);

		ConditionalStep feedPhase2 = new ConditionalStep(this, goDownToCyrisus);
		feedPhase2.addStep(new Conditions(inCyrisusRoom, at40Health), talkToCyrisus3);
		feedPhase2.addStep(inCyrisusRoom, feed4Food2);
		feedPhase2.addStep(inLunarMine, enterCyrisusCave);
		steps.put(8, feedPhase2);
		steps.put(10, feedPhase2);

		ConditionalStep feedPhase3 = new ConditionalStep(this, goDownToCyrisus);
		feedPhase3.addStep(new Conditions(inCyrisusRoom, at70Health), talkToCyrisus4);
		feedPhase3.addStep(inCyrisusRoom, feed6Food);
		feedPhase3.addStep(inLunarMine, enterCyrisusCave);
		steps.put(12, feedPhase3);
		steps.put(14, feedPhase3);

		ConditionalStep goGetArmour = new ConditionalStep(this, talkToJack);
		goGetArmour.addStep(new Conditions(inCyrisusRoom, cyrisusDressed, at100Health), supportCyrisusToRecovery);
		goGetArmour.addStep(new Conditions(inLunarMine, cyrisusDressed, at100Health), enterCyrisusCaveAgain);
		goGetArmour.addStep(new Conditions(cyrisusDressed, at100Health), goBackDownAfterGearing);

		goGetArmour.addStep(new Conditions(inCyrisusRoom, cyrisusDressed), useFood3);
		goGetArmour.addStep(new Conditions(inLunarMine, cyrisusDressed), enterCyrisusCaveAgain);
		goGetArmour.addStep(new Conditions(cyrisusDressed), goBackDownAfterGearing);

		goGetArmour.addStep(new Conditions(inCyrisusRoom, gotItems), giveCyrisusGear);
		goGetArmour.addStep(new Conditions(inLunarMine, gotItems), enterCyrisusCaveAgain);
		goGetArmour.addStep(gotItems, goBackDownToCyrisus);
		goGetArmour.addStep(lookingAtBank, selectEquipment);
		goGetArmour.addStep(inCyrisusRoom, leaveCave);
		goGetArmour.addStep(inLunarMine, goUpToSurface);
		steps.put(16, goGetArmour);

		steps.put(18, talkAfterHelping);

		steps.put(20, talkToOneiromancer);
		steps.put(22, talkToOneiromancer);

		ConditionalStep enterDream = new ConditionalStep(this, fillVialWithWater);
		enterDream.addStep(new Conditions(inArena, illusiveNearby), killIllusive);
		enterDream.addStep(new Conditions(inArena, untouchableNearby), killUntouchable);
		enterDream.addStep(new Conditions(inArena, everlastingNearby), killEverlasting);
		enterDream.addStep(new Conditions(inArena, inadaquacyNearby), killInadaquacy);
		enterDream.addStep(inArena, killIllusive);
		enterDream.addStep(new Conditions(litBrazier, new Conditions(LogicType.OR, hasDreamPotion, unlockedDream)), talkToCyrisusForDream);
		enterDream.addStep(new Conditions(LogicType.OR, unlockedDream, hasDreamPotion), lightBrazier);
		enterDream.addStep(new Conditions(hasVialGout, hasAstralPowder), useGroundAstralOnVial);
		enterDream.addStep(new Conditions(hasVialGout, hasAstralShard), usePestleOnShards);
		enterDream.addStep(hasVialGout, useHammerOnAstralRune);
		enterDream.addStep(hasVialWater, addGoutweed);
		steps.put(24, enterDream);

		steps.put(26, returnToOneiromancer);

		return steps;
	}

	public void setupItemRequirements()
	{
		sealOfPassage = new ItemRequirement("Seal of passage", ItemID.SEAL_OF_PASSAGE);

		dreamVial = new ItemRequirement("Dream vial (empty)", ItemID.DREAM_VIAL_EMPTY);
		dreamVial.setHighlightInInventory(true);
		astralRune = new ItemRequirement("Astral rune", ItemID.ASTRAL_RUNE);
		astralRune.setHighlightInInventory(true);
		astralRuneShards = new ItemRequirement("Astral rune shards", ItemID.ASTRAL_RUNE_SHARDS);
		astralRuneShards.setHighlightInInventory(true);
		groundAstralRune = new ItemRequirement("Ground astral rune", ItemID.GROUND_ASTRAL_RUNE);
		groundAstralRune.setHighlightInInventory(true);
		dreamVialWater = new ItemRequirement("Dream vial (water)", ItemID.DREAM_VIAL_WATER);
		dreamVialWater.setHighlightInInventory(true);
		dreamVialWithGoutweed = new ItemRequirement("Dream vial (herb)", ItemID.DREAM_VIAL_HERB);
		dreamVialWithGoutweed.setHighlightInInventory(true);

		pestleAndMortar = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR);
		pestleAndMortar.setHighlightInInventory(true);

		hammer = new ItemRequirement("Hammer", ItemID.HAMMER);
		hammer.setHighlightInInventory(true);

		goutweed = new ItemRequirement("Goutweed", ItemID.GOUTWEED);
		goutweed.setTip("You can get one from the Troll Kitchen's Storeroom in Trollheim");
		goutweed.setHighlightInInventory(true);

		dreamPotion = new ItemRequirement("Dream potion", ItemID.DREAM_POTION);

		foodAll = new ItemRequirement("3 different types of food, 7 of two and 6 of one", -1, -1);
		foodAll.setDisplayItemId(ItemID.SHARK);
		food14 = new ItemRequirement("14 food, 5x of 2 different types of food, and 4x of another", -1, -1);
		food14.setDisplayItemId(ItemID.SHARK);
		food4 = new ItemRequirement("4 food, 1x of 2 different types of food, and 2x of another", -1, -1);
		food4.setDisplayItemId(ItemID.SHARK);
		food6 = new ItemRequirement("6 food, 2x of 3 different types of food", -1, -1);
		food6.setDisplayItemId(ItemID.SHARK);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);

		chest = new ItemRequirement("Cyrisus's chest", ItemID.CYRISUSS_CHEST);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
	}

	public void loadZones()
	{
		lunarMine = new Zone(new WorldPoint(2300, 10313, 2), new WorldPoint(2370, 10354, 2));
		cyrisusRoom = new Zone(new WorldPoint(2337, 10356, 2), new WorldPoint(2350, 10363, 2));
		arena = new Zone(new WorldPoint(1806, 5135, 2), new WorldPoint(1840, 5167, 2));
	}

	public void setupConditions()
	{
		inLunarMine = new ZoneCondition(lunarMine);
		inCyrisusRoom = new ZoneCondition(cyrisusRoom);
		inArena = new ZoneCondition(arena);

		at40Health = new VarbitCondition(3621, 40, Operation.GREATER_EQUAL);
		at70Health = new VarbitCondition(3621, 70, Operation.GREATER_EQUAL);
		at100Health = new VarbitCondition(3621, 100, Operation.GREATER_EQUAL);

		lookingAtBank = new WidgetTextCondition(260, 41, "Cyrisus's Bank");
		gotItems = new CyrisusBankConditional();
		cyrisusDressed = new VarbitCondition(3623, 100);

		hasVialWater = new ItemRequirementCondition(dreamVialWater);
		hasVialGout = new ItemRequirementCondition(dreamVialWithGoutweed);
		hasAstralShard = new ItemRequirementCondition(astralRuneShards);
		hasAstralPowder = new ItemRequirementCondition(groundAstralRune);
		hasDreamPotion = new ItemRequirementCondition(dreamPotion);

		litBrazier = new VarbitCondition(2430, 1);

		unlockedDream = new VarbitCondition(3625, 1);
		inadaquacyNearby = new NpcCondition(NpcID.THE_INADEQUACY);
		everlastingNearby = new NpcCondition(NpcID.THE_EVERLASTING);
		untouchableNearby = new NpcCondition(NpcID.THE_UNTOUCHABLE);
		illusiveNearby = new Conditions(LogicType.OR, new NpcCondition(NpcID.THE_ILLUSIVE), new NpcCondition(NpcID.THE_ILLUSIVE_3478));

		// 3634 = 1 at one
		// 3634 = 4 at brazier?
	}

	public void setupSteps()
	{
		goDownToCyrisus = new ObjectStep(this, ObjectID.LADDER_14996, new WorldPoint(2142, 3944, 0), "Enter the mine in the north east of Lunar Isle.", food14);
		enterCyrisusCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_11399, new WorldPoint(2335, 10346, 2), "Enter the cave entrance on the north wall.");
		talkToCyrisus = new NpcStep(this, NpcID.FALLEN_MAN, new WorldPoint(2346, 10360, 2), "Attempt to talk to the fallen man.");
		talkToCyrisus.addDialogStep("Yes");

		feed4Food = new NpcStep(this, NpcID.FALLEN_MAN, new WorldPoint(2346, 10360, 2), "Feed the fallen man 4 food. You'll need to alternate between at least 3 different types of food.", food4);

		talkToCyrisus2 = new NpcStep(this, NpcID.FALLEN_MAN_3466, new WorldPoint(2346, 10360, 2), "Talk to the fallen man.");
		((NpcStep)(talkToCyrisus2)).addAlternateNpcs(NpcID.FALLEN_MAN);
		feed4Food2 = new NpcStep(this, NpcID.FALLEN_MAN_3466, new WorldPoint(2346, 10360, 2), "Feed the fallen man 4 food. You'll need to alternate between at least 3 different types of food.", food4);

		// 3622 = spirit
		talkToCyrisus3 = new NpcStep(this, NpcID.FALLEN_MAN_3466, new WorldPoint(2346, 10360, 2), "Talk to the fallen man.");
		((NpcStep)(talkToCyrisus3)).addAlternateNpcs(NpcID.CYRISUS_3467);
		talkToCyrisus3.addDialogSteps("You're looking better now.", "Well, you look and sound more lively.");
		talkToCyrisus3.addDialogSteps("Are you looking forward to getting out?", "That's the spirit!");
		talkToCyrisus3.addDialogSteps("You seem like a nice guy.", "Just being honest.");
		talkToCyrisus3.addDialogSteps("When we get out of here I'll buy you a drink!", "Whatever and wherever you want - my treat.");
		talkToCyrisus3.addDialogSteps("I'm very impressed you managed to get into this cave.", "I would have given up personally.");
		talkToCyrisus3.addDialogSteps("You'll survive this easily.", "Think of all the places you can visit when you get out!");
		talkToCyrisus3.addDialogSteps("Just don't worry.", "Of course.");
		talkToCyrisus3.addDialogSteps("What are you going to do when you get out of here?", "That's up to you. You could travel with me!");
		talkToCyrisus3.addDialogSteps("It's a good thing you have me to look after you.", "Not that I'm bragging or anything.");
		talkToCyrisus3.addDialogSteps("Not long now and you'll be back on your feet!", "On whether you mind me helping you further.");
		talkToCyrisus3.addDialogSteps("You're sounding much better.", "If you need anything, just let me know.");
		talkToCyrisus3.addDialogSteps("It's quite cosy in here.", "The perfect environment for getting back on your feet!");
		talkToCyrisus3.addDialogSteps("You're very safe in this little cave.", "The suqah will never fit through that tunnel.");
		talkToCyrisus3.addDialogSteps("Tell me a bit about yourself.", "Fishing!");

		feed6Food = new NpcStep(this, NpcID.CYRISUS_3467, new WorldPoint(2346, 10360, 2), "Feed Cyrisus 6 food. You'll need to alternate between at least 3 different types of food.", food6);
		talkToCyrisus4 = new NpcStep(this, NpcID.CYRISUS_3467, new WorldPoint(2346, 10360, 2), "Talk to Cyrisus.");
		((NpcStep)(talkToCyrisus4)).addAlternateNpcs(NpcID.CYRISUS_3468);
		talkToCyrisus4.addDialogSteps("You're looking better now.", "Well, you look and sound more lively.");
		talkToCyrisus4.addDialogSteps("Are you looking forward to getting out?", "That's the spirit!");
		talkToCyrisus4.addDialogSteps("You seem like a nice guy.", "Just being honest.");
		talkToCyrisus4.addDialogSteps("When we get out of here I'll buy you a drink!", "Whatever and wherever you want - my treat.");
		talkToCyrisus4.addDialogSteps("I'm very impressed you managed to get into this cave.", "I would have given up personally.");
		talkToCyrisus4.addDialogSteps("You'll survive this easily.", "Think of all the places you can visit when you get out!");
		talkToCyrisus4.addDialogSteps("Just don't worry.", "Of course.");
		talkToCyrisus4.addDialogSteps("What are you going to do when you get out of here?", "That's up to you. You could travel with me!");
		talkToCyrisus4.addDialogSteps("It's a good thing you have me to look after you.", "Not that I'm bragging or anything.");
		talkToCyrisus4.addDialogSteps("Not long now and you'll be back on your feet!", "On whether you mind me helping you further.");
		talkToCyrisus4.addDialogSteps("You're sounding much better.", "If you need anything, just let me know.");
		talkToCyrisus4.addDialogSteps("It's quite cosy in here.", "The perfect environment for getting back on your feet!");
		talkToCyrisus4.addDialogSteps("You're very safe in this little cave.", "The suqah will never fit through that tunnel.");
		talkToCyrisus4.addDialogSteps("Tell me a bit about yourself.", "Fishing!");

		leaveCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_11399, new WorldPoint(2346, 10360, 2), "Talk to 'Bird's-Eye' Jack in the Lunar Isle bank.");
		goUpToSurface = new ObjectStep(this, ObjectID.LADDER_14995, new WorldPoint(2330, 10353, 2), "Talk to 'Bird's-Eye' Jack in the Lunar Isle bank.");
		selectEquipment = new SelectingCombatGear(this);
		talkToJack = new NpcStep(this, NpcID.BIRDSEYE_JACK, new WorldPoint(2099, 3921, 0), "Talk to 'Bird's-Eye' Jack in the Lunar Isle bank for Cyrisus's equipment.");
		talkToJack.addDialogStep("Cyrisus in the mine");
		talkToJack.addSubSteps(leaveCave, goUpToSurface, selectEquipment);
		goBackDownToCyrisus = new ObjectStep(this, ObjectID.LADDER_14996, new WorldPoint(2142, 3944, 0), "Enter the mine in the north east of Lunar Isle.", chest, food6);
		goBackDownAfterGearing = new ObjectStep(this, ObjectID.LADDER_14996, new WorldPoint(2142, 3944, 0), "Enter the mine in the north east of Lunar Isle.", food6);
		enterCyrisusCaveAgain = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_11399, new WorldPoint(2335, 10346, 2), "Enter the cave entrance on the north wall.");
		giveCyrisusGear = new NpcStep(this, NpcID.CYRISUS_3468, new WorldPoint(2346, 10360, 2), "Give Cyrisus his equipment.", chest);
		giveCyrisusGear.addDialogStep("Talk about the Armament");
		giveCyrisusGear.addSubSteps(goBackDownToCyrisus, enterCyrisusCaveAgain);

		useFood3 = new NpcStep(this, NpcID.CYRISUS_3468, new WorldPoint(2346, 10360, 2), "Feed Cyrisus alternating types of food.", food6);
		useFood3.addSubSteps(goBackDownAfterGearing);
		((NpcStep)(useFood3)).addAlternateNpcs(NpcID.CYRISUS_3469);
		((NpcStep)(useFood3)).addAlternateNpcs(NpcID.CYRISUS_3470);
		((NpcStep)(useFood3)).addAlternateNpcs(NpcID.CYRISUS_3471);

		supportCyrisusToRecovery = new NpcStep(this, NpcID.CYRISUS_3468, new WorldPoint(2346, 10360, 2), "Talk to Cyrisus until he's fully recovered.");
		((NpcStep)(supportCyrisusToRecovery)).addAlternateNpcs(NpcID.CYRISUS_3469);
		((NpcStep)(supportCyrisusToRecovery)).addAlternateNpcs(NpcID.CYRISUS_3470);
		((NpcStep)(supportCyrisusToRecovery)).addAlternateNpcs(NpcID.CYRISUS_3471);
		supportCyrisusToRecovery.addDialogSteps("You're looking better now.", "Well, you look and sound more lively.");
		supportCyrisusToRecovery.addDialogSteps("Are you looking forward to getting out?", "That's the spirit!");
		supportCyrisusToRecovery.addDialogSteps("You seem like a nice guy.", "Just being honest.");
		supportCyrisusToRecovery.addDialogSteps("When we get out of here I'll buy you a drink!", "Whatever and wherever you want - my treat.");
		supportCyrisusToRecovery.addDialogSteps("I'm very impressed you managed to get into this cave.", "I would have given up personally.");
		supportCyrisusToRecovery.addDialogSteps("You'll survive this easily.", "Think of all the places you can visit when you get out!");
		supportCyrisusToRecovery.addDialogSteps("Just don't worry.", "Of course.");
		supportCyrisusToRecovery.addDialogSteps("What are you going to do when you get out of here?", "That's up to you. You could travel with me!");
		supportCyrisusToRecovery.addDialogSteps("It's a good thing you have me to look after you.", "Not that I'm bragging or anything.");
		supportCyrisusToRecovery.addDialogSteps("Not long now and you'll be back on your feet!", "On whether you mind me helping you further.");
		supportCyrisusToRecovery.addDialogSteps("You're sounding much better.", "If you need anything, just let me know.");
		supportCyrisusToRecovery.addDialogSteps("It's quite cosy in here.", "The perfect environment for getting back on your feet!");
		supportCyrisusToRecovery.addDialogSteps("You're very safe in this little cave.", "The suqah will never fit through that tunnel.");
		supportCyrisusToRecovery.addDialogSteps("Tell me a bit about yourself.", "Fishing!");

		talkAfterHelping = new NpcStep(this, NpcID.CYRISUS_3468, new WorldPoint(2346, 10360, 2), "Talk to Cyrisus.");
		((NpcStep)(talkAfterHelping)).addAlternateNpcs(NpcID.CYRISUS_3469, NpcID.CYRISUS_3470, NpcID.CYRISUS_3471);
		supportCyrisusToRecovery.addSubSteps(talkAfterHelping);

		talkToOneiromancer = new NpcStep(this, NpcID.ONEIROMANCER, new WorldPoint(2151, 3867, 0), "Talk to the Oneiromancer in the south east of Lunar Isle.", sealOfPassage);
		talkToOneiromancer.addDialogStep("Cyrisus.");

		fillVialWithWater = new ObjectStep(this, ObjectID.SINK_16705, new WorldPoint(2091, 3922, 0), "Fill the vial with water.", dreamVial);
		fillVialWithWater.addIcon(ItemID.DREAM_VIAL_EMPTY);

		addGoutweed = new DetailedQuestStep(this, "Add a goutweed to the dream vial.", dreamVialWater, goutweed);
		useHammerOnAstralRune = new DetailedQuestStep(this, "Use a hammer on an astral rune.", hammer, astralRune);
		usePestleOnShards = new DetailedQuestStep(this, "Use a pestle and mortar on the astral rune shards.", pestleAndMortar, astralRuneShards);
		useGroundAstralOnVial = new DetailedQuestStep(this, "Add the ground astral rune to the dream vial.", groundAstralRune, dreamVialWithGoutweed);
		lightBrazier = new ObjectStep(this, NullObjectID.NULL_17025, new WorldPoint(2073, 3912, 0),
			"Equip your combat equipment, food, and light the Brazier in the west of Lunar Isle's town. The upcoming fight is hard, and you can only leave via the lecturn in the arena and cannot pray. Magic attacks are extremely effective for the fight.",
			sealOfPassage, tinderbox, combatGear);
		lightBrazier.addIcon(ItemID.TINDERBOX);

		talkToCyrisusForDream = new NpcStep(this, NpcID.CYRISUS_3468, new WorldPoint(2075, 3912, 0), "Talk to Cyrisus to enter the dream.", combatGear, sealOfPassage);
		((NpcStep)(talkToCyrisusForDream)).addAlternateNpcs(NpcID.CYRISUS_3469, NpcID.CYRISUS_3470, NpcID.CYRISUS_3471);
		talkToCyrisusForDream.addDialogStep("Yes, let's go!");
		killInadaquacy = new NpcStep(this, NpcID.THE_INADEQUACY, new WorldPoint(1824, 5150, 2), "Kill The Inadequacy.");
		killEverlasting = new NpcStep(this, NpcID.THE_EVERLASTING, new WorldPoint(1824, 5150, 2),"Kill The Everlasting. You can safe spot it by the entry book.");
		killUntouchable = new NpcStep(this, NpcID.THE_UNTOUCHABLE, new WorldPoint(1824, 5150, 2),"Kill The Untouchable. You can safe spot it by the entry book.");
		killIllusive = new NpcStep(this, NpcID.THE_ILLUSIVE, new WorldPoint(1824, 5150, 2),"Kill The Illusive.");
		returnToOneiromancer = new NpcStep(this, NpcID.ONEIROMANCER, new WorldPoint(2151, 3867, 0), "Talk to the Oneiromancer to finish the quest!", sealOfPassage);
		returnToOneiromancer.addDialogStep("Cyrisus.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(sealOfPassage, foodAll, goutweed, astralRune, hammer, pestleAndMortar, tinderbox, combatGear));
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Arrays.asList("The Inadequacy (level 343)", "The Everlasting (level 223, safespottable)", "The Untouchable (level 274, safespottable)", "The Illusive (level 108, won't attack you)"));
	}

	@Override
	public ArrayList<String> getNotes()
	{
		return new ArrayList<>(Collections.singletonList("You will need to fight all 4 bosses in a row without prayers. It's recommended that you use magic as they all have very low magic defence."));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Helping Cyrisus", new ArrayList<>(Arrays.asList(goDownToCyrisus, enterCyrisusCave,
			talkToCyrisus, feed4Food, talkToCyrisus2, feed4Food2, talkToCyrisus3, feed6Food, talkToCyrisus4, talkToJack, giveCyrisusGear,
			useFood3, supportCyrisusToRecovery)), foodAll));
		allSteps.add(new PanelDetails("Defeating his fear", new ArrayList<>(Arrays.asList(talkToOneiromancer, fillVialWithWater, addGoutweed,
			useHammerOnAstralRune, usePestleOnShards, useGroundAstralOnVial, lightBrazier, talkToCyrisusForDream, killInadaquacy, killEverlasting, killUntouchable, killIllusive, returnToOneiromancer)), goutweed, astralRune, hammer, pestleAndMortar, tinderbox, combatGear));
		return allSteps;
	}
}
