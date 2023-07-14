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
package com.questhelper.helpers.achievementdiaries.desert;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.IronmanRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.TileStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.DESERT_MEDIUM
)
public class DesertMedium extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement combatGear, coins, rope, smallFishingNet, axe, lightSource, scrollOfRedir, teleToHouse, harraPot,
		goatHornDust, camulet, iceCooler;

	// Items recommended
	ItemRequirement food, desertBoots, desertRobe, desertShirt, waterskin;

	// Quests required
	Requirement theGolem, eaglesPeak, spiritsOfTheElid, enakhrasLament;

	Requirement notAgiPyramid, notDesertLizard, notOrangeSally, notPhoenixFeather, notMagicCarpet, notEagleTravel,
		notPrayElidinis, notCombatPot, notTPEnakhra, notIronman, notVisitGenie, notTPPollnivneach, notChopTeak, talkedToSimon;

	QuestStep claimReward, agiPyramid, orangeSally, phoenixFeather, magicCarpet, eagleTravel, prayElidinis,
		combatPot, tpEnakhra, visitGenie, tpPollnivneach, chopTeak, moveToEagle, moveToDesert, moveToGenie,
		moveToPyramid, talkToSimon;

	NpcStep desertLizard;

	Zone desert, eagleArea, genie, pyramid;

	ZoneRequirement inDesert, inEagleArea, inGenie, inPyramid;

	ConditionalStep agiPyramidTask, desertLizardTask, orangeSallyTask, phoenixFeatherTask, magicCarpetTask,
		eagleTravelTask, prayElidinisTask, combatPotTask, tpEnakhraTask, visitGenieTask, tpPollnivneachTask,
		chopTeakTask;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doMedium = new ConditionalStep(this, claimReward);

		combatPotTask = new ConditionalStep(this, moveToDesert);
		combatPotTask.addStep(inDesert, combatPot);
		doMedium.addStep(notCombatPot, combatPotTask);

		phoenixFeatherTask = new ConditionalStep(this, phoenixFeather);
		doMedium.addStep(notPhoenixFeather, phoenixFeatherTask);

		orangeSallyTask = new ConditionalStep(this, orangeSally);
		doMedium.addStep(notOrangeSally, orangeSallyTask);

		magicCarpetTask = new ConditionalStep(this, magicCarpet);
		doMedium.addStep(notMagicCarpet, magicCarpetTask);

		chopTeakTask = new ConditionalStep(this, chopTeak);
		doMedium.addStep(notChopTeak, chopTeakTask);

		desertLizardTask = new ConditionalStep(this, desertLizard);
		doMedium.addStep(notDesertLizard, desertLizardTask);

		prayElidinisTask = new ConditionalStep(this, prayElidinis);
		doMedium.addStep(notPrayElidinis, prayElidinisTask);

		visitGenieTask = new ConditionalStep(this, moveToGenie);
		visitGenieTask.addStep(inGenie, visitGenie);
		doMedium.addStep(notVisitGenie, visitGenieTask);

		agiPyramidTask = new ConditionalStep(this, moveToPyramid);
		agiPyramidTask.addStep(inPyramid, talkToSimon);
		agiPyramidTask.addStep(new Conditions(inPyramid, talkedToSimon), agiPyramid);
		doMedium.addStep(notAgiPyramid, agiPyramidTask);

		tpEnakhraTask = new ConditionalStep(this, tpEnakhra);
		doMedium.addStep(notTPEnakhra, tpEnakhraTask);

		eagleTravelTask = new ConditionalStep(this, moveToEagle);
		eagleTravelTask.addStep(inEagleArea, eagleTravel);
		doMedium.addStep(notEagleTravel, eagleTravelTask);

		tpPollnivneachTask = new ConditionalStep(this, tpPollnivneach);
		doMedium.addStep(notTPPollnivneach, tpPollnivneachTask);

		return doMedium;
	}

	@Override
	public void setupRequirements()
	{
		notAgiPyramid = new VarplayerRequirement(1198, false, 12);
		notDesertLizard = new VarplayerRequirement(1198, false, 13);
		notOrangeSally = new VarplayerRequirement(1198, false, 14);
		notPhoenixFeather = new VarplayerRequirement(1198, false, 15);
		notMagicCarpet = new VarplayerRequirement(1198, false, 16);
		notEagleTravel = new VarplayerRequirement(1198, false, 17);
		notPrayElidinis = new VarplayerRequirement(1198, false, 18);
		notCombatPot = new VarplayerRequirement(1198, false, 19);
		notTPEnakhra = new VarplayerRequirement(1198, false, 20);
		notVisitGenie = new VarplayerRequirement(1198, false, 21);
		notTPPollnivneach = new VarplayerRequirement(1198, false, 22);// iron varb different
		notChopTeak = new VarplayerRequirement(1198, false, 23);
		notIronman = new IronmanRequirement(false);

		// 1557 0->1 talking to simon
		// 1558 0->1 talking to simon
		talkedToSimon = new VarbitRequirement(1558, 1, Operation.EQUAL);

		coins = new ItemRequirement("Coins", ItemCollections.COINS).showConditioned(notMagicCarpet);
		rope = new ItemRequirement("Rope", ItemID.ROPE).showConditioned(new Conditions(LogicType.OR, notOrangeSally,
			notEagleTravel, notVisitGenie)).isNotConsumed();
		smallFishingNet = new ItemRequirement("Small fishing net", ItemID.SMALL_FISHING_NET).showConditioned(notOrangeSally).isNotConsumed();
		axe = new ItemRequirement("Any axe", ItemCollections.AXES).showConditioned(notChopTeak).isNotConsumed();
		lightSource = new ItemRequirement("Light source", ItemCollections.LIGHT_SOURCES)
			.showConditioned(notVisitGenie).isNotConsumed();
		scrollOfRedir = new ItemRequirement("Scroll of redirection", ItemID.SCROLL_OF_REDIRECTION)
			.showConditioned(new Conditions(notTPPollnivneach, notIronman));
		teleToHouse = new ItemRequirement("Teleport to house", ItemID.TELEPORT_TO_HOUSE)
			.showConditioned(new Conditions(notTPPollnivneach, notIronman));
		harraPot = new ItemRequirement("Harralander potion (unf)", ItemID.HARRALANDER_POTION_UNF)
			.showConditioned(notCombatPot);
		goatHornDust = new ItemRequirement("Goat horn dust", ItemID.GOAT_HORN_DUST).showConditioned(notCombatPot);
		camulet = new ItemRequirement("Camulet", ItemID.CAMULET).showConditioned(notTPEnakhra);
		iceCooler = new ItemRequirement("Ice cooler (bring multiple just in case)", ItemID.ICE_COOLER).showConditioned(notDesertLizard);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		desertBoots = new ItemRequirement("Desert boots", ItemID.DESERT_BOOTS).isNotConsumed();
		desertRobe = new ItemRequirement("Desert robe", ItemID.DESERT_ROBE).isNotConsumed();
		desertShirt = new ItemRequirement("Desert shirt", ItemID.DESERT_SHIRT).isNotConsumed();
		waterskin = new ItemRequirement("Waterskin", ItemCollections.WATERSKIN).isNotConsumed();

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);

		inDesert = new ZoneRequirement(desert);
		inEagleArea = new ZoneRequirement(eagleArea);
		inGenie = new ZoneRequirement(genie);
		inPyramid = new ZoneRequirement(pyramid);

		theGolem = new QuestRequirement(QuestHelperQuest.THE_GOLEM, QuestState.FINISHED);
		eaglesPeak = new QuestRequirement(QuestHelperQuest.EAGLES_PEAK, QuestState.FINISHED);
		spiritsOfTheElid = new QuestRequirement(QuestHelperQuest.SPIRITS_OF_THE_ELID, QuestState.FINISHED);
		enakhrasLament = new QuestRequirement(QuestHelperQuest.ENAKHRAS_LAMENT, QuestState.FINISHED);
	}

	public void loadZones()
	{
		desert = new Zone(new WorldPoint(3127, 3115, 0), new WorldPoint(3519, 2749, 0));
		eagleArea = new Zone(new WorldPoint(1986, 4985, 3), new WorldPoint(2030, 4944, 3));
		genie = new Zone(new WorldPoint(3367, 9324, 0), new WorldPoint(3378, 9298, 0));
		pyramid = new Zone(new WorldPoint(3334, 2861, 0), new WorldPoint(3384, 2820, 0));
	}

	public void setupSteps()
	{
		moveToEagle = new ObjectStep(this, 19790, new WorldPoint(2329, 3495, 0),
			"Enter the cave at the top of Eagles' Peak. You can use a fairy ring to (AKQ), then head " +
				"south to get there easily.");
		eagleTravel = new NpcStep(this, NpcID.DESERT_EAGLE, new WorldPoint(2027, 4964, 3),
			"Use a rope on the Desert Eagle to travel to the Desert area.", rope.highlighted());

		magicCarpet = new NpcStep(this, NpcID.RUG_MERCHANT, new WorldPoint(3310, 3108, 0),
			"Talk to the rug merchant and travel to Uzer.", coins.quantity(200));
		magicCarpet.addDialogSteps("Yes please.", "I want to travel to Uzer.", "Uzer");

		phoenixFeather = new NpcStep(this, NpcID.DESERT_PHOENIX, new WorldPoint(3417, 3154, 0),
			"Pluck a feather from a Desert Phoenix.");

		orangeSally = new ObjectStep(this, ObjectID.YOUNG_TREE_8732, new WorldPoint(3404, 3134, 0),
			"Setup a net trap and catch an Orange Salamander in the Uzer hunting area.", rope, smallFishingNet);

		combatPot = new ItemStep(this, "Create a combat potion in the desert. Note: Do not be within a city.",
			harraPot.highlighted(), goatHornDust.highlighted());
		moveToDesert = new TileStep(this, new WorldPoint(3305, 3112, 0),
			"Enter the desert and be out of any city limits (You must be losing health or water from the heat).");

		chopTeak = new ObjectStep(this, ObjectID.TEAK_TREE, new WorldPoint(3510, 3073, 0),
			"Chop some teak logs near Uzer.", axe);

		desertLizard = new NpcStep(this, NpcID.SMALL_LIZARD, new WorldPoint(3437, 3067, 0),
			"Use an Ice cooler on a low hp Lizard in the desert.", iceCooler, combatGear);

		prayElidinis = new ObjectStep(this, ObjectID.ELIDINIS_STATUETTE, new WorldPoint(3427, 2930, 0),
			"Pray at the Elidinis Statuette in Nardah. If it doesn't complete expend some prayer points then try again.");

		visitGenie = new NpcStep(this, NpcID.GENIE_4738, new WorldPoint(3371, 9320, 0),
			"Visit the genie.");
		moveToGenie = new ObjectStep(this, 10478, new WorldPoint(3374, 2904, 0),
			"Climb down the crevice west of Nardah.", rope, lightSource);

		talkToSimon = new NpcStep(this, NpcID.SIMON_TEMPLETON, new WorldPoint(3346, 2827, 0),
			"Talk to Simon Templeton.");
		moveToPyramid = new ObjectStep(this, ObjectID.CLIMBING_ROCKS_11948, new WorldPoint(3335, 2829, 0),
			"Go to the Agility Pyramid.");
		agiPyramid = new ObjectStep(this, ObjectID.STAIRS_10857, new WorldPoint(3355, 2832, 0),
			"Climb the Agility Pyramid and collect the pyramid top. Be sure to click continue in the dialog.");

		tpEnakhra = new DetailedQuestStep(this, "Teleport to Enakhra's Temple with the Camulet.", camulet);

		if (client.getAccountType().isIronman() || client.getAccountType().isGroupIronman())
		{
			tpPollnivneach = new DetailedQuestStep(this, "Move your house to Pollnivneach, then enter your house " +
				"there.", coins.quantity(7500));
		}
		else
		{
			tpPollnivneach = new DetailedQuestStep(this, "Teleport to Pollnivneach with a redirected house tablet.", scrollOfRedir, teleToHouse);
		}

		claimReward = new NpcStep(this, NpcID.JARR, new WorldPoint(3303, 3124, 0),
			"Talk to Jarr at the Shantay pass to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear, coins.quantity(200), rope, smallFishingNet, axe, lightSource, scrollOfRedir,
			teleToHouse, harraPot, goatHornDust, camulet, iceCooler);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, desertBoots, desertRobe, desertShirt, waterskin);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.AGILITY, 30));
		reqs.add(new SkillRequirement(Skill.CONSTRUCTION, 20));
		reqs.add(new SkillRequirement(Skill.HERBLORE, 36));
		reqs.add(new SkillRequirement(Skill.HUNTER, 47));
		reqs.add(new SkillRequirement(Skill.SLAYER, 22));
		reqs.add(new SkillRequirement(Skill.THIEVING, 25));
		reqs.add(new SkillRequirement(Skill.WOODCUTTING, 35));

		reqs.add(theGolem);
		reqs.add(eaglesPeak);
		reqs.add(spiritsOfTheElid);
		reqs.add(enakhrasLament);

		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Kill a Desert lizard (lvl 24)");
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Desert amulet 2", ItemID.DESERT_AMULET_2, 1),
			new ItemReward("7,500 Exp. Lamp (Any skill over 40)", ItemID.ANTIQUE_LAMP, 1)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Pharaoh's sceptre can hold up to 25 charges"),
			new UnlockReward("One teleport to Nardah per day on desert amulet")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails combatPotionSteps = new PanelDetails("Combat Potion", Arrays.asList(moveToDesert, combatPot),
			new SkillRequirement(Skill.HERBLORE, 36), harraPot, goatHornDust);
		combatPotionSteps.setDisplayCondition(notCombatPot);
		combatPotionSteps.setLockingStep(combatPotTask);
		allSteps.add(combatPotionSteps);

		PanelDetails phoenixFeatherSteps = new PanelDetails("Phoenix Feather",
			Collections.singletonList(phoenixFeather), new SkillRequirement(Skill.THIEVING, 25));
		phoenixFeatherSteps.setDisplayCondition(notPhoenixFeather);
		phoenixFeatherSteps.setLockingStep(phoenixFeatherTask);
		allSteps.add(phoenixFeatherSteps);

		PanelDetails orangeSalamanderSteps = new PanelDetails("Orange Salamander",
			Collections.singletonList(orangeSally), new SkillRequirement(Skill.HUNTER, 47), rope, smallFishingNet);
		orangeSalamanderSteps.setDisplayCondition(notOrangeSally);
		orangeSalamanderSteps.setLockingStep(orangeSallyTask);
		allSteps.add(orangeSalamanderSteps);

		PanelDetails magicCarpetSteps = new PanelDetails("Magic Carpet to Uzer",
			Collections.singletonList(magicCarpet), theGolem, coins.quantity(200));
		magicCarpetSteps.setDisplayCondition(notMagicCarpet);
		magicCarpetSteps.setLockingStep(magicCarpetTask);
		allSteps.add(magicCarpetSteps);

		PanelDetails chopTeakAtUzerSteps = new PanelDetails("Chop Teak at Uzer", Collections.singletonList(chopTeak),
			new SkillRequirement(Skill.WOODCUTTING, 35), axe);
		chopTeakAtUzerSteps.setDisplayCondition(notChopTeak);
		chopTeakAtUzerSteps.setLockingStep(chopTeakTask);
		allSteps.add(chopTeakAtUzerSteps);

		PanelDetails desertLizardSteps = new PanelDetails("Desert Lizard", Collections.singletonList(desertLizard),
			combatGear, iceCooler);
		desertLizardSteps.setDisplayCondition(notDesertLizard);
		desertLizardSteps.setLockingStep(desertLizardTask);
		allSteps.add(desertLizardSteps);

		PanelDetails elidinisStatuetteSteps = new PanelDetails("Pray at Elidinis Statuette",
			Collections.singletonList(prayElidinis), spiritsOfTheElid);
		elidinisStatuetteSteps.setDisplayCondition(notPrayElidinis);
		elidinisStatuetteSteps.setLockingStep(prayElidinisTask);
		allSteps.add(elidinisStatuetteSteps);

		PanelDetails visitTheGenieSteps = new PanelDetails("Visit the Genie", Arrays.asList(moveToGenie, visitGenie),
			spiritsOfTheElid, rope, lightSource);
		visitTheGenieSteps.setDisplayCondition(notVisitGenie);
		visitTheGenieSteps.setLockingStep(visitGenieTask);
		allSteps.add(visitTheGenieSteps);

		PanelDetails agilityPyramidSteps = new PanelDetails("Agility Pyramid", Arrays.asList(moveToPyramid,
			talkToSimon, agiPyramid), new SkillRequirement(Skill.AGILITY, 30));
		agilityPyramidSteps.setDisplayCondition(notAgiPyramid);
		agilityPyramidSteps.setLockingStep(agiPyramidTask);
		allSteps.add(agilityPyramidSteps);

		PanelDetails tpEnakhraSteps = new PanelDetails("Camulet to Enakhra's Temple",
			Collections.singletonList(tpEnakhra), enakhrasLament, camulet);
		tpEnakhraSteps.setDisplayCondition(notTPEnakhra);
		tpEnakhraSteps.setLockingStep(tpEnakhraTask);
		allSteps.add(tpEnakhraSteps);

		PanelDetails eagleSteps = new PanelDetails("Eagle Travel to Desert", Arrays.asList(moveToEagle, eagleTravel),
			rope);
		eagleSteps.setDisplayCondition(notEagleTravel);
		eagleSteps.setLockingStep(eagleTravelTask);
		allSteps.add(eagleSteps);

		PanelDetails teleportToPollnivneachSteps = new PanelDetails("Pollnivneach House",
			Collections.singletonList(tpPollnivneach), new SkillRequirement(Skill.CONSTRUCTION, 20), teleToHouse,
			scrollOfRedir);
		teleportToPollnivneachSteps.setDisplayCondition(notTPPollnivneach);
		teleportToPollnivneachSteps.setLockingStep(tpPollnivneachTask);
		allSteps.add(teleportToPollnivneachSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
