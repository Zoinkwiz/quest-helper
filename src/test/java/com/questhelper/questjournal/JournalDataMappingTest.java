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
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.questjournal;

import com.questhelper.QuestHelperConfig;
import com.questhelper.QuestHelperConfig.QuestOrdering;
import com.questhelper.managers.QuestManager;
import com.questhelper.managers.QuestMenuHandler;
import com.questhelper.panel.PanelDetails;
import com.questhelper.panel.TopLevelPanelDetails;
import com.questhelper.questhelpers.QuestDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.TrackedContainers;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import net.runelite.api.Client;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.WorldType;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JournalDataMappingTest
{
	private Client client;
	private QuestManager questManager;
	private QuestMenuHandler questMenuHandler;
	private QuestHelperConfig config;
	private CatalogMapper catalogMapper;
	private RequirementMapper requirementMapper;
	private QuestViewMapper questViewMapper;
	private JournalDataSource dataSource;

	@BeforeEach
	public void setUp()
	{
		client = mock(Client.class);
		questManager = mock(QuestManager.class);
		questMenuHandler = mock(QuestMenuHandler.class);
		config = mock(QuestHelperConfig.class);
		when(config.showFullRequirements()).thenReturn(false);
		when(config.hideQuestRewards()).thenReturn(false);
		catalogMapper = new CatalogMapper(client);
		requirementMapper = new RequirementMapper(client, config);
		questViewMapper = new QuestViewMapper(
			client,
			config,
			requirementMapper);
		dataSource = new JournalDataSource(
			questManager,
			questMenuHandler,
			catalogMapper,
			questViewMapper);
	}

	@Test
	public void panelRequirementsNeverPopulateDetailsRegardlessOfPathVisibility()
	{
		Requirement shared = mock(Requirement.class);
		when(shared.getDisplayText()).thenReturn("Bring a shared item");
		when(shared.shouldDisplayText(client)).thenReturn(true);

		Requirement hiddenPath = mock(Requirement.class);
		when(hiddenPath.check(client)).thenReturn(true);
		PanelDetails hidden = new PanelDetails(
			"Hidden path",
			Collections.emptyList(),
			Collections.singletonList(shared)).withHideCondition(hiddenPath);
		PanelDetails visible = new PanelDetails(
			"Visible path",
			Collections.emptyList(),
			Collections.singletonList(shared));
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		when(helper.getPanels()).thenReturn(Arrays.asList(hidden, visible));
		when(helper.getState(client)).thenReturn(QuestState.NOT_STARTED);

		JournalSnapshot.SelectedQuest evaluation = evaluate(
			questViewMapper.buildDefinition(helper));

		assertTrue(evaluation.getRequirements().isEmpty());
		verify(shared, never()).shouldDisplayText(client);
		verify(hiddenPath, never()).check(client);
	}

	@Test
	public void detailsMirrorNativeOverviewWhileSectionChecklistRemainsIndependent()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		Requirement general = displayRequirement("Start the prerequisite quest");
		ItemRequirement startItem = stepItem("2 x Start item", 100);
		Requirement generalRecommended = displayRequirement("Recommended skill level");
		ItemRequirement itemRecommended = stepItem("Recommended teleport", 101);
		ItemRequirement unspecifiedPanelCoins = stepItem("Coins", 995);
		when(unspecifiedPanelCoins.getQuantity()).thenReturn(-1);
		ItemRequirement acquiredDuringQuest = stepItem("Acquired during quest", 102);
		Requirement panelRecommended = displayRequirement("Section-only recommendation");
		ItemRequirement checklistItem = stepItem("Section checklist item", 103);
		DetailedQuestStep objective = objectiveStep("Complete the section");
		when(objective.getRequirements()).thenReturn(Collections.singletonList(checklistItem));
		PanelDetails section = new PanelDetails(
			"Later section",
			Collections.singletonList(objective),
			Arrays.<Requirement>asList(unspecifiedPanelCoins, acquiredDuringQuest),
			Collections.singletonList(panelRecommended));

		when(helper.getGeneralRequirements()).thenReturn(Collections.singletonList(general));
		when(helper.getItemRequirements()).thenReturn(Collections.singletonList(startItem));
		when(helper.getGeneralRecommended()).thenReturn(
			Collections.singletonList(generalRecommended));
		when(helper.getItemRecommended()).thenReturn(Collections.singletonList(itemRecommended));
		when(helper.getPanels()).thenReturn(Collections.singletonList(section));
		when(helper.getState(client)).thenReturn(QuestState.NOT_STARTED);

		JournalSnapshot.SelectedQuest selected = evaluate(
			questViewMapper.buildDefinition(helper));

		assertEquals(
			Arrays.asList("Start the prerequisite quest", "2 x Start item"),
			selected.getRequirements().stream()
				.map(JournalSnapshot.Requirement::getText)
				.collect(java.util.stream.Collectors.toList()));
		assertEquals(
			Arrays.asList("Recommended skill level", "Recommended teleport"),
			selected.getRecommendations().stream()
				.map(JournalSnapshot.Requirement::getText)
				.collect(java.util.stream.Collectors.toList()));
		assertEquals(
			Arrays.asList("Coins", "Acquired during quest", "Section checklist item"),
			selected.getObjectives().get(0).getSectionRequirements().stream()
				.map(JournalSnapshot.Requirement::getText)
				.collect(java.util.stream.Collectors.toList()));
		verify(unspecifiedPanelCoins).shouldDisplayText(client);
		verify(acquiredDuringQuest).shouldDisplayText(client);
		verify(panelRecommended, never()).shouldDisplayText(client);
	}

	@Test
	public void everyQuestHelperTypeMapsToAnIndependentJournalFacet()
	{
		Map<QuestDetails.Type, JournalSnapshot.QuestType> expected =
			new EnumMap<>(QuestDetails.Type.class);
		expected.put(QuestDetails.Type.F2P, JournalSnapshot.QuestType.QUEST);
		expected.put(QuestDetails.Type.P2P, JournalSnapshot.QuestType.QUEST);
		expected.put(QuestDetails.Type.MINIQUEST, JournalSnapshot.QuestType.MINIQUEST);
		expected.put(
			QuestDetails.Type.ACHIEVEMENT_DIARY,
			JournalSnapshot.QuestType.ACHIEVEMENT_DIARY);
		expected.put(QuestDetails.Type.GENERIC, JournalSnapshot.QuestType.GENERIC);
		expected.put(QuestDetails.Type.SKILL, JournalSnapshot.QuestType.SKILL);
		expected.put(QuestDetails.Type.SKILL_F2P, JournalSnapshot.QuestType.SKILL);
		expected.put(QuestDetails.Type.SKILL_P2P, JournalSnapshot.QuestType.SKILL);
		expected.put(QuestDetails.Type.PLAYER_QUEST, JournalSnapshot.QuestType.PLAYER_QUEST);

		assertEquals(EnumSet.allOf(QuestDetails.Type.class), expected.keySet());
		for (QuestDetails.Type type : QuestDetails.Type.values())
		{
			assertEquals(expected.get(type), CatalogMapper.journalType(type), type.name());
		}

		assertFalse(CatalogMapper.isMembers(QuestDetails.Type.F2P));
		assertFalse(CatalogMapper.isMembers(QuestDetails.Type.SKILL_F2P));
		assertTrue(CatalogMapper.isMembers(QuestDetails.Type.P2P));
		assertTrue(CatalogMapper.isMembers(QuestDetails.Type.MINIQUEST));
		assertTrue(CatalogMapper.isMembers(QuestDetails.Type.ACHIEVEMENT_DIARY));
		assertTrue(CatalogMapper.isMembers(QuestDetails.Type.SKILL_P2P));
	}

	@Test
	public void everyQuestAndDiaryPublishesItsCanonicalDifficulty()
	{
		Set<JournalSnapshot.QuestDifficulty> questDifficulties = EnumSet.noneOf(
			JournalSnapshot.QuestDifficulty.class);
		Set<JournalSnapshot.QuestDifficulty> diaryTiers = EnumSet.noneOf(
			JournalSnapshot.QuestDifficulty.class);
		Set<JournalSnapshot.QuestDifficulty> canonicalQuests = EnumSet.of(
			JournalSnapshot.QuestDifficulty.NOVICE,
			JournalSnapshot.QuestDifficulty.INTERMEDIATE,
			JournalSnapshot.QuestDifficulty.EXPERIENCED,
			JournalSnapshot.QuestDifficulty.MASTER,
			JournalSnapshot.QuestDifficulty.GRANDMASTER);
		Set<JournalSnapshot.QuestDifficulty> canonicalDiaries = EnumSet.of(
			JournalSnapshot.QuestDifficulty.EASY,
			JournalSnapshot.QuestDifficulty.MEDIUM,
			JournalSnapshot.QuestDifficulty.HARD,
			JournalSnapshot.QuestDifficulty.ELITE);

		for (QuestHelperQuest quest : QuestHelperQuest.values())
		{
			JournalSnapshot.QuestType type = CatalogMapper.journalType(
				quest.getQuestType());
			JournalSnapshot.QuestDifficulty actual =
				CatalogMapper.journalDifficulty(quest);
			if (type == JournalSnapshot.QuestType.QUEST)
			{
				JournalSnapshot.QuestDifficulty expected = JournalSnapshot.QuestDifficulty.valueOf(
					quest.getDifficulty().name());
				assertTrue(canonicalQuests.contains(expected), quest.name());
				assertEquals(expected, actual, quest.name());
				questDifficulties.add(actual);
			}
			else if (type == JournalSnapshot.QuestType.ACHIEVEMENT_DIARY)
			{
				JournalSnapshot.QuestDifficulty tier =
					CatalogMapper.achievementDiaryTier(quest);
				assertNotNull(tier, quest.name());
				assertTrue(canonicalDiaries.contains(tier), quest.name());
				assertEquals(tier, actual, quest.name());
				diaryTiers.add(tier);
			}
			else
			{
				assertEquals(JournalSnapshot.QuestDifficulty.SPECIAL, actual, quest.name());
			}
		}

		assertEquals(canonicalQuests, questDifficulties);
		assertEquals(canonicalDiaries, diaryTiers);
	}

	@Test
	public void listOptionsDefaultToCurrentMembershipAndIronmanOrdering()
	{
		when(client.getWorldType()).thenReturn(EnumSet.of(WorldType.MEMBERS));
		for (int accountType = 1; accountType <= 6; accountType++)
		{
			when(client.getVarbitValue(VarbitID.IRONMAN)).thenReturn(accountType);
			JournalSnapshot.QuestListOptions options = catalogMapper.buildListOptions(
				catalogMapper.currentMembership(),
				catalogMapper.currentOrder());
			assertEquals(
				Arrays.asList(JournalSnapshot.QuestMembership.values()),
				options.getMemberships());
			assertEquals(
				new LinkedHashSet<>(Arrays.asList(JournalSnapshot.QuestMembership.values())),
				options.getConfiguredMemberships());
			assertEquals(
				JournalSnapshot.QuestOrder.OPTIMAL_IRONMAN,
				options.getConfiguredOrder());
			assertEquals(
				Arrays.asList(JournalSnapshot.QuestOrder.values()),
				options.getOrders());
			assertEquals(
				Arrays.asList(
					JournalSnapshot.QuestDifficulty.NOVICE,
					JournalSnapshot.QuestDifficulty.EASY,
					JournalSnapshot.QuestDifficulty.INTERMEDIATE,
					JournalSnapshot.QuestDifficulty.MEDIUM,
					JournalSnapshot.QuestDifficulty.EXPERIENCED,
					JournalSnapshot.QuestDifficulty.HARD,
					JournalSnapshot.QuestDifficulty.MASTER,
					JournalSnapshot.QuestDifficulty.ELITE,
					JournalSnapshot.QuestDifficulty.GRANDMASTER,
					JournalSnapshot.QuestDifficulty.SPECIAL),
				options.getDifficulties());
		}

		when(client.getWorldType()).thenReturn(EnumSet.noneOf(WorldType.class));
		when(client.getVarbitValue(VarbitID.IRONMAN)).thenReturn(0);
		JournalSnapshot.QuestListOptions normal = catalogMapper.buildListOptions(
			catalogMapper.currentMembership(),
			catalogMapper.currentOrder());
		assertEquals(
			Collections.singleton(JournalSnapshot.QuestMembership.FREE_TO_PLAY),
			normal.getConfiguredMemberships());
		assertEquals(JournalSnapshot.QuestOrder.OPTIMAL, normal.getConfiguredOrder());
	}

	@Test
	public void catalogPublishesStableRanksForEverySupportedOrder()
	{
		QuestHelper dragonSlayer = helper(QuestHelperQuest.DRAGON_SLAYER_I);
		QuestHelper cooksAssistant = helper(QuestHelperQuest.COOKS_ASSISTANT);
		QuestHelper miniquest = helper(QuestHelperQuest.ENTER_THE_ABYSS);
		QuestHelper diary = helper(QuestHelperQuest.ARDOUGNE_EASY);
		QuestHelper skill = helper(QuestHelperQuest.AGILITY);
		QuestHelper generic = helper(QuestHelperQuest.HERB_RUN);
		QuestHelper playerQuest = helper(QuestHelperQuest.BIKE_SHEDDER);

		Map<QuestHelperQuest, Map<JournalSnapshot.QuestOrder, Integer>> ranks =
			catalogMapper.buildOrderRanks(Arrays.asList(
				dragonSlayer,
				cooksAssistant,
				miniquest,
				diary,
				skill,
				generic,
				playerQuest));

		assertEquals(
			QuestOrdering.values().length,
			ranks.get(QuestHelperQuest.COOKS_ASSISTANT).size());
		assertEquals(0, ranks.get(QuestHelperQuest.COOKS_ASSISTANT)
			.get(JournalSnapshot.QuestOrder.A_TO_Z));
		assertEquals(2, ranks.get(QuestHelperQuest.ENTER_THE_ABYSS)
			.get(JournalSnapshot.QuestOrder.A_TO_Z));
		assertEquals(3, ranks.get(QuestHelperQuest.ARDOUGNE_EASY)
			.get(JournalSnapshot.QuestOrder.A_TO_Z));
		assertEquals(4, ranks.get(QuestHelperQuest.AGILITY)
			.get(JournalSnapshot.QuestOrder.A_TO_Z));
		assertEquals(5, ranks.get(QuestHelperQuest.HERB_RUN)
			.get(JournalSnapshot.QuestOrder.A_TO_Z));
		assertEquals(6, ranks.get(QuestHelperQuest.BIKE_SHEDDER)
			.get(JournalSnapshot.QuestOrder.A_TO_Z));
		assertTrue(ranks.get(QuestHelperQuest.HERB_RUN)
			.containsKey(JournalSnapshot.QuestOrder.OPTIMAL_IRONMAN));
		assertFalse(ranks.get(QuestHelperQuest.AGILITY)
			.containsKey(JournalSnapshot.QuestOrder.OPTIMAL_IRONMAN));
		assertFalse(ranks.get(QuestHelperQuest.BIKE_SHEDDER)
			.containsKey(JournalSnapshot.QuestOrder.OPTIMAL_IRONMAN));
	}

	@Test
	public void nestedObjectivesTrackCurrentPriorAndCompletedStates()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		DetailedQuestStep first = objectiveStep("Gather the ingredients");
		DetailedQuestStep second = objectiveStep("Bake the cake");
		when(helper.getCurrentStep()).thenReturn(second);
		when(second.getActiveStep()).thenReturn(second);
		when(first.containsSteps(eq(second), anySet())).thenReturn(false);
		when(second.containsSteps(eq(second), anySet())).thenReturn(true);
		PanelDetails phase = new PanelDetails(
			"Kitchen",
			Arrays.asList(first, second),
			Collections.emptyList());
		when(helper.getPanels()).thenReturn(Collections.singletonList(
			new TopLevelPanelDetails("Quest", phase)));
		AtomicReference<QuestState> state = new AtomicReference<>(QuestState.NOT_STARTED);
		when(helper.getState(client)).thenAnswer(invocation -> state.get());

		QuestViewMapper.Definition definition =
			questViewMapper.buildDefinition(helper);
		JournalSnapshot.SelectedQuest notStarted = evaluate(definition);
		for (JournalSnapshot.Objective objective : notStarted.getObjectives())
		{
			assertEquals(JournalSnapshot.ObjectiveState.AVAILABLE, objective.getState());
		}
		assertTrue(notStarted.getObjectives().get(0).isCurrent());
		assertFalse(notStarted.getObjectives().get(1).isCurrent());

		state.set(QuestState.IN_PROGRESS);
		JournalSnapshot.SelectedQuest active = evaluate(definition);

		assertEquals(2, active.getObjectives().size());
		assertEquals("Kitchen", active.getObjectives().get(0).getSection());
		assertEquals(
			JournalSnapshot.ObjectiveState.COMPLETE,
			active.getObjectives().get(0).getState());
		assertFalse(active.getObjectives().get(0).isCurrent());
		assertEquals(
			JournalSnapshot.ObjectiveState.AVAILABLE,
			active.getObjectives().get(1).getState());
		assertTrue(active.getObjectives().get(1).isCurrent());

		state.set(QuestState.FINISHED);
		JournalSnapshot.SelectedQuest completed = evaluate(definition);
		for (JournalSnapshot.Objective objective : completed.getObjectives())
		{
			assertEquals(JournalSnapshot.ObjectiveState.COMPLETE, objective.getState());
			assertFalse(objective.isCurrent());
		}
	}

	@Test
	public void inactiveNotStartedQuestPreviewDoesNotClaimACurrentStep()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		DetailedQuestStep first = objectiveStep("Gather the ingredients");
		when(helper.getPanels()).thenReturn(Collections.singletonList(
			new PanelDetails(
				"Kitchen",
				Collections.singletonList(first),
				Collections.emptyList())));
		when(helper.getState(client)).thenReturn(QuestState.NOT_STARTED);

		QuestViewMapper.Definition definition =
			questViewMapper.buildDefinition(helper);
		JournalSnapshot.Objective preview = questViewMapper.evaluate(
			definition,
			first,
			first,
			false).getObjectives().get(0);

		assertFalse(preview.isCurrent());
	}

	@Test
	public void lockedSectionsDoNotCompleteBeforeTheQuestStarts()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		DetailedQuestStep objective = objectiveStep("Gather the ingredients");
		QuestStep lockingStep = mock(QuestStep.class);
		when(lockingStep.isLockedWithoutUpdate(client)).thenReturn(true);
		PanelDetails panel = new PanelDetails(
			"Kitchen",
			Collections.singletonList(objective),
			Collections.emptyList());
		panel.setLockingStep(lockingStep);
		when(helper.getPanels()).thenReturn(Collections.singletonList(panel));
		when(helper.getState(client)).thenReturn(QuestState.NOT_STARTED);

		JournalSnapshot.Objective mapped = evaluate(
			questViewMapper.buildDefinition(helper)).getObjectives().get(0);

		assertEquals(JournalSnapshot.ObjectiveState.AVAILABLE, mapped.getState());
		assertTrue(mapped.isCurrent());
	}

	@Test
	public void inactiveLockChecksUseTheMapperClientWithoutInjectingThePreviewStep()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		DetailedQuestStep objective = objectiveStep("Gather the ingredients");
		Requirement lockingCondition = mock(Requirement.class);
		when(lockingCondition.check(client)).thenReturn(true);
		QuestStep lockingStep = new QuestStep(helper)
		{
		};
		lockingStep.setLockingCondition(lockingCondition);
		PanelDetails panel = new PanelDetails(
			"Kitchen",
			Collections.singletonList(objective),
			Collections.emptyList());
		panel.setLockingStep(lockingStep);
		when(helper.getPanels()).thenReturn(Collections.singletonList(panel));
		when(helper.getState(client)).thenReturn(QuestState.IN_PROGRESS);

		JournalSnapshot.Objective mapped = evaluate(
			questViewMapper.buildDefinition(helper)).getObjectives().get(0);

		assertEquals(JournalSnapshot.ObjectiveState.COMPLETE, mapped.getState());
		verify(lockingCondition).check(client);
	}

	@Test
	public void objectiveStateKeepsCompletedAndFadedStepsDistinct()
	{
		assertEquals(
			JournalSnapshot.ObjectiveState.COMPLETE,
			QuestViewMapper.objectiveState(false, true, false));
		assertEquals(
			JournalSnapshot.ObjectiveState.FADED,
			QuestViewMapper.objectiveState(false, false, true));
		assertEquals(
			JournalSnapshot.ObjectiveState.AVAILABLE,
			QuestViewMapper.objectiveState(true, true, true));
	}

	@Test
	public void objectivesPublishOnlyTheirVisibleItemRequirements()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		ItemRequirement item = mock(ItemRequirement.class);
		when(item.getDisplayText()).thenReturn("Bronze axe");
		when(item.shouldDisplayText(client)).thenReturn(true);
		when(item.getDisplayItemIds()).thenReturn(Collections.singletonList(1351));
		when(item.getAllIds()).thenReturn(Collections.singletonList(1351));
		when(item.getQuantity()).thenReturn(1);

		Requirement nonItem = mock(Requirement.class);
		when(nonItem.getDisplayText()).thenReturn("Talk to the cook");
		when(nonItem.shouldDisplayText(client)).thenReturn(true);
		DetailedQuestStep visible = objectiveStep("Gather the ingredients");
		when(visible.getRequirements()).thenReturn(Arrays.asList(nonItem, item));

		ItemRequirement hiddenItem = mock(ItemRequirement.class);
		DetailedQuestStep hidden = objectiveStep("Prepare the cake");
		when(hidden.getRequirements()).thenReturn(Collections.singletonList(hiddenItem));
		hidden.hideRequirements = true;

		PanelDetails phase = new PanelDetails(
			"Kitchen",
			Arrays.asList(visible, hidden),
			Collections.emptyList());
		when(helper.getPanels()).thenReturn(Collections.singletonList(phase));
		when(helper.getState(client)).thenReturn(QuestState.NOT_STARTED);

		JournalSnapshot.SelectedQuest selected = evaluate(
			questViewMapper.buildDefinition(helper));

		assertEquals(2, selected.getObjectives().size());
		JournalSnapshot.Objective first = selected.getObjectives().get(0);
		assertEquals(1, first.getSectionRequirements().size());
		assertEquals("Bronze axe", first.getSectionRequirements().get(0).getText());
		assertEquals(
			first.getSectionRequirements(),
			selected.getObjectives().get(1).getSectionRequirements());
		verify(hiddenItem, never()).shouldDisplayText(client);
	}

	@Test
	public void objectiveRequirementsIncludeEveryWrapperOnTheActivePath()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.IN_AID_OF_THE_MYREQUE);
		ConditionalStep outer = mock(ConditionalStep.class);
		ConditionalStep intermediate = mock(ConditionalStep.class);
		QuestStep leaf = mock(QuestStep.class);
		when(outer.isShowInSidebar()).thenReturn(true);
		when(outer.getText()).thenReturn(Collections.singletonList("Bless the rod"));

		ItemRequirement rope = stepItem("Rope", 954);
		ItemRequirement enchantedRod = stepItem("Silver sickle (b)", 2963);
		when(intermediate.getRequirements()).thenReturn(Arrays.asList(rope, enchantedRod));

		java.util.HashMap<Requirement, QuestStep> outerBranches = new java.util.HashMap<>();
		outerBranches.put(null, intermediate);
		when(outer.getStepsMap()).thenReturn(outerBranches);
		when(outer.getSteps()).thenReturn(Collections.singletonList(intermediate));
		java.util.HashMap<Requirement, QuestStep> intermediateBranches = new java.util.HashMap<>();
		intermediateBranches.put(null, leaf);
		when(intermediate.getStepsMap()).thenReturn(intermediateBranches);
		when(intermediate.getSteps()).thenReturn(Collections.singletonList(leaf));

		PanelDetails phase = new PanelDetails(
			"Myreque",
			Collections.singletonList(outer),
			Collections.emptyList());
		when(helper.getPanels()).thenReturn(Collections.singletonList(phase));
		when(helper.getState(client)).thenReturn(QuestState.IN_PROGRESS);

		JournalSnapshot.Objective objective = questViewMapper.evaluate(
			questViewMapper.buildDefinition(helper),
			leaf,
			leaf,
			true).getObjectives().get(0);

		assertEquals(
			Arrays.asList("Rope", "Silver sickle (b)"),
			objective.getSectionRequirements().stream()
				.map(JournalSnapshot.Requirement::getText)
				.collect(java.util.stream.Collectors.toList()));
	}

	@Test
	public void currentObjectiveInheritsRequirementsFromAnUnlistedLiveWrapper()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.WANTED);
		ConditionalStep liveRoot = mock(ConditionalStep.class);
		DetailedQuestStep listedLeaf = objectiveStep("Get a commorb from Sir Tiffy");
		ItemRequirement components = stepItem(
			"Commorb components or 10,000 coins",
			995);
		when(liveRoot.getRequirements()).thenReturn(Collections.singletonList(components));
		when(liveRoot.getActiveStep()).thenReturn(listedLeaf);
		when(liveRoot.getSteps()).thenReturn(Collections.singletonList(listedLeaf));
		java.util.HashMap<Requirement, QuestStep> branches = new java.util.HashMap<>();
		branches.put(null, listedLeaf);
		when(liveRoot.getStepsMap()).thenReturn(branches);
		when(helper.getCurrentStep()).thenReturn(liveRoot);
		when(helper.getPanels()).thenReturn(Collections.singletonList(new PanelDetails(
			"Investigation",
			Collections.singletonList(listedLeaf),
			Collections.emptyList())));
		when(helper.getState(client)).thenReturn(QuestState.IN_PROGRESS);

		JournalSnapshot.Objective objective = evaluate(
			questViewMapper.buildDefinition(helper)).getObjectives().get(0);

		assertTrue(objective.isCurrent());
		assertEquals(1, objective.getSectionRequirements().size());
		assertEquals(
			"Commorb components or 10,000 coins",
			objective.getSectionRequirements().get(0).getText());
	}

	@Test
	public void nonActivePreviewDoesNotTreatAnInferredWrapperAsLiveItemData()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.WANTED);
		ConditionalStep inferredRoot = mock(ConditionalStep.class);
		DetailedQuestStep listedLeaf = objectiveStep("Get a commorb from Sir Tiffy");
		ItemRequirement components = stepItem(
			"Commorb components or 10,000 coins",
			995);
		when(inferredRoot.getRequirements()).thenReturn(Collections.singletonList(components));
		when(inferredRoot.getSteps()).thenReturn(Collections.singletonList(listedLeaf));
		java.util.HashMap<Requirement, QuestStep> branches = new java.util.HashMap<>();
		branches.put(null, listedLeaf);
		when(inferredRoot.getStepsMap()).thenReturn(branches);
		when(helper.getPanels()).thenReturn(Collections.singletonList(new PanelDetails(
			"Investigation",
			Collections.singletonList(listedLeaf),
			Collections.emptyList())));
		when(helper.getState(client)).thenReturn(QuestState.IN_PROGRESS);

		JournalSnapshot.Objective objective = questViewMapper.evaluate(
			questViewMapper.buildDefinition(helper),
			inferredRoot,
			listedLeaf,
			false).getObjectives().get(0);

		assertFalse(objective.isCurrent());
		assertTrue(objective.getSectionRequirements().isEmpty());
	}

	@Test
	public void hiddenLiveLeafSuppressesInheritedItemsLikeQuestHelper()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.WANTED);
		ConditionalStep liveRoot = mock(ConditionalStep.class);
		DetailedQuestStep hiddenLeaf = objectiveStep("Continue the investigation");
		hiddenLeaf.hideRequirements = true;
		ItemRequirement components = stepItem(
			"Commorb components or 10,000 coins",
			995);
		when(liveRoot.getRequirements()).thenReturn(Collections.singletonList(components));
		when(liveRoot.getSteps()).thenReturn(Collections.singletonList(hiddenLeaf));
		java.util.HashMap<Requirement, QuestStep> branches = new java.util.HashMap<>();
		branches.put(null, hiddenLeaf);
		when(liveRoot.getStepsMap()).thenReturn(branches);
		when(helper.getPanels()).thenReturn(Collections.singletonList(new PanelDetails(
			"Investigation",
			Collections.singletonList(hiddenLeaf),
			Collections.emptyList())));
		when(helper.getState(client)).thenReturn(QuestState.IN_PROGRESS);

		JournalSnapshot.Objective objective = questViewMapper.evaluate(
			questViewMapper.buildDefinition(helper),
			liveRoot,
			hiddenLeaf,
			true).getObjectives().get(0);

		assertTrue(objective.isCurrent());
		assertTrue(objective.getSectionRequirements().isEmpty());
	}

	@Test
	public void summaryObjectiveCombinesItsItemsWithTheSeparateLiveWrapperPath()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.WANTED);
		ConditionalStep liveRoot = mock(ConditionalStep.class);
		ConditionalStep liveWrapper = mock(ConditionalStep.class);
		QuestStep activeLeaf = mock(QuestStep.class);
		DetailedQuestStep summary = objectiveStep("Hunt down Solus");
		ItemRequirement lightSource = stepItem("A light source", 4531);
		ItemRequirement summaryItem = stepItem("Combat equipment", 1277);
		when(liveWrapper.getRequirements()).thenReturn(Collections.singletonList(lightSource));
		when(summary.getRequirements()).thenReturn(Collections.singletonList(summaryItem));
		when(summary.getSubsteps()).thenReturn(Collections.singletonList(activeLeaf));

		when(liveRoot.getActiveStep()).thenReturn(activeLeaf);
		when(liveRoot.getSteps()).thenReturn(Collections.singletonList(liveWrapper));
		java.util.HashMap<Requirement, QuestStep> rootBranches = new java.util.HashMap<>();
		rootBranches.put(null, liveWrapper);
		when(liveRoot.getStepsMap()).thenReturn(rootBranches);
		when(liveWrapper.getSteps()).thenReturn(Collections.singletonList(activeLeaf));
		java.util.HashMap<Requirement, QuestStep> wrapperBranches = new java.util.HashMap<>();
		wrapperBranches.put(null, activeLeaf);
		when(liveWrapper.getStepsMap()).thenReturn(wrapperBranches);

		when(helper.getCurrentStep()).thenReturn(liveRoot);
		when(helper.getPanels()).thenReturn(Collections.singletonList(new PanelDetails(
			"Investigation",
			Collections.singletonList(summary),
			Collections.emptyList())));
		when(helper.getState(client)).thenReturn(QuestState.IN_PROGRESS);

		JournalSnapshot.Objective objective = evaluate(
			questViewMapper.buildDefinition(helper)).getObjectives().get(0);

		assertTrue(objective.isCurrent());
		assertEquals(
			Arrays.asList("A light source", "Combat equipment"),
			objective.getSectionRequirements().stream()
				.map(JournalSnapshot.Requirement::getText)
				.collect(java.util.stream.Collectors.toList()));
	}

	@Test
	public void sharedStepRequirementIsEvaluatedOncePerSelectedQuestRefresh()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		ItemRequirement shared = stepItem("Bucket of milk", 1927);
		DetailedQuestStep first = objectiveStep("Gather the ingredients");
		DetailedQuestStep second = objectiveStep("Bake the cake");
		when(first.getRequirements()).thenReturn(Collections.singletonList(shared));
		when(second.getRequirements()).thenReturn(Collections.singletonList(shared));
		when(helper.getPanels()).thenReturn(Collections.singletonList(new PanelDetails(
			"Kitchen",
			Arrays.asList(first, second),
			Collections.emptyList())));
		when(helper.getState(client)).thenReturn(QuestState.NOT_STARTED);

		JournalSnapshot.SelectedQuest selected = evaluate(
			questViewMapper.buildDefinition(helper));

		assertEquals(1, selected.getObjectives().get(0).getSectionRequirements().size());
		assertEquals(1, selected.getObjectives().get(1).getSectionRequirements().size());
		verify(shared).shouldDisplayText(client);
		verify(shared).getTooltip();
	}

	@Test
	public void sectionItemsCombineThePanelChecklistWithVisibleStepItems()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		ItemRequirement bucket = stepItem("Bucket of milk", 1927);
		ItemRequirement flour = stepItem("Pot of flour", 1933);
		ItemRequirement panelOnly = stepItem("Panel-only item", 995);
		DetailedQuestStep first = objectiveStep("Gather the ingredients");
		DetailedQuestStep second = objectiveStep("Bake the cake");
		when(first.getRequirements()).thenReturn(Collections.singletonList(bucket));
		when(second.getRequirements()).thenReturn(Arrays.asList(bucket, flour));
		PanelDetails panel = new PanelDetails(
			"Kitchen",
			Arrays.asList(first, second),
			Collections.singletonList(panelOnly));
		when(helper.getPanels()).thenReturn(Collections.singletonList(panel));
		when(helper.getState(client)).thenReturn(QuestState.NOT_STARTED);

		JournalSnapshot.SelectedQuest selected = evaluate(
			questViewMapper.buildDefinition(helper));

		assertEquals(2, selected.getObjectives().size());
		JournalSnapshot.Objective firstObjective = selected.getObjectives().get(0);
		JournalSnapshot.Objective secondObjective = selected.getObjectives().get(1);
		assertFalse(firstObjective.getSectionId().isEmpty());
		assertEquals(firstObjective.getSectionId(), secondObjective.getSectionId());
		assertEquals(
			Arrays.asList("Panel-only item", "Bucket of milk", "Pot of flour"),
			firstObjective.getSectionRequirements().stream()
				.map(JournalSnapshot.Requirement::getText)
				.collect(java.util.stream.Collectors.toList()));
		assertEquals(firstObjective.getSectionRequirements(), secondObjective.getSectionRequirements());
		assertTrue(selected.getRequirements().isEmpty());
		verify(panelOnly).shouldDisplayText(client);
		verify(bucket).shouldDisplayText(client);
		verify(bucket).getTooltip();
	}

	@Test
	public void separatePanelsWithTheSameHeadingKeepDistinctSectionIds()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		PanelDetails first = new PanelDetails(
			"Kitchen",
			Collections.singletonList(objectiveStep("Gather the ingredients")),
			Collections.singletonList(stepItem("Bucket of milk", 1927)));
		PanelDetails second = new PanelDetails(
			"Kitchen",
			Collections.singletonList(objectiveStep("Bake the cake")),
			Collections.singletonList(stepItem("Pot of flour", 1933)));
		when(helper.getPanels()).thenReturn(Arrays.asList(first, second));
		when(helper.getState(client)).thenReturn(QuestState.NOT_STARTED);

		List<JournalSnapshot.Objective> objectives = evaluate(
			questViewMapper.buildDefinition(helper)).getObjectives();

		assertEquals(2, objectives.size());
		assertNotEquals(objectives.get(0).getSectionId(), objectives.get(1).getSectionId());
	}

	@Test
	public void hiddenFirstStepDoesNotRenumberItsSectionsRequirements()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		DetailedQuestStep hidden = objectiveStep("Hidden preparation");
		DetailedQuestStep visible = objectiveStep("Bake the cake");
		ItemRequirement hiddenItem = stepItem("Hidden item", 995);
		ItemRequirement flour = stepItem("Pot of flour", 1933);
		when(hidden.isShowInSidebar()).thenReturn(false);
		when(hidden.getRequirements()).thenReturn(Collections.singletonList(hiddenItem));
		when(visible.getRequirements()).thenReturn(Collections.singletonList(flour));
		PanelDetails panel = new PanelDetails(
			"Kitchen",
			Arrays.asList(hidden, visible),
			Collections.emptyList());
		when(helper.getPanels()).thenReturn(Collections.singletonList(panel));
		when(helper.getState(client)).thenReturn(QuestState.NOT_STARTED);

		List<JournalSnapshot.Objective> objectives = evaluate(
			questViewMapper.buildDefinition(helper)).getObjectives();

		assertEquals(1, objectives.size());
		assertEquals("COOKS_ASSISTANT:section:0", objectives.get(0).getSectionId());
		assertEquals(1, objectives.get(0).getSectionRequirements().size());
		assertEquals("Pot of flour", objectives.get(0).getSectionRequirements().get(0).getText());
		verify(hiddenItem, never()).shouldDisplayText(client);
	}

	@Test
	public void itemContainerStatesDistinguishEveryOwnershipLocation()
	{
		assertEquals(
			JournalSnapshot.RequirementState.MET,
			RequirementMapper.itemContainerState(true, true, false, true));
		assertEquals(
			JournalSnapshot.RequirementState.BANKED,
			RequirementMapper.itemContainerState(false, true, false, true));
		assertEquals(
			JournalSnapshot.RequirementState.GROUP_BANKED,
			RequirementMapper.itemContainerState(false, true, true, true));
		assertEquals(
			JournalSnapshot.RequirementState.PARTIAL,
			RequirementMapper.itemContainerState(false, false, false, true));
		assertEquals(
			JournalSnapshot.RequirementState.UNMET,
			RequirementMapper.itemContainerState(false, false, false, false));
	}

	@Test
	public void configuredPartialColorDoesNotMasqueradeAsBankStorage()
	{
		Color partial = new Color(255, 160, 0);
		when(config.partialSuccessColour()).thenReturn(partial);

		assertEquals(
			JournalSnapshot.RequirementState.PARTIAL,
			requirementMapper.configuredColorState(partial));
		assertEquals(
			JournalSnapshot.RequirementState.BANKED,
			RequirementMapper.itemContainerState(false, true, false, true));
	}

	@Test
	public void requirementsRetainTypedLinksLocationsAndIcons()
	{
		RequirementMapper.Session session = requirementMapper.newSession();
		QuestRequirement questRequirement = new QuestRequirement(
			QuestHelperQuest.COOKS_ASSISTANT,
			QuestState.FINISHED);
		JournalSnapshot.Requirement quest = session.build(
			questRequirement,
			"Complete Cook's Assistant");
		assertEquals(QuestHelperQuest.COOKS_ASSISTANT.name(), quest.getLinkedQuestId());
		assertEquals("Cook's Assistant", quest.getLinkedQuestTitle());
		assertEquals(JournalSnapshot.IconType.QUEST, quest.getIcon().getType());

		ItemRequirement itemRequirement = mock(ItemRequirement.class);
		when(itemRequirement.isActualItem()).thenReturn(true);
		when(itemRequirement.getDisplayItemIds()).thenReturn(Collections.singletonList(1351));
		when(itemRequirement.getAllIds()).thenReturn(Collections.singletonList(1351));
		when(itemRequirement.getQuantity()).thenReturn(3);
		when(itemRequirement.getTooltip()).thenReturn(
			"Take one from the stump.\nIt is south of the house.");
		when(itemRequirement.getWikiUrl()).thenReturn(
			"https://oldschool.runescape.wiki/w/Special:Lookup?type=item&id=1351#Item_sources");
		when(itemRequirement.getContainersWithItem()).thenReturn(new LinkedHashSet<>(
			Arrays.asList(TrackedContainers.BANK, TrackedContainers.GROUP_STORAGE)));
		JournalSnapshot.Requirement item = session.build(
			itemRequirement,
			"Bronze axe");
		verify(itemRequirement).getContainersWithItem();

		assertEquals(
			Arrays.asList(
				JournalSnapshot.ItemLocation.BANK,
				JournalSnapshot.ItemLocation.GROUP_STORAGE),
			item.getLocations());
		assertEquals(JournalSnapshot.IconType.ITEM, item.getIcon().getType());
		assertEquals(Integer.valueOf(1351), item.getIcon().getItemId());
		assertEquals(3, item.getIcon().getQuantity());
		assertTrue(item.hasWikiUrl());
		assertEquals(
			"Take one from the stump.\nIt is south of the house.",
			item.getHelpText());

		JournalSnapshot.Requirement skill = session.build(
			new SkillRequirement(Skill.COOKING, 10),
			"10 Cooking");
		assertEquals(JournalSnapshot.IconType.SKILL, skill.getIcon().getType());
		assertEquals(Skill.COOKING.name(), skill.getIcon().getSkill());
	}

	@Test
	public void unavailableRequirementGuidanceDoesNotHideTheRequirement()
	{
		Requirement requirement = mock(Requirement.class);
		when(requirement.getTooltip()).thenThrow(new IllegalStateException("unavailable"));

		JournalSnapshot.Requirement view = requirementMapper.newSession().build(
			requirement,
			"Bring an item");

		assertEquals("Bring an item", view.getText());
		assertEquals("", view.getHelpText());
	}

	@Test
	public void itemIconSelectionPrefersOwnedDisplayThenStableFallback()
	{
		assertEquals(
			Integer.valueOf(1351),
			RequirementMapper.selectItemIconId(
				Arrays.asList(-1, 1349, 1351, 1351),
				Arrays.asList(1349, 1351, 1353),
				itemId -> itemId == 1351));
		assertEquals(
			Integer.valueOf(1349),
			RequirementMapper.selectItemIconId(
				Arrays.asList(-1, 1349, 1351),
				Arrays.asList(1353),
				itemId -> false));
	}

	@Test
	public void rewardsUseNativeItemSkillAndQuestPointIcons()
	{
		JournalSnapshot.IconIdentity item = questViewMapper.rewardIcon(
			new ItemReward("Coins", 995, 5));
		assertEquals(JournalSnapshot.IconType.ITEM, item.getType());
		assertEquals(Integer.valueOf(995), item.getItemId());
		assertEquals(5, item.getQuantity());

		JournalSnapshot.IconIdentity experience = questViewMapper.rewardIcon(
			new ExperienceReward(Skill.WOODCUTTING, 100));
		assertEquals(JournalSnapshot.IconType.SKILL, experience.getType());
		assertEquals(Skill.WOODCUTTING.name(), experience.getSkill());

		assertEquals(
			JournalSnapshot.IconType.QUEST_POINTS,
			questViewMapper.rewardIcon(new QuestPointReward(1)).getType());
	}

	@Test
	public void enemiesAreDistinctWhileNotesRetainAuthoredDuplicates()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.DRAGON_SLAYER_I);
		when(helper.getCombatRequirements()).thenReturn(Arrays.asList(
			"Elvarg (level 83)",
			"Elvarg (level 83)",
			"  Lesser demon  "));
		when(helper.getNotes()).thenReturn(Arrays.asList(
			"Bring food",
			"Bring food",
			"  Use protection prayers  "));

		QuestViewMapper.Definition definition =
			questViewMapper.buildDefinition(helper);

		assertEquals(
			Arrays.asList("Elvarg (level 83)", "Lesser demon"),
			definition.enemies);
		assertEquals(
			Arrays.asList("Bring food", "Bring food", "Use protection prayers"),
			definition.notes);
	}

	@Test
	public void progressUsesCachedCoreStatesAndQuestPointFallback()
	{
		QuestHelper completed = helper(QuestHelperQuest.COOKS_ASSISTANT);
		when(completed.getQuestPointReward()).thenReturn(new QuestPointReward(1));
		QuestHelper incomplete = helper(QuestHelperQuest.DRAGON_SLAYER_I);
		when(incomplete.getQuestPointReward()).thenReturn(new QuestPointReward(2));
		QuestHelper miniquest = helper(QuestHelperQuest.ENTER_THE_ABYSS);
		when(miniquest.getQuestPointReward()).thenReturn(new QuestPointReward(99));
		Map<QuestHelper, QuestState> state = state(
			Arrays.asList(completed, incomplete, miniquest),
			Arrays.asList(QuestState.FINISHED, QuestState.IN_PROGRESS, QuestState.FINISHED));
		when(client.getVarpValue(VarPlayerID.QP)).thenReturn(2);
		when(client.getVarbitValue(VarbitID.QP_MAX)).thenReturn(0);
		when(questManager.getQuestListState()).thenReturn(state);
		dataSource.startUp();
		dataSource.setOpen(true);
		dataSource.afterQuestUpdate(1);

		JournalSnapshot.QuestProgress fallback = dataSource.getSnapshot().getQuestProgress();

		assertEquals(1, fallback.getCompletedQuestCount());
		assertEquals(2, fallback.getTotalQuestCount());
		assertEquals(2, fallback.getCurrentQuestPoints());
		assertEquals(3, fallback.getTotalQuestPoints());
		verify(completed, never()).getState(any(Client.class));
		verify(incomplete, never()).getState(any(Client.class));
		verify(miniquest, never()).getState(any(Client.class));

		when(client.getVarpValue(VarPlayerID.QP)).thenReturn(5);
		when(client.getVarbitValue(VarbitID.QP_MAX)).thenReturn(1);
		dataSource.afterQuestUpdate(2);
		assertEquals(5, dataSource.getSnapshot().getQuestProgress().getTotalQuestPoints());
	}

	@Test
	public void selectedQuestStatePatchesItsCatalogEntryImmediately()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		when(helper.getState(client)).thenReturn(QuestState.IN_PROGRESS);
		Map<QuestHelper, QuestState> initialState =
			state(helper, QuestState.NOT_STARTED);
		when(questManager.getQuestListState()).thenReturn(initialState);
		when(questManager.getSelectedQuestRevision()).thenReturn(1L);
		when(questManager.getSelectedQuest()).thenReturn(helper);

		openAndRefresh(10);

		assertEquals(
			JournalSnapshot.QuestState.IN_PROGRESS,
			dataSource.getSnapshot().getQuests().get(0).getState());
		assertEquals(
			JournalSnapshot.QuestState.IN_PROGRESS,
			dataSource.getSnapshot().getSelectedQuest().getOverview().getState());
	}

	@Test
	public void unavailableCatalogRejectsSelection()
	{
		when(questManager.getQuestListState()).thenReturn(Collections.emptyMap());
		dataSource.startUp();

		assertFalse(dataSource.activateQuest(QuestHelperQuest.COOKS_ASSISTANT.name()));

		verify(questManager, never()).startUpQuest(any(QuestHelper.class), anyBoolean());
	}

	@Test
	public void filteredSnapshotIsCachedUntilItsSourceChanges()
	{
		QuestHelper cooksAssistant = helper(QuestHelperQuest.COOKS_ASSISTANT);
		QuestHelper dragonSlayer = helper(QuestHelperQuest.DRAGON_SLAYER_I);
		AtomicReference<Map<QuestHelper, QuestState>> state = new AtomicReference<>(
			state(
				Arrays.asList(cooksAssistant, dragonSlayer),
				Arrays.asList(QuestState.NOT_STARTED, QuestState.IN_PROGRESS)));
		when(questManager.getQuestListState()).thenAnswer(invocation -> state.get());
		openAndRefresh(20);
		JournalSnapshot.QuestFilter filter =
			JournalSnapshot.QuestFilter.all().withSearchText("cook");

		JournalSnapshot first = dataSource.getSnapshot(filter);
		assertEquals(1, first.getQuests().size());
		assertSame(first, dataSource.getSnapshot(filter));

		state.set(state(
			Arrays.asList(cooksAssistant, dragonSlayer),
			Arrays.asList(QuestState.FINISHED, QuestState.IN_PROGRESS)));
		dataSource.afterQuestUpdate(21);
		JournalSnapshot updated = dataSource.getSnapshot(filter);

		assertNotSame(first, updated);
		assertEquals(JournalSnapshot.QuestState.COMPLETE, updated.getQuests().get(0).getState());
		assertSame(updated, dataSource.getSnapshot(filter));
	}

	private void openAndRefresh(int tick)
	{
		dataSource.startUp();
		dataSource.setOpen(true);
		dataSource.afterQuestUpdate(tick);
	}

	private JournalSnapshot.SelectedQuest evaluate(
		QuestViewMapper.Definition definition)
	{
		QuestViewMapper.StepSelection steps =
			questViewMapper.resolveSteps(definition.helper, true);
		return questViewMapper.evaluate(
			definition,
			steps.getRoot(),
			steps.getLeaf(),
			true);
	}

	private DetailedQuestStep objectiveStep(String text)
	{
		DetailedQuestStep step = mock(DetailedQuestStep.class);
		when(step.isShowInSidebar()).thenReturn(true);
		when(step.getText()).thenReturn(Collections.singletonList(text));
		return step;
	}

	private ItemRequirement stepItem(String text, int itemId)
	{
		ItemRequirement item = mock(ItemRequirement.class);
		when(item.getDisplayText()).thenReturn(text);
		when(item.shouldDisplayText(client)).thenReturn(true);
		when(item.getDisplayItemIds()).thenReturn(Collections.singletonList(itemId));
		when(item.getAllIds()).thenReturn(Collections.singletonList(itemId));
		when(item.getQuantity()).thenReturn(1);
		return item;
	}

	private Requirement displayRequirement(String text)
	{
		Requirement requirement = mock(Requirement.class);
		when(requirement.getDisplayText()).thenReturn(text);
		when(requirement.shouldDisplayText(client)).thenReturn(true);
		return requirement;
	}

	private QuestHelper helper(QuestHelperQuest quest)
	{
		QuestHelper helper = mock(QuestHelper.class);
		when(helper.getQuest()).thenReturn(quest);
		when(helper.getDisplayedQuestName()).thenReturn(quest.getName());
		return helper;
	}

	private QuestHelper selectedHelper(QuestHelperQuest quest)
	{
		QuestHelper helper = helper(quest);
		when(helper.getPanels()).thenReturn(Collections.emptyList());
		when(helper.getGeneralRequirements()).thenReturn(Collections.emptyList());
		when(helper.getItemRequirements()).thenReturn(Collections.emptyList());
		when(helper.getGeneralRecommended()).thenReturn(Collections.emptyList());
		when(helper.getItemRecommended()).thenReturn(Collections.emptyList());
		when(helper.getCombatRequirements()).thenReturn(Collections.emptyList());
		when(helper.getQuestRewards()).thenReturn(Collections.emptyList());
		when(helper.getNotes()).thenReturn(Collections.emptyList());
		return helper;
	}

	private static Map<QuestHelper, QuestState> state(
		QuestHelper helper,
		QuestState state)
	{
		return state(
			Collections.singletonList(helper),
			Collections.singletonList(state));
	}

	private static Map<QuestHelper, QuestState> state(
		List<QuestHelper> helpers,
		List<QuestState> questStates)
	{
		assertEquals(helpers.size(), questStates.size());
		Map<QuestHelper, QuestState> states = new LinkedHashMap<>();
		for (int index = 0; index < helpers.size(); index++)
		{
			states.put(helpers.get(index), questStates.get(index));
		}
		return states;
	}
}
