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
package com.questhelper.achievementdiaries.morytania;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.MORYTANIA_ELITE
)
public class MorytaniaElite extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement combatGear, magicRedwoodPyreLogs, shadeRemains, tinderbox, earthRune, astralRune, natureRune,
		blackLeather, needle, thread, barrowsSet, ahrimSet, karilSet, guthanSet, veracSet, dharokSet, toragSet;

	// Items recommended
	ItemRequirement food, barrowsTab, slayerRing, ectophial, mortTP, spade;

	// Quests required
	Requirement inAidOfTheMyreque, shadesOfMorton, lunarDiplomacy, lunarBook, bareHandBarb;

	Requirement notBareHandShark, notCremateShade, notFertilizeHerb, notCraftBlackDhideBody, notAbyssalDemon,
		notBarrowsChest;

	QuestStep claimReward, bareHandShark, fertilizeHerb, craftBlackDhideBody, abyssalDemon,
		barrowsChest, moveToCanifisBank, moveToSlayer2, moveToSlayer3, moveToBarrows;

	ObjectStep cremateShade;

	Zone canifisBank, slayer2, slayer3, barrows;

	ZoneRequirement inCanifisBank, inSlayer2, inSlayer3, inBarrows;

	ConditionalStep bareHandSharkTask, cremateShadeTask, fertilizeHerbTask, craftBlackDhideBodyTask, abyssalDemonTask,
		barrowsChestTask;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doElite = new ConditionalStep(this, claimReward);

		bareHandSharkTask = new ConditionalStep(this, bareHandShark);
		doElite.addStep(notBareHandShark, bareHandSharkTask);

		cremateShadeTask = new ConditionalStep(this, cremateShade);
		doElite.addStep(notCremateShade, cremateShadeTask);

		barrowsChestTask = new ConditionalStep(this, moveToBarrows);
		barrowsChestTask.addStep(inBarrows, barrowsChest);
		doElite.addStep(notBarrowsChest, barrowsChestTask);

		abyssalDemonTask = new ConditionalStep(this, moveToSlayer2);
		abyssalDemonTask.addStep(inSlayer2, moveToSlayer3);
		abyssalDemonTask.addStep(inSlayer3, abyssalDemon);
		doElite.addStep(notAbyssalDemon, abyssalDemonTask);

		craftBlackDhideBodyTask = new ConditionalStep(this, moveToCanifisBank);
		craftBlackDhideBodyTask.addStep(inCanifisBank, craftBlackDhideBody);
		doElite.addStep(notCraftBlackDhideBody, craftBlackDhideBodyTask);

		fertilizeHerbTask = new ConditionalStep(this, fertilizeHerb);
		doElite.addStep(notFertilizeHerb, fertilizeHerbTask);

		return doElite;
	}

	@Override
	public void setupRequirements()
	{
		notBareHandShark = new VarplayerRequirement(1181, false, 3);
		notCremateShade = new VarplayerRequirement(1181, false, 4);
		notFertilizeHerb = new VarplayerRequirement(1181, false, 5);
		notCraftBlackDhideBody = new VarplayerRequirement(1181, false, 6);
		notAbyssalDemon = new VarplayerRequirement(1181, false, 7);
		notBarrowsChest = new VarplayerRequirement(1181, false, 8);

		magicRedwoodPyreLogs = new ItemRequirement("Magic / Redwood pyrelogs", ItemCollections.ELITE_PYRE_LOGS)
			.showConditioned(notCremateShade);
		magicRedwoodPyreLogs.setTooltip("Can be created by using sacred oil on magic / redwood logs");
		shadeRemains = new ItemRequirement("Shade remains", ItemCollections.SHADE_REMAINS).showConditioned(notCremateShade);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).showConditioned(notCremateShade);
		earthRune = new ItemRequirement("Earth rune", ItemID.EARTH_RUNE).showConditioned(notFertilizeHerb);
		astralRune = new ItemRequirement("Astral rune", ItemID.ASTRAL_RUNE).showConditioned(notFertilizeHerb);
		natureRune = new ItemRequirement("Nature rune", ItemID.NATURE_RUNE).showConditioned(notFertilizeHerb);
		blackLeather = new ItemRequirement("Black dragon leather", ItemID.BLACK_DRAGON_LEATHER).showConditioned(notCraftBlackDhideBody);
		needle = new ItemRequirement("Needle", ItemID.NEEDLE).showConditioned(notCraftBlackDhideBody);
		thread = new ItemRequirement("Thread", ItemID.THREAD).showConditioned(notCraftBlackDhideBody);


		ahrimSet = new ItemRequirements("Ahrim Set", new ItemRequirement("Hood", ItemCollections.AHRIM_HOOD),
			new ItemRequirement("Staff", ItemCollections.AHRIM_STAFF), new ItemRequirement("Top",
			ItemCollections.AHRIM_ROBETOP), new ItemRequirement("Skirt", ItemCollections.AHRIM_ROBESKIRT));

		karilSet = new ItemRequirements("Karil Set", new ItemRequirement("Crossbow",
			ItemCollections.KARIL_CROSSBOW), new ItemRequirement("Coif", ItemCollections.KARIL_COIF),
			new ItemRequirement("Top", ItemCollections.KARIL_TOP), new ItemRequirement("Skirt",
			ItemCollections.KARIL_SKIRT));

		guthanSet = new ItemRequirements("Guthan Set", new ItemRequirement("Helm", ItemCollections.GUTHAN_HELM),
			new ItemRequirement("Spear", ItemCollections.GUTHAN_WARSPEAR), new ItemRequirement("Body",
			ItemCollections.GUTHAN_BODY), new ItemRequirement("Skirt", ItemCollections.GUTHAN_SKIRT));

		veracSet = new ItemRequirements("Verac Set", new ItemRequirement("Helm", ItemCollections.VERAC_HELM),
			new ItemRequirement("Flail", ItemCollections.VERAC_FLAIL), new ItemRequirement("Brassard",
			ItemCollections.VERAC_BRASSARD), new ItemRequirement("Skirt", ItemCollections.VERAC_SKIRT));

		dharokSet = new ItemRequirements("Dharok Set", new ItemRequirement("Helm", ItemCollections.DHAROK_HELM),
			new ItemRequirement("Axe", ItemCollections.DHAROK_AXE), new ItemRequirement("Body",
			ItemCollections.DHAROK_BODY), new ItemRequirement("Legs", ItemCollections.DHAROK_LEGS));

		toragSet = new ItemRequirements("Torag Set", new ItemRequirement("Helm", ItemCollections.TORAG_HELM),
			new ItemRequirement("Hammers", ItemCollections.TORAG_HAMMERS), new ItemRequirement("Body",
			ItemCollections.TORAG_BODY), new ItemRequirement("legs", ItemCollections.TORAG_LEGS));

		barrowsSet = new ItemRequirements(LogicType.OR, "Any complete barrows set", ahrimSet, karilSet, guthanSet,
			veracSet, dharokSet, toragSet).showConditioned(notBarrowsChest);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		barrowsTab = new ItemRequirement("Barrows teleport", ItemID.BARROWS_TELEPORT);
		slayerRing = new ItemRequirement("Slayer ring", ItemCollections.SLAYER_RINGS);
		ectophial = new ItemRequirement("Ectophial", ItemID.ECTOPHIAL);
		mortTP = new ItemRequirement("Mort'ton Teleport", ItemID.MORTTON_TELEPORT);
		spade = new ItemRequirement("Spade", ItemID.SPADE).showConditioned(notBarrowsChest);

		lunarBook = new SpellbookRequirement(Spellbook.LUNAR);

		inCanifisBank = new ZoneRequirement(canifisBank);
		inSlayer2 = new ZoneRequirement(slayer2);
		inSlayer3 = new ZoneRequirement(slayer3);
		inBarrows = new ZoneRequirement(barrows);

		// TODO add barbarian training varb for bare hand fishing
		inAidOfTheMyreque = new QuestRequirement(QuestHelperQuest.IN_AID_OF_THE_MYREQUE, QuestState.FINISHED);
		shadesOfMorton = new QuestRequirement(QuestHelperQuest.SHADES_OF_MORTTON, QuestState.FINISHED);
		lunarDiplomacy = new QuestRequirement(QuestHelperQuest.LUNAR_DIPLOMACY, QuestState.FINISHED);
		bareHandBarb = new ItemRequirement("Completed the Barbarian bare-handed fishing", 1, -1);
	}

	public void loadZones()
	{
		canifisBank = new Zone(new WorldPoint(3509, 3483, 0), new WorldPoint(3514, 3476, 0));
		slayer2 = new Zone(new WorldPoint(3404, 3580, 1), new WorldPoint(3453, 3530, 1));
		slayer3 = new Zone(new WorldPoint(3404, 3580, 2), new WorldPoint(3453, 3530, 2));
		barrows = new Zone(new WorldPoint(3521, 9724, 0), new WorldPoint(3581, 9665, 0));
	}

	public void setupSteps()
	{
		moveToBarrows = new DetailedQuestStep(this, new WorldPoint(3566, 3299, 0),
			"Dig at the top of the mounds and search the Sarcophagi until you find the hidden tunnel. A spade can " +
				"be found in a shed at the entrance.", spade, barrowsSet, food);
		// equipped doesn't work, it's highlighted as long as it's in inventory.
		barrowsChest = new ObjectStep(this, 20973, new WorldPoint(3552, 9696, 0),
			"Loot the chest wearing a complete set of barrows gear.", barrowsSet.equipped());

		cremateShade = new ObjectStep(this, ObjectID.FUNERAL_PYRE, new WorldPoint(3500, 3266, 0),
			"Place the pyre logs and shade remains on the funeral pyre and light them with a tinderbox", tinderbox,
			shadeRemains, magicRedwoodPyreLogs);
		cremateShade.addAlternateObjects(ObjectID.FUNERAL_PYRE_28865, ObjectID.FUNERAL_PYRE_4099);

		bareHandShark = new NpcStep(this, NpcID.FISHING_SPOT_4476, new WorldPoint(3479, 3189, 0),
			"Bare hand fish a shark in Burgh de Rott.");

		moveToSlayer2 = new ObjectStep(this, 2114, new WorldPoint(3436, 3538, 0),
			"Climb the stairs or the Spikey chains in the Slayer tower to ascend to the higher level.", combatGear,
			food);
		moveToSlayer3 = new ObjectStep(this, ObjectID.STAIRCASE_2119, new WorldPoint(3415, 3541, 1),
			"Climb the stairs or the Spikey chains in the Slayer tower to ascend to the higher level.", combatGear,
			food);
		abyssalDemon = new NpcStep(this, NpcID.ABYSSAL_DEMON_415, new WorldPoint(3420, 3569, 2),
			"Kill an Abyssal demon.", combatGear, food);

		moveToCanifisBank = new DetailedQuestStep(this, new WorldPoint(3511, 3480, 0),
			"Move to the bank in Canifis.", blackLeather.quantity(3).highlighted(), needle.highlighted(), thread);
		craftBlackDhideBody = new DetailedQuestStep(this, "Craft a black dragon hide body.",
			blackLeather.quantity(3).highlighted(), needle.highlighted(), thread);

		fertilizeHerb = new ObjectStep(this, 8153, new WorldPoint(3606, 3530, 0),
			"Cast Fertile Soil on the herb patch in Morytania.", lunarBook, earthRune.quantity(15),
			astralRune.quantity(3), natureRune.quantity(2));
		fertilizeHerb.addIcon(SpriteID.SPELL_FERTILE_SOIL);

		claimReward = new NpcStep(this, NpcID.LESABR, new WorldPoint(3464, 3480, 0),
			"Talk to Le-Sabre near Canifis to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear, magicRedwoodPyreLogs, shadeRemains, tinderbox, barrowsSet,
			blackLeather.quantity(3),
			needle, thread, earthRune.quantity(15), astralRune.quantity(3), natureRune.quantity(2));
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, barrowsTab, slayerRing, ectophial, mortTP, spade);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.ATTACK, 70));
		reqs.add(new SkillRequirement(Skill.CRAFTING, 84, true));
		reqs.add(new SkillRequirement(Skill.DEFENCE, 70));
		reqs.add(new SkillRequirement(Skill.FIREMAKING, 80, true));
		reqs.add(new SkillRequirement(Skill.FISHING, 96, true));
		reqs.add(new SkillRequirement(Skill.MAGIC, 83, true));
		reqs.add(new SkillRequirement(Skill.RANGED, 70));
		reqs.add(new SkillRequirement(Skill.SLAYER, 85, true));
		reqs.add(new SkillRequirement(Skill.STRENGTH, 76));

		reqs.add(inAidOfTheMyreque);
		reqs.add(shadesOfMorton);
		reqs.add(lunarDiplomacy);
		reqs.add(bareHandBarb);

		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("One Abyssal Demon (lvl 124)");
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Morytania legs 4", ItemID.MORYTANIA_LEGS_4),
			new ItemReward("50,000 Exp. Lamp (Any skill over 30)", ItemID.ANTIQUE_LAMP)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Unlimited teleports to the slime pit beneath the Ectofuntus from Morytania legs"),
			new UnlockReward("Robin offers 39 free buckets of slime and 39 pots of bonemeal in exchange for bones each day"),
			new UnlockReward("50% more Firemaking experience when burning shade remains"),
			new UnlockReward("Bones buried from the Bonecrusher grant full Prayer experience"),
			new UnlockReward("Access to the herb patch on Harmony Island"),
			new UnlockReward("10% more Slayer experience in the Slayer Tower while on a Slayer task")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails grabSharkSteps = new PanelDetails("Grab Shark", Collections.singletonList(bareHandShark),
			new SkillRequirement(Skill.FISHING, 96, true), new SkillRequirement(Skill.STRENGTH, 76),
			inAidOfTheMyreque, bareHandBarb);
		grabSharkSteps.setDisplayCondition(notBareHandShark);
		grabSharkSteps.setLockingStep(bareHandSharkTask);
		allSteps.add(grabSharkSteps);

		PanelDetails cremateSteps = new PanelDetails("Cremate Shade Remains", Collections.singletonList(cremateShade),
			new SkillRequirement(Skill.FIREMAKING, 80), shadesOfMorton, magicRedwoodPyreLogs, shadeRemains, tinderbox);
		cremateSteps.setDisplayCondition(notCremateShade);
		cremateSteps.setLockingStep(cremateShadeTask);
		allSteps.add(cremateSteps);

		PanelDetails barrowsSteps = new PanelDetails("Barrows Looting", Arrays.asList(moveToBarrows,
			barrowsChest), new SkillRequirement(Skill.DEFENCE, 70, false, "70 Defense and one of the following:"),
			new SkillRequirement(Skill.ATTACK, 70), new SkillRequirement(Skill.MAGIC, 70),
			new SkillRequirement(Skill.RANGED, 70), new SkillRequirement(Skill.STRENGTH, 70), barrowsSet);
		barrowsSteps.setDisplayCondition(notBarrowsChest);
		barrowsSteps.setLockingStep(barrowsChestTask);
		allSteps.add(barrowsSteps);

		PanelDetails abyssalDemonSteps = new PanelDetails("Kill Abyssal Demon", Arrays.asList(moveToSlayer2, moveToSlayer3,
			abyssalDemon), new SkillRequirement(Skill.SLAYER, 85, true), combatGear, food);
		abyssalDemonSteps.setDisplayCondition(notAbyssalDemon);
		abyssalDemonSteps.setLockingStep(abyssalDemonTask);
		allSteps.add(abyssalDemonSteps);

		PanelDetails craftSteps = new PanelDetails("Craft Black D'hide Body", Arrays.asList(moveToCanifisBank,
			craftBlackDhideBody), new SkillRequirement(Skill.CRAFTING, 84, true),
			blackLeather.quantity(3), needle, thread);
		craftSteps.setDisplayCondition(notCraftBlackDhideBody);
		craftSteps.setLockingStep(craftBlackDhideBodyTask);
		allSteps.add(craftSteps);

		PanelDetails fertSteps = new PanelDetails("Fertilize Herb Patch", Collections.singletonList(fertilizeHerb),
			new SkillRequirement(Skill.MAGIC, 83, true), lunarDiplomacy, lunarBook, earthRune.quantity(15),
			astralRune.quantity(3), natureRune.quantity(2));
		fertSteps.setDisplayCondition(notFertilizeHerb);
		fertSteps.setLockingStep(fertilizeHerbTask);
		allSteps.add(fertSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}