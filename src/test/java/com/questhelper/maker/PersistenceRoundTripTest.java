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

import com.google.gson.Gson;
import com.questhelper.maker.HelperConstructModels.DraftHelper;
import com.questhelper.maker.HelperConstructModels.DraftOrderLine;
import com.questhelper.maker.HelperConstructModels.DraftStep;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Round-trips a fixture {@link DraftHelper} through {@link ConstructDraftPersistence#toDraftState}
 * → Gson serialize → Gson deserialize → {@link ConstructDraftPersistence#draftHelperFromState}
 * and asserts that the fields most likely to drift survive intact.
 *
 * <p>Catches the class of bug where a draft field exists in the editor / preview but never gets
 * plumbed into the persisted state (e.g. {@code stepRequirementMode}, {@code passOnceCompletedOnce}).</p>
 */
class PersistenceRoundTripTest
{
	@Test
	void smallDraftRoundTrip()
	{
		DraftHelper original = MakerFixtures.smallDraft();
		DraftHelper reloaded = roundTrip(original);

		assertEquals(original.getQuestName(), reloaded.getQuestName());
		assertEquals(original.getClassName(), reloaded.getClassName());
		assertEquals(original.getPackagePath(), reloaded.getPackagePath());

		assertEquals(original.getStepDefinitions().size(), reloaded.getStepDefinitions().size(),
			"step definitions should round-trip");
		for (int i = 0; i < original.getStepDefinitions().size(); i++)
		{
			DraftStep o = original.getStepDefinitions().get(i);
			DraftStep r = reloaded.getStepDefinitions().get(i);
			assertEquals(o.getStepId(), r.getStepId());
			assertEquals(o.getKind(), r.getKind());
			assertEquals(o.getRawId(), r.getRawId());
			assertEquals(o.getSuggestedVarName(), r.getSuggestedVarName());
			assertEquals(o.getInstructionText(), r.getInstructionText());
			assertEquals(o.getAttachedRequirements().size(), r.getAttachedRequirements().size(),
				"attached requirements should round-trip for step " + o.getStepId());
		}

		assertEquals(original.getOrder().size(), reloaded.getOrder().size(), "order rows should round-trip");
		for (int i = 0; i < original.getOrder().size(); i++)
		{
			DraftOrderLine o = original.getOrder().get(i);
			DraftOrderLine r = reloaded.getOrder().get(i);
			assertEquals(o.getOrderSlotId(), r.getOrderSlotId());
			assertEquals(o.isSectionDivider(), r.isSectionDivider());
			assertEquals(o.getRefStepId(), r.getRefStepId());
			assertEquals(o.getStepRequirementMode(), r.getStepRequirementMode(),
				"stepRequirementMode must persist for slot " + o.getOrderSlotId());
			assertEquals(o.isPassOnceCompletedOnce(), r.isPassOnceCompletedOnce(),
				"passOnceCompletedOnce must persist for slot " + o.getOrderSlotId());
			if (o.getStepRequirement() != null)
			{
				assertNotNull(r.getStepRequirement(), "stepRequirement tree should round-trip");
				assertEquals(o.getStepRequirement().getKind(), r.getStepRequirement().getKind());
				assertEquals(o.getStepRequirement().getVarbitId(), r.getStepRequirement().getVarbitId());
				assertEquals(o.getStepRequirement().getVarbitRequiredValue(), r.getStepRequirement().getVarbitRequiredValue());
			}
		}

		assertEquals(original.getRequirements().size(), reloaded.getRequirements().size(),
			"item requirements should round-trip");
	}

	@Test
	void smallDraftJsonRoundTripIsStable()
	{
		Gson gson = new Gson();
		DraftHelper original = MakerFixtures.smallDraft();
		String firstJson = gson.toJson(ConstructDraftPersistence.toDraftState(original));
		assertTrue(firstJson.contains("\"passOnceCompletedOnce\":true"),
			"sticky pass-once flag must appear in JSON");
		assertTrue(firstJson.contains("\"stepRequirementMode\":\"CONTINUE_WHEN_TRUE\"")
				|| firstJson.contains("\"stepRequirementMode\":\"SHOW_WHEN_TRUE\""),
			"stepRequirementMode must appear in JSON");

		DraftHelper reloaded = roundTrip(original);
		String secondJson = gson.toJson(ConstructDraftPersistence.toDraftState(reloaded));
		assertEquals(firstJson, secondJson, "JSON must be stable across save → load → save");
	}

	private static DraftHelper roundTrip(DraftHelper original)
	{
		Gson gson = new Gson();
		String json = gson.toJson(ConstructDraftPersistence.toDraftState(original));
		ConstructDraftPersistence.DraftState state = gson.fromJson(json, ConstructDraftPersistence.DraftState.class);
		state.formatVersion = ConstructDraftPersistence.DRAFT_FORMAT_VERSION;
		return ConstructDraftPersistence.draftHelperFromState(state);
	}
}
