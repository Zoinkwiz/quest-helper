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

package com.questhelper.achievementdiaries.falador;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.WarriorsGuildAccessRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import java.util.*;

@QuestDescriptor(
	quest = QuestHelperQuest.FALADOR_HARD
)

public class FaladorHard extends ComplexStateQuestHelper
{

	//Items Required
	ItemRequirement pureEss28, mindTiara, coins10000, combatGear, food, lightSource, spade, wyvernProtection, prospectorHelm,
		prospectorChest, prospectorLegs, prospectorBoots, prosyHelm, prosyChest, prosyLegs, dwarvenHelmet, dragonfireProtection;

	//Items Recommended
	ItemRequirement faladorTeleport, combatBracelet;

	ItemRequirements prosySet, prospectorSet;

	Requirement herosQuest, slugMenace, grimTales;

	Requirement notCraftedMindRunes, notChangedFamilyCrest, notKilledMole, notKilledWyvern, notCompleteAgiCourse,
		notEnterMiningGuildWithProspector, notKilledBlueDragon, notCrackedWallSafe, notPraySarimAltarProsy, notEnterWarriorsGuild,
		notDwarvenHelmetDwarvenMines;

	QuestStep claimReward, enterMindAltar, craftMindRunes, climbLadderWhiteKnightCastle, changeFamilyCrest, goToGiantMole,
		killGiantMole, goToIceDungeon, enterWyvernCavern, killWyvern, completeAgiCourse, enterDwarvenMines,
		enterDwarvenMinesHelmet, enterMiningGuild, enterHerosGuild, enterHerosGuildBasement, killBlueDragon, enterRoguesDen,
		crackWallSafe, getProsySet, prayAtAltarSarim, enterWarriorsGuild, equipDwarvenHelmet;

	Zone mindAltar, iceDungeon, wyvernCavern, faladorCastle1, herosGuild, herosGuildBasement, portSarimChurch, dwarvenMine,
		miningGuild, herosGuildEntranceway, herosGuildMainHall, roguesDen, moleDen;

	ZoneRequirement inMindAltar, inWyvernCavern, inIceDungeon, inFaladorCastle1, inHerosGuild, inHerosGuildBasement, inPortSarimChurch,
		inDwarvenMine, inMiningGuild, inRoguesDen, inMoleDen;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doHard = new ConditionalStep(this, claimReward);
		doHard.addStep(new Conditions(notChangedFamilyCrest, inFaladorCastle1), changeFamilyCrest);
		doHard.addStep(notChangedFamilyCrest, climbLadderWhiteKnightCastle);
		doHard.addStep(new Conditions(notKilledMole, inMoleDen), killGiantMole);
		doHard.addStep(notKilledMole, goToGiantMole);
		doHard.addStep(notCompleteAgiCourse, completeAgiCourse);
		doHard.addStep(new Conditions(notDwarvenHelmetDwarvenMines, inDwarvenMine), equipDwarvenHelmet);
		doHard.addStep(notDwarvenHelmetDwarvenMines, enterDwarvenMinesHelmet);
		doHard.addStep(new Conditions(notEnterMiningGuildWithProspector, inDwarvenMine), enterMiningGuild);
		doHard.addStep(notEnterMiningGuildWithProspector, enterDwarvenMines);
		doHard.addStep(notEnterWarriorsGuild, enterWarriorsGuild);
		doHard.addStep(new Conditions(notKilledBlueDragon, inHerosGuildBasement), killBlueDragon);
		doHard.addStep(new Conditions(notKilledBlueDragon, inHerosGuild), enterHerosGuildBasement);
		doHard.addStep(notKilledBlueDragon, enterHerosGuild);
		doHard.addStep(new Conditions(notCrackedWallSafe, inRoguesDen), crackWallSafe);
		doHard.addStep(notCrackedWallSafe, enterRoguesDen);
		doHard.addStep(new Conditions(notCraftedMindRunes, inMindAltar), craftMindRunes);
		doHard.addStep(notCraftedMindRunes, enterMindAltar);
		doHard.addStep(new Conditions(notPraySarimAltarProsy, prosyHelm.alsoCheckBank(questBank),
				prosyLegs.alsoCheckBank(questBank), prosyChest.alsoCheckBank(questBank)),
			prayAtAltarSarim);
		doHard.addStep(notPraySarimAltarProsy, getProsySet);
		doHard.addStep(new Conditions(notKilledWyvern, inWyvernCavern), killWyvern);
		doHard.addStep(new Conditions(notKilledWyvern, inIceDungeon), enterWyvernCavern);
		doHard.addStep(notKilledWyvern, goToIceDungeon);

		return doHard;

	}

	public void setupRequirements()
	{
		notCraftedMindRunes = new VarplayerRequirement(1186, false, 26);
		notChangedFamilyCrest = new VarplayerRequirement(1186, false, 27);
		notKilledMole = new VarplayerRequirement(1186, false, 28);
		notKilledWyvern = new VarplayerRequirement(1186, false, 29);
		notCompleteAgiCourse = new VarplayerRequirement(1186, false, 30);
		notEnterMiningGuildWithProspector = new VarplayerRequirement(1186, false, 31);
		notKilledBlueDragon = new VarplayerRequirement(1187, false, 0);
		notCrackedWallSafe = new VarplayerRequirement(1187, false, 1);
		notPraySarimAltarProsy = new VarplayerRequirement(1187, false, 2);
		notEnterWarriorsGuild = new VarplayerRequirement(1187, false, 3);
		notDwarvenHelmetDwarvenMines = new VarplayerRequirement(1187, false, 4);

		pureEss28 = new ItemRequirement("Pure Essence", ItemID.PURE_ESSENCE, 28).showConditioned(notCraftedMindRunes);
		mindTiara = new ItemRequirement("Mind Tiara", ItemID.MIND_TIARA, 1, true).showConditioned(notCraftedMindRunes);
		coins10000 = new ItemRequirement("Coins", ItemCollections.getCoins(), 10000).showConditioned(notChangedFamilyCrest);
		combatGear = new ItemRequirement("Combat Gear", -1, -1);
		food = new ItemRequirement("Good healing food.", ItemCollections.getGoodEatingFood(), -1);
		lightSource = new ItemRequirement("Light Source", ItemCollections.getLightSources(), -1).showConditioned(notKilledMole);
		spade = new ItemRequirement("Spade", ItemID.SPADE).showConditioned(notKilledMole);
		wyvernProtection = new ItemRequirement("Wyvern Protection", ItemCollections.getAntiWyvernShields()).showConditioned(notKilledWyvern);
		prospectorHelm = new ItemRequirement("Prospector Helm", ItemID.PROSPECTOR_HELMET, 1, true).showConditioned(notEnterMiningGuildWithProspector);
		prospectorChest = new ItemRequirement("Prospector Chest", ItemID.PROSPECTOR_JACKET, 1, true).showConditioned(notEnterMiningGuildWithProspector);
		prospectorLegs = new ItemRequirement("Prospector Legs", ItemID.PROSPECTOR_LEGS, 1, true).showConditioned(notEnterMiningGuildWithProspector);
		prospectorBoots = new ItemRequirement("Prospector Boots", ItemID.PROSPECTOR_BOOTS, 1, true).showConditioned(notEnterMiningGuildWithProspector);
		dragonfireProtection = new ItemRequirement("Protection from Dragonfire", ItemCollections.getAntifireShields()).showConditioned(notKilledBlueDragon);
		prosyHelm = new ItemRequirement("Proselyte Helmet", ItemID.PROSELYTE_SALLET).showConditioned(notPraySarimAltarProsy);
		prosyChest = new ItemRequirement("Proselyte Chest", ItemID.PROSELYTE_HAUBERK).showConditioned(notPraySarimAltarProsy);
		prosyLegs = new ItemRequirement("Proselyte Legs", ItemID.PROSELYTE_CUISSE).showConditioned(notPraySarimAltarProsy);
		prosyLegs.addAlternates(ItemID.PROSELYTE_TASSET);
		dwarvenHelmet = new ItemRequirement("Dwarven Helmet", ItemID.DWARVEN_HELMET, 1).showConditioned(notDwarvenHelmetDwarvenMines);

		faladorTeleport = new ItemRequirement("Multiple teleports to Falador", ItemID.FALADOR_TELEPORT, -1);
		combatBracelet = new ItemRequirement("Combat Bracelet", ItemCollections.getCombatBracelets());
		combatBracelet.addAlternates(ItemCollections.getGamesNecklaces());

		prosySet = new ItemRequirements(prosyHelm, prosyLegs, prosyChest);
		prospectorSet = new ItemRequirements(prospectorBoots, prospectorChest, prospectorHelm, prospectorLegs);

		inMindAltar = new ZoneRequirement(mindAltar);
		inWyvernCavern = new ZoneRequirement(wyvernCavern);
		inIceDungeon = new ZoneRequirement(iceDungeon);
		inFaladorCastle1 = new ZoneRequirement(faladorCastle1);
		inHerosGuild = new ZoneRequirement(herosGuild);
		inHerosGuildBasement = new ZoneRequirement(herosGuildBasement);
		inPortSarimChurch = new ZoneRequirement(portSarimChurch);
		inDwarvenMine = new ZoneRequirement(dwarvenMine);
		inMiningGuild = new ZoneRequirement(miningGuild);
		inHerosGuildBasement = new ZoneRequirement(herosGuildBasement);
		inHerosGuild = new ZoneRequirement(herosGuildEntranceway, herosGuildMainHall);
		inRoguesDen = new ZoneRequirement(roguesDen);
		inMoleDen = new ZoneRequirement(moleDen);

		slugMenace = new QuestRequirement(QuestHelperQuest.THE_SLUG_MENACE, QuestState.FINISHED);
		herosQuest = new QuestRequirement(QuestHelperQuest.HEROES_QUEST, QuestState.FINISHED);
		grimTales = new QuestRequirement(QuestHelperQuest.GRIM_TALES, QuestState.FINISHED);
	}

	public void loadZones()
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
		enterMindAltar = new ObjectStep(this, ObjectID.MYSTERIOUS_RUINS_29094, new WorldPoint(2982, 3514, 0),
			"Click on the Mysterious Ruins, with the Mind Tiara equipped to access the Mind Altar.", mindTiara, pureEss28);
		craftMindRunes = new ObjectStep(this, ObjectID.ALTAR_34761, new WorldPoint(2786, 4841, 0),
			"Click the altar to craft the mind runes.", mindTiara, pureEss28);
		craftMindRunes.addIcon(ItemID.PURE_ESSENCE);
		enterMindAltar.addSubSteps(craftMindRunes);

		//Change Family Crest to Saradomin
		climbLadderWhiteKnightCastle = new ObjectStep(this, ObjectID.LADDER_24070, new WorldPoint(2994, 3341, 0),
			"Climb the ladder in The White Knights Castle.", coins10000);
		changeFamilyCrest = new NpcStep(this, NpcID.SIR_RENITEE, new WorldPoint(2982, 3341, 1),
			"Speak to Sir Renitee to change your family crest to Saradomin.", coins10000);
		changeFamilyCrest.addDialogStep("I don't know, what can you do for me?");
		changeFamilyCrest.addDialogStep("Can you see if I have a family crest?");
		changeFamilyCrest.addDialogStep("I don't like that crest. Can I have a different one?");
		changeFamilyCrest.addDialogStepWithExclusion("More...", "Saradomin");
		changeFamilyCrest.addDialogStep("Saradomin");

		//Giant Mole
		goToGiantMole = new ObjectStep(this, ObjectID.MOLE_HILL, new WorldPoint(2989, 3378, 0),
			"Use a spade on one of the Mole Hills in Falador Park", combatGear, food, lightSource, spade);
		killGiantMole = new NpcStep(this, NpcID.GIANT_MOLE,
			"Kill the Giant Mole.", combatGear, food, lightSource);

		//Wyvern
		goToIceDungeon = new ObjectStep(this, ObjectID.TRAPDOOR_1738, new WorldPoint(3008, 3150, 0),
			"Go down the ladder south of Port Sarim.", combatGear, food, wyvernProtection);
		enterWyvernCavern = new ObjectStep(this, ObjectID.ICY_CAVERN_10596, new WorldPoint(3055, 9560, 0),
			"Enter the Icy Cavern at the back of the dungeon.", combatGear, food, wyvernProtection);
		killWyvern = new NpcStep(this, NpcID.SKELETAL_WYVERN,
			"Kill a Skeletal Wyvern.", combatGear, food, wyvernProtection);

		//Agi Course
		completeAgiCourse = new ObjectStep(this, ObjectID.ROUGH_WALL_14898, new WorldPoint(3036, 3341, 0),
			"Complete a lap of the Falador Agility Course");

		//Prospectors in Mining Guild
		enterDwarvenMines = new ObjectStep(this, ObjectID.STAIRCASE_16664, new WorldPoint(3059, 3376, 0),
			"Go to the Dwarven Mines.", prospectorBoots, prospectorChest, prospectorLegs, prospectorHelm);
		enterMiningGuild = new ObjectStep(this, ObjectID.DOOR_30364, new WorldPoint(3046, 9756, 0),
			"Equip your prospector set and then enter the Mining Guild", prospectorBoots, prospectorChest, prospectorLegs, prospectorHelm);

		//Blue Dragon
		enterHerosGuild = new ObjectStep(this, ObjectID.DOOR_2624, new WorldPoint(2902, 3501, 0),
			"Go to the Hero's Guild south of Burthorpe. You can get here faster by teleporting with a Combat Bracelet to the Warriors Guild.", combatGear, food, dragonfireProtection);
		enterHerosGuildBasement = new ObjectStep(this, ObjectID.LADDER_17384, new WorldPoint(2892, 3507, 0),
			"Climb down the ladder in the Hero's Guild.");
		killBlueDragon = new NpcStep(this, NpcID.BLUE_DRAGON_266,
			"Kill the Blue Dragon to complete your task.");

		//Rogues Den
		enterRoguesDen = new ObjectStep(this, ObjectID.TRAPDOOR_7257, new WorldPoint(2905, 3537, 0),
			"Go to the Rogues Den in Burthorpe.");
		crackWallSafe = new ObjectStep(this, ObjectID.WALL_SAFE, new WorldPoint(3055, 4977, 1),
			"Crack the Wall Safe inside of the Rogues Den.");

		//Pray in Port Sarim in Proselyte
		getProsySet = new NpcStep(this, NpcID.SIR_TIFFY_CASHIEN, new WorldPoint(2997, 3373, 0),
			"Speak to Sir Tiffy Cashien to purchase a set of Proselyte Armor for 28,000 Coins for a full set.");
		getProsySet.addDialogStep("Can I buy some armor?");
		prayAtAltarSarim = new ObjectStep(this, ObjectID.ALTAR, new WorldPoint(2995, 3177, 0),
			"Equip your Proselyte armor and pray at the altar in Port Sarim.", prosyHelm, prosyChest, prosyLegs);

		//Warriors Guild
		enterWarriorsGuild = new ObjectStep(this, ObjectID.DOOR_24318, new WorldPoint(2896, 3510, 0),
			"Enter the Warriors Guild, in Burthorpe. You can get here faster by teleporting with a combat bracelet or a games necklace.");

		//Dwarven Helm
		//Prospectors in Mining Guild
		enterDwarvenMinesHelmet = new ObjectStep(this, ObjectID.STAIRCASE_16664, new WorldPoint(3059, 3376, 0),
			"Go to the Dwarven Mines.", dwarvenHelmet);
		equipDwarvenHelmet = new DetailedQuestStep(this,
			"Equip the Dwarven Helmet.", dwarvenHelmet.equipped());

		//Fin
		claimReward = new NpcStep(this, NpcID.SIR_REBRAL, new WorldPoint(2977, 3346, 0),
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
			prospectorBoots, prospectorChest, prospectorLegs, prosyHelm, prosyChest, prosyLegs, dwarvenHelmet);
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
		req.add(new SkillRequirement(Skill.AGILITY, 59, true));
		req.add(new SkillRequirement(Skill.DEFENCE, 50));
		req.add(new SkillRequirement(Skill.SLAYER, 72, true));
		req.add(new SkillRequirement(Skill.MINING, 60, true));
		req.add(new SkillRequirement(Skill.PRAYER, 70));
		req.add(new SkillRequirement(Skill.RUNECRAFT, 56, true));
		req.add(new SkillRequirement(Skill.THIEVING, 50, true));
		req.add(new WarriorsGuildAccessRequirement());


		req.add(grimTales);
		req.add(herosQuest);
		req.add(slugMenace);

		return req;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("Falador Shield (3)", ItemID.FALADOR_SHIELD_3, 1),
				new ItemReward("15,000 Exp. Lamp (Any skill over 50)", ItemID.ANTIQUE_LAMP, 1));
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
			changeFamilyCrest), new SkillRequirement(Skill.PRAYER, 70), coins10000);
		changeCrestSteps.setDisplayCondition(notChangedFamilyCrest);
		allSteps.add(changeCrestSteps);

		PanelDetails moleSteps = new PanelDetails("Holy Moley!", Arrays.asList(goToGiantMole, killGiantMole),
			lightSource, spade, combatGear, food);
		moleSteps.setDisplayCondition(notKilledMole);
		allSteps.add(moleSteps);

		PanelDetails fallyRoofSteps = new PanelDetails("Make sure to stretch!",
			Collections.singletonList(completeAgiCourse), new SkillRequirement(Skill.AGILITY, 50));
		fallyRoofSteps.setDisplayCondition(notCompleteAgiCourse);
		allSteps.add(fallyRoofSteps);

		PanelDetails dwarvenHelmSteps = new PanelDetails("A snug fit", Arrays.asList(enterDwarvenMinesHelmet,
			equipDwarvenHelmet), new SkillRequirement(Skill.DEFENCE, 50), dwarvenHelmet), grimTales;
		dwarvenHelmSteps.setDisplayCondition(notDwarvenHelmetDwarvenMines);
		allSteps.add(dwarvenHelmSteps);

		PanelDetails miningGuildSteps = new PanelDetails("Gold Rush!", Arrays.asList(enterDwarvenMines,
			enterMiningGuild), new SkillRequirement(Skill.MINING, 60, true), prospectorHelm, prospectorBoots,
			prospectorChest, prospectorLegs);
		miningGuildSteps.setDisplayCondition(notEnterMiningGuildWithProspector);
		allSteps.add(miningGuildSteps);

		PanelDetails warriorsGuildSteps = new PanelDetails("The Dragon Defender",
			Collections.singletonList(enterWarriorsGuild), new WarriorsGuildAccessRequirement());
		warriorsGuildSteps.setDisplayCondition(notEnterWarriorsGuild);
		allSteps.add(warriorsGuildSteps);

		PanelDetails blueDragonSteps = new PanelDetails("The Dragon Slayer", Arrays.asList(enterHerosGuild,
			enterHerosGuildBasement, killBlueDragon), herosQuest, combatGear, food, dragonfireProtection);
		blueDragonSteps.setDisplayCondition(notKilledBlueDragon);
		allSteps.add(blueDragonSteps);

		PanelDetails crackSafeSteps = new PanelDetails("The cat burglar", Arrays.asList(enterRoguesDen, crackWallSafe),
			new SkillRequirement(Skill.THIEVING, 50, true));
		crackSafeSteps.setDisplayCondition(notCrackedWallSafe);
		allSteps.add(crackSafeSteps);

		PanelDetails mindRunesSteps = new PanelDetails("Do you mind?", Arrays.asList(enterMindAltar, craftMindRunes),
			new SkillRequirement(Skill.RUNECRAFT, 56, true), mindTiara, pureEss28);
		mindRunesSteps.setDisplayCondition(notCraftedMindRunes);
		allSteps.add(mindRunesSteps);

		PanelDetails praySteps = new PanelDetails("Praise the Lord!", Arrays.asList(getProsySet, prayAtAltarSarim),
			new SkillRequirement(Skill.DEFENCE, 30), slugMenace, prosyHelm, prosyChest, prosyLegs);
		praySteps.setDisplayCondition(notPraySarimAltarProsy);
		allSteps.add(praySteps);

		PanelDetails wyvernSteps = new PanelDetails("This ain't no dragon!", Arrays.asList(goToIceDungeon,
			enterWyvernCavern, killWyvern), new SkillRequirement(Skill.SLAYER, 72, true), combatGear, food,
			wyvernProtection);
		wyvernSteps.setDisplayCondition(notKilledWyvern);
		allSteps.add(wyvernSteps);

		PanelDetails finishOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishOffSteps);

		return allSteps;
	}
}

