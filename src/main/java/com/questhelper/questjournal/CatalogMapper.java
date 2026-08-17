/*
 * Copyright (c) 2026, nanopink
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
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.questjournal;

import com.questhelper.QuestHelperConfig;
import com.questhelper.QuestHelperConfig.QuestOrdering;
import com.questhelper.config.LeagueFiltering;
import com.questhelper.domain.AccountType;
import com.questhelper.panel.questorders.IronmanOptimalQuestGuide;
import com.questhelper.panel.questorders.OptimalQuestGuide;
import com.questhelper.panel.questorders.ReleaseDate;
import com.questhelper.questhelpers.QuestDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.rewards.QuestPointReward;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.WorldType;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.util.Text;

/** Maps Quest Helper catalog and account data to journal snapshots. */
@Singleton
class CatalogMapper
{
	private final Client client;

	@Inject
	CatalogMapper(Client client)
	{
		this.client = Objects.requireNonNull(client, "client");
	}

	CatalogData buildCatalog(Map<QuestHelper, net.runelite.api.QuestState> states)
	{
		if (states == null || states.isEmpty())
		{
			return new CatalogData(Collections.emptyList(), Collections.emptyMap(), 0, 0, 0);
		}

		List<QuestHelper> helpers = eligibleHelpers(states);
		Map<QuestHelperQuest, Map<JournalSnapshot.QuestOrder, Integer>> ranks =
			buildOrderRanks(helpers);
		List<JournalSnapshot.QuestListItem> quests = new ArrayList<>(helpers.size());
		Map<String, QuestHelper> helpersById = new LinkedHashMap<>();
		for (QuestHelper helper : helpers)
		{
			if (helper == null || helper.getQuest() == null)
			{
				continue;
			}
			QuestHelperQuest quest = helper.getQuest();
			quests.add(new JournalSnapshot.QuestListItem(
				quest.name(),
				displayTitle(helper),
				journalType(quest.getQuestType()),
				journalState(states.get(helper)),
				journalDifficulty(quest),
				isMembers(quest.getQuestType()),
				ranks.getOrDefault(quest, Collections.emptyMap())));
			helpersById.put(quest.name(), helper);
		}
		quests.sort(JournalSnapshot.QuestFilter.all().comparator());

		int completed = 0;
		int total = 0;
		int fallbackPoints = 0;
		for (Map.Entry<QuestHelper, net.runelite.api.QuestState> entry
			: states.entrySet())
		{
			QuestHelper helper = entry.getKey();
			if (!isCoreQuest(helper))
			{
				continue;
			}
			total++;
			if (entry.getValue() == net.runelite.api.QuestState.FINISHED)
			{
				completed++;
			}
			QuestPointReward reward = helper.getQuestPointReward();
			if (reward != null && reward.getPoints() > 0)
			{
				fallbackPoints += reward.getPoints();
			}
		}
		return new CatalogData(
			Collections.unmodifiableList(quests),
			Collections.unmodifiableMap(helpersById),
			completed,
			total,
			fallbackPoints);
	}

	static List<QuestHelper> eligibleHelpers(
		Map<QuestHelper, net.runelite.api.QuestState> states)
	{
		return states.keySet().stream()
			.filter(Objects::nonNull)
			.filter(LeagueFiltering::passesLeagueFilter)
			.collect(Collectors.toList());
	}

	Map<QuestHelperQuest, Map<JournalSnapshot.QuestOrder, Integer>> buildOrderRanks(
		List<QuestHelper> helpers)
	{
		List<QuestHelper> safeHelpers = helpers == null
			? Collections.emptyList()
			: helpers;
		Map<QuestHelperQuest, Map<JournalSnapshot.QuestOrder, Integer>> ranks =
			new LinkedHashMap<>();
		for (QuestHelper helper : safeHelpers)
		{
			if (helper != null && helper.getQuest() != null)
			{
				ranks.put(helper.getQuest(), new EnumMap<>(JournalSnapshot.QuestOrder.class));
			}
		}

		for (QuestOrdering order : QuestOrdering.values())
		{
			JournalSnapshot.QuestOrder journalOrder =
				JournalSnapshot.QuestOrder.valueOf(order.name());
			List<QuestHelper> ordered = order.sort(safeHelpers);
			Set<QuestHelperQuest> ranked = new LinkedHashSet<>();
			int rank = 0;
			for (QuestHelperConfig.QuestFilter section : order.getSections())
			{
				for (QuestHelper helper : ordered)
				{
					if (helper == null || helper.getQuest() == null)
					{
						continue;
					}
					QuestHelperQuest quest = helper.getQuest();
					if (!section.test(helper)
						|| !hasDefinedOrderPosition(order, quest)
						|| !ranked.add(quest))
					{
						continue;
					}
					Map<JournalSnapshot.QuestOrder, Integer> questRanks = ranks.get(quest);
					if (questRanks != null)
					{
						questRanks.put(journalOrder, rank++);
					}
				}
			}
		}

		Map<QuestHelperQuest, Map<JournalSnapshot.QuestOrder, Integer>> immutable =
			new LinkedHashMap<>();
		for (Map.Entry<QuestHelperQuest, Map<JournalSnapshot.QuestOrder, Integer>> entry
			: ranks.entrySet())
		{
			immutable.put(entry.getKey(), Collections.unmodifiableMap(entry.getValue()));
		}
		return Collections.unmodifiableMap(immutable);
	}

	private static boolean hasDefinedOrderPosition(
		QuestOrdering order,
		QuestHelperQuest quest)
	{
		switch (order)
		{
			case OPTIMAL:
				return quest.getQuestType() == QuestDetails.Type.GENERIC
					|| OptimalQuestGuide.getQuestList().contains(quest);
			case OPTIMAL_IRONMAN:
				return quest.getQuestType() == QuestDetails.Type.GENERIC
					|| IronmanOptimalQuestGuide.getQuestList().contains(quest);
			case RELEASE_DATE:
				return ReleaseDate.getQuestList().contains(quest);
			default:
				return true;
		}
	}

	JournalSnapshot.QuestProgress mapProgress(
		JournalSnapshot.QuestProgress current,
		boolean catalogAvailable,
		int completedQuestCount,
		int totalQuestCount,
		int fallbackTotalQuestPoints)
	{
		int completed = catalogAvailable ? completedQuestCount : 0;
		int total = catalogAvailable ? totalQuestCount : 0;
		int currentPoints = catalogAvailable
			? Math.max(0, client.getVarpValue(VarPlayerID.QP))
			: 0;
		int reportedMaximum = catalogAvailable
			? Math.max(0, client.getVarbitValue(VarbitID.QP_MAX))
			: 0;
		int maximumPoints = catalogAvailable
			? Math.max(
				currentPoints,
				reportedMaximum > 0 ? reportedMaximum : fallbackTotalQuestPoints)
			: 0;
		if (current.getCompletedQuestCount() == completed
			&& current.getTotalQuestCount() == total
			&& current.getCurrentQuestPoints() == currentPoints
			&& current.getTotalQuestPoints() == maximumPoints)
		{
			return current;
		}
		return new JournalSnapshot.QuestProgress(
			completed,
			total,
			currentPoints,
			maximumPoints);
	}

	JournalSnapshot.QuestMembership currentMembership()
	{
		return client.getWorldType() != null
			&& client.getWorldType().contains(WorldType.MEMBERS)
			? JournalSnapshot.QuestMembership.MEMBERS
			: JournalSnapshot.QuestMembership.FREE_TO_PLAY;
	}

	JournalSnapshot.QuestOrder currentOrder()
	{
		AccountType accountType = AccountType.get(client.getVarbitValue(VarbitID.IRONMAN));
		return accountType != null && accountType.isAnyIronman()
			? JournalSnapshot.QuestOrder.OPTIMAL_IRONMAN
			: JournalSnapshot.QuestOrder.OPTIMAL;
	}

	JournalSnapshot.QuestListOptions buildListOptions(
		JournalSnapshot.QuestMembership membership,
		JournalSnapshot.QuestOrder order)
	{
		JournalSnapshot.QuestMembership defaultMembership =
			membership == JournalSnapshot.QuestMembership.FREE_TO_PLAY
				? JournalSnapshot.QuestMembership.FREE_TO_PLAY
				: null;
		return new JournalSnapshot.QuestListOptions(defaultMembership, order);
	}

	JournalSnapshot.ActiveQuest buildActiveQuest(
		QuestHelper helper,
		List<JournalSnapshot.QuestListItem> catalog,
		Map<QuestHelper, net.runelite.api.QuestState> catalogState)
	{
		if (helper == null || helper.getQuest() == null)
		{
			return null;
		}
		String id = helper.getQuest().name();
		for (JournalSnapshot.QuestListItem item : catalog)
		{
			if (id.equals(item.getId()))
			{
				return new JournalSnapshot.ActiveQuest(id, item.getTitle(), item.getState());
			}
		}

		net.runelite.api.QuestState state = catalogState == null
			? null
			: catalogState.get(helper);
		if (state == null)
		{
			state = helper.getState(client);
		}
		return new JournalSnapshot.ActiveQuest(
			id,
			displayTitle(helper),
			journalState(state));
	}

	JournalSnapshot.QuestState currentQuestState(QuestHelper helper)
	{
		Objects.requireNonNull(helper, "helper");
		return journalState(helper.getState(client));
	}

	static boolean isCoreQuest(QuestHelper helper)
	{
		if (helper == null || helper.getQuest() == null || helper.getQuest().isDeveloperQuest())
		{
			return false;
		}
		QuestDetails.Type type = helper.getQuest().getQuestType();
		return type == QuestDetails.Type.F2P || type == QuestDetails.Type.P2P;
	}

	static JournalSnapshot.QuestType journalType(QuestDetails.Type type)
	{
		if (type == null)
		{
			return JournalSnapshot.QuestType.GENERIC;
		}
		switch (type)
		{
			case MINIQUEST:
				return JournalSnapshot.QuestType.MINIQUEST;
			case ACHIEVEMENT_DIARY:
				return JournalSnapshot.QuestType.ACHIEVEMENT_DIARY;
			case GENERIC:
				return JournalSnapshot.QuestType.GENERIC;
			case SKILL:
			case SKILL_F2P:
			case SKILL_P2P:
				return JournalSnapshot.QuestType.SKILL;
			case PLAYER_QUEST:
				return JournalSnapshot.QuestType.PLAYER_QUEST;
			case F2P:
			case P2P:
			default:
				return JournalSnapshot.QuestType.QUEST;
		}
	}

	static JournalSnapshot.QuestDifficulty journalDifficulty(QuestHelperQuest quest)
	{
		if (quest == null)
		{
			return JournalSnapshot.QuestDifficulty.SPECIAL;
		}
		QuestDetails.Difficulty difficulty = quest.getDifficulty();
		if (journalType(quest.getQuestType()) == JournalSnapshot.QuestType.QUEST)
		{
			try
			{
				JournalSnapshot.QuestDifficulty converted =
					JournalSnapshot.QuestDifficulty.valueOf(difficulty.name());
				if (converted == JournalSnapshot.QuestDifficulty.NOVICE
					|| converted == JournalSnapshot.QuestDifficulty.INTERMEDIATE
					|| converted == JournalSnapshot.QuestDifficulty.EXPERIENCED
					|| converted == JournalSnapshot.QuestDifficulty.MASTER
					|| converted == JournalSnapshot.QuestDifficulty.GRANDMASTER)
				{
					return converted;
				}
			}
			catch (IllegalArgumentException | NullPointerException ignored)
			{
				// Other Quest Helper values are not quest difficulties.
			}
		}
		JournalSnapshot.QuestDifficulty diaryTier = achievementDiaryTier(quest);
		return diaryTier == null ? JournalSnapshot.QuestDifficulty.SPECIAL : diaryTier;
	}

	static JournalSnapshot.QuestDifficulty achievementDiaryTier(QuestHelperQuest quest)
	{
		if (quest == null || quest.getQuestType() != QuestDetails.Type.ACHIEVEMENT_DIARY)
		{
			return null;
		}
		String enumName = quest.name();
		String displayName = quest.getName() == null
			? ""
			: " " + quest.getName().toUpperCase(Locale.ROOT) + " ";
		for (JournalSnapshot.QuestDifficulty tier : Arrays.asList(
			JournalSnapshot.QuestDifficulty.EASY,
			JournalSnapshot.QuestDifficulty.MEDIUM,
			JournalSnapshot.QuestDifficulty.HARD,
			JournalSnapshot.QuestDifficulty.ELITE))
		{
			if (enumName.endsWith("_" + tier.name())
				|| displayName.contains(" " + tier.name() + " DIARY "))
			{
				return tier;
			}
		}
		return null;
	}

	static boolean isMembers(QuestDetails.Type type)
	{
		return type == QuestDetails.Type.P2P
			|| type == QuestDetails.Type.MINIQUEST
			|| type == QuestDetails.Type.ACHIEVEMENT_DIARY
			|| type == QuestDetails.Type.SKILL_P2P;
	}

	static JournalSnapshot.QuestState journalState(net.runelite.api.QuestState state)
	{
		if (state == net.runelite.api.QuestState.FINISHED)
		{
			return JournalSnapshot.QuestState.COMPLETE;
		}
		if (state == net.runelite.api.QuestState.IN_PROGRESS)
		{
			return JournalSnapshot.QuestState.IN_PROGRESS;
		}
		return JournalSnapshot.QuestState.NOT_STARTED;
	}

	static String normalizeText(String text)
	{
		if (text == null)
		{
			return "";
		}
		String withLines = text
			.replace("</br>", "\n")
			.replace("<br>", "\n")
			.replace("<br/>", "\n");
		return Text.removeTags(withLines).replace('\u00A0', ' ').trim();
	}

	static String displayTitle(QuestHelper helper)
	{
		String title = normalizeText(helper.getDisplayedQuestName());
		return title.isEmpty()
			? normalizeText(helper.getQuest().getName())
			: title;
	}

	@Getter
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	static final class CatalogData
	{
		private final List<JournalSnapshot.QuestListItem> quests;
		private final Map<String, QuestHelper> helpersById;
		private final int completedQuestCount;
		private final int totalQuestCount;
		private final int fallbackTotalQuestPoints;
	}
}
