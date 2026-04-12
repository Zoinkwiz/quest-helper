package com.questhelper.managers.taskstroute;

import com.questhelper.managers.taskstroute.TasksTrackerRouteDto.RouteItemDto;
import com.questhelper.managers.taskstroute.TasksTrackerRouteDto.RouteLocationDto;
import com.questhelper.managers.taskstroute.TasksTrackerRouteDto.RouteSectionDto;

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
