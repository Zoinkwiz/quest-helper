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

import com.questhelper.maker.ConstructDraftPersistence;
import com.questhelper.maker.construct.DraftRoutingIds;
import com.questhelper.maker.taskstroute.TasksTrackerRouteDto.RouteCustomItemDto;
import com.questhelper.maker.taskstroute.TasksTrackerRouteDto.RouteInteractDto;
import com.questhelper.maker.taskstroute.TasksTrackerRouteDto.RouteItemDto;
import com.questhelper.maker.taskstroute.TasksTrackerRouteDto.RouteLocationDto;
import com.questhelper.maker.taskstroute.TasksTrackerRouteDto.RouteSectionDto;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.questhelper.maker.HelperConstructModels.DraftHelper;
import static com.questhelper.maker.HelperConstructModels.DraftOrderLine;
import static com.questhelper.maker.HelperConstructModels.DraftStep;
import static com.questhelper.maker.HelperConstructModels.StepKind;

/**
 * Converts a maker {@link DraftHelper} into {@link TasksTrackerRouteDto} for Tasks Tracker clipboard import,
 * including {@link TasksTrackerRouteDto#getQuestHelperMaker()} for lossless round-trip in Quest Helper.
 */
public final class TasksTrackerRouteExporter
{
	public static TasksTrackerRouteDto export(DraftHelper draft)
	{
		return export(draft, true);
	}

	public static TasksTrackerRouteDto export(DraftHelper draft, boolean includeMakerSnapshot)
	{
		TasksTrackerRouteDto route = new TasksTrackerRouteDto();
		route.setName(trimOrDefault(draft.getQuestName(), "Exported Route"));
		route.setTaskType("LEAGUE_5");
		route.setAuthor("Quest Helper Maker");
		route.setDescription(null);

		Map<String, DraftStep> defByStepId = new HashMap<>();
		for (DraftStep def : draft.getStepDefinitions())
		{
			if (def.getStepId() != null && !def.getStepId().isBlank())
			{
				defByStepId.put(def.getStepId(), def);
			}
		}

		List<RouteSectionDto> sections = new ArrayList<>();
		RouteSectionDto current = null;

		for (DraftOrderLine line : draft.getOrder())
		{
			if (line.isSectionDivider())
			{
				if (current != null && !current.getItems().isEmpty())
				{
					sections.add(current);
				}
				current = new RouteSectionDto();
				current.setName(trimOrDefault(line.getSuggestedVarName(), "Section"));
				current.setDescription(null);
			}
			else
			{
				if (current == null)
				{
					current = new RouteSectionDto();
					current.setName(trimOrDefault(draft.getQuestName(), "Tasks"));
				}
				DraftStep def = defByStepId.get(line.getRefStepId());
				if (def != null)
				{
					current.getItems().add(toRouteItem(def));
				}
			}
		}
		if (current == null)
		{
			current = new RouteSectionDto();
			current.setName(trimOrDefault(draft.getQuestName(), "Tasks"));
		}
		sections.add(current);

		route.setSections(sections);
		route.setQuestHelperMaker(includeMakerSnapshot ? ConstructDraftPersistence.toDraftState(draft) : null);
		return route;
	}

	private static RouteItemDto toRouteItem(DraftStep def)
	{
		Integer sid = def.getStructId();
		boolean leagueTask = sid != null && sid != 0;
		if (leagueTask)
		{
			RouteItemDto item = new RouteItemDto();
			item.setTaskId(sid);
			item.setNote(noteFromInstruction(def.getInstructionText()));
			item.setLocation(locationFromStep(def));
			item.setInteract(interactFromStep(def));
			return item;
		}
		return customItemFromGenericStep(def);
	}

	private static RouteInteractDto interactFromStep(DraftStep def)
	{
		if (def.getKind() != StepKind.NPC && def.getKind() != StepKind.OBJECT)
		{
			return null;
		}
		List<Integer> merged = new ArrayList<>(DraftRoutingIds.mergedStepOrRequirementIds(def.getRawId(), def.getAlternateRawIds()));
		merged.removeIf(id -> id == null || id == 0);
		if (merged.isEmpty())
		{
			return null;
		}
		RouteInteractDto inter = new RouteInteractDto();
		if (def.getKind() == StepKind.NPC)
		{
			inter.setNpc(merged);
		}
		else
		{
			inter.setObject(merged);
		}
		return inter;
	}

	private static RouteItemDto customItemFromGenericStep(DraftStep def)
	{
		RouteItemDto item = new RouteItemDto();
		RouteCustomItemDto c = new RouteCustomItemDto();
		c.setId(safeCustomItemId(def.getStepId()));
		String label = def.getSuggestedVarName();
		if (label == null || label.isBlank())
		{
			label = "Custom";
		}
		c.setLabel(label);
		c.setIcon(null);
		String desc = def.getInstructionText();
		c.setDescription(desc == null || desc.isBlank() ? null : desc);
		item.setCustomItem(c);
		item.setLocation(locationFromStep(def));
		item.setInteract(interactFromStep(def));
		return item;
	}

	private static RouteLocationDto locationFromStep(DraftStep def)
	{
		WorldPoint wp = def.getWorldPoint();
		if (wp == null)
		{
			return null;
		}
		RouteLocationDto loc = new RouteLocationDto();
		loc.setX(wp.getX());
		loc.setY(wp.getY());
		loc.setPlane(wp.getPlane());
		return loc;
	}

	private static String noteFromInstruction(String instructionText)
	{
		if (instructionText == null || instructionText.isBlank())
		{
			return null;
		}
		String t = instructionText.trim();
		if (t.endsWith(".") && t.length() > 1)
		{
			t = t.substring(0, t.length() - 1);
		}
		return t;
	}

	private static String trimOrDefault(String s, String d)
	{
		if (s == null || s.isBlank())
		{
			return d;
		}
		return s.trim();
	}

	private static String safeCustomItemId(String stepId)
	{
		if (stepId == null || stepId.isBlank())
		{
			return "qh_custom";
		}
		String s = stepId.replaceAll("[^a-zA-Z0-9_-]", "_");
		if (s.length() > 40)
		{
			s = s.substring(0, 40);
		}
		return "qh_" + s;
	}
}
