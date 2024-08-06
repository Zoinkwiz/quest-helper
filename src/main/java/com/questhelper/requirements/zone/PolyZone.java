package com.questhelper.requirements.zone;

import net.runelite.api.coords.WorldPoint;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class PolyZone extends Zone
{
	List<Point2D.Double> perimeter = new ArrayList<>();

	private int minPlane = 8;
	private int maxPlane = 0;

	int minX = Integer.MAX_VALUE;
	int minY = Integer.MAX_VALUE;

	//The first plane of the "Overworld"
	public PolyZone(List<WorldPoint> permiter)
	{
		// Check minimum z, check for max z
		for (WorldPoint p : permiter)
		{
			Point2D.Double pos = new Point2D.Double();
			pos.x = p.getX();
			pos.y = p.getY();
			this.perimeter.add(pos);

			if (p.getPlane() > maxPlane)
			{
				maxPlane = p.getPlane();
			}
			else if (p.getPlane() < minPlane)
			{
				minPlane = p.getPlane();
			}

			if (p.getX() < minX)
			{
				minX = p.getX();
			}
			if (p.getY() < minY)
			{
				minY = p.getY();
			}
		}
	}

	public boolean contains(WorldPoint worldPoint)
	{
		Path2D.Double path = new Path2D.Double();
		Point2D.Double firstVertex = perimeter.get(0);
		path.moveTo(firstVertex.x, firstVertex.y);

		for (int i = 1; i < perimeter.size(); i++)
		{
			Point2D.Double vertex = perimeter.get(i);
			path.lineTo(vertex.x, vertex.y);
		}

		path.closePath();
		return path.contains(worldPoint.getX(), worldPoint.getY());
	}

	public WorldPoint getMinWorldPoint()
	{

		return new WorldPoint(minX, minY, minPlane);
	}
}
