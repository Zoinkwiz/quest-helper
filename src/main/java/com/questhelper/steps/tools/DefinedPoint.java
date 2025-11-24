/*
 * Copyright (c) 2025, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.steps.tools;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.TileObject;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import java.util.List;

/**
 * Wrapper representing a quest-defined coordinate. Using this type makes it explicit that any comparison
 * or rendering should go through {@link QuestPerspective} helpers so WorldView translations are handled
 * consistently.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefinedPoint
{
	private final WorldPoint worldPoint;

	public static DefinedPoint of(WorldPoint worldPoint)
	{
		return worldPoint == null ? null : new DefinedPoint(worldPoint);
	}

	public int getX()
	{
		return worldPoint.getX();
	}

	public int getY()
	{
		return worldPoint.getY();
	}

	public int getPlane()
	{
		return worldPoint.getPlane();
	}

	public boolean matchesLocalPoint(Client client, LocalPoint runtimeLocalPoint)
	{
		return QuestPerspective.matchesWorldPoint(client, worldPoint, runtimeLocalPoint);
	}

	public boolean matchesLocalPoint(Client client, LocalPoint runtimeLocalPoint, WorldView runtimeWorldView)
	{
		return QuestPerspective.matchesWorldPoint(client, worldPoint, runtimeLocalPoint, runtimeWorldView);
	}

	public boolean matchesWorldPoint(Client client, WorldPoint runtimeWorldPoint, WorldView runtimeWorldView)
	{
		return QuestPerspective.matchesWorldPoint(client, worldPoint, runtimeWorldPoint, runtimeWorldView);
	}

	public boolean matchesTileObject(Client client, TileObject tileObject)
	{
		if (tileObject == null)
		{
			return false;
		}
		return QuestPerspective.matchesWorldPoint(client, worldPoint, tileObject.getWorldLocation(), tileObject.getWorldView());
	}

	public LocalPoint resolveLocalPoint(Client client)
	{
		return QuestPerspective.resolveLocalPointForWorldPoint(client, worldPoint, null);
	}

	public LocalPoint resolveLocalPoint(Client client, WorldView preferredWorldView)
	{
		return QuestPerspective.resolveLocalPointForWorldPoint(client, worldPoint, preferredWorldView);
	}

	public List<LocalPoint> resolveLocalPoints(Client client)
	{
		var topLevelWorldView = client.getTopLevelWorldView();
		List<LocalPoint> lps = QuestPerspective.getLocalPointsFromWorldPointInInstance(topLevelWorldView, worldPoint);
		var playerWorldView = client.getLocalPlayer().getWorldView();

		if (topLevelWorldView != playerWorldView)
		{
			var moreLps = QuestPerspective.getLocalPointsFromWorldPointInInstance(playerWorldView, worldPoint);
			if (!moreLps.isEmpty()) lps.addAll(moreLps);
		}
		return lps;
	}

	public int distanceTo(WorldPoint runtimeWorldPoint)
	{
		return worldPoint.distanceTo(runtimeWorldPoint);
	}

	public int distanceTo(Client client, LocalPoint runtimeLocalPoint)
	{
		return QuestPerspective.getTileDistance(client, this, runtimeLocalPoint);
	}
}

