package com.questhelper.questhelpers;

import com.questhelper.MockedTest;
import com.questhelper.domain.AccountType;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.statemanagement.AchievementDiaryStepManager;
import java.lang.reflect.Field;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertFalse;
import com.questhelper.steps.ConditionalStep;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

	@Test
	void ensureAllStepsHaveSidebarLink()
	{
		when(questHelperConfig.solvePuzzles()).thenReturn(true);

		AchievementDiaryStepManager.setup(configManager);

		for (var quest : QuestHelperQuest.values())
		{
			var helper = quest.getQuestHelper();
			helper.setQuest(quest);
			if (quest.getPlayerQuests() != null)
			{
				continue;
			}

			this.injector.injectMembers(helper);
			helper.setQuestHelperPlugin(questHelperPlugin);
			helper.setConfig(questHelperConfig);
			helper.init();

			if (quest != QuestHelperQuest.THE_CURSE_OF_ARRAV) {
				continue;
			}

			if (helper instanceof BasicQuestHelper) {
				var basicHelper = (BasicQuestHelper) helper;
				var panels = helper.getPanels();
				var panelSteps = panels.stream().flatMap(panelDetails -> panelDetails.getSteps().stream()).collect(Collectors.toList());
				var steps = basicHelper.getStepList().values();
				for (var step : steps) {
					assertNotNull(step);
					var rawText = step.getText();
					var text = rawText == null ? "" : String.join("\n", step.getText());
					if (step instanceof ConditionalStep) {
						//
					} else {
						var isInPanelSteps = panelSteps.contains(step);
						/* TODO
						var isSubstepOf = steps.stream().filter(questStep -> {
							if (questStep instanceof BasicQuest) {
								return questStep.getSubSteps();
							}
							return null;
						});
						 */
						var isInAnyStepSubStepsThatIsInPanelSteps = true; // todo
						assertTrue(isInPanelSteps);
					}
				}
			}
		}
	}
}
