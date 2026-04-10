package com.questhelper.managers;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HelperConstructManagerTest
{
	private static void addDefinitionAndRef(HelperConstructManager manager, HelperConstructModels.DraftStep step)
	{
		if (step.getStepId() == null || step.getStepId().isBlank())
		{
			step.setStepId("test-step-id");
		}
		manager.getCurrentDraft().getStepDefinitions().add(step);
		HelperConstructModels.DraftOrderLine line = new HelperConstructModels.DraftOrderLine();
		line.setSectionDivider(false);
		line.setRefStepId(step.getStepId());
		line.setLinkedRequirementRawId(null);
		manager.getCurrentDraft().getOrder().add(line);
	}

	@Test
	void removeStepAtBoundsChecked()
	{
		HelperConstructManager manager = new HelperConstructManager();
		HelperConstructModels.DraftStep step = new HelperConstructModels.DraftStep();
		step.setStepId("a");
		addDefinitionAndRef(manager, step);

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
		addDefinitionAndRef(manager, step1);
		HelperConstructModels.DraftStep step2 = new HelperConstructModels.DraftStep();
		step2.setStepId("b");
		step2.setSuggestedVarName("secondStep");
		step2.setTargetText("Second");
		addDefinitionAndRef(manager, step2);

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
		addDefinitionAndRef(manager, step);

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
		assertEquals(1, manager.getCurrentDraft().getStepDefinitions().size());
		assertEquals(100, manager.getCurrentDraft().getRequirements().get(0).getRawId());
		assertEquals(100, manager.getCurrentDraft().getStepDefinitions().get(0).getLinkedRequirementRawId());
	}

	@Test
	void removingNpcDefinitionRemovesOrderRefs()
	{
		HelperConstructManager manager = new HelperConstructManager();
		HelperConstructModels.DraftStep npc = new HelperConstructModels.DraftStep();
		npc.setStepId("npc1");
		npc.setKind(HelperConstructModels.StepKind.NPC);
		manager.getCurrentDraft().getStepDefinitions().add(npc);
		HelperConstructModels.DraftOrderLine line = new HelperConstructModels.DraftOrderLine();
		line.setSectionDivider(false);
		line.setRefStepId("npc1");
		manager.getCurrentDraft().getOrder().add(line);

		assertTrue(manager.removeNpcStepAt(0));
		assertTrue(manager.getCurrentDraft().getStepDefinitions().isEmpty());
		assertTrue(manager.getCurrentDraft().getOrder().isEmpty());
	}
}
