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

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.Model;
import net.runelite.api.ModelData;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.chatbox.ChatboxPanelManager;

public class RuneliteNpc
{
	@Getter
	private final RuneLiteObject runeliteObject;
	private final Client client;
	private final ClientThread clientThread;

	@Setter
	private RuneliteNpcDialogStep dialogTree;

	private Model model;
	private int animation;

	@Setter
	@Getter
	private WorldPoint worldPoint;

	@Setter
	private int face = -1;

	@Getter
	@Setter
	private String name = "Test";

	ChatBox currentChatBox;

	public RuneliteNpc(Client client, ClientThread clientThread, WorldPoint worldPoint, int[] model, int animation)
	{
		this.client = client;
		this.clientThread = clientThread;
		runeliteObject = client.createRuneLiteObject();

		ModelData mdf = createModel(client, model);

		this.model = mdf.cloneColors()
			.light();

		this.animation = animation;
		update();
		runeliteObject.setShouldLoop(true);

		this.worldPoint = worldPoint;

		LocalPoint lp = LocalPoint.fromWorld(client, worldPoint);
		if (lp == null) return;
		runeliteObject.setLocation(lp, client.getPlane());
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
			runeliteObject.setAnimation(client.loadAnimation(animation));
			runeliteObject.setModel(model);
			return true;
		});
	}

	public boolean isActive()
	{
		return runeliteObject.isActive();
	}

	public void remove()
	{
		runeliteObject.setActive(false);
	}

	public RuneliteNpcDialogStep createDialogStepForNpc(String text, int faceAnimation)
	{
		if (face == -1)
		{
			throw new IllegalStateException("Face value must be positive");
		}
		if (name == null)
		{
			throw new IllegalStateException("Name must be assigned");
		}
		return new RuneliteNpcDialogStep(name, text, face, faceAnimation);
	}

	public RuneliteNpcDialogStep createDialogStepForNpc(String text)
	{
		if (face == -1)
		{
			throw new IllegalStateException("Face value must be positive");
		}
		if (name == null)
		{
			throw new IllegalStateException("Name must be assigned");
		}
		return new RuneliteNpcDialogStep(name, text, face);
	}

	public void setupChatBox(ChatboxPanelManager chatboxPanelManager)
	{
		currentChatBox = new ChatBox(client, chatboxPanelManager, clientThread)
			.dialog(dialogTree)
			.build();
	}
}
