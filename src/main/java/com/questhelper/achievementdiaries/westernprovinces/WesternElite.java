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
package com.questhelper.achievementdiaries.westernprovinces;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.ComplexRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
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
	quest = QuestHelperQuest.WESTERN_ELITE
)
public class WesternElite extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement combatGear, magicLongU, bowString, magicSapling, coconuts25, ogreBellows, ogreBow, ogreArrows,
		mouthProtection, rake, spade, voidTop, voidRobe, voidGloves, voidHelm;

	// Items recommended
	ItemRequirement food, tpCrystal;

	// Quests required
	Requirement regicide, mourningsEndPartI, bigChompy, undergroundPass, base42CombatSkills;

	Requirement notMagicLong, notKillThermy, notPrissyScilla, notAdvancedAgi, notFullVoid, notChompyHat,
		notPickpocketElf;

	QuestStep claimReward, magicLong, killThermy, prissyScilla, advancedAgi, fullVoid, chompyHat, pickpocketElf,
		moveToSmoke, moveToThermy, moveToTirannwn;

	Zone tirannwn, smokeDungeon, thermyArena;

	ZoneRequirement inTirannwn, inSmokeDungeon, inThermyArena;

	ConditionalStep magicLongTask, killThermyTask, prissyScillaTask, advancedAgiTask, fullVoidTask, chompyHatTask,
		pickpocketElfTask;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doElite = new ConditionalStep(this, claimReward);

		prissyScillaTask = new ConditionalStep(this, prissyScilla);
		doElite.addStep(notPrissyScilla, prissyScillaTask);

		advancedAgiTask = new ConditionalStep(this, advancedAgi);
		doElite.addStep(notAdvancedAgi, advancedAgiTask);

		magicLongTask = new ConditionalStep(this, moveToTirannwn);
		magicLongTask.addStep(inTirannwn, magicLong);
		doElite.addStep(notMagicLong, magicLongTask);

		killThermyTask = new ConditionalStep(this, moveToSmoke);
		killThermyTask.addStep(inSmokeDungeon, moveToThermy);
		killThermyTask.addStep(inThermyArena, killThermy);
		doElite.addStep(notKillThermy, killThermyTask);

		pickpocketElfTask = new ConditionalStep(this, pickpocketElf);
		doElite.addStep(notPickpocketElf, pickpocketElfTask);

		fullVoidTask = new ConditionalStep(this, fullVoid);
		doElite.addStep(notFullVoid, fullVoidTask);

		chompyHatTask = new ConditionalStep(this, chompyHat);
		doElite.addStep(notChompyHat, chompyHatTask);

		return doElite;
	}

	@Override
	public void setupRequirements()
	{
		notMagicLong = new VarplayerRequirement(1183, false, 6);
		notKillThermy = new VarplayerRequirement(1183, false, 7);
		notPrissyScilla = new VarplayerRequirement(1183, false, 8);
		notAdvancedAgi = new VarplayerRequirement(1183, false, 9);
		notFullVoid = new VarplayerRequirement(1183, false, 12);
		notChompyHat = new VarplayerRequirement(1183, false, 13);
		notPickpocketElf = new VarplayerRequirement(1183, false, 14);

		base42CombatSkills = new ComplexRequirement(LogicType.AND, "Base 42s in all combat skills and 22 prayer",
			new SkillRequirement(Skill.ATTACK, 42), new SkillRequirement(Skill.STRENGTH, 42),
			new SkillRequirement(Skill.DEFENCE, 42), new SkillRequirement(Skill.HITPOINTS, 42),
			new SkillRequirement(Skill.RANGED, 42), new SkillRequirement(Skill.MAGIC, 42),
			new SkillRequirement(Skill.PRAYER, 22));

		magicLongU = new ItemRequirement("Magic longbow (u)", ItemID.MAGIC_LONGBOW_U).showConditioned(notMagicLong);
		bowString = new ItemRequirement("Bow string", ItemID.BOW_STRING).showConditioned(notMagicLong);
		mouthProtection =
			new ItemRequirement("Mouth protection", ItemCollections.MOUTH_PROTECTION).showConditioned(notKillThermy);
		rake = new ItemRequirement("Rake", ItemID.RAKE).showConditioned(notPrissyScilla);
		magicSapling = new ItemRequirement("Magic sapling", ItemID.MAGIC_SAPLING).showConditioned(notPrissyScilla);
		coconuts25 = new ItemRequirement("Coconuts", ItemID.COCONUT, 25).showConditioned(notPrissyScilla);
		spade = new ItemRequirement("Spade", ItemID.SPADE).showConditioned(notPrissyScilla);
		voidHelm = new ItemRequirement("Any void knight helm", ItemCollections.VOID_HELM).showConditioned(notFullVoid);
		voidTop = new ItemRequirement("Void knight top", ItemCollections.VOID_TOP).showConditioned(notFullVoid);
		voidRobe = new ItemRequirement("Void knight robe", ItemCollections.VOID_ROBE).showConditioned(notFullVoid);
		voidGloves = new ItemRequirement("Void knight gloves", ItemID.VOID_KNIGHT_GLOVES).showConditioned(notFullVoid);
		ogreBellows = new ItemRequirement("Ogre bellows", ItemCollections.OGRE_BELLOWS).showConditioned(notChompyHat);
		ogreBow = new ItemRequirement("Ogre bow", ItemCollections.OGRE_BOW).showConditioned(notChompyHat);
		ogreArrows = new ItemRequirement("Ogre / brutal arrows", ItemCollections.OGRE_BRUTAL_ARROWS).showConditioned(notChompyHat);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		tpCrystal = new ItemRequirement("Teleport Crystal", ItemCollections.TELEPORT_CRYSTAL);

		inTirannwn = new ZoneRequirement(tirannwn);
		inSmokeDungeon = new ZoneRequirement(smokeDungeon);
		inThermyArena = new ZoneRequirement(thermyArena);

		bigChompy = new QuestRequirement(QuestHelperQuest.BIG_CHOMPY_BIRD_HUNTING, QuestState.FINISHED);
		regicide = new QuestRequirement(QuestHelperQuest.REGICIDE, QuestState.IN_PROGRESS);
		mourningsEndPartI = new QuestRequirement(QuestHelperQuest.MOURNINGS_END_PART_I, QuestState.IN_PROGRESS);
		undergroundPass = new QuestRequirement(QuestHelperQuest.UNDERGROUND_PASS, QuestState.FINISHED);
	}

	public void loadZones()
	{
		tirannwn = new Zone(new WorldPoint(2142, 3272, 0), new WorldPoint(2317, 3032, 0));
		smokeDungeon = new Zone(new WorldPoint(2378, 9473, 0), new WorldPoint(2432, 9408, 0));
		thermyArena = new Zone(new WorldPoint(2347, 9462, 0), new WorldPoint(2378, 9437, 0));
	}

	public void setupSteps()
	{
		moveToTirannwn = new DetailedQuestStep(this, new WorldPoint(2230, 3166, 0),
			"Enter Tirannwn.", magicLongU, bowString);
		magicLong = new DetailedQuestStep(this, "Fletch a magic longbow in Tirannwn.",
			magicLongU.highlighted(), bowString.highlighted());

		prissyScilla = new NpcStep(this, NpcID.PRISSY_SCILLA, new WorldPoint(2437, 3419, 0),
			"Have Prissy Scilla protect your magic tree.", magicSapling, spade, rake, coconuts25);

		moveToSmoke = new ObjectStep(this, ObjectID.SMOKY_CAVE, new WorldPoint(2412, 3061, 0),
			"Enter the Smoke Devil Dungeon.", combatGear, food, mouthProtection);
		moveToThermy = new ObjectStep(this, ObjectID.CREVICE, new WorldPoint(2378, 9452, 0),
			"Enter the boss area of the Thermonuclear smoke devil.", combatGear, food, mouthProtection);
		killThermy = new NpcStep(this, NpcID.THERMONUCLEAR_SMOKE_DEVIL, new WorldPoint(2356, 9456, 0),
			"Defeat the Thermonuclear smoke devil. You are allowed one kill off-task for the diary.", combatGear, food,
			mouthProtection);

		advancedAgi = new ObjectStep(this, ObjectID.ROCKS_16515, new WorldPoint(2337, 3253, 0),
			"Use the advanced elven overpass cliffside shortcut.");

		fullVoid = new DetailedQuestStep(this, "Equip any complete void set.", voidHelm.equipped(), voidTop.equipped(),
			voidRobe.equipped(), voidGloves.equipped());

		pickpocketElf = new DetailedQuestStep(this, new WorldPoint(2333, 3171, 0), "Pickpocket an elf.");

		chompyHat = new NpcStep(this, NpcID.RANTZ, new WorldPoint(2628, 2979, 0),
			"Claim any Chompy bird hat from Rantz. Kill chompy birds until you have 1000 kills. \n \nYou can check " +
				"your kill count by right clicking selecting 'Check Kills' on an ogre bow.",
			ogreBow, ogreArrows, ogreBellows);
		chompyHat.addDialogStep("Can I have a hat please?");

		claimReward = new NpcStep(this, NpcID.ELDER_GNOME_CHILD, new WorldPoint(2466, 3460, 0),
			"Talk to the Elder gnome child in Gnome Stronghold to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear, magicLongU, bowString, magicSapling, coconuts25, ogreBellows, ogreBow,
			ogreArrows, mouthProtection, rake, spade, voidHelm, voidTop, voidRobe, voidGloves);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, tpCrystal);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new CombatLevelRequirement(40));
		reqs.add(new SkillRequirement(Skill.AGILITY, 85, true));
		reqs.add(new SkillRequirement(Skill.FARMING, 75, true));
		reqs.add(new SkillRequirement(Skill.FLETCHING, 85, true));
		reqs.add(new SkillRequirement(Skill.SLAYER, 93, false));
		reqs.add(new SkillRequirement(Skill.THIEVING, 85, true));
		reqs.add(base42CombatSkills);

		reqs.add(bigChompy);
		reqs.add(mourningsEndPartI);
		reqs.add(regicide);

		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Thermonuclear smoke devil (lvl 301)");
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Western banner 4", ItemID.WESTERN_BANNER_4),
			new ItemReward("50,000 Exp. Lamp (Any skill over 70)", ItemID.ANTIQUE_LAMP)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Unlimited teleports to the Piscatoris Fishing Colony from Western banner 4"),
			new UnlockReward("2 chompy birds will always appear when Chompy bird hunting"),
			new UnlockReward("Chance of receiving a chompy chick pet when chompy bird hunting"),
			new UnlockReward("Slayer point rewards from tasks assigned by Nieve/Steve are increased to match those of Duradel"),
			new UnlockReward("150 free ogre arrows every day from Rantz"),
			new UnlockReward("One free resurrection per day when fighting Zulrah")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails prissySteps = new PanelDetails("Prissy Scilla's Protection",
			Collections.singletonList(prissyScilla), new SkillRequirement(Skill.FARMING, 75), magicSapling,
			coconuts25, rake, spade);
		prissySteps.setDisplayCondition(notPrissyScilla);
		prissySteps.setLockingStep(prissyScillaTask);
		allSteps.add(prissySteps);

		PanelDetails agiSteps = new PanelDetails("Advanced Elven Shortcut", Collections.singletonList(advancedAgi),
			undergroundPass, new SkillRequirement(Skill.AGILITY, 85));
		agiSteps.setDisplayCondition(notAdvancedAgi);
		agiSteps.setLockingStep(advancedAgiTask);
		allSteps.add(agiSteps);

		PanelDetails magicSteps = new PanelDetails("Magic Longbow In Tirannwn", Arrays.asList(moveToTirannwn,
			magicLong), new SkillRequirement(Skill.FLETCHING, 85), regicide, magicLongU, bowString);
		magicSteps.setDisplayCondition(notMagicLong);
		magicSteps.setLockingStep(magicLongTask);
		allSteps.add(magicSteps);

		PanelDetails thermySteps = new PanelDetails("Thermonuclear Smoke Devil", Arrays.asList(moveToSmoke,
			moveToThermy, killThermy), new SkillRequirement(Skill.SLAYER, 93, false), combatGear,
			food, mouthProtection);
		thermySteps.setDisplayCondition(notKillThermy);
		thermySteps.setLockingStep(killThermyTask);
		allSteps.add(thermySteps);

		PanelDetails elfSteps = new PanelDetails("Pickpocket An Elf", Collections.singletonList(pickpocketElf),
			new SkillRequirement(Skill.THIEVING, 85), mourningsEndPartI);
		elfSteps.setDisplayCondition(notPickpocketElf);
		elfSteps.setLockingStep(pickpocketElfTask);
		allSteps.add(elfSteps);

		PanelDetails voidSteps = new PanelDetails("Equip Full Void Set", Collections.singletonList(fullVoid),
			base42CombatSkills, voidHelm, voidTop, voidRobe, voidGloves);
		voidSteps.setDisplayCondition(notFullVoid);
		voidSteps.setLockingStep(fullVoidTask);
		allSteps.add(voidSteps);

		PanelDetails hatSteps = new PanelDetails("Chompy Bird Hat", Collections.singletonList(chompyHat),
			new SkillRequirement(Skill.RANGED, 30), bigChompy, ogreBellows, ogreBow, ogreArrows);
		hatSteps.setDisplayCondition(notChompyHat);
		hatSteps.setLockingStep(chompyHatTask);
		allSteps.add(hatSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
