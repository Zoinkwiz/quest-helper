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

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.RuneliteRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
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
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;

@QuestDescriptor(
	quest = QuestHelperQuest.PRINCE_ALI_RESCUE
)
public class PrinceAliRescue extends BasicQuestHelper
{
	//Items Required
	ItemRequirement softClay, ballsOfWool3, yellowDye, redberries, ashes, bucketOfWater, potOfFlour, bronzeBar, pinkSkirt, beers3, rope, coins100, wig, dyedWig, paste, keyMould, key,
		ropeReqs, yellowDyeReqs, ropeHighlighted;

	//Items Recommended
	ItemRequirement glory;

	Requirement hasOrGivenKeyMould, inCell, givenKeyMould, hasWigPasteAndKey;

	RuneliteRequirement madeMould;

	QuestStep talkToHassan, talkToOsman, talkToNed, talkToAggie, dyeWig, talkToKeli, bringImprintToOsman, talkToLeela, talkToJoe, useRopeOnKeli, useKeyOnDoor, talkToAli, returnToHassan;

	ConditionalStep makeDyedWig, makePaste, makeKeyMould, getKey;

	//Zones
	Zone cell;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupZones();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToHassan);
		steps.put(10, talkToOsman);

		makeDyedWig = new ConditionalStep(this, talkToNed);
		makeDyedWig.addStep(wig.alsoCheckBank(questBank), dyeWig);
		makeDyedWig.setLockingCondition(dyedWig.alsoCheckBank(questBank));

		makePaste = new ConditionalStep(this, talkToAggie);
		makePaste.setLockingCondition(paste.alsoCheckBank(questBank));

		makeKeyMould = new ConditionalStep(this, talkToKeli);
		makeKeyMould.setLockingCondition(hasOrGivenKeyMould);

		getKey = new ConditionalStep(this, bringImprintToOsman);
		getKey.setLockingCondition(givenKeyMould);

		ConditionalStep prepareToSaveAli = new ConditionalStep(this, makeDyedWig);
		prepareToSaveAli.addStep(new Conditions(dyedWig.alsoCheckBank(questBank), paste.alsoCheckBank(questBank),
			new Conditions(LogicType.OR, madeMould, givenKeyMould)), talkToLeela);
		prepareToSaveAli.addStep(new Conditions(dyedWig.alsoCheckBank(questBank), paste.alsoCheckBank(questBank), hasOrGivenKeyMould), getKey);
		prepareToSaveAli.addStep(new Conditions(dyedWig.alsoCheckBank(questBank), paste.alsoCheckBank(questBank)), makeKeyMould);
		prepareToSaveAli.addStep(dyedWig.alsoCheckBank(questBank), makePaste);

		steps.put(20, prepareToSaveAli);

		ConditionalStep getJoeDrunk = new ConditionalStep(this, makeDyedWig);
		getJoeDrunk.addStep(hasWigPasteAndKey, talkToJoe);
		getJoeDrunk.addStep(dyedWig.alsoCheckBank(questBank), makePaste);

		steps.put(30, getJoeDrunk);
		steps.put(31, getJoeDrunk);
		steps.put(32, getJoeDrunk);
		steps.put(33, getJoeDrunk);

		ConditionalStep tieUpKeli = new ConditionalStep(this, makeDyedWig);
		tieUpKeli.addStep(hasWigPasteAndKey, useRopeOnKeli);
		tieUpKeli.addStep(dyedWig.alsoCheckBank(questBank), makePaste);
		steps.put(40, tieUpKeli);

		ConditionalStep freeAli = new ConditionalStep(this, makeDyedWig);
		freeAli.addStep(new Conditions(hasWigPasteAndKey, inCell), talkToAli);
		freeAli.addStep(hasWigPasteAndKey, useKeyOnDoor);
		freeAli.addStep(dyedWig.alsoCheckBank(questBank), makePaste);
		steps.put(50, freeAli);

		steps.put(100, returnToHassan);

		return steps;
	}

	@Override
	public void setupRequirements()
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

		ropeHighlighted = new ItemRequirement("Rope", ItemID.ROPE);
		ropeHighlighted.setHighlightInInventory(true);
		ropeReqs = new ItemRequirement("Rope, or 15 coins / 4 balls of wool to obtain during the quest", ItemID.ROPE);
		coins100 = new ItemRequirement("Coins minimum", ItemCollections.COINS, 100);
		wig = new ItemRequirement("Wig", ItemID.WIG_2421);
		wig.setHighlightInInventory(true);
		dyedWig = new ItemRequirement("Wig (dyed)", ItemID.WIG);
		paste = new ItemRequirement("Paste", ItemID.PASTE);
		keyMould = new ItemRequirement("Key print", ItemID.KEY_PRINT);
		key = new ItemRequirement("Bronze key", ItemID.BRONZE_KEY);
		key.setTooltip("You can get another from Leela for 15 coins");
		yellowDyeReqs = new ItemRequirement("Yellow dye, or 2 onions + 5 coins to obtain during quest", ItemID.YELLOW_DYE);
		glory = new ItemRequirement("Amulet of Glory for Al Kharid and Draynor Village teleports", ItemCollections.AMULET_OF_GLORIES);
	}

	public void setupConditions()
	{
		inCell = new ZoneRequirement(cell);
		hasWigPasteAndKey = new Conditions(dyedWig.alsoCheckBank(questBank), paste.alsoCheckBank(questBank), key.alsoCheckBank(questBank));
		givenKeyMould = new Conditions(true, LogicType.OR,	// TODO quest journal widget text outdated
			new WidgetTextRequirement(119, 3, true, "I have duplicated a key, I need to get it from"),
			new WidgetTextRequirement(119, 3, true, "I got a duplicated cell door key"),
			new WidgetTextRequirement(11, 2, true, "You give Osman the imprint along with a bronze bar."),
			new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "I'll use this to have a copy of the key made. I'll send it<br>to Leela once it's ready."),
			new WidgetTextRequirement(WidgetInfo.DIALOG_PLAYER_TEXT, "I think I have everything needed."),
			key.alsoCheckBank(questBank));
		madeMould = new RuneliteRequirement(getConfigManager(), "princealikeymouldhandedin", "true", givenKeyMould);
		madeMould.initWithValue("false");

		hasOrGivenKeyMould = new Conditions(LogicType.OR, keyMould, givenKeyMould, key.alsoCheckBank(questBank));
	}

	public void setupZones()
	{
		cell = new Zone(new WorldPoint(3121, 3240, 0), new WorldPoint(3125, 3243, 0));
	}

	public void setupSteps()
	{
		talkToHassan = new NpcStep(this, NpcID.CHANCELLOR_HASSAN, new WorldPoint(3298, 3163, 0), "Talk to Hassan in the Al Kharid Palace.");
		talkToHassan.addDialogSteps("Is there anything I can help you with?", "Yes.");
		talkToOsman = new NpcStep(this, NpcID.OSMAN_4286, new WorldPoint(3286, 3180, 0), "Talk to Osman north of the Al Kharid Palace.");

		talkToNed = new NpcStep(this, NpcID.NED, new WorldPoint(3097, 3257, 0), "Have Ned in Draynor Village make you a wig from 3 balls of wool. He can also sell you a rope for 15 coins or 4 balls of wool.", ballsOfWool3);
		talkToNed.addDialogStep("Could you make other things apart from rope?");
		talkToNed.addDialogStep("How about some sort of wig?");
		talkToNed.addDialogStep("I have them here. Please make me a wig.");
		dyeWig = new DetailedQuestStep(this, "Dye the wig with yellow dye. Buy a yellow dye with two onions and 5 coins if you still need it from Aggie in Draynor Village.", yellowDye, wig);
		talkToAggie = new NpcStep(this, NpcID.AGGIE, new WorldPoint(3086, 3257, 0), "Talk to Aggie in Draynor Village to get some paste.", redberries, ashes, potOfFlour, bucketOfWater);
		talkToAggie.addDialogStep("Can you make skin paste?");
		talkToAggie.addDialogStep("Yes please. Mix me some skin paste.");
		talkToKeli = new NpcStep(this, NpcID.LADY_KELI, new WorldPoint(3127, 3244, 0), "Talk to Keli in the jail east of Draynor Village. If you've already made the key mould, open the quest journal to re-sync.", softClay);
		talkToKeli.addDialogStep("Heard of you? You're famous in Gielinor!");
		talkToKeli.addDialogStep("What's your latest plan then?");
		talkToKeli.addDialogStep("How do you know someone won't try to free him?");
		talkToKeli.addDialogStep("Could I see the key please?");
		talkToKeli.addDialogStep("Could I touch the key for a moment please?");
		bringImprintToOsman = new NpcStep(this, NpcID.OSMAN_4286, new WorldPoint(3285, 3179, 0), "Bring the key print to Osman north of the Al Kharid Palace. If you already have, open the quest journal to re-sync.", keyMould, bronzeBar);
		talkToLeela = new NpcStep(this, NpcID.LEELA, new WorldPoint(3113, 3262, 0), "Talk to Leela east of Draynor Village.", beers3, dyedWig, paste, rope, pinkSkirt);
		talkToJoe = new NpcStep(this, NpcID.JOE_11577, new WorldPoint(3124, 3245, 0), "Bring everything to the jail and give Joe there three beers.", beers3, key, dyedWig, paste, rope, pinkSkirt);
		talkToJoe.addDialogStep("I have some beer here. Fancy one?");
		useRopeOnKeli = new NpcStep(this, NpcID.LADY_KELI, new WorldPoint(3127, 3244, 0), "Use rope on Keli.", ropeHighlighted);
		useRopeOnKeli.addIcon(ItemID.ROPE);
		useKeyOnDoor = new ObjectStep(this, ObjectID.PRISON_GATE, new WorldPoint(3123, 3243, 0), "Use the key on the prison door. If Lady Keli respawned you'll need to tie her up again.", key, dyedWig, paste, pinkSkirt);
		useKeyOnDoor.addIcon(ItemID.BRONZE_KEY);
		talkToAli = new NpcStep(this, NpcID.PRINCE_ALI, new WorldPoint(3123, 3240, 0), "Talk to Prince Ali and free him.", key, dyedWig, paste, pinkSkirt);

		returnToHassan = new NpcStep(this, NpcID.CHANCELLOR_HASSAN, new WorldPoint(3298, 3163, 0), "Return to Hassan in the Al Kharid Palace to complete the quest.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
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
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(glory);
		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Able to survive jail guards (level 26) attacking you");
		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(3);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("700 Coins", ItemID.COINS_995, 700));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Free use of the Al Kharid toll gates."),
				new UnlockReward("Access to Sorceress's Garden Minigame (Members)"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToHassan, talkToOsman)));

		PanelDetails makeWigPanel = new PanelDetails("Make a blonde wig", Arrays.asList(talkToNed, dyeWig), yellowDye, ballsOfWool3);
		makeWigPanel.setLockingStep(makeDyedWig);
		allSteps.add(makeWigPanel);

		PanelDetails makePastePanel = new PanelDetails("Make paste", Collections.singletonList(talkToAggie), redberries, ashes, potOfFlour, bucketOfWater);
		makePastePanel.setLockingStep(makePaste);
		allSteps.add(makePastePanel);

		PanelDetails makeKeyMouldPanel = new PanelDetails("Make a key mould", Collections.singletonList(talkToKeli), softClay);
		makeKeyMouldPanel.setLockingStep(makeKeyMould);
		allSteps.add(makeKeyMouldPanel);

		PanelDetails getKeyPanel = new PanelDetails("Make the key", Collections.singletonList(bringImprintToOsman), bronzeBar, keyMould);
		getKeyPanel.setLockingStep(getKey);
		allSteps.add(getKeyPanel);

		allSteps.add(new PanelDetails("Return with the items", Collections.singletonList(talkToLeela), dyedWig, paste, rope, beers3, pinkSkirt));

		allSteps.add(new PanelDetails("Free Ali", Arrays.asList(talkToJoe, useRopeOnKeli, useKeyOnDoor, talkToAli), key, dyedWig, paste, rope, beers3, pinkSkirt));

		allSteps.add(new PanelDetails("Return to Al Kharid", Collections.singletonList(returnToHassan)));
		return allSteps;
	}
}
