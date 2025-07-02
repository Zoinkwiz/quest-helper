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
package com.questhelper.helpers.achievementdiaries.wilderness;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.ComplexRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarPlayerID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WildernessHard extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement combatGear, godRunes, godStaff, unpoweredOrb, airRune, cosmicRune, smallFishingNet, rope, knife,
		oilyRod, fishingBait, godEquip, pickaxe, coins, addyBar, addyOre, barsOrPick, hammer, lavaDragonBones;

	// Items recommended
	ItemRequirement food, burningAmulet;

	// Quests required
	Requirement mageArena, deathPlateau, normalBook;

	Requirement notGodSpells, notAirOrb, notBlackSally, notAddyScim, notLavaDrag, notChaosEle, notThreeBosses,
		notTrollWildy, notSprirtualWarrior, notRawLavaEel, enterGodwars;

	QuestStep claimReward, godSpells, airOrb, blackSally, addyScim, lavaDrag, trollWildy, rawLavaEel, moveToEdge,
		moveToAir, moveToResource, moveToGodWars1, moveToGodWars2, buryBone;

	NpcStep threeBosses, chaosEle, sprirtualWarrior;

	Zone edge, air, resource, godWars1, godWars2, scorpCave;

	ZoneRequirement inEdge, inAir, inResource, inGodWars1, inGodWars2, inScorpCave;

	ConditionalStep godSpellsTask, airOrbTask, blackSallyTask, addyScimTask, lavaDragTask, chaosEleTask, threeBossesTask,
		trollWildyTask, sprirtualWarriorTask, rawLavaEelTask;

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupSteps();

		ConditionalStep doHard = new ConditionalStep(this, claimReward);

		airOrbTask = new ConditionalStep(this, moveToEdge);
		airOrbTask.addStep(inEdge, moveToAir);
		airOrbTask.addStep(inAir, airOrb);
		doHard.addStep(notAirOrb, airOrbTask);

		blackSallyTask = new ConditionalStep(this, blackSally);
		doHard.addStep(notBlackSally, blackSallyTask);

		lavaDragTask = new ConditionalStep(this, lavaDrag);
		lavaDragTask.addStep(lavaDragonBones, buryBone);
		doHard.addStep(notLavaDrag, lavaDragTask);

		rawLavaEelTask = new ConditionalStep(this, rawLavaEel);
		doHard.addStep(notRawLavaEel, rawLavaEelTask);

		sprirtualWarriorTask = new ConditionalStep(this, moveToGodWars1);
		sprirtualWarriorTask.addStep(inGodWars1, moveToGodWars2);
		sprirtualWarriorTask.addStep(inGodWars2, sprirtualWarrior);
		doHard.addStep(notSprirtualWarrior, sprirtualWarriorTask);

		trollWildyTask = new ConditionalStep(this, trollWildy);
		doHard.addStep(notTrollWildy, trollWildyTask);

		addyScimTask = new ConditionalStep(this, moveToResource);
		addyScimTask.addStep(inResource, addyScim);
		doHard.addStep(notAddyScim, addyScimTask);

		chaosEleTask = new ConditionalStep(this, chaosEle);
		doHard.addStep(notChaosEle, chaosEleTask);

		threeBossesTask = new ConditionalStep(this, threeBosses);
		doHard.addStep(notThreeBosses, threeBossesTask);

		godSpellsTask = new ConditionalStep(this, godSpells);
		doHard.addStep(notGodSpells, godSpellsTask);

		return doHard;
	}

	@Override
	protected void setupRequirements()
	{
		notGodSpells = new VarplayerRequirement(VarPlayerID.WILDERNESS_ACHIEVEMENT_DIARY, false, 25);
		notAirOrb = new VarplayerRequirement(VarPlayerID.WILDERNESS_ACHIEVEMENT_DIARY, false, 26);
		notBlackSally = new VarplayerRequirement(VarPlayerID.WILDERNESS_ACHIEVEMENT_DIARY, false, 27);
		notAddyScim = new VarplayerRequirement(VarPlayerID.WILDERNESS_ACHIEVEMENT_DIARY, false, 28);
		notLavaDrag = new VarplayerRequirement(VarPlayerID.WILDERNESS_ACHIEVEMENT_DIARY, false, 29);
		notChaosEle = new VarplayerRequirement(VarPlayerID.WILDERNESS_ACHIEVEMENT_DIARY, false, 30);
		notThreeBosses = new VarplayerRequirement(VarPlayerID.WILDERNESS_ACHIEVEMENT_DIARY, false, 31);
		notTrollWildy = new VarplayerRequirement(VarPlayerID.WILDERNESS_ACHIEVEMENT_DIARY2, false, 0);
		notSprirtualWarrior = new VarplayerRequirement(VarPlayerID.WILDERNESS_ACHIEVEMENT_DIARY2, false, 1);
		notRawLavaEel = new VarplayerRequirement(VarPlayerID.WILDERNESS_ACHIEVEMENT_DIARY2, false, 2);

		normalBook = new SpellbookRequirement(Spellbook.NORMAL);

		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		godRunes = new ItemRequirement("Runes for a god spell that correspond with your god staff", -1, -1)
			.showConditioned(notGodSpells);
		godStaff = new ItemRequirement("Any god staff", ItemCollections.GOD_STAFF).showConditioned(notGodSpells).isNotConsumed();
		unpoweredOrb = new ItemRequirement("Unpowered orb", ItemID.STAFFORB).showConditioned(notAirOrb);
		cosmicRune = new ItemRequirement("Cosmic rune", ItemID.COSMICRUNE).showConditioned(notAirOrb);
		airRune = new ItemRequirement("Air rune", ItemID.AIRRUNE).showConditioned(notAirOrb);
		smallFishingNet = new ItemRequirement("Small fishing net", ItemID.NET).showConditioned(notBlackSally).isNotConsumed();
		rope = new ItemRequirement("Rope", ItemID.ROPE).showConditioned(notBlackSally).isNotConsumed();
		knife = new ItemRequirement("Knife or slashing weapon", -1, -1).showConditioned(notRawLavaEel).isNotConsumed();
		oilyRod = new ItemRequirement("Oily fishing rod", ItemID.OILY_FISHING_ROD).showConditioned(notRawLavaEel).isNotConsumed();
		fishingBait = new ItemRequirement("Fishing bait", ItemID.FISHING_BAIT).showConditioned(notRawLavaEel).isNotConsumed();
		godEquip = new ItemRequirement("Various god equipment (1 of each god suggested)", -1, -1)
			.showConditioned(notSprirtualWarrior).isNotConsumed();
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).showConditioned(notAddyScim).isNotConsumed();
		coins = new ItemRequirement("Coins", ItemCollections.COINS).showConditioned(notAddyScim);
		addyBar = new ItemRequirement("Adamantite bar", ItemID.ADAMANTITE_BAR, 2).showConditioned(notAddyScim);
		addyOre = new ItemRequirement("Adamantite ore", ItemID.ADAMANTITE_ORE);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notAddyScim).isNotConsumed();
		barsOrPick = new ItemRequirements(LogicType.OR, "2 Adamantite bars or a pickaxe", addyBar, pickaxe);
		lavaDragonBones = new ItemRequirement("Lava Dragon Bones", ItemID.LAVA_DRAGON_BONES);

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		burningAmulet = new ItemRequirement("Burning amulet", ItemCollections.BURNING_AMULETS);

		enterGodwars = new ComplexRequirement(LogicType.OR, "60 Agility or Strength",
			new SkillRequirement(Skill.AGILITY, 60, true), new SkillRequirement(Skill.STRENGTH, 60, true));

		inEdge = new ZoneRequirement(edge);
		inAir = new ZoneRequirement(air);
		inResource = new ZoneRequirement(resource);
		inGodWars1 = new ZoneRequirement(godWars1);
		inGodWars2 = new ZoneRequirement(godWars2);
		inScorpCave = new ZoneRequirement(scorpCave);

		mageArena = new QuestRequirement(QuestHelperQuest.THE_MAGE_ARENA, QuestState.FINISHED);
		deathPlateau = new QuestRequirement(QuestHelperQuest.DEATH_PLATEAU, QuestState.IN_PROGRESS);
	}

	@Override
	protected void setupZones()
	{
		edge = new Zone(new WorldPoint(3067, 10000, 0), new WorldPoint(3288, 9821, 0));
		air = new Zone(new WorldPoint(3081, 3576, 0), new WorldPoint(3094, 3564, 0));
		resource = new Zone(new WorldPoint(3174, 3944, 0), new WorldPoint(3196, 3924, 0));
		godWars1 = new Zone(new WorldPoint(3046, 10177, 3), new WorldPoint(3076, 10138, 3));
		godWars2 = new Zone(new WorldPoint(3014, 10168, 0), new WorldPoint(3069, 10115, 0));
		scorpCave = new Zone(new WorldPoint(3217, 10354, 0), new WorldPoint(3249, 10329, 0));
	}

	public void setupSteps()
	{
		godSpells = new DetailedQuestStep(this, "Cast one of the 3 god spells against another player in the " +
			"Wilderness. Splashing will not count.", godStaff.equipped(), godRunes);

		moveToEdge = new ObjectStep(this, ObjectID.TRAPDOOR_OPEN, new WorldPoint(3097, 3468, 0),
			"Enter to the Edgeville dungeon.", airRune.quantity(30), cosmicRune.quantity(3), unpoweredOrb);
		moveToAir = new ObjectStep(this, ObjectID.LADDER_FROM_CELLAR, new WorldPoint(3088, 9971, 0),
			"Climb the ladder that leads to the Obelisk of Air.", airRune.quantity(30), cosmicRune.quantity(3), unpoweredOrb);
		airOrb = new ObjectStep(this, ObjectID.OBELISK_AIR, new WorldPoint(3088, 3569, 0),
			"Cast charge air orb on the Obelisk of Air.", airRune.quantity(30), cosmicRune.quantity(3), unpoweredOrb);

		chaosEle = new NpcStep(this, NpcID.CHAOSELEMENTAL, new WorldPoint(3263, 3918, 0),
			"Kill the Chaos Elemental.", combatGear, food);
		chaosEle.addAlternateNpcs(NpcID.CLANCUP_CHAOSELEMENTAL);

		blackSally = new ObjectStep(this, ObjectID.HUNTING_SAPLING_UP_BLACK, new WorldPoint(3296, 3671, 0),
			"Set a trap and catch a Black salamander in the Wilderness.", true);

		lavaDrag = new NpcStep(this, NpcID.LAVA_DRAGON, new WorldPoint(3209, 3848, 0),
			"Kill a Lava dragon and bury the bones on Lava Dragon Isle.", true, combatGear, food);
		buryBone = new ItemStep(this, "Bury the dragon bones", lavaDragonBones.highlighted());

		rawLavaEel = new NpcStep(this, NpcID._0_47_59_LAVAFISH, new WorldPoint(3071, 3839, 0),
			"Fish a raw lava eel in the Lava Maze.", knife, oilyRod, fishingBait);

		addyScim = new ObjectStep(this, ObjectID.ANVIL, new WorldPoint(3190, 3938, 0),
			"Smith an Adamant scimitar in the Resource Area.", hammer, addyBar);
		moveToResource = new ObjectStep(this, ObjectID.WILDERNESS_RESOURCE_GATE, new WorldPoint(3184, 3944, 0),
			"Enter the Wilderness Resource Area.", coins.quantity(6000), hammer, addyBar);

		moveToGodWars1 = new ObjectStep(this, ObjectID.WILDERNESS_GWD_ENTRANCE, new WorldPoint(3017, 3738, 0),
			"Enter the Wilderness God Wars Dungeon.", combatGear, food, godEquip);
		moveToGodWars2 = new ObjectStep(this, ObjectID.WILDERNESS_GWD_CREVICE, new WorldPoint(3066, 10142, 3),
			"Use the crevice to enter the Wilderness God Wars Dungeon. The Strength entrance is to the West.",
			combatGear, food, godEquip);
		sprirtualWarrior = new NpcStep(this, NpcID.GODWARS_SPIRITUAL_SARADOMIN_WARRIOR, new WorldPoint(3050, 10131, 0),
			"Kill a Spiritual Warrior in the Wilderness God Wars Dungeon.", combatGear, food, godEquip);
		sprirtualWarrior.addAlternateNpcs(NpcID.GODWARS_SPIRITUAL_BANDOS_WARRIOR, NpcID.GODWARS_SPIRITUAL_ZAMORAK_WARRIOR,
			NpcID.GODWARS_SPIRITUAL_ARMADYL_WARRIOR);

		threeBosses = new NpcStep(this, NpcID.CRAZY_ARCHAEOLOGIST, new WorldPoint(3947, 3706, 0),
			"Kill Crazy archaeologist, Chaos Fanatic, and Scorpia. You must complete this task " +
				"fully before continuing the other tasks.", combatGear, food);
		threeBosses.addAlternateNpcs(NpcID.SCORPIA, NpcID.CHAOS_FANATIC);

		trollWildy = new ObjectStep(this, ObjectID.TROLLHEIM_WILDY_CLIMB_ROCKS, new WorldPoint(2916, 3672, 0),
			"Take the agility shortcut from Trollheim to the Wilderness.");

		claimReward = new NpcStep(this, NpcID.LESSER_FANATIC_DIARY, new WorldPoint(3121, 3518, 0),
			"Talk to Lesser Fanatic in Edgeville to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear, godStaff, godRunes, airRune.quantity(30), cosmicRune.quantity(3),
			unpoweredOrb, knife, oilyRod, fishingBait, coins.quantity(6000), hammer, barsOrPick, godEquip);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, burningAmulet);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(enterGodwars);
		reqs.add(new SkillRequirement(Skill.AGILITY, 64, true));
		reqs.add(new SkillRequirement(Skill.FISHING, 53, true));
		reqs.add(new SkillRequirement(Skill.HUNTER, 67, true));
		reqs.add(new SkillRequirement(Skill.MAGIC, 66, true));
		reqs.add(new SkillRequirement(Skill.SLAYER, 68, true));
		reqs.add(new SkillRequirement(Skill.SMITHING, 75, true));

		reqs.add(deathPlateau);
		reqs.add(mageArena);

		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Chaos Elemental (lvl 305)", "Crazy archaeologist (lvl 204)",
			"Chaos Fanatic (lvl 202)", "Lava dragon (lvl 252)", "Scorpia (lvl 225)", "Spiritual warrior (lvl 115-134)");
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Wilderness Sword 3", ItemID.WILDERNESS_SWORD_HARD),
			new ItemReward("15,000 Exp. Lamp (Any skill over 50)", ItemID.THOSF_REWARD_LAMP)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("One free teleport to the Fountain of Rune daily on the Wilderness Sword 3"),
			new UnlockReward("50% more lava shards per lava scale"),
			new UnlockReward("Access to a shortcut to the Lava Dragon Isle (requires Agility 74 )"),
			new UnlockReward("Access to a shortcut to the Lava Maze (requires Agility 82 )"),
			new UnlockReward("Can have 5 ecumenical keys at a time"),
			new UnlockReward("120 random free runes from Lundail once per day"),
			new UnlockReward("Able to choose your destination when teleporting through the Ancient Obelisks"),
			new UnlockReward("50% off entry to Resource Area (3,750gp)"),
			new UnlockReward("Wine of zamorak found in the Chaos Temple (hut) and Deep Wilderness Dungeon will be received in noted form"),
			new UnlockReward("The teleport delay in both the Revenant Caves and the Wilderness boss caves will no longer apply to you"),
			new UnlockReward("25% more loot when opening rogues' chests"),
			new UnlockReward("Ecumenical keys may be sold to the Lesser Fanatic in Edgeville for 61,500 coins per key")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails airSteps = new PanelDetails("Air Orb", Arrays.asList(moveToEdge, moveToAir, airOrb),
			new SkillRequirement(Skill.MAGIC, 60, true), airRune.quantity(30), cosmicRune.quantity(3), unpoweredOrb);
		airSteps.setDisplayCondition(notAirOrb);
		airSteps.setLockingStep(airOrbTask);
		allSteps.add(airSteps);

		PanelDetails sallySteps = new PanelDetails("Black Salamander", Collections.singletonList(blackSally),
			new SkillRequirement(Skill.HUNTER, 67, true), smallFishingNet, rope);
		sallySteps.setDisplayCondition(notBlackSally);
		sallySteps.setLockingStep(blackSallyTask);
		allSteps.add(sallySteps);

		PanelDetails lavaSteps = new PanelDetails("Lava Dragon and Bury", Collections.singletonList(lavaDrag),
			combatGear, food);
		lavaSteps.setDisplayCondition(notLavaDrag);
		lavaSteps.setLockingStep(lavaDragTask);
		allSteps.add(lavaSteps);

		PanelDetails rawLavaEelSteps = new PanelDetails("Fishing a Raw Lava Eel", Collections.singletonList(rawLavaEel),
			new SkillRequirement(Skill.FISHING, 53, true), oilyRod, fishingBait, knife);
		rawLavaEelSteps.setDisplayCondition(notRawLavaEel);
		rawLavaEelSteps.setLockingStep(rawLavaEelTask);
		allSteps.add(rawLavaEelSteps);

		PanelDetails warrSteps = new PanelDetails("Spiritual Warrior", Arrays.asList(moveToGodWars1, moveToGodWars2,
			sprirtualWarrior),
			enterGodwars, new SkillRequirement(Skill.SLAYER, 68, true), combatGear, food, godEquip);
		warrSteps.setDisplayCondition(notSprirtualWarrior);
		warrSteps.setLockingStep(sprirtualWarriorTask);
		allSteps.add(warrSteps);

		PanelDetails trollSteps = new PanelDetails("Agility Shortcut", Collections.singletonList(trollWildy),
			new SkillRequirement(Skill.AGILITY, 64, true), deathPlateau);
		trollSteps.setDisplayCondition(notTrollWildy);
		trollSteps.setLockingStep(trollWildyTask);
		allSteps.add(trollSteps);

		PanelDetails scimSteps = new PanelDetails("Adamant Scimitar in Resource Area", Arrays.asList(moveToResource,
			addyScim), new SkillRequirement(Skill.SMITHING, 75, true), barsOrPick, hammer);
		scimSteps.setDisplayCondition(notAddyScim);
		scimSteps.setLockingStep(addyScimTask);
		allSteps.add(scimSteps);

		PanelDetails chaosSteps = new PanelDetails("Chaos Elemental", Collections.singletonList(chaosEle), combatGear, food);
		chaosSteps.setDisplayCondition(notChaosEle);
		chaosSteps.setLockingStep(chaosEleTask);
		allSteps.add(chaosSteps);

		PanelDetails bossesSteps = new PanelDetails("Three Bosses", Collections.singletonList(threeBosses), combatGear,
			food);
		bossesSteps.setDisplayCondition(notThreeBosses);
		bossesSteps.setLockingStep(threeBossesTask);
		allSteps.add(bossesSteps);

		PanelDetails godSpellSteps = new PanelDetails("God Spell on Player", Collections.singletonList(godSpells),
			new SkillRequirement(Skill.MAGIC, 60, false), godStaff, godRunes);
		godSpellSteps.setDisplayCondition(notGodSpells);
		godSpellSteps.setLockingStep(godSpellsTask);
		allSteps.add(godSpellSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
