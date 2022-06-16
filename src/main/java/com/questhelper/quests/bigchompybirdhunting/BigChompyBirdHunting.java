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
package com.questhelper.quests.bigchompybirdhunting;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.NpcHintArrowRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.util.LogicType;
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
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;

@QuestDescriptor(
	quest = QuestHelperQuest.BIG_CHOMPY_BIRD_HUNTING
)
public class BigChompyBirdHunting extends BasicQuestHelper
{
	//Items Required
	ItemRequirement axe, feathers, knife, hammer, chisel, wolfBones4, acheyLogs, chompy, bloatedToad, knifeHighlighted, feathersHighlighted,
		shaftsHighlighted, wolfBonesHighlighted, tipsHighlighted, flightedArrowsHighlighted, emptyBellow, fullBellow, ogreArrows6Highlighted,
		ogreArrows, ogreBow, ogreBowInventory, onion, tomato, potato, doogle, equa, cabbage, chompyHighlighted, seasonedChompy,
		seasonedChompyHighlighted, bellow;

	Requirement inCave, chompyNearby, deadChompyNearby, rantzWantsOnion, rantzWantsPotato, knowWhatRantzWants,
		bugsWantsEqua, bugsWantsCabbage, knowWhatBugsWants, fycieWantsDoogle, fycieWantsTomato, knowWhatFycieWants,
		hasRantzItem, hasFycieItem, hasBugsItem;

	DetailedQuestStep talkToRantz, getLogs, makeShafts, useFeathersOnShafts, useChiselOnBones, useTipsOnShafts, useArrowsOnRantz,
		askRantzQuestions, enterCave, getBellow, leaveCave, fillBellows, inflateToad, talkToRantzWithToad, dropToad, waitForChompy,
		talkToRantzForBow, placeAnotherToad, killChompy, pluckCarcass, talkToRantzWithChompy, enterCaveAgain, talkToFycie, talkToBugs,
		leaveCaveAgain, getPotato, getOnion, getTomato, getEqua, getDoogle, getCabbage, getIngredients, cookChompy, giveRantzSeasonedChompy;

	//Zones
	Zone cave;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToRantz);

		ConditionalStep makeArrows = new ConditionalStep(this, getLogs);
		makeArrows.addStep(ogreArrows6Highlighted, useArrowsOnRantz);
		makeArrows.addStep(new Conditions(flightedArrowsHighlighted, tipsHighlighted), useTipsOnShafts);
		makeArrows.addStep(flightedArrowsHighlighted, useChiselOnBones);
		makeArrows.addStep(shaftsHighlighted, useFeathersOnShafts);
		makeArrows.addStep(acheyLogs, makeShafts);

		steps.put(5, makeArrows);

		steps.put(10, askRantzQuestions);

		ConditionalStep goGetBellows = new ConditionalStep(this, enterCave);
		goGetBellows.addStep(inCave, getBellow);
		steps.put(15, goGetBellows);

		ConditionalStep goInflateToad = new ConditionalStep(this, fillBellows);
		goInflateToad.addStep(new Conditions(inCave, bellow), leaveCave);
		goInflateToad.addStep(inCave, getBellow);
		goInflateToad.addStep(bloatedToad, talkToRantzWithToad);
		goInflateToad.addStep(fullBellow, inflateToad);
		steps.put(20, goInflateToad);

		steps.put(25, dropToad);

		steps.put(30, waitForChompy);
		steps.put(35, waitForChompy);

		steps.put(40, talkToRantzForBow);

		ConditionalStep getChompy = new ConditionalStep(this, placeAnotherToad);
		getChompy.addStep(chompyNearby, killChompy);
		steps.put(45, getChompy);

		ConditionalStep bringChompyToRantz = new ConditionalStep(this, talkToRantzWithChompy);
		bringChompyToRantz.addStep(deadChompyNearby, pluckCarcass);
		steps.put(50, bringChompyToRantz);

		ConditionalStep seasonChompy = new ConditionalStep(this, talkToRantzWithChompy);
		seasonChompy.addStep(new Conditions(knowWhatFycieWants, knowWhatBugsWants, inCave), leaveCave);

		seasonChompy.addStep(new Conditions(hasFycieItem, hasBugsItem, hasRantzItem), cookChompy);

		seasonChompy.addStep(new Conditions(fycieWantsTomato, hasBugsItem, hasRantzItem), getTomato);
		seasonChompy.addStep(new Conditions(fycieWantsDoogle, hasBugsItem, hasRantzItem), getDoogle);

		seasonChompy.addStep(new Conditions(knowWhatFycieWants, bugsWantsCabbage, hasRantzItem), getCabbage);
		seasonChompy.addStep(new Conditions(knowWhatFycieWants, bugsWantsEqua, hasRantzItem), getEqua);

		seasonChompy.addStep(new Conditions(knowWhatFycieWants, knowWhatBugsWants, rantzWantsOnion), getOnion);
		seasonChompy.addStep(new Conditions(knowWhatFycieWants, knowWhatBugsWants, rantzWantsPotato), getPotato);

		seasonChompy.addStep(new Conditions(knowWhatBugsWants, inCave), talkToFycie);
		seasonChompy.addStep(new Conditions(inCave), talkToBugs);
		seasonChompy.addStep(knowWhatRantzWants, enterCaveAgain);

		steps.put(55, seasonChompy);

		steps.put(60, giveRantzSeasonedChompy);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		axe = new ItemRequirement("Any axe", ItemCollections.AXES);
		feathers = new ItemRequirement("Feathers", ItemID.FEATHER, 100);
		knife = new ItemRequirement("Knife", ItemID.KNIFE);
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER);
		chisel = new ItemRequirement("Chisel", ItemID.CHISEL);
		chisel.setHighlightInInventory(true);
		wolfBones4 = new ItemRequirement("Wolf bones", ItemID.WOLF_BONES, 4);
		wolfBones4.setTooltip("You can kill wolves (level 64) around Feldip for bones");
		acheyLogs = new ItemRequirement("Achey tree logs", ItemID.ACHEY_TREE_LOGS);
		acheyLogs.setHighlightInInventory(true);

		chompy = new ItemRequirement("Raw chompy", ItemID.RAW_CHOMPY);
		chompyHighlighted = new ItemRequirement("Raw chompy", ItemID.RAW_CHOMPY);
		chompyHighlighted.setHighlightInInventory(true);
		bloatedToad = new ItemRequirement("Bloated toad", ItemID.BLOATED_TOAD);

		knifeHighlighted = new ItemRequirement("Knife", ItemID.KNIFE);
		knifeHighlighted.setHighlightInInventory(true);
		feathersHighlighted = new ItemRequirement("Feathers", ItemID.FEATHER);
		feathersHighlighted.setHighlightInInventory(true);
		shaftsHighlighted = new ItemRequirement("Ogre arrow shafts (make more for later)", ItemID.OGRE_ARROW_SHAFT, 6);
		shaftsHighlighted.setHighlightInInventory(true);
		wolfBonesHighlighted = new ItemRequirement("Wolf bones", ItemID.WOLF_BONES);
		wolfBonesHighlighted.setHighlightInInventory(true);
		tipsHighlighted = new ItemRequirement("Wolfbone arrowtips", ItemID.WOLFBONE_ARROWTIPS);
		tipsHighlighted.setHighlightInInventory(true);
		flightedArrowsHighlighted = new ItemRequirement("Flighted ogre arrow", ItemID.FLIGHTED_OGRE_ARROW);
		flightedArrowsHighlighted.setHighlightInInventory(true);
		ogreArrows6Highlighted = new ItemRequirement("Ogre arrow", ItemID.OGRE_ARROW, 6);
		ogreArrows6Highlighted.setHighlightInInventory(true);

		ogreArrows = new ItemRequirement("Ogre arrow", ItemID.OGRE_ARROW, 1, true);
		ogreBow = new ItemRequirement("Ogre bow", ItemID.OGRE_BOW, 1, true);
		ogreBowInventory = new ItemRequirement("Ogre bow", ItemID.OGRE_BOW);

		emptyBellow = new ItemRequirement("Ogre bellows (empty)", ItemID.OGRE_BELLOWS);
		emptyBellow.setTooltip("You can get more from the chest in Rantz's cave");
		emptyBellow.setHighlightInInventory(true);
		fullBellow = new ItemRequirement("Ogre bellows", ItemID.OGRE_BELLOWS_1);
		fullBellow.addAlternates(ItemID.OGRE_BELLOWS_2, ItemID.OGRE_BELLOWS_3);

		bellow = new ItemRequirement("Ogre bellows", ItemID.OGRE_BELLOWS);
		bellow.addAlternates(ItemID.OGRE_BELLOWS_1, ItemID.OGRE_BELLOWS_2, ItemID.OGRE_BELLOWS_3);

		onion = new ItemRequirement("Onion", ItemID.ONION);
		tomato = new ItemRequirement("Tomato", ItemID.TOMATO);
		potato = new ItemRequirement("Potato", ItemID.POTATO);
		doogle = new ItemRequirement("Doogle leaves", ItemID.DOOGLE_LEAVES);
		equa = new ItemRequirement("Equa leaves", ItemID.EQUA_LEAVES);
		cabbage = new ItemRequirement("Cabbage", ItemID.CABBAGE);

		seasonedChompy = new ItemRequirement("Seasoned chompy", ItemID.SEASONED_CHOMPY);
		seasonedChompyHighlighted = new ItemRequirement("Seasoned chompy", ItemID.SEASONED_CHOMPY);
		seasonedChompyHighlighted.setHighlightInInventory(true);
	}

	public void loadZones()
	{
		cave = new Zone(new WorldPoint(2627, 9377, 0), new WorldPoint(2663, 9406, 0));
	}

	public void setupConditions()
	{
		inCave = new ZoneRequirement(cave);


		chompyNearby = new NpcHintArrowRequirement(NpcID.CHOMPY_BIRD);
		deadChompyNearby = new NpcCondition(NpcID.CHOMPY_BIRD_1476);

		rantzWantsOnion = new Conditions(true, new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "want Onion wiv mine"));
		rantzWantsPotato = new Conditions(true, new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "wants Potato wiv mine"));
		knowWhatRantzWants = new Conditions(LogicType.OR, rantzWantsOnion, rantzWantsPotato);

		bugsWantsCabbage = new Conditions(true, new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "has to have cabbage wiv mine"));
		bugsWantsEqua = new Conditions(true, new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "has to have equa leaves wiv mine"));
		knowWhatBugsWants = new Conditions(LogicType.OR, bugsWantsCabbage, bugsWantsEqua);

		fycieWantsTomato = new Conditions(true, new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "Me's wants tomato wiv mine!"));
		fycieWantsDoogle = new Conditions(true, new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "Me's wants doogle leaves wiv mine!"));
		knowWhatFycieWants = new Conditions(LogicType.OR, fycieWantsTomato, fycieWantsDoogle);


		hasRantzItem = new Conditions(LogicType.OR,
			new Conditions(rantzWantsOnion, onion),
			new Conditions(rantzWantsPotato, potato)
		);
		hasFycieItem = new Conditions(LogicType.OR,
			new Conditions(fycieWantsDoogle, doogle),
			new Conditions(fycieWantsTomato, tomato)
		);
		hasBugsItem = new Conditions(LogicType.OR,
			new Conditions(bugsWantsCabbage, cabbage),
			new Conditions(bugsWantsEqua, equa)
		);
	}

	public void setupSteps()
	{
		talkToRantz = new NpcStep(this, NpcID.RANTZ, new WorldPoint(2631, 2982, 0), "Talk to Rantz in the east of Feldip Hills.");
		talkToRantz.addDialogStep("Ok, I'll make you some 'stabbers'.");
		getLogs = new ObjectStep(this, ObjectID.ACHEY_TREE, new WorldPoint(2627, 2975, 0), "Get some achey tree logs near Rantz.", axe);
		makeShafts = new DetailedQuestStep(this, "Use your knife on the achey logs to make arrow shafts.", knifeHighlighted, acheyLogs);
		useFeathersOnShafts = new DetailedQuestStep(this, "Use the feathers on the arrow shafts.", feathersHighlighted, shaftsHighlighted);
		useChiselOnBones = new DetailedQuestStep(this, "Use a chisel on some wolf bones.", chisel, wolfBonesHighlighted, hammer);
		useTipsOnShafts = new DetailedQuestStep(this, "Add the bone arrow tips to the flighted ogre arrows.", tipsHighlighted, flightedArrowsHighlighted);
		useArrowsOnRantz = new NpcStep(this, NpcID.RANTZ, new WorldPoint(2631, 2982, 0), "Use at least 6 ogre arrows on Rantz.", ogreArrows6Highlighted);
		useArrowsOnRantz.addIcon(ItemID.OGRE_ARROW);
		askRantzQuestions = new NpcStep(this, NpcID.RANTZ, new WorldPoint(2631, 2982, 0), "Ask Rantz all the available questions.");
		askRantzQuestions.addDialogStep("How do we make the chompys come?"); //, "What are 'fatsy toadies'?", "Where do we put the fatsy toadies?", "What do you mean 'sneaky..sneaky, stick da chompy?'"
		enterCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_3379, new WorldPoint(2630, 2999, 0), "Enter Rantz's cave.");
		getBellow = new ObjectStep(this, ObjectID.LOCKED_OGRE_CHEST, new WorldPoint(2638, 9398, 0), "Open the chest in the cave for some ogre bellows.");
		((ObjectStep) getBellow).addAlternateObjects(ObjectID.UNLOCKED_OGRE_CHEST);
		leaveCave = new ObjectStep(this, ObjectID.CAVE_EXIT_3381, new WorldPoint(2647, 9377, 0), "Leave the cave.");
		fillBellows = new ObjectStep(this, ObjectID.SWAMP_BUBBLES, new WorldPoint(2601, 2967, 0), "Fill the bellows on swamp bubbles.", emptyBellow);
		inflateToad = new NpcStep(this, NpcID.SWAMP_TOAD, new WorldPoint(2602, 2967, 0), "Inflate 3 toads near the swamp.", fullBellow);
		talkToRantzWithToad = new NpcStep(this, NpcID.RANTZ, new WorldPoint(2631, 2982, 0), "Bring the toads to Rantz.", bloatedToad);
		dropToad = new DetailedQuestStep(this, new WorldPoint(2635, 2967, 0), "Drop the bloated toad in the clearing south of Rantz.");
		waitForChompy = new DetailedQuestStep(this, new WorldPoint(2635, 2967, 0), "Wait for a chompy to spawn. If the frog despawns, you'll need to place another one in the clearing south of Rantz.");
		talkToRantzForBow = new NpcStep(this, NpcID.RANTZ, new WorldPoint(2631, 2982, 0), "Talk to Rantz again for an ogre bow.");
		talkToRantzForBow.addDialogSteps("Come on, let me have a go...", "I'm actually quite strong...please let me try.");
		placeAnotherToad = new DetailedQuestStep(this, new WorldPoint(2635, 2967, 0), "Drop another bloated toad in the clearing south of Rantz, and wait for a chompy to come.", ogreBow, ogreArrows);

		killChompy = new NpcStep(this, NpcID.CHOMPY_BIRD, new WorldPoint(2635, 2966, 0), "Kill the chompy. You can only hurt it with an ogre bow + ogre arrows.", ogreBow, ogreArrows);
		pluckCarcass = new NpcStep(this, NpcID.CHOMPY_BIRD_1476, new WorldPoint(2635, 2966, 0), "Pluck the chompy.");
		talkToRantzWithChompy = new NpcStep(this, NpcID.RANTZ, new WorldPoint(2631, 2982, 0), "Talk to Rantz in the east of Feldip Hills.", chompy);

		enterCaveAgain = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_3379, new WorldPoint(2630, 2999, 0), "Enter Rantz's cave.");
		talkToFycie = new NpcStep(this, NpcID.FYCIE, new WorldPoint(2649, 9391, 0), "Talk to Fycie.");
		talkToBugs = new NpcStep(this, NpcID.BUGS, new WorldPoint(2640, 9391, 0), "Talk to Bugs.");
		leaveCaveAgain = new ObjectStep(this, ObjectID.CAVE_EXIT_3381, new WorldPoint(2647, 9377, 0), "Leave the cave.");

		getPotato = new ObjectStep(this, ObjectID.POTATO, new WorldPoint(2642, 2959, 0), "Pick a potato south east of Rantz.");
		getOnion = new ObjectStep(this, ObjectID.ONION, new WorldPoint(2583, 2965, 0), "Pick an onion from west of Rantz.");
		getTomato = new DetailedQuestStep(this, new WorldPoint(2584, 2966, 0), "Get a tomato from west of Rantz.", tomato);
		getEqua = new DetailedQuestStep(this, new WorldPoint(2648, 2963, 0), "Get some equa leaves from south east of Rantz.", equa);
		getDoogle = new DetailedQuestStep(this, new WorldPoint(2565, 2972, 0), "Get some doogle leaves from west of Rantz.", doogle);
		getCabbage = new ObjectStep(this, ObjectID.CABBAGE, new WorldPoint(2572, 2967, 0), "Get some cabbage from west of Rantz.");
		getIngredients = new DetailedQuestStep(this, "Collect the ingredients Rantz and his children want for the chompy.");
		getIngredients.addSubSteps(getPotato, getOnion, getTomato, getEqua, getDoogle, getCabbage);
		cookChompy = new ObjectStep(this, NullObjectID.NULL_6895, new WorldPoint(2631, 2990, 0), "Cook the chompy on Rantz's spit-roast.", chompyHighlighted);
		cookChompy.addIcon(ItemID.RAW_CHOMPY);

		giveRantzSeasonedChompy = new NpcStep(this, NpcID.RANTZ, new WorldPoint(2631, 2982, 0), "Bring Rantz the seasoned chompy to finish the quest.", seasonedChompyHighlighted);
		giveRantzSeasonedChompy.addIcon(ItemID.SEASONED_CHOMPY);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(axe, feathers, knife, hammer, chisel, wolfBones4);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Chompy");
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
				new ExperienceReward(Skill.FLETCHING, 262),
				new ExperienceReward(Skill.COOKING, 1470),
				new ExperienceReward(Skill.RANGED, 735));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("An Ogre Bow", ItemID.OGRE_BOW, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("The ability to fletch Ogre Arrows."),
				new UnlockReward("The ability to hunt and cook Chompy Birds."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Getting Rantz Arrows",
			Arrays.asList(talkToRantz, getLogs, makeShafts, useFeathersOnShafts, useChiselOnBones,
				useTipsOnShafts, useArrowsOnRantz), axe, knife, feathers, chisel, hammer, wolfBones4));
		allSteps.add(new PanelDetails("Making Bloated Toads",
			Arrays.asList(askRantzQuestions, enterCave, getBellow, leaveCave, fillBellows, inflateToad, talkToRantzWithToad)));
		allSteps.add(new PanelDetails("Hunting Chompy",
			Arrays.asList(dropToad, waitForChompy, talkToRantzForBow, placeAnotherToad, killChompy, talkToRantzWithChompy)));
		allSteps.add(new PanelDetails("Cooking Chompy",
			Arrays.asList(enterCaveAgain, talkToBugs, talkToFycie, leaveCaveAgain, getIngredients, cookChompy,
				giveRantzSeasonedChompy)));

		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new SkillRequirement(Skill.RANGED, 30));
		req.add(new SkillRequirement(Skill.COOKING, 30, true));
		req.add(new SkillRequirement(Skill.FLETCHING, 5, true));
		return req;
	}
}
