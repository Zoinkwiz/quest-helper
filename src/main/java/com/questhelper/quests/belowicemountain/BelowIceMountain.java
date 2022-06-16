/*
 * Copyright (c) 2021, LlemonDuck <https://github.com/LlemonDuck>
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
package com.questhelper.quests.belowicemountain;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.quest.QuestPointRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import com.questhelper.steps.emote.QuestEmote;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.coords.WorldPoint;

import java.util.*;
import java.util.List;

@QuestDescriptor(
	quest = QuestHelperQuest.BELOW_ICE_MOUNTAIN
)
public class BelowIceMountain extends BasicQuestHelper
{
	// varbit 12065 tracks checkal line?
	// varbit 12062 -> 1 after learning flex
	private static final int VARBIT_CHECKAL_LINE = 12065;
	private static final int VARBIT_MARLEY_LINE = 12064;
	private static final int VARBIT_BURNTOF_LINE = 12066;

	//Items Required
	ItemRequirement cookedMeat, bread, knife, coins, knifeHighlight, breadHighlight, steakSandwich,
		beerHighlight;

	ItemRequirement iceMountainTeleport, faladorTeleport, varrockTeleport, combatGearOrPickaxe;

	Requirement needFlex, leftFlexBeforeLearning, haveFlex, recruitedCheckal, needRecipe, haveRecipe, haveIngredients,
		fedMarley, recruitedMarley, needBeer,gaveBeer, needRPS, recruitedBurntof, inDungeon;

	QuestStep talkToWillowToStart, recruitCheckal, talkToAtlas, flexCheckal, talkToMarley, talkToCook, getIngredients,
		makeSandwich, feedMarley, talkToMarleyAfterFeeding, talkToBurntof, buyBeer, giveBeer, playRPS, goToDungeon,
		reenterDungeon, defeatGuardian, watchCutscene;

	ConditionalStep getCheckal, getMarley, getBurntof;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToWillowToStart);
		steps.put(5, talkToWillowToStart);
		steps.put(7, talkToWillowToStart);

		getCheckal = new ConditionalStep(this, recruitCheckal);
		getCheckal.addStep(needFlex, talkToAtlas);
		getCheckal.addStep(leftFlexBeforeLearning, talkToAtlas);
		getCheckal.addStep(haveFlex, flexCheckal);
		getCheckal.setLockingCondition(recruitedCheckal);
		steps.put(10, getCheckal);

		getMarley = new ConditionalStep(this, talkToMarley);
		getMarley.addStep(fedMarley, talkToMarleyAfterFeeding);
		getMarley.addStep(needRecipe, talkToCook);
		getMarley.addStep(steakSandwich, feedMarley);
		getMarley.addStep(new Conditions(LogicType.AND, haveRecipe, haveIngredients), makeSandwich);
		getMarley.addStep(haveRecipe, getIngredients);
		getMarley.setLockingCondition(recruitedMarley);

		getBurntof = new ConditionalStep(this, talkToBurntof);
		getBurntof.addStep(needRPS, playRPS);
		getBurntof.addStep(gaveBeer, playRPS);
		getBurntof.addStep(new Conditions(LogicType.AND, needBeer, beerHighlight), giveBeer);
		getBurntof.addStep(needBeer, buyBeer);
		getBurntof.setLockingCondition(recruitedBurntof);

		ConditionalStep marleyAndBurntof = new ConditionalStep(this, getMarley);
		marleyAndBurntof.addStep(recruitedMarley, getBurntof);
		steps.put(15, marleyAndBurntof);

		steps.put(20, goToDungeon);
		steps.put(25, goToDungeon);
		steps.put(30, reenterDungeon);

		ConditionalStep guardian = new ConditionalStep(this, reenterDungeon);
		guardian.addStep(inDungeon, defeatGuardian);
		steps.put(35, guardian);

		steps.put(40, watchCutscene);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		cookedMeat = new ItemRequirement("Cooked Meat", ItemID.COOKED_MEAT, 1);
		bread = new ItemRequirement("Bread", ItemID.BREAD, 1);
		knife = new ItemRequirement("Knife", ItemID.KNIFE, 1);
		coins = new ItemRequirement("Coins", ItemCollections.COINS, 3);

		knifeHighlight = new ItemRequirement(true, "Knife", ItemID.KNIFE);
		breadHighlight = new ItemRequirement(true, "Bread", ItemID.BREAD);

		steakSandwich = new ItemRequirement("Steak Sandwich", ItemID.STEAK_SANDWICH);

		beerHighlight = new ItemRequirement(true, "Asgarnian Ale", ItemID.ASGARNIAN_ALE);

		iceMountainTeleport = new ItemRequirement("A teleport to near Ice Mountain", ItemCollections.AMULET_OF_GLORIES);
		iceMountainTeleport.addAlternates(ItemCollections.COMBAT_BRACELETS);
		iceMountainTeleport.addAlternates(ItemID.FALADOR_TELEPORT, ItemID.LASSAR_TELEPORT);
		faladorTeleport = new ItemRequirement("Falador teleport", ItemID.FALADOR_TELEPORT);
		varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.VARROCK_TELEPORT);
		combatGearOrPickaxe = new ItemRequirement("Combat gear or a pickaxe if you don't want to fight", -1, -1);
		combatGearOrPickaxe.setDisplayItemId(BankSlotIcons.getCombatGear());
	}

	public void setupConditions()
	{
		needFlex = new VarbitRequirement(VARBIT_CHECKAL_LINE, 5);
		leftFlexBeforeLearning = new VarbitRequirement(VARBIT_CHECKAL_LINE, 10);
		haveFlex = new VarbitRequirement(VARBIT_CHECKAL_LINE, 15);
		recruitedCheckal = new VarbitRequirement(VARBIT_CHECKAL_LINE, 40);

		needRecipe = new VarbitRequirement(VARBIT_MARLEY_LINE, 5);
		haveRecipe = new VarbitRequirement(VARBIT_MARLEY_LINE, 10);
		haveIngredients = new ItemRequirements(cookedMeat, bread, knife);
		fedMarley = new VarbitRequirement(VARBIT_MARLEY_LINE, 35);
		recruitedMarley = new VarbitRequirement(VARBIT_MARLEY_LINE, 40);

		needBeer = new VarbitRequirement(VARBIT_BURNTOF_LINE, 5);
		gaveBeer = new VarbitRequirement(VARBIT_BURNTOF_LINE, 10);
		needRPS = new VarbitRequirement(VARBIT_BURNTOF_LINE, 15);
		recruitedBurntof = new VarbitRequirement(VARBIT_BURNTOF_LINE, 40);

		inDungeon = new NpcRequirement("Ancient Guardian", 10654);
	}

	public void setupSteps()
	{
		talkToWillowToStart = new NpcStep(this, NpcID.WILLOW, new WorldPoint(3003, 3435, 0),
			"Talk to Willow outside Falador, south of the Ice Mountain dwarves.");
		talkToWillowToStart.addDialogStep("Yes.");

		recruitCheckal = new NpcStep(this, NpcID.CHECKAL, new WorldPoint(3087, 3415, 0),
			"Attempt to recruit Checkal to your team in Barbarian Village.");

		talkToAtlas = new NpcStep(this, NpcID.ATLAS, new WorldPoint(3076, 3440, 0), "Speak to Atlas in the Barbarian" +
			" Village Inn to learn how to Flex.");
		talkToAtlas.addDialogStep("Yes.");

		flexCheckal = new NpcEmoteStep(this, NpcID.CHECKAL, QuestEmote.FLEX, new WorldPoint(3087, 3415, 0), "Flex your muscles at Checkal to prove your worth.");

		talkToMarley = new NpcStep(this, NpcID.MARLEY, new WorldPoint(3088, 3470, 0), "Speak to Marley in the Edgeville" +
			" Ruins.");

		talkToCook = new NpcStep(this, NpcID.COOK_2895, new WorldPoint(3230, 3401, 0), "Ask the Cook at the Blue Moon Inn " +
			"for a steak sandwich.");
		talkToCook.addDialogStep("I was wondering if you'd be able to make me a Steak sandwich?");

		getIngredients = new DetailedQuestStep(this, "Collect meat, bread and a knife to make a steak sandwich.", cookedMeat, bread, knife);

		makeSandwich = new DetailedQuestStep(this, "Use the knife on the bread to make a steak sandwich. Be careful not to eat it!", knifeHighlight, breadHighlight);

		feedMarley = new NpcStep(this, NpcID.MARLEY, new WorldPoint(3088, 3470, 0), "Return to Marley and give him the steak sandwich. Be careful not to eat it!", steakSandwich);

		talkToMarleyAfterFeeding = new NpcStep(this,  NpcID.MARLEY, new WorldPoint(3088, 3470, 0), "Talk to Marley to send him off to the excavation site.");
		feedMarley.addSubSteps(talkToMarleyAfterFeeding);

		talkToBurntof = new NpcStep(this, NpcID.BURNTOF, new WorldPoint(2956, 3367, 0), "Talk to Burntof in the " +
			"Falador Inn.");

		buyBeer = new NpcStep(this, NpcID.KAYLEE, new WorldPoint(2954, 3368, 0), "Buy an Asgarnian Ale for Burntof.",
			coins);
		buyBeer.addDialogSteps("What ales are you serving?", "One Asgarnian Ale, please.");

		giveBeer = new NpcStep(this, NpcID.BURNTOF, new WorldPoint(2956, 3367, 0), "Give Burntof the Asgarnian Ale.", beerHighlight);

		playRPS = new NpcStep(this, NpcID.BURNTOF, new WorldPoint(2956, 3367, 0),
			"Beat Burntof in a match of Rock-Paper-Scissors. Your choices of Rock, Paper and Scissors do not matter.");
		playRPS.addDialogStep("Rock.");

		goToDungeon = new NpcStep(this, NpcID.WILLOW, new WorldPoint(2996, 3494, 0), "Talk with Willow at the " +
			"dungeon entrance on the west side of Ice Mountain.");
		goToDungeon.addDialogStep("Yes.");

		reenterDungeon = new ObjectStep(this, NullObjectID.NULL_41357, new WorldPoint(3000, 3494, 0), "Re-enter the " +
			"dungeon to finish the quest.");
		reenterDungeon.addDialogStep("Yes.");

		defeatGuardian = new NpcStep(this, NpcID.ANCIENT_GUARDIAN, "Defeat the Lvl-25 Ancient Guardian. " +
			"Alternatively, with Level 10 Mining, mine the 4 pillars in the corners.");
		defeatGuardian.addSubSteps(reenterDungeon);

		watchCutscene = new ObjectStep(this, NullObjectID.NULL_41357, new WorldPoint(3000, 3494, 0), "Watch the cutscene to " +
			"finish the quest.");
		watchCutscene.addDialogStep("Yes.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		List<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(cookedMeat);
		reqs.add(bread);
		reqs.add(knife);
		reqs.add(coins);
		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(iceMountainTeleport, faladorTeleport, varrockTeleport, combatGearOrPickaxe);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Ancient Guardian (level 25), or 10 mining + a pickaxe");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return Collections.singletonList(new QuestPointRequirement(16));
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("Coins", ItemID.COINS_995, 2000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Access to the Ruins of Camdozaal."),
				new UnlockReward("Flex Emote"),
				new UnlockReward("The ability to make a steak sandwich")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting Off", Collections.singletonList(talkToWillowToStart)));

		PanelDetails checkalPanel = new PanelDetails("Recruit Checkal",
			Arrays.asList(recruitCheckal, talkToAtlas, flexCheckal));
		checkalPanel.setLockingStep(getCheckal);
		allSteps.add(checkalPanel);

		PanelDetails marleyPanel = new PanelDetails("Recruit Marley",
			Arrays.asList(talkToMarley, talkToCook, getIngredients, makeSandwich, feedMarley), cookedMeat, bread, knife);
		marleyPanel.setLockingStep(getMarley);
		allSteps.add(marleyPanel);

		PanelDetails burntofPanel = new PanelDetails("Recruit Burntof",
			Arrays.asList(talkToBurntof, buyBeer, giveBeer, playRPS), coins);
		burntofPanel.setLockingStep(getBurntof);
		allSteps.add(burntofPanel);

		allSteps.add(new PanelDetails("Excavation!", Arrays.asList(goToDungeon, defeatGuardian, watchCutscene), combatGearOrPickaxe));

		return allSteps;
	}
}
