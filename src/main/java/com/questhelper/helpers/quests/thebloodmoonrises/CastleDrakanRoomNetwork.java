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
package com.questhelper.helpers.quests.thebloodmoonrises;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.SpriteID;

class CastleDrakanRoomNetwork
{
	enum RoomKey
	{
		LOBBY_F0,
		LOBBY_F1,
		DINING_ROOM,
		THRONE_ROOM,
		ROOM_SOUTH_OF_THRONE,
		STUDY,
		WEST_DINING_HALLWAY,
		STORAGE_ROOM,
		EXPLOSIVE_ROOM,
		FIRST_EMBLEM_ROOM,
		EXPLOSIVE_HALLWAY,
		HALLWAY_NORTH_OF_LOBBY,
		VANESCULAS_HALLWAY,
		VANESCULAS_STUDY,
		VANESCULAS_CHAMBER,
		VANESCULAS_VENATOR_ROOM,
		RANIS_HALLWAY,
		RANIS_PARLOUR,
		NORTH_CHAPEL_HALLWAY,
		WEST_CHAPEL_HALLWAY,
		BOTTLE_ROOM,
		CHAPEL_LIBRARY,
		SERVANTS_QUARTERS,
		SECRET_ROOM,
		IVAN_ROOM,
		SMALL_HALLWAY,
		DINING_F1,
		THRONE_F1,
		ROOM_ABOVE_STUDY,
		DISPLAY_ROOM,
		VENATOR_PUZZLE_ROOM,
		SOLID_KEY_ROOM,
		BASEMENT_HALLWAY,
		BASEMENT_PRISON,
		UPPER_SOUTHERN_HALLWAY,
		BEDROOM_ABOVE_THRONE_ROOM,
		FIRST_FLOOR_EAST_STAIRCASE,
		GROUND_FLOOR_EAST_STAIRCASE,
		THRONE_ROOM_STORAGE_ROOM,
		CRESCENT_DOOR_ROOM,
		SOLID_DOOR_STORE_ROOM,
		BASEMENT_STORE_ROOM,
		ORNATE_KNIFE_ROOM,
		GUEST_CHAMBER_STOREROOM,
		VENATOR_PUZZLE_LIBRARY,
		EMBLEM_GALLERY,
		EMBLEM_GALLERY_HALLWAY,
		KITCHEN,
		LARDER,
		BASEMENT_VENATOR_ROOM,
		SOLID_DOOR_HALLWAY,
		LABORATORY,
		LABORATORY_STORAGE
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	static final class Room
	{
		private final RoomKey key;
		@Getter
		private final String name;
		private final Requirement location;
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	static final class Edge
	{
		@Getter
		private final Room from;
		@Getter
		private final Room to;
		@Getter
		private final QuestStep step;
		private final Requirement available;
		private final WorldPoint location;
		private final WorldPoint landsAt;

		boolean isAvailable(Client client)
		{
			return available == null || available.check(client);
		}
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	static final class Door
	{
		private final int objectId;
		private final WorldPoint location;
		@NonNull
		private final String text;

		private ObjectStep createStep(QuestHelper questHelper, WorldPoint... traps)
		{
			var step = location == null
				? new ObjectStep(questHelper, objectId, text)
				: new ObjectStep(questHelper, objectId, location, text);
			if (traps.length > 0)
			{
				step.addTileMarkers(SpriteID.PvpwIcons.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING, traps);
			}
			return step;
		}
	}

	static Door door(int objectId, int x, int y, int plane, String text)
	{
		return new Door(objectId, new WorldPoint(x, y, plane), text);
	}

	private final QuestHelper questHelper;
	private final Map<RoomKey, Room> rooms = new LinkedHashMap<>();
	private final Map<Room, List<Edge>> edges = new LinkedHashMap<>();

	CastleDrakanRoomNetwork(QuestHelper questHelper)
	{
		this.questHelper = questHelper;
	}

	Room addRoom(RoomKey key, String name, Requirement location)
	{
		var room = new Room(key, name, location);
		if (rooms.put(key, room) != null)
		{
			throw new IllegalArgumentException("Duplicate Castle Drakan room: " + key);
		}
		edges.put(room, new ArrayList<>());
		return room;
	}

	Room getRoom(RoomKey key)
	{
		return Objects.requireNonNull(rooms.get(key), "Unregistered Castle Drakan room: " + key);
	}

	void connect(Room a, Room b, Requirement available, @NonNull Door aToB, @NonNull Door bToA,
		WorldPoint... traps)
	{
		link(a, b, available, aToB, bToA, traps);
		link(b, a, available, bToA, aToB, traps);
	}

	private void link(Room from, Room to, Requirement available, Door door, Door back,
		WorldPoint... traps)
	{
		var landsAt = back.location != null ? back.location : door.location;
		edges.get(from).add(new Edge(from, to, door.createStep(questHelper, traps), available,
			door.location, landsAt));
	}

	Requirement inRoom(RoomKey key)
	{
		return new Requirement()
		{
			@Override
			public boolean check(Client client)
			{
				return getRoom(key).location.check(client);
			}

			@Override
			public @Nonnull String getDisplayText()
			{
				return "In the Castle Drakan " + key.name().toLowerCase().replace('_', ' ');
			}
		};
	}

	Collection<Room> getRooms()
	{
		return Collections.unmodifiableCollection(rooms.values());
	}

	Collection<Edge> getEdges()
	{
		var allEdges = new ArrayList<Edge>();
		edges.values().forEach(allEdges::addAll);
		return Collections.unmodifiableCollection(allEdges);
	}

	List<QuestStep> getDoorSteps()
	{
		return getEdges().stream().map(Edge::getStep).collect(Collectors.toList());
	}

	Optional<Room> currentRoom(Client client)
	{
		return rooms.values().stream().filter(room -> room.location.check(client)).findFirst();
	}

	Optional<Edge> nextEdge(Client client, Room start, Room destination)
	{
		var open = search(start, destination, edge -> edge.isAvailable(client));
		return open.isPresent() ? open : search(start, destination, edge -> true);
	}

	private Optional<Edge> search(Room start, Room destination, Predicate<Edge> usable)
	{
		if (start == destination)
		{
			return Optional.empty();
		}

		var settled = new HashSet<Edge>();
		var queue = new PriorityQueue<Route>();
		for (Edge edge : edges.getOrDefault(start, List.of()))
		{
			if (usable.test(edge))
			{
				queue.add(new Route(edge, edge, 1, 0));
			}
		}

		while (!queue.isEmpty())
		{
			var route = queue.remove();
			if (!settled.add(route.arrivedBy))
			{
				continue;
			}
			if (route.arrivedBy.to == destination)
			{
				return Optional.of(route.firstEdge);
			}

			for (Edge next : edges.getOrDefault(route.arrivedBy.to, List.of()))
			{
				if (usable.test(next))
				{
					queue.add(new Route(route.firstEdge, next, route.crossings + 1,
						route.walked + walkAcross(route.arrivedBy, next)));
				}
			}
		}

		return Optional.empty();
	}

	private static int walkAcross(Edge arrivedBy, Edge leavingBy)
	{
		return leavingBy.location == null
			? 0
			: arrivedBy.landsAt.distanceTo2D(leavingBy.location);
	}

	private static final class Route implements Comparable<Route>
	{
		private final Edge firstEdge;
		private final Edge arrivedBy;
		private final int crossings;
		private final int walked;

		private Route(Edge firstEdge, Edge arrivedBy, int crossings, int walked)
		{
			this.firstEdge = firstEdge;
			this.arrivedBy = arrivedBy;
			this.crossings = crossings;
			this.walked = walked;
		}

		@Override
		public int compareTo(Route other)
		{
			return crossings != other.crossings
				? Integer.compare(crossings, other.crossings)
				: Integer.compare(walked, other.walked);
		}
	}
}
