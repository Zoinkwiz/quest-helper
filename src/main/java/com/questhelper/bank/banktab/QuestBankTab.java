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
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.gameval.VarClientID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.ItemQuantityMode;
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
import net.runelite.client.util.QuantityFormatter;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.Point;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.questhelper.bank.banktab.PotionStorage.COMPONENTS_PER_POTION;
import static com.questhelper.bank.banktab.PotionStorage.VIAL_IDX;
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
	private static final int EMPTY_BANK_SLOT_ID = ItemID.BLANKOBJECT;

	private static final int MAX_RESULT_COUNT = 250;

	private static final int CROSS_SPRITE_ID = SpriteID.Checkbox.CROSSED;
	private static final int TICK_SPRITE_ID = SpriteID.Checkbox.CHECKED;

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

	private int originalContainerChildren = -1;

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
		clientThread.invokeLater(this::removeAddedWidgets);
	}
	public void register(EventBus eventBus)
	{
		potionStorage.setQuestBankTabInterface(questBankTabInterface);
		eventBus.register(potionStorage);
		eventBus.register(this);
	}

	public void unregister(EventBus eventBus)
	{
		potionStorage.setQuestBankTabInterface(null);
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
		final String input = client.getVarcStrValue(VarClientID.MESLAYERINPUT);
		String QUEST_BANK_TAG = "quest-helper";

		if (!input.equals(QUEST_BANK_TAG) || questHelper.getSelectedQuest() == null)
		{
			return;
		}
		event.consume();

		var itemsToTag = questHelper.getSelectedQuest().getCurrentStep().getActiveStep().getGeInterfaceIcon();
		if (geButtonWidget.notAtGE())
		{
			if (itemsToTag != null)
			{
				updateGrandExchangeUiForSpecificItem(itemsToTag);
			}
		}
		else
		{
			updateGrandExchangeResults();
		}
	}

	public void updateGrandExchangeUiForSpecificItem(List<Integer> itemToTag)
	{
		client.setGeSearchResultIndex(0);
		client.setGeSearchResultCount(itemToTag.size());

		client.setGeSearchResultIds(Shorts.toArray(itemToTag));
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
		final int POTION_STORE_UPDATED = 6555;

		int scriptId = event.getScriptId();

		if (scriptId == POTION_STORE_UPDATED)
		{
			// Since the script vm isn't reentrant, we can't call into POTIONSTORE_DOSES/POTIONSTORE_WITHDRAW_DOSES
			// from bankmain_finishbuilding for the layout. Instead, we record all of the potions on client tick,
			// which is after this is run, but before the var/inv transmit listeners run, so that we will have
			// them by the time the inv transmit listener runs.
			potionStorage.updateCachedPotions = true;
		}
		else if (scriptId == ScriptID.BANKMAIN_FINISHBUILDING)
		{
			resetWidgets();
			if (questBankTabInterface.isQuestTabActive())
			{
				Widget bankTitle = client.getWidget(InterfaceID.Bankmain.TITLE);
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
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == InterfaceID.BANKMAIN && questHelper.getSelectedQuest() != null)
		{
			questBankTabInterface.init();
		}
	}

	@Subscribe(priority = -1)
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		questBankTabInterface.handleClick(event);

		// Update widget index of the menu so withdraws work in laid out tabs.
		if (event.getParam1() == InterfaceID.Bankmain.ITEMS && questBankTabInterface.isQuestTabActive())
		{
			MenuEntry menu = event.getMenuEntry();
			if ("Details".equals(menu.getOption()))
			{
				event.consume();

				Widget widget = event.getWidget();
				if (widget == null) return;
				BankTabItem bankTabItem = widgetItems.get(widget);
				if (bankTabItem == null) return;
				handleFakeItemClick(bankTabItem);
				return;
			}

			Widget w = menu.getWidget();
			if (w != null && w.getItemId() > -1)
			{
				ItemContainer bank = client.getItemContainer(InventoryID.BANK);
				int idx = bank.find(w.getItemId());
				if (idx > -1 && menu.getParam0() != idx)
				{
					menu.setParam0(idx);
					return;
				}

				idx = potionStorage.getIdx(w.getItemId());
				if (idx > -1)
				{
					potionStorage.prepareWidgets();
					menu.setParam1(InterfaceID.Bankmain.POTIONSTORE_ITEMS);
					menu.setParam0(idx);
				}
			}
		}
	}

	private void resetWidgets()
	{
		// We adjust the bank item container children's sizes in layouts,
		// however they are only initially set when the bank is opened,
		// so we have to reset them each time the bank is built.
		Widget w = client.getWidget(InterfaceID.Bankmain.ITEMS);
		if (w == null || w.getChildren() == null) return;

		for (Widget c : w.getChildren())
		{
			if (c.getOriginalHeight() < BANK_ITEM_HEIGHT)
			{
				break;
			}

			if (c.getOriginalWidth() != BANK_ITEM_WIDTH || c.getOriginalHeight() != BANK_ITEM_HEIGHT)
			{
				c.setOriginalWidth(BANK_ITEM_WIDTH);
				c.setOriginalHeight(BANK_ITEM_HEIGHT);
				c.revalidate();
			}
		}
	}

	private void removeAddedWidgets()
	{
		if (originalContainerChildren == -1) return;

		if (addedWidgets.isEmpty()) return;
		Widget parent = addedWidgets.get(0).getParent();
		if (parent == null) return;
		if (parent.getChildren() == null) return;
		parent.setChildren(Arrays.copyOf(parent.getChildren(), originalContainerChildren));
		parent.revalidate();

		addedWidgets.clear();
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

			return;
		}

		if (event.getScriptId() != ScriptID.BANKMAIN_FINISHBUILDING)
		{
			return;
		}

		removeAddedWidgets();

		if (!questBankTabInterface.isQuestTabActive())
		{
			return;
		}

		Widget itemContainer = client.getWidget(InterfaceID.Bankmain.ITEMS);
		if (itemContainer == null)
		{
			return;
		}
		Widget[] children = itemContainer.getChildren();
		if (children != null && originalContainerChildren == -1) originalContainerChildren = children.length;

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

		widgetItems.clear();

		// Hide all widgets as we'll be making our own using them
		hideBankWidgets(itemContainer, containerChildren);


		List<Integer> itemList = new ArrayList<>();
		for (Widget itemWidget : containerChildren)
		{
			if (itemWidget.getSpriteId() == SpriteID.TRADEBACKING_DARK
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

		addRemainingBankTab(client, newLayout);

		for (BankTabItems bankTabItems : newLayout)
		{
			totalSectionsHeight = addPluginTabSection(itemContainer, bankTabItems, totalSectionsHeight, bankItemTexts);
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
						bankText.spriteX,
					bankText.spriteY
				);
				addedWidgets.add(realItemInInventorySprite);
			}

			currentWidgetToUse = 0;
		}

		final Widget bankItemContainer = client.getWidget(InterfaceID.Bankmain.ITEMS);
		if (bankItemContainer == null) return;
		int itemContainerHeight = bankItemContainer.getHeight();

		bankItemContainer.setScrollHeight(Math.max(totalSectionsHeight, itemContainerHeight));

		final int itemContainerScroll = bankItemContainer.getScrollY();
		clientThread.invokeLater(() ->
			client.runScript(ScriptID.UPDATE_SCROLLBAR,
				InterfaceID.Bankmain.SCROLLBAR,
				InterfaceID.Bankmain.ITEMS,
				itemContainerScroll));
	}

	/*
	** Creates a tab in the quest bank for all items not used in the active quest
	 */
	private void addRemainingBankTab(Client client, List<BankTabItems> newLayout)
	{
		ItemContainer bankContainer = client.getItemContainer(InventoryID.BANK);
		if (bankContainer == null)
		{
			newLayout.add(new BankTabItems("Non-quest items"));
			return;
		}

		Set<Integer> usedIds = newLayout.stream()
				.flatMap(tab -> Stream.concat(
						tab.getItems().stream().flatMap(item -> item.getItemIDs().stream()),
						tab.getRecommendedItems().stream().flatMap(item -> item.getItemIDs().stream())
				))
				.collect(Collectors.toSet());

		Set<Integer> allIds = Arrays.stream(bankContainer.getItems())
				.map(Item::getId)
				.filter(id -> !usedIds.contains(id))
				.collect(Collectors.toCollection(LinkedHashSet::new));

		BankTabItems leftoverTab = new BankTabItems("Non-quest items");
		for (Integer id : allIds)
		{
			String name = client.getItemDefinition(id).getName();
			ItemRequirement req = new ItemRequirement(name, id, -1);
			leftoverTab.addItems(new BankTabItem(req));
		}

		newLayout.add(leftoverTab);
	}

	private void hideBankWidgets(Widget itemContainer, Widget[] containerChildren)
	{
		for (int i = 0; i < containerChildren.length; ++i)
		{
			Widget widget = itemContainer.getChild(i);
			if (widget == null) continue;

			// ~bankmain_drawitem uses 6512 for empty item slots
			if (!widget.isSelfHidden() &&
					(widget.getItemId() > -1 && widget.getItemId() != ItemID.BLANKOBJECT) ||
					(widget.getSpriteId() == SpriteID.TRADEBACKING_DARK || widget.getText().contains("Tab"))
			)
			{
				widget.setHidden(true);
			}
		}
	}

	private void drawItem(Widget c, int item, ItemContainer bank, BankTabItem bankTabItem)
	{
		if (item > -1 && item != ItemID.BANK_FILLER)
		{
			int qty = count(bank, item);
			ItemComposition def = client.getItemDefinition(item);

			int bankCount = bank.count(item);
			boolean isPotStorage = bankCount <= 0 && qty > 0;

			c.setItemId(item);
			c.setItemQuantity(qty);
			c.setItemQuantityMode(ItemQuantityMode.ALWAYS);

			// Effectively avoid dragging
			c.setDragDeadTime(1000);

			c.setName("<col=ff9040>" + def.getName() + "</col>");
			c.clearActions();

			// Jagex Placeholder
			if (def.getPlaceholderTemplateId() >= 0 && def.getPlaceholderId() >= 0)
			{
				c.setItemQuantity(qty);
				c.setOpacity(120);
				c.setAction(8 - 1, "Release");
				c.setAction(10 - 1, "Examine");
			}
			// Layout placeholder
			else if (qty == 0)
			{
				c.setOpacity(120);
				c.setItemQuantity(0);
				c.setItemQuantityMode(1);
				c.setText("<col=ff9040>" + bankTabItem.getText() + "</col>");
				c.setAction(1, "Details");
			}
			else
			{
				int quantityType = client.getVarbitValue(VarbitID.BANK_QUANTITY_TYPE);
				int requestQty = client.getVarbitValue(VarbitID.BANK_REQUESTEDQUANTITY);
				// ~script2759
				String suffix;
				switch (quantityType)
				{
					default:
						suffix = "1";
						break;
					case 1:
						suffix = "5";
						break;
					case 2:
						suffix = "10";
						break;
					case 3:
						suffix = Integer.toString(Math.max(1, requestQty));
						break;
					case 4:
						suffix = "All";
						break;
				}
				// ~script669
				c.setAction(0, "Withdraw-" + suffix);
				if (quantityType != 0)
				{
					c.setAction(1, "Withdraw-1");
				}
				c.setAction(2, "Withdraw-5");
				c.setAction(3, "Withdraw-10");
				if (requestQty > 0)
				{
					c.setAction(4, "Withdraw-" + requestQty);
				}
				c.setAction(5, "Withdraw-X");
				c.setAction(6, "Withdraw-All");
				c.setAction(7, "Withdraw-All-but-1");
				if (!isPotStorage && client.getVarbitValue(VarbitID.BANK_BANKOPS_TOGGLE_ON) == 1 && def.getIntValue(ParamID.BANK_AUTOCHARGE) != -1)
				{
					c.setAction(8, "Configure-Charges");
				}
				if (!isPotStorage && client.getVarbitValue(VarbitID.BANK_LEAVEPLACEHOLDERS) == 0)
				{
					c.setAction(9, "Placeholder");
				}
				if (!isPotStorage)
				{
					c.setAction(10, "Examine");
				}
				c.setOpacity(0);
			}

			c.setOnDragListener(ScriptID.BANKMAIN_DRAGSCROLL, ScriptEvent.WIDGET_ID, ScriptEvent.WIDGET_INDEX, ScriptEvent.MOUSE_X, ScriptEvent.MOUSE_Y,
					InterfaceID.Bankmain.SCROLLBAR, 0);
			c.setOnDragCompleteListener((JavaScriptCallback) ev -> {});
		}
		else
		{
			// pad size to not leave a gap between items
			c.setOriginalWidth(BANK_ITEM_WIDTH + BANK_ITEM_X_PADDING);
			c.setOriginalHeight(BANK_ITEM_HEIGHT + BANK_ITEM_Y_PADDING);
			c.clearActions();
			c.setItemId(-1);
			c.setItemQuantity(0);
			c.setOnDragListener((Object[]) null);
			c.setOnDragCompleteListener((Object[]) null);
		}
		widgetItems.put(c, bankTabItem);
		c.setHidden(false);
		c.revalidate();
	}

	private int addPluginTabSection(Widget itemContainer, BankTabItems items, int totalSectionsHeight, List<BankText> bankItemTexts)
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
			newHeight = createPartialSection(items.getItems(), newHeight, bankItemTexts);
		}

		if (!items.getRecommendedItems().isEmpty())
		{
			newHeight = addSubSectionHeader(itemContainer, "Recommended", newHeight);
			newHeight = createPartialSection(items.getRecommendedItems(), newHeight, bankItemTexts);
		}

		return newHeight;
	}

	private int count(ItemContainer bank, int itemId)
	{
		return bank.count(itemId) + potionStorage.count(itemId);
	}

	int currentWidgetToUse = 0;

	// Returns number of items added in partial section
	private int createPartialSection(List<BankTabItem> items, int totalSectionsHeight, List<BankText> bankItemTexts)
	{
		int totalItemsAdded = 0;

		ItemContainer bank = client.getItemContainer(InventoryID.BANK);
		if (bank == null) return totalSectionsHeight;
		Widget bankItemContainer = client.getWidget(InterfaceID.Bankmain.ITEMS);
		if (bankItemContainer == null) return totalSectionsHeight;

		for (BankTabItem item : items)
		{
			int itemId = item.getDisplayID();
			if (itemId == -1)
			{
				continue;
			}

			Widget c = bankItemContainer.getChild(currentWidgetToUse);
			if (c == null)
			{
				return totalSectionsHeight;
			}
			drawItem(c, itemId, bank, item);
			placeItem(c, totalItemsAdded, totalSectionsHeight);
			// move item
			if (item.getQuantity() > 0)
			{
				makeBankText(c.getItemQuantity(), item.getQuantity(), c.getOriginalX(), c.getOriginalY(), item.getItemRequirement(), bankItemTexts);
			}

			currentWidgetToUse++;
			totalItemsAdded++;
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

	private int addSubSectionHeader(Widget itemContainer, String title, int totalSectionsHeight)
	{
		addedWidgets.add(createText(itemContainer, title, new Color(228, 216, 162).getRGB(), (ITEMS_PER_ROW * ITEM_HORIZONTAL_SPACING) + ITEM_ROW_START
			, TEXT_HEIGHT, ITEM_ROW_START, totalSectionsHeight + LINE_VERTICAL_SPACING));

		return totalSectionsHeight + LINE_VERTICAL_SPACING + TEXT_HEIGHT;
	}

	private int addSectionHeader(Widget itemContainer, String title, int totalSectionsHeight)
	{
		addedWidgets.add(createGraphic(itemContainer, SpriteID.TRADEBACKING_DARK, ITEM_ROW_START, totalSectionsHeight));
		addedWidgets.add(createText(itemContainer, title, new Color(228, 216, 162).getRGB(), (ITEMS_PER_ROW * ITEM_HORIZONTAL_SPACING) + ITEM_ROW_START
			, TEXT_HEIGHT, ITEM_ROW_START, totalSectionsHeight + LINE_VERTICAL_SPACING));

		return totalSectionsHeight + LINE_VERTICAL_SPACING + TEXT_HEIGHT;
	}

	private void placeItem(Widget widget, int totalItemsAdded, int totalSectionsHeight)
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

		new Point(adjXOffset, adjYOffset);
	}

	private void handleFakeItemClick(BankTabItem bankTabItem)
	{
		String quantity = QuantityFormatter.formatNumber(bankTabItem.getQuantity()) + " x ";
		if (bankTabItem.getQuantity() == -1)
		{
			quantity = "some ";
		}
		final ChatMessageBuilder message = new ChatMessageBuilder()
				.append("You need ")
				.append(ChatColorType.HIGHLIGHT)
				.append(quantity)
				.append(Text.removeTags(bankTabItem.getText()))
				.append(".");

		if (bankTabItem.getDetails() != null && !bankTabItem.getDetails().isEmpty())
		{
			message.append(ChatColorType.NORMAL)
					.append(" " + bankTabItem.getDetails() + ".");
		}

		chatMessageManager.queue(QueuedMessage.builder()
				.type(ChatMessageType.ITEM_EXAMINE)
				.runeLiteFormattedMessage(message.build())
				.build());
	}

	private Widget createGraphic(Widget container, int spriteId, int x, int y)
	{
		final int WIDTH = ITEMS_PER_ROW * ITEM_HORIZONTAL_SPACING;
		Widget widget = container.createChild(-1, WidgetType.GRAPHIC);
		widget.setOriginalWidth(WIDTH);
		widget.setOriginalHeight(QuestBankTab.LINE_HEIGHT);
		widget.setOriginalX(x);
		widget.setOriginalY(y);

		widget.setSpriteId(spriteId);

		widget.revalidate();

		return widget;
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

	private Widget createIcon(Widget container, int spriteID, int x, int y)
	{
		final int WIDTH = 10;
		final int HEIGHT = 10;
		Widget widget = container.createChild(-1, WidgetType.GRAPHIC);
		widget.setOriginalWidth(WIDTH);
		widget.setOriginalHeight(HEIGHT);
		widget.setOriginalX(x);
		widget.setOriginalY(y);

		widget.setSpriteId(spriteID);

		widget.revalidate();

		return widget;
	}
}
