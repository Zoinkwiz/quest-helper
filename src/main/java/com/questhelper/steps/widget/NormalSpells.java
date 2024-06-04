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

public enum NormalSpells implements Spell
{
	LUMBRIDGE_HOME_TELEPORT("Lumbridge Home Teleport"),
	WIND_STRIKE("Wind Strike"),
	CONFUSE("Confuse"),
	ENCHANT_CROSSBOW_BOLT("Enchant Crossbow Bolt"),
	WATER_STRIKE("Water Strike"),
	LVL_1_ENCHANT("Lvl-1 Enchant"),
	EARTH_STRIKE("Earth Strike"),
	WEAKEN("Weaken"),
	FIRE_STRIKE("Fire Strike"),
	BONES_TO_BANANAS("Bones to Bananas"),
	WIND_BOLT("Wind Bolt"),
	CURSE("Curse"),
	BIND("Bind"),
	LOW_LEVEL_ALCHEMY("Low Level Alchemy"),
	WATER_BOLT("Water Bolt"),
	VARROCK_TELEPORT("Varrock Teleport"),
	LVL_2_ENCHANT("Lvl-2 Enchant"),
	EARTH_BOLT("Earth Bolt"),
	LUMBRIDGE_TELEPORT("Lumbridge Teleport"),
	TELEKINETIC_GRAB("Telekinetic Grab"),
	FIRE_BOLT("Fire Bolt"),
	FALADOR_TELEPORT("Falador Teleport"),
	CRUMBLE_UNDEAD("Crumble Undead"),
	TELEPORT_TO_HOUSE("Teleport to House"),
	WIND_BLAST("Wind Blast"),
	SUPERHEAT_ITEM("Superheat Item"),
	CAMELOT_TELEPORT("Camelot Teleport"),
	WATER_BLAST("Water Blast"),
	KOUREND_CASTLE_TELEPORT("Kourend Castle Teleport"),
	LVL_3_ENCHANT("Lvl-3 Enchant"),
	IBAN_BLAST("Iban Blast"),
	SNARE("Snare"),
	MAGIC_DART("Magic Dart"),
	ARDOUGNE_TELEPORT("Ardougne Teleport"),
	EARTH_BLAST("Earth Blast"),
	CIVITAS_ILLA_FORTIS_TELEPORT("Civitas illa Fortis Teleport"),
	HIGH_LEVEL_ALCHEMY("High Level Alchemy"),
	CHARGE_WATER_ORB("Charge Water Orb"),
	LVL_4_ENCHANT("Lvl-4 Enchant"),
	WATCHTOWER_TELEPORT("Watchtower Teleport"),
	FIRE_BLAST("Fire Blast"),
	CHARGE_EARTH_ORB("Charge Earth Orb"),
	BONES_TO_PEACHES("Bones to Peaches"),
	SARADOMIN_STRIKE("Saradomin Strike"),
	FLAMES_OF_ZAMORAK("Flames of Zamorak"),
	CLAWS_OF_GUTHIX("Claws of Guthix"),
	TROLLHEIM_TELEPORT("Trollheim Teleport"),
	WIND_WAVE("Wind Wave"),
	CHARGE_FIRE_ORB("Charge Fire Orb"),
	APE_ATOLL_TELEPORT("Ape Atoll Teleport"),
	WATER_WAVE("Water Wave"),
	CHARGE_AIR_ORB("Charge Air Orb"),
	VULNERABILITY("Vulnerability"),
	LVL_5_ENCHANT("Lvl-5 Enchant"),
	EARTH_WAVE("Earth Wave"),
	ENFEEBLE("Enfeeble"),
	TELEOTHER_LUMBRIDGE("Teleother Lumbridge"),
	FIRE_WAVE("Fire Wave"),
	ENTANGLE("Entangle"),
	STUN("Stun"),
	CHARGE("Charge"),
	WIND_SURGE("Wind Surge"),
	TELEOTHER_FALADOR("Teleother Falador"),
	WATER_SURGE("Water Surge"),
	TELE_BLOCK("Tele Block"),
	TELEPORT_TO_TARGET("Teleport to Target"),
	LVL_6_ENCHANT("Lvl-6 Enchant"),
	TELEOTHER_CAMELOT("Teleother Camelot"),
	EARTH_SURGE("Earth Surge"),
	LVL_7_ENCHANT("Lvl-7 Enchant"),
	FIRE_SURGE("Fire Surge");

	private final String spellName;

	NormalSpells(String spellName)
	{
		this.spellName = spellName;
	}

	public String getSpellName()
	{
		return spellName;
	}
}
