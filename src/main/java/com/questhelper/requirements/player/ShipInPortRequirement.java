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
import com.questhelper.requirements.util.Port;
import net.runelite.api.Client;

import javax.annotation.Nonnull;

import lombok.Getter;
import net.runelite.api.gameval.VarbitID;
import java.util.stream.IntStream;

/**
 * Requirement that checks if a player has a ship at the requested port.
 */
@Getter
public class ShipInPortRequirement extends AbstractRequirement
{
	private final Port port;
	private final boolean strictOnRecentShip;

	private int[] allDocks;
	private int lastDock;

	/**
	 * Checks if the player has a ship docked at the requested port.
	 *
	 * @param port the id of the port
	 * @param strictOnRecentShip if true, only checks is most recently used boat is at a port, if false checks if any boat is at a port.
	 */
	public ShipInPortRequirement(Port port, boolean strictOnRecentShip)
	{
		assert(port != null);
		this.port = port;
		this.strictOnRecentShip = strictOnRecentShip;
	}
	/**
	 * Checks if the player's most-recently used ship is at the requested port.
	 *
	 * @param port the id of the port
	 */
	public ShipInPortRequirement(Port port)
	{
		this(port, true);
	}

	@Override
	public boolean check(Client client)
	{
		lastDock = client.getVarbitValue(VarbitID.SAILING_BOARDED_BOAT_LAST_DOCK);
		allDocks = new int[]{
			client.getVarbitValue(VarbitID.SAILING_BOAT_1_PORT),
			client.getVarbitValue(VarbitID.SAILING_BOAT_2_PORT),
			client.getVarbitValue(VarbitID.SAILING_BOAT_3_PORT),
			client.getVarbitValue(VarbitID.SAILING_BOAT_4_PORT),
			client.getVarbitValue(VarbitID.SAILING_BOAT_5_PORT)
		};

		if(strictOnRecentShip)
		{
			return lastDock == port.getId();
		}else
		{
			return anyDocked();
		}
	}

	private boolean anyDocked(){
		return IntStream.of(allDocks).anyMatch(x -> x == port.getId());
	}

	@Nonnull
	@Override
	public String getDisplayText()
	{
		return "A ship docked at " + this.port.getName();
	}

	public String getTooltip()
	{
		if(lastDock != port.getId()){
			if(anyDocked()){
				return "You have other ships docked at this port!";
			}
		}
		return null;
	}
}
