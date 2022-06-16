/*
 * Copyright (c) 2021, julysfire <https://github.com/julysfire>
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
package com.questhelper.quests.deviousminds;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.requirements.Requirement;
import java.util.*;
import net.runelite.api.*;
import net.runelite.api.Skill;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.DEVIOUS_MINDS
)
public class DeviousMinds extends BasicQuestHelper
{
	//Items Recommended
	ItemRequirement fallyTele, lumberTele, glory;

	//Items Required
	ItemRequirement mith2h, bowString, largePouch, slenderBlade, bowSword, orb, illumPouch, noEquipment;

	//NPC Discussions
	QuestStep talkToMonk, talkToMonk2, teleToAbyss, enterLawRift, leaveLawAltar, talkToHighPriest,
		talkToSirTiffy, talkToEntranaMonk;

	//Object steps
	QuestStep makeBlade, makeBowSword, makeIllumPouch, usePouchOnAltar, gotoDeadMonk, useGangPlank;

	//Conditions
	Requirement inAbyss, inLawAlter, onEntrana, onEntranaBoat;

	//Zones
	Zone abyss, lawAltar, entrana, entranaBoat;


	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToMonk);

		ConditionalStep makeEntireBowSword = new ConditionalStep(this, makeBlade);
		makeEntireBowSword.addStep(bowSword.alsoCheckBank(questBank), talkToMonk2);
		makeEntireBowSword.addStep(slenderBlade.alsoCheckBank(questBank), makeBowSword);
		steps.put(10, makeEntireBowSword);
		steps.put(20, talkToMonk2);   //Finished talking

		ConditionalStep entranaAltarPouch = new ConditionalStep(this, makeIllumPouch);
		entranaAltarPouch.addStep(new Conditions(illumPouch.alsoCheckBank(questBank), onEntrana), usePouchOnAltar);
		entranaAltarPouch.addStep(new Conditions(illumPouch.alsoCheckBank(questBank), inLawAlter), leaveLawAltar);
		entranaAltarPouch.addStep(new Conditions(illumPouch.alsoCheckBank(questBank), inAbyss), enterLawRift);
		entranaAltarPouch.addStep(illumPouch.alsoCheckBank(questBank), teleToAbyss);
		steps.put(30, entranaAltarPouch);
		steps.put(40, entranaAltarPouch);   //Cutscene finished

		steps.put(50, gotoDeadMonk);

		//Alternate routes such as the air balloon will automatically pick up on the onEntrana step
		ConditionalStep backToEntrana = new ConditionalStep(this, talkToEntranaMonk);
		backToEntrana.addStep(onEntrana, talkToHighPriest);
		backToEntrana.addStep(onEntranaBoat, useGangPlank);
		steps.put(60, backToEntrana);

		steps.put(70, talkToSirTiffy);
		return steps;
	}

	@Override
	public void setupRequirements()
	{
		//Recommended
		fallyTele = new ItemRequirement("Falador Teleports", ItemID.FALADOR_TELEPORT);
		lumberTele = new ItemRequirement("Lumberyard Teleports", ItemID.LUMBERYARD_TELEPORT);
		glory = new ItemRequirement("Amulet of Glory", ItemCollections.AMULET_OF_GLORIES);

		//Required
		mith2h = new ItemRequirement("Mithril 2h Sword", ItemID.MITHRIL_2H_SWORD);
		mith2h.setHighlightInInventory(true);
		bowString = new ItemRequirement("Bow String", ItemID.BOW_STRING);
		bowString.setHighlightInInventory(true);
		largePouch = new ItemRequirement("Large Pouch (non-degraded)", ItemID.LARGE_POUCH);
		largePouch.addAlternates(ItemID.LARGE_POUCH_6819);
		largePouch.setHighlightInInventory(true);
		slenderBlade = new ItemRequirement("Slender Blade", ItemID.SLENDER_BLADE);
		slenderBlade.setHighlightInInventory(true);
		bowSword = new ItemRequirement("Bow Sword", ItemID.BOWSWORD);
		orb = new ItemRequirement("Orb", ItemID.ORB);
		orb.setHighlightInInventory(true);
		illumPouch = new ItemRequirement("Illuminated Pouch", ItemID.LARGE_POUCH_6819);
		noEquipment = new ItemRequirement("Banked all equipment and weapons", -1, -1);
	}

	public void setupConditions()
	{
		//Zones
		inAbyss = new ZoneRequirement(abyss);
		inLawAlter = new ZoneRequirement(lawAltar);
		onEntrana = new ZoneRequirement(entrana);
		onEntranaBoat = new ZoneRequirement(entranaBoat);
	}

	public void loadZones()
	{
		abyss = new Zone(new WorldPoint(3005, 4800, 0), new WorldPoint(3070, 4860, 0));
		lawAltar = new Zone(new WorldPoint(2429, 4801, 0), new WorldPoint(2480, 4850, 0));
		entrana = new Zone(new WorldPoint(2799, 3393, 0), new WorldPoint(2880, 3330, 0));
		entranaBoat = new Zone(new WorldPoint(2824, 3333, 1), new WorldPoint(2841, 3328, 1));
	}

	public void setupSteps()
	{
		//Starting Off
		talkToMonk = new NpcStep(this, NpcID.MONK_4563, new WorldPoint(3406, 3494, 0),
			"Start by talking to the Monk outside the Paterdomus temple, east of Varrock.");
		talkToMonk.addDialogStep("Sure thing, what do you need?");

		//Making the bow sword
		makeBlade = new ObjectStep(this, ObjectID.DORICS_WHETSTONE, new WorldPoint(2953, 3452, 0),
			"Use the Mithril 2H sword on Doric's Whetstone. (Doric is located north of Falador).", mith2h);
		makeBlade.addIcon(ItemID.MITHRIL_2H_SWORD);
		makeBowSword = new DetailedQuestStep(this,
			"Use the bow-string on the Slender blade to make the Bow-sword.", bowString, slenderBlade);

		//A gift for Entrana
		talkToMonk2 = new NpcStep(this, NpcID.MONK_4563, new WorldPoint(3406, 3494, 0),
			"Return to the monk near the Paterdomus temple with the bow-sword.", bowSword);
		talkToMonk2.addDialogStep("Yep, got it right here for you.");
		makeIllumPouch = new DetailedQuestStep(this, "Use the Orb on the Large Pouch.", orb, largePouch);

		teleToAbyss = new NpcStep(this, NpcID.MAGE_OF_ZAMORAK_2581, new WorldPoint(3106, 3556, 0),
			"Teleport with the Mage of Zamorak IN THE WILDERNESS to the Abyss. You will be attacked by " +
				"monsters upon entering, and your prayer drained to 0!", illumPouch, noEquipment);
		enterLawRift = new ObjectStep(this, ObjectID.LAW_RIFT, new WorldPoint(3049, 4839, 0),
			"Enter the central area through a gap/passage/eyes. Enter the Law Rift.", illumPouch, noEquipment);
		leaveLawAltar = new ObjectStep(this, ObjectID.PORTAL_34755, new WorldPoint(2464, 4817, 0),
			"Enter the portal to leave the Law Altar.", illumPouch, noEquipment);

		//Surprise!
		usePouchOnAltar = new ObjectStep(this, NullObjectID.NULL_10638, new WorldPoint(2853, 3349, 0),
			"Use the illuminated pouch on the Altar in the Entrana Church.", illumPouch.highlighted());
		usePouchOnAltar.addIcon(ItemID.LARGE_POUCH_6819);

		gotoDeadMonk = new NpcStep(this, NpcID.DEAD_MONK, new WorldPoint(3406, 3494, 0),
			"Go back to the monk near Paterdomus temple and search the dead monk's body.");

		talkToEntranaMonk = new NpcStep(this, NpcID.MONK_OF_ENTRANA, new WorldPoint(3045, 3236, 0),
			"Talk to the Monk of Entrana to go to Entrana.", noEquipment);
		useGangPlank = new ObjectStep(this, ObjectID.GANGPLANK_2415, new WorldPoint(2834, 3333, 1),
			"Use the gangplank to disembark the boat.");
		talkToHighPriest = new NpcStep(this, NpcID.HIGH_PRIEST, new WorldPoint(2851, 3349, 0),
			"Talk to the High Priest in the Entrana Church (Rat Pits Port Sarim Minigame teleport is the closest " +
				"teleport).");
		talkToHighPriest.addSubSteps(useGangPlank);
		talkToHighPriest.addSubSteps(talkToEntranaMonk);

		talkToSirTiffy = new NpcStep(this, NpcID.SIR_TIFFY_CASHIEN, new WorldPoint(2997, 3373, 0),
			"Talk to Sir Tiffy Cashien in Falador park to complete the quest.");
		talkToSirTiffy.addDialogStep("Devious Minds.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(mith2h, bowString, largePouch));
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Arrays.asList(fallyTele, lumberTele, glory));
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return new ArrayList<>(Collections.singletonList("Survive against Abyssal Creatures in multicombat in the " +
			"Abyss"));
	}

	@Override
	public List<String> getNotes()
	{
		return Arrays.asList("You will need to enter the Wilderness briefly during the " +
			"quest. Other players can attack and kill you here, so don't bring anything you're not willing to lose!",
			"You will enter the Abyss briefly during this quest. This is full of aggressive monsters in multi-combat," +
				" and your prayer will be drained to 0 upon entering!");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.SMITHING, 65, true));
		reqs.add(new SkillRequirement(Skill.RUNECRAFT, 50, false));
		reqs.add(new SkillRequirement(Skill.FLETCHING, 50, true));
		reqs.add(new QuestRequirement(QuestHelperQuest.WANTED, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.TROLL_STRONGHOLD, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.DORICS_QUEST, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.ENTER_THE_ABYSS, QuestState.FINISHED));
		reqs.add(new VarbitRequirement(626, Operation.GREATER_EQUAL, 1, "Talked to the " +
			"Zamorak Mage in Varrock after Enter the Abyss"));
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
				new ExperienceReward(Skill.FLETCHING, 5000),
				new ExperienceReward(Skill.RUNECRAFT, 5000),
				new ExperienceReward(Skill.SMITHING, 6500));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Collections.singletonList(talkToMonk)));
		allSteps.add(new PanelDetails("Making the Bow-sword", Arrays.asList(makeBlade, makeBowSword), mith2h, bowString));
		allSteps.add(new PanelDetails("A Gift for Entrana", Arrays.asList(talkToMonk2, makeIllumPouch, teleToAbyss,
			enterLawRift, leaveLawAltar, usePouchOnAltar), largePouch));
		allSteps.add(new PanelDetails("Surprise!", Arrays.asList(gotoDeadMonk, talkToHighPriest, talkToSirTiffy)));
		return allSteps;
	}
}
