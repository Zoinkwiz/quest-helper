/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
 * Copyright (c) 2019, Trevor <https://github.com/Trevor159>
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

import com.questhelper.QuestHelperConfig;
import com.questhelper.QuestHelperPlugin;
import static com.questhelper.overlays.QuestHelperWorldOverlay.IMAGE_Z_OFFSET;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.overlay.DirectionArrow;
import com.questhelper.steps.tools.QuestPerspective;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ColorUtil;

public class NpcStep extends DetailedQuestStep
{
	@Inject
	protected Client client;

	private final int npcID;
	private final List<Integer> alternateNpcIDs = new ArrayList<>();

	private boolean allowMultipleHighlights;

	private final ArrayList<NPC> npcs = new ArrayList<>();

	@Setter
	private int maxRoamRange = 48;

	private boolean mustBeFocused = false;

	public NpcStep(QuestHelper questHelper, int npcID, String text, Requirement... requirements)
	{
		super(questHelper, text, requirements);
		this.npcID = npcID;
	}

	public NpcStep(QuestHelper questHelper, int[] npcID, String text, Requirement... requirements)
	{
		super(questHelper, text, requirements);
		this.npcID = npcID[0];
		for (int i = 1; i < npcID.length; i++)
		{
			this.alternateNpcIDs.add(npcID[i]);
		}
	}

	public NpcStep(QuestHelper questHelper, int npcID, WorldPoint worldPoint, String text, Requirement... requirements)
	{
		super(questHelper, worldPoint, text, requirements);
		this.npcID = npcID;
	}

	public NpcStep(QuestHelper questHelper, int npcID, WorldPoint worldPoint, String text, List<Requirement> requirements, List<Requirement> optionalRequirements)
	{
		super(questHelper, worldPoint, text, requirements, optionalRequirements);
		this.npcID = npcID;
	}

	public NpcStep(QuestHelper questHelper, int[] npcID, WorldPoint worldPoint, String text, Requirement... requirements)
	{
		super(questHelper, worldPoint, text, requirements);
		this.npcID = npcID[0];
		for (int i = 1; i < npcID.length; i++)
		{
			this.alternateNpcIDs.add(npcID[i]);
		}
	}

	public NpcStep(QuestHelper questHelper, int npcID, WorldPoint worldPoint, String text, boolean allowMultipleHighlights, Requirement... requirements)
	{
		this(questHelper, npcID, worldPoint, text, requirements);
		this.allowMultipleHighlights = allowMultipleHighlights;
	}

	public NpcStep(QuestHelper questHelper, int npcID, String text, boolean allowMultipleHighlights, Requirement... requirements)
	{
		this(questHelper, npcID, null, text, allowMultipleHighlights, requirements);
	}

	@Override
	public void startUp()
	{
		super.startUp();

		for (NPC npc : client.getNpcs())
		{
			if (npcID == npc.getId() || alternateNpcIDs.contains(npc.getId()))
			{
				WorldPoint npcPoint = WorldPoint.fromLocalInstance(client, npc.getLocalLocation());
				if (this.npcs.size() == 0 && (worldPoint == null || npcPoint.distanceTo(worldPoint) < maxRoamRange))
				{
					this.npcs.add(npc);
				}
				else if (allowMultipleHighlights)
				{
					this.npcs.add(npc);
				}
			}
		}
	}

	public void addAlternateNpcs(Integer... alternateNpcIDs)
	{
		this.alternateNpcIDs.addAll(Arrays.asList(alternateNpcIDs));
	}

	public void addAlternateNpcs(List<Integer> alternateNpcIDs)
	{
		this.alternateNpcIDs.addAll(alternateNpcIDs);
	}

	public List<Integer> allIds()
	{
		List<Integer> ids = new ArrayList<>();
		ids.add(npcID);
		ids.addAll(alternateNpcIDs);
		return ids;
	}

	public void setMustBeFocused(boolean mustBeFocused)
	{
		this.mustBeFocused = mustBeFocused;
		if (mustBeFocused) allowMultipleHighlights = true;
	}

	@Override
	public void shutDown()
	{
		super.shutDown();
		npcs.clear();
	}

	@Subscribe
	@Override
	public void onGameStateChanged(GameStateChanged event)
	{
		super.onGameStateChanged(event);
		if (event.getGameState() == GameState.HOPPING)
		{
			npcs.clear();
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		if (event.getNpc().getId() == npcID || alternateNpcIDs.contains(event.getNpc().getId()))
		{
			WorldPoint npcPoint = WorldPoint.fromLocalInstance(client, event.getNpc().getLocalLocation());
			if (npcs.size() == 0)
			{
				if (worldPoint == null)
				{
					npcs.add(event.getNpc());
				}
				else if (npcPoint.distanceTo(worldPoint) < maxRoamRange)
				{
					npcs.add(event.getNpc());
				}
			}
			else if (allowMultipleHighlights)
			{
				if (worldPoint == null || npcPoint.distanceTo(worldPoint) < maxRoamRange)
				{
					npcs.add(event.getNpc());
				}
			}
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		npcs.remove(event.getNpc());
	}

	@Subscribe
	public void onNpcChanged(NpcChanged npcChanged)
	{
		int newNpcId = npcChanged.getNpc().getId();
		npcs.remove(npcChanged.getNpc());

		if (allIds().contains(newNpcId) && npcChanged.getNpc().getComposition().isVisible())
		{
			if (npcs.size() == 0 || allowMultipleHighlights)
			{
				npcs.add(npcChanged.getNpc());
			}
		}
	}

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		npcs.removeIf(npc -> npc.getId() == -1);

		super.makeWorldOverlayHint(graphics, plugin);

		if (worldPoint != null)
		{
			Collection<WorldPoint> localWorldPoints = QuestPerspective.toLocalInstance(client, worldPoint);
			if (localWorldPoints.isEmpty())
			{
				return;
			}
		}

		Color configColor = getQuestHelper().getConfig().targetOverlayColor();

		for (NPC npc : npcs)
		{
			if (mustBeFocused && npc.getInteracting() != client.getLocalPlayer()) continue;
			highlightNpc(npc, configColor, graphics);

			if (questHelper.getConfig().showSymbolOverlay())
			{
				int zOffset = questHelper.getConfig().highlightStyleNpcs() == QuestHelperConfig.NpcHighlightStyle.TILE
					? IMAGE_Z_OFFSET
					: (npc.getLogicalHeight() / 2);

				Point imageLocation = npc.getCanvasImageLocation(icon, zOffset);
				if (imageLocation != null)
				{
					OverlayUtil.renderImageLocation(graphics, imageLocation, icon);
				}
			}
		}
	}

	private void highlightNpc(NPC npc, Color color, Graphics2D graphics)
	{
		switch (questHelper.getConfig().highlightStyleNpcs())
		{
			case CONVEX_HULL:
				OverlayUtil.renderHoverableArea(
					graphics,
					npc.getConvexHull(),
					client.getMouseCanvasPosition(),
					ColorUtil.colorWithAlpha(color, 20),
					questHelper.getConfig().targetOverlayColor().darker(),
					questHelper.getConfig().targetOverlayColor());
				break;
			case OUTLINE:
				modelOutlineRenderer.drawOutline(
					npc,
					questHelper.getConfig().outlineThickness(),
					color,
					questHelper.getConfig().outlineFeathering()
				);
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

	@Override
	public void renderArrow(Graphics2D graphics)
	{
		if (questHelper.getConfig().showMiniMapArrow())
		{
			if (npcs.size() == 0)
			{
				super.renderArrow(graphics);
			}
			else if (!hideWorldArrow && !npcs.contains(client.getHintArrowNpc()))
			{
				Shape hull = npcs.get(0).getConvexHull();
				if (hull != null)
				{
					Rectangle rect = hull.getBounds();
					int x = (int) rect.getCenterX();
					int y = (int) rect.getMinY() - ARROW_SHIFT_Y;

					DirectionArrow.drawWorldArrow(graphics, getQuestHelper().getConfig().targetOverlayColor(), x, y);
				}
			}
		}
	}

	@Override
	public void renderMinimapArrow(Graphics2D graphics)
	{
		if (questHelper.getConfig().showMiniMapArrow())
		{
			if (npcs.contains(client.getHintArrowNpc()))
			{
				return;
			}

			if (!npcs.isEmpty() && npcs.get(0).getMinimapLocation() != null)
			{
				int x = npcs.get(0).getMinimapLocation().getX();
				int y = npcs.get(0).getMinimapLocation().getY();
				Line2D.Double line = new Line2D.Double(x, y - 18, x, y - 8);

				DirectionArrow.drawMinimapArrow(graphics, line, getQuestHelper().getConfig().targetOverlayColor());
				return;
			}

			super.renderMinimapArrow(graphics);
		}
	}
}
