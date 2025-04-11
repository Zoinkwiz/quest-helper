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
package com.questhelper.helpers.quests.tearsofguthix;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestPointRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.*;

public class TearsOfGuthix extends BasicQuestHelper
{
	//Items Required
	ItemRequirement litSapphireLantern, chisel, tinderbox, pickaxe, rope, litSapphireLanternHighlighted,
		ropeHighlighted, tinderboxHighlighted, pickaxeHighlighted, chiselHighlighted, rockHighlighted, stoneBowl;

	Requirement inSwamp, inJunaRoom, atRocks, addedRope;

	QuestStep addRope, enterSwamp, enterJunaRoom, talkToJuna, useLanternOnLightCreature, mineRock, useChiselOnRock,
		talkToJunaToFinish;

	//Zones
	Zone swamp, junaRoom, rocks;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();
		initializeRequirements();
		setupConditions();
		setupSteps();

		ConditionalStep getToJunaRoom = new ConditionalStep(this, addRope);
		getToJunaRoom.addStep(inSwamp, enterJunaRoom);
		getToJunaRoom.addStep(addedRope, enterSwamp);

		ConditionalStep goTalkToJuna = new ConditionalStep(this, getToJunaRoom);
		goTalkToJuna.addStep(inJunaRoom, talkToJuna);
		steps.put(0, goTalkToJuna);

		ConditionalStep goGetRock = new ConditionalStep(this, getToJunaRoom);
		goGetRock.addStep(new Conditions(stoneBowl.alsoCheckBank(questBank), inJunaRoom), talkToJunaToFinish);
		goGetRock.addStep(rockHighlighted, useChiselOnRock);
		goGetRock.addStep(atRocks, mineRock);
		goGetRock.addStep(inJunaRoom, useLanternOnLightCreature);
		steps.put(1, goGetRock);

		return steps;
	}

	@Override
	protected void setupZones()
	{
		swamp = new Zone(new WorldPoint(3138, 9536, 0), new WorldPoint(3261, 9601, 0));
		junaRoom = new Zone(new WorldPoint(3205, 9484, 0), new WorldPoint(3263, 9537, 2));
		rocks = new Zone(new WorldPoint(3209, 9486, 2), new WorldPoint(3238, 9508, 2));
	}

	@Override
	protected void setupRequirements()
	{
		litSapphireLantern = new ItemRequirement("Sapphire lantern", ItemID.TOG_SAPPHIRE_LANTERN_LIT).isNotConsumed();
		litSapphireLantern.setTooltip("You can make this by using a cut sapphire on a bullseye lantern");
		chisel = new ItemRequirement("Chisel", ItemID.CHISEL).isNotConsumed();
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).isNotConsumed();
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).isNotConsumed();
		rope = new ItemRequirement("Rope", ItemID.ROPE);


		litSapphireLanternHighlighted = new ItemRequirement("Sapphire lantern", ItemID.TOG_SAPPHIRE_LANTERN_LIT);
		litSapphireLanternHighlighted.setTooltip("You can make this by using a cut sapphire on a bullseye lantern");
		litSapphireLanternHighlighted.setHighlightInInventory(true);
		chiselHighlighted = new ItemRequirement("Chisel", ItemID.CHISEL);
		chiselHighlighted.setHighlightInInventory(true);
		tinderboxHighlighted = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		tinderboxHighlighted.setHighlightInInventory(true);
		pickaxeHighlighted = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES);
		pickaxeHighlighted.setHighlightInInventory(true);
		ropeHighlighted = new ItemRequirement("Rope", ItemID.ROPE);
		ropeHighlighted.setHighlightInInventory(true);
		rockHighlighted = new ItemRequirement("Magic stone", ItemID.TOG_STONE);
		rockHighlighted.setHighlightInInventory(true);

		stoneBowl = new ItemRequirement("Stone bowl", ItemID.TOG_BOWL);
	}

	private void setupConditions()
	{
		inSwamp = new ZoneRequirement(swamp);
		inJunaRoom = new ZoneRequirement(junaRoom);
		atRocks = new ZoneRequirement(rocks);

		addedRope = new VarbitRequirement(279, 1);

		// 452 = 1, gone through Juna's first dialog
	}

	private void setupSteps()
	{
		addRope = new ObjectStep(this, ObjectID.GOBLIN_CAVE_ENTRANCE, new WorldPoint(3169, 3172, 0),
			"Enter the hole to Lumbridge swamp.", ropeHighlighted, litSapphireLantern, chisel, pickaxe, tinderbox);
		addRope.addIcon(ItemID.ROPE);

		enterSwamp = new ObjectStep(this, ObjectID.GOBLIN_CAVE_ENTRANCE, new WorldPoint(3169, 3172, 0),
			"Enter the hole to Lumbridge swamp.", litSapphireLantern, chisel, pickaxe, tinderbox);
		enterSwamp.addSubSteps(addRope);

		enterJunaRoom = new ObjectStep(this, ObjectID.TOG_CAVE_DOWN, new WorldPoint(3226, 9540, 0),
			"Enter the cave in the south east corner of the swamp.");
		talkToJuna = new ObjectStep(this, ObjectID.TOG_JUNA, new WorldPoint(3252, 9517, 2),
			"Talk to Juna.");
		talkToJuna.addDialogStep("Okay...");
		// TODO: Verify which Light Creature NPC ID is correct
		useLanternOnLightCreature = new NpcStep(this, new int[]{NpcID.DTTD_CUTSCENE_LIGHT_CREATURE, NpcID.TOG_LIGHT_CREATURE_NOOP, NpcID.TOG_LIGHT_CREATURE_OP, NpcID.TOG_LIGHT_CREATURE_CARRYING, NpcID.TOG_LIGHT_CREATURE_TRANSITION}, new WorldPoint(3228, 9518, 2),
			"Go back up the rocks and use the lit sapphire lantern on one of the light creatures nearby.", litSapphireLanternHighlighted);
		mineRock = new ObjectStep(this, ObjectID.TOG_BLUE_STONE_ROCKS2, new WorldPoint(3229, 9497, 2),
			"Mine one of the rocks.", pickaxe);
		useChiselOnRock = new DetailedQuestStep(this, "Use a chisel on the magic stone.", chiselHighlighted, rockHighlighted);
		talkToJunaToFinish = new ObjectStep(this, ObjectID.TOG_JUNA, new WorldPoint(3252, 9517, 2),
			"Talk to Juna to complete the quest.");

	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(rope, litSapphireLantern, tinderbox, chisel, pickaxe);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestPointRequirement(43));
		req.add(new SkillRequirement(Skill.FIREMAKING, 49, true));
		req.add(new SkillRequirement(Skill.CRAFTING, 20));
		req.add(new SkillRequirement(Skill.MINING, 20));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.CRAFTING, 1000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to Tears of Guthix"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Making a bowl", Arrays.asList(enterSwamp, enterJunaRoom, talkToJuna, useLanternOnLightCreature, mineRock, useChiselOnRock, talkToJunaToFinish),
			rope, litSapphireLantern, tinderbox, chisel, pickaxe));

		return allSteps;
	}
}
