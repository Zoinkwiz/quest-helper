package com.questhelper.maker.construct;

import com.questhelper.tools.ConstructWorldMapPoint;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;

/**
 * World map markers for the maker route preview.
 */
public final class MakerPreviewRuntime
{
	private MakerPreviewRuntime()
	{
	}

	public static void clearConstructWorldMapPoints(WorldMapPointManager worldMapPointManager)
	{
		if (worldMapPointManager != null)
		{
			worldMapPointManager.removeIf(ConstructWorldMapPoint.class::isInstance);
		}
	}
}
