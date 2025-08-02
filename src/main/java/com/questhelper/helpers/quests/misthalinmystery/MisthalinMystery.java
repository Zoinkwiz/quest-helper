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

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

public class MisthalinMystery extends BasicQuestHelper
{
	// Zones
	Zone island;
	Zone outside1;
	Zone outside2;
	Zone outside3;
	Zone bossRoom;

	// Miscellaneous requirements
	ItemRequirement bucket;
	ItemRequirement manorKey;
	ItemRequirement knife;
	ItemRequirement notes1;
	ItemRequirement rubyKey;
	ItemRequirement tinderbox;
	ItemRequirement notes2;
	ItemRequirement emeraldKey;
	ItemRequirement notes3;
	ItemRequirement sapphireKey;
	ItemRequirement killersKnife;
	ItemRequirement killersKnifeEquipped;
	ZoneRequirement onIsland;
	ZoneRequirement inOutsideArea;
	ZoneRequirement inBossRoom;
	VarbitRequirement litCandle1;
	VarbitRequirement litCandle2;
	VarbitRequirement litCandle3;
	WidgetTextRequirement inPianoWidget;
	Conditions playedD;
	Conditions playedE;
	Conditions playedA;
	VarbitRequirement playedAnyKey;
	WidgetTextRequirement inGemWidget;
	Conditions selectedSaphire;
	Conditions selectedDiamond;
	Conditions selectedZenyte;
	Conditions selectedEmerald;
	Conditions selectedOnyx;
	VarbitRequirement selectAnyGem;

	// Steps
	NpcStep talkToAbigale;
	ObjectStep takeTheBoat;
	ObjectStep takeTheBucket;
	ObjectStep searchTheBarrel;
	ObjectStep useBucketOnBarrel;
	ObjectStep searchTheBarrelForKey;
	ObjectStep openManorDoor;
	ObjectStep takeKnife;
	ObjectStep tryToOpenPinkKnobDoor;
	ObjectStep takeNote1;
	DetailedQuestStep readNotes1;
	ObjectStep useKnifeOnPainting;
	ObjectStep searchPainting;
	ObjectStep goThroughRubyDoor;
	ObjectStep takeTinderbox;
	ObjectStep lightCandle1;
	ObjectStep lightCandle2;
	ObjectStep lightCandle3;
	ObjectStep lightCandle4;
	ObjectStep lightBarrel;
	ObjectStep leaveExplosionRoom;
	ObjectStep climbWall;
	ObjectStep observeThroughTree;
	ObjectStep takeNote2;
	DetailedQuestStep readNotes2;
	ObjectStep playPiano;
	WidgetStep playD;
	WidgetStep playE;
	WidgetStep playA;
	WidgetStep playDAgain;
	DetailedQuestStep restartPiano;
	ObjectStep searchThePiano;
	ObjectStep returnOverBrokenWall;
	ObjectStep openEmeraldDoor;
	ObjectStep enterBandosGodswordRoomStep;
	ObjectStep takeNote3;
	DetailedQuestStep readNotes3;
	ObjectStep useKnifeOnFireplace;
	ObjectStep searchFireplace;
	WidgetStep clickSapphire;
	WidgetStep clickDiamond;
	WidgetStep clickZenyte;
	WidgetStep clickEmerald;
	WidgetStep clickOnyx;
	WidgetStep clickRuby;
	DetailedQuestStep restartGems;
	ObjectStep searchFireplaceForSapphireKey;
	ObjectStep goThroughSapphireDoor;
	DetailedQuestStep reflectKnives;
	ObjectStep continueThroughSapphireDoor;
	DetailedQuestStep watchTheKillersReveal;
	DetailedQuestStep pickUpKillersKnife;
	NpcStep fightAbigale;
	ObjectStep leaveSapphireRoom;
	NpcStep talkToMandy;

	@Override
	protected void setupZones()
	{
		island = new Zone(new WorldPoint(1600, 4800, 0), new WorldPoint(1679, 4845, 0));
		outside1 = new Zone(new WorldPoint(1648, 4825, 0), new WorldPoint(1654, 4852, 0));
		outside2 = new Zone(new WorldPoint(1634, 4840, 0), new WorldPoint(1648, 4852, 0));
		outside3 = new Zone(new WorldPoint(1631, 4847, 0), new WorldPoint(1633, 4850, 0));
		bossRoom = new Zone(new WorldPoint(1619, 4825, 0), new WorldPoint(1627, 4834, 0));
	}

	@Override
	protected void setupRequirements()
	{
		onIsland = new ZoneRequirement(island);
		inOutsideArea = new ZoneRequirement(outside1, outside2, outside3);
		inBossRoom = new ZoneRequirement(bossRoom);

		litCandle1 = new VarbitRequirement(4042, 1);
		litCandle2 = new VarbitRequirement(4041, 1);
		litCandle3 = new VarbitRequirement(4039, 1);

		playedD = and(new VarbitRequirement(4044, 1), new VarbitRequirement(4049, 1));
		playedE = and(new VarbitRequirement(4045, 1), new VarbitRequirement(4049, 2));
		playedA = and(new VarbitRequirement(4046, 1), new VarbitRequirement(4049, 3));
		playedAnyKey = new VarbitRequirement(VarbitID.MISTMYST_PIANO_ATTEMPTS, 1, Operation.GREATER_EQUAL);
		inPianoWidget = new WidgetTextRequirement(554, 20, "C");
		inGemWidget = new WidgetTextRequirement(555, 1, 1, "Gemstone switch panel");
		selectedSaphire = and(new VarbitRequirement(4051, 1), new VarbitRequirement(4050, 1));
		selectedDiamond = and(new VarbitRequirement(4052, 1), new VarbitRequirement(4050, 2));
		selectedZenyte = and(new VarbitRequirement(4053, 1), new VarbitRequirement(4050, 3));
		selectedEmerald = and(new VarbitRequirement(4054, 1), new VarbitRequirement(4050, 4));
		selectedOnyx = and(new VarbitRequirement(4055, 1), new VarbitRequirement(4050, 5));
		selectAnyGem = new VarbitRequirement(VarbitID.MISTMYST_SWITCH_ATTEMPTS, 1, Operation.GREATER_EQUAL);

		bucket = new ItemRequirement("Bucket", ItemID.BUCKET_EMPTY);
		manorKey = new ItemRequirement("Manor key", ItemID.MISTMYST_FRONTDOOR_KEY);
		knife = new ItemRequirement("Knife", ItemID.KNIFE);
		notes1 = new ItemRequirement("Notes", ItemID.MISTMYST_CLUE_LIBRARY);
		rubyKey = new ItemRequirement("Ruby key", ItemID.MISTMYST_RUBY_KEY);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		notes2 = new ItemRequirement("Notes", ItemID.MISTMYST_CLUE_OUTSIDE);
		emeraldKey = new ItemRequirement("Emerald key", ItemID.MISTMYST_EMERALD_KEY);
		notes3 = new ItemRequirement("Notes", ItemID.MISTMYST_CLUE_KITCHEN);
		sapphireKey = new ItemRequirement("Sapphire key", ItemID.MISTMYST_SAPPHIRE_KEY);
		killersKnife = new ItemRequirement("Killer's knife", ItemID.MISTMYST_CUTSCENE_KNIFE);
		killersKnifeEquipped = new ItemRequirement("Killer's knife", ItemID.MISTMYST_CUTSCENE_KNIFE, 1, true);
	}

	public void setupSteps()
	{
		// TODO: Should this implement PuzzleWrapper?
		talkToAbigale = new NpcStep(this, NpcID.MISTMYST_ABIGALE_LUM_VIS, new WorldPoint(3237, 3155, 0), "Talk to Abigale in the south east corner of Lumbridge Swamp.");
		talkToAbigale.addDialogStep("Yes.");
		takeTheBoat = new ObjectStep(this, ObjectID.MISTMYST_BOAT_LUMBRIDGE, new WorldPoint(3240, 3140, 0), "Board the rowboat south of Abigale.");
		takeTheBucket = new ObjectStep(this, ObjectID.MISTMYST_EMPTY_BUCKET, new WorldPoint(1619, 4816, 0), "Pick up the bucket near the fountain.", bucket);
		searchTheBarrel = new ObjectStep(this, ObjectID.MISTMYST_BARREL, new WorldPoint(1615, 4829, 0), "Search the barrel of rainwater north of the fountain to trigger a cutscene.", bucket);
		useBucketOnBarrel = new ObjectStep(this, ObjectID.MISTMYST_BARREL, new WorldPoint(1615, 4829, 0), "Use the bucket on the barrel of rainwater.", bucket);
		searchTheBarrelForKey = new ObjectStep(this, ObjectID.MISTMYST_BARREL, new WorldPoint(1615, 4829, 0), "Search the barrel of rainwater for the manor key.");
		useBucketOnBarrel.addIcon(ItemID.BUCKET_EMPTY);
		openManorDoor = new ObjectStep(this, ObjectID.MISTMYST_FRONT_DOORL, new WorldPoint(1636, 4824, 0), "Enter the manor.", true);
		openManorDoor.addAlternateObjects(ObjectID.MISTMYST_FRONT_DOORR);
		takeKnife = new ObjectStep(this, ObjectID.MISTMYST_TABLE_KNIFE, new WorldPoint(1639, 4831, 0), "Take the knife from the table.", knife);
		tryToOpenPinkKnobDoor = new ObjectStep(this, ObjectID.MISTMYST_DOOR_REDTOPAZ, new WorldPoint(1635, 4838, 0), "Try to open the door with the pink handle.");
		takeNote1 = new ObjectStep(this, ObjectID.MISTMYST_CLUE_LIBRARY, new WorldPoint(1635, 4839, 0), "Pick up the note that appeared.");
		readNotes1 = new DetailedQuestStep(this, "Read the notes.", notes1.highlighted());
		useKnifeOnPainting = new ObjectStep(this, ObjectID.MISTMYST_PAINTING, new WorldPoint(1632, 4833, 0), "Use a knife on the marked painting.", knife);
		useKnifeOnPainting.addIcon(ItemID.KNIFE);
		searchPainting = new ObjectStep(this, ObjectID.MISTMYST_PAINTING, new WorldPoint(1632, 4833, 0), "Search the painting for a ruby key.");
		goThroughRubyDoor = new ObjectStep(this, ObjectID.MISTMYST_DOOR_RUBY, new WorldPoint(1640, 4828, 0), "Go through the door with the ruby handle.", rubyKey);
		takeTinderbox = new ObjectStep(this, ObjectID.MISTMYST_SHELVES_TINDERBOX, new WorldPoint(1646, 4826, 0), "Search the shelves for a tinderbox.");

		lightCandle1 = new ObjectStep(this, ObjectID.MISTMYST_CANDLE4, new WorldPoint(1641, 4826, 0), "Light the unlit candles in the room.", tinderbox.highlighted());
		lightCandle1.addIcon(ItemID.TINDERBOX);
		lightCandle2 = new ObjectStep(this, ObjectID.MISTMYST_CANDLE3, new WorldPoint(1647, 4827, 0), "Light the unlit candles in the room.", tinderbox.highlighted());
		lightCandle2.addIcon(ItemID.TINDERBOX);
		lightCandle3 = new ObjectStep(this, ObjectID.MISTMYST_CANDLE1, new WorldPoint(1641, 4831, 0), "Light the unlit candles in the room.", tinderbox.highlighted());
		lightCandle3.addIcon(ItemID.TINDERBOX);
		lightCandle4 = new ObjectStep(this, ObjectID.MISTMYST_CANDLE2, new WorldPoint(1646, 4832, 0), "Light the unlit candles in the room.", tinderbox.highlighted());
		lightCandle4.addIcon(ItemID.TINDERBOX);
		lightBarrel = new ObjectStep(this, ObjectID.MISTMYST_EXPLOSIVE_BARREL, new WorldPoint(1647, 4830, 0), "Light the fuse on the barrel.", tinderbox.highlighted());
		lightBarrel.addIcon(ItemID.TINDERBOX);
		leaveExplosionRoom = new ObjectStep(this, ObjectID.MISTMYST_DOOR_RUBY, new WorldPoint(1640, 4828, 0), "Leave the room to trigger the explosion.");

		lightCandle1.addSubSteps(lightCandle2, lightCandle3, lightCandle4);

		climbWall = new ObjectStep(this, ObjectID.MISTMYST_DESTRUCTABLE_WALL_CLIMBABLE, new WorldPoint(1648, 4829, 0), "Climb over the damaged wall.");
		observeThroughTree = new ObjectStep(this, ObjectID.MISTMYST_TREE, new WorldPoint(1630, 4849, 0), "Observe Lacey through the trees.");

		takeNote2 = new ObjectStep(this, ObjectID.MISTMYST_CLUE_OUTSIDE, new WorldPoint(1632, 4850, 0), "Pick up the note that appeared by the fence.");
		readNotes2 = new DetailedQuestStep(this, "Read the notes.", notes2.highlighted());

		playPiano = new ObjectStep(this, ObjectID.MISTMYST_PIANO, new WorldPoint(1647, 4842, 0), "Play the piano in the room to the south.");
		playD = new WidgetStep(this, "Play the D key.", 554, 21);
		playE = new WidgetStep(this, "Play the E key.", 554, 22);
		playA = new WidgetStep(this, "Play the A key.", 554, 25);
		playDAgain = new WidgetStep(this, "Play the D key again.", 554, 21);
		restartPiano = new DetailedQuestStep(this, "Unfortunately you've played an incorrect key. Restart.");
		playPiano.addSubSteps(restartPiano);

		searchThePiano = new ObjectStep(this, ObjectID.MISTMYST_PIANO, new WorldPoint(1647, 4842, 0), "Right-click search the piano for the emerald key.");

		returnOverBrokenWall = new ObjectStep(this, ObjectID.MISTMYST_DESTRUCTABLE_WALL_CLIMBABLE, new WorldPoint(1648, 4829, 0),
			"Climb back over the damaged wall into the manor.", emeraldKey);
		openEmeraldDoor = new ObjectStep(this, ObjectID.MISTMYST_DOOR_EMERALD, new WorldPoint(1633, 4837, 0), "Go through the door with the green handle.", emeraldKey);

		enterBandosGodswordRoomStep = new ObjectStep(this, ObjectID.MISTMYST_DOOR_DIAMOND, new WorldPoint(1629, 4842, 0),
			"Try to enter the room containing the Bandos godsword.");

		takeNote3 = new ObjectStep(this, ObjectID.MISTMYST_CLUE_KITCHEN, new WorldPoint(1630, 4842, 0), "Pick up the note that appeared by the door.");
		readNotes3 = new DetailedQuestStep(this, "Read the notes.", notes3.highlighted());
		useKnifeOnFireplace = new ObjectStep(this, ObjectID.MISTMYST_FIREPLACE, new WorldPoint(1647, 4836, 0), "Use a knife on the unlit fireplace in the eastern room.", knife);
		useKnifeOnFireplace.addIcon(ItemID.KNIFE);

		searchFireplace = new ObjectStep(this, ObjectID.MISTMYST_FIREPLACE, new WorldPoint(1647, 4836, 0), "Search the fireplace.");

		restartGems = new DetailedQuestStep(this, "You've clicked a gem in the wrong order. Try restarting.");
		searchFireplace.addSubSteps(restartGems);

		clickSapphire = new WidgetStep(this, "Click the sapphire.", 555, 19);
		clickDiamond = new WidgetStep(this, "Click the diamond.", 555, 4);
		clickZenyte = new WidgetStep(this, "Click the zenyte.", 555, 11);
		clickEmerald = new WidgetStep(this, "Click the emerald.", 555, 23);
		clickOnyx = new WidgetStep(this, "Click the onyx.", 555, 7);
		clickRuby = new WidgetStep(this, "Click the ruby.", 555, 15);

		searchFireplaceForSapphireKey = new ObjectStep(this, ObjectID.MISTMYST_FIREPLACE, new WorldPoint(1647, 4836, 0), "Search the fireplace again for the sapphire key.");
		goThroughSapphireDoor = new ObjectStep(this, ObjectID.MISTMYST_DOOR_SAPPHIRE, new WorldPoint(1628, 4829, 0),
			"Go through the sapphire door.");
		reflectKnives = new DetailedQuestStep(this, "This puzzle requires you to move the mirror to reflect the knives the murderer throws. You can tell which wardrobe the murderer will throw from by a black swirl that'll surround it.");

		continueThroughSapphireDoor = new ObjectStep(this, ObjectID.MISTMYST_DOOR_SAPPHIRE, new WorldPoint(1628, 4829, 0),
			"Go through the sapphire door to continue.");
		goThroughSapphireDoor.addSubSteps(continueThroughSapphireDoor);

		watchTheKillersReveal = new DetailedQuestStep(this, "Watch the killer's reveal cutscene.");

		pickUpKillersKnife = new DetailedQuestStep(this, "Pick up the killer's knife.", killersKnifeEquipped);

		fightAbigale = new NpcStep(this, NpcID.MISTMYST_ABIGALE_KILLER_ATTACKABLE, new WorldPoint(1623, 4829, 0),
			"Equip the killer's knife, then select Fight on Abigale (no actual combat will occur).", killersKnifeEquipped);

		leaveSapphireRoom = new ObjectStep(this, ObjectID.MISTMYST_DOOR_SAPPHIRE, new WorldPoint(1628, 4829, 0),
			"Attempt to go through the sapphire door.");

		talkToMandy = new NpcStep(this, NpcID.MISTMYST_MANDY_POST_VIS, new WorldPoint(1636, 4817, 0),
			"Talk to Mandy just outside the manor to complete the quest.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToAbigale);

		steps.put(5, talkToAbigale);

		var investigatingTheBarrel = new ConditionalStep(this, takeTheBoat);
		investigatingTheBarrel.addStep(and(onIsland, bucket), searchTheBarrel);
		investigatingTheBarrel.addStep(onIsland, takeTheBucket);
		steps.put(10, investigatingTheBarrel);
		steps.put(15, investigatingTheBarrel);

		var emptyTheBarrel = new ConditionalStep(this, takeTheBoat);
		emptyTheBarrel.addStep(and(onIsland, bucket), useBucketOnBarrel);
		emptyTheBarrel.addStep(onIsland, takeTheBucket);
		steps.put(20, emptyTheBarrel);

		var enterTheHouse = new ConditionalStep(this, takeTheBoat);
		enterTheHouse.addStep(and(onIsland, manorKey), openManorDoor);
		enterTheHouse.addStep(onIsland, searchTheBarrelForKey);
		steps.put(25, enterTheHouse);

		var pinkDoor = new ConditionalStep(this, takeTheBoat);
		pinkDoor.addStep(knife, tryToOpenPinkKnobDoor);
		pinkDoor.addStep(onIsland, takeKnife);
		steps.put(30, pinkDoor);

		var pickUpAndReadNotes1 = new ConditionalStep(this, takeTheBoat);
		pickUpAndReadNotes1.addStep(and(onIsland, notes1), readNotes1);
		pickUpAndReadNotes1.addStep(onIsland, takeNote1);
		steps.put(35, pickUpAndReadNotes1);

		var cutPainting = new ConditionalStep(this, takeTheBoat);
		cutPainting.addStep(and(onIsland, knife), useKnifeOnPainting);
		cutPainting.addStep(onIsland, takeKnife);
		steps.put(40, cutPainting);

		var enterRubyRoom = new ConditionalStep(this, takeTheBoat);
		enterRubyRoom.addStep(and(onIsland, rubyKey), goThroughRubyDoor);
		enterRubyRoom.addStep(onIsland, searchPainting);
		steps.put(45, enterRubyRoom);

		var lightCandles = new ConditionalStep(this, takeTheBoat);
		lightCandles.addStep(and(onIsland, tinderbox, litCandle1, litCandle2, litCandle3), lightCandle4);
		lightCandles.addStep(and(onIsland, tinderbox, litCandle1, litCandle2), lightCandle3);
		lightCandles.addStep(and(onIsland, tinderbox, litCandle1), lightCandle2);
		lightCandles.addStep(and(onIsland, tinderbox), lightCandle1);
		lightCandles.addStep(onIsland, takeTinderbox);
		steps.put(50, lightCandles);

		var lightFuseOnBarrel = new ConditionalStep(this, takeTheBoat);
		lightFuseOnBarrel.addStep(and(onIsland, tinderbox), lightBarrel);
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
		playMusic.addStep(and(playedAnyKey, inPianoWidget), restartPiano);
		playMusic.addStep(inPianoWidget, playD);
		playMusic.addStep(inOutsideArea, playPiano);
		playMusic.addStep(onIsland, climbWall);
		steps.put(75, playMusic);

		var openingTheEmeraldDoor = new ConditionalStep(this, takeTheBoat);
		openingTheEmeraldDoor.addStep(and(inOutsideArea, emeraldKey), returnOverBrokenWall);
		openingTheEmeraldDoor.addStep(inOutsideArea, searchThePiano);
		openingTheEmeraldDoor.addStep(and(onIsland, emeraldKey), openEmeraldDoor);
		openingTheEmeraldDoor.addStep(onIsland, climbWall);
		steps.put(80, openingTheEmeraldDoor);

		var enterBandosGodswordRoom = new ConditionalStep(this, takeTheBoat);
		enterBandosGodswordRoom.addStep(onIsland, enterBandosGodswordRoomStep);
		steps.put(85, enterBandosGodswordRoom);

		var startPuzzle3 = new ConditionalStep(this, takeTheBoat);
		startPuzzle3.addStep(and(onIsland, notes3), readNotes3);
		startPuzzle3.addStep(onIsland, takeNote3);
		steps.put(90, startPuzzle3);

		var openFireplace = new ConditionalStep(this, takeTheBoat);
		openFireplace.addStep(and(onIsland, knife), useKnifeOnFireplace);
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
		openSapphireDoor.addStep(and(onIsland, sapphireKey), goThroughSapphireDoor);
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
		goFightAbigale.addStep(and(inBossRoom, killersKnife), fightAbigale);
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
		var steps = new ArrayList<PanelDetails>();

		steps.add(new PanelDetails("Talk to Abigale", List.of(
			talkToAbigale
		)));

		steps.add(new PanelDetails("Enter the manor", List.of(
			takeTheBoat,
			takeTheBucket,
			searchTheBarrel,
			useBucketOnBarrel,
			searchTheBarrelForKey,
			openManorDoor
		)));

		steps.add(new PanelDetails("Solve the first puzzle", List.of(
			takeKnife,
			tryToOpenPinkKnobDoor,
			takeNote1,
			readNotes1,
			useKnifeOnPainting,
			searchPainting,
			goThroughRubyDoor
		)));

		steps.add(new PanelDetails("Solve the second puzzle", List.of(
			takeTinderbox,
			lightCandle1,
			lightBarrel,
			leaveExplosionRoom,
			climbWall
		)));

		steps.add(new PanelDetails("Solve the third puzzle", List.of(
			observeThroughTree,
			takeNote2,
			readNotes2,
			playPiano,
			playD,
			playE,
			playA,
			playDAgain,
			searchThePiano
		)));

		steps.add(new PanelDetails("Witness another murder", List.of(
			returnOverBrokenWall,
			openEmeraldDoor,
			enterBandosGodswordRoomStep
		)));

		steps.add(new PanelDetails("Solve the fourth puzzle", List.of(
			takeNote3,
			readNotes3,
			useKnifeOnFireplace,
			searchFireplace,
			clickSapphire,
			clickDiamond,
			clickZenyte,
			clickEmerald,
			clickOnyx,
			clickRuby,
			searchFireplaceForSapphireKey
		)));

		steps.add(new PanelDetails("Confront the killer", List.of(
			goThroughSapphireDoor,
			reflectKnives,
			watchTheKillersReveal,
			pickUpKillersKnife,
			fightAbigale,
			leaveSapphireRoom,
			talkToMandy
		)));

		return steps;
	}
}
