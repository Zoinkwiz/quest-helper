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
package com.questhelper.quests.clientofkourend;

import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.VarbitCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.CLIENT_OF_KOUREND
)
public class ClientOfKourend extends BasicQuestHelper
{
	ItemRequirement feather, enchantedScroll, enchantedQuill, mysteriousOrb;

	ConditionForStep hasEnchantedScroll, hasEnchantedQuill, hasMysteriousOrb, hasFeather, talkedToLeenz, talkedToHorace, talkedToJennifer, talkedToMunty, talkedToRegath;

	QuestStep talkToVeos, useFeatherOnScroll, talkToLeenz, talkToHorace, talkToJennifer, talkToMunty, talkToRegath, returnToVeos, goToAltar, finishQuest;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToVeos);

		ConditionalStep makeEnchantedQuill = new ConditionalStep(this, talkToVeos);
		makeEnchantedQuill.addStep(new Conditions(hasEnchantedQuill, talkedToLeenz, talkedToRegath, talkedToMunty, talkedToJennifer), talkToHorace);
		makeEnchantedQuill.addStep(new Conditions(hasEnchantedQuill, talkedToLeenz, talkedToRegath, talkedToMunty), talkToJennifer);
		makeEnchantedQuill.addStep(new Conditions(hasEnchantedQuill, talkedToLeenz, talkedToRegath), talkToMunty);
		makeEnchantedQuill.addStep(new Conditions(hasEnchantedQuill, talkedToLeenz), talkToRegath);
		makeEnchantedQuill.addStep(new Conditions(hasEnchantedQuill), talkToLeenz);
		makeEnchantedQuill.addStep(hasEnchantedScroll, useFeatherOnScroll);
		steps.put(1, makeEnchantedQuill);

		steps.put(2, returnToVeos);

		ConditionalStep takeOrbToAltar = new ConditionalStep(this, returnToVeos);
		takeOrbToAltar.addStep(hasMysteriousOrb, goToAltar);

		steps.put(3, returnToVeos);

		steps.put(4, takeOrbToAltar);

		steps.put(5, finishQuest);
		steps.put(6, finishQuest);

		return steps;
	}

	public void setupItemRequirements()
	{
		feather = new ItemRequirement("Feather", ItemID.FEATHER);
		feather.setHighlightInInventory(true);
		enchantedScroll = new ItemRequirement("Enchanted scroll", ItemID.ENCHANTED_SCROLL);
		enchantedScroll.setHighlightInInventory(true);
		mysteriousOrb = new ItemRequirement("Mysterious orb", ItemID.MYSTERIOUS_ORB);
		mysteriousOrb.setHighlightInInventory(true);

		enchantedQuill = new ItemRequirement("Enchanted quill", ItemID.ENCHANTED_QUILL);
	}

	public void setupConditions()
	{
		hasEnchantedQuill = new ItemRequirementCondition(enchantedQuill);
		hasEnchantedScroll = new ItemRequirementCondition(enchantedScroll);
		hasFeather = new ItemRequirementCondition(feather);
		hasMysteriousOrb = new ItemRequirementCondition(mysteriousOrb);
		talkedToLeenz = new VarbitCondition(5620, 1);
		talkedToRegath = new VarbitCondition(5621, 1);
		talkedToMunty = new VarbitCondition(5622, 1);
		talkedToJennifer = new VarbitCondition(5623, 1);
		talkedToHorace = new VarbitCondition(5624, 1);
	}

	public void setupSteps()
	{
		talkToVeos = new NpcStep(this, NpcID.VEOS, new WorldPoint(1824, 3690, 0), "Talk to Veos on the Port Piscarilius docks. You can travel to him by talking to Veos in Port Sarim.");
		talkToVeos.addDialogStep("Sounds interesting! How can I help?");
		talkToVeos.addDialogStep("Can you take me to Great Kourend?");
		talkToVeos.addDialogStep("Have you got any quests for me?");
		talkToVeos.addDialogStep("Let's talk about your client...");
		talkToVeos.addDialogStep("I've lost something you've given me.");

		useFeatherOnScroll = new DetailedQuestStep(this, "Use a feather on the Enchanted Scroll", feather, enchantedScroll);

		talkToLeenz = new NpcStep(this, NpcID.LEENZ, new WorldPoint(1807, 3726, 0), "Talk to Leenz in Port Piscarilius general store.", enchantedQuill);
		talkToLeenz.addDialogStep("Can I ask you about Port Piscarilius?");
		talkToLeenz.addDialogStep("Why should I gain favour with Port Piscarilius?");
		talkToHorace = new NpcStep(this, NpcID.HORACE, new WorldPoint(1774, 3589, 0), "Talk to Horace in the Hosidius general store.", enchantedQuill);
		talkToHorace.addDialogStep("Can I ask you about Hosidius?");
		talkToHorace.addDialogStep("Why should I gain favour with Hosidius?");
		talkToJennifer = new NpcStep(this, NpcID.JENNIFER, new WorldPoint(1545, 3636, 0), "Talk to Jennifer in Shayzien general store.", enchantedQuill);
		talkToJennifer.addDialogStep("Can I ask you about Shayzien?");
		talkToJennifer.addDialogStep("Why should I gain favour with Shayzien?");
		talkToMunty = new NpcStep(this, NpcID.MUNTY, new WorldPoint(1552, 3716, 0), "Talk to Munty in Lovakengj general store.", enchantedQuill);
		talkToMunty.addDialogStep("Can I ask you about Lovakengj?");
		talkToMunty.addDialogStep("Why should I gain favour with Lovakengj?");
		talkToRegath = new NpcStep(this, NpcID.REGATH, new WorldPoint(1720, 3724, 0), "Talk to Regath in Arceuus general store.", enchantedQuill);
		talkToRegath.addDialogStep("Can I ask you about Arceuus?");
		talkToRegath.addDialogStep("Why should I gain favour with Arceuus?");

		returnToVeos = new NpcStep(this, NpcID.VEOS, new WorldPoint(1824, 3690, 0), "Return to Veos on Piscarilius docks.");
		returnToVeos.addDialogStep("Let's talk about your client...");
		returnToVeos.addDialogStep("I've lost something you've given me.");
		goToAltar = new DetailedQuestStep(this, new WorldPoint(1712, 3883, 0), "Activate the mysterious orb at the Dark Altar. You can either run there through Arceuus, teleport to Wintertodt with the Games Necklace and run south, or teleport straight there on the Arceuus spellbook.", mysteriousOrb);

		finishQuest = new NpcStep(this, NpcID.VEOS, new WorldPoint(1824, 3690, 0), "Return to Veos on Piscarilius docks.");
		finishQuest.addDialogStep("Let's talk about your client...");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(feather);
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Arrays.asList(talkToVeos, useFeatherOnScroll)), feather));
		allSteps.add(new PanelDetails("Learn about Kourend", new ArrayList<>(Arrays.asList(talkToLeenz, talkToRegath, talkToMunty, talkToJennifer, talkToHorace, returnToVeos))));
		allSteps.add(new PanelDetails("The Dark Altar", new ArrayList<>(Arrays.asList(goToAltar, finishQuest))));
		return allSteps;
	}
}
