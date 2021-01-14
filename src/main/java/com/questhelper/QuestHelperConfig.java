/*
 * Copyright (c) 2020, Zoinkwiz
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
package com.questhelper;

import com.questhelper.panel.QuestSelectPanel;
import com.questhelper.panel.questorders.QuestOrders;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("questhelper")
public interface QuestHelperConfig extends Config
{
	enum QuestOrdering
	{
		A_TO_Z,
		OPTIMAL;
	}

	enum QuestFilter
	{
		HIDE_DONE,
		HIDE_LACKING_REQ;
	}

	@ConfigItem(
		keyName = "autostartQuests",
		name = "Auto start helper",
		description = "Automatically start the quest helper when you start a quest"
	)
	default boolean autoStartQuests()
	{
		return true;
	}

	@ConfigItem(
		keyName = "textHighlightColor",
		name = "Text highlight color",
		description = "Change the color of dialog choices highlighted by the helper"
	)
	default Color textHighlightColor()
	{
		return Color.CYAN.darker();
	}

	@ConfigItem(
		keyName = "showTextHighlight",
		name = "Highlight correct dialog",
		description = "Highlight correct dialog choices"
	)
	default boolean showTextHighlight()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSymbolOverlay",
		name = "Display icons on NPCs and objects",
		description = "Choose whether NPCs should icons marking them as the current target or not"
	)
	default boolean showSymbolOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "targetOverlayColor",
		name = "Color of target overlay",
		description = "Change the color which target NPCs/Objects will be highlighted with"
	)
	default Color targetOverlayColor()
	{
		return Color.CYAN;
	}

	@ConfigItem(
		keyName = "orderListBy",
		name = "Quest order",
		description = "Configures which way to order the quest list",
		position = 1
	)
	default QuestOrdering orderListBy()
	{
		return QuestOrdering.A_TO_Z;
	}

	@ConfigItem(
		keyName = "filterListBy",
		name = "Filter",
		description = "Configures what to filter out in the quest list",
		position = 1
	)
	default QuestFilter filterListBy()
	{
		return QuestFilter.HIDE_DONE;
	}
}
