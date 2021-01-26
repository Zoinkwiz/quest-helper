/*
 * Copyright (c) 2020, Zoinkwiz
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
package com.questhelper.quests.dreammentor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Skill;

enum CyrisusArmourSet
{
	MELEE(0,  Arrays.asList(CyrisusBankItem.DRAGON_MED_HELM, CyrisusBankItem.AHRIM_ROBETOP,
		CyrisusBankItem.AHRIM_SKIRT, CyrisusBankItem.RANGER_BOOTS, CyrisusBankItem.ABYSSAL_WHIP)),
	RANGED(1, Arrays.asList(CyrisusBankItem.SPLITBARK_HELM, CyrisusBankItem.KARILS_TOP,
		CyrisusBankItem.TORAG_LEG, CyrisusBankItem.ADAMANT_BOOTS, CyrisusBankItem.MAGIC_SHORTBOW)),
	MAGIC(2, Arrays.asList(CyrisusBankItem.ROBIN_HOOD, CyrisusBankItem.DRAGON_CHAINBODY,
		CyrisusBankItem.BLACK_CHAPS, CyrisusBankItem.INFINITY_BOOTS, CyrisusBankItem.ANCIENT_STAFF));

	@Getter
	private final int combatType;

	@Getter
	private final List<CyrisusBankItem> items;

	CyrisusArmourSet(int combatType, List<CyrisusBankItem> items)
	{
		this.combatType = combatType;
		this.items = items;
	}

	public static CyrisusArmourSet getCorrectSet(Client client)
	{
		float meleeCombatLevel = client.getRealSkillLevel(Skill.ATTACK) + client.getRealSkillLevel(Skill.STRENGTH);
		float rangedCombatLevel = client.getRealSkillLevel(Skill.RANGED) * (3f/2f);
		float magicCombatLevel = client.getRealSkillLevel(Skill.MAGIC) * (3f/2f);

		if (meleeCombatLevel >= rangedCombatLevel)
		{
			if (meleeCombatLevel >= magicCombatLevel)
			{
				return MELEE;
			}
			else
			{
				return MAGIC;
			}
		}
		else if (rangedCombatLevel > magicCombatLevel)
		{
			return RANGED;
		}
		else
		{
			return MAGIC;
		}
	}

	public static boolean isReady(Client client)
	{
		CyrisusArmourSet armourSet = getCorrectSet(client);
		int currentHelmet = client.getVarbitValue(3627);
		int currentBody = client.getVarbitValue(3628);
		int currentLegs = client.getVarbitValue(3629);
		int currentBoots = client.getVarbitValue(3630);
		int currentWeapon = client.getVarbitValue(3631);
		List<Integer> currentEquipment = Arrays.asList(currentHelmet, currentBody, currentLegs, currentBoots, currentWeapon);
		List<Integer> neededEquipment = new ArrayList<>();
		for (CyrisusBankItem item : armourSet.getItems())
		{
			neededEquipment.add(item.getVarbitID());
		}
		return currentEquipment.equals(neededEquipment);
	}
}

