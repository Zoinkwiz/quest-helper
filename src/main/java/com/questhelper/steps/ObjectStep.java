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
import net.runelite.api.events.DecorativeObjectDespawned;
import net.runelite.api.events.DecorativeObjectSpawned;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GroundObjectDespawned;
import net.runelite.api.events.GroundObjectSpawned;
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

	private boolean shouldShowArrow = true;

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
			Collection<WorldPoint> localWorldPoints = WorldPoint.toLocalInstance(client, worldPoint);

			for (WorldPoint point : localWorldPoints)
			{
				LocalPoint localPoint = LocalPoint.fromWorld(client, point);
				if (localPoint == null)
				{
					return;
				}

				Tile tile = client.getScene().getTiles()[client.getPlane()][localPoint.getSceneX()][localPoint.getSceneY()];
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

	@Override
	public void shutDown()
	{
		super.shutDown();
		this.objects.clear();
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

	public void showArrow(boolean shouldShowArrow)
	{
		this.shouldShowArrow = shouldShowArrow;
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
		if (event.getGameObject().equals(object))
		{
			object = null;
			clearArrow();
		}
	}

	@Subscribe
	public void onGroundObjectSpawned(GroundObjectSpawned event)
	{
		handleObjects(event.getGroundObject());
	}

	@Subscribe
	public void onGroundObjectDespawned(GroundObjectDespawned event)
	{
		if (event.getGroundObject().equals(object))
		{
			object = null;
			clearArrow();
		}
	}

	@Subscribe
	public void onDecorativeObjectSpawned(DecorativeObjectSpawned event)
	{
		handleObjects(event.getDecorativeObject());
	}

	@Subscribe
	public void onDecorativeObjectDespawned(DecorativeObjectDespawned event)
	{
		if (event.getDecorativeObject().equals(object))
		{
			object = null;
			clearArrow();
		}
	}

	@Subscribe
	public void onWallObjectSpawned(WallObjectSpawned event)
	{
		handleObjects(event.getWallObject());
	}

	@Subscribe
	public void onWallObjectDespawned(WallObjectDespawned event)
	{
		if (event.getWallObject().equals(object))
		{
			object = null;
			clearArrow();
		}
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
		if (object == null || ! shouldShowArrow)
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

	private void handleObjects(TileObject object)
	{
		if (object == null)
		{
			return;
		}

		Collection<WorldPoint> localWorldPoints = null;
		if (worldPoint != null)
		{
			localWorldPoints = WorldPoint.toLocalInstance(client, worldPoint);
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
