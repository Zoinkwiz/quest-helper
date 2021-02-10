/*
 *
 *  * Copyright (c) 2021
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
package com.questhelper.requirements.magic;

import com.google.common.base.Predicates;
import com.questhelper.BankItems;
import com.questhelper.ItemSearch;
import com.questhelper.QuestHelperConfig;
import com.questhelper.banktab.BankItemHolder;
import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.InventorySlots;
import com.questhelper.spells.MagicSpell;
import com.questhelper.spells.Rune;
import com.questhelper.spells.Staff;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.Skill;
import net.runelite.client.ui.overlay.components.LineComponent;
import org.apache.commons.lang3.StringUtils;

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
@Slf4j
@Getter
public class SpellRequirement extends ItemRequirement implements BankItemHolder
{
	private final MagicSpell spell;

	private ItemRequirement tabletRequirement = null;
	private ItemRequirement staffRequirement;
	private boolean useStaff = true;

	private int numberOfCasts;

	private final Map<Rune, Integer> runeCostMap;
	/** @return all {@link Requirement}s on this SpellRequirement */
	private final List<Requirement> requirements = new ArrayList<>();
	private final List<RuneRequirement> runeRequirements = new LinkedList<>();
	private final SkillRequirement skillRequirement;
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
		setNumberOfCasts(numberOfCasts);
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

	public void setStaff(int staffID)
	{
		if (!useStaff)
		{
			return;
		}
		if (staffID < 0)
		{
			this.staffRequirement = null;
		}
		else
		{
			this.staffRequirement = new ItemRequirement("", staffID);
		}
	}

	public boolean hasStaff()
	{
		return staffRequirement != null && useStaff;
	}

	/**
	 * A convenience method to better indicate to not use a tablet for this spell requirement
	 */
	public void doNotUseTablet()
	{
		setTablet(-1);
	}

	public void setStaffUse(boolean useStaff)
	{
		if (!useStaff && staffRequirement != null)
		{
			throw new UnsupportedOperationException("Cannot require a staff and then require no staff: " + this);
		}
		this.useStaff = useStaff;
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
		if (tabletRequirement != null)
		{
			tabletRequirement.setQuantity(this.numberOfCasts);
		}
		getItemRequirements(this.requirements).forEach(item -> item.setQuantity(this.numberOfCasts));
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
		return tabletRequirement != null ? tabletRequirement.getId() : (staffRequirement != null ? staffRequirement.getId() : null);
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
		if (tabletRequirement != null && tabletRequirement.getId() >= 0)
		{
			ids.add(tabletRequirement.getId());
		}
		if (staffRequirement != null && staffRequirement.getId() >= 0)
		{
			ids.add(staffRequirement.getId());
		}
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
		if (tabletRequirement != null && ItemSearch.hasItemAmountInBank(client, tabletRequirement.getId(), this.numberOfCasts))
		{
			return true;
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
			if (ItemSearch.hasItemAmountOnPlayer(client, tabletID, required))
			{
				return Color.GREEN;
			}
			if (ItemSearch.hasItemAmountInBank(client, tabletID, required))
			{
				return Color.WHITE;
			}
		}
		boolean hasOtherReqs = getNonItemRequirements(this.requirements).stream().allMatch(req -> req.check(client));
		if (!hasOtherReqs)
		{
			return Color.RED;
		}
		boolean hasStaff = staffRequirement != null;
		if (hasStaff && !staffRequirement.check(client, checkWithSlotRestrictions, bankItems))
		{
			return Color.RED; // required staff is not present
		}
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
			hasItems = itemRequirements.stream().allMatch(req -> ItemSearch.hasItemsInBank(client, req));
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
		if (tabletRequirement != null && ItemSearch.hasItemsAnywhere(client, tabletRequirement))
		{
			updateTabletRequirement(client);
			return true;
		}
		if (staffRequirement != null && ItemSearch.hasItemsAnywhere(client, staffRequirement))
		{
			return true;
		}
		boolean hasItems, hasOther, hasRunes;
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
	public List<ItemRequirement> getRequirements(Client client, QuestHelperConfig config)
	{
		updateInternalState(client, getItemRequirements(this.requirements));
		if (tabletRequirement != null && ItemSearch.hasItemAmountAnywhere(client, tabletRequirement.getId(), this.numberOfCasts))
		{
			return Collections.singletonList(tabletRequirement);
		}
		List<ItemRequirement> bankRequirements = new LinkedList<>();
		Map<Rune, ItemRequirement> runeItemRequirements = new HashMap<>();
		if (staffRequirement != null)
		{
			bankRequirements.add(staffRequirement);
		}
		for (RuneRequirement rune : runeRequirements)
		{
			if (runeItemRequirements.containsKey(rune.getRune()))
			{
				continue;
			}
			Rune currentRune = rune.getRune();
			ItemRequirement staves = rune.getStaffItemRequirement();
			ItemRequirement runeItem = rune.getRuneItemRequirement();

			boolean hasRunes = ItemSearch.hasItemsAnywhere(client, runeItem);
			boolean hasStaves = hasStaff(client, staves);

			ItemRequirement toAdd = config.bankFilterSpellPreference().getPreference(rune, () -> hasRunes, () -> hasStaves);

			boolean itemIsRune = toAdd.getAllIds().stream().allMatch(Rune::isRuneItem);
			if (staffRequirement != null && itemIsRune && currentRune.getStaff() != Staff.UNKNOWN)
			{
				// there is a staff present, can it replace the current rune?
				int staffID = ItemSearch.findFirstItem(client, staffRequirement.getAllIds(), 1);
				Staff requiredStaff = Staff.getByItemID(staffID);
				boolean isSourceOf = requiredStaff.isSourceOf(currentRune);
				if (!isSourceOf)
				{
					runeItemRequirements.put(currentRune, toAdd);
				}
			}
			else
			{
				runeItemRequirements.put(currentRune, toAdd);
			}
		}

		bankRequirements.addAll(runeItemRequirements.values());
		bankRequirements.addAll(getItemRequirements(this.requirements));
		updateInternalState(client, bankRequirements);
		return bankRequirements;
	}

	private boolean hasStaff(Client client, ItemRequirement staves)
	{
		if (staves == null || staffRequirement != null)
		{
			return false;
		}
		boolean hasStaff = ItemSearch.hasItemsAnywhere(client, staves);
		if (useStaff && hasStaff)
		{
			staffRequirement = staves;
		}
		return hasStaff;
	}

	@Nullable
	@Override
	public String getUpdatedTooltip(Client client, BankItems bankItems)
	{
		StringBuilder text = new StringBuilder();
		if (tabletRequirement != null && tabletRequirement.check(client, false, bankItems.getItems()))
		{ // only show tooltip for tablet if they actually have it
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
		if (text.length() <= 0)
		{
			return text.toString();
		}
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

	private void updateInternalState(Client client, List<ItemRequirement> requirements)
	{
		updateItemRequirements(client, requirements);
		if (staffRequirement != null && StringUtils.isBlank(staffRequirement.getName()))
		{
			staffRequirement.setName(client.getItemDefinition(staffRequirement.getId()).getName());
		}
	}

	private void updateTooltip()
	{
		setTooltip("This spell requires: ");
		getNonItemRequirements(this.requirements).forEach(req -> appendToTooltip(req.getDisplayText()));
	}

	private void updateItemRequirements(Client client, List<ItemRequirement> requirements)
	{
		requirements.stream()
					.filter(req -> StringUtils.isBlank(req.getName()))
					.forEach(req -> req.setName(client.getItemDefinition(req.getId()).getName()));
	}

	private void updateTabletRequirement(Client client)
	{
		if (tabletRequirement != null && StringUtils.isBlank(tabletRequirement.getName()))
		{
			ItemComposition tablet = client.getItemDefinition(tabletRequirement.getId());
			tabletRequirement = new ItemRequirement(tablet.getName(), tablet.getId(), this.numberOfCasts);
		}
	}
}
