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

import com.questhelper.requirements.item.KeyringRequirement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.client.config.ConfigManager;

public enum KeyringCollection
{
	SHINY_KEY(ItemID.SHINY_KEY),
	BRASS_KEY(ItemID.BRASS_KEY),
	METAL_KEY(ItemID.METAL_KEY),
	WROUGHT_IRON_KEY(ItemID.WROUGHT_IRON_KEY),
	DUSTY_KEY(ItemID.DUSTY_KEY),
	BATTERED_KEY(ItemID.BATTERED_KEY),
	CRYSTAL_MINE_KEY(ItemID.CRYSTALMINE_KEY),
	KEY(ItemID.ANCESTRAL_KEY),
	MAZE_KEY(ItemID.MAZE_KEY),
	WEAPON_STORE_KEY(ItemID.WEAPON_STORE_KEY),
	BONE_KEY(ItemID.BONE_KEY),
	ENCHANTED_KEY(ItemID.ENCHANTED_KEY),
	NEW_KEY(ItemID.NEW_KEY);

	@Getter
	private final int itemID;
	KeyringCollection(int itemID)
	{
		this.itemID = itemID;
	}

	public String toChatText()
	{
		return name().toLowerCase().replaceAll("_", " ");
	}

	public String runeliteName()
	{
		return name().toLowerCase().replaceAll("_", "");
	}

	public static List<KeyringRequirement> allKeyRequirements(ConfigManager configManager)
	{
		List<KeyringRequirement> keys = new ArrayList<>();
		for (KeyringCollection keyringCollection : Collections.unmodifiableList(Arrays.asList(values())))
		{
			keys.add(new KeyringRequirement(configManager, keyringCollection));
		}

		return keys;
	}

	public KeyringRequirement getRequirement(ConfigManager configManager)
	{
		return new KeyringRequirement(configManager, this);
	}
}
