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

import com.questhelper.managers.QuestManager;
import com.questhelper.managers.QuestMenuHandler;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import static com.questhelper.questjournal.CatalogMapper.normalizeText;

/** Builds immutable journal snapshots from Quest Helper state on the client thread. */
@Singleton
@Slf4j
public class JournalDataSource
{
	static final int VIEWED_QUEST_REFRESH_FALLBACK_TICKS = 5;
	static final int FAILURE_RETRY_MAX_TICKS = 16;

	private static final JournalSnapshot.QuestProgress EMPTY_PROGRESS =
		new JournalSnapshot.QuestProgress(0, 0, 0, 0);

	private final QuestManager questManager;
	private final QuestMenuHandler questMenuHandler;
	private final CatalogMapper catalogMapper;
	private final QuestViewMapper questViewMapper;

	private boolean started;
	private boolean open;
	private Map<QuestHelper, net.runelite.api.QuestState> catalogState;
	private List<JournalSnapshot.QuestListItem> catalog = Collections.emptyList();
	private Map<String, QuestHelper> helpersById = Collections.emptyMap();
	private JournalSnapshot.QuestProgress progress = EMPTY_PROGRESS;
	private JournalSnapshot.QuestListOptions listOptions = JournalSnapshot.QuestListOptions.defaults();
	private JournalSnapshot.QuestMembership configuredMembership;
	private JournalSnapshot.QuestOrder configuredOrder;
	private int completedQuestCount;
	private int totalQuestCount;
	private int fallbackTotalQuestPoints;
	private final RetryTracker catalogRetry = new RetryTracker("catalog mapping");

	private long activeQuestRevision = Long.MIN_VALUE;
	private QuestHelper activeHelper;
	private JournalSnapshot.ActiveQuest activeQuest;
	private boolean activeQuestRefreshPending;
	private final RetryTracker activeQuestRetry = new RetryTracker("active view mapping");
	private QuestHelper viewedHelper;
	private String viewedQuestId;
	private boolean viewedQuestFollowsActive = true;
	private QuestViewMapper.Definition viewedDefinition;
	private boolean viewedDefinitionRefreshPending;
	private JournalSnapshot.SelectedQuest viewedEvaluation;
	private boolean viewedEvaluationRefreshPending;
	private QuestStep observedActiveStep;
	private QuestStep observedActiveRoot;
	private int lastViewedEvaluationTick = Integer.MIN_VALUE;
	private final RetryTracker activeStateRetry = new RetryTracker("active-state refresh");
	private final RetryTracker definitionRetry = new RetryTracker("definition mapping");
	private final RetryTracker evaluationRetry =
		new RetryTracker("viewed-quest evaluation");
	private boolean sourceSnapshotRefreshPending;

	private JournalSnapshot sourceSnapshot = new JournalSnapshot(
		Collections.emptyList(),
		null,
		null,
		JournalSnapshot.QuestListOptions.defaults(),
		EMPTY_PROGRESS);
	private JournalSnapshot.QuestFilter filteredFilter;
	private JournalSnapshot filteredSource;
	private JournalSnapshot filteredSnapshot;
	private List<JournalSnapshot.QuestListItem> filteredCatalog;

	@Inject
	public JournalDataSource(
		QuestManager questManager,
		QuestMenuHandler questMenuHandler,
		CatalogMapper catalogMapper,
		QuestViewMapper questViewMapper)
	{
		this.questManager = Objects.requireNonNull(questManager, "questManager");
		this.questMenuHandler = Objects.requireNonNull(
			questMenuHandler,
			"questMenuHandler");
		this.catalogMapper = Objects.requireNonNull(catalogMapper, "catalogMapper");
		this.questViewMapper = Objects.requireNonNull(questViewMapper, "questViewMapper");
	}

	public void startUp()
	{
		started = true;
	}

	public void shutDown()
	{
		started = false;
		open = false;
		catalogState = null;
		catalog = Collections.emptyList();
		helpersById = Collections.emptyMap();
		progress = EMPTY_PROGRESS;
		listOptions = JournalSnapshot.QuestListOptions.defaults();
		configuredMembership = null;
		configuredOrder = null;
		completedQuestCount = 0;
		totalQuestCount = 0;
		fallbackTotalQuestPoints = 0;
		catalogRetry.reset();
		activeQuestRevision = Long.MIN_VALUE;
		activeHelper = null;
		activeQuest = null;
		activeQuestRefreshPending = false;
		activeQuestRetry.reset();
		viewedHelper = null;
		viewedQuestId = null;
		viewedQuestFollowsActive = true;
		viewedDefinition = null;
		viewedDefinitionRefreshPending = false;
		viewedEvaluation = null;
		viewedEvaluationRefreshPending = false;
		observedActiveStep = null;
		observedActiveRoot = null;
		lastViewedEvaluationTick = Integer.MIN_VALUE;
		activeStateRetry.reset();
		definitionRetry.reset();
		evaluationRetry.reset();
		sourceSnapshotRefreshPending = false;
		sourceSnapshot = new JournalSnapshot(
			Collections.emptyList(),
			null,
			null,
			listOptions,
			progress);
		filteredFilter = null;
		filteredSource = null;
		filteredSnapshot = null;
		filteredCatalog = null;
	}

	public void setOpen(boolean open)
	{
		if (this.open == open)
		{
			return;
		}
		this.open = open;
		if (open)
		{
			viewedEvaluationRefreshPending = true;
		}
	}

	/**
	 * Refreshes journal state after Quest Manager updates. Viewed details refresh
	 * when dirty, when the active step changes, or on the fallback interval.
	 */
	public void afterQuestUpdate(int tick)
	{
		if (!started || !open)
		{
			return;
		}

		boolean catalogChanged = refreshCatalog(tick);
		boolean changed = catalogChanged || sourceSnapshotRefreshPending;
		boolean activeQuestDataChanged = catalogChanged;

		if (refreshListOptions())
		{
			changed = true;
		}

		long nextActiveRevision = questManager.getSelectedQuestRevision();
		QuestHelper nextActiveHelper = questManager.getSelectedQuest();
		if (nextActiveRevision != activeQuestRevision || nextActiveHelper != activeHelper)
		{
			QuestHelper previousActiveHelper = activeHelper;
			boolean activeHelperChanged = nextActiveHelper != previousActiveHelper;
			activeQuestRevision = nextActiveRevision;
			activeHelper = nextActiveHelper;
			activeStateRetry.reset();
			activeQuestRetry.reset();
			if (activeHelperChanged)
			{
				activeQuest = null;
			}
			if (nextActiveHelper != null
				&& (viewedQuestFollowsActive
					|| viewedHelper == nextActiveHelper))
			{
				setViewedHelper(nextActiveHelper, true, true);
			}
			else if (nextActiveHelper == null && viewedHelper == previousActiveHelper)
			{
				viewedEvaluationRefreshPending = true;
				observedActiveStep = null;
				observedActiveRoot = null;
			}
			changed = true;
			activeQuestDataChanged = true;
		}

		if (activeHelper != null
			&& activeHelper != viewedHelper
			&& refreshActiveCatalogState(tick))
		{
			changed = true;
			activeQuestDataChanged = true;
		}

		boolean definitionRebuilt = refreshViewedDefinition(tick);

		if (viewedDefinition != null
			&& !viewedDefinitionRefreshPending
			&& evaluationRetry.isDue(tick, viewedHelper))
		{
			try
			{
				QuestViewMapper.StepSelection viewedSteps =
					questViewMapper.resolveSteps(
						viewedHelper,
						viewedHelper == activeHelper);
				QuestStep viewedActiveStep = viewedSteps.getLeaf();
				boolean activeStepChanged = viewedActiveStep != observedActiveStep
					|| viewedSteps.getRoot() != observedActiveRoot;
				boolean fallbackRefresh = fallbackRefreshDue(tick);
				if ((viewedEvaluationRefreshPending || activeStepChanged || fallbackRefresh)
					&& (tick != lastViewedEvaluationTick
						|| viewedEvaluationRefreshPending
						|| activeStepChanged))
				{
					JournalSnapshot.SelectedQuest nextEvaluation =
						questViewMapper.evaluate(
							viewedDefinition,
							viewedSteps.getRoot(),
							viewedSteps.getLeaf(),
							viewedHelper == activeHelper);
					evaluationRetry.succeeded(viewedHelper);
					viewedEvaluationRefreshPending = false;
					observedActiveStep = viewedActiveStep;
					observedActiveRoot = viewedSteps.getRoot();
					lastViewedEvaluationTick = tick;
					if (updateCatalogState(
						viewedDefinition.helper,
						nextEvaluation.getOverview().getState()))
					{
						changed = true;
						if (viewedDefinition.helper == activeHelper)
						{
							activeQuestDataChanged = true;
						}
					}
					if (definitionRebuilt
						|| viewedEvaluation == null
						|| !viewedEvaluation.equals(nextEvaluation))
					{
						viewedEvaluation = nextEvaluation;
						changed = true;
					}
				}
			}
			catch (RuntimeException | LinkageError failure)
			{
				evaluationRetry.failed(tick, viewedHelper, failure);
			}
		}
		else if (viewedHelper == null && viewedEvaluation != null)
		{
			viewedEvaluation = null;
			changed = true;
		}

		if (refreshDynamicProgress())
		{
			changed = true;
		}

		if (activeQuestDataChanged)
		{
			activeQuestRefreshPending = true;
		}
		if (refreshActiveQuest(tick))
		{
			changed = true;
		}
		if (changed)
		{
			publishSourceSnapshot();
		}
	}

	public JournalSnapshot getSnapshot()
	{
		return sourceSnapshot;
	}

	String getViewedQuestId()
	{
		return viewedQuestId;
	}

	public JournalSnapshot getSnapshot(JournalSnapshot.QuestFilter filter)
	{
		Objects.requireNonNull(filter, "filter");
		if (filter == JournalSnapshot.QuestFilter.all())
		{
			return sourceSnapshot;
		}
		if (filter == filteredFilter && sourceSnapshot == filteredSource)
		{
			return filteredSnapshot;
		}

		List<JournalSnapshot.QuestListItem> visible;
		if (filter == filteredFilter && catalog == filteredCatalog && filteredSnapshot != null)
		{
			visible = filteredSnapshot.getQuests();
		}
		else
		{
			visible = sourceSnapshot.getQuests().stream()
				.filter(filter::matches)
				.sorted(filter.comparator())
				.collect(Collectors.toList());
		}
		filteredFilter = filter;
		filteredSource = sourceSnapshot;
		filteredCatalog = catalog;
		filteredSnapshot = new JournalSnapshot(
			visible,
			sourceSnapshot.getSelectedQuest(),
			sourceSnapshot.getActiveQuest(),
			sourceSnapshot.getListOptions(),
			sourceSnapshot.getQuestProgress());
		return filteredSnapshot;
	}

	/** Resolves native quest-list menu text without evaluating quest state or details. */
	public String findQuestIdByTitle(String menuTarget)
	{
		String requestedTitle = normalizeText(menuTarget);
		if (requestedTitle.isEmpty())
		{
			return null;
		}

		Map<QuestHelper, net.runelite.api.QuestState> state =
			questManager.getQuestListState();
		if (state.isEmpty())
		{
			return null;
		}

		List<QuestHelper> eligibleHelpers =
			CatalogMapper.eligibleHelpers(state);
		QuestHelper resolvedHelper =
			questMenuHandler.resolveQuestHelper(requestedTitle);
		if (resolvedHelper != null && resolvedHelper.getQuest() != null)
		{
			for (QuestHelper helper : eligibleHelpers)
			{
				if (helper != null && helper.getQuest() == resolvedHelper.getQuest())
				{
					return helper.getQuest().name();
				}
			}
		}

		if (state == catalogState)
		{
			for (JournalSnapshot.QuestListItem quest : catalog)
			{
				if (requestedTitle.equalsIgnoreCase(quest.getTitle()))
				{
					return quest.getId();
				}
			}
		}

		for (QuestHelper helper : eligibleHelpers)
		{
			if (helper == null || helper.getQuest() == null)
			{
				continue;
			}

			if (requestedTitle.equalsIgnoreCase(CatalogMapper.displayTitle(helper)))
			{
				return helper.getQuest().name();
			}
		}
		return null;
	}

	/** Changes the quest shown in the journal without changing live guidance. */
	public boolean browseQuest(String questId)
	{
		QuestHelper helper = resolveEligibleHelper(questId);
		if (helper == null)
		{
			return false;
		}
		QuestHelper authoritativeActive = questManager.getSelectedQuest();
		setViewedHelper(
			helper,
			helper == activeHelper || helper == authoritativeActive,
			false);
		return true;
	}

	/** Clears only the quest viewed in the journal, without changing live guidance. */
	public boolean clearBrowsedQuest(String expectedQuestId)
	{
		if (!started || expectedQuestId == null
			|| !expectedQuestId.equals(viewedQuestId))
		{
			return false;
		}
		setViewedHelper(null, false, false);
		publishSourceSnapshot();
		return true;
	}

	/** Clears the journal-only selection when its owning RuneScape profile changes. */
	boolean clearBrowsedQuest()
	{
		return clearBrowsedQuest(viewedQuestId);
	}

	/** Returns the journal view to the quest currently driving live guidance. */
	public boolean browseActiveQuest()
	{
		if (!started)
		{
			return false;
		}
		QuestHelper authoritativeActive = questManager.getSelectedQuest();
		if (authoritativeActive == null || authoritativeActive.getQuest() == null)
		{
			return false;
		}
		setViewedHelper(authoritativeActive, true, false);
		return true;
	}

	/** Activates a listed helper without opening Quest Helper's sidebar. */
	public boolean activateQuest(String questId)
	{
		QuestHelper helper = resolveEligibleHelper(questId);
		if (helper == null)
		{
			return false;
		}
		if (helper != questManager.getSelectedQuest())
		{
			questManager.startUpQuest(helper, false);
		}
		return true;
	}

	/** Stops the currently active helper when it still matches the requested quest. */
	public boolean stopActiveQuest(String expectedQuestId)
	{
		if (!started || expectedQuestId == null)
		{
			return false;
		}
		QuestHelper active = questManager.getSelectedQuest();
		if (active == null
			|| active.getQuest() == null
			|| !expectedQuestId.trim().equals(active.getQuest().name()))
		{
			return false;
		}
		questManager.shutDownQuest(true);
		return true;
	}

	private QuestHelper resolveEligibleHelper(String questId)
	{
		if (!started || questId == null)
		{
			return null;
		}
		String id = questId.trim();
		if (id.isEmpty())
		{
			return null;
		}

		Map<QuestHelper, net.runelite.api.QuestState> state =
			questManager.getQuestListState();
		if (state.isEmpty())
		{
			return null;
		}
		QuestHelper helper = state == catalogState
			? helpersById.get(id)
			: null;
		if (helper == null)
		{
			for (QuestHelper candidate : CatalogMapper.eligibleHelpers(state))
			{
				if (candidate != null
					&& candidate.getQuest() != null
					&& id.equals(candidate.getQuest().name()))
				{
					helper = candidate;
					break;
				}
			}
		}
		if (helper == null)
		{
			return null;
		}
		return helper;
	}

	private void setViewedHelper(
		QuestHelper helper,
		boolean followsActive,
		boolean forceRebuild)
	{
		if (viewedHelper == helper)
		{
			viewedQuestFollowsActive = followsActive;
			if (forceRebuild && helper != null)
			{
				viewedDefinitionRefreshPending = true;
				viewedEvaluationRefreshPending = true;
				observedActiveStep = null;
				observedActiveRoot = null;
			}
			return;
		}
		viewedHelper = helper;
		viewedQuestId = helper == null || helper.getQuest() == null
			? null
			: helper.getQuest().name();
		viewedQuestFollowsActive = followsActive;
		viewedDefinition = null;
		viewedDefinitionRefreshPending = helper != null;
		viewedEvaluation = null;
		viewedEvaluationRefreshPending = true;
		observedActiveStep = null;
		observedActiveRoot = null;
		lastViewedEvaluationTick = Integer.MIN_VALUE;
		sourceSnapshotRefreshPending = true;
	}

	private void publishSourceSnapshot()
	{
		sourceSnapshot = new JournalSnapshot(
			catalog,
			viewedEvaluation,
			activeQuest,
			listOptions,
			progress);
		sourceSnapshotRefreshPending = false;
	}

	/** Forces structural state to be rebuilt on the next open tick. */
	public void invalidate()
	{
		catalogState = null;
		catalogRetry.reset();
		activeQuestRevision = Long.MIN_VALUE;
		activeStateRetry.reset();
		activeQuestRefreshPending = true;
		activeQuestRetry.reset();
		viewedDefinitionRefreshPending = viewedHelper != null;
		viewedEvaluationRefreshPending = true;
		observedActiveStep = null;
		observedActiveRoot = null;
		definitionRetry.reset();
		evaluationRetry.reset();
	}

	/** Coalesces viewed-detail changes into the next post-Quest-Helper refresh. */
	public void markViewedQuestDirty()
	{
		viewedEvaluationRefreshPending = true;
	}

	private boolean refreshActiveCatalogState(int tick)
	{
		if (!activeStateRetry.isDue(tick, activeHelper))
		{
			return false;
		}
		try
		{
			JournalSnapshot.QuestState state =
				catalogMapper.currentQuestState(activeHelper);
			activeStateRetry.succeeded(activeHelper);
			return updateCatalogState(activeHelper, state);
		}
		catch (RuntimeException | LinkageError failure)
		{
			activeStateRetry.failed(tick, activeHelper, failure);
			return false;
		}
	}

	private boolean refreshViewedDefinition(int tick)
	{
		if (viewedHelper == null
			|| !viewedDefinitionRefreshPending
			|| !definitionRetry.isDue(tick, viewedHelper))
		{
			return false;
		}
		try
		{
			QuestViewMapper.Definition replacement =
				questViewMapper.buildDefinition(viewedHelper);
			Objects.requireNonNull(
				replacement,
				"questViewMapper.buildDefinition");
			definitionRetry.succeeded(viewedHelper);
			viewedDefinition = replacement;
			viewedDefinitionRefreshPending = false;
			viewedEvaluationRefreshPending = true;
			return true;
		}
		catch (RuntimeException | LinkageError failure)
		{
			definitionRetry.failed(tick, viewedHelper, failure);
			return false;
		}
	}

	private static String helperIdentity(QuestHelper helper)
	{
		if (helper == null)
		{
			return "<none>";
		}
		try
		{
			if (helper.getQuest() != null)
			{
				return helper.getQuest().name();
			}
		}
		catch (RuntimeException | LinkageError ignored)
		{
			// Fall through to the stable class identity.
		}
		return helper.getClass().getName();
	}

	private boolean refreshActiveQuest(int tick)
	{
		if (!activeQuestRefreshPending || !activeQuestRetry.isDue(tick, activeHelper))
		{
			return false;
		}
		try
		{
			JournalSnapshot.ActiveQuest replacement = catalogMapper.buildActiveQuest(
				activeHelper,
				catalog,
				catalogState);
			activeQuestRetry.succeeded(activeHelper);
			activeQuestRefreshPending = false;
			if (Objects.equals(activeQuest, replacement))
			{
				return false;
			}
			activeQuest = replacement;
			return true;
		}
		catch (RuntimeException | LinkageError failure)
		{
			activeQuestRetry.failed(tick, activeHelper, failure);
			return false;
		}
	}

	private boolean fallbackRefreshDue(int tick)
	{
		if (lastViewedEvaluationTick == Integer.MIN_VALUE)
		{
			return true;
		}
		long elapsed = (long) tick - lastViewedEvaluationTick;
		return elapsed < 0L || elapsed >= VIEWED_QUEST_REFRESH_FALLBACK_TICKS;
	}

	private boolean refreshCatalog(int tick)
	{
		Map<QuestHelper, net.runelite.api.QuestState> state =
			questManager.getQuestListState();
		if (state == catalogState || !catalogRetry.isDue(tick, null))
		{
			return false;
		}

		try
		{
			CatalogMapper.CatalogData mapped =
				catalogMapper.buildCatalog(state);
			catalogRetry.succeeded(null);
			catalogState = state;
			catalog = mapped.getQuests();
			helpersById = mapped.getHelpersById();
			completedQuestCount = mapped.getCompletedQuestCount();
			totalQuestCount = mapped.getTotalQuestCount();
			fallbackTotalQuestPoints = mapped.getFallbackTotalQuestPoints();
			return true;
		}
		catch (RuntimeException | LinkageError failure)
		{
			catalogRetry.failed(tick, null, failure);
			return false;
		}
	}

	private boolean refreshDynamicProgress()
	{
		JournalSnapshot.QuestProgress mapped = catalogMapper.mapProgress(
			progress,
			catalogState != null && !catalogState.isEmpty(),
			completedQuestCount,
			totalQuestCount,
			fallbackTotalQuestPoints);
		if (mapped == progress)
		{
			return false;
		}
		progress = mapped;
		return true;
	}

	private boolean refreshListOptions()
	{
		JournalSnapshot.QuestMembership membership = catalogMapper.currentMembership();
		JournalSnapshot.QuestOrder order = catalogMapper.currentOrder();
		if (configuredMembership == membership && configuredOrder == order)
		{
			return false;
		}

		configuredMembership = membership;
		configuredOrder = order;
		listOptions = catalogMapper.buildListOptions(membership, order);
		return true;
	}

	private boolean updateCatalogState(
		QuestHelper helper,
		JournalSnapshot.QuestState state)
	{
		if (helper == null || helper.getQuest() == null)
		{
			return false;
		}
		String questId = helper.getQuest().name();
		for (int index = 0; index < catalog.size(); index++)
		{
			JournalSnapshot.QuestListItem item = catalog.get(index);
			if (!questId.equals(item.getId()) || item.getState() == state)
			{
				continue;
			}
			List<JournalSnapshot.QuestListItem> updated = new ArrayList<>(catalog);
			updated.set(index, new JournalSnapshot.QuestListItem(
				item.getId(),
				item.getTitle(),
				item.getType(),
				state,
				item.getDifficulty(),
				item.isMembers(),
				item.getOrderRanks()));
			catalog = Collections.unmodifiableList(updated);
			if (CatalogMapper.isCoreQuest(helper))
			{
				if (item.getState() != JournalSnapshot.QuestState.COMPLETE
					&& state == JournalSnapshot.QuestState.COMPLETE)
				{
					completedQuestCount = Math.min(totalQuestCount, completedQuestCount + 1);
				}
				else if (item.getState() == JournalSnapshot.QuestState.COMPLETE
					&& state != JournalSnapshot.QuestState.COMPLETE)
				{
					completedQuestCount = Math.max(0, completedQuestCount - 1);
				}
			}
			return true;
		}
		return false;
	}

	private static final class RetryTracker
	{
		private final String operation;
		private QuestHelper failedHelper;
		private int failureCount;
		private int lastFailureTick = Integer.MIN_VALUE;
		private int delayTicks;

		private RetryTracker(String operation)
		{
			this.operation = Objects.requireNonNull(operation, "operation");
		}

		private boolean isDue(int tick, QuestHelper helper)
		{
			if (failureCount == 0 || failedHelper != helper
				|| lastFailureTick == Integer.MIN_VALUE)
			{
				return true;
			}
			long elapsed = (long) tick - lastFailureTick;
			return elapsed < 0L || elapsed >= delayTicks;
		}

		private void succeeded(QuestHelper helper)
		{
			if (failureCount == 0 || failedHelper != helper)
			{
				return;
			}
			int recoveredFailures = failureCount;
			String target = target(helper);
			reset();
			log.debug(
				"Quest Journal {} recovered for {} after {} failures",
				operation,
				target,
				recoveredFailures);
		}

		private void failed(int tick, QuestHelper helper, Throwable failure)
		{
			if (failureCount > 0 && failedHelper != helper)
			{
				reset();
			}
			failedHelper = helper;
			boolean firstFailure = failureCount == 0;
			failureCount++;
			delayTicks = firstFailure
				? 1
				: Math.min(FAILURE_RETRY_MAX_TICKS, delayTicks * 2);
			lastFailureTick = tick;
			String target = target(helper);
			if (firstFailure)
			{
				log.warn(
					"Quest Journal {} failed for {}; retrying in {} ticks",
					operation,
					target,
					delayTicks,
					failure);
			}
			else
			{
				log.debug(
					"Quest Journal {} is still failing for {}; retrying in {} ticks",
					operation,
					target,
					delayTicks);
			}
		}

		private void reset()
		{
			failedHelper = null;
			failureCount = 0;
			lastFailureTick = Integer.MIN_VALUE;
			delayTicks = 0;
		}

		private static String target(QuestHelper helper)
		{
			return helper == null ? "the quest catalog" : helperIdentity(helper);
		}
	}

}
