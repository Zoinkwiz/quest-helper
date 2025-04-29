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
package com.questhelper.helpers.quests.whatliesbelow;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.*;

public class WhatLiesBelow extends BasicQuestHelper
{
	//Items Required
	ItemRequirement intel5, bowl, chaosRunes15, emptyFolder, usedFolder, fullFolder, chaosTalismanOrAbyss, folder, wand,
		wandHighlight, beaconRing, letterToSurok, infusedWand, suroksLetter;

	//Items Recommended
	ItemRequirement chronicle;

	Requirement inChaosAltar, inBattle;

	QuestStep talkToRat, bringFolderToRat, talkToRatAfterFolder, talkToSurok, talkToSurokNoLetter, enterChaosAltar, useWandOnAltar, bringWandToSurok,
		talkToRatAfterSurok, talkToZaff, talkToSurokToFight, fightRoald, talkToRatToFinish, talkToRatAfterSurokNoLetter;

	NpcStep killOutlaws;

	//Zones
	Zone chaosAltar;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToRat);
		// Occurs if starting quest with a full inv
		steps.put(5, talkToRat);

		ConditionalStep getIntel = new ConditionalStep(this, killOutlaws);
		getIntel.addStep(fullFolder, bringFolderToRat);

		steps.put(10, getIntel);

		steps.put(20, talkToRatAfterFolder);

		steps.put(25, talkToSurok);

		steps.put(30, talkToSurokNoLetter);
		steps.put(40, talkToSurokNoLetter);
		steps.put(45, talkToSurokNoLetter);
		steps.put(46, talkToSurokNoLetter);

		ConditionalStep chargeWand = new ConditionalStep(this, enterChaosAltar);
		chargeWand.addStep(inChaosAltar, useWandOnAltar);

		steps.put(50, chargeWand);

		steps.put(55, bringWandToSurok);

		steps.put(60, talkToRatAfterSurok);
		steps.put(61, talkToRatAfterSurok);

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

	@Override
	protected void setupRequirements()
	{
		intel5 = new ItemRequirement("Rat's paper", ItemID.SUROK_PAPER, 5);
		intel5.setHighlightInInventory(true);
		emptyFolder = new ItemRequirement("An empty folder", ItemID.SUROK_RAT_EMPTYFOLDER);
		// Varbit 3525 0,1,2,3,4,5 is fullness of folder
		usedFolder = new ItemRequirement("Used folder", ItemID.SUROK_RAT_HALFFOLDER);
		fullFolder = new ItemRequirement("Full folder", ItemID.SUROK_RAT_FULLFOLDER);

		folder = new ItemRequirement("Folder", ItemID.SUROK_RAT_EMPTYFOLDER);
		folder.addAlternates(ItemID.SUROK_RAT_HALFFOLDER, ItemID.SUROK_RAT_FULLFOLDER);
		folder.setTooltip("You can get another empty folder from Rat");
		folder.setHighlightInInventory(true);

		bowl = new ItemRequirement("Bowl", ItemID.BOWL_EMPTY);
		chaosRunes15 = new ItemRequirement("Chaos runes", ItemID.CHAOSRUNE, 15);
		wand = new ItemRequirement("Wand", ItemID.SUROK_METALWAND);
		wandHighlight = new ItemRequirement("Wand", ItemID.SUROK_METALWAND);
		wandHighlight.setHighlightInInventory(true);

		infusedWand = new ItemRequirement("Infused wand", ItemID.SUROK_GLOWINGWAND);
		infusedWand.setTooltip("You can make another by getting a wand from Surok, and using it on the chaos altar with 15 chaos runes");
		chaosTalismanOrAbyss = new ItemRequirement("Chaos Talisman or access to the Abyss", ItemID.CHAOS_TALISMAN).isNotConsumed();
		chaosTalismanOrAbyss.addAlternates(ItemID.TIARA_CHAOS, ItemID.CATALYTIC_TALISMAN, ItemID.TIARA_CATALYTIC);
		chaosTalismanOrAbyss.setTooltip("If using the Dagon'hai shortcut, catalytic talisman/tiara won't work");

		beaconRing = new ItemRequirement("Beacon ring", ItemID.SUROK_RING);
		beaconRing.setTooltip("You can get another from Zaff");

		letterToSurok = new ItemRequirement("Letter to Surok", ItemID.SUROK_LETTER1);
		letterToSurok.setTooltip("You can get another from Rat");

		suroksLetter = new ItemRequirement("Surok's letter", ItemID.SUROK_LETTER2);
		suroksLetter.setTooltip("You can get another from Surok");

		chronicle = new ItemRequirement("Chronicle for teleports to south of Varrock", ItemID.CHRONICLE);
	}

	@Override
	protected void setupZones()
	{
		chaosAltar = new Zone(new WorldPoint(2245, 4823, 0), new WorldPoint(2299, 4860, 2));
	}

	public void setupConditions()
	{
		inChaosAltar = new ZoneRequirement(chaosAltar);
		inBattle = new VarbitRequirement(6719, 2);
	}

	public void setupSteps()
	{
		talkToRat = new NpcStep(this, NpcID.SUROK_RAT, new WorldPoint(3266, 3333, 0), "Talk to Rat Burgiss south of Varrock.");
		talkToRat.addDialogSteps("Yes.", "Shall I get them back for you?", "Of course! Tell me what you need me to do.");
		killOutlaws = new NpcStep(this, NpcID.SUROK_OUTLAW1, new WorldPoint(3118, 3472, 0),
			"Go to the Bandits west of the Grand Exchange and kill 5 for intel. Put the intel into the folder Rat gave you.", true, folder, intel5);
		killOutlaws.addAlternateNpcs(NpcID.SUROK_OUTLAW2, NpcID.SUROK_OUTLAW3, NpcID.SUROK_OUTLAW4, NpcID.SUROK_OUTLAW5, NpcID.SUROK_OUTLAW6, NpcID.SUROK_OUTLAW7, NpcID.SUROK_OUTLAW8, NpcID.SUROK_OUTLAW9, NpcID.SUROK_OUTLAW10);
		bringFolderToRat = new NpcStep(this, NpcID.SUROK_RAT, new WorldPoint(3266, 3333, 0), "Return to Rat Burgiss south of Varrock.", fullFolder);
		talkToRatAfterFolder = new NpcStep(this, NpcID.SUROK_RAT, new WorldPoint(3266, 3333, 0), "Return to Rat Burgiss south of Varrock.");
		bringFolderToRat.addSubSteps(talkToRatAfterFolder);

		talkToSurok = new NpcStep(this, NpcID.SUROK_SUROK, new WorldPoint(3211, 3493, 0), "Talk to Surok Magis in the Varrock Library.", letterToSurok);
		talkToSurokNoLetter = new NpcStep(this, NpcID.WGS_SUROK_TRANSITION, new WorldPoint(3211, 3493, 0), "Talk to Surok Magis in the Varrock Library.");
		talkToSurokNoLetter.addDialogSteps("Go on, then!", "Go on then!");

		talkToSurok.addSubSteps(talkToSurokNoLetter);

		enterChaosAltar = new DetailedQuestStep(this,
			"Travel to the chaos altar with the wand and 15 chaos runes. You can either enter with a chaos talisman, or use the abyss.", wand, chaosRunes15, chaosTalismanOrAbyss);
		useWandOnAltar = new ObjectStep(this, ObjectID.CHAOS_ALTAR, new WorldPoint(2271, 4842, 0), "Use the wand on the chaos altar.", wandHighlight, chaosRunes15);
		useWandOnAltar.addIcon(ItemID.SUROK_METALWAND);
		bringWandToSurok = new NpcStep(this, NpcID.SUROK_SUROK, new WorldPoint(3211, 3493, 0), "Return to Surok Magis in the Varrock Library with the wand and a bowl.", infusedWand, bowl);
		bringWandToSurok.addDialogStep("I have the things you wanted!");
		talkToRatAfterSurok = new NpcStep(this, NpcID.SUROK_RAT, new WorldPoint(3266, 3333, 0), "Return to Rat Burgiss south of Varrock.", suroksLetter);
		talkToRatAfterSurok.addDialogStep("Yes! I have a letter for you.");

		talkToRatAfterSurokNoLetter = new NpcStep(this, NpcID.SUROK_RAT, new WorldPoint(3266, 3333, 0), "Return to Rat Burgiss south of Varrock.");

		talkToZaff = new NpcStep(this, NpcID.ZAFF, new WorldPoint(3202, 3434, 0), "Talk to Zaff in the Varrock staff shop.");
		talkToZaff.addDialogStep("Rat Burgiss sent me!");
		talkToSurokToFight = new NpcStep(this, NpcID.SUROK_SUROK_TYPE2, new WorldPoint(3211, 3493, 0), "Prepare to fight King Roald (level 47), then go talk to Surok Magis in the Varrock Library.", beaconRing);
		talkToSurokToFight.addDialogStep("Bring it on!");
		fightRoald = new NpcStep(this, NpcID.SUROK_KING, new WorldPoint(3211, 3493, 0), "Fight King Roald. When he's at 1hp, right-click operate the beacon ring.", beaconRing);
		talkToRatToFinish = new NpcStep(this, NpcID.SUROK_RAT, new WorldPoint(3266, 3333, 0), "Return to Rat Burgiss south of Varrock to finish the quest.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(bowl);
		reqs.add(chaosRunes15);
		reqs.add(chaosTalismanOrAbyss);
		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(chronicle);
		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("5 outlaws (level 32)");
		reqs.add("King Roald (level 47)");
		return reqs;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.RUNE_MYSTERIES, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.MINING, 42, false));
		req.add(new SkillRequirement(Skill.RUNECRAFT, 35));
		return req;
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
				new ExperienceReward(Skill.RUNECRAFT, 8000),
				new ExperienceReward(Skill.DEFENCE, 2000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("The Beacon Ring", ItemID.SUROK_RING, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to a shortcut to the Chaos Altar"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Collections.singletonList(talkToRat)));
		allSteps.add(new PanelDetails("Help Rat", Arrays.asList(killOutlaws, bringFolderToRat)));
		allSteps.add(new PanelDetails("Help Surok", Arrays.asList(talkToSurok, enterChaosAltar, useWandOnAltar, bringWandToSurok), chaosRunes15, chaosTalismanOrAbyss, bowl));
		allSteps.add(new PanelDetails("Defeat Surok", Arrays.asList(talkToRatAfterSurok, talkToZaff, talkToSurokToFight, fightRoald, talkToRatToFinish)));
		return allSteps;
	}
}
