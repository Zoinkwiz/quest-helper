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
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
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

import static com.questhelper.requirements.util.LogicHelper.or;

public class ARuffSituation extends BasicQuestHelper
{
	private static final int TALIA = 16486;
	private static final int PICKLENOSE = 16523;
	private static final int STRAY_DOG = 16504;
	private static final int STRAY_DOG_FOLLOWING = 16505;
	private static final int OUTLAW_LEVEL_22 = 16528;
	private static final int OUTLAW_LEVEL_23 = 16527;
	private static final int STUFFED_DOG = 34602;
	/*
	 * The den objects are multilocs: the scene holds the parent id, which transforms into the
	 * Inspect-able child once the quest varbit reaches 25. Match on both, so the step does not
	 * depend on impostor resolution.
	 */
	private static final int ROUGH_BEDDING = 62483;
	private static final int ROUGH_BEDDING_INSPECTABLE = 62485;
	private static final int TORN_NEWSPAPER = 62486;
	private static final int TORN_NEWSPAPER_INSPECTABLE = 62488;
	private static final int CHEWED_BOX = 62489;
	private static final int CHEWED_BOX_INSPECTABLE = 62491;

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
	Requirement outlawNearby;

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
		fur.setTooltip("The item called 'Fur'. Bear fur and grey wolf fur will not work. Dropped by werewolves in " +
			"Canifis, or bought from Baraek in Varrock Square for 12 coins.");

		grain = new ItemRequirement("Grain", ItemID.GRAIN);

		stuffedDog = new ItemRequirement("Stuffed dog", STUFFED_DOG);
		stuffedDog.canBeObtainedDuringQuest();
		stuffedDog.setTooltip("You can make another from grain and fur with a needle and thread.");

		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", -1, -1);
		food.setDisplayItemId(BankSlotIcons.getFood());

		outlawNearby = or(new NpcCondition(OUTLAW_LEVEL_22), new NpcCondition(OUTLAW_LEVEL_23));
	}

	public void setupSteps()
	{
		talkToTalia = new NpcStep(this, TALIA, new WorldPoint(3041, 3462, 0),
			"Talk to Talia at the dog shelter south-west of the Edgeville Monastery.");
		talkToTalia.addDialogStep("Yes.");

		talkToGertrude = new NpcStep(this, NpcID.GERTRUDE_POST, new WorldPoint(3151, 3413, 0),
			"Talk to Gertrude in her house west of Varrock, just south of the Cooks' Guild.");
		talkToGertrude.addAlternateNpcs(NpcID.GERTRUDE_QUEST);
		talkToGertrude.addDialogStep("Ask about lost dogs.");

		interactWithStrayDog = new NpcStep(this, STRAY_DOG, new WorldPoint(3181, 3428, 0),
			"Interact with the stray dog barking at a guard, just south of the western Varrock bank.");

		followStrayDogToDen = new NpcStep(this, STRAY_DOG, new WorldPoint(3195, 3414, 0),
			"Follow the stray dog to the house just west of Thessalia's Fine Clothes.");
		followStrayDogToDen.addAlternateNpcs(STRAY_DOG_FOLLOWING);

		inspectDen = new ObjectStep(this, TORN_NEWSPAPER, new WorldPoint(3195, 3414, 0),
			"Inspect the torn newspaper, the chewed box and the rough bedding in the dog's den.", true);
		inspectDen.addAlternateObjects(CHEWED_BOX, ROUGH_BEDDING,
			TORN_NEWSPAPER_INSPECTABLE, CHEWED_BOX_INSPECTABLE, ROUGH_BEDDING_INSPECTABLE);

		interactWithStrayDogInDen = new NpcStep(this, STRAY_DOG, new WorldPoint(3195, 3414, 0),
			"Interact with the stray dog again to have her pick up the puppies' scent.");
		interactWithStrayDogInDen.addAlternateNpcs(STRAY_DOG_FOLLOWING);

		followStrayDogToCooksGuild = new NpcStep(this, STRAY_DOG, new WorldPoint(3144, 3452, 0),
			"Follow the stray dog until a cutscene triggers just north of the Cooks' Guild.");
		followStrayDogToCooksGuild.addAlternateNpcs(STRAY_DOG_FOLLOWING);

		talkToPicklenose = new NpcStep(this, PICKLENOSE, new WorldPoint(3130, 3436, 0),
			"Talk to Picklenose south-west of the Cooks' Guild. The stray dog must be following you, " +
				"so dismiss any pet you have out first.");

		makeStuffedDog = new DetailedQuestStep(this,
			"Use the grain on the fur to make a stuffed dog.",
			grain.highlighted(), fur.highlighted(), needleOrCostumeNeedle, thread);
		makeStuffedDog.addDialogStep("Yes.");

		talkToPicklenoseAgain = new NpcStep(this, PICKLENOSE, new WorldPoint(3130, 3436, 0),
			"Talk to Picklenose again to trade the stuffed dog for the puppy.", stuffedDog);

		followStrayDogToWall = new NpcStep(this, STRAY_DOG, new WorldPoint(3145, 3462, 0),
			"Interact with the stray dog and follow her until a cutscene triggers by the gap in the wall " +
				"north of the Cooks' Guild.");
		followStrayDogToWall.addAlternateNpcs(STRAY_DOG_FOLLOWING);

		killOutlaws = new NpcStep(this, OUTLAW_LEVEL_22, new WorldPoint(3145, 3462, 0),
			"Kill both outlaws (level 22 and level 23), then watch the cutscene.", true, combatGear, food);
		killOutlaws.addAlternateNpcs(OUTLAW_LEVEL_23);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		/*
		 * The quest varbit counts up to 120 in small increments, and only some of the values line up
		 * with a change of instruction. Each step is mapped across the whole range it is believed to
		 * cover so that the sidebar never falls blank on an unmapped value.
		 */
		steps.put(0, talkToTalia);
		putRange(steps, 1, 14, talkToGertrude);
		putRange(steps, 15, 19, interactWithStrayDog);
		putRange(steps, 20, 24, followStrayDogToDen);
		steps.put(25, inspectDen);
		putRange(steps, 26, 30, interactWithStrayDogInDen);
		putRange(steps, 31, 44, followStrayDogToCooksGuild);

		// The goblins spawn on 45 for the cutscene, but the first conversation only ends on 60.
		putRange(steps, 45, 59, talkToPicklenose);

		var cTradeStuffedDog = new ConditionalStep(this, makeStuffedDog);
		cTradeStuffedDog.addStep(stuffedDog, talkToPicklenoseAgain);
		putRange(steps, 60, 65, cTradeStuffedDog);

		var cFindLastPuppy = new ConditionalStep(this, followStrayDogToWall);
		cFindLastPuppy.addStep(outlawNearby, killOutlaws);
		// The quest completes on 120, so there is no step to show for it.
		putRange(steps, 66, 119, cFindLastPuppy);

		return steps;
	}

	private static void putRange(Map<Integer, QuestStep> steps, int fromInclusive, int toInclusive, QuestStep step)
	{
		for (int i = fromInclusive; i <= toInclusive; i++)
		{
			steps.put(i, step);
		}
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
			new UnlockReward("Access to puppies, which can be grown into dogs"),
			new UnlockReward("Puppies can be adopted from Chase at the dog shelter for 200 coins")
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
