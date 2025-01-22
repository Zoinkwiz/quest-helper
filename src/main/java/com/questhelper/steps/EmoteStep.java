/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
 * Copyright (c) 2018, Lotto <https://github.com/devLotto>
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
import com.questhelper.steps.overlay.IconOverlay;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import net.runelite.api.Player;
import net.runelite.api.ScriptID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

public class EmoteStep extends DetailedQuestStep
{
	private boolean hasScrolled;
	private final QuestEmote emote;
	private List<QuestEmote> emoteSequence;
	private int currentEmoteIndex;
	private final WorldPoint requiredLocation;
	private static final int LOCATION_TOLERANCE = 1;

	public EmoteStep(QuestHelper questHelper, QuestEmote emote, String text, Requirement... requirements)
	{
		super(questHelper, text, requirements);
		this.emote = emote;
		this.emoteSequence = new ArrayList<>();
		this.emoteSequence.add(emote);
		this.currentEmoteIndex = 0;
		this.requiredLocation = null;
	}

	public EmoteStep(QuestHelper questHelper, QuestEmote emote, WorldPoint worldPoint, String text, Requirement... requirements)
	{
		super(questHelper, worldPoint, text, requirements);
		this.emote = emote;
		this.emoteSequence = new ArrayList<>();
		this.emoteSequence.add(emote);
		this.currentEmoteIndex = 0;
		this.requiredLocation = worldPoint;
	}

	public EmoteStep(QuestHelper questHelper, List<QuestEmote> emoteSequence, WorldPoint worldPoint, String text, Requirement... requirements)
	{
		super(questHelper, worldPoint, text, requirements);
		this.emote = emoteSequence.get(0);
		this.emoteSequence = emoteSequence;
		this.currentEmoteIndex = 0;
		this.requiredLocation = worldPoint;
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (!(event.getActor() instanceof Player))
		{
			return;
		}

		Player player = (Player) event.getActor();
		if (player != client.getLocalPlayer())
		{
			return;
		}

		QuestEmote currentEmote = getCurrentEmote();
		if (currentEmote != null && player.getAnimation() == currentEmote.getAnimationId())
		{
			if (isPlayerInCorrectLocation(player))
			{
				nextEmote();
			}
		}
	}

	private boolean isPlayerInCorrectLocation(Player player)
	{
		if (requiredLocation == null)
		{
			return true;
		}

		WorldPoint playerLocation = player.getWorldLocation();
		return playerLocation.distanceTo(requiredLocation) <= LOCATION_TOLERANCE;
	}

	public QuestEmote getCurrentEmote()
	{
		if (currentEmoteIndex >= emoteSequence.size())
		{
			return null;
		}
		return emoteSequence.get(currentEmoteIndex);
	}

	public void nextEmote()
	{
		if (currentEmoteIndex < emoteSequence.size() - 1)
		{
			currentEmoteIndex++;
			hasScrolled = false;
		}
	}

	public void resetEmotes() 
	{
		currentEmoteIndex = 0;
		hasScrolled = false;
	}

	@Override
	public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWidgetOverlayHint(graphics, plugin);

		QuestEmote currentEmote = getCurrentEmote();
		if (currentEmote == null) 
		{
			return;
		}

		Widget emoteContainer = client.getWidget(ComponentID.EMOTES_EMOTE_CONTAINER);
		if (emoteContainer == null || emoteContainer.isHidden()) 
		{
			return;
		}

		Widget emoteWindow = client.getWidget(ComponentID.EMOTES_WINDOW);

		if (emoteWindow == null)
		{
			return;
		}

		Widget finalEmoteWidget = null;

		for (Widget emoteWidget : emoteContainer.getDynamicChildren())
		{
			if (emoteWidget.getSpriteId() == currentEmote.getSpriteId())
			{
				finalEmoteWidget = emoteWidget;
				graphics.setColor(new Color(questHelper.getConfig().targetOverlayColor().getRed(),
					questHelper.getConfig().targetOverlayColor().getGreen(),
					questHelper.getConfig().targetOverlayColor().getBlue(), 65));
				graphics.fill(emoteWidget.getBounds());
				graphics.setColor(questHelper.getConfig().targetOverlayColor());
				graphics.draw(emoteWidget.getBounds());
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
		final Widget parent = client.getWidget(ComponentID.EMOTES_EMOTE_CONTAINER);

		if (widget == null || parent == null)
		{
			return;
		}

		final int newScroll = Math.max(0, Math.min(parent.getScrollHeight(),
			(widget.getRelativeY() + widget.getHeight() / 2) - parent.getHeight() / 2));

		client.runScript(
			ScriptID.UPDATE_SCROLLBAR,
			ComponentID.EMOTES_EMOTE_SCROLLBAR,
			ComponentID.EMOTES_EMOTE_CONTAINER,
			newScroll
		);
	}

	@Override
	protected void setupIcon()
	{
		QuestEmote currentEmote = getCurrentEmote();
		if (currentEmote != null && currentEmote.getSpriteId() != -1 && icon == null)
		{
			BufferedImage emoteImage = spriteManager.getSprite(currentEmote.getSpriteId(), 0);
			if (emoteImage != null)
			{
				icon = IconOverlay.createIconImage(emoteImage);
			}
		}
		super.setupIcon();
	}
}
