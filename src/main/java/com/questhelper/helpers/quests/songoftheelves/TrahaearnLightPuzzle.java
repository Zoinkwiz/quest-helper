/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.songoftheelves;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.steps.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import java.util.List;

public class TrahaearnLightPuzzle extends ConditionalStep
{
	Zone f0, f1, f2, f0SW, f2SW, f2E, f2NW, f2M, f2SE, f2S1, f2S2;

	ItemRequirement handMirrorHighlighted, redCrystalHighlighted, fracturedCrystalHighlighted,
		greenCrystalHighlighted, cyanCrystalHighlighted, yellowCrystalHighlighted, blueCrystalHighlighted;

	DetailedQuestStep resetPuzzle, p1Pillar1, p1Pillar2, p1Pillar3, p1Pillar4, p1Pillar5, p1Pillar6, p1Pillar7, p1Pillar8, p1Pillar9,
		p1Pillar10, p1Pillar11, p1Pillar12, p1Pillar13, p1Pillar14, p1Pillar15, p1Pillar16, p1Pillar17, p1Pillar18, p1Pillar19,
		p1Pillar20, p1Pillar21, p1Pillar22, p1Pillar23, collectMirrors, talkToAmlodd, climbBooks;

	DetailedQuestStep f0ToF1NW, f1ToF0NW, f2ToF1NW, f0ToF1Middle, f1ToF0SW, f0ToF1SW, f1ToF2SW, f2ToF1SW, f2ToF1, f1ToF2E, f2ToF1E,
		f1ToF2NW, f1ToF2SE, f2ToF1SE;

	ConditionalStep goToF0NW, goToF0SW, goToF2SW, goToF2E, goToF1, goToF2SE;

	Requirement hasMirrorsAndCrystal, onF1, onF2, onF0, onF0SW, onF2E, onF2SW, onF2NW, onF2M, onF2SE, onF2S,
		notResetHefin, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22;

	public TrahaearnLightPuzzle(QuestHelper questHelper, ConditionalStep goToF1Steps)
	{
		super(questHelper, goToF1Steps);
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();
		setupConditionalSteps();
		collectMirrors.addSubSteps(goToF1Steps);

		addStep(new Conditions(onF1, notResetHefin), resetPuzzle);
		addStep(new Conditions(notResetHefin), goToF1Steps);

		addStep(new Conditions(r22, onF2E), p1Pillar23);
		addStep(new Conditions(r21, onF2E), p1Pillar22);
		addStep(new Conditions(r20, onF2E), p1Pillar21);
		addStep(new Conditions(r19, onF2E), p1Pillar20);
		addStep(new Conditions(r19), goToF2E);

		addStep(new Conditions(r18, onF1), p1Pillar19);
		addStep(new Conditions(r17, onF1), p1Pillar18);
		addStep(new Conditions(r16, onF1), p1Pillar17);
		addStep(new Conditions(r16), goToF1);

		addStep(new Conditions(r15, new Conditions(LogicType.OR, onF2SE, onF2S)), p1Pillar16);
		addStep(new Conditions(r14, new Conditions(LogicType.OR, onF2SE, onF2S)), p1Pillar15);
		addStep(new Conditions(r13, new Conditions(LogicType.OR, onF2SE, onF2S)), p1Pillar14);
		addStep(new Conditions(r12, new Conditions(LogicType.OR, onF2SE, onF2S)), p1Pillar13);
		addStep(new Conditions(r11, new Conditions(LogicType.OR, onF2SE, onF2S)), p1Pillar12);
		addStep(new Conditions(r10, onF2S), p1Pillar11);
		addStep(new Conditions(r9, onF2S), p1Pillar10);
		addStep(new Conditions(r8, onF2S), p1Pillar9);
		addStep(new Conditions(r8, onF2SE), climbBooks);
		addStep(new Conditions(r8), goToF2SE);

		addStep(new Conditions(onF2SW, r7), p1Pillar8);
		addStep(new Conditions(onF2SW, r6), p1Pillar7);
		addStep(new Conditions(r6), goToF2SW);

		addStep(new Conditions(onF0SW, r5), p1Pillar6);
		addStep(new Conditions(onF0SW, r4), p1Pillar5);
		addStep(new Conditions(r4), goToF0SW);

		addStep(new Conditions(onF1, r3), p1Pillar4);
		addStep(new Conditions(onF1, r2), p1Pillar3);
		addStep(new Conditions(onF1, r1), p1Pillar2);
		addStep(new Conditions(onF1, hasMirrorsAndCrystal), p1Pillar1);
		addStep(onF1, collectMirrors);
	}

	protected void setupItemRequirements()
	{
		handMirrorHighlighted = new ItemRequirement("Hand mirror", ItemID.SOTE_MIRROR);
		handMirrorHighlighted.setHighlightInInventory(true);

		redCrystalHighlighted = new ItemRequirement("Red crystal", ItemID.SOTE_CRYSTAL_RED);
		redCrystalHighlighted.setHighlightInInventory(true);

		fracturedCrystalHighlighted = new ItemRequirement("Fractured crystal", ItemID.SOTE_CRYSTAL_FRACTURED);
		fracturedCrystalHighlighted.setHighlightInInventory(true);

		greenCrystalHighlighted = new ItemRequirement("Green crystal", ItemID.SOTE_CRYSTAL_GREEN);
		greenCrystalHighlighted.setHighlightInInventory(true);

		cyanCrystalHighlighted = new ItemRequirement("Cyan crystal", ItemID.SOTE_CRYSTAL_CYAN);
		cyanCrystalHighlighted.setHighlightInInventory(true);

		yellowCrystalHighlighted = new ItemRequirement("Yellow crystal", ItemID.SOTE_CRYSTAL_YELLOW);
		yellowCrystalHighlighted.setHighlightInInventory(true);

		blueCrystalHighlighted = new ItemRequirement("Blue crystal", ItemID.SOTE_CRYSTAL_BLUE);
		blueCrystalHighlighted.setHighlightInInventory(true);
	}

	protected void setupConditionalSteps()
	{
		goToF0NW = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go to the bottom floor of the library.");
		goToF0NW.addStep(onF1, f1ToF0NW);
		goToF0NW.addStep(onF2, f2ToF1NW);

		goToF1 = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go to the middle floor of the library.");
		goToF1.addStep(onF2E, f2ToF1E);
		goToF1.addStep(onF2NW, f2ToF1NW);
		goToF1.addStep(new Conditions(onF2S, onF2SE), f2ToF1SE);
		goToF1.addStep(onF2, f2ToF1NW);
		goToF1.addStep(onF0SW, f0ToF1SW);
		goToF1.addStep(onF0, f0ToF1NW);

		goToF0SW = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go down the south west stairs to the ground floor.");
		goToF0SW.addStep(onF1, f1ToF0SW);
		goToF0SW.addStep(onF0, f0ToF1Middle);

		goToF2SW = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go up to the top floor by the south west stairs.");
		goToF2SW.addStep(onF1, f1ToF2SW);
		goToF2SW.addStep(onF0SW, f0ToF1SW);
		goToF2SW.addStep(onF0, f0ToF1Middle);

		goToF2E = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go to the top floor by the east stairs.");
		goToF2E.addStep(new Conditions(onF2SW, onF2S), f2ToF1SE);
		goToF2E.addStep(onF2SW, f2ToF1SW);
		goToF2E.addStep(onF2, f2ToF1);
		goToF2E.addStep(onF1, f1ToF2E);
		goToF2E.addStep(onF0SW, f0ToF1SW);
		goToF2E.addStep(onF0, f0ToF1Middle);

		goToF2SE = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go back to the middle floor, then up to the top floor by the south east stairs.");
		goToF2SE.addStep(onF2NW, f2ToF1NW);
		goToF2SE.addStep(onF2SW, f2ToF1SW);
		goToF2SE.addStep(onF2E, f2ToF1E);
		goToF2SE.addStep(onF1, f1ToF2SE);
		goToF2SE.addStep(onF0SW, f0ToF1SW);
		goToF2SE.addStep(onF0, f0ToF1Middle);
	}

	protected void setupZones()
	{
		f0SW = new Zone(new WorldPoint(2566, 6115, 0), new WorldPoint(2588, 6150, 0));
		f0 = new Zone(new WorldPoint(2565, 6080, 0), new WorldPoint(2740, 6204, 0));
		f1 = new Zone(new WorldPoint(2565, 6080, 1), new WorldPoint(2740, 6204, 1));
		f2 = new Zone(new WorldPoint(2565, 6080, 2), new WorldPoint(2740, 6204, 2));
		f2SW = new Zone(new WorldPoint(2565, 6080, 2), new WorldPoint(2585, 6149, 2));
		f2E = new Zone(new WorldPoint(2617, 6124, 2), new WorldPoint(2680, 6165, 2));
		f2NW = new Zone(new WorldPoint(2566, 6150, 2), new WorldPoint(2601, 6203, 2));
		f2M = new Zone(new WorldPoint(2604, 6135, 2), new WorldPoint(2612, 6173, 2));
		f2SE = new Zone(new WorldPoint(2646, 6093, 2), new WorldPoint(2674, 6118, 2));
		f2S1 = new Zone(new WorldPoint(2608, 6093, 2), new WorldPoint(2645, 6109, 2));
		f2S2 = new Zone(new WorldPoint(2605, 6110, 2), new WorldPoint(2614, 6133, 2));
	}

	protected void setupSteps()
	{
		talkToAmlodd = new NpcStep(getQuestHelper(), NpcID.SOTE_LORD_AMLODD_VIS, new WorldPoint(2353, 3179, 0), "Talk to Lord Amlodd in Lletya.");
		talkToAmlodd.addDialogStep("Yes.");

		collectMirrors = new ObjectStep(getQuestHelper(), ObjectID.SOTE_LIBRARY_DISPENSER, new WorldPoint(2623, 6118, 1), "Collect all the items from the dispenser in the central room.");
		collectMirrors.addDialogStep("Take everything.");

		climbBooks = new ObjectStep(getQuestHelper(), ObjectID.SOTE_LIBRARY_HANDHOLD, new WorldPoint(2648, 6101, 2), "Climb across the books to the west.");

		resetPuzzle = new ObjectStep(getQuestHelper(), ObjectID.SOTE_LIBRARY_DISPENSER, new WorldPoint(2623, 6118, 1), "Pull the lever in the dispenser in the central room.");
		resetPuzzle.addDialogSteps("Pull the lever.", "Pull it.");

		p1Pillar1 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_C_1, new WorldPoint(2595, 6102, 1),
			"Add a mirror to a pillar to the south west. Rotate it to point the light south.", handMirrorHighlighted);
		p1Pillar1.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar2 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_C_0, new WorldPoint(2595, 6088, 1),
			"Add a mirror to the pillar to the south. Rotate it to point the light west.", handMirrorHighlighted);
		p1Pillar2.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar3 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_A_0, new WorldPoint(2567, 6088, 1),
			"Add a mirror to the pillar to the west. Rotate it to point the light north.", handMirrorHighlighted);
		p1Pillar3.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar4 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_A_2, new WorldPoint(2567, 6116, 1),
			"Run around to the north pillar and add a mirror. Rotate it to point the light down.", handMirrorHighlighted);
		p1Pillar4.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar5 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_0_A_2, new WorldPoint(2567, 6116, 0),
			"Add a mirror to the pillar to the south west. Rotate it to point the light east.", handMirrorHighlighted);
		p1Pillar5.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar6 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_0_B_2, new WorldPoint(2581, 6116, 0),
			"Add a mirror to the pillar to the east. Rotate it to point the light up.", handMirrorHighlighted);
		p1Pillar6.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar7 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_2_B_2, new WorldPoint(2581, 6116,2),
			"Add a mirror to the pillar to the south. Rotate it to point the light north.", handMirrorHighlighted);
		p1Pillar7.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar8 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_2_B_3, new WorldPoint(2581, 6130,2),
			"Add a mirror to the pillar to the north. Rotate it to point the light east.", handMirrorHighlighted);
		p1Pillar8.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar9 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_2_D_3, new WorldPoint(2609, 6130, 2),
			"Add a mirror to the pillar to the north west. Rotate it to point the light south.", handMirrorHighlighted);
		p1Pillar9.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar10 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_2_D_1, new WorldPoint(2609, 6102, 2),
			"Add a mirror to the pillar to the south. Rotate it to point the light east.", handMirrorHighlighted);
		p1Pillar10.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar11 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_2_F_1, new WorldPoint(2637, 6102, 2),
			"Add a blue crystal in the pillar to the east.", blueCrystalHighlighted);
		p1Pillar11.addIcon(ItemID.SOTE_CRYSTAL_BLUE);

		p1Pillar12 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_2_G_1, new WorldPoint(2651, 6102, 2),
			"Add a mirror to the pillar to the east. Rotate it to point the light south.", handMirrorHighlighted);
		p1Pillar12.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar13 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_2_G_0, new WorldPoint(2651, 6094, 2),
			"Add a mirror to the pillar to the south. Rotate it to point the light east.", handMirrorHighlighted);
		p1Pillar13.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar14 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_2_H_0, new WorldPoint(2665, 6094, 2),
			"Add a mirror to the pillar to the east. Rotate it to point the light north.", handMirrorHighlighted);
		p1Pillar14.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar15 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_2_H_2, new WorldPoint(2665, 6110, 2),
			"Add a mirror to the pillar to the north. Rotate it to point the light west.", handMirrorHighlighted);
		p1Pillar15.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar16 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_2_G_2, new WorldPoint(2651, 6110, 2),
			"Add a mirror to the pillar to the west. Rotate it to point the light north.", handMirrorHighlighted);
		p1Pillar16.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar17 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_G_3, new WorldPoint(2651, 6130, 1),
			"Add a mirror to the marked pillar. Rotate it to point the light north.", handMirrorHighlighted);
		p1Pillar17.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar18 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_G_4, new WorldPoint(2651, 6144, 1),
			"Add a mirror to the pillar to the north. Rotate it to point the light east.", handMirrorHighlighted);
		p1Pillar18.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar19 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_H_4, new WorldPoint(2665, 6144, 1),
			"Add a mirror to the pillar to the east. Rotate it to point the light up.", handMirrorHighlighted);
		p1Pillar19.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar20 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_2_H_4, new WorldPoint(2665, 6144, 2),
			"Add a mirror to the pillar to the west. Rotate it to point the light east.", handMirrorHighlighted);
		p1Pillar20.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar21 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_2_I_4, new WorldPoint(2679, 6144, 2),
			"Add a mirror to the pillar to the east. Rotate it to point the light north.", handMirrorHighlighted);
		p1Pillar21.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar22 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_2_I_5, new WorldPoint(2679, 6158, 2),
			"Add a mirror to the pillar to the north. Rotate it to point the light west.", handMirrorHighlighted);
		p1Pillar22.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar23 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_2_G_5, new WorldPoint(2651, 6158, 2),
			"Add a mirror to the pillar to the west. Rotate it to point the light south.", handMirrorHighlighted);
		p1Pillar23.addIcon(ItemID.SOTE_MIRROR);

		f0ToF1NW = new ObjectStep(getQuestHelper(), ObjectID.SOTE_WARPED_LIBRARY_TELEPORTER_UP, new WorldPoint(2581, 6203, 0), "");

		f1ToF0NW = new ObjectStep(getQuestHelper(), ObjectID.SOTE_WARPED_LIBRARY_TELEPORTER_BOTH, new WorldPoint(2581, 6203, 1), "");
		f1ToF0NW.addDialogStep("Climb down.");

		f2ToF1NW = new ObjectStep(getQuestHelper(), ObjectID.SOTE_WARPED_LIBRARY_TELEPORTER_DOWN, new WorldPoint(2581, 6203, 2), "");

		f1ToF2NW = new ObjectStep(getQuestHelper(), ObjectID.SOTE_WARPED_LIBRARY_TELEPORTER_BOTH, new WorldPoint(2581, 6203, 1), "");
		f1ToF2NW.addDialogStep("Climb up.");

		f0ToF1Middle = new ObjectStep(getQuestHelper(), ObjectID.SOTE_WARPED_LIBRARY_TELEPORTER_UP, new WorldPoint(2626, 6153, 0), "");

		f1ToF0SW = new ObjectStep(getQuestHelper(), ObjectID.SOTE_WARPED_LIBRARY_TELEPORTER_BOTH, new WorldPoint(2584, 6123, 1), "");
		f1ToF0SW.addDialogStep("Climb down.");

		f0ToF1SW = new ObjectStep(getQuestHelper(), ObjectID.SOTE_WARPED_LIBRARY_TELEPORTER_UP, new WorldPoint(2584, 6123, 0), "");

		f1ToF2SW = new ObjectStep(getQuestHelper(), ObjectID.SOTE_WARPED_LIBRARY_TELEPORTER_BOTH, new WorldPoint(2584, 6123, 1), "");
		f1ToF2SW.addDialogStep("Climb up.");

		f2ToF1SW = new ObjectStep(getQuestHelper(), ObjectID.SOTE_WARPED_LIBRARY_TELEPORTER_DOWN, new WorldPoint(2584, 6123, 2), "");
		f2ToF1SW.addDialogStep("Climb down.");

		f2ToF1 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_WARPED_LIBRARY_TELEPORTER_DOWN, new WorldPoint(2634, 6166, 2), "");

		f1ToF2E = new ObjectStep(getQuestHelper(), ObjectID.SOTE_WARPED_LIBRARY_TELEPORTER_UP, new WorldPoint(2682, 6144, 1), "");

		f2ToF1E = new ObjectStep(getQuestHelper(), ObjectID.SOTE_WARPED_LIBRARY_TELEPORTER_DOWN, new WorldPoint(2682, 6144, 2), "");

		f1ToF2SE = new ObjectStep(getQuestHelper(), ObjectID.SOTE_WARPED_LIBRARY_TELEPORTER_UP, new WorldPoint(2674, 6108, 1), "");

		f2ToF1SE = new ObjectStep(getQuestHelper(), ObjectID.SOTE_WARPED_LIBRARY_TELEPORTER_DOWN, new WorldPoint(2674, 6108, 2), "");
	}

	protected void setupConditions()
	{
		hasMirrorsAndCrystal = new Conditions(
			new ItemRequirements(new ItemRequirement("Hand mirror", ItemID.SOTE_MIRROR, 14)),
			redCrystalHighlighted,
			fracturedCrystalHighlighted,
			yellowCrystalHighlighted
		);

		onF0 = new ZoneRequirement(f0);
		onF0SW = new ZoneRequirement(f0SW);
		onF1 = new ZoneRequirement(f1);
		onF2 = new ZoneRequirement(f2);
		onF2SW = new ZoneRequirement(f2SW);
		onF2E = new ZoneRequirement(f2E);
		onF2NW = new ZoneRequirement(f2NW);
		onF2M = new ZoneRequirement(f2M);
		onF2SE = new ZoneRequirement(f2SE);
		onF2S = new ZoneRequirement(f2S1, f2S2);

		int WHITE = 1;
		int CYAN = 2;
		int BLUE = 3;
		int YELLOW = 6;
		int GREEN = 7;

		notResetHefin = new VarbitRequirement(VarbitID.SOTE_LIGHT_0_B_4_TO_0_B_3, BLUE);

		r1 = new VarbitRequirement(VarbitID.SOTE_LIGHT_1_C_0_TO_1_C_1, WHITE);
		r2 = new VarbitRequirement(VarbitID.SOTE_LIGHT_1_A_0_TO_1_C_0, WHITE);
		r3 = new VarbitRequirement(VarbitID.SOTE_LIGHT_1_A_0_TO_1_A_1, WHITE);
		r4 = new VarbitRequirement(VarbitID.SOTE_PILLAR_0_A_2_TO_1_A_2, GREEN);
		r5 = new VarbitRequirement(VarbitID.SOTE_LIGHT_0_A_2_TO_0_B_2, GREEN);
		r6 = new VarbitRequirement(VarbitID.SOTE_PILLAR_0_B_2_TO_1_B_2, GREEN);
		r7 = new VarbitRequirement(VarbitID.SOTE_LIGHT_2_B_2_TO_2_B_3, GREEN);
		r8 = new VarbitRequirement(VarbitID.SOTE_LIGHT_2_B_3_TO_2_D_3, GREEN);
		r9 = new VarbitRequirement(VarbitID.SOTE_LIGHT_2_D_1_TO_2_D_3, GREEN);
		r10 = new VarbitRequirement(VarbitID.SOTE_LIGHT_2_D_1_TO_2_F_1, GREEN);
		r11 = new VarbitRequirement(VarbitID.SOTE_LIGHT_2_F_1_TO_2_G_1, CYAN);
		r12 = new VarbitRequirement(VarbitID.SOTE_LIGHT_2_G_1_TO_2_G_0, CYAN);
		r13 = new VarbitRequirement(VarbitID.SOTE_LIGHT_2_G_0_TO_2_H_0, CYAN);
		r14 = new VarbitRequirement(VarbitID.SOTE_LIGHT_2_H_0_TO_2_H_2, CYAN);
		r15 = new VarbitRequirement(VarbitID.SOTE_LIGHT_2_H_2_TO_2_G_2, CYAN);
		r16 = new VarbitRequirement(VarbitID.SOTE_LIGHT_2_G_2_TO_2_G_3, CYAN);

		r17 = new VarbitRequirement(VarbitID.SOTE_LIGHT_1_G_3_TO_1_G_4, YELLOW);
		r18 = new VarbitRequirement(VarbitID.SOTE_LIGHT_1_G_4_TO_1_H_4, YELLOW);
		r19 = new VarbitRequirement(VarbitID.SOTE_PILLAR_1_H_4_TO_2_H_4, YELLOW);
		r20 = new VarbitRequirement(VarbitID.SOTE_LIGHT_2_H_4_TO_2_I_4, YELLOW);
		r21 = new VarbitRequirement(VarbitID.SOTE_LIGHT_2_I_4_TO_2_I_5, YELLOW);
		r22 = new VarbitRequirement(VarbitID.SOTE_LIGHT_2_I_5_TO_2_G_5, YELLOW);
	}

	public List<QuestStep> getDisplaySteps()
	{
		return QuestUtil.toArrayList(resetPuzzle, collectMirrors, p1Pillar1, p1Pillar2, p1Pillar3, p1Pillar4, goToF0SW, p1Pillar5,
			p1Pillar6, goToF2SW, p1Pillar7, goToF2SE, climbBooks, p1Pillar8, p1Pillar9, p1Pillar10, p1Pillar11, p1Pillar12,
			p1Pillar13, p1Pillar14, p1Pillar15, p1Pillar16, goToF1, p1Pillar17, p1Pillar18, p1Pillar19, goToF2E, p1Pillar20,
			p1Pillar21, p1Pillar22, p1Pillar23);
	}
}
