package com.questhelper.helpers.miniquests.hisfaithfulservants;

import com.questhelper.Zone;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.ZoneRequirement;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
@Getter
public enum BarrowsDoors
{
	// Door route
	// Most doors have 2 WPs for lines. Need to specify which way things connect.
	NW_NORTH_DOOR(BarrowsRooms.NW, BarrowsRooms.NE, 469,
		Arrays.asList(new WorldPoint(3534, 9716, 0), new WorldPoint(3535, 9720, 0),
			new WorldPoint(3538, 9721, 0), new WorldPoint(3567, 9721, 0), new WorldPoint(3569, 9718, 0),
			new WorldPoint(3569, 9715, 0))),
	NW_EAST_DOOR(BarrowsRooms.NW, BarrowsRooms.N, 472, Arrays.asList(new WorldPoint(3537, 9712, 0), new WorldPoint(3549, 9712, 0))),
	NW_SOUTH_DOOR(BarrowsRooms.NW, BarrowsRooms.E, 471, Arrays.asList(new WorldPoint(3534, 9708, 0), new WorldPoint(3534, 9698, 0))),
	NW_WEST_DOOR(BarrowsRooms.NW, BarrowsRooms.SW, 470, Arrays.asList(new WorldPoint(3531, 9711, 0), new WorldPoint(3526, 9711, 0),
		new WorldPoint(3525, 9708, 0), new WorldPoint(3525, 9680, 0), new WorldPoint(3528, 9677, 0),
		new WorldPoint(3532, 9677, 0))),
	NORTH_SOUTH_DOOR(BarrowsRooms.N, BarrowsRooms.C, 473, Arrays.asList(new WorldPoint(3552, 9708, 0), new WorldPoint(3552, 9696, 0))),
	NE_WEST_DOOR(BarrowsRooms.N, BarrowsRooms.NE, 474, Arrays.asList(new WorldPoint(3554, 9711, 0), new WorldPoint(3568, 9711, 0))),
	NE_SOUTH_DOOR(BarrowsRooms.NE, BarrowsRooms.E, 475, Arrays.asList(new WorldPoint(3568, 9710, 0), new WorldPoint(3568, 9696, 0))),
	NE_EAST_DOOR(BarrowsRooms.NE, BarrowsRooms.SE, 476, Arrays.asList(new WorldPoint(3572, 9711, 0), new WorldPoint(3576, 9711, 0),
		new WorldPoint(3578, 9708, 0), new WorldPoint(3578, 9680, 0), new WorldPoint(3576, 9678, 0),
		new WorldPoint(3571, 9678, 0))),
	WEST_EAST_DOOR(BarrowsRooms.W, BarrowsRooms.C, 477, Arrays.asList(new WorldPoint(3536, 9694, 0), new WorldPoint(3551, 9694, 0))),
	EAST_WEST_DOOR(BarrowsRooms.E, BarrowsRooms.C, 478, Arrays.asList(new WorldPoint(3552, 9694, 0), new WorldPoint(3567, 9694, 0))),
	SW_NORTH_DOOR(BarrowsRooms.SW, BarrowsRooms.W, 479, Arrays.asList(new WorldPoint(3534, 9679, 0), new WorldPoint(3534, 9693, 0))),
	SW_EAST_DOOR(BarrowsRooms.SW, BarrowsRooms.S, 482, Arrays.asList(new WorldPoint(3536, 9677, 0), new WorldPoint(3549, 9677, 0))),
	SW_SOUTH_DOOR(BarrowsRooms.SW, BarrowsRooms.SE, 484, Arrays.asList(new WorldPoint(3534, 9675, 0), new WorldPoint(3534, 9670, 0),
		new WorldPoint(3536, 9668, 0), new WorldPoint(3567, 9668, 0), new WorldPoint(3569, 9671, 0),
		new WorldPoint(3569, 9675, 0))),
	SOUTH_NORTH_DOOR(BarrowsRooms.S, BarrowsRooms.C, 480, Arrays.asList(new WorldPoint(3551, 9679, 0), new WorldPoint(3551, 9694, 0))),
	SE_WEST_DOOR(BarrowsRooms.SE, BarrowsRooms.S, 483, Arrays.asList(new WorldPoint(3567, 9677, 0), new WorldPoint(3554, 9677, 0))),
	SE_NORTH_DOOR(BarrowsRooms.SE, BarrowsRooms.E, 481, Arrays.asList(new WorldPoint(3568, 9679, 0), new WorldPoint(3568, 9692, 0)));

	private final BarrowsRooms startRoom;
	private final BarrowsRooms endRoom;

	private final int varbit;
	private final List<WorldPoint> path;
}
