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

import com.google.inject.Inject;
import com.questhelper.requirements.AbstractRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.overlay.DirectionArrow;
import com.questhelper.requirements.ItemRequirements;
import com.questhelper.steps.overlay.WorldLines;
import com.questhelper.steps.tools.QuestPerspective;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import lombok.Setter;
import net.runelite.api.GameState;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
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
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.QuestHelperWorldMapPoint;
import com.questhelper.questhelpers.QuestHelper;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;

public class DetailedQuestStep extends QuestStep
{
	@Inject
	WorldMapPointManager worldMapPointManager;

	protected WorldPoint worldPoint;

	@Setter
	protected WorldPoint worldMapPoint;

	@Setter
	protected List<WorldPoint> linePoints;

	@Setter
	protected List<WorldPoint> worldLinePoints;

	@Setter
	protected List<Requirement> requirements = new ArrayList<>();

	protected HashMap<Tile, List<Integer>> tileHighlights = new HashMap<>();

	protected QuestHelperWorldMapPoint mapPoint;

	protected static final int MAX_DISTANCE = 2350;

	protected int currentRender = 0;

	protected boolean started;

	@Setter
	protected boolean hideWorldArrow;

	@Setter
	protected boolean hideMinimapLines;

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
		started = false;
	}

	public void addRequirement(AbstractRequirement requirement)
	{
		requirements.add(requirement);
	}

	public void addItemRequirements(List<ItemRequirement> requirement)
	{
		requirements.addAll(requirement);
	}

	public void setText(String text) {
		this.text = Collections.singletonList(text);
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOADING)
		{
			tileHighlights.clear();
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
			WorldLines.drawLinesOnWorld(graphics, client, linePoints, getQuestHelper().getConfig().targetOverlayColor());
		}

		tileHighlights.forEach((tile, ids) -> checkAllTilesForHighlighting(tile, ids, graphics));
	}

	public void renderArrow(Graphics2D graphics)
	{
		if (worldPoint == null || hideWorldArrow)
		{
			return;
		}

		LocalPoint lp = QuestPerspective.getInstanceLocalPoint(client, worldPoint);
		if (lp == null)
		{
			return;
		}

		Polygon poly = Perspective.getCanvasTilePoly(client, lp, 30);
		if (poly == null || poly.getBounds() == null)
		{
			return;
		}

		int startX = poly.getBounds().x + (poly.getBounds().width / 2);
		int startY =  poly.getBounds().y + (poly.getBounds().height / 2);

		DirectionArrow.drawWorldArrow(graphics, getQuestHelper().getConfig().targetOverlayColor(),
			startX, startY);
	}

	@Override
	public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		renderInventory(graphics);
		if (!hideMinimapLines)
		{
			WorldLines.createMinimapLines(graphics, client, linePoints, getQuestHelper().getConfig().targetOverlayColor());
		}
		renderMapLines(graphics);

		renderMapArrows(graphics);
	}

	public void renderMapArrows(Graphics2D graphics)
	{
		if (mapPoint == null)
		{
			return;
		}

		WorldPoint point = mapPoint.getWorldPoint();

		if (currentRender < 24)
		{
			renderMinimapArrow(graphics);
		}

		final Rectangle mapViewArea = QuestPerspective.getWorldMapClipArea(client);

		Point drawPoint = QuestPerspective.mapWorldPointToGraphicsPoint(client, point);
		DirectionArrow.renderWorldMapArrow(mapViewArea, drawPoint, mapPoint);
	}

	public void renderMapLines(Graphics2D graphics)
	{
		if (linePoints == null || linePoints.size() < 2)
		{
			return;
		}

		List<WorldPoint> worldMapLines = linePoints;
		if (worldLinePoints != null && worldLinePoints.size() > 1)
		{
			worldMapLines = worldLinePoints;
		}
		WorldLines.createWorldMapLines(graphics, client, worldMapLines, getQuestHelper().getConfig().targetOverlayColor());
	}

	public void renderMinimapArrow(Graphics2D graphics)
	{
		DirectionArrow.renderMinimapArrow(graphics, client, worldPoint, getQuestHelper().getConfig().targetOverlayColor());
	}

	@Override
	public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin, List<String> additionalText, Requirement... additionalRequirements)
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
				List<LineComponent> lines = requirement.getDisplayTextWithChecks(client);

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
				List<LineComponent> lines = requirement.getDisplayTextWithChecks(client);

				for (LineComponent line : lines)
				{
					panelComponent.getChildren().add(line);
				}
			}
		}
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
				if ((requirement instanceof ItemRequirement
					&& ((ItemRequirement)requirement).isHighlightInInventory()
					&& ((ItemRequirement)requirement).getAllIds().contains(item.getId()))
					|| (requirement instanceof ItemRequirements
					&& ((ItemRequirements)requirement).isHighlightInInventory()
					&& ((ItemRequirements)requirement).getAllIds().contains(item.getId())))
				{
					Rectangle slotBounds = item.getCanvasBounds();
					graphics.setColor(new Color(getQuestHelper().getConfig().targetOverlayColor().getRed(),
						getQuestHelper().getConfig().targetOverlayColor().getGreen(),
						getQuestHelper().getConfig().targetOverlayColor().getBlue(),
						65));
					graphics.fill(slotBounds);
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
				tileHighlights.get(tile).add((itemSpawned.getItem().getId()));
			}
		}
	}

	@Subscribe
	public void onItemDespawned(ItemDespawned itemDespawned)
	{
		Tile tile = itemDespawned.getTile();

		if (tileHighlights.containsKey(tile))
		{
			tileHighlights.get(tile).remove(Integer.valueOf(itemDespawned.getItem().getId()));
		}
	}

	private void addItemTiles()
	{
		if (requirements == null)
		{
			return;
		}
		Tile[][] squareOfTiles = client.getScene().getTiles()[client.getPlane()];

		for (Tile[] lineOfTiles : squareOfTiles)
		{
			for (Tile tile : lineOfTiles)
			{
				if (tile == null)
				{
					continue;
				}
				List<TileItem> items = tile.getGroundItems();
				if (items != null)
				{
					for (TileItem item : items)
					{
						for (Requirement requirement : requirements)
						{
							if (requirement != null && requirement.getClass() == ItemRequirement.class
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
						OverlayUtil.renderPolygon(graphics, poly, questHelper.getConfig().targetOverlayColor());
						return;
					}
				}
			}
		}
	}
}
