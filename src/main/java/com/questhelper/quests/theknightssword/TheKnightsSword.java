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
package com.questhelper.quests.theknightssword;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_KNIGHTS_SWORD
)
public class TheKnightsSword extends BasicQuestHelper
{
	ItemRequirement pickaxe, redberryPie, ironBars, bluriteOre, varrockTeleport, faladorTeleports, bluriteSword, portrait;

	ConditionForStep hasPortrait, hasBluriteOre, hasBluriteSword, inDungeon, inFaladorCastle1, inFaladorCastle2;

	QuestStep talkToSquire, talkToReldo, talkToThurgo, talkToThurgoAgain, talkToSquire2, goUpCastle1, goUpCastle2, searchCupboard, enterDungeon,
		mineBlurite, givePortraitToThurgo, bringThurgoOre, finishQuest;

	Zone dungeon, faladorCastle1, faladorCastle2;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToSquire);
		steps.put(1, talkToReldo);
		steps.put(2, talkToThurgo);
		steps.put(3, talkToThurgoAgain);
		steps.put(4, talkToSquire2);

		ConditionalStep getPortrait = new ConditionalStep(this, goUpCastle1);
		getPortrait.addStep(hasPortrait, givePortraitToThurgo);
		getPortrait.addStep(inFaladorCastle2, searchCupboard);
		getPortrait.addStep(inFaladorCastle1, goUpCastle2);

		steps.put(5, getPortrait);

		ConditionalStep returnSwordToSquire = new ConditionalStep(this, enterDungeon);
		returnSwordToSquire.addStep(hasBluriteSword, finishQuest);
		returnSwordToSquire.addStep(hasBluriteOre, bringThurgoOre);
		returnSwordToSquire.addStep(inDungeon, mineBlurite);

		steps.put(6, returnSwordToSquire);

		return steps;
	}

	public void setupItemRequirements()
	{
		redberryPie = new ItemRequirement("Redberry pie", ItemID.REDBERRY_PIE);
		ironBars = new ItemRequirement("Iron bar", ItemID.IRON_BAR, 2);
		bluriteOre = new ItemRequirement("Blurite ore", ItemID.BLURITE_ORE);
		bluriteSword = new ItemRequirement("Blurite sword", ItemID.BLURITE_SWORD);
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes());
		varrockTeleport = new ItemRequirement("A teleport to Varrock", ItemID.VARROCK_TELEPORT);
		faladorTeleports = new ItemRequirement("Teleports to Falador", ItemID.FALADOR_TELEPORT, 4);
		portrait = new ItemRequirement("Portrait", ItemID.PORTRAIT);
	}

	public void setupConditions()
	{
		hasBluriteOre = new ItemRequirementCondition(bluriteOre);
		hasBluriteSword = new ItemRequirementCondition(bluriteSword);
		hasPortrait = new ItemRequirementCondition(portrait);
		inDungeon = new ZoneCondition(dungeon);
		inFaladorCastle1 = new ZoneCondition(faladorCastle1);
		inFaladorCastle2 = new ZoneCondition(faladorCastle2);
	}

	public void setupZones()
	{
		dungeon = new Zone(new WorldPoint(2979, 9538, 0), new WorldPoint(3069, 9602, 0));
		faladorCastle1 = new Zone(new WorldPoint(2954, 3328, 1), new WorldPoint(2997, 3353, 1));
		faladorCastle2 = new Zone(new WorldPoint(2980, 3331, 2), new WorldPoint(2986, 3346, 2));
	}

	public void setupSteps()
	{
		talkToSquire = new NpcStep(this, NpcID.SQUIRE_4737, new WorldPoint(2978, 3341, 0), "Talk to the Squire in Falador Castle's courtyard.");
		talkToSquire.addDialogStep("And how is life as a squire?");
		talkToSquire.addDialogStep("I can make a new sword if you like...");
		talkToSquire.addDialogStep("So would these dwarves make another one?");
		talkToSquire.addDialogStep("Ok, I'll give it a go.");
		talkToReldo = new NpcStep(this, NpcID.RELDO_4243, new WorldPoint(3211, 3494, 0), "Talk to Reldo in Varrock Castle's library.");
		talkToReldo.addDialogStep("What do you know about the Imcando dwarves?");
		talkToThurgo = new NpcStep(this, NpcID.THURGO, new WorldPoint(3000, 3145, 0), "Talk to Thurgo south of Port Sarim and give him a Redberry pie.", redberryPie);
		talkToThurgo.addDialogStep("Would you like a redberry pie?");
		talkToThurgoAgain = new NpcStep(this, NpcID.THURGO, new WorldPoint(3000, 3145, 0), "Talk to Thurgo again.");
		talkToThurgoAgain.addDialogStep("Can you make a special sword for me?");
		talkToSquire2 = new NpcStep(this, NpcID.SQUIRE_4737, new WorldPoint(2978, 3341, 0), "Talk to the Squire in Falador Castle's courtyard.");
		goUpCastle1 = new ObjectStep(this, ObjectID.LADDER_24070, new WorldPoint(2994, 3341, 0), "Climb up the east ladder in Falador Castle.");
		goUpCastle2 = new ObjectStep(this, ObjectID.STAIRCASE_24077, new WorldPoint(2985, 3338, 1), "Go up the staircase west of the ladder on the 1st floor.");
		searchCupboard = new ObjectStep(this, ObjectID.CUPBOARD_2272, new WorldPoint(2985, 3336, 2), "Search the cupboard in the room south of the staircase. You'll need Sir Vyvin to be in the other room.");
		givePortraitToThurgo = new NpcStep(this, NpcID.THURGO, new WorldPoint(3000, 3145, 0), "Bring Thurgo the portrait.", bluriteOre, ironBars, portrait);
		givePortraitToThurgo.addDialogStep("About that sword...");
		enterDungeon = new ObjectStep(this, ObjectID.TRAPDOOR_1738, new WorldPoint(3008, 3150, 0), "Go down the ladder south of Port Sarim. Be prepared for ice giants and ice warriors to attack you.", pickaxe, ironBars, portrait);
		mineBlurite = new ObjectStep(this, ObjectID.ROCKS_11378, new WorldPoint(3049, 9566, 0), "Mine a blurite ore in the eastern cavern.", pickaxe);
		bringThurgoOre =  new NpcStep(this, NpcID.THURGO, new WorldPoint(3000, 3145, 0), "Return to Thurgo with a blurite ore, two iron bars and the portrait.", bluriteOre, ironBars, portrait);
		bringThurgoOre.addDialogStep("Can you make that replacement sword now?");
		finishQuest = new NpcStep(this, NpcID.SQUIRE_4737, new WorldPoint(2978, 3341, 0), "Return to the Squire with the sword to finish the quest.", bluriteSword);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(redberryPie);
		reqs.add(ironBars);
		reqs.add(pickaxe);
		return reqs;
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(varrockTeleport);
		reqs.add(faladorTeleports);
		return reqs;
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Able to survive attacks from Ice Warriors (level 57) and Ice Giants (level 53)");
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

    	allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Arrays.asList(talkToSquire, talkToReldo))));
    	allSteps.add(new PanelDetails("Finding an Imcando", new ArrayList<>(Arrays.asList(talkToThurgo, talkToThurgoAgain)), redberryPie));
		allSteps.add(new PanelDetails("Find the portrait", new ArrayList<>(Arrays.asList(talkToSquire2, goUpCastle1, goUpCastle2, searchCupboard, givePortraitToThurgo))));
		allSteps.add(new PanelDetails("Making the sword", new ArrayList<>(Arrays.asList(enterDungeon, mineBlurite, bringThurgoOre)), pickaxe, portrait, ironBars));
		allSteps.add(new PanelDetails("Return the sword", new ArrayList<>(Collections.singletonList(finishQuest))));
		return allSteps;
	}
}
