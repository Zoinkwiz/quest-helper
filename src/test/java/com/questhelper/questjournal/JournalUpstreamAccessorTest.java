/*
 * Copyright (c) 2026, nanopink
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
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.questjournal;

import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.rewards.ItemReward;
import com.questhelper.steps.QuestStep;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

public class JournalUpstreamAccessorTest
{
	@Test
	public void complexStateHelperExposesLoadedStep()
	{
		QuestStep expected = mock(QuestStep.class);
		ComplexStateQuestHelper helper = new TestComplexStateQuestHelper(expected);

		helper.init();

		assertSame(expected, helper.getStep());
	}

	@Test
	public void itemRewardExposesOnlyJournalItemValues()
	{
		ItemReward reward = new ItemReward("Coins", 995, 25);

		assertEquals("Coins", reward.getName());
		assertEquals(995, reward.getItemID());
		assertEquals(25, reward.getQuantity());
	}

	private static final class TestComplexStateQuestHelper extends ComplexStateQuestHelper
	{
		private final QuestStep loadedStep;

		private TestComplexStateQuestHelper(QuestStep loadedStep)
		{
			this.loadedStep = loadedStep;
		}

		@Override
		public QuestStep loadStep()
		{
			return loadedStep;
		}

		@Override
		protected void setupRequirements()
		{
		}
	}
}
