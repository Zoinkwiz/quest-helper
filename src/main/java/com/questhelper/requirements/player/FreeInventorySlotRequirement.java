/*
 *
 *  * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice, this
 *  *    list of conditions and the following disclaimer.
 *  * 2. Redistributions in binary form must reproduce the above copyright notice,
 *  *    this list of conditions and the following disclaimer in the documentation
 *  *    and/or other materials provided with the distribution.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.questhelper.requirements.player;

import com.questhelper.managers.ActiveRequirementsManager;
import com.questhelper.requirements.AbstractRequirement;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.eventbus.EventBus;

import javax.annotation.Nonnull;

/**
 * Requirement that checks if a player has a required number of slots free in a given
 * inventory as determined by the {@link InventoryID}.
 */
@Getter
public class FreeInventorySlotRequirement extends AbstractRequirement
{
	private final int NUM_INVENTORY_SLOTS_TOTAL = 28;
	private final InventoryID inventoryID;
	private final int numSlotsFree;

	/**
	 * Checks if the player has a required number of slots free in a given
	 * {@link InventoryID}
	 *
	 * @param numSlotsFree the required number of slots free
	 */
	public FreeInventorySlotRequirement(int numSlotsFree)
	{
		this.inventoryID = InventoryID.INVENTORY;
		this.numSlotsFree = numSlotsFree;
	}

	@Override
	public void register(Client client, EventBus eventBus, ActiveRequirementsManager activeRequirementsManager)
	{
		super.register(client, eventBus, activeRequirementsManager);
		ItemContainer inventory = client.getItemContainer(inventoryID);
		if (inventory != null)
		{
			setState(NUM_INVENTORY_SLOTS_TOTAL - inventory.count() >= getNumSlotsFree());
		}
		setState(false);
	}

	public void onItemContainerChanged(ItemContainerChanged itemContainerChanged)
	{
		int itemContainerID = itemContainerChanged.getContainerId();
		if (itemContainerID != inventoryID.getId()) return;

		ItemContainer inventory = itemContainerChanged.getItemContainer();

		if (inventory != null)
		{
			setState(NUM_INVENTORY_SLOTS_TOTAL - inventory.count() >= getNumSlotsFree());
		}
		setState(false);
	}

	@Nonnull
	@Override
	public String getDisplayText()
	{
		return getNumSlotsFree() + " free " + getInventoryID().name().toLowerCase(Locale.ROOT).replaceAll("_", " ") + " slots";
	}
}
