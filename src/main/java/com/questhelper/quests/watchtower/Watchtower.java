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
package com.questhelper.quests.watchtower;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.WidgetTextRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
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
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;

@QuestDescriptor(
	quest = QuestHelperQuest.WATCHTOWER
)
public class Watchtower extends BasicQuestHelper
{
	//Items Required
	ItemRequirement coins20, goldBar, deathRune, pickaxe, dragonBones, rope2, guamUnf, fingernails, rope, tobansKey, goradsTooth, relic3, relic1, relic2, tobansGold,
		crystal, ogreRelic, rockCake, skavidMap, lightSource, nightshade, nightshade2, crystal2, jangerberries, batBones, groundBatBones, pestleAndMortar, partialPotion, potion,
		magicPotion, magicPotionHighlight, crystal3, crystal4, crystalHighlight, crystal2Highlight, crystal3Highlight, crystal4Highlight;

	Requirement inWatchtowerFloor1, inWatchtowerFloor2, hasTobansKey, onGrewIsland, talkedToGrew, talkedToOg, knownOgreStep, onTobanIsland, hasGoradsTooth,
		talkedToToban, hasTobansGold, hasRelic1, hasRelic2, hasRelic3,gettingOgreRockCake, gaveCake, inEndOfJumpingPath, hasBeenAtEndOfPath, knowsRiddle, inScaredSkavidRoom,
		talkedToScaredSkavid, inSkavidRoom1, inSkavidRoom2, inSkavidRoom3, inSkavidRoom4, talkedToSkavid1, talkedToSkavid2, talkedToSkavid3, talkedToSkavid4, inInsaneSkavidPath,
		inInsaneSkavidRoom, inEnclave, seenShamans, killedOgre1, killedOgre2, killedOgre3, killedOgre4, killedOgre5,
		killedOgre6, killedAllOgres, gotCrystal4, placedCrystal1, placedCrystal2, placedCrystal3, placedCrystal4, inAreaBeforeBridgeJump;

	QuestStep goUpTrellis, goUpLadderToWizard, talkToWizard, goDownFromWizard, goDownFromFirstFloor, searchBush, goBackUpToFirstFloor, goBackUpToWizard, talkToWizardAgain,
		talkToOg, useRopeOnBranch, talkToGrew, leaveGrewIsland, syncStep, enterHoleSouthOfGuTanoth, killGorad, talkToToban, giveTobanDragonBones,
		searchChestForTobansGold, talkToOgAgain, talkToGrewAgain, useRopeOnBranchAgain, bringRelicUpToFirstFloor, bringRelicUpToWizard, talkToWizardWithRelic, enterGuTanoth,
		stealRockCake, talkToGuardBattlement, talkToGuardWithRockCake, jumpGap, talkToCityGuard, talkToCityGuardAgain, enterScaredSkavidCave, talkToScaredSkavid, leaveScaredSkavidRoom,
		enterSkavid1Cave, enterSkavid2Cave, enterSkavid3Cave, enterSkavid4Cave, talkToSkavid1, talkToSkavid2, talkToSkavid3, talkToSkavid4, leaveSkavid1, leaveSkavid2, leaveSkavid3,
		leaveSkavid4, tryToGoThroughToInsaneSkavid, enterInsaneSkavidCave, talkToInsaneSkavid, pickUp2Nightshade, useNightshadeOnGuard, leaveMadSkavid, leaveEnclave,
		goBackUpToFirstFloorAfterEnclave, goBackUpToWizardAfterEnclave, talkToWizardAgainEnclave, useJangerberriesOnGuam, grindBatBones, useBonesOnPotion, goUpToFirstFloorWithPotion,
		goUpToWizardWithPotion, talkToWizardWithPotion, useNightshadeOnGuardAgain, usePotionOnOgre1, usePotionOnOgre2, usePotionOnOgre3, usePotionOnOgre4, usePotionOnOgre5, usePotionOnOgre6,
		mineRock, leaveEnclaveWithCrystals, goUpToFirstFloorWithCrystals, goUpToWizardWithCrystals, talkToWizardWithCrystals, useCrystal1, useCrystal2, useCrystal3, useCrystal4, pullLever;

	//Zones
	Zone watchtowerFloor1, watchtowerFloor2, grewIsland, tobanIsland, endOfJumpingPath, scaredSkavidRoom, skavidRoom1, skavidRoom2, skavidRoom3, skavidRoom4, insaneSkavidPath1,
		insaneSkavidPath2, insaneSkavidRoom, enclave, areaBeforeBridgeJump;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep goTalkToWizard = new ConditionalStep(this, goUpTrellis);
		goTalkToWizard.addStep(inWatchtowerFloor2, talkToWizard);
		goTalkToWizard.addStep(inWatchtowerFloor1, goUpLadderToWizard);

		steps.put(0, goTalkToWizard);

		ConditionalStep getBushItem = new ConditionalStep(this, searchBush);
		getBushItem.addStep(new Conditions(fingernails, inWatchtowerFloor2), talkToWizardAgain);
		getBushItem.addStep(new Conditions(fingernails, inWatchtowerFloor1), goBackUpToWizard);
		getBushItem.addStep(fingernails, goBackUpToFirstFloor);
		getBushItem.addStep(inWatchtowerFloor2, goDownFromWizard);
		getBushItem.addStep(inWatchtowerFloor1, goDownFromFirstFloor);

		steps.put(1, getBushItem);

		ConditionalStep helpOgres = new ConditionalStep(this, syncStep);
		helpOgres.addStep(new Conditions(hasRelic1, hasRelic2, hasRelic3, inWatchtowerFloor2), talkToWizardWithRelic);
		helpOgres.addStep(new Conditions(hasRelic1, hasRelic2, hasRelic3, inWatchtowerFloor1), bringRelicUpToWizard);
		helpOgres.addStep(new Conditions(hasRelic1, hasRelic2, hasRelic3), bringRelicUpToFirstFloor);
		helpOgres.addStep(new Conditions(hasRelic1, hasGoradsTooth, hasRelic3, onGrewIsland), talkToGrewAgain);
		helpOgres.addStep(new Conditions(hasRelic1, hasGoradsTooth, hasRelic3), useRopeOnBranchAgain);
		helpOgres.addStep(new Conditions(hasTobansGold, hasGoradsTooth, hasRelic3), talkToOgAgain);
		helpOgres.addStep(new Conditions(talkedToOg, onTobanIsland, hasGoradsTooth, hasRelic3), searchChestForTobansGold);
		helpOgres.addStep(new Conditions(talkedToOg, onTobanIsland, hasGoradsTooth, talkedToToban), giveTobanDragonBones);
		helpOgres.addStep(new Conditions(talkedToOg, onTobanIsland, hasGoradsTooth), talkToToban);
		helpOgres.addStep(new Conditions(talkedToOg, onTobanIsland), killGorad);
		helpOgres.addStep(new Conditions(onGrewIsland, talkedToGrew), leaveGrewIsland);
		helpOgres.addStep(new Conditions(knownOgreStep, talkedToOg, talkedToGrew), enterHoleSouthOfGuTanoth);
		helpOgres.addStep(new Conditions(knownOgreStep, onGrewIsland), talkToGrew);
		helpOgres.addStep(new Conditions(knownOgreStep, talkedToOg), useRopeOnBranch);
		helpOgres.addStep(knownOgreStep, talkToOg);

		steps.put(2, helpOgres);

		steps.put(3, enterGuTanoth);

		ConditionalStep getCrystal1 = new ConditionalStep(this, stealRockCake);
		getCrystal1.addStep(new Conditions(knowsRiddle, inEndOfJumpingPath), talkToCityGuardAgain);
		getCrystal1.addStep(inEndOfJumpingPath, talkToCityGuard);
		getCrystal1.addStep(gaveCake, jumpGap);
		getCrystal1.addStep(new Conditions(rockCake, gettingOgreRockCake), talkToGuardWithRockCake);
		getCrystal1.addStep(rockCake, talkToGuardBattlement);

		steps.put(4, getCrystal1);

		steps.put(5, getCrystal1);

		ConditionalStep goTalkToScaredSkavid = new ConditionalStep(this, enterScaredSkavidCave);
		goTalkToScaredSkavid.addStep(new Conditions(talkedToSkavid1, talkedToSkavid2, talkedToSkavid3, talkedToSkavid4, inInsaneSkavidRoom), talkToInsaneSkavid);
		goTalkToScaredSkavid.addStep(new Conditions(talkedToSkavid1, talkedToSkavid2, talkedToSkavid3, talkedToSkavid4, inInsaneSkavidPath), enterInsaneSkavidCave);
		goTalkToScaredSkavid.addStep(new Conditions(talkedToSkavid1, talkedToSkavid2, talkedToSkavid3, talkedToSkavid4), tryToGoThroughToInsaneSkavid);
		goTalkToScaredSkavid.addStep(new Conditions(talkedToScaredSkavid, talkedToSkavid1, talkedToSkavid2, talkedToSkavid3, talkedToSkavid4, inSkavidRoom4), leaveSkavid4);
		goTalkToScaredSkavid.addStep(new Conditions(talkedToScaredSkavid, talkedToSkavid1, talkedToSkavid2, talkedToSkavid3, inSkavidRoom4), talkToSkavid4);
		goTalkToScaredSkavid.addStep(new Conditions(talkedToScaredSkavid, talkedToSkavid1, talkedToSkavid2, talkedToSkavid3, inSkavidRoom3), leaveSkavid3);
		goTalkToScaredSkavid.addStep(new Conditions(talkedToScaredSkavid, talkedToSkavid1, talkedToSkavid2, inSkavidRoom3), talkToSkavid3);
		goTalkToScaredSkavid.addStep(new Conditions(talkedToScaredSkavid, talkedToSkavid1, talkedToSkavid2, inSkavidRoom2), leaveSkavid2);
		goTalkToScaredSkavid.addStep(new Conditions(talkedToScaredSkavid, talkedToSkavid1, inSkavidRoom2), talkToSkavid2);
		goTalkToScaredSkavid.addStep(new Conditions(talkedToScaredSkavid, talkedToSkavid1, inSkavidRoom1), leaveSkavid1);
		goTalkToScaredSkavid.addStep(new Conditions(talkedToScaredSkavid, inSkavidRoom1), talkToSkavid1);
		goTalkToScaredSkavid.addStep(new Conditions(inScaredSkavidRoom, talkedToScaredSkavid), leaveScaredSkavidRoom);
		goTalkToScaredSkavid.addStep(new Conditions(talkedToScaredSkavid, talkedToSkavid1, talkedToSkavid2, talkedToSkavid3), enterSkavid4Cave);
		goTalkToScaredSkavid.addStep(new Conditions(talkedToScaredSkavid, talkedToSkavid1, talkedToSkavid2), enterSkavid3Cave);
		goTalkToScaredSkavid.addStep(new Conditions(talkedToScaredSkavid, talkedToSkavid1), enterSkavid2Cave);
		goTalkToScaredSkavid.addStep(talkedToScaredSkavid, enterSkavid1Cave);
		goTalkToScaredSkavid.addStep(inScaredSkavidRoom, talkToScaredSkavid);

		steps.put(6, goTalkToScaredSkavid);

		ConditionalStep infiltrateEnclave = new ConditionalStep(this, useNightshadeOnGuard);
		infiltrateEnclave.addStep(new Conditions(seenShamans, inWatchtowerFloor2), talkToWizardAgainEnclave);
		infiltrateEnclave.addStep(new Conditions(seenShamans, inWatchtowerFloor1), goBackUpToWizardAfterEnclave);
		infiltrateEnclave.addStep(new Conditions(seenShamans, inEnclave), leaveEnclave);
		infiltrateEnclave.addStep(new Conditions(seenShamans), goBackUpToFirstFloorAfterEnclave);
		infiltrateEnclave.addStep(new Conditions(inInsaneSkavidRoom, nightshade2), leaveMadSkavid);
		infiltrateEnclave.addStep(inInsaneSkavidRoom, pickUp2Nightshade);

		steps.put(7, infiltrateEnclave);

		steps.put(8, infiltrateEnclave);

		ConditionalStep makePotion = new ConditionalStep(this, useJangerberriesOnGuam);
		makePotion.addStep(new Conditions(potion, inWatchtowerFloor2), talkToWizardWithPotion);
		makePotion.addStep(new Conditions(potion, inWatchtowerFloor1), goUpToWizardWithPotion);
		makePotion.addStep(potion, goUpToFirstFloorWithPotion);
		makePotion.addStep(new Conditions(partialPotion, groundBatBones), useBonesOnPotion);
		makePotion.addStep(partialPotion, grindBatBones);

		steps.put(9, makePotion);

		ConditionalStep killOgres = new ConditionalStep(this, useNightshadeOnGuardAgain);
		killOgres.addStep(new Conditions(inWatchtowerFloor2, gotCrystal4), talkToWizardWithCrystals);
		killOgres.addStep(new Conditions(inWatchtowerFloor1, gotCrystal4), goUpToWizardWithCrystals);
		killOgres.addStep(new Conditions(inEnclave, gotCrystal4), leaveEnclaveWithCrystals);
		killOgres.addStep(gotCrystal4, goUpToFirstFloorWithCrystals);
		killOgres.addStep(new Conditions(inEnclave, killedAllOgres), mineRock);
		killOgres.addStep(new Conditions(inEnclave, killedOgre1, killedOgre2, killedOgre3, killedOgre4, killedOgre5), usePotionOnOgre6);
		killOgres.addStep(new Conditions(inEnclave, killedOgre1, killedOgre2, killedOgre3, killedOgre4), usePotionOnOgre5);
		killOgres.addStep(new Conditions(inEnclave, killedOgre1, killedOgre2, killedOgre3), usePotionOnOgre4);
		killOgres.addStep(new Conditions(inEnclave, killedOgre1, killedOgre2), usePotionOnOgre3);
		killOgres.addStep(new Conditions(inEnclave, killedOgre1), usePotionOnOgre2);
		killOgres.addStep(inEnclave, usePotionOnOgre1);

		steps.put(10, killOgres);

		ConditionalStep placeCrystals = new ConditionalStep(this, goUpToFirstFloorWithCrystals);
		placeCrystals.addStep(new Conditions(inWatchtowerFloor2, placedCrystal1, placedCrystal2, placedCrystal3, placedCrystal4), pullLever);
		placeCrystals.addStep(new Conditions(inWatchtowerFloor2, placedCrystal1, placedCrystal2, placedCrystal3), useCrystal4);
		placeCrystals.addStep(new Conditions(inWatchtowerFloor2, placedCrystal1, placedCrystal2), useCrystal3);
		placeCrystals.addStep(new Conditions(inWatchtowerFloor2, placedCrystal1), useCrystal2);
		placeCrystals.addStep(inWatchtowerFloor2, useCrystal1);
		placeCrystals.addStep(inWatchtowerFloor1, goUpToWizardWithCrystals);

		steps.put(11, placeCrystals);
		steps.put(12, placeCrystals);

		return steps;
	}

	public void setupItemRequirements()
	{
		guamUnf = new ItemRequirement("Guam potion (unf)", ItemID.GUAM_POTION_UNF);
		guamUnf.setHighlightInInventory(true);

		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes());

		fingernails = new ItemRequirement("Fingernails", ItemID.FINGERNAILS);
		rope2 = new ItemRequirement("Rope", ItemID.ROPE, 2);
		rope = new ItemRequirement("Rope", ItemID.ROPE);
		rope.setHighlightInInventory(true);
		tobansKey = new ItemRequirement("Toban's key", ItemID.TOBANS_KEY);
		goradsTooth = new ItemRequirement("Ogre tooth", ItemID.OGRE_TOOTH);

		dragonBones = new ItemRequirement("Dragon bones", ItemID.DRAGON_BONES);

		relic1 = new ItemRequirement("Relic part 1", ItemID.RELIC_PART_1);
		relic2 = new ItemRequirement("Relic part 2", ItemID.RELIC_PART_2);
		relic3 = new ItemRequirement("Relic part 3", ItemID.RELIC_PART_3);

		tobansGold = new ItemRequirement("Toban's gold", ItemID.TOBANS_GOLD);

		crystal = new ItemRequirement("Crystal", ItemID.CRYSTAL);
		crystalHighlight = new ItemRequirement("Crystal", ItemID.CRYSTAL);
		crystalHighlight.setHighlightInInventory(true);

		rockCake = new ItemRequirement("Rock cake", ItemID.ROCK_CAKE);

		ogreRelic = new ItemRequirement("Ogre relic", ItemID.OGRE_RELIC);
		ogreRelic.setTooltip("Obtained during the quest");

		deathRune = new ItemRequirement("Death rune", ItemID.DEATH_RUNE);

		coins20 = new ItemRequirement("Coins", ItemID.COINS_995, 20);

		skavidMap = new ItemRequirement("Skavid map", ItemID.SKAVID_MAP);
		skavidMap.setTooltip("You can get another from the city guard in south east Gu'Tanoth.");

		lightSource = new ItemRequirement("A light source", ItemCollections.getLightSources());

		goldBar = new ItemRequirement("Gold bar", ItemID.GOLD_BAR);

		nightshade = new ItemRequirement("Cave nightshade", ItemID.CAVE_NIGHTSHADE);
		nightshade.setHighlightInInventory(true);
		nightshade2 = new ItemRequirement("Cave nightshade", ItemID.CAVE_NIGHTSHADE, 2);

		crystal2 = new ItemRequirement("Crystal", ItemID.CRYSTAL_2381);
		crystal2.setTooltip("You can get another from the mad skavid");

		crystal2Highlight = new ItemRequirement("Crystal", ItemID.CRYSTAL_2381);
		crystal2Highlight.setTooltip("You can get another from the mad skavid");
		crystal2Highlight.setHighlightInInventory(true);

		crystal3 = new ItemRequirement("Crystal", ItemID.CRYSTAL_2382);
		crystal3.setTooltip("You can get another from the shaman robes on the east side of the Ogre Enclave");

		crystal3Highlight = new ItemRequirement("Crystal", ItemID.CRYSTAL_2382);
		crystal3Highlight.setTooltip("You can get another from the shaman robes on the east side of the Ogre Enclave");
		crystal3Highlight.setHighlightInInventory(true);

		crystal4 = new ItemRequirement("Crystal", ItemID.CRYSTAL_2383);
		crystal4.setTooltip("You can get another from the Rock of Dalgroth in the Ogre Enclave");

		crystal4Highlight = new ItemRequirement("Crystal", ItemID.CRYSTAL_2383);
		crystal4Highlight.setTooltip("You can get another from the Rock of Dalgroth in the Ogre Enclave");
		crystal4Highlight.setHighlightInInventory(true);

		pestleAndMortar = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR);
		pestleAndMortar.setHighlightInInventory(true);

		batBones = new ItemRequirement("Bat bones", ItemID.BAT_BONES);
		batBones.setHighlightInInventory(true);

		groundBatBones = new ItemRequirement("Ground bat bones", ItemID.GROUND_BAT_BONES);
		groundBatBones.setHighlightInInventory(true);

		jangerberries = new ItemRequirement("Jangerberries", ItemID.JANGERBERRIES);
		jangerberries.setHighlightInInventory(true);

		partialPotion = new ItemRequirement("Vial", ItemID.VIAL_2390);
		partialPotion.setHighlightInInventory(true);

		potion = new ItemRequirement("Potion", ItemID.POTION_2394);

		magicPotion = new ItemRequirement("Magic ogre potion", ItemID.MAGIC_OGRE_POTION);
		magicPotion.setTooltip("You can make another with a guam unf potion, adding jangerberries then ground bat bones, and having the Watchtower Wizard enchant it");

		magicPotionHighlight = new ItemRequirement("Magic ogre potion", ItemID.MAGIC_OGRE_POTION);
		magicPotionHighlight.setTooltip("You can make another with a guam unf potion, adding jangerberries then ground bat bones, and having the Watchtower Wizard enchant it");
		magicPotionHighlight.setHighlightInInventory(true);
	}

	public void loadZones()
	{
		watchtowerFloor1 = new Zone(new WorldPoint(2543, 3111, 1), new WorldPoint(2550, 3118, 1));
		watchtowerFloor2 = new Zone(new WorldPoint(2543, 3111, 2), new WorldPoint(2550, 3118, 2));
		grewIsland = new Zone(new WorldPoint(2504, 3078, 0), new WorldPoint(2520, 3092, 0));
		tobanIsland = new Zone(new WorldPoint(2569, 3021, 0), new WorldPoint(2582, 3038, 0));
		endOfJumpingPath = new Zone(new WorldPoint(2526, 3027, 0), new WorldPoint(2545, 3036, 0));
		scaredSkavidRoom = new Zone(new WorldPoint(2498, 9424, 0), new WorldPoint(2510, 9441, 0));
		skavidRoom1 = new Zone(new WorldPoint(2498, 9445, 0), new WorldPoint(2509, 9453, 0));
		skavidRoom2 = new Zone(new WorldPoint(2512, 9446, 0), new WorldPoint(2522, 9455, 0));
		skavidRoom3 = new Zone(new WorldPoint(2528, 9461, 0), new WorldPoint(2535, 9469, 0));
		skavidRoom4 = new Zone(new WorldPoint(2498, 9410, 0), new WorldPoint(2505, 9420, 0));
		insaneSkavidPath1 = new Zone(new WorldPoint(2546, 3011, 0), new WorldPoint(2553, 3027, 0));
		insaneSkavidPath2 = new Zone(new WorldPoint(2529, 3011, 0), new WorldPoint(2545, 3015, 0));
		insaneSkavidRoom = new Zone(new WorldPoint(2522, 9410, 0), new WorldPoint(2530, 9415, 0));
		enclave = new Zone(new WorldPoint(2563, 9408, 0), new WorldPoint(2621, 9470, 0));
		areaBeforeBridgeJump = new Zone(new WorldPoint(2507, 3004, 0), new WorldPoint(2532, 3026, 0));
	}

	public void setupConditions()
	{
		// 3138, 0->1 after killing gonrad
		inWatchtowerFloor1 = new ZoneRequirement(watchtowerFloor1);
		inWatchtowerFloor2 = new ZoneRequirement(watchtowerFloor2);
		inEndOfJumpingPath = new ZoneRequirement(endOfJumpingPath);
		hasBeenAtEndOfPath = new Conditions(true, LogicType.OR, new ZoneRequirement(endOfJumpingPath));
		inScaredSkavidRoom = new ZoneRequirement(scaredSkavidRoom);
		inSkavidRoom1 = new ZoneRequirement(skavidRoom1);
		inSkavidRoom2 = new ZoneRequirement(skavidRoom2);
		inSkavidRoom3 = new ZoneRequirement(skavidRoom3);
		inSkavidRoom4 = new ZoneRequirement(skavidRoom4);
		inInsaneSkavidPath = new ZoneRequirement(insaneSkavidPath1, insaneSkavidPath2);
		inInsaneSkavidRoom = new ZoneRequirement(insaneSkavidRoom);
		inEnclave = new ZoneRequirement(enclave);
		inAreaBeforeBridgeJump = new ZoneRequirement(areaBeforeBridgeJump);

		hasTobansKey = new Conditions(true, tobansKey);
		hasGoradsTooth = new Conditions(true, goradsTooth);
		hasTobansGold = new Conditions(true, tobansGold);

		onGrewIsland = new ZoneRequirement(grewIsland);
		onTobanIsland = new ZoneRequirement(tobanIsland);

		knownOgreStep = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "In the meantime, I'll throw those fingernails out for you."),
			new WidgetTextRequirement(119, 3, true, "deal with the tribal ogres."));

		talkedToGrew = new Conditions(true, LogicType.OR, new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "The morsel is back.", "Heheheheh!"),
			new WidgetTextRequirement(119, 3, true, "Grew wants me to give him", "I have <col=800000>one of Gorad's teeth"));

		talkedToOg = new Conditions(true, LogicType.OR, new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "Here is a key to the chest it's in.", "Where my gold from dat dirty Toban?"),
			new WidgetTextRequirement(119, 3, true, "Og wants me to", "I have Og's <col=800000>stolen gold"), hasTobansKey);

		talkedToToban = new Conditions(true, LogicType.OR, new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "Prove to me your might", "Hahaha! Small t'ing returns."),
			new WidgetTextRequirement(119, 3, true, "Toban wants me to give him", "I have the <col=800000>dragon bones"));

		hasRelic1 = new Conditions(true, LogicType.OR, relic1,
			new WidgetTextRequirement(119, 3, true, "I returned Og's stolen gold."));

		hasRelic2 = new Conditions(true, LogicType.OR, relic2,
			new WidgetTextRequirement(119, 3, true, "I knocked out one of Gorad's teeth and gave it to Grew."));

		hasRelic3 = new Conditions(true, LogicType.OR, relic3,
			new WidgetTextRequirement(119, 3, true, "I gave the dragon bones to Toban."));

		gettingOgreRockCake = new VarbitRequirement(3120, 1);
		gaveCake = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "This time we will let it go."),
			new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "Well, well, look at this."),
			new WidgetTextRequirement(119, 3, true, "<str>I gave the north-east guard a rock cake."),
			inAreaBeforeBridgeJump
		);
		// 3319, 1, tried to enter city
		knowsRiddle = new VarbitRequirement(3121, 1);

		talkedToScaredSkavid = new Conditions(true, LogicType.OR, new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "Master, how are you doing", "Those will gets you started."),
			new WidgetTextRequirement(119, 3, true, "ar, nod, gor, ig, cur"));

		talkedToSkavid1 = new Conditions(true, LogicType.OR, new ChatMessageRequirement(inSkavidRoom1, "It seems the skavid understood you.", "You have already talked to this skavid."),
			new WidgetTextRequirement(119, 3, true, "'Bidith tanath'"));
		talkedToSkavid2 = new Conditions(true, LogicType.OR, new ChatMessageRequirement(inSkavidRoom2, "It seems the skavid understood you.", "You have already talked to this skavid."),
			new WidgetTextRequirement(119, 3, true, "'Gor cur'"));
		talkedToSkavid3 = new Conditions(true, LogicType.OR, new ChatMessageRequirement(inSkavidRoom3, "It seems the skavid understood you.", "You have already talked to this skavid."),
			new WidgetTextRequirement(119, 3, true, "'Cur bidith'"));
		talkedToSkavid4 = new Conditions(true, LogicType.OR, new ChatMessageRequirement(inSkavidRoom4, "It seems the skavid understood you.", "You have already talked to this skavid."),
			new WidgetTextRequirement(119, 3, true, "'Gor nod'"));

		seenShamans = new VarbitRequirement(3125, 1);

		killedOgre1 = new VarbitRequirement(3131, 1);
		killedOgre2 = new VarbitRequirement(3132, 1);
		killedOgre3 = new VarbitRequirement(3133, 1);
		killedOgre4 = new VarbitRequirement(3134, 1);
		killedOgre5 = new VarbitRequirement(3135, 1);
		killedOgre6 = new VarbitRequirement(3136, 1);

		killedAllOgres = new Conditions(killedOgre1, killedOgre2, killedOgre3, killedOgre4, killedOgre5, killedOgre6);

		gotCrystal4 = new VarbitRequirement(3124, 1);

		placedCrystal1 = new VarbitRequirement(3128, 1);
		placedCrystal2 = new VarbitRequirement(3129, 1);
		placedCrystal3 = new VarbitRequirement(3127, 1);
		placedCrystal4 = new VarbitRequirement(3130, 1);

	}

	public void setupSteps()
	{
		goUpTrellis = new ObjectStep(this, ObjectID.TRELLIS_20056, new WorldPoint(2548, 3119, 0), "Go to the top floor of the Watchtower north of Yanille and talk to the Watchtower Wizard.");
		goUpLadderToWizard = new ObjectStep(this, ObjectID.LADDER_2796, new WorldPoint(2549, 3111, 1), "Go to the top floor of the Watchtower north of Yanille and talk to the Watchtower Wizard.");
		talkToWizard = new NpcStep(this, NpcID.WATCHTOWER_WIZARD, new WorldPoint(2546, 3114, 2), "Go to the top floor of the Watchtower north of Yanille and talk to the Watchtower Wizard.");
		talkToWizard.addDialogStep("What's the matter?");
		talkToWizard.addDialogStep("So how come the spell doesn't work?");
		talkToWizard.addDialogStep("Can I be of help?");
		talkToWizard.addSubSteps(goUpTrellis, goUpLadderToWizard);

		goDownFromWizard = new ObjectStep(this, ObjectID.LADDER_2797, new WorldPoint(2549, 3111, 2), "Search the bushes north of the Watchtower.");
		goDownFromFirstFloor = new ObjectStep(this, ObjectID.LADDER_17122, new WorldPoint(2544, 3111, 1), "Search the bushes north of the Watchtower.");
		searchBush = new ObjectStep(this, ObjectID.BUSH_2799, new WorldPoint(2544, 3134, 0), "Search the bushes north of the Watchtower.");

		searchBush.addSubSteps(goDownFromWizard, goDownFromFirstFloor);

		goBackUpToFirstFloor = new ObjectStep(this, ObjectID.LADDER_2833, new WorldPoint(2544, 3111, 0), "Return to the Watchtower Wizard with the fingernails.", fingernails);
		goBackUpToWizard = new ObjectStep(this, ObjectID.LADDER_2796, new WorldPoint(2549, 3111, 1), "Return to the Watchtower Wizard with the fingernails.", fingernails);
		talkToWizardAgain = new NpcStep(this, NpcID.WATCHTOWER_WIZARD, new WorldPoint(2546, 3114, 2), "Return to the Watchtower Wizard with the fingernails.", fingernails);
		talkToWizardAgain.addDialogStep("What do you suggest I do?");
		talkToWizardAgain.addDialogStep("So what do I do?");

		talkToWizardAgain.addSubSteps(goBackUpToFirstFloor, goBackUpToWizard);

		syncStep = new DetailedQuestStep(this, "Please open the Watchtower quest journal to sync your state.");

		talkToOg = new NpcStep(this, NpcID.OG, new WorldPoint(2506, 3115, 0), "Talk to Og north west of Yanille.");
		talkToOg.addDialogStep("I seek entrance to the city of ogres.");

		useRopeOnBranch = new ObjectStep(this, ObjectID.BRANCH, new WorldPoint(2502, 3087, 0), "Use your rope on a branch to swing to the island west of Yanille.", rope);
		useRopeOnBranch.addIcon(ItemID.ROPE);

		talkToGrew = new NpcStep(this, NpcID.GREW, new WorldPoint(2511, 3086, 0), "Talk to Grew on the island west of Yanille.");
		talkToGrew.addDialogStep("Don't eat me; I can help you.");

		leaveGrewIsland = new ObjectStep(this, ObjectID.ROPESWING_23570, new WorldPoint(2511, 3093, 0), "Leave Grew's island.");

		enterHoleSouthOfGuTanoth = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_2811, new WorldPoint(2500, 2990, 0), "Enter the hole south of Gu'Tanoth.");

		killGorad = new NpcStep(this, NpcID.GORAD, new WorldPoint(2578, 3022, 0), "Kill Gorad and pick up his tooth.");
		talkToToban = new NpcStep(this, NpcID.TOBAN, new WorldPoint(2574, 3027, 0), "Talk to Toban on the island east of Gu'Tanoth.");
		talkToToban.addDialogStep("I seek entrance to the city of ogres.");
		talkToToban.addDialogStep("I could do something for you...");
		giveTobanDragonBones = new NpcStep(this, NpcID.TOBAN, new WorldPoint(2574, 3027, 0), "Give Toban some dragon bones on the island east of Gu'Tanoth.", dragonBones);
		searchChestForTobansGold = new ObjectStep(this, ObjectID.CHEST_2790, new WorldPoint(2575, 3031, 0), "Open the chest on the island east of Gu'Tanoth using Toban's Key.", tobansKey);

		talkToOgAgain = new NpcStep(this, NpcID.OG, new WorldPoint(2506, 3115, 0), "Return to Og with his stolen gold north west of Yanille.", tobansGold);

		useRopeOnBranchAgain = new ObjectStep(this, ObjectID.BRANCH, new WorldPoint(2502, 3087, 0), "Return to Grew with Gorad's teeth on the island west of Yanille.", rope);
		useRopeOnBranchAgain.addIcon(ItemID.ROPE);
		talkToGrewAgain = new NpcStep(this, NpcID.GREW, new WorldPoint(2511, 3086, 0), "Return to Grew with Gorad's teeth on the island west of Yanille.", goradsTooth);

		bringRelicUpToFirstFloor = new ObjectStep(this, ObjectID.LADDER_2833, new WorldPoint(2544, 3111, 0), "Return to the Watchtower Wizard with the relic pieces.", relic1, relic2, relic3);
		bringRelicUpToWizard = new ObjectStep(this, ObjectID.LADDER_2796, new WorldPoint(2549, 3111, 1), "Return to the Watchtower Wizard with the relic pieces.", relic1, relic2, relic3);
		talkToWizardWithRelic = new NpcStep(this, NpcID.WATCHTOWER_WIZARD, new WorldPoint(2546, 3114, 2), "Return to the Watchtower Wizard with the relic pieces.", relic1, relic2, relic3);
		talkToWizardWithRelic.addSubSteps(bringRelicUpToWizard, bringRelicUpToFirstFloor);

		enterGuTanoth = new NpcStep(this, NpcID.OGRE_GUARD_4370, new WorldPoint(2504, 3063, 0), "Talk to the Ogre Guard at the entrance of Gu'Tanoth and give them the ogre relic.", ogreRelic);

		stealRockCake = new ObjectStep(this, ObjectID.COUNTER_2793, new WorldPoint(2514, 3036, 0), "Steal a rock cake from Gu'Tanoth's market.");

		talkToGuardBattlement = new NpcStep(this, NpcID.OGRE_GUARD_4371, new WorldPoint(2503, 3012, 0), "Talk to an Ogre Guard next to the battelement.");
		talkToGuardBattlement.addDialogStep("But I am a friend to ogres...");
		talkToGuardWithRockCake = new ObjectStep(this, NpcID.OGRE_GUARD_4371, new WorldPoint(2507, 3012, 0),
			"Attempt to cross the battlement again with a rock cake.", rockCake);
		jumpGap = new ObjectStep(this, ObjectID.GAP, new WorldPoint(2530, 3026, 0), "Jump over the broken bridge.", coins20);
		jumpGap.addDialogStep("Okay, I'll pay it.");

		talkToCityGuard = new NpcStep(this, NpcID.CITY_GUARD, new WorldPoint(2543, 3032, 0), "Talk to the City Guard.");
		talkToCityGuard.addDialogStep("I seek passage into the skavid caves.");
		talkToCityGuardAgain = new NpcStep(this, NpcID.CITY_GUARD, new WorldPoint(2543, 3032, 0), "Talk to the City Guard again.", deathRune);

		enterScaredSkavidCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_2809, new WorldPoint(2554, 3035, 0), "Enter the skavid cave entrance in east Gu'Tanoth.", skavidMap, lightSource);

		talkToScaredSkavid = new NpcStep(this, NpcID.SCARED_SKAVID, new WorldPoint(2502, 9433, 0), "Talk to the scared skavid.");
		talkToScaredSkavid.addDialogStep("Okay, okay, I'm not going to hurt you.");

		leaveScaredSkavidRoom = new ObjectStep(this, ObjectID.CAVE_EXIT_2821, new WorldPoint(2504, 9442, 0), "Talk to four other skavids in their caves.");

		enterSkavid1Cave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_2808, new WorldPoint(2554, 3053, 0), "Talk to four other skavids in their caves.", skavidMap, lightSource);
		enterSkavid2Cave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_2807, new WorldPoint(2541, 3053, 0), "Talk to four other skavids in their caves.", skavidMap, lightSource);
		enterSkavid3Cave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_2806, new WorldPoint(2524, 3069, 0), "Talk to four other skavids in their caves.", skavidMap, lightSource);
		enterSkavid4Cave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_2805, new WorldPoint(2561, 3024, 0), "Talk to four other skavids in their caves.", skavidMap, lightSource);

		talkToSkavid1 = new NpcStep(this, NpcID.SKAVID_4378, new WorldPoint(2503, 9449, 0), "Talk to the skavid.");
		talkToSkavid1.addDialogStep("Cur.");

		talkToSkavid2 = new NpcStep(this, NpcID.SKAVID_4377, new WorldPoint(2516, 9451, 0), "Talk to the skavid.");
		talkToSkavid2.addDialogStep("Ar.");

		talkToSkavid3 = new NpcStep(this, NpcID.SKAVID_4376, new WorldPoint(2531, 9465, 0), "Talk to the skavid.");
		talkToSkavid3.addDialogStep("Ig.");

		talkToSkavid4 = new NpcStep(this, NpcID.SKAVID_4379, new WorldPoint(2503, 9449, 0), "Talk to the skavid.");
		talkToSkavid4.addDialogStep("Nod.");

		leaveSkavid1 = new ObjectStep(this, ObjectID.CAVE_EXIT_2820, new WorldPoint(2497, 9451, 0), "Talk to four other skavids in their caves.");
		leaveSkavid2 = new ObjectStep(this, ObjectID.CAVE_EXIT_2819, new WorldPoint(2518, 9456, 0), "Talk to four other skavids in their caves.");
		leaveSkavid3 = new ObjectStep(this, ObjectID.CAVE_EXIT_2818, new WorldPoint(2532, 9470, 0), "Talk to four other skavids in their caves.");
		leaveSkavid4 = new ObjectStep(this, ObjectID.CAVE_EXIT_2817, new WorldPoint(2497, 9418, 0), "Go talk to the mad skavid.");

		enterSkavid1Cave.addSubSteps(enterSkavid2Cave, enterSkavid3Cave, enterSkavid4Cave, talkToSkavid1, talkToSkavid2, talkToSkavid3, talkToSkavid4, leaveSkavid1, leaveSkavid2, leaveSkavid3, leaveSkavid4, leaveScaredSkavidRoom);

		tryToGoThroughToInsaneSkavid = new ObjectStep(this, ObjectID.CITY_GATE_2786, new WorldPoint(2550, 3028, 0),
			"Try to go through the gate to the south east cave of Gu'Tanoth. Give the guard a gold bar and go through.", goldBar);

		enterInsaneSkavidCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_2810, new WorldPoint(2528, 3013, 0), "Enter the mad skavid's cave.");

		talkToInsaneSkavid = new SkavidChoice(this);

		pickUp2Nightshade = new DetailedQuestStep(this, new WorldPoint(2528, 9415, 0), "Pick up 2 cave nightshade. You can world hop to get a second one.", nightshade2);

		useNightshadeOnGuard = new NpcStep(this, NpcID.ENCLAVE_GUARD, new WorldPoint(2507, 3036, 0), "Use a nightshade on an Enclave Guard in the Gu'Tanoth market.", nightshade);
		useNightshadeOnGuard.addIcon(ItemID.CAVE_NIGHTSHADE);

		leaveMadSkavid = new ObjectStep(this, ObjectID.CAVE_EXIT_2822, new WorldPoint(2521, 9411, 0), "Leave the mad skavid's cave.");

		useNightshadeOnGuard.addSubSteps(leaveMadSkavid);

		leaveEnclave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_2813, new WorldPoint(2600, 9469, 0), "Leave the enclave and return to the Watchtower Wizard.");

		goBackUpToFirstFloorAfterEnclave = new ObjectStep(this, ObjectID.LADDER_2833, new WorldPoint(2544, 3111, 0), "Return to the Watchtower Wizard.");
		goBackUpToWizardAfterEnclave = new ObjectStep(this, ObjectID.LADDER_2796, new WorldPoint(2549, 3111, 1), "Return to the Watchtower Wizard.");
		talkToWizardAgainEnclave = new NpcStep(this, NpcID.WATCHTOWER_WIZARD, new WorldPoint(2546, 3114, 2), "Return to the Watchtower Wizard.");
		talkToWizardAgainEnclave.addSubSteps(leaveEnclave, goBackUpToFirstFloorAfterEnclave, goBackUpToWizardAfterEnclave);

		useJangerberriesOnGuam = new DetailedQuestStep(this, "Use some jangerberries on a guam potion (unf)", guamUnf, jangerberries);
		grindBatBones = new DetailedQuestStep(this, "Use a pestle and mortar on some bat bones.", pestleAndMortar, batBones);
		useBonesOnPotion = new DetailedQuestStep(this, "Use the ground bat bones on the potion", groundBatBones, partialPotion);

		goUpToFirstFloorWithPotion = new ObjectStep(this, ObjectID.LADDER_2833, new WorldPoint(2544, 3111, 0), "Return to the Watchtower Wizard with the potion.", potion);
		goUpToWizardWithPotion = new ObjectStep(this, ObjectID.LADDER_2796, new WorldPoint(2549, 3111, 1), "Return to the Watchtower Wizard with the potion.", potion);
		talkToWizardWithPotion = new NpcStep(this, NpcID.WATCHTOWER_WIZARD, new WorldPoint(2546, 3114, 2), "Return to the Watchtower Wizard with the potion.", potion);

		talkToWizardWithPotion.addSubSteps(goUpToFirstFloorWithPotion, goUpToWizardWithPotion);


		useNightshadeOnGuardAgain = new NpcStep(this, NpcID.ENCLAVE_GUARD, new WorldPoint(2507, 3036, 0),
			"Use a nightshade on an Enclave Guard in the Gu'Tanoth market to enter the enclave again.", nightshade, magicPotion, pickaxe);
		useNightshadeOnGuardAgain.addIcon(ItemID.CAVE_NIGHTSHADE);

		usePotionOnOgre1 = new NpcStep(this, NpcID.OGRE_SHAMAN_4383, new WorldPoint(2590, 9438, 0), "Use the magic ogre potion on all the ogre shamans.", magicPotionHighlight);
		usePotionOnOgre2 = new NpcStep(this, NpcID.OGRE_SHAMAN_4387, new WorldPoint(2579, 9436, 0), "Use the magic ogre potion on all the ogre shamans.", magicPotionHighlight);
		usePotionOnOgre3 = new NpcStep(this, NpcID.OGRE_SHAMAN_4389, new WorldPoint(2578, 9450, 0), "Use the magic ogre potion on all the ogre shamans.", magicPotionHighlight);
		usePotionOnOgre4 = new NpcStep(this, NpcID.OGRE_SHAMAN_4391, new WorldPoint(2600, 9461, 0), "Use the magic ogre potion on all the ogre shamans.", magicPotionHighlight);
		usePotionOnOgre5 = new NpcStep(this, NpcID.OGRE_SHAMAN_4393, new WorldPoint(2608, 9450, 0), "Use the magic ogre potion on all the ogre shamans.", magicPotionHighlight);
		usePotionOnOgre6 = new NpcStep(this, NpcID.OGRE_SHAMAN_4395, new WorldPoint(2610, 9435, 0), "Use the magic ogre potion on all the ogre shamans.", magicPotionHighlight);
		usePotionOnOgre1.addSubSteps(usePotionOnOgre2, usePotionOnOgre3, usePotionOnOgre4, usePotionOnOgre5, usePotionOnOgre6);

		mineRock = new ObjectStep(this, ObjectID.ROCK_OF_DALGROTH, new WorldPoint(2591, 9450, 0), "Mine the Rock of Dalgorth in the enclave.", pickaxe);

		leaveEnclaveWithCrystals = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_2813, new WorldPoint(2600, 9469, 0), "Leave the enclave and return to the Watchtower Wizard with the crystals.", crystal, crystal2, crystal3, crystal4);

		goUpToFirstFloorWithCrystals = new ObjectStep(this, ObjectID.LADDER_2833, new WorldPoint(2544, 3111, 0), "Return to the Watchtower Wizard with the crystals.", crystal, crystal2, crystal3, crystal4);
		goUpToWizardWithCrystals = new ObjectStep(this, ObjectID.LADDER_2796, new WorldPoint(2549, 3111, 1), "Return to the Watchtower Wizard with the crystals.", crystal, crystal2, crystal3, crystal4);
		talkToWizardWithCrystals = new NpcStep(this, NpcID.WATCHTOWER_WIZARD, new WorldPoint(2546, 3114, 2), "Return to the Watchtower Wizard with the crystals.", crystal, crystal2, crystal3, crystal4);

		talkToWizardWithCrystals.addSubSteps(leaveEnclaveWithCrystals, goUpToFirstFloorWithCrystals, goUpToWizardWithCrystals);

		useCrystal1 = new ObjectStep(this, NullObjectID.NULL_20029, new WorldPoint(2545, 3113, 2), "Use the crystals on the pillars.", crystalHighlight);
		useCrystal1.addIcon(ItemID.CRYSTAL);

		useCrystal2 = new ObjectStep(this, NullObjectID.NULL_20033, new WorldPoint(2548, 3116, 2), "Use the crystals on the pillars.", crystal2Highlight);
		useCrystal2.addIcon(ItemID.CRYSTAL_2381);

		useCrystal3 = new ObjectStep(this, NullObjectID.NULL_20025, new WorldPoint(2545, 3116, 2), "Use the crystals on the pillars.", crystal3Highlight);
		useCrystal3.addIcon(ItemID.CRYSTAL_2382);

		useCrystal4 = new ObjectStep(this, NullObjectID.NULL_20037, new WorldPoint(2548, 3113, 2), "Use the crystals on the pillars.", crystal4Highlight);
		useCrystal4.addIcon(ItemID.CRYSTAL_2383);

		useCrystal1.addSubSteps(useCrystal2, useCrystal3, useCrystal4);

		pullLever = new ObjectStep(this, ObjectID.LEVER_2794, new WorldPoint(2543, 3115, 2), "Pull the lever to finish the quest.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(coins20);
		reqs.add(goldBar);
		reqs.add(deathRune);
		reqs.add(pickaxe);
		reqs.add(dragonBones);
		reqs.add(rope2);
		reqs.add(guamUnf);
		reqs.add(lightSource);
		reqs.add(pestleAndMortar);
		reqs.add(batBones);
		reqs.add(jangerberries);
		return reqs;
	}


	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Gorad (level 68)");
		reqs.add("Able to survive blue dragons, ogres, and greater demons attacking you");
		return reqs;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new SkillRequirement(Skill.MAGIC, 15));
		req.add(new SkillRequirement(Skill.THIEVING, 15));
		req.add(new SkillRequirement(Skill.AGILITY, 25, true));
		req.add(new SkillRequirement(Skill.HERBLORE, 14, true));
		req.add(new SkillRequirement(Skill.MINING, 40, true));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(4);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.MAGIC, 15250));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("5,000 Coins", ItemID.COINS_995, 5000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Ability to use the Watchtower Teleport"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Collections.singletonList(talkToWizard)));
		allSteps.add(new PanelDetails("Investigate", Arrays.asList(searchBush, talkToWizardAgain, talkToOg, useRopeOnBranch, talkToGrew, leaveGrewIsland, enterHoleSouthOfGuTanoth, killGorad, talkToToban,
			giveTobanDragonBones, searchChestForTobansGold, talkToOgAgain, useRopeOnBranchAgain, talkToGrewAgain, talkToWizardWithRelic), dragonBones, rope2));
		allSteps.add(new PanelDetails("Enter Gu'Tanoth", Arrays.asList(enterGuTanoth, stealRockCake, talkToGuardBattlement, talkToGuardWithRockCake, jumpGap, talkToCityGuard, talkToCityGuardAgain), ogreRelic, coins20, deathRune, goldBar, lightSource));
		allSteps.add(new PanelDetails("Learn the Skavid language", Arrays.asList(enterScaredSkavidCave, talkToScaredSkavid, enterSkavid1Cave, tryToGoThroughToInsaneSkavid, enterInsaneSkavidCave, talkToInsaneSkavid,
			pickUp2Nightshade, useNightshadeOnGuard), goldBar, lightSource, skavidMap));
		allSteps.add(new PanelDetails("Getting the other crystals", Arrays.asList(talkToWizardAgainEnclave, useJangerberriesOnGuam, grindBatBones, useBonesOnPotion, talkToWizardWithPotion, useNightshadeOnGuardAgain, usePotionOnOgre1, mineRock, talkToWizardWithCrystals, useCrystal1, pullLever),
			guamUnf, jangerberries, pestleAndMortar, batBones, nightshade, pickaxe));
		return allSteps;
	}
}
