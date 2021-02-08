/*
 * Copyright (c) 2021, Zoinkwiz
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

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import java.util.Collections;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.NullObjectID;
import net.runelite.api.coords.WorldPoint;

public class BaxtorianPillar
{
	@Getter
	private final WorldPoint wp;

	@Getter
	private final String answerText;

	@Getter
	@Setter
	private DetailedQuestStep inspectStep;

	@Getter
	@Setter
	private DetailedQuestStep useStep;

	@Getter
	@Setter
	private ItemRequirement placedItem;

	@Getter
	private ItemRequirement solution;

	public BaxtorianPillar(QuestHelper questHelper, WorldPoint inspectWp, WorldPoint wp, String answerText, String name)
	{
		this.wp = wp;
		this.answerText = answerText;
		this.inspectStep = new ObjectStep(questHelper, NullObjectID.NULL_2005, inspectWp, "Inspect the marked pillar.");
		this.useStep = new ObjectStep(questHelper, NullObjectID.NULL_2005, wp, "Place the correct item on the " + name + " pillar.");
	}

	public void setSolution(ItemRequirement solution)
	{
		this.solution = solution;
		useStep.setRequirements(Collections.singletonList(solution));
		useStep.addIcon(solution.getId());
	}
}
