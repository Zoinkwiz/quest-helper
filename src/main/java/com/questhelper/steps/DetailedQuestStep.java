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
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import net.runelite.api.GameState;
import net.runelite.api.Perspective;
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
	protected List<ItemRequirement> itemRequirements = new ArrayList<>();
	protected HashMap<Integer, List<Tile>> tileHighlights = new HashMap<>();

	protected HashMap<Tile, List<Integer>> newTileHighlights = new HashMap<>();

	protected static final int MAX_DISTANCE = 2350;

	@Getter
	public DialogChoiceSteps choices = new DialogChoiceSteps();

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
		if (worldPoint != null)
		{
			worldMapPointManager.add(new QuestHelperWorldMapPoint(worldPoint, getQuestImage()));
			setArrow();
		}
		addItemTiles();
	}

	@Override
	public void shutDown()
	{
		worldMapPointManager.removeIf(QuestHelperWorldMapPoint.class::isInstance);
		tileHighlights.clear();
		clearArrow();
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
		setArrow();
	}

	public void setArrow()
	{
		Collection<WorldPoint> localWorldPoints = WorldPoint.toLocalInstance(client, worldPoint);
		if (localWorldPoints.isEmpty())
		{
			return;
		}
		client.setHintArrow(localWorldPoints.iterator().next());
	}

	public void clearArrow()
	{
		client.clearHintArrow();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (worldPoint != null)
		{
			setArrow();
		}
		else
		{
			clearArrow();
		}
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
			String equipText = "";
			Color equipColor = Color.GREEN;
			panelComponent.getChildren().add(LineComponent.builder()
				.left(text)
				.leftColor(color)
				.build());
			if (itemRequirement.isEquip())
			{
				equipText = "(equipped)";
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
				newTileHighlights.computeIfAbsent(tile, k -> new ArrayList<>());
				newTileHighlights.get(tile).add(itemRequirement.getId());
			}
		}
	}

	@Subscribe
	public void onItemDespawned(ItemDespawned itemDespawned)
	{
		Tile tile = itemDespawned.getTile();

		if (!newTileHighlights.containsKey(tile))
		{
			return;
		}
		newTileHighlights.get(tile).remove((Object) itemDespawned.getItem().getId());
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOADING)
		{
			newTileHighlights.clear();
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
									newTileHighlights.computeIfAbsent(tile, k -> new ArrayList<>());
									newTileHighlights.get(tile).add(item.getId());
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
		if (client.getLocalPlayer() == null)
		{
			return;
		}

		if (inCutscene)
		{
			return;
		}

		renderInventory(graphics);

		newTileHighlights.forEach((tile, ids) -> {
			checkAllTilesForHighlighting(tile, ids, graphics);
		});
	}

	private void renderInventory(Graphics2D graphics)
	{
		Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
		if (inventoryWidget == null || inventoryWidget.isHidden())
		{
			return;
		}

		if(itemRequirements == null)
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
		BufferedImage mapArrow = ImageUtil.getResourceStreamFromClass(getClass(), "/util/clue_arrow.png");
		itemIcon = new BufferedImage(mapArrow.getWidth(), mapArrow.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics tmpGraphics = itemIcon.getGraphics();
		tmpGraphics.drawImage(mapArrow, 0, 0, null);
		int buffer = mapArrow.getWidth() / 2 - itemImg.getWidth() / 2;
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

	private void checkAllTilesForHighlighting(Tile tile, List<Integer> ids, Graphics2D graphics)
	{
		if (inCutscene)
		{
			return;
		}

		LocalPoint playerLocation = client.getLocalPlayer().getLocalLocation();
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
