/*
 * Copyright (c) 2021, Zoinkwiz
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
package com.questhelper.quests.themagearenaii;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.QuestState;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.MA2_LOCATOR
)
public class MA2Locator extends ComplexStateQuestHelper
{
	ItemRequirement zamorakStaff, guthixStaff, saradominStaff, runesForCasts, magicCombatGear, knife, brews, restores
		, food, recoils, enchantedSymbol, justicarsHand, demonsHeart, entRoots, godCape;

	QuestStep locateFollowerSara;

	Zone cavern;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		return locateFollowerSara;
	}

	@Override
	public void setupRequirements()
	{
		zamorakStaff = new ItemRequirement("Zamorak staff", ItemID.ZAMORAK_STAFF);
		zamorakStaff.setTooltip("You can buy one from the Chamber Guardian in the Mage Arena Cavern for 80k");
		guthixStaff = new ItemRequirement("Guthix staff", ItemID.GUTHIX_STAFF);
		guthixStaff.setTooltip("You can buy one from the Chamber Guardian in the Mage Arena Cavern for 80k");
		saradominStaff = new ItemRequirement("Saradomin staff", ItemID.SARADOMIN_STAFF);
		saradominStaff.setTooltip("You can buy one from the Chamber Guardian in the Mage Arena Cavern for 80k");
		runesForCasts = new ItemRequirements("Runes for 50+ casts of god spells",
			new ItemRequirement("Blood runes", ItemID.BLOOD_RUNE, -1),
			new ItemRequirement("Air runes", ItemID.AIR_RUNE, -1),
			new ItemRequirement("Fire runes", ItemID.FIRE_RUNE, -1));
		magicCombatGear = new ItemRequirement("Magic combat gear", -1, 1);
		magicCombatGear.setDisplayItemId(BankSlotIcons.getMagicCombatGear());
		knife = new ItemRequirement("Knife or sharp weapon to cut through a web", ItemID.KNIFE);
		brews =  new ItemRequirement("Saradomin brews", ItemCollections.SARADOMIN_BREWS, -1);
		restores = new ItemRequirement("Super restores", ItemCollections.SUPER_RESTORE_POTIONS, -1);
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		recoils = new ItemRequirement("Rings of recoil", ItemID.RING_OF_RECOIL);

		enchantedSymbol = new ItemRequirement("Enchanted symbol", ItemID.ENCHANTED_SYMBOL);
		enchantedSymbol.setTooltip("You can get another from Kolodion in the Mage Arena Cavern.");
		justicarsHand = new ItemRequirement("Justicar's hand", ItemID.JUSTICIARS_HAND);
		demonsHeart = new ItemRequirement("Demon's heart", ItemID.DEMONS_HEART);
		entRoots = new ItemRequirement("Ent's roots", ItemID.ENTS_ROOTS);

		godCape = new ItemRequirement("God cape", ItemID.ZAMORAK_CAPE);
		godCape.addAlternates(ItemID.GUTHIX_CAPE, ItemID.SARADOMIN_CAPE);
		godCape.setHighlightInInventory(true);
	}

	public void loadZones()
	{
		cavern = new Zone(new WorldPoint(2529, 4709, 0), new WorldPoint(2550, 4725, 0));
	}

	public void setupSteps()
	{
		locateFollowerSara = new MageArenaBossStep(this, saradominStaff, "desired", "",
			enchantedSymbol, food);

	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(knife, zamorakStaff, guthixStaff, saradominStaff, runesForCasts);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(magicCombatGear, brews, restores, food, recoils);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Porazdir (level 235)", "Justiciar Zachariah (level 348)", "Derwen " +
			"(level 235)");
	}

	@Override
	public List<String> getNotes()
	{
		return Collections.singletonList("This is in deep Wilderness. Don't bring anything you're not " +
			"willing to risk! It's recommended to turn off player attack options to avoid potentially getting " +
			"skulled.");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> reqs = new ArrayList<>();
		reqs.add(new QuestRequirement(QuestHelperQuest.THE_MAGE_ARENA_II, QuestState.FINISHED));
		return reqs;
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Upgrading the God Cape", Collections.singletonList(locateFollowerSara),
			knife, saradominStaff, guthixStaff, zamorakStaff, runesForCasts));
		return allSteps;
	}
}
