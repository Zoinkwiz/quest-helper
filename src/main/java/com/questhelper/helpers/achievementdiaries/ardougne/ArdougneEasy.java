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
import java.util.ArrayList;
import java.util.List;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarPlayerID;

public class ArdougneEasy extends ComplexStateQuestHelper
{
	// Required items
	ItemRequirement rustySword;
	ItemRequirement silk;
	ItemRequirement coins;

	// Required quests
	QuestRequirement runeMysteries;
	QuestRequirement biohazard;

	// Required skills
	SkillRequirement thieving;

	// Tasks
	VarplayerRequirement notAlecksEmporium;
	NpcStep alecksEmporium;
	ConditionalStep alecksEmporiumTask;

	VarplayerRequirement notIdentifySword;
	NpcStep identifySword;
	ConditionalStep identifySwordTask;

	VarplayerRequirement notFishingTrawler;
	QuestStep fishingTrawler;
	ConditionalStep fishingTrawlerTask;

	VarplayerRequirement notProbitaPet;
	NpcStep probitaPet;
	ConditionalStep probitaPetTask;

	VarplayerRequirement notEastArdyAltar;
	ObjectStep eastArdyAltar;
	ConditionalStep eastArdyAltarTask;

	VarplayerRequirement notSellSilk;
	NpcStep sellSilk;
	ConditionalStep sellSilkTask;

	VarplayerRequirement notStealCake;
	ObjectStep stealCake;
	ConditionalStep stealCakeTask;

	VarplayerRequirement notEssMine;
	NpcStep essMine;
	ConditionalStep essMineTask;

	VarplayerRequirement notWildyLever;
	ObjectStep wildyLever;
	ConditionalStep wildyLeverTask;

	VarplayerRequirement notEnterCombatCamp;
	ObjectStep enterCombatCamp;
	ConditionalStep enterCombatCampTask;

	NpcStep claimReward;

	@Override
	protected void setupRequirements()
	{
		notAlecksEmporium = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY, false, 11);

		notIdentifySword = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY, false, 7);

		notFishingTrawler = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY, false, 5);

		notProbitaPet = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY, false, 12);

		notEastArdyAltar = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY, false, 4);

		notSellSilk = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY, false, 2);

		notStealCake = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY, false, 1);

		notEssMine = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY, false, 0);

		notEnterCombatCamp = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY, false, 6);

		notWildyLever = new VarplayerRequirement(VarPlayerID.ARDOUNGE_ACHIEVEMENT_DIARY, false, 9);

		rustySword = new ItemRequirement("Rusty sword", ItemID.DIGSITESWORD).showConditioned(notIdentifySword);
		silk = new ItemRequirement("Silk", ItemID.SILK).showConditioned(notSellSilk);
		coins = new ItemRequirement("Coins", ItemCollections.COINS, 100).showConditioned(notIdentifySword);

		runeMysteries = new QuestRequirement(QuestHelperQuest.RUNE_MYSTERIES, QuestState.FINISHED);
		biohazard = new QuestRequirement(QuestHelperQuest.BIOHAZARD, QuestState.FINISHED);
		thieving = new SkillRequirement(Skill.THIEVING, 5, true);
	}

	public void setupSteps()
	{
		alecksEmporium = new NpcStep(this, NpcID.HUNTING_SHOP_OWNER_YANILLE, new WorldPoint(2566, 3083, 0), "View Aleck's Hunter Emporium in Yanille.");
		alecksEmporium.addDialogStep("Ok, let's see what you've got!");
		alecksEmporiumTask = new ConditionalStep(this, alecksEmporium);

		identifySword = new NpcStep(this, NpcID.TINDEL_MARCHANT, new WorldPoint(2676, 3152, 0), "Have Tindel Marchant identify a rusty sword for you. Note: there is about a 1% chance this fails and you'll need another sword and more coins.", rustySword, coins);
		identifySword.addDialogStep("Ok, I'll give it a go!");
		identifySwordTask = new ConditionalStep(this, identifySword);

		fishingTrawler = new ObjectStep(this, ObjectID.TRAWLER_GANGPLANK, new WorldPoint(2675, 3170, 0), "Go out fishing on the Fishing Trawler.");
		fishingTrawlerTask = new ConditionalStep(this, fishingTrawler);

		probitaPet = new NpcStep(this, NpcID.PET_INSURANCE_BROKER, new WorldPoint(2621, 3294, 0), "Check what pets you have insured with Probita in East Ardougne (right-click her to Check).");
		probitaPet.addDialogStep("What pets have I insured?");
		probitaPet.addDialogStepWithExclusion("More options...", "What pets have I insured?");
		probitaPetTask = new ConditionalStep(this, probitaPet);

		eastArdyAltar = new ObjectStep(this, ObjectID.ALTAR, new WorldPoint(2618, 3309, 0), "Use the altar in East Ardougne's church (requires less than full Prayer points).");
		eastArdyAltarTask = new ConditionalStep(this, eastArdyAltar);

		sellSilk = new NpcStep(this, NpcID.SILK_MERCHANT_ARDOUGNE, new WorldPoint(2655, 3300, 0), "Sell silk to the Silk trader in East Ardougne for 60 coins each.");
		sellSilk.addDialogSteps("120 coins.", "I'll give it to you for 60.");
		sellSilkTask = new ConditionalStep(this, sellSilk);

		stealCake = new ObjectStep(this, ObjectID.CAKETHIEFSTALL, new WorldPoint(2668, 3311, 0), "Steal a cake from the East Ardougne market stalls.");
		stealCakeTask = new ConditionalStep(this, stealCake);

		essMine = new NpcStep(this, NpcID.CROMPERTY_PRE_DIARY, new WorldPoint(2683, 3326, 0), "Have Wizard Cromperty teleport you to the Rune essence mine.");
		essMine.addDialogStep("Can you teleport me to the Rune Essence Mine?");
		essMineTask = new ConditionalStep(this, essMine);

		wildyLever = new ObjectStep(this, ObjectID.WILDINLEVER, new WorldPoint(2561, 3311, 0), "Use the Ardougne lever to teleport to the Wilderness (you may pull the lever there to return). This will take you to DEEP Wilderness, bank anything you aren't willing to lose.");
		// We don't add a dialog highlight on purpose here to make sure people don't mindlessly click blue and lose items
		wildyLeverTask = new ConditionalStep(this, wildyLever);

		enterCombatCamp = new ObjectStep(this, ObjectID.LATHASTRAINING_GATER, new WorldPoint(2518, 3356, 0), "Enter the Combat Training Camp north of West Ardougne.");
		enterCombatCampTask = new ConditionalStep(this, enterCombatCamp);

		claimReward = new NpcStep(this, NpcID.ARDY_TWOPINTS_DIARY, new WorldPoint(2574, 3323, 0), "Talk to Two-pints in the Flying Horse Inn at East Ardougne to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupSteps();

		var diary = new ConditionalStep(this, claimReward);

		diary.addStep(notAlecksEmporium, alecksEmporiumTask);

		diary.addStep(notIdentifySword, identifySwordTask);

		diary.addStep(notFishingTrawler, fishingTrawlerTask);

		diary.addStep(notProbitaPet, probitaPetTask);

		diary.addStep(notEastArdyAltar, eastArdyAltarTask);

		diary.addStep(notSellSilk, sellSilkTask);

		diary.addStep(notStealCake, stealCakeTask);

		diary.addStep(notEssMine, essMineTask);

		diary.addStep(notWildyLever, wildyLeverTask);

		diary.addStep(notEnterCombatCamp, enterCombatCampTask);

		return diary;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			rustySword,
			silk,
			coins
		);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			thieving,
			runeMysteries,
			biohazard
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Ardougne Cloak 1", ItemID.ARDY_CAPE_EASY),
			new ItemReward("2,500 Exp. Lamp (Any skill over 30)", ItemID.THOSF_REWARD_LAMP)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Unlimited teleports to the Ardougne Monastery with the Ardougne cloak 1"),
			new UnlockReward("Double death runes (200) when trading in cats to civilians"),
			new UnlockReward("10% increased chance to successfully steal from stalls in Ardougne"),
			new UnlockReward("Jubster and Frogeel drops will be noted at the Tower of Life")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(PanelDetails.diary(
			"Aleck's Hunter Emporium",
			notAlecksEmporium,
			alecksEmporiumTask,
			List.of(
				alecksEmporium
			)
		));

		sections.add(PanelDetails.diary(
			"Identify Sword",
			notIdentifySword,
			identifySword,
			List.of(
				identifySword
			),
			rustySword,
			coins
		));

		sections.add(PanelDetails.diary(
			"Fishing Trawler",
			notFishingTrawler,
			fishingTrawlerTask,
			List.of(
				fishingTrawler
			)
		));

		sections.add(PanelDetails.diary(
			"Pet Insurance",
			notProbitaPet,
			probitaPetTask,
			List.of(
				probitaPet
			)
		));

		sections.add(PanelDetails.diary(
			"Restore Prayer",
			notEastArdyAltar,
			eastArdyAltarTask,
			List.of(
				eastArdyAltar
			)
		));

		sections.add(PanelDetails.diary(
			"Sell Silk",
			notSellSilk,
			sellSilkTask,
			List.of(
				sellSilk
			),
			silk
		));

		sections.add(PanelDetails.diary(
			"Steal Cake",
			notStealCake,
			stealCakeTask,
			List.of(
				stealCake
			),
			thieving
		));

		sections.add(PanelDetails.diary(
			"Essence Mine",
			notEssMine,
			essMineTask,
			List.of(
				essMine
			),
			runeMysteries
		));

		sections.add(PanelDetails.diary(
			"Wilderness Lever",
			notWildyLever,
			wildyLeverTask,
			List.of(
				wildyLever
			)
		));

		sections.add(PanelDetails.diary(
			"Combat Camp",
			notEnterCombatCamp,
			enterCombatCampTask,
			List.of(
				enterCombatCamp
			)
		));

		sections.add(new PanelDetails("Finishing off", List.of(
			claimReward
		)));

		return sections;
	}
}
