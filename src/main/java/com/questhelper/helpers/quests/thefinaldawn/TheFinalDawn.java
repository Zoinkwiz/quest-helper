/*
 * Copyright (c) 2025, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.thefinaldawn;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;

import java.util.*;

import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import static com.questhelper.requirements.util.LogicHelper.*;

/**
 * The quest guide for the "The Final Dawn" OSRS quest
 */
public class TheFinalDawn extends BasicQuestHelper
{
	ItemRequirement emissaryRobesEquipped, emissaryRobes, bone, rangedGear;

	ItemRequirement combatGear, combatWeapon, food, prayerPotions, whistle, pendant;
	FreeInventorySlotRequirement freeInvSlots4;

	ItemRequirement drawerKey, canvasPiece, emissaryScroll, potatoes, knife, coinPurse, branch, coinPurseWithSand, emptySack, makeshiftBlackjack, trapdoorKey;
	ItemRequirement steamforgedBrew, dwarvenStout, beer, emptyGlass, wizardsMindBomb, keystoneFragment;

	DetailedQuestStep startQuest, searchChestForEmissaryRobes, enterTwilightTemple, goDownStairsTemple, enterBackroom, searchBed, openDrawers, openDrawers2;
	DetailedQuestStep useCanvasPieceOnPicture, enterPassage, pickBlueChest, fightEnforcer, pickUpEmissaryScroll, readEmissaryScroll, talkToQueen;
	DetailedQuestStep talkToCaptainVibia, inspectWindow, giveBonesOrMeatToDog, petDog, enterDoorCode, takePotato, takeKnife, takeCoinPurse;
	DetailedQuestStep climbUpStairsInHouse, useKnifeOnPottedFan, fillCoinPurse, takeSackOfPotatoes, useBranchOnCoinPurse, showSackToVibia, searchBodyForKey;
	DetailedQuestStep useKeyOnTrapdoor, talkToQueenToGoCamTorum, enterCamTorum, talkToAttala, talkToServiusInCamTorum, goUpstairsPub, takeBeer, useBeerOnGalna;
	DetailedQuestStep searchCabinetForDrinks, placeSteamforgedBrew, placeDwarvenStout, placeBeer, placeEmptyGlass, placeMindBomb, inspectFireplace, useHold;
	DetailedQuestStep returnToServius, enterNeypotzli, talkToEyatalli, defeatCultists, talkToServiusAtTalTeklan, enterTonaliCavern, defeatFinalCultists;

	Zone templeBasement, eastTempleBasement, hiddenRoom;

	Requirement inTempleBasement, inEastTempleBasement, inHiddenRoom;

	Requirement isSouthDrawer, hasDrawerKeyOrOpened, usedSigilOnCanvas, emissaryScrollNearby;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		// TODO: Swap back
//		initializeRequirements();
		setupZones();
		setupRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, startQuest);
		steps.put(1, startQuest);

		ConditionalStep goEnterTemple = new ConditionalStep(this, searchChestForEmissaryRobes);
		goEnterTemple.addStep(emissaryRobes, enterTwilightTemple);
		steps.put(3, goEnterTemple);

		ConditionalStep goEnterTempleBasement = new ConditionalStep(this, searchChestForEmissaryRobes);
		goEnterTempleBasement.addStep(inHiddenRoom, pickBlueChest);
		goEnterTempleBasement.addStep(and(inEastTempleBasement, usedSigilOnCanvas), enterPassage);
		goEnterTempleBasement.addStep(and(inEastTempleBasement, canvasPiece), useCanvasPieceOnPicture);
		goEnterTempleBasement.addStep(and(inEastTempleBasement, hasDrawerKeyOrOpened, isSouthDrawer), openDrawers2);
		goEnterTempleBasement.addStep(and(inEastTempleBasement, hasDrawerKeyOrOpened), openDrawers);
		goEnterTempleBasement.addStep(inEastTempleBasement, searchBed);
		goEnterTempleBasement.addStep(inTempleBasement, enterBackroom);
		goEnterTempleBasement.addStep(emissaryRobes, goDownStairsTemple);

		steps.put(4, goEnterTempleBasement);
		steps.put(5, goEnterTempleBasement);
		steps.put(6, goEnterTempleBasement);
		steps.put(7, goEnterTempleBasement);

		ConditionalStep goFightInBasement = new ConditionalStep(this, searchChestForEmissaryRobes);
		goFightInBasement.addStep(inEastTempleBasement, fightEnforcer);
		goFightInBasement.addStep(inTempleBasement, enterBackroom);
		goFightInBasement.addStep(emissaryRobes, goDownStairsTemple);
		steps.put(8, goFightInBasement);

		ConditionalStep goReadScroll = new ConditionalStep(this, searchChestForEmissaryRobes);
		goReadScroll.addStep(emissaryScroll, readEmissaryScroll);
		goReadScroll.addStep(and(or(inEastTempleBasement, inHiddenRoom), emissaryScrollNearby), pickUpEmissaryScroll);
		goReadScroll.addStep(inEastTempleBasement, enterPassage);
		goReadScroll.addStep(inHiddenRoom, pickBlueChest);
		goReadScroll.addStep(inTempleBasement, enterBackroom);
		goReadScroll.addStep(emissaryRobes, goDownStairsTemple);
		steps.put(9, goReadScroll);
		return steps;
	}

	@Override
	protected void setupZones()
	{
		templeBasement = new Zone(new WorldPoint(1660, 9680, 0), new WorldPoint(1725, 9720, 0));
		eastTempleBasement = new Zone(new WorldPoint(1707, 9696, 0), new WorldPoint(1718, 9715, 0));
		hiddenRoom = new Zone(new WorldPoint(1721, 9702, 0), new WorldPoint(1725, 9709, 0));
	}

	@Override
	protected void setupRequirements()
	{
		var emissaryHood = new ItemRequirement("Emissary hood", ItemID.VMQ3_CULTIST_HOOD);
		var emissaryTop = new ItemRequirement("Emissary top", ItemID.VMQ3_CULTIST_ROBE_TOP);
		var emissaryBottom = new ItemRequirement("Emissary bottom", ItemID.VMQ3_CULTIST_ROBE_BOTTOM);
		var emissaryBoots = new ItemRequirement("Emissary sandals", ItemID.VMQ3_CULTIST_SANDALS);
		emissaryRobesEquipped = new ItemRequirements("Emissary robes", emissaryHood, emissaryTop, emissaryBottom,
				emissaryBoots).equipped();
		emissaryRobes = new ItemRequirements("Emissary robes", emissaryHood, emissaryTop, emissaryBottom, emissaryBoots);

		// TODO: Add remaining bones
		bone = new ItemRequirement("Any type of bone", ItemID.BONES);
		bone.addAlternates(ItemID.BIG_BONES, ItemID.BONES_BURNT, ItemID.WOLF_BONES, ItemID.BAT_BONES, ItemID.DAGANNOTH_KING_BONES);

		rangedGear = new ItemRequirement("Ranged/Magic Combat gear", -1, -1).isNotConsumed();
		rangedGear.setDisplayItemId(BankSlotIcons.getRangedCombatGear());


		// Item Recommended
		combatWeapon = new ItemRequirement("Combat weapon", -1, -1).isNotConsumed();
		combatWeapon.setDisplayItemId(BankSlotIcons.getCombatGear());

		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		food.setUrlSuffix("Food");

		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS);

		whistle = new ItemRequirement("Quetzal whistle", ItemID.HG_QUETZALWHISTLE_BASIC);
		whistle.addAlternates(ItemID.HG_QUETZALWHISTLE_ENHANCED, ItemID.HG_QUETZALWHISTLE_PERFECTED);
		pendant = new ItemRequirement("Pendant of ates", ItemID.PENDANT_OF_ATES);

		// Quest items
		drawerKey = new ItemRequirement("Key", ItemID.VMQ4_DRAWER_KEY);
		canvasPiece = new ItemRequirement("Canvas piece", ItemID.VMQ4_PAINTING_SIGIL);
		emissaryScroll = new ItemRequirement("Emissary scroll", ItemID.VMQ4_CULT_MANIFEST);

		// Quest requirements
		inTempleBasement = new ZoneRequirement(templeBasement);
		inEastTempleBasement = new ZoneRequirement(eastTempleBasement);
		inHiddenRoom = new ZoneRequirement(hiddenRoom);

		isSouthDrawer = new VarbitRequirement(VarbitID.VMQ4_CANVAS_DRAWER, 2);
		hasDrawerKeyOrOpened = or(drawerKey, new VarbitRequirement(VarbitID.VMQ4_TEMPLE_DRAW_UNLOCKED, 1, Operation.GREATER_EQUAL));
		usedSigilOnCanvas = new VarbitRequirement(VarbitID.VMQ4, 7, Operation.GREATER_EQUAL);
		emissaryScrollNearby = new ItemOnTileRequirement(emissaryScroll);
	}

	public void setupSteps()
	{
		startQuest = new NpcStep(this, NpcID.VMQ3_SERVIUS_PALACE, new WorldPoint(1681, 3168, 0), "Talk to Servius in the Sunrise Palace in Vicitas illa " +
				"Fortis to start the quest.");
		startQuest.addDialogStep("Yes.");

		freeInvSlots4 = new FreeInventorySlotRequirement(4);
		searchChestForEmissaryRobes = new ObjectStep(this, ObjectID.VMQ3_CULTIST_OUTFIT_CHEST, new WorldPoint(1638, 3217, 0), "Search the chest in the south of the tower " +
				"for some emissary robes.", freeInvSlots4);
		searchChestForEmissaryRobes.addTeleport(pendant);
		enterTwilightTemple = new DetailedQuestStep(this, new WorldPoint(1687, 3247, 0), "Enter the temple south-east of Salvager Overlook.",
				emissaryRobesEquipped);

		goDownStairsTemple = new ObjectStep(this, ObjectID.TWILIGHT_TEMPLE_STAIRS, new WorldPoint(1677, 3248, 0), "Go down the stairs in the temple. The " +
				"passphrase is 'Final' and 'Dawn'.", List.of(emissaryRobesEquipped), List.of(combatWeapon, food));
		goDownStairsTemple.addDialogSteps("Final.", "Dawn.");

		enterBackroom = new ObjectStep(this, ObjectID.TWILIGHT_TEMPLE_METZLI_CHAMBER_ENTRY, new WorldPoint(1706, 9706, 0), "Enter the far eastern room. Avoid" +
				" the patrolling guard.");

		searchBed = new ObjectStep(this, ObjectID.VMQ4_TEMPLE_BED_WITH_KEY, new WorldPoint(1713, 9698, 0), "Search the bed in he south room.");
		openDrawers = new ObjectStep(this, ObjectID.VMQ4_TEMPLE_CANVAS_DRAW_1_CLOSED, new WorldPoint(1713, 9714, 0), "Open the drawers in the north room.");
		((ObjectStep) openDrawers).addAlternateObjects(ObjectID.VMQ4_TEMPLE_CANVAS_DRAW_1_OPEN);
		openDrawers.conditionToHideInSidebar(isSouthDrawer);

		openDrawers2 = new ObjectStep(this, ObjectID.VMQ4_TEMPLE_CANVAS_DRAW_2_CLOSED, new WorldPoint(1709, 9700, 0), "Open the drawers in the same room.");
		((ObjectStep) openDrawers2).addAlternateObjects(ObjectID.VMQ4_TEMPLE_CANVAS_DRAW_2_OPEN);
		openDrawers2.conditionToHideInSidebar(not(isSouthDrawer));
		useCanvasPieceOnPicture = new ObjectStep(this, ObjectID.TWILIGHT_TEMPLE_METZLI_PAINTING, new WorldPoint(1719, 9706, 0), "Use canvas piece on the " +
				"painting in the east of the middle room of the eastern rooms.", canvasPiece.highlighted());
		useCanvasPieceOnPicture.addIcon(ItemID.VMQ4_PAINTING_SIGIL);
		enterPassage = new ObjectStep(this, ObjectID.TWILIGHT_TEMPLE_METZLI_PAINTING, new WorldPoint(1719, 9706, 0), "Enter the passage behind the painting.");
		enterPassage.addDialogSteps("Enter the passage.");
		pickBlueChest = new ObjectStep(this, ObjectID.TWILIGHT_TEMPLE_METZLI_CHAMBER_CHEST_CLOSED, new WorldPoint(1723, 9709, 0), "Picklock the chest in the " +
				"hidden room. Be ready for a fight afterwards.");
		fightEnforcer = new NpcStep(this, NpcID.VMQ4_TEMPLE_GUARD_BOSS_FIGHT, new WorldPoint(1712, 9706, 0), "Defeat the enforcer. You cannot use prayers" +
				". Step away each time he goes to attack, and step behind him if he says 'Traitor!' or 'Thief!'.");
		pickUpEmissaryScroll = new ItemStep(this, "Pick up the emissary scroll.", emissaryScroll);
		readEmissaryScroll = new DetailedQuestStep(this, "Read the emissary scroll.", emissaryScroll.highlighted());
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			emissaryRobes, bone, combatGear
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			rangedGear, food, prayerPotions, pendant, whistle
		);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
				new QuestRequirement(QuestHelperQuest.THE_HEART_OF_DARKNESS, QuestState.FINISHED),
				new QuestRequirement(QuestHelperQuest.PERILOUS_MOON, QuestState.FINISHED),
				new SkillRequirement(Skill.THIEVING, 66),
				new SkillRequirement(Skill.FLETCHING, 52),
				new SkillRequirement(Skill.RUNECRAFT, 52)
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		// TODO: Add in rest
		return List.of(
			"Emissary Enforcer (lvl-196)"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(3);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.THIEVING, 55000),
			new ExperienceReward(Skill.FLETCHING, 25000),
			new ExperienceReward(Skill.RUNECRAFT, 25000)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("The Arkan Blade"),
			new UnlockReward("Access to Mokhaiotl"),
			new UnlockReward("Access to Crypt of Tonali")
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("55,000 Experience Lamps (Combat Skills)", ItemID.THOSF_REWARD_LAMP, 1)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var panels = new ArrayList<PanelDetails>();

		panels.add(new PanelDetails("Starting off", List.of(
			startQuest, searchChestForEmissaryRobes, enterTwilightTemple, goDownStairsTemple, enterBackroom, searchBed, openDrawers, openDrawers2,
				useCanvasPieceOnPicture, enterPassage, pickBlueChest, fightEnforcer, pickUpEmissaryScroll, readEmissaryScroll
		), List.of(
			combatWeapon, food
		), List.of(
			// Recommended
		)));

		return panels;
	}
}
