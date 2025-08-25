/*
 * Copyright (c) 2021, Zoinkwiz
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
package com.questhelper.helpers.quests.anothersliceofham;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.FollowerRequirement;
import com.questhelper.requirements.npc.NpcInteractingRequirement;
import com.questhelper.requirements.npc.NpcInteractingWithNpcRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import java.util.*;

public class AnotherSliceOfHam extends BasicQuestHelper
{
	//Items Required
	ItemRequirement lightSource, tinderbox, combatGearRangedMagic, combatGear;

	ItemRequirement lumbridgeTeleports;

	ItemRequirement trowel, specimenBrush, artefact1, artefact2, artefact3, artefact4, artefact5, artefact6,
		armourShard, axeHead, helmetFragment, shieldFragment, swordFragment, mace, ancientMace;

	FollowerRequirement zanikFollower;

	Requirement inBasement, inTunnels, inMines, inCityF0, inCityF1, inRailway, inTower, inGoblinVillage, inSwamp,
		inBase, atCrate, inFinalRoom;

	Requirement dug1, dug2, dug3, dug4, dug5, dug6, cleaned1, cleaned2, cleaned3, cleaned4, cleaned5, cleaned6,
		cleanedAll, handedIn1, handedIn2, handedIn3, handedIn4, handedIn5, handedIn6, zanikFollowing, guardsPassed,
		guardEngaged, weakSigmundNearby;

	DetailedQuestStep talkToMistagToTravel;

	DetailedQuestStep goDownIntoBasement, climbThroughHole, talkToKazgar, enterCity, climbToF1City, talkToUrtag,
		enterRailway, talkToTegdak;

	DetailedQuestStep dig1, dig2, dig3, dig4, dig5, dig6, cleanArtefacts, showTegdakArtefacts;

	DetailedQuestStep talkToZanikRailway, leaveRailway, talkToScribe, goDownToF0City, talkToOldak;

	DetailedQuestStep goToGoblinVillage, talkToGenerals, goUpLadder, killHamMageAndArcher, talkToGeneralsAgain;

	DetailedQuestStep talkToSergeant, enterSwamp, climbEnterHamBase;

	QuestStep goToCrate, waitAtCrate, lureHamMember, enterFinalFight, useSpecial, defeatSigmund, untieZanik;

	ConditionalStep goTalkToUrtag, goTalkToTegdak, goGetArtefacts, goTalkToScribe, goTalkToOldak;

	//Zones
	Zone basement, tunnels, mines, cityF0, cityF1, railway, tower, goblinVillage, swamp, base, finalRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		setupConditionalSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, goTalkToUrtag);
		steps.put(1, goTalkToTegdak);
		steps.put(2, goGetArtefacts);
		steps.put(3, goTalkToScribe);
		steps.put(4, goTalkToOldak);
		steps.put(5, talkToGenerals);

		ConditionalStep goKillHamRangers = new ConditionalStep(this, goToGoblinVillage);
		goKillHamRangers.addStep(inTower, killHamMageAndArcher);
		goKillHamRangers.addStep(inGoblinVillage, goUpLadder);
		steps.put(6, goKillHamRangers);

		steps.put(7, talkToGeneralsAgain);
		steps.put(8, talkToSergeant);

		ConditionalStep goInfiltrateBase = new ConditionalStep(this, enterSwamp);
		goInfiltrateBase.addStep(weakSigmundNearby, defeatSigmund);
		goInfiltrateBase.addStep(inFinalRoom, useSpecial);
		goInfiltrateBase.addStep(new Conditions(inBase, guardEngaged), enterFinalFight);
		goInfiltrateBase.addStep(new Conditions(inBase, guardsPassed), lureHamMember);
		goInfiltrateBase.addStep(new Conditions(inBase, atCrate), waitAtCrate);
		goInfiltrateBase.addStep(inBase, goToCrate);
		goInfiltrateBase.addStep(inSwamp, climbEnterHamBase);
		steps.put(9, goInfiltrateBase);

		ConditionalStep goFinish = new ConditionalStep(this, enterSwamp);
		goFinish.addStep(inFinalRoom, untieZanik);
		goFinish.addStep(inBase, enterFinalFight);
		goFinish.addStep(inSwamp, climbEnterHamBase);
		steps.put(10, goFinish);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		lightSource = new ItemRequirement("A light source", ItemCollections.LIGHT_SOURCES).isNotConsumed();

		lumbridgeTeleports = new ItemRequirement("Lumbridge teleports", ItemID.POH_TABLET_LUMBRIDGETELEPORT, 3);

		zanikFollower = new FollowerRequirement("Zanik following you. If she's not, retrieve her from the " +
			"Dorgesh-Kaan railway", NpcID.SLICE_ZANIK_FOLLOWER);

		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).isNotConsumed();

		combatGearRangedMagic = new ItemRequirement("Magic or ranged combat gear", -1, -1).isNotConsumed();
		combatGearRangedMagic.setDisplayItemId(BankSlotIcons.getMagicCombatGear());

		trowel = new ItemRequirement("Trowel", ItemID.TROWEL).isNotConsumed();
		trowel.setTooltip("You can get another from Tegdak");
		specimenBrush = new ItemRequirement("Specimen brush", ItemID.SPECIMEN_BRUSH).isNotConsumed();
		specimenBrush.setTooltip("You can get another from Tegdak");

		artefact1 = new ItemRequirement("Artefact", ItemID.SLICE_ARTIFACT_1_DIRTY);
		artefact2 = new ItemRequirement("Artefact", ItemID.SLICE_ARTIFACT_5_DIRTY);
		artefact3 = new ItemRequirement("Artefact", ItemID.SLICE_ARTIFACT_3_DIRTY);
		artefact4 = new ItemRequirement("Artefact", ItemID.SLICE_ARTIFACT_2_DIRTY);
		artefact5 = new ItemRequirement("Artefact", ItemID.SLICE_ARTIFACT_4_DIRTY);
		artefact6 = new ItemRequirement("Artefact", ItemID.SLICE_ARTIFACT_6_DIRTY);
		armourShard = new ItemRequirement("Armour shard", ItemID.SLICE_ARTIFACT_1_CLEAN);
		axeHead = new ItemRequirement("Axe head", ItemID.SLICE_ARTIFACT_5_CLEAN);
		helmetFragment = new ItemRequirement("Helmet fragment", ItemID.SLICE_ARTIFACT_3_CLEAN);
		shieldFragment = new ItemRequirement("Shield fragment", ItemID.SLICE_ARTIFACT_2_CLEAN);
		swordFragment = new ItemRequirement("Sword fragment", ItemID.SLICE_ARTIFACT_4_CLEAN);
		mace = new ItemRequirement("Mace", ItemID.SLICE_ARTIFACT_6_CLEAN);

		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		ancientMace = new ItemRequirement("Ancient mace", ItemID.ANCIENT_GOBLIN_MACE);
		ancientMace.setTooltip("You can get this back from the Goblin Village Generals");
	}


	@Override
	protected void setupZones()
	{
		basement = new Zone(new WorldPoint(3208, 9614, 0), new WorldPoint(3219, 9625, 0));
		tunnels = new Zone(new WorldPoint(3221, 9602, 0), new WorldPoint(3308, 9661, 0));
		mines = new Zone(new WorldPoint(3309, 9600, 0), new WorldPoint(3327, 9655, 0));
		cityF0 = new Zone(new WorldPoint(2688, 5248, 0), new WorldPoint(2750, 5375, 0));
		cityF1 = new Zone(new WorldPoint(2688, 5248, 1), new WorldPoint(2750, 5375, 1));
		railway = new Zone(new WorldPoint(2523, 5630, 0), new WorldPoint(2505, 5527, 0));
		tower = new Zone(new WorldPoint(2440, 5416, 2), new WorldPoint(2447, 5418, 2));
		goblinVillage = new Zone(new WorldPoint(2434, 5409, 0), new WorldPoint(2459, 5438, 0));
		swamp = new Zone(new WorldPoint(3138, 9536, 0), new WorldPoint(3261, 9601, 0));
		base = new Zone(new WorldPoint(2393, 5525, 0), new WorldPoint(2414, 5560,0));
		finalRoom = new Zone(new WorldPoint(2532, 5509, 0), new WorldPoint(2553, 5520, 0));
	}

	public void setupConditions()
	{
		inBasement = new ZoneRequirement(basement);
		inTunnels = new ZoneRequirement(tunnels);
		inMines = new ZoneRequirement(mines);
		inCityF0 = new ZoneRequirement(cityF0);
		inCityF1 = new ZoneRequirement(cityF1);
		inRailway = new ZoneRequirement(railway);
		inTower = new ZoneRequirement(tower);
		inGoblinVillage = new ZoneRequirement(goblinVillage);
		inSwamp = new ZoneRequirement(swamp);
		inBase = new ZoneRequirement(base);
		inFinalRoom = new ZoneRequirement(finalRoom);

		// Started, 3550 = 1
		// 3557 = 1

		dug1 = new VarbitRequirement(VarbitID.SLICE_ARTIFACT_1, 1, Operation.GREATER_EQUAL);
		dug2 = new VarbitRequirement(VarbitID.SLICE_ARTIFACT_2, 1, Operation.GREATER_EQUAL);
		dug3 = new VarbitRequirement(VarbitID.SLICE_ARTIFACT_3, 1, Operation.GREATER_EQUAL);
		dug4 = new VarbitRequirement(VarbitID.SLICE_ARTIFACT_4, 1, Operation.GREATER_EQUAL);
		dug5 = new VarbitRequirement(VarbitID.SLICE_ARTIFACT_5, 1, Operation.GREATER_EQUAL);
		dug6 = new VarbitRequirement(VarbitID.SLICE_ARTIFACT_6, 1, Operation.GREATER_EQUAL);

		handedIn1 = new VarbitRequirement(VarbitID.SLICE_ARTIFACT_1, 2);
		handedIn2 = new VarbitRequirement(VarbitID.SLICE_ARTIFACT_2, 2);
		handedIn3 = new VarbitRequirement(VarbitID.SLICE_ARTIFACT_3, 2);
		handedIn4 = new VarbitRequirement(VarbitID.SLICE_ARTIFACT_4, 2);
		handedIn5 = new VarbitRequirement(VarbitID.SLICE_ARTIFACT_5, 2);
		handedIn6 = new VarbitRequirement(VarbitID.SLICE_ARTIFACT_6, 2);

		cleaned1 = new Conditions(LogicType.OR, handedIn1, armourShard);
		cleaned2 = new Conditions(LogicType.OR, handedIn2, shieldFragment);
		cleaned3 = new Conditions(LogicType.OR, handedIn3, helmetFragment);
		cleaned4 = new Conditions(LogicType.OR, handedIn4, swordFragment);
		cleaned5 = new Conditions(LogicType.OR, handedIn5, axeHead);
		cleaned6 = new Conditions(LogicType.OR, handedIn6, mace);

		cleanedAll = new Conditions(cleaned1, cleaned2, cleaned3, cleaned4, cleaned5, cleaned6);

		zanikFollowing = new Conditions(LogicType.OR, new VarbitRequirement(VarbitID.SLICE_ZANIK_AT_DIG, 0),
			new NpcInteractingRequirement(NpcID.SLICE_ZANIK_FOLLOWER));

		// 3564 = 1, searjents etc

		atCrate = new VarbitRequirement(VarbitID.SLICE_HIDING, 1);
		guardsPassed = new NpcCondition(NpcID.SLICE_HAM_GUARD, new WorldPoint(2397, 5551, 0));
		guardEngaged = new Conditions(LogicType.OR,
			new NpcInteractingWithNpcRequirement(NpcID.SLICE_SERGEANT_MOSSFISTS, "Guard"),
			new NpcInteractingWithNpcRequirement(NpcID.SLICE_SERGEANT_SLIMETOES, "Guard")
		);

		weakSigmundNearby = new NpcCondition(NpcID.SLICE_SIGMUND_NOPRAYER);
	}

	public void setupSteps()
	{
		goDownIntoBasement = new ObjectStep(this, ObjectID.QIP_COOK_TRAPDOOR_OPEN, new WorldPoint(3209, 3216, 0), "Enter the Lumbridge Castle basement.");
		climbThroughHole = new ObjectStep(this, ObjectID.LOST_TRIBE_CELLAR_WALL, new WorldPoint(3219, 9618, 0), "");
		talkToKazgar = new NpcStep(this, NpcID.LOST_TRIBE_GUIDE_2OPS, new WorldPoint(3230, 9610, 0), "Travel with Kazgar to shortcut to Mistag.");
		talkToMistagToTravel = new NpcStep(this, NpcID.LOST_TRIBE_MISTAG_2OPS, new WorldPoint(3319, 9615, 0), "Travel with Mistag back to Lumbridge.");
		enterCity = new ObjectStep(this, ObjectID.CAVE_GOBLIN_CITY_DOORR, new WorldPoint(3317, 9601, 0),
			"Enter Dorgesh-Kaan.");
		climbToF1City = new ObjectStep(this, ObjectID.DORGESH_1STAIRS_POSH, new WorldPoint(2721, 5360, 0),
			"Climb up to the next floor.");
		talkToUrtag = new NpcStep(this, NpcID.DORGESH_URTAQ, new WorldPoint(2733, 5366, 1),
			"Talk to Ur-tag in the north east building.");
		enterRailway = new ObjectStep(this, ObjectID.SLICE_GOBLIN_STATION_ENTRANCE, new WorldPoint(2695, 5277, 1),
			"Enter the railway entrance in the south west of Dorgesh-Kaan.");

		talkToTegdak = new NpcStep(this, NpcID.SLICE_GOBLIN_ARCHAEOLOGIST, new WorldPoint(2512, 5564, 0), "");

		dig1 = new ObjectStep(this, ObjectID.SLICE_ARTIFACT_HOTSPOT_01, new WorldPoint(2513, 5563, 0),
			"", trowel.highlighted());
		dig1.addIcon(ItemID.TROWEL);
		dig2 = new ObjectStep(this, ObjectID.SLICE_ARTIFACT_HOTSPOT_02, new WorldPoint(2511, 5561, 0),
			"", trowel.highlighted());
		dig2.addIcon(ItemID.TROWEL);
		dig3 = new ObjectStep(this, ObjectID.SLICE_ARTIFACT_HOTSPOT_03, new WorldPoint(2513, 5550, 0),
			"", trowel.highlighted());
		dig3.addIcon(ItemID.TROWEL);
		dig4 = new ObjectStep(this, ObjectID.SLICE_ARTIFACT_HOTSPOT_04, new WorldPoint(2511, 5547, 0),
			"", trowel.highlighted());
		dig4.addIcon(ItemID.TROWEL);
		dig5 = new ObjectStep(this, ObjectID.SLICE_ARTIFACT_HOTSPOT_05, new WorldPoint(2512, 5544, 0),
			"", trowel.highlighted());
		dig5.addIcon(ItemID.TROWEL);
		dig6 = new ObjectStep(this, ObjectID.SLICE_ARTIFACT_HOTSPOT_06, new WorldPoint(2513, 5539, 0),
			"", trowel.highlighted());
		dig6.addIcon(ItemID.TROWEL);

		cleanArtefacts = new ObjectStep(this, ObjectID.SLICE_TABLE_01, new WorldPoint(2513, 5559, 0),
			"Clean the artefacts on the specimen table.", artefact1.highlighted().hideConditioned(cleaned1),
			artefact2.highlighted().hideConditioned(cleaned2), artefact3.highlighted().hideConditioned(cleaned3),
			artefact4.highlighted().hideConditioned(cleaned4), artefact5.highlighted().hideConditioned(cleaned5),
			artefact6.highlighted().hideConditioned(cleaned6));
		cleanArtefacts.addIcon(ItemID.SLICE_ARTIFACT_1_DIRTY);
		showTegdakArtefacts = new NpcStep(this, NpcID.SLICE_GOBLIN_ARCHAEOLOGIST, new WorldPoint(2512, 5564, 0), "Show Tegdak the " +
			"artefacts.", armourShard.hideConditioned(handedIn1), shieldFragment.hideConditioned(handedIn2),
			helmetFragment.hideConditioned(handedIn3), swordFragment.hideConditioned(handedIn4),
			axeHead.hideConditioned(handedIn5), mace.hideConditioned(handedIn6));

		talkToZanikRailway = new NpcStep(this, NpcID.SLICE_ZANIK_MULTI_THERE, new WorldPoint(2512, 5564, 0),
			"Talk to Zanik.");

		leaveRailway = new ObjectStep(this, ObjectID.SLICE_UNDERGROUND_WALL_EXIT_GOBLIN, new WorldPoint(2521, 5607, 0),
			"Leave the railway.");
		talkToScribe = new NpcStep(this, NpcID.DORGESH_MALE_SCRIBE, new WorldPoint(2716, 5369, 1),
			"");
		goDownToF0City = new ObjectStep(this, ObjectID.DORGESH_1STAIRS_POSH_TOP, new WorldPoint(2721, 5360, 1),
			"Go downstairs.");
		talkToOldak = new NpcStep(this, NpcID.LOTG_OLDAK_CUTSCENE, new WorldPoint(2704, 5365, 0), "");

		talkToGenerals = new NpcStep(this, NpcID.GENERAL_WARTFACE_GREEN, new WorldPoint(2957, 3512, 0),
			"Talk to General Wartface and General Bentnoze in Goblin Village.", zanikFollower, combatGearRangedMagic);

		goToGoblinVillage = new DetailedQuestStep(this, new WorldPoint(2955, 3501, 0), "Go to Goblin Village, ready " +
			"to fight.", combatGearRangedMagic);

		goUpLadder = new ObjectStep(this, ObjectID.SLICE_GOBLIN_LADDER_BOTTOM, new WorldPoint(2442, 5417, 0),
			"Make your way to the tower to the south, using the buildings as cover, and climb up the ladder.");
		goUpLadder.setLinePoints(Arrays.asList(new WorldPoint(2438, 5429, 0), new WorldPoint(2438, 5423, 0),
			new WorldPoint(2436, 5423, 0), new WorldPoint(2436, 5419, 0), new WorldPoint(2440, 5417, 0)));
		killHamMageAndArcher = new NpcStep(this, NpcID.SLICE_HAM_MAGE, new WorldPoint(2447, 5417, 2), "Kill the H.A.M. Mage" +
			" and H.A.M. Archer with a ranged/magic weapon.", true, combatGearRangedMagic);
		((NpcStep) killHamMageAndArcher).addAlternateNpcs(NpcID.SLICE_HAM_ARCHER);

		talkToGeneralsAgain = new NpcStep(this, NpcID.GENERAL_WARTFACE_GREEN, new WorldPoint(2957, 3512, 0),
			"Talk to General Wartface and General Bentnoze in Goblin Village again.");

		talkToSergeant = new NpcStep(this, NpcID.SLICE_SERGEANT_SLIMETOES, new WorldPoint(3170, 3170, 0),
			"Talk to the Sergeants in Lumbridge Swamp.", combatGear, ancientMace, lightSource);

		enterSwamp = new ObjectStep(this, ObjectID.GOBLIN_CAVE_ENTRANCE, new WorldPoint(3169, 3172, 0),
			"Enter the Lumbridge Swamp Caves.", combatGear, ancientMace, lightSource);

		climbEnterHamBase = new ObjectStep(this, ObjectID.SLICE_LADDER_SWAMP, new WorldPoint(3171, 9568, 0),
			"Climb down the ladder to the secret H.A.M. base.");

		goToCrate = new PuzzleWrapperStep(this, new ObjectStep(this, ObjectID.SLICE_STEALTH_CRATE_STACKED, new WorldPoint(2408, 5538, 0), "Hide behind the marked" +
			" crate, and wait for the guards to walk past and around the corner."), "Work out how to avoid all the H.A.M guards.");
		waitAtCrate = new PuzzleWrapperStep(this, new DetailedQuestStep(this, "Wait for the guards to go around the corner."), "Work out how to avoid all the H.A.M guards.");
		goToCrate.addSubSteps(waitAtCrate);
		lureHamMember = new PuzzleWrapperStep(this, new DetailedQuestStep(this, new WorldPoint(2412, 5537, 0), "Run out in front of the final " +
			"guard, and wait for one of the sergeants to attack them."), "Work out how to avoid all the H.A.M guards.");
		enterFinalFight = new ObjectStep(this, ObjectID.SLICE_LADDERTOP, new WorldPoint(2413, 5526, 0), "Climb down the " +
			"ladder.");

		useSpecial = new NpcStep(this, NpcID.SLICE_SIGMUND_SHOWDOWN, new WorldPoint(2543, 5511, 0), "When Sigmund starts " +
			"using protection prayers, use the ancient mace's special attack on Sigmund to remove them and then " +
			"defeat him.",	ancientMace.equipped().highlighted());
		((NpcStep) useSpecial).addAlternateNpcs(NpcID.SLICE_SIGMUND_MELEE, NpcID.SLICE_SIGMUND_RANGED, NpcID.SLICE_SIGMUND_MAGIC);

		defeatSigmund = new NpcStep(this, NpcID.SLICE_SIGMUND_NOPRAYER, new WorldPoint(2543, 5511, 0),
			"Defeat Sigmund.");
		useSpecial.addSubSteps(defeatSigmund);

		untieZanik = new ObjectStep(this, ObjectID.SLICE_ZANIK_TIED_UP, new WorldPoint(2542, 5513, 0), "Untie Zanik.");
	}

	private void setupConditionalSteps()
	{
		ConditionalStep goToCityF0 = new ConditionalStep(this, goDownIntoBasement);
		goToCityF0.addStep(inMines, enterCity);
		goToCityF0.addStep(inTunnels, talkToKazgar);
		goToCityF0.addStep(inBasement, climbThroughHole);

		ConditionalStep goToCityF1 = new ConditionalStep(this, goToCityF0);
		goToCityF1.addStep(inCityF0, climbToF1City);

		goTalkToUrtag = new ConditionalStep(this, goToCityF1, "Travel to Dorgesh-Kaan under Lumbridge, and talk to " +
			"Ur-tag there.", lightSource);
		goTalkToUrtag.addStep(inCityF1, talkToUrtag);
		goTalkToUrtag.addDialogSteps("What are you arguing about?", "I'd love to help!");

		goTalkToTegdak = new ConditionalStep(this, goToCityF1, "Talk to Tegdak through the doorway in south-west " +
			"Dorgesh-Kaan.", lightSource);
		goTalkToTegdak.addStep(inRailway, talkToTegdak);
		goTalkToTegdak.addStep(inCityF1, enterRailway);

		goGetArtefacts = new ConditionalStep(this, goToCityF1, "Dig up the 6 artefacts in the railway tunnel, clean " +
			"them, and show them to Tegdak.");
		goGetArtefacts.addStep(new Conditions(inRailway, cleanedAll), showTegdakArtefacts);
		goGetArtefacts.addStep(new Conditions(inRailway, dug1, dug2, dug3, dug4, dug5, dug6), cleanArtefacts);
		goGetArtefacts.addStep(new Conditions(inRailway, dug1, dug2, dug3, dug4, dug5), dig6);
		goGetArtefacts.addStep(new Conditions(inRailway, dug1, dug2, dug3, dug4), dig5);
		goGetArtefacts.addStep(new Conditions(inRailway, dug1, dug2, dug3), dig4);
		goGetArtefacts.addStep(new Conditions(inRailway, dug1, dug2), dig3);
		goGetArtefacts.addStep(new Conditions(inRailway, dug1), dig2);
		goGetArtefacts.addStep(inRailway, dig1);
		goGetArtefacts.addStep(inCityF1, enterRailway);

		goTalkToScribe = new ConditionalStep(this, goToCityF1, "Talk to Zanik in the railway, and then go talk to the" +
			" Goblin Scribe in the north of Dorgesh-Kaan with her.");
		goTalkToScribe.addStep(new Conditions(inCityF1, zanikFollowing), talkToScribe);
		goTalkToScribe.addStep(new Conditions(inRailway, zanikFollowing), leaveRailway);
		goTalkToScribe.addStep(inRailway, talkToZanikRailway);
		goTalkToScribe.addStep(inCityF1, enterRailway);
		goTalkToScribe.addDialogStep("Yes");

		goTalkToOldak = new ConditionalStep(this, goToCityF0,
			"Talk to Oldak in the north west of Dorgesh-Kaan with Zanik.", zanikFollower);
		goTalkToOldak.addStep(new Conditions(inCityF0, zanikFollowing), talkToOldak);
		goTalkToOldak.addStep(new Conditions(inCityF1, zanikFollowing), goDownToF0City);
		goTalkToOldak.addStep(new Conditions(inRailway, zanikFollowing), leaveRailway);
		goTalkToOldak.addStep(inRailway, talkToZanikRailway);
		goTalkToOldak.addStep(inCityF1, enterRailway);
		goTalkToOldak.addDialogStep("Yes");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(lightSource, tinderbox, combatGearRangedMagic);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Collections.singletonList(lumbridgeTeleports);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("H.A.M. Archer (level 30)", "H.A.M. Mage (level 30)", "Sigmund (level 64)");
	}

	@Override
	public QuestPointReward	getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.MINING, 3000),
				new ExperienceReward(Skill.PRAYER, 3000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("An Ancient Mace", ItemID.ANCIENT_GOBLIN_MACE, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Dorgeshuun Train Access."),
				new UnlockReward("Ability to buy Goblin Village Teleport Spheres"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(goTalkToUrtag, goTalkToTegdak, goGetArtefacts,
			goTalkToScribe), lightSource));
		allSteps.add(new PanelDetails("To Goblin Village", Arrays.asList(goTalkToOldak, talkToGenerals, goUpLadder,
			killHamMageAndArcher, talkToGeneralsAgain), combatGearRangedMagic));
		allSteps.add(new PanelDetails("Saving Zanik", Arrays.asList(talkToSergeant, enterSwamp, climbEnterHamBase,
			goToCrate, lureHamMember, enterFinalFight, useSpecial, untieZanik), combatGear, ancientMace, lightSource));
		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.DEATH_TO_THE_DORGESHUUN, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.THE_GIANT_DWARF, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.THE_DIG_SITE, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.ATTACK, 15));
		req.add(new SkillRequirement(Skill.PRAYER, 25));
		return req;
	}
}
