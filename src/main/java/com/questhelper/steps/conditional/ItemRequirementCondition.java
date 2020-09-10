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
package com.questhelper.steps.conditional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.runelite.api.Client;
import com.questhelper.requirements.ItemRequirement;

public class ItemRequirementCondition extends ConditionForStep
{
	private final List<ItemRequirement> itemRequirements;

	private Operation comparisonType;
	private int compareValue;

	public ItemRequirementCondition(ItemRequirement... itemRequirements)
	{
		this.itemRequirements = new ArrayList<>();
		this.itemRequirements.addAll(Arrays.asList(itemRequirements));
		this.logicType = LogicType.AND;
	}

	public ItemRequirementCondition(LogicType logicType,  ItemRequirement... itemRequirements)
	{
		this.itemRequirements = new ArrayList<>();
		this.itemRequirements.addAll(Arrays.asList(itemRequirements));
		this.logicType = logicType;
	}


	public ItemRequirementCondition(Operation operation, int compareValue, ItemRequirement... itemRequirements)
	{
		this.itemRequirements = new ArrayList<>();
		this.itemRequirements.addAll(Arrays.asList(itemRequirements));
		this.logicType = LogicType.AND;
		this.compareValue = compareValue;
		this.comparisonType = operation;
	}

	@Override
	public boolean checkCondition(Client client)
	{
		int successes = 0;
		for (ItemRequirement itemRequirement : itemRequirements)
		{
			if (itemRequirement.checkConsideringSlot(client))
			{
				successes++;
			}
		}
		if (comparisonType != null)
		{
			if (comparisonType == Operation.EQUAL)
			{
				return successes == compareValue;
			}
			if (comparisonType == Operation.NOT_EQUAL)
			{
				return successes != compareValue;
			}
			else if (comparisonType == Operation.GREATER_EQUAL)
			{
				return successes >= compareValue;
			}
			else if (comparisonType == Operation.LESS_EQUAL)
			{
				return successes <= compareValue;
			}
		}

		return (successes == itemRequirements.size() && logicType == LogicType.AND)
			|| (successes > 0 && logicType == LogicType.OR)
			|| (successes < itemRequirements.size() && logicType == LogicType.NAND)
			|| (successes == 0 && logicType == LogicType.NOR);
	}
}