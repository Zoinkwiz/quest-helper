/*
 * Copyright (c) 2026, Zoinkwiz <https://github.com/Zoinkwiz>
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

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.overlay.DirectionArrow;
import com.questhelper.steps.tools.DefinedPoint;
import com.questhelper.steps.tools.QuestPerspective;
import net.runelite.api.WorldEntity;
import net.runelite.api.WorldEntityConfig;
import net.runelite.api.WorldView;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.NpcID;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ColorUtil;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Arrays;

public class WorldEntityStep extends DetailedQuestStep
{
	public final int worldEntityConfigID;

	// Used to know where to put the hint arrow
	private Shape lastFrameStructure;

	public WorldEntityStep(QuestHelper questHelper, int worldEntityConfigID, DefinedPoint definedPoint, String text, Requirement... requirements)
	{
		super(questHelper, definedPoint, text, requirements);
		this.worldEntityConfigID = worldEntityConfigID;
	}

	public WorldEntityStep(QuestHelper questHelper, int worldEntityConfigID, WorldPoint worldPoint, String text, Requirement... requirements)
	{
		this(questHelper, worldEntityConfigID, DefinedPoint.of(worldPoint), text, requirements);
	}

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWorldOverlayHint(graphics, plugin);

		lastFrameStructure = null;

		WorldEntity worldEntity = findWorldEntity();
		if (worldEntity == null) return;

		// Currently fine as only one boat per world view. Perhaps in the future an issue where multiple things exist in a world view?
		Shape structure = QuestPerspective.getAreaFromWorldView(client, worldEntity.getWorldView());
		if (structure == null) return;

		lastFrameStructure = structure;

		Color configColor = getQuestHelper().getConfig().targetOverlayColor();
		OverlayUtil.renderHoverableArea(
			graphics,
			structure,
			client.getMouseCanvasPosition(),
			ColorUtil.colorWithAlpha(configColor, 20),
			configColor.darker(),
			configColor
		);
	}


	@Override
	public void renderArrow(Graphics2D graphics)
	{
		if (hideWorldArrow || !questHelper.getConfig().showMiniMapArrow())
		{
			return;
		}

		Shape structure = lastFrameStructure;
		if (structure == null)
		{
			super.renderArrow(graphics);
			return;
		}

		Rectangle bounds = structure.getBounds();
		if (bounds.width <= 0 || bounds.height <= 0)
		{
			super.renderArrow(graphics);
			return;
		}

		int tipX = bounds.x + bounds.width / 2;
		int tipY = bounds.y;
		DirectionArrow.drawWorldArrow(graphics, getQuestHelper().getConfig().targetOverlayColor(), tipX, tipY);
	}


	private WorldEntity findWorldEntity()
	{
		WorldEntity worldEntity = findWorldEntityInWorldView(client.getTopLevelWorldView(), worldEntityConfigID);
		if (worldEntity != null)
		{
			return worldEntity;
		}

		for (WorldView worldView : client.getTopLevelWorldView().worldViews())
		{
			worldEntity = findWorldEntityInWorldView(worldView, worldEntityConfigID);
			if (worldEntity != null)
			{
				return worldEntity;
			}
		}

		return null;
	}

	public static WorldEntity findWorldEntityInWorldView(WorldView worldView, int configId)
	{
		if (worldView == null)
		{
			return null;
		}

		for (WorldEntity worldEntity : worldView.worldEntities())
		{
			WorldEntityConfig config = worldEntity.getConfig();
			if (config != null && config.getId() == configId)
			{
				return worldEntity;
			}
		}

		return null;
	}
}
