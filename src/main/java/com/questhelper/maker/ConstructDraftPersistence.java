package com.questhelper.maker;

import com.questhelper.maker.HelperConstructModels.DraftOrderStepRequirement;

import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.questhelper.maker.HelperConstructModels.DraftHelper;
import static com.questhelper.maker.HelperConstructModels.DraftOrderLine;
import static com.questhelper.maker.HelperConstructModels.DraftRequirement;
import static com.questhelper.maker.HelperConstructModels.DraftSkillRequirement;
import static com.questhelper.maker.HelperConstructModels.DraftStep;
import static com.questhelper.maker.HelperConstructModels.DraftStepAttachedRequirement;
import static com.questhelper.maker.HelperConstructModels.StepAttachmentKind;
import static com.questhelper.maker.HelperConstructModels.StepKind;

/**
 * Gson DTOs and load/save mapping for the maker draft (nested {@code questHelperMaker} in extended Tasks Tracker
 * route files). Canonical JSON uses {@code note}, {@code location}, and {@code orderSlotId} only; convert older
 * files with {@code maker/scripts/convert_legacy_maker_draft.py}.
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
			if (state.definitions != null)
			{
				for (DraftStepState stepState : state.definitions)
				{
					loaded.getStepDefinitions().add(draftStepFromState(stepState, false));
				}
			}
			for (DraftOrderLineState lineState : state.order)
			{
				loaded.getOrder().add(draftOrderLineFromState(lineState));
			}
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
		if (state.skillRequirements != null)
		{
			for (DraftSkillRequirementState skillState : state.skillRequirements)
			{
				DraftSkillRequirement skill = new DraftSkillRequirement();
				skill.setSkillName(skillState.skillName);
				skill.setRequiredLevel(skillState.requiredLevel < 1 ? 1 : skillState.requiredLevel);
				skill.setCanBeBoosted(skillState.canBeBoosted);
				skill.setDisplayText(skillState.displayText);
				skill.setOperation(skillState.operation == null || skillState.operation.isBlank() ? "GREATER_EQUAL" : skillState.operation.trim());
				loaded.getSkillRequirements().add(skill);
			}
		}

		OrderStepRequirementSupport.normalizeLoadedDraft(loaded);

		return loaded;
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
				st.skillName = a.getSkillName();
				st.skillRequiredLevel = a.getSkillRequiredLevel();
				st.skillOperation = a.getSkillOperation();
				st.skillDisplayText = a.getSkillDisplayText();
				st.skillCanBeBoosted = a.isSkillCanBeBoosted();
				st.attachmentHighlighted = a.isAttachmentHighlighted();
				st.itemQuantity = a.getItemQuantity();
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
			if (line.getZoneRoutingCorner1() != null)
			{
				lineState.zoneRoutingCorner1 = locationStateFromWorldPoint(line.getZoneRoutingCorner1());
			}
			if (line.getZoneRoutingCorner2() != null)
			{
				lineState.zoneRoutingCorner2 = locationStateFromWorldPoint(line.getZoneRoutingCorner2());
			}
			lineState.zoneRoutingDisplayText = line.getZoneRoutingDisplayText();
			for (DraftStepAttachedRequirement a : line.getAttachedRequirements())
			{
				DraftStepAttachedRequirementState st = new DraftStepAttachedRequirementState();
				st.kind = a.getKind();
				st.itemRawId = a.getItemRawId();
				st.varbitId = a.getVarbitId();
				st.varbitRequiredValue = a.getVarbitRequiredValue();
				st.varbitOperation = a.getVarbitOperation();
				st.varbitDisplayText = a.getVarbitDisplayText();
				st.skillName = a.getSkillName();
				st.skillRequiredLevel = a.getSkillRequiredLevel();
				st.skillOperation = a.getSkillOperation();
				st.skillDisplayText = a.getSkillDisplayText();
				st.skillCanBeBoosted = a.isSkillCanBeBoosted();
				st.attachmentHighlighted = a.isAttachmentHighlighted();
				st.itemQuantity = a.getItemQuantity();
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
		for (DraftSkillRequirement skill : draft.getSkillRequirements())
		{
			DraftSkillRequirementState skillState = new DraftSkillRequirementState();
			skillState.skillName = skill.getSkillName();
			skillState.requiredLevel = skill.getRequiredLevel();
			skillState.canBeBoosted = skill.isCanBeBoosted();
			skillState.displayText = skill.getDisplayText();
			skillState.operation = skill.getOperation();
			state.skillRequirements.add(skillState);
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
		step.setInstructionText(stepState.note == null ? "" : stepState.note);
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
		return step;
	}

	private static int normalizePersistedItemQuantity(String kind, Integer itemQuantity)
	{
		if (kind == null || !StepAttachmentKind.ITEM.name().equalsIgnoreCase(kind.trim()))
		{
			return 0;
		}
		if (itemQuantity == null || itemQuantity < 1)
		{
			return 1;
		}
		return itemQuantity;
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
			d.setSkillName(st.skillName);
			d.setSkillRequiredLevel(st.skillRequiredLevel);
			d.setSkillOperation(st.skillOperation);
			d.setSkillDisplayText(st.skillDisplayText);
			d.setSkillCanBeBoosted(st.skillCanBeBoosted);
			d.setAttachmentHighlighted(st.attachmentHighlighted);
			d.setItemQuantity(normalizePersistedItemQuantity(st.kind, st.itemQuantity));
			d.setOrderSlotId(st.orderSlotId);
			step.getAttachedRequirements().add(d);
		}
	}

	private static WorldPoint worldPointFromStepState(DraftStepState stepState)
	{
		if (stepState.location != null)
		{
			return new WorldPoint(stepState.location.x, stepState.location.y, stepState.location.plane);
		}
		return null;
	}

	private static DraftLocationState locationStateFromWorldPoint(WorldPoint wp)
	{
		if (wp == null)
		{
			return null;
		}
		DraftLocationState loc = new DraftLocationState();
		loc.x = wp.getX();
		loc.y = wp.getY();
		loc.plane = wp.getPlane();
		return loc;
	}

	private static DraftOrderLine draftOrderLineFromState(DraftOrderLineState s)
	{
		DraftOrderLine line = new DraftOrderLine();
		String slot = s.orderSlotId;
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
				d.setSkillName(st.skillName);
				d.setSkillRequiredLevel(st.skillRequiredLevel);
				d.setSkillOperation(st.skillOperation);
				d.setSkillDisplayText(st.skillDisplayText);
				d.setSkillCanBeBoosted(st.skillCanBeBoosted);
				d.setAttachmentHighlighted(st.attachmentHighlighted);
				d.setItemQuantity(normalizePersistedItemQuantity(st.kind, st.itemQuantity));
				d.setOrderSlotId(st.orderSlotId);
				line.getAttachedRequirements().add(d);
			}
		}
		if (s.zoneRoutingCorner1 != null)
		{
			line.setZoneRoutingCorner1(new WorldPoint(s.zoneRoutingCorner1.x, s.zoneRoutingCorner1.y, s.zoneRoutingCorner1.plane));
		}
		if (s.zoneRoutingCorner2 != null)
		{
			line.setZoneRoutingCorner2(new WorldPoint(s.zoneRoutingCorner2.x, s.zoneRoutingCorner2.y, s.zoneRoutingCorner2.plane));
		}
		line.setZoneRoutingDisplayText(s.zoneRoutingDisplayText);
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
		public List<DraftSkillRequirementState> skillRequirements = new ArrayList<>();
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

	public static class DraftStepState
	{
		public String stepId;
		public StepKind kind;
		public boolean sectionDivider;
		public int rawId;
		public String option;
		public String targetText;
		public String suggestedVarName;
		/** Same role as Tasks Tracker route {@code note}. */
		public String note;
		public String panelName;
		public String sectionCondition;
		public boolean skipWhenConditionMet;
		public Integer structId;
		public DraftLocationState location;
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
		public String skillName;
		public Integer skillRequiredLevel;
		public String skillOperation;
		public String skillDisplayText;
		public boolean skillCanBeBoosted;
		public boolean attachmentHighlighted;
		/** When {@code kind} is ITEM: required quantity ({@code >= 1}); omitted or {@code null} in old JSON defaults to 1. */
		public Integer itemQuantity;
		public String orderSlotId;
	}

	public static class DraftOrderLineState
	{
		public String orderSlotId;
		public boolean sectionDivider;
		public String suggestedVarName;
		public String sectionCondition;
		public boolean skipWhenConditionMet;
		public String refStepId;
		public Integer linkedRequirementRawId;
		public DraftOrderStepRequirement stepRequirement;
		public List<DraftStepAttachedRequirementState> attachedRequirements = new ArrayList<>();
		public DraftLocationState zoneRoutingCorner1;
		public DraftLocationState zoneRoutingCorner2;
		public String zoneRoutingDisplayText;
	}

	public static class DraftRequirementState
	{
		public int rawId;
		public String displayName;
		public List<Integer> alternateRawIds;
	}

	public static class DraftSkillRequirementState
	{
		public String skillName;
		public int requiredLevel;
		public boolean canBeBoosted;
		public String displayText;
		public String operation;
	}
}
