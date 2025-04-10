package com.questhelper.helpers.quests.thefremennikisles;

import com.questhelper.MockedTest;
import com.questhelper.domain.AccountType;
import net.runelite.api.Skill;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class TheFremennikIslesTest extends MockedTest
{
	@Test
	void ensureMiningSkillCheckWorks()
	{
		when(playerStateManager.getAccountType()).thenReturn(AccountType.NORMAL);
		var helper = new TheFremennikIsles();
		this.injector.injectMembers(helper);
		helper.setQuestHelperPlugin(questHelperPlugin);
		helper.initializeRequirements();

		when(client.getRealSkillLevel(Skill.MINING)).thenReturn(1);
		assertTrue(helper.tinOre.shouldDisplayText(this.client), "Only Tin ore should display when lvl 1 mining");
		assertFalse(helper.coal.shouldDisplayText(this.client), "Only Tin ore should display when lvl 1 mining");
		assertFalse(helper.mithrilOre.shouldDisplayText(this.client), "Only Tin ore should display when lvl 1 mining");

		when(client.getRealSkillLevel(Skill.MINING)).thenReturn(10);
		assertFalse(helper.tinOre.shouldDisplayText(this.client), "Only Coal should display when lvl 10 mining");
		assertTrue(helper.coal.shouldDisplayText(this.client), "Only Coal should display when lvl 10 mining");
		assertFalse(helper.mithrilOre.shouldDisplayText(this.client), "Only Coal should display when lvl 10 mining");

		when(client.getRealSkillLevel(Skill.MINING)).thenReturn(55);
		assertFalse(helper.tinOre.shouldDisplayText(this.client), "Only Mithril ore should display when lvl 55 mining");
		assertFalse(helper.coal.shouldDisplayText(this.client), "Only Mithril ore should display when lvl 55 mining");
		assertTrue(helper.mithrilOre.shouldDisplayText(this.client), "Only Mithril ore should display when lvl 55 mining");
	}
}
