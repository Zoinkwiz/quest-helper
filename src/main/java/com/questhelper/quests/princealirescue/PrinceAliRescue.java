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
package com.questhelper.quests.princealirescue;

import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.PRINCE_ALI_RESCUE
)
public class PrinceAliRescue extends BasicQuestHelper
{
	ItemRequirement softClay, ballsOfWool3, yellowDye, redberries, ashes, bucketOfWater, potOfFlour, bronzeBar, pinkSkirt, beers3, rope, coins100, wig, dyedWig, paste, keyMould, key,
	ropeReqs, yellowDyeReqs, glory;

	ConditionForStep hasWig, hasDyedWig, hasKey, hasPaste, hasKeyMould, inCell, givenKeyMould;

	QuestStep talkToHassan, talkToOsman, talkToNed, talkToAggie, dyeWig, talkToKeli, bringImprintToOsman, talkToLeela, talkToJoe, useRopeOnKeli, useKeyOnDoor, talkToAli, returnToHassan;

	ConditionalStep makeDyedWig, makePaste, makeKeyMould, getKey;

	Zone cell;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToHassan);
		steps.put(10, talkToOsman);

		makeDyedWig = new ConditionalStep(this, talkToNed);
		makeDyedWig.addStep(hasWig, dyeWig);
		makeDyedWig.setLockingCondition(hasDyedWig);

		makePaste = new ConditionalStep(this, talkToAggie);
		makePaste.setLockingCondition(hasPaste);

		makeKeyMould = new ConditionalStep(this, talkToKeli);
		makeKeyMould.setLockingCondition(new Conditions(hasKeyMould, hasKey));

		getKey = new ConditionalStep(this, talkToLeela);
		getKey.addStep(hasKeyMould, bringImprintToOsman);
		getKey.setLockingCondition(hasKey);

		ConditionalStep prepareToSaveAli = new ConditionalStep(this, makeDyedWig);
		prepareToSaveAli.addStep(new Conditions(hasDyedWig, hasPaste, hasKeyMould), getKey);
		prepareToSaveAli.addStep(new Conditions(hasDyedWig, hasPaste), makeKeyMould);
		prepareToSaveAli.addStep(hasDyedWig, makePaste);

		steps.put(20, prepareToSaveAli);

		ConditionalStep getJoeDrunk = new ConditionalStep(this, makeDyedWig);
		getJoeDrunk.addStep(new Conditions(hasDyedWig, hasPaste, hasKey), talkToJoe);
		getJoeDrunk.addStep(hasDyedWig, makePaste);

		steps.put(30, getJoeDrunk);
		steps.put(31, getJoeDrunk);
		steps.put(32, getJoeDrunk);
		steps.put(33, getJoeDrunk);

		ConditionalStep tieUpKeli = new ConditionalStep(this, makeDyedWig);
		tieUpKeli.addStep(new Conditions(hasDyedWig, hasPaste, hasKey), useRopeOnKeli);
		tieUpKeli.addStep(hasDyedWig, makePaste);
		steps.put(40, tieUpKeli);

		ConditionalStep freeAli = new ConditionalStep(this, makeDyedWig);
		freeAli.addStep(new Conditions(hasDyedWig, hasPaste, hasKey, inCell), talkToAli);
		freeAli.addStep(new Conditions(hasDyedWig, hasPaste, hasKey), useKeyOnDoor);
		freeAli.addStep(hasDyedWig, makePaste);
		steps.put(50, freeAli);

		steps.put(100, returnToHassan);

		return steps;
	}

	public void setupItemRequirements()
	{
		softClay = new ItemRequirement("Soft clay", ItemID.SOFT_CLAY);
		ballsOfWool3 = new ItemRequirement("Balls of wool", ItemID.BALL_OF_WOOL, 3);
		yellowDye = new ItemRequirement("Yellow dye", ItemID.YELLOW_DYE);
		yellowDye.setHighlightInInventory(true);
		redberries = new ItemRequirement("Redberries", ItemID.REDBERRIES);
		ashes = new ItemRequirement("Ashes", ItemID.ASHES);
		bucketOfWater = new ItemRequirement("Bucket of water", ItemID.BUCKET_OF_WATER);
		potOfFlour = new ItemRequirement("Pot of flour", ItemID.POT_OF_FLOUR);
		bronzeBar = new ItemRequirement("Bronze bar", ItemID.BRONZE_BAR);
		pinkSkirt = new ItemRequirement("Pink skirt", ItemID.PINK_SKIRT);
		beers3 = new ItemRequirement("Beers", ItemID.BEER, 3);
		rope = new ItemRequirement("Rope", ItemID.ROPE);
		ropeReqs = new ItemRequirement("Rope, or 15 coins / 4 balls of wool to obtain during the quest", ItemID.ROPE);
		coins100 = new ItemRequirement("100 coins minimum", ItemID.COINS_995, -1);
		wig = new ItemRequirement("Wig", ItemID.WIG_2421);
		wig.setHighlightInInventory(true);
		dyedWig = new ItemRequirement("Wig (dyed)", ItemID.WIG);
		paste = new ItemRequirement("Paste", ItemID.PASTE);
		keyMould = new ItemRequirement("Key print", ItemID.KEY_PRINT);
		key = new ItemRequirement("Bronze key", ItemID.BRONZE_KEY);
		key.setTip("You can get another from Leela for 15 coins");
		yellowDyeReqs = new ItemRequirement("Yellow dye, or 2 onions + 5 coins to obtain during quest", ItemID.YELLOW_DYE);
		glory = new ItemRequirement("Amulet of Glory for Al Kharid and Draynor Village teleports", ItemID.AMULET_OF_GLORY6);
	}

	public void setupConditions()
	{
		inCell = new ZoneCondition(cell);
		hasDyedWig = new ItemRequirementCondition(dyedWig);
		hasWig = new ItemRequirementCondition(wig);
		hasKey = new ItemRequirementCondition(key);
		hasPaste = new ItemRequirementCondition(paste);
		hasKeyMould = new ItemRequirementCondition(keyMould);
		givenKeyMould = new VarbitCondition(200, 1);
	}

	public void setupZones()
	{
		cell = new Zone(new WorldPoint(3121, 3240, 0), new WorldPoint(3125, 3243, 0));
	}

	public void setupSteps()
	{
		talkToHassan = new NpcStep(this, NpcID.HASSAN, new WorldPoint(3298, 3163, 0), "Talk to Hassan in the Al Kharid Palace.");
		talkToHassan.addDialogStep("Can I help you? You must need some help here in the desert.");
		talkToOsman = new NpcStep(this, NpcID.OSMAN_4286, new WorldPoint(3286, 3180, 0), "Talk to Osman north of the Al Kharid Palace.");
		talkToOsman.addDialogStep("What is the first thing I must do?");

		talkToNed = new NpcStep(this, NpcID.NED, new WorldPoint(3097, 3257, 0), "Have Ned in Draynor Village make you a wig from 3 balls of wool. He can also sell you a rope for 15 coins or 4 balls of wool.", ballsOfWool3);
		talkToNed.addDialogStep("Ned, could you make other things from wool?");
		talkToNed.addDialogStep("How about some sort of a wig?");
		talkToNed.addDialogStep("I have that now. Please, make me a wig.");
		dyeWig = new DetailedQuestStep(this, "Dye the wig with yellow dye. Buy a yellow dye with two onions and 5 coins if you still need it from Aggie in Draynor Village.", yellowDye, wig);
		talkToAggie = new NpcStep(this, NpcID.AGGIE, new WorldPoint(3086, 3257, 0), "Talk to Aggie in Draynor Village to get some paste.", redberries, ashes, potOfFlour, bucketOfWater);
		talkToAggie.addDialogStep("Could you think of a way to make skin paste?");
		talkToAggie.addDialogStep("Yes please. Mix me some skin paste.");
		talkToKeli = new NpcStep(this, NpcID.LADY_KELI, new WorldPoint(3127, 3244, 0), "Talk to Keli in the jail east of Draynor Village.");
		talkToKeli.addDialogStep("Heard of you? You are famous in Gielinor!");
		talkToKeli.addDialogStep("What is your latest plan then?");
		talkToKeli.addDialogStep("Can you be sure they will not try to get him out?");
		talkToKeli.addDialogStep("Could I see the key please?");
		talkToKeli.addDialogStep("Could I touch the key for a moment please?");
		bringImprintToOsman = new NpcStep(this, NpcID.OSMAN_4286, new WorldPoint(3285, 3179, 0), "Bring the key print to Osman north of the Al Kharid Palace.", keyMould);
		talkToLeela = new NpcStep(this, NpcID.LEELA, new WorldPoint(3113, 3262, 0), "Get a key from Leela east of Draynor Village.");
		talkToJoe = new NpcStep(this, NpcID.JOE_4275, new WorldPoint(3124, 3245, 0), "Bring everything to the jail and give Joe there three beers.", beers3, key, dyedWig, paste, rope);
		talkToJoe.addDialogStep("I have some beer here, fancy one?");
		useRopeOnKeli = new NpcStep(this, NpcID.LADY_KELI, new WorldPoint(3127, 3244, 0), "Use rope on Keli.", rope);
		useRopeOnKeli.addIcon(ItemID.ROPE);
		useKeyOnDoor = new ObjectStep(this, ObjectID.PRISON_DOOR_2881, new WorldPoint(3123, 3243, 0), "Use the key on the prison door. If Lady Keli respawned you'll need to tie her up again.", key);
		useKeyOnDoor.addIcon(ItemID.BRONZE_KEY);
		talkToAli = new NpcStep(this, NpcID.PRINCE_ALI, new WorldPoint(3123, 3240, 0), "Talk to Prince Ali and free him.");

		returnToHassan = new NpcStep(this, NpcID.HASSAN, new WorldPoint(3298, 3163, 0), "Return Hassan in the Al Kharid Palace to complete the quest.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(softClay);
		reqs.add(ballsOfWool3);
		reqs.add(yellowDye);
		reqs.add(redberries);
		reqs.add(ashes);
		reqs.add(bucketOfWater);
		reqs.add(potOfFlour);
		reqs.add(bronzeBar);
		reqs.add(pinkSkirt);
		reqs.add(beers3);
		reqs.add(rope);
		reqs.add(coins100);
		return reqs;
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(glory);
		return reqs;
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Able to survive jail guards (level 26) attacking you");
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Arrays.asList(talkToHassan, talkToOsman))));

		PanelDetails makeWigPanel = new PanelDetails("Make a blonde wig", new ArrayList<>(Arrays.asList(talkToNed, dyeWig)), yellowDye, ballsOfWool3);
		makeWigPanel.setLockingStep(makeDyedWig);
		allSteps.add(makeWigPanel);

		PanelDetails makePastePanel = new PanelDetails("Make paste", new ArrayList<>(Arrays.asList(talkToAggie)), redberries, ashes, potOfFlour, bucketOfWater);
		makePastePanel.setLockingStep(makePaste);
		allSteps.add(makePastePanel);

		PanelDetails makeKeyMouldPanel = new PanelDetails("Make a key mould", new ArrayList<>(Arrays.asList(talkToKeli)), softClay);
		makeKeyMouldPanel.setLockingStep(makeKeyMould);
		allSteps.add(makeKeyMouldPanel);

		PanelDetails getKeyPanel = new PanelDetails("Get key", new ArrayList<>(Arrays.asList(bringImprintToOsman, talkToLeela)), bronzeBar, keyMould);
		getKeyPanel.setLockingStep(getKey);
		allSteps.add(getKeyPanel);

		allSteps.add(new PanelDetails("Free Ali", new ArrayList<>(Arrays.asList(talkToJoe, useRopeOnKeli, useKeyOnDoor, talkToAli)), dyedWig, paste, key, rope, beers3));

		allSteps.add(new PanelDetails("Return to Al Kharid", new ArrayList<>(Arrays.asList(returnToHassan))));
		return allSteps;
	}
}
