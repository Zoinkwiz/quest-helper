/*
 *
 *  * Copyright (c) 2021, Zoinkwiz
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
package com.questhelper.requirements.widget;

import com.questhelper.requirements.SimpleRequirement;
import javax.annotation.Nullable;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

public class WidgetPresenceRequirement extends SimpleRequirement
{
	protected boolean onlyNeedToPassOnce;

	@Getter
	protected final int groupId;

	protected final int childId;
	protected int childChildId = -1;

	public WidgetPresenceRequirement(int groupId, int childId, int childChildId)
	{
		this.groupId = groupId;
		this.childId = childId;
		this.childChildId = childChildId;
	}

	public WidgetPresenceRequirement(int groupId, int childId)
	{
		this.groupId = groupId;
		this.childId = childId;
	}

	@Override
	public boolean check(Client client)
	{
		if (onlyNeedToPassOnce && state)
		{
			return true;
		}
		return checkWidget(client);
	}

	/* TODO: May be better to use onWidgetLoaded, however unsure if anything would break due to
		model check extending this potentially not having model fully loaded at time of calling
	*/
	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		if (onlyNeedToPassOnce && state)
		{
			return;
		}

		setState(checkWidget(client));
	}

	@Nullable
	protected Widget getWidget(Client client)
	{
		Widget widget = client.getWidget(groupId, childId);
		if (widget == null)
		{
			return null;
		}
		if (childChildId != -1)
		{
			return widget.getChild(childChildId);
		}
		return widget;
	}

	public boolean checkWidget(Client client)
	{
		return getWidget(client) != null;
	}
}

