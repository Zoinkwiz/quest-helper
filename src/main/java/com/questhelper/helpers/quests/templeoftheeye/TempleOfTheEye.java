/*
 * Copyright (c) 2022, zct8002 <https://github.com/zct8002>
 * Copyright (c) 2022, Fincap <https://github.com/Fincap>
 * Copyright (c) 2022, Zoinkwiz <https://github.com/Zoinkwiz>
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

package com.questhelper.helpers.quests.templeoftheeye;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarbits;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
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
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

/*
VARBITS
-------
13738: Quest Step
13739: Told to see Herbert
13740: One-time abyss teleport
13741: Received cup of tea
13742: Apprentice Tamara puzzle
13743: Apprentice Felix puzzle
13744: Apprentice Cordelia puzzle
13745: Quest offered
13747 - 13752: Energy orbs
13753: One-time Wizard's Tower teleport
13759: Guardians of the Rift Tutorial progress
 */

@QuestDescriptor(
	quest = QuestHelperQuest.TEMPLE_OF_THE_EYE
)
public class TempleOfTheEye extends BasicQuestHelper
{
	//Items Required
	ItemRequirement bucketOfWater, strongTea, eyeAmulet, chisel, pickaxe, abyssalIncantation;

	//Items Recommended
	ItemRequirement varrockTeleport, alKharidTeleport;

	Requirement inAbyss, canTeleportFromHerbert, thrownBucket, givenAmuletBack, inWizardBasement, canTeleportFromPersten,
		inWizardFloorOne, felixPuzzleNotSeen, tamaraPuzzleNotSeen, cordeliaPuzzleNotSeen, inTempleOfTheEye,
		felixRiftTalk, tamaraRiftTalk, cordeliaRiftTalk, mysteriousVisionSeen, inTempleOfTheEyeTutorial;

	QuestStep talkToPersten1, finishTalkToPersten1, talkToMage1, getTeaForMage, talkToMage2, finishTalkToMage2,
		teleportViaHerbert, talkToDarkMage1, finishTalkToDarkMage1, talkToMageInWildy, talkToDarkMage2, talkToPersten2,
		finishTalkToPersten2, teleportToArchmage, goDownToArchmage, talktoArchmage1, finishTalkingToArchmage1,
		goUpToTraibornBasement, goUpToTraiborn, talktoTrailborn1, talkToFelix, talkToTamara, talkToCordelia,
		talktoTrailborn2, goDownToArchmageFloorOne, goDownToArchmage2, talktoArchmage2, performIncantation,
		enterWizardBasement, enterPortal, templeCutscene1, talkToFelix2, talkToTamara2, talkToCordelia2, talkToPersten3,
		templeCutscene2, debrief, guardiansTutorial, templeCutscene3, finishQuest;

	ObjectStep touchRunes;

	//Zones
	Zone abyss, wizardBasement, wizardFloorOne, templeOfTheEye, templeOfTheEye2;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupZones();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToPersten1);
		steps.put(5, finishTalkToPersten1);
		steps.put(10, talkToMage1);

		ConditionalStep fetchHerbertsTea = new ConditionalStep(this, getTeaForMage);
		fetchHerbertsTea.addStep(new Conditions(strongTea, bucketOfWater, eyeAmulet), talkToMage2);
		steps.put(15, fetchHerbertsTea);

		steps.put(20, finishTalkToMage2);

		ConditionalStep teleportAbyss = new ConditionalStep(this, talkToMageInWildy);
		teleportAbyss.addStep(inAbyss, talkToDarkMage1);
		teleportAbyss.addStep(canTeleportFromHerbert, teleportViaHerbert);
		teleportAbyss.addRequirement(bucketOfWater);
		steps.put(25, teleportAbyss);

		ConditionalStep goTalkToDarkMage1 = new ConditionalStep(this, talkToMageInWildy);
		goTalkToDarkMage1.addStep(inAbyss, finishTalkToDarkMage1);
		steps.put(30, goTalkToDarkMage1);

		ConditionalStep goTouchRunes = new ConditionalStep(this, talkToMageInWildy);
		goTouchRunes.addStep(inAbyss, touchRunes);
		steps.put(35, goTouchRunes);

		ConditionalStep goTalkToDarkMage2 = new ConditionalStep(this, talkToMageInWildy);
		goTalkToDarkMage2.addStep(inAbyss, talkToDarkMage2);
		steps.put(40, goTalkToDarkMage2);

		steps.put(45, talkToPersten2);
		steps.put(50, talkToPersten2);
		steps.put(55, finishTalkToPersten2);

		ConditionalStep goTalkToArchmage = new ConditionalStep(this, goDownToArchmage);
		goTalkToArchmage.addStep(canTeleportFromPersten, teleportToArchmage);
		goTalkToArchmage.addStep(inWizardBasement, talktoArchmage1);
		goTalkToArchmage.addRequirement(abyssalIncantation);
		steps.put(60, goTalkToArchmage);

		steps.put(65, finishTalkingToArchmage1);

		ConditionalStep goTalkToTraiborn1 = new ConditionalStep(this, goUpToTraiborn);
		goTalkToTraiborn1.addStep(inWizardBasement, goUpToTraibornBasement);
		goTalkToTraiborn1.addStep(inWizardFloorOne, talktoTrailborn1);
		steps.put(70, goTalkToTraiborn1);

		ConditionalStep solveTraibornsPuzzle = new ConditionalStep(this, goUpToTraiborn);
		solveTraibornsPuzzle.addStep(new Conditions(inWizardFloorOne, felixPuzzleNotSeen), talkToFelix);
		solveTraibornsPuzzle.addStep(new Conditions(inWizardFloorOne, tamaraPuzzleNotSeen), talkToTamara);
		solveTraibornsPuzzle.addStep(new Conditions(inWizardFloorOne, cordeliaPuzzleNotSeen), talkToCordelia);
		steps.put(75, solveTraibornsPuzzle);

		ConditionalStep goTalkToTraiborn2 = new ConditionalStep(this, goUpToTraiborn);
		goTalkToTraiborn2.addStep(inWizardBasement, goUpToTraibornBasement);
		goTalkToTraiborn2.addStep(inWizardFloorOne, talktoTrailborn2);
		steps.put(80, goTalkToTraiborn2);

		ConditionalStep goTalkToArchmage2 = new ConditionalStep(this, goDownToArchmage2);
		goTalkToArchmage2.addStep(inWizardFloorOne, goDownToArchmageFloorOne);
		goTalkToArchmage2.addStep(inWizardBasement, talktoArchmage2);
		steps.put(85, goTalkToArchmage2);

		ConditionalStep goBeginIncantation = new ConditionalStep(this, goDownToArchmage2);
		goBeginIncantation.addStep(inWizardBasement, performIncantation);
		steps.put(90, goBeginIncantation);

		steps.put(95, templeCutscene1);

		ConditionalStep investigateTemple = new ConditionalStep(this, enterWizardBasement);
		investigateTemple.addStep(inWizardBasement, enterPortal);
		investigateTemple.addStep(new Conditions(inTempleOfTheEye, felixRiftTalk), talkToFelix2);
		investigateTemple.addStep(new Conditions(inTempleOfTheEye, tamaraRiftTalk), talkToTamara2);
		investigateTemple.addStep(new Conditions(inTempleOfTheEye, cordeliaRiftTalk), talkToCordelia2);
		steps.put(100, investigateTemple);

		ConditionalStep goTalkToPerstenTemple = new ConditionalStep(this, enterWizardBasement);
		goTalkToPerstenTemple.addStep(inWizardBasement, enterPortal);
		goTalkToPerstenTemple.addStep(inTempleOfTheEye, talkToPersten3);
		goTalkToPerstenTemple.addStep(mysteriousVisionSeen, templeCutscene2);
		steps.put(105, goTalkToPerstenTemple);

		ConditionalStep debriefAfterVision = new ConditionalStep(this, enterWizardBasement);
		debriefAfterVision.addStep(inWizardBasement, enterPortal);
		debriefAfterVision.addStep(inTempleOfTheEye, debrief);
		steps.put(110, debriefAfterVision);

		ConditionalStep doGuardiansTutorial = new ConditionalStep(this, enterWizardBasement);
		doGuardiansTutorial.addStep(inWizardBasement, enterPortal);
		doGuardiansTutorial.addStep(inTempleOfTheEyeTutorial, guardiansTutorial);
		steps.put(115, doGuardiansTutorial);    // At this stage, the player is handheld by in-game tutorial.

		ConditionalStep finishGuardiansTutorial = new ConditionalStep(this, enterWizardBasement);
		finishGuardiansTutorial.addStep(inWizardBasement, enterPortal);
		finishGuardiansTutorial.addStep(inTempleOfTheEyeTutorial, templeCutscene3);
		steps.put(120, finishGuardiansTutorial);

		ConditionalStep doFinishQuest = new ConditionalStep(this, enterWizardBasement);
		doFinishQuest.addStep(inWizardBasement, finishQuest);
		steps.put(125, doFinishQuest);

		return steps;
	}

	public void setupRequirements()
	{
		bucketOfWater = new ItemRequirement("Bucket of water", ItemID.BUCKET_OF_WATER);
		chisel = new ItemRequirement("Chisel", ItemID.CHISEL);
		chisel.canBeObtainedDuringQuest();
		pickaxe = new ItemRequirement("Pickaxe", ItemCollections.PICKAXES);
		pickaxe.canBeObtainedDuringQuest();

		varrockTeleport = new ItemRequirement("Method of teleportation to Varrock", ItemID.VARROCK_TELEPORT);
		alKharidTeleport = new ItemRequirement("Method of teleportation to Al Kharid", ItemCollections.RING_OF_DUELINGS);
		alKharidTeleport.addAlternates(ItemCollections.AMULET_OF_GLORIES);
		alKharidTeleport.addAlternates(ItemID.LUMBRIDGE_TELEPORT);

		strongTea = new ItemRequirement("Strong Cup of Tea", ItemID.STRONG_CUP_OF_TEA);
		eyeAmulet = new ItemRequirement("Eye Amulet", ItemID.EYE_AMULET);
		eyeAmulet.setTooltip("You can get another from Wizard Persten if you lost it");
		abyssalIncantation = new ItemRequirement("Abyssal Incantation", ItemID.ABYSSAL_INCANTATION);
		abyssalIncantation.setTooltip("You can get another from the Dark Mage in the Abyss if you lost it. If already" +
			" shown to Wizard Persten, you can get another from her instead");

	}

	public void setupConditions()
	{
		inAbyss = new ZoneRequirement(abyss);
		inWizardBasement = new ZoneRequirement(wizardBasement);
		inWizardFloorOne = new ZoneRequirement(wizardFloorOne);
		inTempleOfTheEye = new ZoneRequirement(templeOfTheEye);
		// TODO: this should also consider the mind altar as part of the area
		inTempleOfTheEyeTutorial = new ZoneRequirement(templeOfTheEye2);

		canTeleportFromHerbert = new VarbitRequirement(13740, 0);
		thrownBucket = new VarbitRequirement(QuestVarbits.QUEST_TEMPLE_OF_THE_EYE.getId(), 30, Operation.GREATER_EQUAL);
		givenAmuletBack = new VarbitRequirement(QuestVarbits.QUEST_TEMPLE_OF_THE_EYE.getId(), 55, Operation.GREATER_EQUAL);
		canTeleportFromPersten = new VarbitRequirement(13753, 0);

		felixPuzzleNotSeen = new VarbitRequirement(13743, 0);
		tamaraPuzzleNotSeen = new VarbitRequirement(13742, 0);
		cordeliaPuzzleNotSeen = new VarbitRequirement(13744, 0);

		felixRiftTalk = new VarbitRequirement(13755, 0);
		tamaraRiftTalk = new VarbitRequirement(13754, 0);
		cordeliaRiftTalk = new VarbitRequirement(13756, 0);

		mysteriousVisionSeen = new VarbitRequirement(12139, 1);
	}

	public void setupZones()
	{
		abyss = new Zone(new WorldPoint(3010, 4803, 0), new WorldPoint(3070, 4862, 0));
		wizardBasement = new Zone(new WorldPoint(3094, 9553, 0), new WorldPoint(3125, 9582, 0));
		wizardFloorOne = new Zone(new WorldPoint(3101, 3153, 1), new WorldPoint(3116, 3167, 1));
		templeOfTheEye = new Zone(new WorldPoint(2370, 5627, 0), new WorldPoint(2425, 5682, 0));
		templeOfTheEye2 = new Zone(new WorldPoint(2433, 5698, 0), new WorldPoint(3648, 9523, 0));
	}

	public void setupSteps()
	{
		talkToPersten1 = new NpcStep(this, NpcID.WIZARD_PERSTEN, new WorldPoint(3285, 3232, 0),
			"Talk to Wizard Persten east of the Al Kharid gate.");
		talkToPersten1.addDialogStep("What's a wizard doing in Al Kharid?");
		talkToPersten1.addDialogStep("Yes.");

		finishTalkToPersten1 = new NpcStep(this, NpcID.WIZARD_PERSTEN, new WorldPoint(3285, 3232, 0),
			"Talk to Wizard Persten east of the Al Kharid gate.");

		talkToPersten1.addSubSteps(finishTalkToPersten1);

		// Note: You also can't be wearing any other god's equipment when talking to him
		talkToMage1 = new NpcStep(this, NpcID.MAGE_OF_ZAMORAK_2582, new WorldPoint(3258, 3383, 0),
			"Talk to Mage of Zamorak in the Varrock chaos temple.",
			eyeAmulet);
		talkToMage1.addDialogStep("I need your help with an amulet.");

		getTeaForMage = new NpcStep(this, NpcID.TEA_SELLER, new WorldPoint(3271, 3411, 0),
			"Talk to Tea Seller near the Varrock east gate.");
		getTeaForMage.addDialogStep("Could I have a strong cup of tea?");

		talkToMage2 = new NpcStep(this, NpcID.MAGE_OF_ZAMORAK_2582, new WorldPoint(3258, 3383, 0),
			"Give the strong tea to the Mage of Zamorak in the Varrock chaos temple.",
			strongTea, bucketOfWater, eyeAmulet);
		talkToMage2.addDialogStep("Could you help me with that amulet now?");
		talkToMage2.addDialogStep("Yes.");

		finishTalkToMage2 = new NpcStep(this, NpcID.MAGE_OF_ZAMORAK_2582, new WorldPoint(3258, 3383, 0),
			"Talk to Mage of Zamorak in the Varrock chaos temple.",
			bucketOfWater.hideConditioned(thrownBucket), eyeAmulet);
		talkToMage2.addDialogStep("Could you help me with that amulet now?");

		talkToMage2.addSubSteps(finishTalkToMage2);

		teleportViaHerbert = new NpcStep(this, NpcID.MAGE_OF_ZAMORAK_2582, new WorldPoint(3258, 3383, 0),
			"Ask the Mage of Zamorak in Varrock to teleport you to the Abyss (this can only be used once).",
			bucketOfWater.hideConditioned(thrownBucket), eyeAmulet);
		teleportViaHerbert.addDialogStep("Could you help me with that amulet now?");
		teleportViaHerbert.addDialogStep("Yes.");

		talkToMageInWildy = new NpcStep(this, NpcID.MAGE_OF_ZAMORAK_2581, new WorldPoint(3102, 3557, 0),
			"Return to the Abyss by talking to the Mage of Zamorak in the Wilderness north of Edgeville. " +
				"BRING NOTHING BUT QUEST ITEMS AS YOU CAN BE KILLED BY OTHER PLAYERS HERE.",
			bucketOfWater.hideConditioned(thrownBucket), eyeAmulet);
		talkToMageInWildy.addDialogStep("Could you teleport me to the Abyss?");

		talkToDarkMage1 = new NpcStep(this, NpcID.DARK_MAGE, new WorldPoint(3039, 4834, 0),
			"Talk to Dark Mage in the Abyss.",
			bucketOfWater.hideConditioned(thrownBucket), eyeAmulet);
		talkToDarkMage1.addDialogStep("I need your help with an amulet.");

		finishTalkToDarkMage1 = new NpcStep(this, NpcID.DARK_MAGE, new WorldPoint(3039, 4834, 0),
			"Talk to Dark Mage in the Abyss.",
			bucketOfWater.hideConditioned(thrownBucket), eyeAmulet);

		talkToDarkMage1.addSubSteps(talkToMageInWildy, teleportViaHerbert, finishTalkToDarkMage1);

		touchRunes = new ObjectStep(this, 43768,
			"Interact with the runic energy (pattern is different for everyone). Click each rune type until all" +
				" turn white.", eyeAmulet);
		touchRunes.addAlternateObjects(43769, 43770, 43771, 43772, 43773);
		touchRunes.setHideWorldArrow(true);

		talkToDarkMage2 = new NpcStep(this, NpcID.DARK_MAGE, new WorldPoint(3039, 4834, 0),
			"Talk to Dark Mage in the Abyss.",
			eyeAmulet);

		talkToPersten2 = new NpcStep(this, NpcID.WIZARD_PERSTEN, new WorldPoint(3285, 3232, 0),
			"Talk to Wizard Persten east of the Al Kharid gate.",
			eyeAmulet.hideConditioned(givenAmuletBack), abyssalIncantation);
		talkToPersten2.addDialogStep("About that incantation...");

		finishTalkToPersten2 = new NpcStep(this, NpcID.WIZARD_PERSTEN, new WorldPoint(3285, 3232, 0),
			"Talk to Wizard Persten east of the Al Kharid gate.",
			eyeAmulet.hideConditioned(givenAmuletBack), abyssalIncantation);

		talkToPersten2.addSubSteps(finishTalkToPersten2);

		teleportToArchmage = new NpcStep(this, NpcID.WIZARD_PERSTEN, new WorldPoint(3285, 3232, 0),
			"Ask Wizard Persten to teleport you to the Wizards' Tower.");
		teleportToArchmage.addDialogStep("Yes.");

		goDownToArchmage = new ObjectStep(this, ObjectID.LADDER_2147, new WorldPoint(3104, 3162, 0),
			"Bring the Abyssal Incantation to Sedridor in the Wizard Tower's basement.", abyssalIncantation);

		talktoArchmage1 = new NpcStep(this, NpcID.ARCHMAGE_SEDRIDOR_11433, new WorldPoint(3104, 9571, 0),
			"Bring the Abyssal Incantation to Sedridor in the Wizard Tower's basement.", abyssalIncantation);
		talktoArchmage1.addDialogStep("I need your help with an incantation.");

		finishTalkingToArchmage1 = new NpcStep(this, NpcID.ARCHMAGE_SEDRIDOR_11433, new WorldPoint(3104, 9571, 0),
			"Finish listening to Sedridor.");
		finishTalkingToArchmage1.addDialogSteps("I need your help with an incantation.",
			"Can you help me with that incantation?");

		talktoArchmage1.addSubSteps(teleportToArchmage, goDownToArchmage, finishTalkingToArchmage1);

		goUpToTraibornBasement = new ObjectStep(this, ObjectID.LADDER_2148, new WorldPoint(3103, 9576, 0),
			"Speak to Wizard Traiborn on the Wizards' Tower 1st floor.");

		goUpToTraiborn = new ObjectStep(this, ObjectID.STAIRCASE_12536, new WorldPoint(3103, 3159, 0),
			"Speak to Wizard Traiborn on the Wizards' Tower 1st floor.");

		talktoTrailborn1 = new NpcStep(this, NpcID.WIZARD_TRAIBORN, new WorldPoint(3112, 3162, 1),
			"Speak to Wizard Traiborn on the Wizards' Tower 1st floor.");
		talktoTrailborn1.addDialogStep("I need your apprentices to help with an incantation.");

		talktoTrailborn1.addSubSteps(goUpToTraibornBasement, goUpToTraiborn);

		talkToFelix = new NpcStep(this, NpcID.APPRENTICE_FELIX_11446, new WorldPoint(3112, 3162, 1),
			"Get puzzle from Apprentice Felix.");
		talkToTamara = new NpcStep(this, NpcID.APPRENTICE_TAMARA, new WorldPoint(3112, 3162, 1),
			"Get puzzle from Apprentice Tamara.");
		talkToCordelia = new NpcStep(this, NpcID.APPRENTICE_CORDELIA, new WorldPoint(3112, 3162, 1),
			"Get puzzle from Apprentice Cordelia.");

		talktoTrailborn2 = new NpcStep(this, NpcID.WIZARD_TRAIBORN, new WorldPoint(3112, 3162, 1),
			"Tell Wizard Traiborn the solution to his puzzle is: 11");
		talktoTrailborn2.addDialogStep("I think I know what a thingummywut is!");

		goDownToArchmageFloorOne = new ObjectStep(this, ObjectID.STAIRCASE_12537, new WorldPoint(3103, 3159, 1),
			"Speak to Archmage Sedridor in the Wizard's Tower basement.");
		goDownToArchmageFloorOne.addDialogStep("Down");

		goDownToArchmage2 = new ObjectStep(this, ObjectID.LADDER_2147, new WorldPoint(3104, 3162, 0),
			"Speak to Archmage Sedridor in the Wizard's Tower basement.");

		talktoArchmage2 = new NpcStep(this, NpcID.ARCHMAGE_SEDRIDOR_11433, new WorldPoint(3104, 9571, 0),
			"Speak to Archmage Sedridor in the Wizard's Tower basement.");

		talktoArchmage2.addSubSteps(goDownToArchmageFloorOne, goDownToArchmage2);

		performIncantation = new NpcStep(this, NpcID.ARCHMAGE_SEDRIDOR_11433, new WorldPoint(3104, 9571, 0),
			"Speak to Archmage Sedridor to begin the incantation.");
		performIncantation.addDialogStep("Let's do it.");
		performIncantation.addDialogStep("So we're ready to perform the incantation?");

		enterWizardBasement = new ObjectStep(this, ObjectID.LADDER_2147, new WorldPoint(3104, 3162, 0),
			"Go to the Wizard's Tower basement.");

		enterPortal = new ObjectStep(this, ObjectID.PORTAL_43765, new WorldPoint(3104, 9574, 0),
			"Enter the portal to the Temple of the Eye.");

		templeCutscene1 = new DetailedQuestStep(this, "Enter the Temple of the Eye.");
		templeCutscene1.addSubSteps(enterPortal);

		talkToFelix2 = new NpcStep(this, NpcID.APPRENTICE_FELIX_11448, new WorldPoint(2401, 5643, 0),
			"Talk to Apprentice Felix.");
		talkToTamara2 = new NpcStep(this, NpcID.APPRENTICE_TAMARA_11442, new WorldPoint(2385, 5659, 0),
			"Talk to Apprentice Tamara.");
		talkToCordelia2 = new NpcStep(this, NpcID.APPRENTICE_CORDELIA_11445, new WorldPoint(2397, 5677, 0),
			"Talk to Apprentice Cordelia.");

		talkToPersten3 = new NpcStep(this, NpcID.WIZARD_PERSTEN_11439, new WorldPoint(2400, 5667, 0),
			"Speak with Wizard Persten.");

		templeCutscene2 = new DetailedQuestStep(this, "Experience the vision.");
		debrief = new DetailedQuestStep(this, "Debrief with Wizard Persten and the apprentices.");
		guardiansTutorial = new DetailedQuestStep(this, "Complete the Guardians of the Rift tutorial.", pickaxe, chisel);

		templeCutscene3 = new DetailedQuestStep(this, "Listen to the Great Guardian.");

		guardiansTutorial.addSubSteps(templeCutscene3);

		finishQuest = new NpcStep(this, NpcID.ARCHMAGE_SEDRIDOR_11433, new WorldPoint(3104, 9571, 0),
			"Speak to Archmage Sedridor in the Wizard's Tower basement to finish the quest.");

	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(bucketOfWater);
		reqs.add(chisel);
		reqs.add(pickaxe);
		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(varrockTeleport);
		reqs.add(alKharidTeleport);
		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("Medium pouch", ItemID.MEDIUM_POUCH));
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.RUNECRAFT, 9210));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to the Guardians of the Rift minigame"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("An Eye for a Favour", Arrays.asList(talkToPersten1), Collections.emptyList(), Arrays.asList(varrockTeleport)));
		allSteps.add(new PanelDetails("Herbert and the Dark Mage", Arrays.asList(talkToMage1, getTeaForMage, talkToMage2, talkToDarkMage1, touchRunes, talkToDarkMage2), Arrays.asList(bucketOfWater, eyeAmulet), Arrays.asList(alKharidTeleport)));
		allSteps.add(new PanelDetails("Help from Some Wizards", Arrays.asList(talkToPersten2, talktoArchmage1, talktoTrailborn1, talkToFelix, talkToTamara, talkToCordelia, talktoTrailborn2), abyssalIncantation));
		allSteps.add(new PanelDetails("Enter the Gate", Arrays.asList(talktoArchmage2, performIncantation, templeCutscene1, talkToFelix2, talkToTamara2, talkToCordelia2, talkToPersten3, templeCutscene2, debrief)));
		allSteps.add(new PanelDetails("Guardians of the Rift", Arrays.asList(guardiansTutorial, finishQuest), pickaxe, chisel));

		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.ENTER_THE_ABYSS, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.RUNECRAFT, 10));
		return req;
	}
}
