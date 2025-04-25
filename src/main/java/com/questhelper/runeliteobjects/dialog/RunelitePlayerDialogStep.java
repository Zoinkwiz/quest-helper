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
package com.questhelper.runeliteobjects.dialog;

import com.questhelper.runeliteobjects.RuneliteConfigSetter;
import com.questhelper.runeliteobjects.extendedruneliteobjects.FaceAnimationIDs;
import net.runelite.api.Client;

public class RunelitePlayerDialogStep extends RuneliteDialogStep
{
	public RunelitePlayerDialogStep(Client client, String text, FaceAnimationIDs animation)
	{
		super(client.getLocalPlayer().getName(), text, -1, animation.getAnimationID());
		client.getLocalPlayer().getName();
	}

	public RunelitePlayerDialogStep(Client client, String text, int animation)
	{
		super(client.getLocalPlayer().getName(), text, -1, animation);
		client.getLocalPlayer().getName();
	}

	public RunelitePlayerDialogStep(Client client, String text)
	{
		this(client, text, 570);
	}

	public RunelitePlayerDialogStep(Client client, String text, RuneliteConfigSetter setter)
	{
		this(client, text, 570);
		this.setStateProgression(setter);
	}

	@Override
	public boolean isPlayer()
	{
		return true;
	}
}

