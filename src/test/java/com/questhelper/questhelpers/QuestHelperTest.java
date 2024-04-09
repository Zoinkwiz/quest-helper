package com.questhelper.questhelpers;

import com.questhelper.MockedTest;
import com.questhelper.questinfo.QuestHelperQuest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class QuestHelperTest extends MockedTest
{
	@Test
	void ensureRequirementsSetUpBySetupRequirements()
	{
		for (var quest : QuestHelperQuest.values())
		{
			// Instantiate helper
			var helper = quest.getQuestHelper();
			helper.setQuest(quest);
			this.injector.injectMembers(helper);
			helper.setQuestHelperPlugin(questHelperPlugin);
			helper.setupRequirements();

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
				}
			}
		}
	}
}
