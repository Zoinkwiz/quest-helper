package com.questhelper.quests.coldwar;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.steps.*;
import com.questhelper.steps.conditional.*;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@QuestDescriptor(
        quest = QuestHelperQuest.COLD_WAR
)
public class ColdWar extends BasicQuestHelper {

    ItemRequirement oakPlanks, oakPlankHighlight, steelNails, hammer, spade, spadeHighlight, clockworkOrSteelBar,
            clockwork, steelBar, plank, silk, rawCodOrCharos, swampTar, feathers, mahoganyPlank, leather, cowbell,
            teleports, clockworkBookHighlight, clockworkSuit, clockworkSuitHighlight, missionReportHighlight,
            lumbridgeMissionReport, mahoganyPlankHighlight, leatherHighlight, bongos;

    ConditionForStep isOnIceberg, birdHideBuilt, isInPOH, hasClockwork, hasSteelBar, hasClockworkSuit, isPenguin,
            isInPenguinPen, isAtZoo, isAtLumbridgeSheepFarm, hasCowbell, isInAgilityStart, isInAgilityWater,
            isInAgilityStones, isInAgilityTreadSoftly, isInAgilityCrossIce, isInAgilityDone, isInPingPongRoom,
            hasBongos, isPreWarRoom, isInIcelordPit, isInIcelordRoom;

    QuestStep talkToLarry, talkToLarryAgain, usePlankOnFirmSnow, useSpadeOnBirdHide, learnPenguinEmotes,
            talkToLarryAfterEmotes, returnToRelleka, talkToLarryInRelleka, readBook, makeClockwork, makePenguin,
            bringSuitToLarry, talkToLarryOnIcebergWithSuit, tuxedoTime, enterPenguinPen, talkToZooPenguin,
            readMissionReport, exitSuit, talkToLarryMissionReport, tuxedoTimeLumbridge, talkToThing, returnToZooPenguin,
            returnToThing, fredTheFarmer, stealCowbell, askThingAboutOutpost, tellLarryAboutOutpost, kgpAgent, noodle1,
            noodle2, kgpAgent2, enterAvalanche, kgpAgentInAvalanche, enterAgilityCourse, agilityCourse,
            agilityEnterWater, agilityExitWater, agilityJumpStones, agilityTreadSoftly, agilityCrossIce, agilityDone,
            tellLarryAboutArmy, kgpBeforePingPong, pingPong1, removePenguinSuitForBongos, makeBongos, pingPong2,
            openControlDoor, enterWarRoom, killIcelordsToExit, useChasm, tellLarryPlans;

    Zone onIceberg, inPOH, inPenguinPen, atZoo, atLumbridgeSheepFarm, inAgilityStart, inAgilityWater, inAgilityStones,
            inAgilityTreadSoftly, inAgilityCrossIce, inAgilityDone, inPingPongRoom, preWarRoom, inIcelordPit,
            inIcelordRoom;

    @Override
    public Map<Integer, QuestStep> loadSteps() {
        setupItemRequirements();
        setupZones();
        setupConditions();
        setupSteps();

        Map<Integer, QuestStep> steps = new HashMap<>();
        steps.put(0, talkToLarry);

        ConditionalStep settingUpTheBirdHide = new ConditionalStep(this, talkToLarryAgain);
        settingUpTheBirdHide.addStep(birdHideBuilt, useSpadeOnBirdHide);
        settingUpTheBirdHide.addStep(isOnIceberg, usePlankOnFirmSnow);
        steps.put(5, talkToLarryAgain);

        steps.put(10, learnPenguinEmotes);

        steps.put(15, talkToLarryAfterEmotes);

        ConditionalStep larryInRelleka = new ConditionalStep(this, talkToLarryInRelleka);
        larryInRelleka.addStep(isOnIceberg, returnToRelleka);
        steps.put(20, larryInRelleka);

        ConditionalStep clockworkPenguin = new ConditionalStep(this, readBook);
        clockworkPenguin.addStep(hasClockworkSuit, bringSuitToLarry);
        clockworkPenguin.addStep(new Conditions(hasClockwork, isInPOH), makePenguin);
        clockworkPenguin.addStep(new Conditions(hasSteelBar, isInPOH), makeClockwork);
        steps.put(25, clockworkPenguin);

        steps.put(30, talkToLarryOnIcebergWithSuit);

        ConditionalStep zooPenguinTrust = new ConditionalStep(this, tuxedoTime);
        zooPenguinTrust.addStep(isInPenguinPen, talkToZooPenguin);
        zooPenguinTrust.addStep(isPenguin, enterPenguinPen);
        steps.put(35, zooPenguinTrust);

        steps.put(40, talkToZooPenguin);

        ConditionalStep lumbridgeVisit1 = new ConditionalStep(this, tuxedoTimeLumbridge);
        lumbridgeVisit1.addStep(new Conditions(isPenguin, isAtLumbridgeSheepFarm), talkToThing);
        lumbridgeVisit1.addStep(new Conditions(isPenguin, isAtZoo), exitSuit);
        lumbridgeVisit1.addStep(isAtZoo, talkToLarryMissionReport);
        steps.put(45, lumbridgeVisit1);

        steps.put(50, returnToZooPenguin);

        steps.put(55, returnToThing);

        steps.put(60, fredTheFarmer);

        ConditionalStep outpostInfo = new ConditionalStep(this, stealCowbell);
        outpostInfo.addStep(hasCowbell, askThingAboutOutpost);
        steps.put(65, outpostInfo);

        ConditionalStep enterTheIceberg = new ConditionalStep(this, tellLarryAboutOutpost);
        enterTheIceberg.addStep(isOnIceberg, kgpAgent);
        steps.put(70, enterTheIceberg);

        steps.put(75, noodle1);

        steps.put(80, noodle2);

        steps.put(85, kgpAgent2);

        steps.put(90, enterAvalanche);

        steps.put(95, kgpAgentInAvalanche);

        ConditionalStep agilityCourse = new ConditionalStep(this, enterAgilityCourse);
        agilityCourse.addStep(isInAgilityStart, agilityEnterWater);
        agilityCourse.addStep(isInAgilityWater, agilityExitWater);
        agilityCourse.addStep(isInAgilityStones, agilityJumpStones);
        agilityCourse.addStep(isInAgilityTreadSoftly, agilityTreadSoftly);
        agilityCourse.addStep(isInAgilityCrossIce, agilityCrossIce);
        agilityCourse.addStep(isInAgilityDone, agilityDone);
        steps.put(100, agilityCourse);

        steps.put(105, tellLarryAboutArmy);

        ConditionalStep pingPongClearance = new ConditionalStep(this, kgpBeforePingPong);
        pingPongClearance.addStep(isInPingPongRoom, pingPong1);
        steps.put(110, pingPongClearance);

        ConditionalStep gatherInstruments = new ConditionalStep(this, makeBongos);
        gatherInstruments.addStep(new Conditions(hasBongos, hasCowbell), pingPong2);
        gatherInstruments.addStep(hasBongos, stealCowbell);
        gatherInstruments.addStep(isPenguin, removePenguinSuitForBongos);
        steps.put(115, gatherInstruments);

        ConditionalStep enterControlRoom = new ConditionalStep(this, openControlDoor);
        enterControlRoom.addStep(isPreWarRoom, enterWarRoom);
        steps.put(120, enterControlRoom);

        ConditionalStep escape = new ConditionalStep(this, tellLarryPlans);
        escape.addStep(isInIcelordPit, killIcelordsToExit);
        escape.addStep(isInIcelordRoom, useChasm);
        steps.put(125, escape);

        return null;
    }

    public void setupItemRequirements() {
        oakPlanks = new ItemRequirement("Oak Planks (unnoted)", ItemID.OAK_PLANK, 10);
        oakPlankHighlight = new ItemRequirement("Oak Plank", ItemID.OAK_PLANK, 1);
        oakPlankHighlight.setHighlightInInventory(true);
        steelNails = new ItemRequirement("Steel Nails", ItemID.STEEL_NAILS, 10);
        hammer = new ItemRequirement("Hammer", ItemID.HAMMER, 1);
        spade = new ItemRequirement("Spade", ItemID.SPADE, 1);
        spadeHighlight = new ItemRequirement("Spade", ItemID.SPADE, 1);
        spadeHighlight.setHighlightInInventory(true);
        clockworkOrSteelBar = new ItemRequirement("Clockwork or Steel Bar", -1, 1);
        steelBar = new ItemRequirement("Steel Bar", ItemID.CLOCKWORK, 1);
        clockwork = new ItemRequirement("Clockwork", ItemID.CLOCKWORK, 1);
        plank = new ItemRequirement("Normal Plank", ItemID.PLANK, 1);
        silk = new ItemRequirement("Silk", ItemID.SILK, 1);
        rawCodOrCharos = new ItemRequirement("A Raw Cod or the Ring of Charos (a)", -1, 1);
        swampTar = new ItemRequirement("Swamp Tar", ItemID.SWAMP_TAR, 1);
        feathers = new ItemRequirement("Feathers", ItemID.FEATHER, 5);
        mahoganyPlank = new ItemRequirement("Mahogany Plank", ItemID.MAHOGANY_PLANK, 1);
        leather = new ItemRequirement("Leather", ItemID.LEATHER, 1);
        cowbell = new ItemRequirement("Cowbell", ItemID.COWBELLS, 1);
        teleports = new ItemRequirement("Teleports to Lumbridge, POH and Ardougne", -1);
        clockworkBookHighlight = new ItemRequirement("Clockwork book", ItemID.CLOCKWORK_BOOK);
        clockworkBookHighlight.setHighlightInInventory(true);
        clockworkSuit = new ItemRequirement("Clockwork suit", ItemID.CLOCKWORK_SUIT);
        clockworkSuitHighlight = new ItemRequirement("Clockwork suit", ItemID.CLOCKWORK_SUIT);
        clockworkSuitHighlight.setHighlightInInventory(true);
        missionReportHighlight = new ItemRequirement("Mission report", ItemID.MISSION_REPORT);
        missionReportHighlight.setHighlightInInventory(true);
        lumbridgeMissionReport = new ItemRequirement("Lumbridge mission report", ItemID.MISSION_REPORT_10598);
        mahoganyPlankHighlight = new ItemRequirement("Mahogany Plank", ItemID.MAHOGANY_PLANK);
        mahoganyPlankHighlight.setHighlightInInventory(true);
        leatherHighlight = new ItemRequirement("Leather", ItemID.LEATHER);
        leatherHighlight.setHighlightInInventory(true);
        bongos = new ItemRequirement("Penguin bongos", ItemID.PENGUIN_BONGOS);
    }

    public void setupZones() {
        onIceberg = new Zone(new WorldPoint(2641, 3978, 1), new WorldPoint(2681, 4011, 1));
        inPOH = new Zone(new WorldPoint(7035, 8124, 0), new WorldPoint(7082, 8172, 0));
        inPenguinPen = new Zone(new WorldPoint(2592, 3267, 0), new WorldPoint(2597, 3270, 0));
        atZoo = new Zone(new WorldPoint(2592, 3260, 0), new WorldPoint(2650, 3290, 0));
        atLumbridgeSheepFarm = new Zone(new WorldPoint(3193, 3257, 0), new WorldPoint(3212, 3276, 0));
        inAgilityStart = new Zone(new WorldPoint(2638, 4032, 1), new WorldPoint(2648, 4056, 1));
        inAgilityWater = new Zone(new WorldPoint(2628, 4053, 0), new WorldPoint(2635, 4065, 0));
        inAgilityStones = new Zone(new WorldPoint(2631, 4057, 1), new WorldPoint(2635, 4065, 1));
        inAgilityTreadSoftly = new Zone(new WorldPoint(2635, 4064, 1), new WorldPoint(2662, 4082, 1));
        inAgilityCrossIce = new Zone(new WorldPoint(2663, 4068, 1), new WorldPoint(2666, 4084, 1));
        inAgilityDone = new Zone(new WorldPoint(2652, 4035, 1), new WorldPoint(2666, 4042, 1));
        inPingPongRoom = new Zone(new WorldPoint(2664, 10394, 0), new WorldPoint(2672, 10399, 0));
        preWarRoom = new Zone(new WorldPoint(2641, 10410, 0), new WorldPoint(2671, 10419, 0));
        inIcelordPit = new Zone(new WorldPoint(2639, 10422, 0), new WorldPoint(2652, 10428, 0));
        inIcelordRoom = new Zone(new WorldPoint(2636, 10419, 0), new WorldPoint(2656, 10429, 0));
    }

    public void setupConditions() {
        isOnIceberg = new ZoneCondition(onIceberg);
        birdHideBuilt = new VarbitCondition(3294, 1);
        isInPOH = new ZoneCondition(inPOH);
        hasClockwork = new ItemRequirementCondition(clockwork);
        hasSteelBar = new ItemRequirementCondition(steelBar);
        hasClockworkSuit = new ItemRequirementCondition(clockworkSuit);
        isPenguin = new VarbitCondition(3306, 1);
        isInPenguinPen = new ZoneCondition(inPenguinPen);
        isAtZoo = new ZoneCondition(atZoo);
        isAtLumbridgeSheepFarm = new ZoneCondition(atLumbridgeSheepFarm);
        hasCowbell = new ItemRequirementCondition(cowbell);
        isInAgilityStart = new ZoneCondition(inAgilityStart);
        isInAgilityWater = new ZoneCondition(inAgilityWater);
        isInAgilityStones = new ZoneCondition(inAgilityStones);
        isInAgilityTreadSoftly = new ZoneCondition(inAgilityTreadSoftly);
        isInAgilityCrossIce = new ZoneCondition(inAgilityCrossIce);
        isInAgilityDone = new ZoneCondition(inAgilityDone);
        isInPingPongRoom = new ZoneCondition(inPingPongRoom);
        hasBongos = new ItemRequirementCondition(bongos);
        isPreWarRoom = new ZoneCondition(preWarRoom);
        isInIcelordPit = new ZoneCondition(inIcelordPit);
        isInIcelordRoom = new ZoneCondition(inIcelordRoom);
    }

    public void setupSteps() {
        talkToLarry = new NpcStep(this, NpcID.LARRY, new WorldPoint(2597, 3266, 0), "Talk to Larry at the Ardougne Zoo.");
        talkToLarry.addDialogStep("Okay, why not!");

        talkToLarryAgain = new NpcStep(this, NpcID.LARRY, new WorldPoint(2597, 3266, 0), "Talk to Larry again.", oakPlanks, steelNails, hammer, spade);
        talkToLarryAgain.addDialogStep("Yes, I have all the materials.");
        talkToLarryAgain.addDialogStep("Yes");

        usePlankOnFirmSnow = new ObjectStep(this, ObjectID.FIRM_SNOW_PATCH, new WorldPoint(2665, 3991, 1), "Use an oak plank on the firm snow patch.", oakPlankHighlight);
        usePlankOnFirmSnow.addIcon(ItemID.OAK_PLANK);

        useSpadeOnBirdHide = new ObjectStep(this, ObjectID.BIRD_HIDE_STRUCTURE, new WorldPoint(2665, 3991, 1), "Use a spade on the bird hide to cover it in snow.", spadeHighlight);
        useSpadeOnBirdHide.addIcon(ItemID.SPADE);

        learnPenguinEmotes = new NpcStep(this, NpcID.LARRY_829, "Talk to Larry on the iceberg to enter the bird hide. PAY ATTENTION TO THIS CUTSCENE and note the 3 emotes the LEFT penguin does.");

        talkToLarryAfterEmotes = new NpcStep(this, NpcID.LARRY_829, "Talk to Larry again.");
        talkToLarryAfterEmotes.addDialogStep("That's crazy!");

        returnToRelleka = new ObjectStep(this, ObjectID.BOAT_21175, "Click the boat to return to Relleka.");

        talkToLarryInRelleka = new NpcStep(this, NpcID.LARRY_828, "Talk to Larry in Relleka.");

        readBook = new ItemStep(this, "Read the clockwork book, then travel to your POH or another POH with a Crafting table 3.", clockworkBookHighlight, clockworkOrSteelBar);

        makeClockwork = new ObjectStep(this, ObjectID.CLOCKMAKERS_BENCH_6798, "Craft a steel bar into a clockwork at a crafting table 3.", steelBar);
        makeClockwork.addDialogStep("Clockwork mechanism");

        makePenguin = new ObjectStep(this, ObjectID.CLOCKMAKERS_BENCH_6798, "Craft a clockwork penguin at a crafting table 3.", clockwork);
        makePenguin.addDialogStep("Clockwork toy");
        makePenguin.addDialogStep("Clockwork penguin");

        bringSuitToLarry = new NpcStep(this, NpcID.LARRY, new WorldPoint(2597, 3266, 0), "Talk to Larry back in the Ardougne Zoo.");
        bringSuitToLarry.addDialogStep("Yes, I have it.");
        bringSuitToLarry.addDialogStep("Yes");

        talkToLarryOnIcebergWithSuit = new NpcStep(this, NpcID.LARRY_829, "Talk to Larry on the iceberg.");
        talkToLarryOnIcebergWithSuit.addDialogStep("It looks like a warning message to keep us away.");
        talkToLarryOnIcebergWithSuit.addDialogStep("Yes");

        tuxedoTime = new NpcStep(this, NpcID.LARRY, new WorldPoint(2597, 3266, 0), "Unequip cape and weapons and right-click Tuxedo-time Larry at the Ardougne Zoo.");

        enterPenguinPen = new ObjectStep(this, ObjectID.DOOR_21243, new WorldPoint(2594, 3266, 0), "Enter the penguin pen.");

        talkToZooPenguin = new NpcStep(this, NpcID.PENGUIN_845, "Talk to the zoo penguin. You will need to use the same 3 emotes as the penguin from the bird hide cutscene.");

        readMissionReport = new DetailedQuestStep(this, "Read the mission report.", missionReportHighlight);

        exitSuit = new NpcStep(this, NpcID.LARRY, new WorldPoint(2597, 3266, 0), "Talk to Larry to exit the penguin suit.");
        talkToLarryMissionReport = new NpcStep(this, NpcID.LARRY, new WorldPoint(2597, 3266, 0), "Talk to Larry about the mission report, then travel to the sheep farm in Lumbridge.");
        talkToLarryMissionReport.addSubSteps(exitSuit);

        tuxedoTimeLumbridge = new NpcStep(this, NpcID.LARRY, new WorldPoint(3212, 3263, 0), "Tuxedo-time Larry in the Lumbridge sheep farm.");

        talkToThing = new NpcStep(this, NpcID.SHEEP, "Talk to the penguins disguised as a sheep in the Lumbridge sheep farm. You will need to use the same 3 emotes as the penguin from the bird hide cutscene.");

        returnToZooPenguin = new NpcStep(this, NpcID.PENGUIN_845, "Return to the Ardougne Zoo penguin with either a raw cod, or wearing the ring of charos.", rawCodOrCharos);
        returnToZooPenguin.addDialogStep("The penguins in Lumbridge refuse to talk to me.");
        returnToZooPenguin.addDialogStep("I must have left the output before they gave out the phrase."); // todo need third dialog option for using raw cod

        returnToThing = new NpcStep(this, NpcID.SHEEP, new WorldPoint(3212, 3263, 0), "Speak to the sheep-penguins again in the Lumbridge sheep farm.");

        fredTheFarmer = new NpcStep(this, NpcID.FRED_THE_FARMER, new WorldPoint(3189, 3273, 0), "Talk to Fred the Farmer without the penguin suit.");
        fredTheFarmer.addDialogStep("I need to talk to you about penguins.");
        fredTheFarmer.addDialogStep("Bully Fred");

        stealCowbell = new ObjectStep(this, ObjectID.DAIRY_COW, new WorldPoint(3172, 3318, 0), "Steal a cowbell from a dairy cow.");

        askThingAboutOutpost = new NpcStep(this, NpcID.SHEEP, new WorldPoint(3212, 3263, 0), "Speak to the sheep-penguins once again in the Lumbridge sheep farm to learn about the outpost.");
        askThingAboutOutpost.addDialogStep("The Farmer is harmless.");

        tellLarryAboutOutpost = new NpcStep(this, NpcID.LARRY, new WorldPoint(3212, 3263, 0), "Tell Larry about the outpost and travel back to the iceberg.", swampTar, feathers, lumbridgeMissionReport);
        tellLarryAboutOutpost.addDialogStep("Yes");

        kgpAgent = new NpcStep(this, NpcID.KGP_AGENT, new WorldPoint(2639, 4008, 1), "Jump in the penguin suit and talk to the KGP Agent on the north west portion of the iceberg.");

        noodle1 = new NpcStep(this, NpcID.NOODLE, "Talk to Noodle.");

        noodle2 = new NpcStep(this, NpcID.NOODLE, "Give Noodle the swamp tar to get an ID card and a mission report.", swampTar, feathers);
        noodle2.addDialogStep("Yeah, I got it.");

        kgpAgent2 = new NpcStep(this, NpcID.KGP_AGENT, new WorldPoint(2639, 4008, 1), "Talk to the KGP Agent again.");

        enterAvalanche = new ObjectStep(this, ObjectID.AVALANCHE, "Enter the avalanche.");

        kgpAgentInAvalanche = new NpcStep(this, NpcID.KGP_AGENT, new WorldPoint(2647, 10384, 0), "Talk to the KGP Agent in the first room to the west of the entrance.");

        enterAgilityCourse = new ObjectStep(this, ObjectID.DOOR_21169, new WorldPoint(2633, 10405, 0), "Enter the door to the west of the KGP Agent to begin the agility course.");

        agilityCourse = new DetailedQuestStep(this, "Complete the agility course.");
        agilityEnterWater = new TileStep(this, new WorldPoint(2636, 4054, 1), "Walk up to the start of the agility course.");
        agilityExitWater = new ObjectStep(this, ObjectID.STEPPING_STONE_21120, new WorldPoint(2630, 4057, 0), "Avoid the ice and exit the water.");
        agilityJumpStones = new TileStep(this, new WorldPoint(2635, 4065, 1), "Jump across the stepping stones.");
        agilityTreadSoftly = new TileStep(this, new WorldPoint(2663, 4082, 1), "Tread-softly across the icicles.");
        agilityCrossIce = new TileStep(this, new WorldPoint(2664, 4068, 1), "Cross ice to get up the hill.");
        agilityDone = new NpcStep(this, NpcID.AGILITY_INSTRUCTOR, "Talk to the Agility Instructor.");
        agilityCourse.addSubSteps(enterAgilityCourse, agilityEnterWater, agilityExitWater, agilityJumpStones, agilityTreadSoftly, agilityCrossIce, agilityDone);

        tellLarryAboutArmy = new NpcStep(this, NpcID.LARRY_829, "Return to Larry to tell him about the penguin army. You can quickly return to him by removing the penguin suit.");

        kgpBeforePingPong = new NpcStep(this, NpcID.KGP_AGENT, new WorldPoint(2655, 10408, 0), "Re-enter the outpost and talk to the KGP agent north of the entrance.");

        pingPong1 = new NpcStep(this, NpcID.PING_839, "Talk to Ping or Pong in the room to the east.");
        ((NpcStep) pingPong1).addAlternateNpcs(NpcID.PONG_840);

        removePenguinSuitForBongos = new DetailedQuestStep(this, "Remove the penguin suit.");

        makeBongos = new DetailedQuestStep(this, "Use the mahogany plank on the leather to make bongos.", mahoganyPlankHighlight, leatherHighlight);
        makeBongos.addSubSteps(removePenguinSuitForBongos);

        pingPong2 = new NpcStep(this, NpcID.PING_839, "Return to Ping or Pong to give them the bongos and cowbells.", bongos, cowbell);
        ((NpcStep) pingPong2).addAlternateNpcs(NpcID.PONG_840);
        pingPong2.addDialogStep("Yes.");

        openControlDoor = new ObjectStep(this, ObjectID.CONTROL_PANEL_21055, "Open the control room doors via the control panel.");

        enterWarRoom = new ObjectStep(this, ObjectID.DOOR_21160, new WorldPoint(2671, 10418, 0), "Enter the war room and walk a few steps in to be captured.");

        killIcelordsToExit = new ObjectStep(this, ObjectID.DOOR_21167, new WorldPoint(2639, 10424, 0), "Kill icelords until you are able to leave through the door to the west. May take up to 3 kills.");

        useChasm = new ObjectStep(this, ObjectID.CHASM, new WorldPoint(2657, 10423, 0), "Use the chasm to exit the cave.");

        tellLarryPlans = new NpcStep(this, NpcID.LARRY_829, "Return to Larry to finish the quest.");
    }

    @Override
    public ArrayList<PanelDetails> getPanels() {
        ArrayList<PanelDetails> allSteps = new ArrayList<>();

        allSteps.add(new PanelDetails("Larry's Story", new ArrayList<>(Arrays.asList(talkToLarry, talkToLarryAgain))));
        allSteps.add(new PanelDetails("Penguin Stake-out", new ArrayList<>(Arrays.asList(usePlankOnFirmSnow, useSpadeOnBirdHide, learnPenguinEmotes, talkToLarryAfterEmotes, returnToRelleka))));
        allSteps.add(new PanelDetails("A Clockwork Penguin", new ArrayList<>(Arrays.asList(talkToLarryInRelleka, readBook, makeClockwork, makePenguin, bringSuitToLarry, talkToLarryOnIcebergWithSuit))));
        allSteps.add(new PanelDetails("Ardougne Mission Report", new ArrayList<>(Arrays.asList(tuxedoTime, enterPenguinPen, talkToZooPenguin, readMissionReport, talkToLarryMissionReport))));
        allSteps.add(new PanelDetails("Lumbridge Mission Report", new ArrayList<>(Arrays.asList(tuxedoTimeLumbridge, talkToThing, returnToZooPenguin, returnToThing, fredTheFarmer, stealCowbell, askThingAboutOutpost))));
        allSteps.add(new PanelDetails("Penguin Outpost", new ArrayList<>(Arrays.asList(tellLarryAboutOutpost, kgpAgent, noodle1, noodle2, kgpAgent2, enterAvalanche))));
        allSteps.add(new PanelDetails("Briefing and Agility", new ArrayList<>(Arrays.asList(kgpAgentInAvalanche, agilityCourse, tellLarryAboutArmy))));
        allSteps.add(new PanelDetails("Musical Penguins", new ArrayList<>(Arrays.asList(kgpBeforePingPong, pingPong1, makeBongos, pingPong2))));
        allSteps.add(new PanelDetails("The War Room", new ArrayList<>(Arrays.asList(openControlDoor, enterWarRoom, killIcelordsToExit, useChasm, tellLarryPlans))));

        return allSteps;
    }
}
