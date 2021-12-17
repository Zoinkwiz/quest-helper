/*
 * Copyright (c) 2021, Zoinkwiz
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
package com.questhelper.quests.bonevoyage;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.Favour;
import com.questhelper.requirements.player.FavourRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.ObjectStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.BONE_VOYAGE
)
public class BoneVoyage extends BasicQuestHelper
{
	//Items Required
	ItemRequirement vodka2, marrentillPotionUnf;

	//Items Recommended
	ItemRequirement digsiteTeleport, woodcuttingGuildTeleport, varrockTeleport, sarimTeleport, lumberyardTeleport;

	ItemRequirement sawmillAgreement, boneCharm, potionOfSealegs, sawmillProposal;

	Requirement canEnterGuild, onBoat, gottenCharm, givenCharm, talkedToApoth, gottenPotion, givenPotion;

	QuestStep talkToHaig, talkToForeman, talkToSawmillOperator, talkToOperatorInGuild, talkToOperatorInGuildFromGate,
		talkToOperatorInGuildGeneric, returnWithAgreement;

	QuestStep talkToForemanAgain, boardBarge, talkToNavigator, talkToJack, boardBargeAfterJack, talkToNavigatorAgain,
		talkToOddOldMan, talkToApoth, talkToApothAgain, boardBargeWithPotionAndCharm, giveLeadPotion, giveJuniorBone,
		boardBargeWithCharm, boardBargeWithPotion;

	QuestStep boardBargeToSail, navigateShip;

	//Zones
	Zone boat, boatSailing;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToHaig);
		steps.put(5, talkToForeman);
		steps.put(10, talkToSawmillOperator);

		ConditionalStep goTalkToOperatorInGuild = new ConditionalStep(this, talkToOperatorInGuildFromGate);
		goTalkToOperatorInGuild.addStep(canEnterGuild, talkToOperatorInGuild);
		steps.put(11, goTalkToOperatorInGuild);

		steps.put(15, returnWithAgreement);
		steps.put(20, talkToForemanAgain);

		ConditionalStep goTalkToLead = new ConditionalStep(this, boardBarge);
		goTalkToLead.addStep(onBoat, talkToNavigator);
		steps.put(21, goTalkToLead);

		steps.put(22, talkToJack);

		ConditionalStep goReturnToLead = new ConditionalStep(this, boardBargeAfterJack);
		goReturnToLead.addStep(onBoat, talkToNavigatorAgain);
		steps.put(23, goReturnToLead);

		ConditionalStep goGetItems = new ConditionalStep(this, talkToOddOldMan);
		goGetItems.addStep(new Conditions(onBoat, givenCharm, gottenPotion), giveLeadPotion);
		goGetItems.addStep(new Conditions(onBoat, gottenCharm, gottenPotion), giveJuniorBone);
		goGetItems.addStep(new Conditions(gottenCharm, givenPotion), boardBargeWithCharm);
		goGetItems.addStep(new Conditions(givenCharm, gottenPotion), boardBargeWithPotion);
		goGetItems.addStep(new Conditions(gottenCharm, gottenPotion), boardBargeWithPotionAndCharm);
		goGetItems.addStep(new Conditions(gottenCharm, talkedToApoth), talkToApothAgain);
		goGetItems.addStep(gottenCharm, talkToApoth);
		steps.put(25, goGetItems);

		ConditionalStep goSail = new ConditionalStep(this, boardBargeToSail);
		goSail.addStep(onBoat, navigateShip);
		steps.put(30, goSail);
		steps.put(35, goSail);

		return steps;
	}

	public void setupRequirements()
	{
		canEnterGuild = new Conditions(
			new SkillRequirement(Skill.WOODCUTTING, 60, true),
			new FavourRequirement(Favour.HOSIDIUS, 75)
		);

		onBoat = new ZoneRequirement(boat, boatSailing);

		gottenCharm = new VarbitRequirement(5796, 1, Operation.GREATER_EQUAL);
		givenCharm = new VarbitRequirement(5796, 2, Operation.GREATER_EQUAL);

		talkedToApoth = new VarbitRequirement(5797, 1, Operation.GREATER_EQUAL);
		gottenPotion = new VarbitRequirement(5797, 2, Operation.GREATER_EQUAL);
		givenPotion = new VarbitRequirement(5797, 3, Operation.GREATER_EQUAL);

		vodka2 = new ItemRequirement("Vodka", ItemID.VODKA, 2);
		marrentillPotionUnf = new ItemRequirement("Marrentill potion (unf)", ItemID.MARRENTILL_POTION_UNF);

		digsiteTeleport = new ItemRequirement("Teleport to the Digsite", ItemCollections.getDigsitePendants());
		digsiteTeleport.addAlternates(ItemID.DIGSITE_TELEPORT);
		woodcuttingGuildTeleport = new ItemRequirement("Teleport to the Woodcutting Guild", ItemCollections.getSkillsNecklaces());
		woodcuttingGuildTeleport.addAlternates(ItemID.XERICS_TALISMAN, ItemID.KHAREDSTS_MEMOIRS);
		varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.VARROCK_TELEPORT);
		sarimTeleport = new ItemRequirement("Port Sarim teleport", ItemCollections.getAmuletOfGlories());
		sarimTeleport.addAlternates(ItemID.DRAYNOR_MANOR_TELEPORT);
		lumberyardTeleport = new ItemRequirement("Lumberyard teleport", ItemID.LUMBERYARD_TELEPORT);

		sawmillProposal = new ItemRequirement("Sawmill proposal", ItemID.SAWMILL_PROPOSAL);
		sawmillProposal.setTooltip("You can get another from the sawmill operator near Varrock");
		sawmillAgreement = new ItemRequirement("Sawmill agreement", ItemID.SAWMILL_AGREEMENT);
		if (canEnterGuild.check(client))
		{
			sawmillAgreement.setTooltip("You can get another from the sawmill operator in the Woodcutting Guild");
		}
		else
		{
			sawmillAgreement.setTooltip("You can get another by trying to enter the Woodcutting Guild");
		}
		boneCharm = new ItemRequirement("Bone charm", ItemID.BONE_CHARM);
		boneCharm.setTooltip("You can get another from the Odd Old Man north of the Dig Site");
		potionOfSealegs = new ItemRequirement("Potion of sealegs", ItemID.POTION_OF_SEALEGS);
		potionOfSealegs.setTooltip("You can get another from the Apothecary in Varrock");
	}

	public void loadZones()
	{
		boat = new Zone(new WorldPoint(3355, 3451, 1), new WorldPoint(3366, 3455, 1));
		boatSailing = new Zone(new WorldPoint(1812, 4750, 0), new WorldPoint(1840, 4774, 2));
	}

	public void setupSteps()
	{
		talkToHaig = new NpcStep(this, NpcID.CURATOR_HAIG_HALEN, new WorldPoint(3257, 3448, 0),
			"Talk to Curator Haig in the Varrock Museum.");
		talkToHaig.addDialogSteps("Have you any interesting news?", "Sign me up!");

		talkToForeman = new NpcStep(this, NpcID.BARGE_FOREMAN, new WorldPoint(3364, 3445, 0),
			"Talk to the Barge Foreman north of the Dig Site.");

		talkToSawmillOperator = new NpcStep(this, NpcID.SAWMILL_OPERATOR, new WorldPoint(3302, 3492, 0),
			"Talk to the Sawmill Operator north east of Varrock.");
		talkToSawmillOperator.addDialogStep("I'm here on behalf of the museum archaeological team.");

		talkToOperatorInGuild = new NpcStep(this, NpcID.SAWMILL_OPERATOR_9140, new WorldPoint(1620, 3499, 0),
			"Talk to the Sawmill Operator in the Woodcutting Guild on Zeah.", sawmillProposal);
		talkToOperatorInGuild.addDialogStep("I'm here on behalf of the museum archaeological team.");
		talkToOperatorInGuildFromGate = new ObjectStep(this, ObjectID.GATE_28852, new WorldPoint(1657, 3504, 0),
			"Attempt to enter the Woodcutting Guild on Zeah to talk to the guild's sawmill operator.", sawmillProposal);

		talkToOperatorInGuildGeneric = talkToOperatorInGuildFromGate;
		if (canEnterGuild.check(client))
		{
			talkToOperatorInGuildGeneric = talkToOperatorInGuild;
		}

		returnWithAgreement = new NpcStep(this, NpcID.SAWMILL_OPERATOR, new WorldPoint(3302, 3492, 0),
		"Return to the Sawmill Operator north east of Varrock.", sawmillAgreement);
		returnWithAgreement.addDialogStep("I'm here on behalf of the museum archaeological team.");

		talkToForemanAgain = new NpcStep(this, NpcID.BARGE_FOREMAN, new WorldPoint(3364, 3445, 0),
			"Return to the Barge Foreman north of the Dig Site.");
		boardBarge = new NpcStep(this, NpcID.BARGE_GUARD_8013, new WorldPoint(3362, 3446, 0),
			"Board the barge.");
		boardBarge.addDialogStep("Can I go onto the barge?");
		talkToNavigator = new NpcStep(this, NpcID.LEAD_NAVIGATOR, new WorldPoint(3363, 3453, 1),
			"Talk to the Lead Navigator.");
		talkToNavigator.addDialogStep("Yep, that would be me.");
		talkToNavigator.addDialogStep("No, what happened?");
		talkToJack = new NpcStep(this, NpcID.JACK_SEAGULL, new WorldPoint(3050, 3257, 0),
			"Talk to Jack Seagull in the Port Sarim Pub.");
		talkToJack.addDialogStep("Ever made any cursed voyages?");
		boardBargeAfterJack = new NpcStep(this, NpcID.BARGE_GUARD_8013, new WorldPoint(3362, 3446, 0),
		"Return to the barge.");
		boardBargeAfterJack.addDialogStep("Can I go onto the barge?");
		talkToNavigatorAgain = new NpcStep(this, NpcID.LEAD_NAVIGATOR, new WorldPoint(3363, 3453, 1),
			"Talk to the Lead Navigator again.");
		talkToOddOldMan = new NpcStep(this, NpcID.ODD_OLD_MAN, new WorldPoint(3360, 3505, 0),
			"Talk to the Odd Old Man north of the Dig Site.");
		talkToOddOldMan.addDialogSteps("Talk about lucky charms.", "I'm making a cursed voyage.");
		talkToApoth = new NpcStep(this, NpcID.APOTHECARY, new WorldPoint(3195, 3405, 0),
			"Talk the Apothecary in south west Varrock.", marrentillPotionUnf, vodka2);
		talkToApoth.addDialogSteps("Talk about something else.", "Talk about Bone Voyage.");
		talkToApothAgain = new NpcStep(this, NpcID.APOTHECARY, new WorldPoint(3195, 3405, 0),
			"Talk the Apothecary again.", marrentillPotionUnf, vodka2);
		talkToApothAgain.addDialogSteps("Talk about something else.", "Talk about Bone Voyage.");
		boardBargeWithPotionAndCharm = new NpcStep(this, NpcID.BARGE_GUARD_8013, new WorldPoint(3362, 3446, 0),
			"Board the barge.", boneCharm, potionOfSealegs);
		boardBargeWithPotionAndCharm.addDialogStep("Can I go onto the barge?");
		boardBargeWithPotion = new NpcStep(this, NpcID.BARGE_GUARD_8013, new WorldPoint(3362, 3446, 0),
			"Board the barge.", potionOfSealegs);
		boardBargeWithPotion.addDialogStep("Can I go onto the barge?");
		boardBargeWithCharm = new NpcStep(this, NpcID.BARGE_GUARD_8013, new WorldPoint(3362, 3446, 0),
			"Board the barge.", boneCharm);
		boardBargeWithCharm.addDialogStep("Can I go onto the barge?");
		boardBargeWithPotionAndCharm.addSubSteps(boardBargeWithCharm, boardBargeWithPotion);
		giveLeadPotion  = new NpcStep(this, NpcID.LEAD_NAVIGATOR, new WorldPoint(3363, 3453, 1),
			"Give the Lead Navigator the potion.", potionOfSealegs);
		giveJuniorBone = new NpcStep(this, NpcID.JUNIOR_NAVIGATOR, new WorldPoint(3363, 3453, 1),
			"Give the Junior Navigator the bone charm.", boneCharm);

		boardBargeToSail = new NpcStep(this, NpcID.BARGE_GUARD_8013, new WorldPoint(3362, 3446, 0),
			"Board the barge.");
		boardBargeToSail.addDialogStep("Can I go onto the barge?");
		navigateShip = new NpcStep(this, NpcID.LEAD_NAVIGATOR, new WorldPoint(3363, 3453, 1),
			"Talk to the Lead Navigator to set sail. Navigate the ship by keeping the arrow in the middle. Raise the " +
				"sails to go faster.");
		navigateShip.addDialogStep("I'm ready, let's go.");
		navigateShip.addSubSteps(boardBargeToSail);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(vodka2, marrentillPotionUnf);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(digsiteTeleport.quantity(4), woodcuttingGuildTeleport, varrockTeleport, sarimTeleport,
			lumberyardTeleport.quantity(2));
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		final int KUDOS_VARBIT = 3637;
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new VarbitRequirement(KUDOS_VARBIT, Operation.GREATER_EQUAL, 100, "100 Kudos"));
		req.add(new QuestRequirement(QuestHelperQuest.THE_DIG_SITE, QuestState.FINISHED));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to Fossil Island"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Collections.singletonList(talkToHaig)));
		allSteps.add(new PanelDetails("Securing materials", Arrays.asList(talkToForeman, talkToSawmillOperator,
			talkToOperatorInGuildGeneric, returnWithAgreement, talkToForemanAgain)));
		allSteps.add(new PanelDetails("Lucky charms", Arrays.asList(boardBarge, talkToNavigator, talkToJack,
			boardBargeAfterJack, talkToNavigatorAgain, talkToOddOldMan, talkToApoth, talkToApothAgain,
			boardBargeWithPotionAndCharm, giveJuniorBone, giveLeadPotion), marrentillPotionUnf, vodka2));
		allSteps.add(new PanelDetails("The voyage", Collections.singletonList(navigateShip)));
		return allSteps;
	}
}
