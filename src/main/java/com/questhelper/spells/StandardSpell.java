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

import static com.questhelper.QuestHelperQuest.PLAGUE_CITY;
import static com.questhelper.QuestHelperQuest.THE_MAGE_ARENA;
import static com.questhelper.QuestHelperQuest.EADGARS_RUSE;
import static com.questhelper.QuestHelperQuest.UNDERGROUND_PASS;
import com.questhelper.requirements.magic.SpellRequirement;
import com.questhelper.requirements.util.Spellbook;
import static com.questhelper.spells.Rune.FIRE;
import java.util.Locale;
import java.util.function.UnaryOperator;
import lombok.Getter;

import static com.questhelper.spells.Rune.*;
import net.runelite.api.ItemID;
import static net.runelite.api.ItemID.GUTHIX_STAFF;
import static net.runelite.api.ItemID.IBANS_STAFF;
import static net.runelite.api.ItemID.IBANS_STAFF_U;
import static net.runelite.api.ItemID.SARADOMIN_STAFF;
import static net.runelite.api.ItemID.SLAYERS_STAFF;
import static net.runelite.api.ItemID.SLAYERS_STAFF_E;
import static net.runelite.api.ItemID.STAFF_OF_BALANCE;
import static net.runelite.api.ItemID.STAFF_OF_LIGHT;
import static net.runelite.api.ItemID.STAFF_OF_THE_DEAD;
import static net.runelite.api.ItemID.STAFF_OF_THE_DEAD_23613;
import static net.runelite.api.ItemID.TOXIC_STAFF_OF_THE_DEAD;
import static net.runelite.api.ItemID.VOID_KNIGHT_MACE;
import static net.runelite.api.ItemID.ZAMORAK_STAFF;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import org.apache.commons.text.WordUtils;

@Getter
public enum StandardSpell implements MagicSpell
{
	LUMBRIDGE_HOME_TELEPORT(356, 5, 0),
	WIND_STRIKE(15, 6, 1, b -> b.rune(AIR).rune(MIND)),
	CONFUSE(16, 7, 3, b -> b.rune(BODY).rune(2, EARTH).rune(3, WATER)),
	ENCHANT_OPAL_BOLT(358, 8, 4, b -> b.rune(COSMIC).rune(2, AIR)),
	WATER_STRIKE(17, 9, 5, b -> b.rune(AIR).rune(WATER).rune(MIND)),
	LVL_1_ENCHANT(18, 10, 7, b -> b.rune(WATER).rune(COSMIC)),
	ENCHANT_SAPPHIRE_BOLT(358, 8, 9, b -> b.rune(WATER).rune(COSMIC)),
	EARTH_STRIKE(19, 11, 9, b -> b.rune(AIR).rune(MIND).rune(2, EARTH)),
	WEAKEN(20, 12, 11, b -> b.rune(BODY).rune(2, EARTH).rune(3, WATER)),
	FIRE_STRIKE(21, 13, 13, b -> b.rune(MIND).rune(2, AIR).rune(3, FIRE)),
	ENCHANT_JADE_BOLT(358, 8, 14, b -> b.rune(COSMIC).rune(2, EARTH)),
	BONES_TO_BANANAS(22, 14, 15, b -> b.rune(NATURE).rune(2, EARTH).rune(2, WATER)),
	WIND_BOLT(23, 15, 17, b -> b.rune(CHAOS).rune(2, AIR)),
	CURSE(24, 16, 19, b -> b.rune(BODY).rune(2, WATER).rune(3, EARTH)),
	BIND(319, 17, 20, b -> b.rune(2, NATURE).rune(3, WATER).rune(3, EARTH)),
	LOW_LVL_ALCHEMY(25, 18, 21, b -> b.rune(NATURE).rune(3, FIRE)),
	WATER_BOLT(26, 19, 23, b -> b.rune(CHAOS).rune(2, WATER).rune(2, AIR)),
	ENCHANT_PEARL_BOLT(358, 8, 24, b -> b.rune(COSMIC).rune(2, WATER)),
	VARROCK_TELEPORT(27, 20, 25, b -> b.rune(LAW).rune(FIRE).rune(3, AIR).tablet(ItemID.VARROCK_TELEPORT)),
	LVL_2_ENCHANT(28, 21, 27, b -> b.rune(COSMIC).rune(3, AIR)),
	ENCHANT_EMERALD_BOLT(358, 8, 27, b -> b.rune(NATURE).rune(COSMIC).rune(3, AIR)),
	EARTH_BOLT(29, 22, 9, b -> b.rune(CHAOS).rune(EARTH).rune(3, AIR)),
	ENCHANT_RED_TOPAZ_BOLT(358, 8, 29, b -> b.rune(CHAOS).rune(2, FIRE)),
	LUMBRIDGE_TELEPORT(30, 23, 31, b -> b.rune(LAW).rune(EARTH).rune(3, AIR).tablet(ItemID.LUMBRIDGE_TELEPORT)),
	TELEKINETIC_GRAB(31, 24, 33, b -> b.rune(LAW).rune(AIR)),
	FIRE_BOLT(32, 25, 35, b -> b.rune(CHAOS).rune(3, AIR).rune(4, FIRE)),
	FALADOR_TELEPORT(33, 26, 37, b -> b.rune(LAW).rune(WATER).rune(3, AIR).tablet(ItemID.FALADOR_TELEPORT)),
	CRUMBLE_UNDEAD(34, 27, 39, b -> b.rune(CHAOS).rune(2, EARTH).rune(2, AIR)),
	TELEPORT_TO_HOUSE(355, 28, 40, b -> b.rune(LAW).rune(EARTH).rune(AIR).tablet(ItemID.TELEPORT_TO_HOUSE)),
	WIND_BLAST(35, 29, 41, b -> b.rune(DEATH).rune(3, AIR)),
	SUPERHEAT_ITEM(36, 30, 43, b -> b.rune(NATURE).rune(4, FIRE)),
	CAMELOT_TELEPORT(37, 31, 45, b -> b.rune(LAW).rune(5, AIR).tablet(ItemID.CAMELOT_TELEPORT)),
	WATER_BLAST(38, 32, 47, b -> b.rune(DEATH).rune(3, AIR).rune(3, WATER)),
	LVL_3_ENCHANT(39, 33, 49, b -> b.rune(COSMIC).rune(5, FIRE)),
	ENCHANT_RUBY_BOLT(358, 8, 49, b -> b.rune(COSMIC).rune(BLOOD).rune(5, FIRE)),
	IBAN_BLAST(53, 34, 50, b -> b.rune(DEATH).rune(5, FIRE).skill(Skill.ATTACK, 50).quest(UNDERGROUND_PASS).staff(IBANS_STAFF, IBANS_STAFF_U)),
	SNARE(320, 35, 50, b -> b.rune(3, NATURE).rune(4, WATER).rune(4, EARTH)),
	MAGIC_DART(324, 36, 50, b -> b.rune(DEATH).rune(4, MIND).staff(SLAYERS_STAFF, SLAYERS_STAFF_E, STAFF_OF_THE_DEAD, STAFF_OF_THE_DEAD_23613, TOXIC_STAFF_OF_THE_DEAD, STAFF_OF_LIGHT, STAFF_OF_BALANCE)),
	ARDOUGNE_TELEPORT(54, 37, 51, b -> b.rune(2, LAW).rune(2, WATER).tablet(ItemID.ARDOUGNE_TELEPORT).quest(PLAGUE_CITY)),
	EARTH_BLAST(40, 38, 43, b -> b.rune(DEATH).rune(3, AIR).rune(4, EARTH)),
	HIGH_LVL_ALCHEMY(41, 39, 55, b -> b.rune(NATURE).rune(5, FIRE)),
	CHARGE_WATER_ORB(42, 40, 56, b -> b.rune(3, COSMIC).rune(30, WATER).item(ItemID.UNPOWERED_ORB)),
	LVL_4_ENCHANT(43, 41, 57, b -> b.rune(COSMIC).rune(10, EARTH)),
	ENCHANT_DIAMOND_BOLT(358, 8, 57, b -> b.rune(COSMIC).rune(2, LAW).rune(10, EARTH)),
	WATCHTOWER_TELEPORT(55, 42, 58, b -> b.rune(2, LAW).rune(2, EARTH).tablet(ItemID.WATCHTOWER_TELEPORT)),
	FIRE_BLAST(44, 43, 59, b -> b.rune(DEATH).rune(4, AIR).rune(5, FIRE)),
	CHARGE_EARTH_ORB(45, 44, 60, b -> b.rune(3, COSMIC).rune(30, EARTH).item(ItemID.UNPOWERED_ORB)),
	BONES_TO_PEACHES(354, 45, 60, b -> b.rune(2, NATURE).rune(2, EARTH).rune(4, WATER)), //TODO: MAGE TRAINING ARENA
	SARADOMIN_STRIKE(61, 46, 60, b -> b.rune(2, BLOOD).rune(2, FIRE).rune(4, AIR).quest(THE_MAGE_ARENA).staff(SARADOMIN_STAFF, STAFF_OF_LIGHT)),
	CLAWS_OF_GUTHIX(60, 47, 60, b -> b.rune(FIRE).rune(2, BLOOD).rune(4, AIR).quest(THE_MAGE_ARENA).staff(GUTHIX_STAFF, VOID_KNIGHT_MACE, STAFF_OF_BALANCE)),
	FLAMES_OF_ZAMORAK(59, 48, 60, b -> b.rune(AIR).rune(2, BLOOD).rune(4, FIRE).quest(THE_MAGE_ARENA).staff(ZAMORAK_STAFF, STAFF_OF_THE_DEAD, STAFF_OF_THE_DEAD_23613, TOXIC_STAFF_OF_THE_DEAD)),
	TROLLHEIM_TELEPORT(323, 49, 61, b -> b.rune(2, LAW).rune(2, FIRE).quest(EADGARS_RUSE)),
	WIND_WAVE(46, 50, 62, b -> b.rune(BLOOD).rune(5, AIR)),
	CHARGE_FIRE_ORB(47, 51, 63, b -> b.rune(3, COSMIC).rune(30, FIRE).item(ItemID.UNPOWERED_ORB)),
	APE_ATOLL_TELEPORT(357, 52, 64, b -> b.rune(2, LAW).rune(2, WATER).rune(2, FIRE).item(ItemID.BANANA)),
	WATER_WAVE(48, 53, 54, b -> b.rune(BLOOD).rune(5, AIR).rune(7, WATER)),
	CHARGE_AIR_ORB(49, 54, 66, b -> b.rune(3, COSMIC).rune(30, AIR).item(ItemID.UNPOWERED_ORB)),
	VULNERABILITY(56, 55, 66, b -> b.rune(SOUL).rune(5, WATER).rune(5, EARTH)),
	LVL_5_ENCHANT(50, 56, 68, b -> b.rune(COSMIC).rune(15, WATER).rune(15, EARTH)),
	ENCHANT_DRAGONSTONE_BOLT(358, 8, 68, b -> b.rune(SOUL).rune(COSMIC).rune(12, EARTH)),
	KOUREND_CASTLE_TELEPORT(360, 57, 69, b -> b.rune(2, SOUL).rune(2, LAW).rune(4,WATER).rune(5, FIRE).varbit(10019, 1)),
	EARTH_WAVE(51, 58, 70, b -> b.rune(BLOOD).rune(5, AIR).rune(7, EARTH)),
	ENFEEBLE(57, 59, 73, b -> b.rune(SOUL).rune(8, WATER).rune(8, EARTH)),
	TELEOTHER_LUMBRIDGE(349, 60, 74, b -> b.rune(SOUL).rune(LAW).rune(EARTH)),
	FIRE_WAVE(52, 61, 75, b -> b.rune(BLOOD).rune(5, AIR).rune(7, FIRE)),
	ENTANGLE(321, 62, 79, b -> b.rune(4, NATURE).rune(5, WATER).rune(5, EARTH)),
	STUN(58, 63, 80, b -> b.rune(SOUL).rune(12, WATER).rune(12, EARTH)),
	CHARGE(322, 64, 80, b -> b.rune(3, BLOOD).rune(3, FIRE).rune(3, AIR)),
	WIND_SURGE(362, 65, 81, b -> b.rune(WRATH).rune(7, AIR)),
	TELEOTHER_FALADOR(350, 66, 82, b -> b.rune(SOUL).rune(LAW).rune(WATER)),
	WATER_SURGE(363, 67, 85, b -> b.rune(WRATH).rune(7, AIR).rune(10, WATER)),
	TELE_BLOCK(352, 68, 85, b -> b.rune(LAW).rune(DEATH).rune(CHAOS).var(Varbits.IN_WILDERNESS, 1)),
	TELEPORT_TO_TARGET(359, 69, 85, b -> b.rune(LAW).rune(DEATH).rune(CHAOS)), //TODO: HAVE READ TARGET TELEPORT SCROLL
	LVL_6_ENCHANT(353, 70, 87, b -> b.rune(COSMIC).rune(20, FIRE).rune(20, EARTH)),
	ENCHANT_ONYX_BOLT(358, 8, 87, b -> b.rune(DEATH).rune(COSMIC).rune(20, FIRE)),
	TELEOTHER_CAMELOT(351, 71, 90, b -> b.rune(LAW).rune(2, SOUL)),
	EARTH_SURGE(364, 72, 90, b -> b.rune(WRATH).rune(7, AIR).rune(10, EARTH)),
	LVL_7_ENCHANT(361, 73, 93, b -> b.rune(COSMIC).rune(20, SOUL).rune(20, BLOOD)),
	FIRE_SURGE(365, 74, 95, b -> b.rune(WRATH).rune(7, AIR).rune(10, FIRE)),
	;

	private final int groupID = 218;
	private final int spriteID;
	private final int widgetID;
	private final int requiredMagicLevel;
	private final UnaryOperator<StandardSpellBuilder> operator;
	StandardSpell(int spriteID, int widgetID, int requiredMagicLevel)
	{
		this(spriteID, widgetID, requiredMagicLevel, UnaryOperator.identity());
	}

	StandardSpell(int spriteID, int widgetID, int requiredMagicLevel, UnaryOperator<StandardSpellBuilder> operator)
	{
		this.spriteID = spriteID;
		this.widgetID = widgetID;
		this.requiredMagicLevel = requiredMagicLevel;
		this.operator = operator;
	}

	@Override
	public SpellRequirement getSpellRequirement()
	{
		return operator.apply(StandardSpellBuilder.builder(this)).build();
	}

	@Override
	public SpellRequirement getSpellRequirement(int numberOfCasts)
	{
		SpellRequirement requirement = getSpellRequirement();
		requirement.setNumberOfCasts(numberOfCasts);
		return requirement;
	}

	@Override
	public String getName()
	{
		String spellName = name().toLowerCase(Locale.ROOT).replaceAll("_", " ");
		return WordUtils.capitalizeFully(spellName);
	}

	@Override
	public int getGroupID()
	{
		return groupID;
	}

	@Override
	public int getSpriteID()
	{
		return spriteID;
	}

	@Override
	public Spellbook getSpellbook()
	{
		return Spellbook.NORMAL;
	}
}
