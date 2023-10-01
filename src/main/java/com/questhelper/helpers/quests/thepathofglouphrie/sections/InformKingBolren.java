package com.questhelper.helpers.quests.thepathofglouphrie.sections;

import com.questhelper.helpers.quests.thepathofglouphrie.ThePathOfGlouphrie;
import com.questhelper.helpers.quests.thepathofglouphrie.YewnocksPuzzle;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.TeleportItemRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.List;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

public class InformKingBolren
{
	public NpcStep informKingBolren;
	public ConditionalStep killEvilCreature;
	public ConditionalStep talkToGianneJnrStep;
	private NpcStep talkToGianneJnr;

	public void setup(ThePathOfGlouphrie quest)
	{
		/// Inform King Bolren
		{
			// Kill the Evil Creature
			var kill = new NpcStep(quest, NpcID.EVIL_CREATURE_12477, new WorldPoint(2542, 3169, 0), "");
			var exitStoreroom = new ObjectStep(quest, ObjectID.TUNNEL_49623, YewnocksPuzzle.regionPoint(37, 17), "Exit the storeroom");
			exitStoreroom.addTeleport(quest.teleToBolren);
			var exitDungeon = new ObjectStep(quest, ObjectID.LADDER_5251, new WorldPoint(2597, 4435, 0), "Exit the dungeon");
			var squeezeThroughRailing = quest.enterTreeGnomeVillageMazeFromMiddle.copy();
			squeezeThroughRailing.addTeleport(quest.teleToBolren);
			killEvilCreature = new ConditionalStep(quest, kill, "Kill the Evil Creature next to King Bolren");
			killEvilCreature.addStep(quest.inTreeGnomeVillageDungeon, exitDungeon);
			killEvilCreature.addStep(quest.inStoreroom, exitStoreroom);
			killEvilCreature.addStep(new Conditions(LogicType.NOR, quest.inTreeGnomeVillageMiddle), squeezeThroughRailing);
		}
		// Talk to King Bolren
		informKingBolren = new NpcStep(quest, NpcID.KING_BOLREN, new WorldPoint(2542, 3169, 0), "Talk to King Bolren about your next step");
		informKingBolren.addTeleport(quest.teleToBolren);

		var teleToStronghold = new TeleportItemRequirement("Spirit tree to Gnome Stronghold [2]", -1, -1);

		// Talk to Gianne Junior in Tree Gnome Stronghold
		talkToGianneJnr = new NpcStep(quest, NpcID.GIANNE_JNR, new WorldPoint(2439, 3502, 1), "Talk to Gianne jnr. in Tree Gnome Stronghold to ask for Longramble's whereabouts.");
		ObjectStep climbUpToGianneJnr = new ObjectStep(quest, ObjectID.LADDER_16683, new WorldPoint(2466, 3495, 0), "");
		climbUpToGianneJnr.setText(talkToGianneJnr.getText());
		climbUpToGianneJnr.addTeleport(teleToStronghold);
		talkToGianneJnr.addSubSteps(climbUpToGianneJnr);
		talkToGianneJnr.addDialogSteps("I need your help finding a certain gnome.");

		talkToGianneJnrStep = new ConditionalStep(quest, climbUpToGianneJnr);
		talkToGianneJnrStep.addStep(quest.inGnomeStrongholdFloor1, talkToGianneJnr);
	}

	public List<QuestStep> getSteps()
	{
		return List.of(
			killEvilCreature, informKingBolren, talkToGianneJnr
		);
	}
}
