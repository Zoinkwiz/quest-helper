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
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.TileStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.Item;
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
	quest = QuestHelperQuest.VARROCK_ELITE
)
public class VarrockElite extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement sAtk4, sStr4, sDef4, torstol, natRune, astRune, earthRune, coins, mahoLog, cookingGuild, rawPie,
		runeBar, feather, hammer, ess, earthTali, runeDartTip;

	// Quests required
	Requirement dreamMentor, runeMyster, touristTrap;

	Requirement notSuperCombat, notPlankMake, notSummerPie, notRuneDart, not100Earth;

	QuestStep claimReward, moveToBank, superCombat, moveToLumb, plankMake, moveToCookingGuild, summerPie,
		moveToEarthRune, earthRune100, dartTip, runeDart;

	Zone bank, lumb, cGuild, earth;

	ZoneRequirement inBank, inLumb, inCookingGuild, inEarth;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doElite = new ConditionalStep(this, claimReward);
		doElite.addStep(new Conditions(notSuperCombat, inBank), moveToBank);
		doElite.addStep(notSuperCombat, moveToBank);
		doElite.addStep(new Conditions(notSummerPie, inCookingGuild), summerPie);
		doElite.addStep(notSummerPie, moveToCookingGuild);
		doElite.addStep(new Conditions(notRuneDart, runeDartTip), runeDart);
		doElite.addStep(notRuneDart, dartTip);
		doElite.addStep(new Conditions(notPlankMake, inLumb), plankMake);
		doElite.addStep(notPlankMake, moveToLumb);
		doElite.addStep(new Conditions(not100Earth, inEarth), earthRune100);
		doElite.addStep(not100Earth, moveToEarthRune);

		return doElite;
	}

	public void setupRequirements()
	{
		notSuperCombat = new VarplayerRequirement(1177, false, 7);
		notPlankMake = new VarplayerRequirement(1177, false, 8);
		notSummerPie = new VarplayerRequirement(1177, false, 9);
		notRuneDart = new VarplayerRequirement(1177, false, 10);
		not100Earth = new VarplayerRequirement(1177, false, 11);

		sAtk4 = new ItemRequirement("Super attack(4)", ItemID.SUPER_ATTACK4).showConditioned(notSuperCombat);
		sStr4 = new ItemRequirement("Super strength(4)", ItemID.SUPER_STRENGTH4).showConditioned(notSuperCombat);
		sDef4 = new ItemRequirement("Super defense(4)", ItemID.SUPER_DEFENCE4).showConditioned(notSuperCombat);
		torstol = new ItemRequirement("Torstol", ItemID.TORSTOL).showConditioned(notSuperCombat);
		natRune = new ItemRequirement("Nature rune", ItemID.NATURE_RUNE).showConditioned(notPlankMake);
		astRune = new ItemRequirement("Astral rune", ItemID.ASTRAL_RUNE).showConditioned(notPlankMake);
		earthRune = new ItemRequirement("Earth rune", ItemID.EARTH_RUNE).showConditioned(notPlankMake);
		coins = new ItemRequirement("Coins", ItemCollections.getCoins()).showConditioned(notPlankMake);
		mahoLog = new ItemRequirement("Mahogany logs", ItemID.MAHOGANY_LOGS).showConditioned(notPlankMake);
		cookingGuild = new ItemRequirement("Access to cooking guild", ItemCollections.getCookingGuild()).showConditioned(notSummerPie);
		cookingGuild.setTooltip("A chef's hat, Varrock Armour 3, or Cooking Cape");
		rawPie = new ItemRequirement("Raw summer pie", ItemID.RAW_SUMMER_PIE).showConditioned(notSummerPie);
		runeBar = new ItemRequirement("Rune bar", ItemID.RUNITE_BAR).showConditioned(notRuneDart);
		feather = new ItemRequirement("Feather", ItemID.FEATHER).showConditioned(notRuneDart);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notRuneDart);
		runeDartTip = new ItemRequirement("Rune dart tip", ItemID.RUNE_DART_TIP);
		ess = new ItemRequirement("Essence", ItemCollections.getEssenceLow()).showConditioned(not100Earth);
		earthTali =
			new ItemRequirement("Access to Earth altar, or travel through abyss", ItemCollections.getEarthAltar()).showConditioned(not100Earth);

		inBank = new ZoneRequirement(bank);
		inLumb = new ZoneRequirement(lumb);
		inCookingGuild = new ZoneRequirement(cGuild);

		dreamMentor = new QuestRequirement(QuestHelperQuest.DREAM_MENTOR, QuestState.FINISHED);
		runeMyster = new QuestRequirement(QuestHelperQuest.RUNE_MYSTERIES, QuestState.FINISHED);
		touristTrap = new QuestRequirement(QuestHelperQuest.THE_TOURIST_TRAP, QuestState.FINISHED);
	}

	public void loadZones()
	{
		bank = new Zone(new WorldPoint(3179, 3448, 0), new WorldPoint(3191, 3432, 0));
		lumb = new Zone(new WorldPoint(3300, 3501, 0), new WorldPoint(3313, 3493, 0));
		cGuild = new Zone(new WorldPoint(3138, 3453, 0), new WorldPoint(3148, 3444, 0));
		earth = new Zone(new WorldPoint(2624, 4863, 0), new WorldPoint(2687, 4800, 0));
	}

	public void setupSteps()
	{
		moveToBank = new TileStep(this, new WorldPoint(3183, 3440, 0),
			"Move to the west Varrock bank.");
		superCombat = new ItemStep(this, "Create a super combat potion.", sAtk4.highlighted(), sStr4.highlighted(),
			sDef4.highlighted(), torstol.highlighted());
		moveToLumb = new ObjectStep(this, 2618, new WorldPoint(3308, 3492, 0),
			"Climb the fence to enter the lumber yard.");
		plankMake = new DetailedQuestStep(this, "Cast plank make until you've made 20 mahogany planks.",
			natRune.quantity(20), astRune.quantity(40), earthRune.quantity(300), coins.quantity(21000), mahoLog.quantity(20));
		moveToCookingGuild = new ObjectStep(this, ObjectID.DOOR_24958, new WorldPoint(3143, 3443, 0),
			"Enter the cooking guild.", cookingGuild.equipped());
		summerPie = new ObjectStep(this, ObjectID.RANGE_7183, new WorldPoint(3146, 3453, 0),
			"Cook the summer pie.", rawPie);
		moveToEarthRune = new ObjectStep(this, 34816, new WorldPoint(3306, 3474, 0),
			"Travel to the earth altar or go through the abyss.", earthTali);
		earthRune100 = new ObjectStep(this, 34763, new WorldPoint(2658, 4841, 0),
			"Craft the earth runes.", ess.quantity(25));
		dartTip = new ObjectStep(this, ObjectID.ANVIL_2097, new WorldPoint(3188, 3426, 0),
			"Make rune dart tips on the anvil in east Varrock.", runeBar);
		runeDart = new ItemStep(this, "Use feathers on the rune dart tips.", runeDartTip.highlighted(),
			feather.quantity(10).highlighted());

		claimReward = new NpcStep(this, NpcID.TOBY, new WorldPoint(3225, 3415, 0),
			"Talk to Toby in Varrock to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(sAtk4, sStr4, sDef4, torstol, natRune.quantity(20), astRune.quantity(40),
			earthRune.quantity(300), coins.quantity(21000), mahoLog.quantity(20), cookingGuild, rawPie, runeBar,
			feather, hammer, ess.quantity(25), earthTali);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.COOKING, 95));
		reqs.add(new SkillRequirement(Skill.FLETCHING, 81));
		reqs.add(new SkillRequirement(Skill.HERBLORE, 90));
		reqs.add(new SkillRequirement(Skill.MAGIC, 86));
		reqs.add(new SkillRequirement(Skill.RUNECRAFT, 78));
		reqs.add(new SkillRequirement(Skill.SMITHING, 89));

		reqs.add(dreamMentor);
		reqs.add(touristTrap);
		reqs.add(runeMyster);

		return reqs;
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Make Super Combat", Arrays.asList(moveToBank, superCombat), sAtk4, sStr4, sDef4, torstol));
		allSteps.add(new PanelDetails("Summer Pie", Arrays.asList(moveToCookingGuild, summerPie), cookingGuild, rawPie));
		allSteps.add(new PanelDetails("Smith and Fletch 10 Rune Darts", Arrays.asList(dartTip, runeDart), runeBar, feather, hammer));
		allSteps.add(new PanelDetails("Plank Make", Arrays.asList(moveToLumb, plankMake), natRune.quantity(20), astRune.quantity(40), earthRune.quantity(300), coins.quantity(21000), mahoLog.quantity(20)));
		allSteps.add(new PanelDetails("Craft 100 Earth runes", Arrays.asList(moveToEarthRune, earthRune100), ess.quantity(25), earthTali));

		PanelDetails superCombatSteps = new PanelDetails("Make Super Combat", Arrays.asList(moveToBank, superCombat),
			new SkillRequirement(Skill.HERBLORE, 90), sAtk4, sStr4, sDef4, torstol);
		superCombatSteps.setDisplayCondition(notSuperCombat);
		allSteps.add(superCombatSteps);

		PanelDetails summerPieSteps = new PanelDetails("Summer Pie", Arrays.asList(moveToCookingGuild, summerPie),
			new SkillRequirement(Skill.COOKING, 95), cookingGuild, rawPie);
		summerPieSteps.setDisplayCondition(notSummerPie);
		allSteps.add(summerPieSteps);

		PanelDetails runeDartsSteps = new PanelDetails("Smith and Fletch 10 Rune Darts", Arrays.asList(dartTip,
			runeDart), new SkillRequirement(Skill.FLETCHING, 81), new SkillRequirement(Skill.SMITHING, 89),
			touristTrap, runeBar, feather.quantity(10), hammer);
		runeDartsSteps.setDisplayCondition(notRuneDart);
		allSteps.add(runeDartsSteps);

		PanelDetails plankMakeSteps = new PanelDetails("Plank Make", Arrays.asList(moveToLumb, plankMake),
			new SkillRequirement(Skill.MAGIC, 86), dreamMentor, natRune.quantity(20), astRune.quantity(40),
			earthRune.quantity(300), coins.quantity(21000), mahoLog.quantity(20));
		plankMakeSteps.setDisplayCondition(notPlankMake);
		allSteps.add(plankMakeSteps);

		PanelDetails earth100Steps = new PanelDetails("Craft 100 Earth runes", Arrays.asList(moveToEarthRune,
			earthRune100), new SkillRequirement(Skill.RUNECRAFT, 78), ess.quantity(25), earthTali);
		earth100Steps.setDisplayCondition(not100Earth);
		allSteps.add(earth100Steps);

		PanelDetails finishOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishOffSteps);

		return allSteps;
	}
}
