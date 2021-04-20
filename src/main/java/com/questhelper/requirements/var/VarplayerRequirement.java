/*
 *
 *  * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice, this
 *  *    list of conditions and the following disclaimer.
 *  * 2. Redistributions in binary form must reproduce the above copyright notice,
 *  *    this list of conditions and the following disclaimer in the documentation
 *  *    and/or other materials provided with the distribution.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.questhelper.requirements.var;

import com.questhelper.requirements.conditional.ConditionForStep;
import com.questhelper.requirements.util.Operation;
import java.math.BigInteger;
import java.util.Locale;
import net.runelite.api.Client;

public class VarplayerRequirement extends ConditionForStep
{

	private final int varplayerId;
	private final int value;
	private final Operation operation;
	private final String displayText;

	private final int bitPosition;
	private final boolean bitIsSet;

	public VarplayerRequirement(int varplayerId, int value)
	{
		this.varplayerId = varplayerId;
		this.value = value;
		this.operation = Operation.EQUAL;
		this.displayText = null;

		this.bitPosition = -1;
		this.bitIsSet = false;
	}

	public VarplayerRequirement(int varplayerId, int value, String displayText)
	{
		this.varplayerId = varplayerId;
		this.value = value;
		this.operation = Operation.EQUAL;
		this.displayText = displayText;

		this.bitPosition = -1;
		this.bitIsSet = false;
	}

	public VarplayerRequirement(int varplayerId, int value, Operation operation)
	{
		this.varplayerId = varplayerId;
		this.value = value;
		this.operation = operation;
		this.displayText = null;

		this.bitPosition = -1;
		this.bitIsSet = false;
	}

	public VarplayerRequirement(int varplayerId, int value, Operation operation, String displayText)
	{
		this.varplayerId = varplayerId;
		this.value = value;
		this.operation = operation;
		this.displayText = displayText;

		this.bitPosition = -1;
		this.bitIsSet = false;
	}

	public VarplayerRequirement(int varplayerId, boolean bitIsSet, int bitPosition)
	{
		this.varplayerId = varplayerId;
		this.value = -1;
		this.operation = Operation.EQUAL;
		this.displayText = null;

		this.bitPosition = bitPosition;
		this.bitIsSet = bitIsSet;
	}

	public VarplayerRequirement(int varplayerId, boolean bitIsSet, int bitPosition, String displayText)
	{
		this.varplayerId = varplayerId;
		this.value = -1;
		this.operation = Operation.EQUAL;
		this.displayText = displayText;

		this.bitPosition = bitPosition;
		this.bitIsSet = bitIsSet;
	}


	@Override
	public boolean check(Client client)
	{
		if (bitPosition >= 0)
		{
			return bitIsSet == BigInteger.valueOf(client.getVarpValue(varplayerId)).testBit(bitPosition);
		}
		return operation.check(client.getVarpValue(varplayerId), value);
	}

	@Override
	public String getDisplayText()
	{
		if (displayText != null)
		{
			return displayText;
		}
		if (bitPosition >= 0)
		{
			return varplayerId + " must have the " + bitPosition + " bit set.";
		}
		return varplayerId + " must be + " + operation.name().toLowerCase(Locale.ROOT) + " " + value;
	}
}
