package com.questhelper.managers;

import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.questhelper.managers.HelperConstructModels.DraftHelper;
import static com.questhelper.managers.HelperConstructModels.DraftOrderLine;
import static com.questhelper.managers.HelperConstructModels.DraftRequirement;
import static com.questhelper.managers.HelperConstructModels.DraftStep;
import static com.questhelper.managers.HelperConstructModels.DraftStepAttachedRequirement;
import static com.questhelper.managers.HelperConstructModels.DraftVarbitRequirement;
import static com.questhelper.managers.HelperConstructModels.StepAttachmentKind;
import static com.questhelper.managers.HelperConstructModels.StepKind;

/**
 * Gson DTOs and load/save mapping for the maker draft (same JSON shape as on-disk draft / import-export).
 */
final class ConstructDraftPersistence
{
	static final int DRAFT_FORMAT_VERSION = 1;

	private ConstructDraftPersistence()
	{
	}

	static DraftHelper draftHelperFromState(DraftState state)
	{
		DraftHelper loaded = new DraftHelper();
		if (state.questName != null)
		{
			loaded.setQuestName(state.questName);
		}
		if (state.className != null)
		{
			loaded.setClassName(state.className);
		}
		if (state.packagePath != null)
		{
			loaded.setPackagePath(state.packagePath);
		}
		if (state.helperType != null)
		{
			loaded.setHelperType(state.helperType);
		}

		if (state.order != null && !state.order.isEmpty())
		{
			Map<String, Integer> legacyDefLinkedByStepId = new HashMap<>();
			if (state.definitions != null)
			{
				for (DraftStepState stepState : state.definitions)
				{
					DraftStep step = draftStepFromState(stepState, false);
					loaded.getStepDefinitions().add(step);
					if (stepState.linkedRequirementRawId != null)
					{
						legacyDefLinkedByStepId.put(step.getStepId(), stepState.linkedRequirementRawId);
					}
				}
			}
			for (DraftOrderLineState lineState : state.order)
			{
				loaded.getOrder().add(draftOrderLineFromState(lineState));
			}
			applyLegacyDefinitionLinkedRequirementsToOrderLines(loaded, legacyDefLinkedByStepId);
		}
		else if (state.definitions != null && !state.definitions.isEmpty())
		{
			for (DraftStepState stepState : state.definitions)
			{
				loaded.getStepDefinitions().add(draftStepFromState(stepState, false));
			}
		}

		if (state.requirements != null)
		{
			for (DraftRequirementState reqState : state.requirements)
			{
				DraftRequirement req = new DraftRequirement();
				req.setRawId(reqState.rawId);
				req.setResolvedSymbol(reqState.resolvedSymbol);
				req.setDisplayName(reqState.displayName);
				loaded.getRequirements().add(req);
			}
		}

		if (state.varbitRequirements != null)
		{
			for (DraftVarbitRequirementState vState : state.varbitRequirements)
			{
				if (vState.lineId == null || vState.lineId.isBlank())
				{
					continue;
				}
				loaded.getVarbitRequirements().add(new DraftVarbitRequirement(
					vState.lineId,
					vState.varbitId,
					vState.requiredValue,
					vState.operation == null || vState.operation.isBlank() ? "EQUAL" : vState.operation,
					vState.displayText));
			}
		}

		return loaded;
	}

	/**
	 * Older drafts stored a per-definition linked item id on each step. That now lives only on quest order rows;
	 * copy legacy values onto order lines that still use default (null) routing.
	 */
	private static void applyLegacyDefinitionLinkedRequirementsToOrderLines(DraftHelper loaded, Map<String, Integer> legacyDefLinkedByStepId)
	{
		if (legacyDefLinkedByStepId.isEmpty())
		{
			return;
		}
		for (DraftOrderLine line : loaded.getOrder())
		{
			if (line.isSectionDivider())
			{
				continue;
			}
			if (line.getLinkedRequirementRawId() != null)
			{
				continue;
			}
			Integer leg = legacyDefLinkedByStepId.get(line.getRefStepId());
			if (leg != null)
			{
				line.setLinkedRequirementRawId(leg);
			}
		}
	}

	static DraftState toDraftState(DraftHelper draft)
	{
		DraftState state = new DraftState();
		state.formatVersion = DRAFT_FORMAT_VERSION;
		state.questName = draft.getQuestName();
		state.className = draft.getClassName();
		state.packagePath = draft.getPackagePath();
		state.helperType = draft.getHelperType();

		for (DraftStep step : draft.getStepDefinitions())
		{
			DraftStepState stepState = new DraftStepState();
			stepState.stepId = step.getStepId();
			stepState.kind = step.getKind();
			stepState.sectionDivider = false;
			stepState.rawId = step.getRawId();
			stepState.resolvedSymbol = step.getResolvedSymbol();
			stepState.option = step.getOption();
			stepState.targetText = step.getTargetText();
			stepState.suggestedVarName = step.getSuggestedVarName();
			stepState.instructionText = step.getInstructionText();
			stepState.panelName = step.getPanelName();
			stepState.sectionCondition = step.getSectionCondition();
			stepState.skipWhenConditionMet = step.isSkipWhenConditionMet();
			for (DraftStepAttachedRequirement a : step.getAttachedRequirements())
			{
				DraftStepAttachedRequirementState st = new DraftStepAttachedRequirementState();
				st.kind = a.getKind();
				st.itemRawId = a.getItemRawId();
				st.varbitId = a.getVarbitId();
				st.varbitRequiredValue = a.getVarbitRequiredValue();
				st.varbitOperation = a.getVarbitOperation();
				st.varbitDisplayText = a.getVarbitDisplayText();
				st.attachmentHighlighted = a.isAttachmentHighlighted();
				stepState.attachedRequirements.add(st);
			}
			if (step.getWorldPoint() != null)
			{
				stepState.worldX = step.getWorldPoint().getX();
				stepState.worldY = step.getWorldPoint().getY();
				stepState.worldPlane = step.getWorldPoint().getPlane();
			}
			state.definitions.add(stepState);
		}

		for (DraftOrderLine line : draft.getOrder())
		{
			DraftOrderLineState lineState = new DraftOrderLineState();
			lineState.lineId = line.getLineId();
			lineState.sectionDivider = line.isSectionDivider();
			lineState.suggestedVarName = line.getSuggestedVarName();
			lineState.sectionCondition = line.getSectionCondition();
			lineState.skipWhenConditionMet = line.isSkipWhenConditionMet();
			lineState.refStepId = line.getRefStepId();
			lineState.linkedRequirementRawId = line.getLinkedRequirementRawId();
			state.order.add(lineState);
		}

		for (DraftRequirement req : draft.getRequirements())
		{
			DraftRequirementState reqState = new DraftRequirementState();
			reqState.rawId = req.getRawId();
			reqState.resolvedSymbol = req.getResolvedSymbol();
			reqState.displayName = req.getDisplayName();
			state.requirements.add(reqState);
		}

		for (DraftVarbitRequirement v : draft.getVarbitRequirements())
		{
			if (v.getLineId() == null || v.getLineId().isBlank())
			{
				continue;
			}
			DraftVarbitRequirementState vs = new DraftVarbitRequirementState();
			vs.lineId = v.getLineId();
			vs.varbitId = v.getVarbitId();
			vs.requiredValue = v.getRequiredValue();
			vs.operation = v.getOperation();
			vs.displayText = v.getDisplayText();
			state.varbitRequirements.add(vs);
		}

		return state;
	}

	private static DraftStep draftStepFromState(DraftStepState stepState, boolean keepSectionDivider)
	{
		DraftStep step = new DraftStep();
		step.setStepId(stepState.stepId == null || stepState.stepId.isBlank() ? UUID.randomUUID().toString() : stepState.stepId);
		step.setKind(stepState.kind);
		step.setSectionDivider(keepSectionDivider && stepState.sectionDivider);
		step.setRawId(stepState.rawId);
		step.setResolvedSymbol(stepState.resolvedSymbol);
		step.setOption(stepState.option);
		step.setTargetText(stepState.targetText);
		step.setSuggestedVarName(stepState.suggestedVarName);
		step.setInstructionText(stepState.instructionText);
		step.setPanelName(stepState.panelName);
		step.setSectionCondition(stepState.sectionCondition);
		step.setSkipWhenConditionMet(stepState.skipWhenConditionMet);
		if (stepState.worldX != null && stepState.worldY != null && stepState.worldPlane != null)
		{
			step.setWorldPoint(new WorldPoint(stepState.worldX, stepState.worldY, stepState.worldPlane));
		}
		migrateStepStateAttachmentsToStep(stepState, step);
		if (step.getKind() == StepKind.ITEM)
		{
			migrateLoadedItemDefinitionToGeneric(step);
		}
		return step;
	}

	/**
	 * Legacy drafts used {@link StepKind#ITEM} definitions; those are now generic ({@link StepKind#TEXT}) steps
	 * with an item attachment (and no definition raw id).
	 */
	private static void migrateLoadedItemDefinitionToGeneric(DraftStep step)
	{
		int rid = step.getRawId();
		step.setKind(StepKind.TEXT);
		step.setRawId(0);
		step.setResolvedSymbol("");
		if (rid == 0)
		{
			return;
		}
		for (DraftStepAttachedRequirement a : step.getAttachedRequirements())
		{
			if (StepAttachmentKind.ITEM.name().equalsIgnoreCase(a.getKind()) && a.getItemRawId() != null && a.getItemRawId() == rid)
			{
				return;
			}
		}
		step.getAttachedRequirements().add(0, DraftStepAttachedRequirement.item(rid));
	}

	private static void migrateStepStateAttachmentsToStep(DraftStepState stepState, DraftStep step)
	{
		if (stepState.attachedRequirements == null || stepState.attachedRequirements.isEmpty())
		{
			return;
		}
		for (DraftStepAttachedRequirementState st : stepState.attachedRequirements)
		{
			DraftStepAttachedRequirement d = new DraftStepAttachedRequirement();
			d.setKind(st.kind);
			d.setItemRawId(st.itemRawId);
			d.setVarbitId(st.varbitId);
			d.setVarbitRequiredValue(st.varbitRequiredValue);
			d.setVarbitOperation(st.varbitOperation);
			d.setVarbitDisplayText(st.varbitDisplayText);
			d.setAttachmentHighlighted(st.attachmentHighlighted);
			step.getAttachedRequirements().add(d);
		}
	}

	private static DraftOrderLine draftOrderLineFromState(DraftOrderLineState s)
	{
		DraftOrderLine line = new DraftOrderLine();
		line.setLineId(s.lineId == null || s.lineId.isBlank() ? UUID.randomUUID().toString() : s.lineId);
		line.setSectionDivider(s.sectionDivider);
		line.setSuggestedVarName(s.suggestedVarName);
		line.setSectionCondition(s.sectionCondition);
		line.setSkipWhenConditionMet(s.skipWhenConditionMet);
		line.setRefStepId(s.refStepId);
		line.setLinkedRequirementRawId(s.linkedRequirementRawId);
		return line;
	}

	static class DraftState
	{
		int formatVersion;
		String questName;
		String className;
		String packagePath;
		String helperType;
		List<DraftStepState> definitions = new ArrayList<>();
		List<DraftOrderLineState> order = new ArrayList<>();
		List<DraftRequirementState> requirements = new ArrayList<>();
		List<DraftVarbitRequirementState> varbitRequirements = new ArrayList<>();
	}

	static class DraftVarbitRequirementState
	{
		String lineId;
		int varbitId;
		int requiredValue;
		String operation;
		String displayText;
	}

	static class DraftStepState
	{
		String stepId;
		StepKind kind;
		boolean sectionDivider;
		int rawId;
		Integer linkedRequirementRawId;
		String resolvedSymbol;
		String option;
		String targetText;
		String suggestedVarName;
		String instructionText;
		String panelName;
		String sectionCondition;
		boolean skipWhenConditionMet;
		Integer worldX;
		Integer worldY;
		Integer worldPlane;
		List<DraftStepAttachedRequirementState> attachedRequirements = new ArrayList<>();
	}

	static class DraftStepAttachedRequirementState
	{
		String kind;
		Integer itemRawId;
		Integer varbitId;
		Integer varbitRequiredValue;
		String varbitOperation;
		String varbitDisplayText;
		boolean attachmentHighlighted;
	}

	static class DraftOrderLineState
	{
		String lineId;
		boolean sectionDivider;
		String suggestedVarName;
		String sectionCondition;
		boolean skipWhenConditionMet;
		String refStepId;
		Integer linkedRequirementRawId;
	}

	static class DraftRequirementState
	{
		int rawId;
		String resolvedSymbol;
		String displayName;
	}
}
