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
package com.questhelper.requirements.item;

import com.questhelper.QuestHelperConfig;
import com.questhelper.requirements.util.ItemSlots;
import java.awt.Color;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.runelite.api.Client;

/**
 * Requirement that checks if a player has no item in a specified {@link ItemSlots}.
 */
public class NoItemRequirement extends ItemRequirement
{
	private final ItemSlots slot;
	private final int matchingItemID;

	/**
	 * Checks if a player has no items in a given {@link ItemSlots}
	 *
	 * @param text display text
	 * @param slot the slot to check
	 */
	public NoItemRequirement(String text, @Nonnull ItemSlots slot)
	{
		super(text, -1, -1);
		this.slot = slot;
		matchingItemID = -1;
	}

	@Override
	public boolean check(Client client)
	{
		return slot.checkInventory(client, Objects::isNull);
	}

	@Override
	public Color getColor(Client client, QuestHelperConfig config)
	{
		return check(client) ? config.passColour() : config.failColour();
	}

	@Override
	public String getDisplayText()
	{
		return "Nothing in your " + slot.getName();
	}
}
