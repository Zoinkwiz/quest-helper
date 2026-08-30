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

import com.questhelper.maker.taskstroute.TasksTrackerRouteDto.RouteItemDto;
import com.questhelper.maker.taskstroute.TasksTrackerRouteDto.RouteLocationDto;
import com.questhelper.maker.taskstroute.TasksTrackerRouteDto.RouteSectionDto;

import java.util.List;

public final class TasksTrackerRouteValidation
{
	private TasksTrackerRouteValidation()
	{
	}

	public static String validateRoute(TasksTrackerRouteDto route)
	{
		if (route == null)
		{
			return "Route is null";
		}
		if (route.getName() == null || route.getName().isBlank())
		{
			return "Route name is required";
		}
		if (route.getTaskType() == null || route.getTaskType().isBlank())
		{
			return "taskType is required (e.g. LEAGUE_5)";
		}
		List<RouteSectionDto> sections = route.getSections();
		if (sections == null || sections.isEmpty())
		{
			return "sections must be a non-empty array";
		}
		for (int s = 0; s < sections.size(); s++)
		{
			RouteSectionDto sec = sections.get(s);
			if (sec == null)
			{
				return "sections[" + s + "] is null";
			}
			if (sec.getName() == null || sec.getName().isBlank())
			{
				return "sections[" + s + "].name is required";
			}
			List<RouteItemDto> items = sec.getItems();
			if (items == null)
			{
				return "sections[" + s + "].items must be an array";
			}
			for (int i = 0; i < items.size(); i++)
			{
				String err = validateItem(items.get(i), s, i);
				if (err != null)
				{
					return err;
				}
			}
		}
		return null;
	}

	private static String validateItem(RouteItemDto item, int sectionIdx, int itemIdx)
	{
		if (item == null)
		{
			return "sections[" + sectionIdx + "].items[" + itemIdx + "] is null";
		}
		boolean hasTask = item.getTaskId() != null;
		boolean hasCustom = item.getCustomItem() != null;
		if (hasTask && hasCustom)
		{
			return "sections[" + sectionIdx + "].items[" + itemIdx + "]: set only one of taskId or customItem";
		}
		if (!hasTask && !hasCustom)
		{
			return "sections[" + sectionIdx + "].items[" + itemIdx + "]: need taskId or customItem";
		}
		if (hasCustom)
		{
			if (item.getCustomItem().getId() == null || item.getCustomItem().getId().isBlank())
			{
				return "sections[" + sectionIdx + "].items[" + itemIdx + "].customItem.id is required";
			}
		}
		RouteLocationDto loc = item.getLocation();
		if (loc != null)
		{
			// Wiki: partial location is ignored by plugin; we require full triple for export consistency.
		}
		return null;
	}
}
