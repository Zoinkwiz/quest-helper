/*
 * Copyright (c) 2023, jLereback <https://github.com/jLereback>
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
package com.questhelper.helpers.skills.mining;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.Skill;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Mining extends ComplexStateQuestHelper
{
	QuestStep copperSidebar, ironSidebar;
	//Items Required
	ItemRequirement ironPickaxe, steelPickaxe, blackPickaxe, mithrilPickaxe, adamantPickaxe, runePickaxe;

	// Levels for pickaxes
	SkillRequirement mi6, mi11, mi21, mi31, mi41;

	SkillRequirement mi15;

	ObjectStep mineCopper, mineIron;
	Requirement inCopperZone, inIronZone;
	Zone copperZone, ironZone;
	DetailedQuestStep copperStep, ironStep;

	WorldPoint COPPER_POINT = new WorldPoint(3287, 3366, 0);
	WorldPoint IRON_POINT = new WorldPoint(3295, 3310, 0);

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupConditions();
		setupZones();
		setupSteps();

		ConditionalStep fullTraining = new ConditionalStep(this, copperStep);
		fullTraining.addStep(new Conditions(inCopperZone), mineCopper);
		fullTraining.addStep(new Conditions(mi15, inIronZone), mineIron);
		fullTraining.addStep(mi15, ironStep);

		copperSidebar = new DetailedQuestStep(this,
			"Mine Copper- and Tin ore at South-east Varrock mine until 15 Mining. You can choose to drop" +
				" the ores as you go or bank them in the eastern Varrock bank.");
		copperSidebar.addSubSteps(copperStep, mineCopper);


		ironSidebar = new DetailedQuestStep(this,
			"Mine Iron ore at Al Kharid Mine until 99 Mining. You can choose to drop the ores as you go," +
				" smelt them on the way to the Al Kharid bank or bank the ores as they are.");
		ironSidebar.addSubSteps(ironStep, mineIron);

		return fullTraining;
	}

	@Override
	protected void setupRequirements()
	{
		mi6 = new SkillRequirement(Skill.MINING, 6);
		mi11 = new SkillRequirement(Skill.MINING, 11);
		mi21 = new SkillRequirement(Skill.MINING, 21);
		mi31 = new SkillRequirement(Skill.MINING, 31);
		mi41 = new SkillRequirement(Skill.MINING, 41);

		mi15 = new SkillRequirement(Skill.MINING, 15);

		ironPickaxe = new ItemRequirement("Iron pickaxe", ItemID.IRON_PICKAXE);
		steelPickaxe = new ItemRequirement("Steel pickaxe", ItemID.STEEL_PICKAXE);
		blackPickaxe = new ItemRequirement("Black pickaxe", ItemID.BLACK_PICKAXE);
		mithrilPickaxe = new ItemRequirement("Mithril pickaxe", ItemID.MITHRIL_PICKAXE);
		adamantPickaxe = new ItemRequirement("Adamant pickaxe", ItemID.ADAMANT_PICKAXE);
		runePickaxe = new ItemRequirement("Rune pickaxe", ItemID.RUNE_PICKAXE);

		ironPickaxe = ironPickaxe.showConditioned(new Conditions(LogicType.NOR, mi6)
		).isNotConsumed();
		steelPickaxe = steelPickaxe.showConditioned(
			new Conditions(mi6, new Conditions(LogicType.NOR, mi11))
		).isNotConsumed();
		blackPickaxe = blackPickaxe.showConditioned(
			new Conditions(mi11, new Conditions(LogicType.NOR, mi21))
		).isNotConsumed();
		mithrilPickaxe = mithrilPickaxe.showConditioned(
			new Conditions(mi21, new Conditions(LogicType.NOR, mi31))
		).isNotConsumed();
		adamantPickaxe = adamantPickaxe.showConditioned(
			new Conditions(mi31, new Conditions(LogicType.NOR, mi41))
		).isNotConsumed();
		runePickaxe = runePickaxe.showConditioned(new Conditions(mi41)
		).isNotConsumed();
	}

	public void setupConditions()
	{
		inCopperZone = new ZoneRequirement(copperZone);
		inIronZone = new ZoneRequirement(ironZone);
	}

	public void setupZones()
	{
		copperZone = new Zone(COPPER_POINT);
		ironZone = new Zone(IRON_POINT);
	}

	private void setupSteps()
	{
		copperStep = new DetailedQuestStep(this, COPPER_POINT,
			"Mine Copper- and Tin ore at South-east Varrock mine until 15 Mining. You can choose to drop " +
				"the ores as you go or bank them in the eastern Varrock bank.",
			ironPickaxe, steelPickaxe, blackPickaxe);
		copperStep.addTileMarker(COPPER_POINT, SpriteID.SKILL_MINING);

		ironStep = new DetailedQuestStep(this, IRON_POINT,
			"Mine Iron ore at Al Kharid Mine until 99 Mining. You can choose to drop the ores as you go," +
				" smelt them on the way to the Al Kharid bank or bank the ores as they are.",
			steelPickaxe, blackPickaxe, mithrilPickaxe, adamantPickaxe, runePickaxe);
		ironStep.addTileMarker(IRON_POINT, SpriteID.SKILL_MINING);


		mineCopper = new ObjectStep(this, ObjectID.TINROCK1, COPPER_POINT,
			"Mine Copper- and Tin ore at South-east Varrock mine until 15 Mining. You can choose to drop " +
				"the ores as you go or bank them in the eastern Varrock bank.", true,
			ironPickaxe, steelPickaxe, blackPickaxe
		);
		mineCopper.addAlternateObjects(ObjectID.TINROCK1, ObjectID.COPPERROCK2, ObjectID.COPPERROCK1);

		mineIron = new ObjectStep(this, ObjectID.IRONROCK1, IRON_POINT,
			"Mine Iron ore at Al Kharid Mine until 99 Mining. You can choose to drop the ores as you go," +
				" smelt them on the way to the Al Kharid bank or bank the ores as they are.", true,
			steelPickaxe, blackPickaxe, mithrilPickaxe, adamantPickaxe, runePickaxe
		);
		mineIron.addAlternateObjects(ObjectID.IRONROCK2);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(
			new UnlockReward("Ability to purchase Mining Cape for 99k")
		);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(ironPickaxe, steelPickaxe, blackPickaxe, mithrilPickaxe, adamantPickaxe, runePickaxe);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("1 - 15: Mine Copper/Tin", Collections.singletonList(copperSidebar),
			ironPickaxe, steelPickaxe, blackPickaxe));
		allSteps.add(new PanelDetails("15 - 99: Mine Iron ore", Collections.singletonList(ironSidebar),
			steelPickaxe, blackPickaxe, mithrilPickaxe, adamantPickaxe, runePickaxe));
		return allSteps;
	}
}
