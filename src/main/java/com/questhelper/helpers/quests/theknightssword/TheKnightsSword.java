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
package com.questhelper.helpers.quests.theknightssword;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
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

// TODO: fix typo in Sir Vyvin's name
public class TheKnightsSword extends BasicQuestHelper
{
	// Required items
	ItemRequirement redberryPie;
	ItemRequirement ironBars;
	ItemRequirement bluriteOre;
	ItemRequirement pickaxe;

	// Recommended items
	ItemRequirement varrockTeleport;
	ItemRequirement faladorTeleports;
	ItemRequirement homeTele;

	// Mid-quest item requirements
	ItemRequirement bluriteSword;
	ItemRequirement portrait;

	// Zones
	Zone dungeon;
	Zone faladorCastle1;
	Zone faladorCastle2;
	Zone faladorCastle2Bedroom;

	// Miscellaneous requirements
	NpcRequirement sirVyvinNotInRoom;
	ZoneRequirement inDungeon;
	ZoneRequirement inFaladorCastle1;
	ZoneRequirement inFaladorCastle2;
	ZoneRequirement inFaladorCastle2Bedroom;

	// Steps
	NpcStep talkToSquire;

	NpcStep talkToReldo;

	NpcStep talkToThurgo;

	NpcStep talkToThurgoAgain;

	NpcStep talkToSquire2;

	ObjectStep goUpCastle1;
	ObjectStep goUpCastle2;
	ObjectStep searchCupboard;
	NpcStep givePortraitToThurgo;

	ObjectStep enterDungeon;
	ObjectStep mineBlurite;
	NpcStep bringThurgoOre;
	NpcStep finishQuest;

	@Override
	protected void setupZones()
	{
		dungeon = new Zone(new WorldPoint(2979, 9538, 0), new WorldPoint(3069, 9602, 0));
		faladorCastle1 = new Zone(new WorldPoint(2954, 3328, 1), new WorldPoint(2997, 3353, 1));
		faladorCastle2 = new Zone(new WorldPoint(2980, 3331, 2), new WorldPoint(2986, 3346, 2));
		faladorCastle2Bedroom = new Zone(new WorldPoint(2981, 3336, 2), new WorldPoint(2986, 3331, 2));
	}

	@Override
	protected void setupRequirements()
	{
		redberryPie = new ItemRequirement("Redberry pie", ItemID.REDBERRY_PIE);
		redberryPie.setTooltip("Purchasable from the grand exchange, or for ironmen: 10 cooking to cook one, or 32 cooking and a chef's hat to buy one from the Cooks' Guild.");
		ironBars = new ItemRequirement("Iron bar", ItemID.IRON_BAR, 2);
		bluriteOre = new ItemRequirement("Blurite ore", ItemID.BLURITE_ORE);
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).isNotConsumed();

		varrockTeleport = new ItemRequirement("A teleport to Varrock", ItemID.POH_TABLET_VARROCKTELEPORT);
		faladorTeleports = new ItemRequirement("Teleports to Falador", ItemID.POH_TABLET_FALADORTELEPORT, 3);
		homeTele = new ItemRequirement("A teleport near Mudskipper Point, such as POH teleport or Fairy Ring to AIQ", ItemID.NZONE_TELETAB_RIMMINGTON, 2);

		bluriteSword = new ItemRequirement("Blurite sword", ItemID.FALADIAN_SWORD);
		portrait = new ItemRequirement("Portrait", ItemID.KNIGHTS_PORTRAIT);

		inDungeon = new ZoneRequirement(dungeon);
		inFaladorCastle1 = new ZoneRequirement(faladorCastle1);
		inFaladorCastle2 = new ZoneRequirement(faladorCastle2);
		inFaladorCastle2Bedroom = new ZoneRequirement(faladorCastle2Bedroom);

		sirVyvinNotInRoom = new NpcRequirement("Sir Vyvin not in the bedroom.", NpcID.SIR_VYVIN, true, faladorCastle2Bedroom);
	}

	public void setupSteps()
	{
		talkToSquire = new NpcStep(this, NpcID.SQUIRE, new WorldPoint(2978, 3341, 0), "Talk to the Squire in Falador Castle's courtyard.");
		talkToSquire.addDialogStep("And how is life as a squire?");
		talkToSquire.addDialogStep("I can make a new sword if you like...");
		talkToSquire.addDialogStep("So would these dwarves make another one?");
		talkToSquire.addDialogStep("Yes.");
		talkToSquire.addTeleport(faladorTeleports.quantity(1));

		talkToReldo = new NpcStep(this, NpcID.RELDO_NORMAL, new WorldPoint(3211, 3494, 0), "Talk to Reldo in Varrock Castle's library.");
		talkToReldo.addDialogStep("What do you know about the Imcando dwarves?");
		talkToReldo.addTeleport(varrockTeleport.quantity(1));

		talkToThurgo = new NpcStep(this, NpcID.THURGO, new WorldPoint(3000, 3145, 0), "Talk to Thurgo south of Port Sarim and give him a redberry pie.", redberryPie);
		talkToThurgo.addDialogStep("Would you like a redberry pie?");
		talkToThurgo.addTeleport(homeTele.quantity(1));

		talkToThurgoAgain = new NpcStep(this, NpcID.THURGO, new WorldPoint(3000, 3145, 0), "Talk to Thurgo again.");
		talkToThurgoAgain.addDialogStep("Can you make a special sword for me?");

		talkToSquire2 = new NpcStep(this, NpcID.SQUIRE, new WorldPoint(2978, 3341, 0), "Talk to the Squire in Falador Castle's courtyard.");
		talkToSquire2.addTeleport(faladorTeleports.quantity(1));

		goUpCastle1 = new ObjectStep(this, ObjectID.FAI_FALADOR_CASTLE_LADDER_UP, new WorldPoint(2994, 3341, 0), "Climb up the east ladder in Falador Castle.");

		goUpCastle2 = new ObjectStep(this, ObjectID.FAI_FALADOR_CASTLE_STAIRS, new WorldPoint(2985, 3338, 1), "Go up the staircase west of the ladder on the 1st floor.");

		searchCupboard = new ObjectStep(this, ObjectID.VYVINCUPBOARDOPEN, new WorldPoint(2985, 3336, 2), "Search the cupboard in the room south of the staircase. You'll need Sir Vyvin to be in the other room.", sirVyvinNotInRoom);
		searchCupboard.addAlternateObjects(ObjectID.VYVINCUPBOARDSHUT);

		givePortraitToThurgo = new NpcStep(this, NpcID.THURGO, new WorldPoint(3000, 3145, 0), "Bring Thurgo the portrait.", pickaxe, ironBars, portrait);
		givePortraitToThurgo.addDialogStep("About that sword...");
		givePortraitToThurgo.addTeleport(homeTele.quantity(1));

		enterDungeon = new ObjectStep(this, ObjectID.FAI_TRAPDOOR, new WorldPoint(3008, 3150, 0), "Go down the ladder south of Port Sarim. Be prepared for ice giants and ice warriors to attack you.", pickaxe, ironBars);

		mineBlurite = new ObjectStep(this, ObjectID.BLURITE_ROCK_1, new WorldPoint(3049, 9566, 0), "Mine a blurite ore in the eastern cavern.", pickaxe);

		bringThurgoOre = new NpcStep(this, NpcID.THURGO, new WorldPoint(3000, 3145, 0), "Return to Thurgo with a blurite ore and two iron bars.", bluriteOre, ironBars);
		bringThurgoOre.addDialogStep("Can you make that replacement sword now?");

		finishQuest = new NpcStep(this, NpcID.SQUIRE, new WorldPoint(2978, 3341, 0), "Return to the Squire with the sword to finish the quest.", bluriteSword);
		finishQuest.addTeleport(faladorTeleports.quantity(1));
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToSquire);
		steps.put(1, talkToReldo);
		steps.put(2, talkToThurgo);
		steps.put(3, talkToThurgoAgain);
		steps.put(4, talkToSquire2);

		var getPortrait = new ConditionalStep(this, goUpCastle1);
		getPortrait.addStep(portrait.alsoCheckBank(), givePortraitToThurgo);
		getPortrait.addStep(inFaladorCastle2, searchCupboard);
		getPortrait.addStep(inFaladorCastle1, goUpCastle2);

		steps.put(5, getPortrait);

		var returnSwordToSquire = new ConditionalStep(this, enterDungeon);
		returnSwordToSquire.addStep(bluriteSword.alsoCheckBank(), finishQuest);
		returnSwordToSquire.addStep(bluriteOre.alsoCheckBank(), bringThurgoOre);
		returnSwordToSquire.addStep(inDungeon, mineBlurite);

		steps.put(6, returnSwordToSquire);

		return steps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new SkillRequirement(Skill.MINING, 10, true)
		);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			redberryPie,
			ironBars,
			pickaxe
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			varrockTeleport,
			faladorTeleports,
			homeTele
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Able to survive attacks from Ice Warriors (level 57) and Ice Giants (level 53)"
		);
	}

	@Override
	public List<String> getNotes()
	{
		return List.of(
			"You can make progress towards the Falador Easy Diary task by mining an additional Blurite ore and smelting another Blurite bar, it will be required for smithing Blurite limbs."
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
			new ExperienceReward(Skill.SMITHING, 12725)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("The ability to smelt Blurite ore."),
			new UnlockReward("The ability to smith Blurite bars.")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			talkToSquire,
			talkToReldo
		)));

		sections.add(new PanelDetails("Finding an Imcando", List.of(
			talkToThurgo,
			talkToThurgoAgain
		), List.of(
			redberryPie
		)));

		sections.add(new PanelDetails("Find the portrait", List.of(
			talkToSquire2,
			goUpCastle1,
			goUpCastle2,
			searchCupboard,
			givePortraitToThurgo
		)));

		sections.add(new PanelDetails("Making the sword", List.of(enterDungeon,
			mineBlurite,
			bringThurgoOre
		), List.of(
			pickaxe,
			ironBars
		)));

		sections.add(new PanelDetails("Return the sword", List.of(
			finishQuest
		)));

		return sections;
	}
}
