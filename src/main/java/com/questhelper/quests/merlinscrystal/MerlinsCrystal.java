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
package com.questhelper.quests.merlinscrystal;

import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.NpcCondition;
import com.questhelper.steps.conditional.ObjectCondition;
import com.questhelper.steps.conditional.WidgetTextCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.ZoneCondition;

@QuestDescriptor(
	quest = QuestHelperQuest.MERLINS_CRYSTAL
)
public class MerlinsCrystal extends BasicQuestHelper
{
	ItemRequirement bread, tinderbox, bucketOfWax, batBones, varrockTeleport, camelotTeleport, twoFaladorTeleports, combatGear, bucket, repellent, blackCandle,
		litBlackCandle, excalabur, batBonesOptional, bucketOfWaxOptional, equippedExcalabur;

	ConditionForStep inFaye, inFayeGround, inFaye1, inFaye2, inCamelot1, inCamelot2, morganNearby, hasBucket, hasRepellent, clearedHive, hasWax, hasBlackCandle,
		hasAnyBlackCandle, hasLitBlackCandle, beggarNearby, talkedToLady, hasExcalabur, hasReadSpell, inCamelot, inStar, thrantaxNearby, inCamelotTower1, inCamelotTower2;

	QuestStep startQuest, talkToGawain, goUpstairsInCamelot, talkToLancelot, goBackDownStairsCamelot, hideInArheinCrate, goToFirstFloor, goToSecondFloor,
		attackMordred, talkToMorgan, goToCatherbyAfterFortress, optionalGetRepellent, optionalGetBucket, optionalUseRepellent, talkToCandleMaker, talkToLadyOfLake,
		enterSarimShopAndTalk, talkToBeggar, goReadMagicWords, returnToCamelot, returnToCamelotLit, goStandInStar, lightCandle, dropBatBones, sayWords,
		goUpLadder1Camelot, goUpLadder2Camelot, smashCrystal, goDownLadder1Camelot, goDownLadder2Camelot, finishQuest;

	ConditionalStep getBlackCandle, getExcalabur;

	Zone fayeGround, faye1, faye2, camelotGround1, camelotGround2, camelotGround3, camelot1, camelot2, star, camelotTower1, camelotTower2;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, startQuest);
		steps.put(1, talkToGawain);

		ConditionalStep findLancelot = new ConditionalStep(this, goUpstairsInCamelot);
		findLancelot.addStep(inCamelot1, talkToLancelot);

		steps.put(2, findLancelot);

		ConditionalStep discoverHowToFreeMerlin = new ConditionalStep(this, hideInArheinCrate);
		discoverHowToFreeMerlin.addStep(new Conditions(inFaye2, morganNearby), talkToMorgan);
		discoverHowToFreeMerlin.addStep(inFaye2, attackMordred);
		discoverHowToFreeMerlin.addStep(inFaye1, goToSecondFloor);
		discoverHowToFreeMerlin.addStep(inFayeGround, goToFirstFloor);
		discoverHowToFreeMerlin.addStep(inCamelot1, goBackDownStairsCamelot);

		steps.put(3, discoverHowToFreeMerlin);

		getBlackCandle = new ConditionalStep(this, optionalGetRepellent);
		getBlackCandle.addStep(inFaye, goToCatherbyAfterFortress);
		getBlackCandle.addStep(hasWax, talkToCandleMaker);
		getBlackCandle.addStep(new Conditions(hasRepellent, hasBucket), optionalUseRepellent);
		getBlackCandle.addStep(hasRepellent, optionalGetBucket);
		getBlackCandle.setLockingCondition(hasAnyBlackCandle);

		getExcalabur = new ConditionalStep(this, talkToLadyOfLake);
		getExcalabur.addStep(beggarNearby, talkToBeggar);
		getExcalabur.addStep(talkedToLady, enterSarimShopAndTalk);
		getExcalabur.setLockingCondition(hasExcalabur);

		ConditionalStep performSpell = new ConditionalStep(this, returnToCamelot);
		performSpell.addStep(thrantaxNearby, sayWords);
		performSpell.addStep(new Conditions(inStar, hasLitBlackCandle), dropBatBones);
		performSpell.addStep(inStar, lightCandle);
		performSpell.addStep(inCamelot, goStandInStar);
		performSpell.addStep(hasLitBlackCandle, returnToCamelotLit);

		ConditionalStep completeAllTasks = new ConditionalStep(this, getBlackCandle);
		completeAllTasks.addStep(new Conditions(hasAnyBlackCandle, hasExcalabur, hasReadSpell), performSpell);
		completeAllTasks.addStep(new Conditions(hasAnyBlackCandle, hasExcalabur), goReadMagicWords);
		completeAllTasks.addStep(hasAnyBlackCandle, getExcalabur);

		steps.put(4, completeAllTasks);

		ConditionalStep goFreeMerlin = new ConditionalStep(this, goUpLadder1Camelot);
		goFreeMerlin.addStep(inCamelotTower2, smashCrystal);
		goFreeMerlin.addStep(inCamelotTower1, goUpLadder2Camelot);

		steps.put(5, goFreeMerlin);

		ConditionalStep goTellArthur = new ConditionalStep(this, finishQuest);
		goTellArthur.addStep(inCamelotTower1, goDownLadder1Camelot);
		goTellArthur.addStep(inCamelotTower2, goDownLadder2Camelot);

		steps.put(6, goTellArthur);

		return steps;
	}

	public void setupItemRequirements()
	{
		bread = new ItemRequirement("Bread", ItemID.BREAD);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		bucketOfWaxOptional = new ItemRequirement("Bucket of wax (obtainable during quest)", ItemID.BUCKET_OF_WAX);
		bucketOfWax = new ItemRequirement("Bucket of wax", ItemID.BUCKET_OF_WAX);
		batBones = new ItemRequirement("Bat bones", ItemID.BAT_BONES);
		batBonesOptional = new ItemRequirement("Bat bones (obtainable during quest)", ItemID.BAT_BONES);
		varrockTeleport = new ItemRequirement("Teleport to Varrock", ItemID.VARROCK_TELEPORT);
		camelotTeleport = new ItemRequirement("Teleport to Camelot", ItemID.CAMELOT_TELEPORT);
		twoFaladorTeleports = new ItemRequirement("Two teleports to Falador", ItemID.FALADOR_TELEPORT);
		combatGear = new ItemRequirement("Combat gear + food for Sir Mordred (level 39)", -1, -1);
		bucket = new ItemRequirement("Bucket", ItemID.BUCKET);
		repellent = new ItemRequirement("Insect repellent", ItemID.INSECT_REPELLENT);
		blackCandle = new ItemRequirement("Black candle", ItemID.BLACK_CANDLE);
		litBlackCandle = new ItemRequirement("Lit black candle", ItemID.LIT_BLACK_CANDLE);
		excalabur = new ItemRequirement("Excalabur", ItemID.EXCALIBUR);
		equippedExcalabur = new ItemRequirement("Excalabur", ItemID.EXCALIBUR, 1, true);
	}

	public void loadZones()
	{
		fayeGround = new Zone(new WorldPoint(2764, 3395, 0), new WorldPoint(2781, 3410, 0));
		faye1 = new Zone(new WorldPoint(2764, 3395, 1), new WorldPoint(2781, 3410, 1));
		faye2 = new Zone(new WorldPoint(2764, 3395, 2), new WorldPoint(2781, 3410, 2));
		camelot1 = new Zone(new WorldPoint(2744, 3483, 1), new WorldPoint(2769, 3517, 1));
		camelot2 = new Zone(new WorldPoint(2744, 3483, 2), new WorldPoint(2769, 3517, 2));
		camelotGround1 = new Zone(new WorldPoint(2744, 3483, 0), new WorldPoint(2774, 3517, 0));
		camelotGround2 = new Zone(new WorldPoint(2775, 3511, 0), new WorldPoint(2783, 3517, 0));
		camelotGround3 = new Zone(new WorldPoint(2774, 3505, 0), new WorldPoint(2776, 3511, 0));
		star = new Zone(new WorldPoint(2780, 3515, 0), new WorldPoint(2780, 3515, 0));
		camelotTower1 = new Zone(new WorldPoint(2765, 3490, 1), new WorldPoint(2770, 3495, 1));
		camelotTower2 = new Zone(new WorldPoint(2765, 3490, 2), new WorldPoint(2770, 3494, 2));
	}

	public void setupConditions()
	{
		inFaye = new ZoneCondition(faye1, fayeGround, faye2);
		inFayeGround = new ZoneCondition(fayeGround);
		inFaye1 = new ZoneCondition(faye1);
		inFaye2 = new ZoneCondition(faye2);
		inCamelot = new ZoneCondition(camelotGround1, camelotGround2, camelotGround3);
		inCamelot1 = new ZoneCondition(camelot1);
		inCamelot2 = new ZoneCondition(camelot2);
		morganNearby = new NpcCondition(NpcID.MORGAN_LE_FAYE);
		hasBucket = new ItemRequirementCondition(bucket);
		hasRepellent = new ItemRequirementCondition(repellent);
		clearedHive = new ObjectCondition(ObjectID.BEEHIVE_305);
		hasWax = new ItemRequirementCondition(bucketOfWax);
		hasBlackCandle = new ItemRequirementCondition(blackCandle);
		hasLitBlackCandle = new ItemRequirementCondition(litBlackCandle);
		hasAnyBlackCandle = new ItemRequirementCondition(LogicType.OR, blackCandle, litBlackCandle);
		beggarNearby = new NpcCondition(NpcID.BEGGAR);
		talkedToLady = new WidgetTextCondition(217, 4, "Ok. That seems easy enough.");
		hasExcalabur = new ItemRequirementCondition(excalabur);
		hasReadSpell = new Conditions(true, LogicType.AND, new WidgetTextCondition(229, 1, "You find a small inscription"));
		inStar = new ZoneCondition(star);
		thrantaxNearby = new NpcCondition(NpcID.THRANTAX_THE_MIGHTY);
		inCamelotTower1 = new ZoneCondition(camelotTower1);
		inCamelotTower2 = new ZoneCondition(camelotTower2);
	}

	public void setupSteps()
	{
		startQuest = new NpcStep(this, NpcID.KING_ARTHUR, new WorldPoint(2763, 3513, 0), "Talk to King Arthur in Camelot Castle to start.");
		startQuest.addDialogStep("I want to become a knight of the round table!");
		talkToGawain = new NpcStep(this, NpcID.SIR_GAWAIN, new WorldPoint(2758, 3504, 0), "Talk to Sir Gawain about how Merlin got trapped.");
		talkToGawain.addDialogStep("Do you know how Merlin got trapped?");

		goUpstairsInCamelot = new ObjectStep(this, ObjectID.STAIRCASE_26106, new WorldPoint(2751, 3511, 0), "Go upstairs to talk to Lancelot.");

		talkToLancelot = new NpcStep(this, NpcID.SIR_LANCELOT, new WorldPoint(2760, 3511, 1), "Talk to Sir Lancelot about getting into Morgan Le Faye's stronghold.");
		talkToLancelot.addDialogStep("Any ideas on how to get into Morgan Le Faye's stronghold?");
		talkToLancelot.addDialogStep("Thank you for the information.");

		goBackDownStairsCamelot = new ObjectStep(this, ObjectID.STAIRCASE_25604, new WorldPoint(2751, 3512, 1), "Hide in Arhein's crate behind the Candle Maker's shop in Catherby.");

		hideInArheinCrate = new ObjectStep(this, ObjectID.CRATE_63, new WorldPoint(2801, 3442, 0), "Hide in Arhein's crate behind the Candle Maker's shop in Catherby.");
		hideInArheinCrate.addSubSteps(goBackDownStairsCamelot);
		hideInArheinCrate.addDialogStep("Yes.");

		goToFirstFloor = new ObjectStep(this, ObjectID.STAIRCASE_15645, new WorldPoint(2770, 3405, 0), "Go up the stairs in the fortress.");
		goToSecondFloor = new ObjectStep(this, ObjectID.STAIRCASE_15645, new WorldPoint(2770, 3399, 1), "Go up another floor.");
		attackMordred = new NpcStep(this, NpcID.SIR_MORDRED, new WorldPoint(2770, 3403, 2), "Attack Sir Mordred down to 0hp to cause Morgan Le Faye to spawn.");
		attackMordred.addDialogStep("Tell me how to untrap Merlin and I might.");
		attackMordred.addDialogStep("Ok I will do all that.");
		talkToMorgan = new DetailedQuestStep(this, "Go through Morgan le Faye's dialog. IF YOU EXIT FROM THIS DIALOG YOU WILL HAVE TO FIGHT MORDRED AGAIN.");
		talkToMorgan.addDialogStep("Tell me how to untrap Merlin and I might.");
		talkToMorgan.addDialogStep("Ok I will do all that.");

		goToCatherbyAfterFortress = new DetailedQuestStep(this, "Return to Catherby. If you still need bat bones, you can kill one of the bats just outside the fortress.");
		optionalGetRepellent = new DetailedQuestStep(this, new WorldPoint(2807, 3450, 0), "If you still need wax, go grab the insect repellent in a house in north Catherby. Otherwise, get your wax out.", repellent);
		optionalGetBucket = new DetailedQuestStep(this, new WorldPoint(2766, 3441, 0), "Go grab the bucket in the bee field west of Catherby.", bucket);

		optionalUseRepellent = new ObjectStep(this, ObjectID.BEEHIVE, new WorldPoint(2762, 3443, 0), "Use the insect repellent on a bee hive, then try to take some wax.", bucket, repellent);
		talkToCandleMaker = new NpcStep(this, NpcID.CANDLE_MAKER, new WorldPoint(2797, 3440, 0), "Talk to the Candle Maker in Catherby twice until he gives you a black candle.", bucketOfWax);
		talkToCandleMaker.addDialogStep("Have you got any black candles?");

		talkToLadyOfLake = new NpcStep(this, NpcID.THE_LADY_OF_THE_LAKE, new WorldPoint(2924, 3404, 0), "Talk to the Lady of the Lake in Taverley.");
		talkToLadyOfLake.addDialogStep("I seek the sword Excalibur.");
		talkToLadyOfLake.setLockingCondition(talkedToLady);

		enterSarimShopAndTalk = new ObjectStep(this, ObjectID.DOOR_59, new WorldPoint(3016, 3246, 0), "Attempt to enter the jewelery store in Port Sarim.", bread);
		enterSarimShopAndTalk.addDialogStep("Yes certainly.");
		talkToBeggar =  new ObjectStep(this, ObjectID.DOOR_59, new WorldPoint(3016, 3246, 0), "Talk to the beggar who appears and give him some bread.", bread);
		talkToBeggar.addDialogStep("Yes certainly.");

		goReadMagicWords = new ObjectStep(this, ObjectID.CHAOS_ALTAR, new WorldPoint(3260, 3381, 0), "Check the altar in the Zamorak Temple in south east Varrock. If you've already learnt the spell, just mark this step complete in the Quest Helper sidebar.");
		goReadMagicWords.setLockingCondition(hasReadSpell);

		returnToCamelot = new ObjectStep(this, ObjectID.GATE_26082, new WorldPoint(2758, 3482, 0), "Return to Camelot", excalabur, blackCandle, batBones, tinderbox);
		returnToCamelotLit = new ObjectStep(this, ObjectID.GATE_26082, new WorldPoint(2758, 3482, 0), "Return to Camelot", excalabur, litBlackCandle, batBones);

		goStandInStar = new DetailedQuestStep(this, new WorldPoint(2780, 3515, 0), "Go stand in the star symbol north east of Camelot Castle.");
		goStandInStar.addDialogStep("Snarthon Candtrick Termanto");

		lightCandle = new DetailedQuestStep(this, "Light the Black candle with your tinderbox.", blackCandle, tinderbox);
		dropBatBones = new DetailedQuestStep(this, "Drop the bat bones in the star.", batBones, excalabur, litBlackCandle);
		dropBatBones.addDialogStep("Snarthon Candtrick Termanto");
		sayWords = new DetailedQuestStep(this, "Say the spell 'Snarthon Candtrick Termanto'. Be careful not to click the wrong option or you'll have to get another Black Candle.", excalabur);
		sayWords.addDialogStep("Snarthon Candtrick Termanto");

		goUpLadder1Camelot = new ObjectStep(this, ObjectID.LADDER_26107, new WorldPoint(2769, 3493, 0), "Go up the ladder in the south east of Camelot castle.", equippedExcalabur);
		goUpLadder2Camelot = new ObjectStep(this, ObjectID.LADDER_26107, new WorldPoint(2767, 3491, 1), "Go up the next ladder.", equippedExcalabur);

		smashCrystal = new ObjectStep(this, ObjectID.GIANT_CRYSTAL, new WorldPoint(2768, 3494, 2), "Smash the Giant Crystal with Excalabur equipped.", equippedExcalabur);

		goDownLadder1Camelot = new ObjectStep(this, ObjectID.LADDER_25606, new WorldPoint(2769, 3493, 1), "Tell King Arthur you've freed Merlin.");
		goDownLadder2Camelot = new ObjectStep(this, ObjectID.LADDER_25606, new WorldPoint(2767, 3491, 2), "Tell King Arthur you've freed Merlin.");

		finishQuest = new NpcStep(this, NpcID.KING_ARTHUR, new WorldPoint(2763, 3513, 0), "Tell King Arthur you've freed Merlin.");
		finishQuest.addSubSteps(goDownLadder1Camelot, goDownLadder2Camelot);
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Sir Mordred (level 39)");
		return reqs;
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(bread);
		reqs.add(tinderbox);
		reqs.add(bucketOfWaxOptional);
		reqs.add(batBonesOptional);
		return reqs;
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(varrockTeleport);
		reqs.add(camelotTeleport);
		reqs.add(twoFaladorTeleports);
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Arrays.asList(startQuest, talkToGawain, goUpstairsInCamelot, talkToLancelot))));
		allSteps.add(new PanelDetails("Infiltrate the fortress", new ArrayList<>(Arrays.asList(hideInArheinCrate, goToFirstFloor, goToSecondFloor, attackMordred, talkToMorgan))));
		PanelDetails getBlackCandlePanel = new PanelDetails("Get a Black Candle",
			new ArrayList<>(Arrays.asList(goToCatherbyAfterFortress, optionalGetRepellent, optionalGetBucket, optionalUseRepellent, talkToCandleMaker)), combatGear);
		getBlackCandlePanel.setLockingStep(getBlackCandle);
		getBlackCandlePanel.setVars(4);

		allSteps.add(getBlackCandlePanel);

		PanelDetails getExcalaburPanel = new PanelDetails("Get Excalabur",
			new ArrayList<>(Arrays.asList(talkToLadyOfLake, enterSarimShopAndTalk, talkToBeggar)), bread);
		getExcalaburPanel.setLockingStep(getExcalabur);
		getExcalaburPanel.setVars(4);

		allSteps.add(getExcalaburPanel);

		PanelDetails readMagicWordsPanel = new PanelDetails("Learn magic words",
			new ArrayList<>(Collections.singletonList(goReadMagicWords)));
		readMagicWordsPanel.setLockingStep(goReadMagicWords);
		readMagicWordsPanel.setVars(4);

		allSteps.add(readMagicWordsPanel);

		PanelDetails performMagicPanel = new PanelDetails("Perform the spell",
			new ArrayList<>(Arrays.asList(returnToCamelot, goStandInStar, lightCandle, dropBatBones, sayWords)), blackCandle, batBones, excalabur, tinderbox);

		allSteps.add(performMagicPanel);

		allSteps.add(new PanelDetails("Free Merlin", new ArrayList<>(Arrays.asList(goUpLadder1Camelot, goUpLadder2Camelot, smashCrystal, finishQuest))));

		return allSteps;
	}
}
