/*
 * Copyright (c) 2021, Obasill <https://github.com/Obasill>
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
package com.questhelper.achievementdiaries.varrock;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
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
	quest = QuestHelperQuest.VARROCK_EASY
)
public class VarrockEasy extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement coins, pickaxe, log, axe, bone, softClay, earthTali, essence, flyRod, feathers;

	ItemRequirement unfiredBowl;

	// Items recommended
	ItemRequirement food;

	// Quests required
	Requirement runeMysteries;

	Requirement notThessalia, notAubury, notIron, notPlank, notStrongholdSecond, notFence, notNews, notDyingTree,
		notDogBone, notBowl, notKudos, notTrout, notTeaStall, notEarthRune, notMoreKudos, madeBowl;

	QuestStep claimReward, thessalia, aubury, iron, plank, moveToStronghold1, moveToStronghold2, fence, dyingTree,
		news, dogBone, potteryWheel, bowl, kudos, moreKudos, moveToEarthRune, earthRune, trout, teaStall;

	Zone stronghold1, earth, potteryRoom;

	ZoneRequirement inStronghold1, inEarth, inPotteryRoom;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doEasy = new ConditionalStep(this, claimReward);
		doEasy.addStep(notThessalia, thessalia);
		doEasy.addStep(notNews, news);
		doEasy.addStep(notDogBone, dogBone);
		doEasy.addStep(new Conditions(notKudos, notMoreKudos), kudos);
		doEasy.addStep(notKudos, moreKudos);
		doEasy.addStep(notAubury, aubury);
		doEasy.addStep(notTeaStall, teaStall);
		doEasy.addStep(notPlank, plank);
		doEasy.addStep(notDyingTree, dyingTree);
		doEasy.addStep(new Conditions(notEarthRune, inEarth), earthRune);
		doEasy.addStep(notEarthRune, moveToEarthRune);
		doEasy.addStep(notIron, iron);
		doEasy.addStep(notFence, fence);
		doEasy.addStep(notTrout, trout);
		doEasy.addStep(new Conditions(notBowl, madeBowl), bowl);
		doEasy.addStep(notBowl, potteryWheel);
		doEasy.addStep(new Conditions(LogicType.OR, notStrongholdSecond, inStronghold1), moveToStronghold2);
		doEasy.addStep(notStrongholdSecond, moveToStronghold1);

		return doEasy;
	}

	public void setupRequirements()
	{
		notThessalia = new VarplayerRequirement(1176, false, 1);
		notAubury = new VarplayerRequirement(1176, false, 2);
		notIron = new VarplayerRequirement(1176, false, 3);
		notPlank = new VarplayerRequirement(1176, false, 4);
		notStrongholdSecond = new VarplayerRequirement(1176, false, 5);
		notFence = new VarplayerRequirement(1176, false, 6);
		notDyingTree = new VarplayerRequirement(1176, false, 7);
		notNews = new VarplayerRequirement(1176, false, 8);
		notDogBone = new VarplayerRequirement(1176, false, 9);
		notBowl = new VarplayerRequirement(1176, false, 10);
		notKudos = new VarplayerRequirement(1176, false, 11);
		notEarthRune = new VarplayerRequirement(1176, false, 12);
		notTrout = new VarplayerRequirement(1176, false, 13);
		notTeaStall = new VarplayerRequirement(1176, false, 14);

		notMoreKudos = new VarbitRequirement(3637, Operation.GREATER_EQUAL, 50, "50+ Kudos");

		coins = new ItemRequirement("Coins", ItemCollections.getCoins(), 150).showConditioned(new Conditions(LogicType.OR,
			notNews, notPlank));
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes()).showConditioned(notIron);
		log = new ItemRequirement("Logs", ItemID.LOGS).showConditioned(notPlank);
		axe = new ItemRequirement("Any axe", ItemCollections.getAxes()).showConditioned(notDyingTree);
		bone = new ItemRequirement("Bones", ItemCollections.getBones()).showConditioned(notDogBone);
		softClay = new ItemRequirement("Soft clay", ItemID.SOFT_CLAY).showConditioned(notBowl);
		earthTali = new ItemRequirement("Access to Earth altar, or travel through abyss", ItemCollections.getEarthAltar()).showConditioned(notEarthRune);
		essence = new ItemRequirement("Essence", ItemCollections.getEssenceLow()).showConditioned(notEarthRune);
		flyRod = new ItemRequirement("Fly fishing rod", ItemID.FLY_FISHING_ROD).showConditioned(notTrout);
		feathers = new ItemRequirement("Feather", ItemID.FEATHER).showConditioned(notTrout);
		unfiredBowl = new ItemRequirement("Unfired bowl", ItemID.UNFIRED_BOWL);

		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);

		runeMysteries = new QuestRequirement(QuestHelperQuest.RUNE_MYSTERIES, QuestState.FINISHED);

		inStronghold1 = new ZoneRequirement(stronghold1);
		inEarth = new ZoneRequirement(earth);
		inPotteryRoom = new ZoneRequirement(potteryRoom);

		madeBowl = new ChatMessageRequirement(
			inPotteryRoom,
			"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
		);
		((ChatMessageRequirement) madeBowl).setInvalidateRequirement(
			new ChatMessageRequirement(
				new Conditions(LogicType.NOR, inPotteryRoom),
				"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
			)
		);
	}

	public void loadZones()
	{
		stronghold1 = new Zone(new WorldPoint(1854, 5248, 0), new WorldPoint(1918, 5183, 0));
		earth = new Zone(new WorldPoint(2624, 4863, 0), new WorldPoint(2687, 4800, 0));
		potteryRoom = new Zone(new WorldPoint(3082, 3407, 0), new WorldPoint(3087, 3411, 0));
	}

	public void setupSteps()
	{
		thessalia = new NpcStep(this, NpcID.THESSALIA, new WorldPoint(3206, 3417, 0),
			"Browse Thessalia's store.");
		aubury = new NpcStep(this, NpcID.AUBURY, new WorldPoint(3253, 3401, 0),
			"Teleport to the essence mine via Aubury.");
		iron = new ObjectStep(this, ObjectID.ROCKS_11365, new WorldPoint(3288, 3370, 0),
			"Mine iron south-east of Varrock.", pickaxe);
		plank = new NpcStep(this, NpcID.SAWMILL_OPERATOR, new WorldPoint(3302, 3492, 0),
			"Make a regular plank at the sawmill.", log);
		moveToStronghold1 = new ObjectStep(this, ObjectID.ENTRANCE_20790, new WorldPoint(3081, 3420, 0),
			"Enter the Stronghold of Security.");
		moveToStronghold2 = new ObjectStep(this, ObjectID.LADDER_20785, new WorldPoint(1902, 5222, 0),
			"Go to the 2nd floor of the Stronghold of Security.");
		fence = new ObjectStep(this, ObjectID.FENCE_16518, new WorldPoint(3240, 3335, 0),
			"Jump the fence south of Varrock.");
		dyingTree = new ObjectStep(this, ObjectID.DYING_TREE, new WorldPoint(3308, 3495, 0),
			"Chop down a dying tree in the sawmill area.", axe);
		news = new NpcStep(this, NpcID.BENNY, new WorldPoint(3219, 3431, 0),
			"Speak with Benny in the Varrock Square to purchase a newspaper.", coins.quantity(50));
		news.addDialogSteps("Can I have a newspaper, please?", "Sure, here you go...");
		dogBone = new NpcStep(this, NpcID.STRAY_DOG_2922, new WorldPoint(3184, 3431, 0),
			"Give the stray dog a bone. If the dog isn't nearby consider changing worlds.");
		dogBone.addIcon(ItemID.BONES);
		potteryWheel = new ObjectStep(this, ObjectID.POTTERS_WHEEL_14887, new WorldPoint(3087, 3410, 0),
			"Use the potters wheel in Barbarian Village to make an unfired bowl.", softClay);
		bowl = new ObjectStep(this, ObjectID.POTTERY_OVEN_11601, new WorldPoint(3085, 3407, 0),
			"Put the unfired bowl in the oven.", unfiredBowl);
		bowl.addIcon(ItemID.UNFIRED_BOWL);
		moreKudos = new DetailedQuestStep(this,
			"Get more kudos from either quests, miniquests, or turning in fossils.");
		kudos = new NpcStep(this, NpcID.CURATOR_HAIG_HALEN, new WorldPoint(3258, 3449, 0),
			"Speak to Curator Haig Halen.", notMoreKudos);
		moveToEarthRune = new ObjectStep(this, 34816, new WorldPoint(3306, 3474, 0),
			"Travel to the earth altar or go through the abyss.", earthTali, essence);
		moveToEarthRune.addIcon(ItemID.EARTH_TALISMAN);
		earthRune = new ObjectStep(this, 34763, new WorldPoint(2658, 4841, 0),
			"Craft an earth rune.", essence);
		trout = new NpcStep(this, NpcID.ROD_FISHING_SPOT_1526, new WorldPoint(3106, 3428, 0),
			"Fish a trout in the River Lum.", flyRod, feathers);
		teaStall = new ObjectStep(this, ObjectID.TEA_STALL, new WorldPoint(3270, 3411, 0),
			"Steal from the tea stall in Varrock.");

		claimReward = new NpcStep(this, NpcID.TOBY, new WorldPoint(3225, 3415, 0),
			"Talk to Toby in Varrock to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(coins.quantity(150), pickaxe, log, axe, bone, softClay, earthTali, essence, flyRod, feathers);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Collections.singletonList(food);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.AGILITY, 13));
		reqs.add(new SkillRequirement(Skill.CRAFTING, 8));
		reqs.add(new SkillRequirement(Skill.FISHING, 20));
		reqs.add(new SkillRequirement(Skill.MINING, 15));
		reqs.add(new SkillRequirement(Skill.RUNECRAFT, 9));
		reqs.add(new SkillRequirement(Skill.THIEVING, 5));

		reqs.add(runeMysteries);

		return reqs;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Varrock Armor (1)", ItemID.VARROCK_ARMOUR_1, 1),
			new ItemReward("2,500 Exp. Lamp (Any skill over 30)", ItemID.ANTIQUE_LAMP, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("10% Chance to mine 2 ores at once up to gold ore"),
			new UnlockReward("10% Chance of smelting 2 bars at once up to Steel when using the Edgeville furnace"),
			new UnlockReward("Zaff will sell 15 Battlestaves per day for 7,000 Coins each"),
			new UnlockReward("The Skull sceptre will now hold 14 charges"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails thessaliaSteps = new PanelDetails("Browse Thessalia's Store",
			Collections.singletonList(thessalia));
		thessaliaSteps.setDisplayCondition(notThessalia);
		allSteps.add(thessaliaSteps);

		PanelDetails newsSteps = new PanelDetails("Buy Newspaper", Collections.singletonList(news), coins.quantity(50));
		newsSteps.setDisplayCondition(notNews);
		allSteps.add(newsSteps);

		PanelDetails dogBoneSteps = new PanelDetails("Give a Dog a Bone", Collections.singletonList(dogBone), bone);
		dogBoneSteps.setDisplayCondition(notDogBone);
		allSteps.add(dogBoneSteps);

		PanelDetails kudosSteps = new PanelDetails("Speak to Haig Halen", Collections.singletonList(kudos), notMoreKudos);
		kudosSteps.setDisplayCondition(notKudos);
		allSteps.add(kudosSteps);

		PanelDetails auburySteps = new PanelDetails("Teleport to Essence Mine", Collections.singletonList(aubury), runeMysteries);
		auburySteps.setDisplayCondition(notAubury);
		allSteps.add(auburySteps);

		PanelDetails teaStallSteps = new PanelDetails("Steal from the Tea Stall", Collections.singletonList(teaStall),
			new SkillRequirement(Skill.THIEVING, 5));
		teaStallSteps.setDisplayCondition(notTeaStall);
		allSteps.add(teaStallSteps);

		PanelDetails plankSteps = new PanelDetails("Make Plank", Collections.singletonList(plank), coins.quantity(100), log);
		plankSteps.setDisplayCondition(notPlank);
		allSteps.add(plankSteps);

		PanelDetails dyingTreeSteps = new PanelDetails("Chop Down Dying Tree", Collections.singletonList(dyingTree), axe);
		dyingTreeSteps.setDisplayCondition(notDyingTree);
		allSteps.add(dyingTreeSteps);

		PanelDetails earthRuneSteps = new PanelDetails("Craft an Earth Rune", Arrays.asList(moveToEarthRune,
			earthRune), new SkillRequirement(Skill.RUNECRAFT, 9), essence, earthTali);
		earthRuneSteps.setDisplayCondition(notEarthRune);
		allSteps.add(earthRuneSteps);

		PanelDetails ironSteps = new PanelDetails("Mine Iron South East", Collections.singletonList(iron),
			new SkillRequirement(Skill.MINING, 15), pickaxe);
		ironSteps.setDisplayCondition(notIron);
		allSteps.add(ironSteps);

		PanelDetails fenceSteps = new PanelDetails("Jump the Fence", Collections.singletonList(fence), new SkillRequirement(Skill.AGILITY, 13));
		fenceSteps.setDisplayCondition(notFence);
		allSteps.add(fenceSteps);

		PanelDetails troutSteps = new PanelDetails("Fish a Trout", Collections.singletonList(trout),
			new SkillRequirement(Skill.FISHING, 20), flyRod, feathers);
		troutSteps.setDisplayCondition(notTrout);
		allSteps.add(troutSteps);

		PanelDetails bowlSteps = new PanelDetails("Spin a Bowl in Barbarian Village", Arrays.asList(potteryWheel, bowl),
			new SkillRequirement(Skill.CRAFTING, 8), softClay);
		bowlSteps.setDisplayCondition(notBowl);
		allSteps.add(bowlSteps);

		PanelDetails strongholdSecondFloorSteps = new PanelDetails("Stronghold Second Floor", Arrays.asList(moveToStronghold1,
			moveToStronghold2), food);
		strongholdSecondFloorSteps.setDisplayCondition(notStrongholdSecond);
		allSteps.add(strongholdSecondFloorSteps);

		PanelDetails finishOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishOffSteps);

		return allSteps;
	}
}
