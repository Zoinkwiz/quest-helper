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
package com.questhelper.helpers.quests.clientofkourend;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.QuestState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.VarbitID;

public class ClientOfKourend extends BasicQuestHelper
{
	// Required items
	ItemRequirement feather;

	// Recommended items
	ItemRequirement gamesNecklace;
	ItemRequirement coinsForMinecart;

	// Miscellaneus requirements
	ItemRequirement enchantedScroll;
	ItemRequirement enchantedQuill;
	ItemRequirement mysteriousOrb;
	VarbitRequirement talkedToLeenz;
	VarbitRequirement talkedToHorace;
	VarbitRequirement talkedToJennifer;
	VarbitRequirement talkedToMunty;
	VarbitRequirement talkedToRegath;

	// Steps
	QuestStep talkToVeos;
	QuestStep useFeatherOnScroll;
	QuestStep talkToLeenz;
	QuestStep talkToHorace;
	QuestStep talkToJennifer;
	QuestStep talkToMunty;
	QuestStep talkToRegath;
	QuestStep returnToVeos;
	QuestStep goToAltar;
	QuestStep finishQuest;


	@Override
	protected void setupRequirements()
	{
		talkedToLeenz = new VarbitRequirement(VarbitID.VEOS_PISCARILIUS, 1);
		talkedToRegath = new VarbitRequirement(VarbitID.VEOS_ARCEUUS, 1);
		talkedToMunty = new VarbitRequirement(VarbitID.VEOS_LOVAKENGJ, 1);
		talkedToJennifer = new VarbitRequirement(VarbitID.VEOS_SHAYZIEN, 1);
		talkedToHorace = new VarbitRequirement(VarbitID.VEOS_HOSIDIUS, 1);

		feather = new ItemRequirement("Feather", ItemID.FEATHER);
		feather.setTooltip("Can be purchased from Gerrant's Fishy Business in Port Sarim.");
		feather.addAlternates(ItemID.HUNTING_POLAR_FEATHER, ItemID.HUNTING_WOODLAND_FEATHER, ItemID.HUNTING_JUNGLE_FEATHER, ItemID.HUNTING_DESERT_FEATHER,
			ItemID.HUNTING_EAGLE_FEATHER, ItemID.HUNTING_STRIPY_BIRD_FEATHER);
		feather.setHighlightInInventory(true);
		gamesNecklace = new ItemRequirement("Games necklace", ItemCollections.GAMES_NECKLACES);
		coinsForMinecart = new ItemRequirement("Coins", ItemCollections.COINS, 60);
		coinsForMinecart.setTooltip("For travel with the Lovakengj Minecart Network.");
		enchantedScroll = new ItemRequirement("Enchanted scroll", ItemID.VEOS_SCROLL);
		mysteriousOrb = new ItemRequirement("Mysterious orb", ItemID.VEOS_ORB);
		mysteriousOrb.setHighlightInInventory(true);

		enchantedQuill = new ItemRequirement("Enchanted quill", ItemID.VEOS_QUILL);
	}

	public void setupSteps()
	{
		talkToVeos = new NpcStep(this, NpcID.VEOS_VIS_AMULET, new WorldPoint(1824, 3690, 0),
			"Talk to Veos on the Port Piscarilius docks. You can travel to him by talking to Veos in Port Sarim.");
		talkToVeos.addDialogStep("Sounds interesting! How can I help?");
		talkToVeos.addDialogStep("Can you take me to Great Kourend?");
		talkToVeos.addDialogStep("Have you got any quests for me?");
		talkToVeos.addDialogStep("Let's talk about your client...");
		talkToVeos.addDialogStep("I've lost something you've given me.");
		talkToVeos.addDialogStep("Yes.");

		useFeatherOnScroll = new DetailedQuestStep(this, "Use a feather on the Enchanted Scroll.", feather, enchantedScroll.highlighted());

		talkToLeenz = new NpcStep(this, NpcID.PISCARILIUS_GENERALSTORE_KEEPER, new WorldPoint(1807, 3726, 0), "Talk to Leenz in Port Piscarilius general store.", enchantedScroll, enchantedQuill);
		talkToLeenz.addDialogStep("Can I ask you about Port Piscarilius?");
		talkToLeenz.addDialogStep("What is there to do in Port Piscarilius?");
		talkToHorace = new NpcStep(this, NpcID.HOSIDIUS_GENERALSTORE, new WorldPoint(1774, 3589, 0), "Talk to Horace in the Hosidius general store. You can take the Lovakengj Minecart Network to Hosidius South.", enchantedScroll, enchantedQuill);
		talkToHorace.addDialogStep("Can I ask you about Hosidius?");
		talkToHorace.addDialogStep("What is there to do in Hosidius?");
		talkToJennifer = new NpcStep(this, NpcID.SHAYZIEN_GENERALSTORE, new WorldPoint(1518, 3586, 0), "Talk to Jennifer in Shayzien general store. You can run west towards the bank and use the Lovakengj Minecart Network to Shayzien East.", enchantedScroll, enchantedQuill);
		talkToJennifer.addDialogStep("Can I ask you about Shayzien?");
		talkToJennifer.addDialogStep("What is there to do in Shayzien?");
		talkToMunty = new NpcStep(this, NpcID.LOVAKENGJ_GENERALSTORE, new WorldPoint(1551, 3752, 0), "Talk to Munty in Lovakengj general store. You can run south towards Kingstown and use the Lovakengj Minecart Network to Lovakengj.", enchantedScroll, enchantedQuill);
		talkToMunty.addDialogStep("Can I ask you about Lovakengj?");
		talkToMunty.addDialogStep("What is there to do in Lovakengj?");
		talkToRegath = new NpcStep(this, NpcID.ARCEUUS_GENERALSTORE, new WorldPoint(1720, 3724, 0), "Talk to Regath in Arceuus general store.", enchantedScroll, enchantedQuill);
		talkToRegath.addDialogStep("Can I ask you about Arceuus?");
		talkToRegath.addDialogStep("What is there to do in Arceuus?");

		returnToVeos = new NpcStep(this, NpcID.VEOS_VIS_AMULET, new WorldPoint(1824, 3690, 0), "Return to Veos on Piscarilius docks.");
		returnToVeos.addDialogStep("Let's talk about your client...");
		returnToVeos.addDialogStep("I've lost something you've given me.");
		goToAltar = new DetailedQuestStep(this, new WorldPoint(1712, 3883, 0), "Activate the mysterious orb at the Dark Altar. You can either run there through Arceuus, teleport to Wintertodt with the Games Necklace and run south, or teleport straight there on the Arceuus spellbook.", mysteriousOrb);

		finishQuest = new NpcStep(this, NpcID.VEOS_VIS_AMULET, new WorldPoint(1824, 3690, 0), "Return to Veos on Piscarilius docks.");
		finishQuest.addDialogStep("Let's talk about your client...");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToVeos);

		var makeEnchantedQuill = new ConditionalStep(this, talkToVeos);
		makeEnchantedQuill.addStep(and(enchantedScroll, enchantedQuill, talkedToLeenz, talkedToRegath, talkedToMunty, talkedToJennifer), talkToHorace);
		makeEnchantedQuill.addStep(and(enchantedScroll, enchantedQuill, talkedToLeenz, talkedToRegath, talkedToMunty), talkToJennifer);
		makeEnchantedQuill.addStep(and(enchantedScroll, enchantedQuill, talkedToLeenz, talkedToRegath), talkToMunty);
		makeEnchantedQuill.addStep(and(enchantedScroll, enchantedQuill, talkedToLeenz), talkToRegath);
		makeEnchantedQuill.addStep(and(enchantedScroll, enchantedQuill), talkToLeenz);
		makeEnchantedQuill.addStep(enchantedScroll, useFeatherOnScroll);
		steps.put(1, makeEnchantedQuill);

		steps.put(2, returnToVeos);

		var takeOrbToAltar = new ConditionalStep(this, returnToVeos);
		takeOrbToAltar.addStep(mysteriousOrb, goToAltar);

		steps.put(3, returnToVeos);

		steps.put(4, takeOrbToAltar);

		steps.put(5, finishQuest);
		steps.put(6, finishQuest);

		return steps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.X_MARKS_THE_SPOT, QuestState.FINISHED)
		);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			feather
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			gamesNecklace,
			coinsForMinecart
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
			new ItemReward("500 Experience Lamps (Any Skill)", ItemID.THOSF_REWARD_LAMP, 2), //4447 Placeholder until confirmed.
			new ItemReward("Kharedst's Memoirs", ItemID.VEOS_KHAREDSTS_MEMOIRS, 1)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			talkToVeos,
			useFeatherOnScroll
		), List.of(
			feather
		)));

		sections.add(new PanelDetails("Learn about Kourend", List.of(
			talkToLeenz,
			talkToRegath,
			talkToMunty,
			talkToJennifer,
			talkToHorace,
			returnToVeos
		)));

		sections.add(new PanelDetails("The Dark Altar", List.of(
			goToAltar,
			finishQuest
		)));

		return sections;
	}
}
