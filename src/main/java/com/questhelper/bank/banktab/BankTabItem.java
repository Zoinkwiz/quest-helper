/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
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
package com.questhelper.bank.banktab;

import com.questhelper.requirements.item.ItemRequirement;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class BankTabItem
{
	@Getter
	private final int quantity;

	@Getter
	@Setter
	private String text;

	@Getter
	private final List<Integer> itemIDs;

	@Getter
	private final Integer displayID;

	@Getter
	private final String details;

	@Getter
	private final ItemRequirement itemRequirement;

	public BankTabItem(ItemRequirement item, int displayID)
	{
		this.quantity = item.getQuantity();
		this.text = item.getName();
		this.itemIDs = Collections.singletonList(displayID);
		this.details = item.getTooltip();
		this.displayID = displayID;
		this.itemRequirement = item;
	}

	public BankTabItem(ItemRequirement item)
	{
		this.quantity = item.getQuantity();
		this.text = item.getName();
		this.itemIDs = Collections.singletonList(item.getId());
		this.details = item.getTooltip();
		this.displayID = -1;
		this.itemRequirement = item;
	}
}
