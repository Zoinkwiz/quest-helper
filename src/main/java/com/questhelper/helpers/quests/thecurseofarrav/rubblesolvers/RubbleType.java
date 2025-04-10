/*
 * Copyright (c) 2024, pajlada <https://github.com/pajlada>
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
package com.questhelper.helpers.quests.thecurseofarrav.rubblesolvers;

import lombok.Getter;
import net.runelite.api.gameval.ObjectID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public enum RubbleType
{
	Three(ObjectID.MAH3_TUNNEL_RUBBLE_MINE_3, ObjectID.MAH3_TUNNEL_RUBBLE_MINE_COLLAPSE),
	Two(ObjectID.MAH3_TUNNEL_RUBBLE_MINE_2, ObjectID.MAH3_RUBBLE_2_WEST, ObjectID.MAH3_RUBBLE_2_SOUTH, ObjectID.MAH3_RUBBLE_2_NORTH),
	One(ObjectID.MAH3_TUNNEL_RUBBLE_MINE_1, ObjectID.MAH3_RUBBLE_1_SOUTH, ObjectID.MAH3_RUBBLE_1_WEST, ObjectID.MAH3_RUBBLE_1_NORTH_WEST, ObjectID.MAH3_RUBBLE_1_SOUTH_WEST, ObjectID.MAH3_RUBBLE_1_NORTH, ObjectID.MAH3_RUBBLE_1_NORTH_SOUTH);

	private final List<Integer> objectIDs;

	RubbleType(Integer... possibleObjectIDs) {
		this.objectIDs = new ArrayList<>();
		Collections.addAll(this.objectIDs, possibleObjectIDs);
	}
}
