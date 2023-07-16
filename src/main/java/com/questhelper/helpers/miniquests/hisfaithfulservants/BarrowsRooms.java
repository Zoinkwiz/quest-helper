package com.questhelper.helpers.miniquests.hisfaithfulservants;

import com.questhelper.Zone;
import com.questhelper.helpers.quests.monkeymadnessii.MM2Route;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
@Getter
public enum BarrowsRooms
{
	NW(0, new Zone(new WorldPoint(3517, 9706, 0), new WorldPoint(3540, 9723, 0)), new BarrowsDoors[] { BarrowsDoors.NW_EAST_DOOR, BarrowsDoors.NW_SOUTH_DOOR, BarrowsDoors.NW_NORTH_DOOR, BarrowsDoors.NW_WEST_DOOR }),
	N(1, new Zone(new WorldPoint(3541, 9706, 0), new WorldPoint(3562, 9716, 0)),
		new BarrowsDoors[] { BarrowsDoors.NORTH_SOUTH_DOOR, BarrowsDoors.NW_EAST_DOOR, BarrowsDoors.NE_WEST_DOOR }),
	NE(2, new Zone(new WorldPoint(3563, 9706, 0), new WorldPoint(3580, 9717, 0)),
		new BarrowsDoors[] { BarrowsDoors.NE_WEST_DOOR, BarrowsDoors.NE_SOUTH_DOOR, BarrowsDoors.NW_NORTH_DOOR, BarrowsDoors.NW_EAST_DOOR }),
	W(3, new Zone(new WorldPoint(3530, 9684, 0), new WorldPoint(3540, 9705, 0)),
		new BarrowsDoors[] { BarrowsDoors.WEST_EAST_DOOR, BarrowsDoors.NW_SOUTH_DOOR, BarrowsDoors.SW_NORTH_DOOR }),
	C(4, new Zone(new WorldPoint(3541, 9684, 0), new WorldPoint(3562, 9705, 0)), new BarrowsDoors[] { null }),
	E(5, new Zone(new WorldPoint(3563, 9684, 0), new WorldPoint(3573, 9705, 0)),
		new BarrowsDoors[] { BarrowsDoors.EAST_WEST_DOOR, BarrowsDoors.NE_SOUTH_DOOR, BarrowsDoors.SE_NORTH_DOOR}),
	SW(6, new Zone(new WorldPoint(3525, 9671, 0), new WorldPoint(3540, 9683, 0)),
		new BarrowsDoors[] { BarrowsDoors.SW_NORTH_DOOR, BarrowsDoors.SW_EAST_DOOR, BarrowsDoors.NW_WEST_DOOR, BarrowsDoors.SW_SOUTH_DOOR }),
	S(7, new Zone(new WorldPoint(3541, 9672, 0), new WorldPoint(3562, 9683, 0)),
		new BarrowsDoors[] { BarrowsDoors.SOUTH_NORTH_DOOR, BarrowsDoors.SW_EAST_DOOR, BarrowsDoors.SE_WEST_DOOR }),
	SE(8, new Zone(new WorldPoint(3563, 9667, 0), new WorldPoint(3580, 9683, 0)),
		new BarrowsDoors[] { BarrowsDoors.SE_WEST_DOOR, BarrowsDoors.SE_NORTH_DOOR, BarrowsDoors.SW_SOUTH_DOOR, BarrowsDoors.NE_EAST_DOOR });

	private final int id;
	private final Zone area;
	private final BarrowsDoors[] paths;
}
