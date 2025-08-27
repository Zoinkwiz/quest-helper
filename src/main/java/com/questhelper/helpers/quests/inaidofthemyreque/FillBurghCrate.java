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
package com.questhelper.helpers.quests.inaidofthemyreque;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.steps.DetailedQuestStep;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.eventbus.Subscribe;

import java.util.Arrays;

public class FillBurghCrate extends DetailedQuestStep
{
	static ItemRequirement crate;
	static ItemRequirement tinderbox3;
	static ItemRequirement bronzeAxe10;
	static ItemRequirement rawSnailsOrMackerel;

	static
	{
		crate = new ItemRequirement("Crate", ItemID.BURGH_GENERALSTORE_CRATE);
		tinderbox3 = new ItemRequirement("Tinderbox", ItemID.TINDERBOX, 3);
		bronzeAxe10 = new ItemRequirement("Bronze axe", ItemID.BRONZE_AXE, 10);
		rawSnailsOrMackerel = new ItemRequirement("Raw mackerel or raw snail meat (random for each player)", ItemID.RAW_MACKEREL, 10);
		rawSnailsOrMackerel.addAlternates(ItemID.SNAIL_CORPSE1, ItemID.SNAIL_CORPSE2, ItemID.SNAIL_CORPSE3);
		rawSnailsOrMackerel.setDisplayMatchedItemName(true);
	}

	boolean decidedOnSnailOrMackerel;

	public FillBurghCrate(QuestHelper questHelper)
	{
		super(questHelper, "Fill the crate with 3 tinderboxes, 10 bronze axes, and either 10 raw snails or 10 raw mackerel.", tinderbox3, bronzeAxe10, rawSnailsOrMackerel);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	protected void updateSteps()
	{
		int numBronzeAxeNeeded = 10 - client.getVarbitValue(VarbitID.BURGH_AXES_CRATE);
		int numSnailOrMackerelNeeded =  10 - client.getVarbitValue(VarbitID.BURGH_FOOD_CRATE);
		int numTinderboxNeeded =  3 - client.getVarbitValue(VarbitID.BURGH_TINDERBOX_CRATE);

		tinderbox3.setQuantity(numTinderboxNeeded);
		bronzeAxe10.setQuantity(numBronzeAxeNeeded);
		rawSnailsOrMackerel.setQuantity(numSnailOrMackerelNeeded);

		if (!decidedOnSnailOrMackerel && QuestHelperQuest.IN_AID_OF_THE_MYREQUE.getVar(client) >= 165)
		{
			decidedOnSnailOrMackerel = true;
			if (client.getVarbitValue(VarbitID.BURGH_FOOD_TYPE) == 1)
			{
				this.setText("Fill the crate with 3 tinderboxes, 10 bronze axes, and 10 raw mackerel.");
				rawSnailsOrMackerel.setDisplayItemId(ItemID.RAW_MACKEREL);
				this.setRequirements(Arrays.asList(crate, tinderbox3, bronzeAxe10, rawSnailsOrMackerel));
			}
			else
			{
				this.setText("Fill the crate with 3 tinderboxes, 10 bronze axes, and 10 raw snails (can be lean, thin or fat).");
				rawSnailsOrMackerel.setDisplayItemId(ItemID.SNAIL_CORPSE3);
				this.setRequirements(Arrays.asList(crate, tinderbox3, bronzeAxe10, rawSnailsOrMackerel));
			}
		}
	}
}
