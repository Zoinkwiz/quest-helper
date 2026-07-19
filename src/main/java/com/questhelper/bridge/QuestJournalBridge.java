/*
 * Copyright (c) 2026, Quest Helper contributors
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
package com.questhelper.bridge;

import com.questhelper.QuestHelperConfig;
import com.questhelper.QuestHelperConfig.QuestOrdering;
import com.questhelper.domain.AccountType;
import com.questhelper.config.LeagueFiltering;
import com.questhelper.managers.ItemAndLastUpdated;
import com.questhelper.managers.QuestContainerManager;
import com.questhelper.managers.QuestManager;
import com.questhelper.panel.PanelDetails;
import com.questhelper.panel.TopLevelPanelDetails;
import com.questhelper.panel.questorders.IronmanOptimalQuestGuide;
import com.questhelper.panel.questorders.OptimalQuestGuide;
import com.questhelper.panel.questorders.ReleaseDate;
import com.questhelper.questhelpers.QuestDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.item.NoItemRequirement;
import com.questhelper.requirements.item.TrackedContainers;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.Reward;
import com.questhelper.steps.PortTaskStep;
import com.questhelper.steps.QuestStep;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.WorldType;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.PluginMessage;
import net.runelite.client.util.Text;

/**
 * Publishes a classloader-safe, immutable view of the data rendered by Quest
 * Helper's sidebar. Payload values are limited to strings, numbers, booleans,
 * lists, and maps so consumers do not depend on Quest Helper implementation
 * classes.
 */
@Singleton
public class QuestJournalBridge
{
	public static final String NAMESPACE = "quest-helper";
	public static final String STATE_REQUEST = "journal-state-request-v1";
	public static final String STATE_CHANGED = "journal-state-v1";
	public static final String UNSUBSCRIBE = "journal-unsubscribe-v1";
	private static final int PROTOCOL_VERSION = 1;
	private static final int QUEST_LIST_REFRESH_TICKS = 10;

	private final Client client;
	private final ClientThread clientThread;
	private final EventBus eventBus;
	private final QuestManager questManager;
	private final QuestHelperConfig config;

	private List<Map<String, Object>> cachedQuestList = Collections.emptyList();
	private List<QuestHelper> cachedEligibleQuests = Collections.emptyList();
	private List<QuestHelper> cachedCoreQuests = Collections.emptyList();
	private int cachedQuestListTick = Integer.MIN_VALUE;
	private Map<String, Object> lastPublishedState;
	private final Set<String> consumers = new LinkedHashSet<>();
	private long revision;
	private String sessionId;
	private boolean started;

	@Inject
	public QuestJournalBridge(
		Client client,
		ClientThread clientThread,
		EventBus eventBus,
		QuestManager questManager,
		QuestHelperConfig config)
	{
		this.client = client;
		this.clientThread = clientThread;
		this.eventBus = eventBus;
		this.questManager = questManager;
		this.config = config;
	}

	public void startUp()
	{
		if (started)
		{
			return;
		}
		sessionId = UUID.randomUUID().toString();
		revision = 0L;
		lastPublishedState = null;
		started = true;
		eventBus.register(this);
		clientThread.invokeLater(() -> publish(true));
	}

	public void shutDown()
	{
		if (!started)
		{
			return;
		}
		publishStopped();
		started = false;
		eventBus.unregister(this);
		cachedQuestList = Collections.emptyList();
		cachedEligibleQuests = Collections.emptyList();
		cachedCoreQuests = Collections.emptyList();
		cachedQuestListTick = Integer.MIN_VALUE;
		lastPublishedState = null;
		consumers.clear();
		revision = 0L;
		sessionId = null;
	}

	/** Called from Quest Helper's client-thread game-tick update. */
	public void publishIfChanged()
	{
		if (started)
		{
			publish(false);
		}
	}

	@Subscribe
	public void onPluginMessage(PluginMessage message)
	{
		if (!NAMESPACE.equals(message.getNamespace()))
		{
			return;
		}

		if (STATE_REQUEST.equals(message.getName()))
		{
			String consumerId = consumerId(message);
			if (consumerId != null)
			{
				clientThread.invokeLater(() ->
				{
					consumers.add(consumerId);
					publish(true);
				});
			}
			return;
		}

		if (UNSUBSCRIBE.equals(message.getName()))
		{
			String consumerId = consumerId(message);
			if (consumerId != null)
			{
				clientThread.invokeLater(() ->
				{
					consumers.remove(consumerId);
				});
			}
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invokeLater(() -> publish(true));
		}
		else
		{
			cachedQuestList = Collections.emptyList();
			cachedEligibleQuests = Collections.emptyList();
			cachedCoreQuests = Collections.emptyList();
			cachedQuestListTick = Integer.MIN_VALUE;
			publish(true);
		}
	}

	private void publish(boolean force)
	{
		if (!started || consumers.isEmpty())
		{
			return;
		}

		Map<String, Object> state = buildState(force);
		if (!force && state.equals(lastPublishedState))
		{
			return;
		}
		lastPublishedState = state;
		broadcast(state);
	}

	private void publishStopped()
	{
		if (consumers.isEmpty() || sessionId == null)
		{
			return;
		}

		Map<String, Object> state = new LinkedHashMap<>();
		state.put("protocol", PROTOCOL_VERSION);
		state.put("sessionId", sessionId);
		state.put("status", "STOPPED");
		state.put("available", false);
		state.put("quests", Collections.emptyList());
		state.put("selected", Collections.emptyMap());
		lastPublishedState = state;
		broadcast(state);
	}

	private void broadcast(Map<String, Object> state)
	{
		long nextRevision = ++revision;
		for (String consumerId : new ArrayList<>(consumers))
		{
			Map<String, Object> payload = new LinkedHashMap<>(state);
			payload.put("consumerId", consumerId);
			payload.put("revision", nextRevision);
			eventBus.post(new PluginMessage(NAMESPACE, STATE_CHANGED, payload));
		}
	}

	private String consumerId(PluginMessage message)
	{
		Object value = message.getData().get("consumerId");
		if (!(value instanceof String))
		{
			return null;
		}
		String consumerId = ((String) value).trim();
		return consumerId.isEmpty() ? null : consumerId;
	}

	private Map<String, Object> buildState(boolean forceQuestList)
	{
		Map<String, Object> state = new LinkedHashMap<>();
		state.put("protocol", PROTOCOL_VERSION);
		state.put("sessionId", sessionId);
		boolean available = client.getGameState() == GameState.LOGGED_IN;
		state.put("status", available ? "READY" : "LOGGED_OUT");
		state.put("available", available);
		if (!available)
		{
			state.put("quests", Collections.emptyList());
			state.put("selected", Collections.emptyMap());
			return state;
		}

		int tick = client.getTickCount();
		if (forceQuestList
			|| cachedQuestListTick == Integer.MIN_VALUE
			|| tick - cachedQuestListTick >= QUEST_LIST_REFRESH_TICKS)
		{
			cachedEligibleQuests = buildEligibleQuestList();
			cachedCoreQuests = buildCoreQuestList();
			cachedQuestList = buildQuestList(cachedEligibleQuests);
			cachedQuestListTick = tick;
		}
		state.put("quests", cachedQuestList);
		state.put("progress", buildProgress(cachedCoreQuests));
		state.put("selected", buildSelectedQuest());
		state.put("listOptions", buildListOptions());
		return state;
	}

	private List<QuestHelper> buildEligibleQuestList()
	{
		return filterEligibleQuestList(
			QuestHelperQuest.getQuestHelpers(questManager.isDeveloperMode()));
	}

	List<QuestHelper> filterEligibleQuestList(List<QuestHelper> quests)
	{
		return quests.stream()
			.filter(LeagueFiltering::passesLeagueFilter)
			.collect(Collectors.toList());
	}

	private List<QuestHelper> buildCoreQuestList()
	{
		return QuestHelperQuest.getQuestHelpers(false).stream()
			.filter(QuestJournalBridge::isCoreQuest)
			.collect(Collectors.toList());
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

	List<Map<String, Object>> buildQuestList(List<QuestHelper> quests)
	{
		Map<QuestHelperQuest, Map<String, Object>> orderRanks = buildOrderRanks(quests);

		List<Map<String, Object>> snapshots = new ArrayList<>(quests.size());
		for (QuestHelper helper : quests)
		{
			QuestHelperQuest quest = helper.getQuest();
			Map<String, Object> snapshot = new LinkedHashMap<>();
			snapshot.put("id", quest.name());
			snapshot.put("title", cleanText(helper.getDisplayedQuestName()));
			snapshot.put("category", quest.getQuestType().name());
			snapshot.put("type", journalType(quest.getQuestType()));
			snapshot.put("state", stateName(helper.getState(client)));
			snapshot.put("difficulty", journalDifficulty(quest));
			snapshot.put("members", isMembers(quest.getQuestType()));
			snapshot.put("orders", orderRanks.get(quest));
			snapshots.add(snapshot);
		}
		return Collections.unmodifiableList(snapshots);
	}

	Map<String, Object> buildProgress(List<QuestHelper> quests)
	{
		int completedQuestCount = 0;
		int fallbackTotalQuestPoints = 0;
		for (QuestHelper helper : quests)
		{
			if (!isCoreQuest(helper))
			{
				continue;
			}
			boolean complete = helper.getState(client) == QuestState.FINISHED;
			if (complete)
			{
				completedQuestCount++;
			}
			QuestPointReward reward = helper.getQuestPointReward();
			if (reward != null && reward.getPoints() > 0)
			{
				fallbackTotalQuestPoints += reward.getPoints();
			}
		}
		int totalQuestCount = (int) quests.stream().filter(QuestJournalBridge::isCoreQuest).count();
		int currentQuestPoints = Math.max(0, client.getVarpValue(VarPlayerID.QP));
		int reportedMaximum = Math.max(0, client.getVarbitValue(VarbitID.QP_MAX));
		int totalQuestPoints = reportedMaximum > 0
			? reportedMaximum
			: fallbackTotalQuestPoints;
		totalQuestPoints = Math.max(totalQuestPoints, currentQuestPoints);
		Map<String, Object> progress = new LinkedHashMap<>();
		progress.put("completedQuestCount", completedQuestCount);
		progress.put("totalQuestCount", totalQuestCount);
		progress.put("currentQuestPoints", currentQuestPoints);
		progress.put("totalQuestPoints", totalQuestPoints);
		return Collections.unmodifiableMap(progress);
	}

	Map<QuestHelperQuest, Map<String, Object>> buildOrderRanks(List<QuestHelper> quests)
	{
		Map<QuestHelperQuest, Map<String, Object>> ranks = new LinkedHashMap<>();
		for (QuestHelper helper : quests)
		{
			ranks.put(helper.getQuest(), new LinkedHashMap<>());
		}
		for (QuestOrdering order : QuestOrdering.values())
		{
			List<QuestHelper> ordered = order.sort(quests);
			Set<QuestHelperQuest> ranked = new LinkedHashSet<>();
			int rank = 0;
			for (QuestHelperConfig.QuestFilter section : order.getSections())
			{
				for (QuestHelper helper : ordered)
				{
					QuestHelperQuest quest = helper.getQuest();
					if (!section.test(helper)
						|| !hasDefinedOrderPosition(order, quest)
						|| !ranked.add(quest))
					{
						continue;
					}
					Map<String, Object> questRanks = ranks.get(quest);
					if (questRanks != null)
					{
						questRanks.put(order.name(), rank++);
					}
				}
			}
		}
		for (Map.Entry<QuestHelperQuest, Map<String, Object>> entry : ranks.entrySet())
		{
			entry.setValue(Collections.unmodifiableMap(entry.getValue()));
		}
		return Collections.unmodifiableMap(ranks);
	}

	private boolean hasDefinedOrderPosition(QuestOrdering order, QuestHelperQuest quest)
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

	Map<String, Object> buildListOptions()
	{
		Map<String, Object> options = new LinkedHashMap<>();
		List<String> types = Arrays.asList(
			"QUEST",
			"MINIQUEST",
			"ACHIEVEMENT_DIARY",
			"GENERIC",
			"SKILL",
			"PLAYER_QUEST");
		List<String> difficulties = Arrays.asList(
			QuestDetails.Difficulty.NOVICE.name(),
			QuestDetails.Difficulty.INTERMEDIATE.name(),
			QuestDetails.Difficulty.EXPERIENCED.name(),
			QuestDetails.Difficulty.MASTER.name(),
			QuestDetails.Difficulty.GRANDMASTER.name(),
			"EASY",
			"MEDIUM",
			"HARD",
			"ELITE");
		List<String> memberships = Arrays.asList("FREE_TO_PLAY", "MEMBERS");
		List<String> orders = new ArrayList<>();
		for (QuestOrdering order : QuestOrdering.values())
		{
			orders.add(order.name());
		}
		options.put("types", Collections.unmodifiableList(types));
		options.put("difficulties", Collections.unmodifiableList(difficulties));
		options.put("memberships", Collections.unmodifiableList(memberships));
		options.put("orders", Collections.unmodifiableList(orders));
		QuestDetails.Difficulty configuredDifficulty = config.difficulty();
		QuestOrdering configuredOrder = config.orderListBy();
		options.put("configuredType", configuredType(config.filterListBy(), configuredDifficulty));
		options.put(
			"configuredDifficulty",
			isCanonicalDifficulty(configuredDifficulty)
				? configuredDifficulty.name()
				: QuestDetails.Difficulty.ALL.name());
		options.put(
			"configuredOrder",
			configuredOrder == null ? QuestOrdering.A_TO_Z.name() : configuredOrder.name());
		options.put("defaultMembership", currentMembership());
		options.put("defaultOrder", currentOrder());
		return Collections.unmodifiableMap(options);
	}

	private Map<String, Object> buildSelectedQuest()
	{
		QuestHelper helper = questManager.getSelectedQuest();
		if (helper == null || helper.getQuest() == null)
		{
			return Collections.emptyMap();
		}

		QuestHelperQuest quest = helper.getQuest();
		QuestState selectedQuestState = helper.getState(client);
		QuestStep currentStep = helper.getCurrentStep();
		QuestStep activeStep = currentStep == null ? null : currentStep.getActiveStep();
		Map<String, Object> selected = new LinkedHashMap<>();
		Map<String, Object> overview = new LinkedHashMap<>();
		overview.put("id", quest.name());
		overview.put("title", cleanText(helper.getDisplayedQuestName()));
		overview.put("summary", "");
		overview.put("category", quest.getQuestType().name());
		overview.put("type", journalType(quest.getQuestType()));
		overview.put("state", stateName(selectedQuestState));
		overview.put("difficulty", journalDifficulty(quest));
		overview.put("members", isMembers(quest.getQuestType()));
		selected.put("overview", overview);

		List<Map<String, Object>> objectives = new ArrayList<>();
		List<Requirement> panelRequirements = new ArrayList<>();
		List<Requirement> panelRecommendations = new ArrayList<>();
		for (PanelDetails panel : safeList(helper.getPanels()))
		{
			appendPanel(
				panel,
				"",
				activeStep,
				objectives,
				panelRequirements,
				panelRecommendations);
		}
		if (selectedQuestState == QuestState.FINISHED)
		{
			markAllObjectivesComplete(objectives);
		}
		else
		{
			markPriorObjectivesComplete(objectives);
		}
		selected.put("objectives", objectives);
		selected.put("requirements", buildRequirements(helper, panelRequirements));
		List<Map<String, Object>> recommendations = buildRecommendations(helper, panelRecommendations);
		selected.put("recommendations", recommendations.stream()
			.map(recommendation -> recommendation.get("text"))
			.collect(Collectors.toList()));
		selected.put("recommendationDetails", recommendations);
		selected.put("enemies", buildEnemies(helper));
		selected.put("rewards", buildRewards(helper));
		selected.put("notes", cleanStrings(helper.getNotes()));
		return selected;
	}

	private void appendPanel(
		PanelDetails panel,
		String parentSection,
		QuestStep activeStep,
		List<Map<String, Object>> objectives,
		List<Requirement> requirements,
		List<Requirement> recommendations)
	{
		if (panel == null || checksTrue(panel.getHideCondition()))
		{
			return;
		}

		String ownHeader = cleanText(panel.getHeader());
		String section = ownHeader.isEmpty() ? parentSection : ownHeader;
		requirements.addAll(safeList(panel.getRequirements()));
		recommendations.addAll(safeList(panel.getRecommended()));
		if (panel instanceof TopLevelPanelDetails)
		{
			for (PanelDetails child : ((TopLevelPanelDetails) panel).getPanelDetails())
			{
				appendPanel(
					child,
					section,
					activeStep,
					objectives,
					requirements,
					recommendations);
			}
			return;
		}

		boolean sectionComplete = isPanelComplete(panel);
		for (QuestStep step : safeList(panel.getSteps()))
		{
			if (step instanceof PortTaskStep)
			{
				for (QuestStep portStep : ((PortTaskStep) step).getStepsList())
				{
					appendStep(portStep, section, sectionComplete, activeStep, objectives);
				}
				continue;
			}
			appendStep(step, section, sectionComplete, activeStep, objectives);
		}
	}

	private boolean isPanelComplete(PanelDetails panel)
	{
		// Quest Helper's selected lock toggle is labelled "Mark section as incomplete".
		QuestStep lockingStep = panel.getLockingQuestSteps();
		if (lockingStep == null)
		{
			return false;
		}
		try
		{
			return lockingStep.isLocked();
		}
		catch (RuntimeException ignored)
		{
			return false;
		}
	}

	private void appendStep(
		QuestStep step,
		String section,
		boolean sectionComplete,
		QuestStep activeStep,
		List<Map<String, Object>> objectives)
	{
		if (step == null || !step.isShowInSidebar() || checksTrue(step.getConditionToHide()))
		{
			return;
		}
		String text = joinStepText(step);
		if (text.isEmpty())
		{
			return;
		}

		boolean current = activeStep != null
			&& step.containsSteps(activeStep, new HashSet<>());
		String objectiveState = objectiveState(
			current,
			sectionComplete,
			checksTrue(step.getFadeCondition()));

		Map<String, Object> objective = new LinkedHashMap<>();
		objective.put("section", section);
		objective.put("text", text);
		objective.put("state", objectiveState);
		objective.put("current", current);
		objectives.add(objective);
	}

	static String objectiveState(boolean current, boolean sectionComplete, boolean faded)
	{
		if (!current && sectionComplete)
		{
			return "COMPLETE";
		}
		if (!current && faded)
		{
			return "FADED";
		}
		return "AVAILABLE";
	}

	static void markPriorObjectivesComplete(List<Map<String, Object>> objectives)
	{
		int currentIndex = -1;
		for (int index = 0; index < objectives.size(); index++)
		{
			if (Boolean.TRUE.equals(objectives.get(index).get("current")))
			{
				currentIndex = index;
				break;
			}
		}
		if (currentIndex < 0)
		{
			return;
		}
		for (int index = 0; index < currentIndex; index++)
		{
			Map<String, Object> objective = objectives.get(index);
			if ("AVAILABLE".equals(objective.get("state")))
			{
				objective.put("state", "COMPLETE");
			}
		}
	}

	static void markAllObjectivesComplete(List<Map<String, Object>> objectives)
	{
		for (Map<String, Object> objective : objectives)
		{
			objective.put("state", "COMPLETE");
			objective.put("current", false);
		}
	}

	private List<Map<String, Object>> buildRequirements(
		QuestHelper helper,
		List<Requirement> panelRequirements)
	{
		LinkedHashMap<String, Map<String, Object>> requirements = new LinkedHashMap<>();
		appendRequirements(requirements, helper.getGeneralRequirements());
		appendRequirements(requirements, helper.getItemRequirements());
		appendRequirements(requirements, panelRequirements);
		return new ArrayList<>(requirements.values());
	}

	private void appendRequirements(
		Map<String, Map<String, Object>> snapshots,
		List<? extends Requirement> requirements)
	{
		for (Requirement requirement : safeList(requirements))
		{
			if (requirement == null)
			{
				continue;
			}
			try
			{
				if (!requirement.shouldDisplayText(client))
				{
					continue;
				}
			}
			catch (RuntimeException ignored)
			{
				// Preserve the requirement with an unknown state if its display check is unavailable.
			}
			String text = cleanText(requirement.getDisplayText());
			if (text.isEmpty())
			{
				continue;
			}
			Color displayColor = requirementDisplayColor(requirement);
			String state = requirementState(requirement, displayColor);
			snapshots.put(text, requirementMap(requirement, text, state, displayColor));
		}
	}

	private Color requirementDisplayColor(Requirement requirement)
	{
		try
		{
			return requirement.getColor(client, config);
		}
		catch (RuntimeException ignored)
		{
			return null;
		}
	}

	private String requirementState(Requirement requirement, Color displayColor)
	{
		if (requirement instanceof ItemRequirement)
		{
			return itemRequirementState((ItemRequirement) requirement, displayColor);
		}
		try
		{
			String colorState = configuredColorState(displayColor);
			return "UNKNOWN".equals(colorState)
				? requirement.check(client) ? "MET" : "UNMET"
				: colorState;
		}
		catch (RuntimeException ignored)
		{
			return "UNKNOWN";
		}
	}

	private String itemRequirementState(ItemRequirement requirement, Color displayColor)
	{
		try
		{
			String colorState = configuredColorState(displayColor);
			if ("MET".equals(colorState) || requirement instanceof NoItemRequirement)
			{
				return colorState;
			}
			if (!requirement.isActualItem())
			{
				return colorState;
			}
			if (requirement instanceof ItemRequirements)
			{
				return compositeItemRequirementState((ItemRequirements) requirement);
			}

			int required = Math.max(1, requirement.getQuantity());
			int onPerson = requirement.checkTotalMatchesInContainers(
				QuestContainerManager.getEquippedData(),
				QuestContainerManager.getInventoryData());
			int total = requirement.checkTotalMatchesInContainers(
				QuestContainerManager.getOrderedListOfContainers().toArray(
					new ItemAndLastUpdated[0]));
			Set<TrackedContainers> containers = total >= required
				? requirement.getContainersWithItem()
				: Collections.emptySet();
			return itemContainerState(
				onPerson >= required,
				total >= required,
				containers.contains(TrackedContainers.GROUP_STORAGE),
				total > 0);
		}
		catch (RuntimeException ignored)
		{
			return "UNKNOWN";
		}
	}

	private String compositeItemRequirementState(ItemRequirements requirement)
	{
		boolean onPlayer = requirement.checkContainers(
			QuestContainerManager.getEquippedData(),
			QuestContainerManager.getInventoryData());
		boolean allContainers = requirement.checkWithAllContainers();
		Set<TrackedContainers> containers = itemLocations(requirement);
		return itemContainerState(
			onPlayer,
			allContainers,
			containers.contains(TrackedContainers.GROUP_STORAGE),
			!containers.isEmpty());
	}

	static String itemContainerState(
		boolean onPlayerComplete,
		boolean allContainersComplete,
		boolean includesGroupStorage,
		boolean hasSomeItems)
	{
		if (onPlayerComplete)
		{
			return "MET";
		}
		if (allContainersComplete)
		{
			return includesGroupStorage ? "GROUP_BANKED" : "BANKED";
		}
		return hasSomeItems ? "PARTIAL" : "UNMET";
	}

	String configuredColorState(Color color)
	{
		if (color == null)
		{
			return "UNKNOWN";
		}
		if (color.equals(config.passColour()))
		{
			return "MET";
		}
		if (color.equals(config.boostColour()))
		{
			return "BOOSTABLE";
		}
		if (color.equals(config.partialSuccessColour()))
		{
			return "PARTIAL";
		}
		if (color.equals(config.failColour()))
		{
			return "UNMET";
		}
		return "UNKNOWN";
	}

	Map<String, Object> requirementMap(
		Requirement requirement,
		String text,
		String state,
		Color displayColor)
	{
		Map<String, Object> snapshot = new LinkedHashMap<>();
		snapshot.put("text", text);
		snapshot.put("state", state);
		if (displayColor != null)
		{
			snapshot.put("color", displayColor.getRGB());
		}
		if (requirement instanceof ItemRequirement)
		{
			ItemRequirement itemRequirement = (ItemRequirement) requirement;
			List<String> locations = itemLocations(itemRequirement).stream()
				.map(TrackedContainers::name)
				.collect(Collectors.toList());
			if (!locations.isEmpty())
			{
				snapshot.put("locations", locations);
			}
			String wikiUrl = itemWikiUrl(itemRequirement);
			if (!wikiUrl.isEmpty())
			{
				snapshot.put("wikiUrl", wikiUrl);
			}
		}
		if (requirement instanceof QuestRequirement)
		{
			QuestHelperQuest linkedQuest = ((QuestRequirement) requirement).getQuest();
			if (linkedQuest != null)
			{
				snapshot.put("linkedQuestId", linkedQuest.name());
				snapshot.put("linkedQuestTitle", cleanText(linkedQuest.getName()));
			}
		}
		Map<String, Object> icon = requirementIcon(requirement);
		if (!icon.isEmpty())
		{
			snapshot.put("icon", icon);
		}
		return snapshot;
	}

	private String itemWikiUrl(ItemRequirement requirement)
	{
		try
		{
			if (!requirement.isActualItem())
			{
				return "";
			}
			String wikiUrl = requirement.getWikiUrl();
			return wikiUrl == null ? "" : wikiUrl.trim();
		}
		catch (RuntimeException ignored)
		{
			return "";
		}
	}

	private Map<String, Object> requirementIcon(Requirement requirement)
	{
		if (requirement instanceof QuestRequirement)
		{
			return iconMap("QUEST", null, null);
		}
		if (requirement instanceof SkillRequirement)
		{
			Skill skill = ((SkillRequirement) requirement).getSkill();
			return skill == null
				? Collections.emptyMap()
				: iconMap("SKILL", null, skill.name());
		}
		if (requirement instanceof ItemRequirement)
		{
			ItemRequirement itemRequirement = (ItemRequirement) requirement;
			Integer itemId = selectItemIconId(
				itemRequirement.getDisplayItemIds(),
				itemRequirement.getAllIds(),
				this::hasTrackedItem);
			if (itemId != null)
			{
				return iconMap("ITEM", itemId, null, Math.max(1, itemRequirement.getQuantity()));
			}
		}
		return Collections.emptyMap();
	}

	static Integer selectItemIconId(
		List<Integer> displayItemIds,
		List<Integer> allItemIds,
		Predicate<Integer> hasItem)
	{
		Objects.requireNonNull(hasItem, "hasItem");
		List<Integer> displayCandidates = validItemIds(displayItemIds);
		List<Integer> allCandidates = validItemIds(allItemIds);
		for (Integer itemId : displayCandidates)
		{
			if (hasItem.test(itemId))
			{
				return itemId;
			}
		}
		for (Integer itemId : allCandidates)
		{
			if (hasItem.test(itemId))
			{
				return itemId;
			}
		}
		if (!displayCandidates.isEmpty())
		{
			return displayCandidates.get(0);
		}
		return allCandidates.isEmpty() ? null : allCandidates.get(0);
	}

	private static List<Integer> validItemIds(List<Integer> itemIds)
	{
		if (itemIds == null)
		{
			return Collections.emptyList();
		}
		return itemIds.stream()
			.filter(Objects::nonNull)
			.filter(itemId -> itemId >= 0)
			.distinct()
			.collect(Collectors.toList());
	}

	private boolean hasTrackedItem(int itemId)
	{
		return QuestContainerManager.getOrderedListOfContainers().stream()
			.filter(container -> container.getItems() != null)
			.flatMap(container -> Arrays.stream(container.getItems()))
			.filter(Objects::nonNull)
			.anyMatch(item -> item.getId() == itemId && item.getQuantity() > 0);
	}

	private Map<String, Object> iconMap(String type, Integer itemId, String skill)
	{
		return iconMap(type, itemId, skill, null);
	}

	private Map<String, Object> iconMap(
		String type,
		Integer itemId,
		String skill,
		Integer quantity)
	{
		Map<String, Object> icon = new LinkedHashMap<>();
		icon.put("type", type);
		if (itemId != null)
		{
			icon.put("itemId", itemId);
			if (quantity != null)
			{
				icon.put("quantity", quantity);
			}
		}
		if (skill != null && !skill.isEmpty())
		{
			icon.put("skill", skill);
		}
		return Collections.unmodifiableMap(icon);
	}

	Set<TrackedContainers> itemLocations(ItemRequirement requirement)
	{
		if (requirement == null
			|| requirement instanceof NoItemRequirement
			|| !requirement.isActualItem())
		{
			return Collections.emptySet();
		}

		try
		{
			Set<TrackedContainers> satisfyingLocations = requirement.getContainersWithItem();
			if (!satisfyingLocations.isEmpty())
			{
				return new LinkedHashSet<>(satisfyingLocations);
			}

			Set<TrackedContainers> partialLocations = new LinkedHashSet<>();
			if (requirement instanceof ItemRequirements)
			{
				for (ItemRequirement child : ((ItemRequirements) requirement).getItemRequirements())
				{
					partialLocations.addAll(itemLocations(child));
				}
				return partialLocations;
			}
			for (ItemAndLastUpdated container : QuestContainerManager.getOrderedListOfContainers())
			{
				if (requirement.checkTotalMatchesInContainers(container) > 0)
				{
					partialLocations.add(container.getContainerType());
				}
			}
			return partialLocations;
		}
		catch (RuntimeException ignored)
		{
			return Collections.emptySet();
		}
	}

	private List<Map<String, Object>> buildRecommendations(
		QuestHelper helper,
		List<Requirement> panelRecommendations)
	{
		LinkedHashMap<String, Map<String, Object>> recommendations = new LinkedHashMap<>();
		appendRecommendationRequirements(recommendations, helper.getGeneralRecommended());
		appendRecommendationRequirements(recommendations, helper.getItemRecommended());
		appendRecommendationRequirements(recommendations, panelRecommendations);
		return new ArrayList<>(recommendations.values());
	}

	private void appendRecommendationRequirements(
		Map<String, Map<String, Object>> snapshots,
		List<? extends Requirement> requirements)
	{
		for (Requirement requirement : safeList(requirements))
		{
			if (requirement == null)
			{
				continue;
			}
			try
			{
				if (!requirement.shouldDisplayText(client))
				{
					continue;
				}
			}
			catch (RuntimeException ignored)
			{
				// Keep recommendations visible if their conditional display check cannot run.
			}
			String text = cleanText(requirement.getDisplayText());
			if (!text.isEmpty())
			{
				Color displayColor = requirementDisplayColor(requirement);
				String state = requirementState(requirement, displayColor);
				snapshots.put(text, requirementMap(requirement, text, state, displayColor));
			}
		}
	}

	List<String> buildEnemies(QuestHelper helper)
	{
		LinkedHashSet<String> enemies = new LinkedHashSet<>();
		for (String combatRequirement : safeList(helper.getCombatRequirements()))
		{
			String text = cleanText(combatRequirement);
			if (!text.isEmpty())
			{
				enemies.add(text);
			}
		}
		return new ArrayList<>(enemies);
	}

	private List<Map<String, Object>> buildRewards(QuestHelper helper)
	{
		List<Map<String, Object>> rewards = new ArrayList<>();
		for (Reward reward : safeList(helper.getQuestRewards()))
		{
			if (reward == null)
			{
				continue;
			}
			String detail = cleanText(reward.getDisplayText());
			if (detail.isEmpty())
			{
				continue;
			}
			Map<String, Object> snapshot = new LinkedHashMap<>();
			snapshot.put("title", prettyName(reward.rewardType().name()));
			snapshot.put("detail", detail);
			Map<String, Object> icon = rewardIcon(reward);
			if (!icon.isEmpty())
			{
				snapshot.put("icon", icon);
			}
			rewards.add(snapshot);
		}
		return rewards;
	}

	Map<String, Object> rewardIcon(Reward reward)
	{
		if (reward instanceof ItemReward)
		{
			ItemReward itemReward = (ItemReward) reward;
			int itemId = itemReward.getItemID();
			return itemId < 0
				? Collections.emptyMap()
				: iconMap("ITEM", itemId, null, Math.max(1, itemReward.getQuantity()));
		}
		if (reward instanceof ExperienceReward)
		{
			ExperienceReward experienceReward = (ExperienceReward) reward;
			return experienceReward.getSkill() == null
				? Collections.emptyMap()
				: iconMap("SKILL", null, experienceReward.getSkill().name());
		}
		if (reward instanceof QuestPointReward)
		{
			return iconMap("QUEST_POINTS", null, null);
		}
		return Collections.emptyMap();
	}

	private String joinStepText(QuestStep step)
	{
		if (step == null)
		{
			return "";
		}
		return safeList(step.getText()).stream()
			.map(this::cleanText)
			.filter(value -> !value.isEmpty())
			.collect(Collectors.joining("\n\n"));
	}

	private List<String> cleanStrings(List<String> values)
	{
		return safeList(values).stream()
			.map(this::cleanText)
			.filter(value -> !value.isEmpty())
			.collect(Collectors.toList());
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

	private String cleanText(String text)
	{
		if (text == null)
		{
			return "";
		}
		String withLines = text
			.replace("</br>", "\n")
			.replace("<br>", "\n")
			.replace("<br/>", "\n");
		return Text.removeTags(withLines)
			.replace('\u00A0', ' ')
			.trim();
	}

	private String stateName(QuestState state)
	{
		return state == QuestState.FINISHED ? "COMPLETE" : state.name();
	}

	private boolean isMembers(QuestDetails.Type type)
	{
		return type == QuestDetails.Type.P2P
			|| type == QuestDetails.Type.MINIQUEST
			|| type == QuestDetails.Type.ACHIEVEMENT_DIARY
			|| type == QuestDetails.Type.SKILL_P2P;
	}

	static String journalType(QuestDetails.Type type)
	{
		switch (type)
		{
			case MINIQUEST:
				return "MINIQUEST";
			case ACHIEVEMENT_DIARY:
				return "ACHIEVEMENT_DIARY";
			case GENERIC:
				return "GENERIC";
			case SKILL:
			case SKILL_F2P:
			case SKILL_P2P:
				return "SKILL";
			case PLAYER_QUEST:
				return "PLAYER_QUEST";
			case F2P:
			case P2P:
			default:
				return "QUEST";
		}
	}

	static String journalDifficulty(QuestHelperQuest quest)
	{
		QuestDetails.Difficulty difficulty = quest.getDifficulty();
		if (journalType(quest.getQuestType()).equals("QUEST")
			&& isCanonicalDifficulty(difficulty))
		{
			return difficulty.name();
		}
		String diaryTier = achievementDiaryTier(quest);
		return diaryTier == null ? "SPECIAL" : diaryTier;
	}

	static String achievementDiaryTier(QuestHelperQuest quest)
	{
		if (quest == null || quest.getQuestType() != QuestDetails.Type.ACHIEVEMENT_DIARY)
		{
			return null;
		}
		String enumName = quest.name();
		String displayName = quest.getName() == null
			? ""
			: " " + quest.getName().toUpperCase(Locale.ROOT) + " ";
		for (String tier : Arrays.asList("EASY", "MEDIUM", "HARD", "ELITE"))
		{
			if (enumName.endsWith("_" + tier)
				|| displayName.contains(" " + tier + " DIARY "))
			{
				return tier;
			}
		}
		return null;
	}

	private static boolean isCanonicalDifficulty(QuestDetails.Difficulty difficulty)
	{
		return difficulty == QuestDetails.Difficulty.NOVICE
			|| difficulty == QuestDetails.Difficulty.INTERMEDIATE
			|| difficulty == QuestDetails.Difficulty.EXPERIENCED
			|| difficulty == QuestDetails.Difficulty.MASTER
			|| difficulty == QuestDetails.Difficulty.GRANDMASTER;
	}

	private String configuredType(
		QuestHelperConfig.QuestFilter filter,
		QuestDetails.Difficulty legacyDifficulty)
	{
		if (filter != null)
		{
			switch (filter)
			{
				case QUEST:
					return "QUEST";
				case MINIQUEST:
					return "MINIQUEST";
				case ACHIEVEMENT_DIARY:
					return "ACHIEVEMENT_DIARY";
				case GENERIC_HELPER:
					return "GENERIC";
				case SKILL_HELPER:
				case SKILL_FREE_TO_PLAY:
				case SKILL_MEMBERS:
					return "SKILL";
				case PLAYER_MADE_QUESTS:
					return "PLAYER_QUEST";
				default:
					break;
			}
		}
		if (legacyDifficulty != null)
		{
			switch (legacyDifficulty)
			{
				case MINIQUEST:
				case ACHIEVEMENT_DIARY:
				case GENERIC:
				case SKILL:
				case PLAYER_QUEST:
					return legacyDifficulty.name();
				default:
					break;
			}
		}
		return "ALL";
	}

	private String currentMembership()
	{
		return client.getWorldType() != null && client.getWorldType().contains(WorldType.MEMBERS)
			? "MEMBERS"
			: "FREE_TO_PLAY";
	}

	private String currentOrder()
	{
		AccountType accountType = AccountType.get(client.getVarbitValue(VarbitID.IRONMAN));
		return accountType != null && accountType.isAnyIronman()
			? QuestOrdering.OPTIMAL_IRONMAN.name()
			: QuestOrdering.OPTIMAL.name();
	}

	private String prettyName(String value)
	{
		String lower = Objects.requireNonNull(value).toLowerCase(Locale.ROOT).replace('_', ' ');
		StringBuilder result = new StringBuilder(lower.length());
		boolean capitalize = true;
		for (int index = 0; index < lower.length(); index++)
		{
			char character = lower.charAt(index);
			result.append(capitalize ? Character.toUpperCase(character) : character);
			capitalize = character == ' ';
		}
		return result.toString();
	}

	private static <T> List<T> safeList(List<T> values)
	{
		return values == null ? Collections.emptyList() : values;
	}
}
