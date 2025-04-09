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

import com.questhelper.collections.ItemCollections;
import com.questhelper.helpers.quests.thepathofglouphrie.ThePathOfGlouphrie;
import com.questhelper.requirements.item.TeleportItemRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.NullObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.List;

public class FindLongramble
{
	public ConditionalStep talkToLongramble;
	public ConditionalStep talkToSpiritTree;
	public ObjectStep useCrystalChime;
	public ConditionalStep talkToSpiritTreeAgain;

	public void setup(ThePathOfGlouphrie quest)
	{
		var teleToLongramble = new TeleportItemRequirement("Fairy Ring BKP or Ring of Dueling to Castle Wars",
			ItemCollections.RING_OF_DUELINGS, 1);
		teleToLongramble.addAlternates(ItemCollections.FAIRY_STAFF);

		var goToLongramble = new ObjectStep(quest, ObjectID.POG_GRAPPLE_TREE_BASE_OP, new WorldPoint(2333, 3081, 0), "");
		goToLongramble.addRecommended(quest.earmuffsOrSlayerHelmet);
		goToLongramble.addDialogStep("Castle Wars Arena.");
		goToLongramble.addTeleport(teleToLongramble);
		var actuallyTalkToLongramble = new NpcStep(quest, NpcID.POG_GNOME_LONGRAMBLE_VIS, new WorldPoint(2340, 3094, 0), "");
		actuallyTalkToLongramble.addRecommended(quest.earmuffsOrSlayerHelmet);


		talkToLongramble = new ConditionalStep(quest, goToLongramble,
			"Go to Longramble, make sure to head to a bank & gear up first. You can drop all leftover discs.",
			quest.combatGear, quest.crossbow.equipped().highlighted(), quest.mithGrapple.equipped().highlighted(),
			quest.prayerPotions, quest.food, quest.crystalChime);
		talkToLongramble.addStep(quest.nearLongramble, actuallyTalkToLongramble);

		{
			var talk = new ObjectStep(quest, NullObjectID.NULL_49598, new WorldPoint(2339, 3111, 0), "");
			talkToSpiritTree = new ConditionalStep(quest, talk, "Talk to the Spirit Tree.",
				quest.combatGear, quest.prayerPotions, quest.food, quest.crystalChime);
		}

		useCrystalChime = new ObjectStep(quest, NullObjectID.NULL_49598, new WorldPoint(2339, 3111, 0),
			"Use the Crystal Chime on the Spirit Tree.", quest.crystalChime.highlighted());
		useCrystalChime.addIcon(ItemID.CRYSTAL_CHIME);

		{
			var talk = new ObjectStep(quest, NullObjectID.NULL_49598, new WorldPoint(2339, 3111, 0), "");
			talkToSpiritTreeAgain = new ConditionalStep(quest, talk, "Talk to the Spirit Tree again.",
				quest.combatGear, quest.prayerPotions, quest.food, quest.crystalChime);
		}
	}

	public List<QuestStep> getSteps()
	{
		return List.of(
			talkToLongramble, talkToSpiritTree, useCrystalChime, talkToSpiritTreeAgain
		);
	}
}
