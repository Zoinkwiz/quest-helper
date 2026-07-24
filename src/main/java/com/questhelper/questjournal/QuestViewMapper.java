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
import com.questhelper.panel.PanelDetails;
import com.questhelper.panel.QuestOverviewPanel;
import com.questhelper.panel.TopLevelPanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.Reward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.OwnerStep;
import com.questhelper.steps.PortTaskStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.ReorderableConditionalStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.Skill;

import static com.questhelper.questjournal.CatalogMapper.normalizeText;

/** Maps a Quest Helper quest to its journal overview, objectives, checklists, and rewards. */
@Singleton
class QuestViewMapper
{
	private final Client client;
	private final QuestHelperConfig config;
	private final RequirementMapper requirementMapper;

	@Inject
	QuestViewMapper(
		Client client,
		QuestHelperConfig config,
		RequirementMapper requirementMapper)
	{
		this.client = Objects.requireNonNull(client, "client");
		this.config = Objects.requireNonNull(config, "config");
		this.requirementMapper = Objects.requireNonNull(requirementMapper, "requirementMapper");
	}

	Definition buildDefinition(QuestHelper helper)
	{
		Objects.requireNonNull(helper, "helper");
		QuestHelperQuest quest = Objects.requireNonNull(helper.getQuest(), "helper.quest");
		helper.initializeRequirements();
		helper.init();
		List<StepDefinition> steps = new ArrayList<>();
		for (PanelDetails panel : safeList(helper.getPanels()))
		{
			appendPanelDefinition(
				panel,
				"",
				Collections.emptyList(),
				steps);
		}

		List<Requirement> generalRequirements = copyNonNull(helper.getGeneralRequirements());
		if (config.showFullRequirements())
		{
			try
			{
				List<Requirement> aggregated =
					copyNonNull(QuestOverviewPanel.getAggregatedRequirements(helper));
				if (!aggregated.isEmpty())
				{
					generalRequirements = aggregated;
				}
			}
			catch (RuntimeException ignored)
			{
				// Fall back to Quest Helper's direct requirements.
			}
		}
		List<Requirement> requirements = copyNonNull(
			generalRequirements,
			helper.getItemRequirements());
		List<Requirement> recommendations = copyNonNull(
			helper.getGeneralRecommended(),
			helper.getItemRecommended());

		List<JournalSnapshot.Reward> rewards =
			buildRewardViews(copyNonNull(helper.getQuestRewards()));
		return new Definition(
			helper,
			quest,
			CatalogMapper.displayTitle(helper),
			steps,
			requirements,
			recommendations,
			cleanStrings(helper.getCombatRequirements(), true),
			rewards,
			cleanStrings(helper.getNotes(), false));
	}

	JournalSnapshot.SelectedQuest evaluate(
		Definition definition,
		QuestStep activeRoot,
		QuestStep activeStep,
		boolean inheritActivePathRequirements)
	{
		QuestHelper helper = definition.helper;
		RequirementMapper.Session requirementSession =
			requirementMapper.newSession();
		net.runelite.api.QuestState questState = helper.getState(client);

		List<ObjectiveData> objectiveData = buildObjectives(
			definition.quest.name(),
			definition.steps,
			activeRoot,
			activeStep,
			questState,
			inheritActivePathRequirements,
			requirementSession);
		List<JournalSnapshot.Objective> objectives = objectiveData.stream()
			.map(ObjectiveData::toView)
			.collect(Collectors.toList());

		List<JournalSnapshot.Requirement> requirementViews =
			requirementSession.buildViews(definition.requirements);
		List<JournalSnapshot.Requirement> recommendationViews =
			requirementSession.buildViews(definition.recommendations);
		List<JournalSnapshot.Reward> rewardViews = config.hideQuestRewards()
			? Collections.emptyList()
			: definition.rewards;

		JournalSnapshot.QuestOverview overview = new JournalSnapshot.QuestOverview(
			definition.quest.name(),
			definition.title,
			CatalogMapper.journalType(definition.quest.getQuestType()),
			CatalogMapper.journalState(questState),
			CatalogMapper.journalDifficulty(definition.quest),
			CatalogMapper.isMembers(definition.quest.getQuestType()));

		return new JournalSnapshot.SelectedQuest(
			overview,
			objectives,
			requirementViews,
			recommendationViews,
			definition.enemies,
			rewardViews,
			definition.notes);
	}

	StepSelection resolveSteps(QuestHelper helper, boolean active)
	{
		return active ? activeSteps(helper) : inactiveSteps(helper);
	}

	private static StepSelection activeSteps(QuestHelper helper)
	{
		if (helper == null)
		{
			return StepSelection.EMPTY;
		}
		QuestStep root = helper.getCurrentStep();
		QuestStep leaf = root == null ? null : root.getActiveStep();
		return new StepSelection(root, leaf);
	}

	private StepSelection inactiveSteps(QuestHelper helper)
	{
		QuestStep root;
		if (helper instanceof BasicQuestHelper)
		{
			BasicQuestHelper basicHelper = (BasicQuestHelper) helper;
			Map<Integer, QuestStep> steps = basicHelper.getStepList();
			if (steps == null || steps.isEmpty())
			{
				return StepSelection.EMPTY;
			}
			Integer selectedState = basicHelper.getSelectedStateOverride();
			int state = selectedState == null ? basicHelper.getVar() : selectedState;
			root = steps.get(state);
		}
		else if (helper instanceof ComplexStateQuestHelper)
		{
			root = ((ComplexStateQuestHelper) helper).getStep();
		}
		else
		{
			return StepSelection.EMPTY;
		}
		QuestStep leaf = resolveInactiveStep(
			root,
			Collections.newSetFromMap(new IdentityHashMap<>()));
		return new StepSelection(root, leaf);
	}

	private QuestStep resolveInactiveStep(QuestStep step, Set<QuestStep> visited)
	{
		if (!(step instanceof ConditionalStep) || !visited.add(step))
		{
			return step;
		}
		QuestStep selected = selectInactiveStep((ConditionalStep) step);
		if (selected == null || selected == step)
		{
			return step;
		}
		return resolveInactiveStep(selected, visited);
	}

	private QuestStep selectInactiveStep(ConditionalStep conditionalStep)
	{
		Map<Requirement, QuestStep> steps = conditionalStep.getStepsMap();
		if (steps == null || steps.isEmpty())
		{
			return null;
		}
		boolean reordered = conditionalStep instanceof ReorderableConditionalStep
			&& conditionalStep.getQuestHelper() != null
			&& conditionalStep.getQuestHelper().getSidebarOrder() != null;
		Requirement lastPossibleCondition = null;

		for (Map.Entry<Requirement, QuestStep> entry : steps.entrySet())
		{
			Requirement condition = entry.getKey();
			QuestStep candidate = entry.getValue();
			if (candidate == null)
			{
				continue;
			}
			boolean locked = isLockedForResolution(candidate);
			if (condition == null && !locked && reordered)
			{
				return candidate;
			}
			if (condition != null && checksTrueForResolution(condition) && !locked)
			{
				return candidate;
			}
			if (candidate.isBlocker() && locked)
			{
				return steps.get(lastPossibleCondition);
			}
			if (condition != null && !locked)
			{
				lastPossibleCondition = condition;
			}
		}

		QuestStep fallback = steps.get(null);
		if (fallback != null && !isLockedForResolution(fallback))
		{
			return fallback;
		}
		return steps.get(lastPossibleCondition);
	}

	private void appendPanelDefinition(
		PanelDetails panel,
		String parentSection,
		List<Requirement> ancestorHideConditions,
		List<StepDefinition> steps)
	{
		if (panel == null)
		{
			return;
		}
		List<Requirement> hideConditions = new ArrayList<>(ancestorHideConditions);
		if (panel.getHideCondition() != null)
		{
			hideConditions.add(panel.getHideCondition());
		}
		String ownHeader = normalizeText(panel.getHeader());
		String section = ownHeader.isEmpty() ? parentSection : ownHeader;

		if (panel instanceof TopLevelPanelDetails)
		{
			for (PanelDetails child : ((TopLevelPanelDetails) panel).getPanelDetails())
			{
				appendPanelDefinition(
					child,
					section,
					hideConditions,
					steps);
			}
			return;
		}

		for (QuestStep step : safeList(panel.getSteps()))
		{
			if (step instanceof PortTaskStep)
			{
				for (QuestStep portStep : ((PortTaskStep) step).getStepsList())
				{
					if (portStep != null)
					{
						steps.add(new StepDefinition(portStep, panel, section, hideConditions));
					}
				}
			}
			else if (step != null)
			{
				steps.add(new StepDefinition(step, panel, section, hideConditions));
			}
		}
	}

	private List<ObjectiveData> buildObjectives(
		String questId,
		List<StepDefinition> definitions,
		QuestStep activeRoot,
		QuestStep activeStep,
		net.runelite.api.QuestState questState,
		boolean inheritActivePathRequirements,
		RequirementMapper.Session requirementSession)
	{
		boolean questStarted = questState != net.runelite.api.QuestState.NOT_STARTED;
		boolean questComplete = questState == net.runelite.api.QuestState.FINISHED;
		List<ObjectiveData> objectives = new ArrayList<>();
		List<QuestStep> activePath = stepPath(activeRoot, activeStep);
		Map<PanelDetails, String> sectionIds = new IdentityHashMap<>();
		Map<String, LinkedHashSet<JournalSnapshot.Requirement>> sectionRequirements =
			new LinkedHashMap<>();
		for (int definitionIndex = 0; definitionIndex < definitions.size(); definitionIndex++)
		{
			StepDefinition definition = definitions.get(definitionIndex);
			String sectionId = sectionIds.get(definition.panel);
			if (sectionId == null)
			{
				sectionId = definition.section.isEmpty()
					? ""
					: questId + ":section:" + definitionIndex;
				sectionIds.put(definition.panel, sectionId);
			}
			QuestStep step = definition.step;
			if (!pathVisible(definition.hideConditions)
				|| !step.isShowInSidebar()
				|| checksTrue(step.getConditionToHide()))
			{
				continue;
			}
			String text = joinStepText(step);
			if (text.isEmpty())
			{
				continue;
			}
			Set<JournalSnapshot.Requirement> requirementsForSection = sectionId.isEmpty()
				? null
				: sectionRequirements.computeIfAbsent(
					sectionId,
					ignored -> new LinkedHashSet<>(
						requirementSession.buildViews(definition.panel.getRequirements())));
			boolean current = questStarted
				&& activeStep != null
				&& containsStep(step, activeStep);
			boolean sectionComplete = questStarted
				&& definition.panel.getLockingQuestSteps() != null
				&& isLocked(definition.panel.getLockingQuestSteps());
			boolean faded = checksTrue(step.getFadeCondition());
			List<JournalSnapshot.Requirement> stepRequirements = buildStepRequirementViews(
				step,
				questStarted ? activeStep : null,
				current ? activePath : Collections.emptyList(),
				inheritActivePathRequirements,
				requirementSession);
			if (requirementsForSection != null)
			{
				requirementsForSection.addAll(stepRequirements);
			}
			objectives.add(new ObjectiveData(
				sectionId,
				definition.section,
				text,
				objectiveState(current, sectionComplete, faded),
				current,
				Collections.emptyList()));
		}
		for (ObjectiveData objective : objectives)
		{
			Set<JournalSnapshot.Requirement> requirements =
				sectionRequirements.get(objective.sectionId);
			objective.sectionRequirements = requirements == null
				? Collections.emptyList()
				: new ArrayList<>(requirements);
		}

		if (questComplete)
		{
			for (ObjectiveData objective : objectives)
			{
				objective.state = JournalSnapshot.ObjectiveState.COMPLETE;
				objective.current = false;
			}
			return objectives;
		}
		if (!questStarted && inheritActivePathRequirements && !objectives.isEmpty())
		{
			ObjectiveData firstObjective = objectives.get(0);
			firstObjective.current = true;
			firstObjective.state = JournalSnapshot.ObjectiveState.AVAILABLE;
		}

		int currentIndex = -1;
		for (int index = 0; index < objectives.size(); index++)
		{
			if (objectives.get(index).current)
			{
				currentIndex = index;
				break;
			}
		}
		for (int index = 0; questStarted && index < currentIndex; index++)
		{
			ObjectiveData objective = objectives.get(index);
			if (objective.state == JournalSnapshot.ObjectiveState.AVAILABLE)
			{
				objective.state = JournalSnapshot.ObjectiveState.COMPLETE;
			}
		}
		if (!inheritActivePathRequirements)
		{
			for (ObjectiveData objective : objectives)
			{
				if (objective.current)
				{
					objective.current = false;
				}
			}
		}
		return objectives;
	}

	private List<JournalSnapshot.Requirement> buildStepRequirementViews(
		QuestStep step,
		QuestStep activeStep,
		List<QuestStep> activePath,
		boolean inheritActivePathRequirements,
		RequirementMapper.Session requirementSession)
	{
		Set<Requirement> seen = Collections.newSetFromMap(new IdentityHashMap<>());
		Set<QuestStep> seenSteps = Collections.newSetFromMap(new IdentityHashMap<>());
		List<JournalSnapshot.Requirement> result = new ArrayList<>();
		boolean followsActivePath = activeStep != null && containsStep(step, activeStep);
		if (followsActivePath && hidesRequirements(activeStep))
		{
			return Collections.emptyList();
		}
		boolean inheritActivePath = followsActivePath && inheritActivePathRequirements;
		appendStepPathRequirements(
			inheritActivePath ? activePath : Collections.singletonList(step),
			seenSteps,
			seen,
			result,
			requirementSession);
		if (inheritActivePath
			&& activePath.stream().noneMatch(pathStep -> pathStep == step))
		{
			appendStepPathRequirements(
				stepPath(step, activeStep),
				seenSteps,
				seen,
				result,
				requirementSession);
		}
		return result;
	}

	private static void appendStepPathRequirements(
		List<QuestStep> path,
		Set<QuestStep> seenSteps,
		Set<Requirement> seenRequirements,
		List<JournalSnapshot.Requirement> result,
		RequirementMapper.Session requirementSession)
	{
		for (QuestStep current : path)
		{
			if (current != null && seenSteps.add(current))
			{
				appendStepItemRequirements(
					current,
					seenRequirements,
					result,
					requirementSession);
			}
		}
	}

	private List<QuestStep> stepPath(QuestStep root, QuestStep leaf)
	{
		if (root == null)
		{
			return leaf == null
				? Collections.emptyList()
				: Collections.singletonList(leaf);
		}
		Set<QuestStep> visited = Collections.newSetFromMap(new IdentityHashMap<>());
		List<QuestStep> path = new ArrayList<>();
		QuestStep current = root;
		while (current != null && visited.add(current))
		{
			path.add(current);
			if (current == leaf || leaf == null)
			{
				break;
			}
			current = childOnPathTo(current, leaf);
		}
		if (leaf != null && !visited.contains(leaf))
		{
			path.add(leaf);
		}
		return path;
	}

	private QuestStep childOnPathTo(QuestStep parent, QuestStep target)
	{
		for (QuestStep child : directChildSteps(parent))
		{
			if (containsStep(child, target))
			{
				return child;
			}
		}
		return null;
	}

	private static List<QuestStep> directChildSteps(QuestStep step)
	{
		if (step == null)
		{
			return Collections.emptyList();
		}
		Set<QuestStep> seen = Collections.newSetFromMap(new IdentityHashMap<>());
		List<QuestStep> children = new ArrayList<>();
		for (QuestStep child : safeList(step.getSubsteps()))
		{
			if (child != null && seen.add(child))
			{
				children.add(child);
			}
		}
		if (step instanceof OwnerStep)
		{
			Collection<QuestStep> owned = ((OwnerStep) step).getSteps();
			if (owned != null)
			{
				for (QuestStep child : owned)
				{
					if (child != null && seen.add(child))
					{
						children.add(child);
					}
				}
			}
		}
		return children;
	}

	private static void appendStepItemRequirements(
		QuestStep step,
		Set<Requirement> seen,
		List<JournalSnapshot.Requirement> result,
		RequirementMapper.Session requirementSession)
	{
		if (step == null || hidesRequirements(step))
		{
			return;
		}
		for (Requirement requirement : stepRequirements(step))
		{
			if (!(requirement instanceof ItemRequirement) || !seen.add(requirement))
			{
				continue;
			}
			JournalSnapshot.Requirement view = requirementSession.build(requirement);
			if (view != null)
			{
				result.add(view);
			}
		}
	}

	private static boolean hidesRequirements(QuestStep step)
	{
		return step instanceof DetailedQuestStep
			&& ((DetailedQuestStep) step).hideRequirements;
	}

	private static List<Requirement> stepRequirements(QuestStep step)
	{
		if (step instanceof DetailedQuestStep)
		{
			return ((DetailedQuestStep) step).getRequirements();
		}
		if (step instanceof ConditionalStep)
		{
			return ((ConditionalStep) step).getRequirements();
		}
		if (step instanceof DetailedOwnerStep)
		{
			return ((DetailedOwnerStep) step).getRequirements();
		}
		return Collections.emptyList();
	}

	List<JournalSnapshot.Reward> buildRewardViews(List<Reward> rewards)
	{
		List<JournalSnapshot.Reward> result = new ArrayList<>();
		for (Reward reward : safeList(rewards))
		{
			if (reward == null)
			{
				continue;
			}
			String detail = normalizeText(reward.getDisplayText());
			if (detail.isEmpty())
			{
				continue;
			}
			JournalSnapshot.IconIdentity icon = rewardIcon(reward);
			result.add(new JournalSnapshot.Reward(detail, icon));
		}
		return result;
	}

	JournalSnapshot.IconIdentity rewardIcon(Reward reward)
	{
		if (reward instanceof ItemReward)
		{
			ItemReward item = (ItemReward) reward;
			return item.getItemID() < 0
				? JournalSnapshot.IconIdentity.none()
				: JournalSnapshot.IconIdentity.item(
					item.getItemID(),
					Math.max(1, item.getQuantity()));
		}
		if (reward instanceof ExperienceReward)
		{
			Skill skill = ((ExperienceReward) reward).getSkill();
			return skill == null
				? JournalSnapshot.IconIdentity.none()
				: JournalSnapshot.IconIdentity.skill(skill.name());
		}
		if (reward instanceof QuestPointReward)
		{
			return JournalSnapshot.IconIdentity.questPoints();
		}
		return JournalSnapshot.IconIdentity.none();
	}

	static JournalSnapshot.ObjectiveState objectiveState(
		boolean current,
		boolean sectionComplete,
		boolean faded)
	{
		if (!current && sectionComplete)
		{
			return JournalSnapshot.ObjectiveState.COMPLETE;
		}
		if (!current && faded)
		{
			return JournalSnapshot.ObjectiveState.FADED;
		}
		return JournalSnapshot.ObjectiveState.AVAILABLE;
	}

	private boolean pathVisible(List<Requirement> hideConditions)
	{
		for (Requirement condition : hideConditions)
		{
			if (checksTrue(condition))
			{
				return false;
			}
		}
		return true;
	}

	private boolean checksTrue(Requirement requirement)
	{
		if (requirement == null)
		{
			return false;
		}
		try
		{
			return requirement.check(client);
		}
		catch (RuntimeException ignored)
		{
			return false;
		}
	}

	private boolean checksTrueForResolution(Requirement requirement)
	{
		return requirement != null && requirement.check(client);
	}

	private static boolean containsStep(QuestStep step, QuestStep activeStep)
	{
		try
		{
			return containsStepReadOnly(
				step,
				activeStep,
				Collections.newSetFromMap(new IdentityHashMap<>()));
		}
		catch (RuntimeException ignored)
		{
			return step == activeStep;
		}
	}

	private static boolean containsStepReadOnly(
		QuestStep step,
		QuestStep activeStep,
		Set<QuestStep> visited)
	{
		if (step == null || activeStep == null || !visited.add(step))
		{
			return false;
		}
		if (step == activeStep)
		{
			return true;
		}
		for (QuestStep child : directChildSteps(step))
		{
			if (containsStepReadOnly(child, activeStep, visited))
			{
				return true;
			}
		}
		return false;
	}

	private boolean isLocked(QuestStep step)
	{
		if (step == null)
		{
			return false;
		}
		try
		{
			return step.isLockedWithoutUpdate(client);
		}
		catch (RuntimeException ignored)
		{
			return false;
		}
	}

	private boolean isLockedForResolution(QuestStep step)
	{
		return step != null && step.isLockedWithoutUpdate(client);
	}

	private static String joinStepText(QuestStep step)
	{
		if (step == null || step.getText() == null)
		{
			return "";
		}
		return step.getText().stream()
			.map(CatalogMapper::normalizeText)
			.filter(value -> !value.isEmpty())
			.collect(Collectors.joining("\n\n"));
	}

	private static List<String> cleanStrings(List<String> values, boolean distinct)
	{
		Collection<String> cleaned = distinct
			? new LinkedHashSet<>()
			: new ArrayList<>();
		for (String value : safeList(values))
		{
			String text = normalizeText(value);
			if (!text.isEmpty())
			{
				cleaned.add(text);
			}
		}
		return new ArrayList<>(cleaned);
	}

	@SafeVarargs
	private static <T> List<T> copyNonNull(List<? extends T>... groups)
	{
		return Arrays.stream(groups)
			.filter(Objects::nonNull)
			.flatMap(Collection::stream)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	private static <T> List<T> safeList(List<T> values)
	{
		return values == null ? Collections.emptyList() : values;
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	static final class Definition
	{
		final QuestHelper helper;
		final QuestHelperQuest quest;
		final String title;
		final List<StepDefinition> steps;
		final List<Requirement> requirements;
		final List<Requirement> recommendations;
		final List<String> enemies;
		final List<JournalSnapshot.Reward> rewards;
		final List<String> notes;
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	private static final class StepDefinition
	{
		private final QuestStep step;
		private final PanelDetails panel;
		private final String section;
		private final List<Requirement> hideConditions;
	}

	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	private static final class ObjectiveData
	{
		private final String sectionId;
		private final String section;
		private final String text;
		private JournalSnapshot.ObjectiveState state;
		private boolean current;
		private List<JournalSnapshot.Requirement> sectionRequirements =
			Collections.emptyList();

		private JournalSnapshot.Objective toView()
		{
			return new JournalSnapshot.Objective(
				sectionId,
				section,
				text,
				state,
				current,
				sectionRequirements);
		}
	}

	@Getter
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	static final class StepSelection
	{
		private static final StepSelection EMPTY = new StepSelection(null, null);

		private final QuestStep root;
		private final QuestStep leaf;
	}
}
