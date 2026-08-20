/*
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
package com.questhelper.helpers.quests.troubledtortugans;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.nor;
import static com.questhelper.requirements.util.LogicHelper.not;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
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

public class TroubledTortugans extends BasicQuestHelper
{
	// Required items
	ItemRequirement anyAxe;
	ItemRequirement hammer;
	ItemRequirement saw;
	ItemRequirement combatGear;
	ItemRequirement food;
	ItemRequirement prayer;

	// Mid-quest item requirements
	ItemRequirement seaweed;
	ItemRequirement palmLeaf;
	ItemRequirement makeshiftBandages;
	ItemRequirement tortuganShield;

	// Zones
	Zone remoteIsland;
	Zone gryphonCave;
	Zone littlePearl;

	// Miscellaneous requirements
	VarbitRequirement onBoat;
	VarbitRequirement notOnBoat;
	Conditions onRemoteIsland;
	ZoneRequirement inGryphonCave;
	ZoneRequirement onLittlePearl;

	// Steps
	// 0
	NpcStep startQuest;

	// 2 + 4
	ObjectStep getPalmLeaf;
	ItemStep pickUpPalmLeaf;
	ItemStep getSeaweed;
	DetailedQuestStep makeBandage;
	NpcStep giveBandage;
	ConditionalStep cMakeBandages;

	// 6 + 7
	DetailedQuestStep talkToInjuredTortuganAfterBandaging;

	// 8 + 10
	ObjectStep boardBoat;
	ObjectStep sailToGreatConch;
	ConditionalStep cSailToGreatConch;

	// 12 + 13
	NpcStep talkToElderKorelAtDocks;

	// 14 + 16
	NpcStep talkToElderRaley;

	// 18
	RepairTown repairTown;

	// 20
	NpcStep talkToElderAfterRepairs;

	// 22 + 24
	ObjectStep inspectMonument;

	// 24
	ObjectStep followTrailPlant1;
	ObjectStep followTrailRockslide;
	ObjectStep followTrailPlant2;
	ObjectStep followTrailPlant3;
	ConditionalStep cFollowTrail;

	// 26
	ObjectStep unblockCave;

	// 28
	ObjectStep enterGryphonCave;

	// 30
	NpcStep fightGryphon;
	ConditionalStep cFightGryphon;

	// 32
	ObjectStep exitGryphonCave;
	NpcStep returnToElder;
	ConditionalStep cReturnToElder;

	// 34 + 36 + 38
	NpcStep getShield;
	ObjectStep boardBoatAtConch;
	DetailedQuestStep sailToLittlePearl;
	NpcStep fightShellbane;
	ConditionalStep cGetToTheLittlePearl;

	// 40
	NpcStep talkToElderKorelAfterBeatingShellbaneGryphon;

	// 42
	ObjectStep boardBoatFromLittlePearl;
	ObjectStep dockAtTheGreatConch;
	NpcStep finishQuest;
	ConditionalStep cFinishQuest;

	@Override
	protected void setupZones()
	{
		remoteIsland = new Zone(new WorldPoint(2946, 2619, 0), new WorldPoint(2974, 2598, 0));
		gryphonCave = new Zone(12682);
		littlePearl = new Zone(13346);
	}

	@Override
	protected void setupRequirements()
	{
		anyAxe = new ItemRequirement("Any axe", ItemCollections.AXES).canBeObtainedDuringQuest();
		anyAxe.appendToTooltip("A bronze axe can be found to the west of The Summer Shore. You need to cut down multiple things, so bring the best woodcutting axe you can use.");

		// NOTE: Haven't tested other hammers
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).canBeObtainedDuringQuest();
		hammer.appendToTooltip("Can be found inside the smith's house to the north-east.");

		// NOTE: Haven't tested other saws
		saw = new ItemRequirement("Saw", ItemCollections.SAW).canBeObtainedDuringQuest();
		saw.appendToTooltip("Can be found inside Elder Raley's house to the south-east.");

		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);

		prayer = new ItemRequirement("Prayer restore", ItemCollections.PRAYER_POTIONS, -1);

		seaweed = new ItemRequirement("Seaweed", ItemID.SEAWEED);
		palmLeaf = new ItemRequirement("Palm leaf", ItemID.PALM_LEAF);
		makeshiftBandages = new ItemRequirement("Makeshift bandages", ItemID.TT_BANDAGES);

		tortuganShield = new ItemRequirement("Tortugan shield", ItemID.TORTUGAN_SHIELD);

		onBoat = new VarbitRequirement(VarbitID.SAILING_BOARDED_BOAT, 1);
		notOnBoat = new VarbitRequirement(VarbitID.SAILING_BOARDED_BOAT, 0);

		onRemoteIsland = and(notOnBoat, new ZoneRequirement(remoteIsland));
		inGryphonCave = new ZoneRequirement(gryphonCave);
		onLittlePearl = new ZoneRequirement(littlePearl);
	}

	void setupSteps()
	{
		// 0
		startQuest = new NpcStep(this, NpcID.TT_FLOOPA_INJURED_VIS, new WorldPoint(2962, 2605, 0), "Talk to the Injured Tortugan on the small island north-west of The Great Conch to start the quest.");
		startQuest.addDialogStep("Yes.");

		// 2 + 4
		getPalmLeaf = new ObjectStep(this, ObjectID.TT_PALM_1, new WorldPoint(2964, 2604, 0), "Shake a nearby palm for a palm leaf.");
		getPalmLeaf.addAlternateObjects(ObjectID.TT_PALM_2, ObjectID.TT_PALM_3);
		pickUpPalmLeaf = new ItemStep(this, "Pick up the palm leaf from the ground", palmLeaf);
		getSeaweed = new ItemStep(this, "Pick up seaweed from the shore.", seaweed);
		makeBandage = new DetailedQuestStep(this, "Use the seaweed on the palm leaf to create makeshift bandages.", seaweed.highlighted(), palmLeaf.highlighted());
		giveBandage = new NpcStep(this, NpcID.TT_FLOOPA_INJURED_VIS, new WorldPoint(2962, 2605, 0), "Talk to the Injured Tortugan and give her the makeshift bandages.", makeshiftBandages);
		cMakeBandages = new ConditionalStep(this, giveBandage, "Help the Injured Tortugan with her wounds.");
		cMakeBandages.addStep(and(not(makeshiftBandages), seaweed, palmLeaf), makeBandage);
		cMakeBandages.addStep(and(not(makeshiftBandages), new ItemOnTileRequirement(palmLeaf)), pickUpPalmLeaf);
		cMakeBandages.addStep(nor(makeshiftBandages, palmLeaf), getPalmLeaf);
		cMakeBandages.addStep(nor(makeshiftBandages, seaweed), getSeaweed);

		// 6 + 7
		talkToInjuredTortuganAfterBandaging = new NpcStep(this, NpcID.TT_FLOOPA_ISLAND, new WorldPoint(2962, 2605, 0), "Talk to the Injured Tortugan again.");

		// 8 + 10
		// NOTE: We are assuming that the player has their boat here. I think there's no way this can be wrong
		boardBoat = new ObjectStep(this, ObjectID.SAILING_MOORING_REMOTE_ISLAND, "Board your boat with Floopa.");
		sailToGreatConch = new ObjectStep(this, ObjectID.SAILING_GANGPLANK_DISEMBARK, new WorldPoint(3174, 2367, 0), "Sail to the docks at the southern edge of The Great Conch with Floopa.");

		cSailToGreatConch = new ConditionalStep(this, sailToGreatConch);
		sailToGreatConch.addSubSteps(boardBoat);
		cSailToGreatConch.addStep(onRemoteIsland, boardBoat);

		// 12 + 13
		talkToElderKorelAtDocks = new NpcStep(this, NpcID.TT_KOREL_CONCH_DOCKS, new WorldPoint(3185, 2371, 0), "Talk to Elder Korel at the docks of The Great Conch.");

		// 14 + 16
		talkToElderRaley = new NpcStep(this, NpcID.TT_RALEY_CONCH, new WorldPoint(3186, 2405, 0), "Head into The Summer Shore to the north and talk to Elder Raley in the south-east building.");

		// 18
		repairTown = new RepairTown(this, anyAxe, hammer, saw);

		// 20
		talkToElderAfterRepairs = new NpcStep(this, NpcID.TT_RALEY_CONCH, new WorldPoint(3186, 2405, 0), "Talk to Elder Raley after completing the town repairs.");

		// 22
		inspectMonument = new ObjectStep(this, ObjectID.TT_HUNTING_MONUMENT_OP, new WorldPoint(3167, 2411, 0), "Inspect the monument in the middle of town to pick up the Gryphon's trail.", anyAxe, combatGear, food, prayer);

		// 24
		followTrailPlant1 = new ObjectStep(this, ObjectID.TT_HUNTING_PLANT_03, new WorldPoint(3128, 2423, 0), "Follow the Gryphon tracks west of town and inspect the red plant.", anyAxe, combatGear, food, prayer);
		followTrailRockslide = new ObjectStep(this, ObjectID.TT_HUNTING_ROCKS_01_OP, new WorldPoint(3127, 2448, 0), "Follow the Gryphon tracks north and inspect the rockslide.", anyAxe, combatGear, food, prayer);
		followTrailPlant2 = new ObjectStep(this, ObjectID.TT_HUNTING_PLANT_05_OP, new WorldPoint(3138, 2469, 0), "Follow the Gryphon tracks north up the hill and inspect the plant on the east side.", anyAxe, combatGear, food, prayer);
		followTrailPlant3 = new ObjectStep(this, ObjectID.TT_HUNTING_PLANT_05_OP, new WorldPoint(3151, 2474, 0), "Follow the Gryphon tracks east and inspect the plant to reveal the final set of tracks.", anyAxe, combatGear, food, prayer);

		cFollowTrail = new ConditionalStep(this, followTrailPlant3);
		cFollowTrail.addStep(new VarbitRequirement(VarbitID.TT_HUNTING_TRAIL_1, 0), inspectMonument);
		cFollowTrail.addStep(new VarbitRequirement(VarbitID.TT_HUNTING_TRAIL_2, 0), followTrailPlant1);
		cFollowTrail.addStep(new VarbitRequirement(VarbitID.TT_HUNTING_TRAIL_3, 0), followTrailRockslide);
		cFollowTrail.addStep(new VarbitRequirement(VarbitID.TT_HUNTING_TRAIL_4, 0), followTrailPlant2);

		// 26
		unblockCave = new ObjectStep(this, ObjectID.TT_LAIR_ENTRANCE_BLOCKED, new WorldPoint(3177, 2477, 0), "Follow the Gryphon tracks east and unblock the cave entrance.", anyAxe, combatGear, food, prayer);

		// 28
		enterGryphonCave = new ObjectStep(this, ObjectID.TT_LAIR_ENTRANCE_CLEAR, new WorldPoint(3177, 2477, 0), "Enter the Gryphon's cave, prepared for a fight.", combatGear, food, prayer);
		enterGryphonCave.addDialogStep("Yes.");

		// 30
		// TODO: Confirm this Npc ID is correct
		fightGryphon = new NpcStep(this, NpcID.TT_CONCH_GRYPHON, "Defeat the Gryphon. It hits with melee, and does a special attack if your worn weight is less than 45kg.", combatGear, food, prayer);
		cFightGryphon = new ConditionalStep(this, enterGryphonCave);
		cFightGryphon.addStep(inGryphonCave, fightGryphon);

		// 32
		exitGryphonCave = new ObjectStep(this, ObjectID.TT_LAIR_EXIT_FIGHT, new WorldPoint(3167, 8874, 0), "Return to Elder Raley with news about the Gryphon's defeat.");
		returnToElder = new NpcStep(this, NpcID.TT_RALEY_CONCH, new WorldPoint(3186, 2405, 0), "Return to Elder Raley with news about the Gryphon's defeat.");
		returnToElder.addSubSteps(exitGryphonCave);
		cReturnToElder = new ConditionalStep(this, returnToElder);
		cReturnToElder.addStep(inGryphonCave, exitGryphonCave);

		// 34 + 36 + 38
		getShield = new NpcStep(this, NpcID.TORTUGAN_BLUNN_1OP, new WorldPoint(3172, 2417, 0), "Get a Tortugan shield from Elder Blunn.");
		boardBoatAtConch = new ObjectStep(this, ObjectID.SAILING_GANGPLANK_EMBARK, new WorldPoint(3174, 2367, 0), "Sail to Little Pearl island, south-east of The Great Conch, to confront the Shellbane gryphon.", combatGear, food, prayer, tortuganShield);
		sailToLittlePearl = new ObjectStep(this, ObjectID.SAILING_MOORING_DISEMBARK, new WorldPoint(3354, 2216, 0), "Sail to Little Pearl island, south-east of The Great Conch, to confront the Shellbane gryphon.", combatGear, food, prayer, tortuganShield.equipped());
		sailToLittlePearl.addSubSteps(boardBoatAtConch);
		fightShellbane = new NpcStep(this, NpcID.TT_PEARL_GRYPHON, "Defeat the Shellbane gryphon. Have your worn weight be at least 45kg to avoid some damage. Protect from ranged if you're ranging it, and protect from melee when in melee range. Avoid tornadoes. Move when it spits a slow blob at you.", combatGear, food, prayer, tortuganShield.equipped());
		cGetToTheLittlePearl = new ConditionalStep(this, boardBoatAtConch);
		cGetToTheLittlePearl.addStep(not(tortuganShield), getShield);
		cGetToTheLittlePearl.addStep(onBoat, sailToLittlePearl);
		cGetToTheLittlePearl.addStep(onLittlePearl, fightShellbane);

		// 40
		talkToElderKorelAfterBeatingShellbaneGryphon = new NpcStep(this, NpcID.TT_KOREL_PEARL_COMBAT_DONE, new WorldPoint(3352, 2211, 0), "Talk to Elder Korel after defeating the Shellbane gryphon.");

		// 42
		boardBoatFromLittlePearl = new ObjectStep(this, ObjectID.SAILING_MOORING_EMBARK, new WorldPoint(3355, 2218, 0), "Return to Elder Raley at The Great Conch to complete the quest.");
		dockAtTheGreatConch = new ObjectStep(this, ObjectID.SAILING_GANGPLANK_DISEMBARK, new WorldPoint(3174, 2367, 0), "Return to Elder Raley at The Great Conch to complete the quest.");
		finishQuest = new NpcStep(this, NpcID.TT_RALEY_CONCH, new WorldPoint(3186, 2405, 0), "Return to Elder Raley at The Great Conch to complete the quest.");
		finishQuest.addSubSteps(boardBoatFromLittlePearl, dockAtTheGreatConch);
		cFinishQuest = new ConditionalStep(this, finishQuest);
		cFinishQuest.addStep(onBoat, dockAtTheGreatConch);
		cFinishQuest.addStep(onLittlePearl, boardBoatFromLittlePearl);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, startQuest);
		steps.put(2, startQuest);
		steps.put(4, cMakeBandages);
		steps.put(6, talkToInjuredTortuganAfterBandaging);
		steps.put(7, talkToInjuredTortuganAfterBandaging);
		steps.put(8, cSailToGreatConch);
		steps.put(10, cSailToGreatConch);
		// NOTE: If the user teleports away and resumes here, we assume they know how to make their way onto the island
		// I don't think this is a bad assumption, since it's just a matter of sailing
		steps.put(12, talkToElderKorelAtDocks);
		steps.put(13, talkToElderKorelAtDocks);
		steps.put(14, talkToElderRaley);
		steps.put(16, talkToElderRaley);
		steps.put(18, repairTown);
		steps.put(20, talkToElderAfterRepairs);
		// NOTE: 22 could maybe just be "inspectMonument"
		steps.put(22, cFollowTrail);
		steps.put(24, cFollowTrail);
		steps.put(26, unblockCave);
		steps.put(28, enterGryphonCave);
		steps.put(30, cFightGryphon);
		steps.put(32, cReturnToElder);
		steps.put(34, cGetToTheLittlePearl);
		steps.put(36, cGetToTheLittlePearl);
		steps.put(38, cGetToTheLittlePearl);
		steps.put(40, talkToElderKorelAfterBeatingShellbaneGryphon);
		steps.put(42, cFinishQuest);

		return steps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			// TODO: Waiting for the Pandemonium helper to be merged in
			// new QuestRequirement(QuestHelperQuest.PANDEMONIUM, QuestState.FINISHED),

			new SkillRequirement(Skill.SAILING, 45, false),
			new SkillRequirement(Skill.SLAYER, 51, false),
			new SkillRequirement(Skill.HUNTER, 45, false),
			new SkillRequirement(Skill.CONSTRUCTION, 48, false),
			new SkillRequirement(Skill.WOODCUTTING, 40, true),
			new SkillRequirement(Skill.CRAFTING, 34, true)
		);
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		// TODO
		return List.of();
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			anyAxe,
			hammer,
			saw,
			combatGear,
			food,
			prayer
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of();
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Gryphon (lvl 95)",
			"Shellbane gryphon (lvl 235)"
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
			new ExperienceReward(Skill.SAILING, 10000),
			new ExperienceReward(Skill.SLAYER, 8000)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of();
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to the Great Conch")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Find the stranded Tortugan", List.of(
			startQuest,
			cMakeBandages,
			talkToInjuredTortuganAfterBandaging
		)));

		sections.add(new PanelDetails("The Great Conch", List.of(
			sailToGreatConch,
			talkToElderKorelAtDocks,
			talkToElderRaley
		)));

		sections.add(new PanelDetails("Making repairs", List.of(
			repairTown,
			talkToElderAfterRepairs
		), List.of(
			repairTown.anyAxe,
			repairTown.hammer,
			repairTown.saw
		), List.of(
			repairTown.freeInvSlotRequirement
		)));

		sections.add(new PanelDetails("Tracking the Gryphon", List.of(
			inspectMonument,
			followTrailPlant1,
			followTrailRockslide,
			followTrailPlant2,
			followTrailPlant3,
			unblockCave,
			enterGryphonCave,
			fightGryphon,
			returnToElder
		), List.of(
			anyAxe,
			combatGear,
			food,
			prayer
		)));

		sections.add(new PanelDetails("Attack on the Little Pearl", List.of(
			getShield,
			sailToLittlePearl,
			fightShellbane,
			talkToElderKorelAfterBeatingShellbaneGryphon,
			finishQuest
		)));

		return sections;
	}
}
