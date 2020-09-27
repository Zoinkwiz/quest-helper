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
package com.questhelper.quests.shadowofthestorm;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import net.runelite.api.ItemID;

public class IncantationStep extends DetailedQuestStep
{
	private final HashMap<Integer, String> words;

	private final boolean reverse;

	public IncantationStep(QuestHelper questHelper, boolean reverse)
	{
		super(questHelper, "Click the demonic sigil and read the incantation.");

		ItemRequirement sigilHighlighted = new ItemRequirement("Demonic sigil", ItemID.DEMONIC_SIGIL);
		sigilHighlighted.setHighlightInInventory(true);
		this.addItemRequirements(new ArrayList<>(Collections.singletonList(sigilHighlighted)));
		this.reverse = reverse;

		words = new HashMap<>();
		words.put(0, "Caldar");
		words.put(1, "Nahudu");
		words.put(2, "Agrith-Naar");
		words.put(3, "Camerinthum");
		words.put(4, "Tarren");
	}

	@Override
	public void startUp()
	{
		super.startUp();
		updateHints();
	}

	protected void updateHints()
	{
		if (client.getVarbitValue(1374) == 0 && client.getVarbitValue(1375) == 0)
		{
			return;
		}

		String incantString = "Say the following in order: ";
		if (reverse)
		{
			incantString += words.get(client.getVarbitValue(1377)) + ", ";
			incantString += words.get(client.getVarbitValue(1376)) + ", ";
			incantString += words.get(client.getVarbitValue(1375)) + ", ";
			incantString += words.get(client.getVarbitValue(1374)) + ", ";
			incantString += words.get(client.getVarbitValue(1373)) + ".";
		}
		else
		{
			incantString += words.get(client.getVarbitValue(1373)) + ", ";
			incantString += words.get(client.getVarbitValue(1374)) + ", ";
			incantString += words.get(client.getVarbitValue(1375)) + ", ";
			incantString += words.get(client.getVarbitValue(1376)) + ", ";
			incantString += words.get(client.getVarbitValue(1377)) + ".";
		}

		setText("Click the demonic sigil and read the incantation.");
		addText(incantString);
	}
}
