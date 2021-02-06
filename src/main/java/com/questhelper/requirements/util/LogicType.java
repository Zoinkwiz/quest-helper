/*
 *
 *  * Copyright (c) 2021, Zoinkwiz
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

import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public enum LogicType
{
	/** Returns true only if all inputs match the supplied predicate. */
	AND(Stream::allMatch, (n1, n2) -> n1.doubleValue() == n2.doubleValue()),
	/** Returns true if any inputs match the supplied predicate. */
	OR(Stream::anyMatch, (n1, n2) -> n1.doubleValue() > 0.0D),
	/** The output is false is all inputs match the supplied predicate. Otherwise returns true. */
	NAND((s,p) -> !s.allMatch(p), (n1, n2) -> n1.doubleValue() < n2.doubleValue()),
	/** Returns true if all elements do not match the supplied predicate. */
	NOR(Stream::noneMatch, (n1, n2) -> n1.doubleValue() == 0.0D),
	/** Returns true if either, but not both, inputs match the given predicate.
	 * This only tests the first two elements of the stream.
	 */
	XOR((s,p) -> s.filter(p).limit(2).count() == 1, (n1, n2) -> {
		return (n1.doubleValue() > 0.0D && !(n2.doubleValue() > 0.0D)) || (n2.doubleValue() > 0.0D && !(n1.doubleValue() > 0.0D));
	}),
	;

	private final BiFunction<Stream, Predicate, Boolean> function;
	private final BiFunction<Number, Number, Boolean> comparatorFunction;
	LogicType(BiFunction<Stream, Predicate, Boolean> func, BiFunction<Number, Number, Boolean> comparator)
	{
		this.function = func;
		this.comparatorFunction = comparator;
	}

	public <T> boolean test(Stream<T> stream, Predicate<T> predicate)
	{
		return function.apply(stream, predicate);
	}

	public boolean compare(Number numberToCheck, Number numberToCheckAgainst)
	{
		return comparatorFunction.apply(numberToCheck, numberToCheckAgainst);
	}
}
