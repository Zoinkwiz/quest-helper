/*
 * Copyright (c) 2026, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 */
package com.questhelper.maker.construct;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.questhelper.maker.taskstroute.TasksTrackerRouteDto;
import com.questhelper.maker.taskstroute.TasksTrackerRouteDto.RouteItemDto;
import com.questhelper.maker.taskstroute.TasksTrackerRouteDto.RouteLocationDto;
import com.questhelper.maker.taskstroute.TasksTrackerRouteDto.RouteSectionDto;
import com.questhelper.maker.taskstroute.TasksTrackerRouteValidation;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Normalizes supported import JSON formats into one route-like document.
 */
public final class MakerImportFormatAdapter
{
	public enum ImportSourceFormat
	{
		QH_EXTENDED_ROUTE,
		LEAGUE_ROUTE,
		LEAGUE_TASK_BASE
	}

	@Getter
	public static final class AdaptedImport
	{
		private final ImportSourceFormat sourceFormat;
		private final TasksTrackerRouteDto route;
		private final Map<Integer, JsonObject> hubByStructId;
		private final String errorMessage;

		private AdaptedImport(
			ImportSourceFormat sourceFormat,
			TasksTrackerRouteDto route,
			Map<Integer, JsonObject> hubByStructId,
			String errorMessage)
		{
			this.sourceFormat = sourceFormat;
			this.route = route;
			this.hubByStructId = hubByStructId;
			this.errorMessage = errorMessage;
		}

		public boolean isSuccess()
		{
			return errorMessage == null;
		}

		public static AdaptedImport ok(ImportSourceFormat format, TasksTrackerRouteDto route, Map<Integer, JsonObject> hubByStructId)
		{
			return new AdaptedImport(format, route, hubByStructId, null);
		}

		public static AdaptedImport failure(String message)
		{
			return new AdaptedImport(null, null, Map.of(), message == null ? "Unknown import error" : message);
		}
	}

	private MakerImportFormatAdapter()
	{
	}

	public static AdaptedImport adapt(String json, Gson gson)
	{
		if (json == null || json.isBlank())
		{
			return AdaptedImport.failure("JSON is empty");
		}

		final JsonElement root;
		try
		{
			root = gson.fromJson(json.trim(), JsonElement.class);
		}
		catch (JsonSyntaxException ex)
		{
			return AdaptedImport.failure(ex.getMessage());
		}

		if (root == null || root.isJsonNull())
		{
			return AdaptedImport.failure("JSON is empty");
		}

		if (root.isJsonObject())
		{
			return adaptRouteObject(root.getAsJsonObject(), gson);
		}
		if (root.isJsonArray())
		{
			return adaptLeaguesTaskArray(root.getAsJsonArray());
		}
		return AdaptedImport.failure("Unsupported JSON shape. Expected route object or leagues task array.");
	}

	private static AdaptedImport adaptRouteObject(JsonObject root, Gson gson)
	{
		if (!root.has("sections") || !root.get("sections").isJsonArray())
		{
			return AdaptedImport.failure("Expected route object with top-level \"sections\" array.");
		}
		TasksTrackerRouteDto route;
		try
		{
			route = gson.fromJson(root, TasksTrackerRouteDto.class);
		}
		catch (RuntimeException ex)
		{
			return AdaptedImport.failure(ex.getMessage());
		}
		String validationError = TasksTrackerRouteValidation.validateRoute(route);
		if (validationError != null)
		{
			return AdaptedImport.failure(validationError);
		}

		ImportSourceFormat format = route.getQuestHelperMaker() != null
			? ImportSourceFormat.QH_EXTENDED_ROUTE
			: ImportSourceFormat.LEAGUE_ROUTE;
		return AdaptedImport.ok(format, route, Map.of());
	}

	private static AdaptedImport adaptLeaguesTaskArray(JsonArray array)
	{
		TasksTrackerRouteDto route = new TasksTrackerRouteDto();
		route.setName("Imported Tasks");
		route.setTaskType("LEAGUE_5");
		route.setAuthor("Quest Helper Maker");

		RouteSectionDto section = new RouteSectionDto();
		section.setName("Imported Tasks");
		section.setItems(new ArrayList<>());
		Map<Integer, JsonObject> hubByStructId = new LinkedHashMap<>();

		for (JsonElement el : array)
		{
			if (!el.isJsonObject())
			{
				continue;
			}
			JsonObject row = el.getAsJsonObject();
			if (!row.has("structId"))
			{
				continue;
			}
			int structId = row.get("structId").getAsInt();
			hubByStructId.put(structId, row);

			RouteItemDto item = new RouteItemDto();
			item.setTaskId(structId);
			item.setNote(jsonString(row, "name"));
			item.setLocation(readLocation(row));
			section.getItems().add(item);
		}
		if (section.getItems().isEmpty())
		{
			return AdaptedImport.failure("No tasks found in leagues task JSON. Expected objects with structId.");
		}
		route.setSections(List.of(section));
		return AdaptedImport.ok(ImportSourceFormat.LEAGUE_TASK_BASE, route, hubByStructId);
	}

	private static String jsonString(JsonObject row, String key)
	{
		if (!row.has(key) || row.get(key).isJsonNull())
		{
			return null;
		}
		return row.get(key).getAsString();
	}

	private static RouteLocationDto readLocation(JsonObject row)
	{
		if (!row.has("location") || !row.get("location").isJsonObject())
		{
			return null;
		}
		JsonObject locObj = row.getAsJsonObject("location");
		if (!locObj.has("x") || !locObj.has("y"))
		{
			return null;
		}
		RouteLocationDto loc = new RouteLocationDto();
		loc.setX(locObj.get("x").getAsInt());
		loc.setY(locObj.get("y").getAsInt());
		loc.setPlane(locObj.has("plane") ? locObj.get("plane").getAsInt() : 0);
		return loc;
	}
}
