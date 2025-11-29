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

import com.questhelper.requirements.AbstractRequirement;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.InventoryID;

import javax.annotation.Nonnull;
import java.util.Locale;

/**
 * Requirement that checks if a player has a required number of slots free in a given
 * inventory as determined by the {@link InventoryID}.
 */
@Getter
public class FreeInventorySlotRequirement extends AbstractRequirement
{
	private final int NUM_INVENTORY_SLOTS_TOTAL = 28;

	private final int inventoryID;

	@Setter
	private int numSlotsFree;

	/**
	 * Checks if the player has a required number of slots free in a given
	 * {@link InventoryID}
	 *
	 * @param numSlotsFree the required number of slots free
	 */
	public FreeInventorySlotRequirement(int numSlotsFree)
	{
		this.inventoryID = InventoryID.INV;
		this.numSlotsFree = numSlotsFree;
	}

	@Override
	public boolean check(Client client)
	{
		ItemContainer container = client.getItemContainer(getInventoryID());

		if (container != null)
		{
			return NUM_INVENTORY_SLOTS_TOTAL - container.count() >= getNumSlotsFree();
		}
		return false;
	}

	private boolean isOpenSlot(Item item)
	{
		return item == null || item.getId() == -1;
	}

	@Nonnull
	@Override
	public String getDisplayText()
	{
		return getNumSlotsFree() + " free inventory slots";
	}
}
