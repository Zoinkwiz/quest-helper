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

public class AmloddLightPuzzle extends ConditionalStep
{
	Zone f0, f1, f2;

	ItemRequirement handMirrorHighlighted, redCrystalHighlighted, fracturedCrystalHighlighted,
		greenCrystalHighlighted, cyanCrystalHighlighted, yellowCrystalHighlighted;

	DetailedQuestStep resetPuzzle, p1Pillar1, p1Pillar2, p1Pillar3, p1Pillar4, p1Pillar5, p1Pillar6, p1Pillar7, p1Pillar8, p1Pillar9,
		p1Pillar10, p1Pillar11, p1Pillar12, p1Pillar13, p1Pillar14, p1Pillar15, p1Pillar16, p1Pillar17, p1Pillar18, p1Pillar19, p1Pillar20,
		collectMirrors, talkToAmlodd;

	DetailedQuestStep goF0ToF1NW, goF1ToF0NW, goF2ToF1NW;

	ConditionalStep goToF0NW, goToF1NW, goToF0Middle;

	Requirement hasMirrorsAndCrystal, onF1, onF2, onF0, notResetCrwys, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15,
		r16, r17, r18, r19;

	public AmloddLightPuzzle(QuestHelper questHelper, ConditionalStep goToF1Steps, ConditionalStep goToF0Steps)
	{
		super(questHelper, goToF1Steps);
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();
		setupConditionalSteps();
		collectMirrors.addSubSteps(goToF1Steps);

		goToF0Middle = goToF0Steps;

		addStep(new Conditions(onF1, notResetCrwys), resetPuzzle);
		addStep(new Conditions(notResetCrwys), goToF1Steps);
		addStep(new Conditions(onF0, r19), p1Pillar20);
		addStep(new Conditions(onF0, r18), p1Pillar19);
		addStep(new Conditions(onF0, r17), p1Pillar18);
		addStep(new Conditions(onF0, r16), p1Pillar17);
		addStep(new Conditions(onF0, r15), p1Pillar16);
		addStep(new Conditions(onF0, r14), p1Pillar15);
		addStep(new Conditions(onF0, r13), p1Pillar14);
		addStep(new Conditions(onF0, r12), p1Pillar13);
		addStep(new Conditions(onF0, r11), p1Pillar12);
		addStep(new Conditions(onF0, r10), p1Pillar11);
		addStep(new Conditions(r10), goToF0Middle);

		addStep(new Conditions(onF1, r9), p1Pillar10);
		addStep(new Conditions(onF1, r8), p1Pillar9);
		addStep(new Conditions(r8), goToF1NW);

		addStep(new Conditions(onF0, r7), p1Pillar8);
		addStep(new Conditions(r7), goToF0NW);

		addStep(new Conditions(onF1, r6), p1Pillar7);
		addStep(new Conditions(onF1, r5), p1Pillar6);
		addStep(new Conditions(onF1, r4), p1Pillar5);
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
	}

	protected void setupConditionalSteps()
	{
		goToF0NW = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go to the bottom floor of the library.");
		goToF0NW.addStep(onF1, goF1ToF0NW);
		goToF0NW.addStep(onF2, goF2ToF1NW);

		goToF1NW = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go to the middle floor of the library.");
		goToF1NW.addStep(onF2, goF2ToF1NW);
		goToF1NW.addStep(onF0, goF0ToF1NW);
	}

	protected void setupZones()
	{
		f0 = new Zone(new WorldPoint(2565, 6080, 0), new WorldPoint(2740, 6204, 0));
		f1 = new Zone(new WorldPoint(2565, 6080, 1), new WorldPoint(2740, 6204, 1));
		f2 = new Zone(new WorldPoint(2565, 6080, 2), new WorldPoint(2740, 6204, 2));
	}

	protected void setupSteps()
	{
		talkToAmlodd = new NpcStep(getQuestHelper(), NpcID.SOTE_LORD_AMLODD_VIS, new WorldPoint(2353, 3179, 0), "Talk to Lord Amlodd in Lletya.");
		talkToAmlodd.addDialogStep("Yes.");

		collectMirrors = new ObjectStep(getQuestHelper(), ObjectID.SOTE_LIBRARY_DISPENSER, new WorldPoint(2623, 6118, 1), "Collect all the items from the dispenser in the central room.");
		collectMirrors.addDialogStep("Take everything.");

		resetPuzzle = new ObjectStep(getQuestHelper(), ObjectID.SOTE_LIBRARY_DISPENSER, new WorldPoint(2623, 6118, 1), "Pull the lever in the dispenser in the central room.");
		resetPuzzle.addDialogSteps("Pull the lever.", "Pull it.");

		p1Pillar1 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_D_4, new WorldPoint(2609, 6144, 1),
			"Add a fractured crystal to the pillar to the north.", fracturedCrystalHighlighted);
		p1Pillar1.addIcon(ItemID.SOTE_CRYSTAL_FRACTURED);

		p1Pillar2 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_C_4, new WorldPoint(2595, 6144, 1),
			"Add a mirror to the pillar to the west. Rotate it to point the light north.", handMirrorHighlighted);
		p1Pillar2.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar3 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_C_5, new WorldPoint(2595, 6158, 1),
			"Add a red crystal to the pillar to the north.", redCrystalHighlighted);
		p1Pillar3.addIcon(ItemID.SOTE_CRYSTAL_RED);

		p1Pillar4 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_C_6, new WorldPoint(2595, 6172, 1),
			"Add a mirror to the pillar to the north. Rotate it to point the light east.", handMirrorHighlighted);
		p1Pillar4.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar5 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_D_6, new WorldPoint(2609, 6172, 1),
			"Add a mirror to the pillar to the east. Rotate it to point the light north.", handMirrorHighlighted);
		p1Pillar5.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar6 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_D_7, new WorldPoint(2609, 6186, 1),
			"Add a mirror to the pillar to the north. Rotate it to point the light west.", handMirrorHighlighted);
		p1Pillar6.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar7 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_C_7, new WorldPoint(2595, 6186, 1),
			"Add a mirror to the pillar to the west. Rotate it to point the light down.", handMirrorHighlighted);
		p1Pillar7.addIcon(ItemID.SOTE_MIRROR);

		// Go to F0

		p1Pillar8 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_0_C_7, new WorldPoint(2595, 6186, 0),
			"Add a mirror to the pillar with light coming down into it. Rotate it to point the light south.", handMirrorHighlighted);
		p1Pillar8.addIcon(ItemID.SOTE_MIRROR);

		// Go to F1

		p1Pillar9 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_D_5, new WorldPoint(2609, 6158, 1),
			"Add a mirror to a pillar to the north of where you placed the fractured crystal. Rotate it to point the light east.", handMirrorHighlighted);
		p1Pillar9.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar10 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_E_5, new WorldPoint(2623, 6158, 1),
			"Add a mirror to a pillar to the east. Rotate it to point the light down.", handMirrorHighlighted);
		p1Pillar10.addIcon(ItemID.SOTE_MIRROR);

		// Go to F0 from middle

		p1Pillar11 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_0_E_5, new WorldPoint(2623, 6158, 0),
			"Add a mirror to the pillar near the stairs. Rotate it to point the light south.", handMirrorHighlighted);
		p1Pillar11.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar12 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_0_E_3, new WorldPoint(2623, 6130,0),
			"Add a mirror to the pillar to the south. Rotate it to point the light west.", handMirrorHighlighted);
		p1Pillar12.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar13 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_0_D_3, new WorldPoint(2609, 6130,0),
			"Add a mirror to the pillar to the west. Rotate it to point the light south.", handMirrorHighlighted);
		p1Pillar13.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar14 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_0_D_2, new WorldPoint(2609, 6116,0),
			"Add a mirror to the pillar to the south. Rotate it to point the light west.", handMirrorHighlighted);
		p1Pillar14.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar15 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_0_C_2, new WorldPoint(2595, 6116,0),
			"Add a mirror to the pillar to the west. Rotate it to point the light north.", handMirrorHighlighted);
		p1Pillar15.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar16 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_0_C_3, new WorldPoint(2595, 6130,0),
			"Add a green crystal to the pillar to the north.", greenCrystalHighlighted);
		p1Pillar16.addIcon(ItemID.SOTE_CRYSTAL_GREEN);

		p1Pillar17 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_0_C_4, new WorldPoint(2595, 6144,0),
			"Add a yellow crystal to the pillar to the north.", yellowCrystalHighlighted);
		p1Pillar17.addIcon(ItemID.SOTE_CRYSTAL_YELLOW);

		p1Pillar18 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_0_C_5, new WorldPoint(2595, 6158,0),
			"Add a mirror to the pillar to the north. Rotate it to point the light east.", handMirrorHighlighted);
		p1Pillar18.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar19 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_0_C_6, new WorldPoint(2595, 6172,0),
			"Add a mirror to the pillar to the north. Rotate it to point the light east.", handMirrorHighlighted);
		p1Pillar19.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar20 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_0_D_6, new WorldPoint(2609, 6172,0),
			"Add a mirror to the pillar to the east. Rotate it to point the light south.", handMirrorHighlighted);
		p1Pillar20.addIcon(ItemID.SOTE_MIRROR);

		goF0ToF1NW = new ObjectStep(getQuestHelper(), ObjectID.SOTE_WARPED_LIBRARY_TELEPORTER_UP, new WorldPoint(2581, 6203, 0), "");

		goF1ToF0NW = new ObjectStep(getQuestHelper(), ObjectID.SOTE_WARPED_LIBRARY_TELEPORTER_BOTH, new WorldPoint(2581, 6203, 1), "");
		goF1ToF0NW.addDialogStep("Climb down.");

		goF2ToF1NW = new ObjectStep(getQuestHelper(), ObjectID.SOTE_WARPED_LIBRARY_TELEPORTER_DOWN, new WorldPoint(2581, 6203, 2), "");
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
		onF1 = new ZoneRequirement(f1);
		onF2 = new ZoneRequirement(f2);

		int CYAN = 2;
		int BLUE = 3;
		int MAGENTA = 4;
		int RED = 5;
		int GREEN = 7;

		notResetCrwys = new VarbitRequirement(VarbitID.SOTE_LIGHT_1_B_5_TO_1_A_5, MAGENTA);

		r1 = new Conditions(
		new VarbitRequirement(VarbitID.SOTE_LIGHT_1_C_4_TO_1_D_4, MAGENTA), // North
		new VarbitRequirement(VarbitID.SOTE_LIGHT_1_D_4_TO_1_D_5, MAGENTA)  // West
		);
		r2 = new Conditions(new VarbitRequirement(VarbitID.SOTE_LIGHT_1_C_4_TO_1_C_5, MAGENTA), r1);
		r3 = new Conditions(new VarbitRequirement(VarbitID.SOTE_LIGHT_1_C_5_TO_1_C_6, RED), r1);
		r4 = new Conditions(new VarbitRequirement(VarbitID.SOTE_LIGHT_1_C_6_TO_1_D_6, RED), r1);
		r5 = new Conditions(new VarbitRequirement(VarbitID.SOTE_LIGHT_1_D_6_TO_1_D_7, RED), r1);
		r6 = new Conditions(new VarbitRequirement(VarbitID.SOTE_LIGHT_1_D_7_TO_1_C_7, RED), r1);
		r7 = new Conditions(new VarbitRequirement(VarbitID.SOTE_PILLAR_0_C_7_TO_1_C_7, RED), r1);
		r8 = new Conditions(new VarbitRequirement(VarbitID.SOTE_LIGHT_0_C_6_TO_0_C_7, RED), r1);
		r9 = new Conditions(new VarbitRequirement(VarbitID.SOTE_LIGHT_1_D_5_TO_1_E_5, MAGENTA), r1);
		r10 = new Conditions(new VarbitRequirement(VarbitID.SOTE_PILLAR_0_E_5_TO_1_E_5, MAGENTA), r1);
		r11 = new Conditions(new VarbitRequirement(VarbitID.SOTE_LIGHT_0_E_4_TO_0_E_5, MAGENTA), r1);
		r12 = new Conditions(new VarbitRequirement(VarbitID.SOTE_LIGHT_0_D_3_TO_0_E_3, BLUE), r1);
		r13 = new Conditions(new VarbitRequirement(VarbitID.SOTE_LIGHT_0_D_2_TO_0_D_3, BLUE), r1);
		r14 = new Conditions(new VarbitRequirement(VarbitID.SOTE_LIGHT_0_C_2_TO_0_D_2, BLUE), r1);
		r15 = new Conditions(new VarbitRequirement(VarbitID.SOTE_LIGHT_0_C_2_TO_0_C_3, BLUE), r1);
		r16 = new Conditions(new VarbitRequirement(VarbitID.SOTE_LIGHT_0_C_3_TO_0_C_4, CYAN), r1);
		r17 = new Conditions(new VarbitRequirement(VarbitID.SOTE_LIGHT_0_C_4_TO_0_C_5, GREEN), r1);
		r18 = new Conditions(new VarbitRequirement(VarbitID.SOTE_LIGHT_0_C_5_TO_0_D_5, GREEN), r1);
		r19 = new Conditions(new VarbitRequirement(VarbitID.SOTE_LIGHT_0_C_6_TO_0_D_6, RED), r1);
	}

	public List<QuestStep> getDisplaySteps()
	{
		return QuestUtil.toArrayList(resetPuzzle, collectMirrors, p1Pillar1, p1Pillar2, p1Pillar3, p1Pillar4, p1Pillar5, p1Pillar6, p1Pillar7, goToF0NW,
			goToF0NW, p1Pillar8, goToF1NW, p1Pillar9, p1Pillar10, goToF0Middle, p1Pillar11, p1Pillar12, p1Pillar13, p1Pillar14, p1Pillar15,
			p1Pillar16, p1Pillar17, p1Pillar18, p1Pillar19, p1Pillar20);
	}
}
