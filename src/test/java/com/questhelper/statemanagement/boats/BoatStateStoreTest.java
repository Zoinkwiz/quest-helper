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

import com.google.gson.Gson;
import net.runelite.client.config.ConfigManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BoatStateStoreTest
{
	private static final String PROFILE = "rs-profile";

	private ConfigManager configManager;
	private Map<String, String> backingStore;
	private BoatStateStore boatStateStore;

	@BeforeEach
	void setup()
	{
		configManager = mock(ConfigManager.class);
		backingStore = new HashMap<>();

		when(configManager.getRSProfileKey()).thenReturn(PROFILE);
		when(configManager.getRSProfileConfiguration(anyString(), anyString()))
			.then(invocation -> backingStore.get(composeKey(PROFILE, invocation.getArgument(0), invocation.getArgument(1))));

		doAnswer(invocation -> {
			String group = invocation.getArgument(0);
			String profile = invocation.getArgument(1);
			String key = invocation.getArgument(2);
			String value = invocation.getArgument(3);
			backingStore.put(composeKey(profile, group, key), value);
			return null;
		}).when(configManager).setConfiguration(anyString(), anyString(), anyString(), anyString());

		boatStateStore = new BoatStateStore(configManager, new Gson());
		boatStateStore.initialize();
	}

	@Test
	void hasBoatMatchingUsesStoredPredicate()
	{
		boatStateStore.updateBoat(BoatSlotState.builder().boatId(42).iceResistant(true).build());
		assertTrue(boatStateStore.hasBoatMatching(BoatSlotState::hasIceResistance));
		assertFalse(boatStateStore.hasBoatMatching(BoatSlotState::hasCrystalResistance));
	}

	@Test
	void statePersistsAcrossInstances()
	{
		boatStateStore.updateBoat(BoatSlotState.builder().boatId(77)
			.rapidResistanceLevel(2)
			.build());

		BoatStateStore rehydrated = new BoatStateStore(configManager, new Gson());
		rehydrated.initialize();

		assertTrue(rehydrated.getBoat(77).isPresent());
		assertTrue(rehydrated.hasBoatMatching(state -> state.hasRapidResistanceAtLeast(1)));
	}

	private String composeKey(String profile, String group, String key)
	{
		return profile + ":" + group + ":" + key;
	}
}

