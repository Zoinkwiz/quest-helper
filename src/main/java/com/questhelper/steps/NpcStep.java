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

import com.questhelper.requirements.Requirement;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javax.inject.Inject;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.QuestHelperPlugin;
import static com.questhelper.QuestHelperWorldOverlay.IMAGE_Z_OFFSET;
import net.runelite.client.ui.overlay.OverlayUtil;

public class NpcStep extends DetailedQuestStep
{
	@Inject
	protected Client client;

	private final int npcID;
	private final ArrayList<Integer> alternateNpcIDs = new ArrayList<>();

	private boolean allowMultipleHighlights;

	private NPC npc;
	private ArrayList<NPC> otherNpcs = new ArrayList<>();

	protected BufferedImage npcIcon;

	@Setter
	private int maxRoamRange = 48;

	public NpcStep(QuestHelper questHelper, int npcID, String text, Requirement... requirements)
	{
		super(questHelper, text, requirements);
		this.npcID = npcID;
	}

	public NpcStep(QuestHelper questHelper, int npcID, WorldPoint worldPoint, String text, Requirement... requirements)
	{
		super(questHelper, worldPoint, text, requirements);
		this.npcID = npcID;
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
				if (this.npc == null && (worldPoint == null || npcPoint.distanceTo(worldPoint) < maxRoamRange))
				{
					this.npc = npc;
				}
				else if (allowMultipleHighlights)
				{
					this.otherNpcs.add(npc);
				}
			}
		}
	}

	public void addAlternateNpcs(Integer... alternateNpcIDs)
	{
		this.alternateNpcIDs.addAll(Arrays.asList(alternateNpcIDs));
	}

	@Override
	public void shutDown()
	{
		super.shutDown();
		npc = null;
		otherNpcs = new ArrayList<>();
	}

	@Subscribe
	@Override
	public void onGameStateChanged(GameStateChanged event)
	{
		super.onGameStateChanged(event);
		if (event.getGameState() == GameState.HOPPING)
		{
			npc = null;
			otherNpcs.clear();
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		if (event.getNpc().getId() == npcID || alternateNpcIDs.contains(event.getNpc().getId()))
		{
			WorldPoint npcPoint = WorldPoint.fromLocalInstance(client, event.getNpc().getLocalLocation());
			if (npc == null)
			{
				if (worldPoint == null)
				{
					npc = event.getNpc();
				}
				else if (npcPoint.distanceTo(worldPoint) < maxRoamRange)
				{
					npc = event.getNpc();
				}
			}
			else if (allowMultipleHighlights)
			{
				if (worldPoint == null)
				{
					npc = event.getNpc();
				}
				else if (npcPoint.distanceTo(worldPoint) < maxRoamRange)
				{
					otherNpcs.add(event.getNpc());
				}
			}
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (event.getNpc().equals(npc))
		{
			npc = null;
			if (allowMultipleHighlights && !otherNpcs.isEmpty())
			{
				npc = otherNpcs.get(0);
				otherNpcs.remove(0);
			}
		}

		otherNpcs.remove(event.getNpc());
	}

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWorldOverlayHint(graphics, plugin);

		if (inCutscene)
		{
			return;
		}

		if (worldPoint != null)
		{
			Collection<WorldPoint> localWorldPoints = toLocalInstance(client, worldPoint);
			if (localWorldPoints.isEmpty())
			{
				return;
			}
		}

		if (npcIcon == null)
		{
			npcIcon = getQuestImage();
		}

		if (iconItemID != -1 && icon == null)
		{
			addIconImage();
			npcIcon = icon;
		}

		for (NPC otherNpc : otherNpcs)
		{
			OverlayUtil.renderActorOverlayImage(graphics, otherNpc, npcIcon, Color.CYAN, IMAGE_Z_OFFSET);
		}

		if (npc == null)
		{
			return;
		}

		OverlayUtil.renderActorOverlayImage(graphics, npc, npcIcon, Color.CYAN, IMAGE_Z_OFFSET);
	}

	@Override
	public void renderArrow(Graphics2D graphics)
	{
		if (npc == null)
		{
			super.renderArrow(graphics);
		}
		else
		{
			BufferedImage arrow = getArrow();
			Shape hull = npc.getConvexHull();
			if (hull != null)
			{
				Rectangle rect = hull.getBounds();
				int x = (int) rect.getCenterX() - ARROW_SHIFT_X;
				int y = (int) rect.getMinY() - ARROW_SHIFT_Y;
				Point point = new Point(x, y);

				OverlayUtil.renderImageLocation(graphics, point, arrow);
			}
		}
	}

	@Override
	public void renderMinimapArrow(Graphics2D graphics)
	{
		Player player = client.getLocalPlayer();

		if (player == null)
		{
			return;
		}

		WorldPoint playerLocation = player.getWorldLocation();

		if (npc != null && npc.getMinimapLocation() != null)
		{
			graphics.drawImage(getSmallArrow(), npc.getMinimapLocation().getX() - 5, npc.getMinimapLocation().getY() - 14, null);
			return;
		}

		if (worldPoint == null)
		{
			return;
		}

		if (worldPoint.distanceTo(playerLocation) >= MAX_DRAW_DISTANCE)
		{
			createMinimapDirectionArrow(graphics);
			return;
		}

		LocalPoint lp = LocalPoint.fromWorld(client, worldPoint);
		if (lp == null)
		{
			return;
		}

		Point posOnMinimap = Perspective.localToMinimap(client, lp);
		if (posOnMinimap == null)
		{
			return;
		}

		graphics.drawImage(getSmallArrow(), posOnMinimap.getX() - 5, posOnMinimap.getY() - 14, null);
	}
}
