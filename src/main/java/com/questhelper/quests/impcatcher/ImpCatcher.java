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
package com.questhelper.quests.impcatcher;

import com.questhelper.QuestHelperQuest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.QuestDescriptor;

@QuestDescriptor(
	quest = QuestHelperQuest.IMP_CATCHER
)
public class ImpCatcher extends BasicQuestHelper
{
	//Items Required
	ItemRequirement blackBead, whiteBead, redBead, yellowBead;

	QuestStep doQuest;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();

		setupRequirements();

		doQuest = new NpcStep(this, NpcID.WIZARD_MIZGOG, new WorldPoint(3103, 3163, 2),
			"Talk to Wizard Mizgog on the top floor of the Wizards' Tower with the required items to finish the quest.",
			blackBead, whiteBead, redBead, yellowBead);
		doQuest.addDialogStep("Give me a quest please.");

		steps.put(0, doQuest);

		steps.put(1, doQuest);

		return steps;
	}

	private void setupRequirements() {
		blackBead = new ItemRequirement("Black bead", ItemID.BLACK_BEAD);
		whiteBead = new ItemRequirement("White bead", ItemID.WHITE_BEAD);
		redBead = new ItemRequirement("Red bead", ItemID.RED_BEAD);
		yellowBead = new ItemRequirement("Yellow bead", ItemID.YELLOW_BEAD);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(blackBead);
		reqs.add(whiteBead);
		reqs.add(redBead);
		reqs.add(yellowBead);

		return reqs;
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Bring Mizgog his beads", Collections.singletonList(doQuest),
			blackBead, whiteBead, redBead, yellowBead));
		return allSteps;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.MAGIC, 875));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("An Amulet of Accuracy", ItemID.AMULET_OF_ACCURACY, 1));
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Imps (level 2) if you plan on collecting the beads yourself");
	}
}
