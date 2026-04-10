package com.questhelper.managers;

import com.questhelper.managers.GamevalSymbolResolver.ResolutionResult;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.questhelper.managers.HelperConstructModels.DraftHelper;
import static com.questhelper.managers.HelperConstructModels.DraftOrderLine;
import static com.questhelper.managers.HelperConstructModels.DraftRequirement;
import static com.questhelper.managers.HelperConstructModels.DraftStep;
import static com.questhelper.managers.HelperConstructModels.IdType;
import static com.questhelper.managers.HelperConstructModels.ORDER_ROUTING_VARBIT_SENTINEL;
import static com.questhelper.managers.HelperConstructModels.StepKind;

@Singleton
public class HelperScaffoldGenerator
{
	private final GamevalSymbolResolver symbolResolver;

	@Inject
	public HelperScaffoldGenerator(GamevalSymbolResolver symbolResolver)
	{
		this.symbolResolver = symbolResolver;
	}

	public GeneratedScaffold generate(DraftHelper draft)
	{
		List<String> warnings = new ArrayList<>();
		StringBuilder out = new StringBuilder();
		String className = sanitizeClassName(draft.getClassName());
		String packageName = draft.getPackagePath();
		List<DraftStep> definitions = draft.getStepDefinitions();
		List<DraftRequirement> requirements = draft.getRequirements();
		List<SectionGroup> sectionGroups = buildSectionGroups(draft);

		out.append("package ").append(packageName).append(";\n\n");
		out.append("import com.questhelper.panel.PanelDetails;\n");
		out.append("import com.questhelper.questhelpers.ComplexStateQuestHelper;\n");
		out.append("import com.questhelper.requirements.item.ItemRequirement;\n");
		out.append("import static com.questhelper.requirements.util.LogicHelper.not;\n");
		out.append("import static com.questhelper.requirements.util.LogicHelper.nor;\n");
		out.append("import com.questhelper.requirements.var.VarbitRequirement;\n");
		out.append("import com.questhelper.steps.ConditionalStep;\n");
		out.append("import com.questhelper.steps.ItemStep;\n");
		out.append("import com.questhelper.steps.NpcStep;\n");
		out.append("import com.questhelper.steps.ObjectStep;\n");
		out.append("import com.questhelper.steps.QuestStep;\n");
		out.append("import java.util.ArrayList;\n");
		out.append("import java.util.List;\n");
		out.append("import net.runelite.api.coords.WorldPoint;\n");
		out.append("import net.runelite.api.gameval.ItemID;\n");
		out.append("import net.runelite.api.gameval.NpcID;\n");
		out.append("import net.runelite.api.gameval.ObjectID;\n\n");

		out.append("public class ").append(className).append(" extends ComplexStateQuestHelper\n");
		out.append("{\n");
		out.append("\t// Captured requirements\n");
		for (DraftRequirement requirement : requirements)
		{
			out.append("\tItemRequirement ").append(toVarName(requirement.getDisplayName(), "itemReq")).append(";\n");
		}

		out.append("\n\t// Captured steps\n");
		Map<DraftStep, String> stepVarNames = new LinkedHashMap<>();
		Map<Integer, String> requirementVarNamesByRawId = new LinkedHashMap<>();
		Map<SectionGroup, String> sectionTaskNames = new LinkedHashMap<>();
		Set<String> usedNames = new LinkedHashSet<>();
		Map<DraftStep, String> varbitReqVarNames = new LinkedHashMap<>();
		for (DraftRequirement requirement : requirements)
		{
			requirementVarNamesByRawId.put(requirement.getRawId(), toVarName(requirement.getDisplayName(), "itemReq"));
		}
		for (DraftStep step : definitions)
		{
			String stepType = stepTypeFor(step.getKind());
			String candidate = toVarName(step.getSuggestedVarName(), "step");
			String unique = makeUnique(candidate, usedNames);
			stepVarNames.put(step, unique);
			out.append("\t").append(stepType).append(" ").append(unique).append(";\n");
			if (!stepHasLinkedItemRequirement(step, requirementVarNamesByRawId))
			{
				String varbitReqVar = makeUnique(unique + "VarbitReq", usedNames);
				varbitReqVarNames.put(step, varbitReqVar);
				out.append("\tVarbitRequirement ").append(varbitReqVar).append(";\n");
			}
		}
		for (int i = 0; i < sectionGroups.size(); i++)
		{
			SectionGroup group = sectionGroups.get(i);
			if (group.slots.isEmpty())
			{
				continue;
			}
			String sectionTaskName = makeUnique("section" + (i + 1) + "Task", usedNames);
			sectionTaskNames.put(group, sectionTaskName);
			out.append("\tConditionalStep ").append(sectionTaskName).append(";\n");
		}

		out.append("\n\t@Override\n");
		out.append("\tprotected void setupRequirements()\n");
		out.append("\t{\n");
		for (DraftRequirement requirement : requirements)
		{
			String varName = toVarName(requirement.getDisplayName(), "itemReq");
			ResolutionResult resolved = symbolResolver.resolve(IdType.ITEM, requirement.getRawId());
			if (resolved.isFallbackLiteral())
			{
				warnings.add("Unresolved item ID: " + requirement.getRawId());
			}
			out.append("\t\t").append(varName).append(" = new ItemRequirement(\"")
				.append(escape(requirement.getDisplayName())).append("\", ")
				.append(resolved.getSymbol()).append(");\n");
		}
		out.append("\t}\n\n");

		out.append("\tprivate void setupSteps()\n");
		out.append("\t{\n");
		Set<String> emittedDefinitionIds = new LinkedHashSet<>();
		for (DraftOrderLine line : draft.getOrder())
		{
			if (line.isSectionDivider())
			{
				appendSectionSetupComment(out, line);
				continue;
			}
			DraftStep step = findDefinition(draft, line.getRefStepId());
			if (step == null || step.getStepId() == null || !emittedDefinitionIds.add(step.getStepId()))
			{
				continue;
			}
			appendDefinitionSetup(out, step, stepVarNames.get(step), requirementVarNamesByRawId, varbitReqVarNames, warnings);
		}
		out.append("\t}\n\n");

		out.append("\t@Override\n");
		out.append("\tpublic QuestStep loadStep()\n");
		out.append("\t{\n");
		out.append("\t\tinitializeRequirements();\n");
		out.append("\t\tsetupSteps();\n\n");
		List<SectionGroup> nonEmptySectionGroups = sectionGroups.stream().filter(g -> !g.slots.isEmpty()).collect(Collectors.toList());
		if (!nonEmptySectionGroups.isEmpty())
		{
			for (SectionGroup group : nonEmptySectionGroups)
			{
				String sectionTask = sectionTaskNames.get(group);
				int initIndex = group.slots.size() - 1;
				OrderedSlot initSlot = group.slots.get(initIndex);
				String initStepVar = stepVarNames.get(initSlot.definition);
				out.append("\t\t").append(sectionTask).append(" = new ConditionalStep(this, ").append(initStepVar).append(");\n");
				for (int i = 0; i < group.slots.size(); i++)
				{
					if (i == initIndex)
					{
						continue;
					}
					OrderedSlot slot = group.slots.get(i);
					String stepVar = stepVarNames.get(slot.definition);
					String reqVar = requirementExpressionForSlot(slot, requirementVarNamesByRawId, varbitReqVarNames, warnings);
					out.append("\t\t").append(sectionTask).append(".addStep(not(").append(reqVar).append("), ").append(stepVar).append(");\n");
				}
				out.append("\n");
			}

			SectionGroup lastSection = nonEmptySectionGroups.get(nonEmptySectionGroups.size() - 1);
			out.append("\t\tConditionalStep allSections = new ConditionalStep(this, ")
				.append(sectionTaskNames.get(lastSection)).append(");\n");
			for (int i = 0; i < nonEmptySectionGroups.size() - 1; i++)
			{
				SectionGroup group = nonEmptySectionGroups.get(i);
				String sectionRequirementExpression = group.slots.stream()
					.map(slot -> requirementExpressionForSlot(slot, requirementVarNamesByRawId, varbitReqVarNames, warnings))
					.collect(Collectors.joining(", "));
				out.append("\t\tallSections.addStep(nor(").append(sectionRequirementExpression).append("), ")
					.append(sectionTaskNames.get(group)).append(");\n");
			}
			out.append("\t\treturn allSections;\n");
		}
		else
		{
			out.append("\t\treturn new QuestStep(this, \"TODO: add captured steps and build ConditionalStep routing.\");\n");
		}
		out.append("\n\t\t// TODO: refine per-step requirement logic for real route progression conditions.\n");
		out.append("\t}\n\n");

		out.append("\t@Override\n");
		out.append("\tpublic List<ItemRequirement> getItemRequirements()\n");
		out.append("\t{\n");
		if (requirements.isEmpty())
		{
			out.append("\t\treturn List.of();\n");
		}
		else
		{
			out.append("\t\treturn List.of(")
				.append(requirements.stream()
					.map(r -> toVarName(r.getDisplayName(), "itemReq"))
					.collect(Collectors.joining(", ")))
				.append(");\n");
		}
		out.append("\t}\n");

		out.append("\n\t@Override\n");
		out.append("\tpublic List<PanelDetails> getPanels()\n");
		out.append("\t{\n");
		out.append("\t\tList<PanelDetails> allSteps = new ArrayList<>();\n\n");
		appendPanelsFromSections(out, sectionGroups, stepVarNames, sectionTaskNames);
		out.append("\t\treturn allSteps;\n");
		out.append("\t}\n");
		out.append("}\n");

		return new GeneratedScaffold(out.toString(), warnings);
	}

	private void appendSectionSetupComment(StringBuilder out, DraftOrderLine line)
	{
		String sectionName = line.getSuggestedVarName() == null || line.getSuggestedVarName().isBlank() ? "Unnamed Section" : line.getSuggestedVarName();
		String sectionCondition = line.getSectionCondition() == null || line.getSectionCondition().isBlank()
			? "no condition"
			: line.getSectionCondition();
		String mode = line.isSkipWhenConditionMet() ? "skip when true" : "show when true";
		out.append("\t\t// Section: ").append(escape(sectionName)).append(" [").append(mode).append(": ")
			.append(escape(sectionCondition)).append("]\n");
	}

	private void appendDefinitionSetup(
		StringBuilder out,
		DraftStep step,
		String varName,
		Map<Integer, String> requirementVarNamesByRawId,
		Map<DraftStep, String> varbitReqVarNames,
		List<String> warnings)
	{
		String instruction = step.getInstructionText() == null || step.getInstructionText().isBlank()
			? "TODO: refine instruction text"
			: step.getInstructionText();
		if (step.getKind() == StepKind.ITEM)
		{
			String requirementVarName = requirementVarNamesByRawId.get(step.getLinkedRequirementRawId());
			if (requirementVarName == null)
			{
				warnings.add("Missing linked requirement for item step ID: " + step.getRawId());
				ResolutionResult resolvedItem = symbolResolver.resolve(IdType.ITEM, step.getRawId());
				out.append("\t\t").append(varName).append(" = new ItemStep(this, \"")
					.append(escape(instruction)).append("\", new ItemRequirement(\"TODO linked item\", ")
					.append(resolvedItem.getSymbol()).append(").highlighted());\n");
			}
			else
			{
				out.append("\t\t").append(varName).append(" = new ItemStep(this, \"")
					.append(escape(instruction)).append("\", ").append(requirementVarName).append(".highlighted());\n");
			}
		}
		else
		{
			String point = worldPointLiteral(step);
			String symbol = resolveSymbol(step, warnings);
			out.append("\t\t").append(varName).append(" = new ").append(stepTypeFor(step.getKind())).append("(this, ")
				.append(symbol).append(", ").append(point).append(", \"")
				.append(escape(instruction)).append("\");\n");
		}
		if (!stepHasLinkedItemRequirement(step, requirementVarNamesByRawId))
		{
			String varbitReqVarName = varbitReqVarNames.get(step);
			out.append("\t\t").append(varbitReqVarName)
				.append(" = new VarbitRequirement(/* TODO varbit id */ 0, 1);\n");
		}
	}

	private String resolveSymbol(DraftStep step, List<String> warnings)
	{
		IdType idType = step.getKind() == StepKind.NPC ? IdType.NPC : step.getKind() == StepKind.OBJECT ? IdType.OBJECT : IdType.ITEM;
		ResolutionResult result = symbolResolver.resolve(idType, step.getRawId());
		if (result.isFallbackLiteral())
		{
			warnings.add("Unresolved " + idType.name().toLowerCase(Locale.ROOT) + " ID: " + step.getRawId());
		}
		else if (result.isAmbiguous())
		{
			warnings.add("Ambiguous " + idType.name().toLowerCase(Locale.ROOT) + " ID: " + step.getRawId() + " resolved to " + result.getSymbol());
		}
		return result.getSymbol();
	}

	private String worldPointLiteral(DraftStep step)
	{
		if (step.getWorldPoint() == null)
		{
			return "new WorldPoint(0, 0, 0) /* TODO point */";
		}
		return "new WorldPoint(" + step.getWorldPoint().getX() + ", " + step.getWorldPoint().getY() + ", " + step.getWorldPoint().getPlane() + ")";
	}

	private boolean stepHasLinkedItemRequirement(DraftStep step, Map<Integer, String> requirementVarNamesByRawId)
	{
		Integer linkedReqId = step.getLinkedRequirementRawId();
		return linkedReqId != null && requirementVarNamesByRawId.containsKey(linkedReqId);
	}

	private String requirementExpressionForSlot(
		OrderedSlot slot,
		Map<Integer, String> requirementVarNamesByRawId,
		Map<DraftStep, String> varbitReqVarNames,
		List<String> warnings)
	{
		Integer override = slot.orderLine.getLinkedRequirementRawId();
		if (override != null && override == ORDER_ROUTING_VARBIT_SENTINEL)
		{
			return varbitReqVarNames.get(slot.definition);
		}
		Integer linkedReqId = override != null ? override : slot.definition.getLinkedRequirementRawId();
		if (linkedReqId != null)
		{
			String requirementVar = requirementVarNamesByRawId.get(linkedReqId);
			if (requirementVar != null)
			{
				return requirementVar;
			}
			warnings.add("Missing linked item requirement for step ID: " + slot.definition.getRawId() + " linked requirement: " + linkedReqId);
		}
		return varbitReqVarNames.get(slot.definition);
	}

	private DraftStep findDefinition(DraftHelper draft, String stepId)
	{
		if (stepId == null || stepId.isBlank())
		{
			return null;
		}
		for (DraftStep def : draft.getStepDefinitions())
		{
			if (stepId.equals(def.getStepId()))
			{
				return def;
			}
		}
		return null;
	}

	private void appendPanelsFromSections(
		StringBuilder out,
		List<SectionGroup> sectionGroups,
		Map<DraftStep, String> stepVarNames,
		Map<SectionGroup, String> sectionTaskNames)
	{
		if (sectionGroups.isEmpty())
		{
			out.append("\t\tallSteps.add(new PanelDetails(\"Captured Steps\", List.of()));\n");
			return;
		}

		for (int i = 0; i < sectionGroups.size(); i++)
		{
			SectionGroup group = sectionGroups.get(i);
			if (group.slots.isEmpty())
			{
				continue;
			}
			String panelVar = "section" + (i + 1) + "Steps";
			String panelName = escape(group.name);

			out.append("\t\tPanelDetails ").append(panelVar).append(" = new PanelDetails(\"")
				.append(panelName).append("\", List.of(");
			for (int j = 0; j < group.slots.size(); j++)
			{
				if (j > 0)
				{
					out.append(", ");
				}
				out.append(stepVarNames.get(group.slots.get(j).definition));
			}
			out.append("));\n");
			if (group.divider != null && group.divider.getSectionCondition() != null && !group.divider.getSectionCondition().isBlank())
			{
				out.append("\t\t// TODO: evaluate section condition: ")
					.append(escape(group.divider.getSectionCondition())).append("\n");
			}
			out.append("\t\t").append(panelVar).append(".setLockingStep(").append(sectionTaskNames.get(group)).append(");\n");
			out.append("\t\tallSteps.add(").append(panelVar).append(");\n\n");
		}
	}

	private List<SectionGroup> buildSectionGroups(DraftHelper draft)
	{
		List<SectionGroup> groups = new ArrayList<>();
		List<OrderedSlot> currentGroup = new ArrayList<>();
		String currentGroupName = "Captured Steps";
		DraftOrderLine currentDivider = null;

		for (DraftOrderLine line : draft.getOrder())
		{
			if (line.isSectionDivider())
			{
				if (!currentGroup.isEmpty())
				{
					groups.add(new SectionGroup(currentGroupName, currentDivider, currentGroup));
					currentGroup = new ArrayList<>();
				}
				currentGroupName = (line.getSuggestedVarName() == null || line.getSuggestedVarName().isBlank())
					? "Section"
					: line.getSuggestedVarName();
				currentDivider = line;
				continue;
			}
			DraftStep def = findDefinition(draft, line.getRefStepId());
			if (def != null)
			{
				currentGroup.add(new OrderedSlot(line, def));
			}
		}

		if (!currentGroup.isEmpty())
		{
			groups.add(new SectionGroup(currentGroupName, currentDivider, currentGroup));
		}
		return groups;
	}

	private static final class OrderedSlot
	{
		private final DraftOrderLine orderLine;
		private final DraftStep definition;

		private OrderedSlot(DraftOrderLine orderLine, DraftStep definition)
		{
			this.orderLine = orderLine;
			this.definition = definition;
		}
	}

	private static final class SectionGroup
	{
		private final String name;
		private final DraftOrderLine divider;
		private final List<OrderedSlot> slots;

		private SectionGroup(String name, DraftOrderLine divider, List<OrderedSlot> slots)
		{
			this.name = name;
			this.divider = divider;
			this.slots = slots;
		}
	}

	private String sanitizeClassName(String className)
	{
		String cleaned = className == null ? "GeneratedQuestHelper" : className.replaceAll("[^a-zA-Z0-9_]", "");
		if (cleaned.isBlank())
		{
			return "GeneratedQuestHelper";
		}
		if (!Character.isJavaIdentifierStart(cleaned.charAt(0)))
		{
			cleaned = "Qh" + cleaned;
		}
		return Character.toUpperCase(cleaned.charAt(0)) + cleaned.substring(1);
	}

	static String toVarName(String text, String fallback)
	{
		if (text == null || text.isBlank())
		{
			return fallback;
		}

		String cleaned = text.replaceAll("<[^>]+>", " ").replaceAll("[^a-zA-Z0-9 ]", " ").trim().toLowerCase(Locale.ROOT);
		if (cleaned.isBlank())
		{
			return fallback;
		}
		String[] parts = cleaned.split("\\s+");
		StringBuilder sb = new StringBuilder(parts[0]);
		for (int i = 1; i < parts.length; i++)
		{
			if (!parts[i].isEmpty())
			{
				sb.append(Character.toUpperCase(parts[i].charAt(0))).append(parts[i].substring(1));
			}
		}
		String candidate = sb.toString();
		if (!Character.isJavaIdentifierStart(candidate.charAt(0)))
		{
			candidate = fallback + candidate;
		}
		return candidate;
	}

	private String makeUnique(String base, Set<String> used)
	{
		if (!used.contains(base))
		{
			used.add(base);
			return base;
		}

		int i = 2;
		while (used.contains(base + i))
		{
			i++;
		}
		String unique = base + i;
		used.add(unique);
		return unique;
	}

	private String stepTypeFor(StepKind kind)
	{
		switch (kind)
		{
			case NPC:
				return "NpcStep";
			case OBJECT:
				return "ObjectStep";
			default:
				return "ItemStep";
		}
	}

	private String escape(String text)
	{
		return text.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	public static final class GeneratedScaffold
	{
		private final String source;
		private final List<String> warnings;

		public GeneratedScaffold(String source, List<String> warnings)
		{
			this.source = source;
			this.warnings = warnings;
		}

		public String getSource()
		{
			return source;
		}

		public List<String> getWarnings()
		{
			return warnings;
		}
	}
}
