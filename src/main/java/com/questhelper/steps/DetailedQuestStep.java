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

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.questhelper.requirements.Requirement;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import static net.runelite.api.Constants.CHUNK_SIZE;
import net.runelite.api.GameState;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.RenderOverview;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemSpawned;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.QuestHelperWorldMapPoint;
import com.questhelper.questhelpers.QuestHelper;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.ImageUtil;

public class DetailedQuestStep extends QuestStep
{
	@Inject
	ItemManager itemManager;

	@Inject
	WorldMapPointManager worldMapPointManager;

	protected WorldPoint worldPoint;

	@Setter
	protected WorldPoint worldMapPoint;

	@Setter
	protected ArrayList<WorldPoint> linePoints;

	@Setter
	protected List<Requirement> requirements = new ArrayList<>();

	protected HashMap<Tile, List<Integer>> tileHighlights = new HashMap<>();

	protected QuestHelperWorldMapPoint mapPoint;

	protected static final int MAX_DISTANCE = 2350;
	protected static final int MAX_DRAW_DISTANCE = 16;
	protected int currentRender = 0;

	private final Color ARROW_COLOUR = new Color(0, 168, 243);

	protected boolean started;

	public DetailedQuestStep(QuestHelper questHelper, String text, Requirement... requirements)
	{
		super(questHelper, text);
		this.requirements.addAll(Arrays.asList(requirements));
	}

	public DetailedQuestStep(QuestHelper questHelper, WorldPoint worldPoint, String text, Requirement... requirements)
	{
		super(questHelper, text);
		this.worldPoint = worldPoint;
		this.requirements.addAll(Arrays.asList(requirements));
	}

	@Override
	public void configure(Binder binder)
	{
	}

	@Override
	public void startUp()
	{
		super.startUp();
		if(worldMapPoint != null)
		{
			mapPoint = new QuestHelperWorldMapPoint(worldMapPoint, getQuestImage());
			worldMapPointManager.add(mapPoint);
		}
		else if (worldPoint != null)
		{
			mapPoint = new QuestHelperWorldMapPoint(worldPoint, getQuestImage());
			worldMapPointManager.add(mapPoint);
		}
		addItemTiles();
		started = true;
	}

	@Override
	public void shutDown()
	{
		worldMapPointManager.removeIf(QuestHelperWorldMapPoint.class::isInstance);
		tileHighlights.clear();
		clearArrow();
		started = false;
	}

	@Override
	public void enteredCutscene()
	{
		super.enteredCutscene();
		clearArrow();
	}

	@Override
	public void leftCutscene()
	{
		super.leftCutscene();
	}

	public void clearArrow()
	{
		client.clearHintArrow();
	}

	public void addRequirement(Requirement requirement)
	{
		requirements.add(requirement);
	}

	public void addRequirement(ItemRequirement requirement)
	{
		requirements.add(requirement);
	}
	public void addRequirement(ArrayList<Requirement> requirement)
	{
		requirements.addAll(requirement);
	}

	public void addItemRequirements(ArrayList<ItemRequirement> requirement)
	{
		requirements.addAll(requirement);
	}

	@Override
	public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin, ArrayList<String> additionalText, Requirement... additionalRequirements)
	{
		super.makeOverlayHint(panelComponent, plugin, additionalText);

		if (inCutscene)
		{
			return;
		}

		if (!requirements.isEmpty() || (additionalRequirements != null && additionalRequirements.length > 0))
		{
			panelComponent.getChildren().add(LineComponent.builder().left("Requirements:").build());
		}
		if (!requirements.isEmpty())
		{
			for (Requirement requirement : requirements)
			{
				ArrayList<LineComponent> lines = requirement.getDisplayText(client);

				for (LineComponent line : lines)
				{
					panelComponent.getChildren().add(line);
				}
			}
		}

		if (additionalRequirements != null)
		{
			for (Requirement requirement : additionalRequirements)
			{
				ArrayList<LineComponent> lines = requirement.getDisplayText(client);

				for (LineComponent line : lines)
				{
					panelComponent.getChildren().add(line);
				}
			}
		}
	}

	@Subscribe
	public void onItemSpawned(ItemSpawned itemSpawned)
	{
		TileItem item = itemSpawned.getItem();
		Tile tile = itemSpawned.getTile();
		for (Requirement requirement : requirements)
		{
			if (requirement.getClass() == ItemRequirement.class && ((ItemRequirement) requirement).getAllIds().contains(item.getId()))
			{
				tileHighlights.computeIfAbsent(tile, k -> new ArrayList<>());
				tileHighlights.get(tile).add(((ItemRequirement) requirement).getId());
			}
		}
	}

	@Subscribe
	public void onItemDespawned(ItemDespawned itemDespawned)
	{
		Tile tile = itemDespawned.getTile();

		if (!tileHighlights.containsKey(tile))
		{
			return;
		}
		tileHighlights.get(tile).remove((Object) itemDespawned.getItem().getId());
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOADING)
		{
			tileHighlights.clear();
		}
	}

	private void addItemTiles()
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
							for (Requirement requirement : requirements)
							{
								if (requirement.getClass() == ItemRequirement.class
									&& ((ItemRequirement) requirement).isActualItem()
									&& ((ItemRequirement) requirement).getAllIds().contains(item.getId()))
								{
									tileHighlights.computeIfAbsent(tile, k -> new ArrayList<>());
									tileHighlights.get(tile).add(item.getId());
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		currentRender = (currentRender + 1) % 48;
		if (client.getLocalPlayer() == null)
		{
			return;
		}

		if (inCutscene)
		{
			return;
		}
		if (currentRender < 24)
		{
			renderArrow(graphics);
		}

		if (linePoints != null && linePoints.size() > 1)
		{
			drawLinesOnWorld(graphics);
		}

		tileHighlights.forEach((tile, ids) -> checkAllTilesForHighlighting(tile, ids, graphics));
	}

	public void drawLinesOnWorld(Graphics2D graphics)
	{
		for (int i = 0; i < linePoints.size() - 1; i++)
		{
			LocalPoint startLp = getInstanceLocalPoint(linePoints.get(i));
			LocalPoint endLp = getInstanceLocalPoint(linePoints.get(i+1));
			if (startLp == null || endLp == null)
			{
				continue;
			}

			Line2D.Double newLine = getWorldLines(client, startLp, endLp);
			if (newLine != null)
			{
				OverlayUtil.renderPolygon(graphics, newLine, Color.CYAN);
			}
		}
	}

	public void renderArrow(Graphics2D graphics)
	{
		if (worldPoint == null)
		{
			return;
		}

		LocalPoint lp = getInstanceLocalPoint(worldPoint);
		if (lp == null)
		{
			return;
		}

		BufferedImage arrow = getArrow();

		Point arrowPoint = Perspective.getCanvasImageLocation(client, lp, arrow, 30);
		if (arrowPoint == null)
		{
			return;
		}
		graphics.drawImage(arrow, arrowPoint.getX(), arrowPoint.getY(), null);
	}

	public void renderMinimapArrow(Graphics2D graphics)
	{
		int MAX_DRAW_DISTANCE = 16;
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return;
		}

		WorldPoint playerLocation = player.getWorldLocation();

		WorldPoint wp = getInstanceWorldPoint(worldPoint);
		if (wp == null)
		{
			return;
		}
		if (wp.distanceTo(playerLocation) >= MAX_DRAW_DISTANCE)
		{
			createMinimapDirectionArrow(graphics);
			return;
		}

		LocalPoint lp = LocalPoint.fromWorld(client, wp);
		if (lp == null)
		{
			return;
		}

		Point posOnMinimap = Perspective.localToMinimap(client, lp);
		if (posOnMinimap == null)
		{
			return;
		}

		graphics.drawImage(getSmallArrow(), posOnMinimap.getX() - 5, posOnMinimap.getY() - 14, null);
	}

	protected void createMinimapDirectionArrow(Graphics2D graphics)
	{
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return;
		}

		LocalPoint playerPoint = player.getLocalLocation();
		LocalPoint destinationPoint = LocalPoint.fromWorld(client, worldPoint);

		if (destinationPoint == null)
		{
			return;
		}
		Point playerPosOnMinimap = Perspective.localToMinimap(client, playerPoint);
		Point destinationPosOnMinimap = Perspective.localToMinimap(client, destinationPoint, 10000000);

		if (playerPosOnMinimap == null || destinationPosOnMinimap == null)
		{
			return;
		}

		double xDiff = playerPosOnMinimap.getX() - destinationPosOnMinimap.getX();
		double yDiff = destinationPosOnMinimap.getY() - playerPosOnMinimap.getY();
		double angle = Math.atan2(yDiff, xDiff);

		int startX = (int) (playerPosOnMinimap.getX() - (Math.cos(angle) * 55));
		int startY = (int) (playerPosOnMinimap.getY() + (Math.sin(angle) * 55));

		int endX = (int) (playerPosOnMinimap.getX() - (Math.cos(angle) * 65));
		int endY = (int) ( playerPosOnMinimap.getY() + (Math.sin(angle) * 65));

		Line2D.Double line = new Line2D.Double(startX,startY,endX,endY);

		drawMinimapArrow(graphics, line);
	}

	protected WorldPoint getInstanceWorldPoint(WorldPoint wp)
	{
		Collection<WorldPoint> points = toLocalInstance(client, wp);

		for (WorldPoint point : points)
		{
			if (point != null && point.getPlane() == wp.getPlane() && point.isInScene(client))
			{
				return point;
			}
		}
		return null;
	}

	protected LocalPoint getInstanceLocalPoint(WorldPoint wp)
	{
		WorldPoint instanceWorldPoint = getInstanceWorldPoint(wp);
		if (instanceWorldPoint == null)
		{
			return null;
		}

		LocalPoint localPoint = LocalPoint.fromWorld(client, instanceWorldPoint);
		if (localPoint == null)
		{
			return null;
		}

		return localPoint;
	}

	protected void createMinimapLines(Graphics2D graphics)
	{
		if (linePoints == null || linePoints.size() < 2)
		{
			return;
		}
		for (int i = 0; i < linePoints.size() - 1; i++)
		{
			LocalPoint startPoint = getInstanceLocalPoint(linePoints.get(i));
			LocalPoint destinationPoint = getInstanceLocalPoint(linePoints.get(i+1));
			if (startPoint == null || destinationPoint == null)
			{
				continue;
			}

			Point startPosOnMinimap = Perspective.localToMinimap(client, startPoint, 10000000);
			Point destinationPosOnMinimap = Perspective.localToMinimap(client, destinationPoint, 10000000);

			if (destinationPosOnMinimap == null || startPosOnMinimap == null)
			{
				continue;
			}

			Line2D.Double line = new Line2D.Double(startPosOnMinimap.getX(), startPosOnMinimap.getY(), destinationPosOnMinimap.getX(), destinationPosOnMinimap.getY());

			Rectangle bounds = new Rectangle(0, 0, client.getCanvasWidth(), client.getCanvasHeight());
			Widget minimapWidget = client.getWidget(WidgetInfo.RESIZABLE_MINIMAP_STONES_DRAW_AREA);

			if (minimapWidget == null)
			{
				minimapWidget = client.getWidget(WidgetInfo.RESIZABLE_MINIMAP_DRAW_AREA);
			}
			if (minimapWidget == null)
			{
				minimapWidget = client.getWidget(WidgetInfo.FIXED_VIEWPORT_MINIMAP_DRAW_AREA);
			}

			if (minimapWidget != null)
			{
				bounds = minimapWidget.getBounds();
			}
			boolean isEndOfLine = (linePoints.size() - 2) == i;
			drawLine(graphics, line, isEndOfLine, bounds);
		}
	}

	public void createWorldMapLines(Graphics2D graphics)
	{
		if (linePoints == null || linePoints.size() < 2)
		{
			return;
		}
		Rectangle mapViewArea = getWorldMapClipArea();
		for (int i = 0; i < linePoints.size() - 1; i++)
		{
			Point startPoint = mapWorldPointToGraphicsPoint(linePoints.get(i));
			Point endPoint = mapWorldPointToGraphicsPoint(linePoints.get(i + 1));

			boolean isEndOfLine = (linePoints.size() - 2) == i;
			renderWorldMapLines(graphics, mapViewArea, startPoint, endPoint, isEndOfLine);
		}
	}

	public void setWorldPoint(WorldPoint worldPoint)
	{
		this.worldPoint = worldPoint;
		if(worldMapPoint == null && started)
		{
			if (mapPoint != null)
			{
				worldMapPointManager.remove(mapPoint);
			}
			if (worldPoint != null)
			{
				mapPoint = new QuestHelperWorldMapPoint(worldPoint, getQuestImage());
				worldMapPointManager.add(mapPoint);
			}
			else
			{
				mapPoint = null;
			}
		}
	}

	public void setWorldPoint(int x, int y, int z)
	{
		setWorldPoint(new WorldPoint(x, y, z));
	}

	private void drawArrowHead(Graphics2D g2d, Line2D.Double line) {
		AffineTransform tx = new AffineTransform();

		Polygon arrowHead = new Polygon();
		arrowHead.addPoint( 0,0);
		arrowHead.addPoint( -3, -6);
		arrowHead.addPoint( 3,-6);

		tx.setToIdentity();
		double angle = Math.atan2(line.y2-line.y1, line.x2-line.x1);
		tx.translate(line.x2, line.y2);
		tx.rotate((angle-Math.PI/2d));

		Graphics2D g = (Graphics2D) g2d.create();
		g.setTransform(tx);
		g.fill(arrowHead);
		g.dispose();
	}

	private void drawMinimapArrowHead(Graphics2D g2d, Line2D.Double line, int extaSize) {
		AffineTransform tx = new AffineTransform();

		Polygon arrowHead = new Polygon();
		arrowHead.addPoint( 0,4 + extaSize);
		arrowHead.addPoint( -6 - extaSize, -5 - extaSize);
		arrowHead.addPoint( 6 + extaSize,-5 - extaSize);

		tx.setToIdentity();
		double angle = Math.atan2(line.y2-line.y1, line.x2-line.x1);
		tx.translate(line.x2, line.y2);
		tx.rotate((angle-Math.PI/2d));

		Graphics2D g = (Graphics2D) g2d.create();
		g.setTransform(tx);
		g.fill(arrowHead);
		g.dispose();
	}

	protected void drawMinimapArrow(Graphics2D graphics, Line2D.Double line)
	{
		graphics.setColor(Color.BLACK);
		graphics.setStroke(new BasicStroke(6));
		graphics.draw(line);
		drawMinimapArrowHead(graphics, line, 2);

		graphics.setColor(ARROW_COLOUR);
		graphics.setStroke(new BasicStroke(4));
		graphics.draw(line);
		drawMinimapArrowHead(graphics, line, 0);
	}

	protected void drawLine(Graphics2D graphics, Line2D.Double line, boolean endOfLine, Rectangle clippingRegion)
	{
		graphics.setStroke(new BasicStroke(1));
		graphics.setClip(clippingRegion);
		graphics.setColor(Color.CYAN);
		graphics.draw(line);

		drawArrowHead(graphics, line);
	}

	@Override
	public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		renderInventory(graphics);
		createMinimapLines(graphics);
		createWorldMapLines(graphics);

		if (mapPoint == null)
		{
			return;
		}

		WorldPoint point = mapPoint.getWorldPoint();

		if (currentRender < 24)
		{
			renderMinimapArrow(graphics);
		}

		final Rectangle mapViewArea = getWorldMapClipArea();

		Point drawPoint = mapWorldPointToGraphicsPoint(point);
		renderWorldMapArrow(mapViewArea, drawPoint);
	}

	public Point mapWorldPointToGraphicsPoint(WorldPoint worldPoint)
	{
		RenderOverview ro = client.getRenderOverview();

		if (!ro.getWorldMapData().surfaceContainsPosition(worldPoint.getX(), worldPoint.getY()))
		{
			return null;
		}

		float pixelsPerTile = ro.getWorldMapZoom();

		Widget map = client.getWidget(WidgetInfo.WORLD_MAP_VIEW);
		if (map != null)
		{
			Rectangle worldMapRect = map.getBounds();

			int widthInTiles = (int) Math.ceil(worldMapRect.getWidth() / pixelsPerTile);
			int heightInTiles = (int) Math.ceil(worldMapRect.getHeight() / pixelsPerTile);

			Point worldMapPosition = ro.getWorldMapPosition();

			//Offset in tiles from anchor sides
			int yTileMax = worldMapPosition.getY() - heightInTiles / 2;
			int yTileOffset = (yTileMax - worldPoint.getY() - 1) * -1;
			int xTileOffset = worldPoint.getX() + widthInTiles / 2 - worldMapPosition.getX();

			int xGraphDiff = ((int) (xTileOffset * pixelsPerTile));
			int yGraphDiff = (int) (yTileOffset * pixelsPerTile);

			//Center on tile.
			yGraphDiff -= pixelsPerTile - Math.ceil(pixelsPerTile / 2);
			xGraphDiff += pixelsPerTile - Math.ceil(pixelsPerTile / 2);

			yGraphDiff = worldMapRect.height - yGraphDiff;
			yGraphDiff += (int) worldMapRect.getY();
			xGraphDiff += (int) worldMapRect.getX();

			return new Point(xGraphDiff, yGraphDiff);
		}
		return null;
	}

	private void renderWorldMapArrow(Rectangle mapViewArea, Point drawPoint)
	{
		if (mapViewArea != null &&  drawPoint != null && !mapViewArea.contains(drawPoint.getX(), drawPoint.getY()))
		{
			if (drawPoint.getX() < mapViewArea.getMinX())
			{
				if (drawPoint.getY() < mapViewArea.getMinY())
				{
					mapPoint.rotateArrow(225);
				}
				else if (drawPoint.getY() > mapViewArea.getMaxY())
				{
					mapPoint.rotateArrow(135);
				}
				else
				{
					mapPoint.rotateArrow(180);
				}
			}
			else if (drawPoint.getX() > mapViewArea.getMaxX())
			{
				if (drawPoint.getY() < mapViewArea.getMinY())
				{
					mapPoint.rotateArrow(315);
				}
				else if (drawPoint.getY() > mapViewArea.getMaxY())
				{
					mapPoint.rotateArrow(45);
				}
				else
				{
					mapPoint.rotateArrow(0);
				}
			}
			else
			{
				if (drawPoint.getY() < mapViewArea.getMinY())
				{
					mapPoint.rotateArrow(270);
				}
				else if (drawPoint.getY() > mapViewArea.getMaxY())
				{
					mapPoint.rotateArrow(90);
				}
			}
		}
	}

	private void renderWorldMapLines(Graphics2D graphics, Rectangle mapViewArea, Point startPoint, Point endPoint, boolean isEndOfLine)
	{
		if (mapViewArea == null || startPoint == null || endPoint == null)
		{
			return;
		}
		if (!mapViewArea.contains(startPoint.getX(), startPoint.getY()) && !mapViewArea.contains(endPoint.getX(), endPoint.getY()))
		{
			return;
		}

		Line2D.Double line = new Line2D.Double(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
		drawLine(graphics, line, isEndOfLine, getWorldMapClipArea());
	}

	public static Line2D.Double getWorldLines(@Nonnull Client client, @Nonnull LocalPoint startLocation, LocalPoint endLocation)
	{
		final int plane = client.getPlane();

		final int startX = startLocation.getX();
		final int startY = startLocation.getY();
		final int endX = endLocation.getX();
		final int endY = endLocation.getY();

		final int sceneX = startLocation.getSceneX();
		final int sceneY = startLocation.getSceneY();

		if (sceneX < 0 || sceneY < 0 || sceneX >= Constants.SCENE_SIZE || sceneY >= Constants.SCENE_SIZE)
		{
			return null;
		}

		int tilePlane = plane;

		final int startHeight = Perspective.getTileHeight(client, startLocation, tilePlane);
		final int endHeight = Perspective.getTileHeight(client, endLocation, tilePlane);

		Point p1 = Perspective.localToCanvas(client, startX, startY, startHeight);
		Point p2 = Perspective.localToCanvas(client, endX, endY, endHeight);

		if (p1 == null || p2 == null)
		{
			return null;
		}

		Line2D.Double line = new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY());

		return line;
	}

	private Rectangle getWorldMapClipArea()
	{
		Widget widget = client.getWidget(WidgetInfo.WORLD_MAP_VIEW);
		if (widget == null)
		{
			return null;
		}

		return widget.getBounds();
	}

	private void renderInventory(Graphics2D graphics)
	{
		Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
		if (inventoryWidget == null || inventoryWidget.isHidden())
		{
			return;
		}

		if (requirements == null)
		{
			return;
		}

		for (WidgetItem item : inventoryWidget.getWidgetItems())
		{
			for (Requirement requirement : requirements)
			{
				if (requirement.getClass() == ItemRequirement.class && ((ItemRequirement)requirement).isHighlightInInventory() && ((ItemRequirement)requirement).getAllIds().contains(item.getId()))
				{
					Rectangle slotBounds = item.getCanvasBounds();
					graphics.setColor(new Color(0, 255, 255, 65));
					graphics.fill(slotBounds);
				}
			}
		}
	}

	protected void addIconImage()
	{
		BufferedImage itemImg = itemManager.getImage(iconItemID);
		BufferedImage iconBackground = ImageUtil.getResourceStreamFromClass(getClass(), "/util/clue_arrow.png");
		icon = new BufferedImage(iconBackground.getWidth(), iconBackground.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics tmpGraphics = icon.getGraphics();
		tmpGraphics.drawImage(iconBackground, 0, 0, null);
		int buffer = iconBackground.getWidth() / 2 - itemImg.getWidth() / 2;
		buffer = Math.max(buffer, 0);
		tmpGraphics.drawImage(itemImg, buffer, buffer, null);
	}

	protected void addItemImageToLocation(Graphics2D graphics, LocalPoint lp)
	{
		if (icon == null)
		{
			addIconImage();
		}

		if (inCutscene)
		{
			return;
		}
		OverlayUtil.renderTileOverlay(client, graphics, lp, icon, Color.CYAN);
	}

	protected void addItemImageToLocation(Graphics2D graphics, int x, int y)
	{
		if (icon == null)
		{
			addIconImage();
		}

		if (inCutscene)
		{
			return;
		}

		graphics.drawImage(icon, x, y, null);
	}

	private void checkAllTilesForHighlighting(Tile tile, List<Integer> ids, Graphics2D graphics)
	{
		if (inCutscene)
		{
			return;
		}
		Player player = client.getLocalPlayer();

		if (player == null)
		{
			return;
		}

		LocalPoint playerLocation = player.getLocalLocation();
		if (!ids.isEmpty())
		{
			LocalPoint location = tile.getLocalLocation();

			if (location == null)
			{
				return;
			}

			if (location.distanceTo(playerLocation) > MAX_DISTANCE)
			{
				return;
			}

			Polygon poly = Perspective.getCanvasTilePoly(client, location);
			if (poly == null)
			{
				return;
			}

			for (int id : ids)
			{
				for (Requirement requirement : requirements)
				{
					if (requirement.getClass() == ItemRequirement.class
						&& ((ItemRequirement)requirement).isActualItem()
						&& ((ItemRequirement)requirement).getAllIds().contains(id)
						&& !requirement.check(client))
					{
						OverlayUtil.renderPolygon(graphics, poly, Color.CYAN);
						return;
					}
				}
			}
		}
	}

	public Collection<WorldPoint> toLocalInstance(Client client, WorldPoint worldPoint)
	{
		if (!client.isInInstancedRegion())
		{
			return Collections.singleton(worldPoint);
		}

		// find instance chunks using the template point. there might be more than one.
		List<WorldPoint> worldPoints = new ArrayList<>();

		int[][][] instanceTemplateChunks = client.getInstanceTemplateChunks();
		for (int z = 0; z < instanceTemplateChunks.length; ++z)
		{
			for (int x = 0; x < instanceTemplateChunks[z].length; ++x)
			{
				for (int y = 0; y < instanceTemplateChunks[z][x].length; ++y)
				{
					int chunkData = instanceTemplateChunks[z][x][y];
					int rotation = chunkData >> 1 & 0x3;
					int templateChunkY = (chunkData >> 3 & 0x7FF) * CHUNK_SIZE;
					int templateChunkX = (chunkData >> 14 & 0x3FF) * CHUNK_SIZE;
					if (worldPoint.getX() >= templateChunkX && worldPoint.getX() < templateChunkX + CHUNK_SIZE
						&& worldPoint.getY() >= templateChunkY && worldPoint.getY() < templateChunkY + CHUNK_SIZE)
					{
						WorldPoint p = new WorldPoint(client.getBaseX() + x * CHUNK_SIZE + (worldPoint.getX() & (CHUNK_SIZE - 1)),
							client.getBaseY() + y * CHUNK_SIZE + (worldPoint.getY() & (CHUNK_SIZE - 1)),
							z);
						p = rotate(p, rotation);
						worldPoints.add(p);
					}
				}
			}
		}
		return worldPoints;
	}

	private static WorldPoint rotate(WorldPoint point, int rotation)
	{
		int chunkX = point.getX() & ~(CHUNK_SIZE - 1);
		int chunkY = point.getY() & ~(CHUNK_SIZE - 1);
		int x = point.getX() & (CHUNK_SIZE - 1);
		int y = point.getY() & (CHUNK_SIZE - 1);
		switch (rotation)
		{
			case 1:
				return new WorldPoint(chunkX + y, chunkY + (CHUNK_SIZE - 1 - x), point.getPlane());
			case 2:
				return new WorldPoint(chunkX + (CHUNK_SIZE - 1 - x), chunkY + (CHUNK_SIZE - 1 - y), point.getPlane());
			case 3:
				return new WorldPoint(chunkX + (CHUNK_SIZE - 1 - y), chunkY + x, point.getPlane());
		}
		return point;
	}
}