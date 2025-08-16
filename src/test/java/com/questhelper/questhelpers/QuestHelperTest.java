package com.questhelper.questhelpers;

import com.questhelper.MockedTest;
import com.questhelper.domain.AccountType;
import com.questhelper.panel.QuestOverviewPanel;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.statemanagement.AchievementDiaryStepManager;
import com.questhelper.steps.OwnerStep;
import com.questhelper.steps.QuestStep;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class QuestHelperTest extends MockedTest
{
	private void testRequirements(QuestHelperQuest[] quests)
	{
		for (var quest : quests)
		{
			// Instantiate helper
			var helper = quest.getQuestHelper();
			helper.setQuest(quest);
			this.injector.injectMembers(helper);
			helper.setQuestHelperPlugin(questHelperPlugin);
			helper.initializeRequirements();

			var itemRecommended = helper.getItemRecommended();
			if (itemRecommended != null)
			{
				for (var requirement : itemRecommended)
				{
					assertNotNull(requirement, String.format("Requirement for quest %s must not be null", quest.getName()));
				}
			}

			var itemRequirements = helper.getItemRequirements();
			if (itemRequirements != null)
			{
				for (var requirement : itemRequirements)
				{
					assertNotNull(requirement, String.format("Requirement for quest %s must not be null", quest.getName()));
				}
			}

			var generalRecommended = helper.getGeneralRecommended();
			if (generalRecommended != null)
			{
				for (var requirement : generalRecommended)
				{
					assertNotNull(requirement, String.format("Requirement for quest %s must not be null", quest.getName()));
				}
			}

			var generalRequirements = helper.getGeneralRequirements();
			if (generalRequirements != null)
			{
				for (var requirement : generalRequirements)
				{
					assertNotNull(requirement, String.format("Requirement for quest %s must not be null", quest.getName()));
					requirement.check(client);
				}
			}
		}
	}

	@Test
	void ensureRequirementsSetUpBySetupRequirementsStandard()
	{
		when(playerStateManager.getAccountType()).thenReturn(AccountType.NORMAL);
		when(client.getRealSkillLevel(any())).thenReturn(1);
		testRequirements(QuestHelperQuest.values());
	}

	@Test
	void ensureRequirementsSetUpBySetupRequirementsMaxed()
	{
		when(playerStateManager.getAccountType()).thenReturn(AccountType.NORMAL);
		when(client.getRealSkillLevel(any())).thenReturn(99);
		testRequirements(QuestHelperQuest.values());
	}

	@Test
	void ensureRequirementsSetUpBySetupRequirementsIronStandard()
	{
		when(client.getRealSkillLevel(any())).thenReturn(1);
		when(playerStateManager.getAccountType()).thenReturn(AccountType.IRONMAN);
		testRequirements(QuestHelperQuest.values());
	}

	@Test
	void ensureRequirementsSetUpBySetupRequirementsIronMaxed()
	{
		when(client.getRealSkillLevel(any())).thenReturn(99);
		when(playerStateManager.getAccountType()).thenReturn(AccountType.IRONMAN);
		testRequirements(QuestHelperQuest.values());
	}

	@Test
	void ensureRequirementsSetUpBySetupRequirementsUIMStandard()
	{
		when(client.getRealSkillLevel(any())).thenReturn(1);
		when(playerStateManager.getAccountType()).thenReturn(AccountType.ULTIMATE_IRONMAN);
		testRequirements(QuestHelperQuest.values());
	}

	@Test
	void ensureRequirementsSetUpBySetupRequirementsUIMMaxed()
	{
		when(client.getRealSkillLevel(any())).thenReturn(99);
		when(playerStateManager.getAccountType()).thenReturn(AccountType.ULTIMATE_IRONMAN);
		testRequirements(QuestHelperQuest.values());
	}

	@Test
	void ensureAllVariablesCorrectlySet()
	{
		when(client.getIntStack()).thenReturn(new int[]{1, 1, 1, 1});
		when(questHelperConfig.solvePuzzles()).thenReturn(true);

		AchievementDiaryStepManager.setup(configManager);

		for (var quest : QuestHelperQuest.values())
		{
			var helper = quest.getQuestHelper();
			if (quest.getPlayerQuests() != null)
			{
				continue;
			}

			helper.setQuest(quest);
			this.injector.injectMembers(helper);
			helper.setQuestHelperPlugin(questHelperPlugin);
			helper.setConfig(questHelperConfig);
			helper.init();

			Field[] fields = helper.getClass().getDeclaredFields();
			for (Field field : fields)
			{
				// Make private fields accessible
				field.setAccessible(true);

				if (Requirement.class.isAssignableFrom(field.getType()))
				{
					// Get the value of the field for the current quest instance
					try
					{
						Object value = field.get(helper);

						assertNotNull(value, String.format("Field %s in %s must not be null", field.getName(), quest.getClass().getSimpleName()));

						((Requirement) value).check(client);
						if (value instanceof ItemRequirement)
						{
							assertNotNull(((ItemRequirement) value).getConditionToHide(), String.format("conditionToHide for quest %s must not be null", quest.getName()));
						}
						if (value instanceof ZoneRequirement)
						{
							assertFalse(((ZoneRequirement) value).getZones().contains(null), String.format("Zones for ZoneRequirements in quest %s must not be null", quest.getName()));
						}
					}
					catch (IllegalAccessException err)
					{
						assertNotNull(null);
					}
				}
			}
		}
	}

	void checkStep(QuestHelper helper, QuestOverviewPanel questOverviewPanel, boolean shouldError, QuestStep step) {
		var rawText = step.getText();
		var text = rawText == null ? "" : String.join("\n", rawText);

		questOverviewPanel.updateHighlight(this.client, step); // getactivestep?

		// All steps must have at least one category/step that's erected
		// If all panels are collapsed, it means the step this fails on needs to be either:
		// 1. Added as a substep to another step
		// 2. Added as a panel step
		if (shouldError)
		{
			assertFalse(questOverviewPanel.isAllCollapsed(), String.format("Quest(%s) step(%s) is missing a side panel step", helper.getQuest().getName(), text));
		}
		else
		{
			if (questOverviewPanel.isAllCollapsed())
			{
				System.out.format("For quest %s, step '%s' is missing sub steps or should be added to panel\n", helper.getQuest(), text);
			}
		}
	}

	void checkSteps(QuestHelper helper, QuestOverviewPanel questOverviewPanel, boolean shouldError, Set<QuestStep> checkedSteps, QuestStep step)
	{
		assertNotNull(step);

		if (step instanceof OwnerStep)
		{
			helper.startUpStep(step);
			for (var innerStep : ((OwnerStep) step).getSteps())
			{
				if (checkedSteps.contains(innerStep)) continue;
				checkedSteps.add(innerStep);
				helper.startUpStep(innerStep);
				checkSteps(helper, questOverviewPanel, shouldError, checkedSteps, innerStep);
				helper.shutDownStep();
			}
			helper.shutDownStep();
		}
		else
		{
			checkStep(helper, questOverviewPanel, shouldError, step);
		}
	}

	/*
	@Test
	void testSingleStep()
	{
		var quest = QuestHelperQuest.MISTHALIN_MYSTERY;
		var shouldError = false;

		when(questHelperConfig.solvePuzzles()).thenReturn(true);

		AchievementDiaryStepManager.setup(configManager);

		var helper = Mockito.spy(quest.getQuestHelper());
		helper.setQuest(quest);

		this.injector.injectMembers(helper);
		helper.setInjector(this.injector);
		helper.setQuestHelperPlugin(questHelperPlugin);
		helper.setConfig(questHelperConfig);
		helper.init();
		helper.startUp(questHelperConfig);

		when(this.questHelperPlugin.getSelectedQuest()).thenReturn(helper);

		var questOverviewPanel = new QuestOverviewPanel(this.questHelperPlugin, this.questHelperPlugin.getQuestManager());

		var realHelper = (MisthalinMystery) helper;

		var step = realHelper.playD;
		helper.startUpStep(realHelper.puzzlePlayPiano);
		helper.startUpStep(step);

		checkStep(helper, questOverviewPanel, shouldError, step);
	}
	 */

	/// The intent of this test is to ensure that each potentially reachable step in a quest helper has a sidebar
	/// step associated with it.
	///
	/// Because of the complexity of this, I've added the `optedInQuests` set which is the list of quests that
	/// have opted in for errors, so if any of those steps in those quests don't have a sidebar steps,
	/// then we'll fail.
	/// Other quests that are run will print a warning to the terminal instead.
	///
	/// Currently, there's a high amount of false positives due to puzzle wrapper steps not being properly implemented.
	@Test
	void ensureAllStepsHaveSidebarLink()
	{
		var optedInQuests = Set.of(
			QuestHelperQuest.STRONGHOLD_OF_SECURITY,
			QuestHelperQuest.X_MARKS_THE_SPOT,
			QuestHelperQuest.COOKS_ASSISTANT,
			QuestHelperQuest.ROMEO__JULIET,
			QuestHelperQuest.SHEEP_SHEARER,
			QuestHelperQuest.BIOHAZARD,
			QuestHelperQuest.PLAGUE_CITY,
			QuestHelperQuest.CLOCK_TOWER,
			QuestHelperQuest.RUNE_MYSTERIES,
			QuestHelperQuest.HAZEEL_CULT,
			QuestHelperQuest.FIGHT_ARENA,
			QuestHelperQuest.TREE_GNOME_VILLAGE,
			QuestHelperQuest.GERTRUDES_CAT,
			QuestHelperQuest.MONKS_FRIEND,
			QuestHelperQuest.IMP_CATCHER,
			QuestHelperQuest.CLIENT_OF_KOUREND,
			QuestHelperQuest.ARDOUGNE_EASY,
			QuestHelperQuest.CHILDREN_OF_THE_SUN,
			QuestHelperQuest.MISTHALIN_MYSTERY,
			QuestHelperQuest.PRINCE_ALI_RESCUE
		);

		// If you add a quest to this list, then this unit test will *only* test this quest
		Set<QuestHelperQuest> exclusiveQuests = Set.of(
		);

		when(questHelperConfig.solvePuzzles()).thenReturn(true);

		AchievementDiaryStepManager.setup(configManager);

		for (var quest : QuestHelperQuest.values())
		{
			Set<QuestStep> checkedSteps = new HashSet<>();
			if (!exclusiveQuests.isEmpty())
			{
				if (!exclusiveQuests.contains(quest))
				{
					continue;
				}
			}

			var helper = quest.getQuestHelper();
			helper.setQuest(quest);
			if (quest.getPlayerQuests() != null)
			{
				continue;
			}

			var shouldError = optedInQuests.contains(quest);

			when(this.questHelperPlugin.getSelectedQuest()).thenReturn(helper);

			if (helper instanceof BasicQuestHelper)
			{
				this.injector.injectMembers(helper);
				helper.setInjector(this.injector);
				helper.setQuestHelperPlugin(questHelperPlugin);
				helper.setConfig(questHelperConfig);
				helper.init();
				helper.startUp(questHelperConfig);

				var questOverviewPanel = new QuestOverviewPanel(this.questHelperPlugin, this.questHelperPlugin.getQuestManager());

				questOverviewPanel.addQuest(helper, false);
				var basicHelper = (BasicQuestHelper) helper;
				var steps = basicHelper.getStepList();
				var sortedSteps = steps.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toList());
				for (var e : sortedSteps)
				{
					var step = e.getValue();
					if (checkedSteps.contains(step)) continue;
					checkedSteps.add(step);
					helper.startUpStep(step);

					assertNotNull(step);

					checkSteps(helper, questOverviewPanel, shouldError, checkedSteps, step);
				}
			}
			else if (helper instanceof ComplexStateQuestHelper)
			{
				this.injector.injectMembers(helper);
				helper.setInjector(this.injector);
				helper.setQuestHelperPlugin(questHelperPlugin);
				helper.setConfig(questHelperConfig);
				helper.init();
				helper.startUp(questHelperConfig);

				var questOverviewPanel = new QuestOverviewPanel(this.questHelperPlugin, this.questHelperPlugin.getQuestManager());

				questOverviewPanel.addQuest(helper, false);
				var complexHelper = (ComplexStateQuestHelper) helper;
				checkSteps(helper, questOverviewPanel, shouldError, checkedSteps, complexHelper.getCurrentStep());
			}
			else
			{
				System.out.format("Unsupported quest helper type: %s\n", quest);
			}
		}
	}
}
