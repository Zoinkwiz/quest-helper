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
package com.questhelper.helpers.quests.shieldofarrav;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.*;

public class ShieldOfArravPhoenixGang extends BasicQuestHelper
{
	//Items Required
	ItemRequirement book, intelReport, twentyCoins, shieldHalf, certificateHalf, blackArmCertificateHalf, certificate;

	Requirement inPhoenixEntry, intelReportNearby, inPhoenixBase, chestOpen;

	QuestStep startQuest, searchBookcase, talkToReldoAgain, talkToBaraek, goDownToPhoenixGang, talkToStraven, goUpFromPhoenixGang, killJonny, pickupIntelReport,
		returnDownLadder, talkToStravenAgain, getShieldHalf, getShieldHalf1, tradeCertificateHalf, combineCertificate, talkToHaig, talkToRoald, leaveAfterGettingShieldHalf;

	//Zones
	Zone phoenixEntry, phoenixBase;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, startQuest);
		steps.put(1, searchBookcase);
		steps.put(2, talkToReldoAgain);
		steps.put(3, talkToBaraek);

		ConditionalStep getPhoenixTask = new ConditionalStep(this, goDownToPhoenixGang);
		getPhoenixTask.addStep(inPhoenixEntry, talkToStraven);

		steps.put(4, getPhoenixTask);

		ConditionalStep goToKillJonny = new ConditionalStep(this, killJonny);
		goToKillJonny.addStep(new Conditions(intelReport, inPhoenixEntry), talkToStravenAgain);
		goToKillJonny.addStep(intelReport.alsoCheckBank(), returnDownLadder);
		goToKillJonny.addStep(intelReportNearby, pickupIntelReport);
		goToKillJonny.addStep(inPhoenixEntry, goUpFromPhoenixGang);

		steps.put(8, goToKillJonny);

		ConditionalStep completeQuest = new ConditionalStep(this, returnDownLadder);
		completeQuest.addStep(certificate.alsoCheckBank(), talkToRoald);
		completeQuest.addStep(new Conditions(certificateHalf.alsoCheckBank(), blackArmCertificateHalf.alsoCheckBank()), combineCertificate);
		completeQuest.addStep(certificateHalf.alsoCheckBank(), tradeCertificateHalf);
		completeQuest.addStep(new Conditions(inPhoenixBase, shieldHalf.alsoCheckBank()), leaveAfterGettingShieldHalf);
		completeQuest.addStep(shieldHalf.alsoCheckBank(), talkToHaig);
		completeQuest.addStep(new Conditions(inPhoenixBase, chestOpen), getShieldHalf1);
		completeQuest.addStep(inPhoenixBase, getShieldHalf);

		steps.put(9, completeQuest);
		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		book = new ItemRequirement("Book", ItemID.THE_SHIELD_OF_ARRAV);
		intelReport = new ItemRequirement("Intel report", ItemID.INTELLIGENCE_REPORT);
		twentyCoins = new ItemRequirement("Coins", ItemCollections.COINS, 20);
		shieldHalf = new ItemRequirement("Broken shield", ItemID.ARRAVSHIELD1);
		inPhoenixBase = new ZoneRequirement(phoenixBase);
		chestOpen = new ObjectCondition(ObjectID.PHOENIXOPENCHEST);
		certificateHalf = new ItemRequirement("Half certificate", ItemID.ARRAVCERTIFICATE_LFT);
		blackArmCertificateHalf = new ItemRequirement("Half certificate", ItemID.ARRAVCERTIFICATE_RHT);
		certificate = new ItemRequirement("Certificate", ItemID.ARRAVCERTIFICATE);
	}

	@Override
	protected void setupZones()
	{
		phoenixEntry = new Zone(new WorldPoint(3239, 9780, 0), new WorldPoint(3249, 9786, 0));
		phoenixBase = new Zone(new WorldPoint(3232, 9761, 0), new WorldPoint(3254, 9785, 0));
	}

	public void setupConditions()
	{
		inPhoenixEntry = new ZoneRequirement(phoenixEntry);
		intelReportNearby = new ItemOnTileRequirement(intelReport);
	}

	public void setupSteps()
	{
		startQuest = new NpcStep(this, NpcID.RELDO_NORMAL, new WorldPoint(3210, 3494, 0),
			"Talk to Reldo in the Varrock Castle library.");
		startQuest.addDialogSteps("I'm in search of a quest.", "Yes.");
		searchBookcase = new ObjectStep(this, ObjectID.QUESTBOOKCASE, new WorldPoint(3212, 3493, 0),
			"Search the marked bookcase for a book, then read it.", book.highlighted());
		talkToReldoAgain = new NpcStep(this, NpcID.RELDO_NORMAL, new WorldPoint(3210, 3494, 0),
			"Talk to Reldo again.");
		talkToReldoAgain.addDialogStep("Okay, I've read that book about the Shield of Arrav.");
		talkToBaraek = new NpcStep(this, NpcID.BARAEK, new WorldPoint(3218, 3435, 0),
			"Talk to Baraek in the Varrock Square.", twentyCoins);
		talkToBaraek.addDialogStep("Can you tell me where I can find the Phoenix Gang?");
		talkToBaraek.addDialogStep("Okay. Have 20 gold coins.");
		goDownToPhoenixGang = new ObjectStep(this, ObjectID.FAI_VARROCK_LADDER_DEEP, new WorldPoint(3244, 3383, 0),
			"Head into the Phoenix Gang's base in south Varrock.");
		talkToStraven = new NpcStep(this, NpcID.STRAVEN, new WorldPoint(3247, 9781, 0),
			"Talk to Straven.");
		talkToStraven.addDialogStep("I know who you are!");
		talkToStraven.addDialogStep("I'd like to offer you my services.");

		goUpFromPhoenixGang = new ObjectStep(this, ObjectID.PHOENIXLADDER, new WorldPoint(3244, 9783, 0),
			"Go back up to the surface.");
		killJonny = new NpcStep(this, NpcID.JONNY_THE_BEARD_1OP, new WorldPoint(3222, 3395, 0),
			"Kill Jonny the Beard in the Blue Moon Inn in Varrock.");
		pickupIntelReport = new DetailedQuestStep(this, "Pick up the Intel Report.", intelReport);
		returnDownLadder = new ObjectStep(this, ObjectID.FAI_VARROCK_LADDER_DEEP, new WorldPoint(3244, 3383, 0),
			"Return to the Phoenix Gang's base.");
		talkToStravenAgain = new NpcStep(this, NpcID.STRAVEN, new WorldPoint(3247, 9781, 0),
			"Talk to Straven again.");

		getShieldHalf = new ObjectStep(this, ObjectID.PHOENIXSHUTCHEST, new WorldPoint(3235, 9761, 0),
			"Search the chest in the Phoenix base for half of the Shield of Arrav.");
		getShieldHalf1 = new ObjectStep(this, ObjectID.PHOENIXOPENCHEST, new WorldPoint(3235, 9761, 0),
			"Search the chest in the Phoenix base for half of the Shield of Arrav.");
		getShieldHalf.addSubSteps(getShieldHalf1);

		leaveAfterGettingShieldHalf = new ObjectStep(this, ObjectID.PHOENIXLADDER, new WorldPoint(3244, 9783, 0),
			"Go back up to the surface.");
		talkToHaig = new NpcStep(this, NpcID.CURATOR, new WorldPoint(3255, 3449, 0),
			"Trade your weapon store key to your partner. If you can't trade, use the key on them to drop it at their feet. " +
				"AFTERWARDS, talk to Curator Haig in the Varrock Museum.", shieldHalf);
		talkToHaig.addSubSteps(leaveAfterGettingShieldHalf);
		talkToRoald = new NpcStep(this, NpcID.KING_ROALD_CUTSCENE, new WorldPoint(3222, 3473, 0),
			"Talk to King Roald in Varrock Castle to finish the quest.", certificate);

		tradeCertificateHalf = new DetailedQuestStep(this,
			"Trade one of your certificate halves for the other half with another player.  If you cannot trade, use " +
				"the certificate on them to drop it at their feet. They can do the same for you.");
		combineCertificate = new DetailedQuestStep(this,
			"Use the two certificate halves together to create the certificate.", certificateHalf, blackArmCertificateHalf);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(twentyCoins);
		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("Coins", ItemID.COINS, 600));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Locating the Phoenix Gang", Arrays.asList(startQuest,
			searchBookcase, talkToReldoAgain, talkToBaraek, goDownToPhoenixGang, talkToStraven)));
		allSteps.add(new PanelDetails("Joining the gang", Arrays.asList(goUpFromPhoenixGang, killJonny, pickupIntelReport, returnDownLadder, talkToStravenAgain)));
		allSteps.add(new PanelDetails("Returning the shield", Arrays.asList(getShieldHalf, talkToHaig, tradeCertificateHalf, combineCertificate, talkToRoald)));
		return allSteps;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Jonny the beard (level 2)");
	}

	@Override
	public List<String> getNotes()
	{
		return Arrays.asList("You can also do this quest by joining the Black Arm Gang, which instead requires you to kill the weaponsmaster (level 23), or have another player kill them for you.",
			"Once you're accepted into one of the gangs, you CANNOT change gang.",
			"This quest requires you to swap items with another player who's in the other gang, so it's recommended to either find a friend to help you, or you can use the friend's chat 'OSRS SOA' and find someone to help there.");

	}

	@Override
	public boolean isCompleted()
	{
		boolean partComplete = super.isCompleted();
		return (partComplete || QuestHelperQuest.SHIELD_OF_ARRAV_BLACK_ARM_GANG.getVar(client) >= 3);
	}
}
