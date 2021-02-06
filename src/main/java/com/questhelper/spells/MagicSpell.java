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

import com.questhelper.requirements.SpellRequirement;
import com.questhelper.requirements.util.Spellbook;

/**
 * Represents a magic spell that can be cast by a player.
 */
public interface MagicSpell
{
	/**
	 * @return the formatted display name
	 */
	String getName();

	/**
	 * @return the widget ID
	 */
	int getWidgetID();

	/**
	 * @return the group ID
	 */
	int getGroupID();

	/**
	 * @return the sprite ID
	 *
	 * @see net.runelite.api.SpriteID
	 */
	int getSpriteID();

	/**
	 * @return the required {@link net.runelite.api.Skill#MAGIC)} level to cast this spell.
	 */
	int getRequiredMagicLevel();

	/**
	 * @return the {@link Spellbook} this spell is contained within.
	 */
	Spellbook getSpellbook();

	/**
	 * @return a new {@link SpellRequirement} for a single cast of this spell.
	 */
	SpellRequirement getSpellRequirement();

	SpellRequirement getSpellRequirement(int numberOfCasts);
}
