/*
 * Copyright (c) 2021, Zoinkwiz
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
package com.questhelper.quests.thegeneralsshadow;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_GENERALS_SHADOW
)
public class TheGeneralsShadow extends BasicQuestHelper
{
	// Required
	ItemRequirement ghostlyHood, ghostlyBody, ghostlyLegs, ghostlyGloves, ghostlyBoots, ghostlyCloak,
	ringOfVisibility, ghostspeak, combatGear, coins40, sinSeersNote, ghostlyRobes, serveredLeg;

	// Recommended
	ItemRequirement kharidTeleport, gnomeTeleport, rellekkaTeleport, karamjaTeleport;

	Requirement inventorySlot;

	Requirement inGoblinCave, inBouncerCave, inSinRoom, hasNote, givenNote, talkedToGnomeScout,
		talkedToKaramjaScout, talkedToShantyScout, talkedToFaladorScout;

	QuestStep talkToKhazard, goUpToSeer, talkToSeer, talkToKhazardAfterSeer, talkToKhazardAfterNote,
		talkToKaramjaScout, talkToGnomeScout, talkToFaladorScout, talkToShantyScout, talkToKhazardAfterScouts,
		enterCave, enterCrack, killBouncer;

	Zone goblinCave, bouncerCave, sinRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToKhazard);

		ConditionalStep goGetSin = new ConditionalStep(this, goUpToSeer);
		goGetSin.addStep(givenNote, talkToKhazardAfterNote);
		goGetSin.addStep(hasNote, talkToKhazardAfterSeer);
		goGetSin.addStep(inSinRoom, talkToSeer);
		steps.put(5, goGetSin);
		steps.put(10, talkToKhazardAfterNote);

		ConditionalStep goTalkToScouts = new ConditionalStep(this, talkToGnomeScout);
		goTalkToScouts.addStep(new Conditions(talkedToGnomeScout, talkedToFaladorScout, talkedToShantyScout),
			talkToKaramjaScout);
		goTalkToScouts.addStep(new Conditions(talkedToGnomeScout, talkedToFaladorScout), talkToShantyScout);
		goTalkToScouts.addStep(talkedToGnomeScout, talkToFaladorScout);
		steps.put(15, goTalkToScouts);

		steps.put(20, talkToKhazardAfterScouts);

		ConditionalStep goDefeatBouncer = new ConditionalStep(this, enterCave);
		goDefeatBouncer.addStep(inBouncerCave, killBouncer);
		goDefeatBouncer.addStep(inGoblinCave, enterCrack);
		steps.put(25, goDefeatBouncer);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		ghostlyHood = new ItemRequirement("Ghostly hood", ItemID.GHOSTLY_HOOD, 1, true).isNotConsumed();
		ghostlyBody = new ItemRequirement("Ghostly robe (top)", ItemID.GHOSTLY_ROBE, 1, true).isNotConsumed();
		ghostlyLegs = new ItemRequirement("Ghostly robe (bottom)", ItemID.GHOSTLY_ROBE_6108, 1, true).isNotConsumed();
		ghostlyGloves = new ItemRequirement("Ghostly gloves", ItemID.GHOSTLY_GLOVES, 1, true).isNotConsumed();
		ghostlyBoots = new ItemRequirement("Ghostly boots", ItemID.GHOSTLY_BOOTS, 1, true).isNotConsumed();
		ghostlyCloak = new ItemRequirement("Ghostly cloak", ItemID.GHOSTLY_CLOAK, 1, true).isNotConsumed();
		ghostlyRobes = new ItemRequirements("Ghostly robes", ghostlyHood, ghostlyBody, ghostlyLegs, ghostlyGloves,
			ghostlyBoots, ghostlyCloak).isNotConsumed();

		ringOfVisibility = new ItemRequirement("Ring of visibility", ItemID.RING_OF_VISIBILITY, 1, true).isNotConsumed();
		ghostspeak = new ItemRequirement("Ghostspeak amulet", ItemID.GHOSTSPEAK_AMULET, 1, true).isNotConsumed();
		ghostspeak.addAlternates(ItemID.GHOSTSPEAK_AMULET_4250);

		coins40 = new ItemRequirement("Coins", ItemCollections.COINS, 40);
		inventorySlot = new FreeInventorySlotRequirement(InventoryID.INVENTORY, 1);

		kharidTeleport = new ItemRequirement("Teleport to Al Kharid", ItemCollections.AMULET_OF_GLORIES);
		gnomeTeleport = new ItemRequirement("Teleport to Tree Gnome Stronghold", -1);
		gnomeTeleport.setDisplayItemId(ItemID.SPIRIT_TREE);
		rellekkaTeleport = new ItemRequirement("Teleports to Rellekka", ItemID.RELLEKKA_TELEPORT, 3);
		karamjaTeleport = new ItemRequirement("Teleport to Tai Bwo Wannai", ItemID.TAI_BWO_WANNAI_TELEPORT);

		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		serveredLeg = new ItemRequirement("Severed leg", ItemID.SEVERED_LEG);
		serveredLeg.setTooltip("You can get another from General Khazard");
		sinSeersNote = new ItemRequirement("Sin seer's note", ItemID.SIN_SEERS_NOTE);
	}

	public void loadZones()
	{
		sinRoom = new Zone(new WorldPoint(2680, 3460, 1), new WorldPoint(2740, 3490, 1));
		goblinCave = new Zone(new WorldPoint(2560, 9792, 0), new WorldPoint(2623, 9855, 0));
		bouncerCave = new Zone(new WorldPoint(1756, 4697, 0), new WorldPoint(1767, 4713, 0));
	}

	public void setupConditions()
	{
		inSinRoom = new ZoneRequirement(sinRoom);
		inGoblinCave = new ZoneRequirement(goblinCave);
		inBouncerCave = new ZoneRequirement(bouncerCave);

		hasNote = sinSeersNote;
		givenNote = new VarbitRequirement(3335, 2);
		talkedToGnomeScout = new VarbitRequirement(3332, 1);
		talkedToFaladorScout = new VarbitRequirement(3333, 1);
		talkedToShantyScout = new VarbitRequirement(3334, 1);
		talkedToKaramjaScout = new VarbitRequirement(3331, 1);

		// 3336 0->2 attempted to bribe Seer
		// 3335 0->1 given money to Seer
	}

	public void setupSteps()
	{
		talkToKhazard = new NpcStep(this, NpcID.GENERAL_KHAZARD_3510, new WorldPoint(2718, 3628, 0), "Talk to General" +
			" Khazard south east of Rellekka.", ghostlyHood, ghostlyBody, ghostlyLegs, ghostlyCloak, ghostlyBoots,
			ghostlyGloves, ringOfVisibility, ghostspeak);
		talkToKhazard.addDialogSteps("I just have one of those faces.", "I'm a very trustworthy person.");

		goUpToSeer = new ObjectStep(this, ObjectID.LADDER_25941, new WorldPoint(2699, 3476, 0), "Talk to the Sin Seer in Seers'" +
			" Village.", coins40, inventorySlot);
		talkToSeer = new NpcStep(this, NpcID.SIN_SEER, new WorldPoint(2702, 3473, 1), "Talk to the Sin Seer in Seers'" +
			" Village.", coins40, inventorySlot);
		talkToSeer.addDialogSteps("Bribe", "Here's the money.");
		talkToSeer.addSubSteps(goUpToSeer);

		talkToKhazardAfterSeer = new NpcStep(this, NpcID.GENERAL_KHAZARD_3510, new WorldPoint(2718, 3628, 0),
			"Return to General Khazard south east of Rellekka.", sinSeersNote, ghostlyRobes, ringOfVisibility, ghostspeak);

		talkToKhazardAfterNote = new NpcStep(this, NpcID.GENERAL_KHAZARD_3510, new WorldPoint(2718, 3628, 0),
			"Return to General Khazard south east of Rellekka.", ghostlyRobes, ringOfVisibility, ghostspeak);

		talkToGnomeScout = new NpcStep(this, NpcID.SCOUT_3512, new WorldPoint(2458, 3358, 0), "Talk to the scout " +
			"south of the Tree Gnome Stronghold.", ghostlyRobes, ringOfVisibility, ghostspeak);

		talkToFaladorScout = new NpcStep(this, NpcID.SCOUT_3513, new WorldPoint(3073, 3336, 0), "Talk to the scout " +
			"east of Falador.", ghostlyRobes, ringOfVisibility, ghostspeak);

		talkToShantyScout = new NpcStep(this, NpcID.SCOUT_3514, new WorldPoint(3304, 3084, 0), "Talk to the scout " +
			"south of the Shanty Pass.", ghostlyRobes, ringOfVisibility, ghostspeak);

		talkToKaramjaScout = new NpcStep(this, NpcID.SCOUT, new WorldPoint(2825, 3053, 0), "Talk to the scout " +
			"south east of Tai Bwo Wannai.", ghostlyRobes, ringOfVisibility, ghostspeak);

		talkToKhazardAfterScouts = new NpcStep(this, NpcID.GENERAL_KHAZARD_3510, new WorldPoint(2718, 3628, 0),
			"Return to General Khazard south east of Rellekka.", ghostlyRobes, ringOfVisibility, ghostspeak);

		enterCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE, new WorldPoint(2624, 3393, 0),
			"Enter the cave south east of the Fishing Guild. Be prepared to fight Bouncer (level 160).", combatGear, serveredLeg);

		enterCrack = new ObjectStep(this, ObjectID.CRACK_21800, new WorldPoint(2617, 9827, 0), "Enter the crack in " +
			"the north east of the caves.", combatGear, serveredLeg);
		enterCrack.addDialogStep("Yes");

		killBouncer = new NpcStep(this, NpcID.BOUNCER_3509, new WorldPoint(8969, 2184, 0), "Kill Bouncer. You cannot " +
			"use prayers.", combatGear, serveredLeg);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(coins40, ghostlyHood, ghostlyBody, ghostlyLegs, ghostlyGloves, ghostlyBoots, ghostlyCloak,
			ringOfVisibility, ghostspeak, combatGear);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(rellekkaTeleport, gnomeTeleport, kharidTeleport, karamjaTeleport);
	}


	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Bouncer (level 160, can't use prayer)");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.CURSE_OF_THE_EMPTY_LORD, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.FIGHT_ARENA, QuestState.FINISHED));
		return req;
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.SLAYER, 2000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("The Shadow Sword", ItemID.SHADOW_SWORD, 1));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Proving your Sin",
			Arrays.asList(talkToKhazard, talkToSeer, talkToKhazardAfterSeer), ghostlyRobes,
			ringOfVisibility, ghostspeak, coins40));

		allSteps.add(new PanelDetails("Finding the Scouts",
			Arrays.asList(talkToGnomeScout, talkToFaladorScout, talkToShantyScout, talkToKaramjaScout,
				talkToKhazardAfterScouts), ghostlyRobes, ringOfVisibility, ghostspeak));

		allSteps.add(new PanelDetails("Defeat Bouncer",
			Arrays.asList(enterCave, enterCrack, killBouncer), combatGear, serveredLeg));

		return allSteps;
	}
}
