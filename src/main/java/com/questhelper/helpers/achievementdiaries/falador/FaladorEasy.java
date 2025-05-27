/*
 * Copyright (c) 2021, Kerpackie <https://github.com/Kerpackie/>
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

package com.questhelper.helpers.achievementdiaries.falador;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.TeleportItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
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

public class FaladorEasy extends ComplexStateQuestHelper
{

	//Items Required
	ItemRequirement bucket, tiara, mindTalisman, hammer, pickaxe, combatGear, bluriteBar, bluriteOre;

	//Items Recommended
	ItemRequirement teleportFalador, teleportMindAltar, explorersRing;

	Requirement doricsQuest, knightSword;

	Requirement notFilledWater, notKilledDuck, notClimbedWall, notGotHaircut, notMotherloadMine,
		notEntrana, notMindTiara, notBluriteLimbs, notGotSecurityBook, notSarahFarmingShop,
		notFamilyCrest, hasBluriteOre, hasBluriteBar;

	QuestStep claimReward, fillWater, climbWall, getHaircut, enterDwarvenMines, enterCaveToMotherlodeMine,
		fixMotherloadMine, goEntrana, enterMindAltar, getMindTiara, getPickaxe, enterDungeon,
		mineBlurite, smeltBlurite, smithBluriteLimbs, climbLadderPortSarimJail, getSecurityBook,
		browseSarahFarmingShop, climbLadderWhiteKnightCastle, discoverFamilyCrest;

	NpcStep killDuck;

	Zone mindAltar, bluriteDungeon, faladorCastle1, portSarimJail1, motherlodeMine, dwarvenMine;

	ZoneRequirement inMindAltar, inBluriteDungeon, inFaladorCastle1, inPortSarimJail1,
		inMotherlodeMine, inDwarvenMine;

	ConditionalStep filledWaterTask, killedDuckTask, climbedWallTask, gotHaircutTask, motherloadMineTask, entranaTask,
		mindTiaraTask, bluriteLimbsTask, gotSecurityBookTask, sarahFarmingShopTask, familyCrestTask;


	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupSteps();

		ConditionalStep doEasy = new ConditionalStep(this, claimReward);

		filledWaterTask = new ConditionalStep(this, fillWater);
		doEasy.addStep(notFilledWater, filledWaterTask);

		gotHaircutTask = new ConditionalStep(this, getHaircut);
		doEasy.addStep(notGotHaircut, gotHaircutTask);

		climbedWallTask = new ConditionalStep(this, climbWall);
		doEasy.addStep(notClimbedWall, climbedWallTask);

		killedDuckTask = new ConditionalStep(this, killDuck);
		doEasy.addStep(notKilledDuck, killedDuckTask);

		motherloadMineTask = new ConditionalStep(this, enterDwarvenMines);
		motherloadMineTask.addStep(inDwarvenMine, enterCaveToMotherlodeMine);
		motherloadMineTask.addStep(inMotherlodeMine, fixMotherloadMine);
		doEasy.addStep(notMotherloadMine, motherloadMineTask);

		sarahFarmingShopTask = new ConditionalStep(this, browseSarahFarmingShop);
		doEasy.addStep(notSarahFarmingShop, sarahFarmingShopTask);

		entranaTask = new ConditionalStep(this, goEntrana);
		doEasy.addStep(notEntrana, entranaTask);

		gotSecurityBookTask = new ConditionalStep(this, climbLadderPortSarimJail);
		gotSecurityBookTask.addStep(inPortSarimJail1, getSecurityBook);
		doEasy.addStep(notGotSecurityBook, gotSecurityBookTask);

		bluriteLimbsTask = new ConditionalStep(this, getPickaxe);
		bluriteLimbsTask.addStep(hasBluriteBar, smithBluriteLimbs);
		bluriteLimbsTask.addStep(hasBluriteOre, smeltBlurite);
		bluriteLimbsTask.addStep(new Conditions(pickaxe, inBluriteDungeon), mineBlurite);
		bluriteLimbsTask.addStep(pickaxe, enterDungeon);
		doEasy.addStep(notBluriteLimbs, bluriteLimbsTask);

		mindTiaraTask = new ConditionalStep(this, enterMindAltar);
		mindTiaraTask.addStep(inMindAltar, getMindTiara);
		doEasy.addStep(notMindTiara, mindTiaraTask);

		familyCrestTask = new ConditionalStep(this, climbLadderWhiteKnightCastle);
		familyCrestTask.addStep(inFaladorCastle1, discoverFamilyCrest);
		doEasy.addStep(notFamilyCrest, familyCrestTask);

		return doEasy;
	}

	@Override
	protected void setupRequirements()
	{
		notFamilyCrest = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY, false, 0);
		notClimbedWall = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY, false, 1);
		notSarahFarmingShop = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY, false, 2);
		notGotHaircut = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY, false, 3);
		notFilledWater = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY, false, 4);
		notKilledDuck = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY, false, 5);
		notMindTiara = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY, false, 6);
		notEntrana = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY, false, 7);
		notMotherloadMine = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY, false, 8);
		notGotSecurityBook = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY, false, 9);
		notBluriteLimbs = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY, false, 10);

		//Required
		bucket = new ItemRequirement("Bucket", ItemID.BUCKET_EMPTY).showConditioned(notFilledWater).isNotConsumed();
		tiara = new ItemRequirement("Silver Tiara", ItemID.TIARA).showConditioned(notMindTiara);
		mindTalisman = new ItemRequirement("Mind Talisman", ItemID.MIND_TALISMAN).showConditioned(notMindTiara);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(new Conditions(LogicType.OR, notMotherloadMine, notBluriteLimbs)).isNotConsumed();
		pickaxe = new ItemRequirement("Any Pickaxe", ItemCollections.PICKAXES)
			.showConditioned(new Conditions(LogicType.OR, notMotherloadMine, notBluriteLimbs)).isNotConsumed();
		combatGear = new ItemRequirement("A range or mage attack to kill a Duck (Level 1)", -1, -1).showConditioned(notKilledDuck).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getRangedCombatGear());
		bluriteOre = new ItemRequirement("Blurite Ore", ItemID.BLURITE_ORE);
		bluriteBar = new ItemRequirement("Blurite Bar", ItemID.BLURITE_BAR);
		bluriteOre.canBeObtainedDuringQuest();
		bluriteBar.canBeObtainedDuringQuest();

		//Recommended
		teleportFalador = new TeleportItemRequirement("Multiple teleports to Falador", ItemID.POH_TABLET_FALADORTELEPORT, -1);
		teleportMindAltar = new TeleportItemRequirement("A Teleport to the Mind Altar", ItemID.TELETAB_MIND_ALTAR);
		explorersRing = new TeleportItemRequirement("Explorers Ring (2) or above.", ItemID.LUMBRIDGE_RING_MEDIUM).isNotConsumed();
		explorersRing.addAlternates(ItemID.LUMBRIDGE_RING_HARD, ItemID.LUMBRIDGE_RING_ELITE);

		hasBluriteOre = bluriteOre.alsoCheckBank(questBank);
		hasBluriteBar = bluriteBar.alsoCheckBank(questBank);

		inMindAltar = new ZoneRequirement(mindAltar);
		inBluriteDungeon = new ZoneRequirement(bluriteDungeon);
		inFaladorCastle1 = new ZoneRequirement(faladorCastle1);
		inPortSarimJail1 = new ZoneRequirement(portSarimJail1);
		inDwarvenMine = new ZoneRequirement(dwarvenMine);
		inMotherlodeMine = new ZoneRequirement(motherlodeMine);

		doricsQuest = new QuestRequirement(QuestHelperQuest.DORICS_QUEST, QuestState.FINISHED);
		knightSword = new QuestRequirement(QuestHelperQuest.THE_KNIGHTS_SWORD, QuestState.FINISHED);
	}

	@Override
	protected void setupZones()
	{
		mindAltar = new Zone(new WorldPoint(2805, 4819, 0), new WorldPoint(2760, 4855, 0));
		bluriteDungeon = new Zone(new WorldPoint(2979, 9538, 0), new WorldPoint(3069, 9602, 0));
		faladorCastle1 = new Zone(new WorldPoint(2954, 3328, 1), new WorldPoint(2997, 3353, 1));
		dwarvenMine = new Zone(new WorldPoint(2979, 9855, 0), new WorldPoint(3069, 9698, 0));
		motherlodeMine = new Zone(new WorldPoint(3712, 5695, 0), new WorldPoint(3780, 5636, 0));
		portSarimJail1 = new Zone(new WorldPoint(3009, 3197, 1), new WorldPoint(3021, 3178, 1));
	}

	public void setupSteps()
	{
		//Fill a bucket of water from the pump north of Falador Park
		fillWater = new ObjectStep(this, ObjectID.FAI_FALADOR_WATERPUMP, new WorldPoint(2947, 3382, 0),
			"Use the bucket on the Waterpump outside the Hairdressers in west Falador.", bucket.highlighted());
		fillWater.addIcon(ItemID.BUCKET_EMPTY);

		//Get a Haircut from the Falador hairdresser
		getHaircut = new NpcStep(this, NpcID.HAIRDRESSER, new WorldPoint(2945, 3380, 0),
			"Visit the hairdresser in west Falador for a well deserved shave.");
		getHaircut.addDialogStep("I'd like a haircut please.");
		getHaircut.addDialogStep("I'd like a shave please.");

		//Climb over the Western Falador Wall
		climbWall = new ObjectStep(this, ObjectID.FAI_FALADOR_CASTLE_CRUMBLE_MID, new WorldPoint(2935, 3355, 0),
			"Climb over the Crumbled Wall west of the West Falador Bank.");

		//Kill a duck in Falador Park
		killDuck = new NpcStep(this, NpcID.DUCK_FEMALE, new WorldPoint(2989, 3379, 0),
			"Kill a Duck (Level 1) in the Falador Park", true, combatGear);
		killDuck.addAlternateNpcs(NpcID.DUCK);

		//Repair a broken strut in the Motherlode Mine
		enterDwarvenMines = new ObjectStep(this, ObjectID.STAIRS_CELLAR, new WorldPoint(3058, 3376, 0),
			"Enter the Dwarven Mines, you can get there quickly by going down the stairs near the Party Room.", pickaxe);
		enterCaveToMotherlodeMine = new ObjectStep(this, ObjectID.MOTHERLODE_ENTRANCE, new WorldPoint(3059, 9764, 0),
			"Go through the Cave entrance to the Motherlode Mines.", pickaxe);
		fixMotherloadMine = new ObjectStep(this, ObjectID.MOTHERLODE_WHEEL_STRUT_BROKEN, new WorldPoint(3742, 5669, 0),
			"Repair a broken strut on the Waterwheel in the Motherlode mine. It may take a few minutes for it to break.", true, hammer.highlighted());
		fixMotherloadMine.addIcon(ItemID.HAMMER);

		//Find out what your family crest is from Sir Renitee
		climbLadderWhiteKnightCastle = new ObjectStep(this, ObjectID.FAI_FALADOR_CASTLE_LADDER_UP, new WorldPoint(2994, 3341, 0),
			"Climb the ladder in The White Knights Castle!");
		discoverFamilyCrest = new NpcStep(this, NpcID.POH_HERALD_OF_FALADOR, new WorldPoint(2982, 3341, 1),
			"Speak to Sir Renitee to find out your family crest.");
		discoverFamilyCrest.addDialogStep("I don't know, what can you do for me?");
		discoverFamilyCrest.addDialogStep("Can you see if I have a family crest?");
		discoverFamilyCrest.addDialogStep("Thanks!");

		//Browse Sarah's Farm Shop
		browseSarahFarmingShop = new NpcStep(this, NpcID.FARMING_SHOPKEEPER_1, new WorldPoint(3039, 3292, 0),
			"Trade Sarah in the Farming Shop south of Falador and browse her goods.");
		browseSarahFarmingShop.addDialogStep("What are you selling?");

		//Take the boat to Entrana
		goEntrana = new NpcStep(this, NpcID.SHIPMONK, new WorldPoint(3046, 3235, 0),
			"Deposit all your combat gear and talk to the Monk of Entrana in Port Sarim to take the boat.");
		goEntrana.addDialogStep("Yes, okay, I'm ready to go.");

		//Claim a security book from the Security Guard at Port Sarim Jail.
		climbLadderPortSarimJail = new ObjectStep(this, ObjectID.PRISON_LADDER_UP, new WorldPoint(3010, 3184, 0),
			"Climb in the Port Sarim Jail to speak to the Security Guard.");
		getSecurityBook = new NpcStep(this, NpcID.SOS_GUARD, new WorldPoint(3013, 3192, 1),
			"Speak to the Security Guard in the Port Sarim Jail to get a Security Book.");
		getSecurityBook.addDialogStep("If you're a security guard, let's talk about security.");

		//Smith some Blurite Limbs on Doric's Anvil
		getPickaxe = new DetailedQuestStep(this, new WorldPoint(2963, 3216, 0),
			"Get a pickaxe in Rimmington if you do not have one.");
		enterDungeon = new ObjectStep(this, ObjectID.FAI_TRAPDOOR, new WorldPoint(3008, 3150, 0),
			"Go down the ladder south of Port Sarim. Be prepared for ice giants and ice warriors to attack you.", pickaxe, hammer);
		mineBlurite = new ObjectStep(this, ObjectID.BLURITE_ROCK_1, new WorldPoint(3049, 9566, 0),
			"Mine a blurite ore in the eastern cavern.", pickaxe, hammer);
		smeltBlurite = new ObjectStep(this, ObjectID.FAI_FALADOR_FURNACE, new WorldPoint(2976, 3369, 0),
			"Smelt the blurite ore into a blurite bar.", hammer, bluriteOre);
		smeltBlurite.addWidgetHighlightWithItemIdRequirement(270, 15, ItemID.BLURITE_BAR, true);
		smithBluriteLimbs = new ObjectStep(this, ObjectID.DORICS_ANVIL, new WorldPoint(2950, 3451, 0),
			"Smith the blurite bar into blurite limbs on Doric's Anvil, north of Falador.", hammer, bluriteBar);
		smithBluriteLimbs.addSubSteps(enterDungeon, mineBlurite, smeltBlurite);
		smithBluriteLimbs.addWidgetHighlightWithItemIdRequirement(270, 13, ItemID.XBOWS_CROSSBOW_LIMBS_BLURITE, true);


		//Make a Mind Tiara
		enterMindAltar = new ObjectStep(this, ObjectID.MINDTEMPLE_RUINED_OLD, new WorldPoint(2982, 3514, 0),
			"Use the mind talisman on the Mysterious Ruins to access the Mind Altar.", mindTalisman.highlighted(), tiara);
		getMindTiara = new ObjectStep(this, ObjectID.ASTRAL_ALTAR, new WorldPoint(2786, 4841, 0),
			"Use the mind talisman on the Altar", mindTalisman.highlighted(), tiara);
		getMindTiara.addIcon(ItemID.MIND_TALISMAN);
		enterMindAltar.addSubSteps(getMindTiara);

		//Claim Reward
		claimReward = new NpcStep(this, NpcID.WHITE_KNIGHT_DIARY, new WorldPoint(2977, 3346, 0),
			"Congratulations! Talk to Sir Rebral in the courtyard of The White Knight Castle to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(pickaxe, hammer, bucket, tiara, mindTalisman, combatGear, bluriteBar, bluriteOre);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(teleportFalador, teleportMindAltar, explorersRing);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new SkillRequirement(Skill.AGILITY, 5, true));
		req.add(new SkillRequirement(Skill.CONSTRUCTION, 16));
		req.add(new SkillRequirement(Skill.MINING, 10, true));
		req.add(new SkillRequirement(Skill.SMITHING, 13, true));

		req.add(doricsQuest);
		req.add(knightSword);
		req.add(new QuestRequirement(QuestHelperQuest.RUNE_MYSTERIES, QuestState.FINISHED));

		return req;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Falador Shield (1)", ItemID.FALADOR_SHIELD_EASY, 1),
			new ItemReward("2,500 Exp. Lamp (Any skill over 30)", ItemID.THOSF_REWARD_LAMP, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Tight-gap shortcut to the Chaos Temple from Burthorpe."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails fillBucketSteps = new PanelDetails("Fill Water Bucket", Collections.singletonList(fillWater),
			bucket);
		fillBucketSteps.setDisplayCondition(notFilledWater);
		fillBucketSteps.setLockingStep(filledWaterTask);
		allSteps.add(fillBucketSteps);

		PanelDetails haircutSteps = new PanelDetails("Get A Haircut", Collections.singletonList(getHaircut));
		haircutSteps.setDisplayCondition(notGotHaircut);
		haircutSteps.setLockingStep(gotHaircutTask);
		allSteps.add(haircutSteps);

		PanelDetails westWallSteps = new PanelDetails("Climb West Wall", Collections.singletonList(climbWall),
			new SkillRequirement(Skill.AGILITY, 5, true));
		westWallSteps.setDisplayCondition(notClimbedWall);
		westWallSteps.setLockingStep(climbedWallTask);
		allSteps.add(westWallSteps);

		PanelDetails duckSteps = new PanelDetails("Kill The Duck", Collections.singletonList(killDuck), combatGear);
		duckSteps.setDisplayCondition(notKilledDuck);
		duckSteps.setLockingStep(killedDuckTask);
		allSteps.add(duckSteps);

		PanelDetails motherlodeRepairsSteps = new PanelDetails("Motherlode Repairs", Arrays.asList(enterDwarvenMines,
			enterCaveToMotherlodeMine, fixMotherloadMine), pickaxe, hammer);
		motherlodeRepairsSteps.setDisplayCondition(notMotherloadMine);
		motherlodeRepairsSteps.setLockingStep(motherloadMineTask);
		allSteps.add(motherlodeRepairsSteps);

		PanelDetails sarahsFarmingSteps = new PanelDetails("Sarah's Farming Shop",
			Collections.singletonList(browseSarahFarmingShop));
		sarahsFarmingSteps.setDisplayCondition(notSarahFarmingShop);
		sarahsFarmingSteps.setLockingStep(sarahFarmingShopTask);
		allSteps.add(sarahsFarmingSteps);

		PanelDetails holyLandSteps = new PanelDetails("To the Holy Land!", Collections.singletonList(goEntrana));
		holyLandSteps.setDisplayCondition(notEntrana);
		holyLandSteps.setLockingStep(entranaTask);
		allSteps.add(holyLandSteps);

		PanelDetails securityBookSteps = new PanelDetails("Get A Security Book",
			Arrays.asList(climbLadderPortSarimJail, getSecurityBook));
		securityBookSteps.setDisplayCondition(notGotSecurityBook);
		securityBookSteps.setLockingStep(gotSecurityBookTask);
		allSteps.add(securityBookSteps);

		PanelDetails bluriteLimbsSteps = new PanelDetails("Blurite Limbs", Arrays.asList(getPickaxe, enterDungeon,
			mineBlurite, smeltBlurite, smithBluriteLimbs), doricsQuest, knightSword,
			new SkillRequirement(Skill.MINING, 10, true),
			new SkillRequirement(Skill.SMITHING, 13, true), hammer, pickaxe);
		bluriteLimbsSteps.setDisplayCondition(notBluriteLimbs);
		bluriteLimbsSteps.setLockingStep(bluriteLimbsTask);
		allSteps.add(bluriteLimbsSteps);

		PanelDetails mindTiaraSteps = new PanelDetails("Mind Tiara", Arrays.asList(enterMindAltar, getMindTiara),
			new QuestRequirement(QuestHelperQuest.RUNE_MYSTERIES, QuestState.FINISHED), tiara, mindTalisman);
		mindTiaraSteps.setDisplayCondition(notMindTiara);
		mindTiaraSteps.setLockingStep(mindTiaraTask);
		allSteps.add(mindTiaraSteps);

		PanelDetails familyCrestSteps = new PanelDetails("Family Crest", Arrays.asList(climbLadderWhiteKnightCastle,
			discoverFamilyCrest), new SkillRequirement(Skill.CONSTRUCTION, 16));
		familyCrestSteps.setDisplayCondition(notFamilyCrest);
		familyCrestSteps.setLockingStep(familyCrestTask);
		allSteps.add(familyCrestSteps);

		PanelDetails finishOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishOffSteps);

		return allSteps;

	}
}
