/*
 *
 *  * Copyright (c) 2021, Senmori
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
package com.questhelper.requirements.util;

import com.questhelper.requirements.Requirement;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.runelite.api.Client;

/**
 * Builder for building non-standard simple {@link Requirement}s.<br>
 * For complex requirements, use {@link ComplexRequirementBuilder},
 * {@link com.questhelper.requirements.ComplexRequirement}, or
 * implement your own {@link Requirement}.
 */
public final class RequirementBuilder
{
	private String displayText = "";
	private Predicate<Client> requirementPredicate = client -> true;


	private RequirementBuilder(@Nonnull String displayText)
	{
		this.displayText = displayText;
	}

	/**
	 * Convenience method to create a simple ComplexRequirement with the default {@link LogicType#AND} and no
	 * display text (not null).
	 * @return a new {@link ComplexRequirementBuilder} with a default {@link LogicType#AND} and no display text
	 */
	public static ComplexRequirementBuilder complex()
	{
		return new ComplexRequirementBuilder(LogicType.AND, "");
	}

	/**
	 * Create a new instance of {@link RequirementBuilder} with the given display text
	 *
	 * @param displayText display text
	 * @return new instance of {@link RequirementBuilder}
	 */
	public static RequirementBuilder builder(@Nonnull String displayText)
	{
		return new RequirementBuilder(displayText);
	}

	/**
	 * @return new instance of {@link RequirementBuilder} with no display text
	 */
	public static RequirementBuilder builder()
	{
		return builder("");
	}

	/**
	 * Define a new check predicate that is used in {@link Requirement#check(Client)}
	 *
	 * @param requirementPredicate predicate to use
	 * @return this
	 */
	public RequirementBuilder check(@Nonnull Predicate<Client> requirementPredicate)
	{
		this.requirementPredicate = requirementPredicate;
		return this;
	}

	/**
	 * @return new instance of {@link Requirement} using the supplied display text and predicate
	 */
	public Requirement build()
	{
		return new Requirement()
		{
			@Override
			public boolean check(Client client)
			{
				return requirementPredicate.test(client);
			}

			@Override
			public String getDisplayText()
			{
				return displayText;
			}
		};
	}
}
