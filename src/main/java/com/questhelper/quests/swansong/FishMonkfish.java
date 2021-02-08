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
package com.questhelper.quests.swansong;

import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.Arrays;
import java.util.Collection;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

public class FishMonkfish extends DetailedOwnerStep
{

	DetailedQuestStep fishMonkfish, cookMonkfish, talkToArnoldWithMonkfish;
	ItemRequirement cookedMonkfish = new ItemRequirement("Fresh monkfish", ItemID.FRESH_MONKFISH_7943, 5);
	ItemRequirement rawMonkfish = new ItemRequirement("Fresh monkfish", ItemID.FRESH_MONKFISH, 5);
	ItemRequirement combatGear = new ItemRequirement("Combat gear", -1, -1);
	ItemRequirement smallNet = new ItemRequirement("Small fishing net", ItemID.SMALL_FISHING_NET);


	public FishMonkfish(QuestHelper questHelper)
	{
		super(questHelper);
		smallNet.setTooltip("You can get one from Arnold");
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	@Override
	protected void updateSteps()
	{
		int numHandedIn = client.getVarbitValue(2105) - 1;
		ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
		int numRaw = 0;
		int numCooked = 0;

		if (inventory != null)
		{
			Item[] inventoryItems = inventory.getItems();
			for (Item item : inventoryItems)
			{
				if (item.getId() == ItemID.FRESH_MONKFISH)
				{
					numRaw++;
				}
				else if (item.getId() == ItemID.FRESH_MONKFISH_7943)
				{
					numCooked++;
				}
			}
		}
		if (numHandedIn + numCooked >= 5)
		{
			cookedMonkfish.setQuantity(5 - numHandedIn);
			startUpStep(talkToArnoldWithMonkfish);
		}
		else if (numHandedIn + numRaw + numCooked >= 5)
		{
			rawMonkfish.setQuantity(5 - numCooked - numHandedIn);
			startUpStep(cookMonkfish);
		}
		else
		{
			startUpStep(fishMonkfish);
		}
	}

	@Override
	protected void setupSteps()
	{
		fishMonkfish = new ObjectStep(getQuestHelper(), NullObjectID.NULL_13477, new WorldPoint(2311, 3696, 0), "Fish at least 5 fresh monkfish. Sea Trolls will appear, and you'll need to kill them.", smallNet, combatGear);
		cookMonkfish = new ObjectStep(getQuestHelper(), ObjectID.RANGE_12611, new WorldPoint(2316, 3669, 0), "Cook 5 monkfish. If you burn any, catch some more.", rawMonkfish);
		talkToArnoldWithMonkfish = new NpcStep(getQuestHelper(), NpcID.ARNOLD_LYDSPOR, new WorldPoint(2329, 3688, 0), "Bring the monkfish to Arnold at the bank.", cookedMonkfish);
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(fishMonkfish, cookMonkfish, talkToArnoldWithMonkfish);
	}
}
