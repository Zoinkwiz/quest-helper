package com.questhelper.helpers.quests.thecurseofarrav;

import net.runelite.api.coords.WorldPoint;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class MetalDoorSolverTest
{
	@Test
	public void testValidCodes()
	{
		// from pajdonk
		assertArrayEquals(new int[]{0, 2, 3, 5},MetalDoorSolver.calculate("IFCB"));

		// from Gupinic
		assertArrayEquals(new int[]{1, 3, 7, 2}, MetalDoorSolver.calculate("FBDG"));

		// from Avsynthe
		assertArrayEquals(new int[]{1, 8, 4, 5}, MetalDoorSolver.calculate("FCEB"));
	}

	@Test
	public void testLowercaseValidCodes()
	{
		// from pajdonk
		assertArrayEquals(new int[]{0, 2, 3, 5},MetalDoorSolver.calculate("ifcb"));

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
}
