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

import com.questhelper.requirements.AbstractRequirement;
import com.questhelper.requirements.util.Operation;
import net.runelite.api.Client;

import javax.annotation.Nonnull;
import java.math.BigInteger;
import java.util.List;
import java.util.Locale;

public class VarplayerRequirement extends AbstractRequirement
{

	private final int VarPlayerID;
	private final List<Integer> values;
	private final Operation operation;
	private final String displayText;

	private final int bitPosition;
	private final boolean bitIsSet;

	private final int bitShiftRight;

	public VarplayerRequirement(int VarPlayerID, int value)
	{
		this.VarPlayerID = VarPlayerID;
		this.values = List.of(value);
		this.operation = Operation.EQUAL;
		this.displayText = null;

		this.bitPosition = -1;
		this.bitIsSet = false;
		this.bitShiftRight = -1;
		shouldCountForFilter = true;
	}

	public VarplayerRequirement(int VarPlayerID, int value, String displayText)
	{
		this.VarPlayerID = VarPlayerID;
		this.values = List.of(value);
		this.operation = Operation.EQUAL;
		this.displayText = displayText;

		this.bitPosition = -1;
		this.bitIsSet = false;
		this.bitShiftRight = -1;
		shouldCountForFilter = true;
	}

	public VarplayerRequirement(int VarPlayerID, int value, Operation operation)
	{
		this.VarPlayerID = VarPlayerID;
		this.values = List.of(value);
		this.operation = operation;
		this.displayText = null;

		this.bitPosition = -1;
		this.bitIsSet = false;
		this.bitShiftRight = -1;
		shouldCountForFilter = true;
	}

	public VarplayerRequirement(int VarPlayerID, int value, Operation operation, String displayText)
	{
		this.VarPlayerID = VarPlayerID;
		this.values = List.of(value);
		this.operation = operation;
		this.displayText = displayText;

		this.bitPosition = -1;
		this.bitIsSet = false;
		this.bitShiftRight = -1;
		shouldCountForFilter = true;
	}

	public VarplayerRequirement(int VarPlayerID, boolean bitIsSet, int bitPosition)
	{
		this.VarPlayerID = VarPlayerID;
		this.values = List.of(-1);
		this.operation = Operation.EQUAL;
		this.displayText = null;

		this.bitPosition = bitPosition;
		this.bitIsSet = bitIsSet;
		this.bitShiftRight = -1;
		shouldCountForFilter = true;
	}

	public VarplayerRequirement(int VarPlayerID, boolean bitIsSet, int bitPosition, String displayText)
	{
		this.VarPlayerID = VarPlayerID;
		this.values = List.of(-1);
		this.operation = Operation.EQUAL;
		this.displayText = displayText;

		this.bitPosition = bitPosition;
		this.bitIsSet = bitIsSet;
		this.bitShiftRight = -1;
		shouldCountForFilter = true;
	}

	public VarplayerRequirement(int VarPlayerID, int value, int bitShiftRight)
	{
		this.VarPlayerID = VarPlayerID;
		this.values = List.of(value);
		this.operation = Operation.EQUAL;
		this.displayText = null;

		this.bitPosition = -1;
		this.bitIsSet = false;
		this.bitShiftRight = bitShiftRight;
		shouldCountForFilter = true;
	}

	public VarplayerRequirement(int VarPlayerID, List<Integer> values, int bitShiftRight)
	{
		this.VarPlayerID = VarPlayerID;
		this.values = values;
		this.operation = Operation.EQUAL;
		this.displayText = null;

		this.bitPosition = -1;
		this.bitIsSet = false;
		this.bitShiftRight = bitShiftRight;
		shouldCountForFilter = true;
	}

	@Override
	public boolean check(Client client)
	{
		int varpValue = client.getVarpValue(VarPlayerID);
		if (bitPosition >= 0)
		{
			return bitIsSet == BigInteger.valueOf(varpValue).testBit(bitPosition);
		}
		else if (bitShiftRight >= 0)
		{
			return values.stream().anyMatch(value -> operation.check(varpValue >> bitShiftRight, value));
		}
		return values.stream().anyMatch(value -> operation.check(varpValue, value));
	}

	@Nonnull
	@Override
	public String getDisplayText()
	{
		if (displayText != null)
		{
			return displayText;
		}
		if (bitPosition >= 0)
		{
			return VarPlayerID + " must have the " + bitPosition + " bit set.";
		}
		return VarPlayerID + " must be + " + operation.name().toLowerCase(Locale.ROOT) + " " + values;
	}
}
