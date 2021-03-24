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
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.util.LogicType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RagBoneGroups
{
	public static List<RagBoneState> getRagBoneIStates()
	{
		return Arrays.asList(RagBoneState.GIANT_RAT_BONE, RagBoneState.UNICORN_BONE,
			RagBoneState.BEAR_RIBS, RagBoneState.RAM_SKULL, RagBoneState.GOBLIN_SKULL, RagBoneState.BIG_FROG_LEGS,
			RagBoneState.MONKEY_PAW, RagBoneState.GIANT_BAT_WING);
	}

	public static List<RagBoneState> getRagBoneIIStates()
	{
		return Arrays.asList(RagBoneState.BAT_WING, RagBoneState.UNDEAD_COW_RIBS,
			RagBoneState.EXPERIMENT_BONE, RagBoneState.WEREWOLF_BONE, RagBoneState.GHOUL_BONE,
			RagBoneState.ZOMBIE_BONE, RagBoneState.RAT_BONE, RagBoneState.MOSS_GIANT_BONE,
			RagBoneState.CAVE_GOBLIN_SKULL, RagBoneState.JACKAL_BONE, RagBoneState.SNAKE_SPINE,
			RagBoneState.DESERT_LIZARD_BONE, RagBoneState.VULTURE_WING, RagBoneState.SEAGULL_WING,
			RagBoneState.ICE_GIANT_RIBS, RagBoneState.MOGRE_BONE, RagBoneState.JOGRE_BONE,
			RagBoneState.BABY_DRAGON_BONE, RagBoneState.TROLL_BONE, RagBoneState.RABBIT_BONE,
			RagBoneState.BASILISK_BONE, RagBoneState.DAGANNOTH_RIBS, RagBoneState.FIRE_GIANT_BONE,
			RagBoneState.TERRORBIRD_WING, RagBoneState.WOLF_BONE, RagBoneState.OGRE_BONE,
			RagBoneState.ZOGRE_BONE);
	}

	public static List<ItemRequirement> getBones(List<RagBoneState> states)
	{
		List<ItemRequirement> bones = new ArrayList<>();
		for (RagBoneState ragBoneState : states)
		{
			bones.add(ragBoneState.getBoneItem());
		}
		return bones;
	}

	public static List<Requirement> getBonesOnFloor(List<ItemRequirement> bones)
	{
		List<Requirement> bonesOnFloor = new ArrayList<>();
		for (ItemRequirement bone : bones)
		{
			bonesOnFloor.add(new ItemOnTileRequirement(bone));
		}
		return bonesOnFloor;
	}

	public static List<Requirement> allBonesObtained(List<RagBoneState> states, QuestBank questBank)
	{
		List<Requirement> boneReq = new ArrayList<>();
		for (RagBoneState ragBoneIState : states)
		{
			boneReq.add(ragBoneIState.hadBoneItem(questBank));
		}
		return boneReq;
	}

	public static List<Requirement> allBonesAddedToVinegar(List<RagBoneState> states, QuestBank questBank)
	{
		List<Requirement> boneReq = new ArrayList<>();
		for (RagBoneState ragBoneIState : states)
		{
			boneReq.add(ragBoneIState.hadBoneInVinegarItem(questBank));
		}
		return boneReq;
	}

	public static List<Requirement> allBonesPolished(List<RagBoneState> states, QuestBank questBank)
	{
		List<Requirement> boneReq = new ArrayList<>();
		for (RagBoneState ragBoneIState : states)
		{
			boneReq.add(ragBoneIState.boneProcessed(questBank));
		}
		return boneReq;
	}

	public static List<ItemRequirement> pickupBones(List<RagBoneState> states)
	{
		List<ItemRequirement> boneReq = new ArrayList<>();
		for (RagBoneState ragBoneIState : states)
		{
			boneReq.add(ragBoneIState.getBoneItem().hideConditioned(
				new Conditions(LogicType.NOR, new ItemOnTileRequirement(ragBoneIState.getBoneItem()))
			));
		}
		return boneReq;
	}

	public static List<ItemRequirement> bonesToAddToVinegar(List<RagBoneState> states, QuestBank questBank)
	{
		List<ItemRequirement> boneReq = new ArrayList<>();
		for (RagBoneState ragBoneIState : states)
		{
			boneReq.add(ragBoneIState.getBoneItem().hideConditioned(ragBoneIState.hadBoneInVinegarItem(questBank)).highlighted());
		}
		return boneReq;
	}

	public static List<ItemRequirement> bonesToAddToBoiler(List<RagBoneState> states, QuestBank questBank)
	{
		List<ItemRequirement> boneReq = new ArrayList<>();
		for (RagBoneState ragBoneIState : states)
		{
			boneReq.add(ragBoneIState.getBoneInVinegarItem().hideConditioned(
				new Conditions(LogicType.OR,
					ragBoneIState.boneProcessed(questBank)
				)
			).highlighted());
		}
		return boneReq;
	}

	public static List<ItemRequirement> dirtyBonesNotHandedIn(List<RagBoneState> states)
	{
		List<ItemRequirement> boneReq = new ArrayList<>();
		for (RagBoneState ragBoneIState : states)
		{
			boneReq.add(ragBoneIState.getBoneItem().hideConditioned(ragBoneIState.handedInBone()));
		}
		return boneReq;
	}

	public static List<ItemRequirement> vinegarBonesNotHandedIn(List<RagBoneState> states)
	{
		List<ItemRequirement> boneReq = new ArrayList<>();
		for (RagBoneState ragBoneIState : states)
		{
			boneReq.add(ragBoneIState.getBoneInVinegarItem().hideConditioned(ragBoneIState.handedInBone()));
		}
		return boneReq;
	}

	public static List<ItemRequirement> cleanBonesNotHandedIn(List<RagBoneState> states)
	{
		List<ItemRequirement> boneReq = new ArrayList<>();
		for (RagBoneState ragBoneIState : states)
		{
			boneReq.add(ragBoneIState.getBoneCleanedItem().hideConditioned(ragBoneIState.handedInBone()));
		}
		return boneReq;
	}
}
