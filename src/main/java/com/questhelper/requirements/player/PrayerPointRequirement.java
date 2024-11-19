package com.questhelper.requirements.player;

import com.questhelper.requirements.AbstractRequirement;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

import javax.annotation.Nonnull;

public class PrayerPointRequirement extends AbstractRequirement
{
	private final int level;
	public PrayerPointRequirement(int level)
	{
		this.level = level;
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		setState(client.getBoostedSkillLevel(Skill.PRAYER) >= level);
	}

	@Nonnull
	@Override
	public String getDisplayText()
	{
		return level + " prayer points";
	}
}
