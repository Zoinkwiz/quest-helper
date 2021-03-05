/*
 * Copyright (c) 2020, Zoinkwiz
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
package com.questhelper.requirements.conditional;

import com.questhelper.steps.tools.QuestPerspective;
import java.util.Collection;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import static net.runelite.api.Perspective.SCENE_SIZE;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public class ObjectCondition extends ConditionForStep
{
	private final int objectID;
	private final WorldPoint worldPoint;

	@Setter
	private int maxDistanceFromPlayer = -1;

	public ObjectCondition(int objectID)
	{
		this.objectID = objectID;
		this.worldPoint = null;
	}

	public ObjectCondition(int objectID, WorldPoint worldPoint)
	{
		this.objectID = objectID;
		this.worldPoint = worldPoint;
	}

	public boolean check(Client client)
	{
		if (worldPoint != null)
		{
			Collection<WorldPoint> wps = QuestPerspective.toLocalInstance(client, worldPoint);
			for (WorldPoint wp : wps)
			{
				LocalPoint localPoint = LocalPoint.fromWorld(client, wp);
				if (localPoint == null)
				{
					continue;
				}
				Tile tile = client.getScene().getTiles()[client.getPlane()][localPoint.getSceneX()][localPoint.getSceneY()];
				boolean inTile = checkTile(tile);
				WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
				boolean playerClose = maxDistanceFromPlayer == -1 ||
					(playerLocation.distanceTo(wp) < maxDistanceFromPlayer);
				if (inTile && playerClose)
				{
					return true;
				}
			}
		}
		else
		{
			Tile[][] tiles;
			tiles = client.getScene().getTiles()[client.getPlane()];

			for (int x = 0; x < SCENE_SIZE; x++)
			{
				for (int y = 0; y < SCENE_SIZE; y++)
				{
					if (checkTile(tiles[x][y]))
					{
						return true;
					}
				}
			}
		}

		return false;
	}

	private boolean checkTile(Tile tile)
	{
		if (tile == null)
		{
			return false;
		}

		for (GameObject object : tile.getGameObjects())
		{
			if (checkForObjects(object)) return true;
		}
		if (checkForObjects(tile.getDecorativeObject())) return true;
		if (checkForObjects(tile.getGroundObject())) return true;
		if (checkForObjects(tile.getWallObject())) return true;

		return false;
	}

	private boolean checkForObjects(TileObject object)
	{
		return object != null && object.getId() == objectID;
	}

	@Override
	public void updateHandler()
	{
		// Once this has checks done in ConditionalStep, this will need to set the boolean condition to false
	}
}
