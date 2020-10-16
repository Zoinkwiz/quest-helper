package com.questhelper;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.QuestState;
import net.runelite.api.ScriptID;

public enum QuestHelperQuest
{
	//Free Quests
	BLACK_KNIGHTS_FORTRESS(299, "Black Knights' Fortress", QuestVarPlayer.QUEST_BLACK_KNIGHTS_FORTRESS),
	COOKS_ASSISTANT(300, "Cook's Assistant", QuestVarPlayer.QUEST_COOKS_ASSISTANT),
	THE_CORSAIR_CURSE(301, "The Corsair Curse", QuestVarbits.QUEST_THE_CORSAIR_CURSE),
	DEMON_SLAYER(302, "Demon Slayer", QuestVarbits.QUEST_DEMON_SLAYER),
	DORICS_QUEST(303, "Doric's Quest", QuestVarPlayer.QUEST_DORICS_QUEST),
	DRAGON_SLAYER(304, "Dragon Slayer", QuestVarPlayer.QUEST_DRAGON_SLAYER),
	ERNEST_THE_CHICKEN(305, "Ernest the Chicken", QuestVarPlayer.QUEST_ERNEST_THE_CHICKEN),
	GOBLIN_DIPLOMACY(306, "Goblin Diplomacy", QuestVarbits.QUEST_GOBLIN_DIPLOMACY),
	IMP_CATCHER(307, "Imp Catcher", QuestVarPlayer.QUEST_IMP_CATCHER),
	THE_KNIGHTS_SWORD(308, "The Knight's Sword", QuestVarPlayer.QUEST_THE_KNIGHTS_SWORD),
	MISTHALIN_MYSTERY(309, "Misthalin Mystery", QuestVarbits.QUEST_MISTHALIN_MYSTERY),
	PIRATES_TREASURE(310, "Pirate's Treasure", QuestVarPlayer.QUEST_PIRATES_TREASURE),
	PRINCE_ALI_RESCUE(311, "Prince Ali Rescue", QuestVarPlayer.QUEST_PRINCE_ALI_RESCUE),
	THE_RESTLESS_GHOST(312, "The Restless Ghost", QuestVarPlayer.QUEST_THE_RESTLESS_GHOST),
	ROMEO__JULIET(313, "Romeo & Juliet", QuestVarPlayer.QUEST_ROMEO_AND_JULIET),
	RUNE_MYSTERIES(314, "Rune Mysteries", QuestVarPlayer.QUEST_RUNE_MYSTERIES),
	SHEEP_SHEARER(315, "Sheep Shearer", QuestVarPlayer.QUEST_SHEEP_SHEARER),
	SHIELD_OF_ARRAV_PHOENIX_GANG(316, "Shield of Arrav - Phoenix Gang", QuestVarPlayer.QUEST_SHIELD_OF_ARRAV),
	SHIELD_OF_ARRAV_BLACK_ARM_GANG(316, "Shield of Arrav - Black Arm Gang", QuestVarPlayer.QUEST_SHIELD_OF_ARRAV_STATE_146),
	VAMPYRE_SLAYER(1278, "Vampyre Slayer", QuestVarPlayer.QUEST_VAMPYRE_SLAYER),
	WITCHS_POTION(318, "Witch's Potion", QuestVarPlayer.QUEST_WITCHS_POTION),
	X_MARKS_THE_SPOT(550, "X Marks the Spot", QuestVarbits.QUEST_X_MARKS_THE_SPOT),

	//Members' Quests
	ANIMAL_MAGNETISM(331, "Animal Magnetism", QuestVarbits.QUEST_ANIMAL_MAGNETISM),
	ANOTHER_SLICE_OF_HAM(332, "Another Slice of H.A.M.", QuestVarbits.QUEST_ANOTHER_SLICE_OF_HAM),
	BETWEEN_A_ROCK(333, "Between a Rock...", QuestVarbits.QUEST_BETWEEN_A_ROCK),
	BIG_CHOMPY_BIRD_HUNTING(334, "Big Chompy Bird Hunting", QuestVarPlayer.QUEST_BIG_CHOMPY_BIRD_HUNTING),
	BIOHAZARD(335, "Biohazard", QuestVarPlayer.QUEST_BIOHAZARD),
	CABIN_FEVER(336, "Cabin Fever", QuestVarPlayer.QUEST_CABIN_FEVER),
	CLOCK_TOWER(337, "Clock Tower", QuestVarPlayer.QUEST_CLOCK_TOWER),
	COLD_WAR(338, "Cold War", QuestVarbits.QUEST_COLD_WAR),
	CONTACT(339, "Contact!", QuestVarbits.QUEST_CONTACT),
	CREATURE_OF_FENKENSTRAIN(340, "Creature of Fenkenstrain", QuestVarPlayer.QUEST_CREATURE_OF_FENKENSTRAIN),
	DARKNESS_OF_HALLOWVALE(341, "Darkness of Hallowvale", QuestVarbits.QUEST_DARKNESS_OF_HALLOWVALE),
	DEATH_PLATEAU(342, "Death Plateau", QuestVarPlayer.QUEST_DEATH_PLATEAU),
	DEATH_TO_THE_DORGESHUUN(343, "Death to the Dorgeshuun", QuestVarbits.QUEST_DEATH_TO_THE_DORGESHUUN),
	THE_DEPTHS_OF_DESPAIR(344, "The Depths of Despair", QuestVarbits.QUEST_THE_DEPTHS_OF_DESPAIR),
	DESERT_TREASURE(345, "Desert Treasure", QuestVarbits.QUEST_DESERT_TREASURE),
	DEVIOUS_MINDS(346, "Devious Minds", QuestVarbits.QUEST_DEVIOUS_MINDS),
	THE_DIG_SITE(347, "The Dig Site", QuestVarPlayer.QUEST_THE_DIG_SITE),
	DRAGON_SLAYER_II(348, "Dragon Slayer II", QuestVarbits.QUEST_DRAGON_SLAYER_II),
	DREAM_MENTOR(349, "Dream Mentor", QuestVarbits.QUEST_DREAM_MENTOR),
	DRUIDIC_RITUAL(350, "Druidic Ritual", QuestVarPlayer.QUEST_DRUIDIC_RITUAL),
	DWARF_CANNON(351, "Dwarf Cannon", QuestVarbits.QUEST_THE_GIANT_DWARF),
	EADGARS_RUSE(352, "Eadgar's Ruse", QuestVarPlayer.QUEST_EADGARS_RUSE),
	EAGLES_PEAK(353, "Eagles' Peak", QuestVarbits.QUEST_EAGLES_PEAK),
	ELEMENTAL_WORKSHOP_I(354, "Elemental Workshop I", QuestVarPlayer.QUEST_ELEMENTAL_WORKSHOP_I),
	ELEMENTAL_WORKSHOP_II(355, "Elemental Workshop II", QuestVarbits.QUEST_ELEMENTAL_WORKSHOP_II),
	ENAKHRAS_LAMENT(356, "Enakhra's Lament", QuestVarbits.QUEST_ENAKHRAS_LAMENT),
	ENLIGHTENED_JOURNEY(357, "Enlightened Journey", QuestVarbits.QUEST_ENLIGHTENED_JOURNEY),
	THE_EYES_OF_GLOUPHRIE(358, "The Eyes of Glouphrie", QuestVarbits.QUEST_THE_EYES_OF_GLOUPHRIE),
	FAIRYTALE_I__GROWING_PAINS(359, "Fairytale I - Growing Pains", QuestVarbits.QUEST_FAIRYTALE_I_GROWING_PAINS),
	FAIRYTALE_II__CURE_A_QUEEN(360, "Fairytale II - Cure a Queen", QuestVarbits.QUEST_FAIRYTALE_II_CURE_A_QUEEN),
	FAMILY_CREST(361, "Family Crest", QuestVarPlayer.QUEST_FAMILY_CREST),
	THE_FEUD(362, "The Feud", QuestVarbits.QUEST_THE_FEUD),
	FIGHT_ARENA(363, "Fight Arena", QuestVarPlayer.QUEST_FIGHT_ARENA),
	FISHING_CONTEST(364, "Fishing Contest", QuestVarPlayer.QUEST_FISHING_CONTEST),
	FORGETTABLE_TALE(365, "Forgettable Tale...", QuestVarbits.QUEST_FORGETTABLE_TALE),
	BONE_VOYAGE(366, "Bone Voyage", QuestVarbits.QUEST_BONE_VOYAGE),
	THE_FREMENNIK_ISLES(367, "The Fremennik Isles", QuestVarbits.QUEST_THE_FREMENNIK_ISLES),
	THE_FREMENNIK_TRIALS(368, "The Fremennik Trials", QuestVarPlayer.QUEST_THE_FREMENNIK_TRIALS),
	GARDEN_OF_TRANQUILLITY(369, "Garden of Tranquillity", QuestVarbits.QUEST_GARDEN_OF_TRANQUILLITY),
	GERTRUDES_CAT(370, "Gertrude's Cat", QuestVarPlayer.QUEST_GERTRUDES_CAT),
	GHOSTS_AHOY(371, "Ghosts Ahoy", QuestVarbits.QUEST_GHOSTS_AHOY),
	THE_GIANT_DWARF(372, "The Giant Dwarf", QuestVarbits.QUEST_THE_GIANT_DWARF),
	THE_GOLEM(373, "The Golem", QuestVarbits.QUEST_THE_GOLEM),
	THE_GRAND_TREE(374, "The Grand Tree", QuestVarPlayer.QUEST_THE_GRAND_TREE),
	THE_GREAT_BRAIN_ROBBERY(375, "The Great Brain Robbery", QuestVarPlayer.QUEST_THE_GREAT_BRAIN_ROBBERY),
	GRIM_TALES(376, "Grim Tales", QuestVarbits.QUEST_GRIM_TALES),
	THE_HAND_IN_THE_SAND(377, "The Hand in the Sand", QuestVarbits.QUEST_THE_HAND_IN_THE_SAND),
	HAUNTED_MINE(378, "Haunted Mine", QuestVarPlayer.QUEST_HAUNTED_MINE),
	HAZEEL_CULT(379, "Hazeel Cult", QuestVarPlayer.QUEST_HAZEEL_CULT),
	HEROES_QUEST(380, "Heroes' Quest", QuestVarPlayer.QUEST_HEROES_QUEST),
	HOLY_GRAIL(381, "Holy Grail", QuestVarPlayer.QUEST_HOLY_GRAIL),
	HORROR_FROM_THE_DEEP(382, "Horror from the Deep", QuestVarbits.QUEST_HORROR_FROM_THE_DEEP),
	ICTHLARINS_LITTLE_HELPER(383, "Icthlarin's Little Helper", QuestVarbits.QUEST_ICTHLARINS_LITTLE_HELPER),
	IN_AID_OF_THE_MYREQUE(384, "In Aid of the Myreque", QuestVarbits.QUEST_IN_AID_OF_THE_MYREQUE),
	IN_SEARCH_OF_THE_MYREQUE(385, "In Search of the Myreque", QuestVarPlayer.QUEST_IN_SEARCH_OF_THE_MYREQUE),
	JUNGLE_POTION(386, "Jungle Potion", QuestVarPlayer.QUEST_JUNGLE_POTION),
	KINGS_RANSOM(387, "King's Ransom", QuestVarbits.QUEST_KINGS_RANSOM),
	LEGENDS_QUEST(388, "Legends' Quest", QuestVarPlayer.QUEST_LEGENDS_QUEST),
	LOST_CITY(389, "Lost City", QuestVarPlayer.QUEST_LOST_CITY),
	THE_LOST_TRIBE(390, "The Lost Tribe", QuestVarbits.QUEST_THE_LOST_TRIBE),
	LUNAR_DIPLOMACY(391, "Lunar Diplomacy", QuestVarbits.QUEST_LUNAR_DIPLOMACY),
	MAKING_FRIENDS_WITH_MY_ARM(392, "Making Friends with My Arm", QuestVarbits.QUEST_MAKING_FRIENDS_WITH_MY_ARM),
	MAKING_HISTORY(393, "Making History", QuestVarbits.QUEST_MAKING_HISTORY),
	MERLINS_CRYSTAL(394, "Merlin's Crystal", QuestVarPlayer.QUEST_MERLINS_CRYSTAL),
	MONKEY_MADNESS_I(395, "Monkey Madness I", QuestVarPlayer.QUEST_MONKEY_MADNESS_I),
	MONKEY_MADNESS_II(396, "Monkey Madness II", QuestVarbits.QUEST_MONKEY_MADNESS_II),
	MONKS_FRIEND(397, "Monk's Friend", QuestVarPlayer.QUEST_MONKS_FRIEND),
	MOUNTAIN_DAUGHTER(398, "Mountain Daughter", QuestVarbits.QUEST_MOUNTAIN_DAUGHTER),
	MOURNINGS_END_PART_I(399, "Mourning's End Part I", QuestVarPlayer.QUEST_MOURNINGS_END_PART_I),
	MOURNINGS_END_PART_II(400, "Mourning's End Part II", QuestVarbits.QUEST_MOURNINGS_END_PART_II),
	MURDER_MYSTERY(401, "Murder Mystery", QuestVarPlayer.QUEST_MURDER_MYSTERY),
	MY_ARMS_BIG_ADVENTURE(402, "My Arm's Big Adventure", QuestVarbits.QUEST_MY_ARMS_BIG_ADVENTURE),
	NATURE_SPIRIT(403, "Nature Spirit", QuestVarPlayer.QUEST_NATURE_SPIRIT),
	OBSERVATORY_QUEST(404, "Observatory Quest", QuestVarPlayer.QUEST_OBSERVATORY_QUEST),
	OLAFS_QUEST(405, "Olaf's Quest", QuestVarbits.QUEST_OLAFS_QUEST),
	ONE_SMALL_FAVOUR(406, "One Small Favour", QuestVarPlayer.QUEST_ONE_SMALL_FAVOUR),
	PLAGUE_CITY(407, "Plague City", QuestVarPlayer.QUEST_PLAGUE_CITY),
	PRIEST_IN_PERIL(408, "Priest in Peril", QuestVarPlayer.QUEST_PRIEST_IN_PERIL),
	THE_QUEEN_OF_THIEVES(409, "The Queen of Thieves", QuestVarbits.QUEST_THE_QUEEN_OF_THIEVES),
	RAG_AND_BONE_MAN(410, "Rag and Bone Man", QuestVarPlayer.QUEST_RAG_AND_BONE_MAN),
	RAG_AND_BONE_MAN_II(411, "Rag and Bone Man II", QuestVarPlayer.QUEST_RAG_AND_BONE_MAN_II),
	RATCATCHERS(412, "Ratcatchers", QuestVarbits.QUEST_RATCATCHERS),
	RECIPE_FOR_DISASTER(413, "Recipe for Disaster", QuestVarbits.QUEST_RECIPE_FOR_DISASTER),
	RECIPE_FOR_DISASTER_START(413, "RFD - Start", QuestVarbits.QUEST_RECIPE_FOR_DISASTER),
//	RECIPE_FOR_DISASTER_DWARF(413, "RFD - Dwarf", QuestVarbits.QUEST_RECIPE_FOR_DISASTER_DWARF),
//	RECIPE_FOR_DISASTER_WARTFACE_AND_BENTNOZE(413, "RFD - Wartface & Bentnoze", QuestVarbits.QUEST_RECIPE_FOR_DISASTER_WARTFACE_AND_BENTNOZE),
	RECIPE_FOR_DISASTER_PIRATE_PETE(413, "RFD - Pirate Pete", QuestVarbits.QUEST_RECIPE_FOR_DISASTER_PIRATE_PETE),
	RECIPE_FOR_DISASTER_LUMBRIDGE_GUIDE(413, "RFD - Lumbridge Guide", QuestVarbits.QUEST_RECIPE_FOR_DISASTER_LUMBRIDGE_GUIDE),
//	RECIPE_FOR_DISASTER_EVIL_DAVE(413, "RFD - Evil Dave", QuestVarbits.QUEST_RECIPE_FOR_DISASTER_EVIL_DAVE),
	RECIPE_FOR_DISASTER_MONKEY_AMBASSADOR(413, "RFD - Monkey Ambassador", QuestVarbits.QUEST_RECIPE_FOR_DISASTER_MONKEY_AMBASSADOR),
	RECIPE_FOR_DISASTER_SIR_AMIK_VARZE(413, "RFD - Sir Amik Varze", QuestVarbits.QUEST_RECIPE_FOR_DISASTER_SIR_AMIK_VARZE),
	RECIPE_FOR_DISASTER_SKRACH_UGLOGWEE(413, "RFD - Skrach Uglogwee", QuestVarbits.QUEST_RECIPE_FOR_DISASTER_SKRACH_UGLOGWEE),
	RECIPE_FOR_DISASTER_FINALE(413, "RFD - Finale", QuestVarbits.QUEST_RECIPE_FOR_DISASTER),
	RECRUITMENT_DRIVE(414, "Recruitment Drive", QuestVarbits.QUEST_RECRUITMENT_DRIVE),
	REGICIDE(415, "Regicide", QuestVarPlayer.QUEST_REGICIDE),
	ROVING_ELVES(416, "Roving Elves", QuestVarPlayer.QUEST_ROVING_ELVES),
	ROYAL_TROUBLE(417, "Royal Trouble", QuestVarbits.QUEST_ROYAL_TROUBLE),
	RUM_DEAL(418, "Rum Deal", QuestVarPlayer.QUEST_RUM_DEAL),
	SCORPION_CATCHER(419, "Scorpion Catcher", QuestVarPlayer.QUEST_SCORPION_CATCHER),
	SEA_SLUG(420, "Sea Slug", QuestVarPlayer.QUEST_SEA_SLUG),
	SHADES_OF_MORTTON(421, "Shades of Mort'ton", QuestVarPlayer.QUEST_SHADES_OF_MORTTON),
	SHADOW_OF_THE_STORM(422, "Shadow of the Storm", QuestVarbits.QUEST_SHADOW_OF_THE_STORM),
	SHEEP_HERDER(423, "Sheep Herder", QuestVarPlayer.QUEST_SHEEP_HERDER),
	SHILO_VILLAGE(424, "Shilo Village", QuestVarPlayer.QUEST_SHILO_VILLAGE),
	THE_SLUG_MENACE(425, "The Slug Menace", QuestVarbits.QUEST_THE_SLUG_MENACE),
	A_SOULS_BANE(426, "A Soul's Bane", QuestVarbits.QUEST_A_SOULS_BANE),
	SPIRITS_OF_THE_ELID(427, "Spirits of the Elid", QuestVarbits.QUEST_SPIRITS_OF_THE_ELID),
	SWAN_SONG(428, "Swan Song", QuestVarbits.QUEST_SWAN_SONG),
	TAI_BWO_WANNAI_TRIO(429, "Tai Bwo Wannai Trio", QuestVarPlayer.QUEST_TAI_BWO_WANNAI_TRIO),
	A_TAIL_OF_TWO_CATS(430, "A Tail of Two Cats", QuestVarbits.QUEST_A_TAIL_OF_TWO_CATS),
	TALE_OF_THE_RIGHTEOUS(431, "Tale of the Righteous", QuestVarbits.QUEST_TALE_OF_THE_RIGHTEOUS),
	A_TASTE_OF_HOPE(432, "A Taste of Hope", QuestVarbits.QUEST_A_TASTE_OF_HOPE),
	TEARS_OF_GUTHIX(433, "Tears of Guthix", QuestVarbits.QUEST_TEARS_OF_GUTHIX),
	TEMPLE_OF_IKOV(434, "Temple of Ikov", QuestVarPlayer.QUEST_TEMPLE_OF_IKOV),
	THRONE_OF_MISCELLANIA(435, "Throne of Miscellania", QuestVarPlayer.QUEST_THRONE_OF_MISCELLANIA),
	THE_TOURIST_TRAP(436, "The Tourist Trap", QuestVarPlayer.QUEST_THE_TOURIST_TRAP),
	TOWER_OF_LIFE(437, "Tower of Life", QuestVarbits.QUEST_TOWER_OF_LIFE),
	TREE_GNOME_VILLAGE(438, "Tree Gnome Village", QuestVarPlayer.QUEST_TREE_GNOME_VILLAGE),
	TRIBAL_TOTEM(439, "Tribal Totem", QuestVarPlayer.QUEST_TRIBAL_TOTEM),
	TROLL_ROMANCE(440, "Troll Romance", QuestVarPlayer.QUEST_TROLL_ROMANCE),
	TROLL_STRONGHOLD(441, "Troll Stronghold", QuestVarPlayer.QUEST_TROLL_STRONGHOLD),
	UNDERGROUND_PASS(442, "Underground Pass", QuestVarPlayer.QUEST_UNDERGROUND_PASS),
	CLIENT_OF_KOUREND(443, "Client of Kourend", QuestVarbits.QUEST_CLIENT_OF_KOUREND),
	WANTED(444, "Wanted!", QuestVarbits.QUEST_WANTED),
	WATCHTOWER(445, "Watchtower", QuestVarPlayer.QUEST_WATCHTOWER),
	WATERFALL_QUEST(446, "Waterfall Quest", QuestVarPlayer.QUEST_WATERFALL_QUEST),
	WHAT_LIES_BELOW(447, "What Lies Below", QuestVarbits.QUEST_WHAT_LIES_BELOW),
	WITCHS_HOUSE(448, "Witch's House", QuestVarPlayer.QUEST_WITCHS_HOUSE),
	ZOGRE_FLESH_EATERS(449, "Zogre Flesh Eaters", QuestVarbits.QUEST_ZOGRE_FLESH_EATERS),
	THE_ASCENT_OF_ARCEUUS(542, "The Ascent of Arceuus", QuestVarbits.QUEST_THE_ASCENT_OF_ARCEUUS),
	THE_FORSAKEN_TOWER(543, "The Forsaken Tower", QuestVarbits.QUEST_THE_FORSAKEN_TOWER),
	SONG_OF_THE_ELVES(603, "Song of the Elves", QuestVarbits.QUEST_SONG_OF_THE_ELVES),
	THE_FREMENNIK_EXILES(718, "The Fremennik Exiles", QuestVarbits.QUEST_THE_FREMENNIK_EXILES),
	SINS_OF_THE_FATHER(1276, "Sins of the Father", QuestVarbits.QUEST_SINS_OF_THE_FATHER),
	A_PORCINE_OF_INTEREST(1690, "A Porcine of Interest", QuestVarbits.QUEST_A_PORCINE_OF_INTEREST),

	//Miniquests
	ENTER_THE_ABYSS(319, "Enter the Abyss", QuestVarPlayer.QUEST_ENTER_THE_ABYSS),
	ARCHITECTURAL_ALLIANCE(320, "Architectural Alliance", QuestVarbits.QUEST_ARCHITECTURAL_ALLIANCE),
	BEAR_YOUR_SOUL(321, "Bear your Soul", QuestVarbits.QUEST_BEAR_YOUR_SOUL),
	ALFRED_GRIMHANDS_BARCRAWL(322, "Alfred Grimhand's Barcrawl", QuestVarPlayer.QUEST_ALFRED_GRIMHANDS_BARCRAWL),
	CURSE_OF_THE_EMPTY_LORD(323, "Curse of the Empty Lord", QuestVarbits.QUEST_CURSE_OF_THE_EMPTY_LORD),
	ENCHANTED_KEY(324, "Enchanted Key", QuestVarbits.QUEST_ENCHANTED_KEY),
	THE_GENERALS_SHADOW(325, "The General's Shadow", QuestVarbits.QUEST_THE_GENERALS_SHADOW),
	SKIPPY_AND_THE_MOGRES(326, "Skippy and the Mogres", QuestVarbits.QUEST_SKIPPY_AND_THE_MOGRES),
	THE_MAGE_ARENA(327, "The Mage Arena", QuestVarPlayer.QUEST_THE_MAGE_ARENA),
	LAIR_OF_TARN_RAZORLOR(328, "Lair of Tarn Razorlor", QuestVarbits.QUEST_LAIR_OF_TARN_RAZORLOR),
	FAMILY_PEST(329, "Family Pest", QuestVarbits.QUEST_FAMILY_PEST),
	THE_MAGE_ARENA_II(330, "The Mage Arena II", QuestVarbits.QUEST_THE_MAGE_ARENA_II),
	IN_SEARCH_OF_KNOWLEDGE(602, "In Search of Knowledge", QuestVarbits.QUEST_IN_SEARCH_OF_KNOWLEDGE),
	DADDYS_HOME(1688, "Daddy's Home", QuestVarbits.QUEST_DADDYS_HOME);

	@Getter
	private final int id;

	@Getter
	private final String name;

	private final QuestVarbits varbit;

	private final QuestVarPlayer varPlayer;

	QuestHelperQuest(int id, String name, QuestVarbits varbit)
	{
		this.id = id;
		this.name = name;
		this.varbit = varbit;
		this.varPlayer = null;
	}

	QuestHelperQuest(int id, String name, QuestVarPlayer varPlayer)
	{
		this.id = id;
		this.name = name;
		this.varbit = null;
		this.varPlayer = varPlayer;
	}

	public QuestState getState(Client client)
	{
		client.runScript(ScriptID.QUESTLIST_PROGRESS, id);
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