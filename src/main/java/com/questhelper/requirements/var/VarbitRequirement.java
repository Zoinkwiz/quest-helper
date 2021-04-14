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
import java.math.BigInteger;
import java.util.Locale;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Varbits;

/**
 * Checks if a player's varbit value is meets the required value as determined by the
 * {@link Operation}
 */
@Getter
public class VarbitRequirement extends AbstractRequirement
{
	private final int varbitID;
	private final int requiredValue;
	private final Operation operation;
	private final String displayText;

	// bit positions
	private boolean bitIsSet = false;
	private int bitPosition = -1;

	/**
	 * Check if the player's varbit value meets the required level using the given
	 * {@link Operation}.
	 *
	 * @param varbitID      the {@link Varbits} id to use
	 * @param operation     the {@link Operation} to check with
	 * @param requiredValue the required varbit value to pass this requirement
	 * @param displayText   the display text
	 */
	public VarbitRequirement(int varbitID, Operation operation, int requiredValue, String displayText)
	{
		this.varbitID = varbitID;
		this.operation = operation;
		this.requiredValue = requiredValue;
		this.displayText = displayText;
	}

	/**
	 * Check if the player's {@link Varbits} value meets the required level using the given
	 * {@link Operation}.
	 *
	 * @param varbit        the {@link Varbits} to check
	 * @param operation     the {@link Operation} to check with
	 * @param requiredValue the required varbit value to pass this requirement
	 * @param displayText   the display text
	 */
	public VarbitRequirement(Varbits varbit, Operation operation, int requiredValue, String displayText)
	{
		this(varbit.getId(), operation, requiredValue, displayText);
	}

	/**
	 * Check if a specified varbit value is exactly the supplied value.
	 *
	 * @param varbitID the varbit id
	 * @param value    the value the varbit should be
	 */
	public VarbitRequirement(int varbitID, int value)
	{
		this(varbitID, Operation.EQUAL, value, null);
	}

	/**
	 * Check the supplied varbit's value using the given {@link Operation}
	 *
	 * @param varbitID  the varbit id
	 * @param value     the value it should be
	 * @param operation the operation to check with
	 */
	public VarbitRequirement(int varbitID, int value, Operation operation)
	{
		this(varbitID, operation, value, null);
	}

	/**
	 * Checks if a specified varbit value has a specific bit position set.
	 *
	 * @param varbitID    the varbit id
	 * @param bitIsSet    if the bit should be set
	 * @param bitPosition the position of the bit in question
	 */
	public VarbitRequirement(int varbitID, boolean bitIsSet, int bitPosition)
	{
		this.varbitID = varbitID;
		this.requiredValue = -1;
		this.operation = Operation.EQUAL;

		this.bitPosition = bitPosition;
		this.bitIsSet = bitIsSet;
		String[] suffixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
		String text = String.valueOf(bitPosition);
		switch (bitPosition % 100)
		{
			case 11:
			case 12:
			case 13:
				text += "th";
			default:
				text = bitPosition + suffixes[bitPosition % 10];
		}
		this.displayText = varbitID + " must have the " + text + " bit set.";
	}

	@Override
	public boolean check(Client client)
	{
		if (bitPosition >= 0)
		{
			return bitIsSet == BigInteger.valueOf(client.getVarbitValue(varbitID)).testBit(bitPosition);
		}

		return operation.check(client.getVarbitValue(varbitID), requiredValue);
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
			return varbitID + " must have the " + bitPosition + " bit set.";
		}
		return varbitID + " must be + " + operation.name().toLowerCase(Locale.ROOT) + " " + requiredValue;
	}
}
