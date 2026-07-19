package com.questhelper.bridge;

import com.questhelper.QuestHelperConfig;
import com.questhelper.QuestHelperConfig.QuestOrdering;
import com.questhelper.managers.QuestManager;
import com.questhelper.questhelpers.QuestDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.TrackedContainers;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.WorldType;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.events.PluginMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QuestJournalBridgeTest
{
	@Test
	void shutdownPublishesStoppedBeforeProducerUnregisters()
	{
		Harness harness = new Harness();
		harness.bridge.startUp();
		harness.post(QuestJournalBridge.STATE_REQUEST, "consumer-a");

		PluginMessage ready = harness.lastState();
		harness.bridge.shutDown();
		PluginMessage stopped = harness.lastState();

		assertEquals("LOGGED_OUT", ready.getData().get("status"));
		assertEquals("STOPPED", stopped.getData().get("status"));
		assertEquals(ready.getData().get("sessionId"), stopped.getData().get("sessionId"));
		assertEquals(2L, ((Number) stopped.getData().get("revision")).longValue());
	}

	@Test
	void restartCreatesNewSessionWithFreshRevision()
	{
		Harness harness = new Harness();
		harness.bridge.startUp();
		harness.post(QuestJournalBridge.STATE_REQUEST, "consumer-a");
		PluginMessage first = harness.lastState();
		harness.bridge.shutDown();

		harness.bridge.startUp();
		harness.post(QuestJournalBridge.STATE_REQUEST, "consumer-a");
		PluginMessage restarted = harness.lastState();

		assertNotEquals(first.getData().get("sessionId"), restarted.getData().get("sessionId"));
		assertEquals(1L, ((Number) restarted.getData().get("revision")).longValue());
		harness.bridge.shutDown();
	}

	@Test
	void unsubscribeStopsPerTickPublicationAndShutdownDelivery()
	{
		Harness harness = new Harness();
		harness.bridge.startUp();
		harness.post(QuestJournalBridge.STATE_REQUEST, "consumer-a");
		int subscribedStates = harness.states().size();

		harness.post(QuestJournalBridge.UNSUBSCRIBE, "consumer-a");
		harness.bridge.publishIfChanged();
		harness.bridge.shutDown();

		assertEquals(subscribedStates, harness.states().size());
	}

	@Test
	void publishesIndependentTypeDifficultyMembershipAndOrderingOptions()
	{
		Harness harness = new Harness();
		Map<String, Object> options = harness.bridge.buildListOptions();

		assertEquals(
			Arrays.asList(
				"QUEST",
				"MINIQUEST",
				"ACHIEVEMENT_DIARY",
				"GENERIC",
				"SKILL",
				"PLAYER_QUEST"),
			options.get("types"));
		assertEquals(
			Arrays.asList(
				"NOVICE",
				"INTERMEDIATE",
				"EXPERIENCED",
				"MASTER",
				"GRANDMASTER",
				"EASY",
				"MEDIUM",
				"HARD",
				"ELITE"),
			options.get("difficulties"));
		assertEquals(Arrays.asList("FREE_TO_PLAY", "MEMBERS"), options.get("memberships"));
		assertEquals(
			Arrays.asList(
				"A_TO_Z",
				"Z_TO_A",
				"OPTIMAL",
				"OPTIMAL_IRONMAN",
				"RELEASE_DATE",
				"QUEST_POINTS_ASC",
				"QUEST_POINTS_DESC"),
			options.get("orders"));
		assertEquals("ALL", options.get("configuredDifficulty"));
		assertEquals("A_TO_Z", options.get("configuredOrder"));
		assertEquals("ALL", options.get("configuredType"));
		assertEquals("FREE_TO_PLAY", options.get("defaultMembership"));
		assertEquals("OPTIMAL", options.get("defaultOrder"));
	}

	@Test
	void accountDefaultsCoverMembersAndEveryIronmanVariant()
	{
		Harness harness = new Harness();
		when(harness.client.getWorldType()).thenReturn(EnumSet.of(WorldType.MEMBERS));
		for (int accountType = 1; accountType <= 6; accountType++)
		{
			when(harness.client.getVarbitValue(VarbitID.IRONMAN)).thenReturn(accountType);
			Map<String, Object> options = harness.bridge.buildListOptions();
			assertEquals("MEMBERS", options.get("defaultMembership"));
			assertEquals("OPTIMAL_IRONMAN", options.get("defaultOrder"));
		}
	}

	@Test
	void legacyQuestHelperSelectionsMapOntoIndependentJournalFacets()
	{
		Harness harness = new Harness();
		when(harness.config.filterListBy()).thenReturn(QuestHelperConfig.QuestFilter.MINIQUEST);
		when(harness.config.difficulty()).thenReturn(QuestDetails.Difficulty.MINIQUEST);
		when(harness.config.orderListBy()).thenReturn(QuestOrdering.RELEASE_DATE);

		Map<String, Object> options = harness.bridge.buildListOptions();

		assertEquals("MINIQUEST", options.get("configuredType"));
		assertEquals("ALL", options.get("configuredDifficulty"));
		assertEquals("RELEASE_DATE", options.get("configuredOrder"));
		assertEquals("OPTIMAL", options.get("defaultOrder"));
	}

	@Test
	void specialHelperKindsArePublishedAsTypesRatherThanDifficulties()
	{
		assertEquals("QUEST", QuestJournalBridge.journalType(
			QuestHelperQuest.COOKS_ASSISTANT.getQuestType()));
		assertEquals("NOVICE", QuestJournalBridge.journalDifficulty(
			QuestHelperQuest.COOKS_ASSISTANT));
		assertEquals("MINIQUEST", QuestJournalBridge.journalType(
			QuestHelperQuest.ENTER_THE_ABYSS.getQuestType()));
		assertEquals("SPECIAL", QuestJournalBridge.journalDifficulty(
			QuestHelperQuest.ENTER_THE_ABYSS));
		assertEquals("ACHIEVEMENT_DIARY", QuestJournalBridge.journalType(
			QuestHelperQuest.ARDOUGNE_EASY.getQuestType()));
		assertEquals("EASY", QuestJournalBridge.journalDifficulty(
			QuestHelperQuest.ARDOUGNE_EASY));
	}

	@Test
	void everyAchievementDiaryPublishesItsTierFromStableQuestMetadata()
	{
		List<String> tiers = Arrays.asList("EASY", "MEDIUM", "HARD", "ELITE");
		for (QuestHelperQuest quest : QuestHelperQuest.values())
		{
			if (quest.getQuestType() != QuestDetails.Type.ACHIEVEMENT_DIARY)
			{
				continue;
			}
			String tier = QuestJournalBridge.achievementDiaryTier(quest);
			assertTrue(tiers.contains(tier), quest.name() + " must publish a diary tier");
			assertEquals(tier, QuestJournalBridge.journalDifficulty(quest));
		}
	}

	@Test
	void journalListDoesNotApplyQuestHelperSidebarSkillExclusions()
	{
		Harness harness = new Harness();
		QuestHelper helper = mock(QuestHelper.class);
		when(helper.getGeneralRequirements()).thenThrow(
			new AssertionError("The journal bridge must not consult sidebar skill exclusions"));

		assertEquals(
			java.util.Collections.singletonList(helper),
			harness.bridge.filterEligibleQuestList(java.util.Collections.singletonList(helper)));
	}

	@Test
	void publishesStableRanksForEverySupportedOrder()
	{
		Harness harness = new Harness();
		QuestHelper dragonSlayer = helper(QuestHelperQuest.DRAGON_SLAYER_I);
		QuestHelper cooksAssistant = helper(QuestHelperQuest.COOKS_ASSISTANT);
		QuestHelper miniquest = helper(QuestHelperQuest.ENTER_THE_ABYSS);
		QuestHelper diary = helper(QuestHelperQuest.ARDOUGNE_EASY);
		QuestHelper skill = helper(QuestHelperQuest.AGILITY);
		QuestHelper generic = helper(QuestHelperQuest.HERB_RUN);
		QuestHelper playerQuest = helper(QuestHelperQuest.BIKE_SHEDDER);

		Map<QuestHelperQuest, Map<String, Object>> ranks = harness.bridge.buildOrderRanks(
			Arrays.asList(
				dragonSlayer,
				cooksAssistant,
				miniquest,
				diary,
				skill,
				generic,
				playerQuest));

		assertEquals(QuestOrdering.values().length, ranks.get(QuestHelperQuest.COOKS_ASSISTANT).size());
		assertEquals(0, ranks.get(QuestHelperQuest.COOKS_ASSISTANT).get("A_TO_Z"));
		assertEquals(2, ranks.get(QuestHelperQuest.ENTER_THE_ABYSS).get("A_TO_Z"));
		assertEquals(3, ranks.get(QuestHelperQuest.ARDOUGNE_EASY).get("A_TO_Z"));
		assertEquals(4, ranks.get(QuestHelperQuest.AGILITY).get("A_TO_Z"));
		assertEquals(5, ranks.get(QuestHelperQuest.HERB_RUN).get("A_TO_Z"));
		assertEquals(6, ranks.get(QuestHelperQuest.BIKE_SHEDDER).get("A_TO_Z"));
		assertTrue(ranks.get(QuestHelperQuest.HERB_RUN).containsKey("OPTIMAL_IRONMAN"));
		assertTrue(!ranks.get(QuestHelperQuest.AGILITY).containsKey("OPTIMAL_IRONMAN"));
		assertTrue(!ranks.get(QuestHelperQuest.BIKE_SHEDDER).containsKey("OPTIMAL_IRONMAN"));
	}

	@Test
	void completedSectionAndUnavailableFadedStepKeepDistinctStates()
	{
		assertEquals("COMPLETE", QuestJournalBridge.objectiveState(false, true, false));
		assertEquals("FADED", QuestJournalBridge.objectiveState(false, false, true));
		assertEquals("AVAILABLE", QuestJournalBridge.objectiveState(true, true, true));
	}

	@Test
	void objectivesBeforeCurrentBecomeCompleteWithoutChangingFadedSteps()
	{
		List<Map<String, Object>> objectives = new ArrayList<>();
		objectives.add(objective("AVAILABLE", false));
		objectives.add(objective("FADED", false));
		objectives.add(objective("AVAILABLE", true));
		objectives.add(objective("AVAILABLE", false));

		QuestJournalBridge.markPriorObjectivesComplete(objectives);

		assertEquals("COMPLETE", objectives.get(0).get("state"));
		assertEquals("FADED", objectives.get(1).get("state"));
		assertEquals("AVAILABLE", objectives.get(2).get("state"));
		assertEquals("AVAILABLE", objectives.get(3).get("state"));
	}

	@Test
	void completedQuestMarksEveryPublishedObjectiveCompleteAndNoneCurrent()
	{
		List<Map<String, Object>> objectives = new ArrayList<>();
		objectives.add(objective("AVAILABLE", false));
		objectives.add(objective("FADED", true));
		objectives.add(objective("COMPLETE", false));

		QuestJournalBridge.markAllObjectivesComplete(objectives);

		for (Map<String, Object> objective : objectives)
		{
			assertEquals("COMPLETE", objective.get("state"));
			assertEquals(false, objective.get("current"));
		}
	}

	@Test
	void itemContainerStatesDistinguishPersonBankGroupAndPartialQuantity()
	{
		assertEquals("MET", QuestJournalBridge.itemContainerState(true, true, false, true));
		assertEquals("BANKED", QuestJournalBridge.itemContainerState(false, true, false, true));
		assertEquals("GROUP_BANKED", QuestJournalBridge.itemContainerState(false, true, true, true));
		assertEquals("PARTIAL", QuestJournalBridge.itemContainerState(false, false, false, true));
		assertEquals("UNMET", QuestJournalBridge.itemContainerState(false, false, false, false));
	}

	@Test
	void partialDisplayColorDoesNotMasqueradeAsBankStorage()
	{
		Harness harness = new Harness();
		Color partial = new Color(255, 160, 0);
		when(harness.config.partialSuccessColour()).thenReturn(partial);

		assertEquals("PARTIAL", harness.bridge.configuredColorState(partial));
		assertEquals("BANKED", QuestJournalBridge.itemContainerState(false, true, false, true));
	}

	@Test
	void requirementsPublishQuestTargetsAndConcreteItemLocations()
	{
		Harness harness = new Harness();
		QuestRequirement questRequirement = new QuestRequirement(
			QuestHelperQuest.COOKS_ASSISTANT,
			QuestState.FINISHED);
		Map<String, Object> questSnapshot = harness.bridge.requirementMap(
			questRequirement,
			"Complete Cook's Assistant",
			"UNMET",
			null);

		assertEquals("COOKS_ASSISTANT", questSnapshot.get("linkedQuestId"));
		assertEquals("Cook's Assistant", questSnapshot.get("linkedQuestTitle"));
		assertEquals("QUEST", ((Map<?, ?>) questSnapshot.get("icon")).get("type"));

		ItemRequirement itemRequirement = mock(ItemRequirement.class);
		when(itemRequirement.isActualItem()).thenReturn(true);
		when(itemRequirement.getDisplayItemIds()).thenReturn(Arrays.asList(1351));
		when(itemRequirement.getAllIds()).thenReturn(Arrays.asList(1351));
		when(itemRequirement.getQuantity()).thenReturn(3);
		when(itemRequirement.getWikiUrl()).thenReturn(
			"https://oldschool.runescape.wiki/w/Special:Lookup?type=item&id=1351#Item_sources");
		when(itemRequirement.getContainersWithItem()).thenReturn(
			new LinkedHashSet<>(Arrays.asList(TrackedContainers.BANK, TrackedContainers.GROUP_STORAGE)));
		Map<String, Object> itemSnapshot = harness.bridge.requirementMap(
			itemRequirement,
			"Bronze axe",
			"UNMET",
			null);
		assertEquals(
			"https://oldschool.runescape.wiki/w/Special:Lookup?type=item&id=1351#Item_sources",
			itemSnapshot.get("wikiUrl"));
		assertEquals(
			new LinkedHashSet<>(Arrays.asList(TrackedContainers.BANK, TrackedContainers.GROUP_STORAGE)),
			harness.bridge.itemLocations(itemRequirement));
		Map<?, ?> itemIcon = (Map<?, ?>) itemSnapshot.get("icon");
		assertEquals("ITEM", itemIcon.get("type"));
		assertEquals(1351, itemIcon.get("itemId"));
		assertEquals(3, itemIcon.get("quantity"));
	}

	@Test
	void skillRequirementsPublishRuneLiteSkillIdentity()
	{
		Harness harness = new Harness();
		Map<String, Object> snapshot = harness.bridge.requirementMap(
			new SkillRequirement(Skill.COOKING, 10),
			"10 Cooking",
			"UNMET",
			null);

		Map<?, ?> icon = (Map<?, ?>) snapshot.get("icon");
		assertEquals("SKILL", icon.get("type"));
		assertEquals(Skill.COOKING.name(), icon.get("skill"));
	}

	@Test
	void enemiesArePublishedSeparatelyAndDeduplicated()
	{
		Harness harness = new Harness();
		QuestHelper helper = mock(QuestHelper.class);
		when(helper.getCombatRequirements()).thenReturn(Arrays.asList(
			"Elvarg (level 83)",
			"Elvarg (level 83)",
			"  Lesser demon  "));

		assertEquals(
			Arrays.asList("Elvarg (level 83)", "Lesser demon"),
			harness.bridge.buildEnemies(helper));
	}

	@Test
	void concreteOwnedItemIconWinsWithStablePrimaryFallback()
	{
		assertEquals(
			Integer.valueOf(1351),
			QuestJournalBridge.selectItemIconId(
				Arrays.asList(1349, 1351, 1353),
				Arrays.asList(1349, 1351, 1353),
				itemId -> itemId == 1351));
		assertEquals(
			Integer.valueOf(1349),
			QuestJournalBridge.selectItemIconId(
				Arrays.asList(1349, 1351, 1353),
				Arrays.asList(1349, 1351, 1353),
				itemId -> false));
	}

	@Test
	void rewardsPublishNativeItemSkillAndQuestPointIcons()
	{
		Harness harness = new Harness();
		ItemReward itemReward = new ItemReward("Coins", 995, 5);
		Map<String, Object> itemIcon = harness.bridge.rewardIcon(itemReward);

		assertEquals(995, itemReward.getItemID());
		assertEquals(5, itemReward.getQuantity());
		assertEquals("ITEM", itemIcon.get("type"));
		assertEquals(995, itemIcon.get("itemId"));
		assertEquals(5, itemIcon.get("quantity"));
		assertEquals(
			"WOODCUTTING",
			harness.bridge.rewardIcon(new ExperienceReward(Skill.WOODCUTTING, 100)).get("skill"));
		assertEquals(
			"QUEST_POINTS",
			harness.bridge.rewardIcon(new QuestPointReward(1)).get("type"));
	}

	@Test
	void progressUsesGlobalCoreQuestCountsAndAuthoritativeQuestPointVariables()
	{
		Harness harness = new Harness();
		when(harness.client.getVarpValue(VarPlayerID.QP)).thenReturn(128);
		when(harness.client.getVarbitValue(VarbitID.QP_MAX)).thenReturn(320);
		QuestHelper completed = helper(QuestHelperQuest.COOKS_ASSISTANT);
		when(completed.getState(any(Client.class))).thenReturn(QuestState.FINISHED);
		when(completed.getQuestPointReward()).thenReturn(new QuestPointReward(1));
		QuestHelper incomplete = helper(QuestHelperQuest.DRAGON_SLAYER_I);
		when(incomplete.getState(any(Client.class))).thenReturn(QuestState.IN_PROGRESS);
		when(incomplete.getQuestPointReward()).thenReturn(new QuestPointReward(2));
		QuestHelper completedMiniquest = helper(QuestHelperQuest.ENTER_THE_ABYSS);
		when(completedMiniquest.getState(any(Client.class))).thenReturn(QuestState.FINISHED);
		when(completedMiniquest.getQuestPointReward()).thenReturn(new QuestPointReward(99));

		Map<String, Object> progress = harness.bridge.buildProgress(
			Arrays.asList(completed, incomplete, completedMiniquest));

		assertEquals(1, progress.get("completedQuestCount"));
		assertEquals(2, progress.get("totalQuestCount"));
		assertEquals(128, progress.get("currentQuestPoints"));
		assertEquals(320, progress.get("totalQuestPoints"));
		assertTrue(QuestJournalBridge.isCoreQuest(completed));
		assertFalse(QuestJournalBridge.isCoreQuest(completedMiniquest));
	}

	@Test
	void progressFallsBackToCoreRewardsAndNeverPublishesMaximumBelowCurrent()
	{
		Harness harness = new Harness();
		QuestHelper first = helper(QuestHelperQuest.COOKS_ASSISTANT);
		when(first.getState(any(Client.class))).thenReturn(QuestState.FINISHED);
		when(first.getQuestPointReward()).thenReturn(new QuestPointReward(1));
		QuestHelper second = helper(QuestHelperQuest.DRAGON_SLAYER_I);
		when(second.getState(any(Client.class))).thenReturn(QuestState.NOT_STARTED);
		when(second.getQuestPointReward()).thenReturn(new QuestPointReward(2));

		when(harness.client.getVarpValue(VarPlayerID.QP)).thenReturn(2);
		when(harness.client.getVarbitValue(VarbitID.QP_MAX)).thenReturn(0);
		Map<String, Object> fallback = harness.bridge.buildProgress(Arrays.asList(first, second));
		assertEquals(2, fallback.get("currentQuestPoints"));
		assertEquals(3, fallback.get("totalQuestPoints"));

		when(harness.client.getVarpValue(VarPlayerID.QP)).thenReturn(5);
		when(harness.client.getVarbitValue(VarbitID.QP_MAX)).thenReturn(1);
		Map<String, Object> guarded = harness.bridge.buildProgress(Arrays.asList(first, second));
		assertEquals(5, guarded.get("currentQuestPoints"));
		assertEquals(5, guarded.get("totalQuestPoints"));
	}

	@Test
	void questListRetainsCompletedEntriesWhenQuestHelperSidebarHidesThem()
	{
		Harness harness = new Harness();
		when(harness.config.showCompletedQuests()).thenReturn(false);
		QuestHelper completed = helper(QuestHelperQuest.COOKS_ASSISTANT);
		when(completed.getDisplayedQuestName()).thenReturn("Cook's Assistant");
		when(completed.getState(any(Client.class))).thenReturn(QuestState.FINISHED);

		List<Map<String, Object>> quests = harness.bridge.buildQuestList(
			java.util.Collections.singletonList(completed));

		assertEquals(1, quests.size());
		assertEquals("COMPLETE", quests.get(0).get("state"));
	}

	private static QuestHelper helper(QuestHelperQuest quest)
	{
		QuestHelper helper = mock(QuestHelper.class);
		when(helper.getQuest()).thenReturn(quest);
		return helper;
	}

	private static Map<String, Object> objective(String state, boolean current)
	{
		Map<String, Object> objective = new java.util.LinkedHashMap<>();
		objective.put("state", state);
		objective.put("current", current);
		return objective;
	}

	private static final class Harness
	{
		private final EventBus eventBus = new EventBus();
		private final List<PluginMessage> messages = new ArrayList<>();
		private final Client client = mock(Client.class);
		private final QuestHelperConfig config = mock(QuestHelperConfig.class);
		private final QuestJournalBridge bridge;

		private Harness()
		{
			when(client.getGameState()).thenReturn(GameState.LOGIN_SCREEN);
			ClientThread clientThread = mock(ClientThread.class);
			doAnswer(invocation ->
			{
				invocation.<Runnable>getArgument(0).run();
				return null;
			}).when(clientThread).invokeLater(any(Runnable.class));

			eventBus.register(PluginMessage.class, messages::add, 0f);
			bridge = new QuestJournalBridge(
				client,
				clientThread,
				eventBus,
				mock(QuestManager.class),
				config);
		}

		private void post(String name, String consumerId)
		{
			eventBus.post(new PluginMessage(
				QuestJournalBridge.NAMESPACE,
				name,
				Map.of("consumerId", consumerId)));
		}

		private List<PluginMessage> states()
		{
			List<PluginMessage> states = new ArrayList<>();
			for (PluginMessage message : messages)
			{
				if (QuestJournalBridge.STATE_CHANGED.equals(message.getName()))
				{
					states.add(message);
				}
			}
			return states;
		}

		private PluginMessage lastState()
		{
			List<PluginMessage> states = states();
			assertTrue(!states.isEmpty());
			return states.get(states.size() - 1);
		}
	}
}
