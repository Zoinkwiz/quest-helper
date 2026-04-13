package com.questhelper.maker.taskstroute;

import com.google.gson.annotations.SerializedName;
import com.questhelper.maker.ConstructDraftPersistence;

import java.util.ArrayList;
import java.util.List;

/**
 * Gson DTOs for Tasks Tracker "Import Route from Clipboard" JSON
 * (<a href="https://github.com/osrs-reldo/tasks-tracker-plugin/wiki/How-to-Export-Routes-to-Plugin">wiki</a>).
 * Optional {@link RouteItemDto#interact} is a Quest Helper extension for NPC/object ids.
 * Optional {@link #questHelperMaker} holds the full Quest Helper maker draft snapshot ({@link ConstructDraftPersistence.DraftState}).
 */
@SuppressWarnings("unused")
public final class TasksTrackerRouteDto
{
	private String id;
	private String name;
	private String taskType;
	private String author;
	private String description;
	private List<Object> completed;
	private List<RouteSectionDto> sections = new ArrayList<>();
	@SerializedName("questHelperMaker")
	private ConstructDraftPersistence.DraftState questHelperMaker;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getTaskType()
	{
		return taskType;
	}

	public void setTaskType(String taskType)
	{
		this.taskType = taskType;
	}

	public String getAuthor()
	{
		return author;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public List<Object> getCompleted()
	{
		return completed;
	}

	public void setCompleted(List<Object> completed)
	{
		this.completed = completed;
	}

	public List<RouteSectionDto> getSections()
	{
		return sections;
	}

	public void setSections(List<RouteSectionDto> sections)
	{
		this.sections = sections != null ? sections : new ArrayList<>();
	}

	public ConstructDraftPersistence.DraftState getQuestHelperMaker()
	{
		return questHelperMaker;
	}

	public void setQuestHelperMaker(ConstructDraftPersistence.DraftState questHelperMaker)
	{
		this.questHelperMaker = questHelperMaker;
	}

	public static final class RouteSectionDto
	{
		private String id;
		private String name;
		private String description;
		private List<RouteItemDto> items = new ArrayList<>();

		public String getId()
		{
			return id;
		}

		public void setId(String id)
		{
			this.id = id;
		}

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public String getDescription()
		{
			return description;
		}

		public void setDescription(String description)
		{
			this.description = description;
		}

		public List<RouteItemDto> getItems()
		{
			return items;
		}

		public void setItems(List<RouteItemDto> items)
		{
			this.items = items != null ? items : new ArrayList<>();
		}
	}

	public static final class RouteItemDto
	{
		@SerializedName("taskId")
		private Integer taskId;
		private RouteCustomItemDto customItem;
		private String note;
		private RouteLocationDto location;
		private RouteInteractDto interact;

		public Integer getTaskId()
		{
			return taskId;
		}

		public void setTaskId(Integer taskId)
		{
			this.taskId = taskId;
		}

		public RouteCustomItemDto getCustomItem()
		{
			return customItem;
		}

		public void setCustomItem(RouteCustomItemDto customItem)
		{
			this.customItem = customItem;
		}

		public String getNote()
		{
			return note;
		}

		public void setNote(String note)
		{
			this.note = note;
		}

		public RouteLocationDto getLocation()
		{
			return location;
		}

		public void setLocation(RouteLocationDto location)
		{
			this.location = location;
		}

		public RouteInteractDto getInteract()
		{
			return interact;
		}

		public void setInteract(RouteInteractDto interact)
		{
			this.interact = interact;
		}
	}

	public static final class RouteCustomItemDto
	{
		private String id;
		private String label;
		private Integer icon;
		private String description;

		public String getId()
		{
			return id;
		}

		public void setId(String id)
		{
			this.id = id;
		}

		public String getLabel()
		{
			return label;
		}

		public void setLabel(String label)
		{
			this.label = label;
		}

		public Integer getIcon()
		{
			return icon;
		}

		public void setIcon(Integer icon)
		{
			this.icon = icon;
		}

		public String getDescription()
		{
			return description;
		}

		public void setDescription(String description)
		{
			this.description = description;
		}
	}

	public static final class RouteLocationDto
	{
		private int x;
		private int y;
		private int plane;

		public int getX()
		{
			return x;
		}

		public void setX(int x)
		{
			this.x = x;
		}

		public int getY()
		{
			return y;
		}

		public void setY(int y)
		{
			this.y = y;
		}

		public int getPlane()
		{
			return plane;
		}

		public void setPlane(int plane)
		{
			this.plane = plane;
		}
	}

	public static final class RouteInteractDto
	{
		private List<Integer> npc;
		private List<Integer> object;

		public List<Integer> getNpc()
		{
			return npc;
		}

		public void setNpc(List<Integer> npc)
		{
			this.npc = npc;
		}

		public List<Integer> getObject()
		{
			return object;
		}

		public void setObject(List<Integer> object)
		{
			this.object = object;
		}
	}
}
