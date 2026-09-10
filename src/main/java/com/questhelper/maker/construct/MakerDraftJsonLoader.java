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
package com.questhelper.maker.construct;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.questhelper.maker.ConstructDraftPersistence;
import com.questhelper.maker.taskstroute.TasksTrackerRouteDto;
import com.questhelper.maker.taskstroute.TasksTrackerRouteImporter;
import com.questhelper.maker.taskstroute.TasksTrackerRouteValidation;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static com.questhelper.maker.HelperConstructModels.DraftHelper;

/**
 * Parses Quest Helper Maker JSON:
 * - QH canonical snapshot documents (questHelperMaker)
 * - extended Tasks Tracker route documents (sections + optional questHelperMaker)
 */
@Slf4j
public final class MakerDraftJsonLoader
{
	@Getter
	public static final class LoadOutcome
	{
		private final boolean success;
		private final String errorMessage;
		/**
		 * -- GETTER --
		 * Non-null only when
		 *  is true.
		 */
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

	}

	public static boolean jsonHasTopLevelRouteEnvelope(Gson gson, String json)
	{
		try
		{
			JsonElement el = gson.fromJson(json, JsonElement.class);
			if (!el.isJsonObject())
			{
				return false;
			}
			JsonObject o = el.getAsJsonObject();
			boolean hasSections = o.has("sections") && o.get("sections").isJsonArray();
			boolean hasMakerSnapshot = o.has("questHelperMaker") && o.get("questHelperMaker").isJsonObject();
			return hasSections || hasMakerSnapshot;
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
		if (!jsonHasTopLevelRouteEnvelope(gson, trimmed))
		{
			return LoadOutcome.failure(
				"Expected QH JSON (questHelperMaker) or route JSON (top-level \"sections\" array).");
		}
		try
		{
			JsonObject root = gson.fromJson(trimmed, JsonObject.class);
			if (root != null && root.has("questHelperMaker") && root.get("questHelperMaker").isJsonObject())
			{
				ConstructDraftPersistence.DraftState snapshot =
					gson.fromJson(root.get("questHelperMaker"), ConstructDraftPersistence.DraftState.class);
				if (ConstructDraftPersistence.isSupportedMakerSnapshot(snapshot))
				{
					return LoadOutcome.ok(ConstructDraftPersistence.draftHelperFromState(snapshot));
				}
				if (!(root.has("sections") && root.get("sections").isJsonArray()))
				{
					return LoadOutcome.failure("questHelperMaker snapshot is malformed or unsupported formatVersion.");
				}
			}

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
