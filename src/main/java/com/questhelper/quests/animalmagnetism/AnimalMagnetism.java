/*
 * Copyright (c) 2021, Robert Maloney <https://github.com/robertmaloney>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.quests.animalmagnetism;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.PuzzleStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
		quest = QuestHelperQuest.ANIMAL_MAGNETISM
)
public class AnimalMagnetism extends BasicQuestHelper
{
	//Items Required
	ItemRequirement mithrilAxe, ironBar5, ghostspeak, ghostspeakEquip, ectoToken20, hammer, hardLeather, holySymbol,
		polishedButtons, croneMadeAmulet, undeadChicken2, selectedIron, barMagnet, blessedAxe, twigs, researchNotes,
		translatedNotes, pattern, container;

	// Items recommended
	ItemRequirement draynorTeleport, burthorpeTeleport, portPhasmatysTeleport;

	QuestStep talkToAva, giveChickensToAva,
		talkToAlice, talkToAlice2,
		talkToAlicesHusband, talkToAlicesHusband2, talkToAlicesHusband3,
		talkToOldCrone, giveAmuletToHusband, buyUndeadChickens,
		talkToWitch, goToIronMine, useHammerOnMagnet, giveMagnetToAva,
		attemptToCutTree, talkToTurael, cutTree, giveTwigsToAva,
		getNotesFromAva, translateNotes, giveNotesToAva, buildPattern, giveContainerToAva;

	Requirement inIronMine;

	Zone ironMine;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToAva);
		steps.put(10, talkToAlicesHusband);
		steps.put(20, talkToAlice);
		steps.put(30, talkToAlicesHusband2);
		steps.put(40, talkToAlice2);
		steps.put(50, talkToAlicesHusband2);
		steps.put(60, talkToAlice2);
		steps.put(70, talkToOldCrone);
		steps.put(73, talkToOldCrone);
		steps.put(76, giveAmuletToHusband);
		steps.put(80, talkToAlicesHusband3);
		steps.put(90, talkToAlicesHusband3);
		//Step 90 is a cutscene

		ConditionalStep undeadChickens = new ConditionalStep(this, buyUndeadChickens);
		undeadChickens.addStep(undeadChicken2, giveChickensToAva);
		steps.put(100, undeadChickens);
		steps.put(110, undeadChickens);

		steps.put(120, talkToWitch);
		steps.put(130, talkToWitch);

		ConditionalStep createMagnet = new ConditionalStep(this, goToIronMine);
		//Goes before iron mine so that acquiring it shows the right step
		createMagnet.addStep(barMagnet, giveMagnetToAva);
		createMagnet.addStep(inIronMine, useHammerOnMagnet);
		steps.put(140, createMagnet);

		steps.put(150, attemptToCutTree);
		//Meant to talk to Ava here, but it doesn't trigger
		steps.put(160, talkToTurael);
		steps.put(170, talkToTurael);

		ConditionalStep getTwigs = new ConditionalStep(this, cutTree);
		getTwigs.addStep(twigs, giveTwigsToAva);
		steps.put(180, getTwigs);

		steps.put(190, getNotesFromAva);
		steps.put(200, translateNotes);
		steps.put(210, giveNotesToAva);
		steps.put(220, buildPattern);
		steps.put(230, giveContainerToAva);

		return steps;
	}

	private void loadZones()
	{
		ironMine = new Zone(new WorldPoint(2971, 3248, 0), new WorldPoint(2987, 3234, 0));
	}

	public void setupItemRequirements()
	{
		ghostspeak = new ItemRequirement("Ghostspeak amulet", ItemID.GHOSTSPEAK_AMULET);
		ghostspeakEquip = new ItemRequirement("Ghostspeak amulet", ItemID.GHOSTSPEAK_AMULET, 1, true);
		croneMadeAmulet = new ItemRequirement("Crone-made amulet", ItemID.CRONEMADE_AMULET);
		ectoToken20 = new ItemRequirement("Ecto-token", ItemID.ECTOTOKEN, 20);
		undeadChicken2 = new ItemRequirement("Undead chicken", ItemID.UNDEAD_CHICKEN, 2);
		undeadChicken2.canBeObtainedDuringQuest();

		//Magnet
		ironBar5 = new ItemRequirement("Iron Bar", ItemID.IRON_BAR, 5);
		hammer = new ItemRequirement("Hammer", ItemCollections.getHammer());
		selectedIron = new ItemRequirement("Selected Iron", ItemID.SELECTED_IRON);
		barMagnet = new ItemRequirement("Bar magnet", ItemID.BAR_MAGNET);

		//Undead twigs
		mithrilAxe = new ItemRequirement("Mithril Axe", ItemID.MITHRIL_AXE);
		holySymbol = new ItemRequirement("Holy Symbol", ItemID.HOLY_SYMBOL);
		blessedAxe = new ItemRequirement("Blessed axe", ItemID.BLESSED_AXE);
		twigs = new ItemRequirement("Undead twigs", ItemID.UNDEAD_TWIGS);
		researchNotes = new ItemRequirement("Research notes", ItemID.RESEARCH_NOTES);
		researchNotes.setHighlightInInventory(true);
		translatedNotes = new ItemRequirement("Translated notes", ItemID.TRANSLATED_NOTES);
		hardLeather = new ItemRequirement("Hard Leather", ItemID.HARD_LEATHER);
		hardLeather.setHighlightInInventory(true);
		polishedButtons = new ItemRequirement("Polished Buttons", ItemID.POLISHED_BUTTONS);
		polishedButtons.setHighlightInInventory(true);

		draynorTeleport = new ItemRequirement("Teleport to Draynor", ItemID.DRAYNOR_MANOR_TELEPORT, 5);
		draynorTeleport.addAlternates(ItemCollections.getAmuletOfGlories());
		burthorpeTeleport = new ItemRequirement("Teleport to Burthorpe", ItemCollections.getGamesNecklaces());
		portPhasmatysTeleport = new ItemRequirement("Teleport to Port Phasmatys", ItemID.ECTOPHIAL);
		portPhasmatysTeleport.addAlternates(ItemID.FENKENSTRAINS_CASTLE_TELEPORT);

		pattern = new ItemRequirement("A pattern", ItemID.A_PATTERN);
		pattern.setHighlightInInventory(true);
		container = new ItemRequirement("A container", ItemID.A_CONTAINER);
	}

	private void setupConditions()
	{
		inIronMine = new ZoneRequirement(ironMine);
	}

	private void setupSteps()
	{
		talkToAva = new NpcStep(this, NpcID.AVA, new WorldPoint(3093, 3357, 0),
			"Talk to Ava to begin the quest.");
		talkToAva.addDialogStep("I would be happy to make your home a better place.");
		talkToAlicesHusband = new NpcStep(this, NpcID.ALICES_HUSBAND, new WorldPoint(3618, 3526, 0),
			"Talk to Alice's husband at the farm west of the Ectofuntus.",
			ghostspeakEquip);
		talkToAlice = new NpcStep(this, NpcID.ALICE, new WorldPoint(3627, 3526, 0),
			"Talk to Alice.");
		talkToAlice.addDialogStep("I'm here about a quest.");
		talkToAlicesHusband2 = new NpcStep(this, NpcID.ALICES_HUSBAND, new WorldPoint(3618, 3526, 0),
			"Talk to Alice's husband again.",
			ghostspeakEquip);
		talkToAlice2 = new NpcStep(this, NpcID.ALICE, new WorldPoint(3627, 3526, 0),
			"Talk to Alice again.");
		talkToAlice2.addDialogStep("I'm here about a quest.");
		talkToOldCrone = new NpcStep(this, NpcID.OLD_CRONE, new WorldPoint(3461, 3558, 0),
			"Talk to the Old crone just east of the Slayer Tower twice.",
			ghostspeakEquip);
		talkToOldCrone.addDialogStep("I'm here about the farmers east of here.");
		giveAmuletToHusband = new NpcStep(this, NpcID.ALICES_HUSBAND, new WorldPoint(3618, 3526, 0),
			"Give the Crone-made amulet to Alice's Husband.",
			ghostspeakEquip, croneMadeAmulet);
		giveAmuletToHusband.addDialogStep("Okay, you need it more than I do, I suppose.");
		talkToAlicesHusband3 = new NpcStep(this, NpcID.ALICES_HUSBAND_4412, new WorldPoint(3618, 3526, 0),
			"Talk to Alice's husband again to watch a cutscene.",
			ghostspeakEquip);
		buyUndeadChickens = new NpcStep(this, NpcID.ALICES_HUSBAND_4412, new WorldPoint(3618, 3526, 0),
			"Buy two undead chickens from Alice's husband. You can acquire ecto-tokens using the Ectofuntus to the east.",
			ghostspeakEquip, ectoToken20);
		buyUndeadChickens.addDialogSteps("Could I buy those chickens now, then?", "Could I buy 2 chickens?");
		giveChickensToAva = new NpcStep(this, NpcID.AVA, new WorldPoint(3093, 3357, 0),
			"Give the two Undead chickens to Ava.",
			undeadChicken2);

		talkToWitch = new NpcStep(this, NpcID.WITCH_4409, new WorldPoint(3099, 3370, 0),
			"Talk to the witch in the manor twice.",
			ironBar5);
		goToIronMine = new DetailedQuestStep(this, new WorldPoint(2978, 3240, 0),
			"Go to the iron mine in Rimmington northeast of the house portal.",
			hammer, selectedIron);
		useHammerOnMagnet = new ItemStep(this, "While looking north, use the hammer on the Selected iron.",
			hammer.highlighted(), selectedIron.highlighted());
		giveMagnetToAva = new NpcStep(this, NpcID.AVA, new WorldPoint(3093, 3357, 0),
			"Give the Bar magnet to Ava.",
			barMagnet);

		attemptToCutTree = new NpcStep(this, NpcID.UNDEAD_TREE, "Try to chop an undead tree outside Draynor manor.", true,
			mithrilAxe);
		talkToTurael = new NpcStep(this, NpcID.TURAEL, new WorldPoint(2931, 3536, 0),
			"Talk to Turael in Burthorpe twice, giving him the Mithril axe and Holy symbol.",
			mithrilAxe, holySymbol);
		talkToTurael.addDialogSteps("I'm here about a quest.", "Hello, I'm here about those trees again.",
			"I'd love one, thanks.");
		cutTree = new NpcStep(this, NpcID.UNDEAD_TREE,
			"Try to chop an undead tree outside Draynor manor with the Blessed axe until you receive undead twigs.",
			true,
			blessedAxe);
		giveTwigsToAva = new NpcStep(this, NpcID.AVA, new WorldPoint(3093, 3357, 0),
			"Give the Undead twigs to Ava.",
			twigs);

		getNotesFromAva = new NpcStep(this, NpcID.AVA, new WorldPoint(3093, 3357, 0),
			"Talk to Ava to receive the research notes.");
		translateNotes = new PuzzleStep(this, "Translate research notes by clicking on all the highlighted switches.",
			new PuzzleSolver()::solver,
			researchNotes);
		giveNotesToAva = new NpcStep(this, NpcID.AVA, new WorldPoint(3093, 3357, 0),
			"Give Ava the translated research notes.",
			translatedNotes);
		buildPattern = new ItemStep(this, "Combine Hard leather and Polished buttons with the pattern.",
			pattern, hardLeather, polishedButtons);
		giveContainerToAva = new NpcStep(this, NpcID.AVA, new WorldPoint(3093, 3357, 0),
			"Give Ava the container.",
			container);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(mithrilAxe, ironBar5, ghostspeak, ectoToken20, hammer, hardLeather, holySymbol, polishedButtons);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(draynorTeleport, burthorpeTeleport, portPhasmatysTeleport);
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
				new ExperienceReward(Skill.CRAFTING, 1000),
				new ExperienceReward(Skill.FLETCHING, 1000),
				new ExperienceReward(Skill.SLAYER, 1000),
				new ExperienceReward(Skill.WOODCUTTING, 2500));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("Ava's Attractor", ItemID.AVAS_ATTRACTOR, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Ability to purchase Ava's Devices"));
	}


	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();

		//Skill Requirements
		req.add(new SkillRequirement(Skill.SLAYER, 18));
		req.add(new SkillRequirement(Skill.CRAFTING, 19));
		req.add(new SkillRequirement(Skill.RANGED, 30));
		req.add(new SkillRequirement(Skill.WOODCUTTING, 35));

		//Quest Requirements
		req.add(new QuestRequirement(QuestHelperQuest.THE_RESTLESS_GHOST, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.ERNEST_THE_CHICKEN, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.PRIEST_IN_PERIL, QuestState.FINISHED));

		return req;
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Collections.singletonList(talkToAva)));
		allSteps.add(new PanelDetails("Undead Chickens",
			Arrays.asList(talkToAlicesHusband, talkToAlice, talkToAlicesHusband2, talkToAlice2, talkToOldCrone,
				giveAmuletToHusband, talkToAlicesHusband3, buyUndeadChickens, giveChickensToAva),
			ghostspeak, ectoToken20));
		allSteps.add(new PanelDetails("Magnet",
			Arrays.asList(talkToWitch, goToIronMine, useHammerOnMagnet, giveMagnetToAva), ironBar5, hammer, undeadChicken2));
		allSteps.add(new PanelDetails("Undead twigs",
			Arrays.asList(attemptToCutTree, talkToTurael, cutTree, giveTwigsToAva, getNotesFromAva, translateNotes, giveNotesToAva, buildPattern, giveContainerToAva),
			mithrilAxe, polishedButtons, hardLeather, holySymbol));

		return allSteps;
	}
}
