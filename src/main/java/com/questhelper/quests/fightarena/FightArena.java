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
package com.questhelper.quests.fightarena;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
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
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.FIGHT_ARENA
)
public class FightArena extends BasicQuestHelper
{
	//Items Required
	ItemRequirement coins, khazardHelmet, khazardPlatebody, khazardHelmetEquipped, khazardPlatebodyEquipped, khaliBrew, cellKeys;

	//Items Recommended
	ItemRequirement combatGear;

	Requirement hasKhazardArmour, inArena, inArenaWithOgre, inArenaWithScorpion, inArenaWithBouncer, inCell;

	QuestStep startQuest, searchChest, talkToGuard, buyKhaliBrew, giveKhaliBrew, getCellKeys, openCell, talkToSammy, killOgre,
		talkToKhazard, talkToHengrad, talkToSammyForScorpion, killScorpion, talkToSammyForBouncer, killBouncer, leaveArena, endQuest;

	//Zones
	Zone arena1, cell;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();
		steps.put(0, startQuest);
		steps.put(1, searchChest);

		ConditionalStep talkToGuardSteps = new ConditionalStep(this, searchChest);
		talkToGuardSteps.addStep(hasKhazardArmour, talkToGuard);
		steps.put(2, talkToGuardSteps);

		ConditionalStep giveKhaliBrewSteps = new ConditionalStep(this, searchChest);
		giveKhaliBrewSteps.addStep(new Conditions(hasKhazardArmour, khaliBrew), giveKhaliBrew);
		giveKhaliBrewSteps.addStep(new Conditions(hasKhazardArmour), buyKhaliBrew);
		steps.put(3, giveKhaliBrewSteps);

		ConditionalStep openCellSteps = new ConditionalStep(this, searchChest);
		// You don't need Khazard armour to open the cell, but unless zones are added for the prison, keep the armour
		// requirement in case you leave the prison and lose the armour
		openCellSteps.addStep(new Conditions(hasKhazardArmour, cellKeys), openCell);
		openCellSteps.addStep(new Conditions(hasKhazardArmour), getCellKeys);
		steps.put(4, openCellSteps);
		steps.put(5, openCellSteps);

		ConditionalStep killOgreSteps = new ConditionalStep(this, talkToSammy);
		killOgreSteps.addStep(new Conditions(inArenaWithOgre), killOgre);
		steps.put(6, killOgreSteps);

		steps.put(7, talkToKhazard); // Step not actually seen, 6->8 when kill Ogre
		steps.put(8, talkToKhazard);

		ConditionalStep inPrisonAndKillScorpionSteps = new ConditionalStep(this, talkToSammyForScorpion);
		inPrisonAndKillScorpionSteps.addStep(inCell, talkToHengrad);
		inPrisonAndKillScorpionSteps.addStep(new Conditions(inArenaWithScorpion), killScorpion);
		steps.put(9, inPrisonAndKillScorpionSteps);

		ConditionalStep killBouncerSteps = new ConditionalStep(this, talkToSammyForBouncer);
		killBouncerSteps.addStep(new Conditions(inArenaWithBouncer), killBouncer);
		steps.put(10, killBouncerSteps);

		ConditionalStep endQuestSteps = new ConditionalStep(this, endQuest);
		endQuestSteps.addStep(inArena, leaveArena);
		steps.put(11, endQuestSteps);
		steps.put(12, endQuestSteps);
		steps.put(13, endQuestSteps);
		steps.put(14, endQuestSteps);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		coins = new ItemRequirement("Coins", ItemCollections.COINS, 5);
		khazardHelmet = new ItemRequirement("Khazard helmet", ItemID.KHAZARD_HELMET);
		khazardPlatebody = new ItemRequirement("Khazard armour", ItemID.KHAZARD_ARMOUR);
		khazardHelmetEquipped = new ItemRequirement("Khazard helmet", ItemID.KHAZARD_HELMET, 1, true);
		khazardPlatebodyEquipped = new ItemRequirement("Khazard armour", ItemID.KHAZARD_ARMOUR, 1, true);
		khaliBrew = new ItemRequirement("Khali brew", ItemID.KHALI_BREW);
		cellKeys = new ItemRequirement("Khazard cell keys", ItemID.KHAZARD_CELL_KEYS);
		cellKeys.setHighlightInInventory(true);
		combatGear = new ItemRequirement("Combat equipment and food (magic/ranged if you want to safe spot)", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
	}

	public void setupZones()
	{
		arena1 = new Zone(new WorldPoint(2583, 3152, 0), new WorldPoint(2606, 3170, 0));
		cell = new Zone(new WorldPoint(2597, 3142, 0), new WorldPoint(2601, 3144, 0));
	}

	public void setupConditions()
	{
		hasKhazardArmour = new ItemRequirements(khazardHelmet, khazardPlatebody);
		inCell = new ZoneRequirement(cell);
		inArena = new ZoneRequirement(arena1);
		inArenaWithOgre = new Conditions(inArena, new NpcCondition(NpcID.KHAZARD_OGRE, arena1));
		inArenaWithScorpion = new Conditions(inArena, new NpcCondition(NpcID.KHAZARD_SCORPION, arena1));
		inArenaWithBouncer = new Conditions(inArena, new NpcCondition(NpcID.BOUNCER, arena1));
	}

	public void setupSteps()
	{
		startQuest = new NpcStep(this, NpcID.LADY_SERVIL, new WorldPoint(2565, 3199, 0),
			"Talk to Lady Servil, west-southwest of the Monastery south of Ardougne.");
		startQuest.addDialogStep(2, "Can I help you?");
		searchChest = new ObjectStep(this, ObjectID.CHEST, new WorldPoint(2613, 3189, 0), "Search the chest to the east for some Khazard armour.");
		((ObjectStep) searchChest).addAlternateObjects(ObjectID.CHEST_76);
		talkToGuard = new NpcStep(this, NpcID.KHAZARD_GUARD_1209, new WorldPoint(2615, 3143, 0),
			"Equip Khazard armour, talk to the Khazard Guard in the southeast of the prison.", khazardHelmetEquipped, khazardPlatebodyEquipped);
		buyKhaliBrew = new NpcStep(this, NpcID.KHAZARD_BARMAN, new WorldPoint(2567, 3140, 0),
			"Buy Khali brew for 5 coins from the nearby bar to the west.", coins);
		buyKhaliBrew.addDialogStep(2, "I'd like a Khali brew please.");
		giveKhaliBrew = new NpcStep(this, NpcID.KHAZARD_GUARD_1209, new WorldPoint(2615, 3143, 0),
			"Take the brew back to the Khazard Guard.", khazardHelmetEquipped, khazardPlatebodyEquipped, khaliBrew);
		getCellKeys = new NpcStep(this, NpcID.KHAZARD_GUARD_1209, new WorldPoint(2615, 3143, 0),
			"Get another set of keys from the Khazard Guard", khazardHelmetEquipped, khazardPlatebodyEquipped);
		openCell = new ObjectStep(this, ObjectID.PRISON_DOOR_80, new WorldPoint(2617, 3167, 0),
			"Get ready to fight the monsters (all safespottable), starting with Khazard Ogre (level 63). Use the keys on Sammy's cell door to free him.", combatGear, cellKeys);
		openCell.addIcon(ItemID.KHAZARD_CELL_KEYS);
		talkToSammy = new NpcStep(this, NpcID.SAMMY_SERVIL_1221, new WorldPoint(2602, 3153, 0), "Talk to Sammy, then fight the ogre.");
		killOgre = new NpcStep(this, NpcID.KHAZARD_OGRE, new WorldPoint(2601, 3163, 0),
			"Kill the Ogre. You can lure it behind a skeleton to safespot it.", combatGear);
		killOgre.addSubSteps(talkToSammy);
		talkToKhazard = new NpcStep(this, NpcID.GENERAL_KHAZARD, new WorldPoint(2605, 3153, 0), "Talk to General Khazard.");
		talkToHengrad = new NpcStep(this, NpcID.HENGRAD, new WorldPoint(2599, 3143, 0),
			"Talk to Hengrad.");
		talkToHengrad.addSubSteps(talkToKhazard);
		talkToSammyForScorpion = new NpcStep(this, NpcID.SAMMY_SERVIL_1221, new WorldPoint(2602, 3153, 0), "Talk to Sammy, then fight the scorpion.");
		killScorpion = new NpcStep(this, NpcID.KHAZARD_SCORPION, new WorldPoint(2601, 3163, 0),
			"Kill the Scorpion. You can lure it behind a skeleton to safespot it.", combatGear);
		killScorpion.addSubSteps(talkToSammyForScorpion);
		talkToSammyForBouncer = new NpcStep(this, NpcID.SAMMY_SERVIL_1221, new WorldPoint(2602, 3153, 0), "Talk to Sammy, then fight Bouncer.");
		killBouncer = new NpcStep(this, NpcID.BOUNCER, new WorldPoint(2601, 3163, 0),
			"Kill Bouncer. You can lure it behind a skeleton to safespot it. Warning: After Bouncer is killed, you will be unable to re-enter the arena.", combatGear);
		killBouncer.addSubSteps(talkToSammyForBouncer);
		leaveArena = new ObjectStep(this, ObjectID.DOOR_82, new WorldPoint(2606, 3152, 0),
			"Exit the arena (can ignore General Khazard). Warning: You will be unable to re-enter the arena.");
		endQuest = new NpcStep(this, NpcID.LADY_SERVIL, new WorldPoint(2565, 3199, 0),
			"Go back to Lady Servil to end the quest.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Collections.singletonList(coins);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Collections.singletonList(combatGear);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Khazard Scorpion (level 44) (safespottable)");
		reqs.add("Khazard Ogre (level 63) (safespottable)");
		reqs.add("Bouncer (level 137) (safespottable)");
		reqs.add("General Khazard (level 112) (safespottable) (optional)");
		return reqs;
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
				new ExperienceReward(Skill.ATTACK, 12175),
				new ExperienceReward(Skill.THIEVING, 2175));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("1,000 Coins", ItemID.COINS_995, 1000),
				new ItemReward("Khazard Armor", ItemID.KHAZARD_ARMOUR, 1));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Start quest", Arrays.asList(startQuest, searchChest, talkToGuard, buyKhaliBrew, giveKhaliBrew), coins));
		allSteps.add(new PanelDetails("Fight!", Arrays.asList(getCellKeys, openCell, killOgre, talkToHengrad, killScorpion, killBouncer), combatGear));
		allSteps.add(new PanelDetails("Finish quest", Arrays.asList(leaveArena, endQuest)));
		return allSteps;
	}
}
