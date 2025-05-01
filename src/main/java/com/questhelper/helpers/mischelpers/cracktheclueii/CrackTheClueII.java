/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
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
package com.questhelper.helpers.mischelpers.cracktheclueii;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.rewards.ItemReward;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.emote.QuestEmote;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.EmoteStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.DigStep;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import static com.questhelper.requirements.util.LogicHelper.nand;
import com.questhelper.questinfo.QuestVarbits; // Added import for QuestVarbits

public class CrackTheClueII extends BasicQuestHelper
{
	ItemRequirement spade, pieDish, rawHerring, goblinMail, plainPizza, woodenShield, cheese;

	DetailedQuestStep week1Dig, week2Dig, week3Emotes, week4Dig, finalEmotes;
	ConditionalStep week1Steps, week2Steps, week3Steps, week4Steps, finalSteps;

	WidgetTextRequirement week1Message, week2Message, week4Message, finalMessage;

	ChatMessageRequirement week3Message;

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
		week3Steps = new ConditionalStep(this, week3Emotes);
		week3Steps.addStep(week3Message, week3Emotes);
		week3Steps.setLockingCondition(week3Message);
		week4Steps = new ConditionalStep(this, week4Dig);
		week4Steps.addStep(week4Message, week4Dig);
		week4Steps.setLockingCondition(week4Message);
		finalSteps = new ConditionalStep(this, finalEmotes);
		finalSteps.addStep(finalMessage, finalEmotes);
		finalSteps.setLockingCondition(finalMessage);

		ConditionalStep allSteps = new ConditionalStep(this, week1Steps);
		allSteps.addStep(nand(inWeek1Zone,week1Message),week1Steps);
		allSteps.addStep(nand(inWeek2Zone, week2Message), week2Steps);
		allSteps.addStep(nand(inWeek3Zone, week3Message), week3Steps);
		allSteps.addStep(nand(inWeek4Zone, week4Message), week4Steps);
		allSteps.addStep(nand(inFinalZone, finalMessage), finalSteps);

		// Updated to use the correct varbit ID for Crack the Clue II
		steps.put(QuestVarbits.CRACK_THE_CLUE_II.getId(), allSteps);

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
		pieDish = new ItemRequirement("Pie dish", ItemID.PIE_DISH).isNotConsumed();
		rawHerring = new ItemRequirement("Raw herring", ItemID.RAW_HERRING).isNotConsumed();
		goblinMail = new ItemRequirement("Goblin mail", ItemID.GOBLIN_MAIL).isNotConsumed();
		plainPizza = new ItemRequirement("Plain pizza", ItemID.PLAIN_PIZZA).isNotConsumed();
		woodenShield = new ItemRequirement("Wooden shield", ItemID.WOODEN_SHIELD).isNotConsumed();
		cheese = new ItemRequirement("Cheese", ItemID.CHEESE).isNotConsumed();
	}

	public void setupZones()
	{
		week1Zone = new Zone(new WorldPoint(2977, 3193, 0), new WorldPoint(2979, 3195, 0));
		week2Zone = new Zone(new WorldPoint(2990, 3294, 0), new WorldPoint(2992, 3296, 0));
		week3Zone = new Zone(new WorldPoint(3034, 3517, 0), new WorldPoint(3036, 3519, 0));
		week4Zone = new Zone(new WorldPoint(3234, 3630, 0), new WorldPoint(3236, 3632, 0));
		finalZone = new Zone(new WorldPoint(3245, 3361, 0), new WorldPoint(3247, 3363, 0));
	}

	public void setupSteps()
	{
		week1Dig = new DigStep(this, new WorldPoint(2978, 3194, 0),
			"Dig south-east of Rimmington and north-west of the chapel.", pieDish);
		week1Message = new WidgetTextRequirement(InterfaceID.Objectbox.TEXT, true, "You find some beautifully ornate gloves and boots.");
		week2Dig = new DigStep(this, new WorldPoint(2991, 3295, 0),
			"Dig by the entrance to the Air Altar south of Falador.", rawHerring);
		week2Message = new WidgetTextRequirement(InterfaceID.Objectbox.TEXT, true, "You find some beautifully ornate leg armour.");

		List<QuestEmote> week3Steps = Arrays.asList(QuestEmote.SHRUG, QuestEmote.CHEER);
		week3Emotes = new EmoteStep(this, week3Steps, new WorldPoint(3035, 3518, 0),
			"Perform the shrug emote followed by cheer emote east of the Black Knights' Fortress.");
		week3Emotes.addTileMarker(new WorldPoint(3035, 3518, 0), SpriteID.TAB_EMOTES);
		week3Message = new ChatMessageRequirement("Some beautifully ornate armour mysteriously appears.");
		week4Dig = new DigStep(this, new WorldPoint(3235, 3631, 0),
			"Dig outside the Chaos Temple in the Wilderness.", goblinMail);
		week4Message = new WidgetTextRequirement(InterfaceID.Objectbox.TEXT, true, "You find a beautifully ornate cape.");

		List<QuestEmote> finalSteps = Arrays.asList(QuestEmote.BOW, QuestEmote.YES, QuestEmote.CLAP);
		finalEmotes = new EmoteStep(this, finalSteps, new WorldPoint(3246, 3362, 0),
			"Perform the bow emote, then yes emote, then clap emote between the trees south of Varrock, east of the stone circle. " +
				"Have only the required items in your inventory/equipped.",
			plainPizza, woodenShield, cheese);
		finalEmotes.addTileMarker(new WorldPoint(3246, 3362, 0), SpriteID.TAB_EMOTES);
		finalMessage = new WidgetTextRequirement(InterfaceID.ChatLeft.TEXT, true, "Here, take this. But tell no one I was here.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(spade, pieDish, rawHerring, goblinMail, plainPizza, woodenShield, cheese);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Ornate gloves", ItemID.ORNATE_GLOVES, 1),
			new ItemReward("Ornate boots", ItemID.ORNATE_BOOTS, 1),
			new ItemReward("Ornate legs", ItemID.ORNATE_LEGS, 1),
			new ItemReward("Ornate top", ItemID.ORNATE_TOP, 1),
			new ItemReward("Ornate cape", ItemID.ORNATE_CAPE, 1),
			new ItemReward("Ornate helm", ItemID.ORNATE_HELM, 1)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails week1Panel = new PanelDetails("Week 1", Collections.singletonList(week1Dig),
			Arrays.asList(spade, pieDish));
		week1Panel.setLockingStep(week1Steps);
		allSteps.add(week1Panel);
		PanelDetails week2Panel = new PanelDetails("Week 2", Collections.singletonList(week2Dig),
			Arrays.asList(spade, rawHerring));
		week2Panel.setLockingStep(week2Steps);
		allSteps.add(week2Panel);

		PanelDetails week3Panel = new PanelDetails("Week 3", Collections.singletonList(week3Emotes));
		week3Panel.setLockingStep(week3Steps);
		allSteps.add(week3Panel);
		PanelDetails week4Panel = new PanelDetails("Week 4", Collections.singletonList(week4Dig),
			Arrays.asList(spade, goblinMail));
		week4Panel.setLockingStep(week4Steps);
		allSteps.add(week4Panel);

		PanelDetails finalPanel = new PanelDetails("Final Clue",
			Collections.singletonList(finalEmotes),
			Arrays.asList(plainPizza, woodenShield, cheese));
		finalPanel.setLockingStep(finalSteps);
		allSteps.add(finalPanel);
		return allSteps;
	}
}
