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
package com.questhelper.quests.enlightenedjourney;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.NpcStep;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

public class GiveAugusteItems extends NpcStep
{
	private static final ItemRequirement sandbag8 = new ItemRequirement("Sandbag", ItemID.SANDBAG, 8);
	private static final ItemRequirement yellowDye = new ItemRequirement("Yellow dye", ItemID.YELLOW_DYE);
	private static final ItemRequirement redDye = new ItemRequirement("Red dye", ItemID.RED_DYE);
	private static final ItemRequirement silk10 = new ItemRequirement("Silk", ItemID.SILK, 10);
	private static final ItemRequirement bowl = new ItemRequirement("Bowl", ItemID.BOWL);

	private final Requirement givenRedDye;
	private final Requirement givenYellowDye;
	private final Requirement givenSandbags;
	private final Requirement givenSilk;
	private final Requirement givenBowl;

	public GiveAugusteItems(QuestHelper questHelper)
	{

		super(questHelper, NpcID.AUGUSTE, new WorldPoint(2809, 3354, 0),
			"Give Auguste the sandbags, silk, dyes, and bowl.", sandbag8, silk10, redDye, yellowDye, bowl);
		givenRedDye = new VarbitRequirement(2873, 1);
		givenYellowDye = new VarbitRequirement(2874, 1); // 2879 = 1 as well, maybe both dyes done
		givenSandbags = new VarbitRequirement(2875, 1);
		givenSilk = new VarbitRequirement(2876, 1);
		givenBowl = new VarbitRequirement(2877, 1);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	protected void updateSteps()
	{
		emptyRequirements();

		if (!givenRedDye.check(client))
		{
			requirements.add(redDye);
		}

		if (!givenYellowDye.check(client))
		{
			requirements.add(yellowDye);
		}

		if (!givenBowl.check(client))
		{
			requirements.add(bowl);
		}

		if (!givenSandbags.check(client))
		{
			requirements.add(sandbag8);
		}

		if (!givenSilk.check(client))
		{
			requirements.add(silk10);
		}
	}
}

