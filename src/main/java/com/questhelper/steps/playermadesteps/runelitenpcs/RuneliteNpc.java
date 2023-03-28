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
package com.questhelper.steps.playermadesteps.runelitenpcs;

import com.questhelper.requirements.Requirement;
import com.questhelper.steps.playermadesteps.RuneliteDialogStep;
import com.questhelper.steps.playermadesteps.RuneliteNpcDialogStep;
import java.util.HashMap;
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

	@Getter
	private HashMap<Requirement, RuneliteDialogStep> dialogTrees = new HashMap<>();

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

	@Getter
	@Setter
	private String examine;

	@Getter
	@Setter
	private Requirement displayReq;

	ChatBox currentChatBox;

	protected RuneliteNpc(Client client, ClientThread clientThread, WorldPoint worldPoint, int[] model, int animation)
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

	public void activate()
	{
		if (displayReq == null || displayReq.check(client))
		{
			runeliteObject.setActive(true);
		}
	}

	public void disable()
	{
		runeliteObject.setActive(false);
	}

	// Won't have a null default at start inherently
	public void addDialogTree(Requirement req, RuneliteDialogStep dialogTree)
	{
		dialogTrees.put(req, dialogTree);
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
		dialogTrees.forEach(((requirement, runeliteDialogStep) -> {
			if (requirement == null || requirement.check(client))
			{
				if (runeliteDialogStep.isPlayer())
				{
					currentChatBox = new PlayerChatBox(client, chatboxPanelManager)
						.dialog(runeliteDialogStep)
						.build();
				}
				else
				{
					currentChatBox = new NpcChatBox(client, chatboxPanelManager)
						.dialog(runeliteDialogStep)
						.build();
				}
				return;
			}
			throw new IllegalStateException("No default dialog specified for " + getName());
		}));
	}

	public void progressDialog()
	{
		if (currentChatBox != null) currentChatBox.progressDialog();
	}

	public void facePlayer(Client client)
	{
		double playerX = client.getLocalPlayer().getLocalLocation().getX();
		double playerY = client.getLocalPlayer().getLocalLocation().getY();

		double currentNPCX = runeliteObject.getLocation().getX();
		double currentNPCY = runeliteObject.getLocation().getY();
		int nextOrientation = calculateRotationFromAtoB(playerX, playerY, currentNPCX, currentNPCY);
		runeliteObject.setOrientation(nextOrientation);
	}

	public int calculateRotationFromAtoB(double aX, double aY, double bX, double bY)
	{

		double xDiff = bX - aX;
		double yDiff = bY - aY;

		double angle = 0;

		double aToBAngle = Math.abs(Math.atan(yDiff / xDiff));

		if (xDiff > 0 && yDiff <= 0)
		{
			angle = aToBAngle + (3 * Math.PI / 2);
		}
		else if (xDiff >= 0 && yDiff > 0)
		{
			angle = Math.atan(xDiff / yDiff) + Math.PI;
		}
		else if (xDiff < 0 && yDiff >= 0)
		{
			angle = aToBAngle + (Math.PI / 2);
		}
		else if (xDiff <= 0 && yDiff < 0)
		{
			angle = Math.atan(xDiff / yDiff);
		}

		int nextOrientation = (int) ((angle * 1024 / Math.PI) - 1024);
		if (nextOrientation < 0)
		{
			nextOrientation += 2048;
		}

		return nextOrientation;
	}

	public boolean partiallyRotateToPlayer(Client client)
	{
		final int MAX_ROTATION_PER_CALL = 32;
		final int MAX_ROTATION = 2048;
		double playerX = client.getLocalPlayer().getLocalLocation().getX();
		double playerY = client.getLocalPlayer().getLocalLocation().getY();

		double currentNPCX = runeliteObject.getLocation().getX();
		double currentNPCY = runeliteObject.getLocation().getY();

		int newOrientation = calculateRotationFromAtoB(playerX, playerY, currentNPCX, currentNPCY);
		int existingOrientation = runeliteObject.getOrientation();

		boolean isClockwise = Math.floorMod(existingOrientation - newOrientation, MAX_ROTATION) >
			Math.floorMod(newOrientation - existingOrientation, MAX_ROTATION);

		int additionalRotation;
		if (isClockwise)
		{
			additionalRotation = Math.min(Math.abs(existingOrientation - newOrientation), MAX_ROTATION_PER_CALL);
		}
		else
		{
			additionalRotation = -1 * Math.min(Math.abs(newOrientation - existingOrientation), MAX_ROTATION_PER_CALL);
		}

		int actualNewOrientation = Math.floorMod(existingOrientation + additionalRotation, MAX_ROTATION);

		runeliteObject.setOrientation(actualNewOrientation);

		boolean isFacingPlayer = actualNewOrientation == newOrientation;
		if (!isFacingPlayer)
		{
			runeliteObject.setAnimation(client.loadAnimation(819));
		}
		else
		{
			runeliteObject.setAnimation(client.loadAnimation(animation));
		}
		return isFacingPlayer;
	}
}
