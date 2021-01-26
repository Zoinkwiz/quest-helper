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

import com.questhelper.requirements.ComplexRequirement;
import com.questhelper.requirements.Requirement;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Builder class for making building {@link ComplexRequirement}s easier.<br>
 * Use {@link RequirementBuilder} for simple requirements.
 */
public final class ComplexRequirementBuilder
{
	private final LogicType logicType;
	private final String displayText;

	private final List<Requirement> requirements = new LinkedList<>();

	/**
	 * Create a new builder with the supplied {@link LogicType} and display text
	 *
	 * @param logicType logic type to use
	 * @param displayText display text for rendering
	 */
	protected ComplexRequirementBuilder(@Nonnull LogicType logicType, @Nonnull String displayText)
	{
		this.logicType = logicType;
		this.displayText = displayText;
	}

	/**
	 * Add a {@link Requirement}.
	 *
	 * @param requirement the {@link Requirement} to add
	 * @return this
	 */
	public ComplexRequirementBuilder with(@Nonnull Requirement requirement)
	{
		this.requirements.add(requirement);
		return this;
	}

	/**
	 * Adds a {@link ComplexRequirement} using the given {@link LogicType} and {@link Requirement}.
	 * This uses the already supplied display text for the additional requirement
	 * <br>
	 * Equivalent to {@link ComplexRequirement#ComplexRequirement(LogicType, String, Requirement...)}.
	 *
	 * @param logicType logic type to use
	 * @param requirement requirement to add
	 * @return this
	 */
	public ComplexRequirementBuilder with(@Nonnull LogicType logicType, @Nonnull Requirement requirement)
	{
		this.requirements.add(new ComplexRequirement(logicType, displayText, requirement));
		return this;
	}

	/**
	 * @return a new instance of {@link ComplexRequirement} with the supplied display text, logic, and requirements.
	 */
	@Nonnull
	public ComplexRequirement build()
	{
		return new ComplexRequirement(logicType, displayText, requirements.toArray(new Requirement[0]));
	}

	/**
	 * Create a new builder with a logic type of {@link LogicType#AND} and the supplied display text.
	 *
	 * @param displayText display text
	 * @return a new {@link ComplexRequirementBuilder}
	 */
	public static ComplexRequirementBuilder and(@Nonnull String displayText)
	{
		return new ComplexRequirementBuilder(LogicType.AND, displayText);
	}

	/**
	 * Create a new builder with a logic type of {@link LogicType#OR} and the supplied display text.
	 *
	 * @param displayText display text
	 * @return a new {@link ComplexRequirementBuilder}
	 */
	public static ComplexRequirementBuilder or(@Nonnull String displayText)
	{
		return new ComplexRequirementBuilder(LogicType.OR, displayText);
	}

	/**
	 * Create a new builder with a logic type of {@link LogicType#NAND} and the supplied display text.
	 *
	 * @param displayText display text
	 * @return a new {@link ComplexRequirementBuilder}
	 */
	public static ComplexRequirementBuilder nand(@Nonnull String displayText)
	{
		return new ComplexRequirementBuilder(LogicType.NAND, displayText);
	}

	/**
	 * Create a new builder with a logic type of {@link LogicType#NOR} and the supplied display text.
	 *
	 * @param displayText display text
	 * @return a new {@link ComplexRequirementBuilder}
	 */
	public static ComplexRequirementBuilder nor(@Nonnull String displayText)
	{
		return new ComplexRequirementBuilder(LogicType.NOR, displayText);
	}

	/**
	 * Create a new builder with a logic type of {@link LogicType#XOR} and the supplied display text.
	 *
	 * @param displayText display text
	 * @return a new {@link ComplexRequirementBuilder}
	 */
	public static ComplexRequirementBuilder xor(@Nonnull String displayText)
	{
		return new ComplexRequirementBuilder(LogicType.XOR, displayText);
	}
}
