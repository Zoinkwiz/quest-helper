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

import com.questhelper.panel.questorders.QuestOrders;
import com.questhelper.questhelpers.QuestDetails;
import com.questhelper.questhelpers.QuestHelper;
import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.Getter;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.util.Text;

@ConfigGroup("questhelper")
public interface QuestHelperConfig extends Config
{
	String QUEST_HELPER_GROUP = "questhelper";
	String QUEST_BACKGROUND_GROUP = "questhelpervars";

	enum QuestOrdering implements Comparator<QuestHelper>
	{
		/**
		 * Sort quests in alphabetical order
		 */
		A_TO_Z(QuestOrders.sortAToZ(), QuestFilter.QUEST, QuestFilter.MINIQUEST, QuestFilter.ACHIEVEMENT_DIARY,
			QuestFilter.SKILL_HELPER, QuestFilter.GENERIC_HELPER, QuestFilter.PLAYER_MADE_QUESTS),
		/**
		 * Sort quests in reverse alphabetical order
		 */
		Z_TO_A(QuestOrders.sortZToA(), QuestFilter.QUEST, QuestFilter.MINIQUEST, QuestFilter.ACHIEVEMENT_DIARY,
			QuestFilter.SKILL_HELPER, QuestFilter.GENERIC_HELPER, QuestFilter.PLAYER_MADE_QUESTS),
		/**
		 * Sort quests according to the Optimal Quest Guide (https://oldschool.runescape.wiki/w/Optimal_quest_guide)
		 */
		OPTIMAL(QuestOrders.sortOptimalOrder(), QuestFilter.OPTIMAL, QuestFilter.GENERIC_HELPER),
		/**
		 * Sort quests according to the Optimal Quest Guide (Ironman version) (https://oldschool.runescape.wiki/w/Optimal_quest_guide/Ironman)
		 */
		OPTIMAL_IRONMAN(QuestOrders.sortOptimalIronmanOrder(), QuestFilter.OPTIMAL, QuestFilter.GENERIC_HELPER),
		/**
		 * Sort quest by their release date (https://oldschool.runescape.wiki/w/Quests/Release_dates)
		 */
		RELEASE_DATE(QuestOrders.sortByRelease(), QuestFilter.QUEST, QuestFilter.MINIQUEST),

		QUEST_POINTS_ASC(QuestOrders.sortByQuestPointRewardAscending(), QuestFilter.QUEST),
		QUEST_POINTS_DESC(QuestOrders.sortByQuestPointRewardDescending(), QuestFilter.QUEST);

		private final Comparator<QuestHelper> comparator;
		@Getter
		private final QuestFilter[] sections;

		QuestOrdering(Comparator<QuestHelper> comparator, QuestFilter... sections)
		{
			this.comparator = comparator;
			this.sections = sections;

		}

		public List<QuestHelper> sort(Collection<QuestHelper> list)
		{
			return list.stream().sorted(this).collect(Collectors.toList());
		}

		@Override
		public int compare(QuestHelper o1, QuestHelper o2)
		{
			return comparator.compare(o1, o2);
		}
	}

	enum QuestFilter implements Predicate<QuestHelper>
	{
		/**
		 * Show all quests
		 */
		SHOW_ALL(q -> true),
		/**
		 * Show quests where the client meets the quest requirements
		 */
		SHOW_MEETS_REQS(QuestHelper::clientMeetsRequirements),
		/**
		 * Show all except generic helpers
		 */
		OPTIMAL("Optimal ordering",
			q -> q.getQuest().getQuestType() == QuestDetails.Type.P2P ||
				q.getQuest().getQuestType() == QuestDetails.Type.F2P ||
				q.getQuest().getQuestType() == QuestDetails.Type.MINIQUEST ||
				q.getQuest().getQuestType() == QuestDetails.Type.ACHIEVEMENT_DIARY,
			false),
		/**
		 * Show all free-to-play quests
		 */
		FREE_TO_PLAY(QuestDetails.Type.F2P),
		/**
		 * Show all members' quests
		 */
		MEMBERS(QuestDetails.Type.P2P),
		/**
		 * Show all quests
		 */
		QUEST("Quests", q -> q.getQuest().getQuestType() == QuestDetails.Type.P2P ||
			q.getQuest().getQuestType() == QuestDetails.Type.F2P),
		/**
		 * Show all miniquests (all miniquests are members' only)
		 */
		MINIQUEST("Miniquests", QuestDetails.Type.MINIQUEST),
		/**
		 * Show all achievement diaries
		 */
		ACHIEVEMENT_DIARY("Achievement diaries", QuestDetails.Type.ACHIEVEMENT_DIARY),
		/**
		 * Show all generic helpers
		 */
		GENERIC_HELPER("Generic helpers", QuestDetails.Type.GENERIC),
		/**
		 * Show all skills
		 */
		SKILL_HELPER("Skill helpers", q -> q.getQuest().getQuestType() == QuestDetails.Type.SKILL_P2P ||
			q.getQuest().getQuestType() == QuestDetails.Type.SKILL_F2P),
		/**
		 * Show all free-to-play skills
		 */
		SKILL_FREE_TO_PLAY(QuestDetails.Type.SKILL_F2P),
		/**
		 * Show all members' skills
		 */
		SKILL_MEMBERS(QuestDetails.Type.SKILL_P2P),

		PLAYER_MADE_QUESTS("Player-made quests", q -> q.getQuest().getQuestType() == QuestDetails.Type.PLAYER_QUEST);


		private final Predicate<QuestHelper> predicate;

		@Getter
		private final String displayName;

		protected final boolean shouldDisplay;

		QuestFilter(Predicate<QuestHelper> predicate)
		{
			this.predicate = predicate;
			this.displayName = Text.titleCase(this);
			this.shouldDisplay = true;
		}

		QuestFilter(String displayName, Predicate<QuestHelper> predicate)
		{
			this.predicate = predicate;
			this.displayName = displayName;
			this.shouldDisplay = true;
		}

		QuestFilter(String displayName, Predicate<QuestHelper> predicate, boolean shouldDisplay)
		{
			this.predicate = predicate;
			this.displayName = displayName;
			this.shouldDisplay = shouldDisplay;
		}

		@Override
		public boolean test(QuestHelper quest)
		{
			return predicate.test(quest);
		}

		public List<QuestHelper> test(Collection<QuestHelper> helpers)
		{

			return helpers.stream().filter(this).collect(Collectors.toList());
		}

		public static QuestFilter[] displayFilters()
		{
			return Arrays.stream(QuestFilter.values()).filter((questFilter -> questFilter.shouldDisplay)).toArray(QuestFilter[]::new);
		}
	}

	enum NpcHighlightStyle
	{
		NONE,
		OUTLINE,
		CONVEX_HULL,
		TILE
	}

	enum ObjectHighlightStyle
	{
		NONE,
		CLICK_BOX,
		OUTLINE,
	}

	enum GroundItemHighlightStyle
	{
		NONE,
		CLICK_BOX,
		OUTLINE,
		TILE
	}

	enum InventoryItemHighlightStyle
	{
		NONE,
		SQUARE,
		OUTLINE,
		FILLED_OUTLINE
	}

	@ConfigItem(
		keyName = "autostartQuests",
		name = "Auto start helper",
		description = "Automatically start the quest helper when you start a quest"
	)
	default boolean autoStartQuests()
	{
		return true;
	}

	@ConfigItem(
		keyName = "autoOpenSidebar",
		name = "Auto open sidebar",
		description = "Automatically opens the quest helper sidebar when you start a quest"
	)
	default boolean autoOpenSidebar()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showOverlayPanel",
		name = "Display overlay on screen",
		description = "Chose whether the overlay should be displayed on screen"
	)
	default boolean showOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "stewBoostsPanel",
		name = "Use Spicy stew for boosts",
		description = "Raises the boost maximum boost for certain skills to 5"
	)
	default boolean stewBoosts()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showFan",
		name = "Fan appears on quest completion",
		description = "Have someone appear to celebrate whenever you complete a quest"
	)
	default boolean showFan()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showRuneliteObjects",
		name = "Show player-made quest rewards",
		description = "Choose whether changes from player-made quests are displayed"
	)
	default boolean showRuneliteObjects()
	{
		return true;
	}

	@ConfigSection(
		position = 0,
		name = "Item highlighting",
		description = "Determines what items to highlight in the background"
	)
	String itemSection = "itemSection";

	@ConfigItem(
		position = 0,
		keyName = "highlightItemsBackground",
		name = "Always highlight needed items",
		description = "Highlight items you need for marked content type at all times",
		section = itemSection
	)
	default boolean highlightItemsBackground()
	{
		return false;
	}

	@ConfigItem(
		position = 1,
		keyName = "highlightNeededQuestItems",
		name = "Highlight active quest items",
		description = "Highlight all the active quest's items you're missing on the floor",
		section = itemSection
	)
	default boolean highlightNeededQuestItems()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "highlightNeededMiniquestItems",
		name = "Highlight miniquest items",
		description = "Highlight all miniquest items you're missing on the floor",
		section = itemSection
	)
	default boolean highlightNeededMiniquestItems()
	{
		return true;
	}

	@ConfigItem(
		position = 3,
		keyName = "highlightNeededAchievementDiaryItems",
		name = "Highlight achievement diary items",
		description = "Highlight all achievement diary items you're missing on the floor",
		section = itemSection
	)
	default boolean highlightNeededAchievementDiaryItems()
	{
		return true;
	}

	@ConfigSection(
		position = 1,
		name = "Quest Hints",
		description = "Determines what hints should be shown"
	)
	String hintsSection = "hintsSection";

	@ConfigItem(
		keyName = "showTextHighlight",
		name = "Highlight correct dialog",
		description = "Highlight correct dialog choices",
		section = hintsSection
	)
	default boolean showTextHighlight()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSymbolOverlay",
		name = "Display icons on NPCs and objects",
		description = "Choose whether NPCs should have icons marking them as the current target or not",
		section = hintsSection
	)
	default boolean showSymbolOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "highlightStyleNpcs",
		name = "Highlight style NPCs",
		description = "Choose the highlight style of the target NPCs",
		section = hintsSection
	)
	default NpcHighlightStyle highlightStyleNpcs()
	{
		return NpcHighlightStyle.OUTLINE;
	}

	@ConfigItem(
		keyName = "highlightStyleObjects",
		name = "Highlight style objects",
		description = "Choose the highlight style of the target objects",
		section = hintsSection
	)
	default ObjectHighlightStyle highlightStyleObjects()
	{
		return ObjectHighlightStyle.OUTLINE;
	}

	@ConfigItem(
		keyName = "highlightStyleGroundItems",
		name = "Highlight style ground items",
		description = "Choose the highlight style of the target items",
		section = hintsSection
	)
	default GroundItemHighlightStyle highlightStyleGroundItems()
	{
		return GroundItemHighlightStyle.OUTLINE;
	}

	@ConfigItem(
		keyName = "highlightStyleInventoryItems",
		name = "Highlight style inventory items",
		description = "Choose the highlight style of the target inventory items",
		section = hintsSection
	)
	default InventoryItemHighlightStyle highlightStyleInventoryItems()
	{
		return InventoryItemHighlightStyle.FILLED_OUTLINE;
	}

	@Range(
		min = 0,
		max = 50
	)
	@ConfigItem(
		keyName = "outlineThickness",
		name = "Outline thickness",
		description = "Choose the thickness of target model outlines",
		section = hintsSection
	)
	default int outlineThickness()
	{
		return 4;
	}

	@Range(
		min = 0,
		max = 4
	)
	@ConfigItem(
		keyName = "outlineFeathering",
		name = "Outline feathering",
		description = "Choose how the model outline is faded out",
		section = hintsSection
	)
	default int outlineFeathering()
	{
		return 4;
	}

	@ConfigItem(
		keyName = "showMiniMapArrow",
		name = "Display arrows on the mini-map and overworld",
		description = "Choose whether flashing arrows point to the next objective",
		section = hintsSection
	)
	default boolean showMiniMapArrow()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showWorldLines",
		name = "Display navigation paths",
		description = "Choose whether navigation paths are drawn to the next objective",
		section = hintsSection
	)
	default boolean showWorldLines()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showWidgetHints",
		name = "Display widget hints",
		description = "Choose whether important widget actions are highlighted",
		section = hintsSection
	)
	default boolean showWidgetHints()
	{
		return true;
	}

	@ConfigSection(
		position = 1,
		name = "Colours",
		description = "What colour each option can be"
	)
	String colorSection = "colorSection";

	@ConfigItem(
		keyName = "textHighlightColor",
		name = "Text highlight colour",
		description = "Change the colour of dialog choices highlighted by the helper",
		section = colorSection
	)
	default Color textHighlightColor()
	{
		return Color.BLUE;
	}

	@ConfigItem(
		keyName = "targetOverlayColor",
		name = "Color of target overlay",
		description = "Change the colour which target NPCs/Objects will be highlighted with",
		section = colorSection
	)
	default Color targetOverlayColor()
	{
		return Color.CYAN;
	}

	@ConfigItem(
		keyName = "passColour",
		name = "Colour of passed requirements/checks",
		description = "Change the colour that will indicate a check has passed",
		section = colorSection
	)
	default Color passColour()
	{
		return Color.GREEN;
	}

	@ConfigItem(
		keyName = "failColour",
		name = "Colour of failed requirements/checks",
		description = "Change the colour that will indicate a check has failed",
		section = colorSection
	)
	default Color failColour()
	{
		return Color.RED;
	}

	@ConfigItem(
		keyName = "boostColour",
		name = "Colour of boostable skill",
		description = "Change the colour that will indicate a skill level check has passed",
		section = colorSection
	)
	default Color boostColour()
	{
		return Color.ORANGE;
	}

	@ConfigItem(
		keyName = "debugColor",
		name = "Debug Colour",
		description = "debug",
		hidden = true,
		section = colorSection
	)
	default Color debugColor()
	{
		return Color.MAGENTA;
	}

	@ConfigSection(
		position = 2,
		name = "Quest Filters",
		description = "Determines which quests should be shown via the selected filter(s)"
	)
	String filterSection = "filterSection";

	@ConfigItem(
		keyName = "orderListBy",
		name = "Quest order",
		description = "Configures which way to order the quest list",
		position = 3,
		section = filterSection
	)
	default QuestOrdering orderListBy()
	{
		return QuestOrdering.A_TO_Z;
	}

	@ConfigItem(
		keyName = "filterListBy",
		name = "Filter",
		description = "Configures what to filter in the quest list",
		position = 1,
		section = filterSection
	)
	default QuestFilter filterListBy()
	{
		return QuestFilter.SHOW_ALL;
	}

	@ConfigItem(
		keyName = "questDifficulty",
		name = "Difficulty",
		description = "Configures what quest difficulty to show",
		position = 2,
		section = filterSection
	)
	default QuestDetails.Difficulty difficulty()
	{
		return QuestDetails.Difficulty.ALL;
	}

	@ConfigItem(
		keyName = "showCompletedQuests",
		name = "Show Completed Quests",
		description = "Will include completed quests in the other filter(s) that are chosen",
		position = 4,
		section = filterSection
	)
	default boolean showCompletedQuests()
	{
		return false;
	}
}
