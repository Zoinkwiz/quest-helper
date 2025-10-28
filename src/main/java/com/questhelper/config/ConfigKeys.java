/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.config;

import lombok.Getter;

public enum ConfigKeys
{
	// Barbarian Training Enums
	BARBARIAN_TRAINING_STARTED_FISHING("barbariantrainingstartedfishing"),
	BARBARIAN_TRAINING_STARTED_HARPOON("barbariantrainingstartedharpoon"),
	BARBARIAN_TRAINING_STARTED_SEED_PLANTING("barbariantrainingstartedseedplanting"),
	BARBARIAN_TRAINING_STARTED_POT_SMASHING("barbariantrainingstartedpotsmashing"),
	BARBARIAN_TRAINING_STARTED_FIREMAKING("barbariantrainingstartedfiremaking"),
	BARBARIAN_TRAINING_STARTED_PYREMAKING("barbariantrainingstartedpyremaking"),
	BARBARIAN_TRAINING_STARTED_HERBLORE("barbariantrainingstartedherblore"),
	BARBARIAN_TRAINING_STARTED_SPEAR("barbariantrainingstartedspear"),
	BARBARIAN_TRAINING_STARTED_HASTA("barbariantrainingstartedhasta"),

	// Finished Barbarian Training Enums
	BARBARIAN_TRAINING_FINISHED_FISHING("barbariantrainingfinishedfishing"),
	BARBARIAN_TRAINING_FINISHED_HARPOON("barbariantrainingfinishedharpoon"),
	BARBARIAN_TRAINING_FINISHED_SEED_PLANTING("barbariantrainingfinishedseedplanting"),
	BARBARIAN_TRAINING_FINISHED_POT_SMASHING("barbariantrainingfinishedpotsmashing"),
	BARBARIAN_TRAINING_FINISHED_FIREMAKING("barbariantrainingfinishedfiremaking"),
	BARBARIAN_TRAINING_FINISHED_PYREMAKING("barbariantrainingfinishedpyremaking"),
	BARBARIAN_TRAINING_FINISHED_SPEAR("barbariantrainingfinishedspear"),
	BARBARIAN_TRAINING_FINISHED_HASTA("barbariantrainingfinishedhasta"),
	BARBARIAN_TRAINING_FINISHED_HERBLORE("barbariantrainingfinishedherblore"),

	// Mid-conditions
	BARBARIAN_TRAINING_PLANTED_SEED("barbariantrainingplantedseed"),
	BARBARIAN_TRAINING_SMASHED_POT("barbariantrainingsmashedpot"),
	BARBARIAN_TRAINING_BOW_FIRE("barbariantrainingbowfire"),
	BARBARIAN_TRAINING_PYRE_MADE("barbariantrainingpyremade"),
	BARBARIAN_TRAINING_BARBFISHED("barbariantrainingbarbfished"),
	BARBARIAN_TRAINING_HARPOONED_FISH("barbariantrainingharpoonedfish"),
	BARBARIAN_TRAINING_MADE_POTION("barbariantrainingmadepotion"),
	BARBARIAN_TRAINING_MADE_SPEAR("barbariantrainingmadespear"),
	BARBARIAN_TRAINING_MADE_HASTA("barbariantrainingmadehasta"),

	// Boaty guide
	CLAIMED_RUNES("claimed-runes"),
	SET_BANK_PIN("set-bank-pin")
	;

	@Getter
	final String key;

	ConfigKeys(String key)
	{
		this.key = key;
	}
}
