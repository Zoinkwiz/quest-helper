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
import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.NoItemRequirement;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.util.ItemSlots;
import static com.questhelper.requirements.util.LogicHelper.nor;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.steps.*;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@QuestDescriptor(
		quest = QuestHelperQuest.HOPESPEARS_WILL
)
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
		setupZones();
		setupRequirements();
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
		defeatGoblins.addStep(new ItemOnTileRequirement(ItemID.SNOTHEADS_BONE), pickUpSnotheadBone);
		defeatGoblins.addStep(new ItemOnTileRequirement(ItemID.SNAILFEETS_BONE), pickUpSnailfeetBone);
		defeatGoblins.addStep(new ItemOnTileRequirement(ItemID.MOSSCHINS_BONE), pickUpMosschinBone);
		defeatGoblins.addStep(new ItemOnTileRequirement(ItemID.REDEYES_BONE), pickUpRedeyesBone);
		defeatGoblins.addStep(new ItemOnTileRequirement(ItemID.STRONGBONES_BONE), pickUpStrongbonesBone);
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

	public void setupZones()
	{
		goblinCave = new Zone(10393);
		goblinTemple = new Zone(14915);
		crypt = new Zone(14916);
		yubiusk = new Zone(14148);
	}

	public void setupRequirements()
	{
		ghostspeakAmulet = new ItemRequirement("Ghostspeak amulet", ItemCollections.GHOSTSPEAK);
		ghostspeakAmulet.setTooltip("or Morytania legs 2 or greater");
		ringOfVisibility = new ItemRequirement("Ring of Visibility", ItemID.RING_OF_VISIBILITY);
		ringOfVisibility.setTooltip("Obtain from Rasolo south of Baxtorian Falls");
		goblinPotion = new ItemRequirement("Goblin potion", Arrays.asList(ItemID.GOBLIN_POTION1, ItemID.GOBLIN_POTION2, ItemID.GOBLIN_POTION3, ItemID.GOBLIN_POTION4));
		goblinPotion.setTooltip("Made by mixing a toadflax potion (unf) and Pharmakos berries from near the Makeover Mage");
		dramenStaff = new ItemRequirement("Dramen or lunar staff", ItemCollections.FAIRY_STAFF);
		dramenStaff.setTooltip("For fairy ring access - not required if Lumbridge Elite is complete");

		potionsAndFood = new ItemRequirement("Food and potions to defeat 5 enemies without armour, weapons, or magic", -1, -1);
		plainOfMudSphere = new ItemRequirement("Plain of mud sphere or charged Skills necklace", ItemCollections.SKILLS_NECKLACES);
		plainOfMudSphere.addAlternates(ItemID.PLAIN_OF_MUD_SPHERE);
		combatLevel = new ItemRequirement("90+ combat", -1, -1);

		snotheadBones = new ItemRequirement("Snothead bones", ItemID.SNOTHEADS_BONE).alsoCheckBank(questBank);
		snailfeetBones = new ItemRequirement("Snailfeet bones", ItemID.SNAILFEETS_BONE).alsoCheckBank(questBank);
		mosschinBones = new ItemRequirement("Mosschin bones", ItemID.MOSSCHINS_BONE).alsoCheckBank(questBank);
		redeyesBones = new ItemRequirement("Redeyes bones", ItemID.REDEYES_BONE).alsoCheckBank(questBank);
		strongbonesBones = new ItemRequirement("Strongbones bones", ItemID.STRONGBONES_BONE).alsoCheckBank(questBank);

		inGoblinCave = new ZoneRequirement(goblinCave);
		nothingEquipped = new NoItemRequirement("No items equipped", ItemSlots.ANY_EQUIPPED);
		goblinWidgetActive = new WidgetTextRequirement(739, 2, 1, "Select Your Goblin");
		isAGoblin = new VarbitRequirement(13612, 1);
		inGoblinTemple = new ZoneRequirement(goblinTemple);
		isInCrypt = new ZoneRequirement(crypt);
		inYubiusk = new ZoneRequirement(yubiusk);

		snotheadAlive = new NpcRequirement("Snothead", NpcID.SNOTHEAD);
		snailfeetAlive = new NpcRequirement("Snailfeet", NpcID.SNAILFEET);
		mosschinAlive = new NpcRequirement("Mosschin", NpcID.MOSSCHIN);
		redeyesAlive = new NpcRequirement("Redeyes", NpcID.REDEYES);
		strongbonesAlive = new NpcRequirement("Strongbones", NpcID.STRONGBONES);

		snotheadBuried = new VarbitRequirement(13620, 1);
		snailfeetBuried = new VarbitRequirement(13621, 1);
		mosschinBuried = new VarbitRequirement(13622, 1);
		redeyesBuried = new VarbitRequirement(13623, 1);
		strongbonesBuried = new VarbitRequirement(13624, 1);

		hasSnotheadBones = new Conditions(LogicType.OR, snotheadBones, snotheadBuried);
		hasSnailfeetBones = new Conditions(LogicType.OR, snailfeetBones, snailfeetBuried);
		hasMosschinBones = new Conditions(LogicType.OR, mosschinBones, mosschinBuried);
		hasRedeyesBones = new Conditions(LogicType.OR, redeyesBones, redeyesBuried);
		hasStrongbonesBones = new Conditions(LogicType.OR, strongbonesBones, strongbonesBuried);
	}

	public void setupSteps()
	{
		goToGoblinCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE, new WorldPoint(2624, 3393, 0),
				"Enter the Goblin Cave next to the Fishing Guild. A plain of mud sphere will teleport you inside the cave.",
				goblinPotion, ringOfVisibility, ghostspeakAmulet, potionsAndFood);
		drinkGoblinPotion = new DetailedQuestStep(this, "Drink the goblin potion.", goblinPotion.highlighted());
		confirmGoblin = new WidgetStep(this, "Confirm to become a goblin. Your selection doesn't matter.", 739, 31);
		goDownStairs = new ObjectStep(this, ObjectID.STAIRS_43261, new WorldPoint(2581, 9853, 0),
				"Go down the stairs to the goblin temple. WARNING: If you equip your items too early, you will turn back to human!", ringOfVisibility, ghostspeakAmulet);
		goDownStairs.addDialogStep("Yes.");
		openCryptDoor = new ObjectStep(this, ObjectID.DOOR_43088, new WorldPoint(3744, 4332, 0),
				"Enter the door to the crypt. Don't equip your items yet!", ringOfVisibility, ghostspeakAmulet);
		talkToGhost = new NpcStep(this, NpcID.GHOST_11301, new WorldPoint(3742, 4389, 0),
				"Equip your ring and ghostspeak item and talk to the ghost in the crypt.", ringOfVisibility.equipped().highlighted(), ghostspeakAmulet.equipped().highlighted());
		talkToGhost.addDialogSteps("Why are you here?", "I visited Yu'biusk.", "It was a wasteland.", "What favour?", "Yes.");
		goToGoblinCaveAfterStart = new ObjectStep(this, ObjectID.CAVE_ENTRANCE, new WorldPoint(2624, 3393, 0),
				"Enter the Goblin Cave next to the Fishing Guild. A plain of mud sphere will teleport you inside the cave.",
				goblinPotion, potionsAndFood);
		goDownStairsAfterStart = new ObjectStep(this, ObjectID.STAIRS_43261, new WorldPoint(2581, 9853, 0),
				"Go down the stairs to the goblin temple.");
		openCryptDoorAfterStart = new ObjectStep(this, ObjectID.DOOR_43088, new WorldPoint(3744, 4332, 0),
				"Enter the door to the crypt.");

		sayNameSnothead = new ObjectStep(this, ObjectID.GRAVE_43122, new WorldPoint(3738, 4385, 0),
				"Unequip all weapons and armour (the ring of visibility and ghostspeak amulet can stay on but you MUST unequip Morytania legs). Say Snothead's name at the south-west grave.");
		sayNameSnothead.addDialogStep("Snothead.");
		sayNameSnailfeet = new ObjectStep(this, ObjectID.GRAVE_43123, new WorldPoint(3746, 4385, 0),
				"Unequip all weapons and armour (the ring of visibility and ghostspeak amulet can stay on but you MUST unequip Morytania legs). Say Snailfeet's name at the south-east grave.");
		sayNameSnailfeet.addDialogStep("Snailfeet.");
		sayNameMosschin = new ObjectStep(this, ObjectID.GRAVE_43124, new WorldPoint(3738, 4389, 0),
				"Unequip all weapons and armour (the ring of visibility and ghostspeak amulet can stay on but you MUST unequip Morytania legs). Say Mosschin's name at the north-west grave.");
		sayNameMosschin.addDialogStep("Mosschin.");
		sayNameRedeyes = new ObjectStep(this, ObjectID.GRAVE_43125, new WorldPoint(3746, 4389, 0),
				"Unequip all weapons and armour (the ring of visibility and ghostspeak amulet can stay on but you MUST unequip Morytania legs). Say Redeyes's name at the north-east grave.");
		sayNameRedeyes.addDialogStep("Redeyes.");
		sayNameStrongbones = new ObjectStep(this, ObjectID.GRAVE_43126, new WorldPoint(3742, 4393, 0),
				"Unequip all weapons and armour (the ring of visibility and ghostspeak amulet can stay on but you MUST unequip Morytania legs). Say Strongbones's name at the north grave.");
		sayNameStrongbones.addDialogStep("Strongbones.");

		defeatSnothead = new NpcStep(this, NpcID.SNOTHEAD, "Defeat Snothead WITHOUT using weapons, armour, or magic. He attacks using melee.");
		defeatSnailfeet = new NpcStep(this, NpcID.SNAILFEET, "Defeat Snailfeet WITHOUT using weapons, armour, or magic. He attacks using melee and range.");
		defeatMosschin = new NpcStep(this, NpcID.MOSSCHIN, "Defeat Mosschin WITHOUT using weapons, armour, or magic. He attacks using melee and magic.");
		defeatRedeyes = new NpcStep(this, NpcID.REDEYES, "Defeat Redeyes WITHOUT using weapons, armour, or magic. He attacks using melee and magic, and lowers your attack, strength, and defence.");
		defeatStrongbones = new NpcStep(this, NpcID.STRONGBONES, "Defeat Strongbones WITHOUT using weapons, armour, or magic. He attacks using all 3 combat styles, lowers your attack, strength and defence. Ignore the level 29 Skoblins he spawns.");

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
