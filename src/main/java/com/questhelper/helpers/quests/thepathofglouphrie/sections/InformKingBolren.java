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
import com.questhelper.helpers.quests.thepathofglouphrie.YewnocksPuzzle;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.TeleportItemRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.List;
import com.questhelper.util.QHObjectID;
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
			var exitStoreroom = new ObjectStep(quest, ObjectID.TUNNEL_49623, YewnocksPuzzle.regionPoint(37, 17), "Exit the storeroom.");
			exitStoreroom.addTeleport(quest.teleToBolren);
			var exitDungeon = new ObjectStep(quest, ObjectID.LADDER_5251, new WorldPoint(2597, 4435, 0), "Exit the dungeon.");
			var exitDungeonPreRovingElves = new ObjectStep(quest, ObjectID.LADDER_17387, new WorldPoint(2533, 9555, 0), "Exit the dungeon.");
			var squeezeThroughRailing = quest.enterTreeGnomeVillageMazeFromMiddle.copy();
			squeezeThroughRailing.addTeleport(quest.teleToBolren);
			killEvilCreature = new ConditionalStep(quest, kill, "Kill the Evil Creature next to King Bolren.");
			killEvilCreature.addStep(quest.inTreeGnomeVillageDungeon, exitDungeon);
			killEvilCreature.addStep(quest.inTreeGnomeVillageDungeonPreRovingElves, exitDungeonPreRovingElves);
			killEvilCreature.addStep(quest.inStoreroom, exitStoreroom);
			killEvilCreature.addStep(new Conditions(LogicType.NOR, quest.inTreeGnomeVillageMiddle), squeezeThroughRailing);
		}
		// Talk to King Bolren
		informKingBolren = new NpcStep(quest, NpcID.KING_BOLREN, new WorldPoint(2542, 3169, 0), "Talk to King Bolren about your next step.");
		informKingBolren.addTeleport(quest.teleToBolren);

		var teleToStronghold = new TeleportItemRequirement("Spirit tree to Gnome Stronghold [2]", -1, -1);

		// Talk to Gianne Junior in Tree Gnome Stronghold
		talkToGianneJnr = new NpcStep(quest, NpcID.GIANNE_JNR, new WorldPoint(2439, 3502, 1), "Talk to Gianne jnr. in Tree Gnome Stronghold to ask for Longramble's whereabouts.");
		// Floor 0 to Floor 1
		var climbUpToGianneJnr = new ObjectStep(quest, QHObjectID.GRAND_TREE_F0_LADDER, new WorldPoint(2466, 3495, 0), "");
		var climbGrandTreeF3ToF2 = new ObjectStep(quest, QHObjectID.GRAND_TREE_F3_LADDER, new WorldPoint(2466, 3495, 3), "");
		var climbGrandTreeF2ToF1 = new ObjectStep(quest, QHObjectID.GRAND_TREE_F2_LADDER, new WorldPoint(2466, 3495, 2), "");
		climbGrandTreeF2ToF1.addDialogStep("Climb Down.");
		climbUpToGianneJnr.setText(talkToGianneJnr.getText());
		climbUpToGianneJnr.addTeleport(teleToStronghold);
		talkToGianneJnr.addSubSteps(climbUpToGianneJnr, climbGrandTreeF3ToF2, climbGrandTreeF2ToF1);
		talkToGianneJnr.addDialogSteps("I need your help finding a certain gnome.");

		talkToGianneJnrStep = new ConditionalStep(quest, climbUpToGianneJnr);
		talkToGianneJnrStep.addStep(quest.inGnomeStrongholdFloor1, talkToGianneJnr);
		talkToGianneJnrStep.addStep(quest.inGnomeStrongholdFloor2, climbGrandTreeF2ToF1);
		talkToGianneJnrStep.addStep(quest.inGnomeStrongholdFloor3, climbGrandTreeF3ToF2);
	}

	public List<QuestStep> getSteps()
	{
		return List.of(
			killEvilCreature, informKingBolren, talkToGianneJnr
		);
	}
}
