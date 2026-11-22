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
package com.questhelper.helpers.quests.ragandbonemaniii;

import com.questhelper.collections.ItemCollections;
import com.questhelper.helpers.quests.deserttreasureii.ChestCodeStep;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitBuilder;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetPresenceRequirement;
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
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;


@SuppressWarnings("FieldCanBeLocal")
public class RagAndBoneManIII extends BasicQuestHelper
{
	// Required items
	ItemRequirement sampleRequirement;
	ItemRequirement combatGear;

	// Recommended items
	ItemRequirement energyRestorePotion;

	// Mid-quest item requirements
	ItemRequirement itemAcquiredDuringQuest;

	// Zones
	Zone sampleZone;

	// Miscellaneous requirements
	VarbitRequirement hasFinishedCertainTask;

	// Steps
	private NpcStep startQuest;
	private ObjectStep enterArmouredZombieBase;
	private ConditionalStep armouredZombieBones;
	private ConditionalStep angryRatBones;
	private NpcStep returnToOddOldMan;
	private VarbitBuilder illusionVB;
	private VarbitRequirement illusionRomeo;
	private VarbitRequirement illusionTutorialIsland;
	private VarbitRequirement illusionCamdozal;
	private VarbitRequirement illusionWizards;
	private VarbitRequirement illusionTolna;
	private NpcStep talkToDeath;
	private NpcStep talkToRomeo;
	private VarbitBuilder tutorialVB;
	private NpcStep talkToGielenorGuide;
	private NpcStep talkToSurvivalInstructor;
	private NpcStep catchShrimpWithYourHands;
	private NpcStep talkToSurvivalInstructorAgain;
	private ConditionalStep cFinishTutorialIsland;
	private Conditions lostSoulsDead;
	private NpcStep killTheLostSoulsSurroundingTheKing;
	private NpcStep talkToKing;
	private ConditionalStep cCamdozal;
	private VarbitBuilder wizardVB;
	private NpcStep talkToArchmageSedridor;
	private ObjectStep makeBed;
	private NpcStep talkToArchmageSedridorAfterMakingTheBed;
	private ConditionalStep cWizards;
	private ConditionalStep cTolna;

	@Override
	protected void setupZones()
	{
		sampleZone = new Zone(new WorldPoint(10, 10, 0), new WorldPoint(20, 20, 0));
	}

	@Override
	protected void setupRequirements()
	{
		// TODO: set fancy icon
		combatGear = new ItemRequirement("Combat gear", -1, -1);

		sampleRequirement = new ItemRequirement("Bucket", ItemID.BUCKET_EMPTY);

		energyRestorePotion = new ItemRequirement("Stamina potion", ItemCollections.STAMINA_POTIONS);

		itemAcquiredDuringQuest = new ItemRequirement("Book of Lore", ItemID.BOOK_OF_ASTROLOGY);

		hasFinishedCertainTask = new VarbitRequirement(VarbitID.PORT_TASK_SLOT_2_ID, 2);

		illusionVB = new VarbitBuilder(VarbitID.RAG_QUEST_3_IN_ILLUSION);
		illusionRomeo = illusionVB.eq(2);
		illusionTutorialIsland = illusionVB.eq(3);
		illusionCamdozal = illusionVB.eq(4);
		illusionWizards = illusionVB.eq(5);
		illusionTolna = illusionVB.eq(6);

		tutorialVB = new VarbitBuilder(VarbitID.RAG_QUEST_3_TUTORIAL_PROGRESS);

		lostSoulsDead = not(new NpcRequirement(NpcID.RAG3_GHOST));

		wizardVB = new VarbitBuilder(VarbitID.RAG_QUEST_3_WIZARD_PROGRESS);
	}

	void setupSteps()
	{
		var TODO = new DetailedQuestStep(this, "TODO IN SETUP STEPS");
		var spokenToTraibornOnce = new VarbitRequirement(VarbitID.RAG_QUEST_3, 12);
		var spokenToTraibornTwice = new VarbitRequirement(VarbitID.RAG_QUEST_3, 14);

		startQuest = new NpcStep(this, NpcID.RAG_ODD_OLD_MAN_VIS, new WorldPoint(3361, 3506, 0), "Talk to the Odd Old Man north-east of Varrock to start the quest.");
		startQuest.addDialogStep("I'm guessing those displays are about done now?");
		startQuest.addDialogStep("Yes.");

		// TODO: Guide to the actual armoured zombie
		enterArmouredZombieBase = new ObjectStep(this, ObjectID.DOV_BASE_ENTRY_OPEN_OP, "");
		armouredZombieBones = new ConditionalStep(this, enterArmouredZombieBase, "Collect bones from an armoured zombie inside Zemouregal's Base.");

		// TODO: Guide to the actual angry rat
		angryRatBones = new ConditionalStep(this, TODO, "Collect bones from an angry rat inside the rift.");

		returnToOddOldMan = new NpcStep(this, NpcID.RAG_ODD_OLD_MAN_VIS, new WorldPoint(3361, 3506, 0), "Return to the Odd Old Man.");
		returnToOddOldMan.addDialogStep("I've had enough of collecting bones!");

		// START OF ILLUSION LOOP AAAAAAAAAAAAAAA
		talkToDeath = new NpcStep(this, NpcID.RAG3_DEATH, new WorldPoint(3179, 5727, 0), "Talk to Death.");
		talkToRomeo = new NpcStep(this, NpcID.RAG3_ROMEO, new WorldPoint(3299, 5914, 1), "Talk to Romeo when he appears.");

		talkToGielenorGuide = new NpcStep(this, NpcID.RAG3_NEWBIE_BASICS_INSTRUCTOR, new WorldPoint(1365, 4642, 0), "Talk to the Gielinor Guide.");
		talkToSurvivalInstructor = new NpcStep(this, NpcID.RAG3_NEWBIE_SURVIVAL_INSTRUCTOR, new WorldPoint(1375, 4631, 0), "Talk to the Survival Expert.");
		catchShrimpWithYourHands = new NpcStep(this, NpcID.RAG3_NEWBIEFISHING, new WorldPoint(1375, 4628, 0), "Fish some shrimp with your hands.", true);
		talkToSurvivalInstructorAgain = new NpcStep(this, NpcID.RAG3_NEWBIE_SURVIVAL_INSTRUCTOR, new WorldPoint(1375, 4631, 0), "Talk to the Survival Expert again.");
		cFinishTutorialIsland = new ConditionalStep(this, talkToGielenorGuide, "Finish tutorial island. :)");
		cFinishTutorialIsland.addStep(tutorialVB.eq(1), talkToSurvivalInstructor);
		cFinishTutorialIsland.addStep(tutorialVB.eq(2), catchShrimpWithYourHands);
		cFinishTutorialIsland.addStep(tutorialVB.eq(3), talkToSurvivalInstructorAgain);

		killTheLostSoulsSurroundingTheKing = new NpcStep(this, NpcID.RAG3_GHOST, new WorldPoint(2954, 5799, 0), "Kill the Lost Souls attacking the king.", true);
		talkToKing = new NpcStep(this, NpcID.RAG3_KING_ROALD, new WorldPoint(2954, 5800, 0), "Talk to King Roald.");
		cCamdozal = new ConditionalStep(this, killTheLostSoulsSurroundingTheKing, "Finish the camdozal segment.");
		var talkToTraibornInCamdozaal = new NpcStep(this, NpcID.RAG3_TRAIBORN_CAMDOZAAL, new WorldPoint(2931, 5773, 0), "Talk to Wizard Traiborn in Camdozaal, south-west of where the king is being pummeled by ghosts.");
		cCamdozal.addStep(spokenToTraibornOnce, talkToTraibornInCamdozaal);
		cCamdozal.addStep(lostSoulsDead, talkToKing);

		talkToArchmageSedridor = new NpcStep(this, NpcID.RAG3_HEAD_WIZARD, new WorldPoint(3102, 9572, 0), "Talk to Archmage Sedridor.");
		makeBed = new ObjectStep(this, ObjectID.RAG3_WIZARD_BED_MESSY, new WorldPoint(3100, 9573, 0), "Make the wizard's bed.");
		talkToArchmageSedridorAfterMakingTheBed = new NpcStep(this, NpcID.RAG3_HEAD_WIZARD, new WorldPoint(3102, 9572, 0), "Talk to Archmage Sedridor after making his bed.");
		// TODO: What if you are on the third loop - can we tell the user to talk to Traiborn there too?
		var talkToTraiborn = new NpcStep(this, NpcID.RAG3_TRAIBORN, new WorldPoint(3116, 9564, 0), "Talk to Wizard Traiborn.");
		cWizards = new ConditionalStep(this, talkToArchmageSedridor, "Wizards.");
		var atLeastSecondLoop = new VarbitRequirement(VarbitID.RAG_QUEST_3_ILLUSION_TIMES_REACHED_WIZARDS, 2, Operation.GREATER_EQUAL);
		var sedridorInCombat = new NpcRequirement(NpcID.RAG3_HEAD_WIZARD_COMBAT);
		var killSedridor = new NpcStep(this, NpcID.RAG3_HEAD_WIZARD_COMBAT, "Kill Archmage Sedridor.");
		var sedridorStaffOnGround = new ItemOnTileRequirement(ItemID.RAG3_STAFF);
		var sedridorStaff = new ItemRequirement("Sedridor's staff", ItemID.RAG3_STAFF);
		var pickUpSedridorsStaff = new DetailedQuestStep(this, "Pick up Sedridor's staff from the ground", sedridorStaff);
		var talkToTraibornAgain = new NpcStep(this, NpcID.RAG3_TRAIBORN, new WorldPoint(3116, 9564, 0), "Talk to Wizard Traiborn.", sedridorStaff);
		cWizards.addStep(sedridorStaff, talkToTraibornAgain);
		cWizards.addStep(sedridorStaffOnGround, pickUpSedridorsStaff);
		cWizards.addStep(sedridorInCombat, killSedridor);
		cWizards.addStep(and(atLeastSecondLoop, not(spokenToTraibornOnce), not(spokenToTraibornTwice)), talkToTraiborn);
		cWizards.addStep(wizardVB.eq(1), makeBed);
		cWizards.addStep(wizardVB.eq(2), talkToArchmageSedridorAfterMakingTheBed);

		var dieToTolna = new DetailedQuestStep(this, "Die to Tolna. This is a safe death.");
		var killTolna = new NpcStep(this, new int[]{NpcID.RAG3_TOLNA1, NpcID.RAG3_TOLNA2, NpcID.RAG3_TOLNA3}, "Kill the Tolna heads.", sedridorStaff);
		killTolna.setAllowMultipleHighlights(true);
		cTolna = new ConditionalStep(this, dieToTolna, "Tolna.");
		cTolna.addStep(sedridorStaff, killTolna);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		var TODO = new DetailedQuestStep(this, "TODO");

		for (var i = 0; i < 500; ++i) {
			var step = new DetailedQuestStep(this, "TODO " + i);
			steps.put(i, step);
		}

		steps.put(0, startQuest);

		// TODO: do you _HAVE_ to check the list? I will assume _NO_
		// [2026-11-22T13:47:23Z 821] varbit RAG_QUEST_3_CHECKED_LIST (16879) 0 -> 1

		steps.put(2, armouredZombieBones);

		// Killed armour zombie, first creature:
		// [2026-11-22T13:54:41Z 1552] varbit RAG3_FIRST_BONE (16878) 0 -> 3
		// [2026-11-22T13:54:41Z 1552] varbit RAG_QUEST_3 (16877) 2 -> 4
		steps.put(4, angryRatBones);

		// Killed angry giant rat, my second creature:
		// [2026-11-22T13:56:29Z 1732] varbit RAG_QUEST_3 (16877) 4 -> 6

		steps.put(6, returnToOddOldMan);

		// after enraging the old man:
		// [2026-11-22T13:58:28Z 1930] varp OPTION_MASTER_VOLUME (3796) 0 -> 93
		// [2026-11-22T13:59:38Z 2046] varbit RAG_QUEST_3_FAKE_DEATH (16880) 0 -> 1
		// [2026-11-22T13:59:38Z 2046] varbit TRACKING_KILLING_DAMAGE (16680) 16 -> 99
		// [2026-11-22T13:59:38Z 2046] varp TRACKING_DEATHS (4517) 58 -> 59
		// [2026-11-22T13:59:41Z 2052] varbit HOLDING_INVENTORY_LOCATION (14283) 0 -> 7
		// [2026-11-22T13:59:41Z 2052] varp DISEASE (456) 0 -> -1
		// [2026-11-22T13:59:41Z 2052] varbit RAG_QUEST_3_FAKE_DEATH (16880) 1 -> 0
		// [2026-11-22T13:59:41Z 2052] varbit RAG_QUEST_3_ILLUSION_TIMES_STARTED (16884) 0 -> 1
		// [2026-11-22T13:59:41Z 2052] varbit RAG_QUEST_3 (16877) 6 -> 8
		// [2026-11-22T13:59:41Z 2052] varbit RAG_QUEST_3_IN_ILLUSION (16891) 0 -> 1



		var cIllusion = new ConditionalStep(this, talkToDeath);
		cIllusion.addStep(illusionRomeo, talkToRomeo);
		cIllusion.addStep(illusionTutorialIsland, cFinishTutorialIsland);
		cIllusion.addStep(illusionCamdozal, cCamdozal);
		cIllusion.addStep(illusionWizards, cWizards);
		cIllusion.addStep(illusionTolna, cTolna);
		steps.put(8, cIllusion);
		steps.put(10, cIllusion);
		steps.put(12, cIllusion);
		steps.put(14, cIllusion);
		steps.put(16, cIllusion);

		var jump = new ObjectStep(this, ObjectID.RAG3_PIT, "Take a leap of faith into the pit.");
		steps.put(18, jump);

		var bansheeKilled = new VarbitRequirement(VarbitID.RAG3_BANSHEE_KILLED, 1);

		var earMuffs = new ItemRequirement("Ear protection", ItemCollections.EAR_PROTECTION).canBeObtainedDuringQuest();
		earMuffs.setConditionToHide(bansheeKilled);
		var earMuffsEquipped = earMuffs.equipped();

		var talkToOddOldManToGetSentBack = new NpcStep(this, NpcID.RAG_ODD_OLD_MAN_VIS, new WorldPoint(3361, 3506, 0), "Talk to the Odd Old Man north-east of Varrock to get sent back to the dungeon.", earMuffs);

		var inAsylum = new VarbitRequirement(VarbitID.RAG_QUEST_3_IN_ASYLUM, 1);

		var talkToHorrifyingCreature = new NpcStep(this, NpcID.RAG3_STALKER, new WorldPoint(1359, 4785, 0), "Talk to the Horrifying Creature in the north-west room.", earMuffs);
		var cTalkToHorrifyingCreature = new ConditionalStep(this, talkToOddOldManToGetSentBack);
		cTalkToHorrifyingCreature.addStep(inAsylum, talkToHorrifyingCreature);
		steps.put(20, cTalkToHorrifyingCreature);

		var shouldDestroyGatestone1 = new VarbitRequirement(VarbitID.RAG_QUEST_3_GATESTONE_1, 0);
		var shouldDestroyGatestone2 = new VarbitRequirement(VarbitID.RAG_QUEST_3_GATESTONE_2, 0);
		var shouldDestroyGatestone3 = new VarbitRequirement(VarbitID.RAG_QUEST_3_GATESTONE_3, 0);
		var shouldDestroyGatestone4 = new VarbitRequirement(VarbitID.RAG_QUEST_3_GATESTONE_4, 0);
		var destroyGatestone1 = new ObjectStep(this, ObjectID.RAG3_GATESTONE_VIS, new WorldPoint(1364, 4790, 0), "Destroy the first gatestone just north-east of the horrifying creature.");
		var destroyGatestone2 = new ObjectStep(this, ObjectID.RAG3_GATESTONE_VIS, new WorldPoint(1384, 4789, 0), "Destroy the second gatestone.", earMuffsEquipped);
		var destroyGatestone3 = new ObjectStep(this, ObjectID.RAG3_GATESTONE_VIS, new WorldPoint(1397, 4745, 0), "Destroy the third gatestone.", earMuffsEquipped);
		var destroyGatestone4 = new ObjectStep(this, ObjectID.RAG3_GATESTONE_VIS, new WorldPoint(1356, 4739, 0), "Destroy the last gatestone. Avoid the shadows. If you get hit, you get teleported back and take some damage.", earMuffsEquipped);
		destroyGatestone4.addSafeSpots(
			new WorldPoint(1375, 4739, 0),
			new WorldPoint(1373, 4738, 0),
			new WorldPoint(1371, 4739, 0),
			new WorldPoint(1370, 4739, 0),
			new WorldPoint(1368, 4741, 0),
			new WorldPoint(1367, 4741, 0),
			new WorldPoint(1366, 4740, 0),
			new WorldPoint(1364, 4741, 0),
			new WorldPoint(1361, 4741, 0),
			new WorldPoint(1360, 4741, 0),
			new WorldPoint(1357, 4740, 0)
		);
		destroyGatestone4.setLinePoints(List.of(
			new WorldPoint(1380, 4739, 0),
			new WorldPoint(1375, 4739, 0),
			new WorldPoint(1375, 4738, 0),
			new WorldPoint(1371, 4738, 0),
			new WorldPoint(1371, 4739, 0),
			new WorldPoint(1370, 4739, 0),
			new WorldPoint(1369, 4740, 0),
			new WorldPoint(1368, 4741, 0),
			new WorldPoint(1367, 4741, 0),
			new WorldPoint(1366, 4740, 0),
			new WorldPoint(1365, 4740, 0),
			new WorldPoint(1364, 4741, 0),
			new WorldPoint(1363, 4740, 0),
			new WorldPoint(1362, 4740, 0),
			new WorldPoint(1361, 4741, 0),
			new WorldPoint(1360, 4741, 0),
			new WorldPoint(1359, 4740, 0),
			new WorldPoint(1357, 4740, 0)
		));

		var cBreakTheGatestones = new ConditionalStep(this, talkToOddOldManToGetSentBack);

		cBreakTheGatestones.addStep(and(inAsylum, shouldDestroyGatestone1), destroyGatestone1);

		var asylumKey = new ItemRequirement("Asylum key", ItemID.RAG3_KEY);
		var pickupAsylumKey = new ObjectStep(this, ObjectID.RAG3_ASYLUM_DESK, new WorldPoint(1353, 4787, 0), "Search the desk north-west of the Horrifying Creature for a key.");

		cBreakTheGatestones.addStep(and(inAsylum, not(asylumKey)), pickupAsylumKey);

		var goThroughEastDoor1 = new ObjectStep(this, ObjectID.RAG3_ASYLUM_GATE_LOCKED_KEY_1, new WorldPoint(1379, 4781, 0), "Go through the door to the east to kill the banshee.", earMuffsEquipped, asylumKey);

		var inA1 = new ZoneRequirement(new Zone(new WorldPoint(1379, 4779, 0), new WorldPoint(1404, 4799, 0)));
		var inA2 = new ZoneRequirement(new Zone(new WorldPoint(1379, 4799, 0), new WorldPoint(1404, 4743, 0)));
		var inA3 = new ZoneRequirement(new Zone(new WorldPoint(1395, 4745, 0), new WorldPoint(1401, 4746, 0)));
		var bansheeAlive = new NpcRequirement(NpcID.RAG3_BANSHEE);
		var killBanshee = new NpcStep(this, NpcID.RAG3_BANSHEE, "Kill the Ancient Banshee.", earMuffsEquipped, asylumKey);

		cBreakTheGatestones.addStep(and(inAsylum, shouldDestroyGatestone2, inA1, bansheeAlive), killBanshee);
		cBreakTheGatestones.addStep(and(inAsylum, shouldDestroyGatestone2, inA1), destroyGatestone2);
		cBreakTheGatestones.addStep(and(inAsylum, shouldDestroyGatestone2), goThroughEastDoor1);


		var combinationLockWidgetOpen = new WidgetPresenceRequirement(InterfaceID.CombinationLock.CONTENTS);
		var solveDoorLock = new ChestCodeStep(this, "door", "3118", 10, 3, 1, 1, 8);
		var solveDoorLockPW = solveDoorLock.puzzleWrapStep("Unlock the door. The answer is somewhere in this asylum.");
		var unlockDoor = new ObjectStep(this, ObjectID.RAG3_ASYLUM_GATE_LOCKED_CODE, new WorldPoint(1400, 4746, 0), "Unlock the door.");
		var doorUnlocked = new VarbitRequirement(VarbitID.RAG3_ASYLUM_GATE_CODE, 1);
		cBreakTheGatestones.addStep(and(inAsylum, shouldDestroyGatestone3, doorUnlocked), destroyGatestone3);
		cBreakTheGatestones.addStep(and(inAsylum, shouldDestroyGatestone3, combinationLockWidgetOpen), solveDoorLockPW);
		cBreakTheGatestones.addStep(and(inAsylum, shouldDestroyGatestone3, inA2), unlockDoor);
		cBreakTheGatestones.addStep(and(inAsylum, shouldDestroyGatestone3), goThroughEastDoor1);

		var leaveLittlePlace = new ObjectStep(this, ObjectID.RAG3_ASYLUM_GATE_LOCKED_CODE, new WorldPoint(1400, 4746, 0), "Head towards the last gatestone.");
		var headToLastGatestone = new ObjectStep(this, ObjectID.RAG3_ASYLUM_GATE_UNLOCKED, new WorldPoint(1390, 4742, 0), "Head towards the last gatestone.");
		cBreakTheGatestones.addStep(and(inAsylum, shouldDestroyGatestone4, inA3), leaveLittlePlace);
		cBreakTheGatestones.addStep(and(inAsylum, shouldDestroyGatestone4, inA2), headToLastGatestone);
		cBreakTheGatestones.addStep(and(inAsylum, shouldDestroyGatestone4), destroyGatestone4);

		// TODO: if not in asylum, return to odd old man
		steps.put(22, cBreakTheGatestones);

		var headlessSkeleton = new NpcStep(this, NpcID.RAG3_SKELETAL_HORROR, new WorldPoint(1374, 4830, 0), "Kill the Skeletal Horror. Use Protect from Melee to avoid most damage.");

		var talkBackToHorrifyingCreature = new ObjectStep(this, ObjectID.RAG3_ASYLUM_STAIRS_DOWN, new WorldPoint(1371, 4790, 0), "Climb downstairs, ready for a fight.", asylumKey);
		// TODO: if not in asylum, return to odd old man
		steps.put(24, talkBackToHorrifyingCreature);

		// TODO: if not in asylum, return to odd old man
		steps.put(26, headlessSkeleton);

		var talkToOddOldManToFinishQuest = new NpcStep(this, NpcID.RAG_ODD_OLD_MAN_NOSACK_VIS, new WorldPoint(3361, 3506, 0), "Talk to the Odd Old Man north-east of Varrock to finish the quest.");
		steps.put(28, talkToOddOldManToFinishQuest);

		// [2026-11-22T14:03:06Z 2393] varbit RAG_QUEST_3_DEATH_CHAT (16881) 0 -> 1
		// [2026-11-22T14:03:11Z 2401] varbit RAG_QUEST_3_ILLUSION_TIMES_REACHED_VOID (16885) 0 -> 1
		// [2026-11-22T14:03:11Z 2401] varbit RAG_QUEST_3_IN_ILLUSION (16891) 1 -> 2

		return steps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.PANDEMONIUM, QuestState.FINISHED),
			new SkillRequirement(Skill.SAILING, 45, false)
		);
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		return List.of(
			//
		);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			combatGear
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Ancient Banshee (lvl 113)",
			"Skeletal Horror (lvl 220)"
		);
	}

	@Override
	public List<String> getNotes()
	{
		return List.of(
			"This is a note to appear in the sidebar"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(3);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.PRAYER, 10000, true),
			new ExperienceReward(Skill.SLAYER, 10000, true)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to the upgraded bonesack")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			startQuest,
			armouredZombieBones,
			angryRatBones
		), List.of(
			combatGear
		)));

		sections.add(new PanelDetails("Death's Domain", List.of(
			talkToDeath,
			talkToRomeo
		)));

		sections.add(new PanelDetails("Tutorial Island", List.of(
			talkToGielenorGuide,
			talkToSurvivalInstructor,
			catchShrimpWithYourHands,
			talkToSurvivalInstructorAgain
		)));

		sections.add(new PanelDetails("Camdozaal", List.of(
			killTheLostSoulsSurroundingTheKing,
			talkToKing
		)));

		sections.add(new PanelDetails("The Wizard's Tower", List.of(
			talkToArchmageSedridor,
			makeBed,
			talkToArchmageSedridorAfterMakingTheBed
		)));

		sections.add(new PanelDetails("The Rift", List.of(
			cTolna
		)));

		sections.add(new PanelDetails("Escape!", List.of(
			cTolna
		)));

		return sections;
	}
}
