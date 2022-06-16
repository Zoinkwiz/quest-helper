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
package com.questhelper.quests.ragandboneman;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestTile;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

@QuestDescriptor(
	quest = QuestHelperQuest.RAG_AND_BONE_MAN_I
)
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
		setupRequirements();
		setupZones();
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
	public void setupRequirements()
	{
		// Required items
		coins = new ItemRequirement("Coins", ItemCollections.COINS);
		pots = new ItemRequirement("Pot", ItemID.POT);
		potNeeded = new ItemRequirement("Pot", ItemID.POT, 8).alsoCheckBank(questBank).highlighted();
		logs = new ItemRequirement("Logs", ItemID.LOGS);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		lightSource = new ItemRequirement("Light source", ItemCollections.LIGHT_SOURCES);

		// Optional items
		rope = new ItemRequirement("Rope", ItemID.ROPE);
		spinyHelmet = new ItemRequirement("Spiny helmet", ItemID.SPINY_HELMET);
		varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.VARROCK_TELEPORT);
		lumbridgeTeleport = new ItemRequirement("Lumbridge teleport", ItemID.LUMBRIDGE_TELEPORT);
		digsitePendant = new ItemRequirement("Digsite pendant", ItemCollections.DIGSITE_PENDANTS);
		draynorTeleport = new ItemRequirement("Draynor teleport", ItemCollections.AMULET_OF_GLORIES);
		draynorTeleport.addAlternates(ItemID.DRAYNOR_MANOR_TELEPORT);
		karamjaTeleport = new ItemRequirement("Karamja teleport", ItemCollections.AMULET_OF_GLORIES);
		karamjaTeleport.addAlternates(ItemID.BRIMHAVEN_TELEPORT, ItemID.TAI_BWO_WANNAI_TELEPORT);
		dramenStaff = new ItemRequirement("Dramen staff for fairy rings", ItemID.DRAMEN_STAFF);

		// Quest items
		jugOfVinegar = new ItemRequirement("Jar of vinegar", ItemID.JUG_OF_VINEGAR);
		potOfVinegar = new ItemRequirement("Pot of vinegar", ItemID.POT_OF_VINEGAR);
		potOfVinegarNeeded =
			new ItemRequirement("Pot of vinegar", ItemID.POT_OF_VINEGAR, 8).alsoCheckBank(questBank).highlighted();
		jugOfVinegarNeeded =
			new ItemRequirement("Jug of vinegar", ItemID.JUG_OF_VINEGAR, 8).alsoCheckBank(questBank).highlighted();
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
		jugsNeeded -= potOfVinegar.alsoCheckBank(questBank).getMatches(client);
		potNeeded.setQuantity(jugsNeeded);
		jugOfVinegarNeeded.setQuantity(jugsNeeded);

		// Need to know how many pots of vinegar needed, and if missing some
	}

	private void setupZones()
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

		logAdded = new VarbitRequirement(2046, 1, Operation.GREATER_EQUAL);
		boneAddedToBoiler = new VarbitRequirement(2046, 2, Operation.GREATER_EQUAL);
		logLit = new VarbitRequirement(2046, 3, Operation.GREATER_EQUAL);
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
		talkToOddOldMan = new NpcStep(this, NpcID.ODD_OLD_MAN, new WorldPoint(3362, 3502, 0),
			"Talk to the Odd Old Man east of Varrock.");
		talkToOddOldMan.addDialogSteps("Anything I can do to help?", "Yes");

		killGiantRat = new NpcStep(this, NpcID.GIANT_RAT_2856, new WorldPoint(3289, 3373, 0),
			"Kill a giant rat south east of Varrock.", true);
		((NpcStep) killGiantRat).addAlternateNpcs(NpcID.GIANT_RAT_2857, NpcID.GIANT_RAT_2858, NpcID.GIANT_RAT_2859,
			NpcID.GIANT_RAT_2860, NpcID.GIANT_RAT_2861, NpcID.GIANT_RAT_2862, NpcID.GIANT_RAT_2863,
			NpcID.GIANT_RAT_2864);

		killUnicorn = new NpcStep(this, NpcID.UNICORN, new WorldPoint(3285, 3351, 0),
			"Kill the unicorn south east of Varrock.", true);
		killBear = new NpcStep(this, NpcID.BLACK_BEAR, new WorldPoint(3295, 3354, 0),
			"Kill the bear south east of Varrock.", true);
		killRam = new NpcStep(this, NpcID.RAM, new WorldPoint(3253, 3350, 0),
			"Kill a ram south of Varrock.", true);
		((NpcStep) killRam).addAlternateNpcs(NpcID.RAM_1262, NpcID.RAM_1263, NpcID.RAM_1264, NpcID.RAM_1265);

		killGoblin = new NpcStep(this, NpcID.GOBLIN_3028, new WorldPoint(3252, 3251, 0),
			"Kill a goblin east of Lumbridge.", true);
		((NpcStep) killGoblin).addAlternateNpcs(NpcID.GOBLIN_3029, NpcID.GOBLIN_3030, NpcID.GOBLIN_3031,
			NpcID.GOBLIN_3032, NpcID.GOBLIN_3033, NpcID.GOBLIN_3034, NpcID.GOBLIN_3035, NpcID.GOBLIN_3036,
			NpcID.GOBLIN_3037, NpcID.GOBLIN_3038, NpcID.GOBLIN_3039, NpcID.GOBLIN_3040, NpcID.GOBLIN_3041,
			NpcID.GOBLIN_3042, NpcID.GOBLIN_3043, NpcID.GOBLIN_3044, NpcID.GOBLIN_3045, NpcID.GOBLIN_3046,
			NpcID.GOBLIN_3047, NpcID.GOBLIN_3048);

		addRope = new ObjectStep(this, ObjectID.DARK_HOLE, new WorldPoint(3169, 3172, 0),
			"Enter the hole to the Lumbridge Swamp caves.", rope.highlighted(), lightSource, tinderbox);
		addRope.addIcon(ItemID.ROPE);
		leaveJunaRoom = new ObjectStep(this, ObjectID.TUNNEL_6658, new WorldPoint(3219, 9534, 2),
			"Enter the Lumbridge Swamp caves.");
		enterSwamp = new ObjectStep(this, ObjectID.DARK_HOLE, new WorldPoint(3169, 3172, 0),
			"Enter the hole to the Lumbridge Swamp caves.", lightSource, tinderbox);
		enterSwamp.addSubSteps(addRope, leaveJunaRoom);
		killFrog = new NpcStep(this, NpcID.BIG_FROG, new WorldPoint(3153, 9558, 0),
			"Kill a big frog in the south west of the caves. Make sure to RUN between the two marked run tiles to " +
				"avoid the Wall Beast.",	true);
		killFrog.addTileMarker(new QuestTile(new WorldPoint(3161, 9574, 0), SpriteID.OPTIONS_RUNNING));
		killFrog.addTileMarker(new QuestTile(new WorldPoint(3163, 9574, 0), SpriteID.OPTIONS_RUNNING));

		ConditionalStep killFrogSteps = new ConditionalStep(this, addRope);
		killFrogSteps.addStep(inSwamp, killFrog);
		killFrogSteps.addStep(inJunaRoom, leaveJunaRoom);
		killFrogSteps.addStep(addedRope, enterSwamp);

		killMonkey = new NpcStep(this, NpcID.MONKEY_2848, new WorldPoint(2886, 3167, 0),
			"Kill a monkey on karamja.", true);

		enterKaramjaDungeon = new ObjectStep(this, ObjectID.ROCKS_11441, new WorldPoint(2857, 3169, 0),
			"Kill a giant bat in the Karamja Volcano Dungeon.");
		killBat = new NpcStep(this, NpcID.GIANT_BAT, new WorldPoint(2858, 9572, 0),
			"Kill a giant bat in the Karamja Volcano Dungeon.", true);
		killBat.addSubSteps(enterKaramjaDungeon);

		ConditionalStep killBatSteps = new ConditionalStep(this, enterKaramjaDungeon);
		killBatSteps.addStep(inKaramjaDungeon, killBat);

		stepsForRagAndBoneManI.put(RagBoneState.GIANT_RAT_BONE, killGiantRat);
		stepsForRagAndBoneManI.put(RagBoneState.UNICORN_BONE, killUnicorn);
		stepsForRagAndBoneManI.put(RagBoneState.BEAR_RIBS, killBear);
		stepsForRagAndBoneManI.put(RagBoneState.RAM_SKULL, killRam);

		stepsForRagAndBoneManI.put(RagBoneState.GOBLIN_SKULL, killGoblin);
		stepsForRagAndBoneManI.put(RagBoneState.BIG_FROG_LEGS, killFrogSteps);
		stepsForRagAndBoneManI.put(RagBoneState.MONKEY_PAW, killMonkey);
		stepsForRagAndBoneManI.put(RagBoneState.GIANT_BAT_WING, killBatSteps);

		pickupBone = new ItemStep(this, "Pickup the bone.");
		pickupBone.addItemRequirements(RagBoneGroups.pickupBones(RagBoneGroups.getRagBoneIStates()));
		pickupBone.setShowInSidebar(false);

		talkToFortunato = new NpcStep(this, NpcID.FORTUNATO, new WorldPoint(3085, 3251, 0),
			"Talk to Fortunato in Draynor Village, and then buy 8 jugs of vinegar from him.", coins.quantity(8));

		makePotOfVinegar = new DetailedQuestStep(this, "Use the vinegar on pots for 8 pots of vinegar.",
			jugOfVinegarNeeded, potNeeded);

		useBonesOnVinegar = new DetailedQuestStep(this, "Use the bones on the pots of vinegar.", potOfVinegar.highlighted());
		useBonesOnVinegar.addItemRequirements(RagBoneGroups.bonesToAddToVinegar(RagBoneGroups.getRagBoneIStates(), questBank));

		placeLogs = new ObjectStep(this, NullObjectID.NULL_14004, new WorldPoint(3360, 3505, 0),
			"Place logs under the pot-boiler near the Odd Old Man. If you've already polished all the bones, hand " +
				"them in to the Odd Old Man.", logs.highlighted());
		placeLogs.addIcon(ItemID.LOGS);

		useBoneOnBoiler = new ObjectStep(this, NullObjectID.NULL_14004, new WorldPoint(3360, 3505, 0),
			"Add a bone to the pot boiler.");
		useBoneOnBoiler.addIcon(ItemID.BONE_IN_VINEGAR);
		useBoneOnBoiler.addItemRequirements(RagBoneGroups.bonesToAddToBoiler(RagBoneGroups.getRagBoneIStates(), questBank));

		lightLogs = new ObjectStep(this, NullObjectID.NULL_14004, new WorldPoint(3360, 3505, 0),
			"Light the logs under the pot-boiler.", tinderbox.highlighted());
		lightLogs.addIcon(ItemID.TINDERBOX);

		waitForCooking = new DetailedQuestStep(this, "Wait for the bones to be cleaned. You can hop worlds to make this happen instantly.");

		removePot = new ObjectStep(this, NullObjectID.NULL_14004, new WorldPoint(3360, 3505, 0),
			"Take the pot from the pot-boiler.");

		giveBones = new NpcStep(this, NpcID.ODD_OLD_MAN, new WorldPoint(3362, 3502, 0),
		"Give the Odd Old Man the bones.");
		giveBones.addItemRequirements(RagBoneGroups.cleanBonesNotHandedIn(RagBoneGroups.getRagBoneIStates()));

		talkToFinish = new NpcStep(this, NpcID.ODD_OLD_MAN, new WorldPoint(3362, 3502, 0),
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
