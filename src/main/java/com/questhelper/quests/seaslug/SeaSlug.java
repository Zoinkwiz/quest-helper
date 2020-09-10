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
package com.questhelper.quests.seaslug;

import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.ZoneCondition;

@QuestDescriptor(
	quest = QuestHelperQuest.SEA_SLUG
)
public class SeaSlug extends BasicQuestHelper
{
	ItemRequirement swampPaste, glass, dampSticks, torch, litTorch, drySticks;

	ConditionForStep onPlatformGroundFloor, onPlatformFirstFloor, onPlatform, onIsland, hasGlass, hasDampSticks, hasTorch, hasLitTorch, hasDrySticks;

	QuestStep talkToCaroline, talkToHolgart, talkToHolgartWithSwampPaste, travelWithHolgart, pickupGlass, pickupDampSticks,
		climbLadder, talkToKennith, goDownLadder, goToIsland, goToIslandFromMainland, talkToKent, returnFromIsland, talkToBaileyForTorch, useGlassOnDampSticks,
		rubSticks, goBackUpLadder, talkToKennithAgain, kickWall, talkToKennithAfterKicking, activateCrane, goDownLadderAgain,
		returnWithHolgart, finishQuest, travelWithHolgartFreeingKennith;

	Zone platformFirstFloor, platformGroundFloor, island;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToCaroline);

		steps.put(1, talkToHolgart);

		steps.put(2, talkToHolgartWithSwampPaste);

		ConditionalStep investigateThePlatform = new ConditionalStep(this, travelWithHolgart);
		investigateThePlatform.addStep(onPlatformFirstFloor, talkToKennith);
		investigateThePlatform.addStep(onPlatformGroundFloor, climbLadder);

		steps.put(3, investigateThePlatform);

		ConditionalStep goFindKent = new ConditionalStep(this, travelWithHolgart);
		goFindKent.addStep(onPlatformGroundFloor, goToIsland);
		goFindKent.addStep(onPlatformFirstFloor, goDownLadder);

		steps.put(4, goFindKent);

		ConditionalStep talkWithKent = new ConditionalStep(this, goToIslandFromMainland);
		talkWithKent.addStep(onIsland, talkToKent);

		steps.put(5, talkWithKent);

		ConditionalStep lightTheTorch = new ConditionalStep(this, travelWithHolgartFreeingKennith);
		lightTheTorch.addStep(new Conditions(onPlatformFirstFloor, hasLitTorch), talkToKennithAgain);
		lightTheTorch.addStep(new Conditions(onPlatformGroundFloor, hasLitTorch), goBackUpLadder);
		lightTheTorch.addStep(new Conditions(onPlatform, hasTorch, hasDrySticks), rubSticks);
		lightTheTorch.addStep(new Conditions(onPlatform, hasTorch, hasGlass, hasDampSticks), useGlassOnDampSticks);
		lightTheTorch.addStep(new Conditions(onPlatform, hasTorch, hasGlass), pickupDampSticks);
		lightTheTorch.addStep(new Conditions(onPlatform, hasTorch), pickupGlass);
		lightTheTorch.addStep(onPlatform, talkToBaileyForTorch);
		lightTheTorch.addStep(onIsland, returnFromIsland);

		steps.put(6, lightTheTorch);

		ConditionalStep freeKennith = new ConditionalStep(this, travelWithHolgartFreeingKennith);
		freeKennith.addStep(onPlatformFirstFloor, talkToKennithAgain);
		freeKennith.addStep(new Conditions(onPlatformGroundFloor, hasLitTorch), goBackUpLadder);
		freeKennith.addStep(new Conditions(onPlatform, hasTorch, hasDrySticks), rubSticks);
		freeKennith.addStep(new Conditions(onPlatform, hasTorch, hasGlass, hasDampSticks), useGlassOnDampSticks);
		freeKennith.addStep(new Conditions(onPlatform, hasTorch, hasGlass), pickupDampSticks);
		freeKennith.addStep(new Conditions(onPlatform, hasTorch), pickupGlass);
		freeKennith.addStep(onPlatform, talkToBaileyForTorch);

		steps.put(7, freeKennith);

		ConditionalStep breakWall = new ConditionalStep(this, travelWithHolgartFreeingKennith);
		breakWall.addStep(onPlatformFirstFloor, kickWall);
		breakWall.addStep(new Conditions(onPlatformGroundFloor, hasLitTorch), goBackUpLadder);
		breakWall.addStep(new Conditions(onPlatformGroundFloor, hasTorch, hasDrySticks), rubSticks);
		breakWall.addStep(new Conditions(onPlatformGroundFloor, hasTorch, hasGlass, hasDampSticks), useGlassOnDampSticks);
		breakWall.addStep(new Conditions(onPlatformGroundFloor, hasTorch, hasGlass), pickupDampSticks);
		breakWall.addStep(new Conditions(onPlatformGroundFloor, hasTorch), pickupGlass);
		breakWall.addStep(onPlatformGroundFloor, talkToBaileyForTorch);

		steps.put(8, breakWall);

		ConditionalStep tellKennethYouBrokeWall = new ConditionalStep(this, travelWithHolgartFreeingKennith);
		tellKennethYouBrokeWall.addStep(onPlatformFirstFloor, talkToKennithAfterKicking);
		tellKennethYouBrokeWall.addStep(new Conditions(onPlatformGroundFloor, hasLitTorch), goBackUpLadder);
		tellKennethYouBrokeWall.addStep(new Conditions(onPlatformGroundFloor, hasTorch, hasDrySticks), rubSticks);
		tellKennethYouBrokeWall.addStep(new Conditions(onPlatformGroundFloor, hasTorch, hasGlass, hasDampSticks), useGlassOnDampSticks);
		tellKennethYouBrokeWall.addStep(new Conditions(onPlatformGroundFloor, hasTorch, hasGlass), pickupDampSticks);
		tellKennethYouBrokeWall.addStep(new Conditions(onPlatformGroundFloor, hasTorch), pickupGlass);
		tellKennethYouBrokeWall.addStep(onPlatformGroundFloor, talkToBaileyForTorch);

		steps.put(9, tellKennethYouBrokeWall);

		ConditionalStep turnTheCrane = new ConditionalStep(this, travelWithHolgartFreeingKennith);
		turnTheCrane.addStep(onPlatformFirstFloor, activateCrane);
		turnTheCrane.addStep(new Conditions(onPlatformGroundFloor, hasLitTorch), goBackUpLadder);
		turnTheCrane.addStep(new Conditions(onPlatformGroundFloor, hasTorch, hasDrySticks), rubSticks);
		turnTheCrane.addStep(new Conditions(onPlatformGroundFloor, hasTorch, hasGlass, hasDampSticks), useGlassOnDampSticks);
		turnTheCrane.addStep(new Conditions(onPlatformGroundFloor, hasTorch, hasGlass), pickupDampSticks);
		turnTheCrane.addStep(new Conditions(onPlatformGroundFloor, hasTorch), pickupGlass);
		turnTheCrane.addStep(onPlatformGroundFloor, talkToBaileyForTorch);

		steps.put(10, turnTheCrane);

		ConditionalStep finishUp = new ConditionalStep(this, finishQuest);
		finishUp.addStep(onPlatformGroundFloor, returnWithHolgart);
		finishUp.addStep(onPlatformFirstFloor, goDownLadderAgain);

		steps.put(11, finishUp);

		return steps;
	}

	public void setupItemRequirements() {
		swampPaste = new ItemRequirement("Swamp paste", ItemID.SWAMP_PASTE);
		dampSticks = new ItemRequirement("Damp sticks", ItemID.DAMP_STICKS);
		drySticks = new ItemRequirement("Dry sticks", ItemID.DRY_STICKS);
		torch = new ItemRequirement("Unlit torch", ItemID.UNLIT_TORCH);
		litTorch = new ItemRequirement("Lit torch", ItemID.LIT_TORCH);
		glass = new ItemRequirement("Broken glass", ItemID.BROKEN_GLASS_1469);

	}

	public void loadZones() {
		platformGroundFloor = new Zone(new WorldPoint(2760, 3271, 0), new WorldPoint(2795, 3293, 0));
		platformFirstFloor = new Zone(new WorldPoint(2760, 3271, 1), new WorldPoint(2795, 3293, 1));
		island = new Zone(new WorldPoint(2787, 3312, 0), new WorldPoint(2802, 3327, 0));
	}

	public void setupConditions() {
		hasDampSticks = new ItemRequirementCondition(dampSticks);
		hasDrySticks = new ItemRequirementCondition(drySticks);
		hasTorch = new ItemRequirementCondition(torch);
		hasLitTorch = new ItemRequirementCondition(litTorch);
		hasGlass = new ItemRequirementCondition(glass);
		onPlatformFirstFloor = new ZoneCondition(platformFirstFloor);
		onPlatformGroundFloor = new ZoneCondition(platformGroundFloor);
		onPlatform = new ZoneCondition(platformFirstFloor, platformGroundFloor);
		onIsland = new ZoneCondition(island);
	}

	public void setupSteps() {
		talkToCaroline = new NpcStep(this, NpcID.CAROLINE, new WorldPoint(2717, 3303, 0), "Talk to Caroline just north of Witchaven, east of East Ardougne.");
		talkToCaroline.addDialogStep("I suppose so, how do I get there?");
		talkToHolgart = new NpcStep(this, NpcID.HOLGART_7324, new WorldPoint(2717, 3303, 0), "Talk to Holgart nearby and give him some swamp paste.", swampPaste);
		talkToHolgartWithSwampPaste = new NpcStep(this, NpcID.HOLGART_7324, new WorldPoint(2717, 3303, 0), "Give Holgart some swamp paste.", swampPaste);
		talkToHolgart.addSubSteps(talkToHolgartWithSwampPaste);
		travelWithHolgart = new NpcStep(this, NpcID.HOLGART_7789, new WorldPoint(2717, 3303, 0), "Travel with Holgart to the fishing platform.");
		travelWithHolgart.addDialogStep("Will you take me there?");
		climbLadder = new ObjectStep(this, ObjectID.LADDER_18324, new WorldPoint(2784, 3286, 0), "Climb the ladder in the north east corner of the platform.");
		talkToKennith = new NpcStep(this, NpcID.KENNITH_5063, new WorldPoint(2765, 3289, 1), "Talk to Kennith from inside the cabin on the west side of the first floor.");
		goDownLadder = new ObjectStep(this,  ObjectID.LADDER_18325, new WorldPoint(2784, 3286, 1), "Go back down the ladder.");
		goToIsland = new NpcStep(this, NpcID.HOLGART_5070, new WorldPoint(2781, 3274, 0), "Travel with Holgart to a nearby island.");
		goToIslandFromMainland = new NpcStep(this, NpcID.HOLGART_7789, new WorldPoint(2717, 3303, 0), "Travel with Holgart north of Witchaven to find Kent.");
		goToIsland.addSubSteps(goToIsland);

		talkToKent = new NpcStep(this, NpcID.KENT, new WorldPoint(2794, 3322, 0), "Talk to Kent on the island.");
		returnFromIsland = new NpcStep(this, NpcID.HOLGART_5072, new WorldPoint(2801, 3320, 0), "Return to the platform with Holgart.");
		travelWithHolgartFreeingKennith = new NpcStep(this, NpcID.HOLGART_7789, new WorldPoint(2717, 3303, 0), "Travel with Holgart to the fishing platform.");
		returnFromIsland.addSubSteps(travelWithHolgartFreeingKennith);

		talkToBaileyForTorch = new NpcStep(this, NpcID.BAILEY, new WorldPoint(2764, 3275, 0), "Talk to Bailey for an unlit torch.");
		pickupGlass = new DetailedQuestStep(this, "Pick up the broken glass in the room.", glass);
		pickupDampSticks = new DetailedQuestStep(this, new WorldPoint(2784, 3289, 0), "Pick up the damp sticks in the north east corner of the platform.", dampSticks);
		useGlassOnDampSticks = new DetailedQuestStep(this, "Use the broken glass on damp sticks to dry them.", glass, dampSticks);
		rubSticks = new DetailedQuestStep(this, "Rub the dry sticks to light the unlit torch.");
		goBackUpLadder = new ObjectStep(this,  ObjectID.LADDER_18324, new WorldPoint(2784, 3286, 0), "Go up the ladder in the north east corner of the platform.");
		talkToKennithAgain = new NpcStep(this, NpcID.KENNITH_5063, new WorldPoint(2765, 3289, 1), "Talk to Kennith to the west.");
		kickWall = new ObjectStep(this, NullObjectID.NULL_18251, new WorldPoint(2768, 3289, 1), "Kick in the badly repaired wall east of Kennith.");
		talkToKennithAfterKicking = new NpcStep(this, NpcID.KENNITH_5063, new WorldPoint(2765, 3289, 1), "Talk to Kennith again.");
		activateCrane = new ObjectStep(this, ObjectID.CRANE_18327, new WorldPoint(2772, 3289, 1), "Rotate the crane east of Kennith's cabin.");
		goDownLadderAgain = new ObjectStep(this, ObjectID.LADDER_18325, new WorldPoint(2784, 3286, 1), "Go back down the ladder.");
		returnWithHolgart = new NpcStep(this, NpcID.HOLGART_5070, new WorldPoint(2781, 3274, 0), "Travel with Holgart back to the mainland.");
		finishQuest = new NpcStep(this, NpcID.CAROLINE, new WorldPoint(2717, 3303, 0), "Talk to Caroline to complete the quest.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(swampPaste);
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Collections.singletonList(talkToCaroline)), swampPaste));
		allSteps.add(new PanelDetails("Investigation", new ArrayList<>(Arrays.asList(talkToHolgart, travelWithHolgart, climbLadder, talkToKennith, goDownLadder, goToIsland))));
		allSteps.add(new PanelDetails("Talking with Kent", new ArrayList<>(Arrays.asList(talkToKent, returnFromIsland))));
		allSteps.add(new PanelDetails("Saving Kennith", new ArrayList<>(
			Arrays.asList(talkToBaileyForTorch, pickupGlass, pickupDampSticks, useGlassOnDampSticks, rubSticks, goBackUpLadder, talkToKennithAgain, kickWall,
				talkToKennithAfterKicking, activateCrane, goDownLadderAgain, returnWithHolgart, finishQuest))));

		return allSteps;
	}
}
