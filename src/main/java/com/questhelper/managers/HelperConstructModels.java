package com.questhelper.managers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.List;

final class HelperConstructModels
{
	private HelperConstructModels()
	{
	}

	enum StepKind
	{
		NPC,
		OBJECT,
		ITEM,
		TEXT
	}

	enum IdType
	{
		NPC,
		OBJECT,
		ITEM,
		VARBIT
	}

	/**
	 * Extra requirements on a step definition (shown in preview / emitted on the step).
	 * {@code kind} is extensible for future types (unknown kinds are skipped at runtime with a warning in codegen).
	 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class DraftStepAttachedRequirement
	{
		/** {@link StepAttachmentKind#name()} or future discriminator. */
		private String kind;
		/** When {@code kind} is {@code ITEM}: captured / item raw id. */
		private Integer itemRawId;
		/** When {@code kind} is {@code VARBIT}: varbit id. */
		private Integer varbitId;
		private Integer varbitRequiredValue;
		/** {@link com.questhelper.requirements.util.Operation} name. */
		private String varbitOperation;
		private String varbitDisplayText;
		/** When {@code kind} is {@link StepAttachmentKind#ITEM}: show {@link com.questhelper.requirements.item.ItemRequirement#highlighted()} in preview / codegen. */
		private boolean attachmentHighlighted;

		static DraftStepAttachedRequirement item(int rawId)
		{
			return item(rawId, false);
		}

		static DraftStepAttachedRequirement item(int rawId, boolean attachmentHighlighted)
		{
			return new DraftStepAttachedRequirement(StepAttachmentKind.ITEM.name(), rawId, null, null, null, null, attachmentHighlighted);
		}

		static DraftStepAttachedRequirement varbit(int varbitId, int requiredValue, String operation, String displayText)
		{
			String op = operation == null || operation.isBlank() ? "EQUAL" : operation.trim();
			return new DraftStepAttachedRequirement(StepAttachmentKind.VARBIT.name(), null, varbitId, requiredValue, op, displayText, false);
		}
	}

	enum StepAttachmentKind
	{
		ITEM,
		VARBIT
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class DraftRequirement
	{
		private int rawId;
		private String resolvedSymbol;
		private String displayName;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class DraftStep
	{
		private String stepId;
		private StepKind kind;
		private boolean sectionDivider;
		/**
		 * Optional league / external struct id persisted in draft JSON only; not read by codegen or preview logic.
		 */
		private Integer structId;
		private int rawId;
		private String resolvedSymbol;
		private WorldPoint worldPoint;
		private String option;
		private String targetText;
		private String suggestedVarName;
		private String instructionText;
		private String panelName;
		private String sectionCondition;
		private boolean skipWhenConditionMet;
		private final List<DraftStepAttachedRequirement> attachedRequirements = new ArrayList<>();
	}

	/** Order row: section divider or ref to a definition. {@code linkedRequirementRawId}: {@code null} = varbit-based routing (item steps use definition raw id for highlight); {@code -1} = varbit only; else captured item requirement raw id. */
	@Data
	@NoArgsConstructor
	static class DraftOrderLine
	{
		private boolean sectionDivider;
		private String lineId;
		private String suggestedVarName;
		private String sectionCondition;
		private boolean skipWhenConditionMet;
		private String refStepId;
		private Integer linkedRequirementRawId;
	}

	/** Routing varbit for one quest-order row (Default / Varbit only). Keyed by {@link DraftOrderLine#lineId}. */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class DraftVarbitRequirement
	{
		private String lineId;
		private int varbitId;
		private int requiredValue;
		/** {@link com.questhelper.requirements.util.Operation} name, e.g. EQUAL */
		private String operation;
		private String displayText;
		/**
		 * Optional league / external struct id (same meaning as {@link DraftStep#structId}); draft JSON only, not used by codegen.
		 */
		private Integer structId;
	}

	@Data
	@NoArgsConstructor
	static class DraftHelper
	{
		private String questName = "Generated Quest";
		private String className = "GeneratedQuestHelper";
		private String packagePath = "com.questhelper.helpers.quests.generated";
		private String helperType = "BasicQuestHelper";
		private final List<DraftStep> stepDefinitions = new ArrayList<>();
		private final List<DraftOrderLine> order = new ArrayList<>();
		private final List<DraftRequirement> requirements = new ArrayList<>();
		private final List<DraftVarbitRequirement> varbitRequirements = new ArrayList<>();
	}

	/** Sentinel for order line: use varbit (not item requirement) for routing on this slot. */
	static final int ORDER_ROUTING_VARBIT_SENTINEL = -1;
}
