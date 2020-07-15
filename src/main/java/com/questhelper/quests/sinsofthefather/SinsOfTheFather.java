//package com.questhelper.quests.sinsofthefather;
//
//import net.runelite.api.ItemID;
//import net.runelite.api.NpcID;
//import net.runelite.api.ObjectID;
//import net.runelite.api.Quest;
//import net.runelite.api.coords.WorldPoint;
//import net.runelite.client.plugins.questhelper.AxeRequirement;
//import net.runelite.client.plugins.questhelper.ItemRequirement;
//import net.runelite.client.plugins.questhelper.QuestDescriptor;
//import net.runelite.client.plugins.questhelper.dialog.DialogueChoiceStep;
//import net.runelite.client.plugins.questhelper.questhelpers.BasicQuestHelper;
//import net.runelite.client.plugins.questhelper.steps.NpcTalkStep;
//import net.runelite.client.plugins.questhelper.steps.ObjectStep;
//import net.runelite.client.plugins.questhelper.steps.QuestStep;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@QuestDescriptor(
//        quest = Quest.SINS_OF_THE_FATHER
//)
//public class SinsOfTheFather extends BasicQuestHelper
//{
//    @Override
//    public Map<Integer, QuestStep> loadSteps()
//    {
//        Map<Integer, QuestStep> steps = new HashMap<>();
//
//        steps.put(0, new NpcTalkStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3729, 3318, 0),
//                "Talk to Veliaf to start the quest."));
//
//        steps.get(0).addDialogueStep(new DialogueChoiceStep("Yes."));
//        steps.put(2, new NpcTalkStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3729, 3318, 0),
//                "Talk to Veliaf to start the quest."));
//        steps.put(4, new NpcTalkStep(this, NpcID.HAMELN_THE_JESTER_9505, new WorldPoint(3736, 3316, 0),
//                "Go to the church and talk Hameln the Jester about how his friend fell ill."));
//        steps.get(4).addDialogueStep(new DialogueChoiceStep("I'd better get going."));
//        steps.get(4).addDialogueStep(new DialogueChoiceStep("Do you know how he fell ill?"));
//
//        // TODO: REPLACE ID WITH CARL
//        // TODO: REPLACE WORLDPOINT
//        steps.put(6, new NpcTalkStep(this, NpcID.CARL_9767, new WorldPoint(3736, 3316, 0),
//                "Go to The Rat & Bat pub south east of the church and speak to Carl."));
//        steps.get(6).addDialogueStep(new DialogueChoiceStep("Where do you get your Bloody Bracers from?"));
//
//        // TODO: REPLACE ID WITH BARREL
//        steps.put(8, new ObjectStep(this, ObjectID.BARREL_37980, new WorldPoint(3749, 3291, 0),
//                "Inspect the barrel south of the Rat & Bat Pub."));
//
//        // ENTER CUTSCENE
//        steps.put(10, steps.get(8));
//
//        // IN CUTSCENE
//        // TODO: Mark spots to go? Could be
//        steps.put(12, new NpcTalkStep(this, NpcID.CARL, new WorldPoint(3736, 3316, 1),
//                "Go to The Rat & Bat pub south east of the church and speak to Carl."));
//
//        steps.put(14, new ObjectStep(this, ObjectID.BARREL_37980, new WorldPoint(3749, 3291, 1),
//                "Follow Carl, hiding behind objects when he turns around."));
//
//        steps.put(16, new NpcTalkStep(this, 9560, new WorldPoint(6672, 4165, 1),
//                "Kill Kroy."));
//
////        steps.put(18, new ObjectStep(this, 37983, new WorldPoint(6673, 4168, 1),
////                "Destroy the Lab table."));
//
//        // DESTROY THESE IF OVERWORLD, IF NOT THE ABOVE. ARROWS TO ALL?
//        steps.put(18, new ObjectStep(this, ObjectID.LAB_TABLE_37983, new WorldPoint(3729, 9760, 0),
//                "Destroy both the Lab table."));
////        steps.put(18, new ObjectStep(this, 37983, new WorldPoint(3725, 9759, 0),
////                "Destroy the Lab table."));
//
//        steps.put(20, new NpcTalkStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3729, 3318, 0),
//                "Go talk to Veliaf."));
//        // TWO OPTIONS< COULD DO EITHER
//
//        steps.put(22, new NpcTalkStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3438, 9897, 0),
//                "Go to Paterdomus and speak to Veliaf."));
//
//        steps.put(24, new NpcTalkStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3438, 9897, 0),
//                "Go to Paterdomus and speak to Veliaf."));
//
//        steps.put(26, new NpcTalkStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3438, 9897, 0),
//                "Go to Paterdomus and speak to Veliaf."));
//        steps.get(26).addDialogueStep(new DialogueChoiceStep("I see. So that's why you wanted to keep him safe, Veliaf?"));
//
//        steps.put(28, new NpcTalkStep(this, NpcID.IVAN_STROM_9530, new WorldPoint(3548, 3516, 0),
//                "Speak to Ivan Strom south of Fenkenstrain's Castle."));
//
//        /* CONVERT TO A BASIC STATEMENT */
//        steps.put(30, new NpcTalkStep(this, NpcID.IVAN_STROM_9530, new WorldPoint(3548, 3516, 0),
//                "Listen to the meeting."));
//
//        steps.put(32, new NpcTalkStep(this, NpcID.IVAN_STROM_9530, new WorldPoint(3548, 3516, 0),
//                "Listen to the meeting."));
//
//        steps.put(34, new NpcTalkStep(this, NpcID.IVAN_STROM_9530, new WorldPoint(3444, 3485, 0),
//                "Speak to Ivan Strom outside the east entrance of Paterdomus."));
//
//        steps.put(36,  new TempleTrekStep(this));
//
//        steps.put(38,  new NpcTalkStep(this, NpcID.IVAN_STROM_9530, new WorldPoint(3486, 3241, 0),
//                "Finish speaking to Ivan Strom."));
//
//        steps.put(40,  new NpcTalkStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3529, 3168, 0),
//                "Talk to Veliaf at the boat house in the south of Burgh de Rott."));
//
//        steps.put(42, new ObjectStep(this, ObjectID.BOAT_17955, new WorldPoint(3522, 3169, 0),
//                "Get into the boat to the Icyene Graveyard."));
//        steps.get(42).addDialogueStep(new DialogueChoiceStep("Icyene Graveyard."));
//
//        // NEW SAFALAAN ID
//        steps.put(44, new NpcTalkStep(this, NpcID.SAFALAAN_HALLOW_9537, new WorldPoint(3684, 3181, 0),
//                "Finish talking to Veliaf and Safalaan."));
//
//        steps.put(46, new NpcTalkStep(this, NpcID.SAFALAAN_HALLOW_9537, new WorldPoint(3684, 3181, 0),
//                "Finish the cutscene."));
//
//        // NEW VANESCULA
//        steps.put(48, new NpcTalkStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3705, 3188, 0),
//                "Talk with Vanescula Drakan."));
//
//        steps.put(50, new DoorPuzzleStep(this));
//
//        steps.put(52, new NpcTalkStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3705, 3188, 0),
//                "Finish the cutscene in the Icyene Graveyard."));
//
//        steps.put(54, new SpeakToTeamStep(this));
//
//        steps.put(56, new NpcTalkStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
//                "Speak to Vanescula in the Icyene Graveyard."));
//
//        steps.put(58, new NpcTalkStep(this, NpcID.SAFALAAN_HALLOW_9537, new WorldPoint(3643, 3305, 0),
//                "Speak to Safalaan at the Lab. The easiest way to get here is to have a Vyrewatch send you to the mines, and head south from there."));
//
//        steps.put(60, new NpcTalkStep(this, NpcID.SAFALAAN_HALLOW_9537, new WorldPoint(3643, 3305, 0),
//                "Go with Safalaan into the deeper Lab."));
//        steps.get(60).addDialogueStep(new DialogueChoiceStep("Shall we get going then?"));
//        steps.get(60).addDialogueStep(new DialogueChoiceStep("Let's go."));
//
//        /* Keep arrow on Safalaan in case person leaves. Kinda obvious where the Bloodveld is in the cutscene. */
//        steps.put(62, new NpcTalkStep(this, NpcID.SAFALAAN_HALLOW_9537, new WorldPoint(3643, 3305, 0),
//                "Defeat the Mutated Bloodveld (lvl-123)."));
//
//        steps.put(64, new NpcTalkStep(this, NpcID.SAFALAAN_HALLOW_9537, new WorldPoint(3611, 9737, 0),
//                "Finish speaking with Safalaan in the Lab."));
//
//        steps.put(66, new ObjectStep(this, ObjectID.BOOKSHELF_38017, new WorldPoint(3589, 9745, 0),
//                "Search the bookshelf in the room west of Safalaan in the Lab."));
//        steps.get(66).addDialogueStep(new DialogueChoiceStep("I'll be back soon."));
//
//        steps.put(68, new NpcTalkStep(this, NpcID.SAFALAAN_HALLOW_9537, new WorldPoint(3611, 9737, 0),
//                "Go show Safalaan the Haemalchemy book 2 you found.",
//                new ItemRequirement(24672, "If you lost the book, search the bookshelf in the room west of Safalaan to get it back.")));
//        steps.get(68).addDialogueStep(new DialogueChoiceStep("Sure. Here you go."));
//
//        steps.put(70, new NpcTalkStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
//                "Speak to Vanescula in the Icyene Graveyard. Take the boat south of Burgh de Rott to get there."));
//        steps.get(70).addDialogueStep(new DialogueChoiceStep("Okay. I'll see you soon."));
//        steps.get(70).addDialogueStep(new DialogueChoiceStep("Icyene Graveyard."));
//
//        /* Won't show the quest symbol as location doesn't match underground area */
//        steps.put(72, new NpcTalkStep(this, NpcID.POLMAFI_FERDYGRIS_9554, new WorldPoint(3605, 3215, 0),
//                "Bring a Vyrewatch disguise to Polmafi in the Meiyerditch hideout in Old Man Ral's basement.",
//                new ItemRequirement(ItemID.VYREWATCH_TOP, "You can get this from Trader Sven in southern Meiyerditch near Old Man Ral's house for 650gp."),
//                new ItemRequirement(ItemID.VYREWATCH_LEGS, "You can get this from Trader Sven in southern Meiyerditch near Old Man Ral's house for 650gp."),
//                new ItemRequirement(ItemID.VYREWATCH_SHOES, "You can get this from Trader Sven in southern Meiyerditch near Old Man Ral's house for 650gp.")));
//        // Trader Sven's ID is NPCID.TRADER_SVEN, location 3603, 3200
//        steps.get(72).addDialogueStep(new DialogueChoiceStep("Here you go."));
//
//        steps.put(74, steps.get(72));
//
//        steps.put(76, new NpcTalkStep(this, 9554, new WorldPoint(3600, 9611, 0),
//                "Finish speaking to Polmafi in the Meiyerditch hideout."));
//        steps.get(76).addDialogueStep(new DialogueChoiceStep("Here you go."));
//
//        ItemRequirement unscentedTop =  new ItemRequirement(ItemID.VYRE_NOBLE_TOP_UNSCENTED, "You can get a replacement from a chest in Old Man Ral's basement.");
//        ItemRequirement unscentedLegs = new ItemRequirement(ItemID.VYRE_NOBLE_LEGS_UNSCENTED, "You can get a replacement from a chest in Old Man Ral's basement.");
//        ItemRequirement unscentedShoes = new ItemRequirement(ItemID.VYRE_NOBLE_SHOES_UNSCENTED, "You can get a replacement from a chest in Old Man Ral's basement.");
//
//        steps.put(78, new NpcTalkStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
//                "Return to Vanescula in the Icyene Graveyard with the Vyre noble outfit",
//                unscentedTop, unscentedLegs, unscentedShoes));
//        steps.get(78).addDialogueStep(new DialogueChoiceStep("Icyene Graveyard."));
//
//        steps.put(80, new NpcTalkStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3729, 3318, 0),
//                "Talk to Veliaf outside the church in Slepe.",
//                unscentedTop, unscentedLegs, unscentedShoes, new ItemRequirement(ItemID.IVANDIS_FLAIL, true)));
//        steps.get(80).addDialogueStep(new DialogueChoiceStep("Slepe."));
//
//        steps.put(82, new NpcTalkStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3729, 3318, 0),
//                "Talk to Veliaf outside the church in Slepe.",
//                unscentedTop, unscentedLegs, unscentedShoes, new ItemRequirement(ItemID.IVANDIS_FLAIL, true)));
//        steps.get(82).addDialogueStep(new DialogueChoiceStep("Slepe."));
//        steps.get(82).addDialogueStep(new DialogueChoiceStep("Let's do this."));
//
//        steps.put(84, new NpcTalkStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3729, 3318, 0),
//                "Kill Damien Leucurte (lvl-204). He can poison you. If you leave the fight, talk to Veliaf in Slepe to return to it.",
//                unscentedTop, unscentedLegs, unscentedShoes, new ItemRequirement(ItemID.IVANDIS_FLAIL, true)));
//        steps.get(84).addDialogueStep(new DialogueChoiceStep("Slepe."));
//        steps.get(84).addDialogueStep(new DialogueChoiceStep("Let's do this."));
//
//        steps.put(86, new NpcTalkStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3720, 3357, 1),
//                "Talk to Veliaf in Slepe."));
//
//        ItemRequirement scentedTop =  new ItemRequirement(ItemID.VYRE_NOBLE_TOP, "You can get a replacement from a chest in Old Man Ral's basement.", true);
//        ItemRequirement scentedLegs = new ItemRequirement(ItemID.VYRE_NOBLE_LEGS, "You can get a replacement from a chest in Old Man Ral's basement.", true);
//        ItemRequirement scentedShoes = new ItemRequirement(ItemID.VYRE_NOBLE_SHOES, "You can get a replacement from a chest in Old Man Ral's basement.", true);
//
//        steps.put(88, new NpcTalkStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
//                "Return to Vanescula in the Icyene Graveyard with the Vyre noble outfit",
//                scentedTop, scentedLegs, scentedShoes));
//        steps.get(88).addDialogueStep(new DialogueChoiceStep("I'll see you there."));
//        steps.get(88).addDialogueStep(new DialogueChoiceStep("Icyene Graveyard."));
//
//        steps.put(90, new ObjectStep(this, ObjectID.CRACKED_WALL, new WorldPoint(3627, 3329, 0),
//                "Go to Darkmeyer. You can take the boat to Meiyerditch, talk to a Vyrewatch to be sent to the mines, get out and you'll be right next to the entrance.",
//                scentedTop, scentedLegs, scentedShoes));
//        steps.get(90).addDialogueStep(new DialogueChoiceStep("Meiyerditch."));
//        steps.get(90).addDialogueStep(new DialogueChoiceStep("Send me to the mines."));
//
//        steps.put(92, new NpcTalkStep(this, NpcID.DESMODUS_LASIURUS, new WorldPoint(3612, 3362, 0),
//                "Speak to Desmodus Lasiurus outside the Aboretum in Darkmeyer.",
//                scentedTop, scentedLegs, scentedShoes));
//
//        steps.put(94, new NpcTalkStep(this, NpcID.MORDAN_NIKAZSI, new WorldPoint(3662, 3347, 0),
//                "Speak to Mordan Mikazsi in lower Darkmeyer.",
//                scentedTop, scentedLegs, scentedShoes));
//
//        steps.put(96, new NpcTalkStep(this, NpcID.MARIA_GADDERANKS, new WorldPoint(3618, 3378, 0),
//                "Speak to Maria Gadderanks in the jail north of the Aboretum in Darkmeyer.",
//                scentedTop, scentedLegs, scentedShoes));
//
//        steps.put(98, steps.get(96));
//
//        steps.put(100, new NpcTalkStep(this, NpcID.DESMODUS_LASIURUS, new WorldPoint(3612, 3362, 0),
//                "Speak to Desmodus Lasiurus outside the Aboretum in Darkmeyer.",
//                scentedTop, scentedLegs, scentedShoes));
//
//        // TODO: Good conditional to add, 'if note, show read'
//        steps.put(102, new ObjectStep(this, ObjectID.SHELVES_37999, new WorldPoint(3626, 3359, 0),
//                "Search the shelves in the Aboretum in Darkmeyer. Read the Old Note you get",
//                scentedTop, scentedLegs, scentedShoes));
//
//        steps.put(104, new ValveStep(this));
//        steps.put(106, steps.get(104));
//        steps.put(108, steps.get(104));
//        steps.put(110, steps.get(104));
//
//        QuestStep bringVanesculaLogs = new NpcTalkStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
//                "Return to Vanescula in the Icyene Graveyard with the 8 Blisterwood logs",
//                new ItemRequirement(ItemID.BLISTERWOOD_LOGS, 8));
//        bringVanesculaLogs.addDialogueStep(new DialogueChoiceStep("Icyene Graveyard."));
//
//        steps.put(112, new GetBlisterwoodStep(this, bringVanesculaLogs));
//
//        QuestStep bringVertidaLogs = new NpcTalkStep(this, NpcID.VERTIDA_SEFALATIS, new WorldPoint(3605, 3215, 0),
//                "Go speak to Vertida in Old Man Ral's basement with the 8 Blisterwood logs",
//                new ItemRequirement(ItemID.BLISTERWOOD_LOGS, 8));
//        bringVertidaLogs.addDialogueStep(new DialogueChoiceStep("Meiyerditch."));
//
//        steps.put(114, new GetBlisterwoodStep(this, bringVertidaLogs));
//        // TODO: Idea, you can addCondition, which is some form of check class (area, etc), which if passes, uses the step passed in
//
//        steps.put(116, new NpcTalkStep(this, NpcID.VERTIDA_SEFALATIS, new WorldPoint(3605, 3215, 0),
//                "Speak to Vertida Sefalatis."));
//
//        steps.put(118, new CreateFlailStep(this));
//
//        steps.put(120, new NpcTalkStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
//                "Return to Vanescula in the Icyene Graveyard with the Blisterwood Flail",
//                new ItemRequirement(ItemID.BLISTERWOOD_FLAIL, "You can get another Blisterwood Flail from Vertida in the Myreque Hideout in Old Man Ral's basement.")));
//        steps.get(120).addDialogueStep(new DialogueChoiceStep("Icyene Graveyard."));
//
//        steps.put(122, new NpcTalkStep(this, NpcID.SAFALAAN_HALLOW_9537, new WorldPoint(3719, 3215, 0),
//                "Speak to Safalaan north of the Icyene Graveyard.",
//                new ItemRequirement(ItemID.BLISTERWOOD_FLAIL, "You can get another Blisterwood Flail from Vertida in the Myreque Hideout in Old Man Ral's basement.")));
//        steps.get(122).addDialogueStep(new DialogueChoiceStep("Icyene Graveyard."));
//
//        steps.put(124, new NpcTalkStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
//                "Speak to Vanescula in the Icyene Graveyard.",
//                new ItemRequirement(ItemID.BLISTERWOOD_FLAIL, "You can get another Blisterwood Flail from Vertida in the Myreque Hideout in Old Man Ral's basement.")));
//        steps.get(124).addDialogueStep(new DialogueChoiceStep("Icyene Graveyard."));
//
//        steps.put(126, new NpcTalkStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
//                "Prepare for a challenging fight. Speak to Vanescula to enter the fight.",
//                new ItemRequirement(ItemID.BLISTERWOOD_FLAIL, "You can get another Blisterwood Flail from Vertida in the Myreque Hideout in Old Man Ral's basement.")));
//        steps.get(126).addDialogueStep(new DialogueChoiceStep("Icyene Graveyard."));
//        steps.get(126).addDialogueStep(new DialogueChoiceStep("I'm ready."));
//
//        steps.put(128, new NpcTalkStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3707, 3188, 0),
//                "Prepare for a challenging fight. Speak to Veliaf to enter the fight.",
//                new ItemRequirement(ItemID.BLISTERWOOD_FLAIL, "You can get another Blisterwood Flail from Vertida in the Myreque Hideout in Old Man Ral's basement.")));
//        steps.get(128).addDialogueStep(new DialogueChoiceStep("Icyene Graveyard."));
//        steps.get(128).addDialogueStep(new DialogueChoiceStep("Let's go."));
//
//        steps.put(130, new NpcTalkStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3707, 3188, 0),
//                "Defeat Vanstrom. Speak to Veliaf in the Icyene graveyard to re-enter the fight.",
//                new ItemRequirement(ItemID.BLISTERWOOD_FLAIL, "You can get another Blisterwood Flail from Vertida in the Myreque Hideout in Old Man Ral's basement.")));
//        steps.get(130).addDialogueStep(new DialogueChoiceStep("Icyene Graveyard."));
//        steps.get(130).addDialogueStep(new DialogueChoiceStep("Let's go."));
//
//        // TODO: This could actually be in the Myreque hideout. Need to verify.
//        steps.put(132, new NpcTalkStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3707, 3188, 0),
//                "Speak to Veliaf in the Icyene graveyard to re-enter the cutscene."));
//        steps.get(132).addDialogueStep(new DialogueChoiceStep("Icyene Graveyard."));
//        steps.get(132).addDialogueStep(new DialogueChoiceStep("Let's go."));
//
//        steps.put(134, new NpcTalkStep(this, NpcID.VELIAF_HURTZ_9522, new WorldPoint(3605, 3215, 0),
//                "Speak to Veliaf in the Myreque Hideout."));
//
//        steps.put(136, new NpcTalkStep(this, NpcID.VELIAF_HURTZ_9522, new WorldPoint(3605, 3215, 0),
//                "Speak to Veliaf in the Myreque Hideout."));
//
//        return steps;
//    }
//}
