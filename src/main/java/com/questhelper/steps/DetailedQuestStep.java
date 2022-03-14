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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.QuestHelperWorldMapPoint;
import com.questhelper.QuestTile;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.overlay.DirectionArrow;
import com.questhelper.steps.overlay.WorldLines;
import com.questhelper.steps.tools.QuestPerspective;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.GameState;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.SpriteID;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemSpawned;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.eventbus.Subscribe;
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

	private final List<QuestTile> markedTiles = new ArrayList<>();

	@Getter
	protected final List<Requirement> requirements = new ArrayList<>();

	protected Multimap<Tile, Integer> tileHighlights = ArrayListMultimap.create();

	protected QuestHelperWorldMapPoint mapPoint;

	protected static final int MAX_DISTANCE = 2350;

	protected int currentRender = 0;

	protected final int MAX_RENDER_SIZE = 4;

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
		if (worldMapPoint != null)
		{
			mapPoint = new QuestHelperWorldMapPoint(worldMapPoint, getQuestImage());
			worldMapPointManager.add(mapPoint);
		}
		else if (worldPoint != null)
		{
			mapPoint = new QuestHelperWorldMapPoint(worldPoint, getQuestImage());
			worldMapPointManager.add(mapPoint);
		}
		addItemTiles(requirements);
		started = true;
	}

	@Override
	public void shutDown()
	{
		worldMapPointManager.removeIf(QuestHelperWorldMapPoint.class::isInstance);
		tileHighlights.clear();
		started = false;
	}

	public void addRequirement(Requirement requirement)
	{
		requirements.add(requirement);
	}

	public void addRequirement(Requirement... requirements)
	{
		this.requirements.addAll(Arrays.asList(requirements));
	}

	public void addItemRequirements(List<ItemRequirement> requirement)
	{
		requirements.addAll(requirement);
	}

	public void emptyRequirements()
	{
		requirements.clear();
	}

	public void setRequirements(List<Requirement> newRequirements)
	{
		requirements.clear();
		requirements.addAll(newRequirements);
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
		if (worldMapPoint == null && started)
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
		if (client.getLocalPlayer() == null)
		{
			return;
		}

		if (inCutscene)
		{
			return;
		}

		if (!markedTiles.isEmpty())
		{
			for (QuestTile location : markedTiles)
			{
				BufferedImage combatIcon = spriteManager.getSprite(location.getIconID(), 0);
				LocalPoint localPoint = QuestPerspective.getInstanceLocalPoint(client, location.getWorldPoint());
				if (localPoint != null)
				{
					OverlayUtil.renderTileOverlay(client, graphics, localPoint, combatIcon, questHelper.getConfig().targetOverlayColor());
				}
			}
		}

		tileHighlights.keySet().forEach(tile -> checkAllTilesForHighlighting(tile, tileHighlights.get(tile), graphics));
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		currentRender = (currentRender + 1) % MAX_RENDER_SIZE;
	}

	@Override
	public void makeWorldArrowOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		if (client.getLocalPlayer() == null)
		{
			return;
		}

		if (inCutscene)
		{
			return;
		}

		if (currentRender < (MAX_RENDER_SIZE / 2))
		{
			renderArrow(graphics);
		}
	}

	@Override
	public void makeWorldLineOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		if (client.getLocalPlayer() == null)
		{
			return;
		}

		if (inCutscene)
		{
			return;
		}

		if (linePoints != null && linePoints.size() > 1)
		{
			WorldLines.drawLinesOnWorld(graphics, client, linePoints, getQuestHelper().getConfig().targetOverlayColor());
		}
	}

	public void renderArrow(Graphics2D graphics)
	{
		if (questHelper.getConfig().showMiniMapArrow())
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
			int startY = poly.getBounds().y + (poly.getBounds().height / 2);

			DirectionArrow.drawWorldArrow(graphics, getQuestHelper().getConfig().targetOverlayColor(),
				startX, startY);
		}
	}

	public void addTileMarker(QuestTile questTile)
	{
		markedTiles.add(questTile);
	}

	public void addTileMarker(WorldPoint worldPoint, int spriteID)
	{
		markedTiles.add(new QuestTile(worldPoint, spriteID));
	}

	public void addTileMarkers(WorldPoint... worldPoints)
	{
		for (WorldPoint point : worldPoints)
		{
			markedTiles.add(new QuestTile(point));
		}
	}

	public void addSafeSpots(WorldPoint... worldPoints)
	{
		for (WorldPoint worldPoint : worldPoints)
		{
			markedTiles.add(new QuestTile(worldPoint, SpriteID.TAB_COMBAT));
		}
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
		if (questHelper.getConfig().showMiniMapArrow())
		{
			if (mapPoint == null)
			{
				return;
			}

			if (inCutscene)
			{
				return;
			}

			WorldPoint point = mapPoint.getWorldPoint();

			if (currentRender < MAX_RENDER_SIZE / 2)
			{
				renderMinimapArrow(graphics);
			}

			final Rectangle mapViewArea = QuestPerspective.getWorldMapClipArea(client);

			Point drawPoint = QuestPerspective.mapWorldPointToGraphicsPoint(client, point);
			DirectionArrow.renderWorldMapArrow(mapViewArea, drawPoint, mapPoint);
		}
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
		if (questHelper.getConfig().showMiniMapArrow())
		{
			DirectionArrow.renderMinimapArrow(graphics, client, worldPoint, getQuestHelper().getConfig().targetOverlayColor());
		}
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
		Stream<Requirement> stream = requirements.stream();
		if (additionalRequirements != null && additionalRequirements.length > 0)
		{
			stream = Stream.concat(stream, Stream.of(additionalRequirements));
		}
		stream
			.distinct()
			.map(req -> req.getDisplayTextWithChecks(client, questHelper.getConfig()))
			.flatMap(Collection::stream)
			.forEach(line -> panelComponent.getChildren().add(line));

	}

	private void renderInventory(Graphics2D graphics)
	{
		Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
		if (inventoryWidget == null || inventoryWidget.isHidden())
		{
			return;
		}

		if (requirements.isEmpty())
		{
			return;
		}

		for (WidgetItem item : inventoryWidget.getWidgetItems())
		{
			for (Requirement requirement : requirements)
			{
				if (isValidRequirementForRenderInInventory(requirement, item))
				{
					Rectangle slotBounds = item.getCanvasBounds();
					int red = getQuestHelper().getConfig().targetOverlayColor().getRed();
					int green = getQuestHelper().getConfig().targetOverlayColor().getGreen();
					int blue = getQuestHelper().getConfig().targetOverlayColor().getBlue();
					graphics.setColor(new Color(red, green, blue, 65));
					graphics.fill(slotBounds);
				}
			}
		}
	}

	private boolean isValidRequirementForRenderInInventory(Requirement requirement, WidgetItem item)
	{
		return requirement instanceof ItemRequirement && isValidRenderRequirementInInventory((ItemRequirement) requirement, item);
	}

	private boolean isValidRenderRequirementInInventory(ItemRequirement requirement, WidgetItem item)
	{
		return requirement.shouldHighlightInInventory(client) && requirement.getAllIds().contains(item.getId());
	}

	@Subscribe
	public void onItemSpawned(ItemSpawned itemSpawned)
	{
		TileItem item = itemSpawned.getItem();
		Tile tile = itemSpawned.getTile();
		for (Requirement requirement : requirements)
		{
			if (isItemRequirement(requirement) && requirementContainsID((ItemRequirement) requirement, item.getId()))
			{
				tileHighlights.get(tile).add(itemSpawned.getItem().getId());
			}
		}
	}

	@Subscribe
	public void onItemDespawned(ItemDespawned itemDespawned)
	{
		Tile tile = itemDespawned.getTile();
		if (tileHighlights.containsKey(tile))
		{
			for (Requirement requirement : requirements)
			{
				if (isItemRequirement(requirement) && requirementContainsID((ItemRequirement) requirement, itemDespawned.getItem().getId()))
				{
					tileHighlights.get(tile).remove(itemDespawned.getItem().getId());
				}
			}
		}
	}

	protected void addItemTiles(Collection<Requirement> requirements)
	{
		if (requirements == null || requirements.isEmpty())
		{
			return;
		}
		Tile[][] squareOfTiles = client.getScene().getTiles()[client.getPlane()];

		// Reduce the two dimensional array into a single list for processing.
		List<Tile> tiles = Stream.of(squareOfTiles)
			.flatMap(Arrays::stream)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
		for (Tile tile : tiles)
		{
			List<TileItem> items = tile.getGroundItems();
			if (items != null)
			{
				for (TileItem item : items)
				{
					if (item == null)
					{
						continue;
					}
					for (Requirement requirement : requirements)
					{
						if (isValidRequirementForTileItem(requirement, item))
						{
							tileHighlights.get(tile).add(item.getId());
							break;
						}
					}
				}
			}
		}
	}

	private boolean isValidRequirementForTileItem(Requirement requirement, TileItem item)
	{
		return isItemRequirement(requirement) && requirementMatchesTileItem((ItemRequirement) requirement, item);
	}

	private boolean isItemRequirement(Requirement requirement)
	{
		return requirement != null && requirement.getClass() == ItemRequirement.class;
	}

	private boolean requirementMatchesTileItem(ItemRequirement requirement, TileItem item)
	{
		return requirementIsItem(requirement) && requirementContainsID(requirement, item.getId());
	}

	private boolean requirementIsItem(ItemRequirement requirement)
	{
		return requirement.isActualItem();
	}

	private boolean requirementContainsID(ItemRequirement requirement, int id)
	{
		return requirement.getAllIds().contains(id);
	}

	private boolean requirementContainsID(ItemRequirement requirement, Collection<Integer> ids)
	{
		return ids.stream().anyMatch(id -> requirementContainsID(requirement, id));
	}

	private void checkAllTilesForHighlighting(Tile tile, Collection<Integer> ids, Graphics2D graphics)
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

			for (Requirement requirement : requirements)
			{
				if (isReqValidForHighlighting(requirement, ids))
				{
					OverlayUtil.renderPolygon(graphics, poly, questHelper.getConfig().targetOverlayColor());
					return;
				}
			}
		}
	}

	private boolean isReqValidForHighlighting(Requirement requirement, Collection<Integer> ids)
	{
		return isItemRequirement(requirement)
			&& requirementIsItem((ItemRequirement) requirement)
			&& requirementContainsID((ItemRequirement) requirement, ids)
			&& ((ItemRequirement) requirement).shouldRenderItemHighlights(client)
			&& !requirement.check(client);
	}
}
