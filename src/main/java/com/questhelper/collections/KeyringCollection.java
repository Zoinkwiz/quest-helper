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
package com.questhelper.collections;

import com.questhelper.QuestHelperConfig;
import lombok.Getter;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.config.ConfigManager;

public enum KeyringCollection
{
	SHINY_KEY(ItemID.IKOV_SHINYKEY),
	BRASS_KEY(ItemID.EDGEVILLEDUNGEONKEY),
	METAL_KEY(ItemID.METAL_KEY),
	WROUGHT_IRON_KEY(ItemID.THGOODMINEKEY),
	DUSTY_KEY(ItemID.DUSTY_KEY),
	BATTERED_KEY(ItemID.ELEMENTAL_WORKSHOP_KEY),
	CRYSTAL_MINE_KEY(ItemID.HAUNTEDMINE_REWARD_KEY),
	KEY(ItemID.ELID_KEY),
	MAZE_KEY(ItemID.MELZARKEY),
	WEAPON_STORE_KEY(ItemID.PHOENIXKEY2),
	BONE_KEY(ItemID.ZQBONEKEY),
	ENCHANTED_KEY(ItemID.MAKINGHISTORY_KEY),
	NEW_KEY(ItemID.MOURNING_EXCAVATION_KEY);

	private final String CONFIG_GROUP = QuestHelperConfig.QUEST_BACKGROUND_GROUP;

	@Getter
	private final int itemID;

	KeyringCollection(int itemID)
	{
		this.itemID = itemID;
	}

	/// The name of this key as seen in the chat box.
	public String chatboxText()
	{
		return name().toLowerCase().replaceAll("_", " ");
	}

	public String runeliteName()
	{
		return name().toLowerCase().replaceAll("_", "");
	}

	public boolean hasKeyOnKeyRing(ConfigManager configManager)
	{
		var value = configManager.getRSProfileConfiguration(CONFIG_GROUP, runeliteName());

		return "true".equals(value);
	}

	public void setHasKeyOnKeyRing(ConfigManager configManager, boolean value)
	{
		if (configManager.getRSProfileKey() == null)
		{
			return;
		}

		configManager.setRSProfileConfiguration(CONFIG_GROUP, runeliteName(), value ? "true" : "false");
	}
}
