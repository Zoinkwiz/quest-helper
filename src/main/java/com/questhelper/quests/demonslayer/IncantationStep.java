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
package com.questhelper.quests.demonslayer;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.QuestStep;
import java.util.HashMap;

public class IncantationStep extends ConditionalStep
{
	private final HashMap<Integer, String> words;

	private QuestStep incantationStep;

	public IncantationStep(QuestHelper questHelper, QuestStep incantationStep)
	{
		super(questHelper, incantationStep);
		this.incantationStep = incantationStep;
		this.steps.get(null).getText().add("Incantation is currently unknown.");
		words = new HashMap<>();
		words.put(0, "Carlem");
		words.put(1, "Aber");
		words.put(2, "Camerinthum");
		words.put(3, "Purchai");
		words.put(4, "Gabindo");
	}

	@Override
	protected void updateSteps()
	{
		if (client.getVarbitValue(2562) == 0 && client.getVarbitValue(2563) == 0)
		{
			return;
		}

		String incantString = "Say the following in order: ";
		incantString += words.get(client.getVarbitValue(2562)) + ", ";
		incantString += words.get(client.getVarbitValue(2563)) + ", ";
		incantString += words.get(client.getVarbitValue(2564)) + ", ";
		incantString += words.get(client.getVarbitValue(2565)) + ", ";
		incantString += words.get(client.getVarbitValue(2566)) + ".";
		steps.get(null).getText().set(1, incantString);

		startUpStep(incantationStep);
	}

}
