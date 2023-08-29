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
package com.questhelper.steps.playermadesteps.extendedruneliteobjects;

import com.questhelper.steps.WidgetDetails;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.callback.ClientThread;

public class ReplacedNpc extends FakeNpc
{
	@Getter
	@Setter
	private NPC npc;

	@Getter
	@Setter
	private int npcIDToReplace;

	@Getter
	private final ArrayList<MenuEntryWrapper> entries = new ArrayList<>();

	@Getter
	private final List<WidgetReplacement> widgetReplacements = new ArrayList<>();

	protected ReplacedNpc(Client client, ClientThread clientThread, WorldPoint worldPoint, int[] model, int npcIDToReplace)
	{
		super(client, clientThread, worldPoint, model, 808);
		this.npcIDToReplace = npcIDToReplace;
//		disable();
	}

	public void updateNpcSync(Client client)
	{
		if (npc.getAnimation() != -1)
		{
			setAnimation(npc.getAnimation());
		}
		else if (npc.getLocalLocation().distanceTo(getRuneliteObject().getLocation()) == 0)
		{
				setAnimation(npc.getIdlePoseAnimation());
		}
		else
		{
			setAnimation(npc.getWalkAnimation());
		}
		getRuneliteObject().setLocation(npc.getLocalLocation(), client.getPlane());
		setOrientationGoal(npc.getOrientation());
		if (!isActive())
		{
			activate();
		}
	}

	public void addWidgetReplacement(WidgetReplacement widgetReplacement)
	{
		widgetReplacements.add(widgetReplacement);
	}

	public void addMenuEntry(MenuEntryWrapper menuEntry)
	{
		entries.add(menuEntry);
	}

	// This changes the clickbox to be the original NPC's clickbox to avoid any possible advantage is interacting
	public Shape getClickbox()
	{
		if (npc == null) return null;
		return Perspective.getClickbox(client, npc.getModel(), npc.getOrientation(), npc.getLocalLocation().getX(), npc.getLocalLocation().getY(),
			Perspective.getTileHeight(client, npc.getLocalLocation(), getWorldPoint().getPlane()));
	}
}
