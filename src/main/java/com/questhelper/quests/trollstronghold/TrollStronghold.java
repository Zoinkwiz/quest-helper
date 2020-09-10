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
package com.questhelper.quests.trollstronghold;

import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemCondition;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.VarplayerCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;

@QuestDescriptor(
	quest = QuestHelperQuest.TROLL_STRONGHOLD
)
public class TrollStronghold extends BasicQuestHelper
{
	ItemRequirement climbingBoots, climbingBootsOr12Coins, foodAndPotions, gamesNecklace, coins12, prisonKey, cellKey1, cellKey2, mageRangedGear;

	ConditionForStep inStrongholdFloor1, inStrongholdFloor2, inTenzingHut, hasClimbingBoots, hasCoins, onMountainPath, inTrollArea1, inArena, inNorthArena,
		beatenDad, inArenaCave, inTrollheimArea, hasPrisonKey, prisonKeyNearby, prisonDoorUnlocked, inPrisonStairsRoom, inPrison, hasCellKey1, hasCellKey2,
		freedEadgar, freedGodric;

	QuestStep talkToDenulth, buyClimbingBoots, travelToTenzing, getCoinsOrBoots, climbOverStile, climbOverRocks, enterArena, fightDad,
		leaveArena, enterArenaCavern, leaveArenaCavern, enterStronghold, killGeneral, pickupPrisonKey, goDownInStronghold, goThroughPrisonDoor,
		goUpTo2ndFloor, goDownToPrison, getTwigKey, getBerryKey, freeEadgar, freeGodric, goToDunstan;

	Zone strongholdFloor1, strongholdFloor2, tenzingHut, mountainPath1, mountainPath2, mountainPath3, mountainPath4, mountainPath5, trollArea1, arena, northArena,
		arenaCave, trollheimArea, prisonStairsRoom, prison;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToDenulth);

		ConditionalStep enterTheStronghold = new ConditionalStep(this, getCoinsOrBoots);
		enterTheStronghold.addStep(new Conditions(freedEadgar, freedGodric), goToDunstan);
		enterTheStronghold.addStep(new Conditions(inPrison, freedEadgar, hasCellKey1), freeGodric);
		enterTheStronghold.addStep(new Conditions(inPrison, freedEadgar), getTwigKey);
		enterTheStronghold.addStep(new Conditions(inPrison, hasCellKey2), freeEadgar);
		enterTheStronghold.addStep(inPrison, getBerryKey);
		enterTheStronghold.addStep(inPrisonStairsRoom, goDownToPrison);
		enterTheStronghold.addStep(new Conditions(new Conditions(LogicType.OR, prisonDoorUnlocked, hasPrisonKey), inStrongholdFloor1), goThroughPrisonDoor);
		enterTheStronghold.addStep(new Conditions(new Conditions(LogicType.OR, prisonDoorUnlocked, hasPrisonKey), inStrongholdFloor2), goDownInStronghold);
		enterTheStronghold.addStep(prisonKeyNearby, pickupPrisonKey);
		enterTheStronghold.addStep(inStrongholdFloor2, killGeneral);
		enterTheStronghold.addStep(inStrongholdFloor1, goUpTo2ndFloor);
		enterTheStronghold.addStep(inTrollheimArea, enterStronghold);
		enterTheStronghold.addStep(inArenaCave, leaveArenaCavern);
		enterTheStronghold.addStep(inNorthArena, enterArenaCavern);
		enterTheStronghold.addStep(new Conditions(inArena, beatenDad), leaveArena);
		enterTheStronghold.addStep(inArena, fightDad);
		enterTheStronghold.addStep(inTrollArea1, enterArena);
		enterTheStronghold.addStep(new Conditions(hasClimbingBoots, onMountainPath), climbOverRocks);
		enterTheStronghold.addStep(new Conditions(hasClimbingBoots, inTenzingHut), climbOverStile);
		enterTheStronghold.addStep(hasClimbingBoots, travelToTenzing);
		enterTheStronghold.addStep(hasCoins, buyClimbingBoots);

		steps.put(10, enterTheStronghold);
		steps.put(20, enterTheStronghold);
		steps.put(30, enterTheStronghold);
		steps.put(40, enterTheStronghold);
		return steps;
	}

	public void setupItemRequirements()
	{
		climbingBoots = new ItemRequirement("Climbing boots", ItemID.CLIMBING_BOOTS, 1, true);
		climbingBootsOr12Coins = new ItemRequirement("Climbing boots or 12 coins", -1, -1);
		gamesNecklace = new ItemRequirement("Games necklace", ItemID.GAMES_NECKLACE8);
		coins12 = new ItemRequirement("Coins", ItemID.COINS_995, 12);
		mageRangedGear = new ItemRequirement("Mage or ranged gear for safe spotting", -1, -1);
		foodAndPotions = new ItemRequirement("Food + prayer potions", -1, -1);
		prisonKey = new ItemRequirement("Prison key", ItemID.PRISON_KEY);
		cellKey1 = new ItemRequirement("Cell key 1", ItemID.CELL_KEY_1);
		cellKey2 = new ItemRequirement("Cell key 2", ItemID.CELL_KEY_2);
	}

	public void loadZones()
	{
		tenzingHut = new Zone(new WorldPoint(2814, 3553, 0), new WorldPoint(2822, 3562, 0));
		mountainPath1 = new Zone(new WorldPoint(2814, 3563, 0), new WorldPoint(2823, 3593, 0));
		mountainPath2 = new Zone(new WorldPoint(2824, 3589, 0), new WorldPoint(2831, 3599, 0));
		mountainPath3 = new Zone(new WorldPoint(2832, 3595, 0), new WorldPoint(2836, 3603, 0));
		mountainPath4 = new Zone(new WorldPoint(2837, 3601, 0), new WorldPoint(2843, 3607, 0));
		mountainPath5 = new Zone(new WorldPoint(2844, 3607, 0), new WorldPoint(2876, 3611, 0));
		trollArea1 = new Zone(new WorldPoint(2822, 3613, 0), new WorldPoint(2896, 3637, 0));
		arena = new Zone(new WorldPoint(2897, 3598, 0), new WorldPoint(2924, 3628, 0));
		northArena = new Zone(new WorldPoint(2898, 3629, 0), new WorldPoint(2927, 3644, 0));
		arenaCave = new Zone(new WorldPoint(2904, 10019, 0), new WorldPoint(2927, 10035, 0));
		trollheimArea = new Zone(new WorldPoint(2836, 3651, 0), new WorldPoint(2934, 3773, 0));
		strongholdFloor1 = new Zone(new WorldPoint(2820, 10048, 1), new WorldPoint(2862, 10110, 1));
		strongholdFloor2 = new Zone(new WorldPoint(2820, 10048, 2), new WorldPoint(2862, 10110, 2));
		prisonStairsRoom = new Zone(new WorldPoint(2848, 10104, 1), new WorldPoint(2857, 10110, 1));
		prison = new Zone(new WorldPoint(2822, 10049, 0), new WorldPoint(2859, 10110, 0));
	}

	public void setupConditions()
	{
		hasClimbingBoots = new ItemRequirementCondition(climbingBoots);
		hasCoins = new ItemRequirementCondition(coins12);
		inTenzingHut = new ZoneCondition(tenzingHut);
		onMountainPath = new ZoneCondition(mountainPath1, mountainPath2, mountainPath3, mountainPath4, mountainPath5);
		inTrollArea1 = new ZoneCondition(trollArea1);
		inArena = new ZoneCondition(arena);
		inNorthArena = new ZoneCondition(northArena);
		beatenDad = new VarplayerCondition(317, 20, Operation.GREATER_EQUAL);
		prisonDoorUnlocked = new VarplayerCondition(317, 30, Operation.GREATER_EQUAL);
		inArenaCave = new ZoneCondition(arenaCave);
		inTrollheimArea = new ZoneCondition(trollheimArea);
		inStrongholdFloor1 = new ZoneCondition(strongholdFloor1);
		inStrongholdFloor2 = new ZoneCondition(strongholdFloor2);
		inPrisonStairsRoom = new ZoneCondition(prisonStairsRoom);
		inPrison = new ZoneCondition(prison);
		hasPrisonKey = new ItemRequirementCondition(prisonKey);
		prisonKeyNearby = new ItemCondition(ItemID.PRISON_KEY);
		hasCellKey1 = new ItemRequirementCondition(cellKey1);
		hasCellKey2 = new ItemRequirementCondition(cellKey2);
		freedEadgar = new VarbitCondition(0, 1);
		freedGodric = new VarplayerCondition(317, 40);
	}

	public void setupSteps()
	{
		talkToDenulth = new NpcStep(this, NpcID.DENULTH, new WorldPoint(2895, 3528, 0), "Talk to Denulth in Burthorpe.");
		talkToDenulth.addDialogStep("How goes your fight with the trolls?");
		talkToDenulth.addDialogStep("Is there anything I can do to help?");
		talkToDenulth.addDialogStep("I'll get Godric back!");

		getCoinsOrBoots = new DetailedQuestStep(this, "Get some climbing boots or 12 coins, and prepare for fighting Dad and the Troll General. Both can be safe spotted by ranged/mage.", climbingBootsOr12Coins);

		travelToTenzing = new DetailedQuestStep(this, new WorldPoint(2820, 3555, 0), "Follow the path west of Burthorpe, then go along the path going south.");
		buyClimbingBoots = new NpcStep(this, NpcID.TENZING, new WorldPoint(2820, 3555, 0), "Follow the path west of Burthorpe, then go along the path going south. Buy some climbing boots from Tenzing in his hut here.", coins12);
		buyClimbingBoots.addDialogStep("Can I buy some Climbing boots?");
		buyClimbingBoots.addDialogStep("OK, sounds good.");
		travelToTenzing.addSubSteps(getCoinsOrBoots, buyClimbingBoots);

		climbOverStile = new ObjectStep(this, ObjectID.STILE_3730, new WorldPoint(2817, 3563, 0), "Climb over the stile north of Tenzing.");
		climbOverRocks = new ObjectStep(this, ObjectID.ROCKS_3748, new WorldPoint(2856, 3612, 0), "Follow the path until you reach some rocks. Climb over them.", climbingBoots);
		enterArena = new ObjectStep(this, ObjectID.ARENA_ENTRANCE_3783, new WorldPoint(2897, 3619, 0), "Follow the path from here east until you enter the arena.");
		fightDad = new NpcStep(this, NpcID.DAD, new WorldPoint(2913, 3617, 0), "Fight Dad until he gives up. You can safe spot him from the gate you entered through.");
		fightDad.addDialogStep("I accept your challenge!");

		leaveArena = new ObjectStep(this, ObjectID.ARENA_EXIT, new WorldPoint(2916, 3629, 0), "Leave the arena and continue through the cave to the north.");
		leaveArena.addDialogStep("I'll be going now.");
		enterArenaCavern = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_3757, new WorldPoint(2904, 3645, 0), "Enter the cave entrance.");
		leaveArenaCavern = new ObjectStep(this, ObjectID.CAVE_EXIT_3758, new WorldPoint(2907, 10037, 0), "Leave through cave's north exit.");
		enterStronghold = new ObjectStep(this, ObjectID.STRONGHOLD, new WorldPoint(2839, 3690, 0), "Follow the path around Trollheim until you reach the Stronghold's entrance. Be wary of thrower trolls on the path, you'll want to use Protect from Ranged.");
		if (client.getRealSkillLevel(Skill.AGILITY) >= 47)
		{
			enterStronghold.getText().add("They can be avoided by taking the agility shortcuts across the mountain.");
		}

		killGeneral = new NpcStep(this, NpcID.TROLL_GENERAL, new WorldPoint(2830, 10086, 2), "Enter the west rooms and kill any of the Troll Generals for a prison key.");
		pickupPrisonKey = new ItemStep(this, "Pick up the prison key.", prisonKey);

		goDownInStronghold = new ObjectStep(this, ObjectID.STONE_STAIRCASE_3789, new WorldPoint(2844, 10109, 2), "Climb down the north staircase.");
		goThroughPrisonDoor = new ObjectStep(this, ObjectID.PRISON_DOOR_3780, new WorldPoint(2848, 10107, 1), "Enter the prison door.");
		goUpTo2ndFloor = new ObjectStep(this, ObjectID.STONE_STAIRCASE, new WorldPoint(2843, 10109, 1), "Go back up the stairs.");
		goDownToPrison = new ObjectStep(this, ObjectID.STONE_STAIRCASE_3789, new WorldPoint(2853, 10108, 1), "Climb down the stairs to the prison.");
		if (client.getRealSkillLevel(Skill.THIEVING) >= 30)
		{
			getTwigKey = new NpcStep(this, NpcID.TWIG_4133, new WorldPoint(2833, 10079, 0), "Pickpocket or kill Twig for a cell key.");
			getBerryKey = new NpcStep(this, NpcID.BERRY_4134, new WorldPoint(2833, 10083, 0), "Pickpocket or kill Berry for a cell key.");
		}
		else
		{
			getTwigKey = new NpcStep(this, NpcID.TWIG_4133, new WorldPoint(2833, 10079, 0), "Kill Twig for a cell key.");
			getBerryKey = new NpcStep(this, NpcID.BERRY_4134, new WorldPoint(2833, 10083, 0), "Kill Berry for a cell key.");
		}

		freeGodric = new ObjectStep(this, ObjectID.CELL_DOOR_3767, new WorldPoint(2832, 10078, 0), "Unlock Godric's cell.");
		freeEadgar = new ObjectStep(this, ObjectID.CELL_DOOR_3765, new WorldPoint(2832, 10082, 0), "Unlock Eadgar's cell.");

		goToDunstan = new NpcStep(this, NpcID.DUNSTAN, new WorldPoint(2919, 3574, 0), "Talk to Dunstan in north east Burthorpe to finish the quest.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(climbingBootsOr12Coins);
		return reqs;
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Dad (level 101) (safespottable)");
		reqs.add("Troll Generall (level 113) (safespottable)");
		return reqs;
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(gamesNecklace);
		reqs.add(foodAndPotions);
		reqs.add(mageRangedGear);
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Collections.singletonList(talkToDenulth))));
		allSteps.add(new PanelDetails("Reach the Stronghold", new ArrayList<>(Arrays.asList(travelToTenzing, climbOverStile, climbOverRocks, enterArena, fightDad, leaveArena, enterArenaCavern, leaveArenaCavern, enterStronghold)), climbingBootsOr12Coins, mageRangedGear, foodAndPotions));
		allSteps.add(new PanelDetails("Free the prisoners", new ArrayList<>(Arrays.asList(killGeneral, goDownInStronghold, goThroughPrisonDoor, goDownToPrison, getBerryKey, freeEadgar, getTwigKey, freeGodric))));
		allSteps.add(new PanelDetails("Finish off", new ArrayList<>(Collections.singletonList(goToDunstan))));
		return allSteps;
	}
}
