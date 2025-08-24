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

import com.questhelper.collections.ItemCollections;
import com.questhelper.managers.QuestContainerManager;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.*;
import com.questhelper.tools.QuestTile;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.eventbus.Subscribe;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.questhelper.requirements.util.LogicHelper.nor;

public class RagAndBoneManI extends BasicQuestHelper
{
	//Items Required
	ItemRequirement coins, pots, logs, tinderbox, lightSource, rope;

	//Items Recommended
	ItemRequirement spinyHelmet, varrockTeleport, lumbridgeTeleport, digsitePendant,
		draynorTeleport, karamjaTeleport, dramenStaff;

	ItemRequirement jugOfVinegar, jugOfVinegarNeeded, potOfVinegar, potOfVinegarNeeded, potNeeded;

	DetailedQuestStep talkToOddOldMan, killGiantRat, killUnicorn, killBear, killRam, killGoblin, killFrog, killMonkey
		, killBat, pickupBone;

	DetailedQuestStep addRope, enterSwamp, leaveJunaRoom, enterKaramjaDungeon;

	DetailedQuestStep talkToFortunato, makePotOfVinegar, useBonesOnVinegar;

	DetailedQuestStep placeLogs, useBoneOnBoiler, lightLogs, waitForCooking, removePot, repeatSteps;

	DetailedQuestStep giveBones, talkToFinish;

	Requirement inSwamp, inJunaRoom, inKaramjaDungeon, addedRope, boneNearby, hadAllBones, talkedToFortunato;

	Requirement hadVinegar, allBonesAtLeastAddedToVinegar, allBonesPolished;

	Requirement logAdded, boneAddedToBoiler, logLit, boneReady;

	Zone swamp, junaRoom, karamjaDungeon;

	ConditionalStep collectBonesSteps, preparingBonesSteps, cookingSteps;

	LinkedHashMap<RagBoneState, QuestStep> stepsForRagAndBoneManI = new LinkedHashMap<>();

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();
		steps.put(0, talkToOddOldMan);
		steps.put(1, talkToOddOldMan);

		collectBonesSteps = new ConditionalStep(this, new DetailedQuestStep(this, "Unknown state."));
		collectBonesSteps.addStep(boneNearby, pickupBone);
		stepsForRagAndBoneManI.forEach((RagBoneState state, QuestStep step) -> collectBonesSteps.addStep(nor(state.hadBoneItem(questBank)), step));
		collectBonesSteps.setLockingCondition(hadAllBones);

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

		ConditionalStep doQuest = new ConditionalStep(this, collectBonesSteps);
		doQuest.addStep(allBonesPolished, giveBones);
		doQuest.addStep(allBonesAtLeastAddedToVinegar, cookingSteps);
		doQuest.addStep(hadAllBones, preparingBonesSteps);

		steps.put(2, doQuest);

		steps.put(3, talkToFinish);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		// Required items
		coins = new ItemRequirement("Coins", ItemCollections.COINS);
		pots = new ItemRequirement("Pot", ItemID.POT_EMPTY).isNotConsumed();
		potNeeded = new ItemRequirement("Pot", ItemID.POT_EMPTY, 8).alsoCheckBank(questBank).highlighted();
		logs = new ItemRequirement("Logs", ItemID.LOGS);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).isNotConsumed();
		lightSource = new ItemRequirement("Light source", ItemCollections.LIGHT_SOURCES).isNotConsumed();

		// Optional items
		rope = new ItemRequirement("Rope", ItemID.ROPE);
		spinyHelmet = new ItemRequirement("Spiny helmet", ItemID.WALLBEAST_SPIKE_HELMET);
		varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.POH_TABLET_VARROCKTELEPORT);
		lumbridgeTeleport = new ItemRequirement("Lumbridge teleport", ItemID.POH_TABLET_LUMBRIDGETELEPORT);
		digsitePendant = new ItemRequirement("Digsite pendant", ItemCollections.DIGSITE_PENDANTS);
		draynorTeleport = new ItemRequirement("Draynor teleport", ItemCollections.AMULET_OF_GLORIES);
		draynorTeleport.addAlternates(ItemID.TELETAB_DRAYNOR);
		karamjaTeleport = new ItemRequirement("Karamja teleport", ItemCollections.AMULET_OF_GLORIES);
		karamjaTeleport.addAlternates(ItemID.NZONE_TELETAB_BRIMHAVEN, ItemID.TELEPORTSCROLL_TAIBWO);
		dramenStaff = new ItemRequirement("Dramen staff for fairy rings", ItemID.DRAMEN_STAFF).isNotConsumed();

		// Quest items
		jugOfVinegar = new ItemRequirement("Jar of vinegar", ItemID.RAG_VINEGAR);
		potOfVinegar = new ItemRequirement("Pot of vinegar", ItemID.RAG_POT_VINEGAR);
		potOfVinegarNeeded =
			new ItemRequirement("Pot of vinegar", ItemID.RAG_POT_VINEGAR, 8).alsoCheckBank(questBank).highlighted();
		jugOfVinegarNeeded =
			new ItemRequirement("Jug of vinegar", ItemID.RAG_VINEGAR, 8).alsoCheckBank(questBank).highlighted();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		AtomicInteger winesNeededQuantity = new AtomicInteger(8);

		stepsForRagAndBoneManI.forEach((RagBoneState state, QuestStep step) -> {
			if (state.hadBoneInVinegarItem(questBank).check(client))
			{
				winesNeededQuantity.getAndDecrement();
			}
		});

		potOfVinegarNeeded.setQuantity(winesNeededQuantity.get());

		int jugsNeeded = winesNeededQuantity.get();
		jugsNeeded -= potOfVinegar.checkTotalMatchesInContainers(QuestContainerManager.getEquippedData(), QuestContainerManager.getInventoryData(), QuestContainerManager.getBankData());
		potNeeded.setQuantity(jugsNeeded);
		jugOfVinegarNeeded.setQuantity(jugsNeeded);

		// Need to know how many pots of vinegar needed, and if missing some
	}

	@Override
	protected void setupZones()
	{
		swamp = new Zone(new WorldPoint(3138, 9536, 0), new WorldPoint(3261, 9601, 0));
		junaRoom = new Zone(new WorldPoint(3205, 9484, 0), new WorldPoint(3263, 9537, 2));
		karamjaDungeon = new Zone(new WorldPoint(2827, 9547, 0), new WorldPoint(2867, 9599, 0));
	}

	private void setupConditions()
	{
		inSwamp = new ZoneRequirement(swamp);
		inJunaRoom = new ZoneRequirement(junaRoom);
		inKaramjaDungeon = new ZoneRequirement(karamjaDungeon);

		// 2044 = 1, talked a bit to Odd Old Man

		addedRope = new VarbitRequirement(279, 1);

		boneNearby = new Conditions(LogicType.OR, RagBoneGroups.getBonesOnFloor(RagBoneGroups.getBones(RagBoneGroups.getRagBoneIStates())));

		logAdded = new VarbitRequirement(VarbitID.RAG_BOILER, 1, Operation.GREATER_EQUAL);
		boneAddedToBoiler = new VarbitRequirement(VarbitID.RAG_BOILER, 2, Operation.GREATER_EQUAL);
		logLit = new VarbitRequirement(VarbitID.RAG_BOILER, 3, Operation.GREATER_EQUAL);
		boneReady = new VarbitRequirement(2046, 4);

		// Every time handing in a bone, 2045 iterates from 0->28 1 by 1. Next time you hand in a bone it goes back
		// to 0 and repeats???

		allBonesPolished = new Conditions(RagBoneGroups.allBonesPolished(RagBoneGroups.getRagBoneIStates(), questBank));

		allBonesAtLeastAddedToVinegar = new Conditions(RagBoneGroups.allBonesAddedToVinegar(RagBoneGroups.getRagBoneIStates(), questBank));

		hadAllBones = new Conditions(RagBoneGroups.allBonesObtained(RagBoneGroups.getRagBoneIStates(), questBank));

		talkedToFortunato = new VarbitRequirement(2047, 1);

		hadVinegar = new Conditions(jugOfVinegar.alsoCheckBank(questBank));
	}

	public void setupSteps()
	{
		talkToOddOldMan = new NpcStep(this, NpcID.RAG_ODD_OLD_MAN, new WorldPoint(3362, 3502, 0),
			"Talk to the Odd Old Man east of Varrock.");
		talkToOddOldMan.addDialogSteps("Anything I can do to help?", "Yes.");

		killGiantRat = new NpcStep(this, NpcID.GIANTRAT, new WorldPoint(3289, 3373, 0),
			"Kill a giant rat south east of Varrock.", true);
		((NpcStep) killGiantRat).addAlternateNpcs(NpcID.GIANTRAT2, NpcID.GIANTRAT3, NpcID.GIANTRAT_GREY,
			NpcID.GIANTRAT_GREY2, NpcID.GIANTRAT_GREY3, NpcID.GIANTRAT1, NpcID.GIANTRAT1_2,
			NpcID.GIANTRAT1_3);

		killUnicorn = new NpcStep(this, NpcID.UNICORN, new WorldPoint(3285, 3351, 0),
			"Kill the unicorn south east of Varrock.", true);
		killBear = new NpcStep(this, NpcID.DARKBEAR, new WorldPoint(3295, 3354, 0),
			"Kill the bear south east of Varrock.", true);
		killRam = new NpcStep(this, NpcID.RAMSHEERED, new WorldPoint(3253, 3350, 0),
			"Kill a ram south of Varrock.", true);
		((NpcStep) killRam).addAlternateNpcs(NpcID.RAMUNSHEERED, NpcID.RAMUNSHEERED2, NpcID.RAMUNSHEERED3, NpcID.RAMUNSHEEREDSHAGGY);

		killGoblin = new NpcStep(this, NpcID.GOBLIN_RED_SOLDIER_2, new WorldPoint(3252, 3251, 0),
			"Kill a goblin east of Lumbridge.", true);
		((NpcStep) killGoblin).addAlternateNpcs(NpcID.GOBLIN_UNARMED_MELEE_1, NpcID.GOBLIN_UNARMED_MELEE_2, NpcID.GOBLIN_UNARMED_MELEE_3,
			NpcID.GOBLIN_UNARMED_MELEE_4, NpcID.GOBLIN_UNARMED_MELEE_5, NpcID.GOBLIN_UNARMED_MELEE_6, NpcID.GOBLIN_UNARMED_MELEE_7, NpcID.GOBLIN_UNARMED_MELEE_8,
			NpcID.GOBLIN_UNARMED_MELEE_IN_1, NpcID.GOBLIN_UNARMED_MELEE_IN_2, NpcID.GOBLIN_UNARMED_MELEE_IN_3, NpcID.GOBLIN_UNARMED_MELEE_IN_4, NpcID.GOBLIN_UNARMED_MELEE_IN_5,
			NpcID.GOBLIN_UNARMED_MELEE_IN_6, NpcID.GOBLIN_UNARMED_MELEE_IN_7, NpcID.GOBLIN_UNARMED_MELEE_IN_8, NpcID.GOBLIN_ARMED, NpcID.GOBLIN_HELMET,
			NpcID.GOBLIN_RED_SOLDIER_1, NpcID.GOBLIN_GREEN_SOLDIER_1);

		addRope = new ObjectStep(this, ObjectID.GOBLIN_CAVE_ENTRANCE, new WorldPoint(3169, 3172, 0),
			"Enter the hole to the Lumbridge Swamp caves.", rope.highlighted(), lightSource, tinderbox);
		addRope.addIcon(ItemID.ROPE);
		leaveJunaRoom = new ObjectStep(this, ObjectID.TOG_CAVE_UP, new WorldPoint(3219, 9534, 2),
			"Enter the Lumbridge Swamp caves.");
		enterSwamp = new ObjectStep(this, ObjectID.GOBLIN_CAVE_ENTRANCE, new WorldPoint(3169, 3172, 0),
			"Enter the hole to the Lumbridge Swamp caves.", lightSource, tinderbox);
		enterSwamp.addSubSteps(addRope, leaveJunaRoom);
		killFrog = new NpcStep(this, NpcID.MEDIUM_FROG, new WorldPoint(3153, 9558, 0),
			"Kill a big frog in the south west of the caves. Make sure to RUN between the two marked run tiles to " +
				"avoid the Wall Beast.",	true);
		killFrog.addTileMarker(new QuestTile(new WorldPoint(3161, 9574, 0), SpriteID.OptionsIcons.RUNNING));
		killFrog.addTileMarker(new QuestTile(new WorldPoint(3163, 9574, 0), SpriteID.OptionsIcons.RUNNING));

		ConditionalStep killFrogSteps = new ConditionalStep(this, addRope);
		killFrogSteps.addStep(inSwamp, killFrog);
		killFrogSteps.addStep(inJunaRoom, leaveJunaRoom);
		killFrogSteps.addStep(addedRope, enterSwamp);

		killMonkey = new NpcStep(this, NpcID.MONKEY, new WorldPoint(2886, 3167, 0),
			"Kill a monkey on Karamja.", true);

		enterKaramjaDungeon = new ObjectStep(this, ObjectID.VOLCANO_ENTRANCE, new WorldPoint(2857, 3169, 0),
			"Kill a giant bat in the Karamja Volcano Dungeon.");
		killBat = new NpcStep(this, NpcID.SMALL_BAT, new WorldPoint(2858, 9572, 0),
			"Kill a giant bat in the Karamja Volcano Dungeon.", true);
		killBat.addSubSteps(enterKaramjaDungeon);

		ConditionalStep killBatSteps = new ConditionalStep(this, enterKaramjaDungeon);
		killBatSteps.addStep(inKaramjaDungeon, killBat);

		stepsForRagAndBoneManI.put(RagBoneState.GIANT_RAT_BONE, killGiantRat);
		stepsForRagAndBoneManI.put(RagBoneState.UNICORN_BONE, killUnicorn);
		stepsForRagAndBoneManI.put(RagBoneState.BEAR_RIBS, killBear);
		stepsForRagAndBoneManI.put(RagBoneState.RAM_SKULL, killRam);

		stepsForRagAndBoneManI.put(RagBoneState.GOBLIN_SKULL, killGoblin);
		stepsForRagAndBoneManI.put(RagBoneState.BIG_FROG_LEG, killFrogSteps);
		stepsForRagAndBoneManI.put(RagBoneState.MONKEY_PAW, killMonkey);
		stepsForRagAndBoneManI.put(RagBoneState.GIANT_BAT_WING, killBatSteps);

		pickupBone = new ItemStep(this, "Pickup the bone.");
		pickupBone.addItemRequirements(RagBoneGroups.pickupBones(RagBoneGroups.getRagBoneIStates()));
		pickupBone.setShowInSidebar(false);

		talkToFortunato = new NpcStep(this, NpcID.RAG_WINE_MERCHANT, new WorldPoint(3085, 3251, 0),
			"Talk to Fortunato in Draynor Village, and then buy 8 jugs of vinegar from him.", coins.quantity(8));

		makePotOfVinegar = new DetailedQuestStep(this, "Use the vinegar on pots for 8 pots of vinegar.",
			jugOfVinegarNeeded, potNeeded);

		useBonesOnVinegar = new DetailedQuestStep(this, "Use the bones on the pots of vinegar.", potOfVinegar.highlighted());
		useBonesOnVinegar.addItemRequirements(RagBoneGroups.bonesToAddToVinegar(RagBoneGroups.getRagBoneIStates(), questBank));

		placeLogs = new ObjectStep(this, ObjectID.RAG_MULTI_POTBOILER, new WorldPoint(3360, 3505, 0),
			"Place logs under the pot-boiler near the Odd Old Man. If you've already polished all the bones, hand " +
				"them in to the Odd Old Man.", logs.highlighted());
		placeLogs.addIcon(ItemID.LOGS);

		useBoneOnBoiler = new ObjectStep(this, ObjectID.RAG_MULTI_POTBOILER, new WorldPoint(3360, 3505, 0),
			"Add a bone to the pot boiler.");
		useBoneOnBoiler.addIcon(ItemID.RAG_POT_GOBLIN_BONE);
		useBoneOnBoiler.addItemRequirements(RagBoneGroups.bonesToAddToBoiler(RagBoneGroups.getRagBoneIStates(), questBank));

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
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("8 low leveled monsters for their bones");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(coins.quantity(8), pots.quantity(8), logs.quantity(8), tinderbox,
			lightSource, rope.hideConditioned(addedRope));
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(varrockTeleport, lumbridgeTeleport, digsitePendant,
			draynorTeleport, karamjaTeleport);
	}

	@Override
	public List<String> getNotes()
	{
		return Collections.singletonList("If you've handed in any bones to the Odd Old Man, open the quest journal to" +
			" sync up the helper's state");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.COOKING, 500),
				new ExperienceReward(Skill.PRAYER, 500));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting out", Collections.singletonList(talkToOddOldMan)));

		PanelDetails collectingPanel = new PanelDetails("Collecting bones", Arrays.asList(killGiantRat, killUnicorn, killBear, killRam,
			killGoblin, enterSwamp, killFrog, killMonkey, killBat, pickupBone), tinderbox, lightSource,
			rope.hideConditioned(addedRope));
		collectingPanel.setLockingStep(collectBonesSteps);
		allSteps.add(collectingPanel);

		List<Requirement> dirtyBones = new ArrayList<>(Arrays.asList(coins.quantity(8), pots.quantity(8)));
		dirtyBones.addAll(RagBoneGroups.dirtyBonesNotHandedIn(RagBoneGroups.getRagBoneIStates()));
		PanelDetails preparingPanel = new PanelDetails("Preparing the bones", Arrays.asList(talkToFortunato, makePotOfVinegar, useBonesOnVinegar),
			dirtyBones);
		preparingPanel.setLockingStep(preparingBonesSteps);
		allSteps.add(preparingPanel);

		List<Requirement> cleaningBones = new ArrayList<>(Arrays.asList(logs.quantity(8), tinderbox));
		cleaningBones.addAll(RagBoneGroups.vinegarBonesNotHandedIn(RagBoneGroups.getRagBoneIStates()));
		PanelDetails cookingPanel = new PanelDetails("Cleaning the bones", Arrays.asList(placeLogs, useBoneOnBoiler, lightLogs,
			waitForCooking, removePot, repeatSteps), cleaningBones);
		cookingPanel.setLockingStep(cookingSteps);
		allSteps.add(cookingPanel);

		List<Requirement> cleanedBones = new ArrayList<>(RagBoneGroups.cleanBonesNotHandedIn(RagBoneGroups.getRagBoneIStates()));
		allSteps.add(new PanelDetails("Handing the bones in", Collections.singletonList(giveBones),
			cleanedBones));

		return allSteps;
	}
}
