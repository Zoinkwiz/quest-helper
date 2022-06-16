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
package com.questhelper.achievementdiaries.desert;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.DESERT_EASY
)
public class DesertEasy extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement coins, shantayPass, birdSnare, pickaxe, rope, knife, desertShirt, desertRobe, desertBoots,
		grimyHerb, pyramidPlunderArtefact, emptyWaterskin, potatoCacti, combatGear;

	// Items recommended
	ItemRequirement food, antipoison, waterskin, pharaohSceptre, necklaceOfPassage;

	// Quests required
	Requirement icthlarinsLittleHelper;

	Requirement notGoldWarbler, notFiveClay, notEnterKalph, notEnterDesert, notKillVulture, notNardahHerb,
		notCollectCacti, notSellArtefact, notOpenSarc, notCutCactus, notMagicCarpet;

	QuestStep claimReward, goldWarbler, fiveClay, enterDesert, nardahHerb, collectCacti, sellArtefact,
		openSarc, cutCactus, magicCarpet, moveToPyramidPlunder, startPyramidPlunder;

	ObjectStep enterKalph, enterKalphForCacti;

	NpcStep killVulture;

	Zone pyramidPlunderLobby, firstRoom, kalphHive;

	ZoneRequirement inPyramidPlunderLobby, inFirstRoom, inKalphHive;

	ConditionalStep goldWarblerTask, fiveClayTask, enterKalphTask, enterDesertTask, killVultureTask, nardahHerbTask,
		collectCactiTask, sellArtefactTask, openSarcTask, cutCactusTask, magicCarpetTask;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doEasy = new ConditionalStep(this, claimReward);

		enterDesertTask = new ConditionalStep(this, enterDesert);
		doEasy.addStep(notEnterDesert, enterDesertTask);

		cutCactusTask = new ConditionalStep(this, cutCactus);
		doEasy.addStep(notCutCactus, cutCactusTask);

		enterKalphTask = new ConditionalStep(this, enterKalph);
		doEasy.addStep(notEnterKalph, enterKalphTask);

		collectCactiTask = new ConditionalStep(this, enterKalphForCacti);
		collectCactiTask.addStep(inKalphHive, collectCacti);
		doEasy.addStep(notCollectCacti, collectCactiTask);

		goldWarblerTask = new ConditionalStep(this, goldWarbler);
		doEasy.addStep(notGoldWarbler, goldWarblerTask);

		fiveClayTask = new ConditionalStep(this, fiveClay);
		doEasy.addStep(notFiveClay, fiveClayTask);

		magicCarpetTask = new ConditionalStep(this, magicCarpet);
		doEasy.addStep(notMagicCarpet, magicCarpetTask);

		nardahHerbTask = new ConditionalStep(this, nardahHerb);
		doEasy.addStep(notNardahHerb, nardahHerbTask);

		killVultureTask = new ConditionalStep(this, killVulture);
		doEasy.addStep(notKillVulture, killVultureTask);

		openSarcTask = new ConditionalStep(this, moveToPyramidPlunder);
		openSarcTask.addStep(inPyramidPlunderLobby, startPyramidPlunder);
		openSarcTask.addStep(inFirstRoom, openSarc);
		doEasy.addStep(notOpenSarc, openSarcTask);

		sellArtefactTask = new ConditionalStep(this, sellArtefact);
		doEasy.addStep(notSellArtefact, sellArtefactTask);

		return doEasy;
	}

	@Override
	public void setupRequirements()
	{
		notGoldWarbler = new VarplayerRequirement(1198, false, 1);
		notFiveClay = new VarplayerRequirement(1198, false, 2);
		notEnterKalph = new VarplayerRequirement(1198, false, 3);
		notEnterDesert = new VarplayerRequirement(1198, false, 4);
		notKillVulture = new VarplayerRequirement(1198, false, 5);
		notNardahHerb = new VarplayerRequirement(1198, false, 6);
		notCollectCacti = new VarplayerRequirement(1198, false, 7);
		notSellArtefact = new VarplayerRequirement(1198, false, 8);
		notOpenSarc = new VarplayerRequirement(1198, false, 9);
		notCutCactus = new VarplayerRequirement(1198, false, 10);
		notMagicCarpet = new VarplayerRequirement(1198, false, 11);

		coins = new ItemRequirement("Coins", ItemCollections.COINS)
			.showConditioned(new Conditions(LogicType.OR, notNardahHerb, notMagicCarpet));
		potatoCacti = new ItemRequirement("Potato Cacti", ItemID.POTATO_CACTUS).showConditioned(notCollectCacti);
		rope = new ItemRequirement("Rope", ItemID.ROPE)
			.showConditioned(new Conditions(LogicType.OR, notEnterKalph, notCollectCacti));
		shantayPass = new ItemRequirement("Shantay pass", ItemID.SHANTAY_PASS).showConditioned(notEnterDesert);
		birdSnare = new ItemRequirement("Bird snare", ItemID.BIRD_SNARE).showConditioned(notGoldWarbler);
		pickaxe = new ItemRequirement("Pickaxe", ItemCollections.PICKAXES).showConditioned(notFiveClay);
		knife = new ItemRequirement("Knife", ItemID.KNIFE).showConditioned(notCutCactus);
		desertBoots = new ItemRequirement("Desert boots", ItemID.DESERT_BOOTS).showConditioned(notEnterDesert);
		desertRobe = new ItemRequirement("Desert robe", ItemID.DESERT_ROBE).showConditioned(notEnterDesert);
		desertShirt = new ItemRequirement("Desert shirt", ItemID.DESERT_SHIRT).showConditioned(notEnterDesert);
		pyramidPlunderArtefact = new ItemRequirement("Any Artefact from Pyramid Plunder",
			ItemCollections.PLUNDER_ARTEFACTS).showConditioned(notSellArtefact);
		emptyWaterskin = new ItemRequirement("Empty waterskin", ItemID.WATERSKIN0).showConditioned(notCutCactus);
		grimyHerb = new ItemRequirement("Grimy herb", ItemCollections.GRIMY_HERB);

		antipoison = new ItemRequirement("Antipoison", ItemCollections.ANTIPOISONS);
		waterskin = new ItemRequirement("Waterskin", ItemCollections.WATERSKIN);
		pharaohSceptre = new ItemRequirement("Pharaoh's sceptre", ItemCollections.PHAROAH_SCEPTRE);
		necklaceOfPassage = new ItemRequirement("Necklace of passage", ItemCollections.NECKLACE_OF_PASSAGES);

		combatGear = new ItemRequirement("Combat gear and ranged weapon or runes for multiple spell casts", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);

		inFirstRoom = new ZoneRequirement(firstRoom);
		inPyramidPlunderLobby = new ZoneRequirement(pyramidPlunderLobby);
		inKalphHive = new ZoneRequirement(kalphHive);

		icthlarinsLittleHelper = new QuestRequirement(QuestHelperQuest.ICTHLARINS_LITTLE_HELPER,
			2, "Icthlarin's Little Helper Started");
	}

	public void loadZones()
	{
		pyramidPlunderLobby = new Zone(new WorldPoint(1926, 4465, 2), new WorldPoint(1976, 4419, 3));
		firstRoom = new Zone(new WorldPoint(1920, 4478, 0), new WorldPoint(1934, 4462, 0));
		kalphHive = new Zone(new WorldPoint(3454, 9531, 2), new WorldPoint(3520, 9473, 2));
	}

	public void setupSteps()
	{
		enterDesert = new ObjectStep(this, ObjectID.SHANTAY_PASS, new WorldPoint(3304, 3116, 0),
			"Enter the desert.", desertBoots.equipped(), desertRobe.equipped(), desertShirt.equipped(), shantayPass);

		goldWarbler = new ObjectStep(this, ObjectID.BIRD_SNARE_9375, new WorldPoint(3404, 3148, 0),
			"Catch a Golden Warbler in the Uzer hunter area.", birdSnare.highlighted());

		magicCarpet = new NpcStep(this, NpcID.RUG_MERCHANT, new WorldPoint(3310, 3108, 0),
			"Talk to the rug merchant and travel to Pollnivneach.", coins.quantity(200));
		magicCarpet.addDialogSteps("Yes please.", "I want to travel to Pollnivneach.");

		cutCactus = new ObjectStep(this, ObjectID.KHARIDIAN_CACTUS_HEALTHY, new WorldPoint(3290, 3103, 0),
			"Cut Khardian cacti and fill up your waterskin. You may need to cut multiple cacti before you " +
				"successfully get water.", true, knife, emptyWaterskin);
		((ObjectStep) cutCactus).setMaxObjectDistance(5000);

		fiveClay = new ObjectStep(this, ObjectID.ROCKS_11362, new WorldPoint(3420, 3163, 0),
			"Mine five clay in the north east of the desert.");

		nardahHerb = new NpcStep(this, NpcID.ZAHUR, new WorldPoint(3425, 2906, 0),
			"Have Zahur in Nardah clean a grimy herb for you.", grimyHerb, coins.quantity(200));
		nardahHerb.addDialogStep("Please clean all my herbs.");

		killVulture = new NpcStep(this, NpcID.VULTURE, new WorldPoint(3334, 2865, 0),
			"Kill a vulture.", true);
		killVulture.addAlternateNpcs(NpcID.VULTURE_1268);

		moveToPyramidPlunder = new ObjectStep(this, 26622, new WorldPoint(3289, 2800, 0),
			"Enter the Pyramid plunder minigame. If you don't see a Guardian mummy exit and try a different entrance.");
		startPyramidPlunder = new NpcStep(this, NpcID.GUARDIAN_MUMMY, new WorldPoint(1934, 4427, 3),
			"Talk to the guardian mummy to start the minigame. If you don't see a Guardian mummy exit and try a different entrance.");
		startPyramidPlunder.addDialogStep("I know what I'm doing - let's get on with it.");
		openSarc = new ObjectStep(this, 26626, new WorldPoint(1928, 4465, 0),
			"Open the sarcophagus in the first room.");

		sellArtefact = new NpcStep(this, NpcID.SIMON_TEMPLETON, new WorldPoint(3346, 2827, 0),
			"Talk to Simon Templeton and sell your artefact.", pyramidPlunderArtefact);
		sellArtefact.addDialogStep("Yes, show me the money.");

		enterKalph = new ObjectStep(this, 3827, new WorldPoint(3228, 3109, 0),
			"Use the rope on the entrance and enter the Kalphite Hive.", rope.highlighted());
		enterKalph.addAlternateObjects(ObjectID.TUNNEL_ENTRANCE);
		enterKalph.addIcon(ItemID.ROPE);

		enterKalphForCacti = new ObjectStep(this, 3827, new WorldPoint(3228, 3109, 0),
			"Use the rope on the entrance and enter the Kalphite Hive.", rope.highlighted());
		enterKalphForCacti.addAlternateObjects(ObjectID.TUNNEL_ENTRANCE);
		enterKalphForCacti.addIcon(ItemID.ROPE);

		collectCacti = new DetailedQuestStep(this, new WorldPoint(3463, 9482, 2),
			"Pickup and drop cacti until task is completed", potatoCacti);

		claimReward = new NpcStep(this, NpcID.JARR, new WorldPoint(3303, 3124, 0),
			"Talk to Jarr at the Shantay pass to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(coins.quantity(405), shantayPass, birdSnare, pickaxe, rope, knife, desertBoots,
			desertRobe, desertShirt, grimyHerb, pyramidPlunderArtefact, emptyWaterskin, combatGear);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, necklaceOfPassage, antipoison, waterskin, pharaohSceptre);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.HUNTER, 5));
		reqs.add(new SkillRequirement(Skill.THIEVING, 21));

		reqs.add(icthlarinsLittleHelper);

		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Kill a Vulture (lvl 31), tank hits from Kalphite Soldier (lvl 85)");
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Desert amulet 1", ItemID.DESERT_AMULET_1),
			new ItemReward("2,500 Exp. Lamp (Any skill over 30)", ItemID.ANTIQUE_LAMP)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Pharaoh's sceptre can hold up to 10 charges"),
			new UnlockReward("Goats will always drop noted desert goat horn"),
			new UnlockReward("Simon Templeton will now buy your noted artefacts too")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails enterSteps = new PanelDetails("Enter the Desert Dressed Appropriately",
			Collections.singletonList(enterDesert), desertBoots.equipped(), desertShirt.equipped(), desertRobe.equipped(),
			shantayPass);
		enterSteps.setDisplayCondition(notEnterDesert);
		enterSteps.setLockingStep(enterDesertTask);
		allSteps.add(enterSteps);

		PanelDetails cutDesertCactiSteps = new PanelDetails("Cut Desert Cacti", Collections.singletonList(cutCactus),
			knife, emptyWaterskin);
		cutDesertCactiSteps.setDisplayCondition(notCutCactus);
		cutDesertCactiSteps.setLockingStep(cutCactusTask);
		allSteps.add(cutDesertCactiSteps);

		PanelDetails kalphiteHiveSteps = new PanelDetails("Kalphite Hive", Collections.singletonList(enterKalph),
			rope);
		kalphiteHiveSteps.setDisplayCondition(notEnterKalph);
		kalphiteHiveSteps.setLockingStep(enterKalphTask);
		allSteps.add(kalphiteHiveSteps);

		PanelDetails kalphiteCactiSteps = new PanelDetails("Kalphite Cacti", Arrays.asList(enterKalphForCacti, collectCacti),
			rope, combatGear, food, antipoison);
		kalphiteCactiSteps.setDisplayCondition(notCollectCacti);
		kalphiteCactiSteps.setLockingStep(collectCactiTask);
		allSteps.add(kalphiteCactiSteps);

		PanelDetails goldenWarblerSteps = new PanelDetails("Golden Warbler", Collections.singletonList(goldWarbler),
			new SkillRequirement(Skill.HUNTER, 5), birdSnare);
		goldenWarblerSteps.setDisplayCondition(notGoldWarbler);
		goldenWarblerSteps.setLockingStep(goldWarblerTask);
		allSteps.add(goldenWarblerSteps);

		PanelDetails fiveClaySteps = new PanelDetails("Five clay", Collections.singletonList(fiveClay), pickaxe);
		fiveClaySteps.setDisplayCondition(notFiveClay);
		fiveClaySteps.setLockingStep(fiveClayTask);
		allSteps.add(fiveClaySteps);

		PanelDetails magicCarpetRideSteps = new PanelDetails("Magic Carpet Ride",
			Collections.singletonList(magicCarpet), coins.quantity(200));
		magicCarpetRideSteps.setDisplayCondition(notMagicCarpet);
		magicCarpetRideSteps.setLockingStep(magicCarpetTask);
		allSteps.add(magicCarpetRideSteps);

		PanelDetails nardahHerbCleanerSteps = new PanelDetails("Nardah Herb Cleaner",
			Collections.singletonList(nardahHerb), grimyHerb, coins.quantity(200));
		nardahHerbCleanerSteps.setDisplayCondition(notNardahHerb);
		nardahHerbCleanerSteps.setLockingStep(nardahHerbTask);
		allSteps.add(nardahHerbCleanerSteps);

		PanelDetails vultureSteps = new PanelDetails("Kill a Vulture", Collections.singletonList(killVulture),
			combatGear);
		vultureSteps.setDisplayCondition(notKillVulture);
		vultureSteps.setLockingStep(killVultureTask);
		allSteps.add(vultureSteps);

		PanelDetails openSarcSteps = new PanelDetails("First Sarcophagus", Arrays.asList(moveToPyramidPlunder,
			startPyramidPlunder, openSarc), new SkillRequirement(Skill.THIEVING, 21), icthlarinsLittleHelper);
		openSarcSteps.setDisplayCondition(notOpenSarc);
		openSarcSteps.setLockingStep(openSarcTask);
		allSteps.add(openSarcSteps);

		PanelDetails sellArtefactSteps = new PanelDetails("Sell Artefact", Collections.singletonList(sellArtefact),
			pyramidPlunderArtefact);
		sellArtefactSteps.setDisplayCondition(notSellArtefact);
		sellArtefactSteps.setLockingStep(sellArtefactTask);
		allSteps.add(sellArtefactSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
