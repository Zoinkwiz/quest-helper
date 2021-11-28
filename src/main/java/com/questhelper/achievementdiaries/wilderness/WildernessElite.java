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
package com.questhelper.achievementdiaries.wilderness;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
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
import com.questhelper.steps.ItemStep;
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
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.WILDERNESS_ELITE
)
public class WildernessElite extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement combatGear, hammer, lawRune, waterRune, lobsterPot, darkFishingBait, coins, pickaxe, axe,
		tinderbox, godEquip, coal, runeOre, magicLog, runeBar, rawDarkCrab;

	// Items recommended
	ItemRequirement food;

	// Quests required
	Requirement desertTreasure, enterGodwars, ancientBook, gatheredLogs, caughtCrab;
	// potentially need one more for rune scim step

	Requirement notThreeBosses, notTPGhorrock, notDarkCrab, notRuneScim, notRoguesChest, notSpiritMage, notMagicLogs;

	QuestStep claimReward, tPGhorrock, darkCrab, cookDarkCrab, runeScim, roguesChest, magicLogs,
		burnLogs, moveToResource1, moveToResource2, moveToResource3, smeltBar, moveToGodWars1, moveToGodWars2,
		killCallisto, killVene, killVetion;

	NpcStep threeBosses, spiritMage, runiteGolem;

	Zone resource, godWars1, godWars2;

	ZoneRequirement inResource, inGodWars2, inGodWars1;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doElite = new ConditionalStep(this, claimReward);
		doElite.addStep(notTPGhorrock, tPGhorrock);
		doElite.addStep(new Conditions(notMagicLogs, inResource, gatheredLogs, magicLog), burnLogs);
		doElite.addStep(new Conditions(notMagicLogs, inResource), magicLogs);
		doElite.addStep(notMagicLogs, moveToResource1);
		doElite.addStep(new Conditions(notRuneScim, inResource, runeBar.quantity(2)), runeScim);
		doElite.addStep(new Conditions(notRuneScim, inResource, runeOre.quantity(2), coal.quantity(16)), smeltBar);
		doElite.addStep(new Conditions(notRuneScim, inResource), runiteGolem);
		doElite.addStep(notRuneScim, moveToResource2);
		doElite.addStep(new Conditions(notDarkCrab, inResource, caughtCrab, rawDarkCrab), cookDarkCrab);
		doElite.addStep(new Conditions(notDarkCrab, inResource), darkCrab);
		doElite.addStep(notDarkCrab, moveToResource3);
		doElite.addStep(notRoguesChest, roguesChest);
		doElite.addStep(new Conditions(notSpiritMage, inGodWars2), spiritMage);
		doElite.addStep(new Conditions(notSpiritMage, inGodWars1), moveToGodWars2);
		doElite.addStep(notSpiritMage, moveToGodWars1);
		doElite.addStep(notThreeBosses, threeBosses);

		return doElite;
	}

	public void setupRequirements()
	{
		// unsure bits need to be tested. If you already have this completed send me the result of your "::getvarp 1193"
		notThreeBosses = new VarplayerRequirement(1193, false, 3); // unsure of bit
		notTPGhorrock = new VarplayerRequirement(1193, false, 4);// unsure of bit
		notDarkCrab = new VarplayerRequirement(1193, false, 5);// unsure of bit
		notRuneScim = new VarplayerRequirement(1193, false, 6);// unsure of bit
		notRoguesChest = new VarplayerRequirement(1193, false, 7);// unsure of bit
		notSpiritMage = new VarplayerRequirement(1193, false, 8);// unsure of bit
		notMagicLogs = new VarplayerRequirement(1193, false, 11);

		ancientBook = new SpellbookRequirement(Spellbook.ANCIENT);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notRuneScim);
		lawRune = new ItemRequirement("Law rune", ItemID.LAW_RUNE).showConditioned(notTPGhorrock);
		waterRune = new ItemRequirement("Water rune", ItemID.WATER_RUNE).showConditioned(notTPGhorrock);
		lobsterPot = new ItemRequirement("Lobster pot", ItemID.LOBSTER_POT).showConditioned(notDarkCrab);
		darkFishingBait = new ItemRequirement("Dark fish bait", ItemID.DARK_FISHING_BAIT).showConditioned(notDarkCrab);
		coins = new ItemRequirement("Coins", ItemCollections.getCoins()).showConditioned(new Conditions(LogicType.OR,
			notDarkCrab, notMagicLogs, notRuneScim));
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes()).showConditioned(notRuneScim);
		axe = new ItemRequirement("Any axe", ItemCollections.getAxes()).showConditioned(notMagicLogs);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).showConditioned(notMagicLogs);
		godEquip = new ItemRequirement("Various god equipment (1 of each god suggested)", -1, -1)
			.showConditioned(notSpiritMage);
		coal = new ItemRequirement("Coal", ItemID.COAL).showConditioned(notRuneScim);
		runeOre = new ItemRequirement("Runite ore", ItemID.RUNITE_ORE);
		runeBar = new ItemRequirement("Runite bar", ItemID.RUNITE_BAR);
		magicLog = new ItemRequirement("Magic logs", ItemID.MAGIC_LOGS);
		rawDarkCrab = new ItemRequirement("Raw dark crab", ItemID.RAW_DARK_CRAB);

		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);

		enterGodwars = new ItemRequirement("60 Strength or Agility", -1, -1);

		inResource = new ZoneRequirement(resource);
		inGodWars1 = new ZoneRequirement(godWars1);
		inGodWars2 = new ZoneRequirement(godWars2);

		gatheredLogs = new ChatMessageRequirement(
			inResource,
			"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
		);
		((ChatMessageRequirement) gatheredLogs).setInvalidateRequirement(
			new ChatMessageRequirement(
				new Conditions(LogicType.NOR, inResource),
				"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
			)
		);

		caughtCrab = new ChatMessageRequirement(
			inResource,
			"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
		);
		((ChatMessageRequirement) caughtCrab).setInvalidateRequirement(
			new ChatMessageRequirement(
				new Conditions(LogicType.NOR, inResource),
				"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
			)
		);

		desertTreasure = new QuestRequirement(QuestHelperQuest.DESERT_TREASURE, QuestState.FINISHED);
	}

	public void loadZones()
	{
		resource = new Zone(new WorldPoint(3174, 3944, 0), new WorldPoint(3196, 3924, 0));
		godWars1 = new Zone(new WorldPoint(3046, 10177, 3), new WorldPoint(3076, 10138, 3));
		godWars2 = new Zone(new WorldPoint(3014, 10168, 0), new WorldPoint(3069, 10115, 0));
	}

	public void setupSteps()
	{
		moveToResource1 = new ObjectStep(this, ObjectID.GATE_26760, new WorldPoint(3184, 3944, 0),
			"Enter the Wilderness Resource Area.", coins.quantity(6000), axe, tinderbox);
		magicLogs = new ObjectStep(this, ObjectID.MAGIC_TREE_10834, new WorldPoint(3190, 3926, 0),
			"Chop some magic logs.", true, axe, tinderbox);
		burnLogs = new ItemStep(this, "Burn the magic logs in the Resource Area.", tinderbox.highlighted(),
			magicLog.highlighted());

		moveToResource2 = new ObjectStep(this, ObjectID.GATE_26760, new WorldPoint(3184, 3944, 0),
			"Enter the Wilderness Resource Area.", coins.quantity(6000), combatGear, food, pickaxe, coal.quantity(16));
		runiteGolem = new NpcStep(this, NpcID.RUNITE_GOLEM, new WorldPoint(3189, 3938, 0),
			"Kill and mine the Runite Golems in the Resource Area.", combatGear, food, pickaxe, coal.quantity(16));
		runiteGolem.addAlternateNpcs(NpcID.RUNITE_ORE);
		smeltBar = new ObjectStep(this, ObjectID.FURNACE_26300, new WorldPoint(3191, 3936, 0),
			"Smelt the ore into runite bars.", hammer, runeOre.quantity(2), coal.quantity(16));
		runeScim = new ObjectStep(this, ObjectID.ANVIL_2097, new WorldPoint(3190, 3938, 0),
			"Smith a runite scimitar in the Resource Area.", hammer, runeBar.quantity(2));

		moveToResource3 = new ObjectStep(this, ObjectID.GATE_26760, new WorldPoint(3184, 3944, 0),
			"Enter the Wilderness Resource Area.", coins.quantity(6000), lobsterPot, darkFishingBait);
		darkCrab = new NpcStep(this, NpcID.FISHING_SPOT_1536, new WorldPoint(3187, 3927, 0),
			"Fish a dark crab in the Resource Area.", lobsterPot, darkFishingBait);
		cookDarkCrab = new ObjectStep(this, ObjectID.FIRE_26185, new WorldPoint(3188, 3930, 0),
			"Cook the raw dark crab on the nearby fire.", rawDarkCrab);

		tPGhorrock = new DetailedQuestStep(this, "Teleport to Ghorrock.", ancientBook, lawRune.quantity(2),
			waterRune.quantity(8));

		moveToGodWars1 = new ObjectStep(this, ObjectID.CAVE_26766, new WorldPoint(3018, 3739, 0),
			"Enter the Wilderness God Wars Dungeon.", combatGear, food, godEquip);
		moveToGodWars2 = new ObjectStep(this, ObjectID.CREVICE_26767, new WorldPoint(3066, 10142, 0),
			"Use the crevice to enter the Wilderness God Wars Dungeon. The Strength entrance is to the west.",
			combatGear, food, godEquip);
		spiritMage = new NpcStep(this, NpcID.SPIRITUAL_MAGE, new WorldPoint(3050, 10131, 0),
			"Kill a Spiritual Warrior in the Wilderness God Wars Dungeon.", combatGear, food, godEquip);
		spiritMage.addAlternateNpcs(NpcID.SPIRITUAL_MAGE_2244, NpcID.SPIRITUAL_MAGE_3161, NpcID.SPIRITUAL_MAGE_3168);

		// TODO multiple minimap markers / track this tasks' varb for multiple steps (I'm a skiller T_T)
		threeBosses = new NpcStep(this, NpcID.CALLISTO, new WorldPoint(3291, 3844, 0),
			"Kill Callisto, Venenatis, and Vet'ion. You must complete this task fully before continuing the other " +
				"tasks.", combatGear, food);
		threeBosses.addAlternateNpcs(NpcID.VETION, NpcID.VENENATIS);
		//killCallisto = new NpcStep(this, NpcID.CALLISTO, new WorldPoint(3291, 3844, 0), "Kill Callisto");
		//killVene = new NpcStep(this, NpcID.VENENATIS, new WorldPoint(3329, 3743, 0), "Kill Venenatis");
		//killVetion = new NpcStep(this, NpcID.VETION, new WorldPoint(3225, 3797, 0), "Kill Vet'ion");

		roguesChest = new ObjectStep(this, ObjectID.CHEST_26757, new WorldPoint(3297, 3940, 0),
			"Steal from the chest in Rogues' Castle.");

		claimReward = new NpcStep(this, NpcID.LESSER_FANATIC, new WorldPoint(3121, 3518, 0),
			"Talk to Lesser Fanatic in Edgeville to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear, lawRune.quantity(2), waterRune.quantity(8), coins.quantity(3750), axe,
			tinderbox, pickaxe, coal.quantity(16), lobsterPot, darkFishingBait, godEquip);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Collections.singletonList(food);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(enterGodwars);
		reqs.add(new SkillRequirement(Skill.COOKING, 90));
		reqs.add(new SkillRequirement(Skill.FIREMAKING, 75));
		reqs.add(new SkillRequirement(Skill.FISHING, 85));
		reqs.add(new SkillRequirement(Skill.MAGIC, 96));
		reqs.add(new SkillRequirement(Skill.MINING, 85));
		reqs.add(new SkillRequirement(Skill.SLAYER, 83));
		reqs.add(new SkillRequirement(Skill.SMITHING, 90));
		reqs.add(new SkillRequirement(Skill.THIEVING, 84));
		reqs.add(new SkillRequirement(Skill.WOODCUTTING, 75));

		reqs.add(desertTreasure);

		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Callisto (lvl 470)", "Venenatis (lvl 464)", "Vet'ion (lvl 454)",
			"Runite Golem (lvl 178)", "Spiritual Mage (lvl 121)");
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Wilderness Sword 4", ItemID.WILDERNESS_SWORD_4),
			new ItemReward("50,000 Exp. Lamp (Any skill over 70)", ItemID.ANTIQUE_LAMP)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Unlimited free teleports to the Fountain of Rune on the Wilderness Sword 4"),
			new UnlockReward("Free entry to the Resource Area"),
			new UnlockReward("All dragon bones drops in the Wilderness are noted. (Note: This does not include the King Black Dragon, as his lair is not Wilderness affected.)"),
			new UnlockReward("	Noted lava dragon bones can be toggled by speaking to the Lesser Fanatic."),
			new UnlockReward("50 random free runes from Lundail once per day"),
			new UnlockReward("Increased dark crab catch rate")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails tpSteps = new PanelDetails("Teleport to Ghorrock", Collections.singletonList(tPGhorrock),
			new SkillRequirement(Skill.MAGIC, 96), desertTreasure, lawRune.quantity(2), waterRune.quantity(8));
		tpSteps.setDisplayCondition(notTPGhorrock);
		allSteps.add(tpSteps);

		PanelDetails magicSteps = new PanelDetails("Chop and Burn Magic Logs", Arrays.asList(moveToResource1, magicLogs,
			burnLogs), new SkillRequirement(Skill.FIREMAKING, 75), new SkillRequirement(Skill.WOODCUTTING, 75),
			coins.quantity(6000), axe, tinderbox);
		magicSteps.setDisplayCondition(notMagicLogs);
		allSteps.add(magicSteps);

		PanelDetails scimSteps = new PanelDetails("Rune Scimitar in Resource Area",
			Arrays.asList(moveToResource2, runiteGolem, smeltBar, runeScim), new SkillRequirement(Skill.MINING, 85),
			new SkillRequirement(Skill.SMITHING, 90), coins.quantity(6000), combatGear, food, pickaxe,
			coal.quantity(16));
		scimSteps.setDisplayCondition(notRuneScim);
		allSteps.add(scimSteps);

		PanelDetails crabSteps = new PanelDetails("Dark Crab in Resource Area", Arrays.asList(moveToResource3, darkCrab,
			cookDarkCrab), new SkillRequirement(Skill.COOKING, 90), new SkillRequirement(Skill.FISHING, 85),
			coins.quantity(6000), lobsterPot, darkFishingBait);
		crabSteps.setDisplayCondition(notDarkCrab);
		allSteps.add(crabSteps);

		PanelDetails chestSteps = new PanelDetails("Rogues' Castle Chest", Collections.singletonList(roguesChest));
		chestSteps.setDisplayCondition(notRoguesChest);
		allSteps.add(chestSteps);

		PanelDetails mageSteps = new PanelDetails("Spiritual Mage", Arrays.asList(moveToGodWars1, moveToGodWars2,
			spiritMage), enterGodwars, combatGear, food, godEquip);
		mageSteps.setDisplayCondition(notSpiritMage);
		allSteps.add(mageSteps);

		PanelDetails bossSteps = new PanelDetails("Three bosses", Collections.singletonList(threeBosses), combatGear,
			food);
		bossSteps.setDisplayCondition(notThreeBosses);
		allSteps.add(bossSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
