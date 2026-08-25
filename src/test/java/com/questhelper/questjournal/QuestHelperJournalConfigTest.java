/*
 * Copyright (c) 2026, nanopink
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
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.questjournal;

import com.questhelper.QuestHelperConfig;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestHelperJournalConfigTest
{
	private final QuestHelperConfig config = new QuestHelperConfig()
	{
	};

	@Test
	void journalDefaultsEnableVisiblePreferences() throws ReflectiveOperationException
	{
		assertSame(Keybind.NOT_SET, config.journalOpenHotkey());
		for (Method method : QuestHelperConfig.class.getDeclaredMethods())
		{
			ConfigItem item = method.getAnnotation(ConfigItem.class);
			if (item != null
				&& !item.hidden()
				&& QuestHelperConfig.journalSection.equals(item.section())
				&& method.getReturnType() == boolean.class)
			{
				assertTrue((boolean) method.invoke(config), item.keyName());
			}
		}
	}

	@Test
	void nativeQuestJournalReplacementHasTheCorrectLabel() throws ReflectiveOperationException
	{
		assertEquals(
			"Replace old quest journal",
			configItem("journalReplaceNativeQuestJournal").name());
	}

	@Test
	void activeQuestSelectionSettingHasTheManualSelectionLabel()
		throws ReflectiveOperationException
	{
		ConfigItem item = configItem("journalChooseActiveQuestManually");
		assertEquals("Choose active quest manually", item.name());
		assertEquals(
			"Keep browsing separate from the quest being actively guided by Quest Helper",
			item.description());
	}

	@Test
	void journalSectionPrecedesClosedDevelopmentSection() throws ReflectiveOperationException
	{
		ConfigSection journal = configSection("journalSection");
		assertSame(
			QuestHelperConfig.class,
			QuestHelperConfig.class.getField("journalSection").getDeclaringClass());
		assertEquals("Quest Journal", journal.name());
		assertEquals(5, journal.position());
		assertTrue(journal.closedByDefault());

		ConfigSection development = configSection("developmentSection");
		assertEquals(6, development.position());
		assertTrue(development.closedByDefault());
	}

	@Test
	void visibleJournalItemsUseStableKeysAndOrdering() throws ReflectiveOperationException
	{
		Map<String, Integer> expectedPositions = new LinkedHashMap<>();
		expectedPositions.put("enableQuestJournal", 0);
		expectedPositions.put("journalShowButton", 1);
		expectedPositions.put("journalReplaceNativeQuestJournal", 2);
		expectedPositions.put("journalChooseActiveQuestManually", 3);
		expectedPositions.put("journalExpandChecklistsOnDemand", 4);
		expectedPositions.put("journalOpenMissingItemWikiLinks", 5);
		expectedPositions.put("journalKeepWithinGameArea", 6);
		expectedPositions.put("journalOpenHotkey", 7);

		for (Map.Entry<String, Integer> expected : expectedPositions.entrySet())
		{
			Method method = QuestHelperConfig.class.getMethod(expected.getKey());
			assertSame(QuestHelperConfig.class, method.getDeclaringClass());
			ConfigItem item = method.getAnnotation(ConfigItem.class);
			assertEquals(expected.getKey(), item.keyName());
			assertEquals(QuestHelperConfig.journalSection, item.section());
			assertEquals(expected.getValue().intValue(), item.position());
			assertFalse(item.hidden());
		}
		long visibleJournalItems = java.util.Arrays.stream(
				QuestHelperConfig.class.getDeclaredMethods())
			.map(method -> method.getAnnotation(ConfigItem.class))
			.filter(item -> item != null
				&& !item.hidden()
				&& QuestHelperConfig.journalSection.equals(item.section()))
			.count();
		assertEquals(expectedPositions.size(), visibleJournalItems);
	}

	private static ConfigItem configItem(String methodName) throws ReflectiveOperationException
	{
		Method method = QuestHelperConfig.class.getMethod(methodName);
		return method.getAnnotation(ConfigItem.class);
	}

	private static ConfigSection configSection(String fieldName) throws ReflectiveOperationException
	{
		Field field = QuestHelperConfig.class.getField(fieldName);
		return field.getAnnotation(ConfigSection.class);
	}
}
