/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
 * Copyright (c) 2024, Harrison Tarr <https://github.com/harrison-tarr>
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
package com.questhelper.helpers.quests.pures;

import com.questhelper.MockedTest;
import com.questhelper.QuestHelperConfig;
import com.questhelper.config.SkillFiltering;
import com.questhelper.helpers.quests.childrenofthesun.ChildrenOfTheSun;
import com.questhelper.helpers.quests.deserttreasure.DesertTreasure;
import com.questhelper.helpers.quests.fairytalei.FairytaleI;
import com.questhelper.helpers.quests.legendsquest.LegendsQuest;
import com.questhelper.helpers.quests.naturespirit.NatureSpirit;
import com.questhelper.helpers.quests.waterfallquest.WaterfallQuest;
import com.questhelper.questhelpers.QuestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class OneDefPureTest extends MockedTest
{
	@BeforeEach
	public void setup()
	{
		when(configManager.getConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, "skillfilterDefence")).thenReturn("true");
		when(configManager.getConfiguration(anyString(), anyString())).thenReturn("false");
	}

	private QuestHelper setupQuest(QuestHelper questHelper)
	{
		QuestHelper qh = Mockito.spy(questHelper);
		doReturn(configManager).when(qh).getConfigManager();
		return qh;
	}

	@Test
	public void testChildrenOfTheSunPassesSkillFilter_WithDefenceFilteredOut()
	{
		ChildrenOfTheSun childrenOfTheSun = new ChildrenOfTheSun();
		QuestHelper questHelperSpy = setupQuest(childrenOfTheSun);

		boolean result = SkillFiltering.questPassesSkillFilter(questHelperSpy);
		assertTrue(result, "Children of the Sun should pass the skill filter with Defence skill filtered out.");
	}

	@Test
	public void testWaterfallQuestPassesSkillFilter_WithDefenceFilteredOut()
	{
		WaterfallQuest waterfallQuest = new WaterfallQuest();
		QuestHelper questHelperSpy = setupQuest(waterfallQuest);

		boolean result = SkillFiltering.questPassesSkillFilter(questHelperSpy);
		assertTrue(result, "Waterfall Quest should pass the skill filter with Defence skill filtered out.");
	}

	@Test
	public void testNatureSpiritPassesSkillFilter_WithDefenceFilteredOut()
	{
		NatureSpirit natureSpirit = new NatureSpirit();
		QuestHelper questHelperSpy = setupQuest(natureSpirit);

		boolean result = SkillFiltering.questPassesSkillFilter(questHelperSpy);
		assertTrue(result, "Nature Spirit should pass the skill filter with Defence skill filtered out.");
	}

	@Test
	public void testFairytaleIPassesSkillFilter_WithDefenceFilteredOut()
	{
		FairytaleI fairyTale1 = new FairytaleI();
		QuestHelper questHelperSpy = setupQuest(fairyTale1);

		boolean result = SkillFiltering.questPassesSkillFilter(questHelperSpy);
		assertTrue(result, "Fairy Tale I should pass the skill filter with Defence skill filtered out.");
	}

	@Test
	public void testDesertTreasurePassesSkillFilter_WithDefenceFilteredOut()
	{
		DesertTreasure desertTreasure = new DesertTreasure();
		QuestHelper questHelperSpy = setupQuest(desertTreasure);

		boolean result = SkillFiltering.questPassesSkillFilter(questHelperSpy);
		assertTrue(result, "Desert Treasure should pass the skill filter with Defence skill filtered out.");
	}

	@Test
	public void testLegendsQuestPassesSkillFilter_WithDefenceFilteredOut()
	{
		LegendsQuest legendsQuest = new LegendsQuest();
		QuestHelper questHelperSpy = setupQuest(legendsQuest);

		boolean result = SkillFiltering.questPassesSkillFilter(questHelperSpy);
		assertTrue(result, "Legends' Quest should pass the skill filter with Defence skill filtered out.");
	}
}
