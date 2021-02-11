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
import com.questhelper.questhelpers.Quest;
import com.questhelper.questhelpers.QuestHelper;
import java.awt.Color;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("questhelper")
public interface QuestHelperConfig extends Config
{
	enum QuestOrdering implements Comparator<QuestHelper>
	{
		/** Sort quests in alphabetical order */
		A_TO_Z(QuestOrders.sortAToZ()),
		/** Sort quests in reverse alphabetical order */
		Z_TO_A(QuestOrders.sortZToA()),
		/** Sort quests according to the Optimal Quest Guide (https://oldschool.runescape.wiki/w/Optimal_quest_guide) */
		OPTIMAL(QuestOrders.sortOptimalOrder()),
		;

		private final Comparator<QuestHelper> comparator;
		QuestOrdering(Comparator<QuestHelper> comparator) {
			this.comparator = comparator;
		}

		public List<QuestHelper> sort(Collection<QuestHelper> list) {
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
		/** Show all quests */
		SHOW_ALL(q -> true),
		/** Show quests where the client meets the quest requirements */
		SHOW_MEETS_REQS(QuestHelper::clientMeetsRequirements),
		/** Show all free-to-play quests */
		FREE_TO_PLAY(Quest.Type.F2P),
		/** Show all members' quests */
		MEMBERS(Quest.Type.P2P),
		/** Show all miniquests (all miniquests are members' only) */
		MINIQUEST(Quest.Type.MINIQUEST),
		;

		private final Predicate<QuestHelper> predicate;

		QuestFilter(Predicate<QuestHelper> predicate) {
			this.predicate = predicate;
		}

		@Override
		public boolean test(QuestHelper quest) {
			return predicate.test(quest);
		}

		public List<QuestHelper> test(Collection<QuestHelper> helpers) {

			return helpers.stream().filter(this).collect(Collectors.toList());
		}
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
		keyName = "showTextHighlight",
		name = "Highlight correct dialog",
		description = "Highlight correct dialog choices"
	)
	default boolean showTextHighlight()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSymbolOverlay",
		name = "Display icons on NPCs and objects",
		description = "Choose whether NPCs should icons marking them as the current target or not"
	)
	default boolean showSymbolOverlay()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showMiniMapArrow",
			name = "Display arrows on the mini-map and overworld",
			description = "Choose whether flashing arrows point to the next objective"
	)
	default boolean showMiniMapArrow()
	{
		return true;
	}
	
	@ConfigSection(
		position = 1,
		name = "Colors",
		description = "What color each option can be"
	)
	String colorSection = "colorSection";

	@ConfigItem(
		keyName = "textHighlightColor",
		name = "Text highlight color",
		description = "Change the color of dialog choices highlighted by the helper",
		section = colorSection
	)
	default Color textHighlightColor()
	{
		return Color.CYAN.darker();
	}

	@ConfigItem(
		keyName = "targetOverlayColor",
		name = "Color of target overlay",
		description = "Change the color which target NPCs/Objects will be highlighted with",
		section = colorSection
	)
	default Color targetOverlayColor()
	{
		return Color.CYAN;
	}

	@ConfigItem(
		keyName = "debugColor",
		name = "Debug Color",
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
	default Quest.Difficulty difficulty()
	{
		return Quest.Difficulty.ALL;
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
