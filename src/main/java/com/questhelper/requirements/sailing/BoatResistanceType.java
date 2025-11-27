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

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.gameval.VarbitID;

@AllArgsConstructor
@Getter
public enum BoatResistanceType
{
	KELP("kelp", VarbitID.SAILING_SIDEPANEL_BOAT_TANGLEDKELP_RESISTANT, false),
	ICE("ice", VarbitID.SAILING_SIDEPANEL_BOAT_ICYSEAS_RESISTANT, false),
	CRYSTAL("crystal", VarbitID.SAILING_SIDEPANEL_BOAT_CRYSTALFLECKED_RESISTANT, false),
	FETID_WATER("fetid water", VarbitID.SAILING_SIDEPANEL_BOAT_FETIDWATER_RESISTANT, false),
	RAPID("rapid", VarbitID.SAILING_SIDEPANEL_BOAT_RAPIDRESISTANCE, true),
	STORM("storm", VarbitID.SAILING_SIDEPANEL_BOAT_STORMRESISTANCE, true);

	private final String displayName;
	private final int varbitId;
	private final boolean showLevel;

	public int getActiveBoatLevel(Client client)
	{
		if (client == null)
		{
			return 0;
		}
		return client.getVarbitValue(varbitId);
	}

	public int getBoatSlotLevel(Client client, int boatSlot)
	{
		if (client == null || boatSlot < 1 || boatSlot > 5)
		{
			return 0;
		}

		switch (this)
		{
			case KELP:
				return getKelpResistance(client, boatSlot);
			case ICE:
				return getIceResistance(client, boatSlot);
			case CRYSTAL:
				return getCrystalResistance(client, boatSlot);
			case FETID_WATER:
				return getFetidWaterResistance(client, boatSlot);
			case RAPID:
				return getRapidResistance(client, boatSlot);
			case STORM:
				return getStormResistance(client, boatSlot);
			default:
				return 0;
		}
	}

	private int getKelpResistance(Client client, int boatSlot)
	{
		int steering = getSteering(client, boatSlot);
		return steering >= 4 ? 1 : 0;
	}

	private int getIceResistance(Client client, int boatSlot)
	{
		int brazier = getBrazier(client, boatSlot);
		return brazier > 0 ? 1 : 0;
	}

	private int getCrystalResistance(Client client, int boatSlot)
	{
		int keel = getKeel(client, boatSlot);
		return keel >= 4 ? 1 : 0;
	}

	private int getFetidWaterResistance(Client client, int boatSlot)
	{
		int boatType = getType(client, boatSlot);
		if (boatType == 0)
		{
			return 0;
		}
		else if (boatType == 1)
		{
			int hotspot5 = getHotspot5(client, boatSlot);
			return hotspot5 == 1 ? 1 : 0;
		}
		else if (boatType == 2)
		{
			int hotspot9 = getHotspot9(client, boatSlot);
			return hotspot9 == 1 ? 1 : 0;
		}
		return 0;
	}

	private int getRapidResistance(Client client, int boatSlot)
	{
		int sail = getSail(client, boatSlot);
		if (sail == 0)
		{
			return 0;
		}
		else if (sail <= 2)
		{
			return 1;
		}
		else if (sail <= 6)
		{
			return 2;
		}
		return 0;
	}

	private int getStormResistance(Client client, int boatSlot)
	{
		int hull = getHull(client, boatSlot);
		if (hull == 0)
		{
			return 0;
		}
		else if (hull <= 3)
		{
			return 1;
		}
		else if (hull <= 6)
		{
			return 2;
		}
		return 0;
	}

	private int getSteering(Client client, int boatSlot)
	{
		switch (boatSlot)
		{
			case 1: return client.getVarbitValue(VarbitID.SAILING_BOAT_1_STEERING);
			case 2: return client.getVarbitValue(VarbitID.SAILING_BOAT_2_STEERING);
			case 3: return client.getVarbitValue(VarbitID.SAILING_BOAT_3_STEERING);
			case 4: return client.getVarbitValue(VarbitID.SAILING_BOAT_4_STEERING);
			case 5: return client.getVarbitValue(VarbitID.SAILING_BOAT_5_STEERING);
			default: return 0;
		}
	}

	private int getBrazier(Client client, int boatSlot)
	{
		switch (boatSlot)
		{
			case 1: return client.getVarbitValue(VarbitID.SAILING_BOAT_1_BRAZIER);
			case 2: return client.getVarbitValue(VarbitID.SAILING_BOAT_2_BRAZIER);
			case 3: return client.getVarbitValue(VarbitID.SAILING_BOAT_3_BRAZIER);
			case 4: return client.getVarbitValue(VarbitID.SAILING_BOAT_4_BRAZIER);
			case 5: return client.getVarbitValue(VarbitID.SAILING_BOAT_5_BRAZIER);
			default: return 0;
		}
	}

	private int getHull(Client client, int boatSlot)
	{
		switch (boatSlot)
		{
			case 1: return client.getVarbitValue(VarbitID.SAILING_BOAT_1_HULL);
			case 2: return client.getVarbitValue(VarbitID.SAILING_BOAT_2_HULL);
			case 3: return client.getVarbitValue(VarbitID.SAILING_BOAT_3_HULL);
			case 4: return client.getVarbitValue(VarbitID.SAILING_BOAT_4_HULL);
			case 5: return client.getVarbitValue(VarbitID.SAILING_BOAT_5_HULL);
			default: return 0;
		}
	}

	private int getKeel(Client client, int boatSlot)
	{
		switch (boatSlot)
		{
			case 1: return client.getVarbitValue(VarbitID.SAILING_BOAT_1_KEEL);
			case 2: return client.getVarbitValue(VarbitID.SAILING_BOAT_2_KEEL);
			case 3: return client.getVarbitValue(VarbitID.SAILING_BOAT_3_KEEL);
			case 4: return client.getVarbitValue(VarbitID.SAILING_BOAT_4_KEEL);
			case 5: return client.getVarbitValue(VarbitID.SAILING_BOAT_5_KEEL);
			default: return 0;
		}
	}

	private int getSail(Client client, int boatSlot)
	{
		switch (boatSlot)
		{
			case 1: return client.getVarbitValue(VarbitID.SAILING_BOAT_1_SAIL);
			case 2: return client.getVarbitValue(VarbitID.SAILING_BOAT_2_SAIL);
			case 3: return client.getVarbitValue(VarbitID.SAILING_BOAT_3_SAIL);
			case 4: return client.getVarbitValue(VarbitID.SAILING_BOAT_4_SAIL);
			case 5: return client.getVarbitValue(VarbitID.SAILING_BOAT_5_SAIL);
			default: return 0;
		}
	}

	private int getType(Client client, int boatSlot)
	{
		switch (boatSlot)
		{
			case 1: return client.getVarbitValue(VarbitID.SAILING_BOAT_1_TYPE);
			case 2: return client.getVarbitValue(VarbitID.SAILING_BOAT_2_TYPE);
			case 3: return client.getVarbitValue(VarbitID.SAILING_BOAT_3_TYPE);
			case 4: return client.getVarbitValue(VarbitID.SAILING_BOAT_4_TYPE);
			case 5: return client.getVarbitValue(VarbitID.SAILING_BOAT_5_TYPE);
			default: return 0;
		}
	}

	private int getHotspot5(Client client, int boatSlot)
	{
		switch (boatSlot)
		{
			case 1: return client.getVarbitValue(VarbitID.SAILING_BOAT_1_HOTSPOT_5);
			case 2: return client.getVarbitValue(VarbitID.SAILING_BOAT_2_HOTSPOT_5);
			case 3: return client.getVarbitValue(VarbitID.SAILING_BOAT_3_HOTSPOT_5);
			case 4: return client.getVarbitValue(VarbitID.SAILING_BOAT_4_HOTSPOT_5);
			case 5: return client.getVarbitValue(VarbitID.SAILING_BOAT_5_HOTSPOT_5);
			default: return 0;
		}
	}

	private int getHotspot9(Client client, int boatSlot)
	{
		switch (boatSlot)
		{
			case 1: return client.getVarbitValue(VarbitID.SAILING_BOAT_1_HOTSPOT_9);
			case 2: return client.getVarbitValue(VarbitID.SAILING_BOAT_2_HOTSPOT_9);
			case 3: return client.getVarbitValue(VarbitID.SAILING_BOAT_3_HOTSPOT_9);
			case 4: return client.getVarbitValue(VarbitID.SAILING_BOAT_4_HOTSPOT_9);
			case 5: return client.getVarbitValue(VarbitID.SAILING_BOAT_5_HOTSPOT_9);
			default: return 0;
		}
	}
}

