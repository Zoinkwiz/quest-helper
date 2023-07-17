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

import com.questhelper.QuestHelperConfig;
import static com.questhelper.QuestHelperConfig.ObjectHighlightStyle.OUTLINE;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.overlay.DirectionArrow;
import com.questhelper.steps.tools.QuestPerspective;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import lombok.Setter;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.DecorativeObjectDespawned;
import net.runelite.api.events.DecorativeObjectSpawned;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GroundObjectDespawned;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.api.events.WallObjectDespawned;
import net.runelite.api.events.WallObjectSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayUtil;

public class ObjectStep extends DetailedQuestStep
{
	private final int objectID;
	private final ArrayList<Integer> alternateObjectIDs = new ArrayList<>();
	private TileObject closestObject = null;

	private boolean showAllInArea;

	private final List<TileObject> objects = new ArrayList<>();
	private int lastPlane;
	private boolean revalidateObjects;

	@Setter
	private int maxObjectDistance = 50;

	@Setter
	private int maxRenderDistance = 50;

	public ObjectStep(QuestHelper questHelper, int objectID, WorldPoint worldPoint, String text, Requirement... requirements)
	{
		super(questHelper, worldPoint, text, requirements);
		this.objectID = objectID;
		this.showAllInArea = false;
	}

	public ObjectStep(QuestHelper questHelper, int objectID, WorldPoint worldPoint, String text, boolean showAllInArea, Requirement... requirements)
	{
		super(questHelper, worldPoint, text, requirements);
		this.showAllInArea = showAllInArea;
		this.objectID = objectID;
	}

	public ObjectStep(QuestHelper questHelper, int objectID, String text, Requirement... requirements)
	{
		super(questHelper, null, text, requirements);
		this.objectID = objectID;
	}

	public ObjectStep(QuestHelper questHelper, int objectID, String text, boolean showAllInArea, Requirement... requirements)
	{
		super(questHelper, null, text, requirements);
		this.showAllInArea = showAllInArea;
		this.objectID = objectID;
	}

	public ObjectStep(QuestHelper questHelper, int objectID, WorldPoint worldPoint, String text, List<Requirement> requirements, List<Requirement> recommended)
	{
		super(questHelper, worldPoint, text, requirements, recommended);
		this.objectID = objectID;
		this.showAllInArea = false;
	}

	public void setRevalidateObjects(boolean value)
	{
		this.revalidateObjects = value;
	}

	@Override
	public void startUp()
	{
		super.startUp();
		if (worldPoint != null && !showAllInArea)
		{
			checkTileForObject(worldPoint);
		}
		else
		{
			loadObjects();
		}
	}

	private void loadObjects()
	{
		objects.clear();
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

	@Subscribe
	public void onGameTick(final GameTick event)
	{
		super.onGameTick(event);
		if (revalidateObjects)
		{
			if (lastPlane != client.getPlane())
			{
				lastPlane = client.getPlane();
				loadObjects();
			}
		}
		if (worldPoint == null || showAllInArea)
		{
			return;
		}
		closestObject = null;
		objects.clear();
		checkTileForObject(worldPoint);
	}

	public void checkTileForObject(WorldPoint wp)
	{
		Collection<WorldPoint> localWorldPoints = QuestPerspective.toLocalInstance(client, wp);

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
				Arrays.stream(tile.getGameObjects()).forEach(this::handleObjects);
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
			closestObject = null;
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
	public void onWallObjectSpawned(WallObjectSpawned event)
	{
		handleObjects(event.getWallObject());
	}

	@Subscribe
	public void onWallObjectDespawned(WallObjectDespawned event)
	{
		handleRemoveObjects(event.getWallObject());
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

		if (client.getLocalPlayer() == null)
		{
			return;
		}
		WorldPoint playerPosition = client.getLocalPlayer().getWorldLocation();

		for (TileObject tileObject : objects)
		{
			int distanceFromPlayer = tileObject.getWorldLocation().distanceTo(playerPosition);
			if (maxRenderDistance < distanceFromPlayer)
			{
				continue;
			}

			if (tileObject.getPlane() == client.getPlane())
			{
				if (closestObject == null || closestObject.getWorldLocation().distanceTo(playerPosition) > distanceFromPlayer)
				{
					closestObject = tileObject;
				}

				Color configColor = getQuestHelper().getConfig().targetOverlayColor();

				QuestHelperConfig.ObjectHighlightStyle highlightStyle = visibilityHelper.isObjectVisible(tileObject)
					? questHelper.getConfig().highlightStyleObjects()
					: OUTLINE;

				switch (highlightStyle)
				{
					case CLICK_BOX:
						Color fillColor = new Color(configColor.getRed(), configColor.getGreen(), configColor.getBlue(), 20);
						OverlayUtil.renderHoverableArea(
							graphics,
							tileObject.getClickbox(),
							mousePosition,
							fillColor,
							questHelper.getConfig().targetOverlayColor().darker(),
							questHelper.getConfig().targetOverlayColor()
						);
						break;
					case OUTLINE:
						modelOutlineRenderer.drawOutline(
							tileObject,
							questHelper.getConfig().outlineThickness(),
							configColor,
							questHelper.getConfig().
								outlineFeathering()
						);
						break;
					default:
				}
			}
		}

		if (iconItemID != -1 && closestObject != null)
		{
			Shape clickbox = closestObject.getClickbox();
			if (clickbox != null && !inCutscene)
			{
				Rectangle2D boundingBox = clickbox.getBounds2D();
				graphics.drawImage(icon, (int) boundingBox.getCenterX() - 15, (int) boundingBox.getCenterY() - 10,
					null);
			}
		}
	}

	@Override
	public void renderArrow(Graphics2D graphics)
	{
		if (questHelper.getConfig().showMiniMapArrow())
		{
			if (closestObject == null || hideWorldArrow)
			{
				return;
			}
			Shape clickbox = closestObject.getClickbox();
			if (clickbox != null && questHelper.getConfig().showMiniMapArrow())
			{
				Rectangle2D boundingBox = clickbox.getBounds2D();
				int x = (int) boundingBox.getCenterX();
				int y = (int) boundingBox.getMinY() - 20;

				DirectionArrow.drawWorldArrow(graphics, getQuestHelper().getConfig().targetOverlayColor(), x, y);
			}
		}
	}

	private void handleRemoveObjects(TileObject object)
	{
		if (object.equals(this.closestObject))
		{
			this.closestObject = null;
		}

		objects.remove(object);
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
			localWorldPoints = QuestPerspective.toLocalInstance(client, worldPoint);
		}

		if (object.getId() == objectID || alternateObjectIDs.contains(object.getId()))
		{
			setObjects(object, localWorldPoints);
			return;
		}

		final ObjectComposition comp = client.getObjectDefinition(object.getId());
		final int[] impostorIds = comp.getImpostorIds();

		if (impostorIds != null && comp.getImpostor() != null)
		{
			boolean imposterIsMainObject = comp.getImpostor().getId() == objectID;
			boolean imposterIsAlternateObject = alternateObjectIDs.contains(comp.getImpostor().getId());
			if (imposterIsMainObject || imposterIsAlternateObject)
			{
				setObjects(object, localWorldPoints);
			}
		}
	}

	private void setObjects(TileObject object, Collection<WorldPoint> localWorldPoints)
	{
		if (localWorldPoints != null &&
			(localWorldPoints.contains(object.getWorldLocation()) ||
				localWorldPoints.contains(objLocation(object))))
		{
			if (!this.objects.contains(object))
			{
				this.objects.add(object);
			}
		}
		else if (worldPoint == null)
		{
			if (!this.objects.contains(object))
			{
				this.objects.add(object);
			}
		}
		else if (localWorldPoints != null && showAllInArea)
		{
			for (WorldPoint localWorldPoint : localWorldPoints)
			{
				if (localWorldPoint.distanceTo(objLocation(object)) < maxObjectDistance)
				{
					if (!this.objects.contains(object))
					{
						this.objects.add(object);
					}
				}
			}
		}
	}

	// This is required due to changes in how object.getWorldLocation() works
	// See https://github.com/runelite/runelite/commit/4f34a0de6a0100adf79cac5b92198aa432debc4c
	private WorldPoint objLocation(TileObject obj)
	{
		return WorldPoint.fromLocal(client, obj.getX(), obj.getY(), obj.getPlane());
	}
}
