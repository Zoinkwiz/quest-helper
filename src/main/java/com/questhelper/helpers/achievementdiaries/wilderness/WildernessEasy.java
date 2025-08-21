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
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.requirements.var.VarbitRequirement;
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
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.gameval.VarPlayerID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WildernessEasy extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement combatGear, chaosAccess, pickaxe, teamCape, redSpiderEggs, alchable;

	// Items recommended
	ItemRequirement food, burningAmulet, oneClick;

	// Quests required
	Requirement enterTheAbyss, firstTimeAbyss;

	Requirement notKillMammoth, notChaosAltar, notLowAlch, notChaosTemple, notWildyLever, notEarthWarrior,
		notDemonicPrayer, notEnterKBDLair, notSpiderEggs, notEnterAbyss, notIronOre, notEquipTeamCape, normalBook;

	QuestStep claimReward, killMammoth, chaosAltar, lowAlch, chaosTemple, wildyLever, earthWarrior,
		demonicPrayer, enterKBDLair, spiderEggs, enterAbyss, ironOre, equipTeamCape, moveToWildy, moveToFount,
		moveToEdgeSpider, moveToEdgeEarth, abyssEnable, enterKBDLair2;

	Zone wildy, fount, edge, eggs, kbd;

	ZoneRequirement inWildy, inFount, inEdge, inEggs, inKbd;

	ConditionalStep killMammothTask, chaosAltarTask, lowAlchTask, chaosTempleTask, wildyLeverTask, earthWarriorTask,
		demonicPrayerTask, enterKBDLairTask, spiderEggsTask, enterAbyssTask, ironOreTask, equipTeamCapeTask;

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupSteps();

		ConditionalStep doEasy = new ConditionalStep(this, claimReward);

		equipTeamCapeTask = new ConditionalStep(this, moveToWildy);
		equipTeamCapeTask.addStep(inWildy, equipTeamCape);
		doEasy.addStep(notEquipTeamCape, equipTeamCapeTask);

		spiderEggsTask = new ConditionalStep(this, moveToEdgeSpider);
		spiderEggsTask.addStep(new Conditions(LogicType.OR, inEdge, inEggs), spiderEggs);
		doEasy.addStep(notSpiderEggs, spiderEggsTask);

		earthWarriorTask = new ConditionalStep(this, moveToEdgeEarth);
		earthWarriorTask.addStep(inEdge, earthWarrior);
		doEasy.addStep(notEarthWarrior, earthWarriorTask);

		wildyLeverTask = new ConditionalStep(this, wildyLever);
		doEasy.addStep(notWildyLever, wildyLeverTask);

		ironOreTask = new ConditionalStep(this, ironOre);
		doEasy.addStep(notIronOre, ironOreTask);

		killMammothTask = new ConditionalStep(this, killMammoth);
		doEasy.addStep(notKillMammoth, killMammothTask);

		chaosTempleTask = new ConditionalStep(this, chaosTemple);
		doEasy.addStep(notChaosTemple, chaosTempleTask);

		lowAlchTask = new ConditionalStep(this, moveToFount);
		lowAlchTask.addStep(inFount, lowAlch);
		doEasy.addStep(notLowAlch, lowAlchTask);

		demonicPrayerTask = new ConditionalStep(this, demonicPrayer);
		doEasy.addStep(notDemonicPrayer, demonicPrayerTask);

		chaosAltarTask = new ConditionalStep(this, chaosAltar);
		doEasy.addStep(notChaosAltar, chaosAltarTask);

		enterKBDLairTask = new ConditionalStep(this, enterKBDLair);
		enterKBDLairTask.addStep(inKbd, enterKBDLair2);
		doEasy.addStep(notEnterKBDLair, enterKBDLairTask);

		enterAbyssTask = new ConditionalStep(this, abyssEnable);
		enterAbyssTask.addStep(firstTimeAbyss, enterAbyss);
		doEasy.addStep(notEnterAbyss, enterAbyssTask);

		return doEasy;
	}

	@Override
	protected void setupRequirements()
	{
		notLowAlch = new VarplayerRequirement(VarPlayerID.WILDERNESS_ACHIEVEMENT_DIARY, false, 1);
		notWildyLever = new VarplayerRequirement(VarPlayerID.WILDERNESS_ACHIEVEMENT_DIARY, false, 2);
		notChaosAltar = new VarplayerRequirement(VarPlayerID.WILDERNESS_ACHIEVEMENT_DIARY, false, 3);
		notChaosTemple = new VarplayerRequirement(VarPlayerID.WILDERNESS_ACHIEVEMENT_DIARY, false, 4);
		notKillMammoth = new VarplayerRequirement(VarPlayerID.WILDERNESS_ACHIEVEMENT_DIARY, false, 5);
		notEarthWarrior = new VarplayerRequirement(VarPlayerID.WILDERNESS_ACHIEVEMENT_DIARY, false, 6);
		notDemonicPrayer = new VarplayerRequirement(VarPlayerID.WILDERNESS_ACHIEVEMENT_DIARY, false, 7);
		notEnterKBDLair = new VarplayerRequirement(VarPlayerID.WILDERNESS_ACHIEVEMENT_DIARY, false, 8);
		notSpiderEggs = new VarplayerRequirement(VarPlayerID.WILDERNESS_ACHIEVEMENT_DIARY, false, 9);
		notIronOre = new VarplayerRequirement(VarPlayerID.WILDERNESS_ACHIEVEMENT_DIARY, false, 10);
		notEnterAbyss = new VarplayerRequirement(VarPlayerID.WILDERNESS_ACHIEVEMENT_DIARY, false, 11);
		notEquipTeamCape = new VarplayerRequirement(VarPlayerID.WILDERNESS_ACHIEVEMENT_DIARY, false, 12);

		firstTimeAbyss = new VarbitRequirement(VarbitID.RCU_ABYSSAL_WARNING, 1);
		normalBook = new SpellbookRequirement(Spellbook.NORMAL);

		chaosAccess = new ItemRequirement("Access to the Chaos altar",
			ItemCollections.CHAOS_ALTAR).showConditioned(notChaosTemple).isNotConsumed();
		chaosAccess.setTooltip("Chaos Talisman/Tiara, Catalytic Talisman/Tiara, RC-skill cape or travel through Abyss");
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).showConditioned(notIronOre).isNotConsumed();
		teamCape = new ItemRequirement("Any team cape", ItemCollections.TEAM_CAPE).showConditioned(notEquipTeamCape).isNotConsumed();
		redSpiderEggs = new ItemRequirement("Red spider eggs", ItemID.RED_SPIDERS_EGGS).showConditioned(notSpiderEggs);
		alchable = new ItemRequirement("Any item that is alch-able", 1, -1);

		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		burningAmulet = new ItemRequirement("Burning amulet", ItemCollections.BURNING_AMULETS);
		oneClick = new ItemRequirement("A one click teleport or accept imminent death", 1, -1)
			.showConditioned(notEnterKBDLair);

		inWildy = new ZoneRequirement(wildy);
		inEdge = new ZoneRequirement(edge);
		inFount = new ZoneRequirement(fount);
		inEggs = new ZoneRequirement(eggs);
		inKbd = new ZoneRequirement(kbd);

		enterTheAbyss = new QuestRequirement(QuestHelperQuest.ENTER_THE_ABYSS, QuestState.FINISHED);
	}

	@Override
	protected void setupZones()
	{
		wildy = new Zone(new WorldPoint(2943, 3978, 0), new WorldPoint(3393, 3522, 0));
		edge = new Zone(new WorldPoint(3067, 10000, 0), new WorldPoint(3288, 9821, 0));
		fount = new Zone(new WorldPoint(3366, 3902, 0), new WorldPoint(3380, 3890, 0));
		eggs = new Zone(new WorldPoint(3113, 9962, 0), new WorldPoint(3132, 9946, 0));
		kbd = new Zone(new WorldPoint(3008, 10293, 0), new WorldPoint(3075, 10238, 0));
	}

	public void setupSteps()
	{
		killMammoth = new NpcStep(this, NpcID.WILDERNESS_MAMMOTH, new WorldPoint(3164, 3593, 0),
			"Kill a Mammoth in the Wilderness.", combatGear);

		moveToEdgeSpider = new ObjectStep(this, ObjectID.TRAPDOOR_OPEN, new WorldPoint(3097, 3468, 0),
			"Enter the Edgeville dungeon.", food);

		moveToEdgeEarth = new ObjectStep(this, ObjectID.TRAPDOOR_OPEN, new WorldPoint(3097, 3468, 0),
			"Enter the Edgeville dungeon.", combatGear, food);

		moveToWildy = new DetailedQuestStep(this, "Enter the Wilderness.", teamCape);
		equipTeamCape = new DetailedQuestStep(this, "Equip a team cape. If you already have one on, re-equip it.",
			teamCape.equipped());

		chaosTemple = new ObjectStep(this, 34822, new WorldPoint(3060, 3591, 0),
			"Enter the chaos altar north of Edgeville with a chaos talisman/tiara, or enter it through the Abyss.");
		chaosTemple.addIcon(ItemID.CHAOS_TALISMAN);

		moveToFount = new DetailedQuestStep(this, new WorldPoint(3374, 3893, 0),
			"Go to the Fountain of Rune with an item you can cast Low Alchemy on.");
		lowAlch = new ObjectStep(this, ObjectID.FOUNTAIN_OF_RUNE_OBELISK_DESKTOP, new WorldPoint(3374, 3893, 0),
			"Cast Low Alchemy on anything.");

		abyssEnable = new NpcStep(this, NpcID.RCU_ZAMMY_MAGE1_EDGEB, new WorldPoint(3259, 3385, 0),
			"Speak with the Mage of Zamorak in Varrock and ask him about the Abyss.");
		enterAbyss = new NpcStep(this, NpcID.RCU_ZAMMY_MAGE1B, new WorldPoint(3105, 3556, 0),
			"Speak with the Mage of Zamorak in the Wilderness to teleport to the Abyss.");

		ironOre = new ObjectStep(this, ObjectID.IRONROCK1, new WorldPoint(3104, 3570, 0),
			"Mine iron in the Wilderness.", pickaxe);

		spiderEggs = new ItemStep(this, new WorldPoint(3122, 9953, 0), "Pickup 5 red spider eggs in the Edgeville " +
			"Wilderness Dungeon.", redSpiderEggs);

		earthWarrior = new NpcStep(this, NpcID.EARTHWARRIOR, new WorldPoint(3121, 9972, 0),
			"Kill an Earth warrior in the north of the Edgeville Wilderness Dungeon.", combatGear, food);

		wildyLever = new ObjectStep(this, ObjectID.EDGEVILLE_WILDY_LEVER, new WorldPoint(3090, 3475, 0),
			"Pull the Lever in Edgeville. This will take you to DEEP Wilderness, bank anything you aren't willing to lose.");

		demonicPrayer = new DetailedQuestStep(this, new WorldPoint(3288, 3886, 0),
			"Stand in the Demonic Ruins until the ruins automatically restore a prayer point for you. You must be at " +
				"less than your max prayer points.");

		chaosAltar = new ObjectStep(this, ObjectID.CHAOSALTAR, new WorldPoint(2947, 3821, 0),
			"Pray at the Chaos Altar.");

		enterKBDLair = new ObjectStep(this, ObjectID.WILDYMIRRORLADDERTOP1, new WorldPoint(3017, 3849, 0),
			"Climb down the ladder that leads to the King Black Dragon Lair.", oneClick);
		enterKBDLair2 = new ObjectStep(this, ObjectID.DRAGONKINGINLEVER, new WorldPoint(3067, 10253, 0),
			"Pull the lever to enter the King Black Dragon lair.", oneClick);

		claimReward = new NpcStep(this, NpcID.LESSER_FANATIC_DIARY, new WorldPoint(3121, 3518, 0),
			"Talk to the Lesser Fanatic in Edgeville to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear, pickaxe, teamCape, chaosAccess, alchable);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, burningAmulet, oneClick);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.AGILITY, 15, true));
		reqs.add(new SkillRequirement(Skill.MAGIC, 21, true));
		reqs.add(new SkillRequirement(Skill.MINING, 15, true));

		reqs.add(enterTheAbyss);

		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Kill an Earth Warrior (lvl 51) and a Mammoth (lvl 80).");
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Wilderness Sword 1", ItemID.WILDERNESS_SWORD_EASY),
			new ItemReward("2,500 Exp. Lamp (Any skill over 30)", ItemID.THOSF_REWARD_LAMP)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Wilderness lever can teleport you to either Edgeville or Ardougne"),
			new UnlockReward("40 random free runes from Lundail once per day")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		/* Edgeville-region tasks */
		PanelDetails teamCapeSteps = new PanelDetails("Team Cape", Arrays.asList(moveToWildy, equipTeamCape), teamCape);
		teamCapeSteps.setDisplayCondition(notEquipTeamCape);
		teamCapeSteps.setLockingStep(equipTeamCapeTask);
		allSteps.add(teamCapeSteps);

		PanelDetails eggsSteps = new PanelDetails("Spider Eggs", Arrays.asList(moveToEdgeSpider, spiderEggs), food);
		eggsSteps.setDisplayCondition(notSpiderEggs);
		eggsSteps.setLockingStep(spiderEggsTask);
		allSteps.add(eggsSteps);

		PanelDetails earthWarriorSteps = new PanelDetails("Earth Warrior", Arrays.asList(moveToEdgeEarth, earthWarrior),
			new SkillRequirement(Skill.AGILITY, 15, true), combatGear, food);
		earthWarriorSteps.setDisplayCondition(notEarthWarrior);
		earthWarriorSteps.setLockingStep(earthWarriorTask);
		allSteps.add(earthWarriorSteps);

		PanelDetails leverSteps = new PanelDetails("Wilderness Lever", Collections.singletonList(wildyLever));
		leverSteps.setDisplayCondition(notWildyLever);
		leverSteps.setLockingStep(wildyLeverTask);
		allSteps.add(leverSteps);

		/* Starting to go up from Edgeville */
		PanelDetails ironOreSteps = new PanelDetails("Iron Ore", Collections.singletonList(ironOre),
			new SkillRequirement(Skill.MINING, 15, true), pickaxe);
		ironOreSteps.setDisplayCondition(notIronOre);
		ironOreSteps.setLockingStep(ironOreTask);
		allSteps.add(ironOreSteps);

		PanelDetails mammothSteps = new PanelDetails("Mammoth", Collections.singletonList(killMammoth), combatGear,
			food);
		mammothSteps.setDisplayCondition(notKillMammoth);
		mammothSteps.setLockingStep(killMammothTask);
		allSteps.add(mammothSteps);

		PanelDetails chaosSteps = new PanelDetails("Chaos RC Altar", Collections.singletonList(chaosTemple),
			chaosAccess);
		chaosSteps.setDisplayCondition(notChaosTemple);
		chaosSteps.setLockingStep(chaosTempleTask);
		allSteps.add(chaosSteps);

		PanelDetails alchSteps = new PanelDetails("Free Low Alchemy", Arrays.asList(moveToFount, lowAlch),
			new SkillRequirement(Skill.MAGIC, 21, true), normalBook, alchable);
		alchSteps.setDisplayCondition(notLowAlch);
		alchSteps.setLockingStep(lowAlchTask);
		allSteps.add(alchSteps);

		PanelDetails ruinsSteps = new PanelDetails("Demonic Ruins", Collections.singletonList(demonicPrayer));
		ruinsSteps.setDisplayCondition(notDemonicPrayer);
		ruinsSteps.setLockingStep(demonicPrayerTask);
		allSteps.add(ruinsSteps);

		PanelDetails altarSteps = new PanelDetails("Pray at Chaos Altar", Collections.singletonList(chaosAltar));
		altarSteps.setDisplayCondition(notChaosAltar);
		altarSteps.setLockingStep(chaosAltarTask);
		allSteps.add(altarSteps);


		PanelDetails kbdSteps = new PanelDetails("The Lair", Arrays.asList(enterKBDLair, enterKBDLair2), oneClick);
		kbdSteps.setDisplayCondition(notEnterKBDLair);
		kbdSteps.setLockingStep(enterKBDLairTask);
		allSteps.add(kbdSteps);

		/* At end as it skulls the player */
		PanelDetails abyssSteps = new PanelDetails("Enter the Abyss", Arrays.asList(abyssEnable, enterAbyss),
			enterTheAbyss);
		abyssSteps.setDisplayCondition(notEnterAbyss);
		abyssSteps.setLockingStep(enterAbyssTask);
		allSteps.add(abyssSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
