/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.config;

import com.questhelper.QuestHelperConfig;
import net.runelite.client.config.ConfigManager;

public class AssistanceLevel
{
	public static final String ASSISTANCE_LEVEL_KEY = "assistanceLevel";

	public static void setToMinimumAssistance(ConfigManager configManager)
	{
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "autoStartQuests", true);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "autoOpenSidebar", true);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "showOverlayPanel", false);

		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "showTextHighlight", false);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "showSymbolOverlay", false);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "highlightStyleNpcs", QuestHelperConfig.NpcHighlightStyle.NONE);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "highlightStyleObjects", QuestHelperConfig.ObjectHighlightStyle.NONE);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "highlightStyleGroundItems", QuestHelperConfig.GroundItemHighlightStyle.NONE);

		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "highlightStyleInventoryItems", QuestHelperConfig.InventoryItemHighlightStyle.NONE);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "showMiniMapArrow", false);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "showWorldLines", false);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "showWidgetHints", false);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "solvePuzzles", false);

		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, ASSISTANCE_LEVEL_KEY, "minimum");
	}

	public static void setToMediumAssistance(ConfigManager configManager)
	{
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "autoStartQuests", true);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "autoOpenSidebar", true);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "showOverlayPanel", true);

		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "showTextHighlight", true);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "showSymbolOverlay", false);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "highlightStyleNpcs", QuestHelperConfig.NpcHighlightStyle.NONE);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "highlightStyleObjects", QuestHelperConfig.ObjectHighlightStyle.NONE);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "highlightStyleGroundItems", QuestHelperConfig.GroundItemHighlightStyle.NONE);

		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "highlightStyleInventoryItems", QuestHelperConfig.InventoryItemHighlightStyle.NONE);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "showMiniMapArrow", true);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "showWorldLines", true);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "showWidgetHints", false);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "solvePuzzles", false);

		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, ASSISTANCE_LEVEL_KEY, "medium");
	}

	public static void setToFullAssistance(ConfigManager configManager)
	{
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "autoStartQuests", true);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "autoOpenSidebar", true);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "showOverlayPanel", true);

		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "showTextHighlight", true);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "showSymbolOverlay", true);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "highlightStyleNpcs", QuestHelperConfig.NpcHighlightStyle.OUTLINE);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "highlightStyleObjects", QuestHelperConfig.ObjectHighlightStyle.OUTLINE);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "highlightStyleGroundItems", QuestHelperConfig.GroundItemHighlightStyle.OUTLINE);

		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "highlightStyleInventoryItems", QuestHelperConfig.InventoryItemHighlightStyle.OUTLINE);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "showMiniMapArrow", true);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "showWorldLines", true);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "showWidgetHints", true);
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "solvePuzzles", true);

		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, ASSISTANCE_LEVEL_KEY, "full");
	}
}
