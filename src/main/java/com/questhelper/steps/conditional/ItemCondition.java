package com.questhelper.steps.conditional;

import com.questhelper.ItemRequirement;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayUtil;

public class ItemCondition extends ConditionForStep
{
	private ArrayList<Integer> itemID;
	private boolean npcInScene = false;

	public ItemCondition(int itemID)
	{
		this.itemID = new ArrayList<>(Collections.singleton(itemID));
	}

	public ItemCondition(ItemRequirement item)
	{
		this.itemID = item.getAllIds();
	}

	public boolean checkCondition(Client client)
	{
		return checkAllTiles(client);
	}

	// TODO: Make this not massively inefficient
	private boolean checkAllTiles(Client client)
	{
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
