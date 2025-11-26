/*
 * Copyright (c) 2023, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.managers;

import com.questhelper.QuestHelperConfig;
import net.runelite.api.ChatMessageType;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NewVersionManager
{
	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private ConfigManager configManager;

	private final String LAST_VERSION_SEEN_CONFIG_KEY = "lastversionchecked";

	private final String UPDATE_CHAT_TEXT = "Quest Helper has been updated to 4.11.0.1! This adds a new helper, 'Sea Charting'!";

	public void updateChatWithNotificationIfNewVersion()
	{
		if (hasLoggedInThisVersion()) return;

		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, LAST_VERSION_SEEN_CONFIG_KEY, UPDATE_CHAT_TEXT);

		chatMessageManager.queue(QueuedMessage.builder()
			.type(ChatMessageType.GAMEMESSAGE)
			.runeLiteFormattedMessage(UPDATE_CHAT_TEXT)
			.build());
	}

	private boolean hasLoggedInThisVersion()
	{
		String lastVersionChecked = configManager.getConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, LAST_VERSION_SEEN_CONFIG_KEY);
		return lastVersionChecked != null && lastVersionChecked.equals(UPDATE_CHAT_TEXT);
	}
}
