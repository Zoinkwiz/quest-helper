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
package com.questhelper.helpers.achievementdiaries.ardougne;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
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

	ConditionalStep trawlerRayTask, yanAgiTask, pickHeroTask, runeCrossbowTask, imbueSalveTask, pickTorstolTask,
		ardyRooftopsTask, iceBarrageTask;

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupSteps();

		ConditionalStep doElite = new ConditionalStep(this, claimReward);

		pickTorstolTask = new ConditionalStep(this, pickTorstol);
		doElite.addStep(notPickTorstol, pickTorstolTask);

		iceBarrageTask = new ConditionalStep(this, iceBarrage);
		doElite.addStep(notIceBarrage, iceBarrageTask);

		yanAgiTask = new ConditionalStep(this, moveToYanAgi);
		yanAgiTask.addStep(inYanAgilityCave, yanAgi);
		doElite.addStep(notYanAgi, yanAgiTask);

		imbueSalveTask = new ConditionalStep(this, farmMorePoints);
		imbueSalveTask.addStep(enoughNMZPoints, imbueSalve);
		imbueSalveTask.addStep(imbuedSalve.alsoCheckBank(), equipSalve);
		doElite.addStep(notImbueSalve, imbueSalveTask);

		trawlerRayTask = new ConditionalStep(this, trawlerRay);
		doElite.addStep(notTrawlerRay, trawlerRayTask);

		ardyRooftopsTask = new ConditionalStep(this, ardyRooftops);
		doElite.addStep(notArdyRooftops, ardyRooftopsTask);

		pickHeroTask = new ConditionalStep(this, pickHero);
		doElite.addStep(notPickHero, pickHeroTask);

		runeCrossbowTask = new ConditionalStep(this, spinString);
		runeCrossbowTask.addStep(new Conditions(madeString, crossbowString), moveToYan);
		runeCrossbowTask.addStep(new Conditions(inYanille, madeString, crossbowString), smithLimbs);
		runeCrossbowTask.addStep(new Conditions(inYanille, madeLimbs, crossbowString, runeLimbs), fletchStock);
		runeCrossbowTask.addStep(new Conditions(inYanille, madeStock, crossbowString, runeLimbs, yewStock), makeUnstrungCross);
		runeCrossbowTask.addStep(new Conditions(inYanille, madeCrossU, runeCrossbowU, crossbowString), runeCrossbow);
		doElite.addStep(notRuneCrossbow, runeCrossbowTask);

		return doElite;
	}

	@Override
	protected void setupRequirements()
	{
		notTrawlerRay = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY2, false, 6);
		notYanAgi = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY2, false, 7);
		notPickHero = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY2, false, 9);
		notRuneCrossbow = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY2, false, 8);
		notImbueSalve = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY2, false, 10);
		notPickTorstol = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY2, false, 11);
		notArdyRooftops = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY2, false, 12);
		notIceBarrage = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY2, false, 13);

		ancientBook = new SpellbookRequirement(Spellbook.ANCIENT);
		enoughNMZPoints = new VarplayerRequirement(VarPlayerID.NZONE_REWARDPOINTS, 800000, Operation.GREATER_EQUAL, 			"800,000 Nightmare Zone reward points");

		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		bloodRune = new ItemRequirement("Blood runes", ItemID.BLOODRUNE).showConditioned(notIceBarrage);
		waterRune = new ItemRequirement("Water runes", ItemID.WATERRUNE).showConditioned(notIceBarrage);
		deathRune = new ItemRequirement("Death runes", ItemID.DEATHRUNE).showConditioned(notIceBarrage);
		lockpick = new ItemRequirement("Lockpick", ItemID.LOCKPICK).showConditioned(notYanAgi).isNotConsumed();
		salveAmmy = new ItemRequirement("Salve amulet", ItemCollections.IMBUABLE_SALVE_AMULET)
			.showConditioned(notImbueSalve).isNotConsumed();
		imbuedSalve = new ItemRequirement("Salve amulet", ItemCollections.IMBUED_SALVE_AMULET)
			.showConditioned(notImbueSalve).isNotConsumed();
		seedDib = new ItemRequirement("Seed dibber", ItemID.DIBBER).showConditioned(notPickTorstol).isNotConsumed();
		torstolSeed = new ItemRequirement("Torstol seed", ItemID.TORSTOL_SEED).showConditioned(notPickTorstol);
		rake = new ItemRequirement("Rake", ItemID.RAKE).showConditioned(notPickTorstol).isNotConsumed();
		compost = new ItemRequirement("Compost", ItemCollections.COMPOST).showConditioned(notPickTorstol);
		spade = new ItemRequirement("Spade", ItemID.SPADE).showConditioned(notPickTorstol).isNotConsumed();

		yewLog = new ItemRequirement("Yew logs", ItemID.YEW_LOGS).showConditioned(notRuneCrossbow);
		runeBar = new ItemRequirement("Runite bar", ItemID.RUNITE_BAR).showConditioned(notRuneCrossbow);
		sinew = new ItemRequirement("Sinew", ItemID.XBOWS_SINEW).showConditioned(notRuneCrossbow);
		root = new ItemRequirement("Root", ItemCollections.NON_MAGIC_TREE_ROOT).showConditioned(notRuneCrossbow);
		sinewOrRoot = new ItemRequirements(LogicType.OR, "Sinew or non-magic tree root", root, sinew)
			.showConditioned(notRuneCrossbow);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notRuneCrossbow).isNotConsumed();
		knife = new ItemRequirement("Knife", ItemID.KNIFE).showConditioned(notRuneCrossbow).isNotConsumed();
		runeCrossbowU = new ItemRequirement("Rune crossbow (u)", ItemID.XBOWS_CROSSBOW_UNSTRUNG_RUNITE).showConditioned(notRuneCrossbow);
		crossbowString = new ItemRequirement("Crossbow string", ItemID.XBOWS_CROSSBOW_STRING)
			.showConditioned(notRuneCrossbow);
		yewStock = new ItemRequirement("Yew stock", ItemID.XBOWS_CROSSBOW_STOCK_YEW).showConditioned(notRuneCrossbow);
		runeLimbs = new ItemRequirement("Runite limbs", ItemID.XBOWS_CROSSBOW_LIMBS_RUNITE).showConditioned(notRuneCrossbow);

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);

		inYanAgilityCave = new ZoneRequirement(yanAgilityCave);
		inYanille = new ZoneRequirement(yanille);
		inWitchaven = new ZoneRequirement(witchaven);

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

	@Override
	protected void setupZones()
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

		moveToYanAgi = new ObjectStep(this, ObjectID.STAIRS_CELLAR, new WorldPoint(2604, 3079, 0),
			"Climb down the stairs to enter the Yanille Agility Dungeon.", lockpick);
		yanAgi = new ObjectStep(this, ObjectID.TOOLLOCK5, new WorldPoint(2601, 9482, 0),
			"Lockpick the door to the Yanille Agility Dungeon.", lockpick);
		yanAgi.addIcon(ItemID.LOCKPICK);

		farmMorePoints = new NpcStep(this, NpcID.NZONE_HOST, new WorldPoint(2608, 3116, 0),
			"Farm more Nightmare Zone points. Speak with Dominic Onion and buy a dream.", combatGear, food);
		imbueSalve = new ObjectStep(this, ObjectID.NZONE_LOBBY_CHEST, new WorldPoint(2609, 3119, 0),
			"Imbue a salve amulet at Nightmare Zone.", enoughNMZPoints, salveAmmy);
		equipSalve = new ItemStep(this, "Equip your salve amulet.", imbuedSalve);

		trawlerRay = new DetailedQuestStep(this, new WorldPoint(2659, 3160, 0),
			"Catch a manta ray in the Fishing Trawler and cook it in Port Khazard.");

		ardyRooftops = new ObjectStep(this, ObjectID.ROOFTOPS_ARDY_WALLCLIMB, new WorldPoint(2673, 3298, 0),
			"Complete a lap of Ardougne's rooftop agility course.");

		pickHero = new NpcStep(this, NpcID.HERO, new WorldPoint(2630, 3292, 0),
			"Pickpocket a Hero in East Ardougne.");

		spinString = new ObjectStep(this, ObjectID.ELF_VILLAGE_SPINNING_WHEEL, new WorldPoint(2731, 3278, 0),
			"Spin a crossbow string in Witchaven. Do not complete any other diary tasks while completing this.", sinewOrRoot);
		moveToYan = new DetailedQuestStep(this, new WorldPoint(2603, 3088, 0),
			"Enter Yanille to continue the task.");
		smithLimbs = new ObjectStep(this, ObjectID.ANVIL, new WorldPoint(2613, 3081, 0),
			"Smith some rune limbs in Yanille.", runeBar, hammer);
		fletchStock = new ItemStep(this, "Use your knife on the  yew logs to make a yew stock.", knife.highlighted(),
			yewLog.highlighted());
		makeUnstrungCross = new ItemStep(this, "Combine the rune limbs with the yew stock.", runeLimbs.highlighted(),
			yewStock.highlighted());
		runeCrossbow = new ItemStep(this, "String the rune crossbow (u).", runeCrossbowU.highlighted(),
			crossbowString.highlighted());

		pickTorstol = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_3, new WorldPoint(2670, 3374, 0),
			"Plant and harvest the Torstol from the north Ardougne herb patch. " +
				"If you're waiting for it to grow and want to complete further tasks, use the tick box on panel.",
			rake, spade, seedDib, torstolSeed);

		claimReward = new NpcStep(this, NpcID.ARDY_TWOPINTS_DIARY, new WorldPoint(2574, 3323, 0),
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
		reqs.add(new SkillRequirement(Skill.CRAFTING, 10, true));
		reqs.add(new SkillRequirement(Skill.FARMING, 85, true));
		reqs.add(new SkillRequirement(Skill.FISHING, 81, false));
		reqs.add(new SkillRequirement(Skill.FLETCHING, 69, true));
		reqs.add(new SkillRequirement(Skill.MAGIC, 94, true));
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
			new ItemReward("Ardougne Cloak 4", ItemID.ARDY_CAPE_ELITE),
			new ItemReward("50,000 Exp. Lamp (Any skill over 70)", ItemID.THOSF_REWARD_LAMP)
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

		PanelDetails torstolSteps = new PanelDetails("Ardougne Torstol", Collections.singletonList(pickTorstol),
			new SkillRequirement(Skill.FARMING, 85, true), torstolSeed, compost, rake, seedDib, spade);
		torstolSteps.setDisplayCondition(notPickTorstol);
		torstolSteps.setLockingStep(pickTorstolTask);
		allSteps.add(torstolSteps);

		PanelDetails iceSteps = new PanelDetails("Ice Barrage in Castle Wars", Collections.singletonList(iceBarrage),
			new SkillRequirement(Skill.MAGIC, 94, true), desertTreasure, ancientBook, waterRune.quantity(6),
			bloodRune.quantity(2), deathRune.quantity(4));
		iceSteps.setDisplayCondition(notIceBarrage);
		iceSteps.setLockingStep(iceBarrageTask);
		allSteps.add(iceSteps);

		PanelDetails yanSteps = new PanelDetails("Yanille Agility Dungeon", Arrays.asList(moveToYanAgi, yanAgi),
			new SkillRequirement(Skill.THIEVING, 82, true), lockpick);
		yanSteps.setDisplayCondition(notYanAgi);
		yanSteps.setLockingStep(yanAgiTask);
		allSteps.add(yanSteps);

		PanelDetails salveSteps = new PanelDetails("Imbued Salve Amulet", Arrays.asList(farmMorePoints, imbueSalve),
			hauntedMine, salveAmmy, enoughNMZPoints);
		salveSteps.setDisplayCondition(notImbueSalve);
		salveSteps.setLockingStep(imbueSalveTask);
		allSteps.add(salveSteps);

		PanelDetails raySteps = new PanelDetails("Fishing Trawler Manta Ray", Collections.singletonList(trawlerRay),
			new SkillRequirement(Skill.COOKING, 91, true), new SkillRequirement(Skill.FISHING, 81, false));
		raySteps.setDisplayCondition(notTrawlerRay);
		raySteps.setLockingStep(trawlerRayTask);
		allSteps.add(raySteps);

		PanelDetails roofSteps = new PanelDetails("Ardougne Rooftop Agility", Collections.singletonList(ardyRooftops),
			new SkillRequirement(Skill.AGILITY, 90, true));
		roofSteps.setDisplayCondition(notArdyRooftops);
		roofSteps.setLockingStep(ardyRooftopsTask);
		allSteps.add(roofSteps);

		PanelDetails heroSteps = new PanelDetails("Pickpocket Hero", Collections.singletonList(pickHero),
			new SkillRequirement(Skill.THIEVING, 80, true));
		heroSteps.setDisplayCondition(notPickHero);
		heroSteps.setLockingStep(pickHeroTask);
		allSteps.add(heroSteps);

		PanelDetails crossSteps = new PanelDetails("Rune Crossbow in Yanille / Witchaven", Arrays.asList(spinString,
			smithLimbs, fletchStock, makeUnstrungCross, runeCrossbow), new SkillRequirement(Skill.CRAFTING, 10, true),
			new SkillRequirement(Skill.SMITHING, 91, true), new SkillRequirement(Skill.FLETCHING, 69, true),
			yewLog, runeBar, hammer, knife, sinewOrRoot);
		crossSteps.setDisplayCondition(notRuneCrossbow);
		crossSteps.setLockingStep(runeCrossbowTask);
		allSteps.add(crossSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
