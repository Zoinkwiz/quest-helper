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
package com.questhelper.steps;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.VarbitChanged;

import java.util.ArrayList;
import java.util.List;


/**
 * MultiNpcStep should be used over NpcStep when the NPC you're trying to track is a "MultiNPC"
 * TODO: Add some documentation for how you can know an NPC is a MultiNPC, and what resources to use to debug it (e.g. how to find out which varbit controls it, what its base ID is etc)
 */
public class MultiNpcStep extends NpcStep
{
	/**
	 * List of NPCs matching the base composition ID, but not necessarily the exact NPC ID we're looking for
	 */
	private final List<NPC> baseNPCs = new ArrayList<>();

	/**
	 * Varbit that signals a change in the NPC ID, meaning we want to re-checked all suspected NPCs we've found.
	 */
	private final int multinpcVarbit;
	/**
	 * The base composition ID of the NPC we're looking for.
	 * @see <a href="https://static.runelite.net/api/runelite-api/net/runelite/api/NPCComposition.html#getId()">NPCComposition#getId()</a>
	 */
	private final int npcCompositionID;

	public MultiNpcStep(QuestHelper questHelper, int npcID, WorldPoint worldPoint, String text, int multinpcVarbit, int npcCompositionID, Requirement... requirements)
	{
		super(questHelper, npcID, worldPoint, text, requirements);
		this.multinpcVarbit = multinpcVarbit;
		this.npcCompositionID = npcCompositionID;
	}

	public MultiNpcStep(QuestHelper questHelper, int npcID, WorldPoint worldPoint, String text, int multinpcVarbit, int npcCompositionID, List<Requirement> requirements)
	{
		super(questHelper, npcID, worldPoint, text, requirements, new ArrayList<>());
		this.multinpcVarbit = multinpcVarbit;
		this.npcCompositionID = npcCompositionID;
	}

	@Override
	public void shutDown()
	{
		super.shutDown();
		baseNPCs.clear();
	}

	@Override
	public void onGameStateChanged(GameStateChanged event)
	{
		super.onGameStateChanged(event);
		if (event.getGameState() == GameState.HOPPING)
		{
			baseNPCs.clear();
		}
	}

	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		super.onVarbitChanged(varbitChanged);

		npcs.removeIf(npc -> npc.getId() != npcID);

		for (NPC foundBaseNPC : baseNPCs)
		{
			if (foundBaseNPC.getId() == npcID)
			{
				npcs.add(foundBaseNPC);
			}
		}
	}

	protected boolean npcIsCompositionMatch(NPC npc)
	{
		return npcCompositionID == npc.getComposition().getId() || alternateNpcIDs.contains(npc.getComposition().getId());
	}

	@Override
	public void scanForNpcs()
	{
		super.scanForNpcs();
		for (NPC npc : client.getTopLevelWorldView().npcs())
		{
			addNpcToListGivenMatchingID(npc, this::npcIsCompositionMatch, baseNPCs);
		}
	}

	@Override
	public void onNpcSpawned(NpcSpawned event)
	{
		super.onNpcSpawned(event);
		addNpcToListGivenMatchingID(event.getNpc(), this::npcIsCompositionMatch, baseNPCs);
	}

	@Override
	public void onNpcDespawned(NpcDespawned event)
	{
		super.onNpcDespawned(event);
		baseNPCs.remove(event.getNpc());
	}

	@Override
	public NpcStep copy()
	{
		MultiNpcStep newStep = new MultiNpcStep(getQuestHelper(), npcID, worldPoint, null, multinpcVarbit, npcCompositionID, requirements);
		if (text != null)
		{
			newStep.setText(text);
		}
		newStep.allowMultipleHighlights = allowMultipleHighlights;
		newStep.addAlternateNpcs(alternateNpcIDs);
		if (mustBeFocusedOnPlayer)
		{
			newStep.setMustBeFocusedOnPlayer(true);
		}
		newStep.setMaxRoamRange(maxRoamRange);

		return newStep;
	}
}
