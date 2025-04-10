/*
 * Copyright (c) 2023, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.steps.playermadesteps;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.runeliteobjects.extendedruneliteobjects.ExtendedRuneliteObject;
import com.questhelper.runeliteobjects.extendedruneliteobjects.RuneliteObjectManager;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.overlay.DirectionArrow;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// TODO: Separate out NPC logic from Step logic
public class RuneliteObjectStep extends DetailedQuestStep
{
	@Inject
	private RuneliteObjectManager runeliteObjectManager;

	private final ExtendedRuneliteObject extendedRuneliteObject;

	// TODO: Maybe a list of npcs to 'delete' with this?
	List<String> npcsGroupsToDelete = new ArrayList<>();
	HashMap<String, ExtendedRuneliteObject> npcsToDelete = new HashMap<>();

	public RuneliteObjectStep(QuestHelper questHelper, ExtendedRuneliteObject extendedRuneliteObject, String text, Requirement... requirements)
	{
		super(questHelper, extendedRuneliteObject.getWorldPoint(), text, requirements);
		this.extendedRuneliteObject = extendedRuneliteObject;
	}

	@Override
	public void startUp()
	{
		super.startUp();
	}

	@Override
	public void shutDown()
	{
		super.shutDown();
		// Delete all fake npcs associated
		clientThread.invokeLater(this::removeRuneliteNpcs);
	}

	private void removeRuneliteNpcs()
	{
		runeliteObjectManager.removeGroupAndSubgroups(this.toString());
	}

	@Override
	public void renderArrow(Graphics2D graphics)
	{
		if (!extendedRuneliteObject.getWorldPoint().isInScene(client)) return;
		if (questHelper.getConfig().showMiniMapArrow())
		{
			if (!extendedRuneliteObject.isRuneliteObjectActive())
			{
				super.renderArrow(graphics);
			}
			else if (!hideWorldArrow)
			{
				Point p = Perspective.localToCanvas(client, extendedRuneliteObject.getRuneliteObject().getLocation(), client.getPlane(),
					extendedRuneliteObject.getRuneliteObject().getModel().getModelHeight());
				if (p != null)
				{
					DirectionArrow.drawWorldArrow(graphics, getQuestHelper().getConfig().targetOverlayColor(), p.getX(), p.getY() - ARROW_SHIFT_Y);
				}
			}
		}
	}

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWorldOverlayHint(graphics, plugin);

		Color configColor = getQuestHelper().getConfig().targetOverlayColor();
		highlightNpc(configColor, graphics);
	}

	private void highlightNpc(Color color, Graphics2D graphics)
	{
		if (!extendedRuneliteObject.getWorldPoint().isInScene(client)) return;
		switch (questHelper.getConfig().highlightStyleNpcs())
		{
			case CONVEX_HULL:
			case OUTLINE:
				if (!extendedRuneliteObject.getRuneliteObject().isActive()) break;
				modelOutlineRenderer.drawOutline(
					extendedRuneliteObject.getRuneliteObject(),
					questHelper.getConfig().outlineThickness(),
					color,
					questHelper.getConfig().outlineFeathering()
				);
				break;
			case TILE:
				Polygon poly = Perspective.getCanvasTilePoly(client, extendedRuneliteObject.getRuneliteObject().getLocation());
				if (poly != null)
				{
					OverlayUtil.renderPolygon(graphics, poly, color);
				}
				break;
			default:
		}
	}
}
