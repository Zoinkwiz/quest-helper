/*
 * Copyright (c) 2023, Zoinkwiz
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
package com.questhelper.helpers.quests.deserttreasureii;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.ObjectStep;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

// 15203->208 dictate order, with 203 being first, etc.
// Value contained dictacts which of the runes it is, so:
// 1 = Earth
// 2 = Cosmic
// 3 = Death
// 4 = Nature
// 5 = Law
// 6 = Fire
// So 15203 = 2 means Cosmic first
public class GrowthPuzzleStep extends ObjectStep
{
	int[] runeIDs = new int[] {
		ObjectID.DT2_SCAR_MAZE_1_RIFT_EARTH_VIS,
		ObjectID.DT2_SCAR_MAZE_1_RIFT_COSMIC_VIS,
		ObjectID.DT2_SCAR_MAZE_1_RIFT_DEATH_VIS,
		ObjectID.DT2_SCAR_MAZE_1_RIFT_NATURE_VIS,
		ObjectID.DT2_SCAR_MAZE_1_RIFT_LAW_VIS,
		ObjectID.DT2_SCAR_MAZE_1_RIFT_FIRE_VIS
	};

	int highestPointReached = 0;
	public GrowthPuzzleStep(QuestHelper questHelper)
	{
		super(questHelper, -1,
			"Work out the correct order to activate the runes through trial and error.");
		addAlternateObjects(ObjectID.DT2_SCAR_MAZE_1_RIFT_EARTH_VIS, ObjectID.DT2_SCAR_MAZE_1_RIFT_COSMIC_VIS,
				ObjectID.DT2_SCAR_MAZE_1_RIFT_DEATH_VIS, ObjectID.DT2_SCAR_MAZE_1_RIFT_NATURE_VIS,
				ObjectID.DT2_SCAR_MAZE_1_RIFT_LAW_VIS, ObjectID.DT2_SCAR_MAZE_1_RIFT_FIRE_VIS);
	}

	@Override
	public void startUp()
	{
		super.startUp();
		setupHighlights();
	}

	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		super.onVarbitChanged(varbitChanged);
		setupHighlights();
	}

	private void setupHighlights()
	{
		int currentRunesActive = client.getVarbitValue(VarbitID.DT2_SCAR_MAZE_1_RIFT_STEP);
		if (highestPointReached < currentRunesActive) highestPointReached = currentRunesActive;

		alternateObjectIDs.clear();
		if (currentRunesActive < highestPointReached)
		{
			int runeToHighlight = client.getVarbitValue(VarbitID.DT2_SCAR_MAZE_1_RIFT_1 + currentRunesActive) - 1;
			addAlternateObjects(runeIDs[runeToHighlight]);
			loadObjects();
			return;
		}

		for (int i=VarbitID.DT2_SCAR_MAZE_1_RIFT_1 + currentRunesActive; i < VarbitID.DT2_SCAR_MAZE_1_RIFT_1 + 6; i++)
		{
			int runeToHighlight = client.getVarbitValue(i) - 1;
			alternateObjectIDs.add(runeIDs[runeToHighlight]);
		}
		loadObjects();
	}

}
