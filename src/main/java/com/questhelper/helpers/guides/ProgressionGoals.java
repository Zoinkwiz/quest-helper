/*
 * Copyright (c) 2024, Zoinkwiz
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
package com.questhelper.helpers.guides;

import com.questhelper.questinfo.QuestHelperQuest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProgressionGoals
{
	// Transportation Goals
	public static final ProgressionGoal GNOME_GLIDERS = new ProgressionGoal(
		"GOAL_GNOME_GLIDERS",
		"Gnome Gliders",
		GoalCategory.TRANSPORTATION,
		"Unlock the gnome glider network",
		"Free teleports to major cities and areas",
		GoalDifficulty.NOVICE,
		Arrays.asList(QuestHelperQuest.THE_GRAND_TREE),
		QuestHelperQuest.THE_GRAND_TREE
	);

	public static final ProgressionGoal FAIRY_RINGS = new ProgressionGoal(
		"GOAL_FAIRY_RINGS",
		"Fairy Rings",
		GoalCategory.TRANSPORTATION,
		"Unlock the fairy ring teleport network",
		"Free teleports to 50+ locations across Gielinor",
		GoalDifficulty.INTERMEDIATE,
		Arrays.asList(QuestHelperQuest.LOST_CITY, QuestHelperQuest.FAIRYTALE_I__GROWING_PAINS, QuestHelperQuest.FAIRYTALE_II__CURE_A_QUEEN),
		QuestHelperQuest.FAIRYTALE_II__CURE_A_QUEEN
	);

	public static final ProgressionGoal SPIRIT_TREES = new ProgressionGoal(
		"GOAL_SPIRIT_TREES",
		"Spirit Trees",
		GoalCategory.TRANSPORTATION,
		"Unlock spirit tree teleportation",
		"Major city teleports and farming access",
		GoalDifficulty.INTERMEDIATE,
		Arrays.asList(QuestHelperQuest.TREE_GNOME_VILLAGE, QuestHelperQuest.THE_GRAND_TREE),
		QuestHelperQuest.THE_GRAND_TREE
	);

	public static final ProgressionGoal ARDOUGNE_TELEPORT = new ProgressionGoal(
		"GOAL_ARDOUGNE_TELEPORT",
		"Ardougne Teleport",
		GoalCategory.TRANSPORTATION,
		"Unlock Ardougne teleport spell",
		"Fast access to Ardougne for diary and clues",
		GoalDifficulty.NOVICE,
		Arrays.asList(QuestHelperQuest.PLAGUE_CITY),
		QuestHelperQuest.PLAGUE_CITY
	);

	public static final ProgressionGoal WATCHTOWER_TELEPORT = new ProgressionGoal(
		"GOAL_WATCHTOWER_TELEPORT",
		"Watchtower Teleport",
		GoalCategory.TRANSPORTATION,
		"Unlock Watchtower teleport spell",
		"Access to Yanille and surrounding areas",
		GoalDifficulty.INTERMEDIATE,
		Arrays.asList(QuestHelperQuest.WATCHTOWER),
		QuestHelperQuest.WATCHTOWER
	);

	// Spellbook Goals
	public static final ProgressionGoal ANCIENT_MAGICKS = new ProgressionGoal(
		"GOAL_ANCIENT_MAGICKS",
		"Ancient Magicks",
		GoalCategory.SPELLBOOKS,
		"Unlock the Ancient spellbook",
		"Powerful combat spells including Ice Barrage",
		GoalDifficulty.ADVANCED,
		Arrays.asList(QuestHelperQuest.DESERT_TREASURE),
		QuestHelperQuest.DESERT_TREASURE
	);

	public static final ProgressionGoal LUNAR_SPELLBOOK = new ProgressionGoal(
		"GOAL_LUNAR_SPELLBOOK",
		"Lunar Spellbook",
		GoalCategory.SPELLBOOKS,
		"Unlock the Lunar spellbook",
		"Utility spells for skilling and support",
		GoalDifficulty.ADVANCED,
		Arrays.asList(QuestHelperQuest.LUNAR_DIPLOMACY),
		QuestHelperQuest.LUNAR_DIPLOMACY
	);

	public static final ProgressionGoal DREAM_SPELLS = new ProgressionGoal(
		"GOAL_DREAM_SPELLS",
		"Dream Spells",
		GoalCategory.SPELLBOOKS,
		"Unlock enhanced Lunar spells",
		"Advanced utility and combat spells",
		GoalDifficulty.ADVANCED,
		Arrays.asList(QuestHelperQuest.LUNAR_DIPLOMACY, QuestHelperQuest.DREAM_MENTOR),
		QuestHelperQuest.DREAM_MENTOR
	);

	// Combat Goals
	public static final ProgressionGoal EARLY_XP_BOOST = new ProgressionGoal(
		"GOAL_EARLY_XP_BOOST",
		"Early XP Boost",
		GoalCategory.COMBAT,
		"Fast early combat levels",
		"Quick progression through early combat levels",
		GoalDifficulty.NOVICE,
		Arrays.asList(QuestHelperQuest.WATERFALL_QUEST, QuestHelperQuest.FIGHT_ARENA),
		QuestHelperQuest.FIGHT_ARENA
	);

	public static final ProgressionGoal DRAGON_SCIMITAR = new ProgressionGoal(
		"GOAL_DRAGON_SCIMITAR",
		"Dragon Scimitar",
		GoalCategory.COMBAT,
		"Best F2P-accessible melee weapon",
		"High attack speed and damage for F2P",
		GoalDifficulty.INTERMEDIATE,
		Arrays.asList(QuestHelperQuest.MONKEY_MADNESS_I),
		QuestHelperQuest.MONKEY_MADNESS_I
	);

	public static final ProgressionGoal RUNE_PLATEBODY = new ProgressionGoal(
		"GOAL_RUNE_PLATEBODY",
		"Rune Platebody",
		GoalCategory.COMBAT,
		"Best F2P defense armor",
		"High defense bonus for F2P",
		GoalDifficulty.NOVICE,
		Arrays.asList(QuestHelperQuest.DRAGON_SLAYER_I),
		QuestHelperQuest.DRAGON_SLAYER_I
	);

	public static final ProgressionGoal AVAS_DEVICE = new ProgressionGoal(
		"GOAL_AVAS_DEVICE",
		"Ava's Device",
		GoalCategory.COMBAT,
		"Auto-pickup ammo device",
		"Automatically collects arrows and bolts",
		GoalDifficulty.INTERMEDIATE,
		Arrays.asList(QuestHelperQuest.ANIMAL_MAGNETISM),
		QuestHelperQuest.ANIMAL_MAGNETISM
	);

	public static final ProgressionGoal PROSELYTE_ARMOR = new ProgressionGoal(
		"GOAL_PROSELYTE_ARMOR",
		"Proselyte Armor",
		GoalCategory.COMBAT,
		"Prayer bonus armor set",
		"High prayer bonus for extended trips",
		GoalDifficulty.INTERMEDIATE,
		Arrays.asList(QuestHelperQuest.THE_SLUG_MENACE),
		QuestHelperQuest.THE_SLUG_MENACE
	);

	public static final ProgressionGoal NEITIZNOT_HELM = new ProgressionGoal(
		"GOAL_NEITIZNOT_HELM",
		"Neitiznot Helm",
		GoalCategory.COMBAT,
		"Best non-degradable helm",
		"High stats without degradation",
		GoalDifficulty.INTERMEDIATE,
		Arrays.asList(QuestHelperQuest.THE_FREMENNIK_ISLES),
		QuestHelperQuest.THE_FREMENNIK_ISLES
	);

	public static final ProgressionGoal BARROWS_GLOVES = new ProgressionGoal(
		"GOAL_BARROWS_GLOVES",
		"Barrows Gloves",
		GoalCategory.COMBAT,
		"Best in slot gloves",
		"+12 to all attack and defense stats",
		GoalDifficulty.ADVANCED,
		Arrays.asList(QuestHelperQuest.RECIPE_FOR_DISASTER_FINALE),
		QuestHelperQuest.RECIPE_FOR_DISASTER_FINALE
	);

	public static final ProgressionGoal PIETY = new ProgressionGoal(
		"GOAL_PIETY",
		"Piety",
		GoalCategory.COMBAT,
		"Best combat prayer",
		"+23% attack, +23% strength, +23% defense",
		GoalDifficulty.ADVANCED,
		Arrays.asList(QuestHelperQuest.KINGS_RANSOM),
		QuestHelperQuest.KINGS_RANSOM
	);

	// Area Unlock Goals
	public static final ProgressionGoal MORYTANIA = new ProgressionGoal(
		"GOAL_MORYTANIA",
		"Morytania",
		GoalCategory.AREA_UNLOCKS,
		"Unlock Morytania region",
		"Access to Slayer Tower, Barrows, Canifis",
		GoalDifficulty.NOVICE,
		Arrays.asList(QuestHelperQuest.PRIEST_IN_PERIL),
		QuestHelperQuest.PRIEST_IN_PERIL
	);

	public static final ProgressionGoal VARLAMORE = new ProgressionGoal(
		"GOAL_VARLAMORE",
		"Varlamore",
		GoalCategory.AREA_UNLOCKS,
		"Unlock Varlamore continent",
		"New continent with unique content",
		GoalDifficulty.INTERMEDIATE,
		Arrays.asList(QuestHelperQuest.CHILDREN_OF_THE_SUN),
		QuestHelperQuest.CHILDREN_OF_THE_SUN
	);

	public static final ProgressionGoal PRIFDDINAS = new ProgressionGoal(
		"GOAL_PRIFDDINAS",
		"Prifddinas",
		GoalCategory.AREA_UNLOCKS,
		"Unlock the Elf City",
		"Best skilling hub in the game",
		GoalDifficulty.ADVANCED,
		Arrays.asList(QuestHelperQuest.SONG_OF_THE_ELVES),
		QuestHelperQuest.SONG_OF_THE_ELVES
	);

	// Skill Unlock Goals
	public static final ProgressionGoal HERBLORE = new ProgressionGoal(
		"GOAL_HERBLORE",
		"Herblore",
		GoalCategory.SKILL_UNLOCKS,
		"Unlock Herblore skill",
		"Make potions for combat and skilling",
		GoalDifficulty.NOVICE,
		Arrays.asList(QuestHelperQuest.DRUIDIC_RITUAL),
		QuestHelperQuest.DRUIDIC_RITUAL
	);

	public static final ProgressionGoal RUNECRAFTING = new ProgressionGoal(
		"GOAL_RUNECRAFTING",
		"Runecrafting",
		GoalCategory.SKILL_UNLOCKS,
		"Unlock Runecrafting skill",
		"Craft runes for magic and profit",
		GoalDifficulty.NOVICE,
		Arrays.asList(QuestHelperQuest.RUNE_MYSTERIES),
		QuestHelperQuest.RUNE_MYSTERIES
	);

	public static final ProgressionGoal MAGIC_SECATEURS = new ProgressionGoal(
		"GOAL_MAGIC_SECATEURS",
		"Magic Secateurs",
		GoalCategory.SKILL_UNLOCKS,
		"Unlock Magic Secateurs",
		"10% bonus farming yield",
		GoalDifficulty.INTERMEDIATE,
		Arrays.asList(QuestHelperQuest.FAIRYTALE_I__GROWING_PAINS),
		QuestHelperQuest.FAIRYTALE_I__GROWING_PAINS
	);

	public static final ProgressionGoal DISEASE_FREE_PATCH = new ProgressionGoal(
		"GOAL_DISEASE_FREE_PATCH",
		"Disease-Free Patch",
		GoalCategory.SKILL_UNLOCKS,
		"Unlock disease-free herb patch",
		"Herb patch at Trollheim that never gets diseased",
		GoalDifficulty.INTERMEDIATE,
		Arrays.asList(QuestHelperQuest.MY_ARMS_BIG_ADVENTURE),
		QuestHelperQuest.MY_ARMS_BIG_ADVENTURE
	);

	// Registry
	private static final List<ProgressionGoal> ALL_GOALS = Arrays.asList(
		// Transportation
		GNOME_GLIDERS, FAIRY_RINGS, SPIRIT_TREES, ARDOUGNE_TELEPORT, WATCHTOWER_TELEPORT,
		// Spellbooks
		ANCIENT_MAGICKS, LUNAR_SPELLBOOK, DREAM_SPELLS,
		// Combat
		EARLY_XP_BOOST, DRAGON_SCIMITAR, RUNE_PLATEBODY, AVAS_DEVICE, PROSELYTE_ARMOR,
		NEITIZNOT_HELM, BARROWS_GLOVES, PIETY,
		// Area Unlocks
		MORYTANIA, VARLAMORE, PRIFDDINAS,
		// Skill Unlocks
		HERBLORE, RUNECRAFTING, MAGIC_SECATEURS, DISEASE_FREE_PATCH
	);

	public static List<ProgressionGoal> getAllGoals()
	{
		return ALL_GOALS;
	}

	public static Map<GoalCategory, List<ProgressionGoal>> getGoalsByCategory()
	{
		return ALL_GOALS.stream()
			.collect(Collectors.groupingBy(ProgressionGoal::getCategory));
	}

	public static ProgressionGoal getGoalById(String id)
	{
		return ALL_GOALS.stream()
			.filter(goal -> goal.getId().equals(id))
			.findFirst()
			.orElse(null);
	}
}
