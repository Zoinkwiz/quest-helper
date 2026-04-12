package com.questhelper.managers;

import com.questhelper.managers.HelperConstructModels.DraftOrderStepRequirement;

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
import static com.questhelper.managers.HelperConstructModels.StepAttachmentKind;
import static com.questhelper.managers.HelperConstructModels.StepKind;

/**
 * Gson DTOs and load/save mapping for the maker draft (same JSON shape as nested {@code questHelperMaker}
 * in extended Tasks Tracker route files, or legacy root-only draft JSON).
 * <p>
 * Step definitions align with the Tasks Tracker route item shape where practical: {@code note} (was
 * {@code instructionText}), {@code location: {x, y, plane}} (was {@code worldX}/{@code worldY}/
 * {@code worldPlane}). Legacy keys are still accepted on load.
 */
public final class ConstructDraftPersistence
{
	public static final int DRAFT_FORMAT_VERSION = 1;

	private ConstructDraftPersistence()
	{
	}

	/** Whether {@code state} is a usable maker snapshot inside {@code questHelperMaker}. */
	public static boolean isSupportedMakerSnapshot(DraftState state)
	{
		return state != null && state.formatVersion == DRAFT_FORMAT_VERSION;
	}

	public static DraftHelper draftHelperFromState(DraftState state)
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
			hoistRoutingVarbitsFromStepDefinitionsOntoOrderLines(loaded);
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
				req.setDisplayName(reqState.displayName);
				if (reqState.alternateRawIds != null && !reqState.alternateRawIds.isEmpty())
				{
					req.getAlternateRawIds().addAll(reqState.alternateRawIds);
				}
				loaded.getRequirements().add(req);
			}
		}

		migrateLegacyVarbitRequirementsIntoAttachments(loaded, state);

		OrderStepRequirementSupport.normalizeLoadedDraft(loaded);

		return loaded;
	}

	/**
	 * Legacy: VARBIT rows with {@link DraftStepAttachedRequirement#getOrderSlotId()} lived on the step definition.
	 * Move each onto the matching {@link DraftOrderLine#getAttachedRequirements()} (same {@code orderSlotId}).
	 */
	private static void hoistRoutingVarbitsFromStepDefinitionsOntoOrderLines(DraftHelper loaded)
	{
		if (loaded.getStepDefinitions() == null || loaded.getOrder() == null)
		{
			return;
		}
		for (DraftStep step : loaded.getStepDefinitions())
		{
			if (step.getAttachedRequirements() == null || step.getAttachedRequirements().isEmpty())
			{
				continue;
			}
			List<DraftStepAttachedRequirement> copy = new ArrayList<>(step.getAttachedRequirements());
			for (DraftStepAttachedRequirement a : copy)
			{
				if (!StepAttachmentKind.VARBIT.name().equalsIgnoreCase(a.getKind()))
				{
					continue;
				}
				String slot = a.getOrderSlotId();
				if (slot == null || slot.isBlank())
				{
					continue;
				}
				DraftOrderLine target = null;
				for (DraftOrderLine ol : loaded.getOrder())
				{
					if (ol.isSectionDivider())
					{
						continue;
					}
					if (slot.equals(ol.getOrderSlotId()))
					{
						target = ol;
						break;
					}
				}
				if (target == null)
				{
					continue;
				}
				step.getAttachedRequirements().remove(a);
				DraftStepAttachedRequirement.setOrderLineRoutingVarbit(target, a);
			}
		}
	}

	/**
	 * Older drafts stored per–quest-order varbits in {@code varbitRequirements}; those become VARBIT rows on the
	 * matching {@link DraftOrderLine#getAttachedRequirements()}.
	 */
	private static void migrateLegacyVarbitRequirementsIntoAttachments(DraftHelper loaded, DraftState state)
	{
		if (state.varbitRequirements == null || state.varbitRequirements.isEmpty())
		{
			return;
		}
		for (DraftVarbitRequirementState vState : state.varbitRequirements)
		{
			String slot = firstNonBlank(vState.orderSlotId, vState.lineId);
			if (slot == null || slot.isBlank())
			{
				continue;
			}
			DraftOrderLine match = null;
			for (DraftOrderLine ol : loaded.getOrder())
			{
				if (!ol.isSectionDivider() && slot.equals(ol.getOrderSlotId()))
				{
					match = ol;
					break;
				}
			}
			if (match == null)
			{
				continue;
			}
			DraftStep step = findDefinitionByStepId(loaded, match.getRefStepId());
			if (DraftStepAttachedRequirement.findOrderRoutingVarbit(match) != null)
			{
				continue;
			}
			DraftStepAttachedRequirement.setOrderLineRoutingVarbit(match, DraftStepAttachedRequirement.varbit(
				vState.varbitId,
				vState.requiredValue,
				vState.operation == null || vState.operation.isBlank() ? "EQUAL" : vState.operation,
				vState.displayText));
			if (step != null && step.getStructId() == null && vState.structId != null)
			{
				step.setStructId(vState.structId);
			}
		}
	}

	private static DraftStep findDefinitionByStepId(DraftHelper draft, String stepId)
	{
		if (draft == null || stepId == null || stepId.isBlank())
		{
			return null;
		}
		for (DraftStep s : draft.getStepDefinitions())
		{
			if (stepId.equals(s.getStepId()))
			{
				return s;
			}
		}
		return null;
	}

	private static String firstNonBlank(String a, String b)
	{
		if (a != null && !a.isBlank())
		{
			return a;
		}
		if (b != null && !b.isBlank())
		{
			return b;
		}
		return null;
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

	public static DraftState toDraftState(DraftHelper draft)
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
			stepState.option = step.getOption();
			stepState.targetText = step.getTargetText();
			stepState.suggestedVarName = step.getSuggestedVarName();
			stepState.note = step.getInstructionText();
			stepState.panelName = step.getPanelName();
			stepState.sectionCondition = step.getSectionCondition();
			stepState.skipWhenConditionMet = step.isSkipWhenConditionMet();
			stepState.structId = step.getStructId();
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
				st.orderSlotId = a.getOrderSlotId();
				stepState.attachedRequirements.add(st);
			}
			if (step.getWorldPoint() != null)
			{
				DraftLocationState loc = new DraftLocationState();
				loc.x = step.getWorldPoint().getX();
				loc.y = step.getWorldPoint().getY();
				loc.plane = step.getWorldPoint().getPlane();
				stepState.location = loc;
			}
			if (!step.getAlternateRawIds().isEmpty())
			{
				stepState.alternateRawIds = new ArrayList<>(step.getAlternateRawIds());
			}
			state.definitions.add(stepState);
		}

		for (DraftOrderLine line : draft.getOrder())
		{
			DraftOrderLineState lineState = new DraftOrderLineState();
			lineState.orderSlotId = line.getOrderSlotId();
			lineState.sectionDivider = line.isSectionDivider();
			lineState.suggestedVarName = line.getSuggestedVarName();
			lineState.sectionCondition = line.getSectionCondition();
			lineState.skipWhenConditionMet = line.isSkipWhenConditionMet();
			lineState.refStepId = line.getRefStepId();
			lineState.linkedRequirementRawId = line.getLinkedRequirementRawId();
			lineState.stepRequirement = line.getStepRequirement();
			for (DraftStepAttachedRequirement a : line.getAttachedRequirements())
			{
				DraftStepAttachedRequirementState st = new DraftStepAttachedRequirementState();
				st.kind = a.getKind();
				st.itemRawId = a.getItemRawId();
				st.varbitId = a.getVarbitId();
				st.varbitRequiredValue = a.getVarbitRequiredValue();
				st.varbitOperation = a.getVarbitOperation();
				st.varbitDisplayText = a.getVarbitDisplayText();
				st.attachmentHighlighted = a.isAttachmentHighlighted();
				st.orderSlotId = a.getOrderSlotId();
				lineState.attachedRequirements.add(st);
			}
			state.order.add(lineState);
		}

		for (DraftRequirement req : draft.getRequirements())
		{
			DraftRequirementState reqState = new DraftRequirementState();
			reqState.rawId = req.getRawId();
			reqState.displayName = req.getDisplayName();
			if (!req.getAlternateRawIds().isEmpty())
			{
				reqState.alternateRawIds = new ArrayList<>(req.getAlternateRawIds());
			}
			state.requirements.add(reqState);
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
		step.setOption(stepState.option);
		step.setTargetText(stepState.targetText);
		step.setSuggestedVarName(stepState.suggestedVarName);
		step.setInstructionText(noteOrLegacyInstructionText(stepState));
		step.setPanelName(stepState.panelName);
		step.setSectionCondition(stepState.sectionCondition);
		step.setSkipWhenConditionMet(stepState.skipWhenConditionMet);
		step.setStructId(stepState.structId);
		if (stepState.alternateRawIds != null && !stepState.alternateRawIds.isEmpty())
		{
			step.getAlternateRawIds().addAll(stepState.alternateRawIds);
		}
		WorldPoint wp = worldPointFromStepState(stepState);
		if (wp != null)
		{
			step.setWorldPoint(wp);
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
			d.setOrderSlotId(st.orderSlotId);
			step.getAttachedRequirements().add(d);
		}
	}

	private static String noteOrLegacyInstructionText(DraftStepState stepState)
	{
		if (stepState.note != null && !stepState.note.isBlank())
		{
			return stepState.note;
		}
		if (stepState.instructionText != null)
		{
			return stepState.instructionText;
		}
		return "";
	}

	private static WorldPoint worldPointFromStepState(DraftStepState stepState)
	{
		if (stepState.location != null)
		{
			return new WorldPoint(stepState.location.x, stepState.location.y, stepState.location.plane);
		}
		if (stepState.worldX != null && stepState.worldY != null && stepState.worldPlane != null)
		{
			return new WorldPoint(stepState.worldX, stepState.worldY, stepState.worldPlane);
		}
		return null;
	}

	private static DraftOrderLine draftOrderLineFromState(DraftOrderLineState s)
	{
		DraftOrderLine line = new DraftOrderLine();
		String slot = firstNonBlank(s.orderSlotId, s.lineId);
		line.setOrderSlotId(slot == null || slot.isBlank() ? UUID.randomUUID().toString() : slot);
		line.setSectionDivider(s.sectionDivider);
		line.setSuggestedVarName(s.suggestedVarName);
		line.setSectionCondition(s.sectionCondition);
		line.setSkipWhenConditionMet(s.skipWhenConditionMet);
		line.setRefStepId(s.refStepId);
		line.setLinkedRequirementRawId(s.linkedRequirementRawId);
		if (s.stepRequirement != null)
		{
			line.setStepRequirement(s.stepRequirement);
		}
		if (s.attachedRequirements != null)
		{
			for (DraftStepAttachedRequirementState st : s.attachedRequirements)
			{
				DraftStepAttachedRequirement d = new DraftStepAttachedRequirement();
				d.setKind(st.kind);
				d.setItemRawId(st.itemRawId);
				d.setVarbitId(st.varbitId);
				d.setVarbitRequiredValue(st.varbitRequiredValue);
				d.setVarbitOperation(st.varbitOperation);
				d.setVarbitDisplayText(st.varbitDisplayText);
				d.setAttachmentHighlighted(st.attachmentHighlighted);
				d.setOrderSlotId(st.orderSlotId);
				line.getAttachedRequirements().add(d);
			}
		}
		return line;
	}

	public static class DraftState
	{
		public int formatVersion;
		public String questName;
		public String className;
		public String packagePath;
		public String helperType;
		public List<DraftStepState> definitions = new ArrayList<>();
		public List<DraftOrderLineState> order = new ArrayList<>();
		public List<DraftRequirementState> requirements = new ArrayList<>();
		public List<DraftVarbitRequirementState> varbitRequirements = new ArrayList<>();
	}

	/**
	 * World position; same keys as Tasks Tracker route {@code location}.
	 */
	public static class DraftLocationState
	{
		public int x;
		public int y;
		public int plane;
	}

	public static class DraftVarbitRequirementState
	{
		/** Legacy JSON key; prefer {@link #orderSlotId}. */
		public String lineId;
		public String orderSlotId;
		public int varbitId;
		public int requiredValue;
		public String operation;
		public String displayText;
		public Integer structId;
	}

	public static class DraftStepState
	{
		public String stepId;
		public StepKind kind;
		public boolean sectionDivider;
		public int rawId;
		public Integer linkedRequirementRawId;
		public String option;
		public String targetText;
		public String suggestedVarName;
		/** Same role as Tasks Tracker route {@code note}; preferred on serialize. */
		public String note;
		/** @deprecated read-only for legacy drafts; use {@link #note}. */
		public String instructionText;
		public String panelName;
		public String sectionCondition;
		public boolean skipWhenConditionMet;
		public Integer structId;
		/** Same shape as Tasks Tracker route {@code location}; preferred on serialize. */
		public DraftLocationState location;
		/** @deprecated read-only for legacy drafts; use {@link #location}. */
		public Integer worldX;
		/** @deprecated read-only for legacy drafts; use {@link #location}. */
		public Integer worldY;
		/** @deprecated read-only for legacy drafts; use {@link #location}. */
		public Integer worldPlane;
		public List<Integer> alternateRawIds;
		public List<DraftStepAttachedRequirementState> attachedRequirements = new ArrayList<>();
	}

	public static class DraftStepAttachedRequirementState
	{
		public String kind;
		public Integer itemRawId;
		public Integer varbitId;
		public Integer varbitRequiredValue;
		public String varbitOperation;
		public String varbitDisplayText;
		public boolean attachmentHighlighted;
		/** When set with VARBIT kind, ties this row to {@link DraftOrderLineState#orderSlotId}. */
		public String orderSlotId;
	}

	public static class DraftOrderLineState
	{
		/**
		 * Stable id for this quest-order row; ties to VARBIT routing attachments via the same value on
		 * {@link DraftStepAttachedRequirementState#orderSlotId}. Legacy drafts used {@code lineId}.
		 */
		public String orderSlotId;
		/** @deprecated Gson-only; use {@link #orderSlotId}. */
		public String lineId;
		public boolean sectionDivider;
		public String suggestedVarName;
		public String sectionCondition;
		public boolean skipWhenConditionMet;
		public String refStepId;
		public Integer linkedRequirementRawId;
		/** Optional per–quest-order branch requirement tree (see {@link DraftOrderStepRequirement}). */
		public DraftOrderStepRequirement stepRequirement;
		/** Order-scoped attachments (e.g. routing VARBIT for this slot). */
		public List<DraftStepAttachedRequirementState> attachedRequirements = new ArrayList<>();
	}

	public static class DraftRequirementState
	{
		public int rawId;
		public String displayName;
		public List<Integer> alternateRawIds;
	}
}
