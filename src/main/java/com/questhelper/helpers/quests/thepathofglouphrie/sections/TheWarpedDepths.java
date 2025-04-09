/*
 * Copyright (c) 2023, pajlada <https://github.com/pajlada>
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
package com.questhelper.helpers.quests.thepathofglouphrie.sections;

import com.questhelper.helpers.quests.thepathofglouphrie.ThePathOfGlouphrie;
import com.questhelper.steps.*;
import net.runelite.api.NullObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.List;

public class TheWarpedDepths
{
	public ConditionalStep enterSewerStep;
	public ConditionalStep watchFinalCutsceneStep;
	public NpcStep talkToHazelmere;
	private ObjectStep enterSewer;
	private ObjectStep sewer1Ladder;
	private ObjectStep sewer5Ladder;
	private ObjectStep sewer2Ladder;
	private ObjectStep sewer3Ladder;
	private ObjectStep sewer4Ladder;
	private ObjectStep bossDoor;
	private NpcStep bossStep;
	private ObjectStep peekHeavyDoor;
	private DetailedQuestStep watchFinalCutscene;

	public void setup(ThePathOfGlouphrie quest)
	{
		enterSewer = new ObjectStep(quest, ObjectID.POG_CANYON_SEWER_ENTRANCE_LOWER_02, new WorldPoint(2322, 3101, 0),
			"Enter the sewer to the west of the Spirit tree.");
		enterSewer.addRequirement(quest.combatGear, quest.prayerPotions, quest.food, quest.crystalChime);
		enterSewer.addRecommended(quest.earmuffsOrSlayerHelmet);

		sewer1Ladder = new ObjectStep(quest, ObjectID.POG_SEWER_PIPE_SIDE_LADDER, "Climb up the ladder.");
		sewer1Ladder.addRecommended(quest.earmuffsOrSlayerHelmet);
		sewer2Ladder = new ObjectStep(quest, ObjectID.POG_SEWER_LADDER_TOP, new WorldPoint(1529, 4236, 1),
			"Climb down the ladder to the east.");
		sewer2Ladder.addRecommended(quest.protectMissiles);
		sewer2Ladder.addRecommended(quest.earmuffsOrSlayerHelmetEquipped);
		sewer3Ladder = new ObjectStep(quest, ObjectID.POG_SEWER_PIPE_SIDE_LADDER, new WorldPoint(1529, 4253, 0),
			"Climb up the ladder.");
		sewer3Ladder.addRecommended(quest.protectMissiles);
		sewer3Ladder.addRecommended(quest.earmuffsOrSlayerHelmetEquipped);
		sewer4Ladder = new ObjectStep(quest, ObjectID.POG_SEWER_LADDER_TOP, new WorldPoint(1486, 4282, 1),
			"Climb down the ladder to the north-west. Re-activate your run if you step in any puddles.");
		sewer4Ladder.addRecommended(quest.protectMissiles);
		sewer4Ladder.addRecommended(quest.earmuffsOrSlayerHelmetEquipped);
		sewer4Ladder.setLinePoints(List.of(
			new WorldPoint(1530, 4253, 1),
			new WorldPoint(1530, 4256, 1),
			new WorldPoint(1512, 4256, 1),
			new WorldPoint(1512, 4256, 1),
			new WorldPoint(1512, 4260, 1),
			new WorldPoint(1512, 4260, 1),
			new WorldPoint(1510, 4260, 1),
			new WorldPoint(1510, 4264, 1),
			new WorldPoint(1503, 4264, 1),
			new WorldPoint(1503, 4262, 1),
			new WorldPoint(1496, 4262, 1),
			new WorldPoint(1496, 4264, 1),
			new WorldPoint(1484, 4264, 1),
			new WorldPoint(1484, 4277, 1),
			new WorldPoint(1482, 4277, 1),
			new WorldPoint(1482, 4284, 1),
			new WorldPoint(1486, 4284, 1)
		));
		sewer5Ladder = new ObjectStep(quest, ObjectID.POG_SEWER_PIPE_SIDE_LADDER, new WorldPoint(1499, 4282, 0),
			"Climb up the ladder.");
		sewer5Ladder.addRecommended(quest.protectMissiles);
		sewer5Ladder.addRecommended(quest.earmuffsOrSlayerHelmetEquipped);
		bossDoor = new ObjectStep(quest, ObjectID.POG_SEWER_GRATE_DOOR_INSTANCE_CLOSED, new WorldPoint(1506, 4319, 1),
			"Go to the boss room to the north. Re-activate your run if you step in any puddles.");
		bossDoor.addRecommended(quest.protectMissiles);
		bossDoor.addRecommended(quest.earmuffsOrSlayerHelmetEquipped);
		bossDoor.setLinePoints(List.of(
			new WorldPoint(1499, 4283, 1),
			new WorldPoint(1506, 4283, 1),
			new WorldPoint(1506, 4291, 1),
			new WorldPoint(1503, 4291, 1),
			new WorldPoint(1503, 4292, 1),
			new WorldPoint(1497, 4292, 1),
			new WorldPoint(1497, 4291, 1),
			new WorldPoint(1487, 4291, 1),
			new WorldPoint(1487, 4302, 1),
			new WorldPoint(1492, 4302, 1),
			new WorldPoint(1492, 4304, 1),
			new WorldPoint(1495, 4304, 1),
			new WorldPoint(1495, 4316, 1),
			new WorldPoint(1498, 4316, 1),
			new WorldPoint(1498, 4319, 1),
			new WorldPoint(1506, 4319, 1)
		));

		// NOTE: If the user logs out, they will be in a non-instanced area of the boss are with the wrong terrorbirds
		bossStep = new NpcStep(quest, new int[]{NpcID.POG_MUTATED_TERRORBIRD_BOSS_1, NpcID.POG_MUTATED_TERRORBIRD_BOSS_2, NpcID.POG_MUTATED_TERRORBIRD_BOSS_3},
			"Kill the Terrorbirds. You can use the pillars around the room to only fight one at a time. They fight with both Melee and Ranged.");
		bossStep.setAllowMultipleHighlights(true);
		bossStep.addRecommended(quest.earmuffsOrSlayerHelmetEquipped);

		peekHeavyDoor = new ObjectStep(quest, NullObjectID.NULL_49909, WorldPoint.fromRegion(5955, 49, 31, 1),
			"Peek through the heavy door.");
		watchFinalCutscene = new DetailedQuestStep(quest, "Watch the final cutscene.");

		talkToHazelmere = new NpcStep(quest, NpcID.GRANDTREE_HAZELMERE, new WorldPoint(2678, 3086, 1),
			"Talk to Hazelmere. If you didn't have room for all 4 lamps, you can speak to Hazelmere after the quest to recover any lost lamps.");

		enterSewerStep = new ConditionalStep(quest, enterSewer);

		enterSewerStep.addStep(quest.inBossRoom, bossStep);
		enterSewerStep.addStep(quest.inSewer6, bossDoor);
		enterSewerStep.addStep(quest.inSewer5, sewer5Ladder);
		enterSewerStep.addStep(quest.inSewer4, sewer4Ladder);
		enterSewerStep.addStep(quest.inSewer3, sewer3Ladder);
		enterSewerStep.addStep(quest.inSewer2, sewer2Ladder);
		enterSewerStep.addStep(quest.inSewer1, sewer1Ladder);

		watchFinalCutsceneStep = new ConditionalStep(quest, peekHeavyDoor);
		watchFinalCutsceneStep.addStep(quest.inCutscene, watchFinalCutscene);
	}

	public List<QuestStep> getSteps()
	{
		return List.of(
			enterSewer, sewer1Ladder, sewer2Ladder, sewer3Ladder, sewer4Ladder, sewer5Ladder, bossDoor, bossStep, peekHeavyDoor, watchFinalCutscene, talkToHazelmere
		);
	}
}
