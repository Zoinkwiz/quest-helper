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
		private int rawId;
		private Integer linkedRequirementRawId;
		private String resolvedSymbol;
		private WorldPoint worldPoint;
		private String option;
		private String targetText;
		private String suggestedVarName;
		private String instructionText;
		private String panelName;
		private String sectionCondition;
		private boolean skipWhenConditionMet;
		private final List<Integer> requiredItems = new ArrayList<>();
	}

	/** Order row: section divider or ref to a definition. {@code linkedRequirementRawId}: null inherit, -1 varbit routing, else item req raw id. */
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
	}

	/** Sentinel for order line: use varbit (not item requirement) for routing on this slot. */
	static final int ORDER_ROUTING_VARBIT_SENTINEL = -1;
}
