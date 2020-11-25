/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.requirements;

public enum ItemSlots
{
	ANY_EQUIPPED_AND_INVENTORY(-3, "equipped or your inventory slots"),
	ANY_INVENTORY(-2, "inventory slots"),
	ANY_EQUIPPED(-1, "equipped slots"),
	HEAD(0, "head slot"),
	CAPE(1, "cape slot"),
	AMULET(2, "amulet slot"),
	WEAPON(3, "weapon slot"),
	BODY(4, "body slot"),
	SHIELD(5, "shield slot"),
	LEGS(7, "legs slot"),
	GLOVES(9, "gloves slot"),
	BOOTS(10, "boots slot"),
	RING(12, "ring slot"),
	AMMO(13, "ammo slot");

	private final int slotIdx;
	private final String name;

	ItemSlots(int slotIdx, String name)
	{
		this.slotIdx = slotIdx;
		this.name = name;
	}

	public int getSlotIdx()
	{
		return slotIdx;
	}

	public String getName()
	{
		return name;
	}

	public static String getById(int id) {
		for(ItemSlots itemSlot : values()) {
			if(itemSlot.slotIdx == id) return itemSlot.getName();
		}
		return ANY_EQUIPPED_AND_INVENTORY.getName();
	}
}