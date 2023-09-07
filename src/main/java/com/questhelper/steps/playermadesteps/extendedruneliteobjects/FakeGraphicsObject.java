/*
 * Copyright (c) 2023, Zoinkwiz
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

import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.callback.ClientThread;

public class FakeGraphicsObject extends ExtendedRuneliteObject
{
	ExtendedRuneliteObject objectToSpawnAfter;
	protected FakeGraphicsObject(Client client, ClientThread clientThread, WorldPoint worldPoint,
								 int[] model, int animation, ExtendedRuneliteObject objectToSpawnAfter)
	{
		super(client, clientThread, worldPoint, model, animation);
		objectType = RuneliteObjectTypes.GRAPHICS_OBJECT;
		this.objectToSpawnAfter = objectToSpawnAfter;
		runeliteObject.setShouldLoop(false);
		runeliteObject.setActive(false);
	}

	protected FakeGraphicsObject(Client client, ClientThread clientThread, WorldPoint worldPoint,
								 int[] model, int animation)
	{
		super(client, clientThread, worldPoint, model, animation);
		objectType = RuneliteObjectTypes.GRAPHICS_OBJECT;
		this.objectToSpawnAfter = null;
		runeliteObject.setShouldLoop(false);
		runeliteObject.setActive(false);
	}

	@Override
	protected void actionOnClientTick()
	{
		if (runeliteObject.getAnimation().getNumFrames() <= runeliteObject.getAnimationFrame() + 1)
		{
			setEnabled(false);
			disable();
			if (objectToSpawnAfter != null)
			{
				objectToSpawnAfter.setEnabled(true);
				objectToSpawnAfter.activate();
			}
		}
	}
}
