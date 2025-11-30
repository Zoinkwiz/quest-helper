/*
 * Copyright (c) 2025, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.requirements.sailing;

import com.questhelper.requirements.AbstractRequirement;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.gameval.VarbitID;

import javax.annotation.Nonnull;
import java.util.Objects;

public class HasBoatResistanceRequirement extends AbstractRequirement
{
	private static final int MAX_BOATS = 5;

	private final BoatResistanceType resistanceType;
	private final boolean checkCurrentShip;
	private final int minimumLevel;
	private final String displayText;

	public HasBoatResistanceRequirement(BoatResistanceType resistanceType, boolean checkCurrentShip, int minimumLevel)
	{
		this.resistanceType = Objects.requireNonNull(resistanceType);
		this.checkCurrentShip = checkCurrentShip;
		this.minimumLevel = minimumLevel;
		this.shouldCountForFilter = true;
		this.displayText = buildDisplayText();
	}

	private String buildDisplayText()
	{
		String shipType = checkCurrentShip ? "Current ship" : "Any ship";
		String levelText = resistanceType.isShowLevel()
			? " (level " + minimumLevel + "+)"
			: "";
		return shipType + " with " + resistanceType.getDisplayName() + " resistance" + levelText;
	}

	@Override
	public boolean check(Client client)
	{
		if (client == null || client.getGameState() != GameState.LOGGED_IN)
		{
			return false;
		}

		if (checkCurrentShip)
		{
			return checkCurrentShipFromVarbits(client);
		}
		else
		{
			return checkAnyShipFromVarbits(client);
		}
	}

	private boolean checkCurrentShipFromVarbits(Client client)
	{
		// Check if player is on a boat
		if (client.getVarbitValue(VarbitID.SAILING_BOARDED_BOAT) != 1)
		{
			return false;
		}

		int currentLevel = resistanceType.getActiveBoatLevel(client);
		return currentLevel >= minimumLevel;
	}

	private boolean checkAnyShipFromVarbits(Client client)
	{
		for (int boatSlot = 1; boatSlot <= MAX_BOATS; boatSlot++)
		{
			int level = resistanceType.getBoatSlotLevel(client, boatSlot);
			if (level >= minimumLevel)
			{
				return true;
			}
		}
		return false;
	}

	@Nonnull
	@Override
	public String getDisplayText()
	{
		return displayText;
	}
}

