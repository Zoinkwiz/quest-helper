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
package com.questhelper.helpers.achievementdiaries.lumbridgeanddraynor;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
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
	quest = QuestHelperQuest.LUMBRIDGE_EASY
)
public class LumbridgeEasy extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement combatGear, lightSource, rope, runeEss, axe, tinderbox, smallFishingNet, pickaxe,
		waterAccessOrAbyss, spinyHelm, dough, oakLogs;

	// Items recommended
	ItemRequirement food;

	// Quests required
	Requirement runeMysteries, cooksAssistant;

	Requirement notDrayAgi, notKillCaveBug, notSedridor, notWaterRune, notHans, notPickpocket, notOak, notKillZombie,
		notFishAnchovies, notBread, notIron, notEnterHAM, choppedLogs;

	Requirement addedRopeToHole;

	QuestStep claimReward, drayAgi, killCaveBug, addRopeToHole, moveToDarkHole, sedridor, moveToSed, moveToWaterAltar,
		waterRune, hans, chopOak, burnOak, fishAnchovies, bread, mineIron;

	NpcStep pickpocket, killZombie;

	ObjectStep moveToDraySewer, enterHAM;

	Zone cave, sewer, water, mageTower, lumby;

	ZoneRequirement inCave, inSewer, inWater, inMageTower, inLumby;

	ConditionalStep drayAgiTask, killCaveBugTask, sedridorTask, waterRuneTask, hansTask, pickpocketTask, oakTask,
		killZombieTask, fishAnchoviesTask, breadTask, ironTask, enterHAMTask;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doEasy = new ConditionalStep(this, claimReward);

		drayAgiTask = new ConditionalStep(this, drayAgi);
		doEasy.addStep(notDrayAgi, drayAgiTask);

		killZombieTask = new ConditionalStep(this, moveToDraySewer);
		killZombieTask.addStep(inSewer, killZombie);
		doEasy.addStep(notKillZombie, killZombieTask);

		sedridorTask = new ConditionalStep(this, moveToSed);
		sedridorTask.addStep(inMageTower, sedridor);
		doEasy.addStep(notSedridor, sedridorTask);

		enterHAMTask = new ConditionalStep(this, enterHAM);
		doEasy.addStep(notEnterHAM, enterHAMTask);

		killCaveBugTask = new ConditionalStep(this, addRopeToHole);
		killCaveBugTask.addStep(addedRopeToHole, moveToDarkHole);
		killCaveBugTask.addStep(inCave, killCaveBug);
		doEasy.addStep(notKillCaveBug, killCaveBugTask);

		waterRuneTask = new ConditionalStep(this, moveToWaterAltar);
		waterRuneTask.addStep(inWater, waterRune);
		doEasy.addStep(notWaterRune, waterRuneTask);

		breadTask = new ConditionalStep(this, bread);
		doEasy.addStep(notBread, breadTask);

		hansTask = new ConditionalStep(this, hans);
		doEasy.addStep(notHans, hansTask);

		pickpocketTask = new ConditionalStep(this, pickpocket);
		doEasy.addStep(notPickpocket, pickpocketTask);

		oakTask = new ConditionalStep(this, chopOak);
		oakTask.addStep(new Conditions(oakLogs, choppedLogs), burnOak);
		doEasy.addStep(notOak, oakTask);

		ironTask = new ConditionalStep(this, mineIron);
		doEasy.addStep(notIron, ironTask);

		fishAnchoviesTask = new ConditionalStep(this, fishAnchovies);
		doEasy.addStep(notFishAnchovies, fishAnchoviesTask);

		return doEasy;
	}

	@Override
	public void setupRequirements()
	{
		notDrayAgi = new VarplayerRequirement(1194, false, 1);
		notKillCaveBug = new VarplayerRequirement(1194, false, 2);
		notSedridor = new VarplayerRequirement(1194, false, 3);
		notWaterRune = new VarplayerRequirement(1194, false, 4);
		notHans = new VarplayerRequirement(1194, false, 5);
		notPickpocket = new VarplayerRequirement(1194, false, 6);
		notOak = new VarplayerRequirement(1194, false, 7);
		notKillZombie = new VarplayerRequirement(1194, false, 8);
		notFishAnchovies = new VarplayerRequirement(1194, false, 9);
		notBread = new VarplayerRequirement(1194, false, 10);
		notIron = new VarplayerRequirement(1194, false, 11);
		notEnterHAM = new VarplayerRequirement(1194, false, 12);

		addedRopeToHole = new VarbitRequirement(279, 1);

		lightSource = new ItemRequirement("Light source", ItemCollections.LIGHT_SOURCES).showConditioned(notKillCaveBug).isNotConsumed();
		rope = new ItemRequirement("Rope", ItemID.ROPE).showConditioned(notKillCaveBug);
		spinyHelm = new ItemRequirement("Spiny helmet or slayer helmet (Recommended for low combat levels / Ironmen)",
			ItemCollections.WALL_BEAST).showConditioned(notKillCaveBug).isNotConsumed();
		waterAccessOrAbyss = new ItemRequirement("Access to water altar, or travel through abyss.",
			ItemCollections.WATER_ALTAR).showConditioned(notWaterRune).isNotConsumed();
		waterAccessOrAbyss.setTooltip("Water talisman or tiara");
		runeEss = new ItemRequirement("Essence", ItemCollections.ESSENCE_LOW).showConditioned(notWaterRune);
		dough = new ItemRequirement("Bread dough", ItemID.BREAD_DOUGH).showConditioned(notBread);
		oakLogs = new ItemRequirement("Oak logs", ItemID.OAK_LOGS).showConditioned(notOak);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).showConditioned(notOak).isNotConsumed();
		axe = new ItemRequirement("Any axe", ItemCollections.AXES).showConditioned(notOak).isNotConsumed();
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).showConditioned(notIron).isNotConsumed();
		smallFishingNet = new ItemRequirement("Small fishing net", ItemID.SMALL_FISHING_NET).showConditioned(notFishAnchovies).isNotConsumed();

		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);

		inCave = new ZoneRequirement(cave);
		inSewer = new ZoneRequirement(sewer);
		inMageTower = new ZoneRequirement(mageTower);
		inWater = new ZoneRequirement(water);
		inLumby = new ZoneRequirement(lumby);

		choppedLogs = new ChatMessageRequirement(
			"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
		);
		((ChatMessageRequirement) choppedLogs).setInvalidateRequirement(
			new ChatMessageRequirement(
				new Conditions(LogicType.NOR, inLumby),
				"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
			)
		);

		runeMysteries = new QuestRequirement(QuestHelperQuest.RUNE_MYSTERIES, QuestState.FINISHED);
		cooksAssistant = new QuestRequirement(QuestHelperQuest.COOKS_ASSISTANT, QuestState.FINISHED);
	}

	public void loadZones()
	{
		lumby = new Zone(new WorldPoint(3212, 3213, 0), new WorldPoint(3227, 3201, 0));
		cave = new Zone(new WorldPoint(3140, 9602, 0), new WorldPoint(3261, 9537, 0));
		sewer = new Zone(new WorldPoint(3077, 9699, 0), new WorldPoint(3132, 9641, 0));
		mageTower = new Zone(new WorldPoint(3095, 9578, 0), new WorldPoint(3122, 9554, 0));
		water = new Zone(new WorldPoint(2688, 4863, 0), new WorldPoint(2751, 4800, 0));
	}

	public void setupSteps()
	{
		drayAgi = new ObjectStep(this, 11404, new WorldPoint(3103, 3279, 0),
			"Complete a lap of the Draynor Rooftop Course.");

		moveToDraySewer = new ObjectStep(this, ObjectID.TRAPDOOR_6435, new WorldPoint(3118, 3244, 0),
			"Climb down into the Draynor Sewer.");
		moveToDraySewer.addAlternateObjects(ObjectID.TRAPDOOR_6434);
		killZombie = new NpcStep(this, NpcID.ZOMBIE_38, new WorldPoint(3123, 9648, 0),
			"Kill a zombie.", true);
		killZombie.addAlternateNpcs(NpcID.ZOMBIE_40, NpcID.ZOMBIE_57, NpcID.ZOMBIE_55, NpcID.ZOMBIE_56);

		moveToSed = new ObjectStep(this, ObjectID.LADDER_2147, new WorldPoint(3104, 3162, 0),
			"Climb down the ladder in the Wizards' Tower.");
		sedridor = new NpcStep(this, NpcID.ARCHMAGE_SEDRIDOR, new WorldPoint(3103, 9571, 0),
			"Teleport to the Rune essence mine via Sedridor.");
		sedridor.addDialogStep("Can you teleport me to the Rune Essence?");

		enterHAM = new ObjectStep(this, ObjectID.TRAPDOOR_5490, new WorldPoint(3166, 3252, 0),
			"Lock pick and enter the HAM hideout.");
		enterHAM.addAlternateObjects(ObjectID.TRAPDOOR_5491);

		moveToDarkHole = new ObjectStep(this, ObjectID.DARK_HOLE, new WorldPoint(3169, 3172, 0),
			"Enter the dark hole in the Lumbridge Swamp.", lightSource, combatGear);
		addRopeToHole = new ObjectStep(this, ObjectID.DARK_HOLE, new WorldPoint(3169, 3172, 0),
			"Use a rope on the dark hole in the Lumbridge Swamp, and then enter it.",
			lightSource, rope.highlighted(), combatGear);
		addRopeToHole.addSubSteps(moveToDarkHole);
		addRopeToHole.addIcon(ItemID.ROPE);
		killCaveBug = new NpcStep(this, NpcID.CAVE_BUG, new WorldPoint(3151, 9574, 0),
			"Kill a Cave Bug.", combatGear, lightSource);

		moveToWaterAltar = new ObjectStep(this, 34815, new WorldPoint(3185, 3165, 0),
			"Enter the water altar in Lumbridge Swamp.", waterAccessOrAbyss.highlighted(), runeEss);
		moveToWaterAltar.addIcon(ItemID.WATER_TALISMAN);
		waterRune = new ObjectStep(this, ObjectID.ALTAR_34762, new WorldPoint(2716, 4836, 0),
			"Craft water runes.", runeEss);

		bread = new ObjectStep(this, ObjectID.COOKING_RANGE, new WorldPoint(3212, 3216, 0),
			"Cook bread on the cooking range in Lumbridge Castle.", dough);

		hans = new NpcStep(this, NpcID.HANS, new WorldPoint(3215, 3219, 0),
			"Talk to Hans to learn your age.");
		hans.addDialogStep("Can you tell me how long I've been here?");

		pickpocket = new NpcStep(this, NpcID.MAN_3107, new WorldPoint(3215, 3219, 0),
			"Pickpocket a man or woman infront of Lumbridge Castle.", true);
		pickpocket.addAlternateNpcs(NpcID.MAN_3108, NpcID.WOMAN_3111, NpcID.MAN_3106);

		chopOak = new ObjectStep(this, ObjectID.OAK_TREE_10820, new WorldPoint(3219, 3206, 0),
			"Chop the oak tree in the Lumbridge Castle Courtyard.", axe);
		burnOak = new ItemStep(this, "Burn the oak logs you've chopped.", tinderbox.highlighted(),
			oakLogs.highlighted());

		mineIron = new ObjectStep(this, ObjectID.IRON_ROCKS, new WorldPoint(3303, 3284, 0),
			"Mine some iron ore at the Al-Kharid mine.", pickaxe);

		fishAnchovies = new NpcStep(this, NpcID.FISHING_SPOT_1528, new WorldPoint(3267, 3148, 0),
			"Fish for anchovies in Al-Kharid.", smallFishingNet);

		claimReward = new NpcStep(this, NpcID.HATIUS_COSAINTUS, new WorldPoint(3235, 3213, 0),
			"Talk to Hatius Cosaintus in Lumbridge to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(lightSource, runeEss, axe, tinderbox, smallFishingNet, pickaxe,
			waterAccessOrAbyss, dough, combatGear);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, rope, spinyHelm);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.AGILITY, 10));
		reqs.add(new SkillRequirement(Skill.FIREMAKING, 15));
		reqs.add(new SkillRequirement(Skill.FISHING, 15));
		reqs.add(new SkillRequirement(Skill.MINING, 15));
		reqs.add(new SkillRequirement(Skill.RUNECRAFT, 5));
		reqs.add(new SkillRequirement(Skill.SLAYER, 7));
		reqs.add(new SkillRequirement(Skill.WOODCUTTING, 15));

		reqs.add(runeMysteries);
		reqs.add(cooksAssistant);

		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Zombie (lvl 13) and cave bug (lvl 6)");
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Explorer's ring 1", ItemID.EXPLORERS_RING_1),
			new ItemReward("2,500 Exp. Lamp (Any skill over 30)", ItemID.ANTIQUE_LAMP)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("30 casts of Low Level Alchemy per day without runes (does not provide experience) from Explorer's ring"),
			new UnlockReward("50% run energy replenish twice a day from Explorer's ring")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails draynorRooftopsSteps = new PanelDetails("Draynor Rooftops", Collections.singletonList(drayAgi),
			new SkillRequirement(Skill.AGILITY, 10));
		draynorRooftopsSteps.setDisplayCondition(notDrayAgi);
		draynorRooftopsSteps.setLockingStep(drayAgiTask);
		allSteps.add(draynorRooftopsSteps);

		PanelDetails zombieSteps = new PanelDetails("Kill Zombie in Draynor Sewers", Arrays.asList(moveToDraySewer,
			killZombie), combatGear);
		zombieSteps.setDisplayCondition(notKillZombie);
		zombieSteps.setLockingStep(killZombieTask);
		allSteps.add(zombieSteps);

		PanelDetails sedridorSteps = new PanelDetails("Rune Essence Mine", Arrays.asList(moveToSed, sedridor),
			runeMysteries);
		sedridorSteps.setDisplayCondition(notSedridor);
		sedridorSteps.setLockingStep(sedridorTask);
		allSteps.add(sedridorSteps);

		PanelDetails enterTheHamHideoutSteps = new PanelDetails("Enter the HAM Hideout", Collections.singletonList(enterHAM));
		enterTheHamHideoutSteps.setDisplayCondition(notEnterHAM);
		enterTheHamHideoutSteps.setLockingStep(enterHAMTask);
		allSteps.add(enterTheHamHideoutSteps);

		PanelDetails killCaveBugSteps = new PanelDetails("Kill Cave Bug", Arrays.asList(addRopeToHole, killCaveBug),
			new SkillRequirement(Skill.SLAYER, 7), lightSource, rope, spinyHelm);
		killCaveBugSteps.setDisplayCondition(notKillCaveBug);
		killCaveBugSteps.setLockingStep(killCaveBugTask);
		allSteps.add(killCaveBugSteps);

		PanelDetails waterRunesSteps = new PanelDetails("Craft Water Runes", Arrays.asList(moveToWaterAltar, waterRune),
			new SkillRequirement(Skill.RUNECRAFT, 5), waterAccessOrAbyss, runeEss);
		waterRunesSteps.setDisplayCondition(notWaterRune);
		waterRunesSteps.setLockingStep(waterRuneTask);
		allSteps.add(waterRunesSteps);

		PanelDetails breadSteps = new PanelDetails("Cooking Bread", Collections.singletonList(bread), cooksAssistant,
			dough);
		breadSteps.setDisplayCondition(notBread);
		breadSteps.setLockingStep(breadTask);
		allSteps.add(breadSteps);

		PanelDetails hansSteps = new PanelDetails("Learn Age from Hans", Collections.singletonList(hans));
		hansSteps.setDisplayCondition(notHans);
		hansSteps.setLockingStep(hansTask);
		allSteps.add(hansSteps);

		PanelDetails pickpocketSteps = new PanelDetails("Pickpocket Man or Woman", Collections.singletonList(pickpocket));
		pickpocketSteps.setDisplayCondition(notPickpocket);
		pickpocketSteps.setLockingStep(pickpocketTask);
		allSteps.add(pickpocketSteps);

		PanelDetails oakSteps = new PanelDetails("Chop and Burn Oak Logs", Arrays.asList(chopOak, burnOak),
			new SkillRequirement(Skill.WOODCUTTING, 15), new SkillRequirement(Skill.FIREMAKING, 15), tinderbox, axe);
		oakSteps.setDisplayCondition(notOak);
		oakSteps.setLockingStep(oakTask);
		allSteps.add(oakSteps);

		PanelDetails mineIronSteps = new PanelDetails("Mine Iron in Al-Kharid", Collections.singletonList(mineIron),
			new SkillRequirement(Skill.MINING, 15), pickaxe);
		mineIronSteps.setDisplayCondition(notIron);
		mineIronSteps.setLockingStep(ironTask);
		allSteps.add(mineIronSteps);

		PanelDetails anchoviesSteps = new PanelDetails("Fish Anchovies in Al-Kharid",
			Collections.singletonList(fishAnchovies), new SkillRequirement(Skill.FISHING, 15), smallFishingNet);
		anchoviesSteps.setDisplayCondition(notFishAnchovies);
		anchoviesSteps.setLockingStep(fishAnchoviesTask);
		allSteps.add(anchoviesSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
