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
package com.questhelper.achievementdiaries.desert;

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
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.util.Spellbook;
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
	quest = QuestHelperQuest.DESERT_ELITE
)
public class DesertElite extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement combatGear, rawPie, waterRune, bloodRune, deathRune, dragonDartTip, feather, KQHead, mahoPlank,
		goldLeaves, coins, saw, hammer, kqHead;

	// Items recommended
	ItemRequirement food, pharaohSceptre, desertRobe, waterskin, desertBoots, desertShirt;

	// Quests required
	Requirement desertTreasure, icthlarinsLittleHelper, touristTrap;

	Requirement notWildPie, notIceBarrage, notDragonDarts, notTalkKQHead, notGrandGoldChest, notRestorePrayer, ancientBook;

	QuestStep claimReward, wildPie, iceBarrage, dragonDarts, talkKQHead, grandGoldChest, restorePrayer,
		moveToPyramidPlunder, startPyramidPlunder, moveToBed;

	Zone bed, pyramidPlunderLobby, lastRoom;

	ZoneRequirement inBed, inPyramidPlunderLobby, inLastRoom;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doElite = new ConditionalStep(this, claimReward);
		doElite.addStep(notWildPie, wildPie);
		doElite.addStep(notIceBarrage, iceBarrage);
		doElite.addStep(new Conditions(notGrandGoldChest, inLastRoom), grandGoldChest);
		doElite.addStep(new Conditions(notGrandGoldChest, inPyramidPlunderLobby), startPyramidPlunder);
		doElite.addStep(notGrandGoldChest, moveToPyramidPlunder);
		doElite.addStep(new Conditions(notDragonDarts, inBed), dragonDarts);
		doElite.addStep(notDragonDarts, moveToBed);
		doElite.addStep(notTalkKQHead, talkKQHead);

		return doElite;
	}

	public void setupRequirements()
	{
		notWildPie = new VarplayerRequirement(1199, false, 2);
		notIceBarrage = new VarplayerRequirement(1199, false, 3);
		notDragonDarts = new VarplayerRequirement(1199, false, 4);
		notTalkKQHead = new VarplayerRequirement(1199, false, 5);
		notGrandGoldChest = new VarplayerRequirement(1199, false, 6);
		notRestorePrayer = new VarplayerRequirement(1199, false, 7);

		ancientBook = new SpellbookRequirement(Spellbook.ANCIENT);

		coins = new ItemRequirement("Coins", ItemCollections.getCoins()).showConditioned(notTalkKQHead);
		rawPie = new ItemRequirement("Raw wild pie", ItemID.RAW_WILD_PIE).showConditioned(notWildPie);
		waterRune = new ItemRequirement("Water rune", ItemID.WATER_RUNE).showConditioned(notIceBarrage);
		bloodRune = new ItemRequirement("Blood rune", ItemID.BLOOD_RUNE).showConditioned(notIceBarrage);
		deathRune = new ItemRequirement("Death rune", ItemID.DEATH_RUNE).showConditioned(notIceBarrage);
		dragonDartTip = new ItemRequirement("Dragon dart tip", ItemID.DRAGON_DART_TIP).showConditioned(notDragonDarts);
		feather = new ItemRequirement("Feather", ItemID.FEATHER).showConditioned(notDragonDarts);
		mahoPlank = new ItemRequirement("Mahogany plank", ItemID.MAHOGANY_PLANK).showConditioned(notTalkKQHead);
		goldLeaves = new ItemRequirement("Gold leaf", ItemID.GOLD_LEAF).showConditioned(notTalkKQHead);
		saw = new ItemRequirement("Saw", ItemID.SAW).showConditioned(notTalkKQHead);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notTalkKQHead);
		kqHead = new ItemRequirement("Stuffed KQ head", ItemID.STUFFED_KQ_HEAD).showConditioned(notTalkKQHead);

		pharaohSceptre = new ItemRequirement("Pharaoh's sceptre", ItemCollections.getPharoahSceptre());
		desertBoots = new ItemRequirement("Desert boots", ItemID.DESERT_BOOTS);
		desertRobe = new ItemRequirement("Desert robe", ItemID.DESERT_ROBE);
		desertShirt = new ItemRequirement("Desert shirt", ItemID.DESERT_SHIRT);
		waterskin = new ItemRequirement("Waterskin", ItemCollections.getWaterskin());

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);

		inBed = new ZoneRequirement(bed);
		inLastRoom = new ZoneRequirement(lastRoom);
		inPyramidPlunderLobby = new ZoneRequirement(pyramidPlunderLobby);

		desertTreasure = new QuestRequirement(QuestHelperQuest.DESERT_TREASURE, QuestState.FINISHED);
		icthlarinsLittleHelper = new QuestRequirement(QuestHelperQuest.ICTHLARINS_LITTLE_HELPER, QuestState.FINISHED);
		touristTrap = new QuestRequirement(QuestHelperQuest.THE_TOURIST_TRAP, QuestState.FINISHED);
	}

	public void loadZones()
	{
		bed = new Zone(new WorldPoint(2821, 9545, 0), new WorldPoint(2879, 9663, 0));
		pyramidPlunderLobby = new Zone(new WorldPoint(1926, 4465, 2), new WorldPoint(1976, 4419, 3));
		lastRoom = new Zone(new WorldPoint(1920, 4478, 0), new WorldPoint(1934, 4462, 0));
	}

	public void setupSteps()
	{
		moveToPyramidPlunder = new ObjectStep(this, 26622, new WorldPoint(3289, 2800, 0),
			"Enter the Pyramid plunder mini-game. If you don't see a Guardian mummy exit and try a different entrance.");
		startPyramidPlunder = new NpcStep(this, NpcID.GUARDIAN_MUMMY, new WorldPoint(1934, 4427, 3),
			"Talk to the guardian mummy to start the minigame. If you don't see a Guardian mummy exit and try a different entrance.");
		startPyramidPlunder.addDialogStep("I know what I'm doing - let's get on with it.");

		claimReward = new NpcStep(this, NpcID.JARR, new WorldPoint(3303, 3124, 0),
			"Talk to Jarr at the Shantay pass to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(rawPie, waterRune.quantity(6), bloodRune.quantity(2), deathRune.quantity(4),
			dragonDartTip, feather, KQHead, mahoPlank.quantity(2), goldLeaves.quantity(2), coins.quantity(50000), saw,
			hammer, kqHead);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, pharaohSceptre, desertRobe, desertBoots, desertShirt, waterskin);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.CONSTRUCTION, 78));
		reqs.add(new SkillRequirement(Skill.COOKING, 85));
		reqs.add(new SkillRequirement(Skill.FLETCHING, 95));
		reqs.add(new SkillRequirement(Skill.CONSTRUCTION, 78));
		reqs.add(new SkillRequirement(Skill.MAGIC, 94));
		reqs.add(new SkillRequirement(Skill.PRAYER, 85));
		reqs.add(new SkillRequirement(Skill.THIEVING, 91));


		reqs.add(desertTreasure);
		reqs.add(icthlarinsLittleHelper);

		return reqs;
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails wildPieSteps = new PanelDetails("Bake Wild Pie", Collections.singletonList(wildPie),
			new SkillRequirement(Skill.COOKING, 85), rawPie);
		wildPieSteps.setDisplayCondition(notWildPie);
		allSteps.add(wildPieSteps);

		PanelDetails iceBarrageSteps = new PanelDetails("Ice Barrage", Collections.singletonList(iceBarrage),
			new SkillRequirement(Skill.MAGIC, 94), desertTreasure, ancientBook, waterRune.quantity(6),
			bloodRune.quantity(2), deathRune.quantity(4));
		iceBarrageSteps.setDisplayCondition(notIceBarrage);
		allSteps.add(iceBarrageSteps);

		PanelDetails grandGoldChestSteps = new PanelDetails("Grand Gold Chest", Arrays.asList(moveToPyramidPlunder,
			startPyramidPlunder, grandGoldChest), new SkillRequirement(Skill.THIEVING, 91), icthlarinsLittleHelper);
		grandGoldChestSteps.setDisplayCondition(notGrandGoldChest);
		allSteps.add(grandGoldChestSteps);

		PanelDetails restorePrayerSteps = new PanelDetails("Restore 85 Prayer",
			Collections.singletonList(restorePrayer), new SkillRequirement(Skill.PRAYER, 85), icthlarinsLittleHelper);
		restorePrayerSteps.setDisplayCondition(notRestorePrayer);
		allSteps.add(restorePrayerSteps);

		PanelDetails dragonDartsSteps = new PanelDetails("Dragon Darts", Collections.singletonList(dragonDarts),
			new SkillRequirement(Skill.FLETCHING, 95), touristTrap, dragonDartTip, feather);
		dragonDartsSteps.setDisplayCondition(notDragonDarts);
		allSteps.add(dragonDartsSteps);

		PanelDetails kqHeadSteps = new PanelDetails("Kalphite Queen Head", Collections.singletonList(talkKQHead),
			new SkillRequirement(Skill.CONSTRUCTION, 78), kqHead, coins.quantity(50000), mahoPlank.quantity(2),
			goldLeaves.quantity(2), saw, hammer);
		kqHeadSteps.setDisplayCondition(notTalkKQHead);
		allSteps.add(kqHeadSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
