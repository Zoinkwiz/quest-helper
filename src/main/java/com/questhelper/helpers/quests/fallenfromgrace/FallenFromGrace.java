/*
 * Copyright (c) 2026, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.fallenfromgrace;

import com.questhelper.collections.ItemCollections;
import com.questhelper.helpers.quests.secretsofthenorth.ArrowChestPuzzleStep;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.util.Port;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.PuzzleWrapperStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.questhelper.steps.SailStep;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;

public class FallenFromGrace extends BasicQuestHelper
{
	// Required items
	ItemRequirement raftOrSkiff;
	ItemRequirement pickaxe;
	ItemRequirement chisel;
	ItemRequirement hammer;
	ItemRequirement combatGear;

	// Recommended items


	// Mid-quest item requirements
	ItemRequirement sunstone;
	ItemRequirement sunstoneCore;
	ItemRequirement largeHat;
	ItemRequirement ancientSunstoneCore;
	ItemRequirement staircaseKey;
	ItemRequirement finalLetter;

	// Zones
	Zone island;
	Zone wyrmscraigCavern1;
	Zone wyrmscraigCavern2;
	Zone bossEntranceArea;
	Zone bossArea;

	// Miscellaneous requirements
	Requirement onIsland;
	VarbitRequirement canEnterIsland;
	Requirement needPickaxe;
	Requirement needChisel;
	Requirement needHammer;
	ZoneRequirement inWyrmscraigCavern;
	VarbitRequirement haveRepairedGolem;
	VarbitRequirement killedTrolls;
	WidgetTextRequirement inArrowPuzzle;
	VarbitRequirement staircaseUnlocked;
	ZoneRequirement nearBoss;


	// Steps
	NpcStep talkToFfion;
	NpcStep talkToCormac;
	SailStep travelToWyrmscraig;
	NpcStep talkToMuriel;
	DetailedQuestStep searchTentForItems;
	ObjectStep searchTentForPickaxe;
	ObjectStep searchTentForHammer;
	ObjectStep searchTentForChisel;
	ObjectStep inspectSunstone;
	ObjectStep mineSunstone;
	DetailedQuestStep chiselSunstoneFragment;
	DetailedQuestStep investigateHat;
	NpcStep returnToCormac;
	ObjectStep sailToCavern;
	NpcStep talkToMortimer;
	NpcStep killTrolls;
	NpcStep talkToMortimerAgain;
	NpcStep investigateGolem;
	NpcStep repairGolem;
	ArrowChestPuzzleStep arrowChestPuzzleStep;
	PuzzleWrapperStep openArrowChestStep;
	ObjectStep openChest;
	PuzzleWrapperStep openArrowChest;
	DetailedQuestStep destroySunstoneCore;
	ObjectStep climbStaircase;
	NpcStep killBoss;
	DetailedQuestStep killBossSidebar;
	ObjectStep searchBody;
	DetailedQuestStep readNote;
	NpcStep finishQuest;


	@Override
	protected void setupZones()
	{
		island = new Zone(new WorldPoint(2495, 2173, 0), new WorldPoint(2637, 2304, 2));
		wyrmscraigCavern1 = new Zone(10374);
		wyrmscraigCavern2 = new Zone(10375);
		bossArea = new Zone(new WorldPoint(2529, 2210, 0), new WorldPoint(2538, 2221, 0));
		bossEntranceArea = new Zone(new WorldPoint(2539, 2212, 0), new WorldPoint(2541, 2219, 0));
	}

	@Override
	protected void setupRequirements()
	{
		raftOrSkiff = new ItemRequirement("A raft or a skiff", -1);
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).isNotConsumed().canBeObtainedDuringQuest();
		chisel = new ItemRequirement("Chisel", ItemID.CHISEL).isNotConsumed().canBeObtainedDuringQuest();
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed().canBeObtainedDuringQuest();
		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();

		// Quest items
		sunstone = new ItemRequirement("Sunstone", ItemID.SUNSTONE);
		sunstoneCore = new ItemRequirement("Sunstone core", ItemID.SUNSTONE_CORE).canBeObtainedDuringQuest();
		largeHat = new ItemRequirement("Large hat", ItemID.FFG_KEENANS_HAT);
		ancientSunstoneCore = new ItemRequirement("Ancient sunstone core", ItemID.FFG_OLD_GOLEM_CORE);

		staircaseUnlocked = new VarbitRequirement(VarbitID.FFG_KEY_USED, 1);
		staircaseKey = new ItemRequirement("Staircase key", ItemID.FFG_STAIRCASE_KEY).hideConditioned(staircaseUnlocked);
		finalLetter = new ItemRequirement("Final letter", ItemID.FFG_FINAL_NOTE);

		needPickaxe = not(pickaxe);
		needChisel = not(chisel);
		needHammer = not(hammer);

		var onBoat = new VarbitRequirement(VarbitID.SAILING_BOARDED_BOAT, 1);
		onIsland = and(new ZoneRequirement(island), not(onBoat));
		inWyrmscraigCavern = new ZoneRequirement(wyrmscraigCavern1, wyrmscraigCavern2);
		nearBoss = new ZoneRequirement(bossArea, bossEntranceArea);

		canEnterIsland = new VarbitRequirement(VarbitID.WYRMSCRAIG_ENTRY, 1);

		haveRepairedGolem = new VarbitRequirement(VarbitID.FFG, 15, Operation.GREATER_EQUAL);
		killedTrolls = new VarbitRequirement(VarbitID.FFG_TROLLS_KILLED, 1);
		inArrowPuzzle = new WidgetTextRequirement(810, 15, 9, "Confirm");
	}


	void setupSteps()
	{
		travelToWyrmscraig = new SailStep(this, Port.WYRMSCRAIG);

		talkToFfion = new NpcStep(this, NpcID.WYRMSCRAIG_VILLAGER_1, new WorldPoint(2575, 2296, 0), "Talk to Ffion to be allowed onto the island.");

		talkToCormac = new NpcStep(this, NpcID.CORMAC, new WorldPoint(2576, 2255, 0), "Talk to Cormac in Auchrie village on Wyrmscraig island.");
		talkToCormac.addDialogStep("Yes.");

		talkToMuriel = new NpcStep(this, NpcID.MURIEL, new WorldPoint(2587, 2256, 0), "Talk to Muriel in the house north east of Cormac.");

		searchTentForPickaxe = new ObjectStep(this, ObjectID.WYRMSCRAIG_CRAFTING_TENT, new WorldPoint(2599, 2223, 0), "Search the tent to the south east on the coast for a pickaxe.");
		searchTentForPickaxe.addDialogStep("Take a pickaxe.");
		searchTentForHammer = new ObjectStep(this, ObjectID.WYRMSCRAIG_CRAFTING_TENT, new WorldPoint(2599, 2223, 0), "Search the tent to the south east on the coast for a hammer.");
		searchTentForHammer.addDialogStep("Take a hammer.");
		searchTentForChisel = new ObjectStep(this, ObjectID.WYRMSCRAIG_CRAFTING_TENT, new WorldPoint(2599, 2223, 0), "Search the tent to the south east on the coast for a chisel.");
		searchTentForChisel.addDialogStep("Take a chisel.");

		searchTentForItems = new DetailedQuestStep(this, "Search the tent to the south east on the coast for a pickaxe, hammer, and chisel.");
		searchTentForItems.addSubSteps(searchTentForPickaxe, searchTentForChisel, searchTentForHammer);

		inspectSunstone = new ObjectStep(this, ObjectID.WYRMSCRAIG_SUNSTONE01, new WorldPoint(2598, 2247, 0), "Inspect the sunstone monolith on the coast south east of Muriel.");

		mineSunstone = new ObjectStep(this, ObjectID.WYRMSCRAIG_SUNSTONE01, new WorldPoint(2598, 2247, 0), "Mine a sunstone fragment off the sunstone monolith.", pickaxe);
		chiselSunstoneFragment = new DetailedQuestStep(this, "Use a chisel on the sunstone fragment.", sunstone.highlighted(), chisel.highlighted());
		investigateHat = new DetailedQuestStep(this, new WorldPoint(2556, 2198, 0), "Investigate the large hat on a basalt column on the south west corner of the island.", largeHat);

		returnToCormac = new NpcStep(this, NpcID.CORMAC, new WorldPoint(2576, 2255, 0), "Return to Cormac in Auchrie village on Wyrmscraig island.");
		sailToCavern = new ObjectStep(this, ObjectID.WYRMSCRAIG_CAVE_ENTRANCE_SAILABLE, new WorldPoint(2562, 2203, 0),
			"Sail into the cave on the south west of the island with either a skiff or a raft.", combatGear, sunstoneCore.hideConditioned(haveRepairedGolem));

		talkToMortimer = new NpcStep(this, NpcID.MORTIMER_CUTSCENE, new WorldPoint(2601, 8608, 0), "Disembark onto the gangplank and talk to Mortimer.");
		killTrolls = new NpcStep(this, NpcID.FFG_TROLL_1, new WorldPoint(2592, 8607, 0), "Kill the 3 trolls near to Mortimer.", true);
		killTrolls.addAlternateNpcs(NpcID.FFG_TROLL_2, NpcID.FFG_TROLL_3);
		talkToMortimerAgain = new NpcStep(this, NpcID.MORTIMER_CUTSCENE, new WorldPoint(2601, 8608, 0), "Talk to Mortimer again.");
		investigateGolem = new NpcStep(this, NpcID.FFG_BROKEN_GOLEM_NOPOWER, new WorldPoint(2573, 8650, 0),
			"Investigate the broken golem in the far north of the cavern.");

		repairGolem = new NpcStep(this, NpcID.FFG_BROKEN_GOLEM_NOPOWER, new WorldPoint(2573, 8650, 0),
			"Use your sunstone core on the broken golem.", sunstoneCore.highlighted());
		repairGolem.addIcon(ItemID.SUNSTONE_CORE);

		arrowChestPuzzleStep = new ArrowChestPuzzleStep(this);
		arrowChestPuzzleStep.setSolution(1, 3, 2, 2);
		openArrowChestStep = new PuzzleWrapperStep(this, arrowChestPuzzleStep, "Work out how to unlock the chest in the north west of the northern room.")
			.withNoHelpHiddenInSidebar(true);

		openChest = new ObjectStep(this, ObjectID.FFG_CATHEDRAL_CHEST, new WorldPoint(2567, 8660, 0), "Unlock the chest in the north west of the northern room. The code is 'RIGHT, LEFT, DOWN, DOWN'.");
		openArrowChest = new PuzzleWrapperStep(this, openChest,
			"Work out how to unlock the chest in the north west of the northern room.");
		openArrowChest.addSubSteps(openArrowChestStep);

		destroySunstoneCore = new DetailedQuestStep(this, "Right-click destroy the ancient sunstone core.", ancientSunstoneCore.highlighted());

		climbStaircase = new ObjectStep(this, ObjectID.WYRMSCRAIG_CATHEDRAL_SPIRALSTAIRS, new WorldPoint(2576, 8657, 0), "Climb the staircase, ready for the boss fight.", combatGear, staircaseKey);

		killBoss = new NpcStep(this, NpcID.MAD_ANGEL_CATHEDRAL_VIS_QUEST, new WorldPoint(2533, 2216, 0), "Defeat the Mad Angel. Read the sidebar for more details.", true);
		killBoss.addAlternateNpcs(NpcID.MAD_ANGEL_INITIAL_QUEST, NpcID.MAD_ANGEL_QUEST, NpcID.MAD_ANGEL_ANIM_QUEST);
		killBoss.addDialogStep("Yes.");

		killBossSidebar = new DetailedQuestStep(this, "Defeat the Mad Angel. Use Protect from Melee.");
		killBossSidebar.addText("If she pulls her sword back slowly, run behind her.");
		killBossSidebar.addText("If she charges a blue attack, protect from magic. If you activate it the tick it hits you, you get a guaranteed max hit on her next hit.");
		killBossSidebar.addText("If she launches a projectile at a tile, stand on the tile to bounce it back to her.");
		killBossSidebar.addSubSteps(killBoss);

		searchBody = new ObjectStep(this, ObjectID.FFG_CORPSE_KEENAN, new WorldPoint(2529, 2215, 0), "Search the body in the church.");
		readNote = new DetailedQuestStep(this, "Read the final letter.", finalLetter.highlighted());
		finishQuest = new NpcStep(this, NpcID.CORMAC, new WorldPoint(2576, 2255, 0), "Return to Cormac to finish the quest.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		ConditionalStep goTalkToCormac = new ConditionalStep(this, travelToWyrmscraig);
		goTalkToCormac.addStep(and(onIsland, canEnterIsland), talkToCormac);
		goTalkToCormac.addStep(onIsland, talkToFfion);
		steps.put(0, goTalkToCormac);
		steps.put(2, goTalkToCormac);

		ConditionalStep goTalkToMuriel = new ConditionalStep(this, travelToWyrmscraig);
		goTalkToMuriel.addStep(onIsland, talkToMuriel);
		steps.put(3, goTalkToMuriel);

		// Steps
		ConditionalStep goInspectSunstone = new ConditionalStep(this, travelToWyrmscraig);
		goInspectSunstone.addStep(and(onIsland, needPickaxe), searchTentForPickaxe);
		goInspectSunstone.addStep(and(onIsland, needHammer), searchTentForHammer);
		goInspectSunstone.addStep(and(onIsland, needChisel), searchTentForChisel);
		goInspectSunstone.addStep(onIsland, inspectSunstone);
		steps.put(4, goInspectSunstone);

		ConditionalStep goMakeCore = new ConditionalStep(this, travelToWyrmscraig);
		goMakeCore.addStep(and(onIsland, sunstone, chisel, hammer), chiselSunstoneFragment);
		goMakeCore.addStep(and(onIsland, chisel, pickaxe, hammer), mineSunstone);
		goMakeCore.addStep(and(onIsland, needPickaxe), searchTentForPickaxe);
		goMakeCore.addStep(and(onIsland, needHammer), searchTentForHammer);
		goMakeCore.addStep(and(onIsland, needChisel), searchTentForChisel);

		ConditionalStep goInvestigateHat = new ConditionalStep(this, goMakeCore);
		goInvestigateHat.addStep(and(onIsland, sunstoneCore), investigateHat);
		steps.put(5, goInvestigateHat);

		ConditionalStep goReturnToCormac = new ConditionalStep(this, travelToWyrmscraig);
		goReturnToCormac.addStep(onIsland, returnToCormac);
		steps.put(6, returnToCormac);

		ConditionalStep goTalkToMortimer = new ConditionalStep(this, goMakeCore);
		goTalkToMortimer.addStep(inWyrmscraigCavern, talkToMortimer);
		goTalkToMortimer.addStep(sunstoneCore, sailToCavern);
		steps.put(8, goTalkToMortimer);
		steps.put(9, goTalkToMortimer);

		ConditionalStep goKillTrolls = new ConditionalStep(this, goMakeCore);
		goKillTrolls.addStep(and(killedTrolls, inWyrmscraigCavern), talkToMortimerAgain);
		goKillTrolls.addStep(inWyrmscraigCavern, killTrolls);
		goKillTrolls.addStep(sunstoneCore, sailToCavern);
		steps.put(11, goKillTrolls);

		ConditionalStep goInvestigateGolem = new ConditionalStep(this, goMakeCore);
		goInvestigateGolem.addStep(and(inWyrmscraigCavern, sunstoneCore), investigateGolem);
		goInvestigateGolem.addStep(sunstoneCore, sailToCavern);
		steps.put(13, goInvestigateGolem);

		ConditionalStep goRepairGolem = new ConditionalStep(this, goMakeCore);
		goRepairGolem.addStep(and(inWyrmscraigCavern, sunstoneCore), repairGolem);
		goRepairGolem.addStep(sunstoneCore, sailToCavern);
		steps.put(14, goRepairGolem);
		// FFG_TROLLS_KILLED 0->1
		// SAILING_BOAT_2_PORT 59->60

		// FFG_NOTE_FOUND in stairs

		ConditionalStep goClimbStairs = new ConditionalStep(this, sailToCavern);
		goClimbStairs.addStep(and(staircaseKey, inWyrmscraigCavern), climbStaircase);
		goClimbStairs.addStep(and(ancientSunstoneCore), destroySunstoneCore);
		goClimbStairs.addStep(and(inArrowPuzzle, inWyrmscraigCavern), arrowChestPuzzleStep);
		goClimbStairs.addStep(inWyrmscraigCavern, openArrowChest);

		steps.put(15, goClimbStairs);

		ConditionalStep goDefeatTheAngel = new ConditionalStep(this, sailToCavern);
		goDefeatTheAngel.addStep(nearBoss, killBoss);
		goDefeatTheAngel.addStep(inWyrmscraigCavern, climbStaircase);
		steps.put(16, goDefeatTheAngel);

		ConditionalStep goSearchBody = new ConditionalStep(this, sailToCavern);
		goSearchBody.addStep(finalLetter, readNote);
		goSearchBody.addStep(nearBoss, searchBody);
		goSearchBody.addStep(inWyrmscraigCavern, climbStaircase);
		steps.put(18, goSearchBody);

		steps.put(19, finishQuest);

		return steps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.PANDEMONIUM, QuestState.FINISHED),
			new SkillRequirement(Skill.SAILING, 62, false),
			new SkillRequirement(Skill.CRAFTING, 60, false),
			new SkillRequirement(Skill.RUNECRAFT, 47, false),
			new SkillRequirement(Skill.MINING, 53, false)
		);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			raftOrSkiff,
			pickaxe,
			chisel,
			hammer,
			combatGear
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Mad Angel (lvl 270)",
			"3 Mountain Trolls (level 69)"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.CRAFTING, 10000),
			new ExperienceReward(Skill.SAILING, 12500),
			new ExperienceReward(Skill.MINING, 5000),
			new ExperienceReward(Skill.RUNECRAFT, 5000)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to Mad Angel boss"),
			new UnlockReward("Access to Sunstone Golem Crafting & Mining"),
			new UnlockReward("Teleports to Wyrmscraig on the Slayer Ring and Necklace of Passage")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Investigating", List.of(
			travelToWyrmscraig,
			talkToFfion,
			talkToCormac,
			talkToMuriel,
			searchTentForItems,
			inspectSunstone,
			mineSunstone,
			chiselSunstoneFragment,
			investigateHat,
			returnToCormac
		)));

		sections.add(new PanelDetails("Below the church", List.of(
			sailToCavern,
			talkToMortimer,
			killTrolls,
			talkToMortimerAgain,
			investigateGolem,
			repairGolem,
			openArrowChest,
			destroySunstoneCore,
			climbStaircase,
			killBossSidebar,
			searchBody,
			readNote,
			finishQuest
		), List.of(
			combatGear,
			sunstoneCore
		)));

		return sections;
	}
}
