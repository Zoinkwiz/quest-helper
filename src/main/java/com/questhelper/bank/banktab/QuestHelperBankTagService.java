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
import com.questhelper.managers.QuestQueueManager;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.ComplexRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.item.KeyringRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.util.LogicType;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Singleton
public class QuestHelperBankTagService
{
	@Inject
	private QuestHelperPlugin plugin;

	@Inject
	private Client client;

	@Inject
	private ConfigManager configManager;

	@Inject
	private QuestQueueManager questQueueManager;

	ArrayList<Integer> taggedItems;

	ArrayList<Integer> taggedItemsForBank;

	int lastTickUpdated = 0;

	private final String RECOMMENDED_TAB_NAME = "Recommended items";

	public ArrayList<Integer> itemsToTag()
	{
		if (client.getTickCount() <= lastTickUpdated)
		{
			return taggedItems;
		}

		lastTickUpdated = client.getTickCount();

		return getItemsFromTabs();
	}

	private ArrayList<Integer> getItemsFromTabs()
	{
		ArrayList<BankTabItems> sortedItems = getPluginBankTagItemsForSections(true);

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
		// Queued quests take priority over the selected quest
		if (!questQueueManager.isEmpty())
		{
			return getQueueBankTagItems(onlyGetMissingItems);
		}

		ArrayList<BankTabItems> newList = new ArrayList<>();

		if (plugin.getSelectedQuest() == null)
		{
			return newList;
		}

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
				.filter(i -> (!onlyGetMissingItems || !i.checkWithAllContainers()) && i.shouldDisplayText(plugin.getClient()))
				.collect(Collectors.toList());
		}

		if (recommendedItems != null && !recommendedItems.isEmpty())
		{
			BankTabItems pluginItems = new BankTabItems(RECOMMENDED_TAB_NAME);
			// Here we specify getItems to avoid a double 'Recommended' title
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
						|| !i.checkWithAllContainers())
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
						|| !i.checkWithAllContainers())
						&& i.shouldDisplayText(plugin.getClient()))
					.collect(Collectors.toList());
			}

			BankTabItems pluginItems = new BankTabItems(questSection.getHeader());
			items.forEach(item -> getItemsFromRequirement(pluginItems.getItems(), item, item));
			recommendedItemsForSection.forEach(item -> getItemsFromRequirement(pluginItems.getRecommendedItems(), item, item));
			// We don't add the recommended items as they're already used
			if (items.size() > 0)
			{
				newList.add(pluginItems);
			}
		}

		// If none of the sections have anything in it, create a generic require items section
		if (newList.size() == 0 || (newList.size() == 1 && newList.get(0).getName().equals(RECOMMENDED_TAB_NAME)))
		{
			BankTabItems allRequiredItems = new BankTabItems("Required items");
			List<ItemRequirement> allRequired = plugin.getSelectedQuest().getItemRequirements();
			List<ItemRequirement> items;
			if (allRequired != null && allRequired.size() > 0)
			{
				items = allRequired.stream()
					.filter(Objects::nonNull)
					.map(ItemRequirement.class::cast)
					.filter(i -> (!onlyGetMissingItems
				   || !i.check(plugin.getClient()))
				   && i.shouldDisplayText(plugin.getClient()))
					.collect(Collectors.toList());

				items.forEach(item -> getItemsFromRequirement(allRequiredItems.getItems(), item, item));
			}
			newList.add(allRequiredItems);
		}

		return newList;
	}

	// One section with everything for the whole queue, then a section per quest
	private ArrayList<BankTabItems> getQueueBankTagItems(boolean onlyGetMissingItems)
	{
		ArrayList<BankTabItems> newList = new ArrayList<>();

		List<QuestHelper> quests = questQueueManager.getQueue().stream()
			.map(QuestHelperQuest::getQuestHelper)
			.filter(q -> !q.isCompleted())
			.collect(Collectors.toList());

		if (quests.isEmpty())
		{
			return newList;
		}

		Map<Integer, BankTabItem> combinedRequired = new LinkedHashMap<>();
		Map<Integer, BankTabItem> combinedRecommended = new LinkedHashMap<>();
		List<BankTabItems> questBlocks = new ArrayList<>();

		int position = 1;
		for (QuestHelper quest : quests)
		{
			List<BankTabItem> required = toBankTabItems(quest.getItemRequirements());
			List<BankTabItem> recommended = toBankTabItems(quest.getItemRecommended());

			required.forEach(item -> mergeInto(combinedRequired, item));
			recommended.forEach(item -> mergeInto(combinedRecommended, item));

			BankTabItems block = new BankTabItems(position + ". " + quest.getQuest().getName(),
				filterMissing(required, onlyGetMissingItems),
				filterMissing(recommended, onlyGetMissingItems));
			block.addNotes(missingSkillNotes(List.of(quest)));
			if (block.isEmpty())
			{
				// Still show it, otherwise the numbering skips this quest
				block.addNotes(List.of("Nothing needed from the bank"));
			}
			questBlocks.add(block);
			position++;
		}

		// The combined section would just be a copy of the only quest's section
		if (quests.size() > 1)
		{
			// No point showing something as recommended if another quest already requires it
			combinedRecommended.keySet().removeAll(combinedRequired.keySet());

			BankTabItems combined = new BankTabItems("All " + quests.size() + " queued quests",
				filterMissing(new ArrayList<>(combinedRequired.values()), onlyGetMissingItems),
				filterMissing(new ArrayList<>(combinedRecommended.values()), onlyGetMissingItems));
			combined.addNotes(missingSkillNotes(quests));
			newList.add(combined);
		}

		newList.addAll(questBlocks);
		return newList;
	}

	private List<BankTabItem> toBankTabItems(List<ItemRequirement> requirements)
	{
		List<BankTabItem> items = new ArrayList<>();
		if (requirements == null)
		{
			return items;
		}

		requirements.stream()
			.filter(Objects::nonNull)
			.filter(i -> i.shouldDisplayText(client))
			.forEach(i -> getItemsFromRequirement(items, i, i));
		return items;
	}

	private List<BankTabItem> filterMissing(List<BankTabItem> items, boolean onlyGetMissingItems)
	{
		if (!onlyGetMissingItems)
		{
			return items;
		}
		return items.stream()
			.filter(i -> !i.getItemRequirement().checkWithAllContainers())
			.collect(Collectors.toList());
	}

	// Consumed items add up across quests, but you only need one of things like a hammer
	private void mergeInto(Map<Integer, BankTabItem> combined, BankTabItem item)
	{
		Integer key = item.getDisplayID();
		BankTabItem existing = combined.get(key);
		if (existing == null)
		{
			combined.put(key, item);
			return;
		}

		ItemRequirement existingReq = existing.getItemRequirement();
		ItemRequirement newReq = item.getItemRequirement();
		int existingQuantity = existingReq.getQuantity();
		int newQuantity = newReq.getQuantity();

		int mergedQuantity;
		if (existingQuantity < 0 || newQuantity < 0)
		{
			// -1 is "some", can't add to that
			mergedQuantity = Math.max(existingQuantity, newQuantity);
		}
		else if (existingReq.isConsumedItem() && newReq.isConsumedItem())
		{
			mergedQuantity = existingQuantity + newQuantity;
		}
		else
		{
			mergedQuantity = Math.max(existingQuantity, newQuantity);
		}

		if (mergedQuantity == existingQuantity)
		{
			return;
		}

		combined.put(key, new BankTabItem(existingReq.quantity(mergedQuantity), key));
	}

	// Only keeps the highest level needed per skill
	private List<String> missingSkillNotes(List<QuestHelper> quests)
	{
		Map<Skill, SkillRequirement> highest = new EnumMap<>(Skill.class);
		for (QuestHelper quest : quests)
		{
			List<Requirement> general = quest.getGeneralRequirements();
			if (general == null)
			{
				continue;
			}
			for (Requirement requirement : general)
			{
				collectSkillRequirements(requirement, highest);
			}
		}

		List<String> notes = new ArrayList<>();
		for (SkillRequirement requirement : highest.values())
		{
			if (requirement.check(client))
			{
				continue;
			}
			int currentLevel = client.getRealSkillLevel(requirement.getSkill());
			notes.add("<col=ff0000>Need " + requirement.getDisplayText() + "</col> - you have " + currentLevel);
		}
		return notes;
	}

	private void collectSkillRequirements(Requirement requirement, Map<Skill, SkillRequirement> highest)
	{
		if (requirement instanceof SkillRequirement)
		{
			SkillRequirement skillRequirement = (SkillRequirement) requirement;
			SkillRequirement current = highest.get(skillRequirement.getSkill());
			if (current == null || skillRequirement.getRequiredLevel() > current.getRequiredLevel())
			{
				highest.put(skillRequirement.getSkill(), skillRequirement);
			}
		}
		else if (requirement instanceof ComplexRequirement)
		{
			ComplexRequirement complex = (ComplexRequirement) requirement;
			// No way to know which side of an OR the player will go for, so skip those
			if (complex.getLogicType() == LogicType.AND)
			{
				for (Requirement inner : complex.getRequirements())
				{
					collectSkillRequirements(inner, highest);
				}
			}
		}
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
						.filter(ItemRequirement::checkWithAllContainers)
						.findFirst()
						.orElse(itemsWhichPassReq.get(0).named(itemRequirements.getName()));

					getItemsFromRequirement(pluginItems, match, match);
				}
			}
		}
		else if (itemRequirement instanceof KeyringRequirement)
		{
			KeyringRequirement keyringRequirement = (KeyringRequirement) itemRequirement;
			var keyringCollection = keyringRequirement.getKeyringCollection();
			var fakeRequirement = keyringRequirement.copy();
			if (keyringCollection.hasKeyOnKeyRing(configManager))
			{
				fakeRequirement.addAlternates(ItemID.FAVOUR_KEY_RING);
				// NOTE: If the key is on your key ring _AND_ in your bank, we will still suggest using the key ring.
				pluginItems.add(new BankTabItem(fakeRequirement, ItemID.FAVOUR_KEY_RING));
			}
			else
			{
				pluginItems.add(makeBankTabItem(fakeRequirement));
			}
		}
		else
		{
			if (itemRequirement.getDisplayItemId() != null || !itemRequirement.getDisplayItemIds().contains(-1))
			{
				pluginItems.add(makeBankTabItem(realItem));
			}
		}
	}

	private BankTabItem makeBankTabItem(ItemRequirement item)
	{
		List<Integer> itemIds = item.getDisplayItemIds();
		Integer displayId = itemIds.stream()
				.filter(this::hasItemInBankOrPotionStorage)
				.findFirst()
				.orElse(item.getAllIds().stream()
						.filter(this::hasItemInBankOrPotionStorage)
						.findFirst()
						.orElse(item.getAllIds().get(0))
				);
		if (displayId == -1 && item.getDisplayItemId() != -1)
		{
			displayId = item.getDisplayItemId();
		}

		return new BankTabItem(item, displayId);
	}

	public boolean hasItemInBankOrPotionStorage(int itemID)
	{
		ItemRequirement tmpReq = new ItemRequirement("tmp", itemID);
		return tmpReq.checkWithAllContainers();
	}
}
