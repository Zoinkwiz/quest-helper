package com.questhelper.requirements;

import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.Experience;
import net.runelite.api.Player;
import net.runelite.api.Skill;

@RequiredArgsConstructor
public class CombatLevelRequirement extends Requirement
{
	private final int level;

	@Override
	public boolean check(Client client)
	{
		Player player = client.getLocalPlayer();
		return player != null && player.getCombatLevel() >= level;
	}

	@Override
	public String getDisplayText()
	{
		return "Combat Level " + level;
	}
}
