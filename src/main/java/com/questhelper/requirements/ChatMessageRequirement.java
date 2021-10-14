/*
 *
 *  * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice, this
 *  *    list of conditions and the following disclaimer.
 *  * 2. Redistributions in binary form must reproduce the above copyright notice,
 *  *    this list of conditions and the following disclaimer in the documentation
 *  *    and/or other materials provided with the distribution.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.questhelper.requirements;

import com.questhelper.requirements.conditional.ConditionForStep;
import java.util.Arrays;
import java.util.List;
import lombok.Setter;
import net.runelite.api.Client;

public class ChatMessageRequirement extends ConditionForStep
{
	@Setter
	private boolean hasReceivedChatMessage = false;

	private Requirement condition;

	@Setter
	private ChatMessageRequirement invalidateRequirement;

	private final List<String> messages;

	public ChatMessageRequirement(String... message)
	{
		this.messages = Arrays.asList(message);
	}

	public ChatMessageRequirement(Requirement condition, String... message)
	{
		this.condition = condition;
		this.messages = Arrays.asList(message);
	}

	@Override
	public boolean check(Client client)
	{
		return hasReceivedChatMessage;
	}

	public void validateCondition(Client client, String chatMessage)
	{
		if (!hasReceivedChatMessage)
		{
			if (messages.contains(chatMessage))
			{
				if (condition == null || condition.check(client))
				{
					hasReceivedChatMessage = true;
				}
			}
		}
		else if (invalidateRequirement != null)
		{
			invalidateRequirement.validateCondition(client, chatMessage);
			if (invalidateRequirement.check(client))
			{
				invalidateRequirement.setHasReceivedChatMessage(false);
				setHasReceivedChatMessage(false);
			}
		}
	}
}
