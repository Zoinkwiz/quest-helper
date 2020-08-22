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
package com.questhelper.quests.architecturalalliance;

import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.VarbitCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;

@QuestDescriptor(
	quest = QuestHelperQuest.ARCHITECTURAL_ALLIANCE
)
public class ArchitecturalAlliance extends BasicQuestHelper
{
	ConditionForStep talkedToHosaStart, talkedToHosa, talkedToArcis, talkedToLovada, talkedToPiliar, talkedToShayda;

	DetailedQuestStep talkToHosa, talkToHosaAsArchitect, talkToArcis, talkToLovada, talkToPiliar, talkToShayda, talkToHosaToFinish;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep repairStatue = new ConditionalStep(this, talkToHosa);
		repairStatue.addStep(new Conditions(talkedToHosa, talkedToArcis, talkedToPiliar, talkedToLovada, talkedToShayda), talkToHosaToFinish);
		repairStatue.addStep(new Conditions(talkedToHosa, talkedToArcis, talkedToPiliar, talkedToLovada), talkToShayda);
		repairStatue.addStep(new Conditions(talkedToHosa, talkedToArcis, talkedToPiliar), talkToLovada);
		repairStatue.addStep(new Conditions(talkedToHosa, talkedToArcis), talkToPiliar);
		repairStatue.addStep(talkedToHosa, talkToArcis);
		repairStatue.addStep(talkedToHosaStart, talkToHosaAsArchitect);

		steps.put(0, repairStatue);
		steps.put(1, talkToHosaAsArchitect);
		steps.put(2, talkToHosaAsArchitect);
		steps.put(3, talkToHosaAsArchitect);
		steps.put(4, talkToHosaAsArchitect);

		return steps;
	}

	public void setupConditions()
	{
		talkedToArcis = new VarbitCondition(4971, 1);
		talkedToHosa = new VarbitCondition(4972, 1);
		talkedToLovada = new VarbitCondition(4973, 1);
		talkedToPiliar = new VarbitCondition(4974, 1);
		talkedToShayda = new VarbitCondition(4975, 1);
		talkedToHosaStart = new VarbitCondition(4976, 1);
	}

	public void setupSteps()
	{
		talkToHosa = new NpcStep(this, NpcID.HOSA, new WorldPoint(1636, 3670, 0), "Talk to Hosa outside the Kourend Castle. You'll need 100% favour in all the Kourend houses to complete this miniquest.");
		talkToHosa.addDialogStep("Can I help?");
		talkToHosaAsArchitect = new NpcStep(this, NpcID.HOSA, new WorldPoint(1636, 3670, 0), "Talk to Hosa outside the Kourend Castle again.");
		talkToHosa.addSubSteps(talkToHosaAsArchitect);
		talkToHosaToFinish = new NpcStep(this, NpcID.HOSA, new WorldPoint(1636, 3670, 0), "Talk to Hosa outside the Kourend Castle to finish the miniquest.");

		talkToArcis = new NpcStep(this, NpcID.ARCIS, new WorldPoint(1652, 3754, 0), "Talk to Arcis in the house east of Arceuus's bank.");
		talkToPiliar = new NpcStep(this, NpcID.PILIAR, new WorldPoint(1794, 3737, 0), "Talk to Piliar in the house north west of the Piscarilius general store.");
		talkToLovada = new NpcStep(this, NpcID.LOVADA, new WorldPoint(1485, 3834, 0), "Talk to Lovada in their home just south of the Blast Mine in Lovajengj.");
		talkToShayda = new NpcStep(this, NpcID.SHAYDA, new WorldPoint(1495, 3631, 0), "Talk to Shayda north of the Shayzien bank.");
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Repairing the statue", new ArrayList<>(Arrays.asList(talkToHosa, talkToArcis, talkToPiliar, talkToShayda, talkToLovada, talkToHosaToFinish))));

		return allSteps;
	}
}

