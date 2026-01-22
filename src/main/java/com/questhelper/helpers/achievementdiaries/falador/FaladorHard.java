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

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.ComplexRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.item.TeleportItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.WarriorsGuildAccessRequirement;
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

public class FaladorHard extends ComplexStateQuestHelper
{

	//Items Required
	ItemRequirement pureEss28, mindTiara, coins10000, combatGear, food, lightSource, spade, wyvernProtection, prospectorHelm,
		prosyHelm, prosyChest, prosyLegs, dwarvenHelmet, dragonfireProtection;

	//Items Recommended
	ItemRequirement faladorTeleport, combatBracelet;

	ItemRequirements prosySet;

	Requirement heroesQuest, slugMenace, grimTales;

	Requirement notCraftedMindRunes, notChangedFamilyCrest, notKilledMole, notKilledWyvern, notCompleteAgiCourse,
		notEnterMiningGuildWithProspector, notKilledBlueDragon, notCrackedWallSafe, notPraySarimAltarProsy, notEnterWarriorsGuild,
		notDwarvenHelmetDwarvenMines;

	QuestStep claimReward, enterMindAltar, craftMindRunes, climbLadderWhiteKnightCastle, changeFamilyCrest, goToGiantMole,
		killGiantMole, goToIceDungeon, enterWyvernCavern, killWyvern, completeAgiCourse, enterDwarvenMines,
		enterDwarvenMinesHelmet, enterMiningGuild, enterHerosGuild, enterHerosGuildBasement, killBlueDragon, enterRoguesDen,
		crackWallSafe, getProsySet, prayAtAltarSarim, enterWarriorsGuild, equipDwarvenHelmet;

	Zone mindAltar, iceDungeon, wyvernCavern, faladorCastle1, herosGuildBasement, dwarvenMine,
		miningGuild, herosGuildEntranceway, herosGuildMainHall, roguesDen, moleDen;

	ZoneRequirement inMindAltar, inWyvernCavern, inIceDungeon, inFaladorCastle1, inHerosGuild, inHerosGuildBasement,
		inDwarvenMine, inMiningGuild, inRoguesDen, inMoleDen;

	ConditionalStep craftedMindRunesTask, changedFamilyCrestTask, killedMoleTask, killedWyvernTask, completeAgiCourseTask,
		enterMiningGuildWithProspectorTask, killedBlueDragonTask, crackedWallSafeTask, praySarimAltarProsyTask,
		enterWarriorsGuildTask, dwarvenHelmetDwarvenMinesTask;

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupSteps();

		ConditionalStep doHard = new ConditionalStep(this, claimReward);

		changedFamilyCrestTask = new ConditionalStep(this, climbLadderWhiteKnightCastle);
		changedFamilyCrestTask.addStep(inFaladorCastle1, changeFamilyCrest);
		doHard.addStep(notChangedFamilyCrest, changedFamilyCrestTask);

		killedMoleTask = new ConditionalStep(this, goToGiantMole);
		killedMoleTask.addStep(inMoleDen, killGiantMole);
		doHard.addStep(notKilledMole, killedMoleTask);

		completeAgiCourseTask = new ConditionalStep(this, completeAgiCourse);
		doHard.addStep(notCompleteAgiCourse, completeAgiCourseTask);

		dwarvenHelmetDwarvenMinesTask = new ConditionalStep(this, enterDwarvenMinesHelmet);
		dwarvenHelmetDwarvenMinesTask.addStep(inDwarvenMine, equipDwarvenHelmet);
		doHard.addStep(notDwarvenHelmetDwarvenMines, dwarvenHelmetDwarvenMinesTask);

		enterMiningGuildWithProspectorTask = new ConditionalStep(this, enterDwarvenMines);
		enterMiningGuildWithProspectorTask.addStep(inDwarvenMine, enterMiningGuild);
		doHard.addStep(notEnterMiningGuildWithProspector, enterMiningGuildWithProspectorTask);

		enterWarriorsGuildTask = new ConditionalStep(this, enterWarriorsGuild);
		doHard.addStep(notEnterWarriorsGuild, enterWarriorsGuildTask);

		killedBlueDragonTask = new ConditionalStep(this, enterHerosGuild);
		killedBlueDragonTask.addStep(inHerosGuild, enterHerosGuildBasement);
		killedBlueDragonTask.addStep(inHerosGuildBasement, killBlueDragon);
		doHard.addStep(notKilledBlueDragon, killedBlueDragonTask);

		crackedWallSafeTask = new ConditionalStep(this, enterRoguesDen);
		crackedWallSafeTask.addStep(inRoguesDen, crackWallSafe);
		doHard.addStep(notCrackedWallSafe, crackedWallSafeTask);

		craftedMindRunesTask = new ConditionalStep(this, enterMindAltar);
		craftedMindRunesTask.addStep(inMindAltar, craftMindRunes);
		doHard.addStep(notCraftedMindRunes, craftedMindRunesTask);

		praySarimAltarProsyTask = new ConditionalStep(this, getProsySet);
		praySarimAltarProsyTask.addStep(new Conditions(notPraySarimAltarProsy, prosyHelm.alsoCheckBank(),
			prosyLegs.alsoCheckBank(), prosyChest.alsoCheckBank()), prayAtAltarSarim);
		doHard.addStep(notPraySarimAltarProsy, praySarimAltarProsyTask);

		killedWyvernTask = new ConditionalStep(this, goToIceDungeon);
		killedWyvernTask.addStep(inIceDungeon, enterWyvernCavern);
		killedWyvernTask.addStep(inWyvernCavern, killWyvern);
		doHard.addStep(notKilledWyvern, killedWyvernTask);

		return doHard;

	}

	@Override
	protected void setupRequirements()
	{
		notCraftedMindRunes = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY, false, 26);
		notChangedFamilyCrest = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY, false, 27);
		notKilledMole = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY, false, 28);
		notKilledWyvern = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY, false, 29);
		notCompleteAgiCourse = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY, false, 30);
		notEnterMiningGuildWithProspector = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY, false, 31);
		notKilledBlueDragon = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY2, false, 0);
		notCrackedWallSafe = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY2, false, 1);
		notPraySarimAltarProsy = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY2, false, 2);
		notEnterWarriorsGuild = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY2, false, 3);
		notDwarvenHelmetDwarvenMines = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY2, false, 4);

		pureEss28 = new ItemRequirement("Pure Essence", ItemID.BLANKRUNE_HIGH, 28).showConditioned(notCraftedMindRunes);
		mindTiara = new ItemRequirement("Access to the Mind Altar", ItemCollections.MIND_ALTAR_WEARABLE, 1, true).showConditioned(notCraftedMindRunes);
		mindTiara.setTooltip("Mind Tiara, Catalytic Tiara, RC-skill cape or via Abyss");
		coins10000 = new ItemRequirement("Coins", ItemCollections.COINS, 10000).showConditioned(notChangedFamilyCrest);
		combatGear = new ItemRequirement("Combat Gear", -1, -1).isNotConsumed();
		food = new ItemRequirement("Good healing food.", ItemCollections.GOOD_EATING_FOOD, -1);
		lightSource = new ItemRequirement("Light Source", ItemCollections.LIGHT_SOURCES, -1).showConditioned(notKilledMole).isNotConsumed();
		spade = new ItemRequirement("Spade", ItemID.SPADE).showConditioned(notKilledMole).isNotConsumed();
		wyvernProtection = new ItemRequirement("Wyvern Protection", ItemCollections.ANTI_WYVERN_SHIELDS)
			.showConditioned(notKilledWyvern).isNotConsumed();
		prospectorHelm = new ItemRequirement("Prospector Helm", ItemCollections.PROSPECTOR_HELMET, 1, true)
			.showConditioned(notEnterMiningGuildWithProspector).isNotConsumed();
		dragonfireProtection = new ItemRequirement("Protection from Dragonfire", ItemCollections.ANTIFIRE_SHIELDS)
			.showConditioned(notKilledBlueDragon).isNotConsumed();
		prosyHelm = new ItemRequirement("Proselyte Helmet", ItemID.BASIC_TK_RANK2_HELM).showConditioned(notPraySarimAltarProsy).isNotConsumed();
		prosyChest = new ItemRequirement("Proselyte Chest", ItemID.BASIC_TK_RANK2_BODY).showConditioned(notPraySarimAltarProsy).isNotConsumed();
		prosyLegs = new ItemRequirement("Proselyte Legs", ItemID.BASIC_TK_RANK2_LEGS).showConditioned(notPraySarimAltarProsy).isNotConsumed();
		prosyLegs.addAlternates(ItemID.BASIC_TK_RANK2_PLATESKIRT);
		dwarvenHelmet = new ItemRequirement("Dwarven Helmet", ItemID.GRIM_WEAR_HELMET, 1)
			.showConditioned(notDwarvenHelmetDwarvenMines).isNotConsumed();

		faladorTeleport = new TeleportItemRequirement("Multiple teleports to Falador", ItemID.POH_TABLET_FALADORTELEPORT, -1);
		combatBracelet = new TeleportItemRequirement("Combat Bracelet", ItemCollections.COMBAT_BRACELETS);
		combatBracelet.addAlternates(ItemCollections.GAMES_NECKLACES);

		prosySet = new ItemRequirements(prosyHelm, prosyLegs, prosyChest);

		inMindAltar = new ZoneRequirement(mindAltar);
		inWyvernCavern = new ZoneRequirement(wyvernCavern);
		inIceDungeon = new ZoneRequirement(iceDungeon);
		inFaladorCastle1 = new ZoneRequirement(faladorCastle1);
		inDwarvenMine = new ZoneRequirement(dwarvenMine);
		inMiningGuild = new ZoneRequirement(miningGuild);
		inHerosGuildBasement = new ZoneRequirement(herosGuildBasement);
		inHerosGuild = new ZoneRequirement(herosGuildEntranceway, herosGuildMainHall);
		inRoguesDen = new ZoneRequirement(roguesDen);
		inMoleDen = new ZoneRequirement(moleDen);

		slugMenace = new QuestRequirement(QuestHelperQuest.THE_SLUG_MENACE, QuestState.FINISHED);
		heroesQuest = new QuestRequirement(QuestHelperQuest.HEROES_QUEST, QuestState.FINISHED);
		grimTales = new QuestRequirement(QuestHelperQuest.GRIM_TALES, QuestState.FINISHED);
	}

	@Override
	protected void setupZones()
	{
		mindAltar = new Zone(new WorldPoint(2805, 4819, 0), new WorldPoint(2760, 4855, 0));
		iceDungeon = new Zone(new WorldPoint(2979, 9538, 0), new WorldPoint(3029, 9602, 0));
		wyvernCavern = new Zone(new WorldPoint(3030, 9602, 0), new WorldPoint(3083, 9556, 0));
		faladorCastle1 = new Zone(new WorldPoint(2954, 3328, 1), new WorldPoint(2997, 3353, 1));
		dwarvenMine = new Zone(new WorldPoint(3011, 9856, 0), new WorldPoint(3069, 9730, 0));
		miningGuild = new Zone(new WorldPoint(3008, 9730, 0), new WorldPoint(3060, 9698, 0));
		herosGuildBasement = new Zone(new WorldPoint(2883, 9919, 0), new WorldPoint(2943, 9881, 0));
		herosGuildEntranceway = new Zone(new WorldPoint(2899, 3512, 0), new WorldPoint(2901, 3509, 0));
		herosGuildMainHall = new Zone(new WorldPoint(2892, 3507, 0), new WorldPoint(2892, 3514, 0));
		roguesDen = new Zone(new WorldPoint(3067, 4991, 1), new WorldPoint(3036, 4956, 1));
		moleDen = new Zone(new WorldPoint(1731, 5132, 0), new WorldPoint(1789, 5248, 0));
	}

	public void setupSteps()
	{
		//Mind Runes
		enterMindAltar = new ObjectStep(this, ObjectID.MINDTEMPLE_RUINED_OLD, new WorldPoint(2982, 3514, 0),
			"Click on the Mysterious Ruins, with the Mind Tiara equipped to access the Mind Altar.", mindTiara, pureEss28);
		craftMindRunes = new ObjectStep(this, ObjectID.MIND_ALTAR, new WorldPoint(2786, 4841, 0),
			"Click the altar to craft the mind runes.", mindTiara, pureEss28);
		craftMindRunes.addIcon(ItemID.BLANKRUNE_HIGH);
		enterMindAltar.addSubSteps(craftMindRunes);

		//Change Family Crest to Saradomin
		climbLadderWhiteKnightCastle = new ObjectStep(this, ObjectID.FAI_FALADOR_CASTLE_LADDER_UP, new WorldPoint(2994, 3341, 0),
			"Climb the ladder in The White Knights Castle.", coins10000);
		changeFamilyCrest = new NpcStep(this, NpcID.POH_HERALD_OF_FALADOR, new WorldPoint(2982, 3341, 1),
			"Speak to Sir Renitee to change your family crest to Saradomin.", coins10000);
		changeFamilyCrest.addDialogStep("I don't know, what can you do for me?");
		changeFamilyCrest.addDialogStep("Can you see if I have a family crest?");
		changeFamilyCrest.addDialogStep("I don't like that crest. Can I have a different one?");
		changeFamilyCrest.addDialogStepWithExclusion("More...", "Saradomin");
		changeFamilyCrest.addDialogStep("Saradomin");

		//Giant Mole
		goToGiantMole = new ObjectStep(this, ObjectID.MOLE_HILL, new WorldPoint(2989, 3378, 0),
			"Use a spade on one of the Mole Hills in Falador Park", combatGear, food, lightSource, spade);
		killGiantMole = new NpcStep(this, NpcID.MOLE_GIANT,
			"Kill the Giant Mole.", combatGear, food, lightSource);

		//Wyvern
		goToIceDungeon = new ObjectStep(this, ObjectID.FAI_TRAPDOOR, new WorldPoint(3008, 3150, 0),
			"Go down the ladder south of Port Sarim.", combatGear, food, wyvernProtection);
		enterWyvernCavern = new ObjectStep(this, ObjectID.WYVERN_SNOW_CAVE_ENTRANCE, new WorldPoint(3055, 9560, 0),
			"Enter the Icy Cavern at the back of the dungeon.", combatGear, food, wyvernProtection);
		killWyvern = new NpcStep(this, NpcID.SKELETAL_WYVERN1,
			"Kill a Skeletal Wyvern.", combatGear, food, wyvernProtection);

		//Agi Course
		completeAgiCourse = new ObjectStep(this, ObjectID.ROOFTOPS_FALADOR_WALLCLIMB, new WorldPoint(3036, 3341, 0),
			"Complete a lap of the Falador Agility Course");

		//Prospectors in Mining Guild
		enterDwarvenMines = new ObjectStep(this, ObjectID.STAIRS_CELLAR, new WorldPoint(3059, 3376, 0),
			"Go to the Dwarven Mines.", prospectorHelm);
		enterMiningGuild = new ObjectStep(this, ObjectID.MGUILD_DOOR, new WorldPoint(3046, 9756, 0),
			"Equip your prospector helmet and then enter the Mining Guild", prospectorHelm);

		//Blue Dragon
		enterHerosGuild = new ObjectStep(this, ObjectID.HERODOOR_L, new WorldPoint(2902, 3501, 0),
			"Go to the Hero's Guild south of Burthorpe. You can get here faster by teleporting with a Combat Bracelet to the Warriors Guild.", combatGear, food, dragonfireProtection);
		enterHerosGuildBasement = new ObjectStep(this, ObjectID.LADDER_CELLAR, new WorldPoint(2892, 3507, 0),
			"Climb down the ladder in the Hero's Guild.");
		killBlueDragon = new NpcStep(this, NpcID.BLUE_DRAGON2,
			"Kill the Blue Dragon to complete your task.");

		//Rogues Den
		enterRoguesDen = new ObjectStep(this, ObjectID.ROGUESDEN_TRAPDOOR_ENTRANCE, new WorldPoint(2905, 3537, 0),
			"Go to the Rogues Den in Burthorpe.");
		crackWallSafe = new ObjectStep(this, ObjectID.ROGUESDEN_WALLDECOR_SAFE, new WorldPoint(3055, 4977, 1),
			"Crack the Wall Safe inside of the Rogues Den.");

		//Pray in Port Sarim in Proselyte
		getProsySet = new NpcStep(this, NpcID.RD_TELEPORTER_GUY, new WorldPoint(2997, 3373, 0),
			"Speak to Sir Tiffy Cashien to purchase a set of Proselyte Armor for 28,000 Coins for a full set.");
		getProsySet.addDialogStep("Can I buy some armor?");
		prayAtAltarSarim = new ObjectStep(this, ObjectID.ALTAR, new WorldPoint(2995, 3177, 0),
			"Equip your Proselyte armor and pray at the altar in Port Sarim.", prosyHelm, prosyChest, prosyLegs);

		//Warriors Guild
		enterWarriorsGuild = new ObjectStep(this, ObjectID.WARGUILD_DOOR_FRONT, new WorldPoint(2877, 3546, 0),
			"Enter the Warriors Guild, in Burthorpe. You can get here faster by teleporting with a combat bracelet or a games necklace.");

		//Dwarven Helm
		//Prospectors in Mining Guild
		enterDwarvenMinesHelmet = new ObjectStep(this, ObjectID.STAIRS_CELLAR, new WorldPoint(3059, 3376, 0),
			"Go to the Dwarven Mines.", dwarvenHelmet);
		equipDwarvenHelmet = new DetailedQuestStep(this,
			"Equip the Dwarven Helmet.", dwarvenHelmet.equipped());

		//Fin
		claimReward = new NpcStep(this, NpcID.WHITE_KNIGHT_DIARY, new WorldPoint(2977, 3346, 0),
			"Congratulations! Talk to Sir Rebral in the courtyard of The White Knight Castle to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");

	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Giant Mole (lvl 230)", "Skeletal Wyvern (lvl 140)", "Blue dragon (lvl 111)");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(pureEss28, mindTiara, coins10000, combatGear, lightSource, spade, wyvernProtection, prospectorHelm,
			prosyHelm, prosyChest, prosyLegs, dwarvenHelmet);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(faladorTeleport, combatBracelet);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new SkillRequirement(Skill.AGILITY, 50, true));
		req.add(new SkillRequirement(Skill.DEFENCE, 50, false));
		req.add(new SkillRequirement(Skill.MINING, 60, true));
		req.add(new SkillRequirement(Skill.PRAYER, 70, false));
		req.add(new ComplexRequirement(LogicType.OR, "56 Runecraft or 42 with Raiments of the Eye set",
			new SkillRequirement(Skill.RUNECRAFT, 56, true, "56 Runecraft"),
			new ItemRequirements("42 with Raiments of the Eye set",
				new ItemRequirement("Hat", ItemCollections.EYE_HAT),
				new ItemRequirement("Top", ItemCollections.EYE_TOP),
				new ItemRequirement("Bottom", ItemCollections.EYE_BOTTOM),
				new ItemRequirement("Boot", ItemID.BOOTS_OF_THE_EYE))
		));
		req.add(new SkillRequirement(Skill.SLAYER, 72, true));
		req.add(new SkillRequirement(Skill.THIEVING, 50, true));
		req.add(new WarriorsGuildAccessRequirement());


		req.add(grimTales);
		req.add(heroesQuest);
		req.add(slugMenace);

		return req;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Falador Shield (3)", ItemID.FALADOR_SHIELD_HARD, 1),
			new ItemReward("15,000 Exp. Lamp (Any skill over 50)", ItemID.THOSF_REWARD_LAMP, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Access to the bank in the Crafting Guild"),
			new UnlockReward("Giant Moles primary drops are now noted"),
			new UnlockReward("Access to shortcut to Fountain of Heroes"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails changeCrestSteps = new PanelDetails("To Saradomin!", Arrays.asList(climbLadderWhiteKnightCastle,
			changeFamilyCrest), new SkillRequirement(Skill.PRAYER, 70, false), coins10000);
		changeCrestSteps.setDisplayCondition(notChangedFamilyCrest);
		changeCrestSteps.setLockingStep(changedFamilyCrestTask);
		allSteps.add(changeCrestSteps);

		PanelDetails moleSteps = new PanelDetails("Holy Moley!", Arrays.asList(goToGiantMole, killGiantMole),
			lightSource, spade, combatGear, food);
		moleSteps.setDisplayCondition(notKilledMole);
		moleSteps.setLockingStep(killedMoleTask);
		allSteps.add(moleSteps);

		PanelDetails fallyRoofSteps = new PanelDetails("Make sure to stretch!",
			Collections.singletonList(completeAgiCourse), new SkillRequirement(Skill.AGILITY, 50, true));
		fallyRoofSteps.setDisplayCondition(notCompleteAgiCourse);
		fallyRoofSteps.setLockingStep(completeAgiCourseTask);
		allSteps.add(fallyRoofSteps);

		PanelDetails dwarvenHelmSteps = new PanelDetails("A snug fit", Arrays.asList(enterDwarvenMinesHelmet,
			equipDwarvenHelmet), new SkillRequirement(Skill.DEFENCE, 50, false), dwarvenHelmet, grimTales);
		dwarvenHelmSteps.setDisplayCondition(notDwarvenHelmetDwarvenMines);
		dwarvenHelmSteps.setLockingStep(dwarvenHelmetDwarvenMinesTask);
		allSteps.add(dwarvenHelmSteps);

		PanelDetails miningGuildSteps = new PanelDetails("Gold Rush!", Arrays.asList(enterDwarvenMines,
			enterMiningGuild), new SkillRequirement(Skill.MINING, 60, true), prospectorHelm);
		miningGuildSteps.setDisplayCondition(notEnterMiningGuildWithProspector);
		miningGuildSteps.setLockingStep(enterMiningGuildWithProspectorTask);
		allSteps.add(miningGuildSteps);

		PanelDetails warriorsGuildSteps = new PanelDetails("The Dragon Defender",
			Collections.singletonList(enterWarriorsGuild), new WarriorsGuildAccessRequirement());
		warriorsGuildSteps.setDisplayCondition(notEnterWarriorsGuild);
		warriorsGuildSteps.setLockingStep(enterWarriorsGuildTask);
		allSteps.add(warriorsGuildSteps);

		PanelDetails blueDragonSteps = new PanelDetails("The Dragon Slayer", Arrays.asList(enterHerosGuild,
			enterHerosGuildBasement, killBlueDragon), heroesQuest, combatGear, food, dragonfireProtection);
		blueDragonSteps.setDisplayCondition(notKilledBlueDragon);
		blueDragonSteps.setLockingStep(killedBlueDragonTask);
		allSteps.add(blueDragonSteps);

		PanelDetails crackSafeSteps = new PanelDetails("The cat burglar", Arrays.asList(enterRoguesDen, crackWallSafe),
			new SkillRequirement(Skill.THIEVING, 50, true));
		crackSafeSteps.setDisplayCondition(notCrackedWallSafe);
		crackSafeSteps.setLockingStep(crackedWallSafeTask);
		allSteps.add(crackSafeSteps);

		PanelDetails mindRunesSteps = new PanelDetails("Do you mind?", Arrays.asList(enterMindAltar, craftMindRunes),
			new SkillRequirement(Skill.RUNECRAFT, 56, true), mindTiara, pureEss28);
		mindRunesSteps.setDisplayCondition(notCraftedMindRunes);
		mindRunesSteps.setLockingStep(craftedMindRunesTask);
		allSteps.add(mindRunesSteps);

		PanelDetails praySteps = new PanelDetails("Praise the Lord!", Arrays.asList(getProsySet, prayAtAltarSarim),
			new SkillRequirement(Skill.DEFENCE, 30, false), slugMenace, prosyHelm, prosyChest, prosyLegs);
		praySteps.setDisplayCondition(notPraySarimAltarProsy);
		praySteps.setLockingStep(praySarimAltarProsyTask);
		allSteps.add(praySteps);

		PanelDetails wyvernSteps = new PanelDetails("This ain't no dragon!", Arrays.asList(goToIceDungeon,
			enterWyvernCavern, killWyvern), new SkillRequirement(Skill.SLAYER, 72, true), combatGear, food,
			wyvernProtection);
		wyvernSteps.setDisplayCondition(notKilledWyvern);
		wyvernSteps.setLockingStep(killedWyvernTask);
		allSteps.add(wyvernSteps);

		PanelDetails finishOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishOffSteps);

		return allSteps;
	}
}

