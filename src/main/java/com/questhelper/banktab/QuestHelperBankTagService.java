/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
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

import com.questhelper.QuestHelperPlugin;
import com.questhelper.banktab.BankTabItem;
import com.questhelper.banktab.BankTabItems;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.requirements.ItemRequirements;
import com.questhelper.steps.conditional.LogicType;
import java.util.ArrayList;
import java.util.HashMap;
import javax.inject.Inject;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;

public class QuestHelperBankTagService
{
	private final QuestHelperPlugin plugin;

	@Inject
	public QuestHelperBankTagService(QuestHelperPlugin plugin)
	{
		this.plugin = plugin;
	}

	public ArrayList<Integer> itemsToTag()
	{
		ArrayList<BankTabItems> sortedItems = getPluginBankTagItemsForSections();

		if (sortedItems == null)
		{
			return null;
		}

		ArrayList<Integer> flattenedList = new ArrayList<>();

		for (BankTabItems sortedItem : sortedItems)
		{
			for (BankTabItem pluginBankTabItem : sortedItem.getItems())
			{
				ArrayList<Integer> itemIds = pluginBankTabItem.getItemIDs();
				if (itemIds != null)
				{
					for (Integer itemId : itemIds)
					{
						if (!flattenedList.contains(itemId))
						{
							flattenedList.add(itemId);
						}
					}
				}
			}
		}
		return flattenedList;
	}
	
	public ArrayList<BankTabItems> getPluginBankTagItemsForSections()
	{
		ArrayList<BankTabItems> newList = new ArrayList<>();

		ArrayList<PanelDetails> questSections = plugin.getSelectedQuest().getPanels();

		if (questSections == null || questSections.isEmpty())
		{
			return newList;
		}

		ArrayList<ItemRequirement> recommendedItems = plugin.getSelectedQuest().getItemRecommended();

		if (recommendedItems != null && !recommendedItems.isEmpty())
		{
			BankTabItems pluginItems = new BankTabItems("Recommended items");
			for (ItemRequirement item : recommendedItems)
			{
				getItemsFromRequirement(pluginItems, item);
			}
			newList.add(pluginItems);
		}

		for (PanelDetails questSection : questSections)
		{
			ArrayList<ItemRequirement> items = questSection.getItemRequirements();
			BankTabItems pluginItems = new BankTabItems(questSection.getHeader());

			for (ItemRequirement item : items)
			{
				getItemsFromRequirement(pluginItems, item);
			}
			newList.add(pluginItems);
		}

		return newList;
	}

	private void getItemsFromRequirement(BankTabItems pluginItems, ItemRequirement itemRequirement)
	{
		if (itemRequirement instanceof ItemRequirements)
		{
			ItemRequirements itemRequirements = (ItemRequirements) itemRequirement;
			LogicType logicType = itemRequirements.getLogicType();
			ArrayList<ItemRequirement> requirements = itemRequirements.getItemRequirements();
			if (logicType == LogicType.AND)
			{
				for (ItemRequirement requirement : requirements)
				{
					getItemsFromRequirement(pluginItems, requirement);
				}
			}
			if (logicType == LogicType.OR)
			{
				boolean foundMatch = false;
				for (ItemRequirement requirement : requirements)
				{
					if (requirement.checkBank(plugin.getClient()))
					{
						foundMatch = true;
						getItemsFromRequirement(pluginItems, requirement);
						break;
					}
				}
				if (!foundMatch)
				{
					getItemsFromRequirement(pluginItems, requirements.get(0));
				}
			}
		}
		else
		{
			if (itemRequirement.getDisplayItemId() != null)
			{
				pluginItems.addItems(new BankTabItem(itemRequirement.getQuantity(), itemRequirement.getName(),
					itemRequirement.getId(), itemRequirement.getTip(), itemRequirement.getDisplayItemId()));
			}
			else if (!itemRequirement.getDisplayItemIds().contains(-1))
			{
				pluginItems.addItems(makeBankTabItem(itemRequirement));
			}
		}
	}

	private BankTabItem makeBankTabItem(ItemRequirement item)
	{
		ArrayList<Integer> itemIds = item.getDisplayItemIds();

		Integer displayId = itemIds.get(0);

		for (Integer itemId : itemIds)
		{
			if (hasItemInBank(itemId))
			{
				displayId = itemId;
				break;
			}
		}

		return new BankTabItem(item.getQuantity(), item.getName(), displayId, item.getTip());
	}

	public boolean hasItemInBank(int itemID)
	{
		ItemContainer bankContainer = plugin.getClient().getItemContainer(InventoryID.BANK);
		if (bankContainer == null)
		{
			return false;
		}

		return bankContainer.contains(itemID);
	}
}
