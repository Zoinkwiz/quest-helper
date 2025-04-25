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
package com.questhelper.helpers.quests.recipefordisaster;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.questinfo.QuestVarPlayer;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.quest.QuestPointRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import java.util.*;

public class RFDSirAmikVarze extends BasicQuestHelper
{
	ItemRequirement combatGear, bucketOfMilk, potOfCream, cornflour, pot, sweetcorn, axe, macheteAndRadimus, machete,
		vanillaPod, dramenStaffOrLunar, dramenBranch, pestleAndMortar, iceGloves, rawChicken, antidragonShield,
		antifirePotion, radimusNotes, bruleeWithEgg, baseBrulee, uncookedBrulee, finishedBrulee, finishedBruleeHighlighted,
		milkyMixture, cornflourMixture, evilEgg, token, cinnamon, pestleAndMortarHighlighted, tokenHighlighted;
	ItemRequirement draynorVillageTele, lumbridgeTele;

	Requirement inDiningRoom, talkedToWom, inEvilChickenLair, inZanaris, inDraynorVillage, hasEggAndToken, tokenNearby, eggNearby;

	QuestStep enterDiningRoom, inspectAmik, enterKitchen, talkToCook, enterDiningRoomAgain, useBruleeOnVarze, talkToWom, useMilkOnCream,
		useCornflourOnMilky, addPodToCornflourMixture, enterZanaris, useChickenOnShrine, killEvilChicken, pickUpEgg, useEggOnBrulee,
		killBlackDragon, pickUpToken, grindBranch, useCinnamonOnBrulee, rubToken;

	ConditionalStep tokenAndEggSteps;

	//Zones
	Zone diningRoom, zanaris, evilChickenLair, draynorVillage;

	int evilChickenLevel = 19;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		calculateEvilChickenLevel();

		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep goInspectPete = new ConditionalStep(this, enterDiningRoom);
		goInspectPete.addStep(inDiningRoom, inspectAmik);
		steps.put(0, goInspectPete);

		ConditionalStep goAskCook = new ConditionalStep(this, talkToCook);
		goAskCook.addStep(inDiningRoom, enterKitchen);
		steps.put(5, goAskCook);

		tokenAndEggSteps = new ConditionalStep(this, enterZanaris);
		tokenAndEggSteps.addStep(tokenNearby, pickUpToken);
		tokenAndEggSteps.addStep(new Conditions(evilEgg.alsoCheckBank(questBank), inEvilChickenLair), killBlackDragon);
		tokenAndEggSteps.addStep(eggNearby, pickUpEgg);
		tokenAndEggSteps.addStep(inEvilChickenLair, killEvilChicken);
		tokenAndEggSteps.addStep(inZanaris, useChickenOnShrine);
		tokenAndEggSteps.setLockingCondition(hasEggAndToken);
		tokenAndEggSteps.setBlocker(true);

		ConditionalStep makeBrulee = new ConditionalStep(this, useMilkOnCream);
		makeBrulee.addStep(new Conditions(uncookedBrulee), rubToken);
		makeBrulee.addStep(new Conditions(bruleeWithEgg, cinnamon), useCinnamonOnBrulee);
		makeBrulee.addStep(bruleeWithEgg, grindBranch);
		makeBrulee.addStep(baseBrulee, useEggOnBrulee);
		makeBrulee.addStep(cornflourMixture, addPodToCornflourMixture);
		makeBrulee.addStep(milkyMixture, useCornflourOnMilky);

		ConditionalStep saveAmik = new ConditionalStep(this, talkToWom);
		saveAmik.addStep(new Conditions(inDiningRoom, finishedBrulee), useBruleeOnVarze);
		saveAmik.addStep(finishedBrulee.alsoCheckBank(questBank), enterDiningRoomAgain);
		saveAmik.addStep(hasEggAndToken, makeBrulee);
		saveAmik.addStep(talkedToWom, tokenAndEggSteps);
		steps.put(10, saveAmik);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		bucketOfMilk = new ItemRequirement("Bucket of milk", ItemID.BUCKET_MILK);
		bucketOfMilk.setHighlightInInventory(true);
		potOfCream = new ItemRequirement("Pot of cream", ItemID.POT_OF_CREAM);
		potOfCream.setHighlightInInventory(true);
		cornflour = new ItemRequirement("Pot of cornflour", ItemID.CHICKENQUEST_POT_CORNFLOUR);
		cornflour.setTooltip("You can make this by grinding a sweetcorn in a windmill and collecting it with a pot");
		cornflour.setHighlightInInventory(true);

		pot = new ItemRequirement("Pot", ItemID.POT_EMPTY);
		sweetcorn = new ItemRequirement("Sweetcorn", ItemID.SWEETCORN);

		axe = new ItemRequirement("Any axe", ItemCollections.AXES).isNotConsumed();
		machete = new ItemRequirement("Any machete", ItemCollections.MACHETE).isNotConsumed();
		radimusNotes = new ItemRequirement("Radimus notes", ItemID.THKARAMJAMAP).isNotConsumed();
		radimusNotes.addAlternates(ItemID.THKARAMJAMAPCOMP);
		if (client.getGameState() == GameState.LOGGED_IN && QuestHelperQuest.LEGENDS_QUEST.getState(client, configManager) == QuestState.FINISHED)
		{
			macheteAndRadimus = machete;
		}
		else
		{
			macheteAndRadimus = new ItemRequirements("Machete and Radimus notes",
				machete, radimusNotes);
		}
		vanillaPod = new ItemRequirement("Vanilla pod", ItemID.CHICKENQUEST_VANILLA_POD);
		vanillaPod.setTooltip("You can get a pod from the Kharazi Jungle. Bring an axe and machete to get in");
		vanillaPod.setHighlightInInventory(true);

		dramenStaffOrLunar = new ItemRequirement("Dramen/lunar staff", ItemID.DRAMEN_STAFF, 1, true).isNotConsumed();
		dramenStaffOrLunar.addAlternates(ItemID.LUNAR_MOONCLAN_LIMINAL_STAFF);
		dramenStaffOrLunar.setDisplayMatchedItemName(true);

		dramenBranch = new ItemRequirement("Dramen branch", ItemID.DRAMEN_BRANCH);
		dramenBranch.setHighlightInInventory(true);
		dramenBranch.setTooltip("You can get one from the dramen tree under Entrana");
		pestleAndMortar = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR).isNotConsumed();
		pestleAndMortarHighlighted = pestleAndMortar.highlighted();

		iceGloves = new ItemRequirement("Ice gloves or smiths gloves(i)", ItemID.ICE_GLOVES).equipped().isNotConsumed();
		iceGloves.addAlternates(ItemID.SMITHING_UNIFORM_GLOVES_ICE);
		iceGloves.setTooltip("Although optional, you'll take a lot of damage if you're not wearing them");
		rawChicken = new ItemRequirement("Raw chicken", ItemID.RAW_CHICKEN);
		rawChicken.setHighlightInInventory(true);
		antidragonShield = new ItemRequirement("Anti-dragon shield", ItemCollections.ANTIFIRE_SHIELDS).isNotConsumed();
		antifirePotion = new ItemRequirement("Antifire potion", ItemCollections.ANTIFIRE);
		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		draynorVillageTele = new ItemRequirement("Draynor Village teleport", ItemCollections.AMULET_OF_GLORIES, 1);
		draynorVillageTele.setChargedItem(true);
		draynorVillageTele.showConditioned(new Conditions(LogicType.NOR, inDraynorVillage));
		lumbridgeTele = new ItemRequirement("Lumbridge Teleport", ItemID.POH_TABLET_LUMBRIDGETELEPORT, 1);

		milkyMixture = new ItemRequirement("Milky mixture", ItemID.CHICKENQUEST_MILKY_MIXTURE);
		milkyMixture.setHighlightInInventory(true);
		cornflourMixture = new ItemRequirement("Cornflour mixture", ItemID.CHICKENQUEST_CORNFLOUR_MILKY_MIXTURE);
		cornflourMixture.setHighlightInInventory(true);
		bruleeWithEgg = new ItemRequirement("Brulee", ItemID.CHICKENQUEST_BRULEE_MIXTURE_EGG);
		bruleeWithEgg.setHighlightInInventory(true);
		baseBrulee = new ItemRequirement("Brulee", ItemID.CHICKENQUEST_BRULEE_MIXTURE);
		baseBrulee.setHighlightInInventory(true);
		uncookedBrulee = new ItemRequirement("Brulee", ItemID.CHICKENQUEST_BRULEE_MIXTURE_CINAMON);
		finishedBrulee = new ItemRequirement("Brulee supreme", ItemID.CHICKENQUEST_BRULEE_MIXTURE_SUPREME);
		finishedBruleeHighlighted = new ItemRequirement("Brulee supreme", ItemID.CHICKENQUEST_BRULEE_MIXTURE_SUPREME);
		finishedBruleeHighlighted.setHighlightInInventory(true);

		cinnamon = new ItemRequirement("Cinnamon", ItemID.CHICKENQUEST_CINAMON);
		cinnamon.setHighlightInInventory(true);

		evilEgg = new ItemRequirement("Evil chickens egg", ItemID.CHICKENQUEST_EVIL_CHICKEN_EGG);
		evilEgg.addAlternates(ItemID.CHICKENQUEST_BRULEE_MIXTURE_EGG, ItemID.CHICKENQUEST_BRULEE_MIXTURE_CINAMON);
		evilEgg.setHighlightInInventory(true);

		token = new ItemRequirement("Dragon token", ItemID.CHICKENQUEST_DRAGON_COIN);
		token.addAlternates(ItemID.CHICKENQUEST_BRULEE_MIXTURE_SUPREME);

		tokenHighlighted = new ItemRequirement("Dragon token", ItemID.CHICKENQUEST_DRAGON_COIN);
		tokenHighlighted.setHighlightInInventory(true);

		hasEggAndToken = new Conditions(evilEgg, token);
		tokenNearby = new ItemOnTileRequirement(token);
		eggNearby = new ItemOnTileRequirement(evilEgg);
	}

	@Override
	protected void setupZones()
	{
		diningRoom = new Zone(new WorldPoint(1856, 5313, 0), new WorldPoint(1870, 5333, 0));
		zanaris = new Zone(new WorldPoint(2368, 4353, 0), new WorldPoint(2495, 4479, 0));
		evilChickenLair = new Zone(new WorldPoint(2430, 4355, 0), new WorldPoint(2492, 4407, 0));
		draynorVillage = new Zone(new WorldPoint(3060, 3221, 0), new WorldPoint(3121, 3283, 0));

		inDiningRoom = new ZoneRequirement(diningRoom);
		inEvilChickenLair = new ZoneRequirement(evilChickenLair);
		inDraynorVillage = new ZoneRequirement(draynorVillage);
		inZanaris = new ZoneRequirement(zanaris);
	}

	public void setupConditions()
	{
		// 1911 0->1->2->3->4->5->6 status of brulee
		// 1912 0->1 picked up token
		// 1913 0->1 evil chicken killed
		talkedToWom = new VarbitRequirement(VarbitID.CHICKENQUEST_WOM_WARNING, 1, Operation.GREATER_EQUAL);
		// 1919 = 2 when entered black dragon lair once
	}

	public void setupSteps()
	{
		enterDiningRoom = new ObjectStep(this, ObjectID.HUNDRED_LUMBRIDGE_DOUBLEDOORL, new WorldPoint(3213, 3221, 0), "Go inspect Sir Amik Varze in Lumbridge Castle.");
		inspectAmik = new ObjectStep(this, ObjectID.HUNDRED_VARZE_BASE, new WorldPoint(1865, 5321, 0), "Inspect Sir Amik Varze.");
		inspectAmik.addSubSteps(enterDiningRoom);

		enterKitchen = new ObjectStep(this, ObjectID.HUNDRED_PORTAL_DOOR1, new WorldPoint(1861, 5316, 0), "Talk to the Lumbridge Cook.");
		talkToCook = new NpcStep(this, NpcID.POH_SERVANT_COOK_WOMAN, new WorldPoint(3209, 3215, 0),
			"Talk to the Lumbridge Cook and ask him all the options about Sir Amik Varze.");
		talkToCook.addDialogSteps("More...", "Protecting Sir Amik Varze");
		talkToCook.addSubSteps(enterKitchen);

		talkToWom = new NpcStep(this, NpcID.WISE_OLD_MAN, new WorldPoint(3088, 3255, 0), "Talk to the Wise Old Man in Draynor Village about strange beasts and the Evil Chicken.");
		((NpcStep) talkToWom).addTeleport(draynorVillageTele.quantity(1).named("Amulet of glory (Draynor Village [3])"));
		talkToWom.addDialogSteps("Draynor Village", "I'd just like to ask you something.", "Strange beasts", "The Evil Chicken");

		useMilkOnCream = new DetailedQuestStep(this, "Use a bucket of milk on a pot of cream.", bucketOfMilk, potOfCream);
		useCornflourOnMilky = new DetailedQuestStep(this, "Use cornflour on the milky mixture.", cornflour, milkyMixture);
		addPodToCornflourMixture = new DetailedQuestStep(this, "Add a vanilla pod to the cornflour mixture.", vanillaPod, cornflourMixture);
		enterZanaris = new DetailedQuestStep(this, "Travel to Zanaris.", dramenStaffOrLunar);
		useChickenOnShrine = new ObjectStep(this, ObjectID.FAIRY_CHICKEN_SHRINE, new WorldPoint(2453, 4477, 0),
			"Use a raw chicken on the Chicken Shrine in the north east of Zanaris.", rawChicken, combatGear);
		useChickenOnShrine.addIcon(ItemID.RAW_CHICKEN);
		killEvilChicken = new NpcStep(this, NpcID.CHICKENQUEST_EVIL_CHICKEN, new WorldPoint(2455, 4399, 0), "Kill the Evil Chicken in the north of the range. Pray protect from magic against it.");
		pickUpEgg = new ItemStep(this, "Pick up the evil chicken egg.", evilEgg);
		useEggOnBrulee = new DetailedQuestStep(this, "Use the evil chickens egg on the brulee.", evilEgg, baseBrulee, pestleAndMortar);
		killBlackDragon = new NpcStep(this, NpcID.BLACK_DRAGON, new WorldPoint(2461, 4367, 0), "Kill a black dragon.", combatGear, antidragonShield);
		pickUpToken = new ItemStep(this, "Pick up the dragon token.", token);
		grindBranch = new DetailedQuestStep(this, "Use a pestle and mortar on the dramen branch.", pestleAndMortarHighlighted, dramenBranch);
		useCinnamonOnBrulee = new DetailedQuestStep(this, "Use the cinnamon on the brulee.", cinnamon, bruleeWithEgg);
		rubToken = new DetailedQuestStep(this, "Equip your Ice Gloves and rub the dragon token in Lumbridge or Zanaris. Ask the fairy dragon to cook your brulee.", tokenHighlighted, uncookedBrulee, iceGloves);
		rubToken.addDialogStep("Please flambe this creme brulee for me.");

		enterDiningRoomAgain = new ObjectStep(this, ObjectID.HUNDRED_LUMBRIDGE_DOOR, new WorldPoint(3207, 3217, 0), "Go give the Brulee to Sir Amik Varze to finish the quest.", finishedBrulee);
		((ObjectStep) enterDiningRoomAgain).addTeleport(lumbridgeTele);
		useBruleeOnVarze = new ObjectStep(this, ObjectID.HUNDRED_VARZE_BASE, new WorldPoint(1865, 5321, 0), "Give the Brulee to Sir Amik Varze to finish the quest.", finishedBruleeHighlighted);
		useBruleeOnVarze.addIcon(ItemID.CHICKENQUEST_BRULEE_MIXTURE_SUPREME);
		useBruleeOnVarze.addSubSteps(enterDiningRoomAgain);
	}

	private void calculateEvilChickenLevel()
	{
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return;
		}

		int combatLevel = player.getCombatLevel();
		if (combatLevel <= 10)
		{
			evilChickenLevel = 19;
		}
		else if (combatLevel <= 20)
		{
			evilChickenLevel = 38;
		}
		else if (combatLevel <= 40)
		{
			evilChickenLevel = 69;
		}
		else if (combatLevel <= 60)
		{
			evilChickenLevel = 81;
		}
		else if (combatLevel <= 90)
		{
			evilChickenLevel = 121;
		}
		else
		{
			evilChickenLevel = 159;
		}
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(axe, macheteAndRadimus, dramenStaffOrLunar, rawChicken, bucketOfMilk,
			potOfCream, cornflour, dramenBranch, vanillaPod, pestleAndMortar, iceGloves);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(combatGear, antidragonShield, antifirePotion, draynorVillageTele, lumbridgeTele);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Evil Chicken (level " + evilChickenLevel + ")", "Black dragon (level 227) (Can be safespotted)");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestPointRequirement(107));
		req.add(new VarplayerRequirement(QuestVarPlayer.QUEST_LEGENDS_QUEST.getId(), 1, Operation.GREATER_EQUAL,
			"Started Legends' Quest to access the Kharazi Jungle"));
		req.add(new QuestRequirement(QuestHelperQuest.FAMILY_CREST, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.HEROES_QUEST, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.SHILO_VILLAGE, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.UNDERGROUND_PASS, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.WATERFALL_QUEST, QuestState.FINISHED));
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
			new ExperienceReward(Skill.COOKING, 4000),
			new ExperienceReward(Skill.HITPOINTS, 4000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Access to the Evil Chickens Lair"),
			new UnlockReward("Further access to the Culinaromancer's Chest"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(inspectAmik, talkToCook, talkToWom), null, Arrays.asList(draynorVillageTele.quantity(1))));
		PanelDetails tokenAndEggPanel = new PanelDetails("Get token and egg", Arrays.asList(enterZanaris, useChickenOnShrine,
			killEvilChicken, pickUpEgg, killBlackDragon, pickUpToken),
			dramenStaffOrLunar, rawChicken, combatGear, antidragonShield, antifirePotion);
		tokenAndEggPanel.setLockingStep(tokenAndEggSteps);
		allSteps.add(tokenAndEggPanel);
		allSteps.add(new PanelDetails("Making the brulee", Arrays.asList(useMilkOnCream, useCornflourOnMilky, addPodToCornflourMixture,
			useEggOnBrulee, grindBranch, useCinnamonOnBrulee, rubToken, useBruleeOnVarze),
			Arrays.asList(bucketOfMilk, potOfCream, cornflourMixture, pestleAndMortar, dramenBranch, vanillaPod, evilEgg, token, iceGloves),
			Arrays.asList(lumbridgeTele)));

		return allSteps;
	}

	@Override
	public QuestState getState(Client client)
	{
		int questState = client.getVarbitValue(VarbitID.CHICKENQUEST);
		if (questState == 0)
		{
			return QuestState.NOT_STARTED;
		}

		if (questState < 20)
		{
			return QuestState.IN_PROGRESS;
		}

		return QuestState.FINISHED;
	}

	@Override
	public boolean isCompleted()
	{
		return (client.getVarbitValue(VarbitID.CHICKENQUEST) >= 20 || client.getVarbitValue(VarbitID.HUNDRED_MAIN_QUEST_VAR) < 3);
	}
}
