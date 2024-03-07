package com.questhelper.helpers.pures;


import com.questhelper.MockedTestBase;
import com.questhelper.helpers.quests.childrenofthesun.ChildrenOfTheSun;
import com.questhelper.helpers.quests.deserttreasure.DesertTreasure;
import com.questhelper.helpers.quests.fairytalei.FairytaleI;
import com.questhelper.helpers.quests.legendsquest.LegendsQuest;
import com.questhelper.helpers.quests.naturespirit.NatureSpirit;
import com.questhelper.helpers.quests.waterfallquest.WaterfallQuest;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OneDefPureTest extends MockedTestBase
{

	OneDefPure oneDefPure;

	@BeforeEach
	public void setup()
	{
		oneDefPure = new OneDefPure();
	}

	@Test
	public void testIsSafeNoRewards()
	{
		assertTrue(oneDefPure.isSafeForPure(new ChildrenOfTheSun()));
	}

	@Test
	public void testIsSafe()
	{
		assertTrue(oneDefPure.isSafeForPure(new WaterfallQuest()));
	}

	@Test
	public void natureSpiritNotSafe()
	{
		assertFalse(oneDefPure.isSafeForPure(new NatureSpirit()));
	}

	@Test
	public void fairyTale1NotSafe()
	{
		assertFalse(oneDefPure.isSafeForPure(new FairytaleI()));
	}

	@Test
	public void desertTreasureIsSafe()
	{
		assertTrue(oneDefPure.isSafeForPure(new DesertTreasure()));
	}

	@Test
	public void legendsIsNotSafe()
	{
		assertFalse(oneDefPure.isSafeForPure(new LegendsQuest()));
	}
}
