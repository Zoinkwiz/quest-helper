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

import com.google.inject.Injector;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.bank.QuestBank;
import com.questhelper.bank.banktab.QuestBankTab;
import com.questhelper.bank.banktab.QuestHelperBankTagService;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Player;
import net.runelite.client.eventbus.EventBus;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class QuestBankManager
{
	@Inject
	private QuestBank questBank;

	@Getter
	@Inject
	private QuestHelperBankTagService bankTagService;

	@Inject
	private QuestBankTab questBankTab;

	private boolean loggedInStateKnown;

	public void startUp(Injector injector, EventBus eventBus)
	{
		questBankTab.startUp();
		injector.injectMembers(questBankTab);
		questBankTab.register(eventBus);
	}

	public void shutDown(EventBus eventBus)
	{
		questBankTab.unregister(eventBus);
		questBankTab.shutDown();
	}

	public void loadInitialStateFromConfig(Client client)
	{
		if (!loggedInStateKnown)
		{
			Player localPlayer = client.getLocalPlayer();
			if (localPlayer != null && localPlayer.getName() != null)
			{
				loggedInStateKnown = true;
				loadState();
			}
		}
	}

	public void setUnknownInitialState()
	{
		loggedInStateKnown = false;
	}

	public void loadState()
	{
		questBank.loadState();
	}

	public void startUpQuest()
	{
		questBankTab.startUp();
	}

	public void shutDownQuest()
	{
		questBankTab.shutDown();
	}

	public List<Item> getBankItems()
	{
		return questBank.getBankItems();
	}

	public void refreshBankTab()
	{
		questBankTab.refreshBankTab();
	}

	public void updateLocalBank(ItemContainer itemContainer)
	{
		questBank.updateLocalBank(itemContainer.getItems());
	}

	public void updateBankForQuestSpeedrunningWorld()
	{
		questBank.updateLocalBank(new Item[]{ });
	}

	public void saveBankToConfig()
	{
		questBank.saveBankToConfig();
	}

	public void emptyState()
	{
		questBank.emptyState();
	}
}
