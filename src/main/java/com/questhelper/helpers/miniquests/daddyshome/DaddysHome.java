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
package com.questhelper.helpers.miniquests.daddyshome;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.steps.*;
import net.runelite.api.NullObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.*;

public class DaddysHome extends BasicQuestHelper
{
	//Items Required
	ItemRequirement plank10, nails20, bolt5, hammer, saw, waxwoodLog3, waxwoodPlank3, bolt2,
		bolt3, nails2, nails4, plank, plank3, plank2;

	//Items Recommended
	ItemRequirement lumberyardTeleport, varrockTeleport3;

	Requirement removedChair, removedTable, removedTable2, removedStool, removedStool2, removedCampbed,
		removedCarpet, repairedCampbed, repairedCarpet, repairedStool, repairedTable, repairedChair,
		repairedStool2, repairedTable2;

	//NPC Steps
	DetailedQuestStep talkToMarlo, talkToYarlo, talkToYarloAgain, talkToOperator, talkToYarloOnceMore, talkToMarloToFinish;

	//Object/items Steps
	DetailedQuestStep removeChair, removeCarpet, removeStool, removeStool2, removeTable,
		removeTable2, removeCampbed, searchCrate, buildChair, buildCarpet, buildStool,
		buildStool2, buildTable, buildTable2, buildCampbed;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToMarlo);
		steps.put(1, talkToYarlo);

		ConditionalStep removeItems = new ConditionalStep(this, removeCampbed);
		removeItems.addStep(new Conditions(removedCampbed, removedCarpet, removedStool, removedTable, removedChair, removedStool2, removedTable2), talkToYarloAgain);
		removeItems.addStep(new Conditions(removedCampbed, removedCarpet, removedStool, removedTable, removedChair, removedStool2), removeTable2);
		removeItems.addStep(new Conditions(removedCampbed, removedCarpet, removedStool, removedTable, removedChair), removeStool2);
		removeItems.addStep(new Conditions(removedCampbed, removedCarpet, removedStool, removedTable), removeChair);
		removeItems.addStep(new Conditions(removedCampbed, removedCarpet, removedStool), removeTable);
		removeItems.addStep(new Conditions(removedCampbed, removedCarpet), removeStool);
		removeItems.addStep(removedCampbed, removeCarpet);

		steps.put(2, removeItems);
		steps.put(3, talkToYarloAgain);
		steps.put(4, talkToYarloAgain);

		ConditionalStep repairFurniture = new ConditionalStep(this, buildCarpet);
		repairFurniture.addStep(new Conditions(repairedCarpet, repairedStool, repairedTable, repairedChair, repairedStool2, repairedTable2, repairedCampbed), talkToYarloOnceMore);
		repairFurniture.addStep(new Conditions(repairedCarpet, repairedStool, repairedTable, repairedChair, repairedStool2, repairedTable2, waxwoodPlank3), buildCampbed);
		repairFurniture.addStep(new Conditions(repairedCarpet, repairedStool, repairedTable, repairedChair, repairedStool2, repairedTable2, waxwoodLog3), talkToOperator);
		repairFurniture.addStep(new Conditions(repairedCarpet, repairedStool, repairedTable, repairedChair, repairedStool2, repairedTable2), searchCrate);
		repairFurniture.addStep(new Conditions(repairedCarpet, repairedStool, repairedTable, repairedChair, repairedStool2), buildTable2);
		repairFurniture.addStep(new Conditions(repairedCarpet, repairedStool, repairedTable, repairedChair), buildStool2);
		repairFurniture.addStep(new Conditions(repairedCarpet, repairedStool, repairedTable), buildChair);
		repairFurniture.addStep(new Conditions(repairedCarpet, repairedStool), buildTable);
		repairFurniture.addStep(new Conditions(repairedCarpet), buildStool);

		steps.put(5, repairFurniture);
		steps.put(6, repairFurniture);
		steps.put(7, repairFurniture);
		steps.put(8, repairFurniture);
		steps.put(9, repairFurniture);

		steps.put(10, talkToMarloToFinish);
		steps.put(11, talkToMarloToFinish);
		steps.put(12, talkToMarloToFinish);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		plank10 = new ItemRequirement("Plank", ItemID.WOODPLANK, 10);
		bolt5 = new ItemRequirement("Bolt of cloth", ItemID.CLOTH, 5);
		nails20 = new ItemRequirement("Nails (bring more in case you fail with some)", ItemCollections.NAILS, 14);
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed();
		saw = new ItemRequirement("Saw", ItemCollections.SAW).isNotConsumed();
		waxwoodLog3 = new ItemRequirement("Waxwood log", ItemID.DADDYSHOME_WAXWOOD_LOGS, 3);
		waxwoodPlank3 = new ItemRequirement("Waxwood plank", ItemID.DADDYSHOME_WAXWOOD_PLANK, 3);

		bolt2 = new ItemRequirement("Bolt of cloth", ItemID.CLOTH, 2);
		bolt3 = new ItemRequirement("Bolt of cloth", ItemID.CLOTH, 3);
		nails2 = new ItemRequirement("Nails", ItemCollections.NAILS, 2);
		nails4 = new ItemRequirement("Nails", ItemCollections.NAILS, 4);
		plank = new ItemRequirement("Plank", ItemID.WOODPLANK);
		plank3 = new ItemRequirement("Plank", ItemID.WOODPLANK, 3);
		plank2 = new ItemRequirement("Plank", ItemID.WOODPLANK, 2);
		lumberyardTeleport = new ItemRequirement("Lumberyard Teleport", ItemID.TELEPORTSCROLL_LUMBERYARD);
		varrockTeleport3 = new ItemRequirement("Varrock Teleports", ItemID.POH_TABLET_VARROCKTELEPORT, 3);
	}

	public void setupConditions()
	{

		removedCampbed = new VarbitRequirement(10568, 2);
		removedCarpet = new VarbitRequirement(10569, 2);
		removedStool = new VarbitRequirement(10564, 2);

		removedTable = new VarbitRequirement(10567, 2);
		removedChair = new VarbitRequirement(10565, 2);
		removedStool2 = new VarbitRequirement(10563, 2);
		removedTable2 = new VarbitRequirement(10566, 2);

		repairedCampbed = new VarbitRequirement(10568, 3);
		repairedCarpet = new VarbitRequirement(10569, 3);
		repairedStool = new VarbitRequirement(10564, 3);

		repairedTable = new VarbitRequirement(10567, 3);
		repairedChair = new VarbitRequirement(10565, 3);
		repairedStool2 = new VarbitRequirement(10563, 3);
		repairedTable2 = new VarbitRequirement(10566, 3);
	}

	public void setupSteps()
	{
		talkToMarlo = new NpcStep(this, NpcID.CON_CONTRACTOR_VARROCK_1OP, new WorldPoint(3241, 3471, 0), "Talk to Marlo in north east Varrock.");
		((NpcStep) talkToMarlo).addAlternateNpcs(NpcID.CON_CONTRACTOR_VARROCK_2OP);
		talkToMarlo.addDialogSteps("What kind of favour do you want me to do?", "Tell me more about the job.", "Tell me where he lives, and I'll do the job.");
		talkToYarlo = new NpcStep(this, NpcID.DADDYSHOME_DADDY, new WorldPoint(3240, 3395, 0), "Talk to Old Man Yarlo in south Varrock.");
		talkToYarloAgain = new NpcStep(this, NpcID.DADDYSHOME_DADDY, new WorldPoint(3240, 3395, 0), "Talk to Old Man Yarlo in south Varrock again.");
		talkToYarloAgain.addDialogStep("Skip Yarlo's lecture. He'll offer it later if you like.");
		talkToYarloOnceMore = new NpcStep(this, NpcID.DADDYSHOME_DADDY, new WorldPoint(3240, 3395, 0), "Talk to Old Man Yarlo in south Varrock.");

		talkToMarloToFinish = new NpcStep(this, NpcID.CON_CONTRACTOR_VARROCK_1OP, new WorldPoint(3241, 3471, 0), "Talk to Marlo in north east Varrock to complete the quest.");
		((NpcStep) talkToMarloToFinish).addAlternateNpcs(NpcID.CON_CONTRACTOR_VARROCK_2OP);
		talkToMarloToFinish.addDialogStep("Yeah, what have you got for me?");

		removeCampbed = new ObjectStep(this, NullObjectID.NULL_40303, new WorldPoint(3242, 3398, 0), "Remove the broken items in the house.");
		removeCarpet = new ObjectStep(this, NullObjectID.NULL_40304, new WorldPoint(3239, 3395, 0), "Remove the broken items in the house.");
		removeStool = new ObjectStep(this, NullObjectID.NULL_40301, new WorldPoint(3239, 3394, 0), "Remove the broken items in the house.");
		removeTable = new ObjectStep(this, NullObjectID.NULL_40224, new WorldPoint(3240, 3394, 0), "Remove the broken items in the house.");
		removeChair = new ObjectStep(this, NullObjectID.NULL_40302, new WorldPoint(3241, 3393, 0), "Remove the broken items in the house.");
		removeTable2 = new ObjectStep(this, NullObjectID.NULL_40223, new WorldPoint(3245, 3394, 0), "Remove the broken items in the house.");
		removeStool2 = new ObjectStep(this, NullObjectID.NULL_40300, new WorldPoint(3244, 3394, 0), "Remove the broken items in the house.");

		removeCampbed.addSubSteps(removeCarpet, removeStool, removeTable, removeChair, removeTable2, removeStool2);

		searchCrate = new ObjectStep(this, ObjectID.DADDYSHOME_CRATES, new WorldPoint(3243, 3398, 0), "Search the crates in Yarlo's house for waxwood logs.");

		talkToOperator = new NpcStep(this, NpcID.POH_SAWMILL_OPP, new WorldPoint(3302, 3492, 0), "Talk to the Sawmill Operator north east of Varrock to make waxwood planks.", waxwoodLog3);
		talkToOperator.addDialogStep("I need some waxwood planks for Old Man Yarlo.");
		buildCampbed = new ObjectStep(this, NullObjectID.NULL_40303, new WorldPoint(3242, 3398, 0), "Build the waxwood bed in the house.", waxwoodPlank3, bolt2, hammer, saw);

		buildCarpet = new ObjectStep(this, NullObjectID.NULL_40304, new WorldPoint(3239, 3395, 0), "Build the items in the house.", bolt3, saw, hammer);
		buildStool = new ObjectStep(this, NullObjectID.NULL_40301, new WorldPoint(3239, 3394, 0), "Build the items in the house.", plank, nails2, saw, hammer);
		buildTable = new ObjectStep(this, NullObjectID.NULL_40224, new WorldPoint(3240, 3394, 0), "Build the items in the house.", plank3, nails4, saw, hammer);
		buildChair = new ObjectStep(this, NullObjectID.NULL_40302, new WorldPoint(3241, 3393, 0), "Build the items in the house.", plank2, nails2, saw, hammer);
		buildTable2 = new ObjectStep(this, NullObjectID.NULL_40223, new WorldPoint(3245, 3394, 0), "Build the items in the house.", plank3, nails4, saw, hammer);
		buildStool2 = new ObjectStep(this, NullObjectID.NULL_40300, new WorldPoint(3244, 3394, 0), "Build the items in the house.", plank, nails2, saw, hammer);
		buildCarpet.addSubSteps(buildStool, buildTable, buildChair, buildTable2, buildStool2);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(plank10, nails20, bolt5, saw, hammer);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(lumberyardTeleport, varrockTeleport3);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.CONSTRUCTION, 400));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Planks", ItemID.WOODPLANK, 25),
			new ItemReward("Oak Planks", ItemID.PLANK_OAK, 10),
			new ItemReward("Mithril Nails", ItemID.NAILS_MITHRIL, 50),
			new ItemReward("Steel Bars", ItemID.STEEL_BAR, 5),
			new ItemReward("Bolt of Cloth", ItemID.CLOTH, 8),
			new ItemReward("House Teleport Tablets", ItemID.POH_TABLET_TELEPORTTOHOUSE, 5),
			new ItemReward("Falador Teleport Tablet", ItemID.POH_TABLET_FALADORTELEPORT, 1),
			new ItemReward("POH in Rimmington or 1,000 Coins", ItemID.COINS, 1));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Helping Yarlo & Marlo", Arrays.asList(talkToMarlo, talkToYarlo, removeCampbed, talkToYarloAgain, buildCarpet, searchCrate, talkToOperator, buildCampbed, talkToYarloOnceMore, talkToMarloToFinish), plank10, nails20, bolt5, hammer, saw));
		return allSteps;
	}
}
