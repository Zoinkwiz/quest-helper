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
package com.questhelper.quests.rumdeal;

import com.questhelper.Zone;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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

public class SlugSteps extends DetailedOwnerStep
{

	DetailedQuestStep addSluglings, talkToPete, goDownFromTop, fish5Slugs, goDownToSluglings, goUpFromSluglings, goUpToDropSluglings, goUpF1ToPressure, goUpToF2ToPressure, pressure;
	ConditionalStep getSluglings, pressureSluglings, pullPressureLever;

	Zone islandF0, islandF1, islandF2;

	Requirement onIslandF0, onIslandF1, onIslandF2;

	ItemRequirement sluglings;
	ItemRequirement sluglingsHighlight;
	ItemRequirement netBowl;

	public SlugSteps(QuestHelper questHelper)
	{
		super(questHelper);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	protected void updateSteps()
	{
		int numHandedIn = client.getVarbitValue(1354);
		ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);

		int numInInv = 0;

		if (inventory != null)
		{
			Item[] inventoryItems = inventory.getItems();
			for (Item item : inventoryItems)
			{
				if (item.getId() == sluglings.getId())
				{
					numInInv++;
				}
			}
		}
		sluglings.setQuantity(5 - numHandedIn);
		if (numHandedIn >= 5)
		{
			startUpStep(pullPressureLever);
		}
		else if (numHandedIn + numInInv >= 5)
		{
			startUpStep(pressureSluglings);
		}
		else
		{
			startUpStep(getSluglings);
		}
	}

	@Override
	protected void setupSteps()
	{
		sluglings = new ItemRequirement("Sluglings or Karamthulu", ItemID.SLUGLINGS, 5);
		sluglingsHighlight = new ItemRequirement("Sluglings or Karamthulu", ItemID.SLUGLINGS, 5);
		netBowl = new ItemRequirement("Fishbowl and net", ItemID.FISHBOWL_AND_NET);
		netBowl.setTooltip("You can get another from Captain Braindeath, or make it with a fishbowl and large net");
		sluglingsHighlight.setHighlightInInventory(true);
		sluglingsHighlight.addAlternates(ItemID.KARAMTHULHU, ItemID.KARAMTHULHU_6717);
		sluglings.addAlternates(ItemID.KARAMTHULHU, ItemID.KARAMTHULHU_6717);

		islandF0 = new Zone(new WorldPoint(2110, 5054, 0), new WorldPoint(2178, 5185, 0));
		islandF1 = new Zone(new WorldPoint(2110, 5054, 1), new WorldPoint(2178, 5185, 1));
		islandF2 = new Zone(new WorldPoint(2110, 5054, 2), new WorldPoint(2178, 5185, 2));
		onIslandF0 = new ZoneRequirement(islandF0);
		onIslandF1 = new ZoneRequirement(islandF1);
		onIslandF2 = new ZoneRequirement(islandF2);

		talkToPete = new NpcStep(getQuestHelper(), NpcID.PIRATE_PETE, new WorldPoint(3680, 3537, 0), "Talk to Pirate Pete north east of the Ectofuntus.");
		talkToPete.addDialogSteps("Okay!");
		addSluglings = new ObjectStep(getQuestHelper(), ObjectID.PRESSURE_BARREL, new WorldPoint(2142, 5102, 2),
			"Add the sea creatures to the pressure barrel on the top floor.", sluglings);
		addSluglings.addIcon(ItemID.SLUGLINGS);
		goDownFromTop = new ObjectStep(getQuestHelper(), ObjectID.LADDER_10168, new WorldPoint(2163, 5092, 2), "Go down the ladder and fish for sea creatures.");

		fish5Slugs = new NpcStep(getQuestHelper(), NpcID.FISHING_SPOT, "Fish 5 sluglings or karamthulu from around the coast of the island.", netBowl);
		goDownToSluglings = new ObjectStep(getQuestHelper(), ObjectID.WOODEN_STAIR_10137, new WorldPoint(2150, 5088, 1), "Go fish 5 sluglings.", netBowl);
		goUpFromSluglings = new ObjectStep(getQuestHelper(), ObjectID.WOODEN_STAIR, new WorldPoint(2150, 5088, 0),
			"Add the sea creatures to the pressure barrel on the top floor.", sluglings);

		fish5Slugs.addSubSteps(goDownFromTop, goDownToSluglings, talkToPete);

		goUpToDropSluglings = new ObjectStep(getQuestHelper(), ObjectID.LADDER_10167, new WorldPoint(2163, 5092, 1),
			"Add the sea creatures to the pressure barrel on the top floor.", sluglings);

		goUpFromSluglings = new ObjectStep(getQuestHelper(), ObjectID.WOODEN_STAIR, new WorldPoint(2150, 5088, 0),
			"Go to the top floor to pull the pressure lever.", sluglings);
		goUpF1ToPressure = new ObjectStep(getQuestHelper(), ObjectID.WOODEN_STAIR, new WorldPoint(2150, 5088, 0),
			"Go to the top floor to pull the pressure lever.");
		goUpToF2ToPressure = new ObjectStep(getQuestHelper(), ObjectID.LADDER_10167, new WorldPoint(2163, 5092, 1),
			"Go to the top floor to pull the pressure lever.");

		pressure = new ObjectStep(getQuestHelper(), NullObjectID.NULL_10164, new WorldPoint(2141, 5103, 2), "Pull the pressure lever.");
		pressure.addSubSteps(goUpToF2ToPressure, goUpF1ToPressure);

		getSluglings = new ConditionalStep(getQuestHelper(), talkToPete);
		getSluglings.addStep(onIslandF0, fish5Slugs);
		getSluglings.addStep(onIslandF1, goDownToSluglings);
		getSluglings.addStep(onIslandF2, goDownToSluglings);

		pressureSluglings = new ConditionalStep(getQuestHelper(), talkToPete);
		pressureSluglings.addStep(onIslandF2, addSluglings);
		pressureSluglings.addStep(onIslandF1, goUpToDropSluglings);
		pressureSluglings.addStep(onIslandF0, goUpFromSluglings);

		pullPressureLever = new ConditionalStep(getQuestHelper(), talkToPete);
		pullPressureLever.addStep(onIslandF2, pressure);
		pullPressureLever.addStep(onIslandF1, goUpToF2ToPressure);
		pullPressureLever.addStep(onIslandF0, goUpF1ToPressure);
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(talkToPete, goDownToSluglings, fish5Slugs, goUpFromSluglings, goUpToDropSluglings, addSluglings,
			getSluglings, pressureSluglings, goUpF1ToPressure, goUpToF2ToPressure, pressure, pullPressureLever);
	}

	public List<QuestStep> getDisplaySteps()
	{
		return Arrays.asList(fish5Slugs, addSluglings, pressure);
	}
}

