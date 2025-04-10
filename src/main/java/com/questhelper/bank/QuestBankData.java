/*
 * Copyright (c) 2022, Adam <Adam@sigterm.info>
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
package com.questhelper.bank;

import lombok.Data;
import net.runelite.api.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
class QuestBankData
{
	int[] idAndQuantity;

	QuestBankData()
	{
		idAndQuantity = new int[0];
	}

	void set(List<Item> items)
	{
		int[] newIdAndQuantity = new int[(items.size() + 1) * 2];
		for (int i = 0; i < items.size(); i++)
		{
			Item item = items.get(i);
			newIdAndQuantity[i*2] = item.getId();
			newIdAndQuantity[(i*2)+1] = item.getQuantity();
		}
		idAndQuantity = newIdAndQuantity;
	}

	void set(Item[] items)
	{
		set(Arrays.asList(items));
	}

	void setEmpty()
	{
		idAndQuantity = new int[0];
	}

	List<Item> getAsList()
	{
		List<Item> items = new ArrayList<>();

		if (idAndQuantity == null) return items;

		for (int i = 0; i < idAndQuantity.length - 2; i += 2)
		{
			items.add(new Item(idAndQuantity[i], idAndQuantity[i+1]));
		}
		return items;
	}
}
