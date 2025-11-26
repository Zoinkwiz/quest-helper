/*
 *
 *  * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice, this
 *  *    list of conditions and the following disclaimer.
 *  * 2. Redistributions in binary form must reproduce the above copyright notice,
 *  *    this list of conditions and the following disclaimer in the documentation
 *  *    and/or other materials provided with the distribution.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.questhelper.requirements.item;

import com.questhelper.requirements.conditional.ConditionForStep;
import com.questhelper.steps.tools.QuestPerspective;
import net.runelite.api.Client;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

import java.util.Collections;
import java.util.List;

public class ItemOnTileRequirement extends ConditionForStep
{
	private final List<Integer> itemID;
	private WorldPoint worldPoint;

	public ItemOnTileRequirement(int itemID)
	{
		this.itemID = Collections.singletonList(itemID);
	}

	public ItemOnTileRequirement(ItemRequirement item)
	{
		assert(item != null);

		this.itemID = item.getAllIds();
	}

	public ItemOnTileRequirement(int itemID, WorldPoint worldPoint)
	{
		assert(worldPoint != null);

		this.itemID = Collections.singletonList(itemID);
		this.worldPoint = worldPoint;
	}

	public ItemOnTileRequirement(ItemRequirement item, WorldPoint worldPoint)
	{
		assert(item != null);
		assert(worldPoint != null);

		this.itemID = item.getAllIds();
		this.worldPoint = worldPoint;
	}


	public boolean check(Client client)
	{
		return checkAllTiles(client);
	}

	private boolean checkAllTiles(Client client)
	{
		if (client.getTopLevelWorldView().getScene() == null) return false;

		if (worldPoint != null)
		{
			List<LocalPoint> localPoints = QuestPerspective.getLocalPointsFromWorldPointInInstance(client.getTopLevelWorldView(), worldPoint);
			for (LocalPoint localPoint : localPoints)
			{
				Tile tile = client.getTopLevelWorldView().getScene().getTiles()[client.getTopLevelWorldView().getPlane()][localPoint.getSceneX()][localPoint.getSceneY()];
				if (tile != null)
				{
					List<TileItem> items = tile.getGroundItems();
					if (items == null) return false;
					for (TileItem item : items)
					{
						if (itemID.contains(item.getId()))
						{
							return true;
						}
					}
				}
			}
			return false;
		}

		Tile[][] squareOfTiles = client.getScene().getTiles()[client.getPlane()];
		for (Tile[] lineOfTiles : squareOfTiles)
		{
			for (Tile tile : lineOfTiles)
			{
				if (tile != null)
				{
					List<TileItem> items = tile.getGroundItems();
					if (items != null)
					{
						for (TileItem item : items)
						{
							if (itemID.contains(item.getId()))
							{
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
}
