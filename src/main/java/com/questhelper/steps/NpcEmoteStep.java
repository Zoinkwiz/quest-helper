/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
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
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.emote.QuestEmote;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import net.runelite.api.ScriptID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.util.ImageUtil;

public class NpcEmoteStep extends NpcStep
{
	private boolean hasScrolled;
	private final QuestEmote emote;

	public NpcEmoteStep(QuestHelper questHelper, int npcID, QuestEmote emote, String text, Requirement... requirements)
	{
		super(questHelper, npcID, text, requirements);
		this.emote = emote;
	}

	public NpcEmoteStep(QuestHelper questHelper, int npcID, QuestEmote emote, WorldPoint worldPoint, String text, Requirement... requirements)
	{
		super(questHelper, npcID, worldPoint, text, requirements);
		this.emote = emote;
	}

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		if (emote.getSpriteId() != -1 && icon == null)
		{
			addEmoteImage();
			npcIcon = icon;
		}

		super.makeWorldOverlayHint(graphics, plugin);
	}

	protected void addEmoteImage()
	{
		BufferedImage iconBackground = ImageUtil.getResourceStreamFromClass(getClass(), "/util/clue_arrow.png");
		icon = new BufferedImage(iconBackground.getWidth(), iconBackground.getHeight(), BufferedImage.TYPE_INT_ARGB);

		BufferedImage emoteImg = spriteManager.getSprite(emote.getSpriteId(), 0);
		Graphics tmpGraphics = icon.getGraphics();
		tmpGraphics.drawImage(iconBackground, 0, 0, null);

		int buffer = iconBackground.getWidth() / 2 - emoteImg.getWidth() / 2;
		buffer = Math.max(buffer, 3);
		tmpGraphics.drawImage(emoteImg, buffer, buffer, iconBackground.getWidth() - 10, iconBackground.getHeight() - 10, null);
	}

	@Override
	public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWidgetOverlayHint(graphics, plugin);

		Widget emoteContainer = client.getWidget(WidgetInfo.EMOTE_CONTAINER);

		if (emoteContainer == null || emoteContainer.isHidden())
		{
			return;
		}

		Widget emoteWindow = client.getWidget(WidgetInfo.EMOTE_WINDOW);

		if (emoteWindow == null)
		{
			return;
		}

		Widget finalEmoteWidget = null;

		for (Widget emoteWidget : emoteContainer.getDynamicChildren())
		{
			if (emoteWidget.getSpriteId() == emote.getSpriteId())
			{
				finalEmoteWidget = emoteWidget;

				graphics.setColor(new Color(0, 255, 255, 65));
				graphics.fill(emoteWidget.getBounds());
				graphics.setColor(Color.CYAN);
				graphics.draw(emoteWidget.getBounds());
			}
		}
		if (!hasScrolled)
		{
			hasScrolled = true;
			scrollToWidget(WidgetInfo.EMOTE_CONTAINER, WidgetInfo.EMOTE_SCROLLBAR, finalEmoteWidget);
		}
	}

	void scrollToWidget(WidgetInfo list, WidgetInfo scrollbar, Widget widget)
	{
		final Widget parent = client.getWidget(list);

		if (widget == null)
		{
			return;
		}

		final int newScroll = Math.max(0, Math.min(parent.getScrollHeight(),
			(widget.getRelativeY() + widget.getHeight() / 2) - parent.getHeight() / 2));

		client.runScript(
			ScriptID.UPDATE_SCROLLBAR,
			scrollbar.getId(),
			list.getId(),
			newScroll
		);
	}
}
