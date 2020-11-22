/*
 * Copyright (c) 2020, INSERT_YOUR_NAME_HERE
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
package com.questhelper.quests.thegiantdwarf;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ChatMessageCondition;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.WidgetTextCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;

@SuppressWarnings("CheckStyle")
@QuestDescriptor(
	quest = QuestHelperQuest.THE_GIANT_DWARF
)
public class TheGiantDwarf extends BasicQuestHelper
{
	ItemRequirement coins2500, logs, tinderbox, coal, ironBar, lawRune, airRune, sapphires3, oresBars, redberryPie,
		houseTeleport, rellekkaTeleport, fairyRings, staminaPotions, varrockTeleport, clay10, copperOre10, tinOre10, ironOre10, coal10, silverOre10, goldOre10, mithrilOre10, bronzeBar10, ironbar10, silverBar10, goldBar10, steelBar10, mithrilBar10,
		weightBelow30, inventorySpace,
		bookOnCostumes, exquisiteClothes;
	Zone keldagrim,	keldagrim2, trollRoom, dwarfEntrance;
	ConditionForStep inTrollRoom, inKeldagrim, inDwarfEntrance,
		talkedToVermundi, talkedToLibrarian, hasBookOnCostumes, talkedToVermundiWithBook, usedCoalOnMachine, startedMachine, hasExquisiteClothes;
	QuestStep enterDwarfCave, enterDwarfCave2, talkToBoatman, talkToVeldaban, talkToBlasidar,
		talkToVermundi, talkToLibrarian, climbBookcase, talkToVermundiWithBook, useCoalOnMachine, startMachine, talkToVermundiWithMachine;

	public void setupItemRequirements()
	{
		// Required
		coins2500 = new ItemRequirement("coins", ItemID.COINS_995, 2500);
		coins2500.setTip("Bring more to be safe.");
		logs = new ItemRequirement("Logs", ItemID.LOGS);
		logs.setTip("Most logs will work, however, arctic pine logs do not work.");
		logs.addAlternates(ItemID.OAK_LOGS, ItemID.WILLOW_LOGS, ItemID.TEAK_LOGS, ItemID.MAPLE_LOGS, ItemID.MAHOGANY_LOGS, ItemID.YEW_LOGS, ItemID.MAGIC_LOGS, ItemID.REDWOOD_LOGS);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		coal = new ItemRequirement("Coal", ItemID.COAL);
		coal.setTip("There are rocks in the city, but you need 30 Mining.");
		ironBar = new ItemRequirement("Iron bar", ItemID.IRON_BAR);
		ironBar.setTip("Purchasable during the quest.");
		lawRune = new ItemRequirement("Law rune", ItemID.LAW_RUNE);
		lawRune.setTip("For Telekinetic Grab.");
		airRune = new ItemRequirement("Air rune", ItemID.AIR_RUNE);
		airRune.setTip("For Telekinetic Grab.");
		sapphires3 = new ItemRequirement("Cut sapphires", ItemID.SAPPHIRE, 3);
		sapphires3.setTip("Purchasable during the quest.");
		oresBars = new ItemRequirement("Various ores and bars", -1, -1);
		oresBars.setTip("Obtainable during the quest.");
		redberryPie = new ItemRequirement("Redberry pie", ItemID.REDBERRY_PIE);
		redberryPie.setTip("Unless you have previously given Thurgo an extra pie with nothing in return.");

		// Recommended
		houseTeleport = new ItemRequirement("A house teleport if your house is in Rimmington and if you have no faster way to Mudskipper Point", -1, -1);
		rellekkaTeleport = new ItemRequirement("A Camelot/Rellekka teleport (for starting the quest)", -1, -1);
		fairyRings = new ItemRequirement("Access to fairy rings", -1, -1);
		staminaPotions = new ItemRequirement("Some stamina potions (when collecting the ores)", -1, -1);
		varrockTeleport = new ItemRequirement("A ring of wealth/amulet of glory/Varrock teleport", -1, -1);
		clay10 = new ItemRequirement("Clay", ItemID.CLAY, 10);
		copperOre10 = new ItemRequirement("Copper ore", ItemID.COPPER_ORE, 10);
		tinOre10 = new ItemRequirement("Tin ore", ItemID.TIN_ORE, 10);
		ironOre10 = new ItemRequirement("Iron ore", ItemID.IRON_ORE, 10);
		coal10 = new ItemRequirement("Coal", ItemID.COAL, 10);
		silverOre10 = new ItemRequirement("Silver ore", ItemID.SILVER_ORE, 10);
		goldOre10 = new ItemRequirement("Gold ore", ItemID.GOLD_ORE, 10);
		mithrilOre10 = new ItemRequirement("Mithril ore", ItemID.MITHRIL_ORE, 10);
		bronzeBar10 = new ItemRequirement("Bronze bar", ItemID.BRONZE_BAR, 10);
		ironbar10 = new ItemRequirement("Iron bar", ItemID.IRON_BAR, 10);
		silverBar10 = new ItemRequirement("Silver bar", ItemID.SILVER_BAR, 10);
		goldBar10 = new ItemRequirement("Gold bar", ItemID.GOLD_BAR, 10);
		steelBar10 = new ItemRequirement("Steel bar", ItemID.STEEL_BAR, 10);
		mithrilBar10 = new ItemRequirement("Mithril bar", ItemID.MITHRIL_BAR, 10);

		// Extra
		weightBelow30 = new ItemRequirement("Weight below 30 kg", -1, -1);
		inventorySpace = new ItemRequirement("Free inventory slot", -1);

		// Quest
		bookOnCostumes = new ItemRequirement("Book on costumes", ItemID.BOOK_ON_COSTUMES);
		exquisiteClothes = new ItemRequirement("Exquisite clothes", ItemID.EXQUISITE_CLOTHES);
	}

	public void setupZones()
	{
		trollRoom = new Zone(new WorldPoint(2762, 10123, 0), new WorldPoint(2804, 10164, 0));
		dwarfEntrance = new Zone(new WorldPoint(2814, 10121, 0), new WorldPoint(2884, 10139, 0));
		keldagrim = new Zone(new WorldPoint(2816, 10177, 0), new WorldPoint(2943, 10239, 0));
		keldagrim2 = new Zone(new WorldPoint(2901, 10150, 0), new WorldPoint(2943, 10177, 0));

	}

	public void setupConditions()
	{
		inTrollRoom = new ZoneCondition(trollRoom);
		inDwarfEntrance = new ZoneCondition(dwarfEntrance);
		inKeldagrim = new ZoneCondition(keldagrim, keldagrim2);

		talkedToVermundi = new Conditions(true, LogicType.OR,
			new WidgetTextCondition(WidgetInfo.DIALOG_NPC_TEXT, true, "Great, thanks a lot, I'll check out the library!"));
			// TODO: Find widget text
			//new WidgetTextCondition(119, 3, true, true, "N/A"));

		talkedToLibrarian = new Conditions(true, LogicType.OR,
			new WidgetTextCondition(WidgetInfo.DIALOG_NPC_TEXT, true, "Well, thanks, I'll have a look."),
			new WidgetTextCondition(119, 3, true, true, "<col=000080>I must find the <col=800000>original axe<col=000080> of <col=800000>King Alvis<col=000080>."));

		hasBookOnCostumes = new Conditions(true, new ItemRequirementCondition(bookOnCostumes));

		talkedToVermundiWithBook = new Conditions(true, LogicType.OR,
			new WidgetTextCondition(WidgetInfo.DIALOG_NPC_TEXT, true, "Don't worry, I'll get them for you. Let's see... some<br>coal and some logs. Shouldn't be too hard."),
			new WidgetTextCondition(119, 3, true, true, "<col=000080>I need to get some fuel for the dwarven <col=800000>spinning machine<col=000080>."));

		usedCoalOnMachine = new Conditions(true, LogicType.OR,
			new ChatMessageCondition("You load the spinning machine with coal and logs."),
			new WidgetTextCondition(119, 3, true, true, "<col=000080>I have to start up the dwarven <col=800000>spinning machine<col=000080> in the"));

		startedMachine = new Conditions(true, LogicType.OR,
			new ChatMessageCondition("...and successfully start the engine!"),
			new WidgetTextCondition(119, 3, true, true, "<col=000080>I should ask <col=800000>Vermundi<col=000080>, the owner of the <col=800000>clothes stall<col=000080> in the"));

		hasExquisiteClothes = new Conditions(true, new ItemRequirementCondition(exquisiteClothes));
	}

	public void setupSteps()
	{
		// Starting off
		enterDwarfCave = new ObjectStep(this, ObjectID.TUNNEL_5008, new WorldPoint(2732, 3713, 0), "Speak to the Dwarven Boatman.");
		enterDwarfCave2 = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_5973, new WorldPoint(2781, 10161, 0), "Speak to the Dwarven Boatman.");
		talkToBoatman = new NpcStep(this, NpcID.DWARVEN_BOATMAN_7725, new WorldPoint(2829, 10129, 0), "Speak to the Dwarven Boatman.");
		talkToBoatman.addDialogStep("That's a deal!");
		talkToBoatman.addDialogStep("Yes, I'm ready and don't mind it taking a few minutes.");
		talkToBoatman.addSubSteps(enterDwarfCave, enterDwarfCave2);

		talkToVeldaban = new NpcStep(this, NpcID.COMMANDER_VELDABAN_6045, new WorldPoint(2827, 10214, 0), "Finish speaking to Commander Veldaban.");
		talkToVeldaban.addDialogStep("Yes, I will do this.");

		talkToBlasidar = new NpcStep(this, NpcID.BLASIDAR_THE_SCULPTOR, new WorldPoint(2907, 10205, 0), "Talk to Blasidar the scultor.");
		talkToBlasidar.addDialogStep("Yes, I will do this.");

		// Clothes fit for a king
		talkToVermundi = new NpcStep(this, NpcID.VERMUNDI, new WorldPoint(2887, 10188, 0), "Talk to Vermundi.");
		talkToVermundi.addDialogStep("Yes, I'm looking for some special clothes.");

		talkToLibrarian = new NpcStep(this, NpcID.LIBRARIAN, new WorldPoint(2861, 10226, 0), "Talk to the Librarian.");
		talkToLibrarian.addDialogStep("Do you know anything about King Alvis' clothes?");

		climbBookcase = new ObjectStep(this, ObjectID.BOOKCASE_6092, new WorldPoint(2859, 10228, 0), "Climb any bookcase to find a book on costumes.", weightBelow30);

		talkToVermundiWithBook = new NpcStep(this, NpcID.VERMUNDI, new WorldPoint(2887, 10188, 0), "Talk to Vermundi with the book on costumes.", bookOnCostumes);
		talkToVermundiWithBook.addDialogStep("Yes, about those special clothes again...");

		useCoalOnMachine = new ObjectStep(this, ObjectID.SPINNING_MACHINE, new WorldPoint(2885, 10188, 0), "Use your coal on the spinning machine light it with a tinder box.", coal);
		useCoalOnMachine.addIcon(ItemID.COAL);

		startMachine = new ObjectStep(this, ObjectID.SPINNING_MACHINE, new WorldPoint(2885, 10188, 0), "Start the spinning machine with a tinder box.", tinderbox);
		startMachine.addIcon(ItemID.TINDERBOX);

		talkToVermundiWithMachine = new NpcStep(this, NpcID.VERMUNDI, new WorldPoint(2887, 10188, 0), "Talk to Vermundi again and pay 200gp.", coins2500);
		talkToVermundiWithMachine.addDialogStep("Yes, about those special clothes again...");
		talkToVermundiWithMachine.addDialogStep("I'll pay.");

	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(coins2500, logs, tinderbox, coal, ironBar, lawRune, airRune, sapphires3, oresBars, redberryPie));
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Arrays.asList(houseTeleport, rellekkaTeleport, fairyRings, staminaPotions, varrockTeleport, clay10, copperOre10, tinOre10, ironOre10, coal10, silverOre10, goldOre10, mithrilOre10, bronzeBar10, ironbar10, silverBar10, goldBar10, steelBar10, mithrilBar10));
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Arrays.asList());
	}

	@Override
	public ArrayList<String> getNotes()
	{
		return new ArrayList<>(Arrays.asList());
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting out", new ArrayList<>(Arrays.asList(talkToBoatman, talkToVeldaban, talkToBlasidar))));
		allSteps.add(new PanelDetails("Clothes fit for a king", new ArrayList<>(Arrays.asList(talkToVermundi, talkToLibrarian, climbBookcase, talkToVermundiWithBook, useCoalOnMachine, startMachine, talkToVermundiWithMachine)), weightBelow30, logs, coal, tinderbox, coins2500, inventorySpace));
		return allSteps;
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		// Varbit 571
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		// Starting out
		ConditionalStep goToBoatman = new ConditionalStep(this, enterDwarfCave);
		goToBoatman.addStep(inTrollRoom, enterDwarfCave2);
		goToBoatman.addStep(inDwarfEntrance, talkToBoatman);
		steps.put(0, goToBoatman);
		steps.put(5, talkToVeldaban);
		steps.put(10, talkToBlasidar);

		// Clothes fit for a king
		ConditionalStep getExquisiteClothes = new ConditionalStep(this, talkToVermundi);
		getExquisiteClothes.addStep(startedMachine, talkToVermundiWithMachine);
		getExquisiteClothes.addStep(usedCoalOnMachine, startMachine);
		getExquisiteClothes.addStep(talkedToVermundiWithBook, useCoalOnMachine);
		getExquisiteClothes.addStep(hasBookOnCostumes, talkToVermundiWithBook);
		getExquisiteClothes.addStep(talkedToLibrarian, climbBookcase);
		getExquisiteClothes.addStep(talkedToVermundi, talkToLibrarian);
		steps.put(20, getExquisiteClothes);

		return steps;
	}
}