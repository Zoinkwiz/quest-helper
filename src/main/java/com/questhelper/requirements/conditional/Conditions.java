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
package com.questhelper.requirements.conditional;

import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.Setter;
import net.runelite.api.Client;

public class Conditions extends ConditionForStep
{
	protected Operation operation;
	protected int quantity;

	@Setter
	protected String text;

	public Conditions(Requirement... conditions)
	{
		this.conditions = new ArrayList<>();
		Collections.addAll(this.conditions, conditions);
		logicType = LogicType.AND;
	}

	public Conditions(List<Requirement> conditions)
	{
		this.conditions = new ArrayList<>(conditions);
		logicType = LogicType.AND;
	}

	public Conditions(LogicType logicType, Requirement... conditions)
	{
		this.conditions = new ArrayList<>();
		Collections.addAll(this.conditions, conditions);
		this.logicType = logicType;
	}

	public Conditions(Operation operation, int quantity, Requirement... conditions)
	{
		this.conditions = new ArrayList<>();
		Collections.addAll(this.conditions, conditions);
		this.logicType = LogicType.AND;
		this.operation = operation;
		this.quantity = quantity;
	}

	public Conditions(LogicType logicType, List<Requirement> conditions)
	{
		this.conditions = new ArrayList<>(conditions);
		this.logicType = logicType;
	}

	public Conditions(boolean onlyNeedToPassOnce, Operation operation, int quantity, Requirement... conditions)
	{
		this.conditions = new ArrayList<>();
		Collections.addAll(this.conditions, conditions);
		this.onlyNeedToPassOnce = onlyNeedToPassOnce;
		this.logicType = LogicType.AND;
		this.operation = operation;
		this.quantity = quantity;
	}

	public Conditions(boolean onlyNeedToPassOnce, LogicType logicType, Requirement... conditions)
	{
		this.conditions = new ArrayList<>();
		Collections.addAll(this.conditions, conditions);
		this.onlyNeedToPassOnce = onlyNeedToPassOnce;
		this.logicType = logicType;
	}

	public Conditions(boolean onlyNeedToPassOnce, Requirement... conditions)
	{
		this.conditions = new ArrayList<>();
		Collections.addAll(this.conditions, conditions);
		this.onlyNeedToPassOnce = onlyNeedToPassOnce;
		this.logicType = LogicType.AND;
	}

	@Override
	@Nonnull
	public String getDisplayText()
	{
		return text;
	}

	@Override
	public boolean check(Client client)
	{
		if (onlyNeedToPassOnce && hasPassed)
		{
			return true;
		}

		int conditionsPassed = (int) conditions.stream().filter(c -> {
			if (c == null)
			{
				return true;
			}
			return c.check(client);
		}).count();

		if (operation != null)
		{
			return operation.check(conditionsPassed, quantity);
		}

		//TODO: Replace with LogicType check, however more testing to be done to make sure nothing breaks
		if ((conditionsPassed > 0 && logicType == LogicType.OR)
			|| (conditionsPassed == 0 && logicType == LogicType.NOR)
			|| (conditionsPassed == conditions.size() && logicType == LogicType.AND)
			|| (conditionsPassed < conditions.size() && logicType == LogicType.NAND))
		{
			hasPassed = true;
			return true;
		}

		return false;
	}
}
