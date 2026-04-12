package com.questhelper.maker;

import java.util.UUID;

/** Shared helpers for construct draft / scaffold tests. */
public final class ConstructDraftTestUtil
{
	private ConstructDraftTestUtil()
	{
	}

	static void addDefinitionAndRef(HelperConstructModels.DraftHelper draft, HelperConstructModels.DraftStep step)
	{
		if (step.getStepId() == null || step.getStepId().isBlank())
		{
			step.setStepId(UUID.randomUUID().toString());
		}
		draft.getStepDefinitions().add(step);
		HelperConstructModels.DraftOrderLine line = new HelperConstructModels.DraftOrderLine();
		line.setSectionDivider(false);
		line.setRefStepId(step.getStepId());
		line.setLinkedRequirementRawId(null);
		draft.getOrder().add(line);
	}

	static void addSectionDivider(HelperConstructModels.DraftHelper draft, String name)
	{
		HelperConstructModels.DraftOrderLine line = new HelperConstructModels.DraftOrderLine();
		line.setOrderSlotId(UUID.randomUUID().toString());
		line.setSectionDivider(true);
		line.setSuggestedVarName(name);
		line.setSectionCondition("");
		line.setSkipWhenConditionMet(false);
		draft.getOrder().add(line);
	}

	static void addDefinitionAndRef(HelperConstructManager manager, HelperConstructModels.DraftStep step)
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
}
