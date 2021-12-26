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
package com.questhelper.achievementdiaries.ardougne;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
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
	quest = QuestHelperQuest.ARDOUGNE_ELITE
)
public class ArdougneElite extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement combatGear, lockpick, yewLog, runeBar, hammer, knife, sinewOrRoot, salveAmmy,
		torstolSeed, seedDib, rake, compost, waterRune, bloodRune, deathRune, sinew, root, imbuedSalve, spade,
		runeCrossbowU, crossbowString, yewStock, runeLimbs;

	// Items recommended
	ItemRequirement food;

	// Quests required
	Requirement desertTreasure, hauntedMine, ancientBook, enoughNMZPoints, madeString, madeLimbs, madeStock, madeCrossU;

	Requirement notTrawlerRay, notYanAgi, notPickHero, notRuneCrossbow, notImbueSalve, notPickTorstol,
		notArdyRooftops, notIceBarrage;

	QuestStep claimReward, trawlerRay, yanAgi, pickHero, runeCrossbow, imbueSalve, pickTorstol,
		ardyRooftops, iceBarrage, moveToYanAgi, farmMorePoints, equipSalve, spinString, smithLimbs, fletchStock, moveToYan,
		makeUnstrungCross;

	Zone yanille, yanAgilityCave, witchaven;

	ZoneRequirement inYanille, inYanAgilityCave, inWitchaven;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doElite = new ConditionalStep(this, claimReward);
		doElite.addStep(notIceBarrage, iceBarrage);
		doElite.addStep(new Conditions(notYanAgi, inYanAgilityCave), yanAgi);
		doElite.addStep(notYanAgi, moveToYanAgi);
		doElite.addStep(new Conditions(notImbueSalve, imbuedSalve.alsoCheckBank(questBank)), equipSalve);
		doElite.addStep(new Conditions(notImbueSalve, enoughNMZPoints), imbueSalve);
		doElite.addStep(notImbueSalve, farmMorePoints);
		doElite.addStep(notArdyRooftops, ardyRooftops);
		doElite.addStep(notPickHero, pickHero);
		doElite.addStep(new Conditions(notRuneCrossbow, inYanille, madeCrossU, runeCrossbowU, crossbowString), runeCrossbow);
		doElite.addStep(new Conditions(notRuneCrossbow, inYanille, madeStock, crossbowString, runeLimbs, yewStock), makeUnstrungCross);
		doElite.addStep(new Conditions(notRuneCrossbow, inYanille, madeLimbs, crossbowString, runeLimbs), fletchStock);
		doElite.addStep(new Conditions(notRuneCrossbow, inYanille, madeString, crossbowString), smithLimbs);
		doElite.addStep(new Conditions(notRuneCrossbow, madeString, crossbowString), moveToYan);
		doElite.addStep(notRuneCrossbow, spinString);
		doElite.addStep(notPickTorstol, pickTorstol);

		return doElite;
	}

	public void setupRequirements()
	{
		notTrawlerRay = new VarplayerRequirement(1197, false, 6);
		notYanAgi = new VarplayerRequirement(1197, false, 7);
		notPickHero = new VarplayerRequirement(1197, false, 8);
		notRuneCrossbow = new VarplayerRequirement(1197, false, 9);
		notImbueSalve = new VarplayerRequirement(1197, false, 10);
		notPickTorstol = new VarplayerRequirement(1197, false, 11);
		notArdyRooftops = new VarplayerRequirement(1197, false, 12);
		notIceBarrage = new VarplayerRequirement(1197, false, 13);

		ancientBook = new SpellbookRequirement(Spellbook.ANCIENT);
		enoughNMZPoints = new VarplayerRequirement(1060, 800000, Operation.GREATER_EQUAL,
			"800,000 Nightmare Zone reward points");

		// = new ItemRequirement("", ItemID.).showConditioned();
		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		bloodRune = new ItemRequirement("Blood rune", ItemID.BLOOD_RUNE).showConditioned(notIceBarrage);
		waterRune = new ItemRequirement("Water rune", ItemID.WATER_RUNE).showConditioned(notIceBarrage);
		deathRune = new ItemRequirement("Death rune", ItemID.DEATH_RUNE).showConditioned(notIceBarrage);
		lockpick = new ItemRequirement("Lockpick", ItemID.LOCKPICK).showConditioned(notYanAgi);
		salveAmmy = new ItemRequirement("Salve amulet", ItemCollections.getImbuableSalveAmulet())
			.showConditioned(notImbueSalve);
		imbuedSalve = new ItemRequirement("Salve amulet", ItemCollections.getImbuedSalveAmulet())
			.showConditioned(notImbueSalve);
		seedDib = new ItemRequirement("Seed dibber", ItemID.SEED_DIBBER).showConditioned(notPickTorstol);
		torstolSeed = new ItemRequirement("Torstol seed", ItemID.TORSTOL_SEED).showConditioned(notPickTorstol);
		rake = new ItemRequirement("Rake", ItemID.RAKE).showConditioned(notPickTorstol);
		compost = new ItemRequirement("Compost", ItemCollections.getCompost()).showConditioned(notPickTorstol);
		spade = new ItemRequirement("Spade", ItemID.SPADE).showConditioned(notPickTorstol);
		yewLog = new ItemRequirement("Yew logs", ItemID.YEW_LOGS).showConditioned(notRuneCrossbow);
		runeBar = new ItemRequirement("Rune bar", ItemID.RUNITE_BAR).showConditioned(notRuneCrossbow);
		sinew = new ItemRequirement("Sinew", ItemID.SINEW).showConditioned(notRuneCrossbow);
		root = new ItemRequirement("Root", ItemCollections.getNonMagicTreeRoot()).showConditioned(notRuneCrossbow);
		sinewOrRoot = new ItemRequirements(LogicType.OR, "Sinew or non-magic tree root", root, sinew)
			.showConditioned(notRuneCrossbow);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notRuneCrossbow);
		knife = new ItemRequirement("Knife", ItemID.KNIFE).showConditioned(notRuneCrossbow);
		runeCrossbowU = new ItemRequirement("Rune crossbow (u)", ItemID.RUNE_CROSSBOW_23601).showConditioned(notRuneCrossbow);
		crossbowString = new ItemRequirement("Crossbow string", ItemID.CROSSBOW_STRING)
			.showConditioned(notRuneCrossbow);
		yewStock = new ItemRequirement("Yew stock", ItemID.YEW_STOCK).showConditioned(notRuneCrossbow);
		runeLimbs = new ItemRequirement("Runite limbs", ItemID.RUNITE_LIMBS).showConditioned(notRuneCrossbow);

		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);

		inYanAgilityCave = new ZoneRequirement(yanAgilityCave);
		inYanille = new ZoneRequirement(yanille);

		madeString = new ChatMessageRequirement(
			inWitchaven,
			"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
		);
		((ChatMessageRequirement) madeString).setInvalidateRequirement(
			new ChatMessageRequirement(
				new Conditions(LogicType.NOR, inWitchaven),
				"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
			)
		);

		madeLimbs = new ChatMessageRequirement(
			inYanille,
			"<col=0040ff>Achievement Diary Stage Task - Current stage: 2.</col>"
		);
		((ChatMessageRequirement) madeLimbs).setInvalidateRequirement(
			new ChatMessageRequirement(
				new Conditions(LogicType.NOR, inYanille),
				"<col=0040ff>Achievement Diary Stage Task - Current stage: 2.</col>"
			)
		);

		madeStock = new ChatMessageRequirement(
			inYanille,
			"<col=0040ff>Achievement Diary Stage Task - Current stage: 3.</col>"
		);
		((ChatMessageRequirement) madeStock).setInvalidateRequirement(
			new ChatMessageRequirement(
				new Conditions(LogicType.NOR, inYanille),
				"<col=0040ff>Achievement Diary Stage Task - Current stage: 3.</col>"
			)
		);

		madeCrossU = new ChatMessageRequirement(
			inYanille,
			"<col=0040ff>Achievement Diary Stage Task - Current stage: 4.</col>"
		);
		((ChatMessageRequirement) madeCrossU).setInvalidateRequirement(
			new ChatMessageRequirement(
				new Conditions(LogicType.NOR, inYanille),
				"<col=0040ff>Achievement Diary Stage Task - Current stage: 4.</col>"
			)
		);

		desertTreasure = new QuestRequirement(QuestHelperQuest.DESERT_TREASURE, QuestState.FINISHED);
		hauntedMine = new QuestRequirement(QuestHelperQuest.HAUNTED_MINE, QuestState.FINISHED);
	}

	public void loadZones()
	{
		yanille = new Zone(new WorldPoint(2540, 3109, 0), new WorldPoint(2619, 3075, 0));
		yanAgilityCave = new Zone(new WorldPoint(2821, 9545, 0), new WorldPoint(2879, 9663, 0));
		witchaven = new Zone(new WorldPoint(2728, 3280, 0), new WorldPoint(2736, 3273, 0));
	}

	public void setupSteps()
	{
		iceBarrage = new DetailedQuestStep(this, new WorldPoint(2441, 3089, 0),
			"Cast Ice Barrage on a Player within Castle Wars.", ancientBook, waterRune.quantity(6),
			bloodRune.quantity(2), deathRune.quantity(4));

		moveToYanAgi = new ObjectStep(this, ObjectID.STAIRCASE_16664, new WorldPoint(2604, 3079, 0),
			"Climb down the stairs to enter the Yanille Agility Dungeon.", lockpick);
		yanAgi = new ObjectStep(this, ObjectID.DOOR_11728, new WorldPoint(2601, 9482, 0),
			"Lockpick the door to the Yanille Agility Dungeon.", lockpick);
		yanAgi.addIcon(ItemID.LOCKPICK);

		farmMorePoints = new NpcStep(this, NpcID.DOMINIC_ONION, new WorldPoint(2608, 3116, 0),
			"Farm more Nightmare Zone points. Speak with Dominic Onion and buy a dream.", combatGear, food);
		imbueSalve = new ObjectStep(this, ObjectID.REWARDS_CHEST, new WorldPoint(2609, 3119, 0),
			"Imbue a salve amulet at Nightmare Zone.", enoughNMZPoints, salveAmmy);
		equipSalve = new ItemStep(this, "Equip your salve amulet.", imbuedSalve);

		// TODO make this task two steps and check varb / chat message
		trawlerRay = new DetailedQuestStep(this, new WorldPoint(2659, 3160, 0),
			"Catch a manta ray in the Fishing Trawler and cook it in Port Khazard.");

		ardyRooftops = new ObjectStep(this, ObjectID.WOODEN_BEAMS, new WorldPoint(2673, 3298, 0),
			"Complete a lap of Ardougne's rooftop agility course.");

		pickHero = new NpcStep(this, NpcID.HERO, new WorldPoint(2630, 3292, 0),
			"Pickpocket a Hero in East Ardougne.");

		spinString = new ObjectStep(this, ObjectID.SPINNING_WHEEL_8748, new WorldPoint(2731, 3278, 0),
			"Spin a crossbow string in Witchaven. Do not complete any other diary tasks while completing this.", sinewOrRoot);
		moveToYan = new DetailedQuestStep(this, new WorldPoint(2603, 3088, 0),
			"Enter Yanille to continue the task.");
		smithLimbs = new ObjectStep(this, ObjectID.ANVIL_2097, new WorldPoint(2613, 3081, 0),
			"Smith some rune limbs in Yanille.", runeBar, hammer);
		fletchStock = new ItemStep(this, "Use your knife on the  yew logs to make a yew stock.", knife.highlighted(),
			yewLog.highlighted());
		makeUnstrungCross = new ItemStep(this, "Combine the rune limbs with the yew stock.", runeLimbs.highlighted(),
			yewStock.highlighted());
		runeCrossbow = new ItemStep(this, "String the rune crossbow (u).", runeCrossbowU.highlighted(),
			crossbowString.highlighted());

		pickTorstol = new ObjectStep(this, NullObjectID.NULL_8555, new WorldPoint(2667, 3371, 0),
			"Plant and harvest the Torstol from the north Ardougne herb patch.", rake, spade, seedDib, torstolSeed);

		claimReward = new NpcStep(this, NpcID.TWOPINTS, new WorldPoint(2574, 3323, 0),
			"Talk to Two-pints in the Flying Horse Inn at East Ardougne to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(waterRune.quantity(6), bloodRune.quantity(2), deathRune.quantity(4), lockpick, salveAmmy,
			sinewOrRoot, runeBar, hammer, knife, yewLog, rake, spade, seedDib, torstolSeed);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, compost);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.AGILITY, 90, true));
		reqs.add(new SkillRequirement(Skill.COOKING, 91, true));
		reqs.add(new SkillRequirement(Skill.CRAFTING, 10));
		reqs.add(new SkillRequirement(Skill.FARMING, 85));
		reqs.add(new SkillRequirement(Skill.FISHING, 81));
		reqs.add(new SkillRequirement(Skill.FLETCHING, 69));
		reqs.add(new SkillRequirement(Skill.MAGIC, 94));
		reqs.add(new SkillRequirement(Skill.SMITHING, 91, true));
		reqs.add(new SkillRequirement(Skill.THIEVING, 82, true));
		reqs.add(ancientBook);
		reqs.add(desertTreasure);
		reqs.add(hauntedMine);

		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Nightmare Zone monsters (levels vary)");
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Ardougne Cloak 4", ItemID.ARDOUGNE_CLOAK_4),
			new ItemReward("50,000 Exp. Lamp (Any skill over 70)", ItemID.ANTIQUE_LAMP)
		);
	}

	// TODO add nightmare zone and fishing trawler wiki page to external resources

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Unlimited teleports to Ardougne farm patch with the Ardougne cloak 4"),
			new UnlockReward("250 free noted pure essence every day from Wizard Cromperty"),
			new UnlockReward("50% more fish from Fishing Trawler"),
			new UnlockReward("25% more marks of grace from the Ardougne Rooftop Course"),
			new UnlockReward("Bert will automatically deliver 84 buckets of sand to your bank each day you log in (unless you are an Ultimate Ironman), even if you cancel membership")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails iceSteps = new PanelDetails("Ice Barrage in Castle Wars", Collections.singletonList(iceBarrage),
			new SkillRequirement(Skill.MAGIC, 94), desertTreasure, ancientBook, waterRune.quantity(6),
			bloodRune.quantity(2), deathRune.quantity(4));
		iceSteps.setDisplayCondition(notIceBarrage);
		allSteps.add(iceSteps);

		PanelDetails yanSteps = new PanelDetails("Yanille Agility Dungeon", Arrays.asList(moveToYanAgi, yanAgi),
			new SkillRequirement(Skill.THIEVING, 82, true), lockpick);
		yanSteps.setDisplayCondition(notYanAgi);
		allSteps.add(yanSteps);

		PanelDetails salveSteps = new PanelDetails("Imbued Salve Amulet", Arrays.asList(farmMorePoints, imbueSalve),
			hauntedMine, salveAmmy, enoughNMZPoints);
		salveSteps.setDisplayCondition(notImbueSalve);
		allSteps.add(salveSteps);

		PanelDetails raySteps = new PanelDetails("Fishing Trawler Manta Ray", Collections.singletonList(trawlerRay),
			new SkillRequirement(Skill.COOKING, 91, true), new SkillRequirement(Skill.FISHING, 81));
		raySteps.setDisplayCondition(notTrawlerRay);
		allSteps.add(raySteps);

		PanelDetails roofSteps = new PanelDetails("Ardougne Rooftop Agility", Collections.singletonList(ardyRooftops),
			new SkillRequirement(Skill.AGILITY, 90, true));
		roofSteps.setDisplayCondition(notArdyRooftops);
		allSteps.add(roofSteps);

		PanelDetails heroSteps = new PanelDetails("Pickpocket Hero", Collections.singletonList(pickHero),
			new SkillRequirement(Skill.THIEVING, 80));
		heroSteps.setDisplayCondition(notPickHero);
		allSteps.add(heroSteps);

		PanelDetails crossSteps = new PanelDetails("Rune Crossbow in Yanille / Witchaven", Arrays.asList(spinString,
			smithLimbs, fletchStock, makeUnstrungCross, runeCrossbow), new SkillRequirement(Skill.CRAFTING, 10),
			new SkillRequirement(Skill.SMITHING, 91, true), new SkillRequirement(Skill.FLETCHING, 69),
			yewLog, runeBar, hammer, knife, sinewOrRoot);
		crossSteps.setDisplayCondition(notRuneCrossbow);
		allSteps.add(crossSteps);

		PanelDetails torstolSteps = new PanelDetails("Ardougne Torstol", Collections.singletonList(pickTorstol),
			new SkillRequirement(Skill.FARMING, 85), torstolSeed, compost, rake, seedDib, spade);
		torstolSteps.setDisplayCondition(notPickTorstol);
		allSteps.add(torstolSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
