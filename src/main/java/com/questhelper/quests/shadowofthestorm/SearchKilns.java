/*
 * Copyright (c) 2020, Zoinkwiz
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
package com.questhelper.quests.shadowofthestorm;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.Arrays;
import java.util.Collection;
import net.runelite.api.NullObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

public class SearchKilns extends DetailedOwnerStep
{
	ObjectStep searchKiln1, searchKiln2, searchKiln3, searchKiln4;

	public SearchKilns(QuestHelper questHelper)
	{
		super(questHelper, "Search the kilns in Uzer until you find a book.");
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	@Override
	protected void updateSteps()
	{
		int correctKiln = client.getVarbitValue(1378);
		if (correctKiln == 0)
		{
			startUpStep(searchKiln1);
		}
		else if (correctKiln == 1)
		{
			startUpStep(searchKiln2);
		}
		else if (correctKiln == 2)
		{
			startUpStep(searchKiln3);
		}
		else if (correctKiln == 3)
		{
			startUpStep(searchKiln4);
		}
	}

	@Override
	protected void setupSteps()
	{
		searchKiln1 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_10242, new WorldPoint(3468, 3124, 0), "Search the kilns in Uzer until you find a book.");
		searchKiln2 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_10243, new WorldPoint(3479, 3083, 0), "Search the kilns in Uzer until you find a book.");
		searchKiln3 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_10244, new WorldPoint(3473, 3093, 0), "Search the kilns in Uzer until you find a book.");
		searchKiln4 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_10245, new WorldPoint(3501, 3085, 0), "Search the kilns in Uzer until you find a book.");
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(searchKiln1, searchKiln2, searchKiln3, searchKiln4);
	}
}
