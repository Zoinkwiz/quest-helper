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
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
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
import com.questhelper.ItemRequirement;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.QuestHelperWorldMapPoint;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.choice.DialogChoiceSteps;
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

	protected List<ItemRequirement> itemRequirements = new ArrayList<>();

	protected HashMap<Tile, List<Integer>> tileHighlights = new HashMap<>();

	protected QuestHelperWorldMapPoint mapPoint;

	protected static final int MAX_DISTANCE = 2350;
	protected static final int MAX_DRAW_DISTANCE = 16;
	protected int currentRender = 0;

	protected boolean started;

	public DetailedQuestStep(QuestHelper questHelper, String text, ItemRequirement... itemRequirements)
	{
		super(questHelper, text);
		this.itemRequirements.addAll(Arrays.asList(itemRequirements));
	}

	public DetailedQuestStep(QuestHelper questHelper, WorldPoint worldPoint, String text, ItemRequirement... itemRequirements)
	{
		super(questHelper, text);
		this.worldPoint = worldPoint;
		this.itemRequirements.addAll(Arrays.asList(itemRequirements));
	}

	@Override
	public void configure(Binder binder)
	{
	}

	@Override
	public void startUp()
	{
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

	public void addItemRequirement(ItemRequirement itemRequirement)
	{
		itemRequirements.add(itemRequirement);
	}

	public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin)
	{
		super.makeOverlayHint(panelComponent, plugin);

		if (itemRequirements.isEmpty())
		{
			return;
		}

		if (inCutscene)
		{
			return;
		}

		panelComponent.getChildren().add(LineComponent.builder().left("Required Items:").build());
		for (ItemRequirement itemRequirement : itemRequirements)
		{
			String text = "";
			if (itemRequirement.showQuantity())
			{
				text = itemRequirement.getQuantity() + " x ";
			}
			text = text + itemRequirement.getName();

			Color color;
			if (!itemRequirement.isActualItem())
			{
				color = Color.GRAY;
			}
			else if (itemRequirement.check(client))
			{
				color = Color.GREEN;
			}
			else
			{
				color = Color.RED;
			}
			Color equipColor = Color.GREEN;
			panelComponent.getChildren().add(LineComponent.builder()
				.left(text)
				.leftColor(color)
				.build());
			if (itemRequirement.isEquip())
			{
				String equipText = "(equipped)";
				if (!itemRequirement.check(client, true))
				{
					equipColor = Color.RED;
				}
				panelComponent.getChildren().add(LineComponent.builder()
					.left(equipText)
					.leftColor(equipColor)
					.build());
			}
			if (itemRequirement.getTip() != null && color == Color.RED)
			{
				panelComponent.getChildren().add(LineComponent.builder()
					.left("- " + itemRequirement.getTip())
					.leftColor(Color.WHITE)
					.build());
			}
		}
	}

	@Subscribe
	public void onItemSpawned(ItemSpawned itemSpawned)
	{
		TileItem item = itemSpawned.getItem();
		Tile tile = itemSpawned.getTile();
		for (ItemRequirement itemRequirement : itemRequirements)
		{
			if (itemRequirement.getAllIds().contains(item.getId()))
			{
				tileHighlights.computeIfAbsent(tile, k -> new ArrayList<>());
				tileHighlights.get(tile).add(itemRequirement.getId());
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
							for (ItemRequirement itemRequirement : itemRequirements)
							{
								if (itemRequirement.isActualItem() && itemRequirement.getAllIds().contains(item.getId()))
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

		tileHighlights.forEach((tile, ids) -> checkAllTilesForHighlighting(tile, ids, graphics));
	}

	public void renderArrow(Graphics2D graphics)
	{
		if (worldPoint == null || !worldPoint.isInScene(client))
		{
			return;
		}

		LocalPoint lp = LocalPoint.fromWorld(client, worldPoint);
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

		if (worldPoint.distanceTo(playerLocation) >= MAX_DRAW_DISTANCE)
		{
			createMinimapDirectionArrow(graphics);
			return;
		}

		LocalPoint lp = LocalPoint.fromWorld(client, worldPoint);
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
		double yDiff =   destinationPosOnMinimap.getY() - playerPosOnMinimap.getY();
		double angle = Math.atan2(yDiff, xDiff);

		int startX = (int) (playerPosOnMinimap.getX() - (Math.cos(angle) * 55));
		int startY = (int) (playerPosOnMinimap.getY() + (Math.sin(angle) * 55));

		int endX = (int) (playerPosOnMinimap.getX() - (Math.cos(angle) * 65));
		int endY = (int) ( playerPosOnMinimap.getY() + (Math.sin(angle) * 65));

		Line2D.Double line = new Line2D.Double(startX,startY,endX,endY);

		drawMinimapArrow(graphics, line);
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

	private void drawArrowHead(Graphics2D g2d, Line2D.Double line, AffineTransform tx, Polygon arrowHead) {
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
		AffineTransform tx = new AffineTransform();

		graphics.setStroke(new BasicStroke(7));
		graphics.setColor(Color.BLACK);
		graphics.draw(line);
		Polygon arrowHead = new Polygon();
		arrowHead.addPoint( 0,5);
		arrowHead.addPoint( -7, -5);
		arrowHead.addPoint( 7,-5);
		drawArrowHead(graphics, line, tx, arrowHead);

		graphics.setStroke(new BasicStroke(4));
		graphics.setColor(Color.CYAN);
		graphics.draw(line);
		Polygon arrowHead2 = new Polygon();
		arrowHead2.addPoint( 0,4);
		arrowHead2.addPoint( -6, -5);
		arrowHead2.addPoint( 6,-5);
		drawArrowHead(graphics, line, tx, arrowHead2);
	}

	@Override
	public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		renderInventory(graphics);

		if (mapPoint == null)
		{
			return;
		}

		WorldPoint point = mapPoint.getWorldPoint();

		if (currentRender < 24)
		{
			renderMinimapArrow(graphics);
		}


		Point drawPoint = mapWorldPointToGraphicsPoint(point);
		final Rectangle mapViewArea = getWorldMapClipArea();

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

		if (itemRequirements == null)
		{
			return;
		}

		for (WidgetItem item : inventoryWidget.getWidgetItems())
		{
			for (ItemRequirement itemRequirement : itemRequirements)
			{
				if (itemRequirement.isHighlightInInventory() && itemRequirement.getAllIds().contains(item.getId()))
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
		itemIcon = new BufferedImage(iconBackground.getWidth(), iconBackground.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics tmpGraphics = itemIcon.getGraphics();
		tmpGraphics.drawImage(iconBackground, 0, 0, null);
		int buffer = iconBackground.getWidth() / 2 - itemImg.getWidth() / 2;
		buffer = Math.max(buffer, 0);
		tmpGraphics.drawImage(itemImg, buffer, buffer, null);
	}

	protected void addItemImageToLocation(Graphics2D graphics, LocalPoint lp)
	{
		if (itemIcon == null)
		{
			addIconImage();
		}

		if (inCutscene)
		{
			return;
		}
		OverlayUtil.renderTileOverlay(client, graphics, lp, itemIcon, Color.CYAN);
	}

	protected void addItemImageToLocation(Graphics2D graphics, int x, int y)
	{
		if (itemIcon == null)
		{
			addIconImage();
		}

		if (inCutscene)
		{
			return;
		}

		graphics.drawImage(itemIcon, x, y, null);
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
				for (ItemRequirement itemRequirement : itemRequirements)
				{
					if (itemRequirement.isActualItem() && itemRequirement.getAllIds().contains(id) && !itemRequirement.check(client))
					{
						OverlayUtil.renderPolygon(graphics, poly, Color.CYAN);
						return;
					}
				}
			}
		}
	}
}