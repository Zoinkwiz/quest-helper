/*
 * Copyright (c) 2024, Zoinkwiz <https://www.github.com/Zoinkwiz>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.steps.widget;

public enum LunarSpells implements Spell
{
	LUNAR_HOME_TELEPORT("Lunar Home Teleport"),
	BAKE_PIE("Bake Pie"),
	GEOMANCY("Geomancy"),
	CURE_PLANT("Cure Plant"),
	MONSTER_EXAMINE("Monster Examine"),
	NPC_CONTACT("NPC Contact"),
	CURE_OTHER("Cure Other"),
	HUMIDIFY("Humidify"),
	MOONCLAN_TELEPORT("Moonclan Teleport"),
	TELE_GROUP_MOONCLAN("Tele Group Moonclan"),
	CURE_ME("Cure Me"),
	OURIANA_TELEPORT("Ourania Teleport"),
	HUNTER_KIT("Hunter Kit"),
	WATERBIRTH_TELEPORT("Waterbirth Teleport"),
	TELE_GROUP_WATERBIRTH("Tele Group Waterbirth"),
	CURE_GROUP("Cure Group"),
	STAT_SPY("Stat Spy"),
	BARBARIAN_TELEPORT("Barbarian Teleport"),
	TELE_GROUP_BARBARIAN("Tele Group Barbarian"),
	SPIN_FLAX("Spin Flax"),
	SUPERGLASS_MAKE("Superglass Make"),
	TAN_LEATHER("Tan Leather"),
	KHAZARD_TELEPORT("Khazard Teleport"),
	TELE_GROUP_KHAZARD("Tele Group Khazard"),
	DREAM("Dream"),
	STRING_JEWELLERY("String Jewellery"),
	STAT_RESTORE_POT_SHARE("Stat Restore Pot Share"),
	MAGIC_IMBUE("Magic Imbue"),
	FERTILE_SOIL("Fertile Soil"),
	BOOST_POTION_SHARE("Boost Potion Share"),
	FISHING_GUILD_TELEPORT("Fishing Guild Teleport"),
	TELEPORT_TO_TARGET("Teleport to Target"),
	TELE_GROUP_FISHING_GUILD("Tele Group Fishing Guild"),
	PLANK_MAKE("Plank Make"),
	CATHERBY_TELEPORT("Catherby Teleport"),
	TELE_GROUP_CATHERBY("Tele Group Catherby"),
	RECHARGE_DRAGONSTONE("Recharge Dragonstone"),
	ICE_PLATEAU_TELEPORT("Ice Plateau Teleport"),
	TELE_GROUP_ICE_PLATEAU("Tele Group Ice Plateau"),
	ENERGY_TRANSFER("Energy Transfer"),
	HEAL_OTHER("Heal Other"),
	VENGEANCE_OTHER("Vengeance Other"),
	VENGEANCE("Vengeance"),
	HEAL_GROUP("Heal Group"),
	SPELLBOOK_SWAP("Spellbook Swap");

	private final String spellName;

	LunarSpells(String spellName)
	{
		this.spellName = spellName;
	}

	@Override
	public String getSpellName()
	{
		return spellName;
	}
}
