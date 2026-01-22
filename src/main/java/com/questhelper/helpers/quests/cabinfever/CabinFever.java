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
package com.questhelper.helpers.quests.cabinfever;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import java.util.*;

public class CabinFever extends BasicQuestHelper
{
	//Items Required
	ItemRequirement food, fuse1, tinderbox, ropes4, ropeHighlight, fuseHighlight, floorTinderbox, rope3, floorRope, tinderboxHighlight, planks2, planks4,
		planks6, hammer, tacks10, tacks20, tacks30, paste1, paste2, paste3, ropes2, loot10, plunderHighlight, barrel, gunpowder, ramrod, canister,
		powderHighlight, ramrodHighlight, canisterHighlight, fuses, cannonball, cannonballHighlight;

	Requirement onBoatF0, onBoatF1, onBoatF2, onEnemyBoatF0, onEnemyBoatF1, onEnemyBoatF2, onEnemyBoat, onBoatAtDock,
		hasSetSail, onSail, onEnemySail, addedFuse, explodedBarrel, planked1, planked2, planked3, pasted1, pasted2, pasted3,
		hasPlanked1, hasPlanked2, hasPlanked3, hasRepaired1, hasRepaired2, hasRepaired3, lootedAll, cannonBroken, addedPowder,
		usedRamrod, usedCanister, usedFuse, firedCannon, canisterInWrong, usedBalls, hasFuseOrAdded;

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

	//Zones
	Zone boatAtDock, boatF0, boatF1, boatF2, enemyBoatF0, enemyBoatF1, enemyBoatF2, sail, enemySail;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
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
		sabotageSteps.addStep(new Conditions(onEnemyBoatF1, addedFuse, tinderbox), lightEnemyFuse);
		sabotageSteps.addStep(new Conditions(onEnemySail, addedFuse, tinderbox), leaveEnemySail);
		sabotageSteps.addStep(new Conditions(onEnemySail, addedFuse, ropeHighlight), swingToBoat);
		sabotageSteps.addStep(new Conditions(onEnemyBoatF1, addedFuse, ropeHighlight), goUpToEnemySail);
		sabotageSteps.addStep(new Conditions(onEnemyBoatF1, addedFuse), pickUpRope);

		sabotageSteps.addStep(new Conditions(onEnemyBoatF1, hasFuseOrAdded), useFuseOnEnemyBarrel);
		sabotageSteps.addStep(new Conditions(onEnemyBoatF0, hasFuseOrAdded), leaveEnemyHull);
		sabotageSteps.addStep(new Conditions(onEnemySail, hasFuseOrAdded), leaveEnemySail);

		sabotageSteps.addStep(new Conditions(onSail, ropes4, hasFuseOrAdded, tinderbox), useRopeOnSailForSabo);
		sabotageSteps.addStep(new Conditions(onBoatF1, ropes4, hasFuseOrAdded, tinderbox), climbUpNetForSabo);
		sabotageSteps.addStep(new Conditions(onBoatF0, ropes4, hasFuseOrAdded, tinderbox), leaveHullForSabo);
		sabotageSteps.addStep(new Conditions(onBoatF0, ropes4, hasFuseOrAdded), takeTinderbox);
		sabotageSteps.addStep(new Conditions(onBoatF0, ropes4), take1Fuse);

		sabotageSteps.addStep(new Conditions(onEnemySail, ropeHighlight), swingToBoat);
		sabotageSteps.addStep(new Conditions(onEnemyBoatF1, ropeHighlight), goUpToEnemySail);
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
		talkToBillAfterSaboSteps.addStep(new Conditions(onEnemySail, ropeHighlight), useRopeOnEnemySailAfterSabo);
		talkToBillAfterSaboSteps.addStep(new Conditions(onEnemyBoatF1, ropeHighlight), climbEnemyNetAfterSabo);
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
		repairWalls.addStep(new Conditions(onBoatF0, pasted1, pasted2, planked3, hammer, paste1), pasteHole3);
		repairWalls.addStep(new Conditions(onBoatF0, pasted1, pasted2, hammer, paste1, planks2, tacks10),
			repairHole3);
		repairWalls.addStep(new Conditions(onBoatF0, pasted1, planked2, hammer, paste2, planks2, tacks10), pasteHole2);
		repairWalls.addStep(new Conditions(onBoatF0, pasted1, hammer, paste2, planks4, tacks20), repairHole2);
		repairWalls.addStep(new Conditions(onBoatF0, planked1, hammer, paste3, planks4, tacks20), pasteHole1);
		repairWalls.addStep(new Conditions(onBoatF0, hammer, paste3, planks6, tacks30), repairHole1);

		repairWalls.addStep(new Conditions(onBoatF0, hasRepaired3, hasPlanked3), takePasteHole3);
		repairWalls.addStep(new Conditions(onBoatF0, hasRepaired2), takeHoleItems3);
		repairWalls.addStep(new Conditions(onBoatF0, hasRepaired1, hasPlanked2), takePasteHole2);
		repairWalls.addStep(new Conditions(onBoatF0, hasRepaired1), takeHoleItems2);
		repairWalls.addStep(new Conditions(onBoatF0, planked1), takePasteHole1);
		repairWalls.addStep(onBoatF0, takeHoleItems1);

		repairWalls.addStep(new Conditions(onEnemySail, ropeHighlight), swingToBoat);
		repairWalls.addStep(new Conditions(onEnemyBoatF1, ropeHighlight), goUpToEnemySail);
		repairWalls.addStep(onEnemySail, leaveEnemySail);
		repairWalls.addStep(onEnemyBoatF2, leaveEnemyTop);
		repairWalls.addStep(onEnemyBoatF1, pickUpRope);
		repairWalls.addStep(onEnemyBoatF0, leaveEnemyHull);
		repairWalls.addStep(onSail, leaveSail);
		repairWalls.addStep(onBoatF2, leaveTop);
		repairWalls.addStep(onBoatF1, goDownToFixLeak);

		steps.put(50, repairWalls);

		ConditionalStep talkToBillAfterRepairing = new ConditionalStep(this, setSail);
		talkToBillAfterRepairing.addStep(new Conditions(onEnemySail, ropeHighlight), swingToBoat);
		talkToBillAfterRepairing.addStep(new Conditions(onEnemyBoatF1, ropeHighlight), goUpToEnemySail);
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
		lootingSteps.addStep(new Conditions(onBoatF0, loot10), useLootOnChest);
		lootingSteps.addStep(new Conditions(onBoatF1, loot10), enterHullWithLoot);
		lootingSteps.addStep(new Conditions(onEnemySail, loot10, ropeHighlight), useRopeOnSailWithLoot);
		lootingSteps.addStep(new Conditions(onEnemySail, loot10), leaveEnemySail);
		lootingSteps.addStep(new Conditions(onEnemyBoatF1, loot10, ropeHighlight), climbNetWithLoot);
		lootingSteps.addStep(new Conditions(onEnemyBoatF1, loot10), pickUpRope);
		lootingSteps.addStep(new Conditions(onEnemyBoatF0, loot10), leaveEnemyHullWithLoot);
		lootingSteps.addStep(new Conditions(onEnemyBoatF0, lootedAll), hopWorld);
		// TODO: Make this change if you've already handed in some plunder
		lootingSteps.addStep(onEnemyBoatF0, lootEnemyShip);
		lootingSteps.addStep(onEnemyBoatF1, enterEnemyHullForLoot);
		lootingSteps.addStep(onEnemyBoatF2, leaveEnemyTop);
		lootingSteps.addStep(onEnemySail, leaveEnemySail);
		lootingSteps.addStep(new Conditions(onSail, ropes2), useRopeOnSailToLoot);
		lootingSteps.addStep(new Conditions(onBoatF1, ropes2), goUpToSailToLoot);
		lootingSteps.addStep(new Conditions(onBoatF0, ropes2), leaveHull);
		lootingSteps.addStep(onSail, leaveSail);
		lootingSteps.addStep(onBoatF2, leaveTop);
		lootingSteps.addStep(onBoatF1, enterHull);
		lootingSteps.addStep(onBoatF0, take2Ropes);

		steps.put(70, lootingSteps);

		ConditionalStep talkToBillAfterLooting = new ConditionalStep(this, setSail);
		talkToBillAfterLooting.addStep(new Conditions(onEnemySail, ropeHighlight), swingToBoat);
		talkToBillAfterLooting.addStep(new Conditions(onEnemyBoatF1, ropeHighlight), goUpToEnemySail);
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
		repairCannonSteps.addStep(new Conditions(onBoatF1, barrel), useBarrel);
		repairCannonSteps.addStep(new Conditions(onBoatF0, barrel), goUpWithBarrel);
		repairCannonSteps.addStep(new Conditions(onEnemySail, ropeHighlight), swingToBoat);
		repairCannonSteps.addStep(new Conditions(onEnemyBoatF1, ropeHighlight), goUpToEnemySail);
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
		billAfterCannonRepair.addStep(new Conditions(onEnemySail, ropeHighlight), swingToBoat);
		billAfterCannonRepair.addStep(new Conditions(onEnemyBoatF1, ropeHighlight), goUpToEnemySail);
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
		fireCannons.addStep(new Conditions(onBoatF1, cannonBroken, barrel), useBarrel);
		fireCannons.addStep(new Conditions(onBoatF0, cannonBroken, barrel), goUpWithBarrel);
		fireCannons.addStep(new Conditions(onBoatF1, cannonBroken), goDownForBarrel);
		fireCannons.addStep(new Conditions(onBoatF0, cannonBroken), takeBarrel);

		fireCannons.addStep(new Conditions(onBoatF1, canisterInWrong), resetCannon);
		fireCannons.addStep(new Conditions(onBoatF1, firedCannon), useRamrodToClean);
		fireCannons.addStep(new Conditions(onBoatF1, usedFuse, usedCanister), fireCannon);
		fireCannons.addStep(new Conditions(onBoatF1, fuse1, usedCanister), useFuse);
		fireCannons.addStep(new Conditions(onBoatF1, fuse1, canister, usedRamrod), useCanister);
		fireCannons.addStep(new Conditions(onBoatF1, fuse1, canister, ramrod, addedPowder), useRamrod);
		fireCannons.addStep(new Conditions(onBoatF1, fuse1, canister, ramrod, gunpowder), usePowder);
		fireCannons.addStep(new Conditions(onBoatF1, fuse1, canister, ramrod), getPowder);
		fireCannons.addStep(new Conditions(onBoatF0, fuse1, canister, ramrod), goUpToCannon);

		fireCannons.addStep(new Conditions(onEnemySail, ropeHighlight), swingToBoat);
		fireCannons.addStep(new Conditions(onEnemyBoatF1, ropeHighlight), goUpToEnemySail);
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
		talkToBillAfterCanisters.addStep(new Conditions(onEnemySail, ropeHighlight), swingToBoat);
		talkToBillAfterCanisters.addStep(new Conditions(onEnemyBoatF1, ropeHighlight), goUpToEnemySail);
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
		fireCannonsWithBalls.addStep(new Conditions(onBoatF1, cannonBroken, barrel), useBarrel);
		fireCannonsWithBalls.addStep(new Conditions(onBoatF0, cannonBroken, barrel), goUpWithBarrel);
		fireCannonsWithBalls.addStep(new Conditions(onBoatF1, cannonBroken), goDownForBarrel);
		fireCannonsWithBalls.addStep(new Conditions(onBoatF0, cannonBroken), takeBarrel);

		fireCannonsWithBalls.addStep(new Conditions(onBoatF1, canisterInWrong), resetCannon);
		fireCannonsWithBalls.addStep(new Conditions(onBoatF1, firedCannon), useRamrodToCleanForBalls);
		fireCannonsWithBalls.addStep(new Conditions(onBoatF1, usedFuse, usedBalls), fireCannonForBalls);
		fireCannonsWithBalls.addStep(new Conditions(onBoatF1, fuse1, usedBalls), useFuseForBalls);
		fireCannonsWithBalls.addStep(new Conditions(onBoatF1, fuse1, cannonball, usedRamrod), useBall);
		fireCannonsWithBalls.addStep(new Conditions(onBoatF1, fuse1, cannonball, ramrod, addedPowder), useRamrodForBalls);
		fireCannonsWithBalls.addStep(new Conditions(onBoatF1, fuse1, cannonball, ramrod, gunpowder), usePowderForBalls);
		fireCannonsWithBalls.addStep(new Conditions(onBoatF1, fuse1, cannonball, ramrod), getPowderForBalls);
		fireCannonsWithBalls.addStep(new Conditions(onBoatF0, fuse1, cannonball, ramrod), goUpToCannonWithBalls);

		fireCannonsWithBalls.addStep(new Conditions(onEnemySail, ropeHighlight), swingToBoat);
		fireCannonsWithBalls.addStep(new Conditions(onEnemyBoatF1, ropeHighlight), goUpToEnemySail);
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

	@Override
	protected void setupRequirements()
	{
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		fuse1 = new ItemRequirement("Fuse", ItemID.FEVER_FUSE);
		ropes4 = new ItemRequirement("Rope", ItemID.ROPE, 4);
		ropes2 = new ItemRequirement("Rope", ItemID.ROPE, 2);
		ropeHighlight = new ItemRequirement("Rope", ItemID.ROPE);
		ropeHighlight.setHighlightInInventory(true);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).isNotConsumed();
		tinderbox.setTooltip("You can get another from the ship's hull");
		tinderboxHighlight = tinderbox.highlighted();
		floorTinderbox = new ItemRequirement("Tinderbox", ItemID.FEVER_TINDERBOX);
		rope3 = new ItemRequirement("Rope", ItemID.ROPE, 3);
		floorRope = new ItemRequirement("Rope", ItemID.FEVER_ROPE);

		fuseHighlight = new ItemRequirement("Fuse", ItemID.FEVER_FUSE);
		fuseHighlight.setHighlightInInventory(true);

		planks2 = new ItemRequirement("Repair plank", ItemID.FEVER_REPAIR_PLANK, 2);
		planks4 = new ItemRequirement("Repair plank", ItemID.FEVER_REPAIR_PLANK, 4);
		planks6 = new ItemRequirement("Repair plank", ItemID.FEVER_REPAIR_PLANK, 6);
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER);
		tacks10 = new ItemRequirement("Tacks", ItemID.FEVER_TACK, 10);
		tacks20 = new ItemRequirement("Tacks", ItemID.FEVER_TACK, 20);
		tacks30 = new ItemRequirement("Tacks", ItemID.FEVER_TACK, 30);
		paste1 = new ItemRequirement("Swamp paste", ItemID.SWAMPPASTE, 1);
		paste2 = new ItemRequirement("Swamp paste", ItemID.SWAMPPASTE, 2);
		paste3 = new ItemRequirement("Swamp paste", ItemID.SWAMPPASTE, 3);
		loot10 = new ItemRequirement("Plunder", ItemID.FEVER_PLUNDER, 10);
		plunderHighlight = new ItemRequirement("Plunder", ItemID.FEVER_PLUNDER);
		plunderHighlight.setHighlightInInventory(true);

		barrel = new ItemRequirement("Cannon barrel", ItemID.FEVER_CANNON);

		gunpowder = new ItemRequirement("Gunpowder", ItemID.FEVER_GUNPOWDER);
		canister = new ItemRequirement("A few canister", ItemID.FEVER_CANNISTER);
		fuses = new ItemRequirement("A few fuses", ItemID.FEVER_FUSE);
		ramrod = new ItemRequirement("Ramrod", ItemID.FEVER_CANNON_PROD);

		powderHighlight = new ItemRequirement("Gunpowder", ItemID.FEVER_GUNPOWDER);
		powderHighlight.setHighlightInInventory(true);
		canisterHighlight = new ItemRequirement("Canister", ItemID.FEVER_CANNISTER);
		canisterHighlight.setHighlightInInventory(true);
		ramrodHighlight = new ItemRequirement("Ramrod", ItemID.FEVER_CANNON_PROD);
		ramrodHighlight.setHighlightInInventory(true);
		fuseHighlight = new ItemRequirement("Fuse", ItemID.FEVER_FUSE);
		fuseHighlight.setHighlightInInventory(true);

		cannonball = new ItemRequirement("A few Cannon balls", ItemID.FEVER_CANNON_BALL);

		cannonballHighlight = new ItemRequirement("Cannon ball", ItemID.FEVER_CANNON_BALL);
		cannonballHighlight.setHighlightInInventory(true);
	}

	@Override
	protected void setupZones()
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
		onBoatAtDock = new ZoneRequirement(boatAtDock);
		onBoatF0 = new ZoneRequirement(boatF0);
		onBoatF1 = new ZoneRequirement(boatF1);
		onBoatF2 = new ZoneRequirement(boatF2);
		onSail = new ZoneRequirement(sail);

		onEnemyBoatF0 = new ZoneRequirement(enemyBoatF0);
		onEnemyBoatF1 = new ZoneRequirement(enemyBoatF1);
		onEnemyBoatF2 = new ZoneRequirement(enemyBoatF2);
		onEnemySail = new ZoneRequirement(enemySail);
		onEnemyBoat = new ZoneRequirement(enemyBoatF0, enemyBoatF1, enemyBoatF2, enemySail);

		hasSetSail = new VarbitRequirement(VarbitID.FEVER_CANNON, 1);

		addedFuse = new VarbitRequirement(VarbitID.FEVER_GUNPOWDER_BARREL, 2);
		hasFuseOrAdded = new Conditions(LogicType.OR, fuse1, addedFuse);

		explodedBarrel = new VarbitRequirement(VarbitID.FEVER_GUNPOWDER_BARREL, 1);
		// 1740 1 if swinging

		planked1 = new VarbitRequirement(VarbitID.FEVER_HOLE_1, 1);
		planked2 = new VarbitRequirement(VarbitID.FEVER_HOLE_2, 1);
		planked3 = new VarbitRequirement(VarbitID.FEVER_HOLE_3, 1);
		pasted1 = new VarbitRequirement(VarbitID.FEVER_HOLE_1, 2);
		pasted2 = new VarbitRequirement(VarbitID.FEVER_HOLE_2, 2);
		pasted3 = new VarbitRequirement(VarbitID.FEVER_HOLE_3, 2);

		hasPlanked1 = new VarbitRequirement(VarbitID.FEVER_HOLES_PATCHED, 1);
		hasPlanked2 = new VarbitRequirement(VarbitID.FEVER_HOLES_PATCHED, 2);
		hasPlanked3 = new VarbitRequirement(VarbitID.FEVER_HOLES_PATCHED, 3);

		hasRepaired1 = new VarbitRequirement(VarbitID.FEVER_HOLES_PROOFED, 1);
		hasRepaired2 = new VarbitRequirement(VarbitID.FEVER_HOLES_PROOFED, 2);
		hasRepaired3 = new VarbitRequirement(VarbitID.FEVER_HOLES_PROOFED, 3);

		lootedAll = new Conditions(new VarbitRequirement(VarbitID.FEVER_CRATE, 1), new VarbitRequirement(VarbitID.FEVER_CHEST, 1), new VarbitRequirement(VarbitID.FEVER_BARREL, 1));

		cannonBroken = new VarbitRequirement(VarbitID.FEVER_CANNON, 1);
		addedPowder = new VarbitRequirement(VarbitID.FEVER_CANNON_POWDER, 1);
		usedRamrod = new VarbitRequirement(VarbitID.FEVER_CANNON_TAMP, 1);
		usedCanister = new VarbitRequirement(VarbitID.FEVER_CANNON_AMMO, 2);
		usedBalls = new VarbitRequirement(VarbitID.FEVER_CANNON_AMMO, 1);
		usedFuse = new VarbitRequirement(VarbitID.FEVER_CANNON, 3);
		firedCannon = new VarbitRequirement(VarbitID.FEVER_CANNON_CLEAN, 1);


		// SHOTS CAN FAIL
		// Second cannonball
		// 1750 1->2

		// Third succesful shot:
		// 1750 2->3

		canisterInWrong = new VarbitRequirement(VarbitID.FEVER_CANNON_GONNA_BLOW, 1);

		// 1752 = num plunder stashed
	}

	public void setupSteps()
	{
		talkToBill = new NpcStep(this, NpcID.FEVER_TEACH, new WorldPoint(3678, 3494, 0), "Talk to Bill Teach in Port Phasmatys.");
		talkToBill.addDialogSteps("Yes.", "Yes, I've always wanted to be a pirate!", "Yes, I am a woman of my word.", "Yes, I am a man of my word.");
		goOnBillBoat = new ObjectStep(this, ObjectID.FEVER_GANGPLANK, new WorldPoint(3710, 3496, 0), "Talk to Bill Teach on his boat in Port Phasmatys.");
		talkToBillOnBoat = new NpcStep(this, NpcID.FEVER_TEACH, new WorldPoint(3714, 3496, 1), "Talk to Bill Teach on his boat in Port Phasmatys.");
		talkToBillOnBoat.addDialogStep("Let's go Cap'n!");
		talkToBillOnBoat.addSubSteps(goOnBillBoat);

		leaveHull = new ObjectStep(this, ObjectID.FEVER_SHIP_LADDER, new WorldPoint(1815, 4836, 0), "Leave the hull.");
		enterHull = new ObjectStep(this, ObjectID.FEVER_SHIP_LADDERTOP, new WorldPoint(1815, 4836, 1), "Enter the ship's hull.");
		leaveTop = new ObjectStep(this, ObjectID.FEVER_SHIPLADDER_TOP_ANGLED, new WorldPoint(1813, 4828, 2), "Go back down to the deck.");
		leaveSail = new ObjectStep(this, ObjectID.FEVER_CLIMB_DOWN_LOCATION, new WorldPoint(1816, 4831, 2), "Climb back down the net.");
		goUpToSail = new ObjectStep(this, ObjectID.FEVER_CLIMBING_NET, new WorldPoint(1816, 4831, 1), "Climb the climbing net.");
		leaveEnemyHull = new ObjectStep(this, ObjectID.FEVER_SHIP_LADDER, new WorldPoint(1824, 4829, 0), "Leave the hull.");
		enterEnemyHull = new ObjectStep(this, ObjectID.FEVER_SHIP_LADDERTOP, new WorldPoint(1824, 4829, 1), "Enter the ship's hull.");
		leaveEnemyTop = new ObjectStep(this, ObjectID.FEVER_SHIPLADDER_TOP_ANGLED, new WorldPoint(1822, 4837, 2), "Go back down to the deck.");
		leaveEnemySail = new ObjectStep(this, ObjectID.FEVER_CLIMB_DOWN_LOCATION, new WorldPoint(1823, 4834, 2), "Climb back down the net.");
		goUpToEnemySail = new ObjectStep(this, ObjectID.FEVER_CLIMBING_NET, new WorldPoint(1823, 4834, 1), "Climb the climbing net.");

		pickUpRope = new DetailedQuestStep(this, new WorldPoint(1822, 4827, 1), "Pick up the nearby rope.", floorRope);

		take1Fuse = new ObjectStep(this, ObjectID.FEVER_WEAPONS_LOCKER, new WorldPoint(1816, 4833, 0), "Search the gun locker for 1 fuse.", fuse1);
		take1Fuse.addAlternateObjects(ObjectID.FEVER_WEAPONS_LOCKER_OPEN);
		take4Ropes = new ObjectStep(this, ObjectID.FEVER_REPAIR_LOCKER, new WorldPoint(1814, 4832, 0), "Search the repair locker for 4 ropes.", ropes4);
		take4Ropes.addAlternateObjects(ObjectID.FEVER_REPAIR_LOCKER_OPEN);
		takeTinderbox = new DetailedQuestStep(this, new WorldPoint(1814, 4825, 0), "Pick up the tinderbox nearby.", floorTinderbox);

		leaveHullForSabo = new ObjectStep(this, ObjectID.FEVER_SHIP_LADDER, new WorldPoint(1815, 4836, 0), "Go up to the deck.");
		climbUpNetForSabo = new ObjectStep(this, ObjectID.FEVER_CLIMBING_NET, new WorldPoint(1816, 4831, 1), "Climb the climbing net.");
		useRopeOnSailForSabo = new ObjectStep(this, ObjectID.FEVER_SAIL1_HOISTEDL_CLIMB, new WorldPoint(1817, 4830, 2), "Use a rope on the hoisted sail.", ropeHighlight);
		useRopeOnSailForSabo.addIcon(ItemID.ROPE);

		useFuseOnEnemyBarrel = new ObjectStep(this, ObjectID.FEVER_MULTI_GUNPOWDER_BARREL, new WorldPoint(1822, 4831, 1), "Use the fuse on the pirate's powder barrel.", fuseHighlight);
		useFuseOnEnemyBarrel.addIcon(ItemID.FEVER_FUSE);

		lightEnemyFuse = new ObjectStep(this, ObjectID.FEVER_MULTI_FUSE_2, new WorldPoint(1824, 4831, 1), "Use the tinderbox on the fuse.", tinderboxHighlight);
		lightEnemyFuse.addIcon(ItemID.TINDERBOX);

		swingToBoat = new ObjectStep(this, ObjectID.FEVER_SAIL1_HOISTEDL_CLIMB, new WorldPoint(1822, 4835, 2), "Use a rope on the hoisted sail.", ropeHighlight);
		swingToBoat.addIcon(ItemID.ROPE);

		swingToEnemyBoat = new ObjectStep(this, ObjectID.FEVER_SAIL1_HOISTEDL_CLIMB, new WorldPoint(1817, 4830, 2), "Use a rope on the hoisted sail.", ropeHighlight);
		swingToEnemyBoat.addIcon(ItemID.ROPE);

		climbEnemyNetAfterSabo = new ObjectStep(this, ObjectID.FEVER_CLIMBING_NET, new WorldPoint(1823, 4834, 1), "Climb the climbing net.");
		useRopeOnEnemySailAfterSabo = new ObjectStep(this, ObjectID.FEVER_SAIL1_HOISTEDL_CLIMB, new WorldPoint(1822, 4835, 2), "Use a rope on the hoisted sail.", ropeHighlight);
		useRopeOnEnemySailAfterSabo.addIcon(ItemID.ROPE);
		talkToBillAfterSabo = new NpcStep(this, NpcID.FEVER_QUEST_SHIP_TEACH, new WorldPoint(1815, 4834, 1), "Talk to Bill Teach.");

		goDownToFixLeak = new ObjectStep(this, ObjectID.FEVER_SHIP_LADDERTOP, new WorldPoint(1815, 4836, 1), "Enter the ship's hull.");
		takeHoleItems1 = new ObjectStep(this, ObjectID.FEVER_REPAIR_LOCKER, new WorldPoint(1814, 4832, 0), "Search the repair locker for repair items.", hammer, planks6, tacks30, paste3);
		takeHoleItems1.addAlternateObjects(ObjectID.FEVER_REPAIR_LOCKER_OPEN);
		takeHoleItems2 = new ObjectStep(this, ObjectID.FEVER_REPAIR_LOCKER, new WorldPoint(1814, 4832, 0), "Search the repair locker for repair items.", hammer, planks4, tacks20, paste2);
		takeHoleItems2.addAlternateObjects(ObjectID.FEVER_REPAIR_LOCKER_OPEN);
		takeHoleItems3 = new ObjectStep(this, ObjectID.FEVER_REPAIR_LOCKER, new WorldPoint(1814, 4832, 0), "Search the repair locker for repair items.", hammer, planks2, tacks10, paste1);
		takeHoleItems3.addAlternateObjects(ObjectID.FEVER_REPAIR_LOCKER_OPEN);

		takePasteHole1 = new ObjectStep(this, ObjectID.FEVER_REPAIR_LOCKER, new WorldPoint(1814, 4832, 0), "Search the repair locker for repair items.", hammer, planks4, tacks20, paste3);
		takePasteHole1.addAlternateObjects(ObjectID.FEVER_REPAIR_LOCKER_OPEN);
		takePasteHole2 = new ObjectStep(this, ObjectID.FEVER_REPAIR_LOCKER, new WorldPoint(1814, 4832, 0), "Search the repair locker for repair items.", hammer, planks2, tacks10, paste2);
		takePasteHole2.addAlternateObjects(ObjectID.FEVER_REPAIR_LOCKER_OPEN);
		takePasteHole3 = new ObjectStep(this, ObjectID.FEVER_REPAIR_LOCKER, new WorldPoint(1814, 4832, 0), "Search the repair locker for repair items.", hammer, paste1);
		takePasteHole3.addAlternateObjects(ObjectID.FEVER_REPAIR_LOCKER_OPEN);

		takeHoleItems1.addSubSteps(takeHoleItems2, takeHoleItems3, takePasteHole1, takePasteHole2, takePasteHole3);

		repairHole1 = new ObjectStep(this, ObjectID.FEVER_MULTI_HOLE_1, new WorldPoint(1817, 4834, 0), "Repair the holes.", hammer, planks2, tacks10);
		repairHole2 = new ObjectStep(this, ObjectID.FEVER_MULTI_HOLE_2, new WorldPoint(1817, 4832, 0), "Repair the holes.", hammer, planks2, tacks10);
		repairHole3 = new ObjectStep(this, ObjectID.FEVER_MULTI_HOLE_3, new WorldPoint(1817, 4830, 0), "Repair the holes.", hammer, planks2, tacks10);
		repairHole1.addSubSteps(repairHole2, repairHole3);

		pasteHole1 = new ObjectStep(this, ObjectID.FEVER_MULTI_HOLE_1, new WorldPoint(1817, 4834, 0), "Waterproof the hole.", paste1);
		pasteHole2 = new ObjectStep(this, ObjectID.FEVER_MULTI_HOLE_2, new WorldPoint(1817, 4832, 0), "Waterproof the hole.", paste1);
		pasteHole3 = new ObjectStep(this, ObjectID.FEVER_MULTI_HOLE_3, new WorldPoint(1817, 4830, 0), "Waterproof the hole.", paste1);
		pasteHole1.addSubSteps(pasteHole2, pasteHole3);

		goUpAfterRepair = new ObjectStep(this, ObjectID.FEVER_SHIP_LADDER, new WorldPoint(1815, 4836, 0), "Go up to the deck.");
		talkToBillAfterRepair = new NpcStep(this, NpcID.FEVER_QUEST_SHIP_TEACH, new WorldPoint(1815, 4834, 1), "Talk to Bill Teach.");

		take2Ropes = new ObjectStep(this, ObjectID.FEVER_REPAIR_LOCKER, new WorldPoint(1814, 4832, 0), "Search the repair locker for 2 ropes.", ropes2);
		take2Ropes.addAlternateObjects(ObjectID.FEVER_REPAIR_LOCKER_OPEN);

		goUpToSailToLoot = new ObjectStep(this, ObjectID.FEVER_CLIMBING_NET, new WorldPoint(1816, 4831, 1), "Climb the climbing net.");
		useRopeOnSailToLoot = new ObjectStep(this, ObjectID.FEVER_SAIL1_HOISTEDL_CLIMB, new WorldPoint(1817, 4830, 2), "Use a rope on the hoisted sail.", ropeHighlight);
		useRopeOnSailToLoot.addIcon(ItemID.ROPE);

		lootEnemyShip = new ObjectStep(this, ObjectID.FEVER_MULTI_CHEST, "Plunder the chest, loot the crate and ransack the barrel for 10 plunder. Switch world after looting to make plunder respawn instantly.", loot10);
		lootEnemyShip.addAlternateObjects(ObjectID.FEVER_MULTI_CRATE, ObjectID.FEVER_MULTI_BARREL);

		hopWorld = new DetailedQuestStep(this, "Hop worlds so that the chest resets.");

		enterEnemyHullForLoot = new ObjectStep(this, ObjectID.FEVER_SHIP_LADDERTOP, new WorldPoint(1824, 4829, 1), "Enter the ship's hull to loot it.");

		leaveEnemyHullWithLoot = new ObjectStep(this, ObjectID.FEVER_SHIP_LADDER, new WorldPoint(1824, 4829, 0), "Return the plunder to the chest in Bill's hull.", loot10);
		climbNetWithLoot = new ObjectStep(this, ObjectID.FEVER_CLIMBING_NET, new WorldPoint(1823, 4834, 1), "Return the plunder to the chest in Bill's hull.");
		useRopeOnSailWithLoot = new ObjectStep(this, ObjectID.FEVER_SAIL1_HOISTEDL_CLIMB, new WorldPoint(1822, 4835, 2), "Return the plunder to the chest in Bill's hull.", ropeHighlight);
		useRopeOnSailWithLoot.addIcon(ItemID.ROPE);
		enterHullWithLoot = new ObjectStep(this, ObjectID.FEVER_SHIP_LADDERTOP, new WorldPoint(1815, 4836, 1), "Return the plunder to the chest in Bill's hull.");
		enterHullWithLoot.addSubSteps(leaveEnemyHullWithLoot, climbNetWithLoot, useRopeOnSailWithLoot);
		useLootOnChest = new ObjectStep(this, ObjectID.FEVER_PLUNDER_DEPOSIT, new WorldPoint(1815, 4824, 0), "Add the plunder to the plunder storage.", plunderHighlight);
		useLootOnChest.addIcon(ItemID.FEVER_PLUNDER);

		goUpAfterLoot = new ObjectStep(this, ObjectID.FEVER_SHIP_LADDER, new WorldPoint(1815, 4836, 0), "Go up to the deck.");

		talkToBillAfterLoot = new NpcStep(this, NpcID.FEVER_QUEST_SHIP_TEACH, new WorldPoint(1815, 4834, 1), "Talk to Bill Teach.");

		goDownForBarrel = new ObjectStep(this, ObjectID.FEVER_SHIP_LADDERTOP, new WorldPoint(1815, 4836, 1), "Search the gun locker for a cannon barrel.");
		takeBarrel = new ObjectStep(this, ObjectID.FEVER_WEAPONS_LOCKER, new WorldPoint(1816, 4833, 0), "Search the gun locker for a cannon barrel.", barrel);
		takeBarrel.addAlternateObjects(ObjectID.FEVER_WEAPONS_LOCKER_OPEN);
		takeBarrel.addSubSteps(goDownForBarrel);
		goUpWithBarrel = new ObjectStep(this, ObjectID.FEVER_SHIP_LADDER, new WorldPoint(1815, 4836, 0), "Go up to the deck and repair the cannon.");
		useBarrel = new ObjectStep(this, ObjectID.FEVER_MULTI_CANNON, new WorldPoint(1817, 4833, 1), "Repair the cannon.", barrel);
		talkToBillAfterBarrel = new NpcStep(this, NpcID.FEVER_QUEST_SHIP_TEACH, new WorldPoint(1815, 4834, 1), "Talk to Bill Teach.");

		goDownForRamrod = new ObjectStep(this, ObjectID.FEVER_SHIP_LADDERTOP, new WorldPoint(1815, 4836, 1), "Search the gun locker for a ram rod, and a few fuses and canisters.");
		getRamrod = new ObjectStep(this, ObjectID.FEVER_WEAPONS_LOCKER, new WorldPoint(1816, 4833, 0), "Search the gun locker for a ram rod, 3 fuses, and canisters.", ramrod, fuse1, canister);
		getRamrod.addAlternateObjects(ObjectID.FEVER_WEAPONS_LOCKER_OPEN);
		getRamrod.addSubSteps(goDownForRamrod);
		goUpToCannon = new ObjectStep(this, ObjectID.FEVER_SHIP_LADDER, new WorldPoint(1815, 4836, 0), "Go up to the deck to fire the cannon.");
		usePowder = new ObjectStep(this, ObjectID.FEVER_MULTI_CANNON, new WorldPoint(1817, 4833, 1), "Use the gunpowder on the cannon.", powderHighlight);
		usePowder.addIcon(ItemID.FEVER_GUNPOWDER);
		useRamrod = new ObjectStep(this, ObjectID.FEVER_MULTI_CANNON, new WorldPoint(1817, 4833, 1), "Use the ramrod on the cannon.", ramrodHighlight);
		useRamrod.addIcon(ItemID.FEVER_CANNON_PROD);
		useCanister = new ObjectStep(this, ObjectID.FEVER_MULTI_CANNON, new WorldPoint(1817, 4833, 1), "Use the canister on the cannon.", canisterHighlight);
		useCanister.addIcon(ItemID.FEVER_CANNISTER);
		useFuse = new ObjectStep(this, ObjectID.FEVER_MULTI_CANNON, new WorldPoint(1817, 4833, 1), "Use the fuse on the cannon.", fuseHighlight);
		useFuse.addIcon(ItemID.FEVER_FUSE);
		fireCannon = new ObjectStep(this, ObjectID.FEVER_MULTI_CANNON, new WorldPoint(1817, 4833, 1), "Fire the cannon!", tinderbox);
		useRamrodToClean = new ObjectStep(this, ObjectID.FEVER_MULTI_CANNON, new WorldPoint(1817, 4833, 1), "Use the ramrod on the cannon to clean it.", ramrodHighlight);
		useRamrodToClean.addIcon(ItemID.FEVER_CANNON_PROD);

		getPowder = new ObjectStep(this, ObjectID.FEVER_YOUR_GUNPOWDER_BARREL, new WorldPoint(1817, 4832, 1), "Get some gunpowder.");

		resetCannon = new ObjectStep(this, ObjectID.FEVER_MULTI_CANNON, new WorldPoint(1817, 4833, 1), "Right-click empty out the cannon.");

		talkToBillAfterCanisterCannon = new NpcStep(this, NpcID.FEVER_QUEST_SHIP_TEACH, new WorldPoint(1815, 4834, 1), "Talk to Bill Teach.");

		goDownForBalls = new ObjectStep(this, ObjectID.FEVER_SHIP_LADDERTOP, new WorldPoint(1815, 4836, 1), "Search the gun locker for a ram rod, and a few fuses and cannon balls.");
		getBalls = new ObjectStep(this, ObjectID.FEVER_WEAPONS_LOCKER, new WorldPoint(1816, 4833, 0), "Search the gun locker for a ram rod, 3 fuses, and cannon balls.", ramrod, fuses, cannonball);
		getBalls.addAlternateObjects(ObjectID.FEVER_WEAPONS_LOCKER_OPEN);
		goUpToCannonWithBalls = new ObjectStep(this, ObjectID.FEVER_SHIP_LADDER, new WorldPoint(1815, 4836, 0), "Go up to the deck to fire the cannon.");
		usePowderForBalls = new ObjectStep(this, ObjectID.FEVER_MULTI_CANNON, new WorldPoint(1817, 4833, 1), "Use the gunpowder on the cannon.", powderHighlight);
		usePowderForBalls.addIcon(ItemID.FEVER_GUNPOWDER);
		useRamrodForBalls = new ObjectStep(this, ObjectID.FEVER_MULTI_CANNON, new WorldPoint(1817, 4833, 1), "Use the ramrod on the cannon.", ramrodHighlight);
		useRamrodForBalls.addIcon(ItemID.FEVER_CANNON_PROD);
		useBall = new ObjectStep(this, ObjectID.FEVER_MULTI_CANNON, new WorldPoint(1817, 4833, 1), "Use the cannon ball on the cannon.", cannonballHighlight);
		useBall.addIcon(ItemID.FEVER_CANNON_BALL);
		useFuseForBalls = new ObjectStep(this, ObjectID.FEVER_MULTI_CANNON, new WorldPoint(1817, 4833, 1), "Use the fuse on the cannon.", fuseHighlight);
		useFuseForBalls.addIcon(ItemID.FEVER_FUSE);
		fireCannonForBalls = new ObjectStep(this, ObjectID.FEVER_MULTI_CANNON, new WorldPoint(1817, 4833, 1), "Fire the cannon!", tinderbox);
		useRamrodToCleanForBalls = new ObjectStep(this, ObjectID.FEVER_MULTI_CANNON, new WorldPoint(1817, 4833, 1), "Use the ramrod on the cannon to clean it.", ramrodHighlight);
		useRamrodToCleanForBalls.addIcon(ItemID.FEVER_CANNON_PROD);

		getPowderForBalls = new ObjectStep(this, ObjectID.FEVER_YOUR_GUNPOWDER_BARREL, new WorldPoint(1817, 4832, 1), "Get some gunpowder.");

		repeatCanisterSteps = new DetailedQuestStep(this, "Repeat this 3-4 times until indicated to stop.");
		repeatBallSteps = new DetailedQuestStep(this, "Keep firing cannonballs until Bill tells you to stop. Quest completed!");
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Collections.singletonList(food);
	}


	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Able to survive multiple pirates (level 57) attacking you");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.SMITHING, 7000),
				new ExperienceReward(Skill.CRAFTING, 7000),
				new ExperienceReward(Skill.AGILITY, 7000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("Coins", ItemID.COINS, 10000),
				new ItemReward("The Book o' Piracy", ItemID.FEVER_PIRACY_BOOK, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Access to Mos Le'Harmless"),
				new UnlockReward("Access to Cave Horrors and the ability to receive them as a Slayer Task."),
				new UnlockReward("Charter Ship prices are now halved."),
				new UnlockReward("Ability to play the Trouble Brewing minigame."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToBill, talkToBillOnBoat)));
		allSteps.add(new PanelDetails("Sabotage", Arrays.asList(take4Ropes, take1Fuse, takeTinderbox, leaveHullForSabo, climbUpNetForSabo,
			useRopeOnSailForSabo, useFuseOnEnemyBarrel, lightEnemyFuse, climbEnemyNetAfterSabo, useRopeOnEnemySailAfterSabo, talkToBillAfterSabo)));
		allSteps.add(new PanelDetails("Repair", Arrays.asList(goDownToFixLeak, takeHoleItems1, repairHole1, pasteHole1, goUpAfterRepair, talkToBillAfterRepair)));
		allSteps.add(new PanelDetails("Plunder the pirates", Arrays.asList(goUpToSailToLoot, useRopeOnSailToLoot, enterEnemyHullForLoot, lootEnemyShip,
			enterHullWithLoot, useLootOnChest, goUpAfterLoot, talkToBillAfterLoot)));
		allSteps.add(new PanelDetails("Repair the cannon", Arrays.asList(takeBarrel, goUpWithBarrel, useBarrel, talkToBillAfterBarrel)));
		allSteps.add(new PanelDetails("Fire canisters", Arrays.asList(getRamrod, goUpToCannon, getPowder, usePowder, useRamrod, useCanister, useFuse, fireCannon, repeatCanisterSteps, talkToBillAfterCanisterCannon)));
		allSteps.add(new PanelDetails("Fire cannon balls", Arrays.asList(goDownForBalls, getBalls, goUpToCannonWithBalls, getPowderForBalls, usePowderForBalls, useRamrodForBalls, useBall, useFuseForBalls, fireCannonForBalls, repeatBallSteps)));
		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new FreeInventorySlotRequirement(11));
		return req;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.PIRATES_TREASURE, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.RUM_DEAL, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.PRIEST_IN_PERIL, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.AGILITY, 42));
		req.add(new SkillRequirement(Skill.CRAFTING, 45));
		req.add(new SkillRequirement(Skill.SMITHING, 50));
		req.add(new SkillRequirement(Skill.RANGED, 40));
		return req;
	}
}
