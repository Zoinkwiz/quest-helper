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
package com.questhelper.helpers.quests.misthalinmystery;

import com.questhelper.requirements.zone.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.WidgetStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

public class MisthalinMystery extends BasicQuestHelper
{
	//Requirements
	ItemRequirement bucket, manorKey, knife, notes1, rubyKey, tinderbox, notes2, emeraldKey, notes3, sapphireKey, killersKnife, killersKnifeEquipped;

	Requirement onIsland, litCandle1, litCandle2, litCandle3, inOutsideArea, inPianoWidget, playedD, playedE,
		playedA, playedAnyKey, inGemWidget, selectedSaphire, selectedDiamond, selectedZenyte, selectedEmerald, selectedOnyx,
		selectAnyGem, inBossRoom;

	QuestStep talkToAbigale, takeTheBoat, takeTheBucket, searchTheBarrel, useBucketOnBarrel, searchTheBarrelForKey, openManorDoor,
		takeKnife, tryToOpenPinkKnobDoor, takeNote1, readNotes1, useKnifeOnPainting, searchPainting, goThroughRubyDoor, takeTinderbox, lightCandle1, lightCandle2,
		lightCandle3, lightCandle4, lightBarrel, leaveExplosionRoom, climbWall, observeThroughTree, takeNote2, readNotes2, playPiano, playD, playE, playA, playDAgain, restartPiano,
		searchThePiano, returnOverBrokenWall, openEmeraldDoor, enterBandosGodswordRoomStep, takeNote3, readNotes3, useKnifeOnFireplace, searchFireplace, clickSapphire, clickDiamond,
		clickZenyte, clickEmerald, clickOnyx, clickRuby, restartGems, searchFireplaceForSapphireKey, goThroughSapphireDoor, reflectKnives, continueThroughSapphireDoor, watchTheKillersReveal,
		pickUpKillersKnife, fightAbigale, leaveSapphireRoom, talkToMandy;

	//Zones
	private Zone island, outside1, outside2, outside3, bossRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToAbigale);

		steps.put(5, talkToAbigale);

		var investigatingTheBarrel = new ConditionalStep(this, takeTheBoat);
		investigatingTheBarrel.addStep(new Conditions(onIsland, bucket), searchTheBarrel);
		investigatingTheBarrel.addStep(onIsland, takeTheBucket);
		steps.put(10, investigatingTheBarrel);
		steps.put(15, investigatingTheBarrel);

		var emptyTheBarrel = new ConditionalStep(this, takeTheBoat);
		emptyTheBarrel.addStep(new Conditions(onIsland, bucket), useBucketOnBarrel);
		emptyTheBarrel.addStep(onIsland, takeTheBucket);
		steps.put(20, emptyTheBarrel);

		var enterTheHouse = new ConditionalStep(this, takeTheBoat);
		enterTheHouse.addStep(new Conditions(onIsland, manorKey), openManorDoor);
		enterTheHouse.addStep(onIsland, searchTheBarrelForKey);
		steps.put(25, enterTheHouse);

		var pinkDoor = new ConditionalStep(this, takeTheBoat);
		pinkDoor.addStep(knife, tryToOpenPinkKnobDoor);
		pinkDoor.addStep(onIsland, takeKnife);
		steps.put(30, pinkDoor);

		var pickUpAndReadNotes1 = new ConditionalStep(this, takeTheBoat);
		pickUpAndReadNotes1.addStep(new Conditions(onIsland, notes1), readNotes1);
		pickUpAndReadNotes1.addStep(onIsland, takeNote1);
		steps.put(35, pickUpAndReadNotes1);

		var cutPainting = new ConditionalStep(this, takeTheBoat);
		cutPainting.addStep(new Conditions(onIsland, knife), useKnifeOnPainting);
		cutPainting.addStep(onIsland, takeKnife);
		steps.put(40, cutPainting);

		var enterRubyRoom = new ConditionalStep(this, takeTheBoat);
		enterRubyRoom.addStep(new Conditions(onIsland, rubyKey), goThroughRubyDoor);
		enterRubyRoom.addStep(onIsland, searchPainting);
		steps.put(45, enterRubyRoom);

		var lightCandles = new ConditionalStep(this, takeTheBoat);
		lightCandles.addStep(new Conditions(onIsland, tinderbox, litCandle1, litCandle2, litCandle3), lightCandle4);
		lightCandles.addStep(new Conditions(onIsland, tinderbox, litCandle1, litCandle2), lightCandle3);
		lightCandles.addStep(new Conditions(onIsland, tinderbox, litCandle1), lightCandle2);
		lightCandles.addStep(new Conditions(onIsland, tinderbox), lightCandle1);
		lightCandles.addStep(onIsland, takeTinderbox);
		steps.put(50, lightCandles);

		var lightFuseOnBarrel = new ConditionalStep(this, takeTheBoat);
		lightFuseOnBarrel.addStep(new Conditions(onIsland, tinderbox), lightBarrel);
		lightFuseOnBarrel.addStep(onIsland, takeTinderbox);
		steps.put(55, lightFuseOnBarrel);
		steps.put(60, leaveExplosionRoom);

		var goToLacey = new ConditionalStep(this, takeTheBoat);
		goToLacey.addStep(inOutsideArea, observeThroughTree);
		goToLacey.addStep(onIsland, climbWall);
		steps.put(65, goToLacey);

		var pickUpAndReadNotes2 = new ConditionalStep(this, takeTheBoat);
		pickUpAndReadNotes2.addStep(notes2, readNotes2);
		pickUpAndReadNotes2.addStep(inOutsideArea, takeNote2);
		pickUpAndReadNotes2.addStep(onIsland, climbWall);
		steps.put(70, pickUpAndReadNotes2);

		var playMusic = new ConditionalStep(this, takeTheBoat);
		playMusic.addStep(playedA, playDAgain);
		playMusic.addStep(playedE, playA);
		playMusic.addStep(playedD, playE);
		playMusic.addStep(new Conditions(playedAnyKey, inPianoWidget), restartPiano);
		playMusic.addStep(inPianoWidget, playD);
		playMusic.addStep(inOutsideArea, playPiano);
		playMusic.addStep(onIsland, climbWall);
		steps.put(75, playMusic);

		var openingTheEmeraldDoor = new ConditionalStep(this, takeTheBoat);
		openingTheEmeraldDoor.addStep(new Conditions(inOutsideArea, emeraldKey), returnOverBrokenWall);
		openingTheEmeraldDoor.addStep(inOutsideArea, searchThePiano);
		openingTheEmeraldDoor.addStep(new Conditions(onIsland, emeraldKey), openEmeraldDoor);
		openingTheEmeraldDoor.addStep(onIsland, climbWall);
		steps.put(80, openingTheEmeraldDoor);

		var enterBandosGodswordRoom = new ConditionalStep(this, takeTheBoat);
		enterBandosGodswordRoom.addStep(onIsland, enterBandosGodswordRoomStep);
		steps.put(85, enterBandosGodswordRoom);

		var startPuzzle3 = new ConditionalStep(this, takeTheBoat);
		startPuzzle3.addStep(new Conditions(onIsland, notes3), readNotes3);
		startPuzzle3.addStep(onIsland, takeNote3);
		steps.put(90, startPuzzle3);

		var openFireplace = new ConditionalStep(this, takeTheBoat);
		openFireplace.addStep(new Conditions(onIsland, knife), useKnifeOnFireplace);
		openFireplace.addStep(onIsland, takeKnife);
		steps.put(95, openFireplace);

		var solveFireplacePuzzle = new ConditionalStep(this, takeTheBoat);
		solveFireplacePuzzle.addStep(selectedOnyx, clickRuby);
		solveFireplacePuzzle.addStep(selectedEmerald, clickOnyx);
		solveFireplacePuzzle.addStep(selectedZenyte, clickEmerald);
		solveFireplacePuzzle.addStep(selectedDiamond, clickZenyte);
		solveFireplacePuzzle.addStep(selectedSaphire, clickDiamond);
		solveFireplacePuzzle.addStep(selectAnyGem, restartGems);
		solveFireplacePuzzle.addStep(inGemWidget, clickSapphire);
		solveFireplacePuzzle.addStep(onIsland, searchFireplace);
		steps.put(100, solveFireplacePuzzle);

		var openSapphireDoor = new ConditionalStep(this, takeTheBoat);
		openSapphireDoor.addStep(new Conditions(onIsland, sapphireKey), goThroughSapphireDoor);
		openSapphireDoor.addStep(onIsland, searchFireplaceForSapphireKey);
		steps.put(105, openSapphireDoor);

		var goDoBoss = new ConditionalStep(this, takeTheBoat);
		goDoBoss.addStep(inBossRoom, reflectKnives);
		goDoBoss.addStep(onIsland, goThroughSapphireDoor);
		steps.put(110, goDoBoss);
		steps.put(111, goDoBoss);

		var watchRevealCutscene = new ConditionalStep(this, takeTheBoat);
		watchRevealCutscene.addStep(inBossRoom, watchTheKillersReveal);
		watchRevealCutscene.addStep(onIsland, continueThroughSapphireDoor);
		steps.put(115, watchRevealCutscene);

		var goFightAbigale = new ConditionalStep(this, takeTheBoat);
		goFightAbigale.addStep(new Conditions(inBossRoom, killersKnife), fightAbigale);
		goFightAbigale.addStep(inBossRoom, pickUpKillersKnife);
		goFightAbigale.addStep(onIsland, continueThroughSapphireDoor);
		steps.put(120, goFightAbigale);

		var attemptToLeaveSapphireRoom = new ConditionalStep(this, takeTheBoat);
		attemptToLeaveSapphireRoom.addStep(onIsland, leaveSapphireRoom);
		steps.put(125, attemptToLeaveSapphireRoom);

		var finishTheQuest = new ConditionalStep(this, takeTheBoat);
		finishTheQuest.addStep(onIsland, talkToMandy);
		steps.put(130, finishTheQuest);

		return steps;
	}

	@Override
	protected void setupZones()
	{
		island = new Zone(new WorldPoint(1600, 4800, 0), new WorldPoint(1679, 4845, 0));
		outside1 = new Zone(new WorldPoint(1648, 4825, 0), new WorldPoint(1654, 4852, 0));
		outside2 = new Zone(new WorldPoint(1634, 4840, 0), new WorldPoint(1648, 4852, 0));
		outside3 = new Zone(new WorldPoint(1631, 4847, 0), new WorldPoint(1633, 4850, 0));
		bossRoom = new Zone(new WorldPoint(1619, 4825, 0), new WorldPoint(1627, 4834, 0));
	}

	public void setupConditions()
	{
		onIsland = new ZoneRequirement(island);
		inOutsideArea = new ZoneRequirement(outside1, outside2, outside3);

		litCandle1 = new VarbitRequirement(4042, 1);
		litCandle2 = new VarbitRequirement(4041, 1);
		litCandle3 = new VarbitRequirement(4039, 1);

		playedD = new Conditions(new VarbitRequirement(4044, 1), new VarbitRequirement(4049, 1));
		playedE = new Conditions(new VarbitRequirement(4045, 1), new VarbitRequirement(4049, 2));
		playedA = new Conditions(new VarbitRequirement(4046, 1), new VarbitRequirement(4049, 3));
		playedAnyKey = new VarbitRequirement(4049, 1, Operation.GREATER_EQUAL);
		inPianoWidget = new WidgetTextRequirement(554, 20, "C");
		inGemWidget = new WidgetTextRequirement(555, 1, 1, "Gemstone switch panel");
		selectedSaphire = new Conditions(new VarbitRequirement(4051, 1), new VarbitRequirement(4050, 1));
		selectedDiamond = new Conditions(new VarbitRequirement(4052, 1), new VarbitRequirement(4050, 2));
		selectedZenyte = new Conditions(new VarbitRequirement(4053, 1), new VarbitRequirement(4050, 3));
		selectedEmerald = new Conditions(new VarbitRequirement(4054, 1), new VarbitRequirement(4050, 4));
		selectedOnyx = new Conditions(new VarbitRequirement(4055, 1), new VarbitRequirement(4050, 5));
		selectAnyGem = new VarbitRequirement(4050, 1, Operation.GREATER_EQUAL);
		inBossRoom = new ZoneRequirement(bossRoom);
	}

	@Override
	protected void setupRequirements()
	{
		bucket = new ItemRequirement("Bucket", ItemID.BUCKET);
		manorKey = new ItemRequirement("Manor key", ItemID.MANOR_KEY_21052);
		knife = new ItemRequirement("Knife", ItemID.KNIFE);
		notes1 = new ItemRequirement("Notes", ItemID.NOTES_21056);
		rubyKey = new ItemRequirement("Ruby key", ItemID.RUBY_KEY_21053);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		notes2 = new ItemRequirement("Notes", ItemID.NOTES_21057);
		emeraldKey = new ItemRequirement("Emerald key", ItemID.EMERALD_KEY_21054);
		notes3 = new ItemRequirement("Notes", ItemID.NOTES_21058);
		sapphireKey = new ItemRequirement("Sapphire key", ItemID.SAPPHIRE_KEY_21055);
		killersKnife = new ItemRequirement("Killer's knife", ItemID.KILLERS_KNIFE_21059);
		killersKnifeEquipped = new ItemRequirement("Killer's knife", ItemID.KILLERS_KNIFE_21059, 1, true);
	}

	public void setupSteps()
	{
		// TODO: Should this implement PuzzleWrapper?
		talkToAbigale = new NpcStep(this, NpcID.ABIGALE, new WorldPoint(3237, 3155, 0), "Talk to Abigale in the south east corner of Lumbridge Swamp.");
		talkToAbigale.addDialogStep("Yes.");
		takeTheBoat = new ObjectStep(this, ObjectID.ROWBOAT_30108, new WorldPoint(3240, 3140, 0), "Board the rowboat south of Abigale.");
		takeTheBucket = new ObjectStep(this, ObjectID.BUCKET_30147, new WorldPoint(1619, 4816, 0), "Pick up the bucket near the fountain.", bucket);
		searchTheBarrel = new ObjectStep(this, NullObjectID.NULL_29649, new WorldPoint(1615, 4829, 0), "Search the barrel of rainwater north of the fountain to trigger a cutscene.", bucket);
		useBucketOnBarrel = new ObjectStep(this, NullObjectID.NULL_29649, new WorldPoint(1615, 4829, 0), "Use the bucket on the barrel of rainwater.", bucket);
		searchTheBarrelForKey = new ObjectStep(this, NullObjectID.NULL_29649, new WorldPoint(1615, 4829, 0), "Search the barrel of rainwater for the manor key.");
		useBucketOnBarrel.addIcon(ItemID.BUCKET);
		openManorDoor = new ObjectStep(this, ObjectID.LARGE_DOOR_30110, new WorldPoint(1636, 4824, 0), "Enter the manor.", true);
		((ObjectStep) openManorDoor).addAlternateObjects(ObjectID.LARGE_DOOR_30111);
		takeKnife = new ObjectStep(this, ObjectID.TABLE_30145, new WorldPoint(1639, 4831, 0), "Take the knife from the table.", knife);
		tryToOpenPinkKnobDoor = new ObjectStep(this, ObjectID.DOOR_30112, new WorldPoint(1635, 4838, 0), "Try to open the door with the pink handle.");
		takeNote1 = new ObjectStep(this, NullObjectID.NULL_2266, new WorldPoint(1635, 4839, 0), "Pick up the note that appeared.");
		readNotes1 = new DetailedQuestStep(this, "Read the notes.", notes1.highlighted());
		useKnifeOnPainting = new ObjectStep(this, NullObjectID.NULL_29650, new WorldPoint(1632, 4833, 0), "Use a knife on the marked painting.", knife);
		useKnifeOnPainting.addIcon(ItemID.KNIFE);
		searchPainting = new ObjectStep(this, NullObjectID.NULL_29650, new WorldPoint(1632, 4833, 0), "Search the painting for a ruby key.");
		goThroughRubyDoor = new ObjectStep(this, ObjectID.DOOR_30116, new WorldPoint(1640, 4828, 0), "Go through the door with the ruby handle.", rubyKey);
		takeTinderbox = new ObjectStep(this, ObjectID.SHELVES_30146, new WorldPoint(1646, 4826, 0), "Search the shelves for a tinderbox.");

		lightCandle1 = new ObjectStep(this, NullObjectID.NULL_29655, new WorldPoint(1641, 4826, 0), "Light the unlit candles in the room.", tinderbox.highlighted());
		lightCandle1.addIcon(ItemID.TINDERBOX);
		lightCandle2 = new ObjectStep(this, NullObjectID.NULL_29654, new WorldPoint(1647, 4827, 0), "Light the unlit candles in the room.", tinderbox.highlighted());
		lightCandle2.addIcon(ItemID.TINDERBOX);
		lightCandle3 = new ObjectStep(this, NullObjectID.NULL_29652, new WorldPoint(1641, 4831, 0), "Light the unlit candles in the room.", tinderbox.highlighted());
		lightCandle3.addIcon(ItemID.TINDERBOX);
		lightCandle4 = new ObjectStep(this, NullObjectID.NULL_29653, new WorldPoint(1646, 4832, 0), "Light the unlit candles in the room.", tinderbox.highlighted());
		lightCandle4.addIcon(ItemID.TINDERBOX);
		lightBarrel = new ObjectStep(this, NullObjectID.NULL_29651, new WorldPoint(1647, 4830, 0), "Light the fuse on the barrel.", tinderbox.highlighted());
		lightBarrel.addIcon(ItemID.TINDERBOX);
		leaveExplosionRoom = new ObjectStep(this, ObjectID.DOOR_30116, new WorldPoint(1640, 4828, 0), "Leave the room to trigger the explosion.");

		lightCandle1.addSubSteps(lightCandle2, lightCandle3, lightCandle4);

		climbWall = new ObjectStep(this, NullObjectID.NULL_29657, new WorldPoint(1648, 4829, 0), "Climb over the damaged wall.");
		observeThroughTree = new ObjectStep(this, ObjectID.DEAD_TREE_30150, new WorldPoint(1630, 4849, 0), "Observe Lacey through the trees.");

		takeNote2 = new ObjectStep(this, NullObjectID.NULL_2267, new WorldPoint(1632, 4850, 0), "Pick up the note that appeared by the fence.");
		readNotes2 = new DetailedQuestStep(this, "Read the notes.", notes2.highlighted());

		playPiano = new ObjectStep(this, NullObjectID.NULL_29658, new WorldPoint(1647, 4842, 0), "Play the piano in the room to the south.");
		playD = new WidgetStep(this, "Play the D key.", 554, 21);
		playE = new WidgetStep(this, "Play the E key.", 554, 22);
		playA = new WidgetStep(this, "Play the A key.", 554, 25);
		playDAgain = new WidgetStep(this, "Play the D key again.", 554, 21);
		restartPiano = new DetailedQuestStep(this, "Unfortunately you've played an incorrect key. Restart.");

		searchThePiano = new ObjectStep(this, NullObjectID.NULL_29658, new WorldPoint(1647, 4842, 0), "Right-click search the piano for the emerald key.");

		returnOverBrokenWall = new ObjectStep(this, NullObjectID.NULL_29657, new WorldPoint(1648, 4829, 0),
			"Climb back over the damaged wall into the manor.", emeraldKey);
		openEmeraldDoor = new ObjectStep(this, ObjectID.DOOR_30117, new WorldPoint(1633, 4837, 0), "Go through the door with the green handle.", emeraldKey);

		enterBandosGodswordRoomStep = new ObjectStep(this, ObjectID.DOOR_30118, new WorldPoint(1629, 4842, 0),
			"Try to enter the room containing the Bandos godsword.");

		takeNote3 = new ObjectStep(this, NullObjectID.NULL_29648, new WorldPoint(1630, 4842, 0), "Pick up the note that appeared by the door.");
		readNotes3 = new DetailedQuestStep(this, "Read the notes.", notes3.highlighted());
		useKnifeOnFireplace = new ObjectStep(this, NullObjectID.NULL_29659, new WorldPoint(1647, 4836, 0), "Use a knife on the unlit fireplace in the eastern room.", knife);
		useKnifeOnFireplace.addIcon(ItemID.KNIFE);

		searchFireplace = new ObjectStep(this, NullObjectID.NULL_29659, new WorldPoint(1647, 4836, 0), "Search the fireplace.");

		restartGems = new DetailedQuestStep(this, "You've clicked a gem in the wrong order. Try restarting.");

		clickSapphire = new WidgetStep(this, "Click the sapphire.", 555, 19);
		clickDiamond = new WidgetStep(this, "Click the diamond.", 555, 4);
		clickZenyte = new WidgetStep(this, "Click the zenyte.", 555, 11);
		clickEmerald = new WidgetStep(this, "Click the emerald.", 555, 23);
		clickOnyx = new WidgetStep(this, "Click the onyx.", 555, 7);
		clickRuby = new WidgetStep(this, "Click the ruby.", 555, 15);

		searchFireplaceForSapphireKey = new ObjectStep(this, NullObjectID.NULL_29659, new WorldPoint(1647, 4836, 0), "Search the fireplace again for the sapphire key.");
		goThroughSapphireDoor = new ObjectStep(this, ObjectID.DOOR_30119, new WorldPoint(1628, 4829, 0),
			"Go through the sapphire door.");
		reflectKnives = new DetailedQuestStep(this, "This puzzle requires you to move the mirror to reflect the knives the murderer throws. You can tell which wardrobe the murderer will throw from by a black swirl that'll surround it.");

		continueThroughSapphireDoor = new ObjectStep(this, ObjectID.DOOR_30119, new WorldPoint(1628, 4829, 0),
			"Go through the sapphire door to continue.");
		goThroughSapphireDoor.addSubSteps(continueThroughSapphireDoor);

		watchTheKillersReveal = new DetailedQuestStep(this, "Watch the killer's reveal cutscene.");

		pickUpKillersKnife = new DetailedQuestStep(this, "Pick up the killer's knife.", killersKnifeEquipped);

		fightAbigale = new NpcStep(this, NpcID.ABIGALE_7635, new WorldPoint(1623, 4829, 0),
			"Equip the killer's knife, then select Fight on Abigale (no actual combat will occur).", killersKnifeEquipped);

		leaveSapphireRoom = new ObjectStep(this, ObjectID.DOOR_30119, new WorldPoint(1628, 4829, 0),
			"Attempt to go through the sapphire door.");

		talkToMandy = new NpcStep(this, NpcID.MANDY_7630, new WorldPoint(1636, 4817, 0),
			"Talk to Mandy just outside the manor to complete the quest.");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.CRAFTING, 600)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Uncut Ruby", ItemID.UNCUT_RUBY, 1),
			new ItemReward("Uncut Emerald", ItemID.UNCUT_EMERALD, 1),
			new ItemReward("Uncut Sapphire", ItemID.UNCUT_SAPPHIRE, 1)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var allSteps = new ArrayList<PanelDetails>();

		allSteps.add(new PanelDetails("Talk to Abigale", Collections.singletonList(talkToAbigale)));
		allSteps.add(new PanelDetails("Enter the manor", Arrays.asList(takeTheBoat, takeTheBucket, searchTheBarrel, useBucketOnBarrel, searchTheBarrelForKey, openManorDoor)));
		allSteps.add(new PanelDetails("Solve the first puzzle", Arrays.asList(takeKnife, tryToOpenPinkKnobDoor, takeNote1, readNotes1, useKnifeOnPainting, searchPainting, goThroughRubyDoor)));
		allSteps.add(new PanelDetails("Solve the second puzzle", Arrays.asList(takeTinderbox, lightCandle1, lightBarrel, leaveExplosionRoom, climbWall)));
		allSteps.add(new PanelDetails("Solve the third puzzle", Arrays.asList(observeThroughTree, takeNote2, readNotes2, playPiano, playD, playE, playA, playDAgain, searchThePiano)));
		allSteps.add(new PanelDetails("Witness another murder", Arrays.asList(returnOverBrokenWall, openEmeraldDoor, enterBandosGodswordRoomStep)));
		allSteps.add(new PanelDetails("Solve the fourth puzzle", Arrays.asList(takeNote3, readNotes3, useKnifeOnFireplace, searchFireplace, clickSapphire, clickDiamond, clickZenyte, clickEmerald, clickOnyx, clickRuby, searchFireplaceForSapphireKey)));
		allSteps.add(new PanelDetails("Confront the killer", Arrays.asList(goThroughSapphireDoor, reflectKnives, watchTheKillersReveal, pickUpKillersKnife, fightAbigale, leaveSapphireRoom, talkToMandy)));

		return allSteps;
	}
}
