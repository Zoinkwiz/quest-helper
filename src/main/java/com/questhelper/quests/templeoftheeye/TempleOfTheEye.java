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

package com.questhelper.quests.templeoftheeye;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
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
	ItemRequirement varrock, alkharid;

	Requirement inAbyss, teleportedFromVarrock, inWizardBasement, teleportedFromPersten, inWizardFloorOne,
		apprenticeFelixPuzzle, apprenticeTamaraPuzzle, apprenticeCordeliaPuzzle, inTempleOfTheEye,
		felixRiftTalk, tamaraRiftTalk, cordeliaRiftTalk, mysteriousVision, inTempleOfTheEye2;

	QuestStep talkToPersten1, talkToPersten1b, talkToMage1, getTeaForMage, talkToMage2, talkToDarkMage1,
		talkToMageInWildy, talkToDarkMage2, talkToPersten2, teleportToArchmage, goDownToArchmage, talktoArchmage,
		finishTalkingToArchmage, goUpToTraibornBasement, goUpToTraiborn, talktoTrailborn1,
		talkToFelix, talkToTamara, talkToCordelia, talktoTrailborn2, goDownToArchmageFloorOne, goDownToArchmage2,
		talktoArchmage2, performIncantation, enterWizardBasement, enterPortal, templeCutscene1,
		talkToFelix2, talkToTamara2, talkToCordelia2, talkToPersten3, templeCutscene2, debrief, guardiansTutorial,
		templeCutscene3, finishQuest;

	ObjectStep touchRunes;

	//Zones
	Zone abyss, wizardBasement, wizardFloorOne, templeOfTheEye, templeOfTheEye2;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToPersten1);
		steps.put(5, talkToPersten1b);
		steps.put(10, talkToMage1);

		ConditionalStep goToAbyss = new ConditionalStep(this, getTeaForMage);
		goToAbyss.addStep(new Conditions(strongTea, bucketOfWater, eyeAmulet), talkToMage2);

		steps.put(15, goToAbyss);

		steps.put(20, talkToMage2);

		ConditionalStep teleportAbyss = new ConditionalStep(this, talkToMage2);
		teleportAbyss.addStep(inAbyss, talkToDarkMage1);
		teleportAbyss.addStep(teleportedFromVarrock, talkToMageInWildy);

		steps.put(25, teleportAbyss);

		steps.put(30, talkToDarkMage1);
		steps.put(35, touchRunes);
		steps.put(40, talkToDarkMage2);

		steps.put(45, talkToPersten2);
		steps.put(50, talkToPersten2);
		steps.put(55, talkToPersten2);

		ConditionalStep goTalkToArchmage = new ConditionalStep(this, goDownToArchmage);
		goTalkToArchmage.addStep(teleportedFromPersten, teleportToArchmage);
		goTalkToArchmage.addStep(inWizardBasement, talktoArchmage);
		goTalkToArchmage.addRequirement(abyssalIncantation);

		steps.put(60, goTalkToArchmage);

		steps.put(65, finishTalkingToArchmage);

		ConditionalStep goTalkToTraiborn = new ConditionalStep(this, goUpToTraiborn);
		goTalkToTraiborn.addStep(inWizardBasement, goUpToTraibornBasement);
		goTalkToTraiborn.addStep(inWizardFloorOne, talktoTrailborn1);

		steps.put(70, goTalkToTraiborn);

		ConditionalStep solveTraibornsPuzzle = new ConditionalStep(this, goUpToTraiborn);
		solveTraibornsPuzzle.addStep(new Conditions(inWizardFloorOne, apprenticeFelixPuzzle), talkToFelix);
		solveTraibornsPuzzle.addStep(new Conditions(inWizardFloorOne, apprenticeTamaraPuzzle), talkToTamara);
		solveTraibornsPuzzle.addStep(new Conditions(inWizardFloorOne, apprenticeCordeliaPuzzle), talkToCordelia);

		steps.put(75, solveTraibornsPuzzle);

		steps.put(80, talktoTrailborn2);

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
		goTalkToPerstenTemple.addStep(mysteriousVision, templeCutscene2);

		steps.put(105, goTalkToPerstenTemple);

		ConditionalStep debriefAfterVision = new ConditionalStep(this, enterWizardBasement);
		debriefAfterVision.addStep(inWizardBasement, enterPortal);
		debriefAfterVision.addStep(inTempleOfTheEye, debrief);

		steps.put(110, debriefAfterVision);

		ConditionalStep doGuardiansTutorial = new ConditionalStep(this, enterWizardBasement);
		doGuardiansTutorial.addStep(inWizardBasement, enterPortal);
		doGuardiansTutorial.addStep(inTempleOfTheEye2, guardiansTutorial);

		steps.put(115, doGuardiansTutorial);	// At this stage, the player is handheld by in-game tutorial.

		ConditionalStep finishGuardiansTutorial = new ConditionalStep(this, enterWizardBasement);
		finishGuardiansTutorial.addStep(inWizardBasement, enterPortal);
		finishGuardiansTutorial.addStep(inTempleOfTheEye2, templeCutscene3);

		steps.put(120, finishGuardiansTutorial);

		ConditionalStep doFinishQuest = new ConditionalStep(this, enterWizardBasement);
		doFinishQuest.addStep(inWizardBasement, finishQuest);

		steps.put(125, doFinishQuest);

		return steps;
	}

	public void setupItemRequirements()
	{
		bucketOfWater = new ItemRequirement("Bucket of water", ItemID.BUCKET_OF_WATER);
		bucketOfWater.setHighlightInInventory(true);
		chisel = new ItemRequirement("Chisel", ItemID.CHISEL);
		chisel.canBeObtainedDuringQuest();
		pickaxe = new ItemRequirement("Pickaxe", ItemCollections.getPickaxes());
		pickaxe.canBeObtainedDuringQuest();

		varrock = new ItemRequirement("Method of teleportation to Varrock", ItemID.VARROCK_TELEPORT);
		alkharid = new ItemRequirement("Method of teleportation to Al Kharid", ItemID.LUMBRIDGE_TELEPORT);

		strongTea = new ItemRequirement("Strong Cup of Tea", ItemID.STRONG_CUP_OF_TEA);
		eyeAmulet = new ItemRequirement("Eye Amulet", ItemID.EYE_AMULET);
		abyssalIncantation = new ItemRequirement("Abyssal Incantation", ItemID.ABYSSAL_INCANTATION);

	}

	public void setupConditions()
	{
		inAbyss = new ZoneRequirement(abyss);
		inWizardBasement = new ZoneRequirement(wizardBasement);
		inWizardFloorOne = new ZoneRequirement(wizardFloorOne);
		inTempleOfTheEye = new ZoneRequirement(templeOfTheEye);
		inTempleOfTheEye2 = new ZoneRequirement(templeOfTheEye2);

		teleportedFromVarrock = new VarbitRequirement(13740, 1);
		teleportedFromPersten = new VarbitRequirement(13753, 0);

		apprenticeFelixPuzzle = new VarbitRequirement(13743, 0);
		apprenticeTamaraPuzzle = new VarbitRequirement(13742, 0);
		apprenticeCordeliaPuzzle = new VarbitRequirement(13744, 0);

		felixRiftTalk = new VarbitRequirement(13755, 0);
		tamaraRiftTalk = new VarbitRequirement(13754, 0);
		cordeliaRiftTalk = new VarbitRequirement(13756, 0);

		mysteriousVision = new VarbitRequirement(12139, 1);
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

		talkToPersten1b = new NpcStep(this, NpcID.WIZARD_PERSTEN, new WorldPoint(3285, 3232, 0),
			"Talk to Wizard Persten east of the Al Kharid gate.");

		talkToPersten1.addSubSteps(talkToPersten1b);

		talkToMage1 = new NpcStep(this, NpcID.MAGE_OF_ZAMORAK_2582, new WorldPoint(3258, 3383, 0),
			"Talk to Mage of Zamorak in the Varrock chaos temple.",
			eyeAmulet);
		talkToMage1.addDialogStep("I need your help with an amulet.");

		getTeaForMage = new NpcStep(this, NpcID.TEA_SELLER, new WorldPoint(3271, 3411, 0),
			"Talk to Tea Seller near the Varrock east gate.");
		getTeaForMage.addDialogStep("Could I have a strong cup of tea?");

		talkToMage2 = new NpcStep(this, NpcID.MAGE_OF_ZAMORAK_2582, new WorldPoint(3258, 3383, 0),
			"Give the strong tea to the Mage of Zamorak in the Varrock chaos temple.",
			strongTea);
		talkToMage2.addDialogStep("Could you help me with that amulet now?");
		talkToMage2.addDialogStep("Yes.");

		talkToDarkMage1 = new NpcStep(this, NpcID.DARK_MAGE, new WorldPoint(3039, 4834, 0),
			"Talk to Dark Mage in the Abyss",
			eyeAmulet, bucketOfWater);
		talkToDarkMage1.addDialogStep("I need your help with an amulet.");

		talkToMageInWildy = new NpcStep(this, NpcID.MAGE_OF_ZAMORAK, new WorldPoint(3102, 3557, 0), "Talk to the Mage" +
			" of Zamorak in the Wilderness north of Edgeville. BRING NOTHING BUT QUEST ITEMS AS YOU CAN BE KILLED BY OTHER PLAYERS HERE.",
			eyeAmulet, bucketOfWater);
		talkToMageInWildy.addDialogStep("Could you teleport me to the Abyss?");

		touchRunes = new ObjectStep(this, 43768,
			"Interact with the runic energy (pattern is different for everyone). Click each rune type until all" +
				" turn white.");
		touchRunes.addAlternateObjects(43769, 43770, 43771, 43772, 43773);
		touchRunes.setHideWorldArrow(true);

		talkToDarkMage2 = new NpcStep(this, NpcID.DARK_MAGE, new WorldPoint(3039, 4834, 0),
			"Talk to Dark Mage in the Abyss.");

		talkToPersten2 = new NpcStep(this, NpcID.WIZARD_PERSTEN, new WorldPoint(3285, 3232, 0),
			"Talk to Wizard Persten east of the Al Kharid gate.",
			abyssalIncantation);

		teleportToArchmage = new NpcStep(this, NpcID.WIZARD_PERSTEN, new WorldPoint(3285, 3232, 0),
			"Ask Wizard Persten to teleport you to the Wizard's Tower.");
		teleportToArchmage.addDialogStep("Yes.");

		goDownToArchmage = new ObjectStep(this, ObjectID.LADDER_2147, new WorldPoint(3104, 3162, 0),
			"Bring the Abyssal Incantation to Sedridor in the Wizard Tower's basement.");

		talktoArchmage = new NpcStep(this, NpcID.ARCHMAGE_SEDRIDOR_11433, new WorldPoint(3104, 9571, 0),
			"Bring the Abyssal Incantation to Sedridor in the Wizard Tower's basement.");
		talktoArchmage.addDialogStep("I need your help with an incantation.");

		finishTalkingToArchmage = new NpcStep(this, NpcID.ARCHMAGE_SEDRIDOR_11433, new WorldPoint(3104, 9571, 0),
			"Finish listening to Sedridor.");
		finishTalkingToArchmage.addDialogStep("I need your help with an incantation.");

		talktoArchmage.addSubSteps(teleportToArchmage, goDownToArchmage, finishTalkingToArchmage);

		goUpToTraibornBasement = new ObjectStep(this, ObjectID.LADDER_2148, new WorldPoint(3103, 9576, 0),
			"Speak to Wizard Traiborn on the Wizard's Tower 1st floor.");

		goUpToTraiborn = new ObjectStep(this, ObjectID.STAIRCASE_12536, new WorldPoint(3103, 3159, 0),
			"Speak to Wizard Traiborn on the Wizard's Tower 1st floor.");

		talktoTrailborn1 = new NpcStep(this, NpcID.WIZARD_TRAIBORN, new WorldPoint(3112, 3162, 1),
			"Speak to Wizard Traiborn on the Wizard's Tower 1st floor.");
		talktoTrailborn1.addDialogStep("I need your apprentices to help with an incantation.");

		talktoTrailborn1.addSubSteps(goUpToTraibornBasement, goUpToTraiborn);

		talkToFelix = new NpcStep(this, NpcID.APPRENTICE_FELIX_11446, new WorldPoint(3112, 3162, 1),
			"Get puzzle from Apprentice Felix.");
		talkToTamara = new NpcStep(this, NpcID.APPRENTICE_TAMARA, new WorldPoint(3112, 3162, 1),
			"Get puzzle from Apprentice Tamara.");
		talkToCordelia = new NpcStep(this, NpcID.APPRENTICE_CORDELIA_11443, new WorldPoint(3112, 3162, 1),
			"Get puzzle from Apprentice Cordelia.");

		talktoTrailborn2 = new NpcStep(this, NpcID.WIZARD_TRAIBORN, new WorldPoint(3112, 3162, 1),
			"Tell Wizard Traiborn the solution to his puzzle is: 11");
		talktoTrailborn2.addDialogStep("I think I know what a thingummywut is!");

		goDownToArchmageFloorOne = new ObjectStep(this, ObjectID.STAIRCASE_12537, new WorldPoint(3103, 3159, 1),
			"Speak to Archmage Sedridor in the Wizard's Tower basement.");

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
		reqs.add(varrock);
		reqs.add(alkharid);
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
		allSteps.add(new PanelDetails("An Eye for a Favour", Arrays.asList(talkToPersten1, talkToPersten1b), Collections.emptyList(), Arrays.asList(varrock)));
		allSteps.add(new PanelDetails("Herbert and the Dark Mage", Arrays.asList(talkToMage1, getTeaForMage, talkToMage2, talkToDarkMage1, touchRunes, talkToDarkMage2), Arrays.asList(bucketOfWater, eyeAmulet), Arrays.asList(alkharid)));
		allSteps.add(new PanelDetails("Help from Some Wizards", Arrays.asList(talkToPersten2, talktoArchmage, talktoTrailborn1, talkToFelix, talkToTamara, talkToCordelia, talktoTrailborn2), abyssalIncantation));
		allSteps.add(new PanelDetails("Enter the Gate", Arrays.asList(talktoArchmage2, performIncantation, templeCutscene1, talkToFelix2, talkToTamara2, talkToCordelia2, talkToPersten3, templeCutscene2, debrief)));
		allSteps.add(new PanelDetails("Guardians of the Rift", Arrays.asList(guardiansTutorial, finishQuest), pickaxe, chisel));

		return allSteps;
	}
}