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
import static com.questhelper.managers.HelperConstructModels.DraftRequirement;
import static com.questhelper.managers.HelperConstructModels.DraftStep;
import static com.questhelper.managers.HelperConstructModels.IdType;
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
		List<DraftStep> steps = draft.getSteps();
		List<DraftRequirement> requirements = draft.getRequirements();

		out.append("package ").append(packageName).append(";\n\n");
		out.append("import com.questhelper.questhelpers.BasicQuestHelper;\n");
		out.append("import com.questhelper.requirements.item.ItemRequirement;\n");
		out.append("import com.questhelper.steps.ItemStep;\n");
		out.append("import com.questhelper.steps.NpcStep;\n");
		out.append("import com.questhelper.steps.ObjectStep;\n");
		out.append("import com.questhelper.steps.QuestStep;\n");
		out.append("import java.util.HashMap;\n");
		out.append("import java.util.List;\n");
		out.append("import java.util.Map;\n");
		out.append("import net.runelite.api.coords.WorldPoint;\n");
		out.append("import net.runelite.api.gameval.ItemID;\n");
		out.append("import net.runelite.api.gameval.NpcID;\n");
		out.append("import net.runelite.api.gameval.ObjectID;\n\n");

		out.append("public class ").append(className).append(" extends BasicQuestHelper\n");
		out.append("{\n");
		out.append("\t// Captured requirements\n");
		for (DraftRequirement requirement : requirements)
		{
			out.append("\tItemRequirement ").append(toVarName(requirement.getDisplayName(), "itemReq")).append(";\n");
		}

		out.append("\n\t// Captured steps\n");
		Map<DraftStep, String> stepVarNames = new LinkedHashMap<>();
		Set<String> usedNames = new LinkedHashSet<>();
		for (DraftStep step : steps)
		{
			String stepType = stepTypeFor(step.getKind());
			String candidate = toVarName(step.getSuggestedVarName(), "step");
			String unique = makeUnique(candidate, usedNames);
			stepVarNames.put(step, unique);
			out.append("\t").append(stepType).append(" ").append(unique).append(";\n");
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
		for (DraftStep step : steps)
		{
			String varName = stepVarNames.get(step);
			String instruction = step.getInstructionText() == null || step.getInstructionText().isBlank()
				? "TODO: refine instruction text"
				: step.getInstructionText();
			String point = worldPointLiteral(step);
			String symbol = resolveSymbol(step, warnings);
			out.append("\t\t").append(varName).append(" = new ").append(stepTypeFor(step.getKind())).append("(this, ")
				.append(symbol).append(", ").append(point).append(", \"")
				.append(escape(instruction)).append("\");\n");
		}
		out.append("\t}\n\n");

		out.append("\t@Override\n");
		out.append("\tpublic Map<Integer, QuestStep> loadSteps()\n");
		out.append("\t{\n");
		out.append("\t\tinitializeRequirements();\n");
		out.append("\t\tsetupSteps();\n\n");
		out.append("\t\tvar steps = new HashMap<Integer, QuestStep>();\n");
		if (!steps.isEmpty())
		{
			String firstStepVar = stepVarNames.get(steps.get(0));
			out.append("\t\tsteps.put(0, ").append(firstStepVar).append(");\n");
			out.append("\t\tsteps.put(1, ").append(firstStepVar).append(");\n");
		}
		else
		{
			out.append("\t\t// TODO: add state->step mapping once steps are captured.\n");
		}
		out.append("\n\t\t// TODO: replace baseline mapping with full ConditionalStep graph logic.\n");
		out.append("\t\treturn steps;\n");
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
		out.append("}\n");

		return new GeneratedScaffold(out.toString(), warnings);
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
