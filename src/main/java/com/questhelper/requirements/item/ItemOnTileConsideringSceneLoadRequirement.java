package com.questhelper.requirements.item;

import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.InitializableRequirement;
import com.questhelper.requirements.location.TileIsLoadedRequirement;
import com.questhelper.steps.tools.QuestPerspective;
import lombok.NonNull;
import net.runelite.api.Client;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemOnTileConsideringSceneLoadRequirement implements InitializableRequirement
{
	private final List<Integer> itemID;
	private WorldPoint worldPoint;

	// This is inclusive of 27
	private final int MAX_ZONE = 27;

	private boolean hasFoundItemThisLoad = true;

	private boolean playerHasBeenInRegionThisLoad;

	private TileIsLoadedRequirement tileLoadedReq;

	public ItemOnTileConsideringSceneLoadRequirement(ItemRequirement item, WorldPoint worldPoint)
	{
		assert(item != null);
		assert(worldPoint != null);

		this.itemID = item.getAllIds();
		this.worldPoint = worldPoint;
		tileLoadedReq = new TileIsLoadedRequirement(worldPoint);
	}

	public ItemOnTileConsideringSceneLoadRequirement(int itemID, WorldPoint worldPoint)
	{
		assert(worldPoint != null);

		this.itemID = Collections.singletonList(itemID);
		this.worldPoint = worldPoint;
		tileLoadedReq = new TileIsLoadedRequirement(worldPoint);
	}


	public boolean check(Client client)
	{
		// Scenario 1:
		// Player has entered region, key hasn't loaded in but tile has
		// We should return that 'true' that key is on tile until proven otherwise
		// Scenario 2:
		// Player enters region of key
		// S2 V1: Key is there
		// Key is there, so we set hasFoundItemThisLoad to true
		// S2 V2: Key is not there
		// Key is not there, so we return false, set hasFoundItemThisLoad to false
		// Scenario 3:
		// We have hasFoundItemThisLoad set to true AND in region AND key has gone
		// hasFoundItemThisLoad should be set to false


		// If scene has reloaded, do something

		if (playerInRegion(client) || playerHasBeenInRegionThisLoad)
		{
			hasFoundItemThisLoad = checkAllTiles(client);
		}

		return hasFoundItemThisLoad;
	}

	@NonNull
	@Override
	public String getDisplayText()
	{
		return "";
	}

	// TODO: Consider some implementation which allows for true/false/unknown
	private boolean playerInRegion(Client client)
	{
		// Return true for unknown
		if (worldPoint == null) return true;
		if (!tileLoadedReq.check(client)) return true;

		WorldPoint playerPoint = QuestPerspective.getWorldPointConsideringWorldView(client, client.getLocalPlayer().getWorldView(), client.getLocalPlayer().getWorldLocation());
		if (playerPoint == null) return false;
		if (playerPoint.distanceTo(worldPoint) <= MAX_ZONE)
		{


			playerHasBeenInRegionThisLoad = true;
			return true;
		}
		return false;
	}

	private boolean checkAllTiles(Client client)
	{
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

	@Override
	public void initialize(Client client)
	{

	}

	@Override
	public void updateHandler()
	{
		playerHasBeenInRegionThisLoad = false;
		hasFoundItemThisLoad = true;
	}

	@NonNull
	@Override
	public List<Requirement> getConditions()
	{
		return new ArrayList<>();
	}
}
