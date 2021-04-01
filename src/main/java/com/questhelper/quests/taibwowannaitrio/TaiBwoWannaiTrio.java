/*
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.TAI_BWO_WANNAI_TRIO
)
public class TaiBwoWannaiTrio extends BasicQuestHelper
{
	ItemRequirement hammer, slicedBanana, banana, knife, slicedBananaOrKnife, smallFishingNet, pestleAndMortar, spear,
		agilityPotion4, rangedOrMagic, tinderbox, jogreBones, burntJogreBones, pastyJogreBones, marinatedJogreBones,
		anyJogreBones, seaweed, karamjanRum, karambwanji, poisonedSpear, logsForFire, rawKarambwans, coins, karambwanVessel,
		karambwanVessel2, filledKarabmwanVessel, filledKarabmwanVessel2, bananaSlices, karamjanRumWithBanana, monkeyCorpse,
		monkeySkin, seaweedSandwich, craftingManual;

	QuestStep goToTimfrakuLadder, talkToTimfrakuStart, fishKarambwaji, goToLubufu, getMoreVessel, fillVessel, getBananaRum,
		makeBananaRum, talkToTiadeche1, getJogreBones, getMonkeyCorpse, talkToTamayu1, cookKarambwan, cookBones,
		talkToTamayu2, makeSeaweedSandwich, talkToTinsay, talkToTinsay1, talkToTinsay2, talkToTinsay3, goToTiadecheFinal,
		defaultStep, goToTimfrakuLadderEnd, talkToTimfrakuEnd;

	Requirement hasKarambwaji, inTimfrakusHut, inLufubuZone, haveOneEmptyVessel, haveTwoVessel, vesselOnGround, filledTwoVessel,
		haveKaramjanRum, haveBananaSlices, haveKaramjanRumWithBanana, haveSeaweed, haveJogreBones, haveMonkeyCorpse,
		haveCookedJogreBones, havePoisonSpear, haveBurntBones, haveMonkeySkin, haveSeaweedSandwich, inCairnIsle, haveCraftingManual;

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

		ConditionalStep findBrothers = new ConditionalStep(this, defaultStep);
		findBrothers.addStep(new Conditions(LogicType.NOR, hasKarambwaji, haveCookedJogreBones, haveCraftingManual, haveCookedJogreBones), fishKarambwaji);
		findBrothers.addStep(new Conditions(LogicType.OR, inLufubuZone, hasKarambwaji), goToLubufu);
		findBrothers.addStep(haveOneEmptyVessel, getMoreVessel);
		findBrothers.addStep(vesselOnGround, getMoreVessel);
		findBrothers.addStep(haveTwoVessel, fillVessel);
		findBrothers.addStep(new Conditions(LogicType.AND, filledTwoVessel, new Conditions(LogicType.NOR, haveKaramjanRumWithBanana, inCairnIsle), new Conditions(LogicType.NAND, haveKaramjanRum, haveBananaSlices)), getBananaRum);
		findBrothers.addStep(new Conditions(LogicType.AND, filledTwoVessel, haveKaramjanRum, haveBananaSlices, new Conditions(LogicType.NOR, haveKaramjanRumWithBanana)), makeBananaRum);
		findBrothers.addStep(new Conditions(LogicType.AND, filledTwoVessel, haveKaramjanRumWithBanana, new Conditions(LogicType.NOR, haveSeaweed, haveSeaweedSandwich)), talkToTiadeche1);
		findBrothers.addStep(new Conditions(LogicType.AND, haveSeaweed, new Conditions(LogicType.NOR, haveSeaweedSandwich), new Conditions(LogicType.NOR, haveJogreBones, haveBurntBones, haveCookedJogreBones)), getJogreBones);
		findBrothers.addStep(new Conditions(LogicType.AND, haveSeaweed, new Conditions(LogicType.NOR, haveMonkeyCorpse, haveMonkeySkin, haveSeaweedSandwich)), getMonkeyCorpse);
		findBrothers.addStep(new Conditions(LogicType.AND, haveSeaweed, haveMonkeyCorpse,  new Conditions(LogicType.NOR, haveBurntBones, haveCookedJogreBones)), talkToTamayu1);
		findBrothers.addStep(new Conditions(LogicType.AND, haveSeaweed, haveMonkeyCorpse, haveBurntBones, new Conditions(LogicType.NOR, havePoisonSpear)), cookKarambwan);
		findBrothers.addStep(new Conditions(LogicType.AND, haveSeaweed, haveMonkeyCorpse, haveBurntBones, havePoisonSpear, new Conditions(LogicType.NOR, haveCookedJogreBones)), cookBones);
		findBrothers.addStep(new Conditions(LogicType.AND, haveSeaweed, haveMonkeyCorpse, haveCookedJogreBones), talkToTamayu2);
		findBrothers.addStep(new Conditions(LogicType.AND, haveSeaweed, haveMonkeySkin, haveKaramjanRumWithBanana), makeSeaweedSandwich);
		findBrothers.addStep(new Conditions(LogicType.AND, haveSeaweedSandwich, haveCookedJogreBones, haveKaramjanRumWithBanana), talkToTinsay);
		findBrothers.addStep(new Conditions(LogicType.AND, inCairnIsle, haveCookedJogreBones, haveSeaweedSandwich, new Conditions(LogicType.NOR, haveKaramjanRumWithBanana)), talkToTinsay1);
		findBrothers.addStep(new Conditions(LogicType.AND, inCairnIsle, haveCookedJogreBones, new Conditions(LogicType.NOR, haveKaramjanRumWithBanana, haveSeaweedSandwich)), talkToTinsay2);
		findBrothers.addStep(new Conditions(LogicType.AND, inCairnIsle, new Conditions(LogicType.NOR, haveCraftingManual, haveKaramjanRumWithBanana, haveSeaweedSandwich, haveCookedJogreBones)), talkToTinsay3);
		findBrothers.addStep(new Conditions(LogicType.AND, haveCraftingManual), goToTiadecheFinal);

		steps.put(3, findBrothers);

		ConditionalStep endQuest = new ConditionalStep(this, goToTimfrakuLadderEnd);
		startQuest.addStep(inTimfrakusHut, talkToTimfrakuEnd);

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
			"Go to Timfraku's house to start the quest");

		talkToTimfrakuStart = new NpcStep(this, NpcID.TIMFRAKU, "Talk to Timfraku to start the quest");
		talkToTimfrakuStart.addDialogStep("Trufitus sent me.");

		defaultStep = new DetailedQuestStep(this, "Hmm, it seems the quest helper has no clue what to do. " +
			"Because this quest is old and the dev had to rely on janky logic it is most likely fixed by opening your quest " +
			"journal and letting the helper do its thing but in the event that does not fix it create an issue on github or ask in the discord channel");

		fishKarambwaji = new NpcStep(this, NpcID.FISHING_SPOT_4710, new WorldPoint(2791,3019,0),
			"Using your small fishing net, catch atleast 23 raw karambwanji just south of Tai Bwo Wannai");

		goToLubufu = new NpcStep(this, NpcID.LUBUFU, lubufuWorldPoint, "Go to Brimhaven and talk to Lubufu." +
			"You have to talk to him multiple times. The dialogue order is as follows: 3, 1-4-2-1-3-4-1, 1, 3-1");
		goToLubufu.addDialogStep("Who are you?");
		goToLubufu.addDialogStep("Talk about him...");
		goToLubufu.addDialogStep("How old are you?");
		goToLubufu.addDialogStep("What do you use for bait?");
		goToLubufu.addDialogStep("What do you do?");
		goToLubufu.addDialogStep("I could help collect the bait.");
		goToLubufu.addDialogStep("You sound like you could do with the help.");
		goToLubufu.addDialogStep("What do you use to catch Karambwan?");
		goToLubufu.addDialogStep("Yes!");

		getMoreVessel = new NpcStep(this, NpcID.LUBUFU, lubufuWorldPoint,
			"Drop the Vessel Lubufu gave you and talk to him go get another, you can get as many as you want but " +
				"you need 2 to complete this quest");
		getMoreVessel.addDialogStep("Actually, I've lost my Karambwan vessel.");
		getMoreVessel.addDialogStep("... a shark ate it!");

		fillVessel = new DetailedQuestStep(this, "Use a karambwanji on a karambwan Vessel to fill it. Note " +
			"if you have less karambwanji than vessel then this will use up all your karambwanji and will require you to " +
			"fish for atleast 1 more to complete the quest", karambwanji, karambwanVessel);

		getBananaRum = new DetailedQuestStep(this, new WorldPoint(2925, 3143, 0), "Go east to Musa point to buy some Karamjan rum, while you are there grab a banana if you do not have one and use your knife on the banana to make banana slices");
		makeBananaRum = new ItemStep(this, "Add banana slices to the karamjan rum to make Karamjan rum with banana slices", bananaSlices, karamjanRum);

		talkToTiadeche1 = new NpcStep(this, NpcID.TIADECHE, new WorldPoint(2912, 2116, 0), "Go to Tiadeche, he is located near the fairy ring DKP, after talking to him use one of your filled karambwan vessel on him. Grab a seaweed after you are done.");
		talkToTiadeche1.addDialogStep(3, "Is there anything I can do to help?");

		getJogreBones = new DetailedQuestStep(this, new WorldPoint(2925, 3062, 0), "If you do not have a Jogre bone yet there are Jogre south and you should kill one now to get its bones.");
		getMonkeyCorpse = new DetailedQuestStep(this, "Kill a monkey to get its corpse, there are plenty of monkeys around Karamja and you will need either magic or range to kill one");

		talkToTamayu1 = new NpcStep(this, NpcID.TAMAYU, new WorldPoint(2845, 3041, 0), "Talk to Tamayu, he is located north of Shilo Village near the mining sign on the map, after watching him use your tinder box on your jogre bones and pick up the burnt jogre bones.");
		talkToTamayu1.addDialogStep("When will you succeed?");
		talkToTamayu1.addDialogStep("Yes");

		cookKarambwan = new DetailedQuestStep(this, "Light a fire using your tinderbox and logs and cook your karambwan (If it is burnt you will need to either buy another one or fish it with 65 fishing). Use your poison karambwan on your pestel and mortar to make a poison paste and use that on your spear.");
		cookBones = new DetailedQuestStep(this, "Use a karambwanji on your pestle and mortar to make a karambwanji paste, use that paste on your burnt jogre bones and cook the outcome again on the fire");
		talkToTamayu2 = new NpcStep(this, NpcID.TAMAYU, new WorldPoint(2845, 3041, 0), "Use your 4 dose agility potion and poisoned spear on Tamayu then talk to him. After the cutscene use your monkey corpse on him to have him skin it for you");
		talkToTamayu2.addDialogStep("Take me on your next hunt for the Shaikahan.");

		makeSeaweedSandwich = new DetailedQuestStep(this, "Use your seaweed on your monkey skin to make a seaweed sandwich");

		talkToTinsay = new NpcStep(this, NpcID.TINSAY, new WorldPoint(2764, 2975, 0), "Talk to the third brother, he is located on Carin Isle west of Shilo Village, south of fairy ring CKR. You will need some food if your agility level is low. After talking to him use your bottle of rum with banana slices on him");
		talkToTinsay1 = new NpcStep(this, NpcID.TINSAY, new WorldPoint(2764, 2975, 0), "Finish the conversation and use your seaweed sandwich on him. Be careful to right click on the seaweed sandwich as the default option is to eat it.");
		talkToTinsay2 = new NpcStep(this, NpcID.TINSAY, new WorldPoint(2764, 2975, 0), "Finish the conversation and use your marinated j'bones on him. Be careful to right click on your marinated bones as the default option is the bury it");
		talkToTinsay3 = new NpcStep(this, NpcID.TINSAY, new WorldPoint(2764, 2975, 0), "Use a karambwan vessel on Tinsay to get a crafting manual.");

		goToTiadecheFinal = new NpcStep(this, NpcID.TIADECHE, new WorldPoint(2912, 2116, 0), "Go to back to Tiadeche located north of fairy ring DKP and use the crafting manual on him");

		goToTimfrakuLadderEnd = new ObjectStep(this, ObjectID.LADDER_16683, timfrakuHutWorldPoint,
			"Go to Timfraku's house to finish the quest");
		talkToTimfrakuEnd = new NpcStep(this, NpcID.TIMFRAKU, "Talk to Timfraku to end the quest, the option chosen here does not matter. NOTE: you will need to talk to each of the brothers individually to receive experience rewards as well as the ability to cook karambwans properly");
	}


	private void setupConditions()
	{
		inTimfrakusHut = new ZoneRequirement(timfrakusHut);
		hasKarambwaji = new ItemRequirements(karambwanji);
		inLufubuZone = new ZoneRequirement(lubufuZone);
		haveTwoVessel = new ItemRequirements(LogicType.OR, karambwanVessel2, new ItemRequirements(karambwanVessel, filledKarabmwanVessel));
		haveOneEmptyVessel = new ItemRequirements(karambwanVessel);
		vesselOnGround = new ItemOnTileRequirement(karambwanVessel);
		filledTwoVessel = new ItemRequirements(filledKarabmwanVessel2);
		haveKaramjanRum = new ItemRequirements(karamjanRum);
		haveBananaSlices = new ItemRequirements(bananaSlices);
		haveKaramjanRumWithBanana = new ItemRequirements(karamjanRumWithBanana);
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
		karambwanji = new ItemRequirement("Atleast 23 Raw Karambwanji", ItemID.RAW_KARAMBWANJI, 23);
		rawKarambwans = new ItemRequirement("Karambwan", ItemID.RAW_KARAMBWAN);
		coins = new ItemRequirement("30 GP", ItemID.COINS_995, 30);

		karambwanVessel = new ItemRequirement("Karambwan Vessel", ItemID.KARAMBWAN_VESSEL);
		karambwanVessel2 = new ItemRequirement("Karambwan Vessel", ItemID.KARAMBWAN_VESSEL, 2);
		filledKarabmwanVessel = new ItemRequirement("Karabmwan Vessel (full)", ItemID.KARAMBWAN_VESSEL_3159);
		filledKarabmwanVessel2 = new ItemRequirement("Karabmwan Vessel (full)", ItemID.KARAMBWAN_VESSEL_3159, 2);

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
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(hammer);
		reqs.add(smallFishingNet);
		reqs.add(pestleAndMortar);
		reqs.add(spear);
		reqs.add(agilityPotion4);
		reqs.add(rangedOrMagic);
		reqs.add(tinderbox);
		reqs.add(slicedBananaOrKnife);
		reqs.add(logsForFire);

		return reqs;
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
		allSteps.add(new PanelDetails("Starting Off", goToTimfrakuLadder));

		return allSteps;
	}
}
