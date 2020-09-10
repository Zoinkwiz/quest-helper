/*
 * Copyright (c) 2020, Zoinkwiz
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
package com.questhelper.quests.piratestreasure;

import com.questhelper.QuestHelperQuest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.*;

import java.util.HashMap;
import java.util.Map;
import com.questhelper.steps.conditional.ItemRequirementCondition;

@QuestDescriptor(
        quest = QuestHelperQuest.PIRATES_TREASURE
)
public class PiratesTreasure extends BasicQuestHelper
{
	private ItemRequirement sixtyCoins, spade;

	private NpcStep speakToRedbeard;
	private RumSmugglingStep smuggleRum;
	private QuestStep readPirateMessage;
	private ObjectStep openChest;
	private QuestStep digUpTreasure;

	@Override
    public Map<Integer, QuestStep> loadSteps()
    {
    	sixtyCoins = new ItemRequirement("Coins", ItemID.COINS_995, 60);
    	spade = new ItemRequirement("Spade", ItemID.SPADE);

        Map<Integer, QuestStep> steps = new HashMap<>();

		speakToRedbeard = new NpcStep(this, NpcID.REDBEARD_FRANK, new WorldPoint(3053, 3251, 0),
			"Talk to Redbeard Frank in Port Sarim.");
		speakToRedbeard.addDialogStep("I'm in search of treasure.");
		speakToRedbeard.addDialogStep("Ok, I will bring you some rum");

		steps.put(0, speakToRedbeard);

		smuggleRum = new RumSmugglingStep(this);

        steps.put(1, smuggleRum);

		ItemRequirement pirateMessage = new ItemRequirement("Pirate message", ItemID.PIRATE_MESSAGE);
        ItemRequirement chestKey = new ItemRequirement("Chest key", ItemID.CHEST_KEY);
        chestKey.setTip("You can get another one from Redbeard Frank");

		ItemRequirementCondition hasPirateMessage = new ItemRequirementCondition(pirateMessage);

        readPirateMessage = new DetailedQuestStep(this, "Read the Pirate message.", pirateMessage);
		openChest = new ObjectStep(this, ObjectID.CHEST_2079, new WorldPoint(3219, 3396, 1),
                "Open the chest upstairs in The Blue Moon Inn in Varrock by using the key on it.",
                chestKey);
		openChest.addDialogStep("Ok thanks, I'll go and get it.");
		openChest.addIcon(ItemID.CHEST_KEY);

        ConditionalStep getTreasureMap = new ConditionalStep(this, openChest);
        getTreasureMap.addStep(hasPirateMessage, readPirateMessage);

        steps.put(2, getTreasureMap);

        digUpTreasure = new DigStep(this, new WorldPoint(2999, 3383, 0),
			"Dig in the middle of the cross in Falador Park, and kill the Gardener (level 4) who appears. Once killed, dig again.");

        steps.put(3, digUpTreasure);
        return steps;
    }

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(sixtyCoins);
		reqs.add(spade);

		return reqs;
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(new ItemRequirement("A teleport to Varrock", ItemID.VARROCK_TELEPORT));
		reqs.add(new ItemRequirement("A teleport to Falador", ItemID.FALADOR_TELEPORT));
		reqs.add(new ItemRequirement("Bananas (obtainable in quest)", ItemID.BANANA, 10));

		return reqs;
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Arrays.asList("Gardener (level 4)"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels() {
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Talk to Redbeard Frank", new ArrayList<>(Collections.singletonList(speakToRedbeard)), sixtyCoins));
		allSteps.addAll(smuggleRum.panelDetails());
		allSteps.add(new PanelDetails("Discover the treasure", new ArrayList<>(Arrays.asList(openChest, readPirateMessage, digUpTreasure)), spade));

		return allSteps;
	}
}
