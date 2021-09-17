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

import com.questhelper.questhelpers.Quest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.QuestState;
import net.runelite.api.ScriptID;

public enum QuestHelperQuest
{
	//Free Quests
	BELOW_ICE_MOUNTAIN(2874, "Below Ice Mountain", QuestVarbits.QUEST_BELOW_ICE_MOUNTAIN, Quest.Type.F2P, Quest.Difficulty.NOVICE),
	BLACK_KNIGHTS_FORTRESS(299, "Black Knights' Fortress", QuestVarPlayer.QUEST_BLACK_KNIGHTS_FORTRESS, Quest.Type.F2P, Quest.Difficulty.NOVICE),
	COOKS_ASSISTANT(300, "Cook's Assistant", QuestVarPlayer.QUEST_COOKS_ASSISTANT, Quest.Type.F2P, Quest.Difficulty.NOVICE),
	THE_CORSAIR_CURSE(301, "The Corsair Curse", QuestVarbits.QUEST_THE_CORSAIR_CURSE, Quest.Type.F2P, Quest.Difficulty.NOVICE),
	DEMON_SLAYER(302, "Demon Slayer", QuestVarbits.QUEST_DEMON_SLAYER, Quest.Type.F2P, Quest.Difficulty.NOVICE),
	DORICS_QUEST(3138, "Doric's Quest", QuestVarPlayer.QUEST_DORICS_QUEST, Quest.Type.F2P, Quest.Difficulty.NOVICE),
	DRAGON_SLAYER_I(3139, "Dragon Slayer I", QuestVarPlayer.QUEST_DRAGON_SLAYER_I, Quest.Type.F2P, Quest.Difficulty.EXPERIENCED),
	ERNEST_THE_CHICKEN(305, "Ernest the Chicken", QuestVarPlayer.QUEST_ERNEST_THE_CHICKEN, Quest.Type.F2P, Quest.Difficulty.NOVICE),
	GOBLIN_DIPLOMACY(306, "Goblin Diplomacy", QuestVarbits.QUEST_GOBLIN_DIPLOMACY, Quest.Type.F2P, Quest.Difficulty.NOVICE),
	IMP_CATCHER(307, "Imp Catcher", QuestVarPlayer.QUEST_IMP_CATCHER, Quest.Type.F2P, Quest.Difficulty.NOVICE),
	THE_KNIGHTS_SWORD(308, "The Knight's Sword", QuestVarPlayer.QUEST_THE_KNIGHTS_SWORD, Quest.Type.F2P, Quest.Difficulty.INTERMEDIATE),
	MISTHALIN_MYSTERY(309, "Misthalin Mystery", QuestVarbits.QUEST_MISTHALIN_MYSTERY, Quest.Type.F2P, Quest.Difficulty.NOVICE),
	PIRATES_TREASURE(310, "Pirate's Treasure", QuestVarPlayer.QUEST_PIRATES_TREASURE, Quest.Type.F2P, Quest.Difficulty.NOVICE),
	PRINCE_ALI_RESCUE(311, "Prince Ali Rescue", QuestVarPlayer.QUEST_PRINCE_ALI_RESCUE, Quest.Type.F2P, Quest.Difficulty.NOVICE),
	THE_RESTLESS_GHOST(312, "The Restless Ghost", QuestVarPlayer.QUEST_THE_RESTLESS_GHOST, Quest.Type.F2P, Quest.Difficulty.NOVICE),
	ROMEO__JULIET(313, "Romeo & Juliet", QuestVarPlayer.QUEST_ROMEO_AND_JULIET, Quest.Type.F2P, Quest.Difficulty.NOVICE),
	RUNE_MYSTERIES(314, "Rune Mysteries", QuestVarPlayer.QUEST_RUNE_MYSTERIES, Quest.Type.F2P, Quest.Difficulty.NOVICE),
	SHEEP_SHEARER(315, "Sheep Shearer", QuestVarPlayer.QUEST_SHEEP_SHEARER, Quest.Type.F2P, Quest.Difficulty.NOVICE),
	SHIELD_OF_ARRAV_PHOENIX_GANG(316, "Shield of Arrav - Phoenix Gang", QuestVarPlayer.QUEST_SHIELD_OF_ARRAV, Quest.Type.F2P, Quest.Difficulty.NOVICE),
	SHIELD_OF_ARRAV_BLACK_ARM_GANG(316, "Shield of Arrav - Black Arm Gang", QuestVarPlayer.QUEST_SHIELD_OF_ARRAV_STATE_146, Quest.Type.F2P, Quest.Difficulty.NOVICE),
	VAMPYRE_SLAYER(1278, "Vampyre Slayer", QuestVarPlayer.QUEST_VAMPYRE_SLAYER, Quest.Type.F2P, Quest.Difficulty.NOVICE),
	WITCHS_POTION(318, "Witch's Potion", QuestVarPlayer.QUEST_WITCHS_POTION, Quest.Type.F2P, Quest.Difficulty.NOVICE),
	X_MARKS_THE_SPOT(3155, "X Marks the Spot", QuestVarbits.QUEST_X_MARKS_THE_SPOT, Quest.Type.F2P, Quest.Difficulty.NOVICE),

	//Members' Quests
	ANIMAL_MAGNETISM(331, "Animal Magnetism", QuestVarbits.QUEST_ANIMAL_MAGNETISM, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	ANOTHER_SLICE_OF_HAM(332, "Another Slice of H.A.M.", QuestVarbits.QUEST_ANOTHER_SLICE_OF_HAM, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	BETWEEN_A_ROCK(333, "Between a Rock...", QuestVarbits.QUEST_BETWEEN_A_ROCK, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	BIG_CHOMPY_BIRD_HUNTING(334, "Big Chompy Bird Hunting", QuestVarPlayer.QUEST_BIG_CHOMPY_BIRD_HUNTING, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	BIOHAZARD(335, "Biohazard", QuestVarPlayer.QUEST_BIOHAZARD, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	CABIN_FEVER(336, "Cabin Fever", QuestVarPlayer.QUEST_CABIN_FEVER, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	CLOCK_TOWER(337, "Clock Tower", QuestVarPlayer.QUEST_CLOCK_TOWER, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	COLD_WAR(338, "Cold War", QuestVarbits.QUEST_COLD_WAR, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	CONTACT(339, "Contact!", QuestVarbits.QUEST_CONTACT, Quest.Type.P2P, Quest.Difficulty.MASTER),
	CREATURE_OF_FENKENSTRAIN(340, "Creature of Fenkenstrain", QuestVarPlayer.QUEST_CREATURE_OF_FENKENSTRAIN, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	DARKNESS_OF_HALLOWVALE(341, "Darkness of Hallowvale", QuestVarbits.QUEST_DARKNESS_OF_HALLOWVALE, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	DEATH_PLATEAU(342, "Death Plateau", QuestVarPlayer.QUEST_DEATH_PLATEAU, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	DEATH_TO_THE_DORGESHUUN(343, "Death to the Dorgeshuun", QuestVarbits.QUEST_DEATH_TO_THE_DORGESHUUN, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	THE_DEPTHS_OF_DESPAIR(344, "The Depths of Despair", QuestVarbits.QUEST_THE_DEPTHS_OF_DESPAIR, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	DESERT_TREASURE(345, "Desert Treasure", QuestVarbits.QUEST_DESERT_TREASURE, Quest.Type.P2P, Quest.Difficulty.MASTER),
	DEVIOUS_MINDS(346, "Devious Minds", QuestVarbits.QUEST_DEVIOUS_MINDS, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	THE_DIG_SITE(347, "The Dig Site", QuestVarPlayer.QUEST_THE_DIG_SITE, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	DRAGON_SLAYER_II(348, "Dragon Slayer II", QuestVarbits.QUEST_DRAGON_SLAYER_II, Quest.Type.P2P, Quest.Difficulty.GRANDMASTER),
	DREAM_MENTOR(349, "Dream Mentor", QuestVarbits.QUEST_DREAM_MENTOR, Quest.Type.P2P, Quest.Difficulty.MASTER),
	DRUIDIC_RITUAL(350, "Druidic Ritual", QuestVarPlayer.QUEST_DRUIDIC_RITUAL, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	DWARF_CANNON(351, "Dwarf Cannon", QuestVarPlayer.QUEST_DWARF_CANNON, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	EADGARS_RUSE(352, "Eadgar's Ruse", QuestVarPlayer.QUEST_EADGARS_RUSE, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	EAGLES_PEAK(353, "Eagles' Peak", QuestVarbits.QUEST_EAGLES_PEAK, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	ELEMENTAL_WORKSHOP_I(354, "Elemental Workshop I", QuestVarPlayer.QUEST_ELEMENTAL_WORKSHOP_I, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	ELEMENTAL_WORKSHOP_II(355, "Elemental Workshop II", QuestVarbits.QUEST_ELEMENTAL_WORKSHOP_II, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	ENAKHRAS_LAMENT(356, "Enakhra's Lament", QuestVarbits.QUEST_ENAKHRAS_LAMENT, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	ENLIGHTENED_JOURNEY(357, "Enlightened Journey", QuestVarbits.QUEST_ENLIGHTENED_JOURNEY, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	THE_EYES_OF_GLOUPHRIE(358, "The Eyes of Glouphrie", QuestVarbits.QUEST_THE_EYES_OF_GLOUPHRIE, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	FAIRYTALE_I__GROWING_PAINS(359, "Fairytale I - Growing Pains", QuestVarbits.QUEST_FAIRYTALE_I_GROWING_PAINS, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	FAIRYTALE_II__CURE_A_QUEEN(360, "Fairytale II - Cure a Queen", QuestVarbits.QUEST_FAIRYTALE_II_CURE_A_QUEEN, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	FAMILY_CREST(361, "Family Crest", QuestVarPlayer.QUEST_FAMILY_CREST, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	THE_FEUD(362, "The Feud", QuestVarbits.QUEST_THE_FEUD, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	FIGHT_ARENA(363, "Fight Arena", QuestVarPlayer.QUEST_FIGHT_ARENA, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	FISHING_CONTEST(364, "Fishing Contest", QuestVarPlayer.QUEST_FISHING_CONTEST, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	FORGETTABLE_TALE(365, "Forgettable Tale...", QuestVarbits.QUEST_FORGETTABLE_TALE, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	BONE_VOYAGE(3135, "Bone Voyage", QuestVarbits.QUEST_BONE_VOYAGE, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	THE_FREMENNIK_ISLES(367, "The Fremennik Isles", QuestVarbits.QUEST_THE_FREMENNIK_ISLES, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	THE_FREMENNIK_TRIALS(368, "The Fremennik Trials", QuestVarPlayer.QUEST_THE_FREMENNIK_TRIALS, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	GARDEN_OF_TRANQUILLITY(369, "Garden of Tranquillity", QuestVarbits.QUEST_GARDEN_OF_TRANQUILLITY, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	GERTRUDES_CAT(370, "Gertrude's Cat", QuestVarPlayer.QUEST_GERTRUDES_CAT, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	GHOSTS_AHOY(371, "Ghosts Ahoy", QuestVarbits.QUEST_GHOSTS_AHOY, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	THE_GIANT_DWARF(372, "The Giant Dwarf", QuestVarbits.QUEST_THE_GIANT_DWARF, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	THE_GOLEM(373, "The Golem", QuestVarbits.QUEST_THE_GOLEM, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	THE_GRAND_TREE(374, "The Grand Tree", QuestVarPlayer.QUEST_THE_GRAND_TREE, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	THE_GREAT_BRAIN_ROBBERY(375, "The Great Brain Robbery", QuestVarPlayer.QUEST_THE_GREAT_BRAIN_ROBBERY, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	GRIM_TALES(376, "Grim Tales", QuestVarbits.QUEST_GRIM_TALES, Quest.Type.P2P, Quest.Difficulty.MASTER),
	THE_HAND_IN_THE_SAND(377, "The Hand in the Sand", QuestVarbits.QUEST_THE_HAND_IN_THE_SAND, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	HAUNTED_MINE(378, "Haunted Mine", QuestVarPlayer.QUEST_HAUNTED_MINE, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	HAZEEL_CULT(379, "Hazeel Cult", QuestVarPlayer.QUEST_HAZEEL_CULT, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	HEROES_QUEST(3142, "Heroes' Quest", QuestVarPlayer.QUEST_HEROES_QUEST, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	HOLY_GRAIL(381, "Holy Grail", QuestVarPlayer.QUEST_HOLY_GRAIL, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	HORROR_FROM_THE_DEEP(382, "Horror from the Deep", QuestVarbits.QUEST_HORROR_FROM_THE_DEEP, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	ICTHLARINS_LITTLE_HELPER(383, "Icthlarin's Little Helper", QuestVarbits.QUEST_ICTHLARINS_LITTLE_HELPER, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	IN_AID_OF_THE_MYREQUE(384, "In Aid of the Myreque", QuestVarbits.QUEST_IN_AID_OF_THE_MYREQUE, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	IN_SEARCH_OF_THE_MYREQUE(385, "In Search of the Myreque", QuestVarPlayer.QUEST_IN_SEARCH_OF_THE_MYREQUE, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	JUNGLE_POTION(386, "Jungle Potion", QuestVarPlayer.QUEST_JUNGLE_POTION, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	KINGS_RANSOM(387, "King's Ransom", QuestVarbits.QUEST_KINGS_RANSOM, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	LEGENDS_QUEST(3145, "Legends' Quest", QuestVarPlayer.QUEST_LEGENDS_QUEST, Quest.Type.P2P, Quest.Difficulty.MASTER),
	LOST_CITY(389, "Lost City", QuestVarPlayer.QUEST_LOST_CITY, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	THE_LOST_TRIBE(390, "The Lost Tribe", QuestVarbits.QUEST_THE_LOST_TRIBE, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	LUNAR_DIPLOMACY(391, "Lunar Diplomacy", QuestVarbits.QUEST_LUNAR_DIPLOMACY, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	MAKING_FRIENDS_WITH_MY_ARM(392, "Making Friends with My Arm", QuestVarbits.QUEST_MAKING_FRIENDS_WITH_MY_ARM, Quest.Type.P2P, Quest.Difficulty.MASTER),
	MAKING_HISTORY(393, "Making History", QuestVarbits.QUEST_MAKING_HISTORY, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	MERLINS_CRYSTAL(394, "Merlin's Crystal", QuestVarPlayer.QUEST_MERLINS_CRYSTAL, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	MONKEY_MADNESS_I(395, "Monkey Madness I", QuestVarPlayer.QUEST_MONKEY_MADNESS_I, Quest.Type.P2P, Quest.Difficulty.MASTER),
	MONKEY_MADNESS_II(396, "Monkey Madness II", QuestVarbits.QUEST_MONKEY_MADNESS_II, Quest.Type.P2P, Quest.Difficulty.GRANDMASTER),
	MONKS_FRIEND(397, "Monk's Friend", QuestVarPlayer.QUEST_MONKS_FRIEND, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	MOUNTAIN_DAUGHTER(398, "Mountain Daughter", QuestVarbits.QUEST_MOUNTAIN_DAUGHTER, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	MOURNINGS_END_PART_I(3147, "Mourning's End Part I", QuestVarPlayer.QUEST_MOURNINGS_END_PART_I, Quest.Type.P2P, Quest.Difficulty.MASTER),
	MOURNINGS_END_PART_II(3148, "Mourning's End Part II", QuestVarbits.QUEST_MOURNINGS_END_PART_II, Quest.Type.P2P, Quest.Difficulty.MASTER),
	MURDER_MYSTERY(401, "Murder Mystery", QuestVarPlayer.QUEST_MURDER_MYSTERY, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	MY_ARMS_BIG_ADVENTURE(402, "My Arm's Big Adventure", QuestVarbits.QUEST_MY_ARMS_BIG_ADVENTURE, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	NATURE_SPIRIT(403, "Nature Spirit", QuestVarPlayer.QUEST_NATURE_SPIRIT, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	OBSERVATORY_QUEST(3149, "Observatory Quest", QuestVarPlayer.QUEST_OBSERVATORY_QUEST, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	OLAFS_QUEST(3150, "Olaf's Quest", QuestVarbits.QUEST_OLAFS_QUEST, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	ONE_SMALL_FAVOUR(406, "One Small Favour", QuestVarPlayer.QUEST_ONE_SMALL_FAVOUR, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	PLAGUE_CITY(407, "Plague City", QuestVarPlayer.QUEST_PLAGUE_CITY, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	PRIEST_IN_PERIL(408, "Priest in Peril", QuestVarPlayer.QUEST_PRIEST_IN_PERIL, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	THE_QUEEN_OF_THIEVES(409, "The Queen of Thieves", QuestVarbits.QUEST_THE_QUEEN_OF_THIEVES, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	RAG_AND_BONE_MAN_I(3152, "Rag and Bone Man I", QuestVarPlayer.QUEST_RAG_AND_BONE_MAN_I, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	RAG_AND_BONE_MAN_II(411, "Rag and Bone Man II", QuestVarPlayer.QUEST_RAG_AND_BONE_MAN_II, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	RATCATCHERS(412, "Ratcatchers", QuestVarbits.QUEST_RATCATCHERS, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	RECIPE_FOR_DISASTER(413, "Recipe for Disaster", QuestVarbits.QUEST_RECIPE_FOR_DISASTER, Quest.Type.P2P, Quest.Difficulty.GRANDMASTER),
	RECIPE_FOR_DISASTER_START(413, "RFD - Start", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	RECIPE_FOR_DISASTER_DWARF(413, "RFD - Dwarf", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_DWARF, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	RECIPE_FOR_DISASTER_WARTFACE_AND_BENTNOZE(413, "RFD - Wartface & Bentnoze", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_WARTFACE_AND_BENTNOZE, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	RECIPE_FOR_DISASTER_PIRATE_PETE(413, "RFD - Pirate Pete", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_PIRATE_PETE, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	RECIPE_FOR_DISASTER_LUMBRIDGE_GUIDE(413, "RFD - Lumbridge Guide", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_LUMBRIDGE_GUIDE, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	RECIPE_FOR_DISASTER_EVIL_DAVE(413, "RFD - Evil Dave", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_EVIL_DAVE, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	RECIPE_FOR_DISASTER_MONKEY_AMBASSADOR(413, "RFD - Monkey Ambassador", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_MONKEY_AMBASSADOR, Quest.Type.P2P, Quest.Difficulty.MASTER),
	RECIPE_FOR_DISASTER_SIR_AMIK_VARZE(413, "RFD - Sir Amik Varze", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_SIR_AMIK_VARZE, Quest.Type.P2P, Quest.Difficulty.MASTER),
	RECIPE_FOR_DISASTER_SKRACH_UGLOGWEE(413, "RFD - Skrach Uglogwee", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_SKRACH_UGLOGWEE, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	RECIPE_FOR_DISASTER_FINALE(413, "RFD - Finale", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER, Quest.Type.P2P, Quest.Difficulty.GRANDMASTER),
	RECRUITMENT_DRIVE(414, "Recruitment Drive", QuestVarbits.QUEST_RECRUITMENT_DRIVE, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	REGICIDE(415, "Regicide", QuestVarPlayer.QUEST_REGICIDE, Quest.Type.P2P, Quest.Difficulty.MASTER),
	ROVING_ELVES(416, "Roving Elves", QuestVarPlayer.QUEST_ROVING_ELVES, Quest.Type.P2P, Quest.Difficulty.MASTER),
	ROYAL_TROUBLE(417, "Royal Trouble", QuestVarbits.QUEST_ROYAL_TROUBLE, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	RUM_DEAL(418, "Rum Deal", QuestVarPlayer.QUEST_RUM_DEAL, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	SCORPION_CATCHER(419, "Scorpion Catcher", QuestVarPlayer.QUEST_SCORPION_CATCHER, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	SEA_SLUG(420, "Sea Slug", QuestVarPlayer.QUEST_SEA_SLUG, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	SHADES_OF_MORTTON(421, "Shades of Mort'ton", QuestVarPlayer.QUEST_SHADES_OF_MORTTON, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	SHADOW_OF_THE_STORM(422, "Shadow of the Storm", QuestVarbits.QUEST_SHADOW_OF_THE_STORM, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	SHEEP_HERDER(423, "Sheep Herder", QuestVarPlayer.QUEST_SHEEP_HERDER, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	SHILO_VILLAGE(424, "Shilo Village", QuestVarPlayer.QUEST_SHILO_VILLAGE, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	THE_SLUG_MENACE(425, "The Slug Menace", QuestVarbits.QUEST_THE_SLUG_MENACE, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	A_SOULS_BANE(426, "A Soul's Bane", QuestVarbits.QUEST_A_SOULS_BANE, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	SPIRITS_OF_THE_ELID(427, "Spirits of the Elid", QuestVarbits.QUEST_SPIRITS_OF_THE_ELID, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	SWAN_SONG(428, "Swan Song", QuestVarbits.QUEST_SWAN_SONG, Quest.Type.P2P, Quest.Difficulty.MASTER),
	TAI_BWO_WANNAI_TRIO(429, "Tai Bwo Wannai Trio", QuestVarPlayer.QUEST_TAI_BWO_WANNAI_TRIO, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	A_TAIL_OF_TWO_CATS(430, "A Tail of Two Cats", QuestVarbits.QUEST_A_TAIL_OF_TWO_CATS, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	TALE_OF_THE_RIGHTEOUS(431, "Tale of the Righteous", QuestVarbits.QUEST_TALE_OF_THE_RIGHTEOUS, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	A_TASTE_OF_HOPE(432, "A Taste of Hope", QuestVarbits.QUEST_A_TASTE_OF_HOPE, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	TEARS_OF_GUTHIX(433, "Tears of Guthix", QuestVarbits.QUEST_TEARS_OF_GUTHIX, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	TEMPLE_OF_IKOV(434, "Temple of Ikov", QuestVarPlayer.QUEST_TEMPLE_OF_IKOV, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	THRONE_OF_MISCELLANIA(435, "Throne of Miscellania", QuestVarPlayer.QUEST_THRONE_OF_MISCELLANIA, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	THE_TOURIST_TRAP(436, "The Tourist Trap", QuestVarPlayer.QUEST_THE_TOURIST_TRAP, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	TOWER_OF_LIFE(437, "Tower of Life", QuestVarbits.QUEST_TOWER_OF_LIFE, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	TREE_GNOME_VILLAGE(438, "Tree Gnome Village", QuestVarPlayer.QUEST_TREE_GNOME_VILLAGE, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	TRIBAL_TOTEM(439, "Tribal Totem", QuestVarPlayer.QUEST_TRIBAL_TOTEM, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	TROLL_ROMANCE(440, "Troll Romance", QuestVarPlayer.QUEST_TROLL_ROMANCE, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	TROLL_STRONGHOLD(441, "Troll Stronghold", QuestVarPlayer.QUEST_TROLL_STRONGHOLD, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	UNDERGROUND_PASS(442, "Underground Pass", QuestVarPlayer.QUEST_UNDERGROUND_PASS, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	CLIENT_OF_KOUREND(3136, "Client of Kourend", QuestVarbits.QUEST_CLIENT_OF_KOUREND, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	WANTED(444, "Wanted!", QuestVarbits.QUEST_WANTED, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	WATCHTOWER(445, "Watchtower", QuestVarPlayer.QUEST_WATCHTOWER, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	WATERFALL_QUEST(3154, "Waterfall Quest", QuestVarPlayer.QUEST_WATERFALL_QUEST, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	WHAT_LIES_BELOW(447, "What Lies Below", QuestVarbits.QUEST_WHAT_LIES_BELOW, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	WITCHS_HOUSE(448, "Witch's House", QuestVarPlayer.QUEST_WITCHS_HOUSE, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	ZOGRE_FLESH_EATERS(449, "Zogre Flesh Eaters", QuestVarbits.QUEST_ZOGRE_FLESH_EATERS, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	THE_ASCENT_OF_ARCEUUS(542, "The Ascent of Arceuus", QuestVarbits.QUEST_THE_ASCENT_OF_ARCEUUS, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	THE_FORSAKEN_TOWER(543, "The Forsaken Tower", QuestVarbits.QUEST_THE_FORSAKEN_TOWER, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	SONG_OF_THE_ELVES(603, "Song of the Elves", QuestVarbits.QUEST_SONG_OF_THE_ELVES, Quest.Type.P2P, Quest.Difficulty.GRANDMASTER),
	THE_FREMENNIK_EXILES(3141, "The Fremennik Exiles", QuestVarbits.QUEST_THE_FREMENNIK_EXILES, Quest.Type.P2P, Quest.Difficulty.MASTER),
	SINS_OF_THE_FATHER(1276, "Sins of the Father", QuestVarbits.QUEST_SINS_OF_THE_FATHER, Quest.Type.P2P, Quest.Difficulty.MASTER),
	GETTING_AHEAD(752, "Getting Ahead", QuestVarbits.QUEST_GETTING_AHEAD, Quest.Type.P2P, Quest.Difficulty.INTERMEDIATE),
	A_PORCINE_OF_INTEREST(3151, "A Porcine of Interest", QuestVarbits.QUEST_A_PORCINE_OF_INTEREST, Quest.Type.P2P, Quest.Difficulty.NOVICE),
	A_KINGDOM_DIVIDED(2971, "A Kingdom Divided", QuestVarbits.QUEST_A_KINGDOM_DIVIDED, Quest.Type.P2P, Quest.Difficulty.EXPERIENCED),
	A_NIGHT_AT_THE_THEATRE(949, "A Night at the Theatre", QuestVarbits.QUEST_A_NIGHT_AT_THE_THEATRE, Quest.Type.P2P, Quest.Difficulty.MASTER),


	//Miniquests
	ENTER_THE_ABYSS(3140, "Enter the Abyss", QuestVarPlayer.QUEST_ENTER_THE_ABYSS, Quest.Type.MINIQUEST, Quest.Difficulty.MINIQUEST),
	ARCHITECTURAL_ALLIANCE(320, "Architectural Alliance", QuestVarbits.QUEST_ARCHITECTURAL_ALLIANCE, Quest.Type.MINIQUEST, Quest.Difficulty.MINIQUEST),
	BEAR_YOUR_SOUL(1275, "Bear your Soul", QuestVarbits.QUEST_BEAR_YOUR_SOUL, Quest.Type.MINIQUEST, Quest.Difficulty.MINIQUEST),
	ALFRED_GRIMHANDS_BARCRAWL(322, "Alfred Grimhand's Barcrawl", QuestVarPlayer.QUEST_ALFRED_GRIMHANDS_BARCRAWL, Quest.Type.MINIQUEST, Quest.Difficulty.MINIQUEST),
	CURSE_OF_THE_EMPTY_LORD(3137, "Curse of the Empty Lord", QuestVarbits.QUEST_CURSE_OF_THE_EMPTY_LORD, Quest.Type.MINIQUEST, Quest.Difficulty.MINIQUEST),
	ENCHANTED_KEY(324, "Enchanted Key", QuestVarbits.QUEST_ENCHANTED_KEY, Quest.Type.MINIQUEST, Quest.Difficulty.MINIQUEST),
	THE_GENERALS_SHADOW(325, "The General's Shadow", QuestVarbits.QUEST_THE_GENERALS_SHADOW, Quest.Type.MINIQUEST, Quest.Difficulty.MINIQUEST),
	SKIPPY_AND_THE_MOGRES(3153, "Skippy and the Mogres", QuestVarbits.QUEST_SKIPPY_AND_THE_MOGRES, Quest.Type.MINIQUEST, Quest.Difficulty.MINIQUEST),
	THE_MAGE_ARENA(3146, "The Mage Arena", QuestVarPlayer.QUEST_THE_MAGE_ARENA, Quest.Type.MINIQUEST, Quest.Difficulty.MINIQUEST),
	LAIR_OF_TARN_RAZORLOR(3144, "Lair of Tarn Razorlor", QuestVarbits.QUEST_LAIR_OF_TARN_RAZORLOR, Quest.Type.MINIQUEST, Quest.Difficulty.MINIQUEST),
	FAMILY_PEST(329, "Family Pest", QuestVarbits.QUEST_FAMILY_PEST, Quest.Type.MINIQUEST, Quest.Difficulty.MINIQUEST),
	THE_MAGE_ARENA_II(330, "The Mage Arena II", QuestVarbits.QUEST_THE_MAGE_ARENA_II, Quest.Type.MINIQUEST, Quest.Difficulty.MINIQUEST),
	IN_SEARCH_OF_KNOWLEDGE(3143, "In Search of Knowledge", QuestVarbits.QUEST_IN_SEARCH_OF_KNOWLEDGE, Quest.Type.MINIQUEST, Quest.Difficulty.MINIQUEST),
	DADDYS_HOME(1688, "Daddy's Home", QuestVarbits.QUEST_DADDYS_HOME, Quest.Type.MINIQUEST, Quest.Difficulty.MINIQUEST),

	// Fake miniquests
	KNIGHT_WAVES_TRAINING_GROUNDS("Knight Waves Training Grounds", QuestVarbits.KNIGHT_WAVES_TRAINING_GROUNDS, 8,
		Quest.Type.MINIQUEST, Quest.Difficulty.MINIQUEST),

	// Achievement diaries

	// Ardougne
	ARDOUGNE_EASY("Ardougne Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_ARDOUGNE_EASY, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	ARDOUGNE_MEDIUM("Ardougne Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_ARDOUGNE_MEDIUM, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	ARDOUGNE_HARD("Ardougne Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_ARDOUGNE_HARD, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	ARDOUGNE_ELITE("Ardougne Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_ARDOUGNE_ELITE, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),

	// Desert
	DESERT_EASY("Desert Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_DESERT_EASY, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	DESERT_MEDIUM("Desert Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_DESERT_MEDIUM, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	DESERT_HARD("Desert Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_DESERT_HARD, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	DESERT_ELITE("Desert Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_DESERT_ELITE, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),

	// Falador
	FALADOR_EASY("Falador Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_FALADOR_EASY, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	FALADOR_MEDIUM("Falador Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_FALADOR_MEDIUM, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	FALADOR_HARD("Falador Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_FALADOR_HARD, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	FALADOR_ELITE("Falador Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_FALADOR_ELITE, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),

	// Fremennik
	FREMENNIK_EASY("Fremennik Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_FREMENNIK_EASY, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	FREMENNIK_MEDIUM("Fremennik Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_FREMENNIK_MEDIUM, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	FREMENNIK_HARD("Fremennik Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_FREMENNIK_HARD, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	FREMENNIK_ELITE("Fremennik Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_FREMENNIK_ELITE, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),

	// Kandarin
	KANDARIN_EASY("Kandarin Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_KANDARIN_EASY, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	KANDARIN_MEDIUM("Kandarin Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_KANDARIN_MEDIUM, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	KANDARIN_HARD("Kandarin Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_KANDARIN_HARD, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	KANDARIN_ELITE("Kandarin Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_KANDARIN_ELITE, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),

	// Karamja
	KARAMJA_EASY("Karamja Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_KARAMJA_EASY, 1, Quest.Type.ACHIEVEMENT_DIARY,
		Quest.Difficulty.ACHIEVEMENT_DIARY),
	KARAMJA_MEDIUM("Karamja Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_KARAMJA_MEDIUM, 1, Quest.Type.ACHIEVEMENT_DIARY,
		Quest.Difficulty.ACHIEVEMENT_DIARY),
	KARAMJA_HARD("Karamja Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_KARAMJA_HARD, 1, Quest.Type.ACHIEVEMENT_DIARY,
		Quest.Difficulty.ACHIEVEMENT_DIARY),
	KARAMJA_ELITE("Karamja Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_KARAMJA_ELITE, 1, Quest.Type.ACHIEVEMENT_DIARY,
		Quest.Difficulty.ACHIEVEMENT_DIARY),

	// Kourend & Kebos
	KOUREND_EASY("Kourend Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_KOUREND_EASY, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	KOUREND_MEDIUM("Kourend Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_KOUREND_MEDIUM, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	KOUREND_HARD("Kourend Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_KOUREND_HARD, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	KOUREND_ELITE("Kourend Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_KOUREND_ELITE, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),

	// Lumbridge & Draynor
	LUMBRIDGE_EASY("Lumbridge Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_LUMBRIDGE_EASY, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	LUMBRIDGE_MEDIUM("Lumbridge Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_LUMBRIDGE_MEDIUM, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	LUMBRIDGE_HARD("Lumbridge Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_LUMBRIDGE_HARD, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	LUMBRIDGE_ELITE("Lumbridge Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_LUMBRIDGE_ELITE, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),

	// Morytania
	MORYTANIA_EASY("Morytania Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_MORYTANIA_EASY, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	MORYTANIA_MEDIUM("Morytania Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_MORYTANIA_MEDIUM, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	MORYTANIA_HARD("Morytania Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_MORYTANIA_HARD, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	MORYTANIA_ELITE("Morytania Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_MORYTANIA_ELITE, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),

	// Varrock
	VARROCK_EASY("Varrock Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_VARROCK_EASY, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	VARROCK_MEDIUM("Varrock Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_VARROCK_MEDIUM, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	VARROCK_HARD("Varrock Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_VARROCK_HARD, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	VARROCK_ELITE("Varrock Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_VARROCK_ELITE, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),

	// Western Provinces
	WESTERN_EASY("Western Provinces Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_WESTERN_EASY, 1, Quest.Type.ACHIEVEMENT_DIARY,
		Quest.Difficulty.ACHIEVEMENT_DIARY),
	WESTERN_MEDIUM("Western Provinces Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_WESTERN_MEDIUM, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	WESTERN_HARD("Western Provinces Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_WESTERN_HARD, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	WESTERN_ELITE("Western Provinces Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_WESTERN_ELITE, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),

	// Wilderness
	WILDERNESS_EASY("Wilderness Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_WILDERNESS_EASY, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	WILDERNESS_MEDIUM("Wilderness Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_WILDERNESS_MEDIUM, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	WILDERNESS_HARD("Wilderness Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_WILDERNESS_HARD, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),
	WILDERNESS_ELITE("Wilderness Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_WILDERNESS_ELITE, 1, Quest.Type.ACHIEVEMENT_DIARY,
			Quest.Difficulty.ACHIEVEMENT_DIARY),


	// Generic
	MA2_LOCATOR("Mage Arena II Locator", QuestVarbits.QUEST_THE_MAGE_ARENA_II, -1, Quest.Type.GENERIC,
		Quest.Difficulty.GENERIC);

	@Getter
	private final int id;

	@Getter
	private final String name;

	@Getter
	private final List<String> keywords;

	@Getter
	private final Quest.Type questType;

	@Getter
	private final Quest.Difficulty difficulty;

	private final QuestVarbits varbit;

	private final QuestVarPlayer varPlayer;

	private final int completeValue;

	QuestHelperQuest(int id, String name, QuestVarbits varbit, Quest.Type questType, Quest.Difficulty difficulty)
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

	QuestHelperQuest(int id, String name, QuestVarPlayer varPlayer, Quest.Type questType, Quest.Difficulty difficulty)
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

	QuestHelperQuest(int id, String name, List<String> keywords, QuestVarbits varbit, Quest.Type questType, Quest.Difficulty difficulty)
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

	QuestHelperQuest(String name, QuestVarbits varbit, int completeValue, Quest.Type questType, Quest.Difficulty difficulty)
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

	// Can be used for non-variables based helpers
	QuestHelperQuest(String name, Quest.Type questType, Quest.Difficulty difficulty)
	{
		this.id = -1;
		this.name = name;
		this.keywords = titleToKeywords(name);
		this.varbit = null;
		this.varPlayer = null;
		this.questType = questType;
		this.difficulty = difficulty;
		this.completeValue = -1;
	}

	private List<String> titleToKeywords(String title)
	{
		return Arrays.asList(title.toLowerCase().split(" "));
	}

	public QuestState getState(Client client)
	{
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
