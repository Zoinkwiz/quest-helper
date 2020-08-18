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
package com.questhelper.quests.myarmsbigadventure;

import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;

@QuestDescriptor(
	quest = QuestHelperQuest.MY_ARMS_BIG_ADVENTURE
)
public class MyArmsBigAdventure extends BasicQuestHelper
{
	ItemRequirement goutLump, bucket, bucketHighlight;

	ConditionForStep inStrongholdFloor1, inStrongholdFloor2, inPrison;

	DetailedQuestStep enterStronghold, goDownToChef, goUpToChef, talkToBurntmeat, talkToMyArm, enterDeathPlateau, pickUpBucket, useBucketOnPot, enterStrongholdForWithLump, goDownToArmWithLump,
		talkToArmWithLump, goUpToMyArm, talkToMyArmUpstairs, readBook, talkToMyArmAfterReading, useUgthankiDung, useCompost, talkToMyArmAfterFertilising, talkToBarnaby, talkToMyArmAtTai,
		talkToMurcaily, talkToMyArmAfterMurcaily, enterStrongholdForFight, goUpToMyArmForFight, talkToMyArmForFight, giveRake, giveSupercompost, giveHardyGout, giveDibber, giveCure,
		talkToMyArmAfterGrow, killBabyRoc, killGiantRoc, talkToMyArmAfterFight, giveSpade, goDownFromMyArmToBurntmeat, goDownToBurntmeat, talkToBurntmeatAgain, goUpToMyArmFinish,
		goUpFromBurntmeatFinish, talkToMyArmFinish;

	Zone strongholdFloor1, strongholdFloor2, prison;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep startingOffSteps = new ConditionalStep(this, enterStronghold);
		startingOffSteps.addStep(inStrongholdFloor1, talkToBurntmeat);
		startingOffSteps.addStep(inPrison, goUpToChef);
		startingOffSteps.addStep(inStrongholdFloor2, goDownToChef);

		steps.put(0, startingOffSteps);

		ConditionalStep goTalkToBurntmeat = new ConditionalStep(this, enterStronghold);
		goTalkToBurntmeat.addStep(inStrongholdFloor1, talkToMyArm);
		goTalkToBurntmeat.addStep(inPrison, goUpToChef);
		goTalkToBurntmeat.addStep(inStrongholdFloor2, goDownToChef);


		steps.put(1, goTalkToBurntmeat);

		steps.put(2, useBucketOnPot);

		return steps;
	}

	public void setupItemRequirements()
	{
		goutLump = new ItemRequirement("Goutweedy lump", ItemID.GOUTWEEDY_LUMP);
		bucket = new ItemRequirement("Bucket", ItemID.BUCKET);
		bucketHighlight = new ItemRequirement("Bucket", ItemID.BUCKET);
		bucketHighlight.setHighlightInInventory(true);
	}

	public void loadZones()
	{
		strongholdFloor1 = new Zone(new WorldPoint(2820, 10048, 1), new WorldPoint(2862, 10110, 1));
		strongholdFloor2 = new Zone(new WorldPoint(2820, 10048, 2), new WorldPoint(2862, 10110, 2));
		prison = new Zone(new WorldPoint(2822, 10049, 0), new WorldPoint(2859, 10110, 0));
	}

	public void setupConditions()
	{
		inStrongholdFloor1 = new ZoneCondition(strongholdFloor1);
		inStrongholdFloor2 = new ZoneCondition(strongholdFloor2);
		inPrison = new ZoneCondition(prison);
	}

	public void setupSteps()
	{
		enterStronghold = new ObjectStep(this, ObjectID.STRONGHOLD, new WorldPoint(2839, 3690, 0), "Enter the Troll Stronghold.");

		goDownToChef = new ObjectStep(this, ObjectID.STONE_STAIRCASE_3789, new WorldPoint(2844, 10052, 2), "Go down the south staircase.");
		goDownToChef.setWorldMapPoint(new WorldPoint(2971, 10115, 1));

		goUpToChef = new ObjectStep(this, ObjectID.STONE_STAIRCASE, new WorldPoint(2853, 10107, 0), "Go up the stairs from the prison.");
		goUpToChef.setWorldMapPoint(new WorldPoint(2853, 10106, 1));

		talkToBurntmeat = new NpcStep(this, NpcID.BURNTMEAT, new WorldPoint(2845, 10057, 1), "Talk to Burntmeat in the Troll Stronghold.");
		talkToBurntmeat.setWorldMapPoint(new WorldPoint(2911, 10087, 1));
		talkToBurntmeat.addSubSteps(enterStronghold, goDownToChef, goUpToChef);

		talkToMyArm = new NpcStep(this, NpcID.MY_ARM, new WorldPoint(2855, 10053, 1), "Talk to My Arm near Burntmeat.");
		talkToMyArm.setWorldMapPoint(new WorldPoint(2919, 10084, 0));
//		enterDeathPlateau;
		pickUpBucket = new ItemStep(this, "Pick up the nearby bucket.", bucket);
		useBucketOnPot = new ObjectStep(this, ObjectID.POT, new WorldPoint(2865, 3586, 0), "Use a bucket on the pot.", bucketHighlight);
		useBucketOnPot.addIcon(ItemID.BUCKET);
		enterStrongholdForWithLump = new ObjectStep(this, ObjectID.STRONGHOLD, new WorldPoint(2839, 3690, 0), "Enter the Troll Stronghold.", goutLump);
//		goDownToArmWithLump;
//
//		talkToArmWithLump;
//		goUpToMyArm;
//		talkToMyArmUpstairs;
//		readBook;
//		talkToMyArmAfterReading;
//		useUgthankiDung;
//		useCompost;
//		talkToMyArmAfterFertilising;
//		talkToBarnaby;
//		talkToMyArmAtTai;
//
//		talkToMurcaily;
//		talkToMyArmAfterMurcaily;
//		enterStrongholdForFight;
//		goUpToMyArmForFight;
//		talkToMyArmForFight;
//		giveRake;
//		giveSupercompost;
//		giveHardyGout;
//		giveDibber;
//		giveCure;
//
//		talkToMyArmAfterGrow;
//		killBabyRoc;
//		killGiantRoc;
//		talkToMyArmAfterFight;
//		giveSpade;
//		goDownFromMyArmToBurntmeat;
//		goDownToBurntmeat;
//		talkToBurntmeatAgain;
//		goUpToMyArmFinish;
//
//		goUpFromBurntmeatFinish;
//		talkToMyArmFinish;
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList());
	}


	@Override
	public ArrayList<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Baby Roc (level 75)");
		reqs.add("Giant Roc (level 172)");
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Arrays.asList(talkToBurntmeat))));
		return allSteps;
	}
}

