/*
 * Copyright (c) 2026, nwheise <https://github.com/nwheise>
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
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
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

/**
 * The OSRS wiki was referenced for this guide: https://oldschool.runescape.wiki/w/A_Ruff_Situation
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

	// Steps
	NpcStep talkToTalia;
	NpcStep talkToGertrude;
	NpcStep interactWithStrayDog;
	NpcStep followStrayDogToDen;
	ObjectStep inspectDen;
	NpcStep interactWithStrayDogInDen;
	NpcStep followStrayDogToCooksGuild;
	NpcStep talkToPicklenose;
	DetailedQuestStep makeStuffedDog;
	NpcStep talkToPicklenoseAgain;
	NpcStep followStrayDogToWall;
	NpcStep killOutlaws;

	@Override
	protected void setupRequirements()
	{
		needleOrCostumeNeedle = new ItemRequirement("Needle or costume needle",
			List.of(ItemID.NEEDLE, ItemID.COSTUMENEEDLE)).isNotConsumed();

		// A costume needle comes with its own thread, so it satisfies this requirement on its own.
		thread = new ItemRequirement("Thread", List.of(ItemID.THREAD, ItemID.COSTUMENEEDLE));
		thread.setTooltip("Not needed if you are using a costume needle.");

		// The quest only accepts item 6814, 'Fur'. Note that the constant RuneLite names FUR is bear fur.
		fur = new ItemRequirement("Fur", ItemID.WEREWOLVE_FUR);
		fur.setTooltip("Bear fur and grey wolf fur will not work. Dropped by werewolves in " +
			"Canifis, or bought from Baraek in Varrock Square for 12 coins.");

		grain = new ItemRequirement("Grain", ItemID.GRAIN);

		stuffedDog = new ItemRequirement("Stuffed dog", 34602);
		stuffedDog.canBeObtainedDuringQuest();
		stuffedDog.setTooltip("You can make another from grain and fur with a needle and thread.");

		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", -1, -1);
		food.setDisplayItemId(BankSlotIcons.getFood());
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

		followStrayDogToDen = new NpcStep(this, 16504, new WorldPoint(3195, 3414, 0),
			"Follow the stray dog to the house just west of Thessalia's Fine Clothes.");
		followStrayDogToDen.addAlternateNpcs(16505);

		inspectDen = new ObjectStep(this, 62486, new WorldPoint(3196, 3414, 0),
			"Inspect the torn newspaper, the chewed box and the rough bedding in the dog's den.", true);
		inspectDen.addAlternateObjects(62489, 62483, 62488, 62491, 62485);

		interactWithStrayDogInDen = new NpcStep(this, 16504, new WorldPoint(3196, 3414, 0),
			"Interact with the stray dog again to have her pick up the puppies' scent.");
		interactWithStrayDogInDen.addAlternateNpcs(16505);

		followStrayDogToCooksGuild = new NpcStep(this, 16504, new WorldPoint(3146, 3456, 0),
			"Follow the stray dog until a cutscene triggers just north of the Cooks' Guild.");
		followStrayDogToCooksGuild.addAlternateNpcs(16505);

		talkToPicklenose = new NpcStep(this, 16523, new WorldPoint(3130, 3436, 0),
			"Talk to Picklenose south-west of the Cooks' Guild. The stray dog must be following you, " +
				"so dismiss any pet you have out first.");

		makeStuffedDog = new DetailedQuestStep(this,
			"Use the grain on the fur to make a stuffed dog.",
			grain.highlighted(), fur.highlighted(), needleOrCostumeNeedle, thread);
		makeStuffedDog.addDialogStep("Yes.");

		talkToPicklenoseAgain = new NpcStep(this, 16523, new WorldPoint(3130, 3436, 0),
			"Talk to Picklenose again to trade the stuffed dog for the puppy.", stuffedDog);

		followStrayDogToWall = new NpcStep(this, 16504, new WorldPoint(3136, 3467, 0),
			"Interact with the stray dog and follow her until a cutscene triggers by the gap in the wall " +
				"north of the Cooks' Guild.");
		followStrayDogToWall.addAlternateNpcs(16505);

		killOutlaws = new NpcStep(this, 16528, new WorldPoint(3135, 3475, 0),
			"Kill both outlaws (level 22 and level 23), then watch the cutscene.", true, combatGear, food);
		killOutlaws.addAlternateNpcs(16527);
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
		steps.put(25, inspectDen);
		steps.put(30, interactWithStrayDogInDen);
		steps.put(35, followStrayDogToCooksGuild);
		steps.put(55, talkToPicklenose);
		steps.put(60, makeStuffedDog);
		steps.put(65, talkToPicklenoseAgain);
		steps.put(80, followStrayDogToWall);
		steps.put(90, killOutlaws);

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
			inspectDen,
			interactWithStrayDogInDen,
			followStrayDogToCooksGuild
		)));

		sections.add(new PanelDetails("Bargaining with the goblins", List.of(
			talkToPicklenose,
			makeStuffedDog,
			talkToPicklenoseAgain
		), needleOrCostumeNeedle, thread, fur, grain));

		sections.add(new PanelDetails("The last puppy", List.of(
			followStrayDogToWall,
			killOutlaws
		), combatGear, food));

		return sections;
	}
}
