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
package com.questhelper.helpers.quests.aporcineofinterest;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
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

public class APorcineOfInterest extends BasicQuestHelper
{
	// Required items
	ItemRequirement rope;
	ItemRequirement slashItem;
	ItemRequirement combatGear;

	// Recommended items
	ItemRequirement draynorTeleport;
	ItemRequirement faladorFarmTeleport;

	// Mid-quest item requirements
	ItemRequirement reinforcedGoggles;
	ItemRequirement hoof;

	// Zones
	Zone cave;

	// Miscellaneous requirements
	Requirement inCave;

	// Steps
	ObjectStep readNotice;
	NpcStep talkToSarah;
	ObjectStep useRopeOnHole;
	ObjectStep enterHole;
	ObjectStep investigateSkeleton;
	NpcStep talkToSpria;
	ObjectStep enterHoleAgain;
	NpcStep killSourhog;
	ObjectStep enterHoleForFoot;
	ObjectStep cutOffFoot;
	NpcStep returnToSarah;
	NpcStep returnToSpria;


	@Override
	protected void setupZones()
	{
		cave = new Zone(new WorldPoint(3152, 9669, 0), new WorldPoint(3181, 9720, 0));
	}

	@Override
	protected void setupRequirements()
	{
		rope = new ItemRequirement("Rope", ItemID.ROPE);
		rope.setHighlightInInventory(true);

		slashItem = new ItemRequirement("A knife or slash weapon", ItemID.KNIFE).isNotConsumed();
		slashItem.setTooltip("Except abyssal whip, abyssal tentacle, noxious halberd, or dragon claws.");

		reinforcedGoggles = new ItemRequirement("Reinforced goggles", ItemID.SLAYER_REINFORCED_GOGGLES, 1, true).isNotConsumed();
		reinforcedGoggles.setTooltip("You can get another pair from Spria");

		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		hoof = new ItemRequirement("Sourhog foot", ItemID.PORCINE_SOURHOG_TROPHY);

		// Recommended
		draynorTeleport = new ItemRequirement("Teleport to north Draynor", ItemID.TELETAB_DRAYNOR);
		draynorTeleport.addAlternates(ItemCollections.AMULET_OF_GLORIES);
		faladorFarmTeleport = new ItemRequirement("Teleport to Falador Farm", ItemCollections.EXPLORERS_RINGS);

		inCave = new ZoneRequirement(cave);
	}

	public void setupSteps()
	{
		readNotice = new ObjectStep(this, ObjectID.PORCINE_NOTICEBOARD, new WorldPoint(3086, 3251, 0), "Read the notice board in Draynor Village.");
		readNotice.addDialogStep("Yes.");

		talkToSarah = new NpcStep(this, NpcID.FARMING_SHOPKEEPER_1, new WorldPoint(3033, 3293, 0), "Talk to Sarah in the South Falador Farm.");
		talkToSarah.addDialogSteps("Talk about the bounty.");

		useRopeOnHole = new ObjectStep(this, ObjectID.PORCINE_HOLE, new WorldPoint(3151, 3348, 0), "Use a rope on the Strange Hole east of Draynor Manor.", rope);
		useRopeOnHole.addTeleport(draynorTeleport);
		useRopeOnHole.addIcon(ItemID.ROPE);
		useRopeOnHole.addDialogSteps("I think that'll be all for now.");

		enterHole = new ObjectStep(this, ObjectID.PORCINE_HOLE, new WorldPoint(3151, 3348, 0), "Climb down into the Strange Hole east of Draynor Manor.");
		investigateSkeleton = new ObjectStep(this, ObjectID.PORCINE_SKELETON, new WorldPoint(3164, 9676, 0), "Go to the end of the cave and investigate the skeleton there.");

		talkToSpria = new NpcStep(this, NpcID.PORCINE_SPRIA, new WorldPoint(3092, 3267, 0), "Talk to Spria in Draynor Village.");

		enterHoleAgain = new ObjectStep(this, ObjectID.PORCINE_HOLE, new WorldPoint(3151, 3348, 0), "Climb down into the Strange Hole east of Draynor Manor. Be prepared to fight Sourhog (level 37)", reinforcedGoggles, slashItem, combatGear);
		killSourhog = new NpcStep(this, NpcID.PORCINE_SOURHOG_SECOND, "Kill Sourhog.", reinforcedGoggles);
		killSourhog.addDialogStep("Yes");

		enterHoleForFoot = new ObjectStep(this, ObjectID.PORCINE_HOLE, new WorldPoint(3151, 3348, 0), "Climb down into the Strange Hole east of Draynor Manor.", slashItem);
		cutOffFoot = new ObjectStep(this, ObjectID.PORCINE_DEAD_SOURHOG, "Cut off Sourhog's foot.", slashItem);
		cutOffFoot.addAlternateObjects(ObjectID.PORCINE_DEAD_SOURHOG_9);
		cutOffFoot.addSubSteps(enterHoleForFoot);

		returnToSarah = new NpcStep(this, NpcID.FARMING_SHOPKEEPER_1, new WorldPoint(3033, 3293, 0), "Return to Sarah in the South Falador Farm.", hoof);
		returnToSarah.addTeleport(faladorFarmTeleport);
		returnToSarah.addDialogSteps("Talk about the bounty.");
		returnToSpria = new NpcStep(this, NpcID.PORCINE_SPRIA, new WorldPoint(3092, 3267, 0), "Return to Spria in Draynor Village.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, readNotice);
		steps.put(5, talkToSarah);
		steps.put(10, useRopeOnHole);

		var investigateCave = new ConditionalStep(this, enterHole);
		investigateCave.addStep(inCave, investigateSkeleton);

		steps.put(15, investigateCave);

		steps.put(20, talkToSpria);

		var goKillSourhog = new ConditionalStep(this, enterHoleAgain);
		goKillSourhog.addStep(inCave, killSourhog);

		steps.put(25, goKillSourhog);

		var getFootSteps = new ConditionalStep(this, enterHoleForFoot);
		getFootSteps.addStep(hoof.alsoCheckBank(), returnToSarah);
		getFootSteps.addStep(inCave, cutOffFoot);

		steps.put(30, getFootSteps);
		steps.put(35, returnToSpria);

		return steps;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			rope,
			slashItem,
			combatGear
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			draynorTeleport,
			faladorFarmTeleport
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Sourhog (level 37)"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.SLAYER, 1000)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Coins", ItemID.COINS, 5000)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("30 Slayer Points"),
			new UnlockReward("Access to Sourhog Cave"),
			new UnlockReward("Sourhogs can be assigned as a slayer task by Spria")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Helping Sarah", List.of(
			readNotice,
			talkToSarah,
			useRopeOnHole,
			enterHole,
			investigateSkeleton,
			talkToSpria,
			enterHoleAgain,
			killSourhog,
			cutOffFoot,
			returnToSarah,
			returnToSpria
		), List.of(
			rope,
			slashItem,
			combatGear
		)));

		return sections;
	}
}
