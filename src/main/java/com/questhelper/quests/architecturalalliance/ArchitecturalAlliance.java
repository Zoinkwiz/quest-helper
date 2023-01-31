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

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.Favour;
import com.questhelper.requirements.player.FavourRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;

import java.util.*;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.ARCHITECTURAL_ALLIANCE
)
public class ArchitecturalAlliance extends BasicQuestHelper
{
	Requirement talkedToHosaStart, talkedToHosa, talkedToArcis, talkedToLovada, talkedToPiliar, talkedToShayda;

	// Recommended Items
	ItemRequirement kharedstsMemoirs;

	DetailedQuestStep talkToHosa, talkToHosaAsArchitect, talkToArcis, talkToLovada, talkToPiliar, talkToShayda, talkToHosaToFinish;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
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

	@Override
	public void setupRequirements()
	{
		kharedstsMemoirs = new ItemRequirement("Kharedst's Memoirs", ItemID.KHAREDSTS_MEMOIRS).isNotConsumed();
		kharedstsMemoirs.setTooltip("Make sure to have memories available. Xeric's Talisman + Fairy Rings can be used instead.");

	}

	public void setupConditions()
	{
		talkedToArcis = new VarbitRequirement(4971, 1);
		talkedToHosa = new VarbitRequirement(4972, 1);
		talkedToLovada = new VarbitRequirement(4973, 1);
		talkedToPiliar = new VarbitRequirement(4974, 1);
		talkedToShayda = new VarbitRequirement(4975, 1);
		talkedToHosaStart = new VarbitRequirement(4976, 1);
	}

	public void setupSteps()
	{
		talkToHosa = new NpcStep(this, NpcID.HOSA, new WorldPoint(1636, 3670, 0),
			"Talk to Hosa outside the Kourend Castle.");
		talkToHosa.addDialogStep("Can I help?");
		talkToHosaAsArchitect = new NpcStep(this, NpcID.HOSA, new WorldPoint(1636, 3670, 0),
			"Talk to Hosa outside the Kourend Castle again.");
		talkToHosa.addSubSteps(talkToHosaAsArchitect);
		talkToHosaToFinish = new NpcStep(this, NpcID.HOSA, new WorldPoint(1636, 3670, 0),
			"Talk to Hosa outside the Kourend Castle to finish the miniquest.");

		talkToArcis = new NpcStep(this, NpcID.ARCIS, new WorldPoint(1652, 3754, 0),
			"Talk to Arcis in the house east of Arceuus's bank.");
		talkToPiliar = new NpcStep(this, NpcID.PILIAR, new WorldPoint(1794, 3737, 0),
			"Talk to Piliar in the house north west of the Piscarilius general store.");
		talkToLovada = new NpcStep(this, NpcID.LOVADA, new WorldPoint(1485, 3834, 0),
			"Talk to Lovada in their home just south of the Blast Mine in Lovakengj.");
		talkToShayda = new NpcStep(this, NpcID.SHAYDA, new WorldPoint(1532, 3544, 0),
			"Talk to Shayda in their home just south of the Shayzien Administration building");
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Repairing the statue",
			Arrays.asList(talkToHosa, talkToArcis, talkToPiliar, talkToLovada, talkToShayda, talkToHosaToFinish)));

		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new FavourRequirement(Favour.LOVAKENGJ, 100));
		req.add(new FavourRequirement(Favour.ARCEUUS, 100));
		req.add(new FavourRequirement(Favour.HOSIDIUS, 100));
		req.add(new FavourRequirement(Favour.PISCARILIUS, 100));
		req.add(new FavourRequirement(Favour.SHAYZIEN, 100));

		return req;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("10,000 Experience Lamp (Any skill over level 40).", ItemID.ANTIQUE_LAMP_21262, 1)); //21262 May not be the correct ID, I can't find a proper way to confirm until I can get it ingame again.
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Xeric's Heart Teleport on Xeric's Talisman."));
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> req = new ArrayList<>();
		req.add(kharedstsMemoirs);

		return req;
	}
}
