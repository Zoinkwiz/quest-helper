/*
 *
 *  * Copyright (c) 2025, TTvanWillegen <https://github.com/TTvanWillegen>
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice, this
 *  *    list of conditions and the following disclaimer.
 *  * 2. Redistributions in binary form must reproduce the above copyright notice,
 *  *    this list of conditions and the following disclaimer in the documentation
 *  *    and/or other materials provided with the distribution.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.questhelper.requirements.player;

import com.questhelper.requirements.AbstractRequirement;
import net.runelite.api.Client;

import javax.annotation.Nonnull;

import lombok.Getter;
import net.runelite.api.gameval.VarbitID;

/**
 * Requirement that checks if a player has a required number of Port Task slots free.
 */
public class FreePortTaskSlotsRequirement extends AbstractRequirement
{
	@Getter
	private final int numSlotsFree;

	/**
	 * Checks if the player has a required number of slots free.
	 *
	 * @param numSlotsFree the required number of slots free
	 */
	public FreePortTaskSlotsRequirement(int numSlotsFree)
	{
		this.numSlotsFree = numSlotsFree;
	}

	@Override
	public boolean check(Client client)
	{
		int freeSlots = client.getVarbitValue(VarbitID.PORT_TASK_SLOT_0_ID) == 0 ? 1 : 0;
		int extraSlotsUnlocked = client.getVarbitValue(VarbitID.PORT_TASK_EXTRA_SLOTS_UNLOCKED);
		if (extraSlotsUnlocked >= 1)
		{
			freeSlots += client.getVarbitValue(VarbitID.PORT_TASK_SLOT_1_ID) == 0 ? 1 : 0;
		}
		if (extraSlotsUnlocked >= 2)
		{
			freeSlots += client.getVarbitValue(VarbitID.PORT_TASK_SLOT_2_ID) == 0 ? 1 : 0;
		}
		if (extraSlotsUnlocked >= 3)
		{
			freeSlots += client.getVarbitValue(VarbitID.PORT_TASK_SLOT_3_ID) == 0 ? 1 : 0;
		}
		if (extraSlotsUnlocked >= 4)
		{
			freeSlots += client.getVarbitValue(VarbitID.PORT_TASK_SLOT_4_ID) == 0 ? 1 : 0;
		}
		return freeSlots >= numSlotsFree;
	}

	@Nonnull
	@Override
	public String getDisplayText()
	{
		return getNumSlotsFree() + " free Sailing Port task slot" + (getNumSlotsFree() == 1 ? "" : "s");
	}
}
