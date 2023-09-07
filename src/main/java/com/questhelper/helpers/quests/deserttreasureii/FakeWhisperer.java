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
package com.questhelper.helpers.quests.deserttreasureii;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import static com.questhelper.requirements.util.LogicHelper.nor;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.playermadesteps.RuneliteDialogStep;
import com.questhelper.steps.playermadesteps.RuneliteObjectDialogStep;
import com.questhelper.steps.playermadesteps.extendedruneliteobjects.FaceAnimationIDs;
import com.questhelper.steps.playermadesteps.extendedruneliteobjects.FakeGraphicsObject;
import com.questhelper.steps.playermadesteps.extendedruneliteobjects.FakeNpc;
import com.questhelper.steps.playermadesteps.extendedruneliteobjects.RuneliteObjectManager;
import com.questhelper.steps.playermadesteps.extendedruneliteobjects.actions.Action;
import com.questhelper.steps.tools.QuestPerspective;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import net.runelite.api.Client;
import net.runelite.api.HeadIcon;
import net.runelite.api.MenuEntry;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Projectile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public class FakeWhisperer
{
	public static int showRedHitsplatWhispererUntilTick;
	public static int showBlueHitsplatWhispererUntilTick;

	public static void createWhisperer(Client client, QuestHelper qh, RuneliteObjectManager runeliteObjectManager)
	{
		FakeNpc whisperer = runeliteObjectManager.createFakeNpc(qh.toString(),
			new int[] { 49222, 49218, 49221, 49224, 49219 },
			new WorldPoint(2656, 6379, 0),
			10230);

		String TENTACLE_SUBGROUP = "tentacleAttack";
		String WATER_SUBGROUP = "waterSplash";
		runeliteObjectManager.addSubGroup(qh.toString(), TENTACLE_SUBGROUP);
		runeliteObjectManager.addSubGroup(qh.toString(), WATER_SUBGROUP);

		AtomicInteger actionsPerformed = new AtomicInteger(0);

		Action deleteTentacles = new Action((menuEntry -> {
			runeliteObjectManager.removeGroup(TENTACLE_SUBGROUP);
		}));

		Consumer<MenuEntry> spawnTentacles = (menuEntry) -> {
			WorldPoint playerP = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
			if (playerP == null) return;
			WorldPoint tentacleNEWP = new WorldPoint(playerP.getX() + 4, playerP.getY() + 4, 0);
			WorldPoint tentacleNWWP = new WorldPoint(playerP.getX() - 4, playerP.getY() + 4, 0);
			WorldPoint tentacleSEWP = new WorldPoint(playerP.getX() + 4, playerP.getY() - 4, 0);
			WorldPoint tentacleSWWP = new WorldPoint(playerP.getX() - 4, playerP.getY() - 4, 0);

			// 7483 - ???
			// 7262
			// 7265
			// Sound

			// Shuffle noise
			FakeNpc tentacleNE = runeliteObjectManager.createFakeNpc(TENTACLE_SUBGROUP,
				new int[] { 49250 },
				tentacleNEWP, 10264);
			tentacleNE.setAnimation(10263, 10266);

			tentacleNE.setOrientationGoal(1792);
			tentacleNE.getRuneliteObject().setOrientation(1792);

			FakeNpc tentacleNW = runeliteObjectManager.createFakeNpc(TENTACLE_SUBGROUP, new int[] { 49250 },
				tentacleNWWP, 10264);
			tentacleNW.setAnimation(10263, 10266);

			tentacleNW.setOrientationGoal(256);
			tentacleNW.getRuneliteObject().setOrientation(256);

			FakeNpc tentacleSE = runeliteObjectManager.createFakeNpc(TENTACLE_SUBGROUP, new int[]  {49250 },
				tentacleSEWP, 10264);
			tentacleSE.setAnimation(10263, 10266);

			tentacleSE.setOrientationGoal(768);
			tentacleSE.getRuneliteObject().setOrientation(768);

			FakeNpc tentacleSW = runeliteObjectManager.createFakeNpc(TENTACLE_SUBGROUP, new int[] { 49250 },
				tentacleSWWP, 10264);
			tentacleSW.setAnimation(10263, 10266);

			tentacleSW.setOrientationGoal(1280);
			tentacleSW.getRuneliteObject().setOrientation(1280);

			whisperer.addDelayedAction(4, deleteTentacles);
			whisperer.addDelayedAction(3, new Action((menuEntry2) -> {
				client.playSoundEffect(7321);

				WorldPoint splash0NW = new WorldPoint(playerP.getX() - 3, playerP.getY() + 3, 0);
				WorldPoint splash1NW = new WorldPoint(playerP.getX() - 2, playerP.getY() + 2, 0);
				WorldPoint splash2NW = new WorldPoint(playerP.getX() - 1, playerP.getY() + 1, 0);

				WorldPoint splash0NE = new WorldPoint(playerP.getX() + 3, playerP.getY() + 3, 0);
				WorldPoint splash1NE = new WorldPoint(playerP.getX() + 2, playerP.getY() + 2, 0);
				WorldPoint splash2NE = new WorldPoint(playerP.getX() + 1, playerP.getY() + 1, 0);

				WorldPoint splash0SE = new WorldPoint(playerP.getX() + 3, playerP.getY() - 3, 0);
				WorldPoint splash1SE = new WorldPoint(playerP.getX() + 2, playerP.getY() - 2, 0);
				WorldPoint splash2SE = new WorldPoint(playerP.getX() + 1, playerP.getY() - 1, 0);

				WorldPoint splash0SW = new WorldPoint(playerP.getX() - 3, playerP.getY() - 3, 0);
				WorldPoint splash1SW = new WorldPoint(playerP.getX() - 2, playerP.getY() - 2, 0);
				WorldPoint splash2SW = new WorldPoint(playerP.getX() - 1, playerP.getY() - 1, 0);

				WorldPoint splash2Middle = new WorldPoint(playerP.getX(), playerP.getY(), 0);

				FakeGraphicsObject waterNW0 = runeliteObjectManager.createGraphicsFakeObject(
					WATER_SUBGROUP, new int[] { 49225 },
					splash0NW, 10221);
				FakeGraphicsObject waterNE0 = runeliteObjectManager.createGraphicsFakeObject(
					WATER_SUBGROUP, new int[] { 49225 },
					splash0NE, 10221);
				FakeGraphicsObject waterSE0 = runeliteObjectManager.createGraphicsFakeObject(
					WATER_SUBGROUP, new int[] { 49225 },
					splash0SE, 10221);
				FakeGraphicsObject waterSW0 = runeliteObjectManager.createGraphicsFakeObject(
					WATER_SUBGROUP, new int[] { 49225 },
					splash0SW, 10221);

				waterNW0.setScaledModel(new int[] { 49225 }, 60, 60, 60);
				waterNE0.setScaledModel(new int[] { 49225 }, 60, 60, 60);
				waterSW0.setScaledModel(new int[] { 49225 }, 60, 60, 60);
				waterSE0.setScaledModel(new int[] { 49225 }, 60, 60, 60);

				FakeGraphicsObject waterNW1 = runeliteObjectManager.createGraphicsFakeObject(
					WATER_SUBGROUP, new int[] { 49225 },
					splash1NW, 10221);
				FakeGraphicsObject waterNE1 = runeliteObjectManager.createGraphicsFakeObject(
					WATER_SUBGROUP, new int[] { 49225 },
					splash1NE, 10221);
				FakeGraphicsObject waterSE1 = runeliteObjectManager.createGraphicsFakeObject(
					WATER_SUBGROUP, new int[] { 49225 },
					splash1SE, 10221);
				FakeGraphicsObject waterSW1 = runeliteObjectManager.createGraphicsFakeObject(
					WATER_SUBGROUP, new int[] { 49225 },
					splash1SW, 10221);

				waterNW1.setScaledModel(new int[] { 49225 }, 90, 90, 90);
				waterNE1.setScaledModel(new int[] { 49225 }, 90, 90, 90);
				waterSW1.setScaledModel(new int[] { 49225 }, 90, 90, 90);
				waterSE1.setScaledModel(new int[] { 49225 }, 90, 90, 90);
				// Scale all to 90

				FakeGraphicsObject waterNW2 = runeliteObjectManager.createGraphicsFakeObject(
					WATER_SUBGROUP, new int[] { 49225 },
					splash2NW, 10221);
				FakeGraphicsObject waterNE2 = runeliteObjectManager.createGraphicsFakeObject(
					WATER_SUBGROUP, new int[] { 49225 },
					splash2NE, 10221);
				FakeGraphicsObject waterSE2 = runeliteObjectManager.createGraphicsFakeObject(
					WATER_SUBGROUP, new int[] { 49225 },
					splash2SE, 10221);
				FakeGraphicsObject waterSW2 = runeliteObjectManager.createGraphicsFakeObject(
					WATER_SUBGROUP, new int[] { 49225 },
					splash2SW, 10221);

				waterNW2.setScaledModel(new int[] { 49225 }, 150, 150, 150);
				waterNE2.setScaledModel(new int[] { 49225 }, 150, 150, 150);
				waterSW2.setScaledModel(new int[] { 49225 }, 150, 150, 150);
				waterSE2.setScaledModel(new int[] { 49225 }, 150, 150, 150);

				FakeGraphicsObject waterMiddle = runeliteObjectManager.createGraphicsFakeObject(
					WATER_SUBGROUP, new int[] { 49225 },
					splash2Middle, 10221);
				waterMiddle.setScaledModel(new int[] { 49225 }, 220, 220, 220);

				whisperer.addDelayedAction(1, new Action((menuEntry3) -> {
					WorldPoint playerP2 = WorldPoint.fromLocalInstance(
						client, client.getLocalPlayer().getLocalLocation());
					WorldPoint[] splashPoints = new WorldPoint[] { splash1NW, splash2NW, splash1NE, splash2NE,
						splash1SE, splash2SE, splash1SW, splash2SW };
					for (WorldPoint splashPoint : splashPoints)
					{
						if (splashPoint.equals(playerP2))
						{
							showRedHitsplatWhispererUntilTick = client.getTickCount() + 1;
							return;
						}
						showBlueHitsplatWhispererUntilTick = client.getTickCount() + 1;
					}
				}));
			}));
		};

		Consumer<MenuEntry> rangedAttack = (menuEntry) -> {
			whisperer.setAnimation(10237);

			client.playSoundEffect(7455);
			client.playSoundEffect(7418);

			createProjectile(client, 2444, whisperer);
			whisperer.addDelayedAction(3, new Action(menuEntryA -> {
				if (client.getLocalPlayer().getOverheadIcon() != HeadIcon.RANGED)
				{
					showRedHitsplatWhispererUntilTick = client.getTickCount() + 1;
				}
				else
				{
					showBlueHitsplatWhispererUntilTick = client.getTickCount() + 1;
				}
			}));

			if (actionsPerformed.get() == 2)
			{
				whisperer.disableActiveLoopedAction();
				actionsPerformed.set(0);
				spawnTentacles.accept(menuEntry);
			}
			else
			{
				actionsPerformed.set(actionsPerformed.get() + 1);
			}
		};

		Consumer<MenuEntry> magicAttack = (menuEntry) -> {
			whisperer.setAnimation(10237);

			// TODO: SOUND?
			// 7374
			// 7322? - quite woosh
			// 7354?
			client.playSoundEffect(7354);

			createProjectile(client, 2445, whisperer);
			whisperer.addDelayedAction(3, new Action(menuEntryA -> {
				if (client.getLocalPlayer().getOverheadIcon() != HeadIcon.MAGIC)
				{
					showRedHitsplatWhispererUntilTick = client.getTickCount() + 1;
				}
				else
				{
					showBlueHitsplatWhispererUntilTick = client.getTickCount() + 1;
				}
			}));

			if (actionsPerformed.get() == 2)
			{
				whisperer.disableActiveLoopedAction();
				actionsPerformed.set(0);
				spawnTentacles.accept(menuEntry);
			}
			else
			{
				actionsPerformed.set(actionsPerformed.get() + 1);
			}
		};

		Consumer<MenuEntry> orbAttack = (menuEntry) -> {
			// Noise to tele
			// 7330
			// Wind-up
			// 7447
			// Spawn orbs
			// 7289
			// 7263 = crushed orb


			// Orbs objects, 47575, A 10271
			// Used blackstone, was yellow outline version
			// Light-green: 47573, A 10270
			// Dark-green: 47574, A: 10271
			// Orb stepped on: 2463 SpotAnim
		};

		AtomicInteger ticksBetweenActions = new AtomicInteger(1);
		whisperer.setName("The Whisperer");
		whisperer.setExamine("A fake Whisperer.");
		whisperer.addLoopedAction("Ranged attack", rangedAttack, ticksBetweenActions);
		whisperer.addLoopedAction("Magic attack", magicAttack, ticksBetweenActions);
		whisperer.setNeedToBeCloseToTalk(false);
		// Set display requirement

		RuneliteDialogStep whispererDialog = new RuneliteObjectDialogStep("Zoinkwiz",
			"Hi, this Whisperer is part of the Quest Helper plugin. You can use this Whisperer to see what its attacks look like.", NpcID.ELIZA, FaceAnimationIDs.CHATTY.getAnimationID());
		whispererDialog.addContinueDialog(new RuneliteObjectDialogStep("Zoinkwiz", "Right-click the Whisperer to see the various attacks you can test.", NpcID.ELIZA, FaceAnimationIDs.CHATTY.getAnimationID()))
			.addContinueDialog(new RuneliteObjectDialogStep("Zoinkwiz", "You can press the 'Stop' option to stop any actions.", NpcID.ELIZA, FaceAnimationIDs.CHATTY.getAnimationID()));
		whisperer.addDialogTree(null, whispererDialog);
		whisperer.addTalkAction(runeliteObjectManager);
		whisperer.setOrientationGoalAsPlayer(client);
		whisperer.setAlwaysFacePlayer(true);
		whisperer.setDisplayRequirement(new Conditions(
			nor(new ObjectCondition(ObjectID.TENTACLE_47586, new WorldPoint(2656, 6384, 0)),
					new ObjectCondition(ObjectID.TENTACLE, new WorldPoint(2656, 6384, 0))
			),
			// TODO: Determine if in boss fight?
			new VarbitRequirement(15126, 40, Operation.GREATER_EQUAL),
			new VarbitRequirement(15126, 42, Operation.LESS_EQUAL)
		));
	}

	private static void createProjectile(Client client, int projectileID, FakeNpc whisperer)
	{
		int tileHeight = client.getTileHeights()[0]
			[client.getLocalPlayer().getLocalLocation().getSceneX()]
			[client.getLocalPlayer().getLocalLocation().getSceneY()];

		LocalPoint lp = QuestPerspective.getInstanceLocalPointFromReal(client, whisperer.getWorldPoint());

		Projectile proj = client.createProjectile(projectileID,
			client.getPlane(),
			lp.getX(),
			lp.getY(),
			tileHeight - 348, // z coordinate
			client.getGameCycle(),  // start cycle
			client.getGameCycle() + 90,  // end cycle
			30, // slope ???
			128, // start height
			100, // end height
			client.getLocalPlayer(),
			client.getLocalPlayer().getLocalLocation().getX(),
			client.getLocalPlayer().getLocalLocation().getY()
		);
		client.getProjectiles()
			.addLast(proj);
	}
}
