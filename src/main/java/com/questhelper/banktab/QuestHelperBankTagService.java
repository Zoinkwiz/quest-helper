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

import com.questhelper.ItemSearch;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.panel.PanelDetails;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.util.LogicType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.swing.SwingUtilities;

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

		 sortedItems.stream()
					.map(BankTabItems::getItems)
					.flatMap(Collection::stream)
					.map(BankTabItem::getItemIDs)
					.flatMap(Collection::stream)
					.filter(Objects::nonNull) // filter non-null just in case any Integer get in the list
					.filter(id -> !flattenedList.contains(id))
					.forEach(flattenedList::add);
		return flattenedList;
	}
	
	public ArrayList<BankTabItems> getPluginBankTagItemsForSections()
	{
		ArrayList<BankTabItems> newList = new ArrayList<>();

		List<PanelDetails> questSections = plugin.getSelectedQuest().getPanels();

		if (questSections == null || questSections.isEmpty())
		{
			return newList;
		}

		List<ItemRequirement> recommendedItems = plugin.getSelectedQuest().getItemRecommended();

		if (recommendedItems != null && !recommendedItems.isEmpty())
		{
			BankTabItems pluginItems = new BankTabItems("Recommended items");
			recommendedItems.forEach(item -> getItemsFromRequirement(pluginItems, item));
			newList.add(pluginItems);
		}

		for (PanelDetails questSection : questSections)
		{
			List<ItemRequirement> items = questSection.getRequirements()
				.stream()
				.filter(ItemRequirement.class::isInstance)
				.map(ItemRequirement.class::cast)
				.collect(Collectors.toList());

			BankTabItems pluginItems = new BankTabItems(questSection.getHeader());
			items.forEach(item -> getItemsFromRequirement(pluginItems, item));
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
				requirements.forEach(req -> getItemsFromRequirement(pluginItems, req));
			}
			if (logicType == LogicType.OR)
			{
				ItemRequirement match = requirements.stream()
					.filter(r -> r.checkBank(plugin.getClient()))
					.findFirst()
					.orElse(requirements.get(0));

				getItemsFromRequirement(pluginItems, match);
			}
		}
		else if (itemRequirement instanceof BankItemHolder)
		{
			BankItemHolder holder = (BankItemHolder) itemRequirement;
			// Force run on client thread even though it's not as responsive as not doing that, however it
			// ensures we run on the client thread and never run into threading issues.
			plugin.getClientThread().invoke(() -> {
				List<ItemRequirement> reqs = holder.getRequirements(plugin.getClient(), plugin);
				makeBankHolderItems(reqs, pluginItems); // callback because we can't halt on the client thread);
			});
		}
		else
		{
			makeBankHolderItems(Collections.singletonList(itemRequirement), pluginItems);
		}
	}

	private void makeBankHolderItems(List<ItemRequirement> requirements, BankTabItems pluginItems)
	{
		for (ItemRequirement req : requirements)
		{
			if (req.getDisplayItemId() != null)
			{
				pluginItems.addItems(new BankTabItem(req));
			}
			else if (!req.getDisplayItemIds().contains(-1))
			{
				pluginItems.addItems(makeBankTabItem(req));
			}
		}
	}

	private BankTabItem makeBankTabItem(ItemRequirement item)
	{
		List<Integer> itemIds = item.getDisplayItemIds();

		Integer displayId = itemIds.stream().filter(this::hasItemInBank).findFirst().orElse(itemIds.get(0));

		return new BankTabItem(item.getQuantity(), item.getName(), displayId, item.getTooltip());
	}

	public boolean hasItemInBank(int itemID)
	{
		return ItemSearch.hasItemInBank(itemID, plugin.getBankItems().getItems());
	}
}
