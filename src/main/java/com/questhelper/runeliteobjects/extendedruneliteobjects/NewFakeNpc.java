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
package com.questhelper.runeliteobjects.extendedruneliteobjects;

import com.questhelper.requirements.Requirement;
import com.questhelper.runeliteobjects.RuneliteConfigSetter;
import com.questhelper.runeliteobjects.dialog.RuneliteDialogStep;
import com.questhelper.runeliteobjects.dialog.RuneliteObjectDialogStep;
import com.questhelper.runeliteobjects.extendedruneliteobjects.actions.Action;
import com.questhelper.runeliteobjects.extendedruneliteobjects.actions.LoopedAction;
import com.questhelper.steps.tools.QuestPerspective;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.MenuEntry;
import net.runelite.api.Model;
import net.runelite.api.ModelData;
import net.runelite.api.Perspective;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.game.chatbox.ChatboxPanelManager;

public class NewFakeNpc
{
	@Getter
	protected final RuneLiteObject runeliteObject;
	protected final Client client;
	protected final ClientThread clientThread;

	// TODO: Some requirements kinda require an external tracking element, so may need to shove into a ConditionalStep or some weirdness?
	@Getter
	private final LinkedHashMap<Requirement, RuneliteDialogStep> dialogTrees = new LinkedHashMap<>();

	protected Model model;
	protected int animation;

	@Getter
	private WorldPoint worldPoint;

	@Getter
	@Setter
	private String name = "Test";

	@Getter
	private String examine;

	@Getter
	@Setter
	private int faceID = -1;


	ChatBox currentChatBox;

	@Getter
	private Action examineAction;

	@Getter
	private Action talkAction;

	@Getter
	protected String nameColor = "ffff00";

	@Getter
	@Setter
	private int orientationGoal;

	@Setter
	@Getter
	private boolean needToBeCloseToTalk = true;

	@Setter
	private boolean alwaysFacePlayer = true;

	protected NewFakeNpc(Client client, ClientThread clientThread, WorldPoint worldPoint, Model model, int animation)
	{
		this.client = client;
		this.clientThread = clientThread;
		runeliteObject = client.createRuneLiteObject();
		this.model = model;
		this.animation = animation;
		update();
		runeliteObject.setShouldLoop(true);

		this.worldPoint = worldPoint;

		LocalPoint lp = QuestPerspective.getInstanceLocalPointFromReal(client, worldPoint);
		if (lp == null) return;
		runeliteObject.setLocation(lp, client.getPlane());
		setVisible(true);
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

	public Shape getClickbox()
	{
		if (QuestPerspective.getInstanceLocalPointFromReal(client, worldPoint) == null) return null;

		return Perspective.getClickbox(client,
			getRuneliteObject().getModel(),
			getRuneliteObject().getOrientation(),
			getRuneliteObject().getLocation().getX(),
			getRuneliteObject().getLocation().getY(),
			Perspective.getTileHeight(client, getRuneliteObject().getLocation(), client.getPlane()));
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

	public void setWorldPoint(WorldPoint worldPoint)
	{
		this.worldPoint = worldPoint;
		LocalPoint lp = QuestPerspective.getInstanceLocalPointFromReal(client, worldPoint);
		if (lp == null) return;
		this.runeliteObject.setLocation(lp, client.getPlane());
	}

	protected void update()
	{
		clientThread.invoke(() ->
		{
			runeliteObject.setAnimation(client.loadAnimation(animation));
			runeliteObject.setModel(model);
			return true;
		});
	}

	public boolean isRuneliteObjectActive()
	{
		return runeliteObject.isActive();
	}

	public void setVisible(boolean visible)
	{
		runeliteObject.setActive(visible);
		render();
	}

	public void render()
	{
		LocalPoint lp = LocalPoint.fromWorld(client, getWorldPoint());
		if (lp == null)
		{
			runeliteObject.setActive(false);
			return;
		}

		updateLocation(lp);
	}

	public void updateLocation(LocalPoint lp)
	{
		runeliteObject.setLocation(lp, getWorldPoint().getPlane());
	}

	protected void actionOnClientTick()
	{
		if (alwaysFacePlayer)
		{
			setOrientationGoalAsPlayer(client);
		}
	}

	public void activateAction(Action action, MenuEntry menuEntry)
	{
		action.activate(menuEntry);
	}

	public void performTalkAction(MenuEntry menuEntry)
	{
		activateAction(talkAction, menuEntry);
	}

	public void addExamine(RLObjectManager rlObjectManager, String examineText)
	{
		examine = examineText;
		addExamineAction(rlObjectManager);
	}


	// Won't have a null default at start inherently
	public void addDialogTree(RLObjectManager rlObjectManager, Requirement req, RuneliteDialogStep dialogTree)
	{
		dialogTrees.put(req, dialogTree);
		if (talkAction == null)
		{
			addTalkAction(rlObjectManager);
		}
	}

	private void addTalkAction(RLObjectManager runeliteObjectManager)
	{
		talkAction = new Action(runeliteObjectManager.getTalkAction(this));
	}

	private void addExamineAction(RLObjectManager runeliteObjectManager)
	{
		examineAction = new Action(runeliteObjectManager.getExamineAction(this));
	}

	public RuneliteObjectDialogStep createDialogStepForNpc(String text, int faceAnimation)
	{
		if (faceID == -1)
		{
			throw new IllegalStateException("Face value must be positive");
		}
		if (name == null)
		{
			throw new IllegalStateException("Name must be assigned");
		}
		return new RuneliteObjectDialogStep(name, text, faceID, faceAnimation);
	}

	public void setupChatBox(ChatboxPanelManager chatboxPanelManager)
	{
		for (Map.Entry<Requirement, RuneliteDialogStep> dialogTree : dialogTrees.entrySet())
		{
			Requirement requirement = dialogTree.getKey();
			RuneliteDialogStep runeliteDialogStep = dialogTree.getValue();
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
			}
		}
		if (currentChatBox == null) throw new IllegalStateException("No default dialog specified for " + getName());
	}

	public void progressDialog()
	{
		if (currentChatBox != null) currentChatBox.progressDialog();
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

	public void setOrientationGoalAsPlayer(Client client)
	{
		double playerX = client.getLocalPlayer().getLocalLocation().getX();
		double playerY = client.getLocalPlayer().getLocalLocation().getY();
		double currentNPCX = runeliteObject.getLocation().getX();
		double currentNPCY = runeliteObject.getLocation().getY();

		int newOrientation = calculateRotationFromAtoB(playerX, playerY, currentNPCX, currentNPCY);
		setOrientationGoal(newOrientation);
	}

	public boolean partiallyRotateToGoal()
	{
		final int MAX_ROTATION_PER_CALL = 32;
		final int MAX_ROTATION = 2048;

		int newOrientation = getOrientationGoal();

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

		return actualNewOrientation == newOrientation;
	}
}
