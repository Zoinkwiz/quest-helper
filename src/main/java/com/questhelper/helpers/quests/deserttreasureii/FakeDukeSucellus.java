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
import com.questhelper.requirements.player.InInstanceRequirement;
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
import java.util.function.Consumer;
import net.runelite.api.Client;
import net.runelite.api.HeadIcon;
import net.runelite.api.MenuEntry;
import net.runelite.api.NpcID;
import net.runelite.api.Projectile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public class FakeDukeSucellus
{
	public static int showRedHitsplatFromDukeUntilTick = 0;
	public static int showBlueHitsplatFromDukeUntilTick = 0;
	public static void createDuke(Client client, QuestHelper qh, RuneliteObjectManager runeliteObjectManager)
	{
		FakeNpc duke = runeliteObjectManager.createFakeNpc(qh.toString(),
			new int[] { 49194, 49193 },
			new WorldPoint(3039, 6455, 0),
			10175);
		String SPIKES_SUBGROUP = "dukeSpikes";
		Consumer<MenuEntry> meleeAttack = (menuEntry) -> {
			duke.setAnimation(10176);

			client.playSoundEffect(7177);

			FakeGraphicsObject iceBeforeSpike = runeliteObjectManager.createGraphicsFakeObject(SPIKES_SUBGROUP, new int[]{ 48346 },
				new WorldPoint(duke.getWorldPoint().getX(), duke.getWorldPoint().getY() - 4, 0), 10210);
			FakeGraphicsObject iceBeforeSpike2 = runeliteObjectManager.createGraphicsFakeObject(SPIKES_SUBGROUP, new int[]{ 48346 },
				new WorldPoint(duke.getWorldPoint().getX() - 1, duke.getWorldPoint().getY() - 4, 0), 10210);
			FakeGraphicsObject iceBeforeSpike3 = runeliteObjectManager.createGraphicsFakeObject(SPIKES_SUBGROUP, new int[]{ 48346 },
				new WorldPoint(duke.getWorldPoint().getX() + 1, duke.getWorldPoint().getY() - 4, 0), 10210);
			FakeGraphicsObject iceBeforeSpike4 = runeliteObjectManager.createGraphicsFakeObject(SPIKES_SUBGROUP, new int[]{ 48346 },
				new WorldPoint(duke.getWorldPoint().getX() - 2, duke.getWorldPoint().getY() - 4, 0), 10210);
			FakeGraphicsObject iceBeforeSpike5 = runeliteObjectManager.createGraphicsFakeObject(SPIKES_SUBGROUP, new int[]{ 48346 },
				new WorldPoint(duke.getWorldPoint().getX() + 2, duke.getWorldPoint().getY() - 4, 0), 10210);
			FakeGraphicsObject iceBeforeSpike6 = runeliteObjectManager.createGraphicsFakeObject(SPIKES_SUBGROUP, new int[]{ 48346 },
				new WorldPoint(duke.getWorldPoint().getX() - 3, duke.getWorldPoint().getY() - 4, 0), 10210);
			FakeGraphicsObject iceBeforeSpike7 = runeliteObjectManager.createGraphicsFakeObject(SPIKES_SUBGROUP, new int[]{ 48346 },
				new WorldPoint(duke.getWorldPoint().getX() + 3, duke.getWorldPoint().getY() - 4, 0), 10210);

			iceBeforeSpike.addDelayedAction(2, new Action(menuEntryA -> {
				client.playSoundEffect(7224);
			}));
		};

		Consumer<MenuEntry> magicAttack = (menuEntry) -> {
			// TODO: Verify sound + animation + projectileID
			duke.setAnimation(10177);
			// 7161?
			client.playSoundEffect(7214);
			createProjectile(client, 2434, duke);
			duke.addDelayedAction(3, new Action(menuEntryA -> {
				if (client.getLocalPlayer().getOverheadIcon() != HeadIcon.MAGIC)
				{
					showRedHitsplatFromDukeUntilTick = client.getTickCount() + 1;
				}
				else
				{
					showBlueHitsplatFromDukeUntilTick = client.getTickCount() + 1;
				}
			}));
		};

		Consumer<MenuEntry> stareAttack = (menuEntry) -> {
			duke.setAnimation(10180);
			duke.addDelayedAction(3, new Action(menuEntryA -> {

			}));
			// TODO: Check sound
			client.playSoundEffect(7215);

			// Have pillar to hide behind?
		};

		String GAS_CLOUD = "gasCloud";
		Consumer<MenuEntry> poisonAttack = (menuEntry) -> {
			duke.setAnimation(10178);
			client.playSoundEffect(7248);
			createPoisonProjectile(client, 2436, duke);
			duke.addDelayedAction(3, new Action(menuEntryA -> {
				runeliteObjectManager.createGraphicsFakeObject(GAS_CLOUD, new int[] { 49202 }, new WorldPoint(3036, 6450, 0), 10196);
			}));
		};

		Consumer<MenuEntry> stopActions = (menuEntry) -> {
			runeliteObjectManager.removeGroup(SPIKES_SUBGROUP);
			duke.disableActiveLoopedAction();
		};

		duke.setName("Duke Sucellus");
		duke.setExamine("A fake Duke.");
		duke.addAction("Melee attack", meleeAttack);
		duke.addAction("Magic attack", magicAttack);
		duke.addAction("Stop", stopActions);
		duke.addAction("Stare attack", stareAttack);
		duke.addAction("Poison attack", poisonAttack);
		duke.setNeedToBeCloseToTalk(false);
		// Set display requirement

		RuneliteDialogStep dukeDialog = new RuneliteObjectDialogStep("Zoinkwiz",
			"Hi, this Duke is part of the Quest Helper plugin. You can use this Duke to see what its attacks look like.", NpcID.ELIZA, FaceAnimationIDs.CHATTY.getAnimationID());
		dukeDialog.addContinueDialog(new RuneliteObjectDialogStep("Zoinkwiz", "Right-click the Duke to see the various attacks you can test.", NpcID.ELIZA, FaceAnimationIDs.CHATTY.getAnimationID()))
			.addContinueDialog(new RuneliteObjectDialogStep("Zoinkwiz", "You can press the 'Stop' option to stop any actions.", NpcID.ELIZA, FaceAnimationIDs.CHATTY.getAnimationID()));
		duke.addDialogTree(null, dukeDialog);
		duke.addTalkAction(runeliteObjectManager);
		duke.getRuneliteObject().setDrawFrontTilesFirst(true);
		duke.setDisplayRequirement(new Conditions(
			nor(new InInstanceRequirement()),
			new VarbitRequirement(15127, 60, Operation.GREATER_EQUAL),
			new VarbitRequirement(15127, 62, Operation.LESS_EQUAL)
		));
	}

	private static void createProjectile(Client client, int projectileID, FakeNpc duke)
	{
		int tileHeight = client.getTileHeights()[0]
			[client.getLocalPlayer().getLocalLocation().getSceneX()]
			[client.getLocalPlayer().getLocalLocation().getSceneY()];

		LocalPoint lp = LocalPoint.fromWorld(client, duke.getWorldPoint().getX(), duke.getWorldPoint().getY());

		Projectile proj = client.createProjectile(projectileID,
			client.getPlane(),
			lp.getX(),
			lp.getY(),
			tileHeight - 30, // z coordinate
			client.getGameCycle(),  // start cycle
			client.getGameCycle() + 90,  // end cycle
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

	private static void createPoisonProjectile(Client client, int projectileID, FakeNpc duke)
	{
		LocalPoint lp = LocalPoint.fromWorld(client, duke.getWorldPoint().getX(), duke.getWorldPoint().getY());
		int tileHeight = client.getTileHeights()[0]
			[lp.getSceneX()]
			[lp.getSceneY()];
		LocalPoint goalLp = LocalPoint.fromWorld(client, new WorldPoint(3036, 6450, 0));
		if (goalLp == null) return;

		Projectile proj = client.createProjectile(projectileID,
			client.getPlane(),
			lp.getX(),
			lp.getY(),
			tileHeight - 30, // z coordinate
			client.getGameCycle(),  // start cycle
			client.getGameCycle() + 90,  // end cycle
			30, // slope ???
			124, // start height
			100, // end height
			null,
			goalLp.getX(),
			goalLp.getY()
		);
		client.getProjectiles()
			.addLast(proj);
	}
}
