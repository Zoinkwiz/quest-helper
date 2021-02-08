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
package com.questhelper.requirements.npc;

import com.questhelper.requirements.AbstractRequirement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.runelite.api.Client;

public class FollowerRequirement extends AbstractRequirement
{
	List<Integer> followers;
	String text;

	public FollowerRequirement(String text, Integer... followers)
	{
		this.text = text;
		this.followers = new ArrayList<>();
		Collections.addAll(this.followers, followers);
	}

	public FollowerRequirement(String text, List<Integer> followers)
	{
		this.text = text;
		this.followers = followers;
	}

	@Override
	public boolean check(Client client)
	{
		return client.getNpcs()
			.stream()
			.filter(npc -> npc.getInteracting() != null) // we need this check because Client#getLocalPlayer is Nullable
			.filter(npc -> npc.getInteracting() == client.getLocalPlayer())
			.anyMatch(npc -> followers.contains(npc.getId()));
	}

	@Override
	public String getDisplayText()
	{
		return text;
	}
}
