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
package com.questhelper.quests.monkeymadnessii;

import com.questhelper.Zone;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.requirements.conditional.Conditions;
import java.util.Arrays;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;

public class MM2Sabotage extends ConditionalStep
{
	DetailedQuestStep climbF0ToF1ForSatchels, climbF1ToF0ForSatchels, pickUp6Satchels, goUpFromSatchelsToF1, goFromF1WithSatchelToF0, goFromF0WithSatchelToF1, goDownToGunpowder,
		fillSatchels, goUpFromGunpowder, placeSatchel1, goDownFromSatchel1, placeSatchel2, goUpToSatchel3, placeSatchel3, goUpToSatchel4,
		placeSatchel4, placeSatchel5, goF2ToF1ForSatchel6, goF1ToF0ForSatchel6, placeSatchel6, leavePlatform;

	ItemRequirement satchelCurrentQuantity, filledSatchelCurrentQuantity, filledSatchel1, filledSatchel1Highlighted;

	Zone platformF1, platformF2, platformF3, platformSatchelArea, platformGunpowderArea, platformAboveGunpowder;

	Requirement hasSatchelNeededQuantity, hasFilledSatchelNeededQuantity, hasFilledSatchel1, onPlatformF1, onPlatformF2,
		onPlatformF3, onPlatformSatchelArea, onPlatformGunpowderArea, onPlatformAboveGunpowder, placedSatchel1, placedSatchel2, placedSatchel3, placedSatchel4, placedSatchel5,
		placedSatchel6, placedAllSatchels;

	List<WorldPoint> boatToEastLadder, ladderToSatchelLadder, satchelLadderToF0Ladder, f0ToF1ForGunpowderRoute, pathToGunpowder, pathToSatchel3, pathFrom3To4Ladder, pathToSatchel5,
		pathBackFromSatchel5, ladderToSatchel6;

	public MM2Sabotage(QuestHelper questHelper, QuestStep step)
	{
		super(questHelper, step);
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupPaths();
		setupSteps();

		addStep(placedAllSatchels, leavePlatform);
		addStep(new Conditions(onPlatformF1, hasFilledSatchelNeededQuantity, placedSatchel1, placedSatchel2, placedSatchel3, placedSatchel4, placedSatchel5), placeSatchel6);
		addStep(new Conditions(onPlatformF2, hasFilledSatchelNeededQuantity, placedSatchel1, placedSatchel2, placedSatchel3, placedSatchel4, placedSatchel5), goF1ToF0ForSatchel6);
		addStep(new Conditions(onPlatformF3, hasFilledSatchelNeededQuantity, placedSatchel1, placedSatchel2, placedSatchel3, placedSatchel4, placedSatchel5), goF2ToF1ForSatchel6);
		addStep(new Conditions(onPlatformF3, hasFilledSatchelNeededQuantity, placedSatchel1, placedSatchel2, placedSatchel3, placedSatchel4), placeSatchel5);
		addStep(new Conditions(onPlatformF3, hasFilledSatchelNeededQuantity, placedSatchel1, placedSatchel2, placedSatchel3), placeSatchel4);
		addStep(new Conditions(onPlatformF2, hasFilledSatchelNeededQuantity, placedSatchel1, placedSatchel2, placedSatchel3), goUpToSatchel4);
		addStep(new Conditions(onPlatformF2, hasFilledSatchelNeededQuantity, placedSatchel1, placedSatchel2), placeSatchel3);
		addStep(new Conditions(onPlatformF1, hasFilledSatchelNeededQuantity, placedSatchel1, placedSatchel2), goUpToSatchel3);
		addStep(new Conditions(onPlatformF1, hasFilledSatchelNeededQuantity, placedSatchel1), placeSatchel2);
		addStep(new Conditions(onPlatformAboveGunpowder, hasFilledSatchelNeededQuantity, placedSatchel1), goDownFromSatchel1);
		addStep(new Conditions(onPlatformAboveGunpowder, hasFilledSatchelNeededQuantity), placeSatchel1);
		addStep(new Conditions(onPlatformGunpowderArea, hasFilledSatchelNeededQuantity), goUpFromGunpowder);

		addStep(new Conditions(onPlatformAboveGunpowder, hasSatchelNeededQuantity), goDownToGunpowder);
		addStep(new Conditions(onPlatformF2, hasSatchelNeededQuantity), goFromF1WithSatchelToF0);
		addStep(new Conditions(onPlatformGunpowderArea, hasSatchelNeededQuantity), fillSatchels);
		addStep(new Conditions(onPlatformSatchelArea, hasSatchelNeededQuantity), goUpFromSatchelsToF1);
		addStep(new Conditions(onPlatformF1, hasSatchelNeededQuantity), goFromF0WithSatchelToF1);

		addStep(onPlatformSatchelArea, pickUp6Satchels);
		addStep(onPlatformF2, climbF1ToF0ForSatchels);
		addStep(onPlatformF1, climbF0ToF1ForSatchels);
	}

	public void setupItemRequirements()
	{
		satchelCurrentQuantity = new ItemRequirement("Satchel", ItemID.SATCHEL, 6);
		satchelCurrentQuantity.addAlternates(ItemID.SATCHEL_19528);

		filledSatchel1 = new ItemRequirement("Satchel (filled)", ItemID.SATCHEL_19528);
		filledSatchel1.setTooltip("You can fill another satchel from the gunpowder barrels in the north east of the platform");
		filledSatchel1Highlighted = new ItemRequirement("Satchel (filled)", ItemID.SATCHEL_19528);
		filledSatchel1Highlighted.setTooltip("You can fill another satchel from the gunpowder barrels in the north east of the platform");
		filledSatchel1Highlighted.setHighlightInInventory(true);
		filledSatchelCurrentQuantity = new ItemRequirement("Satchel (filled)", ItemID.SATCHEL_19528, 6);
	}

	public void setupZones()
	{
		platformF1 = new Zone(new WorldPoint(2066, 5385, 1), new WorldPoint(2099, 5432, 1));
		platformF2 = new Zone(new WorldPoint(2066, 5385, 2), new WorldPoint(2099, 5432, 2));
		platformF3 = new Zone(new WorldPoint(2066, 5385, 3), new WorldPoint(2099, 5432, 3));
		platformSatchelArea = new Zone(new WorldPoint(2086, 5385, 1), new WorldPoint(2099, 5404, 1));
		platformGunpowderArea = new Zone(new WorldPoint(2090, 5411, 1), new WorldPoint(2099, 5432, 1));
		platformAboveGunpowder = new Zone(new WorldPoint(2085, 2097, 2), new WorldPoint(2099, 5432, 2));
	}

	public void setupConditions()
	{
		onPlatformF1 = new ZoneRequirement(platformF1);
		onPlatformF2 = new ZoneRequirement(platformF2);
		onPlatformF3 = new ZoneRequirement(platformF3);
		onPlatformSatchelArea = new ZoneRequirement(platformSatchelArea);
		onPlatformGunpowderArea = new ZoneRequirement(platformGunpowderArea);
		onPlatformAboveGunpowder = new ZoneRequirement(platformAboveGunpowder);

		hasSatchelNeededQuantity = satchelCurrentQuantity;
		hasFilledSatchel1 = filledSatchel1Highlighted;
		hasFilledSatchelNeededQuantity = filledSatchelCurrentQuantity;

		// 5047 0->8 when first placed
		// 8->10
		// 10->14
		// 14->46
		// 46->62
		placedSatchel1 = new VarbitRequirement(5044, 1);
		placedSatchel2 = new VarbitRequirement(5042, 1);
		placedSatchel3 = new VarbitRequirement(5043, 1);
		placedSatchel4 = new VarbitRequirement(5046, 1);
		placedSatchel5 = new VarbitRequirement(5045, 1);
		placedSatchel6 = new VarbitRequirement(5041, 1);

		placedAllSatchels = new VarbitRequirement(5047, 63);
	}

	public void setupSteps()
	{
		climbF0ToF1ForSatchels = new ObjectStep(getQuestHelper(), ObjectID.LADDER_28618, new WorldPoint(2098, 5408, 1), "Navigate through the platform, avoiding the monkey guards, to the east side and climb the ladder there.");
		climbF0ToF1ForSatchels.setLinePoints(boatToEastLadder);
		climbF0ToF1ForSatchels.setHideMinimapLines(true);

		climbF1ToF0ForSatchels = new ObjectStep(getQuestHelper(), ObjectID.LADDER_28619, new WorldPoint(2086, 5387, 2), "Make your way south then west, and go down the ladder there.");
		climbF1ToF0ForSatchels.setLinePoints(ladderToSatchelLadder);

		climbF1ToF0ForSatchels.setHideMinimapLines(true);

		pickUp6Satchels = new ObjectStep(getQuestHelper(), ObjectID.CRATE_28652, new WorldPoint(2097, 5405, 1), "Follow the path around and search the crates there for 6 satchels. There are no monkeys here to avoid.", satchelCurrentQuantity);

		goUpFromSatchelsToF1 = new ObjectStep(getQuestHelper(), ObjectID.LADDER_28618, new WorldPoint(2086, 5387, 1), "Climb back up the ladder.");

		goFromF1WithSatchelToF0 = new ObjectStep(getQuestHelper(), ObjectID.LADDER_28619, new WorldPoint(2098, 5408, 2), "Make your way east then north, and go down the ladder there.");
		goFromF1WithSatchelToF0.setLinePoints(satchelLadderToF0Ladder);
		goFromF1WithSatchelToF0.setHideMinimapLines(true);

		goFromF0WithSatchelToF1 = new ObjectStep(getQuestHelper(), ObjectID.LADDER_28618, new WorldPoint(2085, 5431, 1), "Navigate through the platform, avoiding the monkey guards, to the north east side and climb the ladder there.");
		goFromF0WithSatchelToF1.setLinePoints(f0ToF1ForGunpowderRoute);
		goFromF0WithSatchelToF1.setHideMinimapLines(true);

		goDownToGunpowder = new ObjectStep(getQuestHelper(), ObjectID.LADDER_28619, new WorldPoint(2098, 5421, 2), "Make your way east then south and go down the ladder there. Avoid the monkey on the way.");
		fillSatchels = new ObjectStep(getQuestHelper(), ObjectID.BARREL_28653, new WorldPoint(2089, 5431, 1), "Fill your satchels with gunpowder from the barrel to the north.", filledSatchelCurrentQuantity);
		fillSatchels.setLinePoints(pathToGunpowder);

		fillSatchels.setHideMinimapLines(true);

		goUpFromGunpowder = new ObjectStep(getQuestHelper(), ObjectID.LADDER_28618, new WorldPoint(2098, 5421, 1), "Go back up the ladder to the first floor.", filledSatchel1);

		placeSatchel1 = new ObjectStep(getQuestHelper(), ObjectID.COMPROMISED_FLOORBOARDS_28624, new WorldPoint(2098, 5413, 2), "Place a satchel to the south.", filledSatchel1Highlighted);
		placeSatchel1.addIcon(ItemID.SATCHEL_19528);

		goDownFromSatchel1 = new ObjectStep(getQuestHelper(), ObjectID.LADDER_28619, new WorldPoint(2085, 5431, 2), "Go down the ladder to the north west.", filledSatchel1);
		placeSatchel2 = new ObjectStep(getQuestHelper(), ObjectID.COMPROMISED_SUPPORT_28622, new WorldPoint(2090, 5418, 1), "Place a satchel on the support to the south.", filledSatchel1Highlighted);
		placeSatchel2.addIcon(ItemID.SATCHEL_19528);
		goUpToSatchel3 = new ObjectStep(getQuestHelper(), ObjectID.LADDER_28618, new WorldPoint(2098, 5408, 1), "Go to the east side and climb the ladder there.", filledSatchel1);

		placeSatchel3 = new ObjectStep(getQuestHelper(), ObjectID.COMPROMISED_FLOORBOARDS, new WorldPoint(2082, 5431, 2), "Make your way to the west across the vine, then north then east. Place a satchel here.", filledSatchel1Highlighted);
		placeSatchel3.addIcon(ItemID.SATCHEL_19528);
		placeSatchel3.setHideMinimapLines(true);
		placeSatchel3.setLinePoints(pathToSatchel3);

		goUpToSatchel4 = new ObjectStep(getQuestHelper(), ObjectID.LADDER_28618, new WorldPoint(2098, 5407, 2), "Return to the east side where you came from and go up to the 2nd floor.", filledSatchel1);
		goUpToSatchel4.setLinePoints(pathFrom3To4Ladder);
		goUpToSatchel4.setHideMinimapLines(true);

		placeSatchel4 = new ObjectStep(getQuestHelper(), ObjectID.GAS_CYLINDER_28626, new WorldPoint(2096, 5393, 3), "Place a satchel on the gas cylinder to the south.", filledSatchel1Highlighted);
		placeSatchel4.addIcon(ItemID.SATCHEL_19528);

		placeSatchel5 = new ObjectStep(getQuestHelper(), ObjectID.GAS_CYLINDER, new WorldPoint(2069, 5421, 3), "Make your way to the west then north and place a satchel on the gas cylinder there.", filledSatchel1Highlighted);
		placeSatchel5.setLinePoints(pathToSatchel5);
		placeSatchel5.setHideMinimapLines(true);
		placeSatchel5.addIcon(ItemID.SATCHEL_19528);
		goF2ToF1ForSatchel6 = new ObjectStep(getQuestHelper(), ObjectID.LADDER_28619, new WorldPoint(2098, 5407, 3), "Go back to the bottom floor.", filledSatchel1);
		goF2ToF1ForSatchel6.setLinePoints(pathBackFromSatchel5);
		goF2ToF1ForSatchel6.setHideMinimapLines(true);
		goF1ToF0ForSatchel6 = new ObjectStep(getQuestHelper(), ObjectID.LADDER_28619, new WorldPoint(2098, 5408, 2), "Go back to the bottom floor.", filledSatchel1);

		placeSatchel6 = new ObjectStep(getQuestHelper(), ObjectID.COMPROMISED_SUPPORT, new WorldPoint(2075, 5396, 1), "Place the final satchel in the centre of the platform.", filledSatchel1Highlighted);
		placeSatchel6.addIcon(ItemID.SATCHEL_19528);
		placeSatchel6.setHideMinimapLines(true);
		placeSatchel6.setLinePoints(ladderToSatchel6);

		leavePlatform = new ObjectStep(getQuestHelper(), ObjectID.LADDER_28620, new WorldPoint(2065, 5404, 1), "Leave the platform via the boat. DON'T teleport away. You can shortcut here by getting caught.");

	}

	@Override
	public void onGameTick(GameTick event)
	{
		super.onGameTick(event);
		int currentlyNeededExplosives = 6;
		if (placedSatchel1.check(client))
		{
			currentlyNeededExplosives--;
		}
		if (placedSatchel2.check(client))
		{
			currentlyNeededExplosives--;
		}
		if (placedSatchel3.check(client))
		{
			currentlyNeededExplosives--;
		}
		if (placedSatchel4.check(client))
		{
			currentlyNeededExplosives--;
		}
		if (placedSatchel5.check(client))
		{
			currentlyNeededExplosives--;
		}
		if (placedSatchel6.check(client))
		{
			currentlyNeededExplosives--;
		}

		filledSatchelCurrentQuantity.setQuantity(currentlyNeededExplosives);
		satchelCurrentQuantity.setQuantity(currentlyNeededExplosives);
	}

	private void setupPaths()
	{
		boatToEastLadder = Arrays.asList(
			new WorldPoint(2067, 5403, 1),
			new WorldPoint(2067, 5402, 1),
			new WorldPoint(2068, 5402, 1),
			new WorldPoint(2068, 5398, 1),
			new WorldPoint(2067, 5398, 1),
			new WorldPoint(2067, 5395, 1),
			new WorldPoint(2068, 5395, 1),
			new WorldPoint(2067, 5395, 1),
			new WorldPoint(2067, 5394, 1),
			new WorldPoint(2067, 5391, 1),
			new WorldPoint(2068, 5391, 1),
			new WorldPoint(2068, 5386, 1),
			new WorldPoint(2072, 5386, 1),
			new WorldPoint(2072, 5388, 1),
			new WorldPoint(2072, 5386, 1),
			new WorldPoint(2075, 5386, 1),
			new WorldPoint(2075, 5387, 1),
			new WorldPoint(2078, 5387, 1),
			new WorldPoint(2078, 5386, 1),
			new WorldPoint(2081, 5386, 1),
			new WorldPoint(2081, 5387, 1),
			new WorldPoint(2082, 5387, 1),
			new WorldPoint(2082, 5391, 1),
			new WorldPoint(2083, 5391, 1),
			new WorldPoint(2083, 5394, 1),

			new WorldPoint(2083, 5396, 1),
			new WorldPoint(2084, 5396, 1),
			new WorldPoint(2084, 5398, 1),
			new WorldPoint(2082, 5398, 1),

			new WorldPoint(2082, 5399, 1),
			new WorldPoint(2081, 5399, 1),
			new WorldPoint(2082, 5399, 1),
			new WorldPoint(2082, 5405, 1),
			new WorldPoint(2084, 5405, 1),

			new WorldPoint(2082, 5405, 1),
			new WorldPoint(2082, 5407, 1),
			new WorldPoint(2081, 5407, 1),
			new WorldPoint(2081, 5408, 1),
			new WorldPoint(2078, 5408, 1),
			new WorldPoint(2078, 5409, 1),
			new WorldPoint(2075, 5409, 1),
			new WorldPoint(2075, 5407, 1),
			new WorldPoint(2075, 5409, 1),
			new WorldPoint(2073, 5409, 1),
			new WorldPoint(2071, 5409, 1),
			new WorldPoint(2071, 5408, 1),
			new WorldPoint(2068, 5408, 1),
			new WorldPoint(2068, 5411, 1),
			new WorldPoint(2067, 5411, 1),
			new WorldPoint(2067, 5412, 1),
			new WorldPoint(2066, 5412, 1),
			new WorldPoint(2066, 5414, 1),
			new WorldPoint(2067, 5414, 1),
			new WorldPoint(2067, 5415, 1),
			new WorldPoint(2069, 5415, 1),
			new WorldPoint(2067, 5415, 1),
			new WorldPoint(2067, 5417, 1),
			new WorldPoint(2067, 5423, 1),
			new WorldPoint(2068, 5423, 1),
			new WorldPoint(2068, 5426, 1),
			new WorldPoint(2067, 5426, 1),
			new WorldPoint(2067, 5429, 1),
			new WorldPoint(2068, 5429, 1),
			new WorldPoint(2068, 5430, 1),
			new WorldPoint(2069, 5430, 1),
			new WorldPoint(2069, 5431, 1),
			new WorldPoint(2069, 5430, 1),
			new WorldPoint(2071, 5430, 1),
			new WorldPoint(2071, 5429, 1),
			new WorldPoint(2074, 5429, 1),
			new WorldPoint(2074, 5430, 1),
			new WorldPoint(2075, 5430, 1),
			new WorldPoint(2075, 5431, 1),
			new WorldPoint(2076, 5431, 1),
			new WorldPoint(2076, 5432, 1),
			new WorldPoint(2078, 5432, 1),
			new WorldPoint(2078, 5430, 1),
			new WorldPoint(2081, 5430, 1),
			new WorldPoint(2081, 5431, 1),
			new WorldPoint(2081, 5430, 1),
			new WorldPoint(2082, 5430, 1),
			new WorldPoint(2082, 5429, 1),
			new WorldPoint(2082, 5428, 1),
			new WorldPoint(2081, 5428, 1),
			new WorldPoint(2081, 5426, 1),
			new WorldPoint(2084, 5426, 1),
			new WorldPoint(2082, 5426, 1),
			new WorldPoint(2082, 5424, 1),
			new WorldPoint(2082, 5423, 1),
			new WorldPoint(2081, 5423, 1),
			new WorldPoint(2081, 5420, 1),
			new WorldPoint(2082, 5420, 1),
			new WorldPoint(2082, 5419, 1),
			new WorldPoint(2083, 5419, 1),
			new WorldPoint(2083, 5418, 1),
			new WorldPoint(2085, 5418, 1),
			new WorldPoint(2083, 5418, 1),
			new WorldPoint(2083, 5415, 1),
			new WorldPoint(2083, 5412, 1),
			new WorldPoint(2081, 5412, 1),
			new WorldPoint(2083, 5412, 1),
			new WorldPoint(2083, 5410, 1),
			new WorldPoint(2085, 5410, 1),
			new WorldPoint(2085, 5408, 1),
			new WorldPoint(2090, 5408, 1),
			new WorldPoint(2090, 5410, 1),
			new WorldPoint(2090, 5408, 1),
			new WorldPoint(2091, 5408, 1),
			new WorldPoint(2092, 5408, 1),
			new WorldPoint(2092, 5409, 1),
			new WorldPoint(2096, 5409, 1),
			new WorldPoint(2096, 5408, 1),
			new WorldPoint(2098, 5408, 1)
		);

		ladderToSatchelLadder = Arrays.asList(
			new WorldPoint(2097, 5406, 2),
			new WorldPoint(2097, 5404, 2),
			new WorldPoint(0, 0, 0),
			new WorldPoint(2098, 5403, 2),
			new WorldPoint(2098, 5401, 2),
			new WorldPoint(2097, 5401, 2),
			new WorldPoint(2097, 5400, 2),
			new WorldPoint(2096, 5400, 2),
			new WorldPoint(2096, 5397, 2),
			new WorldPoint(2099, 5397, 2),
			new WorldPoint(2099, 5398, 2),
			new WorldPoint(0, 0, 0),
			new WorldPoint(2098, 5395, 2),
			new WorldPoint(2098, 5394, 2),
			new WorldPoint(2097, 5394, 2),
			new WorldPoint(2097, 5393, 2),
			new WorldPoint(2096, 5393, 2),
			new WorldPoint(2096, 5391, 2),
			new WorldPoint(2097, 5391, 2),
			new WorldPoint(2097, 5387, 2),
			new WorldPoint(2099, 5387, 2),
			new WorldPoint(2099, 5386, 2),
			new WorldPoint(0, 0, 0),
			new WorldPoint(2096, 5387, 2),
			new WorldPoint(2093, 5387, 2),
			new WorldPoint(2093, 5386, 2),
			new WorldPoint(2089, 5386, 2),
			new WorldPoint(2089, 5385, 2),
			new WorldPoint(2087, 5385, 2),
			new WorldPoint(2087, 5387, 2)
		);

		satchelLadderToF0Ladder = Arrays.asList(
			new WorldPoint(2087, 5385, 2),
			new WorldPoint(2089, 5385, 2),
			new WorldPoint(2089, 5386, 2),
			new WorldPoint(2093, 5386, 2),
			new WorldPoint(2093, 5387, 2),
			new WorldPoint(2099, 5387, 2),
			new WorldPoint(2099, 5386, 2),
			new WorldPoint(0, 0, 0),
			new WorldPoint(2097, 5388, 2),
			new WorldPoint(2097, 5391, 2),
			new WorldPoint(2096, 5391, 2),
			new WorldPoint(2096, 5393, 2),
			new WorldPoint(2097, 5393, 2),
			new WorldPoint(2097, 5394, 2),
			new WorldPoint(2098, 5394, 2),
			new WorldPoint(2098, 5397, 2),
			new WorldPoint(2099, 5397, 2),
			new WorldPoint(2099, 5398, 2),
			new WorldPoint(0, 0, 0),
			new WorldPoint(2096, 5397, 2),
			new WorldPoint(2096, 5400, 2),
			new WorldPoint(2097, 5400, 2),
			new WorldPoint(2097, 5401, 2),
			new WorldPoint(2098, 5401, 2),
			new WorldPoint(2098, 5404, 2),
			new WorldPoint(2097, 5404, 2),
			new WorldPoint(2097, 5408, 2)
		);


		f0ToF1ForGunpowderRoute = Arrays.asList(
			new WorldPoint(2067, 5403, 1),
			new WorldPoint(2067, 5402, 1),
			new WorldPoint(2068, 5402, 1),
			new WorldPoint(2068, 5398, 1),
			new WorldPoint(2068, 5398, 1),
			new WorldPoint(2067, 5395, 1),
			new WorldPoint(2068, 5395, 1),
			new WorldPoint(2068, 5395, 0),
			new WorldPoint(2067, 5394, 1),
			new WorldPoint(2067, 5391, 1),
			new WorldPoint(2068, 5391, 1),
			new WorldPoint(2068, 5386, 1),
			new WorldPoint(2072, 5386, 1),
			new WorldPoint(2072, 5388, 1),
			new WorldPoint(2072, 5388, 0),
			new WorldPoint(2073, 5386, 1),
			new WorldPoint(2075, 5386, 1),
			new WorldPoint(2075, 5387, 1),
			new WorldPoint(2078, 5387, 1),
			new WorldPoint(2078, 5386, 1),
			new WorldPoint(2081, 5386, 1),
			new WorldPoint(2081, 5387, 1),
			new WorldPoint(2082, 5387, 1),
			new WorldPoint(2082, 5391, 1),
			new WorldPoint(2083, 5391, 1),
			new WorldPoint(2083, 5394, 1),

			new WorldPoint(2083, 5396, 1),
			new WorldPoint(2084, 5396, 1),
			new WorldPoint(2084, 5398, 1),
			new WorldPoint(2084, 5498, 1),
			new WorldPoint(0, 0, 0),

			new WorldPoint(2082, 5399, 1),
			new WorldPoint(2082, 5405, 1),
			new WorldPoint(2084, 5405, 1),
			new WorldPoint(2084, 5405, 0),
			new WorldPoint(2082, 5407, 1),
			new WorldPoint(2081, 5407, 1),
			new WorldPoint(2081, 5408, 1),
			new WorldPoint(2078, 5408, 1),
			new WorldPoint(2078, 5409, 1),
			new WorldPoint(2075, 5409, 1),
			new WorldPoint(2075, 5407, 1),
			new WorldPoint(0, 0, 0),
			new WorldPoint(2073, 5409, 1),
			new WorldPoint(2071, 5409, 1),
			new WorldPoint(2071, 5408, 1),
			new WorldPoint(2068, 5408, 1),
			new WorldPoint(2068, 5411, 1),
			new WorldPoint(2067, 5411, 1),
			new WorldPoint(2067, 5412, 1),
			new WorldPoint(2066, 5412, 1),
			new WorldPoint(2066, 5414, 1),
			new WorldPoint(2067, 5414, 1),
			new WorldPoint(2067, 5415, 1),
			new WorldPoint(2069, 5415, 1),
			new WorldPoint(0, 0, 0),
			new WorldPoint(2067, 5417, 1),
			new WorldPoint(2067, 5423, 1),
			new WorldPoint(2068, 5423, 1),
			new WorldPoint(2068, 5426, 1),
			new WorldPoint(2067, 5426, 1),
			new WorldPoint(2067, 5429, 1),
			new WorldPoint(2068, 5429, 1),
			new WorldPoint(2068, 5430, 1),
			new WorldPoint(2069, 5430, 1),
			new WorldPoint(2069, 5431, 1),
			new WorldPoint(0, 0, 0),
			new WorldPoint(2071, 5430, 1),
			new WorldPoint(2071, 5429, 1),
			new WorldPoint(2074, 5429, 1),
			new WorldPoint(2074, 5430, 1),
			new WorldPoint(2075, 5430, 1),
			new WorldPoint(2075, 5431, 1),
			new WorldPoint(2076, 5431, 1),
			new WorldPoint(2076, 5432, 1),
			new WorldPoint(2078, 5432, 1),
			new WorldPoint(2078, 5430, 1),
			new WorldPoint(2081, 5430, 1),
			new WorldPoint(2081, 5431, 1),
			new WorldPoint(0, 0, 0),
			new WorldPoint(2082, 5429, 1),
			new WorldPoint(2082, 5428, 1),
			new WorldPoint(2081, 5428, 1),
			new WorldPoint(2081, 5426, 1),
			new WorldPoint(2082, 5426, 1),
			new WorldPoint(2082, 5423, 1),
			new WorldPoint(2081, 5423, 1),
			new WorldPoint(2081, 5420, 1),
			new WorldPoint(2082, 5420, 1),
			new WorldPoint(2082, 5419, 1),
			new WorldPoint(2083, 5419, 1),
			new WorldPoint(2083, 5414, 1),
			new WorldPoint(0, 0, 0),
			new WorldPoint(2082, 5412, 1),
			new WorldPoint(2083, 5412, 1),
			new WorldPoint(2083, 5410, 1),
			new WorldPoint(2085, 5410, 1),
			new WorldPoint(2085, 5408, 1),
			new WorldPoint(2088, 5408, 1),
			new WorldPoint(0, 0, 0),
			new WorldPoint(2090, 5410, 1),
			new WorldPoint(2090, 5408, 1),
			new WorldPoint(2092, 5408, 1),
			new WorldPoint(2092, 5409, 1),
			new WorldPoint(2096, 5409, 1),
			new WorldPoint(2096, 5408, 1),
			new WorldPoint(2098, 5408, 1)
		);

		pathToGunpowder = Arrays.asList(
			new WorldPoint(2097, 5423, 1),
			new WorldPoint(2098, 5423, 1),
			new WorldPoint(2098, 5424, 1),
			new WorldPoint(0, 0, 0),
			new WorldPoint(2099, 5427, 1),
			new WorldPoint(2096, 5427, 1),
			new WorldPoint(2096, 5426, 1),
			new WorldPoint(0, 0, 0),
			new WorldPoint(2097, 5429, 1),
			new WorldPoint(2097, 5431, 1),
			new WorldPoint(2093, 5431, 1),
			new WorldPoint(2093, 5429, 1),
			new WorldPoint(2091, 5429, 1),
			new WorldPoint(2091, 5430, 1),
			new WorldPoint(2090, 5430, 1)
		);

		pathToSatchel3 = Arrays.asList(
			new WorldPoint(2096, 5409, 2),
			new WorldPoint(2093, 5409, 2),
			new WorldPoint(2093, 5408, 2),

			new WorldPoint(2093, 5409, 2),
			new WorldPoint(2092, 5409, 2),
			new WorldPoint(2091, 5409, 2),
			new WorldPoint(2091, 5408, 2),
			new WorldPoint(2088, 5408, 2),
			new WorldPoint(2088, 5409, 2),
			new WorldPoint(2074, 5409, 2),
			new WorldPoint(2074, 5408, 2),
			new WorldPoint(2072, 5408, 2),
			new WorldPoint(2072, 5407, 2),

			new WorldPoint(2072, 5408, 2),
			new WorldPoint(2071, 5408, 2),
			new WorldPoint(2071, 5409, 2),
			new WorldPoint(2068, 5409, 2),
			new WorldPoint(2068, 5410, 2),
			new WorldPoint(2067, 5410, 2),
			new WorldPoint(2067, 5413, 2),
			new WorldPoint(2069, 5413, 2),
			new WorldPoint(2068, 5413, 2),
			new WorldPoint(2068, 5414, 2),
			new WorldPoint(2068, 5416, 2),
			new WorldPoint(2067, 5416, 2),
			new WorldPoint(2067, 5421, 2),
			new WorldPoint(2069, 5421, 2),
			new WorldPoint(2068, 5421, 2),
			new WorldPoint(2068, 5423, 2),
			new WorldPoint(2068, 5426, 2),
			new WorldPoint(2067, 5426, 2),
			new WorldPoint(2067, 5430, 2),
			new WorldPoint(2066, 5430, 2),
			new WorldPoint(2066, 5431, 2),
			new WorldPoint(2066, 5430, 2),
			new WorldPoint(2068, 5430, 2),
			new WorldPoint(2069, 5430, 2),
			new WorldPoint(2069, 5431, 2),
			new WorldPoint(2070, 5431, 2),
			new WorldPoint(2070, 5432, 2),
			new WorldPoint(2072, 5432, 2),
			new WorldPoint(2072, 5431, 2),
			new WorldPoint(2072, 5432, 2),
			new WorldPoint(2073, 5432, 2),
			new WorldPoint(2074, 5432, 2),
			new WorldPoint(2074, 5431, 2),
			new WorldPoint(2075, 5431, 2),
			new WorldPoint(2075, 5430, 2),
			new WorldPoint(2078, 5430, 2),
			new WorldPoint(2078, 5431, 2)
		);

		pathFrom3To4Ladder = Arrays.asList(
			new WorldPoint(2096, 5408, 2),
			new WorldPoint(2096, 5409, 2),
			new WorldPoint(2093, 5409, 2),
			new WorldPoint(2093, 5408, 2),

			new WorldPoint(2093, 5409, 2),
			new WorldPoint(2092, 5409, 2),
			new WorldPoint(2091, 5409, 2),
			new WorldPoint(2091, 5408, 2),
			new WorldPoint(2088, 5408, 2),
			new WorldPoint(2088, 5409, 2),
			new WorldPoint(2074, 5409, 2),
			new WorldPoint(2074, 5408, 2),
			new WorldPoint(2072, 5408, 2),
			new WorldPoint(2072, 5407, 2),

			new WorldPoint(2072, 5408, 2),
			new WorldPoint(2071, 5408, 2),
			new WorldPoint(2071, 5409, 2),
			new WorldPoint(2068, 5409, 2),
			new WorldPoint(2068, 5410, 2),
			new WorldPoint(2067, 5410, 2),
			new WorldPoint(2067, 5413, 2),
			new WorldPoint(2066, 5413, 2),
			new WorldPoint(2068, 5413, 2),
			new WorldPoint(2068, 5414, 2),
			new WorldPoint(2068, 5416, 2),
			new WorldPoint(2067, 5416, 2),
			new WorldPoint(2067, 5421, 2),
			new WorldPoint(2066, 5421, 2),
			new WorldPoint(2068, 5421, 2),
			new WorldPoint(2068, 5423, 2),
			new WorldPoint(2068, 5426, 2),
			new WorldPoint(2067, 5426, 2),
			new WorldPoint(2067, 5430, 2),
			new WorldPoint(2066, 5430, 2),
			new WorldPoint(2066, 5431, 2),
			new WorldPoint(2066, 5430, 2),
			new WorldPoint(2068, 5430, 2),
			new WorldPoint(2069, 5430, 2),
			new WorldPoint(2069, 5431, 2),
			new WorldPoint(2070, 5431, 2),
			new WorldPoint(2070, 5432, 2),
			new WorldPoint(2072, 5432, 2),
			new WorldPoint(2072, 5431, 2),
			new WorldPoint(2072, 5432, 2),
			new WorldPoint(2073, 5432, 2),
			new WorldPoint(2074, 5432, 2),
			new WorldPoint(2074, 5431, 2),
			new WorldPoint(2075, 5431, 2),
			new WorldPoint(2075, 5430, 2),
			new WorldPoint(2078, 5430, 2),
			new WorldPoint(2078, 5431, 2)
		);

		pathToSatchel5 = Arrays.asList(
			new WorldPoint(2096, 5409, 3),
			new WorldPoint(2092, 5409, 3),
			new WorldPoint(2092, 5408, 3),
			new WorldPoint(2092, 5408, 3),
			new WorldPoint(2092, 5408, 3),
			new WorldPoint(2090, 5408, 3),
			new WorldPoint(2090, 5409, 3),
			new WorldPoint(2090, 5408, 3),
			new WorldPoint(2084, 5408, 3),
			new WorldPoint(2084, 5407, 3),
			new WorldPoint(2081, 5407, 3),
			new WorldPoint(2081, 5408, 3),
			new WorldPoint(2078, 5408, 3),
			new WorldPoint(2078, 5409, 3),
			new WorldPoint(2075, 5409, 3),
			new WorldPoint(2075, 5408, 3),
			new WorldPoint(2075, 5409, 3),
			new WorldPoint(2071, 5409, 3),
			new WorldPoint(2071, 5408, 3),
			new WorldPoint(2068, 5408, 3),
			new WorldPoint(2068, 5407, 3),
			new WorldPoint(2067, 5407, 3),
			new WorldPoint(2068, 5407, 3),
			new WorldPoint(2068, 5410, 3),
			new WorldPoint(2067, 5410, 3),
			new WorldPoint(2067, 5413, 3),
			new WorldPoint(2069, 5413, 3),
			new WorldPoint(2068, 5413, 3),
			new WorldPoint(2068, 5416, 3),
			new WorldPoint(2067, 5416, 3),
			new WorldPoint(2067, 5421, 3),
			new WorldPoint(2068, 5421, 3)
		);

		pathBackFromSatchel5 = Arrays.asList(
			new WorldPoint(2096, 5409, 3),
			new WorldPoint(2092, 5409, 3),
			new WorldPoint(2092, 5408, 3),
			new WorldPoint(2092, 5408, 3),
			new WorldPoint(2092, 5408, 3),
			new WorldPoint(2090, 5408, 3),
			new WorldPoint(2090, 5409, 3),
			new WorldPoint(2090, 5408, 3),
			new WorldPoint(2084, 5408, 3),
			new WorldPoint(2084, 5407, 3),
			new WorldPoint(2081, 5407, 3),
			new WorldPoint(2081, 5408, 3),
			new WorldPoint(2078, 5408, 3),
			new WorldPoint(2078, 5409, 3),
			new WorldPoint(2075, 5409, 3),
			new WorldPoint(2075, 5408, 3),
			new WorldPoint(2075, 5409, 3),
			new WorldPoint(2071, 5409, 3),
			new WorldPoint(2071, 5408, 3),
			new WorldPoint(2068, 5408, 3),
			new WorldPoint(2068, 5407, 3),
			new WorldPoint(2067, 5407, 3),
			new WorldPoint(2068, 5407, 3),
			new WorldPoint(2068, 5410, 3),
			new WorldPoint(2067, 5410, 3),
			new WorldPoint(2067, 5413, 3),
			new WorldPoint(2066, 5413, 3),
			new WorldPoint(2068, 5413, 3),
			new WorldPoint(2068, 5416, 3),
			new WorldPoint(2067, 5416, 3),
			new WorldPoint(2067, 5421, 3),
			new WorldPoint(2068, 5421, 3)
		);

		ladderToSatchel6 = Arrays.asList(
			new WorldPoint(2076, 5396, 1),
			new WorldPoint(2078, 5396, 1),
			new WorldPoint(2078, 5398, 1),
			new WorldPoint(2080, 5398, 1),
			new WorldPoint(2080, 5399, 1),
			new WorldPoint(2082, 5399, 1),
			new WorldPoint(2082, 5405, 1),
			new WorldPoint(2084, 5405, 1),

			new WorldPoint(2082, 5405, 1),
			new WorldPoint(2082, 5407, 1),
			new WorldPoint(2081, 5407, 1),
			new WorldPoint(2081, 5408, 1),
			new WorldPoint(2078, 5408, 1),
			new WorldPoint(2078, 5409, 1),
			new WorldPoint(2075, 5409, 1),
			new WorldPoint(2075, 5407, 1),
			new WorldPoint(2075, 5409, 1),
			new WorldPoint(2073, 5409, 1),
			new WorldPoint(2071, 5409, 1),
			new WorldPoint(2071, 5408, 1),
			new WorldPoint(2068, 5408, 1),
			new WorldPoint(2068, 5411, 1),
			new WorldPoint(2067, 5411, 1),
			new WorldPoint(2067, 5412, 1),
			new WorldPoint(2066, 5412, 1),
			new WorldPoint(2066, 5414, 1),
			new WorldPoint(2067, 5414, 1),
			new WorldPoint(2067, 5415, 1),
			new WorldPoint(2069, 5415, 1),
			new WorldPoint(2067, 5415, 1),
			new WorldPoint(2067, 5417, 1),
			new WorldPoint(2067, 5423, 1),
			new WorldPoint(2068, 5423, 1),
			new WorldPoint(2068, 5426, 1),
			new WorldPoint(2067, 5426, 1),
			new WorldPoint(2067, 5429, 1),
			new WorldPoint(2068, 5429, 1),
			new WorldPoint(2068, 5430, 1),
			new WorldPoint(2069, 5430, 1),
			new WorldPoint(2069, 5431, 1),
			new WorldPoint(2069, 5430, 1),
			new WorldPoint(2071, 5430, 1),
			new WorldPoint(2071, 5429, 1),
			new WorldPoint(2074, 5429, 1),
			new WorldPoint(2074, 5430, 1),
			new WorldPoint(2075, 5430, 1),
			new WorldPoint(2075, 5431, 1),
			new WorldPoint(2076, 5431, 1),
			new WorldPoint(2076, 5432, 1),
			new WorldPoint(2078, 5432, 1),
			new WorldPoint(2078, 5430, 1),
			new WorldPoint(2081, 5430, 1),
			new WorldPoint(2081, 5431, 1),
			new WorldPoint(2081, 5430, 1),
			new WorldPoint(2082, 5430, 1),
			new WorldPoint(2082, 5429, 1),
			new WorldPoint(2082, 5428, 1),
			new WorldPoint(2081, 5428, 1),
			new WorldPoint(2081, 5426, 1),
			new WorldPoint(2084, 5426, 1),
			new WorldPoint(2082, 5426, 1),
			new WorldPoint(2082, 5424, 1),
			new WorldPoint(2082, 5423, 1),
			new WorldPoint(2081, 5423, 1),
			new WorldPoint(2081, 5420, 1),
			new WorldPoint(2082, 5420, 1),
			new WorldPoint(2082, 5419, 1),
			new WorldPoint(2083, 5419, 1),
			new WorldPoint(2083, 5418, 1),
			new WorldPoint(2085, 5418, 1),
			new WorldPoint(2083, 5418, 1),
			new WorldPoint(2083, 5415, 1),
			new WorldPoint(2083, 5412, 1),
			new WorldPoint(2081, 5412, 1),
			new WorldPoint(2083, 5412, 1),
			new WorldPoint(2083, 5410, 1),
			new WorldPoint(2085, 5410, 1),
			new WorldPoint(2085, 5408, 1),
			new WorldPoint(2090, 5408, 1),
			new WorldPoint(2090, 5410, 1),
			new WorldPoint(2090, 5408, 1),
			new WorldPoint(2091, 5408, 1),
			new WorldPoint(2092, 5408, 1),
			new WorldPoint(2092, 5409, 1),
			new WorldPoint(2096, 5409, 1),
			new WorldPoint(2096, 5408, 1),
			new WorldPoint(2098, 5408, 1)
		);
	}

	public List<QuestStep> getDisplaySteps()
	{
		return Arrays.asList(climbF0ToF1ForSatchels, climbF1ToF0ForSatchels, pickUp6Satchels, goUpFromSatchelsToF1, goFromF1WithSatchelToF0, goFromF0WithSatchelToF1,
			goDownToGunpowder, fillSatchels, goUpFromGunpowder, placeSatchel1, goDownFromSatchel1, placeSatchel2, goUpToSatchel3, placeSatchel3, goUpToSatchel4,
			placeSatchel4, placeSatchel5, goF2ToF1ForSatchel6, goF1ToF0ForSatchel6, placeSatchel6, leavePlatform);
	}
}
