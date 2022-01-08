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
package com.questhelper.quests.thefremennikisles;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.PrayerRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Prayer;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_FREMENNIK_ISLES
)
public class TheFremennikIsles extends BasicQuestHelper
{
	//Items Required
	ItemRequirement tuna, ores, jesterHat, jesterTights, jesterTop, jesterBoots, arcticLogs8, splitLogs8,
		knife, rope8, rope4, splitLogs4, yakTop, yakBottom, royalDecree, roundShield, yakTopWorn, yakBottomWorn,
		shieldWorn, meleeWeapon, food, head, needle, thread, coins15, bronzeNail, hammer, rope, axe, rope9;

	Requirement inIslands, inJatizso, inNeitiznot, inTrollLands, hasJesterOutfit, jestering1, repairedBridge1,
		repairedBridge2, inNeitiznotOrTrollLands, collectedFlosi, collectedHring, collectedSkuli,
		collectedValigga, collectedKeepa, collectedRaum, inTrollCave, inKingCave, killedTrolls, protectRanged;

	QuestStep talkToMord, travelToJatizso, talkToGjuki, continueTalkingToGjuki, bringOreToGjuki,
		talkToGjukiAfterOre, getJesterOutfit, talkToSlug, travelToNeitiznot, returnToRellekkaFromJatizso,
		goSpyOnMawnis, performForMawnis, tellSlugReport1, talkToMawnis, talkToMawnisWithLogs, talkToMawnisAfterItems,
		repairBridge1, repairBridge2, repairBridge1Second, talkToMawnisAfterRepair, talkToGjukiToReport,
		travelToJatizsoToReport, leaveNeitiznotToReport, collectFromHring, collectFromSkuli, collectFromVanligga, collectFromKeepa,
		talkToGjukiAfterCollection1, collectFromHringAgain, collectFromRaum, collectFromSkuliAgain, collectFromKeepaAgain, collectFromFlosi,
		talkToGjukiAfterCollection2, travelToNeitiznotToSpyAgain, talkToSlugToSpyAgain, returnToRellekkaFromJatizsoToSpyAgain, performForMawnisAgain,
		goSpyOnMawnisAgain, reportBackToSlugAgain, returnToRellekkaFromNeitiznotAfterSpy2, travelToJatizsoAfterSpy2, talkToGjukiAfterSpy2,
		returnToRellekkaFromJatizsoWithDecree, travelToNeitiznotWithDecree, talkToMawnisWithDecree, getYakArmour, returnToRellekkaFromJatizsoAfterDecree,
		travelToNeitiznotAfterDecree, talkToMawnisAfterDecree, makeShield, enterCave, killTrolls, enterKingRoom, killKing, decapitateKing, finishQuest, finishQuestGivenHead;

	//Zones
	Zone islands, jatizso1, jatizso2, neitiznot1, neitiznot2, trollLands, trollCave, kingCave;

	PanelDetails prepareForRepairPanel, prepareForCombatPanel;

	List<ItemRequirement> items;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		setupPanels();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToMord);

		ConditionalStep goTalkToGjuki = new ConditionalStep(this, travelToJatizso);
		goTalkToGjuki.addStep(inIslands, talkToGjuki);

		steps.put(5, goTalkToGjuki);
		steps.put(10, goTalkToGjuki);

		ConditionalStep finishGjukiDialog = new ConditionalStep(this, travelToJatizso);
		finishGjukiDialog.addStep(inIslands, continueTalkingToGjuki);
		steps.put(20, finishGjukiDialog);

		ConditionalStep getMithrilOre = new ConditionalStep(this, travelToJatizso);
		getMithrilOre.addStep(inIslands, bringOreToGjuki);
		steps.put(30, getMithrilOre);

		ConditionalStep continueTalkingToGjukiAfterOre = new ConditionalStep(this, travelToJatizso);
		continueTalkingToGjukiAfterOre.addStep(inIslands, talkToGjukiAfterOre);

		steps.put(40, continueTalkingToGjukiAfterOre);

		ConditionalStep spyOnMawnis = new ConditionalStep(this, travelToJatizso);
		spyOnMawnis.addStep(jestering1, performForMawnis);
		spyOnMawnis.addStep(new Conditions(hasJesterOutfit, inNeitiznot), talkToSlug);
		spyOnMawnis.addStep(new Conditions(new Conditions(LogicType.OR, inJatizso, inTrollLands), hasJesterOutfit), returnToRellekkaFromJatizso);
		spyOnMawnis.addStep(hasJesterOutfit, travelToNeitiznot);
		spyOnMawnis.addStep(inJatizso, getJesterOutfit);

		steps.put(50, spyOnMawnis);

		ConditionalStep spyOnMawnisP2 = new ConditionalStep(this, travelToJatizso);
		spyOnMawnisP2.addStep(jestering1, performForMawnis);
		spyOnMawnisP2.addStep(new Conditions(hasJesterOutfit, inNeitiznot), goSpyOnMawnis);
		spyOnMawnisP2.addStep(new Conditions(new Conditions(LogicType.OR, inJatizso, inTrollLands), hasJesterOutfit), returnToRellekkaFromJatizso);
		spyOnMawnisP2.addStep(hasJesterOutfit, travelToNeitiznot);
		spyOnMawnisP2.addStep(inJatizso, getJesterOutfit);

		steps.put(55, spyOnMawnisP2);

		ConditionalStep reportToSlug = new ConditionalStep(this, travelToNeitiznot);
		reportToSlug.addStep(inNeitiznot, tellSlugReport1);

		steps.put(60, reportToSlug);
		steps.put(70, reportToSlug);
		steps.put(80, reportToSlug);

		ConditionalStep talkToMawnisToHelp = new ConditionalStep(this, travelToNeitiznot);
		talkToMawnisToHelp.addStep(new Conditions(inNeitiznot), talkToMawnis);
		talkToMawnisToHelp.addStep(new Conditions(LogicType.OR, inJatizso, inTrollLands), returnToRellekkaFromJatizso);

		steps.put(90, talkToMawnisToHelp);

		ConditionalStep bringMawnisItems = new ConditionalStep(this, travelToNeitiznot);
		bringMawnisItems.addStep(new Conditions(inNeitiznot), talkToMawnisWithLogs);
		bringMawnisItems.addStep(new Conditions(LogicType.OR, inJatizso, inTrollLands), returnToRellekkaFromJatizso);

		steps.put(100, bringMawnisItems);
		steps.put(110, bringMawnisItems);
		steps.put(120, bringMawnisItems);

		ConditionalStep talkToMawnisAfterItemsSteps = new ConditionalStep(this, travelToNeitiznot);
		talkToMawnisAfterItemsSteps.addStep(new Conditions(inNeitiznot), talkToMawnisAfterItems);
		talkToMawnisAfterItemsSteps.addStep(new Conditions(LogicType.OR, inJatizso, inTrollLands), returnToRellekkaFromJatizso);

		steps.put(130, talkToMawnisAfterItemsSteps);

		ConditionalStep repairBridges = new ConditionalStep(this, travelToNeitiznot);
		repairBridges.addStep(new Conditions(inNeitiznotOrTrollLands, repairedBridge1, repairedBridge2), talkToMawnisAfterRepair);
		repairBridges.addStep(new Conditions(inNeitiznotOrTrollLands, repairedBridge1), repairBridge2);
		repairBridges.addStep(new Conditions(inNeitiznotOrTrollLands), repairBridge1);
		repairBridges.addStep(new Conditions(inJatizso), returnToRellekkaFromJatizso);

		steps.put(140, repairBridges);

		ConditionalStep reportAfterBridgeRepair = new ConditionalStep(this, travelToNeitiznot);
		reportAfterBridgeRepair.addStep(inNeitiznotOrTrollLands, talkToMawnisAfterRepair);
		reportAfterBridgeRepair.addStep(inJatizso, returnToRellekkaFromJatizso);

		steps.put(150, reportAfterBridgeRepair);

		ConditionalStep reportBackToGjuki = new ConditionalStep(this, travelToJatizsoToReport);
		reportBackToGjuki.addStep(inNeitiznotOrTrollLands, leaveNeitiznotToReport);
		reportBackToGjuki.addStep(inJatizso, talkToGjukiToReport);

		steps.put(160, reportBackToGjuki);
		steps.put(170, reportBackToGjuki);
		steps.put(180, reportBackToGjuki);
		steps.put(190, reportBackToGjuki);

		ConditionalStep collectTax = new ConditionalStep(this, travelToJatizsoToReport);
		collectTax.addStep(new Conditions(inJatizso, collectedValigga, collectedKeepa, collectedSkuli, collectedHring), talkToGjukiAfterCollection1);
		collectTax.addStep(new Conditions(inJatizso, collectedValigga, collectedKeepa, collectedSkuli), collectFromHring);
		collectTax.addStep(new Conditions(inJatizso, collectedValigga, collectedKeepa), collectFromSkuli);
		collectTax.addStep(new Conditions(inJatizso, collectedKeepa), collectFromVanligga);
		collectTax.addStep(inJatizso, collectFromKeepa);

		steps.put(200, collectTax);

		ConditionalStep collectTaxOnBeards = new ConditionalStep(this, travelToJatizsoToReport);
		collectTaxOnBeards.addStep(new Conditions(inJatizso, collectedHring, collectedRaum, collectedSkuli, collectedKeepa, collectedFlosi), talkToGjukiAfterCollection2);
		collectTaxOnBeards.addStep(new Conditions(inJatizso, collectedHring, collectedRaum, collectedSkuli, collectedKeepa), collectFromFlosi);
		collectTaxOnBeards.addStep(new Conditions(inJatizso, collectedHring, collectedRaum, collectedSkuli), collectFromKeepaAgain);
		collectTaxOnBeards.addStep(new Conditions(inJatizso, collectedHring, collectedRaum), collectFromSkuliAgain);
		collectTaxOnBeards.addStep(new Conditions(inJatizso, collectedHring), collectFromRaum);
		collectTaxOnBeards.addStep(inJatizso, collectFromHringAgain);

		steps.put(210, collectTaxOnBeards);

		ConditionalStep returnToSpyAgain = new ConditionalStep(this, travelToNeitiznotToSpyAgain);
		returnToSpyAgain.addStep(inNeitiznotOrTrollLands, talkToSlugToSpyAgain);
		returnToSpyAgain.addStep(inJatizso, returnToRellekkaFromJatizsoToSpyAgain);

		steps.put(230, returnToSpyAgain);

		ConditionalStep spyOnMawnisAgain = new ConditionalStep(this, travelToNeitiznotToSpyAgain);
		spyOnMawnisAgain.addStep(jestering1, performForMawnisAgain);
		spyOnMawnisAgain.addStep(inNeitiznotOrTrollLands, goSpyOnMawnisAgain);
		spyOnMawnisAgain.addStep(inJatizso, returnToRellekkaFromJatizsoToSpyAgain);

		steps.put(235, spyOnMawnisAgain);

		ConditionalStep reportBackToSlug = new ConditionalStep(this, travelToNeitiznotToSpyAgain);
		reportBackToSlug.addStep(inNeitiznotOrTrollLands, reportBackToSlugAgain);
		reportBackToSlug.addStep(inJatizso, returnToRellekkaFromJatizsoToSpyAgain);

		steps.put(240, reportBackToSlug);
		steps.put(250, reportBackToSlug);

		ConditionalStep reportBackToGjukiAgain = new ConditionalStep(this, travelToJatizsoAfterSpy2);
		reportBackToGjukiAgain.addStep(inJatizso, talkToGjukiAfterSpy2);
		reportBackToGjukiAgain.addStep(inNeitiznotOrTrollLands, returnToRellekkaFromNeitiznotAfterSpy2);

		steps.put(260, reportBackToGjukiAgain);

		ConditionalStep reportBackToMawnisWithDecree = new ConditionalStep(this, travelToNeitiznotWithDecree);
		reportBackToMawnisWithDecree.addStep(inNeitiznotOrTrollLands, talkToMawnisWithDecree);
		reportBackToMawnisWithDecree.addStep(inJatizso, returnToRellekkaFromJatizsoWithDecree);

		steps.put(270, reportBackToMawnisWithDecree);

		ConditionalStep reportBackToMawnisAfterDecree = new ConditionalStep(this, travelToNeitiznotAfterDecree);
		reportBackToMawnisAfterDecree.addStep(inNeitiznotOrTrollLands, talkToMawnisAfterDecree);
		reportBackToMawnisAfterDecree.addStep(inJatizso, returnToRellekkaFromJatizsoAfterDecree);

		steps.put(275, reportBackToMawnisAfterDecree);

		ConditionalStep makeArmour = new ConditionalStep(this, travelToNeitiznotAfterDecree);
		makeArmour.addStep(inNeitiznotOrTrollLands, getYakArmour);
		makeArmour.addStep(inJatizso, returnToRellekkaFromJatizsoAfterDecree);

		steps.put(280, makeArmour);

		ConditionalStep makeShieldSteps = new ConditionalStep(this, travelToNeitiznotAfterDecree);
		makeShieldSteps.addStep(inNeitiznotOrTrollLands, makeShield);
		makeShieldSteps.addStep(inJatizso, returnToRellekkaFromJatizsoAfterDecree);

		steps.put(290, makeShieldSteps);

		ConditionalStep goToKillKing = new ConditionalStep(this, travelToNeitiznotAfterDecree);
		goToKillKing.addStep(new Conditions(inKingCave), killKing);
		goToKillKing.addStep(new Conditions(inTrollCave, killedTrolls), enterKingRoom);
		goToKillKing.addStep(inTrollCave, killTrolls);
		goToKillKing.addStep(inIslands, enterCave);

		steps.put(300, goToKillKing);
		steps.put(310, goToKillKing);

		ConditionalStep removeHead = new ConditionalStep(this, travelToNeitiznotAfterDecree);
		removeHead.addStep(new Conditions(inKingCave), decapitateKing);
		removeHead.addStep(new Conditions(inTrollCave, killedTrolls), enterKingRoom);
		removeHead.addStep(inTrollCave, killTrolls);
		removeHead.addStep(inIslands, enterCave);

		steps.put(320, removeHead);

		steps.put(325, finishQuest);
		steps.put(330, finishQuestGivenHead);
		steps.put(331, finishQuestGivenHead);
		steps.put(332, finishQuestGivenHead);

		return steps;
	}

	public void setupItemRequirements()
	{
		needle = new ItemRequirement("Needle", ItemID.NEEDLE);
		thread = new ItemRequirement("Thread", ItemID.THREAD);
		coins15 = new ItemRequirement("Coins", ItemID.COINS_995, 15);
		bronzeNail = new ItemRequirement("Bronze nail", ItemID.BRONZE_NAILS);
		hammer = new ItemRequirement("Hammer", ItemCollections.getHammer());
		rope = new ItemRequirement("Rope", ItemID.ROPE);
		rope9 = new ItemRequirement("Rope", ItemID.ROPE, 9);
		yakTopWorn = new ItemRequirement("Yak-hide armour (top)", ItemID.YAKHIDE_ARMOUR, 1, true);
		yakBottomWorn = new ItemRequirement("Yak-hide armour (bottom)", ItemID.YAKHIDE_ARMOUR_10824, 1, true);
		shieldWorn = new ItemRequirement("Neitiznot shield", ItemID.NEITIZNOT_SHIELD, 1, true);
		meleeWeapon = new ItemRequirement("Melee gear", -1, -1);
		meleeWeapon.setDisplayItemId(BankSlotIcons.getCombatGear());
		food = new ItemRequirement("Food + potions", ItemCollections.getGoodEatingFood(), -1);
		tuna = new ItemRequirement("Raw tuna", ItemID.RAW_TUNA);
		axe = new ItemRequirement("Any axe", ItemCollections.getAxes());

		tuna.setTooltip("You can buy some from Flosi in east Jatizso, or fish some from the pier.");
		if (client.getRealSkillLevel(Skill.MINING) >= 55)
		{
			ores = new ItemRequirement("Mithril ore", ItemID.MITHRIL_ORE, 6);
		}
		else if (client.getRealSkillLevel(Skill.MINING) >= 10)
		{
			ores = new ItemRequirement("Coal", ItemID.COAL, 7);
		}
		else
		{
			ores = new ItemRequirement("Tin ore", ItemID.TIN_ORE, 8);
		}
		ores.setTooltip("You can mine some in the underground mine north west of Jatizso.");

		jesterHat = new ItemRequirement("Silly jester hat", ItemID.SILLY_JESTER_HAT, 1, true);
		jesterTop = new ItemRequirement("Silly jester body", ItemID.SILLY_JESTER_TOP, 1, true);
		jesterTights = new ItemRequirement("Silly jester tights", ItemID.SILLY_JESTER_TIGHTS, 1, true);
		jesterBoots = new ItemRequirement("Silly jester boots", ItemID.SILLY_JESTER_BOOTS, 1, true);
		arcticLogs8 = new ItemRequirement("Arctic pine logs", ItemID.ARCTIC_PINE_LOGS, 8);
		splitLogs8 = new ItemRequirement("Split log", ItemID.SPLIT_LOG, 8);
		splitLogs4 = new ItemRequirement("Split log", ItemID.SPLIT_LOG, 4);
		yakTop = new ItemRequirement("Yak-hide armour (top)", ItemID.YAKHIDE_ARMOUR);
		yakBottom = new ItemRequirement("Yak-hide armour (bottom)", ItemID.YAKHIDE_ARMOUR_10824);
		roundShield = new ItemRequirement("Neitiznot shield", ItemID.NEITIZNOT_SHIELD);

		if (client.getAccountType().isIronman())
		{
			splitLogs8.setTooltip("Cut down the arctic pines nearby, and split them on the woodcutting stump in central Neitiznot");
			splitLogs4.setTooltip("Cut down the arctic pines nearby, and split them on the woodcutting stump in central Neitiznot");
			yakTop.setTooltip("Kill yaks for 2 hides, have Thakkrad next to Mawnis cure them, and use a thread + needle to craft");
			yakBottom.setTooltip("Kill yaks for a hide, have Thakkrad next to Mawnis cure them, and use a thread + needle to craft");
			roundShield.setTooltip("Get 2 arctic pine logs, a bronze nail, a hammer, and a rope, and make the shield on the woodcutting stump in central Neitiznot");
		}
		else
		{
			if (client.getRealSkillLevel(Skill.WOODCUTTING) >= 56)
			{
				splitLogs8.setTooltip("Buy some from the GE, or cut down the arctic pines nearby, and split them on the woodcutting stump in central Neitiznot");
				splitLogs4.setTooltip("Buy some from the GE, or cut down the arctic pines nearby, and split them on the woodcutting stump in central Neitiznot");
				roundShield.setTooltip("Buy from the GE, or get 2 arctic pine logs, a bronze nail, a hammer, and a rope, and make the shield on the woodcutting stump in central Neitiznot");
			}
			else
			{
				splitLogs8.setTooltip("Buy some from the GE, or get level 56 Woodcutting");
				splitLogs4.setTooltip("Buy some from the GE, or get level 56 Woodcutting");
				roundShield.setTooltip("Buy from the GE, or get level 56 Woodcutting");
			}

			if (client.getRealSkillLevel(Skill.CRAFTING) >= 46)
			{
				yakTop.setTooltip("Buy from the GE, or kill yaks for 2 hides, have Thakkrad next to Mawnis cure them, and use a thread + needle to craft");
				yakBottom.setTooltip("Buy from the GE, or kill yaks for a hide, have Thakkrad next to Mawnis cure them, and use a thread + needle to craft");
			}
			else
			{
				yakBottom.setTooltip("Buy from the GE, or get 46 crafting");
				yakTop.setTooltip("Buy from the GE, or get 46 crafting");
			}
		}
		knife = new ItemRequirement("Knife", ItemID.KNIFE);
		rope8 = new ItemRequirement("Rope", ItemID.ROPE, 8);
		rope4 = new ItemRequirement("Rope", ItemID.ROPE, 4);

		royalDecree = new ItemRequirement("Royal decree", ItemID.ROYAL_DECREE);
		royalDecree.setTooltip("You can get another from Gjuki on Jatizso");

		head = new ItemRequirement("Decapitated head", ItemID.DECAPITATED_HEAD_10842);
		head.setTooltip("You can get another from the corpse of the Ice Troll King");

		protectRanged = new PrayerRequirement("Protect from Missiles", Prayer.PROTECT_FROM_MISSILES);
	}

	public void loadZones()
	{
		islands = new Zone(new WorldPoint(2298, 3771, 0), new WorldPoint(2432, 3913, 3));
		neitiznot1 = new Zone(new WorldPoint(2304, 3775, 0), new WorldPoint(2368, 3842, 3));
		neitiznot2 = new Zone(new WorldPoint(2368, 3823, 0), new WorldPoint(2383, 3842, 0));
		jatizso1 = new Zone(new WorldPoint(2369, 3777, 0), new WorldPoint(2432, 3825, 3));
		jatizso2 = new Zone(new WorldPoint(2402, 2825, 0), new WorldPoint(2434, 3842, 0));
		trollLands = new Zone(new WorldPoint(2306, 3843, 0), new WorldPoint(2431, 3907, 0));
		trollCave = new Zone(new WorldPoint(2373, 10263, 1), new WorldPoint(2412, 10300, 1));
		kingCave = new Zone(new WorldPoint(2374, 10242, 1), new WorldPoint(2396, 10261, 1));
	}

	public void setupConditions()
	{
		inIslands = new ZoneRequirement(islands);
		inNeitiznot = new ZoneRequirement(neitiznot1, neitiznot2);
		inNeitiznotOrTrollLands = new ZoneRequirement(neitiznot1, neitiznot1, trollLands);
		inJatizso = new ZoneRequirement(jatizso1, jatizso2);
		inTrollLands = new ZoneRequirement(trollLands);
		inTrollCave = new ZoneRequirement(trollCave);
		inKingCave = new ZoneRequirement(kingCave);
		hasJesterOutfit = new ItemRequirements(jesterBoots, jesterHat, jesterTights, jesterTop);
		jestering1 = new VarbitRequirement(6719, 2);
		repairedBridge1 = new VarbitRequirement(3313, 1);
		repairedBridge2 = new VarbitRequirement(3314, 1);

		collectedHring = new VarbitRequirement(3321, 1);
		collectedSkuli = new VarbitRequirement(3320, 1);
		collectedValigga = new VarbitRequirement(3324, 1);
		collectedKeepa = new VarbitRequirement(3325, 1);
		collectedRaum = new VarbitRequirement(3323, 1);
		collectedFlosi = new VarbitRequirement(3322, 1);

		killedTrolls = new VarbitRequirement(3312, 0);
	}

	public void setupSteps()
	{
		talkToMord = new NpcStep(this, NpcID.MORD_GUNNARS, new WorldPoint(2644, 3709, 0), "Talk to Mord Gunnars on a pier in north Rellekka.");
		travelToJatizso = new NpcStep(this, NpcID.MORD_GUNNARS, new WorldPoint(2644, 3709, 0), "Travel to Jatizso with Mord.");
		travelToJatizso.addDialogStep("Can you ferry me to Jatizso?");

		travelToNeitiznot = new NpcStep(this, NpcID.MARIA_GUNNARS_1883, new WorldPoint(2644, 3710, 0), "Travel to Neitiznot with Maria Gunnars.");

		talkToGjuki = new NpcStep(this, NpcID.KING_GJUKI_SORVOTT_IV, new WorldPoint(2407, 3804, 0), "Talk to King Gjuki Sorvott IV on Jatizso.", tuna);
		continueTalkingToGjuki = new NpcStep(this, NpcID.KING_GJUKI_SORVOTT_IV, new WorldPoint(2407, 3804, 0), "Talk to King Gjuki Sorvott IV on Jatizso.");
		talkToGjuki.addSubSteps(continueTalkingToGjuki);

		bringOreToGjuki = new NpcStep(this, NpcID.KING_GJUKI_SORVOTT_IV, new WorldPoint(2407, 3804, 0), "Talk to King Gjuki Sorvott IV on Jatizso.", ores);
		talkToGjukiAfterOre = new NpcStep(this, NpcID.KING_GJUKI_SORVOTT_IV, new WorldPoint(2407, 3804, 0), "Talk to King Gjuki Sorvott IV on Jatizso.");
		bringOreToGjuki.addSubSteps(talkToGjukiAfterOre);

		returnToRellekkaFromJatizso = new NpcStep(this, NpcID.MORD_GUNNARS_1940, new WorldPoint(2420, 3781, 0), "Return to Rellekka with Mord.");
		returnToRellekkaFromJatizso.addDialogStep("Can you ferry me to Rellekka?");

		talkToSlug = new NpcStep(this, NpcID.SLUG_HEMLIGSSEN, new WorldPoint(2335, 3811, 0), "Talk to Slug Hemligssen wearing nothing but your Silly Jester outfit.", jesterHat, jesterTop, jesterTights, jesterBoots);
		talkToSlug.addSubSteps(returnToRellekkaFromJatizso, travelToNeitiznot);
		talkToSlug.addDialogStep("Free stuff please.");
		talkToSlug.addDialogStep("I am ready.");

		getJesterOutfit = new ObjectStep(this, ObjectID.CHEST_21299, new WorldPoint(2407, 3800, 0), "Search the chest behind Gjuki's throne for a silly jester outfit.");
		getJesterOutfit.addDialogStep("Take the jester's hat.");
		getJesterOutfit.addDialogStep("Take the jester's top.");
		getJesterOutfit.addDialogStep("Take the jester's tights.");
		getJesterOutfit.addDialogStep("Take the jester's boots.");

		performForMawnis = new DetailedQuestStep(this, "Perform the actions that Mawnis requests of you.");
		goSpyOnMawnis = new NpcStep(this, NpcID.MAWNIS_BUROWGAR, new WorldPoint(2335, 3800, 0), "Talk to Mawnis in Neitiznot to start spying on him.", jesterHat, jesterTop, jesterTights, jesterBoots);
		goSpyOnMawnis.addSubSteps(performForMawnis);

		tellSlugReport1 = new NpcStep(this, NpcID.SLUG_HEMLIGSSEN, new WorldPoint(2335, 3811, 0), "Report back to Slug Hemligssen.");
		tellSlugReport1.addDialogStep("Yes I have.");
		tellSlugReport1.addDialogStep("They will be ready in two days.");
		tellSlugReport1.addDialogStep("Seventeen militia have been trained.");
		tellSlugReport1.addDialogStep("There are two bridges to repair.");

		talkToMawnis = new NpcStep(this, NpcID.MAWNIS_BUROWGAR, new WorldPoint(2335, 3800, 0), "Take off the jester outfit, and talk to Mawnis.");

		talkToMawnisWithLogs = new NpcStep(this, NpcID.MAWNIS_BUROWGAR, new WorldPoint(2335, 3800, 0), "Bring Mawnis the 8 split logs, 8 rope, and a knife.", splitLogs8, rope8, knife);

		talkToMawnisAfterItems = new NpcStep(this, NpcID.MAWNIS_BUROWGAR, new WorldPoint(2335, 3800, 0), "Talk to Mawnis.");
		talkToMawnisWithLogs.addSubSteps(talkToMawnisAfterItems);

		repairBridge1 = new ObjectStep(this, ObjectID.ROPE_BRIDGE_21310, new WorldPoint(2314, 3840, 0), "Right-click " +
			"repair the bridges to the north of Neitiznot. Protect from Missiles before doing this as you'll " +
			"automatically cross the aggressive trolls.", splitLogs8, rope8, knife, protectRanged);
		repairBridge1Second = new ObjectStep(this, ObjectID.ROPE_BRIDGE_21310, new WorldPoint(2314, 3840, 0),
			"Right-click repair the bridges to the north of Neitiznot. Protect from Missiles before doing this as " +
				"you'll automatically cross the aggressive trolls. Protect from Missiles before doing this as you'll " +
				"automatically cross the aggressive trolls.", splitLogs4, rope4, knife, protectRanged);
		repairBridge2 = new ObjectStep(this, ObjectID.ROPE_BRIDGE_21312, new WorldPoint(2355, 3840, 0),
			"Right-click repair the bridges to the north of Neitiznot.", splitLogs4, rope4, knife, protectRanged);
		repairBridge1.addSubSteps(repairBridge1Second, repairBridge2);

		talkToMawnisAfterRepair = new NpcStep(this, NpcID.MAWNIS_BUROWGAR, new WorldPoint(2335, 3800, 0), "Report back to Mawnis.");

		talkToGjukiToReport = new NpcStep(this, NpcID.KING_GJUKI_SORVOTT_IV, new WorldPoint(2407, 3804, 0), "Report back to King Gjuki Sorvott IV on Jatizso.");
		travelToJatizsoToReport = new NpcStep(this, NpcID.MORD_GUNNARS, new WorldPoint(2644, 3709, 0), "Travel to Jatizso with Mord.");
		leaveNeitiznotToReport = new NpcStep(this, NpcID.MARIA_GUNNARS, new WorldPoint(2311, 3781, 0), "Travel back to Rellekka with Maria.");

		talkToGjukiToReport.addSubSteps(travelToJatizsoToReport, leaveNeitiznotToReport);

		collectFromHring = new NpcStep(this, NpcID.HRING_HRING, new WorldPoint(2397, 3797, 0), "Collect 8000 coins from Hring Hring in south west Jatizso.");
		collectFromHring.addDialogStep("But rules are rules. Pay up!");
		collectFromSkuli = new NpcStep(this, NpcID.SKULI_MYRKA, new WorldPoint(2395, 3804, 0), "Collect 6000 coins from Skuli in north west Jatizso.");
		collectFromSkuli.addDialogStep("But, rules are rules. Pay up!");
		collectFromVanligga = new NpcStep(this, NpcID.VANLIGGA_GASTFRIHET, new WorldPoint(2405, 3813, 0), "Collect 5000 coins from Vanligga north of Gjuki's building.");
		collectFromVanligga.addDialogStep("But, rules are rules. Pay up!");
		collectFromKeepa = new NpcStep(this, NpcID.KEEPA_KETTILON, new WorldPoint(2417, 3816, 0), "Collect 5000 coins from Keepa in north east Jatizso.");
		collectFromKeepa.addDialogStep("But rules are rules. Pay up!");
		talkToGjukiAfterCollection1 = new NpcStep(this, NpcID.KING_GJUKI_SORVOTT_IV, new WorldPoint(2407, 3804, 0), "Report back to King Gjuki Sorvott IV on Jatizso.");
		collectFromHringAgain = new NpcStep(this, NpcID.HRING_HRING, new WorldPoint(2397, 3797, 0), "Collect tax from Hring Hring in south west Jatizso.");
		collectFromHringAgain.addDialogStep("But rules are rules. Pay up!");
		collectFromRaum = new NpcStep(this, NpcID.RAUM_URDASTEIN, new WorldPoint(2395, 3797, 0), "Collect tax from Raum in south west Jatizso.");
		collectFromRaum.addDialogStep("But rules are rules. Pay up!");
		collectFromSkuliAgain = new NpcStep(this, NpcID.SKULI_MYRKA, new WorldPoint(2395, 3804, 0), "Collect tax from Skuli in north west Jatizso.");
		collectFromSkuliAgain.addDialogStep("But rules are rules. Pay up!");
		collectFromKeepaAgain = new NpcStep(this, NpcID.KEEPA_KETTILON, new WorldPoint(2417, 3816, 0), "Collect tax from Keepa in north east Jatizso.");
		collectFromKeepaAgain.addDialogStep("But rules are rules. Pay up!");
		collectFromFlosi = new NpcStep(this, NpcID.FLOSI_DALKSSON, new WorldPoint(2418, 3813, 0), "Collect tax from Flossi in north east Jatizso.");
		collectFromFlosi.addDialogStep("But rules are rules. Pay up!");
		talkToGjukiAfterCollection2 = new NpcStep(this, NpcID.KING_GJUKI_SORVOTT_IV, new WorldPoint(2407, 3804, 0), "Report back to King Gjuki Sorvott IV on Jatizso.");

		travelToNeitiznotToSpyAgain = new NpcStep(this, NpcID.MARIA_GUNNARS_1883, new WorldPoint(2644, 3710, 0), "Travel to Neitiznot with Maria Gunnars.");
		returnToRellekkaFromJatizsoToSpyAgain = new NpcStep(this, NpcID.MORD_GUNNARS_1940, new WorldPoint(2420, 3781, 0), "Return to Rellekka with Mord.");
		returnToRellekkaFromJatizsoToSpyAgain.addDialogStep("Can you ferry me to Rellekka?");
		talkToSlugToSpyAgain = new NpcStep(this, NpcID.SLUG_HEMLIGSSEN, new WorldPoint(2335, 3811, 0), "Talk to Slug Hemligssen wearing nothing but your Silly Jester outfit.", jesterHat, jesterTop, jesterTights, jesterBoots);
		talkToSlugToSpyAgain.addSubSteps(travelToNeitiznotToSpyAgain, returnToRellekkaFromJatizsoToSpyAgain);

		performForMawnisAgain = new DetailedQuestStep(this, "Perform the actions that Mawnis requests of you.");

		goSpyOnMawnisAgain = new NpcStep(this, NpcID.MAWNIS_BUROWGAR, new WorldPoint(2335, 3800, 0), "Talk to Mawnis to start spying on him.", jesterHat, jesterTop, jesterTights, jesterBoots);
		goSpyOnMawnisAgain.addSubSteps(performForMawnisAgain);

		reportBackToSlugAgain = new NpcStep(this, NpcID.SLUG_HEMLIGSSEN, new WorldPoint(2335, 3811, 0), "Report to Slug Hemligssen.");
		reportBackToSlugAgain.addDialogSteps("Yes, I am.", "They are in a secluded bay, near Etceteria.", "They will be given some potions.", "I have been helping Neitiznot.");
		returnToRellekkaFromNeitiznotAfterSpy2 = new NpcStep(this, NpcID.MARIA_GUNNARS, new WorldPoint(2311, 3781, 0), "Travel back to Rellekka with Maria.");
		travelToJatizsoAfterSpy2 = new NpcStep(this, NpcID.MORD_GUNNARS, new WorldPoint(2644, 3709, 0), "Travel to Jatizso with Mord.");
		talkToGjukiAfterSpy2 = new NpcStep(this, NpcID.KING_GJUKI_SORVOTT_IV, new WorldPoint(2407, 3804, 0), "Report back to King Gjuki Sorvott IV on Jatizso.");
		talkToGjukiAfterSpy2.addSubSteps(returnToRellekkaFromNeitiznotAfterSpy2, travelToJatizsoAfterSpy2);

		returnToRellekkaFromJatizsoWithDecree = new NpcStep(this, NpcID.MORD_GUNNARS_1940, new WorldPoint(2420, 3781, 0), "Return to Rellekka with Mord.", royalDecree);
		returnToRellekkaFromJatizsoWithDecree.addDialogStep("Can you ferry me to Rellekka?");

		returnToRellekkaFromJatizsoAfterDecree = new NpcStep(this, NpcID.MORD_GUNNARS_1940, new WorldPoint(2420, 3781, 0), "Return to Rellekka with Mord.");
		returnToRellekkaFromJatizsoAfterDecree.addDialogStep("Can you ferry me to Rellekka?");

		travelToNeitiznotWithDecree = new NpcStep(this, NpcID.MARIA_GUNNARS_1883, new WorldPoint(2644, 3710, 0), "Travel to Neitiznot with Maria Gunnars.", royalDecree);
		travelToNeitiznotAfterDecree = new NpcStep(this, NpcID.MARIA_GUNNARS_1883, new WorldPoint(2644, 3710, 0), "Travel to Neitiznot with Maria Gunnars.");

		talkToMawnisWithDecree = new NpcStep(this, NpcID.MAWNIS_BUROWGAR, new WorldPoint(2335, 3800, 0), "Take off your jester outfit, and talk to Mawnis.", royalDecree);
		talkToMawnisAfterDecree = new NpcStep(this, NpcID.MAWNIS_BUROWGAR, new WorldPoint(2335, 3800, 0), "Take off your jester outfit, and talk to Mawnis.");
		talkToMawnisWithDecree.addSubSteps(talkToMawnisAfterDecree, returnToRellekkaFromJatizsoAfterDecree, returnToRellekkaFromJatizsoWithDecree, travelToNeitiznotWithDecree, travelToNeitiznotAfterDecree);

		getYakArmour = new NpcStep(this, NpcID.MAWNIS_BUROWGAR, new WorldPoint(2335, 3800, 0), "Talk to Mawnis with full yak-hide armour.", yakTop, yakBottom);
		makeShield = new NpcStep(this, NpcID.MAWNIS_BUROWGAR, new WorldPoint(2335, 3800, 0), "Talk to Mawnis with a Neitiznot Shield.", roundShield);

		enterCave = new ObjectStep(this, ObjectID.CAVE_21584, new WorldPoint(2402, 3890, 0),
			"Enter the cave in the north east of the islands. Be prepared in your yak armour, Neitiznot shield, and a melee weapon.", yakTopWorn, yakBottomWorn, shieldWorn, meleeWeapon, food);

		killTrolls = new KillTrolls(this);
		enterKingRoom = new ObjectStep(this, ObjectID.ROPE_BRIDGE_21316, new WorldPoint(2385, 10263, 1), "Cross the rope bridge. Be prepared to fight the Ice Troll King. Use the Protect from Magic prayer for the fight.");
		killKing = new NpcStep(this, NpcID.ICE_TROLL_KING, new WorldPoint(2386, 10249, 1), "Kill the king. Use the Protect from Magic prayer for the fight.");
		decapitateKing = new ObjectStep(this, ObjectID.ICE_TROLL_KING, "Decapitate the king's head.");
		finishQuest = new NpcStep(this, NpcID.MAWNIS_BUROWGAR, new WorldPoint(2335, 3800, 0), "Talk to Mawnis with the Ice Troll King's head to complete the quest.", head);
		finishQuestGivenHead = new NpcStep(this, NpcID.MAWNIS_BUROWGAR, new WorldPoint(2335, 3800, 0), "Talk to Mawnis to complete the quest.");
		finishQuest.addSubSteps(finishQuestGivenHead);
	}

	public void setupPanels()
	{
		if (client.getAccountType().isIronman())
		{
			prepareForRepairPanel = new PanelDetails("Helping Mawnis", Arrays.asList(talkToMawnis, talkToMawnisWithLogs, repairBridge1, talkToMawnisAfterRepair), rope8, axe, knife);
			prepareForCombatPanel = new PanelDetails("Preparing to fight", Arrays.asList(getYakArmour, makeShield), needle, thread, coins15, bronzeNail, hammer, rope);
			items = Arrays.asList(tuna, ores, rope9, knife, axe, hammer, bronzeNail, needle, thread, meleeWeapon, food);
		}
		else
		{
			prepareForCombatPanel = new PanelDetails("Preparing to fight", Arrays.asList(getYakArmour, makeShield), yakBottom, yakTop, roundShield);
			prepareForRepairPanel = new PanelDetails("Helping Mawnis", Arrays.asList(talkToMawnis,
				talkToMawnisWithLogs, repairBridge1, talkToMawnisAfterRepair), rope8, splitLogs8, knife);
			items = Arrays.asList(tuna, ores, rope9, knife, splitLogs8, roundShield, yakTop, yakBottom, meleeWeapon, food);
		}
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return items;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("10 Ice Trolls (level 74-82)", "Ice Troll King (level 122)");
	}


	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.THE_FREMENNIK_TRIALS, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.AGILITY, 40, true));
		req.add(new SkillRequirement(Skill.CONSTRUCTION, 20, true));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.CONSTRUCTION, 5000),
				new ExperienceReward(Skill.CRAFTING, 5000),
				new ExperienceReward(Skill.WOODCUTTING, 10000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("10,000 Exp. Lamp (Combat)", ItemID.ANTIQUE_LAMP, 2),
				new ItemReward("Helm of Neitiznot", ItemID.HELM_OF_NEITIZNOT, 1),
				new ItemReward("Jester Outfit", ItemID.JESTER, 1));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Travel to Jatizso", Arrays.asList(talkToMord, travelToJatizso), tuna, ores));
		allSteps.add(new PanelDetails("Helping Gjuki", Arrays.asList(talkToGjuki, bringOreToGjuki, getJesterOutfit)));
		allSteps.add(new PanelDetails("Spy on Mawnis", Arrays.asList(talkToSlug, goSpyOnMawnis, tellSlugReport1)));
		allSteps.add(prepareForRepairPanel);
		allSteps.add(new PanelDetails("Collecting window tax", Arrays.asList(talkToGjukiToReport, collectFromKeepa, collectFromVanligga, collectFromSkuli, collectFromHring, talkToGjukiAfterCollection1)));
		allSteps.add(new PanelDetails("Collecting beard tax", Arrays.asList(collectFromHringAgain, collectFromRaum, collectFromSkuliAgain, collectFromKeepaAgain, collectFromFlosi, talkToGjukiAfterCollection2)));
		allSteps.add(new PanelDetails("Spy on Mawnis again", Arrays.asList(talkToSlugToSpyAgain, goSpyOnMawnisAgain, reportBackToSlugAgain, talkToGjukiAfterSpy2, talkToMawnisWithDecree)));
		allSteps.add(prepareForCombatPanel);
		allSteps.add(new PanelDetails("Killing the king", Arrays.asList(enterCave, killTrolls, enterKingRoom, killKing, decapitateKing, finishQuest), yakBottom, yakTop, roundShield, meleeWeapon, food));

		return allSteps;
	}
}
