/*
 * Copyright (c) 2026, NJKimb <https://github.com/NJKimb>
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
package com.questhelper.helpers.quests.thebirdsandthewasps;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ExperienceReward;
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
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

public class TheBirdsAndTheWasps extends BasicQuestHelper
{
	// Required items
	ItemRequirement beeBox, beeHunterHat, insectRepellent, insectNet, birdSnare;
	ItemRequirement beeHunterHatEquipped, insectNetEquipped;

	// Mid-quest item requirements
	ItemRequirement waspStings, brownFeathers, anyBrownFeathers, waspDarts;

	// Miscellaneous requirements
	Conditions lostBees;
	VarbitRequirement spokenToBeaAfterCutscene;
	ChatMessageRequirement beesAngry;
	Conditions receivedBirdSnares;
	VarbitRequirement caughtFiveBirds;

	// Steps
	NpcStep speakToFarmerBea, speakToBeeDealer, returnBees, speakToBeaAfterCutscene, returnStingsToBea, speakToBeaAfterCutsceneAgain, catchBirds, returnFeathersToBea;
	NpcStep speakToBeaAfterFinalCutscene, returnDartsToBea;
	ObjectStep takeEquipmentFromBox;
	DetailedQuestStep calmBees, equipGear, catchWasps, makeDarts;
	NpcStep talkToBeaAfterReturningTheBees;

	@Override
	protected void setupRequirements()
	{
		beeBox = new ItemRequirement("Bee box", ItemID.BAW_BEE_BOX);

		beeHunterHat = new ItemRequirement("Bee hunter's hat", ItemID.BAW_BEE_HUNTER_HAT);
		beeHunterHat.canBeObtainedDuringQuest();
		beeHunterHatEquipped = beeHunterHat.equipped();

		insectNet = new ItemRequirement("Insect net", ItemID.HUNTING_BUTTERFLY_NET);
		insectNet.canBeObtainedDuringQuest();
		insectNetEquipped = insectNet.equipped();

		insectRepellent = new ItemRequirement("Insect repellent", ItemID.INSECT_REPELLENT);
		insectRepellent.canBeObtainedDuringQuest();

		waspStings = new ItemRequirement("Wasp sting", ItemID.BAW_WASP_STING, 50);

		birdSnare = new ItemRequirement("Bird snare", ItemID.HUNTING_OJIBWAY_BIRD_SNARE, 2);
		birdSnare.canBeObtainedDuringQuest();

		anyBrownFeathers = new ItemRequirement("Brown feather", ItemID.BAW_BROWN_FEATHER);
		brownFeathers = anyBrownFeathers.quantity(50);

		waspDarts = new ItemRequirement("Wasp dart", ItemID.BAW_WASP_DART, 50);

		lostBees = not(beeBox.alsoCheckBank());

		spokenToBeaAfterCutscene = new VarbitRequirement(VarbitID.BAW_SPOKEN_ABOUT_WASPS, 1);

		beesAngry = new ChatMessageRequirement("You can sense the insects in the box getting agitated. Best give them a shake.");
		beesAngry.setInvalidateRequirement(
			new ChatMessageRequirement(
				"The agitated insects lash out and sting you through the holes in the box.",
				"You give the box a firm shake and the insects seem to calm down."
			)
		);

		receivedBirdSnares = new Conditions(true, LogicType.OR, birdSnare, anyBrownFeathers);
		caughtFiveBirds = new VarbitRequirement(VarbitID.BAW_BIRDS_CAUGHT, Operation.GREATER_EQUAL, 5, "Caught 5 Honey Buzzards");
	}

	void setupSteps()
	{
		speakToFarmerBea = new NpcStep(this, NpcID.BAW_BEA, new WorldPoint(3313, 3376, 0), "Speak to Farmer Bea south-east of Varrock to start the quest.");
		speakToFarmerBea.addDialogStep("Yes.");

		speakToBeeDealer = new NpcStep(this, NpcID.BAW_BEE_DEALER, new WorldPoint(3188, 3494, 0), "Speak to the Bee Dealer in the Grand Exchange");

		returnBees = new NpcStep(this, NpcID.BAW_BEA, new WorldPoint(3313, 3376, 0), "Return the bees to Farmer Bea, clicking on the bee box when the bees get agitated.", beeBox);
		talkToBeaAfterReturningTheBees = new NpcStep(this, NpcID.BAW_BEA, new WorldPoint(3313, 3376, 0), "Talk to Farmer Bea after returning the bees.");
		returnBees.addSubSteps(talkToBeaAfterReturningTheBees);

		calmBees = new DetailedQuestStep(this, "Click the bee box to calm the bees.", beeBox.highlighted());

		speakToBeaAfterCutscene = new NpcStep(this, NpcID.BAW_BEA, new WorldPoint(3313, 3376, 0), "Speak to Farmer Bea after watching the cutscene.");

		takeEquipmentFromBox = new ObjectStep(this, ObjectID.BAW_EQUIPMENT_CRATE, new WorldPoint(3315, 3376, 0), "Take the equipment from the equipment box.");
		takeEquipmentFromBox.addDialogStep("Take everything.");

		equipGear = new DetailedQuestStep(this, "Equip the Bee hunter's hat and Insect net.", beeHunterHat.highlighted(), insectNet.highlighted());

		catchWasps = new NpcStep(this, NpcID.BAW_WASP_SWARM, new WorldPoint(3316, 3363, 0), "Obtain 50 Wasp Stings by catching wasps with your insect net, clicking on the wasps when they get enraged to calm them.", true, beeHunterHatEquipped, insectNetEquipped);

		returnStingsToBea = new NpcStep(this, NpcID.BAW_BEA, new WorldPoint(3313, 3376, 0), "Return to Farmer Bea with the wasp stings.", waspStings);

		speakToBeaAfterCutsceneAgain = new NpcStep(this, NpcID.BAW_BEA, new WorldPoint(3313, 3376, 0), "Speak to Farmer Bea after watching the cutscene.");

		catchBirds = new NpcStep(this, NpcID.BAW_HONEY_BUZZARD, new WorldPoint(3324, 3361, 0), "Catch Honey Buzzards southeast of the beehives near Farmer Bea using the bird snares until you have 50 brown feathers.", true, birdSnare.highlighted());

		returnFeathersToBea = new NpcStep(this, NpcID.BAW_BEA, new WorldPoint(3313, 3376, 0), "Return to Farmer Bea with the brown feathers.", brownFeathers);

		speakToBeaAfterFinalCutscene = new NpcStep(this, NpcID.BAW_BEA, new WorldPoint(3313, 3376, 0), "Speak to Farmer Bea after watching the cutscene.");

		makeDarts = new DetailedQuestStep(this, "Use the wasp stings on the brown feathers to make 50 wasp darts.", waspStings.highlighted(), brownFeathers.highlighted());

		returnDartsToBea = new NpcStep(this, NpcID.BAW_BEA, new WorldPoint(3313, 3376, 0), "Bring the 50 wasp darts to Farmer Bea to finish the quest.", waspDarts);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, speakToFarmerBea);
		steps.put(5, speakToFarmerBea);
		steps.put(10, speakToBeeDealer);

		var cCalmBees = new ConditionalStep(this, returnBees);
		cCalmBees.addStep(lostBees, speakToBeeDealer);
		cCalmBees.addStep(beesAngry, calmBees);
		steps.put(15, cCalmBees);
		steps.put(20, talkToBeaAfterReturningTheBees);

		var cAfterCutscene = new ConditionalStep(this, speakToBeaAfterCutscene);
		cAfterCutscene.addStep(waspStings, returnStingsToBea);
		cAfterCutscene.addStep(and(spokenToBeaAfterCutscene, beeHunterHatEquipped, insectNetEquipped), catchWasps);
		cAfterCutscene.addStep(and(spokenToBeaAfterCutscene, beeHunterHat, insectNet), equipGear);
		cAfterCutscene.addStep(spokenToBeaAfterCutscene, takeEquipmentFromBox);
		steps.put(25, cAfterCutscene);

		steps.put(30, speakToBeaAfterCutsceneAgain);

		var cCatchBirds = new ConditionalStep(this, speakToBeaAfterCutsceneAgain);
		cCatchBirds.addStep(caughtFiveBirds, returnFeathersToBea);
		cCatchBirds.addStep(receivedBirdSnares, catchBirds);
		steps.put(35, cCatchBirds);

		steps.put(40, speakToBeaAfterFinalCutscene);

		var cMakeDarts = new ConditionalStep(this, makeDarts);
		cMakeDarts.addStep(waspDarts, returnDartsToBea);
		steps.put(45, cMakeDarts);
		steps.put(50, cMakeDarts);

		return steps;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			beeHunterHat,
			insectNet,
			insectRepellent,
			birdSnare
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
			new ExperienceReward(Skill.HUNTER, 1000),
			new ExperienceReward(Skill.FLETCHING, 250)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Ability to hunt wasps, killer wasps, honey buzzards and black honey buzzards"),
			new UnlockReward("Ability to fletch wasp darts and killer wasp darts"),
			new UnlockReward("Ability to trade wasp darts and killer wasp darts to Farmer Bea for supply bags")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			speakToFarmerBea
		), List.of(
		)));

		sections.add(new PanelDetails("Getting the bees", List.of(
			speakToBeeDealer,
			returnBees,
			calmBees,
			speakToBeaAfterCutscene
		), List.of(
		)));

		sections.add(new PanelDetails("Catching wasps", List.of(
			takeEquipmentFromBox,
			equipGear,
			catchWasps,
			returnStingsToBea,
			speakToBeaAfterCutsceneAgain
		), List.of(beeHunterHat, insectRepellent, insectNet
		)));

		sections.add(new PanelDetails("Catching birds", List.of(
			catchBirds,
			returnFeathersToBea
		), List.of(
			birdSnare
		)));

		sections.add(new PanelDetails("Making darts", List.of(
			speakToBeaAfterFinalCutscene,
			makeDarts,
			returnDartsToBea
		), List.of(
			waspStings,
			brownFeathers
		)));

		return sections;
	}
}
