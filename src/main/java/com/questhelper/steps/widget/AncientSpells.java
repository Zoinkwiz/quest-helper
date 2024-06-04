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

public enum AncientSpells implements Spell
{
	EDGEVILLE_HOME_TELEPORT("Edgeville Home Teleport"),
	SMOKE_RUSH("Smoke Rush"),
	SHADOW_RUSH("Shadow Rush"),
	PADDEWWA_TELEPORT("Paddewwa Teleport"),
	BLOOD_RUSH("Blood Rush"),
	ICE_RUSH("Ice Rush"),
	SENNTISTEN_TELEPORT("Senntisten Teleport"),
	SMOKE_BURST("Smoke Burst"),
	SHADOW_BURST("Shadow Burst"),
	KHARYRLL_TELEPORT("Kharyrll Teleport"),
	BLOOD_BURST("Blood Burst"),
	ICE_BURST("Ice Burst"),
	LASSAR_TELEPORT("Lassar Teleport"),
	SMOKE_BLITZ("Smoke Blitz"),
	SHADOW_BLITZ("Shadow Blitz"),
	DAREEYAK_TELEPORT("Dareeyak Teleport"),
	BLOOD_BLITZ("Blood Blitz"),
	ICE_BLITZ("Ice Blitz"),
	CARRALLANGER_TELEPORT("Carrallanger Teleport"),
	TELEPORT_TO_TARGET("Teleport to Target"),
	SMOKE_BARRAGE("Smoke Barrage"),
	SHADOW_BARRAGE("Shadow Barrage"),
	ANNAKARL_TELEPORT("Annakarl Teleport"),
	BLOOD_BARRAGE("Blood Barrage"),
	ICE_BARRAGE("Ice Barrage"),
	GHORROCK_TELEPORT("Ghorrock Teleport");

	private final String spellName;

	AncientSpells(String spellName)
	{
		this.spellName = spellName;
	}

	@Override
	public String getSpellName()
	{
		return spellName;
	}
}
