package com.questhelper.helpers.quests.thepathofglouphrie.sections;

import com.questhelper.helpers.quests.thepathofglouphrie.ThePathOfGlouphrie;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import java.util.List;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;

public class StartingOff
{
	public NpcStep talkToKingBolren;
	public NpcStep talkToKingBolrenAgain;
	public ConditionalStep golrie;

	public void setup(ThePathOfGlouphrie quest)
	{
		/// Starting off
		// Talk to King Bolren
		talkToKingBolren = new NpcStep(quest, NpcID.KING_BOLREN, new WorldPoint(2542, 3169, 0), "Talk to King Bolren in the Tree Gnome Village to start the quest");
		talkToKingBolren.addDialogSteps("Yes.");
		talkToKingBolren.addTeleport(quest.teleToBolren);

		// Talk to King Bolren again
		talkToKingBolrenAgain = new NpcStep(quest, NpcID.KING_BOLREN, new WorldPoint(2542, 3169, 0), "Talk to King Bolren again");

		// TODO: Add step for freeing Golrie if the user hasn't started Roving Elves

		// Talk to Golrie
		{
			var enterTreeGnomeVillageMazeFromMiddle = quest.enterTreeGnomeVillageMazeFromMiddle.copy();
			var climbDownIntoTreeGnomeVillageDungeon = quest.climbDownIntoTreeGnomeVillageDungeon.copy();
			var talk = new NpcStep(quest, NpcID.GOLRIE, new WorldPoint(2580, 4450, 0), "");
			talk.addDialogSteps("I need your help with a device.");
			talk.addSubSteps(enterTreeGnomeVillageMazeFromMiddle, climbDownIntoTreeGnomeVillageDungeon);
			golrie = new ConditionalStep(quest, climbDownIntoTreeGnomeVillageDungeon, "Talk to Golrie in the Tree Gnome Village dungeon");
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
