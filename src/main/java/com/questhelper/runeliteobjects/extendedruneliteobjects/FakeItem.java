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
package com.questhelper.runeliteobjects.extendedruneliteobjects;

import com.questhelper.runeliteobjects.RuneliteConfigSetter;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.AnimationID;
import net.runelite.client.callback.ClientThread;

public class FakeItem extends ExtendedRuneliteObject
{
	protected FakeItem(Client client, ClientThread clientThread, WorldPoint worldPoint, int[] model, int animation)
	{
		super(client, clientThread, worldPoint, model, animation);
		objectType = RuneliteObjectTypes.ITEM;
		nameColor = "FFA07A";
	}

	public void addTakeAction(RuneliteObjectManager runeliteObjectManager, RuneliteConfigSetter stateChange, String actionText)
	{
		setReplaceWalkActionText("Pick");
		setReplaceWalkAction(menuEntry -> {
			// Bend down and pick up the item
			setPendingAction(() -> {
				// Kinda needs to be a 'last interacted object'
				Player player = client.getLocalPlayer();
				// TODO: Won't work in instances?
				if (player.getWorldLocation().distanceTo(getWorldPoint()) <= 1)
				{
					runeliteObjectManager.createChatboxMessage(actionText);
					player.setAnimation(AnimationID.HUMAN_PICKUPFLOOR);
					player.setAnimationFrame(0);

					// Set variable
					stateChange.setConfigValue();
					this.activate();

					return true;
				}
				return false;
			});
		});

	}
}
