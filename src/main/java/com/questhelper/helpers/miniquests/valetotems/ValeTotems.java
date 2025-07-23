/*
 * Copyright (c) 2025, pajlada <https://github.com/pajlada>
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
package com.questhelper.helpers.miniquests.valetotems;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.widget.WidgetHighlight;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

/**
 * The quest guide for the "Vale Totems" OSRS quest
 */
public class ValeTotems extends BasicQuestHelper
{
	// Item requirements
	ItemRequirement knife;
	ItemRequirement oneOakLog;
	ItemRequirement fourDecorativeItems;
	ItemRequirement threeDecorativeItems;
	ItemRequirement twoDecorativeItems;
	ItemRequirement oneDecorativeItem;

	// Miscellaneous requirements
	VarbitRequirement needToBuildTotem;
	VarbitRequirement isTotemBaseBuilt;

	VarbitRequirement needToCarveAnimals;

	Conditions isBuffaloNearby;
	Conditions isJaguarNearby;
	Conditions isEagleNearby;
	Conditions isSnakeNearby;
	Conditions isScorpionNearby;

	Conditions missingBuffaloCarve;
	Conditions missingJaguarCarve;
	Conditions missingEagleCarve;
	Conditions missingSnakeCarve;
	Conditions missingScorpionCarve;

	VarbitRequirement isDoneCarving;

	VarbitRequirement needToDecorate;

	VarbitRequirement oneShieldAdded;
	VarbitRequirement twoShieldsAdded;
	VarbitRequirement threeShieldsAdded;
	VarbitRequirement isDoneDecorating;

	// Zones
	Zone firstTotemZone;

	// Steps
	NpcStep startQuest;

	ObjectStep buildTotemBase;

	ObjectStep carveAnimalsYouSee;
	ConditionalStep carveTotem;
	NpcStep talkToIsadoraAfterCarvingTotem;
	ConditionalStep decorateTotem;
	ObjectStep decorateTotemFourShields;
	NpcStep talkToIsadoraAfterDecoratingTotem;
	ConditionalStep carveAndDecorateTotem;

	QuestStep claimOffering;

	QuestStep finishQuest;
	NpcStep talkToIsadoraToLearnAboutCarving;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, startQuest);
		steps.put(10, startQuest);
		steps.put(20, startQuest);
		steps.put(30, carveAndDecorateTotem);
		steps.put(40, talkToIsadoraAfterDecoratingTotem);
		steps.put(50, claimOffering);
		steps.put(60, finishQuest);

		return steps;
	}

	@Override
	protected void setupZones()
	{
		firstTotemZone = new Zone(new WorldPoint(1380, 3384, 0), new WorldPoint(1362, 3365, 0));
	}

	@Override
	protected void setupRequirements()
	{
		knife = new ItemRequirement("Knife", ItemID.KNIFE);
		oneOakLog = new ItemRequirement("Oak log", ItemID.OAK_LOGS, 1);
		oneOakLog.setTooltip("You can also use Willow, Maple, Yew, Magic, or Redwood logs, but it needs to match the decorative items you're bringing.");

		var possibleDecorativeItems = List.of(ItemID.OAK_SHIELD, ItemID.UNSTRUNG_OAK_LONGBOW, ItemID.OAK_LONGBOW, ItemID.UNSTRUNG_OAK_SHORTBOW, ItemID.OAK_SHORTBOW);

		fourDecorativeItems = new ItemRequirement("Oak shield/longbow/shortbow", possibleDecorativeItems, 4);
		fourDecorativeItems.setTooltip("You can also use Willow, Maple, Yew, Magic, or Redwood decorative items, but it needs to match the logs you used to build the totem.");
		threeDecorativeItems = new ItemRequirement("Oak shield/longbow/shortbow", possibleDecorativeItems, 3);
		threeDecorativeItems.setTooltip("You can also use Willow, Maple, Yew, Magic, or Redwood decorative items, but it needs to match the logs you used to build the totem.");
		twoDecorativeItems = new ItemRequirement("Oak shield/longbow/shortbow", possibleDecorativeItems, 2);
		twoDecorativeItems.setTooltip("You can also use Willow, Maple, Yew, Magic, or Redwood decorative items, but it needs to match the logs you used to build the totem.");
		oneDecorativeItem = new ItemRequirement("Oak shield/longbow/shortbow", possibleDecorativeItems, 1);
		oneDecorativeItem.setTooltip("You can also use Willow, Maple, Yew, Magic, or Redwood decorative items, but it needs to match the logs you used to build the totem.");

		needToBuildTotem = new VarbitRequirement(VarbitID.ENT_TOTEMS_BROKEN_CHAT, 1);
		isTotemBaseBuilt = new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_BASE, 1);

		isBuffaloNearby = or(
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_ANIMAL_1, 1),
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_ANIMAL_2, 1),
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_ANIMAL_3, 1)
		);
		isJaguarNearby = or(
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_ANIMAL_1, 2),
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_ANIMAL_2, 2),
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_ANIMAL_3, 2)
		);
		isEagleNearby = or(
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_ANIMAL_1, 3),
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_ANIMAL_2, 3),
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_ANIMAL_3, 3)
		);
		isSnakeNearby = or(
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_ANIMAL_1, 4),
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_ANIMAL_2, 4),
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_ANIMAL_3, 4)
		);
		isScorpionNearby = or(
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_ANIMAL_1, 5),
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_ANIMAL_2, 5),
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_ANIMAL_3, 5)
		);

		missingBuffaloCarve = and(
			// TODO: CONFIrm it's 10
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_LOW, 10, Operation.NOT_EQUAL),
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_MID, 10, Operation.NOT_EQUAL),
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_TOP, 10, Operation.NOT_EQUAL)
		);

		missingJaguarCarve = and(
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_LOW, 11, Operation.NOT_EQUAL),
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_MID, 11, Operation.NOT_EQUAL),
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_TOP, 11, Operation.NOT_EQUAL)
		);

		missingEagleCarve = and(
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_LOW, 12, Operation.NOT_EQUAL),
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_MID, 12, Operation.NOT_EQUAL),
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_TOP, 12, Operation.NOT_EQUAL)
		);

		missingSnakeCarve = and(
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_LOW, 13, Operation.NOT_EQUAL),
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_MID, 13, Operation.NOT_EQUAL),
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_TOP, 13, Operation.NOT_EQUAL)
		);

		missingScorpionCarve = and(
			// TODO: Confirm that 14 is the correct number
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_LOW, 14, Operation.NOT_EQUAL),
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_MID, 14, Operation.NOT_EQUAL),
			new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_TOP, 14, Operation.NOT_EQUAL)
		);

		isDoneCarving = new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_BASE_CARVED, 1);

		needToCarveAnimals = new VarbitRequirement(VarbitID.ENT_TOTEMS_CARVE_CHAT, 1);
		needToDecorate = new VarbitRequirement(VarbitID.ENT_TOTEMS_DECORATE_CHAT, 1);

		oneShieldAdded = new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_DECORATIONS, 1);
		twoShieldsAdded = new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_DECORATIONS, 2);
		threeShieldsAdded = new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_DECORATIONS, 3);
		isDoneDecorating = new VarbitRequirement(VarbitID.ENT_TOTEMS_SITE_1_DECORATIONS, 4);
	}

	public void setupSteps()
	{
		startQuest = new NpcStep(this, new int[]{NpcID.ENT_TOTEMS_INTRO_RANULPH, NpcID.ENT_TOTEMS_INTRO_RANULPH_1OP, NpcID.ENT_TOTEMS_INTRO_RANULPH_2OP, NpcID.ENT_TOTEMS_INTRO_RANULPH_CS,}, new WorldPoint(1365, 3366, 0), "Talk to Ranulph in the west part of Auburnvale to start the quest.");
		startQuest.addDialogStep("Yes.");

		buildTotemBase = new ObjectStep(this, ObjectID.ENT_TOTEMS_BASE_NONE, new WorldPoint(1370, 3375, 0), "Build the totem site.", knife, oneOakLog);

		talkToIsadoraToLearnAboutCarving = new NpcStep(this, NpcID.ENT_TOTEMS_INTRO_CHILD_VIS, new WorldPoint(1366, 3369, 0), "Talk to Isadora to learn about carving.");
		talkToIsadoraAfterCarvingTotem = new NpcStep(this, NpcID.ENT_TOTEMS_INTRO_CHILD_VIS, new WorldPoint(1366, 3369, 0), "Return to Isadora after carving the spirit animals into the totem.");

		var carveBuffalo = new ObjectStep(this, ObjectID.ENT_TOTEMS_SITE_1_BASE, new WorldPoint(1370, 3375, 0), "Carve a Buffalo into the totem base.");
		carveBuffalo.addWidgetHighlight(WidgetHighlight.createMultiskillByName("Buffalo"));

		var carveJaguar = new ObjectStep(this, ObjectID.ENT_TOTEMS_SITE_1_BASE, new WorldPoint(1370, 3375, 0), "Carve a Jaguar into the totem base.");
		carveJaguar.addWidgetHighlight(WidgetHighlight.createMultiskillByName("Jaguar"));

		var carveEagle = new ObjectStep(this, ObjectID.ENT_TOTEMS_SITE_1_BASE, new WorldPoint(1370, 3375, 0), "Carve a Eagle into the totem base.");
		carveEagle.addWidgetHighlight(WidgetHighlight.createMultiskillByName("Eagle"));

		var carveSnake = new ObjectStep(this, ObjectID.ENT_TOTEMS_SITE_1_BASE, new WorldPoint(1370, 3375, 0), "Carve a Snake into the totem base.");
		carveSnake.addWidgetHighlight(WidgetHighlight.createMultiskillByName("Snake"));

		var carveScorpion = new ObjectStep(this, ObjectID.ENT_TOTEMS_SITE_1_BASE, new WorldPoint(1370, 3375, 0), "Carve a Scorpion into the totem base.");
		carveScorpion.addWidgetHighlight(WidgetHighlight.createMultiskillByName("Scorpion"));

		// fallback step in case our detection fails
		carveAnimalsYouSee = new ObjectStep(this, ObjectID.ENT_TOTEMS_SITE_1_BASE, new WorldPoint(1370, 3375, 0), "Carve spirit animals you see into the totem.");
		carveAnimalsYouSee.addSubSteps(carveBuffalo, carveJaguar, carveEagle, carveSnake, carveScorpion);

		carveTotem = new ConditionalStep(this, carveAnimalsYouSee);
		carveTotem.addStep(and(isBuffaloNearby, missingBuffaloCarve), carveBuffalo);
		carveTotem.addStep(and(isJaguarNearby, missingJaguarCarve), carveJaguar);
		carveTotem.addStep(and(isEagleNearby, missingEagleCarve), carveEagle);
		carveTotem.addStep(and(isSnakeNearby, missingSnakeCarve), carveSnake);
		carveTotem.addStep(and(isScorpionNearby, missingScorpionCarve), carveScorpion);

		var decorateTotemOneShield = new ObjectStep(this, ObjectID.ENT_TOTEMS_SITE_1_BASE, new WorldPoint(1370, 3375, 0), "Decorate the totem with one decorative item (shield, longbow, or shortbow) of the same wood type you used to build/carve the totem.", oneDecorativeItem);
		var decorateTotemTwoShields = new ObjectStep(this, ObjectID.ENT_TOTEMS_SITE_1_BASE, new WorldPoint(1370, 3375, 0), "Decorate the totem with two decorative items (shield, longbow, or shortbow) of the same wood type you used to build/carve the totem.", twoDecorativeItems);
		var decorateTotemThreeShields = new ObjectStep(this, ObjectID.ENT_TOTEMS_SITE_1_BASE, new WorldPoint(1370, 3375, 0), "Decorate the totem with three decorative items (shield, longbow, or shortbow) of the same wood type you used to build/carve the totem.", threeDecorativeItems);
		decorateTotemFourShields = new ObjectStep(this, ObjectID.ENT_TOTEMS_SITE_1_BASE, new WorldPoint(1370, 3375, 0), "Decorate the totem with four decorative items (shield, longbow, or shortbow) of the same wood type you used to build/carve the totem.", fourDecorativeItems);
		decorateTotemFourShields.addSubSteps(decorateTotemOneShield, decorateTotemTwoShields, decorateTotemThreeShields);
		talkToIsadoraAfterDecoratingTotem = new NpcStep(this, NpcID.ENT_TOTEMS_INTRO_CHILD_VIS, new WorldPoint(1366, 3369, 0), "Return to Isadora to talk after decorating the totem.");
		decorateTotem = new ConditionalStep(this, decorateTotemFourShields);
		decorateTotem.addStep(oneShieldAdded, decorateTotemThreeShields);
		decorateTotem.addStep(twoShieldsAdded, decorateTotemTwoShields);
		decorateTotem.addStep(threeShieldsAdded, decorateTotemOneShield);

		carveAndDecorateTotem = new ConditionalStep(this, startQuest);
		carveAndDecorateTotem.addStep(isDoneDecorating, talkToIsadoraAfterDecoratingTotem);
		carveAndDecorateTotem.addStep(needToDecorate, decorateTotem);
		carveAndDecorateTotem.addStep(isDoneCarving, talkToIsadoraAfterCarvingTotem);
		carveAndDecorateTotem.addStep(needToCarveAnimals, carveTotem);
		carveAndDecorateTotem.addStep(isTotemBaseBuilt, talkToIsadoraToLearnAboutCarving);
		carveAndDecorateTotem.addStep(needToBuildTotem, buildTotemBase);

		claimOffering = new ObjectStep(this, ObjectID.ENT_TOTEMS_OFFERINGS_B, new WorldPoint(1370, 3374, 0), "Claim the offering the Ent left next to your totem.");
		finishQuest = new NpcStep(this, NpcID.ENT_TOTEMS_INTRO_CHILD_VIS, new WorldPoint(1366, 3369, 0), "Return to Isadora to finish the quest.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			knife,
			oneOakLog,
			fourDecorativeItems
		);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.CHILDREN_OF_THE_SUN, QuestState.FINISHED),
			new SkillRequirement(Skill.FLETCHING, 20)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Unlocks the Auburnvale fletching minigame")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var panels = new ArrayList<PanelDetails>();

		panels.add(new PanelDetails("Repair the totem", List.of(
			startQuest,
			buildTotemBase,
			talkToIsadoraToLearnAboutCarving,
			carveAnimalsYouSee,
			talkToIsadoraAfterCarvingTotem,
			decorateTotemFourShields,
			talkToIsadoraAfterDecoratingTotem,
			claimOffering,
			finishQuest
		), List.of(
			knife,
			oneOakLog,
			fourDecorativeItems
		)));

		return panels;
	}
}
