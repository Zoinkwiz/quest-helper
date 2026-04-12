package com.questhelper.managers;

import com.questhelper.managers.GamevalSymbolResolver.ResolutionResult;

import com.questhelper.requirements.util.Operation;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.questhelper.managers.HelperConstructModels.DraftHelper;
import static com.questhelper.managers.HelperConstructModels.DraftOrderLine;
import static com.questhelper.managers.HelperConstructModels.DraftRequirement;
import static com.questhelper.managers.HelperConstructModels.DraftStep;
import static com.questhelper.managers.HelperConstructModels.DraftStepAttachedRequirement;
import static com.questhelper.managers.HelperConstructModels.DraftVarbitRequirement;
import static com.questhelper.managers.HelperConstructModels.StepAttachmentKind;
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
		normalizeVarbitRoutingDraft(draft);
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
		out.append("import com.questhelper.requirements.util.Operation;\n");
		out.append("import com.questhelper.requirements.var.VarbitRequirement;\n");
		out.append("import com.questhelper.steps.ConditionalStep;\n");
		out.append("import com.questhelper.steps.DetailedQuestStep;\n");
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
		for (DraftRequirement requirement : requirements)
		{
			String var = toVarName(requirement.getDisplayName(), "itemReq");
			for (int rid : HelperConstructManager.mergedStepOrRequirementIds(requirement.getRawId(), requirement.getAlternateRawIds()))
			{
				requirementVarNamesByRawId.put(rid, var);
			}
		}
		for (DraftStep step : definitions)
		{
			String stepType = stepTypeFor(step.getKind());
			String candidate = toVarName(step.getSuggestedVarName(), "step");
			String unique = makeUnique(candidate, usedNames);
			stepVarNames.put(step, unique);
			out.append("\t").append(stepType).append(" ").append(unique).append(";\n");
		}
		Map<String, String> varbitFieldByOrderLineId = new LinkedHashMap<>();
		for (DraftOrderLine line : draft.getOrder())
		{
			if (line.isSectionDivider())
			{
				continue;
			}
			if (!orderRowUsesDefaultOrVarbitRouting(line.getLinkedRequirementRawId()))
			{
				continue;
			}
			DraftStep step = findDefinition(draft, line.getRefStepId());
			if (step == null)
			{
				continue;
			}
			String stepVar = stepVarNames.get(step);
			if (stepVar == null)
			{
				continue;
			}
			String fieldName = makeUnique(stepVar + "VarbitReq", usedNames);
			varbitFieldByOrderLineId.put(line.getLineId(), fieldName);
			out.append("\tVarbitRequirement ").append(fieldName).append(";\n");
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
			List<Integer> itemIds = HelperConstructManager.mergedStepOrRequirementIds(requirement.getRawId(), requirement.getAlternateRawIds());
			if (itemIds.size() == 1)
			{
				ResolutionResult resolved = symbolResolver.resolve(IdType.ITEM, itemIds.get(0));
				if (resolved.isFallbackLiteral())
				{
					warnings.add("Unresolved item ID: " + itemIds.get(0));
				}
				out.append("\t\t").append(varName).append(" = new ItemRequirement(\"")
					.append(escape(requirement.getDisplayName())).append("\", ")
					.append(resolved.getSymbol()).append(");\n");
			}
			else
			{
				List<String> syms = new ArrayList<>();
				for (int id : itemIds)
				{
					ResolutionResult resolved = symbolResolver.resolve(IdType.ITEM, id);
					if (resolved.isFallbackLiteral())
					{
						warnings.add("Unresolved item ID: " + id);
					}
					syms.add(resolved.getSymbol());
				}
				out.append("\t\t").append(varName).append(" = new ItemRequirement(\"")
					.append(escape(requirement.getDisplayName())).append("\", List.of(")
					.append(String.join(", ", syms)).append("));\n");
			}
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
			appendDefinitionSetup(out, draft, step, stepVarNames.get(step), requirementVarNamesByRawId, warnings);
		}
		appendOrderVarbitRequirementInits(out, draft, varbitFieldByOrderLineId, warnings);
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
					String reqVar = requirementExpressionForSlot(slot, requirementVarNamesByRawId, varbitFieldByOrderLineId, warnings);
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
					.map(slot -> requirementExpressionForSlot(slot, requirementVarNamesByRawId, varbitFieldByOrderLineId, warnings))
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
		DraftHelper draft,
		DraftStep step,
		String varName,
		Map<Integer, String> requirementVarNamesByRawId,
		List<String> warnings)
	{
		String instruction = step.getInstructionText() == null || step.getInstructionText().isBlank()
			? "TODO: refine instruction text"
			: step.getInstructionText();
		ConstructStepKindHandlers.ConstructStepKindHandler handler = ConstructStepKindHandlers.forStepKind(step.getKind());
		if (handler != null)
		{
			handler.appendScaffoldDefinitionSetup(new ConstructStepKindHandlers.ScaffoldDefinitionSetupContext(
				this, out, draft, step, varName, instruction, requirementVarNamesByRawId, warnings));
		}
		appendExtraStepRequirements(out, draft, varName, step, requirementVarNamesByRawId, warnings);
	}

	void appendNpcObjectDefinitionSetup(StringBuilder out, DraftStep step, String varName, String instruction, List<String> warnings)
	{
		String point = worldPointLiteral(step);
		List<Integer> ids = HelperConstructManager.mergedStepOrRequirementIds(step.getRawId(), step.getAlternateRawIds());
		IdType idType = step.getKind() == StepKind.NPC ? IdType.NPC : IdType.OBJECT;
		List<String> symbols = new ArrayList<>();
		for (int id : ids)
		{
			ResolutionResult result = symbolResolver.resolve(idType, id);
			if (result.isFallbackLiteral())
			{
				warnings.add("Unresolved " + idType.name().toLowerCase(Locale.ROOT) + " ID: " + id);
			}
			else if (result.isAmbiguous())
			{
				warnings.add("Ambiguous " + idType.name().toLowerCase(Locale.ROOT) + " ID: " + id + " resolved to " + result.getSymbol());
			}
			symbols.add(result.getSymbol());
		}
		if (symbols.isEmpty())
		{
			symbols.add(resolveSymbol(step, warnings));
		}
		if (step.getKind() == StepKind.NPC)
		{
			if (symbols.size() == 1)
			{
				out.append("\t\t").append(varName).append(" = new NpcStep(this, ")
					.append(symbols.get(0)).append(", ").append(point).append(", \"")
					.append(escape(instruction)).append("\", true);\n");
			}
			else
			{
				out.append("\t\t").append(varName).append(" = new NpcStep(this, new int[]{")
					.append(String.join(", ", symbols)).append("}, ").append(point).append(", \"")
					.append(escape(instruction)).append("\", true);\n");
			}
		}
		else
		{
			String lead = symbols.get(0);
			out.append("\t\t").append(varName).append(" = new ObjectStep(this, ")
				.append(lead).append(", ").append(point).append(", \"")
				.append(escape(instruction)).append("\", true");
			if (symbols.size() > 1)
			{
				out.append(").addAlternateObjects(")
					.append(String.join(", ", symbols.subList(1, symbols.size())))
					.append(");\n");
			}
			else
			{
				out.append(");\n");
			}
		}
	}

	void appendTextGenericDefinitionSetup(StringBuilder out, DraftStep step, String varName, String instruction)
	{
		if (step.getWorldPoint() != null)
		{
			out.append("\t\t").append(varName).append(" = new DetailedQuestStep(this, ")
				.append(worldPointLiteral(step)).append(", \"")
				.append(escape(instruction)).append("\");\n");
		}
		else
		{
			out.append("\t\t").append(varName).append(" = new DetailedQuestStep(this, \"")
				.append(escape(instruction)).append("\");\n");
		}
	}

	String resolveSymbol(DraftStep step, List<String> warnings)
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

	String worldPointLiteral(DraftStep step)
	{
		if (step.getWorldPoint() == null)
		{
			return "new WorldPoint(0, 0, 0) /* TODO point */";
		}
		return "new WorldPoint(" + step.getWorldPoint().getX() + ", " + step.getWorldPoint().getY() + ", " + step.getWorldPoint().getPlane() + ")";
	}

	/** {@code null} when the draft has no world point (ItemStep overload without coordinates). */
	String finiteWorldPointLiteral(DraftStep step)
	{
		if (step.getWorldPoint() == null)
		{
			return null;
		}
		return "new WorldPoint(" + step.getWorldPoint().getX() + ", " + step.getWorldPoint().getY() + ", " + step.getWorldPoint().getPlane() + ")";
	}

	void appendExtraStepRequirements(
		StringBuilder out,
		DraftHelper draft,
		String varName,
		DraftStep step,
		Map<Integer, String> requirementVarNamesByRawId,
		List<String> warnings)
	{
		if (step.getAttachedRequirements() == null || step.getAttachedRequirements().isEmpty())
		{
			return;
		}
		for (DraftStepAttachedRequirement a : step.getAttachedRequirements())
		{
			String k = a.getKind() == null ? StepAttachmentKind.ITEM.name() : a.getKind();
			if (StepAttachmentKind.VARBIT.name().equalsIgnoreCase(k))
			{
				if (a.getVarbitOperation() != null && !a.getVarbitOperation().isBlank())
				{
					try
					{
						Operation.valueOf(a.getVarbitOperation().trim());
					}
					catch (IllegalArgumentException ex)
					{
						warnings.add("Unknown varbit Operation on step attachment: " + a.getVarbitOperation());
					}
				}
				VarbitSpec spec = VarbitSpec.fromStepAttachment(a);
				String displayArg = spec.getDisplayText() == null || spec.getDisplayText().isBlank()
					? "null"
					: "\"" + escape(spec.getDisplayText()) + "\"";
				out.append("\t\t").append(varName).append(".addRequirement(new VarbitRequirement(")
					.append(spec.getVarbitId()).append(", Operation.").append(spec.getOperation().name()).append(", ")
					.append(spec.getRequiredValue()).append(", ").append(displayArg).append("));\n");
				continue;
			}
			if (StepAttachmentKind.ITEM.name().equalsIgnoreCase(k))
			{
				Integer rid = a.getItemRawId();
				if (rid == null)
				{
					continue;
				}
				String reqVar = requirementVarNamesByRawId != null ? requirementVarNamesByRawId.get(rid) : null;
				if (reqVar != null)
				{
					Integer orderLink = firstConcreteOrderItemLinkForStep(draft, step.getStepId());
					boolean highlight = a.isAttachmentHighlighted() || (orderLink != null && orderLink.equals(rid));
					out.append("\t\t").append(varName).append(".addRequirement(").append(reqVar)
						.append(highlight ? ".highlighted()" : "").append(");\n");
					continue;
				}
				ResolutionResult resolved = symbolResolver.resolve(IdType.ITEM, rid);
				if (resolved.isFallbackLiteral())
				{
					warnings.add("Unresolved extra item ID on step: " + rid);
				}
				out.append("\t\t").append(varName).append(".addRequirement(new ItemRequirement(\"Item requirement\", ")
					.append(resolved.getSymbol()).append(")").append(a.isAttachmentHighlighted() ? ".highlighted()" : "").append(");\n");
				continue;
			}
			warnings.add("Unknown step attachment kind (skipped in scaffold): " + k);
		}
	}

	static Integer firstConcreteOrderItemLinkForStep(DraftHelper draft, String stepId)
	{
		if (stepId == null || stepId.isBlank())
		{
			return null;
		}
		for (DraftOrderLine line : draft.getOrder())
		{
			if (line.isSectionDivider())
			{
				continue;
			}
			if (!stepId.equals(line.getRefStepId()))
			{
				continue;
			}
			Integer o = line.getLinkedRequirementRawId();
			if (o != null && o != ORDER_ROUTING_VARBIT_SENTINEL)
			{
				return o;
			}
		}
		return null;
	}

	private String requirementExpressionForSlot(
		OrderedSlot slot,
		Map<Integer, String> requirementVarNamesByRawId,
		Map<String, String> varbitFieldByOrderLineId,
		List<String> warnings)
	{
		Integer override = slot.orderLine.getLinkedRequirementRawId();
		if (override != null && override == ORDER_ROUTING_VARBIT_SENTINEL)
		{
			return varbitFieldNameForOrderLine(slot.orderLine, varbitFieldByOrderLineId, warnings);
		}
		Integer linkedReqId = override;
		if (linkedReqId != null)
		{
			String requirementVar = requirementVarNamesByRawId.get(linkedReqId);
			if (requirementVar != null)
			{
				return requirementVar;
			}
			warnings.add("Missing linked item requirement for step ID: " + slot.definition.getRawId() + " linked requirement: " + linkedReqId);
		}
		return varbitFieldNameForOrderLine(slot.orderLine, varbitFieldByOrderLineId, warnings);
	}

	private static String varbitFieldNameForOrderLine(
		DraftOrderLine orderLine,
		Map<String, String> varbitFieldByOrderLineId,
		List<String> warnings)
	{
		String lid = orderLine.getLineId();
		String field = lid == null ? null : varbitFieldByOrderLineId.get(lid);
		if (field == null)
		{
			warnings.add("Missing varbit routing field for order line " + lid);
			return "new VarbitRequirement(0, Operation.EQUAL, 1, null)";
		}
		return field;
	}

	private static boolean orderRowUsesDefaultOrVarbitRouting(Integer linkedRequirementRawId)
	{
		return linkedRequirementRawId == null || linkedRequirementRawId == ORDER_ROUTING_VARBIT_SENTINEL;
	}

	private void normalizeVarbitRoutingDraft(DraftHelper draft)
	{
		for (DraftOrderLine line : draft.getOrder())
		{
			if (line.isSectionDivider())
			{
				continue;
			}
			if (line.getLineId() == null || line.getLineId().isBlank())
			{
				line.setLineId(UUID.randomUUID().toString());
			}
		}
		LinkedHashSet<String> wanted = new LinkedHashSet<>();
		for (DraftOrderLine line : draft.getOrder())
		{
			if (line.isSectionDivider())
			{
				continue;
			}
			if (orderRowUsesDefaultOrVarbitRouting(line.getLinkedRequirementRawId()))
			{
				wanted.add(line.getLineId());
			}
		}
		draft.getVarbitRequirements().removeIf(v -> v.getLineId() == null || !wanted.contains(v.getLineId()));
		for (String lineId : wanted)
		{
			boolean has = false;
			for (DraftVarbitRequirement v : draft.getVarbitRequirements())
			{
				if (lineId.equals(v.getLineId()))
				{
					has = true;
					break;
				}
			}
			if (!has)
			{
				draft.getVarbitRequirements().add(new DraftVarbitRequirement(lineId, 0, 1, "EQUAL", null, null));
			}
		}
	}

	private void appendOrderVarbitRequirementInits(
		StringBuilder out,
		DraftHelper draft,
		Map<String, String> varbitFieldByOrderLineId,
		List<String> warnings)
	{
		Map<String, DraftVarbitRequirement> byLineId = new LinkedHashMap<>();
		for (DraftVarbitRequirement v : draft.getVarbitRequirements())
		{
			if (v.getLineId() != null && !v.getLineId().isBlank())
			{
				byLineId.put(v.getLineId(), v);
			}
		}
		for (DraftOrderLine line : draft.getOrder())
		{
			if (line.isSectionDivider())
			{
				continue;
			}
			if (!orderRowUsesDefaultOrVarbitRouting(line.getLinkedRequirementRawId()))
			{
				continue;
			}
			String field = varbitFieldByOrderLineId.get(line.getLineId());
			if (field == null)
			{
				continue;
			}
			DraftVarbitRequirement cfg = byLineId.get(line.getLineId());
			int varbitId = cfg == null ? 0 : cfg.getVarbitId();
			int requiredValue = cfg == null ? 1 : cfg.getRequiredValue();
			Operation op = Operation.EQUAL;
			if (cfg != null && cfg.getOperation() != null && !cfg.getOperation().isBlank())
			{
				try
				{
					op = Operation.valueOf(cfg.getOperation().trim());
				}
				catch (IllegalArgumentException ex)
				{
					warnings.add("Unknown varbit Operation '" + cfg.getOperation() + "' for order line " + line.getLineId() + "; using EQUAL.");
				}
			}
			String displayArg;
			if (cfg == null || cfg.getDisplayText() == null || cfg.getDisplayText().isBlank())
			{
				displayArg = "null";
			}
			else
			{
				displayArg = "\"" + escape(cfg.getDisplayText()) + "\"";
			}
			out.append("\t\t").append(field).append(" = new VarbitRequirement(")
				.append(varbitId).append(", Operation.").append(op.name()).append(", ")
				.append(requiredValue).append(", ").append(displayArg).append(");\n");
		}
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

	public static String toVarName(String text, String fallback)
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

	String stepTypeFor(StepKind kind)
	{
		switch (kind)
		{
			case NPC:
				return "NpcStep";
			case OBJECT:
				return "ObjectStep";
			case TEXT:
				return "DetailedQuestStep";
			case ITEM:
				return "DetailedQuestStep";
			default:
				return "DetailedQuestStep";
		}
	}

	String escape(String text)
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
