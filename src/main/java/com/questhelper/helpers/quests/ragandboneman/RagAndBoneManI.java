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
package com.questhelper.helpers.quests.ragandboneman;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.managers.QuestContainerManager;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.QuestSyncStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;

public class RagAndBoneManI extends BasicQuestHelper
{
	// Required items
	ItemRequirement coins;
	ItemRequirement pots;
	ItemRequirement logs;
	ItemRequirement tinderbox;

	// Recommended items
	ItemRequirement combatGear;
	ItemRequirement varrockTeleport;
	ItemRequirement lumbridgeTeleport;
	ItemRequirement digsitePendant;
	ItemRequirement draynorTeleport;
	ItemRequirement karamjaTeleport;
	// TODO: Remove this unused requirement
	ItemRequirement dramenStaff;

	// Mid-quest item requirements
	ItemRequirement jugOfVinegar;
	ItemRequirement jugOfVinegarNeeded;
	ItemRequirement potOfVinegar;
	ItemRequirement potOfVinegarNeeded;
	ItemRequirement potNeeded;

	// Zones
	Zone swamp;
	Zone junaRoom;
	Zone karamjaDungeon;

	// Miscellaneous requirements
	Requirement inSwamp;
	Requirement inJunaRoom;
	Requirement inKaramjaDungeon;
	Requirement addedRope;
	Requirement boneNearby;
	Requirement hadAllBones;
	Requirement talkedToFortunato;
	Requirement hadVinegar;
	Requirement allBonesAtLeastAddedToVinegar;
	Requirement allBonesPolished;
	Requirement logAdded;
	Requirement boneAddedToBoiler;
	Requirement logLit;
	Requirement boneReady;

	// Steps
	QuestSyncStep questSyncStep;

	NpcStep talkToOddOldMan;

	NpcStep killGiantRat;
	NpcStep killUnicorn;
	NpcStep killBear;
	NpcStep killRam;
	NpcStep killGoblin;
	NpcStep killFrog;
	NpcStep killMonkey;
	ObjectStep enterKaramjaDungeon;
	NpcStep killBat;
	ConditionalStep collectBonesSteps;

	NpcStep talkToFortunato;
	DetailedQuestStep makePotOfVinegar;
	DetailedQuestStep useBonesOnVinegar;
	ObjectStep placeLogs;
	ObjectStep useBoneOnBoiler;
	ObjectStep lightLogs;
	DetailedQuestStep waitForCooking;
	ObjectStep removePot;
	DetailedQuestStep repeatSteps;
	NpcStep giveBones;
	NpcStep talkToFinish;
	ConditionalStep preparingBonesSteps;
	ConditionalStep cookingSteps;

	@Override
	protected void setupZones()
	{
		swamp = new Zone(new WorldPoint(3138, 9536, 0), new WorldPoint(3261, 9601, 0));
		junaRoom = new Zone(new WorldPoint(3205, 9484, 0), new WorldPoint(3263, 9537, 2));
		karamjaDungeon = new Zone(new WorldPoint(2827, 9547, 0), new WorldPoint(2867, 9599, 0));
	}

	@Override
	protected void setupRequirements()
	{
		// Required items
		coins = new ItemRequirement("Coins", ItemCollections.COINS);
		pots = new ItemRequirement("Pot", ItemID.POT_EMPTY).isNotConsumed();
		potNeeded = new ItemRequirement("Pot", ItemID.POT_EMPTY, 8).alsoCheckBank().highlighted();
		logs = new ItemRequirement("Logs", ItemID.LOGS);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).isNotConsumed();

		// Recommended items
		varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.POH_TABLET_VARROCKTELEPORT);
		lumbridgeTeleport = new ItemRequirement("Lumbridge teleport", ItemID.POH_TABLET_LUMBRIDGETELEPORT);
		digsitePendant = new ItemRequirement("Digsite pendant", ItemCollections.DIGSITE_PENDANTS);
		draynorTeleport = new ItemRequirement("Draynor teleport", ItemCollections.AMULET_OF_GLORIES);
		draynorTeleport.addAlternates(ItemID.TELETAB_DRAYNOR);
		karamjaTeleport = new ItemRequirement("Karamja teleport", ItemCollections.AMULET_OF_GLORIES);
		karamjaTeleport.addAlternates(ItemID.NZONE_TELETAB_BRIMHAVEN, ItemID.TELEPORTSCROLL_TAIBWO);
		dramenStaff = new ItemRequirement("Dramen staff for fairy rings", ItemID.DRAMEN_STAFF).isNotConsumed();
		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		// Quest items
		jugOfVinegar = new ItemRequirement("Jar of vinegar", ItemID.RAG_VINEGAR);
		potOfVinegar = new ItemRequirement("Pot of vinegar", ItemID.RAG_POT_VINEGAR);
		potOfVinegarNeeded =
			new ItemRequirement("Pot of vinegar", ItemID.RAG_POT_VINEGAR, 8).alsoCheckBank().highlighted();
		jugOfVinegarNeeded =
			new ItemRequirement("Jug of vinegar", ItemID.RAG_VINEGAR, 8).alsoCheckBank().highlighted();
		inSwamp = new ZoneRequirement(swamp);
		inJunaRoom = new ZoneRequirement(junaRoom);
		inKaramjaDungeon = new ZoneRequirement(karamjaDungeon);

		// 2044 = 1, talked a bit to Odd Old Man

		addedRope = new VarbitRequirement(VarbitID.SWAMP_CAVES_ROPED_ENTRANCE, 1);

		boneNearby = new Conditions(LogicType.OR, RagBoneGroups.getBonesOnFloor(RagBoneGroups.getBones(RagBoneGroups.getRagBoneIStates())));

		logAdded = new VarbitRequirement(VarbitID.RAG_BOILER, 1, Operation.GREATER_EQUAL);
		boneAddedToBoiler = new VarbitRequirement(VarbitID.RAG_BOILER, 2, Operation.GREATER_EQUAL);
		logLit = new VarbitRequirement(VarbitID.RAG_BOILER, 3, Operation.GREATER_EQUAL);
		boneReady = new VarbitRequirement(VarbitID.RAG_BOILER, 4);

		// Every time handing in a bone, 2045 iterates from 0->28 1 by 1. Next time you hand in a bone it goes back
		// to 0 and repeats???

		allBonesPolished = new Conditions(RagBoneGroups.allBonesPolished(RagBoneGroups.getRagBoneIStates()));

		allBonesAtLeastAddedToVinegar = new Conditions(RagBoneGroups.allBonesAddedToVinegar(RagBoneGroups.getRagBoneIStates()));

		hadAllBones = new Conditions(RagBoneGroups.allBonesObtained(RagBoneGroups.getRagBoneIStates()));

		talkedToFortunato = new VarbitRequirement(VarbitID.RAG_WINE, 1);

		hadVinegar = new Conditions(jugOfVinegar.alsoCheckBank());
	}

	public void setupSteps()
	{
		questSyncStep = new QuestSyncStep(this, getQuest(), "Open the quest's Quest Journal to sync your state.");

		talkToOddOldMan = new NpcStep(this, NpcID.RAG_ODD_OLD_MAN, new WorldPoint(3362, 3502, 0), "Talk to the Odd Old Man east of Varrock.");
		talkToOddOldMan.addDialogSteps("Anything I can do to help?", "Yes.");

		collectBonesSteps = new ConditionalStep(this, questSyncStep);

		{
			var rbs = RagBoneState.GIANT_RAT_BONE;

			killGiantRat = new NpcStep(this, NpcID.GIANTRAT, new WorldPoint(3289, 3373, 0), "Kill a giant rat south east of Varrock and pick up its bone.", true);
			killGiantRat.addAlternateNpcs(NpcID.GIANTRAT2, NpcID.GIANTRAT3, NpcID.GIANTRAT_GREY, NpcID.GIANTRAT_GREY2, NpcID.GIANTRAT_GREY3, NpcID.GIANTRAT1, NpcID.GIANTRAT1_2, NpcID.GIANTRAT1_3);
			killGiantRat.conditionToHideInSidebar(rbs.hadBoneItem());
			var takeBones = new ItemStep(this, "Pick up the " + Text.titleCase(rbs) + ".", rbs.getBoneItem());
			killGiantRat.addSubSteps(takeBones);

			collectBonesSteps.addStep(and(rbs.boneNearby(), not(rbs.hadBoneItem())), takeBones);
			collectBonesSteps.addStep(not(rbs.hadBoneItem()), killGiantRat);
		}

		{
			var rbs = RagBoneState.UNICORN_BONE;

			killUnicorn = new NpcStep(this, NpcID.UNICORN, new WorldPoint(3285, 3351, 0), "Kill the unicorn south east of Varrock and pick up its bone.", true);
			killUnicorn.conditionToHideInSidebar(rbs.hadBoneItem());
			var takeBones = new ItemStep(this, "Pick up the " + Text.titleCase(rbs) + ".", rbs.getBoneItem());
			killUnicorn.addSubSteps(takeBones);

			collectBonesSteps.addStep(and(rbs.boneNearby(), not(rbs.hadBoneItem())), takeBones);
			collectBonesSteps.addStep(not(rbs.hadBoneItem()), killUnicorn);
		}

		{
			var rbs = RagBoneState.BEAR_RIBS;

			killBear = new NpcStep(this, NpcID.DARKBEAR, new WorldPoint(3295, 3354, 0), "Kill the bear south east of Varrock and pick up its bone.", true);
			killBear.conditionToHideInSidebar(rbs.hadBoneItem());
			var takeBones = new ItemStep(this, "Pick up the " + Text.titleCase(rbs) + ".", rbs.getBoneItem());
			killBear.addSubSteps(takeBones);

			collectBonesSteps.addStep(and(rbs.boneNearby(), not(rbs.hadBoneItem())), takeBones);
			collectBonesSteps.addStep(not(rbs.hadBoneItem()), killBear);
		}

		{
			var rbs = RagBoneState.RAM_SKULL;

			killRam = new NpcStep(this, NpcID.RAMSHEERED, new WorldPoint(3253, 3350, 0), "Kill a ram south of Varrock and pick up its bone.", true);
			killRam.addAlternateNpcs(NpcID.RAMUNSHEERED, NpcID.RAMUNSHEERED2, NpcID.RAMUNSHEERED3, NpcID.RAMUNSHEEREDSHAGGY);
			killRam.conditionToHideInSidebar(rbs.hadBoneItem());
			var takeBones = new ItemStep(this, "Pick up the " + Text.titleCase(rbs) + ".", rbs.getBoneItem());
			killRam.addSubSteps(takeBones);

			collectBonesSteps.addStep(and(rbs.boneNearby(), not(rbs.hadBoneItem())), takeBones);
			collectBonesSteps.addStep(not(rbs.hadBoneItem()), killRam);
		}

		{
			var rbs = RagBoneState.GOBLIN_SKULL;

			killGoblin = new NpcStep(this, NpcID.GOBLIN_RED_SOLDIER_2, new WorldPoint(3252, 3251, 0), "Kill a goblin east of Lumbridge and pick up its bone.", true);
			killGoblin.addAlternateNpcs(NpcID.GOBLIN_UNARMED_MELEE_1, NpcID.GOBLIN_UNARMED_MELEE_2, NpcID.GOBLIN_UNARMED_MELEE_3, NpcID.GOBLIN_UNARMED_MELEE_4, NpcID.GOBLIN_UNARMED_MELEE_5, NpcID.GOBLIN_UNARMED_MELEE_6, NpcID.GOBLIN_UNARMED_MELEE_7, NpcID.GOBLIN_UNARMED_MELEE_8, NpcID.GOBLIN_UNARMED_MELEE_IN_1, NpcID.GOBLIN_UNARMED_MELEE_IN_2, NpcID.GOBLIN_UNARMED_MELEE_IN_3, NpcID.GOBLIN_UNARMED_MELEE_IN_4, NpcID.GOBLIN_UNARMED_MELEE_IN_5, NpcID.GOBLIN_UNARMED_MELEE_IN_6, NpcID.GOBLIN_UNARMED_MELEE_IN_7, NpcID.GOBLIN_UNARMED_MELEE_IN_8, NpcID.GOBLIN_ARMED, NpcID.GOBLIN_HELMET, NpcID.GOBLIN_RED_SOLDIER_1, NpcID.GOBLIN_GREEN_SOLDIER_1);
			killGoblin.conditionToHideInSidebar(rbs.hadBoneItem());
			var takeBones = new ItemStep(this, "Pick up the " + Text.titleCase(rbs) + ".", rbs.getBoneItem());
			killGoblin.addSubSteps(takeBones);

			collectBonesSteps.addStep(and(rbs.boneNearby(), not(rbs.hadBoneItem())), takeBones);
			collectBonesSteps.addStep(not(rbs.hadBoneItem()), killGoblin);
		}

		{
			var rbs = RagBoneState.BIG_FROG_LEG;

			killFrog = new NpcStep(this, NpcID.MEDIUM_FROG_NODROPS, new WorldPoint(3216, 3182, 0), "Kill a big frog in the Lumbridge Swamp and pick up its bone.", true);
			killFrog.conditionToHideInSidebar(rbs.hadBoneItem());
			var takeBones = new ItemStep(this, "Pick up the " + Text.titleCase(rbs) + ".", rbs.getBoneItem());
			killFrog.addSubSteps(takeBones);

			collectBonesSteps.addStep(and(rbs.boneNearby(), not(rbs.hadBoneItem())), takeBones);
			collectBonesSteps.addStep(not(rbs.hadBoneItem()), killFrog);
		}

		{
			var rbs = RagBoneState.MONKEY_PAW;

			killMonkey = new NpcStep(this, NpcID.MONKEY, new WorldPoint(2886, 3167, 0), "Kill a monkey on Karamja and pick up its bone.", true);
			killMonkey.conditionToHideInSidebar(rbs.hadBoneItem());
			var takeBones = new ItemStep(this, "Pick up the " + Text.titleCase(rbs) + ".", rbs.getBoneItem());
			killMonkey.addSubSteps(takeBones);

			collectBonesSteps.addStep(and(rbs.boneNearby(), not(rbs.hadBoneItem())), takeBones);
			collectBonesSteps.addStep(not(rbs.hadBoneItem()), killMonkey);
		}

		{
			var rbs = RagBoneState.GIANT_BAT_WING;

			enterKaramjaDungeon = new ObjectStep(this, ObjectID.VOLCANO_ENTRANCE, new WorldPoint(2857, 3169, 0), "Kill a giant bat in the Karamja Volcano Dungeon and pick up its bone.");
			killBat = new NpcStep(this, NpcID.SMALL_BAT, new WorldPoint(2858, 9572, 0), "Kill a giant bat in the Karamja Volcano Dungeon and pick up its bone.", true);
			killBat.addSubSteps(enterKaramjaDungeon);
			killBat.conditionToHideInSidebar(rbs.hadBoneItem());
			var takeBones = new ItemStep(this, "Pick up the " + Text.titleCase(rbs) + ".", rbs.getBoneItem());
			killBat.addSubSteps(takeBones);

			var killBatSteps = new ConditionalStep(this, enterKaramjaDungeon);
			killBatSteps.addStep(inKaramjaDungeon, killBat);

			collectBonesSteps.addStep(and(rbs.boneNearby(), not(rbs.hadBoneItem())), takeBones);
			collectBonesSteps.addStep(not(rbs.hadBoneItem()), killBatSteps);
		}

		collectBonesSteps.setLockingCondition(hadAllBones);

		talkToFortunato = new NpcStep(this, NpcID.RAG_WINE_MERCHANT, new WorldPoint(3085, 3251, 0),
			"Talk to Fortunato in Draynor Village, and then buy 8 jugs of vinegar from him.", coins.quantity(8));

		makePotOfVinegar = new DetailedQuestStep(this, "Use the vinegar on pots for 8 pots of vinegar.",
			jugOfVinegarNeeded, potNeeded);

		useBonesOnVinegar = new DetailedQuestStep(this, "Use the bones on the pots of vinegar.", potOfVinegar.highlighted());
		useBonesOnVinegar.addItemRequirements(RagBoneGroups.bonesToAddToVinegar(RagBoneGroups.getRagBoneIStates()));

		placeLogs = new ObjectStep(this, ObjectID.RAG_MULTI_POTBOILER, new WorldPoint(3360, 3505, 0),
			"Place logs under the pot-boiler near the Odd Old Man. If you've already polished all the bones, hand " +
				"them in to the Odd Old Man.", logs.highlighted());
		placeLogs.addIcon(ItemID.LOGS);

		useBoneOnBoiler = new ObjectStep(this, ObjectID.RAG_MULTI_POTBOILER, new WorldPoint(3360, 3505, 0),
			"Add a bone to the pot boiler.");
		useBoneOnBoiler.addIcon(ItemID.RAG_POT_GOBLIN_BONE);
		useBoneOnBoiler.addItemRequirements(RagBoneGroups.bonesToAddToBoiler(RagBoneGroups.getRagBoneIStates()));

		lightLogs = new ObjectStep(this, ObjectID.RAG_MULTI_POTBOILER, new WorldPoint(3360, 3505, 0),
			"Light the logs under the pot-boiler.", tinderbox.highlighted());
		lightLogs.addIcon(ItemID.TINDERBOX);

		waitForCooking = new DetailedQuestStep(this, "Wait for the bones to be cleaned. You can hop worlds to make this happen instantly.");

		removePot = new ObjectStep(this, ObjectID.RAG_MULTI_POTBOILER, new WorldPoint(3360, 3505, 0),
			"Take the pot from the pot-boiler.");

		giveBones = new NpcStep(this, NpcID.RAG_ODD_OLD_MAN, new WorldPoint(3362, 3502, 0),
			"Give the Odd Old Man the bones.");
		giveBones.addItemRequirements(RagBoneGroups.cleanBonesNotHandedIn(RagBoneGroups.getRagBoneIStates()));

		talkToFinish = new NpcStep(this, NpcID.RAG_ODD_OLD_MAN, new WorldPoint(3362, 3502, 0),
			"Talk to the Odd Old Man to finish.");
		giveBones.addSubSteps(talkToFinish);

		repeatSteps = new DetailedQuestStep(this, "Repeat the steps until all the bones are cleaned.");

		preparingBonesSteps = new ConditionalStep(this, talkToFortunato);
		preparingBonesSteps.addStep(potOfVinegarNeeded, useBonesOnVinegar);
		preparingBonesSteps.addStep(talkedToFortunato, makePotOfVinegar);
		preparingBonesSteps.setLockingCondition(allBonesAtLeastAddedToVinegar);

		cookingSteps = new ConditionalStep(this, placeLogs);
		cookingSteps.addStep(boneReady, removePot);
		cookingSteps.addStep(logLit, waitForCooking);
		cookingSteps.addStep(boneAddedToBoiler, lightLogs);
		cookingSteps.addStep(logAdded, useBoneOnBoiler);
		cookingSteps.setLockingCondition(allBonesPolished);

	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();
		steps.put(0, talkToOddOldMan);
		steps.put(1, talkToOddOldMan);

		var doQuest = new ConditionalStep(this, collectBonesSteps);
		doQuest.addStep(allBonesPolished, giveBones);
		doQuest.addStep(allBonesAtLeastAddedToVinegar, cookingSteps);
		doQuest.addStep(hadAllBones, preparingBonesSteps);

		steps.put(2, doQuest);

		steps.put(3, talkToFinish);

		return steps;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			coins.quantity(8),
			pots.quantity(8),
			logs.quantity(8),
			tinderbox
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			combatGear,
			varrockTeleport,
			lumbridgeTeleport,
			digsitePendant,
			draynorTeleport,
			karamjaTeleport
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"8 low leveled monsters for their bones"
		);
	}


	@Override
	public List<String> getNotes()
	{
		return List.of(
			"If you've handed in any bones to the Odd Old Man, open the quest journal to sync up the helper's state"
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
			new ExperienceReward(Skill.COOKING, 500),
			new ExperienceReward(Skill.PRAYER, 500)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting out", Collections.singletonList(talkToOddOldMan)));

		var collectingPanel = new PanelDetails("Collecting bones", List.of(
			killGiantRat,
			killUnicorn,
			killBear,
			killRam,
			killGoblin,
			killFrog,
			killMonkey,
			killBat
		), List.of(
			// no requirements
		), List.of(
			combatGear
		));
		collectingPanel.setLockingStep(collectBonesSteps);
		sections.add(collectingPanel);

		var dirtyBones = new ArrayList<Requirement>(Arrays.asList(coins.quantity(8), pots.quantity(8)));
		dirtyBones.addAll(RagBoneGroups.dirtyBonesNotHandedIn(RagBoneGroups.getRagBoneIStates()));
		var preparingPanel = new PanelDetails("Preparing the bones", List.of(
			talkToFortunato,
			makePotOfVinegar,
			useBonesOnVinegar
		), dirtyBones);
		preparingPanel.setLockingStep(preparingBonesSteps);
		sections.add(preparingPanel);

		var cleaningBones = new ArrayList<Requirement>(Arrays.asList(logs.quantity(8), tinderbox));
		cleaningBones.addAll(RagBoneGroups.vinegarBonesNotHandedIn(RagBoneGroups.getRagBoneIStates()));
		var cookingPanel = new PanelDetails("Cleaning the bones", List.of(
			placeLogs,
			useBoneOnBoiler,
			lightLogs,
			waitForCooking,
			removePot,
			repeatSteps
		), cleaningBones);
		cookingPanel.setLockingStep(cookingSteps);
		sections.add(cookingPanel);

		var cleanedBones = new ArrayList<Requirement>(RagBoneGroups.cleanBonesNotHandedIn(RagBoneGroups.getRagBoneIStates()));
		sections.add(new PanelDetails("Handing the bones in", List.of(
			giveBones
		), cleanedBones));

		return sections;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		var winesNeededQuantity = new AtomicInteger(8);

		for (var state : RagBoneState.values())
		{
			if (state.hadBoneInVinegarItem().check(client))
			{
				winesNeededQuantity.getAndDecrement();
			}
		}

		potOfVinegarNeeded.setQuantity(winesNeededQuantity.get());

		int jugsNeeded = winesNeededQuantity.get();
		jugsNeeded -= potOfVinegar.checkTotalMatchesInContainers(QuestContainerManager.getEquippedData(), QuestContainerManager.getInventoryData(), QuestContainerManager.getBankData());
		potNeeded.setQuantity(jugsNeeded);
		jugOfVinegarNeeded.setQuantity(jugsNeeded);

		// Need to know how many pots of vinegar needed, and if missing some
	}
}
