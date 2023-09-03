/*
 * Copyright (c) 2022, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.requirements.item;

import com.questhelper.KeyringCollection;
import com.questhelper.QuestHelperConfig;
import com.questhelper.requirements.runelite.RuneliteRequirement;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.client.config.ConfigManager;

public class KeyringRequirement extends ItemRequirement
{
	RuneliteRequirement runeliteRequirement;

	KeyringCollection keyringCollection;

	ConfigManager configManager;

	ItemRequirement keyring;

	public KeyringRequirement(String name, ConfigManager configManager, KeyringCollection key)
	{
		super(name, key.getItemID());
		keyring = new ItemRequirement("Steel key ring", ItemID.STEEL_KEY_RING);
		runeliteRequirement = new RuneliteRequirement(configManager, key.runeliteName(),
			"true", key.toChatText());
		this.keyringCollection = key;
		this.configManager = configManager;
	}

	public KeyringRequirement(ConfigManager configManager, KeyringCollection key)
	{
		super(key.toChatText(), key.getItemID());
		keyring = new ItemRequirement("Steel key ring", ItemID.STEEL_KEY_RING);
		runeliteRequirement = new RuneliteRequirement(configManager, key.runeliteName(),
			"true", key.toChatText());
		this.keyringCollection = key;
		this.configManager = configManager;
	}

	public String chatboxText()
	{
		return keyringCollection.toChatText();
	}

	public void setConfigValue(String value)
	{
		runeliteRequirement.setConfigValue(value);
	}

	@Override
	public ItemRequirement copy()
	{
		KeyringRequirement newItem = new KeyringRequirement(getName(), configManager, keyringCollection);
		newItem.addAlternates(alternateItems);
		newItem.setDisplayItemId(getDisplayItemId());
		newItem.setExclusiveToOneItemType(exclusiveToOneItemType);
		newItem.setHighlightInInventory(highlightInInventory);
		newItem.setDisplayMatchedItemName(isDisplayMatchedItemName());
		newItem.setConditionToHide(getConditionToHide());
		newItem.setQuestBank(getQuestBank());
		newItem.setTooltip(getTooltip());
		newItem.setUrlSuffix(getUrlSuffix());

		return newItem;
	}

	@Override
	public boolean check(Client client, boolean checkConsideringSlotRestrictions, List<Item> items)
	{
		boolean match = runeliteRequirement.check(client);

		if (match && keyring.check(client))
		{
			return true;
		}

		return super.check(client, checkConsideringSlotRestrictions, items);
	}

	@Override
	public Color getColorConsideringBank(Client client, boolean checkConsideringSlotRestrictions,
										 List<Item> bankItems, QuestHelperConfig config)
	{
		Color color = config.failColour();
		if (!this.isActualItem())
		{
			color = Color.GRAY;
		}
		else if (super.check(client, checkConsideringSlotRestrictions, new ArrayList<>()))
		{
			color = config.passColour();
		}

		if (color == config.failColour() && bankItems != null)
		{
			if (super.check(client, false, bankItems))
			{
				color = Color.WHITE;
			}
		}

		if (color == config.failColour())
		{
			boolean match = runeliteRequirement.check(client);

			if (match)
			{
				color = Color.ORANGE;
			}
		}
		return color;
	}
}
