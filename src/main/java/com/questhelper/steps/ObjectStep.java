/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.steps;

import com.questhelper.requirements.Requirement;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.DecorativeObjectChanged;
import net.runelite.api.events.DecorativeObjectDespawned;
import net.runelite.api.events.DecorativeObjectSpawned;
import net.runelite.api.events.GameObjectChanged;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GroundObjectChanged;
import net.runelite.api.events.GroundObjectDespawned;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.api.events.WallObjectChanged;
import net.runelite.api.events.WallObjectDespawned;
import net.runelite.api.events.WallObjectSpawned;
import net.runelite.client.eventbus.Subscribe;
import com.questhelper.QuestHelperPlugin;
import static com.questhelper.QuestHelperWorldOverlay.CLICKBOX_BORDER_COLOR;
import static com.questhelper.QuestHelperWorldOverlay.CLICKBOX_FILL_COLOR;
import static com.questhelper.QuestHelperWorldOverlay.CLICKBOX_HOVER_BORDER_COLOR;
import com.questhelper.questhelpers.QuestHelper;
import net.runelite.client.ui.overlay.OverlayUtil;

public class ObjectStep extends DetailedQuestStep
{
	private final int objectID;
	private final ArrayList<Integer> alternateObjectIDs = new ArrayList<>();
	private TileObject object;

	private final List<TileObject> objects = new ArrayList<>();

	public ObjectStep(QuestHelper questHelper, int objectID, WorldPoint worldPoint, String text, Requirement... requirements)
	{
		super(questHelper, worldPoint, text, requirements);
		this.objectID = objectID;
	}

	public ObjectStep(QuestHelper questHelper, int objectID, String text, Requirement... requirements)
	{
		super(questHelper, null, text, requirements);
		this.objectID = objectID;
	}

	@Override
	public void startUp()
	{
		super.startUp();

		if (worldPoint != null)
		{
			checkTileForObject(worldPoint);
		}
		else
		{
			Tile[][] tiles = client.getScene().getTiles()[client.getPlane()];
			for (Tile[] lineOfTiles : tiles)
			{
				for (Tile tile : lineOfTiles)
				{
					if (tile != null)
					{
						for (GameObject object : tile.getGameObjects())
						{
							handleObjects(object);
						}

						handleObjects(tile.getDecorativeObject());
						handleObjects(tile.getGroundObject());
						handleObjects(tile.getWallObject());
					}
				}
			}
		}
	}

	@Subscribe
	public void onGameTick(final GameTick event)
	{
		if (worldPoint == null)
		{
			return;
		}
		object = null;
		objects.clear();
		checkTileForObject(worldPoint);
	}

	public void checkTileForObject(WorldPoint wp)
	{
		Collection<WorldPoint> localWorldPoints = toLocalInstance(client, wp);

		for (WorldPoint point : localWorldPoints)
		{
			LocalPoint localPoint = LocalPoint.fromWorld(client, point);
			if (localPoint == null)
			{
				continue;
			}
			Tile[][][] tiles = client.getScene().getTiles();
			if (tiles == null)
			{
				continue;
			}

			Tile tile = tiles[client.getPlane()][localPoint.getSceneX()][localPoint.getSceneY()];
			if (tile != null)
			{
				for (GameObject object : tile.getGameObjects())
				{
					handleObjects(object);
				}

				handleObjects(tile.getDecorativeObject());
				handleObjects(tile.getGroundObject());
				handleObjects(tile.getWallObject());
			}
		}
	}

	@Override
	public void shutDown()
	{
		super.shutDown();
		objects.clear();
	}

	@Subscribe
	@Override
	public void onGameStateChanged(GameStateChanged event)
	{
		super.onGameStateChanged(event);
		if (event.getGameState() == GameState.LOADING)
		{
			object = null;
			objects.clear();
		}
	}

	public void addAlternateObjects(Integer... alternateObjectIDs)
	{
		this.alternateObjectIDs.addAll(Arrays.asList(alternateObjectIDs));
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		handleObjects(event.getGameObject());
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		handleRemoveObjects(event.getGameObject());
	}

	@Subscribe
	public void onGameObjectChanged(GameObjectChanged event)
	{
		handleRemoveObjects(event.getPrevious());
		handleObjects(event.getGameObject());
	}

	@Subscribe
	public void onGroundObjectSpawned(GroundObjectSpawned event)
	{
		handleObjects(event.getGroundObject());
	}

	@Subscribe
	public void onGroundObjectDespawned(GroundObjectDespawned event)
	{
		handleRemoveObjects(event.getGroundObject());
	}

	@Subscribe
	public void onGroundObjectChanged(GroundObjectChanged event)
	{
		handleRemoveObjects(event.getPrevious());
		handleObjects(event.getGroundObject());
	}

	@Subscribe
	public void onDecorativeObjectSpawned(DecorativeObjectSpawned event)
	{
		handleObjects(event.getDecorativeObject());
	}

	@Subscribe
	public void onDecorativeObjectDespawned(DecorativeObjectDespawned event)
	{
		handleRemoveObjects(event.getDecorativeObject());
	}

	@Subscribe
	public void onDecorativeObjectChanged(DecorativeObjectChanged event)
	{
		handleRemoveObjects(event.getPrevious());
		handleObjects(event.getDecorativeObject());
	}

	@Subscribe
	public void onWallObjectSpawned(WallObjectSpawned event)
	{
		handleObjects(event.getWallObject());
	}

	@Subscribe
	public void onWallObjectDespawned(WallObjectDespawned event)
	{
		handleRemoveObjects(event.getWallObject());
	}

	@Subscribe
	public void onWallObjectChanged(WallObjectChanged event)
	{
		handleRemoveObjects(event.getPrevious());
		handleObjects(event.getWallObject());
	}

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWorldOverlayHint(graphics, plugin);
		if (objects.isEmpty())
		{
			return;
		}

		if (inCutscene)
		{
			return;
		}

		Point mousePosition = client.getMouseCanvasPosition();
		for (TileObject tileObject : objects)
		{
			if (tileObject.getPlane() == client.getPlane())
			{
				OverlayUtil.renderHoverableArea(graphics, tileObject.getClickbox(), mousePosition,
					CLICKBOX_FILL_COLOR, CLICKBOX_BORDER_COLOR, CLICKBOX_HOVER_BORDER_COLOR);
			}
		}

		if (iconItemID != -1 && object != null)
		{
			Shape clickbox = object.getClickbox();
			if (clickbox != null)
			{
				Rectangle2D boundingBox = clickbox.getBounds2D();
				addItemImageToLocation(graphics, (int) boundingBox.getCenterX() - 15, (int) boundingBox.getCenterY() - 10);
			}
		}
	}

	@Override
	public void renderArrow(Graphics2D graphics)
	{
		if (object == null || ! hideWorldArrow)
		{
			return;
		}
		Shape clickbox = object.getClickbox();
		if (clickbox != null)
		{
			BufferedImage arrow = getArrow();
			Rectangle2D boundingBox = clickbox.getBounds2D();
			int x = (int) boundingBox.getCenterX() - ARROW_SHIFT_X;
			int y = (int) boundingBox.getMinY() - ARROW_SHIFT_Y;
			Point point = new Point(x, y);

			OverlayUtil.renderImageLocation(graphics, point, arrow);
		}
	}

	private void handleRemoveObjects(TileObject object)
	{
		if (object.equals(this.object))
		{
			this.object = null;
			clearArrow();
		}

		if (objects.contains(object))
		{
			objects.remove(object);
		}
	}

	private void handleObjects(TileObject object)
	{
		if (object == null)
		{
			return;
		}

		Collection<WorldPoint> localWorldPoints = null;
		if (worldPoint != null)
		{
			localWorldPoints = toLocalInstance(client, worldPoint);
		}
		
		if (object.getId() == objectID || alternateObjectIDs.contains(object.getId()))
		{
			if (localWorldPoints != null && localWorldPoints.contains(object.getWorldLocation()))
			{
				this.object = object;
				this.objects.add(object);
				return;
			}
			if (worldPoint == null)
			{
				this.object = object;
				this.objects.add(object);
			}
		}
	}
}
