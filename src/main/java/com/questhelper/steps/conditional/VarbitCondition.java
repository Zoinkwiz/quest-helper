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

import java.math.BigInteger;
import net.runelite.api.Client;

public class VarbitCondition extends ConditionForStep
{

	private final int varbitId;
	private final int value;
	private final Operation operation;

	private final boolean bitIsSet;
	private final int bitPosition;

	public VarbitCondition(int varbitId, int value)
	{
		this.varbitId = varbitId;
		this.value = value;
		this.operation = Operation.EQUAL;

		this.bitPosition = -1;
		this.bitIsSet = false;
	}

	public VarbitCondition(int varbitId, int value, Operation operation)
	{
		this.varbitId = varbitId;
		this.value = value;
		this.operation = operation;

		this.bitPosition = -1;
		this.bitIsSet = false;
	}

	public VarbitCondition(int varbitId, boolean bitIsSet, int bitPosition)
	{
		this.varbitId = varbitId;
		this.value = -1;
		this.operation = Operation.EQUAL;

		this.bitPosition = bitPosition;
		this.bitIsSet = bitIsSet;
	}

	@Override
	public boolean checkCondition(Client client)
	{
		if (bitPosition >= 0)
		{
			return bitIsSet == BigInteger.valueOf(client.getVarbitValue(varbitId)).testBit(bitPosition);
		}

		if (operation == Operation.EQUAL)
		{
			return client.getVarbitValue(varbitId) == value;
		}
		else if (operation == Operation.NOT_EQUAL)
		{
			return client.getVarbitValue(varbitId) != value;
		}
		else if (operation == Operation.LESS_EQUAL)
		{
			return client.getVarbitValue(varbitId) <= value;
		}

		else if (operation == Operation.GREATER_EQUAL)
		{
			return client.getVarbitValue(varbitId) >= value;
		}

		return false;
	}
}
