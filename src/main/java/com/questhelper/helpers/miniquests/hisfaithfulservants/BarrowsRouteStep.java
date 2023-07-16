package com.questhelper.helpers.miniquests.hisfaithfulservants;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.DetailedQuestStep;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;

public class BarrowsRouteStep extends DetailedQuestStep
{
	public BarrowsRouteStep(QuestHelper questHelper, String text, Requirement... requirements)
	{
		super(questHelper, text, requirements);
	}
	// Optimal route solver:
	// Checks for corner you're in
	// Checks for corner which door is open
	// Checks which of the middle doors is open
	// Path goes through first door, then loops AWAY from original room to door


	// Basic template IS:
	// IF room has only 1 route, go through it

	public void startDelving(Client client)
	{
		WorldPoint currentPos = client.getLocalPlayer().getWorldLocation();
		BarrowsRooms startRoom = null;
		for (BarrowsRooms room : BarrowsRooms.values())
		{
			if (room.getArea().contains(currentPos)) startRoom = room;
		}
		if(startRoom == null) return;

		ArrayList<BarrowsDoors> route = createRoute(client, startRoom, startRoom, new ArrayList<>(), 0);

		ArrayList<WorldPoint> points = new ArrayList<>();
		BarrowsRooms currentRoomInLoop = startRoom;
		if (route == null)
		{
			System.out.println("FAILED TO FIND PATH@");
			return;
		}
		for (BarrowsDoors barrowsDoors : route)
		{
			if (barrowsDoors.getStartRoom() == currentRoomInLoop)
			{
				points.addAll(barrowsDoors.getPath());
			}
			else
			{
				ArrayList<WorldPoint> newPath = new ArrayList<>(barrowsDoors.getPath());
				Collections.reverse(newPath);
				points.addAll(newPath);
			}
		}
		setLinePoints(points);
	}
	public ArrayList<BarrowsDoors> createRoute(Client client, BarrowsRooms previousRoom, BarrowsRooms currentRoom, ArrayList<BarrowsDoors> pastRoute, int depth)
	{
		// TODO: ISsue is the rooms aren't initialized as of recursive referencing?
		int MAX_DEPTH = 8;
		if (depth > MAX_DEPTH) return null;
		BarrowsRooms GOAL_ROOM = BarrowsRooms.C;
		for (BarrowsDoors path : currentRoom.getPaths())
		{
			BarrowsRooms nextRoom;
			if (path.getEndRoom() == currentRoom)
			{
				System.out.println("END ROOM");
				System.out.println(path.getStartRoom());
				nextRoom = path.getStartRoom();
			}
			else
			{
				System.out.println("START ROOM");
				System.out.println(path.getEndRoom());
				nextRoom = path.getEndRoom();
			}
			System.out.println(nextRoom);

			// If blocked path, continue
			if (client.getVarbitValue(path.getVarbit()) != 0) continue;
			if (previousRoom == nextRoom) continue;
			if (nextRoom == null) continue;

			System.out.println("WOW");
			if (nextRoom == GOAL_ROOM)
			{
				pastRoute.add(path);
				return pastRoute;
			}
			ArrayList<BarrowsDoors>  nextPath = new ArrayList<>(pastRoute);
			nextPath.add(path);
			ArrayList<BarrowsDoors> fullRoute = createRoute(client, currentRoom, nextRoom, nextPath, depth + 1);
			if (fullRoute != null) return fullRoute;
		}
		return null;
	}
}
