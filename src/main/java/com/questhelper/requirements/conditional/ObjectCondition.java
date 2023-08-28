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

import com.questhelper.Zone;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import static net.runelite.api.Perspective.SCENE_SIZE;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;

public class ObjectCondition extends ConditionForStep
{
	private final int objectID;
	private final Zone zone;

	@Setter
	private int maxDistanceFromPlayer = -1;

	@Setter
	private boolean onlyCheckGameObjects = false;

	public ObjectCondition(int objectID)
	{
		this.objectID = objectID;
		this.zone = null;
	}

	public ObjectCondition(int objectID, WorldPoint worldPoint)
	{
		this.objectID = objectID;
		this.zone = new Zone(worldPoint);
	}

	public ObjectCondition(int objectID, Zone zone)
	{
		this.objectID = objectID;
		this.zone = zone;
	}

	public boolean check(Client client)
	{
		Tile[][] tiles;
		tiles = client.getScene().getTiles()[client.getPlane()];

		for (int x = 0; x < SCENE_SIZE; x++)
		{
			for (int y = 0; y < SCENE_SIZE; y++)
			{
				if (checkTile(tiles[x][y], client))
				{
					return true;
				}
			}
		}


		return false;
	}

	private boolean checkTile(Tile tile, Client client)
	{
		if (tile == null)
		{
			return false;
		}
		WorldPoint wp = WorldPoint.fromLocalInstance(client, tile.getLocalLocation());
		if (zone != null && !zone.contains(wp)) return false;

		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
		boolean playerClose = maxDistanceFromPlayer == -1 ||
			(playerLocation.distanceTo(wp) < maxDistanceFromPlayer);

		if (!playerClose) return false;

		for (GameObject object : tile.getGameObjects())
		{
			if (checkForObjects(object)) return true;
		}
		if (onlyCheckGameObjects) return false;

		if (checkForObjects(tile.getDecorativeObject())) return true;
		if (checkForObjects(tile.getGroundObject())) return true;
		if (checkForObjects(tile.getWallObject())) return true;

		return false;
	}

	private boolean checkForObjects(TileObject object)
	{
		return object != null && (object.getId() == objectID || objectID == -1);
	}

	@Override
	public void updateHandler()
	{
		// Once this has checks done in ConditionalStep, this will need to set the boolean condition to false
	}
}
