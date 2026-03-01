// SPDX-FileCopyrightText: 2026 pajlada <rasmus.karlsson@pajlada.com>
//
// SPDX-License-Identifier: BSD-2-Clause

package com.questhelper.requirements.conditional;

import com.questhelper.MockedTest;
import com.questhelper.requirements.ManualRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.nor;
import static com.questhelper.requirements.util.LogicHelper.or;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ConditionsTest extends MockedTest
{
	@Test
	void checkAnd()
	{
		var t1 = new ManualRequirement();
		t1.setShouldPass(true);
		var t2 = new ManualRequirement();
		t2.setShouldPass(true);
		var t3 = new ManualRequirement();
		t3.setShouldPass(true);
		var f1 = new ManualRequirement();
		var f2 = new ManualRequirement();
		var f3 = new ManualRequirement();

		assertTrue(and(t1).check(client));
		assertTrue(and(t1, t2).check(client));
		assertTrue(and(t1, t2, t3).check(client));

		assertFalse(and(f1, t1).check(client));
		assertFalse(and(f1, t1, t2).check(client));
		assertFalse(and(f1, t1, t2, t3).check(client));

		assertFalse(and(f1).check(client));
		assertFalse(and(f1, f2).check(client));
		assertFalse(and(f1, f2, f3).check(client));
	}

	@Test
	void checkOr()
	{
		var t1 = new ManualRequirement();
		t1.setShouldPass(true);
		var t2 = new ManualRequirement();
		t2.setShouldPass(true);
		var t3 = new ManualRequirement();
		t3.setShouldPass(true);
		var f1 = new ManualRequirement();
		var f2 = new ManualRequirement();
		var f3 = new ManualRequirement();

		assertTrue(or(t1).check(client));
		assertTrue(or(t1, t2).check(client));
		assertTrue(or(t1, t2, t3).check(client));

		assertTrue(or(f1, t1).check(client));
		assertTrue(or(f1, t1, t2).check(client));
		assertTrue(or(f1, t1, t2, t3).check(client));

		assertFalse(or(f1).check(client));
		assertFalse(or(f1, f2).check(client));
		assertFalse(or(f1, f2, f3).check(client));
	}

	@Test
	void checkNor()
	{
		var t1 = new ManualRequirement();
		t1.setShouldPass(true);
		var t2 = new ManualRequirement();
		t2.setShouldPass(true);
		var t3 = new ManualRequirement();
		t3.setShouldPass(true);
		var f1 = new ManualRequirement();
		var f2 = new ManualRequirement();
		var f3 = new ManualRequirement();

		assertFalse(nor(t1).check(client));
		assertFalse(nor(t1, t2).check(client));
		assertFalse(nor(t1, t2, t3).check(client));

		assertFalse(nor(f1, t1).check(client));
		assertFalse(nor(f1, t1, t2).check(client));
		assertFalse(nor(f1, t1, t2, t3).check(client));

		assertTrue(nor(f1).check(client));
		assertTrue(nor(f1, f2).check(client));
		assertTrue(nor(f1, f2, f3).check(client));
	}
}
