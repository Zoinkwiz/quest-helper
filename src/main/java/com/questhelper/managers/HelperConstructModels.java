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
		private StepKind kind;
		private int rawId;
		private String resolvedSymbol;
		private WorldPoint worldPoint;
		private String option;
		private String targetText;
		private String suggestedVarName;
		private String instructionText;
		private String panelName;
		private final List<Integer> requiredItems = new ArrayList<>();
	}

	@Data
	@NoArgsConstructor
	static class DraftHelper
	{
		private String questName = "Generated Quest";
		private String className = "GeneratedQuestHelper";
		private String packagePath = "com.questhelper.helpers.quests.generated";
		private String helperType = "BasicQuestHelper";
		private final List<DraftStep> steps = new ArrayList<>();
		private final List<DraftRequirement> requirements = new ArrayList<>();
	}
}
