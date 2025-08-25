/*
 * Copyright (c) 2022, scatter <https://github.com/scatter-dev>
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
package com.questhelper.helpers.miniquests.hopespearswill;

import com.google.common.collect.ImmutableList;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.NoItemRequirement;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.ItemSlots;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import java.util.*;

import static com.questhelper.requirements.util.LogicHelper.nor;

public class HopespearsWill extends BasicQuestHelper
{
	ItemRequirement ghostspeakAmulet, ringOfVisibility, goblinPotion, dramenStaff;
	ItemRequirement potionsAndFood, combatLevel, plainOfMudSphere;
	Zone goblinCave, goblinTemple, crypt, yubiusk;
	QuestStep goToGoblinCave, drinkGoblinPotion, confirmGoblin, goDownStairs, openCryptDoor, talkToGhost,
				goToGoblinCaveAfterStart, goDownStairsAfterStart, openCryptDoorAfterStart,
				sayNameSnothead, sayNameSnailfeet, sayNameMosschin, sayNameRedeyes, sayNameStrongbones,
				defeatSnothead, defeatSnailfeet, defeatMosschin, defeatRedeyes, defeatStrongbones,
				pickUpSnotheadBone, pickUpSnailfeetBone, pickUpMosschinBone, pickUpRedeyesBone, pickUpStrongbonesBone,
				goToYubiusk, burySnothead, burySnailfeet, buryMosschin, buryRedeyes, buryStrongbones;
	Requirement inGoblinCave, nothingEquipped, goblinWidgetActive, isAGoblin, inGoblinTemple, isInCrypt, inYubiusk,
				snotheadAlive, snailfeetAlive, mosschinAlive, redeyesAlive, strongbonesAlive,
				snotheadBuried, snailfeetBuried, mosschinBuried, redeyesBuried, strongbonesBuried;

	ItemRequirement snotheadBones, snailfeetBones, mosschinBones, redeyesBones, strongbonesBones;

	Conditions hasSnotheadBones, hasSnailfeetBones, hasMosschinBones, hasRedeyesBones, hasStrongbonesBones;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep startQuest = new ConditionalStep(this, goToGoblinCave);
		startQuest.addStep(goblinWidgetActive, confirmGoblin);
		startQuest.addStep(isInCrypt, talkToGhost);
		startQuest.addStep(inGoblinTemple, openCryptDoor);
		startQuest.addStep(new Conditions(inGoblinCave, isAGoblin), goDownStairs);
		startQuest.addStep(inGoblinCave, drinkGoblinPotion);

		steps.put(0, startQuest);

		Conditions hasAllBones = new Conditions(hasSnotheadBones, hasSnailfeetBones, hasMosschinBones, hasRedeyesBones, hasStrongbonesBones);

		ConditionalStep defeatGoblins = new ConditionalStep(this, openCryptDoor);
		defeatGoblins.addStep(snotheadAlive, defeatSnothead);
		defeatGoblins.addStep(snailfeetAlive, defeatSnailfeet);
		defeatGoblins.addStep(mosschinAlive, defeatMosschin);
		defeatGoblins.addStep(redeyesAlive, defeatRedeyes);
		defeatGoblins.addStep(strongbonesAlive, defeatStrongbones);
		defeatGoblins.addStep(new ItemOnTileRequirement(ItemID.LOTG_BONE_HIGHPRIEST1), pickUpSnotheadBone);
		defeatGoblins.addStep(new ItemOnTileRequirement(ItemID.LOTG_BONE_HIGHPRIEST2), pickUpSnailfeetBone);
		defeatGoblins.addStep(new ItemOnTileRequirement(ItemID.LOTG_BONE_HIGHPRIEST3), pickUpMosschinBone);
		defeatGoblins.addStep(new ItemOnTileRequirement(ItemID.LOTG_BONE_HIGHPRIEST4), pickUpRedeyesBone);
		defeatGoblins.addStep(new ItemOnTileRequirement(ItemID.LOTG_BONE_HIGHPRIEST5), pickUpStrongbonesBone);
		defeatGoblins.addStep(nor(hasSnotheadBones), sayNameSnothead);
		defeatGoblins.addStep(nor(hasSnailfeetBones), sayNameSnailfeet);
		defeatGoblins.addStep(nor(hasMosschinBones), sayNameMosschin);
		defeatGoblins.addStep(nor(hasRedeyesBones), sayNameRedeyes);
		defeatGoblins.addStep(nor(hasStrongbonesBones), sayNameStrongbones);

		ConditionalStep buryBones = new ConditionalStep(this, goToYubiusk);
		buryBones.addStep(nor(snotheadBuried), burySnothead);
		buryBones.addStep(nor(snailfeetBuried), burySnailfeet);
		buryBones.addStep(nor(mosschinBuried), buryMosschin);
		buryBones.addStep(nor(redeyesBuried), buryRedeyes);
		buryBones.addStep(nor(strongbonesBuried), buryStrongbones);

		ConditionalStep finishQuest = new ConditionalStep(this, goToGoblinCaveAfterStart);
		finishQuest.addStep(new Conditions(inYubiusk, hasAllBones), buryBones);
		finishQuest.addStep(hasAllBones, goToYubiusk);
		finishQuest.addStep(goblinWidgetActive, confirmGoblin);
		finishQuest.addStep(isInCrypt, defeatGoblins);
		finishQuest.addStep(inGoblinTemple, openCryptDoorAfterStart);
		finishQuest.addStep(new Conditions(inGoblinCave, isAGoblin), goDownStairsAfterStart);
		finishQuest.addStep(inGoblinCave, drinkGoblinPotion);

		steps.put(1, finishQuest);

		return steps;
	}

	@Override
	protected void setupZones()
	{
		goblinCave = new Zone(10393);
		goblinTemple = new Zone(14915);
		crypt = new Zone(14916);
		yubiusk = new Zone(14148);
	}

	@Override
	protected void setupRequirements()
	{
		ghostspeakAmulet = new ItemRequirement("Ghostspeak amulet", ItemCollections.GHOSTSPEAK);
		ghostspeakAmulet.setTooltip("or Morytania legs 2 or greater");
		ringOfVisibility = new ItemRequirement("Ring of Visibility", ItemID.FD_RING_VISIBILITY);
		ringOfVisibility.setTooltip("Obtain from Rasolo south of Baxtorian Falls");
		goblinPotion = new ItemRequirement("Goblin potion", Arrays.asList(ItemID.LOTG_1DOSEGOBLIN, ItemID.LOTG_2DOSEGOBLIN, ItemID.LOTG_3DOSEGOBLIN, ItemID.LOTG_4DOSEGOBLIN));
		goblinPotion.setTooltip("Made by mixing a toadflax potion (unf) and Pharmakos berries from near the Makeover Mage");
		dramenStaff = new ItemRequirement("Dramen or lunar staff", ItemCollections.FAIRY_STAFF);
		dramenStaff.setTooltip("For fairy ring access - not required if Lumbridge Elite is complete");

		potionsAndFood = new ItemRequirement("Food and potions to defeat 5 enemies without armour, weapons, or magic", -1, -1);
		plainOfMudSphere = new ItemRequirement("Plain of mud sphere or charged Skills necklace", ItemCollections.SKILLS_NECKLACES);
		plainOfMudSphere.addAlternates(ItemID.LOTG_TELEPORT_ARTIFACT);
		combatLevel = new ItemRequirement("90+ combat", -1, -1);

		snotheadBones = new ItemRequirement("Snothead bones", ItemID.LOTG_BONE_HIGHPRIEST1).alsoCheckBank(questBank);
		snailfeetBones = new ItemRequirement("Snailfeet bones", ItemID.LOTG_BONE_HIGHPRIEST2).alsoCheckBank(questBank);
		mosschinBones = new ItemRequirement("Mosschin bones", ItemID.LOTG_BONE_HIGHPRIEST3).alsoCheckBank(questBank);
		redeyesBones = new ItemRequirement("Redeyes bones", ItemID.LOTG_BONE_HIGHPRIEST4).alsoCheckBank(questBank);
		strongbonesBones = new ItemRequirement("Strongbones bones", ItemID.LOTG_BONE_HIGHPRIEST5).alsoCheckBank(questBank);

		inGoblinCave = new ZoneRequirement(goblinCave);
		nothingEquipped = new NoItemRequirement("No items equipped", ItemSlots.ANY_EQUIPPED);
		goblinWidgetActive = new WidgetTextRequirement(739, 2, 1, "Select Your Goblin");
		isAGoblin = new VarbitRequirement(VarbitID.LOTG_PLAYER_IS_A_GOBLIN, 1);
		inGoblinTemple = new ZoneRequirement(goblinTemple);
		isInCrypt = new ZoneRequirement(crypt);
		inYubiusk = new ZoneRequirement(yubiusk);

		snotheadAlive = new NpcRequirement("Snothead", NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST1);
		snailfeetAlive = new NpcRequirement("Snailfeet", NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST2);
		mosschinAlive = new NpcRequirement("Mosschin", NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST3);
		redeyesAlive = new NpcRequirement("Redeyes", NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST4);
		strongbonesAlive = new NpcRequirement("Strongbones", NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST5);

		snotheadBuried = new VarbitRequirement(VarbitID.HOPESPEAR_PRIEST_1, 1);
		snailfeetBuried = new VarbitRequirement(VarbitID.HOPESPEAR_PRIEST_2, 1);
		mosschinBuried = new VarbitRequirement(VarbitID.HOPESPEAR_PRIEST_3, 1);
		redeyesBuried = new VarbitRequirement(VarbitID.HOPESPEAR_PRIEST_4, 1);
		strongbonesBuried = new VarbitRequirement(VarbitID.HOPESPEAR_PRIEST_5, 1);

		hasSnotheadBones = new Conditions(LogicType.OR, snotheadBones, snotheadBuried);
		hasSnailfeetBones = new Conditions(LogicType.OR, snailfeetBones, snailfeetBuried);
		hasMosschinBones = new Conditions(LogicType.OR, mosschinBones, mosschinBuried);
		hasRedeyesBones = new Conditions(LogicType.OR, redeyesBones, redeyesBuried);
		hasStrongbonesBones = new Conditions(LogicType.OR, strongbonesBones, strongbonesBuried);
	}

	public void setupSteps()
	{
		goToGoblinCave = new ObjectStep(this, ObjectID.MCANNONCAVE, new WorldPoint(2624, 3393, 0),
				"Enter the Goblin Cave next to the Fishing Guild. A plain of mud sphere will teleport you inside the cave.",
				goblinPotion, ringOfVisibility, ghostspeakAmulet, potionsAndFood);
		drinkGoblinPotion = new DetailedQuestStep(this, "Drink the goblin potion.", goblinPotion.highlighted());
		confirmGoblin = new WidgetStep(this, "Confirm to become a goblin. Your selection doesn't matter.", 739, 31);
		goDownStairs = new ObjectStep(this, ObjectID.LOTG_GOBLIN_STAIRCASE, new WorldPoint(2581, 9853, 0),
				"Go down the stairs to the goblin temple. WARNING: If you equip your items too early, you will turn back to human!", ringOfVisibility, ghostspeakAmulet);
		goDownStairs.addDialogStep("Yes.");
		openCryptDoor = new ObjectStep(this, ObjectID.LOTG_TEMPLE_HUGE_DOOR, new WorldPoint(3744, 4332, 0),
				"Enter the door to the crypt. Don't equip your items yet!", ringOfVisibility, ghostspeakAmulet);
		talkToGhost = new NpcStep(this, NpcID.LOTG_HOPESPEAR_THERE, new WorldPoint(3742, 4389, 0),
				"Equip your ring and ghostspeak item and talk to the ghost in the crypt.", ringOfVisibility.equipped().highlighted(), ghostspeakAmulet.equipped().highlighted());
		talkToGhost.addDialogSteps("Why are you here?", "I visited Yu'biusk.", "It was a wasteland.", "What favour?", "Yes.");
		goToGoblinCaveAfterStart = new ObjectStep(this, ObjectID.MCANNONCAVE, new WorldPoint(2624, 3393, 0),
				"Enter the Goblin Cave next to the Fishing Guild. A plain of mud sphere will teleport you inside the cave.",
				goblinPotion, potionsAndFood);
		goDownStairsAfterStart = new ObjectStep(this, ObjectID.LOTG_GOBLIN_STAIRCASE, new WorldPoint(2581, 9853, 0),
				"Go down the stairs to the goblin temple.");
		openCryptDoorAfterStart = new ObjectStep(this, ObjectID.LOTG_TEMPLE_HUGE_DOOR, new WorldPoint(3744, 4332, 0),
				"Enter the door to the crypt.");

		sayNameSnothead = new ObjectStep(this, ObjectID.LOTG_CRYPT_PRIEST_GRAVE1, new WorldPoint(3738, 4385, 0),
				"Unequip all weapons and armour (the ring of visibility and ghostspeak amulet can stay on but you MUST unequip Morytania legs). Say Snothead's name at the south-west grave.");
		sayNameSnothead.addDialogStep("Snothead.");
		sayNameSnailfeet = new ObjectStep(this, ObjectID.LOTG_CRYPT_PRIEST_GRAVE2, new WorldPoint(3746, 4385, 0),
				"Unequip all weapons and armour (the ring of visibility and ghostspeak amulet can stay on but you MUST unequip Morytania legs). Say Snailfeet's name at the south-east grave.");
		sayNameSnailfeet.addDialogStep("Snailfeet.");
		sayNameMosschin = new ObjectStep(this, ObjectID.LOTG_CRYPT_PRIEST_GRAVE3, new WorldPoint(3738, 4389, 0),
				"Unequip all weapons and armour (the ring of visibility and ghostspeak amulet can stay on but you MUST unequip Morytania legs). Say Mosschin's name at the north-west grave.");
		sayNameMosschin.addDialogStep("Mosschin.");
		sayNameRedeyes = new ObjectStep(this, ObjectID.LOTG_CRYPT_PRIEST_GRAVE4, new WorldPoint(3746, 4389, 0),
				"Unequip all weapons and armour (the ring of visibility and ghostspeak amulet can stay on but you MUST unequip Morytania legs). Say Redeyes's name at the north-east grave.");
		sayNameRedeyes.addDialogStep("Redeyes.");
		sayNameStrongbones = new ObjectStep(this, ObjectID.LOTG_CRYPT_PRIEST_GRAVE5, new WorldPoint(3742, 4393, 0),
				"Unequip all weapons and armour (the ring of visibility and ghostspeak amulet can stay on but you MUST unequip Morytania legs). Say Strongbones's name at the north grave.");
		sayNameStrongbones.addDialogStep("Strongbones.");

		defeatSnothead = new NpcStep(this, NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST1, "Defeat Snothead WITHOUT using weapons, armour, or magic. He attacks using melee.");
		defeatSnailfeet = new NpcStep(this, NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST2, "Defeat Snailfeet WITHOUT using weapons, armour, or magic. He attacks using melee and range.");
		defeatMosschin = new NpcStep(this, NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST3, "Defeat Mosschin WITHOUT using weapons, armour, or magic. He attacks using melee and magic.");
		defeatRedeyes = new NpcStep(this, NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST4, "Defeat Redeyes WITHOUT using weapons, armour, or magic. He attacks using melee and magic, and lowers your attack, strength, and defence.");
		defeatStrongbones = new NpcStep(this, NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST5, "Defeat Strongbones WITHOUT using weapons, armour, or magic. He attacks using all 3 combat styles, lowers your attack, strength and defence. Ignore the level 29 Skoblins he spawns.");

		pickUpSnotheadBone = new DetailedQuestStep(this, "Pick up Snothead's bone from the ground.", snotheadBones.hideConditioned(snotheadBuried));
		pickUpSnailfeetBone = new DetailedQuestStep(this, "Pick up Snailfeet's bone from the ground.", snailfeetBones.hideConditioned(snailfeetBuried));
		pickUpMosschinBone = new DetailedQuestStep(this, "Pick up Mosschin's bone from the ground.", mosschinBones.hideConditioned(mosschinBuried));
		pickUpRedeyesBone = new DetailedQuestStep(this, "Pick up Redeyes's bone from the ground.", redeyesBones.hideConditioned(redeyesBuried));
		pickUpStrongbonesBone = new DetailedQuestStep(this, "Pick up Strongbones's bone from the ground.", strongbonesBones.hideConditioned(strongbonesBuried));

		goToYubiusk = new DetailedQuestStep(this, "Take all five bones to Yu'Biusk. You must get there via fairy ring BLQ.",
			snotheadBones.hideConditioned(snotheadBuried), snailfeetBones.hideConditioned(snailfeetBuried), mosschinBones.hideConditioned(mosschinBuried),
			redeyesBones.hideConditioned(redeyesBuried), strongbonesBones.hideConditioned(strongbonesBuried));
		burySnothead = new DetailedQuestStep(this, "Bury Snothead's bone.", snotheadBones.highlighted());
		burySnailfeet = new DetailedQuestStep(this, "Bury Snailfeet's bone.", snailfeetBones.highlighted());
		buryMosschin = new DetailedQuestStep(this, "Bury Mosschin's bone.", mosschinBones.highlighted());
		buryRedeyes = new DetailedQuestStep(this, "Bury Redeyes' bone.", redeyesBones.highlighted());
		buryStrongbones = new DetailedQuestStep(this, "Bury Strongbones' bone.", strongbonesBones.highlighted());

		goToGoblinCave.addSubSteps(goToGoblinCaveAfterStart);
		drinkGoblinPotion.addSubSteps(confirmGoblin);
		goDownStairsAfterStart.addSubSteps(goDownStairs);
		openCryptDoorAfterStart.addSubSteps(openCryptDoor);
		defeatSnothead.addSubSteps(sayNameSnothead, pickUpSnotheadBone);
		defeatSnailfeet.addSubSteps(sayNameSnailfeet, pickUpSnailfeetBone);
		defeatMosschin.addSubSteps(sayNameMosschin, pickUpMosschinBone);
		defeatRedeyes.addSubSteps(sayNameRedeyes, pickUpRedeyesBone);
		defeatStrongbones.addSubSteps(sayNameStrongbones, pickUpStrongbonesBone);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(ghostspeakAmulet, ringOfVisibility, goblinPotion, dramenStaff, potionsAndFood));
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Arrays.asList(plainOfMudSphere, combatLevel));
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(ImmutableList.of("Snothead (level 32)",
				"Snailfeet (level 56)",
				"Mosschin (level 88)",
				"Redeyes (level 121)",
				"Strongbones (level 184)"));
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.THE_RESTLESS_GHOST, QuestState.IN_PROGRESS));
		req.add(new QuestRequirement(QuestHelperQuest.DESERT_TREASURE, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.FAIRYTALE_II__CURE_A_QUEEN, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.LAND_OF_THE_GOBLINS, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.PRAYER, 50, false));
		return req;
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(new ExperienceReward(Skill.PRAYER, 38750));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> panels = new ArrayList<>();
		panels.add(new PanelDetails("Obtaining the Goblin Bones",
				Arrays.asList(goToGoblinCave, drinkGoblinPotion, goDownStairsAfterStart, openCryptDoorAfterStart, talkToGhost,
								defeatSnothead, defeatSnailfeet, defeatMosschin, defeatRedeyes, defeatStrongbones),
				Arrays.asList(goblinPotion, ringOfVisibility, ghostspeakAmulet, potionsAndFood),
				Arrays.asList(plainOfMudSphere)));
		panels.add(new PanelDetails("Freeing the Goblin Souls",
				Arrays.asList(goToYubiusk, burySnothead, burySnailfeet, buryMosschin, buryRedeyes, buryStrongbones),
				Arrays.asList(dramenStaff, snotheadBones, snailfeetBones, mosschinBones, redeyesBones, strongbonesBones)));
		return panels;
	}
}
