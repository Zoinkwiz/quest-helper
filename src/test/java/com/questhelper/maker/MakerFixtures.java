/*
 * Copyright (c) 2026, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.maker;

import com.questhelper.maker.HelperConstructModels.DraftHelper;
import com.questhelper.maker.HelperConstructModels.DraftOrderLine;
import com.questhelper.maker.HelperConstructModels.DraftOrderStepRequirement;
import com.questhelper.maker.HelperConstructModels.DraftRequirement;
import com.questhelper.maker.HelperConstructModels.DraftStep;
import com.questhelper.maker.HelperConstructModels.DraftStepAttachedRequirement;
import com.questhelper.maker.HelperConstructModels.OrderConditionMode;
import com.questhelper.maker.HelperConstructModels.StepKind;
import net.runelite.api.coords.WorldPoint;

/**
 * Shared fixture builders for maker tests. Each fixture is intentionally small but exercises
 * the field surface that has historically drifted between the four representations
 * (draft model, JSON state, in-game preview, generated source).
 */
final class MakerFixtures
{
	static final int VARBIT_GATE = 4242;
	static final int VARBIT_PASSONCE = 7777;
	static final int ITEM_BUCKET = 1925;
	static final int NPC_HANS = 0;

	private MakerFixtures()
	{
	}

	/**
	 * One section, two NPC steps. First row CONTINUE-when-true varbit selector with passOnceCompletedOnce,
	 * second row SHOW-when-true varbit selector. Plus one captured item requirement and an attached widget
	 * highlight on the first step.
	 */
	static DraftHelper smallDraft()
	{
		DraftHelper d = new DraftHelper();
		d.setQuestName("Test Helper");
		d.setClassName("TestGeneratedHelper");
		d.setPackagePath("com.questhelper.helpers.quests.generated");

		DraftRequirement bucket = new DraftRequirement();
		bucket.setRequirementId("item:" + ITEM_BUCKET);
		bucket.setRawId(ITEM_BUCKET);
		bucket.setDisplayName("Bucket");
		d.getRequirements().add(bucket);

		DraftStep step1 = new DraftStep();
		step1.setStepId("step-1");
		step1.setKind(StepKind.NPC);
		step1.setRawId(NPC_HANS);
		step1.setSuggestedVarName("talkToHans");
		step1.setInstructionText("Talk to Hans.");
		step1.setWorldPoint(new WorldPoint(3221, 3219, 0));
		step1.getAttachedRequirements().add(
			DraftStepAttachedRequirement.item(ITEM_BUCKET, true, 1, false));
		step1.getAttachedRequirements().add(
			DraftStepAttachedRequirement.widgetByGroupChild(160, 5, null));
		d.getStepDefinitions().add(step1);

		DraftStep step2 = new DraftStep();
		step2.setStepId("step-2");
		step2.setKind(StepKind.NPC);
		step2.setRawId(NPC_HANS);
		step2.setSuggestedVarName("talkAgain");
		step2.setInstructionText("Talk to Hans again.");
		step2.setWorldPoint(new WorldPoint(3221, 3219, 0));
		d.getStepDefinitions().add(step2);

		DraftOrderLine divider = new DraftOrderLine();
		divider.setOrderSlotId("section-1");
		divider.setSectionDivider(true);
		divider.setSuggestedVarName("Intro");
		d.getOrder().add(divider);

		DraftOrderLine row1 = new DraftOrderLine();
		row1.setOrderSlotId("slot-1");
		row1.setRefStepId("step-1");
		row1.setStepRequirement(DraftOrderStepRequirement.varbit(VARBIT_PASSONCE, 1, "EQUAL", "intro varbit"));
		row1.setStepRequirementMode(OrderConditionMode.CONTINUE_WHEN_TRUE);
		row1.setPassOnceCompletedOnce(true);
		d.getOrder().add(row1);

		DraftOrderLine row2 = new DraftOrderLine();
		row2.setOrderSlotId("slot-2");
		row2.setRefStepId("step-2");
		row2.setStepRequirement(DraftOrderStepRequirement.varbit(VARBIT_GATE, 2, "EQUAL", "second varbit"));
		row2.setStepRequirementMode(OrderConditionMode.SHOW_WHEN_TRUE);
		d.getOrder().add(row2);

		return d;
	}
}
