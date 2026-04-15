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
package com.questhelper.maker.taskstroute;

import com.google.gson.JsonObject;
import com.questhelper.maker.HelperScaffoldGenerator;
import com.questhelper.maker.taskstroute.TasksTrackerRouteDto.RouteCustomItemDto;
import com.questhelper.maker.taskstroute.TasksTrackerRouteDto.RouteInteractDto;
import com.questhelper.maker.taskstroute.TasksTrackerRouteDto.RouteItemDto;
import com.questhelper.maker.taskstroute.TasksTrackerRouteDto.RouteLocationDto;
import com.questhelper.maker.taskstroute.TasksTrackerRouteDto.RouteSectionDto;
import net.runelite.api.coords.WorldPoint;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.questhelper.maker.HelperConstructModels.DraftHelper;
import static com.questhelper.maker.HelperConstructModels.DraftOrderLine;
import static com.questhelper.maker.HelperConstructModels.DraftOrderStepRequirement;
import static com.questhelper.maker.HelperConstructModels.DraftStep;
import static com.questhelper.maker.HelperConstructModels.StepKind;

/**
 * Builds a maker {@link DraftHelper} from Tasks Tracker route JSON.
 * Optional {@code hubByStructId} (e.g. from {@code LEAGUE_5.full.json}) enriches name, description, sortId, and location fallbacks when present.
 */
public final class TasksTrackerRouteImporter
{
	private TasksTrackerRouteImporter()
	{
	}

	/**
	 * @param hubByStructId optional structId → hub row; may be {@code null} or empty to import from route fields only
	 *                      ({@code taskId}, {@code note}, {@code location}, {@code interact}).
	 */
	public static DraftHelper importRoute(
		TasksTrackerRouteDto route,
		Map<Integer, JsonObject> hubByStructId)
	{
		DraftHelper draft = new DraftHelper();
		draft.setQuestName(trimOrDefault(route.getName(), "Imported Route"));
		draft.setClassName("ImportedRouteHelper");
		draft.setPackagePath("com.questhelper.helpers.quests.generated");
		draft.setHelperType("BasicQuestHelper");
		draft.getRequirements().clear();
		draft.getStepDefinitions().clear();
		draft.getOrder().clear();
		List<RouteSectionDto> sections = route.getSections();
		for (int si = 0; si < sections.size(); si++)
		{
			RouteSectionDto sec = sections.get(si);
			if (si > 0)
			{
				draft.getOrder().add(newSectionDivider(sec.getName()));
			}
			else if (sec.getItems() == null || sec.getItems().isEmpty())
			{
				draft.getOrder().add(newSectionDivider(sec.getName()));
			}

			List<RouteItemDto> items = sec.getItems() != null ? sec.getItems() : List.of();
			for (RouteItemDto item : items)
			{
				if (item.getTaskId() != null)
				{
					appendLeagueTaskRow(draft, item, hubByStructId);
				}
				else if (item.getCustomItem() != null)
				{
					appendCustomItemRow(draft, item);
				}
			}
		}
		return draft;
	}

	private static void appendLeagueTaskRow(
		DraftHelper draft,
		RouteItemDto item,
		Map<Integer, JsonObject> hubByStructId)
	{
		DraftStep step = newLeagueTaskStep(item, hubByStructId);
		draft.getStepDefinitions().add(step);
		draft.getOrder().add(newOrderRefLine(step.getStepId(), truncate(step.getInstructionText(), 120)));
	}

	private static void appendCustomItemRow(DraftHelper draft, RouteItemDto item)
	{
		RouteCustomItemDto c = item.getCustomItem();
		DraftStep step = newCustomItemStep(item, c != null && c.getId() != null && !c.getId().isBlank()
			? c.getId().trim()
			: UUID.randomUUID().toString());
		draft.getStepDefinitions().add(step);
		draft.getOrder().add(newOrderRefLine(step.getStepId(), truncate(c == null ? "" : c.getLabel(), 120)));
	}

	public static DraftOrderLine newSectionDivider(String sectionName)
	{
		DraftOrderLine div = new DraftOrderLine();
		div.setOrderSlotId(UUID.randomUUID().toString());
		div.setSectionDivider(true);
		div.setSuggestedVarName(trimOrDefault(sectionName, "Section"));
		div.setSectionCondition("");
		div.setSkipWhenConditionMet(false);
		div.setRefStepId(null);
		div.setLinkedRequirementRawId(null);
		return div;
	}

	public static DraftStep newLeagueTaskStep(RouteItemDto item, Map<Integer, JsonObject> hubByStructId)
	{
		int structId = item.getTaskId();
		JsonObject hub = hubByStructId != null ? hubByStructId.get(structId) : null;
		int sortId = 0;
		String name;
		String desc = "";
		if (hub != null)
		{
			sortId = hub.has("sortId") ? hub.get("sortId").getAsInt() : 0;
			name = jsonString(hub, "name");
			desc = jsonString(hub, "description");
			if (name.isBlank())
			{
				name = displayNameFromRouteNote(item.getNote(), structId);
			}
		}
		else
		{
			name = displayNameFromRouteNote(item.getNote(), structId);
		}

		StepKind kind;
		int rawId;
		RouteInteractDto inter = item.getInteract();
		Integer firstNpc = firstId(inter == null ? null : inter.getNpc());
		Integer firstObj = firstId(inter == null ? null : inter.getObject());
		if (firstNpc != null)
		{
			kind = StepKind.NPC;
			rawId = firstNpc;
		}
		else if (firstObj != null)
		{
			kind = StepKind.OBJECT;
			rawId = firstObj;
		}
		else
		{
			kind = StepKind.TEXT;
			rawId = 0;
		}

		String stepId = structId != 0 ? String.valueOf(structId) : UUID.randomUUID().toString();
		DraftStep step = new DraftStep();
		step.setStepId(stepId);
		step.setKind(kind);
		step.setSectionDivider(false);
		step.setStructId(structId);
		step.setRawId(rawId);
		step.setOption("");
		step.setTargetText("");
		String varSuffix = hub != null ? String.valueOf(sortId) : String.valueOf(structId);
		step.setSuggestedVarName(HelperScaffoldGenerator.toVarName(name, "task") + varSuffix);
		step.setInstructionText(instructionFromNoteOrHub(item.getNote(), name, desc));
		step.setPanelName("League tasks");
		step.setSectionCondition("");
		step.setSkipWhenConditionMet(false);
		applyWorldPoint(step, item.getLocation(), hub);
		if (inter != null && kind == StepKind.NPC && inter.getNpc() != null && inter.getNpc().size() > 1)
		{
			for (int i = 1; i < inter.getNpc().size(); i++)
			{
				Integer x = inter.getNpc().get(i);
				if (x != null)
				{
					step.getAlternateRawIds().add(x);
				}
			}
		}
		else if (inter != null && kind == StepKind.OBJECT && inter.getObject() != null && inter.getObject().size() > 1)
		{
			for (int i = 1; i < inter.getObject().size(); i++)
			{
				Integer x = inter.getObject().get(i);
				if (x != null)
				{
					step.getAlternateRawIds().add(x);
				}
			}
		}
		return step;
	}

	public static DraftStep newCustomItemStep(RouteItemDto item, String stepId)
	{
		RouteCustomItemDto c = item.getCustomItem();
		DraftStep step = new DraftStep();
		step.setStepId(stepId == null || stepId.isBlank() ? UUID.randomUUID().toString() : stepId.trim());
		step.setKind(StepKind.TEXT);
		step.setSectionDivider(false);
		step.setStructId(null);
		step.setRawId(0);
		step.setOption("");
		step.setTargetText("");
		step.setSuggestedVarName(HelperScaffoldGenerator.toVarName(
			c != null && c.getLabel() != null ? c.getLabel() : "custom", "step"));
		StringBuilder instr = new StringBuilder();
		String label = c != null && c.getLabel() != null ? c.getLabel().trim() : "";
		String description = c != null && c.getDescription() != null ? c.getDescription().trim() : "";
		// Avoid polluting step text with internal-style var labels (camelCase/no spaces) when we already have richer text.
		boolean includeLabel = !label.isBlank() && (!looksLikeInternalVarLabel(label) || description.isBlank());
		if (includeLabel)
		{
			instr.append(label);
		}
		if (!description.isBlank())
		{
			if (instr.length() > 0)
			{
				instr.append("\n\n");
			}
			instr.append(description);
		}
		if (instr.length() == 0)
		{
			instr.append("Custom step");
		}
		String routeNote = item.getNote();
		if (routeNote != null && !routeNote.isBlank())
		{
			instr.append("\n\n").append(routeNote.trim());
		}
		if (!instr.toString().endsWith("."))
		{
			instr.append(".");
		}
		step.setInstructionText(instr.toString());
		step.setPanelName("League tasks");
		step.setSectionCondition("");
		step.setSkipWhenConditionMet(false);
		applyWorldPoint(step, item.getLocation(), null);
		return step;
	}

	private static boolean looksLikeInternalVarLabel(String label)
	{
		if (label == null)
		{
			return false;
		}
		String trimmed = label.trim();
		if (trimmed.isEmpty())
		{
			return false;
		}
		// Treat no-whitespace alphanumeric/underscore labels with mixed case as likely internal vars.
		boolean hasWhitespace = trimmed.chars().anyMatch(Character::isWhitespace);
		if (hasWhitespace)
		{
			return false;
		}
		boolean validChars = trimmed.matches("[A-Za-z0-9_]+");
		boolean hasLower = trimmed.chars().anyMatch(Character::isLowerCase);
		boolean hasUpper = trimmed.chars().anyMatch(Character::isUpperCase);
		return validChars && hasLower && hasUpper;
	}

	public static DraftOrderLine newOrderRefLine(String stepId, String routingDisplayText)
	{
		DraftOrderLine ord = new DraftOrderLine();
		ord.setOrderSlotId(UUID.randomUUID().toString());
		ord.setSectionDivider(false);
		ord.setSuggestedVarName(null);
		ord.setSectionCondition("");
		ord.setSkipWhenConditionMet(false);
		ord.setRefStepId(stepId);
		ord.setLinkedRequirementRawId(null);
		ord.setStepRequirement(DraftOrderStepRequirement.varbit(
			0,
			0,
			"EQUAL",
			truncate(routingDisplayText, 120)));
		return ord;
	}

	private static Integer firstId(List<Integer> list)
	{
		if (list == null || list.isEmpty())
		{
			return null;
		}
		return list.get(0);
	}

	private static void applyWorldPoint(DraftStep step, RouteLocationDto itemLoc, JsonObject hub)
	{
		RouteLocationDto loc = itemLoc;
		if (loc == null && hub != null && hub.has("location") && hub.get("location").isJsonObject())
		{
			JsonObject l = hub.getAsJsonObject("location");
			if (l.has("x") && l.has("y"))
			{
				loc = new RouteLocationDto();
				loc.setX(l.get("x").getAsInt());
				loc.setY(l.get("y").getAsInt());
				loc.setPlane(l.has("plane") ? l.get("plane").getAsInt() : 0);
			}
		}
		if (loc == null)
		{
			step.setWorldPoint(null);
			return;
		}
		step.setWorldPoint(new WorldPoint(loc.getX(), loc.getY(), loc.getPlane()));
	}

	/** First line of note, or short fallback label for var names / varbit display when hub is not used. */
	private static String displayNameFromRouteNote(String note, int structId)
	{
		if (note != null && !note.isBlank())
		{
			String first = note.trim().split("\\R", 2)[0].trim();
			if (!first.isEmpty())
			{
				return first.length() > 80 ? first.substring(0, 80) : first;
			}
		}
		return "Task" + structId;
	}

	private static String instructionFromNoteOrHub(String note, String name, String desc)
	{
		if (note != null && !note.isBlank())
		{
			String n = note.trim();
			return n.endsWith(".") ? n : n + ".";
		}
		String base = name != null && !name.isBlank() ? name.trim() : "Task";
		if (desc != null && !desc.isBlank() && !desc.trim().equals(base))
		{
			String text = base + "\n\n" + desc.trim();
			if (text.contains("\n\n"))
			{
				text = text.split("\n\n", 2)[0].trim();
			}
			return text + ".";
		}
		return base + ".";
	}

	private static String jsonString(JsonObject o, String key)
	{
		if (!o.has(key) || o.get(key).isJsonNull())
		{
			return "";
		}
		return o.get(key).getAsString();
	}

	private static String truncate(String s, int max)
	{
		if (s == null)
		{
			return "";
		}
		return s.length() <= max ? s : s.substring(0, max);
	}

	private static String trimOrDefault(String s, String d)
	{
		if (s == null || s.isBlank())
		{
			return d;
		}
		return s.trim();
	}
}
