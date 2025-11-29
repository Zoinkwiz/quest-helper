/*
 * Copyright (c) 2020, Zoinkwiz
 * Copyright (c) 2025, pajlada <https://github.com/pajlada>
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
package com.questhelper.helpers.quests.runemysteries;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.util.QHObjectID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

public class RuneMysteries extends BasicQuestHelper
{
	// Recommended items
	ItemRequirement varrockTeleport;
	ItemRequirement wizardTeleport;

	// Zones
	Zone wizardBasement;
	Zone upstairsLumbridge;

	// Miscellaneous requirements
	ItemRequirement airTalisman;
	ItemRequirement researchPackage;
	ItemRequirement notes;

	ZoneRequirement inUpstairsLumbridge;
	ZoneRequirement inWizardBasement;

	VarbitRequirement needsToGrabPackage;
	VarbitRequirement needsToGrabResearchNotes;
	VarbitRequirement hasGivenSedridorTheNotes;

	// Steps
	ObjectStep goUpToHoracio;
	NpcStep talkToHoracio;
	ObjectStep goF1ToF0LumbridgeCastle;
	ObjectStep goDownToSedridor;
	NpcStep bringTalismanToSedridor;
	ObjectStep goDownToSedridorAfterHandingInAirTalisman;
	NpcStep getResearchPackageFromSedridor;
	NpcStep deliverPackageToAubury;
	NpcStep talkToAudburyAgain;
	ObjectStep goDownToSedridor2;
	NpcStep deliverResearchNotesToSedridor;
	NpcStep talkToSedridorAfterGivingHimTheNotes;

	@Override
	protected void setupZones()
	{
		upstairsLumbridge = new Zone(new WorldPoint(3203, 3206, 1), new WorldPoint(3218, 3231, 1));
		wizardBasement = new Zone(new WorldPoint(3094, 9553, 0), new WorldPoint(3125, 9582, 0));
	}

	@Override
	protected void setupRequirements()
	{
		inUpstairsLumbridge = new ZoneRequirement(upstairsLumbridge);
		inWizardBasement = new ZoneRequirement(wizardBasement);

		needsToGrabPackage = new VarbitRequirement(VarbitID.RUNEMYSTERIES_PACKAGE, 0);
		needsToGrabResearchNotes = new VarbitRequirement(VarbitID.RUNEMYSTERIES_NOTES, 0);
		hasGivenSedridorTheNotes = new VarbitRequirement(VarbitID.RUNEMYSTERIES_NOTES_GIVEN, 1);

		airTalisman = new ItemRequirement("Air talisman", ItemID.AIR_TALISMAN).isNotConsumed();
		airTalisman.setTooltip("You can get another from Duke Horacio if you lost it");
		researchPackage = new ItemRequirement("Research package", ItemID.RESEARCH_PACKAGE);
		researchPackage.setTooltip("You can get another from Sedridor if you lost it");
		notes = new ItemRequirement("Research notes", ItemID.RESEARCH_NOTES);
		notes.setTooltip("You can get another from Aubury if you lost them");
		varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.POH_TABLET_VARROCKTELEPORT);
		wizardTeleport = new ItemRequirement("A teleport to the Wizard's Tower", ItemCollections.NECKLACE_OF_PASSAGES);
	}

	public void setupSteps()
	{
		goUpToHoracio = new ObjectStep(this, QHObjectID.LUMBRIDGE_CASTLE_F0_SOUTH_STAIRCASE, new WorldPoint(3205, 3208, 0), "Talk to Duke Horacio on the first floor of Lumbridge castle.");

		talkToHoracio = new NpcStep(this, NpcID.DUKE_OF_LUMBRIDGE, new WorldPoint(3209, 3222, 1), "Talk to Duke Horacio on the first floor of Lumbridge castle.");
		talkToHoracio.addDialogStep("Have you any quests for me?");
		talkToHoracio.addDialogStep("Yes.");
		talkToHoracio.addSubSteps(goUpToHoracio);

		goF1ToF0LumbridgeCastle = new ObjectStep(this, ObjectID.SPIRALSTAIRSMIDDLE, new WorldPoint(3204, 3207, 1),
			"Bring the Air Talisman to Sedridor in the Wizard Tower's basement.", airTalisman);
		goF1ToF0LumbridgeCastle.addDialogStep("Climb down the stairs.");

		goDownToSedridor = new ObjectStep(this, ObjectID.WIZARDS_TOWER_LADDERTOP, new WorldPoint(3104, 3162, 0), "Bring the Air Talisman to Sedridor in the Wizard Tower's basement.", airTalisman);
		goDownToSedridor.addDialogStep("Have you any quests for me?");

		bringTalismanToSedridor = new NpcStep(this, NpcID.HEAD_WIZARD_1OP, new WorldPoint(3104, 9571, 0), "Bring the Air Talisman to Sedridor in the Wizard Tower's basement.", airTalisman);
		bringTalismanToSedridor.addDialogStep("I'm looking for the head wizard.");
		bringTalismanToSedridor.addDialogStep("Okay, here you are.");

		bringTalismanToSedridor.addSubSteps(goDownToSedridor, goF1ToF0LumbridgeCastle);

		goDownToSedridorAfterHandingInAirTalisman = new ObjectStep(this, ObjectID.WIZARDS_TOWER_LADDERTOP, new WorldPoint(3104, 3162, 0), "Talk to Sedridor in the Wizard Tower's basement and accept the Research Package.");
		goDownToSedridorAfterHandingInAirTalisman.addDialogStep("Have you any quests for me?");

		getResearchPackageFromSedridor = new NpcStep(this, NpcID.HEAD_WIZARD_1OP, new WorldPoint(3104, 9571, 0), "Talk to Sedridor in the Wizard Tower's basement and accept the Research Package.");
		getResearchPackageFromSedridor.addDialogSteps("Go ahead.", "Actually, I'm not interested.", "Yes, certainly.");
		getResearchPackageFromSedridor.addSubSteps(goDownToSedridorAfterHandingInAirTalisman);

		deliverPackageToAubury = new NpcStep(this, NpcID.AUBURY_2OP, new WorldPoint(3253, 3401, 0), "Bring the Research Package to Aubury in south east Varrock.", researchPackage);
		deliverPackageToAubury.addDialogStep("I've been sent here with a package for you.");
		deliverPackageToAubury.addTeleport(varrockTeleport);
		talkToAudburyAgain = new NpcStep(this, NpcID.AUBURY_2OP, new WorldPoint(3253, 3401, 0), "Talk to Aubury again in south east Varrock.");

		goDownToSedridor2 = new ObjectStep(this, ObjectID.WIZARDS_TOWER_LADDERTOP, new WorldPoint(3104, 3162, 0), "Bring the research notes to Sedridor in the Wizard Tower's basement.", notes);
		deliverResearchNotesToSedridor = new NpcStep(this, NpcID.HEAD_WIZARD_1OP, new WorldPoint(3104, 9571, 0), "Bring the notes to Sedridor in the Wizard Tower's basement.", notes);
		talkToSedridorAfterGivingHimTheNotes = new NpcStep(this, NpcID.HEAD_WIZARD_1OP, new WorldPoint(3104, 9571, 0), "Talk to Sedridor in the Wizard Tower's basement to finish the quest.");
		deliverResearchNotesToSedridor.addSubSteps(goDownToSedridor2, talkToSedridorAfterGivingHimTheNotes);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		var goTalkToHoracio = new ConditionalStep(this, goUpToHoracio);
		goTalkToHoracio.addStep(inUpstairsLumbridge, talkToHoracio);

		steps.put(0, goTalkToHoracio);

		var goTalkToSedridor = new ConditionalStep(this, goDownToSedridor);
		goTalkToSedridor.addStep(and(airTalisman, inUpstairsLumbridge), goF1ToF0LumbridgeCastle);
		goTalkToSedridor.addStep(inWizardBasement, bringTalismanToSedridor);

		steps.put(1, goTalkToSedridor);

		var getPackageFromSedridor = new ConditionalStep(this, goDownToSedridorAfterHandingInAirTalisman);
		getPackageFromSedridor.addStep(inWizardBasement, getResearchPackageFromSedridor);
		steps.put(2, getPackageFromSedridor);

		var cDeliverPackageToAbury = new ConditionalStep(this, deliverPackageToAubury);
		cDeliverPackageToAbury.addStep(and(inWizardBasement, needsToGrabPackage), getResearchPackageFromSedridor);
		cDeliverPackageToAbury.addStep(needsToGrabPackage, goDownToSedridorAfterHandingInAirTalisman);
		steps.put(3, cDeliverPackageToAbury);

		steps.put(4, talkToAudburyAgain);

		var cDeliverResearchNotesToSedridor = new ConditionalStep(this, goDownToSedridor2);
		cDeliverResearchNotesToSedridor.addStep(needsToGrabResearchNotes, talkToAudburyAgain);
		cDeliverResearchNotesToSedridor.addStep(and(inWizardBasement, hasGivenSedridorTheNotes), talkToSedridorAfterGivingHimTheNotes);
		cDeliverResearchNotesToSedridor.addStep(inWizardBasement, deliverResearchNotesToSedridor);
		steps.put(5, cDeliverResearchNotesToSedridor);

		return steps;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			varrockTeleport,
			wizardTeleport
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Air Talisman", ItemID.AIR_TALISMAN, 1)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Ability to mine Rune and Pure Essence.")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			talkToHoracio,
			bringTalismanToSedridor
		)));

		sections.add(new PanelDetails("Discovering the lost incantation", List.of(
			getResearchPackageFromSedridor,
			deliverPackageToAubury,
			talkToAudburyAgain,
			deliverResearchNotesToSedridor
		)));

		return sections;
	}
}
