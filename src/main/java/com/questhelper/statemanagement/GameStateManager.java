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
package com.questhelper.statemanagement;

import com.questhelper.collections.KeyringCollection;
import com.questhelper.requirements.item.KeyringRequirement;
import com.questhelper.runeliteobjects.extendedruneliteobjects.QuestCompletedWidget;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;

@Singleton
public class GameStateManager
{
	@Inject
	Client client;

	@Inject
	ConfigManager configManager;

	@Inject
	QuestCompletedWidget playerQuestCompleteWidget;

	List<KeyringRequirement> keyringKeys;

	WorldPoint lastPlayerPos = null;

	public void startUp()
	{
		keyringKeys = KeyringCollection.allKeyRequirements(configManager);
		AchievementDiaryStepManager.setup(configManager);
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (keyringKeys == null || chatMessage.getType() != ChatMessageType.GAMEMESSAGE) return;
		if (chatMessage.getMessage().contains("to your key ring."))
		{
			for (KeyringRequirement keyringKey : keyringKeys)
			{
				if (chatMessage.getMessage().contains("You add the " + keyringKey.chatboxText()))
				{
					keyringKey.setConfigValue("true");
				}
			}
		}
		if (chatMessage.getMessage().contains("from your key ring."))
		{
			for (KeyringRequirement keyringKey : keyringKeys)
			{
				if (chatMessage.getMessage().contains("You remove the " + keyringKey.chatboxText()))
				{
					keyringKey.setConfigValue("false");
				}
			}
		}

		if (chatMessage.getMessage().contains("Achievement Diary Stage Task - "))
		{
			AchievementDiaryStepManager.check(client);
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		Player player = client.getLocalPlayer();
		if (player != null)
		{
			WorldPoint newPos = player.getWorldLocation();
			if (newPos != null && lastPlayerPos != null)
			{
				if (newPos.distanceTo(lastPlayerPos) != 0)
				{
					playerQuestCompleteWidget.close(client);
				}
			}
			lastPlayerPos = newPos;
		}
	}
}
