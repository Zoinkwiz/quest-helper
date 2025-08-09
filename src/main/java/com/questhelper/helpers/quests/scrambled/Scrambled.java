/*
 * Copyright (c) 2025, pajlada <https://github.com/pajlada>
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
package com.questhelper.helpers.quests.scrambled;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.NpcHintArrowRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetPresenceRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;
import static com.questhelper.requirements.util.LogicHelper.or;

/**
 * The quest guide for the "Scrambled" OSRS quest
 */
public class Scrambled extends BasicQuestHelper
{
	NpcStep startQuest;
	NpcStep inspectEggAfterItFell;
	NpcStep talkToKing;
	ConditionalStep gatherTheMen;
	NpcStep talkToGatheredMen;
	ConditionalStep collectAllEggs;
	NpcStep judgeEggs;
	NpcStep panicWithKingsMen;
	ConditionalStep cPutEggBackTogether;
	QuestStep finishQuest;

	ItemRequirement combatGear;
	ItemRequirement antifireShield;
	ItemRequirement bowlOfWater;
	ItemRequirement twoPlanks;
	ItemRequirement sixNails;
	ItemRequirement saw;
	ItemRequirement hammer;
	private ItemStep getEmptyBowl;
	private VarbitRequirement acatzinAgreedToHelp;
	private VarbitRequirement acatzinAgreedToFixWhetstone;
	private VarbitRequirement acatzinhasFixedWhetstone;
	private VarbitRequirement stillNeedToHelpAcatzin;
	private ItemRequirement emptyBowl;
	private ItemRequirement acatzinsDamagedAxe;
	private ItemRequirement acatzinsRepairedAxe;
	private Zone dragonCave;
	private VarbitRequirement canStillTakeNails;
	private VarbitRequirement kauayotlNeedToRepairCart;
	private VarbitRequirement kauayotlhasRepairedCart;
	private VarbitRequirement stillNeedToHelpKauayotl;
	private ItemRequirement damianaLeaves;
	private VarbitRequirement nezketiAgreedToHelp;
	private ItemRequirement damianaWater;
	private ItemRequirement damianaTea;
	private ItemRequirement emptyCup;
	private ItemRequirement cupOfDamianaTea;
	private VarbitRequirement needToClickLargeEggToSpawnChicken;
	private VarbitRequirement needToSpawnRedDragon;
	private VarbitRequirement hasKilledRedDragon;
	private VarbitRequirement hasTurnedInDragonEgg;
	private VarbitRequirement needToSpawnJaguar;
	private VarbitRequirement hasTurnedInJaguarEgg;
	private VarbitRequirement hasTurnedInChickenEgg;
	private ItemRequirement largeEgg;
	private ItemRequirement jaguarEgg;
	private ItemRequirement dragonEgg;
	private SkillRequirement has40Agi;
	private ZoneRequirement nearCaveEntrance;
	private ZoneRequirement inDragonCave;
	private Zone nearCaveEntranceZone;
	private WidgetPresenceRequirement isPuzzleOpen;
	private NpcStep acatzinTalkToBlacksmith;
	private ObjectStep acatzinFixWhetstone;
	private ObjectStep acatzinRepairAxe;
	private ItemStep acatzinGetSaw;
	private NpcStep acatzinReturnRepairedAxe;
	private NpcStep talkToKauayotl;
	private NpcStep talkToKauayotlAgain;
	private ObjectStep kauayotlRepairCart;
	private ItemStep getPlanks;
	private ObjectStep getDamianaLeaves;
	private NpcStep giveTeaToNezketi;
	private NpcStep talkToNezketi;
	private ObjectStep fillEmptyBowlWithWater;
	private DetailedQuestStep mixWaterAndDamianaLeaves;
	private ObjectStep boilDamianaWater;
	private DetailedQuestStep pourTeaIntoCup;
	private ConditionalStep cReturnToTempleWithEggs;
	private ConditionalStep cCollectLargeEgg;
	private ConditionalStep cCollectDragonEgg;
	private ConditionalStep cCollectJaguarEgg;
	private NpcStep talkToAcatzin;
	private VarbitRequirement stillNeedToHelpNezketi;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, startQuest);
		steps.put(2, startQuest);
		steps.put(4, startQuest);
		steps.put(6, inspectEggAfterItFell);
		steps.put(8, talkToKing);
		steps.put(10, talkToKing);
		steps.put(12, talkToKing);
		steps.put(14, gatherTheMen);
		steps.put(16, talkToGatheredMen);
		steps.put(18, collectAllEggs);
		steps.put(19, collectAllEggs);

		// has turned in all eggs
		steps.put(20, judgeEggs);

		steps.put(22, panicWithKingsMen);
		steps.put(24, cPutEggBackTogether);
		steps.put(26, finishQuest);
		steps.put(28, finishQuest);

		return steps;
	}

	@Override
	protected void setupZones()
	{
		dragonCave = new Zone(5012);

		nearCaveEntranceZone = new Zone(new WorldPoint(1277, 3134, 0), new WorldPoint(1299, 3141, 0));
	}

	@Override
	protected void setupRequirements()
	{
		acatzinAgreedToHelp = new VarbitRequirement(VarbitID.SCRAMBLED_KINGS_MAN_3, 2);
		acatzinAgreedToFixWhetstone = new VarbitRequirement(VarbitID.SCRAMBLED_KINGS_MAN_3, 3);
		acatzinhasFixedWhetstone = new VarbitRequirement(VarbitID.SCRAMBLED_KINGS_MAN_3, 4);
		stillNeedToHelpAcatzin = new VarbitRequirement(VarbitID.SCRAMBLED_KINGS_MAN_3, 7, Operation.LESS);
		
		canStillTakeNails = new VarbitRequirement(VarbitID.SCRAMBLED_NAILS_GIVEN, 0);
		kauayotlNeedToRepairCart = new VarbitRequirement(VarbitID.SCRAMBLED_KINGS_MAN_2, 2);
		kauayotlhasRepairedCart = new VarbitRequirement(VarbitID.SCRAMBLED_KINGS_MAN_2, 3);
		stillNeedToHelpKauayotl = new VarbitRequirement(VarbitID.SCRAMBLED_KINGS_MAN_2, 7, Operation.LESS);
		nezketiAgreedToHelp = new VarbitRequirement(VarbitID.SCRAMBLED_KINGS_MAN_1, 2);
		// probably no longer needed, we use it as the "conditional fallback step"
		stillNeedToHelpNezketi = new VarbitRequirement(VarbitID.SCRAMBLED_KINGS_MAN_1, 7, Operation.LESS);
		needToClickLargeEggToSpawnChicken = new VarbitRequirement(VarbitID.SCRAMBLED_REPLACEMENT_EGG_3, 1, Operation.GREATER_EQUAL);
		
		needToSpawnRedDragon = new VarbitRequirement(VarbitID.SCRAMBLED_REPLACEMENT_EGG_2, 1, Operation.GREATER_EQUAL);
		hasKilledRedDragon = new VarbitRequirement(VarbitID.SCRAMBLED_REPLACEMENT_EGG_2, 3);
		hasTurnedInDragonEgg = new VarbitRequirement(VarbitID.SCRAMBLED_REPLACEMENT_EGG_2, 4);

		needToSpawnJaguar = new VarbitRequirement(VarbitID.SCRAMBLED_REPLACEMENT_EGG_1, 1, Operation.GREATER_EQUAL);
		hasTurnedInJaguarEgg = new VarbitRequirement(VarbitID.SCRAMBLED_REPLACEMENT_EGG_1, 4);

		hasTurnedInChickenEgg = new VarbitRequirement(VarbitID.SCRAMBLED_REPLACEMENT_EGG_3, 4);

		has40Agi = new SkillRequirement(Skill.AGILITY, 40);
		nearCaveEntrance = new ZoneRequirement(nearCaveEntranceZone);
		inDragonCave = new ZoneRequirement(dragonCave);
		isPuzzleOpen = new WidgetPresenceRequirement(InterfaceID.Jigsaw.BACKGROUND);

		bowlOfWater = new ItemRequirement("Bowl of water", ItemID.BOWL_WATER).canBeObtainedDuringQuest().showConditioned(stillNeedToHelpNezketi);
		// NOTE: Confirmed it has to be two basic planks
		twoPlanks = new ItemRequirement("Plank", ItemID.WOODPLANK, 2).canBeObtainedDuringQuest().showConditioned(stillNeedToHelpKauayotl);
		// NOTE: Confirmed other nails work
		sixNails = new ItemRequirement("Nail", ItemCollections.NAILS, 6).canBeObtainedDuringQuest().showConditioned(stillNeedToHelpKauayotl);
		saw = new ItemRequirement("Saw", ItemCollections.SAW).canBeObtainedDuringQuest().showConditioned(stillNeedToHelpKauayotl);
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).canBeObtainedDuringQuest().showConditioned(or(stillNeedToHelpAcatzin, stillNeedToHelpKauayotl));

		// If the user does not have a bowl of water with them, then the empty bowl requirement will be used in the middle
		emptyBowl = new ItemRequirement("Empty bowl", ItemID.BOWL_EMPTY);

		acatzinsDamagedAxe = new ItemRequirement("Acatzin's damaged axe", ItemID.SCRAMBLED_AXE_DAMAGED);
		acatzinsRepairedAxe = new ItemRequirement("Acatzin's axe", ItemID.SCRAMBLED_AXE_REPAIRED);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		antifireShield = new ItemRequirement("Antifire shield", ItemCollections.ANTIFIRE_SHIELDS);

		damianaLeaves = new ItemRequirement("Damiana leaves", ItemID.DAMIANA_LEAVES);

		damianaWater = new ItemRequirement("Damiana water", ItemID.BOWL_DAMIANA_WATER);
		damianaTea = new ItemRequirement("Damiana tea", ItemID.BOWL_DAMIANA_TEA);

		emptyCup = new ItemRequirement("Empty cup", ItemID.CUP_EMPTY);
		cupOfDamianaTea = new ItemRequirement("Cup of damiana tea", ItemID.CUP_DAMIANA_TEA);


		largeEgg = new ItemRequirement("Large egg", ItemID.SCRAMBLED_CHICKEN_EGG).hideConditioned(hasTurnedInChickenEgg);
		jaguarEgg = new ItemRequirement("Jaguar egg", ItemID.SCRAMBLED_JAGUAR_EGG).hideConditioned(hasTurnedInJaguarEgg);
		dragonEgg = new ItemRequirement("Dragon egg", ItemID.SCRAMBLED_DRAGON_EGG).hideConditioned(hasTurnedInDragonEgg);
	}

	public void setupSteps()
	{
		startQuest = new NpcStep(this, NpcID.SCRAMBLED_ALAN, new WorldPoint(1247, 3167, 0), "Talk to Alan in Tal Teok Temple, north of Tal Teklan, to start the quest.");
		startQuest.addDialogStep("Yes.");
		startQuest.addDialogStep("Of course!");

		inspectEggAfterItFell = new NpcStep(this, NpcID.SCRAMBLED_EGG_DEAD, new WorldPoint(1247, 3170, 0), "Inspect the egg in front of you.");

		talkToKing = new NpcStep(this, NpcID.SCRAMBLED_KING, new WorldPoint(1224, 3119, 0), "Head south into Tal Teklan and talk to King in the pub.");
		talkToKing.addDialogSteps("Are you the king?");
		talkToKing.addDialogStep(Pattern.compile(".* fell off a wall!"));

		getEmptyBowl = new ItemStep(this, new WorldPoint(1229, 3119, 0), "Get an empty bowl from the pub, will need it later. If it's not there, hop worlds or" +
				" wait for it to spawn again.", emptyBowl);

		talkToAcatzin = new NpcStep(this, NpcID.SCRAMBLED_KINGS_MAN_3, new WorldPoint(1228, 3117, 0), "Talk to Acatzin inside the Tal Teklan pub.");
		talkToAcatzin.addDialogStep("I can talk to the blacksmith.");

		acatzinTalkToBlacksmith = new NpcStep(this, NpcID.SCRAMBLED_BLACKSMITH, new WorldPoint(1209, 3109, 0), "Talk to the Blacksmith west of the Tal Teklan pub.");

		var acatzinGetHammer = new ItemStep(this, new WorldPoint(1207, 3108, 0), "Get the hammer from the table. If it's not there, hop worlds or wait for it" +
				" to spawn again.",	hammer);

		var acatzinGetNails = new ObjectStep(this, ObjectID.SCRAMBLED_WORKBENCH, new WorldPoint(1210, 3112, 0), "Get some nails from the blacksmith's workbench, we'll need them later.");
		acatzinGetNails.addDialogStep("Take the nails.");

		acatzinGetSaw = new ItemStep(this, new WorldPoint(1212, 3093, 0), "Get the Saw from the house to the south, we'll need it later. If it's not there, " +
				"hop worlds or wait for it to spawn again.", saw);

		acatzinFixWhetstone = new ObjectStep(this, ObjectID.SCRAMBLED_WHETSTONE_BROKEN_OP, new WorldPoint(1211, 3108, 0), "Fix the whetstone.");
		acatzinFixWhetstone.addDialogStep("Yes.");

		acatzinFixWhetstone.addSubSteps(acatzinGetHammer);
		acatzinFixWhetstone.addSubSteps(acatzinGetNails);

		var cAcatzinFixWhetstone = new ConditionalStep(this, acatzinFixWhetstone);
		cAcatzinFixWhetstone.addStep(and(not(sixNails), canStillTakeNails), acatzinGetNails);
		cAcatzinFixWhetstone.addStep(not(hammer), acatzinGetHammer);

		var acatzinTalkToBlacksmithAgain = new NpcStep(this, NpcID.SCRAMBLED_BLACKSMITH, new WorldPoint(1209, 3109, 0), "Talk to the Blacksmith again after fixing the whetstone to receive a damaged axe.");

		acatzinRepairAxe = new ObjectStep(this, ObjectID.SCRAMBLED_WHETSTONE_FIXED_OP, new WorldPoint(1211, 3108, 0), "Repair the axe on the whetstone.", acatzinsDamagedAxe);
		acatzinRepairAxe.addDialogStep("Yes.");
		acatzinRepairAxe.addSubSteps(acatzinTalkToBlacksmithAgain);

		var cAcatzinRepairAxe = new ConditionalStep(this, acatzinRepairAxe);
		cAcatzinRepairAxe.addStep(not(acatzinsDamagedAxe),  acatzinTalkToBlacksmithAgain);

		acatzinReturnRepairedAxe = new NpcStep(this, NpcID.SCRAMBLED_KINGS_MAN_3, new WorldPoint(1228, 3117, 0), "Return the repaired axe to Acatzin in the Tel Teklan pub.", acatzinsRepairedAxe);


		var helpAcatzin = new ConditionalStep(this, talkToAcatzin);
		helpAcatzin.addStep(and(not(emptyBowl), not(bowlOfWater)), getEmptyBowl);
		helpAcatzin.addStep(acatzinAgreedToHelp, acatzinTalkToBlacksmith);
		helpAcatzin.addStep(acatzinAgreedToFixWhetstone, cAcatzinFixWhetstone);
		helpAcatzin.addStep(and(acatzinsRepairedAxe, not(saw)), acatzinGetSaw);
		helpAcatzin.addStep(acatzinsRepairedAxe, acatzinReturnRepairedAxe);
		helpAcatzin.addStep(acatzinhasFixedWhetstone, cAcatzinRepairAxe);


		talkToKauayotl = new NpcStep(this, NpcID.SCRAMBLED_KINGS_MAN_2, new WorldPoint(1251, 3104, 0), "Talk to Kauayotl at the east entrance of Tal Teklan."
				, hammer, saw, sixNails, twoPlanks);
		talkToKauayotl.addDialogStep("I see. Well, maybe I can help out with that?");

		talkToKauayotlAgain = new NpcStep(this, NpcID.SCRAMBLED_KINGS_MAN_2, new WorldPoint(1251, 3104, 0), "Talk to Kauayotl again after repairing his cart.");

		kauayotlRepairCart = new ObjectStep(this, ObjectID.SCRAMBLED_CART_BROKEN_OP, new WorldPoint(1250, 3107, 0), "Repair Kauayotl's cart.", hammer, saw, sixNails, twoPlanks);
		kauayotlRepairCart.addDialogStep("Yes.");

		getPlanks = new ItemStep(this, new WorldPoint(1238, 3076, 0), "Go south-west of Kauayotl and pick up two planks from besides the lake. If it's not " +
				"there, hop worlds or wait for it to spawn again.", twoPlanks);

		getDamianaLeaves = new ObjectStep(this, ObjectID.DAMIANA_SHRUB, new WorldPoint(1250, 3110, 0), "Pick the damiana bush at the east entrance of Tal " +
				"Teklan for some damiana leaves, we'll need these later.");

		var helpKauayotl = new ConditionalStep(this, talkToKauayotl);
		helpKauayotl.addStep(kauayotlhasRepairedCart, talkToKauayotlAgain);
		helpKauayotl.addStep(not(hammer), acatzinGetHammer);
		helpKauayotl.addStep(not(saw), acatzinGetSaw);
		helpKauayotl.addStep(not(damianaLeaves), getDamianaLeaves);
		helpKauayotl.addStep(not(twoPlanks), getPlanks);
		// helpKauayotl.addStep(not(onePlank), getPlanks);
		helpKauayotl.addStep(kauayotlNeedToRepairCart, kauayotlRepairCart);


		talkToNezketi = new NpcStep(this, NpcID.SCRAMBLED_KINGS_MAN_1, new WorldPoint(1224, 3105, 0), "Talk to Nezketi in the Tal Teklan temple.");
		talkToNezketi.addDialogStep("I can get you some tea.");

		fillEmptyBowlWithWater = new ObjectStep(this, ObjectID.FORTIS_WATER_PUMP, new WorldPoint(1242, 3097, 0), "Fill your empty bowl with water at the water pump near the eastern entrance of Tal Teklan.", emptyBowl.highlighted());
		fillEmptyBowlWithWater.addIcon(ItemID.BOWL_EMPTY);

		mixWaterAndDamianaLeaves = new ItemStep(this, "Mix damiana leaves with your bowl of water.", damianaLeaves.highlighted(), bowlOfWater.highlighted());

		boilDamianaWater = new ObjectStep(this, ObjectID.STOVE_CLAY01_TALKASTI01_NOOP, "Use your bowl of damiana water on the stove in the east part of Tal Teklan to boil it.", damianaWater.highlighted());
		boilDamianaWater.addIcon(ItemID.BOWL_DAMIANA_WATER);

		pourTeaIntoCup = new DetailedQuestStep(this, "Pour the damiana tea from your bowl into the empty cup",  damianaTea.highlighted(), emptyCup.highlighted());

		var cMakeTea = new ConditionalStep(this, getDamianaLeaves);
		cMakeTea.addStep(and(damianaTea, emptyCup), pourTeaIntoCup);
		cMakeTea.addStep(damianaWater, boilDamianaWater);
		cMakeTea.addStep(not(emptyCup), talkToNezketi);
		cMakeTea.addStep(and(damianaLeaves, bowlOfWater), mixWaterAndDamianaLeaves);
		cMakeTea.addStep(not(bowlOfWater), fillEmptyBowlWithWater);
		cMakeTea.addStep(not(emptyBowl), getEmptyBowl);

		giveTeaToNezketi = new NpcStep(this, NpcID.SCRAMBLED_KINGS_MAN_1, new WorldPoint(1224, 3105, 0), "Return to Nezketi in the Tal Teklan temple and give him the cup of damiana tea.", cupOfDamianaTea);

		var helpNezketi = new ConditionalStep(this, talkToNezketi);
		helpNezketi.addStep(cupOfDamianaTea, giveTeaToNezketi);
		helpNezketi.addStep(nezketiAgreedToHelp, cMakeTea);

		gatherTheMen = new ConditionalStep(this, helpNezketi);
		gatherTheMen.addStep(stillNeedToHelpAcatzin, helpAcatzin);
		gatherTheMen.addStep(stillNeedToHelpKauayotl, helpKauayotl);

		var anyOfTheKingsMen = new int[]{
			NpcID.SCRAMBLED_KINGS_MAN_1,
			NpcID.SCRAMBLED_KINGS_MAN_2,
			NpcID.SCRAMBLED_KINGS_MAN_3,
		};

		talkToGatheredMen = new NpcStep(this, anyOfTheKingsMen, new WorldPoint(1247, 3166, 0), "Return to the temple north of Tal Teklan and speak with one of King's men.", true);

		var collectLargeEgg = new ObjectStep(this, ObjectID.SCRAMBLED_CHICKEN_EGGS_OP, new WorldPoint(1238, 3137, 0), "Search the Eggs to get a Large egg.");
		var collectLargeEggSpawnChicken = new ObjectStep(this, ObjectID.SCRAMBLED_CHICKEN_EGGS_OP, new WorldPoint(1238, 3137, 0), "");

		var fightLargeChicken = new NpcStep(this, NpcID.SCRAMBLED_CHICKEN, new WorldPoint(1239, 3137, 0), "Kill the large chicken.");
		var largeChickenNearby = new NpcHintArrowRequirement(NpcID.SCRAMBLED_CHICKEN);

		cCollectLargeEgg = new ConditionalStep(this, collectLargeEgg, "Fetch the Large egg from the chicken coop south of the large temple. Be ready to fight" +
				" a level 17 chicken.");
		cCollectLargeEgg.addStep(largeChickenNearby, fightLargeChicken);
		cCollectLargeEgg.addStep(needToClickLargeEggToSpawnChicken, collectLargeEggSpawnChicken);

		var useDragonShortcut = new ObjectStep(this, ObjectID.TLATI_NORTH_RIVER_LOG_BALANCE_1, new WorldPoint(1283, 3146, 0), "Step across the log balance towards the dragon cave.", has40Agi);

		var enterDragonCave = new ObjectStep(this, ObjectID.TLATI_DRAGON_NEST_CAVE_ENTRY, new WorldPoint(1288, 3133, 0), "", List.of(), List.of(antifireShield));

		// does protect from melee avoid all damage?

		var spawnDragonFromEgg = new ObjectStep(this, ObjectID.SCRAMBLED_DRAGON_EGGS_OP, new WorldPoint(1259, 9482, 0), "Search the Eggs in the dragon cave.");

		var fightRedDragon = new NpcStep(this, NpcID.SCRAMBLED_DRAGON, new WorldPoint(1257, 9482, 0), "Fight the red dragon. You can safespot it by standing " +
				"south-east of the Eggs.");
		fightRedDragon.addSafeSpots(new WorldPoint(1260, 9481, 0));
		var redDragonNearby = new NpcHintArrowRequirement(NpcID.SCRAMBLED_DRAGON);
		var collectDragonEgg = new ObjectStep(this, ObjectID.SCRAMBLED_DRAGON_EGGS_OP, new WorldPoint(1259, 9482, 0), "Search the Eggs in the " +
				"south-east of the dragon cave to collect your Dragon egg.", List.of(), List.of(antifireShield));

		cCollectDragonEgg = new ConditionalStep(this, enterDragonCave, "Fetch the Dragon egg from the dragon cave south-east of the large temple, across " +
				"the river. Be ready to fight a Red dragon.");
		cCollectDragonEgg.addStep(and(inDragonCave, hasKilledRedDragon), collectDragonEgg);
		cCollectDragonEgg.addStep(and(inDragonCave, redDragonNearby), fightRedDragon);
		cCollectDragonEgg.addStep(and(inDragonCave, needToSpawnRedDragon), spawnDragonFromEgg);
		cCollectDragonEgg.addStep(and(has40Agi, not(nearCaveEntrance)), useDragonShortcut);

		var exitDragonCave = new ObjectStep(this, ObjectID.TLATI_DRAGON_NEST_CAVE_EXIT, new WorldPoint(1244, 9528, 0), "Exit the dragon's cave.");

		var spawnJaguarFromEgg = new ObjectStep(this, ObjectID.SCRAMBLED_JAGUAR_EGGS_OP, new WorldPoint(1332, 3122, 0), "");

		var fightJaguar = new NpcStep(this, NpcID.SCRAMBLED_JAGUAR, new WorldPoint(1329, 3122, 0), "Fight the Jaguar. You can safespot it by standing just " +
				"north east of the camp.");
		fightJaguar.addSafeSpots(new WorldPoint(1337, 3128, 0));
		var jaguarNearby = new NpcHintArrowRequirement(NpcID.SCRAMBLED_JAGUAR);

		var collectJaguarEgg = new ObjectStep(this, ObjectID.SCRAMBLED_JAGUAR_EGGS_OP, new WorldPoint(1332, 3122, 0), "Search the Eggs at the camp, east of the dragon age, and collect your Jaguar egg.");

		cCollectJaguarEgg = new ConditionalStep(this, collectJaguarEgg, "Fetch the Jaguar egg from the tent, east of the dragon cave. Be ready to fight a " +
				"level 88 jaguar.");
		cCollectJaguarEgg.addStep(inDragonCave, exitDragonCave);
		cCollectJaguarEgg.addStep(jaguarNearby, fightJaguar);
		cCollectJaguarEgg.addStep(needToSpawnJaguar, spawnJaguarFromEgg);

		var returnToTempleWithEggs = new NpcStep(this, anyOfTheKingsMen, new WorldPoint(1247, 3166, 0), "Return to the temple north of Tal Teklan with the egg replacements and speak with one of \"King\"'s men.", true, largeEgg, dragonEgg, jaguarEgg);

		var returnToTempleWithDragonEgg = new NpcStep(this, NpcID.SCRAMBLED_KINGS_MAN_2, new WorldPoint(1247, 3166, 0), "Give Kauayotl the Dragon egg.",  largeEgg, dragonEgg, jaguarEgg);
		var returnToTempleWithJaguarEgg = new NpcStep(this, NpcID.SCRAMBLED_KINGS_MAN_1, new WorldPoint(1247, 3166, 0), "Give Nezketi the Jaguar egg.",  largeEgg, dragonEgg, jaguarEgg);
		var returnToTempleWithChickenEgg = new NpcStep(this, NpcID.SCRAMBLED_KINGS_MAN_3, new WorldPoint(1247, 3166, 0), "Give Acatzin the Large egg.",  largeEgg, dragonEgg, jaguarEgg);

		cReturnToTempleWithEggs = new ConditionalStep(this, returnToTempleWithEggs, "Give the eggs to King's men at the Tal Teok Temple, north of Tal Teklan.");
		cReturnToTempleWithEggs.addStep(inDragonCave, exitDragonCave);
		cReturnToTempleWithEggs.addStep(not(hasTurnedInDragonEgg), returnToTempleWithDragonEgg);
		cReturnToTempleWithEggs.addStep(not(hasTurnedInJaguarEgg), returnToTempleWithJaguarEgg);
		cReturnToTempleWithEggs.addStep(not(hasTurnedInChickenEgg), returnToTempleWithChickenEgg);

		collectAllEggs = new ConditionalStep(this, cReturnToTempleWithEggs);
		collectAllEggs.addStep(and(not(largeEgg), not(hasTurnedInChickenEgg)), cCollectLargeEgg);
		collectAllEggs.addStep(and(not(dragonEgg), not(hasTurnedInDragonEgg)), cCollectDragonEgg);
		collectAllEggs.addStep(and(not(jaguarEgg), not(hasTurnedInJaguarEgg)), cCollectJaguarEgg);

		judgeEggs = new NpcStep(this, anyOfTheKingsMen, new WorldPoint(1247, 3166, 0), "Return to the large temple north of Tal Teklan and talk with one of King's men to evaluate the Humphrey Dumphrey alternatives.", true);

		// all eggs went from 4 -> 5 when they fell and broke
		// and quest state went from 20 -> 22

		panicWithKingsMen = new NpcStep(this, anyOfTheKingsMen, new WorldPoint(1247, 3166, 0), "Talk to one of King's men to figure out how to solve the broken-egg conundrum.", true);

		var putEggBackTogether = new NpcStep(this, NpcID.SCRAMBLED_EGG_FIX, new WorldPoint(1244,3168, 0), "Inspect the egg.");

		var puzzleSolver = new PuzzleWrapperStep(this, new EggSolver(this));

		cPutEggBackTogether = new ConditionalStep(this, putEggBackTogether, "Put Lumpty Mumpty back together again.");
		cPutEggBackTogether.addStep(isPuzzleOpen, puzzleSolver);

		finishQuest = new NpcStep(this, anyOfTheKingsMen, new WorldPoint(1247, 3166, 0), "Talk to one of King's men to finish the quest.", true);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			bowlOfWater,
			twoPlanks,
			sixNails,
			saw,
			hammer,
			combatGear
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			antifireShield
		);
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		return List.of(
			combatGear
		);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.CHILDREN_OF_THE_SUN, QuestState.FINISHED),
			new SkillRequirement(Skill.CONSTRUCTION, 38), // TODO: boostable?
			new SkillRequirement(Skill.COOKING, 36), // TODO: boostable?
			new SkillRequirement(Skill.SMITHING, 35) // TODO: boostable?
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Large chicken (lvl 16)",
			"Black jaguar (lvl 88)",
			"Red dragon (lvl 106, can be safespotted)"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.CONSTRUCTION, 5000),
			new ExperienceReward(Skill.COOKING, 5000),
			new ExperienceReward(Skill.SMITHING, 5000)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Your very own egg")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var panels = new ArrayList<PanelDetails>();

		panels.add(new PanelDetails("Humphrey Dumphrey sat on a wall", List.of(
			startQuest
		), List.of(
			// Requirements
		), List.of(
			// Recommended
		)));

		panels.add(new PanelDetails("Wumty Scrumpty had a great fall", List.of(
			inspectEggAfterItFell,
			talkToKing
		), List.of(
			// Requirements
		), List.of(
			// Recommended
		)));

		panels.add(new PanelDetails("All the king's horses and all the king's men...", List.of(
			getEmptyBowl,
			
			// Help Acatzin
			talkToAcatzin,
			acatzinTalkToBlacksmith,
			acatzinFixWhetstone,
			acatzinRepairAxe,
			acatzinGetSaw,
			acatzinReturnRepairedAxe,

			// Help Kauayotl
			getDamianaLeaves,
			getPlanks,
			talkToKauayotl,
			kauayotlRepairCart,
			talkToKauayotlAgain,

			// Help Nezketi
				talkToNezketi,
			fillEmptyBowlWithWater,
			mixWaterAndDamianaLeaves,
			boilDamianaWater,
			pourTeaIntoCup,
			giveTeaToNezketi,

			talkToGatheredMen
		), List.of(
			bowlOfWater,
			twoPlanks,
			sixNails,
			saw,
			hammer
			// Requirements
		), List.of(
			// Recommended
		)));

		panels.add(new PanelDetails("Couldn't put Bumpty Numpty back together again...", List.of(
			cCollectLargeEgg,
			cCollectDragonEgg,
			cCollectJaguarEgg,

			cReturnToTempleWithEggs,
			judgeEggs,
			panicWithKingsMen,
			cPutEggBackTogether,
			finishQuest
		), List.of(
			combatGear
		), List.of(
			antifireShield
		)));

		return panels;
	}
}
