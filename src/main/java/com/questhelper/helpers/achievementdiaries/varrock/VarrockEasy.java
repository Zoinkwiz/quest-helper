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
package com.questhelper.helpers.achievementdiaries.varrock;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

	ConditionalStep thessaliaTask, auburyTask, ironTask, plankTask, strongholdSecondTask, fenceTask, newsTask,
		dyingTreeTask, dogBoneTask, bowlTask, kudosTask, troutTask, teaStallTask, earthRuneTask;

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupSteps();

		ConditionalStep doEasy = new ConditionalStep(this, claimReward);

		thessaliaTask = new ConditionalStep(this, thessalia);
		doEasy.addStep(notThessalia, thessaliaTask);

		newsTask = new ConditionalStep(this, news);
		doEasy.addStep(notNews, newsTask);

		dogBoneTask = new ConditionalStep(this, dogBone);
		doEasy.addStep(notDogBone, dogBoneTask);

		kudosTask = new ConditionalStep(this, moreKudos);
		kudosTask.addStep(new Conditions(notKudos, notMoreKudos), kudos);
		doEasy.addStep(notKudos, kudosTask);

		auburyTask = new ConditionalStep(this, aubury);
		doEasy.addStep(notAubury, auburyTask);

		teaStallTask = new ConditionalStep(this, teaStall);
		doEasy.addStep(notTeaStall, teaStallTask);

		plankTask = new ConditionalStep(this, plank);
		doEasy.addStep(notPlank, plankTask);

		dyingTreeTask = new ConditionalStep(this, dyingTree);
		doEasy.addStep(notDyingTree, dyingTreeTask);

		earthRuneTask = new ConditionalStep(this, moveToEarthRune);
		earthRuneTask.addStep(new Conditions(notEarthRune, inEarth), earthRune);
		doEasy.addStep(notEarthRune, earthRuneTask);

		ironTask = new ConditionalStep(this, iron);
		doEasy.addStep(notIron, ironTask);

		fenceTask = new ConditionalStep(this, fence);
		doEasy.addStep(notFence, fenceTask);

		troutTask = new ConditionalStep(this, trout);
		doEasy.addStep(notTrout, troutTask);

		bowlTask = new ConditionalStep(this, potteryWheel);
		bowlTask.addStep(new Conditions(notBowl, madeBowl), bowl);
		doEasy.addStep(notBowl, bowlTask);

		strongholdSecondTask = new ConditionalStep(this, moveToStronghold1);
		strongholdSecondTask.addStep(new Conditions(LogicType.OR, notStrongholdSecond, inStronghold1), moveToStronghold2);
		doEasy.addStep(notStrongholdSecond, strongholdSecondTask);

		return doEasy;
	}

	@Override
	protected void setupRequirements()
	{
		notThessalia = new VarplayerRequirement(VarPlayerID.VARROCK_ACHIEVEMENT_DIARY, false, 1);
		notAubury = new VarplayerRequirement(VarPlayerID.VARROCK_ACHIEVEMENT_DIARY, false, 2);
		notIron = new VarplayerRequirement(VarPlayerID.VARROCK_ACHIEVEMENT_DIARY, false, 3);
		notPlank = new VarplayerRequirement(VarPlayerID.VARROCK_ACHIEVEMENT_DIARY, false, 4);
		notStrongholdSecond = new VarplayerRequirement(VarPlayerID.VARROCK_ACHIEVEMENT_DIARY, false, 5);
		notFence = new VarplayerRequirement(VarPlayerID.VARROCK_ACHIEVEMENT_DIARY, false, 6);
		notDyingTree = new VarplayerRequirement(VarPlayerID.VARROCK_ACHIEVEMENT_DIARY, false, 7);
		notNews = new VarplayerRequirement(VarPlayerID.VARROCK_ACHIEVEMENT_DIARY, false, 8);
		notDogBone = new VarplayerRequirement(VarPlayerID.VARROCK_ACHIEVEMENT_DIARY, false, 9);
		notBowl = new VarplayerRequirement(VarPlayerID.VARROCK_ACHIEVEMENT_DIARY, false, 10);
		notKudos = new VarplayerRequirement(VarPlayerID.VARROCK_ACHIEVEMENT_DIARY, false, 11);
		notEarthRune = new VarplayerRequirement(VarPlayerID.VARROCK_ACHIEVEMENT_DIARY, false, 12);
		notTrout = new VarplayerRequirement(VarPlayerID.VARROCK_ACHIEVEMENT_DIARY, false, 13);
		notTeaStall = new VarplayerRequirement(VarPlayerID.VARROCK_ACHIEVEMENT_DIARY, false, 14);

		notMoreKudos = new VarbitRequirement(VarbitID.VM_KUDOS, Operation.GREATER_EQUAL, 50, "50+ Kudos");

		coins = new ItemRequirement("Coins", ItemCollections.COINS, 150).showConditioned(new Conditions(LogicType.OR,
			notNews, notPlank));
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).showConditioned(notIron).isNotConsumed();
		log = new ItemRequirement("Logs", ItemID.LOGS).showConditioned(notPlank);
		axe = new ItemRequirement("Any axe", ItemCollections.AXES).showConditioned(notDyingTree).isNotConsumed();
		bone = new ItemRequirement("Bones", ItemCollections.BONES).showConditioned(notDogBone);
		softClay = new ItemRequirement("Soft clay", ItemID.SOFTCLAY).showConditioned(notBowl);
		earthTali = new ItemRequirement("Access to the Earth Altar", ItemCollections.EARTH_ALTAR)
			.showConditioned(notEarthRune).isNotConsumed();
		earthTali.setTooltip("Earth Talisman/Tiara, Elemental Talisman/Tiara, RC-skill cape or via Abyss");
		essence = new ItemRequirement("Essence", ItemCollections.ESSENCE_LOW).showConditioned(notEarthRune);
		flyRod = new ItemRequirement("Fly fishing rod", ItemID.FLY_FISHING_ROD).showConditioned(notTrout).isNotConsumed();
		feathers = new ItemRequirement("Feather", ItemID.FEATHER, 10).showConditioned(notTrout);
		unfiredBowl = new ItemRequirement("Unfired bowl", ItemID.BOWL_UNFIRED);

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);

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

	@Override
	protected void setupZones()
	{
		stronghold1 = new Zone(new WorldPoint(1854, 5248, 0), new WorldPoint(1918, 5183, 0));
		earth = new Zone(new WorldPoint(2624, 4863, 0), new WorldPoint(2687, 4800, 0));
		potteryRoom = new Zone(new WorldPoint(3082, 3407, 0), new WorldPoint(3087, 3411, 0));
	}

	public void setupSteps()
	{
		thessalia = new NpcStep(this, NpcID.THESSALIA_NORMAL, new WorldPoint(3206, 3417, 0),
			"Browse Thessalia's store.");
		aubury = new NpcStep(this, NpcID.BIM_AUBURY, new WorldPoint(3253, 3401, 0),
			"Teleport to the essence mine via Aubury.");
		iron = new ObjectStep(this, ObjectID.IRONROCK2, new WorldPoint(3288, 3370, 0),
			"Mine iron south-east of Varrock.", pickaxe);
		plank = new NpcStep(this, NpcID.POH_SAWMILL_OPP, new WorldPoint(3302, 3492, 0),
			"Make a regular plank at the sawmill.", log);
		plank.addWidgetHighlightWithItemIdRequirement(270, 13, ItemID.LOGS, true);
		moveToStronghold1 = new ObjectStep(this, ObjectID.SOS_DUNG_ENT_OPEN, new WorldPoint(3081, 3420, 0),
			"Enter the Stronghold of Security.");
		moveToStronghold2 = new ObjectStep(this, ObjectID.SOS_WAR_LADD_DOWN, new WorldPoint(1902, 5222, 0),
			"Go to the 2nd floor of the Stronghold of Security.");
		fence = new ObjectStep(this, ObjectID.LUMBRIDGE_SC_FENCEJUMP, new WorldPoint(3240, 3335, 0),
			"Jump the fence south of Varrock.");
		dyingTree = new ObjectStep(this, ObjectID.FAI_VARROCK_DEAD_TREE, new WorldPoint(3308, 3495, 0),
			"Chop down a dying tree in the sawmill area.", axe);
		news = new NpcStep(this, NpcID.QIP_SOA_NEWSPAPERSELLER, new WorldPoint(3219, 3431, 0),
			"Speak with Benny in the Varrock Square to purchase a newspaper.", coins.quantity(50));
		news.addDialogSteps("Can I have a newspaper, please?", "Sure, here you go...");
		dogBone = new NpcStep(this, NpcID.DOG_STRAY2, new WorldPoint(3184, 3431, 0),
			"Give the stray dog a bone. If the dog isn't nearby consider changing worlds.", bone.highlighted());
		dogBone.addIcon(ItemID.BONES);
		potteryWheel = new ObjectStep(this, ObjectID.POTTERYWHEEL, new WorldPoint(3087, 3410, 0),
			"Use the potters wheel in Barbarian Village to make an unfired bowl.", softClay);
		potteryWheel.addWidgetHighlightWithItemIdRequirement(270, 13, ItemID.BOWL_UNFIRED, true);
		bowl = new ObjectStep(this, ObjectID.FAI_BARBARIAN_POTTERY_OVEN, new WorldPoint(3085, 3407, 0),
			"Put the unfired bowl in the oven.", unfiredBowl);
		bowl.addIcon(ItemID.BOWL_UNFIRED);
		bowl.addWidgetHighlightWithItemIdRequirement(270, 13, ItemID.BOWL_UNFIRED, true);

		moreKudos = new DetailedQuestStep(this,
			"Get more kudos from either quests, miniquests, or turning in fossils.");
		kudos = new NpcStep(this, NpcID.CURATOR, new WorldPoint(3258, 3449, 0),
			"Speak to Curator Haig Halen.", notMoreKudos);
		moveToEarthRune = new ObjectStep(this, ObjectID.EARTHTEMPLE_RUINED, new WorldPoint(3306, 3474, 0),
			"Travel to the earth altar or go through the abyss.", earthTali.highlighted(), essence);
		moveToEarthRune.addIcon(ItemID.EARTH_TALISMAN);
		earthRune = new ObjectStep(this, ObjectID.EARTH_ALTAR, new WorldPoint(2658, 4841, 0),
			"Craft an earth rune.", essence);
		trout = new NpcStep(this, NpcID._0_48_53_FRESHFISH, new WorldPoint(3106, 3428, 0),
			"Fish a trout in the River Lum at Barbarian Village.", flyRod, feathers);
		teaStall = new ObjectStep(this, ObjectID.TEA_STALL, new WorldPoint(3270, 3411, 0),
			"Steal from the tea stall in Varrock.");

		claimReward = new NpcStep(this, NpcID.TOBY_VARROCK_DIARY, new WorldPoint(3225, 3415, 0),
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
		reqs.add(new SkillRequirement(Skill.AGILITY, 13, true));
		reqs.add(new SkillRequirement(Skill.CRAFTING, 8, true));
		reqs.add(new SkillRequirement(Skill.FISHING, 20, true));
		reqs.add(new SkillRequirement(Skill.MINING, 15, true));
		reqs.add(new SkillRequirement(Skill.RUNECRAFT, 9, true));
		reqs.add(new SkillRequirement(Skill.THIEVING, 5, true));

		reqs.add(runeMysteries);

		return reqs;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Varrock Armor (1)", ItemID.VARROCK_ARMOUR_EASY, 1),
			new ItemReward("2,500 Exp. Lamp (Any skill over 30)", ItemID.THOSF_REWARD_LAMP, 1));
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
		thessaliaSteps.setLockingStep(thessaliaTask);
		allSteps.add(thessaliaSteps);

		PanelDetails newsSteps = new PanelDetails("Buy Newspaper", Collections.singletonList(news), coins.quantity(50));
		newsSteps.setDisplayCondition(notNews);
		newsSteps.setLockingStep(newsTask);
		allSteps.add(newsSteps);

		PanelDetails dogBoneSteps = new PanelDetails("Give a Dog a Bone", Collections.singletonList(dogBone), bone);
		dogBoneSteps.setDisplayCondition(notDogBone);
		dogBoneSteps.setLockingStep(dogBoneTask);
		allSteps.add(dogBoneSteps);

		PanelDetails kudosSteps = new PanelDetails("Speak to Haig Halen", Arrays.asList(moreKudos, kudos),
			notMoreKudos);
		kudosSteps.setDisplayCondition(notKudos);
		kudosSteps.setLockingStep(kudosTask);
		allSteps.add(kudosSteps);

		PanelDetails auburySteps = new PanelDetails("Teleport to Essence Mine", Collections.singletonList(aubury), runeMysteries);
		auburySteps.setDisplayCondition(notAubury);
		auburySteps.setLockingStep(auburyTask);
		allSteps.add(auburySteps);

		PanelDetails teaStallSteps = new PanelDetails("Steal from the Tea Stall", Collections.singletonList(teaStall),
			new SkillRequirement(Skill.THIEVING, 5, true));
		teaStallSteps.setDisplayCondition(notTeaStall);
		teaStallSteps.setLockingStep(teaStallTask);
		allSteps.add(teaStallSteps);

		PanelDetails plankSteps = new PanelDetails("Make Plank", Collections.singletonList(plank), coins.quantity(100), log);
		plankSteps.setDisplayCondition(notPlank);
		plankSteps.setLockingStep(plankTask);
		allSteps.add(plankSteps);

		PanelDetails dyingTreeSteps = new PanelDetails("Chop Down Dying Tree", Collections.singletonList(dyingTree), axe);
		dyingTreeSteps.setDisplayCondition(notDyingTree);
		dyingTreeSteps.setLockingStep(dyingTreeTask);
		allSteps.add(dyingTreeSteps);

		PanelDetails earthRuneSteps = new PanelDetails("Craft an Earth Rune", Arrays.asList(moveToEarthRune,
			earthRune), new SkillRequirement(Skill.RUNECRAFT, 9, true), essence, earthTali);
		earthRuneSteps.setDisplayCondition(notEarthRune);
		earthRuneSteps.setLockingStep(earthRuneTask);
		allSteps.add(earthRuneSteps);

		PanelDetails ironSteps = new PanelDetails("Mine Iron South East", Collections.singletonList(iron),
			new SkillRequirement(Skill.MINING, 15, true), pickaxe);
		ironSteps.setDisplayCondition(notIron);
		ironSteps.setLockingStep(ironTask);
		allSteps.add(ironSteps);

		PanelDetails fenceSteps = new PanelDetails("Jump the Fence", Collections.singletonList(fence), new SkillRequirement(Skill.AGILITY, 13, true));
		fenceSteps.setDisplayCondition(notFence);
		fenceSteps.setLockingStep(fenceTask);
		allSteps.add(fenceSteps);

		PanelDetails troutSteps = new PanelDetails("Fish a Trout", Collections.singletonList(trout),
			new SkillRequirement(Skill.FISHING, 20, true), flyRod, feathers);
		troutSteps.setDisplayCondition(notTrout);
		troutSteps.setLockingStep(troutTask);
		allSteps.add(troutSteps);

		PanelDetails bowlSteps = new PanelDetails("Spin a Bowl in Barbarian Village", Arrays.asList(potteryWheel, bowl),
			new SkillRequirement(Skill.CRAFTING, 8, true), softClay);
		bowlSteps.setDisplayCondition(notBowl);
		bowlSteps.setLockingStep(bowlTask);
		allSteps.add(bowlSteps);

		PanelDetails strongholdSecondFloorSteps = new PanelDetails("Stronghold Second Floor", Arrays.asList(moveToStronghold1,
			moveToStronghold2), food);
		strongholdSecondFloorSteps.setDisplayCondition(notStrongholdSecond);
		strongholdSecondFloorSteps.setLockingStep(strongholdSecondTask);
		allSteps.add(strongholdSecondFloorSteps);

		PanelDetails finishOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishOffSteps);

		return allSteps;
	}
}
