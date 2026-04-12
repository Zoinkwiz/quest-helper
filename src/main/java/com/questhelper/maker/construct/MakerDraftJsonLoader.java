package com.questhelper.maker.construct;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.questhelper.maker.ConstructDraftPersistence;
import com.questhelper.managers.taskstroute.TasksTrackerRouteDto;
import com.questhelper.managers.taskstroute.TasksTrackerRouteImporter;
import com.questhelper.managers.taskstroute.TasksTrackerRouteValidation;
import lombok.extern.slf4j.Slf4j;

import static com.questhelper.maker.HelperConstructModels.DraftHelper;

/**
 * Parses Quest Helper Maker JSON: extended Tasks Tracker route documents only
 * ({@code sections} at root). Use {@code maker/scripts/convert_legacy_maker_draft.py} for older shapes.
 */
@Slf4j
public final class MakerDraftJsonLoader
{
	private MakerDraftJsonLoader()
	{
	}

	public static final class LoadOutcome
	{
		private final boolean success;
		private final String errorMessage;
		private final DraftHelper draft;

		private LoadOutcome(boolean success, String errorMessage, DraftHelper draft)
		{
			this.success = success;
			this.errorMessage = errorMessage;
			this.draft = draft;
		}

		public static LoadOutcome ok(DraftHelper draft)
		{
			return new LoadOutcome(true, null, draft);
		}

		public static LoadOutcome failure(String message)
		{
			return new LoadOutcome(false, message == null ? "Unknown error" : message, null);
		}

		public boolean isSuccess()
		{
			return success;
		}

		public String getErrorMessage()
		{
			return errorMessage;
		}

		/** Non-null only when {@link #isSuccess()} is true. */
		public DraftHelper getDraft()
		{
			return draft;
		}
	}

	public static boolean jsonHasTopLevelRouteEnvelope(String json)
	{
		try
		{
			@SuppressWarnings("deprecation")
			JsonElement el = new JsonParser().parse(json);
			if (!el.isJsonObject())
			{
				return false;
			}
			JsonObject o = el.getAsJsonObject();
			return o.has("sections") && o.get("sections").isJsonArray();
		}
		catch (Exception e)
		{
			return false;
		}
	}

	/**
	 * @param json raw document (trimmed internally)
	 */
	public static LoadOutcome loadDraftFromJson(String json, Gson gson)
	{
		if (json == null || json.isBlank())
		{
			return LoadOutcome.failure("JSON is empty");
		}
		String trimmed = json.trim();
		if (!jsonHasTopLevelRouteEnvelope(trimmed))
		{
			return LoadOutcome.failure(
				"Expected extended Tasks Tracker route JSON (top-level \"sections\" array). "
					+ "Convert legacy maker files with: python maker/scripts/convert_legacy_maker_draft.py <file.json>");
		}
		try
		{
			TasksTrackerRouteDto route = gson.fromJson(trimmed, TasksTrackerRouteDto.class);
			String validationError = TasksTrackerRouteValidation.validateRoute(route);
			if (validationError != null)
			{
				return LoadOutcome.failure(validationError);
			}
			DraftHelper draft;
			if (ConstructDraftPersistence.isSupportedMakerSnapshot(route.getQuestHelperMaker()))
			{
				draft = ConstructDraftPersistence.draftHelperFromState(route.getQuestHelperMaker());
			}
			else
			{
				if (route.getQuestHelperMaker() != null)
				{
					log.warn("Ignoring questHelperMaker: unsupported formatVersion or malformed snapshot; using route sections only.");
				}
				draft = TasksTrackerRouteImporter.importRoute(route, null);
			}
			return LoadOutcome.ok(draft);
		}
		catch (JsonSyntaxException | IllegalArgumentException e)
		{
			return LoadOutcome.failure(e.getMessage());
		}
	}
}
