/*
 * Copyright (c) 2021, Trevor <https://github.com/Trevor159>
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
package com.questhelper.steps.playermadesteps;

import java.util.function.Function;
import lombok.Getter;
import net.runelite.api.Animation;
import net.runelite.api.Client;
import net.runelite.api.Model;
import net.runelite.api.ModelData;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import java.awt.Color;
import net.runelite.client.callback.ClientThread;

class RuneliteNpc
{
	@Getter
	private final RuneLiteObject runeLiteObject;
	private final Client client;
	private final ClientThread clientThread;

	private Model model;
	private int animation;

	private static Function<RuneliteNpc, Animation> anim(int id)
	{
		return b -> b.client.loadAnimation(id);
	}

	public RuneliteNpc(Client client, ClientThread clientThread, WorldPoint worldPoint, int[] model, int animation)
	{
		this.client = client;
		this.clientThread = clientThread;
		runeLiteObject = client.createRuneLiteObject();

		ModelData mdf = createModel(client, model);

		this.model = mdf.cloneColors()
			.light();

		this.animation = animation;
		update();
		runeLiteObject.setShouldLoop(true);

		LocalPoint lp = LocalPoint.fromWorld(client, worldPoint);
		if (lp == null) return;
		runeLiteObject.setLocation(lp, client.getPlane());

		runeLiteObject.setActive(true);
	}

	private static ModelData createModel(Client client, int[] data)
	{
		ModelData[] modelData = new ModelData[data.length];
		for (int i = 0; i < data.length; i++)
		{
			modelData[i] = client.loadModelData(data[i]);
		}

		return client.mergeModels(modelData);
	}

	private static ModelData createModel(Client client, ModelData[] data)
	{
		return client.mergeModels(data);
	}

	public void setAnimation(int animation)
	{
		if (this.animation == animation)
		{
			return;
		}

		this.animation = animation;
		update();
	}

	public void setModel(int[] model)
	{
		this.model = createModel(client, model).cloneColors()
			.light();
		update();
	}

	private void update()
	{
		clientThread.invoke(() ->
		{
			runeLiteObject.setAnimation(client.loadAnimation(animation));
			runeLiteObject.setModel(model);
			return true;
		});
	}

	public void setActive(LocalPoint lp)
	{
		runeLiteObject.setLocation(lp, client.getPlane());
		runeLiteObject.setActive(true);
	}

	public boolean isActive()
	{
		return runeLiteObject.isActive();
	}

	public void remove()
	{
		runeLiteObject.setActive(false);
	}

}
