/*
 * Copyright (c) 2023, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.steps.playermadesteps.extendedruneliteobjects;

import com.questhelper.requirements.Requirement;
import com.questhelper.steps.playermadesteps.RuneliteConfigSetter;
import com.questhelper.steps.playermadesteps.RuneliteDialogStep;
import com.questhelper.steps.playermadesteps.RuneliteObjectDialogStep;
import java.awt.Shape;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.Setter;
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
import net.runelite.client.game.chatbox.ChatboxPanelManager;

public class ExtendedRuneliteObject
{
	@Getter
	private final RuneLiteObject runeliteObject;
	protected final Client client;
	private final ClientThread clientThread;

	// TODO: Some requirements kinda require an external tracking element, so may need to shove into a ConditionalStep or some weirdness?
	@Getter
	private final LinkedHashMap<Requirement, RuneliteDialogStep> dialogTrees = new LinkedHashMap<>();

	private Model model;
	private int animation;

	@Getter
	private final WorldPoint worldPoint;

	@Setter
	@Getter
	private int face = -1;

	@Getter
	@Setter
	private String name = "Test";

	@Getter
	@Setter
	private String examine;

	@Getter
	private Requirement displayReq;

	ChatBox currentChatBox;

	@Getter
	private ReplacedObject objectToRemove;

	@Getter
	@Setter
	private Consumer<MenuEntry> replaceWalkAction;

	@Getter
	@Setter
	private String replaceWalkActionText;

	@Getter
	private final HashMap<String, Consumer<MenuEntry>> actions = new HashMap<>();

	@Getter
	private final HashMap<String, Consumer<MenuEntry>> priorityActions = new HashMap<>();

	protected RuneliteObjectTypes objectType = RuneliteObjectTypes.UNDEFINED;

	@Getter
	protected String nameColor = "ffff00";

	@Getter
	@Setter
	private boolean isHiddenNoOptions;

	@Getter
	@Setter
	private int orientationGoal;

	public static final int MAX_TALK_DISTANCE = 3;

	protected ExtendedRuneliteObject(Client client, ClientThread clientThread, WorldPoint worldPoint, int[] model, int animation)
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
		activate();
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

	public Shape getClickbox()
	{
		return Perspective.getClickbox(client, getRuneliteObject().getModel(), getRuneliteObject().getOrientation(), getRuneliteObject().getLocation().getX(), getRuneliteObject().getLocation().getY(),
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

	protected void update()
	{
		clientThread.invoke(() ->
		{
			runeliteObject.setAnimation(client.loadAnimation(animation));
			runeliteObject.setModel(model);
			runeliteObject.setShouldLoop(true);
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
			if (objectToRemove != null)
			{
				removeOtherObjects();
				// Delete item
			}
			isHiddenNoOptions = false;
			runeliteObject.setActive(true);
		}
		else
		{
			isHiddenNoOptions = true;
			runeliteObject.setActive(false);
		}
	}

	public void disable()
	{
		if (objectToRemove != null)
		{
			// Add item
			// TODO: Can't recreate object, but could create a fake RuneliteObject to represent it
		}
		runeliteObject.setActive(false);
	}

	public void setObjectToRemove(ReplacedObject replacedObject)
	{
		this.objectToRemove = replacedObject;
		if (isActive())
		{
			removeOtherObjects();
		}
	}

	public void setDisplayRequirement(Requirement req)
	{
		this.displayReq = req;
		activate();
	}

	private void removeOtherObjects()
	{
		Scene scene = client.getScene();
		Tile[][] tiles = scene.getTiles()[client.getPlane()];

		LocalPoint lp = LocalPoint.fromWorld(client, objectToRemove.getWp());
		if (lp == null) return;
		Tile tile = tiles[lp.getSceneX()][lp.getSceneY()];
		if (tile == null) return;

		for (GameObject gameObject : tile.getGameObjects())
		{
			if (gameObject == null) continue;;
			if (gameObject.getId() == objectToRemove.getObjectID())
			{
				// Currently it's not possible to re-add the Game Object outside of an area load
				scene.removeGameObject(gameObject);
			}
		}

	}

	// Won't have a null default at start inherently
	public void addDialogTree(Requirement req, RuneliteDialogStep dialogTree)
	{
		dialogTrees.put(req, dialogTree);
	}

	public void addTalkAction(RuneliteObjectManager runeliteObjectManager)
	{
		priorityActions.put("Talk-to", runeliteObjectManager.getTalkAction(this));
	}

	public void addExamineAction(RuneliteObjectManager runeliteObjectManager)
	{
		actions.put("Examine", runeliteObjectManager.getExamineAction(this));
	}

	@Setter
	@Getter
	private Supplier<Boolean> pendingAction;

	public void checkPendingAction()
	{
		if (pendingAction != null && pendingAction.get())
		{
			pendingAction = null;
		}
	}

	public void addAction(String name, Consumer<MenuEntry> action)
	{
		actions.put(name, action);
	}

	public void addPriorityAction(String name, Consumer<MenuEntry> action)
	{
		priorityActions.put(name, action);
	}

	public RuneliteObjectDialogStep createDialogStepForNpc(String text, FaceAnimationIDs faceAnimation)
	{
		return createDialogStepForNpc(text, faceAnimation.getAnimationID());
	}

	public RuneliteObjectDialogStep createDialogStepForNpc(String text, int faceAnimation)
	{
		if (face == -1)
		{
			throw new IllegalStateException("Face value must be positive");
		}
		if (name == null)
		{
			throw new IllegalStateException("Name must be assigned");
		}
		return new RuneliteObjectDialogStep(name, text, face, faceAnimation);
	}

	public RuneliteObjectDialogStep createDialogStepForNpc(String text)
	{
		if (face == -1)
		{
			throw new IllegalStateException("Face value must be positive");
		}
		if (name == null)
		{
			throw new IllegalStateException("Name must be assigned");
		}
		return new RuneliteObjectDialogStep(name, text, face);
	}

	public RuneliteObjectDialogStep createDialogStepForNpc(String text, RuneliteConfigSetter setter)
	{
		if (face == -1)
		{
			throw new IllegalStateException("Face value must be positive");
		}
		if (name == null)
		{
			throw new IllegalStateException("Name must be assigned");
		}
		return new RuneliteObjectDialogStep(name, text, face, setter);
	}

	public RuneliteObjectDialogStep createDialogStepForNpc(String text, int faceAnimation, RuneliteConfigSetter setter)
	{
		if (face == -1)
		{
			throw new IllegalStateException("Face value must be positive");
		}
		if (name == null)
		{
			throw new IllegalStateException("Name must be assigned");
		}
		return new RuneliteObjectDialogStep(name, text, face, faceAnimation, setter);
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

	public boolean partiallyRotateToGoal(Client client)
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

		boolean isFacingPlayer = actualNewOrientation == newOrientation;
		if (!isFacingPlayer)
		{
//			runeliteObject.setAnimation(client.loadAnimation(819));
		}
		else
		{
//			runeliteObject.setAnimation(client.loadAnimation(animation));
		}
		return isFacingPlayer;
	}
}
