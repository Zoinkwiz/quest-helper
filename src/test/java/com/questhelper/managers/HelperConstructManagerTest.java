package com.questhelper.managers;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

	@Test
	void moveStepReordersCombinedRows()
	{
		HelperConstructManager manager = new HelperConstructManager();
		HelperConstructModels.DraftStep step1 = new HelperConstructModels.DraftStep();
		step1.setStepId("a");
		step1.setSuggestedVarName("firstStep");
		step1.setTargetText("First");
		HelperConstructModels.DraftStep step2 = new HelperConstructModels.DraftStep();
		step2.setStepId("b");
		step2.setSuggestedVarName("secondStep");
		step2.setTargetText("Second");
		manager.getCurrentDraft().getSteps().add(step1);
		manager.getCurrentDraft().getSteps().add(step2);

		assertTrue(manager.moveStep(1, 0));
		List<HelperConstructManager.CombinedStepRow> rows = manager.getCombinedStepRows();
		assertEquals("secondStep", rows.get(0).getVarName());
		assertEquals("firstStep", rows.get(1).getVarName());
	}

	@Test
	void updateStepVarNameUpdatesCombinedRows()
	{
		HelperConstructManager manager = new HelperConstructManager();
		HelperConstructModels.DraftStep step = new HelperConstructModels.DraftStep();
		step.setStepId("a");
		step.setSuggestedVarName("oldName");
		manager.getCurrentDraft().getSteps().add(step);

		assertTrue(manager.updateStepVarName(0, "newName"));
		assertEquals("newName", manager.getCombinedStepRows().get(0).getVarName());
		assertFalse(manager.updateStepVarName(1, "x"));
	}

	@Test
	void addItemStepReusesExistingRequirement()
		throws Exception
	{
		HelperConstructManager manager = new HelperConstructManager();
		Method addItemStep = HelperConstructManager.class.getDeclaredMethod("addItemStep", int.class, String.class);
		addItemStep.setAccessible(true);

		addItemStep.invoke(manager, 100, "Bucket");
		addItemStep.invoke(manager, 100, "Bucket");

		assertEquals(1, manager.getCurrentDraft().getRequirements().size());
		assertEquals(2, manager.getCurrentDraft().getSteps().size());
		assertEquals(100, manager.getCurrentDraft().getRequirements().get(0).getRawId());
		assertEquals(100, manager.getCurrentDraft().getSteps().get(0).getLinkedRequirementRawId());
	}
}
