/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
 * Copyright (c) 2019, Trevor <https://github.com/Trevor159>
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
import com.questhelper.steps.tools.DefinedPoint;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NpcStep extends DetailedQuestStep
{
	@Inject
	protected Client client;

	public NpcStep(QuestHelper questHelper, int npcID, String text, Requirement... requirements)
	{
		super(questHelper, text, requirements);
		this.npcID = npcID;
	}

	public NpcStep(QuestHelper questHelper, int[] npcID, String text, Requirement... requirements)
	{
		super(questHelper, text, requirements);
		this.npcID = npcID[0];
		if (npcID.length > 1)
		{
			this.alternateNpcIDs = new ArrayList<>();
			for (int i = 1; i < npcID.length; i++)
			{
				this.alternateNpcIDs.add(npcID[i]);
			}
		}
	}

	public NpcStep(QuestHelper questHelper, int[] npcID, WorldPoint worldPoint, String text, boolean allowMultipleHighlights, Requirement... requirements)
	{
		this(questHelper, npcID, worldPoint, text, requirements);
		this.allowMultipleHighlights = allowMultipleHighlights;
	}

	public NpcStep(QuestHelper questHelper, int npcID, WorldPoint worldPoint, String text, Requirement... requirements)
	{
		super(questHelper, worldPoint, text, requirements);
		this.npcID = npcID;
	}

	public NpcStep(QuestHelper questHelper, int npcID, String npcName, WorldPoint worldPoint, String text, Requirement... requirements)
	{
		super(questHelper, worldPoint, text, requirements);
		this.npcID = npcID;
		this.npcName = npcName;
	}

	public NpcStep(QuestHelper questHelper, int npcID, String npcName, WorldPoint worldPoint, String text, boolean allowMultipleHighlights, Requirement... requirements)
	{
		super(questHelper, worldPoint, text, requirements);
		this.npcID = npcID;
		this.npcName = npcName;
		this.allowMultipleHighlights = allowMultipleHighlights;
	}

	public NpcStep(QuestHelper questHelper, int npcID, WorldPoint worldPoint, String text, List<Requirement> requirements, List<Requirement> optionalRequirements)
	{
		super(questHelper, worldPoint, text, requirements, optionalRequirements);
		this.npcID = npcID;
	}

	public NpcStep(QuestHelper questHelper, int npcID, WorldPoint worldPoint, String text, boolean allowMultipleHighlights, List<Requirement> requirements, List<Requirement> optionalRequirements)
	{
		super(questHelper, worldPoint, text, requirements, optionalRequirements);
		this.npcID = npcID;
		this.allowMultipleHighlights = allowMultipleHighlights;
	}

	public NpcStep(QuestHelper questHelper, int[] npcID, WorldPoint worldPoint, String text, Requirement... requirements)
	{
		super(questHelper, worldPoint, text, requirements);
		this.npcID = npcID[0];
		if (npcID.length > 1)
		{
			this.alternateNpcIDs = new ArrayList<>();
			for (int i = 1; i < npcID.length; i++)
			{
				this.alternateNpcIDs.add(npcID[i]);
			}
		}
	}

	public NpcStep(QuestHelper questHelper, int npcID, WorldPoint worldPoint, String text, boolean allowMultipleHighlights, Requirement... requirements)
	{
		this(questHelper, npcID, worldPoint, text, requirements);
		this.allowMultipleHighlights = allowMultipleHighlights;
	}

	public NpcStep(QuestHelper questHelper, int npcID, String text, boolean allowMultipleHighlights, Requirement... requirements)
	{
		this(questHelper, npcID, null, text, allowMultipleHighlights, requirements);
	}

	public NpcStep(QuestHelper questHelper, int npcID, DefinedPoint definedPoint, String text, Requirement... requirements)
	{
		super(questHelper, definedPoint, text, requirements);
		this.npcID = npcID;
	}

	public NpcStep copy()
	{
		NpcStep newStep = new NpcStep(getQuestHelper(), npcID, definedPoint, null);
		newStep.setRequirements(requirements);
		newStep.setRecommended(recommended);

		if (text != null)
		{
			newStep.setText(text);
		}
		newStep.allowMultipleHighlights = allowMultipleHighlights;
		if (this.alternateNpcIDs != null)
		{
			newStep.addAlternateNpcs(alternateNpcIDs);
		}
		if (mustBeFocusedOnPlayer)
		{
			newStep.setMustBeFocusedOnPlayer(true);
		}
		newStep.setMaxRoamRange(maxRoamRange);

		return newStep;
	}

	@Override
	public NpcStep addAlternateNpcs(Integer... alternateNpcIDs)
	{
		super.addAlternateNpcs(alternateNpcIDs);

		return this;
	}

	@Override
	public NpcStep addAlternateNpcs(@Nonnull List<Integer> alternateNpcIDs)
	{
		super.addAlternateNpcs(alternateNpcIDs);

		return this;
	}

	@Override
	public void shutDown()
	{
		super.shutDown();
		npcs.clear();
	}

	@Override
	protected void renderTileIcon(Graphics2D graphics)
	{
	}
}
