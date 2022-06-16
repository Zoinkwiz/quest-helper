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
package com.questhelper.quests.shieldofarrav;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
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
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.SHIELD_OF_ARRAV_BLACK_ARM_GANG
)
public class ShieldOfArravBlackArmGang extends BasicQuestHelper
{
	//Items Required
	ItemRequirement storeRoomKey, twoPhoenixCrossbow, shieldHalf, certificateHalf, phoenixCertificateHalf, certificate;

	Requirement inStoreRoom, weaponMasterAlive, isUpstairsInBase, cupboardOpen;

	QuestStep talkToCharlie, getWeaponStoreKey, talkToKatrine, goUpToWeaponStore, killWeaponsMaster, pickupTwoCrossbows, goDownFromWeaponStore, returnToKatrine,
		goUpstairsInBase, getShieldFromCupboard, getShieldFromCupboard1, goDownstairsInBase, talkToHaig, tradeCertificateHalf, combineCertificate, talkToRoald;

	//Zones
	Zone storeRoom, upstairsInBase;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToCharlie);
		steps.put(1, talkToKatrine);

		ConditionalStep gettingTheCrossbows = new ConditionalStep(this, getWeaponStoreKey);
		gettingTheCrossbows.addStep(new Conditions(twoPhoenixCrossbow, inStoreRoom), goDownFromWeaponStore);
		gettingTheCrossbows.addStep(twoPhoenixCrossbow, returnToKatrine);
		gettingTheCrossbows.addStep(new Conditions(weaponMasterAlive, inStoreRoom), killWeaponsMaster);
		gettingTheCrossbows.addStep(inStoreRoom, pickupTwoCrossbows);
		gettingTheCrossbows.addStep(storeRoomKey.alsoCheckBank(questBank), goUpToWeaponStore);

		steps.put(2, gettingTheCrossbows);

		ConditionalStep completeQuest = new ConditionalStep(this, goUpstairsInBase);
		completeQuest.addStep(certificate.alsoCheckBank(questBank), talkToRoald);
		completeQuest.addStep(new Conditions(certificateHalf.alsoCheckBank(questBank), phoenixCertificateHalf.alsoCheckBank(questBank)), combineCertificate);
		completeQuest.addStep(certificateHalf.alsoCheckBank(questBank), tradeCertificateHalf);
		completeQuest.addStep(new Conditions(shieldHalf.alsoCheckBank(questBank), isUpstairsInBase), goDownstairsInBase);
		completeQuest.addStep(shieldHalf.alsoCheckBank(questBank), talkToHaig);
		completeQuest.addStep(new Conditions(isUpstairsInBase, cupboardOpen), getShieldFromCupboard1);
		completeQuest.addStep(isUpstairsInBase, getShieldFromCupboard);

		steps.put(3, completeQuest);
		return steps;
	}

	@Override
	public void setupRequirements()
	{
		storeRoomKey = new ItemRequirement("Weapon store key", ItemID.WEAPON_STORE_KEY);
		twoPhoenixCrossbow = new ItemRequirement("Phoenix crossbow", ItemID.PHOENIX_CROSSBOW, 2);
		shieldHalf = new ItemRequirement("Broken shield", ItemID.BROKEN_SHIELD_765);
		certificateHalf = new ItemRequirement("Half certificate", ItemID.HALF_CERTIFICATE_11174);
		phoenixCertificateHalf = new ItemRequirement("Half certificate", ItemID.HALF_CERTIFICATE);
		certificate = new ItemRequirement("Certificate", ItemID.CERTIFICATE);
	}

	public void loadZones()
	{
		storeRoom = new Zone(new WorldPoint(3242, 3380, 1), new WorldPoint(3252, 3386, 1));
		upstairsInBase = new Zone(new WorldPoint(3182, 3382, 1), new WorldPoint(3201, 3398, 1));
	}

	public void setupConditions()
	{
		inStoreRoom = new ZoneRequirement(storeRoom);
		weaponMasterAlive = new NpcCondition(NpcID.WEAPONSMASTER);
		isUpstairsInBase = new ZoneRequirement(upstairsInBase);
		cupboardOpen = new ObjectCondition(ObjectID.CUPBOARD_2401);
	}

	public void setupSteps()
	{
		talkToCharlie = new NpcStep(this, NpcID.CHARLIE_THE_TRAMP, new WorldPoint(3208, 3392, 0), "To start the quest as the Black Arm Gang, talk to Charlie the Tramp in south Varrock to start.");
		talkToCharlie.addDialogStep("Is there anything down this alleyway?");
		talkToCharlie.addDialogStep("Do you think they would let me join?");

		talkToKatrine = new NpcStep(this, NpcID.KATRINE, new WorldPoint(3185, 3385, 0), "Talk to Katrine down the alley to the west.");
		talkToKatrine.addDialogStep("I've heard you're the Black Arm Gang.");
		talkToKatrine.addDialogStep("I'd rather not reveal my sources.");
		talkToKatrine.addDialogStep("I want to become a member of your gang.");
		talkToKatrine.addDialogStep("Well, you can give me a try can't you?");
		talkToKatrine.addDialogStep("Ok, no problem.");

		// TODO: Convert info to initial step details
		getWeaponStoreKey = new DetailedQuestStep(this, "Get the weapon storeroom key from another player.  If you cannot trade, have them use the key on you to drop it at your feet.");

		goUpToWeaponStore = new ObjectStep(this, ObjectID.LADDER_11794, new WorldPoint(3252, 3384, 0), "Go up the ladder in south east Varrock to the Phoenix Weapon Storeroom.", storeRoomKey);

		killWeaponsMaster = new NpcStep(this, NpcID.WEAPONSMASTER, new WorldPoint(3247, 3384, 1), "Kill the Weaponsmaster, or have someone else kill him.");

		// TODO: Issue with this step, as a crossbow upon killing the weaponsmaster dissappears/appears, the initial despawn doesn't effect the initial area check, so a blue marker remains on the floor
		pickupTwoCrossbows = new DetailedQuestStep(this, "Pick up TWO phoenix crossbows", twoPhoenixCrossbow);

		goDownFromWeaponStore = new ObjectStep(this, ObjectID.LADDER_11802, new WorldPoint(3252, 3384, 1), "Go back down from the storeroom.", twoPhoenixCrossbow);

		returnToKatrine = new NpcStep(this, NpcID.KATRINE, new WorldPoint(3185, 3385, 0), "Return to Katrine with the crossbows.", twoPhoenixCrossbow);
		returnToKatrine.addSubSteps(goDownFromWeaponStore);

		goUpstairsInBase = new ObjectStep(this, ObjectID.STAIRCASE_11796, new WorldPoint(3189, 3390, 0), "Go up the stairs in the Black Arm Gang base.");

		getShieldFromCupboard = new ObjectStep(this, ObjectID.CUPBOARD_2400, new WorldPoint(3189, 3386, 1), "Search the cupboard for half of the Shield of Arrav.");
		getShieldFromCupboard1 = new ObjectStep(this, ObjectID.CUPBOARD_2401, new WorldPoint(3189, 3386, 1), "Search the cupboard for half of the Shield of Arrav.");
		getShieldFromCupboard.addSubSteps(getShieldFromCupboard1);

		goDownstairsInBase = new ObjectStep(this, ObjectID.STAIRCASE_11799, new WorldPoint(3189, 3391, 1), "Go back downstairs.");

		tradeCertificateHalf = new DetailedQuestStep(this, "Trade one of your certificate halves for the other half with another player. If you cannot trade, use the certificate on them to drop it at their feet. They can do the same for you.");
		combineCertificate = new DetailedQuestStep(this, "Use the two certificate halves together to create the certificate.", certificateHalf, phoenixCertificateHalf);

		talkToHaig = new NpcStep(this, NpcID.CURATOR_HAIG_HALEN, new WorldPoint(3255, 3449, 0), "Talk to Curator Haig in the Varrock Museum.", shieldHalf);
		talkToHaig.addSubSteps(goDownstairsInBase);

		talkToRoald = new NpcStep(this, NpcID.KING_ROALD_4163, new WorldPoint(3222, 3473, 0), "Talk to King Roald in Varrock Castle to finish the quest.", certificate);

	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("600 Coins", ItemID.COINS_995, 600));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Start quest", Arrays.asList(talkToCharlie, talkToKatrine)));
		allSteps.add(new PanelDetails("Get the phoenix crossbows", Arrays.asList(getWeaponStoreKey, goUpToWeaponStore, killWeaponsMaster, pickupTwoCrossbows, returnToKatrine)));
		allSteps.add(new PanelDetails("Return the shield", Arrays.asList(goUpstairsInBase, getShieldFromCupboard, talkToHaig, tradeCertificateHalf, combineCertificate, talkToRoald)));
		return allSteps;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Weaponsmaster (level 23), or a friend to kill him for you");
	}

	@Override
	public List<String> getNotes()
	{
		return
			Arrays.asList("You can also do this quest by joining the Phoenix Gang, which instead requires you to kill Jonny the beard (level 2).",
				"Once you're accepted into one of the gangs, you CANNOT change gang.",
				"This quest requires you to swap items with another player who's in the other gang, so it's recommended to either find a friend to help you, or you can use the friend's chat 'OSRS SOA' and find someone to help there.");
	}

	@Override
	public boolean isCompleted()
	{
		boolean partComplete = super.isCompleted();
		return (partComplete || QuestHelperQuest.SHIELD_OF_ARRAV_PHOENIX_GANG.getVar(client) >= 6);
	}
}
