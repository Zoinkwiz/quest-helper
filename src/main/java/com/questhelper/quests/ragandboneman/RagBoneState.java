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
package com.questhelper.quests.ragandboneman;

import com.questhelper.QuestBank;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.WidgetTextRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.util.Text;

@AllArgsConstructor
@Getter
public enum RagBoneState
{
	GOBLIN_SKULL("Goblin Skull", 1, 34),
	BEAR_RIBS("Bear Ribs", 2, 35),
	RAM_SKULL("Ram Skull", 3, 37),
	UNICORN_BONE("Unicorn Bone", 4, 38),
	GIANT_RAT_BONE("Giant Rat Bone", 5, 40),
	GIANT_BAT_WING("Giant Bat Wing", 6, 41),

	WOLF_BONE("Wolf", 7, 7),
	BAT_WING("Bat", 8, 8),
	RAT_BONE("Rat", 9, 9),
	BABY_DRAGON_BONE("Baby Blue Dragon", 10, 10),
	OGRE_BONE("Ogre", 11, 11),
	JOGRE_BONE("Jogre", 12, 12),
	ZOGRE_BONE("Zogre", 13, 13),
	MOGRE_BONE("Mogre", 14, 14),

	// P1
	MONKEY_PAW("Monkey Paw",15,39),

	// P2
	DAGANNOTH_RIBS("Dagannoth", 16, 15),
	SNAKE_SPINE("Snake", 17, 16),
	ZOMBIE_BONE("Zombie", 18, 17),
	WEREWOLF_BONE("Werewolf", 19, 18),
	MOSS_GIANT_BONE("Moss Giant", 20, 19),
	FIRE_GIANT_BONE("Fire Giant", 21, 20),
	ICE_GIANT_RIBS("Ice Giant", 22, 21),
	TERRORBIRD_WING("Terrorbird", 23, 22),
	GHOUL_BONE("Ghoul", 24, 23),
	TROLL_BONE("Troll", 25, 24),
	SEAGULL_WING("Seagull", 26, 25),
	UNDEAD_COW_RIBS("Undead Cow", 27, 26),
	EXPERIMENT_BONE("Experiment", 28, 27),
	RABBIT_BONE("Rabbit", 29, 28),
	BASILISK_BONE("Basilisk", 30, 29),
	DESERT_LIZARD_BONE("Massive Desert Lizard", 31, 30),
	CAVE_GOBLIN_SKULL("Cave Goblin", 32, 31),

	// P1
	BIG_FROG_LEGS("Big Frog Legs",33,36),

	// P2
	VULTURE_WING("Vulture", 34, 32),
	JACKAL_BONE("Jackal", 35, 33);

	private final int START_BONE_ID = 7809;

	private final int boneID;
	private final int widgetID;

	private final ItemRequirement boneItem;
	private final ItemRequirement boneInVinegarItem;
	private final ItemRequirement boneCleanedItem;

	private final Requirement boneIsBeingCleaned;

	private final Conditions hadFromWidgets;

	private Requirement hadBoneItem;
	private Requirement hadBoneInVinegar;
	private Requirement hadBoneProcessed;

	RagBoneState(String nameInList, int pos, int widgetID)
	{
		boneID = START_BONE_ID + (pos * 3);
		this.widgetID = widgetID;

		boneItem = new ItemRequirement(Text.titleCase(this), boneID);
		boneItem.addAlternates(boneID + 1, boneID + 2);

		boneInVinegarItem = new ItemRequirement("Bone in vinegar (" + Text.titleCase(this) + ")", boneID + 1);
		boneInVinegarItem.addAlternates(boneID + 2);

		boneCleanedItem = new ItemRequirement(Text.titleCase(this), boneID + 2);

		VarbitRequirement boneAddedToBoiler = new VarbitRequirement(2046, 2, Operation.GREATER_EQUAL);
		boneIsBeingCleaned = new Conditions(boneAddedToBoiler, new VarbitRequirement(2043, pos));

		// Mark always true once obtained as no way to identify if a bone has been handed in easily
		WidgetTextRequirement hadFromWidgetsCheck = new WidgetTextRequirement(119, widgetID, "<str>" + nameInList);
		hadFromWidgets = new Conditions(true, hadFromWidgetsCheck);
	}

	public Requirement hadBoneItem(QuestBank questBank)
	{
		if (hadBoneItem == null)
		{
			hadBoneItem = new Conditions(LogicType.OR,
				hadBoneInVinegarItem(questBank),
				boneItem.alsoCheckBank(questBank)
			);
		}

		return hadBoneItem;
	}

	public Requirement hadBoneInVinegarItem(QuestBank questBank)
	{
		if (hadBoneInVinegar == null)
		{
			hadBoneInVinegar = new Conditions(LogicType.OR,
				boneProcessed(questBank),
				boneIsBeingCleaned,
				boneInVinegarItem.alsoCheckBank(questBank)
			);
		}

		return hadBoneInVinegar;
	}

	public Requirement boneProcessed(QuestBank questBank)
	{
		if (hadBoneProcessed == null)
		{
			hadBoneProcessed = new Conditions(LogicType.OR,
				boneCleanedItem.alsoCheckBank(questBank),
				handedInBone()
			);
		}
		return hadBoneProcessed;
	}

	public Requirement handedInBone()
	{
		return hadFromWidgets;
	}
}
