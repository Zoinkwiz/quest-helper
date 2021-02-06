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
package com.questhelper.requirements;

import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.util.LogicType;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import lombok.Getter;
import net.runelite.api.Client;

public class Requirements extends AbstractRequirement
{
	String name;

	@Getter
	ArrayList<Requirement> requirements = new ArrayList<>();

	@Getter
	LogicType logicType;

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
	@Nonnull
	public String getDisplayText()
	{
		return name;
	}

	@Override
	public boolean check(Client client)
	{
		Predicate<Requirement> predicate = r -> r.check(client);
		int successes = (int) requirements.stream().filter(predicate).count();
		return logicType.compare(successes, requirements.size());
	}

	@Override
	public Color getColor(Client client)
	{
		return this.check(client) ? Color.GREEN : Color.RED;
	}
}
