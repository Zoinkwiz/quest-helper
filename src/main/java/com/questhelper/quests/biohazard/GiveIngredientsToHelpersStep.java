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
package com.questhelper.quests.biohazard;

import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import java.util.Arrays;
import java.util.Collection;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;
import net.runelite.client.eventbus.Subscribe;

public class GiveIngredientsToHelpersStep extends DetailedOwnerStep
{
	DetailedQuestStep giveHopsBroline, giveVinciEthenea, giveChancyHoney;

	ItemRequirement ethenea, liquidHoney, sulphuricBroline;

	int lastNpcInteractedWith;

	public GiveIngredientsToHelpersStep(QuestHelper questHelper)
	{
		super(questHelper);
	}

	@Override
	protected void updateSteps()
	{
		if (sulphuricBroline.check(client))
		{
			startUpStep(giveHopsBroline);
		}
		else if (liquidHoney.check(client))
		{
			startUpStep(giveChancyHoney);
		}
		else if (ethenea.check(client))
		{
			startUpStep(giveVinciEthenea);
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if ((currentStep == giveHopsBroline && !sulphuricBroline.check(client))
			|| (currentStep == giveChancyHoney && !liquidHoney.check(client))
		    || (currentStep == giveVinciEthenea && !ethenea.check(client)))
		{
			updateSteps();
		}
	}

	@Subscribe
	public void onInteractingChanged(InteractingChanged event)
	{
		if (event.getSource() == client.getLocalPlayer()
			&& event.getTarget() instanceof NPC)
		{
			int npcID = ((NPC) event.getTarget()).getId();

			if (npcID == NpcID.HOPS && sulphuricBroline.check(client))
			{
				lastNpcInteractedWith = npcID;
				startUpStep(giveHopsBroline);
			}
			else if (npcID == NpcID.CHANCY && liquidHoney.check(client))
			{
				lastNpcInteractedWith = npcID;
				startUpStep(giveChancyHoney);
			}
			else if (npcID == NpcID.DA_VINCI && ethenea.check(client))
			{
				lastNpcInteractedWith = npcID;
				startUpStep(giveVinciEthenea);
			}
		}
	}

	@Override
	protected void setupSteps()
	{
		ethenea = new ItemRequirement("Ethenea", ItemID.ETHENEA);
		ethenea.setTooltip("You can get another from Elena in East Ardougne.");
		liquidHoney = new ItemRequirement("Liquid honey", ItemID.LIQUID_HONEY);
		liquidHoney.setTooltip("You can get another from Elena in East Ardougne.");
		sulphuricBroline = new ItemRequirement("Sulphuric broline", ItemID.SULPHURIC_BROLINE);
		sulphuricBroline.setTooltip("You can get another from Elena in East Ardougne.");

		giveHopsBroline = new NpcStep(getQuestHelper(), NpcID.HOPS, new WorldPoint(2930, 3220, 0), "Give Hops the Sulphuric Broline.", sulphuricBroline);
		giveHopsBroline.addDialogStep("You give him the vial of sulphuric broline...");
		giveChancyHoney = new NpcStep(getQuestHelper(), NpcID.CHANCY, new WorldPoint(2930, 3220, 0), "Give Chancy the Liquid honey.", liquidHoney);
		giveChancyHoney.addDialogStep("You give him the vial of liquid honey...");
		giveVinciEthenea = new NpcStep(getQuestHelper(), NpcID.DA_VINCI, new WorldPoint(2930, 3220, 0), "Give Da Vinci the Ethenea.", ethenea);
		giveVinciEthenea.addDialogStep("You give him the vial of ethenea...");
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(giveHopsBroline, giveChancyHoney, giveVinciEthenea);
	}

	public Collection<DetailedQuestStep> getDisplaySteps()
	{
		return Arrays.asList(giveHopsBroline, giveChancyHoney, giveVinciEthenea);
	}
}
