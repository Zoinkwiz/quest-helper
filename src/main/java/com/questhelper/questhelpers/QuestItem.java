/*
 *  * Copyright (c) 2021, Senmori
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
package com.questhelper.questhelpers;

import javax.annotation.Nonnull;
import lombok.Getter;

/**
 * Represents an item that can be used for quests.
 */
@Getter
public class QuestItem
{
	/** Get the {@link net.runelite.api.ItemID} for this item. */
	private final int itemID;
	/** Get the display text for this item (how it will be rendered). */
	@Nonnull
	private final String displayText;
	private final int quantity;

	/**
	 * Create a new Quest Item with the given item id and display text and a
	 * default quantity of 1 (one).
	 *
	 * @param itemID the item id
	 * @param displayText the display text, cannot be null
	 */
	public QuestItem(int itemID, @Nonnull String displayText)
	{
		this.itemID = itemID;
		this.displayText = displayText;
		this.quantity = 1;
	}

	/**
	 * @return true if the item id is greater than or equal to 0. Item IDs start at 0.
	 */
	public boolean isValidItem()
	{
		return itemID >= 0;
	}

	/**
	 * Create a new Quest Item with a given item id, display text, and quantity
	 *
	 * @param itemID the item id
	 * @param displayText the display text, cannot be null
	 * @param quantity the quantity of this item
	 */
	public QuestItem(int itemID, @Nonnull String displayText, int quantity)
	{
		this.itemID = itemID;
		this.displayText = displayText;
		this.quantity = quantity;
	}


	/**
	 * Create a new QuestItem using this QuestItem's item id and display text.
	 *
	 * @param quantity the new quantity of item(s)
	 * @return a new instance of QuestItem with the updated quantity
	 */
	public QuestItem withQuantity(int quantity)
	{
		return new QuestItem(getItemID(), getDisplayText(), quantity);
	}

	/**
	 * Create a new QuestItem with this QuestItem's item id and quantity.
	 *
	 * @param displayText the new display text
	 * @return a new instance of QuestItem
	 */
	public QuestItem withName(@Nonnull String displayText)
	{
		return new QuestItem(getItemID(), displayText, getQuantity());
	}
}
