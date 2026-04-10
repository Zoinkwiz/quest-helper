package com.questhelper.tools;

import com.questhelper.util.worldmap.WorldPointMapper;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;

import net.runelite.api.Point;
import java.awt.image.BufferedImage;

public class ConstructWorldMapPoint extends WorldMapPoint
{
	public ConstructWorldMapPoint(WorldPoint worldPoint, BufferedImage image, String label)
	{
		super(WorldPointMapper.getMapWorldPointFromRealWorldPoint(worldPoint).getWorldPoint(), null);
		setName(label);
		setSnapToEdge(false);
		setJumpOnClick(false);
		setImage(image);
		setImagePoint(new Point(image.getWidth() / 2, image.getHeight() / 2));
	}
}
