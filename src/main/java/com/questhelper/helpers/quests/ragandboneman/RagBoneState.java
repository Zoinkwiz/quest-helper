/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.ragandboneman;

import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.util.Text;

@AllArgsConstructor
public enum RagBoneState
{
	GOBLIN_SKULL("Goblin Skull", 1, ItemID.RAG_GOBLIN_BONE, ItemID.RAG_POT_GOBLIN_BONE, ItemID.RAG_POLISHED_GOBLIN_BONE),
	BEAR_RIBS("Bear Ribs", 2, ItemID.RAG_BEAR_BONE, ItemID.RAG_POT_BEAR_BONE, ItemID.RAG_POLISHED_BEAR_BONE),
	RAM_SKULL("Ram Skull", 3, ItemID.RAG_RAM_BONE, ItemID.RAG_POT_RAM_BONE, ItemID.RAG_POLISHED_RAM_BONE),
	UNICORN_BONE("Unicorn Bone", 4, ItemID.RAG_UNICORN_BONE, ItemID.RAG_POT_UNICORN_BONE, ItemID.RAG_POLISHED_UNICORN_BONE),
	GIANT_RAT_BONE("Giant Rat Bone", 5, ItemID.RAG_GIANT_RAT_BONE, ItemID.RAG_POT_GIANT_RAT_BONE, ItemID.RAG_POLISHED_GIANT_RAT_BONE),
	GIANT_BAT_WING("Giant Bat Wing", 6, ItemID.RAG_GIANT_BAT_BONE, ItemID.RAG_POT_GIANT_BAT_BONE, ItemID.RAG_POLISHED_GIANT_BAT_BONE),

	WOLF_BONE("Wolf", 7, ItemID.RAG_WOLF_BONE, ItemID.RAG_POT_WOLF_BONE, ItemID.RAG_POLISHED_WOLF_BONE),
	BAT_WING("Bat", 8, ItemID.RAG_BAT_BONE, ItemID.RAG_POT_BAT_BONE, ItemID.RAG_POLISHED_BAT_BONE),
	RAT_BONE("Rat", 9, ItemID.RAG_RAT_BONE, ItemID.RAG_POT_RAT_BONE, ItemID.RAG_POLISHED_RAT_BONE),
	BABY_DRAGON_BONE("Baby Blue Dragon", 10, ItemID.RAG_BABY_BLUE_DRAGON_BONE, ItemID.RAG_POT_BABY_BLUE_DRAGON_BONE, ItemID.RAG_POLISHED_BABY_BLUE_DRAGON_BONE),
	OGRE_RIBS("Ogre Ribs", 11, ItemID.RAG_OGRE_BONE, ItemID.RAG_POT_OGRE_BONE, ItemID.RAG_POLISHED_OGRE_BONE),
	JOGRE_BONE("Jogre", 12, ItemID.RAG_JOGRE_BONE, ItemID.RAG_POT_JOGRE_BONE, ItemID.RAG_POLISHED_JOGRE_BONE),
	ZOGRE_BONE("Zogre", 13, ItemID.RAG_ZOGRE_BONE, ItemID.RAG_POT_ZOGRE_BONE, ItemID.RAG_POLISHED_ZOGRE_BONE),
	MOGRE_BONE("Mogre", 14, ItemID.RAG_MOGRE_BONE, ItemID.RAG_POT_MOGRE_BONE, ItemID.RAG_POLISHED_MOGRE_BONE),

	// P1
	MONKEY_PAW("Monkey Paw", 15, ItemID.RAG_MONKEY_BONE, ItemID.RAG_POT_MONKEY_BONE, ItemID.RAG_POLISHED_MONKEY_BONE),

	// P2
	DAGANNOTH_RIBS("Dagannoth", 16, ItemID.RAG_DAGGANOTH_BONE, ItemID.RAG_POT_DAGGANOTH_BONE, ItemID.RAG_POLISHED_DAGGANOTH_BONE),
	SNAKE_SPINE("Snake", 17, ItemID.RAG_SNAKE_BONE, ItemID.RAG_POT_SNAKE_BONE, ItemID.RAG_POLISHED_SNAKE_BONE),
	ZOMBIE_BONE("Zombie", 18, ItemID.RAG_ZOMBIE_BONE, ItemID.RAG_POT_ZOMBIE_BONE, ItemID.RAG_POLISHED_ZOMBIE_BONE),
	WEREWOLF_BONE("Werewolf", 19, ItemID.RAG_WEREWOLF_BONE, ItemID.RAG_POT_WEREWOLF_BONE, ItemID.RAG_POLISHED_WEREWOLF_BONE),
	MOSS_GIANT_BONE("Moss Giant", 20, ItemID.RAG_MOSS_GIANT_BONE, ItemID.RAG_POT_MOSS_GIANT_BONE, ItemID.RAG_POLISHED_MOSS_GIANT_BONE),
	FIRE_GIANT_BONE("Fire Giant", 21, ItemID.RAG_FIRE_GIANT_BONE, ItemID.RAG_POT_FIRE_GIANT_BONE, ItemID.RAG_POLISHED_FIRE_GIANT_BONE),
	ICE_GIANT_RIBS("Ice Giant", 22, ItemID.RAG_ICE_GIANT_BONE, ItemID.RAG_POT_ICE_GIANT_BONE, ItemID.RAG_POLISHED_ICE_GIANT_BONE),
	TERRORBIRD_WING("Terrorbird", 23, ItemID.RAG_TERRORBIRD_BONE, ItemID.RAG_POT_TERRORBIRD_BONE, ItemID.RAG_POLISHED_TERRORBIRD_BONE),
	GHOUL_BONE("Ghoul", 24, ItemID.RAG_GHOUL_BONE, ItemID.RAG_POT_GHOUL_BONE, ItemID.RAG_POLISHED_GHOUL_BONE),
	TROLL_BONE("Troll", 25, ItemID.RAG_TROLL_BONE, ItemID.RAG_POT_TROLL_BONE, ItemID.RAG_POLISHED_TROLL_BONE),
	SEAGULL_WING("Seagull", 26, ItemID.RAG_SEAGULL_BONE, ItemID.RAG_POT_SEAGULL_BONE, ItemID.RAG_POLISHED_SEAGULL_BONE),
	UNDEAD_COW_RIBS("Undead Cow", 27, ItemID.RAG_UNDEAD_COW_BONE, ItemID.RAG_POT_UNDEAD_COW_BONE, ItemID.RAG_POLISHED_UNDEAD_COW_BONE),
	EXPERIMENT_BONE("Experiment", 28, ItemID.RAG_EXPERIMENT_BONE, ItemID.RAG_POT_EXPERIMENT_BONE, ItemID.RAG_POLISHED_EXPERIMENT_BONE),
	RABBIT_BONE("Rabbit", 29, ItemID.RAG_RABBIT_BONE, ItemID.RAG_POT_RABBIT_BONE, ItemID.RAG_POLISHED_RABBIT_BONE),
	BASILISK_BONE("Basilisk", 30, ItemID.RAG_BABY_BASILISK_BONE, ItemID.RAG_POT_BABY_BASILISK_BONE, ItemID.RAG_POLISHED_BABY_BASILISK_BONE),
	DESERT_LIZARD_BONE("Massive Desert Lizard", 31, ItemID.RAG_DESERT_LIZARD_BONE, ItemID.RAG_POT_DESERT_LIZARD_BONE, ItemID.RAG_POLISHED_DESERT_LIZARD_BONE),
	CAVE_GOBLIN_SKULL("Cave Goblin", 32, ItemID.RAG_CAVE_GOBLIN_BONE, ItemID.RAG_POT_CAVE_GOBLIN_BONE, ItemID.RAG_POLISHED_CAVE_GOBLIN_BONE),

	// P1
	BIG_FROG_LEG("Big Frog Leg", 33, ItemID.RAG_MEDIUM_FROG_BONE, ItemID.RAG_POT_MEDIUM_FROG_BONE, ItemID.RAG_POLISHED_MEDIUM_FROG_BONE),

	// P2
	VULTURE_WING("Vulture", 34, ItemID.RAG_VULTURE_BONE, ItemID.RAG_POT_VULTURE_BONE, ItemID.RAG_POLISHED_VULTURE_BONE),
	JACKAL_BONE("Jackal", 35, ItemID.RAG_JACKAL_BONE, ItemID.RAG_POT_JACKAL_BONE, ItemID.RAG_POLISHED_JACKAL_BONE);


	@Getter
	private final ItemRequirement boneItem;
	@Getter
	private final ItemRequirement boneInVinegarItem;
	@Getter
	private final ItemRequirement boneCleanedItem;

	private final Requirement boneIsBeingCleaned;

	private final Conditions hadFromWidgets;

	private final Requirement hadBoneItem;
	private final Requirement hadBoneInVinegar;
	private final Requirement hadBoneProcessed;

	RagBoneState(String nameInList, int boilerVarbitValue, int boneID, int potBoneID, int polishedBoneID)
	{
		// Mark always true once obtained as no way to identify if a bone has been handed in easily
		var hadFromWidgetsCheck = new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "<str>" + nameInList);
		hadFromWidgets = new Conditions(true, hadFromWidgetsCheck);

		boneItem = new ItemRequirement(Text.titleCase(this), boneID);
		boneItem.addAlternates(potBoneID, polishedBoneID);

		boneInVinegarItem = new ItemRequirement("Bone in vinegar (" + Text.titleCase(this) + ")", potBoneID);
		boneInVinegarItem.addAlternates(polishedBoneID);

		boneCleanedItem = new ItemRequirement(Text.titleCase(this), polishedBoneID);

		hadBoneProcessed = or(
			boneCleanedItem.alsoCheckBank(),
			hadFromWidgets
		);

		var boneAddedToBoiler = new VarbitRequirement(VarbitID.RAG_BOILER, 2, Operation.GREATER_EQUAL);
		boneIsBeingCleaned = new Conditions(boneAddedToBoiler, new VarbitRequirement(VarbitID.RAG_POTBOILER, boilerVarbitValue));

		hadBoneInVinegar = or(
			hadBoneProcessed,
			boneIsBeingCleaned,
			boneInVinegarItem.alsoCheckBank()
		);

		hadBoneItem = or(
			hadBoneInVinegar,
			boneItem.alsoCheckBank()
		);
	}

	public Requirement hadBoneItem()
	{
		return hadBoneItem;
	}

	public Requirement hadBoneInVinegarItem()
	{
		return hadBoneInVinegar;
	}

	public Requirement boneProcessed()
	{
		return hadBoneProcessed;
	}

	public Requirement handedInBone()
	{
		return hadFromWidgets;
	}
}
