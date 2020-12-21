/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper;

import com.questhelper.panel.PanelDetails;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.requirements.ItemRequirements;
import com.questhelper.steps.conditional.LogicType;
import java.util.ArrayList;
import java.util.HashMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.client.plugins.banktags.PluginBankTagService;
import net.runelite.client.plugins.banktags.PluginBankTabItem;
import net.runelite.client.plugins.banktags.PluginBankTabItems;

@Singleton
public class QuestHelperBankTagService implements PluginBankTagService
{
	private final QuestHelperPlugin plugin;

	@Inject
	public QuestHelperBankTagService(QuestHelperPlugin plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public boolean shouldTag(int itemID)
	{
		return itemsToTag().contains(itemID);
	}

	@Override
	public ArrayList<Integer> itemsToTag()
	{
		ArrayList<PluginBankTabItems> sortedItems = getPluginBankTagItemsForSections();

		if (sortedItems == null)
		{
			return null;
		}

		ArrayList<Integer> flattenedList = new ArrayList<>();

		for (PluginBankTabItems sortedItem : sortedItems)
		{
			for (PluginBankTabItem pluginBankTabItem : sortedItem.getItems())
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

	@Override
	public ArrayList<PluginBankTabItems> getPluginBankTagItemsForSections()
	{
		ArrayList<PluginBankTabItems> newList = new ArrayList<>();
		if (plugin.getSelectedQuest() == null)
		{
			return newList;
		}

		ArrayList<PanelDetails> questSections = plugin.getSelectedQuest().getPanels();

		if (questSections == null || questSections.isEmpty())
		{
			return newList;
		}

		ArrayList<ItemRequirement> recommendedItems = plugin.getSelectedQuest().getItemRecommended();

		if (recommendedItems != null && !recommendedItems.isEmpty())
		{
			PluginBankTabItems pluginItems = new PluginBankTabItems("Recommended items");
			for (ItemRequirement item : recommendedItems)
			{
				getItemsFromRequirement(pluginItems, item);
			}
			newList.add(pluginItems);
		}

		for (PanelDetails questSection : questSections)
		{
			ArrayList<ItemRequirement> items = questSection.getItemRequirements();
			PluginBankTabItems pluginItems = new PluginBankTabItems(questSection.getHeader());

			for (ItemRequirement item : items)
			{
				getItemsFromRequirement(pluginItems, item);
			}
			newList.add(pluginItems);
		}

		return newList;
	}

	private void getItemsFromRequirement(PluginBankTabItems pluginItems, ItemRequirement itemRequirement)
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
				// If any of the requirements pass, use it
				// Else, use first one?
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
			if (!itemRequirement.getDisplayItemIds().contains(-1))
			{
				pluginItems.addItems(makeBankTabItem(itemRequirement));
			}
		}
	}

	private PluginBankTabItem makeBankTabItem(ItemRequirement item)
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

		return new PluginBankTabItem(item.getQuantity(), item.getName(), displayId);
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

	@Override
	public boolean shouldSortTabIntoSections()
	{
		return true;
	}

	@Override
	public boolean shouldShowMissingItems()
	{
		return true;
	}
}
