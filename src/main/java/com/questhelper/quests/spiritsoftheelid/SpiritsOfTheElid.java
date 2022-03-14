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
package com.questhelper.quests.spiritsoftheelid;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;

import java.util.*;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
        quest = QuestHelperQuest.SPIRITS_OF_THE_ELID
)
public class SpiritsOfTheElid extends BasicQuestHelper
{
	//Items Required
	ItemRequirement airRune, lawRune, needle, thread, crushWep, stabWep, slashWep, lightSource, knife, rope,
		pickaxe, bow, arrows, tornRobeTop, tornRobeBottom, robeOfElidinisTop, robeOfElidinisBottom,
		ancestralKey, shoes, soles, statuette, robeOfElidinisTopEquipped, robeOfElidinisBottomEquipped;

	//Items Recommended
	ItemRequirement combatGear, waterskins, shantaypass, coins, spear, food, necklaceOfPassage;

	//Quest Steps -- Broken down for part
	QuestStep speakToAwusah, speakToGhaslor, openCuboard, useNeedleTornRobes, useNeedleTornRobesTop, telegrabKey;
	QuestStep enterCave, useAncestralKey, openStabDoor, openStabDoorAfterGolem, clearChannel, openSlashDoor, openSlashDoorAfterGolem, clearChannel2, openCrushDoor, openCrushDoorAfterGolem, clearChannel3, openFarNorthDoor, speakToSpirits;
	QuestStep speakToAwusah2, takeShoes, leaveAwusah, cutShoes, enterCrevice, talkToGenie, talkToGenieAgain, useStatuette;

	//Conditions
	Requirement hasTornRobeTop, whiteGolem, greyGolem, blackGolem, stabChannel, slashChannel, crushChannel;
	Requirement inCaveEntrance, inWhiteGolemRoom, inGreyGolemRoom, inBlackGolemRoom, notAwusahHouse, insideCrevice,
		inSourceCave;

	//Zones
	Zone riverElidCaveEntrance, whiteGolemRoom, greyGolemRoom, blackGolemRoom, outsideAwusahHouse, creviceOutsideNardah,
		sourceCave;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, speakToAwusah);
		steps.put(10, speakToGhaslor);
		ConditionalStep getRobesAndKey = new ConditionalStep(this, openCuboard);
		getRobesAndKey.addStep(new Conditions(robeOfElidinisTop, robeOfElidinisBottom, ancestralKey), enterCave);
		getRobesAndKey.addStep(new Conditions(robeOfElidinisTop, robeOfElidinisBottom), telegrabKey);
		getRobesAndKey.addStep(new Conditions(hasTornRobeTop, robeOfElidinisBottom), useNeedleTornRobesTop);
		getRobesAndKey.addStep(new Conditions(tornRobeBottom, hasTornRobeTop), useNeedleTornRobes);
		steps.put(20, getRobesAndKey);

		ConditionalStep goUseKey = new ConditionalStep(this, enterCave);
		goUseKey.addStep(inCaveEntrance, useAncestralKey);
		steps.put(25, goUseKey);

		ConditionalStep clearChannels = new ConditionalStep(this, enterCave);
		clearChannels.addStep(new Conditions(inSourceCave, crushChannel), openFarNorthDoor);
		clearChannels.addStep(new Conditions(inSourceCave, inBlackGolemRoom), clearChannel3);
		clearChannels.addStep(new Conditions(inSourceCave, blackGolem), openCrushDoorAfterGolem);
		clearChannels.addStep(new Conditions(inSourceCave, slashChannel), openCrushDoor);
		clearChannels.addStep(new Conditions(inSourceCave, inGreyGolemRoom), clearChannel2);
		clearChannels.addStep(new Conditions(inSourceCave, greyGolem), openSlashDoorAfterGolem);
		clearChannels.addStep(new Conditions(inSourceCave, stabChannel), openSlashDoor);
		clearChannels.addStep(new Conditions(inSourceCave, inWhiteGolemRoom), clearChannel);
		clearChannels.addStep(new Conditions(inSourceCave, whiteGolem), openStabDoorAfterGolem);
		clearChannels.addStep(inSourceCave, openStabDoor);
		steps.put(27, clearChannels);

		ConditionalStep goSpeakToSpirits = new ConditionalStep(this, enterCave);
		goSpeakToSpirits.addStep(inSourceCave, speakToSpirits);
		steps.put(30, goSpeakToSpirits);
		steps.put(35, speakToAwusah2);

		ConditionalStep creviceSteps = new ConditionalStep(this, takeShoes);
		creviceSteps.addStep(insideCrevice, talkToGenie);
		creviceSteps.addStep(soles.alsoCheckBank(questBank), enterCrevice);
		creviceSteps.addStep(new Conditions(shoes, notAwusahHouse), cutShoes);
		creviceSteps.addStep(new Conditions(shoes), leaveAwusah);
		steps.put(40, creviceSteps);

		steps.put(50, talkToGenieAgain);
		steps.put(55, useStatuette);
		return steps;
	}

	public void setupItemRequirements()
	{
		//Requirements
		airRune = new ItemRequirement("Air Rune", ItemID.AIR_RUNE, 1);
		lawRune = new ItemRequirement("Law Rune", ItemID.LAW_RUNE, 1);
		needle = new ItemRequirement("Needle", ItemID.NEEDLE, 1);
		needle.setHighlightInInventory(true);
		thread = new ItemRequirement("Thread", ItemID.THREAD, 2);
		crushWep = new ItemRequirement("Crush Weapon Style", -1, 1);
		crushWep.setDisplayItemId(ItemID.RUNE_MACE);
		stabWep = new ItemRequirement("Stab Weapon Style", -1, 1);
		stabWep.setDisplayItemId(ItemID.RUNE_SWORD);
		slashWep = new ItemRequirement("Slash Weapon Style", -1, 1);
		slashWep.setDisplayItemId(ItemID.RUNE_SCIMITAR);
		lightSource = new ItemRequirement("Lightsource", ItemCollections.getLightSources(), 1);
		knife = new ItemRequirement("Knife", ItemID.KNIFE, 1);
		knife.setHighlightInInventory(true);
		rope = new ItemRequirement("Rope", ItemID.ROPE, 1);
		rope.setHighlightInInventory(true);
		pickaxe = new ItemRequirement("Any Pickaxe", ItemCollections.getPickaxes(), 1);
		bow = new ItemRequirement("Any bow", ItemCollections.getBows(), 1, true);
		bow.setTooltip("Short bow obtainable during quest east of the cave entrance");
		arrows = new ItemRequirement("Arrows for bow", ItemCollections.getMetalArrows(), 1, true);
		arrows.setTooltip("Bronze arrows obtainable during quest south of cave entrance");

		//Quest Items
		tornRobeTop = new ItemRequirement("Torn Robe (top)", ItemID.TORN_ROBE, 1);
		tornRobeTop.setHighlightInInventory(true);
		tornRobeBottom = new ItemRequirement("Torn Robe (bottom)", ItemID.TORN_ROBE_6789, 1);
		tornRobeBottom.setHighlightInInventory(true);
		robeOfElidinisTop = new ItemRequirement("Robe of Elidinis (top)", ItemID.ROBE_OF_ELIDINIS);
		robeOfElidinisBottom = new ItemRequirement("Robe of Elidinis (bottom)", ItemID.ROBE_OF_ELIDINIS_6787);
		robeOfElidinisTopEquipped = new ItemRequirement("Robe of Elidinis (top)", ItemID.ROBE_OF_ELIDINIS, 1, true);
		robeOfElidinisBottomEquipped = new ItemRequirement("Robe of Elidinis (bottom)", ItemID.ROBE_OF_ELIDINIS_6787, 1,
			true);
		ancestralKey = new ItemRequirement("Ancestral Key", ItemID.ANCESTRAL_KEY, 1);
		ancestralKey.setHighlightInInventory(true);
		shoes = new ItemRequirement("Shoes", ItemID.SHOES, 1);
		shoes.setHighlightInInventory(true);
		soles = new ItemRequirement("Sole", ItemID.SOLE, 1);
		statuette = new ItemRequirement("Statuette", ItemID.STATUETTE_6785, 1);
		statuette.setHighlightInInventory(true);

		//Recommended
		combatGear = new ItemRequirement("Combat Gear", -1, 1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		waterskins = new ItemRequirement("Waterskins", ItemID.WATERSKIN4, -1);
		waterskins.addAlternates(ItemID.WATERSKIN3, ItemID.WATERSKIN2, ItemID.WATERSKIN1);
		shantaypass = new ItemRequirement("Shantay Passes", ItemID.SHANTAY_PASS, -1);
		coins = new ItemRequirement("Coins for magic carpet", ItemCollections.getCoins(), -1);
		spear = new ItemRequirement("Spear or Hasta for the 3 weapon styles", -1, 1);
		spear.setDisplayItemId(ItemID.DRAGON_SPEAR);
		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);
		necklaceOfPassage = new ItemRequirement("Necklace of Passage", ItemCollections.getNecklaceOfPassages(), 1);
	}

	public void setupZones()
	{
		sourceCave = new Zone(new WorldPoint(3336, 9532, 0), new WorldPoint(3384, 9600, 0));
		riverElidCaveEntrance = new Zone(new WorldPoint(3343, 9532, 0), new WorldPoint(3356, 9544, 0));
		whiteGolemRoom = new Zone(new WorldPoint(3360, 9535, 0), new WorldPoint(3369, 9542, 0));
		greyGolemRoom = new Zone(new WorldPoint(3374, 9543, 0), new WorldPoint(3381, 9550, 0));
		blackGolemRoom = new Zone(new WorldPoint(3372, 9553, 0), new WorldPoint(3380, 9561, 0));
		outsideAwusahHouse = new Zone(new WorldPoint(3435, 2885, 0), new WorldPoint(3387, 2949, 0));
		creviceOutsideNardah = new Zone(new WorldPoint(3362, 9298, 0), new WorldPoint(3380, 9329, 0));
	}

	public void setupConditions()
	{
		hasTornRobeTop = new Conditions(LogicType.OR, tornRobeTop,
			robeOfElidinisTop);

		inSourceCave = new ZoneRequirement(sourceCave);
		inCaveEntrance = new ZoneRequirement(riverElidCaveEntrance);
		inWhiteGolemRoom = new ZoneRequirement(whiteGolemRoom);
		inGreyGolemRoom = new ZoneRequirement(greyGolemRoom);
		inBlackGolemRoom = new ZoneRequirement(blackGolemRoom);
		notAwusahHouse = new ZoneRequirement(outsideAwusahHouse);
		insideCrevice = new ZoneRequirement(creviceOutsideNardah);

		whiteGolem = new VarbitRequirement(1447, 1);
		greyGolem = new VarbitRequirement(1448, 1);
		blackGolem = new VarbitRequirement(1446, 1);
		stabChannel = new VarbitRequirement(1450, 1);
		slashChannel = new VarbitRequirement(1449, 1);
		crushChannel = new VarbitRequirement(1451, 1);
	}

	public void setupSteps()
	{
		//Starting Off
		speakToAwusah = new NpcStep(this, NpcID.AWUSAH_THE_MAYOR, new WorldPoint(3443, 2915, 0), "Speak to Awusah the Mayor in Nardah.");
		speakToAwusah.addDialogSteps("I am an adventurer in search of quests.", "Any idea how you got this curse?", "Yes.", "Ok I will have a look around and see what I can do.");
		speakToGhaslor = new NpcStep(this, NpcID.GHASLOR_THE_ELDER, new WorldPoint(3441, 2932, 0), "Speak to Ghaslor the Elder, just north of Awusah.");
		speakToGhaslor.addDialogSteps("I am trying to find out the cause of this town's curse.", "River spirits, what are they?");
		openCuboard = new ObjectStep(this, ObjectID.CUPBOARD_10384, new WorldPoint(3420, 2930, 0), "Open and search the cupboard in the north-west corner of the house that is north of the fountain.");
		((ObjectStep) (openCuboard)).addAlternateObjects(ObjectID.CUPBOARD_10385);
		useNeedleTornRobes = new DetailedQuestStep(this, "Use the needle and thread on the torn robes (both top and bottom).", needle, tornRobeBottom);
		useNeedleTornRobesTop = new DetailedQuestStep(this, "Use the needle and thread on the torn robes (both top and bottom).", needle, tornRobeTop);
		useNeedleTornRobes.addSubSteps(useNeedleTornRobesTop);
		telegrabKey = new ObjectStep(this, ObjectID.TABLE_10490, new WorldPoint(3432, 2929, 0), "Cast telegrab on the ancestral key on the table.", airRune, lawRune);

		//The Golems
		enterCave = new ObjectStep(this, ObjectID.ROOT_6382, new WorldPoint(3370, 3132, 0), "Enter the cave north-west of Nardah where the river turns into a waterfall.", rope);
		enterCave.addIcon(ItemID.ROPE);
		useAncestralKey = new ObjectStep(this, ObjectID.DOOR_10427, new WorldPoint(3353, 9544, 0), "Equip the Robes of " +
			"Elidinis and use the ancestral key on the door.", ancestralKey, robeOfElidinisTopEquipped,
			robeOfElidinisBottomEquipped);
		((ObjectStep) (useAncestralKey)).addAlternateObjects(ObjectID.DOOR_10429);
		useAncestralKey.addIcon(ItemID.ANCESTRAL_KEY);

		openStabDoor = new ObjectStep(this, ObjectID.DOOR_10420, new WorldPoint(3365, 9542, 0),
			"Open the door to the south and fight the White Golem (level-75).  You can only damage the Golem with the" +
				" stab attack style.", stabWep);
		openStabDoorAfterGolem = new ObjectStep(this, ObjectID.DOOR_10420, new WorldPoint(3365, 9542, 0), "Open the southern door again.");
		openStabDoor.addSubSteps(openStabDoorAfterGolem);
		clearChannel = new ObjectStep(this, ObjectID.WATER_CHANNEL_10407, new WorldPoint(3365, 9538, 0), "Clear the Water Channel then leave the room.");

		openSlashDoor = new ObjectStep(this, ObjectID.DOOR_10421, new WorldPoint(3374, 9547, 0),
			"Open the door to the east and fight the Grey Golem (level-75).  You can only damage the Golem with the " +
				"slash attack style.", slashWep);
		openSlashDoorAfterGolem = new ObjectStep(this, ObjectID.DOOR_10421, new WorldPoint(3374, 9547, 0), "Open the eastern door again.");
		openSlashDoor.addSubSteps(openSlashDoorAfterGolem);
		clearChannel2 = new ObjectStep(this, ObjectID.WATER_CHANNEL_10405, new WorldPoint(3378, 9547, 0), "Clear the Water Channel then leave the room.", pickaxe);

		openCrushDoor = new ObjectStep(this, ObjectID.DOOR_10419, new WorldPoint(3372, 9556, 0),
			"Open the door to the north-eastern and fight the Black Golem (level-75).  You can only damge the Golem " +
				"with the crush attack style.", crushWep);
		openCrushDoorAfterGolem = new ObjectStep(this, ObjectID.DOOR_10419, new WorldPoint(3372, 9556, 0), "Open the north-eastern door again.");
		openCrushDoor.addSubSteps(openCrushDoorAfterGolem);
		clearChannel3 = new NpcStep(this, NpcID.TARGET, new WorldPoint(3376, 9557, 0), "Clear the Water Channel by shooting the target then leave the room.", bow, arrows);

		openFarNorthDoor = new ObjectStep(this, ObjectID.DOOR_10423, new WorldPoint(3354, 9558, 0), "Open the door to the north near the lake.");
		speakToSpirits = new NpcStep(this, NpcID.NIRRIE, new WorldPoint(3364, 9589, 0), "Talk to the Water Spirits on the north side of the lake.", true);
		((NpcStep) (speakToSpirits)).addAlternateNpcs(NpcID.TIRRIE);
		((NpcStep) (speakToSpirits)).addAlternateNpcs(NpcID.HALLAK);

		speakToSpirits.addDialogSteps("I come as an emissary from the people of Nardah.", "Is there anything they can do to get their fountain working again?");

		//The Genie
		speakToAwusah2 = new NpcStep(this, NpcID.AWUSAH_THE_MAYOR, new WorldPoint(3443, 2915, 0), "Return to Nardah and speak to Awusah the Mayor.");
		takeShoes = new DetailedQuestStep(this, new WorldPoint(3439, 2913, 0),
			"Take the shoes near the doorway then leave Awusah's house.", shoes);
		leaveAwusah = new DetailedQuestStep(this, new WorldPoint(3431, 2915, 0), "Leave Awusah's home.");
		takeShoes.addSubSteps(leaveAwusah);
		cutShoes = new DetailedQuestStep(this, "Use a knife on the shoes to cut out the sole.", knife, shoes);
		enterCrevice = new ObjectStep(this, ObjectID.CREVICE_10416, new WorldPoint(3373, 2905, 0), "Enter the crevice to the west of Nardah.", rope, lightSource);
		enterCrevice.addIcon(ItemID.ROPE);
		talkToGenie = new NpcStep(this, NpcID.GENIE_4738, new WorldPoint(3371, 9320, 0), "Talk to the Genie in the Crevice.");
		talkToGenie.addDialogSteps("I'm after a statue that was thrown down here.", "Maybe I can make a deal for it?", "Ok I agree to the deal.");
		talkToGenieAgain = new NpcStep(this, NpcID.GENIE_4738, new WorldPoint(3371, 9320, 0), "Talk to the Genie again with the sole.", soles);
		useStatuette = new ObjectStep(this, NullObjectID.NULL_10389, new WorldPoint(3427, 2930, 0), "Return to Nardah and use the statuette on the statuette plinth in the northern house.", statuette);
		useStatuette.addIcon(ItemID.STATUETTE_6785);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(airRune, lawRune, needle, thread, crushWep, stabWep, slashWep, lightSource, knife, rope, pickaxe, bow, arrows);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(combatGear, waterskins, necklaceOfPassage, spear, coins, food);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Black Golem, Grey Golem, White Golem (Level 75)");
		return reqs;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.MAGIC, 33, true));
		reqs.add(new SkillRequirement(Skill.RANGED, 37, true));
		reqs.add(new SkillRequirement(Skill.MINING, 37, true));
		reqs.add(new SkillRequirement(Skill.THIEVING, 37, true));
		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.PRAYER, 8000),
				new ExperienceReward(Skill.THIEVING, 1000),
				new ExperienceReward(Skill.MAGIC, 1000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to Nardah's Fountain and Shrine"));
	}


	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting Off", Arrays.asList(speakToAwusah, speakToGhaslor, openCuboard, useNeedleTornRobes, telegrabKey), airRune, lawRune, needle, thread));
		allSteps.add(new PanelDetails("The Golems", Arrays.asList(enterCave, useAncestralKey, openStabDoor, clearChannel, openSlashDoor, clearChannel2, openCrushDoor, clearChannel3, openFarNorthDoor, speakToSpirits), ancestralKey, robeOfElidinisTop, robeOfElidinisBottom, rope, pickaxe, bow, arrows, crushWep, stabWep, slashWep));
		allSteps.add(new PanelDetails("The Genie", Arrays.asList(speakToAwusah2, takeShoes, cutShoes, enterCrevice, talkToGenie, talkToGenieAgain, useStatuette), knife, rope, lightSource));
		return allSteps;
	}
}
