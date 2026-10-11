/*
 * Copyright (c) 2026, leafyboat <https://github.com/leafyboat>
 * Copyright (c) 2026, pajlada <rasmus.karlsson@pajlada.com>
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
package com.questhelper.helpers.quests.aruffsituation;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.InInstanceRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import static com.questhelper.requirements.util.LogicHelper.not;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
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
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;

/**
 * <a href="https://oldschool.runescape.wiki/w/A_Ruff_Situation">The OSRS wiki guide</a> was referenced for this guide.
 */
public class ARuffSituation extends BasicQuestHelper
{
	// Required items
	ItemRequirement needleOrCostumeNeedle;
	ItemRequirement thread;
	ItemRequirement fur;
	ItemRequirement grain;

	// Recommended items
	ItemRequirement combatGear;
	ItemRequirement food;

	// Mid-quest item requirements
	ItemRequirement stuffedDog;

	// Miscellaneous requirements
	VarbitRequirement needsToInspectRoughBedding;
	VarbitRequirement needsToInspectChewedBox;
	VarbitRequirement needsToInspectTornNewspaper;
	VarplayerRequirement strayDogFollowing;
	VarbitRequirement inCutscene;
	InInstanceRequirement inInstance;
	ItemRequirement puppy1;
	ItemRequirement puppy2;
	ItemRequirement puppy3;

	// Steps
	NpcStep talkToTalia;
	NpcStep talkToGertrude;
	NpcStep interactWithStrayDog;
	NpcStep followStrayDogToDen;
	ConditionalStep cInspectDen;
	NpcStep interactWithStrayDogInDen;
	NpcStep followStrayDogToCooksGuild;
	NpcStep interactWithStrayDogAtCooksGuild;
	NpcStep talkToStrayDog;
	ConditionalStep cTalkToPicklenose;
	NpcStep talkToPicklenose;
	DetailedQuestStep makeStuffedDog;
	NpcStep talkToStrayDogAtGoblins;
	NpcStep talkToPicklenoseAgain;
	ConditionalStep cTalkToPicklenoseAgain;
	NpcStep talkToPicklenoseAgainAfterHandingHimTheStuffedDog;
	ConditionalStep cTalkToPicklenoseAgainAfterHandingHimTheStuffedDog;
	NpcStep pickUpStrayPuppy2;
	NpcStep talkToStrayDogOutsideCooksGuild;
	NpcStep followStrayDogToWall;
	DetailedQuestStep followStrayDogToWallWatchCutscene;
	ConditionalStep cFollowStrayDogToWallWatchCutscene;

	// 90 + 95 + 100
	NpcStep talkToDogToFight;
	NpcStep killOutlaws;
	ConditionalStep cTalkToDogAndKillOutlaws;
	NpcStep talkToDogAfterFight;
	ConditionalStep cKillOutlaws;

	// 105
	NpcStep talkToStrayDogAfterCollectingLastPuppy;

	// 110
	NpcStep pickupPuppy1FromDen;
	NpcStep pickupPuppy2FromDen;
	NpcStep pickupPuppy3FromDen;
	NpcStep talkToStrayDogInDenAfterCollectingLastPuppy;
	ConditionalStep cGetPuppies;

	// 115
	NpcStep talkToTaliaWithPuppies;
	ConditionalStep cTalkToTaliaWithPuppies;

	@Override
	protected void setupRequirements()
	{
		needleOrCostumeNeedle = new ItemRequirement("Needle or costume needle",
			List.of(ItemID.NEEDLE, ItemID.COSTUMENEEDLE)).isNotConsumed();

		// A costume needle comes with its own thread, so it satisfies this requirement on its own.
		thread = new ItemRequirement("Thread", List.of(ItemID.THREAD, ItemID.COSTUMENEEDLE));
		thread.setTooltip("Not needed if you are using a costume needle.");

		fur = new ItemRequirement("Fur", ItemID.WEREWOLVE_FUR);
		fur.addAlternates(ItemID.FUR, ItemID.GREY_WOLF_FUR, ItemID.HUNTING_FENNECFOX_FUR, ItemID.VARLAMORE_JAGUAR_FUR);

		grain = new ItemRequirement("Grain", ItemID.GRAIN);

		stuffedDog = new ItemRequirement("Stuffed dog", 34602);
		stuffedDog.canBeObtainedDuringQuest();
		stuffedDog.setTooltip("You can make another from grain and fur with a needle and thread.");

		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", -1, -1);
		food.setDisplayItemId(BankSlotIcons.getFood());

		puppy1 = new ItemRequirement("Puppy labrador", ItemID.DOGQ_PUPPY_ONE_OBJECT);
		puppy2 = new ItemRequirement("Puppy pug", ItemID.DOGQ_PUPPY_TWO_OBJECT);
		puppy3 = new ItemRequirement("Puppy spaniel", ItemID.DOGQ_PUPPY_THREE_OBJECT);

		needsToInspectRoughBedding = new VarbitRequirement(15890, 0);
		needsToInspectChewedBox = new VarbitRequirement(15893, 0);
		needsToInspectTornNewspaper = new VarbitRequirement(15891, 0);

		strayDogFollowing = new VarplayerRequirement(VarPlayerID.FOLLOWER_NPC, 16505, 16);
		inCutscene = new VarbitRequirement(VarbitID.CUTSCENE_STATUS, 1);
		inInstance = new InInstanceRequirement();
	}

	public void setupSteps()
	{
		talkToTalia = new NpcStep(this, 16486, new WorldPoint(3037, 3457, 0),
			"Talk to Talia at the dog shelter south-west of the Edgeville Monastery.");
		talkToTalia.addDialogStep("Yes.");

		talkToGertrude = new NpcStep(this, NpcID.GERTRUDE_POST, new WorldPoint(3151, 3413, 0),
			"Talk to Gertrude in her house west of Varrock, just south of the Cooks' Guild.");
		talkToGertrude.addAlternateNpcs(NpcID.GERTRUDE_QUEST);
		talkToGertrude.addDialogStep("Ask about lost dogs.");

		interactWithStrayDog = new NpcStep(this, 16504, new WorldPoint(3180, 3425, 0),
			"Interact with the stray dog barking at a guard, just south of the western Varrock bank.");

		followStrayDogToDen = new NpcStep(this, 16504, new WorldPoint(3180, 3425, 0),
			"Follow the stray dog to the house just west of Thessalia's Fine Clothes. " +
				"If she isn't there, return to the guard just south of the western Varrock bank.");
		followStrayDogToDen.addAlternateNpcs(16505);

		var inspectRoughBedding = new ObjectStep(this, 62485, new WorldPoint(3195, 3416, 0), "");
		var inspectChewedBox = new ObjectStep(this, 62491, new WorldPoint(3195, 3413, 0), "");
		var inspectTornNewspaper = new ObjectStep(this, 62488, new WorldPoint(3197, 3413, 0), "");

		interactWithStrayDogInDen = new NpcStep(this, 16504, new WorldPoint(3196, 3414, 0),
			"Interact with the stray dog again to have her pick up the puppies' scent.");
		interactWithStrayDogInDen.addAlternateNpcs(16505);

		cInspectDen = new ConditionalStep(this, inspectRoughBedding,
			"Inspect the chewed box, the torn newspaper and the rough bedding in the dog's den. " +
				"Finish the dialogue after each inspection.");
		cInspectDen.addStep(needsToInspectChewedBox, inspectChewedBox);
		cInspectDen.addStep(needsToInspectTornNewspaper, inspectTornNewspaper);
		cInspectDen.addStep(needsToInspectRoughBedding, inspectRoughBedding);

		followStrayDogToCooksGuild = new NpcStep(this, 16504, new WorldPoint(3146, 3456, 0),
			"Follow the stray dog from the den until a cutscene triggers north of the Cooks' Guild.");
		followStrayDogToCooksGuild.addAlternateNpcs(16505);
		followStrayDogToCooksGuild.setMaxRoamRange(100);

		interactWithStrayDogAtCooksGuild = new NpcStep(this, 16504, new WorldPoint(3146, 3456, 0),
			"Interact with the stray dog to watch the cutscene.");
		interactWithStrayDogAtCooksGuild.addAlternateNpcs(16505);

		talkToStrayDog = new NpcStep(this, 16504, new WorldPoint(3146, 3456, 0),
			"Talk to the stray dog. Dismiss any pet you have out first.");
		talkToStrayDog.addAlternateNpcs(16505);

		talkToPicklenose = new NpcStep(this, 16523, new WorldPoint(3130, 3436, 0), "");

		var talkToStrayDogInDen = new NpcStep(this, NpcID.DOGQ_DOG_STRAY, new WorldPoint(3197, 3415, 0), "Talk to the stray dog in its den, west of the Varrock clothes shop.");
		cTalkToPicklenose = new ConditionalStep(this, talkToStrayDog,
			"Talk to Picklenose south-west of the Cooks' Guild with the stray dog following you.");
		cTalkToPicklenose.addStep(strayDogFollowing, talkToPicklenose);
		var strayDogInDen = new VarbitRequirement(VarbitID.DOGQ_STRAY_FOLLOWER, 1);
		cTalkToPicklenose.addStep(strayDogInDen, talkToStrayDogInDen);


		makeStuffedDog = new DetailedQuestStep(this,
			"Use the grain on the fur to make a stuffed dog.",
			grain.highlighted(), fur.highlighted(), needleOrCostumeNeedle, thread);
		makeStuffedDog.addDialogStep("Yes.");

		talkToStrayDogAtGoblins = new NpcStep(this, NpcID.DOGQ_DOG_STRAY, new WorldPoint(3131, 3437, 0),
			"Talk to the stray dog next to the goblins south-west of the Cooks' Guild.");

		talkToPicklenoseAgain = new NpcStep(this, 16523, new WorldPoint(3130, 3436, 0),
			"Talk to Picklenose again to trade the stuffed dog for the puppy.", stuffedDog);
		talkToPicklenoseAgainAfterHandingHimTheStuffedDog = new NpcStep(this, 16523, new WorldPoint(3130, 3436, 0), "Talk to Picklenose south-west of the cook's guild after handing him the stuffed dog.");
		talkToPicklenoseAgain.addSubSteps(talkToStrayDogAtGoblins, talkToPicklenoseAgainAfterHandingHimTheStuffedDog);

		cTalkToPicklenoseAgain = new ConditionalStep(this, talkToStrayDogAtGoblins);
		cTalkToPicklenoseAgain.addStep(not(stuffedDog), makeStuffedDog);
		cTalkToPicklenoseAgain.addStep(strayDogFollowing, talkToPicklenoseAgain);

		cTalkToPicklenoseAgainAfterHandingHimTheStuffedDog = new ConditionalStep(this, talkToStrayDogAtGoblins);
		cTalkToPicklenoseAgainAfterHandingHimTheStuffedDog.addStep(strayDogFollowing, talkToPicklenoseAgainAfterHandingHimTheStuffedDog);

		pickUpStrayPuppy2 = new NpcStep(this, NpcID.DOGQ_PUPPY_TWO, new WorldPoint(3131, 3435, 0), "Pick up the stray puppy south-west of the cook's guild.");

		talkToStrayDogOutsideCooksGuild = new NpcStep(this, NpcID.DOGQ_DOG_STRAY, new WorldPoint(3132, 3436, 0), "Talk to the stray dog south-west of the cook's guild to see if she can sniff out the last puppy.");

		followStrayDogToWall = new NpcStep(this, 16504, new WorldPoint(3136, 3467, 0),
			"Interact with the stray dog and follow her until a cutscene triggers by the gap in the wall " +
				"north of the Cooks' Guild.");
		followStrayDogToWall.addAlternateNpcs(16505);
		followStrayDogToWall.addDialogStep("Yes.");

		followStrayDogToWallWatchCutscene = followStrayDogToWall.cutscene();

		killOutlaws = new NpcStep(this, 16528, new WorldPoint(3135, 3475, 0), "Kill both outlaws (level 22 and level 23), then watch the cutscene.", true, combatGear, food);
		killOutlaws.addAlternateNpcs(16527);

		talkToDogToFight = new NpcStep(this, NpcID.DOGQ_DOG_STRAY, new WorldPoint(3136, 3467, 0),
			"Talk to the stray dog near the bandits' camp, ready to fight.");
		talkToDogToFight.addDialogStep("Yes.");
		cTalkToDogAndKillOutlaws = new ConditionalStep(this, talkToDogToFight);
		cTalkToDogAndKillOutlaws.addStep(inInstance, killOutlaws);

		cFollowStrayDogToWallWatchCutscene = new ConditionalStep(this, followStrayDogToWall);
		cFollowStrayDogToWallWatchCutscene.addStep(inCutscene, followStrayDogToWallWatchCutscene);

		talkToDogAfterFight = new NpcStep(this, NpcID.DOGQ_DOG_STRAY, new WorldPoint(3136, 3467, 0),
			"Talk to the stray dog near the bandits' camp for a cutscene.");
		talkToDogAfterFight.addDialogStep("Yes.");
		killOutlaws.addSubSteps(talkToDogToFight, talkToDogAfterFight);

		cKillOutlaws = new ConditionalStep(this, talkToDogAfterFight);
		cKillOutlaws.addStep(inCutscene, killOutlaws.cutscene());

		pickupPuppy1FromDen = new NpcStep(this, NpcID.DOGQ_PUPPY_ONE, new WorldPoint(3195, 3415, 0), "Pick up the puppy labrador from the den west of the Varrock apothecary.");
		pickupPuppy2FromDen = new NpcStep(this, NpcID.DOGQ_PUPPY_TWO, new WorldPoint(3196, 3416, 0), "Pick up the puppy pug from the den west of the Varrock apothecary.");
		pickupPuppy3FromDen = new NpcStep(this, NpcID.DOGQ_PUPPY_THREE, new WorldPoint(3196, 3413, 0), "Pick up the puppy spaniel from the den west of the Varrock apothecary.");
		talkToStrayDogAfterCollectingLastPuppy = new NpcStep(this, NpcID.DOGQ_DOG_STRAY, new WorldPoint(3136, 3477, 0), "Talk to the stray dog north-west of the cook's guild.");
		talkToStrayDogInDenAfterCollectingLastPuppy = new NpcStep(this, NpcID.DOGQ_DOG_STRAY, new WorldPoint(3197, 3414, 0), "");
		cGetPuppies = new ConditionalStep(this, talkToStrayDogInDenAfterCollectingLastPuppy, "With the three puppies in your inventory, talk to the stray dog in the den west of the Varrock apothecary.", puppy1, puppy2, puppy3);
		cGetPuppies.addStep(not(puppy1), pickupPuppy1FromDen);
		cGetPuppies.addStep(not(puppy2), pickupPuppy2FromDen);
		cGetPuppies.addStep(not(puppy3), pickupPuppy3FromDen);

		talkToTaliaWithPuppies = new NpcStep(this, NpcID.DOGQ_TALIA, new WorldPoint(3037, 3457, 0), "");
		cTalkToTaliaWithPuppies = new ConditionalStep(this, talkToTaliaWithPuppies, "With the three puppies in your inventory, talk to Talia at the dog shelter south-west of the Edgeville Monastery.", puppy1, puppy2, puppy3);
		cTalkToTaliaWithPuppies.addStep(not(puppy1), pickupPuppy1FromDen);
		cTalkToTaliaWithPuppies.addStep(not(puppy2), pickupPuppy2FromDen);
		cTalkToTaliaWithPuppies.addStep(not(puppy3), pickupPuppy3FromDen);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToTalia);
		steps.put(5, talkToTalia);
		steps.put(10, talkToGertrude);
		steps.put(15, interactWithStrayDog);
		steps.put(20, followStrayDogToDen);
		steps.put(25, cInspectDen);
		steps.put(30, interactWithStrayDogInDen);
		steps.put(35, followStrayDogToCooksGuild);
		steps.put(40, interactWithStrayDogAtCooksGuild);
		steps.put(45, talkToStrayDog);
		steps.put(50, talkToStrayDog);
		steps.put(55, cTalkToPicklenose);
		steps.put(60, makeStuffedDog);
		steps.put(65, cTalkToPicklenoseAgain);
		steps.put(66, cTalkToPicklenoseAgainAfterHandingHimTheStuffedDog);
		steps.put(70, pickUpStrayPuppy2);
		steps.put(75, talkToStrayDogOutsideCooksGuild);
		steps.put(80, followStrayDogToWall);
		steps.put(85, cFollowStrayDogToWallWatchCutscene);
		steps.put(90, cTalkToDogAndKillOutlaws);
		steps.put(95, cKillOutlaws);
		steps.put(100, cKillOutlaws);
		steps.put(105, talkToStrayDogAfterCollectingLastPuppy);
		steps.put(110, cGetPuppies);
		steps.put(115, cTalkToTaliaWithPuppies);

		return steps;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(needleOrCostumeNeedle, thread, fur, grain);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(combatGear, food);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new SkillRequirement(Skill.CRAFTING, 15)
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of("2 outlaws (level 22 and level 23)");
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
			new ExperienceReward(Skill.CRAFTING, 1000)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to puppies, which can be grown into dogs")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			talkToTalia,
			talkToGertrude
		)));

		sections.add(new PanelDetails("Following the stray dog", List.of(
			interactWithStrayDog,
			followStrayDogToDen,
			cInspectDen,
			interactWithStrayDogInDen,
			followStrayDogToCooksGuild,
			interactWithStrayDogAtCooksGuild,
			talkToStrayDog
		)));

		sections.add(new PanelDetails("Bargaining with the goblins", List.of(
			cTalkToPicklenose,
			makeStuffedDog,
			talkToPicklenoseAgain,
			pickUpStrayPuppy2
		), needleOrCostumeNeedle, thread, fur, grain));

		sections.add(new PanelDetails("The last puppy", List.of(
			talkToStrayDogOutsideCooksGuild,
			followStrayDogToWall,
			killOutlaws
		), combatGear, food));

		sections.add(new PanelDetails("Puppy reunion", List.of(
			talkToStrayDogAfterCollectingLastPuppy,
			cGetPuppies,
			cTalkToTaliaWithPuppies
		)));

		return sections;
	}
}
