/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.quests.tearsofguthix;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.VarbitCondition;
import java.util.ArrayList;
import java.util.Arrays;
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
	quest = QuestHelperQuest.TEARS_OF_GUTHIX
)
public class TearsOfGuthix extends BasicQuestHelper
{
	ItemRequirement litSapphireLantern, chisel, tinderbox, pickaxe, rope, litSapphireLanternHighlighted, ropeHighlighted, tinderboxHighlighted, pickaxeHighlighted,
		chiselHighlighted, rockHighlighted, stoneBowl;

	ConditionForStep inSwamp, inJunaRoom, atRocks, addedRope, hasRock, hasStoneBowl;

	Zone swamp, junaRoom, rocks;

	QuestStep addRope, enterSwamp, enterJunaRoom, talkToJuna, useLanternOnLightCreature, mineRock, useChiselOnRock, talkToJunaToFinish;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();
		setupZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();

		ConditionalStep getToJunaRoom = new ConditionalStep(this, addRope);
		getToJunaRoom.addStep(inSwamp, enterJunaRoom);
		getToJunaRoom.addStep(addedRope, enterSwamp);

		ConditionalStep goTalkToJuna = new ConditionalStep(this, getToJunaRoom);
		goTalkToJuna.addStep(inJunaRoom, talkToJuna);
		steps.put(0, goTalkToJuna);

		ConditionalStep goGetRock = new ConditionalStep(this, getToJunaRoom);
		goGetRock.addStep(new Conditions(hasStoneBowl, inJunaRoom), talkToJunaToFinish);
		goGetRock.addStep(hasRock, useChiselOnRock);
		goGetRock.addStep(atRocks, mineRock);
		goGetRock.addStep(inJunaRoom, useLanternOnLightCreature);
		steps.put(1, goGetRock);

		return steps;
	}

	private void setupZones()
	{
		swamp = new Zone(new WorldPoint(3138, 9536, 0), new WorldPoint(3261, 9601, 0));
		junaRoom = new Zone(new WorldPoint(3205, 9484, 0), new WorldPoint(3263, 9537, 2));
		rocks = new Zone(new WorldPoint(3209, 9486, 2), new WorldPoint(3238, 9508, 2));
	}

	private void setupItemRequirements()
	{
		litSapphireLantern = new ItemRequirement("Sapphire lantern", ItemID.SAPPHIRE_LANTERN_4702);
		chisel = new ItemRequirement("Chisel", ItemID.CHISEL);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes());
		rope = new ItemRequirement("Rope", ItemID.ROPE);


		litSapphireLanternHighlighted = new ItemRequirement("Sapphire lantern", ItemID.SAPPHIRE_LANTERN_4702);
		litSapphireLanternHighlighted.setHighlightInInventory(true);
		chiselHighlighted = new ItemRequirement("Chisel", ItemID.CHISEL);
		chiselHighlighted.setHighlightInInventory(true);
		tinderboxHighlighted = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		tinderboxHighlighted.setHighlightInInventory(true);
		pickaxeHighlighted = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes());
		pickaxeHighlighted.setHighlightInInventory(true);
		ropeHighlighted = new ItemRequirement("Rope", ItemID.ROPE);
		ropeHighlighted.setHighlightInInventory(true);
		rockHighlighted = new ItemRequirement("Magic stone", ItemID.MAGIC_STONE);
		rockHighlighted.setHighlightInInventory(true);

		stoneBowl = new ItemRequirement("Stone bowl", ItemID.STONE_BOWL);
	}

	private void setupConditions()
	{
		inSwamp = new ZoneCondition(swamp);
		inJunaRoom = new ZoneCondition(junaRoom);
		atRocks = new ZoneCondition(rocks);

		addedRope = new VarbitCondition(279, 1);

		hasRock = new ItemRequirementCondition(rockHighlighted);
		hasStoneBowl = new ItemRequirementCondition(stoneBowl);
		
		// 452 = 1, gone through Juna's first dialog
	}

	private void setupSteps()
	{
		addRope = new ObjectStep(this, ObjectID.DARK_HOLE, new WorldPoint(3169, 3172, 0), "Enter the hole to Lumbridge swamp.", ropeHighlighted, litSapphireLantern, chisel, pickaxe, tinderbox);
		addRope.addIcon(ItemID.ROPE);

		enterSwamp = new ObjectStep(this, ObjectID.DARK_HOLE, new WorldPoint(3169, 3172, 0), "Enter the hole to Lumbridge swamp.", litSapphireLantern, chisel, pickaxe, tinderbox);
		enterSwamp.addSubSteps(addRope);

		enterJunaRoom = new ObjectStep(this, ObjectID.TUNNEL_6659, new WorldPoint(3226, 9540, 0), "Enter the cave in the south east corner of the swamp.");
		talkToJuna = new ObjectStep(this, NullObjectID.NULL_3193, new WorldPoint(3252, 9517, 2), "Talk to Juna.");
		talkToJuna.addDialogStep("Okay...");
		useLanternOnLightCreature = new NpcStep(this, NpcID.LIGHT_CREATURE_5783, new WorldPoint(3228, 9518, 2), "Go back up the rocks and use the lit sapphire lantern on one of the light creatures nearby.", litSapphireLanternHighlighted);
		mineRock = new ObjectStep(this, ObjectID.ROCKS_6670, new WorldPoint(3229, 9497, 2), "Mine one of the rocks.", pickaxe);
		useChiselOnRock = new DetailedQuestStep(this, "Use a chisel on the magic stone.", chiselHighlighted, rockHighlighted);
		talkToJunaToFinish = new ObjectStep(this, NullObjectID.NULL_3193, new WorldPoint(3252, 9517, 2), "Talk to Juna to complete the quest.");

	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(rope, litSapphireLantern, tinderbox, chisel, pickaxe));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Making a bowl", new ArrayList<>(Arrays.asList(enterSwamp, enterJunaRoom, talkToJuna, useLanternOnLightCreature, mineRock, useChiselOnRock, talkToJunaToFinish)),
			rope, litSapphireLantern, tinderbox, chisel, pickaxe));

		return allSteps;
	}
}
