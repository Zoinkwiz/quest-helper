/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.miniquests.alfredgrimhandsbarcrawl;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.util.QHObjectID;
import java.util.ArrayList;
import java.util.List;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.VarPlayerID;

public class AlfredGrimhandsBarcrawl extends ComplexStateQuestHelper
{
	// Required items
	ItemRequirement coins208;

	// Recommended items
	ItemRequirement gamesNecklace;
	ItemRequirement varrockTeleport;
	ItemRequirement lumberyardTeleport;
	ItemRequirement faladorTeleport;
	ItemRequirement glory;
	ItemRequirement ardougneTeleport;
	ItemRequirement camelotTeleport;
	ItemRequirement duelingRing;

	// Mid-quest item requirements
	ItemRequirement coins50;
	ItemRequirement coins10;
	ItemRequirement coins70;
	ItemRequirement coins8;
	ItemRequirement coins7;
	ItemRequirement coins15;
	ItemRequirement coins18;
	ItemRequirement coins12;
	ItemRequirement barcrawlCard;

	// Zones
	Zone grandTreeF1;

	// Miscellaneous requirements
	VarplayerRequirement notTalkedToGuard;
	VarplayerRequirement notTalkedToBlueMoon;
	VarplayerRequirement notTalkedToJollyBoar;
	VarplayerRequirement notTalkedToRisingSun;
	VarplayerRequirement notTalkedToRustyAnchor;
	VarplayerRequirement notTalkedToZambo;
	VarplayerRequirement notTalkedToDeadMansChest;
	VarplayerRequirement notTalkedToFlyingHorseInn;
	VarplayerRequirement notTalkedToForestersArms;
	VarplayerRequirement notTalkedToBlurberry;
	VarplayerRequirement notTalkedToDragonInn;
	ZoneRequirement inGrandTreeF1;

	// Steps
	NpcStep talkToGuardToGetCard;
	NpcStep talkToBlueMoon;
	NpcStep talkToJollyBoar;
	NpcStep talkToRisingSun;
	NpcStep talkToRustyAnchor;
	NpcStep talkToZambo;
	NpcStep talkToDeadMansChest;
	NpcStep talkToFlyingHorseInn;
	NpcStep talkToForestersArms;
	ObjectStep goUpToBlurberry;
	NpcStep talkToBlurberry;
	NpcStep talkToDragonInn;
	NpcStep talkToGuardAgain;

	public void setupZones()
	{
		grandTreeF1 = new Zone(new WorldPoint(2437, 3474, 1), new WorldPoint(2493, 3511, 1));
	}

	@Override
	protected void setupRequirements()
	{
		coins208 = new ItemRequirement("Coins", ItemCollections.COINS, 208);
		coins50 = new ItemRequirement("Coins", ItemCollections.COINS, 50);
		coins10 = new ItemRequirement("Coins", ItemCollections.COINS, 10);
		coins70 = new ItemRequirement("Coins", ItemCollections.COINS, 70);
		coins8 = new ItemRequirement("Coins", ItemCollections.COINS, 8);
		coins7 = new ItemRequirement("Coins", ItemCollections.COINS, 7);
		coins15 = new ItemRequirement("Coins", ItemCollections.COINS, 15);
		coins18 = new ItemRequirement("Coins", ItemCollections.COINS, 18);
		coins12 = new ItemRequirement("Coins", ItemCollections.COINS, 12);

		gamesNecklace = new ItemRequirement("Games necklace", ItemCollections.GAMES_NECKLACES);
		varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.POH_TABLET_VARROCKTELEPORT);
		lumberyardTeleport = new ItemRequirement("Lumberyard teleport", ItemID.TELEPORTSCROLL_LUMBERYARD);
		lumberyardTeleport.addAlternates(ItemID.RING_OF_ELEMENTS_CHARGED);
		faladorTeleport = new ItemRequirement("Falador teleport", ItemID.POH_TABLET_FALADORTELEPORT);
		glory = new ItemRequirement("Amulet of Glory", ItemCollections.AMULET_OF_GLORIES).isNotConsumed();
		ardougneTeleport = new ItemRequirement("Ardougne teleport", ItemID.POH_TABLET_ARDOUGNETELEPORT);
		camelotTeleport = new ItemRequirement("Camelot teleport", ItemID.POH_TABLET_CAMELOTTELEPORT);
		duelingRing = new ItemRequirement("Ring of dueling", ItemCollections.RING_OF_DUELINGS);

		barcrawlCard = new ItemRequirement("Barcrawl card", ItemID.BARCRAWL_CARD);
		barcrawlCard.setTooltip("If you've lost it you can get another from the Barbarian Guard");
		inGrandTreeF1 = new ZoneRequirement(grandTreeF1);

		notTalkedToGuard = new VarplayerRequirement(VarPlayerID.BARCRAWL, false, 0);
		notTalkedToBlueMoon = new VarplayerRequirement(VarPlayerID.BARCRAWL, false, 3);
		notTalkedToJollyBoar = new VarplayerRequirement(VarPlayerID.BARCRAWL, false, 9);
		notTalkedToRisingSun = new VarplayerRequirement(VarPlayerID.BARCRAWL, false, 11);
		notTalkedToRustyAnchor = new VarplayerRequirement(VarPlayerID.BARCRAWL, false, 12);
		notTalkedToZambo = new VarplayerRequirement(VarPlayerID.BARCRAWL, false, 10);
		notTalkedToDeadMansChest = new VarplayerRequirement(VarPlayerID.BARCRAWL, false, 5);
		notTalkedToFlyingHorseInn = new VarplayerRequirement(VarPlayerID.BARCRAWL, false, 7);
		notTalkedToForestersArms = new VarplayerRequirement(VarPlayerID.BARCRAWL, false, 8);
		notTalkedToBlurberry = new VarplayerRequirement(VarPlayerID.BARCRAWL, false, 4);
		notTalkedToDragonInn = new VarplayerRequirement(VarPlayerID.BARCRAWL, false, 6);
	}

	public void setupSteps()
	{
		talkToGuardToGetCard = new NpcStep(this, NpcID.BARBGUARD1_PRECRAWL, new WorldPoint(2544, 3568, 0), "Talk to a barbarian guard outside the Barbarian Agility Course at the Barbarian Outpost to learn about Alfred Grimhand's Barcrawl.");
		talkToGuardToGetCard.addDialogSteps("I want to come through this gate.", "Looks can be deceiving, I am in fact a barbarian.");

		talkToBlueMoon = new NpcStep(this, NpcID.BLUEMOON_BARTENDER, new WorldPoint(3226, 3399, 0), "Talk to the bartender in the Blue Moon Inn in Varrock.", coins50);
		talkToBlueMoon.addDialogStep("I'm doing Alfred Grimhand's Barcrawl.");

		talkToJollyBoar = new NpcStep(this, NpcID.JOLLYBOAR_BARTENDER, new WorldPoint(3279, 3488, 0), "Talk to the bartender in the Jolly Boar Inn north east of Varrock.", coins10);
		talkToJollyBoar.addTeleport(lumberyardTeleport);
		talkToJollyBoar.addDialogStep("I'm doing Alfred Grimhands Barcrawl.");

		talkToRisingSun = new NpcStep(this, NpcID.RISINGSUN_BARMAID2, new WorldPoint(2956, 3370, 0), "Talk to a bartender in the Rising Sun Inn in Falador.", true, coins70);
		talkToRisingSun.addDialogStep("I'm doing Alfred Grimhand's barcrawl.");
		talkToRisingSun.addAlternateNpcs(NpcID.RISINGSUN_BARMAID, NpcID.RISINGSUN_BARMAID3);

		talkToRustyAnchor = new NpcStep(this, NpcID.RUSTYANCHOR_BARTENDER, new WorldPoint(3046, 3257, 0), "Talk to the bartender in the Rusty Anchor in Port Sarim.", coins8);
		talkToRustyAnchor.addDialogStep("I'm doing Alfred Grimhand's Barcrawl.");

		talkToZambo = new NpcStep(this, NpcID.ZEMBO, new WorldPoint(2927, 3144, 0), "Talk to Zembo in the Karamja Spirits Bar on Musa Point.", coins7);
		talkToZambo.addDialogStep("I'm doing Alfred Grimhand's barcrawl.");

		talkToDeadMansChest = new NpcStep(this, NpcID.DEADMANS_BARTENDER, new WorldPoint(2796, 3156, 0), "Talk to the bartender in the Dead Man's Chest in Brimhaven.", coins15);
		talkToDeadMansChest.addDialogStep("I'm doing Alfred Grimhand's Barcrawl.");

		talkToFlyingHorseInn = new NpcStep(this, NpcID.FLYINGHORSE_BARTENDER, new WorldPoint(2574, 3323, 0), "Talk to the bartender in the Flying Horse Inn in Ardougne.", coins8);
		talkToFlyingHorseInn.addDialogStep("I'm doing Alfred Grimhand's Barcrawl.");

		talkToForestersArms = new NpcStep(this, NpcID.FORESTERS_BARTENDER, new WorldPoint(2690, 3494, 0), "Talk to the bartender in the Forester's Arms in Seers' Village.", coins18);
		talkToForestersArms.addDialogStep("I'm doing Alfred Grimhand's Barcrawl.");

		goUpToBlurberry = new ObjectStep(this, QHObjectID.GRAND_TREE_F0_LADDER, new WorldPoint(2466, 3495, 0), "Talk to Blurberry in the Grand Tree.", coins10);
		talkToBlurberry = new NpcStep(this, NpcID.BLURBERRY, new WorldPoint(2482, 3491, 1), "Talk to Blurberry in the Grand Tree.", coins10);
		talkToBlurberry.addSubSteps(goUpToBlurberry);

		talkToDragonInn = new NpcStep(this, NpcID.DRAGON_BARTENDER, new WorldPoint(2556, 3079, 0), "Talk to the bartender in Dragon Inn in Yanille.", coins12);
		talkToDragonInn.addDialogStep("I'm doing Alfred Grimhand's Barcrawl.");

		talkToGuardAgain = new NpcStep(this, NpcID.BARBGUARD1_PRECRAWL, new WorldPoint(2544, 3568, 0), "Return to the barbarian guards outside the Barbarian Agility Course.");
	}

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupSteps();

		var barcrawl = new ConditionalStep(this, talkToGuardAgain);
		barcrawl.addStep(notTalkedToGuard, talkToGuardToGetCard);
		barcrawl.addStep(notTalkedToBlueMoon, talkToBlueMoon);
		barcrawl.addStep(notTalkedToJollyBoar, talkToJollyBoar);
		barcrawl.addStep(notTalkedToRisingSun, talkToRisingSun);
		barcrawl.addStep(notTalkedToRustyAnchor, talkToRustyAnchor);
		barcrawl.addStep(notTalkedToZambo, talkToZambo);
		barcrawl.addStep(notTalkedToDeadMansChest, talkToDeadMansChest);
		barcrawl.addStep(notTalkedToFlyingHorseInn, talkToFlyingHorseInn);
		barcrawl.addStep(notTalkedToForestersArms, talkToForestersArms);
		barcrawl.addStep(and(notTalkedToBlurberry, inGrandTreeF1), talkToBlurberry);
		barcrawl.addStep(notTalkedToBlurberry, goUpToBlurberry);
		barcrawl.addStep(notTalkedToDragonInn, talkToDragonInn);

		return barcrawl;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			coins208
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			gamesNecklace,
			varrockTeleport,
			lumberyardTeleport,
			faladorTeleport,
			glory,
			ardougneTeleport,
			camelotTeleport,
			duelingRing
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to Barbarian Outpost Agility Course"),
			new UnlockReward("Speak to the Barbarian Guard to learn how to smash empty vials automatically.")
		);
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Getting started", List.of(
			talkToGuardToGetCard
		)));

		sections.add(new PanelDetails("Drinking", List.of(
			talkToBlueMoon,
			talkToJollyBoar,
			talkToRisingSun,
			talkToRustyAnchor,
			talkToZambo,
			talkToDeadMansChest,
			talkToFlyingHorseInn,
			talkToForestersArms,
			talkToBlurberry,
			talkToDragonInn,
			talkToGuardAgain
		), List.of(
			barcrawlCard,
			coins208
		)));

		return sections;
	}
}
