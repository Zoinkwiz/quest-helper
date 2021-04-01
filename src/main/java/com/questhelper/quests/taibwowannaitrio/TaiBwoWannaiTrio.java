/*
 * Copyright (c) 2021, Zoinkwiz
 * Copyright (c) 2021, itofu1
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
package com.questhelper.quests.taibwowannaitrio;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.WidgetTextRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;

@QuestDescriptor(
	quest = QuestHelperQuest.TAI_BWO_WANNAI_TRIO
)
public class TaiBwoWannaiTrio extends BasicQuestHelper
{
	ItemRequirement hammer, slicedBanana, banana, knife, slicedBananaOrKnife, smallFishingNet, pestleAndMortar, spear,
		agilityPotion4, rangedOrMagic, tinderbox, jogreBones, burntJogreBones, pastyJogreBones, marinatedJogreBones,
		anyJogreBones, seaweed, karamjanRum, karambwanji, poisonedSpear, logsForFire, rawKarambwans, coins, karambwanVessel,
		filledKarabmwanVessel, bananaSlices, karamjanRumWithBanana, monkeyCorpse,
		monkeySkin, seaweedSandwich, craftingManual;

	QuestStep goToTimfrakuLadder, talkToTimfrakuStart, fishKarambwaji, goToLubufu, dropVessel, getAnotherVessel, pickupVessel,
		getMoreVessel, fillVessel,	getRum, sliceBanana, makeBananaRum, talkToTiadeche1, giveVessel, getJogreBones,
		getMonkeyCorpse, talkToTamayu1,
		cookKarambwan, cookBones, talkToTamayu2, makeSeaweedSandwich, talkToTinsay, talkToTinsay1, talkToTinsay2, talkToTinsay3,
		goToTiadecheFinal, defaultStep, goToTimfrakuLadderEnd, talkToTimfrakuEnd;

	Requirement hasKarambwaji, inTimfrakusHut, inLufubuZone, givenKarambwanji, vesselOnGround, talkedToTiadeche,
		haveKaramjanRum, haveBananaSlices, haveKaramjanRumWithBanana, haveSeaweed, haveJogreBones, haveMonkeyCorpse,
		haveCookedJogreBones, havePoisonSpear, haveBurntBones, haveMonkeySkin, haveSeaweedSandwich, inCairnIsle,
		haveCraftingManual;

	Zone timfrakusHut, lubufuZone, cairnIsleZone;

	WorldPoint lubufuWorldPoint, timfrakuHutWorldPoint;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupWorldPoints();
		setupZones();
		setupConditions();
		setupSteps();

		ConditionalStep startQuest = new ConditionalStep(this, goToTimfrakuLadder);
		startQuest.addStep(inTimfrakusHut, talkToTimfrakuStart);


		Map<Integer, QuestStep> steps = new HashMap<>();
		steps.put(0, startQuest);
		steps.put(1, startQuest);
		steps.put(2, startQuest);

//		ConditionalStep findBrothers = new ConditionalStep(this, defaultStep);
//		findBrothers.addStep(new Conditions(LogicType.NOR, hasKarambwaji, haveCookedJogreBones, haveCraftingManual, haveCookedJogreBones), fishKarambwaji);
//		findBrothers.addStep(new Conditions(LogicType.OR, inLufubuZone, hasKarambwaji), goToLubufu);
//		findBrothers.addStep(givenKarambwanji, getMoreVessel);
//		findBrothers.addStep(karambwanVessel, getMoreVessel);
//		findBrothers.addStep(vesselOnGround, getMoreVessel);
//		findBrothers.addStep(karambwanVessel.quantity(2), fillVessel);
//		findBrothers.addStep(new Conditions(LogicType.AND,
//			filledTwoVessel,
//			new Conditions(LogicType.NOR, haveKaramjanRumWithBanana, inCairnIsle),
//			new Conditions(LogicType.NAND, haveKaramjanRum, haveBananaSlices)), getRum);
//		findBrothers.addStep(new Conditions(LogicType.AND,
//			filledTwoVessel, haveKaramjanRum, haveBananaSlices,
//			new Conditions(LogicType.NOR, haveKaramjanRumWithBanana)), makeBananaRum);
//		findBrothers.addStep(new Conditions(LogicType.AND, filledTwoVessel, haveKaramjanRumWithBanana, new Conditions(LogicType.NOR, haveSeaweed, haveSeaweedSandwich)), talkToTiadeche1);
//		findBrothers.addStep(new Conditions(LogicType.AND, haveSeaweed, new Conditions(LogicType.NOR, haveSeaweedSandwich), new Conditions(LogicType.NOR, haveJogreBones, haveBurntBones, haveCookedJogreBones)), getJogreBones);
//		findBrothers.addStep(new Conditions(LogicType.AND, haveSeaweed, new Conditions(LogicType.NOR, haveMonkeyCorpse, haveMonkeySkin, haveSeaweedSandwich)), getMonkeyCorpse);
//		findBrothers.addStep(new Conditions(LogicType.AND, haveSeaweed, haveMonkeyCorpse,  new Conditions(LogicType.NOR, haveBurntBones, haveCookedJogreBones)), talkToTamayu1);
//		findBrothers.addStep(new Conditions(LogicType.AND, haveSeaweed, haveMonkeyCorpse, haveBurntBones, new Conditions(LogicType.NOR, havePoisonSpear)), cookKarambwan);
//		findBrothers.addStep(new Conditions(LogicType.AND, haveSeaweed, haveMonkeyCorpse, haveBurntBones, havePoisonSpear, new Conditions(LogicType.NOR, haveCookedJogreBones)), cookBones);
//		findBrothers.addStep(new Conditions(LogicType.AND, haveSeaweed, haveMonkeyCorpse, haveCookedJogreBones), talkToTamayu2);
//		findBrothers.addStep(new Conditions(LogicType.AND, haveSeaweed, haveMonkeySkin, haveKaramjanRumWithBanana), makeSeaweedSandwich);
//		findBrothers.addStep(new Conditions(LogicType.AND, haveSeaweedSandwich, haveCookedJogreBones, haveKaramjanRumWithBanana), talkToTinsay);
//		findBrothers.addStep(new Conditions(LogicType.AND, inCairnIsle, haveCookedJogreBones, haveSeaweedSandwich, new Conditions(LogicType.NOR, haveKaramjanRumWithBanana)), talkToTinsay1);
//		findBrothers.addStep(new Conditions(LogicType.AND, inCairnIsle, haveCookedJogreBones, new Conditions(LogicType.NOR, haveKaramjanRumWithBanana, haveSeaweedSandwich)), talkToTinsay2);
//		findBrothers.addStep(new Conditions(LogicType.AND, inCairnIsle, new Conditions(LogicType.NOR, haveCraftingManual, haveKaramjanRumWithBanana, haveSeaweedSandwich, haveCookedJogreBones)), talkToTinsay3);
//		findBrothers.addStep(new Conditions(LogicType.AND, haveCraftingManual), goToTiadecheFinal);

		ConditionalStep try2 = new ConditionalStep(this, fishKarambwaji);
		try2.addStep(new Conditions(filledKarabmwanVessel.quantity(2), karamjanRumWithBanana, talkedToTiadeche), talkToTiadeche1);
		try2.addStep(new Conditions(filledKarabmwanVessel.quantity(2), karamjanRumWithBanana), talkToTiadeche1);
		try2.addStep(new Conditions(filledKarabmwanVessel.quantity(2), karamjanRum, bananaSlices), makeBananaRum);
		try2.addStep(new Conditions(filledKarabmwanVessel.quantity(2), karamjanRum), sliceBanana);
		try2.addStep(new Conditions(filledKarabmwanVessel.quantity(2)), getRum);
		try2.addStep(new Conditions(karambwanVessel.quantity(2)), fillVessel);
		try2.addStep(new Conditions(vesselOnGround, karambwanVessel), pickupVessel);
		try2.addStep(new Conditions(vesselOnGround), getAnotherVessel);
		try2.addStep(new Conditions(karambwanVessel), dropVessel);
		try2.addStep(new Conditions(LogicType.OR, givenKarambwanji, karambwanVessel, vesselOnGround), getMoreVessel);
		try2.addStep(karambwanji.quantity(23), goToLubufu);

		steps.put(3, try2);

		ConditionalStep endQuest = new ConditionalStep(this, goToTimfrakuLadderEnd);
		endQuest.addStep(inTimfrakusHut, talkToTimfrakuEnd);

		steps.put(4, endQuest);
		return steps;
	}

	private void setupWorldPoints()
	{
		timfrakuHutWorldPoint = new WorldPoint(2782,3087,0);
		lubufuWorldPoint = new WorldPoint(2769,3171,0);
	}

	private void setupSteps()
	{
		goToTimfrakuLadder = new ObjectStep(this, ObjectID.LADDER_16683, timfrakuHutWorldPoint,
			"Talk Timfraku upstairs in his house in Tai Bwo Wannai.");

		talkToTimfrakuStart = new NpcStep(this, NpcID.TIMFRAKU, "Talk Timfraku upstairs in his house in Tai Bwo Wannai.");
		talkToTimfrakuStart.addDialogSteps("I am a roving adventurer.", "I am a travelling explorer.", "I am a " +
			"wandering wayfarer.", "Who me? Oh I'm just a nobody.");
		talkToTimfrakuStart.addDialogStep("Trufitus sent me.");
		talkToTimfrakuStart.addDialogSteps("Your gratitude is all I deserve.", "Well, some gold would be nice.", "So " +
			"far??", "Yes");
		talkToTimfrakuStart.addSubSteps(goToTimfrakuLadder);

		defaultStep = new DetailedQuestStep(this, "Hmm, it seems the quest helper has no clue what to do. " +
			"Because this quest is old and the dev had to rely on janky logic it is most likely fixed by opening your quest " +
			"journal and letting the helper do its thing but in the event that does not fix it create an issue on " +
			"github or ask in the discord channel.");

		fishKarambwaji = new NpcStep(this, NpcID.FISHING_SPOT_4710, new WorldPoint(2791,3019,0),
			"Using your small fishing net, catch atleast 23 raw karambwanji just south of Tai Bwo Wannai. If you've " +
				"already given them to Lubufu, open your quest journal to re-sync.", smallFishingNet);

		goToLubufu = new NpcStep(this, NpcID.LUBUFU, lubufuWorldPoint, "Go to Brimhaven and talk to Lubufu." +
			"You have to talk to him multiple times. You'll need to ask him twice about what he does, then talk to " +
			"him to give him the karambwanji.", karambwanji.quantity(20));
		goToLubufu.addDialogStep("Talk about him...");
		goToLubufu.addDialogStep("What do you do?");
		goToLubufu.addDialogStepWithExclusion("What do you do with your Karambwan?", "I could help collect the bait.");
		goToLubufu.addDialogStep("I could help collect the bait.");
		goToLubufu.addDialogStep("You sound like you could do with the help.");
		goToLubufu.addDialogStep("What do you use to catch Karambwan?");
		goToLubufu.addDialogStep("Yes!");

		getMoreVessel = new NpcStep(this, NpcID.LUBUFU, lubufuWorldPoint,
			"Get a Vessel from Lubufu by asking what he uses to catch them multiple times. Drop it, and talk to him " +
				"go get another, then pick up the one you dropped. You can get as many as you want but you need 2 to" +
				" complete this quest.");
		getMoreVessel.addDialogStep("What do you use to catch Karambwan?");
		getMoreVessel.addDialogStep("Yes!");
		getMoreVessel.addDialogStep("Actually, I've lost my Karambwan vessel.");
		getMoreVessel.addDialogStep("... a shark ate it!");

		dropVessel = new DetailedQuestStep(this, "Drop the vessel, then get another from Lubufu.", karambwanVessel.highlighted());
		getAnotherVessel = new NpcStep(this, NpcID.LUBUFU, lubufuWorldPoint,
			"Get another Vessel from Lubufu.");
		getAnotherVessel.addDialogStep("Actually, I've lost my Karambwan vessel.");
		getAnotherVessel.addDialogStep("... a shark ate it!");
		pickupVessel = new ItemStep(this, "Pick up the vessel you dropped.", karambwanVessel.quantity(2));
		getMoreVessel.addSubSteps(dropVessel, getAnotherVessel, pickupVessel);

		fillVessel = new DetailedQuestStep(this, "Use a karambwanji on a karambwan Vessel to fill it. Note " +
			"if you have less karambwanji than vessel then this will use up all your karambwanji and will require you to " +
			"fish for atleast 1 more to complete the quest.", karambwanji, karambwanVessel);

		getRum = new NpcStep(this, NpcID.ZAMBO, new WorldPoint(2925, 3143, 0),
			"Go east to Musa point to buy some Karamjan rum from Zambo.", coins.quantity(30));

		sliceBanana = new DetailedQuestStep(this, "Slice a banana with a knife. You can get a banana from one of the " +
			"trees in the plantation.", knife.highlighted(), banana.highlighted());
		makeBananaRum = new ItemStep(this, "Add banana slices to the karamjan rum to make Karamjan rum with banana " +
			"slices.", bananaSlices.highlighted(), karamjanRum.highlighted());

		talkToTiadeche1 = new NpcStep(this, NpcID.TIADECHE, new WorldPoint(2912, 3116, 0),
			"Talk to Tiadeche in east Karamja, near the fairy ring DKP.");

		giveVessel = new NpcStep(this, NpcID.TIADECHE, new WorldPoint(2912, 3116, 0),
			"Use a filled vessel on Tiadeche.", filledKarabmwanVessel.highlighted());
		giveVessel.addIcon(ItemID.KARAMBWAN_VESSEL_3159);

		getJogreBones = new DetailedQuestStep(this, new WorldPoint(2925, 3062, 0),
			"If you do not have a Jogre bone yet there are Jogre south and you should kill one now to get its bones.");
		getMonkeyCorpse = new DetailedQuestStep(this,
			"Kill a monkey to get its corpse, there are plenty of monkeys around Karamja and you will need either magic or range to kill one");

		talkToTamayu1 = new NpcStep(this, NpcID.TAMAYU, new WorldPoint(2845, 3041, 0),
			"Talk to Tamayu, he is located north of Shilo Village near the mining sign on the map," +
				" after watching him use your tinder box on your jogre bones and pick up the burnt jogre bones.");
		talkToTamayu1.addDialogStep("When will you succeed?");
		talkToTamayu1.addDialogStep("Yes");

		cookKarambwan = new DetailedQuestStep(this,
			"Light a fire using your tinderbox and logs and cook your karambwan (If it is burnt you will need to " +
				"either buy another one or fish it with 65 fishing). Use your poison karambwan on your pestel and mortar " +
				"to make a poison paste and use that on your spear.");
		cookBones = new DetailedQuestStep(this,
			"Use a karambwanji on your pestle and mortar to make a karambwanji paste, " +
				"use that paste on your burnt jogre bones and cook the outcome again on the fire.");
		talkToTamayu2 = new NpcStep(this, NpcID.TAMAYU, new WorldPoint(2845, 3041, 0),
			"Use your 4 dose agility potion and poisoned spear on Tamayu then talk to him. " +
				"After the cutscene use your monkey corpse on him to have him skin it for you.");
		talkToTamayu2.addDialogStep("Take me on your next hunt for the Shaikahan.");

		makeSeaweedSandwich = new DetailedQuestStep(this, "Use your seaweed on your monkey skin to make a seaweed sandwich");

		talkToTinsay = new NpcStep(this, NpcID.TINSAY, new WorldPoint(2764, 2975, 0),
			"Talk to the third brother, he is located on Carin Isle west of Shilo Village, south of fairy ring CKR." +
				" You will need some food if your agility level is low. After talking to him use your bottle of rum with banana slices on him");
		talkToTinsay1 = new NpcStep(this, NpcID.TINSAY, new WorldPoint(2764, 2975, 0),
			"Finish the conversation and use your seaweed sandwich on him. Be careful to right click on " +
				"the seaweed sandwich as the default option is to eat it.");
		talkToTinsay2 = new NpcStep(this, NpcID.TINSAY, new WorldPoint(2764, 2975, 0),
			"Finish the conversation and use your marinated j'bones on him. Be careful to right click on " +
				"your marinated bones as the default option is the bury it");
		talkToTinsay3 = new NpcStep(this, NpcID.TINSAY, new WorldPoint(2764, 2975, 0),
			"Use a karambwan vessel on Tinsay to get a crafting manual.");

		goToTiadecheFinal = new NpcStep(this, NpcID.TIADECHE, new WorldPoint(2912, 2116, 0),
			"Go to back to Tiadeche located north of fairy ring DKP and use the crafting manual on him");

		goToTimfrakuLadderEnd = new ObjectStep(this, ObjectID.LADDER_16683, timfrakuHutWorldPoint,
			"Go to Timfraku's house to finish the quest");
		talkToTimfrakuEnd = new NpcStep(this, NpcID.TIMFRAKU,
			"Talk to Timfraku to end the quest, the option chosen here does not matter. " +
				"NOTE: you will need to talk to each of the brothers individually to receive " +
				"experience rewards as well as the ability to cook karambwans properly");
	}


	private void setupConditions()
	{
		inTimfrakusHut = new ZoneRequirement(timfrakusHut);
		hasKarambwaji = new ItemRequirements(karambwanji);
		inLufubuZone = new ZoneRequirement(lubufuZone);
		givenKarambwanji = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(193, 2, "You hand Lubufu 20 raw Karambwanji."),
			new WidgetTextRequirement(119, 3, true, "<str>I have given Lubufu 20 Karambwanji.")
		);

		vesselOnGround = new ItemOnTileRequirement(karambwanVessel);
		haveKaramjanRum = new ItemRequirements(karamjanRum);
		haveBananaSlices = new ItemRequirements(bananaSlices);
		haveKaramjanRumWithBanana = new ItemRequirements(karamjanRumWithBanana);

		talkedToTiadeche = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "I will return only when I have caught a Karambwan."),
			new WidgetTextRequirement(219, 1, true, "How are you fishing for the Karambwan?"),
			new WidgetTextRequirement(119, 3, true, "<col=000080>He will only return to the village once he has caught a")
		);

		haveSeaweed = new ItemRequirements(seaweed);
		haveJogreBones = new ItemRequirements(jogreBones);
		haveMonkeyCorpse = new ItemRequirements(monkeyCorpse);
		haveCookedJogreBones = new ItemRequirements(marinatedJogreBones);
		havePoisonSpear = new ItemRequirements(poisonedSpear);
		haveBurntBones = new ItemRequirements(burntJogreBones);
		haveMonkeySkin = new ItemRequirements(monkeySkin);
		haveSeaweedSandwich = new ItemRequirements(seaweedSandwich);
		inCairnIsle = new ZoneRequirement(cairnIsleZone);
		haveCraftingManual = new ItemRequirements(craftingManual);
	}

	private void setupZones()
	{
		timfrakusHut = new Zone(new WorldPoint(2778,3084,1), new WorldPoint(2786, 3090,1));
		lubufuZone = new Zone(new WorldPoint(2759,3173,0), new WorldPoint(2780,3162,0));
		cairnIsleZone = new Zone(new WorldPoint(2747, 2992, 0), new WorldPoint(2774, 2963, 0));
	}

	private void setupItemRequirements()
	{
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER);

		slicedBanana = new ItemRequirement("Sliced Banana", ItemID.SLICED_BANANA);
		banana = new ItemRequirement("Banana", ItemID.BANANA);
		knife = new ItemRequirement("Knife", ItemID.KNIFE);
		knife.setTooltip("There's one on the counter in the Musa Point general store");
		slicedBananaOrKnife = new ItemRequirements(LogicType.OR, slicedBanana, knife);

		smallFishingNet = new ItemRequirement("Small Fishing Net", ItemID.SMALL_FISHING_NET);
		pestleAndMortar = new ItemRequirement("Pestle And Mortar", ItemID.PESTLE_AND_MORTAR);
		logsForFire = new ItemRequirement("Any logs to make a fire", ItemCollections.getLogsForFire());

		ItemRequirement ironSpear = new ItemRequirement("Iron Spear", ItemID.IRON_SPEAR);
		ItemRequirement steelSpear = new ItemRequirement("Steel Spear", ItemID.STEEL_SPEAR);
		ItemRequirement mithrilSpear = new ItemRequirement("Mithril Spear", ItemID.MITHRIL_SPEAR);
		ItemRequirement adamantSpear = new ItemRequirement("Adamant Spear", ItemID.ADAMANT_SPEAR);
		ItemRequirement runeSpear = new ItemRequirement("Rune Spear", ItemID.RUNE_SPEAR);
		ItemRequirement dragonSpear = new ItemRequirement("Dragon Spear", ItemID.DRAGON_SPEAR);

		spear = new ItemRequirements(LogicType.OR, ironSpear, steelSpear, mithrilSpear, adamantSpear, runeSpear, dragonSpear);

		ItemRequirement ironSpearKp = new ItemRequirement("Iron Spear (kp)", ItemID.IRON_SPEARKP);
		ItemRequirement steelSpearKp = new ItemRequirement("Steel Spear (kp)", ItemID.STEEL_SPEARKP);
		ItemRequirement mithrilSpearKp = new ItemRequirement("Mithril Spear (kp)", ItemID.MITHRIL_SPEARKP);
		ItemRequirement adamantSpearKp = new ItemRequirement("Adamant Spear (kp)", ItemID.ADAMANT_SPEARKP);
		ItemRequirement runeSpearKp = new ItemRequirement("Rune Spear (kp)", ItemID.RUNE_SPEARKP);
		ItemRequirement dragonSpearKp = new ItemRequirement("Dragon Spear (kp)", ItemID.DRAGON_SPEARKP);

		poisonedSpear = new ItemRequirements(LogicType.OR, ironSpearKp, steelSpearKp, mithrilSpearKp, adamantSpearKp, runeSpearKp, dragonSpearKp);

		agilityPotion4 = new ItemRequirement("Agility Potion", ItemID.AGILITY_POTION4);
		rangedOrMagic = new ItemRequirement("Ranged or Magic equipment to kill a level 3 monkey", -1, -1);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);

		jogreBones = new ItemRequirement("Jogre Bones", ItemID.JOGRE_BONES);
		burntJogreBones = new ItemRequirement("Burnt Jogre Bones", ItemID.BURNT_JOGRE_BONES);
		pastyJogreBones = new ItemRequirement("Pasty Jogre Bones", ItemID.PASTY_JOGRE_BONES);
		marinatedJogreBones = new ItemRequirement("Marinated Jogre Bones", ItemID.MARINATED_J_BONES);
		anyJogreBones = new ItemRequirements(LogicType.OR, jogreBones, burntJogreBones, pastyJogreBones);

		seaweed = new ItemRequirement("Seaweed", ItemID.SEAWEED);
		karamjanRum = new ItemRequirement("Karamjan Rum", ItemID.KARAMJAN_RUM);
		karambwanji = new ItemRequirement("Atleast 23 Raw Karambwanji", ItemID.RAW_KARAMBWANJI);
		rawKarambwans = new ItemRequirement("Karambwan", ItemID.RAW_KARAMBWAN);
		coins = new ItemRequirement("Coins", ItemID.COINS_995);

		karambwanVessel = new ItemRequirement("Karambwan Vessel", ItemID.KARAMBWAN_VESSEL);
		karambwanVessel.addAlternates(ItemID.KARAMBWAN_VESSEL_3159);
		filledKarabmwanVessel = new ItemRequirement("Karabmwan Vessel (full)", ItemID.KARAMBWAN_VESSEL_3159);

		bananaSlices = new ItemRequirement("Sliced Banana", ItemID.SLICED_BANANA);
		karamjanRumWithBanana = new ItemRequirement("Karamjan Rum (with banana slices)", ItemID.KARAMJAN_RUM_3164);
		monkeyCorpse = new ItemRequirement("Monkey Corpse", ItemID.MONKEY_CORPSE);
		monkeySkin = new ItemRequirement("Monkey Skin", ItemID.MONKEY_SKIN);
		seaweedSandwich = new ItemRequirement("Seaweed Sandwich", ItemID.SEAWEED_SANDWICH);

		craftingManual = new ItemRequirement("Crafting manual", ItemID.CRAFTING_MANUAL);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(
			coins.quantity(30), hammer, smallFishingNet, pestleAndMortar, spear, agilityPotion4,
			rangedOrMagic, tinderbox, slicedBananaOrKnife, logsForFire
		));
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(new ItemRequirement("Jogre Bones, obtainable during the quest", ItemID.JOGRE_BONE));
		reqs.add(new ItemRequirement("Extra Karambwans in case you burn the one given", ItemID.RAW_KARAMBWAN));
		reqs.add(new ItemRequirement("Any Antipoisons", ItemCollections.getAntipoisons()));
		reqs.add(new ItemRequirement("Stamina potions", ItemCollections.getStaminaPotions()));
		reqs.add(new ItemRequirement("Dramen staff if you have access to fairy rings", ItemID.DRAMEN_STAFF));
		reqs.add(new ItemRequirement("Sliced Banana (Use a knife on a banana)", ItemID.SLICED_BANANA));
		reqs.add(new ItemRequirement("Poison Karambwan", ItemID.POISON_KARAMBWAN));
		reqs.add(new ItemRequirement("Food", -1, -1));
		return reqs;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.JUNGLE_POTION, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.AGILITY, 15, false));
		req.add(new SkillRequirement(Skill.COOKING, 30, false));
		req.add(new SkillRequirement(Skill.FISHING, 5, false));
		return req;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting Off", Collections.singletonList(talkToTimfrakuStart), hammer,
			smallFishingNet, pestleAndMortar, spear, agilityPotion4, rangedOrMagic, tinderbox, slicedBananaOrKnife, logsForFire));
		allSteps.add(new PanelDetails("Gathering quest materials", Arrays.asList(fishKarambwaji, goToLubufu,
			getMoreVessel, fillVessel, getRum, sliceBanana, makeBananaRum), smallFishingNet, slicedBananaOrKnife));
		allSteps.add(new PanelDetails("Helping the three brothers", talkToTiadeche1, giveVessel, getJogreBones,
			getMonkeyCorpse,
			talkToTamayu1, cookKarambwan, cookBones, talkToTamayu2, makeSeaweedSandwich, talkToTinsay, talkToTinsay1, talkToTinsay2,
			talkToTinsay3, goToTiadecheFinal));
		allSteps.add(new PanelDetails("Finishing the quest", goToTimfrakuLadderEnd, talkToTimfrakuEnd));
		return allSteps;
	}
}
