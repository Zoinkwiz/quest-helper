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
package com.questhelper.quests.cabinfever;

import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
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
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;

@QuestDescriptor(
	quest = QuestHelperQuest.CABIN_FEVER
)
public class CabinFever extends BasicQuestHelper
{
	ItemRequirement food, fuse1, tinderbox, ropes4, ropeHighlight, fuseHighlight, floorTinderbox, rope3, floorRope, tinderboxHighlight, planks2, planks4,
		planks6, hammer, tacks10, tacks20, tacks30, paste1, paste2, paste3, ropes2, loot10, plunderHighlight, barrel, gunpowder, ramrod, canister,
		powderHighlight, ramrodHighlight, canisterHighlight, fuses, cannonball, cannonballHighlight;

	ConditionForStep onBoatF0, onBoatF1, onBoatF2, onEnemyBoatF0, onEnemyBoatF1, onEnemyBoatF2, onEnemyBoat, onBoatAtDock, hasRopes4, hasTinderbox, hasFuseOrAdded,
		hasSetSail, onSail, onEnemySail, hasRope, addedFuse, explodedBarrel, hasPlank6, hasPlank4, hasPlank2, hasHammer, hasTacks30, hasTacks60, hasTacks90,
		hasPaste3, hasPaste6, hasPaste9, planked1, planked2, planked3, pasted1, pasted2, pasted3, hasPlanked1, hasPlanked2, hasPlanked3,
		hasRepaired1, hasRepaired2, hasRepaired3, hasRopes2, lootedAll, has10Plunder, hasBarrel, cannonBroken, addedPowder, hasFuse, hasCanister, hasRamrod,
		hasPowder, usedRamrod, usedCanister, usedFuse, firedCannon, canisterInWrong, hasCannonball, usedBalls;

	DetailedQuestStep talkToBill, goOnBillBoat, talkToBillOnBoat, takeTinderbox, leaveHullForSabo, goUpToSail, goUpToEnemySail,
		climbUpNetForSabo, useRopeOnSailForSabo, useFuseOnEnemyBarrel, lightEnemyFuse, climbEnemyNetAfterSabo, useRopeOnEnemySailAfterSabo, talkToBillAfterSabo,
		goDownToFixLeak, repairHole1, repairHole2, repairHole3, pasteHole1, pasteHole2, pasteHole3, goUpAfterRepair, pickUpRope, swingToBoat, swingToEnemyBoat,
		talkToBillAfterRepair, leaveHull, enterHull, leaveTop, leaveSail, leaveEnemyHull, leaveEnemySail, enterEnemyHull, leaveEnemyTop, enterEnemyHullForLoot,
		goUpAfterLoot, talkToBillAfterLoot, goDownForBarrel, goUpWithBarrel, useBarrel, talkToBillAfterBarrel, goDownForRamrod, goUpToCannon, usePowder,
		useRamrod, useCanister, useFuse, fireCannon, useRamrodToClean, getPowder, resetCannon, talkToBillAfterCanisterCannon, repeatCanisterSteps, repeatBallSteps,
		goUpToSailToLoot, useRopeOnSailToLoot, hopWorld, leaveEnemyHullWithLoot, climbNetWithLoot, useRopeOnSailWithLoot, enterHullWithLoot, useLootOnChest;

	DetailedQuestStep goDownForBalls, goUpToCannonWithBalls, usePowderForBalls,
		useRamrodForBalls, useBall, useFuseForBalls, fireCannonForBalls, useRamrodToCleanForBalls, getPowderForBalls;

	ObjectStep take4Ropes, take1Fuse, takeHoleItems1, takeHoleItems2, takeHoleItems3, takePasteHole1, takePasteHole2, takePasteHole3, take2Ropes, lootEnemyShip, takeBarrel, getRamrod,
		getBalls;

	Zone boatAtDock, boatF0, boatF1, boatF2, enemyBoatF0, enemyBoatF1, enemyBoatF2, sail, enemySail;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToBill);

		ConditionalStep setSail = new ConditionalStep(this, goOnBillBoat);
		setSail.addStep(onBoatAtDock, talkToBillOnBoat);

		steps.put(10, setSail);
		steps.put(20, setSail);
		// Set sail, 1741 0->1

		ConditionalStep sabotageSteps = new ConditionalStep(this, setSail);
		sabotageSteps.addStep(new Conditions(onEnemyBoatF1, addedFuse, hasTinderbox), lightEnemyFuse);
		sabotageSteps.addStep(new Conditions(onEnemySail, addedFuse, hasTinderbox), leaveEnemySail);
		sabotageSteps.addStep(new Conditions(onEnemySail, addedFuse, hasRope), swingToBoat);
		sabotageSteps.addStep(new Conditions(onEnemyBoatF1, addedFuse, hasRope), goUpToEnemySail);
		sabotageSteps.addStep(new Conditions(onEnemyBoatF1, addedFuse), pickUpRope);

		sabotageSteps.addStep(new Conditions(onEnemyBoatF1, hasFuseOrAdded), useFuseOnEnemyBarrel);
		sabotageSteps.addStep(new Conditions(onEnemyBoatF0, hasFuseOrAdded), leaveEnemyHull);
		sabotageSteps.addStep(new Conditions(onEnemySail, hasFuseOrAdded), leaveEnemySail);

		sabotageSteps.addStep(new Conditions(onSail, hasRopes4, hasFuseOrAdded, hasTinderbox), useRopeOnSailForSabo);
		sabotageSteps.addStep(new Conditions(onBoatF1, hasRopes4, hasFuseOrAdded, hasTinderbox), climbUpNetForSabo);
		sabotageSteps.addStep(new Conditions(onBoatF0, hasRopes4, hasFuseOrAdded, hasTinderbox), leaveHullForSabo);
		sabotageSteps.addStep(new Conditions(onBoatF0, hasRopes4, hasFuseOrAdded), takeTinderbox);
		sabotageSteps.addStep(new Conditions(onBoatF0, hasRopes4), take1Fuse);

		sabotageSteps.addStep(new Conditions(onEnemySail, hasRope), swingToBoat);
		sabotageSteps.addStep(new Conditions(onEnemyBoatF1, hasRope), goUpToEnemySail);
		sabotageSteps.addStep(onEnemySail, leaveEnemySail);
		sabotageSteps.addStep(onEnemyBoatF2, leaveEnemyTop);
		sabotageSteps.addStep(onEnemyBoatF1, pickUpRope);
		sabotageSteps.addStep(onEnemyBoatF0, leaveEnemyHull);
		sabotageSteps.addStep(onSail, leaveSail);
		sabotageSteps.addStep(onBoatF2, leaveTop);
		sabotageSteps.addStep(onBoatF1, enterHull);
		sabotageSteps.addStep(onBoatF0, take4Ropes);

		steps.put(30, sabotageSteps);

		ConditionalStep talkToBillAfterSaboSteps = new ConditionalStep(this, setSail);
		talkToBillAfterSaboSteps.addStep(new Conditions(onEnemySail, hasRope), useRopeOnEnemySailAfterSabo);
		talkToBillAfterSaboSteps.addStep(new Conditions(onEnemyBoatF1, hasRope), climbEnemyNetAfterSabo);
		talkToBillAfterSaboSteps.addStep(onEnemySail, leaveEnemySail);
		talkToBillAfterSaboSteps.addStep(onEnemyBoatF2, leaveEnemyTop);
		talkToBillAfterSaboSteps.addStep(onEnemyBoatF1, pickUpRope);
		talkToBillAfterSaboSteps.addStep(onEnemyBoatF0, leaveEnemyHull);
		talkToBillAfterSaboSteps.addStep(onSail, leaveSail);
		talkToBillAfterSaboSteps.addStep(onBoatF2, leaveTop);
		talkToBillAfterSaboSteps.addStep(onBoatF1, talkToBillAfterSabo);
		talkToBillAfterSaboSteps.addStep(onBoatF0, leaveHull);

		steps.put(40, talkToBillAfterSaboSteps);

		// TODO: Make this at least a bit less terrible
		ConditionalStep repairWalls = new ConditionalStep(this, setSail);
		repairWalls.addStep(new Conditions(onBoatF0, pasted1, pasted2, planked3, hasHammer, hasPaste3), pasteHole3);
		repairWalls.addStep(new Conditions(onBoatF0, pasted1, pasted2, hasHammer, hasPaste3, hasPlank2, hasTacks30), repairHole3);
		repairWalls.addStep(new Conditions(onBoatF0, pasted1, planked2, hasHammer, hasPaste6, hasPlank2, hasTacks30), pasteHole2);
		repairWalls.addStep(new Conditions(onBoatF0, pasted1, hasHammer, hasPaste6, hasPlank4, hasTacks60), repairHole2);
		repairWalls.addStep(new Conditions(onBoatF0, planked1, hasHammer, hasPaste9, hasPlank4, hasTacks60), pasteHole1);
		repairWalls.addStep(new Conditions(onBoatF0, hasHammer, hasPaste9, hasPlank6, hasTacks90), repairHole1);

		repairWalls.addStep(new Conditions(onBoatF0, hasRepaired3, hasPlanked3), takePasteHole3);
		repairWalls.addStep(new Conditions(onBoatF0, hasRepaired2), takeHoleItems3);
		repairWalls.addStep(new Conditions(onBoatF0, hasRepaired1, hasPlanked2), takePasteHole2);
		repairWalls.addStep(new Conditions(onBoatF0, hasRepaired1), takeHoleItems2);
		repairWalls.addStep(new Conditions(onBoatF0, planked1), takePasteHole1);
		repairWalls.addStep(onBoatF0, takeHoleItems1);

		repairWalls.addStep(new Conditions(onEnemySail, hasRope), swingToBoat);
		repairWalls.addStep(new Conditions(onEnemyBoatF1, hasRope), goUpToEnemySail);
		repairWalls.addStep(onEnemySail, leaveEnemySail);
		repairWalls.addStep(onEnemyBoatF2, leaveEnemyTop);
		repairWalls.addStep(onEnemyBoatF1, pickUpRope);
		repairWalls.addStep(onEnemyBoatF0, leaveEnemyHull);
		repairWalls.addStep(onSail, leaveSail);
		repairWalls.addStep(onBoatF2, leaveTop);
		repairWalls.addStep(onBoatF1, goDownToFixLeak);

		steps.put(50, repairWalls);

		ConditionalStep talkToBillAfterRepairing = new ConditionalStep(this, setSail);
		talkToBillAfterRepairing.addStep(new Conditions(onEnemySail, hasRope), swingToBoat);
		talkToBillAfterRepairing.addStep(new Conditions(onEnemyBoatF1, hasRope), goUpToEnemySail);
		talkToBillAfterRepairing.addStep(onEnemySail, leaveEnemySail);
		talkToBillAfterRepairing.addStep(onEnemyBoatF2, leaveEnemyTop);
		talkToBillAfterRepairing.addStep(onEnemyBoatF1, pickUpRope);
		talkToBillAfterRepairing.addStep(onEnemyBoatF0, leaveEnemyHull);
		talkToBillAfterRepairing.addStep(onSail, leaveSail);
		talkToBillAfterRepairing.addStep(onBoatF2, leaveTop);
		talkToBillAfterRepairing.addStep(onBoatF1, talkToBillAfterRepair);
		talkToBillAfterRepairing.addStep(onBoatF0, goUpAfterRepair);

		steps.put(60, talkToBillAfterRepairing);

		ConditionalStep lootingSteps = new ConditionalStep(this, setSail);
		lootingSteps.addStep(new Conditions(onBoatF0, has10Plunder), useLootOnChest);
		lootingSteps.addStep(new Conditions(onBoatF1, has10Plunder), enterHullWithLoot);
		lootingSteps.addStep(new Conditions(onEnemySail, has10Plunder, hasRope), useRopeOnSailWithLoot);
		lootingSteps.addStep(new Conditions(onEnemySail, has10Plunder), leaveEnemySail);
		lootingSteps.addStep(new Conditions(onEnemyBoatF1, has10Plunder, hasRope), climbNetWithLoot);
		lootingSteps.addStep(new Conditions(onEnemyBoatF1, has10Plunder), pickUpRope);
		lootingSteps.addStep(new Conditions(onEnemyBoatF0, has10Plunder), leaveEnemyHullWithLoot);
		lootingSteps.addStep(new Conditions(onEnemyBoatF0, lootedAll), hopWorld);
		// TODO: Make this change if you've already handed in some plunder
		lootingSteps.addStep(onEnemyBoatF0, lootEnemyShip);
		lootingSteps.addStep(onEnemyBoatF1, enterEnemyHullForLoot);
		lootingSteps.addStep(onEnemyBoatF2, leaveEnemyTop);
		lootingSteps.addStep(onEnemySail, leaveEnemySail);
		lootingSteps.addStep(new Conditions(onSail, hasRopes2), useRopeOnSailToLoot);
		lootingSteps.addStep(new Conditions(onBoatF1, hasRopes2), goUpToSailToLoot);
		lootingSteps.addStep(new Conditions(onBoatF0, hasRopes2), leaveHull);
		lootingSteps.addStep(onSail, leaveSail);
		lootingSteps.addStep(onBoatF2, leaveTop);
		lootingSteps.addStep(onBoatF1, enterHull);
		lootingSteps.addStep(onBoatF0, take2Ropes);

		steps.put(70, lootingSteps);

		ConditionalStep talkToBillAfterLooting = new ConditionalStep(this, setSail);
		talkToBillAfterLooting.addStep(new Conditions(onEnemySail, hasRope), swingToBoat);
		talkToBillAfterLooting.addStep(new Conditions(onEnemyBoatF1, hasRope), goUpToEnemySail);
		talkToBillAfterLooting.addStep(onEnemySail, leaveEnemySail);
		talkToBillAfterLooting.addStep(onEnemyBoatF2, leaveEnemyTop);
		talkToBillAfterLooting.addStep(onEnemyBoatF1, pickUpRope);
		talkToBillAfterLooting.addStep(onEnemyBoatF0, leaveEnemyHull);
		talkToBillAfterLooting.addStep(onSail, leaveSail);
		talkToBillAfterLooting.addStep(onBoatF2, leaveTop);
		talkToBillAfterLooting.addStep(onBoatF1, talkToBillAfterLoot);
		talkToBillAfterLooting.addStep(onBoatF0, goUpAfterLoot);

		steps.put(80, talkToBillAfterLooting);

		ConditionalStep repairCannonSteps = new ConditionalStep(this, setSail);
		repairCannonSteps.addStep(new Conditions(onBoatF1, hasBarrel), useBarrel);
		repairCannonSteps.addStep(new Conditions(onBoatF0, hasBarrel), goUpWithBarrel);
		repairCannonSteps.addStep(new Conditions(onEnemySail, hasRope), swingToBoat);
		repairCannonSteps.addStep(new Conditions(onEnemyBoatF1, hasRope), goUpToEnemySail);
		repairCannonSteps.addStep(onEnemySail, leaveEnemySail);
		repairCannonSteps.addStep(onEnemyBoatF2, leaveEnemyTop);
		repairCannonSteps.addStep(onEnemyBoatF1, pickUpRope);
		repairCannonSteps.addStep(onEnemyBoatF0, leaveEnemyHull);
		repairCannonSteps.addStep(onSail, leaveSail);
		repairCannonSteps.addStep(onBoatF2, leaveTop);
		repairCannonSteps.addStep(onBoatF1, goDownForBarrel);
		repairCannonSteps.addStep(onBoatF0, takeBarrel);

		steps.put(90, repairCannonSteps);

		ConditionalStep billAfterCannonRepair = new ConditionalStep(this, setSail);
		billAfterCannonRepair.addStep(new Conditions(onEnemySail, hasRope), swingToBoat);
		billAfterCannonRepair.addStep(new Conditions(onEnemyBoatF1, hasRope), goUpToEnemySail);
		billAfterCannonRepair.addStep(onEnemySail, leaveEnemySail);
		billAfterCannonRepair.addStep(onEnemyBoatF2, leaveEnemyTop);
		billAfterCannonRepair.addStep(onEnemyBoatF1, pickUpRope);
		billAfterCannonRepair.addStep(onEnemyBoatF0, leaveEnemyHull);
		billAfterCannonRepair.addStep(onSail, leaveSail);
		billAfterCannonRepair.addStep(onBoatF2, leaveTop);
		billAfterCannonRepair.addStep(onBoatF1, talkToBillAfterBarrel);
		billAfterCannonRepair.addStep(onBoatF0, leaveHull);

		steps.put(100, billAfterCannonRepair);

		ConditionalStep fireCannons = new ConditionalStep(this, setSail);
		fireCannons.addStep(new Conditions(onBoatF1, cannonBroken, hasBarrel), useBarrel);
		fireCannons.addStep(new Conditions(onBoatF0, cannonBroken, hasBarrel), goUpWithBarrel);
		fireCannons.addStep(new Conditions(onBoatF1, cannonBroken), goDownForBarrel);
		fireCannons.addStep(new Conditions(onBoatF0, cannonBroken), takeBarrel);

		fireCannons.addStep(new Conditions(onBoatF1, canisterInWrong), resetCannon);
		fireCannons.addStep(new Conditions(onBoatF1, firedCannon), useRamrodToClean);
		fireCannons.addStep(new Conditions(onBoatF1, usedFuse, usedCanister), fireCannon);
		fireCannons.addStep(new Conditions(onBoatF1, hasFuse, usedCanister), useFuse);
		fireCannons.addStep(new Conditions(onBoatF1, hasFuse, hasCanister, usedRamrod), useCanister);
		fireCannons.addStep(new Conditions(onBoatF1, hasFuse, hasCanister, hasRamrod, addedPowder), useRamrod);
		fireCannons.addStep(new Conditions(onBoatF1, hasFuse, hasCanister, hasRamrod, hasPowder), usePowder);
		fireCannons.addStep(new Conditions(onBoatF1, hasFuse, hasCanister, hasRamrod), getPowder);
		fireCannons.addStep(new Conditions(onBoatF0, hasFuse, hasCanister, hasRamrod), goUpToCannon);

		fireCannons.addStep(new Conditions(onEnemySail, hasRope), swingToBoat);
		fireCannons.addStep(new Conditions(onEnemyBoatF1, hasRope), goUpToEnemySail);
		fireCannons.addStep(onEnemySail, leaveEnemySail);
		fireCannons.addStep(onEnemyBoatF2, leaveEnemyTop);
		fireCannons.addStep(onEnemyBoatF1, pickUpRope);
		fireCannons.addStep(onEnemyBoatF0, leaveEnemyHull);
		fireCannons.addStep(onSail, leaveSail);
		fireCannons.addStep(onBoatF2, leaveTop);
		fireCannons.addStep(onBoatF1, goDownForRamrod);
		fireCannons.addStep(onBoatF0, getRamrod);

		steps.put(110, fireCannons);

		ConditionalStep talkToBillAfterCanisters = new ConditionalStep(this, setSail);
		talkToBillAfterCanisters.addStep(new Conditions(onEnemySail, hasRope), swingToBoat);
		talkToBillAfterCanisters.addStep(new Conditions(onEnemyBoatF1, hasRope), goUpToEnemySail);
		talkToBillAfterCanisters.addStep(onEnemySail, leaveEnemySail);
		talkToBillAfterCanisters.addStep(onEnemyBoatF2, leaveEnemyTop);
		talkToBillAfterCanisters.addStep(onEnemyBoatF1, pickUpRope);
		talkToBillAfterCanisters.addStep(onEnemyBoatF0, leaveEnemyHull);
		talkToBillAfterCanisters.addStep(onSail, leaveSail);
		talkToBillAfterCanisters.addStep(onBoatF2, leaveTop);
		talkToBillAfterCanisters.addStep(onBoatF1, talkToBillAfterCanisterCannon);
		talkToBillAfterCanisters.addStep(onBoatF0, leaveHull);

		steps.put(120, talkToBillAfterCanisters);

		ConditionalStep fireCannonsWithBalls = new ConditionalStep(this, setSail);
		fireCannonsWithBalls.addStep(new Conditions(onBoatF1, cannonBroken, hasBarrel), useBarrel);
		fireCannonsWithBalls.addStep(new Conditions(onBoatF0, cannonBroken, hasBarrel), goUpWithBarrel);
		fireCannonsWithBalls.addStep(new Conditions(onBoatF1, cannonBroken), goDownForBarrel);
		fireCannonsWithBalls.addStep(new Conditions(onBoatF0, cannonBroken), takeBarrel);

		fireCannonsWithBalls.addStep(new Conditions(onBoatF1, canisterInWrong), resetCannon);
		fireCannonsWithBalls.addStep(new Conditions(onBoatF1, firedCannon), useRamrodToCleanForBalls);
		fireCannonsWithBalls.addStep(new Conditions(onBoatF1, usedFuse, usedBalls), fireCannonForBalls);
		fireCannonsWithBalls.addStep(new Conditions(onBoatF1, hasFuse, usedBalls), useFuseForBalls);
		fireCannonsWithBalls.addStep(new Conditions(onBoatF1, hasFuse, hasCannonball, usedRamrod), useBall);
		fireCannonsWithBalls.addStep(new Conditions(onBoatF1, hasFuse, hasCannonball, hasRamrod, addedPowder), useRamrodForBalls);
		fireCannonsWithBalls.addStep(new Conditions(onBoatF1, hasFuse, hasCannonball, hasRamrod, hasPowder), usePowderForBalls);
		fireCannonsWithBalls.addStep(new Conditions(onBoatF1, hasFuse, hasCannonball, hasRamrod), getPowderForBalls);
		fireCannonsWithBalls.addStep(new Conditions(onBoatF0, hasFuse, hasCannonball, hasRamrod), goUpToCannonWithBalls);

		fireCannonsWithBalls.addStep(new Conditions(onEnemySail, hasRope), swingToBoat);
		fireCannonsWithBalls.addStep(new Conditions(onEnemyBoatF1, hasRope), goUpToEnemySail);
		fireCannonsWithBalls.addStep(onEnemySail, leaveEnemySail);
		fireCannonsWithBalls.addStep(onEnemyBoatF2, leaveEnemyTop);
		fireCannonsWithBalls.addStep(onEnemyBoatF1, pickUpRope);
		fireCannonsWithBalls.addStep(onEnemyBoatF0, leaveEnemyHull);
		fireCannonsWithBalls.addStep(onSail, leaveSail);
		fireCannonsWithBalls.addStep(onBoatF2, leaveTop);
		fireCannonsWithBalls.addStep(onBoatF1, goDownForBalls);
		fireCannonsWithBalls.addStep(onBoatF0, getBalls);

		steps.put(130, fireCannonsWithBalls);

		return steps;
	}

	public void setupItemRequirements()
	{
		food = new ItemRequirement("Food", -1, -1);
		fuse1 = new ItemRequirement("Fuse", ItemID.FUSE);
		ropes4 = new ItemRequirement("Rope", ItemID.ROPE, 4);
		ropes2 = new ItemRequirement("Rope", ItemID.ROPE, 2);
		ropeHighlight = new ItemRequirement("Rope", ItemID.ROPE);
		ropeHighlight.setHighlightInInventory(true);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		tinderbox.setTip("You can get another from the ship's hull");
		tinderboxHighlight = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		tinderboxHighlight.setHighlightInInventory(true);
		floorTinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX_7156);
		rope3 = new ItemRequirement("Rope", ItemID.ROPE, 3);
		floorRope = new ItemRequirement("Rope", ItemID.ROPE_7155);

		fuseHighlight = new ItemRequirement("Fuse", ItemID.FUSE);
		fuseHighlight.setHighlightInInventory(true);

		planks2 = new ItemRequirement("Repair plank", ItemID.REPAIR_PLANK_7148, 2);
		planks4 = new ItemRequirement("Repair plank", ItemID.REPAIR_PLANK_7148, 4);
		planks6 = new ItemRequirement("Repair plank", ItemID.REPAIR_PLANK_7148, 6);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER);
		tacks10 = new ItemRequirement("Tacks", ItemID.TACKS, 10);
		tacks20 = new ItemRequirement("Tacks", ItemID.TACKS, 20);
		tacks30 = new ItemRequirement("Tacks", ItemID.TACKS, 30);
		paste1 = new ItemRequirement("Swamp paste", ItemID.SWAMP_PASTE, 1);
		paste2 = new ItemRequirement("Swamp paste", ItemID.SWAMP_PASTE, 2);
		paste3 = new ItemRequirement("Swamp paste", ItemID.SWAMP_PASTE, 3);
		loot10 = new ItemRequirement("Plunder", ItemID.PLUNDER, 10);
		plunderHighlight = new ItemRequirement("Plunder", ItemID.PLUNDER);
		plunderHighlight.setHighlightInInventory(true);

		barrel = new ItemRequirement("Cannon barrel", ItemID.CANNON_BARREL);

		gunpowder = new ItemRequirement("Gunpowder", ItemID.GUNPOWDER);
		canister = new ItemRequirement("A few canister", ItemID.CANISTER_7149);
		fuses = new ItemRequirement("A few fuses", ItemID.FUSE);
		ramrod = new ItemRequirement("Ramrod", ItemID.RAMROD);

		powderHighlight = new ItemRequirement("Gunpowder", ItemID.GUNPOWDER);
		powderHighlight.setHighlightInInventory(true);
		canisterHighlight = new ItemRequirement("Canister", ItemID.CANISTER_7149);
		canisterHighlight.setHighlightInInventory(true);
		ramrodHighlight = new ItemRequirement("Ramrod", ItemID.RAMROD);
		ramrodHighlight.setHighlightInInventory(true);
		fuseHighlight = new ItemRequirement("Fuse", ItemID.FUSE);
		fuseHighlight.setHighlightInInventory(true);

		cannonball = new ItemRequirement("A few Cannon balls", ItemID.CANNON_BALL_7119);

		cannonballHighlight = new ItemRequirement("Cannon ball", ItemID.CANNON_BALL_7119);
		cannonballHighlight.setHighlightInInventory(true);
	}

	public void loadZones()
	{
		boatAtDock = new Zone(new WorldPoint(3712, 3488, 0), new WorldPoint(3716, 3507, 2));
		boatF0 = new Zone(new WorldPoint(1817, 4839, 0), new WorldPoint(1813, 4821, 0));
		boatF1 = new Zone(new WorldPoint(1817, 4839, 1), new WorldPoint(1813, 4828, 1));
		boatF2 = new Zone(new WorldPoint(1817, 4827, 2), new WorldPoint(1813, 4824, 2));
		enemyBoatF0 = new Zone(new WorldPoint(1825, 4842, 0), new WorldPoint(1823, 4826, 0));
		enemyBoatF1 = new Zone(new WorldPoint(1826, 4837, 1), new WorldPoint(1822, 4826, 1));
		enemyBoatF2 = new Zone(new WorldPoint(1826, 4842, 2), new WorldPoint(1822, 4838, 2));
		sail = new Zone(new WorldPoint(1816, 4830, 2), new WorldPoint(1817, 4830, 2));
		enemySail = new Zone(new WorldPoint(1822, 4835, 2), new WorldPoint(1823, 4835, 2));
	}

	public void setupConditions()
	{
		onBoatAtDock = new ZoneCondition(boatAtDock);
		onBoatF0 = new ZoneCondition(boatF0);
		onBoatF1 = new ZoneCondition(boatF1);
		onBoatF2 = new ZoneCondition(boatF2);
		onSail = new ZoneCondition(sail);

		onEnemyBoatF0 = new ZoneCondition(enemyBoatF0);
		onEnemyBoatF1 = new ZoneCondition(enemyBoatF1);
		onEnemyBoatF2 = new ZoneCondition(enemyBoatF2);
		onEnemySail = new ZoneCondition(enemySail);
		onEnemyBoat = new ZoneCondition(enemyBoatF0, enemyBoatF1, enemyBoatF2, enemySail);

		hasSetSail = new VarbitCondition(1741, 1);
		hasRopes4 = new ItemRequirementCondition(ropes4);
		hasRopes2 = new ItemRequirementCondition(ropes2);
		hasTinderbox = new ItemRequirementCondition(tinderbox);
		hasRope = new ItemRequirementCondition(ropeHighlight);

		addedFuse = new VarbitCondition(1756, 2);
		hasFuseOrAdded = new Conditions(LogicType.OR, new ItemRequirementCondition(fuse1), addedFuse);

		explodedBarrel = new VarbitCondition(1756, 1);
		// 1740 1 if swinging

		planked1 = new VarbitCondition(1751, 1);
		planked2 = new VarbitCondition(1757, 1);
		planked3 = new VarbitCondition(1758, 1);
		pasted1 = new VarbitCondition(1751, 2);
		pasted2 = new VarbitCondition(1757, 2);
		pasted3 = new VarbitCondition(1758, 2);

		hasHammer = new ItemRequirementCondition(hammer);
		hasPlank2 = new ItemRequirementCondition(planks2);
		hasPlank4 = new ItemRequirementCondition(planks4);
		hasPlank6 = new ItemRequirementCondition(planks6);
		hasTacks30 = new ItemRequirementCondition(tacks10);
		hasTacks60 = new ItemRequirementCondition(tacks20);
		hasTacks90 = new ItemRequirementCondition(tacks30);
		hasPaste3 = new ItemRequirementCondition(paste1);
		hasPaste6 = new ItemRequirementCondition(paste2);
		hasPaste9 = new ItemRequirementCondition(paste3);

		hasPlanked1 = new VarbitCondition(1759, 1);
		hasPlanked2 = new VarbitCondition(1759, 2);
		hasPlanked3 = new VarbitCondition(1759, 3);

		hasRepaired1 = new VarbitCondition(1760, 1);
		hasRepaired2 = new VarbitCondition(1760, 2);
		hasRepaired3 = new VarbitCondition(1760, 3);

		lootedAll = new Conditions(new VarbitCondition(1753, 1), new VarbitCondition(1754, 1), new VarbitCondition(1755, 1));

		has10Plunder = new ItemRequirementCondition(loot10);
		hasBarrel = new ItemRequirementCondition(barrel);

		cannonBroken = new VarbitCondition(1741, 1);
		addedPowder = new VarbitCondition(1742, 1);
		usedRamrod = new VarbitCondition(1743, 1);
		usedCanister = new VarbitCondition(1744, 2);
		usedBalls = new VarbitCondition(1744, 1);
		usedFuse = new VarbitCondition(1741, 3);
		firedCannon = new VarbitCondition(1746, 1);

		hasFuse = new ItemRequirementCondition(fuse1);
		hasCanister = new ItemRequirementCondition(canister);
		hasRamrod = new ItemRequirementCondition(ramrod);
		hasPowder = new ItemRequirementCondition(gunpowder);
		hasCannonball = new ItemRequirementCondition(cannonball);

		canisterInWrong = new VarbitCondition(1747, 1);

		// 1752 = num plunder stashed

	}

	public void setupSteps()
	{
		talkToBill = new NpcStep(this, NpcID.BILL_TEACH, new WorldPoint(3678, 3494, 0), "Talk to Bill Teach in Port Phasmatys.");
		talkToBill.addDialogSteps("Yes, I've always wanted to be a pirate!", "Yes, I am a woman of my word.", "Yes, I am a man of my word.");
		goOnBillBoat = new ObjectStep(this, ObjectID.GANGPLANK_11209, new WorldPoint(3710, 3496, 0), "Talk to Bill Teach on his boat in Port Phasmatys.");
		talkToBillOnBoat = new NpcStep(this, NpcID.BILL_TEACH, new WorldPoint(3714, 3496, 1), "Talk to Bill Teach on his boat in Port Phasmatys.");
		talkToBillOnBoat.addDialogStep("Let's go Cap'n!");
		talkToBillOnBoat.addSubSteps(goOnBillBoat);

		leaveHull = new ObjectStep(this, ObjectID.SHIPS_LADDER_11308, new WorldPoint(1815, 4836, 0), "Leave the hull.");
		enterHull = new ObjectStep(this, ObjectID.SHIPS_LADDER_11309, new WorldPoint(1815, 4836, 1), "Enter the ship's hull.");
		leaveTop = new ObjectStep(this, ObjectID.SHIPS_LADDER_11290, new WorldPoint(1813, 4828, 2), "Go back down to the deck.");
		leaveSail = new ObjectStep(this, ObjectID.CLIMBING_NET, new WorldPoint(1816, 4831, 2), "Climb back down the net.");
		goUpToSail = new ObjectStep(this, ObjectID.CLIMBING_NET_11310, new WorldPoint(1816, 4831, 1), "Climb the climbing net.");
		leaveEnemyHull = new ObjectStep(this, ObjectID.SHIPS_LADDER_11308, new WorldPoint(1824, 4829, 0), "Leave the hull.");
		enterEnemyHull = new ObjectStep(this, ObjectID.SHIPS_LADDER_11309, new WorldPoint(1824, 4829, 1), "Enter the ship's hull.");
		leaveEnemyTop = new ObjectStep(this, ObjectID.SHIPS_LADDER_11290, new WorldPoint(1822, 4837, 2), "Go back down to the deck.");
		leaveEnemySail = new ObjectStep(this, ObjectID.CLIMBING_NET, new WorldPoint(1823, 4834, 2), "Climb back down the net.");
		goUpToEnemySail = new ObjectStep(this, ObjectID.CLIMBING_NET_11310, new WorldPoint(1823, 4834, 1), "Climb the climbing net.");

		pickUpRope = new DetailedQuestStep(this, new WorldPoint(1822, 4827, 1), "Pick up the nearby rope.", floorRope);

		take1Fuse = new ObjectStep(this, ObjectID.GUN_LOCKER, new WorldPoint(1816, 4833, 0), "Search the gun locker for 1 fuse.", fuse1);
		take1Fuse.addAlternateObjects(ObjectID.GUN_LOCKER_11249);
		take4Ropes = new ObjectStep(this, ObjectID.REPAIR_LOCKER, new WorldPoint(1814, 4832, 0), "Search the repair locker for 4 ropes.", ropes4);
		take4Ropes.addAlternateObjects(ObjectID.REPAIR_LOCKER_11247);
		takeTinderbox = new DetailedQuestStep(this, new WorldPoint(1814, 4825, 0), "Pick up the tinderbox nearby.", floorTinderbox);

		leaveHullForSabo = new ObjectStep(this, ObjectID.SHIPS_LADDER_11308, new WorldPoint(1815, 4836, 0), "Go up to the deck.");
		climbUpNetForSabo = new ObjectStep(this, ObjectID.CLIMBING_NET_11310, new WorldPoint(1816, 4831, 1), "Climb the climbing net.");
		useRopeOnSailForSabo = new ObjectStep(this, ObjectID.HOISTED_SAIL_11297, new WorldPoint(1817, 4830, 2), "Use a rope on the hoisted sail.", ropeHighlight);
		useRopeOnSailForSabo.addIcon(ItemID.ROPE);

		useFuseOnEnemyBarrel = new ObjectStep(this, NullObjectID.NULL_11237, new WorldPoint(1822, 4831, 1), "Use the fuse on the pirate's powder barrel.", fuseHighlight);
		useFuseOnEnemyBarrel.addIcon(ItemID.FUSE);

		lightEnemyFuse = new ObjectStep(this, NullObjectID.NULL_11242, new WorldPoint(1824, 4831, 1), "Use the tinderbox on the fuse.", tinderboxHighlight);
		lightEnemyFuse.addIcon(ItemID.TINDERBOX);

		swingToBoat = new ObjectStep(this, ObjectID.HOISTED_SAIL_11297, new WorldPoint(1822, 4835, 2), "Use a rope on the hoisted sail.", ropeHighlight);
		swingToBoat.addIcon(ItemID.ROPE);

		swingToEnemyBoat = new ObjectStep(this, ObjectID.HOISTED_SAIL_11297, new WorldPoint(1817, 4830, 2), "Use a rope on the hoisted sail.", ropeHighlight);
		swingToEnemyBoat.addIcon(ItemID.ROPE);

		climbEnemyNetAfterSabo = new ObjectStep(this, ObjectID.CLIMBING_NET_11310, new WorldPoint(1823, 4834, 1), "Climb the climbing net.");
		useRopeOnEnemySailAfterSabo = new ObjectStep(this, ObjectID.HOISTED_SAIL_11297, new WorldPoint(1822, 4835, 2), "Use a rope on the hoisted sail.", ropeHighlight);
		useRopeOnEnemySailAfterSabo.addIcon(ItemID.ROPE);
		talkToBillAfterSabo = new NpcStep(this, NpcID.BILL_TEACH_4014, new WorldPoint(1815, 4834, 1), "Talk to Bill Teach.");

		goDownToFixLeak = new ObjectStep(this, ObjectID.SHIPS_LADDER_11309, new WorldPoint(1815, 4836, 1), "Enter the ship's hull.");
		takeHoleItems1 = new ObjectStep(this, ObjectID.REPAIR_LOCKER, new WorldPoint(1814, 4832, 0), "Search the repair locker for repair items.", hammer, planks6, tacks30, paste3);
		takeHoleItems1.addAlternateObjects(ObjectID.REPAIR_LOCKER_11247);
		takeHoleItems2 = new ObjectStep(this, ObjectID.REPAIR_LOCKER, new WorldPoint(1814, 4832, 0), "Search the repair locker for repair items.", hammer, planks4, tacks20, paste2);
		takeHoleItems2.addAlternateObjects(ObjectID.REPAIR_LOCKER_11247);
		takeHoleItems3 = new ObjectStep(this, ObjectID.REPAIR_LOCKER, new WorldPoint(1814, 4832, 0), "Search the repair locker for repair items.", hammer, planks2, tacks10, paste1);
		takeHoleItems3.addAlternateObjects(ObjectID.REPAIR_LOCKER_11247);

		takePasteHole1 = new ObjectStep(this, ObjectID.REPAIR_LOCKER, new WorldPoint(1814, 4832, 0), "Search the repair locker for repair items.", hammer, planks4, tacks20, paste3);
		takePasteHole1.addAlternateObjects(ObjectID.REPAIR_LOCKER_11247);
		takePasteHole2 = new ObjectStep(this, ObjectID.REPAIR_LOCKER, new WorldPoint(1814, 4832, 0), "Search the repair locker for repair items.", hammer, planks2, tacks10, paste2);
		takePasteHole2.addAlternateObjects(ObjectID.REPAIR_LOCKER_11247);
		takePasteHole3 = new ObjectStep(this, ObjectID.REPAIR_LOCKER, new WorldPoint(1814, 4832, 0), "Search the repair locker for repair items.", hammer, paste1);
		takePasteHole3.addAlternateObjects(ObjectID.REPAIR_LOCKER_11247);

		takeHoleItems1.addSubSteps(takeHoleItems2, takeHoleItems3, takePasteHole1, takePasteHole2, takePasteHole3);

		repairHole1 = new ObjectStep(this, NullObjectID.NULL_11221, new WorldPoint(1817, 4834, 0), "Repair the holes.", hammer, planks2, tacks10);
		repairHole2 = new ObjectStep(this, NullObjectID.NULL_11222, new WorldPoint(1817, 4832, 0), "Repair the holes.", hammer, planks2, tacks10);
		repairHole3 = new ObjectStep(this, NullObjectID.NULL_11223, new WorldPoint(1817, 4830, 0), "Repair the holes.", hammer, planks2, tacks10);
		repairHole1.addSubSteps(repairHole2, repairHole3);

		pasteHole1 = new ObjectStep(this, NullObjectID.NULL_11221, new WorldPoint(1817, 4834, 0), "Waterproof the hole.", paste1);
		pasteHole2 = new ObjectStep(this, NullObjectID.NULL_11222, new WorldPoint(1817, 4832, 0), "Waterproof the hole.", paste1);
		pasteHole3 = new ObjectStep(this, NullObjectID.NULL_11223, new WorldPoint(1817, 4830, 0), "Waterproof the hole.", paste1);
		pasteHole1.addSubSteps(pasteHole2, pasteHole3);

		goUpAfterRepair =  new ObjectStep(this, ObjectID.SHIPS_LADDER_11308, new WorldPoint(1815, 4836, 0), "Go up to the deck.");
		talkToBillAfterRepair = new NpcStep(this, NpcID.BILL_TEACH_4014, new WorldPoint(1815, 4834, 1), "Talk to Bill Teach.");

		take2Ropes = new ObjectStep(this, ObjectID.REPAIR_LOCKER, new WorldPoint(1814, 4832, 0), "Search the repair locker for 2 ropes.", ropes2);
		take2Ropes.addAlternateObjects(ObjectID.REPAIR_LOCKER_11247);

		goUpToSailToLoot = new ObjectStep(this, ObjectID.CLIMBING_NET_11310, new WorldPoint(1816, 4831, 1), "Climb the climbing net.");
		useRopeOnSailToLoot = new ObjectStep(this, ObjectID.HOISTED_SAIL_11297, new WorldPoint(1817, 4830, 2), "Use a rope on the hoisted sail.", ropeHighlight);
		useRopeOnSailToLoot.addIcon(ItemID.ROPE);

		lootEnemyShip = new ObjectStep(this, NullObjectID.NULL_11230, "Plunder the chest, loot the crate and ransack the barrel for 10 plunder.", loot10);
		lootEnemyShip.addAlternateObjects(NullObjectID.NULL_11227, NullObjectID.NULL_11233);

		hopWorld = new DetailedQuestStep(this, "Hop worlds so that the chest resets.");

		enterEnemyHullForLoot = new ObjectStep(this, ObjectID.SHIPS_LADDER_11309, new WorldPoint(1824, 4829, 1), "Enter the ship's hull to loot it.");

		leaveEnemyHullWithLoot = new ObjectStep(this, ObjectID.SHIPS_LADDER_11308, new WorldPoint(1824, 4829, 0), "Return the plunder to the chest in Bill's hull.", loot10);
		climbNetWithLoot = new ObjectStep(this, ObjectID.CLIMBING_NET_11310, new WorldPoint(1823, 4834, 1), "Return the plunder to the chest in Bill's hull.");
		useRopeOnSailWithLoot = new ObjectStep(this, ObjectID.HOISTED_SAIL_11297, new WorldPoint(1822, 4835, 2), "Return the plunder to the chest in Bill's hull.", ropeHighlight);
		useRopeOnSailWithLoot.addIcon(ItemID.ROPE);
		enterHullWithLoot = new ObjectStep(this, ObjectID.SHIPS_LADDER_11309, new WorldPoint(1815, 4836, 1), "Return the plunder to the chest in Bill's hull.");
		useLootOnChest = new ObjectStep(this, ObjectID.PLUNDER_STORAGE, new WorldPoint(1815, 4824, 0), "Add the plunder to the plunder storage.", plunderHighlight);
		useLootOnChest.addIcon(ItemID.PLUNDER);

		goUpAfterLoot = new ObjectStep(this, ObjectID.SHIPS_LADDER_11308, new WorldPoint(1815, 4836, 0), "Go up to the deck.");

		talkToBillAfterLoot = new NpcStep(this, NpcID.BILL_TEACH_4014, new WorldPoint(1815, 4834, 1), "Talk to Bill Teach.");

		goDownForBarrel = new ObjectStep(this, ObjectID.SHIPS_LADDER_11309, new WorldPoint(1815, 4836, 1), "Search the gun locker for a cannon barrel.");
		takeBarrel = new ObjectStep(this, ObjectID.GUN_LOCKER, new WorldPoint(1816, 4833, 0), "Search the gun locker for a cannon barrel.", barrel);
		takeBarrel.addAlternateObjects(ObjectID.GUN_LOCKER_11249);
		goUpWithBarrel = new ObjectStep(this, ObjectID.SHIPS_LADDER_11308, new WorldPoint(1815, 4836, 0), "Go up to the deck and repair the cannon.");
		useBarrel = new ObjectStep(this, NullObjectID.NULL_11213, new WorldPoint(1817, 4833, 1), "Repair the cannon.", barrel);
		talkToBillAfterBarrel = new NpcStep(this, NpcID.BILL_TEACH_4014, new WorldPoint(1815, 4834, 1), "Talk to Bill Teach.");

		goDownForRamrod = new ObjectStep(this, ObjectID.SHIPS_LADDER_11309, new WorldPoint(1815, 4836, 1), "Search the gun locker for a ram rod, and a few fuses and canisters.");
		getRamrod = new ObjectStep(this, ObjectID.GUN_LOCKER, new WorldPoint(1816, 4833, 0), "Search the gun locker for a ram rod, 3 fuses, and a few fuses and canisters.", ramrod, fuse1, canister);
		getRamrod.addAlternateObjects(ObjectID.GUN_LOCKER_11249);
		goUpToCannon = new ObjectStep(this, ObjectID.SHIPS_LADDER_11308, new WorldPoint(1815, 4836, 0), "Go up to the deck to fire the cannon.");
		usePowder = new ObjectStep(this, NullObjectID.NULL_11213, new WorldPoint(1817, 4833, 1), "Use the gunpowder on the cannon.", powderHighlight);
		usePowder.addIcon(ItemID.GUNPOWDER);
		useRamrod = new ObjectStep(this, NullObjectID.NULL_11213, new WorldPoint(1817, 4833, 1), "Use the ramrod on the cannon.", ramrodHighlight);
		useRamrod.addIcon(ItemID.RAMROD);
		useCanister = new ObjectStep(this, NullObjectID.NULL_11213, new WorldPoint(1817, 4833, 1), "Use the canister on the cannon.", canisterHighlight);
		useCanister.addIcon(ItemID.CANISTER_7149);
		useFuse = new ObjectStep(this, NullObjectID.NULL_11213, new WorldPoint(1817, 4833, 1), "Use the fuse on the cannon.", fuseHighlight);
		useFuse.addIcon(ItemID.FUSE);
		fireCannon = new ObjectStep(this, NullObjectID.NULL_11213, new WorldPoint(1817, 4833, 1), "Fire the cannon!", tinderbox);
		useRamrodToClean = new ObjectStep(this, NullObjectID.NULL_11213, new WorldPoint(1817, 4833, 1), "Use the ramrod on the cannon to clean it.", ramrodHighlight);
		useRamrodToClean.addIcon(ItemID.RAMROD);

		getPowder = new ObjectStep(this, ObjectID.POWDER_BARREL_11245, new WorldPoint(1817, 4832, 1), "Get some gunpowder.");

		resetCannon = new ObjectStep(this, NullObjectID.NULL_11213, new WorldPoint(1817, 4833, 1), "Right-click empty out the cannon.");

		talkToBillAfterCanisterCannon = new NpcStep(this, NpcID.BILL_TEACH_4014, new WorldPoint(1815, 4834, 1), "Talk to Bill Teach.");

		goDownForBalls = new ObjectStep(this, ObjectID.SHIPS_LADDER_11309, new WorldPoint(1815, 4836, 1), "Search the gun locker for a ram rod, and a few fuses and cannon balls.");
		getBalls = new ObjectStep(this, ObjectID.GUN_LOCKER, new WorldPoint(1816, 4833, 0), "Search the gun locker for a ram rod, 3 fuses, and a few fuses and cannon balls.", ramrod, fuses, cannonball);
		getBalls.addAlternateObjects(ObjectID.GUN_LOCKER_11249);
		goUpToCannonWithBalls = new ObjectStep(this, ObjectID.SHIPS_LADDER_11308, new WorldPoint(1815, 4836, 0), "Go up to the deck to fire the cannon.");
		usePowderForBalls = new ObjectStep(this, NullObjectID.NULL_11213, new WorldPoint(1817, 4833, 1), "Use the gunpowder on the cannon.", powderHighlight);
		usePowderForBalls.addIcon(ItemID.GUNPOWDER);
		useRamrodForBalls = new ObjectStep(this, NullObjectID.NULL_11213, new WorldPoint(1817, 4833, 1), "Use the ramrod on the cannon.", ramrodHighlight);
		useRamrodForBalls.addIcon(ItemID.RAMROD);
		useBall = new ObjectStep(this, NullObjectID.NULL_11213, new WorldPoint(1817, 4833, 1), "Use the cannon ball on the cannon.", cannonballHighlight);
		useBall.addIcon(ItemID.CANNON_BALL_7119);
		useFuseForBalls = new ObjectStep(this, NullObjectID.NULL_11213, new WorldPoint(1817, 4833, 1), "Use the fuse on the cannon.", fuseHighlight);
		useFuseForBalls.addIcon(ItemID.FUSE);
		fireCannonForBalls = new ObjectStep(this, NullObjectID.NULL_11213, new WorldPoint(1817, 4833, 1), "Fire the cannon!", tinderbox);
		useRamrodToCleanForBalls = new ObjectStep(this, NullObjectID.NULL_11213, new WorldPoint(1817, 4833, 1), "Use the ramrod on the cannon to clean it.", ramrodHighlight);
		useRamrodToCleanForBalls.addIcon(ItemID.RAMROD);

		getPowderForBalls = new ObjectStep(this, ObjectID.POWDER_BARREL_11245, new WorldPoint(1817, 4832, 1), "Get some gunpowder.");

		repeatCanisterSteps = new DetailedQuestStep(this, "Repeat this 3-4 times until indicated to stop.");
		repeatBallSteps = new DetailedQuestStep(this, "Keep firing cannonballs until Bill tells you to stop. Quest completed!");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Collections.singletonList(food));
	}


	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Collections.singletonList("Able to survive multiple pirates (level 57) attacking you"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Arrays.asList(talkToBill, talkToBillOnBoat))));
		allSteps.add(new PanelDetails("Sabotage", new ArrayList<>(Arrays.asList(take4Ropes, take1Fuse, takeTinderbox, leaveHullForSabo, climbUpNetForSabo,
			useRopeOnSailForSabo, useFuseOnEnemyBarrel, lightEnemyFuse, climbEnemyNetAfterSabo, useRopeOnEnemySailAfterSabo, talkToBillAfterSabo))));
		allSteps.add(new PanelDetails("Repair", new ArrayList<>(Arrays.asList(goDownToFixLeak, takeHoleItems1, repairHole1, pasteHole1, goUpAfterRepair, talkToBillAfterRepair))));
		allSteps.add(new PanelDetails("Plunder the pirates", new ArrayList<>(Arrays.asList(goUpToSailToLoot, useRopeOnSailToLoot, enterEnemyHullForLoot, lootEnemyShip, leaveEnemyHullWithLoot,
			climbNetWithLoot, useRopeOnSailWithLoot, enterHullWithLoot, useLootOnChest, goUpAfterLoot, talkToBillAfterLoot))));
		allSteps.add(new PanelDetails("Repair the cannon", new ArrayList<>(Arrays.asList(goDownForBarrel, takeBarrel, goUpWithBarrel, useBarrel, talkToBillAfterBarrel))));
		allSteps.add(new PanelDetails("Fire canisters", new ArrayList<>(Arrays.asList(goDownForRamrod, getRamrod,goUpToCannon, getPowder, usePowder, useRamrod, useCanister, useFuse, fireCannon, repeatCanisterSteps, talkToBillAfterCanisterCannon))));
		allSteps.add(new PanelDetails("Fire cannon balls", new ArrayList<>(Arrays.asList(goDownForBalls, getBalls, goUpToCannonWithBalls, getPowderForBalls, usePowderForBalls, useRamrodForBalls, useBall, useFuseForBalls, fireCannonForBalls, repeatBallSteps))));
		return allSteps;
	}
}

