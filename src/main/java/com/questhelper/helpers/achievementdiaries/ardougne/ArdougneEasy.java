/*
 * Copyright (c) 2021, Obasill <https://github.com/Obasill>
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
package com.questhelper.helpers.achievementdiaries.ardougne;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarPlayerID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArdougneEasy extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement rustySword, silk, coins;

	// Quests required
	Requirement runeMysteries, biohazard;

	Requirement notEssMine, notStealCake, notSellSilk, notEastArdyAltar, notFishingTrawler, notEnterCombatCamp,
		notIdentifySword, notWildyLever, notAlecksEmporium, notProbitaPet;

	QuestStep claimReward, essMine, stealCake, sellSilk, eastArdyAltar, fishingTrawler, enterCombatCamp,
		identifySword, wildyLever, alecksEmporium, probitaPet;

	ConditionalStep essMineTask, stealCakeTask, sellSilkTask, eastArdyAltarTask, fishingTrawlerTask, enterCombatCampTask,
		identifySwordTask, wildyLeverTask, alecksEmporiumTask, probitaPetTask;

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupSteps();

		ConditionalStep doEasy = new ConditionalStep(this, claimReward);

		alecksEmporiumTask = new ConditionalStep(this, alecksEmporium);
		doEasy.addStep(notAlecksEmporium, alecksEmporiumTask);

		fishingTrawlerTask = new ConditionalStep(this, fishingTrawler);
		doEasy.addStep(notFishingTrawler, fishingTrawlerTask);

		identifySwordTask = new ConditionalStep(this, identifySword);
		doEasy.addStep(notIdentifySword, identifySwordTask);

		essMineTask = new ConditionalStep(this, essMine);
		doEasy.addStep(notEssMine, essMineTask);

		stealCakeTask = new ConditionalStep(this, stealCake);
		doEasy.addStep(notStealCake, stealCakeTask);

		sellSilkTask = new ConditionalStep(this, sellSilk);
		doEasy.addStep(notSellSilk, sellSilkTask);

		probitaPetTask = new ConditionalStep(this, probitaPet);
		doEasy.addStep(notProbitaPet, probitaPetTask);

		eastArdyAltarTask = new ConditionalStep(this, eastArdyAltar);
		doEasy.addStep(notEastArdyAltar, eastArdyAltarTask);

		wildyLeverTask = new ConditionalStep(this, wildyLever);
		doEasy.addStep(notWildyLever, wildyLeverTask);

		enterCombatCampTask = new ConditionalStep(this, enterCombatCamp);
		doEasy.addStep(notEnterCombatCamp, enterCombatCampTask);

		return doEasy;
	}

	@Override
	protected void setupRequirements()
	{
		notEssMine = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY, false, 0);
		notStealCake = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY, false, 1);
		notSellSilk = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY, false, 2);
		notEastArdyAltar = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY, false, 4);
		notFishingTrawler = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY, false, 5);
		notEnterCombatCamp = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY, false, 6);
		notIdentifySword = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY, false, 7);
		notWildyLever = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY, false, 9);
		notAlecksEmporium = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY, false, 11);
		notProbitaPet = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY, false, 12);

		silk = new ItemRequirement("Silk", ItemID.SILK).showConditioned(notSellSilk);
		rustySword = new ItemRequirement("Rusty sword", ItemID.DIGSITESWORD).showConditioned(notIdentifySword);
		coins = new ItemRequirement("Coins", ItemCollections.COINS).showConditioned(notIdentifySword);

		runeMysteries = new QuestRequirement(QuestHelperQuest.RUNE_MYSTERIES, QuestState.FINISHED);
		biohazard = new QuestRequirement(QuestHelperQuest.BIOHAZARD, QuestState.FINISHED);
	}

	public void setupSteps()
	{
		essMine = new NpcStep(this, NpcID.CROMPERTY_PRE_DIARY, new WorldPoint(2683, 3326, 0),
			"Have Wizard Cromperty teleport you to the Rune essence mine.");
		essMine.addDialogStep("Can you teleport me to the Rune Essence?");

		stealCake = new ObjectStep(this, ObjectID.CAKETHIEFSTALL, new WorldPoint(2668, 3311, 0),
			"Steal a cake from the East Ardougne market stalls.");

		sellSilk = new NpcStep(this, NpcID.SILK_MERCHANT_ARDOUGNE, new WorldPoint(2655, 3300, 0),
			"Sell silk to the Silk trader in East Ardougne for 60 coins each.");// finish dialog
		sellSilk.addDialogSteps("120 coins.", "I'll give it do you for 60.");

		eastArdyAltar = new ObjectStep(this, ObjectID.ALTAR, new WorldPoint(2618, 3309, 0),
			"Use the altar in East Ardougne's church (requires less than full Prayer points).");

		probitaPet = new NpcStep(this, NpcID.PET_INSURANCE_BROKER, new WorldPoint(2621, 3294, 0),
			"Check what pets you have insured with Probita in East Ardougne (right-click her to Check).");

		wildyLever = new ObjectStep(this, ObjectID.WILDINLEVER, new WorldPoint(2561, 3311, 0),
			"Use the Ardougne lever to teleport to the Wilderness (you may pull the lever there to return). This will take you to DEEP Wilderness, bank anything you aren't willing to lose.");

		enterCombatCamp = new ObjectStep(this, ObjectID.LATHASTRAINING_GATER, new WorldPoint(2518, 3356, 0),
			"Enter the Combat Training Camp north of West Ardougne.");

		fishingTrawler = new ObjectStep(this, ObjectID.TRAWLER_GANGPLANK, new WorldPoint(2675, 3170, 0),
			"Go out fishing on the Fishing Trawler.");

		identifySword = new NpcStep(this, NpcID.TINDEL_MARCHANT, new WorldPoint(2676, 3152, 0),
			"Have Tindel Marchant identify a rusty sword for you. Note: there is about a 1% chance this fails and " +
				"you'll need another sword and more coins.", rustySword, coins.quantity(100));
		identifySword.addDialogStep("Ok, I'll give it a go!");

		alecksEmporium = new NpcStep(this, NpcID.HUNTING_SHOP_OWNER_YANILLE, new WorldPoint(2566, 3083, 0),
			"View Aleck's Hunter Emporium in Yanille.");
		alecksEmporium.addDialogStep("Ok, let's see what you've got!");

		claimReward = new NpcStep(this, NpcID.ARDY_TWOPINTS_DIARY, new WorldPoint(2574, 3323, 0),
			"Talk to Two-pints in the Flying Horse Inn at East Ardougne to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(rustySword, silk, coins.quantity(100));
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.THIEVING, 5, true));

		reqs.add(runeMysteries);
		reqs.add(biohazard);

		return reqs;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Ardougne Cloak 1", ItemID.ARDY_CAPE_EASY),
			new ItemReward("2,500 Exp. Lamp (Any skill over 30)", ItemID.THOSF_REWARD_LAMP)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Unlimited teleports to the Ardougne Monastery with the Ardougne cloak 1"),
			new UnlockReward("Double death runes (200) when trading in cats to civilians"),
			new UnlockReward("10% increased chance to successfully steal from stalls in Ardougne"),
			new UnlockReward("Jubster and Frogeel drops will be noted at the Tower of Life")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails aleckSteps = new PanelDetails("Aleck's Hunter Emporium",
			Collections.singletonList(alecksEmporium));
		aleckSteps.setDisplayCondition(notAlecksEmporium);
		aleckSteps.setLockingStep(alecksEmporiumTask);
		allSteps.add(aleckSteps);

		PanelDetails trawlerSteps = new PanelDetails("Fishing Trawler", Collections.singletonList(fishingTrawler));
		trawlerSteps.setDisplayCondition(notFishingTrawler);
		trawlerSteps.setLockingStep(fishingTrawlerTask);
		allSteps.add(trawlerSteps);

		PanelDetails swordSteps = new PanelDetails("Identify Sword", Collections.singletonList(identifySword),
			rustySword, coins.quantity(100));
		swordSteps.setDisplayCondition(notIdentifySword);
		swordSteps.setLockingStep(identifySwordTask);
		allSteps.add(swordSteps);

		PanelDetails essSteps = new PanelDetails("Essence Mine", Collections.singletonList(essMine), runeMysteries);
		essSteps.setDisplayCondition(notEssMine);
		essSteps.setLockingStep(essMineTask);
		allSteps.add(essSteps);

		PanelDetails cakeSteps = new PanelDetails("Steal Cake", Collections.singletonList(stealCake),
			new SkillRequirement(Skill.THIEVING, 5, true));
		cakeSteps.setDisplayCondition(notStealCake);
		cakeSteps.setLockingStep(stealCakeTask);
		allSteps.add(cakeSteps);

		PanelDetails silkSteps = new PanelDetails("Sell Silk", Collections.singletonList(sellSilk), silk);
		silkSteps.setDisplayCondition(notSellSilk);
		silkSteps.setLockingStep(sellSilkTask);
		allSteps.add(silkSteps);

		PanelDetails petSteps = new PanelDetails("Pet Insurance", Collections.singletonList(probitaPet));
		petSteps.setDisplayCondition(notProbitaPet);
		petSteps.setLockingStep(probitaPetTask);
		allSteps.add(petSteps);

		PanelDetails altarSteps = new PanelDetails("Restore Prayer", Collections.singletonList(eastArdyAltar));
		altarSteps.setDisplayCondition(notEastArdyAltar);
		altarSteps.setLockingStep(eastArdyAltarTask);
		allSteps.add(altarSteps);

		PanelDetails leverSteps = new PanelDetails("Wilderness Lever", Collections.singletonList(wildyLever));
		leverSteps.setDisplayCondition(notWildyLever);
		leverSteps.setLockingStep(wildyLeverTask);
		allSteps.add(leverSteps);

		PanelDetails campSteps = new PanelDetails("Combat Camp", Collections.singletonList(enterCombatCamp),
			biohazard);
		campSteps.setDisplayCondition(notEnterCombatCamp);
		campSteps.setLockingStep(enterCombatCampTask);
		allSteps.add(campSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
