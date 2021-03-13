/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * Copyright (c) 2018, Ron Young <https://github.com/raiyni>
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
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

import com.google.common.primitives.Shorts;
import com.questhelper.QuestHelperPlugin;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.FontID;
import net.runelite.api.FontTypeFace;
import net.runelite.api.ItemID;
import net.runelite.api.ScriptEvent;
import net.runelite.api.ScriptID;
import net.runelite.api.SpriteID;
import net.runelite.api.VarClientStr;
import net.runelite.api.events.GrandExchangeSearched;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.FontType;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemVariationMapping;
import net.runelite.client.util.QuantityFormatter;
import net.runelite.client.util.Text;

public class QuestBankTab
{
	private static final int ITEMS_PER_ROW = 8;
	private static final int ITEM_VERTICAL_SPACING = 36;
	private static final int ITEM_HORIZONTAL_SPACING = 48;
	private static final int ITEM_ROW_START = 51;
	private static final int LINE_VERTICAL_SPACING = 5;
	private static final int LINE_HEIGHT = 2;
	private static final int TEXT_HEIGHT = 15;
	private static final int ITEM_HEIGHT = 32;
	private static final int ITEM_WIDTH = 36;

	private static final int MAX_RESULT_COUNT = 250;

	private final ArrayList<Widget> addedWidgets = new ArrayList<>();

	@Inject
	private ItemManager itemManager;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private QuestBankTabInterface questBankTabInterface;

	private final QuestHelperPlugin questHelper;

	private final HashMap<Widget, BankTabItem> widgetItems = new HashMap<>();

	public QuestBankTab(QuestHelperPlugin questHelperPlugin)
	{
		questHelper = questHelperPlugin;
	}

	public void startUp()
	{
		if (questHelper.getSelectedQuest() != null)
		{
			clientThread.invokeLater(questBankTabInterface::init);
		}
	}

	public void shutDown()
	{
		clientThread.invokeLater(questBankTabInterface::destroy);
		if (!addedWidgets.isEmpty())
		{
			for (Widget addedWidget : addedWidgets)
			{
				addedWidget.setHidden(true);
			}
			addedWidgets.clear();
		}
	}

	@Subscribe
	public void onGrandExchangeSearched(GrandExchangeSearched event)
	{
		final String input = client.getVar(VarClientStr.INPUT_TEXT);
		String QUEST_BANK_TAG = "quest-helper";
		if (!input.equals(QUEST_BANK_TAG))
		{
			return;
		}

		event.consume();

		final List<Integer> idsList = questHelper.getBankTagService().itemsToTag();

		final Set<Integer> ids = idsList
			.stream()
			.mapToInt(Math::abs)
			.mapToObj(ItemVariationMapping::getVariations)
			.flatMap(Collection::stream)
			.distinct()
			.filter(i -> itemManager.getItemComposition(i).isTradeable())
			.limit(MAX_RESULT_COUNT)
			.collect(Collectors.toCollection(TreeSet::new));

		client.setGeSearchResultIndex(0);
		client.setGeSearchResultCount(ids.size());
		client.setGeSearchResultIds(Shorts.toArray(ids));
	}

	@Subscribe
	public void onScriptPreFired(ScriptPreFired event)
	{
		int scriptId = event.getScriptId();
		if (scriptId == ScriptID.BANKMAIN_FINISHBUILDING)
		{
			// Since we apply tag tab search filters even when the bank is not in search mode,
			// bankkmain_build will reset the bank title to "The Bank of Gielinor". So apply our
			// own title.
			if (questBankTabInterface.isQuestTabActive())
			{
				Widget bankTitle = client.getWidget(WidgetInfo.BANK_TITLE_BAR);
				if (bankTitle != null)
				{
					if (questHelper.getSelectedQuest() != null)
					{
						bankTitle.setText("Tab <col=ff0000>" + questHelper.getSelectedQuest().getQuest().getName() + "</col>");
					}
					else
					{
						bankTitle.setText("Tab <col=ff0000>Quest Helper</col>");
					}
				}
			}
		}
		else if (scriptId == ScriptID.BANKMAIN_SEARCH_TOGGLE)
		{
			questBankTabInterface.handleSearch();
		}
	}

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent event)
	{
		String eventName = event.getEventName();

		int[] intStack = client.getIntStack();
		int intStackSize = client.getIntStackSize();

		if ("getSearchingTagTab".equals(eventName))
		{
			intStack[intStackSize - 1] = questBankTabInterface.isQuestTabActive() ? 1 : 0;
			if (questBankTabInterface.isQuestTabActive())
			{
				// As we're on the quest helper tab, we don't need to check again for tab tags
				// Change the name of the event so as to not proc another check
				event.setEventName("getSearchingQuestHelperTab");
			}
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == WidgetID.BANK_GROUP_ID && questHelper.getSelectedQuest() != null)
		{
			questBankTabInterface.init();
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		questBankTabInterface.handleClick(event);
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		if (event.getScriptId() == ScriptID.BANKMAIN_SEARCHING)
		{
			// The return value of bankmain_searching is on the stack. If we have a tag tab active
			// make it return true to put the bank in a searching state.
			if (questBankTabInterface.isQuestTabActive())
			{
				client.getIntStack()[client.getIntStackSize() - 1] = 1; // true
			}
			if (!addedWidgets.isEmpty())
			{
				for (Widget addedWidget : addedWidgets)
				{
					addedWidget.setHidden(true);
				}
				addedWidgets.clear();
			}
			return;
		}

		if (event.getScriptId() != ScriptID.BANKMAIN_BUILD)
		{
			return;
		}

		if (!questBankTabInterface.isQuestTabActive())
		{
			return;
		}

		Widget itemContainer = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
		if (itemContainer == null)
		{
			return;
		}

		if (!addedWidgets.isEmpty())
		{

			for (Widget addedWidget : addedWidgets)
			{
				addedWidget.setHidden(true);
			}
			addedWidgets.clear();
		}

		Widget[] containerChildren = itemContainer.getDynamicChildren();

		ArrayList<BankTabItems> tabLayout = questHelper.getBankTagService().getPluginBankTagItemsForSections();

		if (tabLayout != null)
		{
			sortBankTabItems(itemContainer, containerChildren, tabLayout);
		}
	}

	private void sortBankTabItems(Widget itemContainer, Widget[] containerChildren, List<BankTabItems> newLayout)
	{
		int totalSectionsHeight = 0;

		List<Integer> itemList = new ArrayList<>();
		for (Widget itemWidget : containerChildren)
		{
			if (itemWidget.getSpriteId() == SpriteID.RESIZEABLE_MODE_SIDE_PANEL_BACKGROUND
				|| itemWidget.getText().contains("Tab"))
			{
				itemWidget.setHidden(true);
			}
			else if (!itemWidget.isHidden() && itemWidget.getItemId() != -1 && !itemList.contains(itemWidget.getItemId()))
			{
				itemList.add(itemWidget.getItemId());
			}
		}

		for (BankTabItems bankTabItems : newLayout)
		{
			totalSectionsHeight = addPluginTabSection(itemContainer, bankTabItems.getItems(), itemList,
				bankTabItems.getName(), totalSectionsHeight);
		}

		totalSectionsHeight = addGeneralSection(itemContainer, itemList, totalSectionsHeight);

		final Widget bankItemContainer = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
		int itemContainerHeight = bankItemContainer.getHeight();

		bankItemContainer.setScrollHeight(Math.max(totalSectionsHeight, itemContainerHeight));

		final int itemContainerScroll = bankItemContainer.getScrollY();
		clientThread.invokeLater(() ->
			client.runScript(ScriptID.UPDATE_SCROLLBAR,
				WidgetInfo.BANK_SCROLLBAR.getId(),
				WidgetInfo.BANK_ITEM_CONTAINER.getId(),
				itemContainerScroll));
	}

	private int addPluginTabSection(Widget itemContainer, List<BankTabItem> items, List<Integer> itemIds,
									String title, int totalSectionsHeight)
	{
		int totalItemsAdded = 0;

		if (items == null)
		{
			return 0;
		}

		// Get FontMetrics so we can accurately get font height/width
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = img.getGraphics();
		FontMetrics fm = graphics.getFontMetrics(FontType.SMALL.getFont());
		img = null;
		graphics = null;
		for (BankTabItem bankTabItem : items)
		{
			boolean foundItem = false;
			if (!Collections.disjoint(itemIds, bankTabItem.getItemIDs()))
			{
				for (Widget widget : itemContainer.getDynamicChildren())
				{
					if (!widget.isHidden() && widget.getOpacity() != 150 && (bankTabItem.getItemIDs().contains(widget.getItemId())))
					{
						foundItem = true;
						if (totalItemsAdded == 0)
						{
							totalSectionsHeight = addSectionHeader(itemContainer, title, totalSectionsHeight);
						}

						Point point = placeItem(widget, totalItemsAdded, totalSectionsHeight);
						widget.setItemQuantityMode(1);
						widgetItems.put(widget, bankTabItem);


						if (bankTabItem.getQuantity() > 0)
						{
							String quantityString = QuantityFormatter.quantityToStackSize(bankTabItem.getQuantity());
							int itemStackSizeLength = fm.stringWidth(QuantityFormatter.quantityToStackSize(widget.getItemQuantity()));
							int requirementLength = fm.stringWidth(quantityString);

							int xPos = point.x + 2 + itemStackSizeLength;
							int yPos = point.y - 1;
							if (itemStackSizeLength + requirementLength > 24)
							{ // put text on next line
								xPos = point.x;
								yPos = point.y + fm.getHeight();
							}

							Color color = questHelper.getConfig().textHighlightColor();

							addedWidgets.add(createText(itemContainer,
								"/ " + quantityString,
								color.getRGB(),
								ITEM_HORIZONTAL_SPACING,
								TEXT_HEIGHT - 3,
								xPos,
								yPos));
						}
						totalItemsAdded++;
						itemIds.removeAll(Collections.singletonList(widget.getItemId()));
						break;
					}
				}
			}
			if (!foundItem)
			{
				if (totalItemsAdded == 0)
				{
					totalSectionsHeight = addSectionHeader(itemContainer, title, totalSectionsHeight);
				}

				// calculate correct item position as if this was a normal tab
				int adjXOffset = (totalItemsAdded % ITEMS_PER_ROW) * ITEM_HORIZONTAL_SPACING + ITEM_ROW_START;
				int adjYOffset = totalSectionsHeight + (totalItemsAdded / ITEMS_PER_ROW) * ITEM_VERTICAL_SPACING;

				Widget fakeItemWidget = createMissingItem(itemContainer, bankTabItem, adjXOffset, adjYOffset);
				widgetItems.put(fakeItemWidget, bankTabItem);
				addedWidgets.add(fakeItemWidget);

				if (bankTabItem.getQuantity() > 0)
				{
					String quantityString = QuantityFormatter.quantityToStackSize(bankTabItem.getQuantity());
					int requirementLength = fm.stringWidth(quantityString);
					int xPos = adjXOffset + 8;
					int yPos = adjYOffset - 1;
					if (requirementLength > 20)
					{ // put text on next line
						xPos = adjXOffset;
						yPos = adjYOffset + fm.getHeight();
					}
					Color color = questHelper.getConfig().textHighlightColor();

					addedWidgets.add(createText(itemContainer,
						"/ " + quantityString,
						color.getRGB(),
						ITEM_HORIZONTAL_SPACING,
						TEXT_HEIGHT - 3,
						xPos,
						yPos));
				}
				itemIds.removeAll(bankTabItem.getItemIDs());
				totalItemsAdded++;
			}
		}
		int newHeight = totalSectionsHeight + (totalItemsAdded / ITEMS_PER_ROW) * ITEM_VERTICAL_SPACING;
		newHeight = totalItemsAdded % ITEMS_PER_ROW != 0 ? newHeight + ITEM_VERTICAL_SPACING : newHeight;

		return newHeight;
	}

	private int addGeneralSection(Widget itemContainer, List<Integer> items, int totalSectionsHeight)
	{
		int totalItemsAdded = 0;

		if (items.isEmpty())
		{
			return totalSectionsHeight;
		}

		for (Integer itemID : items)
		{
			for (Widget widget : itemContainer.getDynamicChildren())
			{
				if (!widget.isHidden() && widget.getOpacity() != 150 && widget.getItemId() == itemID)
				{
					if (totalItemsAdded == 0)
					{
						totalSectionsHeight = addSectionHeader(itemContainer, "General", totalSectionsHeight);
					}

					placeItem(widget, totalItemsAdded, totalSectionsHeight);
					totalItemsAdded++;
				}
			}
		}
		int newHeight = totalSectionsHeight + (totalItemsAdded / ITEMS_PER_ROW) * ITEM_VERTICAL_SPACING;
		newHeight = totalItemsAdded % ITEMS_PER_ROW != 0 ? newHeight + ITEM_VERTICAL_SPACING : newHeight;

		return newHeight;
	}

	private int addSectionHeader(Widget itemContainer, String title, int totalSectionsHeight)
	{
		addedWidgets.add(createGraphic(itemContainer, SpriteID.RESIZEABLE_MODE_SIDE_PANEL_BACKGROUND, ITEMS_PER_ROW * ITEM_HORIZONTAL_SPACING, LINE_HEIGHT, ITEM_ROW_START, totalSectionsHeight));
		addedWidgets.add(createText(itemContainer, title, new Color(228, 216, 162).getRGB(), (ITEMS_PER_ROW * ITEM_HORIZONTAL_SPACING) + ITEM_ROW_START
			, TEXT_HEIGHT, ITEM_ROW_START, totalSectionsHeight + LINE_VERTICAL_SPACING));

		return totalSectionsHeight + LINE_VERTICAL_SPACING + TEXT_HEIGHT;
	}

	private Point placeItem(Widget widget, int totalItemsAdded, int totalSectionsHeight)
	{
		int adjYOffset = totalSectionsHeight + (totalItemsAdded / ITEMS_PER_ROW) * ITEM_VERTICAL_SPACING;
		int adjXOffset = (totalItemsAdded % ITEMS_PER_ROW) * ITEM_HORIZONTAL_SPACING + ITEM_ROW_START;

		if (widget.getOriginalY() != adjYOffset)
		{
			widget.setOriginalY(adjYOffset);
			widget.revalidate();
		}

		if (widget.getOriginalX() != adjXOffset)
		{
			widget.setOriginalX(adjXOffset);
			widget.revalidate();
		}

		return new Point(adjXOffset, adjYOffset);
	}

	private Widget createGraphic(Widget container, int spriteId, int width, int height, int x, int y)
	{
		Widget widget = container.createChild(-1, WidgetType.GRAPHIC);
		widget.setOriginalWidth(width);
		widget.setOriginalHeight(height);
		widget.setOriginalX(x);
		widget.setOriginalY(y);

		widget.setSpriteId(spriteId);

		widget.revalidate();

		return widget;
	}

	private Widget createMissingItem(Widget container, BankTabItem bankTabItem, int x, int y)
	{
		Widget widget = container.createChild(-1, WidgetType.GRAPHIC);
		widget.setItemQuantityMode(1); // quantity of 1 still shows number
		widget.setOriginalWidth(ITEM_WIDTH);
		widget.setOriginalHeight(ITEM_HEIGHT);
		widget.setOriginalX(x);
		widget.setOriginalY(y);

		List<Integer> itemIDs = bankTabItem.getItemIDs();
		if (bankTabItem.getDisplayID() != null)
		{
			itemIDs = Collections.singletonList(bankTabItem.getDisplayID());
		}

		if (itemIDs.size() == 0)
		{
			itemIDs.add(ItemID.CAKE_OF_GUIDANCE);
		}

		widget.setItemId(itemIDs.get(0));
		widget.setName("<col=ff9040>" + bankTabItem.getText() + "</col>");
		if (bankTabItem.getDetails() != null)
		{
			widget.setText(bankTabItem.getDetails());
		}
		widget.setItemQuantity(0);
		widget.setOpacity(150);
		widget.setOnOpListener(ScriptID.NULL);
		widget.setHasListener(true);

		addTabActions(widget);

		widget.revalidate();

		return widget;
	}

	private void addTabActions(Widget w)
	{
		w.setAction(1, "Details");

		w.setOnOpListener((JavaScriptCallback) this::handleFakeItemClick);
	}

	private void handleFakeItemClick(ScriptEvent event)
	{
		Widget widget = event.getSource();
		if (widget.getItemId() != -1)
		{
			String name = widget.getName();
			BankTabItem item = widgetItems.get(widget);

			String quantity = QuantityFormatter.formatNumber(item.getQuantity()) + " x ";
			if (item.getQuantity() == -1)
			{
				quantity = "some ";
			}
			final ChatMessageBuilder message = new ChatMessageBuilder()
				.append("You need ")
				.append(ChatColorType.HIGHLIGHT)
				.append(quantity)
				.append(Text.removeTags(name))
				.append(".");

			if (!widget.getText().isEmpty())
			{
				message.append(ChatColorType.NORMAL)
					.append(" " + widget.getText() + ".");
			}

			chatMessageManager.queue(QueuedMessage.builder()
				.type(ChatMessageType.ITEM_EXAMINE)
				.runeLiteFormattedMessage(message.build())
				.build());
		}
	}

	private Widget createText(Widget container, String text, int color, int width, int height, int x, int y)
	{
		Widget widget = container.createChild(-1, WidgetType.TEXT);
		widget.setOriginalWidth(width);
		widget.setOriginalHeight(height);
		widget.setOriginalX(x);
		widget.setOriginalY(y);

		widget.setText(text);
		widget.setFontId(FontID.PLAIN_11);
		widget.setTextColor(color);
		widget.setTextShadowed(true);

		widget.revalidate();

		return widget;
	}
}
