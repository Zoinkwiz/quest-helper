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
package com.questhelper.helpers.quests.eadgarsruse;

import com.questhelper.QuestHelperConfig;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.DetailedQuestStep;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ColorUtil;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/** Marks a tile which is safe to stand on, and highlights the guards which can catch the run to it. */
public class SafeSpotStep extends DetailedQuestStep
{
	private static final int SAFE_SPOT_FILL_ALPHA = 60;

	private static final int GUARD_HULL_FILL_ALPHA = 20;

	private static final Stroke SAFE_SPOT_STROKE = new BasicStroke(2);

	private final Set<Integer> guardIds;

	public SafeSpotStep(QuestHelper questHelper, WorldPoint worldPoint, String text, int[] guardIds,
						Requirement... requirements)
	{
		super(questHelper, worldPoint, text, requirements);
		this.guardIds = Arrays.stream(guardIds).boxed().collect(Collectors.toSet());
	}

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWorldOverlayHint(graphics, plugin);

		if (client.getLocalPlayer() == null)
		{
			return;
		}

		renderSafeSpot(graphics);
		renderGuards(graphics);
	}

	private void renderSafeSpot(Graphics2D graphics)
	{
		if (definedPoint == null)
		{
			return;
		}

		Color color = questHelper.getConfig().targetOverlayColor();

		for (LocalPoint localPoint : definedPoint.resolveLocalPoints(client))
		{
			Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
			if (poly == null)
			{
				continue;
			}

			Stroke originalStroke = graphics.getStroke();
			graphics.setColor(ColorUtil.colorWithAlpha(color, SAFE_SPOT_FILL_ALPHA));
			graphics.fill(poly);
			graphics.setColor(color);
			graphics.setStroke(SAFE_SPOT_STROKE);
			graphics.draw(poly);
			graphics.setStroke(originalStroke);
		}
	}

	private void renderGuards(Graphics2D graphics)
	{
		if (guardIds.isEmpty())
		{
			return;
		}

		QuestHelperConfig config = questHelper.getConfig();
		Color color = config.failColour();

		for (NPC npc : client.getTopLevelWorldView().npcs())
		{
			if (!guardIds.contains(npc.getId()))
			{
				continue;
			}

			switch (config.highlightStyleNpcs())
			{
				case CONVEX_HULL:
					OverlayUtil.renderHoverableArea(
						graphics,
						npc.getConvexHull(),
						client.getMouseCanvasPosition(),
						ColorUtil.colorWithAlpha(color, GUARD_HULL_FILL_ALPHA),
						color.darker(),
						color);
					break;
				case OUTLINE:
					modelOutlineRenderer.drawOutline(
						npc,
						config.outlineThickness(),
						color,
						config.outlineFeathering());
					break;
				case TILE:
					Polygon poly = npc.getCanvasTilePoly();
					if (poly != null)
					{
						OverlayUtil.renderPolygon(graphics, poly, color);
					}
					break;
				default:
			}
		}
	}
}
