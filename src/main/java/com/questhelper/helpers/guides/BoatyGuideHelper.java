/*
 * Copyright (c) 2025, Zoinkwiz <https://www.github.com/Zoinkwiz>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.helpers.guides;

import com.questhelper.config.ConfigKeys;
import com.questhelper.helpers.achievementdiaries.lumbridgeanddraynor.LumbridgeEasy;
import com.questhelper.helpers.quests.runemysteries.RuneMysteries;
import com.questhelper.helpers.quests.therestlessghost.TheRestlessGhost;
import com.questhelper.helpers.quests.xmarksthespot.XMarksTheSpot;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.MesBoxRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.runelite.RuneliteRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.widget.WidgetHighlight;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarPlayerID;
import org.apache.commons.lang3.tuple.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.nand;
import static com.questhelper.requirements.util.LogicHelper.nor;
import static com.questhelper.requirements.util.LogicHelper.not;
import static com.questhelper.requirements.util.LogicHelper.or;

public class BoatyGuideHelper extends ComplexStateQuestHelper
{
	/** Required items **/

	// Bank 0
	ItemRequirement bronzeDagger;
	ItemRequirement bronzeSword;
	ItemRequirement woodenShield;
	ItemRequirement shortbow;
	ItemRequirement chisel;
	ItemRequirement spade;
	ItemRequirement hammer;
	ItemRequirement logs4;

	// Bank 1
	ItemRequirement coins;
	ItemRequirement airRunes;
	ItemRequirement mindRunes;
	ItemRequirement bread;
	ItemRequirement tinderbox;
	ItemRequirement airTalisman;

	ItemRequirement bowl;
	ItemRequirement knife;
	ItemRequirement jug;
	ItemRequirement jugOfWater;
	ItemRequirement bowlOfWater;
	ItemRequirement ratMeat;
	ItemRequirement bones;

	// Zones


	/** Miscellaneous requirements **/

	// Bank 0
	Conditions notClaimedRunes;
	Conditions notSoldDaggerSwordShieldShortbow;
	Conditions notOwnSpadeHammerChisel;

	/** Steps **/

	// Bank 0
	NpcStep getRunesFromMagicTutor;
	NpcStep sellItemsToGeneralStore;
	NpcStep buySpadeChiselHammer;
	ConditionalStep goCollect4Logs;
	ConditionalStep goDepositEverythingInLumbridgeBank;
	ConditionalStep goSetBankPin;

	// Bank 1
	ConditionalStep goGetBank1Items;
	ConditionalStep goPickUpJug;
	ItemStep pickUpBowl;
	ItemStep pickUpKnife;
	ObjectStep fillJug;
	ObjectStep fillBowl;
	NpcStep killGiantRat;

	/** Loaded Helpers **/
	TheRestlessGhost theRestlessGhost = (TheRestlessGhost) QuestHelperQuest.THE_RESTLESS_GHOST.getQuestHelper();
	LumbridgeEasy lumbridgeEasy = (LumbridgeEasy) QuestHelperQuest.LUMBRIDGE_EASY.getQuestHelper();
	XMarksTheSpot xMarksTheSpot = (XMarksTheSpot) QuestHelperQuest.X_MARKS_THE_SPOT.getQuestHelper();
	RuneMysteries runeMysteries = (RuneMysteries) QuestHelperQuest.RUNE_MYSTERIES.getQuestHelper();


	@Override
	protected void setupZones()
	{

	}

	@Override
	protected void setupRequirements()
	{
		notClaimedRunes = not(new RuneliteRequirement(
			getConfigManager(), ConfigKeys.CLAIMED_RUNES.getKey(),
			new MesBoxRequirement("Mikasi gives you 30 mind runes and 30 air runes."),
			"Claimed runes from Mikasi"
		));

		bronzeDagger = new ItemRequirement("Bronze dagger", ItemID.BRONZE_DAGGER);
		bronzeSword = new ItemRequirement("Bronze sword", ItemID.BRONZE_SWORD);
		woodenShield = new ItemRequirement("Wooden shield", ItemID.WOODEN_SHIELD);
		shortbow = new ItemRequirement("Shortbow", ItemID.SHORTBOW);

		notSoldDaggerSwordShieldShortbow = or(bronzeDagger, bronzeSword, woodenShield, shortbow);

		chisel = new ItemRequirement("Chisel", ItemID.CHISEL);
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER);
		notOwnSpadeHammerChisel = nand(chisel.alsoCheckBank(), spade.alsoCheckBank(), hammer.alsoCheckBank());

		logs4 = new ItemRequirement("Logs", ItemID.LOGS, 4);

		// Bank 1
		coins = new ItemRequirement("Coins", ItemID.COINS);
		airRunes = new ItemRequirement("Air runes", ItemID.AIRRUNE);
		mindRunes = new ItemRequirement("Mind runes", ItemID.MINDRUNE);
		bread = new ItemRequirement("Bread", ItemID.BREAD);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		airTalisman = new ItemRequirement("Air talisman", ItemID.AIR_TALISMAN);

		jug = new ItemRequirement("Jug", ItemID.JUG_EMPTY);
		knife = new ItemRequirement("Knife", ItemID.KNIFE);
		bowl = new ItemRequirement("Bowl", ItemID.BOWL_EMPTY);

		jugOfWater = new ItemRequirement("Jug of water", ItemID.JUG_WATER);
		bowlOfWater = new ItemRequirement("Bowl of water", ItemID.BOWL_WATER);
		ratMeat = new ItemRequirement("Rat meat", ItemID.RAW_RAT_MEAT);
		bones = new ItemRequirement("Bones", ItemID.BONES);

	}

	public void setupSteps()
	{
		getRunesFromMagicTutor = new NpcStep(this, NpcID.AIDE_TUTOR_MAGIC, new WorldPoint(3217, 3239, 0), "Claim air and mind runes from the Magic combat tutor. " +
			"You will need to drop any air and mind runes you have in your bank or inventory.");
		sellItemsToGeneralStore = new NpcStep(this, NpcID.GENERALSHOPKEEPER1, new WorldPoint(3211, 3247, 0), "Sell your bronze dagger, bronze sword, wooden shield, and shortbow.", true);
		sellItemsToGeneralStore.addAlternateNpcs(NpcID.GENERALASSISTANT1);
		sellItemsToGeneralStore.addWidgetHighlight(WidgetHighlight.createShopInventoryItemHighlight(ItemID.BRONZE_DAGGER));
		sellItemsToGeneralStore.addWidgetHighlight(WidgetHighlight.createShopInventoryItemHighlight(ItemID.BRONZE_DAGGER));
		sellItemsToGeneralStore.addWidgetHighlight(WidgetHighlight.createShopInventoryItemHighlight(ItemID.BRONZE_SWORD));
		sellItemsToGeneralStore.addWidgetHighlight(WidgetHighlight.createShopInventoryItemHighlight(ItemID.WOODEN_SHIELD));
		sellItemsToGeneralStore.addWidgetHighlight(WidgetHighlight.createShopInventoryItemHighlight(ItemID.SHORTBOW));

		buySpadeChiselHammer = new NpcStep(this, NpcID.GENERALSHOPKEEPER1, new WorldPoint(3211, 3247, 0), "Buy a spade, a chisel, and a hammer.", true);
		buySpadeChiselHammer.addAlternateNpcs(NpcID.GENERALASSISTANT1);
		buySpadeChiselHammer.addWidgetHighlight(WidgetHighlight.createShopItemHighlight(ItemID.SPADE).setRequirementToShow(not(spade)));
		buySpadeChiselHammer.addWidgetHighlight(WidgetHighlight.createShopItemHighlight(ItemID.CHISEL).setRequirementToShow(not(chisel)));
		buySpadeChiselHammer.addWidgetHighlight(WidgetHighlight.createShopItemHighlight(ItemID.HAMMER).setRequirementToShow(not(hammer)));

		var goF0ToF2LumbridgeCastle = new ObjectStep(this, ObjectID.SPIRALSTAIRSBOTTOM_3, new WorldPoint(3204, 3229, 0), "Go to the top floor of Lumbridge Castle. " +
			"Right-click the stairs for 'Top-floor' to skip the middle floor.");
		var goF1ToF2LumbridgeCastle = new ObjectStep(this, ObjectID.SPIRALSTAIRSMIDDLE, new WorldPoint(3204, 3229, 1), "Climb-up to the top of Lumbridge Castle.");
		var pickUpLogs = new ItemStep(this, new WorldPoint(3205, 3224, 2), "", logs4);
		var lumbridgeCastleF2 = new ZoneRequirement(new Zone(new WorldPoint(3204, 3206, 2), new WorldPoint(3214, 3232, 2)));

		goCollect4Logs = new ConditionalStep(this, goF0ToF2LumbridgeCastle, "Pick up 4 logs from the top floor of Lumbridge Castle.");
		goCollect4Logs.addStep(lumbridgeCastleF2, pickUpLogs);
		goCollect4Logs.addStep(runeMysteries.getInUpstairsLumbridge(), goF1ToF2LumbridgeCastle);

		var depositItems = new ObjectStep(this, ObjectID.AIDE_BANKBOOTH, new WorldPoint(3208, 3221, 2), "");
		depositItems.addWidgetHighlight(new WidgetHighlight(InterfaceID.Bankmain.DEPOSITINV));

		goDepositEverythingInLumbridgeBank = new ConditionalStep(this, goF0ToF2LumbridgeCastle, "Bank everything in the bank on top of Lumbridge Castle.");
		goDepositEverythingInLumbridgeBank.addStep(lumbridgeCastleF2, depositItems);
		goDepositEverythingInLumbridgeBank.addStep(runeMysteries.getInUpstairsLumbridge(), goF1ToF2LumbridgeCastle);

		var setBankPin = new NpcStep(this, NpcID.AIDE_TUTOR_BANKER, new WorldPoint(3208, 3222, 2), "");
		setBankPin.addDialogStep("I'd like to check my PIN settings.");
		setBankPin.addWidgetHighlight(new WidgetHighlight(InterfaceID.BankpinSettings.SET));
		goSetBankPin = new ConditionalStep(this, goF0ToF2LumbridgeCastle, "TALK to the Banker tutor to set a bank PIN. " +
			"Make sure to make it something memorable as you will need to use it to access your bank after this point.");
		goSetBankPin.addStep(lumbridgeCastleF2, setBankPin);
		goSetBankPin.addStep(runeMysteries.getInUpstairsLumbridge(), goF1ToF2LumbridgeCastle);

		// Bank 1
		var takeOutItemsBank1 = new ObjectStep(this, ObjectID.AIDE_BANKBOOTH, new WorldPoint(3208, 3221, 2), "");
		goGetBank1Items = new ConditionalStep(this, goF0ToF2LumbridgeCastle, "Take out some coins, all your mind runes and air runes, bread, a spade, tinderbox, and air talisman.");
		goGetBank1Items.addText("There will be a quest icon in the top-right of your bank. You can use this to easily filter your bank to needed items for each section.");
		goGetBank1Items.addStep(lumbridgeCastleF2, takeOutItemsBank1);
		goGetBank1Items.addStep(runeMysteries.getInUpstairsLumbridge(), goF1ToF2LumbridgeCastle);

		var goF2ToF0LumbridgeCastle = new ObjectStep(this, ObjectID.SPIRALSTAIRSTOP_3, new WorldPoint(3205, 3208, 2), "Go to the ground floor of Lumbridge Castle. " +
			"Right-click the stairs for 'Bottom-floor' to skip the middle floor.");
		var goF1ToF0LumbridgeCastle = new ObjectStep(this, ObjectID.SPIRALSTAIRSMIDDLE, new WorldPoint(3204, 3207, 1), "Climb-down to the bottom of Lumbridge Castle.");

		var pickUpJug = new ItemStep(this, new WorldPoint(3211, 3212, 0), "", jug);
		goPickUpJug = new ConditionalStep(this, pickUpJug, "Pick up the jug in the kitchen on the ground floor of Lumbridge Castle.");
		goPickUpJug.addStep(lumbridgeCastleF2, goF2ToF0LumbridgeCastle);
		goPickUpJug.addStep(runeMysteries.getInUpstairsLumbridge(), goF1ToF0LumbridgeCastle);

		pickUpBowl = new ItemStep(this, new WorldPoint(3208, 3214, 0), "Pick up the bowl in the kitchen on the ground floor of Lumbridge Castle.", bowl);

		pickUpKnife = new ItemStep(this, new WorldPoint(3205, 3212, 0), "Pick up the knife in the kitchen on the ground floor of Lumbridge Castle.", knife);
		fillJug = new ObjectStep(this, ObjectID.QIP_COOK_SINK, new WorldPoint(3205, 3215, 0), "Fill the jug at the sink in the kitchen.", jug.highlighted());
		fillJug.addIcon(ItemID.JUG_EMPTY);
		fillBowl = new ObjectStep(this, ObjectID.QIP_COOK_SINK, new WorldPoint(3205, 3215, 0), "Fill the bowl at the sink in the kitchen.", bowl.highlighted());
		fillBowl.addIcon(ItemID.BOWL_EMPTY);
		killGiantRat = new NpcStep(this, NpcID.GIANTRAT_GREY, new WorldPoint(3193, 3208, 0), "Kill one of the giant rats west of the castle for their meat and bones.", true);
		killGiantRat.addAlternateNpcs(NpcID.GIANTRAT_GREY2, NpcID.GIANTRAT_GREY3);
	}

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupSteps();
		lumbridgeEasy.init();
		xMarksTheSpot.init();
		theRestlessGhost.init();
		runeMysteries.init();

		var fullHelper = new ConditionalStep(this, new DetailedQuestStep(this, "You've completed everything, or something has gone wrong!"));
//		fullHelper.addStep(getBank0Steps());
		fullHelper.addStep(getBank1Steps());

		return fullHelper;
	}

	private Pair<Requirement, QuestStep> getBank0Steps()
	{
		var notCompletedFirstStepRestlessGhost = not(new QuestRequirement(theRestlessGhost, 1));
		var notPickpocketedManOrWoman = new VarplayerRequirement(VarPlayerID.LUMB_DRAY_ACHIEVEMENT_DIARY, false, 6);
		var notCompletedFirstStepXMarksTheSpot = nor(new QuestRequirement(xMarksTheSpot, 3));
		var notCheckPlaytime = new VarplayerRequirement(VarPlayerID.LUMB_DRAY_ACHIEVEMENT_DIARY, false, 5);
		var notStartedRuneMysteries = not(new QuestRequirement(runeMysteries, 1));

		var bank0Steps = new ConditionalStep(this, goSetBankPin);
		bank0Steps.addStep(notCompletedFirstStepRestlessGhost, theRestlessGhost.loadStepsAsConditionalStep());
		bank0Steps.addStep(notPickpocketedManOrWoman, lumbridgeEasy.getPickpocket());
		// TODO: Determine way to make this not annoying if you have runes or want to skip step somehow
//		bank0Steps.addStep(notClaimedRunes, getRunesFromMagicTutor);
		bank0Steps.addStep(notSoldDaggerSwordShieldShortbow, sellItemsToGeneralStore);
		bank0Steps.addStep(notOwnSpadeHammerChisel, buySpadeChiselHammer);
		bank0Steps.addStep(notCompletedFirstStepXMarksTheSpot, xMarksTheSpot.loadStepsAsConditionalStep());
		bank0Steps.addStep(notCheckPlaytime, lumbridgeEasy.getHans());
		bank0Steps.addStep(notStartedRuneMysteries, runeMysteries.loadStepsAsConditionalStep());
		bank0Steps.addStep(not(logs4.alsoCheckBank()), goCollect4Logs);

		var haveItems = not(new FreeInventorySlotRequirement(28));
		var pinStateMap = new HashMap<String, Requirement>();
		pinStateMap.put("true", or(new WidgetTextRequirement(InterfaceID.BankpinSettings.STATUSOUTPUT, "PIN coming soon"), new WidgetTextRequirement(InterfaceID.BankpinSettings.STATUSOUTPUT, "You have a PIN")));
		pinStateMap.put("false", new WidgetTextRequirement(InterfaceID.BankpinSettings.STATUSOUTPUT, "No PIN set"));

		var notSetBankPin = not(new RuneliteRequirement(
			getConfigManager(), ConfigKeys.SET_BANK_PIN.getKey(), "true",
			pinStateMap
		));
		bank0Steps.addStep(haveItems, goDepositEverythingInLumbridgeBank);
		bank0Steps.addStep(notSetBankPin, goSetBankPin);

		var requirement = or(notCompletedFirstStepRestlessGhost, notPickpocketedManOrWoman, notCompletedFirstStepXMarksTheSpot, notCheckPlaytime, notStartedRuneMysteries, notSetBankPin);

		return Pair.of(requirement, bank0Steps);
	}

	private Pair<Requirement, QuestStep> getBank1Steps()
	{
		var notCompletedStep3XMarksTheSpot = not(new QuestRequirement(xMarksTheSpot, 4));
		var givenAirTalismanAway = new QuestRequirement(runeMysteries, 2);

		var bank1Steps = new ConditionalStep(this, killGiantRat);
		bank1Steps.addStep(nor(airTalisman.withAdditionalOptions(givenAirTalismanAway)), goGetBank1Items);
		bank1Steps.addStep(nor(jug, jugOfWater), goPickUpJug);
		bank1Steps.addStep(nor(bowl, bowlOfWater), pickUpBowl);
		bank1Steps.addStep(not(knife), pickUpKnife);
		bank1Steps.addStep(not(jugOfWater), fillJug);
		bank1Steps.addStep(not(bowlOfWater), fillBowl);
		bank1Steps.addStep(notCompletedStep3XMarksTheSpot, xMarksTheSpot.getDigCastle());

		var requirement = or(not(ratMeat));
		return Pair.of(requirement, bank1Steps);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Song of the Elves completed.")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var steps = new ArrayList<PanelDetails>();

		steps.add(new PanelDetails("Starting off", List.of(
			theRestlessGhost.getTalkToAereck(),
			lumbridgeEasy.getPickpocket(),
			getRunesFromMagicTutor,
			sellItemsToGeneralStore,
			buySpadeChiselHammer,
			xMarksTheSpot.getStartQuest(),
			xMarksTheSpot.getDigOutsideBob(),
			lumbridgeEasy.getHans(),
			runeMysteries.getTalkToHoracio(),
			goCollect4Logs,
			goDepositEverythingInLumbridgeBank,
			goSetBankPin
		), List.of(
		)));

		steps.add(new PanelDetails("Bank 1", List.of(
			goGetBank1Items,
			goPickUpJug,
			pickUpBowl,
			pickUpKnife,
			fillJug,
			fillBowl,
			xMarksTheSpot.getDigCastle(),
			killGiantRat
		), List.of(
			coins,
			airRunes.quantity(30),
			mindRunes.quantity(30),
			bread,
			spade,
			tinderbox,
			airTalisman
		)));

		return steps;
	}
}
