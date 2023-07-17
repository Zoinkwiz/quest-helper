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
package com.questhelper;

import com.questhelper.helpers.achievementdiaries.ardougne.ArdougneEasy;
import com.questhelper.helpers.achievementdiaries.ardougne.ArdougneElite;
import com.questhelper.helpers.achievementdiaries.ardougne.ArdougneHard;
import com.questhelper.helpers.achievementdiaries.ardougne.ArdougneMedium;
import com.questhelper.helpers.achievementdiaries.desert.DesertEasy;
import com.questhelper.helpers.achievementdiaries.desert.DesertElite;
import com.questhelper.helpers.achievementdiaries.desert.DesertHard;
import com.questhelper.helpers.achievementdiaries.desert.DesertMedium;
import com.questhelper.helpers.achievementdiaries.falador.FaladorEasy;
import com.questhelper.helpers.achievementdiaries.falador.FaladorElite;
import com.questhelper.helpers.achievementdiaries.falador.FaladorHard;
import com.questhelper.helpers.achievementdiaries.falador.FaladorMedium;
import com.questhelper.helpers.achievementdiaries.fremennik.DagRouteHelper;
import com.questhelper.helpers.achievementdiaries.fremennik.FremennikEasy;
import com.questhelper.helpers.achievementdiaries.fremennik.FremennikElite;
import com.questhelper.helpers.achievementdiaries.fremennik.FremennikHard;
import com.questhelper.helpers.achievementdiaries.fremennik.FremennikMedium;
import com.questhelper.helpers.achievementdiaries.kandarin.KandarinEasy;
import com.questhelper.helpers.achievementdiaries.kandarin.KandarinElite;
import com.questhelper.helpers.achievementdiaries.kandarin.KandarinHard;
import com.questhelper.helpers.achievementdiaries.kandarin.KandarinMedium;
import com.questhelper.helpers.achievementdiaries.karamja.KaramjaEasy;
import com.questhelper.helpers.achievementdiaries.karamja.KaramjaElite;
import com.questhelper.helpers.achievementdiaries.karamja.KaramjaHard;
import com.questhelper.helpers.achievementdiaries.karamja.KaramjaMedium;
import com.questhelper.helpers.achievementdiaries.kourend.KourendEasy;
import com.questhelper.helpers.achievementdiaries.kourend.KourendElite;
import com.questhelper.helpers.achievementdiaries.kourend.KourendHard;
import com.questhelper.helpers.achievementdiaries.kourend.KourendMedium;
import com.questhelper.helpers.achievementdiaries.lumbridgeanddraynor.LumbridgeEasy;
import com.questhelper.helpers.achievementdiaries.lumbridgeanddraynor.LumbridgeElite;
import com.questhelper.helpers.achievementdiaries.lumbridgeanddraynor.LumbridgeHard;
import com.questhelper.helpers.achievementdiaries.lumbridgeanddraynor.LumbridgeMedium;
import com.questhelper.helpers.achievementdiaries.morytania.MorytaniaEasy;
import com.questhelper.helpers.achievementdiaries.morytania.MorytaniaElite;
import com.questhelper.helpers.achievementdiaries.morytania.MorytaniaHard;
import com.questhelper.helpers.achievementdiaries.morytania.MorytaniaMedium;
import com.questhelper.helpers.achievementdiaries.varrock.VarrockEasy;
import com.questhelper.helpers.achievementdiaries.varrock.VarrockElite;
import com.questhelper.helpers.achievementdiaries.varrock.VarrockHard;
import com.questhelper.helpers.achievementdiaries.varrock.VarrockMedium;
import com.questhelper.helpers.achievementdiaries.westernprovinces.WesternEasy;
import com.questhelper.helpers.achievementdiaries.westernprovinces.WesternElite;
import com.questhelper.helpers.achievementdiaries.westernprovinces.WesternHard;
import com.questhelper.helpers.achievementdiaries.westernprovinces.WesternMedium;
import com.questhelper.helpers.achievementdiaries.wilderness.WildernessEasy;
import com.questhelper.helpers.achievementdiaries.wilderness.WildernessElite;
import com.questhelper.helpers.achievementdiaries.wilderness.WildernessHard;
import com.questhelper.helpers.achievementdiaries.wilderness.WildernessMedium;
import com.questhelper.helpers.miniquests.hisfaithfulservants.BarrowsHelper;
import com.questhelper.helpers.miniquests.hisfaithfulservants.HisFaithfulServants;
import com.questhelper.helpers.mischelpers.allneededitems.AllNeededItems;
import com.questhelper.helpers.mischelpers.herbrun.HerbRun;
import com.questhelper.helpers.quests.akingdomdivided.AKingdomDivided;
import com.questhelper.helpers.miniquests.alfredgrimhandsbarcrawl.AlfredGrimhandsBarcrawl;
import com.questhelper.helpers.quests.anightatthetheatre.ANightAtTheTheatre;
import com.questhelper.helpers.quests.animalmagnetism.AnimalMagnetism;
import com.questhelper.helpers.quests.anothersliceofham.AnotherSliceOfHam;
import com.questhelper.helpers.quests.aporcineofinterest.APorcineOfInterest;
import com.questhelper.helpers.miniquests.architecturalalliance.ArchitecturalAlliance;
import com.questhelper.helpers.quests.asoulsbane.ASoulsBane;
import com.questhelper.helpers.quests.atailoftwocats.ATailOfTwoCats;
import com.questhelper.helpers.quests.atasteofhope.ATasteOfHope;
import com.questhelper.helpers.quests.bearyoursoul.BearYourSoul;
import com.questhelper.helpers.quests.belowicemountain.BelowIceMountain;
import com.questhelper.helpers.quests.beneathcursedsands.BeneathCursedSands;
import com.questhelper.helpers.quests.betweenarock.BetweenARock;
import com.questhelper.helpers.quests.bigchompybirdhunting.BigChompyBirdHunting;
import com.questhelper.helpers.quests.biohazard.Biohazard;
import com.questhelper.helpers.quests.blackknightfortress.BlackKnightFortress;
import com.questhelper.helpers.quests.bonevoyage.BoneVoyage;
import com.questhelper.helpers.quests.cabinfever.CabinFever;
import com.questhelper.helpers.quests.clientofkourend.ClientOfKourend;
import com.questhelper.helpers.quests.clocktower.ClockTower;
import com.questhelper.helpers.quests.coldwar.ColdWar;
import com.questhelper.helpers.quests.contact.Contact;
import com.questhelper.helpers.quests.cooksassistant.CooksAssistant;
import com.questhelper.helpers.quests.creatureoffenkenstrain.CreatureOfFenkenstrain;
import com.questhelper.helpers.miniquests.curseoftheemptylord.CurseOfTheEmptyLord;
import com.questhelper.helpers.miniquests.daddyshome.DaddysHome;
import com.questhelper.helpers.quests.darknessofhallowvale.DarknessOfHallowvale;
import com.questhelper.helpers.quests.deathplateau.DeathPlateau;
import com.questhelper.helpers.quests.deathtothedorgeshuun.DeathToTheDorgeshuun;
import com.questhelper.helpers.quests.demonslayer.DemonSlayer;
import com.questhelper.helpers.quests.deserttreasure.DesertTreasure;
import com.questhelper.helpers.quests.deserttreasureii.DesertTreasureII;
import com.questhelper.helpers.quests.deviousminds.DeviousMinds;
import com.questhelper.helpers.quests.doricsquest.DoricsQuest;
import com.questhelper.helpers.quests.dragonslayer.DragonSlayer;
import com.questhelper.helpers.quests.dragonslayerii.DragonSlayerII;
import com.questhelper.helpers.quests.dreammentor.DreamMentor;
import com.questhelper.helpers.quests.druidicritual.DruidicRitual;
import com.questhelper.helpers.quests.dwarfcannon.DwarfCannon;
import com.questhelper.helpers.quests.eadgarsruse.EadgarsRuse;
import com.questhelper.helpers.quests.eaglespeak.EaglesPeak;
import com.questhelper.helpers.quests.elementalworkshopi.ElementalWorkshopI;
import com.questhelper.helpers.quests.elementalworkshopii.ElementalWorkshopII;
import com.questhelper.helpers.quests.enakhraslament.EnakhrasLament;
import com.questhelper.helpers.miniquests.enchantedkey.EnchantedKey;
import com.questhelper.helpers.quests.enlightenedjourney.EnlightenedJourney;
import com.questhelper.helpers.quests.entertheabyss.EnterTheAbyss;
import com.questhelper.helpers.quests.ernestthechicken.ErnestTheChicken;
import com.questhelper.helpers.quests.fairytalei.FairytaleI;
import com.questhelper.helpers.quests.fairytaleii.FairytaleII;
import com.questhelper.helpers.quests.familycrest.FamilyCrest;
import com.questhelper.helpers.miniquests.familypest.FamilyPest;
import com.questhelper.helpers.quests.fightarena.FightArena;
import com.questhelper.helpers.quests.fishingcontest.FishingContest;
import com.questhelper.helpers.quests.forgettabletale.ForgettableTale;
import com.questhelper.helpers.quests.gardenoftranquility.GardenOfTranquillity;
import com.questhelper.helpers.quests.gertrudescat.GertrudesCat;
import com.questhelper.helpers.quests.gettingahead.GettingAhead;
import com.questhelper.helpers.quests.ghostsahoy.GhostsAhoy;
import com.questhelper.helpers.quests.goblindiplomacy.GoblinDiplomacy;
import com.questhelper.helpers.quests.grimtales.GrimTales;
import com.questhelper.helpers.quests.hauntedmine.HauntedMine;
import com.questhelper.helpers.quests.hazeelcult.HazeelCult;
import com.questhelper.helpers.quests.heroesquest.HeroesQuest;
import com.questhelper.helpers.quests.holygrail.HolyGrail;
import com.questhelper.helpers.miniquests.hopespearswill.HopespearsWill;
import com.questhelper.helpers.quests.horrorfromthedeep.HorrorFromTheDeep;
import com.questhelper.helpers.quests.icthlarinslittlehelper.IcthlarinsLittleHelper;
import com.questhelper.helpers.quests.impcatcher.ImpCatcher;
import com.questhelper.helpers.quests.inaidofthemyreque.InAidOfTheMyreque;
import com.questhelper.helpers.quests.insearchofknowledge.InSearchOfKnowledge;
import com.questhelper.helpers.quests.insearchofthemyreque.InSearchOfTheMyreque;
import com.questhelper.helpers.quests.junglepotion.JunglePotion;
import com.questhelper.helpers.quests.kingsransom.KingsRansom;
import com.questhelper.helpers.mischelpers.knightswaves.KnightWaves;
import com.questhelper.helpers.miniquests.lairoftarnrazorlor.LairOfTarnRazorlor;
import com.questhelper.helpers.quests.landofthegoblins.LandOfTheGoblins;
import com.questhelper.helpers.quests.legendsquest.LegendsQuest;
import com.questhelper.helpers.quests.lostcity.LostCity;
import com.questhelper.helpers.quests.lunardiplomacy.LunarDiplomacy;
import com.questhelper.helpers.quests.makingfriendswithmyarm.MakingFriendsWithMyArm;
import com.questhelper.helpers.quests.makinghistory.MakingHistory;
import com.questhelper.helpers.quests.merlinscrystal.MerlinsCrystal;
import com.questhelper.helpers.quests.misthalinmystery.MisthalinMystery;
import com.questhelper.helpers.quests.monkeymadnessi.MonkeyMadnessI;
import com.questhelper.helpers.quests.monkeymadnessii.MonkeyMadnessII;
import com.questhelper.helpers.quests.monksfriend.MonksFriend;
import com.questhelper.helpers.quests.mountaindaughter.MountainDaughter;
import com.questhelper.helpers.quests.mourningsendparti.MourningsEndPartI;
import com.questhelper.helpers.quests.mourningsendpartii.MourningsEndPartII;
import com.questhelper.helpers.quests.murdermystery.MurderMystery;
import com.questhelper.helpers.quests.myarmsbigadventure.MyArmsBigAdventure;
import com.questhelper.helpers.quests.naturespirit.NatureSpirit;
import com.questhelper.helpers.quests.observatoryquest.ObservatoryQuest;
import com.questhelper.helpers.quests.olafsquest.OlafsQuest;
import com.questhelper.helpers.quests.onesmallfavour.OneSmallFavour;
import com.questhelper.helpers.quests.piratestreasure.PiratesTreasure;
import com.questhelper.helpers.quests.plaguecity.PlagueCity;
import com.questhelper.helpers.quests.priestinperil.PriestInPeril;
import com.questhelper.helpers.quests.princealirescue.PrinceAliRescue;
import com.questhelper.helpers.quests.ragandboneman.RagAndBoneManI;
import com.questhelper.helpers.quests.ragandboneman.RagAndBoneManII;
import com.questhelper.helpers.quests.ratcatchers.RatCatchers;
import com.questhelper.helpers.quests.recipefordisaster.RFDAwowogei;
import com.questhelper.helpers.quests.recipefordisaster.RFDDwarf;
import com.questhelper.helpers.quests.recipefordisaster.RFDEvilDave;
import com.questhelper.helpers.quests.recipefordisaster.RFDFinal;
import com.questhelper.helpers.quests.recipefordisaster.RFDGoblins;
import com.questhelper.helpers.quests.recipefordisaster.RFDLumbridgeGuide;
import com.questhelper.helpers.quests.recipefordisaster.RFDPiratePete;
import com.questhelper.helpers.quests.recipefordisaster.RFDSirAmikVarze;
import com.questhelper.helpers.quests.recipefordisaster.RFDSkrachUglogwee;
import com.questhelper.helpers.quests.recipefordisaster.RFDStart;
import com.questhelper.helpers.quests.recruitmentdrive.RecruitmentDrive;
import com.questhelper.helpers.quests.regicide.Regicide;
import com.questhelper.helpers.quests.romeoandjuliet.RomeoAndJuliet;
import com.questhelper.helpers.quests.rovingelves.RovingElves;
import com.questhelper.helpers.quests.royaltrouble.RoyalTrouble;
import com.questhelper.helpers.quests.rumdeal.RumDeal;
import com.questhelper.helpers.quests.runemysteries.RuneMysteries;
import com.questhelper.helpers.quests.scorpioncatcher.ScorpionCatcher;
import com.questhelper.helpers.quests.seaslug.SeaSlug;
import com.questhelper.helpers.quests.secretsofthenorth.SecretsOfTheNorth;
import com.questhelper.helpers.quests.shadesofmortton.ShadesOfMortton;
import com.questhelper.helpers.quests.shadowofthestorm.ShadowOfTheStorm;
import com.questhelper.helpers.quests.sheepherder.SheepHerder;
import com.questhelper.helpers.quests.sheepshearer.SheepShearer;
import com.questhelper.helpers.quests.shieldofarrav.ShieldOfArravBlackArmGang;
import com.questhelper.helpers.quests.shieldofarrav.ShieldOfArravPhoenixGang;
import com.questhelper.helpers.quests.shilovillage.ShiloVillage;
import com.questhelper.helpers.quests.sinsofthefather.SinsOfTheFather;
import com.questhelper.helpers.miniquests.skippyandthemogres.SkippyAndTheMogres;
import com.questhelper.helpers.quests.sleepinggiants.SleepingGiants;
import com.questhelper.helpers.quests.songoftheelves.SongOfTheElves;
import com.questhelper.helpers.quests.spiritsoftheelid.SpiritsOfTheElid;
import com.questhelper.helpers.quests.swansong.SwanSong;
import com.questhelper.helpers.quests.taibwowannaitrio.TaiBwoWannaiTrio;
import com.questhelper.helpers.quests.taleoftherighteous.TaleOfTheRighteous;
import com.questhelper.helpers.quests.tearsofguthix.TearsOfGuthix;
import com.questhelper.helpers.quests.templeofikov.TempleOfIkov;
import com.questhelper.helpers.quests.templeoftheeye.TempleOfTheEye;
import com.questhelper.helpers.quests.theascentofarceuus.TheAscentOfArceuus;
import com.questhelper.helpers.quests.thecorsaircurse.TheCorsairCurse;
import com.questhelper.helpers.quests.thedepthsofdespair.TheDepthsOfDespair;
import com.questhelper.helpers.quests.thedigsite.TheDigSite;
import com.questhelper.helpers.quests.theeyesofglouphrie.TheEyesOfGlouphrie;
import com.questhelper.helpers.quests.thefeud.TheFeud;
import com.questhelper.helpers.quests.theforsakentower.TheForsakenTower;
import com.questhelper.helpers.quests.thefremennikexiles.TheFremennikExiles;
import com.questhelper.helpers.quests.thefremennikisles.TheFremennikIsles;
import com.questhelper.helpers.quests.thefremenniktrials.TheFremennikTrials;
import com.questhelper.helpers.quests.thegardenofdeath.TheGardenOfDeath;
import com.questhelper.helpers.miniquests.thegeneralsshadow.TheGeneralsShadow;
import com.questhelper.helpers.quests.thegiantdwarf.TheGiantDwarf;
import com.questhelper.helpers.quests.thegolem.TheGolem;
import com.questhelper.helpers.quests.thegrandtree.TheGrandTree;
import com.questhelper.helpers.quests.thegreatbrainrobbery.TheGreatBrainRobbery;
import com.questhelper.helpers.quests.thehandinthesand.TheHandInTheSand;
import com.questhelper.helpers.quests.theknightssword.TheKnightsSword;
import com.questhelper.helpers.quests.thelosttribe.TheLostTribe;
import com.questhelper.helpers.miniquests.themagearenai.TheMageArenaI;
import com.questhelper.helpers.miniquests.themagearenaii.MA2Locator;
import com.questhelper.helpers.miniquests.themagearenaii.TheMageArenaII;
import com.questhelper.helpers.quests.thequeenofthieves.TheQueenOfThieves;
import com.questhelper.helpers.quests.therestlessghost.TheRestlessGhost;
import com.questhelper.helpers.quests.theslugmenace.TheSlugMenace;
import com.questhelper.helpers.quests.thetouristtrap.TheTouristTrap;
import com.questhelper.helpers.quests.throneofmiscellania.ThroneOfMiscellania;
import com.questhelper.helpers.quests.toweroflife.TowerOfLife;
import com.questhelper.helpers.quests.treegnomevillage.TreeGnomeVillage;
import com.questhelper.helpers.quests.tribaltotem.TribalTotem;
import com.questhelper.helpers.quests.trollromance.TrollRomance;
import com.questhelper.helpers.quests.trollstronghold.TrollStronghold;
import com.questhelper.helpers.quests.undergroundpass.UndergroundPass;
import com.questhelper.helpers.quests.vampyreslayer.VampyreSlayer;
import com.questhelper.helpers.quests.wanted.Wanted;
import com.questhelper.helpers.quests.watchtower.Watchtower;
import com.questhelper.helpers.quests.waterfallquest.WaterfallQuest;
import com.questhelper.helpers.quests.whatliesbelow.WhatLiesBelow;
import com.questhelper.helpers.quests.witchshouse.WitchsHouse;
import com.questhelper.helpers.quests.witchspotion.WitchsPotion;
import com.questhelper.helpers.quests.xmarksthespot.XMarksTheSpot;
import com.questhelper.helpers.quests.zogreflesheaters.ZogreFleshEaters;
import com.questhelper.helpers.skills.agility.Agility;
import com.questhelper.helpers.skills.woodcutting.Woodcutting;
import com.questhelper.helpers.skills.woodcuttingmember.WoodcuttingMember;
import com.questhelper.playerquests.cookshelper.CooksHelper;
import com.questhelper.questhelpers.QuestDetails;
import com.questhelper.questhelpers.QuestHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.ScriptID;
import net.runelite.api.Skill;
import net.runelite.client.config.ConfigManager;

public enum QuestHelperQuest
{
	//Free Quests
	BELOW_ICE_MOUNTAIN(new BelowIceMountain(), Quest.BELOW_ICE_MOUNTAIN, QuestVarbits.QUEST_BELOW_ICE_MOUNTAIN, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	BLACK_KNIGHTS_FORTRESS(new BlackKnightFortress(), Quest.BLACK_KNIGHTS_FORTRESS, QuestVarPlayer.QUEST_BLACK_KNIGHTS_FORTRESS, QuestDetails.Type.F2P, QuestDetails.Difficulty.INTERMEDIATE),
	COOKS_ASSISTANT(new CooksAssistant(), Quest.COOKS_ASSISTANT, QuestVarPlayer.QUEST_COOKS_ASSISTANT, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	THE_CORSAIR_CURSE(new TheCorsairCurse(), Quest.THE_CORSAIR_CURSE, QuestVarbits.QUEST_THE_CORSAIR_CURSE, QuestDetails.Type.F2P, QuestDetails.Difficulty.INTERMEDIATE),
	DEMON_SLAYER(new DemonSlayer(), Quest.DEMON_SLAYER, QuestVarbits.QUEST_DEMON_SLAYER, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	DORICS_QUEST(new DoricsQuest(), Quest.DORICS_QUEST, QuestVarPlayer.QUEST_DORICS_QUEST, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	DRAGON_SLAYER_I(new DragonSlayer(), Quest.DRAGON_SLAYER_I, QuestVarPlayer.QUEST_DRAGON_SLAYER_I, QuestDetails.Type.F2P, QuestDetails.Difficulty.EXPERIENCED),
	ERNEST_THE_CHICKEN(new ErnestTheChicken(), Quest.ERNEST_THE_CHICKEN, QuestVarPlayer.QUEST_ERNEST_THE_CHICKEN, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	GOBLIN_DIPLOMACY(new GoblinDiplomacy(), Quest.GOBLIN_DIPLOMACY, QuestVarbits.QUEST_GOBLIN_DIPLOMACY, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	IMP_CATCHER(new ImpCatcher(), Quest.IMP_CATCHER, QuestVarPlayer.QUEST_IMP_CATCHER, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	THE_KNIGHTS_SWORD(new TheKnightsSword(), Quest.THE_KNIGHTS_SWORD, QuestVarPlayer.QUEST_THE_KNIGHTS_SWORD, QuestDetails.Type.F2P, QuestDetails.Difficulty.INTERMEDIATE),
	MISTHALIN_MYSTERY(new MisthalinMystery(), Quest.MISTHALIN_MYSTERY, QuestVarbits.QUEST_MISTHALIN_MYSTERY, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	PIRATES_TREASURE(new PiratesTreasure(), Quest.PIRATES_TREASURE, QuestVarPlayer.QUEST_PIRATES_TREASURE, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	PRINCE_ALI_RESCUE(new PrinceAliRescue(), Quest.PRINCE_ALI_RESCUE, QuestVarPlayer.QUEST_PRINCE_ALI_RESCUE, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	THE_RESTLESS_GHOST(new TheRestlessGhost(), Quest.THE_RESTLESS_GHOST, QuestVarPlayer.QUEST_THE_RESTLESS_GHOST, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	ROMEO__JULIET(new RomeoAndJuliet(), Quest.ROMEO__JULIET, QuestVarPlayer.QUEST_ROMEO_AND_JULIET, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	RUNE_MYSTERIES(new RuneMysteries(), Quest.RUNE_MYSTERIES, QuestVarPlayer.QUEST_RUNE_MYSTERIES, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	SHEEP_SHEARER(new SheepShearer(), Quest.SHEEP_SHEARER, QuestVarPlayer.QUEST_SHEEP_SHEARER, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	SHIELD_OF_ARRAV_PHOENIX_GANG(new ShieldOfArravPhoenixGang(), Quest.SHIELD_OF_ARRAV.getId(), "Shield of Arrav - Phoenix Gang", QuestVarPlayer.QUEST_SHIELD_OF_ARRAV, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	SHIELD_OF_ARRAV_BLACK_ARM_GANG(new ShieldOfArravBlackArmGang(), Quest.SHIELD_OF_ARRAV.getId(), "Shield of Arrav - Black Arm Gang", QuestVarPlayer.QUEST_SHIELD_OF_ARRAV_STATE_146, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	VAMPYRE_SLAYER(new VampyreSlayer(), Quest.VAMPYRE_SLAYER, QuestVarPlayer.QUEST_VAMPYRE_SLAYER, QuestDetails.Type.F2P, QuestDetails.Difficulty.INTERMEDIATE),
	WITCHS_POTION(new WitchsPotion(), Quest.WITCHS_POTION, QuestVarPlayer.QUEST_WITCHS_POTION, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),
	X_MARKS_THE_SPOT(new XMarksTheSpot(), Quest.X_MARKS_THE_SPOT, QuestVarbits.QUEST_X_MARKS_THE_SPOT, QuestDetails.Type.F2P, QuestDetails.Difficulty.NOVICE),

	//Members' Quests
	ANIMAL_MAGNETISM(new AnimalMagnetism(), Quest.ANIMAL_MAGNETISM.getId(), "Animal Magnetism", QuestVarbits.QUEST_ANIMAL_MAGNETISM, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	ANOTHER_SLICE_OF_HAM(new AnotherSliceOfHam(), Quest.ANOTHER_SLICE_OF_HAM, QuestVarbits.QUEST_ANOTHER_SLICE_OF_HAM, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	BENEATH_CURSED_SANDS(new BeneathCursedSands(), Quest.BENEATH_CURSED_SANDS, QuestVarbits.QUEST_BENEATH_CURSED_SANDS, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	BETWEEN_A_ROCK(new BetweenARock(), Quest.BETWEEN_A_ROCK, QuestVarbits.QUEST_BETWEEN_A_ROCK, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	BIG_CHOMPY_BIRD_HUNTING(new BigChompyBirdHunting(), Quest.BIG_CHOMPY_BIRD_HUNTING, QuestVarPlayer.QUEST_BIG_CHOMPY_BIRD_HUNTING, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	BIOHAZARD(new Biohazard(), Quest.BIOHAZARD, QuestVarPlayer.QUEST_BIOHAZARD, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	CABIN_FEVER(new CabinFever(), Quest.CABIN_FEVER, QuestVarPlayer.QUEST_CABIN_FEVER, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	CLOCK_TOWER(new ClockTower(), Quest.CLOCK_TOWER, QuestVarPlayer.QUEST_CLOCK_TOWER, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	COLD_WAR(new ColdWar(), Quest.COLD_WAR, QuestVarbits.QUEST_COLD_WAR, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	CONTACT(new Contact(), Quest.CONTACT, QuestVarbits.QUEST_CONTACT, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	CREATURE_OF_FENKENSTRAIN(new CreatureOfFenkenstrain(), Quest.CREATURE_OF_FENKENSTRAIN, QuestVarPlayer.QUEST_CREATURE_OF_FENKENSTRAIN, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	DARKNESS_OF_HALLOWVALE(new DarknessOfHallowvale(), Quest.DARKNESS_OF_HALLOWVALE, QuestVarbits.QUEST_DARKNESS_OF_HALLOWVALE, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	DEATH_PLATEAU(new DeathPlateau(), Quest.DEATH_PLATEAU, QuestVarPlayer.QUEST_DEATH_PLATEAU, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	DEATH_TO_THE_DORGESHUUN(new DeathToTheDorgeshuun(), Quest.DEATH_TO_THE_DORGESHUUN, QuestVarbits.QUEST_DEATH_TO_THE_DORGESHUUN, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	THE_DEPTHS_OF_DESPAIR(new TheDepthsOfDespair(), Quest.THE_DEPTHS_OF_DESPAIR, QuestVarbits.QUEST_THE_DEPTHS_OF_DESPAIR, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	DESERT_TREASURE(new DesertTreasure(), Quest.DESERT_TREASURE_I, QuestVarbits.QUEST_DESERT_TREASURE, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	DESERT_TREASURE_II(new DesertTreasureII(), "Desert Treasure II", QuestVarbits.QUEST_DESERT_TREASURE_II, 1, QuestDetails.Type.P2P, QuestDetails.Difficulty.GRANDMASTER),
	DEVIOUS_MINDS(new DeviousMinds(), Quest.DEVIOUS_MINDS, QuestVarbits.QUEST_DEVIOUS_MINDS, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	THE_DIG_SITE(new TheDigSite(), Quest.THE_DIG_SITE, QuestVarPlayer.QUEST_THE_DIG_SITE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	DRAGON_SLAYER_II(new DragonSlayerII(), Quest.DRAGON_SLAYER_II, QuestVarbits.QUEST_DRAGON_SLAYER_II, QuestDetails.Type.P2P, QuestDetails.Difficulty.GRANDMASTER),
	DREAM_MENTOR(new DreamMentor(), Quest.DREAM_MENTOR, QuestVarbits.QUEST_DREAM_MENTOR, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	DRUIDIC_RITUAL(new DruidicRitual(), Quest.DRUIDIC_RITUAL, QuestVarPlayer.QUEST_DRUIDIC_RITUAL, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	DWARF_CANNON(new DwarfCannon(), Quest.DWARF_CANNON, QuestVarPlayer.QUEST_DWARF_CANNON, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	EADGARS_RUSE(new EadgarsRuse(), Quest.EADGARS_RUSE, QuestVarPlayer.QUEST_EADGARS_RUSE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	EAGLES_PEAK(new EaglesPeak(), Quest.EAGLES_PEAK, QuestVarbits.QUEST_EAGLES_PEAK, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	ELEMENTAL_WORKSHOP_I(new ElementalWorkshopI(), Quest.ELEMENTAL_WORKSHOP_I, QuestVarPlayer.QUEST_ELEMENTAL_WORKSHOP_I, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	ELEMENTAL_WORKSHOP_II(new ElementalWorkshopII(), Quest.ELEMENTAL_WORKSHOP_II, QuestVarbits.QUEST_ELEMENTAL_WORKSHOP_II, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	ENAKHRAS_LAMENT(new EnakhrasLament(), Quest.ENAKHRAS_LAMENT, QuestVarbits.QUEST_ENAKHRAS_LAMENT, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	ENLIGHTENED_JOURNEY(new EnlightenedJourney(), Quest.ENLIGHTENED_JOURNEY, QuestVarbits.QUEST_ENLIGHTENED_JOURNEY, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	THE_EYES_OF_GLOUPHRIE(new TheEyesOfGlouphrie(), Quest.THE_EYES_OF_GLOUPHRIE, QuestVarbits.QUEST_THE_EYES_OF_GLOUPHRIE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	FAIRYTALE_I__GROWING_PAINS(new FairytaleI(), Quest.FAIRYTALE_I__GROWING_PAINS, QuestVarbits.QUEST_FAIRYTALE_I_GROWING_PAINS, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	FAIRYTALE_II__CURE_A_QUEEN(new FairytaleII(), Quest.FAIRYTALE_II__CURE_A_QUEEN, QuestVarbits.QUEST_FAIRYTALE_II_CURE_A_QUEEN, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	FAMILY_CREST(new FamilyCrest(), Quest.FAMILY_CREST, QuestVarPlayer.QUEST_FAMILY_CREST, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	THE_FEUD(new TheFeud(), Quest.THE_FEUD, QuestVarbits.QUEST_THE_FEUD, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	FIGHT_ARENA(new FightArena(), Quest.FIGHT_ARENA, QuestVarPlayer.QUEST_FIGHT_ARENA, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	FISHING_CONTEST(new FishingContest(), Quest.FISHING_CONTEST, QuestVarPlayer.QUEST_FISHING_CONTEST, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	FORGETTABLE_TALE(new ForgettableTale(), Quest.FORGETTABLE_TALE, QuestVarbits.QUEST_FORGETTABLE_TALE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	BONE_VOYAGE(new BoneVoyage(), Quest.BONE_VOYAGE, QuestVarbits.QUEST_BONE_VOYAGE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	THE_FREMENNIK_ISLES(new TheFremennikIsles(), Quest.THE_FREMENNIK_ISLES, QuestVarbits.QUEST_THE_FREMENNIK_ISLES, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	THE_FREMENNIK_TRIALS(new TheFremennikTrials(), Quest.THE_FREMENNIK_TRIALS, QuestVarPlayer.QUEST_THE_FREMENNIK_TRIALS, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	GARDEN_OF_TRANQUILLITY(new GardenOfTranquillity(), Quest.GARDEN_OF_TRANQUILLITY, QuestVarbits.QUEST_GARDEN_OF_TRANQUILLITY, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	GERTRUDES_CAT(new GertrudesCat(), Quest.GERTRUDES_CAT, QuestVarPlayer.QUEST_GERTRUDES_CAT, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	GHOSTS_AHOY(new GhostsAhoy(), Quest.GHOSTS_AHOY, QuestVarbits.QUEST_GHOSTS_AHOY, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	THE_GIANT_DWARF(new TheGiantDwarf(), Quest.THE_GIANT_DWARF, QuestVarbits.QUEST_THE_GIANT_DWARF, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	THE_GOLEM(new TheGolem(), Quest.THE_GOLEM, QuestVarbits.QUEST_THE_GOLEM, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	THE_GRAND_TREE(new TheGrandTree(), Quest.THE_GRAND_TREE, QuestVarPlayer.QUEST_THE_GRAND_TREE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	THE_GREAT_BRAIN_ROBBERY(new TheGreatBrainRobbery(), Quest.THE_GREAT_BRAIN_ROBBERY, QuestVarPlayer.QUEST_THE_GREAT_BRAIN_ROBBERY, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	GRIM_TALES(new GrimTales(), Quest.GRIM_TALES, QuestVarbits.QUEST_GRIM_TALES, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	THE_HAND_IN_THE_SAND(new TheHandInTheSand(), Quest.THE_HAND_IN_THE_SAND, QuestVarbits.QUEST_THE_HAND_IN_THE_SAND, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	HAUNTED_MINE(new HauntedMine(), Quest.HAUNTED_MINE, QuestVarPlayer.QUEST_HAUNTED_MINE, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	HAZEEL_CULT(new HazeelCult(), Quest.HAZEEL_CULT, QuestVarPlayer.QUEST_HAZEEL_CULT, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	HEROES_QUEST(new HeroesQuest(), Quest.HEROES_QUEST, QuestVarPlayer.QUEST_HEROES_QUEST, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	HOLY_GRAIL(new HolyGrail(), Quest.HOLY_GRAIL, QuestVarPlayer.QUEST_HOLY_GRAIL, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	HORROR_FROM_THE_DEEP(new HorrorFromTheDeep(), Quest.HORROR_FROM_THE_DEEP, QuestVarbits.QUEST_HORROR_FROM_THE_DEEP, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	ICTHLARINS_LITTLE_HELPER(new IcthlarinsLittleHelper(), Quest.ICTHLARINS_LITTLE_HELPER, QuestVarbits.QUEST_ICTHLARINS_LITTLE_HELPER, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	IN_AID_OF_THE_MYREQUE(new InAidOfTheMyreque(), Quest.IN_AID_OF_THE_MYREQUE, QuestVarbits.QUEST_IN_AID_OF_THE_MYREQUE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	IN_SEARCH_OF_THE_MYREQUE(new InSearchOfTheMyreque(), Quest.IN_SEARCH_OF_THE_MYREQUE, QuestVarPlayer.QUEST_IN_SEARCH_OF_THE_MYREQUE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	JUNGLE_POTION(new JunglePotion(), Quest.JUNGLE_POTION, QuestVarPlayer.QUEST_JUNGLE_POTION, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	KINGS_RANSOM(new KingsRansom(), Quest.KINGS_RANSOM, QuestVarbits.QUEST_KINGS_RANSOM, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	LAND_OF_THE_GOBLINS(new LandOfTheGoblins(), Quest.LAND_OF_THE_GOBLINS, QuestVarbits.QUEST_LAND_OF_THE_GOBLINS, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	LEGENDS_QUEST(new LegendsQuest(), Quest.LEGENDS_QUEST, QuestVarPlayer.QUEST_LEGENDS_QUEST, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	LOST_CITY(new LostCity(), Quest.LOST_CITY, QuestVarPlayer.QUEST_LOST_CITY, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	THE_LOST_TRIBE(new TheLostTribe(), Quest.THE_LOST_TRIBE, QuestVarbits.QUEST_THE_LOST_TRIBE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	LUNAR_DIPLOMACY(new LunarDiplomacy(), Quest.LUNAR_DIPLOMACY, QuestVarbits.QUEST_LUNAR_DIPLOMACY, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	MAKING_FRIENDS_WITH_MY_ARM(new MakingFriendsWithMyArm(), Quest.MAKING_FRIENDS_WITH_MY_ARM, QuestVarbits.QUEST_MAKING_FRIENDS_WITH_MY_ARM, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	MAKING_HISTORY(new MakingHistory(), Quest.MAKING_HISTORY, QuestVarbits.QUEST_MAKING_HISTORY, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	MERLINS_CRYSTAL(new MerlinsCrystal(), Quest.MERLINS_CRYSTAL, QuestVarPlayer.QUEST_MERLINS_CRYSTAL, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	MONKEY_MADNESS_I(new MonkeyMadnessI(), Quest.MONKEY_MADNESS_I, QuestVarPlayer.QUEST_MONKEY_MADNESS_I, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	MONKEY_MADNESS_II(new MonkeyMadnessII(), Quest.MONKEY_MADNESS_II, QuestVarbits.QUEST_MONKEY_MADNESS_II, QuestDetails.Type.P2P, QuestDetails.Difficulty.GRANDMASTER),
	MONKS_FRIEND(new MonksFriend(), Quest.MONKS_FRIEND, QuestVarPlayer.QUEST_MONKS_FRIEND, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	MOUNTAIN_DAUGHTER(new MountainDaughter(), Quest.MOUNTAIN_DAUGHTER, QuestVarbits.QUEST_MOUNTAIN_DAUGHTER, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	MOURNINGS_END_PART_I(new MourningsEndPartI(), Quest.MOURNINGS_END_PART_I, QuestVarPlayer.QUEST_MOURNINGS_END_PART_I, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	MOURNINGS_END_PART_II(new MourningsEndPartII(), Quest.MOURNINGS_END_PART_II, QuestVarbits.QUEST_MOURNINGS_END_PART_II, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	MURDER_MYSTERY(new MurderMystery(), Quest.MURDER_MYSTERY, QuestVarPlayer.QUEST_MURDER_MYSTERY, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	MY_ARMS_BIG_ADVENTURE(new MyArmsBigAdventure(), Quest.MY_ARMS_BIG_ADVENTURE, QuestVarbits.QUEST_MY_ARMS_BIG_ADVENTURE, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	NATURE_SPIRIT(new NatureSpirit(), Quest.NATURE_SPIRIT, QuestVarPlayer.QUEST_NATURE_SPIRIT, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	OBSERVATORY_QUEST(new ObservatoryQuest(), Quest.OBSERVATORY_QUEST, QuestVarPlayer.QUEST_OBSERVATORY_QUEST, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	OLAFS_QUEST(new OlafsQuest(), Quest.OLAFS_QUEST, QuestVarbits.QUEST_OLAFS_QUEST, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	ONE_SMALL_FAVOUR(new OneSmallFavour(), Quest.ONE_SMALL_FAVOUR, QuestVarPlayer.QUEST_ONE_SMALL_FAVOUR, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	PLAGUE_CITY(new PlagueCity(), Quest.PLAGUE_CITY, QuestVarPlayer.QUEST_PLAGUE_CITY, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	PRIEST_IN_PERIL(new PriestInPeril(), Quest.PRIEST_IN_PERIL, QuestVarPlayer.QUEST_PRIEST_IN_PERIL, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	THE_QUEEN_OF_THIEVES(new TheQueenOfThieves(), Quest.THE_QUEEN_OF_THIEVES, QuestVarbits.QUEST_THE_QUEEN_OF_THIEVES, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	RAG_AND_BONE_MAN_I(new RagAndBoneManI(), Quest.RAG_AND_BONE_MAN_I, QuestVarPlayer.QUEST_RAG_AND_BONE_MAN_I, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	RAG_AND_BONE_MAN_II(new RagAndBoneManII(), Quest.RAG_AND_BONE_MAN_II, QuestVarPlayer.QUEST_RAG_AND_BONE_MAN_II, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	RATCATCHERS(new RatCatchers(), Quest.RATCATCHERS, QuestVarbits.QUEST_RATCATCHERS, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	RECIPE_FOR_DISASTER(new RFDStart(), Quest.RECIPE_FOR_DISASTER, QuestVarbits.QUEST_RECIPE_FOR_DISASTER, QuestDetails.Type.P2P, QuestDetails.Difficulty.GRANDMASTER),
	RECIPE_FOR_DISASTER_START(new RFDStart(), Quest.RECIPE_FOR_DISASTER.getId(), "RFD - Start", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	RECIPE_FOR_DISASTER_DWARF(new RFDDwarf(), Quest.RECIPE_FOR_DISASTER.getId(), "RFD - Dwarf", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_DWARF, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	RECIPE_FOR_DISASTER_WARTFACE_AND_BENTNOZE(new RFDGoblins(), Quest.RECIPE_FOR_DISASTER.getId(), "RFD - Wartface & Bentnoze", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_WARTFACE_AND_BENTNOZE, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	RECIPE_FOR_DISASTER_PIRATE_PETE(new RFDPiratePete(), Quest.RECIPE_FOR_DISASTER.getId(), "RFD - Pirate Pete", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_PIRATE_PETE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	RECIPE_FOR_DISASTER_LUMBRIDGE_GUIDE(new RFDLumbridgeGuide(), Quest.RECIPE_FOR_DISASTER.getId(), "RFD - Lumbridge Guide", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_LUMBRIDGE_GUIDE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	RECIPE_FOR_DISASTER_EVIL_DAVE(new RFDEvilDave(), Quest.RECIPE_FOR_DISASTER.getId(), "RFD - Evil Dave", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_EVIL_DAVE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	RECIPE_FOR_DISASTER_MONKEY_AMBASSADOR(new RFDAwowogei(), Quest.RECIPE_FOR_DISASTER.getId(), "RFD - Monkey Ambassador", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_MONKEY_AMBASSADOR, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	RECIPE_FOR_DISASTER_SIR_AMIK_VARZE(new RFDSirAmikVarze(), Quest.RECIPE_FOR_DISASTER.getId(), "RFD - Sir Amik Varze", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_SIR_AMIK_VARZE, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	RECIPE_FOR_DISASTER_SKRACH_UGLOGWEE(new RFDSkrachUglogwee(), Quest.RECIPE_FOR_DISASTER.getId(), "RFD - Skrach Uglogwee", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER_SKRACH_UGLOGWEE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	RECIPE_FOR_DISASTER_FINALE(new RFDFinal(), Quest.RECIPE_FOR_DISASTER.getId(), "RFD - Finale", Arrays.asList("recipe", "for", "disaster"), QuestVarbits.QUEST_RECIPE_FOR_DISASTER, QuestDetails.Type.P2P, QuestDetails.Difficulty.GRANDMASTER),
	RECRUITMENT_DRIVE(new RecruitmentDrive(), Quest.RECRUITMENT_DRIVE, QuestVarbits.QUEST_RECRUITMENT_DRIVE, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	REGICIDE(new Regicide(), Quest.REGICIDE, QuestVarPlayer.QUEST_REGICIDE, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	ROVING_ELVES(new RovingElves(), Quest.ROVING_ELVES, QuestVarPlayer.QUEST_ROVING_ELVES, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	ROYAL_TROUBLE(new RoyalTrouble(), Quest.ROYAL_TROUBLE, QuestVarbits.QUEST_ROYAL_TROUBLE, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	RUM_DEAL(new RumDeal(), Quest.RUM_DEAL, QuestVarPlayer.QUEST_RUM_DEAL, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	SCORPION_CATCHER(new ScorpionCatcher(), Quest.SCORPION_CATCHER, QuestVarPlayer.QUEST_SCORPION_CATCHER, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	SEA_SLUG(new SeaSlug(), Quest.SEA_SLUG, QuestVarPlayer.QUEST_SEA_SLUG, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	SHADES_OF_MORTTON(new ShadesOfMortton(), Quest.SHADES_OF_MORTTON, QuestVarPlayer.QUEST_SHADES_OF_MORTTON, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	SHADOW_OF_THE_STORM(new ShadowOfTheStorm(), Quest.SHADOW_OF_THE_STORM, QuestVarbits.QUEST_SHADOW_OF_THE_STORM, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	SHEEP_HERDER(new SheepHerder(), Quest.SHEEP_HERDER, QuestVarPlayer.QUEST_SHEEP_HERDER, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	SHILO_VILLAGE(new ShiloVillage(), Quest.SHILO_VILLAGE, QuestVarPlayer.QUEST_SHILO_VILLAGE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	SLEEPING_GIANTS(new SleepingGiants(), Quest.SLEEPING_GIANTS, QuestVarbits.QUEST_SLEEPING_GIANTS, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	THE_SLUG_MENACE(new TheSlugMenace(), Quest.THE_SLUG_MENACE, QuestVarbits.QUEST_THE_SLUG_MENACE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	A_SOULS_BANE(new ASoulsBane(), Quest.A_SOULS_BANE, QuestVarbits.QUEST_A_SOULS_BANE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	SPIRITS_OF_THE_ELID(new SpiritsOfTheElid(), Quest.SPIRITS_OF_THE_ELID, QuestVarbits.QUEST_SPIRITS_OF_THE_ELID, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	SWAN_SONG(new SwanSong(), Quest.SWAN_SONG, QuestVarbits.QUEST_SWAN_SONG, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	TAI_BWO_WANNAI_TRIO(new TaiBwoWannaiTrio(), Quest.TAI_BWO_WANNAI_TRIO, QuestVarPlayer.QUEST_TAI_BWO_WANNAI_TRIO, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	A_TAIL_OF_TWO_CATS(new ATailOfTwoCats(), Quest.A_TAIL_OF_TWO_CATS, QuestVarbits.QUEST_A_TAIL_OF_TWO_CATS, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	TALE_OF_THE_RIGHTEOUS(new TaleOfTheRighteous(), Quest.TALE_OF_THE_RIGHTEOUS, QuestVarbits.QUEST_TALE_OF_THE_RIGHTEOUS, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	A_TASTE_OF_HOPE(new ATasteOfHope(), Quest.A_TASTE_OF_HOPE, QuestVarbits.QUEST_A_TASTE_OF_HOPE, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	TEARS_OF_GUTHIX(new TearsOfGuthix(), Quest.TEARS_OF_GUTHIX, QuestVarbits.QUEST_TEARS_OF_GUTHIX, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	TEMPLE_OF_IKOV(new TempleOfIkov(), Quest.TEMPLE_OF_IKOV, QuestVarPlayer.QUEST_TEMPLE_OF_IKOV, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	TEMPLE_OF_THE_EYE(new TempleOfTheEye(), Quest.TEMPLE_OF_THE_EYE, QuestVarbits.QUEST_TEMPLE_OF_THE_EYE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	THRONE_OF_MISCELLANIA(new ThroneOfMiscellania(), Quest.THRONE_OF_MISCELLANIA, QuestVarPlayer.QUEST_THRONE_OF_MISCELLANIA, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	THE_TOURIST_TRAP(new TheTouristTrap(), Quest.THE_TOURIST_TRAP, QuestVarPlayer.QUEST_THE_TOURIST_TRAP, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	TOWER_OF_LIFE(new TowerOfLife(), Quest.TOWER_OF_LIFE, QuestVarbits.QUEST_TOWER_OF_LIFE, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	TREE_GNOME_VILLAGE(new TreeGnomeVillage(), Quest.TREE_GNOME_VILLAGE, QuestVarPlayer.QUEST_TREE_GNOME_VILLAGE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	TRIBAL_TOTEM(new TribalTotem(), Quest.TRIBAL_TOTEM, QuestVarPlayer.QUEST_TRIBAL_TOTEM, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	TROLL_ROMANCE(new TrollRomance(), Quest.TROLL_ROMANCE, QuestVarPlayer.QUEST_TROLL_ROMANCE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	TROLL_STRONGHOLD(new TrollStronghold(), Quest.TROLL_STRONGHOLD, QuestVarPlayer.QUEST_TROLL_STRONGHOLD, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	UNDERGROUND_PASS(new UndergroundPass(), Quest.UNDERGROUND_PASS, QuestVarPlayer.QUEST_UNDERGROUND_PASS, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	CLIENT_OF_KOUREND(new ClientOfKourend(), Quest.CLIENT_OF_KOUREND, QuestVarbits.QUEST_CLIENT_OF_KOUREND, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	WANTED(new Wanted(), Quest.WANTED, QuestVarbits.QUEST_WANTED, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	WATCHTOWER(new Watchtower(), Quest.WATCHTOWER, QuestVarPlayer.QUEST_WATCHTOWER, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	WATERFALL_QUEST(new WaterfallQuest(), Quest.WATERFALL_QUEST, QuestVarPlayer.QUEST_WATERFALL_QUEST, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	WHAT_LIES_BELOW(new WhatLiesBelow(), Quest.WHAT_LIES_BELOW, QuestVarbits.QUEST_WHAT_LIES_BELOW, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	WITCHS_HOUSE(new WitchsHouse(), Quest.WITCHS_HOUSE, QuestVarPlayer.QUEST_WITCHS_HOUSE, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	ZOGRE_FLESH_EATERS(new ZogreFleshEaters(), Quest.ZOGRE_FLESH_EATERS, QuestVarbits.QUEST_ZOGRE_FLESH_EATERS, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	THE_ASCENT_OF_ARCEUUS(new TheAscentOfArceuus(), Quest.THE_ASCENT_OF_ARCEUUS, QuestVarbits.QUEST_THE_ASCENT_OF_ARCEUUS, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	THE_FORSAKEN_TOWER(new TheForsakenTower(), Quest.THE_FORSAKEN_TOWER, QuestVarbits.QUEST_THE_FORSAKEN_TOWER, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	SONG_OF_THE_ELVES(new SongOfTheElves(), Quest.SONG_OF_THE_ELVES, QuestVarbits.QUEST_SONG_OF_THE_ELVES, QuestDetails.Type.P2P, QuestDetails.Difficulty.GRANDMASTER),
	THE_FREMENNIK_EXILES(new TheFremennikExiles(), Quest.THE_FREMENNIK_EXILES, QuestVarbits.QUEST_THE_FREMENNIK_EXILES, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	SINS_OF_THE_FATHER(new SinsOfTheFather(), Quest.SINS_OF_THE_FATHER, QuestVarbits.QUEST_SINS_OF_THE_FATHER, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	GETTING_AHEAD(new GettingAhead(), Quest.GETTING_AHEAD, QuestVarbits.QUEST_GETTING_AHEAD, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	A_PORCINE_OF_INTEREST(new APorcineOfInterest(), Quest.A_PORCINE_OF_INTEREST, QuestVarbits.QUEST_A_PORCINE_OF_INTEREST, QuestDetails.Type.P2P, QuestDetails.Difficulty.NOVICE),
	A_KINGDOM_DIVIDED(new AKingdomDivided(), Quest.A_KINGDOM_DIVIDED, QuestVarbits.QUEST_A_KINGDOM_DIVIDED, QuestDetails.Type.P2P, QuestDetails.Difficulty.EXPERIENCED),
	A_NIGHT_AT_THE_THEATRE(new ANightAtTheTheatre(), Quest.A_NIGHT_AT_THE_THEATRE, QuestVarbits.QUEST_A_NIGHT_AT_THE_THEATRE, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),
	THE_GARDEN_OF_DEATH(new TheGardenOfDeath(), Quest.THE_GARDEN_OF_DEATH, QuestVarbits.QUEST_THE_GARDEN_OF_DEATH, QuestDetails.Type.P2P, QuestDetails.Difficulty.INTERMEDIATE),
	SECRETS_OF_THE_NORTH(new SecretsOfTheNorth(), Quest.SECRETS_OF_THE_NORTH, QuestVarbits.QUEST_SECRETS_OF_THE_NORTH, QuestDetails.Type.P2P, QuestDetails.Difficulty.MASTER),

	//Miniquests
	ENTER_THE_ABYSS(new EnterTheAbyss(), Quest.ENTER_THE_ABYSS, QuestVarPlayer.QUEST_ENTER_THE_ABYSS, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	ARCHITECTURAL_ALLIANCE(new ArchitecturalAlliance(), Quest.ARCHITECTURAL_ALLIANCE, QuestVarbits.QUEST_ARCHITECTURAL_ALLIANCE, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	BEAR_YOUR_SOUL(new BearYourSoul(), Quest.BEAR_YOUR_SOUL, QuestVarbits.QUEST_BEAR_YOUR_SOUL, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	ALFRED_GRIMHANDS_BARCRAWL(new AlfredGrimhandsBarcrawl(), Quest.ALFRED_GRIMHANDS_BARCRAWL, QuestVarPlayer.QUEST_ALFRED_GRIMHANDS_BARCRAWL, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	CURSE_OF_THE_EMPTY_LORD(new CurseOfTheEmptyLord(), Quest.CURSE_OF_THE_EMPTY_LORD, QuestVarbits.QUEST_CURSE_OF_THE_EMPTY_LORD, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	ENCHANTED_KEY(new EnchantedKey(), Quest.THE_ENCHANTED_KEY, QuestVarbits.QUEST_ENCHANTED_KEY, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	THE_GENERALS_SHADOW(new TheGeneralsShadow(), Quest.THE_GENERALS_SHADOW, QuestVarbits.QUEST_THE_GENERALS_SHADOW, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	SKIPPY_AND_THE_MOGRES(new SkippyAndTheMogres(), Quest.SKIPPY_AND_THE_MOGRES, QuestVarbits.QUEST_SKIPPY_AND_THE_MOGRES, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	THE_MAGE_ARENA(new TheMageArenaI(), Quest.MAGE_ARENA_I, QuestVarPlayer.QUEST_THE_MAGE_ARENA, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	LAIR_OF_TARN_RAZORLOR(new LairOfTarnRazorlor(), Quest.LAIR_OF_TARN_RAZORLOR, QuestVarbits.QUEST_LAIR_OF_TARN_RAZORLOR, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	FAMILY_PEST(new FamilyPest(), Quest.FAMILY_PEST, QuestVarbits.QUEST_FAMILY_PEST, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	THE_MAGE_ARENA_II(new TheMageArenaII(), Quest.MAGE_ARENA_II, QuestVarbits.QUEST_THE_MAGE_ARENA_II, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	IN_SEARCH_OF_KNOWLEDGE(new InSearchOfKnowledge(), Quest.IN_SEARCH_OF_KNOWLEDGE, QuestVarbits.QUEST_IN_SEARCH_OF_KNOWLEDGE, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	DADDYS_HOME(new DaddysHome(), Quest.DADDYS_HOME, QuestVarbits.QUEST_DADDYS_HOME, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	HOPESPEARS_WILL(new HopespearsWill(), Quest.HOPESPEARS_WILL, QuestVarbits.QUEST_HOPESPEARS_WILL, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),
	HIS_FAITHFUL_SERVANTS(new HisFaithfulServants(), Quest.HIS_FAITHFUL_SERVANTS, QuestVarbits.HIS_FAITHFUL_SERVANTS, QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),

	// Fake miniquests
	KNIGHT_WAVES_TRAINING_GROUNDS(new KnightWaves(), "Knight Waves Training Grounds", QuestVarbits.KNIGHT_WAVES_TRAINING_GROUNDS, 8,
		QuestDetails.Type.MINIQUEST, QuestDetails.Difficulty.MINIQUEST),

	// Achievement diaries

	// Ardougne
	ARDOUGNE_EASY(new ArdougneEasy(), "Ardougne Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_ARDOUGNE_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	ARDOUGNE_MEDIUM(new ArdougneMedium(), "Ardougne Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_ARDOUGNE_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	ARDOUGNE_HARD(new ArdougneHard(), "Ardougne Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_ARDOUGNE_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	ARDOUGNE_ELITE(new ArdougneElite(), "Ardougne Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_ARDOUGNE_ELITE, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),

	// Desert
	DESERT_EASY(new DesertEasy(), "Desert Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_DESERT_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	DESERT_MEDIUM(new DesertMedium(), "Desert Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_DESERT_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	DESERT_HARD(new DesertHard(), "Desert Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_DESERT_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	DESERT_ELITE(new DesertElite(), "Desert Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_DESERT_ELITE, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),

	// Falador
	FALADOR_EASY(new FaladorEasy(), "Falador Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_FALADOR_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	FALADOR_MEDIUM(new FaladorMedium(), "Falador Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_FALADOR_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	FALADOR_HARD(new FaladorHard(), "Falador Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_FALADOR_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	FALADOR_ELITE(new FaladorElite(), "Falador Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_FALADOR_ELITE, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),

	// Fremennik
	FREMENNIK_EASY(new FremennikEasy(), "Fremennik Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_FREMENNIK_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	FREMENNIK_MEDIUM(new FremennikMedium(), "Fremennik Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_FREMENNIK_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	FREMENNIK_HARD(new FremennikHard(), "Fremennik Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_FREMENNIK_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	FREMENNIK_ELITE(new FremennikElite(), "Fremennik Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_FREMENNIK_ELITE, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),

	// Kandarin
	KANDARIN_EASY(new KandarinEasy(), "Kandarin Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_KANDARIN_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	KANDARIN_MEDIUM(new KandarinMedium(), "Kandarin Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_KANDARIN_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	KANDARIN_HARD(new KandarinHard(), "Kandarin Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_KANDARIN_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	KANDARIN_ELITE(new KandarinElite(), "Kandarin Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_KANDARIN_ELITE, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),

	// Karamja
	KARAMJA_EASY(new KaramjaEasy(), "Karamja Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_KARAMJA_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	KARAMJA_MEDIUM(new KaramjaMedium(), "Karamja Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_KARAMJA_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	KARAMJA_HARD(new KaramjaHard(), "Karamja Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_KARAMJA_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	KARAMJA_ELITE(new KaramjaElite(), "Karamja Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_KARAMJA_ELITE, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),

	// Kourend & Kebos
	KOUREND_EASY(new KourendEasy(), "Kourend & Kebos Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_KOUREND_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	KOUREND_MEDIUM(new KourendMedium(), "Kourend & Kebos Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_KOUREND_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	KOUREND_HARD(new KourendHard(), "Kourend & Kebos Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_KOUREND_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	KOUREND_ELITE(new KourendElite(), "Kourend & Kebos Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_KOUREND_ELITE, 1,
		QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),

	// Lumbridge & Draynor
	LUMBRIDGE_EASY(new LumbridgeEasy(), "Lumbridge & Draynor Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_LUMBRIDGE_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	LUMBRIDGE_MEDIUM(new LumbridgeMedium(), "Lumbridge & Draynor Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_LUMBRIDGE_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	LUMBRIDGE_HARD(new LumbridgeHard(), "Lumbridge & Draynor Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_LUMBRIDGE_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	LUMBRIDGE_ELITE(new LumbridgeElite(), "Lumbridge & Draynor Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_LUMBRIDGE_ELITE, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),

	// Morytania
	MORYTANIA_EASY(new MorytaniaEasy(), "Morytania Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_MORYTANIA_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	MORYTANIA_MEDIUM(new MorytaniaMedium(), "Morytania Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_MORYTANIA_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	MORYTANIA_HARD(new MorytaniaHard(), "Morytania Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_MORYTANIA_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	MORYTANIA_ELITE(new MorytaniaElite(), "Morytania Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_MORYTANIA_ELITE, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),

	// Varrock
	VARROCK_EASY(new VarrockEasy(), "Varrock Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_VARROCK_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	VARROCK_MEDIUM(new VarrockMedium(), "Varrock Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_VARROCK_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	VARROCK_HARD(new VarrockHard(), "Varrock Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_VARROCK_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	VARROCK_ELITE(new VarrockElite(), "Varrock Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_VARROCK_ELITE, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),

	// Western Provinces
	WESTERN_EASY(new WesternEasy(), "Western Provinces Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_WESTERN_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	WESTERN_MEDIUM(new WesternMedium(), "Western Provinces Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_WESTERN_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	WESTERN_HARD(new WesternHard(), "Western Provinces Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_WESTERN_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	WESTERN_ELITE(new WesternElite(), "Western Provinces Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_WESTERN_ELITE, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),

	// Wilderness
	WILDERNESS_EASY(new WildernessEasy(), "Wilderness Easy Diary", QuestVarbits.ACHIEVEMENT_DIARY_WILDERNESS_EASY, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	WILDERNESS_MEDIUM(new WildernessMedium(), "Wilderness Medium Diary", QuestVarbits.ACHIEVEMENT_DIARY_WILDERNESS_MEDIUM, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	WILDERNESS_HARD(new WildernessHard(), "Wilderness Hard Diary", QuestVarbits.ACHIEVEMENT_DIARY_WILDERNESS_HARD, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),
	WILDERNESS_ELITE(new WildernessElite(), "Wilderness Elite Diary", QuestVarbits.ACHIEVEMENT_DIARY_WILDERNESS_ELITE, 1, QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Difficulty.ACHIEVEMENT_DIARY),


	// Generic
	CHECK_ITEMS(new AllNeededItems(), "Check all items", QuestVarbits.CUTSCENE, -1, QuestDetails.Type.GENERIC,
		QuestDetails.Difficulty.GENERIC),
	MA2_LOCATOR(new MA2Locator(), "Mage Arena II Locator", QuestVarbits.QUEST_THE_MAGE_ARENA_II, -1, QuestDetails.Type.GENERIC,
		QuestDetails.Difficulty.GENERIC),
	DAG_ROUTE(new DagRouteHelper(), "Dagannoth Kings Route", QuestVarbits.QUEST_THE_FREMENNIK_ISLES, -1, QuestDetails.Type.GENERIC,
		QuestDetails.Difficulty.GENERIC),
	HERB_RUN(new HerbRun(), "Herb run", QuestVarbits.CUTSCENE, -1, QuestDetails.Type.GENERIC, QuestDetails.Difficulty.GENERIC),
	BARROWS_HELPER(new BarrowsHelper(), "Barrows helper", QuestVarbits.CUTSCENE, -1, QuestDetails.Type.GENERIC, QuestDetails.Difficulty.GENERIC),
	// Skill
	AGILITY(new Agility(), "Agility", Skill.AGILITY, 99, QuestDetails.Type.SKILL_P2P, QuestDetails.Difficulty.SKILL),
	WOODCUTTING_MEMBER(new WoodcuttingMember(), "Woodcutting - Member", Skill.WOODCUTTING, 99, QuestDetails.Type.SKILL_P2P, QuestDetails.Difficulty.SKILL),

	WOODCUTTING(new Woodcutting(), "Woodcutting", Skill.WOODCUTTING, 99, QuestDetails.Type.SKILL_F2P, QuestDetails.Difficulty.SKILL),

	// Player Quests
	COOKS_HELPER(new CooksHelper(), "Cook's Helper", PlayerQuests.COOKS_HELPER, 4);

	@Getter
	private final int id;

	@Getter
	private final String name;

	@Getter
	private final List<String> keywords;

	@Getter
	private final QuestDetails.Type questType;

	@Getter
	private final QuestDetails.Difficulty difficulty;

	private final QuestVarbits varbit;

	private final QuestVarPlayer varPlayer;

	private Skill skill;

	@Getter
	private PlayerQuests playerQuests;

	private final int completeValue;

	@Getter
	private final QuestHelper questHelper;

	QuestHelperQuest(QuestHelper questHelper, int id, String name, QuestVarbits varbit, QuestDetails.Type questType, QuestDetails.Difficulty difficulty)
	{
		this.questHelper = questHelper;
		this.id = id;
		this.name = name;
		this.keywords = titleToKeywords(name);
		this.varbit = varbit;
		this.varPlayer = null;
		this.questType = questType;
		this.difficulty = difficulty;
		this.completeValue = -1;
	}

	QuestHelperQuest(QuestHelper questHelper, Quest quest, QuestVarbits varbit, QuestDetails.Type questType, QuestDetails.Difficulty difficulty)
	{
		this.questHelper = questHelper;
		this.id = quest.getId();
		this.name = quest.getName();
		this.keywords = titleToKeywords(name);
		this.varbit = varbit;
		this.varPlayer = null;
		this.questType = questType;
		this.difficulty = difficulty;
		this.completeValue = -1;
	}

	QuestHelperQuest(QuestHelper questHelper, Quest quest, QuestVarPlayer varPlayer, QuestDetails.Type questType, QuestDetails.Difficulty difficulty)
	{
		this.questHelper = questHelper;
		this.id = quest.getId();
		this.name = quest.getName();
		this.keywords = titleToKeywords(name);
		this.varbit = null;
		this.varPlayer = varPlayer;
		this.questType = questType;
		this.difficulty = difficulty;
		this.completeValue = -1;
	}

	QuestHelperQuest(QuestHelper questHelper, int id, String name, QuestVarPlayer varPlayer, QuestDetails.Type questType, QuestDetails.Difficulty difficulty)
	{
		this.questHelper = questHelper;
		this.id = id;
		this.name = name;
		this.keywords = titleToKeywords(name);
		this.varbit = null;
		this.varPlayer = varPlayer;
		this.questType = questType;
		this.difficulty = difficulty;
		this.completeValue = -1;
	}

	QuestHelperQuest(QuestHelper questHelper, int id, String name, List<String> keywords, QuestVarbits varbit, QuestDetails.Type questType, QuestDetails.Difficulty difficulty)
	{
		this.questHelper = questHelper;
		this.id = id;
		this.name = name;
		this.keywords = Stream.concat(titleToKeywords(name).stream(), keywords.stream()).collect(Collectors.toList());
		this.varbit = varbit;
		this.varPlayer = null;
		this.questType = questType;
		this.difficulty = difficulty;
		this.completeValue = -1;
	}

	QuestHelperQuest(QuestHelper questHelper, String name, QuestVarbits varbit, int completeValue, QuestDetails.Type questType, QuestDetails.Difficulty difficulty)
	{
		this.questHelper = questHelper;
		this.id = -1;
		this.name = name;
		this.keywords = titleToKeywords(name);
		this.varbit = varbit;
		this.varPlayer = null;
		this.questType = questType;
		this.difficulty = difficulty;
		this.completeValue = completeValue;
	}

	// Used where no Quest exists yet
	QuestHelperQuest(QuestHelper questHelper, String name, QuestDetails.Type questType, QuestDetails.Difficulty difficulty)
	{
		this.questHelper = questHelper;
		this.id = -1;
		this.name = name;
		this.keywords = titleToKeywords(name);
		this.varbit = null;
		this.varPlayer = null;
		this.questType = questType;
		this.difficulty = difficulty;
		this.completeValue = -1;
	}

	// Can be used for skill based helpers
	QuestHelperQuest(QuestHelper questHelper, String name, Skill skill, int completeValue, QuestDetails.Type questType, QuestDetails.Difficulty difficulty)
	{
		this.questHelper = questHelper;
		this.id = -1;
		this.name = name;
		this.keywords = titleToKeywords(name);
		this.varbit = null;
		this.varPlayer = null;
		this.skill = skill;
		this.questType = questType;
		this.difficulty = difficulty;
		this.completeValue = completeValue;
	}

	// User for Player Quests
	QuestHelperQuest(QuestHelper questHelper, String name, PlayerQuests playerQuests, int completeValue)
	{
		this.questHelper = questHelper;
		this.id = -1;
		this.name = name;
		this.keywords = titleToKeywords(name);
		this.varbit = null;
		this.varPlayer = null;
		this.skill = null;
		this.playerQuests = playerQuests;
		this.questType = QuestDetails.Type.PLAYER_QUEST;
		this.difficulty = QuestDetails.Difficulty.PLAYER_QUEST;
		this.completeValue = completeValue;
	}

	private List<String> titleToKeywords(String title)
	{
		return Arrays.asList(title.toLowerCase().split(" "));
	}

	public QuestState getState(Client client, ConfigManager configManager)
	{
		if (playerQuests != null)
		{
			String currentStateString = configManager.getRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, playerQuests.getConfigValue());
			try
			{
				int currentState = Integer.parseInt(currentStateString);
				if (currentState == 0) return QuestState.NOT_STARTED;
				if (currentState >= completeValue) return QuestState.FINISHED;
				return QuestState.IN_PROGRESS;

			}
			catch (NumberFormatException err)
			{
				configManager.setRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, playerQuests.getConfigValue(), "0");
				return QuestState.NOT_STARTED;
			}
		}
		return getState(client);
	}
	private QuestState getQuestState(Client client)
	{
		client.runScript(ScriptID.QUEST_STATUS_GET, id);
		switch (client.getIntStack()[0])
		{
			case 2:
				return QuestState.FINISHED;
			case 1:
				return QuestState.NOT_STARTED;
			default:
				return QuestState.IN_PROGRESS;
		}
	}

	public QuestState getState(Client client)
	{
		if (id != -1)
		{
			return getQuestState(client);
		}

		if (skill != null)
		{
			if (client.getRealSkillLevel(skill) >= completeValue)
			{
				return QuestState.FINISHED;
			}
			return QuestState.IN_PROGRESS;
		}

		if (getVar(client) == -1)
		{
			return QuestState.IN_PROGRESS;
		}

		if (completeValue != -1)
		{
			int currentState = getVar(client);
			if (currentState == completeValue)
			{
				return QuestState.FINISHED;
			}
			if (currentState == 0)
			{
				return QuestState.NOT_STARTED;
			}
			return QuestState.IN_PROGRESS;
		}

		return QuestState.NOT_STARTED;
	}

	public int getVar(Client client)
	{
		if (varbit != null)
		{
			return client.getVarbitValue(varbit.getId());
		}
		else if (varPlayer != null)
		{
			return client.getVarpValue(varPlayer.getId());
		}
		else
		{
			return -1;
		}
	}

	public static QuestHelper getByName(String name)
	{
		for (QuestHelperQuest qhq : QuestHelperQuest.values())
		{
			if (qhq.name.equals(name)) return qhq.getQuestHelper();
		}
		return null;
	}

	public static List<QuestHelper> getQuestHelpers()
	{
		List<QuestHelper> helpers = new ArrayList<>();
		for (QuestHelperQuest questHelperQuest : QuestHelperQuest.values())
		{
			helpers.add(questHelperQuest.questHelper);
		}
		return helpers;
	}
}
