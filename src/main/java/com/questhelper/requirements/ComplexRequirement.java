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

import com.questhelper.requirements.util.LogicType;
import java.util.stream.Stream;
import lombok.Getter;
import net.runelite.api.Client;

/**
 * Requirement that combines multiple other {@link Requirement}s using
 * {@link LogicType} to determine if the requirement(s) is/are met.
 */
@Getter
public class ComplexRequirement extends AbstractRequirement
{
	private final Requirement[] requirements;
	private final LogicType logicType;
	private final String name;

	/**
	 * Requirement that combines multiple other {@link Requirement}s using
	 * {@link LogicType} to determine if the requirement(s) is/are met.
	 * <br>
	 * The default {@link LogicType} is {@link LogicType#AND}.
	 */
	public ComplexRequirement(String name, AbstractRequirement... requirements)
	{
		this.name = name;
		this.requirements = requirements;
		this.logicType = LogicType.AND;

		shouldCountForFilter = true;
		// If any sub-requirements shouldn't be considered for filtering, don't consider the
		// whole ComplexRequirement for filtering
		for (AbstractRequirement requirement : requirements)
		{
			if (!requirement.shouldConsiderForFilter())
			{
				shouldCountForFilter = false;
				break;
			}
		}
	}

	/**
	 * Requirement that combines multiple other {@link Requirement}s using
	 * {@link LogicType} to determine if the requirement(s) is/are met.
	 */
	public ComplexRequirement(LogicType logicType, String name, Requirement... requirements)
	{
		this.name = name;
		this.requirements = requirements;
		this.logicType = logicType;
	}

	@Override
	public boolean check(Client client)
	{
		if (logicType == null)
		{
			return false;
		}
		return logicType.test(Stream.of(requirements), r -> r.check(client));
	}

	@Override
	public String getDisplayText()
	{
		return name;
	}
}
