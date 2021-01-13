package com.questhelper.requirements;

import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.Experience;
import net.runelite.api.Skill;

@RequiredArgsConstructor
public class CombatLevelRequirement extends Requirement
{
	private final int level;

	@Override
	public boolean check(Client client)
	{
		int combatLevel = Experience.getCombatLevel(
			client.getRealSkillLevel(Skill.ATTACK),
			client.getRealSkillLevel(Skill.STRENGTH),
			client.getRealSkillLevel(Skill.DEFENCE),
			client.getRealSkillLevel(Skill.HITPOINTS),
			client.getRealSkillLevel(Skill.MAGIC),
			client.getRealSkillLevel(Skill.RANGED),
			client.getRealSkillLevel(Skill.PRAYER));
		return combatLevel >= level;
	}

	@Override
	public String getDisplayText()
	{
		return "Combat Level " + level;
	}
}
