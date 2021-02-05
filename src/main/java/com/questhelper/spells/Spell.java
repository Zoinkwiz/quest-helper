/*
 *
 *  * Copyright (c) 2021, Senmori
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice, this
 *  *    list of conditions and the following disclaimer.
 *  * 2. Redistributions in binary form must reproduce the above copyright notice,
 *  *    this list of conditions and the following disclaimer in the documentation
 *  *    and/or other materials provided with the distribution.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.questhelper.spells;

import com.questhelper.requirements.Requirement;
import java.util.function.UnaryOperator;
import lombok.Getter;

import static com.questhelper.spells.Rune.*;
import net.runelite.api.ItemID;

@Getter
public enum Spell
{
	LUMBRIDGE_HOME_TELEPORT(5, 0),
	WIND_STRIKE(6, 1, b -> b.rune(AIR).rune(MIND)),
	CONFUSE(7, 3, b -> b.rune(BODY).rune(2, EARTH).rune(3, WATER)),
	ENCHANT_OPAL_BOLT(8, 4, b -> b.rune(COSMIC).rune(2, AIR)),
	WATER_STRIKE(9, 5, b -> b.rune(AIR).rune(WATER).rune(MIND)),
	ENCHANT_LVL_1(10, 7, b -> b.rune(WATER).rune(COSMIC)),
	ENCHANT_SAPPHIRE_BOLT(8, 9, b -> b.rune(WATER).rune(COSMIC)),
	EARTH_STRIKE(11, 9, b -> b.rune(AIR).rune(MIND).rune(2, EARTH)),
	WEAKEN(12, 11, b -> b.rune(BODY).rune(2, EARTH).rune(3, WATER)),
	FIRE_STRIKE(13, 13, b -> b.rune(MIND).rune(2, AIR).rune(3, FIRE)),
	ENCHANT_JADE_BOLT(8, 14, b -> b.rune(COSMIC).rune(2, EARTH)),
	BONES_TO_BANANAS(14, 15, b -> b.rune(NATURE).rune(2, EARTH).rune(2, WATER)),
	WIND_BOLT(15, 17, b -> b.rune(CHAOS).rune(2, AIR)),
	CURSE(16, 19, b -> b.rune(BODY).rune(2, WATER).rune(3, EARTH)),
	BIND(17, 20, b -> b.rune(2, NATURE).rune(3, WATER).rune(3, EARTH)),
	LOW_LVL_ALCHEMY(18, 21, b -> b.rune(NATURE).rune(3, FIRE)),
	WATER_BOLT(19, 23, b -> b.rune(CHAOS).rune(2, WATER).rune(2, AIR)),
	ENCHANT_PEARL_BOLT(8, 24, b -> b.rune(COSMIC).rune(2, WATER)),
	VARROCK_TELEPORT(20, 25, b -> b.rune(LAW).rune(FIRE).rune(3, AIR)),
	ENCHANT_LVL_2(21, 27, b -> b.rune(COSMIC).rune(3, AIR)),
	ENCHANT_EMERALD_BOLT(8, 27, b -> b.rune(NATURE).rune(COSMIC).rune(3, AIR)),
	EARTH_BOLT(22, 9, b -> b.rune(CHAOS).rune(EARTH).rune(3, AIR)),
	ENCHANT_RED_TOPAZ_BOLT(8, 29, b -> b.rune(CHAOS).rune(2, FIRE)),
	LUMBRIDGE_TELEPORT(23, 31, b -> b.rune(LAW).rune(EARTH).rune(3, AIR)),
	TELEKINETIC_GRAB(24, 33, b -> b.rune(LAW).rune(AIR)),
	FIRE_BOLT(25, 35, b -> b.rune(CHAOS).rune(3, AIR).rune(4, FIRE)),
	FALADOR_TELEPORT(26, 37, b -> b.rune(LAW).rune(WATER).rune(3, AIR)),
	CRUMBLE_UNDEAD(27, 39, b -> b.rune(CHAOS).rune(2, EARTH).rune(2, AIR)),
	TELEPORT_TO_HOUSE(28, 40, b -> b.rune(LAW).rune(EARTH).rune(AIR)),
	WIND_BLAST(29, 41, b -> b.rune(DEATH).rune(3, AIR)),
	SUPERHEAT_ITEM(30, 43, b -> b.rune(NATURE).rune(4, FIRE)),
	CAMELOT_TELEPORT(31, 45, b -> b.rune(LAW).rune(5, AIR)),
	WATER_BLAST(32, 47, b -> b.rune(DEATH).rune(3, AIR).rune(3, WATER)),
	ENCHANT_LVL_3(33, 49, b -> b.rune(COSMIC).rune(5, FIRE)),
	ENCHANT_RUBY_BOLT(8, 49, b -> b.rune(COSMIC).rune(BLOOD).rune(5, FIRE)),
	IBAN_BLAST(34, 50, b -> b.rune(DEATH).rune(5, FIRE).item(true, ItemID.IBANS_STAFF, ItemID.IBANS_STAFF_U)),
	SNARE(35, 50, b -> b.rune(3, NATURE).rune(4, WATER).rune(4, EARTH)),
	MAGIC_DART(36, 50, b -> b.rune(DEATH).rune(4, MIND)),
	ARDOUGNE_TELEPORT(37, 51, b -> b.rune(2, LAW).rune(2, WATER)), //TODO: QUEST REQ -> PLAGUE CITY
	EARTH_BLAST(38, 43, b -> b.rune(DEATH).rune(3, AIR).rune(4, EARTH)),
	HIGH_LVL_ALCHEMY(39, 55, b -> b.rune(NATURE).rune(5, FIRE)),
	CHARGE_WATER_ORB(40, 56, b -> b.rune(3, COSMIC).rune(30, WATER).item(ItemID.UNPOWERED_ORB)),
	ENCHANT_LVL_4(41, 57, b -> b.rune(COSMIC).rune(10, EARTH)),
	ENCHANT_DIAMOND_BOLT(8, 57, b -> b.rune(COSMIC).rune(2, LAW).rune(10, EARTH)),
	WATCHTOWER_TELEPORT(42, 58, b -> b.rune(2, LAW).rune(2, EARTH)),
	FIRE_BLAST(43, 59, b -> b.rune(DEATH).rune(4, AIR).rune(5, FIRE)),
	CHARGE_EARTH_ORB(44, 60, b -> b.rune(3, COSMIC).rune(30, EARTH).item(ItemID.UNPOWERED_ORB)),
	BONES_TO_PEACHES(45, 60, b -> b.rune(2, NATURE).rune(2, EARTH).rune(4, WATER)), //TODO: MAGE TRAINING ARENA
	SARADOMIN_STRIKE(46, 60, b -> b.rune(2, BLOOD).rune(2, FIRE).rune(4, AIR)), // TODO: MAGE ARENA
	CLAWS_OF_GUTHIX(47, 60, b -> b.rune(FIRE).rune(2, BLOOD).rune(4, AIR)), // TODO: MAGE ARENA
	FLAMES_OF_ZAMORAK(48, 60, b -> b.rune(AIR).rune(2, BLOOD).rune(4, FIRE)), // TODO: MAGE ARENA
	TROLLHEIM_TELEPORT(49, 61, b -> b.rune(2, LAW).rune(2, FIRE)), // TODO: QUEST REQ -> EADGARS RUSE QUEST
	WIND_WAVE(50, 62, b -> b.rune(BLOOD).rune(5, AIR)),
	CHARGE_FIRE_ORB(51, 63, b -> b.rune(3, COSMIC).rune(30, FIRE).item(ItemID.UNPOWERED_ORB)),
	APE_ATOLL_TELEPORT(52, 64, b -> b.rune(2, LAW).rune(2, WATER).rune(2, FIRE).item(ItemID.BANANA)),
	WATER_WAVE(53, 54, b -> b.rune(BLOOD).rune(5, AIR).rune(7, WATER)),
	CHARGE_AIR_ORB(54, 66, b -> b.rune(3, COSMIC).rune(30, AIR).item(ItemID.UNPOWERED_ORB)),
	VULNERABILITY(55, 66, b -> b.rune(SOUL).rune(5, WATER).rune(5, EARTH)),
	ENCHANT_LVL_5(56, 68, b -> b.rune(COSMIC).rune(15, WATER).rune(15, EARTH)),
	ENCHANT_DRAGONSTONE_BOLT(8, 68, b -> b.rune(SOUL).rune(COSMIC).rune(12, EARTH)),
	KOUREND_CASTLE_TELEPORT(57, 69, b -> b.rune(2, SOUL).rune(2, LAW).rune(4,WATER).rune(5, FIRE)), //TODO: UNLOCKED VIA BOOK
	EARTH_WAVE(58, 70, b -> b.rune(BLOOD).rune(5, AIR).rune(7, EARTH)),
	ENFEEBLE(59, 73, b -> b.rune(SOUL).rune(8, WATER).rune(8, EARTH)),
	TELEOTHER_LUMBRIDGE(60, 74, b -> b.rune(SOUL).rune(LAW).rune(EARTH)),
	FIRE_WAVE(61, 75, b -> b.rune(BLOOD).rune(5, AIR).rune(7, FIRE)),
	ENTANGLE(62, 79, b -> b.rune(4, NATURE).rune(5, WATER).rune(5, EARTH)),
	STUN(63, 80, b -> b.rune(SOUL).rune(12, WATER).rune(12, EARTH)),
	CHARGE(64, 80, b -> b.rune(3, BLOOD).rune(3, FIRE).rune(3, AIR)),
	WIND_SURGE(65, 81, b -> b.rune(WRATH).rune(7, AIR)),
	TELEOTHER_FALADOR(66, 82, b -> b.rune(SOUL).rune(LAW).rune(WATER)),
	WATER_SURGE(67, 85, b -> b.rune(WRATH).rune(7, AIR).rune(10, WATER)),
	TELE_BLOCK(68, 85, b -> b.rune(LAW).rune(DEATH).rune(CHAOS)), //TODO: IN WILDERNESS
	TELEPORT_TO_TARGET(69, 85, b -> b.rune(LAW).rune(DEATH).rune(CHAOS)), //TODO: HAVE READ TARGET TELEPORT SCROLL
	ENCHANT_LVL_6(70, 87, b -> b.rune(COSMIC).rune(20, FIRE).rune(20, EARTH)),
	ENCHANT_ONYX_BOLT(8, 87, b -> b.rune(DEATH).rune(COSMIC).rune(20, FIRE)),
	TELEOTHER_CAMELOT(71, 90, b -> b.rune(LAW).rune(2, SOUL)),
	EARTH_SURGE(72, 90, b -> b.rune(WRATH).rune(7, AIR).rune(10, EARTH)),
	ENCHANT_LVL_7(73, 93, b -> b.rune(COSMIC).rune(20, SOUL).rune(20, BLOOD)),
	FIRE_SURGE(74, 95, b -> b.rune(WRATH).rune(7, AIR).rune(10, FIRE)),
	;

	private final int groupId;
	private final int widgetID;
	private final int requiredMagicLevel;
	private final UnaryOperator<StandardSpellBuilder> operator;
	Spell(int widgetID, int requiredMagicLevel)
	{
		this.groupId = 218;
		this.widgetID = widgetID;
		this.requiredMagicLevel = requiredMagicLevel;
		this.operator = UnaryOperator.identity(); // Spell has no requirements
	}

	Spell(int widgetID, int requiredMagicLevel, UnaryOperator<StandardSpellBuilder> operator)
	{
		this.groupId = 218;
		this.widgetID = widgetID;
		this.requiredMagicLevel = requiredMagicLevel;
		this.operator = operator;
	}

	public Requirement getRequirements()
	{
		return operator.apply(StandardSpellBuilder.builder(this)).build();
	}
}
