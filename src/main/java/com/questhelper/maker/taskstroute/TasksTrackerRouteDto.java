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

import com.google.gson.annotations.SerializedName;
import com.questhelper.maker.ConstructDraftPersistence;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Gson DTOs for Tasks Tracker "Import Route from Clipboard" JSON
 * (<a href="https://github.com/osrs-reldo/tasks-tracker-plugin/wiki/How-to-Export-Routes-to-Plugin">wiki</a>).
 * Optional {@link RouteItemDto#interact} is a Quest Helper extension for NPC/object ids.
 * Optional {@link #questHelperMaker} holds the full Quest Helper maker draft snapshot ({@link ConstructDraftPersistence.DraftState}).
 */
@Data
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

	public void setSections(List<RouteSectionDto> sections)
	{
		this.sections = sections != null ? sections : new ArrayList<>();
	}

	@Data
	public static final class RouteSectionDto
	{
		private String id;
		private String name;
		private String description;
		private List<RouteItemDto> items = new ArrayList<>();

		public void setItems(List<RouteItemDto> items)
		{
			this.items = items != null ? items : new ArrayList<>();
		}
	}

	@Data
	public static final class RouteItemDto
	{
		@SerializedName("taskId")
		private Integer taskId;
		private RouteCustomItemDto customItem;
		private String note;
		private RouteLocationDto location;
		private RouteInteractDto interact;

	}

	@Data
	public static final class RouteCustomItemDto
	{
		private String id;
		private String label;
		private Integer icon;
		private String description;

	}

	@Data
	public static final class RouteLocationDto
	{
		private int x;
		private int y;
		private int plane;

	}

	@Data
	public static final class RouteInteractDto
	{
		private List<Integer> npc;
		private List<Integer> object;

	}
}
