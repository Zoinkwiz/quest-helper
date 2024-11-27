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
package com.questhelper.bank.banktab;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.panel.PanelDetails;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.util.LogicType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.api.Client;

@Singleton
public class QuestHelperBankTagService
{
	@Inject
	private QuestHelperPlugin plugin;

	@Inject
	private Client client;

	ArrayList<Integer> taggedItems;

	ArrayList<Integer> taggedItemsForBank;

	int lastTickUpdated = 0;

	int lastTickUpdatedForBank = 0;

	public ArrayList<Integer> itemsToTagForBank()
	{
		if (client.getTickCount() <= lastTickUpdatedForBank)
		{
			return taggedItemsForBank;
		}

		lastTickUpdatedForBank = client.getTickCount();

		return getItemsFromTabs(false);
	}

	public ArrayList<Integer> itemsToTag()
	{
		if (client.getTickCount() <= lastTickUpdated)
		{
			return taggedItems;
		}

		lastTickUpdated = client.getTickCount();

		return getItemsFromTabs(true);
	}

	private ArrayList<Integer> getItemsFromTabs(boolean onlyGetMissingItems)
	{
		ArrayList<BankTabItems> sortedItems = getPluginBankTagItemsForSections(onlyGetMissingItems);

		if (sortedItems == null)
		{
			return null;
		}

		taggedItemsForBank = new ArrayList<>();

		sortedItems.stream()
				.map(BankTabItems::getItems)
				.flatMap(Collection::stream)
				.map(BankTabItem::getItemIDs)
				.flatMap(Collection::stream)
				.filter(Objects::nonNull) // filter non-null just in case any Integer get in the list
				.filter(id -> !taggedItemsForBank.contains(id))
				.forEach(taggedItemsForBank::add);
		return taggedItemsForBank;
	}

	public ArrayList<BankTabItems> getPluginBankTagItemsForSections(boolean onlyGetMissingItems)
	{
		ArrayList<BankTabItems> newList = new ArrayList<>();

		List<PanelDetails> questSections = plugin.getSelectedQuest().getPanels();

		if (questSections == null || questSections.isEmpty())
		{
			return newList;
		}

		List<ItemRequirement> recommendedItems = plugin.getSelectedQuest().getItemRecommended();
		if (recommendedItems != null)
		{
			recommendedItems = recommendedItems.stream()
				.filter(Objects::nonNull)
				.filter(i -> (!onlyGetMissingItems || !i.checkWithBank(plugin.getClient())) && i.shouldDisplayText(plugin.getClient()))
				.collect(Collectors.toList());
		}

		if (recommendedItems != null && !recommendedItems.isEmpty())
		{
			BankTabItems pluginItems = new BankTabItems("Recommended items");
			// Here we specify getItems so as to avoid a double 'Recommended' title
			recommendedItems.forEach(item -> getItemsFromRequirement(pluginItems.getItems(), item, item));
			newList.add(pluginItems);
		}

		List<PanelDetails> shouldShowSections = questSections.stream()
			.filter(panelDetail -> panelDetail.getHideCondition() == null ||
				!panelDetail.getHideCondition().check(plugin.getClient()))
			.collect(Collectors.toList());

		for (PanelDetails questSection : shouldShowSections)
		{
			List<ItemRequirement> items = new ArrayList<>();
			if (questSection.getRequirements() != null)
			{
				items = questSection.getRequirements()
					.stream()
					.filter(ItemRequirement.class::isInstance)
					.map(ItemRequirement.class::cast)
					.filter(i -> (!onlyGetMissingItems
						|| !i.checkWithBank(plugin.getClient()))
						&& i.shouldDisplayText(plugin.getClient()))
					.collect(Collectors.toList());
			}
			List<ItemRequirement> recommendedItemsForSection = new ArrayList<>();
			if (questSection.getRecommended() != null)
			{
				recommendedItemsForSection = questSection.getRecommended()
					.stream()
					.filter(ItemRequirement.class::isInstance)
					.map(ItemRequirement.class::cast)
					.filter(i -> (!onlyGetMissingItems
						|| !i.checkWithBank(plugin.getClient()))
						&& i.shouldDisplayText(plugin.getClient()))
					.collect(Collectors.toList());
			}

			BankTabItems pluginItems = new BankTabItems(questSection.getHeader());
			items.forEach(item -> getItemsFromRequirement(pluginItems.getItems(), item, item));
			recommendedItemsForSection.forEach(item -> getItemsFromRequirement(pluginItems.getRecommendedItems(), item, item));
			// We don't add the recommended items as they're already used
			newList.add(pluginItems);
		}

		return newList;
	}

	private void getItemsFromRequirement(List<BankTabItem> pluginItems, ItemRequirement itemRequirement, ItemRequirement realItem)
	{
		if (itemRequirement instanceof ItemRequirements)
		{
			ItemRequirements itemRequirements = (ItemRequirements) itemRequirement;
			LogicType logicType = itemRequirements.getLogicType();
			ArrayList<ItemRequirement> requirements = itemRequirements.getItemRequirements();
			if (logicType == LogicType.AND)
			{
				requirements.forEach(req -> getItemsFromRequirement(pluginItems, req, req));
			}
			if (logicType == LogicType.OR)
			{
				List<ItemRequirement> itemsWhichPassReq = requirements.stream()
					.filter(r -> r.shouldDisplayText(plugin.getClient()))
					.collect(Collectors.toList());

				if (itemsWhichPassReq.isEmpty())
				{
					getItemsFromRequirement(pluginItems, requirements.get(0).named(itemRequirements.getName()), requirements.get(0));
				}
				else
				{
					ItemRequirement match = itemsWhichPassReq.stream()
						.filter(r -> r.checkWithBank(plugin.getClient()))
						.findFirst()
						.orElse(itemsWhichPassReq.get(0).named(itemRequirements.getName()));

					getItemsFromRequirement(pluginItems, match, match);
				}
			}
		}
		else
		{
			if (itemRequirement.getDisplayItemId() != null)
			{
				pluginItems.add(new BankTabItem(realItem));
			}
			else if (!itemRequirement.getDisplayItemIds().contains(-1))
			{
				pluginItems.add(makeBankTabItem(realItem));
			}
		}
	}

	private BankTabItem makeBankTabItem(ItemRequirement item)
	{
		List<Integer> itemIds = item.getDisplayItemIds();

		Integer displayId = itemIds.stream().filter(this::hasItemInBankOrPotionStorage).findFirst().orElse(itemIds.get(0));

		return new BankTabItem(item, displayId);
	}

	public boolean hasItemInBankOrPotionStorage(int itemID)
	{
		ItemRequirement tmpReq = new ItemRequirement("tmp", itemID);
		return tmpReq.checkWithBank(client);
	}
}
