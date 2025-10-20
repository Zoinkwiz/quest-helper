/*
 * Copyright (c) 2025, Zoinkwiz <https://github.com/Zoinkwiz>
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
import net.runelite.api.gameval.SpriteID;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UnlockRegistry
{
	// Transportation Unlocks
	public static final Unlock GNOME_GLIDERS = new Unlock(
		"UNLOCK_GNOME_GLIDERS",
		"Gnome Gliders",
		UnlockCategory.TRANSPORTATION,
		"Free teleports to major cities",
		SpriteID.Staticons.PRAYER,
		Arrays.asList(QuestHelperQuest.THE_GRAND_TREE)
	);

	public static final Unlock GNOME_GLIDERS_2 = new Unlock(
		"UNLOCK_GNOME_GLIDERS",
		"Gnome Gliders",
		UnlockCategory.TRANSPORTATION,
		"Free teleports to major cities",
		SpriteID.Staticons.PRAYER,
		Arrays.asList(QuestHelperQuest.THE_GRAND_TREE)
	);

	public static final Unlock GNOME_GLIDERS_3 = new Unlock(
		"UNLOCK_GNOME_GLIDERS",
		"Gnome Gliders",
		UnlockCategory.TRANSPORTATION,
		"Free teleports to major cities",
		SpriteID.Staticons.PRAYER,
		Arrays.asList(QuestHelperQuest.THE_GRAND_TREE)
	);

	public static final Unlock GNOME_GLIDERS_4 = new Unlock(
		"UNLOCK_GNOME_GLIDERS",
		"Gnome Gliders",
		UnlockCategory.TRANSPORTATION,
		"Free teleports to major cities",
		SpriteID.Staticons.PRAYER,
		Arrays.asList(QuestHelperQuest.THE_GRAND_TREE)
	);

	public static final Unlock GNOME_GLIDERS_5 = new Unlock(
		"UNLOCK_GNOME_GLIDERS",
		"Gnome Gliders",
		UnlockCategory.TRANSPORTATION,
		"Free teleports to major cities",
		SpriteID.Staticons.PRAYER,
		Arrays.asList(QuestHelperQuest.THE_GRAND_TREE)
	);
	public static final Unlock FAIRY_RINGS = new Unlock(
		"UNLOCK_FAIRY_RINGS",
		"Fairy Rings",
		UnlockCategory.TRANSPORTATION,
		"Network of 50+ teleport locations",
		SpriteID.Staticons.PRAYER,
		Arrays.asList(
			QuestHelperQuest.LOST_CITY,
			QuestHelperQuest.FAIRYTALE_I__GROWING_PAINS,
			QuestHelperQuest.FAIRYTALE_II__CURE_A_QUEEN
		)
	);

	public static final Unlock SPIRIT_TREES = new Unlock(
		"UNLOCK_SPIRIT_TREES",
		"Spirit Trees",
		UnlockCategory.TRANSPORTATION,
		"Major city teleports and farming access",
		SpriteID.Staticons.PRAYER,
		Arrays.asList(
			QuestHelperQuest.TREE_GNOME_VILLAGE,
			QuestHelperQuest.THE_GRAND_TREE
		)
	);

	public static final Unlock ARDOUGNE_TELEPORT = new Unlock(
		"UNLOCK_ARDOUGNE_TELEPORT",
		"Ardougne Teleport",
		UnlockCategory.TRANSPORTATION,
		"Fast access to Ardougne for diary and clues",
		SpriteID.Staticons.PRAYER,
		Arrays.asList(QuestHelperQuest.PLAGUE_CITY)
	);

	public static final Unlock WATCHTOWER_TELEPORT = new Unlock(
		"UNLOCK_WATCHTOWER_TELEPORT",
		"Watchtower Teleport",
		UnlockCategory.TRANSPORTATION,
		"Access to Yanille and surrounding areas",
		SpriteID.Staticons.PRAYER,
		Arrays.asList(QuestHelperQuest.WATCHTOWER)
	);

	// Combat Unlocks
	public static final Unlock EARLY_XP_BOOST = new Unlock(
		"UNLOCK_EARLY_XP_BOOST",
		"Early XP Boost",
		UnlockCategory.COMBAT_UNLOCKS,
		"Fast early combat levels",
		SpriteID.Staticons.ATTACK,
		Arrays.asList(
			QuestHelperQuest.WATERFALL_QUEST,
			QuestHelperQuest.FIGHT_ARENA
		)
	);

	public static final Unlock DRAGON_SCIMITAR = new Unlock(
		"UNLOCK_DRAGON_SCIMITAR",
		"Dragon Scimitar",
		UnlockCategory.COMBAT_UNLOCKS,
		"Best F2P-accessible melee weapon",
		SpriteID.Staticons.ATTACK,
		Arrays.asList(QuestHelperQuest.MONKEY_MADNESS_I)
	);

	public static final Unlock RUNE_PLATEBODY = new Unlock(
		"UNLOCK_RUNE_PLATEBODY",
		"Rune Platebody",
		UnlockCategory.COMBAT_UNLOCKS,
		"Best F2P defense armor",
		SpriteID.Staticons.DEFENCE,
		Arrays.asList(QuestHelperQuest.DRAGON_SLAYER_I)
	);

	public static final Unlock AVAS_DEVICE = new Unlock(
		"UNLOCK_AVAS_DEVICE",
		"Ava's Device",
		UnlockCategory.COMBAT_UNLOCKS,
		"Auto-pickup ammo device",
		SpriteID.Staticons.RANGED,
		Arrays.asList(QuestHelperQuest.ANIMAL_MAGNETISM)
	);

	public static final Unlock PROSELYTE_ARMOR = new Unlock(
		"UNLOCK_PROSELYTE_ARMOR",
		"Proselyte Armor",
		UnlockCategory.COMBAT_UNLOCKS,
		"Prayer bonus armor set",
		SpriteID.Staticons.PRAYER,
		Arrays.asList(QuestHelperQuest.THE_SLUG_MENACE)
	);

	public static final Unlock NEITIZNOT_HELM = new Unlock(
		"UNLOCK_NEITIZNOT_HELM",
		"Neitiznot Helm",
		UnlockCategory.COMBAT_UNLOCKS,
		"Best non-degradable helm",
		SpriteID.Staticons.DEFENCE,
		Arrays.asList(QuestHelperQuest.THE_FREMENNIK_ISLES)
	);

	public static final Unlock BARROWS_GLOVES = new Unlock(
		"UNLOCK_BARROWS_GLOVES",
		"Barrows Gloves",
		UnlockCategory.COMBAT_UNLOCKS,
		"Best in slot gloves",
		SpriteID.Staticons.ATTACK,
		Arrays.asList(QuestHelperQuest.RECIPE_FOR_DISASTER_FINALE)
	);

	public static final Unlock PIETY = new Unlock(
		"UNLOCK_PIETY",
		"Piety",
		UnlockCategory.COMBAT_UNLOCKS,
		"Best combat prayer",
		SpriteID.Staticons.PRAYER,
		Arrays.asList(QuestHelperQuest.KINGS_RANSOM)
	);

	// Area Unlocks
	public static final Unlock MORYTANIA = new Unlock(
		"UNLOCK_MORYTANIA",
		"Morytania",
		UnlockCategory.AREA_UNLOCKS,
		"Access to Slayer Tower, Barrows, Canifis",
		SpriteID.Staticons.PRAYER,
		Arrays.asList(QuestHelperQuest.PRIEST_IN_PERIL)
	);

	public static final Unlock VARLAMORE = new Unlock(
		"UNLOCK_VARLAMORE",
		"Varlamore",
		UnlockCategory.AREA_UNLOCKS,
		"New continent with unique content",
		SpriteID.Staticons.PRAYER,
		Arrays.asList(QuestHelperQuest.CHILDREN_OF_THE_SUN)
	);

	public static final Unlock PRIFDDINAS = new Unlock(
		"UNLOCK_PRIFDDINAS",
		"Prifddinas",
		UnlockCategory.AREA_UNLOCKS,
		"Best skilling hub in the game",
		SpriteID.Staticons.PRAYER,
		Arrays.asList(QuestHelperQuest.SONG_OF_THE_ELVES)
	);

	// Skill Unlocks
	public static final Unlock HERBLORE = new Unlock(
		"UNLOCK_HERBLORE",
		"Herblore",
		UnlockCategory.SKILL_UNLOCKS,
		"Make potions for combat and skilling",
		SpriteID.Staticons.HERBLORE,
		Arrays.asList(QuestHelperQuest.DRUIDIC_RITUAL)
	);

	public static final Unlock RUNECRAFTING = new Unlock(
		"UNLOCK_RUNECRAFTING",
		"Runecrafting",
		UnlockCategory.SKILL_UNLOCKS,
		"Craft runes for magic and profit",
		SpriteID.Staticons.MAGIC,
		Arrays.asList(QuestHelperQuest.RUNE_MYSTERIES)
	);

	public static final Unlock MAGIC_SECATEURS = new Unlock(
		"UNLOCK_MAGIC_SECATEURS",
		"Magic Secateurs",
		UnlockCategory.SKILL_UNLOCKS,
		"10% bonus farming yield",
		SpriteID.Staticons.PRAYER,
		Arrays.asList(QuestHelperQuest.FAIRYTALE_I__GROWING_PAINS)
	);

	public static final Unlock DISEASE_FREE_PATCH = new Unlock(
		"UNLOCK_DISEASE_FREE_PATCH",
		"Disease-Free Patch",
		UnlockCategory.SKILL_UNLOCKS,
		"Herb patch at Trollheim that never gets diseased",
		SpriteID.Staticons.PRAYER,
		Arrays.asList(QuestHelperQuest.MY_ARMS_BIG_ADVENTURE)
	);

	// Spellbooks
	public static final Unlock ANCIENT_MAGICKS = new Unlock(
		"UNLOCK_ANCIENT_MAGICKS",
		"Ancient Magicks",
		UnlockCategory.SPELLBOOKS,
		"Powerful combat spells including Ice Barrage",
		SpriteID.Staticons.MAGIC,
		Arrays.asList(QuestHelperQuest.DESERT_TREASURE)
	);

	public static final Unlock LUNAR_SPELLBOOK = new Unlock(
		"UNLOCK_LUNAR_SPELLBOOK",
		"Lunar Spellbook",
		UnlockCategory.SPELLBOOKS,
		"Utility spells for skilling and support",
		SpriteID.Staticons.MAGIC,
		Arrays.asList(QuestHelperQuest.LUNAR_DIPLOMACY)
	);

	public static final Unlock DREAM_SPELLS = new Unlock(
		"UNLOCK_DREAM_SPELLS",
		"Dream Spells",
		UnlockCategory.SPELLBOOKS,
		"Advanced utility and combat spells",
		SpriteID.Staticons.MAGIC,
		Arrays.asList(
			QuestHelperQuest.LUNAR_DIPLOMACY,
			QuestHelperQuest.DREAM_MENTOR
		)
	);

	// Registry
	private static final List<Unlock> ALL_UNLOCKS = Arrays.asList(
		// Transportation
		GNOME_GLIDERS, FAIRY_RINGS, SPIRIT_TREES, ARDOUGNE_TELEPORT, WATCHTOWER_TELEPORT,
		// Combat Unlocks
		EARLY_XP_BOOST, DRAGON_SCIMITAR, RUNE_PLATEBODY, AVAS_DEVICE, PROSELYTE_ARMOR,
		NEITIZNOT_HELM, BARROWS_GLOVES, PIETY,
		// Area Unlocks
		MORYTANIA, VARLAMORE, PRIFDDINAS,
		// Skill Unlocks
		HERBLORE, RUNECRAFTING, MAGIC_SECATEURS, DISEASE_FREE_PATCH,
		// Spellbooks
		ANCIENT_MAGICKS, LUNAR_SPELLBOOK, DREAM_SPELLS
	);

	public static List<Unlock> getAllUnlocks()
	{
		return ALL_UNLOCKS;
	}

	public static Map<UnlockCategory, List<Unlock>> getUnlocksByCategory()
	{
		return ALL_UNLOCKS.stream()
			.collect(Collectors.groupingBy(Unlock::getCategory));
	}

	public static Unlock getUnlockById(String id)
	{
		return ALL_UNLOCKS.stream()
			.filter(unlock -> unlock.getId().equals(id))
			.findFirst()
			.orElse(null);
	}
}
