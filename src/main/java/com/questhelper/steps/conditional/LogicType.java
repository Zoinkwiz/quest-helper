/*
 * Copyright (c) 2020, Zoinkwiz
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

import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public enum LogicType
{
	/** Returns true only if all inputs match the supplied predicate. */
	AND(Stream::allMatch),
	/** Returns true if any inputs match the supplied predicate. */
	OR(Stream::anyMatch),
	/** The output is false is all inputs match the supplied predicate. Otherwise returns true. */
	NAND((s,p) -> !s.allMatch(p)),
	/** Returns true if all elements do not match the supplied predicate. */
	NOR(Stream::noneMatch),
	/** Returns true if either, but not both, inputs match the given predicate.
	 * This only tests the first two elements of the stream.
	 */
	XOR((s,p) -> s.filter(e -> p.test(e)).limit(2).count() == 1),
	;

	private final BiFunction<Stream, Predicate, Boolean> function;
	LogicType(BiFunction<Stream, Predicate, Boolean> func)
	{
		this.function = func;
	}

	public <T> boolean test(Stream<T> stream, Predicate<T> predicate)
	{
		return function.apply(stream, predicate);
	}
}
