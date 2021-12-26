/*
 * Copyright (c) 2021, Obasill <https://github.com/Obasill>
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
package com.questhelper.achievementdiaries.kandarin;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import com.questhelper.requirements.util.Spellbook;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;

@QuestDescriptor(
        quest = QuestHelperQuest.KANDARIN_ELITE
)

public class KandarinElite extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement dwarfSeed, seedDib, spade, rake, compost, harpoon, cookingGaunt, stamPot, caviar, runiteBar, magicLogs, hammer, chewedBone, tinderbox, axe, lawRune, astralRune, waterRune, combatGear;

	// unlisted item reqs
	ItemRequirement rawShark;

	Requirement notbarb5, notPickDwarf, not5Shark, notStamMix, notRuneHasta, notPyre, notTPCath, notHeal, notAtk, notDef, notCol, notDwarfGrowing, dwarfReady, dwarfGrowing;

	Requirement barbSmith, barbFire, barbHerb, familyCrest, lunarDip;

	//Quest steps
	QuestStep claimReward, tpCath, plantAndPickDwarf, moveToSeersRooftop, stamMix,
		runeHasta, pyre, barb5, barb52, barb5Heal, barb5Atk, barb5Def, barb5Col;

	NpcStep catchAndCook5Sharks;

	Zone bankRoof, barbUnder;

	ZoneRequirement inBankRoof, inBarbUnder;

	Requirement lunarBook;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doElite = new ConditionalStep(this, claimReward);
		doElite.addStep(notTPCath, tpCath);
		doElite.addStep(not5Shark, catchAndCook5Sharks);
		doElite.addStep(new Conditions(notStamMix, inBankRoof), stamMix);
		doElite.addStep(notStamMix, moveToSeersRooftop);
		doElite.addStep(notRuneHasta, runeHasta);
		doElite.addStep(notPyre, pyre);
		doElite.addStep(new Conditions(notbarb5, notHeal), barb5Heal);
		doElite.addStep(new Conditions(notbarb5, notAtk), barb5Atk);
		doElite.addStep(new Conditions(notbarb5, notDef), barb5Def);
		doElite.addStep(new Conditions(notbarb5, notCol), barb5Col);
		doElite.addStep(new Conditions(notbarb5, inBarbUnder), barb52);
		doElite.addStep(notbarb5, barb5);
		doElite.addStep(notPickDwarf, plantAndPickDwarf);

		return doElite;
	}

	public void setupRequirements()
	{
		notbarb5 = new VarplayerRequirement(1179, false, 5);
		notPickDwarf = new VarplayerRequirement(1179, false, 6);
		not5Shark = new VarplayerRequirement(1179, false, 7);
		notStamMix = new VarplayerRequirement(1179, false, 8);
		notRuneHasta = new VarplayerRequirement(1179, false, 9);
		notPyre = new VarplayerRequirement(1179, false, 10);
		notTPCath = new VarplayerRequirement(1179, false, 11);

		// dwarf weed
		dwarfReady = new VarbitRequirement(4774, 100);
		dwarfGrowing = new VarbitRequirement(4774, 96, Operation.GREATER_EQUAL);
		notDwarfGrowing = new VarbitRequirement(4774, 6, Operation.LESS_EQUAL);

		// BA levels
		notHeal = new VarbitRequirement(3255, 4, Operation.LESS_EQUAL);
		notAtk = new VarbitRequirement(3251, 4, Operation.LESS_EQUAL);
		notDef = new VarbitRequirement(3252, 4, Operation.LESS_EQUAL);
		notCol = new VarbitRequirement(3254, 4, Operation.LESS_EQUAL);

		dwarfSeed = new ItemRequirement("Dwarf weed seed", ItemID.DWARF_WEED_SEED).showConditioned(notPickDwarf);
		seedDib = new ItemRequirement("Seed dibber", ItemID.SEED_DIBBER).showConditioned(notPickDwarf);
		spade = new ItemRequirement("Spade", ItemID.SPADE).showConditioned(notPickDwarf);
		rake = new ItemRequirement("Rake", ItemID.RAKE).showConditioned(notPickDwarf);
		compost = new ItemRequirement("Any compost", ItemCollections.getCompost()).showConditioned(notPickDwarf);
		harpoon = new ItemRequirement("Harpoon", ItemID.HARPOON).showConditioned(not5Shark);
		cookingGaunt = new ItemRequirement("Cooking gauntlets", ItemID.COOKING_GAUNTLETS).showConditioned(not5Shark);
		stamPot = new ItemRequirement("Stamina potion (2)", ItemID.STAMINA_POTION2).showConditioned(notStamMix);
		caviar = new ItemRequirement("Caviar", ItemID.CAVIAR).showConditioned(notStamMix);
		runiteBar = new ItemRequirement("Runite bar", ItemID.RUNITE_BAR).showConditioned(notRuneHasta);
		magicLogs = new ItemRequirement("Magic logs", ItemID.MAGIC_LOGS).showConditioned(new Conditions(LogicType.OR, notRuneHasta, notPyre));
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notRuneHasta);
		chewedBone = new ItemRequirement("Chewed bones", ItemID.CHEWED_BONES).showConditioned(notPyre);
		chewedBone.setTooltip("These are a rare drop from mithril dragons");
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).showConditioned(notPyre);
		axe = new ItemRequirement("Any axe", ItemCollections.getAxes()).showConditioned(notPyre);
		lawRune = new ItemRequirement("Law runes", ItemID.LAW_RUNE).showConditioned(notTPCath);
		astralRune = new ItemRequirement("Astral runes", ItemID.ASTRAL_RUNE).showConditioned(notTPCath);
		waterRune = new ItemRequirement("Water runes", ItemID.WATER_RUNE).showConditioned(notTPCath);
		rawShark = new ItemRequirement("Raw shark", ItemID.RAW_SHARK).showConditioned(not5Shark);

		combatGear = new ItemRequirement("Combat gear", -1, -1).showConditioned(notbarb5);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		lunarBook = new SpellbookRequirement(Spellbook.LUNAR);

		setupGeneralRequirements();

		inBankRoof = new ZoneRequirement(bankRoof);
		inBarbUnder = new ZoneRequirement(barbUnder);
	}

	private void setupGeneralRequirements()
	{
		// TODO find a way to track barb training
		barbHerb = new ItemRequirement("Completed Barbarian herblore", 1, -1);
		barbFire = new ItemRequirement("Unlocked the Ancient Caverns through Barbarian Firemaking", 1, -1);
		barbSmith = new ItemRequirement("Completed Barbarian Smithing", 1, -1);
		familyCrest = new QuestRequirement(QuestHelperQuest.FAMILY_CREST, QuestState.FINISHED);
		lunarDip = new QuestRequirement(QuestHelperQuest.LUNAR_DIPLOMACY, QuestState.FINISHED);
	}

	public void loadZones()
	{
		bankRoof = new Zone(new WorldPoint(2721, 3495, 3), new WorldPoint(2730, 3490, 3));
		barbUnder = new Zone(new WorldPoint(2572, 53202, 0), new WorldPoint(2614, 5258, 0));
	}

	public void setupSteps()
	{
		tpCath = new DetailedQuestStep(this, "Teleport to Catherby.",
			lunarBook, waterRune.quantity(10), astralRune.quantity(3), lawRune.quantity(3));
		plantAndPickDwarf = new ObjectStep(this, NullObjectID.NULL_8151, new WorldPoint(2814, 3464, 0),
			"Plant and harvest the dwarf weed from the Catherby patch.", rake, dwarfSeed, seedDib);
		catchAndCook5Sharks = new NpcStep(this, NpcID.FISHING_SPOT_1519, new WorldPoint(2837, 3431, 0),
			"Catch 5 sharks in Catherby, then successfully cook 5 on the ranged in Catherby.", harpoon,
			cookingGaunt.equipped());
		catchAndCook5Sharks.addAlternateNpcs(NpcID.FISHING_SPOT_1520);
		moveToSeersRooftop = new ObjectStep(this, ObjectID.WALL_14927, new WorldPoint(2729, 3489, 0),
			"Climb on top of Seers' Bank.", stamPot, caviar);
		stamMix = new ItemStep(this, "Create a stamina mix.", stamPot.highlighted(), caviar.highlighted());
		runeHasta = new ObjectStep(this, ObjectID.BARBARIAN_ANVIL, new WorldPoint(2502, 3485, 0),
			"Smith an rune hasta on the barbarian anvil near Otto.", runiteBar, magicLogs, hammer);
		runeHasta.addIcon(ItemID.RUNITE_BAR);
		pyre = new ObjectStep(this, ObjectID.PYRE_SITE, new WorldPoint(2519, 3519, 0),
			"Construct a pyre ship from magic logs.", magicLogs, chewedBone, tinderbox, axe);
		barb5Heal = new DetailedQuestStep(this, "Get to level 5 in the healer role at Barbarian Assault.");
		barb5Atk = new DetailedQuestStep(this, "Get to level 5 in the attacker role at Barbarian Assault.");
		barb5Def = new DetailedQuestStep(this, "Get to level 5 in the defender role at Barbarian Assault.");
		barb5Col = new DetailedQuestStep(this, "Get to level 5 in the collector role at Barbarian Assault.");
		barb5 = new ObjectStep(this, ObjectID.BLACKBOARD_20134, new WorldPoint(2535, 3569, 0),
			"Click one of the blackboards around Barbarian Assault!");
		barb52 = new ObjectStep(this, ObjectID.BLACKBOARD_20134, new WorldPoint(2587, 5264, 0),
			"Click one of the blackboards around Barbarian Assault!");
		barb5.addSubSteps(barb52);

		claimReward = new NpcStep(this, NpcID.THE_WEDGE, new WorldPoint(2760, 3476, 0),
			"Talk to the 'Wedge' in front of Camelot Castle to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(dwarfSeed, seedDib, spade, rake, compost, harpoon,
			cookingGaunt, stamPot, caviar, runiteBar, magicLogs.quantity(2), hammer, chewedBone, tinderbox, axe,
			lawRune.quantity(3), astralRune.quantity(3), waterRune.quantity(10), combatGear);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		setupGeneralRequirements();

		ArrayList<Requirement> req = new ArrayList<>();
		req.add(barbHerb);
		req.add(barbFire);
		req.add(barbSmith);
		req.add(familyCrest);
		req.add(lunarDip);
		req.add(new SkillRequirement(Skill.AGILITY, 60, true));
		req.add(new SkillRequirement(Skill.COOKING, 80));
		req.add(new SkillRequirement(Skill.CRAFTING, 85));
		req.add(new SkillRequirement(Skill.FARMING, 79));
		req.add(new SkillRequirement(Skill.FIREMAKING, 85));
		req.add(new SkillRequirement(Skill.FISHING, 76));
		req.add(new SkillRequirement(Skill.HERBLORE, 86));
		req.add(new SkillRequirement(Skill.MAGIC, 87));
		req.add(new SkillRequirement(Skill.SMITHING, 90));

		return req;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Mithril Dragons (level 304) for chewed bones");
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("Kandarin headgear (4)", ItemID.KANDARIN_HEADGEAR_4, 1),
				new ItemReward("50,000 Exp. Lamp (Any skill over 70)", ItemID.ANTIQUE_LAMP, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Thormac will enchant battlestaves for 20,000 coins each"),
				new UnlockReward("The Flax keeper will exchange 250 noted flax for 250 noted bow strings daily"),
				new UnlockReward("15% increased chance to save a harvest life from the Catherby herb patch"),
				new UnlockReward("The first 200 Coal placed into coal trucks every day will be automatically transported to your bank"),
				new UnlockReward("Otto Godblessed will turn a Zamorakian spear into Zamorakian hasta for 150,000 Coins"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails teleCathSteps = new PanelDetails("Teleport to Catherby", Collections.singletonList(tpCath),
			new SkillRequirement(Skill.MAGIC, 87, true),
			lunarDip, waterRune.quantity(10), lawRune.quantity(3), astralRune.quantity(3));
		teleCathSteps.setDisplayCondition(notTPCath);
		allSteps.add(teleCathSteps);

		PanelDetails dwarfWeedSteps = new PanelDetails("Dwarf Weed in Catherby", Collections.singletonList(plantAndPickDwarf),
			new SkillRequirement(Skill.FARMING, 79, true),
			dwarfSeed, seedDib, rake, spade, compost);
		dwarfWeedSteps.setDisplayCondition(notPickDwarf);
		allSteps.add(dwarfWeedSteps);

		PanelDetails catchSharkSteps = new PanelDetails("5 Sharks Caught and Cooked in Catherby", Collections.singletonList(catchAndCook5Sharks),
			new SkillRequirement(Skill.FISHING, 76, true),
			new SkillRequirement(Skill.COOKING, 80, true),
			familyCrest, harpoon, cookingGaunt);
		catchSharkSteps.setDisplayCondition(not5Shark);
		allSteps.add(catchSharkSteps);

		PanelDetails staminaSteps = new PanelDetails("Stamina Mix on the Bank", Arrays.asList(moveToSeersRooftop, stamMix),
			new SkillRequirement(Skill.HERBLORE, 86, true),
			new SkillRequirement(Skill.AGILITY, 60, true),
			barbHerb, stamPot, caviar);
		staminaSteps.setDisplayCondition(notStamMix);
		allSteps.add(staminaSteps);

		PanelDetails smithRuneHastaSteps = new PanelDetails("Smith Rune Hasta", Collections.singletonList(runeHasta),
			new SkillRequirement(Skill.SMITHING, 90, true),
			barbSmith, magicLogs.quantity(1), runiteBar, hammer);
		smithRuneHastaSteps.setDisplayCondition(notRuneHasta);
		allSteps.add(smithRuneHastaSteps);

		PanelDetails magicPyreSteps = new PanelDetails("Magic Pyre Ship", Collections.singletonList(pyre),
			new SkillRequirement(Skill.FIREMAKING, 85, true),
			new SkillRequirement(Skill.CRAFTING, 85, true),
			barbFire, axe, tinderbox, magicLogs.quantity(1), chewedBone);
		magicPyreSteps.setDisplayCondition(notPyre);
		allSteps.add(magicPyreSteps);

		PanelDetails level5RolesSteps = new PanelDetails("Level 5 each Role", Arrays.asList(barb5Heal, barb5Atk, barb5Def, barb5Col, barb5));
		level5RolesSteps.setDisplayCondition(notbarb5);
		allSteps.add(level5RolesSteps);

		PanelDetails finishingOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishingOffSteps);

		return allSteps;
	}
}
