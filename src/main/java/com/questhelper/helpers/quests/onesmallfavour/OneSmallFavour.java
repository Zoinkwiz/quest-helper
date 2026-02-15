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

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.nor;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.TileStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

public class OneSmallFavour extends BasicQuestHelper
{
	// Required items
	ItemRequirement steelBars4;
	ItemRequirement bronzeBar;
	ItemRequirement ironBar;
	ItemRequirement chisel;
	ItemRequirement guam2;
	ItemRequirement marrentill;
	ItemRequirement harralander;
	ItemRequirement hammer;
	ItemRequirement emptyCup;
	ItemRequirement pot;
	ItemRequirement hotWaterBowl;

	// Recommended items
	ItemRequirement draynorVillageTeleports;
	ItemRequirement lumbridgeTeleports;
	ItemRequirement varrockTeleports;
	ItemRequirement taverleyOrFaladorTeleports;
	ItemRequirement camelotTeleports;
	ItemRequirement fishingGuildAndDwarvenMineTeleports;
	ItemRequirement ardougneTeleports;
	ItemRequirement khazardTeleports;
	ItemRequirement feldipHillsTeleports;
	ItemRequirement pickaxe;
	ItemRequirement opal2;
	ItemRequirement jade2;
	ItemRequirement redTopaz2;
	ItemRequirement coins3000;
	ItemRequirement combatGear;

	// Mid-quest item requirements
	ItemRequirement opal;
	ItemRequirement jade;
	ItemRequirement redTopaz;
	ItemRequirement sapphire;
	ItemRequirement softClay;
	ItemRequirement bluntAxe;
	ItemRequirement herbalTincture;
	ItemRequirement guthixRest;
	ItemRequirement uncutSapphire;
	ItemRequirement cupOfWater;
	ItemRequirement uncutOpal;
	ItemRequirement uncutJade;
	ItemRequirement uncutRedTopaz;
	ItemRequirement stodgyMattress;
	ItemRequirement mattress;
	ItemRequirement animateRockScroll;
	ItemRequirement animateRockScrollHighlight;
	ItemRequirement ironOxide;
	ItemRequirement brokenVane1;
	ItemRequirement brokenVane2;
	ItemRequirement brokenVane3;
	ItemRequirement ornament;
	ItemRequirement weathervanePillar;
	ItemRequirement directionals;
	ItemRequirement weatherReport;
	ItemRequirement unfiredPotLid;
	ItemRequirement potLid;
	ItemRequirement potWithLid;
	ItemRequirement breathingSalts;
	ItemRequirement chickenCages5;
	ItemRequirement sharpenedAxe;
	ItemRequirement redMahog;
	ItemRequirement steelBars3;
	ItemRequirement steelBar;
	ItemRequirement guam;
	ItemRequirement hammerHighlight;
	ItemRequirement pigeonCages5;
	ItemRequirement guamTea;
	ItemRequirement guam2Tea;
	ItemRequirement harrTea;
	ItemRequirement marrTea;
	ItemRequirement guamMarrTea;
	ItemRequirement guamHarrTea;
	ItemRequirement harrMarrTea;
	ItemRequirement guam2MarrTea;
	ItemRequirement guam2HarrTea;
	ItemRequirement guamHarrMarrTea;
	ItemRequirement herbTeaMix;

	// Zones
	Zone sanfewRoom;
	Zone hamBase;
	Zone dwarvenMine;
	Zone goblinCave;
	Zone scrollSpot;
	Zone seersVillageUpstairs;
	Zone roof;

	// Miscellaneous requirements
	ZoneRequirement inSanfewRoom;
	ZoneRequirement inHamBase;
	ZoneRequirement inDwarvenMine;
	ZoneRequirement inGoblinCave;
	VarbitRequirement lamp1Empty;
	VarbitRequirement lamp1Full;
	VarbitRequirement lamp2Empty;
	VarbitRequirement lamp2Full;
	VarbitRequirement lamp3Empty;
	VarbitRequirement lamp3Full;
	VarbitRequirement lamp4Empty;
	VarbitRequirement lamp4Full;
	VarbitRequirement lamp5Empty;
	VarbitRequirement lamp5Full;
	VarbitRequirement lamp6Empty;
	VarbitRequirement lamp6Full;
	VarbitRequirement lamp7Empty;
	VarbitRequirement lamp7Full;
	VarbitRequirement lamp8Empty;
	VarbitRequirement lamp8Full;
	VarbitRequirement allEmpty;
	VarbitRequirement allFull;
	ZoneRequirement inScrollSpot;
	NpcCondition slagilithNearby;
	NpcCondition petraNearby;
	ZoneRequirement inSeersVillageUpstairs;
	ZoneRequirement onRoof;
	VarbitRequirement addedOrnaments;
	VarbitRequirement addedDirectionals;
	VarbitRequirement addedWeathervanePillar;
	Conditions hasOrUsedOrnament;
	Conditions hasOrUsedDirectionals;
	Conditions hasOrUsedWeathervanePillar;

	// Steps
	NpcStep talkToYanni;

	NpcStep talkToJungleForester;

	NpcStep talkToBrian;

	NpcStep talkToAggie;

	ObjectStep goDownToJohanhus;
	NpcStep talkToJohanhus;

	NpcStep talkToFred;

	NpcStep talkToSeth;

	NpcStep talkToHorvik;

	NpcStep talkToApoth;

	NpcStep talkToTassie;

	ObjectStep goDownToHammerspike;
	NpcStep talkToHammerspike;

	ObjectStep goUpToSanfew;
	NpcStep talkToSanfew;

	DetailedQuestStep useBowlOnCup;
	DetailedQuestStep useHerbsOnCup;
	DetailedQuestStep makeGuthixRest;
	NpcStep talkToBleemadge;

	NpcStep talkToBleemadgeNoTea;

	NpcStep talkToArhein;

	NpcStep talkToPhantuwti;

	ObjectStep enterGoblinCave;
	ObjectStep searchWall;

	NpcStep talkToCromperty;

	NpcStep talkToTindel;

	NpcStep talkToRantz;

	NpcStep talkToGnormadium;

	ObjectStep take1;
	ObjectStep take2;
	ObjectStep take3;
	ObjectStep take4;
	ObjectStep take5;
	ObjectStep take6;
	ObjectStep take7;
	ObjectStep take8;
	DetailedQuestStep cutSaph;
	DetailedQuestStep cutJade;
	DetailedQuestStep cutTopaz;
	DetailedQuestStep cutOpal;
	ObjectStep put1;
	ObjectStep put2;
	ObjectStep put3;
	ObjectStep put4;
	ObjectStep put5;
	ObjectStep put6;
	ObjectStep put7;
	ObjectStep put8;
	DetailedQuestStep fixAllLamps;

	NpcStep talkToGnormadiumAgain;

	NpcStep returnToRantz;

	NpcStep returnToTindel;

	NpcStep returnToCromperty;

	DetailedQuestStep getPigeonCages;
	ObjectStep enterGoblinCaveAgain;
	DetailedQuestStep standNextToSculpture;
	DetailedQuestStep readScroll;
	NpcStep killSlagilith;

	DetailedQuestStep readScrollAgain;
	NpcStep talkToPetra;

	NpcStep returnToPhantuwti;

	NpcStep returnToPhantuwti2;

	ObjectStep goUpLadder;
	ObjectStep goUpToRoof;
	ObjectStep searchVane;

	ObjectStep useHammerOnVane;

	ObjectStep searchVaneAgain;

	ObjectStep goDownFromRoof;
	ObjectStep goDownLadderToSeers;
	ObjectStep useVane123OnAnvil;
	ObjectStep useVane12OnAnvil;
	ObjectStep useVane13OnAnvil;
	ObjectStep useVane23OnAnvil;
	ObjectStep useVane1OnAnvil;
	ObjectStep useVane2OnAnvil;
	ObjectStep useVane3OnAnvil;

	ObjectStep goBackUpLadder;
	ObjectStep goBackUpToRoof;
	ObjectStep useVane1;
	ObjectStep useVane2;
	ObjectStep useVane3;

	ObjectStep goFromRoofToPhantuwti;
	ObjectStep goDownLadderToPhantuwti;
	NpcStep finishWithPhantuwti;

	NpcStep returnToArhein;

	NpcStep returnToBleemadge;

	ObjectStep returnUpToSanfew;
	NpcStep returnToSanfew;

	ObjectStep goDownToHammerspikeAgain;
	NpcStep returnToHammerspike;

	NpcStep killGangMembers;

	NpcStep talkToHammerspikeFinal; // TODO: weird one, never selected

	NpcStep returnToTassie;

	ObjectStep spinPotLid;
	ItemStep pickUpPot;
	ObjectStep firePotLid;
	DetailedQuestStep usePotLidOnPot;
	NpcStep returnToApothecary;

	NpcStep returnToHorvik;

	NpcStep talkToHorvikFinal;

	NpcStep returnToSeth;

	ObjectStep returnDownToJohnahus;
	NpcStep returnToJohnahus;

	NpcStep returnToAggie;

	NpcStep returnToBrian;

	NpcStep returnToForester;

	NpcStep returnToYanni;

	@Override
	protected void setupZones()
	{
		sanfewRoom = new Zone(new WorldPoint(2893, 3423, 1), new WorldPoint(2903, 3433, 1));
		hamBase = new Zone(new WorldPoint(3140, 9600, 0), new WorldPoint(3190, 9655, 0));
		dwarvenMine = new Zone(new WorldPoint(2960, 9696, 0), new WorldPoint(3062, 9854, 0));
		goblinCave = new Zone(new WorldPoint(2560, 9792, 0), new WorldPoint(2623, 9855, 0));
		scrollSpot = new Zone(new WorldPoint(2616, 9835, 0), new WorldPoint(2619, 9835, 0));
		seersVillageUpstairs = new Zone(new WorldPoint(2698, 3468, 1), new WorldPoint(2717, 3476, 1));
		roof = new Zone(new WorldPoint(2695, 3469, 3), new WorldPoint(2716, 3476, 3));
	}

	@Override
	protected void setupRequirements()
	{
		steelBars4 = new ItemRequirement("Steel bar", ItemID.STEEL_BAR, 4);
		steelBars3 = new ItemRequirement("Steel bar", ItemID.STEEL_BAR, 3);
		steelBar = new ItemRequirement("Steel bar", ItemID.STEEL_BAR);
		softClay = new ItemRequirement("Soft clay", ItemID.SOFTCLAY);
		softClay.setHighlightInInventory(true);
		chisel = new ItemRequirement("Chisel", ItemID.CHISEL).isNotConsumed();
		chisel.setHighlightInInventory(true);
		bronzeBar = new ItemRequirement("Bronze bar", ItemID.BRONZE_BAR);
		ironBar = new ItemRequirement("Iron bar", ItemID.IRON_BAR);
		guam2 = new ItemRequirement("Guam leaf", ItemID.GUAM_LEAF, 2);
		guam = new ItemRequirement("Guam leaf", ItemID.GUAM_LEAF);
		marrentill = new ItemRequirement("Marrentill", ItemID.MARENTILL);
		harralander = new ItemRequirement("Harralander", ItemID.HARRALANDER);
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed();

		hammerHighlight = hammer.highlighted();
		emptyCup = new ItemRequirement("Empty cup", ItemID.CUP_EMPTY);
		emptyCup.setTooltip("You can find a cup of tea in a house north of Sanfew. Drink it for an empty cup");
		pigeonCages5 = new ItemRequirement("Pigeon cages", ItemID.PIGEONS, 5);
		pigeonCages5.setTooltip("You can get more from outside a house in East Ardougne");
		pot = new ItemRequirement("Pot", ItemID.POT_EMPTY);
		pot.setHighlightInInventory(true);
		hotWaterBowl = new ItemRequirement("Bowl of hot water", ItemID.BOWL_HOT_WATER);
		hotWaterBowl.setTooltip("You can find a bowl in Lumbridge castle. Fill it up, then boil it on the range");

		draynorVillageTeleports = new ItemRequirement("Draynor Teleport with glory, 2 or more charges", ItemCollections.AMULET_OF_GLORIES);
		lumbridgeTeleports = new ItemRequirement("Teleports to Lumbridge", ItemID.POH_TABLET_LUMBRIDGETELEPORT, 4);
		varrockTeleports = new ItemRequirement("Teleports to Varrock", ItemID.POH_TABLET_VARROCKTELEPORT, 2);
		taverleyOrFaladorTeleports = new ItemRequirement("Teleports to Taverley or Falador", ItemID.NZONE_TELETAB_TAVERLEY, 2);
		camelotTeleports = new ItemRequirement("Teleports to Camelot or Catherby", ItemID.POH_TABLET_CAMELOTTELEPORT, 2);
		camelotTeleports.addAlternates(ItemID.LUNAR_TABLET_CATHERBY_TELEPORT);
		fishingGuildAndDwarvenMineTeleports = new ItemRequirement("Fishing and Mining Guild Teleport for Dwarven Mine with skills necklace, 4 or more charges", ItemID.JEWL_NECKLACE_OF_SKILLS_4);
		fishingGuildAndDwarvenMineTeleports.addAlternates(ItemID.JEWL_NECKLACE_OF_SKILLS_5, ItemID.JEWL_NECKLACE_OF_SKILLS_6);
		ardougneTeleports = new ItemRequirement("Teleports to Ardougne", ItemID.POH_TABLET_ARDOUGNETELEPORT, 2);
		khazardTeleports = new ItemRequirement("Teleports to Port Khazard", ItemID.LUNAR_TABLET_KHAZARD_TELEPORT, 2);
		feldipHillsTeleports = new ItemRequirement("Teleports to Feldip Hills", ItemID.TELEPORTSCROLL_FELDIP, 2);

		pickaxe = new ItemRequirement("Any pickaxe to kill Slagilith", ItemCollections.PICKAXES).isNotConsumed();

		combatGear = new ItemRequirement("Combat gear to fight Slagilith and the dwarf gang members", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		opal2 = new ItemRequirement("Opal", ItemID.OPAL, 2);
		opal2.setHighlightInInventory(true);
		jade2 = new ItemRequirement("Jade", ItemID.JADE, 2);
		jade2.setHighlightInInventory(true);
		redTopaz2 = new ItemRequirement("Red topaz", ItemID.RED_TOPAZ, 2);
		redTopaz2.setHighlightInInventory(true);
		coins3000 = new ItemRequirement("Coins", ItemID.COINS, 3000);
		coins3000.setTooltip("Alternatively for purchasing backup gems from Gnormadium Avlafrim");

		bluntAxe = new ItemRequirement("Blunt axe", ItemID.FAVOUR_JUNGLEFORESTERAXE_BLUNT);
		bluntAxe.setTooltip("You can get another from a Jungle Forester south of Shilo Village");
		herbalTincture = new ItemRequirement("Herbal tincture", ItemID.FAVOUR_HERBAL_TINCTURE);
		cupOfWater = new ItemRequirement("Cup of hot water", ItemID.CUP_HOT_WATER);

		harrTea = new ItemRequirement("Herb tea mix (harralander)", ItemID.FAVOUR_CUP_HARRALANDER);
		guamTea = new ItemRequirement("Herb tea mix (guam)", ItemID.FAVOUR_CUP_GUAM);
		marrTea = new ItemRequirement("Herb tea mix (marrentill)", ItemID.FAVOUR_CUP_MARRENTILL);
		harrMarrTea = new ItemRequirement("Herb tea mix (harr/marr)", ItemID.FAVOUR_CUP_HARRALANDER_MARRENTILL);
		guamHarrTea = new ItemRequirement("Herb tea mix (harr/guam)", ItemID.FAVOUR_CUP_HARRALANDER_GUAM);
		guam2Tea = new ItemRequirement("Herb tea mix (2 guam)", ItemID.FAVOUR_CUP_GUAM_GUAM);
		guamMarrTea = new ItemRequirement("Herb tea mix (marr/guam)", ItemID.FAVOUR_CUP_GUAM_MARRENTILL);
		guamHarrMarrTea = new ItemRequirement("Herb tea mix (harr/marr/guam)", ItemID.FAVOUR_CUP_HARRALANDER_MARRENTILL_GUAM);
		guam2MarrTea = new ItemRequirement("Herb tea mix (2 guam/marr)", ItemID.FAVOUR_CUP_GUAM_GUAM_MARRENTILL);
		guam2HarrTea = new ItemRequirement("Herb tea mix (2 guam/harr)", ItemID.FAVOUR_CUP_GUAM_GUAM_HARRALANDER);
		herbTeaMix = new ItemRequirement("Herb tea mix", ItemID.FAVOUR_CUP_HARRALANDER);
		herbTeaMix.addAlternates(ItemID.FAVOUR_CUP_GUAM, ItemID.FAVOUR_CUP_MARRENTILL, ItemID.FAVOUR_CUP_HARRALANDER_MARRENTILL,
			ItemID.FAVOUR_CUP_HARRALANDER_GUAM, ItemID.FAVOUR_CUP_GUAM_GUAM, ItemID.FAVOUR_CUP_GUAM_MARRENTILL,
			ItemID.FAVOUR_CUP_HARRALANDER_MARRENTILL_GUAM, ItemID.FAVOUR_CUP_GUAM_GUAM_MARRENTILL, ItemID.FAVOUR_CUP_GUAM_GUAM_HARRALANDER);

		guthixRest = new ItemRequirement("Guthix rest(3)", ItemID.CUP_GUTHIX_REST_3);

		sapphire = new ItemRequirement("Sapphire", ItemID.SAPPHIRE);
		sapphire.setHighlightInInventory(true);
		opal = opal2.quantity(1);
		jade = jade2.quantity(1);
		redTopaz = redTopaz2.quantity(1);

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

		stodgyMattress = new ItemRequirement("Stodgy mattress", ItemID.FAVOUR_MATRESS_STODGY);
		stodgyMattress.setTooltip("You can buy another from Tindel Marchant in Port Khazard for 100 gp");

		mattress = new ItemRequirement("Comfy mattress", ItemID.FAVOUR_MATRESS_COMFY);
		mattress.setTooltip("You can get another stodgy mattress from Tindel Marchant for 100 gp, then get it filled by Rantz");

		animateRockScroll = new ItemRequirement("Animate rock scroll", ItemID.FAVOUR_ANIMATE_ROCK);
		animateRockScroll.setTooltip("You can get another from Wizard Cromperty for 100 gp");

		animateRockScrollHighlight = new ItemRequirement("Animate rock scroll", ItemID.FAVOUR_ANIMATE_ROCK);
		animateRockScrollHighlight.setHighlightInInventory(true);
		animateRockScrollHighlight.setTooltip("You can get another from Wizard Cromperty for 100 gp");

		ironOxide = new ItemRequirement("Iron oxide", ItemID.FAVOUR_IRON_OXIDE);
		ironOxide.setTooltip("You can buy another from Tindel Marchant for 200 gp");

		brokenVane1 = new ItemRequirement("Broken vane part", ItemID.FAVOUR_DIRECTIONALS_BROKEN);
		brokenVane1.setHighlightInInventory(true);
		brokenVane1.setTooltip("You can get another from Phantuwti for 335 gp");
		directionals = new ItemRequirement("Directionals", ItemID.FAVOUR_DIRECTIONALS_FIXED);
		directionals.setHighlightInInventory(true);
		brokenVane2 = new ItemRequirement("Broken vane part", ItemID.FAVOUR_ORNAMENT_BROKEN);
		brokenVane2.setTooltip("You can get another from Phantuwti for 335 gp");
		brokenVane2.setHighlightInInventory(true);
		ornament = new ItemRequirement("Ornament", ItemID.FAVOUR_ORNAMENT_FIXED);
		ornament.setHighlightInInventory(true);
		brokenVane3 = new ItemRequirement("Broken vane part", ItemID.FAVOUR_PILLAR_BROKEN);
		brokenVane3.setTooltip("You can get another from Phantuwti for 335 gp");
		brokenVane3.setHighlightInInventory(true);
		weathervanePillar = new ItemRequirement("Weathervane pillar", ItemID.FAVOUR_PILLAR_FIXED);
		weathervanePillar.setHighlightInInventory(true);

		weatherReport = new ItemRequirement("Weather report", ItemID.FAVOUR_WEATHER_REPORT);
		weatherReport.setTooltip("You can get another from Phantuwti in Seers' Village");

		potLid = new ItemRequirement("Pot lid", ItemID.POTLID);
		potLid.setHighlightInInventory(true);
		unfiredPotLid = new ItemRequirement("Unfired pot lid", ItemID.POTLID_UNFIRED);
		potWithLid = new ItemRequirement("Airtight pot", ItemID.FAVOUR_AIRTIGHT_POT);

		breathingSalts = new ItemRequirement("Breathing salts", ItemID.FAVOUR_BREATHING_SALTS);
		breathingSalts.setTooltip("You can get more by bringing the Apothecary another airtight pot and 200 gp");

		chickenCages5 = new ItemRequirement("Chicken cage", ItemID.FAVOUR_CHICKEN_CAGE, 5);
		chickenCages5.setTooltip("You can get more chicken cages by bringing Horvik a pigeon cage and 100 coins per cage");

		sharpenedAxe = new ItemRequirement("Sharpened axe", ItemID.FAVOUR_JUNGLEFORESTERAXE_SHARP);
		sharpenedAxe.setTooltip("You can get another from Brian in Port Sarim");

		redMahog = new ItemRequirement("Red mahogany log", ItemID.FAVOUR_MAHOGANY_LOG);
		redMahog.setTooltip("You can get another from a jungle forester for 200 gp");

		inSanfewRoom = new ZoneRequirement(sanfewRoom);
		inHamBase = new ZoneRequirement(hamBase);
		inDwarvenMine = new ZoneRequirement(dwarvenMine);
		inGoblinCave = new ZoneRequirement(goblinCave);

		lamp1Empty = new VarbitRequirement(VarbitID.JADELIGHT1_TAKEN, 1);
		lamp2Empty = new VarbitRequirement(VarbitID.TOPAZLIGHT1_TAKEN, 1);
		lamp3Empty = new VarbitRequirement(VarbitID.OPALLIGHT1_TAKEN, 1);
		lamp4Empty = new VarbitRequirement(VarbitID.SAPPHIRELIGHT1_TAKEN, 1);
		lamp5Empty = new VarbitRequirement(VarbitID.JADELIGHT2_TAKEN, 1);
		lamp6Empty = new VarbitRequirement(VarbitID.TOPAZLIGHT2_TAKEN, 1);
		lamp7Empty = new VarbitRequirement(VarbitID.OPALLIGHT2_TAKEN, 1);
		lamp8Empty = new VarbitRequirement(VarbitID.SAPPHIRELIGHT2_TAKEN, 1);

		allEmpty = new VarbitRequirement(VarbitID.CHECKLANDINGLIGHTS, 255);

		lamp1Full = new VarbitRequirement(VarbitID.JADELIGHT1_FIXED, 1);
		lamp2Full = new VarbitRequirement(VarbitID.TOPAZLIGHT1_FIXED, 1);
		lamp3Full = new VarbitRequirement(VarbitID.OPALLIGHT1_FIXED, 1);
		lamp4Full = new VarbitRequirement(VarbitID.SAPPHIRELIGHT1_FIXED, 1);
		lamp5Full = new VarbitRequirement(VarbitID.JADELIGHT2_FIXED, 1);
		lamp6Full = new VarbitRequirement(VarbitID.TOPAZLIGHT2_FIXED, 1);
		lamp7Full = new VarbitRequirement(VarbitID.OPALLIGHT2_FIXED, 1);
		lamp8Full = new VarbitRequirement(VarbitID.SAPPHIRELIGHT2_FIXED, 1);

		allFull = new VarbitRequirement(VarbitID.FIXEDLANDINGLIGHTS, 255);

		slagilithNearby = new NpcCondition(NpcID.SLAGILITH);
		inScrollSpot = new ZoneRequirement(scrollSpot);
		inSeersVillageUpstairs = new ZoneRequirement(seersVillageUpstairs);
		onRoof = new ZoneRequirement(roof);

		petraNearby = new NpcCondition(NpcID.FAVOUR_PETRA);

		addedOrnaments = new VarbitRequirement(VarbitID.ORNAMENTFIXED, 1);
		addedDirectionals = new VarbitRequirement(VarbitID.DIRECTIONALSFIXED, 1);
		addedWeathervanePillar = new VarbitRequirement(VarbitID.ROTATINGPILLARFIXED, 1);

		hasOrUsedDirectionals = or(addedDirectionals, directionals.alsoCheckBank());
		hasOrUsedOrnament = or(addedOrnaments, ornament.alsoCheckBank());
		hasOrUsedWeathervanePillar = or(addedWeathervanePillar, weathervanePillar.alsoCheckBank());
	}

	void setupSteps()
	{
		talkToYanni = new NpcStep(this, NpcID.SHILOANTIQUES, new WorldPoint(2836, 2983, 0), "Talk to Yanni Salika in Shilo Village. CKR fairy ring or take cart from Brimhaven.");
		talkToYanni.addDialogStep("Is there anything else interesting to do around here?");
		talkToYanni.addDialogStep("Yes.");

		talkToJungleForester = new NpcStep(this, new int[]{NpcID.JUNGLEFORESTER_F, NpcID.JUNGLEFORESTER_M}, new WorldPoint(2861, 2942, 0), "Talk to a Jungle Forester south of Shilo Village.", true);
		talkToJungleForester.addDialogStep("I need to talk to you about red mahogany.");
		talkToJungleForester.addDialogStep("Okay, I'll take your axe to get it sharpened.");

		talkToBrian = new NpcStep(this, NpcID.BRIAN, new WorldPoint(3027, 3249, 0), "Talk to Brian in the Port Sarim axe shop.", bluntAxe);
		talkToBrian.addDialogStep("Do you sharpen axes?");
		talkToBrian.addDialogStepWithExclusion("Look, can you sharpen this cursed axe or what?", "Ok, ok, I'll do it! I'll go and see Aggie.");
		talkToBrian.addDialogStep("Ok, ok, I'll do it! I'll go and see Aggie.");

		talkToAggie = new NpcStep(this, NpcID.AGGIE_1OP, new WorldPoint(3086, 3258, 0), "Talk to Aggie in Draynor Village.");
		talkToAggie.addDialogStep("Could I ask you about being a character witness?");
		talkToAggie.addDialogStep("Let me guess, you're going to ask me to do you a favour?");
		talkToAggie.addDialogStep("Oh, okay, I'll see if I can find Jimmy.");

		goDownToJohanhus = new ObjectStep(this, ObjectID.HAM_MULTI_TRAPDOOR, new WorldPoint(3166, 3252, 0), "Enter the H.A.M hideout west of Lumbridge and talk to Johanhus Ulsbrecht in there.");
		talkToJohanhus = new NpcStep(this, NpcID.FAVOUR_JOHANHUS_ULSBRECHT, new WorldPoint(3171, 9619, 0), "Talk to Johanhus Ulsbrecht in the south of the H.A.M hideout.");
		talkToJohanhus.addDialogStep("I'm looking for Jimmy the Chisel.");
		talkToJohanhus.addDialogStep("And I suppose you need me to do you a favour?");
		talkToJohanhus.addDialogStep("Ok, Jimmy has to be worth more than a few scrawny chickens!");

		talkToJohanhus.addSubSteps(goDownToJohanhus);

		talkToFred = new NpcStep(this, NpcID.FRED_THE_FARMER, new WorldPoint(3190, 3273, 0), "Talk to Fred the Farmer north of Lumbridge.");
		talkToFred.addDialogStep("I need to talk to you about Jimmy.");

		talkToSeth = new NpcStep(this, NpcID.FAVOUR_SETH_GROATS, new WorldPoint(3228, 3291, 0), "Talk to Seth Groats in the farm north east of Lumbridge, across the river.");
		talkToSeth.addDialogStep("Oh, ok! I guess it's not that much further to Varrock!");
		talkToHorvik = new NpcStep(this, NpcID.HORVIK_THE_ARMOURER, new WorldPoint(3229, 3437, 0), "Talk to Horvik in the armour shop north east of the Varrock square.", steelBars3);
		talkToHorvik.addDialogStep("Hi, I need to talk to you about chicken cages!");
		talkToHorvik.addDialogStep("Ok, I guess one good turn deserves another.");
		talkToApoth = new NpcStep(this, NpcID.APOTHECARY, new WorldPoint(3196, 3404, 0), "Talk to the Apothecary in west Varrock.");
		talkToApoth.addDialogStep("Talk about One Small Favour.");
		talkToApoth.addDialogStep("Oh, ok, I guess it's not that far to the Barbarian Village.");
		talkToApoth.addDialogStep("I guess I can go to the Barbarian Village.");

		talkToTassie = new NpcStep(this, NpcID.FAVOUR_TASSIE_SLIPCAST, new WorldPoint(3085, 3409, 0), "Talk to Tassie Slipcast in the Barbarian Village pottery building.");
		talkToTassie.addDialogStep("Ok, I'll deal with Hammerspike!");

		goDownToHammerspike = new ObjectStep(this, ObjectID.FAI_DWARF_TRAPDOOR_DOWN, new WorldPoint(3019, 3450, 0), "Go into the Dwarven Mine and talk to Hammerspike Stoutbeard in the west side.");
		talkToHammerspike = new NpcStep(this, NpcID.FAVOUR_HAMMERSPIKE_STOUTBEARD, new WorldPoint(2968, 9811, 0), "Talk to Hammerspike Stoutbeard in the west cavern of the Dwarven Mine.");
		talkToHammerspike.addDialogStep("Have you always been a gangster?");
		talkToHammerspike.addDialogStep("Ok, another favour...I think I can manage that.");
		talkToHammerspike.addSubSteps(goDownToHammerspike);

		goUpToSanfew = new ObjectStep(this, ObjectID.SPIRALSTAIRS, new WorldPoint(2899, 3429, 0), "Talk to Sanfew upstairs in the Taverley herblore store.");
		talkToSanfew = new NpcStep(this, NpcID.SANFEW, new WorldPoint(2899, 3429, 1), "Talk to Sanfew upstairs in the Taverley herblore store.");
		talkToSanfew.addDialogStep("Are you taking any new initiates?");
		talkToSanfew.addDialogStepWithExclusion("Do you accept dwarves?", "A dwarf I know wants to become an initiate.");
		talkToSanfew.addDialogSteps("A dwarf I know wants to become an initiate.", "Yep, it's a deal.");
		talkToSanfew.addSubSteps(goUpToSanfew);

		useBowlOnCup = new DetailedQuestStep(this, "Use a bowl of hot water on an empty cup.", hotWaterBowl.highlighted(), emptyCup.highlighted());
		useHerbsOnCup = new DetailedQuestStep(this, "Use 2 guams, a marrentill and a harralander on the cup.",
			guam2.hideConditioned(or(guamTea, guam2Tea, guamMarrTea, guamHarrTea, guamHarrMarrTea)).highlighted(),
			guam.hideConditioned(or(cupOfWater, marrTea, harrTea, guam2Tea, guam2MarrTea, guam2HarrTea)).highlighted(),
			marrentill.hideConditioned(or(marrTea, harrMarrTea, guamMarrTea, guam2MarrTea, guamHarrMarrTea)).highlighted(),
			harralander.hideConditioned(or(harrTea, harrMarrTea, guamHarrTea, guam2HarrTea, guamHarrMarrTea)).highlighted(),
			cupOfWater.hideConditioned(or(guamTea, harrTea, marrTea, harrMarrTea, guamHarrTea, guam2Tea, guam2MarrTea, guamMarrTea, guam2HarrTea, guamHarrMarrTea)).highlighted(),
			herbTeaMix.hideConditioned(nor(guamTea, harrTea, marrTea, harrMarrTea, guamHarrTea, guam2Tea, guam2MarrTea, guamMarrTea, guam2HarrTea, guamHarrMarrTea)).highlighted());
		makeGuthixRest = new DetailedQuestStep(this, "Make Guthix Rest by using a bowl of hot water on an empty tea cup, then using 2 guams, a marrentill and a harralander on it.", emptyCup, hotWaterBowl, guam2, marrentill, harralander);
		makeGuthixRest.addSubSteps(useBowlOnCup, useHerbsOnCup);
		talkToBleemadge = new NpcStep(this, NpcID.PILOT_WHITE_WOLF_BASE, new WorldPoint(2847, 3498, 0), "Right-click talk to Captain Bleemadge on White Wolf Mountain.", guthixRest);
		talkToBleemadge.addAlternateNpcs(NpcID.PILOT_WHITE_WOLF_GRANDTREE, NpcID.PILOT_WHITE_WOLF_KARAMJA,
			NpcID.PILOT_WHITE_WOLF_AL_KHARID, NpcID.PILOT_WHITE_WOLF_VARROCK, NpcID.PILOT_WHITE_WOLF_OGRE,
			NpcID.PILOT_WHITE_WOLF_APE);
		talkToBleemadge.addDialogStep("I have a special tea here for you from Sanfew!");

		talkToBleemadgeNoTea = new NpcStep(this, NpcID.PILOT_WHITE_WOLF_BASE, new WorldPoint(2847, 3498, 0), "Right-click talk to Captain Bleemadge on White Wolf Mountain.");
		talkToBleemadgeNoTea.addAlternateNpcs(NpcID.PILOT_WHITE_WOLF_GRANDTREE, NpcID.PILOT_WHITE_WOLF_KARAMJA,
			NpcID.PILOT_WHITE_WOLF_AL_KHARID, NpcID.PILOT_WHITE_WOLF_VARROCK, NpcID.PILOT_WHITE_WOLF_OGRE,
			NpcID.PILOT_WHITE_WOLF_APE);
		talkToBleemadgeNoTea.addDialogStep("How was that tea?");
		talkToBleemadgeNoTea.addDialogStep("Ok, I'll go and get you some T.R.A.S.H.");

		talkToBleemadge.addSubSteps(talkToBleemadgeNoTea);

		talkToArhein = new NpcStep(this, NpcID.ARHEIN, new WorldPoint(2804, 3432, 0), "Talk to Arhein in Catherby.");
		talkToArhein.addDialogStep("I need to talk T.R.A.S.H. to you.");
		talkToArhein.addDialogStep("Yes, Ok, I'll do it!");

		talkToPhantuwti = new NpcStep(this, NpcID.FAVOUR_PHANTUWTI_FARSIGHT, new WorldPoint(2702, 3473, 0), "Talk to Phantuwti in the south west house of Seers' Village.");
		talkToPhantuwti.addDialogStep("Hi, can you give me a weather forecast?");
		talkToPhantuwti.addDialogStep("What can I do to help?");
		talkToPhantuwti.addDialogStep("Yes, Ok, I'll do it.");

		enterGoblinCave = new ObjectStep(this, ObjectID.MCANNONCAVE, new WorldPoint(2624, 3393, 0), "Enter the cave south east of the Fishing Guild.");
		searchWall = new ObjectStep(this, ObjectID.FAVOUR_LADY_IN_WALL, new WorldPoint(2621, 9835, 0), "Right-click search the sculpture in the wall in the north east corner of the cave.");

		talkToCromperty = new NpcStep(this, NpcID.CROMPERTY_PRE_DIARY, new WorldPoint(2684, 3323, 0), "Talk to Wizard Cromperty in north east Ardougne.");
		talkToCromperty.addAlternateNpcs(NpcID.CROMPERTY_PRE_DIARY, NpcID.CROMPERTY_POST_DIARY);
		talkToCromperty.addDialogStep("Chat.");
		talkToCromperty.addDialogStep("I need to talk to you about a girl stuck in some rock!");
		talkToCromperty.addDialogStep("Oh! Ok, one more 'small favour' isn't going to kill me...I hope!");

		talkToTindel = new NpcStep(this, NpcID.TINDEL_MARCHANT, new WorldPoint(2678, 3153, 0), "Talk to Tindel Marchant in Port Khazard.");
		talkToTindel.addDialogSteps("Wizard Cromperty sent me to get some iron oxide.", "Ask about iron oxide.", "Okay, I'll do it!");

		talkToRantz = new NpcStep(this, NpcID.RANTZ, new WorldPoint(2631, 2969, 0), "Talk to Rantz in the Feldip Hills. AKS fairy ring or Feldip Hills Teleport.");
		talkToRantz.addDialogStep("I need to talk to you about a mattress.");
		talkToRantz.addDialogStep("Ok, I'll see what I can do.");

		talkToGnormadium = new NpcStep(this, NpcID.GNORMADIUM_AVLAFRIM_TALK, new WorldPoint(2542, 2968, 0), "Talk to Gnormadium Avlafrim west of Rantz.");
		talkToGnormadium.addDialogStep("Rantz said I should help you finish this project.");
		talkToGnormadium.addDialogStep("Yes, I'll take a look at them.");

		take1 = new ObjectStep(this, ObjectID.OSF_MULTI_LANDINGLIGHT_JADE_1, new WorldPoint(2554, 2974, 0), "Take a jade from the north east landing light.");
		take2 = new ObjectStep(this, ObjectID.OSF_MULTI_LANDINGLIGHT_REDTOPAZ_1, new WorldPoint(2551, 2974, 0), "Take a red topaz from the landing light.");
		take3 = new ObjectStep(this, ObjectID.OSF_MULTI_LANDINGLIGHT_OPAL_1, new WorldPoint(2548, 2974, 0), "Take an opal from the landing light.");
		take4 = new ObjectStep(this, ObjectID.OSF_MULTI_LANDINGLIGHT_SAPPHIRE_1, new WorldPoint(2545, 2974, 0), "Take a sapphire from the landing light.");

		take5 = new ObjectStep(this, ObjectID.OSF_MULTI_LANDINGLIGHT_JADE_1, new WorldPoint(2554, 2969, 0), "Take a jade from the landing light.");
		take6 = new ObjectStep(this, ObjectID.OSF_MULTI_LANDINGLIGHT_REDTOPAZ_1, new WorldPoint(2551, 2969, 0), "Take a red topaz from the landing light.");
		take7 = new ObjectStep(this, ObjectID.OSF_MULTI_LANDINGLIGHT_OPAL_1, new WorldPoint(2548, 2969, 0), "Take an opal from the landing light.");
		take8 = new ObjectStep(this, ObjectID.OSF_MULTI_LANDINGLIGHT_SAPPHIRE_1, new WorldPoint(2545, 2969, 0), "Take a sapphire from the landing light.");

		put1 = new ObjectStep(this, ObjectID.OSF_MULTI_LANDINGLIGHT_JADE_1, new WorldPoint(2554, 2974, 0), "Put a jade into the north east landing light.", jade);
		put1.addIcon(ItemID.JADE);
		put2 = new ObjectStep(this, ObjectID.OSF_MULTI_LANDINGLIGHT_REDTOPAZ_1, new WorldPoint(2551, 2974, 0), "Put a red topaz into the landing light.", redTopaz);
		put2.addIcon(ItemID.RED_TOPAZ);
		put3 = new ObjectStep(this, ObjectID.OSF_MULTI_LANDINGLIGHT_OPAL_1, new WorldPoint(2548, 2974, 0), "Put an opal into the landing light.", opal);
		put3.addIcon(ItemID.OPAL);
		put4 = new ObjectStep(this, ObjectID.OSF_MULTI_LANDINGLIGHT_SAPPHIRE_1, new WorldPoint(2545, 2974, 0), "Put a sapphire into the landing light.", sapphire);
		put4.addIcon(ItemID.SAPPHIRE);

		put5 = new ObjectStep(this, ObjectID.OSF_MULTI_LANDINGLIGHT_JADE_1, new WorldPoint(2554, 2969, 0), "Put a jade into the landing light.", jade);
		put5.addIcon(ItemID.JADE);
		put6 = new ObjectStep(this, ObjectID.OSF_MULTI_LANDINGLIGHT_REDTOPAZ_1, new WorldPoint(2551, 2969, 0), "Put a red topaz into the landing light.", redTopaz);
		put6.addIcon(ItemID.RED_TOPAZ);
		put7 = new ObjectStep(this, ObjectID.OSF_MULTI_LANDINGLIGHT_OPAL_1, new WorldPoint(2548, 2969, 0), "Put an opal into the landing light.", opal);
		put7.addIcon(ItemID.OPAL);
		put8 = new ObjectStep(this, ObjectID.OSF_MULTI_LANDINGLIGHT_SAPPHIRE_1, new WorldPoint(2545, 2969, 0), "Put a sapphire into the landing light.", sapphire);
		put8.addIcon(ItemID.SAPPHIRE);

		cutJade = new DetailedQuestStep(this, "Cut the uncut jade.", chisel, uncutJade);
		cutSaph = new DetailedQuestStep(this, "Cut the uncut sapphire.", chisel, uncutSapphire);
		cutOpal = new DetailedQuestStep(this, "Cut the uncut opal.", chisel, uncutOpal);
		cutTopaz = new DetailedQuestStep(this, "Cut the uncut red topaz.", chisel, uncutRedTopaz);

		fixAllLamps = new DetailedQuestStep(this, "Take the gems out of each of the gnome lamps, cut them, then put back in the cut gem.");
		fixAllLamps.addSubSteps(cutJade, cutOpal, cutSaph, cutTopaz, put1, put2, put3, put4, put5, put6, put7, put8, take1, take2, take3, take4, take5, take6, take7, take8);

		talkToGnormadiumAgain = new NpcStep(this, NpcID.GNORMADIUM_AVLAFRIM_TALK, new WorldPoint(2542, 2968, 0), "Talk to Gnormadium Avlafrim again.");
		talkToGnormadiumAgain.addDialogStep("I've fixed all the lights!");

		returnToRantz = new NpcStep(this, NpcID.RANTZ, new WorldPoint(2631, 2975, 0), "Return to Rantz in the Feldip Hills. AKS fairy ring or Feldip Hills Teleport.", stodgyMattress);
		returnToRantz.addDialogStep("Ok, I've helped that Gnome, he shouldn't bother you anymore.");
		returnToTindel = new NpcStep(this, NpcID.TINDEL_MARCHANT, new WorldPoint(2678, 3153, 0), "Return to Tindel Marchant in Port Khazard.", mattress);
		returnToTindel.addDialogStep("I have the mattress.");

		returnToCromperty = new NpcStep(this, NpcID.CROMPERTY_PRE_DIARY, new WorldPoint(2684, 3323, 0), "Return to Wizard Cromperty in north east Ardougne.", ironOxide);
		returnToCromperty.addAlternateNpcs(NpcID.CROMPERTY_PRE_DIARY, NpcID.CROMPERTY_POST_DIARY);
		returnToCromperty.addDialogStep("Chat.");
		returnToCromperty.addDialogStep("I have that iron oxide you asked for!");

		getPigeonCages = new DetailedQuestStep(this, new WorldPoint(2618, 3325, 0), "Get 5 pigeon cages from behind Jerico's house in central East Ardougne.", pigeonCages5);

		enterGoblinCaveAgain = new ObjectStep(this, ObjectID.MCANNONCAVE, new WorldPoint(2624, 3393, 0), "Enter the cave south east of the Fishing Guild. " +
			"Be prepared to fight the Slagilith(level 92, takes reduced damage from anything other than pickaxes).", Arrays.asList(pigeonCages5, animateRockScroll), Collections.singletonList(pickaxe));
		standNextToSculpture = new TileStep(this, new WorldPoint(2616, 9835, 0), "Use the animate rock scroll next to the sculpture in the north east cavern.", animateRockScroll);
		readScroll = new DetailedQuestStep(this, "Read the animate rock scroll.", animateRockScrollHighlight);

		standNextToSculpture.addSubSteps(readScroll);

		killSlagilith = new NpcStep(this, NpcID.SLAGILITH, new WorldPoint(2617, 9837, 0), "Kill the Slagilith. They take reduced damage from anything other than a pickaxe.");
		killSlagilith.addRecommended(pickaxe);
		readScrollAgain = new DetailedQuestStep(this, "Read the animate rock scroll again.", animateRockScrollHighlight);
		talkToPetra = new NpcStep(this, NpcID.FAVOUR_PETRA, new WorldPoint(2617, 9837, 0), "Talk to Petra Fiyed.");

		returnToPhantuwti = new NpcStep(this, NpcID.FAVOUR_PHANTUWTI_FARSIGHT, new WorldPoint(2702, 3473, 0), "Return to Phantuwti in the south west house of Seers' Village.");
		returnToPhantuwti.addDialogStep("I've released Petra, she should have returned.");
		returnToPhantuwti.addDialogStep("I'll run you through if you don't give me that weather report.");
		returnToPhantuwti.addDialogStepWithExclusion("Why can't you get a clear picture?", "I'll run you through if you don't give me that weather report.");

		returnToPhantuwti2 = new NpcStep(this, NpcID.FAVOUR_PHANTUWTI_FARSIGHT, new WorldPoint(2702, 3473, 0), "Return to Phantuwti in the south west house of Seers' Village.");
		returnToPhantuwti2.addDialogStep("I'll run you through if you don't give me that weather report.");
		returnToPhantuwti2.addDialogStepWithExclusion("Why can't you get a clear picture?", "I'll run you through if you don't give me that weather report.");
		returnToPhantuwti2.addDialogStep("Which special Seers tools do you mean?");
		returnToPhantuwti2.addDialogStep("What do you mean, 'special combination of items'?");

		returnToPhantuwti.addSubSteps(returnToPhantuwti2);

		goUpLadder = new ObjectStep(this, ObjectID.KR_LADDER_DIRECTIONAL, new WorldPoint(2699, 3476, 0), "Go up the ladder nearby.");
		goUpToRoof = new ObjectStep(this, ObjectID.FAVOUR_SEER_LADDER, new WorldPoint(2715, 3472, 1), "Go up to the roof.");
		searchVane = new ObjectStep(this, ObjectID.OSF_WEATHERVANE, new WorldPoint(2702, 3476, 3), "Right-click search the weathervane on top of the Seers' building.");
		searchVane.addSubSteps(goUpLadder, goUpToRoof);
		useHammerOnVane = new ObjectStep(this, ObjectID.OSF_WEATHERVANE, new WorldPoint(2702, 3476, 3), "Use a hammer on the weathervane.", hammerHighlight);
		useHammerOnVane.addIcon(ItemID.HAMMER);
		searchVaneAgain = new ObjectStep(this, ObjectID.OSF_WEATHERVANE, new WorldPoint(2702, 3476, 3), "Right-click search the weathervane on top of the Seers' building again.");

		goDownFromRoof = new ObjectStep(this, ObjectID.FAVOUR_ROOF_TRAPDOOR, new WorldPoint(2715, 3472, 3), "Climb down from the roof.");
		goDownLadderToSeers = new ObjectStep(this, ObjectID.KR_LADDERTOP, new WorldPoint(2715, 3470, 1), "Repair the vane parts on the anvil in north Seers' Village.");
		useVane123OnAnvil = new ObjectStep(this, ObjectID.ANVIL, new WorldPoint(2712, 3495, 0), "Repair the vane parts on an anvil. You can find one in the north of Seers' Village.", brokenVane1, brokenVane2, brokenVane3, hammer, steelBar, ironBar, bronzeBar);
		useVane12OnAnvil = new ObjectStep(this, ObjectID.ANVIL, new WorldPoint(2712, 3495, 0), "Repair the vane parts on an anvil. You can find one in the north of Seers' Village.", brokenVane1, brokenVane2, hammer, steelBar, bronzeBar);
		useVane13OnAnvil = new ObjectStep(this, ObjectID.ANVIL, new WorldPoint(2712, 3495, 0), "Repair the vane parts on an anvil. You can find one in the north of Seers' Village.", brokenVane1, brokenVane3, hammer, ironBar, bronzeBar);
		useVane23OnAnvil = new ObjectStep(this, ObjectID.ANVIL, new WorldPoint(2712, 3495, 0), "Repair the vane parts on an anvil. You can find one in the north of Seers' Village.", brokenVane2, brokenVane3, hammer, ironBar, bronzeBar);
		useVane1OnAnvil = new ObjectStep(this, ObjectID.ANVIL, new WorldPoint(2712, 3495, 0), "Repair the vane parts on an anvil. You can find one in the north of Seers' Village.", brokenVane1, hammer, steelBar);
		useVane2OnAnvil = new ObjectStep(this, ObjectID.ANVIL, new WorldPoint(2712, 3495, 0), "Repair the vane parts on an anvil. You can find one in the north of Seers' Village.", brokenVane2, hammer, bronzeBar);
		useVane3OnAnvil = new ObjectStep(this, ObjectID.ANVIL, new WorldPoint(2712, 3495, 0), "Repair the vane parts on an anvil. You can find one in the north of Seers' Village.", brokenVane3, hammer, ironBar);
		useVane123OnAnvil.addSubSteps(goDownFromRoof, goDownLadderToSeers, useVane1OnAnvil, useVane2OnAnvil, useVane3OnAnvil, useVane12OnAnvil, useVane13OnAnvil, useVane23OnAnvil);

		goBackUpLadder = new ObjectStep(this, ObjectID.KR_LADDER_DIRECTIONAL, new WorldPoint(2699, 3476, 0), "Go back up to the Seers' roof and fix the vane.");
		goBackUpToRoof = new ObjectStep(this, ObjectID.FAVOUR_SEER_LADDER, new WorldPoint(2715, 3472, 1), "Go back up to the Seers' roof and fix the vane.");
		useVane1 = new ObjectStep(this, ObjectID.OSF_WEATHERVANE, new WorldPoint(2702, 3476, 3), "Use the ornament on the weathervane.", ornament);
		useVane2 = new ObjectStep(this, ObjectID.OSF_WEATHERVANE, new WorldPoint(2702, 3476, 3), "Use the directionals on the weathervane.", directionals);
		useVane3 = new ObjectStep(this, ObjectID.OSF_WEATHERVANE, new WorldPoint(2702, 3476, 3), "Use the weathervane pillar on the weathervane.", weathervanePillar);
		goBackUpLadder.addSubSteps(goBackUpToRoof, useVane1, useVane2, useVane3);

		goFromRoofToPhantuwti = new ObjectStep(this, ObjectID.FAVOUR_ROOF_TRAPDOOR, new WorldPoint(2715, 3472, 3), "Return to Phantuwti.");
		goDownLadderToPhantuwti = new ObjectStep(this, ObjectID.KR_LADDERTOP_DIRECTIONAL, new WorldPoint(2699, 3476, 1), "Return to Phantuwti.");
		finishWithPhantuwti = new NpcStep(this, NpcID.FAVOUR_PHANTUWTI_FARSIGHT, new WorldPoint(2702, 3473, 0), "Return to Phantuwti in the south west house of Seers' Village.");
		finishWithPhantuwti.addSubSteps(goFromRoofToPhantuwti, goDownLadderToPhantuwti);
		finishWithPhantuwti.addDialogStep("I've fixed the weather vane!");

		returnToArhein = new NpcStep(this, NpcID.ARHEIN, new WorldPoint(2804, 3432, 0), "Talk to Arhein in Catherby.", weatherReport);
		returnToArhein.addDialogStep("What did you want me to do again?");
		returnToArhein.addDialogStep("I have the weather report for you.");

		returnToBleemadge = new NpcStep(this, NpcID.PILOT_WHITE_WOLF_BASE, new WorldPoint(2847, 3498, 0), "Right-click talk to Captain Bleemadge on White Wolf Mountain.");
		returnToBleemadge.addDialogStep("Hey there, did you get your T.R.A.S.H?");
		returnToBleemadge.addAlternateNpcs(NpcID.PILOT_WHITE_WOLF_GRANDTREE, NpcID.PILOT_WHITE_WOLF_KARAMJA,
			NpcID.PILOT_WHITE_WOLF_AL_KHARID, NpcID.PILOT_WHITE_WOLF_VARROCK, NpcID.PILOT_WHITE_WOLF_OGRE,
			NpcID.PILOT_WHITE_WOLF_APE);

		returnUpToSanfew = new ObjectStep(this, ObjectID.SPIRALSTAIRS, new WorldPoint(2899, 3429, 0), "Return to Sanfew upstairs in the Taverley herblore store.");
		returnToSanfew = new NpcStep(this, NpcID.SANFEW, new WorldPoint(2899, 3429, 1), "Return to Sanfew upstairs in the Taverley herblore store.");
		returnToSanfew.addDialogStep("Hi there, the Gnome Pilot has agreed to take you to see the ogres!");

		goDownToHammerspikeAgain = new ObjectStep(this, ObjectID.FAI_DWARF_TRAPDOOR_DOWN, new WorldPoint(3019, 3450, 0), "Return to the Dwarven Mine and talk to Hammerspike Stoutbeard in the west side.");
		returnToHammerspike = new NpcStep(this, NpcID.FAVOUR_HAMMERSPIKE_STOUTBEARD, new WorldPoint(2968, 9811, 0), "Return to Hammerspike Stoutbeard in the west cavern of the Dwarven Mine.");
		returnToHammerspike.addSubSteps(goDownToHammerspikeAgain);

		killGangMembers = new NpcStep(this, NpcID.FAVOUR_GANGSTER_DWARF, new WorldPoint(2968, 9811, 0),
			"Kill 3 dwarf gang members until Hammerspike gives in. One dwarf gang member should appear after each kill.", true);
		killGangMembers.addAlternateNpcs(NpcID.FAVOUR_GANGSTER_DWARF_2, NpcID.FAVOUR_GANGSTER_DWARF_3);
		talkToHammerspikeFinal = new NpcStep(this, NpcID.FAVOUR_HAMMERSPIKE_STOUTBEARD, new WorldPoint(2968, 9811, 0), "Return to Hammerspike Stoutbeard in the west cavern of the Dwarven Mine.");
		returnToTassie = new NpcStep(this, NpcID.FAVOUR_TASSIE_SLIPCAST, new WorldPoint(3085, 3409, 0), "Return to Tassie Slipcast in the Barbarian Village pottery building.");
		spinPotLid = new ObjectStep(this, ObjectID.POTTERYWHEEL, new WorldPoint(3087, 3409, 0), "Spin the clay into a pot lid.", softClay);
		spinPotLid.addWidgetHighlightWithItemIdRequirement(270, 19, ItemID.POTLID_UNFIRED, true);
		pickUpPot = new ItemStep(this, "Get a pot to put your lid on. There's one in the Barbarian Village helmet shop.", pot);
		firePotLid = new ObjectStep(this, ObjectID.FAI_BARBARIAN_POTTERY_OVEN, new WorldPoint(3085, 3407, 0), "Fire the unfired pot lid.", unfiredPotLid);
		firePotLid.addWidgetHighlightWithItemIdRequirement(270, 19, ItemID.POTLID, true);
		usePotLidOnPot = new DetailedQuestStep(this, "Use the pot lid on a pot.", pot, potLid);
		returnToApothecary = new NpcStep(this, NpcID.APOTHECARY, new WorldPoint(3196, 3404, 0), "Return to the Apothecary in west Varrock.", potWithLid);
		returnToApothecary.addDialogStep("Talk about One Small Favour.");

		returnToHorvik = new NpcStep(this, NpcID.HORVIK_THE_ARMOURER, new WorldPoint(3229, 3437, 0), "Return to Horvik in the armour shop north east of the Varrock square.", pigeonCages5, breathingSalts, herbalTincture);
		returnToHorvik.addDialogStep("I have the tincture and the breathing salts.");
		talkToHorvikFinal = new NpcStep(this, NpcID.HORVIK_THE_ARMOURER, new WorldPoint(3229, 3437, 0), "Talk to Horvik once more in the armour shop north east of the Varrock square.", pigeonCages5);
		talkToHorvikFinal.addDialogStep("I have the five pigeon cages you asked for!");
		returnToSeth = new NpcStep(this, NpcID.FAVOUR_SETH_GROATS, new WorldPoint(3228, 3291, 0), "Return to Seth Groats in the farm north east of Lumbridge, across the river.", chickenCages5);
		returnDownToJohnahus = new ObjectStep(this, ObjectID.HAM_MULTI_TRAPDOOR, new WorldPoint(3166, 3252, 0), "Enter the H.A.M hideout west of Lumbridge and talk to Johanhus Ulsbrecht in there.");
		returnToJohnahus = new NpcStep(this, NpcID.FAVOUR_JOHANHUS_ULSBRECHT, new WorldPoint(3171, 9619, 0), "Return to Johanhus Ulsbrecht in the south of the H.A.M hideout.");
		returnToJohnahus.addDialogStep("You're in luck, I've managed to swing that chicken deal for you.");
		returnToJohnahus.addSubSteps(returnDownToJohnahus);
		returnToAggie = new NpcStep(this, NpcID.AGGIE_1OP, new WorldPoint(3086, 3258, 0), "Return to Aggie in Draynor Village.");
		returnToAggie.addDialogStep("Good news! Jimmy has been released!");
		returnToBrian = new NpcStep(this, NpcID.BRIAN, new WorldPoint(3027, 3249, 0), "Return to Brian in the Port Sarim axe shop.");
		returnToBrian.addDialogStep("I've returned with good news.");

		returnToForester = new NpcStep(this, new int[]{NpcID.JUNGLEFORESTER_F, NpcID.JUNGLEFORESTER_M}, new WorldPoint(2861, 2942, 0), "Return to a Jungle Forester south of Shilo Village. CKR fairy ring or take cart from Brimhaven.", true, sharpenedAxe);
		returnToForester.addDialogStep("Good news, I have your sharpened axe!");
		returnToYanni = new NpcStep(this, NpcID.SHILOANTIQUES, new WorldPoint(2836, 2983, 0), "Return to Yanni Salika in Shilo Village.", redMahog);
		returnToYanni.addDialogStep("Here's the red mahogany you asked for.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToYanni);
		steps.put(5, talkToJungleForester);

		steps.put(10, talkToBrian);
		steps.put(15, talkToBrian);

		steps.put(20, talkToAggie);

		var goTalkToJohanhus = new ConditionalStep(this, goDownToJohanhus);
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

		var goTalkToHammerspike = new ConditionalStep(this, goDownToHammerspike);
		goTalkToHammerspike.addStep(inDwarvenMine, talkToHammerspike);
		steps.put(70, goTalkToHammerspike);

		var goTalkToSanfew = new ConditionalStep(this, goUpToSanfew);
		goTalkToSanfew.addStep(inSanfewRoom, talkToSanfew);

		steps.put(75, goTalkToSanfew);

		var makeGuthixRestForGnome = new ConditionalStep(this, useBowlOnCup);
		makeGuthixRestForGnome.addStep(guthixRest, talkToBleemadge);
		makeGuthixRestForGnome.addStep(or(herbTeaMix, cupOfWater), useHerbsOnCup);

		steps.put(80, makeGuthixRestForGnome);
		steps.put(81, makeGuthixRestForGnome);
		steps.put(82, makeGuthixRestForGnome);
		steps.put(83, makeGuthixRestForGnome);
		steps.put(84, makeGuthixRestForGnome);

		steps.put(86, talkToBleemadgeNoTea);

		steps.put(88, talkToArhein);

		steps.put(90, talkToPhantuwti);

		var investigateWall = new ConditionalStep(this, enterGoblinCave);
		investigateWall.addStep(inGoblinCave, searchWall);

		steps.put(95, investigateWall);

		steps.put(100, talkToCromperty);

		steps.put(105, talkToTindel);

		steps.put(110, talkToRantz);

		steps.put(115, talkToGnormadium);

		var repairLights = new ConditionalStep(this, take1);
		repairLights.addStep(allFull, talkToGnormadiumAgain);
		repairLights.addStep(and(lamp1Full, lamp2Full, lamp3Full, lamp4Full, lamp5Full, lamp6Full, lamp7Full, lamp8Empty, sapphire), put8);
		repairLights.addStep(and(lamp1Full, lamp2Full, lamp3Full, lamp4Full, lamp5Full, lamp6Full, lamp7Full, lamp8Empty), cutSaph);
		repairLights.addStep(and(lamp1Full, lamp2Full, lamp3Full, lamp4Full, lamp5Full, lamp6Full, lamp7Full), take8);
		repairLights.addStep(and(lamp1Full, lamp2Full, lamp3Full, lamp4Full, lamp5Full, lamp6Full, lamp7Empty, opal), put7);
		repairLights.addStep(and(lamp1Full, lamp2Full, lamp3Full, lamp4Full, lamp5Full, lamp6Full, lamp7Empty), cutOpal);
		repairLights.addStep(and(lamp1Full, lamp2Full, lamp3Full, lamp4Full, lamp5Full, lamp6Full), take7);
		repairLights.addStep(and(lamp1Full, lamp2Full, lamp3Full, lamp4Full, lamp5Full, lamp6Empty, redTopaz), put6);
		repairLights.addStep(and(lamp1Full, lamp2Full, lamp3Full, lamp4Full, lamp5Full, lamp6Empty), cutTopaz);
		repairLights.addStep(and(lamp1Full, lamp2Full, lamp3Full, lamp4Full, lamp5Full), take6);
		repairLights.addStep(and(lamp1Full, lamp2Full, lamp3Full, lamp4Full, lamp5Empty, jade), put5);
		repairLights.addStep(and(lamp1Full, lamp2Full, lamp3Full, lamp4Full, lamp5Empty), cutJade);
		repairLights.addStep(and(lamp1Full, lamp2Full, lamp3Full, lamp4Full), take5);
		repairLights.addStep(and(lamp1Full, lamp2Full, lamp3Full, lamp4Empty, sapphire), put4);
		repairLights.addStep(and(lamp1Full, lamp2Full, lamp3Full, lamp4Empty), cutSaph);
		repairLights.addStep(and(lamp1Full, lamp2Full, lamp3Full), take4);
		repairLights.addStep(and(lamp1Full, lamp2Full, lamp3Empty, opal), put3);
		repairLights.addStep(and(lamp1Full, lamp2Full, lamp3Empty), cutOpal);
		repairLights.addStep(and(lamp1Full, lamp2Full), take3);
		repairLights.addStep(and(lamp1Full, lamp2Empty, redTopaz), put2);
		repairLights.addStep(and(lamp1Full, lamp2Empty), cutTopaz);
		repairLights.addStep(lamp1Full, take2);
		repairLights.addStep(and(lamp1Empty, jade), put1);
		repairLights.addStep(lamp1Empty, cutJade);

		steps.put(120, repairLights);

		steps.put(125, talkToGnormadiumAgain);

		steps.put(130, returnToRantz);

		steps.put(135, returnToTindel);

		steps.put(140, returnToCromperty);

		var fightSlagilith = new ConditionalStep(this, getPigeonCages);
		fightSlagilith.addStep(slagilithNearby, killSlagilith);
		fightSlagilith.addStep(inScrollSpot, readScroll);
		fightSlagilith.addStep(inGoblinCave, standNextToSculpture);
		fightSlagilith.addStep(pigeonCages5.alsoCheckBank(), enterGoblinCaveAgain);

		steps.put(145, fightSlagilith);
		steps.put(150, fightSlagilith);

		var freePetra = new ConditionalStep(this, getPigeonCages);
		freePetra.addStep(petraNearby, talkToPetra);
		freePetra.addStep(inScrollSpot, readScrollAgain);
		freePetra.addStep(inGoblinCave, standNextToSculpture);
		freePetra.addStep(pigeonCages5.alsoCheckBank(), enterGoblinCaveAgain);

		steps.put(152, freePetra);
		steps.put(155, freePetra);

		steps.put(160, returnToPhantuwti);
		steps.put(165, returnToPhantuwti2);
		steps.put(170, returnToPhantuwti2);

		var repairVane = new ConditionalStep(this, goUpLadder);
		repairVane.addStep(onRoof, searchVane);
		repairVane.addStep(inSeersVillageUpstairs, goUpToRoof);

		steps.put(175, repairVane);

		var hitVane = new ConditionalStep(this, goUpLadder);
		hitVane.addStep(onRoof, useHammerOnVane);
		hitVane.addStep(inSeersVillageUpstairs, goUpToRoof);

		steps.put(176, hitVane);

		var getVaneBits = new ConditionalStep(this, goUpLadder);
		getVaneBits.addStep(onRoof, searchVaneAgain);
		getVaneBits.addStep(inSeersVillageUpstairs, goUpToRoof);

		steps.put(177, getVaneBits);

		var repairVaneParts = new ConditionalStep(this, useVane123OnAnvil);

		repairVaneParts.addStep(and(addedOrnaments, addedDirectionals, weathervanePillar, onRoof), useVane3);
		repairVaneParts.addStep(and(addedOrnaments, directionals, onRoof), useVane2);
		repairVaneParts.addStep(and(ornament, onRoof), useVane1);
		repairVaneParts.addStep(onRoof, goDownFromRoof);
		repairVaneParts.addStep(and(hasOrUsedDirectionals, hasOrUsedOrnament, hasOrUsedWeathervanePillar, inSeersVillageUpstairs), goBackUpToRoof);
		repairVaneParts.addStep(inSeersVillageUpstairs, goDownLadderToSeers);
		repairVaneParts.addStep(and(hasOrUsedDirectionals, hasOrUsedOrnament, hasOrUsedWeathervanePillar), goBackUpLadder);
		repairVaneParts.addStep(and(hasOrUsedDirectionals, hasOrUsedOrnament), useVane3OnAnvil);
		repairVaneParts.addStep(and(hasOrUsedOrnament, hasOrUsedWeathervanePillar), useVane1OnAnvil);
		repairVaneParts.addStep(and(hasOrUsedDirectionals, hasOrUsedWeathervanePillar), useVane2OnAnvil);
		repairVaneParts.addStep(hasOrUsedOrnament, useVane13OnAnvil);
		repairVaneParts.addStep(hasOrUsedWeathervanePillar, useVane12OnAnvil);
		repairVaneParts.addStep(hasOrUsedDirectionals, useVane23OnAnvil);

		steps.put(180, repairVaneParts);

		var reportBackToPhantuwti = new ConditionalStep(this, finishWithPhantuwti);
		reportBackToPhantuwti.addStep(inSeersVillageUpstairs, goDownLadderToPhantuwti);
		reportBackToPhantuwti.addStep(onRoof, goFromRoofToPhantuwti);

		steps.put(185, reportBackToPhantuwti);

		steps.put(190, returnToArhein);

		steps.put(195, returnToBleemadge);

		var goAndReturnToSanfew = new ConditionalStep(this, returnUpToSanfew);
		goAndReturnToSanfew.addStep(inSanfewRoom, returnToSanfew);
		steps.put(200, goAndReturnToSanfew);

		var dealWithHammerspike = new ConditionalStep(this, goDownToHammerspikeAgain);
		dealWithHammerspike.addStep(inDwarvenMine, returnToHammerspike);

		steps.put(205, dealWithHammerspike);

		var sortOutGangMembers = new ConditionalStep(this, goDownToHammerspikeAgain);
		sortOutGangMembers.addStep(inDwarvenMine, killGangMembers);

		steps.put(210, sortOutGangMembers);
		steps.put(215, sortOutGangMembers);
		steps.put(220, sortOutGangMembers);
		steps.put(225, dealWithHammerspike);

		steps.put(230, returnToTassie);

		var makePotAndReturnToApoth = new ConditionalStep(this, spinPotLid);
		makePotAndReturnToApoth.addStep(potWithLid, returnToApothecary);
		makePotAndReturnToApoth.addStep(and(potLid, pot), usePotLidOnPot);
		makePotAndReturnToApoth.addStep(potLid, pickUpPot);
		makePotAndReturnToApoth.addStep(unfiredPotLid, firePotLid);

		steps.put(235, makePotAndReturnToApoth);

		steps.put(240, returnToHorvik);

		steps.put(245, talkToHorvikFinal);

		steps.put(250, returnToSeth);

		var goFinishWithJohanhus = new ConditionalStep(this, returnDownToJohnahus);
		goFinishWithJohanhus.addStep(inHamBase, returnToJohnahus);

		steps.put(255, goFinishWithJohanhus);

		steps.put(260, returnToAggie);

		steps.put(265, returnToBrian);

		steps.put(270, returnToForester);

		steps.put(275, returnToYanni);

		return steps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.RUNE_MYSTERIES, QuestState.FINISHED),
			new QuestRequirement(QuestHelperQuest.DRUIDIC_RITUAL, QuestState.FINISHED),
			new QuestRequirement(QuestHelperQuest.SHILO_VILLAGE, QuestState.FINISHED),
			new SkillRequirement(Skill.AGILITY, 36, true),
			new SkillRequirement(Skill.CRAFTING, 25, true),
			new SkillRequirement(Skill.HERBLORE, 18, true),
			new SkillRequirement(Skill.SMITHING, 30, true)
		);
	}


	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			steelBars4,
			bronzeBar,
			ironBar,
			chisel,
			guam2,
			marrentill,
			harralander,
			hammer,
			emptyCup,
			pot,
			hotWaterBowl
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			draynorVillageTeleports,
			lumbridgeTeleports,
			varrockTeleports,
			taverleyOrFaladorTeleports,
			camelotTeleports,
			fishingGuildAndDwarvenMineTeleports,
			ardougneTeleports,
			khazardTeleports,
			feldipHillsTeleports,
			opal2,
			jade2,
			redTopaz2,
			coins3000,
			pickaxe
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Slagilith (level 92)",
			"3x Dwarf gang members (level 44)"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("10,000 Experience Lamps (Any skill over level 30)", ItemID.THOSF_REWARD_LAMP, 2),
			new ItemReward("A Steel Keyring", ItemID.FAVOUR_KEY_RING, 1)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("The ability to make Guthix Rest tea."),
			new UnlockReward("Gnome Glider in Feldip Hills.")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			talkToYanni
		)));

		sections.add(new PanelDetails("A few small favours", List.of(
			talkToJungleForester,
			talkToBrian,
			talkToAggie,
			talkToJohanhus,
			talkToFred,
			talkToSeth,
			talkToHorvik,
			talkToApoth,
			talkToTassie,
			talkToHammerspike,
			talkToSanfew,
			makeGuthixRest,
			talkToBleemadge,
			talkToArhein,
			talkToPhantuwti,
			enterGoblinCave,
			searchWall,
			talkToCromperty,
			talkToTindel,
			talkToRantz,
			talkToGnormadium,
			fixAllLamps
		), List.of(
			chisel,
			steelBars3,
			emptyCup,
			hotWaterBowl,
			guam2,
			marrentill,
			harralander
		), List.of(
			opal2,
			jade2,
			redTopaz2,
			coins3000
		)));

		sections.add(new PanelDetails("Completing the favours", List.of(
			talkToGnormadiumAgain,
			returnToRantz,
			returnToTindel,
			returnToCromperty,
			getPigeonCages,
			enterGoblinCaveAgain,
			standNextToSculpture,
			killSlagilith,
			readScrollAgain,
			talkToPetra,
			returnToPhantuwti,
			searchVane,
			useHammerOnVane,
			searchVaneAgain,
			useVane123OnAnvil,
			goBackUpLadder,
			finishWithPhantuwti,
			returnToArhein,
			returnToBleemadge,
			returnToSanfew,
			returnToHammerspike,
			killGangMembers,
			talkToHammerspikeFinal,
			returnToTassie,
			spinPotLid,
			firePotLid,
			pickUpPot,
			usePotLidOnPot,
			returnToApothecary,
			returnToHorvik,
			talkToHorvikFinal,
			returnToSeth,
			returnToJohnahus,
			returnToAggie,
			returnToBrian,
			returnToForester,
			returnToYanni
		), List.of(
			bronzeBar,
			ironBar,
			steelBar,
			hammer,
			pot
		), List.of(
			combatGear,
			pickaxe
		)));

		return sections;
	}
}
