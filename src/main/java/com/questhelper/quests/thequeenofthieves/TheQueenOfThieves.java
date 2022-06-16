/*
 * Copyright (c) 2020, andmcadams
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
package com.questhelper.quests.thequeenofthieves;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.player.Favour;
import com.questhelper.requirements.player.FavourRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_QUEEN_OF_THIEVES
)
public class TheQueenOfThieves extends BasicQuestHelper
{
	//Items Required
	ItemRequirement stew, hughesLetter;

	QuestStep talkToLawry, talkToPoorLookingPerson, talkToOReilly, talkToDevan, exitWarrens, killConrad,
	tellDevanAboutConrad, exitWarrens2, goToKingstown, openChest, leaveKingstown, talkToLawry2,
	talkToShauna;

	ObjectStep enterWarrens, enterWarrens2, enterWarrens3, enterWarrens4;

	NpcStep talkToQueenOfThieves;

	//Zones
	ZoneRequirement inWarrens, inUpstairsHughesHouse;
	Zone warrens, kingstown, upstairsHughesHouse;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep searchHughesChest = new ConditionalStep(this, goToKingstown);
		searchHughesChest.addStep(inWarrens, exitWarrens);
		searchHughesChest.addStep(inUpstairsHughesHouse, openChest);

		steps.put(0, talkToLawry);
		steps.put(1, talkToPoorLookingPerson);
		steps.put(2, talkToOReilly);
		// Switches to 3 after mention of stew, but before giving it to him.
		steps.put(3, talkToOReilly);

		ConditionalStep findDevan = new ConditionalStep(this, enterWarrens);
		findDevan.addStep(inWarrens, talkToDevan);

		steps.put(4, findDevan);
		// Switches to 5 when you enter the manhole.
		steps.put(5, findDevan);

		ConditionalStep killConradConditional = new ConditionalStep(this, killConrad);
		killConradConditional.addStep(inWarrens, exitWarrens);

		steps.put(6, killConradConditional);

		ConditionalStep tellDevanAboutConradConditional = new ConditionalStep(this, enterWarrens2);
		tellDevanAboutConradConditional.addStep(inWarrens, tellDevanAboutConrad);
		steps.put(7, tellDevanAboutConradConditional);

		ConditionalStep talkToQueenOfThievesConditional = new ConditionalStep(this, enterWarrens3);
		talkToQueenOfThievesConditional.addStep(inWarrens, talkToQueenOfThieves);
		steps.put(8, talkToQueenOfThievesConditional);
		steps.put(9, talkToQueenOfThievesConditional);

		ConditionalStep exposeHughes = new ConditionalStep(this, goToKingstown);
		exposeHughes.addStep(inWarrens, exitWarrens2);
		exposeHughes.addStep(inUpstairsHughesHouse, openChest);

		steps.put(10, exposeHughes);

		ConditionalStep talkToLawry2Conditional = new ConditionalStep(this, talkToLawry2);
		talkToLawry2Conditional.addStep(inUpstairsHughesHouse, leaveKingstown);
		steps.put(11, talkToLawry2Conditional);

		ConditionalStep talkToShaunaConditional = new ConditionalStep(this, enterWarrens4);
		talkToShaunaConditional.addStep(inWarrens, talkToShauna);

		steps.put(12, talkToShaunaConditional);

		return steps;
	}

	@Override
	public void setupRequirements() {
		stew = new ItemRequirement("Stew", ItemID.STEW);
		hughesLetter = new ItemRequirement("Letter", ItemID.LETTER_21774);
		hughesLetter.setTooltip("You can get another letter by searching the chest upstairs in Hughes' house in Kingstown.");
	}

	public void loadZones() {
		warrens = new Zone(new WorldPoint(1728, 10115, 0), new WorldPoint(1814, 10177, 0));
		kingstown = new Zone(new WorldPoint(1668,  3675, 1), new WorldPoint(1685, 3684, 1));
		upstairsHughesHouse = new Zone(new WorldPoint(1668,  3675, 1), new WorldPoint(1685, 3684, 1));
	}

	public void setupConditions()
	{
		inWarrens = new ZoneRequirement(warrens);
		inUpstairsHughesHouse = new ZoneRequirement(upstairsHughesHouse);
	}

	public void setupSteps() {
		WorldPoint tomasPoint = new WorldPoint(1796, 3781, 0);
		WorldPoint manholePoint = new WorldPoint(1813, 3745, 0);
		WorldPoint ladderPoint = new WorldPoint(1813, 10145, 0);
		WorldPoint devanPoint = new WorldPoint(1767, 10146, 0);
		WorldPoint queenOfThievesPoint = new WorldPoint(1764, 10158, 0);

		talkToLawry = new NpcStep(this, NpcID.TOMAS_LAWRY, tomasPoint, "Speak to Tomas Lawry in Port Piscarilius.");
		talkToLawry.addDialogStep("I'm looking for a quest.");
		talkToLawry.addDialogStep("What are you investigating?");
		talkToLawry.addDialogStep("Yes.");

		talkToPoorLookingPerson = new NpcStep(this, NpcID.POOR_LOOKING_WOMAN_7923, new WorldPoint(1803, 3738, 0), "Speak to the poor looking woman.");
		talkToOReilly = new NpcStep(this, NpcID.ROBERT_OREILLY, new WorldPoint(1794, 3757, 0), "Speak to Robert O'Reilly, and give him the bowl of stew.", stew);
		talkToOReilly.addDialogStep("Okay.");

		enterWarrens = new ObjectStep(this, ObjectID.MANHOLE_31706, manholePoint, "Enter the Warrens.");
		enterWarrens.addAlternateObjects(ObjectID.MANHOLE_31707);

		talkToDevan = new NpcStep(this, NpcID.DEVAN_RUTTER, devanPoint, "Speak to Devan Rutter.");
		talkToDevan.addDialogStep("Nope, sounds good to me.");

		exitWarrens = new ObjectStep(this, ObjectID.LADDER_31708, ladderPoint, "Exit the Warrens.");

		// Could potentially add a dialog step for Brutally or Softly, but seems unnecessary since either works.
		killConrad = new NpcStep(this, NpcID.CONRAD_KING, new WorldPoint(1847, 3734, 0), "Murder Conrad King.");

		// Enter the Warrens again.
		enterWarrens2 = new ObjectStep(this, ObjectID.MANHOLE_31706, manholePoint, "Enter the Warrens.");
		enterWarrens2.addAlternateObjects(ObjectID.MANHOLE_31707);

		tellDevanAboutConrad = new NpcStep(this, NpcID.DEVAN_RUTTER, devanPoint, "Tell Devan Rutter about the murder.");

		enterWarrens3 = new ObjectStep(this, ObjectID.MANHOLE_31706, manholePoint, "Enter the Warrens.");
		enterWarrens3.addAlternateObjects(ObjectID.MANHOLE_31707);

		talkToQueenOfThieves = new NpcStep(this, NpcID.THE_QUEEN_OF_THIEVES, queenOfThievesPoint, "Talk to the Queen of Thieves.");
		talkToQueenOfThieves.addAlternateNpcs(NpcID.LADY_SHAUNA_PISCARILIUS);

		// Exit the Warrens again.
		exitWarrens2 = new ObjectStep(this, ObjectID.LADDER_31708, ladderPoint, "Exit the Warrens.");

		goToKingstown = new ObjectStep(this, ObjectID.STAIRCASE_11796, new WorldPoint(1672, 3681, 0), "Go up the stairs in Councillor Hughes' home in Kingstown.");
		openChest = new ObjectStep(this, NullObjectID.NULL_10084, new WorldPoint(1681, 3677, 1), "Pick the locked chest.");

		leaveKingstown = new ObjectStep(this, ObjectID.STAIRCASE_11799, new WorldPoint(1672, 3682, 1), "Go downstairs.");

		// Talk to Lawry again
		talkToLawry2 = new NpcStep(this, NpcID.TOMAS_LAWRY, tomasPoint, "Speak to Tomas Lawry in Port Piscarilius.", hughesLetter);
		talkToLawry2.addDialogStep("Let's talk about my quest.");

		// Enter the Warrens again
		enterWarrens4 = new ObjectStep(this, ObjectID.MANHOLE_31706, manholePoint, "Enter the Warrens.");
		enterWarrens4.addAlternateObjects(ObjectID.MANHOLE_31707);

		talkToShauna = new NpcStep(this, NpcID.LADY_SHAUNA_PISCARILIUS, queenOfThievesPoint, "Talk to Lady Shauna Piscarilius.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Collections.singletonList(stew);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return Arrays.asList(
			new SkillRequirement(Skill.THIEVING, 20),
			new FavourRequirement(Favour.PISCARILIUS, 20),
			new QuestRequirement(QuestHelperQuest.CLIENT_OF_KOUREND, QuestState.FINISHED),
			new QuestRequirement(QuestHelperQuest.X_MARKS_THE_SPOT, QuestState.FINISHED)
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
		return Collections.singletonList(new ExperienceReward(Skill.THIEVING, 1000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("2,000 Coins", ItemID.COINS_995, 2000),
				new ItemReward("A Piscarilius Favour Certificate", ItemID.PISCARILIUS_FAVOUR_CERTIFICATE, 1),
				new ItemReward("A page for Kharedst's Memoirs", ItemID.KHAREDSTS_MEMOIRS, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to The Warrens."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Investigation", Arrays.asList(talkToLawry, talkToPoorLookingPerson, talkToOReilly), stew));
		allSteps.add(new PanelDetails("Gaining Trust", Arrays.asList(enterWarrens, talkToDevan, exitWarrens, killConrad, enterWarrens2, tellDevanAboutConrad)));
		allSteps.add(new PanelDetails("Exposing Hughes", Arrays.asList(enterWarrens3, talkToQueenOfThieves, exitWarrens2, goToKingstown, openChest, leaveKingstown, talkToLawry2, enterWarrens4, talkToShauna)));
		return allSteps;
	}
}
