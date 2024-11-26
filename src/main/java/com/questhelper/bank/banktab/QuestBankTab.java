/*
 * Copyright (c) 2021, geheur <https://github.com/geheur>
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
package com.questhelper.bank.banktab;

import com.google.common.primitives.Shorts;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.requirements.item.ItemRequirement;
import java.awt.Color;
import java.awt.Point;
import java.util.*;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GrandExchangeSearched;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.banktags.BankTag;
import net.runelite.client.plugins.banktags.tabs.Layout;
import net.runelite.client.util.QuantityFormatter;
import net.runelite.client.util.Text;

import static net.runelite.client.plugins.banktags.BankTagsPlugin.*;

@Singleton
@Slf4j
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
	private static final int EMPTY_BANK_SLOT_ID = 6512;

	private static final int MAX_RESULT_COUNT = 250;

	private static final int CROSS_SPRITE_ID = 1216;
	private static final int TICK_SPRITE_ID = 1217;


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

	@Inject
	private QuestGrandExchangeInterface geButtonWidget;

	@Inject
	private QuestHelperPlugin questHelper;

	@Inject
	private PotionStorage potionStorage;

	@Inject
	private QuestHelperBankTagService questHelperBankTagService;

	private final HashMap<Widget, BankTabItem> widgetItems = new HashMap<>();

	private final HashMap<BankWidget, BankWidget> fakeToRealItem = new HashMap<>();

	public void startUp()
	{
		if (questHelper.getSelectedQuest() != null)
		{
			clientThread.invokeLater(questBankTabInterface::init);
			clientThread.invokeLater(geButtonWidget::init);
		}
	}

	public void shutDown()
	{
		clientThread.invokeLater(questBankTabInterface::destroy);
		clientThread.invokeLater(geButtonWidget::destroy);
		if (!addedWidgets.isEmpty())
		{
			for (Widget addedWidget : addedWidgets)
			{
				addedWidget.setHidden(true);
			}
			addedWidgets.clear();
		}
	}

	public void register(EventBus eventBus)
	{
		potionStorage.setQuestBankTabInterface(questBankTabInterface);
		eventBus.register(potionStorage);
		eventBus.register(this);
	}

	public void unregister(EventBus eventBus)
	{
		eventBus.unregister(potionStorage);
		eventBus.unregister(this);
	}

	public void refreshBankTab()
	{
		questBankTabInterface.refreshTab();
	}

	@Subscribe
	public void onGrandExchangeSearched(GrandExchangeSearched event)
	{
		final String input = client.getVarcStrValue(VarClientStr.INPUT_TEXT);
		String QUEST_BANK_TAG = "quest-helper";

		if (!input.equals(QUEST_BANK_TAG) || questHelper.getSelectedQuest() == null)
		{
			return;
		}
		event.consume();
		updateGrandExchangeResults();
	}

	public void updateGrandExchangeResults()
	{
		final List<Integer> idsList = questHelper.itemsToTag();

		final Set<Integer> ids = idsList.stream()
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
			if (questBankTabInterface.isQuestTabActive())
			{
				Widget bankTitle = client.getWidget(ComponentID.BANK_TITLE_BAR);
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

				// Since the script vm isn't reentrant, we can't call into POTIONSTORE_DOSES/POTIONSTORE_WITHDRAW_DOSES
				// from bankmain_finishbuilding for the layout. Instead, we record all of the potions on client tick,
				// which is after this is run, but before the var/inv transmit listeners run, so that we will have
				// them by the time the inv transmit listener runs.
				potionStorage.cachePotions = true;
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
		}
		else if ("bankSearchFilter".equals(eventName))
		{
			final int itemId = intStack[intStackSize - 1];
			if (!questBankTabInterface.isQuestTabActive())
			{
				return;
			}

			if (itemId == -1)
			{
				// item -1 always passes on a laid out tab so items can be dragged to it
				return;
			}
			List<Integer> items = questHelperBankTagService.itemsToTagForBank();
			if (itemId > -1 && items.contains(itemId))
			{
				// return true
				intStack[intStackSize - 2] = 1;
			}
			else
			{
				// if the item isn't tagged we return false to prevent the item matching if the item name happens
				// to contain the tag name.
				intStack[intStackSize - 2] = 0;
			}
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == InterfaceID.BANK && questHelper.getSelectedQuest() != null)
		{
			questBankTabInterface.init();
		}
	}

	@Subscribe
	public void onClientTick(ClientTick clientTick)
	{
		if (!questBankTabInterface.isQuestTabActive() || questBankTabInterface.isHidden()) return;

		net.runelite.api.Point mousePoint = client.getMouseCanvasPosition();
		if (fakeToRealItem.isEmpty())
		{
			return;
		}

		for (BankWidget bankWidget : fakeToRealItem.keySet())
		{
			if (bankWidget.isPointOverWidget(mousePoint))
			{
				bankWidget.swap(fakeToRealItem.get(bankWidget));
				return;
			}
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
		int SEARCHBOX_LOADED = 750;
		if (event.getScriptId() == SEARCHBOX_LOADED)
		{
			geButtonWidget.init();
		}

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
			fakeToRealItem.clear();

			return;
		}

		if (event.getScriptId() != ScriptID.BANKMAIN_FINISHBUILDING)
		{
			return;
		}

		if (!questBankTabInterface.isQuestTabActive())
		{
			return;
		}

		Widget itemContainer = client.getWidget(ComponentID.BANK_ITEM_CONTAINER);
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
		fakeToRealItem.clear();

		Widget[] containerChildren = itemContainer.getDynamicChildren();

		clientThread.invokeAtTickEnd(() -> {
			// Desired extra functionality:
			// X - Recommended items also included in section
			// X - Expand option to see alternative items for a recommended item
			List<BankTabItems> tabLayout = questHelper.getPluginBankTagItemsForSections();

			if (tabLayout != null)
			{
				sortBankTabItems(itemContainer, containerChildren, tabLayout);
			}
		});
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
			else if (!itemWidget.isHidden() &&
				itemWidget.getItemId() != -1 &&
				!itemList.contains(itemWidget.getItemId()) &&
				itemWidget.getItemId() != EMPTY_BANK_SLOT_ID)
			{
				itemList.add(itemWidget.getItemId());
			}
		}

		List<BankText> bankItemTexts = new ArrayList<>();
		HashMap<Integer, BankWidget> itemIDsAdded = new HashMap<>();

		for (BankTabItems bankTabItems : newLayout)
		{
			totalSectionsHeight = addPluginTabSection(itemContainer, bankTabItems, itemList, totalSectionsHeight, bankItemTexts, itemIDsAdded);
		}

		// We add item texts after all items are added so they always overlay
		for (BankText bankText : bankItemTexts)
		{
			Widget realItemQuantityText = createText(itemContainer,
				bankText.text,
				Color.WHITE.getRGB(),
				ITEM_HORIZONTAL_SPACING,
				TEXT_HEIGHT - 3,
				bankText.x,
				bankText.y);

			addedWidgets.add(realItemQuantityText);

			if (bankText.spriteID != -1)
			{
				Widget realItemInInventorySprite = createIcon(itemContainer,
					bankText.spriteID,
					10,
					10,
					bankText.spriteX,
					bankText.spriteY
				);
				addedWidgets.add(realItemInInventorySprite);
			}

		}

		totalSectionsHeight = addGeneralSection(itemContainer, itemList, totalSectionsHeight);

		final Widget bankItemContainer = client.getWidget(ComponentID.BANK_ITEM_CONTAINER);
		if (bankItemContainer == null) return;
		int itemContainerHeight = bankItemContainer.getHeight();

		bankItemContainer.setScrollHeight(Math.max(totalSectionsHeight, itemContainerHeight));

		final int itemContainerScroll = bankItemContainer.getScrollY();
		clientThread.invokeLater(() ->
			client.runScript(ScriptID.UPDATE_SCROLLBAR,
				ComponentID.BANK_SCROLLBAR,
				ComponentID.BANK_ITEM_CONTAINER,
				itemContainerScroll));
	}

	private int addPluginTabSection(Widget itemContainer, BankTabItems items, List<Integer> itemIds,
									int totalSectionsHeight, List<BankText> bankItemTexts,
									HashMap<Integer, BankWidget> itemIDsAdded)
	{
		int newHeight = totalSectionsHeight;

		// Contains list of items used, for easy identification for duplicate items

		if (items == null || (items.getItems().isEmpty() && items.getRecommendedItems().isEmpty()))
		{
			return newHeight;
		}

		// Presume there'll be some content as we have fake items now
		newHeight = addSectionHeader(itemContainer, items.getName(), totalSectionsHeight);

		if (!items.getItems().isEmpty())
		{
			newHeight = createPartialSection(itemContainer, items.getItems(), itemIds, newHeight, bankItemTexts, itemIDsAdded);
		}

		if (!items.getRecommendedItems().isEmpty())
		{
			newHeight = addSubSectionHeader(itemContainer, "Recommended", newHeight);
			newHeight = createPartialSection(itemContainer, items.getRecommendedItems(), itemIds, newHeight, bankItemTexts, itemIDsAdded);
		}

		return newHeight;
	}


	// Returns number of items added in partial section
	private int createPartialSection(Widget itemContainer, List<BankTabItem> items, List<Integer> itemIds,
									 int totalSectionsHeight, List<BankText> bankItemTexts,
									 HashMap<Integer, BankWidget> itemIDsAdded)
	{
		int totalItemsAdded = 0;
		// Loop through all items in section
		for (BankTabItem bankTabItem : items)
		{
			boolean foundItem = false;

			// If item exists, move it to correct pos + append a quantity required string
			if (!Collections.disjoint(itemIds, bankTabItem.getItemIDs()))
			{
				// Loop through all widgets to find there's a real item in bank
				for (Widget widget : itemContainer.getDynamicChildren())
				{
					if (!widget.isHidden() && widget.getOpacity() != 150 && (bankTabItem.getItemIDs().contains(widget.getItemId())))
					{
						foundItem = true;

						Point point = placeItem(widget, totalItemsAdded, totalSectionsHeight);
						widget.setItemQuantityMode(1);
						widgetItems.put(widget, bankTabItem);

						if (bankTabItem.getQuantity() > 0)
						{
							makeBankText(widget.getItemQuantity(), bankTabItem.getQuantity(), point.x, point.y, bankTabItem.getItemRequirement(), bankItemTexts);
						}

						totalItemsAdded++;
						itemIds.removeAll(Collections.singletonList(widget.getItemId()));
						itemIDsAdded.put(widget.getItemId(), new BankWidget(widget));
						break;
					}
				}
			}

			if (!foundItem)
			{
				// calculate correct item position as if this was a normal tab
				int adjXOffset = (totalItemsAdded % ITEMS_PER_ROW) * ITEM_HORIZONTAL_SPACING + ITEM_ROW_START;
				int adjYOffset = totalSectionsHeight + (totalItemsAdded / ITEMS_PER_ROW) * ITEM_VERTICAL_SPACING;

				Widget fakeItemWidget;
				// Have list of all real items + text. Do check to see if any of those items
				// Match the ItemIDs
				if (Collections.disjoint(itemIDsAdded.keySet(), bankTabItem.getItemIDs()))
				{
					fakeItemWidget = createMissingItem(itemContainer, bankTabItem, adjXOffset, adjYOffset);
					itemIds.removeAll(bankTabItem.getItemIDs());
				}
				else
				{
					List<Integer> result = bankTabItem.getItemIDs().stream()
						.distinct()
						.filter(itemIDsAdded.keySet()::contains)
						.collect(Collectors.toList());

					BankWidget realItemWidget = itemIDsAdded.get((result.get(0)));

					fakeItemWidget = createDuplicateItem(itemContainer, bankTabItem,
						realItemWidget.getItemQuantity(), adjXOffset, adjYOffset);

					fakeToRealItem.put(new BankWidget(fakeItemWidget), realItemWidget);
				}

				if (bankTabItem.getQuantity() > 0)
				{
					makeBankText(fakeItemWidget.getItemQuantity(), bankTabItem.getQuantity(), adjXOffset, adjYOffset, bankTabItem.getItemRequirement(), bankItemTexts);
				}

				widgetItems.put(fakeItemWidget, bankTabItem);
				addedWidgets.add(fakeItemWidget);

				totalItemsAdded++;
			}
		}

		int newHeight = totalSectionsHeight + (totalItemsAdded / ITEMS_PER_ROW) * ITEM_VERTICAL_SPACING;
		newHeight = totalItemsAdded % ITEMS_PER_ROW != 0 ? newHeight + ITEM_VERTICAL_SPACING : newHeight;
		return newHeight;
	}

	private void makeBankText(int currentQuantity, int goalQuantity, int baseX, int baseY, ItemRequirement item, List<BankText> bankItemTexts)
	{
		String quantityString = QuantityFormatter.quantityToStackSize(goalQuantity);
		int requirementLength = (int) Math.round(quantityString.length() * 5.5);
		int extraLength =
			QuantityFormatter.quantityToStackSize(currentQuantity).length() * 6;

		int xPos = baseX + 2 + extraLength;
		int yPos = baseY - 1;
		if (extraLength + requirementLength > 20)
		{
			xPos = baseX;
			yPos = baseY + 9;
		}

		boolean hasItem = item.check(client);
		int spritePosX = xPos + requirementLength + 10;
		int spritePosY = yPos;
		// If required quantity moved down a line, put tick/cross after current quantity
		if (yPos != baseY - 1)
		{
			spritePosX = baseX + 2 + extraLength;
			spritePosY = baseY - 1;
		}

		if (hasItem)
		{
			bankItemTexts.add(new BankText("/ " + quantityString, xPos, yPos, TICK_SPRITE_ID, spritePosX, spritePosY));
		}
		else
		{
			bankItemTexts.add(new BankText("/ " + quantityString, xPos, yPos, CROSS_SPRITE_ID, spritePosX, spritePosY));
		}
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

	private int addSubSectionHeader(Widget itemContainer, String title, int totalSectionsHeight)
	{
		addedWidgets.add(createText(itemContainer, title, new Color(228, 216, 162).getRGB(), (ITEMS_PER_ROW * ITEM_HORIZONTAL_SPACING) + ITEM_ROW_START
			, TEXT_HEIGHT, ITEM_ROW_START, totalSectionsHeight + LINE_VERTICAL_SPACING));

		return totalSectionsHeight + LINE_VERTICAL_SPACING + TEXT_HEIGHT;
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
		if (bankTabItem.getItemRequirement().getDisplayItemId() != null)
		{
			itemIDs = Collections.singletonList(bankTabItem.getItemRequirement().getDisplayItemId());
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

	private Widget createDuplicateItem(Widget container, BankTabItem bankTabItem, int quantity, int x, int y)
	{
		Widget widget = container.createChild(-1, WidgetType.GRAPHIC);
		widget.setItemQuantityMode(1); // quantity of 1 still shows number
		widget.setOriginalWidth(ITEM_WIDTH);
		widget.setOriginalHeight(ITEM_HEIGHT);
		widget.setOriginalX(x);
		widget.setOriginalY(y);
		widget.setBorderType(1);

		List<Integer> itemIDs = bankTabItem.getItemIDs();
		if (bankTabItem.getDisplayID() != null)
		{
			itemIDs = Collections.singletonList(bankTabItem.getDisplayID());
		}

		widget.setItemId(itemIDs.get(0));
		widget.setName("<col=ff9040>" + bankTabItem.getText() + "</col>");
		if (bankTabItem.getDetails() != null)
		{
			widget.setText(bankTabItem.getDetails());
		}
		widget.setItemQuantity(quantity);
		widget.setOnOpListener(ScriptID.NULL);
		widget.setHasListener(true);

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

	private Widget createIcon(Widget container, int spriteID, int width, int height, int x, int y)
	{
		Widget widget = container.createChild(-1, WidgetType.GRAPHIC);
		widget.setOriginalWidth(width);
		widget.setOriginalHeight(height);
		widget.setOriginalX(x);
		widget.setOriginalY(y);

		widget.setSpriteId(spriteID);

		widget.revalidate();

		return widget;
	}
}
