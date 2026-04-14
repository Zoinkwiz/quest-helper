/*
 * Copyright (c) 2026, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.maker;

import com.questhelper.maker.HelperConstructModels.OrderConditionMode;
import com.questhelper.maker.construct.DraftRoutingIds;
import com.questhelper.requirements.util.Operation;

import lombok.Getter;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

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

import static com.questhelper.maker.HelperConstructModels.DraftHelper;
import static com.questhelper.maker.HelperConstructModels.DraftOrderLine;
import static com.questhelper.maker.HelperConstructModels.DraftRequirement;
import static com.questhelper.maker.HelperConstructModels.DraftStep;
import static com.questhelper.maker.HelperConstructModels.DraftStepAttachedRequirement;
import static com.questhelper.maker.HelperConstructModels.StepAttachmentKind;
import static com.questhelper.maker.HelperConstructModels.ORDER_ROUTING_VARBIT_SENTINEL;
import static com.questhelper.maker.HelperConstructModels.StepKind;

@Singleton
public class HelperScaffoldGenerator
{
	@Inject
	public HelperScaffoldGenerator()
	{
	}

	public GeneratedScaffold generate(DraftHelper draft)
	{
		normalizeVarbitRoutingDraft(draft);
		OrderStepRequirementSupport.normalizeZoneRoutingOnDraft(draft);
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
		out.append("import com.questhelper.requirements.player.SkillRequirement;\n");
		out.append("import com.questhelper.requirements.conditional.Conditions;\n");
		out.append("import static com.questhelper.requirements.util.LogicHelper.not;\n");
		out.append("import static com.questhelper.requirements.util.LogicHelper.nor;\n");
		out.append("import com.questhelper.requirements.util.LogicType;\n");
		out.append("import com.questhelper.requirements.util.Operation;\n");
		out.append("import com.questhelper.requirements.ManualRequirement;\n");
		out.append("import com.questhelper.requirements.var.VarbitRequirement;\n");
		out.append("import com.questhelper.steps.ConditionalStep;\n");
		out.append("import com.questhelper.steps.DetailedQuestStep;\n");
		out.append("import com.questhelper.steps.NpcStep;\n");
		out.append("import com.questhelper.steps.ObjectStep;\n");
		out.append("import com.questhelper.steps.QuestStep;\n");
		out.append("import java.util.ArrayList;\n");
		out.append("import java.util.List;\n");
		out.append("import net.runelite.api.coords.WorldPoint;\n");
		out.append("import net.runelite.api.Skill;\n");
		if (OrderStepRequirementSupport.draftUsesZoneRequirement(draft))
		{
			out.append("import com.questhelper.requirements.zone.Zone;\n");
			out.append("import com.questhelper.requirements.zone.ZoneRequirement;\n");
		}
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
			for (int rid : DraftRoutingIds.mergedStepOrRequirementIds(requirement.getRawId(), requirement.getAlternateRawIds()))
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
		Map<String, String> varbitFieldByOrderSlotId = new LinkedHashMap<>();
		Map<String, String> zoneFieldByOrderSlotId = new LinkedHashMap<>();
		Map<String, String> manualFieldByOrderSlotId = new LinkedHashMap<>();
		for (DraftOrderLine line : draft.getOrder())
		{
			if (line.isSectionDivider())
			{
				continue;
			}
			DraftStep step = findDefinition(draft, line.getRefStepId());
			if (step == null)
			{
				continue;
			}
			String sid = line.getOrderSlotId();
			if (sid == null || sid.isBlank())
			{
				continue;
			}
			String stepVar = stepVarNames.get(step);
			if (OrderStepRequirementSupport.treeContainsOrderVarbitLeaf(line.getStepRequirement()))
			{
				if (stepVar == null)
				{
					continue;
				}
				String fieldName = makeUnique(stepVar + "OrderVarbitReq", usedNames);
				varbitFieldByOrderSlotId.put(sid, fieldName);
				out.append("\tVarbitRequirement ").append(fieldName).append(";\n");
			}
			if (OrderStepRequirementSupport.treeContainsOrderZoneLeaf(line.getStepRequirement()))
			{
				if (stepVar == null)
				{
					continue;
				}
				String zoneField = makeUnique(stepVar + "OrderZoneReq", usedNames);
				zoneFieldByOrderSlotId.put(sid, zoneField);
				out.append("\tZoneRequirement ").append(zoneField).append(";\n");
			}
			if (line.getStepRequirement() == null)
			{
				Integer lk = line.getLinkedRequirementRawId();
				if (lk == null || lk <= 0)
				{
					String mf = makeUnique("orderManual_" + shortSlotSuffixForJava(sid), usedNames);
					manualFieldByOrderSlotId.put(sid, mf);
					out.append("\tManualRequirement ").append(mf).append(";\n");
				}
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
			List<Integer> itemIds = DraftRoutingIds.mergedStepOrRequirementIds(requirement.getRawId(), requirement.getAlternateRawIds());
			if (itemIds.size() == 1)
			{
				out.append("\t\t").append(varName).append(" = new ItemRequirement(\"")
					.append(escape(requirement.getDisplayName())).append("\", ")
					.append(itemIds.get(0)).append(");\n");
			}
			else
			{
				out.append("\t\t").append(varName).append(" = new ItemRequirement(\"")
					.append(escape(requirement.getDisplayName())).append("\", List.of(")
					.append(itemIds.stream().map(String::valueOf).collect(Collectors.joining(", "))).append("));\n");
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
		appendOrderVarbitRequirementInits(out, draft, varbitFieldByOrderSlotId, warnings);
		appendOrderZoneRequirementInits(out, draft, zoneFieldByOrderSlotId, warnings);
		appendOrderManualRequirementInits(out, manualFieldByOrderSlotId);
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
					String branchExpr = branchRequirementExpressionForSlot(slot, requirementVarNamesByRawId, varbitFieldByOrderSlotId, zoneFieldByOrderSlotId, manualFieldByOrderSlotId, warnings);
					out.append("\t\t").append(sectionTask).append(".addStep(").append(branchExpr).append(", ").append(stepVar).append(");\n");
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
					.map(slot -> sectionNorCompletionExpressionForSlot(slot, requirementVarNamesByRawId, varbitFieldByOrderSlotId, zoneFieldByOrderSlotId, manualFieldByOrderSlotId, warnings))
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

	public void appendNpcObjectDefinitionSetup(StringBuilder out, DraftStep step, String varName, String instruction, List<String> warnings)
	{
		String point = worldPointLiteral(step);
		List<Integer> ids = DraftRoutingIds.mergedStepOrRequirementIds(step.getRawId(), step.getAlternateRawIds());
		List<String> symbols = ids.stream().map(String::valueOf).collect(Collectors.toList());
		if (symbols.isEmpty())
		{
			symbols.add(String.valueOf(step.getRawId()));
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

	public void appendTextGenericDefinitionSetup(StringBuilder out, DraftStep step, String varName, String instruction)
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

	private static int itemAttachmentQuantityForCodegen(DraftStepAttachedRequirement a)
	{
		if (a == null)
		{
			return 1;
		}
		String k = a.getKind() == null ? StepAttachmentKind.ITEM.name() : a.getKind();
		if (!StepAttachmentKind.ITEM.name().equalsIgnoreCase(k))
		{
			return 1;
		}
		int q = a.getItemQuantity();
		return Math.max(q, 1);
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
				if (a.getOrderSlotId() != null && !a.getOrderSlotId().isBlank())
				{
					continue;
				}
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
					int qty = itemAttachmentQuantityForCodegen(a);
					out.append("\t\t").append(varName).append(".addRequirement(").append(reqVar);
					if (qty > 1)
					{
						out.append(".quantity(").append(qty).append(")");
					}
					out.append(a.isAttachmentHighlighted() ? ".highlighted()" : "").append(");\n");
					continue;
				}
				int qty = itemAttachmentQuantityForCodegen(a);
				out.append("\t\t").append(varName).append(".addRequirement(new ItemRequirement(\"Item requirement\", ")
					.append(rid).append(")");
				if (qty > 1)
				{
					out.append(".quantity(").append(qty).append(")");
				}
				out.append(a.isAttachmentHighlighted() ? ".highlighted()" : "").append(");\n");
				continue;
			}
			if (StepAttachmentKind.SKILL.name().equalsIgnoreCase(k))
			{
				String skillName = a.getSkillName() == null || a.getSkillName().isBlank() ? "ATTACK" : a.getSkillName().trim().toUpperCase(Locale.ROOT);
				try
				{
					Skill.valueOf(skillName);
				}
				catch (IllegalArgumentException ex)
				{
					warnings.add("Unknown Skill on step attachment: " + a.getSkillName());
					skillName = "ATTACK";
				}
				int level = a.getSkillRequiredLevel() == null ? 1 : Math.max(1, a.getSkillRequiredLevel());
				String opName = a.getSkillOperation() == null || a.getSkillOperation().isBlank() ? "GREATER_EQUAL" : a.getSkillOperation().trim().toUpperCase(Locale.ROOT);
				Operation op;
				try
				{
					op = Operation.valueOf(opName);
				}
				catch (IllegalArgumentException ex)
				{
					warnings.add("Unknown skill Operation on step attachment: " + a.getSkillOperation());
					op = Operation.GREATER_EQUAL;
				}
				out.append("\t\t{\n");
				String display = a.getSkillDisplayText();
				if (op == Operation.GREATER_EQUAL)
				{
					if (display != null && !display.isBlank())
					{
						out.append("\t\t\tSkillRequirement sr = new SkillRequirement(Skill.").append(skillName).append(", ")
							.append(level).append(", ").append(a.isSkillCanBeBoosted()).append(", \"")
							.append(escape(display)).append("\");\n");
					}
					else
					{
						out.append("\t\t\tSkillRequirement sr = new SkillRequirement(Skill.").append(skillName).append(", ")
							.append(level).append(", ").append(a.isSkillCanBeBoosted()).append(");\n");
					}
				}
				else
				{
					out.append("\t\t\tSkillRequirement sr = new SkillRequirement(Skill.").append(skillName).append(", ")
						.append(level).append(", Operation.").append(op.name()).append(");\n");
				}
				out.append("\t\t\t").append(varName).append(".addRequirement(sr);\n");
				out.append("\t\t}\n");
				continue;
			}
			warnings.add("Unknown step attachment kind (skipped in scaffold): " + k);
		}
	}

	private String requirementExpressionForSlot(
		OrderedSlot slot,
		Map<Integer, String> requirementVarNamesByRawId,
		Map<String, String> varbitFieldByOrderSlotId,
		Map<String, String> manualFieldByOrderSlotId,
		List<String> warnings)
	{
		Integer linkedReqId = slot.orderLine.getLinkedRequirementRawId();
		if (linkedReqId != null && linkedReqId > 0)
		{
			String requirementVar = requirementVarNamesByRawId.get(linkedReqId);
			if (requirementVar != null)
			{
				return requirementVar;
			}
			warnings.add("Missing linked item requirement for step ID: " + slot.definition.getRawId() + " linked requirement: " + linkedReqId);
		}
		String sid = slot.orderLine.getOrderSlotId();
		String manual = sid == null ? null : manualFieldByOrderSlotId.get(sid);
		if (manual != null)
		{
			return manual;
		}
		warnings.add("No order-slot ManualRequirement field for slot " + sid);
		return "new ManualRequirement()";
	}

	/** Per-slot branch requirement passed to {@code ConditionalStep.addStep} (selector semantics; no outer {@code not()}). */
	private String branchRequirementExpressionForSlot(
		OrderedSlot slot,
		Map<Integer, String> requirementVarNamesByRawId,
		Map<String, String> varbitFieldByOrderSlotId,
		Map<String, String> zoneFieldByOrderSlotId,
		Map<String, String> manualFieldByOrderSlotId,
		List<String> warnings)
	{
		if (slot.orderLine.getStepRequirement() != null)
		{
			String selectorExpr = OrderStepRequirementSupport.emitSelectorJava(
				slot.orderLine.getStepRequirement(),
				slot.orderLine,
				slot.definition,
				requirementVarNamesByRawId,
				varbitFieldByOrderSlotId,
				zoneFieldByOrderSlotId,
				warnings);
			return OrderStepRequirementSupport.effectiveConditionMode(slot.orderLine) == OrderConditionMode.CONTINUE_WHEN_TRUE
				? "not(" + selectorExpr + ")"
				: selectorExpr;
		}
		return "not(" + requirementExpressionForSlot(slot, requirementVarNamesByRawId, varbitFieldByOrderSlotId, manualFieldByOrderSlotId, warnings) + ")";
	}

	/** Per-slot completion-style expression for {@code nor(...)} between sections (game "done" checks). */
	private String sectionNorCompletionExpressionForSlot(
		OrderedSlot slot,
		Map<Integer, String> requirementVarNamesByRawId,
		Map<String, String> varbitFieldByOrderSlotId,
		Map<String, String> zoneFieldByOrderSlotId,
		Map<String, String> manualFieldByOrderSlotId,
		List<String> warnings)
	{
		if (slot.orderLine.getStepRequirement() != null)
		{
			String sel = OrderStepRequirementSupport.emitSelectorJava(
				slot.orderLine.getStepRequirement(),
				slot.orderLine,
				slot.definition,
				requirementVarNamesByRawId,
				varbitFieldByOrderSlotId,
				zoneFieldByOrderSlotId,
				warnings);
			return OrderStepRequirementSupport.effectiveConditionMode(slot.orderLine) == OrderConditionMode.CONTINUE_WHEN_TRUE
				? sel
				: "not(" + sel + ")";
		}
		return requirementExpressionForSlot(slot, requirementVarNamesByRawId, varbitFieldByOrderSlotId, manualFieldByOrderSlotId, warnings);
	}

	public static String varbitFieldNameForOrderSlot(
		DraftOrderLine orderLine,
		Map<String, String> varbitFieldByOrderSlotId,
		List<String> warnings)
	{
		String sid = orderLine.getOrderSlotId();
		String field = sid == null ? null : varbitFieldByOrderSlotId.get(sid);
		if (field == null)
		{
			warnings.add("Missing varbit routing field for order slot " + sid);
			return "new VarbitRequirement(0, Operation.EQUAL, 1, null)";
		}
		return field;
	}

	public static String zoneFieldNameForOrderSlot(
		DraftOrderLine orderLine,
		Map<String, String> zoneFieldByOrderSlotId,
		List<String> warnings)
	{
		String sid = orderLine.getOrderSlotId();
		String field = sid == null ? null : zoneFieldByOrderSlotId.get(sid);
		if (field == null)
		{
			warnings.add("Missing zone routing field for order slot " + sid);
			return "new ZoneRequirement(\"In zone\", new Zone(new WorldPoint(0, 0, 0), new WorldPoint(0, 0, 0)))";
		}
		return field;
	}

	private void normalizeVarbitRoutingDraft(DraftHelper draft)
	{
		for (DraftOrderLine line : draft.getOrder())
		{
			if (line.isSectionDivider())
			{
				continue;
			}
			if (line.getOrderSlotId() == null || line.getOrderSlotId().isBlank())
			{
				line.setOrderSlotId(UUID.randomUUID().toString());
			}
		}
		LinkedHashSet<String> wantedSlots = new LinkedHashSet<>();
		for (DraftOrderLine line : draft.getOrder())
		{
			if (line.isSectionDivider())
			{
				continue;
			}
			if (OrderStepRequirementSupport.treeContainsOrderVarbitLeaf(line.getStepRequirement()))
			{
				wantedSlots.add(line.getOrderSlotId());
			}
		}
		for (DraftOrderLine line : draft.getOrder())
		{
			if (line.isSectionDivider())
			{
				continue;
			}
			if (!OrderStepRequirementSupport.treeContainsOrderVarbitLeaf(line.getStepRequirement()))
			{
				DraftStepAttachedRequirement.clearVarbitAttachmentsOnOrderLine(line);
			}
		}
		for (DraftStep step : draft.getStepDefinitions())
		{
			if (step.getAttachedRequirements() == null)
			{
				continue;
			}
			step.getAttachedRequirements().removeIf(a ->
				StepAttachmentKind.VARBIT.name().equalsIgnoreCase(a.getKind())
					&& a.getOrderSlotId() != null && !a.getOrderSlotId().isBlank()
					&& !wantedSlots.contains(a.getOrderSlotId()));
		}
	}

	private void appendOrderVarbitRequirementInits(
		StringBuilder out,
		DraftHelper draft,
		Map<String, String> varbitFieldByOrderSlotId,
		List<String> warnings)
	{
		for (DraftOrderLine line : draft.getOrder())
		{
			if (line.isSectionDivider())
			{
				continue;
			}
			if (!OrderStepRequirementSupport.treeContainsOrderVarbitLeaf(line.getStepRequirement()))
			{
				continue;
			}
			String field = varbitFieldByOrderSlotId.get(line.getOrderSlotId());
			if (field == null)
			{
				continue;
			}
			DraftStep def = findDefinition(draft, line.getRefStepId());
			DraftStepAttachedRequirement cfg = DraftStepAttachedRequirement.findOrderRoutingVarbit(line);
			if (cfg == null && def != null)
			{
				cfg = DraftStepAttachedRequirement.findRoutingVarbitForSlot(def, line.getOrderSlotId());
			}
			VarbitSpec spec = VarbitSpec.fromStepAttachment(cfg);
			int varbitId = spec.getVarbitId();
			int requiredValue = spec.getRequiredValue();
			Operation op = spec.getOperation();
			String displayArg;
			if (spec.getDisplayText() == null || spec.getDisplayText().isBlank())
			{
				displayArg = "null";
			}
			else
			{
				displayArg = "\"" + escape(spec.getDisplayText()) + "\"";
			}
			out.append("\t\t").append(field).append(" = new VarbitRequirement(")
				.append(varbitId).append(", Operation.").append(op.name()).append(", ")
				.append(requiredValue).append(", ").append(displayArg).append(");\n");
		}
	}

	private void appendOrderZoneRequirementInits(
		StringBuilder out,
		DraftHelper draft,
		Map<String, String> zoneFieldByOrderSlotId,
		List<String> warnings)
	{
		for (DraftOrderLine line : draft.getOrder())
		{
			if (line.isSectionDivider())
			{
				continue;
			}
			if (!OrderStepRequirementSupport.treeContainsOrderZoneLeaf(line.getStepRequirement()))
			{
				continue;
			}
			String field = zoneFieldByOrderSlotId.get(line.getOrderSlotId());
			if (field == null)
			{
				continue;
			}
			WorldPoint c1 = line.getZoneRoutingCorner1();
			WorldPoint c2 = line.getZoneRoutingCorner2();
			if (c1 == null || c2 == null)
			{
				warnings.add("ORDER_ZONE in tree but zone corners missing on order row " + line.getOrderSlotId());
				continue;
			}
			String disp = line.getZoneRoutingDisplayText();
			String dispArg = disp == null || disp.isBlank()
				? "\"In zone\""
				: "\"" + escape(disp) + "\"";
			out.append("\t\t").append(field).append(" = new ZoneRequirement(").append(dispArg).append(", new Zone(")
				.append("new WorldPoint(").append(c1.getX()).append(", ").append(c1.getY()).append(", ").append(c1.getPlane()).append("), ")
				.append("new WorldPoint(").append(c2.getX()).append(", ").append(c2.getY()).append(", ").append(c2.getPlane()).append(")));\n");
		}
	}

	private void appendOrderManualRequirementInits(StringBuilder out, Map<String, String> manualFieldByOrderSlotId)
	{
		for (String field : manualFieldByOrderSlotId.values())
		{
			out.append("\t\t").append(field).append(" = new ManualRequirement();\n");
		}
	}

	static String shortSlotSuffixForJava(String orderSlotId)
	{
		if (orderSlotId == null || orderSlotId.isBlank())
		{
			return "slot";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < orderSlotId.length() && sb.length() < 16; i++)
		{
			char c = orderSlotId.charAt(i);
			if (Character.isLetterOrDigit(c))
			{
				sb.append(c);
			}
		}
		return sb.length() != 0 ? sb.toString() : "slot";
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
		return escapeJavaLiteral(text);
	}

	public static String escapeJavaLiteral(String text)
	{
		if (text == null)
		{
			return "";
		}
		String singleLine = text.replace('\r', ' ').replace('\n', ' ');
		return singleLine.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	@Getter
	public static final class GeneratedScaffold
	{
		private final String source;
		private final List<String> warnings;

		public GeneratedScaffold(String source, List<String> warnings)
		{
			this.source = source;
			this.warnings = warnings;
		}

	}
}
