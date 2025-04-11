/*
 * Copyright (c) 2024, pajlada <https://github.com/pajlada>
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
package com.questhelper.helpers.quests.thecurseofarrav;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MetalDoorSolverTest
{
	@Test
	public void testValidCodes()
	{
		// from pajdonk
		assertArrayEquals(new int[]{0, 2, 3, 5}, MetalDoorSolver.calculate("IFCB"));

		// from Gupinic
		assertArrayEquals(new int[]{1, 3, 7, 2}, MetalDoorSolver.calculate("FBDG"));

		// from Avsynthe
		assertArrayEquals(new int[]{1, 8, 4, 5}, MetalDoorSolver.calculate("FCEB"));

		// from Zoinkwiz
		assertArrayEquals(new int[]{6, 3, 6, 4}, MetalDoorSolver.calculate("AEGH"));

		// from pajdank
		assertArrayEquals(new int[]{1, 3, 4, 2}, MetalDoorSolver.calculate("BIAF"));
	}

	@Test
	public void testLowercaseValidCodes()
	{
		// from pajdonk
		assertArrayEquals(new int[]{0, 2, 3, 5}, MetalDoorSolver.calculate("ifcb"));

		// from Gupinic
		assertArrayEquals(new int[]{1, 3, 7, 2}, MetalDoorSolver.calculate("fbdg"));
	}

	@Test
	public void testInvalidCodes()
	{
		// Some character in the code is not valid
		assertNull(MetalDoorSolver.calculate("JAAA"));
		assertNull(MetalDoorSolver.calculate("AJAA"));
		assertNull(MetalDoorSolver.calculate("AAJA"));
		assertNull(MetalDoorSolver.calculate("AAAJ"));
	}

	@Test
	public void testCodeTooLong()
	{
		assertNull(MetalDoorSolver.calculate("AAAAA"));
		assertNull(MetalDoorSolver.calculate("AAAAAAAAA"));
		assertNull(MetalDoorSolver.calculate("AAAA "));
	}

	@Test
	public void testCodeTooShort()
	{
		assertNull(MetalDoorSolver.calculate("AAA"));
		assertNull(MetalDoorSolver.calculate("AA"));
		assertNull(MetalDoorSolver.calculate("A"));
		assertNull(MetalDoorSolver.calculate(""));
	}

	@Test
	public void testCodeIsNull()
	{
		assertNull(MetalDoorSolver.calculate(null));
	}

	@Test
	public void testDistanceUp()
	{
		assertEquals(0, MetalDoorSolver.calculateDistanceUp(0, 0));
		assertEquals(0, MetalDoorSolver.calculateDistanceUp(1, 1));
		assertEquals(0, MetalDoorSolver.calculateDistanceUp(2, 2));
		assertEquals(0, MetalDoorSolver.calculateDistanceUp(3, 3));
		assertEquals(0, MetalDoorSolver.calculateDistanceUp(4, 4));
		assertEquals(0, MetalDoorSolver.calculateDistanceUp(5, 5));
		assertEquals(0, MetalDoorSolver.calculateDistanceUp(6, 6));
		assertEquals(0, MetalDoorSolver.calculateDistanceUp(7, 7));
		assertEquals(0, MetalDoorSolver.calculateDistanceUp(8, 8));
		assertEquals(0, MetalDoorSolver.calculateDistanceUp(9, 9));

		assertEquals(1, MetalDoorSolver.calculateDistanceUp(0, 1));
		assertEquals(1, MetalDoorSolver.calculateDistanceUp(1, 2));
		assertEquals(1, MetalDoorSolver.calculateDistanceUp(2, 3));
		assertEquals(1, MetalDoorSolver.calculateDistanceUp(3, 4));
		assertEquals(1, MetalDoorSolver.calculateDistanceUp(4, 5));
		assertEquals(1, MetalDoorSolver.calculateDistanceUp(5, 6));
		assertEquals(1, MetalDoorSolver.calculateDistanceUp(6, 7));
		assertEquals(1, MetalDoorSolver.calculateDistanceUp(7, 8));
		assertEquals(1, MetalDoorSolver.calculateDistanceUp(8, 9));
		assertEquals(1, MetalDoorSolver.calculateDistanceUp(9, 0));

		assertEquals(9, MetalDoorSolver.calculateDistanceUp(0, 9));
		assertEquals(9, MetalDoorSolver.calculateDistanceUp(1, 0));
		assertEquals(9, MetalDoorSolver.calculateDistanceUp(2, 1));
		assertEquals(9, MetalDoorSolver.calculateDistanceUp(3, 2));
		assertEquals(9, MetalDoorSolver.calculateDistanceUp(4, 3));
		assertEquals(9, MetalDoorSolver.calculateDistanceUp(5, 4));
		assertEquals(9, MetalDoorSolver.calculateDistanceUp(6, 5));
		assertEquals(9, MetalDoorSolver.calculateDistanceUp(7, 6));
		assertEquals(9, MetalDoorSolver.calculateDistanceUp(8, 7));
		assertEquals(9, MetalDoorSolver.calculateDistanceUp(9, 8));
	}

	@Test
	public void testDistanceDown()
	{
		assertEquals(0, MetalDoorSolver.calculateDistanceDown(0, 0));
		assertEquals(0, MetalDoorSolver.calculateDistanceDown(1, 1));
		assertEquals(0, MetalDoorSolver.calculateDistanceDown(2, 2));
		assertEquals(0, MetalDoorSolver.calculateDistanceDown(3, 3));
		assertEquals(0, MetalDoorSolver.calculateDistanceDown(4, 4));
		assertEquals(0, MetalDoorSolver.calculateDistanceDown(5, 5));
		assertEquals(0, MetalDoorSolver.calculateDistanceDown(6, 6));
		assertEquals(0, MetalDoorSolver.calculateDistanceDown(7, 7));
		assertEquals(0, MetalDoorSolver.calculateDistanceDown(8, 8));
		assertEquals(0, MetalDoorSolver.calculateDistanceDown(9, 9));

		assertEquals(1, MetalDoorSolver.calculateDistanceDown(0, 9));
		assertEquals(1, MetalDoorSolver.calculateDistanceDown(1, 0));
		assertEquals(1, MetalDoorSolver.calculateDistanceDown(2, 1));
		assertEquals(1, MetalDoorSolver.calculateDistanceDown(3, 2));
		assertEquals(1, MetalDoorSolver.calculateDistanceDown(4, 3));
		assertEquals(1, MetalDoorSolver.calculateDistanceDown(5, 4));
		assertEquals(1, MetalDoorSolver.calculateDistanceDown(6, 5));
		assertEquals(1, MetalDoorSolver.calculateDistanceDown(7, 6));
		assertEquals(1, MetalDoorSolver.calculateDistanceDown(8, 7));
		assertEquals(1, MetalDoorSolver.calculateDistanceDown(9, 8));

		assertEquals(9, MetalDoorSolver.calculateDistanceDown(0, 1));
		assertEquals(9, MetalDoorSolver.calculateDistanceDown(1, 2));
		assertEquals(9, MetalDoorSolver.calculateDistanceDown(2, 3));
		assertEquals(9, MetalDoorSolver.calculateDistanceDown(3, 4));
		assertEquals(9, MetalDoorSolver.calculateDistanceDown(4, 5));
		assertEquals(9, MetalDoorSolver.calculateDistanceDown(5, 6));
		assertEquals(9, MetalDoorSolver.calculateDistanceDown(6, 7));
		assertEquals(9, MetalDoorSolver.calculateDistanceDown(7, 8));
		assertEquals(9, MetalDoorSolver.calculateDistanceDown(8, 9));
		assertEquals(9, MetalDoorSolver.calculateDistanceDown(9, 0));
	}
}
