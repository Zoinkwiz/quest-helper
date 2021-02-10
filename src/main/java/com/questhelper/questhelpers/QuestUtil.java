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

package com.questhelper.questhelpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public class QuestUtil
{
	public static <T> List<T> toArrayList(@Nonnull T... elements)
	{
		return new ArrayList<>(Arrays.asList(elements));
	}

	public static <T> Collector<T, ?, List<T>> collectToArrayList()
	{
		return Collectors.toCollection(ArrayList::new);
	}

	/**
	 * Removes all the duplicate elements from a stream and collects them into a
	 * mutable ArrayList
	 *
	 * @param stream stream to remove duplicates from
	 * @return a mutable list containing all the remaining elements of the stream
	 */
	public static <T> List<T> collectAndRemoveDuplicates(Stream<T> stream)
	{
		return stream.distinct().collect(collectToArrayList());
	}

	/**
	 * Remove duplicates in the given list.
	 *
	 * @param list the list
	 * @return a mutable list without duplicates.
	 */
	public static <T> List<T> removeDuplicates(List<T> list)
	{
		return collectAndRemoveDuplicates(list.stream());
	}
}
