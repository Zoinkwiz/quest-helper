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
package com.questhelper.quests.whatliesbelow;

import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;

@QuestDescriptor(
	quest = QuestHelperQuest.WHAT_LIES_BELOW
)
public class WhatLiesBelow extends BasicQuestHelper
{
	ItemRequirement intel5, bowl, chaosRunes15, emptyFolder, usedFolder, fullFolder, chaosTalismanOrAbyss, folder, wand,
		wandHighlight, beaconRing, letterToSurok, infusedWand, suroksLetter, chronicle;

	ConditionForStep inChaosAltar, hasFullFolder, inBattle;

	QuestStep talkToRat, bringFolderToRat, talkToRatAfterFolder, talkToSurok, talkToSurokNoLetter, enterChaosAltar, useWandOnAltar, bringWandToSurok,
		talkToRatAfterSurok, talkToZaff, talkToSurokToFight, fightRoald, talkToRatToFinish, talkToRatAfterSurokNoLetter;

	NpcStep killOutlaws;

	Zone chaosAltar;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToRat);

		ConditionalStep getIntel = new ConditionalStep(this, killOutlaws);
		getIntel.addStep(hasFullFolder, bringFolderToRat);

		steps.put(10, getIntel);

		steps.put(20, talkToRatAfterFolder);

		steps.put(25, talkToSurok);
		steps.put(30, talkToSurok);

		steps.put(40, talkToSurok);
		steps.put(45, talkToSurok);
		steps.put(46, talkToSurok);

		ConditionalStep chargeWand = new ConditionalStep(this, enterChaosAltar);
		chargeWand.addStep(inChaosAltar, useWandOnAltar);

		steps.put(50, chargeWand);

		steps.put(55, bringWandToSurok);

		steps.put(60, talkToRatAfterSurok);

		steps.put(70, talkToRatAfterSurokNoLetter);
		steps.put(71, talkToRatAfterSurokNoLetter);
		steps.put(72, talkToRatAfterSurokNoLetter);

		steps.put(80, talkToZaff);
		steps.put(81, talkToZaff);
		ConditionalStep defeatSurok = new ConditionalStep(this, talkToSurokToFight);
		defeatSurok.addStep(inBattle, fightRoald);

		steps.put(110, defeatSurok);
		steps.put(115, defeatSurok);
		steps.put(120, defeatSurok);
		steps.put(140, talkToRatToFinish);

		return steps;
	}

	public void setupItemRequirements()
	{
		intel5 = new ItemRequirement("Rat's paper", ItemID.RATS_PAPER, 5);
		intel5.setHighlightInInventory(true);
		emptyFolder = new ItemRequirement("An empty folder", ItemID.AN_EMPTY_FOLDER);
		// Varbit 3525 0,1,2,3,4,5 is fullness of folder
		usedFolder = new ItemRequirement("Used folder", ItemID.USED_FOLDER);
		fullFolder = new ItemRequirement("Full folder", ItemID.FULL_FOLDER);

		folder = new ItemRequirement("Folder", ItemID.AN_EMPTY_FOLDER);
		folder.addAlternates(ItemID.USED_FOLDER, ItemID.FULL_FOLDER);
		folder.setTip("You can get another empty folder from Rat");
		folder.setHighlightInInventory(true);

		bowl = new ItemRequirement("Bowl", ItemID.BOWL);
		chaosRunes15 = new ItemRequirement("Chaos runes", ItemID.CHAOS_RUNE);
		wand = new ItemRequirement("Wand", ItemID.WAND);
		wandHighlight = new ItemRequirement("Wand", ItemID.WAND);
		wandHighlight.setHighlightInInventory(true);

		infusedWand = new ItemRequirement("Infused wand", ItemID.INFUSED_WAND);
		infusedWand.setTip("You can make another by getting a wand from Surok, and using it on the chaos altar with 15 chaos runes");
		chaosTalismanOrAbyss = new ItemRequirement("Chaos Talisman or access to the Abyss", ItemID.CHAOS_TALISMAN);

		beaconRing = new ItemRequirement("Beacon ring", ItemID.BEACON_RING);
		beaconRing.setTip("You can get another from Zaff");

		letterToSurok = new ItemRequirement("Letter to Surok", ItemID.LETTER_TO_SUROK);
		letterToSurok.setTip("You can get another from Rat");

		suroksLetter = new ItemRequirement("Surok's letter", ItemID.SUROKS_LETTER);
		suroksLetter.setTip("You can get another from Surok");

		chronicle = new ItemRequirement("Chronicle for teleports to south of Varrock", ItemID.CHRONICLE);
	}

	public void loadZones()
	{
		chaosAltar = new Zone(new WorldPoint(2245, 4823, 0), new WorldPoint(2299, 4860, 2));
	}

	public void setupConditions()
	{
		hasFullFolder = new ItemRequirementCondition(fullFolder);
		inChaosAltar = new ZoneCondition(chaosAltar);
		inBattle = new VarbitCondition(6719, 2);
	}

	public void setupSteps()
	{
		talkToRat = new NpcStep(this, NpcID.RAT_BURGISS, new WorldPoint(3266, 3333, 0), "Talk to Rat Burgiss south of Varrock.");
		talkToRat.addDialogStep("Shall I get them back for you?");
		talkToRat.addDialogStep("Of course! Tell me what you need me to do.");
		killOutlaws = new NpcStep(this, NpcID.OUTLAW, new WorldPoint(3118, 3472, 0),
			"Go to the Bandits west of the Grand Exchange and kill 5 for intel. Put the intel into the folder Rat gave you.", true, folder, intel5);
		killOutlaws.addAlternateNpcs(NpcID.OUTLAW_4168, NpcID.OUTLAW_4169, NpcID.OUTLAW_4170, NpcID.OUTLAW_4171, NpcID.OUTLAW_4172, NpcID.OUTLAW_4173, NpcID.OUTLAW_4174, NpcID.OUTLAW_4175, NpcID.OUTLAW_4176);
		bringFolderToRat = new NpcStep(this, NpcID.RAT_BURGISS, new WorldPoint(3266, 3333, 0), "Return to Rat Burgiss south of Varrock.", fullFolder);
		talkToRatAfterFolder = new NpcStep(this, NpcID.RAT_BURGISS, new WorldPoint(3266, 3333, 0), "Return to Rat Burgiss south of Varrock.");
		bringFolderToRat.addSubSteps(talkToRatAfterFolder);

		talkToSurok = new NpcStep(this, NpcID.SUROK_MAGIS, new WorldPoint(3211, 3493, 0), "Talk to Surok Magis in the Varrock Library.", letterToSurok);
		talkToSurokNoLetter = new NpcStep(this, NpcID.SUROK_MAGIS, new WorldPoint(3211, 3493, 0), "Talk to Surok Magis in the Varrock Library.");
		talkToSurokNoLetter.addDialogStep("Go on, then!");

		talkToSurok.addSubSteps(talkToSurokNoLetter);

		enterChaosAltar = new DetailedQuestStep(this,
			"Travel to the chaos altar with the wand and 15 chaos runes. You can either enter with a chaos talisman, or use the abyss.", wand, chaosRunes15, chaosTalismanOrAbyss);
		useWandOnAltar = new ObjectStep(this, ObjectID.ALTAR_34769, new WorldPoint(2271, 4842, 0), "Use the wand on the chaos altar.", wandHighlight, chaosRunes15);
		useWandOnAltar.addIcon(ItemID.WAND);
		bringWandToSurok = new NpcStep(this, NpcID.SUROK_MAGIS, new WorldPoint(3211, 3493, 0), "Return to Surok Magis in the Varrock Library with the wand and a bowl.", infusedWand, bowl);
		bringWandToSurok.addDialogStep("I have the things you wanted!");
		talkToRatAfterSurok = new NpcStep(this, NpcID.RAT_BURGISS, new WorldPoint(3266, 3333, 0), "Return to Rat Burgiss south of Varrock.", suroksLetter);
		talkToRatAfterSurok.addDialogStep("Yes! I have a letter for you.");

		talkToRatAfterSurokNoLetter = new NpcStep(this, NpcID.RAT_BURGISS, new WorldPoint(3266, 3333, 0), "Return to Rat Burgiss south of Varrock.");

		talkToZaff = new NpcStep(this, NpcID.ZAFF, new WorldPoint(3202, 3434, 0), "Talk to Zaff in the Varrock staff shop.");
		talkToZaff.addDialogStep("Rat Burgiss sent me!");
		talkToSurokToFight =  new NpcStep(this, NpcID.SUROK_MAGIS_4160, new WorldPoint(3211, 3493, 0), "Prepare to fight King Roald (level 47), then go talk to Surok Magis in the Varrock Library.", beaconRing);
		talkToSurokToFight.addDialogStep("Bring it on!");
		fightRoald = new NpcStep(this, NpcID.KING_ROALD_4163, new WorldPoint(3211, 3493, 0), "Fight King Roald. When he's at 1hp, right-click operate the beacon ring.", beaconRing);
		talkToRatToFinish = new NpcStep(this, NpcID.RAT_BURGISS, new WorldPoint(3266, 3333, 0), "Return to Rat Burgiss south of Varrock to finish the quest.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(bowl);
		reqs.add(chaosRunes15);
		reqs.add(chaosTalismanOrAbyss);
		return reqs;
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(chronicle);
		return reqs;
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("5 outlaws (level 32)");
		reqs.add("King Roald (level 47)");
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Collections.singletonList(talkToRat))));
		allSteps.add(new PanelDetails("Help Rat", new ArrayList<>(Arrays.asList(killOutlaws, bringFolderToRat))));
		allSteps.add(new PanelDetails("Help Surok", new ArrayList<>(Arrays.asList(talkToSurok, enterChaosAltar, useWandOnAltar, bringWandToSurok)), chaosRunes15, chaosTalismanOrAbyss, bowl));
		allSteps.add(new PanelDetails("Defeat Surok", new ArrayList<>(Arrays.asList(talkToRatAfterSurok, talkToZaff, talkToSurokToFight, fightRoald, talkToRatToFinish))));
		return allSteps;
	}
}
