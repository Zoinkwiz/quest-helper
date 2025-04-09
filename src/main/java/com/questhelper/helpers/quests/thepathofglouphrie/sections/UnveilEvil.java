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

import com.questhelper.helpers.quests.thepathofglouphrie.MonolithPuzzle;
import com.questhelper.helpers.quests.thepathofglouphrie.ThePathOfGlouphrie;
import com.questhelper.helpers.quests.thepathofglouphrie.YewnocksPuzzle;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.steps.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ObjectID;

import java.util.List;

public class UnveilEvil
{
	public ConditionalStep enterStoreroom;
	public ConditionalStep solveMonolithPuzzleStep;
	public ConditionalStep solveYewnocksMachinePuzzleStep;
	public ConditionalStep learnLoreStep;
	public DetailedQuestStep watchCutscene;
	private MonolithPuzzle solveMonolithPuzzle;
	private ConditionalStep learnLore;
	private PuzzleWrapperStep solveYewnocksMachinePuzzle;

	public void setup(ThePathOfGlouphrie quest)
	{
		{
			var squeezeThroughRailing = quest.enterTreeGnomeVillageMazeFromMiddle.copy();
			var climbIntoDungeon = quest.climbDownIntoTreeGnomeVillageDungeon.copy();
			var enter = new ObjectStep(quest, ObjectID.POG_CAVE_WALL_ENTRANCE_GOLRIE_ROVING, new WorldPoint(2608, 4451, 0), "");
			enter.addSubSteps(squeezeThroughRailing, climbIntoDungeon);
			var enterStoreroomPreRovingElves = new ObjectStep(quest, ObjectID.POG_CAVE_WALL_ENTRANCE_GOLRIE_ROVING, new WorldPoint(2544, 9571, 0),
				"");
			enterStoreroomPreRovingElves.addAlternateObjects(ObjectID.POG_CAVE_WALL_ENTRANCE_GOLRIE_WATERFALL);

			enterStoreroom = new ConditionalStep(quest, climbIntoDungeon, "Enter the storeroom to the east in the Tree Gnome Village dungeon.");
			enterStoreroom.addStep(quest.inTreeGnomeVillageDungeonPreRovingElves, enterStoreroomPreRovingElves);
			enterStoreroom.addStep(quest.inTreeGnomeVillageDungeon, enter);
			enterStoreroom.addStep(quest.inTreeGnomeVillageMiddle, squeezeThroughRailing);
		}

		/// Storeroom monolith puzzle
		solveMonolithPuzzle = new MonolithPuzzle(quest);

		solveMonolithPuzzleStep = new ConditionalStep(quest, enterStoreroom);
		solveMonolithPuzzleStep.addStep(quest.inStoreroom, solveMonolithPuzzle);

		var clickLectern = new ObjectStep(quest, ObjectID.POG_LECTURN, YewnocksPuzzle.regionPoint(24, 28), "Click the lectern.");
		var clickChapter1 = new WidgetStep(quest, "Click Chapter 1 to learn about the mysterious stranger.", 854, 5);
		var clickChapter2 = new WidgetStep(quest, "Click Chapter 2 to learn about the great king's death.", 854, 9);
		var clickChapter3 = new WidgetStep(quest, "Click Chapter 3 to learn about the old foe.", 854, 13);

		DetailedQuestStep watchLoreCutscene = new DetailedQuestStep(quest, "Watch the cutscene.");
		learnLore = new ConditionalStep(quest, clickLectern, "Learn about the lore. All items left on the ground are lost.");
		learnLore.addStep(new Conditions(quest.lecternWidgetActive, quest.learnedAboutChapter1, quest.learnedAboutChapter2), clickChapter3);
		learnLore.addStep(new Conditions(quest.lecternWidgetActive, quest.learnedAboutChapter1), clickChapter2);
		learnLore.addStep(quest.lecternWidgetActive, clickChapter1);
		learnLore.addSubSteps(watchLoreCutscene);

		learnLoreStep = new ConditionalStep(quest, enterStoreroom);
		learnLoreStep.addStep(quest.inCutscene, watchLoreCutscene);
		learnLoreStep.addStep(quest.inStoreroom, learnLore);

		{
			var squeezeThroughRailing = quest.enterTreeGnomeVillageMazeFromMiddle.copy();
			squeezeThroughRailing.setText("Squeeze through the loose railing.");
			var climbIntoDungeon = quest.climbDownIntoTreeGnomeVillageDungeon.copy();
			climbIntoDungeon.setText("Climb down the ladder to the Tree Gnome Village dungeon.");
			var enterStoreroom = new ObjectStep(quest, ObjectID.POG_CAVE_WALL_ENTRANCE_GOLRIE_ROVING, new WorldPoint(2608, 4451, 0),
				"Enter the storeroom to the east in the Tree Gnome Village dungeon.");
			enterStoreroom.addAlternateObjects(ObjectID.POG_CAVE_WALL_ENTRANCE_GOLRIE_WATERFALL);
			var enterStoreroomPreRovingElves = new ObjectStep(quest, ObjectID.POG_CAVE_WALL_ENTRANCE_GOLRIE_ROVING, new WorldPoint(2544, 9571, 0),
				"Enter the storeroom to the east in the Tree Gnome Village dungeon.");
			enterStoreroomPreRovingElves.addAlternateObjects(ObjectID.POG_CAVE_WALL_ENTRANCE_GOLRIE_WATERFALL);

			var enterStoreroomPuzzle = new ConditionalStep(quest, climbIntoDungeon, "Get to Yewnock's storeroom.");
			enterStoreroomPuzzle.addStep(quest.inTreeGnomeVillageDungeonPreRovingElves, enterStoreroomPreRovingElves);
			enterStoreroomPuzzle.addStep(quest.inTreeGnomeVillageDungeon, enterStoreroom);
			enterStoreroomPuzzle.addStep(quest.inTreeGnomeVillageMiddle, squeezeThroughRailing);
			solveYewnocksMachinePuzzle = new PuzzleWrapperStep(quest, new YewnocksPuzzle(quest), "Solve the disc puzzle.");
			solveYewnocksMachinePuzzle.addSubSteps(enterStoreroomPuzzle);
			solveYewnocksMachinePuzzle.addSubSteps(enterStoreroomPuzzle.getSteps());

			solveYewnocksMachinePuzzleStep = new ConditionalStep(quest, enterStoreroomPuzzle);
			solveYewnocksMachinePuzzleStep.addStep(quest.inStoreroom, solveYewnocksMachinePuzzle);
		}

		watchCutscene = new DetailedQuestStep(quest, "Watch the cutscene.");
	}

	public List<QuestStep> getSteps()
	{
		return List.of(
			enterStoreroom, solveMonolithPuzzle, learnLore, solveYewnocksMachinePuzzle, watchCutscene
		);
	}
}
