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
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.questjournal;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.Invocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;

class QuestViewRendererTest
{
	@Test
	void compactLayoutPlacesPreparationDetailsBeforeStepsAndRewardsAfter()
	{
		DetailRenderer detailRenderer = mock(DetailRenderer.class);
		QuestViewRenderer renderer = new QuestViewRenderer(
			mock(JournalOverlay.class),
			null,
			detailRenderer,
			mock(ChromeRenderer.class));
		JournalSnapshot.SelectedQuest quest = selectedQuestWithEverySection();
		BufferedImage image = new BufferedImage(
			320,
			800,
			BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		try
		{
			renderer.layoutMainContent(
				DetailRenderer.LayoutContext.measure(
					graphics,
					new Rectangle(0, 0, image.getWidth(), image.getHeight())),
				quest,
				true,
				Collections.emptySet(),
				false);
		}
		finally
		{
			graphics.dispose();
			image.flush();
		}

		List<String> calls = new ArrayList<>();
		for (Invocation invocation : mockingDetails(detailRenderer).getInvocations())
		{
			calls.add(invocation.getMethod().getName());
		}
		assertEquals(
			Arrays.asList(
				"layoutRequirements",
				"layoutRecommendations",
				"layoutNotes",
				"layoutObjectiveList",
				"layoutRewards"),
			calls);
	}

	private static JournalSnapshot.SelectedQuest selectedQuestWithEverySection()
	{
		JournalSnapshot.QuestOverview overview = new JournalSnapshot.QuestOverview(
			"COOKS_ASSISTANT",
			"Cook's Assistant",
			JournalSnapshot.QuestType.QUEST,
			JournalSnapshot.QuestState.IN_PROGRESS,
			JournalSnapshot.QuestDifficulty.NOVICE,
			false);
		JournalSnapshot.Objective objective = new JournalSnapshot.Objective(
			"",
			"",
			"Complete the quest step.",
			JournalSnapshot.ObjectiveState.AVAILABLE,
			true,
			Collections.emptyList());
		return new JournalSnapshot.SelectedQuest(
			overview,
			Collections.singletonList(objective),
			Collections.singletonList(mock(JournalSnapshot.Requirement.class)),
			Collections.singletonList(mock(JournalSnapshot.Requirement.class)),
			Collections.singletonList("Defeat an enemy."),
			Collections.singletonList(mock(JournalSnapshot.Reward.class)),
			Collections.singletonList("Keep this note visible before the steps."));
	}
}
