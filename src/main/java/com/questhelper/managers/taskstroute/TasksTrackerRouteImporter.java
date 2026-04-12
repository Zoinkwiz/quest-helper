package com.questhelper.managers.taskstroute;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.questhelper.managers.HelperScaffoldGenerator;
import com.questhelper.managers.taskstroute.TasksTrackerRouteDto.RouteCustomItemDto;
import com.questhelper.managers.taskstroute.TasksTrackerRouteDto.RouteInteractDto;
import com.questhelper.managers.taskstroute.TasksTrackerRouteDto.RouteItemDto;
import com.questhelper.managers.taskstroute.TasksTrackerRouteDto.RouteLocationDto;
import com.questhelper.managers.taskstroute.TasksTrackerRouteDto.RouteSectionDto;
import net.runelite.api.coords.WorldPoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.questhelper.maker.HelperConstructModels.DraftHelper;
import static com.questhelper.maker.HelperConstructModels.DraftOrderLine;
import static com.questhelper.maker.HelperConstructModels.DraftStep;
import static com.questhelper.maker.HelperConstructModels.DraftStepAttachedRequirement;
import static com.questhelper.maker.HelperConstructModels.StepKind;

/**
 * Builds a maker {@link DraftHelper} from Tasks Tracker route JSON.
 * Optional {@code hubByStructId} (e.g. from {@code LEAGUE_5.full.json}) enriches name, description, sortId, and location fallbacks when present.
 */
public final class TasksTrackerRouteImporter
{
	private static final Gson HUB_PARSE_GSON = new Gson();

	private TasksTrackerRouteImporter()
	{
	}

	public static Map<Integer, JsonObject> parseLeagueFullHub(String leagueFullJson) throws JsonSyntaxException
	{
		JsonElement root = HUB_PARSE_GSON.fromJson(leagueFullJson.trim(), JsonElement.class);
		if (root == null || !root.isJsonArray())
		{
			throw new JsonSyntaxException("LEAGUE_5.full.json must be a JSON array of tasks");
		}
		JsonArray arr = root.getAsJsonArray();
		Map<Integer, JsonObject> byStruct = new HashMap<>();
		for (JsonElement el : arr)
		{
			if (!el.isJsonObject())
			{
				continue;
			}
			JsonObject o = el.getAsJsonObject();
			if (!o.has("structId"))
			{
				continue;
			}
			int sid = o.get("structId").getAsInt();
			byStruct.put(sid, o);
		}
		return byStruct;
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
				DraftOrderLine div = new DraftOrderLine();
				div.setOrderSlotId(UUID.randomUUID().toString());
				div.setSectionDivider(true);
				div.setSuggestedVarName(trimOrDefault(sec.getName(), "Section"));
				div.setSectionCondition("");
				div.setSkipWhenConditionMet(false);
				div.setRefStepId(null);
				div.setLinkedRequirementRawId(null);
				draft.getOrder().add(div);
			}
			else if (sec.getItems() == null || sec.getItems().isEmpty())
			{
				DraftOrderLine div = new DraftOrderLine();
				div.setOrderSlotId(UUID.randomUUID().toString());
				div.setSectionDivider(true);
				div.setSuggestedVarName(trimOrDefault(sec.getName(), "Section"));
				div.setSectionCondition("");
				div.setSkipWhenConditionMet(false);
				div.setRefStepId(null);
				div.setLinkedRequirementRawId(null);
				draft.getOrder().add(div);
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
			if (firstObj != null)
			{
				// Object ids ignored when NPC list is present (same as before).
			}
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
		draft.getStepDefinitions().add(step);

		String orderSlotId = UUID.randomUUID().toString();
		DraftOrderLine ord = new DraftOrderLine();
		ord.setOrderSlotId(orderSlotId);
		ord.setSectionDivider(false);
		ord.setSuggestedVarName(null);
		ord.setSectionCondition("");
		ord.setSkipWhenConditionMet(false);
		ord.setRefStepId(stepId);
		ord.setLinkedRequirementRawId(null);
		draft.getOrder().add(ord);

		DraftStepAttachedRequirement.setOrderLineRoutingVarbit(ord, DraftStepAttachedRequirement.varbit(
			0,
			0,
			"EQUAL",
			truncate(name, 120)));
	}

	private static void appendCustomItemRow(DraftHelper draft, RouteItemDto item)
	{
		RouteCustomItemDto c = item.getCustomItem();
		String stepId = UUID.randomUUID().toString();
		DraftStep step = new DraftStep();
		step.setStepId(stepId);
		step.setKind(StepKind.TEXT);
		step.setSectionDivider(false);
		step.setStructId(null);
		step.setRawId(0);
		step.setOption("");
		step.setTargetText("");
		step.setSuggestedVarName(HelperScaffoldGenerator.toVarName(
			c.getLabel() != null ? c.getLabel() : "custom", "step"));
		StringBuilder instr = new StringBuilder();
		if (c.getLabel() != null && !c.getLabel().isBlank())
		{
			instr.append(c.getLabel().trim());
		}
		if (c.getDescription() != null && !c.getDescription().isBlank())
		{
			if (instr.length() > 0)
			{
				instr.append("\n\n");
			}
			instr.append(c.getDescription().trim());
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
		draft.getStepDefinitions().add(step);

		String orderSlotId = UUID.randomUUID().toString();
		DraftOrderLine ord = new DraftOrderLine();
		ord.setOrderSlotId(orderSlotId);
		ord.setSectionDivider(false);
		ord.setSuggestedVarName(null);
		ord.setSectionCondition("");
		ord.setSkipWhenConditionMet(false);
		ord.setRefStepId(stepId);
		ord.setLinkedRequirementRawId(null);
		draft.getOrder().add(ord);

		DraftStepAttachedRequirement.setOrderLineRoutingVarbit(ord, DraftStepAttachedRequirement.varbit(
			0,
			0,
			"EQUAL",
			truncate(c.getLabel(), 120)));
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
