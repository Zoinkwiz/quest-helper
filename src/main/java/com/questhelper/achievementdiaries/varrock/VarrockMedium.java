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
import com.questhelper.QuestVarPlayer;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
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
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.VARROCK_MEDIUM
)
public class VarrockMedium extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement coins, limpRoot, redSpiderEgg, ringOfCharos, digsitePend, lawRune, airRune, fireRune, mahoLog, willowLog, log;

	// Items recommended
	ItemRequirement food;

	// Quests required
	Requirement gardenOfTranq, digSite, gertCat, treeGnomeVillage, soulsBane, enlightenedJourney; // 32 qp

	Requirement notApothStr, notChamps, notCatColour, notGESpirit, notStrongholdEmote, notTolna, notTPDigsite, notTPVarrock, notVannaka, notMaho20, notWhiteFruit, notBalloon, notVarrAgi;

	QuestStep claimReward;

	Zone cave;

	ZoneRequirement inCave;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doMedium = new ConditionalStep(this, claimReward);
		// doMedium.addStep(notUsedShortcut, useShortcut);

		return doMedium;
	}

	public void setupRequirements()
	{
		notApothStr = new VarplayerRequirement(1179, false, 15);
		notChamps = new VarplayerRequirement(1179, false, 16);
		notCatColour = new VarplayerRequirement(1179, false, 18);
		notGESpirit = new VarplayerRequirement(1179, false, 19);
		notStrongholdEmote = new VarplayerRequirement(1179, false, 20);
		notTolna = new VarplayerRequirement(1179, false, 21);
		notTPDigsite = new VarplayerRequirement(1179, false, 22);
		notTPVarrock = new VarplayerRequirement(1179, false, 23);
		notVannaka = new VarplayerRequirement(1179, false, 24);
		notMaho20 = new VarplayerRequirement(1179, false, 25);
		notWhiteFruit = new VarplayerRequirement(1179, false, 26);
		notBalloon = new VarplayerRequirement(1179, false, 27);
		notVarrAgi = new VarplayerRequirement(1179, false, 28);

		coins = new ItemRequirement("Coins", ItemID.COINS).showConditioned(new Conditions(LogicType.OR, notApothStr, notCatColour, notMaho20));
		limpRoot = new ItemRequirement("Limpwurt root", ItemID.LIMPWURT_ROOT).showConditioned(notApothStr);
		redSpiderEgg = new ItemRequirement("Red spiders' egg", ItemID.RED_SPIDERS_EGGS).showConditioned(notApothStr);
		ringOfCharos = new ItemRequirement("Ring of Charos (A)", ItemID.RING_OF_CHAROSA).showConditioned(notCatColour);
		digsitePend = new ItemRequirement("Digsite pendant", ItemCollections.getDigsitePendants()).showConditioned(notTPDigsite);
		lawRune = new ItemRequirement("Law rune", ItemID.LAW_RUNE).showConditioned(notTPVarrock);
		airRune = new ItemRequirement("Air rune", ItemID.AIR_RUNE).showConditioned(notTPVarrock);
		fireRune = new ItemRequirement("Fire rune", ItemID.FIRE_RUNE).showConditioned(notTPVarrock);
		mahoLog = new ItemRequirement("Mahogany logs", ItemID.MAHOGANY_LOGS, 20).showConditioned(notMaho20);
		willowLog = new ItemRequirement("Willow logs", ItemID.WILLOW_LOGS, 10).showConditioned(notBalloon);
		log = new ItemRequirement("Logs", ItemID.LOGS).showConditioned(notBalloon);

		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);

		inCave = new ZoneRequirement(cave);

		gardenOfTranq = new QuestRequirement(QuestHelperQuest.GARDEN_OF_TRANQUILLITY, QuestState.FINISHED);
		digSite = new QuestRequirement(QuestHelperQuest.THE_DIG_SITE, QuestState.FINISHED);
		gertCat = new QuestRequirement(QuestHelperQuest.GERTRUDES_CAT, QuestState.FINISHED);
		treeGnomeVillage = new QuestRequirement(QuestHelperQuest.TREE_GNOME_VILLAGE, QuestState.FINISHED);
		soulsBane = new QuestRequirement(QuestHelperQuest.A_SOULS_BANE, QuestState.FINISHED);
		enlightenedJourney = new QuestRequirement(QuestHelperQuest.ENLIGHTENED_JOURNEY, QuestState.FINISHED);
	}

	public void loadZones()
	{
		cave = new Zone(new WorldPoint(2821, 9545, 0), new WorldPoint(2879, 9663, 0));
	}

	public void setupSteps()
	{

		claimReward = new NpcStep(this, NpcID.TOBY, new WorldPoint(3225, 3415, 0),
			"Talk to Toby in Varrock to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(coins.quantity(30105), limpRoot, redSpiderEgg, ringOfCharos, digsitePend, lawRune.quantity(1), airRune.quantity(3), fireRune.quantity(1), mahoLog.quantity(20), willowLog.quantity(10), log);
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
		reqs.add(new CombatLevelRequirement(40));
		reqs.add(new SkillRequirement(Skill.AGILITY, 30));
		reqs.add(new SkillRequirement(Skill.CRAFTING, 36));
		reqs.add(new SkillRequirement(Skill.FARMING, 30));
		reqs.add(new SkillRequirement(Skill.FIREMAKING, 40));
		reqs.add(new SkillRequirement(Skill.HERBLORE, 10));
		reqs.add(new SkillRequirement(Skill.MAGIC, 25));
		reqs.add(new SkillRequirement(Skill.THIEVING, 25));

		reqs.add(gardenOfTranq);
		reqs.add(digSite);
		reqs.add(treeGnomeVillage);
		reqs.add(gertCat);
		reqs.add(soulsBane);
		reqs.add(enlightenedJourney);

		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("At least 1 Gorak (level 145)");
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
