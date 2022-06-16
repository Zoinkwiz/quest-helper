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
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.requirements.conditional.Conditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_MAGE_ARENA_II
)
public class TheMageArenaII extends BasicQuestHelper
{
	ItemRequirement zamorakStaff, guthixStaff, saradominStaff, runesForCasts, magicCombatGear, knife, brews, restores
		, food, recoils, enchantedSymbol, justicarsHand, demonsHeart, entRoots, godCape;

	Requirement inCavern, givenHand, givenHeart, givenRoots;

	QuestStep enterCavern, talkToKolodion, locateFollowerSara, locateFollowerGuthix, locateFollowerZammy,
		enterCavernWithHand, enterCavernWithHeart, enterCavernWithRoots, giveKolodionHeart, giveKolodionHand,
		giveKolodionRoots, locateAndKillMinions, enterCavernAfterMinions, talkToKolodionAfterMinions,
		useGodCapeOnKolidion;

	Zone cavern;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep goTalkToKolodion = new ConditionalStep(this, enterCavern);
		goTalkToKolodion.addStep(inCavern, talkToKolodion);
		steps.put(0, goTalkToKolodion);
		steps.put(1, goTalkToKolodion);

		ConditionalStep goKillMinions = new ConditionalStep(this, locateFollowerSara);
		goKillMinions.addStep(new Conditions(inCavern, givenRoots, givenHand, givenHeart), talkToKolodionAfterMinions);
		goKillMinions.addStep(new Conditions(givenRoots, givenHand, givenHeart), enterCavernAfterMinions);

		goKillMinions.addStep(new Conditions(inCavern, demonsHeart), giveKolodionHeart);
		goKillMinions.addStep(demonsHeart, enterCavernWithHeart);
		goKillMinions.addStep(new Conditions(givenHand, givenRoots), locateFollowerZammy);

		goKillMinions.addStep(new Conditions(inCavern, entRoots.alsoCheckBank(questBank)), giveKolodionRoots);
		goKillMinions.addStep(entRoots.alsoCheckBank(questBank), enterCavernWithRoots);
		goKillMinions.addStep(givenHand, locateFollowerGuthix);

		goKillMinions.addStep(new Conditions(inCavern, justicarsHand.alsoCheckBank(questBank)), giveKolodionHand);
		goKillMinions.addStep(justicarsHand.alsoCheckBank(questBank), enterCavernWithHand);
		steps.put(2, goKillMinions);

		ConditionalStep goImbueCape = new ConditionalStep(this, enterCavernAfterMinions);
		goImbueCape.addStep(inCavern, useGodCapeOnKolidion);
		steps.put(3, goImbueCape);

		return steps;
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

	public void setupConditions()
	{
		inCavern = new ZoneRequirement(cavern);

		givenHand = new VarbitRequirement(6063, 1);
		givenRoots = new VarbitRequirement(6064, 1);
		givenHeart = new VarbitRequirement(6065, 1);
		// Handed in hand:
		// 6066, 6063 0->1
		// 6066 varies 0->7 (3 bits)
	}

	public void setupSteps()
	{
		enterCavern = new ObjectStep(this, ObjectID.LEVER_5959, new WorldPoint(3090, 3956, 0), "Pull the lever in the" +
			" building north of the Mage Arena. This is IN THE WILDERNESS, so don't bring anything you don't want to " +
			"lose.", knife);
		talkToKolodion = new NpcStep(this, NpcID.KOLODION, new WorldPoint(2539, 4716, 0), "Talk to Kolodion.");
		talkToKolodion.addDialogSteps("Are there any more challenges available?", "Great, I've been waiting for an " +
			"improvement!");

		locateFollowerSara = new MageArenaBossStep(this, saradominStaff, "Saradomin", "If he fires a blue wave at " +
			"you, move off your tile to avoid it. If you don't, he will pull you into melee distance.",
			enchantedSymbol, food);
		locateFollowerSara.addDialogStep("Saradomin");
		locateFollowerGuthix = new MageArenaBossStep(this, guthixStaff, "Guthix", "If he spawns green orbs, destroy " +
			"them to stop them healing him.",	enchantedSymbol, food);
		locateFollowerGuthix.addDialogStep("Guthix");
		locateFollowerZammy = new MageArenaBossStep(this, zamorakStaff, "Zamorak", "If he fires an energy ball at " +
			"you, move away away from the boss to reduce the damage you take.",
			enchantedSymbol, food);
		locateFollowerZammy.addDialogStep("Zamorak");

		enterCavernWithHand = new ObjectStep(this, ObjectID.LEVER_5959, new WorldPoint(3090, 3956, 0), "Return with" +
			" the hand to Kolodion.", justicarsHand, knife);
		enterCavernWithRoots = new ObjectStep(this, ObjectID.LEVER_5959, new WorldPoint(3090, 3956, 0), "Return with" +
			" the roots to Kolodion.", entRoots, knife);
		enterCavernWithHeart = new ObjectStep(this, ObjectID.LEVER_5959, new WorldPoint(3090, 3956, 0), "Return with" +
			" the heart to Kolodion.", demonsHeart, knife);

		giveKolodionHand = new NpcStep(this, NpcID.KOLODION, new WorldPoint(2539, 4716, 0), "Return with" +
			" the hand to Kolodion.", justicarsHand);
		giveKolodionRoots = new NpcStep(this, NpcID.KOLODION, new WorldPoint(2539, 4716, 0), "Return with" +
			" the roots to Kolodion.", entRoots);
		giveKolodionHeart = new NpcStep(this, NpcID.KOLODION, new WorldPoint(2539, 4716, 0), "Return with" +
			" the heart to Kolodion.", demonsHeart);

		enterCavernAfterMinions = new ObjectStep(this, ObjectID.LEVER_5959, new WorldPoint(3090, 3956, 0), "Pull the " +
			"lever in the building north of the Mage Arena. This is IN THE WILDERNESS, so don't bring anything you don't want to lose.", knife);
		talkToKolodionAfterMinions = new NpcStep(this, NpcID.KOLODION, new WorldPoint(2539, 4716, 0), "Talk to Kolodion.");

		locateAndKillMinions = new DetailedQuestStep(this, "Use the Enchanted Symbol to locate the 3 bosses. It's " +
			"recommended to find where a boss is with just food and the symbol, then once you have the location gear " +
			"up and come fight it. Fight bosses with their respective god staff and spell, food, potions, and armour " +
			"you're willing to risk. Protect from Magic, and once you've killed them take their drop to Kolodion.");
		locateAndKillMinions.addSubSteps(locateFollowerGuthix, locateFollowerZammy, locateFollowerSara,
			enterCavernWithRoots, enterCavernWithHeart, enterCavernWithHand, giveKolodionHand, giveKolodionRoots,
			giveKolodionHeart, enterCavernAfterMinions, talkToKolodionAfterMinions);

		useGodCapeOnKolidion = new NpcStep(this, NpcID.KOLODION, new WorldPoint(2539, 4716, 0), "Use a god cape of " +
			"your choice on Kolodion to have it imbued.", godCape);
		useGodCapeOnKolidion.addIcon(ItemID.GUTHIX_CAPE);
		useGodCapeOnKolidion.addDialogStep("Yes.");

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
		return Collections.singletonList("This miniquest is in deep Wilderness. Don't bring anything you're not " +
			"willing to risk! It's recommended to turn off player attack options to avoid potentially getting " +
			"skulled.");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.MAGIC, 75));
		reqs.add(new QuestRequirement(QuestHelperQuest.THE_MAGE_ARENA, QuestState.FINISHED));
		reqs.add(new ItemRequirement("Unlocked all 3 god spells", -1, -1));
		return reqs;
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Ability to upgrade your God Cape"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Upgrading the God Cape", Arrays.asList(enterCavern,
			talkToKolodion, locateAndKillMinions), knife, saradominStaff, guthixStaff, zamorakStaff, runesForCasts));
		return allSteps;
	}
}
