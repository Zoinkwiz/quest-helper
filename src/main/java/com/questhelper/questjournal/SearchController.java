/*
 * Copyright (c) 2026, nanopink
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
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.questjournal;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.SoundEffectID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.game.chatbox.ChatboxTextInput;

/** Manages chatbox search and Quest Journal UI sounds. */
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
final class SearchController
{
	@NonNull
	private final Client client;
	@NonNull
	private final ClientThread clientThread;
	@NonNull
	private final ChatboxPanelManager chatboxPanelManager;

	private volatile ChatboxTextInput searchInput;

	void toggle(
		String currentQuery,
		Consumer<String> updateQuery,
		BooleanSupplier soundAllowed)
	{
		assert client.isClientThread();
		if (searchInput != null)
		{
			close(true, updateQuery, soundAllowed);
			return;
		}

		playUiBoopOnClientThread(soundAllowed);
		ChatboxTextInput input = chatboxPanelManager.openTextInput("Search quest journal");
		searchInput = input;
		try
		{
			input.value(currentQuery)
				.onChanged(value ->
				{
					if (searchInput == input)
					{
						updateQuery.accept(value);
					}
				})
				.onDone(value -> false)
				.onClose(() -> searchClosed(input, updateQuery))
				.build();
		}
		catch (RuntimeException exception)
		{
			if (searchInput == input)
			{
				searchInput = null;
			}
			throw exception;
		}
	}

	void close(Consumer<String> updateQuery)
	{
		close(false, updateQuery, () -> false);
	}

	void close(
		boolean playSound,
		Consumer<String> updateQuery,
		BooleanSupplier soundAllowed)
	{
		assert client.isClientThread();
		ChatboxTextInput input = searchInput;
		if (input != null)
		{
			if (playSound)
			{
				playUiBoopOnClientThread(soundAllowed);
			}
			if (chatboxPanelManager.getCurrentInput() == input || !input.isBuilt())
			{
				chatboxPanelManager.close();
			}
			else if (searchInput == input)
			{
				searchInput = null;
			}
		}
		updateQuery.accept("");
	}

	boolean isInputActiveOrPending()
	{
		return searchInput != null || chatboxPanelManager.getCurrentInput() != null;
	}

	void playUiBoop(BooleanSupplier soundAllowed)
	{
		if (client.isClientThread())
		{
			playUiBoopOnClientThread(soundAllowed);
		}
		else
		{
			clientThread.invokeLater(() -> playUiBoopOnClientThread(soundAllowed));
		}
	}

	private void searchClosed(ChatboxTextInput input, Consumer<String> updateQuery)
	{
		if (!client.isClientThread())
		{
			clientThread.invokeLater(() -> searchClosed(input, updateQuery));
			return;
		}
		if (searchInput != input)
		{
			return;
		}
		searchInput = null;
		updateQuery.accept("");
	}

	private void playUiBoopOnClientThread(BooleanSupplier soundAllowed)
	{
		assert client.isClientThread();
		if (soundAllowed.getAsBoolean() && client.getGameState() == GameState.LOGGED_IN)
		{
			client.playSoundEffect(SoundEffectID.UI_BOOP);
		}
	}
}
