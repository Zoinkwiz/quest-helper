/*
 *
 *  * Copyright (c) 2021, Zoinkwiz
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice, this
 *  *    list of conditions and the following disclaimer.
 *  * 2. Redistributions in binary form must reproduce the above copyright notice,
 *  *    this list of conditions and the following disclaimer in the documentation
 *  *    and/or other materials provided with the distribution.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.questhelper.requirements.location;

import com.questhelper.requirements.AbstractRequirement;
import com.questhelper.steps.tools.QuestPerspective;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

import javax.annotation.Nonnull;
import java.util.List;

public class TileIsLoadedRequirement extends AbstractRequirement
{
	private final WorldPoint worldPoint;
	private final String displayText;

	/**
	 * Check if the player is either in the specified zone.
	 *
	 * @param worldPoint the WorldPoint to check
	 */
	public TileIsLoadedRequirement(WorldPoint worldPoint)
	{
		assert(worldPoint != null);
		this.worldPoint = worldPoint;
		this.displayText = "WorldPoint " + worldPoint.toString() + "is loaded locally.";
	}

	@Override
	public boolean check(Client client)
	{
		List<LocalPoint> localPoints = QuestPerspective.getLocalPointsFromWorldPointInInstance(client.getTopLevelWorldView(), worldPoint);
		for (LocalPoint localPoint : localPoints)
		{
			// Final tiles of a scene do not have objects of them
			if (localPoint.getSceneX() != Constants.SCENE_SIZE - 1 && localPoint.getSceneY() != Constants.SCENE_SIZE - 1)
			{
				return true;
			}
		}
		return false;
	}

	@Nonnull
	@Override
	public String getDisplayText()
	{
		return displayText == null ? "" : displayText;
	}
}
