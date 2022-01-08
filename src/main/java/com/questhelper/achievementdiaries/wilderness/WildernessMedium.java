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
import com.questhelper.requirements.ComplexRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
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
	quest = QuestHelperQuest.WILDERNESS_MEDIUM
)
public class WildernessMedium extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement combatGear, pickaxe, runeAxe, antiDragonShield, unpoweredOrb, cosmicRune, earthRune, coins,
		goldBar, goldOre, hammer, muddyKey, godEquip, knife, goldHelmet, barsOrPick;

	Requirement enterGodwars;

	// Items recommended
	ItemRequirement food, burningAmulet, gamesNeck;

	// Quests required
	Requirement betweenARock;

	Requirement notMineMith, notEntYew, notWildyGodWars, notWildyAgi, notKillGreenDrag, notKillAnkou,
		notWildyGWBloodveld, notEmblemTrader, notGoldHelm, notMuddyChest, notEarthOrb;

	QuestStep claimReward, mineMith, wildyAgi, killAnkou, wildyGWBloodveld, emblemTrader, goldHelm, muddyChest,
		earthOrb, moveToResource, moveToGodWars, mineGoldOre, smeltGoldOre, moveToEdge, moveToSlayer1, moveToSlayer2,
		wildyGodwars;

	NpcStep entYew, killGreenDrag;

	Zone resource, godWars1, godWars2, slayer, edge;

	ZoneRequirement inResource, inGodWars1, inGodWars2, inSlayer, inEdge;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doMedium = new ConditionalStep(this, claimReward);

		doMedium.addStep(notEntYew, entYew);
		doMedium.addStep(new Conditions(notKillAnkou, inSlayer), killAnkou);
		doMedium.addStep(notKillAnkou, moveToSlayer1);
		doMedium.addStep(new Conditions(notKillGreenDrag, inSlayer), killGreenDrag);
		doMedium.addStep(notKillGreenDrag, moveToSlayer2);
		doMedium.addStep(new Conditions(notWildyGodWars, inGodWars1), wildyGodwars);
		doMedium.addStep(notWildyGodWars, moveToGodWars);
		doMedium.addStep(new Conditions(notWildyGWBloodveld, inGodWars2), wildyGWBloodveld);
		doMedium.addStep(new Conditions(notWildyGWBloodveld, inGodWars1), wildyGodwars);
		doMedium.addStep(notWildyGWBloodveld, moveToGodWars);
		doMedium.addStep(notEmblemTrader, emblemTrader);
		doMedium.addStep(new Conditions(notEarthOrb, inEdge), earthOrb);
		doMedium.addStep(notEarthOrb, moveToEdge);
		doMedium.addStep(notMineMith, mineMith);
		doMedium.addStep(notWildyAgi, wildyAgi);
		doMedium.addStep(new Conditions(notGoldHelm, inResource, goldBar.quantity(3).alsoCheckBank(questBank)), goldHelm);
		doMedium.addStep(new Conditions(notGoldHelm, inResource, goldOre.quantity(3)), smeltGoldOre);
		doMedium.addStep(new Conditions(notGoldHelm, inResource), mineGoldOre);
		doMedium.addStep(notGoldHelm, moveToResource);
		doMedium.addStep(notMuddyChest, muddyChest);

		return doMedium;
	}

	public void setupRequirements()
	{
		notMineMith = new VarplayerRequirement(1192, false, 13);
		notEntYew = new VarplayerRequirement(1192, false, 14);
		notWildyGodWars = new VarplayerRequirement(1192, false, 15);
		notWildyAgi = new VarplayerRequirement(1192, false, 16);
		notKillGreenDrag = new VarplayerRequirement(1192, false, 18);
		notKillAnkou = new VarplayerRequirement(1192, false, 19);
		notEarthOrb = new VarplayerRequirement(1192, false, 20);
		notWildyGWBloodveld = new VarplayerRequirement(1192, false, 21);
		notEmblemTrader = new VarplayerRequirement(1192, false, 22);
		notGoldHelm = new VarplayerRequirement(1192, false, 23);
		notMuddyChest = new VarplayerRequirement(1192, false, 24);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		runeAxe = new ItemRequirement("Rune axe or better", ItemCollections.getRuneAxeBetter())
			.showConditioned(notEntYew);
		antiDragonShield = new ItemRequirement("Anti-dragon shield", ItemCollections.getAntifireShields())
			.showConditioned(notKillGreenDrag);
		godEquip = new ItemRequirement("Various god equipment (1 of each god suggested)", -1, -1)
			.showConditioned(notWildyGWBloodveld);
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes())
			.showConditioned(notMineMith);
		unpoweredOrb = new ItemRequirement("Unpowered orb", ItemID.UNPOWERED_ORB).showConditioned(notEarthOrb);
		cosmicRune = new ItemRequirement("Cosmic rune", ItemID.COSMIC_RUNE).showConditioned(notEarthOrb);
		earthRune = new ItemRequirement("Earth rune", ItemID.EARTH_RUNE).showConditioned(notEarthOrb);
		knife = new ItemRequirement("Knife or slashing weapon", -1, -1);
		muddyKey = new ItemRequirement("Muddy key", ItemID.MUDDY_KEY).showConditioned(notMuddyChest);
		goldHelmet = new ItemRequirement("Golden helmet not in inventory or bank (make sure this is red)",
			ItemID.GOLD_HELMET).showConditioned(notGoldHelm);
		coins = new ItemRequirement("Coins", ItemCollections.getCoins()).showConditioned(notGoldHelm);
		goldBar = new ItemRequirement("Gold bar", ItemID.GOLD_BAR).showConditioned(notGoldHelm);
		goldOre = new ItemRequirement("Gold ore", ItemID.GOLD_ORE);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notGoldHelm);
		barsOrPick = new ItemRequirements(LogicType.OR, "3 gold bars or a pickaxe", goldBar.quantity(3), pickaxe);

		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);
		burningAmulet = new ItemRequirement("Burning amulet", ItemCollections.getBurningAmulets());
		gamesNeck = new ItemRequirement("Games necklace", ItemCollections.getGamesNecklaces());

		enterGodwars = new ComplexRequirement(LogicType.OR,"60 Strength or Agility",
			new SkillRequirement(Skill.AGILITY,	60),
			new SkillRequirement(Skill.STRENGTH, 60));

		inEdge = new ZoneRequirement(edge);
		inResource = new ZoneRequirement(resource);
		inGodWars1 = new ZoneRequirement(godWars1);
		inGodWars2 = new ZoneRequirement(godWars2);
		inSlayer = new ZoneRequirement(slayer);

		betweenARock = new QuestRequirement(QuestHelperQuest.BETWEEN_A_ROCK, QuestState.IN_PROGRESS,
			"Schematic to make gold helmet in Between a Rock");
	}

	public void loadZones()
	{
		resource = new Zone(new WorldPoint(3174, 3944, 0), new WorldPoint(3196, 3924, 0));
		godWars1 = new Zone(new WorldPoint(3046, 10177, 3), new WorldPoint(3076, 10138, 3));
		godWars2 = new Zone(new WorldPoint(3014, 10168, 0), new WorldPoint(3069, 10115, 0));
		slayer = new Zone(new WorldPoint(3327, 10165, 0), new WorldPoint(3456, 10043, 0));
		edge = new Zone(new WorldPoint(3067, 10000, 0), new WorldPoint(3288, 9821, 0));
	}

	public void setupSteps()
	{

		entYew = new NpcStep(this, NpcID.ENT, new WorldPoint(3227, 3666, 0),
			"Kill an Ent in the wilderness and cut yew logs from its trunk after killing it.", combatGear, runeAxe);
		entYew.addAlternateNpcs(NpcID.ENT_TRUNK);

		moveToSlayer1 = new ObjectStep(this, ObjectID.STAIRS_40388, new WorldPoint(3260, 3665, 0),
			"Enter the Wilderness Slayer Cave.", combatGear, food);
		killAnkou = new NpcStep(this, NpcID.ANKOU_7864, new WorldPoint(3373, 10073, 0),
			"Kill an Ankou in the Wilderness Slayer Cave.", true, combatGear, food);

		moveToSlayer2 = new ObjectStep(this, ObjectID.STAIRS_40388, new WorldPoint(3260, 3665, 0),
			"Enter the Wilderness Slayer Cave.", combatGear, food, antiDragonShield);
		killGreenDrag = new NpcStep(this, NpcID.GREEN_DRAGON_7868, new WorldPoint(3412, 10066, 0),
			"Kill a Green dragon in the Wilderness Slayer Cave.", true, combatGear, food, antiDragonShield);
		killGreenDrag.addAlternateNpcs(NpcID.GREEN_DRAGON_7869, NpcID.GREEN_DRAGON_7870);

		moveToGodWars = new ObjectStep(this, ObjectID.CAVE_26766, new WorldPoint(3018, 3739, 0),
			"Enter the Wilderness God Wars Dungeon.", combatGear, food, godEquip);
		wildyGodwars = new ObjectStep(this, ObjectID.CREVICE_26767, new WorldPoint(3066, 10142, 0),
			"Use the crevice to enter the Wilderness God Wars Dungeon. The Strength entrance is to the West.",
			combatGear, food, godEquip);
		wildyGWBloodveld = new NpcStep(this, NpcID.BLOODVELD_3138, new WorldPoint(3050, 10131, 0),
			"Kill a Bloodveld in the Wilderness God Wars Dungeon.", combatGear, food, godEquip);

		mineMith = new ObjectStep(this, ObjectID.ROCKS_11373, new WorldPoint(3057, 3944, 0),
			"Mine mithril in the Wilderness.", pickaxe);

		wildyAgi = new ObjectStep(this, ObjectID.DOOR_23555, new WorldPoint(2998, 3917, 0),
			"Complete a lap of the Wilderness Agility Course.");

		moveToEdge = new ObjectStep(this, ObjectID.TRAPDOOR_1581, new WorldPoint(3097, 3468, 0),
			"Enter to the Edgeville Dungeon.");
		earthOrb = new ObjectStep(this, ObjectID.OBELISK_OF_EARTH, new WorldPoint(3087, 9933, 0),
			"Cast charge earth orb on the Obelisk of Earth.", unpoweredOrb, earthRune.quantity(30),
			cosmicRune.quantity(3));

		emblemTrader = new NpcStep(this, NpcID.EMBLEM_TRADER, new WorldPoint(3097, 3504, 0),
			"Speak with the Emblem Trader.");

		goldHelm = new ObjectStep(this, ObjectID.ANVIL_2097, new WorldPoint(3190, 3938, 0),
			"Smith the gold helmet in the Resource Area. If you already have one in your bank you will need to drop " +
				"it first.", hammer, goldBar.quantity(3));
		moveToResource = new ObjectStep(this, ObjectID.GATE_26760, new WorldPoint(3184, 3944, 0),
			"Enter the Wilderness Resource Area.", coins.quantity(7500), hammer, barsOrPick);
		smeltGoldOre = new ObjectStep(this, ObjectID.FURNACE_26300, new WorldPoint(3191, 3936, 0),
			"Smelt the gold ore into gold bars.", hammer, goldOre.quantity(3));
		mineGoldOre = new ObjectStep(this, ObjectID.ROCKS_11370, new WorldPoint(3184, 3941, 0),
			"Mine gold ore.", true, hammer, pickaxe);

		muddyChest = new ObjectStep(this, ObjectID.CLOSED_CHEST_170, new WorldPoint(3089, 3859, 0),
			"Use a Muddy key on the chest in the Lava Maze.", muddyKey, knife);

		claimReward = new NpcStep(this, NpcID.LESSER_FANATIC, new WorldPoint(3121, 3518, 0),
			"Talk to Lesser Fanatic in Edgeville to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(unpoweredOrb, cosmicRune.quantity(3), earthRune.quantity(30), pickaxe, antiDragonShield,
			runeAxe, combatGear, barsOrPick, hammer, coins.quantity(7500), knife, muddyKey);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, gamesNeck, burningAmulet);
	}


	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(enterGodwars);
		reqs.add(new SkillRequirement(Skill.AGILITY, 52));
		reqs.add(new SkillRequirement(Skill.MAGIC, 60));
		reqs.add(new SkillRequirement(Skill.MINING, 55));
		reqs.add(new SkillRequirement(Skill.SLAYER, 50));
		reqs.add(new SkillRequirement(Skill.SMITHING, 50));
		reqs.add(new SkillRequirement(Skill.WOODCUTTING, 61));

		reqs.add(betweenARock);

		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Bloodveld (lvl 81)", "Green dragon (lvl 88)", "Ankou (lvl 98)",
			"Ent (lvl 101)");
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Wilderness Sword 2", ItemID.WILDERNESS_SWORD_2),
			new ItemReward("7,500 Exp. Lamp (Any skill over 40)", ItemID.ANTIQUE_LAMP)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Increases the chance of a successful yield from ents by 15%"),
			new UnlockReward("20% off entry to Resource Area (6000gp)"),
			new UnlockReward("Can have 4 ecumenical keys at a time"),
			new UnlockReward("20 random free runes from Lundail once per day"),
			new UnlockReward("Access to the shortcut in the Deep Wilderness Dungeon (requires Agility 46 )")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails entSteps = new PanelDetails("Ent Yew", Collections.singletonList(entYew),
			new SkillRequirement(Skill.WOODCUTTING, 61), combatGear, food, runeAxe);
		entSteps.setDisplayCondition(notEntYew);
		allSteps.add(entSteps);

		PanelDetails ankouSteps = new PanelDetails("Kill Ankou", Arrays.asList(moveToSlayer1, killAnkou), combatGear,
			food);
		ankouSteps.setDisplayCondition(notKillAnkou);
		allSteps.add(ankouSteps);

		PanelDetails greenDragSteps = new PanelDetails("Kill Green Dragon", Arrays.asList(moveToSlayer2,
			killGreenDrag), combatGear, food, antiDragonShield);
		greenDragSteps.setDisplayCondition(notKillGreenDrag);
		allSteps.add(greenDragSteps);

		PanelDetails godWarsSteps = new PanelDetails("Enter Wilderness God Wars", Arrays.asList(moveToGodWars,
			wildyGodwars));
		godWarsSteps.setDisplayCondition(notWildyGodWars);
		allSteps.add(godWarsSteps);

		PanelDetails bloodveldSteps = new PanelDetails("Kill Bloodveld in God Wars Dungeon",
			Arrays.asList(moveToGodWars, wildyGodwars, wildyGWBloodveld), new SkillRequirement(Skill.SLAYER, 50),
			enterGodwars, combatGear, food, godEquip);
		bloodveldSteps.setDisplayCondition(notWildyGWBloodveld);
		allSteps.add(bloodveldSteps);

		PanelDetails emblemSteps = new PanelDetails("Emblem Trader", Collections.singletonList(emblemTrader));
		emblemSteps.setDisplayCondition(notEmblemTrader);
		allSteps.add(emblemSteps);

		PanelDetails earthOrbSteps = new PanelDetails("Earth Orb", Arrays.asList(moveToEdge, earthOrb),
			new SkillRequirement(Skill.MAGIC, 60), unpoweredOrb, earthRune.quantity(30), cosmicRune.quantity(3));
		earthOrbSteps.setDisplayCondition(notEarthOrb);
		allSteps.add(earthOrbSteps);

		PanelDetails mithSteps = new PanelDetails("Mine Mithril", Collections.singletonList(mineMith),
			new SkillRequirement(Skill.MINING, 55), pickaxe, knife);
		mithSteps.setDisplayCondition(notMineMith);
		allSteps.add(mithSteps);

		PanelDetails wildyAgiSteps = new PanelDetails("Wilderness Agility Course",
			Collections.singletonList(wildyAgi), new SkillRequirement(Skill.AGILITY, 52), knife);
		wildyAgiSteps.setDisplayCondition(notWildyAgi);
		allSteps.add(wildyAgiSteps);

		PanelDetails goldHelmSteps = new PanelDetails("Gold Helmet in Resource Area", Arrays.asList(moveToResource,
			mineGoldOre, smeltGoldOre, goldHelm), new SkillRequirement(Skill.SMITHING, 50), betweenARock,
			coins.quantity(7500), barsOrPick, hammer, knife);
		goldHelmSteps.setDisplayCondition(notGoldHelm);
		allSteps.add(goldHelmSteps);

		PanelDetails chestSteps = new PanelDetails("Muddy Chest", Collections.singletonList(muddyChest), muddyKey,
			knife);
		chestSteps.setDisplayCondition(notMuddyChest);
		allSteps.add(chestSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
