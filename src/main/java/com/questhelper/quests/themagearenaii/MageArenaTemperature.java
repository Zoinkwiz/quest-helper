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
package com.questhelper.quests.themagearenaii;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MageArenaTemperature
{
	VERY_COLD("is very cold", 200, 5000),
	COLD("is cold",  150, 199),
	HOT("is hot", 70, 149),
	VERY_HOT("is very hot", 30, 69),
	INCREDIBLY_HOT("is incredibly hot", 15, 29),
	SHAKING("is visibly shaking", 0, 14);

	public static final Set<MageArenaTemperature> temperatureSet = Sets.immutableEnumSet(
		VERY_COLD,
		COLD,
		HOT,
		VERY_HOT,
		INCREDIBLY_HOT,
		SHAKING
	);

	private final String text;
	private final int minDistance;
	private final int maxDistance;

	@Nullable
	public static MageArenaTemperature getFromTemperatureSet(final String message)
	{
		for (final MageArenaTemperature temperature : temperatureSet)
		{
			if (message.contains(temperature.getText()))
			{
				return temperature;
			}
		}

		return null;
	}
}
