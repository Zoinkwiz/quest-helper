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

import com.questhelper.statemanagement.boats.BoatSlotState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.gameval.VarbitID;
import java.util.function.Function;

@AllArgsConstructor
@Getter
public enum BoatResistanceType
{
	KELP("kelp", VarbitID.SAILING_SIDEPANEL_BOAT_TANGLEDKELP_RESISTANT, boat -> boat.hasKelpResistance() ? 1 : 0, false),
	ICE("ice", VarbitID.SAILING_SIDEPANEL_BOAT_ICYSEAS_RESISTANT, boat -> boat.hasIceResistance() ? 1 : 0, false),
	CRYSTAL("crystal", VarbitID.SAILING_SIDEPANEL_BOAT_CRYSTALFLECKED_RESISTANT, boat -> boat.hasCrystalResistance() ? 1 : 0, false),
	FETID_WATER("fetid water", VarbitID.SAILING_SIDEPANEL_BOAT_FETIDWATER_RESISTANT, boat -> boat.hasFetidWaterResistance() ? 1 : 0, false),
	RAPID("rapid", VarbitID.SAILING_SIDEPANEL_BOAT_RAPIDRESISTANCE, BoatSlotState::getRapidResistanceLevel, true),
	STORM("storm", VarbitID.SAILING_SIDEPANEL_BOAT_STORMRESISTANCE, BoatSlotState::getStormResistanceLevel, true);

	private final String displayName;
	private final int varbitId;
	private final Function<BoatSlotState, Integer> levelGetter;
	private final boolean showLevel;

	public int getLevel(BoatSlotState boat)
	{
		return levelGetter.apply(boat);
	}

	public int getActiveBoatLevel(Client client)
	{
		return client.getVarbitValue(varbitId);
	}
}

