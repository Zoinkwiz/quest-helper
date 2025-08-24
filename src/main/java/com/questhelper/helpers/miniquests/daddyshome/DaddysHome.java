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
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
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
import net.runelite.api.gameval.VarbitID;

public class DaddysHome extends BasicQuestHelper
{
	// Required items
	ItemRequirement plank10;
	ItemRequirement nails20;
	ItemRequirement bolt5;
	ItemRequirement saw;
	ItemRequirement hammer;

	// Recommended items
	ItemRequirement lumberyardTeleport;
	ItemRequirement varrockTeleport3;

	// Miscellaneous requirements
	ItemRequirement waxwoodLog3;
	ItemRequirement waxwoodPlank3;
	ItemRequirement bolt2;
	ItemRequirement bolt3;
	ItemRequirement nails2;
	ItemRequirement nails4;
	ItemRequirement plank;
	ItemRequirement plank3;
	ItemRequirement plank2;

	VarbitRequirement removedChair;
	VarbitRequirement removedTable;
	VarbitRequirement removedTable2;
	VarbitRequirement removedStool;
	VarbitRequirement removedStool2;
	VarbitRequirement removedCampbed;
	VarbitRequirement removedCarpet;
	VarbitRequirement repairedCampbed;
	VarbitRequirement repairedCarpet;
	VarbitRequirement repairedStool;
	VarbitRequirement repairedTable;
	VarbitRequirement repairedChair;
	VarbitRequirement repairedStool2;
	VarbitRequirement repairedTable2;

	// Steps
	NpcStep talkToMarlo;
	NpcStep talkToYarlo;
	NpcStep talkToYarloAgain;
	NpcStep talkToOperator;
	NpcStep talkToYarloOnceMore;
	NpcStep talkToMarloToFinish;
	ObjectStep removeChair;
	ObjectStep removeCarpet;
	ObjectStep removeStool;
	ObjectStep removeStool2;
	ObjectStep removeTable;
	ObjectStep removeTable2;
	ObjectStep removeCampbed;
	ObjectStep searchCrate;
	ObjectStep buildChair;
	ObjectStep buildCarpet;
	ObjectStep buildStool;
	ObjectStep buildStool2;
	ObjectStep buildTable;
	ObjectStep buildTable2;
	ObjectStep buildCampbed;

	@Override
	protected void setupRequirements()
	{
		removedCampbed = new VarbitRequirement(VarbitID.DADDYSHOME_BED, 2);
		removedCarpet = new VarbitRequirement(VarbitID.DADDYSHOME_CARPET, 2);
		removedStool = new VarbitRequirement(VarbitID.DADDYSHOME_STOOL_2, 2);

		removedTable = new VarbitRequirement(VarbitID.DADDYSHOME_TABLE_2, 2);
		removedChair = new VarbitRequirement(VarbitID.DADDYSHOME_CHAIR, 2);
		removedStool2 = new VarbitRequirement(VarbitID.DADDYSHOME_STOOL_1, 2);
		removedTable2 = new VarbitRequirement(VarbitID.DADDYSHOME_TABLE_1, 2);

		repairedCampbed = new VarbitRequirement(VarbitID.DADDYSHOME_BED, 3);
		repairedCarpet = new VarbitRequirement(VarbitID.DADDYSHOME_CARPET, 3);
		repairedStool = new VarbitRequirement(VarbitID.DADDYSHOME_STOOL_2, 3);

		repairedTable = new VarbitRequirement(VarbitID.DADDYSHOME_TABLE_2, 3);
		repairedChair = new VarbitRequirement(VarbitID.DADDYSHOME_CHAIR, 3);
		repairedStool2 = new VarbitRequirement(VarbitID.DADDYSHOME_STOOL_1, 3);
		repairedTable2 = new VarbitRequirement(VarbitID.DADDYSHOME_TABLE_1, 3);

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

	public void setupSteps()
	{
		talkToMarlo = new NpcStep(this, NpcID.CON_CONTRACTOR_VARROCK_1OP, new WorldPoint(3241, 3471, 0), "Talk to Marlo in north east Varrock.");
		talkToMarlo.addAlternateNpcs(NpcID.CON_CONTRACTOR_VARROCK_2OP);
		talkToMarlo.addDialogSteps("What kind of favour do you want me to do?", "Tell me more about the job.", "Tell me where he lives, and I'll do the job.");

		talkToYarlo = new NpcStep(this, NpcID.DADDYSHOME_DADDY, new WorldPoint(3240, 3395, 0), "Talk to Old Man Yarlo in south-east Varrock, west of Aubury's Rune Shop.");

		talkToYarloAgain = new NpcStep(this, NpcID.DADDYSHOME_DADDY, new WorldPoint(3240, 3395, 0), "Talk to Old Man Yarlo in south Varrock again.");
		talkToYarloAgain.addDialogStep("Skip Yarlo's lecture. He'll offer it later if you like.");
		talkToYarloOnceMore = new NpcStep(this, NpcID.DADDYSHOME_DADDY, new WorldPoint(3240, 3395, 0), "Talk to Old Man Yarlo in south Varrock.");

		talkToMarloToFinish = new NpcStep(this, NpcID.CON_CONTRACTOR_VARROCK_1OP, new WorldPoint(3241, 3471, 0), "Talk to Marlo in north east Varrock to complete the quest.");
		talkToMarloToFinish.addAlternateNpcs(NpcID.CON_CONTRACTOR_VARROCK_2OP);
		talkToMarloToFinish.addDialogStep("Yeah, what have you got for me?");

		removeCampbed = new ObjectStep(this, ObjectID.DADDYSHOME_BED, new WorldPoint(3242, 3398, 0), "Remove the broken items in the house.");
		removeCarpet = new ObjectStep(this, ObjectID.DADDYSHOME_CARPET_MIDDLE, new WorldPoint(3239, 3395, 0), "Remove the broken items in the house.");
		removeStool = new ObjectStep(this, ObjectID.DADDYSHOME_STOOL_2, new WorldPoint(3239, 3394, 0), "Remove the broken items in the house.");
		removeTable = new ObjectStep(this, ObjectID.DADDYSHOME_TABLE_2, new WorldPoint(3240, 3394, 0), "Remove the broken items in the house.");
		removeChair = new ObjectStep(this, ObjectID.DADDYSHOME_CHAIR, new WorldPoint(3241, 3393, 0), "Remove the broken items in the house.");
		removeTable2 = new ObjectStep(this, ObjectID.DADDYSHOME_TABLE_1, new WorldPoint(3245, 3394, 0), "Remove the broken items in the house.");
		removeStool2 = new ObjectStep(this, ObjectID.DADDYSHOME_STOOL_1, new WorldPoint(3244, 3394, 0), "Remove the broken items in the house.");

		removeCampbed.addSubSteps(removeCarpet, removeStool, removeTable, removeChair, removeTable2, removeStool2);

		searchCrate = new ObjectStep(this, ObjectID.DADDYSHOME_CRATES, new WorldPoint(3243, 3398, 0), "Search the crates in Yarlo's house for waxwood logs.");

		talkToOperator = new NpcStep(this, NpcID.POH_SAWMILL_OPP, new WorldPoint(3302, 3492, 0), "Talk to the Sawmill Operator north east of Varrock to make waxwood planks.", waxwoodLog3);
		talkToOperator.addDialogStep("I need some waxwood planks for Old Man Yarlo.");
		buildCampbed = new ObjectStep(this, ObjectID.DADDYSHOME_BED, new WorldPoint(3242, 3398, 0), "Build the waxwood bed in the house.", waxwoodPlank3, bolt2, hammer, saw);

		buildCarpet = new ObjectStep(this, ObjectID.DADDYSHOME_CARPET_MIDDLE, new WorldPoint(3239, 3395, 0), "Build the items in the house.", bolt3, saw, hammer);
		buildStool = new ObjectStep(this, ObjectID.DADDYSHOME_STOOL_2, new WorldPoint(3239, 3394, 0), "Build the items in the house.", plank, nails2, saw, hammer);
		buildTable = new ObjectStep(this, ObjectID.DADDYSHOME_TABLE_2, new WorldPoint(3240, 3394, 0), "Build the items in the house.", plank3, nails4, saw, hammer);
		buildChair = new ObjectStep(this, ObjectID.DADDYSHOME_CHAIR, new WorldPoint(3241, 3393, 0), "Build the items in the house.", plank2, nails2, saw, hammer);
		buildTable2 = new ObjectStep(this, ObjectID.DADDYSHOME_TABLE_1, new WorldPoint(3245, 3394, 0), "Build the items in the house.", plank3, nails4, saw, hammer);
		buildStool2 = new ObjectStep(this, ObjectID.DADDYSHOME_STOOL_1, new WorldPoint(3244, 3394, 0), "Build the items in the house.", plank, nails2, saw, hammer);
		buildCarpet.addSubSteps(buildStool, buildTable, buildChair, buildTable2, buildStool2);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToMarlo);
		steps.put(1, talkToYarlo);

		var removeItems = new ConditionalStep(this, removeCampbed);
		removeItems.addStep(and(removedCampbed, removedCarpet, removedStool, removedTable, removedChair, removedStool2, removedTable2), talkToYarloAgain);
		removeItems.addStep(and(removedCampbed, removedCarpet, removedStool, removedTable, removedChair, removedStool2), removeTable2);
		removeItems.addStep(and(removedCampbed, removedCarpet, removedStool, removedTable, removedChair), removeStool2);
		removeItems.addStep(and(removedCampbed, removedCarpet, removedStool, removedTable), removeChair);
		removeItems.addStep(and(removedCampbed, removedCarpet, removedStool), removeTable);
		removeItems.addStep(and(removedCampbed, removedCarpet), removeStool);
		removeItems.addStep(removedCampbed, removeCarpet);

		steps.put(2, removeItems);
		steps.put(3, talkToYarloAgain);
		steps.put(4, talkToYarloAgain);

		var repairFurniture = new ConditionalStep(this, buildCarpet);
		repairFurniture.addStep(and(repairedCarpet, repairedStool, repairedTable, repairedChair, repairedStool2, repairedTable2, repairedCampbed), talkToYarloOnceMore);
		repairFurniture.addStep(and(repairedCarpet, repairedStool, repairedTable, repairedChair, repairedStool2, repairedTable2, waxwoodPlank3), buildCampbed);
		repairFurniture.addStep(and(repairedCarpet, repairedStool, repairedTable, repairedChair, repairedStool2, repairedTable2, waxwoodLog3), talkToOperator);
		repairFurniture.addStep(and(repairedCarpet, repairedStool, repairedTable, repairedChair, repairedStool2, repairedTable2), searchCrate);
		repairFurniture.addStep(and(repairedCarpet, repairedStool, repairedTable, repairedChair, repairedStool2), buildTable2);
		repairFurniture.addStep(and(repairedCarpet, repairedStool, repairedTable, repairedChair), buildStool2);
		repairFurniture.addStep(and(repairedCarpet, repairedStool, repairedTable), buildChair);
		repairFurniture.addStep(and(repairedCarpet, repairedStool), buildTable);
		repairFurniture.addStep(repairedCarpet, buildStool);

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
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			plank10,
			nails20,
			bolt5,
			saw,
			hammer
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			lumberyardTeleport,
			varrockTeleport3
		);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.CONSTRUCTION, 400)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Planks", ItemID.WOODPLANK, 25),
			new ItemReward("Oak Planks", ItemID.PLANK_OAK, 10),
			new ItemReward("Mithril Nails", ItemID.NAILS_MITHRIL, 50),
			new ItemReward("Steel Bars", ItemID.STEEL_BAR, 5),
			new ItemReward("Bolt of Cloth", ItemID.CLOTH, 8),
			new ItemReward("House Teleport Tablets", ItemID.POH_TABLET_TELEPORTTOHOUSE, 5),
			new ItemReward("Falador Teleport Tablet", ItemID.POH_TABLET_FALADORTELEPORT, 1),
			new ItemReward("POH in Rimmington or 1,000 Coins", ItemID.COINS, 1)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Helping Yarlo & Marlo", List.of(
			talkToMarlo,
			talkToYarlo,
			removeCampbed,
			talkToYarloAgain,
			buildCarpet,
			searchCrate,
			talkToOperator,
			buildCampbed,
			talkToYarloOnceMore,
			talkToMarloToFinish
		), List.of(
			plank10,
			nails20,
			bolt5,
			hammer,
			saw
		)));

		return sections;
	}
}
