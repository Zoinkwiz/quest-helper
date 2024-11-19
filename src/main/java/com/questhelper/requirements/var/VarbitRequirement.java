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

import com.questhelper.managers.ActiveRequirementsManager;
import com.questhelper.requirements.AbstractRequirement;
import com.questhelper.requirements.util.Operation;
import java.math.BigInteger;
import java.util.Locale;
import com.questhelper.util.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import javax.annotation.Nonnull;

/**
 * Checks if a player's varbit value is meets the required value as determined by the
 * {@link Operation}
 */
@Getter
@Slf4j
public class VarbitRequirement extends AbstractRequirement
{
	private final int varbitID;

	@Setter
	private int requiredValue;
	private final Operation operation;
	private final String displayText;

	// bit positions
	private boolean bitIsSet = false;
	private int bitPosition = -1;

	private boolean hasFiredWarning = false;

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
		shouldCountForFilter = true;
	}

	/**
	 * Check if the player's varbit value meets the required level using the given
	 * {@link Operation}.
	 *
	 * @param varbitID             the {@link Varbits} id to use
	 * @param operation            the {@link Operation} to check with
	 * @param requiredValue        the required varbit value to pass this requirement
	 * @param displayText          the display text
	 * @param shouldCountForFilter if the requirement should count for quest filtering
	 */
	public VarbitRequirement(int varbitID, Operation operation, int requiredValue, String displayText, boolean shouldCountForFilter)
	{
		this.varbitID = varbitID;
		this.operation = operation;
		this.requiredValue = requiredValue;
		this.displayText = displayText;
		this.shouldCountForFilter = shouldCountForFilter;
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
				break;
			default:
				text = bitPosition + suffixes[bitPosition % 10];
		}
		this.displayText = varbitID + " must have the " + text + " bit set.";

		shouldCountForFilter = true;
	}

	private void checkVarbitValue(int varbitValue)
	{
		try {
			if (bitPosition >= 0)
			{
				setState(bitIsSet == BigInteger.valueOf(varbitValue).testBit(bitPosition));
				return;
			}

			setState(operation.check(varbitValue, requiredValue));
		} catch (IndexOutOfBoundsException e) {
			if (!hasFiredWarning) {
				var message = String.format("Error reading varbit %d, please report this in the Quest Helper discord.", varbitID);
				log.warn(message);
				Utils.addChatMessage(client, message);
				hasFiredWarning = true;
			}
			setState(false);
		}
	}

	@Override
	public void register(Client client, EventBus eventBus, ActiveRequirementsManager activeRequirementsManager)
	{
		super.register(client, eventBus, activeRequirementsManager);
		int varbitValue = client.getVarbitValue(varbitID);
		checkVarbitValue(varbitValue);
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		if (varbitChanged.getVarbitId() != varbitID) return;

		int varbitValue = varbitChanged.getValue();

		checkVarbitValue(varbitValue);
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
			return varbitID + " must have the " + bitPosition + " bit set.";
		}
		return varbitID + " must be + " + operation.name().toLowerCase(Locale.ROOT) + " " + requiredValue;
	}
}
