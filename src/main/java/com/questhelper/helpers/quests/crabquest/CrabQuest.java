package com.questhelper.helpers.quests.crabquest;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.EmoteStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.emote.QuestEmote;
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


public class CrabQuest extends BasicQuestHelper
{
	// Initial Reqs
	SkillRequirement sailingLevel;
	SkillRequirement fishingLevel;
	ItemRequirement bigFishingNet;
	FreeInventorySlotRequirement nineSlots, fourSlots, threeSlots, twoSlots, oneSlot;

	// Zones
	Zone crabOneZone, crabSixZone, crabNineZone, crabTenZone;

	// Quest Reqs
	ItemRequirement shellOne, shellTwo, shellThree, shellFour, shellFive, shellSix, shellSeven;
	ItemRequirement seaSoakedBowstring, batteredBarrel, weatheredRosewoodPlank;
	ItemRequirement instrument;
	VarbitRequirement inQuestInstance, penguinKicked, sufficientBandmatesRecruited;
	Requirement shellOneObtained, shellTwoObtained, shellThreeObtained, shellFourObtained, shellFiveObtained,
		shellSixObtained, shellSevenObtained;
	Conditions floatsamPresent;
	NpcCondition shyFemaleCrab, crabOne, crabTwo, crabThree, crabFour, crabFive, crabSix, crabSeven, crabEight,
		crabNine, crabTen, crabEleven, penguin;
	VarbitRequirement crabShy, crabOneRecruited, crabTwoRecruited, crabThreeRecruited, crabFourRecruited, crabFiveRecruited,
		crabSixRecruited, crabSevenRecruited, crabEightRecruited, crabNineRecruited, crabTenRecruited,
		crabElevenRecruited;
	ZoneRequirement nearCrabOne, nearCrabSix, nearCrabNine, nearCrabTen;

	// Steps
	ObjectStep drinkFromBottle;
	NpcStep talkToCrab;
	ItemStep pickUpShellOne, pickUpShellTwo, pickUpShellThree, pickUpShellFour, pickUpShellFive, pickUpShellSix,
		pickUpShellSeven;
	DetailedQuestStep pickUpShells;
	NpcStep turnInShells;
	ItemStep pickUpBigFishingNet;
	NpcStep resetFloatsam, fishSeaSoakedBowstring, fishBatteredBarrel, fishWeatheredRosewoodPlank;
	ItemStep combineInstrument;
	NpcStep turnInInstrument, talkToCrabAfterInstrument;
	ObjectStep findShyCrab, findCrabOne, findCrabTwo, findCrabThree, findCrabFour, findCrabFive, findCrabSix,
		findPenguin, findCrabEight, findCrabNine, findCrabTen, findCrabEleven;
	EmoteStep sitForCrabOne, sitForCrabSix, sitForCrabNine, sitForCrabTen;
	NpcStep kickPenguin, talkToShyCrab, talkToCrabOne, talkToCrabTwo, talkToCrabThree, talkToCrabFour, talkToCrabFive,
		talkToCrabSix, talkToCrabSeven, talkToCrabEight, talkToCrabNine, talkToCrabTen, talkToCrabEleven,
		returnToTheBeach;

	@Override
	protected void setupZones()
	{
		crabOneZone = new Zone(new WorldPoint(3041, 2638, 0), new WorldPoint(3043, 2636, 0));
		crabSixZone = new Zone(new WorldPoint(3042, 2642, 0), new WorldPoint(3045, 2639, 0));
		crabNineZone = new Zone(new WorldPoint(3035, 2646, 0), new WorldPoint(3038, 2643, 0));
		crabTenZone = new Zone(new WorldPoint(3045, 2658, 0), new WorldPoint(3048, 2655, 0));
	}

	@Override
	protected void setupRequirements()
	{
		sailingLevel = new SkillRequirement(Skill.SAILING, 40, false);
		fishingLevel = new SkillRequirement(Skill.FISHING, 30, false);

		bigFishingNet = new ItemRequirement("Big Fishing Net", ItemID.BIG_NET);
		bigFishingNet.canBeObtainedDuringQuest();

		inQuestInstance = new VarbitRequirement(VarbitID.CRAB_PUT_BACK_IN_INSTANCE, 1);
		nineSlots = new FreeInventorySlotRequirement(9);
		fourSlots = new FreeInventorySlotRequirement(4);
		threeSlots = new FreeInventorySlotRequirement(3);
		twoSlots = new FreeInventorySlotRequirement(2);
		oneSlot = new FreeInventorySlotRequirement(1);

		shellOne = new ItemRequirement("Shell", ItemID.CRAB_SHELL_1);
		shellTwo = new ItemRequirement("Shell", ItemID.CRAB_SHELL_2);
		shellThree = new ItemRequirement("Shell", ItemID.CRAB_SHELL_3);
		shellFour = new ItemRequirement("Shell", ItemID.CRAB_SHELL_4);
		shellFive = new ItemRequirement("Shell", ItemID.CRAB_SHELL_5);
		shellSix = new ItemRequirement("Shell", ItemID.CRAB_SHELL_6);
		shellSeven = new ItemRequirement("Shell", ItemID.CRAB_SHELL_7);

		seaSoakedBowstring = new ItemRequirement("Sea-soaked Bowstring", ItemID.CRAB_BOWSTRING);
		batteredBarrel = new ItemRequirement("Battered Barrel", ItemID.CRAB_BARREL);
		weatheredRosewoodPlank = new ItemRequirement("Weathered Rosewood Plank", ItemID.CRAB_PLANK);

		instrument = new ItemRequirement("Instrument", ItemID.CRAB_BASS);

		shellOneObtained = or(shellOne, new VarbitRequirement(VarbitID.CRAB_SEASHELL_1, 1));
		shellTwoObtained = or(shellTwo, new VarbitRequirement(VarbitID.CRAB_SEASHELL_2, 1));
		shellThreeObtained = or(shellThree, new VarbitRequirement(VarbitID.CRAB_SEASHELL_3, 1));
		shellFourObtained = or(shellFour, new VarbitRequirement(VarbitID.CRAB_SEASHELL_4, 1));
		shellFiveObtained = or(shellFive, new VarbitRequirement(VarbitID.CRAB_SEASHELL_5, 1));
		shellSixObtained = or(shellSix, new VarbitRequirement(VarbitID.CRAB_SEASHELL_6, 1));
		shellSevenObtained = or(shellSeven, new VarbitRequirement(VarbitID.CRAB_SEASHELL_7, 1));

		floatsamPresent = not(new VarbitRequirement(VarbitID.CRAB_FLOATSAM_HIDDEN, 3));

		shyFemaleCrab = new NpcCondition(NpcID.CRAB_BANDCRAB, new WorldPoint(3045, 2653, 0));
		crabOne = new NpcCondition(NpcID.CRAB_BANDCRAB, new WorldPoint(3041, 2637, 0));
		crabTwo = new NpcCondition(NpcID.CRAB_BANDCRAB, new WorldPoint(3056, 2640, 0));
		crabThree = new NpcCondition(NpcID.CRAB_BANDCRAB, new WorldPoint(3041, 2649, 0));
		crabFour = new NpcCondition(NpcID.CRAB_BANDCRAB, new WorldPoint(3053, 2656, 0));
		crabFive = new NpcCondition(NpcID.CRAB_BANDCRAB, new WorldPoint(3037, 2646, 0));
		crabSix = new NpcCondition(NpcID.CRAB_BANDCRAB, new WorldPoint(3042, 2639, 0));
		crabSeven = new NpcCondition(NpcID.CRAB_BANDCRAB, new WorldPoint(3049, 2657, 0));
		crabEight = new NpcCondition(NpcID.CRAB_BANDCRAB, new WorldPoint(3051, 2636, 0));
		crabNine = new NpcCondition(NpcID.CRAB_BANDCRAB, new WorldPoint(3038, 2644, 0));
		crabTen = new NpcCondition(NpcID.CRAB_BANDCRAB, new WorldPoint(3047, 2655, 0));
		crabEleven = new NpcCondition(NpcID.CRAB_BANDCRAB, new WorldPoint(3040, 2647, 0));
		penguin = new NpcCondition(NpcID.CRAB_PENGUIN, new WorldPoint(3049, 2658, 0));

		penguinKicked = new VarbitRequirement(VarbitID.CRAB_KICKED_PENGUIN, 1);
		crabShy = new VarbitRequirement(VarbitID.CRAB_SHY, 2);
		crabOneRecruited = new VarbitRequirement(VarbitID.CRAB_RECRUITED_BANDMATE_1, 1);
		crabTwoRecruited = new VarbitRequirement(VarbitID.CRAB_RECRUITED_BANDMATE_2, 1);
		crabThreeRecruited = new VarbitRequirement(VarbitID.CRAB_RECRUITED_BANDMATE_3, 1);
		crabFourRecruited = new VarbitRequirement(VarbitID.CRAB_RECRUITED_BANDMATE_4, 1);
		crabFiveRecruited = new VarbitRequirement(VarbitID.CRAB_RECRUITED_BANDMATE_5, 1);
		crabSixRecruited = new VarbitRequirement(VarbitID.CRAB_RECRUITED_BANDMATE_6, 1);
		crabSevenRecruited = new VarbitRequirement(VarbitID.CRAB_RECRUITED_BANDMATE_7, 1);
		crabEightRecruited = new VarbitRequirement(VarbitID.CRAB_RECRUITED_BANDMATE_8, 1);
		crabNineRecruited = new VarbitRequirement(VarbitID.CRAB_RECRUITED_BANDMATE_9, 1);
		crabTenRecruited = new VarbitRequirement(VarbitID.CRAB_RECRUITED_BANDMATE_10, 1);
		crabElevenRecruited = new VarbitRequirement(VarbitID.CRAB_RECRUITED_BANDMATE_11, 1);
		sufficientBandmatesRecruited = new VarbitRequirement(VarbitID.CRAB_PARTIAL_COMPLETE, 1);

		nearCrabOne = new ZoneRequirement(true, crabOneZone);
		nearCrabSix = new ZoneRequirement(true, crabSixZone);
		nearCrabNine = new ZoneRequirement(true, crabNineZone);
		nearCrabTen = new ZoneRequirement(true, crabTenZone);
	}

	public void setupSteps()
	{
		drinkFromBottle = new ObjectStep(this,
			ObjectID.CRAB_BOTTLE,
			new WorldPoint(3055, 2639, 0),
			"Drink from the Bottle on the shore of Dognose Island.",
			sailingLevel, fishingLevel);
		drinkFromBottle.addDialogStep("Yes.");

		talkToCrab = new NpcStep(this,
			NpcID.CRAB_MAINCRAB_START,
			"Talk to the crab.");
		talkToCrab.addAlternateNpcs(NpcID.CRAB_MAINCRAB_BLUSHING);

		pickUpShellThree = new ItemStep(this,
			new WorldPoint(3051, 2634, 0),
			"Pick up a nearby Shell.",
			shellThree);

		pickUpShellSix = new ItemStep(this,
			new WorldPoint(3063, 2642, 0),
			"Pick up a nearby Shell.",
			shellSix);

		pickUpShellOne = new ItemStep(this,
			new WorldPoint(3059, 2648, 0),
			"Pick up a nearby Shell.",
			shellOne);

		pickUpShellSeven = new ItemStep(this,
			new WorldPoint(3040, 2636, 0),
			"Pick up a nearby Shell.",
			shellSeven);

		pickUpShellFour = new ItemStep(this,
			new WorldPoint(3036, 2643, 0),
			"Pick up a nearby Shell.",
			shellFour);

		pickUpShellTwo = new ItemStep(this,
			new WorldPoint(3044, 2652, 0),
			"Pick up a nearby Shell.",
			shellTwo);

		pickUpShellFive = new ItemStep(this,
			new WorldPoint(3050, 2655, 0),
			"Pick up a nearby Shell.",
			shellFive);

		turnInShells = new NpcStep(this, NpcID.CRAB_MAINCRAB_START,
			"Show the shells to the crab.");

		pickUpShells = new DetailedQuestStep(this,
			"Pick up some nearby seashells. " +
				"If your inventory fills up, you can talk to the crab to turn them in.");

		pickUpShells.addSubSteps(pickUpShellThree, pickUpShellSix, pickUpShellOne, pickUpShellSeven,
			pickUpShellFour, pickUpShellTwo, pickUpShellFive, turnInShells);

		pickUpBigFishingNet = new ItemStep(this,
			new WorldPoint(3061, 2646, 0),
			"Pick up the nearby Big Fishing Net.",
			bigFishingNet, fourSlots);

		fishSeaSoakedBowstring = new NpcStep(this,
			NpcID.CRAB_FLOATSAM_1,
			"Fish a Sea-soaked bowstring from the nearby Floatsam.",
			bigFishingNet, threeSlots);
		fishSeaSoakedBowstring.addAlternateNpcs(NpcID.CRAB_FLOATSAM_2, NpcID.CRAB_FLOATSAM_3);

		fishBatteredBarrel = new NpcStep(this,
			NpcID.CRAB_FLOATSAM_1,
			"Fish a Battered Barrel from the nearby Floatsam.",
			seaSoakedBowstring, bigFishingNet, twoSlots);
		fishBatteredBarrel.addAlternateNpcs(NpcID.CRAB_FLOATSAM_2, NpcID.CRAB_FLOATSAM_3);

		fishWeatheredRosewoodPlank = new NpcStep(this,
			NpcID.CRAB_FLOATSAM_1,
			"Fish a Weathered Rosewood Plank from the nearby Floatsam.",
			seaSoakedBowstring, batteredBarrel, bigFishingNet, oneSlot);
		fishWeatheredRosewoodPlank.addAlternateNpcs(NpcID.CRAB_FLOATSAM_2, NpcID.CRAB_FLOATSAM_3);

		resetFloatsam = new NpcStep(this,
			NpcID.CRAB_MAINCRAB,
			"Speak to the crab to reset the floatsam.");

		combineInstrument = new ItemStep(this,
			"Combine the instrument using the items in your inventory.",
			seaSoakedBowstring.highlighted(),
			batteredBarrel.highlighted(),
			weatheredRosewoodPlank.highlighted());

		turnInInstrument = new NpcStep(this,
			NpcID.CRAB_MAINCRAB_START,
			"Show the instrument to the crab.",
			instrument);

		talkToCrabAfterInstrument = new NpcStep(this,
			NpcID.CRAB_MAINCRAB_START,
			"Speak to the crab about the instrument.");
		talkToCrabAfterInstrument.addDialogStep("A disaster.");
		talkToCrabAfterInstrument.addDialogStep("Let me show you!");

		sitForCrabOne = new EmoteStep(this, QuestEmote.SIT, new WorldPoint(3041, 2638, 0),
			"Perform the sit emote on loop until the crab appears. " +
				"You must interact with the plant before emoting.", nearCrabOne);
		sitForCrabSix = new EmoteStep(this, QuestEmote.SIT, new WorldPoint(3042, 2640, 0),
			"Perform the sit emote on loop until the crab appears. " +
				"You must interact with the plant before emoting.", nearCrabSix);
		sitForCrabNine = new EmoteStep(this, QuestEmote.SIT, new WorldPoint(3038, 2644, 0),
			"Perform the sit emote on loop until the crab appears. " +
				"You must interact with the plant before emoting.", nearCrabNine);
		sitForCrabTen = new EmoteStep(this, QuestEmote.SIT, new WorldPoint(3046, 2655, 0),
			"Perform the sit emote on loop until the crab appears. " +
				"You must interact with the plant before emoting.", nearCrabTen);

		kickPenguin = new NpcStep(this, NpcID.CRAB_PENGUIN, new WorldPoint(3049, 2658, 0),
			"Speak to the 'crab'.");
		findShyCrab = new ObjectStep(this, ObjectID.CRAB_PALM_UPDATE03,
			new WorldPoint(3044, 2653, 0),
			"Check the palm near the center of the island, north of the bottle.");
		talkToShyCrab = new NpcStep(this, NpcID.CRAB_BANDCRAB, new WorldPoint(3045, 2653, 0),
			"Talk to the shy female crab about the crab on the beach.");
		talkToShyCrab.setMaxRoamRange(1);

		findCrabOne = new ObjectStep(this, ObjectID.CRAB_PALM_UPDATE01,
			new WorldPoint(3042, 2637, 0),
			"Check the palm south of the magic tree.");
		findCrabTwo = new ObjectStep(this, ObjectID.CRAB_ENAKH_LEAFLESSBUSH,
			new WorldPoint(3057, 2640, 0),
			"Check the leafless bush closest to the bottle.");
		findCrabThree = new ObjectStep(this, ObjectID.CRAB_PALM_UPDATE01,
			new WorldPoint(3040, 2649, 0),
			"Check the palm northwest of the bottle.");
		findCrabFour = new ObjectStep(this, ObjectID.CRAB_GROUNDCOVER_PLANT4_WITHERED,
			new WorldPoint(3054, 2656, 0),
			"Check the eastern-most plant on the north of the island.");
		findCrabFive = new ObjectStep(this, ObjectID.CRAB_PALM_UPDATE02,
			new WorldPoint(3037, 2647, 0),
			"Check the northern-most plant along the western coast of the island.");
		findCrabSix = new ObjectStep(this, ObjectID.CRAB_MAGICTREE,
			new WorldPoint(3043, 2640, 0),
			"Check the magic tree.");
		findPenguin = new ObjectStep(this, ObjectID.CRAB_PALM_UPDATE01,
			new WorldPoint(3048, 2658, 0),
			"Check the northernmost palm.");
		findCrabEight = new ObjectStep(this, ObjectID.CRAB_ENAKH_LEAFLESSBUSH,
			new WorldPoint(3050, 2636, 0),
			"Check the leafless bush southwest of the bottle.");
		findCrabNine = new ObjectStep(this, ObjectID.CRAB_AVIUM_TREE_1,
			new WorldPoint(3037, 2644, 0),
			"Check the avium tree on the western coast of the island.");
		findCrabTen = new ObjectStep(this, ObjectID.CRAB_AVIUM_TREE_1,
			new WorldPoint(3047, 2656, 0),
			"Check the avium tree west of the northernmost palm.");
		findCrabEleven = new ObjectStep(this, ObjectID.CRAB_PALM_UPDATE03,
			new WorldPoint(3039, 2647, 0),
			"Check the palm east of the westernmost palm.");

		talkToCrabOne = new NpcStep(this, NpcID.CRAB_BANDCRAB, new WorldPoint(3041, 2637, 0),
			"Talk to the crab to recruit a bandmember.");
		talkToCrabOne.setMaxRoamRange(1);
		talkToCrabTwo = new NpcStep(this, NpcID.CRAB_BANDCRAB, new WorldPoint(3056, 2640, 0),
			"Talk to the crab to recruit a bandmember.");
		talkToCrabTwo.setMaxRoamRange(1);
		talkToCrabThree = new NpcStep(this, NpcID.CRAB_BANDCRAB, new WorldPoint(3041, 2649, 0),
			"Talk to the crab to recruit a bandmember.");
		talkToCrabThree.setMaxRoamRange(1);
		talkToCrabFour = new NpcStep(this, NpcID.CRAB_BANDCRAB, new WorldPoint(3053, 2656, 0),
			"Talk to the crab to recruit a bandmember.");
		talkToCrabFour.setMaxRoamRange(1);
		talkToCrabFive = new NpcStep(this, NpcID.CRAB_BANDCRAB, new WorldPoint(3037, 2646, 0),
			"Talk to the crab to recruit a bandmember.");
		talkToCrabFive.setMaxRoamRange(1);
		talkToCrabSix = new NpcStep(this, NpcID.CRAB_BANDCRAB, new WorldPoint(3042, 2639, 0),
			"Talk to the crab to recruit a bandmember.");
		talkToCrabSix.setMaxRoamRange(1);
		talkToCrabSeven = new NpcStep(this, NpcID.CRAB_BANDCRAB, new WorldPoint(3049, 2657, 0),
			"Talk to the crab to recruit a bandmember.");
		talkToCrabSeven.setMaxRoamRange(1);
		talkToCrabEight = new NpcStep(this, NpcID.CRAB_BANDCRAB, new WorldPoint(3051, 2636, 0),
			"Talk to the crab to recruit a bandmember.");
		talkToCrabEight.setMaxRoamRange(1);
		talkToCrabNine = new NpcStep(this, NpcID.CRAB_BANDCRAB, new WorldPoint(3038, 2644, 0),
			"Talk to the crab to recruit a bandmember.");
		talkToCrabNine.setMaxRoamRange(1);
		talkToCrabNine.addDialogStep("1, 2, 3, 10, 11, 12, 13, 20, 21, 22, 23...");
		talkToCrabNine.addDialogStep("Every 100 years.");
		talkToCrabNine.addDialogStep("On the beach.");
		talkToCrabNine.addDialogStep("You can't possibly expect me to know that!");
		talkToCrabTen = new NpcStep(this, NpcID.CRAB_BANDCRAB, new WorldPoint(3047, 2655, 0),
			"Talk to the crab to recruit a bandmember.");
		talkToCrabTen.setMaxRoamRange(1);
		talkToCrabEleven = new NpcStep(this, NpcID.CRAB_BANDCRAB, new WorldPoint(3040, 2647, 0),
			"Talk to the crab to recruit a bandmember.");
		talkToCrabEleven.setMaxRoamRange(1);

		returnToTheBeach = new NpcStep(this,
			NpcID.CRAB_MAINCRAB_END,
			"Speak to the crab to finish the quest.");

		returnToTheBeach.addDialogStep("Wait for leaders to turn up.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, new ConditionalStep(this, drinkFromBottle)
			.addStep(not(inQuestInstance), drinkFromBottle));
		steps.put(5, new ConditionalStep(this, talkToCrab)
			.addStep(not(inQuestInstance), drinkFromBottle));
		steps.put(10, new ConditionalStep(this, talkToCrab)
			.addStep(not(inQuestInstance), drinkFromBottle));
		steps.put(15, new ConditionalStep(this, talkToCrab)
			.addStep(not(inQuestInstance), drinkFromBottle));
		steps.put(20, new ConditionalStep(this, talkToCrab)
			.addStep(not(inQuestInstance), drinkFromBottle));

		var cShells = new ConditionalStep(this, turnInShells);
		cShells.addStep(not(inQuestInstance), drinkFromBottle);
		cShells.addStep(and(not(oneSlot),
			or(shellOne,
				shellTwo,
				shellThree,
				shellFour,
				shellFive,
				shellSix,
				shellSeven)), turnInShells);
		cShells.addStep(not(shellThreeObtained), pickUpShellThree);
		cShells.addStep(not(shellSixObtained), pickUpShellSix);
		cShells.addStep(not(shellOneObtained), pickUpShellOne);
		cShells.addStep(not(shellSevenObtained), pickUpShellSeven);
		cShells.addStep(not(shellFourObtained), pickUpShellFour);
		cShells.addStep(not(shellTwoObtained), pickUpShellTwo);
		cShells.addStep(not(shellFiveObtained), pickUpShellFive);
		steps.put(25, cShells);
		steps.put(30, new ConditionalStep(this, turnInShells).addStep(not(inQuestInstance), drinkFromBottle));

		var cInstrument = new ConditionalStep(this, combineInstrument, seaSoakedBowstring, batteredBarrel,
			weatheredRosewoodPlank);
		cInstrument.addStep(not(inQuestInstance), drinkFromBottle);
		cInstrument.addStep(and(not(and(seaSoakedBowstring, batteredBarrel, weatheredRosewoodPlank)),
			not(bigFishingNet)), pickUpBigFishingNet);
		cInstrument.addStep(and(not(and(seaSoakedBowstring, batteredBarrel, weatheredRosewoodPlank)),
			not(floatsamPresent)), resetFloatsam);
		cInstrument.addStep(not(seaSoakedBowstring), fishSeaSoakedBowstring);
		cInstrument.addStep(not(batteredBarrel), fishBatteredBarrel);
		cInstrument.addStep(not(weatheredRosewoodPlank), fishWeatheredRosewoodPlank);
		steps.put(35, cInstrument);
		steps.put(37, cInstrument);

		var cInstrumentSafety = new ConditionalStep(this, turnInInstrument, instrument);
		cInstrumentSafety.addStep(not(inQuestInstance), drinkFromBottle);
		cInstrumentSafety.addStep(and(not(instrument),
			not(and(seaSoakedBowstring, batteredBarrel, weatheredRosewoodPlank)),
			not(bigFishingNet)), pickUpBigFishingNet);
		cInstrumentSafety.addStep(and(not(instrument),
			not(and(seaSoakedBowstring, batteredBarrel, weatheredRosewoodPlank)),
			not(floatsamPresent)), resetFloatsam);
		cInstrumentSafety.addStep(and(not(instrument), not(seaSoakedBowstring)), fishSeaSoakedBowstring);
		cInstrumentSafety.addStep(and(not(instrument), not(batteredBarrel)), fishBatteredBarrel);
		cInstrumentSafety.addStep(and(not(instrument), not(weatheredRosewoodPlank)), fishWeatheredRosewoodPlank);
		cInstrumentSafety.addStep(and(not(instrument), seaSoakedBowstring, batteredBarrel, weatheredRosewoodPlank),
			combineInstrument);
		steps.put(40, cInstrumentSafety);

		steps.put(45, new ConditionalStep(this, talkToCrabAfterInstrument)
			.addStep(not(inQuestInstance), drinkFromBottle));

		var cRustacean = new ConditionalStep(this, returnToTheBeach);
		cRustacean.addStep(not(inQuestInstance), drinkFromBottle);
		cRustacean.addStep(sufficientBandmatesRecruited, returnToTheBeach);
		cRustacean.addStep(and(not(crabShy), not(shyFemaleCrab)), findShyCrab);
		cRustacean.addStep(not(crabShy), talkToShyCrab);
		cRustacean.addStep(and(not(penguin), not(penguinKicked)), findPenguin);
		cRustacean.addStep(not(penguinKicked), kickPenguin);
		cRustacean.addStep(and(not(crabSeven), not(crabSevenRecruited)), findPenguin);
		cRustacean.addStep(not(crabSevenRecruited), talkToCrabSeven);
		cRustacean.addStep(and(not(crabFour), not(crabFourRecruited)), findCrabFour);
		cRustacean.addStep(not(crabFourRecruited), talkToCrabFour);
		cRustacean.addStep(and(not(crabTen), not(crabTenRecruited), not(nearCrabTen)), findCrabTen);
		cRustacean.addStep(and(not(crabTen), not(crabTenRecruited)), sitForCrabTen);
		cRustacean.addStep(not(crabTenRecruited), talkToCrabTen);
		cRustacean.addStep(and(not(crabThree), not(crabThreeRecruited)), findCrabThree);
		cRustacean.addStep(not(crabThreeRecruited), talkToCrabThree);
		cRustacean.addStep(and(not(crabEleven), not(crabElevenRecruited)), findCrabEleven);
		cRustacean.addStep(not(crabElevenRecruited), talkToCrabEleven);
		cRustacean.addStep(and(not(crabFive), not(crabFiveRecruited)), findCrabFive);
		cRustacean.addStep(not(crabFiveRecruited), talkToCrabFive);
		cRustacean.addStep(and(not(crabNine), not(crabNineRecruited), not(nearCrabNine)), findCrabNine);
		cRustacean.addStep(and(not(crabNine), not(crabNineRecruited)), sitForCrabNine);
		cRustacean.addStep(not(crabNineRecruited), talkToCrabNine);
		cRustacean.addStep(and(not(crabOne), not(crabOneRecruited), not(nearCrabOne)), findCrabOne);
		cRustacean.addStep(and(not(crabOne), not(crabOneRecruited)), sitForCrabOne);
		cRustacean.addStep(not(crabOneRecruited), talkToCrabOne);
		cRustacean.addStep(and(not(crabSix), not(crabSixRecruited), not(nearCrabSix)), findCrabSix);
		cRustacean.addStep(and(not(crabSix), not(crabSixRecruited)), sitForCrabSix);
		cRustacean.addStep(not(crabSixRecruited), talkToCrabSix);
		cRustacean.addStep(and(not(crabEight), not(crabEightRecruited)), findCrabEight);
		cRustacean.addStep(not(crabEightRecruited), talkToCrabEight);
		cRustacean.addStep(and(not(crabTwo), not(crabTwoRecruited)), findCrabTwo);
		cRustacean.addStep(not(crabTwoRecruited), talkToCrabTwo);
		steps.put(47, cRustacean);

		steps.put(50, new ConditionalStep(this, returnToTheBeach)
			.addStep(not(inQuestInstance), drinkFromBottle));
		steps.put(55, new ConditionalStep(this, returnToTheBeach)
			.addStep(not(inQuestInstance), drinkFromBottle));
		steps.put(60, new ConditionalStep(this, returnToTheBeach)
			.addStep(not(inQuestInstance), drinkFromBottle));

		return steps;
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		return List.of(
			nineSlots
		);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			sailingLevel,
			fishingLevel,
			threeSlots
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			bigFishingNet
		);
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
			new ExperienceReward(Skill.SAILING, 10000),
			new ExperienceReward(Skill.FISHING, 3000)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Some colorful seashells"),
			new UnlockReward("A nagging doubt that you might have just dreamt all of this")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			drinkFromBottle,
			talkToCrab,
			pickUpShells
		), oneSlot));

		sections.add(new PanelDetails("Assembling an instrument", List.of(
			pickUpBigFishingNet,
			resetFloatsam,
			fishSeaSoakedBowstring,
			fishBatteredBarrel,
			fishWeatheredRosewoodPlank,
			combineInstrument,
			turnInInstrument,
			talkToCrabAfterInstrument
		), bigFishingNet, threeSlots));

		sections.add(new PanelDetails("Recruit some band members", List.of(
			findShyCrab, talkToShyCrab,
			findPenguin, kickPenguin, talkToCrabSeven,
			findCrabFour, talkToCrabFour,
			findCrabTen, sitForCrabTen, talkToCrabTen,
			findCrabThree, talkToCrabThree,
			findCrabEleven, talkToCrabEleven,
			findCrabFive, talkToCrabFive,
			findCrabNine, sitForCrabNine, talkToCrabNine,
			findCrabOne, sitForCrabOne, talkToCrabOne,
			findCrabSix, sitForCrabSix, talkToCrabSix,
			findCrabEight, talkToCrabEight,
			findCrabTwo, talkToCrabTwo
		)));

		sections.add(new PanelDetails("Attend the concert", List.of(
			returnToTheBeach
		)));

		return sections;
	}
}
