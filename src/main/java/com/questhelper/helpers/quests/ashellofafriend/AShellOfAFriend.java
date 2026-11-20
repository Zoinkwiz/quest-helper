/*
 * Copyright (c) 2026, eindiran <https://github.com/eindiran>
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
package com.questhelper.helpers.quests.ashellofafriend;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;

public class AShellOfAFriend extends BasicQuestHelper
{
	// RuneLite has no ObjectID constant for the tree yet
	private static final int SHELTER_TREE = 33159;

	// Required items
	ItemRequirement axe;

	// Recommended items
	ItemRequirement combatGear;
	ItemRequirement food;
	ItemRequirement hauntedWoodsTeleport;

	// Mid-quest item requirements
	ItemRequirement freshLeaves;
	ItemRequirement dewCoveredBranches;
	ItemRequirement restorativeDew;
	ItemRequirement strongSticks;

	// Miscellaneous requirements
	NpcRequirement leechNearby;

	// Steps
	NpcStep talkToSnail;

	ItemStep gatherLeaves;
	NpcStep killLeech;
	NpcStep feedSnail;

	ItemStep gatherBranches;
	DetailedQuestStep stripBranches;
	NpcStep healSnail;
	NpcStep nameSnail;

	ItemStep pickUpAxe;
	ItemStep gatherSticks;
	ObjectStep buildShelter;
	NpcStep talkToSnailAfterShelter;

	ItemStep gatherMoreLeaves;
	NpcStep confrontSnelmLover;
	NpcStep killSnelmLover;

	@Override
	protected void setupRequirements()
	{
		axe = new ItemRequirement("Any axe", ItemCollections.AXES).isNotConsumed().canBeObtainedDuringQuest();

		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		hauntedWoodsTeleport = new ItemRequirement("Fairy ring (ALQ), Fenkenstrain's Castle teleport, or Ectophial", ItemID.TELETAB_FENK);
		hauntedWoodsTeleport.addAlternates(ItemID.ECTOPHIAL);
		hauntedWoodsTeleport.addAlternates(ItemCollections.FAIRY_STAFF);

		freshLeaves = new ItemRequirement("Fresh leaves", ItemID.SNAILQUEST_FOOD, 5);
		dewCoveredBranches = new ItemRequirement("Dew-covered branch", ItemID.SNAILQUEST_HEALING_BRANCH);
		restorativeDew = new ItemRequirement("Restorative dew", ItemID.SNAILQUEST_HEALING_DEW, 10);
		strongSticks = new ItemRequirement("Strong sticks", ItemID.SNAILQUEST_SHELTER, 5);

		leechNearby = new NpcRequirement(NpcID.SNAILQUEST_LEECH);
	}

	public void setupSteps()
	{
		var snailTile = new WorldPoint(3564, 3493, 0);
		var leafTiles = new WorldPoint[]{
			new WorldPoint(3573, 3502, 0),
			new WorldPoint(3571, 3506, 0),
			new WorldPoint(3575, 3508, 0),
			new WorldPoint(3579, 3511, 0),
			new WorldPoint(3573, 3513, 0)
		};

		talkToSnail = new NpcStep(this, NpcID.SNAILQUEST_SNAIL, snailTile, "Talk to the Juvenile Snail on the small island in the Haunted Woods, west of fairy ring ALQ. Cross the stepping stone south-east of the island to reach it.");
		talkToSnail.addTeleport(hauntedWoodsTeleport);

		gatherLeaves = new ItemStep(this, leafTiles[0], "Take 5 fresh leaves from the ground north-east of the island.", freshLeaves);
		gatherLeaves.addTileMarkers(leafTiles);
		killLeech = new NpcStep(this, NpcID.SNAILQUEST_LEECH, snailTile, "Return to the island and kill the leech attacking the snail. The snail cannot die.", freshLeaves);
		feedSnail = new NpcStep(this, NpcID.SNAILQUEST_SNAIL, snailTile, "Talk to the snail to feed it the fresh leaves.", freshLeaves);

		gatherBranches = new ItemStep(this, new WorldPoint(3573, 3485, 0), "Take 5 dew-covered branches from the ground south-east of the island.", dewCoveredBranches.quantity(5));
		gatherBranches.addTileMarkers(new WorldPoint(3573, 3478, 0), new WorldPoint(3576, 3482, 0), new WorldPoint(3575, 3483, 0), new WorldPoint(3573, 3485, 0), new WorldPoint(3574, 3486, 0));
		stripBranches = new DetailedQuestStep(this, "Strip the dew-covered branches to get 10 restorative dew.", dewCoveredBranches.highlighted());
		healSnail = new NpcStep(this, NpcID.SNAILQUEST_SNAIL, snailTile, "Talk to the snail to heal it with the restorative dew.", restorativeDew);
		nameSnail = new NpcStep(this, NpcID.SNAILQUEST_SNAIL, snailTile, "Talk to the snail and pick any name for it.");
		healSnail.addSubSteps(nameSnail);

		pickUpAxe = new ItemStep(this, new WorldPoint(3585, 3496, 0), "Take the bronze axe east of the island if you did not bring an axe.", axe);
		gatherSticks = new ItemStep(this, new WorldPoint(3580, 3488, 0), "Take 5 strong sticks from the ground east of the island.", strongSticks);
		gatherSticks.addTileMarkers(new WorldPoint(3581, 3479, 0), new WorldPoint(3581, 3483, 0), new WorldPoint(3583, 3486, 0), new WorldPoint(3580, 3488, 0), new WorldPoint(3582, 3497, 0));
		buildShelter = new ObjectStep(this, SHELTER_TREE, new WorldPoint(3562, 3491, 0), "Build the shelter in the tree on the island.", strongSticks, axe);
		talkToSnailAfterShelter = new NpcStep(this, NpcID.SNAILQUEST_SNAIL, snailTile, "Talk to the snail.");

		gatherMoreLeaves = new ItemStep(this, leafTiles[0], "Take 5 more fresh leaves from the ground north-east of the island.", freshLeaves);
		gatherMoreLeaves.addTileMarkers(leafTiles);
		confrontSnelmLover = new NpcStep(this, NpcID.SNAILQUEST_SNELMLOVER, new WorldPoint(3564, 3492, 0), "Return to the island to confront SnelmLover500.", combatGear);
		killSnelmLover = new NpcStep(this, NpcID.SNAILQUEST_SNELMLOVER_COMBAT, new WorldPoint(3564, 3492, 0), "Kill SnelmLover500.", combatGear);
		killSnelmLover.addAlternateNpcs(NpcID.SNAILQUEST_SNELMLOVER);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToSnail);

		// The leech appears when the player returns to the island with the leaves
		var cKillLeech = new ConditionalStep(this, gatherLeaves);
		cKillLeech.addStep(or(leechNearby, freshLeaves), killLeech);
		steps.put(2, cKillLeech);

		steps.put(4, feedSnail);

		var cHealSnail = new ConditionalStep(this, gatherBranches);
		cHealSnail.addStep(restorativeDew, healSnail);
		cHealSnail.addStep(dewCoveredBranches.quantity(5), stripBranches);
		steps.put(6, cHealSnail);

		steps.put(8, nameSnail);
		steps.put(10, nameSnail);

		var cBuildShelter = new ConditionalStep(this, pickUpAxe);
		cBuildShelter.addStep(and(axe, strongSticks), buildShelter);
		cBuildShelter.addStep(axe, gatherSticks);
		steps.put(12, cBuildShelter);

		steps.put(14, talkToSnailAfterShelter);
		steps.put(16, talkToSnailAfterShelter);

		steps.put(18, gatherMoreLeaves);
		steps.put(20, confrontSnelmLover);
		steps.put(22, killSnelmLover);

		return steps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.PRIEST_IN_PERIL, QuestState.FINISHED),
			new SkillRequirement(Skill.HERBLORE, 16),
			new SkillRequirement(Skill.CONSTRUCTION, 15)
		);
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		return List.of(
			new CombatLevelRequirement(40)
		);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			axe
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			combatGear,
			food,
			hauntedWoodsTeleport
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Leech (level 39)",
			"SnelmLover500 (level 50)"
		);
	}

	@Override
	public List<String> getNotes()
	{
		return List.of(
			"Feral vampyres in the Haunted Woods are aggressive throughout the quest."
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
			new ExperienceReward(Skill.HERBLORE, 2500),
			new ExperienceReward(Skill.CONSTRUCTION, 2500)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Pink snelm", ItemID.SNAILQUEST_SNELM)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Feeding the snail", List.of(
			talkToSnail,
			gatherLeaves,
			killLeech,
			feedSnail
		), List.of(
			combatGear,
			food
		)));

		sections.add(new PanelDetails("Healing the snail", List.of(
			gatherBranches,
			stripBranches,
			healSnail
		)));

		sections.add(new PanelDetails("Building a shelter", List.of(
			pickUpAxe,
			gatherSticks,
			buildShelter,
			talkToSnailAfterShelter
		), List.of(
			axe
		)));

		sections.add(new PanelDetails("A parting gift", List.of(
			gatherMoreLeaves,
			confrontSnelmLover,
			killSnelmLover
		), List.of(
			combatGear,
			food
		)));

		return sections;
	}
}
