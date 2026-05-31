/*
 * Copyright (c) 2026, pajlada <rasmus.karlsson@pajlada.com>
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
package com.questhelper.helpers.quests.theredreef;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.NoFollowerRequirement;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.PolyZone;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.questhelper.steps.WorldEntityStep;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

public class TheRedReef extends BasicQuestHelper
{
	// Required items
	ItemRequirement rangedCombatGearOrShipWithCannons;
	ItemRequirement combatGear;
	ItemRequirement combatGearBethel;
	ItemRequirement combatGearLobster;

	// Recommended items
	ItemRequirement food;
	ItemRequirement prayerPotions;
	ItemRequirement repairKits;

	// Mid-quest item requirements
	ItemRequirement divingHelmet;
	ItemRequirement divingHelmetEquipped;
	ItemRequirement divingApparatus;
	ItemRequirement divingApparatusEquipped;

	// Zones
	Zone redRock;
	Zone lastLightF0;

	// Miscellaneous requirements
	ZoneRequirement onRedRock;
	ZoneRequirement onLastLightF0;
	ZoneRequirement onLastLightF1;
	ZoneRequirement onLastLightF2;
	NpcRequirement onZenith;
	VarbitRequirement diving;
	NpcRequirement giantLobsterAround;
	VarbitRequirement onBoat;
	FreeInventorySlotRequirement freeInv2;
	NoFollowerRequirement noFollower;

	// Steps
	// 0 + 2
	NpcStep talkToRaleyToStartQuest;

	// 4
	NpcStep talkToFinn;

	// 6
	NpcStep talkToElderKatt;

	// 8
	NpcStep talkToFloopa;

	// 10
	DetailedQuestStep sailToRedRock;

	// 12
	NpcStep talkToRedRockReceptionist;

	// 14
	ConditionalStep cInspectCases;

	// 16
	NpcStep talkToTheodorePaxton;

	// 18
	NpcStep sinkBlackEyeBethelBoats;

	// 20
	ObjectStep disembarkAtLastLight;

	// 22
	NpcStep killPiratesAtLastLight;

	// 24
	ObjectStep climbUpToF1;
	ObjectStep climbUpToF2;
	NpcStep killBlackEyeBethel;

	// 26
	ObjectStep climbDownToF1;
	ObjectStep climbDownToF0;
	ObjectStep sailToRedRock2;
	ObjectStep boardYourShip;
	NpcStep talkToTheodorePaxtonAgain;
	ConditionalStep cReturnToTheodorePaxton;

	// 28
	WorldEntityStep sailToZenith;

	// 30
	NpcStep talkToSpencerBrentwood;
	DetailedQuestStep equipDivingGearAndDive;
	NpcStep dive;
	DetailedQuestStep listenToSpencer;

	// 32
	ObjectStep repairCoralDredger;
	NpcStep fightTheGiantLobster;

	// 34
	ObjectStep repairCoralDredger2;

	// 36
	NpcStep talkToSpencerNearEastCoralDredger;

	// 38
	NpcStep talkToSpencerAboutFuturePlans;

	// 40
	ConditionalStep cReturnToFloopa;

	@Override
	protected void setupZones()
	{
		redRock = new PolyZone(List.of(
			new WorldPoint(2809, 2508, 0),
			new WorldPoint(2804, 2511, 0)
		));

		lastLightF0 = new Zone(
			new WorldPoint(2867, 2319, 0),
			new WorldPoint(2849, 2335, 0)
		);
	}

	@Override
	protected void setupRequirements()
	{
		rangedCombatGearOrShipWithCannons = new ItemRequirement("Ranged/Mage combat gear to skip boat combat, or a boat with cannons to deal with pirates", -1, -1).isNotConsumed();
		rangedCombatGearOrShipWithCannons.setDisplayItemId(BankSlotIcons.getRangedCombatGear());
		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getRangedCombatGear());
		combatGearBethel = new ItemRequirement("Combat gear to fight Black Eye Bethel", -1, -1).isNotConsumed();
		combatGearBethel.setDisplayItemId(BankSlotIcons.getRangedCombatGear());
		combatGearLobster = new ItemRequirement("Any combat gear to fight a giant lobster", -1, -1).isNotConsumed();
		combatGearLobster.setDisplayItemId(BankSlotIcons.getRangedCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS, -1);
		repairKits = new ItemRequirement("Boat repair kits", ItemCollections.BOAT_REPAIR_KITS, 5);

		divingHelmet = new ItemRequirement("Deep sea helmet", ItemID.TRR_DIVING_HELMET);
		divingHelmetEquipped = divingHelmet.equipped();
		divingHelmetEquipped.setHighlightInInventory(true);
		divingApparatus = new ItemRequirement("Deep sea apparatus", ItemID.TRR_DIVING_BACKPACK);
		divingApparatusEquipped = divingApparatus.equipped();
		divingApparatusEquipped.setHighlightInInventory(true);

		var rr1 = new Zone(
			new WorldPoint(2805, 2500, 0),
			new WorldPoint(2790, 2527, 0)
		);
		var rr2 = new Zone(
			new WorldPoint(2808, 2521, 0),
			new WorldPoint(2806, 2522, 0)
		);
		var rr3 = new Zone(
			new WorldPoint(2806, 2510, 0),
			new WorldPoint(2808, 2509, 0)
		);
		var rr4 = new Zone(
			new WorldPoint(2789, 2515, 0),
			new WorldPoint(2785, 2529, 0)
		);
		onRedRock = new ZoneRequirement(rr1, rr2, rr3, rr4);

		onLastLightF0 = new ZoneRequirement(lastLightF0);
		onLastLightF1 = new ZoneRequirement(new Zone(11300, 1));
		onLastLightF2 = new ZoneRequirement(new Zone(11300, 2));

		onZenith = new NpcRequirement(NpcID.TRR_SPENCER_BRENTWOOD_VIS);

		diving = new VarbitRequirement(VarbitID.PLAYER_DIVING, 1);
		giantLobsterAround = new NpcRequirement(NpcID.TRR_GIANT_LOBSTER);
		onBoat = new VarbitRequirement(VarbitID.SAILING_BOARDED_BOAT, 1);
		freeInv2 = new FreeInventorySlotRequirement(2);
		noFollower = new NoFollowerRequirement("No pet following you");
	}

	void setupSteps()
	{
		talkToRaleyToStartQuest = new NpcStep(this, NpcID.TT_RALEY_CONCH, new WorldPoint(3186, 2405, 0), "Talk to Elder Raley at his home in The Great Conch to start the quest.");
		talkToRaleyToStartQuest.addDialogStep("Yes.");

		talkToFinn = new NpcStep(this, NpcID.TORTUGAN_FINN, new WorldPoint(3197, 2401, 0), "Talk to Finn, south-east of Elder Raley's house, about Floopa's whereabouts.");

		talkToElderKatt = new NpcStep(this, NpcID.TORTUGAN_GROVE_GUARDIAN, new WorldPoint(3196, 2466, 0), "Head to The Sacred Grove and talk to Elder Katt.");

		talkToFloopa = new NpcStep(this, NpcID.TRR_FLOOPA_VIS, new WorldPoint(3194, 2490, 0), "Enter The Sacred Grove and talk to Floopa.");

		sailToRedRock = new ObjectStep(this, ObjectID.SAILING_GANGPLANK_DISEMBARK, new WorldPoint(2809, 2509, 0), "Sail to Red Rock, north-west of The Great Conch.");

		talkToRedRockReceptionist = new NpcStep(this, NpcID.TRR_RED_ROCK_RECEPTIONIST, new WorldPoint(2792, 2522, 0), "Talk to the receptionist on Red Rock.");

		var needToInspectCase1 = new VarbitRequirement(VarbitID.TRR_DISPLAY_CASE_1, 0);
		var inspectCase1 = new ObjectStep(this, ObjectID.TRR_DISPLAY_CASE_1, new WorldPoint(2796, 2522, 0), "");

		var needToInspectCase2 = new VarbitRequirement(VarbitID.TRR_DISPLAY_CASE_2, 0);
		var inspectCase2 = new ObjectStep(this, ObjectID.TRR_DISPLAY_CASE_2, new WorldPoint(2794, 2524, 0), "");

		var needToInspectCase3 = new VarbitRequirement(VarbitID.TRR_DISPLAY_CASE_3, 0);
		var inspectCase3 = new ObjectStep(this, ObjectID.TRR_DISPLAY_CASE_3, new WorldPoint(2789, 2523, 0), "");

		var inspectCase4 = new ObjectStep(this, ObjectID.TRR_DISPLAY_CASE_4, new WorldPoint(2789, 2519, 0), "");

		cInspectCases = new ConditionalStep(this, inspectCase4, "Familiarize yourself with the history of the Red Reef Trading Company by inspecting the display cases around the building.");
		cInspectCases.addStep(needToInspectCase1, inspectCase1);
		cInspectCases.addStep(needToInspectCase2, inspectCase2);
		cInspectCases.addStep(needToInspectCase3, inspectCase3);

		talkToTheodorePaxton = new NpcStep(this, NpcID.TRR_THEODORE_PAXTON, new WorldPoint(2795, 2517, 0), "Talk to Theodore Paxton.");

		// TODO: add all pirate npc ids
		sinkBlackEyeBethelBoats = new NpcStep(this, new int[]{NpcID.TRR_PIRATE_1, NpcID.TRR_PIRATE_2, NpcID.TRR_PIRATE_3, NpcID.TRR_PIRATE_4}, new WorldPoint(2834, 2357, 0),
			"Get ready for boat combat, then sail south towards Last Light and deal with Black Eye Bethel. Protect from Missiles to avoid most damage. Repair your ship. " +
				"Combat is over when all boats are sunk or all pirates are killed.", rangedCombatGearOrShipWithCannons, combatGearBethel);
		sinkBlackEyeBethelBoats.setRecommended(List.of(food, prayerPotions, repairKits));

		disembarkAtLastLight = new ObjectStep(this, ObjectID.SAILING_MOORING_DISEMBARK, new WorldPoint(2849, 2327, 0), "Disembark at Last Light.");

		killPiratesAtLastLight = new NpcStep(this, new int[]{NpcID.TRR_PIRATE_1, NpcID.TRR_PIRATE_2, NpcID.TRR_PIRATE_3, NpcID.TRR_PIRATE_4}, new WorldPoint(2855, 2325, 0), "Kill all six pirates at Last Light.", true);

		var blackEyeBethelText = "Kill Black Eye Bethel. Use Protect from Melee to avoid some damage. Avoid her charge attack. Re-enable your prayers if she disables them.";
		climbUpToF1 = new ObjectStep(this, ObjectID.LAST_LIGHT_SPIRALSTAIRS_BASE, new WorldPoint(2863, 2326, 0), blackEyeBethelText, combatGearBethel);
		climbUpToF2 = new ObjectStep(this, ObjectID.LAST_LIGHT_SPIRALSTAIRS_MIDDLE, new WorldPoint(2863, 2326, 1), blackEyeBethelText, combatGearBethel);
		climbUpToF2.addDialogStep("Climb up.");
		killBlackEyeBethel = new NpcStep(this, NpcID.TRR_PIRATE_CAPTAIN, new WorldPoint(2862, 2323, 2), blackEyeBethelText, combatGearBethel);
		killBlackEyeBethel.addSubSteps(climbUpToF1, climbUpToF2);

		sailToRedRock2 = new ObjectStep(this, ObjectID.SAILING_GANGPLANK_DISEMBARK, new WorldPoint(2809, 2509, 0), "Sail to Red Rock, north of Last Light.");
		climbDownToF1 = new ObjectStep(this, ObjectID.LAST_LIGHT_SPIRALSTAIRS_TOP, new WorldPoint(2863, 2326, 2), "Climb down the tower.");
		climbDownToF0 = new ObjectStep(this, ObjectID.LAST_LIGHT_SPIRALSTAIRS_MIDDLE, new WorldPoint(2862, 2326, 1), "Climb down the tower.");
		climbDownToF0.addDialogStep("Climb down.");
		boardYourShip = new ObjectStep(this, ObjectID.SAILING_MOORING_EMBARK, new WorldPoint(2849, 2327, 0), "Board your boat.");
		talkToTheodorePaxtonAgain = new NpcStep(this, NpcID.TRR_THEODORE_PAXTON, new WorldPoint(2795, 2517, 0), "Talk to Theodore Paxton.");
		cReturnToTheodorePaxton = new ConditionalStep(this, sailToRedRock2, "Return to Theodore Paxton on Red Rock with the good news.");
		cReturnToTheodorePaxton.addStep(onRedRock, talkToTheodorePaxtonAgain);
		cReturnToTheodorePaxton.addStep(onLastLightF2, climbDownToF1);
		cReturnToTheodorePaxton.addStep(onLastLightF1, climbDownToF0);
		cReturnToTheodorePaxton.addStep(onLastLightF0, boardYourShip);

		sailToZenith = new WorldEntityStep(this, 9, new WorldPoint(2874, 2506, 0),
			"Sail to and board the Zenith, east of Red Rock. If you don't see the Zenith there, try hopping to a different world.", combatGearLobster);
		talkToSpencerBrentwood = new NpcStep(this, NpcID.TRR_SPENCER_BRENTWOOD_VIS, new WorldPoint(2878, 2505, 0), "Talk to Spencer Brentwood aboard the Zenith.", combatGearLobster);
		equipDivingGearAndDive = new DetailedQuestStep(this, "Equip the deep sea diving gear and dive down to the bottom of the sea.", combatGearLobster, divingHelmetEquipped, divingApparatusEquipped);
		dive = new NpcStep(this, NpcID.TRR_SPENCER_BRENTWOOD_VIS, "Talk to Spencer Brentwood again with the deep sea diving gear equipped to dive down to the bottom of the sea.", combatGearLobster, divingHelmetEquipped, divingApparatusEquipped, noFollower);
		dive.addDialogStep("Let's go.");
		dive.addSubSteps(equipDivingGearAndDive);
		listenToSpencer = new NpcStep(this, NpcID.TRR_SPENCER_BRENTWOOD_DIVING_A, new WorldPoint(2836, 8922, 1), "Listen to Spencer Brentwood's instructions.");

		repairCoralDredger = new ObjectStep(this, ObjectID.TRR_CORAL_DREDGER_BROKEN_OP, new WorldPoint(2844, 8942, 1), "Head north and repair the Coral dredger, ready to fight a Giant lobster.", combatGearLobster);
		fightTheGiantLobster = new NpcStep(this, NpcID.TRR_GIANT_LOBSTER, "Kill the Giant lobster. Avoid the blue particles on the floor.");

		repairCoralDredger2 = new ObjectStep(this, ObjectID.TRR_CORAL_DREDGER_BROKEN_OP, new WorldPoint(2844, 8942, 1), "Repair the Coral dredger.");

		talkToSpencerNearEastCoralDredger = new NpcStep(this, NpcID.TRR_SPENCER_BRENTWOOD_DIVING_VIS, new WorldPoint(2857, 8915, 1), "Talk to Spencer Brentwood near the eastern coral dredger.");

		talkToSpencerAboutFuturePlans = new NpcStep(this, NpcID.TRR_SPENCER_BRENTWOOD_VIS, new WorldPoint(2878, 2505, 0), "Talk to Spencer Brentwood aboard the Zenith after repairing the coral dredgers.");

		var talkToSpencerBrentwoodAgain = new NpcStep(this, NpcID.TRR_SPENCER_BRENTWOOD_VIS, new WorldPoint(2878, 2505, 0), "Return to Spencer Brentwood to disembark onto your boat.");
		talkToSpencerBrentwoodAgain.addDialogStep("Actually, I'd like to disembark.");

		var sailToGreatConch = new ObjectStep(this, ObjectID.SAILING_GANGPLANK_DISEMBARK, new WorldPoint(3174, 2367, 0), "Sail to The Great Conch.");
		var talkToFloopaAtElderRaleysHouse = new NpcStep(this, NpcID.TT_FLOOPA_VIS, new WorldPoint(3185, 2405, 0), "");

		cReturnToFloopa = new ConditionalStep(this, talkToFloopaAtElderRaleysHouse, "Return to Floopa at Elder Raley's house at The Great Conch.");
		cReturnToFloopa.addStep(onZenith, talkToSpencerBrentwoodAgain);
		cReturnToFloopa.addStep(onBoat, sailToGreatConch);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToRaleyToStartQuest);
		steps.put(2, talkToRaleyToStartQuest);

		steps.put(4, talkToFinn);

		steps.put(6, talkToElderKatt);

		steps.put(8, talkToFloopa);

		steps.put(10, sailToRedRock);

		var cTalkToReceptionist = new ConditionalStep(this, sailToRedRock);
		cTalkToReceptionist.addStep(onRedRock, talkToRedRockReceptionist);
		steps.put(12, cTalkToReceptionist);

		var cInspectCases = new ConditionalStep(this, sailToRedRock);
		cInspectCases.addStep(onRedRock, this.cInspectCases);
		steps.put(14, cInspectCases);

		var cTalkToMrPaxton = new ConditionalStep(this, sailToRedRock);
		cTalkToMrPaxton.addStep(onRedRock, talkToTheodorePaxton);
		steps.put(16, cTalkToMrPaxton);

		steps.put(18, sinkBlackEyeBethelBoats);

		steps.put(20, disembarkAtLastLight);

		var cKillPiratesAtLastLight = new ConditionalStep(this, disembarkAtLastLight);
		cKillPiratesAtLastLight.addStep(onLastLightF0, killPiratesAtLastLight);
		steps.put(22, cKillPiratesAtLastLight);

		var cKillBlackEyeBethel = new ConditionalStep(this, disembarkAtLastLight);
		cKillBlackEyeBethel.addStep(onLastLightF0, climbUpToF1);
		cKillBlackEyeBethel.addStep(onLastLightF1, climbUpToF2);
		cKillBlackEyeBethel.addStep(onLastLightF2, killBlackEyeBethel);
		steps.put(24, cKillBlackEyeBethel);

		steps.put(26, cReturnToTheodorePaxton);

		var cInitialDive = new ConditionalStep(this, sailToZenith);
		cInitialDive.addStep(diving, listenToSpencer);
		cInitialDive.addStep(and(onZenith, divingApparatusEquipped, divingHelmetEquipped), dive);
		cInitialDive.addStep(and(onZenith, divingApparatus, divingHelmet), equipDivingGearAndDive);
		cInitialDive.addStep(onZenith, talkToSpencerBrentwood);
		steps.put(28, cInitialDive);
		steps.put(30, cInitialDive);

		var cFightLobster = new ConditionalStep(this, sailToZenith);
		cFightLobster.addStep(and(diving, giantLobsterAround), fightTheGiantLobster);
		cFightLobster.addStep(diving, repairCoralDredger);
		cFightLobster.addStep(and(onZenith, divingApparatusEquipped, divingHelmetEquipped), dive);
		cFightLobster.addStep(and(onZenith, divingApparatus, divingHelmet), equipDivingGearAndDive);
		cFightLobster.addStep(onZenith, talkToSpencerBrentwood);

		steps.put(32, cFightLobster);

		var cRepairNorthernCoralDredger = new ConditionalStep(this, sailToZenith);
		cRepairNorthernCoralDredger.addStep(diving, repairCoralDredger2);
		cRepairNorthernCoralDredger.addStep(and(onZenith, divingApparatusEquipped, divingHelmetEquipped), dive);
		cRepairNorthernCoralDredger.addStep(and(onZenith, divingApparatus, divingHelmet), equipDivingGearAndDive);
		cRepairNorthernCoralDredger.addStep(onZenith, talkToSpencerBrentwood);
		steps.put(34, cRepairNorthernCoralDredger);

		var cRepairEasternCoralDredgerWithSpencer = new ConditionalStep(this, sailToZenith);
		cRepairEasternCoralDredgerWithSpencer.addStep(diving, talkToSpencerNearEastCoralDredger);
		cRepairEasternCoralDredgerWithSpencer.addStep(and(onZenith, divingApparatusEquipped, divingHelmetEquipped), dive);
		cRepairEasternCoralDredgerWithSpencer.addStep(and(onZenith, divingApparatus, divingHelmet), equipDivingGearAndDive);
		cRepairEasternCoralDredgerWithSpencer.addStep(onZenith, talkToSpencerBrentwood);

		steps.put(36, cRepairEasternCoralDredgerWithSpencer);

		var cTalkToSpencerAboutFuturePlans = new ConditionalStep(this, sailToZenith);
		cTalkToSpencerAboutFuturePlans.addStep(onZenith, talkToSpencerAboutFuturePlans);

		steps.put(38, cTalkToSpencerAboutFuturePlans);

		steps.put(40, cReturnToFloopa);

		return steps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.TROUBLED_TORTUGANS, QuestState.FINISHED),
			new SkillRequirement(Skill.SAILING, 52, false),
			new SkillRequirement(Skill.SMITHING, 48, false)
		);
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		return List.of(
			new SkillRequirement(Skill.PRAYER, 40, false, "43 prayer for Protect from Missiles & Melee"),
			new CombatLevelRequirement(65)
		);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			rangedCombatGearOrShipWithCannons,
			combatGear
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			food,
			prayerPotions,
			repairKits
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"2 pirate ships, or the crew on the ship with ranged combat gear",
			"6 pirates (lvl 52)",
			"Black Eye Bethel (lvl 191)",
			"Giant lobster (lvl 112)"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.SAILING, 15000),
			new ExperienceReward(Skill.SMITHING, 5000)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Bosun's Workbench Schematic", /*ItemID.LOST_SCHEMATIC_BOSUNS_WORKBENCH*/ -1) // NOTE: The gameval for this item is not in the latest RuneLite release
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to the Great Conch sacred grove")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Finding Floopa", List.of(
			talkToRaleyToStartQuest,
			talkToFinn,
			talkToElderKatt,
			talkToFloopa
		)));

		sections.add(new PanelDetails("Infiltrating the Red Reef", List.of(
			sailToRedRock,
			talkToRedRockReceptionist,
			cInspectCases,
			talkToTheodorePaxton
		)));

		sections.add(new PanelDetails("Dealing with Black Eye Bethel", List.of(
			sinkBlackEyeBethelBoats,
			disembarkAtLastLight,
			killPiratesAtLastLight,
			killBlackEyeBethel,
			cReturnToTheodorePaxton
		), List.of(
			rangedCombatGearOrShipWithCannons,
			combatGearBethel
		), List.of(
			food,
			prayerPotions,
			repairKits
		)));

		sections.add(new PanelDetails("Helping the Zenith", List.of(
			sailToZenith,
			talkToSpencerBrentwood,
			dive,
			listenToSpencer,
			repairCoralDredger,
			fightTheGiantLobster,
			repairCoralDredger2,
			talkToSpencerNearEastCoralDredger,
			talkToSpencerAboutFuturePlans
		), List.of(
			combatGearLobster,
			freeInv2
		), List.of(
			food
		)));

		sections.add(new PanelDetails("Return to Floopa", List.of(
			cReturnToFloopa
		)));

		return sections;
	}
}
