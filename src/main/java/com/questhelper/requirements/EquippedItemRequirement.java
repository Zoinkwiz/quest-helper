/*
 *
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

package com.questhelper.requirements;

import com.questhelper.ItemDefinitions;
import com.questhelper.questhelpers.QuestItem;
import com.questhelper.requirements.util.ItemSlots;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.components.LineComponent;

/**
 * Checks if the player has supplied item(s) in the given slot.
 * Unlike {@link ItemRequirement}, this only checks given slots, it does not check bank items.
 */
@Getter
public class EquippedItemRequirement extends AbstractRequirement
{
	private final ItemSlots slot;
	private final String name;
	private final Map<Integer, QuestItem> items = new LinkedHashMap<>();

	/**
	 * Check if the player has a certain item in a certain {@link ItemSlots}.
	 *
	 * @param name the display text
	 * @param slot the slot to check
	 * @param id the item id
	 */
	public EquippedItemRequirement(String name, ItemSlots slot, int id)
	{
		this.slot = slot;
		this.name = name;
		addNewItem(new QuestItem(id, name));
	}

	/**
	 * Check if the player has any of the given items in a certain {@link ItemSlots}
	 *
	 * @param name the display text
	 * @param slot the slot to check
	 * @param items list of valid item ids
	 */
	public EquippedItemRequirement(String name, ItemSlots slot, List<Integer> items)
	{
		this.slot = slot;
		this.name = name;
		addNewItem(name, items.get(0));
		items.forEach(item -> addNewItem(name, item));
	}

	/**
	 * Check if the player has any of the given items in a certain {@link ItemSlots}
	 *
	 * @param name the display text
	 * @param slot the slot to check
	 * @param items list of valid item ids
	 */
	public EquippedItemRequirement(String name, ItemSlots slot, @Nonnull Integer... items)
	{
		this.slot = slot;
		this.name = name;
		addNewItem(name, items[0]);
		Stream.of(items).forEach(item -> addNewItem(name, item));
	}

	@Override
	public void setOverlayReplacement(Requirement panelReplacement)
	{
		super.setOverlayReplacement(panelReplacement);
		if (panelReplacement instanceof ItemRequirement)
		{
			ItemRequirement req = (ItemRequirement) panelReplacement;
			addNewItem(req.getDisplayText(), req.getId());
		}
	}

	public void addNewItem(String text, int itemID)
	{
		addNewItem(new QuestItem(itemID, text));
	}

	public void addNewItem(QuestItem item)
	{
		items.put(item.getItemID(), item);
		ItemDefinitions.addQuestItem(item);
	}

	@Override
	public boolean check(Client client)
	{
		return slot.contains(client, i -> items.containsKey(i.getId()));
	}

	@Override
	protected List<LineComponent> getOverlayDisplayText(Client client)
	{
		List<LineComponent> lines = new ArrayList<>();
		Color equipColor = Color.RED;

		String text = name;
		for (Map.Entry<Integer, QuestItem> entry : items.entrySet())
		{
			int itemID = entry.getKey();
			if (slot.contains(client, i -> i != null && i.getId() == itemID))
			{
				equipColor = Color.GREEN;
				text = entry.getValue().getDisplayText();
				break;
			}
		}

		lines.add(LineComponent.builder()
			.left(text)
			.leftColor(equipColor)
			.build());
		if (slot.getSlotIdx() >= ItemSlots.ANY_EQUIPPED.getSlotIdx())
		{
			// >= -1 means it's an equipment slot, not an inventory slot
			lines.add(LineComponent.builder()
				.left("(equipped)")
				.leftColor(equipColor)
				.build());
		}
		return lines;
	}

	@Override
	public String getDisplayText()
	{
		return name;
	}
}
