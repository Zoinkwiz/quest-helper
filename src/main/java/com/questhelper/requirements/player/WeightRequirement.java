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
package com.questhelper.requirements.player;

import com.questhelper.requirements.AbstractRequirement;
import com.questhelper.requirements.util.Operation;
import net.runelite.api.Client;

/**
 * Checks if the player meets a weight check
 */
public class WeightRequirement extends AbstractRequirement
{
	private final int weight;
	private final String text;
	private final Operation operation;

	/**
	 * Checks if the player meets the weight requirement.
	 *
	 * @param text the display text
	 * @param weight the weight required
	 * @param operation the {@link Operation} to use
	 */
	public WeightRequirement(String text, int weight, Operation operation)
	{
		this.weight = weight;
		this.text = text;
		this.operation = operation;
	}

	public WeightRequirement(int weight)
	{
		this(null, weight, Operation.EQUAL);
	}

	public WeightRequirement(int weight, Operation operation)
	{
		this(null, weight, operation);
	}

	@Override
	public boolean check(Client client)
	{
		return operation.check(client.getWeight(), weight);
	}

	@Override
	public String getDisplayText()
	{
		if (text == null)
		{
			return "Weight " + operation.getDisplayText() + " than " + weight;
		}
		return text;
	}
}
