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

public enum ArceuusSpells implements Spell
{
	ARCEUUS_HOME_TELEPORT("Arceuus Home Teleport"),
	ARCEUUS_LIBRARY_TELEPORT("Arceuus Library Teleport"),
	BASIC_REANIMATION("Basic Reanimation"),
	DRAYNOR_MANOR_TELEPORT("Draynor Manor Teleport"),
	BATTLEFRONT_TELEPORT("Battlefront Teleport"),
	MIND_ALTAR_TELEPORT("Mind Altar Teleport"),
	RESPAWN_TELEPORT("Respawn Teleport"),
	GHOSTLY_GRASP("Ghostly Grasp"),
	RESURRECT_LESSER_GHOST("Resurrect Lesser Ghost"),
	RESURRECT_LESSER_SKELETON("Resurrect Lesser Skeleton"),
	RESURRECT_LESSER_ZOMBIE("Resurrect Lesser Zombie"),
	SALVE_GRAVEYARD_TELEPORT("Salve Graveyard Teleport"),
	ADEPT_REANIMATION("Adept Reanimation"),
	INFERIOR_DEMONBANE("Inferior Demonbane"),
	SHADOW_VEIL("Shadow Veil"),
	FENKENSTRAINS_CASTLE_TELEPORT("Fenkenstrain's Castle Teleport"),
	DARK_LURE("Dark Lure"),
	SKELETAL_GRASP("Skeletal Grasp"),
	RESURRECT_SUPERIOR_GHOST("Resurrect Superior Ghost"),
	RESURRECT_SUPERIOR_SKELETON("Resurrect Superior Skeleton"),
	RESURRECT_SUPERIOR_ZOMBIE("Resurrect Superior Zombie"),
	MARK_OF_DARKNESS("Mark of Darkness"),
	WEST_ARDOUGNE_TELEPORT("West Ardougne Teleport"),
	SUPERIOR_DEMONBANE("Superior Demonbane"),
	LESSER_CORRUPTION("Lesser Corruption"),
	HARMONY_ISLAND_TELEPORT("Harmony Island Teleport"),
	VILE_VIGOUR("Vile Vigour"),
	DEGRIME("Degrime"),
	CEMETERY_TELEPORT("Cemetery Teleport"),
	EXPERT_REANIMATION("Expert Reanimation"),
	WARD_OF_ARCEUUS("Ward of Arceuus"),
	RESURRECT_GREATER_GHOST("Resurrect Greater Ghost"),
	RESURRECT_GREATER_SKELETON("Resurrect Greater Skeleton"),
	RESURRECT_GREATER_ZOMBIE("Resurrect Greater Zombie"),
	RESURRECT_CROPS("Resurrect Crops"),
	UNDEAD_GRASP("Undead Grasp"),
	DEATH_CHARGE("Death Charge"),
	DARK_DEMONBANE("Dark Demonbane"),
	BARROWS_TELEPORT("Barrows Teleport"),
	DEMONIC_OFFERING("Demonic Offering"),
	GREATER_CORRUPTION("Greater Corruption"),
	MASTER_REANIMATION("Master Reanimation"),
	APE_ATOLL_TELEPORT("Ape Atoll Teleport"),
	SINISTER_OFFERING("Sinister Offering");

	private final String spellName;

	ArceuusSpells(String spellName)
	{
		this.spellName = spellName;
	}

	@Override
	public String getSpellName()
	{
		return spellName;
	}
}
