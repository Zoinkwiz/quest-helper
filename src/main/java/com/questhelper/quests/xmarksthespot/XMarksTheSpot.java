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
package com.questhelper.quests.xmarksthespot;

import com.questhelper.QuestHelperQuest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.DigStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.QuestDescriptor;

@QuestDescriptor(
	quest = QuestHelperQuest.X_MARKS_THE_SPOT
)
public class XMarksTheSpot extends BasicQuestHelper
{
	ItemRequirement spade;

	QuestStep speakVeosLumbridge, digOutsideBob, digCastle, digDraynor, digMartin, speakVeosSarim, speakVeosSarimWithoutCasket;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, speakVeosLumbridge);
		steps.put(1, steps.get(0));
		steps.put(2, digOutsideBob);
		steps.put(3, digCastle);
		steps.put(4, digDraynor);
		steps.put(5, digMartin);
		steps.put(6, speakVeosSarim);
		steps.put(7, speakVeosSarimWithoutCasket);

		return steps;
	}

	private void setupRequirements() {
		spade = new ItemRequirement("Spade", ItemID.SPADE);
	}

	private void setupSteps() {
		speakVeosLumbridge = new NpcStep(this, NpcID.VEOS_8484, new WorldPoint(3228, 3242, 0),
			"Talk to Veos in The Sheared Ram pub in Lumbridge to start the quest.");
		speakVeosLumbridge.addDialogStep("I'm looking for a quest.");
		speakVeosLumbridge.addDialogStep("Sounds good, what should I do?");

		digOutsideBob = new DigStep(this, new WorldPoint(3230, 3209, 0),
			"Dig north of Bob's Brilliant Axes, on the west side of the plant against the wall of his house.");
		digOutsideBob.addDialogStep("Okay, thanks Veos.");

		digCastle = new DigStep(this, new WorldPoint(3203, 3212, 0),
			"Dig behind Lumbridge Castle, just outside the kitchen door.");

		digDraynor = new DigStep(this, new WorldPoint(3109, 3264, 0),
			"Dig north-west of the Draynor Village jail, just by the wheat farm.");

		digMartin = new DigStep(this, new WorldPoint(3078, 3259, 0),
			"Dig in the pig pen just west where Martin the Master Gardener is.",
			new ItemRequirement("Treasure scroll", ItemID.TREASURE_SCROLL_23070));

		ItemRequirement ancientCasket = new ItemRequirement("Ancient casket", ItemID.ANCIENT_CASKET);
		ancientCasket.setTip("If you've lost this you can get another by digging in the pig pen in Draynor Village.");

		speakVeosSarim = new NpcStep(this, NpcID.VEOS_8484, new WorldPoint(3054, 3245, 0),
			"Talk to Veos directly south of the Rusty Anchor Inn in Port Sarim to finish the quest.",
			ancientCasket);

		speakVeosSarimWithoutCasket = new NpcStep(this, NpcID.VEOS_8484, new WorldPoint(3054, 3245, 0),
			"Talk to Veos directly south of the Rusty Anchor Inn in Port Sarim to finish the quest.");

		speakVeosSarim.addSubSteps(speakVeosSarimWithoutCasket);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(spade);
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Speak to Veos", new ArrayList<>(Arrays.asList(speakVeosLumbridge)), spade));
		allSteps.add(new PanelDetails("Solve the clue scroll", new ArrayList<>(Arrays.asList(digOutsideBob, digCastle, digDraynor, digMartin))));
		allSteps.add(new PanelDetails("Bring the casket to Veos", new ArrayList<>(Arrays.asList(speakVeosSarim))));
		return allSteps;
	}
}
