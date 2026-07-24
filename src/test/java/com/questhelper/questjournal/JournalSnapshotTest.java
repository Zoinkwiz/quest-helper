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
package com.questhelper.questjournal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JournalSnapshotTest
{
	@Test
	public void activeQuestIsIndependentFromTheViewedQuestAndCanBeAbsent()
	{
		JournalSnapshot.ActiveQuest active = new JournalSnapshot.ActiveQuest(
			"COOKS_ASSISTANT",
			"Cook's Assistant",
			JournalSnapshot.QuestState.IN_PROGRESS);
		JournalSnapshot snapshot = new JournalSnapshot(
			Collections.emptyList(),
			null,
			active,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 1, 0, 1));

		assertEquals("COOKS_ASSISTANT", snapshot.getActiveQuest().getId());
		assertEquals("Cook's Assistant", snapshot.getActiveQuest().getTitle());
		assertEquals(JournalSnapshot.QuestState.IN_PROGRESS, snapshot.getActiveQuest().getState());
		assertEquals(
			active,
			new JournalSnapshot.ActiveQuest(
				"COOKS_ASSISTANT",
				"Cook's Assistant",
				JournalSnapshot.QuestState.IN_PROGRESS));
		assertNull(new JournalSnapshot(
			Collections.emptyList(),
			null,
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 0, 0, 0)).getActiveQuest());
	}

	@Test
	public void snapshotDefensivelyCopiesEveryCollection()
	{
		List<JournalSnapshot.Objective> objectives = new ArrayList<>();
		objectives.add(objective(
			"Begin the test quest.",
			JournalSnapshot.ObjectiveState.AVAILABLE,
			true));
		List<JournalSnapshot.Requirement> requirements = new ArrayList<>();
		requirements.add(requirement(
			"Have a testing framework.",
			JournalSnapshot.RequirementState.MET));
		List<JournalSnapshot.Requirement> recommendations = new ArrayList<>();
		recommendations.add(new JournalSnapshot.Requirement(
			"Bring patience.",
			JournalSnapshot.RequirementState.BANKED,
			null,
			Collections.singletonList(JournalSnapshot.ItemLocation.BANK),
			"",
			"",
			JournalSnapshot.IconIdentity.none(),
			"",
			""));
		List<String> enemies = new ArrayList<>();
		enemies.add("A dangerous test double");
		List<JournalSnapshot.Reward> rewards = new ArrayList<>();
		rewards.add(new JournalSnapshot.Reward(
			"Immutable collection coverage.",
			JournalSnapshot.IconIdentity.none()));
		List<String> notes = new ArrayList<>();
		notes.add("A useful note.");

		JournalSnapshot.QuestOverview overview = overview("test_quest", "Test Quest");
		JournalSnapshot.SelectedQuest selectedQuest = new JournalSnapshot.SelectedQuest(
			overview,
			objectives,
			requirements,
			recommendations,
			enemies,
			rewards,
			notes);
		List<JournalSnapshot.QuestListItem> quests = new ArrayList<>();
		quests.add(listItem(overview));
		JournalSnapshot snapshot = new JournalSnapshot(
			quests,
			selectedQuest,
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 0, 0, 0));

		objectives.clear();
		requirements.clear();
		recommendations.clear();
		enemies.clear();
		rewards.clear();
		notes.clear();
		quests.clear();

		assertEquals(1, snapshot.getQuests().size());
		assertEquals(1, snapshot.getSelectedQuest().getObjectives().size());
		assertEquals(1, snapshot.getSelectedQuest().getRequirements().size());
		assertEquals("Bring patience.", snapshot.getSelectedQuest().getRecommendations().get(0).getText());
		assertEquals("Located in: Bank", snapshot.getSelectedQuest().getRecommendations().get(0).getLocationHint());
		assertEquals(
			Collections.singletonList("A dangerous test double"),
			snapshot.getSelectedQuest().getEnemies());
		assertEquals(1, snapshot.getSelectedQuest().getRewards().size());
		assertEquals(Collections.singletonList("A useful note."), snapshot.getSelectedQuest().getNotes());

		assertUnmodifiable(snapshot.getQuests(), listItem(overview));
		assertUnmodifiable(
			snapshot.getSelectedQuest().getObjectives(),
			objective("Another objective.", JournalSnapshot.ObjectiveState.LOCKED, false));
		assertUnmodifiable(
			snapshot.getSelectedQuest().getRequirements(),
			requirement("Another requirement.", JournalSnapshot.RequirementState.UNKNOWN));
		assertUnmodifiable(
			snapshot.getSelectedQuest().getRecommendations(),
			requirement(
				"Another recommendation.",
				JournalSnapshot.RequirementState.UNKNOWN));
		assertUnmodifiable(snapshot.getSelectedQuest().getEnemies(), "Another enemy.");
		assertUnmodifiable(
			snapshot.getSelectedQuest().getRewards(),
			new JournalSnapshot.Reward(
				"Another reward detail.",
				JournalSnapshot.IconIdentity.none()));
		assertUnmodifiable(snapshot.getSelectedQuest().getNotes(), "Another note.");
	}

	@Test
	public void requirementsExposeLocationsAndLinkedQuestTargets()
	{
		JournalSnapshot.Requirement requirement = new JournalSnapshot.Requirement(
			"Complete Cook's Assistant",
			JournalSnapshot.RequirementState.UNMET,
			null,
			java.util.Arrays.asList(
				JournalSnapshot.ItemLocation.INVENTORY,
			JournalSnapshot.ItemLocation.GROUP_STORAGE),
			"COOKS_ASSISTANT",
			"Cook's Assistant",
			JournalSnapshot.IconIdentity.quest(),
			"",
			"");

		assertTrue(requirement.hasLinkedQuest());
		assertEquals("COOKS_ASSISTANT", requirement.getLinkedQuestId());
		assertEquals("Cook's Assistant", requirement.getLinkedQuestTitle());
		assertEquals("Located in: Inventory, Group storage", requirement.getLocationHint());
		assertEquals(JournalSnapshot.IconType.QUEST, requirement.getIcon().getType());
		assertUnmodifiable(requirement.getLocations(), JournalSnapshot.ItemLocation.BANK);
	}

	@Test
	public void requirementsRetainOptionalMultilineQuestHelperGuidance()
	{
		JournalSnapshot.Requirement requirement = new JournalSnapshot.Requirement(
			"Bronze axe",
			JournalSnapshot.RequirementState.UNMET,
			null,
			Collections.emptyList(),
			"",
			"",
			JournalSnapshot.IconIdentity.item(1351, 1),
			"",
			"Take one from the stump.\nIt is south of the house.");

		assertEquals(
			"Take one from the stump.\nIt is south of the house.",
			requirement.getHelpText());
	}

	@Test
	public void requirementWikiDestinationsAcceptOnlyTheOfficialHttpsHost()
	{
		String official =
			"https://oldschool.runescape.wiki/w/Special:Lookup?type=item&id=1351#Item_sources";
		String questHelperFragment =
			"https://oldschool.runescape.wiki/w/Belladonna#Gloves#Item_sources";
		JournalSnapshot.Requirement accepted = requirementWithWikiUrl(official);
		JournalSnapshot.Requirement acceptedQuestHelperFragment =
			requirementWithWikiUrl(questHelperFragment);
		JournalSnapshot.Requirement insecure = requirementWithWikiUrl(
			"http://oldschool.runescape.wiki/w/Bronze_axe");
		JournalSnapshot.Requirement lookalike = requirementWithWikiUrl(
			"https://oldschool.runescape.wiki.example.com/w/Bronze_axe");

		assertTrue(accepted.hasWikiUrl());
		assertEquals(official, accepted.getWikiUrl());
		assertEquals(questHelperFragment, acceptedQuestHelperFragment.getWikiUrl());
		assertFalse(insecure.hasWikiUrl());
		assertEquals("", insecure.getWikiUrl());
		assertFalse(lookalike.hasWikiUrl());
	}

	@Test
	public void iconIdentityAndQuestProgressUseValidatedValues()
	{
		JournalSnapshot.IconIdentity itemIcon = JournalSnapshot.IconIdentity.item(1351, 1);
		JournalSnapshot.IconIdentity stackedItemIcon = JournalSnapshot.IconIdentity.item(995, 5);
		JournalSnapshot.IconIdentity skillIcon = JournalSnapshot.IconIdentity.skill("WOODCUTTING");
		JournalSnapshot.QuestProgress progress = new JournalSnapshot.QuestProgress(42, 100, 87, 200);

		assertEquals(JournalSnapshot.IconType.ITEM, itemIcon.getType());
		assertEquals(Integer.valueOf(1351), itemIcon.getItemId());
		assertEquals(1, itemIcon.getQuantity());
		assertEquals(Integer.valueOf(995), stackedItemIcon.getItemId());
		assertEquals(5, stackedItemIcon.getQuantity());
		assertEquals(JournalSnapshot.IconType.SKILL, skillIcon.getType());
		assertEquals("WOODCUTTING", skillIcon.getSkill());
		assertThrows(
			IllegalArgumentException.class,
			() -> JournalSnapshot.IconIdentity.item(995, 0));
		assertEquals(42, progress.getCompletedQuestCount());
		assertEquals(100, progress.getTotalQuestCount());
		assertEquals(87, progress.getCurrentQuestPoints());
		assertEquals(200, progress.getTotalQuestPoints());
	}

	@Test
	public void titleSearchIsCaseInsensitiveTrimmedAndImmutable()
	{
		JournalSnapshot.QuestFilter all = JournalSnapshot.QuestFilter.all();
		JournalSnapshot.QuestFilter filtered = all.withSearchText("  aSsIsTaNt  ");
		JournalSnapshot.QuestListItem quest = quest(
			"cooks_assistant",
			"Cook's Assistant",
			JournalSnapshot.QuestType.QUEST,
			JournalSnapshot.QuestState.COMPLETE,
			JournalSnapshot.QuestDifficulty.NOVICE,
			false);

		assertEquals("", all.getSearchText());
		assertEquals("  aSsIsTaNt  ", filtered.getSearchText());
		assertTrue(filtered.matches(quest));
		assertFalse(filtered
			.withStateSelections(Collections.singleton(JournalSnapshot.QuestState.NOT_STARTED))
			.matches(quest));
		assertFalse(all.withSearchText("Lumbridge").matches(quest));
		assertFalse(all.withSearchText("Novice").matches(quest));
	}

	@Test
	public void checklistFacetsAndStarredUnionCompose()
	{
		JournalSnapshot.QuestListItem membersQuest = quest(
			"DESERT_TREASURE",
			"Desert Treasure I",
			JournalSnapshot.QuestType.QUEST,
			JournalSnapshot.QuestState.NOT_STARTED,
			JournalSnapshot.QuestDifficulty.MASTER,
			true,
			Collections.emptyMap());
		JournalSnapshot.QuestListItem freeMiniquest = quest(
			"KNIGHT_WAVES",
			"Knight Waves Training Grounds",
			JournalSnapshot.QuestType.MINIQUEST,
			JournalSnapshot.QuestState.NOT_STARTED,
			JournalSnapshot.QuestDifficulty.SPECIAL,
			false,
			Collections.emptyMap());
		Set<String> starredIds = new LinkedHashSet<>(Collections.singleton(freeMiniquest.getId()));
		JournalSnapshot.QuestFilter starredUnion = JournalSnapshot.QuestFilter.all()
			.withStateSelections(Collections.singleton(JournalSnapshot.QuestState.NOT_STARTED))
			.withTypeSelections(Collections.singleton(JournalSnapshot.QuestType.QUEST))
			.withStarredQuestIds(starredIds);
		JournalSnapshot.QuestFilter filter = starredUnion
			.withDifficultySelections(Collections.singleton(JournalSnapshot.QuestDifficulty.MASTER))
			.withMembershipSelections(Collections.singleton(JournalSnapshot.QuestMembership.MEMBERS));

		starredIds.clear();
		assertTrue(starredUnion.matches(freeMiniquest));
		assertTrue(filter.matches(membersQuest));
		assertFalse(starredUnion.withStarredSelected(false).matches(freeMiniquest));
		assertFalse(filter.withStateSelections(Collections.emptySet()).matches(membersQuest));
		assertFalse(filter.withDifficultySelections(Collections.emptySet()).matches(membersQuest));
		assertUnmodifiable(filter.getStates(), JournalSnapshot.QuestState.COMPLETE);
		assertUnmodifiable(filter.getTypes(), JournalSnapshot.QuestType.SKILL);
	}

	@Test
	public void diaryTiersAndQuestHelperOrderingRemainIndependent()
	{
		JournalSnapshot.QuestListItem easyDiary = quest(
			"ARDOUGNE_EASY",
			"Ardougne Easy Diary",
			JournalSnapshot.QuestType.ACHIEVEMENT_DIARY,
			JournalSnapshot.QuestState.NOT_STARTED,
			JournalSnapshot.QuestDifficulty.EASY,
			true,
			Collections.emptyMap());
		JournalSnapshot.QuestListItem eliteDiary = quest(
			"ARDOUGNE_ELITE",
			"Ardougne Elite Diary",
			JournalSnapshot.QuestType.ACHIEVEMENT_DIARY,
			JournalSnapshot.QuestState.NOT_STARTED,
			JournalSnapshot.QuestDifficulty.ELITE,
			true,
			Collections.emptyMap());
		JournalSnapshot.QuestListItem unclassifiedHelper = quest(
			"HERB_RUN",
			"Herb Run",
			JournalSnapshot.QuestType.GENERIC,
			JournalSnapshot.QuestState.NOT_STARTED,
			JournalSnapshot.QuestDifficulty.SPECIAL,
			true,
			Collections.emptyMap());

		JournalSnapshot.QuestFilter easyOnly = JournalSnapshot.QuestFilter.all()
			.withDifficultySelections(Collections.singleton(JournalSnapshot.QuestDifficulty.EASY));
		assertTrue(easyOnly.matches(easyDiary));
		assertFalse(easyOnly.matches(eliteDiary));
		assertFalse(easyOnly.matches(unclassifiedHelper));
		assertTrue(JournalSnapshot.QuestFilter.all().matches(easyDiary));
		assertTrue(JournalSnapshot.QuestFilter.all().matches(unclassifiedHelper));

		EnumMap<JournalSnapshot.QuestOrder, Integer> firstRanks =
			new EnumMap<>(JournalSnapshot.QuestOrder.class);
		firstRanks.put(JournalSnapshot.QuestOrder.OPTIMAL_IRONMAN, 1);
		EnumMap<JournalSnapshot.QuestOrder, Integer> secondRanks =
			new EnumMap<>(JournalSnapshot.QuestOrder.class);
		secondRanks.put(JournalSnapshot.QuestOrder.OPTIMAL_IRONMAN, 0);
		JournalSnapshot.QuestFilter ordered = JournalSnapshot.QuestFilter.all()
			.withOrder(JournalSnapshot.QuestOrder.OPTIMAL_IRONMAN);
		assertTrue(ordered.comparator().compare(
			quest("SECOND", "Second", JournalSnapshot.QuestType.QUEST,
				JournalSnapshot.QuestState.NOT_STARTED, JournalSnapshot.QuestDifficulty.NOVICE,
				true, secondRanks),
			quest("FIRST", "First", JournalSnapshot.QuestType.QUEST,
				JournalSnapshot.QuestState.NOT_STARTED, JournalSnapshot.QuestDifficulty.MASTER,
				true, firstRanks)) < 0);
	}

	@Test
	public void listOptionsExposeFixedFacetsAndAccountDefaults()
	{
		JournalSnapshot.QuestListOptions defaults = JournalSnapshot.QuestListOptions.defaults();
		JournalSnapshot.QuestListOptions free =
			new JournalSnapshot.QuestListOptions(
				JournalSnapshot.QuestMembership.FREE_TO_PLAY,
				JournalSnapshot.QuestOrder.OPTIMAL_IRONMAN);

		assertEquals(java.util.Arrays.asList(JournalSnapshot.QuestType.values()), defaults.getTypes());
		assertEquals(
			new LinkedHashSet<>(java.util.Arrays.asList(JournalSnapshot.QuestType.values())),
			defaults.getConfiguredTypes());
		assertEquals(
			Collections.singleton(JournalSnapshot.QuestMembership.FREE_TO_PLAY),
			free.getConfiguredMemberships());
		assertEquals(JournalSnapshot.QuestOrder.OPTIMAL_IRONMAN, free.getConfiguredOrder());
		assertUnmodifiable(defaults.getConfiguredTypes(), JournalSnapshot.QuestType.SKILL);
	}

	private static JournalSnapshot.QuestOverview overview(String id, String title)
	{
		return new JournalSnapshot.QuestOverview(
			id,
			title,
			JournalSnapshot.QuestType.QUEST,
			JournalSnapshot.QuestState.NOT_STARTED,
			JournalSnapshot.QuestDifficulty.NOVICE,
			false);
	}

	private static JournalSnapshot.Requirement requirementWithWikiUrl(String wikiUrl)
	{
		return new JournalSnapshot.Requirement(
			"Bronze axe",
			JournalSnapshot.RequirementState.UNMET,
			null,
			Collections.emptyList(),
			"",
			"",
			JournalSnapshot.IconIdentity.item(1351, 1),
			wikiUrl,
			"");
	}

	private static JournalSnapshot.Requirement requirement(
		String text,
		JournalSnapshot.RequirementState state)
	{
		return new JournalSnapshot.Requirement(
			text,
			state,
			null,
			Collections.emptyList(),
			"",
			"",
			JournalSnapshot.IconIdentity.none(),
			"",
			"");
	}

	private static JournalSnapshot.QuestListItem listItem(JournalSnapshot.QuestOverview overview)
	{
		return new JournalSnapshot.QuestListItem(
			overview.getId(),
			overview.getTitle(),
			overview.getType(),
			overview.getState(),
			overview.getDifficulty(),
			overview.isMembers(),
			Collections.emptyMap());
	}

	private static JournalSnapshot.Objective objective(
		String text,
		JournalSnapshot.ObjectiveState state,
		boolean current)
	{
		return new JournalSnapshot.Objective(
			"", "", text, state, current, Collections.emptyList());
	}

	private static JournalSnapshot.QuestListItem quest(
		String id,
		String title,
		JournalSnapshot.QuestType type,
		JournalSnapshot.QuestState state,
		JournalSnapshot.QuestDifficulty difficulty,
		boolean members)
	{
		return quest(id, title, type, state, difficulty, members, Collections.emptyMap());
	}

	private static JournalSnapshot.QuestListItem quest(
		String id,
		String title,
		JournalSnapshot.QuestType type,
		JournalSnapshot.QuestState state,
		JournalSnapshot.QuestDifficulty difficulty,
		boolean members,
		Map<JournalSnapshot.QuestOrder, Integer> orderRanks)
	{
		return new JournalSnapshot.QuestListItem(
			id, title, type, state, difficulty, members, orderRanks);
	}

	private static <T> void assertUnmodifiable(List<T> values, T newValue)
	{
		assertThrows(UnsupportedOperationException.class, () -> values.add(newValue));
	}

	private static <T> void assertUnmodifiable(Set<T> values, T newValue)
	{
		assertThrows(UnsupportedOperationException.class, () -> values.add(newValue));
	}
}
