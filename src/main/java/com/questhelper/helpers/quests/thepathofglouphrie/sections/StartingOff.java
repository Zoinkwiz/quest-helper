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
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.List;

import static com.questhelper.requirements.util.LogicHelper.and;

public class StartingOff
{
	public NpcStep talkToKingBolren;
	public NpcStep talkToKingBolrenAgain;
	public ConditionalStep golrie;

	public void setup(ThePathOfGlouphrie quest)
	{
		/// Starting off
		// Talk to King Bolren
		talkToKingBolren = new NpcStep(quest, NpcID.KING_BOLREN, new WorldPoint(2542, 3169, 0), "Talk to King Bolren in the Tree Gnome Village to start the quest.");
		talkToKingBolren.addDialogSteps("Yes.");
		talkToKingBolren.addTeleport(quest.teleToBolren);

		// Talk to King Bolren again
		talkToKingBolrenAgain = new NpcStep(quest, NpcID.KING_BOLREN, new WorldPoint(2542, 3169, 0), "Talk to King Bolren again.");

		// TODO: Add step for freeing Golrie if the user hasn't started Roving Elves

		// Talk to Golrie
		{
			var enterTreeGnomeVillageMazeFromMiddle = quest.enterTreeGnomeVillageMazeFromMiddle.copy();
			var climbDownIntoTreeGnomeVillageDungeon = quest.climbDownIntoTreeGnomeVillageDungeon.copy();
			var talk = new NpcStep(quest, NpcID.ROVING_GOLRIE, new WorldPoint(2580, 4450, 0), "");
			talk.addAlternateNpcs(NpcID.GOLRIE_WATERFALL_QUEST);
			talk.addDialogSteps("I need your help with a device.");
			talk.addSubSteps(enterTreeGnomeVillageMazeFromMiddle, climbDownIntoTreeGnomeVillageDungeon);
			golrie = new ConditionalStep(quest, climbDownIntoTreeGnomeVillageDungeon, "Talk to Golrie in the Tree Gnome Village dungeon.");

			var talkPreRovingElves = new NpcStep(quest, NpcID.ROVING_GOLRIE, new WorldPoint(2514, 9580, 0), "You'll need to talk to Golrie again after giving him the key.");
			talkPreRovingElves.addAlternateNpcs(NpcID.GOLRIE_WATERFALL_QUEST);
			talkPreRovingElves.addDialogSteps("I need your help with a device.");

			var withGolrie = new ZoneRequirement(new Zone(new WorldPoint(2505, 9576, 0), new WorldPoint(2526, 9587, 0)));

			var getKey = new ObjectStep(quest, ObjectID.GOLRIE_CRATE_WATERFALL_QUEST, new WorldPoint(2548, 9565, 0), "Get the Tree Gnome Village dungeon key from the crate to the north-east.");
			var unlockDoor = new ObjectStep(quest, ObjectID.GOLRIE_GATE_WATERFALL_QUEST, new WorldPoint(2515, 9575, 0), "");

			golrie.addStep(and(withGolrie), talkPreRovingElves);
			golrie.addStep(and(quest.inTreeGnomeVillageDungeonPreRovingElves, quest.treeGnomeVillageDungeonKey), unlockDoor);
			golrie.addStep(quest.inTreeGnomeVillageDungeonPreRovingElves, getKey);
			golrie.addStep(quest.inTreeGnomeVillageDungeon, talk);
			golrie.addStep(quest.inTreeGnomeVillageMiddle, enterTreeGnomeVillageMazeFromMiddle);
		}
	}

	public List<QuestStep> getSteps()
	{
		return List.of(
			talkToKingBolren, talkToKingBolrenAgain, golrie
		);
	}
}
