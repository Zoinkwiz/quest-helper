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
import com.questhelper.bank.QuestBank;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.steps.widget.AbstractWidgetHighlight;
import com.questhelper.tools.QuestHelperWorldMapPoint;
import com.questhelper.tools.QuestTile;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.steps.overlay.DirectionArrow;
import com.questhelper.steps.overlay.WorldLines;
import com.questhelper.steps.tools.QuestPerspective;
import com.questhelper.util.worldmap.WorldMapAreaChanged;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.runelite.api.GameState;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
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
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.PluginMessage;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

public class DetailedQuestStep extends QuestStep
{
	@Inject
	WorldMapPointManager worldMapPointManager;

	@Inject
	EventBus eventBus;

	@Inject
	private QuestBank questBank;

	@Getter
	protected WorldPoint worldPoint;

	@Setter
	protected Zone highlightZone;

	@Setter
	protected List<WorldPoint> linePoints;

	@Setter
	protected List<WorldPoint> worldLinePoints;

	private final List<QuestTile> markedTiles = new ArrayList<>();

	@Getter
	protected final List<Requirement> requirements = new ArrayList<>();

	@Getter
	protected final List<Requirement> recommended = new ArrayList<>();

	@Getter
	protected final List<Requirement> teleport = new ArrayList<>();

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

	public boolean hideRequirements;
	public boolean considerBankForItemHighlight;
	public int iconToUseForNeededItems = -1;


	public DetailedQuestStep(QuestHelper questHelper, String text, Requirement... requirements)
	{
		super(questHelper, text);
		this.requirements.addAll(Arrays.asList(requirements));
	}

	public DetailedQuestStep(QuestHelper questHelper, String text, List<Requirement> requirements)
	{
		super(questHelper, text);
		this.requirements.addAll(requirements);
	}

	public DetailedQuestStep(QuestHelper questHelper, WorldPoint worldPoint, String text, Requirement... requirements)
	{
		super(questHelper, text);
		this.worldPoint = worldPoint;
		this.requirements.addAll(Arrays.asList(requirements));
	}

	public DetailedQuestStep(QuestHelper questHelper, WorldPoint worldPoint, String text, List<Requirement> requirements, List<Requirement> recommended)
	{
		super(questHelper, text);
		this.worldPoint = worldPoint;
		if (requirements != null)
		{
			this.requirements.addAll(requirements);
		}
		if (recommended != null)
		{
			this.recommended.addAll(recommended);
		}
	}

	public DetailedQuestStep(QuestHelper questHelper, String text, List<Requirement> requirements, List<Requirement> recommended)
	{
		this(questHelper, null, text, requirements, recommended);
	}

	@Override
	public void startUp()
	{
		super.startUp();
		if (worldPoint != null)
		{
			mapPoint = new QuestHelperWorldMapPoint(worldPoint, getQuestImage());
			worldMapPointManager.add(mapPoint);

			setShortestPath();
		}
		addItemTiles(requirements);
		addItemTiles(recommended);
		started = true;

	}

	@Override
	public void shutDown()
	{
		worldMapPointManager.removeIf(QuestHelperWorldMapPoint.class::isInstance);
		removeShortestPath();
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

	public void setRequirements(List<? extends Requirement> newRequirements)
	{
		requirements.clear();
		requirements.addAll(newRequirements);
	}

	public void addRecommended(Requirement newRecommended)
	{
		recommended.add(newRecommended);
	}

	public void setRecommended(List<Requirement> newRecommended)
	{
		recommended.clear();
		recommended.addAll(newRecommended);
	}

	public void addTeleport(Requirement newTeleport)
	{
		teleport.add(newTeleport);
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
		if (started)
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
				LocalPoint localPoint = QuestPerspective.getInstanceLocalPointFromReal(client, location.getWorldPoint());
				if (localPoint != null)
				{
					OverlayUtil.renderTileOverlay(client, graphics, localPoint, combatIcon, questHelper.getConfig().targetOverlayColor());
				}
			}
		}

		if (highlightZone != null)
		{
			Polygon zonePoly = QuestPerspective.getZonePoly(client, highlightZone);
			OverlayUtil.renderPolygon(graphics, zonePoly, questHelper.getConfig().targetOverlayColor());
		}

		tileHighlights.keySet().forEach(tile -> checkAllTilesForHighlighting(tile, tileHighlights.get(tile), graphics));
		renderTileIcon(graphics);
	}

	protected void renderTileIcon(Graphics2D graphics)
	{
		LocalPoint lp = QuestPerspective.getInstanceLocalPointFromReal(client, worldPoint);
		if (lp != null && icon != null && iconItemID != -1)
		{
			OverlayUtil.renderTileOverlay(client, graphics, lp, icon, questHelper.getConfig().targetOverlayColor());
		}
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

			LocalPoint lp = QuestPerspective.getInstanceLocalPointFromReal(client, worldPoint);
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

	public void addTileMarkers(int spriteID, WorldPoint... worldPoints)
	{
		for (WorldPoint point : worldPoints)
		{
			markedTiles.add(new QuestTile(point, spriteID));
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
		if (hideRequirements)
		{
			return;
		}

		renderInventory(graphics);
		for (AbstractWidgetHighlight widgetHighlights : widgetsToHighlight)
		{
			widgetHighlights.highlightChoices(graphics, client, plugin);
		}
	}

	public void makeDirectionOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		if (!hideMinimapLines && plugin.getConfig().showWorldLines())
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

			if (currentRender < MAX_RENDER_SIZE / 2 || !getQuestHelper().getConfig().haveMinimapArrowFlash())
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

	@Subscribe
	public void onWorldMapAreaChanged(WorldMapAreaChanged worldMapAreaChanged)
	{
		if (mapPoint != null)
		{
			mapPoint.onWorldMapAreaChanged(worldMapAreaChanged);
		}
	}

	@Override
	public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin, @NonNull List<String> additionalText, @NonNull List<Requirement> additionalRequirements)
	{
		super.makeOverlayHint(panelComponent, plugin, additionalText, new ArrayList<>());

		if (inCutscene || hideRequirements)
		{
			return;
		}

		processRequirements(Stream.concat(requirements.stream(), additionalRequirements.stream()), panelComponent, "Requirements:");
		processRequirements(recommended.stream(), panelComponent, "Recommended:");
		processRequirements(teleport.stream(), panelComponent, "Teleports:");
	}

	private void processRequirements(Stream<Requirement> requirementsStream, PanelComponent panelComponent, String title)
	{
		PanelComponent tmpComponent = new PanelComponent();

		requirementsStream
			.distinct()
			.filter(Objects::nonNull)
			.map(req -> req.getDisplayTextWithChecks(client, questHelper.getConfig()))
			.flatMap(Collection::stream)
			.forEach(line -> tmpComponent.getChildren().add(line));

		if (tmpComponent.getChildren().size() > 0)
		{
			panelComponent.getChildren().add(LineComponent.builder().left(title).build());
			panelComponent.getChildren().addAll(tmpComponent.getChildren());
		}
	}

	protected Widget getInventoryWidget()
	{
		return client.getWidget(ComponentID.INVENTORY_CONTAINER);
	}

	private void renderInventory(Graphics2D graphics)
	{
		Widget inventoryWidget = getInventoryWidget();
		if (inventoryWidget == null || inventoryWidget.isHidden())
		{
			return;
		}

		Color baseColor = questHelper.getConfig().targetOverlayColor();

		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
		WorldPoint goalWp = QuestPerspective.getInstanceWorldPointFromReal(client, worldPoint);
		if (goalWp == null || playerLocation.distanceTo(goalWp) > 100)
		{
			for (Requirement requirement : teleport)
			{
				for (Widget item : inventoryWidget.getDynamicChildren())
				{
					if (requirement instanceof ItemRequirement && ((ItemRequirement) requirement).getAllIds().contains(item.getItemId()))
					{
						highlightInventoryItem(item, baseColor, graphics);
					}
					// TODO: If teleport, highlight teleport in spellbook
				}
			}
		}

		if (requirements.isEmpty())
		{
			return;
		}

		if (inventoryWidget.getDynamicChildren() == null)
		{
			return;
		}

		for (Widget item : inventoryWidget.getDynamicChildren())
		{
			for (Requirement requirement : requirements)
			{
				if (isValidRequirementForRenderInInventory(requirement, item))
				{
					highlightInventoryItem(item, baseColor, graphics);
				}
			}
		}
	}

	private void highlightInventoryItem(Widget item, Color color, Graphics2D graphics)
	{
		Rectangle slotBounds = item.getBounds();
		switch (questHelper.getConfig().highlightStyleInventoryItems())
		{
			case SQUARE:
				graphics.setColor(ColorUtil.colorWithAlpha(color, 65));
				graphics.fill(slotBounds);
				graphics.setColor(color);
				graphics.draw(slotBounds);
				break;
			case OUTLINE:
				BufferedImage outlined = itemManager.getItemOutline(item.getItemId(), item.getItemQuantity(), color);
				graphics.drawImage(outlined, (int) slotBounds.getX(), (int) slotBounds.getY(), null);
				break;
			case FILLED_OUTLINE:
				BufferedImage outline = itemManager.getItemOutline(item.getItemId(), item.getItemQuantity(), color);
				graphics.drawImage(outline, (int) slotBounds.getX(), (int) slotBounds.getY(), null);
				Image image = ImageUtil.fillImage(itemManager.getImage(item.getItemId(), item.getItemQuantity(), false), ColorUtil.colorWithAlpha(color, 65));
				graphics.drawImage(image, (int) slotBounds.getX(), (int) slotBounds.getY(), null);
				break;
			default:
		}
	}

	private boolean isValidRequirementForRenderInInventory(Requirement requirement, Widget item)
	{
		return requirement instanceof ItemRequirement && isValidRenderRequirementInInventory((ItemRequirement) requirement, item);
	}

	private boolean isValidRenderRequirementInInventory(ItemRequirement requirement, Widget item)
	{
		return requirement.shouldHighlightInInventory(client) && requirement.getAllIds().contains(item.getItemId());
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

		// Reduce the two-dimensional array into a single list for processing.
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

			Color highlightColor = questHelper.getConfig().targetOverlayColor();

			for (Requirement requirement : requirements)
			{
				if (isReqValidForHighlighting(requirement, ids))
				{
					if (iconToUseForNeededItems != -1)
					{
						BufferedImage icon = spriteManager.getSprite(iconToUseForNeededItems, 0);
						LocalPoint localPoint = QuestPerspective.getInstanceLocalPointFromReal(client, tile.getWorldLocation());
						if (localPoint != null)
						{
							OverlayUtil.renderTileOverlay(client, graphics, localPoint, icon, questHelper.getConfig().targetOverlayColor());
						}
					}
					else
					{
						highlightGroundItem(tile, highlightColor, graphics, poly);
					}
					return;
				}
			}
		}
	}

	private void highlightGroundItem(Tile tile, Color color, Graphics2D graphics, Polygon poly)
	{
		switch (questHelper.getConfig().highlightStyleGroundItems())
		{
			case CLICK_BOX:
				break;
			case OUTLINE:
				modelOutlineRenderer.drawOutline(
					tile.getItemLayer(),
					questHelper.getConfig().outlineThickness(),
					color,
					questHelper.getConfig().outlineFeathering());
				break;
			case TILE:
				OverlayUtil.renderPolygon(graphics, poly, color);
				break;
			default:
		}
	}

	private boolean isReqValidForHighlighting(Requirement requirement, Collection<Integer> ids)
	{
		return isItemRequirement(requirement)
			&& requirementIsItem((ItemRequirement) requirement)
			&& requirementContainsID((ItemRequirement) requirement, ids)
			&& ((ItemRequirement) requirement).shouldRenderItemHighlights(client)
			&& ((!considerBankForItemHighlight && !requirement.check(client)) ||
			(considerBankForItemHighlight &&
				!((ItemRequirement) requirement).check(client, false, questBank.getBankItems())));
	}

	@Override
	protected void renderHoveredItemTooltip(String tooltipText)
	{
		Menu menu = client.getMenu();
		MenuEntry[] menuEntries = menu.getMenuEntries();
		int last = menuEntries.length - 1;

		if (last < 0)
		{
			return;
		}

		MenuEntry menuEntry = menuEntries[last];

		if (!isActionForRequiredItem(menuEntry))
		{
			return;
		}

		tooltipManager.add(new Tooltip(tooltipText));
	}

	@Override
	protected void renderHoveredMenuEntryPanel(PanelComponent panelComponent, String tooltipText)
	{
		Menu menu = client.getMenu();
		MenuEntry[] currentMenuEntries = menu.getMenuEntries();

		if (currentMenuEntries != null)
		{
			net.runelite.api.Point mousePosition = client.getMouseCanvasPosition();
			int menuX = menu.getMenuX();
			int menuY = menu.getMenuY();
			int menuWidth = menu.getMenuWidth();

			int menuEntryHeight = 15;
			int headerHeight = menuEntryHeight + 3;

			for (int i = currentMenuEntries.length - 1; i >= 0; i--)
			{
				MenuEntry hoveredEntry = currentMenuEntries[i];

				int realPos = currentMenuEntries.length - i - 1;

				if (!isActionForRequiredItem(hoveredEntry)) continue;

				int entryTopY = menuY + headerHeight + realPos * menuEntryHeight;
				int entryBottomY = entryTopY + menuEntryHeight;

				if (mousePosition.getX() > menuX && mousePosition.getX() < menuX + menuWidth &&
					mousePosition.getY() > entryTopY && mousePosition.getY() <= entryBottomY)
				{
					panelComponent.getChildren().add(LineComponent.builder().left(tooltipText).build());
					double infoPanelWidth = panelComponent.getBounds().getWidth();
					int viewportWidth = client.getViewportWidth();
					if (menuX + menuWidth + infoPanelWidth > viewportWidth)
					{
						panelComponent.setPreferredLocation(new java.awt.Point(menuX - (int) infoPanelWidth, entryTopY));
					}
					else
					{
						panelComponent.setPreferredLocation(new java.awt.Point(menuX + menuWidth, entryTopY));
					}

					break;
				}
			}
		}
	}

	protected boolean isActionForRequiredItem(MenuEntry entry)
	{
		int itemID = entry.getIdentifier();
		String option = entry.getOption();
		MenuAction type = entry.getType();
		return requirements.stream().anyMatch((item) ->  item instanceof ItemRequirement &&
			type == MenuAction.GROUND_ITEM_THIRD_OPTION &&
			((ItemRequirement) item).getAllIds().contains(itemID) &&
			!((ItemRequirement) item).check(client, false, questBank.getBankItems()) &&
			option.equals("Take"));
	}

	@Override
	public void setShortestPath()
	{
		if (worldPoint != null)
		{
			WorldPoint playerWp = client.getLocalPlayer().getWorldLocation();
			if (getQuestHelper().getConfig().useShortestPath() && playerWp != null) {
				Map<String, Object> data = new HashMap<>();
				data.put("start", playerWp);
				data.put("target", worldPoint);
				eventBus.post(new PluginMessage("shortestpath", "path", data));
			}
		}
	}

	@Override
	public void removeShortestPath()
	{
		if (getQuestHelper().getConfig().useShortestPath())
		{
			eventBus.post(new PluginMessage("shortestpath", "clear"));
		}
	}

	@Override
	public void disableShortestPath()
	{
		if (worldPoint != null)
		{
			eventBus.post(new PluginMessage("shortestpath", "clear"));
		}
	}
}
