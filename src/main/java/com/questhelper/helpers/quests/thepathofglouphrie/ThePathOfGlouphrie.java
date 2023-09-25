/*
 * Copyright (c) 2023, pajlada <https://github.com/pajlada>
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
package com.questhelper.helpers.quests.thepathofglouphrie;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_PATH_OF_GLOUPHRIE
)
public class ThePathOfGlouphrie extends BasicQuestHelper
{
	ItemRequirement sampleRequirement;
	Zone sampleZone;
	ZoneRequirement sampleCondition;
	QuestStep sampleStep;

	/// Required items
	private ItemRequirement crossbow;
	private ItemRequirement mithGrapple;
	private ItemRequirement treeGnomeVillageDungeonKey;
	private ItemRequirement combatGear;
	private ItemRequirement food;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep startQuest = new ConditionalStep(this, sampleStep);

		steps.put(0, startQuest);

		return steps;
	}

	public void setupItemRequirements()
	{
		// Required items
		var rovingElvesNotStarted = new QuestRequirement(QuestHelperQuest.ROVING_ELVES, QuestState.NOT_STARTED);
		sampleRequirement = new ItemRequirement("Bucket", ItemID.BUCKET);
		crossbow = new ItemRequirement("Any crossbow", ItemID.CROSSBOW).isNotConsumed();
		crossbow.addAlternates(ItemID.BRONZE_CROSSBOW, ItemID.IRON_CROSSBOW, ItemID.STEEL_CROSSBOW,
			ItemID.MITHRIL_CROSSBOW, ItemID.ADAMANT_CROSSBOW, ItemID.RUNE_CROSSBOW, ItemID.DRAGON_CROSSBOW,
			ItemID.BLURITE_CROSSBOW, ItemID.DORGESHUUN_CROSSBOW, ItemID.ARMADYL_CROSSBOW, ItemID.ZARYTE_CROSSBOW);
		mithGrapple = new ItemRequirement("Mith grapple", ItemID.MITH_GRAPPLE_9419).isNotConsumed();
		treeGnomeVillageDungeonKey = new ItemRequirement("Tree Gnome Village dungeon key", ItemID.KEY_293).showConditioned(rovingElvesNotStarted);
		treeGnomeVillageDungeonKey.canBeObtainedDuringQuest();
		combatGear = new ItemRequirement("Combat equipment", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
	}

	public void setupZones()
	{
		sampleZone = new Zone(new WorldPoint(10, 10, 0), new WorldPoint(20, 20, 0));
	}

	public void setupConditions()
	{
		sampleCondition = new ZoneRequirement(sampleZone);
	}

	public void setupSteps()
	{
		sampleStep = new DetailedQuestStep(this, "Talk to Hans in Lumbridge", sampleRequirement);
	}

	@Override
	public void setupRequirements()
	{

	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(
			crossbow,
			mithGrapple,
			treeGnomeVillageDungeonKey,
			combatGear,
			food
		);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Arrays.asList());
	}

	@Override
	public ArrayList<String> getNotes()
	{
		return new ArrayList<>(Arrays.asList("This is a note to appear in the sidebar"));
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList(
			"3 Warped Terrorbirds (level 138)",
			"Evil Creature (level 1)"
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("The section's header!", new ArrayList<>(Arrays.asList(sampleStep)), sampleRequirement));
		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();

		req.add(new QuestRequirement(QuestHelperQuest.THE_EYES_OF_GLOUPHRIE, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.WATERFALL_QUEST, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.TREE_GNOME_VILLAGE, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.STRENGTH, 60));
		req.add(new SkillRequirement(Skill.SLAYER, 56));
		req.add(new SkillRequirement(Skill.THIEVING, 56));
		req.add(new SkillRequirement(Skill.RANGED, 47));
		req.add(new SkillRequirement(Skill.AGILITY, 45));

		return req;
	}
}
