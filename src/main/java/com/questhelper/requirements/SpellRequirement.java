/*
 *
 *  * Copyright (c) 2021, Senmori
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice, this
 *  *    list of conditions and the following disclaimer.
 *  * 2. Redistributions in binary form must reproduce the above copyright notice,
 *  *    this list of conditions and the following disclaimer in the documentation
 *  *    and/or other materials provided with the distribution.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.questhelper.requirements;

import com.google.common.base.Predicates;
import com.questhelper.BankItems;
import com.questhelper.banktab.BankItemHolder;
import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.conditional.VarplayerCondition;
import com.questhelper.requirements.util.InventorySlots;
import com.questhelper.spells.MagicSpell;
import com.questhelper.spells.Rune;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.Skill;
import net.runelite.client.ui.overlay.components.LineComponent;

/*
 * LOGIC:
 *
 * Order of priority for requirements:
 * 1. Player has tablet
 * 2. Player has runes
 * 3. Player has staff
 *
 * We can ignore the requirements if the player has the tablet because those
 * have no requirements in order to be used.
 */

/**
 * Represents a spell that can be cast.
 * This will check if the user has the required magic level, quest requirements,
 * any extra items (i.e. unpowered orb for charge orb spells) and any extra requirements
 * that are added.
 * <br>
 * If the player has this spell's tablet, then the only requirement that is checked is the
 * quest requirement. This is because player's still cannot use a tablet whose
 * spell counterpart is locked behind quest progression.
 * <br>
 * This spell requirements prioritizes using tablets over runes/staves since
 * it is more compact and has less requirements.
 */
public class SpellRequirement extends ItemRequirement implements BankItemHolder
{
	@Getter
	private final MagicSpell spell;

	private ItemRequirement tabletRequirement = null;
	@Getter
	private int numberOfCasts;

	private final Map<Rune, Integer> runeCostMap;
	private final List<Requirement> requirements = new ArrayList<>();
	private final List<RuneRequirement> runeRequirements = new LinkedList<>();
	@Getter
	private final SkillRequirement skillRequirement;
	@Getter
	private final SpellbookRequirement spellbookRequirement;
	public SpellRequirement(MagicSpell spell, Map<Rune, Integer> runeCostMap, List<Requirement> requirements)
	{
		this(spell, 1, runeCostMap, requirements);
	}

	public SpellRequirement(MagicSpell spell, int numberOfCasts, Map<Rune, Integer> runeCostMap, List<Requirement> requirements)
	{
		super(spell.getName(), -1, numberOfCasts);
		this.spell = spell;
		this.numberOfCasts = numberOfCasts;
		this.runeCostMap = runeCostMap;
		this.requirements.addAll(requirements);
		this.skillRequirement = new SkillRequirement(Skill.MAGIC, spell.getRequiredMagicLevel());
		this.spellbookRequirement = new SpellbookRequirement(spell.getSpellbook());
		this.requirements.add(this.skillRequirement);
		this.requirements.add(this.spellbookRequirement);
		setQuantity(numberOfCasts);
		updateTooltip();
	}

	/**
	 * Add an additional {@link Requirement}.
	 *
	 * @param requirement requirement to add
	 */
	public void addRequirement(@Nonnull Requirement requirement)
	{
		if (requirement instanceof RuneRequirement)
		{
			runeRequirements.add((RuneRequirement) requirement);
		}
		else
		{
			requirements.add(requirement);
		}
	}

	/**
	 * @return a copy of all {@link Requirement} on this requirement
	 */
	public List<Requirement> getRequirements()
	{
		return new ArrayList<>(requirements);
	}

	/**
	 * Set the tablet's item id. If the item id is less than 0, the tablet will be removed
	 * and this requirement will no longer check for it.
	 *
	 * @param itemID the tablet's item id
	 */
	public void setTablet(int itemID)
	{
		if (itemID < 0)
		{
			this.tabletRequirement = null;
		}
		else
		{
			this.tabletRequirement = new ItemRequirement("", itemID, this.numberOfCasts);
		}
	}

	/**
	 * A convenience method to better indicate to not use a tablet for this spell requirement
	 */
	public void requireRunes()
	{
		setTablet(-1);
	}

	/**
	 * Set the number of casts this requirement should account for.
	 *
	 * @param numberOfCasts the number of casts
	 */
	public void setNumberOfCasts(int numberOfCasts)
	{
		this.numberOfCasts = numberOfCasts;
		setQuantity(numberOfCasts);
		runeRequirements.clear();
		for (Map.Entry<Rune, Integer> entry : runeCostMap.entrySet())
		{
			Rune rune  = entry.getKey();
			int costPerCast = entry.getValue();
			RuneRequirement runeRequirement = new RuneRequirement(rune, costPerCast, numberOfCasts);
			runeRequirements.add(runeRequirement);
		}
	}

	@Override
	public boolean isActualItem()
	{
		return true;
	}

	@Override
	public boolean showQuantity()
	{
		return true;
	}

	@Override
	public Integer getDisplayItemId()
	{
		return tabletRequirement != null ? tabletRequirement.getId() : null;
	}

	@Override
	public void setDisplayItemId(Integer displayItemId)
	{
		// Don't set so we use our requirements to determine what to show in the bank
	}

	@Override
	public List<LineComponent> getOverlayDisplayText(Client client)
	{
		updateInternalState(client, getItemRequirements(this.requirements));
		List<LineComponent> lines = new ArrayList<>();

		StringBuilder text = new StringBuilder();
		if (this.showQuantity())
		{
			text.append(this.numberOfCasts).append(" x ");
		}

		String name = spell.getName();
		if (tabletRequirement != null && tabletRequirement.check(client))
		{
			name = tabletRequirement.getName();
		}
		text.append(name);

		Color color = getColorConsideringBank(client, false, null);
		if (color == Color.RED && checkBank(client))
		{
			color = Color.WHITE;
		}
		lines.add(LineComponent.builder()
			.left(text.toString())
			.leftColor(color)
			.build()
		);
		return lines;
	}

	@Override
	public List<Integer> getAllIds()
	{
		List<Integer> ids = requirements.stream()
			.filter(ItemRequirement.class::isInstance)
			.map(ItemRequirement.class::cast)
			.map(ItemRequirement::getAllIds)
			.flatMap(Collection::stream)
			.collect(QuestUtil.collectToArrayList());
		List<Integer> runeIDs = runeRequirements.stream()
			.map(RuneRequirement::getAllIds)
			.flatMap(Collection::stream)
			.collect(Collectors.toList());
		ids.addAll(runeIDs);
		return ids;
	}

	@Override
	public boolean check(Client client)
	{
		return this.check(client, false, null);
	}

	@Override
	public boolean checkBank(Client client)
	{
		updateInternalState(client, getItemRequirements(this.requirements));
		if (tabletRequirement != null)
		{
			int tabletID = tabletRequirement.getId();
			if (InventorySlots.BANK.contains(client, i -> i.getId() == tabletID && i.getQuantity() >= this.numberOfCasts))
			{
				updateTabletRequirement(client);
				return true;
			}
		}
		boolean hasRunes = runeRequirements.stream().allMatch(req -> req.checkBank(client));
		boolean hasItems = requirements.stream().allMatch(req -> req.check(client));
		return hasRunes && hasItems;
	}

	@Override
	public Color getColorConsideringBank(Client client, boolean checkWithSlotRestrictions, Item[] bankItems)
	{
		if (tabletRequirement != null)
		{
			updateTabletRequirement(client);
			int tabletID = tabletRequirement.getId();
			int required = tabletRequirement.getQuantity();
			if (InventorySlots.INVENTORY_SLOTS.contains(client, i -> i.getId() == tabletID && i.getQuantity() >= required))
			{
				return Color.GREEN;
			}
			if (InventorySlots.BANK.contains(client, i -> i.getId() == tabletID && i.getQuantity() >= required))
			{
				return Color.WHITE;
			}
		}
		boolean hasOtherReqs = getNonItemRequirements(this.requirements).stream().allMatch(req -> req.check(client));
		if (!hasOtherReqs)
		{
			return Color.RED;
		}
		// No tablet (either set or found), we need all runes and other requirements
		boolean hasRunes = false;
		boolean hasItems = false;
		List<ItemRequirement> itemRequirements = getItemRequirements(this.requirements);
		updateInternalState(client, itemRequirements);
		hasRunes = runeRequirements.stream().allMatch(req -> hasItemAmount(client, req.getAllIds(), req.getRequiredAmount()));
		hasItems = itemRequirements.stream().allMatch(req -> hasItemAmount(client, req.getAllIds(), req.getQuantity()));
		if (hasRunes && hasItems)
		{
			return Color.GREEN;
		}
		if (bankItems != null)
		{
			hasRunes = runeRequirements.stream().allMatch(req -> req.checkBank(client));
			hasItems = itemRequirements.stream().allMatch(req -> {
				return InventorySlots.BANK.contains(client, i -> req.getAllIds().contains(i.getId()) && i.getQuantity() >= req.getQuantity());
			});
			if (hasRunes && hasItems)
			{
				return Color.WHITE;
			}
		}
		return Color.RED;
	}

	private boolean hasItemAmount(Client client, List<Integer> idList, int amount)
	{
		return InventorySlots.INVENTORY_SLOTS.contains(client, i -> idList.contains(i.getId()) && i.getQuantity() >= amount);
	}


	@Override
	public boolean check(Client client, boolean checkWithSlotRestrictions, Item[] items)
	{
		if (tabletRequirement != null)
		{
			int id = findFirstItemID(client, Collections.singletonList(tabletRequirement.getId()), this.numberOfCasts, checkWithSlotRestrictions, items);
			if (id >= 0)
			{
				updateTabletRequirement(client);
				return true;
			}
		}
		boolean hasItems, hasOther, hasRunes = false;
		List<ItemRequirement> itemRequirements = getItemRequirements(this.requirements);
		updateInternalState(client, itemRequirements);
		if (!itemRequirements.isEmpty())
		{
			hasItems = itemRequirements.stream().allMatch(req -> req.check(client, checkWithSlotRestrictions, items));
		}
		else
		{
			hasItems = true;
		}
		hasOther = getNonItemRequirements(this.requirements).stream().allMatch(req -> req.check(client));
		hasRunes = runeRequirements.stream().allMatch(req -> req.check(client, checkWithSlotRestrictions, items));
		return hasItems && hasOther && hasRunes;
	}

	@Override
	public List<ItemRequirement> getRequirements(Client client, boolean checkWithSlotRestrictions, Item[] bankItems)
	{
		if (tabletRequirement != null)
		{
			int tabletID = tabletRequirement.getId();
			if (hasItem(client, Collections.singletonList(tabletID), this.numberOfCasts, checkWithSlotRestrictions, bankItems))
			{
				return Collections.singletonList(tabletRequirement);
			}
		}
		List<ItemRequirement> requirements = runeRequirements.stream()
			.map(req -> req.getRequirements(client, checkWithSlotRestrictions, bankItems))
			.flatMap(Collection::stream)
			.collect(Collectors.toCollection(LinkedList::new));
		requirements.addAll(getItemRequirements(this.requirements));
		updateInternalState(client, requirements);
		return requirements;
	}

	@Nullable
	@Override
	public String getUpdatedTooltip(Client client, BankItems bankItems)
	{
		StringBuilder text = new StringBuilder();
		if (tabletRequirement != null)
		{
			AtomicInteger count = new AtomicInteger();
			getNonItemRequirements(this.requirements).stream()
				.filter(r -> r instanceof QuestRequirement)
				.map(QuestRequirement.class::cast)
				.filter(r -> r.check(client))
				.peek(q -> count.incrementAndGet())
				.forEach(q -> text.append(q.getDisplayText()));
			return count.get() > 0 ? text.toString() : null; // no requirements to use a tablet
		}
		getNonItemRequirements(this.requirements).stream()
			.filter(req -> !req.getDisplayText().isEmpty())
			.filter(req -> !req.check(client))
			.forEach(req -> text.append(req.getDisplayText()).append("\n"));
		return text.insert(0, "This spell requires: \n").toString();
	}

	private List<ItemRequirement> getItemRequirements(List<Requirement> requirements)
	{
		return requirements.stream()
			.filter(ItemRequirement.class::isInstance)
			.map(ItemRequirement.class::cast)
			.collect(Collectors.toList());
	}

	private List<Requirement> getNonItemRequirements(List<Requirement> requirements)
	{
		return requirements.stream()
			.filter(Predicates.not(ItemRequirement.class::isInstance))
			.collect(Collectors.toList());
	}

	private int findFirstItemID(Client client, List<Integer> itemIDList, int requiredAmount, boolean checkWithSlotRestrictions, Item[] items)
	{
		int remainder = requiredAmount;
		for (int id : itemIDList)
		{
			remainder -= (requiredAmount - getRequiredItemDifference(client, id, checkWithSlotRestrictions, items));
			if (remainder <= 0)
			{
				return id;
			}
		}
		return -1;
	}

	private boolean hasItem(Client client, List<Integer> ids, int amount, boolean checkWithSlotRestrictions, Item[] items)
	{
		return findFirstItemID(client, ids, amount, checkWithSlotRestrictions, items) >= 0;
	}

	private void updateInternalState(Client client, List<ItemRequirement> requirements)
	{
		updateItemRequirements(client, requirements);
	}

	private void updateTooltip()
	{
		setTooltip("This spell requires: ");
		getNonItemRequirements(this.requirements).stream()
			.forEach(req -> appendToTooltip(req.getDisplayText()));
	}

	private void updateItemRequirements(Client client, List<ItemRequirement> requirements)
	{
		for (ItemRequirement requirement : requirements)
		{
			if (requirement.getName() == null || requirement.getName().isEmpty())
			{
				requirement.setName(client.getItemDefinition(requirement.getId()).getName());
			}
		}
	}

	private void updateTabletRequirement(Client client)
	{
		if (tabletRequirement != null && (tabletRequirement.getName() == null || tabletRequirement.getName().isEmpty()))
		{
			ItemComposition tablet = client.getItemDefinition(tabletRequirement.getId());
			tabletRequirement = new ItemRequirement(tablet.getName(), tablet.getId(), this.numberOfCasts);
		}
	}
}
