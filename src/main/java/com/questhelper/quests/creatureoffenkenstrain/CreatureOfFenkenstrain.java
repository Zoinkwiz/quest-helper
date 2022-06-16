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
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.DigStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
        quest = QuestHelperQuest.CREATURE_OF_FENKENSTRAIN
)
public class CreatureOfFenkenstrain extends BasicQuestHelper
{
	ItemRequirement armor, hammer, ghostSpeakAmulet, silverBar, bronzeWire, needle, thread, spade, coins, telegrabOrCoins, pickledBrain,
		obsidianAmulet, marbleAmulet, starAmulet, decapitatedHead, decapitatedHeadWithBrain, cavernKey, torso, legs, arms,
		shedKey, brush, canes, extendedBrush3, conductorMould, lightningRod, towerKey,
		fenkenstrainTeleports, teleportToFurnace, staminaPotion;
	Zone barZone, castleZoneFloor0, castleZoneFloor1, experimentCave, graveIsland, castleTower, monsterTower;
	Requirement inCanifisBar, inCastleFloor0, inCastleFloor1, followingGardenerForHead, putStarOnGrave, inExperiementCave,
		inGraveIsland, inCastleTower, usedTowerKey, inMonsterTower, keyNearby, hasDecapitatedHeadWithBrain, hasArm, hasLegs,
		hasTorso, hasCavernKey, hasStarAmulet, hasObsidianAmulet, hasMarbleAmulet, usedShedKey;
	QuestStep getPickledBrain, talkToFrenkenstrain, goUpstairsForStar, getBook1, getBook2, combineAmulet, pickupKey,
		goDownstairsForStar, talkToGardenerForHead, goToHeadGrave, combinedHead, useStarOnGrave, killExperiment, leaveExperimentCave,
		getTorso, getArm, getLeg, deliverBodyParts, gatherNeedleAndThread, talkToGardenerForKey, searchForBrush,
		grabCanes, extendBrush, goUpWestStairs, searchFirePlace, makeLightningRod, goUpWestStairsWithRod, goUpTowerLadder,
		repairConductor, goBackToFirstFloor, talkToFenkenstrainAfterFixingRod, goToMonsterFloor1, openLockedDoor, goToMonsterFloor2,
		talkToMonster, pickPocketFenkenstrain, enterExperimentCave;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep grabTheBrain = new ConditionalStep(this, getPickledBrain);
		grabTheBrain.addStep(pickledBrain, talkToFrenkenstrain);
		steps.put(0, grabTheBrain);

		ConditionalStep gatherBodyParts = new ConditionalStep(this, goUpstairsForStar);
		gatherBodyParts.addStep(new Conditions(hasDecapitatedHeadWithBrain, hasArm, hasLegs, hasTorso), deliverBodyParts);
		gatherBodyParts.addStep(new Conditions(inGraveIsland, hasTorso, hasArm, hasDecapitatedHeadWithBrain), getLeg);
		gatherBodyParts.addStep(new Conditions(inGraveIsland, hasTorso, hasDecapitatedHeadWithBrain), getArm);
		gatherBodyParts.addStep(new Conditions(inGraveIsland, hasDecapitatedHeadWithBrain), getTorso);
		gatherBodyParts.addStep(new Conditions(inExperiementCave, hasDecapitatedHeadWithBrain, hasCavernKey), leaveExperimentCave);
		gatherBodyParts.addStep(new Conditions(inExperiementCave, hasDecapitatedHeadWithBrain, keyNearby), pickupKey);
		gatherBodyParts.addStep(new Conditions(inExperiementCave, hasDecapitatedHeadWithBrain), killExperiment);

		gatherBodyParts.addStep(new Conditions(hasDecapitatedHeadWithBrain, putStarOnGrave), enterExperimentCave);
		gatherBodyParts.addStep(new Conditions(hasDecapitatedHeadWithBrain, hasStarAmulet), useStarOnGrave);
		gatherBodyParts.addStep(new Conditions(decapitatedHead, pickledBrain), combinedHead);
		gatherBodyParts.addStep(new Conditions(decapitatedHead), getPickledBrain);
		gatherBodyParts.addStep(followingGardenerForHead, goToHeadGrave);
		gatherBodyParts.addStep(new Conditions(inCastleFloor1, hasStarAmulet), goDownstairsForStar);
		gatherBodyParts.addStep(new Conditions(hasStarAmulet), talkToGardenerForHead);

		gatherBodyParts.addStep(new Conditions(hasObsidianAmulet, hasMarbleAmulet), combineAmulet);
		gatherBodyParts.addStep(new Conditions(inCastleFloor1, hasObsidianAmulet), getBook2);
		gatherBodyParts.addStep(new Conditions(inCastleFloor1), getBook1);

		ConditionalStep fixLightningRod = new ConditionalStep(this, talkToGardenerForKey);
		fixLightningRod.addStep(new Conditions(inCastleTower, lightningRod), repairConductor);
		fixLightningRod.addStep(new Conditions(lightningRod, inCastleFloor1), goUpTowerLadder);
		fixLightningRod.addStep(new Conditions(lightningRod), goUpWestStairs);
		fixLightningRod.addStep(new Conditions(conductorMould), makeLightningRod);
		fixLightningRod.addStep(new Conditions(inCastleFloor1, extendedBrush3), searchFirePlace);
		fixLightningRod.addStep(new Conditions(extendedBrush3), goUpWestStairs);
		fixLightningRod.addStep(new Conditions(LogicType.AND, brush, canes), extendBrush);
		fixLightningRod.addStep(new Conditions(brush), grabCanes);
		fixLightningRod.addStep(new Conditions(LogicType.OR, usedShedKey, shedKey), searchForBrush);

		ConditionalStep talkToFenkentrain = new ConditionalStep(this, goBackToFirstFloor);
		talkToFenkentrain.addStep(inCastleFloor0, talkToFenkenstrainAfterFixingRod);

		ConditionalStep goToMonster = new ConditionalStep(this, goToMonsterFloor1);
		goToMonster.addStep(inMonsterTower, talkToMonster);
		goToMonster.addStep(new Conditions(usedTowerKey, inCastleFloor1), goToMonsterFloor2);
		goToMonster.addStep(new Conditions(towerKey, inCastleFloor1), openLockedDoor);
		
		steps.put(1, gatherBodyParts);
		steps.put(2, gatherNeedleAndThread);
		steps.put(3, fixLightningRod);
		steps.put(4, talkToFenkentrain);
		steps.put(5, goToMonster);
		steps.put(6, pickPocketFenkenstrain);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		ItemRequirement telegrab = new ItemRequirements("Telegrab runes", new ItemRequirement("Law rune",
			ItemID.LAW_RUNE), new ItemRequirement("Air rune", ItemID.AIR_RUNE));
		ItemRequirement coins50 = new ItemRequirement("Coins", ItemCollections.COINS, 50);

		// TODO: Add magic req once rebased
		telegrabOrCoins = new ItemRequirements(LogicType.OR,
			"33 Magic and runes to cast telegrab, or 50 coins",
			coins50, telegrab);
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER);
		ghostSpeakAmulet = new ItemRequirement("Ghostspeak amulet", ItemCollections.GHOSTSPEAK);
		silverBar = new ItemRequirement("Silver bar", ItemID.SILVER_BAR);
		bronzeWire = new ItemRequirement("Bronze wires", ItemID.BRONZE_WIRE, 3);
		needle = new ItemRequirement("Needle", ItemID.NEEDLE);
		thread = new ItemRequirement("Threads", ItemID.THREAD, 5);
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		coins = new ItemRequirement("Coins at least", ItemCollections.COINS, 100);
		pickledBrain = new ItemRequirement("Pickled Brain", ItemID.PICKLED_BRAIN);
		obsidianAmulet = new ItemRequirement("Obsidian Amulet", ItemID.OBSIDIAN_AMULET);
		marbleAmulet = new ItemRequirement("Marble Amulet", ItemID.MARBLE_AMULET);
		starAmulet = new ItemRequirement("Star Amulet", ItemID.STAR_AMULET);
		decapitatedHead = new ItemRequirement("Decapitated Head", ItemID.DECAPITATED_HEAD);
		decapitatedHeadWithBrain = new ItemRequirement("Decapitated Head (with brain)", ItemID.DECAPITATED_HEAD_4198);
		armor = new ItemRequirement("Armour and weapons defeat a level 51 monster and run past level 72 monsters", -1, -1);
		armor.setDisplayItemId(BankSlotIcons.getCombatGear());
		cavernKey = new ItemRequirement("Cavern Key", ItemID.CAVERN_KEY);
		torso = new ItemRequirement("Torso", ItemID.TORSO);
		legs = new ItemRequirement("Legs", ItemID.LEGS);
		arms = new ItemRequirement("Arms", ItemID.ARMS);
		shedKey = new ItemRequirement("Shed key", ItemID.SHED_KEY);
		brush = new ItemRequirement("Brush", ItemID.GARDEN_BRUSH);
		brush.addAlternates(ItemID.EXTENDED_BRUSH, ItemID.EXTENDED_BRUSH_4192);
		canes = new ItemRequirement("Garden Cane", ItemID.GARDEN_CANE);
		extendedBrush3 = new ItemRequirement("Extended Brush", ItemID.EXTENDED_BRUSH_4193);
		conductorMould = new ItemRequirement("Conductor Mold", ItemID.CONDUCTOR_MOULD);
		lightningRod = new ItemRequirement("Lightning Rod", ItemID.CONDUCTOR);
		towerKey = new ItemRequirement("Tower Key", ItemID.TOWER_KEY);

		fenkenstrainTeleports = new ItemRequirement("Fenkenstrain's Castle Teleport", ItemID.FENKENSTRAINS_CASTLE_TELEPORT, 2);
		teleportToFurnace = new ItemRequirement("Teleport to any furnace such as glory for Edgeville teleport, Ectophial to Port Phasmatys or a Falador teleport",
			ItemCollections.AMULET_OF_GLORIES);
		teleportToFurnace.addAlternates(ItemID.ECTOPHIAL, ItemID.FALADOR_TELEPORT);
		staminaPotion = new ItemRequirement("Stamina potions", ItemCollections.STAMINA_POTIONS, -1);
	}

	public void setupZones()
	{
		barZone = new Zone(new WorldPoint(3488, 3477, 0), new WorldPoint(3504, 3471, 0));
		castleZoneFloor0 = new Zone(new WorldPoint(3526, 3574, 0), new WorldPoint(3566, 3531, 0));
		castleZoneFloor1 = new Zone(new WorldPoint(3526, 3574, 1), new WorldPoint(3566, 3531, 1));
		experimentCave = new Zone(new WorldPoint(3466, 9921, 0), new WorldPoint(3582, 9982, 0));
		graveIsland = new Zone(new WorldPoint(3484, 3585, 0), new WorldPoint(3517, 3561, 0));
		castleTower = new Zone(new WorldPoint(3544, 3543, 2), new WorldPoint(3552, 3536, 2));
		monsterTower = new Zone(new WorldPoint(3544, 3558, 2), new WorldPoint(3553, 3551, 2));
	}

	public void setupConditions()
	{
		inCanifisBar = new ZoneRequirement(barZone);
		inCastleFloor0 = new ZoneRequirement(castleZoneFloor0);
		inCastleFloor1 = new ZoneRequirement(castleZoneFloor1);
		putStarOnGrave = new VarbitRequirement(192, 1);
		hasMarbleAmulet = new Conditions(LogicType.OR, marbleAmulet, putStarOnGrave);
		hasObsidianAmulet = new Conditions(LogicType.OR, obsidianAmulet, putStarOnGrave);
		hasStarAmulet = new Conditions(LogicType.OR, starAmulet, putStarOnGrave);
		followingGardenerForHead = new VarbitRequirement(185, 1);

		hasDecapitatedHeadWithBrain = new Conditions(LogicType.OR,
			decapitatedHeadWithBrain,
			new VarbitRequirement(189, 1)
		);

		inExperiementCave = new ZoneRequirement(experimentCave);
		inGraveIsland = new ZoneRequirement(graveIsland);

		hasCavernKey = new Conditions(LogicType.OR,
			cavernKey,
			new VarbitRequirement(199, 1)
		);
		keyNearby = new ItemOnTileRequirement(cavernKey);
		hasTorso = new Conditions(LogicType.OR,
			torso,
			new VarbitRequirement(188, 1)
		);
		hasLegs = new Conditions(LogicType.OR,
			legs,
			new VarbitRequirement(187, 1)
		);
		hasArm = new Conditions(LogicType.OR,
			arms,
			new VarbitRequirement(186, 1)
		);

		// Needle given, 190 = 1
		// Thread given, 191 0->5

		usedShedKey = new VarbitRequirement(200, 1);
		inCastleTower = new ZoneRequirement(castleTower);

		usedTowerKey = new VarbitRequirement(198, 1);
		inMonsterTower = new ZoneRequirement(monsterTower);
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
			"Talk to the Gardener Ghost.", ghostSpeakAmulet.equipped());
		talkToGardenerForHead.addDialogStep("What happened to your head?");

		goToHeadGrave = new DigStep(this, new WorldPoint(3608, 3490, 0),
			"Go to the grave of the gardener and dig for his head.");

		combinedHead = new ItemStep(this, "Use the decapitated head on the pickled brain to create a decapitated head.",
			decapitatedHead.highlighted(), pickledBrain.highlighted());

		useStarOnGrave = new ObjectStep(this, ObjectID.MEMORIAL, new WorldPoint(3578, 3528, 0),
			"Use the Star Amulet on the memorial and push it to go in the caves.", starAmulet.highlighted());
		useStarOnGrave.addIcon(ItemID.STAR_AMULET);

		enterExperimentCave = new ObjectStep(this, ObjectID.MEMORIAL, new WorldPoint(3578, 3528, 0),
			"Push the memorial south east of the castle.");

		killExperiment = new NpcStep(this, NpcID.EXPERIMENT, new WorldPoint(3557, 9946, 0),
			"Kill the level 51 Experiment north-west of the ladder to get a key.", true);

		pickupKey = new ItemStep(this, "Pick up the key.", cavernKey);
		killExperiment.addSubSteps(pickupKey);

		leaveExperimentCave = new ObjectStep(this, ObjectID.LADDER_17387, new WorldPoint(3504, 9970, 0),
			"Leave the caves by going north-west, be sure to pick up the key from the level 51 experiment.");

		getTorso = new DigStep(this, new WorldPoint(3503, 3576, 0), "Use your spade on this tile to get torsos.");

		getArm = new DigStep(this, new WorldPoint(3504, 3576, 0), "Use your spade on this tile to get arms.");

		getLeg = new DigStep(this, new WorldPoint(3505, 3576, 0), "Use your spade on this tile to get legs.");

		deliverBodyParts = new NpcStep(this, NpcID.DR_FENKENSTRAIN, new WorldPoint(3551, 3548, 0),
			"Deliver the body parts to Dr.Fenkenstrain, use a teleport to Fenkenstrain's castle or run back through " +
				"the caves.");
		deliverBodyParts.addDialogStep("I have some body parts for you.");

		gatherNeedleAndThread = new NpcStep(this, NpcID.DR_FENKENSTRAIN, new WorldPoint(3551, 3548, 0),
			"Get a needle and 5 threads and deliver them to Dr.Fenkenstrain.", needle, thread);

		talkToGardenerForKey = new NpcStep(this, NpcID.GARDENER_GHOST, new WorldPoint(3548, 3562, 0),
			"Talk to the Gardener Ghost and ask for the shed key.", ghostSpeakAmulet.equipped(), bronzeWire, silverBar);
		talkToGardenerForKey.addDialogStep("Do you know where the key to the shed is?");

		searchForBrush = new ObjectStep(this, ObjectID.CUPBOARD_5156, new WorldPoint(3546, 3564, 0),
			"Open the cupboard and search it for a brush.", bronzeWire, silverBar);
		((ObjectStep) searchForBrush).addAlternateObjects(ObjectID.OPEN_CUPBOARD_5157);
		grabCanes = new ObjectStep(this, ObjectID.PILE_OF_CANES, new WorldPoint(3551, 3564, 0),
			"Grab 3 canes from the pile.", bronzeWire, silverBar);
		extendBrush = new DetailedQuestStep(this, "Use 3 canes on the brush one at a time.", canes.highlighted(),
			brush.highlighted(),
			bronzeWire, silverBar);

		goUpWestStairs = new ObjectStep(this, ObjectID.STAIRCASE_5206, new WorldPoint(3538, 3552, 0),
			"Go up to the second floor of the castle.");
		searchFirePlace = new ObjectStep(this, ObjectID.FIREPLACE_5165, new WorldPoint(3544, 3555, 1),
			"Use the extended brush on the fireplace to get the conductor mould.", extendedBrush3.highlighted());
		searchFirePlace.addIcon(ItemID.EXTENDED_BRUSH_4193);
		makeLightningRod = new DetailedQuestStep(this, "Go to any furnace make a lightning rod.", silverBar, conductorMould);
		goUpWestStairsWithRod = new ObjectStep(this, ObjectID.STAIRCASE_5206, new WorldPoint(3537, 3553, 0),
			"Return to the castle and go upstairs.");
		goUpTowerLadder = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(3548, 3539, 1),
			"Go up to the third floor using the ladder in the middle of the castle.");
		repairConductor = new ObjectStep(this, ObjectID.LIGHTNING_CONDUCTOR, new WorldPoint(3549, 3537, 2),
			"Repair the lightning Conductor.");

		goBackToFirstFloor = new DetailedQuestStep(this, "Go back to the first floor of the castle and talk to Dr" +
			".Fenkenstrain.");
		talkToFenkenstrainAfterFixingRod = new NpcStep(this, NpcID.DR_FENKENSTRAIN, new WorldPoint(3551, 3548, 0),
			"Go back to the first floor of the castle and talk to Dr.Fenkenstrain.");

		goToMonsterFloor1 = new ObjectStep(this, ObjectID.STAIRCASE_5206, new WorldPoint(3538, 3552, 0),
			"Go up to the second floor to confront Fenkenstrain's monster.");
		openLockedDoor = new ObjectStep(this, ObjectID.DOOR_5172, new WorldPoint(3548, 3551, 1),
			"Use the Tower Key on the door.");
		goToMonsterFloor2 = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(3548, 3554, 1),
			"Go up the ladder");
		talkToMonster = new NpcStep(this, NpcID.FENKENSTRAINS_MONSTER, new WorldPoint(3548, 3555, 2),
			"Talk to Fenkenstrain's monster.");

		pickPocketFenkenstrain = new NpcStep(this, NpcID.DR_FENKENSTRAIN, new WorldPoint(3551, 3548, 0),
			"Go back to Dr.Fenkenstrain, instead of talking to him right click and pickpocket him.");
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
		reqs.add(teleportToFurnace);
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
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.THIEVING, 1000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("Ring of Charos", ItemID.RING_OF_CHAROS, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to the Werewolf Agility Course"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(getPickledBrain, talkToFrenkenstrain),
			telegrabOrCoins));
		allSteps.add(new PanelDetails("Graverobbing", Arrays.asList(goUpstairsForStar,
			getBook1, getBook2, combineAmulet, goDownstairsForStar, talkToGardenerForHead, goToHeadGrave, combinedHead,
			useStarOnGrave, enterExperimentCave, killExperiment, leaveExperimentCave, getTorso, getArm, getLeg,
			deliverBodyParts), ghostSpeakAmulet, spade, armor, needle, thread));
		allSteps.add(new PanelDetails("Getting tools",
			Collections.singletonList(gatherNeedleAndThread), ghostSpeakAmulet, needle, thread));
		allSteps.add(new PanelDetails("Attracting lightning",
			Arrays.asList(talkToGardenerForKey, searchForBrush, grabCanes, extendBrush, goUpWestStairs,
				searchFirePlace, makeLightningRod, goUpWestStairsWithRod, goUpTowerLadder,
			repairConductor, goBackToFirstFloor, talkToFenkenstrainAfterFixingRod), ghostSpeakAmulet, bronzeWire,
			silverBar, hammer));
		allSteps.add(new PanelDetails("Facing the monster", Arrays.asList(goToMonsterFloor1,
			openLockedDoor, goToMonsterFloor2, talkToMonster)));
		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(pickPocketFenkenstrain)));
		return allSteps;
	}
}
