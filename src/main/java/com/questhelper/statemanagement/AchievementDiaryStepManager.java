/*
 * Copyright (c) 2023, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.statemanagement;

import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.runelite.RuneliteRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.NpcID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;

import javax.inject.Singleton;

@Singleton
public class AchievementDiaryStepManager
{
	@Getter
	static Zone workshop;

	@Getter
	static Requirement inWorkshop;

	@Getter
	static RuneliteRequirement killedFire, killedEarth, killedWater, killedAir;

	public static void setup(ConfigManager configManager)
	{
		setupKandarin(configManager);
	}

	private static void setupKandarin(ConfigManager configManager)
	{
		workshop = new Zone(new WorldPoint(2682, 9862, 0), new WorldPoint(2747, 9927, 0));
		inWorkshop = new ZoneRequirement(workshop);
		killedFire = new RuneliteRequirement(configManager, "kandarin-easy-killed-fire", "true");
		killedEarth = new RuneliteRequirement(configManager, "kandarin-easy-killed-earth", "true");
		killedAir = new RuneliteRequirement(configManager, "kandarin-easy-killed-air", "true");
		killedWater = new RuneliteRequirement(configManager, "kandarin-easy-killed-water", "true");
	}

	public static void check(Client client)
	{
		if (!inWorkshop.check(client))
		{
			killedFire.setConfigValue("false");
			killedEarth.setConfigValue("false");
			killedWater.setConfigValue("false");
			killedAir.setConfigValue("false");
		}
	}

	@Subscribe
	public void onNpcLootReceived(final NpcLootReceived npcLootReceived)
	{
		final NPC npc = npcLootReceived.getNpc();

		final int id = npc.getId();
		if (id == NpcID.ELEMENTAL_FIRE) killedFire.setConfigValue("true");
		if (id == NpcID.ELEMENTAL_EARTH) killedEarth.setConfigValue("true");
		if (id == NpcID.ELEMENTAL_WATER) killedWater.setConfigValue("true");
		if (id == NpcID.ELEMENTAL_AIR) killedAir.setConfigValue("true");
	}
}
