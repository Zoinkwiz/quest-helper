/*
 * Copyright (c) 2021, Zoinkwiz
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
package com.questhelper.quests.anightatthetheatre;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.A_NIGHT_AT_THE_THEATRE
)

public class ANightAtTheTheatre extends BasicQuestHelper
{
	QuestStep NotYetImplemented;

	QuestStep speakWithMysteriousStrangerToStart, enterVerSinhazaCrypts, killVyrewatchForKey, pickupCryptKey,
		unlockTheCryptGate, searchTheCoffinInVerSinhazaCrypts, speakWithMysteriousStrangerWithRanisHead, leaveCrypts,
		enterSpiderCave, searchSpiderCaveSkeleton, readStickyNote, speakWithDaerKrand, exitSpiderCave, enterSisterhoodSanctuary,
		climbStairsToSisterhoodSanctuaryF1, returnToSpiderCave, climbStairsDownSisterhoodF0, exitSisterhoodSanctuary, useSulphuricAcidOnEggSac,
		returnToMysteriousStrangerWithEggs, speakWithMysteriousStrangerAndWatchCutscenes, mysteriousStrangerCutscenes, goToNatureGrotto,
		speakToNatureSpiritInGrotto, goToHesporiFight, exitNatureGrotto, activateHesporiFight, fightHespori, chopHesporiForBark,
		returnToMysteriousStrangerWithBark, mysteriousStrangerCutscenes2, speakWithMysteriousStrangerAndWatchCutscenes2, completeTob,
		speakWithMysteriousStrangerToFinish, speakMoreWithMysteriousStranger;

	Requirement inVerSinhazaCrypts, inSpiderCave, inSisterhoodSanctuaryF0, inSisterhoodSanctuaryF1, inNatureGrotto, inHesporiArea;

	// Required Items
	ItemRequirement flail, saw, axe, ghostSpeakAmulet, combatGear, food;

	// Recommended Items
	ItemRequirement drakansMedallion, antiVenom, antipoison, fairyRings;

	// Quest Items
	ItemRequirement cryptKey, ranisHead, stickyNote, sulphuricAcid, strangeSpiderEggs, hesporiBark;

	NpcCondition vyrewatch1, vyrewatch2, vyrewatch3, vyrewatch4, vyrewatch5, hesporiNearby;

	Zone sisterhoodSanctuaryF0, sisterhoodSanctuaryF1, natureGrotto, hesporiArea;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, speakWithMysteriousStrangerToStart);
		steps.put(2, speakWithMysteriousStrangerToStart);
		steps.put(4, speakWithMysteriousStrangerToStart);
		steps.put(6, speakWithMysteriousStrangerToStart);

		ConditionalStep unlockCoffinCryptDoor = new ConditionalStep(this, enterVerSinhazaCrypts);
		unlockCoffinCryptDoor.addStep(new Conditions(inVerSinhazaCrypts, new ItemRequirements(cryptKey)), unlockTheCryptGate);
		unlockCoffinCryptDoor.addStep(new Conditions(inVerSinhazaCrypts, new ItemOnTileRequirement(cryptKey)), pickupCryptKey);
		unlockCoffinCryptDoor.addStep(inVerSinhazaCrypts, killVyrewatchForKey);

		steps.put(8, unlockCoffinCryptDoor);
		steps.put(10, unlockCoffinCryptDoor);

		ConditionalStep searchTheCoffinConditional = new ConditionalStep(this, enterVerSinhazaCrypts);
		searchTheCoffinConditional.addStep(inVerSinhazaCrypts, searchTheCoffinInVerSinhazaCrypts);
		steps.put(12, searchTheCoffinConditional);

		ConditionalStep speakWithMysteriousStrangerWithRanisHeadConditional = new ConditionalStep(this, speakWithMysteriousStrangerWithRanisHead);
		speakWithMysteriousStrangerWithRanisHeadConditional.addStep(inVerSinhazaCrypts, leaveCrypts);
		steps.put(14, speakWithMysteriousStrangerWithRanisHeadConditional);

		steps.put(16, speakMoreWithMysteriousStranger);
		steps.put(18, speakMoreWithMysteriousStranger);
		steps.put(20, enterSpiderCave);

		ConditionalStep searchSkeletonConditional = new ConditionalStep(this, enterSpiderCave);
		searchSkeletonConditional.addStep(new ItemRequirements(stickyNote), readStickyNote);
		searchSkeletonConditional.addStep(inSpiderCave, searchSpiderCaveSkeleton);

		steps.put(22, searchSkeletonConditional);
		steps.put(24, searchSkeletonConditional);

		ConditionalStep speakWithDaerKandConditional = new ConditionalStep(this, enterSisterhoodSanctuary);
		speakWithDaerKandConditional.addStep(inSpiderCave, exitSpiderCave);
		speakWithDaerKandConditional.addStep(inSisterhoodSanctuaryF0, climbStairsToSisterhoodSanctuaryF1);
		speakWithDaerKandConditional.addStep(inSisterhoodSanctuaryF1, speakWithDaerKrand);

		steps.put(26, speakWithDaerKandConditional);

		ConditionalStep returnToSpiderCaveConditional = new ConditionalStep(this, returnToSpiderCave);
		returnToSpiderCaveConditional.addStep(inSpiderCave, useSulphuricAcidOnEggSac);
		returnToSpiderCaveConditional.addStep(inSisterhoodSanctuaryF1, climbStairsDownSisterhoodF0);
		returnToSpiderCaveConditional.addStep(inSisterhoodSanctuaryF0, exitSisterhoodSanctuary);

		steps.put(28, returnToSpiderCaveConditional);
		steps.put(30, returnToMysteriousStrangerWithEggs);
		steps.put(32, speakWithMysteriousStrangerAndWatchCutscenes);
		steps.put(34, speakWithMysteriousStrangerAndWatchCutscenes);
		steps.put(36, speakWithMysteriousStrangerAndWatchCutscenes);

		ConditionalStep goToNatureGrottoConditional = new ConditionalStep(this, goToNatureGrotto);
		goToNatureGrottoConditional.addStep(inNatureGrotto, speakToNatureSpiritInGrotto);

		steps.put(38, goToNatureGrottoConditional);

		ConditionalStep goToHesporiFightConditional = new ConditionalStep(this, goToHesporiFight);
		goToHesporiFightConditional.addStep(inNatureGrotto, exitNatureGrotto);
		goToHesporiFightConditional.addStep(inHesporiArea, activateHesporiFight);

		steps.put(40, goToHesporiFightConditional);

		ConditionalStep hesporiFightConditional = new ConditionalStep(this, goToHesporiFightConditional);
		hesporiFightConditional.addStep(hesporiNearby, fightHespori);

		steps.put(42, hesporiFightConditional);

		steps.put(44, chopHesporiForBark);

		steps.put(46, returnToMysteriousStrangerWithBark);

		steps.put(48, speakWithMysteriousStrangerAndWatchCutscenes2);
		steps.put(50, speakWithMysteriousStrangerAndWatchCutscenes2);
		steps.put(52, speakWithMysteriousStrangerAndWatchCutscenes2);
		steps.put(54, completeTob);
		steps.put(56, completeTob);
		steps.put(58, completeTob);
		steps.put(60, completeTob);
		steps.put(62, completeTob);
		steps.put(64, completeTob);
		steps.put(66, completeTob);
		steps.put(68, completeTob);
		steps.put(70, completeTob);
		steps.put(72, completeTob);
		steps.put(74, completeTob);
		steps.put(76, completeTob);
		steps.put(78, completeTob);
		steps.put(80, speakWithMysteriousStrangerToFinish);
		steps.put(82, speakWithMysteriousStrangerToFinish);
		steps.put(84, speakWithMysteriousStrangerToFinish);
		steps.put(86, speakWithMysteriousStrangerToFinish);


		return steps;
	}

	public void setupSteps()
	{
		NotYetImplemented = new DetailedQuestStep(this, "Not yet Implemented");

		speakWithMysteriousStrangerToStart = new NpcStep(this, NpcID.MYSTERIOUS_STRANGER_10875, new WorldPoint(3673, 3223, 0),
			"Speak with the Mysterious Stranger in Ver Sinhaza.");
		((NpcStep) speakWithMysteriousStrangerToStart).addAlternateNpcs(NpcID.MYSTERIOUS_STRANGER_10876);
		speakWithMysteriousStrangerToStart.addDialogSteps("What's all this really about?", "What's this thing you need from me?", "Yes.");

		enterVerSinhazaCrypts = new ObjectStep(this, ObjectID.STAIRCASE_42523, new WorldPoint(3682, 3231, 0),
			"Enter the crypts north east of the Mysterious Stranger.");
		enterVerSinhazaCrypts.addDialogSteps("Yes.");

		killVyrewatchForKey = new NpcStep(this, NpcID.VYREWATCH_11173,
			"Kill a vyrewatch for a key. You must have your flail equipped in order to damage the vyrewatches.", true, flail.equipped(), combatGear, food);
		((NpcStep) killVyrewatchForKey).addAlternateNpcs(NpcID.VYREWATCH_11169, NpcID.VYREWATCH_11170, NpcID.VYREWATCH_11171, NpcID.VYREWATCH_11172, NpcID.VYREWATCH_11173);
		pickupCryptKey = new ItemStep(this, "Pick up the crypt key", cryptKey);
		killVyrewatchForKey.addSubSteps(pickupCryptKey);

		unlockTheCryptGate = new ObjectStep(this, ObjectID.GATE_42529, "Unlock the crypt gate with the key.", cryptKey);

		searchTheCoffinInVerSinhazaCrypts = new ObjectStep(this, ObjectID.COFFIN_42532, "Search the coffin.");

		leaveCrypts = new ObjectStep(this, ObjectID.DOOR_42524, "Return to the Mysterious Stranger with Ranis' head.", ranisHead);
		speakWithMysteriousStrangerWithRanisHead = new NpcStep(this, NpcID.MYSTERIOUS_STRANGER_10875,
			new WorldPoint(3673, 3223, 0), "Return to the Mysterious Stranger with Ranis' head.", ranisHead);
		((NpcStep) speakWithMysteriousStrangerWithRanisHead).addAlternateNpcs(NpcID.MYSTERIOUS_STRANGER_10876, NpcID.MYSTERIOUS_STRANGER);
		speakWithMysteriousStrangerWithRanisHead.addDialogSteps("I managed to recover Ranis' head.");
		speakWithMysteriousStrangerWithRanisHead.addSubSteps(leaveCrypts);

		speakMoreWithMysteriousStranger = new NpcStep(this, NpcID.MYSTERIOUS_STRANGER_10875, new WorldPoint(3673, 3223, 0),
			"Speak to the Mysterious Stranger some more.");
		((NpcStep) speakMoreWithMysteriousStranger).addAlternateNpcs(NpcID.MYSTERIOUS_STRANGER_10876, NpcID.MYSTERIOUS_STRANGER);
		speakMoreWithMysteriousStranger.addDialogSteps("So what are we doing with Ranis' head?", "So about that memory...");

		enterSpiderCave = new ObjectStep(this, ObjectID.CAVE_42594, new WorldPoint(3658, 3409, 0), "Enter the spider cave south of Port Phasmatys.");
		enterSpiderCave.addDialogSteps("Yes.");

		searchSpiderCaveSkeleton = new ObjectStep(this, ObjectID.SKELETON_42605, "Search the skeleton found near the Egg Sac at the end of the cave.");
		readStickyNote = new DetailedQuestStep(this, "Read the Sticky note.", stickyNote.highlighted());

		exitSpiderCave = new ObjectStep(this, ObjectID.CAVE_42595, "Exit the spider cave via the way you came in.");
		enterSisterhoodSanctuary = new ObjectStep(this, ObjectID.STAIRS_32637, new WorldPoint(3728, 3300, 0),
			"Speak with Daer Krand found in the north east corner of the main room of the Sisterhood Sanctuary in Slepe.");
		climbStairsToSisterhoodSanctuaryF1 = new ObjectStep(this, ObjectID.STAIRS_37832, new WorldPoint(3826, 9778, 1),
			"Speak with Daer Krand found in the north east corner of the main room of the Sisterhood Sanctuary in Slepe.");


		List<WorldPoint> sisterhoodSanctuaryLines = Arrays.asList(
			new WorldPoint(3738, 9703, 1),
			new WorldPoint(3738, 9710, 1),
			new WorldPoint(3751, 9710, 1),
			new WorldPoint(3763, 9710, 1),
			new WorldPoint(3775, 9710, 1),
			new WorldPoint(3789, 9710, 1),
			new WorldPoint(3795, 9698, 1),
			new WorldPoint(3801, 9693, 1),
			new WorldPoint(3808, 9695, 1),
			new WorldPoint(3808, 9712, 1),
			new WorldPoint(3808, 9733, 1),
			new WorldPoint(3814, 9733, 1),
			new WorldPoint(3824, 9733, 1),
			new WorldPoint(3831, 9733, 1),
			new WorldPoint(3831, 9746, 1),
			new WorldPoint(3831, 9760, 1),
			new WorldPoint(3831, 9773, 1),
			new WorldPoint(3828, 9777, 1)
		);

		((ObjectStep) climbStairsToSisterhoodSanctuaryF1).setLinePoints(sisterhoodSanctuaryLines);

		speakWithDaerKrand = new NpcStep(this, NpcID.DAER_KRAND, new WorldPoint(3821, 9778, 2),
			"Speak with Daer Krand found in the north east corner of the main room of the Sisterhood Sanctuary in Slepe.");
		speakWithDaerKrand.addDialogSteps("Got any sulphuric acid?");
		speakWithDaerKrand.addSubSteps(exitSpiderCave, climbStairsToSisterhoodSanctuaryF1, enterSisterhoodSanctuary);

		returnToSpiderCave = new ObjectStep(this, ObjectID.CAVE_42594, new WorldPoint(3658, 3409, 0),
			"Return to the spider cave south of Port Phasmatys then use the Sulphuric acid on the Egg sac at the end of the cave.", sulphuricAcid);
		returnToSpiderCave.addDialogSteps("Yes.");
		climbStairsDownSisterhoodF0 = new ObjectStep(this, ObjectID.STAIRS_37833, new WorldPoint(3826, 9778, 2),
			"Return to the spider cave south of Port Phasmatys then use the Sulphuric acid on the Egg sac at the end of the cave. " +
				"Use the Drakan's Medallion Slepe tele to exit the Sisterhood Sanctuary quickly.", sulphuricAcid);
		exitSisterhoodSanctuary = new ObjectStep(this, ObjectID.STAIRS_32638, new WorldPoint(3739, 9701, 1),
			"Return to the spider cave south of Port Phasmatys then use the Sulphuric acid on the Egg sac at the end of the cave. " +
				"Use the Drakan's Medallion Slepe tele to exit the Sisterhood Sanctuary quickly.", sulphuricAcid);
		List<WorldPoint> sisterhoodSanctuaryLinesReversed = new ArrayList<>(sisterhoodSanctuaryLines);
		Collections.reverse(sisterhoodSanctuaryLinesReversed);
		((ObjectStep) exitSisterhoodSanctuary).setLinePoints(sisterhoodSanctuaryLinesReversed);
		useSulphuricAcidOnEggSac = new ObjectStep(this, ObjectID.EGG_SAC_42601,
			"Return to the spider cave south of Port Phasmatys then use the Sulphuric acid on the Egg sac at the end " +
				"of the cave. You do not need to kill the spiders which appear.", sulphuricAcid.highlighted());
		returnToSpiderCave.addSubSteps(climbStairsDownSisterhoodF0, exitSisterhoodSanctuary, useSulphuricAcidOnEggSac);
		useSulphuricAcidOnEggSac.addIcon(ItemID.SULPHURIC_ACID);

		returnToMysteriousStrangerWithEggs = new NpcStep(this, NpcID.MYSTERIOUS_STRANGER_10876, new WorldPoint(3673, 3223, 0),
			"Speak with the Mysterious Stranger in Ver Sinhaza again with the spider eggs retrieved from the Egg sac. " +
				"Use the Drakan's Medallion tele to get to Ver Sinhaza quickly.", strangeSpiderEggs);
		returnToMysteriousStrangerWithEggs.addDialogSteps("I found some of those eggs.");
		((NpcStep) returnToMysteriousStrangerWithEggs).addAlternateNpcs(NpcID.MYSTERIOUS_STRANGER_10875);

		mysteriousStrangerCutscenes = new DetailedQuestStep(this, "Watch cutscenes.");
		speakWithMysteriousStrangerAndWatchCutscenes = new NpcStep(this, NpcID.MYSTERIOUS_STRANGER_10876, new WorldPoint(3673, 3223, 0),
			"Speak with the Mysterious Stranger in Ver Sinhaza and watch the cutscenes.");
		((NpcStep) speakWithMysteriousStrangerAndWatchCutscenes).addAlternateNpcs(NpcID.MYSTERIOUS_STRANGER_10875);
		mysteriousStrangerCutscenes.addSubSteps(speakWithMysteriousStrangerAndWatchCutscenes);
		speakWithMysteriousStrangerAndWatchCutscenes.addDialogSteps("That memory didn't tell us too much.",
			"That memory seemed more useful.");

		goToNatureGrotto = new ObjectStep(this, ObjectID.GROTTO, new WorldPoint(3440, 3337, 0),
			"Go to the Nature Grotto in the Mort Myre Swamp and speak with the Nature Spirit inside. " +
				"Use fairy ring BIP (with 50 agility) to get there quickly.",
			ghostSpeakAmulet.equipped(), combatGear, food, antipoison, axe);
		speakToNatureSpiritInGrotto = new NpcStep(this, NpcID.NATURE_SPIRIT,
			"Go to the Nature Grotto in the Mort Myre Swamp and speak with the Nature Spirit inside. " +
				"Use fairy ring BIP (with 50 agility) to get there quickly.", ghostSpeakAmulet.equipped());
		goToNatureGrotto.addSubSteps(speakToNatureSpiritInGrotto);

		String hesporiFightText = "Go to the island directly east of the Nature Grotto. Be prepared to fight Hespori. " +
			"The fight is similar to what you would expect from the Hespori fight in the Farming Guild. Activate Hespori when ready.";
		goToHesporiFight = new ObjectStep(this, ObjectID.STEPPING_STONE_42588, new WorldPoint(3499, 3355, 0),
				hesporiFightText, combatGear, food, antipoison, axe);
		((ObjectStep) goToHesporiFight).setLinePoints(Arrays.asList(
			new WorldPoint(3440, 3327, 0),
			new WorldPoint(3427, 3330, 0),
			new WorldPoint(3429, 3346, 0),
			new WorldPoint(3442, 3350, 0),
			new WorldPoint(3454, 3347, 0),
			new WorldPoint(3463, 3357, 0),
			new WorldPoint(3474, 3366, 0),
			new WorldPoint(3484, 3366, 0),
			new WorldPoint(3487, 3356, 0),
			new WorldPoint(3497, 3355, 0)
		));
		exitNatureGrotto = new ObjectStep(this, ObjectID.GROTTO_3526, new WorldPoint(3442, 9733, 1),
				hesporiFightText, combatGear, food, antipoison, axe);
		activateHesporiFight = new ObjectStep(this, ObjectID.HESPORI_42591, new WorldPoint(3507, 3357, 0),
				hesporiFightText, combatGear, food, antipoison, axe);
		goToHesporiFight.addSubSteps(exitNatureGrotto, activateHesporiFight);

		fightHespori = new NpcStep(this, NpcID.HESPORI_11192, "Fight Hespori.");

		chopHesporiForBark = new ObjectStep(this, ObjectID.HESPORI_42592, new WorldPoint(3507, 3357, 0), "Chop Hespori to obtain Hespori bark.", axe);
		returnToMysteriousStrangerWithBark = new NpcStep(this, NpcID.MYSTERIOUS_STRANGER_10876, new WorldPoint(3673, 3223, 0),
			"Speak with the Mysterious Stranger in Ver Sinhaza again with the Hespori bark. Use the Drakan's Medallion tele to get to Ver Sinhaza quickly.", hesporiBark);
		returnToMysteriousStrangerWithBark.addDialogSteps("I found a hespori.");
		((NpcStep) returnToMysteriousStrangerWithBark).addAlternateNpcs(NpcID.MYSTERIOUS_STRANGER_10875);

		mysteriousStrangerCutscenes2 = new DetailedQuestStep(this, "Watch cutscenes.");
		speakWithMysteriousStrangerAndWatchCutscenes2 = new NpcStep(this, NpcID.MYSTERIOUS_STRANGER_10876, new WorldPoint(3673, 3223, 0),
		"Speak with the Mysterious Stranger in Ver Sinhaza and watch the cutscenes.");
		mysteriousStrangerCutscenes2.addSubSteps(speakWithMysteriousStrangerAndWatchCutscenes2);

		speakWithMysteriousStrangerAndWatchCutscenes2.addDialogSteps("Can we use this bark to find more memories?",
			"Do you have any more memories for us to look at?");
		((NpcStep) speakWithMysteriousStrangerAndWatchCutscenes2).addAlternateNpcs(NpcID.MYSTERIOUS_STRANGER_10875);


		completeTob = new DetailedQuestStep(this, "Complete the Theatre of Blood.");
		completeTob.addDialogSteps("Yes.");

		speakWithMysteriousStrangerToFinish = new NpcStep(this, NpcID.MYSTERIOUS_STRANGER_10876, new WorldPoint(3673, 3223, 0),
			"Speak with the Mysterious Stranger in Ver Sinhaza and watch the cutscenes to finish the quest.");
		((NpcStep) speakWithMysteriousStrangerToFinish).addAlternateNpcs(NpcID.MYSTERIOUS_STRANGER_10877, NpcID.MYSTERIOUS_STRANGER_10875);
		speakWithMysteriousStrangerToFinish.addDialogSteps("Did you manage to get any useful memories from Verzik?");

	}

	public void loadZones()
	{
		sisterhoodSanctuaryF0 = new Zone(new WorldPoint(3702, 9834, 1), new WorldPoint(3899, 9600, 1));
		sisterhoodSanctuaryF1 = new Zone(new WorldPoint(3785, 9805, 2), new WorldPoint(3840, 9765, 2));
		natureGrotto = new Zone(new WorldPoint(3430, 9748, 1), new WorldPoint(3451, 9731, 1));
		hesporiArea = new Zone(new WorldPoint(3500, 3364, 0), new WorldPoint(3514, 3350, 0));
	}

	@Override
	public void setupRequirements()
	{
		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getMeleeCombatGear());
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD);
		drakansMedallion = new ItemRequirement("Drakan's Medallion", ItemID.DRAKANS_MEDALLION, 1);
		antiVenom = new ItemRequirement("Anti-venom", ItemCollections.ANTIVENOMS, 1);
		antipoison = new ItemRequirement("Antipoison", ItemCollections.ANTIPOISONS, 1);
		fairyRings = new ItemRequirement("Access to fairy rings", ItemCollections.FAIRY_STAFF, 1);

		flail = new ItemRequirement("Ivandis/Blisterwood flail", ItemID.IVANDIS_FLAIL);
		flail.addAlternates(ItemID.BLISTERWOOD_FLAIL);
		saw = new ItemRequirement("Saw", ItemCollections.SAW);
		ghostSpeakAmulet = new ItemRequirement("Ghostspeak amulet", ItemCollections.GHOSTSPEAK);
		ghostSpeakAmulet.setTooltip("Morytania legs 2+ work as well.");
		axe = new ItemRequirement("An axe", ItemCollections.AXES);

		cryptKey = new ItemRequirement("Crypt key", ItemID.CRYPT_KEY);
		ranisHead = new ItemRequirement("Ranis' Head", ItemID.RANIS_HEAD);
		ranisHead.setTooltip("You can obtain another one from the coffin in the crypt.");

		stickyNote = new ItemRequirement("Stick note", ItemID.STICKY_NOTE);
		stickyNote.setTooltip("You can obtain another from the skeleton in the spider cave south of Port Phasmatys.");

		sulphuricAcid = new ItemRequirement("Sulphiric acid", ItemID.SULPHURIC_ACID);
		sulphuricAcid.setTooltip("You can get more from Daer Krand in the Sisterhood Sanctuary");

		strangeSpiderEggs = new ItemRequirement("Strange spider eggs", ItemID.STRANGE_SPIDER_EGGS);
		strangeSpiderEggs.setTooltip("You can obtain more from the egg sac in the spider cave south of Port Phasmatys.");

		hesporiBark = new ItemRequirement("Hespori bark", ItemID.HESPORI_BARK);
		hesporiBark.setTooltip("You can chop some more from the hespori east of the Nature Grotto in the Mort Myre Swamp.");
	}

	public void setupConditions()
	{
		vyrewatch1 = new NpcCondition(NpcID.VYREWATCH_11169);
		vyrewatch2 = new NpcCondition(NpcID.VYREWATCH_11170);
		vyrewatch3 = new NpcCondition(NpcID.VYREWATCH_11171);
		vyrewatch4 = new NpcCondition(NpcID.VYREWATCH_11172);
		vyrewatch5 = new NpcCondition(NpcID.VYREWATCH_11173);

		inVerSinhazaCrypts = new Conditions(LogicType.OR, vyrewatch1, vyrewatch2, vyrewatch3, vyrewatch4, vyrewatch5,
			new ObjectCondition(ObjectID.DOOR_42524), new ObjectCondition(ObjectID.COFFIN_42532));

		inSpiderCave = new Conditions(new ObjectCondition(ObjectID.SPIDER_EGGS_42645));

		inSisterhoodSanctuaryF0 = new ZoneRequirement(sisterhoodSanctuaryF0);
		inSisterhoodSanctuaryF1 = new ZoneRequirement(sisterhoodSanctuaryF1);

		inNatureGrotto = new ZoneRequirement(natureGrotto);

		inHesporiArea = new ZoneRequirement(hesporiArea);

		hesporiNearby = new NpcCondition(NpcID.HESPORI_11192);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(flail, saw, ghostSpeakAmulet, axe, combatGear, food);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(drakansMedallion, antiVenom, antipoison, fairyRings);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Vyrewatch (level 105)");
		reqs.add("Several venomous spiders (level 87 and 123)");
		reqs.add("Hespori (level 302)");
		reqs.add("All ToB Bosses");

		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("Antique Lamp (20,000 Exp. Any Combat Skill)", ItemID.ANTIQUE_LAMP, 3));
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new QuestRequirement(QuestHelperQuest.A_TASTE_OF_HOPE, QuestState.FINISHED));

		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Memories of a \"Friend\"", Arrays.asList(speakWithMysteriousStrangerToStart,
			enterVerSinhazaCrypts, killVyrewatchForKey, unlockTheCryptGate, searchTheCoffinInVerSinhazaCrypts,
			speakWithMysteriousStrangerWithRanisHead, speakMoreWithMysteriousStranger, enterSpiderCave, searchSpiderCaveSkeleton,
			readStickyNote, speakWithDaerKrand, returnToSpiderCave, returnToMysteriousStrangerWithEggs,
			mysteriousStrangerCutscenes), combatGear, food.quantity(20), flail, saw, antiVenom, drakansMedallion));

		allSteps.add(new PanelDetails("In Touch with Nature", Arrays.asList(goToNatureGrotto, goToHesporiFight, fightHespori,
			chopHesporiForBark, returnToMysteriousStrangerWithBark, mysteriousStrangerCutscenes2), combatGear,
			food.quantity(20),
			ghostSpeakAmulet, axe, antipoison));

		allSteps.add(new PanelDetails("Theatre of Blood", Arrays.asList(completeTob, speakWithMysteriousStrangerToFinish)));

		return allSteps;
	}
}
