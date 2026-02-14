/*
 * Copyright (c) 2020, Patyfatycake <https://github.com/Patyfatycake/>
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
 * ON ANY THEORY OF LIABI`LITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.helpers.quests.junglepotion;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

public class JunglePotion extends BasicQuestHelper
{
	// Recommended items
	ItemRequirement food;
	ItemRequirement antipoison;
	ItemRequirement karaTele;

	// Mid-quest item requirements
	ItemRequirement grimySnakeWeed;
	ItemRequirement snakeWeed;
	ItemRequirement grimyArdrigal;
	ItemRequirement ardrigal;
	ItemRequirement grimySitoFoil;
	ItemRequirement sitoFoil;
	ItemRequirement grimyVolenciaMoss;
	ItemRequirement volenciaMoss;
	ItemRequirement roguesPurse;
	ItemRequirement grimyRoguesPurse;

	// Zones
	Zone undergroundZone;

	// Miscellaneous requirements
	ZoneRequirement isUnderground;

	// Steps
	NpcStep startQuest;
	ObjectStep getSnakeWeed;
	ConditionalStep cleanAndReturnSnakeWeed;
	ObjectStep getArdrigal;
	ConditionalStep cleanAndReturnArdrigal;
	ObjectStep getSitoFoil;
	ConditionalStep cleanAndReturnSitoFoil;
	ObjectStep getVolenciaMoss;
	ConditionalStep cleanAndReturnVolenciaMoss;
	ObjectStep enterCave;
	ObjectStep getRoguePurseHerb;
	ConditionalStep getRoguesPurse;
	ConditionalStep cleanAndReturnRoguesPurse;
	NpcStep finishQuest;

	@Override
	protected void setupZones()
	{
		undergroundZone = new Zone(new WorldPoint(2824, 9462, 0), new WorldPoint(2883, 9533, 0));
	}

	@Override
	protected void setupRequirements()
	{
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		antipoison = new ItemRequirement("Antipoison", ItemCollections.ANTIPOISONS);
		karaTele = new ItemRequirement("Teleport to Karamja (Glory/house teleport)", ItemID.NZONE_TELETAB_BRIMHAVEN);
		karaTele.addAlternates(ItemCollections.AMULET_OF_GLORIES);

		grimySnakeWeed = new ItemRequirement("Grimy Snake Weed", ItemID.UNIDENTIFIED_SNAKE_WEED);
		grimySnakeWeed.setHighlightInInventory(true);
		snakeWeed = new ItemRequirement("Snake Weed", ItemID.SNAKE_WEED);

		grimyArdrigal = new ItemRequirement("Grimy Ardrigal", ItemID.UNIDENTIFIED_ARDRIGAL);
		grimyArdrigal.setHighlightInInventory(true);
		ardrigal = new ItemRequirement("Ardrigal", ItemID.ARDRIGAL);

		grimySitoFoil = new ItemRequirement("Grimy Sito Foil", ItemID.UNIDENTIFIED_SITO_FOIL);
		grimySitoFoil.setHighlightInInventory(true);
		sitoFoil = new ItemRequirement("Sito Foil", ItemID.SITO_FOIL);

		grimyVolenciaMoss = new ItemRequirement("Grimy Volencia Moss", ItemID.UNIDENTIFIED_VOLENCIA_MOSS);
		grimyVolenciaMoss.setHighlightInInventory(true);
		volenciaMoss = new ItemRequirement("Volencia Moss", ItemID.VOLENCIA_MOSS);

		grimyRoguesPurse = new ItemRequirement("Grimy Rogues Purse", ItemID.UNIDENTIFIED_ROGUES_PURSE);
		grimyRoguesPurse.setHighlightInInventory(true);
		roguesPurse = new ItemRequirement("Rogues Purse", ItemID.ROGUES_PURSE);

		isUnderground = new ZoneRequirement(undergroundZone);
	}

	private ConditionalStep getReturnHerbStep(String herbName, ItemRequirement grimyHerb, ItemRequirement cleanHerb)
	{
		NpcStep returnHerb = talkToTrufitus("", cleanHerb);
		returnHerb.addDialogSteps("Of course!");
		var cleanGrimyHerb = new DetailedQuestStep(this, "", grimyHerb);

		var cleanAndReturnHerb = new ConditionalStep(this, cleanGrimyHerb, "Clean and return the " + herbName + " to Trufitus.");
		cleanAndReturnHerb.addStep(cleanHerb, returnHerb);
		return cleanAndReturnHerb;
	}

	private NpcStep talkToTrufitus(String text, Requirement... requirements)
	{
		return new NpcStep(this, NpcID.TRUFITUS, new WorldPoint(2809, 3085, 0), text, requirements);
	}

	private void setupSteps()
	{
		startQuest = talkToTrufitus("Talk to Trufitus in Tai Bwo Wannai on Karamja.");
		startQuest.addDialogSteps("It's a nice village, where is everyone?");
		startQuest.addDialogSteps("Me? How can I help?");
		startQuest.addDialogSteps("It sounds like just the challenge for me.");

		getSnakeWeed = new ObjectStep(this, ObjectID.SNAKE_VINE_FULL, new WorldPoint(2763, 3044, 0), "Search a marshy jungle vine south of Tai Bwo Wannai for some snake weed.");
		getSnakeWeed.addText("If you want to do Zogre Flesh Eaters or Legends' Quest grab one for each as you will need them later.");

		cleanAndReturnSnakeWeed = getReturnHerbStep("Snake Weed", grimySnakeWeed, snakeWeed);

		getArdrigal = new ObjectStep(this, ObjectID.ARDRIGAL_PALM_FULL, new WorldPoint(2871, 3116, 0), "Search the palm trees north east of Tai Bwo Wannai for an Ardrigal herb.");
		getArdrigal.addText("If you want to do Legends' Quest grab one extra as you will need it later.");

		cleanAndReturnArdrigal = getReturnHerbStep("Ardrigal", grimyArdrigal, ardrigal);

		getSitoFoil = new ObjectStep(this, ObjectID.SITO_SOIL_FULL, new WorldPoint(2791, 3047, 0), "Search the scorched earth in the south of Tai Bwo Wannai for a Sito Foil herb.");

		cleanAndReturnSitoFoil = getReturnHerbStep("Sito Foil", grimySitoFoil, sitoFoil);

		getVolenciaMoss = new ObjectStep(this, ObjectID.VOLENCIA_MOSS_ROCK_FULL, new WorldPoint(2851, 3036, 0), "Search the rock for a Volencia Moss herb at the mine south east of Tai Bwo Wannai.");
		getVolenciaMoss.addText("If you plan on doing Fairy Tale I then take an extra.");

		cleanAndReturnVolenciaMoss = getReturnHerbStep("Volencia Moss", grimyVolenciaMoss, volenciaMoss);

		enterCave = new ObjectStep(this, ObjectID.POTHOLE_CAVE_ENTRANCE, new WorldPoint(2825, 3119, 0),
			"Enter the cave to the north by clicking on the rocks.");
		enterCave.addDialogStep("Yes, I'll enter the cave.");
		getRoguePurseHerb = new ObjectStep(this, ObjectID.ROGUES_PURSE_CAVE_FULL, "Get the Rogues Purse from the fungus covered " +
			"wall in the underground dungeon.");
		getRoguePurseHerb.setHideWorldArrow(true);
		getRoguePurseHerb.addText("If you are planning on doing Zogre Flesh Eaters then take an extra.");

		getRoguesPurse = new ConditionalStep(this, enterCave);
		getRoguesPurse.addStep(isUnderground, getRoguePurseHerb);

		getRoguesPurse.addSubSteps(enterCave);

		cleanAndReturnRoguesPurse = getReturnHerbStep("Rogues Purse", grimyRoguesPurse, roguesPurse);
		cleanAndReturnRoguesPurse.addStep(isUnderground, new ObjectStep(this, ObjectID.JP_CAVEROCKSOUT, new WorldPoint(2830, 9522, 0), "Climb out of the cave."));

		finishQuest = talkToTrufitus("Talk to Trufitus to finish the quest.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, startQuest);
		steps.put(1, getSnakeWeed);
		steps.put(2, cleanAndReturnSnakeWeed);
		steps.put(3, getArdrigal);
		steps.put(4, cleanAndReturnArdrigal);
		steps.put(5, getSitoFoil);
		steps.put(6, cleanAndReturnSitoFoil);
		steps.put(7, getVolenciaMoss);
		steps.put(8, cleanAndReturnVolenciaMoss);
		steps.put(9, getRoguesPurse);
		steps.put(10, cleanAndReturnRoguesPurse);
		steps.put(11, finishQuest);

		return steps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.DRUIDIC_RITUAL, QuestState.FINISHED),
			new SkillRequirement(Skill.HERBLORE, 3, false)
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			food,
			antipoison,
			karaTele
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Survive against level 53 Jogres and level 46 Harpie Bug Swarms."
		);
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
			new ExperienceReward(Skill.HERBLORE, 775)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting quest", List.of(
			startQuest
		)));

		sections.add(new PanelDetails("Snake Weed", List.of(
			getSnakeWeed,
			cleanAndReturnSnakeWeed
		)));

		sections.add(new PanelDetails("Ardrigal", List.of(
			getArdrigal,
			cleanAndReturnArdrigal
		)));

		sections.add(new PanelDetails("Sito Foil", List.of(
			getSitoFoil,
			cleanAndReturnSitoFoil
		)));

		sections.add(new PanelDetails("Volencia Moss", List.of(
			getVolenciaMoss,
			cleanAndReturnVolenciaMoss
		)));

		sections.add(new PanelDetails("Rogues Purse", List.of(
			enterCave,
			getRoguePurseHerb,
			cleanAndReturnRoguesPurse
		)));

		return sections;
	}
}
