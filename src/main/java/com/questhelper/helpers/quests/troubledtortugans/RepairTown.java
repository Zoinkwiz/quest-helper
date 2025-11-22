/*
 * Copyright (c) 2025, pajlada <https://github.com/pajlada>
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
package com.questhelper.helpers.quests.troubledtortugans;

import com.questhelper.managers.QuestContainerManager;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.List;
import net.runelite.api.ItemContainer;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.eventbus.Subscribe;

public class RepairTown extends DetailedOwnerStep
{
	// Required items
	ItemRequirement anyAxe;
	ItemRequirement hammer;
	ItemRequirement saw;

	// Mid-quest requirements
	ItemRequirement jatobaLogs;
	ItemRequirement seaShells;
	ItemRequirement tortuganScutes;

	// Miscellaneous requirements
	VarbitRequirement repairedKrillStall;
	VarbitRequirement repairedCocoStall;
	VarbitRequirement repairedStromCrates;
	VarbitRequirement repairedStromWall;
	VarbitRequirement repairedCocoCrates;
	VarbitRequirement repairedKrillWall;
	FreeInventorySlotRequirement freeInvSlotRequirement;

	// Steps
	ItemStep gatherShells;
	ItemStep gatherScutes;
	ObjectStep chopJatobaTrees;
	ObjectStep repairKrillStall;
	ObjectStep repairCocoStall;
	ObjectStep repairStromCrates;
	ObjectStep repairStromWall;
	ObjectStep repairCocoCrates;
	ObjectStep repairKrillWall;

	private boolean hasInitialized;

	public RepairTown(QuestHelper questHelper, ItemRequirement anyAxe, ItemRequirement hammer, ItemRequirement saw)
	{
		super(questHelper, "Gather materials and repair the objects around the town. Clear your inventory until you have 22 slots free for the material to speed up repairing.");

		this.anyAxe = anyAxe;
		this.hammer = hammer;
		this.saw = saw;
	}

	void initializeRequirements()
	{
		if (hasInitialized)
		{
			return;
		}

		freeInvSlotRequirement = new FreeInventorySlotRequirement(22);

		jatobaLogs = new ItemRequirement("Jatoba Logs", ItemID.JATOBA_LOGS, -1);
		jatobaLogs.setTooltip("A big cluster of these trees can be found to the west of the town.");
		seaShells = new ItemRequirement("Sea Shells", ItemID.SEA_SHELL, -1);
		seaShells.setTooltip("Can be found to the south by the docks you entered from.");
		tortuganScutes = new ItemRequirement("Tortugan scutes", ItemID.TORTUGAN_SCUTE, -1);
		tortuganScutes.setTooltip("Found scattered around the town.");

		repairedKrillStall = new VarbitRequirement(VarbitID.TT_REPAIR_KRILL_STALL, 2);
		repairedCocoStall = new VarbitRequirement(VarbitID.TT_REPAIR_COCO_STALL, 2);
		repairedStromCrates = new VarbitRequirement(VarbitID.TT_REPAIR_STROM_CRATES, 2);
		repairedStromWall = new VarbitRequirement(VarbitID.TT_REPAIR_STROM_WALL, 2);
		repairedCocoCrates = new VarbitRequirement(VarbitID.TT_REPAIR_COCO_CRATES, 2);
		repairedKrillWall = new VarbitRequirement(VarbitID.TT_REPAIR_KRILL_WALL, 2);

		hasInitialized = true;
	}

	@Override
	public void startUp()
	{
		updateSteps();
	}

	@Override
	protected void setupSteps()
	{
		initializeRequirements();

		gatherShells = new ItemStep(this.questHelper, new WorldPoint(3184, 2384, 0), "Gather sea shells on the beach south of The Summer Shore.", seaShells);
		gatherScutes = new ItemStep(this.questHelper, new WorldPoint(3169, 2408, 0), "Gather Tortugan scutes around the town.", tortuganScutes);
		chopJatobaTrees = new ObjectStep(this.questHelper, ObjectID.JATOBA_TREE, new WorldPoint(3111, 2412, 0), "Chop Jatoba trees for jatoba logs.", anyAxe, jatobaLogs);

		repairKrillStall = new ObjectStep(this.questHelper, ObjectID.TT_REPAIR_KRILL_STALL, new WorldPoint(3164, 2415, 0), "Repair Elder Krill's broken fish stall.", hammer, saw, seaShells.quantity(1), tortuganScutes.quantity(1), jatobaLogs.quantity(2));
		repairCocoStall = new ObjectStep(this.questHelper, ObjectID.TT_REPAIR_COCO_STALL, new WorldPoint(3170, 2406, 0), "Repair Elder Coco's broken crafting stall.", hammer, saw, seaShells.quantity(1), tortuganScutes.quantity(1), jatobaLogs.quantity(2));
		repairStromCrates = new ObjectStep(this.questHelper, ObjectID.TT_REPAIR_STROM_CRATES, new WorldPoint(3156, 2403, 0), "Repair Elder Strom's broken crate.", hammer, saw, seaShells.quantity(2), jatobaLogs.quantity(1));
		repairStromWall = new ObjectStep(this.questHelper, ObjectID.TT_REPAIR_STROM_WALL, new WorldPoint(3153, 2410, 0), "Repair Elder Strom's damaged wall.", hammer, saw, tortuganScutes.quantity(2), jatobaLogs.quantity(2));
		repairCocoCrates = new ObjectStep(this.questHelper, ObjectID.TT_REPAIR_COCO_CRATES, new WorldPoint(3168, 2402, 0), "Repair Elder Coco's broken coconut crate.", hammer, saw, seaShells.quantity(2), jatobaLogs.quantity(1));
		repairKrillWall = new ObjectStep(this.questHelper, ObjectID.TT_REPAIR_KRILL_WALL, new WorldPoint(3166, 2419, 0), "Repair Elder Krill's damaged wall.", hammer, saw, tortuganScutes.quantity(2), jatobaLogs.quantity(2));
	}

	@Subscribe
	public void onGameTick(final GameTick event)
	{
		// TODO: optimize?
		updateSteps();
	}

	protected void updateSteps()
	{
		var requiredLogs = 0;
		var requiredShells = 0;
		var requiredScutes = 0;

		ItemContainer container = client.getItemContainer(InventoryID.INV);

		var inv = QuestContainerManager.getInventoryData();
		var numLogs = jatobaLogs.checkTotalMatchesInContainers(inv);
		var numShells = seaShells.checkTotalMatchesInContainers(inv);
		var numScutes = tortuganScutes.checkTotalMatchesInContainers(inv);

		// NOTE: Is there a nicer way to get number of free slots from QuestContainerManager?
		var numFreeSlots = 0;
		if (container != null)
		{
			numFreeSlots = 28 - container.count();
		}


		QuestStep task = null;

		if (!repairedKrillStall.check(client))
		{
			requiredScutes += 1;
			requiredShells += 1;
			requiredLogs += 2;
			task = repairKrillStall;
		}

		if (!repairedKrillWall.check(client))
		{
			requiredScutes += 2;
			requiredShells += 0;
			requiredLogs += 2;
			if (task == null)
			{
				task = repairKrillWall;
			}
		}

		if (!repairedStromWall.check(client))
		{
			requiredScutes += 2;
			requiredShells += 0;
			requiredLogs += 2;
			if (task == null)
			{
				task = repairStromWall;
			}
		}

		if (!repairedStromCrates.check(client))
		{
			requiredScutes += 0;
			requiredShells += 2;
			requiredLogs += 1;
			if (task == null)
			{
				task = repairStromCrates;
			}
		}

		if (!repairedCocoStall.check(client))
		{
			requiredScutes += 1;
			requiredShells += 1;
			requiredLogs += 2;
			if (task == null)
			{
				task = repairCocoStall;
			}
		}

		if (!repairedCocoCrates.check(client))
		{
			requiredScutes += 0;
			requiredShells += 2;
			requiredLogs += 1;
			if (task == null)
			{
				task = repairCocoCrates;
			}
		}


		var numFreeSlotsNeeded = 0;
		if (requiredLogs > numLogs)
		{
			numFreeSlotsNeeded += requiredLogs - numLogs;
		}
		if (requiredShells > numShells)
		{
			numFreeSlotsNeeded += requiredShells - numShells;
		}
		if (requiredScutes > numScutes)
		{
			numFreeSlotsNeeded += requiredScutes - numScutes;
		}

		freeInvSlotRequirement.setNumSlotsFree(numFreeSlotsNeeded);

		jatobaLogs.setQuantity(requiredLogs);
		seaShells.setQuantity(requiredShells);
		tortuganScutes.setQuantity(requiredScutes);

		// The following logic could probably be refactored a bit, but changing it without being at the quest spot with your
		// brain intact is a dangerous task, so I'm leaving this as-is.

		if (numFreeSlots == 0)
		{
			// The player's inventory is full, we should tell them to do the first available repair task.
			// They will have to gather materials based off of the tooltips, rather than nicely highlighted objects.
			// This is to accomodate UIM players, other players are prompted to clear their inventory for an
			// easier path.
			if (numFreeSlotsNeeded == 0)
			{
				// If the user is actually done gathering everything, but their inventory is full, make the messaging nicer.
				this.setText("Repair the objects around the town.");
			}
			else
			{
				this.setText(String.format("Gather materials and repair the objects around the town. Clear your inventory until you have %d slots free for the material to speed up repairing.", numFreeSlotsNeeded));
			}
			startUpStep(task);
			return;
		}

		if (numFreeSlots >= numFreeSlotsNeeded)
		{
			// User has enough free inventory slots to gather everything _then_ repair things.
			if (numShells < requiredShells)
			{
				this.setText("Gather materials, then repair the objects around the town.");
				startUpStep(gatherShells);
				return;
			}

			if (numScutes < requiredScutes)
			{
				this.setText("Gather materials, then repair the objects around the town.");
				startUpStep(gatherScutes);
				return;
			}

			if (numLogs < requiredLogs)
			{
				this.setText("Gather materials, then repair the objects around the town.");
				startUpStep(chopJatobaTrees);
				return;
			}
		}

		this.setText(String.format("Gather materials and repair the objects around the town. Clear your inventory until you have %d slots free for the material to speed up repairing.", numFreeSlotsNeeded));
		startUpStep(task);
	}

	@Override
	public List<QuestStep> getSteps()
	{
		return List.of(
			gatherShells,
			gatherScutes,
			chopJatobaTrees,
			repairKrillStall,
			repairKrillWall,
			repairStromWall,
			repairStromCrates,
			repairCocoStall,
			repairCocoCrates
		);
	}
}
