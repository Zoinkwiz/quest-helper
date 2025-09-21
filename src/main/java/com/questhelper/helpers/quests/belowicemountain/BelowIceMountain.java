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
package com.questhelper.helpers.quests.belowicemountain;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.quest.QuestPointRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.var.VarbitBuilder;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcEmoteStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.emote.QuestEmote;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

public class BelowIceMountain extends BasicQuestHelper
{
	// Required items
	ItemRequirement cookedMeat;
	ItemRequirement bread;
	ItemRequirement knife;
	ItemRequirement coins;

	// Recommended items
	ItemRequirement iceMountainTeleport;
	ItemRequirement faladorTeleport;
	ItemRequirement varrockTeleport;
	ItemRequirement combatGearOrPickaxe;

	// Mid-quest item requirements
	ItemRequirement steakSandwich;
	ItemRequirement beer;

	// Miscellaneous requirements
	Conditions needFlex;
	Conditions haveFlex;
	VarbitRequirement recruitedCheckal;
	VarbitRequirement needRecipe;
	VarbitRequirement haveRecipe;
	ItemRequirements haveIngredients;
	VarbitRequirement fedMarley;
	VarbitRequirement recruitedMarley;
	VarbitRequirement needBeer;
	VarbitRequirement gaveBeer;
	VarbitRequirement needRPS;
	VarbitRequirement recruitedBurntof;
	NpcRequirement inDungeon;

	// Steps
	NpcStep talkToWillowToStart;

	NpcStep recruitCheckal;
	NpcStep talkToAtlas;
	NpcEmoteStep flexCheckal;

	NpcStep talkToMarley;
	NpcStep talkToCook;
	DetailedQuestStep getIngredients;
	DetailedQuestStep makeSandwich;
	NpcStep feedMarley;
	NpcStep talkToMarleyAfterFeeding;

	NpcStep talkToBurntof;
	NpcStep buyBeer;
	NpcStep giveBeer;
	NpcStep playRPS;

	NpcStep goToDungeon;

	ObjectStep reenterDungeon;

	NpcStep defeatGuardian;

	ObjectStep watchCutscene;

	@Override
	protected void setupRequirements()
	{
		cookedMeat = new ItemRequirement("Cooked Meat", ItemID.COOKED_MEAT);
		cookedMeat.canBeObtainedDuringQuest();
		bread = new ItemRequirement("Bread", ItemID.BREAD);
		knife = new ItemRequirement("Knife", ItemID.KNIFE).isNotConsumed();
		coins = new ItemRequirement("Coins", ItemCollections.COINS, 3);

		steakSandwich = new ItemRequirement("Steak Sandwich", ItemID.BIM_STEAK_SANDWICH);

		beer = new ItemRequirement(true, "Asgarnian Ale", ItemID.ASGARNIAN_ALE);

		iceMountainTeleport = new ItemRequirement("A teleport to near Ice Mountain", ItemCollections.AMULET_OF_GLORIES);
		iceMountainTeleport.addAlternates(ItemCollections.COMBAT_BRACELETS);
		iceMountainTeleport.addAlternates(ItemID.POH_TABLET_FALADORTELEPORT, ItemID.TABLET_LASSAR);
		faladorTeleport = new ItemRequirement("Falador teleport", ItemID.POH_TABLET_FALADORTELEPORT);
		varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.POH_TABLET_VARROCKTELEPORT);
		combatGearOrPickaxe = new ItemRequirement("Combat gear or a pickaxe if you don't want to fight", -1, -1).isNotConsumed();
		combatGearOrPickaxe.setDisplayItemId(BankSlotIcons.getCombatGear());

		var checkalState = new VarbitBuilder(VarbitID.BIM_CHECKAL);
		needFlex = or(checkalState.eq(5), checkalState.eq(10));
		haveFlex = or(checkalState.eq(15), checkalState.eq(20));
		recruitedCheckal = checkalState.eq(40);

		var marleyState = new VarbitBuilder(VarbitID.BIM_MARLEY);
		needRecipe = marleyState.eq(5);
		haveRecipe = marleyState.eq(10);
		haveIngredients = new ItemRequirements(cookedMeat, bread, knife);
		fedMarley = marleyState.eq(35);
		recruitedMarley = marleyState.eq(40);

		var burntofState = new VarbitBuilder(VarbitID.BIM_BURNTOF);
		needBeer = burntofState.eq(5);
		gaveBeer = burntofState.eq(10);
		needRPS = burntofState.eq(15);
		recruitedBurntof = burntofState.eq(40);

		inDungeon = new NpcRequirement("Ancient Guardian", NpcID.BIM_GOLEM_BOSS);
	}

	public void setupSteps()
	{
		talkToWillowToStart = new NpcStep(this, NpcID.BIM_WILLOW, new WorldPoint(3003, 3435, 0),
			"Talk to Willow outside Falador, south of the Ice Mountain dwarves.");
		talkToWillowToStart.addDialogStep("Yes.");

		recruitCheckal = new NpcStep(this, NpcID.BIM_CHECKAL, new WorldPoint(3087, 3415, 0),
			"Attempt to recruit Checkal to your team in Barbarian Village.");

		talkToAtlas = new NpcStep(this, NpcID.BIM_ATLAS, new WorldPoint(3076, 3440, 0), "Speak to Atlas in the Barbarian" +
			" Village Inn to learn how to Flex. Pick up a cooked meat if you need one.", cookedMeat);
		talkToAtlas.addDialogStep("Yes.");

		flexCheckal = new NpcEmoteStep(this, NpcID.BIM_CHECKAL, QuestEmote.FLEX, new WorldPoint(3087, 3415, 0), "Talk to Checkal and flex your muscles to prove your worth.");

		talkToMarley = new NpcStep(this, NpcID.BIM_MARLEY, new WorldPoint(3088, 3470, 0), "Speak to Marley in the Edgeville" +
			" Ruins. Pick up the cooked meat from the Barbarian longhall if you don't have it already.");

		talkToCook = new NpcStep(this, NpcID.FAI_VARROCK_BLUEMOON_CHEF, new WorldPoint(3230, 3401, 0), "Ask the Cook at the Blue Moon Inn " +
			"for a steak sandwich.");
		talkToCook.addDialogStep("I was wondering if you'd be able to make me a Steak sandwich?");

		getIngredients = new DetailedQuestStep(this, "Collect meat, bread and a knife to make a steak sandwich.", cookedMeat, bread, knife);

		makeSandwich = new DetailedQuestStep(this, "Use the knife on the bread to make a steak sandwich. Be careful not to eat it!", knife.highlighted(), bread.highlighted());

		feedMarley = new NpcStep(this, NpcID.BIM_MARLEY, new WorldPoint(3088, 3470, 0), "Return to Marley and give him the steak sandwich. Be careful not to eat it!", steakSandwich);

		talkToMarleyAfterFeeding = new NpcStep(this, NpcID.BIM_MARLEY, new WorldPoint(3088, 3470, 0), "Talk to Marley to send him off to the excavation site.");
		feedMarley.addSubSteps(talkToMarleyAfterFeeding);

		talkToBurntof = new NpcStep(this, NpcID.BIM_BURNTOF, new WorldPoint(2956, 3367, 0), "Talk to Burntof in the " +
			"Falador Inn.");

		buyBeer = new NpcStep(this, NpcID.RISINGSUN_BARMAID2, new WorldPoint(2954, 3368, 0), "Buy an Asgarnian Ale for Burntof.",
			coins);
		buyBeer.addDialogSteps("What ales are you serving?", "One Asgarnian Ale, please.");

		giveBeer = new NpcStep(this, NpcID.BIM_BURNTOF, new WorldPoint(2956, 3367, 0), "Give Burntof the Asgarnian Ale.", beer);

		playRPS = new NpcStep(this, NpcID.BIM_BURNTOF, new WorldPoint(2956, 3367, 0),
			"Beat Burntof in a match of Rock-Paper-Scissors. Your choices of Rock, Paper and Scissors do not matter.");
		playRPS.addDialogStep("Rock.");

		goToDungeon = new NpcStep(this, NpcID.BIM_WILLOW, new WorldPoint(2996, 3494, 0), "Talk with Willow at the " +
			"dungeon entrance on the west side of Ice Mountain.");
		goToDungeon.addDialogStep("Yes.");

		reenterDungeon = new ObjectStep(this, ObjectID.BIM_ENTRANCE, new WorldPoint(3000, 3494, 0), "Re-enter the " +
			"dungeon to finish the quest.");
		reenterDungeon.addDialogStep("Yes.");

		defeatGuardian = new NpcStep(this, NpcID.BIM_GOLEM_BOSS, "Defeat the Lvl-25 Ancient Guardian. " +
			"Alternatively, with Level 10 Mining, mine the 4 pillars in the corners.");
		defeatGuardian.addSubSteps(reenterDungeon);

		watchCutscene = new ObjectStep(this, ObjectID.BIM_ENTRANCE, new WorldPoint(3000, 3494, 0), "Watch the cutscene to " +
			"finish the quest.");
		watchCutscene.addDialogStep("Yes.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToWillowToStart);
		steps.put(5, talkToWillowToStart);
		steps.put(7, talkToWillowToStart);

		var getCheckal = new ConditionalStep(this, recruitCheckal);
		getCheckal.addStep(needFlex, talkToAtlas);
		getCheckal.addStep(haveFlex, flexCheckal);
		getCheckal.setLockingCondition(recruitedCheckal);
		steps.put(10, getCheckal);

		var getMarley = new ConditionalStep(this, talkToMarley);
		getMarley.addStep(fedMarley, talkToMarleyAfterFeeding);
		getMarley.addStep(needRecipe, talkToCook);
		getMarley.addStep(steakSandwich, feedMarley);
		getMarley.addStep(and(haveRecipe, haveIngredients), makeSandwich);
		getMarley.addStep(haveRecipe, getIngredients);
		getMarley.setLockingCondition(recruitedMarley);

		var getBurntof = new ConditionalStep(this, talkToBurntof);
		getBurntof.addStep(needRPS, playRPS);
		getBurntof.addStep(gaveBeer, playRPS);
		getBurntof.addStep(and(needBeer, beer), giveBeer);
		getBurntof.addStep(needBeer, buyBeer);
		getBurntof.setLockingCondition(recruitedBurntof);

		var marleyAndBurntof = new ConditionalStep(this, getMarley);
		marleyAndBurntof.addStep(recruitedMarley, getBurntof);
		steps.put(15, marleyAndBurntof);

		steps.put(20, goToDungeon);
		steps.put(25, goToDungeon);
		steps.put(30, reenterDungeon);

		var guardian = new ConditionalStep(this, reenterDungeon);
		guardian.addStep(inDungeon, defeatGuardian);
		steps.put(35, guardian);

		steps.put(40, watchCutscene);

		return steps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestPointRequirement(16)
		);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			cookedMeat,
			bread,
			knife,
			coins
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			iceMountainTeleport,
			faladorTeleport,
			varrockTeleport,
			combatGearOrPickaxe
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Ancient Guardian (level 25), or 10 mining + a pickaxe"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Coins", ItemID.COINS, 2000)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to the Ruins of Camdozaal."),
			new UnlockReward("Flex Emote"),
			new UnlockReward("The ability to make a steak sandwich")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting Off", List.of(
			talkToWillowToStart
		)));

		sections.add(new PanelDetails("Recruit Checkal", List.of(
			recruitCheckal,
			talkToAtlas,
			flexCheckal
		)));

		sections.add(new PanelDetails("Recruit Marley", List.of(
			talkToMarley,
			talkToCook,
			getIngredients,
			makeSandwich,
			feedMarley
		), List.of(
			cookedMeat,
			bread,
			knife
		)));

		sections.add(new PanelDetails("Recruit Burntof", List.of(
			talkToBurntof,
			buyBeer,
			giveBeer,
			playRPS
		), List.of(
			coins
		)));

		sections.add(new PanelDetails("Excavation!", List.of(
			goToDungeon, defeatGuardian, watchCutscene
		), List.of(
			combatGearOrPickaxe
		)));

		return sections;
	}
}
