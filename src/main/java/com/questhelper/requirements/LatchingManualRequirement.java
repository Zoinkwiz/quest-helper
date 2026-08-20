/*
 * Copyright (c) 2026, Zoinkwiz <https://github.com/Zoinkwiz>
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

import javax.annotation.Nonnull;
import net.runelite.api.Client;

/**
 * Wraps a source {@link Requirement} and latches a target {@link ManualRequirement} the first time the source's
 * {@link Requirement#check(Client)} returns the configured polarity. Returns the source's value unchanged so the
 * wrapper can be composed transparently into branch / completion expressions to get sticky-once-completed behaviour.
 *
 * <p>Mirrors the maker preview's {@code selectorWithAutoPersistOnCompletion}: once the row has been "completed" once
 * (selector true under {@code CONTINUE_WHEN_TRUE}, selector false under {@code SHOW_WHEN_TRUE}), the manual override
 * stays passed even if the source later flips back.</p>
 */
public class LatchingManualRequirement extends SimpleRequirement
{
	private final Requirement source;
	private final ManualRequirement target;
	private final boolean latchOnSourceTrue;

	public LatchingManualRequirement(Requirement source, ManualRequirement target, boolean latchOnSourceTrue)
	{
		this.source = source;
		this.target = target;
		this.latchOnSourceTrue = latchOnSourceTrue;
	}

	@Override
	public boolean check(Client client)
	{
		boolean sourceValue = source.check(client);
		if (sourceValue == latchOnSourceTrue && !target.isShouldPass())
		{
			target.setShouldPass(true);
		}
		return sourceValue;
	}

	@Nonnull
	@Override
	public String getDisplayText()
	{
		String text = source.getDisplayText();
		return text == null ? "" : text;
	}
}
