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
package com.questhelper.banktab;

import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.ScriptEvent;
import net.runelite.api.ScriptID;
import net.runelite.api.SoundEffectID;
import net.runelite.api.SpriteID;
import net.runelite.api.VarClientInt;
import net.runelite.api.VarClientStr;
import net.runelite.api.Varbits;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.plugins.bank.BankSearch;

public class QuestBankTabInterface
{
	private static final String VIEW_TAB = "View tab ";

	@Setter
	@Getter
	private boolean questTabActive = false;

	@Getter
	private Widget parent;

	@Getter
	private Widget questIconWidget;

	@Getter
	private Widget questBackgroundWidget;

	private final Client client;
	private final BankSearch bankSearch;

	@Inject
	public QuestBankTabInterface(Client client, BankSearch bankSearch)
	{
		this.client = client;
		this.bankSearch = bankSearch;
	}

	public void init()
	{
		if (isHidden())
		{
			return;
		}

		parent = client.getWidget(WidgetInfo.BANK_CONTAINER);

		int QUEST_BUTTON_SIZE = 25;
		int QUEST_BUTTON_X = 408;
		int QUEST_BUTTON_Y = 5;
		questBackgroundWidget = createGraphic("quest-helper", SpriteID.UNKNOWN_BUTTON_SQUARE_SMALL, QUEST_BUTTON_SIZE,
			QUEST_BUTTON_SIZE,
			QUEST_BUTTON_X, QUEST_BUTTON_Y);
		questBackgroundWidget.setAction(1, VIEW_TAB);
		questBackgroundWidget.setOnOpListener((JavaScriptCallback) this::handleTagTab);

		questIconWidget = createGraphic("", SpriteID.QUESTS_PAGE_ICON_BLUE_QUESTS, QUEST_BUTTON_SIZE - 6,
			QUEST_BUTTON_SIZE - 6,
			QUEST_BUTTON_X + 3, QUEST_BUTTON_Y + 3);

		if (questTabActive)
		{
			questTabActive = false;
			activateTab();
		}
	}

	public void destroy()
	{
		if (questTabActive)
		{
			closeTab();
			bankSearch.reset(true);
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
		questTabActive = false;
	}

	public void handleClick(MenuOptionClicked event)
	{
		if (isHidden())
		{
			return;
		}
		String menuOption = event.getMenuOption();

		// If click a base tab, close
		boolean clickedTabTag = menuOption.startsWith("View tab") && !event.getMenuTarget().equals("quest-helper");
		boolean clickedOtherTab = menuOption.equals("View all items") || menuOption.startsWith("View tag tab");
		if (questTabActive && (clickedTabTag || clickedOtherTab))
		{
			closeTab();
		}
	}

	public void handleSearch()
	{
		if (questTabActive)
		{
			closeTab();
			// This ensures that when clicking Search when tab is selected, the search input is opened rather
			// than client trying to close it first
			client.setVar(VarClientStr.INPUT_TEXT, "");
			client.setVar(VarClientInt.INPUT_TYPE, 0);
		}
	}

	public boolean isHidden()
	{
		Widget widget = client.getWidget(WidgetInfo.BANK_CONTAINER);
		return widget == null || widget.isHidden();
	}

	private void handleTagTab(ScriptEvent event)
	{
		if (event.getOp() == 2)
		{
			client.setVarbit(Varbits.CURRENT_BANK_TAB, 0);

			if (questTabActive)
			{
				closeTab();
				bankSearch.reset(true);
			}
			else
			{
				activateTab();
				// openTag will reset and relayout
			}

			client.playSoundEffect(SoundEffectID.UI_BOOP);
		}
	}

	public void closeTab()
	{
		questTabActive = false;
		if (questBackgroundWidget != null)
		{
			questBackgroundWidget.setSpriteId(SpriteID.UNKNOWN_BUTTON_SQUARE_SMALL);
			questBackgroundWidget.revalidate();
		}
	}

	private void activateTab()
	{
		if (questTabActive)
		{
			return;
		}

		questBackgroundWidget.setSpriteId(SpriteID.UNKNOWN_BUTTON_SQUARE_SMALL_SELECTED);
		questBackgroundWidget.revalidate();
		questTabActive = true;

		bankSearch.reset(true); // clear search dialog & relayout bank for new tab.

		// When searching the button has a script on timer to detect search end, that will set the background back
		// and remove the timer. However since we are going from a bank search to our fake search this will not remove
		// the timer but instead re-add it and reset the background. So remove the timer and the background. This is the
		// same as bankmain_search_setbutton.
		Widget searchButtonBackground = client.getWidget(WidgetInfo.BANK_SEARCH_BUTTON_BACKGROUND);
		if (searchButtonBackground != null)
		{
			searchButtonBackground.setOnTimerListener((Object[]) null);
			searchButtonBackground.setSpriteId(SpriteID.EQUIPMENT_SLOT_TILE);
		}
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
}
