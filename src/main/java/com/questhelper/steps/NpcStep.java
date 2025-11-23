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
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.overlay.DirectionArrow;
import com.questhelper.steps.tools.QuestPerspective;
import lombok.Setter;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static com.questhelper.overlays.QuestHelperWorldOverlay.IMAGE_Z_OFFSET;

public class NpcStep extends DetailedQuestStep
{
	@Inject
	protected Client client;

	protected final int npcID;
	protected final List<Integer> alternateNpcIDs = new ArrayList<>();

	@Setter
	protected boolean allowMultipleHighlights;

	protected final ArrayList<NPC> npcs = new ArrayList<>();

	@Setter
	protected int maxRoamRange = 48;

	protected boolean mustBeFocusedOnPlayer = false;

	protected List<Integer> mustBeFocusedOnNpcs = new ArrayList<>();

	@Setter
	protected String npcName;

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

	public NpcStep(QuestHelper questHelper, int[] npcID, WorldPoint worldPoint, String text, boolean allowMultipleHighlights, Requirement... requirements)
	{
		this(questHelper, npcID, worldPoint, text, requirements);
		this.allowMultipleHighlights = allowMultipleHighlights;
	}

	public NpcStep(QuestHelper questHelper, int npcID, WorldPoint worldPoint, String text, Requirement... requirements)
	{
		super(questHelper, worldPoint, text, requirements);
		this.npcID = npcID;
	}

	public NpcStep(QuestHelper questHelper, int npcID, String npcName, WorldPoint worldPoint, String text, Requirement... requirements)
	{
		super(questHelper, worldPoint, text, requirements);
		this.npcID = npcID;
		this.npcName = npcName;
	}

	public NpcStep(QuestHelper questHelper, int npcID, String npcName, WorldPoint worldPoint, String text, boolean allowMultipleHighlights, Requirement... requirements)
	{
		super(questHelper, worldPoint, text, requirements);
		this.npcID = npcID;
		this.npcName = npcName;
		this.allowMultipleHighlights = allowMultipleHighlights;
	}

	public NpcStep(QuestHelper questHelper, int npcID, WorldPoint worldPoint, String text, List<Requirement> requirements, List<Requirement> optionalRequirements)
	{
		super(questHelper, worldPoint, text, requirements, optionalRequirements);
		this.npcID = npcID;
	}

	public NpcStep(QuestHelper questHelper, int npcID, WorldPoint worldPoint, String text, boolean allowMultipleHighlights, List<Requirement> requirements, List<Requirement> optionalRequirements)
	{
		super(questHelper, worldPoint, text, requirements, optionalRequirements);
		this.npcID = npcID;
		this.allowMultipleHighlights = allowMultipleHighlights;
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

	public NpcStep copy()
	{
		NpcStep newStep = new NpcStep(getQuestHelper(), npcID, worldPoint, null, requirements, recommended);
		if (text != null)
		{
			newStep.setText(text);
		}
		newStep.allowMultipleHighlights = allowMultipleHighlights;
		newStep.addAlternateNpcs(alternateNpcIDs);
		if (mustBeFocusedOnPlayer)
		{
			newStep.setMustBeFocusedOnPlayer(true);
		}
		newStep.setMaxRoamRange(maxRoamRange);

		return newStep;
	}

	protected boolean npcPassesChecks(NPC npc)
	{
		if (npcName != null && (npc.getName() == null || !npc.getName().equals(npcName))) return false;
		return npcID == npc.getId() ||  npcID == npc.getComposition().getId() || alternateNpcIDs.contains(npc.getId()) || alternateNpcIDs.contains(npc.getComposition().getId());
	}

	@Override
	public void startUp()
	{
		super.startUp();

		scanForNpcs();
	}

	public void scanForNpcs()
	{
		for (NPC npc : client.getTopLevelWorldView().npcs())
		{
			addNpcToListGivenMatchingID(npc, this::npcPassesChecks, npcs);
		}
	}

	public NpcStep addAlternateNpcs(Integer... alternateNpcIDs)
	{
		this.alternateNpcIDs.addAll(Arrays.asList(alternateNpcIDs));

		return this;
	}

	public NpcStep addAlternateNpcs(List<Integer> alternateNpcIDs)
	{
		this.alternateNpcIDs.addAll(alternateNpcIDs);

		return this;
	}

	public void setMustBeFocusedOnNpcs(Integer... ids)
	{
		mustBeFocusedOnNpcs.clear();
		mustBeFocusedOnNpcs.addAll(List.of(ids));
	}

	public List<Integer> allIds()
	{
		List<Integer> ids = new ArrayList<>();
		ids.add(npcID);
		ids.addAll(alternateNpcIDs);
		return ids;
	}

	public void setMustBeFocusedOnPlayer(boolean mustBeFocusedOnPlayer)
	{
		this.mustBeFocusedOnPlayer = mustBeFocusedOnPlayer;
		if (mustBeFocusedOnPlayer) allowMultipleHighlights = true;
	}

	@Override
	public void shutDown()
	{
		super.shutDown();
		npcs.clear();
	}

	@Override
	public void onGameStateChanged(GameStateChanged event)
	{
		super.onGameStateChanged(event);
		if (event.getGameState() == GameState.HOPPING)
		{
			npcs.clear();
		}
		else if (event.getGameState() == GameState.LOGGED_IN)
		{
			scanForNpcs();
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		addNpcToListGivenMatchingID(event.getNpc(), this::npcPassesChecks, npcs);
	}

	public void addNpcToListGivenMatchingID(NPC npc, Function<NPC, Boolean> condition, List<NPC> list)
	{
		if (condition.apply(npc))
		{
			WorldPoint npcPoint = WorldPoint.fromLocalInstance(client, npc.getLocalLocation());
			if (npcs.size() == 0)
			{
				if (worldPoint == null)
				{
					list.add(npc);
				}
				else if (npcPoint.distanceTo(worldPoint) < maxRoamRange)
				{
					list.add(npc);
				}
			}
			else if (allowMultipleHighlights)
			{
				if (worldPoint == null || npcPoint.distanceTo(worldPoint) < maxRoamRange)
				{
					list.add(npc);
				}
			}
		}
	};

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

		// This used to contain isVisible check as well, but it doesn't seem to be accurate for a lot
		// This MAY for some NPCs which have alternate version (The Kendal) require re-consideration
		if (allIds().contains(newNpcId))
		{
			if (npcs.isEmpty() || allowMultipleHighlights)
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
			WorldPoint localWorldPoint = QuestPerspective.getWorldPointConsideringWorldView(client, client.getTopLevelWorldView(), worldPoint);
			if (localWorldPoint == null) return;
		}

		Color configColor = getQuestHelper().getConfig().targetOverlayColor();

		for (NPC npc : npcs)
		{
			if (!passesFocusChecks(npc)) continue;
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

	private boolean passesFocusChecks(NPC npc)
	{
		Actor actor = npc.getInteracting();
		boolean passesPlayerCheck = !mustBeFocusedOnPlayer || actor == client.getLocalPlayer();
		if (passesPlayerCheck) return true;

		if (!(actor instanceof NPC)) return false;
		NPC focusedNpc = (NPC) actor;
		return mustBeFocusedOnNpcs.isEmpty() || mustBeFocusedOnNpcs.contains(focusedNpc.getId());
	}

	@Override
	protected void renderTileIcon(Graphics2D graphics)
	{
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
