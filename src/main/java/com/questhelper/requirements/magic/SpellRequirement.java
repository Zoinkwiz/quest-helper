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
import com.questhelper.ItemSearch;
import com.questhelper.QuestHelperConfig;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.banktab.BankItemHolder;
import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
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
 * have no requirements in order to be used (other than quests).
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
public class SpellRequirement extends ItemRequirement implements BankItemHolder
{
	@Getter
	private final MagicSpell spell;

	private ItemRequirement tabletRequirement = null;
	private ItemRequirement staffRequirement;
	private boolean useStaff = true;

	private int numberOfCasts;

	private final Map<Rune, Integer> runeCostMap;
	/** @return all {@link Requirement}s on this SpellRequirement */
	private final List<Requirement> requirements = new ArrayList<>();
	private final List<RuneRequirement> runeRequirements = new LinkedList<>();

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
		this.requirements.add(new SkillRequirement(Skill.MAGIC, spell.getRequiredMagicLevel()));
		this.requirements.add(new SpellbookRequirement(spell.getSpellbook()));
		setNumberOfCasts(numberOfCasts);
	}

	/**
	 * Add an additional {@link Requirement}.
	 * If this is an {@link ItemRequirement}, it's assumed that it's directly needed to cast this spell.
	 * Thus, it's quantity will be adjusted to match the number of casts this requirement has.
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

	/**
	 * Set the new staff item id this requirement should use.
	 *
	 * @param staffID the new staff item id.
	 *
	 * @throws UnsupportedOperationException if staff use is disabled.
	 */
	public void setStaff(int staffID)
	{
		if (!useStaff)
		{
			throw new UnsupportedOperationException("Cannot require a staff and then require no staff");
		}
		if (staffID < 0)
		{
			this.staffRequirement = null;
		}
		else
		{
			this.staffRequirement = new ItemRequirement("", staffID);
			doNotUseTablet();
		}
	}

	public void setStaff(ItemRequirement staff)
	{
		if (!useStaff)
		{
			throw new UnsupportedOperationException("Cannot require a staff and then require no staff");
		}
		this.staffRequirement = staff;
		doNotUseTablet();
	}

	/**
	 * @return true if this requirement currently has a staff and if staves are enabled.
	 */
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

	/**
	 * Set if this requirement should use staffs.
	 *
	 * @param useStaff true to use staffs.
	 *
	 * @throws UnsupportedOperationException if staff use is disabled while there is still a staff required
	 */
	public void setStaffUse(boolean useStaff)
	{
		if (!useStaff && staffRequirement != null)
		{
			throw new UnsupportedOperationException("Cannot require a staff and then require no staff");
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
	public List<LineComponent> getOverlayDisplayText(Client client, QuestHelperPlugin plugin)
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
			text.append(tabletRequirement.getName());
		}
		else
		{
			text.append(name);
		}
		Color color = getItemOverlayColor(client, plugin.getBankItems().getItems());
		// N x <Spell_Name>
		lines.add(LineComponent.builder()
			.left(text.toString())
			.leftColor(color)
			.build()
		);
		if (color == Color.WHITE)
		{
			lines.add(getInBankLine());
		}
		if (hasStaff())
		{
			int firstStaffID = ItemSearch.findFirstItem(client, staffRequirement.getAllIds(), staffRequirement.getQuantity());
			String staffName = staffRequirement.getName();
			if (staffName == null || staffName.isEmpty())
			{
				staffName = client.getItemDefinition(firstStaffID).getName();
			}
			Color staffColor = getStaffColor(client, plugin.getBankItems().getItems());
			// <Staff Name>
			lines.add(LineComponent.builder()
				.left(staffName)
				.leftColor(staffColor)
				.build());
			if (staffColor == Color.WHITE)
			{
				lines.add(getInBankLine());
			}
			// Add '(equipped)'
			staffColor = ItemSearch.hasItemEquipped(client, firstStaffID) ? Color.GREEN : Color.RED;
			lines.add(LineComponent.builder()
				.left("(equipped)")
				.leftColor(staffColor)
				.build());
		}
		return lines;
	}

	private Color getItemOverlayColor(Client client, Item[] bankItems)
	{
		//TODO: This is duplicated code from #getColorConsideringBank
		//TODO: Remove the need for duplicated code.
		boolean hasRunes, hasItems;
		List<ItemRequirement> itemRequirements = getItemRequirements(this.requirements);
		hasRunes = runeRequirements.stream().allMatch(req -> ItemSearch.hasItemsOnPlayer(client, req));
		hasItems = itemRequirements.stream().allMatch(req -> ItemSearch.hasItemsOnPlayer(client, req));
		if (hasRunes && hasItems)
		{
			return Color.GREEN;
		}
		// Don't use ItemSearch for RuneRequirement because RuneRequirement overrides checkBank
		hasRunes = runeRequirements.stream().allMatch(req -> req.checkCachedBank(bankItems));
		hasItems = itemRequirements.stream().allMatch(req -> ItemSearch.hasItemsInBank(req, bankItems));
		if (hasRunes && hasItems)
		{
			return Color.WHITE;
		}
		return Color.RED;
	}

	private Color getStaffColor(Client client, Item[] bankItems)
	{
		Color staffColor = Color.RED;
		if (ItemSearch.hasItemsOnPlayer(client, staffRequirement))
		{
			staffColor = Color.GREEN;
		}
		else if (ItemSearch.hasItemsInBank(client, staffRequirement) || ItemSearch.hasItemsInBank(staffRequirement, bankItems))
		{
			staffColor = Color.WHITE;
		}
		return staffColor;
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
		boolean hasStaff = !hasStaff();
		if (hasStaff())
		{
			int firstStaffID = ItemSearch.findFirstItem(client, staffRequirement.getAllIds(), staffRequirement.getQuantity());
			hasStaff = ItemSearch.hasItemAmountInBank(client, firstStaffID, staffRequirement.getQuantity());
		}
		return hasRunes && hasItems && hasStaff;
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
			return Color.RED; // abort early if they can't even cast the spell
		}
		boolean hasStaff = !hasStaff() || ItemSearch.hasItemsOnPlayer(client, staffRequirement);
		boolean hasRunes = false;
		boolean hasItems = false;
		List<ItemRequirement> itemRequirements = getItemRequirements(this.requirements);
		hasRunes = runeRequirements.stream().allMatch(req -> ItemSearch.hasItemsOnPlayer(client, req));
		hasItems = itemRequirements.stream().allMatch(req -> ItemSearch.hasItemsOnPlayer(client, req));
		if (hasRunes && hasItems && hasStaff)
		{
			return Color.GREEN;
		}
		hasRunes = runeRequirements.stream().allMatch(req -> req.checkBank(client) || req.checkCachedBank(bankItems)); // Don't use ItemSearch here because RuneRequirement overrides checkBank
		hasItems = itemRequirements.stream().allMatch(req -> ItemSearch.hasItemsInBank(client, req) || ItemSearch.hasItemsInBank(req, bankItems));
		hasStaff = !hasStaff() || ItemSearch.hasItemsInBank(client, staffRequirement) || ItemSearch.hasItemsInBank(staffRequirement, bankItems);
		if (hasRunes && hasItems && hasStaff)
		{
			return Color.WHITE;
		}
		updateInternalState(client, itemRequirements);
		return Color.RED;
	}


	@Override
	public boolean check(Client client, boolean checkWithSlotRestrictions, Item[] items)
	{
		if (tabletRequirement != null && ItemSearch.hasItemsAnywhere(client, tabletRequirement))
		{
			updateTabletRequirement(client);
			return true;
		}
		boolean hasStaff = true;
		if (hasStaff())
		{
			hasStaff = ItemSearch.hasItemsAnywhere(client, staffRequirement);
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
		return hasItems && hasOther && hasRunes && hasStaff;
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
		bankRequirements.stream()
			.filter(req -> StringUtils.isBlank(req.getName()))
			.forEach(req -> {
				int firstID = ItemSearch.findFirstItem(client, req.getAllIds(), req.getQuantity());
				if (firstID < 0) firstID = req.getId();
				req.setName(client.getItemDefinition(firstID).getName());
			});
		return bankRequirements;
	}

	private boolean hasStaff(Client client, ItemRequirement staves)
	{
		if (staves == null || staffRequirement != null || !useStaff)
		{
			return false;
		}
		boolean hasStaff = staves.getAllIds().stream().anyMatch(staffID -> ItemSearch.hasItemAmountAnywhere(client, staffID, 1));
		if (useStaff && hasStaff)
		{
			staffRequirement = staves;
		}
		return hasStaff;
	}

	@Nullable
	@Override
	public String getUpdatedTooltip(Client client)
	{
		StringBuilder text = new StringBuilder();
		if (tabletRequirement != null && ItemSearch.hasItemAnywhere(client, tabletRequirement.getId()))
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

	private void updateItemRequirements(Client client, List<ItemRequirement> requirements)
	{
		requirements.stream()
					.filter(req -> StringUtils.isBlank(req.getName()))
					.forEach(req -> req.setName(client.getItemDefinition(req.getId()).getName()));
		requirements.forEach(item -> item.setQuantity(this.numberOfCasts));
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
