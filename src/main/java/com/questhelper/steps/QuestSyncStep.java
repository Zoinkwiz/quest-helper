/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.tools.QuestWidgets;
import net.runelite.api.ScriptID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.util.ColorUtil;

import java.awt.*;

public class QuestSyncStep extends QuestStep
{
	private boolean hasScrolled;
	private final QuestHelperQuest quest;

	public QuestSyncStep(QuestHelper questHelper, QuestHelperQuest quest, String text)
	{
		super(questHelper, text);
		this.quest = quest;
	}

	@Override
	public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWidgetOverlayHint(graphics, plugin);

		Widget questContainer = client.getWidget(QuestWidgets.QUESTLIST_CONTAINER.getPackedId());
		if (questContainer == null || questContainer.isHidden())
		{
			return;
		}

		Widget questsContainer = client.getWidget(QuestWidgets.QUEST_CONTAINER.getPackedId());

		Widget finalEmoteWidget = null;

		Color overlayColor = questHelper.getConfig().targetOverlayColor();


		for (Widget questWidget : questsContainer.getDynamicChildren())
		{
			if (questWidget.getText().equals(quest.getName()))
			{
				finalEmoteWidget = questWidget;
				if (questWidget.getCanvasLocation().getY() > questContainer.getCanvasLocation().getY() &&
					questWidget.getCanvasLocation().getY() < questContainer.getCanvasLocation().getY() + questContainer.getHeight())
				{
					graphics.setColor(ColorUtil.colorWithAlpha(overlayColor, 65));
					graphics.fill(questWidget.getBounds());
					graphics.setColor(questHelper.getConfig().targetOverlayColor());
					graphics.draw(questWidget.getBounds());
					break;
				}
			}
		}

		if (!hasScrolled)
		{
			hasScrolled = true;
			scrollToWidget(finalEmoteWidget);
		}
	}

	void scrollToWidget(Widget widget)
	{
		final Widget parent = client.getWidget(QuestWidgets.QUESTLIST_CONTAINER.getPackedId());

		if (widget == null || parent == null)
		{
			return;
		}

		final int newScroll = Math.max(0, Math.min(parent.getScrollHeight(),
			(widget.getRelativeY() / 2 + (widget.getHeight()) / 2) - parent.getHeight() / 2));

		client.runScript(
			ScriptID.UPDATE_SCROLLBAR,
			QuestWidgets.QUESTLIST_SCROLLBAR.getId(),
			QuestWidgets.QUESTLIST_CONTAINER.getId(),
			newScroll
		);
	}
}
