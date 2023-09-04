/*
 * Copyright (c) 2018 Abex
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.helpers.mischelpers.farmruns;

import java.time.Instant;
import javax.annotation.Nullable;
import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.timetracking.TimeTrackingConfig;
import net.runelite.client.plugins.timetracking.farming.CropState;
import net.runelite.client.plugins.timetracking.farming.Produce;

public class FarmingHandler
{
	private final Client client;
	private final ConfigManager configManager;

	public FarmingHandler(Client client, ConfigManager configManager)
	{
		this.client = client;
		this.configManager = configManager;
	}

	public CropState predictPatch(FarmingPatch patch)
	{
		return predictPatch(patch, configManager.getRSProfileKey());
	}

	@Nullable
	public CropState predictPatch(FarmingPatch patch, String profile)
	{
		long unixNow = Instant.now().getEpochSecond();

		String key = patch.configKey();
		String storedValue = configManager.getConfiguration("timetracking", profile, key);

		if (storedValue == null)
		{
			return null;
		}

		long unixTime = 0;
		int value = 0;
		{
			String[] parts = storedValue.split(":");
			if (parts.length == 2)
			{
				try
				{
					value = Integer.parseInt(parts[0]);
					unixTime = Long.parseLong(parts[1]);
				}
				catch (NumberFormatException ignored)
				{
				}
			}
		}

		PatchState state = patch.getImplementation().forVarbitValue(value);
		if (state == null) return null;
		if (state.getCropState() == CropState.EMPTY) return CropState.EMPTY;
		if (state.getProduce() == Produce.WEEDS) return CropState.EMPTY;
		if (state.getCropState() == CropState.DEAD) return CropState.DEAD;

		if (unixTime <= 0)
		{
			return null;
		}

		int stage = state.getStage();
		int stages = state.getStages();
		int tickrate = state.getTickRate();

		boolean botanist = client.getVarbitValue(Varbits.LEAGUE_RELIC_5) == 1;

		if (botanist)
		{
			tickrate /= 5;
		}

		long doneEstimate = 0;
		if (tickrate > 0)
		{
			long tickTime = getTickTime(tickrate, 0, unixTime, profile);
			doneEstimate = getTickTime(tickrate, stages - 1 - stage, tickTime, profile);
		}

		if (unixNow >= doneEstimate) return CropState.HARVESTABLE;

		return CropState.GROWING;
	}

	public long getTickTime(int tickRate, int ticks, long requestedTime, String profile)
	{
		Integer offsetPrecisionMins = configManager.getConfiguration(TimeTrackingConfig.CONFIG_GROUP, profile, TimeTrackingConfig.FARM_TICK_OFFSET_PRECISION, int.class);
		Integer offsetTimeMins = configManager.getConfiguration(TimeTrackingConfig.CONFIG_GROUP, profile, TimeTrackingConfig.FARM_TICK_OFFSET, int.class);

		//All offsets are negative but are stored as positive
		long calculatedOffsetTime = 0L;
		if (offsetPrecisionMins != null && offsetTimeMins != null && (offsetPrecisionMins >= tickRate || offsetPrecisionMins >= 40))
		{
			calculatedOffsetTime = (offsetTimeMins % tickRate) * 60L;
		}

		//Calculate "now" as +offset seconds in the future so we calculate the correct ticks
		long unixNow = requestedTime + calculatedOffsetTime;

		//The time that the tick requested will happen
		long timeOfCurrentTick = (unixNow - (unixNow % (tickRate * 60L)));
		long timeOfGoalTick = timeOfCurrentTick + ((long) ticks * tickRate * 60);

		//Move ourselves back to real time
		return timeOfGoalTick - calculatedOffsetTime;
	}
}
