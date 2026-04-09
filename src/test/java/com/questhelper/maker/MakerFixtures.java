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
import com.questhelper.maker.HelperConstructModels.DraftSkillRequirement;
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
	static final int VARBIT_EXTRA = 9101;
	static final int ITEM_BUCKET = 1925;
	static final int ITEM_BUCKET_ALT = 1926;
	static final int ITEM_KNIFE = 946;
	static final int NPC_HANS = 0;
	static final int OBJECT_LADDER = 17385;

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

	/**
	 * Broader coverage: two sections, three step kinds, captured item with alternate ids,
	 * helper-level skill requirement, item-linked row (no tree), manual-only row,
	 * tree row using ITEM leaf, tree row using a NOR group of two varbits, and per-step
	 * SKILL + non-routing VARBIT attachments.
	 */
	static DraftHelper comprehensiveDraft()
	{
		DraftHelper d = new DraftHelper();
		d.setQuestName("Comprehensive Helper");
		d.setClassName("ComprehensiveGeneratedHelper");
		d.setPackagePath("com.questhelper.helpers.quests.generated");

		DraftRequirement bucket = new DraftRequirement();
		bucket.setRequirementId("item:" + ITEM_BUCKET);
		bucket.setRawId(ITEM_BUCKET);
		bucket.setDisplayName("Bucket");
		bucket.getAlternateRawIds().add(ITEM_BUCKET_ALT);
		d.getRequirements().add(bucket);

		DraftRequirement knife = new DraftRequirement();
		knife.setRequirementId("item:" + ITEM_KNIFE);
		knife.setRawId(ITEM_KNIFE);
		knife.setDisplayName("Knife");
		d.getRequirements().add(knife);

		DraftSkillRequirement crafting = new DraftSkillRequirement();
		crafting.setRequirementId("skill:CRAFTING:10");
		crafting.setSkillName("CRAFTING");
		crafting.setRequiredLevel(10);
		crafting.setCanBeBoosted(true);
		crafting.setDisplayText("10 Crafting");
		crafting.setOperation("GREATER_EQUAL");
		d.getSkillRequirements().add(crafting);

		DraftStep npcStep = new DraftStep();
		npcStep.setStepId("step-npc");
		npcStep.setKind(StepKind.NPC);
		npcStep.setRawId(NPC_HANS);
		npcStep.setSuggestedVarName("talkToHans");
		npcStep.setInstructionText("Talk to Hans.");
		npcStep.setWorldPoint(new WorldPoint(3221, 3219, 0));
		npcStep.getAttachedRequirements().add(
			DraftStepAttachedRequirement.skill("CRAFTING", 10, "GREATER_EQUAL", "10 Crafting", true));
		npcStep.getAttachedRequirements().add(
			DraftStepAttachedRequirement.varbit(VARBIT_EXTRA, 1, "EQUAL", "intro varbit on step"));
		d.getStepDefinitions().add(npcStep);

		DraftStep objectStep = new DraftStep();
		objectStep.setStepId("step-object");
		objectStep.setKind(StepKind.OBJECT);
		objectStep.setRawId(OBJECT_LADDER);
		objectStep.setSuggestedVarName("climbLadder");
		objectStep.setInstructionText("Climb the ladder.");
		objectStep.setWorldPoint(new WorldPoint(3098, 3107, 0));
		d.getStepDefinitions().add(objectStep);

		DraftStep textStep = new DraftStep();
		textStep.setStepId("step-text");
		textStep.setKind(StepKind.TEXT);
		textStep.setSuggestedVarName("waitOut");
		textStep.setInstructionText("Wait for the cutscene to finish.");
		d.getStepDefinitions().add(textStep);

		DraftOrderLine intro = new DraftOrderLine();
		intro.setOrderSlotId("section-intro");
		intro.setSectionDivider(true);
		intro.setSuggestedVarName("Intro");
		d.getOrder().add(intro);

		// Tree row: ITEM leaf — done when Bucket is in inventory.
		DraftOrderLine row1 = new DraftOrderLine();
		row1.setOrderSlotId("slot-item-tree");
		row1.setRefStepId("step-npc");
		row1.setStepRequirement(DraftOrderStepRequirement.item(ITEM_BUCKET));
		row1.setStepRequirementMode(OrderConditionMode.CONTINUE_WHEN_TRUE);
		d.getOrder().add(row1);

		// Item-linked row, no tree — legacy routing path.
		DraftOrderLine row2 = new DraftOrderLine();
		row2.setOrderSlotId("slot-item-linked");
		row2.setRefStepId("step-object");
		row2.setLinkedRequirementRawId(ITEM_KNIFE);
		d.getOrder().add(row2);

		DraftOrderLine outro = new DraftOrderLine();
		outro.setOrderSlotId("section-outro");
		outro.setSectionDivider(true);
		outro.setSuggestedVarName("Outro");
		d.getOrder().add(outro);

		// Tree row: NOR group of two varbits with passOnce, SHOW mode.
		DraftOrderLine row3 = new DraftOrderLine();
		row3.setOrderSlotId("slot-nor-tree");
		row3.setRefStepId("step-text");
		row3.setStepRequirement(DraftOrderStepRequirement.group("NOR",
			DraftOrderStepRequirement.varbit(VARBIT_GATE, 1, "EQUAL", "gate"),
			DraftOrderStepRequirement.varbit(VARBIT_PASSONCE, 1, "EQUAL", "passonce")));
		row3.setStepRequirementMode(OrderConditionMode.SHOW_WHEN_TRUE);
		row3.setPassOnceCompletedOnce(true);
		d.getOrder().add(row3);

		// Manual-only row — no tree, no linked requirement.
		DraftOrderLine row4 = new DraftOrderLine();
		row4.setOrderSlotId("slot-manual");
		row4.setRefStepId("step-npc");
		d.getOrder().add(row4);

		return d;
	}

	/**
	 * Two rows that both reference the same DraftStep (e.g. "do task X, then do task X again").
	 * Each row should produce its own step instance + its own ManualRequirement so the sidebar
	 * checkboxes for the two occurrences latch independently.
	 */
	static DraftHelper sharedStepDraft()
	{
		DraftHelper d = new DraftHelper();
		d.setQuestName("Shared Step Helper");
		d.setClassName("SharedStepHelper");
		d.setPackagePath("com.questhelper.helpers.quests.generated");

		DraftStep furnace = new DraftStep();
		furnace.setStepId("step-furnace");
		furnace.setKind(StepKind.OBJECT);
		furnace.setRawId(OBJECT_LADDER);
		furnace.setSuggestedVarName("useFurnace");
		furnace.setInstructionText("Use the furnace.");
		furnace.setWorldPoint(new WorldPoint(3098, 3107, 0));
		d.getStepDefinitions().add(furnace);

		DraftOrderLine divider = new DraftOrderLine();
		divider.setOrderSlotId("section-shared");
		divider.setSectionDivider(true);
		divider.setSuggestedVarName("Smelt");
		d.getOrder().add(divider);

		DraftOrderLine first = new DraftOrderLine();
		first.setOrderSlotId("slot-furnace-first");
		first.setRefStepId("step-furnace");
		first.setStepRequirement(DraftOrderStepRequirement.varbit(VARBIT_GATE, 1, "EQUAL", "first batch"));
		first.setStepRequirementMode(OrderConditionMode.CONTINUE_WHEN_TRUE);
		d.getOrder().add(first);

		DraftOrderLine second = new DraftOrderLine();
		second.setOrderSlotId("slot-furnace-second");
		second.setRefStepId("step-furnace");
		second.setStepRequirement(DraftOrderStepRequirement.varbit(VARBIT_GATE, 2, "EQUAL", "second batch"));
		second.setStepRequirementMode(OrderConditionMode.CONTINUE_WHEN_TRUE);
		d.getOrder().add(second);

		return d;
	}
}
