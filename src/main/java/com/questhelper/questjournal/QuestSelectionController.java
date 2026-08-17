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
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.questjournal;

import com.questhelper.QuestHelperConfig;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;

/** Manages the browsed quest separately from Quest Helper's active quest. */
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
final class QuestSelectionController
{
	@NonNull
	private final Client client;
	@NonNull
	private final QuestHelperConfig config;

	private volatile JournalDataSource dataSource;
	private volatile JournalSnapshot sourceSnapshot;
	private volatile String browsedQuestId;

	void attach(JournalDataSource directSource)
	{
		dataSource = directSource;
		sourceSnapshot = null;
		browsedQuestId = null;
	}

	void detach()
	{
		dataSource = null;
		sourceSnapshot = null;
		browsedQuestId = null;
	}

	String getBrowsedQuestId()
	{
		return browsedQuestId;
	}

	JournalSnapshot getSourceSnapshot()
	{
		return sourceSnapshot;
	}

	boolean isManualActiveQuestSelection()
	{
		return config.journalChooseActiveQuestManually();
	}

	boolean publishSnapshot(
		JournalSnapshot source,
		JournalSnapshot.SelectedQuest selectedQuest)
	{
		String previousQuestId = browsedQuestId;
		sourceSnapshot = source;
		if (selectedQuest != null)
		{
			browsedQuestId = selectedQuest.getOverview().getId();
		}
		else
		{
			JournalDataSource directSource = dataSource;
			browsedQuestId = directSource == null
				? null
				: directSource.getViewedQuestId();
		}
		return !Objects.equals(previousQuestId, browsedQuestId);
	}

	void clearBrowsedQuest()
	{
		JournalDataSource directSource = dataSource;
		if (directSource != null)
		{
			directSource.clearBrowsedQuest();
		}
		browsedQuestId = null;
	}

	boolean browseQuest(String questId)
	{
		JournalDataSource directSource = dataSource;
		if (questId == null || questId.equals(browsedQuestId) || directSource == null
			|| !directSource.browseQuest(questId))
		{
			return false;
		}
		synchronizeAutomaticActiveQuest(questId);
		refreshViewedQuest(directSource);
		return true;
	}

	boolean toggleBrowsedQuest(String questId)
	{
		JournalDataSource directSource = dataSource;
		if (questId == null || directSource == null)
		{
			return false;
		}
		boolean clearing = questId.equals(browsedQuestId);
		boolean changed = clearing
			? directSource.clearBrowsedQuest(questId)
			: directSource.browseQuest(questId);
		if (!changed)
		{
			return false;
		}
		synchronizeAutomaticActiveQuest(clearing ? null : questId);
		refreshViewedQuest(directSource);
		return true;
	}

	boolean synchronizeFocusedQuestAutomatically()
	{
		JournalDataSource directSource = dataSource;
		if (directSource == null || !synchronizeAutomaticActiveQuest(browsedQuestId))
		{
			return false;
		}
		refreshViewedQuest(directSource);
		return true;
	}

	void clearFinishedAutomaticQuest(int tick)
	{
		JournalDataSource directSource = dataSource;
		if (isManualActiveQuestSelection() || directSource == null)
		{
			return;
		}
		JournalSnapshot snapshot = directSource.getSnapshot();
		JournalSnapshot.SelectedQuest selectedQuest = snapshot.getSelectedQuest();
		if (selectedQuest != null
			&& selectedQuest.getOverview().getState()
				!= JournalSnapshot.QuestState.COMPLETE)
		{
			return;
		}
		JournalSnapshot.ActiveQuest activeQuest = snapshot.getActiveQuest();
		if (activeQuest != null && directSource.stopActiveQuest(activeQuest.getId()))
		{
			directSource.markViewedQuestDirty();
			directSource.afterQuestUpdate(tick);
		}
	}

	boolean activateQuest(String questId)
	{
		JournalDataSource directSource = dataSource;
		if (questId == null || directSource == null || !directSource.activateQuest(questId))
		{
			return false;
		}
		refreshViewedQuest(directSource);
		return true;
	}

	boolean stopActiveQuest(String questId)
	{
		JournalDataSource directSource = dataSource;
		if (questId == null || directSource == null || !directSource.stopActiveQuest(questId))
		{
			return false;
		}
		refreshViewedQuest(directSource);
		return true;
	}

	boolean returnToActiveQuest()
	{
		JournalDataSource directSource = dataSource;
		if (directSource == null
			|| !directSource.browseActiveQuest())
		{
			return false;
		}
		refreshViewedQuest(directSource);
		return true;
	}

	private boolean synchronizeAutomaticActiveQuest(String questId)
	{
		JournalDataSource directSource = dataSource;
		if (isManualActiveQuestSelection() || directSource == null)
		{
			return false;
		}
		if (automaticActiveQuestEligible(questId))
		{
			return directSource.activateQuest(questId);
		}
		JournalSnapshot snapshot = sourceSnapshot;
		JournalSnapshot.ActiveQuest activeQuest = snapshot == null
			? null
			: snapshot.getActiveQuest();
		return activeQuest != null
			&& directSource.stopActiveQuest(activeQuest.getId());
	}

	private boolean automaticActiveQuestEligible(String questId)
	{
		JournalSnapshot snapshot = sourceSnapshot;
		if (questId == null || snapshot == null)
		{
			return false;
		}
		for (JournalSnapshot.QuestListItem quest : snapshot.getQuests())
		{
			if (questId.equals(quest.getId()))
			{
				return quest.getState() != JournalSnapshot.QuestState.COMPLETE;
			}
		}
		return false;
	}

	private void refreshViewedQuest(JournalDataSource directSource)
	{
		directSource.markViewedQuestDirty();
		directSource.afterQuestUpdate(client.getTickCount());
	}
}
