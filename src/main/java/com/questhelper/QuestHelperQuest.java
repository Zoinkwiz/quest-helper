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
package com.questhelper;

import com.questhelper.questhelpers.QuestDetails;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import net.runelite.api.*;

public enum QuestHelperQuest
{
	//Free Quests
	BELOW_ICE_MOUNTAIN(Quest.BELOW_ICE_MOUNTAIN, QuestVarbits.QUEST_BELOW_ICE_MOUNTAIN, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	BLACK_KNIGHTS_FORTRESS(Quest.BLACK_KNIGHTS_FORTRESS, QuestVarPlayer.QUEST_BLACK_KNIGHTS_FORTRESS, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	COOKS_ASSISTANT(Quest.COOKS_ASSISTANT, QuestVarPlayer.QUEST_COOKS_ASSISTANT, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	THE_CORSAIR_CURSE(Quest.THE_CORSAIR_CURSE, QuestVarbits.QUEST_THE_CORSAIR_CURSE, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	DEMON_SLAYER(Quest.DEMON_SLAYER, QuestVarbits.QUEST_DEMON_SLAYER, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	DORICS_QUEST(Quest.DORICS_QUEST, QuestVarPlayer.QUEST_DORICS_QUEST, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	DRAGON_SLAYER_I(Quest.DRAGON_SLAYER_I, QuestVarPlayer.QUEST_DRAGON_SLAYER_I, QuestDetails.Type.F2P, QuestDetails.Difficulty.EXPERIENCED),
	ERNEST_THE_CHICKEN(Quest.ERNEST_THE_CHICKEN, QuestVarPlayer.QUEST_ERNEST_THE_CHICKEN, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	GOBLIN_DIPLOMACY(Quest.GOBLIN_DIPLOMACY, QuestVarbits.QUEST_GOBLIN_DIPLOMACY, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	IMP_CATCHER(Quest.IMP_CATCHER, QuestVarPlayer.QUEST_IMP_CATCHER, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	THE_KNIGHTS_SWORD(Quest.THE_KNIGHTS_SWORD, QuestVarPlayer.QUEST_THE_KNIGHTS_SWORD, QuestDetails.Type.F2P, QuestDetails.Difficulty.INTERMEDIATE),
	MISTHALIN_MYSTERY(Quest.MISTHALIN_MYSTERY, QuestVarbits.QUEST_MISTHALIN_MYSTERY, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	PIRATES_TREASURE(Quest.PIRATES_TREASURE, QuestVarPlayer.QUEST_PIRATES_TREASURE, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	PRINCE_ALI_RESCUE(Quest.PRINCE_ALI_RESCUE, QuestVarPlayer.QUEST_PRINCE_ALI_RESCUE, QuestDetails.Type.F2P, QuestDetails.Difficulty.INTERMEDIATE),
	THE_RESTLESS_GHOST(Quest.THE_RESTLESS_GHOST, QuestVarPlayer.QUEST_THE_RESTLESS_GHOST, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	ROMEO__JULIET(Quest.ROMEO__JULIET, QuestVarPlayer.QUEST_ROMEO_AND_JULIET, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	RUNE_MYSTERIES(Quest.RUNE_MYSTERIES, QuestVarPlayer.QUEST_RUNE_MYSTERIES, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	SHEEP_SHEARER(Quest.SHEEP_SHEARER, QuestVarPlayer.QUEST_SHEEP_SHEARER, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	SHIELD_OF_ARRAV_PHOENIX_GANG(Quest.SHIELD_OF_ARRAV.getId(), "Shield of Arrav - Phoenix Gang", QuestVarPlayer.QUEST_SHIELD_OF_ARRAV, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	SHIELD_OF_ARRAV_BLACK_ARM_GANG(Quest.SHIELD_OF_ARRAV.getId(), "Shield of Arrav - Black Arm Gang", QuestVarPlayer.QUEST_SHIELD_OF_ARRAV_STATE_146, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	VAMPYRE_SLAYER(Quest.VAMPYRE_SLAYER, QuestVarPlayer.QUEST_VAMPYRE_SLAYER, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	WITCHS_POTION(Quest.WITCHS_POTION, QuestVarPlayer.QUEST_WITCHS_POTION, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	X_MARKS_THE_SPOT(Quest.X_MARKS_THE_SPOT, QuestVarbits.QUEST_X_MARKS_THE_SPOT, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),

	//Members' Quests
	ANIMAL_MAGNETISM(Quest.ANIMAL_MAGNETISM.getId(), "Animal Magnetism", QuestVarbits.QUEST_ANIMAL_MAGNETISM, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	ANOTHER_SLICE_OF_HAM(Quest.ANOTHER_SLICE_OF_HAM, QuestVarbits.QUEST_ANOTHER_SLICE_OF_HAM, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	BENEATH_CURSED_SANDS(Quest.BENEATH_CURSED_SANDS, QuestVarbits.QUEST_BENEATH_CURSED_SANDS, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	BETWEEN_A_ROCK(Quest.BETWEEN_A_ROCK, QuestVarbits.QUEST_BETWEEN_A_ROCK, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	BIG_CHOMPY_BIRD_HUNTING(Quest.BIG_CHOMPY_BIRD_HUNTING, QuestVarPlayer.QUEST_BIG_CHOMPY_BIRD_HUNTING, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	BIOHAZARD(Quest.BIOHAZARD, QuestVarPlayer.QUEST_BIOHAZARD, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	CABIN_FEVER(Quest.CABIN_FEVER, QuestVarPlayer.QUEST_CABIN_FEVER, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	CLOCK_TOWER(Quest.CLOCK_TOWER, QuestVarPlayer.QUEST_CLOCK_TOWER, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	COLD_WAR(Quest.COLD_WAR, QuestVarbits.QUEST_COLD_WAR, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	CONTACT(Quest.CONTACT, QuestVarbits.QUEST_CONTACT, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	CREATURE_OF_FENKENSTRAIN(Quest.CREATURE_OF_FENKENSTRAIN, QuestVarPlayer.QUEST_CREATURE_OF_FENKENSTRAIN, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	DARKNESS_OF_HALLOWVALE(Quest.DARKNESS_OF_HALLOWVALE, QuestVarbits.QUEST_DARKNESS_OF_HALLOWVALE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	DEATH_PLATEAU(Quest.DEATH_PLATEAU, QuestVarPlayer.QUEST_DEATH_PLATEAU, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	DEATH_TO_THE_DORGESHUUN(Quest.DEATH_TO_THE_DORGESHUUN, QuestVarbits.QUEST_DEATH_TO_THE_DORGESHUUN, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	THE_DEPTHS_OF_DESPAIR(Quest.THE_DEPTHS_OF_DESPAIR, QuestVarbits.QUEST_THE_DEPTHS_OF_DESPAIR, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	DESERT_TREASURE(Quest.DESERT_TREASURE, QuestVarbits.QUEST_DESERT_TREASURE, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	DEVIOUS_MINDS(Quest.DEVIOUS_MINDS, QuestVarbits.QUEST_DEVIOUS_MINDS, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	THE_DIG_SITE(Quest.THE_DIG_SITE, QuestVarPlayer.QUEST_THE_DIG_SITE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	DRAGON_SLAYER_II(Quest.DRAGON_SLAYER_II, QuestVarbits.QUEST_DRAGON_SLAYER_II, QuestDetails.Type.P2P, QuestDetails.Difficulty.GRANDMASTER),
	DREAM_MENTOR(Quest.DREAM_MENTOR, QuestVarbits.QUEST_DREAM_MENTOR, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	DRUIDIC_RITUAL(Quest.DRUIDIC_RITUAL, QuestVarPlayer.QUEST_DRUIDIC_RITUAL, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	DWARF_CANNON(Quest.DWARF_CANNON, QuestVarPlayer.QUEST_DWARF_CANNON, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	EADGARS_RUSE(Quest.EADGARS_RUSE, QuestVarPlayer.QUEST_EADGARS_RUSE, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	EAGLES_PEAK(Quest.EAGLES_PEAK, QuestVarbits.QUEST_EAGLES_PEAK, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	ELEMENTAL_WORKSHOP_I(Quest.ELEMENTAL_WORKSHOP_I, QuestVarPlayer.QUEST_ELEMENTAL_WORKSHOP_I, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	ELEMENTAL_WORKSHOP_II(Quest.ELEMENTAL_WORKSHOP_II, QuestVarbits.QUEST_ELEMENTAL_WORKSHOP_II, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	ENAKHRAS_LAMENT(Quest.ENAKHRAS_LAMENT, QuestVarbits.QUEST_ENAKHRAS_LAMENT, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	ENLIGHTENED_JOURNEY(Quest.ENLIGHTENED_JOURNEY, QuestVarbits.QUEST_ENLIGHTENED_JOURNEY, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	THE_EYES_OF_GLOUPHRIE(Quest.THE_EYES_OF_GLOUPHRIE, QuestVarbits.QUEST_THE_EYES_OF_GLOUPHRIE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	FAIRYTALE_I__GROWING_PAINS(Quest.FAIRYTALE_I__GROWING_PAINS, QuestVarbits.QUEST_FAIRYTALE_I_GROWING_PAINS, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	FAIRYTALE_II__CURE_A_QUEEN(Quest.FAIRYTALE_II__CURE_A_QUEEN, QuestVarbits.QUEST_FAIRYTALE_II_CURE_A_QUEEN, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	FAMILY_CREST(Quest.FAMILY_CREST, QuestVarPlayer.QUEST_FAMILY_CREST, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	THE_FEUD(Quest.THE_FEUD, QuestVarbits.QUEST_THE_FEUD, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	FIGHT_ARENA(Quest.FIGHT_ARENA, QuestVarPlayer.QUEST_FIGHT_ARENA, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	FISHING_CONTEST(Quest.FISHING_CONTEST, QuestVarPlayer.QUEST_FISHING_CONTEST, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	FORGETTABLE_TALE(Quest.FORGETTABLE_TALE, QuestVarbits.QUEST_FORGETTABLE_TALE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	BONE_VOYAGE(Quest.BONE_VOYAGE, QuestVarbits.QUEST_BONE_VOYAGE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	THE_FREMENNIK_ISLES(Quest.THE_FREMENNIK_ISLES, QuestVarbits.QUEST_THE_FREMENNIK_ISLES, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	THE_FREMENNIK_TRIALS(Quest.THE_FREMENNIK_TRIALS, QuestVarPlayer.QUEST_THE_FREMENNIK_TRIALS, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	GARDEN_OF_TRANQUILLITY(Quest.GARDEN_OF_TRANQUILLITY, QuestVarbits.QUEST_GARDEN_OF_TRANQUILLITY, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	GERTRUDES_CAT(Quest.GERTRUDES_CAT, QuestVarPlayer.QUEST_GERTRUDES_CAT, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	GHOSTS_AHOY(Quest.GHOSTS_AHOY, QuestVarbits.QUEST_GHOSTS_AHOY, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	THE_GIANT_DWARF(Quest.THE_GIANT_DWARF, QuestVarbits.QUEST_THE_GIANT_DWARF, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	THE_GOLEM(Quest.THE_GOLEM, QuestVarbits.QUEST_THE_GOLEM, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	THE_GRAND_TREE(Quest.THE_GRAND_TREE, QuestVarPlayer.QUEST_THE_GRAND_TREE, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	THE_GREAT_BRAIN_ROBBERY(Quest.THE_GREAT_BRAIN_ROBBERY, QuestVarPlayer.QUEST_THE_GREAT_BRAIN_ROBBERY, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	GRIM_TALES(Quest.GRIM_TALES, QuestVarbits.QUEST_GRIM_TALES, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	THE_HAND_IN_THE_SAND(Quest.THE_HAND_IN_THE_SAND, QuestVarbits.QUEST_THE_HAND_IN_THE_SAND, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	HAUNTED_MINE(Quest.HAUNTED_MINE, QuestVarPlayer.QUEST_HAUNTED_MINE, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	HAZEEL_CULT(Quest.HAZEEL_CULT, QuestVarPlayer.QUEST_HAZEEL_CULT, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	HEROES_QUEST(Quest.HEROES_QUEST, QuestVarPlayer.QUEST_HEROES_QUEST, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	HOLY_GRAIL(Quest.HOLY_GRAIL, QuestVarPlayer.QUEST_HOLY_GRAIL, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	HORROR_FROM_THE_DEEP(Quest.HORROR_FROM_THE_DEEP, QuestVarbits.QUEST_HORROR_FROM_THE_DEEP, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	ICTHLARINS_LITTLE_HELPER(Quest.ICTHLARINS_LITTLE_HELPER, QuestVarbits.QUEST_ICTHLARINS_LITTLE_HELPER, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	IN_AID_OF_THE_MYREQUE(Quest.IN_AID_OF_THE_MYREQUE, QuestVarbits.QUEST_IN_AID_OF_THE_MYREQUE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	IN_SEARCH_OF_THE_MYREQUE(Quest.IN_SEARCH_OF_THE_MYREQUE, QuestVarPlayer.QUEST_IN_SEARCH_OF_THE_MYREQUE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	JUNGLE_POTION(Quest.JUNGLE_POTION, QuestVarPlayer.QUEST_JUNGLE_POTION, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	KINGS_RANSOM(Quest.KINGS_RANSOM, QuestVarbits.QUEST_KINGS_RANSOM, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	LAND_OF_THE_GOBLINS(Quest.LAND_OF_THE_GOBLINS, QuestVarbits.QUEST_LAND_OF_THE_GOBLINS, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	LEGENDS_QUEST(Quest.LEGENDS_QUEST, QuestVarPlayer.QUEST_LEGENDS_QUEST, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	LOST_CITY(Quest.LOST_CITY, QuestVarPlayer.QUEST_LOST_CITY, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	THE_LOST_TRIBE(Quest.THE_LOST_TRIBE, QuestVarbits.QUEST_THE_LOST_TRIBE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	LUNAR_DIPLOMACY(Quest.LUNAR_DIPLOMACY, QuestVarbits.QUEST_LUNAR_DIPLOMACY, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	MAKING_FRIENDS_WITH_MY_ARM(Quest.MAKING_FRIENDS_WITH_MY_ARM, QuestVarbits.QUEST_MAKING_FRIENDS_WITH_MY_ARM, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	MAKING_HISTORY(Quest.MAKING_HISTORY, QuestVarbits.QUEST_MAKING_HISTORY, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	MERLINS_CRYSTAL(Quest.MERLINS_CRYSTAL, QuestVarPlayer.QUEST_MERLINS_CRYSTAL, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	MONKEY_MADNESS_I(Quest.MONKEY_MADNESS_I, QuestVarPlayer.QUEST_MONKEY_MADNESS_I, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	MONKEY_MADNESS_II(Quest.MONKEY_MADNESS_II, QuestVarbits.QUEST_MONKEY_MADNESS_II, QuestDetails.Type.P2P, QuestDetails.Difficulty.GRANDMASTER),
	MONKS_FRIEND(Quest.MONKS_FRIEND, QuestVarPlayer.QUEST_MONKS_FRIEND, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	MOUNTAIN_DAUGHTER(Quest.MOUNTAIN_DAUGHTER, QuestVarbits.QUEST_MOUNTAIN_DAUGHTER, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	MOURNINGS_END_PART_I(Quest.MOURNINGS_END_PART_I, QuestVarPlayer.QUEST_MOURNINGS_END_PART_I, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	MOURNINGS_END_PART_II(Quest.MOURNINGS_END_PART_II, QuestVarbits.QUEST_MOURNINGS_END_PART_II, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	MURDER_MYSTERY(Quest.MURDER_MYSTERY, QuestVarPlayer.QUEST_MURDER_MYSTERY, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	MY_ARMS_BIG_ADVENTURE(Quest.MY_ARMS_BIG_ADVENTURE, QuestVarbits.QUEST_MY_ARMS_BIG_ADVENTURE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	NATURE_SPIRIT(Quest.NATURE_SPIRIT, QuestVarPlayer.QUEST_NATURE_SPIRIT, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	OBSERVATORY_QUEST(Quest.OBSERVATORY_QUEST, QuestVarPlayer.QUEST_OBSERVATORY_QUEST, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	OLAFS_QUEST(Quest.OLAFS_QUEST, QuestVarbits.QUEST_OLAFS_QUEST, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	ONE_SMALL_FAVOUR(Quest.ONE_SMALL_FAVOUR, QuestVarPlayer.QUEST_ONE_SMALL_FAVOUR, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	PLAGUE_CITY(Quest.PLAGUE_CITY, QuestVarPlayer.QUEST_PLAGUE_CITY, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	PRIEST_IN_PERIL(Quest.PRIEST_IN_PERIL, QuestVarPlayer.QUEST_PRIEST_IN_PERIL, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	THE_QUEEN_OF_THIEVES(Quest.THE_QUEEN_OF_THIEVES, QuestVarbits.QUEST_THE_QUEEN_OF_THIEVES, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	RAG_AND_BONE_MAN_I(Quest.RAG_AND_BONE_MAN_I, QuestVarPlayer.QUEST_RAG_AND_BONE_MAN_I, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	RAG_AND_BONE_MAN_II(Quest.RAG_AND_BONE_MAN_II, QuestVarPlayer.QUEST_RAG_AND_BONE_MAN_II, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	RATCATCHERS(Quest.RATCATCHERS, QuestVarbits.QUEST_RATCATCHERS, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	RECIPE_FOR_DISASTER(Quest.RECIPE_FOR_DISASTER, QuestVarbits.QUEST_RECIPE_FOR_DISASTER, QuestDetails.Type.P2P, QuestDetails.Difficulty.GRANDMASTER),
	RECIPE_FOR_DISASTER_START(Quest.RECIPE_FOR_DISASTER.getId(), "RFD - Start", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	RECIPE_FOR_DISASTER_DWARF(Quest.RECIPE_FOR_DISASTER.getId(), "RFD - Dwarf", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_DWARF, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	RECIPE_FOR_DISASTER_WARTFACE_AND_BENTNOZE(Quest.RECIPE_FOR_DISASTER.getId(), "RFD - Wartface & Bentnoze", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_WARTFACE_AND_BENTNOZE, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	RECIPE_FOR_DISASTER_PIRATE_PETE(Quest.RECIPE_FOR_DISASTER.getId(), "RFD - Pirate Pete", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_PIRATE_PETE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	RECIPE_FOR_DISASTER_LUMBRIDGE_GUIDE(Quest.RECIPE_FOR_DISASTER.getId(), "RFD - Lumbridge Guide", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_LUMBRIDGE_GUIDE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	RECIPE_FOR_DISASTER_EVIL_DAVE(Quest.RECIPE_FOR_DISASTER.getId(), "RFD - Evil Dave", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_EVIL_DAVE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	RECIPE_FOR_DISASTER_MONKEY_AMBASSADOR(Quest.RECIPE_FOR_DISASTER.getId(), "RFD - Monkey Ambassador", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_MONKEY_AMBASSADOR, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	RECIPE_FOR_DISASTER_SIR_AMIK_VARZE(Quest.RECIPE_FOR_DISASTER.getId(), "RFD - Sir Amik Varze", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_SIR_AMIK_VARZE, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	RECIPE_FOR_DISASTER_SKRACH_UGLOGWEE(Quest.RECIPE_FOR_DISASTER.getId(), "RFD - Skrach Uglogwee", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_SKRACH_UGLOGWEE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	RECIPE_FOR_DISASTER_FINALE(Quest.RECIPE_FOR_DISASTER.getId(), "RFD - Finale", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER, QuestDetails.Type.P2P, QuestDetails.Difficulty.GRANDMASTER),
	RECRUITMENT_DRIVE(Quest.RECRUITMENT_DRIVE, QuestVarbits.QUEST_RECRUITMENT_DRIVE, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	REGICIDE(Quest.REGICIDE, QuestVarPlayer.QUEST_REGICIDE, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	ROVING_ELVES(Quest.ROVING_ELVES, QuestVarPlayer.QUEST_ROVING_ELVES, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	ROYAL_TROUBLE(Quest.ROYAL_TROUBLE, QuestVarbits.QUEST_ROYAL_TROUBLE, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	RUM_DEAL(Quest.RUM_DEAL, QuestVarPlayer.QUEST_RUM_DEAL, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	SCORPION_CATCHER(Quest.SCORPION_CATCHER, QuestVarPlayer.QUEST_SCORPION_CATCHER, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	SEA_SLUG(Quest.SEA_SLUG, QuestVarPlayer.QUEST_SEA_SLUG, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	SHADES_OF_MORTTON(Quest.SHADES_OF_MORTTON, QuestVarPlayer.QUEST_SHADES_OF_MORTTON, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	SHADOW_OF_THE_STORM(Quest.SHADOW_OF_THE_STORM, QuestVarbits.QUEST_SHADOW_OF_THE_STORM, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	SHEEP_HERDER(Quest.SHEEP_HERDER, QuestVarPlayer.QUEST_SHEEP_HERDER, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	SHILO_VILLAGE(Quest.SHILO_VILLAGE, QuestVarPlayer.QUEST_SHILO_VILLAGE, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	SLEEPING_GIANTS(169, "Sleeping Giants", QuestVarbits.QUEST_SLEEPING_GIANTS, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	THE_SLUG_MENACE(Quest.THE_SLUG_MENACE, QuestVarbits.QUEST_THE_SLUG_MENACE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	A_SOULS_BANE(Quest.A_SOULS_BANE, QuestVarbits.QUEST_A_SOULS_BANE, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	SPIRITS_OF_THE_ELID(Quest.SPIRITS_OF_THE_ELID, QuestVarbits.QUEST_SPIRITS_OF_THE_ELID, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	SWAN_SONG(Quest.SWAN_SONG, QuestVarbits.QUEST_SWAN_SONG, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	TAI_BWO_WANNAI_TRIO(Quest.TAI_BWO_WANNAI_TRIO, QuestVarPlayer.QUEST_TAI_BWO_WANNAI_TRIO, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	A_TAIL_OF_TWO_CATS(Quest.A_TAIL_OF_TWO_CATS, QuestVarbits.QUEST_A_TAIL_OF_TWO_CATS, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	TALE_OF_THE_RIGHTEOUS(Quest.TALE_OF_THE_RIGHTEOUS, QuestVarbits.QUEST_TALE_OF_THE_RIGHTEOUS, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	A_TASTE_OF_HOPE(Quest.A_TASTE_OF_HOPE, QuestVarbits.QUEST_A_TASTE_OF_HOPE, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	TEARS_OF_GUTHIX(Quest.TEARS_OF_GUTHIX, QuestVarbits.QUEST_TEARS_OF_GUTHIX, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	TEMPLE_OF_IKOV(Quest.TEMPLE_OF_IKOV, QuestVarPlayer.QUEST_TEMPLE_OF_IKOV, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	TEMPLE_OF_THE_EYE(Quest.TEMPLE_OF_THE_EYE, QuestVarbits.QUEST_TEMPLE_OF_THE_EYE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	THRONE_OF_MISCELLANIA(Quest.THRONE_OF_MISCELLANIA, QuestVarPlayer.QUEST_THRONE_OF_MISCELLANIA, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	THE_TOURIST_TRAP(Quest.THE_TOURIST_TRAP, QuestVarPlayer.QUEST_THE_TOURIST_TRAP, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	TOWER_OF_LIFE(Quest.TOWER_OF_LIFE, QuestVarbits.QUEST_TOWER_OF_LIFE, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	TREE_GNOME_VILLAGE(Quest.TREE_GNOME_VILLAGE, QuestVarPlayer.QUEST_TREE_GNOME_VILLAGE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	TRIBAL_TOTEM(Quest.TRIBAL_TOTEM, QuestVarPlayer.QUEST_TRIBAL_TOTEM, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	TROLL_ROMANCE(Quest.TROLL_ROMANCE, QuestVarPlayer.QUEST_TROLL_ROMANCE, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	TROLL_STRONGHOLD(Quest.TROLL_STRONGHOLD, QuestVarPlayer.QUEST_TROLL_STRONGHOLD, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	UNDERGROUND_PASS(Quest.UNDERGROUND_PASS, QuestVarPlayer.QUEST_UNDERGROUND_PASS, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	CLIENT_OF_KOUREND(Quest.CLIENT_OF_KOUREND, QuestVarbits.QUEST_CLIENT_OF_KOUREND, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	WANTED(Quest.WANTED, QuestVarbits.QUEST_WANTED, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	WATCHTOWER(Quest.WATCHTOWER, QuestVarPlayer.QUEST_WATCHTOWER, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	WATERFALL_QUEST(Quest.WATERFALL_QUEST, QuestVarPlayer.QUEST_WATERFALL_QUEST, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	WHAT_LIES_BELOW(Quest.WHAT_LIES_BELOW, QuestVarbits.QUEST_WHAT_LIES_BELOW, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	WITCHS_HOUSE(Quest.WITCHS_HOUSE, QuestVarPlayer.QUEST_WITCHS_HOUSE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	ZOGRE_FLESH_EATERS(Quest.ZOGRE_FLESH_EATERS, QuestVarbits.QUEST_ZOGRE_FLESH_EATERS, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	THE_ASCENT_OF_ARCEUUS(Quest.THE_ASCENT_OF_ARCEUUS, QuestVarbits.QUEST_THE_ASCENT_OF_ARCEUUS, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	THE_FORSAKEN_TOWER(Quest.THE_FORSAKEN_TOWER, QuestVarbits.QUEST_THE_FORSAKEN_TOWER, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	SONG_OF_THE_ELVES(Quest.SONG_OF_THE_ELVES, QuestVarbits.QUEST_SONG_OF_THE_ELVES, QuestDetails.Type.P2P, QuestDetails.Difficulty.GRANDMASTER),
	THE_FREMENNIK_EXILES(Quest.THE_FREMENNIK_EXILES, QuestVarbits.QUEST_THE_FREMENNIK_EXILES, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	SINS_OF_THE_FATHER(Quest.SINS_OF_THE_FATHER, QuestVarbits.QUEST_SINS_OF_THE_FATHER, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	GETTING_AHEAD(Quest.GETTING_AHEAD, QuestVarbits.QUEST_GETTING_AHEAD, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	A_PORCINE_OF_INTEREST(Quest.A_PORCINE_OF_INTEREST, QuestVarbits.QUEST_A_PORCINE_OF_INTEREST, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	A_KINGDOM_DIVIDED(Quest.A_KINGDOM_DIVIDED, QuestVarbits.QUEST_A_KINGDOM_DIVIDED, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	A_NIGHT_AT_THE_THEATRE(Quest.A_NIGHT_AT_THE_THEATRE, QuestVarbits.QUEST_A_NIGHT_AT_THE_THEATRE, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),


	//Miniquests
	ENTER_THE_ABYSS(Quest.ENTER_THE_ABYSS, QuestVarPlayer.QUEST_ENTER_THE_ABYSS, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	ARCHITECTURAL_ALLIANCE(Quest.ARCHITECTURAL_ALLIANCE, QuestVarbits.QUEST_ARCHITECTURAL_ALLIANCE, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	BEAR_YOUR_SOUL(Quest.BEAR_YOUR_SOUL, QuestVarbits.QUEST_BEAR_YOUR_SOUL, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	ALFRED_GRIMHANDS_BARCRAWL(Quest.ALFRED_GRIMHANDS_BARCRAWL, QuestVarPlayer.QUEST_ALFRED_GRIMHANDS_BARCRAWL, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	CURSE_OF_THE_EMPTY_LORD(Quest.CURSE_OF_THE_EMPTY_LORD, QuestVarbits.QUEST_CURSE_OF_THE_EMPTY_LORD, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	ENCHANTED_KEY(Quest.THE_ENCHANTED_KEY, QuestVarbits.QUEST_ENCHANTED_KEY, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	THE_GENERALS_SHADOW(Quest.THE_GENERALS_SHADOW, QuestVarbits.QUEST_THE_GENERALS_SHADOW, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	SKIPPY_AND_THE_MOGRES(Quest.SKIPPY_AND_THE_MOGRES, QuestVarbits.QUEST_SKIPPY_AND_THE_MOGRES, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	THE_MAGE_ARENA(Quest.MAGE_ARENA_I, QuestVarPlayer.QUEST_THE_MAGE_ARENA, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	LAIR_OF_TARN_RAZORLOR(Quest.LAIR_OF_TARN_RAZORLOR, QuestVarbits.QUEST_LAIR_OF_TARN_RAZORLOR, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	FAMILY_PEST(Quest.FAMILY_PEST, QuestVarbits.QUEST_FAMILY_PEST, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	THE_MAGE_ARENA_II(Quest.MAGE_ARENA_II, QuestVarbits.QUEST_THE_MAGE_ARENA_II, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	IN_SEARCH_OF_KNOWLEDGE(Quest.IN_SEARCH_OF_KNOWLEDGE, QuestVarbits.QUEST_IN_SEARCH_OF_KNOWLEDGE, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	DADDYS_HOME(Quest.DADDYS_HOME, QuestVarbits.QUEST_DADDYS_HOME, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),

	// Fake miniquests
	KNIGHT_WAVES_TRAINING_GROUNDS("Knight Waves Training Grounds", QuestVarbits.KNIGHT_WAVES_TRAINING_GROUNDS, 8,
		QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),

	// Achievement diaries

	// Ardougne
	ARDOUGNE_EASY("Ardougne Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_ARDOUGNE_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	ARDOUGNE_MEDIUM("Ardougne Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_ARDOUGNE_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	ARDOUGNE_HARD("Ardougne Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_ARDOUGNE_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	ARDOUGNE_ELITE("Ardougne Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_ARDOUGNE_ELITE, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),

	// Desert
	DESERT_EASY("Desert Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_DESERT_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	DESERT_MEDIUM("Desert Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_DESERT_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	DESERT_HARD("Desert Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_DESERT_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	DESERT_ELITE("Desert Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_DESERT_ELITE, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),

	// Falador
	FALADOR_EASY("Falador Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_FALADOR_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	FALADOR_MEDIUM("Falador Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_FALADOR_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	FALADOR_HARD("Falador Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_FALADOR_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	FALADOR_ELITE("Falador Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_FALADOR_ELITE, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),

	// Fremennik
	FREMENNIK_EASY("Fremennik Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_FREMENNIK_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	FREMENNIK_MEDIUM("Fremennik Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_FREMENNIK_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	FREMENNIK_HARD("Fremennik Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_FREMENNIK_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	FREMENNIK_ELITE("Fremennik Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_FREMENNIK_ELITE, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),

	// Kandarin
	KANDARIN_EASY("Kandarin Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_KANDARIN_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	KANDARIN_MEDIUM("Kandarin Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_KANDARIN_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	KANDARIN_HARD("Kandarin Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_KANDARIN_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	KANDARIN_ELITE("Kandarin Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_KANDARIN_ELITE, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),

	// Karamja
	KARAMJA_EASY("Karamja Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_KARAMJA_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	KARAMJA_MEDIUM("Karamja Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_KARAMJA_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	KARAMJA_HARD("Karamja Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_KARAMJA_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	KARAMJA_ELITE("Karamja Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_KARAMJA_ELITE, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),

	// Kourend & Kebos
	KOUREND_EASY("Kourend Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_KOUREND_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	KOUREND_MEDIUM("Kourend Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_KOUREND_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	KOUREND_HARD("Kourend Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_KOUREND_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	KOUREND_ELITE("Kourend Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_KOUREND_ELITE, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),

	// Lumbridge & Draynor
	LUMBRIDGE_EASY("Lumbridge Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_LUMBRIDGE_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	LUMBRIDGE_MEDIUM("Lumbridge Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_LUMBRIDGE_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	LUMBRIDGE_HARD("Lumbridge Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_LUMBRIDGE_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	LUMBRIDGE_ELITE("Lumbridge Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_LUMBRIDGE_ELITE, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),

	// Morytania
	MORYTANIA_EASY("Morytania Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_MORYTANIA_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	MORYTANIA_MEDIUM("Morytania Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_MORYTANIA_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	MORYTANIA_HARD("Morytania Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_MORYTANIA_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	MORYTANIA_ELITE("Morytania Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_MORYTANIA_ELITE, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),

	// Varrock
	VARROCK_EASY("Varrock Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_VARROCK_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	VARROCK_MEDIUM("Varrock Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_VARROCK_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	VARROCK_HARD("Varrock Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_VARROCK_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	VARROCK_ELITE("Varrock Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_VARROCK_ELITE, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),

	// Western Provinces
	WESTERN_EASY("Western Provinces Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_WESTERN_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	WESTERN_MEDIUM("Western Provinces Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_WESTERN_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	WESTERN_HARD("Western Provinces Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_WESTERN_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	WESTERN_ELITE("Western Provinces Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_WESTERN_ELITE, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),

	// Wilderness
	WILDERNESS_EASY("Wilderness Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_WILDERNESS_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	WILDERNESS_MEDIUM("Wilderness Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_WILDERNESS_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	WILDERNESS_HARD("Wilderness Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_WILDERNESS_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	WILDERNESS_ELITE("Wilderness Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_WILDERNESS_ELITE, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
			QuestDetails.Difficulty.ACHIEVEMENT_DIARY),


	// Generic
	MA2_LOCATOR("Mage Arena II Locator", QuestVarbits.QUEST_THE_MAGE_ARENA_II, -1, QuestDetails.Type.GENERIC,
		QuestDetails.Difficulty.GENERIC),

	WOODCUTTING("Woodcutting", Skill.WOODCUTTING, 99, QuestDetails.Type.SKILL, QuestDetails.Difficulty.SKILL);

	@Getter
	private final int id;

	@Getter
	private final String name;

	@Getter
	private final List<String> keywords;

	@Getter
	private final QuestDetails.Type questType;

	@Getter
	private final QuestDetails.Difficulty difficulty;

	private final QuestVarbits varbit;

	private final QuestVarPlayer varPlayer;

	private Skill skill;

	private final int completeValue;

	QuestHelperQuest(int id, String name, QuestVarbits varbit, QuestDetails.Type questType, QuestDetails.Difficulty difficulty)
	{
		this.id = id;
		this.name = name;
		this.keywords = titleToKeywords(name);
		this.varbit = varbit;
		this.varPlayer = null;
		this.questType = questType;
		this.difficulty = difficulty;
		this.completeValue = -1;
	}

	QuestHelperQuest(Quest quest, QuestVarbits varbit, QuestDetails.Type questType, QuestDetails.Difficulty difficulty)
	{
		this.id = quest.getId();
		this.name = quest.getName();
		this.keywords = titleToKeywords(name);
		this.varbit = varbit;
		this.varPlayer = null;
		this.questType = questType;
		this.difficulty = difficulty;
		this.completeValue = -1;
	}

	QuestHelperQuest(Quest quest, QuestVarPlayer varPlayer, QuestDetails.Type questType, QuestDetails.Difficulty difficulty)
	{
		this.id = quest.getId();
		this.name = quest.getName();
		this.keywords = titleToKeywords(name);
		this.varbit = null;
		this.varPlayer = varPlayer;
		this.questType = questType;
		this.difficulty = difficulty;
		this.completeValue = -1;
	}

	QuestHelperQuest(int id, String name, QuestVarPlayer varPlayer, QuestDetails.Type questType, QuestDetails.Difficulty difficulty)
	{
		this.id = id;
		this.name = name;
		this.keywords = titleToKeywords(name);
		this.varbit = null;
		this.varPlayer = varPlayer;
		this.questType = questType;
		this.difficulty = difficulty;
		this.completeValue = -1;
	}

	QuestHelperQuest(int id, String name, List<String> keywords, QuestVarbits varbit, QuestDetails.Type questType, QuestDetails.Difficulty difficulty)
	{
		this.id = id;
		this.name = name;
		this.keywords = Stream.concat(titleToKeywords(name).stream(), keywords.stream()).collect(Collectors.toList());
		this.varbit = varbit;
		this.varPlayer = null;
		this.questType = questType;
		this.difficulty = difficulty;
		this.completeValue = -1;
	}

	QuestHelperQuest(String name, QuestVarbits varbit, int completeValue, QuestDetails.Type questType, QuestDetails.Difficulty difficulty)
	{
		this.id = -1;
		this.name = name;
		this.keywords = titleToKeywords(name);
		this.varbit = varbit;
		this.varPlayer = null;
		this.questType = questType;
		this.difficulty = difficulty;
		this.completeValue = completeValue;
	}

	// Can be used for skill based helpers
	QuestHelperQuest(String name, Skill skill, int completeValue, QuestDetails.Type questType, QuestDetails.Difficulty difficulty)
	{
		this.id = -1;
		this.name = name;
		this.keywords = titleToKeywords(name);
		this.varbit = null;
		this.varPlayer = null;
		this.skill = skill;
		this.questType = questType;
		this.difficulty = difficulty;
		this.completeValue = 99;
	}

	private List<String> titleToKeywords(String title)
	{
		return Arrays.asList(title.toLowerCase().split(" "));
	}

	public QuestState getState(Client client)
	{
		if (skill != null)
		{
			if (client.getRealSkillLevel(skill) >= completeValue)
			{
				return QuestState.FINISHED;
			}
			return QuestState.IN_PROGRESS;
		}

		if (getVar(client) == -1)
		{
			return QuestState.IN_PROGRESS;
		}

		if (completeValue != -1)
		{
			int currentState = getVar(client);
			if (currentState == completeValue)
			{
				return  QuestState.FINISHED;
			}
			if (currentState == 0)
			{
				return QuestState.NOT_STARTED;
			}
			return QuestState.IN_PROGRESS;
		}

		client.runScript(ScriptID.QUEST_STATUS_GET, id);
		switch (client.getIntStack()[0])
		{
			case 2:
				return QuestState.FINISHED;
			case 1:
				return QuestState.NOT_STARTED;
			default:
				return QuestState.IN_PROGRESS;
		}
	}

	public int getVar(Client client)
	{
		if (varbit != null)
		{
			return client.getVarbitValue(varbit.getId());
		}
		else if (varPlayer != null)
		{
			return client.getVarpValue(varPlayer.getId());
		}
		else
		{
			return -1;
		}
	}
}
