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
import com.questhelper.domain.AccountType;
import com.questhelper.managers.QuestContainerManager;
import com.questhelper.runeliteobjects.extendedruneliteobjects.QuestCompletedWidget;
import lombok.Getter;
import lombok.NonNull;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneScapeProfileType;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;

@Singleton
public class PlayerStateManager
{
	private final KeyringCollection[] keyringKeys = KeyringCollection.values();

	@Inject
	Client client;

	@Inject
	ConfigManager configManager;

	@Inject
	EventBus eventBus;

	@Inject
	QuestCompletedWidget playerQuestCompleteWidget;

	@Inject
	BarbarianTrainingStateTracker barbarianTrainingStateTracker;

	WorldPoint lastPlayerPos = null;

	private boolean loggedInStateKnown = false;
	private RuneScapeProfileType worldType;

	/**
	 * The type of the logged in account (e.g. ironman, hardcore ironman)
	 * Quest helpers should use this value when building their requirements instead of fetching the value themselves.
	 */
	@Getter
	private @NonNull AccountType accountType = AccountType.NORMAL;

	public void startUp()
	{
		AchievementDiaryStepManager.setup(configManager);
		barbarianTrainingStateTracker.startUp(configManager, eventBus);
	}

	public void shutDown()
	{
		barbarianTrainingStateTracker.shutDown(eventBus);
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() != ChatMessageType.GAMEMESSAGE) return;
		if (chatMessage.getMessage().contains("to your key ring."))
		{
			for (var keyringKey : keyringKeys)
			{
				if (chatMessage.getMessage().contains("You add the " + keyringKey.chatboxText()))
				{
					keyringKey.setHasKeyOnKeyRing(configManager, true);

					QuestContainerManager.getKeyRingData().add(client.getTickCount(), keyringKey.getItemID(), 1);
					break;
				}
			}
		}
		else if (chatMessage.getMessage().contains("from your key ring."))
		{
			for (var keyringKey : keyringKeys)
			{
				if (chatMessage.getMessage().contains("You remove the " + keyringKey.chatboxText()))
				{
					keyringKey.setHasKeyOnKeyRing(configManager, false);

					QuestContainerManager.getKeyRingData().removeByItemID(client.getTickCount(), keyringKey.getItemID());
					break;
				}
			}
		}
		else if (chatMessage.getMessage().contains("Achievement Diary Stage Task - "))
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

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getVarbitId() == VarbitID.IRONMAN) {
			var newAccountType = AccountType.get(event.getValue());
			if (newAccountType == null) {
				// The account type value is invalid, leave previous account type as is
				return;
			}

			accountType = newAccountType;
		}
	}


	public String getPlayerName()
	{
		if (client.getLocalPlayer() == null)
		{
			return null;
		}
		return client.getLocalPlayer().getName();
	}

	public void loadInitialStateFromConfig()
	{
		if (loggedInStateKnown)
		{
			return;
		}

		var localPlayer = client.getLocalPlayer();
		if (localPlayer != null && localPlayer.getName() != null)
		{
			loggedInStateKnown = true;
			loadState();
		}
	}

	private void loadState()
	{
		// Only re-load from config if loading from a new profile
		if (!RuneScapeProfileType.getCurrent(client).equals(worldType))
		{
			// Load key ring state from config
			loadKeyRingFromConfig();
		}
	}

	private void loadKeyRingFromConfig()
	{
		var keys = new ArrayList<Item>();

		for (var keyringKey : keyringKeys)
		{
			if (keyringKey.hasKeyOnKeyRing(configManager))
			{
				keys.add(new Item(keyringKey.getItemID(), 1));
			}
		}

		QuestContainerManager.getKeyRingData().update(client.getTickCount(), keys.toArray(new Item[0]));
	}

	public void emptyState()
	{
		worldType = null;
	}

	public void setUnknownInitialState()
	{
		loggedInStateKnown = false;
	}
}
