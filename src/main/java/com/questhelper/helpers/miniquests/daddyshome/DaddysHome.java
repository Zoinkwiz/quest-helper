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
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.widget.WidgetHighlight;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
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

	VarbitRequirement needToRemoveCampbed;
	VarbitRequirement needToRemoveCarpet;
	VarbitRequirement needToRemoveStool;
	VarbitRequirement needToRemoveTable;
	VarbitRequirement needToRemoveChair;
	VarbitRequirement needToRemoveStool2;
	VarbitRequirement needToRemoveTable2;
	Conditions needToRemoveAnyFurniture;

	VarbitRequirement needToBuildCarpet;
	VarbitRequirement needToBuildStool;
	VarbitRequirement needToBuildTable;
	VarbitRequirement needToBuildChair;
	VarbitRequirement needToBuildStool2;
	VarbitRequirement needToBuildTable2;
	Conditions needToBuildSimpleFurniture;

	VarbitRequirement needToBuildCampbed;

	// Steps
	NpcStep talkToMarlo;

	NpcStep talkToYarlo;

	ObjectStep removeCampbed;
	ObjectStep removeCarpet;
	ObjectStep removeStool;
	ObjectStep removeTable;
	ObjectStep removeChair;
	ObjectStep removeStool2;
	ObjectStep removeTable2;
	NpcStep talkToYarloAfterRemovingFurniture;
	ConditionalStep removeFurniture;

	ObjectStep buildCarpet;
	ObjectStep buildStool;
	ObjectStep buildTable;
	ObjectStep buildChair;
	ObjectStep buildStool2;
	ObjectStep buildTable2;
	ConditionalStep buildSimpleFurniture;

	ObjectStep searchCrate;
	NpcStep talkToOperator;
	ObjectStep buildCampbed;

	NpcStep talkToYarloAfterBuildingFurniture;

	NpcStep talkToMarloToFinish;

	@Override
	protected void setupRequirements()
	{
		needToRemoveCampbed = new VarbitRequirement(VarbitID.DADDYSHOME_BED, 2, Operation.LESS);
		needToRemoveCarpet = new VarbitRequirement(VarbitID.DADDYSHOME_CARPET, 2, Operation.LESS);
		needToRemoveStool = new VarbitRequirement(VarbitID.DADDYSHOME_STOOL_2, 2, Operation.LESS);
		needToRemoveTable = new VarbitRequirement(VarbitID.DADDYSHOME_TABLE_2, 2, Operation.LESS);
		needToRemoveChair = new VarbitRequirement(VarbitID.DADDYSHOME_CHAIR, 2, Operation.LESS);
		needToRemoveStool2 = new VarbitRequirement(VarbitID.DADDYSHOME_STOOL_1, 2, Operation.LESS);
		needToRemoveTable2 = new VarbitRequirement(VarbitID.DADDYSHOME_TABLE_1, 2, Operation.LESS);
		needToRemoveAnyFurniture = or(needToRemoveCampbed, needToRemoveCarpet, needToRemoveStool, needToRemoveTable, needToRemoveChair, needToRemoveStool2);

		needToBuildCarpet = new VarbitRequirement(VarbitID.DADDYSHOME_CARPET, 3, Operation.LESS);
		needToBuildStool = new VarbitRequirement(VarbitID.DADDYSHOME_STOOL_2, 3, Operation.LESS);
		needToBuildTable = new VarbitRequirement(VarbitID.DADDYSHOME_TABLE_2, 3, Operation.LESS);
		needToBuildChair = new VarbitRequirement(VarbitID.DADDYSHOME_CHAIR, 3, Operation.LESS);
		needToBuildStool2 = new VarbitRequirement(VarbitID.DADDYSHOME_STOOL_1, 3, Operation.LESS);
		needToBuildTable2 = new VarbitRequirement(VarbitID.DADDYSHOME_TABLE_1, 3, Operation.LESS);
		needToBuildSimpleFurniture = or(needToBuildCarpet, needToBuildStool, needToBuildTable, needToBuildChair, needToBuildStool2, needToBuildTable2);

		needToBuildCampbed = new VarbitRequirement(VarbitID.DADDYSHOME_BED, 3, Operation.LESS);

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
		talkToMarlo = new NpcStep(this, NpcID.CON_CONTRACTOR_VARROCK_1OP, new WorldPoint(3241, 3471, 0), "Talk to Marlo in north-east Varrock.");
		talkToMarlo.addAlternateNpcs(NpcID.CON_CONTRACTOR_VARROCK_2OP);
		talkToMarlo.addDialogSteps("What kind of favour do you want me to do?", "Tell me more about the job.", "Tell me where he lives, and I'll do the job.");

		talkToYarlo = new NpcStep(this, NpcID.DADDYSHOME_DADDY, new WorldPoint(3240, 3395, 0), "Talk to Old Man Yarlo in south-east Varrock, west of Aubury's Rune Shop.");

		removeCampbed = new ObjectStep(this, ObjectID.DADDYSHOME_BED, new WorldPoint(3242, 3398, 0), "Remove the campbed.");
		removeCarpet = new ObjectStep(this, ObjectID.DADDYSHOME_CARPET_MIDDLE, new WorldPoint(3239, 3395, 0), "Right-click remove the rotten carpet.");
		removeStool = new ObjectStep(this, ObjectID.DADDYSHOME_STOOL_2, new WorldPoint(3239, 3394, 0), "Demolish the broken stool.");
		removeTable = new ObjectStep(this, ObjectID.DADDYSHOME_TABLE_2, new WorldPoint(3240, 3394, 0), "Demolish the broken table.");
		removeChair = new ObjectStep(this, ObjectID.DADDYSHOME_CHAIR, new WorldPoint(3241, 3393, 0), "Demolish the broken chair.");
		removeStool2 = new ObjectStep(this, ObjectID.DADDYSHOME_STOOL_1, new WorldPoint(3244, 3394, 0), "Demolish the other broken stool.");
		removeTable2 = new ObjectStep(this, ObjectID.DADDYSHOME_TABLE_1, new WorldPoint(3245, 3394, 0), "Demolish the other broken table.");

		talkToYarloAfterRemovingFurniture = new NpcStep(this, NpcID.DADDYSHOME_DADDY, new WorldPoint(3240, 3395, 0), "Talk to Old Man Yarlo again after removing all the broken furniture.");
		talkToYarloAfterRemovingFurniture.addDialogStep("Skip Yarlo's lecture. He'll offer it later if you like.");

		removeFurniture = new ConditionalStep(this, removeTable2, "Remove the broken furniture in Old Man Yarlo's house.");
		removeFurniture.addStep(needToRemoveCampbed, removeCampbed);
		removeFurniture.addStep(needToRemoveCarpet, removeCarpet);
		removeFurniture.addStep(needToRemoveStool, removeStool);
		removeFurniture.addStep(needToRemoveTable, removeTable);
		removeFurniture.addStep(needToRemoveChair, removeChair);
		removeFurniture.addStep(needToRemoveStool2, removeStool2);

		var highlightFirstOption = new WidgetHighlight(InterfaceID.PohFurnitureCreation._01);
		buildCarpet = new ObjectStep(this, ObjectID.DADDYSHOME_CARPET_MIDDLE, new WorldPoint(3239, 3395, 0), "Right-click build the carpet.", bolt3, saw, hammer);
		buildCarpet.addWidgetHighlight(highlightFirstOption);
		buildStool = new ObjectStep(this, ObjectID.DADDYSHOME_STOOL_2, new WorldPoint(3239, 3394, 0), "Build the stool.", plank, nails2, saw, hammer);
		buildStool.addWidgetHighlight(highlightFirstOption);
		buildTable = new ObjectStep(this, ObjectID.DADDYSHOME_TABLE_2, new WorldPoint(3240, 3394, 0), "Build the table.", plank3, nails4, saw, hammer);
		buildTable.addWidgetHighlight(highlightFirstOption);
		buildChair = new ObjectStep(this, ObjectID.DADDYSHOME_CHAIR, new WorldPoint(3241, 3393, 0), "Build the chair.", plank2, nails2, saw, hammer);
		buildChair.addWidgetHighlight(highlightFirstOption);
		buildStool2 = new ObjectStep(this, ObjectID.DADDYSHOME_STOOL_1, new WorldPoint(3244, 3394, 0), "Build the other stool.", plank, nails2, saw, hammer);
		buildStool2.addWidgetHighlight(highlightFirstOption);
		buildTable2 = new ObjectStep(this, ObjectID.DADDYSHOME_TABLE_1, new WorldPoint(3245, 3394, 0), "Build the other table.", plank3, nails4, saw, hammer);
		buildTable2.addWidgetHighlight(highlightFirstOption);

		buildSimpleFurniture = new ConditionalStep(this, buildTable2, "Rebuild the furniture in Old Man Yarlo's house.");
		buildSimpleFurniture.addStep(needToBuildCarpet, buildCarpet);
		buildSimpleFurniture.addStep(needToBuildStool, buildStool);
		buildSimpleFurniture.addStep(needToBuildTable, buildTable);
		buildSimpleFurniture.addStep(needToBuildChair, buildChair);
		buildSimpleFurniture.addStep(needToBuildStool2, buildStool2);

		talkToYarloAfterBuildingFurniture = new NpcStep(this, NpcID.DADDYSHOME_DADDY, new WorldPoint(3240, 3395, 0), "Talk to Old Man Yarlo in south-east Varrock.");

		talkToMarloToFinish = new NpcStep(this, NpcID.CON_CONTRACTOR_VARROCK_1OP, new WorldPoint(3241, 3471, 0), "Talk to Marlo in north-east Varrock to complete the quest.");
		talkToMarloToFinish.addAlternateNpcs(NpcID.CON_CONTRACTOR_VARROCK_2OP);
		talkToMarloToFinish.addDialogStep("Yeah, what have you got for me?");

		searchCrate = new ObjectStep(this, ObjectID.DADDYSHOME_CRATES, new WorldPoint(3243, 3398, 0), "Search the crates in Old Man Yarlo's house for waxwood logs.");

		talkToOperator = new NpcStep(this, NpcID.POH_SAWMILL_OPP, new WorldPoint(3302, 3492, 0), "Talk to the Sawmill operator north-east of Varrock to make waxwood planks.", waxwoodLog3);
		talkToOperator.addDialogStep("I need some waxwood planks for Old Man Yarlo.");
		talkToOperator.addTeleport(lumberyardTeleport);
		buildCampbed = new ObjectStep(this, ObjectID.DADDYSHOME_BED, new WorldPoint(3242, 3398, 0), "Build the waxwood bed in Old Man Yarlo's house.", waxwoodPlank3, bolt2, hammer, saw);
		buildCampbed.addTeleport(varrockTeleport3.quantity(1));
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToMarlo);
		steps.put(1, talkToYarlo);

		var cRemoveFurniture = new ConditionalStep(this, talkToYarloAfterRemovingFurniture);
		cRemoveFurniture.addStep(needToRemoveAnyFurniture, removeFurniture);
		steps.put(2, cRemoveFurniture);

		steps.put(3, talkToYarloAfterRemovingFurniture);
		steps.put(4, talkToYarloAfterRemovingFurniture);

		var cRepairFurniture = new ConditionalStep(this, talkToYarloAfterBuildingFurniture);
		cRepairFurniture.addStep(needToBuildSimpleFurniture, buildSimpleFurniture);
		cRepairFurniture.addStep(and(needToBuildCampbed, waxwoodPlank3.alsoCheckBank(questBank)), buildCampbed);
		cRepairFurniture.addStep(and(needToBuildCampbed, waxwoodLog3.alsoCheckBank(questBank)), talkToOperator);
		cRepairFurniture.addStep(needToBuildCampbed, searchCrate);

		steps.put(5, cRepairFurniture);
		steps.put(6, cRepairFurniture);
		steps.put(7, cRepairFurniture);
		steps.put(8, cRepairFurniture);
		steps.put(9, cRepairFurniture);

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
			removeFurniture,
			talkToYarloAfterRemovingFurniture,
			buildSimpleFurniture,
			searchCrate,
			talkToOperator,
			buildCampbed,
			talkToYarloAfterBuildingFurniture,
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
