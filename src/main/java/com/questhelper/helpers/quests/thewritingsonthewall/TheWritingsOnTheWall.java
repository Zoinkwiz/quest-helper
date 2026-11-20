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
package com.questhelper.helpers.quests.thewritingsonthewall;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.nor;
import static com.questhelper.requirements.util.LogicHelper.not;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetPresenceRequirement;
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
import com.questhelper.steps.TileStep;
import com.questhelper.steps.UnreachableStep;
import com.questhelper.steps.WidgetStep;
import com.questhelper.util.QuestStepIcon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

@SuppressWarnings("FieldCanBeLocal")
public class TheWritingsOnTheWall extends BasicQuestHelper
{
	// Recommended items
	ItemRequirement energyRestorePotion;
	FreeInventorySlotRequirement freeSlots;

	// Mid-quest item requirements
	ItemRequirement newspaperCutting;
	ItemRequirement letter;
	ItemRequirement bloodyKnife;
	ItemRequirement hosidiusCertificate;
	ItemRequirement uncleHead;
	ItemRequirement letterFromUncleHead;

	// Miscellaneous requirements
	private VarbitRequirement needToReadNews;
	private VarbitRequirement needWallInfo;
	private VarbitRequirement needHandsInfo;
	private VarbitRequirement needLetter;
	private VarbitRequirement needToInspectBloodOnWall;
	private VarbitRequirement needToQuestionLanAboutVictim;
	private VarbitRequirement needToSearchButcherBed;
	private VarbitRequirement needToQuestionHugor;
	private VarbitRequirement needAlibiFromBetty;
	private VarbitRequirement needHistoryFromBetty;
	private ZoneRequirement inUnclesHouse;
	private ZoneRequirement inBasement;
	private VarbitRequirement needToInvestigateHead;
	private VarbitRequirement needToInvestigateArms;
	private VarbitRequirement needToInvestigateShelves;
	private VarbitRequirement needToAskBettyAboutUnclesDrinkingHabits;
	private VarbitRequirement needToAskBettyAboutUnclesJob;
	private VarbitRequirement needToReadCertificate;
	private Conditions needToGetCertificateFromUpstairs;
	private Conditions needToSearchCompost;
	private VarbitRequirement needToInspectHead;
	private VarbitRequirement needToSpeakToFarmer;
	private VarbitRequirement needToSpeakToMonk;
	private VarbitRequirement needToTalkToBettyAboutKeystoneLodge;
	private VarbitRequirement hosidiusReadyToTalkToBetty;
	private ZoneRequirement inPrisonCell;
	private ZoneRequirement inPiscHouseFirstFloor;
	private ZoneRequirement inPiscHouseSecondFloor;
	private VarbitRequirement needToInspectPiscHead;
	private VarbitRequirement needToInspectPiscTorso;
	private VarbitRequirement needToInspectPiscLegs;
	private VarbitRequirement needToInspectPiscDrawers;
	private VarbitRequirement needToInspectPiscDrawersForDagger;
	private VarbitRequirement needToInspectPiscBed;
	private VarbitRequirement needToInspectPiscWardrobe;
	private VarbitRequirement needToInspectShayzienHead;
	private VarbitRequirement needToInspectShayzienArms;
	private VarbitRequirement needToInspectShayzienWall;
	private VarbitRequirement needToInspectShayzienShelves;
	private VarbitRequirement needToInterrogateKyle;
	private VarbitRequirement needToTalkToSergeantHudsonInShayzien;
	private VarbitRequirement shayzienLearnedEverything;
	private ZoneRequirement inSecretHideout;
	private WidgetPresenceRequirement isInspectingSecretHideoutBlood;
	private VarbitRequirement inCutscene;

	// Steps
	private DetailedQuestStep unreachableStep;
	private NpcStep startQuest;
	private NpcStep talkToSergeantHudson;
	private ObjectStep searchShelves;
	private DetailedQuestStep readNews;
	private ObjectStep inspectBodyWall;
	private ObjectStep inspectBodyHands;
	private ObjectStep inspectBodyTorso;
	private DetailedQuestStep readLetter;
	private NpcStep returnToSergeantHudson;
	private ObjectStep inspectBloodOnWall;
	private NpcStep questionButcher;
	private NpcStep questionButcherAboutKnife;
	private ObjectStep searchButcherBed;
	private NpcStep questionHugor;
	private NpcStep getHistoryFromBetty;
	private NpcStep getAlibiFromBetty;
	private ConditionalStep cInvestigate;

	private NpcStep returnToSergeantHudsonForCutscene;
	private NpcStep talkToBettyAfterSergeantHudsonLeft;
	private NpcStep talkToBettyAtUnclesHouse;
	private ObjectStep enterUnclesHouseThroughWindow;
	private ObjectStep openDoorForBetty;
	private ConditionalStep cBreakIntoUnclesHouse;
	private ObjectStep climbDownLadder;
	private NpcStep talkToBettyInUnclesBasement;
	private ConditionalStep cTalkToBettyInUnclesBasement;
	private ObjectStep climbDownLadder2;
	private ObjectStep investigateHead;
	private ObjectStep investigateArms;
	private ObjectStep investigateShelves;
	private NpcStep askAboutUnclesDrinkingHabits;
	private NpcStep askAboutUncle;
	private ObjectStep goUpstairs;
	private ObjectStep getCertificate;
	private DetailedQuestStep readCertificate;
	private ObjectStep searchCompost;
	private DetailedQuestStep inspectHead;
	private DetailedQuestStep inspectLetterFromHead;
	private NpcStep speakToFarmer;
	private NpcStep speakToMonk;
	private NpcStep talkToBettyAboutKeystoneLodge;
	private NpcStep talkToBettyInUnclesBasementAboutEverythingYouHaveFound;
	private ConditionalStep cInvestigateUnclesBasement;
	private NpcStep headToPiscariliusWithBetty;
	private ConditionalStep cHeadToPiscariliusWithBetty;
	private NpcStep introduceYourselfToPiscCaptain;
	private NpcStep talkToBettyInPrisonCell;
	private ObjectStep removePlankFromWall;
	private ObjectStep escapePrisonCell;
	private ObjectStep sneakPastGuardsToNorthWesternHouse;
	private ObjectStep inspectBodyUpstairsInPiscHouse;
	private ConditionalStep cInvestigatePisc;
	private ObjectStep inspectPiscHead;
	private ObjectStep inspectPiscTorso;
	private ObjectStep inspectPiscLegs;
	private ObjectStep inspectPiscDrawers;
	private ObjectStep inspectPiscDrawersForDagger;
	private ObjectStep piscClimbToSecondFloor;
	private ObjectStep inspectPiscBed;
	private ObjectStep inspectPiscWardrobe;
	private ConditionalStep cInvestigatePisc2;
	private ObjectStep piscClimbDownFromSecondFloor;
	private ObjectStep piscClimbDownFromFirstFloor;
	private TileStep piscGetCaught;
	private ObjectStep inspectPiscBlood;
	private ConditionalStep cSpeakToOneOfTheGuardsDownstairsInPisc;
	private ObjectStep escapePrisonCell2;
	private DetailedQuestStep shayzienEnterWarTent;
	private ConditionalStep cShayzienEnterWarTent;
	private ObjectStep inspectShayzienHead;
	private ObjectStep inspectShayzienArms;
	private ObjectStep inspectShayzienWall;
	private ObjectStep inspectShayzienShelves;
	private NpcStep interrogateKyle;
	private NpcStep talkToSergeantHudsonInShayzien;
	private NpcStep talkToSergeantHudsonInShayzien2;
	private ConditionalStep cShayzienMurder;
	private ObjectStep enterSecretTrapdoor;
	private ObjectStep inspectSecretHideoutBlood;
	private WidgetStep closeSecretHideoutBlood;
	private NpcStep talkToBettyTheMurderer;
	private ConditionalStep cEnterSecretTrapdoor;
	private NpcStep finishQuest;

	@Override
	protected void setupRequirements()
	{
		energyRestorePotion = new ItemRequirement("Stamina potion", ItemCollections.STAMINA_POTIONS);
		freeSlots = new FreeInventorySlotRequirement(5); // TODO: confirm

		newspaperCutting = new ItemRequirement("Newspaper cutting", ItemID.WOTW_NEWS_LANDS_END);
		letter = new ItemRequirement("Letter", ItemID.WOTW_LETTER_LANDS_END);
		bloodyKnife = new ItemRequirement("Bloody knife", ItemID.WOTW_KNIFE_LANDS_END);
		hosidiusCertificate = new ItemRequirement("Certificate", ItemID.WOTW_CERTIFICATE_HOSIDIUS).highlighted();
		uncleHead = new ItemRequirement("Human head", ItemID.WOTW_HEAD_HOSIDIUS).highlighted();
		letterFromUncleHead = new ItemRequirement("Letter", ItemID.WOTW_LETTER_HOSIDIUS).highlighted();

		needToReadNews = new VarbitRequirement(VarbitID.WOTW_LANDS_END_NEWS, Operation.LESS, 2, "need to read news");
		needWallInfo = new VarbitRequirement(VarbitID.WOTW_LANDS_END_WALL, 0);
		needHandsInfo = new VarbitRequirement(VarbitID.WOTW_LANDS_END_HANDS, 0);
		needLetter = new VarbitRequirement(VarbitID.WOTW_LANDS_END_LETTER, Operation.LESS, 2, "Need to read the letter");
		needToInspectBloodOnWall = new VarbitRequirement(VarbitID.WOTW_LANDS_END_WALL, 1);
		needToQuestionLanAboutVictim = new VarbitRequirement(VarbitID.WOTW_LANDS_END_BUTCHER_QUESTIONED, 0);
		needToSearchButcherBed = new VarbitRequirement(VarbitID.WOTW_LANDS_END_KNIFE, 0);
		needToQuestionHugor = new VarbitRequirement(VarbitID.WOTW_LANDS_END_HUGOR_ALIBI, 0);
		needAlibiFromBetty = new VarbitRequirement(VarbitID.WOTW_LANDS_END_BETTY_ALIBI, 0);
		needHistoryFromBetty = new VarbitRequirement(VarbitID.WOTW_LANDS_END_HISTORY, 0);
		inUnclesHouse = ZoneRequirement.square(1784, 3526, 0, 1789, 3521, 0);
		inBasement = ZoneRequirement.square(1745, 9938, 0, 1750, 9933, 0);
		needToInvestigateHead = new VarbitRequirement(VarbitID.WOTW_HOSIDIUS_HEAD, 0);
		needToInvestigateArms = new VarbitRequirement(VarbitID.WOTW_HOSIDIUS_UNFINISHED_DRINK, 0);
		needToInvestigateShelves = new VarbitRequirement(VarbitID.WOTW_HOSIDIUS_DRINK_SHELVES, 0);
		needToAskBettyAboutUnclesDrinkingHabits = new VarbitRequirement(VarbitID.WOTW_HOSIDIUS_UNCLE_DRINKING_HABITS, 0);
		needToAskBettyAboutUnclesJob = new VarbitRequirement(VarbitID.WOTW_HOSIDIUS_UNCLE_JOB, 0);
		needToReadCertificate = new VarbitRequirement(VarbitID.WOTW_HOSIDIUS_CERTIFICATE, 1);
		needToGetCertificateFromUpstairs = or(new VarbitRequirement(VarbitID.WOTW_HOSIDIUS_CERTIFICATE, 0), and(needToReadCertificate, not(hosidiusCertificate)));
		needToSearchCompost = or(new VarbitRequirement(VarbitID.WOTW_HOSIDIUS_HEAD, 1), nor(uncleHead, letterFromUncleHead));
		needToInspectHead = new VarbitRequirement(VarbitID.WOTW_HOSIDIUS_HEAD, 2);
		needToSpeakToFarmer = new VarbitRequirement(VarbitID.WOTW_HOSIDIUS_STRANGER_INFO, 0);
		needToSpeakToMonk = new VarbitRequirement(VarbitID.WOTW_HOSIDIUS_CONFRONTATION_INFO, 0);
		needToTalkToBettyAboutKeystoneLodge = new VarbitRequirement(VarbitID.WOTW_HOSIDIUS_CERTIFICATE, 2);
		hosidiusReadyToTalkToBetty = new VarbitRequirement(VarbitID.WOTW_HOSIDIUS_NOTIFY, 1);
		inPrisonCell = ZoneRequirement.square(1784, 3782, 0, 1789, 3779, 0);
		inPiscHouseFirstFloor = ZoneRequirement.square(1753, 3773, 1, 1759, 3784, 1);
		inPiscHouseSecondFloor = ZoneRequirement.square(1753, 3773, 2, 1759, 3784, 2);
		needToInspectPiscHead = new VarbitRequirement(VarbitID.WOTW_PISCARILIUS_HEAD, 0);
		needToInspectPiscTorso = new VarbitRequirement(VarbitID.WOTW_PISCARILIUS_TORSO, 0);
		needToInspectPiscLegs = new VarbitRequirement(VarbitID.WOTW_PISCARILIUS_FEET, 0);
		needToInspectPiscDrawers = new VarbitRequirement(VarbitID.WOTW_PISCARILIUS_BLOOD, 0);
		needToInspectPiscDrawersForDagger = new VarbitRequirement(VarbitID.WOTW_PISCARILIUS_DAGGER, 0);
		needToInspectPiscBed = new VarbitRequirement(VarbitID.WOTW_PISCARILIUS_BED, 0);
		needToInspectPiscWardrobe = new VarbitRequirement(VarbitID.WOTW_PISCARILIUS_WARDROBE, 0);
		needToInspectShayzienHead = new VarbitRequirement(VarbitID.WOTW_SHAYZIEN_EYES, 0);
		needToInspectShayzienArms = new VarbitRequirement(VarbitID.WOTW_SHAYZIEN_HANDS, 0);
		needToInspectShayzienWall = new VarbitRequirement(VarbitID.WOTW_SHAYZIEN_WALL, 1);
		needToInspectShayzienShelves = new VarbitRequirement(VarbitID.WOTW_SHAYZIEN_DRINK_SHELVES, 0);
		needToInterrogateKyle = new VarbitRequirement(VarbitID.WOTW_SHAYZIEN_WITNESS, 0);
		needToTalkToSergeantHudsonInShayzien = new VarbitRequirement(VarbitID.WOTW_SHAYZIEN_VICTIM_INFO, 0);
		shayzienLearnedEverything = new VarbitRequirement(VarbitID.WOTW_SHAYZIEN_NOTIFY, 1);
		inSecretHideout = ZoneRequirement.square(1620, 9946, 0, 1626, 9931, 0);
		isInspectingSecretHideoutBlood = new WidgetPresenceRequirement(InterfaceID.WotwWallHideout.CONTENT);
		inCutscene = new VarbitRequirement(VarbitID.CUTSCENE_STATUS, 1);
	}

	void setupSteps()
	{
		unreachableStep = new UnreachableStep(this);

		startQuest = new NpcStep(this, NpcID.WOTW_BETTY_CHILD, new WorldPoint(1518, 3437, 0), "Talk to Betty in Land's End to start the quest.");
		startQuest.addDialogStep("Yes.");


		// talk to Sergeant Hudson
		talkToSergeantHudson = new NpcStep(this, NpcID.WOTW_HUDSON_CHILD, new WorldPoint(1521, 3441, 0), "Talk to Sergeant Hudson and about the investigation.");

		searchShelves = new ObjectStep(this, ObjectID.WOTW_SHELVES_LANDS_END, "Search the shelves inside Betty's house.");
		readNews = new DetailedQuestStep(this, "Read the newspaper cutting.", newspaperCutting.highlighted());


		inspectBodyWall = new ObjectStep(this, ObjectID.WOTW_BED_LANDS_END_BODY_INSPECT, new WorldPoint(1514, 3438, 0), "Inspect the dead body inside Betty's house.");
		inspectBodyWall.addDialogStep("Inspect the head.");

		inspectBodyHands = new ObjectStep(this, ObjectID.WOTW_BED_LANDS_END_BODY_INSPECT, new WorldPoint(1514, 3438, 0), "Inspect the dead body inside Betty's house.");
		inspectBodyHands.addDialogStep("Inspect the arms.");

		inspectBodyTorso = new ObjectStep(this, ObjectID.WOTW_BED_LANDS_END_BODY_INSPECT, new WorldPoint(1514, 3438, 0), "Inspect the dead body inside Betty's house.");
		inspectBodyTorso.addDialogStep("Inspect the torso.");
		inspectBodyTorso.addDialogStep("Yes.");

		inspectBodyWall.addSubSteps(inspectBodyHands, inspectBodyTorso);

		readLetter = new DetailedQuestStep(this, "Read the letter you picked up from the body.", letter.highlighted());

		inspectBloodOnWall = new ObjectStep(this, ObjectID.WOTW_BLOOD_LANDS_END_INSPECT, new WorldPoint(1517, 3439, 0), "Inspect the blood on the wall near the entrance.");

		// Speak to Lan the Butcher
		questionButcher = new NpcStep(this, NpcID.KW_BUTCHER_LAN_VIS, new WorldPoint(1498, 3422, 0), "Talk to Lan the Butcher in the south-western house of Land's end.");
		questionButcher.addDialogStep("What do you know about the victim?");
		questionButcherAboutKnife = new NpcStep(this, NpcID.KW_BUTCHER_LAN_VIS, new WorldPoint(1498, 3422, 0), "Talk to Lan the Butcher in the south-western house of Land's end.");
		questionButcherAboutKnife.addDialogStep("I found this knife under your bed...");

		questionButcher.addSubSteps(questionButcherAboutKnife);

		searchButcherBed = new ObjectStep(this, ObjectID.WOTW_BUTCHER_BED_LANDS_END, new WorldPoint(1496, 3419, 0), "Search Lan the Butcher's bed.");

		questionHugor = new NpcStep(this, NpcID.KW_HUGOR, new WorldPoint(1510, 3420, 0), "Talk to Hugor in Land's End");
		questionHugor.addDialogStep("Did you see or hear anything around the time of the murder?");

		getAlibiFromBetty = new NpcStep(this, NpcID.WOTW_BETTY_CHILD, new WorldPoint(1518, 3437, 0), "Talk to Betty in Land's End to start the quest.");
		getAlibiFromBetty.addDialogStep("Did you see or hear anything before your father's death?");
		getHistoryFromBetty = new NpcStep(this, NpcID.WOTW_BETTY_CHILD, new WorldPoint(1518, 3437, 0), "Talk to Betty in Land's End to start the quest.");
		getHistoryFromBetty.addDialogStep("Do you know why someone would want your father dead?");

		returnToSergeantHudson = new NpcStep(this, NpcID.WOTW_HUDSON_CHILD, new WorldPoint(1521, 3441, 0), "Return to Sergeant Hudson and fill him in on what you've found.");

		cInvestigate = new ConditionalStep(this, returnToSergeantHudson);
		// Shelves
		cInvestigate.addStep(and(newspaperCutting, needToReadNews), readNews);
		cInvestigate.addStep(needToReadNews, searchShelves);

		// Body
		cInvestigate.addStep(needWallInfo, inspectBodyWall);
		cInvestigate.addStep(needHandsInfo, inspectBodyHands);
		cInvestigate.addStep(and(needLetter, letter), readLetter);
		cInvestigate.addStep(needLetter, inspectBodyTorso);

		// Blood on the wall
		cInvestigate.addStep(needToInspectBloodOnWall, inspectBloodOnWall);

		cInvestigate.addStep(needToQuestionLanAboutVictim, questionButcher);
		cInvestigate.addStep(needToSearchButcherBed, searchButcherBed);
		// TODO: Is this step actually necessary?
		// cInvestigate.addStep(bloodyKnife, questionButcherAboutKnife);

		// Speak to Hugor
		cInvestigate.addStep(needToQuestionHugor, questionHugor);

		cInvestigate.addStep(needAlibiFromBetty, getAlibiFromBetty);
		cInvestigate.addStep(needHistoryFromBetty, getHistoryFromBetty);


		// TODO: STEP 6 IS MISSING THE ALIBI PART FROM BETTY, AND MAYBE THE HISTORY IS REQUIRED TOO

		/// 8: You have given Hudson the information, and there's a cutscene!
		returnToSergeantHudsonForCutscene = new NpcStep(this, NpcID.WOTW_HUDSON_CHILD, new WorldPoint(1521, 3441, 0), "Return to Sergeant Hudson and fill him in on what you've found.");

		returnToSergeantHudson.addSubSteps(returnToSergeantHudsonForCutscene);

		// TODO: ADD SIDEBAR STEPS FROM HERE

		/// 10: Cutscene is finished, we are talking to Betty
		talkToBettyAfterSergeantHudsonLeft = new NpcStep(this, NpcID.WOTW_BETTY_CHILD, new WorldPoint(1518, 3437, 0), "Talk to Betty outside of her house in Land's end.");

		/// 12: Agreed to meet Betty at her uncle's house, north-west of the Tithe Farm
		talkToBettyAtUnclesHouse = new NpcStep(this, NpcID.WOTW_BETTY_CHILD, new WorldPoint(1784, 3519, 0), "Talk to Betty outside of her uncles house, north-west of the Tithe Farm.");

		/// 14: Curiously, the uncle isn't home :O. Let's break into the house and see if he's there.
		enterUnclesHouseThroughWindow = new ObjectStep(this, ObjectID.WOTW_HOSIDIUS_WINDOW, new WorldPoint(1783, 3525, 0), "Enter Betty's uncle's house through the open window on the western wall.");
		openDoorForBetty = new ObjectStep(this, ObjectID.WOTW_HOSIDIUS_FRONT_DOOR, new WorldPoint(1785, 3520, 0), "Open the front door for Betty.");
		cBreakIntoUnclesHouse = new ConditionalStep(this, enterUnclesHouseThroughWindow);
		cBreakIntoUnclesHouse.addStep(inUnclesHouse, openDoorForBetty);

		/// 16: Betty walked down by herself and found her Uncle dead.
		climbDownLadder = new ObjectStep(this, ObjectID.HOSIDIUS_BASEMENT_LADDER_ENTRY, new WorldPoint(1789, 3521, 0), "Climb down the ladder to Betty's uncle's basement.");
		talkToBettyInUnclesBasement = new NpcStep(this, NpcID.WOTW_BETTY_CHILD, new WorldPoint(1749, 9934, 0), "Talk to Betty in her uncle's basement.");
		cTalkToBettyInUnclesBasement = new ConditionalStep(this, climbDownLadder);
		cTalkToBettyInUnclesBasement.addStep(inBasement, talkToBettyInUnclesBasement);

		/// 18: Before talking to the authorities, I will just look through the crime scene. Clueless
		climbDownLadder2 = new ObjectStep(this, ObjectID.HOSIDIUS_BASEMENT_LADDER_ENTRY, new WorldPoint(1789, 3521, 0), "Climb down to Betty's uncle's basement.");

		investigateHead = new ObjectStep(this, ObjectID.WOTW_TABLE_HOSIDIUS_BODY, new WorldPoint(1747, 9935, 0), "Investigate the dead body on the table.");
		investigateHead.addDialogStep("Inspect the head.");

		investigateArms = new ObjectStep(this, ObjectID.WOTW_TABLE_HOSIDIUS_BODY, new WorldPoint(1747, 9935, 0), "Investigate the dead body on the table.");
		investigateArms.addDialogStep("Inspect the arms.");

		investigateHead.addSubSteps(investigateArms);

		investigateShelves = new ObjectStep(this, ObjectID.WOTW_DRINK_SHELVES_HOSIDIUS, new WorldPoint(1747, 9938, 0), "Investigate the shelves at the back of the basement.");

		askAboutUnclesDrinkingHabits = new NpcStep(this, NpcID.WOTW_BETTY_CHILD, new WorldPoint(1749, 9934, 0), "Ask Betty about her uncle's drinking habits.");
		askAboutUnclesDrinkingHabits.addDialogStep("Was your uncle a big drinker?");

		askAboutUncle = new NpcStep(this, NpcID.WOTW_BETTY_CHILD, new WorldPoint(1749, 9934, 0), "Ask Betty about her uncle's potential enemies.");
		askAboutUncle.addDialogStep("Do you know why someone would want your uncle dead?");

		goUpstairs = new ObjectStep(this, ObjectID.HOSIDIUS_BASEMENT_LADDER_EXIT, new WorldPoint(1750, 9933, 0), "Climb up from Betty's uncle's basement.");
		getCertificate = new ObjectStep(this, ObjectID.WOTW_BOOKCASE_HOSIDIUS, new WorldPoint(1784, 3523, 0), "Search the bookcase upstairs for a mysterious certificate.");
		readCertificate = new DetailedQuestStep(this, "Read the certificate.", hosidiusCertificate);

		searchCompost = new ObjectStep(this, ObjectID.WOTW_COMPOST_BIN_HOSIDIUS, new WorldPoint(1786, 3531, 0), "Search the compost bin outside Betty's uncle's house.");

		inspectHead = new DetailedQuestStep(this, "Inspect the human head.", uncleHead);
		inspectLetterFromHead = new DetailedQuestStep(this, "Inspect the letter that you just found in the human head.", letterFromUncleHead);

		speakToFarmer = new NpcStep(this, NpcID.WOTW_FARMER, new WorldPoint(1772, 3529, 0), "Speak to the farmer west of Betty's uncle's house.");
		speakToFarmer.addDialogStep("Have you seen anything odd around here lately?");

		speakToMonk = new NpcStep(this, NpcID.WOTW_MONK, new WorldPoint(1783, 3516, 0), "Speak to the monk south of Betty's uncle's house.");
		speakToMonk.addDialogStep("Do you know anything about the person who lives in this house?");

		talkToBettyAboutKeystoneLodge = new NpcStep(this, NpcID.WOTW_BETTY_CHILD, new WorldPoint(1749, 9934, 0), "Return to Betty in her uncle's basement to ask her about the Keystone Lodge.");
		talkToBettyAboutKeystoneLodge.addDialogStep("Have you heard of the Keystone Lodge?");

		talkToBettyInUnclesBasementAboutEverythingYouHaveFound = new NpcStep(this, NpcID.WOTW_BETTY_CHILD, new WorldPoint(1749, 9934, 0), "Talk to Betty in her uncle's basement with all the information you've found.");
		talkToBettyInUnclesBasementAboutEverythingYouHaveFound.addDialogStep("Yes, I think so.");

		cInvestigateUnclesBasement = new ConditionalStep(this, climbDownLadder2);
		cInvestigateUnclesBasement.addStep(and(inBasement, hosidiusReadyToTalkToBetty), talkToBettyInUnclesBasementAboutEverythingYouHaveFound);
		cInvestigateUnclesBasement.addStep(hosidiusReadyToTalkToBetty, climbDownLadder2);

		cInvestigateUnclesBasement.addStep(and(inBasement, needToInvestigateHead), investigateHead);
		cInvestigateUnclesBasement.addStep(and(inBasement, needToInvestigateArms), investigateArms);
		cInvestigateUnclesBasement.addStep(and(inBasement, needToInvestigateShelves), investigateShelves);
		cInvestigateUnclesBasement.addStep(and(inBasement, needToAskBettyAboutUnclesDrinkingHabits), askAboutUnclesDrinkingHabits);
		cInvestigateUnclesBasement.addStep(and(inBasement, needToAskBettyAboutUnclesJob), askAboutUncle);
		cInvestigateUnclesBasement.addStep(and(inBasement, needToAskBettyAboutUnclesJob), askAboutUncle);

		cInvestigateUnclesBasement.addStep(and(inBasement, needToGetCertificateFromUpstairs), goUpstairs);
		cInvestigateUnclesBasement.addStep(needToGetCertificateFromUpstairs, getCertificate);
		cInvestigateUnclesBasement.addStep(needToReadCertificate, readCertificate);

		cInvestigateUnclesBasement.addStep(and(inBasement, needToSearchCompost), goUpstairs);
		cInvestigateUnclesBasement.addStep(needToSearchCompost, searchCompost);
		cInvestigateUnclesBasement.addStep(and(needToInspectHead, uncleHead), inspectHead);
		cInvestigateUnclesBasement.addStep(and(needToInspectHead, letterFromUncleHead), inspectLetterFromHead);

		cInvestigateUnclesBasement.addStep(and(inBasement, needToSpeakToFarmer), goUpstairs);
		cInvestigateUnclesBasement.addStep(and(needToSpeakToFarmer), speakToFarmer);

		cInvestigateUnclesBasement.addStep(and(inBasement, needToSpeakToMonk), goUpstairs);
		cInvestigateUnclesBasement.addStep(and(needToSpeakToMonk), speakToMonk);

		cInvestigateUnclesBasement.addStep(and(inBasement, needToTalkToBettyAboutKeystoneLodge), talkToBettyAboutKeystoneLodge);
		cInvestigateUnclesBasement.addStep(needToTalkToBettyAboutKeystoneLodge, climbDownLadder);


		headToPiscariliusWithBetty = new NpcStep(this, NpcID.WOTW_BETTY_CHILD, new WorldPoint(1749, 9934, 0), "Return to Betty in her uncle's basement when you're ready to head to Port Piscarilius.");
		headToPiscariliusWithBetty.addDialogStep("Let's get going.");
		cHeadToPiscariliusWithBetty = new ConditionalStep(this, climbDownLadder2);
		cHeadToPiscariliusWithBetty.addStep(inBasement, headToPiscariliusWithBetty);

		// 22: headed to Port Piscarilius with Betty
		introduceYourselfToPiscCaptain = new NpcStep(this, NpcID.WOTW_HUDSON_CHILD, new WorldPoint(1751, 3783, 0), "Talk to Sergeant Hudson in Port Piscarilius.");

		// SECTION: PRISON BREAK

		// 24: \o/ CAUGHT by Captain Hudson in Port Piscarilius
		talkToBettyInPrisonCell = new NpcStep(this, NpcID.WOTW_BETTY_CHILD, new WorldPoint(1786, 3781, 0), "Talk to Betty in the Port Piscarilius prison cell.");

		// 26: In the prison cell, talked to Betty about an escape plan
		// TODO: what happens if you just teleport out lol
		removePlankFromWall = new ObjectStep(this, ObjectID.WOTW_PISCARILIUS_CELL_WALL_BLOCKED, new WorldPoint(1787, 3782, 0), "Remove the plank from the northern wall of the cell and escape.");
		removePlankFromWall.addAlternateObjects(ObjectID.WOTW_PISCARILIUS_CELL_WALL_UNBLOCKED);
		removePlankFromWall.addDialogStep("Climb through the gap.");

		// 28: Escaped the prison
		escapePrisonCell = new ObjectStep(this, ObjectID.WOTW_PISCARILIUS_CELL_WALL_UNBLOCKED, new WorldPoint(1787, 3782, 0), "Escape the prison cell through the northern wall.");
		escapePrisonCell.addDialogStep("Climb through the gap.");

		sneakPastGuardsToNorthWesternHouse = new ObjectStep(this, ObjectID.WOTW_PISCARILIUS_LADDER_BOTTOM, new WorldPoint(1754, 3782, 0), "Sneak past the two stationary guards and climb the ladder to the second floor of the north-western house in Port Piscarilius.");
		sneakPastGuardsToNorthWesternHouse.addAlternateNpcs(NpcID.WOTW_SOLDIER_CUTSCENE_1, NpcID.WOTW_SOLDIER_CUTSCENE_2);
		sneakPastGuardsToNorthWesternHouse.setAllowMultipleHighlights(true);
		sneakPastGuardsToNorthWesternHouse.addCustomIcon(new QuestStepIcon(ItemID.BANK_FILLER, NpcID.WOTW_SOLDIER_CUTSCENE_1, 1.0));
		sneakPastGuardsToNorthWesternHouse.addCustomIcon(new QuestStepIcon(ItemID.BANK_FILLER, NpcID.WOTW_SOLDIER_CUTSCENE_2, 1.0));

		inspectBodyUpstairsInPiscHouse = new ObjectStep(this, ObjectID.WOTW_ARMOUR_STAND_PISCARILIUS_BODY, new WorldPoint(1754, 3779, 1), "Inspect the body in the middle of the room.");


		cInvestigatePisc = new ConditionalStep(this, escapePrisonCell);

		cInvestigatePisc.addStep(inPiscHouseFirstFloor, inspectBodyUpstairsInPiscHouse);
		cInvestigatePisc.addStep(not(inPrisonCell), sneakPastGuardsToNorthWesternHouse);


		// 28 -> 30 when inspecting body the first time

		inspectPiscHead = new ObjectStep(this, ObjectID.WOTW_ARMOUR_STAND_PISCARILIUS_BODY, new WorldPoint(1754, 3779, 1), "Inspect the body in the middle of the room.");
		inspectPiscHead.addDialogStep("Inspect the head.");

		inspectPiscTorso = new ObjectStep(this, ObjectID.WOTW_ARMOUR_STAND_PISCARILIUS_BODY, new WorldPoint(1754, 3779, 1), "Inspect the body in the middle of the room.");
		inspectPiscTorso.addDialogStep("Inspect the torso.");

		inspectPiscLegs = new ObjectStep(this, ObjectID.WOTW_ARMOUR_STAND_PISCARILIUS_BODY, new WorldPoint(1754, 3779, 1), "Inspect the body in the middle of the room.");
		inspectPiscLegs.addDialogStep("Inspect the legs.");

		inspectBodyUpstairsInPiscHouse.addSubSteps(inspectPiscHead, inspectPiscTorso, inspectPiscLegs);

		inspectPiscDrawers = new ObjectStep(this, ObjectID.WOTW_DRAWERS_PISCARILIUS_BLOODY, new WorldPoint(1756, 3782, 1), "Inspect the bloody drawers in the corner of the room.");
		inspectPiscDrawers.addDialogStep("Inspect the blood.");

		inspectPiscDrawersForDagger = new ObjectStep(this, ObjectID.WOTW_DRAWERS_PISCARILIUS_BLOODY, new WorldPoint(1756, 3782, 1), "Inspect the bloody drawers in the corner of the room.");
		inspectPiscDrawersForDagger.addDialogStep("Search the drawers.");

		inspectPiscDrawers.addSubSteps(inspectPiscDrawersForDagger);

		piscClimbToSecondFloor = new ObjectStep(this, ObjectID.PISCARILIUS_LADDER_BOTTOM, "Climb to the second floor.");

		inspectPiscBed = new ObjectStep(this, ObjectID.WOTW_BED_PISCARILIUS, new WorldPoint(1754, 3775, 2), "Inspect the bed.");

		inspectPiscWardrobe = new ObjectStep(this, ObjectID.WOTW_WARDROBE_PISCARILIUS, new WorldPoint(1757, 3778, 2), "Inspect the wardrobe.");

		cInvestigatePisc2 = new ConditionalStep(this, unreachableStep);

		cInvestigatePisc2.addStep(inPrisonCell, escapePrisonCell);

		cInvestigatePisc2.addStep(and(inPiscHouseFirstFloor, needToInspectPiscHead), inspectPiscHead);
		cInvestigatePisc2.addStep(and(needToInspectPiscHead), sneakPastGuardsToNorthWesternHouse);

		cInvestigatePisc2.addStep(and(inPiscHouseFirstFloor, needToInspectPiscHead), inspectPiscHead);
		cInvestigatePisc2.addStep(and(needToInspectPiscHead), sneakPastGuardsToNorthWesternHouse);

		cInvestigatePisc2.addStep(and(inPiscHouseFirstFloor, needToInspectPiscTorso), inspectPiscTorso);
		cInvestigatePisc2.addStep(and(needToInspectPiscTorso), sneakPastGuardsToNorthWesternHouse);

		cInvestigatePisc2.addStep(and(inPiscHouseFirstFloor, needToInspectPiscLegs), inspectPiscLegs);
		cInvestigatePisc2.addStep(and(needToInspectPiscLegs), sneakPastGuardsToNorthWesternHouse);

		cInvestigatePisc2.addStep(and(inPiscHouseFirstFloor, needToInspectPiscDrawers), inspectPiscDrawers);
		cInvestigatePisc2.addStep(and(needToInspectPiscDrawers), sneakPastGuardsToNorthWesternHouse);

		cInvestigatePisc2.addStep(and(inPiscHouseFirstFloor, needToInspectPiscDrawersForDagger), inspectPiscDrawersForDagger);
		cInvestigatePisc2.addStep(and(needToInspectPiscDrawersForDagger), sneakPastGuardsToNorthWesternHouse);

		cInvestigatePisc2.addStep(and(inPiscHouseSecondFloor, needToInspectPiscBed), inspectPiscBed);
		cInvestigatePisc2.addStep(and(inPiscHouseFirstFloor, needToInspectPiscBed), piscClimbToSecondFloor);
		cInvestigatePisc2.addStep(and(needToInspectPiscBed), sneakPastGuardsToNorthWesternHouse);

		cInvestigatePisc2.addStep(and(inPiscHouseSecondFloor, needToInspectPiscWardrobe), inspectPiscWardrobe);
		cInvestigatePisc2.addStep(and(inPiscHouseFirstFloor, needToInspectPiscWardrobe), piscClimbToSecondFloor);
		cInvestigatePisc2.addStep(and(needToInspectPiscWardrobe), sneakPastGuardsToNorthWesternHouse);


		piscClimbDownFromSecondFloor = new ObjectStep(this, ObjectID.PISCARILIUS_LADDER_TOP_01, new WorldPoint(1757, 3780, 2), "Leave the house.");
		piscClimbDownFromFirstFloor = new ObjectStep(this, ObjectID.PISCARILIUS_LADDER_TOP_02, new WorldPoint(1754, 3782, 1), "Leave the house.");
		piscClimbDownFromSecondFloor.addSubSteps(piscClimbDownFromFirstFloor);
		piscGetCaught = new TileStep(this, new WorldPoint(1751, 3781, 0), "Get caught by one of the guards.");
		inspectPiscBlood = new ObjectStep(this, ObjectID.WOTW_BLOOD_PISCARILIUS_INSPECT, "Inspect the blood on the eastern wall.");
		cSpeakToOneOfTheGuardsDownstairsInPisc = new ConditionalStep(this, piscGetCaught);
		cSpeakToOneOfTheGuardsDownstairsInPisc.addStep(inPrisonCell, inspectPiscBlood);
		cSpeakToOneOfTheGuardsDownstairsInPisc.addStep(inPiscHouseSecondFloor, piscClimbDownFromSecondFloor);
		cSpeakToOneOfTheGuardsDownstairsInPisc.addStep(inPiscHouseFirstFloor, piscClimbDownFromFirstFloor);

		// 32 -> 34: Time to head to Shayzien! Betty was just captured :O
		escapePrisonCell2 = new ObjectStep(this, ObjectID.WOTW_PISCARILIUS_CELL_WALL_UNBLOCKED, new WorldPoint(1787, 3782, 0), "Escape the prison cell through the northern wall.");
		shayzienEnterWarTent = new DetailedQuestStep(this, new WorldPoint(1485, 3635, 0), "Enter the War Tent in the Shayzien Encampment.");
		escapePrisonCell.addDialogStep("Climb through the gap.");
		cShayzienEnterWarTent = new ConditionalStep(this, shayzienEnterWarTent);
		cShayzienEnterWarTent.addStep(inPrisonCell, escapePrisonCell2);

		// 36: time to investigate shayzien murder
		// 38: inspected body the first time
		inspectShayzienHead = new ObjectStep(this, ObjectID.WOTW_BED_SHAYZIEN_BODY, new WorldPoint(1452, 3642, 0), "Inspect the body in the Shayzien Encampment.");
		inspectShayzienHead.addDialogStep("Inspect the head.");

		inspectShayzienArms = new ObjectStep(this, ObjectID.WOTW_BED_SHAYZIEN_BODY, new WorldPoint(1452, 3642, 0), "Inspect the body in the Shayzien Encampment.");
		inspectShayzienArms.addDialogStep("Inspect the arms.");

		inspectShayzienHead.addSubSteps(inspectShayzienArms);

		inspectShayzienWall = new ObjectStep(this, ObjectID.WOTW_BLOOD_SHAYZIEN_INSPECT, new WorldPoint(1452, 3644, 0), "Inspect the bloody wall in the Shayzien Encampment.");

		inspectShayzienShelves = new ObjectStep(this, ObjectID.WOTW_DRINK_SHELVES_SHAYZIEN, new WorldPoint(1450, 3644, 0), "Inspect the shelves in the Shayzien Encampment.");

		interrogateKyle = new NpcStep(this, NpcID.WOTW_FORESTER, new WorldPoint(1453, 3630, 0), "Talk to Kyle outside the building with the body in the Shayzien Encampment.");
		interrogateKyle.addDialogStep("Did you see or hear anything around the time of the murder?");

		talkToSergeantHudsonInShayzien = new NpcStep(this, NpcID.WOTW_HUDSON_CHILD, new WorldPoint(1449, 3638, 0), "Talk to Sergeant Hudson outside the building with the body in the Shayzien Encampment.");
		talkToSergeantHudsonInShayzien.addDialogStep("What can you tell me about the victim?");

		talkToSergeantHudsonInShayzien2 = new NpcStep(this, NpcID.WOTW_HUDSON_CHILD, new WorldPoint(1449, 3638, 0), "Talk to Sergeant Hudson outside the building with the body in the Shayzien Encampment.");
		talkToSergeantHudsonInShayzien2.addDialogStep("I think I might have worked it out!");

		talkToSergeantHudsonInShayzien.addSubSteps(talkToSergeantHudsonInShayzien2);

		cShayzienMurder = new ConditionalStep(this, unreachableStep);

		cShayzienMurder.addStep(shayzienLearnedEverything, talkToSergeantHudsonInShayzien2);
		cShayzienMurder.addStep(needToInspectShayzienHead, inspectShayzienHead);
		cShayzienMurder.addStep(needToInspectShayzienArms, inspectShayzienArms);
		cShayzienMurder.addStep(needToInspectShayzienWall, inspectShayzienWall);
		cShayzienMurder.addStep(needToInspectShayzienShelves, inspectShayzienShelves);
		cShayzienMurder.addStep(needToInterrogateKyle, interrogateKyle);
		cShayzienMurder.addStep(needToTalkToSergeantHudsonInShayzien, talkToSergeantHudsonInShayzien);


		// 40: "solved" shayzien clues, time to find the killer
		enterSecretTrapdoor = new ObjectStep(this, ObjectID.WOTW_HIDEOUT_TRAPDOOR_BUSH, new WorldPoint(1615, 3582, 0), "Inspect the plant on the island west of Watson's house.");
		enterSecretTrapdoor.addDialogStep("Go down it.");

		inspectSecretHideoutBlood = new ObjectStep(this, ObjectID.WOTW_BLOOD_HIDEOUT_INSPECT, new WorldPoint(1625, 9942, 0), "Inspect the blood on the wall inside the secret hideout.");
		closeSecretHideoutBlood = new WidgetStep(this, "Close out of the spooky text!", InterfaceID.WotwWallHideout.CLOSE);
		inspectSecretHideoutBlood.addSubSteps(closeSecretHideoutBlood);

		talkToBettyTheMurderer = new NpcStep(this, NpcID.WOTW_BETTY_CUTSCENE_REVEALED, "Talk to the hooded figure.");
		talkToBettyTheMurderer.addDialogStep("What is this?");
		talkToBettyTheMurderer.addDialogStep("It was you!");
		talkToBettyTheMurderer.addDialogStep("Just as I suspected!");

		cEnterSecretTrapdoor = new ConditionalStep(this, enterSecretTrapdoor);
		cEnterSecretTrapdoor.addStep(isInspectingSecretHideoutBlood, closeSecretHideoutBlood);
		cEnterSecretTrapdoor.addStep(and(inSecretHideout, inCutscene), talkToBettyTheMurderer);
		cEnterSecretTrapdoor.addStep(inSecretHideout, inspectSecretHideoutBlood);


		// 44 finish the quest
		finishQuest = new NpcStep(this, NpcID.WOTW_HUDSON_CHILD, new WorldPoint(1617, 3581, 0), "Talk to Sergeant Hudson on the island west of Watson's house to finish the quest.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, startQuest);

		// talk to Sergeant Hudson
		steps.put(2, talkToSergeantHudson);

		steps.put(4, cInvestigate);
		steps.put(6, cInvestigate);

		/// 8: You have given Hudson the information, and there's a cutscene!
		steps.put(8, returnToSergeantHudsonForCutscene);

		/// 10: Cutscene is finished, we are talking to Betty
		steps.put(10, talkToBettyAfterSergeantHudsonLeft);

		/// 12: Agreed to meet Betty at her uncle's house, north-west of the Tithe Farm
		steps.put(12, talkToBettyAtUnclesHouse);

		/// 14: Curiously, the uncle isn't home :O. Let's break into the house and see if he's there.
		steps.put(14, cBreakIntoUnclesHouse);

		/// 16: Betty walked down by herself and found her Uncle dead.
		steps.put(16, cTalkToBettyInUnclesBasement);

		/// 18: Before talking to the authorities, I will just look through the crime scene. Clueless
		steps.put(18, cInvestigateUnclesBasement);

		steps.put(20, cHeadToPiscariliusWithBetty);

		// 22: headed to Port Piscarilius with Betty
		steps.put(22, introduceYourselfToPiscCaptain);

		// SECTION: PRISON BREAK

		// 24: \o/ CAUGHT by Captain Hudson in Port Piscarilius
		steps.put(24, talkToBettyInPrisonCell);

		// 26: In the prison cell, talked to Betty about an escape plan
		// TODO: what happens if you just teleport out lol
		steps.put(26, removePlankFromWall);

		// 28: Escaped the prison
		steps.put(28, cInvestigatePisc);

		// 28 -> 30 when inspecting body the first time
		steps.put(30, cInvestigatePisc2);

		steps.put(32, cSpeakToOneOfTheGuardsDownstairsInPisc);

		// 32 -> 34: Time to head to Shayzien! Betty was just captured :O
		steps.put(34, cShayzienEnterWarTent);

		// 36: time to investigate shayzien murder
		// 38: inspected body the first time
		steps.put(36, cShayzienMurder);
		steps.put(38, cShayzienMurder);

		// 40: "solved" shayzien clues, time to find the killer

		steps.put(40, cEnterSecretTrapdoor);
		steps.put(42, cEnterSecretTrapdoor);

		// 44 finish the quest
		steps.put(44, finishQuest);
		steps.put(46, finishQuest);

		return steps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new SkillRequirement(Skill.AGILITY, 22, false),
			new SkillRequirement(Skill.THIEVING, 20, false)
		);
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		return List.of(
			freeSlots
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			energyRestorePotion
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.AGILITY, 2500),
			new ExperienceReward(Skill.THIEVING, 2500)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of();
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to something grand")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			startQuest
		), List.of(
		), List.of(
			freeSlots
		)));

		sections.add(new PanelDetails("Jack Holland", List.of(
			talkToSergeantHudson,
			searchShelves,
			readNews,
			inspectBodyWall,
			readLetter,
			inspectBloodOnWall,
			questionButcher,
			searchButcherBed,
			questionHugor,
			getAlibiFromBetty,
			getHistoryFromBetty,
			returnToSergeantHudson,
			talkToBettyAfterSergeantHudsonLeft
		), List.of(
		), List.of(
			freeSlots
		)));

		sections.add(new PanelDetails("Bernard Holland", List.of(
			talkToBettyAtUnclesHouse,
			enterUnclesHouseThroughWindow,
			openDoorForBetty,
			climbDownLadder,
			talkToBettyInUnclesBasement,
			investigateHead,
			investigateShelves,
			askAboutUnclesDrinkingHabits,
			askAboutUncle,
			goUpstairs,
			getCertificate,
			readCertificate,
			searchCompost,
			inspectHead,
			inspectLetterFromHead,
			speakToFarmer,
			speakToMonk,
			climbDownLadder2,
			talkToBettyAboutKeystoneLodge,
			talkToBettyInUnclesBasementAboutEverythingYouHaveFound,
			headToPiscariliusWithBetty
		), List.of(
		)));

		sections.add(new PanelDetails("Captain Jensen", List.of(
			introduceYourselfToPiscCaptain,
			talkToBettyInPrisonCell,
			removePlankFromWall,
			escapePrisonCell,
			sneakPastGuardsToNorthWesternHouse,
			inspectBodyUpstairsInPiscHouse,
			inspectPiscDrawers,
			piscClimbToSecondFloor,
			inspectPiscBed,
			inspectPiscWardrobe,
			piscClimbDownFromSecondFloor,
			piscGetCaught,
			inspectPiscBlood,
			escapePrisonCell2
		), List.of(
		)));

		sections.add(new PanelDetails("Commissioner Edwards", List.of(
			shayzienEnterWarTent,
			inspectShayzienHead,
			inspectShayzienWall,
			inspectShayzienShelves,
			interrogateKyle,
			talkToSergeantHudsonInShayzien
		), List.of(
		)));

		sections.add(new PanelDetails("Confronting the hooded figure", List.of(
			enterSecretTrapdoor,
			inspectSecretHideoutBlood,
			talkToBettyTheMurderer,
			finishQuest
		), List.of(
		)));

		return sections;
	}
}
