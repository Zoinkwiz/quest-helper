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

import com.questhelper.steps.conditional.Operation;
import java.awt.Color;
import java.util.ArrayList;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.components.LineComponent;

public class WeightRequirement extends Requirement
{
	int weight;
	String text;
	Operation operation;

	public WeightRequirement(String text, int weight, Operation operation)
	{
		this.weight = weight;
		this.text = text;
		this.operation = operation;
	}

	@Override
	public boolean check(Client client)
	{
		if (operation == Operation.EQUAL)
		{
			return client.getWeight() == weight;
		}
		else if (operation == Operation.NOT_EQUAL)
		{
			return client.getWeight() != weight;
		}
		else if (operation == Operation.LESS_EQUAL)
		{
			return client.getWeight() <= weight;
		}
		else if (operation == Operation.GREATER_EQUAL)
		{
			return client.getWeight() >= weight;
		}
		return false;
	}

	@Override
	public ArrayList<LineComponent> getDisplayText(Client client)
	{
		ArrayList<LineComponent> lines = new ArrayList<>();

		Color color = Color.RED;
		if (check(client))
		{
			color = Color.GREEN;
		}

		lines.add(LineComponent.builder()
			.left(text)
			.leftColor(color)
			.build());

		return lines;
	}
}
