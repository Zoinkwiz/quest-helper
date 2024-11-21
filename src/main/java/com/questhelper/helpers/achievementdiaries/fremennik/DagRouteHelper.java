/*
 * Copyright (c) 2022, Obasill
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
package com.questhelper.helpers.achievementdiaries.fremennik;

import com.questhelper.collections.ItemCollections;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.PrayerRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.Prayer;
import net.runelite.api.QuestState;

public class DagRouteHelper extends ComplexStateQuestHelper
{
	ItemRequirement combatGear, food, prayerPot, petRock, thrownaxe, stamPot;

	Requirement protectMelee;

	DagRoute dagRoute;

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupSteps();

		return new ConditionalStep(this, dagRoute);
	}

	@Override
	protected void setupRequirements()
	{
		thrownaxe = new ItemRequirement("Rune thrownaxe", ItemID.RUNE_THROWNAXE);
		petRock = new ItemRequirement("Pet rock", ItemID.PET_ROCK);
		petRock.setTooltip("Can be substituted by having a friend");

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		prayerPot = new ItemRequirement("Prayer Potions", ItemCollections.PRAYER_POTIONS, -1);
		stamPot = new ItemRequirement("Stamina Potions", ItemCollections.STAMINA_POTIONS, -1);

		protectMelee = new PrayerRequirement("Protect from Melee", Prayer.PROTECT_FROM_MELEE);
	}

	public void setupSteps()
	{
		dagRoute = new DagRoute(this);
	}


	@Override
	protected List<ItemRequirement> generateItemRequirements()
	{
		return Arrays.asList(petRock, thrownaxe, combatGear);
	}

	@Override
    protected List<ItemRequirement> generateItemRecommended()
	{
		return Arrays.asList(food, prayerPot, stamPot);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Tank many hits in the Waterbirth Island Dungeon");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> reqs = new ArrayList<>();
		reqs.add(new QuestRequirement(QuestHelperQuest.HORROR_FROM_THE_DEEP, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.THE_FREMENNIK_TRIALS, QuestState.IN_PROGRESS));
		return reqs;
	}

	@Override
    protected List<PanelDetails> setupPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		PanelDetails fullRoute = new PanelDetails("Travel to the Kings", dagRoute.getDisplaySteps(),
			combatGear, thrownaxe, petRock);
		allSteps.add(fullRoute);
		return allSteps;
	}
}
