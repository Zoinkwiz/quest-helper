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
import com.questhelper.config.LeagueFiltering;
import com.questhelper.managers.QuestManager;
import com.questhelper.managers.QuestMenuHandler;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.questinfo.LeagueRegion;
import com.questhelper.requirements.Requirement;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.QuestStep;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import net.runelite.api.Client;
import net.runelite.api.QuestState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class JournalDataSourceTest
{
	private Client client;
	private QuestManager questManager;
	private QuestMenuHandler questMenuHandler;
	private QuestHelperConfig config;
	private CountingCatalogMapper catalogMapper;
	private RequirementMapper requirementMapper;
	private CountingQuestViewMapper questViewMapper;
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
		catalogMapper = new CountingCatalogMapper(client);
		requirementMapper = new RequirementMapper(client, config);
		questViewMapper = new CountingQuestViewMapper(
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
	public void stoppedAndClosedTicksPerformNoDataWork()
	{
		dataSource.afterQuestUpdate(1);

		assertNoBuildWork();
		verifyNoInteractions(questManager);

		dataSource.startUp();
		dataSource.afterQuestUpdate(2);

		assertNoBuildWork();
		verifyNoInteractions(questManager);
		assertTrue(dataSource.getSnapshot().getQuests().isEmpty());
		assertNull(dataSource.getSnapshot().getSelectedQuest());
	}

	@Test
	public void catalogUsesQuestManagersCachedStateWithoutCallingHelperState()
	{
		QuestHelper helper = helper(QuestHelperQuest.COOKS_ASSISTANT);
		when(helper.getState(any(Client.class))).thenThrow(
			new AssertionError("Catalog mapping must use QuestManager's cached quest states"));
		Map<QuestHelper, QuestState> state = state(helper, QuestState.FINISHED);
		when(questManager.getQuestListState()).thenReturn(state);

		openAndRefresh(10);

		assertEquals(1, dataSource.getSnapshot().getQuests().size());
		assertEquals(
			JournalSnapshot.QuestState.COMPLETE,
			dataSource.getSnapshot().getQuests().get(0).getState());
		assertEquals(1, catalogMapper.catalogBuilds);
		verify(helper, never()).getState(any(Client.class));
	}

	@Test
	public void catalogLinkageFailureKeepsThePreviousCatalogAndRetries()
	{
		QuestHelper first = helper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> firstState = state(first, QuestState.NOT_STARTED);
		when(questManager.getQuestListState()).thenReturn(firstState);

		openAndRefresh(11);
		List<JournalSnapshot.QuestListItem> previous =
			dataSource.getSnapshot().getQuests();

		QuestHelper second = helper(QuestHelperQuest.DRAGON_SLAYER_I);
		Map<QuestHelper, QuestState> secondState =
			state(second, QuestState.NOT_STARTED);
		when(questManager.getQuestListState()).thenReturn(secondState);
		catalogMapper.remainingCatalogLinkageFailures = 1;

		assertDoesNotThrow(() -> dataSource.afterQuestUpdate(12));
		assertSame(previous, dataSource.getSnapshot().getQuests());

		dataSource.afterQuestUpdate(13);

		assertEquals(QuestHelperQuest.DRAGON_SLAYER_I.name(),
			dataSource.getSnapshot().getQuests().get(0).getId());
	}

	@Test
	public void unchangedAdjacentTicksRetainWithoutAnotherSelectedEvaluation()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(7L);
		when(questManager.getSelectedQuest()).thenReturn(helper);

		openAndRefresh(20);
		JournalSnapshot first = dataSource.getSnapshot();
		JournalSnapshot.SelectedQuest selected = first.getSelectedQuest();

		dataSource.afterQuestUpdate(20);
		assertSame(first, dataSource.getSnapshot());
		assertSame(selected, dataSource.getSnapshot().getSelectedQuest());
		assertEquals(1, questViewMapper.viewEvaluations);

		dataSource.afterQuestUpdate(21);
		assertSame(first, dataSource.getSnapshot());
		assertSame(selected, dataSource.getSnapshot().getSelectedQuest());
		assertEquals(1, catalogMapper.catalogBuilds);
		assertEquals(1, questViewMapper.definitionBuilds);
		assertEquals(1, questViewMapper.viewEvaluations);
		verify(helper).getState(client);
	}

	@Test
	public void dirtyBurstsCoalesceIntoOneSelectedEvaluation()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(8L);
		when(questManager.getSelectedQuest()).thenReturn(helper);

		openAndRefresh(20);
		dataSource.markViewedQuestDirty();
		dataSource.markViewedQuestDirty();
		dataSource.markViewedQuestDirty();
		dataSource.afterQuestUpdate(21);
		dataSource.afterQuestUpdate(21);

		assertEquals(2, questViewMapper.viewEvaluations);
		verify(helper, times(2)).getState(client);
	}

	@Test
	public void activeStepIdentityChangeRefreshesOnTheAdjacentTick()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		QuestStep first = mock(QuestStep.class);
		QuestStep second = mock(QuestStep.class);
		when(first.getActiveStep()).thenReturn(first);
		when(second.getActiveStep()).thenReturn(second);
		when(helper.getCurrentStep()).thenReturn(first);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(9L);
		when(questManager.getSelectedQuest()).thenReturn(helper);

		openAndRefresh(60);
		when(helper.getCurrentStep()).thenReturn(second);
		dataSource.afterQuestUpdate(61);

		assertEquals(2, questViewMapper.viewEvaluations);
		verify(helper, times(2)).getState(client);
	}

	@Test
	public void liveRootChangeRefreshesEvenWhenTheDeepestStepIsReused()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		QuestStep leaf = mock(QuestStep.class);
		QuestStep firstRoot = mock(QuestStep.class);
		QuestStep secondRoot = mock(QuestStep.class);
		when(firstRoot.getActiveStep()).thenReturn(leaf);
		when(secondRoot.getActiveStep()).thenReturn(leaf);
		when(helper.getCurrentStep()).thenReturn(firstRoot);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(13L);
		when(questManager.getSelectedQuest()).thenReturn(helper);

		openAndRefresh(80);
		when(helper.getCurrentStep()).thenReturn(secondRoot);
		dataSource.afterQuestUpdate(81);

		assertEquals(2, questViewMapper.viewEvaluations);
		verify(helper, times(2)).getState(client);
	}

	@Test
	public void fallbackRefreshCoversHelpersWithoutDedicatedDirtyEvents()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(10L);
		when(questManager.getSelectedQuest()).thenReturn(helper);

		openAndRefresh(100);
		for (int tick = 101; tick < 100 + JournalDataSource.VIEWED_QUEST_REFRESH_FALLBACK_TICKS; tick++)
		{
			dataSource.afterQuestUpdate(tick);
		}
		assertEquals(1, questViewMapper.viewEvaluations);

		dataSource.afterQuestUpdate(100 + JournalDataSource.VIEWED_QUEST_REFRESH_FALLBACK_TICKS);

		assertEquals(2, questViewMapper.viewEvaluations);
		verify(helper, times(2)).getState(client);
	}

	@Test
	public void failedPreviewDefinitionRetriesWithBackoffAndRecovers()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(12L);
		when(questManager.getSelectedQuest()).thenReturn(helper);
		questViewMapper.remainingDefinitionFailures = 2;

		openAndRefresh(110);
		dataSource.afterQuestUpdate(110);
		dataSource.afterQuestUpdate(111);
		dataSource.afterQuestUpdate(112);

		assertEquals(2, questViewMapper.definitionBuilds);
		assertNull(dataSource.getSnapshot().getSelectedQuest());

		dataSource.afterQuestUpdate(113);

		assertEquals(3, questViewMapper.definitionBuilds);
		assertNotNull(dataSource.getSnapshot().getSelectedQuest());
	}

	@Test
	public void missingPreviewClassIsIsolatedAndRetried()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(12L);
		when(questManager.getSelectedQuest()).thenReturn(helper);
		questViewMapper.remainingDefinitionLinkageFailures = 2;

		assertDoesNotThrow(() -> openAndRefresh(114));
		dataSource.afterQuestUpdate(114);
		dataSource.afterQuestUpdate(115);
		dataSource.afterQuestUpdate(116);

		assertEquals(2, questViewMapper.definitionBuilds);
		assertEquals(
			QuestHelperQuest.COOKS_ASSISTANT.name(),
			dataSource.getViewedQuestId());
		assertNull(dataSource.getSnapshot().getSelectedQuest());

		dataSource.afterQuestUpdate(117);

		assertEquals(3, questViewMapper.definitionBuilds);
		assertNotNull(dataSource.getSnapshot().getSelectedQuest());
	}

	@Test
	public void pendingSelectionRemainsFocusedWhileItsDefinitionRetries()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(12L);
		when(questManager.getSelectedQuest()).thenReturn(helper);
		questViewMapper.remainingDefinitionLinkageFailures = 1;
		QuestSelectionController controller =
			new QuestSelectionController(client, config);
		controller.attach(dataSource);

		openAndRefresh(119);
		assertNull(dataSource.getSnapshot().getSelectedQuest());

		assertTrue(controller.publishSnapshot(dataSource.getSnapshot(), null));
		assertEquals(
			QuestHelperQuest.COOKS_ASSISTANT.name(),
			controller.getBrowsedQuestId());
	}

	@Test
	public void clearingAndReselectingABrokenQuestDoesNotBypassBackoff()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(12L);
		when(questManager.getSelectedQuest()).thenReturn(helper);
		questViewMapper.remainingDefinitionLinkageFailures = 2;

		openAndRefresh(120);
		assertEquals(1, questViewMapper.definitionBuilds);

		assertTrue(dataSource.clearBrowsedQuest(
			QuestHelperQuest.COOKS_ASSISTANT.name()));
		assertTrue(dataSource.browseQuest(
			QuestHelperQuest.COOKS_ASSISTANT.name()));
		dataSource.afterQuestUpdate(120);

		assertEquals(1, questViewMapper.definitionBuilds);

		dataSource.afterQuestUpdate(121);

		assertEquals(2, questViewMapper.definitionBuilds);
	}

	@Test
	public void fatalVmErrorDuringDefinitionIsNotMasked()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(12L);
		when(questManager.getSelectedQuest()).thenReturn(helper);
		questViewMapper.fatalDefinitionFailure = true;

		assertThrows(OutOfMemoryError.class, () -> openAndRefresh(118));
	}

	@Test
	public void failedDefinitionRebuildRetainsTheLastGoodSelectedView()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(13L);
		when(questManager.getSelectedQuest()).thenReturn(helper);

		openAndRefresh(120);
		JournalSnapshot.SelectedQuest selected =
			dataSource.getSnapshot().getSelectedQuest();
		questViewMapper.remainingDefinitionFailures = 1;

		dataSource.invalidate();
		assertDoesNotThrow(() -> dataSource.afterQuestUpdate(121));

		assertSame(selected, dataSource.getSnapshot().getSelectedQuest());
		assertEquals(2, questViewMapper.definitionBuilds);

		dataSource.afterQuestUpdate(122);

		assertNotNull(dataSource.getSnapshot().getSelectedQuest());
		assertEquals(3, questViewMapper.definitionBuilds);
	}

	@Test
	public void failedNewSelectionDoesNotDisplayThePreviousQuest()
	{
		QuestHelper active = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		QuestHelper browsed = selectedHelper(QuestHelperQuest.DRAGON_SLAYER_I);
		Map<QuestHelper, QuestState> states = new LinkedHashMap<>();
		states.put(active, QuestState.IN_PROGRESS);
		states.put(browsed, QuestState.NOT_STARTED);
		when(questManager.getQuestListState()).thenReturn(states);
		when(questManager.getSelectedQuestRevision()).thenReturn(14L);
		when(questManager.getSelectedQuest()).thenReturn(active);

		openAndRefresh(130);
		assertNotNull(dataSource.getSnapshot().getSelectedQuest());
		questViewMapper.remainingDefinitionFailures = 1;

		assertTrue(dataSource.browseQuest(QuestHelperQuest.DRAGON_SLAYER_I.name()));
		assertDoesNotThrow(() -> dataSource.afterQuestUpdate(131));

		assertNull(dataSource.getSnapshot().getSelectedQuest());
		assertEquals(
			QuestHelperQuest.COOKS_ASSISTANT.name(),
			dataSource.getSnapshot().getActiveQuest().getId());

		dataSource.afterQuestUpdate(132);

		assertEquals(
			QuestHelperQuest.DRAGON_SLAYER_I.name(),
			dataSource.getSnapshot().getSelectedQuest().getOverview().getId());
	}

	@Test
	public void selectedResolutionAndEvaluationFailuresAreIsolatedAndRetried()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(15L);
		when(questManager.getSelectedQuest()).thenReturn(helper);

		openAndRefresh(140);
		JournalSnapshot.SelectedQuest selected =
			dataSource.getSnapshot().getSelectedQuest();

		questViewMapper.remainingResolutionFailures = 1;
		dataSource.markViewedQuestDirty();
		assertDoesNotThrow(() -> dataSource.afterQuestUpdate(141));
		assertSame(selected, dataSource.getSnapshot().getSelectedQuest());
		assertEquals(1, questViewMapper.viewEvaluations);

		dataSource.afterQuestUpdate(141);
		assertEquals(1, questViewMapper.viewEvaluations);
		dataSource.afterQuestUpdate(142);
		assertEquals(2, questViewMapper.viewEvaluations);

		questViewMapper.remainingEvaluationFailures = 1;
		dataSource.markViewedQuestDirty();
		assertDoesNotThrow(() -> dataSource.afterQuestUpdate(143));
		assertSame(selected, dataSource.getSnapshot().getSelectedQuest());
		assertEquals(3, questViewMapper.viewEvaluations);

		dataSource.afterQuestUpdate(143);
		assertEquals(3, questViewMapper.viewEvaluations);
		dataSource.afterQuestUpdate(144);
		assertEquals(4, questViewMapper.viewEvaluations);
	}

	@Test
	public void missingClassDuringEvaluationRetainsTheLastGoodPreview()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(15L);
		when(questManager.getSelectedQuest()).thenReturn(helper);

		openAndRefresh(145);
		JournalSnapshot.SelectedQuest selected =
			dataSource.getSnapshot().getSelectedQuest();
		questViewMapper.remainingEvaluationLinkageFailures = 1;
		dataSource.markViewedQuestDirty();

		assertDoesNotThrow(() -> dataSource.afterQuestUpdate(146));
		assertSame(selected, dataSource.getSnapshot().getSelectedQuest());

		dataSource.afterQuestUpdate(147);

		assertNotNull(dataSource.getSnapshot().getSelectedQuest());
		assertEquals(3, questViewMapper.viewEvaluations);
	}

	@Test
	public void missingClassDuringStepResolutionRetainsTheLastGoodPreview()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(15L);
		when(questManager.getSelectedQuest()).thenReturn(helper);

		openAndRefresh(148);
		JournalSnapshot.SelectedQuest selected =
			dataSource.getSnapshot().getSelectedQuest();
		questViewMapper.remainingResolutionLinkageFailures = 1;
		dataSource.markViewedQuestDirty();

		assertDoesNotThrow(() -> dataSource.afterQuestUpdate(149));
		assertSame(selected, dataSource.getSnapshot().getSelectedQuest());

		dataSource.afterQuestUpdate(150);

		assertNotNull(dataSource.getSnapshot().getSelectedQuest());
		assertEquals(3, questViewMapper.stepResolutions);
	}

	@Test
	public void activeHelperStepResolutionFailureRetainsTheLastGoodPreview()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		QuestStep objective = objectiveStep("Prepare the ingredients");
		when(objective.getActiveStep()).thenReturn(objective);
		when(helper.getPanels()).thenReturn(Collections.singletonList(
			new PanelDetails(
				"Cooking",
				Collections.singletonList(objective),
				Collections.emptyList())));
		AtomicBoolean stepAvailable = new AtomicBoolean(true);
		when(helper.getCurrentStep()).thenAnswer(invocation ->
		{
			if (!stepAvailable.get())
			{
				throw new IllegalStateException("active step unavailable");
			}
			return objective;
		});
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(16L);
		when(questManager.getSelectedQuest()).thenReturn(helper);

		openAndRefresh(150);
		JournalSnapshot.SelectedQuest selected =
			dataSource.getSnapshot().getSelectedQuest();
		assertTrue(selected.getObjectives().get(0).isCurrent());

		stepAvailable.set(false);
		dataSource.markViewedQuestDirty();
		assertDoesNotThrow(() -> dataSource.afterQuestUpdate(151));

		assertSame(selected, dataSource.getSnapshot().getSelectedQuest());
		assertTrue(dataSource.getSnapshot().getSelectedQuest()
			.getObjectives().get(0).isCurrent());

		stepAvailable.set(true);
		dataSource.afterQuestUpdate(152);

		assertTrue(dataSource.getSnapshot().getSelectedQuest()
			.getObjectives().get(0).isCurrent());
	}

	@Test
	public void inactiveConditionalResolutionFailureRetainsTheLastGoodPreview()
	{
		QuestHelper active = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		BasicQuestHelper browsed =
			selectedBasicHelper(QuestHelperQuest.DRAGON_SLAYER_I);
		QuestStep first = objectiveStep("Prepare for the dragon");
		QuestStep second = objectiveStep("Defeat the dragon");
		Requirement secondStepActive = mock(Requirement.class);
		AtomicBoolean conditionAvailable = new AtomicBoolean(true);
		when(secondStepActive.check(client)).thenAnswer(invocation ->
		{
			if (!conditionAvailable.get())
			{
				throw new IllegalStateException("step condition unavailable");
			}
			return true;
		});
		ConditionalStep progression = mock(ConditionalStep.class);
		LinkedHashMap<Requirement, QuestStep> branches = new LinkedHashMap<>();
		branches.put(null, first);
		branches.put(secondStepActive, second);
		when(progression.getStepsMap()).thenReturn(branches);
		when(browsed.getStepList()).thenReturn(Collections.singletonMap(1, progression));
		when(browsed.getVar()).thenReturn(1);
		when(browsed.getPanels()).thenReturn(Collections.singletonList(
			new PanelDetails(
				"Dragon",
				List.of(first, second),
				Collections.emptyList())));
		Map<QuestHelper, QuestState> states = new LinkedHashMap<>();
		states.put(active, QuestState.IN_PROGRESS);
		states.put(browsed, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(states);
		when(questManager.getSelectedQuestRevision()).thenReturn(17L);
		when(questManager.getSelectedQuest()).thenReturn(active);

		openAndRefresh(160);
		assertTrue(dataSource.browseQuest(QuestHelperQuest.DRAGON_SLAYER_I.name()));
		dataSource.afterQuestUpdate(161);
		JournalSnapshot.SelectedQuest selected =
			dataSource.getSnapshot().getSelectedQuest();
		assertEquals(
			JournalSnapshot.ObjectiveState.COMPLETE,
			selected.getObjectives().get(0).getState());

		conditionAvailable.set(false);
		dataSource.markViewedQuestDirty();
		assertDoesNotThrow(() -> dataSource.afterQuestUpdate(162));

		assertSame(selected, dataSource.getSnapshot().getSelectedQuest());
		assertEquals(
			JournalSnapshot.ObjectiveState.COMPLETE,
			dataSource.getSnapshot().getSelectedQuest()
				.getObjectives().get(0).getState());

		conditionAvailable.set(true);
		dataSource.afterQuestUpdate(163);

		assertEquals(
			JournalSnapshot.ObjectiveState.COMPLETE,
			dataSource.getSnapshot().getSelectedQuest()
				.getObjectives().get(0).getState());
	}

	@Test
	public void initialQuestStateFailureDoesNotPublishAMislabelledPreview()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		AtomicBoolean stateAvailable = new AtomicBoolean(false);
		when(helper.getState(client)).thenAnswer(invocation ->
		{
			if (!stateAvailable.get())
			{
				throw new IllegalStateException("quest state unavailable");
			}
			return QuestState.IN_PROGRESS;
		});
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(18L);
		when(questManager.getSelectedQuest()).thenReturn(helper);

		assertDoesNotThrow(() -> openAndRefresh(170));

		assertNull(dataSource.getSnapshot().getSelectedQuest());
		assertEquals(
			JournalSnapshot.QuestState.IN_PROGRESS,
			dataSource.getSnapshot().getActiveQuest().getState());

		stateAvailable.set(true);
		dataSource.afterQuestUpdate(171);

		assertEquals(
			JournalSnapshot.QuestState.IN_PROGRESS,
			dataSource.getSnapshot().getSelectedQuest().getOverview().getState());
	}

	@Test
	public void questStateRefreshFailureRetainsTheLastGoodPreview()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		AtomicBoolean stateAvailable = new AtomicBoolean(true);
		AtomicReference<QuestState> liveState =
			new AtomicReference<>(QuestState.IN_PROGRESS);
		when(helper.getState(client)).thenAnswer(invocation ->
		{
			if (!stateAvailable.get())
			{
				throw new IllegalStateException("quest state unavailable");
			}
			return liveState.get();
		});
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(19L);
		when(questManager.getSelectedQuest()).thenReturn(helper);

		openAndRefresh(180);
		JournalSnapshot.SelectedQuest selected =
			dataSource.getSnapshot().getSelectedQuest();

		stateAvailable.set(false);
		dataSource.markViewedQuestDirty();
		assertDoesNotThrow(() -> dataSource.afterQuestUpdate(181));

		assertSame(selected, dataSource.getSnapshot().getSelectedQuest());
		assertEquals(
			JournalSnapshot.QuestState.IN_PROGRESS,
			dataSource.getSnapshot().getSelectedQuest().getOverview().getState());

		stateAvailable.set(true);
		liveState.set(QuestState.FINISHED);
		dataSource.afterQuestUpdate(182);

		assertEquals(
			JournalSnapshot.QuestState.COMPLETE,
			dataSource.getSnapshot().getSelectedQuest().getOverview().getState());
	}

	@Test
	public void activeStateLinkageFailureRetainsTheLastGoodState()
	{
		QuestHelper active = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		QuestHelper browsed = selectedHelper(QuestHelperQuest.DRAGON_SLAYER_I);
		AtomicBoolean stateAvailable = new AtomicBoolean(true);
		when(active.getState(client)).thenAnswer(invocation ->
		{
			if (!stateAvailable.get())
			{
				throw new NoClassDefFoundError("missing active-state dependency");
			}
			return QuestState.IN_PROGRESS;
		});
		Map<QuestHelper, QuestState> states = new LinkedHashMap<>();
		states.put(active, QuestState.IN_PROGRESS);
		states.put(browsed, QuestState.NOT_STARTED);
		when(questManager.getQuestListState()).thenReturn(states);
		when(questManager.getSelectedQuestRevision()).thenReturn(20L);
		when(questManager.getSelectedQuest()).thenReturn(active);

		openAndRefresh(183);
		assertTrue(dataSource.browseQuest(QuestHelperQuest.DRAGON_SLAYER_I.name()));
		dataSource.afterQuestUpdate(184);
		JournalSnapshot.ActiveQuest previous =
			dataSource.getSnapshot().getActiveQuest();

		stateAvailable.set(false);
		assertDoesNotThrow(() -> dataSource.afterQuestUpdate(185));

		assertSame(previous, dataSource.getSnapshot().getActiveQuest());
	}

	@Test
	public void activeViewLinkageFailureIsIsolatedAndRetried()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(21L);
		when(questManager.getSelectedQuest()).thenReturn(helper);
		catalogMapper.remainingActiveViewLinkageFailures = 1;

		assertDoesNotThrow(() -> openAndRefresh(186));
		assertNull(dataSource.getSnapshot().getActiveQuest());

		dataSource.afterQuestUpdate(187);

		assertNotNull(dataSource.getSnapshot().getActiveQuest());
	}

	@Test
	public void selectedDefinitionRebuildsOnlyWhenSelectedQuestRevisionChanges()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		AtomicLong selectedRevision = new AtomicLong(11L);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenAnswer(
			invocation -> selectedRevision.get());
		when(questManager.getSelectedQuest()).thenReturn(helper);

		openAndRefresh(30);
		JournalSnapshot.SelectedQuest first = dataSource.getSnapshot().getSelectedQuest();
		dataSource.afterQuestUpdate(31);

		assertEquals(1, questViewMapper.definitionBuilds);
		assertSame(first, dataSource.getSnapshot().getSelectedQuest());

		selectedRevision.incrementAndGet();
		dataSource.afterQuestUpdate(32);

		assertEquals(2, questViewMapper.definitionBuilds);
		assertNotSame(first, dataSource.getSnapshot().getSelectedQuest());
	}

	@Test
	public void selectedQuestSwitchEvaluatesImmediatelyWithinTheSameTick()
	{
		QuestHelper firstHelper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		QuestHelper secondHelper = selectedHelper(QuestHelperQuest.DRAGON_SLAYER_I);
		Map<QuestHelper, QuestState> states = new LinkedHashMap<>();
		states.put(firstHelper, QuestState.IN_PROGRESS);
		states.put(secondHelper, QuestState.IN_PROGRESS);
		Map<QuestHelper, QuestState> state = states;
		AtomicLong selectedRevision = new AtomicLong(1L);
		AtomicReference<QuestHelper> selectedHelper = new AtomicReference<>(firstHelper);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenAnswer(
			invocation -> selectedRevision.get());
		when(questManager.getSelectedQuest()).thenAnswer(
			invocation -> selectedHelper.get());

		openAndRefresh(33);
		assertEquals(
			QuestHelperQuest.COOKS_ASSISTANT.name(),
			dataSource.getSnapshot().getSelectedQuest().getOverview().getId());

		selectedHelper.set(secondHelper);
		selectedRevision.incrementAndGet();
		dataSource.afterQuestUpdate(33);

		assertEquals(
			QuestHelperQuest.DRAGON_SLAYER_I.name(),
			dataSource.getSnapshot().getSelectedQuest().getOverview().getId());
		assertEquals(2, questViewMapper.definitionBuilds);
		assertEquals(2, questViewMapper.viewEvaluations);
	}

	@Test
	public void invalidationRebuildsAndEvaluatesImmediatelyWithinTheSameTick()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(2L);
		when(questManager.getSelectedQuest()).thenReturn(helper);

		openAndRefresh(34);
		JournalSnapshot.SelectedQuest first = dataSource.getSnapshot().getSelectedQuest();

		dataSource.invalidate();
		dataSource.afterQuestUpdate(34);

		assertNotSame(first, dataSource.getSnapshot().getSelectedQuest());
		assertEquals(
			QuestHelperQuest.COOKS_ASSISTANT.name(),
			dataSource.getSnapshot().getSelectedQuest().getOverview().getId());
		assertEquals(2, questViewMapper.definitionBuilds);
		assertEquals(2, questViewMapper.viewEvaluations);
	}

	@Test
	public void knownQuestActivatesWithoutOpeningTheSidebar()
	{
		QuestHelper helper = helper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.NOT_STARTED);
		when(questManager.getQuestListState()).thenReturn(state);
		dataSource.startUp();

		assertTrue(dataSource.activateQuest(QuestHelperQuest.COOKS_ASSISTANT.name()));

		verify(questManager).startUpQuest(helper, false);
	}

	@Test
	public void unknownOrBlankQuestDoesNotActivateAnything()
	{
		QuestHelper helper = helper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.NOT_STARTED);
		when(questManager.getQuestListState()).thenReturn(state);
		dataSource.startUp();

		assertFalse(dataSource.activateQuest("NOT_A_QUEST"));
		assertFalse(dataSource.activateQuest("  "));
		assertFalse(dataSource.activateQuest(null));

		verify(questManager, never()).startUpQuest(any(QuestHelper.class), any(Boolean.class));
	}

	@Test
	public void alreadySelectedQuestIsAcceptedWithoutRestartingTheHelper()
	{
		QuestHelper helper = helper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuest()).thenReturn(helper);
		dataSource.startUp();

		assertTrue(dataSource.activateQuest(QuestHelperQuest.COOKS_ASSISTANT.name()));

		verify(questManager, never()).startUpQuest(any(QuestHelper.class), any(Boolean.class));
	}

	@Test
	public void activeNotStartedQuestPointsAtItsFirstVisibleObjective()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		QuestStep first = objectiveStep("Gather the ingredients");
		QuestStep second = objectiveStep("Bake the cake");
		when(helper.getPanels()).thenReturn(Collections.singletonList(
			new PanelDetails(
				"Kitchen",
				List.of(first, second),
				Collections.emptyList())));
		when(helper.getCurrentStep()).thenReturn(second);
		when(second.getActiveStep()).thenReturn(second);
		when(helper.getState(client)).thenReturn(QuestState.NOT_STARTED);
		Map<QuestHelper, QuestState> questListState =
			state(helper, QuestState.NOT_STARTED);
		when(questManager.getQuestListState()).thenReturn(questListState);
		when(questManager.getSelectedQuestRevision()).thenReturn(3L);
		when(questManager.getSelectedQuest()).thenReturn(helper);

		openAndRefresh(60);

		List<JournalSnapshot.Objective> objectives =
			dataSource.getSnapshot().getSelectedQuest().getObjectives();
		assertEquals(2, objectives.size());
		assertTrue(objectives.get(0).isCurrent());
		assertFalse(objectives.get(1).isCurrent());
		assertEquals(JournalSnapshot.ObjectiveState.AVAILABLE, objectives.get(0).getState());
	}

	@Test
	public void matchingActiveQuestCanBeStoppedThroughTheQuestManager()
	{
		QuestHelper helper = helper(QuestHelperQuest.COOKS_ASSISTANT);
		when(questManager.getSelectedQuest()).thenReturn(helper);
		dataSource.startUp();

		assertTrue(dataSource.stopActiveQuest(QuestHelperQuest.COOKS_ASSISTANT.name()));

		verify(questManager).shutDownQuest(true);
	}

	@Test
	public void staleOrMissingActiveQuestCannotStopAnotherHelper()
	{
		QuestHelper helper = helper(QuestHelperQuest.DRAGON_SLAYER_I);
		when(questManager.getSelectedQuest()).thenReturn(helper);
		dataSource.startUp();

		assertFalse(dataSource.stopActiveQuest(QuestHelperQuest.COOKS_ASSISTANT.name()));
		when(questManager.getSelectedQuest()).thenReturn(null);
		assertFalse(dataSource.stopActiveQuest(QuestHelperQuest.DRAGON_SLAYER_I.name()));
		assertFalse(dataSource.stopActiveQuest(null));

		verify(questManager, never()).shutDownQuest(anyBoolean());
	}

	@Test
	public void browsingAnotherQuestPreservesTheActiveQuestWithoutStartingIt()
	{
		QuestHelper active = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		BasicQuestHelper browsed = selectedBasicHelper(QuestHelperQuest.DRAGON_SLAYER_I);
		QuestStep first = objectiveStep("Prepare for the dragon");
		QuestStep second = objectiveStep("Defeat the dragon");
		Requirement secondStepActive = mock(Requirement.class);
		ConditionalStep progression = mock(ConditionalStep.class);
		LinkedHashMap<Requirement, QuestStep> branches = new LinkedHashMap<>();
		branches.put(null, first);
		branches.put(secondStepActive, second);
		when(progression.getStepsMap()).thenReturn(branches);
		when(secondStepActive.check(client)).thenReturn(true);
		when(browsed.getStepList()).thenReturn(Collections.singletonMap(1, progression));
		when(browsed.getVar()).thenReturn(1);
		when(browsed.getPanels()).thenReturn(Collections.singletonList(
			new PanelDetails("Dragon", List.of(first, second), Collections.emptyList())));
		when(first.containsSteps(eq(second), anySet())).thenReturn(false);
		when(second.containsSteps(eq(second), anySet())).thenReturn(true);
		Map<QuestHelper, QuestState> states = new LinkedHashMap<>();
		states.put(active, QuestState.IN_PROGRESS);
		states.put(browsed, QuestState.IN_PROGRESS);
		Map<QuestHelper, QuestState> state = states;
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(4L);
		when(questManager.getSelectedQuest()).thenReturn(active);

		openAndRefresh(80);
		clearInvocations(browsed, questManager);

		assertTrue(dataSource.browseQuest(QuestHelperQuest.DRAGON_SLAYER_I.name()));
		dataSource.afterQuestUpdate(81);

		JournalSnapshot snapshot = dataSource.getSnapshot();
		assertEquals(
			QuestHelperQuest.DRAGON_SLAYER_I.name(),
			snapshot.getSelectedQuest().getOverview().getId());
		assertNotNull(snapshot.getActiveQuest());
		assertEquals(QuestHelperQuest.COOKS_ASSISTANT.name(), snapshot.getActiveQuest().getId());
		assertEquals(2, snapshot.getSelectedQuest().getObjectives().size());
		assertEquals(
			JournalSnapshot.ObjectiveState.COMPLETE,
			snapshot.getSelectedQuest().getObjectives().get(0).getState());
		assertFalse(snapshot.getSelectedQuest().getObjectives().get(0).isCurrent());
		assertEquals(
			JournalSnapshot.ObjectiveState.AVAILABLE,
			snapshot.getSelectedQuest().getObjectives().get(1).getState());
		assertFalse(snapshot.getSelectedQuest().getObjectives().get(1).isCurrent());
		verify(browsed).initializeRequirements();
		verify(browsed).init();
		verify(browsed, never()).getCurrentStep();
		verify(browsed, never()).startUp(any(QuestHelperConfig.class));
		verify(browsed, never()).updateQuest();
		verify(browsed, never()).setSelectedStateOverride(any());
		verify(browsed, never()).shutDown();
		verify(progression, never()).getActiveStep();
		verify(progression, never()).startUp();
		verify(progression, never()).shutDown();
		verify(first, never()).isLocked();
		verify(second, never()).isLocked();
		verify(questManager, never()).startUpQuest(any(QuestHelper.class), any(Boolean.class));
	}

	@Test
	public void activeQuestStateStaysFreshWhileBrowsingAnotherQuest()
	{
		QuestHelper active = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		QuestHelper browsed = selectedHelper(QuestHelperQuest.DRAGON_SLAYER_I);
		AtomicReference<QuestState> activeState =
			new AtomicReference<>(QuestState.NOT_STARTED);
		when(active.getState(client)).thenAnswer(invocation -> activeState.get());
		Map<QuestHelper, QuestState> states = new LinkedHashMap<>();
		states.put(active, QuestState.NOT_STARTED);
		states.put(browsed, QuestState.NOT_STARTED);
		when(questManager.getQuestListState()).thenReturn(states);
		when(questManager.getSelectedQuestRevision()).thenReturn(40L);
		when(questManager.getSelectedQuest()).thenReturn(active);

		openAndRefresh(800);
		assertTrue(dataSource.browseQuest(QuestHelperQuest.DRAGON_SLAYER_I.name()));
		dataSource.afterQuestUpdate(801);

		activeState.set(QuestState.FINISHED);
		dataSource.afterQuestUpdate(802);

		JournalSnapshot snapshot = dataSource.getSnapshot();
		JournalSnapshot.QuestListItem activeItem = snapshot.getQuests().stream()
			.filter(item -> QuestHelperQuest.COOKS_ASSISTANT.name().equals(item.getId()))
			.findFirst()
			.orElseThrow(AssertionError::new);
		assertEquals(JournalSnapshot.QuestState.COMPLETE, activeItem.getState());
		assertEquals(
			JournalSnapshot.QuestState.COMPLETE,
			snapshot.getActiveQuest().getState());
		assertEquals(
			QuestHelperQuest.DRAGON_SLAYER_I.name(),
			snapshot.getSelectedQuest().getOverview().getId());
	}

	@Test
	public void clearingTheBrowsedQuestKeepsActiveGuidanceAndSurvivesARefresh()
	{
		QuestHelper active = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> state = state(active, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(41L);
		when(questManager.getSelectedQuest()).thenReturn(active);

		openAndRefresh(810);
		assertNotNull(dataSource.getSnapshot().getSelectedQuest());
		assertFalse(dataSource.clearBrowsedQuest(QuestHelperQuest.DRAGON_SLAYER_I.name()));

		assertTrue(dataSource.clearBrowsedQuest(QuestHelperQuest.COOKS_ASSISTANT.name()));
		assertNull(dataSource.getSnapshot().getSelectedQuest());
		assertEquals(
			QuestHelperQuest.COOKS_ASSISTANT.name(),
			dataSource.getSnapshot().getActiveQuest().getId());

		dataSource.invalidate();
		dataSource.afterQuestUpdate(811);

		assertNull(dataSource.getSnapshot().getSelectedQuest());
		assertEquals(
			QuestHelperQuest.COOKS_ASSISTANT.name(),
			dataSource.getSnapshot().getActiveQuest().getId());
		verify(questManager, never()).shutDownQuest(anyBoolean());
	}

	@Test
	public void browsingAnInactiveComplexStateQuestResolvesItsCurrentBranchReadOnly()
	{
		QuestHelper active = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		ComplexStateQuestHelper browsed = selectedComplexHelper(QuestHelperQuest.ELEMENTAL_WORKSHOP_I);
		QuestStep first = objectiveStep("Enter the workshop");
		QuestStep second = objectiveStep("Repair the bellows");
		Requirement secondStepActive = mock(Requirement.class);
		ConditionalStep progression = mock(ConditionalStep.class);
		LinkedHashMap<Requirement, QuestStep> branches = new LinkedHashMap<>();
		branches.put(null, first);
		branches.put(secondStepActive, second);
		when(progression.getStepsMap()).thenReturn(branches);
		when(secondStepActive.check(client)).thenReturn(true);
		when(browsed.getStep()).thenReturn(progression);
		when(browsed.getPanels()).thenReturn(Collections.singletonList(
			new PanelDetails("Workshop", List.of(first, second), Collections.emptyList())));
		Map<QuestHelper, QuestState> states = new LinkedHashMap<>();
		states.put(active, QuestState.IN_PROGRESS);
		states.put(browsed, QuestState.IN_PROGRESS);
		Map<QuestHelper, QuestState> state = states;
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(5L);
		when(questManager.getSelectedQuest()).thenReturn(active);

		openAndRefresh(90);
		clearInvocations(browsed, questManager);
		assertTrue(dataSource.browseQuest(QuestHelperQuest.ELEMENTAL_WORKSHOP_I.name()));
		dataSource.afterQuestUpdate(91);

		List<JournalSnapshot.Objective> objectives =
			dataSource.getSnapshot().getSelectedQuest().getObjectives();
		assertEquals(JournalSnapshot.ObjectiveState.COMPLETE, objectives.get(0).getState());
		assertFalse(objectives.get(0).isCurrent());
		assertFalse(objectives.get(1).isCurrent());
		verify(browsed, never()).getCurrentStep();
		verify(browsed, never()).startUp(any(QuestHelperConfig.class));
		verify(browsed, never()).updateQuest();
		verify(browsed, never()).shutDown();
		verify(progression, never()).getActiveStep();
		verify(questManager, never()).startUpQuest(any(QuestHelper.class), any(Boolean.class));
	}

	@Test
	public void filteredSnapshotKeepsAnActiveQuestHiddenByTheListFilter()
	{
		QuestHelper active = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		QuestHelper browsed = selectedHelper(QuestHelperQuest.DRAGON_SLAYER_I);
		Map<QuestHelper, QuestState> states = new LinkedHashMap<>();
		states.put(active, QuestState.IN_PROGRESS);
		states.put(browsed, QuestState.NOT_STARTED);
		Map<QuestHelper, QuestState> state = states;
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(5L);
		when(questManager.getSelectedQuest()).thenReturn(active);

		openAndRefresh(82);
		JournalSnapshot filtered = dataSource.getSnapshot(
			JournalSnapshot.QuestFilter.all().withSearchText("Dragon Slayer"));

		assertEquals(1, filtered.getQuests().size());
		assertEquals(QuestHelperQuest.DRAGON_SLAYER_I.name(), filtered.getQuests().get(0).getId());
		assertNotNull(filtered.getActiveQuest());
		assertEquals(QuestHelperQuest.COOKS_ASSISTANT.name(), filtered.getActiveQuest().getId());
	}

	@Test
	public void externalActiveQuestChangePreservesASeparatelyBrowsedQuest()
	{
		QuestHelper firstActive = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		QuestHelper browsed = selectedHelper(QuestHelperQuest.DRAGON_SLAYER_I);
		QuestHelper nextActive = selectedHelper(QuestHelperQuest.SHEEP_SHEARER);
		Map<QuestHelper, QuestState> states = new LinkedHashMap<>();
		states.put(firstActive, QuestState.IN_PROGRESS);
		states.put(browsed, QuestState.NOT_STARTED);
		states.put(nextActive, QuestState.IN_PROGRESS);
		Map<QuestHelper, QuestState> state = states;
		AtomicLong activeRevision = new AtomicLong(6L);
		AtomicReference<QuestHelper> authoritativeActive = new AtomicReference<>(firstActive);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenAnswer(
			invocation -> activeRevision.get());
		when(questManager.getSelectedQuest()).thenAnswer(
			invocation -> authoritativeActive.get());

		openAndRefresh(83);
		assertTrue(dataSource.browseQuest(QuestHelperQuest.DRAGON_SLAYER_I.name()));
		dataSource.afterQuestUpdate(84);

		authoritativeActive.set(nextActive);
		activeRevision.incrementAndGet();
		dataSource.afterQuestUpdate(85);

		JournalSnapshot snapshot = dataSource.getSnapshot();
		assertEquals(
			QuestHelperQuest.DRAGON_SLAYER_I.name(),
			snapshot.getSelectedQuest().getOverview().getId());
		assertEquals(QuestHelperQuest.SHEEP_SHEARER.name(), snapshot.getActiveQuest().getId());
	}

	@Test
	public void clearingTheActiveQuestRetainsItsPreview()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		AtomicLong activeRevision = new AtomicLong(7L);
		AtomicReference<QuestHelper> authoritativeActive = new AtomicReference<>(helper);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenAnswer(
			invocation -> activeRevision.get());
		when(questManager.getSelectedQuest()).thenAnswer(
			invocation -> authoritativeActive.get());

		openAndRefresh(86);
		authoritativeActive.set(null);
		activeRevision.incrementAndGet();
		dataSource.afterQuestUpdate(87);

		assertNull(dataSource.getSnapshot().getActiveQuest());
		assertEquals(
			QuestHelperQuest.COOKS_ASSISTANT.name(),
			dataSource.getSnapshot().getSelectedQuest().getOverview().getId());
	}

	@Test
	public void stoppingTheActiveQuestClearsGuidanceButRetainsItsPreview()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		AtomicLong activeRevision = new AtomicLong(8L);
		AtomicReference<QuestHelper> authoritativeActive = new AtomicReference<>(helper);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenAnswer(
			invocation -> activeRevision.get());
		when(questManager.getSelectedQuest()).thenAnswer(
			invocation -> authoritativeActive.get());
		doAnswer(invocation ->
		{
			authoritativeActive.set(null);
			activeRevision.incrementAndGet();
			return null;
		}).when(questManager).shutDownQuest(true);

		openAndRefresh(88);
		assertTrue(dataSource.stopActiveQuest(QuestHelperQuest.COOKS_ASSISTANT.name()));
		dataSource.afterQuestUpdate(89);

		assertNull(dataSource.getSnapshot().getActiveQuest());
		assertEquals(
			QuestHelperQuest.COOKS_ASSISTANT.name(),
			dataSource.getSnapshot().getSelectedQuest().getOverview().getId());
	}

	@Test
	public void returningToTheActiveQuestMakesFutureActiveChangesFollowAgain()
	{
		QuestHelper firstActive = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		QuestHelper browsed = selectedHelper(QuestHelperQuest.DRAGON_SLAYER_I);
		Map<QuestHelper, QuestState> states = new LinkedHashMap<>();
		states.put(firstActive, QuestState.IN_PROGRESS);
		states.put(browsed, QuestState.IN_PROGRESS);
		Map<QuestHelper, QuestState> state = states;
		AtomicLong activeRevision = new AtomicLong(8L);
		AtomicReference<QuestHelper> authoritativeActive = new AtomicReference<>(firstActive);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenAnswer(
			invocation -> activeRevision.get());
		when(questManager.getSelectedQuest()).thenAnswer(
			invocation -> authoritativeActive.get());

		openAndRefresh(88);
		assertTrue(dataSource.browseQuest(QuestHelperQuest.DRAGON_SLAYER_I.name()));
		dataSource.afterQuestUpdate(89);
		assertTrue(dataSource.browseActiveQuest());
		dataSource.afterQuestUpdate(90);

		authoritativeActive.set(browsed);
		activeRevision.incrementAndGet();
		dataSource.afterQuestUpdate(91);

		assertEquals(
			QuestHelperQuest.DRAGON_SLAYER_I.name(),
			dataSource.getSnapshot().getSelectedQuest().getOverview().getId());
		assertEquals(QuestHelperQuest.DRAGON_SLAYER_I.name(), dataSource.getSnapshot().getActiveQuest().getId());
	}

	@Test
	public void taggedQuestTitleResolvesCaseInsensitivelyWithoutEvaluatingState()
	{
		QuestHelper helper = helper(QuestHelperQuest.COOKS_ASSISTANT);
		when(helper.getState(any(Client.class))).thenThrow(
			new AssertionError("Title lookup must not evaluate quest state"));
		Map<QuestHelper, QuestState> state = state(helper, QuestState.NOT_STARTED);
		when(questManager.getQuestListState()).thenReturn(state);

		assertEquals(
			QuestHelperQuest.COOKS_ASSISTANT.name(),
			dataSource.findQuestIdByTitle("  <col=ff9040>cOoK's AsSiStAnT</col>  "));

		verify(helper, never()).getState(any(Client.class));
	}

	@Test
	public void nativeRecipeForDisasterTitleUsesResolvedEligibleHelper()
	{
		QuestHelper helper = helper(QuestHelperQuest.RECIPE_FOR_DISASTER_START);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.NOT_STARTED);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questMenuHandler.resolveQuestHelper("Recipe for Disaster"))
			.thenReturn(helper);

		assertEquals(
			QuestHelperQuest.RECIPE_FOR_DISASTER_START.name(),
			dataSource.findQuestIdByTitle("Recipe for Disaster"));
	}

	@Test
	public void nativeResolverCannotSelectIneligibleHelper()
	{
		QuestHelper eligible = helper(QuestHelperQuest.COOKS_ASSISTANT);
		QuestHelper ineligible = helper(QuestHelperQuest.RECIPE_FOR_DISASTER_START);
		when(questManager.getQuestListState()).thenReturn(
			state(eligible, QuestState.NOT_STARTED));
		when(questMenuHandler.resolveQuestHelper("Recipe for Disaster"))
			.thenReturn(ineligible);

		assertNull(dataSource.findQuestIdByTitle("Recipe for Disaster"));
	}

	@Test
	public void unavailableOrBlankTitleDoesNotResolve()
	{
		when(questManager.getQuestListState()).thenReturn(Collections.emptyMap());

		assertNull(dataSource.findQuestIdByTitle("Cook's Assistant"));
		assertNull(dataSource.findQuestIdByTitle("  "));
		assertNull(dataSource.findQuestIdByTitle(null));
	}

	@Test
	public void unknownTitleDoesNotResolve()
	{
		QuestHelper helper = helper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.NOT_STARTED);
		when(questManager.getQuestListState()).thenReturn(state);

		assertNull(dataSource.findQuestIdByTitle("Not a real quest"));
		verify(helper, never()).getState(any(Client.class));
	}

	@Test
	public void leagueIneligibleQuestTitleIsExcluded()
	{
		QuestHelper eligible = helper(QuestHelperQuest.COOKS_ASSISTANT);
		QuestHelper ineligible = helper(QuestHelperQuest.WITCHS_HOUSE);
		Map<QuestHelper, QuestState> state = new LinkedHashMap<>();
		state.put(eligible, QuestState.NOT_STARTED);
		state.put(ineligible, QuestState.NOT_STARTED);
		LeagueFiltering.setSelectedRegions(EnumSet.of(LeagueRegion.MISTHALIN));
		try
		{
			when(questManager.getQuestListState()).thenReturn(state);
			assertNull(dataSource.findQuestIdByTitle("Witch's House"));
			verify(eligible, never()).getState(any(Client.class));
			verify(ineligible, never()).getState(any(Client.class));
		}
		finally
		{
			LeagueFiltering.setSelectedRegions(null);
		}
	}

	@Test
	public void hideQuestRewardsChangesOnlyTheSelectedViewWhenMarkedDirty()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		when(helper.getQuestRewards()).thenReturn(
			Collections.singletonList(new UnlockReward("A test shortcut")));
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		AtomicBoolean hideRewards = new AtomicBoolean();
		when(config.hideQuestRewards()).thenAnswer(invocation -> hideRewards.get());
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(5L);
		when(questManager.getSelectedQuest()).thenReturn(helper);

		openAndRefresh(40);
		JournalSnapshot first = dataSource.getSnapshot();
		assertEquals(1, first.getSelectedQuest().getRewards().size());

		hideRewards.set(true);
		dataSource.markViewedQuestDirty();
		dataSource.afterQuestUpdate(41);
		JournalSnapshot hidden = dataSource.getSnapshot();

		assertTrue(hidden.getSelectedQuest().getRewards().isEmpty());
		assertNotSame(first, hidden);
		assertEquals(1, questViewMapper.definitionBuilds);
		assertEquals(2, questViewMapper.viewEvaluations);
	}

	@Test
	public void detailOnlyRefreshReusesFilteredQuestWork()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		when(helper.getQuestRewards()).thenReturn(
			Collections.singletonList(new UnlockReward("A test shortcut")));
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		AtomicBoolean hideRewards = new AtomicBoolean();
		when(config.hideQuestRewards()).thenAnswer(invocation -> hideRewards.get());
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(5L);
		when(questManager.getSelectedQuest()).thenReturn(helper);
		JournalSnapshot.QuestFilter filter = spy(
			JournalSnapshot.QuestFilter.all().withSearchText("Cook"));

		openAndRefresh(40);
		JournalSnapshot filtered = dataSource.getSnapshot(filter);
		assertEquals(1, filtered.getSelectedQuest().getRewards().size());
		clearInvocations(filter);

		hideRewards.set(true);
		dataSource.markViewedQuestDirty();
		dataSource.afterQuestUpdate(41);
		JournalSnapshot refreshed = dataSource.getSnapshot(filter);

		assertNotSame(filtered, refreshed);
		assertTrue(refreshed.getSelectedQuest().getRewards().isEmpty());
		assertEquals(filtered.getQuests(), refreshed.getQuests());
		verify(filter, never()).matches(any(JournalSnapshot.QuestListItem.class));
		verify(filter, never()).comparator();
	}

	@Test
	public void staticRewardsAreMappedOncePerSelectedDefinition()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		UnlockReward reward = spy(new UnlockReward("A test shortcut"));
		when(helper.getQuestRewards()).thenReturn(Collections.singletonList(reward));
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(11L);
		when(questManager.getSelectedQuest()).thenReturn(helper);

		openAndRefresh(70);
		dataSource.markViewedQuestDirty();
		dataSource.afterQuestUpdate(71);

		assertEquals(2, questViewMapper.viewEvaluations);
		verify(reward).getDisplayText();
		verify(reward, never()).rewardType();
	}

	@Test
	public void shutdownClearsRetainedStateAndStopsFurtherWork()
	{
		QuestHelper helper = selectedHelper(QuestHelperQuest.COOKS_ASSISTANT);
		Map<QuestHelper, QuestState> state = state(helper, QuestState.IN_PROGRESS);
		when(questManager.getQuestListState()).thenReturn(state);
		when(questManager.getSelectedQuestRevision()).thenReturn(2L);
		when(questManager.getSelectedQuest()).thenReturn(helper);
		openAndRefresh(50);
		assertFalse(dataSource.getSnapshot().getQuests().isEmpty());

		dataSource.shutDown();
		JournalSnapshot cleared = dataSource.getSnapshot();
		int evaluationsBeforeStoppedTick = questViewMapper.viewEvaluations;
		clearInvocations(questManager);
		dataSource.afterQuestUpdate(51);

		assertTrue(cleared.getQuests().isEmpty());
		assertNull(cleared.getSelectedQuest());
		assertEquals(evaluationsBeforeStoppedTick, questViewMapper.viewEvaluations);
		verifyNoInteractions(questManager);
	}

	private void openAndRefresh(int tick)
	{
		dataSource.startUp();
		dataSource.setOpen(true);
		dataSource.afterQuestUpdate(tick);
	}

	private void assertNoBuildWork()
	{
		assertEquals(0, catalogMapper.catalogBuilds);
		assertEquals(0, questViewMapper.definitionBuilds);
		assertEquals(0, questViewMapper.viewEvaluations);
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
		when(helper.getState(client)).thenReturn(QuestState.IN_PROGRESS);
		return helper;
	}

	private BasicQuestHelper selectedBasicHelper(QuestHelperQuest quest)
	{
		BasicQuestHelper helper = mock(BasicQuestHelper.class);
		when(helper.getQuest()).thenReturn(quest);
		when(helper.getDisplayedQuestName()).thenReturn(quest.getName());
		when(helper.getSelectedStateOverride()).thenReturn(null);
		when(helper.getPanels()).thenReturn(Collections.emptyList());
		when(helper.getGeneralRequirements()).thenReturn(Collections.emptyList());
		when(helper.getItemRequirements()).thenReturn(Collections.emptyList());
		when(helper.getGeneralRecommended()).thenReturn(Collections.emptyList());
		when(helper.getItemRecommended()).thenReturn(Collections.emptyList());
		when(helper.getCombatRequirements()).thenReturn(Collections.emptyList());
		when(helper.getQuestRewards()).thenReturn(Collections.emptyList());
		when(helper.getNotes()).thenReturn(Collections.emptyList());
		when(helper.getState(client)).thenReturn(QuestState.IN_PROGRESS);
		return helper;
	}

	private ComplexStateQuestHelper selectedComplexHelper(QuestHelperQuest quest)
	{
		ComplexStateQuestHelper helper = mock(ComplexStateQuestHelper.class);
		when(helper.getQuest()).thenReturn(quest);
		when(helper.getDisplayedQuestName()).thenReturn(quest.getName());
		when(helper.getPanels()).thenReturn(Collections.emptyList());
		when(helper.getGeneralRequirements()).thenReturn(Collections.emptyList());
		when(helper.getItemRequirements()).thenReturn(Collections.emptyList());
		when(helper.getGeneralRecommended()).thenReturn(Collections.emptyList());
		when(helper.getItemRecommended()).thenReturn(Collections.emptyList());
		when(helper.getCombatRequirements()).thenReturn(Collections.emptyList());
		when(helper.getQuestRewards()).thenReturn(Collections.emptyList());
		when(helper.getNotes()).thenReturn(Collections.emptyList());
		when(helper.getState(client)).thenReturn(QuestState.IN_PROGRESS);
		return helper;
	}

	private static QuestStep objectiveStep(String text)
	{
		QuestStep step = mock(QuestStep.class);
		when(step.isShowInSidebar()).thenReturn(true);
		when(step.getText()).thenReturn(Collections.singletonList(text));
		return step;
	}

	private static Map<QuestHelper, QuestState> state(
		QuestHelper helper,
		QuestState state)
	{
		Map<QuestHelper, QuestState> states = new LinkedHashMap<>();
		states.put(helper, state);
		return states;
	}

	private static final class CountingQuestViewMapper
		extends QuestViewMapper
	{
		private int definitionBuilds;
		private int stepResolutions;
		private int viewEvaluations;
		private int remainingDefinitionFailures;
		private int remainingDefinitionLinkageFailures;
		private int remainingResolutionFailures;
		private int remainingResolutionLinkageFailures;
		private int remainingEvaluationFailures;
		private int remainingEvaluationLinkageFailures;
		private boolean fatalDefinitionFailure;

		private CountingQuestViewMapper(
			Client client,
			QuestHelperConfig config,
			RequirementMapper requirementMapper)
		{
			super(client, config, requirementMapper);
		}

		@Override
		Definition buildDefinition(QuestHelper helper)
		{
			definitionBuilds++;
			if (fatalDefinitionFailure)
			{
				throw new OutOfMemoryError("fatal definition failure");
			}
			if (remainingDefinitionLinkageFailures > 0)
			{
				remainingDefinitionLinkageFailures--;
				throw new NoClassDefFoundError("missing definition dependency");
			}
			if (remainingDefinitionFailures > 0)
			{
				remainingDefinitionFailures--;
				throw new IllegalStateException("definition unavailable");
			}
			return super.buildDefinition(helper);
		}

		@Override
		StepSelection resolveSteps(QuestHelper helper, boolean active)
		{
			stepResolutions++;
			if (remainingResolutionLinkageFailures > 0)
			{
				remainingResolutionLinkageFailures--;
				throw new NoClassDefFoundError("missing step dependency");
			}
			if (remainingResolutionFailures > 0)
			{
				remainingResolutionFailures--;
				throw new IllegalStateException("step resolution unavailable");
			}
			return super.resolveSteps(helper, active);
		}

		@Override
		JournalSnapshot.SelectedQuest evaluate(
			Definition definition,
			QuestStep activeRoot,
			QuestStep activeStep,
			boolean inheritActivePathRequirements)
		{
			viewEvaluations++;
			if (remainingEvaluationLinkageFailures > 0)
			{
				remainingEvaluationLinkageFailures--;
				throw new NoClassDefFoundError("missing evaluation dependency");
			}
			if (remainingEvaluationFailures > 0)
			{
				remainingEvaluationFailures--;
				throw new IllegalStateException("selected evaluation unavailable");
			}
			return super.evaluate(
				definition,
				activeRoot,
				activeStep,
				inheritActivePathRequirements);
		}
	}

	private static final class CountingCatalogMapper extends CatalogMapper
	{
		private int catalogBuilds;
		private int remainingCatalogLinkageFailures;
		private int remainingActiveViewLinkageFailures;

		private CountingCatalogMapper(Client client)
		{
			super(client);
		}

		@Override
		CatalogData buildCatalog(Map<QuestHelper, QuestState> states)
		{
			catalogBuilds++;
			if (remainingCatalogLinkageFailures > 0)
			{
				remainingCatalogLinkageFailures--;
				throw new NoClassDefFoundError("missing catalog dependency");
			}
			return super.buildCatalog(states);
		}

		@Override
		JournalSnapshot.ActiveQuest buildActiveQuest(
			QuestHelper helper,
			List<JournalSnapshot.QuestListItem> catalog,
			Map<QuestHelper, QuestState> catalogState)
		{
			if (remainingActiveViewLinkageFailures > 0)
			{
				remainingActiveViewLinkageFailures--;
				throw new NoClassDefFoundError("missing active-view dependency");
			}
			return super.buildActiveQuest(helper, catalog, catalogState);
		}
	}
}
