/*
 * Copyright (c) 2022, Obasill <https://github.com/Obasill>
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
package com.questhelper.helpers.achievementdiaries.westernprovinces;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.WESTERN_MEDIUM
)
public class WesternMedium extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement combatGear, ogreBellows, ogreBow, ogreArrows, teasingStick, logs, knife, bigFishingNet, axe, tinderbox,
		rope, gnomebowl, gianneDough, chocolateBar, equaLeaf, potOfCream, chocolateDust, crystalSawSeed, pickaxe, teakLogs;

	// Items recommended
	ItemRequirement food, fairyAccess, seedPod;

	// Quests required
	Requirement grandTree, eyesOfGlouphrie, bigChompy, eaglesPeak, monkeyMadnessI, treeGnomeVillage, oneSmallFavour,
		trainingCompleted, choppedLogs;

	Requirement notAgiShortcut, notSpiritToStronghold, notSpinedLarupia, notApeBass, notApeTeak, notInterPest,
		notGliderToFeldip, notChompyHat, notEagleFeldip, notChocolateBomb, notGnomeDelivery, notCrystalSaw, notMineGold;

	QuestStep claimReward, agiShortcut, spiritToStronghold, spinedLarupia, apeBass, interPest, gliderToFeldip,
		chompyHat, eagleFeldip, chocolateBomb, gnomeDelivery, crystalSaw, moveToApeBass, moveToApeTeak, moveToStrongFirstDelivery,
		moveToStrongFirstChoco, moveToStrongBase, moveToStrongBase2, moveToBrimstailCave, moveToEagle, moveToPest,
		apeTeakChop, apeTeakBurn;

	ObjectStep mineGold;

	NpcStep completeTraining;

	Zone brimstailCave, apeAtoll, pest, strongholdBasement, strongholdFirst, eagleArea;

	ZoneRequirement inBrimstailCave, inApeAtoll, inPest, inStrongholdBasement, inStrongholdFirst, inEagleArea;

	ConditionalStep agiShortcutTask, spiritToStrongholdTask, spinedLarupiaTask, apeBassTask, apeTeakTask, interPestTask,
		gliderToFeldipTask, chompyHatTask, eagleFeldipTask, chocolateBombTask, gnomeDeliveryTask, crystalSawTask, mineGoldTask;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doMedium = new ConditionalStep(this, claimReward);

		spiritToStrongholdTask = new ConditionalStep(this, spiritToStronghold);
		doMedium.addStep(notSpiritToStronghold, spiritToStrongholdTask);

		chocolateBombTask = new ConditionalStep(this, moveToStrongFirstChoco);
		chocolateBombTask.addStep(inStrongholdFirst, chocolateBomb);
		doMedium.addStep(notChocolateBomb, chocolateBombTask);

		gnomeDeliveryTask = new ConditionalStep(this, moveToStrongFirstDelivery);
		gnomeDeliveryTask.addStep(inStrongholdFirst, gnomeDelivery);
		//gnomeDeliveryTask.addStep(new Conditions(inStrongholdFirst, trainingCompleted), gnomeDelivery);
		//gnomeDeliveryTask.addStep(new Conditions(inStrongholdFirst), completeTraining);
		doMedium.addStep(notGnomeDelivery, gnomeDeliveryTask);

		mineGoldTask = new ConditionalStep(this, moveToStrongBase);
		mineGoldTask.addStep(inStrongholdFirst, moveToStrongBase2);
		mineGoldTask.addStep(inStrongholdBasement, mineGold);
		doMedium.addStep(notMineGold, mineGoldTask);

		crystalSawTask = new ConditionalStep(this, moveToBrimstailCave);
		crystalSawTask.addStep(inBrimstailCave, crystalSaw);
		doMedium.addStep(notCrystalSaw, crystalSawTask);

		agiShortcutTask = new ConditionalStep(this, agiShortcut);
		doMedium.addStep(notAgiShortcut, agiShortcutTask);

		eagleFeldipTask = new ConditionalStep(this, moveToEagle);
		eagleFeldipTask.addStep(inEagleArea, eagleFeldip);
		doMedium.addStep(notEagleFeldip, eagleFeldipTask);

		spinedLarupiaTask = new ConditionalStep(this, spinedLarupia);
		doMedium.addStep(notSpinedLarupia, spinedLarupiaTask);

		gliderToFeldipTask = new ConditionalStep(this, gliderToFeldip);
		doMedium.addStep(notGliderToFeldip, gliderToFeldipTask);

		apeBassTask = new ConditionalStep(this, moveToApeBass);
		apeBassTask.addStep(inApeAtoll, apeBass);
		doMedium.addStep(notApeBass, apeBassTask);

		apeTeakTask = new ConditionalStep(this, moveToApeTeak);
		apeTeakTask.addStep(inApeAtoll, apeTeakChop);
		apeTeakTask.addStep(new Conditions(inApeAtoll, teakLogs, choppedLogs), apeTeakBurn);
		doMedium.addStep(notApeTeak, apeTeakTask);

		interPestTask = new ConditionalStep(this, moveToPest);
		interPestTask.addStep(inPest, interPest);
		doMedium.addStep(notInterPest, interPestTask);

		chompyHatTask = new ConditionalStep(this, chompyHat);
		doMedium.addStep(notChompyHat, chompyHatTask);

		return doMedium;
	}

	@Override
	public void setupRequirements()
	{
		notAgiShortcut = new VarplayerRequirement(1182, false, 12);
		notSpiritToStronghold = new VarplayerRequirement(1182, false, 13);
		notSpinedLarupia = new VarplayerRequirement(1182, false, 14);
		notApeBass = new VarplayerRequirement(1182, false, 15);
		notApeTeak = new VarplayerRequirement(1182, false, 16);
		notInterPest = new VarplayerRequirement(1182, false, 17);
		notGliderToFeldip = new VarplayerRequirement(1182, false, 18);
		notChompyHat = new VarplayerRequirement(1182, false, 19);
		notEagleFeldip = new VarplayerRequirement(1182, false, 20);
		notChocolateBomb = new VarplayerRequirement(1182, false, 21);
		notGnomeDelivery = new VarplayerRequirement(1182, false, 22);
		notCrystalSaw = new VarplayerRequirement(1182, false, 23);
		notMineGold = new VarplayerRequirement(1182, false, 24);

		// todo this varb only tracks training with aluft NOT blurberry
		trainingCompleted = new VarbitRequirement(2493, Operation.EQUAL, 1,
			"Completed training with Blurberry and Aluft Gianne snr.");

		teasingStick = new ItemRequirement("Teasing stick", ItemID.TEASING_STICK).showConditioned(notSpinedLarupia).isNotConsumed();
		logs = new ItemRequirement("Logs", ItemID.LOGS).showConditioned(notSpinedLarupia);
		knife = new ItemRequirement("Knife", ItemID.KNIFE).showConditioned(notSpinedLarupia).isNotConsumed();
		bigFishingNet = new ItemRequirement("Big fishing net", ItemID.BIG_FISHING_NET).showConditioned(notApeBass).isNotConsumed();
		axe = new ItemRequirement("Axe", ItemCollections.AXES).showConditioned(notApeTeak).isNotConsumed();
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).showConditioned(notApeTeak).isNotConsumed();
		rope = new ItemRequirement("Rope", ItemID.ROPE).showConditioned(notEagleFeldip);
		gnomebowl = new ItemRequirement("Gnomebowl mould", ItemID.GNOMEBOWL_MOULD).showConditioned(notChocolateBomb).isNotConsumed();
		gnomebowl.setTooltip("can be purchased at Grand Tree Groceries");
		gianneDough = new ItemRequirement("Gianne dough", ItemID.GIANNE_DOUGH).showConditioned(notChocolateBomb);
		gianneDough.setTooltip("can be purchased at Grand Tree Groceries");
		chocolateBar = new ItemRequirement("Chocolate bar", ItemID.CHOCOLATE_BAR, 4).showConditioned(notChocolateBomb);
		chocolateBar.setTooltip("can be purchased at Grand Tree Groceries");
		equaLeaf = new ItemRequirement("Equa leaves", ItemID.EQUA_LEAVES).showConditioned(notChocolateBomb);
		equaLeaf.setTooltip("can be purchased at Grand Tree Groceries");
		potOfCream = new ItemRequirement("Pot of cream", ItemID.POT_OF_CREAM, 2).showConditioned(notChocolateBomb);
		potOfCream.setTooltip("can be purchased at Grand Tree Groceries");
		chocolateDust = new ItemRequirement("Chocolate dust", ItemID.CHOCOLATE_DUST).showConditioned(notChocolateBomb);
		chocolateDust.setTooltip("can be purchased at Grand Tree Groceries");
		crystalSawSeed = new ItemRequirement("Crystal saw seed", ItemID.CRYSTAL_SAW_SEED).showConditioned(notCrystalSaw).isNotConsumed();
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).showConditioned(notMineGold).isNotConsumed();
		ogreBellows = new ItemRequirement("Ogre bellows", ItemCollections.OGRE_BELLOWS).showConditioned(notChompyHat).isNotConsumed();
		ogreBow = new ItemRequirement("Ogre bow", ItemCollections.OGRE_BOW).showConditioned(notChompyHat).isNotConsumed();
		ogreArrows = new ItemRequirement("Ogre / brutal arrows", ItemCollections.OGRE_BRUTAL_ARROWS).showConditioned(notChompyHat);
		teakLogs = new ItemRequirement("Teak logs", ItemID.TEAK_LOGS);

		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		fairyAccess = new ItemRequirement("Dramen or Lunar staff", ItemCollections.FAIRY_STAFF).isNotConsumed();
		seedPod = new ItemRequirement("Royal seed pod", ItemID.ROYAL_SEED_POD).isNotConsumed();

		inBrimstailCave = new ZoneRequirement(brimstailCave);
		inPest = new ZoneRequirement(pest);
		inApeAtoll = new ZoneRequirement(apeAtoll);
		inStrongholdBasement = new ZoneRequirement(strongholdBasement);
		inStrongholdFirst = new ZoneRequirement(strongholdFirst);
		inEagleArea = new ZoneRequirement(eagleArea);

		choppedLogs = new ChatMessageRequirement(
			inApeAtoll,
			"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
		);
		((ChatMessageRequirement) choppedLogs).setInvalidateRequirement(
			new ChatMessageRequirement(
				new Conditions(LogicType.NOR, inApeAtoll),
				"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
			)
		);

		grandTree = new QuestRequirement(QuestHelperQuest.THE_GRAND_TREE, QuestState.FINISHED);
		eyesOfGlouphrie = new QuestRequirement(QuestHelperQuest.THE_EYES_OF_GLOUPHRIE, QuestState.FINISHED);
		bigChompy = new QuestRequirement(QuestHelperQuest.BIG_CHOMPY_BIRD_HUNTING, QuestState.FINISHED);
		eaglesPeak = new QuestRequirement(QuestHelperQuest.EAGLES_PEAK, QuestState.FINISHED);
		monkeyMadnessI = new QuestRequirement(QuestHelperQuest.MONKEY_MADNESS_I, QuestState.IN_PROGRESS);
		treeGnomeVillage = new QuestRequirement(QuestHelperQuest.TREE_GNOME_VILLAGE, QuestState.FINISHED);
		oneSmallFavour = new QuestRequirement(QuestHelperQuest.ONE_SMALL_FAVOUR, QuestState.IN_PROGRESS);
	}

	public void loadZones()
	{
		brimstailCave = new Zone(new WorldPoint(2376, 9835, 0), new WorldPoint(2416, 9802, 0));
		pest = new Zone(new WorldPoint(2631, 2681, 0), new WorldPoint(2683, 2626, 0));
		eagleArea = new Zone(new WorldPoint(1986, 4985, 3), new WorldPoint(2030, 4944, 3));
		strongholdFirst = new Zone(new WorldPoint(2436, 3515, 1), new WorldPoint(2496, 3475, 1));
		strongholdBasement = new Zone(new WorldPoint(2431, 9919, 0), new WorldPoint(2499, 9855, 0));
		apeAtoll = new Zone(new WorldPoint(2687, 2814, 0), new WorldPoint(2817, 2686, 0));
	}

	public void setupSteps()
	{

		spiritToStronghold = new DetailedQuestStep(this, "Travel to the Gnome Stronghold by spirit tree.");

		// todo more detailed chocobomb steps
		moveToStrongFirstChoco = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2466, 3495, 0),
			"Climb the ladder at the Grand Tree.", gnomebowl, gianneDough, chocolateBar, equaLeaf, potOfCream, chocolateDust);
		chocolateBomb = new DetailedQuestStep(this, "Make a chocolate bomb.", gnomebowl, gianneDough, chocolateBar,
			equaLeaf, potOfCream, chocolateDust);

		moveToStrongFirstDelivery = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2466, 3495, 0),
			"Climb the ladder at the Grand Tree.");
		completeTraining = new NpcStep(this, NpcID.ALUFT_GIANNE_SNR, new WorldPoint(2449, 3501, 1),
			"Complete your training by talking to Blurberry and Aluft Gianne snr.");
		completeTraining.addAlternateNpcs(NpcID.BLURBERRY);
		completeTraining.addDialogStep("Okay then I'll give it a go.");
		gnomeDelivery = new NpcStep(this, NpcID.GIANNE_JNR, new WorldPoint(2439, 3502, 1),
			"Complete a delivery for the Gnome Restaurant.");
		gnomeDelivery.addDialogSteps("Yes, tell me more.", "I'm ready to take a job on now.");

		moveToStrongBase = new ObjectStep(this, ObjectID.TRAPDOOR_2446, new WorldPoint(2463, 3497, 0),
			"Open the trapdoor to enter the underground of the Grand Tree.", pickaxe);
		moveToStrongBase2 = new ObjectStep(this, ObjectID.LADDER_16684, new WorldPoint(2466, 3495, 1),
			"Open the trapdoor to enter the underground of the Grand Tree.", pickaxe);
		moveToStrongBase2.addDialogStep("Climb Down.");
		moveToStrongBase.addSubSteps(moveToStrongBase2);
		mineGold = new ObjectStep(this, ObjectID.GOLD_ROCKS, new WorldPoint(2490, 9916, 0),
			"Mine some gold ore underneath the Grand Tree.", true, pickaxe);

		moveToBrimstailCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_17209, new WorldPoint(2403, 3419, 0),
			"Enter Brimstail's cave.", crystalSawSeed);
		crystalSaw = new ObjectStep(this, ObjectID.SINGING_BOWL, new WorldPoint(2388, 9813, 0),
			"Turn your crystal saw seed into a crystal saw.", crystalSawSeed);

		agiShortcut = new ObjectStep(this, ObjectID.ROCKS_16534, new WorldPoint(2487, 3515, 0),
			"Take the agility shortcut from the Grand Tree to Otto's Grotto.");

		moveToEagle = new ObjectStep(this, 19790, new WorldPoint(2329, 3495, 0),
			"Enter the cave at the top of Eagles' Peak. " +
				"You can use a fairy ring to (AKQ), then head south to get there easily.");
		eagleFeldip = new NpcStep(this, NpcID.JUNGLE_EAGLE, new WorldPoint(2027, 4964, 3),
			"Use a rope on the Jungle Eagle to travel to the Feldip Hills area.", rope.highlighted());
		eagleFeldip.addIcon(ItemID.ROPE);

		spinedLarupia = new NpcStep(this, NpcID.SPINED_LARUPIA, new WorldPoint(2551, 2904, 0),
			"Place logs over a pit in the Feldip hunter area, and poke a larupia with a teasing stick. " +
				"Jump over the pits until the larupia falls in and loot it.", teasingStick, logs, knife);

		gliderToFeldip = new DetailedQuestStep(this, "Travel to the Feldip Hills by Gnome Glider.");

		moveToApeBass = new DetailedQuestStep(this, "Travel to Ape Atoll.", bigFishingNet);
		apeBass = new NpcStep(this, NpcID.FISHING_SPOT_5234, new WorldPoint(2705, 2700, 0),
			"Fish some bass on Ape Atoll.", bigFishingNet);

		moveToApeTeak = new DetailedQuestStep(this, "Travel to Ape Atoll.", axe, tinderbox);
		apeTeakChop = new ObjectStep(this, ObjectID.TEAK_TREE, new WorldPoint(2773, 2698, 0),
			"Chop some teak logs on Ape Atoll.", axe, tinderbox);
		apeTeakBurn = new ItemStep(this, "Burn some teak logs on Ape Atoll.",
			teakLogs.highlighted(), tinderbox.highlighted());

		moveToPest = new NpcStep(this, NpcID.SQUIRE_1770, new WorldPoint(3041, 3202, 0),
			"Talk to the squire to travel to the Void Knights' Outpost. Alternatively, use the pest control minigame teleport.");
		moveToPest.addDialogStep("I'd like to go to your outpost.");
		interPest = new ObjectStep(this, ObjectID.GANGPLANK_25631, new WorldPoint(2643, 2644, 0),
			"Complete an intermediate game of Pest Control.");

		chompyHat = new NpcStep(this, NpcID.RANTZ, new WorldPoint(2628, 2979, 0),
			"Claim any Chompy bird hat from Rantz. Kill chompy birds until you have 125 kills. \n \nYou can check " +
				"your kill count by right clicking 'Check Kills' on an ogre bow.",
			ogreBow, ogreArrows, ogreBellows);
		chompyHat.addDialogStep("Can I have a hat please?");

		claimReward = new NpcStep(this, NpcID.ELDER_GNOME_CHILD, new WorldPoint(2466, 3460, 0),
			"Talk to the Elder gnome child in Gnome Stronghold to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear, ogreBellows, ogreBow, ogreArrows, teasingStick, logs, knife, bigFishingNet, axe,
			tinderbox, rope, gnomebowl, gianneDough, chocolateBar, equaLeaf, potOfCream, chocolateDust, crystalSawSeed,
			pickaxe);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, fairyAccess, seedPod);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new CombatLevelRequirement(70));
		reqs.add(new SkillRequirement(Skill.AGILITY, 37));
		reqs.add(new SkillRequirement(Skill.COOKING, 42));
		reqs.add(new SkillRequirement(Skill.FIREMAKING, 35));
		reqs.add(new SkillRequirement(Skill.FISHING, 46));
		reqs.add(new SkillRequirement(Skill.HUNTER, 31));
		reqs.add(new SkillRequirement(Skill.MINING, 40));
		reqs.add(new SkillRequirement(Skill.WOODCUTTING, 35));

		reqs.add(bigChompy);
		reqs.add(eaglesPeak);
		reqs.add(monkeyMadnessI);
		reqs.add(eyesOfGlouphrie);
		reqs.add(oneSmallFavour);

		return reqs;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Western banner 2", ItemID.WESTERN_BANNER_2),
			new ItemReward("7,500 Exp. Lamp (Any skill over 40)", ItemID.ANTIQUE_LAMP)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("50% chance of 2 chompy birds appearing when Chompy bird hunting"),
			new UnlockReward("Crystal saw holds twice as many charges (56 up from 28)"),
			new UnlockReward("50 free ogre arrows every day from Rantz")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails spiritSteps = new PanelDetails("Spirit Tree To Gnome Stronghold",
			Collections.singletonList(spiritToStronghold), treeGnomeVillage);
		spiritSteps.setDisplayCondition(notSpiritToStronghold);
		spiritSteps.setLockingStep(spiritToStrongholdTask);
		allSteps.add(spiritSteps);

		PanelDetails chocoSteps = new PanelDetails("Chocolate Bomb", Arrays.asList(moveToStrongFirstChoco,
			chocolateBomb), new SkillRequirement(Skill.COOKING, 42), gnomebowl, gianneDough, chocolateBar, equaLeaf,
			potOfCream, chocolateDust);
		chocoSteps.setDisplayCondition(notChocolateBomb);
		chocoSteps.setLockingStep(chocolateBombTask);
		allSteps.add(chocoSteps);

		PanelDetails restSteps = new PanelDetails("Gnome Restaurant Delivery",
			Arrays.asList(moveToStrongFirstDelivery, gnomeDelivery), new SkillRequirement(Skill.COOKING, 42));
		restSteps.setDisplayCondition(notGnomeDelivery);
		restSteps.setLockingStep(gnomeDeliveryTask);
		allSteps.add(restSteps);

		PanelDetails goldSteps = new PanelDetails("Gold Under The Grand Tree",
			Arrays.asList(moveToStrongBase, mineGold), new SkillRequirement(Skill.MINING, 40), pickaxe);
		goldSteps.setDisplayCondition(notMineGold);
		goldSteps.setLockingStep(mineGoldTask);
		allSteps.add(goldSteps);

		PanelDetails sawSteps = new PanelDetails("Crystal Saw", Arrays.asList(moveToBrimstailCave, crystalSaw), eyesOfGlouphrie,
			crystalSawSeed);
		sawSteps.setDisplayCondition(notCrystalSaw);
		sawSteps.setLockingStep(crystalSawTask);
		allSteps.add(sawSteps);

		PanelDetails agiSteps = new PanelDetails("Agility Shortcut To Otto", Collections.singletonList(agiShortcut),
			new SkillRequirement(Skill.AGILITY, 37), treeGnomeVillage, grandTree);
		agiSteps.setDisplayCondition(notAgiShortcut);
		agiSteps.setLockingStep(agiShortcutTask);
		allSteps.add(agiSteps);

		PanelDetails eagleSteps = new PanelDetails("Eagle To Feldip Hills", Arrays.asList(moveToEagle, eagleFeldip));
		eagleSteps.setDisplayCondition(notEagleFeldip);
		eagleSteps.setLockingStep(eagleFeldipTask);
		allSteps.add(eagleSteps);

		PanelDetails laruSteps = new PanelDetails("Spined Larupia", Collections.singletonList(spinedLarupia),
			new SkillRequirement(Skill.HUNTER, 31), teasingStick, knife, logs);
		laruSteps.setDisplayCondition(notSpinedLarupia);
		laruSteps.setLockingStep(spinedLarupiaTask);
		allSteps.add(laruSteps);

		PanelDetails gliderSteps = new PanelDetails("Glider To Feldip Hills", Collections.singletonList(gliderToFeldip),
			grandTree, oneSmallFavour);
		gliderSteps.setDisplayCondition(notGliderToFeldip);
		gliderSteps.setLockingStep(gliderToFeldipTask);
		allSteps.add(gliderSteps);

		PanelDetails bassSteps = new PanelDetails("Bass On Ape Atoll", Arrays.asList(moveToApeBass, apeBass),
			new SkillRequirement(Skill.FISHING, 46), monkeyMadnessI, bigFishingNet);
		bassSteps.setDisplayCondition(notApeBass);
		bassSteps.setLockingStep(apeBassTask);
		allSteps.add(bassSteps);

		PanelDetails teakSteps = new PanelDetails("Teaks On Ape Atoll", Arrays.asList(moveToApeTeak, apeTeakChop,
			apeTeakBurn), new SkillRequirement(Skill.FIREMAKING, 35), new SkillRequirement(Skill.WOODCUTTING, 35),
			monkeyMadnessI, axe, tinderbox);
		teakSteps.setDisplayCondition(notApeTeak);
		teakSteps.setLockingStep(apeTeakTask);
		allSteps.add(teakSteps);

		PanelDetails pestSteps = new PanelDetails("Intermediate Pest Control", Arrays.asList(moveToPest, interPest),
			new CombatLevelRequirement(70), combatGear);
		pestSteps.setDisplayCondition(notInterPest);
		pestSteps.setLockingStep(interPestTask);
		allSteps.add(pestSteps);

		PanelDetails hatSteps = new PanelDetails("Chompy Bird Hat", Collections.singletonList(chompyHat),
			new SkillRequirement(Skill.RANGED, 30), bigChompy, ogreBellows, ogreBow, ogreArrows);
		hatSteps.setDisplayCondition(notChompyHat);
		hatSteps.setLockingStep(chompyHatTask);
		allSteps.add(hatSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
