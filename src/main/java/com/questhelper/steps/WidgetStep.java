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

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Setter;
import net.runelite.api.widgets.Widget;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import net.runelite.api.widgets.WidgetItem;

public class WidgetStep extends DetailedQuestStep
{
	@Setter
	protected List<WidgetDetails> widgetDetails = new ArrayList<>();

	public WidgetStep(QuestHelper questHelper, String text, int groupID, int childID)
	{
		super(questHelper, text);
		widgetDetails.add(new WidgetDetails(groupID, childID, -1));
	}

	public WidgetStep(QuestHelper questHelper, String text, WidgetDetails... widgetDetails)
	{
		super(questHelper, text);
		this.widgetDetails.addAll(Arrays.asList(widgetDetails));
	}

	@Override
	public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWidgetOverlayHint(graphics, plugin);
		for (WidgetDetails widgetDetail : widgetDetails)
		{
			Widget widget = client.getWidget(widgetDetail.groupID, widgetDetail.childID);
			if (widget == null || widget.isHidden())
			{
				continue;
			}

			if (widgetDetail.childChildID != -1)
			{
				Widget tmpWidget = widget.getChild(widgetDetail.childChildID);
				if (tmpWidget != null)
				{
					widget = tmpWidget;
				}
				else
				{
					WidgetItem widgetItem = widget.getWidgetItem(widgetDetail.childChildID);
					graphics.setColor(new Color(questHelper.getConfig().targetOverlayColor().getRed(),
						questHelper.getConfig().targetOverlayColor().getGreen(),
						questHelper.getConfig().targetOverlayColor().getBlue(), 65));
					graphics.fill(widgetItem.getCanvasBounds());
					graphics.setColor(questHelper.getConfig().targetOverlayColor());
					graphics.draw(widgetItem.getCanvasBounds());
					continue;
				}
			}
			graphics.setColor(new Color(questHelper.getConfig().targetOverlayColor().getRed(),
				questHelper.getConfig().targetOverlayColor().getGreen(),
				questHelper.getConfig().targetOverlayColor().getBlue(), 65));
			graphics.fill(widget.getBounds());
			graphics.setColor(questHelper.getConfig().targetOverlayColor());
			graphics.draw(widget.getBounds());
		}
	}
}
