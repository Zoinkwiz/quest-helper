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
import com.questhelper.requirements.util.LogicType;
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

public class MeilyrLightPuzzle extends ConditionalStep
{
	Zone f0, f1, f2, f0SW, f2SW, f2E, f2NW, f2M;

	ItemRequirement handMirrorHighlighted, redCrystalHighlighted, fracturedCrystalHighlighted,
		greenCrystalHighlighted, cyanCrystalHighlighted, yellowCrystalHighlighted;

	DetailedQuestStep resetPuzzle, p1Pillar1, p1Pillar2, p1Pillar3, p1Pillar4, p1Pillar5, p1Pillar6, p1Pillar7, p1Pillar8, p1Pillar9,
		p1Pillar10, p1Pillar11, p1Pillar12, p1Pillar13, p1Pillar14, p1Pillar15, p1Pillar16, p1Pillar17, p1Pillar18, p1Pillar19,
		climbAcrossBooks, collectMirrors, talkToAmlodd;

	DetailedQuestStep f0ToF1NW, f1ToF0NW, f2ToF1NW, f0ToF1Middle, f1ToF0SW, f0ToF1SW, f1ToF2SW, f2ToF1SW, f2ToF1, f1ToF2E, f2ToF1E,
		f1ToF2NW;

	ConditionalStep goToF0NW, goToF1NW, goToF0Middle, goToF0SW, goToF1SW, goToF2SW, goDownToF1SW, goToF2E, goToF2NW;

	Requirement hasMirrorsAndCrystal, onF1, onF2, onF0, onF0SW, onF2E, onF2SW, onF2NW, onF2M, notResetAmlodd, r1, r2, r3, r4, r5, r6, r7,
		r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18;

	public MeilyrLightPuzzle(QuestHelper questHelper,ConditionalStep goToF1Steps, ConditionalStep goToF0Steps)
	{
		super(questHelper, goToF1Steps);
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();
		setupConditionalSteps();
		collectMirrors.addSubSteps(goToF1Steps);

		goToF0Middle = goToF0Steps;

		addStep(new Conditions(onF1, notResetAmlodd), resetPuzzle);
		addStep(new Conditions(notResetAmlodd), goToF1Steps);

		addStep(new Conditions(new Conditions(LogicType.OR, onF2M, onF2NW), r18), p1Pillar19);
		addStep(new Conditions(new Conditions(LogicType.OR, onF2M, onF2NW), r17), p1Pillar18);
		addStep(new Conditions(onF2M, r16), p1Pillar17);
		addStep(new Conditions(onF2M, r15), p1Pillar16);
		addStep(new Conditions(onF2NW, r15), climbAcrossBooks);
		addStep(new Conditions(r15), goToF2NW);

		addStep(new Conditions(onF2E, r14), p1Pillar15);
		addStep(new Conditions(onF2E, r13), p1Pillar14);
		addStep(new Conditions(r13), goToF2E);

		addStep(new Conditions(onF1, r12), p1Pillar13);
		addStep(new Conditions(r12), goDownToF1SW);

		addStep(new Conditions(onF2, r11), p1Pillar12);
		addStep(new Conditions(r11), goToF2SW);

		addStep(new Conditions(onF1, r10), p1Pillar11);
		addStep(new Conditions(r10), goToF1SW);

		addStep(new Conditions(onF0SW, r9), p1Pillar10);
		addStep(new Conditions(onF0SW, r8), p1Pillar9);
		addStep(new Conditions(r8), goToF0SW);
		addStep(new Conditions(onF0, r7), p1Pillar8);
		addStep(new Conditions(onF0, r6), p1Pillar7);
		addStep(new Conditions(onF0, r5), p1Pillar6);
		addStep(new Conditions(onF0, r4), p1Pillar5);
		addStep(new Conditions(onF0, r3), p1Pillar4);
		addStep(new Conditions(onF0, r2), p1Pillar3);
		addStep(new Conditions(r2), goToF0Middle);

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
	}

	protected void setupConditionalSteps()
	{
		goToF0NW = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go to the bottom floor of the library.");
		goToF0NW.addStep(onF1, f1ToF0NW);
		goToF0NW.addStep(onF2, f2ToF1NW);

		goToF1NW = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go to the middle floor of the library.");
		goToF1NW.addStep(onF2, f2ToF1NW);
		goToF1NW.addStep(onF0, f0ToF1NW);

		goToF0SW = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go back up to the middle floor, then back down the south west stairs.");
		goToF0SW.addStep(onF1, f1ToF0SW);
		goToF0SW.addStep(onF0, f0ToF1Middle);

		goToF1SW = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go back up to the middle floor.");
		goToF1SW.addStep(onF2, f2ToF1NW);
		goToF1SW.addStep(onF0SW, f0ToF1SW);
		goToF1SW.addStep(onF0, f0ToF1Middle);

		goToF2SW = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go up to the top floor by the south west stairs.");
		goToF2SW.addStep(onF1, f1ToF2SW);
		goToF2SW.addStep(onF0SW, f0ToF1SW);
		goToF2SW.addStep(onF0, f0ToF1Middle);

		goDownToF1SW = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go to the middle floor.");
		goDownToF1SW.addStep(onF2SW, f2ToF1SW);
		goDownToF1SW.addStep(onF2, f2ToF1);
		goDownToF1SW.addStep(onF0SW, f0ToF1SW);
		goDownToF1SW.addStep(onF0, f0ToF1Middle);

		goToF2E = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go to the top floor by the east stairs.");
		goToF2E.addStep(onF2SW, f2ToF1SW);
		goToF2E.addStep(onF2, f2ToF1);
		goToF2E.addStep(onF1, f1ToF2E);
		goToF2E.addStep(onF0SW, f0ToF1SW);
		goToF2E.addStep(onF0, f0ToF1Middle);

		goToF2NW = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go back to the middle floor, then up to the top floor by the north west stairs.");
		goToF2NW.addStep(onF2SW, f2ToF1SW);
		goToF2NW.addStep(onF2E, f2ToF1E);
		goToF2NW.addStep(onF1, f1ToF2NW);
		goToF2NW.addStep(onF0SW, f0ToF1SW);
		goToF2NW.addStep(onF0, f0ToF1Middle);
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
	}

	protected void setupSteps()
	{
		talkToAmlodd = new NpcStep(getQuestHelper(), NpcID.LORD_IEUAN_AMLODD, new WorldPoint(2353, 3179, 0), "Talk to Lord Amlodd in Lletya.");
		talkToAmlodd.addDialogStep("Yes.");

		collectMirrors = new ObjectStep(getQuestHelper(), ObjectID.CRYSTAL_DISPENSER_35076, new WorldPoint(2623, 6118, 1), "Collect all the items from the dispenser in the central room.");
		collectMirrors.setWorldMapPoint(new WorldPoint(2815, 6118, 1));
		collectMirrors.addDialogStep("Take everything.");

		resetPuzzle = new ObjectStep(getQuestHelper(), ObjectID.CRYSTAL_DISPENSER_35076, new WorldPoint(2623, 6118, 1), "Pull the lever in the dispenser in the central room.");
		resetPuzzle.setWorldMapPoint(new WorldPoint(2815, 6118, 1));
		resetPuzzle.addDialogSteps("Pull the lever.", "Pull it.");


		p1Pillar1 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35287, new WorldPoint(2609, 6158, 1),
			"Add a mirror to a pillar to the north. Rotate it to point the light east.", handMirrorHighlighted);
		p1Pillar1.setWorldMapPoint(new WorldPoint(2801, 6158, 1));
		p1Pillar1.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar2 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35293, new WorldPoint(2623, 6158, 1),
			"Add a mirror to a pillar to the east. Rotate it to point the light down.", handMirrorHighlighted);
		p1Pillar2.setWorldMapPoint(new WorldPoint(2815, 6158, 1));
		p1Pillar2.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar3 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_36705, new WorldPoint(2623, 6158, 0),
			"Add a mirror to the pillar near the stairs. Rotate it to point the light south.", handMirrorHighlighted);
		p1Pillar3.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar4 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35151, new WorldPoint(2623, 6130,0),
			"Add a mirror to the pillar to the south. Rotate it to point the light west.", handMirrorHighlighted);
		p1Pillar4.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar5 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35145, new WorldPoint(2609, 6130,0),
			"Add a mirror to the pillar to the west. Rotate it to point the light south.", handMirrorHighlighted);
		p1Pillar5.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar6 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35144, new WorldPoint(2609, 6116,0),
			"Add a mirror to the pillar to the south. Rotate it to point the light west.", handMirrorHighlighted);
		p1Pillar6.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar7 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35138, new WorldPoint(2595, 6116,0),
			"Add a mirror to the pillar to the west. Rotate it to point the light north.", handMirrorHighlighted);
		p1Pillar7.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar8 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35139, new WorldPoint(2595, 6130,0),
			"Add a mirror to the pillar to the north. Rotate it to point the light west.", handMirrorHighlighted);
		p1Pillar8.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar9 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35137, new WorldPoint(2581, 6130,0),
			"Add a mirror to the marked pillar. Rotate it to point the light north.", handMirrorHighlighted);
		p1Pillar9.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar10 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_8802, new WorldPoint(2581, 6144,0),
			"Add a mirror to the pillar to the north. Rotate it to point the light up.", handMirrorHighlighted);
		p1Pillar10.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar11 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_35264, new WorldPoint(2581, 6144,1),
			"Add a red crystal to the pillar to the north.", redCrystalHighlighted);
		p1Pillar11.setWorldMapPoint(new WorldPoint(2773, 6144, 1));
		p1Pillar11.addIcon(ItemID.RED_CRYSTAL_23776);

		p1Pillar12 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35089, new WorldPoint(2581, 6144,2),
			"Add a mirror to the pillar to the north. Rotate it to point the light north.", handMirrorHighlighted);
		p1Pillar12.setWorldMapPoint(new WorldPoint(2965, 6144, 2));
		p1Pillar12.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar13 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_35303, new WorldPoint(2637, 6144, 1),
			"Add a mirror to a pillar to the east. Rotate it to point the light up.", handMirrorHighlighted);
		p1Pillar13.setWorldMapPoint(new WorldPoint(2829, 6144, 1));
		p1Pillar13.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar14 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35108, new WorldPoint(2637, 6144, 2),
			"Add a mirror to a pillar to the west. Rotate it to point the light north.", handMirrorHighlighted);
		p1Pillar14.setWorldMapPoint(new WorldPoint(3021, 6144, 2));
		p1Pillar14.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar15 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35109, new WorldPoint(2637, 6158, 2),
			"Add a mirror to a pillar to the north. Rotate it to point the light west.", handMirrorHighlighted);
		p1Pillar15.setWorldMapPoint(new WorldPoint(3021, 6158, 2));
		p1Pillar15.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar16 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35101, new WorldPoint(2609, 6158, 2),
			"Add a mirror to the marked pillar. You'll need to climb across the floating books to reach it. Rotate it to point the light north.", handMirrorHighlighted);
		p1Pillar16.setWorldMapPoint(new WorldPoint(2993, 6158, 2));
		p1Pillar16.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar17 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35102, new WorldPoint(2609, 6172, 2),
			"Add a mirror to the pillar to the north. Rotate it to point the light west.", handMirrorHighlighted);
		p1Pillar17.setWorldMapPoint(new WorldPoint(2993, 6172, 2));
		p1Pillar17.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar18 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35095, new WorldPoint(2595, 6172, 2),
			"Add a mirror to the pillar to the west. Rotate it to point the light south.", handMirrorHighlighted);
		p1Pillar18.setWorldMapPoint(new WorldPoint(2979, 6172, 2));
		p1Pillar18.addIcon(ItemID.HAND_MIRROR_23775);

		p1Pillar19 = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_OF_LIGHT_35094, new WorldPoint(2595, 6158, 2),
			"Add a mirror to the pillar to the south. Rotate it to point the light west.", handMirrorHighlighted);
		p1Pillar19.setWorldMapPoint(new WorldPoint(2979, 6158, 2));
		p1Pillar19.addIcon(ItemID.HAND_MIRROR_23775);

		climbAcrossBooks = new ObjectStep(getQuestHelper(), ObjectID.FLOATING_BOOK, new WorldPoint(2598, 6173, 2),
			"Climb across the books to the south east.");
		climbAcrossBooks.setWorldMapPoint(new WorldPoint(2982, 6173, 2));

		f0ToF1NW = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35387, new WorldPoint(2581, 6203, 0), "");

		f1ToF0NW = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35389, new WorldPoint(2581, 6203, 1), "");
		f1ToF0NW.addDialogStep("Climb down.");
		f1ToF0NW.setWorldMapPoint(new WorldPoint(2773, 6203, 1));

		f2ToF1NW = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35388, new WorldPoint(2581, 6203, 2), "");
		f2ToF1NW.setWorldMapPoint(new WorldPoint(2965, 6203, 2));

		f1ToF2NW = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35389, new WorldPoint(2581, 6203, 1), "");
		f1ToF2NW.addDialogStep("Climb up.");
		f1ToF2NW.setWorldMapPoint(new WorldPoint(2773, 6203, 1));

		f0ToF1Middle = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35387, new WorldPoint(2626, 6153, 0), "");

		f1ToF0SW = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35389, new WorldPoint(2584, 6123, 1), "");
		f1ToF0SW.addDialogStep("Climb down.");
		f1ToF0SW.setWorldMapPoint(new WorldPoint(2776, 6123, 1));

		f0ToF1SW = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35387, new WorldPoint(2584, 6137, 0), "");

		f1ToF2SW = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35389, new WorldPoint(2584, 6137, 1), "");
		f1ToF2SW.addDialogStep("Climb up.");
		f1ToF2SW.setWorldMapPoint(new WorldPoint(2776, 6137, 1));

		f2ToF1SW = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35388, new WorldPoint(2584, 6137, 2), "");
		f2ToF1SW.addDialogStep("Climb up.");
		f2ToF1SW.setWorldMapPoint(new WorldPoint(2776, 6137, 1));

		f2ToF1 = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35388, new WorldPoint(2634, 6166, 2), "");
		f2ToF1.setWorldMapPoint(new WorldPoint(3017, 6154, 2));

		f1ToF2E = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35387, new WorldPoint(2682, 6144, 1), "");
		f1ToF2E.setWorldMapPoint(new WorldPoint(2874, 6154, 1));

		f2ToF1E = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_35388, new WorldPoint(2682, 6144, 2), "");
		f2ToF1E.setWorldMapPoint(new WorldPoint(3066, 6154, 2));
	}

	protected void setupConditions()
	{
		hasMirrorsAndCrystal = new Conditions(
			handMirrorHighlighted,
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

		int BLUE = 3;
		int MAGENTA = 4;
		int YELLOW = 6;
		int GREEN = 7;

		notResetAmlodd = new VarbitRequirement(8861, GREEN);

		r1 = new VarbitRequirement(8971, MAGENTA);
		r2 = new VarbitRequirement(8586, MAGENTA);
		r3 = new VarbitRequirement(8858, MAGENTA);
		r4 = new VarbitRequirement(8854, BLUE);
		r5 = new VarbitRequirement(8853, BLUE);
		r6 = new VarbitRequirement(8843, BLUE);
		r7 = new VarbitRequirement(8842, BLUE);
		r8 = new VarbitRequirement(8838, BLUE);
		r9 = new VarbitRequirement(8837, BLUE);
		r10 = new VarbitRequirement(8581, BLUE);
		r11 = new VarbitRequirement(8603, MAGENTA);
		r12 = new VarbitRequirement(8733, MAGENTA);
		r13 = new VarbitRequirement(8602, YELLOW);
		r14 = new VarbitRequirement(8764, YELLOW);
		r15 = new VarbitRequirement(8742, YELLOW);
		r16 = new VarbitRequirement(8741, YELLOW);
		r17 = new VarbitRequirement(8739, YELLOW);
		r18 = new VarbitRequirement(8738, YELLOW);
	}

	public List<QuestStep> getDisplaySteps()
	{
		return QuestUtil.toArrayList(resetPuzzle, collectMirrors, p1Pillar1, p1Pillar2, goToF0Middle, p1Pillar3, p1Pillar4, p1Pillar5,
			p1Pillar6, p1Pillar7, p1Pillar8, goToF0SW, p1Pillar9, p1Pillar10, goToF1SW, p1Pillar11, goToF2SW, p1Pillar12,
			goDownToF1SW, p1Pillar13, goToF2E, p1Pillar14, p1Pillar15, goToF2NW, climbAcrossBooks, p1Pillar16, p1Pillar17, p1Pillar18,
			p1Pillar19);
	}
}
