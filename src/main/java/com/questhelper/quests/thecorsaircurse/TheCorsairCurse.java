/*
 * Copyright (c) 2020, Zoinkwiz
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
package com.questhelper.quests.thecorsaircurse;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.Operation;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.DigStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;

import java.util.*;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_CORSAIR_CURSE
)
public class TheCorsairCurse extends BasicQuestHelper
{
	//Items Required
	ItemRequirement spade, tinderbox, ogreArtfact, combatGear;

	Requirement inCove, inCavern, inIthoiHut, inGnocciHut, inArsenHut, inShip, talkedToIthoi, talkedToGnocci, talkedToArsen, talkedToColin,
		foundDoll, returnedToothPick, lookedThroughTelescope, finishedGnocci, finishedArsen, finishedColin;

	QuestStep talkToTockFarm, talkToTockRimmington, returnToCove, goUpToIthoi, talkToIthoi, goDownFromIthoi, goUpToArsen, talkToArsen,
		goUpToColin, talkToColin, goDownFromArsen, goUpToGnocci, talkToGnocci, grabTinderbox, goDownFromGnocci, pickUpSpade, goOntoShip,
		talkToTockShip, leaveShip, goDownToTess, talkToTess, goUpFromTess, goUpToIthoi2, lookThroughTelescope, goDownFromIthoi2, digSand,
		goUpToGnocci2, talkToGnocci2, goDownFromGnocci2, goUpToArsen2, goUpToColin2, talkToArsen2, talkToColin2, talkToTockShip2, goOntoShip2, leaveShip2,
		goUpToGnocci3, talkToGnocci3, goDownFromGnocci3, goUpToArsen3, talkToArsen3, goDownFromArsen3, goUpToIthoi3, talkToIthoi2, goDownFromIthoi3,
		goOntoShip3, talkToTockShip3, useTinderboxOnWood, goUpToIthoiToKill, killIthoi, goOntoShip4, talkToTockShip4;

	//Zones
	Zone cove, cavern, ithoiHut, gnocciHut, arsenHut, ship;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToTockFarm);
		steps.put(5, talkToTockFarm);

		steps.put(10, talkToTockRimmington);

		ConditionalStep solveCurses = new ConditionalStep(this, goUpToIthoi);

		solveCurses.addStep(new Conditions(talkedToIthoi, finishedArsen, lookedThroughTelescope, finishedGnocci, inArsenHut), talkToColin2);
		solveCurses.addStep(new Conditions(talkedToIthoi, finishedArsen, lookedThroughTelescope, finishedGnocci), goUpToColin2);

		solveCurses.addStep(new Conditions(talkedToIthoi, returnedToothPick, lookedThroughTelescope, finishedGnocci, inArsenHut), talkToArsen2);
		solveCurses.addStep(new Conditions(talkedToIthoi, returnedToothPick, lookedThroughTelescope, finishedGnocci, inGnocciHut), goDownFromGnocci2);
		solveCurses.addStep(new Conditions(talkedToIthoi, returnedToothPick, lookedThroughTelescope, finishedGnocci), goUpToArsen2);

		solveCurses.addStep(new Conditions(talkedToIthoi, returnedToothPick, lookedThroughTelescope, foundDoll, inGnocciHut), talkToGnocci2);
		solveCurses.addStep(new Conditions(talkedToIthoi, returnedToothPick, lookedThroughTelescope, foundDoll), goUpToGnocci2);

		solveCurses.addStep(new Conditions(talkedToIthoi, returnedToothPick, lookedThroughTelescope, foundDoll, inIthoiHut), goDownFromIthoi2);
		solveCurses.addStep(new Conditions(talkedToIthoi, returnedToothPick, talkedToColin, foundDoll, inIthoiHut), lookThroughTelescope);
		solveCurses.addStep(new Conditions(talkedToIthoi, returnedToothPick, talkedToColin, foundDoll), goUpToIthoi2);

		solveCurses.addStep(new Conditions(talkedToIthoi, returnedToothPick, talkedToColin, spade), digSand);

		solveCurses.addStep(new Conditions(talkedToIthoi, talkedToArsen, talkedToColin, talkedToGnocci, inCavern, ogreArtfact), talkToTess);
		solveCurses.addStep(new Conditions(talkedToIthoi, talkedToArsen, talkedToColin, talkedToGnocci, inShip, ogreArtfact), leaveShip);
		solveCurses.addStep(new Conditions(talkedToIthoi, talkedToArsen, talkedToColin, talkedToGnocci, ogreArtfact), goDownToTess);

		solveCurses.addStep(new Conditions(talkedToIthoi, talkedToArsen, talkedToColin, talkedToGnocci, spade, inShip),
			talkToTockShip);
		solveCurses.addStep(new Conditions(talkedToIthoi, talkedToArsen, talkedToColin, talkedToGnocci, spade), goOntoShip);
		solveCurses.addStep(new Conditions(talkedToIthoi, talkedToArsen, talkedToColin, talkedToGnocci, inGnocciHut, tinderbox), pickUpSpade);
		solveCurses.addStep(new Conditions(talkedToIthoi, talkedToArsen, talkedToColin, talkedToGnocci, inGnocciHut), grabTinderbox);
		solveCurses.addStep(new Conditions(talkedToIthoi, talkedToArsen, talkedToColin, talkedToGnocci), pickUpSpade);

		solveCurses.addStep(new Conditions(talkedToIthoi, talkedToArsen, talkedToColin, inGnocciHut), talkToGnocci);
		solveCurses.addStep(new Conditions(talkedToIthoi, talkedToArsen, talkedToColin, inArsenHut), goDownFromArsen);
		solveCurses.addStep(new Conditions(talkedToIthoi, talkedToArsen, talkedToColin), goUpToGnocci);
		solveCurses.addStep(new Conditions(talkedToIthoi, talkedToArsen, inArsenHut), talkToColin);
		solveCurses.addStep(new Conditions(talkedToIthoi, talkedToArsen), goUpToColin);
		solveCurses.addStep(new Conditions(talkedToIthoi, inArsenHut), talkToArsen);
		solveCurses.addStep(new Conditions(talkedToIthoi, inIthoiHut), goDownFromIthoi);

		solveCurses.addStep(talkedToIthoi, goUpToArsen);

		solveCurses.addStep(inIthoiHut, talkToIthoi);
		solveCurses.addStep(inCavern, goUpFromTess);
		steps.put(15, solveCurses);

		ConditionalStep returnToTock = new ConditionalStep(this, goOntoShip2);
		returnToTock.addStep(inShip, talkToTockShip2);
		steps.put(20, returnToTock);

		ConditionalStep solveFoodMystery = new ConditionalStep(this, goUpToGnocci3);
		solveFoodMystery.addStep(inGnocciHut, talkToGnocci3);
		solveFoodMystery.addStep(inShip, leaveShip2);
		steps.put(25, solveFoodMystery);

		ConditionalStep solveFoodWithArsen = new ConditionalStep(this, grabTinderbox);
		solveFoodWithArsen.addStep(inArsenHut, talkToArsen3);
		solveFoodWithArsen.addStep(new Conditions(tinderbox, inGnocciHut), goDownFromGnocci3);
		solveFoodWithArsen.addStep(tinderbox, goUpToArsen3);

		steps.put(30, solveFoodWithArsen);

		ConditionalStep solveFoodWithIthoi = new ConditionalStep(this, grabTinderbox);
		solveFoodWithIthoi.addStep(inArsenHut, goDownFromArsen3);
		solveFoodWithIthoi.addStep(new Conditions(tinderbox, inIthoiHut), talkToIthoi2);
		solveFoodWithIthoi.addStep(tinderbox, goUpToIthoi3);

		steps.put(35, solveFoodWithIthoi);
		steps.put(40, solveFoodWithIthoi);

		ConditionalStep burnIthoi = new ConditionalStep(this, useTinderboxOnWood);
		burnIthoi.addStep(inIthoiHut, goDownFromIthoi3);

		steps.put(45, burnIthoi);

		steps.put(49, new DetailedQuestStep(this, "Watch the cutscene."));

		ConditionalStep returnToTockWithAnswers = new ConditionalStep(this, goOntoShip3);
		returnToTockWithAnswers.addStep(inShip, talkToTockShip3);

		steps.put(50, returnToTockWithAnswers);

		ConditionalStep goAndKillIthoi = new ConditionalStep(this, goUpToIthoiToKill);
		goAndKillIthoi.addStep(inIthoiHut, killIthoi);

		steps.put(52, goAndKillIthoi);

		ConditionalStep finishQuest = new ConditionalStep(this, goOntoShip4);
		finishQuest.addStep(inShip, talkToTockShip4);

		steps.put(55, finishQuest);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		combatGear = new ItemRequirement("Combat gear + food to defeat Ithoi (level 34), who uses magic", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		ogreArtfact = new ItemRequirement("Ogre artefact", ItemID.OGRE_ARTEFACT_21837);
	}

	public void loadZones()
	{
		cove = new Zone(new WorldPoint(2308, 2806, 0), new WorldPoint(2705, 3136, 2));
		ithoiHut = new Zone(new WorldPoint(2527, 2835, 1), new WorldPoint(2532, 2841, 1));
		gnocciHut = new Zone(new WorldPoint(2543, 2860, 1), new WorldPoint(2547, 2864, 1));
		arsenHut = new Zone(new WorldPoint(2553, 2853, 1), new WorldPoint(2559, 2859, 1));
		ship = new Zone(new WorldPoint(2573, 2835, 1), new WorldPoint(2583, 2837, 1));
		cavern = new Zone(new WorldPoint(1876, 8960, 1), new WorldPoint(2073, 9093, 1));
	}

	public void setupConditions()
	{
		inCove = new ZoneRequirement(cove);
		inIthoiHut = new ZoneRequirement(ithoiHut);
		inGnocciHut = new ZoneRequirement(gnocciHut);
		inArsenHut = new ZoneRequirement(arsenHut);
		inShip = new ZoneRequirement(ship);
		inCavern = new ZoneRequirement(cavern);
		talkedToIthoi = new VarbitRequirement(6075, 1);

		talkedToArsen = new VarbitRequirement(6074, 2, Operation.GREATER_EQUAL);
		returnedToothPick = new VarbitRequirement(6074, 4);
		finishedArsen = new VarbitRequirement(6074, 6, Operation.GREATER_EQUAL);

		talkedToColin = new VarbitRequirement(6072, 1, Operation.GREATER_EQUAL);
		lookedThroughTelescope = new VarbitRequirement(6072, 2);
		finishedColin = new VarbitRequirement(6072, 3);

		talkedToGnocci = new VarbitRequirement(6073, 1);
		foundDoll = new VarbitRequirement(6073, 2);
		finishedGnocci = new VarbitRequirement(6073, 3);
	}

	public void setupSteps()
	{
		talkToTockFarm = new NpcStep(this, NpcID.CAPTAIN_TOCK, new WorldPoint(3030, 3273, 0), "Talk to Captain Tock north of Port Sarim.");
		talkToTockFarm.addDialogStep("What kind of help do you need?");
		talkToTockFarm.addDialogStep("Sure, I'll try to help with your curse.");

		talkToTockRimmington = new NpcStep(this, NpcID.CAPTAIN_TOCK, new WorldPoint(2910, 3226, 0), "Talk to Captain Tock west of Rimmington.");
		talkToTockRimmington.addDialogStep("Okay, I'm ready go to Corsair Cove.");

		returnToCove = new NpcStep(this, NpcID.CAPTAIN_TOCK_7958, new WorldPoint(2910, 3226, 0), "Return to Corsair Cove.");
		returnToCove.addDialogStep("Let's go.");

		goUpToIthoi = new ObjectStep(this, ObjectID.STAIRS_31735, new WorldPoint(2531, 2833, 0), "Talk to Ithoi in the south western hut.");
		talkToIthoi = new NpcStep(this, NpcID.ITHOI_THE_NAVIGATOR, new WorldPoint(2529, 2840, 1), "Talk to Ithoi in the south western hut.");
		talkToIthoi.addDialogStep("I hear you've been cursed.");
		talkToIthoi.addSubSteps(goUpToIthoi);

		goDownFromIthoi = new ObjectStep(this, ObjectID.STAIRS_31735, new WorldPoint(2529, 2834, 1), "Talk to Arsen in the central hut.");
		goUpToGnocci = new ObjectStep(this, ObjectID.STAIRS_31733, new WorldPoint(2549, 2862, 0), "Talk to Gnocci in the north west hut.");
		talkToGnocci = new NpcStep(this, NpcID.GNOCCI_THE_COOK, new WorldPoint(2545, 2863, 1), "Talk to Gnocci in the north west hut.");
		talkToGnocci.addDialogStep("I hear you've been cursed.");
		talkToGnocci.addSubSteps(goUpToGnocci, goDownFromIthoi);

		goDownFromGnocci = new ObjectStep(this, ObjectID.STAIRS_31734, new WorldPoint(2548, 2862, 1), "Talk to Chief Tess down the hole west of the Cove.");

		goUpToArsen = new ObjectStep(this, ObjectID.STAIRS_31733, new WorldPoint(2555, 2856, 0), "Talk to Arsen in the central hut.");
		talkToArsen = new NpcStep(this, NpcID.ARSEN_THE_THIEF, new WorldPoint(2554, 2859, 1), "Talk to Arsen in the central hut.");
		talkToArsen.addDialogStep("I hear you've been cursed.");
		talkToArsen.addSubSteps(goDownFromIthoi, goUpToArsen);

		goUpToColin = new ObjectStep(this, ObjectID.STAIRS_31733, new WorldPoint(2555, 2856, 0), "Talk to Colin in the central hut.");
		talkToColin = new NpcStep(this, NpcID.CABIN_BOY_COLIN, new WorldPoint(2558, 2858, 1), "Talk to Colin in the central hut.");
		talkToColin.addDialogStep("I hear you've been cursed.");
		talkToColin.addSubSteps(goUpToColin);

		goDownFromArsen = new ObjectStep(this, ObjectID.STAIRS_31734, new WorldPoint(2555, 2855, 1), "Talk to Gnocci in the north west hut.");
		grabTinderbox = new ObjectStep(this, ObjectID.TINDERBOX, new WorldPoint(2543, 2862, 1), "Pick up the tinderbox next to Gnocci.", tinderbox);
		pickUpSpade = new ObjectStep(this, ObjectID.SPADE_31585, new WorldPoint(2552, 2846, 0), "Take the spade in the south of the Corsair Cove.");

		talkToTockShip = new NpcStep(this, NpcID.CAPTAIN_TOCK_7958, new WorldPoint(2574, 2835, 1), "Talk to Captain Tock on the ship.");
		talkToTockShip.addDialogStep("Arsen says he gave you a sacred ogre relic.");
		talkToTockShip.addDialogStep("About that sacred ogre relic...");
		goOntoShip = new ObjectStep(this, ObjectID.GANGPLANK_31756, new WorldPoint(2578, 2839, 0),
			"Talk to Captain Tock on the ship.");

		leaveShip = new ObjectStep(this, ObjectID.GANGPLANK_31756, new WorldPoint(2578, 2838, 1), "Enter the hole west of the Corsair Cove and talk to Chief Tess.");
		goDownToTess = new ObjectStep(this, ObjectID.HOLE_31791, new WorldPoint(2523, 2861, 0), "Enter the hole west of the Corsair Cove and talk to Chief Tess.");
		talkToTess = new NpcStep(this, NpcID.CHIEF_TESS, new WorldPoint(2012, 9006, 1), "Talk to Chief Tess.");
		talkToTess.addSubSteps(goDownToTess, leaveShip);
		talkToTess.addDialogStep("I've come to return what Arsen stole.");

		goUpFromTess = new ObjectStep(this, ObjectID.VINE_LADDER_31790, new WorldPoint(2012, 9005, 1), "Leave the cavern.");
		digSand = new DigStep(this, new WorldPoint(2504, 2840, 0), "Dig next to the tree south west of the Cove.");
		digSand.addDialogStep("Search for the possessed doll and face the consequences.");
		digSand.addSubSteps(goUpFromTess);

		goUpToIthoi2 = new ObjectStep(this, ObjectID.STAIRS_31735, new WorldPoint(2531, 2833, 0), "Go look through Ithoi's telescope.");
		lookThroughTelescope = new ObjectStep(this, ObjectID.TELESCOPE_31632, new WorldPoint(2528, 2835, 1), "Go look through Ithoi's telescope.");
		lookThroughTelescope.addSubSteps(goUpToIthoi2);

		goDownFromIthoi2 = new ObjectStep(this, ObjectID.STAIRS_31735, new WorldPoint(2529, 2834, 1), "Leave Ithoi's hut.");
		goUpToGnocci2 = new ObjectStep(this, ObjectID.STAIRS_31733, new WorldPoint(2549, 2862, 0), "Talk to Gnocci in the north west hut.");
		talkToGnocci2 = new NpcStep(this, NpcID.GNOCCI_THE_COOK, new WorldPoint(2545, 2863, 1), "Talk to Gnocci in the north west hut.");
		talkToGnocci2.addSubSteps(goUpToGnocci2, goDownFromIthoi2);

		goDownFromGnocci2 = new ObjectStep(this, ObjectID.STAIRS_31734, new WorldPoint(2548, 2862, 1), "Talk to Arsen.");
		goUpToArsen2 = new ObjectStep(this, ObjectID.STAIRS_31733, new WorldPoint(2555, 2856, 0), "Talk to Arsen in the central hut.");
		talkToArsen2 = new NpcStep(this, NpcID.ARSEN_THE_THIEF, new WorldPoint(2554, 2859, 1), "Talk to Arsen in the central hut.");
		talkToArsen2.addSubSteps(goUpToArsen2, goDownFromGnocci2);

		goUpToColin2 = new ObjectStep(this, ObjectID.STAIRS_31733, new WorldPoint(2555, 2856, 0), "Talk to Colin in the central hut.");
		talkToColin2 = new NpcStep(this, NpcID.CABIN_BOY_COLIN, new WorldPoint(2558, 2858, 1), "Talk to Colin in the central hut.");
		talkToColin2.addSubSteps(goUpToColin);

		goOntoShip2 = new ObjectStep(this, ObjectID.GANGPLANK_31756, new WorldPoint(2578, 2839, 0), "Talk to Captain Tock on the ship.");
		talkToTockShip2 = new NpcStep(this, NpcID.CAPTAIN_TOCK_7958, new WorldPoint(2574, 2835, 1), "Talk to Captain Tock on the ship.");
		talkToTockShip2.addDialogStep("I've ruled out all the Corsairs' theories...");
		talkToTockShip2.addDialogStep("So what do I do now?");
		talkToTockShip2.addSubSteps(goOntoShip2);

		leaveShip2 = new ObjectStep(this, ObjectID.GANGPLANK_31756, new WorldPoint(2578, 2838, 1), "Talk to Gnocci in the north west hut.");
		goUpToGnocci3 = new ObjectStep(this, ObjectID.STAIRS_31733, new WorldPoint(2549, 2862, 0), "Talk to Gnocci in the north west hut.");
		talkToGnocci3 = new NpcStep(this, NpcID.GNOCCI_THE_COOK, new WorldPoint(2545, 2863, 1), "Talk to Gnocci in the north west hut.");
		talkToGnocci3.addDialogStep("I hear it happened straight after dinner.");
		talkToGnocci3.addSubSteps(goUpToGnocci3, leaveShip2);

		goDownFromGnocci3 = new ObjectStep(this, ObjectID.STAIRS_31734, new WorldPoint(2548, 2862, 1), "Talk to Arsen.");
		goUpToArsen3 = new ObjectStep(this, ObjectID.STAIRS_31733, new WorldPoint(2555, 2856, 0), "Talk to Arsen in the central hut.");
		talkToArsen3 = new NpcStep(this, NpcID.ARSEN_THE_THIEF, new WorldPoint(2554, 2859, 1), "Talk to Arsen in the central hut.");
		talkToArsen3.addDialogStep("I hear Ithoi cooked the meal you ate that night.");
		talkToArsen3.addSubSteps(goUpToArsen3, goDownFromGnocci3);

		goDownFromArsen3 = new ObjectStep(this, ObjectID.STAIRS_31734, new WorldPoint(2555, 2855, 1), "Talk to Ithoi in the south western hut.");
		goUpToIthoi3 = new ObjectStep(this, ObjectID.STAIRS_31735, new WorldPoint(2531, 2833, 0), "Talk to Ithoi in the south western hut.");
		talkToIthoi2 = new NpcStep(this, NpcID.ITHOI_THE_NAVIGATOR, new WorldPoint(2529, 2840, 1), "Talk to Ithoi.");
		talkToIthoi2.addSubSteps(goUpToIthoi3, goDownFromArsen3);
		talkToIthoi2.addDialogStep("I bet I can prove you're well enough to get up.");
		talkToIthoi2.addDialogStep("I know you've faked the curse.");
		talkToIthoi2.addDialogStep("I hear you cooked the meal they ate before getting sick.");
		talkToIthoi2.addDialogStep("Maybe because the Captain's thinking of firing you.");

		goDownFromIthoi3 = new ObjectStep(this, ObjectID.STAIRS_31735, new WorldPoint(2529, 2834, 1), "Use your tinderbox on the driftwood under Ithoi's hut.");
		useTinderboxOnWood = new ObjectStep(this, NullObjectID.NULL_31724, new WorldPoint(2531, 2838, 0), "Use your tinderbox on the driftwood under Ithoi's hut.", tinderbox);
		useTinderboxOnWood.addIcon(ItemID.TINDERBOX);
		useTinderboxOnWood.addSubSteps(goDownFromIthoi3);

		goOntoShip3 = new ObjectStep(this, ObjectID.GANGPLANK_31756, new WorldPoint(2578, 2839, 0), "Talk to Captain Tock on the ship.");
		talkToTockShip3 = new NpcStep(this, NpcID.CAPTAIN_TOCK_7958, new WorldPoint(2574, 2835, 1), "Talk to Captain Tock on the ship.");
		talkToTockShip3.addDialogStep("I've seen Ithoi running around. He's not sick at all.");
		talkToTockShip3.addSubSteps(goOntoShip3);

		goUpToIthoiToKill = new ObjectStep(this, ObjectID.STAIRS_31735, new WorldPoint(2531, 2833, 0), "Go kill Ithoi (level 35) in his hut.");
		goUpToIthoiToKill.addDialogStep("I'll be back.");
		killIthoi = new NpcStep(this, NpcID.ITHOI_THE_NAVIGATOR_7964, new WorldPoint(2529, 2840, 1), "Kill Ithoi (level 35).");
		killIthoi.addSubSteps(goUpToIthoiToKill);

		goOntoShip4 = new ObjectStep(this, ObjectID.GANGPLANK_31756, new WorldPoint(2578, 2839, 0), "Talk to Captain Tock on the ship to finish the quest.");
		talkToTockShip4 = new NpcStep(this, NpcID.CAPTAIN_TOCK_7958, new WorldPoint(2574, 2835, 1), "Talk to Captain Tock on the ship to finish the quest.");
		talkToTockShip4.addDialogStep("I've killed Ithoi for poisoning your crew.");
		talkToTockShip4.addSubSteps(goOntoShip4);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(combatGear);
		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Ithoi the Navigator (level 34)");
		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to Yusuf's Bank in the Corsair Cove."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Travel to the cove", Arrays.asList(talkToTockFarm, talkToTockRimmington), combatGear));
		allSteps.add(new PanelDetails("Solve the curse", Arrays.asList(talkToIthoi, talkToGnocci, talkToArsen, talkToColin, grabTinderbox, pickUpSpade, talkToTockShip, talkToTess, digSand, lookThroughTelescope, talkToGnocci2, talkToArsen2, talkToColin2)));
		allSteps.add(new PanelDetails("Discover betrayal", Arrays.asList(talkToTockShip2, talkToGnocci3, talkToArsen3, talkToIthoi2, useTinderboxOnWood, talkToTockShip3)));
		allSteps.add(new PanelDetails("Deal with Ithoi", Arrays.asList(killIthoi, talkToTockShip4)));
		return allSteps;
	}
}
