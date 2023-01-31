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
package com.questhelper.quests.anothersliceofham;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.npc.FollowerRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.NpcInteractingRequirement;
import com.questhelper.requirements.npc.NpcInteractingWithNpcRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
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
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.ANOTHER_SLICE_OF_HAM
)
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

	DetailedQuestStep goToCrate, waitAtCrate, lureHamMember, enterFinalFight, useSpecial, defeatSigmund, untieZanik;

	ConditionalStep goTalkToUrtag, goTalkToTegdak, goGetArtefacts, goTalkToScribe, goTalkToOldak;

	//Zones
	Zone basement, tunnels, mines, cityF0, cityF1, railway, tower, goblinVillage, swamp, base, finalRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
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
	public void setupRequirements()
	{
		lightSource = new ItemRequirement("A light source", ItemCollections.LIGHT_SOURCES).isNotConsumed();

		lumbridgeTeleports = new ItemRequirement("Lumbridge teleports", ItemID.LUMBRIDGE_TELEPORT, 3);

		zanikFollower = new FollowerRequirement("Zanik following you. If she's not, retrieve her from the " +
			"Dorgesh-Kaan railway", NpcID.ZANIK_5147);

		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).isNotConsumed();

		combatGearRangedMagic = new ItemRequirement("Magic or ranged combat gear", -1, -1).isNotConsumed();
		combatGearRangedMagic.setDisplayItemId(BankSlotIcons.getMagicCombatGear());

		trowel = new ItemRequirement("Trowel", ItemID.TROWEL).isNotConsumed();
		trowel.setTooltip("You can get another from Tegdak");
		specimenBrush = new ItemRequirement("Specimen brush", ItemID.SPECIMEN_BRUSH).isNotConsumed();
		specimenBrush.setTooltip("You can get another from Tegdak");

		artefact1 = new ItemRequirement("Artefact", ItemID.ARTEFACT);
		artefact2 = new ItemRequirement("Artefact", ItemID.ARTEFACT_11051);
		artefact3 = new ItemRequirement("Artefact", ItemID.ARTEFACT_11053);
		artefact4 = new ItemRequirement("Artefact", ItemID.ARTEFACT_11055);
		artefact5 = new ItemRequirement("Artefact", ItemID.ARTEFACT_11057);
		artefact6 = new ItemRequirement("Artefact", ItemID.ARTEFACT_11059);
		armourShard = new ItemRequirement("Armour shard", ItemID.ARMOUR_SHARD);
		axeHead = new ItemRequirement("Axe head", ItemID.AXE_HEAD);
		helmetFragment = new ItemRequirement("Helmet fragment", ItemID.HELMET_FRAGMENT);
		shieldFragment = new ItemRequirement("Shield fragment", ItemID.SHIELD_FRAGMENT);
		swordFragment = new ItemRequirement("Sword fragment", ItemID.SWORD_FRAGMENT);
		mace = new ItemRequirement("Mace", ItemID.MACE);

		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		ancientMace = new ItemRequirement("Ancient mace", ItemID.ANCIENT_MACE);
		ancientMace.setTooltip("You can get this back from the Goblin Village Generals");
	}


	public void loadZones()
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

		dug1 = new VarbitRequirement(3551, 1, Operation.GREATER_EQUAL);
		dug2 = new VarbitRequirement(3552, 1, Operation.GREATER_EQUAL);
		dug3 = new VarbitRequirement(3553, 1, Operation.GREATER_EQUAL);
		dug4 = new VarbitRequirement(3554, 1, Operation.GREATER_EQUAL);
		dug5 = new VarbitRequirement(3555, 1, Operation.GREATER_EQUAL);
		dug6 = new VarbitRequirement(3556, 1, Operation.GREATER_EQUAL);

		handedIn1 = new VarbitRequirement(3551, 2);
		handedIn2 = new VarbitRequirement(3552, 2);
		handedIn3 = new VarbitRequirement(3553, 2);
		handedIn4 = new VarbitRequirement(3554, 2);
		handedIn5 = new VarbitRequirement(3555, 2);
		handedIn6 = new VarbitRequirement(3556, 2);

		cleaned1 = new Conditions(LogicType.OR, handedIn1, armourShard);
		cleaned2 = new Conditions(LogicType.OR, handedIn2, shieldFragment);
		cleaned3 = new Conditions(LogicType.OR, handedIn3, helmetFragment);
		cleaned4 = new Conditions(LogicType.OR, handedIn4, swordFragment);
		cleaned5 = new Conditions(LogicType.OR, handedIn5, axeHead);
		cleaned6 = new Conditions(LogicType.OR, handedIn6, mace);

		cleanedAll = new Conditions(cleaned1, cleaned2, cleaned3, cleaned4, cleaned5, cleaned6);

		zanikFollowing = new Conditions(LogicType.OR, new VarbitRequirement(3557, 0),
			new NpcInteractingRequirement(NpcID.ZANIK_5147));

		// 3564 = 1, searjents etc

		atCrate = new VarbitRequirement(3558, 1);
		guardsPassed = new NpcCondition(NpcID.GUARD_5141, new WorldPoint(2397, 5551, 0));
		guardEngaged = new Conditions(LogicType.OR,
			new NpcInteractingWithNpcRequirement(NpcID.SERGEANT_MOSSFISTS, "Guard"),
			new NpcInteractingWithNpcRequirement(NpcID.SERGEANT_SLIMETOES, "Guard")
		);

		weakSigmundNearby = new NpcCondition(NpcID.SIGMUND_5146);
	}

	public void setupSteps()
	{
		goDownIntoBasement = new ObjectStep(this, ObjectID.TRAPDOOR_14880, new WorldPoint(3209, 3216, 0), "Enter the Lumbridge Castle basement.");
		climbThroughHole = new ObjectStep(this, NullObjectID.NULL_6898, new WorldPoint(3219, 9618, 0), "");
		talkToKazgar = new NpcStep(this, NpcID.KAZGAR, new WorldPoint(3230, 9610, 0), "Travel with Kazgar to shortcut to Mistag.");
		talkToMistagToTravel = new NpcStep(this, NpcID.MISTAG_7298, new WorldPoint(3319, 9615, 0), "Travel with Mistag back to Lumbridge.");
		enterCity = new ObjectStep(this, ObjectID.DOOR_6919, new WorldPoint(3317, 9601, 0),
			"Enter Dorgesh-Kaan.");
		climbToF1City = new ObjectStep(this, ObjectID.STAIRS_22939, new WorldPoint(2721, 5360, 0),
			"Climb up to the next floor.");
		talkToUrtag = new NpcStep(this, NpcID.URTAG, new WorldPoint(2733, 5366, 1),
			"Talk to Ur-tag in the north east building.");
		talkToUrtag.setWorldMapPoint(new WorldPoint(2797, 5429, 1));
		enterRailway = new ObjectStep(this, ObjectID.DOORWAY_23052, new WorldPoint(2695, 5277, 1),
			"Enter the railway entrance in the south west of Dorgesh-Kaan.");
		enterRailway.setWorldMapPoint(new WorldPoint(2760, 5341, 1));

		talkToTegdak = new NpcStep(this, NpcID.TEGDAK, new WorldPoint(2512, 5564, 0), "");

		dig1 = new ObjectStep(this, NullObjectID.NULL_23290, new WorldPoint(2513, 5563, 0),
			"", trowel.highlighted());
		dig1.addIcon(ItemID.TROWEL);
		dig2 = new ObjectStep(this, NullObjectID.NULL_23293, new WorldPoint(2511, 5561, 0),
			"", trowel.highlighted());
		dig2.addIcon(ItemID.TROWEL);
		dig3 = new ObjectStep(this, NullObjectID.NULL_23296, new WorldPoint(2513, 5550, 0),
			"", trowel.highlighted());
		dig3.addIcon(ItemID.TROWEL);
		dig4 = new ObjectStep(this, NullObjectID.NULL_23534, new WorldPoint(2511, 5547, 0),
			"", trowel.highlighted());
		dig4.addIcon(ItemID.TROWEL);
		dig5 = new ObjectStep(this, NullObjectID.NULL_23301, new WorldPoint(2512, 5544, 0),
			"", trowel.highlighted());
		dig5.addIcon(ItemID.TROWEL);
		dig6 = new ObjectStep(this, NullObjectID.NULL_23304, new WorldPoint(2513, 5539, 0),
			"", trowel.highlighted());
		dig6.addIcon(ItemID.TROWEL);

		cleanArtefacts = new ObjectStep(this, ObjectID.SPECIMEN_TABLE, new WorldPoint(2513, 5559, 0),
			"Clean the artefacts on the specimen table.", artefact1.highlighted().hideConditioned(cleaned1),
			artefact2.highlighted().hideConditioned(cleaned2), artefact3.highlighted().hideConditioned(cleaned3),
			artefact4.highlighted().hideConditioned(cleaned4), artefact5.highlighted().hideConditioned(cleaned5),
			artefact6.highlighted().hideConditioned(cleaned6));
		cleanArtefacts.addIcon(ItemID.ARTEFACT);
		showTegdakArtefacts = new NpcStep(this, NpcID.TEGDAK, new WorldPoint(2512, 5564, 0), "Show Tegdak the " +
			"artefacts.", armourShard.hideConditioned(handedIn1), shieldFragment.hideConditioned(handedIn2),
			helmetFragment.hideConditioned(handedIn3), swordFragment.hideConditioned(handedIn4),
			axeHead.hideConditioned(handedIn5), mace.hideConditioned(handedIn6));

		talkToZanikRailway = new NpcStep(this, NpcID.ZANIK_5184, new WorldPoint(2512, 5564, 0),
			"Talk to Zanik.");

		leaveRailway = new ObjectStep(this, ObjectID.DOORWAY_23285, new WorldPoint(2521, 5607, 0),
			"Leave the railway.");
		talkToScribe = new NpcStep(this, NpcID.GOBLIN_SCRIBE, new WorldPoint(2716, 5369, 1),
			"");
		talkToScribe.setWorldMapPoint(new WorldPoint(2778, 5432, 1));
		goDownToF0City = new ObjectStep(this, ObjectID.STAIRS_22940, new WorldPoint(2721, 5360, 1),
			"Go downstairs.");
		goDownToF0City.setWorldMapPoint(new WorldPoint(2784, 5425, 1));
		talkToOldak = new NpcStep(this, NpcID.OLDAK, new WorldPoint(2704, 5365, 0), "");

		talkToGenerals = new NpcStep(this, NpcID.GENERAL_WARTFACE, new WorldPoint(2957, 3512, 0),
			"Talk to General Wartface and General Bentnoze in Goblin Village.", zanikFollower, combatGearRangedMagic);

		goToGoblinVillage = new DetailedQuestStep(this, new WorldPoint(2955, 3501, 0), "Go to Goblin Village, ready " +
			"to fight.", combatGearRangedMagic);

		goUpLadder = new ObjectStep(this, ObjectID.LADDER_23531, new WorldPoint(2442, 5417, 0),
			"Make your way to the tower to the south, using the buildings as cover, and climb up the ladder.");
		goUpLadder.setLinePoints(Arrays.asList(new WorldPoint(2438, 5429, 0), new WorldPoint(2438, 5423, 0),
			new WorldPoint(2436, 5423, 0), new WorldPoint(2436, 5419, 0), new WorldPoint(2440, 5417, 0)));
		killHamMageAndArcher = new NpcStep(this, NpcID.HAM_MAGE, new WorldPoint(2447, 5417, 2), "Kill the H.A.M. Mage" +
			" and H.A.M. Archer with a ranged/magic weapon.", true, combatGearRangedMagic);
		((NpcStep) killHamMageAndArcher).addAlternateNpcs(NpcID.HAM_ARCHER);

		talkToGeneralsAgain = new NpcStep(this, NpcID.GENERAL_WARTFACE, new WorldPoint(2957, 3512, 0),
			"Talk to General Wartface and General Bentnoze in Goblin Village again.");

		talkToSergeant = new NpcStep(this, NpcID.SERGEANT_SLIMETOES, new WorldPoint(3170, 3170, 0),
			"Talk to the Sergeants in Lumbridge Swamp.", combatGear, ancientMace, lightSource);

		enterSwamp = new ObjectStep(this, ObjectID.DARK_HOLE, new WorldPoint(3169, 3172, 0),
			"Enter the Lumbridge Swamp Caves.", combatGear, ancientMace, lightSource);

		climbEnterHamBase = new ObjectStep(this, NullObjectID.NULL_23282, new WorldPoint(3171, 9568, 0),
			"Climb down the ladder to the secret H.A.M. base.");

		goToCrate = new ObjectStep(this, ObjectID.CRATE_23283, new WorldPoint(2408, 5538, 0), "Hide behind the marked" +
			" crate, and " +
			"wait for the guards to walk past and around the corner.");
		waitAtCrate = new DetailedQuestStep(this, "Wait for the guards to go around the corner.");
		goToCrate.addSubSteps(waitAtCrate);
		lureHamMember = new DetailedQuestStep(this, new WorldPoint(2412, 5537, 0), "Run out infront of the final " +
			"guard, and wait for one of the sergeants to attack them.");
		enterFinalFight = new ObjectStep(this, ObjectID.LADDER_23376, new WorldPoint(2413, 5526, 0), "Climb down the " +
			"ladder.");

		useSpecial = new NpcStep(this, NpcID.SIGMUND_5142, new WorldPoint(2543, 5511, 0), "When Sigmund starts " +
			"using protection prayers, use the ancient mace's special attack on Sigmund to remove them and then " +
			"defeat him.",	ancientMace.equipped().highlighted());
		((NpcStep) useSpecial).addAlternateNpcs(NpcID.SIGMUND_5143, NpcID.SIGMUND_5144, NpcID.SIGMUND_5145);

		defeatSigmund = new NpcStep(this, NpcID.SIGMUND_5146, new WorldPoint(2543, 5511, 0),
			"Defeat Sigmund.");
		useSpecial.addSubSteps(defeatSigmund);

		untieZanik = new ObjectStep(this, ObjectID.ZANIK_23284, new WorldPoint(2542, 5513, 0), "Untie Zanik.");
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
		return Collections.singletonList(new ItemReward("An Ancient Mace", ItemID.ANCIENT_MACE, 1));
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
