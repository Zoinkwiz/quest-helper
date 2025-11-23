/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.steps.tools;

import com.questhelper.requirements.zone.Zone;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;

import java.awt.*;
import java.util.*;
import java.util.List;

public class QuestPerspective
{
	// Order of poly corners from getCanvasTilePoly
	private final static int SW = 0;
	private final static int NW = 3;
	private final static int NE = 2;
	private final static int SE = 1;

	/**
	 * Converts a LocalPoint to a WorldPoint, handling WorldView considerations
	 * for instanced areas like boats.
	 *
	 * @param client the {@link Client}
	 * @param localPoint the {@link LocalPoint} to convert
	 * @return the {@link WorldPoint} in the real world, or null if conversion fails
	 */
	public static WorldPoint getWorldPointConsideringWorldView(Client client, LocalPoint localPoint)
	{
		if (localPoint == null)
		{
			return null;
		}

		var worldView = client.getWorldView(localPoint.getWorldView());

		// If in a non-top level WorldView (a boat) need to translate
		if (worldView != null && !worldView.isTopLevel())
		{
			// Currently the entity should be the player's boat only?
			var worldEntity = client.getTopLevelWorldView()
				.worldEntities()
				.byIndex(worldView.getId());

			if (worldEntity == null)
			{
				return null;
			}

			var mainLocal = worldEntity.transformToMainWorld(localPoint);
			return WorldPoint.fromLocal(client.getTopLevelWorldView(),
				mainLocal.getX(), mainLocal.getY(), client.getTopLevelWorldView().getPlane());
		}
		else
		{
			return WorldPoint.fromLocalInstance(client, localPoint);
		}
	}

	/**
	 * Converts a WorldPoint from a given WorldView to the main world coordinate system,
	 * handling WorldView considerations for instanced areas like boats.
	 *
	 * @param client the {@link Client}
	 * @param worldView the {@link WorldView} the worldPoint is currently in
	 * @param worldPoint the {@link WorldPoint} to convert
	 * @return the {@link WorldPoint} in the main/real world, or the original worldPoint if conversion fails
	 */
	public static WorldPoint getWorldPointConsideringWorldView(Client client, WorldView worldView, WorldPoint worldPoint)
	{
		if (worldPoint == null)
		{
			return null;
		}

		var localPoint = LocalPoint.fromWorld(worldView, worldPoint);
		if (localPoint == null)
		{
			return worldPoint;
		}

		// If in a non-top level WorldView (a boat) need to translate to main world
		if (!worldView.isTopLevel())
		{
			// Currently the entity should be the player's boat only?
			var worldEntity = client.getTopLevelWorldView()
				.worldEntities()
				.byIndex(worldView.getId());

			if (worldEntity == null)
			{
				return worldPoint;
			}

			var mainLocal = worldEntity.transformToMainWorld(localPoint);
			return WorldPoint.fromLocal(client.getTopLevelWorldView(),
				mainLocal.getX(), mainLocal.getY(), client.getTopLevelWorldView().getPlane());
		}
		else
		{
			// For top-level WorldView, still use fromLocalInstance to handle any instance normalization
			return WorldPoint.fromLocalInstance(client, localPoint);
		}
	}

	public static LocalPoint getLocalPointFromWorldPointInInstance(WorldView wv, WorldPoint worldPoint)
	{
		if (worldPoint == null) return null;
		var instanceWps = WorldPoint.toLocalInstance(wv, worldPoint);
		if (instanceWps.isEmpty()) return null;
		return LocalPoint.fromWorld(wv, instanceWps.iterator().next());
	}

	public static List<LocalPoint> getLocalPointsFromWorldPointInInstance(WorldView wv, WorldPoint worldPoint)
	{
		if (worldPoint == null) return List.of();
		var instanceWps = WorldPoint.toLocalInstance(wv, worldPoint);
		if (instanceWps.isEmpty()) return List.of();

		List<LocalPoint> lps = new ArrayList<>();
		for (WorldPoint instanceWp : instanceWps)
		{
			lps.add(LocalPoint.fromWorld(wv, instanceWp));
		}
		return lps;
	}

	public static Rectangle getWorldMapClipArea(Client client)
	{
		Widget widget = client.getWidget(InterfaceID.Worldmap.MAP_CONTAINER);
		if (widget == null)
		{
			return null;
		}

		return widget.getBounds();
	}

	public static Point mapWorldPointToGraphicsPoint(Client client, WorldPoint worldPoint)
	{
		var worldMap = client.getWorldMap();
		if (worldPoint == null) return null;
		if (!worldMap.getWorldMapData().surfaceContainsPosition(worldPoint.getX(), worldPoint.getY()))
		{
			return null;
		}

		float pixelsPerTile = worldMap.getWorldMapZoom();

		Widget map = client.getWidget(InterfaceID.Worldmap.MAP_CONTAINER);
		if (map != null)
		{
			Rectangle worldMapRect = map.getBounds();

			int widthInTiles = (int) Math.ceil(worldMapRect.getWidth() / pixelsPerTile);
			int heightInTiles = (int) Math.ceil(worldMapRect.getHeight() / pixelsPerTile);

			var worldMapPosition = worldMap.getWorldMapPosition();

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

	public static Point getMinimapPoint(Client client, WorldPoint start, WorldPoint destination)
	{
		var worldMap = client.getWorldMap();
		if (worldMap == null)
		{
			return null;
		}

		var worldMapData = worldMap.getWorldMapData();
		if (worldMapData == null)
		{
			return null;
		}

		if (worldMapData.surfaceContainsPosition(start.getX(), start.getY()) !=
			worldMapData.surfaceContainsPosition(destination.getX(), destination.getY()))
		{
			return null;
		}

		int x = (destination.getX() - start.getX());
		int y = (destination.getY() - start.getY());

		float maxDistance = Math.max(Math.abs(x), Math.abs(y));
		// Avoid division by zero when start == destination
		if (maxDistance == 0)
		{
			return null;
		}

		x = x * 100;
		y = y * 100;
		x /= maxDistance;
		y /= maxDistance;

		Widget minimapDrawWidget;
		if (client.isResized())
		{
			if (client.getVarbitValue(VarbitID.RESIZABLE_STONE_ARRANGEMENT) == 1)
			{
				minimapDrawWidget = client.getWidget(InterfaceID.ToplevelPreEoc.MINIMAP);
			}
			else
			{
				minimapDrawWidget = client.getWidget(InterfaceID.ToplevelOsrsStretch.MINIMAP);
			}
		}
		else
		{
			minimapDrawWidget = client.getWidget(InterfaceID.Toplevel.MINIMAP);
		}

		if (minimapDrawWidget == null)
		{
			return null;
		}

		final int angle = client.getCameraYawTarget() & 0x7FF;

		final int sin = Perspective.SINE[angle];
		final int cos = Perspective.COSINE[angle];

		final int xx = y * sin + cos * x >> 16;
		final int yy = sin * x - y * cos >> 16;

		Point loc = minimapDrawWidget.getCanvasLocation();
		int miniMapX = loc.getX() + xx + minimapDrawWidget.getWidth() / 2;
		int miniMapY = minimapDrawWidget.getHeight() / 2 + loc.getY() + yy;
		return new Point(miniMapX, miniMapY);
	}

	public static Polygon getZonePoly(Client client, Zone zone)
	{
		Polygon areaPoly = new Polygon();
		if (zone == null) return areaPoly;

		for (int x = zone.getMinX(); x < zone.getMaxX(); x++)
		{
			addToPoly(client, areaPoly, new WorldPoint(x, zone.getMaxY(), zone.getMinWorldPoint().getPlane()), NW);
		}

		// NE corner
		addToPoly(client, areaPoly, new WorldPoint(zone.getMaxX(), zone.getMaxY(), zone.getMinWorldPoint().getPlane()), NW, NE, SE);

		// West side
		for (int y = zone.getMaxY() - 1; y > zone.getMinY(); y--)
		{
			addToPoly(client, areaPoly, new WorldPoint(zone.getMaxX(), y, zone.getMinWorldPoint().getPlane()), SE);
		}

		// SE corner
		addToPoly(client, areaPoly, new WorldPoint(zone.getMaxX(), zone.getMinY(), zone.getMinWorldPoint().getPlane()), SE, SW);

		// South side
		for (int x = zone.getMaxX() - 1; x > zone.getMinX(); x--)
		{
			addToPoly(client, areaPoly, new WorldPoint(x, zone.getMinY(), zone.getMinWorldPoint().getPlane()), SW);
		}

		// SW corner
		addToPoly(client, areaPoly, new WorldPoint(zone.getMinX(), zone.getMinY(), zone.getMinWorldPoint().getPlane()), SW, NW);

		for (int y = zone.getMinY() + 1; y < zone.getMaxY(); y++)
		{
			addToPoly(client, areaPoly, new WorldPoint(zone.getMinX(), y, zone.getMinWorldPoint().getPlane()), NW);
		}


		return areaPoly;
	}

	private static void addToPoly(Client client, Polygon areaPoly, WorldPoint wp, int... points)
	{
		LocalPoint localPoint = getLocalPointFromWorldPointInInstance(client.getTopLevelWorldView(), wp);
		if (localPoint == null) return;

		Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
		if (poly != null)
		{
			for (int point : points)
			{
				areaPoly.addPoint(poly.xpoints[point], poly.ypoints[point]);
			}
		}
	}
}
