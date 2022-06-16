/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.quests.thegreatbrainrobbery;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarbits;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.InInstanceRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_GREAT_BRAIN_ROBBERY
)
public class TheGreatBrainRobbery extends BasicQuestHelper
{
	// Required
	ItemRequirement fishbowlHelmet, divingApparatus, woodenCats, oakPlank, saw, plank, fur, hammer, nails,
		holySymbol, ringOfCharos, catsOrResources, tinderbox;

	// Recommended
	ItemRequirement ectophial, edgevilleTeleport, fenkenstrainTeleport, watermelonSeeds, combatGearForSafespotting,
		food, prayerPotions;

	// Quest items
	ItemRequirement prayerBook, wolfWhistle, shippingOrder, cratePart, keg, fuse, cranialClamp, brainTongs, bellJars,
		skullStaples, neededJars, neededStaples, anchor;

	Zone harmony, waterEntrance, water, waterExit, peepRoom, fenkF2, fenkF1, harmonyBasement, boat, boatToMos, mos;

	Requirement inHarmony, inWaterEntrance, inWater, inWaterExit, inPeepRoom, inFenkF2, inFenkF1, inHarmonyBasement,
		onBoat, repairedStairs,	hasReadPrayerBook, talkedToFenk, talkedToRufus, madeCrateWalls, madeCrateBottom,
		addedCats, addedCatsOrHas10, fenkInCrate, placedKeg, addedFuse, litFuse, churchDoorGone, hasKeg, hasFuse,
		hasTinderbox, givenClamp, givenStaples, givenBells, givenTongs, hadClamp, hadStaples, hadBells, hadTongs,
		givenHammer, barrelchestAppeared, inBoatToMos, inMos;

	QuestStep talkToTranquility, talkToTranquilityOnIsland, pullStatue, enterWater, repairWaterStairs,
		climbFromWater, climbFromWaterCaveToPeep, peerThroughHole, goFromHoleToWater, leaveWaterBack,
		enterWaterReturn, leaveWaterEntranceReturn, talkToTranquilityAfterPeeping, talkToTranquilityMosAfterPeeping,
		moveToMos, moveToCapt;

	QuestStep searchBookcase, readBook, returnToTranquility, recitePrayer, returnToHarmonyAfterPrayer,
		talkToTranquilityAfterPrayer;

	QuestStep goToF1Fenk, goToF2Fenk, talkToFenk, talkToRufus, makeOrGetWoodenCats, goToF1FenkForCrate,
		goToF2FenkForCrate, buildCrate, addBottomToCrate, fillCrate, blowWhistle, goF1WithOrder, goF2WithOrder,
		putOrderOnCrate;

	QuestStep goToHarmonyAfterFenk, goDownToFenk, talkToFenkOnHarmony, leaveWindmillBasement, goToHarmonyForBrainItems,
		getFuse, climbShipLadder, getTinderbox, getKeg, climbDownFromShip, useKegOnDoor, useFuseOnDoor, lightFuse,
		killSorebones,  goBackDownToFenk, talkToFenkWithItems, goUpFromFenkAfterItems, talkToTranquilityAfterHelping;

	QuestStep enterChurchForFight, confrontMigor, defeatBarrelchest, pickupAnchor, talkToTranquilityToFinish;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep goStart = new ConditionalStep(this, moveToCapt);
		goStart.addStep(inBoatToMos, moveToMos);
		goStart.addStep(inMos, talkToTranquility);
		goStart.addStep(inHarmony, talkToTranquilityOnIsland);
		steps.put(0, goStart);

		ConditionalStep goPeep = new ConditionalStep(this, talkToTranquility);
		goPeep.addStep(inPeepRoom, peerThroughHole);
		goPeep.addStep(inWaterExit, climbFromWaterCaveToPeep);
		goPeep.addStep(new Conditions(inWater, repairedStairs), climbFromWater);
		goPeep.addStep(inWater, repairWaterStairs);
		goPeep.addStep(inWaterEntrance, enterWater);
		goPeep.addStep(inHarmony, pullStatue);
		steps.put(10, goPeep);

		ConditionalStep returnAfterPeep = new ConditionalStep(this, talkToTranquilityMosAfterPeeping);
		returnAfterPeep.addStep(inPeepRoom, goFromHoleToWater);
		returnAfterPeep.addStep(inWaterExit, enterWaterReturn);
		returnAfterPeep.addStep(inWater, leaveWaterEntranceReturn);
		returnAfterPeep.addStep(inWaterEntrance, leaveWaterBack);
		returnAfterPeep.addStep(inHarmony, talkToTranquilityAfterPeeping);
		steps.put(20, returnAfterPeep);

		ConditionalStep goGetBook = new ConditionalStep(this, searchBookcase);
		goGetBook.addStep(new Conditions(prayerBook.alsoCheckBank(questBank), hasReadPrayerBook, inHarmony),
			recitePrayer);
		goGetBook.addStep(new Conditions(prayerBook.alsoCheckBank(questBank), hasReadPrayerBook), returnToTranquility);
		goGetBook.addStep(prayerBook.alsoCheckBank(questBank), readBook);
		steps.put(30, goGetBook);
		steps.put(40, goGetBook);

		ConditionalStep goAfterPrayer = new ConditionalStep(this, returnToHarmonyAfterPrayer);
		goAfterPrayer.addStep(inHarmony, talkToTranquilityAfterPrayer);
		steps.put(50, goAfterPrayer);

		ConditionalStep goSortCrate = new ConditionalStep(this, makeOrGetWoodenCats);
		goSortCrate.addStep(new Conditions(inFenkF2, addedCats), blowWhistle);
		goSortCrate.addStep(new Conditions(addedCatsOrHas10, inFenkF2, madeCrateBottom), fillCrate);
		goSortCrate.addStep(new Conditions(addedCatsOrHas10, inFenkF2, madeCrateWalls), addBottomToCrate);
		goSortCrate.addStep(new Conditions(addedCatsOrHas10, inFenkF2), buildCrate);
		goSortCrate.addStep(new Conditions(addedCatsOrHas10, inFenkF1), goToF2FenkForCrate);
		goSortCrate.addStep(new Conditions(addedCatsOrHas10), goToF1FenkForCrate);

		ConditionalStep goTalkFenk = new ConditionalStep(this, goToF1Fenk);
		goTalkFenk.addStep(new Conditions(fenkInCrate, inFenkF2), putOrderOnCrate);
		goTalkFenk.addStep(new Conditions(fenkInCrate, inFenkF1), goF2WithOrder);
		goTalkFenk.addStep(fenkInCrate, goF1WithOrder);
		goTalkFenk.addStep(talkedToRufus, goSortCrate);
		goTalkFenk.addStep(talkedToFenk, talkToRufus);
		goTalkFenk.addStep(inFenkF1, goToF2Fenk);
		goTalkFenk.addStep(inFenkF2, talkToFenk);
		steps.put(60, goTalkFenk);

		ConditionalStep goTalkFenkHarmony = new ConditionalStep(this, goToHarmonyAfterFenk);
		goTalkFenkHarmony.addStep(inHarmonyBasement, talkToFenkOnHarmony);
		goTalkFenkHarmony.addStep(inHarmony, goDownToFenk);
		steps.put(70, goTalkFenkHarmony);

		ConditionalStep goBlowTheDoor = new ConditionalStep(this, goToHarmonyForBrainItems);
		goBlowTheDoor.addStep(new Conditions(inHarmonyBasement, hadClamp, hadTongs, hadBells,
			hadStaples), talkToFenkWithItems);
		goBlowTheDoor.addStep(new Conditions(hadClamp, hadTongs, hadBells, hadStaples), goBackDownToFenk);
		goBlowTheDoor.addStep(new Conditions(onBoat, hasFuse, hasTinderbox, hasKeg), climbDownFromShip);
		goBlowTheDoor.addStep(new Conditions(inHarmony, churchDoorGone), killSorebones);
		goBlowTheDoor.addStep(new Conditions(inHarmony, addedFuse, hasTinderbox), lightFuse);
		goBlowTheDoor.addStep(new Conditions(inHarmony, hasFuse, hasTinderbox, placedKeg), useFuseOnDoor);
		goBlowTheDoor.addStep(new Conditions(inHarmony, hasFuse, hasTinderbox, hasKeg), useKegOnDoor);
		goBlowTheDoor.addStep(new Conditions(onBoat, hasFuse, hasTinderbox), getKeg);
		goBlowTheDoor.addStep(new Conditions(onBoat, hasFuse), getTinderbox);
		goBlowTheDoor.addStep(new Conditions(inHarmony, hasFuse), climbShipLadder);
		goBlowTheDoor.addStep(onBoat, climbDownFromShip);
		goBlowTheDoor.addStep(inHarmony, getFuse);
		steps.put(80, goBlowTheDoor);
		steps.put(90, goBlowTheDoor);

		ConditionalStep goTalkToTranqAfterHelping = new ConditionalStep(this, talkToTranquilityAfterHelping);
		goTalkToTranqAfterHelping.addStep(inHarmonyBasement, goUpFromFenkAfterItems);
		steps.put(100, goTalkToTranqAfterHelping);

		ConditionalStep goConfront = new ConditionalStep(this, enterChurchForFight);
		goConfront.addStep(new Conditions(inHarmony, new InInstanceRequirement(), barrelchestAppeared), defeatBarrelchest);
		goConfront.addStep(new Conditions(inHarmony, new InInstanceRequirement()), confrontMigor);
		steps.put(110, goConfront);

		ConditionalStep goFinish = new ConditionalStep(this, talkToTranquilityToFinish);
		goFinish.addStep(new ItemOnTileRequirement(anchor), pickupAnchor);
		steps.put(120, goFinish);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		// Item reqs
		fishbowlHelmet = new ItemRequirement("Fishbowl helmet", ItemID.FISHBOWL_HELMET);
		fishbowlHelmet.setTooltip("You can get another from Murphy in Port Khazard");
		divingApparatus = new ItemRequirement("Diving apparatus", ItemID.DIVING_APPARATUS);
		divingApparatus.setTooltip("You can get another from Murphy in Port Khazard");
		woodenCats = new ItemRequirement("Wooden cat", ItemID.WOODEN_CAT);
		oakPlank = new ItemRequirement("Oak plank", ItemID.OAK_PLANK);
		saw = new ItemRequirement("Saw", ItemCollections.SAW);
		plank = new ItemRequirement("Plank", ItemID.PLANK);
		fur = new ItemRequirement("Fur", ItemID.FUR);
		fur.addAlternates(ItemID.BEAR_FUR, ItemID.GREY_WOLF_FUR);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER);
		hammer.setTooltip("a standard hammer, NOT Imcando Hammer, as it will be given to Dr. Fenkenstrain");
		nails = new ItemRequirement("Nails", ItemCollections.NAILS);
		holySymbol = new ItemRequirement("Holy symbol", ItemID.HOLY_SYMBOL);
		ringOfCharos = new ItemRequirement("Ring of Charos", ItemID.RING_OF_CHAROS);
		ringOfCharos.addAlternates(ItemID.RING_OF_CHAROSA);
		ringOfCharos.setDisplayMatchedItemName(true);
		catsOrResources = new ItemRequirements(LogicType.OR, "10 Wooden cats, or 10 planks and 10 furs to make them",
			woodenCats.quantity(10), new ItemRequirements(plank.quantity(10), fur.quantity(10)));
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		tinderbox.addAlternates(ItemID.TINDERBOX_7156);

		// Item recommended
		ectophial = new ItemRequirement("Ectophial", ItemID.ECTOPHIAL);
		edgevilleTeleport = new ItemRequirement("Monastery teleport", ItemCollections.COMBAT_BRACELETS);
		edgevilleTeleport.addAlternates(ItemCollections.AMULET_OF_GLORIES);
		fenkenstrainTeleport = new ItemRequirement("Fenkenstrain's Castle teleport", ItemID.FENKENSTRAINS_CASTLE_TELEPORT);
		watermelonSeeds = new ItemRequirement("Watermelon seeds to plant on Harmony for Hard Morytania Diary",
			ItemID.WATERMELON_SEED);
		combatGearForSafespotting = new ItemRequirement("Combat gear for safespotting", -1, -1);
		combatGearForSafespotting.setDisplayItemId(BankSlotIcons.getMagicCombatGear());
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD);
		prayerPotions = new ItemRequirement("Prayer potion", ItemCollections.PRAYER_POTIONS);

		// Quest items
		prayerBook = new ItemRequirement("Prayer book", ItemID.PRAYER_BOOK);
		prayerBook.addAlternates(ItemID.PRAYER_BOOK_10890);
		wolfWhistle = new ItemRequirement("Wolf whistle", ItemID.WOLF_WHISTLE);
		wolfWhistle.setTooltip("You can get another from Rufus in Canifis");
		shippingOrder = new ItemRequirement("Shipping order", ItemID.SHIPPING_ORDER);
		shippingOrder.setTooltip("You can get another from Rufus in Canifis");
		cratePart = new ItemRequirement("Crate part", ItemID.CRATE_PART);
		cratePart.setTooltip("You can get more from Rufus in Canifis");
		keg = new ItemRequirement("Keg", ItemID.KEG_10898);
		keg.addAlternates(ItemID.KEG);
		fuse = new ItemRequirement("Fuse", ItemID.FUSE_10884);
		cranialClamp = new ItemRequirement("Cranial clamp", ItemID.CRANIAL_CLAMP);
		brainTongs = new ItemRequirement("Brain tongs", ItemID.BRAIN_TONGS); // 10894
		bellJars = new ItemRequirement("Bell jar", ItemID.BELL_JAR);
		skullStaples = new ItemRequirement("Skull staple", ItemID.SKULL_STAPLE);
		anchor = new ItemRequirement("Barrelchest anchor", ItemID.BARRELCHEST_ANCHOR_10888);

		neededJars = bellJars.quantity(3 - client.getVarbitValue(3399));
		neededStaples = skullStaples.quantity(30 - client.getVarbitValue(3400));
	}

	public void setupZones()
	{
		harmony = new Zone(new WorldPoint(3771, 2813, 0), new WorldPoint(3840, 2881, 3));
		waterEntrance = new Zone(new WorldPoint(3782, 9250, 0), new WorldPoint(3795, 9259, 0));
		water = new Zone(new WorldPoint(3777, 9242, 1), new WorldPoint(3835, 9267, 1));
		waterExit = new Zone(new WorldPoint(3822, 9251, 0), new WorldPoint(3833, 9258, 0));
		peepRoom = new Zone(new WorldPoint(3817, 2842, 0), new WorldPoint(3821, 2846, 0));
		fenkF1 = new Zone(new WorldPoint(3526, 3574, 1), new WorldPoint(3566, 3531, 1));
		fenkF2 = new Zone(new WorldPoint(3544, 3558, 2), new WorldPoint(3553, 3551, 2));
		harmonyBasement = new Zone(new WorldPoint(3778, 9219, 0), new WorldPoint(3792, 9232, 0));
		boat = new Zone(new WorldPoint(3790, 2870, 1), new WorldPoint(3810, 2876, 2));
		boatToMos = new Zone(new WorldPoint(3709, 3508, 1), new WorldPoint(3721, 3489, 1));
		mos = new Zone(new WorldPoint(3642, 3077, 0), new WorldPoint(3855, 2924, 1));
	}

	public void setupConditions()
	{
		inHarmony = new ZoneRequirement(harmony);
		inWater = new ZoneRequirement(water);
		inWaterEntrance = new ZoneRequirement(waterEntrance);
		inWaterExit = new ZoneRequirement(waterExit);
		inPeepRoom = new ZoneRequirement(peepRoom);
		inFenkF2 = new ZoneRequirement(fenkF2);
		inFenkF1 = new ZoneRequirement(fenkF1);
		inHarmonyBasement = new ZoneRequirement(harmonyBasement);
		onBoat = new ZoneRequirement(boat);
		inBoatToMos = new ZoneRequirement(boatToMos);
		inMos = new ZoneRequirement(mos);
		// 3383 started talking to tranq =1, accepted =2

		// Pulling statue, 3401 =1, statue pulled, 3401 = 2
		// 3384, 0->1, entered statue

		repairedStairs = new VarbitRequirement(3385, 1);
		hasReadPrayerBook = new VarbitRequirement(3386, 1);

		// 3387 = 1, talked a bit to Tranq in Mos Le after getting the Prayer book
		// 3388 = 1, part way through Fenk convo
		talkedToFenk = new VarbitRequirement(3388, 2, Operation.GREATER_EQUAL);
		talkedToRufus = new VarbitRequirement(3388, 3, Operation.GREATER_EQUAL);
		// 3390 = 1, talked to Rufus. Probably construction spot appears

		madeCrateWalls = new VarbitRequirement(3390, 2, Operation.GREATER_EQUAL);
		madeCrateBottom = new VarbitRequirement(3390, 3, Operation.GREATER_EQUAL);
		addedCats = new VarbitRequirement(3390, 4, Operation.GREATER_EQUAL);
		fenkInCrate = new VarbitRequirement(3390, 5, Operation.GREATER_EQUAL);

		addedCatsOrHas10 = new Conditions(LogicType.OR, addedCats, woodenCats.quantity(10).alsoCheckBank(questBank));

		// 3392 0->10, added wooden cats


		placedKeg = new VarbitRequirement(3393, 2, Operation.GREATER_EQUAL);
		addedFuse = new VarbitRequirement(3393, 3, Operation.GREATER_EQUAL);
		litFuse = new VarbitRequirement(3393, 4, Operation.GREATER_EQUAL);
		churchDoorGone  = new VarbitRequirement(3393, 5, Operation.GREATER_EQUAL);

		hasKeg = new Conditions(LogicType.OR, keg, placedKeg);
		hasFuse = new Conditions(LogicType.OR, fuse, addedFuse);
		hasTinderbox = new Conditions(LogicType.OR, tinderbox, litFuse);

		givenClamp = new VarbitRequirement(3396, 1);
		givenTongs = new VarbitRequirement(3397, 1);
		givenHammer = new VarbitRequirement(3398, 1);
		givenBells = new VarbitRequirement(3399, 3);
		givenStaples = new VarbitRequirement(3400, 30);

		hadClamp = new Conditions(LogicType.OR, givenClamp, cranialClamp);
		hadStaples = new Conditions(LogicType.OR, givenStaples, neededStaples);
		hadBells = new Conditions(LogicType.OR, givenBells, neededJars);
		hadTongs = new Conditions(LogicType.OR, givenTongs, brainTongs);

		barrelchestAppeared = new VarbitRequirement(3410, 1);
		// received blessing, 3411 = 1
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		int staplesNeeded = 30;
		int bellsNeeded = 3;
		neededStaples.quantity(staplesNeeded - client.getVarbitValue(3400));
		neededJars.quantity(bellsNeeded - client.getVarbitValue(3399));
	}

	public void setupSteps()
	{
		moveToCapt = new ObjectStep(this, ObjectID.GANGPLANK_11209, new WorldPoint(3710, 3496, 0),
			"Cross the gangplank to Bill Teach's ship.");
		moveToMos = new NpcStep(this, NpcID.BILL_TEACH_4016, new WorldPoint(3714, 3497, 1),
			"Talk to Bill Teach to travel to Mos Le'Harmless.");
		talkToTranquility = new NpcStep(this, NpcID.BROTHER_TRANQUILITY, new WorldPoint(3681, 2963, 0),
			"Talk to Brother Tranquility on Mos Le'Harmless.");
		talkToTranquility.addDialogStep("Undead pirates? Let me at 'em!");

		talkToTranquilityOnIsland = new NpcStep(this, NpcID.BROTHER_TRANQUILITY, new WorldPoint(3787, 2825,
			0), "Talk to Brother Tranquility on Harmony.");
		talkToTranquility.addSubSteps(talkToTranquilityOnIsland);

		ItemRequirement conditionalPlanks = plank.quantity(4).hideConditioned(repairedStairs);
		ItemRequirement conditionalNails = nails.quantity(60).hideConditioned(repairedStairs);
		ItemRequirement conditionalHammer = hammer.hideConditioned(repairedStairs);
		pullStatue = new ObjectStep(this, NullObjectID.NULL_22355, new WorldPoint(3794, 2844, 0),
			"Pull the saradomin statue on Harmony, then enter it.", fishbowlHelmet.equipped(),
			divingApparatus.equipped(), conditionalHammer, conditionalPlanks, conditionalNails);
		enterWater = new ObjectStep(this, ObjectID.STAIRS_22365, new WorldPoint(3788, 9254, 0),
			"Enter the water.");
		repairWaterStairs = new ObjectStep(this, NullObjectID.NULL_22370, new WorldPoint(3829, 9254, 1),
			"Repair the stairs to the east.", hammer, plank.quantity(4), nails.quantity(100));
		climbFromWater = new ObjectStep(this, NullObjectID.NULL_22370, new WorldPoint(3829, 9254, 1),
			"Climb up the stairs to the east.");
		climbFromWaterCaveToPeep = new ObjectStep(this, ObjectID.LADDER_22372, new WorldPoint(3831, 9254, 0),
			"Climb up the ladder.");
		peerThroughHole = new ObjectStep(this, ObjectID.PEEPHOLE, new WorldPoint(3817, 2844, 0),
			"Peer through the nearby peephole.");
		goFromHoleToWater = new ObjectStep(this, ObjectID.LADDER_22164, new WorldPoint(3819, 2843, 0),
			"Return to Brother Tranquility on Harmony.");
		enterWaterReturn = new ObjectStep(this, ObjectID.STAIRS_22366, new WorldPoint(3827, 9254, 0),
			"Return to Brother Tranquility on Harmony.");
		leaveWaterEntranceReturn = new ObjectStep(this, ObjectID.STAIRS_22367, new WorldPoint(3785, 9254, 1),
			"Return to Brother Tranquility on Harmony.");
		leaveWaterBack = new ObjectStep(this, ObjectID.LADDER_22371, new WorldPoint(3784, 9254, 0),
			"Return to Brother Tranquility on Harmony.");

		talkToTranquilityMosAfterPeeping = new NpcStep(this, NpcID.BROTHER_TRANQUILITY, new WorldPoint(3681, 2963, 0),
			"Return to Brother Tranquility on Harmony.");
		talkToTranquilityMosAfterPeeping.addDialogStep("Yes, please.");

		talkToTranquilityAfterPeeping = new NpcStep(this, NpcID.BROTHER_TRANQUILITY, new WorldPoint(3787, 2825,
			0), "Return to Brother Tranquility.");
		talkToTranquilityAfterPeeping.addSubSteps(goFromHoleToWater, enterWaterReturn, leaveWaterEntranceReturn,
			leaveWaterBack, talkToTranquilityMosAfterPeeping);

		searchBookcase = new ObjectStep(this, ObjectID.BOOKCASE_380, new WorldPoint(3049, 3484, 0),
			"Search the south west bookcase in the Edgeville Monastery.");
		readBook = new DetailedQuestStep(this, "Read the prayer book.", prayerBook.highlighted());
		returnToTranquility = new NpcStep(this, NpcID.BROTHER_TRANQUILITY, new WorldPoint(3681, 2963, 0),
			"Return to Harmony.", prayerBook, holySymbol.equipped());
		recitePrayer = new DetailedQuestStep(this, new WorldPoint(3787, 2825,
			0), "Right-click recite the prayer book on Harmony.", prayerBook.highlighted(), holySymbol.equipped());


		returnToHarmonyAfterPrayer = new NpcStep(this, NpcID.BROTHER_TRANQUILITY, new WorldPoint(3681, 2963, 0),
			"Return to Brother Tranquility on Harmony.");
		returnToHarmonyAfterPrayer.addDialogStep("Yes, please.");
		talkToTranquilityAfterPrayer = new NpcStep(this, NpcID.BROTHER_TRANQUILITY, new WorldPoint(3787, 2825,
			0), "Talk to Brother Tranquility again.");
		talkToTranquilityAfterPrayer.addSubSteps(returnToHarmonyAfterPrayer);

		goToF1Fenk = new ObjectStep(this, ObjectID.STAIRCASE_5206, new WorldPoint(3538, 3552, 0),
			"Go up to the second floor of Fenkenstrain's Castle and talk to Dr. Fenkenstrain.");
		goToF1Fenk.addDialogStep("Yes, please.");
		goToF2Fenk = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(3548, 3554, 1),
			"Go up to the second floor of Fenkenstrain's Castle and talk to Dr. Fenkenstrain.");
		talkToFenk = new NpcStep(this, NpcID.DR_FENKENSTRAIN, new WorldPoint(3548, 3554, 2),
			"Go up to the second floor of Fenkenstrain's Castle and talk to Dr. Fenkenstrain.");
		talkToFenk.addSubSteps(goToF1Fenk, goToF2Fenk);
		talkToRufus = new NpcStep(this, NpcID.RUFUS_6478, new WorldPoint(3507, 3494, 0),
			"Talk to Rufus in Canifis' food store.", ringOfCharos.equipped());
		talkToRufus.addDialogStep("Talk about the meat shipment");
		makeOrGetWoodenCats = new DetailedQuestStep(this,
			"Either buy 10 wooden cats from the G.E., or make them on a clockmaker's bench in your POH.",
			catsOrResources);
		makeOrGetWoodenCats.addDialogStep("Wooden cat");
		goToF1FenkForCrate = new ObjectStep(this, ObjectID.STAIRCASE_5206, new WorldPoint(3538, 3552, 0),
			"Return to Dr. Fenkenstrain.", ringOfCharos.equipped(),
			plank.quantity(4).hideConditioned(madeCrateBottom), nails.quantity(100).hideConditioned(madeCrateBottom),
			hammer.hideConditioned(madeCrateBottom), woodenCats.quantity(10).hideConditioned(addedCats),
			cratePart.quantity(6).hideConditioned(madeCrateWalls), wolfWhistle);
		goToF2FenkForCrate = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(3548, 3554, 1),
			"Return to Dr. Fenkenstrain.", ringOfCharos.equipped(),
			plank.quantity(4).hideConditioned(madeCrateBottom), nails.quantity(100).hideConditioned(madeCrateBottom),
			hammer.hideConditioned(madeCrateBottom), woodenCats.quantity(10).hideConditioned(addedCats),
			cratePart.quantity(6).hideConditioned(madeCrateWalls), wolfWhistle);
		buildCrate = new ObjectStep(this, NullObjectID.NULL_22489,  new WorldPoint(3550, 3554, 2),
			"Return to Dr. Fenkenstrain and build the crate next to him.", ringOfCharos.equipped(), plank.quantity(4), nails.quantity(100),
			hammer, woodenCats.quantity(10), cratePart.quantity(6), wolfWhistle);
		buildCrate.addSubSteps(goToF1FenkForCrate, goToF2FenkForCrate);

		addBottomToCrate = new ObjectStep(this, NullObjectID.NULL_22489,  new WorldPoint(3550, 3554, 2),
			"Add a bottom to the crate.", plank.quantity(4), nails.quantity(100), hammer);

		fillCrate = new ObjectStep(this, NullObjectID.NULL_22489,  new WorldPoint(3550, 3554, 2),
			"Fill the crate next to Fenkenstrain with wooden cats.", woodenCats.quantity(10).highlighted());
		fillCrate.addIcon(ItemID.WOODEN_CAT);

		blowWhistle = new DetailedQuestStep(this, "Blow the wolf whistle.", wolfWhistle.highlighted(), ringOfCharos.equipped());

		goF1WithOrder = new ObjectStep(this, ObjectID.STAIRCASE_5206, new WorldPoint(3538, 3552, 0),
			"Return to Dr. Fenkenstrain and place the shipping order on the crate.", shippingOrder);
		goF2WithOrder = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(3548, 3554, 1),
			"Return to Dr. Fenkenstrain and place the shipping order on the crate.", shippingOrder);
		putOrderOnCrate = new ObjectStep(this, NullObjectID.NULL_22489,  new WorldPoint(3550, 3554, 2),
			"Return to Dr. Fenkenstrain and place the shipping order on the crate..", shippingOrder.highlighted());
		putOrderOnCrate.addIcon(ItemID.SHIPPING_ORDER);
		putOrderOnCrate.addSubSteps(goF1WithOrder, goF2WithOrder);

		goToHarmonyAfterFenk = new NpcStep(this, NpcID.BROTHER_TRANQUILITY, new WorldPoint(3681, 2963, 0),
			"Return to Harmony and talk to Fenkenstrain in the windmill's basement.");
		goDownToFenk = new ObjectStep(this, ObjectID.LADDER_22173, new WorldPoint(3789, 2826, 0),
			"Talk to Dr. Fenkenstrain in the Harmony Windmill basement.");
		talkToFenkOnHarmony = new NpcStep(this, NpcID.DR_FENKENSTRAIN, new WorldPoint(3785, 9225, 0),
			"Talk to Dr. Fenkenstrain in the Harmony Windmill basement.");
		talkToFenkOnHarmony.addSubSteps(goToHarmonyAfterFenk, goDownToFenk);
		leaveWindmillBasement = new ObjectStep(this, ObjectID.LADDER_22172, new WorldPoint(3789, 9226, 0),
			"Leave the basement.");

		goToHarmonyForBrainItems = new NpcStep(this, NpcID.BROTHER_TRANQUILITY, new WorldPoint(3681, 2963, 0),
			"Return to Harmony.");
		getFuse = new ObjectStep(this, ObjectID.LOCKER_22298, new WorldPoint(3791, 2873, 0),
			"Search the locker on the ship on the north of Harmony.", fishbowlHelmet.equipped(), divingApparatus.equipped());
		((ObjectStep) getFuse).addAlternateObjects(ObjectID.LOCKER_22299);
		getFuse.addSubSteps(goToHarmonyForBrainItems);
		climbShipLadder = new ObjectStep(this, ObjectID.LADDER_22274, new WorldPoint(3802, 2873, 0),
			"Climb the ship's ladder.");
		getTinderbox = new ItemStep(this, "Take a tinderbox.", tinderbox);
		getKeg = new ItemStep(this, "Take a keg.", keg);
		climbDownFromShip = new ObjectStep(this, ObjectID.LADDER_22275, new WorldPoint(3802, 2873, 1),
			"Climb down the ship's ladder.");
		useKegOnDoor = new ObjectStep(this, NullObjectID.NULL_22119, new WorldPoint(3805, 2844, 0),
			"Use the keg on the church's door.", keg.highlighted());
		useKegOnDoor.addIcon(ItemID.KEG_10898);
		useFuseOnDoor = new ObjectStep(this, NullObjectID.NULL_22119, new WorldPoint(3805, 2844, 0),
			"Use the fuse on the keg.", fuse.highlighted());
		useFuseOnDoor.addIcon(ItemID.FUSE_10884);
		lightFuse = new ObjectStep(this, NullObjectID.NULL_22492, new WorldPoint(3803, 2844, 0),
			"Light the fuse.", tinderbox.highlighted());
		lightFuse.addIcon(ItemID.TINDERBOX);
		killSorebones = new NpcStep(this, NpcID.SOREBONES, new WorldPoint(3815, 2843, 0),
			"Kill sorebones in the church for items.", true,
			cranialClamp.hideConditioned(givenClamp), brainTongs.hideConditioned(givenTongs),
			neededJars.hideConditioned(givenBells), neededStaples.hideConditioned(givenStaples));
		((NpcStep) killSorebones).addAlternateNpcs(NpcID.SOREBONES_562);
		goBackDownToFenk = new ObjectStep(this, ObjectID.LADDER_22173, new WorldPoint(3789, 2826, 0),
			"Return to Dr. Fenkenstrain in the Harmony Windmill basement.", hammer.hideConditioned(givenHammer),
			cranialClamp.hideConditioned(givenClamp), brainTongs.hideConditioned(givenTongs),
			neededJars.hideConditioned(givenBells), neededStaples.hideConditioned(givenStaples));
		talkToFenkWithItems = new NpcStep(this, NpcID.DR_FENKENSTRAIN, new WorldPoint(3785, 9225, 0),
			"Return to Dr. Fenkenstrain in the Harmony Windmill basement.", hammer.hideConditioned(givenHammer),
			cranialClamp.hideConditioned(givenClamp), brainTongs.hideConditioned(givenTongs),
			neededJars.hideConditioned(givenBells), neededStaples.hideConditioned(givenStaples));
		talkToFenkWithItems.addSubSteps(goBackDownToFenk);

		goUpFromFenkAfterItems = new ObjectStep(this, ObjectID.LADDER_22172, new WorldPoint(3789, 9226, 0),
			"Talk to Brother Tranquility on Harmony.");
		talkToTranquilityAfterHelping = new NpcStep(this, NpcID.BROTHER_TRANQUILITY_551, new WorldPoint(3787, 2825,
			0), "Talk to Brother Tranquility on Harmony.");
		talkToTranquilityAfterHelping.addSubSteps(goUpFromFenkAfterItems);

		enterChurchForFight = new ObjectStep(this, NullObjectID.NULL_22119, new WorldPoint(3805, 2844, 0),
			"Enter the church, ready to fight Barrelchest. If after initially entering you leave then re-enter, the " +
				"Barrelchest will get stuck and you can safespot it from the door.", fishbowlHelmet.equipped(),
			divingApparatus.equipped(), combatGearForSafespotting);
		confrontMigor = new NpcStep(this, NpcID.MIGOR, new WorldPoint(3815, 2844, 0),
			"Confront Migor.");
		defeatBarrelchest = new NpcStep(this, NpcID.BARRELCHEST, new WorldPoint(3815, 2844, 0),
			"Defeat the barrelchest. You can safespot it from the door if you leave and re-enter the instance.");
		((NpcStep) defeatBarrelchest).addSafeSpots(new WorldPoint(3806, 2844, 0));

		pickupAnchor = new ItemStep(this, "Pickup the anchor.", anchor);
		talkToTranquilityToFinish = new NpcStep(this, NpcID.BROTHER_TRANQUILITY_551, new WorldPoint(3787, 2825,
			0), "Talk to Brother Tranquility on Harmony to complete the quest.");
		talkToTranquilityToFinish.addSubSteps(pickupAnchor);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(fishbowlHelmet, divingApparatus, catsOrResources, plank.quantity(8), hammer,
			nails.quantity(100), holySymbol, ringOfCharos);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(ectophial, edgevilleTeleport, fenkenstrainTeleport, watermelonSeeds.quantity(3),
			combatGearForSafespotting, food, prayerPotions);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new QuestRequirement(QuestHelperQuest.CREATURE_OF_FENKENSTRAIN, QuestState.FINISHED));
		reqs.add(new VarbitRequirement(QuestVarbits.QUEST_RECIPE_FOR_DISASTER_PIRATE_PETE.getId(),
			Operation.GREATER_EQUAL, 110, "Finished RFD - Pirate Pete"));
		reqs.add(new QuestRequirement(QuestHelperQuest.RUM_DEAL, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.CABIN_FEVER, QuestState.FINISHED));

		reqs.add(new SkillRequirement(Skill.CRAFTING, 16));
		reqs.add(new SkillRequirement(Skill.CONSTRUCTION, 30));
		reqs.add(new SkillRequirement(Skill.PRAYER, 50));
		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Barrelchest (level 190, safespottable)", "4 Sorebones (level 57)");
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
				new ExperienceReward(Skill.PRAYER, 6000),
				new ExperienceReward(Skill.CRAFTING, 2000),
				new ExperienceReward(Skill.CONSTRUCTION, 2000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("Barrelchest Anchor", ItemID.BARRELCHEST_ANCHOR, 1),
				new ItemReward("5,000 Exp Reward Lamp (Any skill above 30)", ItemID.ANTIQUE_LAMP, 1),
				new ItemReward("Prayer Book", ItemID.PRAYER_BOOK, 1));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting off",
			Arrays.asList(talkToTranquility, pullStatue, enterWater, repairWaterStairs, climbFromWater,
				climbFromWaterCaveToPeep, peerThroughHole, talkToTranquilityAfterPeeping),
			plank.quantity(4), nails.quantity(60), hammer, fishbowlHelmet, divingApparatus));

		allSteps.add(new PanelDetails("Protecting the windmill",
			Arrays.asList(searchBookcase, readBook, returnToTranquility, recitePrayer, talkToTranquilityAfterPrayer),
			holySymbol));

		allSteps.add(new PanelDetails("Finding a Doctor",
			Arrays.asList(talkToFenk, talkToRufus, makeOrGetWoodenCats, buildCrate, addBottomToCrate, fillCrate,
				blowWhistle, putOrderOnCrate),
			ringOfCharos, catsOrResources, plank.quantity(4), nails.quantity(100), hammer));

		allSteps.add(new PanelDetails("Saving the Monks",
			Arrays.asList(talkToFenkOnHarmony, getFuse, climbShipLadder, getTinderbox, climbDownFromShip,
				useKegOnDoor, useFuseOnDoor, lightFuse, killSorebones, talkToFenkWithItems, talkToTranquilityAfterHelping),
			hammer, fishbowlHelmet, divingApparatus, combatGearForSafespotting));

		allSteps.add(new PanelDetails("Defeating a Barrelchest",
			Arrays.asList(enterChurchForFight, confrontMigor, defeatBarrelchest, talkToTranquilityToFinish),
			fishbowlHelmet, divingApparatus, combatGearForSafespotting));

		return allSteps;
	}
}
