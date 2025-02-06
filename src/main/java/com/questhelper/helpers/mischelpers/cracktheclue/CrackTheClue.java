/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.mischelpers.cracktheclue;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.DigStep;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.ComponentID;
import static com.questhelper.requirements.util.LogicHelper.nand;

public class CrackTheClue extends BasicQuestHelper
{
	ItemRequirement spade, natureRune, superAntipoison, leatherBoots;

	DetailedQuestStep week1Dig, week2Dig, week3Dig, week4Dig, finalDig;

	ConditionalStep week1Steps, week2Steps, week3Steps, week4Steps, finalSteps;

	WidgetTextRequirement week1Message, week2Message, week3Message, week4Message, finalMessage;

	Zone week1Zone, week2Zone, week3Zone, week4Zone, finalZone;

	ZoneRequirement inWeek1Zone, inWeek2Zone, inWeek3Zone, inWeek4Zone, inFinalZone;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		week1Steps = new ConditionalStep(this, week1Dig);
		week1Steps.addStep(week1Message, week1Dig);
		week1Steps.setLockingCondition(week1Message);

		week2Steps = new ConditionalStep(this, week2Dig);
		week2Steps.addStep(week2Message, week2Dig);
		week2Steps.setLockingCondition(week2Message);

		week3Steps = new ConditionalStep(this, week3Dig);
		week3Steps.addStep(week3Message, week3Dig);
		week3Steps.setLockingCondition(week3Message);

		week4Steps = new ConditionalStep(this, week4Dig);
		week4Steps.addStep(week4Message, week4Dig);
		week4Steps.setLockingCondition(week4Message);

		finalSteps = new ConditionalStep(this, finalDig);
		finalSteps.addStep(finalMessage, finalDig);
		finalSteps.setLockingCondition(finalMessage);

		ConditionalStep allSteps = new ConditionalStep(this, week1Steps);
		allSteps.addStep(nand(inWeek1Zone, week1Message), week1Steps);
		allSteps.addStep(nand(inWeek2Zone, week2Message), week2Steps);
		allSteps.addStep(nand(inWeek3Zone, week3Message), week3Steps);
		allSteps.addStep(nand(inWeek4Zone, week4Message), week4Steps);
		allSteps.addStep(nand(inFinalZone, finalMessage), finalSteps);

		steps.put(0, allSteps);

		return steps;
	}

	public void setupConditions()
	{
		inWeek1Zone = new ZoneRequirement(week1Zone);
		inWeek2Zone = new ZoneRequirement(week2Zone);
		inWeek3Zone = new ZoneRequirement(week3Zone);
		inWeek4Zone = new ZoneRequirement(week4Zone);
		inFinalZone = new ZoneRequirement(finalZone);
	}

	@Override
	public void setupRequirements()
	{
		spade = new ItemRequirement("Spade", ItemID.SPADE).isNotConsumed();
		natureRune = new ItemRequirement("Nature rune", ItemID.NATURE_RUNE).isNotConsumed();
		superAntipoison = new ItemRequirement("Superantipoison(1)", ItemID.SUPERANTIPOISON1).isNotConsumed();
		leatherBoots = new ItemRequirement("Leather boots", ItemID.LEATHER_BOOTS).isNotConsumed();
	}

	public void setupZones()
	{
		// Coordinates for each dig location zone
		week1Zone = new Zone(new WorldPoint(2579, 3377, 0), new WorldPoint(2579, 3377, 0)); // Oak tree SW of Fishing Guild
		week2Zone = new Zone(new WorldPoint(1595, 3628, 0), new WorldPoint(1595, 3628, 0)); // North of East Shayzien
		week3Zone = new Zone(new WorldPoint(2820, 3127, 0), new WorldPoint(2820, 3127, 0)); // Near Pothole Dungeon
		week4Zone = new Zone(new WorldPoint(2616, 3066, 0), new WorldPoint(2616, 3066, 0)); // SE of Yanille
		finalZone = new Zone(new WorldPoint(2590, 3231, 0), new WorldPoint(2590, 3231, 0)); // East of Clock Tower
	}

	public void setupSteps()
	{
		week1Dig = new DigStep(this, new WorldPoint(2579, 3378, 0),
			"Dig two squares south and one square west of the oak tree south-west of the Fishing Guild.");
		week1Message = new WidgetTextRequirement(ComponentID.DIALOG_SPRITE_TEXT, "You dig with your spade and find some gear hidden in the ground.");

		week2Dig = new DigStep(this, new WorldPoint(1595, 3628, 0),
			"Dig north of the East Shayzien station.");
		week2Message = new WidgetTextRequirement(ComponentID.DIALOG_SPRITE_TEXT, "You dig with your spade and find some gear hidden in the ground.");

		week3Dig = new DigStep(this, new WorldPoint(2820, 3127, 0),
			"Dig near the entrance to the Pothole Dungeon north of Tai Bwo Wannai.");
		week3Message = new WidgetTextRequirement(ComponentID.DIALOG_SPRITE_TEXT, "You dig with your spade and find some gear hidden in the ground.");

		week4Dig = new DigStep(this, new WorldPoint(2616, 3066, 0),
			"Dig between two willow trees south-east of Yanille.");
		week4Message = new WidgetTextRequirement(ComponentID.DIALOG_SPRITE_TEXT, "You dig with your spade and find some gear hidden in the ground.");

		finalDig = new DigStep(this, new WorldPoint(2590, 3231, 0),
			"Dig near the iron rocks by the cave entrance east of the Clock Tower. Bring a nature rune, one dose of superantipoison, and leather boots.",
			natureRune, superAntipoison, leatherBoots);
		finalMessage = new WidgetTextRequirement(ComponentID.DIALOG_SPRITE_TEXT, "A selection of the items you are carrying somehow allow you to spot a piece of gear in the ground, which you take.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(spade, natureRune, superAntipoison, leatherBoots);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Clue hunter gloves", ItemID.CLUE_HUNTER_GLOVES, 1),
			new ItemReward("Clue hunter boots", ItemID.CLUE_HUNTER_BOOTS, 1),
			new ItemReward("Clue hunter garb", ItemID.CLUE_HUNTER_GARB, 1),
			new ItemReward("Clue hunter trousers", ItemID.CLUE_HUNTER_TROUSERS, 1),
			new ItemReward("Clue hunter cloak", ItemID.CLUE_HUNTER_CLOAK, 1),
			new ItemReward("Helm of raedwald", ItemID.HELM_OF_RAEDWALD, 1)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails week1Panel = new PanelDetails("Week 1", Collections.singletonList(week1Dig),
			Collections.singletonList(spade));
		week1Panel.setLockingStep(week1Steps);
		allSteps.add(week1Panel);

		PanelDetails week2Panel = new PanelDetails("Week 2", Collections.singletonList(week2Dig),
			Collections.singletonList(spade));
		week2Panel.setLockingStep(week2Steps);
		allSteps.add(week2Panel);

		PanelDetails week3Panel = new PanelDetails("Week 3", Collections.singletonList(week3Dig),
			Collections.singletonList(spade));
		week3Panel.setLockingStep(week3Steps);
		allSteps.add(week3Panel);

		PanelDetails week4Panel = new PanelDetails("Week 4", Collections.singletonList(week4Dig),
			Collections.singletonList(spade));
		week4Panel.setLockingStep(week4Steps);
		allSteps.add(week4Panel);

		PanelDetails finalPanel = new PanelDetails("Final Clue",
			Collections.singletonList(finalDig),
			Arrays.asList(spade, natureRune, superAntipoison, leatherBoots));
		finalPanel.setLockingStep(finalSteps);
		allSteps.add(finalPanel);

		return allSteps;
	}
}
