package com.questhelper.util;

import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.InterfaceID;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilsTest
{
	@Test
	void pack()
	{
		assertEquals(
			Utils.packWidget(InterfaceID.FAIRY_RING_PANEL, 8),
			ComponentID.FAIRY_RING_PANEL_FAVORITES
		);
	}

	@Test
	void unpack()
	{
		assertEquals(
			Utils.unpackWidget(ComponentID.RESIZABLE_VIEWPORT_BOTTOM_LINE_INTERFACE_CONTAINER),
			Pair.of(164, 69)
		);
		assertEquals(
			Utils.unpackWidget(ComponentID.RESIZABLE_VIEWPORT_BOTTOM_LINE_MINIMAP),
			Pair.of(164, 90)
		);
	}
}
