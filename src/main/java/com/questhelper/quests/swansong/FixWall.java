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

import com.questhelper.ItemCollections;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.Arrays;
import java.util.Collection;
import net.runelite.api.ItemID;
import net.runelite.api.NullObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

public class FixWall extends DetailedOwnerStep
{
	DetailedQuestStep useIronBar, repairWall1, repairWall2, repairWall3, repairWall4, repairWall5;

	ItemRequirement ironSheets, ironBars, hammer;

	public FixWall(QuestHelper questHelper)
	{
		super(questHelper);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	@Override
	protected void updateSteps()
	{
		int wall1Fixed = client.getVarbitValue(2100);
		int wall2Fixed = client.getVarbitValue(2101);
		int wall3Fixed = client.getVarbitValue(2102);
		int wall4Fixed = client.getVarbitValue(2103);
		int wall5Fixed = client.getVarbitValue(2104);

		int wallsToRepair = 5 - (wall1Fixed + wall2Fixed + wall3Fixed + wall4Fixed + wall5Fixed);

		ironBars.setQuantity(wallsToRepair);
		ironSheets.setQuantity(wallsToRepair);

		if (ironSheets.check(client))
		{
			if (wall1Fixed == 0)
			{
				startUpStep(repairWall1);
			}
			else if (wall2Fixed == 0)
			{
				startUpStep(repairWall2);
			}
			else if (wall3Fixed == 0)
			{
				startUpStep(repairWall3);
			}
			else if (wall4Fixed == 0)
			{
				startUpStep(repairWall4);
			}
			else if (wall5Fixed == 0)
			{
				startUpStep(repairWall5);
			}
		}
		else
		{
			startUpStep(useIronBar);
		}
	}

	@Override
	protected void setupSteps()
	{
		ironSheets = new ItemRequirement("Iron sheet", ItemID.IRON_SHEET, 5);
		ironBars = new ItemRequirement("Iron bars", ItemID.IRON_BAR, 5);
		hammer = new ItemRequirement("Hammer", ItemCollections.getHammer());
		hammer.setTooltip("Franklin will give you one");
		ironBars.setHighlightInInventory(true);
		ironSheets.setHighlightInInventory(true);

		useIronBar = new ObjectStep(getQuestHelper(), NullObjectID.NULL_13701, new WorldPoint(2342, 3676, 0), "Flatten 5 iron bars using the metal press.", ironBars);
		useIronBar.addIcon(ItemID.IRON_BAR);
		repairWall1 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_13612, new WorldPoint(2311, 3688, 0), "Repair the west wall.", ironSheets, hammer);
		repairWall1.addIcon(ItemID.IRON_SHEET);
		repairWall2 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_13613, new WorldPoint(2311, 3687, 0), "Repair the west wall.", ironSheets, hammer);
		repairWall2.addIcon(ItemID.IRON_SHEET);
		repairWall3 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_13614, new WorldPoint(2311, 3686, 0), "Repair the west wall.", ironSheets, hammer);
		repairWall3.addIcon(ItemID.IRON_SHEET);
		repairWall4 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_13699, new WorldPoint(2311, 3685, 0), "Repair the west wall.", ironSheets, hammer);
		repairWall4.addIcon(ItemID.IRON_SHEET);
		repairWall5 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_13700, new WorldPoint(2311, 3684, 0), "Repair the west wall.", ironSheets, hammer);
		repairWall5.addIcon(ItemID.IRON_SHEET);
		repairWall1.addSubSteps(repairWall2, repairWall3, repairWall4, repairWall5);
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(useIronBar, repairWall1, repairWall2, repairWall3, repairWall4, repairWall5);
	}

	public Collection<DetailedQuestStep> getDisplaySteps()
	{
		return Arrays.asList(useIronBar, repairWall1);
	}
}

