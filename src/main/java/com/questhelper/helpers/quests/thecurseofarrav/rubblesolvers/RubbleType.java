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
import net.runelite.api.ObjectID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public enum RubbleType
{
	Three(ObjectID.RUBBLE_50603, ObjectID.RUBBLE_50604),
	Two(ObjectID.RUBBLE_50598, ObjectID.RUBBLE_50602, ObjectID.RUBBLE_50601, ObjectID.RUBBLE_50599),
	One(ObjectID.RUBBLE_50587, ObjectID.RUBBLE_50589, ObjectID.RUBBLE_50590, ObjectID.RUBBLE_50594, ObjectID.RUBBLE_50597, ObjectID.RUBBLE_50588, ObjectID.RUBBLE_50593);

	private final List<Integer> objectIDs;

	RubbleType(Integer... possibleObjectIDs) {
		this.objectIDs = new ArrayList<>();
		Collections.addAll(this.objectIDs, possibleObjectIDs);
	}
}
