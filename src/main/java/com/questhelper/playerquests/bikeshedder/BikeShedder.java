/*
 * Copyright (c) 2024, pajlada <https://github.com/pajlada>
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
package com.questhelper.playerquests.bikeshedder;

import com.google.common.collect.ImmutableMap;
import com.questhelper.collections.ItemCollections;
import com.questhelper.collections.TeleportCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.steps.*;
import com.questhelper.steps.widget.NormalSpells;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.questhelper.requirements.util.LogicHelper.or;

public class BikeShedder extends BasicQuestHelper
{
	private DetailedQuestStep moveToLumbridge;
	private DetailedQuestStep confuseHans;
	private DetailedQuestStep equipLightbearer;

	private ItemRequirement anyLog;
	private ObjectStep useLogOnBush;

	private ItemRequirement oneCoin;
	private ItemRequirement manyCoins;
	private ObjectStep useCoinOnBush;
	private ObjectStep useManyCoinsOnBush;

	private ItemRequirement varrockTeleport;
	private ItemRequirement ardougneTeleport;
	private ItemRequirement faladorTeleport;

	private Zone conditionalRequirementZone;
	private ZoneRequirement conditionalRequirementZoneRequirement;
	private ZoneRequirement conditionalRequirementZoneSouthRequirement;
	private ZoneRequirement conditionalRequirementZoneNorthRequirement;
	private ItemRequirement conditionalRequirementCoins;
	private DetailedQuestStep conditionalRequirementLookAtCoins;
	private ItemRequirement conditionalRequirementGoldBar;
	private WidgetTextRequirement lookAtCooksAssistantRequirement;
	private DetailedQuestStep lookAtCooksAssistant;
	private WidgetTextRequirement lookAtCooksAssistantTextRequirement;
	private ZoneRequirement byStaircaseInSunrisePalace;
	private ObjectStep goDownstairsInSunrisePalace;
	private DetailedQuestStep haveRunes;
	private ItemRequirement lightbearer;
	private ItemRequirement elemental30Unique;
	private ItemRequirement elemental30;
	private ItemRequirement anyCoins;
	private ItemStep getCoins;

	// Sailing
	private NpcStep talkToNpcOnBoat;
	private NpcStep talkToKlarenceFromShip;
	private ObjectStep useSalvagingHook;
	private ObjectStep useObjectOffBoat;
	private ZoneRequirement onBoat1;
	private ZoneRequirement onBoat2;
	private ZoneRequirement onBoat3;
	private ZoneRequirement onBoat4;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		var lumbridge = new Zone(new WorldPoint(3217, 3210, 0), new WorldPoint(3226, 3228, 0));
		var outsideLumbridge = new ZoneRequirement(false, lumbridge);
		moveToLumbridge.setHighlightZone(lumbridge);
		var steps = new ConditionalStep(this, confuseHans);

		// aldarin bank
		var plankNoBank = new ItemRequirement("Plank (no bank)", ItemID.WOODPLANK);
		{
			// Do not check bank for plank
			var plankStep = new DetailedQuestStep(this, "Plank requirement will not check bank", plankNoBank);
			var plankStepPassed = new DetailedQuestStep(this, "Plank requirement will not check bank (Requirement used as conditional step passed)", plankNoBank);
			var cPlankStep = new ConditionalStep(this, plankStep);
			cPlankStep.addStep(plankNoBank, plankStepPassed);
			steps.addStep(new ZoneRequirement(new WorldPoint(1399, 2926, 0)), cPlankStep);
		}

		{
			// Check bank for plank using alsoCheckBank
			var plank = plankNoBank.alsoCheckBank();
			plank.setName("Plank (check bank 1)");
			var plankStep = new DetailedQuestStep(this, "Plank requirement will check bank 1", plank);
			var plankStepPassed = new DetailedQuestStep(this, "Plank requirement will check bank 1 (Requirement used as conditional step passed)", plank);
			var cPlankStep = new ConditionalStep(this, plankStep);
			cPlankStep.addStep(plank, plankStepPassed);
			steps.addStep(new ZoneRequirement(new WorldPoint(1399, 2925, 0)), cPlankStep);
		}

		{
			// Check bank for plank using setShouldCheckBank
			var plank = new ItemRequirement("Plank (no bank)", ItemID.WOODPLANK);
			plank.setShouldCheckBank(true);
			plank.setName("Plank (check bank 2)");
			var plankStep = new DetailedQuestStep(this, "Plank requirement will check bank 2", plank);
			var plankStepPassed = new DetailedQuestStep(this, "Plank requirement will check bank 2 (Requirement used as conditional step passed)", plank);
			var cPlankStep = new ConditionalStep(this, plankStep);
			cPlankStep.addStep(plank, plankStepPassed);
			steps.addStep(new ZoneRequirement(new WorldPoint(1399, 2924, 0)), cPlankStep);
		}

		// mistrock bank
		{
			// does not need to be equipped
			var blueWizardHat = new ItemRequirement("Blue wizard hat", ItemID.BLUEWIZHAT);
			var step = new DetailedQuestStep(this, "Blue wizard hat, does not have to be equipped", blueWizardHat);
			var stepPassed = new DetailedQuestStep(this, "Blue wizard hat, does not have to be equipped (Requirement used as conditional step passed)", blueWizardHat);
			var cStep = new ConditionalStep(this, step);
			cStep.addStep(blueWizardHat, stepPassed);
			steps.addStep(new ZoneRequirement(new WorldPoint(1383, 2866, 0)), cStep);
		}

		{
			// must be equipped
			var blueWizardHat = new ItemRequirement("Blue wizard hat", ItemID.BLUEWIZHAT);
			blueWizardHat.setMustBeEquipped(true);
			var step = new DetailedQuestStep(this, "Blue wizard hat, must be equipped", blueWizardHat);
			var stepPassed = new DetailedQuestStep(this, "Blue wizard hat, must be equipped (Requirement used as conditional step passed)", blueWizardHat);
			var cStep = new ConditionalStep(this, step);
			cStep.addStep(blueWizardHat, stepPassed);
			steps.addStep(new ZoneRequirement(new WorldPoint(1382, 2866, 0)), cStep);
		}

		// Boat
		steps.addStep(onBoat1, talkToNpcOnBoat);
		steps.addStep(onBoat2, talkToKlarenceFromShip);
		steps.addStep(onBoat3, useSalvagingHook);
		steps.addStep(onBoat4, useObjectOffBoat);

		steps.addStep(byStaircaseInSunrisePalace, goDownstairsInSunrisePalace);
		steps.addStep(outsideLumbridge, moveToLumbridge);
		steps.addStep(new ZoneRequirement(new WorldPoint(3224, 3218, 0)), haveRunes);
		steps.addStep(new ZoneRequirement(new WorldPoint(3222, 3218, 0)), equipLightbearer);
		steps.addStep(new ZoneRequirement(new WorldPoint(3223, 3218, 0)), useLogOnBush);
		steps.addStep(new ZoneRequirement(new WorldPoint(3222, 3217, 0)), useCoinOnBush);
		steps.addStep(new ZoneRequirement(new WorldPoint(3223, 3216, 0)), useManyCoinsOnBush);
		steps.addStep(new ZoneRequirement(new WorldPoint(3224, 3216, 0)), getCoins);
		steps.addStep(conditionalRequirementZoneRequirement, conditionalRequirementLookAtCoins);
		steps.addStep(new ZoneRequirement(new WorldPoint(3224, 3221, 0)), lookAtCooksAssistant);

		return new ImmutableMap.Builder<Integer, QuestStep>()
			.put(-1, steps)
			.build();
	}

	@Override
	protected void setupRequirements()
	{
		moveToLumbridge = new DetailedQuestStep(this, new WorldPoint(3221, 3218, 0), "Move to outside Lumbridge Castle");

		var normalSpellbook = new SpellbookRequirement(Spellbook.NORMAL);

		confuseHans = new NpcStep(this, NpcID.HANS, new WorldPoint(3221, 3218, 0), "Cast Confuse on Hans", normalSpellbook);
		confuseHans.addSpellHighlight(NormalSpells.CONFUSE);

		lightbearer = new ItemRequirement("Lightbearer", ItemID.LIGHTBEARER).highlighted();
		equipLightbearer = new DetailedQuestStep(this, "Equip a Lightbearer", lightbearer.equipped());

		anyLog = new ItemRequirement("Any log", ItemCollections.LOGS_FOR_FIRE).highlighted();

		useLogOnBush = new ObjectStep(this, ObjectID.PVPW_ARMOURSTAND_BUSH, new WorldPoint(3223, 3217, 0), "Use log on bush", anyLog);
		useLogOnBush.addIcon(ItemID.LOGS);

		varrockTeleport = TeleportCollections.VARROCK_TELEPORT.getItemRequirement();
		ardougneTeleport = TeleportCollections.ARDOUGNE_TELEPORT.getItemRequirement();
		faladorTeleport = TeleportCollections.FALADOR_TELEPORT.getItemRequirement();

		oneCoin = new ItemRequirement("Coins", ItemCollections.COINS, 1);
		oneCoin.setHighlightInInventory(true);
		useCoinOnBush = new ObjectStep(this, ObjectID.PVPW_ARMOURSTAND_BUSH, new WorldPoint(3223, 3217, 0), "Use coins on the bush.", oneCoin);
		useCoinOnBush.addIcon(ItemID.COINS);

		manyCoins = new ItemRequirement("Coins", ItemCollections.COINS, 100);
		manyCoins.setHighlightInInventory(true);
		useManyCoinsOnBush = new ObjectStep(this, ObjectID.PVPW_ARMOURSTAND_BUSH, new WorldPoint(3223, 3217, 0), "Use many coins on the bush.", manyCoins);
		useManyCoinsOnBush.addIcon(ItemID.COINS);

		conditionalRequirementZone = new Zone(new WorldPoint(3223, 3221, 0), new WorldPoint(3223, 3223, 0));
		conditionalRequirementZoneRequirement = new ZoneRequirement(conditionalRequirementZone);
		conditionalRequirementZoneSouthRequirement = new ZoneRequirement(new WorldPoint(3223, 3221, 0));
		conditionalRequirementZoneNorthRequirement = new ZoneRequirement(new WorldPoint(3223, 3223, 0));

		conditionalRequirementCoins = new ItemRequirement("Coins", ItemCollections.COINS, 50);
		conditionalRequirementCoins.setTooltip("Obtained by robbing a bank");
		conditionalRequirementCoins.setHighlightInInventory(true);
		conditionalRequirementCoins.setConditionToHide(conditionalRequirementZoneSouthRequirement);

		conditionalRequirementGoldBar = new ItemRequirement("Gold Bar", ItemID.GOLD_BAR, 1);
		conditionalRequirementGoldBar.setTooltip("Obtained by robbing a bank");
		conditionalRequirementGoldBar.setHighlightInInventory(true);
		conditionalRequirementGoldBar.setConditionToHide(or(conditionalRequirementZoneNorthRequirement, conditionalRequirementZoneSouthRequirement));

		conditionalRequirementLookAtCoins = new DetailedQuestStep(this, "Admire the coins in your inventory.", conditionalRequirementCoins);

		lookAtCooksAssistantRequirement = new WidgetTextRequirement(InterfaceID.Questjournal.TITLE, "Cook's Assistant");
		lookAtCooksAssistantRequirement.setDisplayText("Cook's Assistant quest journal open");
		lookAtCooksAssistantTextRequirement = new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "he now lets me use his high quality range");
		lookAtCooksAssistantTextRequirement.setDisplayText("Cook's Assistant quest journal open & received reward (checking text)");
		lookAtCooksAssistant = new DetailedQuestStep(this, "Open the Cook's Assistant quest journal. You must have started the quest for this test to work.", lookAtCooksAssistantRequirement, lookAtCooksAssistantTextRequirement);

		var upstairsInSunrisePalace = new Zone(new WorldPoint(1684, 3162, 1), new WorldPoint(1691, 3168, 1));
		byStaircaseInSunrisePalace = new ZoneRequirement(upstairsInSunrisePalace);
		goDownstairsInSunrisePalace = new ObjectStep(getQuest().getQuestHelper(), ObjectID.CIVITAS_PALACE_STAIRS_DOWN, new WorldPoint(1690, 3164, 1), "Climb downstairs, ensure stairs are well highlighted!");


		var fire30 = new ItemRequirement("Fire runes", ItemID.FIRERUNE, 30)
			.showConditioned(new SkillRequirement(Skill.MAGIC, 63));
		var air30 = new ItemRequirement("Air runes", ItemID.AIRRUNE, 30)
			.showConditioned(new SkillRequirement(Skill.MAGIC, 66));
		var water30 = new ItemRequirement("Water runes", ItemID.WATERRUNE, 30)
			.showConditioned(new SkillRequirement(Skill.MAGIC, 56));
		var earth30 = new ItemRequirement("Earth runes", ItemID.EARTHRUNE, 30)
			.showConditioned(new SkillRequirement(Skill.MAGIC, 60));
		elemental30Unique = new ItemRequirements(LogicType.OR, "Elemental runes as ItemRequirements OR", air30, water30, earth30, fire30);
		elemental30Unique.addAlternates(ItemID.FIRERUNE, ItemID.EARTHRUNE, ItemID.AIRRUNE);
		elemental30 = new ItemRequirement("Elemental rune as ItemRequirement", List.of(ItemID.AIRRUNE, ItemID.EARTHRUNE, ItemID.WATERRUNE,
			ItemID.FIRERUNE), 30);
		elemental30.setTooltip("You have potato");
		haveRunes = new DetailedQuestStep(this, "Compare rune checks for ItemRequirement and ItemRequirements with OR.", elemental30, elemental30Unique);

		anyCoins = new ItemRequirement("Coins", ItemCollections.COINS);
		getCoins = new ItemStep(this, new WorldPoint(3224, 3215, 0), "Get coins", anyCoins);

		// Sailing

		var zones1 = new Zone[] {
			new Zone(new WorldPoint(3843, 6402, 1), new WorldPoint(3844, 6402, 1)), // Tutorial
			new Zone(new WorldPoint(3842, 6389, 1), new WorldPoint(3876, 6389, 1)),
			new Zone(new WorldPoint(3842, 6365, 1), new WorldPoint(3860, 6365, 1)),
			new Zone(new WorldPoint(3843, 6354, 1), new WorldPoint(3884, 6354, 1))
		};
		onBoat1 = new ZoneRequirement(zones1);

		var zones2 = new Zone[] {
			new Zone(new WorldPoint(3843, 6403, 1), new WorldPoint(3844, 6403, 1)), // Tutorial
			new Zone(new WorldPoint(3842, 6390, 1), new WorldPoint(3876, 6390, 1)),
			new Zone(new WorldPoint(3842, 6366, 1), new WorldPoint(3860, 6366, 1)),
			new Zone(new WorldPoint(3843, 6355, 1), new WorldPoint(3884, 6355, 1))
		};
		onBoat2 = new ZoneRequirement(zones2);

		var zones3 = new Zone[] {
			new Zone(new WorldPoint(3843, 6404, 1), new WorldPoint(3844, 6404, 1)), // Tutorial
			new Zone(new WorldPoint(3842, 6391, 1), new WorldPoint(3876, 6391, 1)),
			new Zone(new WorldPoint(3842, 6367, 1), new WorldPoint(3860, 6367, 1)),
			new Zone(new WorldPoint(3843, 6356, 1), new WorldPoint(3884, 6356, 1))
		};
		onBoat3 = new ZoneRequirement(zones3);

		var zones4 = new Zone[] {
			new Zone(new WorldPoint(3843, 6405, 1), new WorldPoint(3844, 6405, 1)), // Tutorial
			new Zone(new WorldPoint(3842, 6312, 1), new WorldPoint(3876, 6392, 1)),
			new Zone(new WorldPoint(3842, 6368, 1), new WorldPoint(3860, 6368, 1)),
			new Zone(new WorldPoint(3843, 6357, 1), new WorldPoint(3884, 6357, 1))
		};
		onBoat4 = new ZoneRequirement(zones4);

		talkToNpcOnBoat = new NpcStep(this, NpcID.SAILING_INTRO_ANNE_BOAT, "Talk to a ship NPC.");
		List<Integer> shipNpcs = new ArrayList<>();
		for (int i = NpcID.SAILING_CREW_GENERIC_1_WORLD; i <= NpcID.SAILING_CREW_GHOST_JENKINS_CARGO_3; i++)
		{
			shipNpcs.add(i);
		}
		talkToNpcOnBoat.addHighlightZones(zones1);
		talkToNpcOnBoat.addAlternateNpcs(shipNpcs.toArray(new Integer[0]));

		talkToKlarenceFromShip = new NpcStep(this, NpcID.KLARENSE, new WorldPoint(3046, 3205, 0), "Klarence off the ship.");
		talkToKlarenceFromShip.setHighlightZone(new Zone(new WorldPoint(3044, 3202, 0), new WorldPoint(3050, 3205, 0)));
		talkToKlarenceFromShip.addHighlightZones(zones2);
		List<Integer> salvagingHookIds = new ArrayList<>();
		for (int i = ObjectID.SAILING_INTRO_SALVAGING_HOOK; i <= ObjectID.SALVAGING_HOOK_LARGE_DRAGON_B; i++)
		{
			salvagingHookIds.add(i);
		}

		useSalvagingHook = new ObjectStep(this, ObjectID.SAILING_INTRO_SALVAGING_HOOK, "Use the salvaging hook.");
		useSalvagingHook.addAlternateObjects(salvagingHookIds.toArray(new Integer[0]));
		useSalvagingHook.addHighlightZones(zones3);

		useObjectOffBoat = new ObjectStep(this, ObjectID.DRAGONSHIPGANGPLANK_ON, new WorldPoint(3047, 3205, 0), "Click Klarense's gangplank.");
		useObjectOffBoat.setHighlightZone(new Zone(new WorldPoint(3044, 3202, 0), new WorldPoint(3050, 3205, 0)));
		useObjectOffBoat.addHighlightZones(zones4);
	}

	@Override
	public List<ItemRequirement> getItemRecommended() {
		return Arrays.asList(varrockTeleport, ardougneTeleport, faladorTeleport, elemental30, elemental30Unique);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var panels = new ArrayList<PanelDetails>();

		panels.add(new PanelDetails("Move to Lumbridge", List.of(moveToLumbridge)));
		panels.add(new PanelDetails("Normal Spellbook", List.of(confuseHans)));
		panels.add(new PanelDetails("Equip Lightbearer", List.of(equipLightbearer), List.of(lightbearer)));
		panels.add(new PanelDetails("Use log on mysterious bush", List.of(useLogOnBush), List.of(anyLog)));
		panels.add(new PanelDetails("Use coins on mysterious bush", List.of(useCoinOnBush, useManyCoinsOnBush), List.of(oneCoin, manyCoins)));
		panels.add(new PanelDetails("Conditional requirement", List.of(conditionalRequirementLookAtCoins), List.of(conditionalRequirementCoins, conditionalRequirementGoldBar)));
		panels.add(new PanelDetails("Item step", List.of(getCoins), List.of(anyCoins)));
		panels.add(new PanelDetails("Quest state", List.of(lookAtCooksAssistant), List.of(lookAtCooksAssistantRequirement, lookAtCooksAssistantTextRequirement)));
		panels.add(new PanelDetails("Ensure staircase upstairs in Sunrise Palace is highlighted", List.of(goDownstairsInSunrisePalace), List.of()));
		panels.add(new PanelDetails("Sailing", List.of(talkToNpcOnBoat, talkToKlarenceFromShip, useSalvagingHook, useObjectOffBoat), List.of()));

		return panels;
	}
}
