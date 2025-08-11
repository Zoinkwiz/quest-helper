/*
 * Copyright (c) 2025, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.questhelper.managers.QuestManager;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import net.runelite.api.Client;
import net.runelite.api.MessageNode;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatCommandManager;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.util.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class QuestHelperChatCommands
{
	public static final String COMMAND = "!qh";

	@Inject
	ChatCommandManager chatCommandManager;

	@Inject
	QuestManager questManager;

	@Inject
	Client client;

	private boolean chatMessageReceivedThisTick;

	public void startUp()
	{
		chatCommandManager.registerCommand(COMMAND, this::replaceQhCommand);
	}

	public void shutDown()
	{
		chatCommandManager.unregisterCommand(COMMAND);
	}

	public void hasReceivedChatMessageThisTick()
	{
		chatMessageReceivedThisTick = true;
	}

	private void replaceQhCommand(ChatMessage chatMessage, String message)
	{
		if (!chatMessageReceivedThisTick) return;
		chatMessageReceivedThisTick = false;

		String questName = message.substring(COMMAND.length() + 1);
		QuestHelper questHelper = QuestHelperQuest.getByName(questName);
		if (questHelper == null) return;

		final String newMessage = new ChatMessageBuilder()
			.append(ChatColorType.NORMAL)
			.append("Open ")
			.append(ChatColorType.HIGHLIGHT)
			.append(questHelper.getQuest().getName())
			.append(ChatColorType.NORMAL)
			.append(" in Quest Helper.")
			.build();

		MessageNode node = chatMessage.getMessageNode();
		node.setRuneLiteFormatMessage(newMessage);
		client.refreshChat();
	}

	Pattern OPEN_X_IN_QH = Pattern.compile(
		"Open (?<target>.+?) in Quest Helper.$"
	);

	public void updateMessageClickbox()
	{
		Widget messages = client.getWidget(InterfaceID.Chatbox.SCROLLAREA);
		if (messages == null || messages.getChildren() == null) return;
		Widget[] messageArray = messages.getChildren();

		if (messageArray == null) return;

		for (Widget widget : messageArray)
		{
			String text = widget.getText();
			String tagsRemovedText = Text.removeTags(text);
			Matcher m = OPEN_X_IN_QH.matcher(tagsRemovedText);

			if (!m.matches())
			{
				widget.setHasListener(false);
				continue;
			}
			String questName = m.group("target").trim();
			QuestHelper questHelper = QuestHelperQuest.getByName(questName);
			if (questHelper == null)
			{
				widget.setHasListener(false);
				continue;
			}
			widget.setHasListener(true);
			widget.setAction(1, "Start up helper " + questHelper.getQuest().getName());
			widget.setOnOpListener((JavaScriptCallback) e ->
				questManager.startUpQuest(questHelper, true));
		}
	}
}
