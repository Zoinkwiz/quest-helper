/*
 *
 *  * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
import com.questhelper.requirements.util.Operation;
import net.runelite.api.Client;
import net.runelite.api.Player;

/**
 * Checks if the player's combat level meets the required level
 */
public class CombatLevelRequirement extends AbstractRequirement
{
	private final int requiredLevel;
	private final Operation operation;

	/**
	 * Checks if the player's combat level meets the required level using the given
	 * {@link Operation}
	 *
	 * @param operation the {@link Operation} to use
	 * @param requiredLevel the required combat level
	 */
	public CombatLevelRequirement(Operation operation, int requiredLevel)
	{
		this.operation = operation;
		this.requiredLevel = requiredLevel;
		shouldCountForFilter = true;
	}

	/**
	 * Check if the player has the required combat level using {@link Operation#GREATER_EQUAL}
	 *
	 * @param requiredLevel the required combat level
	 */
	public CombatLevelRequirement(int requiredLevel)
	{
		this(Operation.GREATER_EQUAL, requiredLevel);
	}

	@Override
	public boolean check(Client client)
	{
		Player player = client.getLocalPlayer();
		return player != null && operation.check(player.getCombatLevel(), requiredLevel);
	}

	@Override
	public String getDisplayText()
	{
		return "Combat Level " + requiredLevel;
	}
}
