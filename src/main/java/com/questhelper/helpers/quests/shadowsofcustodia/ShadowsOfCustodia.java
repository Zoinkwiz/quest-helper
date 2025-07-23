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
package com.questhelper.helpers.quests.shadowsofcustodia;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.TeleportItemRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import static com.questhelper.requirements.util.LogicHelper.not;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
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
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

/**
 * The quest guide for the "Shadows of Custodia" OSRS quest
 */
public class ShadowsOfCustodia extends BasicQuestHelper
{
	// Item requirements
	ItemRequirement fishingRod;
	ItemRequirement hammer;
	ItemRequirement fourMapleLogs;
	ItemRequirement fourWillowLongbows;

	// Item recommendations
	ItemRequirement combatGear;
	TeleportItemRequirement startTeleport;

	// Miscellaneous requirements
	VarbitRequirement needToTalkToCitizen;
	VarbitRequirement needToTalkToBarkeep;
	VarbitRequirement needToTalkToShopkeep;
	VarbitRequirement needToTalkToIctus;
	Conditions needToFishClothFromLog;
	VarbitRequirement needsToHandInWillowLongbows;
	VarbitRequirement needToReinforceWall;
	FreeInventorySlotRequirement oneFreeInventorySlot;

	// Zones
	Zone caveRegion01;
	Zone caveRegion02;
	Zone caveRegion03;
	Zone upstairsOfParentsHouse;

	// Zone requirements
	ZoneRequirement outsideOfCave;
	ZoneRequirement isUpstairsOfParentsHouse;

	// Steps
	ObjectStep startQuest;
	ConditionalStep findOutAboutTheMissingPeople;
	NpcStep talkToTheParents;
	ObjectStep inspectWall;
	ObjectStep inspectPuddle;
	ObjectStep inspectPlank;
	NpcStep returnToTheParentsWithCloth;
	ObjectStep enterCave;
	NpcStep talkToInjuredBoyInCave;
	ConditionalStep findInjuredBoys;
	NpcStep returnToTheParentsWithBoy;
	ObjectStep reinforceWall;
	NpcStep informCaptainAboutMissingPeople;
	ConditionalStep reinforceWallAndTalkToCaptain;
	NpcStep talkToEtzAboutWhatTheyRemember;
	ConditionalStep talkToBoysUpstairs;
	ObjectStep enterCave2;
	NpcStep talkToAntos;
	ConditionalStep saveAntos;
	ConditionalStep talkToAntosAfterSavingHim;
	NpcStep finishQuest;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, startQuest);
		steps.put(2, findOutAboutTheMissingPeople);
		steps.put(4, talkToTheParents);
		steps.put(5, inspectWall);
		steps.put(6, inspectPuddle);
		steps.put(8, inspectPlank);
		steps.put(9, returnToTheParentsWithCloth);
		steps.put(10, findInjuredBoys);
		steps.put(12, findInjuredBoys);
		steps.put(14, returnToTheParentsWithBoy);
		steps.put(15, reinforceWallAndTalkToCaptain);
		steps.put(16, talkToBoysUpstairs);
		steps.put(18, saveAntos);
		steps.put(20, talkToAntosAfterSavingHim);
		steps.put(22, finishQuest);

		return steps;
	}

	@Override
	protected void setupZones()
	{
		caveRegion01 = new Zone(5272);
		caveRegion02 = new Zone(5273);
		caveRegion03 = new Zone(5016);

		upstairsOfParentsHouse = new Zone(new WorldPoint(1378, 3357, 1), new WorldPoint(1383, 3360, 1));
	}

	@Override
	protected void setupRequirements()
	{
		// For the step where you're prompted to talk to citizens about missing people
		needToTalkToCitizen = new VarbitRequirement(VarbitID.SOC_CITIZEN, 0);
		needToTalkToBarkeep = new VarbitRequirement(VarbitID.SOC_BARKEEP, 0);
		needToTalkToShopkeep = new VarbitRequirement(VarbitID.SOC_SHOPKEEP, 0);
		needToTalkToIctus = new VarbitRequirement(VarbitID.SOC_SILLYMAN, 0);

		needToFishClothFromLog = not(new QuestRequirement(QuestHelperQuest.SHADOWS_OF_CUSTODIA, 9));
		needsToHandInWillowLongbows = new VarbitRequirement(VarbitID.SOC_BOWSMADE, 2, Operation.NOT_EQUAL);
		needToReinforceWall = new VarbitRequirement(VarbitID.SOC_WALL_STATE, 3, Operation.NOT_EQUAL);

		outsideOfCave = new ZoneRequirement(false, caveRegion01, caveRegion02, caveRegion03);
		isUpstairsOfParentsHouse = new ZoneRequirement(upstairsOfParentsHouse);

		oneFreeInventorySlot = new FreeInventorySlotRequirement(1);

		// TODO: It would be nice to make this be not-red if you have lumby elite done, but that should probably be a teleport item feature
		startTeleport = new TeleportItemRequirement("Teleport to Auburnvale (Fairy ring AIS)", ItemCollections.FAIRY_STAFF);

		fishingRod = new ItemRequirement("Fishing rod", ItemID.FISHING_ROD).showConditioned(needToFishClothFromLog);

		// NOTE: I have _not_ confirmed you can use any other hammer, this is an educated guess.
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).showConditioned(needToReinforceWall);
		fourMapleLogs = new ItemRequirement("Maple logs", ItemID.MAPLE_LOGS, 4).showConditioned(needToReinforceWall);

		fourWillowLongbows = new ItemRequirement("Willow longbow", ItemID.WILLOW_LONGBOW, 4).showConditioned(needsToHandInWillowLongbows);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
	}

	public void setupSteps()
	{
		var unreachableState = new DetailedQuestStep(this, "This state should not be reachable, please make a report with a screenshot in the Quest Helper discord.");

		startQuest = new ObjectStep(this, ObjectID.SOC_MISSING_PERSONS, new WorldPoint(1396, 3356, 0), "Read the noticeboard in the Auburnvale bar to start the quest.");
		startQuest.addDialogStep("Yes.");
		startQuest.addTeleport(startTeleport);

		var talkToCitizen = new NpcStep(this, NpcID.SOC_CITIZEN, new WorldPoint(1394, 3354, 0), "Talk to Marcus in the Auburnvale bar.");
		var talkToBarkeep = new NpcStep(this, NpcID.AUBURN_BARTENDER, new WorldPoint(1391, 3354, 0), "Talk to the Bartender in the Auburnvale bar.");
		talkToBarkeep.addDialogStep("About the missing people...");
		var talkToShopkeep = new NpcStep(this, NpcID.AUBURN_GENERAL_STORE, new WorldPoint(1380, 3348, 0), "Talk to the Shopkeep in the Auburnvale General store.");
		talkToShopkeep.addDialogStep("About the missing people...");
		var talkToIctus = new NpcStep(this, NpcID.SOC_SILLYMAN, new WorldPoint(1409, 3375, 0), "Talk to Ictus, north of the Auburnvale Quetzal.");

		findOutAboutTheMissingPeople = new ConditionalStep(this, unreachableState, "Talk to citizens of Auburnvale about the missing people.");
		findOutAboutTheMissingPeople.addStep(needToTalkToCitizen, talkToCitizen);
		findOutAboutTheMissingPeople.addStep(needToTalkToBarkeep, talkToBarkeep);
		findOutAboutTheMissingPeople.addStep(needToTalkToIctus, talkToIctus);
		findOutAboutTheMissingPeople.addStep(needToTalkToShopkeep, talkToShopkeep);

		talkToTheParents = new NpcStep(this, NpcID.SOC_PARENT, new WorldPoint(1381, 3360, 0), "Talk to Aemilia and Francis, north of the Auburnvale General store, about their missing sons.", true);
		talkToTheParents.addAlternateNpcs(NpcID.SOC_PARENT_2);

		inspectWall = new ObjectStep(this, ObjectID.SOC_WALL_INSPECT_OP, new WorldPoint(1378, 3358, 0), "Inspect the wall outside Aemilia and Francis' house.");

		inspectPuddle = new ObjectStep(this, ObjectID.SOC_PUDDLE, new WorldPoint(1376, 3357, 0), "Inspect the puddle outside Aemilia and Francis' house.");
		inspectPlank = new ObjectStep(this, ObjectID.SOC_LOG_OP, new WorldPoint(1347, 3354, 0), "Follow the puddle trail and inspect the plank in the river, west of Auburnvale.", fishingRod);

		returnToTheParentsWithCloth = talkToTheParents.copy();
		returnToTheParentsWithCloth.setText("Return to Aemilia and Francis' house, north of the Auburnvale General store, and talk to them about the cloth you found.");

		// 16649: clicked plank but without a fishing rod

		enterCave = new ObjectStep(this, ObjectID.SOC_CAVE_ENTRANCE, new WorldPoint(1295, 3373, 0), "Follow the trail west of the city, then through the mountains, and enter the cave.");

		talkToInjuredBoyInCave = new NpcStep(this, NpcID.SOC_INJURED_PERSON, new WorldPoint(1298, 9757, 0), "Talk to the Injured boy inside the cave.");

		findInjuredBoys = new ConditionalStep(this, talkToInjuredBoyInCave);
		findInjuredBoys.addStep(outsideOfCave, enterCave);

		returnToTheParentsWithBoy = talkToTheParents.copy();
		returnToTheParentsWithBoy.setText("Return to Aemilia and Francis's house, north of the Auburnvale General store, and tell them you found their missing boys.");

		reinforceWall = new ObjectStep(this, ObjectID.SOC_WALL_INSPECT_REINFORCE, new WorldPoint(1378, 3358, 0), "Reinforce the wall outside Aemilia and Francis' house.", hammer, fourMapleLogs);

		informCaptainAboutMissingPeople = new NpcStep(this, NpcID.AUBURNVALE_GUARD_CAPTAIN, new WorldPoint(1369, 3344, 0), "Talk to Captain Ariadna, south-west of the Auburnvale General store, and inform her about the missing people.", fourWillowLongbows);

		reinforceWallAndTalkToCaptain = new ConditionalStep(this, informCaptainAboutMissingPeople);
		reinforceWallAndTalkToCaptain.addStep(needToReinforceWall, reinforceWall);

		var climbUpLadder = new ObjectStep(this, ObjectID.SOC_LADDER, new WorldPoint(1380, 3357, 0), "Climb upstairs of Aemilia and Francis' house and talk to Etz.");

		talkToEtzAboutWhatTheyRemember = new NpcStep(this, NpcID.SOC_ETZ, new WorldPoint(1381, 3360, 1), "Talk to Etz, upstairs of Aemilia and Francis' house, about what he remembers.");
		talkToEtzAboutWhatTheyRemember.addSubSteps(climbUpLadder);

		talkToBoysUpstairs = new ConditionalStep(this, talkToEtzAboutWhatTheyRemember);
		talkToBoysUpstairs.addStep(not(isUpstairsOfParentsHouse), climbUpLadder);

		var climbDownstairs = new ObjectStep(this, ObjectID.SOC_LADDERTOP, new WorldPoint(1380, 3357, 1), "Climb downstairs, then head back to the cave.");
		enterCave2 = enterCave.copy();
		enterCave2.addSubSteps(climbDownstairs);

		var killCreatures = new NpcStep(this, NpcID.SOC_QUEST_JUVENILE, new WorldPoint(1337, 9753, 0), "Kill the strange creatures. Protect from Melee works to avoid all damage.", true);

		talkToAntos = new NpcStep(this, NpcID.SOC_ANTOS, new WorldPoint(1337, 9753, 0), "Talk to Antos in the eastern part of the cave, ready to fight three Strange creatures. Protect from Melee works to avoid all damage.");
		talkToAntos.addSubSteps(killCreatures);

		var hasSpawnedCreatures = new VarbitRequirement(VarbitID.SOC_STALKERS_ENCOUNTERED, 1);

		saveAntos = new ConditionalStep(this, talkToAntos);
		// TODO: technically a little route would be nice
		saveAntos.addStep(isUpstairsOfParentsHouse, climbDownstairs);
		saveAntos.addStep(outsideOfCave, enterCave2);
		saveAntos.addStep(hasSpawnedCreatures, killCreatures);

		talkToAntosAfterSavingHim = new ConditionalStep(this, talkToAntos);
		// TODO: technically a little route would be nice
		talkToAntosAfterSavingHim.addStep(outsideOfCave, enterCave2);

		finishQuest = new NpcStep(this, NpcID.AUBURNVALE_GUARD_CAPTAIN, new WorldPoint(1368, 3343, 0), "Talk to Captain Ariadna in Auburnvale to finish the quest!");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			fishingRod,
			fourMapleLogs,
			hammer,
			fourWillowLongbows
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			startTeleport,
			combatGear
		);
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		return List.of(
			oneFreeInventorySlot
		);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		// NOTE: Ironment need 4 strung willow longbows. They can get it from elsewhere, maybe add as a note on the item requirement?
		// I don't think it should be a general requirement.
		return List.of(
			new QuestRequirement(QuestHelperQuest.CHILDREN_OF_THE_SUN, QuestState.FINISHED),
			new SkillRequirement(Skill.SLAYER, 54),
			new SkillRequirement(Skill.FISHING, 45),
			new SkillRequirement(Skill.CONSTRUCTION, 41),
			new SkillRequirement(Skill.HUNTER, 36)
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"3 Strange creatures (level 93)"
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
			new ExperienceReward(Skill.SLAYER, 10000),
			new ExperienceReward(Skill.HUNTER, 4000),
			new ExperienceReward(Skill.FISHING, 3000),
			new ExperienceReward(Skill.CONSTRUCTION, 3000)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to the Custodia Pass Slayer Dungeon")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var panels = new ArrayList<PanelDetails>();

		panels.add(new PanelDetails("Starting off", List.of(
			startQuest,
			findOutAboutTheMissingPeople,
			talkToTheParents
		), List.of(
			// Requirements
		), List.of(
			startTeleport
		)));

		panels.add(new PanelDetails("Hunting the trail", List.of(
			inspectWall,
			inspectPuddle,
			inspectPlank,
			returnToTheParentsWithCloth,
			enterCave,
			talkToInjuredBoyInCave,
			returnToTheParentsWithBoy
		), List.of(
			fishingRod
		), List.of(
			// Recommended
		)));

		panels.add(new PanelDetails("Inform the authorities", List.of(
			reinforceWall,
			informCaptainAboutMissingPeople,
			talkToEtzAboutWhatTheyRemember
		), List.of(
			fourMapleLogs,
			hammer,
			fourWillowLongbows
		), List.of(
			// Recommended
		)));

		panels.add(new PanelDetails("Save Antos", List.of(
			enterCave2,
			talkToAntos,
			finishQuest
		), List.of(
			// Requirements
		), List.of(
			combatGear
		)));

		return panels;
	}
}
