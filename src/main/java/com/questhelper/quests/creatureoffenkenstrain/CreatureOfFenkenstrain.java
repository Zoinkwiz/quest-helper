/*
 * Copyright (c) 2020, itofu1
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
package com.questhelper.quests.creatureoffenkenstrain;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.AbstractRequirement;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.requirements.ItemRequirements;
import com.questhelper.requirements.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.SkillRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.DigStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.requirements.conditional.*;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

import java.util.*;

@QuestDescriptor(
        quest = QuestHelperQuest.CREATURE_OF_FENKENSTRAIN
)
public class CreatureOfFenkenstrain extends BasicQuestHelper
{
	ItemRequirement armor, hammer, ghostSpeakAmulet, silverBar, bronzeWire, needle, thread, spade, coins, telegrabOrCoins, pickledBrain,
		obsidianAmulet, marbleAmulet, starAmulet, decapitatedHead, decapitatedHeadWithBrain, cavernKey, torso, legs, arms,
		shedKey, brush, canes, extendedBrush1, extendedBrush2, extendedBrush3, conductorMold, lightningRod, towerKey,
		fenkenstrainTeleports, teleportToFurnance, staminaPotion;
	Zone barZone, castleZoneFloor1, castleZoneFloor2, experimentCave, graveIsland, castleTower, monsterTower;
	ConditionForStep hasPickledBrain, inCanifisBar, inCastleFloor1, inCastleFloor2, hasObsidianAmulet, hasMarbleAmulet, hasStarAmulet,
		hasGhostSpeakAmulet, followingGardenerForHead, hasDecapitatedHead, hasDecapitatedHeadWithBrain, putStarOnGrave, inExperiementCave,
		hasCavernKey, hasTorso, hasLegs, hasArm, inGraveIsland, hasShedKey, usedShedKey, hasBrush, hasBrush1, hasBrush2, hasBrush3,
		hasCanes, hasLightningRod, hasMould, inCastleTower, usedTowerKey, hasTowerKey, inMonsterTower;
	QuestStep getPickledBrain, talkToFrenkenstrain, getBodyParts, goUpstairsForStar, getBook1, getBook2, combineAmulet,
		goDownstairsForStar, talkToGardenerForHead, goToHeadGrave, combinedHead, useStarOnGrave, killExperiment, leaveExperimentCave,
		getTorso, getArm, getLeg, deliverBodyParts, gatherNeedleAndThread, fixConductorRod, talkToGardenerForKey, searchForBrush,
		grabCanes, extendBrush, goUpWestStairs, searchFirePlace, makeLightningRod, goUpWestStairsWithRod, goUpTowerLadder,
		repairConductor, goBackToFirstFloor, talkToFenkenstrainAfterFixingRod, goToMonsterFloor1, openLockedDoor, goToMonsterFloor2,
		talkToMonster, pickPocketFenkenstrain;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep grabTheBrain = new ConditionalStep(this, getPickledBrain);
		grabTheBrain.addStep(hasPickledBrain, talkToFrenkenstrain);
		steps.put(0, grabTheBrain);

		ConditionalStep gatherBodyParts = new ConditionalStep(this, getBodyParts, ghostSpeakAmulet.equipped(), spade);
		gatherBodyParts.addStep(new Conditions(LogicType.NOR, hasMarbleAmulet, hasObsidianAmulet, hasDecapitatedHeadWithBrain), goUpstairsForStar);
		gatherBodyParts.addStep(new Conditions(inCastleFloor2, new Conditions(LogicType.NOR, hasMarbleAmulet, hasObsidianAmulet)), getBook1);
		gatherBodyParts.addStep(new Conditions(inCastleFloor2, hasObsidianAmulet, new Conditions(LogicType.NOR, hasMarbleAmulet)), getBook2);
		gatherBodyParts.addStep(new Conditions(hasObsidianAmulet, hasMarbleAmulet), combineAmulet);
		gatherBodyParts.addStep(new Conditions(inCastleFloor2, hasStarAmulet, new Conditions(LogicType.NOR, hasDecapitatedHead, hasDecapitatedHeadWithBrain)), goDownstairsForStar);
		gatherBodyParts.addStep(new Conditions(hasStarAmulet, new Conditions(LogicType.NOR, hasDecapitatedHead,
			hasDecapitatedHeadWithBrain)), talkToGardenerForHead);
		gatherBodyParts.addStep(followingGardenerForHead, goToHeadGrave);
		gatherBodyParts.addStep(new Conditions(hasDecapitatedHead, hasPickledBrain), combinedHead);
		gatherBodyParts.addStep(new Conditions(hasDecapitatedHeadWithBrain, hasStarAmulet), useStarOnGrave);
		gatherBodyParts.addStep(new Conditions(hasDecapitatedHeadWithBrain, inExperiementCave, new Conditions(LogicType.NOR, hasCavernKey, hasArm, hasLegs, hasTorso)), killExperiment);
		gatherBodyParts.addStep(new Conditions(hasDecapitatedHeadWithBrain, inExperiementCave, hasCavernKey, new Conditions(LogicType.NOR, hasArm, hasLegs, hasTorso)), leaveExperimentCave);
		gatherBodyParts.addStep(new Conditions(inGraveIsland, new Conditions(LogicType.NOR, hasArm, hasLegs, hasTorso)), getTorso);
		gatherBodyParts.addStep(new Conditions(inGraveIsland, hasTorso, new Conditions(LogicType.NOR, hasArm, hasLegs)), getArm);
		gatherBodyParts.addStep(new Conditions(inGraveIsland, hasTorso, hasArm, new Conditions(LogicType.NOR, hasLegs)), getLeg);
		gatherBodyParts.addStep(new Conditions(hasArm, hasLegs, hasTorso), deliverBodyParts);

		ConditionalStep fixLightningRod = new ConditionalStep(this, fixConductorRod);
		fixLightningRod.addStep(new Conditions(LogicType.NOR, usedShedKey, hasShedKey), talkToGardenerForKey);
		fixLightningRod.addStep(new Conditions(LogicType.NOR, hasBrush, hasBrush1, hasBrush2, hasBrush3), searchForBrush);
		fixLightningRod.addStep(new Conditions(hasBrush, new Conditions(LogicType.NOR, hasCanes, hasBrush1, hasBrush2, hasBrush3)), grabCanes);
		fixLightningRod.addStep(new Conditions(LogicType.OR, new Conditions(LogicType.AND, hasBrush, hasCanes), hasBrush1, hasBrush2), extendBrush);
		fixLightningRod.addStep(new Conditions(new Conditions(LogicType.NOR, inCastleFloor2), hasBrush3), goUpWestStairs);
		fixLightningRod.addStep(new Conditions(inCastleFloor2, hasBrush3, new Conditions(LogicType.NOR, hasLightningRod, hasMould)), searchFirePlace);
		fixLightningRod.addStep(new Conditions(hasMould, new Conditions(LogicType.NOR, hasLightningRod)), makeLightningRod);
		fixLightningRod.addStep(new Conditions(new Conditions(LogicType.NOR, inCastleFloor2), hasLightningRod), goUpWestStairs);
		fixLightningRod.addStep(new Conditions(hasLightningRod, inCastleFloor2), goUpTowerLadder);
		fixLightningRod.addStep(new Conditions(inCastleTower, hasLightningRod), repairConductor);

		ConditionalStep talkToFenkentrain = new ConditionalStep(this, goBackToFirstFloor);
		talkToFenkentrain.addStep(inCastleFloor1, talkToFenkenstrainAfterFixingRod);

		ConditionalStep goToMonster = new ConditionalStep(this, goToMonsterFloor1);
		goToMonster.addStep(new Conditions(hasTowerKey, new Conditions(LogicType.NOR, inCastleFloor2)), openLockedDoor);
		goToMonster.addStep(new Conditions(hasTowerKey, inCastleFloor2), openLockedDoor);
		goToMonster.addStep(new Conditions(usedTowerKey, inCastleFloor2), goToMonsterFloor2);
		goToMonster.addStep(inMonsterTower, talkToMonster);

		steps.put(1, gatherBodyParts);
		steps.put(2, gatherNeedleAndThread);
		steps.put(3, fixLightningRod);
		steps.put(4, talkToFenkentrain);
		steps.put(5, goToMonster);
		steps.put(6, pickPocketFenkenstrain);

		return steps;
	}

	public void setupItemRequirements()
	{
		ItemRequirement telegrab = new ItemRequirements("Telegrab runes", new ItemRequirement("Law rune",
			ItemID.LAW_RUNE), new ItemRequirement("Air rune", ItemID.AIR_RUNE));
		ItemRequirement coins50 = new ItemRequirement("Coins", ItemID.COINS_995, 50);

		// TODO: Add magic req once rebased
		telegrabOrCoins = new ItemRequirements(LogicType.OR,
			"33 Magic and runes to cast telegrab, or 50 coins",
			coins50, telegrab);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER);
		ghostSpeakAmulet = new ItemRequirement("Ghostspeak amulet", ItemCollections.getGhostspeak());
		silverBar = new ItemRequirement("Silver bar", ItemID.SILVER_BAR);
		bronzeWire = new ItemRequirement("Bronze wires", ItemID.BRONZE_WIRE, 3);
		needle = new ItemRequirement("Needle", ItemID.NEEDLE);
		thread = new ItemRequirement("Threads", ItemID.THREAD, 5);
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		coins = new ItemRequirement("Coins at least", ItemID.COINS_995, 100);
		pickledBrain = new ItemRequirement("Pickled Brain", ItemID.PICKLED_BRAIN);
		obsidianAmulet = new ItemRequirement("Obsidian Amulet", ItemID.OBSIDIAN_AMULET);
		marbleAmulet = new ItemRequirement("Marble Amulet", ItemID.MARBLE_AMULET);
		starAmulet = new ItemRequirement("Star Amulet", ItemID.STAR_AMULET);
		decapitatedHead = new ItemRequirement("Decapitated Head", ItemID.DECAPITATED_HEAD);
		decapitatedHeadWithBrain = new ItemRequirement("Decapitated Head (with brain)", ItemID.DECAPITATED_HEAD_4198);
		armor = new ItemRequirement("Armour and weapons defeat a level 51 monster and run past level 72 monsters", -1, -1);
		armor.setDisplayItemId(BankSlotIcons.getCombatGear());
		cavernKey = new ItemRequirement("Tavern Key", ItemID.CAVERN_KEY);
		torso = new ItemRequirement("Torso", ItemID.TORSO);
		legs = new ItemRequirement("Legs", ItemID.LEGS);
		arms = new ItemRequirement("Arms", ItemID.ARMS);
		shedKey = new ItemRequirement("Shed key", ItemID.SHED_KEY);
		brush = new ItemRequirement("Brush", ItemID.GARDEN_BRUSH);
		canes = new ItemRequirement("Garden Canes (3)", ItemID.GARDEN_CANE);
		extendedBrush1 = new ItemRequirement("Extended Brush", ItemID.EXTENDED_BRUSH);
		extendedBrush2 = new ItemRequirement("Extended Brush", ItemID.EXTENDED_BRUSH_4192);
		extendedBrush3 = new ItemRequirement("Extended Brush", ItemID.EXTENDED_BRUSH_4193);
		conductorMold = new ItemRequirement("Conductor Mold", ItemID.CONDUCTOR_MOULD);
		lightningRod = new ItemRequirement("Lightning Rod", ItemID.CONDUCTOR);
		towerKey = new ItemRequirement("Tower Key", ItemID.TOWER_KEY);

		fenkenstrainTeleports = new ItemRequirement("Fenkenstrain's Castle Teleport", ItemID.FENKENSTRAINS_CASTLE_TELEPORT, 2);
		teleportToFurnance = new ItemRequirement("Teleport to any furnance such as glory for edgeville teleport, ectophial to Port Phasmatys or a Falador teleport",
			ItemCollections.getAmuletOfGlories());
		teleportToFurnance.addAlternates(ItemID.ECTOPHIAL, ItemID.FALADOR_TELEPORT);
		staminaPotion = new ItemRequirement("Stamina potions", ItemCollections.getStaminaPotions(), -1);
	}

	public void setupZones()
	{
		barZone = new Zone(new WorldPoint(3488, 3477, 0), new WorldPoint(3504, 3471, 0));
		castleZoneFloor1 = new Zone(new WorldPoint(3526, 3574, 0), new WorldPoint(3566, 3531, 0));
		castleZoneFloor2 = new Zone(new WorldPoint(3526, 3574, 1), new WorldPoint(3566, 3531, 1));
		experimentCave = new Zone(new WorldPoint(3490, 9977, 0), new WorldPoint(3583, 9922, 0));
		graveIsland = new Zone(new WorldPoint(3484, 3585, 0), new WorldPoint(3517, 3561, 0));
		castleTower = new Zone(new WorldPoint(3544, 3543, 2), new WorldPoint(3552, 3536, 2));
		monsterTower = new Zone(new WorldPoint(3544, 3558, 2), new WorldPoint(3553, 3551, 2));
	}

	public void setupConditions()
	{
		inCanifisBar = new ZoneCondition(barZone);
		inCastleFloor1 = new ZoneCondition(castleZoneFloor1);
		inCastleFloor2 = new ZoneCondition(castleZoneFloor2);
		hasPickledBrain = new ItemRequirementCondition(pickledBrain);
		hasMarbleAmulet = new ItemRequirementCondition(marbleAmulet);
		hasObsidianAmulet = new ItemRequirementCondition(obsidianAmulet);
		hasGhostSpeakAmulet = new ItemRequirementCondition(ghostSpeakAmulet);
		hasStarAmulet = new ItemRequirementCondition(starAmulet);
		followingGardenerForHead = new VarbitCondition(185, 1);
		hasDecapitatedHead = new ItemRequirementCondition(decapitatedHead);
		hasDecapitatedHeadWithBrain = new ItemRequirementCondition(decapitatedHeadWithBrain);
		putStarOnGrave = new VarbitCondition(192, 1);
		inExperiementCave = new ZoneCondition(experimentCave);
		inGraveIsland = new ZoneCondition(graveIsland);
		hasCavernKey = new ItemRequirementCondition(cavernKey);
		hasTorso = new ItemRequirementCondition(torso);
		hasLegs = new ItemRequirementCondition(legs);
		hasArm = new ItemRequirementCondition(arms);

		hasShedKey = new ItemRequirementCondition(shedKey);
		usedShedKey = new VarbitCondition(200, 1);
		hasBrush = new ItemRequirementCondition(brush);
		hasBrush1 = new ItemRequirementCondition(extendedBrush1);
		hasBrush2 = new ItemRequirementCondition(extendedBrush2);
		hasBrush3 = new ItemRequirementCondition(extendedBrush3);
		hasCanes = new ItemRequirementCondition(canes);
		hasLightningRod = new ItemRequirementCondition(lightningRod);
		hasMould = new ItemRequirementCondition(conductorMold);
		inCastleTower = new ZoneCondition(castleTower);

		usedTowerKey = new VarbitCondition(198, 1);
		hasTowerKey = new ItemRequirementCondition(towerKey);
		inMonsterTower = new ZoneCondition(monsterTower);
	}

	public void setupSteps()
	{
		getPickledBrain = new DetailedQuestStep(this, new WorldPoint(3492, 3474, 0),
			"Head to the Canifis bar and either buy the pickled brain for 50 coins, or telegrab it.", telegrabOrCoins);
		getPickledBrain.addDialogStep("I'll buy one.");
		talkToFrenkenstrain = new NpcStep(this, NpcID.DR_FENKENSTRAIN, new WorldPoint(3551, 3548, 0),
			"Talk to Dr.Fenkenstrain to start the quest");
		talkToFrenkenstrain.addDialogStep("Yes.");
		talkToFrenkenstrain.addDialogStep("Braindead");
		talkToFrenkenstrain.addDialogStep("Grave-digging");

		getBodyParts = new DetailedQuestStep(this, "You will now need to gather the 4 body parts");

		goUpstairsForStar = new ObjectStep(this, ObjectID.STAIRCASE_5206, new WorldPoint(3560, 3552, 0),
			"Go up the staircase to grab the items you will need.");

		getBook1 = new ObjectStep(this, ObjectID.BOOKCASE_5166, new WorldPoint(3555, 3558, 1),
			"Search the nearby bookcase for Handy Maggot Avoidance Techniques.");
		getBook1.addDialogSteps("Handy Maggot Avoidance Techniques");

		getBook2 = new ObjectStep(this, ObjectID.BOOKCASE_5166, new WorldPoint(3542, 3558, 1),
			"Search the west bookcase for The Joy of Grave Digging.");
		getBook2.addDialogSteps("The Joy of Gravedigging");

		combineAmulet = new ItemStep(this, "Combine the two amulet by using one on the other.",
			marbleAmulet.highlighted(),
			obsidianAmulet.highlighted());

		goDownstairsForStar = new ObjectStep(this, ObjectID.STAIRCASE_5207, new WorldPoint(3573, 3553, 1),
			"Go back to the ground floor");

		talkToGardenerForHead = new NpcStep(this, NpcID.GARDENER_GHOST, new WorldPoint(3548, 3562, 0),
			"Talk to the Gardener Ghost, have your Ghostspeak amulet equipped", ghostSpeakAmulet);
		talkToGardenerForHead.addDialogStep("What happened to your head?");

		goToHeadGrave = new DigStep(this, new WorldPoint(3608, 3490, 0), "Go to the grave of the gardener and dig for his head");

		combinedHead = new ItemStep(this,
			"Use the decapitated head on the pickled brain to create a decapitated head", decapitatedHead, pickledBrain);

		useStarOnGrave = new ObjectStep(this, ObjectID.MEMORIAL, new WorldPoint(3578, 3527, 0),
			"Use the Star Amulet on the memorial and push it to go in the caves", starAmulet);

		killExperiment = new NpcStep(this, NpcID.EXPERIMENT, new WorldPoint(3557, 9946, 0),
			"Kill the level 51 Experiment north-west of the ladder to get a key");

		leaveExperimentCave = new ObjectStep(this, ObjectID.LADDER_17387, new WorldPoint(3504, 9970, 0),
			"Leave the caves by going north-west, be sure to pick up the key from the level 51 experiment");

		getTorso = new DigStep(this, new WorldPoint(3503, 3576, 0), "Use your spade on this tile to get torsos");

		getArm = new DigStep(this, new WorldPoint(3504, 3576, 0), "Use your spade on this tile to get arms");

		getLeg = new DigStep(this, new WorldPoint(3505, 3576, 0), "Use your spade on this tile to get legs");

		deliverBodyParts = new NpcStep(this, NpcID.DR_FENKENSTRAIN, new WorldPoint(3551, 3548, 0),
			"Deliver the body parts to Dr.Fenkenstrain, use a teleport to Fenkenstrain's castle or run back through the caves");
		deliverBodyParts.addDialogStep("I have some body parts for you.");

		gatherNeedleAndThread = new NpcStep(this, NpcID.DR_FENKENSTRAIN, new WorldPoint(3551, 3548, 0),
			"Get a needle and 5 threads and deliver them to Dr.Fenkenstrain", needle, thread);

		fixConductorRod = new DetailedQuestStep(this, "You will now need to fix the lightning rod for Dr.Frenkenstrain");
		talkToGardenerForKey = new NpcStep(this, NpcID.GARDENER_GHOST, new WorldPoint(3548, 3562, 0),
			"Talk to the Gardener Ghost and ask for the shed key", bronzeWire, silverBar);
		talkToGardenerForKey.addDialogStep("Do you know where the key to the shed is?");

		searchForBrush = new ObjectStep(this, ObjectID.CUPBOARD_5156, new WorldPoint(3546, 3563, 0),
			"Open the cupboard and search it for a brush", bronzeWire, silverBar);
		grabCanes = new ObjectStep(this, ObjectID.PILE_OF_CANES, new WorldPoint(3551, 3564, 0),
			"Grab 3 canes from the pile", bronzeWire, silverBar);
		extendBrush = new DetailedQuestStep(this, "Use all 3 canes on the brush one at a time", canes, brush, bronzeWire, silverBar);

		goUpWestStairs = new ObjectStep(this, ObjectID.STAIRCASE_5206, new WorldPoint(3537, 3553, 0),
			"Go up to the second floor of the castle");
		searchFirePlace = new ObjectStep(this, ObjectID.FIREPLACE_5165, new WorldPoint(3544, 3555, 1),
			"Use the extended brush on the fireplace to get the conductor mould", silverBar);
		makeLightningRod = new DetailedQuestStep(this,
			"Go to any furnace and use the silver bar on the furnace with the conductor mould in your inventory", silverBar);
		goUpWestStairsWithRod = new ObjectStep(this, ObjectID.STAIRCASE_5206, new WorldPoint(3537, 3553, 0),
			"Return to the castle and go upstairs");
		goUpTowerLadder = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(3548, 3539, 1),
			"Go up to the third floor using the ladder in the middle of the castle");
		repairConductor = new ObjectStep(this, ObjectID.LIGHTNING_CONDUCTOR, new WorldPoint(3548, 3537, 2),
			"Repair the lightning Conductor");

		goBackToFirstFloor = new DetailedQuestStep(this, "Go back to the the first floor of the castle and talk to Dr.Fenkenstrain");
		talkToFenkenstrainAfterFixingRod = new NpcStep(this, NpcID.DR_FENKENSTRAIN, new WorldPoint(3551, 3548, 0),
			"Go back to the the first floor of the castle and talk to Dr.Fenkenstrain");

		goToMonsterFloor1 = new ObjectStep(this, ObjectID.STAIRCASE_5206, new WorldPoint(3537, 3553, 0),
			"You will now confront Fenkenstrain's monster, go up to the second floor");
		openLockedDoor = new ObjectStep(this, ObjectID.DOOR_5172, new WorldPoint(3548, 3552, 1),
			"Use the Tower Key on the door");
		goToMonsterFloor2 = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(3548, 3554, 1),
			"Go up the ladder");
		talkToMonster = new NpcStep(this, NpcID.FENKENSTRAINS_MONSTER, new WorldPoint(3548, 3555, 2),
			"Talk to Fenkenstrain's monster");

		pickPocketFenkenstrain = new NpcStep(this, NpcID.DR_FENKENSTRAIN, new WorldPoint(3551, 3548, 0),
			"Go back to Dr.Fenkenstrain, instead of talking to him right click and pickpocket him");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(hammer, ghostSpeakAmulet, silverBar, bronzeWire, needle, thread, spade, coins, telegrabOrCoins, armor));
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(fenkenstrainTeleports);
		reqs.add(teleportToFurnance);
		reqs.add(staminaPotion);

		return reqs;
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Collections.singletonList("Able to defeat an experiment (level 51)"));
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.PRIEST_IN_PERIL, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.THE_RESTLESS_GHOST, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.CRAFTING, 20, true));
		req.add(new SkillRequirement(Skill.THIEVING, 25, true));
		return req;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(getPickledBrain, talkToFrenkenstrain),
			telegrabOrCoins));
		allSteps.add(new PanelDetails("Graverobbing", Arrays.asList(goUpstairsForStar,
			getBook1, getBook2, combineAmulet, goDownstairsForStar, talkToGardenerForHead, goToHeadGrave, combinedHead,
			useStarOnGrave, killExperiment, leaveExperimentCave, getTorso, getArm, getLeg, deliverBodyParts)));
		allSteps.add(new PanelDetails("Getting tools",
			Collections.singletonList(gatherNeedleAndThread)));
		allSteps.add(new PanelDetails("Attracting lightning",
			Arrays.asList(talkToGardenerForKey, searchForBrush, grabCanes, extendBrush, goUpWestStairs,
				searchFirePlace, makeLightningRod, goUpWestStairsWithRod, goUpTowerLadder,
			repairConductor, goBackToFirstFloor, talkToFenkenstrainAfterFixingRod)));
		allSteps.add(new PanelDetails("Facing the monster", Arrays.asList(goToMonsterFloor1,
			openLockedDoor, goToMonsterFloor2, talkToMonster)));
		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(pickPocketFenkenstrain)));
		return allSteps;
	}
}
