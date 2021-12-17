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
import com.questhelper.QuestVarPlayer;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.requirements.var.VarbitRequirement;
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
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.WILDERNESS_EASY
)
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

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doEasy = new ConditionalStep(this, claimReward);
		doEasy.addStep(notKillMammoth, killMammoth);
		doEasy.addStep(notIronOre, ironOre);
		doEasy.addStep(new Conditions(notEnterAbyss, firstTimeAbyss), enterAbyss);
		doEasy.addStep(notEnterAbyss, abyssEnable);
		doEasy.addStep(notChaosTemple, chaosTemple);
		doEasy.addStep(new Conditions(notEquipTeamCape, inWildy), equipTeamCape);
		doEasy.addStep(notEquipTeamCape, moveToWildy);
		doEasy.addStep(new Conditions(notSpiderEggs, new Conditions(LogicType.OR, inEdge, inEggs)), spiderEggs);
		doEasy.addStep(notSpiderEggs, moveToEdgeSpider);
		doEasy.addStep(new Conditions(notEarthWarrior, inEdge), earthWarrior);
		doEasy.addStep(notEarthWarrior, moveToEdgeEarth);
		doEasy.addStep(notWildyLever, wildyLever);
		doEasy.addStep(new Conditions(notLowAlch, inFount), lowAlch);
		doEasy.addStep(notLowAlch, moveToFount);
		doEasy.addStep(notDemonicPrayer, demonicPrayer);
		doEasy.addStep(notChaosAltar, chaosAltar);
		doEasy.addStep(new Conditions(notEnterKBDLair, inKbd), enterKBDLair2);
		doEasy.addStep(notEnterKBDLair, enterKBDLair);

		return doEasy;
	}

	public void setupRequirements()
	{
		notLowAlch = new VarplayerRequirement(1192, false, 1);
		notWildyLever = new VarplayerRequirement(1192, false, 2);
		notChaosAltar = new VarplayerRequirement(1192, false, 3);
		notChaosTemple = new VarplayerRequirement(1192, false, 4);
		notKillMammoth = new VarplayerRequirement(1192, false, 5);
		notEarthWarrior = new VarplayerRequirement(1192, false, 6);
		notDemonicPrayer = new VarplayerRequirement(1192, false, 7);
		notEnterKBDLair = new VarplayerRequirement(1192, false, 8);
		notSpiderEggs = new VarplayerRequirement(1192, false, 9);
		notIronOre = new VarplayerRequirement(1192, false, 10);
		notEnterAbyss = new VarplayerRequirement(1192, false, 11);
		notEquipTeamCape = new VarplayerRequirement(1192, false, 12);

		firstTimeAbyss = new VarbitRequirement(626, 1);
		normalBook = new SpellbookRequirement(Spellbook.NORMAL);

		chaosAccess = new ItemRequirement("Access to Chaos altar, or travel through abyss",
			ItemCollections.getChaosAltar()).showConditioned(notChaosTemple);
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes()).showConditioned(notIronOre);
		teamCape = new ItemRequirement("Any team cape", ItemCollections.getTeamCape()).showConditioned(notEquipTeamCape);
		redSpiderEggs = new ItemRequirement("Red spider eggs", ItemID.RED_SPIDERS_EGGS).showConditioned(notSpiderEggs);
		alchable = new ItemRequirement("Any item that is alch-able", 1, -1);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);
		burningAmulet = new ItemRequirement("Burning amulet", ItemCollections.getBurningAmulets());
		oneClick = new ItemRequirement("A one click teleport or accept imminent death", 1, -1)
			.showConditioned(notEnterKBDLair);

		inWildy = new ZoneRequirement(wildy);
		inEdge = new ZoneRequirement(edge);
		inFount = new ZoneRequirement(fount);
		inEggs = new ZoneRequirement(eggs);
		inKbd = new ZoneRequirement(kbd);

		enterTheAbyss = new QuestRequirement(QuestHelperQuest.ENTER_THE_ABYSS, QuestState.FINISHED);
	}

	public void loadZones()
	{
		wildy = new Zone(new WorldPoint(2943, 3978, 0), new WorldPoint(3393, 3522, 0));
		edge = new Zone(new WorldPoint(3067, 10000, 0), new WorldPoint(3288, 9821, 0));
		fount = new Zone(new WorldPoint(3366, 3902, 0), new WorldPoint(3380, 3890, 0));
		eggs = new Zone(new WorldPoint(3113, 9962, 0), new WorldPoint(3132, 9946, 0));
		kbd = new Zone(new WorldPoint(3008, 10293, 0), new WorldPoint(3075, 10238, 0));
	}

	public void setupSteps()
	{
		killMammoth = new NpcStep(this, NpcID.MAMMOTH, new WorldPoint(3164, 3593, 0),
			"Kill a Mammoth in the Wilderness.", combatGear);

		moveToEdgeSpider = new ObjectStep(this, ObjectID.TRAPDOOR_1581, new WorldPoint(3097, 3468, 0),
			"Enter the Edgeville dungeon.", food);

		moveToEdgeEarth = new ObjectStep(this, ObjectID.TRAPDOOR_1581, new WorldPoint(3097, 3468, 0),
			"Enter the Edgeville dungeon.", combatGear, food);

		moveToWildy = new DetailedQuestStep(this, "Enter the Wilderness.", teamCape);
		equipTeamCape = new DetailedQuestStep(this, "Equip a team cape. If you already have one on, re-equip it.",
			teamCape.equipped());

		chaosTemple = new ObjectStep(this, 34822, new WorldPoint(3060, 3591, 0),
			"Enter the chaos altar north of Edgeville with a chaos talisman/tiara, or enter it through the Abyss.");
		chaosTemple.addIcon(ItemID.CHAOS_TALISMAN);

		moveToFount = new DetailedQuestStep(this, new WorldPoint(3373, 3893, 0), "Go to the Fountain of Rune, with an" +
			" item you can cast Low Alchemy on.");
		lowAlch = new DetailedQuestStep(this, "Cast Low Alchemy on anything. Be sure to bring something alch-able.");

		abyssEnable = new NpcStep(this, NpcID.MAGE_OF_ZAMORAK_2582, new WorldPoint(3259, 3385, 0),
			"Speak with the Mage of Zamorak in Varrock and ask him about the Abyss.");
		enterAbyss = new NpcStep(this, NpcID.MAGE_OF_ZAMORAK_2581, new WorldPoint(3105, 3556, 0),
			"Speak with the Mage of Zamorak in the Wilderness to teleport to the Abyss.");

		ironOre = new ObjectStep(this, ObjectID.ROCKS_11364, new WorldPoint(3104, 3570, 0),
			"Mine iron in the Wilderness.", pickaxe);

		spiderEggs = new ItemStep(this, new WorldPoint(3122, 9953, 0), "Pickup 5 red spider eggs in the Edgeville " +
			"Wilderness Dungeon.",	redSpiderEggs);

		earthWarrior = new NpcStep(this, NpcID.EARTH_WARRIOR, new WorldPoint(3121, 9972, 0),
			"Kill an Earth warrior in the north of the Edgeville Wilderness Dungeon.", combatGear, food);

		wildyLever = new ObjectStep(this, ObjectID.LEVER_26761, new WorldPoint(3090, 3475, 0),
			"Pull the Lever in Edgeville. This will take you to DEEP Wilderness, bank anything you aren't willing to lose.");

		demonicPrayer = new DetailedQuestStep(this, new WorldPoint(3288, 3886, 0),
			"Stand in the Demonic Ruins until the ruins automatically restore a prayer point for you. You must be at " +
				"less than your max prayer points.");

		chaosAltar = new ObjectStep(this, ObjectID.CHAOS_ALTAR_411, new WorldPoint(2947, 3821, 0),
			"Pray at the Chaos Altar.");

		enterKBDLair = new ObjectStep(this, ObjectID.LADDER_18987, new WorldPoint(3017, 3849, 0),
			"Climb down the ladder that leads to the King Black Dragon Lair.", oneClick);
		enterKBDLair2 = new ObjectStep(this, ObjectID.LEVER_1816, new WorldPoint(3067, 10253, 0),
			"Pull the lever to enter the King Black Dragon lair.", oneClick);

		claimReward = new NpcStep(this, NpcID.LESSER_FANATIC, new WorldPoint(3121, 3518, 0),
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
		reqs.add(new SkillRequirement(Skill.AGILITY, 15));
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
			new ItemReward("Wilderness Sword 1", ItemID.WILDERNESS_SWORD_1),
			new ItemReward("2,500 Exp. Lamp (Any skill over 30)", ItemID.ANTIQUE_LAMP)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Wilderness lever can teleport you to either Edgeville or Ardougne"),
			new UnlockReward("10 random free runes from Lundail once per day")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails mammothSteps = new PanelDetails("Mammoth", Collections.singletonList(killMammoth), combatGear,
			food);
		mammothSteps.setDisplayCondition(notKillMammoth);
		allSteps.add(mammothSteps);

		PanelDetails ironOreSteps = new PanelDetails("Iron Ore", Collections.singletonList(ironOre),
			new SkillRequirement(Skill.MINING, 15), pickaxe);
		ironOreSteps.setDisplayCondition(notIronOre);
		allSteps.add(ironOreSteps);

		PanelDetails abyssSteps = new PanelDetails("Enter the Abyss", Collections.singletonList(enterAbyss),
			enterTheAbyss);
		abyssSteps.setDisplayCondition(notEnterAbyss);
		allSteps.add(abyssSteps);

		PanelDetails chaosSteps = new PanelDetails("Chaos Temple", Collections.singletonList(chaosTemple), chaosAccess);
		chaosSteps.setDisplayCondition(notChaosTemple);
		allSteps.add(chaosSteps);

		PanelDetails teamCapeSteps = new PanelDetails("Team Cape", Collections.singletonList(equipTeamCape), teamCape);
		teamCapeSteps.setDisplayCondition(notEquipTeamCape);
		allSteps.add(teamCapeSteps);

		PanelDetails eggsSteps = new PanelDetails("Spider Eggs", Arrays.asList(moveToEdgeSpider, spiderEggs), food);
		eggsSteps.setDisplayCondition(notSpiderEggs);
		allSteps.add(eggsSteps);

		PanelDetails earthWarriorSteps = new PanelDetails("Earth Warrior", Arrays.asList(moveToEdgeEarth, earthWarrior),
			new SkillRequirement(Skill.AGILITY, 15), combatGear, food);
		earthWarriorSteps.setDisplayCondition(notEarthWarrior);
		allSteps.add(earthWarriorSteps);

		PanelDetails leverSteps = new PanelDetails("Wilderness Lever", Collections.singletonList(wildyLever));
		leverSteps.setDisplayCondition(notWildyLever);
		allSteps.add(leverSteps);

		PanelDetails alchSteps = new PanelDetails("Free Low Alchemy", Collections.singletonList(lowAlch),
			new SkillRequirement(Skill.MAGIC, 21), normalBook, alchable);
		alchSteps.setDisplayCondition(notLowAlch);
		allSteps.add(alchSteps);

		PanelDetails ruinsSteps = new PanelDetails("Demonic Ruins", Collections.singletonList(demonicPrayer));
		ruinsSteps.setDisplayCondition(notDemonicPrayer);
		allSteps.add(ruinsSteps);

		PanelDetails altarSteps = new PanelDetails("Pray at Chaos Altar", Collections.singletonList(chaosAltar));
		altarSteps.setDisplayCondition(notChaosAltar);
		allSteps.add(altarSteps);

		PanelDetails kbdSteps = new PanelDetails("The Lair", Collections.singletonList(enterKBDLair), oneClick);
		kbdSteps.setDisplayCondition(notEnterKBDLair);
		allSteps.add(kbdSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
