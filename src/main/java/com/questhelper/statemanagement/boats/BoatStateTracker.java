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
package com.questhelper.statemanagement.boats;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;

@Singleton
public class BoatStateTracker
{
	private static final int PLAYER_ROLE_CAPTAIN = 10;
	private static final Set<Integer> WATCHED_VARBITS = Set.of(
		VarbitID.SAILING_BOARDED_BOAT,
		VarbitID.SAILING_SIDEPANEL_PLAYER_ROLE,
		VarbitID.SAILING_BOAT_SPAWNED,
		VarbitID.SAILING_SIDEPANEL_BOAT_TANGLEDKELP_RESISTANT,
		VarbitID.SAILING_SIDEPANEL_BOAT_FETIDWATER_RESISTANT,
		VarbitID.SAILING_SIDEPANEL_BOAT_CRYSTALFLECKED_RESISTANT,
		VarbitID.SAILING_SIDEPANEL_BOAT_ICYSEAS_RESISTANT,
		VarbitID.SAILING_SIDEPANEL_BOAT_RAPIDRESISTANCE,
		VarbitID.SAILING_SIDEPANEL_BOAT_STORMRESISTANCE
	);

	private final Client client;
	private final ClientThread clientThread;
	private final BoatStateStore boatStateStore;

	@Inject
	public BoatStateTracker(Client client, ClientThread clientThread, BoatStateStore boatStateStore)
	{
		this.client = client;
		this.clientThread = clientThread;
		this.boatStateStore = boatStateStore;
	}

	public void startUp(EventBus eventBus)
	{
		eventBus.register(this);
	}

	public void shutDown(EventBus eventBus)
	{
		eventBus.unregister(this);
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (!WATCHED_VARBITS.contains(event.getVarbitId()))
		{
			return;
		}

		clientThread.invokeLater(this::captureBoatState);
	}

	private void captureBoatState()
	{
		if (client == null || client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if (!isPlayerOnOwnBoat())
		{
			return;
		}

		int boatId = client.getVarbitValue(VarbitID.SAILING_BOAT_SPAWNED);
		if (boatId <= 0)
		{
			return;
		}

		BoatSlotState slotState = BoatSlotState.builder()
			.boatId(boatId)
			.kelpResistant(client.getVarbitValue(VarbitID.SAILING_SIDEPANEL_BOAT_TANGLEDKELP_RESISTANT) == 1)
			.iceResistant(client.getVarbitValue(VarbitID.SAILING_SIDEPANEL_BOAT_ICYSEAS_RESISTANT) == 1)
			.crystalResistant(client.getVarbitValue(VarbitID.SAILING_SIDEPANEL_BOAT_CRYSTALFLECKED_RESISTANT) == 1)
			.fetidWaterResistant(client.getVarbitValue(VarbitID.SAILING_SIDEPANEL_BOAT_FETIDWATER_RESISTANT) == 1)
			.rapidResistanceLevel(client.getVarbitValue(VarbitID.SAILING_SIDEPANEL_BOAT_RAPIDRESISTANCE))
			.stormResistanceLevel(client.getVarbitValue(VarbitID.SAILING_SIDEPANEL_BOAT_STORMRESISTANCE))
			.build();

		boatStateStore.updateBoat(slotState);
	}

	private boolean isPlayerOnOwnBoat()
	{
		return client.getVarbitValue(VarbitID.SAILING_BOARDED_BOAT) == 1
			&& client.getVarbitValue(VarbitID.SAILING_SIDEPANEL_PLAYER_ROLE) == PLAYER_ROLE_CAPTAIN;
	}
}

