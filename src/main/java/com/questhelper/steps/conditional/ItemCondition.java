package com.questhelper.steps.conditional;

import com.questhelper.requirements.ItemRequirement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public class ItemCondition extends ConditionForStep
{
	private ArrayList<Integer> itemID;
	private WorldPoint worldPoint;
	private boolean npcInScene = false;

	public ItemCondition(int itemID)
	{
		this.itemID = new ArrayList<>(Collections.singleton(itemID));
	}

	public ItemCondition(ItemRequirement item)
	{
		this.itemID = item.getAllIds();
	}

	public ItemCondition(int itemID, WorldPoint worldPoint)
	{
		this.itemID = new ArrayList<>(Collections.singleton(itemID));
		this.worldPoint = worldPoint;
	}

	public ItemCondition(ItemRequirement item, WorldPoint worldPoint)
	{
		this.itemID = item.getAllIds();
		this.worldPoint = worldPoint;
	}


	public boolean checkCondition(Client client)
	{
		return checkAllTiles(client);
	}

	// TODO: Make this not massively inefficient
	private boolean checkAllTiles(Client client)
	{
		if (worldPoint != null)
		{
			Collection<WorldPoint> localWorldPoints = WorldPoint.toLocalInstance(client, worldPoint);

			for (WorldPoint point : localWorldPoints)
			{
				LocalPoint localPoint = LocalPoint.fromWorld(client, point);
				if (localPoint == null)
				{
					continue;
				}

				Tile tile = client.getScene().getTiles()[client.getPlane()][localPoint.getSceneX()][localPoint.getSceneY()];
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
