/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.requirements;

import com.google.common.base.CaseFormat;
import com.questhelper.NpcCollections;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Prayer;
import net.runelite.client.ui.overlay.components.LineComponent;

public class FollowerRequirement extends Requirement
{
	ArrayList<Integer> followers;
	String text;

	public FollowerRequirement(String text, Integer... followers)
	{
		this.text = text;
		this.followers = new ArrayList<>();
		Collections.addAll(this.followers, followers);
	}

	public FollowerRequirement(String text, ArrayList<Integer> followers)
	{
		this.text = text;
		this.followers = followers;
	}

	@Override
	public boolean check(Client client)
	{
		for (NPC npc : client.getNpcs())
		{
			Actor ta = npc.getInteracting();
			if (ta != null && client.getLocalPlayer() == ta)
			{
				if (followers.contains(npc.getId()))
				{
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public ArrayList<LineComponent> getDisplayText(Client client)
	{
		ArrayList<LineComponent> lines = new ArrayList<>();

		Color color = Color.RED;
		if (check(client))
		{
			color = Color.GREEN;
		}

		lines.add(LineComponent.builder()
			.left(text)
			.leftColor(color)
			.build());

		return lines;
	}
}
