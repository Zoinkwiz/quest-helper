/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
 * Copyright (c) 2019, Jordan Atwood <nightfirecat@protonmail.com>
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
package com.questhelper.quests.enchantedkey;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.util.Text;

@AllArgsConstructor
@Getter
public enum EnchantedKeyTemperature
{
	FREEZING("It's freezing", 500, 5000),
	COLD("It's cold",  120, 499),
	WARM("It's warm", 60, 119),
	VERY_HOT("It's very hot", 30, 59),
	BURNING("Ouch! It's burning hot", 5, 29),
	STEAMING("The key is steaming.", 0, 4);

	public static final Set<EnchantedKeyTemperature> temperatureSet = Sets.immutableEnumSet(
		FREEZING,
		COLD,
		WARM,
		VERY_HOT,
		BURNING,
		STEAMING
	);

	private final String text;
	private final int minDistance;
	private final int maxDistance;

	@Nullable
	public static EnchantedKeyTemperature getFromTemperatureSet(final String message)
	{
		for (final EnchantedKeyTemperature temperature : temperatureSet)
		{
			if (message.contains(temperature.getText()))
			{
				return temperature;
			}
		}

		return null;
	}
}