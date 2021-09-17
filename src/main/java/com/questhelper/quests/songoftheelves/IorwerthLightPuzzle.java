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
package com.questhelper.quests.songoftheelves;

import com.questhelper.Zone;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

public class IorwerthLightPuzzle extends ConditionalStep
{
	Zone f0, f1, f2, f0SW, f2SW, f2E, f2NW, f2M, f2SE, f2S1, f2S2, f0NE1, f0NE2, f2NE;

	ItemRequirement handMirrorHighlighted, redCrystalHighlighted, fracturedCrystalHighlighted, magentaCrystalHighlighted,
		greenCrystalHighlighted, cyanCrystalHighlighted, yellowCrystalHighlighted, blueCrystalHighlighted;

	DetailedQuestStep resetPuzzle, p1Pillar1, p1Pillar2, p1Pillar3, p1Pillar4, p1Pillar5, p1Pillar6, p1Pillar7, p1Pillar8, p1Pillar9,
		p1Pillar10, p1Pillar11, p1Pillar12, p1Pillar13, p1Pillar14, p1Pillar15, p1Pillar16, p1Pillar17, p1Pillar18, p1Pillar19,
		p1Pillar20, p1Pillar21, p1Pillar22, p1Pillar23, p1Pillar24, p1Pillar25, p1Pillar26, p1Pillar27, p1Pillar28, p1Pillar29,
		p1Pillar30, p1Pillar31, p1Pillar32, collectMirrors, talkToAmlodd, climbBooks;

	DetailedQuestStep f0ToF1NW, f1ToF0NW, f2ToF1NW, f0ToF1Middle, f1ToF0SW, f0ToF1SW, f1ToF2SW, f2ToF1SW, f2ToF1, f1ToF2E, f2ToF1E,
		f1ToF2NW, f1ToF2SE, f2ToF1SE, f1ToF0NE, f0ToF1NE, f1ToF2NE;

	ConditionalStep goToF0NW, goToF0Middle, goToF0SW, goToF2SW, goToF2E, goToF2NW, goToF1, goToF2SE, goToF0NE, goToF2NE;

	Requirement hasMirrorsAndCrystal, onF1, onF2, onF0, onF0SW, onF2E, onF2SW, onF2NW, onF2M, onF2SE, onF2S, onF0NE, onF2NE,
		notResetTra, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24,
		r25, r26, r27, r28, r29, r30, r31;

	public IorwerthLightPuzzle(QuestHelper questHelper, ConditionalStep goToF1Steps, ConditionalStep goToF0Steps)
	{
		super(questHelper, goToF1Steps);
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();
		setupConditionalSteps();
		collectMirrors.addSubSteps(goToF1Steps);

		goToF0Middle = goToF0Steps;

		addStep(new Conditions(onF1, notResetTra), resetPuzzle);
		addStep(new Conditions(notResetTra), goToF1Steps);

		addStep(new Conditions(onF2NE, r31), p1Pillar32);
		addStep(new Conditions(onF2NE, r30), p1Pillar31);
		addStep(new Conditions(onF2NE, r29), p1Pillar30);
		addStep(new Conditions(onF2NE, r28), p1Pillar29);
		addStep(new Conditions(onF2NE, r27), p1Pillar28);
		addStep(new Conditions(onF2NE, r26), p1Pillar27);
		addStep(new Conditions(r26), goToF2NE);

		addStep(new Conditions(onF1, r25), p1Pillar26);
		addStep(new Conditions(onF1, r24), p1Pillar25);
		addStep(new Conditions(onF1, r23), p1Pillar24);
		addStep(new Conditions(onF1, r22), p1Pillar23);
		addStep(new Conditions(onF1, r21), p1Pillar22);
		addStep(new Conditions(onF1, r20), p1Pillar21);
		addStep(new Conditions(onF1, r19), p1Pillar20);
		addStep(new Conditions(onF1, r18), p1Pillar19);
		addStep(new Conditions(onF1, r17), p1Pillar18);
		addStep(new Conditions(onF1, r16), p1Pillar17);
		addStep(new Conditions(onF1, r15), p1Pillar16);
		addStep(new Conditions(r15), goToF1);

		addStep(new Conditions(onF0NE, r14), p1Pillar15);
		addStep(new Conditions(onF0NE, r13), p1Pillar14);
		addStep(new Conditions(onF0NE, r12), p1Pillar13);
		addStep(new Conditions(onF0NE, r11), p1Pillar12);
		addStep(new Conditions(onF0NE, r10), p1Pillar11);
		addStep(new Conditions(onF0NE, r9), p1Pillar10);
		addStep(new Conditions(onF0NE, r8), p1Pillar9);
		addStep(new Conditions(onF0NE, r7), p1Pillar8);
		addStep(new Conditions(onF0NE, r6), p1Pillar7);
		addStep(new Conditions(onF0NE, r5), p1Pillar6);
		addStep(new Conditions(onF0NE, r4), p1Pillar5);
		addStep(new Conditions(r4), goToF0NE);

		addStep(new Conditions(onF1, r3), p1Pillar4);
		addStep(new Conditions(onF1, r2), p1Pillar3);
		addStep(new Conditions(onF1, r1), p1Pillar2);
		addStep(new Conditions(onF1, hasMirrorsAndCrystal), p1Pillar1);
		addStep(onF1, collectMirrors);
	}

	protected void setupItemRequirements()
	{
		handMirrorHighlighted = new ItemRequirement("Hand mirror", ItemID.HAND_MIRROR_23775);
		handMirrorHighlighted.setHighlightInInventory(true);

		redCrystalHighlighted = new ItemRequirement("Red crystal", ItemID.RED_CRYSTAL_23776);
		redCrystalHighlighted.setHighlightInInventory(true);

		fracturedCrystalHighlighted = new ItemRequirement("Fractured crystal", ItemID.FRACTURED_CRYSTAL_23784);
		fracturedCrystalHighlighted.setHighlightInInventory(true);

		greenCrystalHighlighted = new ItemRequirement("Green crystal", ItemID.GREEN_CRYSTAL_23778);
		greenCrystalHighlighted.setHighlightInInventory(true);

		cyanCrystalHighlighted = new ItemRequirement("Cyan crystal", ItemID.CYAN_CRYSTAL_23779);
		cyanCrystalHighlighted.setHighlightInInventory(true);

		yellowCrystalHighlighted = new ItemRequirement("Yellow crystal", ItemID.YELLOW_CRYSTAL_23777);
		yellowCrystalHighlighted.setHighlightInInventory(true);

		blueCrystalHighlighted = new ItemRequirement("Blue crystal", ItemID.BLUE_CRYSTAL_23780);
		blueCrystalHighlighted.setHighlightInInventory(true);

		magentaCrystalHighlighted = new ItemRequirement("Magenta crystal", ItemID.MAGENTA_CRYSTAL_23781);
		magentaCrystalHighlighted.setHighlightInInventory(true);
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
		goToF1.addStep(onF0NE, f0ToF1NE);
		goToF1.addStep(onF0SW, f0ToF1SW);
		goToF1.addStep(onF0, f0ToF1NW);

		goToF0SW = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go down the south west stairs to the ground floor.");
		goToF0SW.addStep(onF1, f1ToF0SW);
		goToF0SW.addStep(onF0, f0ToF1Middle);

		goToF0NE = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go down the north east stairs to the ground floor.");
		goToF0NE.addStep(onF2NW, f2ToF1NW);
		goToF0NE.addStep(onF2SW, f2ToF1SW);
		goToF0NE.addStep(onF2SE, f2ToF1SE);
		goToF0NE.addStep(onF2E, f2ToF1E);
		goToF0NE.addStep(onF1, f1ToF0NE);
		goToF0NE.addStep(onF0SW, f0ToF1SW);
		goToF0NE.addStep(onF0, f0ToF1Middle);

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

		goToF2NW = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go back to the middle floor, then up to the top floor by the north west stairs.");
		goToF2NW.addStep(new Conditions(onF2SW, onF2S), f2ToF1SE);
		goToF2NW.addStep(onF2SW, f2ToF1SW);
		goToF2NW.addStep(onF2E, f2ToF1E);
		goToF2NW.addStep(onF1, f1ToF2NW);
		goToF2NW.addStep(onF0SW, f0ToF1SW);
		goToF2NW.addStep(onF0, f0ToF1Middle);

		goToF2SE = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go back to the middle floor, then up to the top floor by the south east stairs.");
		goToF2SE.addStep(onF2NW, f2ToF1NW);
		goToF2SE.addStep(onF2SW, f2ToF1SW);
		goToF2SE.addStep(onF2E, f2ToF1E);
		goToF2SE.addStep(onF1, f1ToF2SE);
		goToF2SE.addStep(onF0SW, f0ToF1SW);
		goToF2SE.addStep(onF0, f0ToF1Middle);

		goToF2NE = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go up to the top floor by the north east stairs.");
		goToF2NE.addStep(onF2SE, f2ToF1SE);
		goToF2NE.addStep(onF2NW, f2ToF1NW);
		goToF2NE.addStep(onF2SW, f2ToF1SW);
		goToF2NE.addStep(onF2E, f2ToF1E);
		goToF2NE.addStep(onF1, f1ToF2NE);
		goToF2NE.addStep(onF0SW, f0ToF1SW);
		goToF2NE.addStep(onF0, f0ToF1Middle);
	}

	protected void setupZones()
	{
		f0SW = new Zone(new WorldPoint(2566, 6115, 0), new WorldPoint(2588, 6150, 0));
		f0 = new Zone(new WorldPoint(2565, 6080, 0), new WorldPoint(2740, 6204, 0));
		f0NE1 = new Zone(new WorldPoint(2619,6166, 0), new WorldPoint(2680, 6201, 0));
		f0NE2 = new Zone(new WorldPoint(2636,6164, 0), new WorldPoint(2680, 6165, 0));

		f1 = new Zone(new WorldPoint(2565, 6080, 1), new WorldPoint(2740, 6204, 1));
		f2 = new Zone(new WorldPoint(2565, 6080, 2), new WorldPoint(2740, 6204, 2));
		f2SW = new Zone(new WorldPoint(2565, 6080, 2), new WorldPoint(2585, 6149, 2));
		f2E = new Zone(new WorldPoint(2617, 6124, 2), new WorldPoint(2680, 6165, 2));
		f2NW = new Zone(new WorldPoint(2566, 6150, 2), new WorldPoint(2601, 6203, 2));
		f2M = new Zone(new WorldPoint(2604, 6135, 2), new WorldPoint(2612, 6173, 2));
		f2SE = new Zone(new WorldPoint(2646, 6093, 2), new WorldPoint(2674, 6118, 2));
		f2S1 = new Zone(new WorldPoint(2608, 6093, 2), new WorldPoint(2645, 6109, 2));
		f2S2 = new Zone(new WorldPoint(2605, 6110, 2), new WorldPoint(2614, 6133, 2));
		f2NE = new Zone(new WorldPoint(2619, 6164, 2), new WorldPoint(2680, 6201, 2));

	}

	protected void setupSteps()
	{
		talkToAmlodd = new NpcStep(getQuestHelper(), NpcID.LORD_IEUAN_AMLODD, new WorldPoint(2353, 3179, 0), "Talk to Lord Amlodd in Lletya.");
		talkToAmlodd.addDialogStep("Yes.");

		collectMirrors = new ObjectStep(getQuestHelper(), ObjectID.CRYSTAL_DISPENSER_35076, new WorldPoint(2623, 6118, 1), "Collect all the items from the dispenser in the central room.");
		collectMirrors.setWorldMapPoint(new WorldPoint(2815, 6118, 1));
		collectMirrors.addDialogStep("Take everything.");

		climbBooks = new ObjectStep(getQuestHelper(), ObjectID.FLOATING_BOOK, new WorldPoint(2648, 6101, 2), "Climb across the books to the west.");
		climbBooks.setWorldMapPoint(new WorldPoint(3032, 6101, 1));

		resetPuzzle = new ObjectStep(getQuestHelper(), ObjectID.CRYSTAL_DISPENSER_35076, new WorldPoint(2623, 6118, 1), "Pull the lever in the dispenser in the central room.");
		resetPuzzle.setWorldMapPoint(new WorldPoint(2815, 6118, 1));
		resetPuzzle.addDialogSteps("Pull the lever.", "Pull it.");

		p1Pillar1 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_35303, new WorldPoint(2637, 6144, 1),
			"Add a cyan crystal to a pillar to the north east.", cyanCrystalHighlighted);
		p1Pillar1.setWorldMapPoint(new WorldPoint(2829, 6144, 1));
		p1Pillar1.addIcon(ItemID.CYAN_CRYSTAL_23779);

		p1Pillar2 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35305, new WorldPoint(2637, 6158, 1),
			"Add a blue crystal to a pillar to the north.", blueCrystalHighlighted);
		p1Pillar2.setWorldMapPoint(new WorldPoint(2829, 6158, 1));
		p1Pillar2.addIcon(ItemID.BLUE_CRYSTAL_23780);

		p1Pillar3 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35306, new WorldPoint(2637, 6172, 1),
			"Add a mirror to a pillar in the north east room. Rotate it to point the light west.", handMirrorHighlighted);
		p1Pillar3.setWorldMapPoint(new WorldPoint(2829, 6172, 1));
		p1Pillar3.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar4 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_35294, new WorldPoint(2623, 6172, 1),
			"Add a mirror to a pillar to the west. Rotate it to point the light down.", handMirrorHighlighted);
		p1Pillar4.setWorldMapPoint(new WorldPoint(2815, 6172, 1));
		p1Pillar4.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar5 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_36707, new WorldPoint(2623, 6172, 0),
			"Add a mirror to a pillar to the west. Rotate it to point the light north.", handMirrorHighlighted);
		p1Pillar5.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar6 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_36709, new WorldPoint(2623, 6186, 0),
			"Add the fractured crystal to a pillar to the north.", fracturedCrystalHighlighted);
		p1Pillar6.addIcon(ItemID.FRACTURED_CRYSTAL_23784);

		p1Pillar7 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_36711, new WorldPoint(2623, 6200, 0),
			"Add a mirror to a pillar to the north. Rotate it to point the light east.", handMirrorHighlighted);
		p1Pillar7.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar8 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_36713, new WorldPoint(2637, 6200, 0),
			"Add a mirror to a pillar to the east. Rotate it to point the light up.", handMirrorHighlighted);
		p1Pillar8.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar9 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35157, new WorldPoint(2637, 6186, 0),
			"Add a mirror to a pillar to the south. Rotate it to point the light south.", handMirrorHighlighted);
		p1Pillar9.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar10 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35156, new WorldPoint(2637, 6172, 0),
			"Add a mirror to a pillar to the south. Rotate it to point the light east.", handMirrorHighlighted);
		p1Pillar10.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar11 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35158, new WorldPoint(2651, 6172, 0),
			"Add a yellow crystal to a pillar to the east.", yellowCrystalHighlighted);
		p1Pillar11.addIcon(ItemID.YELLOW_CRYSTAL_23777);

		p1Pillar12 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35160, new WorldPoint(2665, 6172, 0),
			"Add a mirror to a pillar to the east. Rotate it to point the light north.", handMirrorHighlighted);
		p1Pillar12.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar13 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35161, new WorldPoint(2665, 6186, 0),
			"Add a mirror to a pillar to the north. Rotate it to point the light west.", handMirrorHighlighted);
		p1Pillar13.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar14 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35159, new WorldPoint(2651, 6186, 0),
			"Add a mirror to a pillar to the west. Rotate it to point the light north.", handMirrorHighlighted);
		p1Pillar14.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar15 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_36715, new WorldPoint(2651, 6200, 0),
			"Add a mirror to a pillar to the north. Rotate it to point the light up.", handMirrorHighlighted);
		p1Pillar15.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar16 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_35315, new WorldPoint(2651, 6200, 1),
			"Add a mirror to a pillar in the north east room. Rotate it to point the light south.", handMirrorHighlighted);
		p1Pillar16.setWorldMapPoint(new WorldPoint(2843, 6200, 1));
		p1Pillar16.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar17 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_35309, new WorldPoint(2637, 6200, 1),
			"Add a magenta crystal to a pillar in the north east room.", magentaCrystalHighlighted);
		p1Pillar17.setWorldMapPoint(new WorldPoint(2829, 6200, 1));
		p1Pillar17.addIcon(ItemID.MAGENTA_CRYSTAL_23781);

		p1Pillar18 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35312, new WorldPoint(2651, 6130, 1),
			"Add a mirror to a pillar in the east room. Rotate it to point the yellow light north.", handMirrorHighlighted);
		p1Pillar18.setWorldMapPoint(new WorldPoint(2843, 6130, 1));
		p1Pillar18.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar19 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35313, new WorldPoint(2651, 6144, 1),
			"Add a mirror to a pillar to the north. Rotate it to point the light east.", handMirrorHighlighted);
		p1Pillar19.setWorldMapPoint(new WorldPoint(2843, 6144, 1));
		p1Pillar19.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar20 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_35320, new WorldPoint(2665, 6144, 1),
			"Add a red crystal to the pillar to the east.", redCrystalHighlighted);
		p1Pillar20.setWorldMapPoint(new WorldPoint(2857, 6144, 1));
		p1Pillar20.addIcon(ItemID.RED_CRYSTAL_23776);

		p1Pillar21 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35330, new WorldPoint(2679, 6144, 1),
			"Add a mirror to a pillar to the east. Rotate it to point the light north.", handMirrorHighlighted);
		p1Pillar21.setWorldMapPoint(new WorldPoint(2871, 6144, 1));
		p1Pillar21.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar22 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35331, new WorldPoint(2679, 6158, 1),
			"Add a mirror to a pillar to the north. Rotate it to point the light west.", handMirrorHighlighted);
		p1Pillar22.setWorldMapPoint(new WorldPoint(2871, 6158, 1));
		p1Pillar22.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar23 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35322, new WorldPoint(2665, 6158, 1),
			"Add a mirror to a pillar to the west. Rotate it to point the light north.", handMirrorHighlighted);
		p1Pillar23.setWorldMapPoint(new WorldPoint(2857, 6158, 1));
		p1Pillar23.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar24 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35323, new WorldPoint(2665, 6172, 1),
			"Add a mirror to a pillar to the north. Rotate it to point the light east.", handMirrorHighlighted);
		p1Pillar24.setWorldMapPoint(new WorldPoint(2857, 6172, 1));
		p1Pillar24.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar25 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_35332, new WorldPoint(2679, 6172, 1),
			"Add a mirror to a pillar to the east. Rotate it to point the light north.", handMirrorHighlighted);
		p1Pillar25.setWorldMapPoint(new WorldPoint(2871, 6172, 1));
		p1Pillar25.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar26 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_35334, new WorldPoint(2679, 6186, 1),
			"Add a mirror to a pillar to the north. Rotate it to point the light up.", handMirrorHighlighted);
		p1Pillar26.setWorldMapPoint(new WorldPoint(2871, 6186, 1));
		p1Pillar26.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar27 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35133, new WorldPoint(2679, 6186, 2),
			"Add a mirror to a pillar to the north. Rotate it to point the light north.", handMirrorHighlighted);
		p1Pillar27.setWorldMapPoint(new WorldPoint(3063, 6186, 2));
		p1Pillar27.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar28 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35134, new WorldPoint(2679, 6200, 2),
			"Add a mirror to a pillar to the north. Rotate it to point the light west.", handMirrorHighlighted);
		p1Pillar28.setWorldMapPoint(new WorldPoint(3063, 6200, 2));
		p1Pillar28.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar29 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35128, new WorldPoint(2665, 6200, 2),
			"Add a mirror to a pillar to the west. Rotate it to point the light south.", handMirrorHighlighted);
		p1Pillar29.setWorldMapPoint(new WorldPoint(3049, 6200, 2));
		p1Pillar29.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar30 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35127, new WorldPoint(2665, 6186, 2),
			"Add a mirror to a pillar to the south. Rotate it to point the light down.", handMirrorHighlighted);
		p1Pillar30.setWorldMapPoint(new WorldPoint(3049, 6186, 2));
		p1Pillar30.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar31 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35112, new WorldPoint(2637, 6200, 2),
			"Add a mirror to a pillar to the west. Rotate it to point the light south.", handMirrorHighlighted);
		p1Pillar31.setWorldMapPoint(new WorldPoint(3021, 6200, 2));
		p1Pillar31.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar32 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35111, new WorldPoint(2637, 6186, 2),
			"Add a mirror to a pillar to the south. Rotate it to point the light down.", handMirrorHighlighted);
		p1Pillar32.setWorldMapPoint(new WorldPoint(3021, 6186, 2));
		p1Pillar32.addIcon(ItemID.HAND_MIRROR_23775);


		f0ToF1NW = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35387, new WorldPoint(2581, 6203, 0), "");

		f1ToF0NW = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35389, new WorldPoint(2581, 6203, 1), "");
		f1ToF0NW.addDialogStep("Climb down.");
		f1ToF0NW.setWorldMapPoint(new WorldPoint(2773, 6203, 1));

		f2ToF1NW = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35388, new WorldPoint(2581, 6203, 2), "");
		f2ToF1NW.setWorldMapPoint(new WorldPoint(2965, 6203, 2));

		f0ToF1NE = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35387, new WorldPoint(2662, 6166, 0), "");

		f1ToF2NW = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35389, new WorldPoint(2581, 6203, 1), "");
		f1ToF2NW.addDialogStep("Climb up.");
		f1ToF2NW.setWorldMapPoint(new WorldPoint(2773, 6203, 1));

		f0ToF1Middle = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35387, new WorldPoint(2626, 6153, 0), "");

		f1ToF0SW = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35389, new WorldPoint(2584, 6123, 1), "");
		f1ToF0SW.addDialogStep("Climb down.");
		f1ToF0SW.setWorldMapPoint(new WorldPoint(2776, 6123, 1));

		f1ToF0NE = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35388, new WorldPoint(2640, 6166, 1), "");
		f1ToF0NE.addDialogStep("Climb down.");
		f1ToF0NE.setWorldMapPoint(new WorldPoint(2832, 6166, 1));

		f0ToF1SW = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35387, new WorldPoint(2584, 6123, 0), "");

		f1ToF2SW = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35389, new WorldPoint(2584, 6123, 1), "");
		f1ToF2SW.addDialogStep("Climb up.");
		f1ToF2SW.setWorldMapPoint(new WorldPoint(2776, 6123, 1));

		f2ToF1SW = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35388, new WorldPoint(2584, 6123, 2), "");
		f2ToF1SW.addDialogStep("Climb down.");
		f2ToF1SW.setWorldMapPoint(new WorldPoint(2776, 6123, 1));

		f1ToF2NE = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35387, new WorldPoint(2668, 6166, 1), "");
		f1ToF2NE.addDialogStep("Climb up.");
		f1ToF2NE.setWorldMapPoint(new WorldPoint(2860, 6166, 1));

		f2ToF1 = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35388, new WorldPoint(2634, 6166, 2), "");
		f2ToF1.setWorldMapPoint(new WorldPoint(3017, 6154, 2));

		f1ToF2E = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35387, new WorldPoint(2682, 6144, 1), "");
		f1ToF2E.setWorldMapPoint(new WorldPoint(2874, 6144, 1));

		f2ToF1E = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35388, new WorldPoint(2682, 6144, 2), "");
		f2ToF1E.setWorldMapPoint(new WorldPoint(3066, 6144, 2));

		f1ToF2SE = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35387, new WorldPoint(2674, 6108, 1), "");
		f1ToF2SE.setWorldMapPoint(new WorldPoint(2866, 6108, 1));

		f2ToF1SE = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35388, new WorldPoint(2674, 6108, 2), "");
		f2ToF1SE.setWorldMapPoint(new WorldPoint(3056, 6108, 2));
	}

	protected void setupConditions()
	{
		hasMirrorsAndCrystal = new Conditions(
			new ItemRequirements(new ItemRequirement("Hand mirror", ItemID.HAND_MIRROR_23775, 14)),
			redCrystalHighlighted,
			fracturedCrystalHighlighted,
			yellowCrystalHighlighted
		);

		onF0 = new ZoneRequirement(f0);
		onF0SW = new ZoneRequirement(f0SW);
		onF0NE = new ZoneRequirement(f0NE1, f0NE2);

		onF1 = new ZoneRequirement(f1);

		onF2 = new ZoneRequirement(f2);
		onF2SW = new ZoneRequirement(f2SW);
		onF2E = new ZoneRequirement(f2E);
		onF2NW = new ZoneRequirement(f2NW);
		onF2M = new ZoneRequirement(f2M);
		onF2SE = new ZoneRequirement(f2SE);
		onF2S = new ZoneRequirement(f2S1, f2S2);
		onF2NE = new ZoneRequirement(f2NE);

		int CYAN = 2;
		int BLUE = 3;
		int RED = 5;
		int YELLOW = 6;
		int GREEN = 7;

		notResetTra = new VarbitRequirement(8773, YELLOW);

		r1 = new VarbitRequirement(8987, GREEN);
		r2 = new VarbitRequirement(8990, CYAN);
		r3 = new VarbitRequirement(9002, CYAN);
		r4 = new VarbitRequirement(8587, CYAN);
		r5 = new VarbitRequirement(8876, CYAN);
		r6 = new Conditions(
			new VarbitRequirement(8879, CYAN), // NORTH
			new VarbitRequirement(8878, CYAN)  // EAST
		);

		r7 = new Conditions(r6, new VarbitRequirement(8881, CYAN));
		r8 = new Conditions(r6, new VarbitRequirement(8591, CYAN));

		r9 = new Conditions(r8, new VarbitRequirement(8882, CYAN));
		r10 = new Conditions(r8, new VarbitRequirement(8883, CYAN));
		r11 = new Conditions(r8, new VarbitRequirement(8885, GREEN));
		r12 = new Conditions(r8, new VarbitRequirement(8886, GREEN));
		r13 = new Conditions(r8, new VarbitRequirement(8887, GREEN));
		r14 = new Conditions(r8, new VarbitRequirement(8888, GREEN));
		r15 = new Conditions(r8, new VarbitRequirement(8592, GREEN));
		r16 = new Conditions(r8, new VarbitRequirement(8992, GREEN));

		// Back to cyan beam
		r17 = new Conditions(r16, new VarbitRequirement(8614, BLUE));

		// Start on yellow beam
		r18 = new Conditions(r17, new VarbitRequirement(8974, YELLOW));
		r19 = new Conditions(r17, new VarbitRequirement(8982, YELLOW));
		r20 = new Conditions(r17, new VarbitRequirement(8983, RED));
		r21 = new Conditions(r17, new VarbitRequirement(8984, RED));
		r22 = new Conditions(r17, new VarbitRequirement(8985, RED));
		r23 = new Conditions(r17, new VarbitRequirement(8986, RED));
		r24 = new Conditions(r17, new VarbitRequirement(8995, RED));
		r25 = new Conditions(r17, new VarbitRequirement(8996, RED));
		r26 = new Conditions(r17, new VarbitRequirement(8625, RED));

		r27 = new Conditions(r17, new VarbitRequirement(8825, RED));
		r28 = new Conditions(r17, new VarbitRequirement(8827, RED));
		r29 = new Conditions(r17, new VarbitRequirement(8823, RED));
		r30 = new Conditions(r17, new VarbitRequirement(8619, RED));

		r31 = new Conditions(r30, new VarbitRequirement(8804, BLUE));
	}

	public List<QuestStep> getDisplaySteps()
	{
		return QuestUtil.toArrayList(resetPuzzle, collectMirrors, p1Pillar1, p1Pillar2, p1Pillar3, p1Pillar4, goToF0NE, p1Pillar5, p1Pillar6, p1Pillar7, p1Pillar8, p1Pillar9, p1Pillar10,
			p1Pillar11, p1Pillar12, p1Pillar13, p1Pillar14, goToF1, p1Pillar15, p1Pillar16, p1Pillar17, p1Pillar18, p1Pillar19, p1Pillar20, p1Pillar21, p1Pillar22, p1Pillar23,
			p1Pillar24, p1Pillar25, goToF2NE, p1Pillar26, p1Pillar27, p1Pillar28, p1Pillar29, p1Pillar30, p1Pillar31, p1Pillar32);
	}
}
