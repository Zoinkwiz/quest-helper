/*
 * Copyright (c) 2023, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.steps.playermadesteps;

import com.questhelper.requirements.runelite.PlayerQuestStateRequirement;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

public class RuneliteDialogStep
{
	@Getter
	protected final String name;

	@Getter
	protected final String text;

	// Only used for NPCs
	@Getter
	protected final int faceID;

	@Getter
	protected final int animation;

	@Setter
	@Getter
	private RuneliteDialogStep continueDialog;

	@Setter
	@Getter
	private RuneliteConfigSetter stateProgression;

	@Getter
	private final ArrayList<RuneliteDialogStep> dialogChoices = new ArrayList<>();

	public RuneliteDialogStep(String name, String text, int faceID, int animation)
	{
		this.name = name;
		this.text = text;
		this.animation = animation;
		this.faceID = faceID;
	}

	public void addNewDialogChoice(RuneliteDialogStep step)
	{
		dialogChoices.add(step);
	}

	public boolean isPlayer()
	{
		return false;
	}

	public boolean isStateChanger()
	{
		return stateProgression != null;
	}

	public void progressState()
	{
		if (isStateChanger())
		{
			stateProgression.setConfigValue();
		}
	}
}
