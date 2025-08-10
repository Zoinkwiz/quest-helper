/*
 * Copyright (c) 2020, Zantier
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
package com.questhelper.helpers.quests.fightarena;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import static com.questhelper.requirements.util.LogicHelper.and;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

public class FightArena extends BasicQuestHelper
{
	// Required items
	ItemRequirement coins;

	// Recommended items
	ItemRequirement combatGear;

	// Zones
	Zone arena1;
	Zone cell;

	// Miscellaneous requirements
	ItemRequirement khazardHelmet;
	ItemRequirement khazardPlatebody;
	ItemRequirement khazardHelmetEquipped;
	ItemRequirement khazardPlatebodyEquipped;
	ItemRequirement khaliBrew;
	ItemRequirement cellKeys;

	Requirement hasKhazardArmour;
	Requirement inArena;
	Requirement inArenaWithOgre;
	Requirement inArenaWithScorpion;
	Requirement inArenaWithBouncer;
	Requirement inCell;

	// Steps
	NpcStep startQuest;
	ObjectStep searchChest;
	NpcStep talkToGuard;
	NpcStep buyKhaliBrew;
	NpcStep giveKhaliBrew;
	NpcStep getCellKeys;
	ObjectStep openCell;
	NpcStep talkToSammy;
	NpcStep killOgre;
	NpcStep talkToKhazard;
	NpcStep talkToHengrad;
	NpcStep talkToSammyForScorpion;
	NpcStep killScorpion;
	NpcStep talkToSammyForBouncer;
	NpcStep killBouncer;
	ObjectStep leaveArena;
	NpcStep endQuest;


	@Override
	protected void setupZones()
	{
		arena1 = new Zone(new WorldPoint(2583, 3152, 0), new WorldPoint(2606, 3170, 0));
		cell = new Zone(new WorldPoint(2597, 3142, 0), new WorldPoint(2601, 3144, 0));
	}

	@Override
	protected void setupRequirements()
	{
		inCell = new ZoneRequirement(cell);
		inArena = new ZoneRequirement(arena1);
		inArenaWithOgre = and(inArena, new NpcCondition(NpcID.ARENA_OGRE, arena1));
		inArenaWithScorpion = and(inArena, new NpcCondition(NpcID.ARENA_SCORPION, arena1));
		inArenaWithBouncer = and(inArena, new NpcCondition(NpcID.ARENA_BOUNCER, arena1));

		coins = new ItemRequirement("Coins", ItemCollections.COINS, 5);

		combatGear = new ItemRequirement("Combat equipment and food (magic/ranged if you want to safe spot)", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		khazardHelmet = new ItemRequirement("Khazard helmet", ItemID.KHAZARD_HELMET);
		khazardPlatebody = new ItemRequirement("Khazard armour", ItemID.KHAZARD_PLATEMAIL);
		khazardHelmetEquipped = khazardHelmet.equipped();
		khazardPlatebodyEquipped = khazardPlatebody.equipped();
		khaliBrew = new ItemRequirement("Khali brew", ItemID.KHALI_BREW);
		cellKeys = new ItemRequirement("Khazard cell keys", ItemID.KHAZARD_CELLKEYS);
		cellKeys.setHighlightInInventory(true);

		hasKhazardArmour = new ItemRequirements(khazardHelmet, khazardPlatebody);
	}

	public void setupSteps()
	{
		startQuest = new NpcStep(this, NpcID.LADY_SERVIL_VIS, new WorldPoint(2565, 3199, 0), "Talk to Lady Servil, west-southwest of the Monastery south of Ardougne.");
		startQuest.addDialogStep("Yes.");

		searchChest = new ObjectStep(this, ObjectID.ARENA_GUARD_CHEST_SHUT, new WorldPoint(2613, 3189, 0), "Search the chest to the east for some Khazard armour.");
		searchChest.addAlternateObjects(ObjectID.ARENA_GUARD_CHEST_OPEN);

		talkToGuard = new NpcStep(this, NpcID.ARENA_GUARD2, new WorldPoint(2615, 3143, 0), "Equip Khazard armour, talk to the Khazard Guard in the southeast of the prison.", khazardHelmetEquipped, khazardPlatebodyEquipped);

		buyKhaliBrew = new NpcStep(this, NpcID.KHAZARD_BARMAN, new WorldPoint(2567, 3140, 0), "Buy Khali brew from the nearby bar to the west for 5 coins.", coins);
		buyKhaliBrew.addDialogStep("I'd like a Khali Brew please.");

		giveKhaliBrew = new NpcStep(this, NpcID.ARENA_GUARD2, new WorldPoint(2615, 3143, 0), "Take the brew back to the Khazard Guard.", khazardHelmetEquipped, khazardPlatebodyEquipped, khaliBrew);

		getCellKeys = new NpcStep(this, NpcID.ARENA_GUARD2, new WorldPoint(2615, 3143, 0),
			"Get another set of keys from the Khazard Guard", khazardHelmetEquipped, khazardPlatebodyEquipped);
		openCell = new ObjectStep(this, ObjectID.ARENA_JEREMYDOOR, new WorldPoint(2617, 3167, 0),
			"Get ready to fight the monsters (all safespottable), starting with Khazard Ogre (level 63). Use the keys on Sammy's cell door to free him.", combatGear, cellKeys);
		openCell.addIcon(ItemID.KHAZARD_CELLKEYS);
		talkToSammy = new NpcStep(this, NpcID.SAMMY_SERVIL_VIS_NOOP, new WorldPoint(2602, 3153, 0), "Talk to Sammy, then fight the ogre.");

		killOgre = new NpcStep(this, NpcID.ARENA_OGRE, new WorldPoint(2601, 3163, 0), "Kill the Ogre. You can lure it behind a skeleton to safespot it.", combatGear);
		killOgre.addSubSteps(talkToSammy);
		killOgre.addSafeSpots(new WorldPoint(2598, 3162, 0));

		talkToKhazard = new NpcStep(this, NpcID.SHADOW_MAJ_KHAZARD, new WorldPoint(2605, 3153, 0), "Talk to General Khazard.");
		talkToHengrad = new NpcStep(this, NpcID.HENGRAD, new WorldPoint(2599, 3143, 0),
			"Talk to Hengrad.");
		talkToHengrad.addSubSteps(talkToKhazard);

		talkToSammyForScorpion = new NpcStep(this, NpcID.SAMMY_SERVIL_VIS_NOOP, new WorldPoint(2602, 3153, 0), "Talk to Sammy, then fight the scorpion.");

		killScorpion = new NpcStep(this, NpcID.ARENA_SCORPION, new WorldPoint(2601, 3163, 0),
			"Kill the Scorpion. You can lure it behind a skeleton to safespot it.", combatGear);
		killScorpion.addSafeSpots(new WorldPoint(2598, 3162, 0));
		killScorpion.addSubSteps(talkToSammyForScorpion);

		talkToSammyForBouncer = new NpcStep(this, NpcID.SAMMY_SERVIL_VIS_NOOP, new WorldPoint(2602, 3153, 0), "Talk to Sammy, then fight Bouncer.");
		killBouncer = new NpcStep(this, NpcID.ARENA_BOUNCER, new WorldPoint(2601, 3163, 0),
			"Kill Bouncer. You can lure it behind a skeleton to safespot it. Warning: After Bouncer is killed, you will be unable to re-enter the arena.", combatGear);
		killBouncer.addSubSteps(talkToSammyForBouncer);
		leaveArena = new ObjectStep(this, ObjectID.FIGHTARENA_DOOR2, new WorldPoint(2606, 3152, 0),
			"Exit the arena (can ignore General Khazard). Warning: You will be unable to re-enter the arena.");
		endQuest = new NpcStep(this, NpcID.LADY_SERVIL_VIS, new WorldPoint(2565, 3199, 0),
			"Go back to Lady Servil to end the quest.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();
		steps.put(0, startQuest);
		steps.put(1, searchChest);

		var talkToGuardSteps = new ConditionalStep(this, searchChest);
		talkToGuardSteps.addStep(hasKhazardArmour, talkToGuard);
		steps.put(2, talkToGuardSteps);

		var giveKhaliBrewSteps = new ConditionalStep(this, searchChest);
		giveKhaliBrewSteps.addStep(and(hasKhazardArmour, khaliBrew), giveKhaliBrew);
		giveKhaliBrewSteps.addStep(hasKhazardArmour, buyKhaliBrew);
		steps.put(3, giveKhaliBrewSteps);

		var openCellSteps = new ConditionalStep(this, searchChest);
		// You don't need Khazard armour to open the cell, but unless zones are added for the prison, keep the armour
		// requirement in case you leave the prison and lose the armour
		openCellSteps.addStep(and(hasKhazardArmour, cellKeys), openCell);
		openCellSteps.addStep(hasKhazardArmour, getCellKeys);
		steps.put(4, openCellSteps);
		steps.put(5, openCellSteps);

		var killOgreSteps = new ConditionalStep(this, talkToSammy);
		killOgreSteps.addStep(inArenaWithOgre, killOgre);
		steps.put(6, killOgreSteps);

		steps.put(7, talkToKhazard); // Step not actually seen, 6->8 when kill Ogre
		steps.put(8, talkToKhazard);

		var inPrisonAndKillScorpionSteps = new ConditionalStep(this, talkToSammyForScorpion);
		inPrisonAndKillScorpionSteps.addStep(inCell, talkToHengrad);
		inPrisonAndKillScorpionSteps.addStep(inArenaWithScorpion, killScorpion);
		steps.put(9, inPrisonAndKillScorpionSteps);

		var killBouncerSteps = new ConditionalStep(this, talkToSammyForBouncer);
		killBouncerSteps.addStep(inArenaWithBouncer, killBouncer);
		steps.put(10, killBouncerSteps);

		var endQuestSteps = new ConditionalStep(this, endQuest);
		endQuestSteps.addStep(inArena, leaveArena);
		steps.put(11, endQuestSteps);
		steps.put(12, endQuestSteps);
		steps.put(13, endQuestSteps);
		steps.put(14, endQuestSteps);

		return steps;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			coins
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			combatGear
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Khazard Scorpion (level 44) (safespottable)",
			"Khazard Ogre (level 63) (safespottable)",
			"Bouncer (level 137) (safespottable)",
			"General Khazard (level 142) (safespottable) (optional)"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.ATTACK, 12175),
			new ExperienceReward(Skill.THIEVING, 2175)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Coins", ItemID.COINS, 1000),
			new ItemReward("Khazard Armor", ItemID.KHAZARD_PLATEMAIL, 1)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Start quest", List.of(
			startQuest,
			searchChest,
			talkToGuard,
			buyKhaliBrew,
			giveKhaliBrew
		), List.of(
			coins
		)));

		sections.add(new PanelDetails("Fight!", List.of(
			getCellKeys,
			openCell,
			killOgre,
			talkToHengrad,
			killScorpion,
			killBouncer
		), List.of(
			combatGear
		)));

		sections.add(new PanelDetails("Finish quest", List.of(
			leaveArena,
			endQuest
		)));

		return sections;
	}
}
