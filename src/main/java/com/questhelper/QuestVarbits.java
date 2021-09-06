package com.questhelper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum QuestVarbits
{
	QUEST_TAB(8168),

	/**
	 * f2p Quest varbits, these don't hold the completion value.
	 */
	QUEST_BELOW_ICE_MOUNTAIN(12063),
	QUEST_DEMON_SLAYER(2561),
	QUEST_GOBLIN_DIPLOMACY(2378),
	QUEST_MISTHALIN_MYSTERY(3468),
	QUEST_THE_CORSAIR_CURSE(6071),
	QUEST_X_MARKS_THE_SPOT(8063),

	/**
	 * member Quest varbits, these don't hold the completion value.
	 */
	QUEST_ANIMAL_MAGNETISM(3185),
	QUEST_BETWEEN_A_ROCK(299),
	QUEST_CONTACT(3274),
	QUEST_ZOGRE_FLESH_EATERS(487),
	QUEST_DARKNESS_OF_HALLOWVALE(2573),
	QUEST_DEATH_TO_THE_DORGESHUUN(2258),
	QUEST_DESERT_TREASURE(358),
	QUEST_DEVIOUS_MINDS(1465),
	QUEST_EAGLES_PEAK(2780),
	QUEST_ELEMENTAL_WORKSHOP_II(2639),
	QUEST_ENAKHRAS_LAMENT(1560),
	QUEST_ENLIGHTENED_JOURNEY(2866),
	QUEST_THE_EYES_OF_GLOUPHRIE(2497),
	QUEST_FAIRYTALE_I_GROWING_PAINS(1803),
	QUEST_FAIRYTALE_II_CURE_A_QUEEN(2326),
	QUEST_THE_FEUD(334),
	QUEST_FORGETTABLE_TALE(822),
	QUEST_GARDEN_OF_TRANQUILLITY(961),
	QUEST_GHOSTS_AHOY(217),
	QUEST_THE_GIANT_DWARF(571),
	QUEST_THE_GOLEM(346),
	QUEST_THE_HAND_IN_THE_SAND(1527),
	QUEST_HORROR_FROM_THE_DEEP(34),
	QUEST_ICTHLARINS_LITTLE_HELPER(418),
	QUEST_IN_AID_OF_THE_MYREQUE(1990),
	QUEST_THE_LOST_TRIBE(532),
	QUEST_LUNAR_DIPLOMACY(2448),
	QUEST_MAKING_HISTORY(1383),
	QUEST_MOUNTAIN_DAUGHTER(260),
	QUEST_MOURNINGS_END_PART_II(1103),
	QUEST_MY_ARMS_BIG_ADVENTURE(2790),
	QUEST_RATCATCHERS(1404),
	QUEST_RECIPE_FOR_DISASTER(1850),
    QUEST_RECIPE_FOR_DISASTER_DWARF(1892),
	QUEST_RECIPE_FOR_DISASTER_WARTFACE_AND_BENTNOZE(1867),
	QUEST_RECIPE_FOR_DISASTER_PIRATE_PETE(1895),
	QUEST_RECIPE_FOR_DISASTER_LUMBRIDGE_GUIDE(1896),
	QUEST_RECIPE_FOR_DISASTER_EVIL_DAVE(1878),
	QUEST_RECIPE_FOR_DISASTER_SIR_AMIK_VARZE(1910),
	QUEST_RECIPE_FOR_DISASTER_SKRACH_UGLOGWEE(1904),
	QUEST_RECIPE_FOR_DISASTER_MONKEY_AMBASSADOR(1914),
	QUEST_RECRUITMENT_DRIVE(657),
	QUEST_ROYAL_TROUBLE(2140),
	QUEST_THE_SLUG_MENACE(2610),
	QUEST_SHADOW_OF_THE_STORM(1372),
	QUEST_A_SOULS_BANE(2011),
	QUEST_SPIRITS_OF_THE_ELID(1444),
	QUEST_SWAN_SONG(2098),
	QUEST_A_TAIL_OF_TWO_CATS(1028),
	QUEST_TEARS_OF_GUTHIX(451),
	QUEST_WANTED(1051),
	QUEST_COLD_WAR(3293),
	QUEST_THE_FREMENNIK_ISLES(3311),
	QUEST_TOWER_OF_LIFE(3337),
	QUEST_WHAT_LIES_BELOW(3523),
	QUEST_OLAFS_QUEST(3534),
	QUEST_ANOTHER_SLICE_OF_HAM(3550),
	QUEST_DREAM_MENTOR(3618),
	QUEST_GRIM_TALES(2783),
	QUEST_KINGS_RANSOM(3888),
	QUEST_MONKEY_MADNESS_II(5027),
	QUEST_CLIENT_OF_KOUREND(5619),
	QUEST_BONE_VOYAGE(5795),
	QUEST_THE_QUEEN_OF_THIEVES(6037),
	QUEST_THE_DEPTHS_OF_DESPAIR(6027),
	QUEST_DRAGON_SLAYER_II(6104),
	QUEST_TALE_OF_THE_RIGHTEOUS(6358),
	QUEST_A_TASTE_OF_HOPE(6396),
	QUEST_MAKING_FRIENDS_WITH_MY_ARM(6528),
	QUEST_THE_ASCENT_OF_ARCEUUS(7856),
	QUEST_THE_FORSAKEN_TOWER(7796),
	QUEST_SONG_OF_THE_ELVES(9016),
	QUEST_THE_FREMENNIK_EXILES(9459),
	QUEST_SINS_OF_THE_FATHER(7255),
	QUEST_A_PORCINE_OF_INTEREST(10582),
	QUEST_GETTING_AHEAD(693),
	QUEST_A_KINGDOM_DIVIDED(12296),
	QUEST_A_NIGHT_AT_THE_THEATRE(12276),

	/**
	 * mini-quest varbits, these don't hold the completion value.
	 */
	QUEST_ARCHITECTURAL_ALLIANCE(4982),
	QUEST_BEAR_YOUR_SOUL(5078),
	QUEST_CURSE_OF_THE_EMPTY_LORD(821),
	QUEST_ENCHANTED_KEY(1391),
	QUEST_THE_GENERALS_SHADOW(3330),
	QUEST_SKIPPY_AND_THE_MOGRES(1344),
	QUEST_LAIR_OF_TARN_RAZORLOR(3290),
	QUEST_FAMILY_PEST(5347),
	QUEST_THE_MAGE_ARENA_II(6067),
	QUEST_IN_SEARCH_OF_KNOWLEDGE(8403),
	QUEST_DADDYS_HOME(10570),

	// Fake miniquests
	KNIGHT_WAVES_TRAINING_GROUNDS(3909),

	// Achievement Diaries

	// Ardougne
	ACHIEVEMENT_DIARY_ARDOUGNE_EASY(4499),
	ACHIEVEMENT_DIARY_ARDOUGNE_MEDIUM(4500),
	ACHIEVEMENT_DIARY_ARDOUGNE_HARD(4501),
	ACHIEVEMENT_DIARY_ARDOUGNE_ELITE(4502),

	// Desert
	ACHIEVEMENT_DIARY_DESERT_EASY(4523),
	ACHIEVEMENT_DIARY_DESERT_MEDIUM(4524),
	ACHIEVEMENT_DIARY_DESERT_HARD(4525),
	ACHIEVEMENT_DIARY_DESERT_ELITE(4526),

	// Falador
	ACHIEVEMENT_DIARY_FALADOR_EASY(4503),
	ACHIEVEMENT_DIARY_FALADOR_MEDIUM(4504),
	ACHIEVEMENT_DIARY_FALADOR_HARD(4505),
	ACHIEVEMENT_DIARY_FALADOR_ELITE(4506),

	// Fremennik
	ACHIEVEMENT_DIARY_FREMENNIK_EASY(4531),
	ACHIEVEMENT_DIARY_FREMENNIK_MEDIUM(4532),
	ACHIEVEMENT_DIARY_FREMENNIK_HARD(4533),
	ACHIEVEMENT_DIARY_FREMENNIK_ELITE(4534),

	// Kandarin
	ACHIEVEMENT_DIARY_KANDARIN_EASY(4515),
	ACHIEVEMENT_DIARY_KANDARIN_MEDIUM(4516),
	ACHIEVEMENT_DIARY_KANDARIN_HARD(4517),
	ACHIEVEMENT_DIARY_KANDARIN_ELITE(4518),

	// Karamja
	ACHIEVEMENT_DIARY_KARAMJA_EASY(3577),
	ACHIEVEMENT_DIARY_KARAMJA_MEDIUM(3598),
	ACHIEVEMENT_DIARY_KARAMJA_HARD(3610),
	ACHIEVEMENT_DIARY_KARAMJA_ELITE(4567),

	// Kourend & Kebos
	ACHIEVEMENT_DIARY_KOUREND_EASY(7929),
	ACHIEVEMENT_DIARY_KOUREND_MEDIUM(7930),
	ACHIEVEMENT_DIARY_KOUREND_HARD(7931),
	ACHIEVEMENT_DIARY_KOUREND_ELITE(7932),

	// Lumbridge & Draynor
	ACHIEVEMENT_DIARY_LUMBRIDGE_EASY(4523),
	ACHIEVEMENT_DIARY_LUMBRIDGE_MEDIUM(4524),
	ACHIEVEMENT_DIARY_LUMBRIDGE_HARD(4525),
	ACHIEVEMENT_DIARY_LUMBRIDGE_ELITE(4526),

	// Morytania
	ACHIEVEMENT_DIARY_MORYTANIA_EASY(4527),
	ACHIEVEMENT_DIARY_MORYTANIA_MEDIUM(4528),
	ACHIEVEMENT_DIARY_MORYTANIA_HARD(4529),
	ACHIEVEMENT_DIARY_MORYTANIA_ELITE(4530),

	// Varrock
	ACHIEVEMENT_DIARY_VARROCK_EASY(4519),
	ACHIEVEMENT_DIARY_VARROCK_MEDIUM(4520),
	ACHIEVEMENT_DIARY_VARROCK_HARD(4521),
	ACHIEVEMENT_DIARY_VARROCK_ELITE(4522),

	// Western Provinces
	// TODO: Fix incorrect values!
	ACHIEVEMENT_DIARY_WESTERN_EASY(4519),
	ACHIEVEMENT_DIARY_WESTERN_MEDIUM(4520),
	ACHIEVEMENT_DIARY_WESTERN_HARD(4521),
	ACHIEVEMENT_DIARY_WESTERN_ELITE(4522),

	// Wilderness
	ACHIEVEMENT_DIARY_WILDERNESS_EASY(4535),
	ACHIEVEMENT_DIARY_WILDERNESS_MEDIUM(4536),
	ACHIEVEMENT_DIARY_WILDERNESS_HARD(4537),
	ACHIEVEMENT_DIARY_WILDERNESS_ELITE(4538),

	CUTSCENE(542),
	DIALOG_CHOICE(5983);

	private final int id;
}
