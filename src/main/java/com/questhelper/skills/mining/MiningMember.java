/*
 * Copyright (c) 2022, jLereback <https://github.com/jLereback>
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
package com.questhelper.skills.mining;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.TileStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.MINING_MEMBER
)
public class MiningMember extends ComplexStateQuestHelper
{
	//Items Required
	ItemRequirement ironPickaxe, steelPickaxe, blackPickaxe, mithrilPickaxe, adamantPickaxe, runePickaxe, dragonPickaxe;

	// Levels for pickaxes
	SkillRequirement mi6, mi11, mi21, mi31, mi41, mi61;
	SkillRequirement at5, at10, at20, at30, at40, at60;
	SkillRequirement mi15;

	ItemRequirement dragonPickaxeRec, varrockArmour1, prospectorHelmet, prospectorJacket, prospectorLegs, prospectorBoots;


	ObjectStep mineCopperOrTin, mineIron;
	Requirement inCopperZone, inIronZone;
	Zone copperZone, ironZone;
	TileStep copperStep, ironStep;

	WorldPoint COPPER_POINT = new WorldPoint(3287, 3366, 0);
	WorldPoint IRON_POINT = new WorldPoint(3295, 3310, 0);

	@Override
	public QuestStep loadStep()
	{
		setupRequirements();
		setupConditions();
		setupZones();
		setupSteps();

		ConditionalStep fullTraining = new ConditionalStep(this, copperStep);
		fullTraining.addStep(new Conditions(inCopperZone), mineCopperOrTin);
		fullTraining.addStep(new Conditions(mi15, inIronZone), mineIron);
		fullTraining.addStep(mi15, ironStep);
		return fullTraining;
	}

	@Override
	public void setupRequirements()
	{
		mi6 = new SkillRequirement(Skill.MINING, 6);
		mi11 = new SkillRequirement(Skill.MINING, 11);
		mi21 = new SkillRequirement(Skill.MINING, 21);
		mi31 = new SkillRequirement(Skill.MINING, 31);
		mi41 = new SkillRequirement(Skill.MINING, 41);

		mi15 = new SkillRequirement(Skill.MINING, 15);

		ironPickaxe = new ItemRequirement("Iron axe", ItemID.IRON_PICKAXE);
		steelPickaxe = new ItemRequirement("Steel axe", ItemID.STEEL_PICKAXE);
		blackPickaxe = new ItemRequirement("Black axe", ItemID.BLACK_PICKAXE);
		mithrilPickaxe = new ItemRequirement("Mithril axe", ItemID.MITHRIL_PICKAXE);
		adamantPickaxe = new ItemRequirement("Adamant axe", ItemID.ADAMANT_PICKAXE);
		dragonPickaxe = new ItemRequirement("Dragon axe", ItemID.DRAGON_PICKAXE);
		dragonPickaxeRec = new ItemRequirement("Dragon axe", ItemID.DRAGON_PICKAXE);
		runePickaxe = new ItemRequirement("Rune axe", ItemID.RUNE_PICKAXE);


		ironPickaxe = ironPickaxe.showConditioned(new Conditions(LogicType.NOR, at5, mi6)
		).isNotConsumed();
		steelPickaxe = steelPickaxe.showConditioned(
			new Conditions(at5, mi6, new Conditions(LogicType.NOR, at10, mi11))
		).isNotConsumed();
		blackPickaxe = blackPickaxe.showConditioned(
			new Conditions(at10, mi11, new Conditions(LogicType.NOR, at20, mi21))
		).isNotConsumed();
		mithrilPickaxe = mithrilPickaxe.showConditioned(
			new Conditions(at20, mi21, new Conditions(LogicType.NOR, at30, mi31))
		).isNotConsumed();
		adamantPickaxe = adamantPickaxe.showConditioned(
			new Conditions(at30, mi31, new Conditions(LogicType.NOR, at40, mi41))
		).isNotConsumed();
		dragonPickaxeRec = dragonPickaxeRec.showConditioned(
			new Conditions(mi61, at60, new Conditions(LogicType.NOR, dragonPickaxe.alsoCheckBank(questBank)))
		).isNotConsumed();
		runePickaxe = runePickaxe.showConditioned(
			new Conditions(at40, mi41, new Conditions(LogicType.NOR, dragonPickaxe.alsoCheckBank(questBank)))
		).isNotConsumed();
		dragonPickaxe = dragonPickaxe.showConditioned(
			new Conditions(mi61, at60, dragonPickaxe.alsoCheckBank(questBank))
		).isNotConsumed();


		//Recommended items
		prospectorHelmet = new ItemRequirement("Prospector helmet", ItemID.PROSPECTOR_HELMET);
		prospectorJacket = new ItemRequirement("Prospector jacket", ItemID.PROSPECTOR_JACKET);
		prospectorLegs = new ItemRequirement("Prospector legs", ItemID.PROSPECTOR_LEGS);
		prospectorBoots = new ItemRequirement("Prospector boots", ItemID.PROSPECTOR_BOOTS);
		varrockArmour1 = new ItemRequirement("Varrock Armour", ItemID.VARROCK_ARMOUR_1);

		prospectorHelmet = prospectorHelmet.showConditioned(prospectorHelmet.alsoCheckBank(questBank));
		prospectorHelmet.addAlternates(ItemID.GOLDEN_PROSPECTOR_HELMET);

		prospectorJacket = prospectorJacket.showConditioned(prospectorJacket.alsoCheckBank(questBank));
		prospectorJacket.addAlternates(ItemID.GOLDEN_PROSPECTOR_JACKET, ItemID.VARROCK_ARMOUR_4);

		prospectorLegs = prospectorLegs.showConditioned(prospectorLegs.alsoCheckBank(questBank));
		prospectorLegs.addAlternates(ItemID.GOLDEN_PROSPECTOR_LEGS);

		prospectorBoots = prospectorBoots.showConditioned(prospectorBoots.alsoCheckBank(questBank));
		prospectorBoots.addAlternates(ItemID.GOLDEN_PROSPECTOR_BOOTS);

		varrockArmour1.showConditioned(
			new Conditions(mi15, varrockArmour1.alsoCheckBank(questBank),
				new Conditions(LogicType.NOR, prospectorJacket.alsoCheckBank(questBank)))
		);
		varrockArmour1.addAlternates(ItemID.VARROCK_ARMOUR, ItemID.VARROCK_ARMOUR_2, ItemID.VARROCK_ARMOUR_3);
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
		copperStep = new TileStep(this, COPPER_POINT,
			"Stand next to the Copper- and Tin ore",
			ironPickaxe, steelPickaxe, blackPickaxe);
		copperStep.addTileMarker(COPPER_POINT, SpriteID.SKILL_MINING);

		ironStep = new TileStep(this, IRON_POINT,
			"Stand between the Iron ores",
			steelPickaxe, blackPickaxe, mithrilPickaxe, adamantPickaxe, runePickaxe, dragonPickaxe);
		ironStep.addTileMarker(IRON_POINT, SpriteID.SKILL_MINING);


		mineCopperOrTin = new ObjectStep(this, ObjectID.ROCKS_11360, COPPER_POINT,
			"Mine Copper- and Tin ore at South-east Varrock mine until 15 Mining. You can choose to drop " +
				"the ores as you go or bank them in the eastern Varrock bank.", true,
			ironPickaxe, steelPickaxe, blackPickaxe
		);
		mineCopperOrTin.addAlternateObjects(ObjectID.ROCKS_11360, ObjectID.ROCKS_11161, ObjectID.ROCKS_10943);

		mineIron = new ObjectStep(this, ObjectID.ROCKS_11364, IRON_POINT,
			"Mine Iron ore at Al Kharid Mine until 99 Mining. You can choose to drop the ores as you go," +
				" smelt them on the way to the Al Kharid bank or bank the ores as they are.", true,
			steelPickaxe, blackPickaxe, mithrilPickaxe, adamantPickaxe, runePickaxe, dragonPickaxe
		);
		mineIron.addAlternateObjects(ObjectID.ROCKS_11365);
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
		return Arrays.asList(
			ironPickaxe, steelPickaxe, blackPickaxe, mithrilPickaxe, adamantPickaxe, runePickaxe, dragonPickaxe);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(dragonPickaxeRec, prospectorHelmet, prospectorJacket, prospectorLegs, prospectorBoots);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("1 - 15: Mine Copper/Tin", Arrays.asList(copperStep, mineCopperOrTin),
			ironPickaxe, steelPickaxe, blackPickaxe));
		allSteps.add(new PanelDetails("15 - 99: Mine Iron ore", Arrays.asList(ironStep, mineIron),
			Arrays.asList(steelPickaxe, blackPickaxe, mithrilPickaxe, adamantPickaxe, runePickaxe, dragonPickaxe),
			Arrays.asList(dragonPickaxeRec, varrockArmour1, prospectorHelmet, prospectorJacket, prospectorLegs, prospectorBoots)));
		return allSteps;
	}
}