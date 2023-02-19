package com.questhelper;

import java.awt.Shape;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.Model;
import net.runelite.api.Renderable;
import net.runelite.api.TileObject;
import net.runelite.api.WallObject;

public class VisibilityHelper
{
	private final Map<Integer, Boolean> visibilityCache = new HashMap<>();

	public boolean isObjectVisible(TileObject tileObject)
	{
		if (!visibilityCache.containsKey(tileObject.getId()))
		{
			visibilityCache.put(tileObject.getId(), isObjectVisibleChecker(tileObject));
		}
		return visibilityCache.get(tileObject.getId());
	}


	public Shape getObjectHull(TileObject tileObject)
	{
		if (tileObject instanceof GameObject)
		{
			return ((GameObject) tileObject).getConvexHull();
		}
		else if (tileObject instanceof GroundObject)
		{
			return ((GroundObject) tileObject).getConvexHull();
		}
		else if (tileObject instanceof DecorativeObject)
		{
			return ((DecorativeObject) tileObject).getConvexHull();
		}
		else if (tileObject instanceof WallObject)
		{
			return ((WallObject) tileObject).getConvexHull();
		}
		return null;
	}

	private boolean isObjectVisibleChecker(TileObject tileObject)
	{
		if (tileObject instanceof GameObject)
		{
			Model model = extractModel(((GameObject) tileObject).getRenderable());
			return modelHasVisibleTriangles(model);
		}
		else if (tileObject instanceof GroundObject)
		{
			Model model = extractModel(((GroundObject) tileObject).getRenderable());
			return modelHasVisibleTriangles(model);
		}
		else if (tileObject instanceof DecorativeObject)
		{
			DecorativeObject decoObj = ((DecorativeObject) tileObject);
			Model model1 = extractModel(decoObj.getRenderable());
			Model model2 = extractModel(decoObj.getRenderable2());
			return modelHasVisibleTriangles(model1) || modelHasVisibleTriangles(model2);
		}
		else if (tileObject instanceof WallObject)
		{
			WallObject wallObj = ((WallObject) tileObject);
			Model model1 = extractModel(wallObj.getRenderable1());
			Model model2 = extractModel(wallObj.getRenderable2());
			return modelHasVisibleTriangles(model1) || modelHasVisibleTriangles(model2);
		}
		return false;
	}

	private Model extractModel(Renderable renderable)
	{
		if (renderable == null)
		{
			return null;
		}
		return renderable instanceof Model ? (Model) renderable : renderable.getModel();
	}

	private boolean modelHasVisibleTriangles(Model model)
	{
		if (model == null)
		{
			return false;
		}
		byte[] triangleTransparencies = model.getFaceTransparencies();
		int triangleCount = model.getFaceCount();
		if (triangleTransparencies == null)
		{
			return true;
		}
		for (int i = 0; i < triangleCount; i++)
		{
			if ((triangleTransparencies[i] & 255) < 254)
			{
				return true;
			}
		}
		return false;
	}
}
