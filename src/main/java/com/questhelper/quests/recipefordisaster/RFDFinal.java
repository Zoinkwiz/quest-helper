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
package com.questhelper.quests.recipefordisaster;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarbits;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestPointRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;

import java.util.*;

import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.RECIPE_FOR_DISASTER_FINALE
)
public class RFDFinal extends BasicQuestHelper
{
	ItemRequirement iceGloves, restorePotions, combatGear;

	Requirement inFightArena, killedAgrith, killedFlambeed, killedKaramel, killedDessourt, killedMother;

	QuestStep enterPortal, killAgrith, enterPortalFlambeed, killFlambeed, enterPortalKaramel, killKaramel, enterPortalDessourt, killDessourt,
		enterPortalMother, killMother, enterPortalCulinaromancer, killCulinaromancer;

	//Zones
	Zone fightArena;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep defeatAll = new ConditionalStep(this, enterPortal);
		defeatAll.addStep(new Conditions(killedMother, inFightArena), killCulinaromancer);
		defeatAll.addStep(new Conditions(killedMother), enterPortalCulinaromancer);

		defeatAll.addStep(new Conditions(killedDessourt, inFightArena), killMother);
		defeatAll.addStep(new Conditions(killedDessourt), enterPortalMother);

		defeatAll.addStep(new Conditions(killedKaramel, inFightArena), killDessourt);
		defeatAll.addStep(new Conditions(killedKaramel), enterPortalDessourt);

		defeatAll.addStep(new Conditions(killedFlambeed, inFightArena), killKaramel);
		defeatAll.addStep(new Conditions(killedFlambeed), enterPortalKaramel);

		defeatAll.addStep(new Conditions(killedAgrith, inFightArena), killFlambeed);
		defeatAll.addStep(new Conditions(killedAgrith), enterPortalFlambeed);

		defeatAll.addStep(inFightArena, killAgrith);
		steps.put(4, defeatAll);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		iceGloves = new ItemRequirement("Ice gloves or smiths gloves(i)", ItemID.ICE_GLOVES);
		iceGloves.addAlternates(ItemID.SMITHS_GLOVES_I);
		restorePotions = new ItemRequirement("Restore potions for Karamel", ItemCollections.RESTORE_POTIONS);
		combatGear = new ItemRequirement("Combat gear, food and potions", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

	}

	public void loadZones()
	{
		fightArena = new Zone(new WorldPoint(1889, 5345, 2), new WorldPoint(1910, 5366, 2));
		killedAgrith = new VarbitRequirement(1855, 1);
		killedFlambeed = new VarbitRequirement(1855, 2);
		killedKaramel = new VarbitRequirement(1855, 3);
		killedDessourt = new VarbitRequirement(1855, 4);
		killedMother = new VarbitRequirement(1855, 5);
	}

	public void setupConditions()
	{
		inFightArena = new ZoneRequirement(fightArena);
	}

	public void setupSteps()
	{
		enterPortal = new ObjectStep(this, NullObjectID.NULL_12354, new WorldPoint(3209, 3218, 0), "Enter the portal in Lumbridge Castle, ready to fight. You can leave between fights to re-gear.", combatGear);
		killAgrith = new NpcStep(this, NpcID.AGRITHNANA, new WorldPoint(1900, 5355, 2),"Kill Agrith-Na-Na. He uses magic at ranged, and melee up close.");

		enterPortalFlambeed = new ObjectStep(this, NullObjectID.NULL_12354, new WorldPoint(3209, 3218, 0), "Enter the portal in Lumbridge Castle, ready to fight kill Flambeed. Water spells are especially effective.", combatGear, iceGloves);
		killFlambeed = new NpcStep(this, NpcID.FLAMBEED, new WorldPoint(1900, 5355, 2),"Equip ice gloves and kill Flambeed. Water spells are especially effective.", iceGloves);
		killFlambeed.addSubSteps(enterPortalFlambeed);

		enterPortalKaramel = new ObjectStep(this, NullObjectID.NULL_12354, new WorldPoint(3209, 3218, 0), "Enter the portal to fight Karamel. Stand in melee distance, and bring restore potions to as they drain your stats. Fire spells are especially effective.", combatGear, restorePotions);
		killKaramel = new NpcStep(this, NpcID.KARAMEL, new WorldPoint(1900, 5355, 2), "Kill Karamel. Stand in melee distance, and bring restore potions to as they drain your stats. Fire spells are especially effective.", combatGear, restorePotions);
		killKaramel.addSubSteps(enterPortalKaramel);

		enterPortalDessourt = new ObjectStep(this, NullObjectID.NULL_12354, new WorldPoint(3209, 3218, 0), "Enter the portal in Lumbridge Castle, ready to fight Dessourt.", combatGear);
		killDessourt = new NpcStep(this, NpcID.DESSOURT, new WorldPoint(1900, 5355, 2), "Kill Dessourt.");
		killDessourt.addSubSteps(enterPortalDessourt);

		enterPortalMother = new ObjectStep(this, NullObjectID.NULL_12354, new WorldPoint(3209, 3218, 0), "Enter the portal in Lumbridge Castle, ready to fight the Gelatinnoth mother. You'll need to use air spells when she's white, earth when brown, fire when red and water when blue.", combatGear);
		killMother = new NpcStep(this, NpcID.GELATINNOTH_MOTHER, new WorldPoint(1900, 5355, 2), "Kill the Gelatinnoth mother. You'll need to use air spells when she's white, earth when brown, fire when red and water when blue, melee when orange and ranged when green.");
		((NpcStep)(killMother)).addAlternateNpcs(NpcID.GELATINNOTH_MOTHER_4885, NpcID.GELATINNOTH_MOTHER_4886, NpcID.GELATINNOTH_MOTHER_4887, NpcID.GELATINNOTH_MOTHER_4888, NpcID.GELATINNOTH_MOTHER_4889);
		killMother.addSubSteps(enterPortalMother);

		enterPortalCulinaromancer = new ObjectStep(this, NullObjectID.NULL_12354, new WorldPoint(3209, 3218, 0), "Enter the portal in Lumbridge Castle, ready to fight the Culinaromancer. Try to keep your distance.", combatGear);
		killCulinaromancer = new NpcStep(this, NpcID.CULINAROMANCER_4878,  new WorldPoint(1900, 5355, 2), "Kill the Culinaromancer. Try to keep your distance.");
		killCulinaromancer.addSubSteps(enterPortalCulinaromancer);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(iceGloves, restorePotions, combatGear);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Agrith-Na-Na (level 146)", "Flambeed (level 149)", "Karamel (level 136)", "Dessourt (level 121)", "Gelatinnoth Mother (level 130)", "Culinaromancer (level 75)");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestPointRequirement(175));
		req.add(new QuestRequirement(QuestHelperQuest.DESERT_TREASURE, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.HORROR_FROM_THE_DEEP, QuestState.FINISHED));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("20,000 Experience Lamp (Any skill over level 50)", ItemID.ANTIQUE_LAMP, 1)); //4447 is placeholder for filter
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Full access to the Culinaromancer's Chest"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Defeating the Culinaromancer", Arrays.asList(enterPortal, killAgrith, killFlambeed, killKaramel, killDessourt, killMother, killCulinaromancer)));
		return allSteps;
	}

	@Override
	public QuestState getState(Client client)
	{
		int questState = client.getVarbitValue(QuestVarbits.QUEST_RECIPE_FOR_DISASTER.getId());
		if (questState < 4)
		{
			return QuestState.NOT_STARTED;
		}

		return getQuest().getState(client);
	}

	@Override
	public boolean isCompleted()
	{
		return (client.getVarbitValue(QuestVarbits.QUEST_RECIPE_FOR_DISASTER.getId()) < 4 || super.isCompleted());
	}
}
