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
package com.questhelper.steps.choice;

import lombok.Getter;
import net.runelite.api.Client;

import java.util.ArrayList;
import java.util.Collections;

public class DialogChoiceSteps
{
    @Getter
    final private ArrayList<DialogChoiceStep> choices = new ArrayList<>();

	public DialogChoiceSteps(DialogChoiceStep... choices) {
        Collections.addAll(this.choices, choices);
    }

    public void addChoice(DialogChoiceStep choice) {
        choices.add(choice);
    }

    public void addDialogChoiceWithExclusion(DialogChoiceStep choice, String exclusionString)
	{
		choice.addExclusion(exclusionString, 219, 1);
		addChoice(choice);
	}

    public void checkChoices(Client client) {
        if (choices.size() == 0) {
            return;
        }

        for (DialogChoiceStep currentChoice : choices) {
			currentChoice.highlightChoice(client);
        }
    }

    /**
     * Clears all choices previously set for this dialogs step.
     */
    public void ResetChoices() {
	    choices.clear();
    }
}
