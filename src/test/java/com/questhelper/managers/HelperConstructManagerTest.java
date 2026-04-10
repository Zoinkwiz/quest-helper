package com.questhelper.managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HelperConstructManagerTest
{
	@Test
	void removeStepAtBoundsChecked()
	{
		HelperConstructManager manager = new HelperConstructManager();
		manager.getCurrentDraft().getSteps().add(new HelperConstructModels.DraftStep());

		assertFalse(manager.removeStepAt(-1));
		assertFalse(manager.removeStepAt(1));
		assertTrue(manager.removeStepAt(0));
		assertFalse(manager.removeStepAt(0));
	}

	@Test
	void removeRequirementAtBoundsChecked()
	{
		HelperConstructManager manager = new HelperConstructManager();
		manager.getCurrentDraft().getRequirements().add(new HelperConstructModels.DraftRequirement());

		assertFalse(manager.removeRequirementAt(-1));
		assertFalse(manager.removeRequirementAt(1));
		assertTrue(manager.removeRequirementAt(0));
		assertFalse(manager.removeRequirementAt(0));
	}
}
