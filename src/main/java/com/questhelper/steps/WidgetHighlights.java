/*
 * Copyright (c) 2020, Zoinkwiz
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
package com.questhelper.steps;

import com.questhelper.QuestHelperPlugin;
import java.awt.Color;
import java.awt.Graphics2D;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;

public class WidgetHighlights
{
	@Getter
	protected final int groupId;

	protected final int childId;

	@Getter
	protected Integer itemIdRequirement;

	protected final boolean checkChildren;

	public WidgetHighlights(int groupId, int childId)
	{
		this.groupId = groupId;
		this.childId = childId;
		this.checkChildren = false;
	}

	public WidgetHighlights(int groupId, int childId, int itemIdRequirement)
	{
		this(groupId, childId, itemIdRequirement, false);
	}

	public WidgetHighlights(int groupId, int childId, int itemIdRequirement, boolean checkChildren)
	{
		this.groupId = groupId;
		this.childId = childId;
		this.itemIdRequirement = itemIdRequirement;
		this.checkChildren = checkChildren;
	}

	public void highlightChoices(Graphics2D graphics, Client client, QuestHelperPlugin questHelper)
	{
		Widget widgetToHighlight = client.getWidget(groupId, childId);
		if (widgetToHighlight == null) return;

		if (checkChildren)
		{
			Widget[] widgets = widgetToHighlight.getChildren();
			if (widgets == null) return;

			for (Widget widget : widgets)
			{
				highlightChoice(graphics, questHelper, widget);
			}
		}
		else
		{
			highlightChoice(graphics, questHelper, widgetToHighlight);
		}
	}

	private void highlightChoice(Graphics2D graphics, QuestHelperPlugin questHelper, Widget widgetToHighlight)
	{
		if (widgetToHighlight == null || (itemIdRequirement != null && widgetToHighlight.getItemId() != itemIdRequirement)) return;

		graphics.setColor(new Color(questHelper.getConfig().targetOverlayColor().getRed(),
			questHelper.getConfig().targetOverlayColor().getGreen(),
			questHelper.getConfig().targetOverlayColor().getBlue(), 65));
		graphics.fill(widgetToHighlight.getBounds());
		graphics.setColor(questHelper.getConfig().targetOverlayColor());
		graphics.draw(widgetToHighlight.getBounds());
	}
}
