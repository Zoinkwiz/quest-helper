package com.questhelper.requirements.item;

import com.questhelper.managers.ActiveRequirementsManager;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.location.TileIsLoadedRequirement;
import com.questhelper.steps.tools.QuestPerspective;
import java.util.Collections;
import java.util.List;

import lombok.NonNull;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

public class ItemOnTileConsideringSceneLoadRequirement implements Requirement
{
	private ActiveRequirementsManager activeRequirementsManager;

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

		WorldPoint playerPoint = QuestPerspective.getRealWorldPointFromLocal(client, client.getLocalPlayer().getWorldLocation());
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
			LocalPoint localPoint = QuestPerspective.getInstanceLocalPointFromReal(client, worldPoint);
			if (localPoint == null) return false;

			Tile tile = client.getScene().getTiles()[client.getPlane()][localPoint.getSceneX()][localPoint.getSceneY()];
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

				return false;
			}
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

	@Subscribe
	public void onGameStateChanged(final GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOADING || event.getGameState() == GameState.HOPPING)
		{
			playerHasBeenInRegionThisLoad = false;
			hasFoundItemThisLoad = true;
		}
	}

	public void register(Client client, EventBus eventBus, ActiveRequirementsManager activeRequirementsManager)
	{
		eventBus.register(this);
		this.activeRequirementsManager = activeRequirementsManager;
	}

	@Override
	public ActiveRequirementsManager getActiveRequirementsManager()
	{
		return activeRequirementsManager;
	}
}
