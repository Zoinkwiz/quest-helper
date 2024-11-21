/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.perilousmoon;

import com.google.common.collect.Lists;
import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.coords.WorldPoint;

public class PerilousMoon extends BasicQuestHelper
{
	//Items Required
	ItemRequirement knife, bigFishingNet, rope, pestleAndMortar, saw, hammer, earthTalisman, waterTalisman,
		stabWeapon, slashWeapon, crushWeapon;

	ItemRequirement combatGear, staminaPotions, antipoison;

	ItemRequirement buildingSupplies, mossLizardTail, bream, breamScales, moonlightGrub, moonlightGrubPaste,
		enchantedWaterTalisman, enchantedEarthTalisman, infusedWaterTalisman, infusedEarthTalisman, rawMossLizard;

	Requirement emptyInventory5;

	QuestStep talkToAttala, killSulphurNagua, returnToAttala, enterCamTorum,
		talkToJessamine, enterNeypotzli;

	QuestStep talkToAttalaInNey, talkToZumaInNey, talkToJessamineInNey;

	QuestStep takeBuildingSupplies, enterPrison, buildPrisonCamp, leavePrison;

	QuestStep enterEarthbound, buildEarthCamp, leaveEarth;

	QuestStep enterStreambound, buildStreamCamp, leaveStream;

	QuestStep talkToJessamineForTalismans, leaveNeypotzli, talkToNahta, talkToBlacksmith,
		enterNeypotzliAfterBlacksmith, talkToAttalaAfterBlacksmith, useTalismans,
		useTalismansSidebar, talkToEyatlalli, talkToJessamineAfterEyatlalli;

	QuestStep enterStreamboundAgain, getHerbloreSupplies, collectGrub, useGrubOnPestle, getFishingSupplies, fishBream,
		useKnifeOnBream, getHunterSupplies, enterSmallEntrance, setTrap, rustleBush, pickUpMossLizard,
		useKnifeOnLizard, leaveEarthboundWithLizard, talkToEyatlalliWithItems, talkToEyatlalliAfterItems;

	QuestStep enterCamTorumForFights, enterNeypotzliForFights;
	QuestStep enterEarthboundForFight, enterBlueMoonFight, fightBlueMoon, fightBlueMoonSidebar,
		enterPrisonForFight, enterBloodMoonFight, fightBloodMoon, fightBloodMoonSidebar,
		enterStreamboundForFight, enterEclipseMoonFight, fightEclipseMoon, fightEclipseMoonSidebar,
		talkToJessamineEnd, talkToZumaEnd, finishQuest;

	//Zones
	Zone camTorum, antechamber, prison, streambound, earthbound, ancientShrine, blueMoonRoom, bloodMoonRoom, eclipseRoom;

	Requirement talkedToBuilders, inCamTorum, inAntechamber, talkedToAttalaInNey, talkedToZumaInNey, inPrison, inStreambound,
		inEarthbound, madePrisonCamp, madeStreamCamp, madeEarthCamp, inAncientShrine, inNeypotzli, eyatlalliNearby, trapSetup,
		lizardNearby, usedTail, usedPaste, usedScales, hadTail, hadPaste, hadScales, inBlueMoon, inEclipseMoon,
		inBloodMoon, defeatedBlueMoon, defeatedBloodMoon, defeatedEclipseMoon;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();
		initializeRequirements();
		setupConditions();
		setupSteps();

		steps.put(0, talkToAttala);

		steps.put(2, killSulphurNagua);

		steps.put(4, returnToAttala);

		ConditionalStep goTalkToJess = new ConditionalStep(this, enterCamTorum);
		goTalkToJess.addStep(and(inCamTorum), talkToJessamine);
		steps.put(5, goTalkToJess);
		// 5->6 when entered cam torum
		// 9826 0->1, probably builders going
		steps.put(6, goTalkToJess);

		ConditionalStep goEnterNeypotzli = new ConditionalStep(this, enterCamTorum);
		goEnterNeypotzli.addStep(inCamTorum, enterNeypotzli);

		steps.put(7, goEnterNeypotzli);

		ConditionalStep goTalkInNey = goEnterNeypotzli.copy();
		goTalkInNey.addStep(and(inAntechamber, talkedToZumaInNey), talkToJessamineInNey);
		goTalkInNey.addStep(and(inAntechamber, talkedToAttalaInNey), talkToZumaInNey);
		goTalkInNey.addStep(inAntechamber, talkToAttalaInNey);
		steps.put(8, goTalkInNey);

		ConditionalStep goBuildPrisonCamp = goEnterNeypotzli.copy();
		goBuildPrisonCamp.addStep(inPrison, buildPrisonCamp);
		goBuildPrisonCamp.addStep(inAntechamber, enterPrison);

		ConditionalStep goBuildEarthCamp = goEnterNeypotzli.copy();
		goBuildEarthCamp.addStep(inEarthbound, buildEarthCamp);
		goBuildEarthCamp.addStep(inPrison, leavePrison);
		goBuildEarthCamp.addStep(inAntechamber, enterEarthbound);

		ConditionalStep goBuildStreamCamp = goEnterNeypotzli.copy();
		goBuildStreamCamp.addStep(inStreambound, buildStreamCamp);
		goBuildStreamCamp.addStep(inEarthbound, leaveEarth);
		goBuildStreamCamp.addStep(inPrison, leavePrison);
		goBuildStreamCamp.addStep(inAntechamber, enterStreambound);

		ConditionalStep buildCamps = goEnterNeypotzli.copy();
		buildCamps.addStep(and(buildingSupplies, madePrisonCamp, madeEarthCamp), goBuildStreamCamp);
		buildCamps.addStep(and(buildingSupplies, madePrisonCamp), goBuildEarthCamp);
		buildCamps.addStep(buildingSupplies, goBuildPrisonCamp);
		buildCamps.addStep(inAntechamber, takeBuildingSupplies);
		steps.put(10, buildCamps);

		ConditionalStep goReturnToJessamine = goEnterNeypotzli.copy();
		goReturnToJessamine.addStep(inAntechamber, talkToJessamineForTalismans);
		goReturnToJessamine.addStep(inStreambound, leaveStream);
		goReturnToJessamine.addStep(inEarthbound, leaveEarth);
		goReturnToJessamine.addStep(inPrison, leavePrison);
		steps.put(12, goReturnToJessamine);
		steps.put(13, goReturnToJessamine);
		steps.put(14, goReturnToJessamine);

		ConditionalStep goTalkToNahta = new ConditionalStep(this, enterCamTorum);
		goTalkToNahta.addStep(inCamTorum, talkToNahta);
		goTalkToNahta.addStep(inAntechamber, leaveNeypotzli);
		steps.put(15, goTalkToNahta);

		ConditionalStep goTalkToBlacksmith = new ConditionalStep(this, enterCamTorum);
		goTalkToBlacksmith.addStep(inCamTorum, talkToBlacksmith);
		goTalkToBlacksmith.addStep(inAntechamber, leaveNeypotzli);
		steps.put(16, goTalkToBlacksmith);

		ConditionalStep goFindEyatlalli = new ConditionalStep(this, enterCamTorum);
		goFindEyatlalli.addStep(eyatlalliNearby, talkToEyatlalli);
		goFindEyatlalli.addStep(inNeypotzli, useTalismans);
		goFindEyatlalli.addStep(inCamTorum, enterNeypotzliAfterBlacksmith);
		steps.put(17, goFindEyatlalli);
		steps.put(18, goFindEyatlalli);

		ConditionalStep goTalkToJessamineAfterEyatlalli = new ConditionalStep(this, enterCamTorum);
		goTalkToJessamineAfterEyatlalli.addStep(inNeypotzli, talkToJessamineAfterEyatlalli);
		goTalkToJessamineAfterEyatlalli.addStep(inCamTorum, enterNeypotzliAfterBlacksmith);
		steps.put(19, goTalkToJessamineAfterEyatlalli);
		steps.put(20, goTalkToJessamineAfterEyatlalli);
		steps.put(21, goTalkToJessamineAfterEyatlalli);
		steps.put(22, goTalkToJessamineAfterEyatlalli);

		ConditionalStep getItemsWhenInStream = new ConditionalStep(this, getHerbloreSupplies);
		getItemsWhenInStream.addStep(and(hadPaste, hadScales, rope), enterSmallEntrance);
		getItemsWhenInStream.addStep(and(hadPaste, hadScales), getHunterSupplies);
		getItemsWhenInStream.addStep(and(hadPaste, bream), useKnifeOnBream);
		getItemsWhenInStream.addStep(and(hadPaste, bigFishingNet), fishBream);
		getItemsWhenInStream.addStep(and(hadPaste), getFishingSupplies);
		getItemsWhenInStream.addStep(and(pestleAndMortar, moonlightGrub), useGrubOnPestle);
		getItemsWhenInStream.addStep(and(pestleAndMortar), collectGrub);

		ConditionalStep goGetItems = new ConditionalStep(this, enterCamTorum);
		goGetItems.addStep(and(inEarthbound, hadPaste, hadScales, hadTail), leaveEarthboundWithLizard);
		goGetItems.addStep(and(inNeypotzli, hadPaste, hadScales, hadTail), talkToEyatlalliWithItems);
		goGetItems.addStep(and(inEarthbound, hadPaste, hadScales, rawMossLizard), useKnifeOnLizard);
		goGetItems.addStep(and(inEarthbound, hadPaste, hadScales, lizardNearby), pickUpMossLizard);
		goGetItems.addStep(and(inEarthbound, hadPaste, hadScales, trapSetup), rustleBush);
		goGetItems.addStep(and(inEarthbound, hadPaste, hadScales, rope), setTrap);
		goGetItems.addStep(inStreambound, getItemsWhenInStream);
		goGetItems.addStep(inNeypotzli, enterStreamboundAgain);
		goGetItems.addStep(inCamTorum, enterNeypotzliAfterBlacksmith);
		steps.put(23, goGetItems);
		steps.put(24, goGetItems);
		steps.put(25, goGetItems);
		steps.put(26, goGetItems);

		ConditionalStep goTalkToEyatlalliAfterItems = goEnterNeypotzli.copy();
		goTalkToEyatlalliAfterItems.addStep(inNeypotzli, talkToEyatlalliAfterItems);
		steps.put(27, goTalkToEyatlalliAfterItems);

		ConditionalStep goFightBlueMoon = new ConditionalStep(this, enterEarthboundForFight);
		goFightBlueMoon.addStep(inBlueMoon, fightBlueMoon);
		goFightBlueMoon.addStep(inEarthbound, enterBlueMoonFight);

		ConditionalStep goFightBloodMoon = new ConditionalStep(this, enterPrisonForFight);
		goFightBloodMoon.addStep(inBloodMoon, fightBloodMoon);
		goFightBloodMoon.addStep(inPrison, enterBloodMoonFight);

		ConditionalStep goFightEclipseMoon = new ConditionalStep(this, enterStreamboundForFight);
		goFightEclipseMoon.addStep(inEclipseMoon, fightEclipseMoon);
		goFightEclipseMoon.addStep(inStreambound, enterEclipseMoonFight);

		ConditionalStep goDefeatMoons = new ConditionalStep(this, enterCamTorumForFights);
		goDefeatMoons.addStep(and(inNeypotzli, defeatedBlueMoon, defeatedBloodMoon, defeatedEclipseMoon), talkToJessamineEnd);
		goDefeatMoons.addStep(and(inNeypotzli, defeatedBlueMoon, defeatedBloodMoon), goFightEclipseMoon);
		goDefeatMoons.addStep(and(inNeypotzli, defeatedBlueMoon), goFightBloodMoon);
		goDefeatMoons.addStep(inNeypotzli, goFightBlueMoon);
		goDefeatMoons.addStep(inCamTorum, enterNeypotzliForFights);

		steps.put(28, goDefeatMoons);
		steps.put(29, goDefeatMoons);

		ConditionalStep goFinishWithZuma = goEnterNeypotzli.copy();
		goFinishWithZuma.addStep(inNeypotzli, talkToZumaEnd);
		steps.put(30, goFinishWithZuma);

		ConditionalStep goFinishQuest = goEnterNeypotzli.copy();
		goFinishQuest.addStep(inNeypotzli, finishQuest);
		steps.put(31, goFinishQuest);

		return steps;
	}

	@Override
	protected void setupZones()
	{
		camTorum = new Zone(new WorldPoint(1378, 9502, 1), new WorldPoint(1524, 9600, 3));
		antechamber = new Zone(5782, 1);
		prison = new Zone(5525, 0);
		earthbound = new Zone(5527, 0);
		streambound = new Zone(6039, 0);
		ancientShrine = new Zone(6037, 0);

		blueMoonRoom = new Zone(new WorldPoint(1417, 9647, 0), new WorldPoint(1463, 9699, 0));
		bloodMoonRoom = new Zone(new WorldPoint(1373, 9614, 0), new WorldPoint(1421, 9650, 0));
		eclipseRoom = new Zone(new WorldPoint(1457, 9614, 0), new WorldPoint(1506, 9650, 0));
	}

	@Override
	protected void setupRequirements()
	{
		knife = new ItemRequirement("Knife", ItemID.KNIFE).isNotConsumed();
		knife.setTooltip("Can be obtained during the quest from the Supply crates (fishing supplies).");
		bigFishingNet = new ItemRequirement("Big fishing net", ItemID.BIG_FISHING_NET).isNotConsumed();
		bigFishingNet.setTooltip("Can be obtained during the quest from the Supply crates (fishing supplies).");
		rope = new ItemRequirement("Rope", ItemID.ROPE);
		rope.setTooltip("Can be obtained during the quest from the Supply crates (hunting supplies).");
		pestleAndMortar = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR).isNotConsumed();
		pestleAndMortar.setTooltip("Can be obtained during the quest from the Supply crates (herblore supplies).");
		saw = new ItemRequirement("Saw", ItemID.SAW);
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER);
		combatGear = new ItemRequirement("Combat armour with high defensive bonuses", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		stabWeapon = new ItemRequirement("Stab weapon", -1, -1).isNotConsumed();
		stabWeapon.setDisplayItemId(ItemID.DRAGON_SPEAR);
		slashWeapon = new ItemRequirement("Slash weapon", -1, -1).isNotConsumed();
		slashWeapon.setDisplayItemId(ItemID.DRAGON_SCIMITAR);
		crushWeapon = new ItemRequirement("Crush weapon", -1, -1).isNotConsumed();
		crushWeapon.setDisplayItemId(ItemID.DRAGON_MACE);

		earthTalisman = new ItemRequirement("Earth talisman", ItemID.EARTH_TALISMAN);
		earthTalisman.setTooltip("If you lost the one given to you you'll need to find another way to get one");
		waterTalisman = new ItemRequirement("Water talisman", ItemID.WATER_TALISMAN);
		waterTalisman.setTooltip("If you lost the one given to you you'll need to find another way to get one");

		// Recommended
		staminaPotions = new ItemRequirement("Stamina potions", ItemCollections.STAMINA_POTIONS);
		antipoison = new ItemRequirement("Antipoison", ItemCollections.ANTIPOISONS);

		emptyInventory5 = new FreeInventorySlotRequirement(5);

		// Quest
		buildingSupplies = new ItemRequirement("Building supplies", ItemID.BUILDING_SUPPLIES);
		rawMossLizard = new ItemRequirement("Raw moss lizard", ItemID.RAW_MOSS_LIZARD);
		mossLizardTail = new ItemRequirement("Moss lizard tail", ItemID.MOSS_LIZARD_TAIL);
		bream = new ItemRequirement("Raw bream", ItemID.RAW_BREAM);
		breamScales = new ItemRequirement("Bream scales", ItemID.BREAM_SCALES);
		moonlightGrub = new ItemRequirement("Moonlight grub", ItemID.MOONLIGHT_GRUB);
		moonlightGrubPaste = new ItemRequirement("Moonlight grub paste", ItemID.MOONLIGHT_GRUB_PASTE);
		enchantedWaterTalisman = new ItemRequirement("Enchanted water talisman", ItemID.ENCHANTED_WATER_TALISMAN);
		enchantedWaterTalisman.setTooltip("You can get another from Nahta.");
		enchantedEarthTalisman = new ItemRequirement("Enchanted earth talisman", ItemID.ENCHANTED_EARTH_TALISMAN);
		enchantedEarthTalisman.setTooltip("You can get another from Nahta.");
		infusedWaterTalisman = new ItemRequirement("Infused water talisman", ItemID.INFUSED_WATER_TALISMAN);
		infusedWaterTalisman.setTooltip("You can get another from the Blacksmith in Cam Torum");
		infusedEarthTalisman = new ItemRequirement("Infused earth talisman", ItemID.INFUSED_EARTH_TALISMAN);
		infusedEarthTalisman.setTooltip("You can get another from the Blacksmith in Cam Torum");
	}

	private void setupConditions()
	{
		talkedToBuilders = new VarbitRequirement(9824, 1);
		inCamTorum = new ZoneRequirement(camTorum);
		inAntechamber = new ZoneRequirement(antechamber);
		talkedToAttalaInNey = new VarbitRequirement(9823, 1);
		talkedToZumaInNey = new VarbitRequirement(9823, 2);
		// 9826 0->2
		// 9827 0->1 (Interrupted by guard to not risk stuff), increments each time, reached 5

		inPrison = new ZoneRequirement(prison);
		inStreambound = new ZoneRequirement(streambound);
		inEarthbound = new ZoneRequirement(earthbound);
		inAncientShrine = new ZoneRequirement(ancientShrine);
		inBlueMoon = new ZoneRequirement(blueMoonRoom);
		inBloodMoon = new ZoneRequirement(bloodMoonRoom);
		inEclipseMoon = new ZoneRequirement(eclipseRoom);
		inNeypotzli = new ZoneRequirement(antechamber, prison, streambound, earthbound, ancientShrine, blueMoonRoom,
			bloodMoonRoom, eclipseRoom);

		// 15064 0->100
		madePrisonCamp = new VarbitRequirement(9820, 1);
		madeEarthCamp = new VarbitRequirement(9821, 1);
		madeStreamCamp = new VarbitRequirement(9822, 1);

		// Found eyat
		// Location: 1525, 9580, 0. 3x3 region for summoning. Directions work for exact row/column
		// 9819 17->18
		// 9825 0->1
		eyatlalliNearby = new NpcRequirement(NpcID.EYATLALLI);
		// icosahedron
		trapSetup = or(new VarbitRequirement(9871, 1), new VarbitRequirement(9872, 1),
			new VarbitRequirement(9873, 1));
		lizardNearby = new ItemOnTileRequirement(ItemID.RAW_MOSS_LIZARD);
		// 9871/2/3 for traps, 0->1

		usedScales = new VarbitRequirement(9819, 24, Operation.GREATER_EQUAL);
		hadScales = or(breamScales, usedScales);
		usedTail = new VarbitRequirement(9819, 25, Operation.GREATER_EQUAL);
		hadTail = or(mossLizardTail, usedTail);
		usedPaste = new VarbitRequirement(9819, 26, Operation.GREATER_EQUAL);
		hadPaste = or(moonlightGrubPaste, usedPaste);

		// 9848 0->1
		// 9846 9->0
		// 9853 0->...->17->0
		// 9855 0->1->0

		// Ded, PROBS ONE OF THESE
		// 9859 0->1 - I think this is the tick (for usual killing)
		// varp 4149 0->1
		// 9862 0->1


		// Varp 4150 0->1 blood moon ded
		defeatedBloodMoon = new VarbitRequirement(9858, 1);
		defeatedBlueMoon = new VarbitRequirement(9859, 1);
		defeatedEclipseMoon = new VarbitRequirement(9860, 1);
	}

	private void setupSteps()
	{
		talkToAttala = new NpcStep(this, NpcID.ATTALA, new WorldPoint(1435, 3124, 0),
			"Talk to Attala, stood south of Ralos' Rise.");
		talkToAttala.addDialogStep("Yes.");
		killSulphurNagua = new NpcStep(this, NpcID.SULPHUR_NAGUA, new WorldPoint(1453, 3129, 0),
			"Kill the sulphur nagua just atop the cliff north east of Attala." +
				" You can climb the rocks just to the east to reach it quickly with 47 Agility.");
		returnToAttala = new NpcStep(this, NpcID.ATTALA, new WorldPoint(1435, 3124, 0),
		"Return to Attala.");
		enterCamTorum = new ObjectStep(this, ObjectID.ENTRANCE_51375, new WorldPoint(1436, 3129, 0),
			"Enter the entrance to Cam Torum.");
		talkToJessamine = new NpcStep(this, NpcID.JESSAMINE_12865, new WorldPoint(1441, 9596, 1),
			"Talk to Jessamine in the far north end of Cam Torum.");
		talkToJessamine.addDialogStep("Can I be of any help with the dig?");
		enterNeypotzli = new ObjectStep(this, ObjectID.ENTRANCE_51375, new WorldPoint(1439, 9600, 1),
			"Enter the Neypotzli entrance in the far north of the cavern.");
		// Entered, varp 4066 8174-> 40942
		talkToAttalaInNey = new NpcStep(this, NpcID.ATTALA, new WorldPoint(1433, 9632, 1),
			"Talk to Attala near the entrance.");
		talkToZumaInNey = new NpcStep(this, NpcID.ZUMA_12860, new WorldPoint(1447, 9632, 1),
		"Talk to Zuma near the entrance.");
		talkToJessamineInNey = new NpcStep(this, NpcID.JESSAMINE_12865, new WorldPoint(1440, 9639, 1),
			"Talk to Jessamine near the entrance.");

		takeBuildingSupplies = new ObjectStep(this, ObjectID.BUILDING_SUPPLIES_50857, new WorldPoint(1436, 9637, 1),
			"Take some building supplies from the crates near Jessamine.", emptyInventory5);

		enterPrison = new ObjectStep(this, ObjectID.ENTRANCE_51376, new WorldPoint(1421, 9613, 1),
			"Enter the south-west entrance.", buildingSupplies, hammer, saw);
		// Path to prison camp
		List<WorldPoint> prisonPath = new ArrayList<>(List.of(
			new WorldPoint(1388, 9573, 0),
			new WorldPoint(1388, 9561, 0),
			new WorldPoint(1376, 9561, 0),
			new WorldPoint(1376, 9546, 0),
			new WorldPoint(1357, 9546, 0),
			new WorldPoint(1357, 9580, 0),
			new WorldPoint(1350, 9581, 0)
		));

		// TODO: Work out build spot ID
		// 50037?
		buildPrisonCamp = new ObjectStep(this, NullObjectID.NULL_53007, new WorldPoint(1349, 9580, 0),
			"Make your way to the camp spot, marked on the minimap with a cooking range icon.", buildingSupplies, hammer, saw);
		((ObjectStep) buildPrisonCamp).setLinePoints(prisonPath);
		leavePrison = new ObjectStep(this, ObjectID.ENTRANCE_51375, new WorldPoint(1388, 9576, 0),
			"Return to the antechamber.");
		((ObjectStep) leavePrison).setLinePoints(Lists.reverse(prisonPath));

		enterEarthbound = new ObjectStep(this, ObjectID.ENTRANCE_51376, new WorldPoint(1421, 9650, 1),
			"Enter the north-west entrance.", buildingSupplies, hammer, saw);

		List<WorldPoint> earthPath = new ArrayList<>(List.of(
			new WorldPoint(1402, 9717, 0),
			new WorldPoint(1402, 9722, 0),
			new WorldPoint(1389, 9722, 0),
			new WorldPoint(1389, 9727, 0),
			new WorldPoint(1375, 9727, 0),
			new WorldPoint(1375, 9720, 0),
			new WorldPoint(1367, 9712, 0),
			new WorldPoint(1367, 9697, 0),
			new WorldPoint(1375, 9697, 0),
			new WorldPoint(1375, 9709, 0)
		));
		buildEarthCamp = new ObjectStep(this, NullObjectID.NULL_53007, new WorldPoint(1374, 9710, 0),
			"Make your way to the camp spot, marked on the minimap with a cooking range icon.", buildingSupplies, hammer, saw);
		((ObjectStep) buildEarthCamp).setLinePoints(earthPath);
		leaveEarth = new ObjectStep(this, ObjectID.ENTRANCE_51375, new WorldPoint(1404, 9716, 0),
			"Return to the antechamber.");
		((ObjectStep) leaveEarth).setLinePoints(Lists.reverse(earthPath));

		enterStreambound = new ObjectStep(this, ObjectID.ENTRANCE_51376, new WorldPoint(1458, 9650, 1),
			"Enter the north-east entrance.");
		List<WorldPoint> streamPath = List.of(
			new WorldPoint(1482, 9671, 0),
			new WorldPoint(1482, 9681, 0),
			new WorldPoint(1485, 9684, 0),
			new WorldPoint(1485, 9695, 0),
			new WorldPoint(1489, 9699, 0),
			new WorldPoint(1507, 9699, 0),
			new WorldPoint(1511, 9703, 0),
			new WorldPoint(1513, 9709, 0),
			new WorldPoint(1520, 9709, 0),
			new WorldPoint(1520, 9693, 0),
			new WorldPoint(1512, 9693, 0)
		);
		buildStreamCamp = new ObjectStep(this, NullObjectID.NULL_53007, new WorldPoint(1510, 9693, 0),
			"Make your way to the camp spot, marked on the minimap with a cooking range icon.", buildingSupplies, hammer, saw);
		((ObjectStep) buildStreamCamp).setLinePoints(streamPath);
		leaveStream = new ObjectStep(this, ObjectID.ENTRANCE_51375, new WorldPoint(1480, 9667, 0),
			"Return to the antechamber.");
		((ObjectStep) leaveStream).setLinePoints(Lists.reverse(streamPath));

		talkToJessamineForTalismans = new NpcStep(this, NpcID.JESSAMINE_12865, new WorldPoint(1440, 9639, 1),
			"Talk to Jessamine near the Monolith.");
		talkToJessamineForTalismans.addDialogSteps("What does the inscription say?", "What do we do now?");
		leaveNeypotzli = new ObjectStep(this, ObjectID.ENTRANCE_51377, new WorldPoint(1440, 9613, 1),
			"Return to Cam Torum through the south entrance.");
		talkToNahta = new NpcStep(this, NpcID.NAHTA, new WorldPoint(1424, 9568, 1),
			"Talk to Nahta in the magic shop.", earthTalisman, waterTalisman);
		talkToNahta.addDialogStep("Actually, I've been sent by Attala.");
		talkToBlacksmith = new NpcStep(this, NpcID.BLACKSMITH_13038, new WorldPoint(1448, 9584, 1),
			"Talk to the blacksmith near the anvils.", enchantedEarthTalisman, enchantedWaterTalisman);
		talkToBlacksmith.addDialogSteps("I need your help with these talismans.", "Can you help?");
		enterNeypotzliAfterBlacksmith = new ObjectStep(this, ObjectID.ENTRANCE_51375, new WorldPoint(1439, 9600, 1),
			"Enter the Neypotzli entrance in the far north of Cam Torum.");
		talkToAttalaAfterBlacksmith = new NpcStep(this, NpcID.ATTALA, new WorldPoint(1439, 9638, 1),
			"Talk to Attala near the Monolith.");
		useTalismansSidebar = new DetailedQuestStep(this, "Use the talismans to find the correct location." +
			" Earth tells you if it's east/west, and the water talisman tells you north/south.");
		useTalismansSidebar.addText("Despite how the world map looks, the antechamber counts as being directly in the middle of the 4 areas, so if it says to go south and east when in there, " +
			"you should go to the ancient shrine, accessed through the prison or the streambound cavern.");
		useTalismans = new DetailedQuestStep(this,
			"Use the talismans to find the correct location. More details in the sidebar.", infusedWaterTalisman, infusedEarthTalisman);
		useTalismansSidebar.addSubSteps(useTalismans);
		talkToEyatlalli = new NpcStep(this, NpcID.EYATLALLI, "Talk to Eyatlalli.");
		talkToJessamineAfterEyatlalli = new NpcStep(this, NpcID.JESSAMINE_12865, new WorldPoint(1440, 9639, 1),
			"Talk to Jessamine near the Monolith again.");
//
		enterStreamboundAgain = new ObjectStep(this, ObjectID.ENTRANCE_51376, new WorldPoint(1458, 9650, 1),
			"Enter the north-east entrance to the streambound cavern.");
		collectGrub = new ObjectStep(this, ObjectID.GRUBBY_SAPLING, new WorldPoint(1517, 9690, 0),
			"Collect a grub from the grubby sapling east of the camp.");
		getHerbloreSupplies = new ObjectStep(this, ObjectID.SUPPLY_CRATES, new WorldPoint(1511, 9695, 0),
			"Take herblore supplies from the supply crates in the camp. You only need the pestle and mortar.");
		getHerbloreSupplies.addDialogStep("Take herblore supplies.");
		useGrubOnPestle = new DetailedQuestStep(this, "Use a pestle and mortar on the moonlight grub.", pestleAndMortar.highlighted(), moonlightGrub.highlighted());
		getFishingSupplies = new ObjectStep(this, ObjectID.SUPPLY_CRATES, new WorldPoint(1511, 9695, 0),
			"Take fishing supplies from the supply crates in the camp.");
		getFishingSupplies.addDialogStep("Take fishing supplies.");
		fishBream = new ObjectStep(this, NullObjectID.NULL_51367, new WorldPoint(1521, 9689, 0),
			"Fish a bream from the stream east of the camp.", bigFishingNet, knife);
		useKnifeOnBream = new DetailedQuestStep(this, "Use a knife on the raw bream.", knife.highlighted(), bream.highlighted());
		getHunterSupplies = new ObjectStep(this, ObjectID.SUPPLY_CRATES, new WorldPoint(1511, 9695, 0),
			"Take hunter supplies from the supply crates in the camp. You only need the rope, and the knife from the fishing supplies.");
		getHunterSupplies.addDialogStep("Take hunting supplies.");
		enterSmallEntrance = new ObjectStep(this, ObjectID.ENTRANCE_51378, new WorldPoint(1522, 9720, 0),
			"Enter the entrance north of the camp.", rope, knife);
		setTrap = new ObjectStep(this, ObjectID.ROCK_51359, new WorldPoint(1377, 9682, 0),
			"Set up a trap by clicking two rocks near one another.", true, rope);
		rustleBush = new ObjectStep(this, ObjectID.BUSH_51358, new WorldPoint(1380, 9684, 0), "Rustle the nearby bush to trap a lizard.");
		pickUpMossLizard = new ItemStep(this, "Pick up the raw moss lizard.", rawMossLizard);
		useKnifeOnLizard = new DetailedQuestStep(this, "Use a knife on the raw moss lizard.", knife.highlighted(), rawMossLizard.highlighted());
		leaveEarthboundWithLizard = new ObjectStep(this, ObjectID.ENTRANCE_51378, new WorldPoint(1389, 9674, 0),
		"Leave the area back to the antechamber via the south-east entrance.");
		talkToEyatlalliWithItems = new NpcStep(this, NpcID.EYATLALLI, new WorldPoint(1445, 9639, 1),
			"Talk to Eyatlalli with the items.", moonlightGrubPaste.hideConditioned(usedPaste),
			breamScales.hideConditioned(usedScales), mossLizardTail.hideConditioned(usedTail));
		talkToEyatlalliAfterItems = new NpcStep(this, NpcID.EYATLALLI, new WorldPoint(1445, 9639, 1),
			"Talk to Eyatlalli.");
		talkToEyatlalliWithItems.addSubSteps(talkToEyatlalliAfterItems);

		enterCamTorumForFights = new ObjectStep(this, ObjectID.ENTRANCE_51375, new WorldPoint(1436, 3129, 0),
			"Enter the entrance to Cam Torum, ready to fight. You can make good food and potions inside Neypotzli.", crushWeapon, slashWeapon, stabWeapon, combatGear);

		enterNeypotzliForFights = new ObjectStep(this, ObjectID.ENTRANCE_51375, new WorldPoint(1439, 9600, 1),
		"Enter the Neypotzli entrance in the far north of Cam Torum, ready to fight.", crushWeapon, slashWeapon, stabWeapon, combatGear);

		enterEarthboundForFight = new ObjectStep(this, ObjectID.ENTRANCE_51376, new WorldPoint(1421, 9650, 1),
			"Enter the north-west entrance to the earthbound cavern when you're ready for the blue moon fight." +
				" Make food and potions from the lizards, breams, and grubby saplings before the fights.", crushWeapon, combatGear);
		enterEarthboundForFight.addSubSteps(enterCamTorumForFights, enterNeypotzliForFights);
		enterBlueMoonFight = new ObjectStep(this, ObjectID.ENTRANCE_51377, new WorldPoint(1404, 9704, 0),
			"Enter the entrance to the blue moon fight.", crushWeapon, combatGear);
		fightBlueMoon = new NpcStep(this, NpcID.BLUE_MOON, new WorldPoint(1441, 9678, 0), "Defeat the blue moon. The sidebar has more details.", crushWeapon);
		fightBlueMoonSidebar = new DetailedQuestStep(this,
			"Defeat the blue moon. Use a crush weapon. Protection prayers have no effect.");
		fightBlueMoonSidebar.addText("Remain inside of the highlighted circle on the floor to remain safe. It will move periodically.");
		fightBlueMoonSidebar.addText("When the highlighted circle is the full or new moon, they will use a special attack:");
		fightBlueMoonSidebar.addText("Weapon freeze - Your weapon is unequipped and put into ice. Attack the ice to get it back.");
		fightBlueMoonSidebar.addText("Brazier - two braziers will extinguish. Re-ignite them whilst avoiding the tornadoes.");
		fightBlueMoonSidebar.addSubSteps(fightBlueMoon);

		enterPrisonForFight = new ObjectStep(this, ObjectID.ENTRANCE_51376, new WorldPoint(1421, 9613, 1),
			"Enter the ancient prison when you're ready for the blood moon fight." +
				" Make food and potions from the lizards, breams, and grubby saplings before the fights.",
			slashWeapon, combatGear);
		enterBloodMoonFight = new ObjectStep(this, ObjectID.ENTRANCE_51375, new WorldPoint(1388, 9589, 0),
			"Enter the north-east entrance to the blood moon fight.", slashWeapon, combatGear);
		fightBloodMoon = new NpcStep(this, NpcID.BLOOD_MOON, new WorldPoint(1391, 9632, 0), "Defeat the blood moon. The sidebar has more details.", slashWeapon);
		fightBloodMoonSidebar = new DetailedQuestStep(this,
			"Defeat the blood moon. Use a slash weapon. Protection prayers have no effect.");
		fightBloodMoonSidebar.addText("Remain inside of the highlighted circle on the floor to remain safe. It will move periodically.");
		fightBloodMoonSidebar.addText("When the highlighted circle is the full or new moon, they will use a special attack:");
		fightBloodMoonSidebar.addText("Raining blood - Blood falls from the ceiling. Avoid the blood puddles.");
		fightBloodMoonSidebar.addText("Blood jaguar - Jaguars appear. Attack the highlighted jaguar, avoiding the blood square. Step away from the jaguar just before each of its attacks.");
		fightBloodMoonSidebar.addSubSteps(fightBloodMoon);

		enterStreamboundForFight = new ObjectStep(this, ObjectID.ENTRANCE_51376, new WorldPoint(1458, 9650, 1),
			"Enter the streambound cavern when you're ready for the eclipse moon fight." +
				" Make food and potions from the lizards, breams, and grubby saplings before the fights.",
			stabWeapon, combatGear);
		enterEclipseMoonFight = new ObjectStep(this, ObjectID.ENTRANCE_51375, new WorldPoint(1509, 9673, 0),
			"Enter the south entrance to the eclipse moon fight.", stabWeapon, combatGear);
		fightEclipseMoon = new NpcStep(this, NpcID.ECLIPSE_MOON, new WorldPoint(1487, 9632, 0), "Defeat the eclipse moon. The sidebar has more details.", stabWeapon);
		fightEclipseMoonSidebar = new DetailedQuestStep(this,
			"Defeat the eclipse moon. Use a stab weapon. Protection prayers have no effect.");
		fightEclipseMoonSidebar.addText("Remain inside of the highlighted circle on the floor to remain safe. It will move periodically.");
		fightEclipseMoonSidebar.addText("Only use food and potions made inside of Neypotzli.");
		fightEclipseMoonSidebar.addText("When the highlighted circle is the full or new moon, they will use a special attack:");
		fightEclipseMoonSidebar.addText("Eclipse shield - A moon shield appears. Keep behind the shield as it moves.");
		fightEclipseMoonSidebar.addText("Mimic - Clones of the moon will appear. Face the clones to avoid taking damage.");
		fightEclipseMoonSidebar.addSubSteps(fightEclipseMoon);

		talkToJessamineEnd = new NpcStep(this, NpcID.JESSAMINE_12865, new WorldPoint(1440, 9639, 1),
			"Return to Jessamine near the Monolith again.");
		talkToZumaEnd = new NpcStep(this, NpcID.ZUMA_12860, new WorldPoint(1441, 9638, 1), "Talk to Zuma.");
		finishQuest = new NpcStep(this, NpcID.EYATLALLI, new WorldPoint(1445, 9639, 1),
			"Talk to Eyatlalli to finish the quest.");
	}

	@Override
	protected List<ItemRequirement> generateItemRequirements()
	{
		return Arrays.asList(pestleAndMortar, bigFishingNet, knife, rope, combatGear);
	}

	@Override
    protected List<ItemRequirement> generateItemRecommended()
	{
		return Arrays.asList(staminaPotions, antipoison);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.TWILIGHTS_PROMISE, QuestState.FINISHED),
			new SkillRequirement(Skill.SLAYER, 48),
			new SkillRequirement(Skill.HUNTER, 20),
			new SkillRequirement(Skill.FISHING,  20),
			new SkillRequirement(Skill.RUNECRAFT, 20),
			new SkillRequirement(Skill.CONSTRUCTION, 10)
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Sulphur Nagua (level 98)",
			"Blue Moon (level 329)",
			"Blood Moon (level 329)",
			"Eclipse Moon (level 329)");
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
			new ExperienceReward(Skill.SLAYER, 40000),
			new ExperienceReward(Skill.RUNECRAFT, 5000),
			new ExperienceReward(Skill.HUNTER, 5000),
			new ExperienceReward(Skill.FISHING, 5000)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(new UnlockReward("Access to Neypotzli and the Moons of Peril"));
	}

	@Override
    protected List<PanelDetails> setupPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Ruined", List.of(
			talkToAttala, killSulphurNagua, returnToAttala, enterCamTorum, talkToJessamine, enterNeypotzli,
			talkToAttalaInNey, talkToZumaInNey, talkToJessamineInNey
		), combatGear));
		allSteps.add(new PanelDetails("Camping up", List.of(
			takeBuildingSupplies, enterPrison, buildPrisonCamp, leavePrison,
			enterEarthbound, buildEarthCamp, leaveEarth, enterStreambound, buildStreamCamp, leaveStream
		), emptyInventory5));
		allSteps.add(new PanelDetails("Enchanting", List.of(
			talkToJessamineForTalismans, leaveNeypotzli, talkToNahta, talkToBlacksmith, enterNeypotzliAfterBlacksmith,
			useTalismansSidebar, talkToEyatlalli, talkToJessamineAfterEyatlalli
		)));
		allSteps.add(new PanelDetails("Ritual", List.of(
			enterStreamboundAgain, getHerbloreSupplies, collectGrub, useGrubOnPestle, getFishingSupplies, fishBream,
			useKnifeOnBream, getHunterSupplies, enterSmallEntrance, setTrap, rustleBush, pickUpMossLizard, useKnifeOnLizard,
			leaveEarthboundWithLizard, talkToEyatlalliWithItems
		)));
		allSteps.add(new PanelDetails("Blue moon", List.of(
			enterEarthboundForFight, enterBlueMoonFight, fightBlueMoonSidebar
		), crushWeapon, combatGear));
		allSteps.add(new PanelDetails("Blood moon", List.of(
			enterPrisonForFight, enterBloodMoonFight, fightBloodMoonSidebar
		), slashWeapon, combatGear));
		allSteps.add(new PanelDetails("Eclipse moon", List.of(
			enterStreamboundForFight, enterEclipseMoonFight, fightEclipseMoonSidebar
		), stabWeapon, combatGear));
		allSteps.add(new PanelDetails("Finishing off", List.of(
			talkToJessamineEnd, talkToZumaEnd, finishQuest
		)));
		return allSteps;
	}
}
