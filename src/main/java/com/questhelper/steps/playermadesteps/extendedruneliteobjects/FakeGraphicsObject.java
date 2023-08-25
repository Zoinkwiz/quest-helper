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

	@Override
	protected void actionOnGameTick()
	{
		if (runeliteObject.getAnimation().getNumFrames() <= runeliteObject.getAnimationFrame() + 1)
		{
			setEnabled(false);
			disable();
			objectToSpawnAfter.setEnabled(true);
			objectToSpawnAfter.activate();
		}
	}
}
