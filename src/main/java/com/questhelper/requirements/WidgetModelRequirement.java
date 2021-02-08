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
package com.questhelper.requirements;

import com.questhelper.requirements.SimpleRequirement;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;

public class WidgetModelRequirement extends SimpleRequirement
{
	@Setter
	@Getter
	protected boolean hasPassed;
	protected boolean onlyNeedToPassOnce;

	@Getter
	private final int groupId;

	private final int childId;
	private final int id;
	private int childChildId = -1;

	public WidgetModelRequirement(int groupId, int childId, int childChildId, int id)
	{
		this.groupId = groupId;
		this.childId = childId;
		this.childChildId = childChildId;
		this.id = id;
	}

	public WidgetModelRequirement(int groupId, int childId, int id)
	{
		this.groupId = groupId;
		this.childId = childId;
		this.id = id;
	}

	@Override
	public boolean check(Client client)
	{
		if (onlyNeedToPassOnce && hasPassed)
		{
			return true;
		}
		return checkWidget(client);
	}

	public boolean checkWidget(Client client)
	{
		Widget widget = client.getWidget(groupId, childId);
		if (widget == null)
		{
			return false;
		}
		if (childChildId != -1)
		{
			widget = widget.getChild(childChildId);
		}
		if (widget != null)
		{
			return widget.getModelId() == id;
		}
		return false;
	}

	public void checkWidgetText(Client client)
	{
		hasPassed = hasPassed || checkWidget(client);
	}
}

