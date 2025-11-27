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
import com.google.gson.JsonSyntaxException;
import com.questhelper.QuestHelperConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
@Singleton
public class BoatStateStore
{
	private static final String CONFIG_GROUP = QuestHelperConfig.QUEST_HELPER_GROUP;
	private static final String CONFIG_KEY = "boats";

	@Getter
	public static BoatStateStore instance;

	private final ConfigManager configManager;
	private final Gson gson;

	private final Map<Integer, BoatSlotState> boatsById = new LinkedHashMap<>();

	@Getter
	private String rsProfileKey;

	@Inject
	public BoatStateStore(ConfigManager configManager, Gson gson)
	{
		this.configManager = configManager;
		this.gson = gson;
		instance = this;
	}

	public synchronized void initialize()
	{
		ensureProfileLoaded();
	}

	public synchronized List<BoatSlotState> getKnownBoats()
	{
		return Collections.unmodifiableList(new ArrayList<>(boatsById.values()));
	}

	public synchronized Optional<BoatSlotState> getBoat(int boatId)
	{
		return Optional.ofNullable(boatsById.get(boatId));
	}

	public synchronized boolean hasBoatMatching(Predicate<BoatSlotState> predicate)
	{
		return boatsById.values().stream().anyMatch(predicate);
	}

	public synchronized void updateBoat(BoatSlotState newState)
	{
		if (newState == null || newState.getBoatId() <= 0)
		{
			return;
		}

		ensureProfileLoaded();
		BoatSlotState normalized = normalizeState(newState);

		BoatSlotState existing = boatsById.get(normalized.getBoatId());
		if (Objects.equals(existing, normalized))
		{
			return;
		}

		boatsById.remove(normalized.getBoatId());
		boatsById.put(normalized.getBoatId(), normalized);
		saveToConfig();
	}

	public synchronized void handleProfileChanged()
	{
		saveToConfig();
		boatsById.clear();
		rsProfileKey = null;
		ensureProfileLoaded();
	}

	public synchronized void clearCache()
	{
		boatsById.clear();
		rsProfileKey = null;
	}

	private void ensureProfileLoaded()
	{
		String currentProfileKey = configManager.getRSProfileKey();
		if (currentProfileKey == null || currentProfileKey.isEmpty())
		{
			return;
		}

		if (Objects.equals(rsProfileKey, currentProfileKey))
		{
			return;
		}

		saveToConfig();
		rsProfileKey = currentProfileKey;
		loadFromConfig();
	}

	private void loadFromConfig()
	{
		boatsById.clear();
		String json = configManager.getRSProfileConfiguration(CONFIG_GROUP, CONFIG_KEY);
		if (json == null || json.isEmpty())
		{
			return;
		}

		try
		{
			BoatSlotState[] saved = gson.fromJson(json, BoatSlotState[].class);
			if (saved == null)
			{
				return;
			}

			for (BoatSlotState state : saved)
			{
				if (state != null && state.getBoatId() > 0)
				{
					boatsById.put(state.getBoatId(), normalizeState(state));
				}
			}
		}
		catch (JsonSyntaxException ex)
		{
			log.warn("Failed to parse saved boat data", ex);
		}
	}

	private void saveToConfig()
	{
		if (rsProfileKey == null || rsProfileKey.isEmpty())
		{
			return;
		}

		String json = gson.toJson(boatsById.values());
		configManager.setConfiguration(CONFIG_GROUP, rsProfileKey, CONFIG_KEY, json);
	}

	private BoatSlotState normalizeState(BoatSlotState state)
	{
		if (state == null)
		{
			return null;
		}

		int rapid = Math.max(0, state.getRapidResistanceLevel());
		int storm = Math.max(0, state.getStormResistanceLevel());

		return BoatSlotState.builder()
			.boatId(state.getBoatId())
			.kelpResistant(state.hasKelpResistance())
			.iceResistant(state.hasIceResistance())
			.crystalResistant(state.hasCrystalResistance())
			.fetidWaterResistant(state.hasFetidWaterResistance())
			.rapidResistanceLevel(rapid)
			.stormResistanceLevel(storm)
			.build();
	}
}

