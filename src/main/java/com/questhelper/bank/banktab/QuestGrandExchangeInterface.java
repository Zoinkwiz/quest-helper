/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
 * Copyright (c) 2018, Ron Young <https://github.com/raiyni>
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
package com.questhelper.bank.banktab;

import com.questhelper.QuestHelperPlugin;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.gameval.VarClientID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetTextAlignment;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.ui.JagexColors;

import javax.inject.Inject;

public class QuestGrandExchangeInterface
{
	private static final String VIEW_TAB = "View missing items ";

	@Setter
	@Getter
	private boolean active = false;

	@Getter
	private Widget parent;

	@Getter
	private Widget questIconWidget;

	@Getter
	private Widget questBackgroundWidget;

	@Getter
	private Widget grandExchangeTitle;

	private final Client client;
	private final QuestHelperPlugin questHelper;
	private final ClientThread clientThread;

	@Inject
	public QuestGrandExchangeInterface(Client client, QuestHelperPlugin questHelper, ClientThread clientThread)
	{
		this.client = client;
		this.questHelper = questHelper;
		this.clientThread = clientThread;
	}

	public void init()
	{
		if (isHidden() || questHelper.getSelectedQuest() == null)
		{
			return;
		}

		parent = client.getWidget(InterfaceID.Chatbox.MES_LAYER);

		if (notAtGE())
		{
			if (questHelper.getConfig().solvePuzzles() && questHelper.getSelectedQuest().getCurrentStep().getActiveStep().getGeInterfaceIcon() != null)
			{
				onceOffActivateTab(parent);
			}
			return;
		}

		int QUEST_BUTTON_SIZE = 20;
		int QUEST_BUTTON_X = 480;
		int QUEST_BUTTON_Y = 0;
		questBackgroundWidget = createGraphic("quest helper", SpriteID.Miscgraphics3.UNKNOWN_BUTTON_SQUARE_SMALL,
			QUEST_BUTTON_SIZE,
			QUEST_BUTTON_SIZE,
			QUEST_BUTTON_X, QUEST_BUTTON_Y);
		questBackgroundWidget.setAction(1, VIEW_TAB);
		questBackgroundWidget.setOnOpListener((JavaScriptCallback) this::handleTagTab);

		questIconWidget = createGraphic("", SpriteID.AchievementDiaryIcons.BLUE_QUESTS, QUEST_BUTTON_SIZE - 6,
			QUEST_BUTTON_SIZE - 6,
			QUEST_BUTTON_X + 3, QUEST_BUTTON_Y + 3);

		grandExchangeTitle = createTitle(parent);

		if (active)
		{
			active = false;
			activateTab();
		}
	}

	public void destroy()
	{
		if (active)
		{
			closeOptions();
		}

		parent = null;

		if (questIconWidget != null)
		{
			questIconWidget.setHidden(true);
		}

		if (questBackgroundWidget != null)
		{
			questBackgroundWidget.setHidden(true);
		}

		if (grandExchangeTitle != null)
		{
			grandExchangeTitle.setHidden(true);
		}
		active = false;
	}

	public boolean notAtGE()
	{
		// Only show button if we're at GE
		var playerLocation = client.getLocalPlayer().getWorldLocation();
		return playerLocation.distanceTo(new WorldPoint(3164, 3489, 0)) > 20;
	}

	public boolean isHidden()
	{
		Widget widget = client.getWidget(InterfaceID.Chatbox.MES_LAYER);
		return widget == null || widget.isHidden();
	}

	private void handleTagTab(ScriptEvent event)
	{
		if (active)
		{
			closeOptions();
		}
		else
		{
			activateTab();
		}

		client.playSoundEffect(SoundEffectID.UI_BOOP);
	}

	public void closeOptions()
	{
		active = false;
		if (questBackgroundWidget != null)
		{
			questBackgroundWidget.setSpriteId(SpriteID.Miscgraphics3.UNKNOWN_BUTTON_SQUARE_SMALL);
			questBackgroundWidget.revalidate();
		}

		grandExchangeTitle.setHidden(true);

		client.setVarcStrValue(VarClientID.MESLAYERINPUT, "");
		client.setVarcIntValue(VarClientID.MESLAYERMODE, 14);

		clientThread.invokeLater(() -> updateSearchInterface(false));
	}

	private void activateTab()
	{
		if (active)
		{
			return;
		}

		questBackgroundWidget.setSpriteId(SpriteID.Miscgraphics3.UNKNOWN_BUTTON_SQUARE_SMALL_SELECTED);
		questBackgroundWidget.revalidate();
		grandExchangeTitle.setHidden(false);
		active = true;
		client.setVarcStrValue(VarClientID.MESLAYERINPUT, "quest-helper");
		client.setVarcIntValue(VarClientID.MESLAYERMODE, 14);

		clientThread.invokeLater(() -> updateSearchInterface(true));
	}

	// Used for non-ge ge interfaces
	public void onceOffActivateTab(Widget parent)
	{
		createTitleStepNeededItem(parent);
		client.setVarcStrValue(VarClientID.MESLAYERINPUT, "quest-helper");
		client.setVarcIntValue(VarClientID.MESLAYERMODE, 14);

		clientThread.invokeLater(() -> updateSearchInterface(true));
	}

	private void updateSearchInterface(boolean hideSearchBox)
	{
		Widget geSearchBox = client.getWidget(InterfaceID.Chatbox.MES_TEXT2);
		if (geSearchBox == null)
		{
			return;
		}

		Object[] scriptArgs = geSearchBox.getOnKeyListener();
		if (scriptArgs == null)
		{
			return;
		}

		client.runScript(scriptArgs);
		geSearchBox.setHidden(hideSearchBox);
	}

	private Widget createGraphic(Widget container, String name, int spriteId, int width, int height, int x, int y)
	{
		Widget widget = container.createChild(-1, WidgetType.GRAPHIC);
		widget.setOriginalWidth(width);
		widget.setOriginalHeight(height);
		widget.setOriginalX(x);
		widget.setOriginalY(y);

		widget.setSpriteId(spriteId);
		widget.setOnOpListener(ScriptID.NULL);
		widget.setHasListener(true);
		widget.setName(name);
		widget.revalidate();

		return widget;
	}

	private Widget createGraphic(String name, int spriteId, int width, int height, int x, int y)
	{
		return createGraphic(parent, name, spriteId, width, height, x, y);
	}

	private Widget createTitle(Widget container)
	{
		Widget chatbox = client.getWidget(InterfaceID.Chatbox.MES_TEXT2);

		Widget widget = container.createChild(-1, WidgetType.TEXT);
		if (chatbox == null)
		{
			return widget;
		}

		widget.setOriginalWidth(chatbox.getWidth());
		widget.setOriginalHeight(chatbox.getHeight());
		widget.setOriginalX(0);
		widget.setOriginalY(0);
		widget.setTextShadowed(false);
		widget.setXTextAlignment(WidgetTextAlignment.CENTER);
		widget.setYTextAlignment(WidgetTextAlignment.CENTER);

		widget.setText("<col=b40000>" + questHelper.getSelectedQuest().getQuest().getName() + "</col> required items");
		widget.setFontId(FontID.BOLD_12);
		widget.setTextColor(JagexColors.CHAT_GAME_EXAMINE_TEXT_OPAQUE_BACKGROUND.getRGB());

		if (!active)
		{
			widget.setHidden(true);
		}

		widget.revalidate();

		return widget;
	}

	private Widget createTitleStepNeededItem(Widget container)
	{
		Widget chatbox = client.getWidget(InterfaceID.Chatbox.MES_TEXT2);

		Widget widget = container.createChild(-1, WidgetType.TEXT);
		if (chatbox == null)
		{
			return widget;
		}

		widget.setOriginalWidth(chatbox.getWidth());
		widget.setOriginalHeight(chatbox.getHeight());
		widget.setOriginalX(0);
		widget.setOriginalY(0);
		widget.setTextShadowed(false);
		widget.setXTextAlignment(WidgetTextAlignment.CENTER);
		widget.setYTextAlignment(WidgetTextAlignment.CENTER);

		widget.setText("<col=b40000>Quest Helper</col> Items");
		widget.setFontId(FontID.BOLD_12);
		widget.setTextColor(JagexColors.CHAT_GAME_EXAMINE_TEXT_OPAQUE_BACKGROUND.getRGB());

		widget.revalidate();

		return widget;
	}
}
