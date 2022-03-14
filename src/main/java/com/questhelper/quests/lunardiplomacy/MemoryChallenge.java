/*
 * Copyright (c) 2021, Zoinkwiz
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
package com.questhelper.quests.lunardiplomacy;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.tools.QuestPerspective;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;

public class MemoryChallenge extends DetailedQuestStep
{
	List<WorldPoint> wps = new ArrayList<>();
	List<WorldPoint> currentPath = new ArrayList<>();
	int column1, column2, column3, column4, lastPos;

	public MemoryChallenge(QuestHelper questHelper)
	{
		super(questHelper, "Work out the route across the cloud tiles through trial and error.");
	}

	@Override
	public void startUp()
	{
		super.startUp();
		setupPaths();
	}

	@Subscribe
	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		super.onVarbitChanged(varbitChanged);
		setupPaths();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (wps.size() == 0)
		{
			return;
		}

		// If start of path, check first point in legit path
		if (currentPath.size() == 0)
		{
			checkNextTile(0);
			return;
		}

		// If started path, check furthest we've reached in path
		WorldPoint lastPoint = currentPath.get(currentPath.size() - 1);

		WorldPoint instanceWp = QuestPerspective.getInstanceWorldPoint(client, wps.get(lastPos));
		if (instanceWp == null)
		{
			return;
		}

		if (instanceWp.distanceTo(lastPoint) == 0 && wps.get(lastPos + 1) != null)
		{
			checkNextTile(lastPos + 1);
		}
	}

	public void checkNextTile(int wpsPos)
	{
		WorldPoint instanceWp = QuestPerspective.getInstanceWorldPoint(client, wps.get(wpsPos));
		if (instanceWp == null)
		{
			return;
		}

		// If on same tiles as wpsPos
		if (client.getLocalPlayer() != null &&
			client.getLocalPlayer().getWorldLocation().distanceTo(instanceWp) == 0)
		{
			currentPath.add(wps.get(wpsPos));
			lastPos = wpsPos;
			setLinePoints(currentPath);
		}
	}

	public void setupPaths()
	{
		// Store world points
		// Compare to world points
		int current1 = client.getVarbitValue(2412);
		int current2 = client.getVarbitValue(2413);
		int current3 = client.getVarbitValue(2414);
		int current4 = client.getVarbitValue(2415);

		if (current1 == column1 &&
			current2 == column2 &&
			current3 == column3 &&
			current4 == column4
		)
		{
			return;
		}

		column1 = current1;
		column2 = current2;
		column3 = current3;
		column4 = current4;

		setLinePoints(new ArrayList<>());
		currentPath = new ArrayList<>();


		// Path 1
		if (client.getVarbitValue(2414) == 83)
		{
			wps = Arrays.asList(
				new WorldPoint(1731, 5106, 2),
				new WorldPoint(1731, 5103, 2),
				new WorldPoint(1734, 5103, 2),
				new WorldPoint(1737, 5103, 2),
				new WorldPoint(1740, 5103, 2),
				new WorldPoint(1740, 5100, 2),
				new WorldPoint(1740, 5097, 2),
				new WorldPoint(1737, 5097, 2),
				new WorldPoint(1734, 5097, 2),
				new WorldPoint(1731, 5097, 2),
				new WorldPoint(1731, 5094, 2),
				new WorldPoint(1731, 5091, 2),
				new WorldPoint(1731, 5088, 2),
				new WorldPoint(1734, 5088, 2),
				new WorldPoint(1737, 5088, 2),
				new WorldPoint(1737, 5085, 2),
				new WorldPoint(1737, 5083, 2)
			);
			setWorldPoint(1737, 5083, 2);
		}
		// Path 2
		else if (client.getVarbitValue(2414) == 192)
		{
			wps = Arrays.asList(
				new WorldPoint(1737, 5106, 2),
				new WorldPoint(1737, 5103, 2),
				new WorldPoint(1734, 5103, 2),
				new WorldPoint(1731, 5103, 2),
				new WorldPoint(1731, 5100, 2),
				new WorldPoint(1731, 5097, 2),
				new WorldPoint(1734, 5097, 2),
				new WorldPoint(1734, 5094, 2),
				new WorldPoint(1734, 5091, 2),
				new WorldPoint(1731, 5091, 2),
				new WorldPoint(1731, 5088, 2),
				new WorldPoint(1731, 5085, 2),
				new WorldPoint(1731, 5083, 2)
			);
			setWorldPoint(1731, 5083, 2);
		}
		// Path 3
		else if (client.getVarbitValue(2415) == 7)
		{
			wps = Arrays.asList(
				new WorldPoint(1731, 5106, 2),
				new WorldPoint(1731, 5103, 2),
				new WorldPoint(1734, 5103, 2),
				new WorldPoint(1737, 5103, 2),
				new WorldPoint(1737, 5100, 2),
				new WorldPoint(1737, 5097, 2),
				new WorldPoint(1734, 5097, 2),
				new WorldPoint(1731, 5097, 2),
				new WorldPoint(1731, 5094, 2),
				new WorldPoint(1731, 5091, 2),
				new WorldPoint(1734, 5091, 2),
				new WorldPoint(1737, 5091, 2),
				new WorldPoint(1740, 5091, 2),
				new WorldPoint(1740, 5088, 2),
				new WorldPoint(1740, 5085, 2),
				new WorldPoint(1740, 5083, 2)
			);
			setWorldPoint(1740, 5083, 2);
		}
		// Path 4, shared varbit with 3 but will 3 has already passed
		else if (client.getVarbitValue(2412) == 28)
		{
			wps = Arrays.asList(
				new WorldPoint(1734, 5106, 2),
				new WorldPoint(1734, 5103, 2),
				new WorldPoint(1737, 5103, 2),
				new WorldPoint(1737, 5100, 2),
				new WorldPoint(1737, 5097, 2),
				new WorldPoint(1734, 5097, 2),
				new WorldPoint(1731, 5097, 2),
				new WorldPoint(1731, 5094, 2),
				new WorldPoint(1731, 5091, 2),
				new WorldPoint(1734, 5091, 2),
				new WorldPoint(1734, 5088, 2),
				new WorldPoint(1737, 5088, 2),
				new WorldPoint(1737, 5085, 2),
				new WorldPoint(1737, 5083, 2)
			);
			setWorldPoint(1731, 5083, 2);
		}
		// Path 5
		else if (client.getVarbitValue(2415) == 123)
		{
			wps = Arrays.asList(
				new WorldPoint(1734, 5106, 2),
				new WorldPoint(1734, 5103, 2),
				new WorldPoint(1737, 5103, 2),
				new WorldPoint(1740, 5103, 2),
				new WorldPoint(1740, 5100, 2),
				new WorldPoint(1740, 5097, 2),
				new WorldPoint(1740, 5094, 2),
				new WorldPoint(1737, 5094, 2),
				new WorldPoint(1734, 5094, 2),
				new WorldPoint(1731, 5094, 2),
				new WorldPoint(1731, 5091, 2),
				new WorldPoint(1731, 5088, 2),
				new WorldPoint(1734, 5088, 2),
				new WorldPoint(1737, 5088, 2),
				new WorldPoint(1740, 5088, 2),
				new WorldPoint(1740, 5085, 2),
				new WorldPoint(1740, 5083, 2)
			);
			setWorldPoint(1740, 5083, 2);
		}
		// Path 6
		else if (client.getVarbitValue(2414) == 42)
		{
			wps = Arrays.asList(
				new WorldPoint(1731, 5106, 2),
				new WorldPoint(1731, 5103, 2),
				new WorldPoint(1731, 5100, 2),
				new WorldPoint(1734, 5100, 2),
				new WorldPoint(1737, 5100, 2),
				new WorldPoint(1740, 5100, 2),
				new WorldPoint(1740, 5097, 2),
				new WorldPoint(1740, 5094, 2),
				new WorldPoint(1737, 5094, 2),
				new WorldPoint(1734, 5094, 2),
				new WorldPoint(1734, 5091, 2),
				new WorldPoint(1734, 5088, 2),
				new WorldPoint(1737, 5088, 2),
				new WorldPoint(1740, 5088, 2),
				new WorldPoint(1740, 5085, 2),
				new WorldPoint(1740, 5083, 2)
			);
			setWorldPoint(1740, 5083, 2);
		}
		// Path 7
		else if (client.getVarbitValue(2413) == 218)
		{
			wps = Arrays.asList(
				new WorldPoint(1734, 5106, 2),
				new WorldPoint(1734, 5103, 2),
				new WorldPoint(1737, 5103, 2),
				new WorldPoint(1737, 5100, 2),
				new WorldPoint(1737, 5097, 2),
				new WorldPoint(1734, 5097, 2),
				new WorldPoint(1734, 5094, 2),
				new WorldPoint(1731, 5094, 2),
				new WorldPoint(1731, 5091, 2),
				new WorldPoint(1731, 5088, 2),
				new WorldPoint(1734, 5088, 2),
				new WorldPoint(1737, 5088, 2),
				new WorldPoint(1737, 5085, 2),
				new WorldPoint(1737, 5083, 2)
			);
			setWorldPoint(1737, 5083, 2);
		}
		// Path 8
		else if (client.getVarbitValue(2414) == 91)
		{
			wps = Arrays.asList(
				new WorldPoint(1734, 5106, 2),
				new WorldPoint(1734, 5103, 2),
				new WorldPoint(1737, 5103, 2),
				new WorldPoint(1740, 5103, 2),
				new WorldPoint(1740, 5100, 2),
				new WorldPoint(1740, 5097, 2),
				new WorldPoint(1737, 5097, 2),
				new WorldPoint(1737, 5094, 2),
				new WorldPoint(1734, 5094, 2),
				new WorldPoint(1734, 5091, 2),
				new WorldPoint(1734, 5088, 2),
				new WorldPoint(1737, 5088, 2),
				new WorldPoint(1737, 5085, 2),
				new WorldPoint(1737, 5083, 2)
			);
			setWorldPoint(1737, 5083, 2);
		}
		// Path 9
		else if (client.getVarbitValue(2413) == 3)
		{
			wps = Arrays.asList(
				new WorldPoint(1740, 5106, 2),
				new WorldPoint(1740, 5103, 2),
				new WorldPoint(1740, 5100, 2),
				new WorldPoint(1737, 5100, 2),
				new WorldPoint(1737, 5097, 2),
				new WorldPoint(1737, 5094, 2),
				new WorldPoint(1740, 5094, 2),
				new WorldPoint(1740, 5091, 2),
				new WorldPoint(1740, 5088, 2),
				new WorldPoint(1737, 5088, 2),
				new WorldPoint(1734, 5088, 2),
				new WorldPoint(1734, 5085, 2),
				new WorldPoint(1734, 5083, 2)
			);
			setWorldPoint(1734, 5083, 2);
		}
		// Path 10
		else if (client.getVarbitValue(2412) == 30)
		{
			wps = Arrays.asList(
				new WorldPoint(1734, 5106, 2),
				new WorldPoint(1734, 5103, 2),
				new WorldPoint(1737, 5103, 2),
				new WorldPoint(1737, 5100, 2),
				new WorldPoint(1737, 5097, 2),
				new WorldPoint(1734, 5097, 2),
				new WorldPoint(1731, 5097, 2),
				new WorldPoint(1731, 5094, 2),
				new WorldPoint(1731, 5091, 2),
				new WorldPoint(1731, 5088, 2),
				new WorldPoint(1734, 5088, 2),
				new WorldPoint(1737, 5088, 2),
				new WorldPoint(1737, 5085, 2),
				new WorldPoint(1737, 5083, 2)
			);
			setWorldPoint(1737, 5083, 2);
		}
	}
}
