package com.questhelper.util;

import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.WidgetUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilsTest
{
	@Test
	void unpack()
	{
		var interfaceID = InterfaceID.CHATBOX;
		var childID = 1;
		var componentID = WidgetUtil.packComponentId(interfaceID, childID);
		assertEquals(
			Utils.unpackWidget(componentID),
			Pair.of(interfaceID, childID)
		);
	}
}
