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
package com.questhelper;

import com.questhelper.questhelpers.QuestItem;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.runelite.api.ItemID;

@UtilityClass
public class ItemDefinitions
{
	/** Represents an item with an id of -1 and some non-null display text. */
	@Getter
	private final QuestItem nullItem = new QuestItem(-1, "I'm null. Fix me!");
	private final Map<Integer, QuestItem> questItems = new ConcurrentHashMap<>();

	protected void init()
	{
		//TODO: Add more commonly used items so they devs can save time creating those
		// These items should be used in almost every quest
		add(getNullItem());
		add(new QuestItem(ItemID.COINS_995,"Coins"));
		add(new QuestItem(ItemID.SPADE, "Spade"));
		add(new QuestItem(ItemID.ROPE, "Rope"));
		// so all coin item ids point towards COINS_995
		addAlternate(ItemID.COINS, ItemID.COINS_995);
		addAlternate(ItemID.COINS_6964, ItemID.COINS_995);
		addAlternate(ItemID.COINS_8890, ItemID.COINS_995);
		addAlternate(ItemID.ROPE_11046, ItemID.ROPE);
		addAlternate(ItemID.ROPE_7155, ItemID.ROPE);
		addAlternate(ItemID.ROPE_20587, ItemID.ROPE);

		add(new QuestItem(ItemID.KNIFE, "Knife"));
		add(new QuestItem(ItemID.HAMMER, "Hammer"));
		add(new QuestItem(ItemID.CHISEL, "Chisel"));
		add(new QuestItem(ItemID.BUCKET, "Bucket"));

	}

	private void add(QuestItem item)
	{
		questItems.put(item.getItemID(), item);
	}
	private void addAlternate(int alternateID, int originalID)
	{
		questItems.computeIfAbsent(alternateID, i -> getQuestItem(originalID));
	}


	/**
	 * Add the {@link QuestItem} to the list of registered Quest Items.
	 * If this item already exists, then nothing happens.
	 * <br>
	 * The provided QuestItem is always returned
	 *
	 * @param item the item to add
	 * @return the provided QuestItem
	 */
	public QuestItem addQuestItem(QuestItem item)
	{
		questItems.putIfAbsent(item.getItemID(), item);
		return item;
	}

	/**
	 * Get an existing {@link QuestItem} for the given {@link net.runelite.api.ItemID}
	 *
	 * @param itemID the item id to get
	 * @return the {@link QuestItem}, or a new {@link QuestItem} with empty display text
	 */
	@Nonnull
	public QuestItem getQuestItem(int itemID)
	{
		return questItems.getOrDefault(itemID, getQuestItem(itemID, ""));
	}

	/**
	 * Get or create a {@link QuestItem} for the given id and display text.
	 *
	 * @param itemID the item id
	 * @param displayText the display text, cannot be null
	 * @return the new {@link QuestItem} or an existing one if the {@param itemID} already exists
	 */
	@Nonnull
	public QuestItem getQuestItem(int itemID, @Nonnull String displayText)
	{
		return questItems.computeIfAbsent(itemID, id -> new QuestItem(id, displayText));
	}

	/**
	 * Get, or create, a {@link QuestItem} for the given parameters.
	 *
	 * @param itemID the item id
	 * @param displayText the display text
	 * @param quantity the quantity of the item
	 * @return the {@link QuestItem}
	 */
	public QuestItem getQuestItem(int itemID, @Nonnull String displayText, int quantity)
	{
		return questItems.computeIfAbsent(itemID, id -> new QuestItem(id, displayText, quantity));
	}
}
