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
package com.questhelper.requirements;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;

// This is used where you need multiple chatMessageRequirements to checked together, where one alone isn't enough context
public class MultiChatMessageRequirement extends ChatMessageRequirement
{
	Map<ChatMessageRequirement, Integer> requiredMessages;

	int maxTicksDiff = 1;

	public MultiChatMessageRequirement(ChatMessageRequirement... requiredMessages)
	{
		this.requiredMessages = new HashMap<>();
		for (ChatMessageRequirement requirement : requiredMessages)
		{
			this.requiredMessages.put(requirement, -1);
		}
	}

	// TODO: Currently this wouldn't allow for re-checking should an invalidateCondition occur for a sub-condition
	@Override
	public boolean validateCondition(Client client, ChatMessage chatMessage)
	{
		if (hasReceivedChatMessage) return true;
		AtomicInteger minTime = new AtomicInteger(Integer.MAX_VALUE);
		AtomicInteger maxTime = new AtomicInteger(Integer.MIN_VALUE);
		requiredMessages.forEach((requirement, lastSeenTime) -> {
			if (requirement.validateCondition(client, chatMessage))
			{
				requiredMessages.put(requirement, chatMessage.getTimestamp());
			}
			if (minTime.get() > requiredMessages.get(requirement))
			{
				minTime.set(requiredMessages.get(requirement));
			}

			if (maxTime.get() < requiredMessages.get(requirement))
			{
				maxTime.set(requiredMessages.get(requirement));
			}

			if (minTime.get() != -1 && maxTime.get() != -1 && maxTime.get() - minTime.get() < maxTicksDiff * 600)
			{
				hasReceivedChatMessage = true;
			}
		});

		return hasReceivedChatMessage;
	}

	@Override
	public boolean check(Client client)
	{
		return hasReceivedChatMessage;
	}
}
