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
package com.questhelper.helpers.quests.onesmallfavour;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
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
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.ONE_SMALL_FAVOUR
)
public class OneSmallFavour extends BasicQuestHelper
{
	//Items Required
	ItemRequirement steelBars4, steelBars3, steelBar, bronzeBar, ironBar, chisel, guam2, guam, marrentill, harralander, hammer, hammerHighlight, emptyCup, pigeonCages5, pot, hotWaterBowl, softClay,
		opal, jade, sapphire, redTopaz, bluntAxe, herbalTincture, guthixRest, uncutSapphire, cupOfWater,
		uncutOpal, uncutJade, uncutRedTopaz, stodgyMattress, mattress, animateRockScroll, animateRockScrollHighlight, ironOxide, brokenVane1, brokenVane2, brokenVane3, ornament,
		weathervanePillar, directionals, weatherReport, unfiredPotLid, potLid, potWithLid, breathingSalts, chickenCages5, sharpenedAxe, redMahog;

	ItemRequirement guamTea, guam2Tea, harrTea, marrTea, guamMarrTea, guamHarrTea, harrMarrTea, guam2MarrTea,
		guam2HarrTea, guamHarrMarrTea, herbTeaMix;

	//Items Recommended
	ItemRequirement varrockTeleports, lumbridgeTeleports, ardougneTeleports, camelotTeleports, faladorTeleports, pickaxe;

	Requirement inSanfewRoom, inHamBase, inDwarvenMine, inGoblinCave, lamp1Empty, lamp1Full, lamp2Empty, lamp2Full,
		lamp3Empty, lamp3Full, lamp4Empty, lamp4Full, lamp5Empty, lamp5Full, lamp6Empty, lamp6Full, lamp7Empty, lamp7Full, lamp8Empty, lamp8Full, allEmpty, allFull, inScrollSpot,
		slagilithNearby, petraNearby, inSeersVillageUpstairs, onRoof, addedOrnaments, addedDirectionals, addedWeathervanePillar, hasOrUsedOrnament, hasOrUsedDirectionals,
		hasOrUsedWeathervanePillar;

	DetailedQuestStep talkToYanni, talkToJungleForester, talkToBrian, talkToAggie, goDownToJohanhus, talkToJohanhus, talkToFred, talkToSeth, talkToHorvik,
		talkToApoth, talkToTassie, goDownToHammerspike, talkToHammerspike, goUpToSanfew, talkToSanfew, makeGuthixRest, talkToBleemadge, talkToArhein, talkToPhantuwti, enterGoblinCave,
		searchWall, talkToCromperty, talkToTindel, talkToRantz, talkToGnormadium, talkToBleemadgeNoTea, take1, take2, take3, take4, take5, take6, take7, take8,
		cutSaph, cutJade, cutTopaz, cutOpal, put1, put2, put3, put4, put5, put6, put7, put8, talkToGnormadiumAgain, returnToRantz, returnToTindel, returnToCromperty, getPigeonCages,
		enterGoblinCaveAgain, standNextToSculpture, readScroll, killSlagilith, readScrollAgain, talkToPetra, returnToPhantuwti, goUpLadder, goUpToRoof, searchVane, useHammerOnVane,
		goDownFromRoof, goDownLadderToSeers, useVane123OnAnvil, useVane2OnAnvil, useVane3OnAnvil, goBackUpLadder, goBackUpToRoof, useVane1, useVane2, useVane3,
		goFromRoofToPhantuwti, goDownLadderToPhantuwti, finishWithPhantuwti, returnToArhein, returnToBleemadge, returnToSanfew, goDownToHammerspikeAgain, returnToHammerspike,
		killGangMembers, talkToHammerspikeFinal, returnToTassie, spinPotLid, pickUpPot, firePotLid, usePotLidOnPot, returnToApothecary, returnToHorvik, talkToHorvikFinal, returnToSeth,
		returnDownToJohnahus, returnToJohnahus, returnToAggie, returnToBrian, returnToForester, returnToYanni, returnUpToSanfew, returnToPhantuwti2, useVane12OnAnvil, useVane13OnAnvil,
		useVane23OnAnvil, useVane1OnAnvil, fixAllLamps, searchVaneAgain;

	DetailedQuestStep useBowlOnCup, useHerbsOnCup;

	//Zones
	Zone sanfewRoom, hamBase, dwarvenMine, goblinCave, scrollSpot, seersVillageUpstairs, roof;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToYanni);
		steps.put(5, talkToJungleForester);

		steps.put(10, talkToBrian);
		steps.put(15, talkToBrian);

		steps.put(20, talkToAggie);

		ConditionalStep goTalkToJohanhus = new ConditionalStep(this, goDownToJohanhus);
		goTalkToJohanhus.addStep(inHamBase, talkToJohanhus);

		steps.put(25, goTalkToJohanhus);
		steps.put(30, goTalkToJohanhus);
		steps.put(35, goTalkToJohanhus);
		steps.put(40, goTalkToJohanhus);

		steps.put(45, talkToFred);
		steps.put(50, talkToSeth);
		steps.put(55, talkToHorvik);
		steps.put(60, talkToApoth);
		steps.put(62, talkToApoth);
		steps.put(63, talkToApoth);

		steps.put(65, talkToTassie);

		ConditionalStep goTalkToHammerspike = new ConditionalStep(this, goDownToHammerspike);
		goTalkToHammerspike.addStep(inDwarvenMine, talkToHammerspike);
		steps.put(70, goTalkToHammerspike);

		ConditionalStep goTalkToSanfew = new ConditionalStep(this, goUpToSanfew);
		goTalkToSanfew.addStep(inSanfewRoom, talkToSanfew);

		steps.put(75, goTalkToSanfew);

		ConditionalStep makeGuthixRestForGnome = new ConditionalStep(this, useBowlOnCup);
		makeGuthixRestForGnome.addStep(guthixRest, talkToBleemadge);
		makeGuthixRestForGnome.addStep(new Conditions(LogicType.OR, herbTeaMix, cupOfWater), useHerbsOnCup);

		steps.put(80, makeGuthixRestForGnome);
		steps.put(81, makeGuthixRestForGnome);
		steps.put(82, makeGuthixRestForGnome);
		steps.put(83, makeGuthixRestForGnome);
		steps.put(84, makeGuthixRestForGnome);

		steps.put(86, talkToBleemadgeNoTea);

		steps.put(88, talkToArhein);

		steps.put(90, talkToPhantuwti);

		ConditionalStep investigateWall = new ConditionalStep(this, enterGoblinCave);
		investigateWall.addStep(inGoblinCave, searchWall);

		steps.put(95, investigateWall);

		steps.put(100, talkToCromperty);

		steps.put(105, talkToTindel);

		steps.put(110, talkToRantz);

		steps.put(115, talkToGnormadium);

		ConditionalStep repairLights = new ConditionalStep(this, take1);
		repairLights.addStep(new Conditions(lamp1Full, lamp2Full, lamp3Full, lamp4Full, lamp5Full, lamp6Full, lamp7Full, lamp8Empty, sapphire), put8);
		repairLights.addStep(new Conditions(lamp1Full, lamp2Full, lamp3Full, lamp4Full, lamp5Full, lamp6Full, lamp7Full, lamp8Empty), cutSaph);
		repairLights.addStep(new Conditions(lamp1Full, lamp2Full, lamp3Full, lamp4Full, lamp5Full, lamp6Full, lamp7Full), take8);
		repairLights.addStep(new Conditions(lamp1Full, lamp2Full, lamp3Full, lamp4Full, lamp5Full, lamp6Full, lamp7Empty, opal), put7);
		repairLights.addStep(new Conditions(lamp1Full, lamp2Full, lamp3Full, lamp4Full, lamp5Full, lamp6Full, lamp7Empty), cutOpal);
		repairLights.addStep(new Conditions(lamp1Full, lamp2Full, lamp3Full, lamp4Full, lamp5Full, lamp6Full), take7);
		repairLights.addStep(new Conditions(lamp1Full, lamp2Full, lamp3Full, lamp4Full, lamp5Full, lamp6Empty, redTopaz),
			put6);
		repairLights.addStep(new Conditions(lamp1Full, lamp2Full, lamp3Full, lamp4Full, lamp5Full, lamp6Empty), cutTopaz);
		repairLights.addStep(new Conditions(lamp1Full, lamp2Full, lamp3Full, lamp4Full, lamp5Full), take6);
		repairLights.addStep(new Conditions(lamp1Full, lamp2Full, lamp3Full, lamp4Full, lamp5Empty, jade), put5);
		repairLights.addStep(new Conditions(lamp1Full, lamp2Full, lamp3Full, lamp4Full, lamp5Empty), cutJade);
		repairLights.addStep(new Conditions(lamp1Full, lamp2Full, lamp3Full, lamp4Full), take5);
		repairLights.addStep(new Conditions(lamp1Full, lamp2Full, lamp3Full, lamp4Empty, sapphire), put4);
		repairLights.addStep(new Conditions(lamp1Full, lamp2Full, lamp3Full, lamp4Empty), cutSaph);
		repairLights.addStep(new Conditions(lamp1Full, lamp2Full, lamp3Full), take4);
		repairLights.addStep(new Conditions(lamp1Full, lamp2Full, lamp3Empty, opal), put3);
		repairLights.addStep(new Conditions(lamp1Full, lamp2Full, lamp3Empty), cutOpal);
		repairLights.addStep(new Conditions(lamp1Full, lamp2Full), take3);
		repairLights.addStep(new Conditions(lamp1Full, lamp2Empty, redTopaz), put2);
		repairLights.addStep(new Conditions(lamp1Full, lamp2Empty), cutTopaz);
		repairLights.addStep(lamp1Full, take2);
		repairLights.addStep(new Conditions(lamp1Empty, jade), put1);
		repairLights.addStep(lamp1Empty, cutJade);

		steps.put(120, repairLights);

		steps.put(125, talkToGnormadiumAgain);

		steps.put(130, returnToRantz);

		steps.put(135, returnToTindel);

		steps.put(140, returnToCromperty);

		ConditionalStep fightSlagilith = new ConditionalStep(this, getPigeonCages);
		fightSlagilith.addStep(slagilithNearby, killSlagilith);
		fightSlagilith.addStep(inScrollSpot, readScroll);
		fightSlagilith.addStep(inGoblinCave, standNextToSculpture);
		fightSlagilith.addStep(pigeonCages5.alsoCheckBank(questBank), enterGoblinCaveAgain);

		steps.put(145, fightSlagilith);
		steps.put(150, fightSlagilith);

		ConditionalStep freePetra = new ConditionalStep(this, getPigeonCages);
		freePetra.addStep(petraNearby, talkToPetra);
		freePetra.addStep(inScrollSpot, readScroll);
		freePetra.addStep(inGoblinCave, standNextToSculpture);
		freePetra.addStep(pigeonCages5.alsoCheckBank(questBank), enterGoblinCaveAgain);

		steps.put(152, freePetra);
		steps.put(155, freePetra);

		steps.put(160, returnToPhantuwti);
		steps.put(165, returnToPhantuwti2);
		steps.put(170, returnToPhantuwti2);

		ConditionalStep repairVane = new ConditionalStep(this, goUpLadder);
		repairVane.addStep(onRoof, searchVane);
		repairVane.addStep(inSeersVillageUpstairs, goUpToRoof);

		steps.put(175, repairVane);

		ConditionalStep hitVane = new ConditionalStep(this, goUpLadder);
		hitVane.addStep(onRoof, useHammerOnVane);
		hitVane.addStep(inSeersVillageUpstairs, goUpToRoof);

		steps.put(176, hitVane);

		ConditionalStep getVaneBits = new ConditionalStep(this, goUpLadder);
		getVaneBits.addStep(onRoof, searchVaneAgain);
		getVaneBits.addStep(inSeersVillageUpstairs, goUpToRoof);

		steps.put(177, getVaneBits);

		ConditionalStep repairVaneParts = new ConditionalStep(this, useVane123OnAnvil);

		repairVaneParts.addStep(new Conditions(addedOrnaments, addedDirectionals, weathervanePillar, onRoof), useVane3);
		repairVaneParts.addStep(new Conditions(addedOrnaments, directionals, onRoof), useVane2);
		repairVaneParts.addStep(new Conditions(ornament, onRoof), useVane1);
		repairVaneParts.addStep(onRoof, goDownFromRoof);
		repairVaneParts.addStep(new Conditions(hasOrUsedDirectionals, hasOrUsedOrnament, hasOrUsedWeathervanePillar, inSeersVillageUpstairs), goBackUpToRoof);
		repairVaneParts.addStep(inSeersVillageUpstairs, goDownLadderToSeers);
		repairVaneParts.addStep(new Conditions(hasOrUsedDirectionals, hasOrUsedOrnament, hasOrUsedWeathervanePillar), goBackUpLadder);
		repairVaneParts.addStep(new Conditions(hasOrUsedDirectionals, hasOrUsedOrnament), useVane3OnAnvil);
		repairVaneParts.addStep(new Conditions(hasOrUsedOrnament, hasOrUsedWeathervanePillar), useVane1OnAnvil);
		repairVaneParts.addStep(new Conditions(hasOrUsedDirectionals, hasOrUsedWeathervanePillar), useVane2OnAnvil);
		repairVaneParts.addStep(hasOrUsedOrnament, useVane13OnAnvil);
		repairVaneParts.addStep(hasOrUsedWeathervanePillar, useVane12OnAnvil);
		repairVaneParts.addStep(hasOrUsedDirectionals, useVane23OnAnvil);

		steps.put(180, repairVaneParts);

		ConditionalStep reportBackToPhantuwti = new ConditionalStep(this, finishWithPhantuwti);
		reportBackToPhantuwti.addStep(inSeersVillageUpstairs, goDownLadderToPhantuwti);
		reportBackToPhantuwti.addStep(onRoof, goFromRoofToPhantuwti);

		steps.put(185, reportBackToPhantuwti);

		steps.put(190, returnToArhein);

		steps.put(195, returnToBleemadge);

		ConditionalStep goAndReturnToSanfew = new ConditionalStep(this, returnUpToSanfew);
		goAndReturnToSanfew.addStep(inSanfewRoom, returnToSanfew);
		steps.put(200, goAndReturnToSanfew);

		ConditionalStep dealWithHammerspike = new ConditionalStep(this, goDownToHammerspikeAgain);
		dealWithHammerspike.addStep(inDwarvenMine, returnToHammerspike);

		steps.put(205, dealWithHammerspike);

		ConditionalStep sortOutGangMembers = new ConditionalStep(this, goDownToHammerspikeAgain);
		sortOutGangMembers.addStep(inDwarvenMine, killGangMembers);

		steps.put(210, sortOutGangMembers);
		steps.put(215, sortOutGangMembers);
		steps.put(220, sortOutGangMembers);
		steps.put(225, dealWithHammerspike);

		steps.put(230, returnToTassie);

		ConditionalStep makePotAndReturnToApoth = new ConditionalStep(this, spinPotLid);
		makePotAndReturnToApoth.addStep(potWithLid, returnToApothecary);
		makePotAndReturnToApoth.addStep(new Conditions(potLid, pot), usePotLidOnPot);
		makePotAndReturnToApoth.addStep(potLid, pickUpPot);
		makePotAndReturnToApoth.addStep(unfiredPotLid, firePotLid);

		steps.put(235, makePotAndReturnToApoth);

		steps.put(240, returnToHorvik);

		steps.put(245, talkToHorvikFinal);

		steps.put(250, returnToSeth);

		ConditionalStep goFinishWithJohanhus = new ConditionalStep(this, returnDownToJohnahus);
		goFinishWithJohanhus.addStep(inHamBase, returnToJohnahus);

		steps.put(255, goFinishWithJohanhus);

		steps.put(260, returnToAggie);

		steps.put(265, returnToBrian);

		steps.put(270, returnToForester);

		steps.put(275, returnToYanni);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		steelBars4 = new ItemRequirement("Steel bar", ItemID.STEEL_BAR, 4);
		steelBars3 = new ItemRequirement("Steel bar", ItemID.STEEL_BAR, 3);
		steelBar = new ItemRequirement("Steel bar", ItemID.STEEL_BAR);
		softClay = new ItemRequirement("Soft clay", ItemID.SOFT_CLAY);
		softClay.setHighlightInInventory(true);
		chisel = new ItemRequirement("Chisel", ItemID.CHISEL).isNotConsumed();
		chisel.setHighlightInInventory(true);
		bronzeBar = new ItemRequirement("Bronze bar", ItemID.BRONZE_BAR);
		ironBar = new ItemRequirement("Iron bar", ItemID.IRON_BAR);
		guam2 = new ItemRequirement("Guam leaf", ItemID.GUAM_LEAF, 2);
		guam = new ItemRequirement("Guam leaf", ItemID.GUAM_LEAF);
		marrentill = new ItemRequirement("Marrentill", ItemID.MARRENTILL);
		harralander = new ItemRequirement("Harralander", ItemID.HARRALANDER);
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed();

		hammerHighlight = hammer.highlighted();
		emptyCup = new ItemRequirement("Empty cup", ItemID.EMPTY_CUP);
		emptyCup.setTooltip("You can find a cup of tea in a house north of Sanfew. Drink it for an empty cup");
		pigeonCages5 = new ItemRequirement("Pigeon cages", ItemID.PIGEON_CAGE, 5);
		pigeonCages5.setTooltip("You can get more from outside a house in East Ardougne");
		pot = new ItemRequirement("Pot", ItemID.POT);
		pot.setHighlightInInventory(true);
		hotWaterBowl = new ItemRequirement("Bowl of hot water", ItemID.BOWL_OF_HOT_WATER);
		hotWaterBowl.setTooltip("You can find a bowl in Lumbridge castle. Fill it up, then boil it on the range");
		varrockTeleports = new ItemRequirement("Teleports to Varrock", ItemID.VARROCK_TELEPORT, 2);
		faladorTeleports = new ItemRequirement("Teleports to Falador", ItemID.FALADOR_TELEPORT, 2);
		ardougneTeleports = new ItemRequirement("Teleports to Ardougne", ItemID.ARDOUGNE_TELEPORT, 2);
		camelotTeleports = new ItemRequirement("Teleports to Camelot", ItemID.CAMELOT_TELEPORT, 2);
		lumbridgeTeleports = new ItemRequirement("Teleports to Lumbridge", ItemID.LUMBRIDGE_TELEPORT, 2);
		pickaxe = new ItemRequirement("Any pickaxe to kill Slagilith", ItemCollections.PICKAXES).isNotConsumed();

		bluntAxe = new ItemRequirement("Blunt axe", ItemID.BLUNT_AXE);
		bluntAxe.setTooltip("You can get another from a Jungle Forester south of Shilo Village");
		herbalTincture = new ItemRequirement("Herbal tincture", ItemID.HERBAL_TINCTURE);
		cupOfWater = new ItemRequirement("Cup of hot water", ItemID.CUP_OF_HOT_WATER);

		harrTea = new ItemRequirement("Herb tea mix (harralander)", ItemID.HERB_TEA_MIX);
		guamTea = new ItemRequirement("Herb tea mix (guam)", ItemID.HERB_TEA_MIX_4466);
		marrTea =  new ItemRequirement("Herb tea mix (marrentill)", ItemID.HERB_TEA_MIX_4468);
		harrMarrTea = new ItemRequirement("Herb tea mix (harr/marr)", ItemID.HERB_TEA_MIX_4470);
		guamHarrTea =  new ItemRequirement("Herb tea mix (harr/guam)", ItemID.HERB_TEA_MIX_4472);
		guam2Tea = new ItemRequirement("Herb tea mix (2 guam)", ItemID.HERB_TEA_MIX_4474);
		guamMarrTea = new ItemRequirement("Herb tea mix (marr/guam)", ItemID.HERB_TEA_MIX_4476);
		guamHarrMarrTea = new ItemRequirement("Herb tea mix (harr/marr/guam)", ItemID.HERB_TEA_MIX_4478);
		guam2MarrTea = new ItemRequirement("Herb tea mix (2 guam/marr)", ItemID.HERB_TEA_MIX_4480);
		guam2HarrTea = new ItemRequirement("Herb tea mix (2 guam/harr)", ItemID.HERB_TEA_MIX_4482);
		herbTeaMix = new ItemRequirement("Herb tea mix", ItemID.HERB_TEA_MIX);
		herbTeaMix.addAlternates(ItemID.HERB_TEA_MIX_4466, ItemID.HERB_TEA_MIX_4468, ItemID.HERB_TEA_MIX_4470,
			ItemID.HERB_TEA_MIX_4472, ItemID.HERB_TEA_MIX_4474, ItemID.HERB_TEA_MIX_4476,
			ItemID.HERB_TEA_MIX_4478, ItemID.HERB_TEA_MIX_4480, ItemID.HERB_TEA_MIX_4482);

		guthixRest = new ItemRequirement("Guthix rest(3)", ItemID.GUTHIX_REST3);

		sapphire = new ItemRequirement("Sapphire", ItemID.SAPPHIRE);
		sapphire.setHighlightInInventory(true);
		opal = new ItemRequirement("Opal", ItemID.OPAL);
		opal.setHighlightInInventory(true);
		jade = new ItemRequirement("Jade", ItemID.JADE);
		jade.setHighlightInInventory(true);
		redTopaz = new ItemRequirement("Red topaz", ItemID.RED_TOPAZ);
		redTopaz.setHighlightInInventory(true);

		uncutSapphire = new ItemRequirement("Uncut sapphire", ItemID.UNCUT_SAPPHIRE);
		uncutSapphire.setHighlightInInventory(true);
		uncutOpal = new ItemRequirement("Uncut opal", ItemID.UNCUT_OPAL);
		uncutOpal.setHighlightInInventory(true);
		uncutOpal.setTooltip("If you crushed it, you can buy another from Gnormadium for 500 gp");
		uncutJade = new ItemRequirement("Uncut jade", ItemID.UNCUT_JADE);
		uncutJade.setTooltip("If you crushed it, you can buy another from Gnormadium for 500 gp");
		uncutJade.setHighlightInInventory(true);
		uncutRedTopaz = new ItemRequirement("Uncut red topaz", ItemID.UNCUT_RED_TOPAZ);
		uncutRedTopaz.setTooltip("If you crushed it, you can buy another from Gnormadium for 500 gp");
		uncutRedTopaz.setHighlightInInventory(true);

		stodgyMattress = new ItemRequirement("Stodgy mattress", ItemID.STODGY_MATTRESS);
		stodgyMattress.setTooltip("You can buy another from Tindel Marchant in Port Khazard for 100 gp");

		mattress = new ItemRequirement("Comfy mattress", ItemID.COMFY_MATTRESS);
		mattress.setTooltip("You can get another stodgy mattress from Tindel Marchant for 100 gp, then get it filled by Rantz");

		animateRockScroll = new ItemRequirement("Animate rock scroll", ItemID.ANIMATE_ROCK_SCROLL);
		animateRockScroll.setTooltip("You can get another from Wizard Cromperty for 100 gp");

		animateRockScrollHighlight = new ItemRequirement("Animate rock scroll", ItemID.ANIMATE_ROCK_SCROLL);
		animateRockScrollHighlight.setHighlightInInventory(true);
		animateRockScrollHighlight.setTooltip("You can get another from Wizard Cromperty for 100 gp");

		ironOxide = new ItemRequirement("Iron oxide", ItemID.IRON_OXIDE);
		ironOxide.setTooltip("You can buy another from Tindel Marchant for 200 gp");

		brokenVane1 = new ItemRequirement("Broken vane part", ItemID.BROKEN_VANE_PART);
		brokenVane1.setHighlightInInventory(true);
		brokenVane1.setTooltip("You can get another from Phantuwti for 335 gp");
		directionals = new ItemRequirement("Directionals", ItemID.DIRECTIONALS);
		directionals.setHighlightInInventory(true);
		brokenVane2 = new ItemRequirement("Broken vane part", ItemID.BROKEN_VANE_PART_4431);
		brokenVane2.setTooltip("You can get another from Phantuwti for 335 gp");
		brokenVane2.setHighlightInInventory(true);
		ornament = new ItemRequirement("Ornament", ItemID.ORNAMENT);
		ornament.setHighlightInInventory(true);
		brokenVane3 = new ItemRequirement("Broken vane part", ItemID.BROKEN_VANE_PART_4433);
		brokenVane3.setTooltip("You can get another from Phantuwti for 335 gp");
		brokenVane3.setHighlightInInventory(true);
		weathervanePillar = new ItemRequirement("Weathervane pillar", ItemID.WEATHERVANE_PILLAR);
		weathervanePillar.setHighlightInInventory(true);

		weatherReport = new ItemRequirement("Weather report", ItemID.WEATHER_REPORT);
		weatherReport.setTooltip("You can get another from Phantuwti in Seers' Village");

		potLid = new ItemRequirement("Pot lid", ItemID.POT_LID);
		potLid.setHighlightInInventory(true);
		unfiredPotLid = new ItemRequirement("Unfired pot lid", ItemID.UNFIRED_POT_LID);
		potWithLid = new ItemRequirement("Airtight pot", ItemID.AIRTIGHT_POT);

		breathingSalts = new ItemRequirement("Breathing salts", ItemID.BREATHING_SALTS);
		breathingSalts.setTooltip("You can get more by bringing the Apothecary another airtight pot and 200 gp");

		chickenCages5 = new ItemRequirement("Chicken cage", ItemID.CHICKEN_CAGE, 5);
		chickenCages5.setTooltip("You can get more chicken cages by bringing Horvik a pidgeon cage and 100 coins per cage");

		sharpenedAxe = new ItemRequirement("Sharpened axe", ItemID.SHARPENED_AXE);
		sharpenedAxe.setTooltip("You can get another from Brian in Port Sarim");

		redMahog = new ItemRequirement("Red mahogany log", ItemID.RED_MAHOGANY_LOG);
		redMahog.setTooltip("You can get another from a jungle forester for 200 gp");
	}

	public void loadZones()
	{
		sanfewRoom = new Zone(new WorldPoint(2893, 3423, 1), new WorldPoint(2903, 3433, 1));
		hamBase = new Zone(new WorldPoint(3140, 9600, 0), new WorldPoint(3190, 9655, 0));
		dwarvenMine = new Zone(new WorldPoint(2960, 9696, 0), new WorldPoint(3062, 9854, 0));
		goblinCave = new Zone(new WorldPoint(2560, 9792, 0), new WorldPoint(2623, 9855, 0));
		scrollSpot = new Zone(new WorldPoint(2616, 9835, 0), new WorldPoint(2619, 9835, 0));
		seersVillageUpstairs = new Zone(new WorldPoint(2698, 3468, 1), new WorldPoint(2717, 3476, 1));
		roof = new Zone(new WorldPoint(2695, 3469, 3), new WorldPoint(2716, 3476, 3));
	}

	public void setupConditions()
	{
		inSanfewRoom = new ZoneRequirement(sanfewRoom);
		inHamBase = new ZoneRequirement(hamBase);
		inDwarvenMine = new ZoneRequirement(dwarvenMine);
		inGoblinCave = new ZoneRequirement(goblinCave);

		lamp1Empty = new VarbitRequirement(6225, 1);
		lamp2Empty = new VarbitRequirement(6226, 1);
		lamp3Empty = new VarbitRequirement(6227, 1);
		lamp4Empty = new VarbitRequirement(6228, 1);
		lamp5Empty = new VarbitRequirement(6229, 1);
		lamp6Empty = new VarbitRequirement(6230, 1);
		lamp7Empty = new VarbitRequirement(6231, 1);
		lamp8Empty = new VarbitRequirement(6232, 1);

		allEmpty = new VarbitRequirement(244, 255);

		lamp1Full = new VarbitRequirement(6233, 1);
		lamp2Full = new VarbitRequirement(6234, 1);
		lamp3Full = new VarbitRequirement(6235, 1);
		lamp4Full = new VarbitRequirement(6236, 1);
		lamp5Full = new VarbitRequirement(6237, 1);
		lamp6Full = new VarbitRequirement(6238, 1);
		lamp7Full = new VarbitRequirement(6239, 1);
		lamp8Full = new VarbitRequirement(6240, 1);

		allFull = new VarbitRequirement(6241, 255);

		slagilithNearby = new NpcCondition(NpcID.SLAGILITH);
		inScrollSpot = new ZoneRequirement(scrollSpot);
		inSeersVillageUpstairs = new ZoneRequirement(seersVillageUpstairs);
		onRoof = new ZoneRequirement(roof);

		petraNearby = new NpcCondition(NpcID.PETRA_FIYED);

		addedOrnaments = new VarbitRequirement(255, 1);
		addedDirectionals = new VarbitRequirement(254, 1);
		addedWeathervanePillar = new VarbitRequirement(253, 1);

		hasOrUsedDirectionals = new Conditions(LogicType.OR, addedDirectionals, directionals.alsoCheckBank(questBank));
		hasOrUsedOrnament = new Conditions(LogicType.OR, addedOrnaments, ornament.alsoCheckBank(questBank));
		hasOrUsedWeathervanePillar = new Conditions(LogicType.OR, addedWeathervanePillar, weathervanePillar.alsoCheckBank(questBank));
	}

	public void setupSteps()
	{
		talkToYanni = new NpcStep(this, NpcID.YANNI_SALIKA, new WorldPoint(2836, 2983, 0), "Talk to Yanni Salika in Shilo Village.");
		talkToYanni.addDialogSteps("Is there anything else interesting to do around here?", "Ok, see you in a tick!");
		talkToJungleForester = new NpcStep(this, NpcID.JUNGLE_FORESTER, new WorldPoint(2861, 2942, 0), "Talk to a Jungle Forester south of Shilo Village.", bluntAxe);
		talkToJungleForester.addDialogSteps("I'll get going then!", "I need to talk to you about red mahogany.");
		talkToJungleForester.addDialogStep("Okay, I'll take your axe to get it sharpened.");
		talkToBrian = new NpcStep(this, NpcID.BRIAN, new WorldPoint(3027, 3249, 0), "Talk to Brian in the Port Sarim axe shop.");
		talkToBrian.addDialogStep("Do you sharpen axes?");
		talkToBrian.addDialogStepWithExclusion("Look, can you sharpen this cursed axe or what?", "Ok, ok, I'll do it! I'll go and see Aggie.");
		talkToBrian.addDialogStep("Ok, ok, I'll do it! I'll go and see Aggie.");

		talkToAggie = new NpcStep(this, NpcID.AGGIE, new WorldPoint(3086, 3258, 0), "Talk to Aggie in Draynor Village.");
		talkToAggie.addDialogStep("Could I ask you about being a character witness?");
		talkToAggie.addDialogStep("Let me guess, you're going to ask me to do you a favour?");
		talkToAggie.addDialogStep("Oh, Ok, I'll see if I can find Jimmy.");

		goDownToJohanhus = new ObjectStep(this, NullObjectID.NULL_5492, new WorldPoint(3166, 3252, 0), "Enter the H.A.M hideout west of Lumbridge and talk to Johanhus Ulsbrecht in there.");
		talkToJohanhus = new NpcStep(this, NpcID.JOHANHUS_ULSBRECHT, new WorldPoint(3171, 9619, 0), "Talk to Johanhus Ulsbrecht in the south of the H.A.M hideout.");
		talkToJohanhus.addDialogStep("I'm looking for Jimmy the Chisel.");
		talkToJohanhus.addDialogStep("And I suppose you need me to do you a favour?");
		talkToJohanhus.addDialogStep("Ok, Jimmy has to be worth more than a few scrawny chickens!");

		talkToJohanhus.addSubSteps(goDownToJohanhus);

		talkToFred = new NpcStep(this, NpcID.FRED_THE_FARMER, new WorldPoint(3190, 3273, 0), "Talk to Fred the Farmer north of Lumbridge.");
		talkToFred.addDialogStep("I need to talk to you about Jimmy.");

		talkToSeth = new NpcStep(this, NpcID.SETH_GROATS, new WorldPoint(3228, 3291, 0), "Talk to Seth Groats in the farm north east of Lumbridge, across the river.");
		talkToSeth.addDialogStep("Oh, ok! I guess it's not that much further to Varrock!");
		talkToHorvik = new NpcStep(this, NpcID.HORVIK, new WorldPoint(3229, 3437, 0), "Talk to Horvik in the armour shop north east of the Varrock square.", steelBars3);
		talkToHorvik.addDialogStep("Hi, I need to talk to you about chicken cages!");
		talkToHorvik.addDialogStep("Ok, I guess one good turn deserves another.");
		talkToApoth = new NpcStep(this, NpcID.APOTHECARY, new WorldPoint(3196, 3404, 0), "Talk to the Apothecary in west Varrock.");
		talkToApoth.addDialogStep("Talk about One Small Favour.");
		talkToApoth.addDialogStep("Oh, ok, I guess it's not that far to the Barbarian Village.");
		talkToApoth.addDialogStep("I guess I can go to the Barbarian Village.");

		talkToTassie = new NpcStep(this, NpcID.TASSIE_SLIPCAST, new WorldPoint(3085, 3409, 0), "Talk to Tassie Slipcast in the Barbarian Village pottery building.");
		talkToTassie.addDialogStep("Ok, I'll deal with Hammerspike!");

		goDownToHammerspike = new ObjectStep(this, ObjectID.TRAPDOOR_11867, new WorldPoint(3019, 3450, 0), "Go into the Dwarven Mine and talk to Hammerspike Stoutbeard in the west side.");
		talkToHammerspike = new NpcStep(this, NpcID.HAMMERSPIKE_STOUTBEARD, new WorldPoint(2968, 9811, 0), "Talk to Hammerspike Stoutbeard in the west cavern of the Dwarven Mine.");
		talkToHammerspike.addDialogStep("Have you always been a gangster?");
		talkToHammerspike.addDialogStep("Ok, another favour...I think I can manage that.");
		talkToHammerspike.addSubSteps(goDownToHammerspike);

		goUpToSanfew = new ObjectStep(this, ObjectID.STAIRCASE_16671, new WorldPoint(2899, 3429, 0), "Talk to Sanfew upstairs in the Taverley herblore store.");
		talkToSanfew = new NpcStep(this, NpcID.SANFEW, new WorldPoint(2899, 3429, 1), "Talk to Sanfew upstairs in the Taverley herblore store.");
		talkToSanfew.addDialogStep("Are you taking any new initiates?");
		talkToSanfew.addDialogStepWithExclusion("Do you accept dwarves?", "A dwarf I know wants to become an initiate.");
		talkToSanfew.addDialogSteps("A dwarf I know wants to become an initiate.", "Yep, it's a deal.");
		talkToSanfew.addSubSteps(goUpToSanfew);

		useBowlOnCup = new DetailedQuestStep(this, "Use a bowl of hot water on an empty cup.",
			hotWaterBowl.highlighted(), emptyCup.highlighted());
		useHerbsOnCup = new DetailedQuestStep(this,
			"Use 2 guams, a marrentill and a harralander on the cup.",
			guam2.hideConditioned(new Conditions(LogicType.OR, guamTea, guam2Tea, guamMarrTea, guamHarrTea,
				guamHarrMarrTea)).highlighted(),
			guam.hideConditioned(new Conditions(LogicType.OR, cupOfWater, marrTea, harrTea, guam2Tea, guam2MarrTea,
				guam2HarrTea)).highlighted(),
			marrentill.hideConditioned(new Conditions(LogicType.OR, marrTea, harrMarrTea, guamMarrTea, guam2MarrTea,
				guamHarrMarrTea)).highlighted(),
			harralander.hideConditioned(new Conditions(LogicType.OR, harrTea, harrMarrTea, guamHarrTea, guam2HarrTea,
				guamHarrMarrTea)).highlighted(),
			cupOfWater.hideConditioned(new Conditions(LogicType.OR, guamTea, harrTea, marrTea, harrMarrTea, guamHarrTea,
				guam2Tea, guam2MarrTea, guamMarrTea, guam2HarrTea, guamHarrMarrTea)).highlighted(),
		    herbTeaMix.hideConditioned(new Conditions(LogicType.NOR, guamTea, harrTea, marrTea, harrMarrTea,
				guamHarrTea, guam2Tea, guam2MarrTea, guamMarrTea, guam2HarrTea, guamHarrMarrTea)).highlighted());
		makeGuthixRest = new DetailedQuestStep(this, "Make Guthix Rest by using a bowl of hot water on an empty tea cup, then using 2 guams, a marrentill and a harralander on it.", emptyCup, hotWaterBowl, guam2, marrentill, harralander);
		makeGuthixRest.addSubSteps(useBowlOnCup, useHerbsOnCup);
		talkToBleemadge = new NpcStep(this, NpcID.CAPTAIN_BLEEMADGE, new WorldPoint(2847, 3498, 0), "Right-click talk to Captain Bleemadge on White Wolf Mountain.", guthixRest);
		((NpcStep) talkToBleemadge).addAlternateNpcs(NpcID.CAPTAIN_BLEEMADGE_10461, NpcID.CAPTAIN_BLEEMADGE_10462,
			NpcID.CAPTAIN_BLEEMADGE_10463, NpcID.CAPTAIN_BLEEMADGE_10464, NpcID.CAPTAIN_BLEEMADGE_10465,
			NpcID.CAPTAIN_BLEEMADGE_10466);
		talkToBleemadge.addDialogStep("I have a special tea here for you from Sanfew!");

		talkToBleemadgeNoTea = new NpcStep(this, NpcID.CAPTAIN_BLEEMADGE, new WorldPoint(2847, 3498, 0), "Right-click talk to Captain Bleemadge on White Wolf Mountain.");
		((NpcStep) talkToBleemadgeNoTea).addAlternateNpcs(NpcID.CAPTAIN_BLEEMADGE_10461, NpcID.CAPTAIN_BLEEMADGE_10462,
			NpcID.CAPTAIN_BLEEMADGE_10463, NpcID.CAPTAIN_BLEEMADGE_10464, NpcID.CAPTAIN_BLEEMADGE_10465,
			NpcID.CAPTAIN_BLEEMADGE_10466);
		talkToBleemadgeNoTea.addDialogStep("How was that tea?");
		talkToBleemadgeNoTea.addDialogStep("Ok, I'll go and get you some T.R.A.S.H.");

		talkToBleemadge.addSubSteps(talkToBleemadgeNoTea);

		talkToArhein = new NpcStep(this, NpcID.ARHEIN, new WorldPoint(2804, 3432, 0), "Talk to Arhein in Catherby.");
		talkToArhein.addDialogStep("I need to talk T.R.A.S.H to you.");
		talkToArhein.addDialogStep("Yes, Ok, I'll do it!");

		talkToPhantuwti = new NpcStep(this, NpcID.PHANTUWTI_FANSTUWI_FARSIGHT, new WorldPoint(2702, 3473, 0), "Talk to Phantuwti in the south west house of Seers' Village.");
		talkToPhantuwti.addDialogStep("Hi, can you give me a weather forecast?");
		talkToPhantuwti.addDialogStep("What can I do to help?");
		talkToPhantuwti.addDialogStep("Yes, Ok, I'll do it.");

		enterGoblinCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE, new WorldPoint(2624, 3393, 0), "Enter the cave south east of the Fishing Guild.");
		searchWall = new ObjectStep(this, ObjectID.SCULPTURE, new WorldPoint(2621, 9835, 0), "Right-click search the sculpture in the wall in the north east corner of the cave.");

		talkToCromperty = new NpcStep(this, NpcID.WIZARD_CROMPERTY, new WorldPoint(2684, 3323, 0), "Talk to Wizard Cromperty in north east Ardougne.");
		talkToCromperty.addDialogStep("I need to talk to you about a girl stuck in some rock!");
		talkToCromperty.addDialogStep("Oh! Ok, one more 'small favour' isn't going to kill me...I hope!");

		talkToTindel = new NpcStep(this, NpcID.TINDEL_MARCHANT, new WorldPoint(2678, 3153, 0), "Talk to Tindel Marchant in Port Khazard.");
		talkToTindel.addDialogSteps("Wizard Cromperty sent me to get some iron oxide.", "Ask about iron oxide.", "Okay, I'll do it!");

		talkToRantz = new NpcStep(this, NpcID.RANTZ, new WorldPoint(2631, 2969, 0), "Talk to Rantz in Feldip Hills.");
		talkToRantz.addDialogStep("I need to talk to you about a mattress.");
		talkToRantz.addDialogStep("Ok, I'll see what I can do.");

		talkToGnormadium = new NpcStep(this, NpcID.GNORMADIUM_AVLAFRIM, new WorldPoint(2542, 2968, 0), "Talk to Gnormadium Avlafrim west of Rantz.");
		talkToGnormadium.addDialogStep("Rantz said I should help you finish this project.");
		talkToGnormadium.addDialogStep("Yes, I'll take a look at them.");

		take1 = new ObjectStep(this, NullObjectID.NULL_5820, new WorldPoint(2554, 2974, 0), "Take a jade from the north east landing light.");
		take2 = new ObjectStep(this, NullObjectID.NULL_5822, new WorldPoint(2551, 2974, 0), "Take a red topaz from the landing light.");
		take3 = new ObjectStep(this, NullObjectID.NULL_5821, new WorldPoint(2548, 2974, 0), "Take an opal from the landing light.");
		take4 = new ObjectStep(this, NullObjectID.NULL_5823, new WorldPoint(2545, 2974, 0), "Take a sapphire from the landing light.");

		take5 = new ObjectStep(this, NullObjectID.NULL_5820, new WorldPoint(2554, 2969, 0), "Take a jade from the landing light.");
		take6 = new ObjectStep(this, NullObjectID.NULL_5822, new WorldPoint(2551, 2969, 0), "Take a red topaz from the landing light.");
		take7 = new ObjectStep(this, NullObjectID.NULL_5821, new WorldPoint(2548, 2969, 0), "Take an opal from the landing light.");
		take8 = new ObjectStep(this, NullObjectID.NULL_5823, new WorldPoint(2545, 2969, 0), "Take a sapphire from the landing light.");

		put1 = new ObjectStep(this, NullObjectID.NULL_5820, new WorldPoint(2554, 2974, 0), "Put a jade from the north east landing light.", jade);
		put1.addIcon(ItemID.JADE);
		put2 = new ObjectStep(this, NullObjectID.NULL_5822, new WorldPoint(2551, 2974, 0), "Put a red topaz from the landing light.", redTopaz);
		put2.addIcon(ItemID.RED_TOPAZ);
		put3 = new ObjectStep(this, NullObjectID.NULL_5821, new WorldPoint(2548, 2974, 0), "Put an opal from the landing light.", opal);
		put3.addIcon(ItemID.OPAL);
		put4 = new ObjectStep(this, NullObjectID.NULL_5823, new WorldPoint(2545, 2974, 0), "Put a sapphire from the landing light.", sapphire);
		put4.addIcon(ItemID.SAPPHIRE);

		put5 = new ObjectStep(this, NullObjectID.NULL_5820, new WorldPoint(2554, 2969, 0), "Put a jade from the landing light.", jade);
		put5.addIcon(ItemID.JADE);
		put6 = new ObjectStep(this, NullObjectID.NULL_5822, new WorldPoint(2551, 2969, 0), "Put a red topaz from the landing light.", redTopaz);
		put6.addIcon(ItemID.RED_TOPAZ);
		put7 = new ObjectStep(this, NullObjectID.NULL_5821, new WorldPoint(2548, 2969, 0), "Put an opal from the landing light.", opal);
		put7.addIcon(ItemID.OPAL);
		put8 = new ObjectStep(this, NullObjectID.NULL_5823, new WorldPoint(2545, 2969, 0), "Put a sapphire from the landing light.", sapphire);
		put8.addIcon(ItemID.SAPPHIRE);

		cutJade = new DetailedQuestStep(this, "Cut the uncut jade.", chisel, uncutJade);
		cutSaph = new DetailedQuestStep(this, "Cut the uncut sapphire.", chisel, uncutSapphire);
		cutOpal = new DetailedQuestStep(this, "Cut the uncut opal.", chisel, uncutOpal);
		cutTopaz = new DetailedQuestStep(this, "Cut the uncut red topaz.", chisel, uncutRedTopaz);

		fixAllLamps = new DetailedQuestStep(this, "Take the gems out of each of the gnome lamps, cut them, then put back in the cut gem.");
		fixAllLamps.addSubSteps(cutJade, cutOpal, cutSaph, cutTopaz, put1, put2, put3, put4, put5, put6, put7, put8, take1, take2, take3, take4, take5, take6, take7, take8);

		talkToGnormadiumAgain = new NpcStep(this, NpcID.GNORMADIUM_AVLAFRIM, new WorldPoint(2542, 2968, 0), "Talk to Gnormadium Avlafrim again.");
		talkToGnormadiumAgain.addDialogStep("I've fixed all the lights!");

		returnToRantz = new NpcStep(this, NpcID.RANTZ, new WorldPoint(2631, 2975, 0), "Return to Rantz in Feldip Hills.", stodgyMattress);
		returnToRantz.addDialogStep("Ok, I've helped that Gnome, he shouldn't bother you anymore.");
		returnToTindel = new NpcStep(this, NpcID.TINDEL_MARCHANT, new WorldPoint(2678, 3153, 0), "Return to Tindel Marchant in Port Khazard.", mattress);
		returnToTindel.addDialogStep("I have the mattress.");

		returnToCromperty = new NpcStep(this, NpcID.WIZARD_CROMPERTY, new WorldPoint(2684, 3323, 0), "Return to Wizard Cromperty in north east Ardougne.", ironOxide);
		returnToCromperty.addDialogStep("I have that iron oxide you asked for!");

		getPigeonCages = new DetailedQuestStep(this, new WorldPoint(2618, 3325, 0), "Get 5 pigeon cages from behind Jerico's house in central East Ardougne.", pigeonCages5);

		enterGoblinCaveAgain = new ObjectStep(this, ObjectID.CAVE_ENTRANCE, new WorldPoint(2624, 3393, 0), "Enter the cave south east of the Fishing Guild. " +
			"Be prepared to fight the Slagilith(level 92, takes reduced damage from anything other than pickaxes).", Arrays.asList(pigeonCages5, animateRockScroll), Collections.singletonList(pickaxe));
		standNextToSculpture = new DetailedQuestStep(this, new WorldPoint(2616, 9835, 0), "Use the animate rock scroll next to the sculpture in the north east cavern.", animateRockScroll);
		readScroll = new DetailedQuestStep(this, "Read the animate rock scroll.", animateRockScrollHighlight);

		standNextToSculpture.addSubSteps(readScroll);

		killSlagilith = new NpcStep(this, NpcID.SLAGILITH, new WorldPoint(2617, 9837, 0), "Kill the Slagilith. They take reduced damage from anything other than a pickaxe.",
			Collections.EMPTY_LIST, Collections.singletonList(pickaxe));
		readScrollAgain = new DetailedQuestStep(this, "Read the animate rock scroll", animateRockScrollHighlight);
		talkToPetra = new NpcStep(this, NpcID.PETRA_FIYED, new WorldPoint(2617, 9837, 0), "Talk to Petra Fiyed.");

		returnToPhantuwti = new NpcStep(this, NpcID.PHANTUWTI_FANSTUWI_FARSIGHT, new WorldPoint(2702, 3473, 0), "Return to Phantuwti in the south west house of Seers' Village.");
		returnToPhantuwti.addDialogStep("I've released Petra, she should have returned.");
		returnToPhantuwti.addDialogStep("I'll run you through if you don't give me that weather report.");
		returnToPhantuwti.addDialogStepWithExclusion("Why can't you get a clear picture?", "I'll run you through if you don't give me that weather report.");

		returnToPhantuwti2 = new NpcStep(this, NpcID.PHANTUWTI_FANSTUWI_FARSIGHT, new WorldPoint(2702, 3473, 0), "Return to Phantuwti in the south west house of Seers' Village.");
		returnToPhantuwti2.addDialogStep("I'll run you through if you don't give me that weather report.");
		returnToPhantuwti2.addDialogStepWithExclusion("Why can't you get a clear picture?", "I'll run you through if you don't give me that weather report.");
		returnToPhantuwti2.addDialogStep("Which special Seers tools do you mean?");
		returnToPhantuwti2.addDialogStep("What do you mean, 'special combination of items'?");

		returnToPhantuwti.addSubSteps(returnToPhantuwti2);

		goUpLadder = new ObjectStep(this, ObjectID.LADDER_25941, new WorldPoint(2699, 3476, 0), "Go up the ladder nearby.");
		goUpToRoof = new ObjectStep(this, ObjectID.LADDER_26118, new WorldPoint(2715, 3472, 1), "Go up to the roof.");
		searchVane = new ObjectStep(this, NullObjectID.NULL_5811, new WorldPoint(2702, 3476, 3), "Right-click search the weathervane on top of the Seers' building.");
		searchVane.addSubSteps(goUpLadder, goUpToRoof);
		useHammerOnVane = new ObjectStep(this, NullObjectID.NULL_5811, new WorldPoint(2702, 3476, 3), "Use a hammer on the weathervane.", hammerHighlight);
		useHammerOnVane.addIcon(ItemID.HAMMER);
		searchVaneAgain = new ObjectStep(this, NullObjectID.NULL_5811, new WorldPoint(2702, 3476, 3), "Right-click search the weathervane on top of the Seers' building again.");

		goDownFromRoof = new ObjectStep(this, ObjectID.TRAPDOOR_26119, new WorldPoint(2715, 3472, 3), "Climb down from the roof.");
		goDownLadderToSeers = new ObjectStep(this, ObjectID.LADDER_25939, new WorldPoint(2715, 3470, 1), "Repair the vane parts on the anvil in north Seers' Village.");
		useVane123OnAnvil = new ObjectStep(this, ObjectID.ANVIL_2097, new WorldPoint(2712, 3495, 0), "Repair the vane parts on an anvil. You can find one in the north of Seers' Village.", brokenVane1, brokenVane2, brokenVane3, hammer, steelBar, ironBar, bronzeBar);
		useVane12OnAnvil = new ObjectStep(this, ObjectID.ANVIL_2097, new WorldPoint(2712, 3495, 0), "Repair the vane parts on an anvil. You can find one in the north of Seers' Village.", brokenVane1, brokenVane2, hammer, steelBar, bronzeBar);
		useVane13OnAnvil = new ObjectStep(this, ObjectID.ANVIL_2097, new WorldPoint(2712, 3495, 0), "Repair the vane parts on an anvil. You can find one in the north of Seers' Village.", brokenVane1, brokenVane3, hammer, ironBar, bronzeBar);
		useVane23OnAnvil = new ObjectStep(this, ObjectID.ANVIL_2097, new WorldPoint(2712, 3495, 0), "Repair the vane parts on an anvil. You can find one in the north of Seers' Village.", brokenVane2, brokenVane3, hammer, ironBar, bronzeBar);
		useVane1OnAnvil = new ObjectStep(this, ObjectID.ANVIL_2097, new WorldPoint(2712, 3495, 0), "Repair the vane parts on an anvil. You can find one in the north of Seers' Village.", brokenVane1, hammer, steelBar);
		useVane2OnAnvil = new ObjectStep(this, ObjectID.ANVIL_2097, new WorldPoint(2712, 3495, 0), "Repair the vane parts on an anvil. You can find one in the north of Seers' Village.", brokenVane2, hammer, bronzeBar);
		useVane3OnAnvil = new ObjectStep(this, ObjectID.ANVIL_2097, new WorldPoint(2712, 3495, 0), "Repair the vane parts on an anvil. You can find one in the north of Seers' Village.", brokenVane3, hammer, ironBar);
		useVane123OnAnvil.addSubSteps(goDownFromRoof, goDownLadderToSeers, useVane1OnAnvil, useVane2OnAnvil, useVane3OnAnvil, useVane12OnAnvil, useVane13OnAnvil, useVane23OnAnvil);

		goBackUpLadder = new ObjectStep(this, ObjectID.LADDER_25941, new WorldPoint(2699, 3476, 0), "Go back up to the Seers' roof and fix the vane.");
		goBackUpToRoof = new ObjectStep(this, ObjectID.LADDER_26118, new WorldPoint(2715, 3472, 1), "Go back up to the Seers' roof and fix the vane.");
		useVane1 = new ObjectStep(this, NullObjectID.NULL_5811, new WorldPoint(2702, 3476, 3), "Use the ornament on the weathervane.", ornament);
		useVane2 = new ObjectStep(this, NullObjectID.NULL_5811, new WorldPoint(2702, 3476, 3), "Use the directionals on the weathervane.", directionals);
		useVane3 = new ObjectStep(this, NullObjectID.NULL_5811, new WorldPoint(2702, 3476, 3), "Use the weathervane pillar on the weathervane.", weathervanePillar);
		goBackUpLadder.addSubSteps(goBackUpToRoof, useVane1, useVane2, useVane3);

		goFromRoofToPhantuwti = new ObjectStep(this, ObjectID.TRAPDOOR_26119, new WorldPoint(2715, 3472, 3), "Return to Phantuwti.");
		goDownLadderToPhantuwti = new ObjectStep(this, ObjectID.LADDER_25940, new WorldPoint(2699, 3476, 1), "Return to Phantuwti.");
		finishWithPhantuwti = new NpcStep(this, NpcID.PHANTUWTI_FANSTUWI_FARSIGHT, new WorldPoint(2702, 3473, 0), "Return to Phantuwti in the south west house of Seers' Village.");
		finishWithPhantuwti.addSubSteps(goFromRoofToPhantuwti, goDownLadderToPhantuwti);
		finishWithPhantuwti.addDialogStep("I've fixed the weather vane!");

		returnToArhein = new NpcStep(this, NpcID.ARHEIN, new WorldPoint(2804, 3432, 0), "Talk to Arhein in Catherby.", weatherReport);
		returnToArhein.addDialogStep("I have the weather report for you.");

		returnToBleemadge = new NpcStep(this, NpcID.CAPTAIN_BLEEMADGE, new WorldPoint(2847, 3498, 0), "Right-click talk to Captain Bleemadge on White Wolf Mountain.");
		returnToBleemadge.addDialogStep("Hey there, did you get your T.R.A.S.H?");
		((NpcStep) returnToBleemadge).addAlternateNpcs(NpcID.CAPTAIN_BLEEMADGE_10461, NpcID.CAPTAIN_BLEEMADGE_10462,
			NpcID.CAPTAIN_BLEEMADGE_10463, NpcID.CAPTAIN_BLEEMADGE_10464, NpcID.CAPTAIN_BLEEMADGE_10465,
			NpcID.CAPTAIN_BLEEMADGE_10466);

		returnUpToSanfew = new ObjectStep(this, ObjectID.STAIRCASE_16671, new WorldPoint(2899, 3429, 0), "Return to Sanfew upstairs in the Taverley herblore store.");
		returnToSanfew = new NpcStep(this, NpcID.SANFEW, new WorldPoint(2899, 3429, 1), "Return to Sanfew upstairs in the Taverley herblore store.");
		returnToSanfew.addDialogStep("Hi there, the Gnome Pilot has agreed to take you to see the ogres!");

		goDownToHammerspikeAgain = new ObjectStep(this, ObjectID.TRAPDOOR_11867, new WorldPoint(3019, 3450, 0), "Return to the Dwarven Mine and talk to Hammerspike Stoutbeard in the west side.");
		returnToHammerspike = new NpcStep(this, NpcID.HAMMERSPIKE_STOUTBEARD, new WorldPoint(2968, 9811, 0), "Return to Hammerspike Stoutbeard in the west cavern of the Dwarven Mine.");
		returnToHammerspike.addSubSteps(goDownToHammerspikeAgain);

		killGangMembers = new NpcStep(this, NpcID.DWARF_GANG_MEMBER, new WorldPoint(2968, 9811, 0), "Kill 3 dwarf gang members until Hammerspike gives in. One dwarf gang member should appear after each kill");
		talkToHammerspikeFinal = new NpcStep(this, NpcID.HAMMERSPIKE_STOUTBEARD, new WorldPoint(2968, 9811, 0), "Return to Hammerspike Stoutbeard in the west cavern of the Dwarven Mine.");
		returnToTassie = new NpcStep(this, NpcID.TASSIE_SLIPCAST, new WorldPoint(3085, 3409, 0), "Return to Tassie Slipcast in the Barbarian Village pottery building.");
		spinPotLid = new ObjectStep(this, ObjectID.POTTERS_WHEEL_14887, new WorldPoint(3087, 3409, 0), "Spin the clay into a pot lid.", softClay);
		spinPotLid.addWidgetHighlightWithItemIdRequirement(270, 19, ItemID.UNFIRED_POT_LID, true);
		pickUpPot = new ItemStep(this, "Get a pot to put your lid on. There's one in the Barbarian Village helmet shop.", pot);
		firePotLid = new ObjectStep(this, ObjectID.POTTERY_OVEN_11601, new WorldPoint(3085, 3407, 0), "Fire the unfired pot lid", unfiredPotLid);
		usePotLidOnPot = new DetailedQuestStep(this, "Use the pot lid on a pot.", pot, potLid);
		returnToApothecary = new NpcStep(this, NpcID.APOTHECARY, new WorldPoint(3196, 3404, 0), "Return to the Apothecary in west Varrock.", potWithLid);
		returnToApothecary.addDialogStep("Talk about One Small Favour.");

		returnToHorvik = new NpcStep(this, NpcID.HORVIK, new WorldPoint(3229, 3437, 0), "Return to Horvik in the armour shop north east of the Varrock square.", pigeonCages5, breathingSalts, herbalTincture);
		returnToHorvik.addDialogStep("I have the tincture and the breathing salts.");
		talkToHorvikFinal = new NpcStep(this, NpcID.HORVIK, new WorldPoint(3229, 3437, 0), "Talk to Horvik once more in the armour shop north east of the Varrock square.", pigeonCages5);
		talkToHorvikFinal.addDialogStep("I have the five pigeon cages you asked for!");
		returnToSeth = new NpcStep(this, NpcID.SETH_GROATS, new WorldPoint(3228, 3291, 0), "Return to Seth Groats in the farm north east of Lumbridge, across the river.", chickenCages5);
		returnDownToJohnahus = new ObjectStep(this, NullObjectID.NULL_5492, new WorldPoint(3166, 3252, 0), "Enter the H.A.M hideout west of Lumbridge and talk to Johanhus Ulsbrecht in there.");
		returnToJohnahus = new NpcStep(this, NpcID.JOHANHUS_ULSBRECHT, new WorldPoint(3171, 9619, 0), "Return to Johanhus Ulsbrecht in the south of the H.A.M hideout.");
		returnToJohnahus.addDialogStep("You're in luck, I've managed to swing that chicken deal for you.");
		returnToAggie = new NpcStep(this, NpcID.AGGIE, new WorldPoint(3086, 3258, 0), "Return to Aggie in Draynor Village.");
		returnToAggie.addDialogStep("Good news! Jimmy has been released!");
		returnToBrian = new NpcStep(this, NpcID.BRIAN, new WorldPoint(3027, 3249, 0), "Return to Brian in the Port Sarim axe shop.");
		returnToBrian.addDialogStep("I've returned with good news.");

		returnToForester = new NpcStep(this, NpcID.JUNGLE_FORESTER, new WorldPoint(2861, 2942, 0), "Return to a Jungle Forester south of Shilo Village.", sharpenedAxe);
		returnToForester.addDialogStep("Good news, I have your sharpened axe!");
		returnToYanni = new NpcStep(this, NpcID.YANNI_SALIKA, new WorldPoint(2836, 2983, 0), "Return to Yanni Salika in Shilo Village.", redMahog);
		returnToYanni.addDialogStep("Here's the red mahogany you asked for.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(steelBars4);
		reqs.add(bronzeBar);
		reqs.add(ironBar);
		reqs.add(chisel);
		reqs.add(guam2);
		reqs.add(marrentill);
		reqs.add(harralander);
		reqs.add(hammer);
		reqs.add(emptyCup);
		reqs.add(pot);
		reqs.add(hotWaterBowl);

		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(varrockTeleports);
		reqs.add(lumbridgeTeleports);
		reqs.add(faladorTeleports);
		reqs.add(ardougneTeleports);
		reqs.add(camelotTeleports);
		reqs.add(opal.quantity(2));
		reqs.add(jade.quantity(2));
		reqs.add(redTopaz.quantity(2));
		reqs.add(pickaxe);
		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Slagilith (level 92)", "3x Dwarf gang members (level 44)");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.RUNE_MYSTERIES, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.DRUIDIC_RITUAL, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.SHILO_VILLAGE, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.AGILITY, 36, true));
		req.add(new SkillRequirement(Skill.CRAFTING, 25, true));
		req.add(new SkillRequirement(Skill.HERBLORE, 18, true));
		req.add(new SkillRequirement(Skill.SMITHING, 30, true));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("2 x 10,000 Experience Lamps (Any skill over level 30)", ItemID.ANTIQUE_LAMP, 2), //4447 is placeholder for filter
				new ItemReward("A Steel Keyring", ItemID.STEEL_KEY_RING, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("The ability to make Guthix Rest tea."),
				new UnlockReward("Gnome Glider in Feldip Hills."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Collections.singletonList(talkToYanni)));
		allSteps.add(new PanelDetails("A few small favours", Arrays.asList(talkToJungleForester, talkToBrian, talkToAggie, talkToJohanhus, talkToFred, talkToSeth,
			talkToHorvik, talkToApoth, talkToTassie, talkToHammerspike, talkToSanfew, makeGuthixRest, talkToBleemadge, talkToArhein, talkToPhantuwti, enterGoblinCave,
			searchWall, talkToCromperty, talkToTindel, talkToRantz, talkToGnormadium, fixAllLamps),
			chisel, steelBars3, emptyCup, hotWaterBowl, guam2, marrentill, harralander));
		allSteps.add(new PanelDetails("Completing the favours", Arrays.asList(talkToGnormadiumAgain, returnToRantz,
			returnToTindel, returnToCromperty, getPigeonCages, enterGoblinCaveAgain, standNextToSculpture, killSlagilith,
			readScrollAgain, talkToPetra, returnToPhantuwti, searchVane, useHammerOnVane, searchVaneAgain,
			useVane123OnAnvil, goBackUpLadder, finishWithPhantuwti, returnToArhein, returnToBleemadge, returnToSanfew,
			returnToHammerspike, killGangMembers, talkToHammerspikeFinal, returnToTassie, spinPotLid, firePotLid, pickUpPot,
			usePotLidOnPot, returnToApothecary, returnToHorvik, talkToHorvikFinal, returnToSeth, returnToJohnahus, returnToAggie,
			returnToBrian, returnToForester, returnToYanni), bronzeBar, ironBar, steelBar, hammer, pot));

		return allSteps;
	}
}
