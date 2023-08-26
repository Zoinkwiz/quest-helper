/*
 * Copyright (c) 2023, Zoinkwiz (https://www.github.com/Zoinkwiz)
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
package com.questhelper.helpers.miniquests.hisfaithfulservants;

import com.questhelper.Zone;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;

public class BarrowsRouteCalculator
{
	private static int lastStartRoom = -1;
	static final BarrowsRooms GOAL_ROOM = BarrowsRooms.C;
	public static List<WorldPoint> startDelving(Client client)
	{
		WorldPoint currentPos = client.getLocalPlayer().getWorldLocation();
		BarrowsRooms startRoom = null;
		for (BarrowsRooms room : BarrowsRooms.values())
		{
			for (Zone zone : room.getArea())
			{
				if (zone.contains(currentPos))
				{
					startRoom = room;
					break;
				}
			}
		}

		if(startRoom == null)
		{
			return null;
		}
		if (startRoom == GOAL_ROOM) return new ArrayList<>();

		if (lastStartRoom == startRoom.getId()) return null;
		lastStartRoom = startRoom.getId();

		ArrayList<BarrowsDoors> route = createRoute(client, startRoom, new ArrayList<>(), new ArrayList<>(), 0);

		ArrayList<WorldPoint> points = new ArrayList<>();
		BarrowsRooms currentRoomInLoop = startRoom;
		if (route == null || route.size() == 0) return null;
		for (BarrowsDoors barrowsDoors : route)
		{
			if (barrowsDoors.getStartRoom() == currentRoomInLoop.getId())
			{
				points.addAll(barrowsDoors.getPath());
				currentRoomInLoop = BarrowsRooms.getRoomById(barrowsDoors.getEndRoom());
			}
			else
			{
				ArrayList<WorldPoint> newPath = new ArrayList<>(barrowsDoors.getPath());
				Collections.reverse(newPath);
				points.addAll(newPath);
				currentRoomInLoop = BarrowsRooms.getRoomById(barrowsDoors.getStartRoom());
			}
		}
		return points;
	}
	public static ArrayList<BarrowsDoors> createRoute(Client client, BarrowsRooms currentRoom, List<Integer> previousRooms, ArrayList<BarrowsDoors> pastRoute, int depth)
	{
		ArrayList<BarrowsDoors> bestRoute = new ArrayList<>();
		int MAX_DEPTH = 8;
		if (depth > MAX_DEPTH) return null;

		for (BarrowsDoors path : currentRoom.getPaths())
		{
			BarrowsRooms nextRoom;
			if (path.getEndRoom() == currentRoom.getId())
			{
				nextRoom = BarrowsRooms.getRoomById(path.getStartRoom());
			}
			else
			{
				nextRoom = BarrowsRooms.getRoomById(path.getEndRoom());
			}

			// If blocked path, continue
			if (client.getVarbitValue(path.getVarbit()) != 0) continue;
			if (previousRooms.contains(nextRoom.getId()))
			{
				continue;
			}

			if (nextRoom == GOAL_ROOM)
			{
				pastRoute.add(path);
				return pastRoute;
			}

			ArrayList<Integer> newPreviousRooms = new ArrayList<>(previousRooms);
			newPreviousRooms.add(currentRoom.getId());
			ArrayList<BarrowsDoors> nextPath = new ArrayList<>(pastRoute);
			nextPath.add(path);
			ArrayList<BarrowsDoors> fullRoute = createRoute(client, nextRoom, newPreviousRooms, nextPath, depth + 1);


			if (fullRoute != null && fullRoute.size() > 0 && fullRoute.size() < bestRoute.size() || bestRoute.size() == 0)
			{
				bestRoute = fullRoute;
			}
		}
		return bestRoute;
	}
}
