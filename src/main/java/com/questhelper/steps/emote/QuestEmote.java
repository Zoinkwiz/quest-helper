/*
 * Copyright (c) 2018, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.steps.emote;

import lombok.Getter;
import net.runelite.api.gameval.SpriteID;

@Getter
public enum QuestEmote
{
	SKILL_CAPE("Skill Cape", SpriteID.Emotes.SKILLCAPE),
	FLEX("Flex", SpriteID.Emotes._51),
	CLAP("Clap", SpriteID.Emotes.CLAP),
	CRY("Cry", SpriteID.Emotes.CRY),
	BOW("Bow", SpriteID.Emotes.BOW),
	DANCE("Dance", SpriteID.Emotes.DANCE),
	WAVE("Wave", SpriteID.Emotes.WAVE),
	THINK("Think", SpriteID.Emotes.THINK),
	GOBLIN_BOW("Goblin bow", SpriteID.Emotes.GOBLIN_BOW),
	BLOW_KISS("Blow Kiss", SpriteID.Emotes.BLOW_KISS),
	IDEA("Idea", SpriteID.Emotes.IDEA),
	STAMP("Stamp", SpriteID.Emotes.STAMP),
	FLAP("Flap", SpriteID.Emotes.FLAP),
	SLAP_HEAD("Slap Head", SpriteID.Emotes.SLAP_HEAD),
	SPIN("Spin", SpriteID.Emotes.SPIN);

	private String name;
	private int spriteId;

	QuestEmote(String name, int spriteId)
	{
		this.name = name;
		this.spriteId = spriteId;
	}
}
