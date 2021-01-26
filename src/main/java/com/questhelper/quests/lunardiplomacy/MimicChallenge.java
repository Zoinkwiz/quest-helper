/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.quests.lunardiplomacy;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.EmoteStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.emote.QuestEmote;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;

public class MimicChallenge extends DetailedOwnerStep
{
	DetailedQuestStep cry, bow, dance, wave, think, talk;

	public MimicChallenge(QuestHelper questHelper)
	{
		super(questHelper, "Copy the emotes that the NPC does.");
	}

	public void setupSteps()
	{
		cry = new EmoteStep(getQuestHelper(), QuestEmote.CRY, new WorldPoint(1769, 5058, 2), "Perform the cry emote.");
		bow = new EmoteStep(getQuestHelper(), QuestEmote.BOW, new WorldPoint(1770, 5063, 2), "Perform the bow emote.");
		dance = new EmoteStep(getQuestHelper(), QuestEmote.DANCE, new WorldPoint(1772, 5070, 2), "Perform the dance emote.");
		wave = new EmoteStep(getQuestHelper(), QuestEmote.WAVE, new WorldPoint(1767, 5061, 2), "Perform the wave emote.");
		think = new EmoteStep(getQuestHelper(), QuestEmote.THINK, new WorldPoint(1772, 5070, 2), "Perform the think emote.");
		talk = new NpcStep(getQuestHelper(), NpcID.ETHEREAL_MIMIC, "Talk to the Ethereal Mimic.");
		talk.addDialogStep("Suppose I may as well have a go.");
	}

	@Subscribe
	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		super.onVarbitChanged(varbitChanged);
		updateSteps();
	}

	@Override
	protected void updateSteps()
	{
		if (client.getVarbitValue(2419) == 0)
		{
			startUpStep(talk);
			return;
		}

		switch (client.getVarbitValue(2420))
		{
			case 1:
				startUpStep(cry);
				break;
			case 2:
				startUpStep(bow);
				break;
			case 3:
				startUpStep(dance);
				break;
			case 4:
				startUpStep(wave);
				break;
			case 5:
				startUpStep(think);
				break;
			default:
				startUpStep(talk);
				break;
		}
	}

	public void chooseStepBasedOnIfTalked(QuestStep emoteStep)
	{
		if (client.getVarbitValue(2419) == 1)
		{
			startUpStep(emoteStep);
		}
		else
		{
			startUpStep(talk);
		}
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(talk, cry, bow, dance, think, wave);
	}

	public List<QuestStep> getDisplaySteps()
	{
		return Arrays.asList(talk, cry, bow, dance, think, wave);
	}
}

