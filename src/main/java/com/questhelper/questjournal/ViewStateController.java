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

import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;

/** Manages profile-scoped Quest Journal view restoration and persistence. */
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
final class ViewStateController
{
	@NonNull
	private final Client client;
	@NonNull
	private final ViewStateStore viewStateStore;
	@NonNull
	private final FilterController filterController;

	private volatile ViewState viewState = ViewState.empty();
	private boolean restorePending;
	private boolean profileResetPending;
	private boolean clearOverlayPending;
	private volatile boolean dirty;

	void load(boolean resetMissingProfileState)
	{
		viewState = viewStateStore.load();
		restorePending = viewState.isPresent();
		profileResetPending = resetMissingProfileState && !viewState.isPresent();
		clearOverlayPending = false;
		dirty = false;
	}

	void reset()
	{
		viewState = ViewState.empty();
		restorePending = false;
		profileResetPending = false;
		clearOverlayPending = false;
		dirty = false;
	}

	void resetConfiguration()
	{
		viewStateStore.save(ViewState.empty());
		reset();
		profileResetPending = true;
		clearOverlayPending = true;
	}

	boolean applyRestoredViewState(JournalDataSource dataSource)
	{
		ViewState state = viewState;
		JournalSnapshot source = dataSource.getSnapshot();
		if ((profileResetPending || restorePending) && source.getQuests().isEmpty())
		{
			return false;
		}
		JournalSnapshot.QuestListOptions options = source.getListOptions();
		if (profileResetPending)
		{
			filterController.applyConfiguredProfileDefaults(options);
			profileResetPending = false;
			clearOverlayPending = true;
			return false;
		}
		if (!restorePending || !state.isPresent())
		{
			return false;
		}
		filterController.restoreViewState(state, options);

		JournalSnapshot.SelectedQuest currentSelection = source.getSelectedQuest();
		String currentQuestId = currentSelection == null
			? null
			: currentSelection.getOverview().getId();
		String desiredQuestId = state.getSelectedQuestId();
		boolean selectionChanged = false;
		if (desiredQuestId != null && !desiredQuestId.equals(currentQuestId))
		{
			if (sourceContainsQuest(source, desiredQuestId))
			{
				selectionChanged = dataSource.browseQuest(desiredQuestId);
				if (!selectionChanged)
				{
					return false;
				}
			}
			else if (currentQuestId != null)
			{
				selectionChanged = dataSource.clearBrowsedQuest(currentQuestId);
			}
		}
		else if (desiredQuestId == null && currentQuestId != null)
		{
			selectionChanged = dataSource.clearBrowsedQuest(currentQuestId);
		}
		if (selectionChanged)
		{
			dataSource.markViewedQuestDirty();
			dataSource.afterQuestUpdate(client.getTickCount());
		}
		restorePending = false;
		return true;
	}

	void applyPendingOverlayState(
		JournalOverlay overlay,
		boolean stateRestored)
	{
		if (overlay == null)
		{
			return;
		}
		if (stateRestored)
		{
			overlay.restoreViewState(viewState);
			clearOverlayPending = false;
		}
		else if (clearOverlayPending)
		{
			overlay.clearPersistentViewState();
			clearOverlayPending = false;
		}
	}

	void markDirty(boolean enabled, boolean journalOpen)
	{
		if (enabled && journalOpen)
		{
			dirty = true;
		}
	}

	void flushIfDirty(
		boolean enabled,
		boolean journalOpen,
		JournalOverlay overlay,
		String browsedQuestId)
	{
		if (dirty && !restorePending && !profileResetPending)
		{
			persist(enabled, journalOpen, overlay, browsedQuestId, false);
		}
	}

	void persist(
		boolean enabled,
		boolean journalOpen,
		JournalOverlay overlay,
		String browsedQuestId,
		boolean restoreOnNextOpen)
	{
		if (!enabled || !journalOpen || overlay == null
			|| restorePending || profileResetPending)
		{
			return;
		}
		ViewState captured = overlay.captureViewState(
			browsedQuestId,
			filterController.getFilter());
		if (captured == null)
		{
			return;
		}
		boolean changed = !captured.equals(viewState);
		viewState = captured;
		if (restoreOnNextOpen)
		{
			restorePending = true;
		}
		dirty = false;
		if (changed)
		{
			viewStateStore.save(captured);
		}
	}

	private static boolean sourceContainsQuest(
		JournalSnapshot source,
		String questId)
	{
		for (JournalSnapshot.QuestListItem quest : source.getQuests())
		{
			if (questId.equals(quest.getId()))
			{
				return true;
			}
		}
		return false;
	}
}
