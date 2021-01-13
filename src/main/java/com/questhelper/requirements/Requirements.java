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

import com.questhelper.steps.conditional.LogicType;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.components.LineComponent;

public class Requirements extends Requirement
{
	ArrayList<Requirement> requirements = new ArrayList<>();
	LogicType logicType;
	String name;

	public Requirements(String name, Requirement... requirements)
	{
		this.name = name;
		this.requirements.addAll(Arrays.asList(requirements));
		this.logicType = LogicType.AND;
	}

	public Requirements(LogicType logicType, String name, Requirement... requirements)
	{
		this.name = name;
		this.requirements.addAll(Arrays.asList(requirements));
		this.logicType = logicType;
	}

	@Override
	public boolean check(Client client)
	{
		int successes = 0;
		for (Requirement requirement : requirements)
		{
			if (requirement.check(client))
			{
				successes++;
			}
		}
		return (successes == requirements.size() && logicType == LogicType.AND)
			|| (successes > 0 && logicType == LogicType.OR)
			|| (successes < requirements.size() && logicType == LogicType.NAND)
			|| (successes == 0 && logicType == LogicType.NOR);
	}

	@Override
	public ArrayList<LineComponent> getDisplayTextWithChecks(Client client)
	{
		ArrayList<LineComponent> lines = new ArrayList<>();

		Color color = Color.RED;
		if (check(client))
		{
			color = Color.GREEN;
		}

		lines.add(LineComponent.builder()
			.left(getDisplayText())
			.leftColor(color)
			.build());

		return lines;
	}

	@Override
	public String getDisplayText()
	{
		return name;
	}

	public Color getColor(Client client)
	{
		Color color;

		if (this.check(client))
		{
			color = Color.GREEN;
		}
		else
		{
			color = Color.RED;
		}
		return color;
	}
}
