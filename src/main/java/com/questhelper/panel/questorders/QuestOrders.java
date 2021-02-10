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
package com.questhelper.panel.questorders;

import com.google.common.collect.ImmutableList;
import com.questhelper.QuestHelperQuest;
import com.questhelper.questhelpers.QuestHelper;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import lombok.Getter;

public class QuestOrders
{
	// Test for 'The', 'A', 'An' at the start of a string followed by a word boundary (space/tab); this ignores case sensitivity
	private static final Pattern QUEST_NAME_PATTERN = Pattern.compile("(?i)(a\\b)|(the\\b)|(an\\b)", Pattern.CASE_INSENSITIVE);
	@Getter
	private static final List<QuestHelperQuest> optimalOrder = ImmutableList.of(
		QuestHelperQuest.COOKS_ASSISTANT,
		QuestHelperQuest.X_MARKS_THE_SPOT,
		QuestHelperQuest.THE_RESTLESS_GHOST,
		QuestHelperQuest.RUNE_MYSTERIES,
		QuestHelperQuest.IMP_CATCHER,
		QuestHelperQuest.WITCHS_POTION,
		QuestHelperQuest.CLIENT_OF_KOUREND,
		QuestHelperQuest.DWARF_CANNON,
		QuestHelperQuest.WATERFALL_QUEST,
		QuestHelperQuest.TREE_GNOME_VILLAGE,
		QuestHelperQuest.MONKS_FRIEND,
		QuestHelperQuest.HAZEEL_CULT,
		QuestHelperQuest.MURDER_MYSTERY,
		QuestHelperQuest.MERLINS_CRYSTAL,
		QuestHelperQuest.HOLY_GRAIL,
		QuestHelperQuest.DRUIDIC_RITUAL,
		QuestHelperQuest.WITCHS_HOUSE,
		QuestHelperQuest.BLACK_KNIGHTS_FORTRESS,
		QuestHelperQuest.RECRUITMENT_DRIVE,
		QuestHelperQuest.OBSERVATORY_QUEST,
		QuestHelperQuest.PRIEST_IN_PERIL,
		QuestHelperQuest.RAG_AND_BONE_MAN_I,
		QuestHelperQuest.NATURE_SPIRIT,
		QuestHelperQuest.ALFRED_GRIMHANDS_BARCRAWL,
		QuestHelperQuest.SCORPION_CATCHER,
		QuestHelperQuest.PLAGUE_CITY,
		QuestHelperQuest.BIOHAZARD,
		QuestHelperQuest.FIGHT_ARENA,
		QuestHelperQuest.GERTRUDES_CAT,
		QuestHelperQuest.JUNGLE_POTION,
		QuestHelperQuest.VAMPYRE_SLAYER,
		QuestHelperQuest.A_PORCINE_OF_INTEREST,
		QuestHelperQuest.DEATH_PLATEAU,
		QuestHelperQuest.GOBLIN_DIPLOMACY,
		QuestHelperQuest.THE_QUEEN_OF_THIEVES,
		QuestHelperQuest.THE_DEPTHS_OF_DESPAIR,
		QuestHelperQuest.MOUNTAIN_DAUGHTER,
		QuestHelperQuest.ICTHLARINS_LITTLE_HELPER,
		QuestHelperQuest.THE_GRAND_TREE,
		QuestHelperQuest.TRIBAL_TOTEM,
		QuestHelperQuest.THE_DIG_SITE,
		QuestHelperQuest.THE_GOLEM,
		QuestHelperQuest.THE_KNIGHTS_SWORD,
		QuestHelperQuest.ELEMENTAL_WORKSHOP_I,
		QuestHelperQuest.RECIPE_FOR_DISASTER_START,
		QuestHelperQuest.RECIPE_FOR_DISASTER_WARTFACE_AND_BENTNOZE,
		QuestHelperQuest.DEMON_SLAYER,
		QuestHelperQuest.SHADOW_OF_THE_STORM,
		QuestHelperQuest.ELEMENTAL_WORKSHOP_II,
		QuestHelperQuest.LOST_CITY,
		QuestHelperQuest.FAIRYTALE_I__GROWING_PAINS,
		QuestHelperQuest.SHIELD_OF_ARRAV_BLACK_ARM_GANG,
		QuestHelperQuest.SHIELD_OF_ARRAV_PHOENIX_GANG,
		QuestHelperQuest.CREATURE_OF_FENKENSTRAIN,
		QuestHelperQuest.A_SOULS_BANE,
		QuestHelperQuest.THE_LOST_TRIBE,
		QuestHelperQuest.DEATH_TO_THE_DORGESHUUN,
		QuestHelperQuest.THE_GIANT_DWARF,
		QuestHelperQuest.ANOTHER_SLICE_OF_HAM,
		QuestHelperQuest.MAKING_HISTORY,
		QuestHelperQuest.IN_SEARCH_OF_THE_MYREQUE,
		QuestHelperQuest.SHADES_OF_MORTTON,
		QuestHelperQuest.IN_AID_OF_THE_MYREQUE,
		QuestHelperQuest.BONE_VOYAGE,
		QuestHelperQuest.ENTER_THE_ABYSS,
		QuestHelperQuest.WANTED,
		QuestHelperQuest.THE_FEUD,
		QuestHelperQuest.TROLL_STRONGHOLD,
		QuestHelperQuest.TROLL_ROMANCE,
		QuestHelperQuest.DRAGON_SLAYER_I,
		QuestHelperQuest.HORROR_FROM_THE_DEEP,
		QuestHelperQuest.ERNEST_THE_CHICKEN,
		QuestHelperQuest.ANIMAL_MAGNETISM,
		QuestHelperQuest.SHILO_VILLAGE,
		QuestHelperQuest.DORICS_QUEST,
		QuestHelperQuest.SPIRITS_OF_THE_ELID,
		QuestHelperQuest.DADDYS_HOME,
		QuestHelperQuest.DARKNESS_OF_HALLOWVALE,
		QuestHelperQuest.TOWER_OF_LIFE,
		QuestHelperQuest.FISHING_CONTEST,
		QuestHelperQuest.RECIPE_FOR_DISASTER_DWARF,
		QuestHelperQuest.GHOSTS_AHOY,
		QuestHelperQuest.FORGETTABLE_TALE,
		QuestHelperQuest.GARDEN_OF_TRANQUILLITY,
		QuestHelperQuest.ENLIGHTENED_JOURNEY,
		QuestHelperQuest.RECIPE_FOR_DISASTER_EVIL_DAVE,
		QuestHelperQuest.BIG_CHOMPY_BIRD_HUNTING,
		QuestHelperQuest.ZOGRE_FLESH_EATERS,
		QuestHelperQuest.RECIPE_FOR_DISASTER_PIRATE_PETE,
		QuestHelperQuest.TAI_BWO_WANNAI_TRIO,
		QuestHelperQuest.THE_TOURIST_TRAP,
		QuestHelperQuest.EADGARS_RUSE,
		QuestHelperQuest.MY_ARMS_BIG_ADVENTURE,
		QuestHelperQuest.THE_FREMENNIK_TRIALS,
		QuestHelperQuest.THE_FREMENNIK_ISLES,
		QuestHelperQuest.RECIPE_FOR_DISASTER_LUMBRIDGE_GUIDE,
		QuestHelperQuest.RECIPE_FOR_DISASTER_SKRACH_UGLOGWEE,
		QuestHelperQuest.HAUNTED_MINE,
		QuestHelperQuest.WATCHTOWER,
		QuestHelperQuest.PRINCE_ALI_RESCUE,
		QuestHelperQuest.CONTACT,
		QuestHelperQuest.THE_EYES_OF_GLOUPHRIE,
		QuestHelperQuest.SEA_SLUG,
		QuestHelperQuest.OLAFS_QUEST,
		QuestHelperQuest.TEARS_OF_GUTHIX,
		QuestHelperQuest.RATCATCHERS,
		QuestHelperQuest.TEMPLE_OF_IKOV,
		QuestHelperQuest.ONE_SMALL_FAVOUR,
		QuestHelperQuest.A_TAIL_OF_TWO_CATS,
		QuestHelperQuest.BETWEEN_A_ROCK,
		QuestHelperQuest.THE_SLUG_MENACE,
		QuestHelperQuest.MONKEY_MADNESS_I,
		QuestHelperQuest.GETTING_AHEAD,
		QuestHelperQuest.COLD_WAR,
		QuestHelperQuest.THE_ASCENT_OF_ARCEUUS,
		QuestHelperQuest.EAGLES_PEAK,
		QuestHelperQuest.UNDERGROUND_PASS,
		QuestHelperQuest.SKIPPY_AND_THE_MOGRES,
		QuestHelperQuest.RAG_AND_BONE_MAN_II,
		QuestHelperQuest.LAIR_OF_TARN_RAZORLOR,
		QuestHelperQuest.RUM_DEAL,
		QuestHelperQuest.SHEEP_SHEARER,
		QuestHelperQuest.MISTHALIN_MYSTERY,
		QuestHelperQuest.PIRATES_TREASURE,
		QuestHelperQuest.CABIN_FEVER,
		QuestHelperQuest.THE_GREAT_BRAIN_ROBBERY,
		QuestHelperQuest.THE_HAND_IN_THE_SAND,
		QuestHelperQuest.ENAKHRAS_LAMENT,
		QuestHelperQuest.HEROES_QUEST,
		QuestHelperQuest.THRONE_OF_MISCELLANIA,
		QuestHelperQuest.ROYAL_TROUBLE,
		QuestHelperQuest.DESERT_TREASURE,
		QuestHelperQuest.CURSE_OF_THE_EMPTY_LORD,
		QuestHelperQuest.THE_GENERALS_SHADOW,
		QuestHelperQuest.A_TASTE_OF_HOPE,
		QuestHelperQuest.FAMILY_CREST,
		QuestHelperQuest.LEGENDS_QUEST,
		QuestHelperQuest.RECIPE_FOR_DISASTER_SIR_AMIK_VARZE,
		QuestHelperQuest.FAIRYTALE_II__CURE_A_QUEEN,
		QuestHelperQuest.RECIPE_FOR_DISASTER_MONKEY_AMBASSADOR,
		QuestHelperQuest.REGICIDE,
		QuestHelperQuest.SHEEP_HERDER,
		QuestHelperQuest.ROVING_ELVES,
		QuestHelperQuest.MOURNINGS_END_PART_I,
		QuestHelperQuest.MOURNINGS_END_PART_II,
		QuestHelperQuest.LUNAR_DIPLOMACY,
		QuestHelperQuest.WHAT_LIES_BELOW,
		QuestHelperQuest.KINGS_RANSOM,
		QuestHelperQuest.SWAN_SONG,
		QuestHelperQuest.RECIPE_FOR_DISASTER_FINALE,
		QuestHelperQuest.GRIM_TALES,
		QuestHelperQuest.DREAM_MENTOR,
		QuestHelperQuest.TALE_OF_THE_RIGHTEOUS,
		QuestHelperQuest.THE_FORSAKEN_TOWER,
		QuestHelperQuest.DEVIOUS_MINDS,
		QuestHelperQuest.THE_FREMENNIK_EXILES,
		QuestHelperQuest.SINS_OF_THE_FATHER,
		QuestHelperQuest.ROMEO__JULIET,
		QuestHelperQuest.MAKING_FRIENDS_WITH_MY_ARM,
		QuestHelperQuest.MONKEY_MADNESS_II,
		QuestHelperQuest.DRAGON_SLAYER_II,
		QuestHelperQuest.SONG_OF_THE_ELVES,
		QuestHelperQuest.THE_CORSAIR_CURSE,
		QuestHelperQuest.CLOCK_TOWER,
		QuestHelperQuest.BEAR_YOUR_SOUL,
		QuestHelperQuest.ARCHITECTURAL_ALLIANCE,
		QuestHelperQuest.ENCHANTED_KEY,
		QuestHelperQuest.FAMILY_PEST,
		QuestHelperQuest.IN_SEARCH_OF_KNOWLEDGE,
		QuestHelperQuest.THE_MAGE_ARENA,
		QuestHelperQuest.THE_MAGE_ARENA_II
	);

	public static String normalizeQuestName(String questName)
	{
		return QUEST_NAME_PATTERN.matcher(questName).replaceAll("").trim();
	}

	public static Comparator<QuestHelper> sortOptimalOrder()
	{
		return Comparator.comparing(q -> getOptimalOrder().indexOf(q.getQuest()));
	}

	public static Comparator<QuestHelper> sortAToZ()
	{
		return Comparator.comparing(q -> normalizeQuestName(q.getQuest().getName()));
	}

	public static Comparator<QuestHelper> sortZToA()
	{
		return Comparator.comparing(q -> normalizeQuestName(q.getQuest().getName()), Comparator.reverseOrder());
	}
}
