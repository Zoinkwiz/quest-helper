/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.theribbitingtaleofalilypadlabourdispute;

import com.questhelper.collections.ItemCollections;
import com.questhelper.helpers.quests.deserttreasureii.ChestCodeStep;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.PuzzleWrapperStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.coords.WorldPoint;

public class TheRibbitingTaleOfALilyPadLabourDispute extends BasicQuestHelper
{
	//Items Required
	ItemRequirement axe;

	Requirement inChestInterface, cuthbertNearby;

	QuestStep talkToMarcellus, talkToBlueFrogs, talkToMarcellus2, talkToGary, talkToYellowFrogs,
		chopOrangeTree, sabotageLilyPad, talkToGary2, talkToMarcellus3, talkToGaryToBlame,
		enterCode, openChest, plantPlushy, inspectDung, defeatCuthbert, talkToMarcellusEnd,
		talkToGaryEnd, pickUpAxe;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();

		initializeRequirements();
		setupConditions();
		setupSteps();

		steps.put(0, talkToMarcellus);
		steps.put(2, talkToBlueFrogs);
		steps.put(4, talkToMarcellus2);
		steps.put(6, talkToGary);
		steps.put(8, talkToYellowFrogs);
		ConditionalStep goChopOrangeTree = new ConditionalStep(this, pickUpAxe);
		goChopOrangeTree.addStep(axe, chopOrangeTree);
		steps.put(10, goChopOrangeTree);
		steps.put(12, sabotageLilyPad);
		steps.put(14, talkToGary2);
		steps.put(16, talkToGary2); // After cutscene
		steps.put(18, talkToMarcellus3);
		steps.put(20, talkToGaryToBlame);

		ConditionalStep goOpenChest = new ConditionalStep(this, openChest);
		goOpenChest.addStep(inChestInterface, enterCode);
		steps.put(22, goOpenChest);
		steps.put(24, plantPlushy);

		ConditionalStep goDefeatCuthbert = new ConditionalStep(this, inspectDung);
		goDefeatCuthbert.addStep(cuthbertNearby, defeatCuthbert);
		steps.put(26, goDefeatCuthbert);
		steps.put(28, talkToMarcellusEnd);
		steps.put(30, talkToGaryEnd);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		axe = new ItemRequirement("Any axe", ItemCollections.AXES);
		axe.canBeObtainedDuringQuest();
	}

	private void setupConditions()
	{
		inChestInterface = new WidgetTextRequirement(809, 5, 9, "Confirm");
		cuthbertNearby = new NpcRequirement(NpcID.CUTHBERT_LORD_OF_DREAD);
		// 12401 0->1 for Cuthbert about
	}

	private void setupSteps()
	{
		int[] SUE_AND_GARY = new int[]{NpcID.SUE, NpcID.GARY};
		int[] DAVE_AND_JANE = new int[]{NpcID.JANE, NpcID.DAVE_12943};
		talkToMarcellus = new NpcStep(this, NpcID.MARCELLUS, new WorldPoint(1683, 2973, 0),
			"Talk to Marcellus near the Locus Oasis south of Civitas illa Fortis. AJP Fairy ring is fastest.");
		talkToMarcellus.addDialogStep("Yes.");
		talkToBlueFrogs = new NpcStep(this, NpcID.FROG_12938, new WorldPoint(1694, 2996, 0),
			"Talk to the blue frogs in the north-east of the oasis.");
		talkToMarcellus2 = new NpcStep(this, NpcID.MARCELLUS, new WorldPoint(1683, 2973, 0),
			"Return to Marcellus.");
		talkToGary = new NpcStep(this, SUE_AND_GARY, new WorldPoint(1694, 2996, 0),
			"Return to the blue frogs Sue and Gary in the north-east of the oasis.", true);
		talkToYellowFrogs = new NpcStep(this, DAVE_AND_JANE, new WorldPoint(1696, 2982, 0),
			"Talk to the yellow frogs Jane and Dave to the south of Gary.", true);
		((NpcStep) talkToYellowFrogs).addAlternateNpcs();
		pickUpAxe = new ObjectStep(this, ObjectID.LOGS, new WorldPoint(1683, 2975, 0),
			"Take the axe from the logs next to Marcellus.");
		chopOrangeTree = new ObjectStep(this, NullObjectID.NULL_52977, new WorldPoint(1695, 2980, 0),
			"Chop down the orange tree next to the yellow frog.", axe);
		chopOrangeTree.addSubSteps(pickUpAxe);
		sabotageLilyPad = new ObjectStep(this, NullObjectID.NULL_50891, new WorldPoint(1692, 2983, 0),
			"Sabotage the lily pad near to the yellow frogs.");
		talkToGary2 = new NpcStep(this, SUE_AND_GARY, new WorldPoint(1694, 2996, 0),
			"Return to Sue and Gary in the north-east of the oasis.", true);
		talkToMarcellus3 = new NpcStep(this, NpcID.MARCELLUS, new WorldPoint(1683, 2973, 0),
			"Return to Marcellus.");
		talkToGaryToBlame = new NpcStep(this, SUE_AND_GARY, new WorldPoint(1694, 2996, 0),
			"Go talk to Sue and Gary to discuss framing the flies.", true);
		ObjectStep chestStep = new ObjectStep(this, ObjectID.CHEST_50895, new WorldPoint(1676, 2974, 0),
			"Search the chest in the building next to Marcellus. The code is 'NALIA'.");
		chestStep.addAlternateObjects(ObjectID.CHEST_50896);
		openChest = new PuzzleWrapperStep(this, chestStep, "Work out how to open the chest in Marcellus' house.");
		enterCode = new PuzzleWrapperStep(this, new ChestCodeStep(this, "NALIA", 10,
			3, 3, 4, 4, 9), "Work out how to open the chest in Marcellus' house.");
		openChest.addSubSteps(enterCode);
		plantPlushy = new ObjectStep(this, NullObjectID.NULL_52979, new WorldPoint(1694, 2976, 0),
			"Plant the plushy in the capybara dung east of Marcellus, south of the orange tree.");
		inspectDung = new ObjectStep(this, NullObjectID.NULL_52979, new WorldPoint(1694, 2976, 0),
			"Inspect the dung east of Marcellus, south of the orange tree.");
		defeatCuthbert = new NpcStep(this, NpcID.CUTHBERT_LORD_OF_DREAD, new WorldPoint(1688, 2977, 0),
			"Defeat Cuthbert, Lord of Dread.");
		defeatCuthbert.addSubSteps(inspectDung);
		talkToMarcellusEnd = new NpcStep(this, NpcID.MARCELLUS, new WorldPoint(1683, 2973, 0),
			"Tell Marcellus about the plushy.");
		talkToGaryEnd = new NpcStep(this, SUE_AND_GARY, new WorldPoint(1694, 2996, 0),
			"Talk to Sue and Gary to resolve the dispute!", true);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(axe);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new SkillRequirement(Skill.WOODCUTTING, 15),
			new QuestRequirement(QuestHelperQuest.CHILDREN_OF_THE_SUN, QuestState.FINISHED)
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of("Cuthbert, Lord of Dread (level 1)");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(
			new ExperienceReward(Skill.WOODCUTTING, 2000)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to a new Hardwood Farming Patch"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Toad-aly mad", List.of(
			talkToMarcellus, talkToBlueFrogs, talkToMarcellus2, talkToGary), axe));
		allSteps.add(new PanelDetails("Rig-it", List.of(
			talkToYellowFrogs, chopOrangeTree, sabotageLilyPad, talkToGary2
		)));
		allSteps.add(new PanelDetails("Hopping to conclusions", List.of(
			talkToMarcellus3, talkToGaryToBlame,
			openChest, plantPlushy, defeatCuthbert, talkToMarcellusEnd, talkToGaryEnd
		)));

		return allSteps;
	}
}
