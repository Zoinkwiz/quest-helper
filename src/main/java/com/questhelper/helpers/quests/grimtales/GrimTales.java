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
package com.questhelper.helpers.quests.grimtales;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetModelRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.Operation;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.WidgetStep;
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

@QuestDescriptor(
	quest = QuestHelperQuest.GRIM_TALES
)
public class GrimTales extends BasicQuestHelper
{
	//Items Required
	ItemRequirement tarrominUnf2, tarrominUnf, dibber, can, axe, combatGear, griffinFeather, rupertsHelmet, miazrqasPendant, goldenGoblin, houseKey, ogleroot, shrinkPotion,
		shrinkPotionHighlight, tarrominUnfHighlight, oglerootHighlight, magicBeans, canHighlight, food;

	Requirement inHouse, inBasement, grimgnashAsleep, givenFeather, inTowerBase, inTowerUpstairs, talkedToDrainOnce, beardDropped, talkedToRupert, talkedToMiazrqa,
		inPianoWidget, pressed1, pressed2, pressed3, pressed4, pressed5, pressed6, pressed7, pressed8, unlockedPiano, searchedPiano, inMouseRoom1, inMouseRoom2, inMouseRoom3,
		inMouseRoom4, inMouseRoom5, inMouseRoom6, inWrongMouse1, inWrongMouse2, givenPendant, releasedRupert,
		plantedSeed, wateredSeed, onCloud, killedGlod, usedPotion, hasMiazrqasPendant;

	QuestStep talkToSylas, talkToGrimgnash, stealFeather, returnFeatherToSylas, climbWall, talkToDrainPipe, talkToDrainPipeAgain, climbBeard, talkToRupert, climbDownBeard, talkToMiazrqa,
		enterWitchsHouse, enterWitchBasement, playPiano, upperE, upperF, upperEAgain, upperD, upperC, lowerA, lowerE, lowerG, lowerAAgain, searchPiano, makePotions, leaveBasement, drinkPotion,
		enterWitchsHouseWithPotion, climb1, climb2, climb3, climb4, climb5, takePendant, givePendant, talkMizAfterPendant, talkToRupertAfterAmulet, leaveWrong1, leaveWrong2,
		giveHelmetToSylas, talkToSylasAfterGivingItems, plantBean, waterBean, climbBean, climbBeanForStatue, killGlod, pickUpGoldenGoblin, giveGoldenGoblinToSylas, usePotionOnBean, chopBean, talkToSylasFinish;

	//Zones
	Zone house, basement, towerBase, towerUpstairs, mouseRoom1, mouseRoom2, mouseRoom3, mouseRoom4, mouseRoom5, mouseRoom6, wrongMouse1, wrongMouse2, cloud;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToSylas);
		steps.put(1, talkToSylas);
		steps.put(2, talkToSylas);
		steps.put(3, talkToSylas);
		steps.put(4, talkToSylas);

		ConditionalStep getHelmet = new ConditionalStep(this, climbWall);
		getHelmet.addStep(rupertsHelmet, giveHelmetToSylas);
		getHelmet.addStep(releasedRupert, talkToRupertAfterAmulet);
		getHelmet.addStep(new Conditions(givenPendant), talkMizAfterPendant);
		getHelmet.addStep(new Conditions(hasMiazrqasPendant), givePendant);
		getHelmet.addStep(new Conditions(inMouseRoom6), takePendant);
		getHelmet.addStep(new Conditions(inMouseRoom5), climb5);
		getHelmet.addStep(new Conditions(inMouseRoom4), climb4);
		getHelmet.addStep(new Conditions(inMouseRoom3), climb3);
		getHelmet.addStep(new Conditions(inMouseRoom2), climb2);
		getHelmet.addStep(new Conditions(inMouseRoom1), climb1);
		getHelmet.addStep(new Conditions(inWrongMouse2), leaveWrong2);
		getHelmet.addStep(new Conditions(inWrongMouse1), leaveWrong1);
		getHelmet.addStep(new Conditions(inHouse, shrinkPotion), drinkPotion);
		getHelmet.addStep(new Conditions(inBasement, shrinkPotion), leaveBasement);
		getHelmet.addStep(new Conditions(shrinkPotion), enterWitchsHouseWithPotion);
		getHelmet.addStep(new Conditions(searchedPiano), makePotions);
		getHelmet.addStep(new Conditions(inBasement, unlockedPiano), searchPiano);
		getHelmet.addStep(new Conditions(inPianoWidget, talkedToMiazrqa, pressed8), lowerAAgain);
		getHelmet.addStep(new Conditions(inPianoWidget, talkedToMiazrqa, pressed7), lowerG);
		getHelmet.addStep(new Conditions(inPianoWidget, talkedToMiazrqa, pressed6), lowerE);
		getHelmet.addStep(new Conditions(inPianoWidget, talkedToMiazrqa, pressed5), lowerA);
		getHelmet.addStep(new Conditions(inPianoWidget, talkedToMiazrqa, pressed4), upperC);
		getHelmet.addStep(new Conditions(inPianoWidget, talkedToMiazrqa, pressed3), upperD);
		getHelmet.addStep(new Conditions(inPianoWidget, talkedToMiazrqa, pressed2), upperEAgain);

		getHelmet.addStep(new Conditions(inPianoWidget, talkedToMiazrqa, pressed1), upperF);
		getHelmet.addStep(new Conditions(inPianoWidget, talkedToMiazrqa), upperE);
		getHelmet.addStep(new Conditions(inBasement, talkedToMiazrqa), playPiano);
		getHelmet.addStep(new Conditions(inHouse, talkedToMiazrqa), enterWitchBasement);
		getHelmet.addStep(new Conditions(talkedToMiazrqa), enterWitchsHouse);
		getHelmet.addStep(new Conditions(inTowerUpstairs, talkedToRupert), climbDownBeard);
		getHelmet.addStep(new Conditions(talkedToRupert), talkToMiazrqa);
		getHelmet.addStep(new Conditions(inTowerUpstairs), talkToRupert);
		getHelmet.addStep(new Conditions(inTowerBase, beardDropped), climbBeard);
		getHelmet.addStep(new Conditions(talkedToDrainOnce, inTowerBase), talkToDrainPipeAgain);
		getHelmet.addStep(new Conditions(inTowerBase), talkToDrainPipe);

		ConditionalStep getFeatherAndHelmet = new ConditionalStep(this, talkToGrimgnash);
		getFeatherAndHelmet.addStep(givenFeather, getHelmet);
		getFeatherAndHelmet.addStep(griffinFeather, returnFeatherToSylas);
		getFeatherAndHelmet.addStep(grimgnashAsleep, stealFeather);

		steps.put(10, getFeatherAndHelmet);

		steps.put(12, talkToSylasAfterGivingItems);
		steps.put(15, talkToSylasAfterGivingItems);
		steps.put(16, talkToSylasAfterGivingItems);
		steps.put(17, talkToSylasAfterGivingItems);
		steps.put(19, talkToSylasAfterGivingItems);

		ConditionalStep sortBean = new ConditionalStep(this, plantBean);
		sortBean.addStep(plantedSeed, waterBean);

		steps.put(20, sortBean);

		ConditionalStep climbAndKillGlod = new ConditionalStep(this, climbBean);
		climbAndKillGlod.addStep(goldenGoblin, giveGoldenGoblinToSylas);
		climbAndKillGlod.addStep(new Conditions(onCloud, killedGlod), pickUpGoldenGoblin);
		climbAndKillGlod.addStep(killedGlod, climbBeanForStatue);
		climbAndKillGlod.addStep(onCloud, killGlod);

		steps.put(30, climbAndKillGlod);

		ConditionalStep finish = new ConditionalStep(this, usePotionOnBean);
		finish.addStep(usedPotion, chopBean);

		steps.put(40, finish);

		steps.put(50, talkToSylasFinish);
		return steps;
	}

	@Override
	public void setupRequirements()
	{
		tarrominUnf2 = new ItemRequirement("Tarromin potion (unf)", ItemID.TARROMIN_POTION_UNF, 2);
		tarrominUnf = new ItemRequirement("Tarromin potion (unf)", ItemID.TARROMIN_POTION_UNF);
		tarrominUnfHighlight = new ItemRequirement("Tarromin potion (unf)", ItemID.TARROMIN_POTION_UNF);
		tarrominUnfHighlight.setHighlightInInventory(true);

		dibber = new ItemRequirement("Seed dibber", ItemID.SEED_DIBBER).isNotConsumed();
		can = new ItemRequirement("Watering can with at least 1 use", ItemCollections.WATERING_CANS).isNotConsumed();
		can.setTooltip("Gricollers' can is also valid. ");
		axe = new ItemRequirement("Any axe", ItemCollections.AXES).isNotConsumed();
		combatGear = new ItemRequirement("Combat gear and food", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);

		griffinFeather = new ItemRequirement("Griffin feather", ItemID.GRIFFIN_FEATHER);
		rupertsHelmet = new ItemRequirement("Rupert's helmet", ItemID.RUPERTS_HELMET);
		miazrqasPendant = new ItemRequirement("Miazrqa's pendant", ItemID.MIAZRQAS_PENDANT);
		goldenGoblin = new ItemRequirement("Golden goblin", ItemID.GOLDEN_GOBLIN);
		houseKey = new ItemRequirement("Door key", ItemID.DOOR_KEY);
		houseKey.setTooltip("You can get another from the pot outside the Witch's House");
		ogleroot = new ItemRequirement("Shrunk ogleroot", ItemID.SHRUNK_OGLEROOT);
		ogleroot.setTooltip("You will need to get more by fighting Experiment No.2 in the sewer outside the Witch's House");

		oglerootHighlight = new ItemRequirement("Shrunk ogleroot", ItemID.SHRUNK_OGLEROOT);
		oglerootHighlight.setTooltip("You will need to get more by fighting Experiment No.2 in the sewer outside the Witch's House");
		oglerootHighlight.setHighlightInInventory(true);
		shrinkPotion = new ItemRequirement("Shrink-me-quick", ItemID.SHRINKMEQUICK);
		shrinkPotionHighlight = new ItemRequirement("Shrink-me-quick", ItemID.SHRINKMEQUICK);
		shrinkPotionHighlight.setHighlightInInventory(true);

		magicBeans = new ItemRequirement("Magic beans", ItemID.MAGIC_BEANS);
		magicBeans.setTooltip("You can get more from Sylas in Taverley");
		magicBeans.setHighlightInInventory(true);
		canHighlight = new ItemRequirement("Watering can with at least 1 use", ItemCollections.WATERING_CANS);
	}

	public void loadZones()
	{
		house = new Zone(new WorldPoint(2901, 3466, 0), new WorldPoint(2907, 3476, 0));
		basement = new Zone(new WorldPoint(2897, 9870, 0), new WorldPoint(2909, 9878, 0));
		towerBase = new Zone(new WorldPoint(2966, 3460, 0), new WorldPoint(2970, 3464, 0));
		towerUpstairs = new Zone(new WorldPoint(2966, 3465, 2), new WorldPoint(2972, 3473, 2));
		mouseRoom1 = new Zone(new WorldPoint(2274, 5521, 0), new WorldPoint(2286, 5557, 0));
		mouseRoom2 = new Zone(new WorldPoint(2262, 5514, 1), new WorldPoint(2284, 5556, 1));
		mouseRoom3 = new Zone(new WorldPoint(2263, 5514, 2), new WorldPoint(2277, 5520, 2));
		mouseRoom4 = new Zone(new WorldPoint(2263, 5513, 3), new WorldPoint(2284, 5532, 3));
		mouseRoom5 = new Zone(new WorldPoint(2276, 5524, 2), new WorldPoint(2283, 5545, 2));

		mouseRoom6 = new Zone(new WorldPoint(2276, 5539, 3), new WorldPoint(2284, 5556, 3));

		wrongMouse1 = new Zone(new WorldPoint(2276, 5549, 2), new WorldPoint(2285, 5560, 2));
		wrongMouse2 = new Zone(new WorldPoint(2263, 5516, 0), new WorldPoint(2276, 5519, 0));
		// Grim sleeps, 3693, 30

		cloud = new Zone(new WorldPoint(2130, 5522, 3), new WorldPoint(2156, 5547, 3));
	}

	public void setupConditions()
	{
		inHouse = new ZoneRequirement(house);
		inBasement = new ZoneRequirement(basement);
		inTowerBase = new ZoneRequirement(towerBase);
		inTowerUpstairs = new ZoneRequirement(towerUpstairs);

		grimgnashAsleep = new VarbitRequirement(3717, 1);
		givenFeather = new VarbitRequirement(3719, 1);

		talkedToDrainOnce = new VarbitRequirement(3694, 5, Operation.GREATER_EQUAL);
		beardDropped = new VarbitRequirement(3694, 10, Operation.GREATER_EQUAL);
		talkedToRupert = new VarbitRequirement(3694, 15, Operation.GREATER_EQUAL);
		talkedToMiazrqa = new VarbitRequirement(3694, 20, Operation.GREATER_EQUAL);
		inPianoWidget = new WidgetModelRequirement(535, 1, 25890);

		pressed1 = new VarbitRequirement(3697, 1);
		pressed2 = new VarbitRequirement(3697, 2);
		pressed3 = new VarbitRequirement(3697, 3);
		pressed4 = new VarbitRequirement(3697, 4);
		pressed5 = new VarbitRequirement(3697, 5);
		pressed6 = new VarbitRequirement(3697, 6);
		pressed7 = new VarbitRequirement(3697, 7);
		pressed8 = new VarbitRequirement(3697, 8);

		unlockedPiano = new VarbitRequirement(3698, 1);
		searchedPiano = new VarbitRequirement(3716, 1);

		inMouseRoom1 = new ZoneRequirement(mouseRoom1);
		inMouseRoom2 = new ZoneRequirement(mouseRoom2);
		inMouseRoom3 = new ZoneRequirement(mouseRoom3);
		inMouseRoom4 = new ZoneRequirement(mouseRoom4);
		inMouseRoom5 = new ZoneRequirement(mouseRoom5);
		inMouseRoom6 = new ZoneRequirement(mouseRoom6);

		inWrongMouse1 = new ZoneRequirement(wrongMouse1);
		inWrongMouse2 = new ZoneRequirement(wrongMouse2);

		hasMiazrqasPendant = new VarbitRequirement(3721, 1);

		givenPendant = new VarbitRequirement(3694, 25);

		releasedRupert = new VarbitRequirement(3701, 1);

		plantedSeed = new VarbitRequirement(3714, 1);
		wateredSeed = new VarbitRequirement(3714, 2);
		onCloud = new ZoneRequirement(cloud);

		killedGlod = new VarbitRequirement(3715, 1);

		usedPotion = new VarbitRequirement(3714, 3);
	}

	public void setupSteps()
	{
		talkToSylas = new NpcStep(this, NpcID.SYLAS, new WorldPoint(2892, 3454, 0), "Talk to Sylas in Taverley.");
		talkToSylas.addDialogStep("Yes");
		talkToGrimgnash = new NpcStep(this, NpcID.GRIMGNASH, new WorldPoint(2862, 3511, 0), "Talk to Grimgnash in the north east of White Wolf Mountain.");
		talkToGrimgnash.addDialogSteps("I heard you were a great and mighty Griffin!", "There once was a graveyard filled with undead.", "There lived a skeleton named Skullrot.",
			"Skullrot was insane!", "Skullrot hungrily grabbed the gnome's hair.", "Started to strangle the poor gnome.", "He saw some bones lying in the corner.");
		stealFeather = new ObjectStep(this, ObjectID.FEATHERS, new WorldPoint(2864, 3510, 0), "Steal a feather from Grimgnash.");
		returnFeatherToSylas = new NpcStep(this, NpcID.SYLAS, new WorldPoint(2892, 3454, 0), "Bring Sylas in Taverley a griffin feather.", griffinFeather);
		climbWall = new ObjectStep(this, ObjectID.CRUMBLING_WALL_24749, new WorldPoint(2971, 3462, 0), "Climb over the crumbling wall of the tower south east of Goblin Village.");
		climbBeard = new ObjectStep(this, NullObjectID.NULL_24776, new WorldPoint(2968, 3464, 0), "Climb the beard.");
		talkToDrainPipe = new ObjectStep(this, ObjectID.DRAIN_PIPE, new WorldPoint(2966, 3465, 0), "Talk to the drain pipe.");
		talkToDrainPipeAgain = new ObjectStep(this, ObjectID.DRAIN_PIPE, new WorldPoint(2966, 3465, 0), "Talk to the drain pipe again.");
		talkToDrainPipeAgain.addDialogSteps("I could try and climb up.", "Is there anything up there that can help?");
		talkToRupert = new ObjectStep(this, NullObjectID.NULL_24774, new WorldPoint(2968, 3467, 2), "Talk to Rupert the beard.");
		climbDownBeard = new ObjectStep(this, NullObjectID.NULL_24774, new WorldPoint(2968, 3467, 2), "Climb down the beard.");
		talkToMiazrqa = new NpcStep(this, NpcID.MIAZRQA, new WorldPoint(2968, 3473, 0), "Talk to Miazrqa.");
		talkToMiazrqa.addDialogSteps("I see there is an embarrassed-looking dwarf...", "Your second-cousin, twice removed?", "I need a key for the house.", "I should be off, I think.");

		enterWitchsHouse = new ObjectStep(this, ObjectID.DOOR_2861, new WorldPoint(2900, 3473, 0), "Enter the witch's house.", houseKey);
		enterWitchBasement = new ObjectStep(this, ObjectID.LADDER_24718, new WorldPoint(2907, 3476, 0), "Go down the ladder to the basement.");

		playPiano = new ObjectStep(this, NullObjectID.NULL_24881, new WorldPoint(2908, 9870, 0), "Play the piano. Press upper E-F-E-D-C, then lower A-E-G-A.");

		upperE = new WidgetStep(this, "Press upper E.", 535, 77);
		upperF = new WidgetStep(this, "Press upper F.", 535, 78);
		upperEAgain = new WidgetStep(this, "Press upper E again.", 535, 77);
		upperD = new WidgetStep(this, "Press upper D.", 535, 76);
		upperC = new WidgetStep(this, "Press upper C.", 535, 75);
		lowerA = new WidgetStep(this, "Press lower A.", 535, 73);
		lowerE = new WidgetStep(this, "Press lower E.", 535, 70);
		lowerG = new WidgetStep(this, "Press lower G.", 535, 72);
		lowerAAgain = new WidgetStep(this, "Presslower A.", 535, 73);

		playPiano.addSubSteps(upperE, upperF, upperEAgain, upperD, upperC, lowerA, lowerE, lowerG, lowerAAgain);

		searchPiano = new ObjectStep(this, NullObjectID.NULL_24881, new WorldPoint(2908, 9870, 0), "Right-click search the piano.");

		makePotions = new DetailedQuestStep(this, "Add the shrunk ogleroot to both your tarromin potion (unf). You'll need the second potion for later in the quest.", tarrominUnfHighlight, oglerootHighlight);

		leaveBasement = new ObjectStep(this, ObjectID.LADDER_24717, new WorldPoint(2907, 9876, 0), "Climb back up the ladder.");
		enterWitchsHouseWithPotion = new ObjectStep(this, ObjectID.DOOR_2861, new WorldPoint(2900, 3473, 0), "Return to the witch's house with your shrinking potion.", houseKey, shrinkPotion);
		drinkPotion = new DetailedQuestStep(this, new WorldPoint(2903, 3466, 0), "Stand in the south room and drink the shrink-me-quick potion.", shrinkPotionHighlight);
		climb1 = new ObjectStep(this, ObjectID.NAILS, new WorldPoint(2282, 5543, 0), "Climb the nails in the north east of the room");
		climb2 = new ObjectStep(this, ObjectID.NAILS, new WorldPoint(2268, 5520, 1), "Climb up the nails in the south west of the room.");
		climb3 = new ObjectStep(this, ObjectID.NAILS, new WorldPoint(2270, 5515, 2), "Climb the nails in the south of the room.");
		climb4 = new ObjectStep(this, ObjectID.NAILS_24796, new WorldPoint(2283, 5530, 3), "Climb down the nails in the north east of the room.");
		climb5 = new ObjectStep(this, ObjectID.NAILS, new WorldPoint(2284, 5542, 2), "Climb down the nails in the north east of the room.");
		takePendant = new ObjectStep(this, NullObjectID.NULL_24886, new WorldPoint(2279, 5555, 3), "Take the pendant and return to Miazrqa.");
		leaveWrong1 = new ObjectStep(this, ObjectID.NAILS_24796, new WorldPoint(2277, 5551, 2), "Go back down the nails.");
		leaveWrong2 = new ObjectStep(this, ObjectID.NAILS, new WorldPoint(2268, 5515, 0), "Go back up the nails.");

		givePendant = new NpcStep(this, NpcID.MIAZRQA, new WorldPoint(2968, 3473, 0), "Return the pendant to Miazrqa.", miazrqasPendant);
		talkMizAfterPendant = new NpcStep(this, NpcID.MIAZRQA, new WorldPoint(2968, 3473, 0), "Talk to Miazrqa.");

		talkToRupertAfterAmulet = new NpcStep(this, NpcID.RUPERT_THE_BEARD, new WorldPoint(2968, 3475, 0), "Talk to Rupert the beard to get his helmet.");

		giveHelmetToSylas = new NpcStep(this, NpcID.SYLAS, new WorldPoint(2892, 3454, 0), "Bring Sylas in Taverley Rupert's helmet.", rupertsHelmet);
		talkToSylasAfterGivingItems = new NpcStep(this, NpcID.SYLAS, new WorldPoint(2892, 3454, 0), "Talk to Sylas in Taverley.");

		plantBean = new ObjectStep(this, NullObjectID.NULL_24884, new WorldPoint(2922, 3425, 0), "Plant the magic beans in the earth mound in south east Taverley", magicBeans, dibber, can);
		plantBean.addIcon(ItemID.MAGIC_BEANS);

		waterBean = new ObjectStep(this, NullObjectID.NULL_24884, new WorldPoint(2922, 3425, 0), "Use your watering can on the magic beans you just planted in the earth mound", canHighlight);
		waterBean.addIcon(ItemID.WATERING_CAN);

		climbBean = new ObjectStep(this, NullObjectID.NULL_24884, new WorldPoint(2922, 3425, 0), "Climb the bean stalk, ready to fight Glod.", combatGear);
		climbBeanForStatue = new ObjectStep(this, NullObjectID.NULL_24884, new WorldPoint(2922, 3425, 0), "Climb the bean stalk for another golden goblin.");

		killGlod = new NpcStep(this, NpcID.GLOD, "Kill Glod.");

		pickUpGoldenGoblin = new ItemStep(this, "Pick up the golden goblin.", goldenGoblin);

		giveGoldenGoblinToSylas = new NpcStep(this, NpcID.SYLAS, new WorldPoint(2892, 3454, 0), "Bring the golden goblin to Sylas in Taverley.", goldenGoblin);

		usePotionOnBean = new ObjectStep(this, NullObjectID.NULL_24884, new WorldPoint(2922, 3425, 0), "Use a shrink potion on the bean stalk.", shrinkPotionHighlight, axe);
		usePotionOnBean.addIcon(ItemID.SHRINKMEQUICK);
		chopBean = new ObjectStep(this, NullObjectID.NULL_24884, new WorldPoint(2922, 3425, 0), "Chop down the bean stalk.", axe);
		talkToSylasFinish = new NpcStep(this, NpcID.SYLAS, new WorldPoint(2892, 3454, 0), "Talk to Sylas in Taverley.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(tarrominUnf2, dibber, can, axe, combatGear);
	}


	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Glod (level 138)");
		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.FARMING, 10000),
				new ExperienceReward(Skill.HERBLORE, 15000),
				new ExperienceReward(Skill.HITPOINTS, 5000),
				new ExperienceReward(Skill.WOODCUTTING, 60000),
				new ExperienceReward(Skill.AGILITY, 25000),
				new ExperienceReward(Skill.THIEVING, 25000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("A Dwarven Helmet", ItemID.DWARVEN_HELMET, 1));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Collections.singletonList(talkToSylas)));
		allSteps.add(new PanelDetails("Griffin feather",
			Arrays.asList(talkToGrimgnash, stealFeather, returnFeatherToSylas)));
		allSteps.add(new PanelDetails("Rupert's helmet",
			Arrays.asList(climbWall, talkToDrainPipe, talkToDrainPipeAgain, climbBeard, talkToRupert, climbDownBeard,
				talkToMiazrqa, enterWitchsHouse, enterWitchBasement, playPiano, searchPiano, makePotions, leaveBasement, drinkPotion,
				climb1, climb2, climb3, climb4, climb5, takePendant, givePendant, talkToRupertAfterAmulet),
			tarrominUnf2, houseKey, food));
		allSteps.add(new PanelDetails("Golden goblin",
			Arrays.asList(giveHelmetToSylas, plantBean, waterBean, climbBean, killGlod, pickUpGoldenGoblin,
				giveGoldenGoblinToSylas, usePotionOnBean, chopBean, talkToSylasFinish), combatGear, food, dibber, can,
			axe, shrinkPotion));
		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.WITCHS_HOUSE, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.FARMING, 45, true));
		req.add(new SkillRequirement(Skill.HERBLORE, 52, true));
		req.add(new SkillRequirement(Skill.THIEVING, 58, true));
		req.add(new SkillRequirement(Skill.AGILITY, 59, true));
		req.add(new SkillRequirement(Skill.WOODCUTTING, 71, true));
		return req;
	}
}
