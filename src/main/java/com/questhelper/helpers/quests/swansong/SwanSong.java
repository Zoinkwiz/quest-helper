/*
 * Copyright (c) 2020, Zoinkwiz
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
package com.questhelper.helpers.quests.swansong;

import com.questhelper.collections.ItemCollections;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestPointRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

public class SwanSong extends BasicQuestHelper
{
	//Item Requirements
	ItemRequirement mist10, lava10, blood5, bones7, pot, potLid, ironBar5, log, tinderbox, hammer, brownApron, monkfish5, rawMonkfish5, combatGear, potHiglight,
		potLidHiglight, tinderboxHiglight, ironBar5Higlight, logHiglight, ironSheet5, smallNet, airtightPot, combatGearRanged, boneSeeds, hammerPanel;

	// Recommended
	ItemRequirement draynorTeleport, piscTeleport, yanilleTeleport, craftingGuildTeleport, cookingGauntlets;

	Requirement inColonyEntrance, talkedToFranklin, addedLog, litLog, wall1Fixed, wall2Fixed, wall3Fixed, wall4Fixed, wall5Fixed, wallsFixed,
		talkedToArnold, finishedFranklin, inBasement, queenNearby;

	DetailedQuestStep talkToHerman, talkToWom, talkToWomAtColony, kill79Trolls, talkToHermanInBuilding, talkToFranklin, enterColony, useLog, useTinderbox, talkToArnold,
		talkToFranklinAgain, talkToHermanAfterTasks, enterWizardsBasement, talkToFruscone, talkToMalignius, talkToCrafter, makeAirtightPot, talkToMaligniusWithPot,
		talkToHermanForFinalFight, killQueen, talkToHermanToFinish, talkToHermanWithPot;

	FixWall repairWall;

	FishMonkfish fishAndCookMonkfish;

	//Zones
	Zone colonyEntrance, basement;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToHerman);
		steps.put(5, talkToHerman);
		steps.put(10, talkToWom);
		steps.put(15, talkToWom);
		steps.put(20, talkToWom);
		steps.put(30, talkToWomAtColony);
		steps.put(40, talkToWomAtColony);

		ConditionalStep defeatTrolls = new ConditionalStep(this, enterColony);
		defeatTrolls.addStep(inColonyEntrance, kill79Trolls);
		steps.put(50, defeatTrolls);

		steps.put(55, talkToHermanInBuilding);
		steps.put(60, talkToHermanInBuilding);
		steps.put(65, talkToHermanInBuilding);

		ConditionalStep completeTasks = new ConditionalStep(this, talkToFranklin);
		completeTasks.addStep(new Conditions(talkedToArnold, finishedFranklin), fishAndCookMonkfish);
		completeTasks.addStep(finishedFranklin, talkToArnold);
		completeTasks.addStep(wallsFixed, talkToFranklinAgain);
		completeTasks.addStep(litLog, repairWall);
		completeTasks.addStep(addedLog, useTinderbox);
		completeTasks.addStep(talkedToFranklin, useLog);

		steps.put(70, completeTasks);

		steps.put(80, talkToHermanAfterTasks);

		ConditionalStep goTalkToFru = new ConditionalStep(this, enterWizardsBasement);
		goTalkToFru.addStep(inBasement, talkToFruscone);

		steps.put(90, goTalkToFru);
		steps.put(95, goTalkToFru);

		steps.put(100, talkToMalignius);
		steps.put(110, talkToMalignius);
		steps.put(120, talkToCrafter);

		ConditionalStep getPotToMal = new ConditionalStep(this, makeAirtightPot);
		getPotToMal.addStep(airtightPot, talkToMaligniusWithPot);

		steps.put(130, getPotToMal);

		steps.put(140, talkToHermanWithPot);

		ConditionalStep bossFight = new ConditionalStep(this, talkToHermanForFinalFight);
		bossFight.addStep(queenNearby, killQueen);
		steps.put(150, talkToHermanForFinalFight);
		steps.put(160, talkToHermanForFinalFight);

		steps.put(170, killQueen);

		steps.put(180, talkToHermanToFinish);
		steps.put(190, talkToHermanToFinish);
		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		mist10 = new ItemRequirement("Mist rune", ItemID.MIST_RUNE, 10);
		lava10 = new ItemRequirement("Lava rune", ItemID.LAVA_RUNE, 10);
		blood5 = new ItemRequirement("Blood rune", ItemID.BLOOD_RUNE, 5);
		bones7 = new ItemRequirement("Bones", ItemID.BONES, 7);
		bones7.setTooltip("Obtainable during the quest, just pick up bones when killing the sea trolls.");

		pot = new ItemRequirement("Pot", ItemID.POT);
		potLid = new ItemRequirement("Pot lid", ItemID.POT_LID);
		airtightPot = new ItemRequirement("Airtight pot", ItemID.AIRTIGHT_POT);
		ironBar5 = new ItemRequirement("Iron bar", ItemID.IRON_BAR, 5);
		log = new ItemRequirement("Any log", ItemID.LOGS);
		log.addAlternates(ItemID.OAK_LOGS, ItemID.WILLOW_LOGS, ItemID.MAPLE_LOGS, ItemID.YEW_LOGS, ItemID.MAGIC_LOGS, ItemID.TEAK_LOGS, ItemID.MAHOGANY_LOGS, ItemID.REDWOOD_LOGS);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).isNotConsumed();

		potHiglight = new ItemRequirement("Pot", ItemID.POT);
		potHiglight.setHighlightInInventory(true);
		potLidHiglight = new ItemRequirement("Pot lid", ItemID.POT_LID);
		potLidHiglight.setTooltip("You can make one from wet clay on a potter's wheel");
		potLidHiglight.setHighlightInInventory(true);
		ironBar5Higlight = new ItemRequirement("Iron bar", ItemID.IRON_BAR, 5);
		ironBar5Higlight.setHighlightInInventory(true);
		logHiglight = new ItemRequirement("Any log", ItemID.LOGS);
		logHiglight.addAlternates(ItemID.OAK_LOGS, ItemID.WILLOW_LOGS, ItemID.MAPLE_LOGS, ItemID.YEW_LOGS, ItemID.MAGIC_LOGS, ItemID.TEAK_LOGS, ItemID.MAHOGANY_LOGS, ItemID.REDWOOD_LOGS);
		logHiglight.setHighlightInInventory(true);
		tinderboxHiglight = tinderbox.highlighted();

		// Recommended
		draynorTeleport = new ItemRequirement("Teleport to Draynor Village", ItemCollections.AMULET_OF_GLORIES);
		piscTeleport = new ItemRequirement("Piscatoris teleport", ItemID.PISCATORIS_TELEPORT);
		yanilleTeleport = new ItemRequirement("Yanille teleport or Nightmare Zone minigame teleport", ItemID.WATCHTOWER_TELEPORT);
		craftingGuildTeleport = new ItemRequirement("Crafting Guild teleport", ItemCollections.SKILLS_NECKLACES);
		cookingGauntlets = new ItemRequirement("Cooking gauntlets for lower burn chance on monkfish", ItemID.COOKING_GAUNTLETS);

		smallNet = new ItemRequirement("Small fishing net", ItemID.SMALL_FISHING_NET).isNotConsumed();
		smallNet.setTooltip("You can get one from Arnold");

		hammerPanel = new ItemRequirement("Hammer (obtainable in quest)", ItemCollections.HAMMER).isNotConsumed();
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed();
		hammer.setTooltip("Franklin will give you one");
		brownApron = new ItemRequirement("Brown apron", ItemID.BROWN_APRON, 1, true).isNotConsumed();
		brownApron.setHighlightInInventory(true);
		brownApron.setTooltip("Malignius will give you one");
		monkfish5 = new ItemRequirement("Fresh monkfish", ItemID.FRESH_MONKFISH_7943, 5);
		rawMonkfish5 = new ItemRequirement("Fresh monkfish", ItemID.FRESH_MONKFISH, 5);
		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		combatGearRanged = new ItemRequirement("Ranged or melee combat gear", -1, -1).isNotConsumed();
		combatGearRanged.setDisplayItemId(BankSlotIcons.getCombatGear());
		ironSheet5 = new ItemRequirement("Iron sheet", ItemID.IRON_SHEET, 5);

		boneSeeds = new ItemRequirement("Bone seed", ItemID.BONE_SEEDS);
		boneSeeds.setTooltip("You can get more by bringing Malignius another airtight pot");
	}

	@Override
	protected void setupZones()
	{
		colonyEntrance = new Zone(new WorldPoint(2338, 3653, 0), new WorldPoint(2349, 3662, 0));
		basement = new Zone(new WorldPoint(2582, 9484, 0), new WorldPoint(2594, 9489, 0));
	}

	public void setupConditions()
	{
		inColonyEntrance = new ZoneRequirement(colonyEntrance);
		inBasement = new ZoneRequirement(basement);
		// 2111 is number of trolls killed

		talkedToFranklin = new VarbitRequirement(2099, 1);
		addedLog = new VarbitRequirement(2099, 2);
		litLog = new VarbitRequirement(2099, 3);
		wall1Fixed = new VarbitRequirement(2100, 1);
		wall2Fixed = new VarbitRequirement(2101, 1);
		wall3Fixed = new VarbitRequirement(2102, 1);
		wall4Fixed = new VarbitRequirement(2103, 1);
		wall5Fixed = new VarbitRequirement(2104, 1);
		wallsFixed = new Conditions(wall1Fixed, wall2Fixed, wall3Fixed, wall4Fixed, wall5Fixed);

		talkedToArnold = new VarbitRequirement(2105, 1, Operation.GREATER_EQUAL);
		finishedFranklin = new VarbitRequirement(2099, 4);
		queenNearby = new NpcCondition(NpcID.SEA_TROLL_QUEEN);

		// 2108 = number of bones given to Mort
	}

	public void setupSteps()
	{
		talkToHerman = new NpcStep(this, NpcID.HERMAN_CARANOS, new WorldPoint(2345, 3651, 0), "Talk to Herman Carnos outside the Fishing Colony.");
		talkToHerman.addTeleport(piscTeleport);
		talkToHerman.addDialogSteps("What's the rush?", "Do you need any help?", "I'm a brave adventurer! Can I try?", "Yes.");
		talkToWom = new NpcStep(this, NpcID.WISE_OLD_MAN, new WorldPoint(3089, 3253, 0), "Talk to the Wise Old Man in Draynor Village.", blood5, lava10, mist10);
		talkToWom.addTeleport(draynorTeleport);
		talkToWom.addDialogSteps("Draynor Village", "You used to be a famous adventurer, didn't you?");
		talkToWomAtColony = new NpcStep(this, NpcID.WISE_OLD_MAN_2112, new WorldPoint(2345, 3651, 0), "Talk to the Wise Old Man outside the Fishing Colony. Be prepared to fight sea trolls.", combatGear);
		talkToWomAtColony.addTeleport(piscTeleport);
		talkToWomAtColony.addDialogStep("I'm ready to fight.");
		enterColony = new ObjectStep(this, ObjectID.HOLE_12656, new WorldPoint(2344, 3651, 0), "Enter the Fishing Colony, prepared to fight trolls.", combatGear, log, tinderbox, ironBar5, hammer);
		talkToWomAtColony.addSubSteps(enterColony);
		kill79Trolls = new NpcStep(this, NpcID.SEA_TROLL, new WorldPoint(2343, 3657, 0), "Kill the sea trolls.", true);
		talkToHermanInBuilding = new NpcStep(this, NpcID.HERMAN_CARANOS, new WorldPoint(2354, 3683, 0), "Talk to Herman in the east of the Fishing Colony.");
		talkToFranklin = new NpcStep(this, NpcID.FRANKLIN_CARANOS, new WorldPoint(2341, 3667, 0), "Talk to Franklin in the middle of the Fishing Colony.");
		useLog = new ObjectStep(this, NullObjectID.NULL_13702, new WorldPoint(2344, 3676, 0), "Add some logs to the firebox in the building with a furnace.", logHiglight);
		useLog.addIcon(ItemID.LOGS);
		useTinderbox = new ObjectStep(this, NullObjectID.NULL_13702, new WorldPoint(2344, 3676, 0), "Light the logs in the firebox in the building with a furnace.", tinderboxHiglight);
		useTinderbox.addIcon(ItemID.TINDERBOX);
		repairWall = new FixWall(this);

		talkToArnold = new NpcStep(this, NpcID.ARNOLD_LYDSPOR, new WorldPoint(2329, 3688, 0), "Talk to Arnold at the bank in the Fishing Colony.");
		fishAndCookMonkfish = new FishMonkfish(this);

		talkToFranklinAgain = new NpcStep(this, NpcID.FRANKLIN_CARANOS, new WorldPoint(2341, 3667, 0), "Talk to Franklin again.");
		talkToHermanAfterTasks = new NpcStep(this, NpcID.HERMAN_CARANOS, new WorldPoint(2354, 3683, 0), "Talk to Herman in the east of the colony again.");

		enterWizardsBasement = new ObjectStep(this, ObjectID.LADDER_17384, new WorldPoint(2594, 3085, 0), "Talk to Wizard Frumscone in the Wizards' Guild basement.");
		enterWizardsBasement.addTeleport(yanilleTeleport);

		talkToFruscone = new NpcStep(this, NpcID.WIZARD_FRUMSCONE, new WorldPoint(2587, 9488, 0), "Talk to Wizard Frumscone in the Wizards' Guild basement.");
		talkToFruscone.addDialogStep("I'll see what the necromancer needs me to do.");
		talkToFruscone.addSubSteps(enterWizardsBasement);
		talkToMalignius = new NpcStep(this, NpcID.MALIGNIUS_MORTIFER, new WorldPoint(2993, 3269, 0), "Talk to Malignius Mortifer near Port Sarim.", bones7);
		talkToMalignius.addDialogStep("I need help with saving a fishing colony.");
		talkToMalignius.addTeleport(craftingGuildTeleport);
		talkToCrafter = new NpcStep(this, NpcID.MASTER_CRAFTER_5812, new WorldPoint(2937, 3290, 0), "Talk to a Master Crafter with dreadlocks in the Crafting Guild.", brownApron);
		talkToCrafter.addDialogStep("Swan Song.");
		makeAirtightPot = new DetailedQuestStep(this, "Add a pot lid to a pot.", potHiglight, potLidHiglight);
		talkToMaligniusWithPot = new NpcStep(this, NpcID.MALIGNIUS_MORTIFER, new WorldPoint(2993, 3269, 0), "Bring the airtight pot and 7 bones to Malignius Mortifer near Port Sarim. Make sure you're equipped for the fight before speaking to Malignus.", airtightPot);
		talkToMaligniusWithPot.addDialogStep("I've spoken to the master crafter...");
		talkToHermanWithPot = new NpcStep(this, NpcID.HERMAN_CARANOS, new WorldPoint(2354, 3683, 0),
			"Be prepared to fight, and talk to Herman in the colony.", combatGearRanged, boneSeeds);
		talkToHermanWithPot.addDialogSteps("I've lost the army!");

		talkToHermanForFinalFight = new NpcStep(this, NpcID.HERMAN_CARANOS, new WorldPoint(2354, 3683, 0),
			"Be prepared to fight, and talk to Herman in the colony.", combatGearRanged);
		talkToHermanForFinalFight.addDialogStep("I'm ready. Let's fight!");
		talkToHermanForFinalFight.addSubSteps(talkToHermanWithPot);

		killQueen = new NpcStep(this, NpcID.SEA_TROLL_QUEEN, new WorldPoint(2347, 3704, 0), "Kill the Sea Troll Queen.", combatGearRanged);
		killQueen.addDialogStep("No, I'm ready to move on with the quest.");
		talkToHermanToFinish = new NpcStep(this, NpcID.HERMAN_CARANOS, new WorldPoint(2354, 3683, 0), "Talk to Herman to finish the quest.");
	}

	@Override
	protected List<ItemRequirement> generateItemRequirements()
	{
		return Arrays.asList(mist10, lava10, blood5, bones7, pot, potLid, ironBar5, log, tinderbox, hammerPanel);
	}

	@Override
    protected List<ItemRequirement> generateItemRecommended()
	{
		return Arrays.asList(draynorTeleport, piscTeleport.quantity(2), yanilleTeleport, craftingGuildTeleport, cookingGauntlets);
	}


	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("11 Sea trolls (levels 65/79/87/101)");
		reqs.add("Sea troll queen (level 170)");
		return reqs;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestPointRequirement(100));
		req.add(new QuestRequirement(QuestHelperQuest.ONE_SMALL_FAVOUR, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.GARDEN_OF_TRANQUILLITY, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.MAGIC, 66, true));
		req.add(new SkillRequirement(Skill.COOKING, 62, true));
		req.add(new SkillRequirement(Skill.FISHING, 62, true));
		req.add(new SkillRequirement(Skill.SMITHING, 45, true));
		req.add(new SkillRequirement(Skill.CRAFTING, 40, true));
		req.add(new SkillRequirement(Skill.FIREMAKING, 42));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
			new ExperienceReward(Skill.MAGIC, 15000),
			new ExperienceReward(Skill.PRAYER, 10000),
			new ExperienceReward(Skill.FISHING, 50000)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("Coins", ItemID.COINS_995, 25000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Access to the Piscatoris Fishing Colony"),
			new UnlockReward("The ability to fish Monkfish"));
	}

	@Override
    protected List<PanelDetails> setupPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToHerman, talkToWom), Arrays.asList(blood5, mist10, lava10), Arrays.asList(piscTeleport, draynorTeleport)));
		allSteps.add(new PanelDetails("Entering the colony", Arrays.asList(talkToWomAtColony, kill79Trolls, talkToHermanInBuilding), Arrays.asList(combatGear, log, tinderbox, ironBar5, hammerPanel),
			Collections.singletonList(piscTeleport)));
		List<QuestStep> helpingSteps = QuestUtil.toArrayList(talkToFranklin, useLog, useTinderbox);
		helpingSteps.addAll(repairWall.getDisplaySteps());
		helpingSteps.add(talkToFranklinAgain);
		allSteps.add(new PanelDetails("Helping Franklin", helpingSteps, Arrays.asList(combatGear, log, tinderbox, ironBar5, hammerPanel)));

		List<QuestStep> helpingArnoldSteps = QuestUtil.toArrayList(talkToArnold);
		helpingArnoldSteps.addAll(fishAndCookMonkfish.getSteps());
		helpingArnoldSteps.add(talkToHermanAfterTasks);

		allSteps.add(new PanelDetails("Helping Arnold", helpingArnoldSteps, Collections.singletonList(combatGear), Collections.singletonList(cookingGauntlets)));
		allSteps.add(new PanelDetails("Making an army", Arrays.asList(talkToFruscone, talkToMalignius,
			talkToCrafter, makeAirtightPot, talkToMaligniusWithPot), Arrays.asList(bones7, pot, potLid, combatGearRanged), Arrays.asList(yanilleTeleport, craftingGuildTeleport)));
		allSteps.add(new PanelDetails("Defeating the trolls", Arrays.asList(talkToHermanForFinalFight, killQueen, talkToHermanToFinish), combatGearRanged));
		return allSteps;
	}
}
