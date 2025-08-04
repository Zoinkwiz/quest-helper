/*
 * Copyright (c) 2019, Trevor <https://github.com/Trevor159>
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
package com.questhelper.helpers.quests.xmarksthespot;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.DigStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;

public class XMarksTheSpot extends BasicQuestHelper
{
	// Required items
	ItemRequirement spade;

	// Recommended items
	ItemRequirement glory;

	// Miscellaneous requirements
	ItemRequirement ancientCasket;
	ItemRequirement treasureScroll;

	// Steps
	NpcStep startQuest;
	DigStep digOutsideBob;
	DigStep digCastle;
	DigStep digDraynor;
	DigStep digMartin;
	NpcStep speakVeosSarim;
	NpcStep speakVeosSarimWithoutCasket;

	@Override
	protected void setupRequirements()
	{
		spade = new ItemRequirement("Spade", ItemID.SPADE).isNotConsumed();
		spade.setTooltip("Can be bought from the Lumbridge General Store.");
		glory = new ItemRequirement("Amulet of Glory for faster teleport to Draynor Village.", ItemCollections.AMULET_OF_GLORIES).isNotConsumed();

		ancientCasket = new ItemRequirement("Ancient casket", ItemID.CLUEQUEST_CASKET);
		ancientCasket.setTooltip("If you've lost this you can get another by digging in the pig pen in Draynor Village.");

		treasureScroll = new ItemRequirement("Treasure scroll", ItemID.CLUEQUEST_CLUE4);
	}

	private void setupSteps()
	{
		// TODO: Worth adding PuzzleWrapperStep at all given the Clue Plugin also does this?
		startQuest = new NpcStep(this, NpcID.VEOS_VISIBLE, new WorldPoint(3228, 3242, 0),
			"Talk to Veos in The Sheared Ram pub in Lumbridge to start the quest.");
		startQuest.addDialogStep("I'm looking for a quest.");
		startQuest.addDialogStep("Sounds good, what should I do?");
		startQuest.addDialogSteps("Can I help?", "Yes.");

		digOutsideBob = DigStep.withCustomSpadeRequirement(this, new WorldPoint(3230, 3209, 0),
			"Dig north of Bob's Brilliant Axes, on the west side of the plant against the wall of his house.", spade);
		digOutsideBob.addDialogStep("Okay, thanks Veos.");
		digOutsideBob.setWhenToHighlight(DigStep.WhenToHighlight.OnTile);

		digCastle = DigStep.withCustomSpadeRequirement(this, new WorldPoint(3203, 3212, 0),
			"Dig behind Lumbridge Castle, just outside the kitchen door.", spade);
		digCastle.setWhenToHighlight(DigStep.WhenToHighlight.OnTile);

		digDraynor = DigStep.withCustomSpadeRequirement(this, new WorldPoint(3109, 3264, 0),
			"Dig north-west of the Draynor Village jail, just by the wheat farm.", spade);
		digDraynor.addTeleport(glory);
		digDraynor.setWhenToHighlight(DigStep.WhenToHighlight.OnTile);

		digMartin = DigStep.withCustomSpadeRequirement(this, new WorldPoint(3078, 3259, 0),
			"Dig in the pig pen just west where Martin the Master Gardener is.", spade,
			treasureScroll);
		digMartin.setWhenToHighlight(DigStep.WhenToHighlight.OnTile);

		speakVeosSarim = new NpcStep(this, NpcID.VEOS_VISIBLE, new WorldPoint(3054, 3245, 0),
			"Talk to Veos directly south of the Rusty Anchor Inn in Port Sarim to finish the quest.",
			ancientCasket);
		speakVeosSarim.addAlternateNpcs(NpcID.VEOS_VISIBLE_TRAVEL);

		speakVeosSarimWithoutCasket = new NpcStep(this, NpcID.VEOS_VISIBLE, new WorldPoint(3054, 3245, 0),
			"Talk to Veos directly south of the Rusty Anchor Inn in Port Sarim to finish the quest.");
		speakVeosSarimWithoutCasket.addAlternateNpcs(NpcID.VEOS_VISIBLE_TRAVEL);

		speakVeosSarim.addSubSteps(speakVeosSarimWithoutCasket);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, startQuest);
		steps.put(1, startQuest);
		steps.put(2, digOutsideBob);
		steps.put(3, digCastle);
		steps.put(4, digDraynor);
		steps.put(5, digMartin);
		steps.put(6, speakVeosSarim);
		steps.put(7, speakVeosSarimWithoutCasket);

		return steps;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			spade
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			glory
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("300 Exp. Lamp (Any Skill)", ItemID.THOSF_REWARD_LAMP, 1),
			new ItemReward("Coins", ItemID.COINS, 200),
			new ItemReward("A Beginner Clue Scroll", ItemID.TRAIL_CLUE_BEGINNER, 1)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var steps = new ArrayList<PanelDetails>();

		steps.add(new PanelDetails("Speak to Veos", List.of(
			startQuest
		), List.of(
			spade
		)));

		steps.add(new PanelDetails("Solve the clue scroll", List.of(
			digOutsideBob,
			digCastle,
			digDraynor,
			digMartin
		), List.of(
			spade
		)));

		steps.add(new PanelDetails("Bring the casket to Veos", List.of(
			speakVeosSarim
		)));

		return steps;
	}
}
