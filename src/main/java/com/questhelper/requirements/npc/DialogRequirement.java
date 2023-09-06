/*
 * Copyright (c) 2023, Zoinkwiz
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
package com.questhelper.requirements.npc;

import com.questhelper.requirements.SimpleRequirement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.Setter;
import net.runelite.api.Client;

public class DialogRequirement extends SimpleRequirement
{
	@Setter
	String talkerName;
	final List<String> text = new ArrayList<>();
	final boolean mustBeActive;

	boolean hasSeenDialog = false;

	public DialogRequirement(String... text)
	{
		this.talkerName = null;
		this.text.addAll(Arrays.asList(text));
		this.mustBeActive = false;
	}
	public DialogRequirement(String talkerName, String text, boolean mustBeActive)
	{
		this.talkerName = talkerName;
		this.text.add(text);
		this.mustBeActive = mustBeActive;
	}

	public DialogRequirement(String text)
	{
		this(null, text, false);
	}

	@Override
	public boolean check(Client client)
	{
		return hasSeenDialog;
	}

	public void validateCondition(String dialogMessage)
	{
		if (!hasSeenDialog)
		{
			hasSeenDialog = isCurrentDialogMatching(dialogMessage);
		}
		// If it's not the dialog,
		else if (mustBeActive)
		{
			hasSeenDialog = isCurrentDialogMatching(dialogMessage);
		}
	}

	private boolean isCurrentDialogMatching(String dialogMessage)
	{
		if (talkerName != null && !dialogMessage.contains(talkerName + "|")) return false;
		return text.stream().anyMatch(dialogMessage::contains);
	}
}
