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
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.playermadesteps.RuneliteDialogStep;
import com.questhelper.steps.playermadesteps.RuneliteObjectDialogStep;
import com.questhelper.steps.playermadesteps.extendedruneliteobjects.ExtendedRuneliteObject;
import com.questhelper.steps.playermadesteps.extendedruneliteobjects.ExtendedRuneliteObjects;
import com.questhelper.steps.playermadesteps.extendedruneliteobjects.FaceAnimationIDs;
import com.questhelper.steps.playermadesteps.extendedruneliteobjects.FakeGraphicsObject;
import com.questhelper.steps.playermadesteps.extendedruneliteobjects.FakeNpc;
import com.questhelper.steps.playermadesteps.extendedruneliteobjects.FakeObject;
import com.questhelper.steps.playermadesteps.extendedruneliteobjects.RuneliteObjectManager;
import com.questhelper.steps.playermadesteps.extendedruneliteobjects.actions.Action;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import net.runelite.api.Client;
import net.runelite.api.HeadIcon;
import net.runelite.api.MenuEntry;
import net.runelite.api.NpcID;
import net.runelite.api.Projectile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public class FakeLeviathan
{
	public static int showRedHitsplatFromLeviathanUntilTick = 0;
	public static int showBlueHitsplatFromLeviathanUntilTick = 0;
	public static void createLeviathan(Client client, QuestHelper qh, RuneliteObjectManager runeliteObjectManager)
	{
		FakeNpc leviathan = runeliteObjectManager.createFakeNpc(qh.toString(),
			new int[]{49285},
			new WorldPoint(2068, 6425, 0),
			10276);

		int MAX_ROCKS = 5;
		AtomicInteger currentRocks = new AtomicInteger(0);

		String BOULDERS_SUBGROUP = "leviathanBoulders";
		Consumer<MenuEntry> spawnBoulder = (menuEntry) -> {
			leviathan.setAnimation(10282);
			client.playSoundEffect(7043, 5);
			WorldPoint pPoint = client.getLocalPlayer().getWorldLocation();
			ExtendedRuneliteObjects objs =  runeliteObjectManager.addSubGroup(qh.toString(), BOULDERS_SUBGROUP);

			boolean skipRockSpawn = false;
			for (ExtendedRuneliteObject extendedRuneliteObject : objs.getExtendedRuneliteObjects())
			{
				if (pPoint.distanceTo(extendedRuneliteObject.getWorldPoint()) == 0)
				{
					skipRockSpawn = true;
					break;
				}
			}

			FakeObject rock = null;
			if (!skipRockSpawn && currentRocks.get() <= MAX_ROCKS)
			{
				currentRocks.set(currentRocks.get() + 1);
				rock = runeliteObjectManager.createFakeObject(BOULDERS_SUBGROUP, new int[]{49266}, pPoint, -1);
				rock.setEnabled(false);
				rock.disable();
			}
			FakeGraphicsObject fallingRock = runeliteObjectManager.createGraphicsFakeObject(BOULDERS_SUBGROUP, new int[]{49264},
				pPoint, 10318, rock);
			fallingRock.setOrientationGoal(512);
			fallingRock.getRuneliteObject().setOrientation(512);
			leviathan.addDelayedAction(4, new Action(menuEntryA -> {
				if (client.getLocalPlayer().getWorldLocation().distanceTo(pPoint) == 0)
				{
					showRedHitsplatFromLeviathanUntilTick = client.getTickCount() + 1;
				}
				else
				{
					showBlueHitsplatFromLeviathanUntilTick = client.getTickCount() + 1;
				}
			}));
		};

		Consumer<MenuEntry> rangedAttack = (menuEntry) -> {
			leviathan.setAnimation(10278, 10281);
			client.playSoundEffect(7023);
			createProjectile(client, 2487, leviathan);
			leviathan.addDelayedAction(4, new Action(menuEntryA -> {
				if (client.getLocalPlayer().getOverheadIcon() != HeadIcon.RANGED)
				{
					showRedHitsplatFromLeviathanUntilTick = client.getTickCount() + 1;
				}
				else
				{
					showBlueHitsplatFromLeviathanUntilTick = client.getTickCount() + 1;
				}
			}));
		};

		Consumer<MenuEntry> meleeAttack = (menuEntry) -> {
			leviathan.setAnimation(10278, 10281);
			client.playSoundEffect(7023);
			createProjectile(client, 2488, leviathan);
			leviathan.addDelayedAction(4, new Action(menuEntryA -> {
				if (client.getLocalPlayer().getOverheadIcon() != HeadIcon.MELEE)
				{
					showRedHitsplatFromLeviathanUntilTick = client.getTickCount() + 1;
				}
				else
				{
					showBlueHitsplatFromLeviathanUntilTick = client.getTickCount() + 1;
				}
			}));
		};

		Consumer<MenuEntry> magicAttack = (menuEntry) -> {
			leviathan.setAnimation(10278, 10281);
			client.playSoundEffect(7030);
			createProjectile(client, 2489, leviathan);
			leviathan.addDelayedAction(4, new Action(menuEntryA -> {
				if (client.getLocalPlayer().getOverheadIcon() != HeadIcon.MAGIC)
				{
					showRedHitsplatFromLeviathanUntilTick = client.getTickCount() + 1;
				}
				else
				{
					showBlueHitsplatFromLeviathanUntilTick = client.getTickCount() + 1;
				}
			}));
		};

		List<Consumer<MenuEntry>> p1Attacks = Arrays.asList(
			meleeAttack, magicAttack, rangedAttack
		);

		AtomicInteger actionsPerformed = new AtomicInteger(1);
		AtomicInteger attackRate = new AtomicInteger(5);
		AtomicInteger phase = new AtomicInteger(0);
		Consumer<MenuEntry> phase1 = (menuEntry) -> {
			int[] phaseShots = new int[]  { 4, 4, 5, 6, 8, 10 };
			int[] phaseDelays = new int[] { 5, 5, 4, 3, 2, 1 };
			if (actionsPerformed.get() != 0)
			{
				int rnd = new Random().nextInt(p1Attacks.size());
				p1Attacks.get(rnd).accept(menuEntry);
			}
			else
			{
				spawnBoulder.accept(menuEntry);
				phase.set(Math.min(phase.get() + 1, 5));
				attackRate.set(phaseDelays[phase.get()]);
			}
			actionsPerformed.set((actionsPerformed.get() + 1) % phaseShots[phase.get()]);
		};

		Consumer<MenuEntry> stopActions = (menuEntry) -> {
			runeliteObjectManager.removeGroup(BOULDERS_SUBGROUP);
			leviathan.disableActiveLoopedAction();
			phase.set(0);
			currentRocks.set(0);
		};

		leviathan.setName("The Leviathan");
		leviathan.setExamine("A fake leviathan.");
		leviathan.addAction("Stop", stopActions);
		leviathan.addAction("Ranged attack", rangedAttack);
		leviathan.addAction("Melee attack", meleeAttack);
		leviathan.addAction("Magic attack", magicAttack);
		leviathan.addAction("Boulder", spawnBoulder);
		leviathan.addLoopedAction("Phase 1", phase1, attackRate);
		leviathan.setNeedToBeCloseToTalk(false);
		// Set display requirement
		leviathan.setOrientationGoalAsPlayer(client);
		leviathan.setAlwaysFacePlayer(true);



		RuneliteDialogStep leviathanDialog = new RuneliteObjectDialogStep("Zoinkwiz", "Hi, this Leviathan is part of the Quest Helper plugin. You can use this Leviathan to see what its attacks look like, and practice reacting to them.", NpcID.ELIZA, FaceAnimationIDs.CHATTY.getAnimationID());
		leviathanDialog.addContinueDialog(new RuneliteObjectDialogStep("Zoinkwiz", "Right-click the Leviathan to see the various attacks and phases you can test.", NpcID.ELIZA, FaceAnimationIDs.CHATTY.getAnimationID()))
			.addContinueDialog(new RuneliteObjectDialogStep("Zoinkwiz", "You'll see a hitsplat on your character the moment the attack counts as hitting.", NpcID.ELIZA, FaceAnimationIDs.CHATTY.getAnimationID()))
			.addContinueDialog(new RuneliteObjectDialogStep("Zoinkwiz", "If you've reacted accordingly, by praying correctly or moving, the hitsplat will be blue. Otherwise, it'll be red.", NpcID.ELIZA, FaceAnimationIDs.CHATTY.getAnimationID()))
			.addContinueDialog(new RuneliteObjectDialogStep("Zoinkwiz", "You can press the 'Stop' option to delete boulders and stop the Leviathan from attacking.", NpcID.ELIZA, FaceAnimationIDs.CHATTY.getAnimationID()));
		leviathan.addDialogTree(null, leviathanDialog);
		leviathan.addTalkAction(runeliteObjectManager);
		leviathan.setDisplayRequirement(new Conditions(new VarbitRequirement(15128, 38, Operation.GREATER_EQUAL),
		new VarbitRequirement(15128, 40, Operation.LESS_EQUAL)));
	}

	private static void createProjectile(Client client, int projectileID, FakeNpc leviathan)
	{
		int tileHeight = client.getTileHeights()[0]
			[client.getLocalPlayer().getLocalLocation().getSceneX()]
			[client.getLocalPlayer().getLocalLocation().getSceneY()];

		LocalPoint lp = LocalPoint.fromWorld(client, leviathan.getWorldPoint().getX(), leviathan.getWorldPoint().getY());

		Projectile proj = client.createProjectile(projectileID,
			client.getPlane(),
			lp.getX(),
			lp.getY(),
			tileHeight - 30, // z coordinate
			client.getGameCycle(),  // start cycle
			client.getGameCycle() + 120,  // end cycle
			30, // slope ???
			124, // start height
			100, // end height
			client.getLocalPlayer(),
			client.getLocalPlayer().getLocalLocation().getX(),
			client.getLocalPlayer().getLocalLocation().getY()
		);
		client.getProjectiles()
			.addLast(proj);
	}

}
