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
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
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
import com.questhelper.QuestHelperPlugin;
import java.awt.Canvas;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.inject.Provider;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.SoundEffectID;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Keybind;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.game.chatbox.ChatboxTextInput;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.ui.overlay.OverlayManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JournalInteractionTest
{
	private static final Rectangle VIEWPORT = new Rectangle(0, 0, 1200, 800);
	private static final Rectangle JOURNAL_BOUNDS = new Rectangle(100, 100, 700, 500);
	private static final Rectangle SEARCH_BOUNDS = new Rectangle(323, 139, 18, 17);
	private static final Rectangle FILTER_BUTTON_BOUNDS = new Rectangle(302, 139, 18, 17);
	private static final Rectangle STAR_BUTTON_BOUNDS = new Rectangle(281, 139, 18, 17);
	private static final Rectangle TITLE_ICON_BOUNDS = new Rectangle(370, 108, 16, 16);
	private static final Rectangle MAXIMIZED_TITLE_ICON_BOUNDS = new Rectangle(570, 8, 16, 16);
	private static final Rectangle LAUNCHER_BOUNDS = new Rectangle(
		20,
		20,
		LauncherGeometry.BUTTON_SIZE,
		LauncherGeometry.BUTTON_SIZE);

	private Fixture fixture;

	@BeforeEach
	void setUp()
	{
		fixture = new Fixture(JournalSnapshot.QuestListOptions.defaults());
	}

	@Test
	void altManagedEventsPassThroughAndReleaseAfterModifierLoss()
	{
		fixture.startAndOpen();
		fixture.renderInteractiveSurfaces();

		MouseEvent press = mouseEvent(
			MouseEvent.MOUSE_PRESSED,
			MouseEvent.BUTTON1,
			InputEvent.ALT_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK,
			200,
			200);
		MouseEvent dragWithoutAlt = mouseEvent(
			MouseEvent.MOUSE_DRAGGED,
			MouseEvent.NOBUTTON,
			InputEvent.BUTTON1_DOWN_MASK,
			230,
			220);
		MouseEvent releaseWithoutAlt = mouseEvent(
			MouseEvent.MOUSE_RELEASED,
			MouseEvent.BUTTON1,
			0,
			230,
			220);

		fixture.manager.mousePressed(press);
		fixture.manager.mouseDragged(dragWithoutAlt);
		fixture.manager.mouseReleased(releaseWithoutAlt);

		assertFalse(press.isConsumed());
		assertFalse(dragWithoutAlt.isConsumed());
		assertFalse(releaseWithoutAlt.isConsumed());

		MouseEvent ordinaryHover = mouseEvent(
			MouseEvent.MOUSE_MOVED,
			MouseEvent.NOBUTTON,
			0,
			200,
			200);
		MouseEvent returnedHover = fixture.manager.mouseMoved(ordinaryHover);
		assertFalse(ordinaryHover.isConsumed());
		assertSame(ordinaryHover, returnedHover);
		assertEquals(new Point(200, 200), fixture.manager.getPointerCanvasPoint());
	}

	@Test
	void middleCameraGesturePassesAcrossBothSurfacesAndNoButtonReleaseEndsIt()
	{
		fixture.startAndOpen();
		fixture.renderInteractiveSurfaces();
		Point launcherCenter = center(LAUNCHER_BOUNDS);

		MouseEvent press = mouseEvent(
			MouseEvent.MOUSE_PRESSED,
			MouseEvent.BUTTON2,
			InputEvent.BUTTON2_DOWN_MASK,
			10,
			10);
		MouseEvent journalDrag = mouseEvent(
			MouseEvent.MOUSE_DRAGGED,
			MouseEvent.NOBUTTON,
			InputEvent.BUTTON2_DOWN_MASK,
			200,
			200);
		MouseEvent launcherMoveWithoutModifier = mouseEvent(
			MouseEvent.MOUSE_MOVED,
			MouseEvent.NOBUTTON,
			0,
			launcherCenter.x,
			launcherCenter.y);
		MouseEvent noButtonRelease = mouseEvent(
			MouseEvent.MOUSE_RELEASED,
			MouseEvent.NOBUTTON,
			0,
			200,
			200);

		fixture.manager.mousePressed(press);
		fixture.manager.mouseDragged(journalDrag);
		MouseEvent middleHover = fixture.manager.mouseMoved(launcherMoveWithoutModifier);
		fixture.manager.mouseReleased(noButtonRelease);

		assertFalse(press.isConsumed());
		assertFalse(journalDrag.isConsumed());
		assertFalse(launcherMoveWithoutModifier.isConsumed());
		assertFalse(noButtonRelease.isConsumed());
		assertSame(launcherMoveWithoutModifier, middleHover);

		MouseEvent ordinaryLauncherHover = mouseEvent(
			MouseEvent.MOUSE_MOVED,
			MouseEvent.NOBUTTON,
			0,
			launcherCenter.x,
			launcherCenter.y);
		MouseEvent launcherHover = fixture.manager.mouseMoved(ordinaryLauncherHover);
		assertFalse(ordinaryLauncherHover.isConsumed());
		assertSame(ordinaryLauncherHover, launcherHover);
		assertEquals(launcherCenter, fixture.manager.getPointerCanvasPoint());
	}

	@Test
	void ordinaryMovementOutsideTheJournalSurfacesPassesThroughUnchanged()
	{
		fixture.startAndOpen();
		fixture.renderInteractiveSurfaces();
		MouseEvent outside = mouseEvent(
			MouseEvent.MOUSE_MOVED,
			MouseEvent.NOBUTTON,
			0,
			900,
			700);

		MouseEvent returned = fixture.manager.mouseMoved(outside);

		assertSame(outside, returned);
		assertFalse(outside.isConsumed());
		assertEquals(new Point(900, 700), fixture.manager.getPointerCanvasPoint());
	}

	@Test
	void tenCleanTitleIconClicksTriggerTheHiddenTitleAction()
	{
		fixture.startAndOpen();
		fixture.renderInteractiveSurfaces();
		clearInvocations(fixture.configManager);

		for (int click = 0; click < 10; click++)
		{
			click(fixture.manager, TITLE_ICON_BOUNDS);
		}

		verify(fixture.journalOverlay, times(10)).recordTitleIconClick();
		verify(fixture.configManager, never()).setConfiguration(any(), any(), any());
	}

	@Test
	void draggingFromTheTitleIconDoesNotCountAsAnEasterEggClick()
	{
		fixture.startAndOpen();
		fixture.renderInteractiveSurfaces();
		Point icon = center(TITLE_ICON_BOUNDS);

		fixture.manager.mousePressed(mouseEvent(
			MouseEvent.MOUSE_PRESSED,
			MouseEvent.BUTTON1,
			InputEvent.BUTTON1_DOWN_MASK,
			icon.x,
			icon.y));
		fixture.manager.mouseDragged(mouseEvent(
			MouseEvent.MOUSE_DRAGGED,
			MouseEvent.NOBUTTON,
			InputEvent.BUTTON1_DOWN_MASK,
			icon.x + 12,
			icon.y + 8));
		fixture.manager.mouseReleased(mouseEvent(
			MouseEvent.MOUSE_RELEASED,
			MouseEvent.BUTTON1,
			0,
			icon.x + 12,
			icon.y + 8));

		verify(fixture.journalOverlay, never()).recordTitleIconClick();
	}

	@Test
	void maximizedTitleIconStillAcceptsACleanClick()
	{
		fixture.startAndOpen();
		fixture.renderInteractiveSurfaces();
		fixture.panelController.toggleMaximized(fixture.journalOverlay);
		when(fixture.journalOverlay.getPanelBounds()).thenReturn(VIEWPORT);
		when(fixture.journalOverlay.getBounds()).thenReturn(new Rectangle(VIEWPORT));

		click(fixture.manager, MAXIMIZED_TITLE_ICON_BOUNDS);

		verify(fixture.journalOverlay).recordTitleIconClick();
	}

	@Test
	void launcherPrimaryClickOwnsTheWholeGestureOverNativeWidgets()
	{
		fixture.startAndOpen();
		fixture.renderInteractiveSurfaces();
		Point launcherCenter = center(LAUNCHER_BOUNDS);
		MouseEvent press = mouseEvent(
			MouseEvent.MOUSE_PRESSED,
			MouseEvent.BUTTON1,
			InputEvent.BUTTON1_DOWN_MASK,
			launcherCenter.x,
			launcherCenter.y);
		MouseEvent release = mouseEvent(
			MouseEvent.MOUSE_RELEASED,
			MouseEvent.BUTTON1,
			0,
			launcherCenter.x,
			launcherCenter.y);
		MouseEvent click = mouseEvent(
			MouseEvent.MOUSE_CLICKED,
			MouseEvent.BUTTON1,
			0,
			launcherCenter.x,
			launcherCenter.y);

		fixture.manager.mousePressed(press);
		fixture.manager.mouseReleased(release);
		fixture.manager.mouseClicked(click);

		assertTrue(press.isConsumed());
		assertTrue(release.isConsumed());
		assertTrue(click.isConsumed());
		assertFalse(fixture.manager.isJournalOpen());
	}

	@Test
	void capturedLauncherClickSurvivesAnOverlappingNativeMenuOpening()
	{
		fixture.startAndOpen();
		fixture.renderInteractiveSurfaces();
		Point launcherCenter = center(LAUNCHER_BOUNDS);
		MouseEvent press = mouseEvent(
			MouseEvent.MOUSE_PRESSED,
			MouseEvent.BUTTON1,
			InputEvent.BUTTON1_DOWN_MASK,
			launcherCenter.x,
			launcherCenter.y);
		MouseEvent release = mouseEvent(
			MouseEvent.MOUSE_RELEASED,
			MouseEvent.BUTTON1,
			0,
			launcherCenter.x,
			launcherCenter.y);
		MouseEvent click = mouseEvent(
			MouseEvent.MOUSE_CLICKED,
			MouseEvent.BUTTON1,
			0,
			launcherCenter.x,
			launcherCenter.y);

		fixture.manager.mousePressed(press);
		fixture.manager.onMenuOpened(mock(MenuOpened.class));
		fixture.manager.mouseReleased(release);
		fixture.manager.mouseClicked(click);

		assertTrue(press.isConsumed());
		assertTrue(release.isConsumed());
		assertTrue(click.isConsumed());
		assertFalse(fixture.manager.isJournalOpen());
	}

	@Test
	void launcherSecondaryClickPassesThroughToTheNativeMenu()
	{
		fixture.startAndOpen();
		fixture.renderInteractiveSurfaces();
		Point launcherCenter = center(LAUNCHER_BOUNDS);
		MouseEvent press = mouseEvent(
			MouseEvent.MOUSE_PRESSED,
			MouseEvent.BUTTON3,
			InputEvent.BUTTON3_DOWN_MASK,
			launcherCenter.x,
			launcherCenter.y);
		MouseEvent release = mouseEvent(
			MouseEvent.MOUSE_RELEASED,
			MouseEvent.BUTTON3,
			0,
			launcherCenter.x,
			launcherCenter.y);
		MouseEvent click = mouseEvent(
			MouseEvent.MOUSE_CLICKED,
			MouseEvent.BUTTON3,
			0,
			launcherCenter.x,
			launcherCenter.y);

		assertSame(press, fixture.manager.mousePressed(press));
		assertSame(release, fixture.manager.mouseReleased(release));
		assertSame(click, fixture.manager.mouseClicked(click));
		assertFalse(press.isConsumed());
		assertFalse(release.isConsumed());
		assertFalse(click.isConsumed());
	}

	@Test
	void noButtonDispatchStillClassifiesHeldMouseButtons()
	{
		MouseEvent primary = mouseEvent(
			MouseEvent.MOUSE_DRAGGED,
			MouseEvent.NOBUTTON,
			InputEvent.BUTTON1_DOWN_MASK,
			20,
			20);
		MouseEvent middle = mouseEvent(
			MouseEvent.MOUSE_DRAGGED,
			MouseEvent.NOBUTTON,
			InputEvent.BUTTON2_DOWN_MASK,
			20,
			20);
		MouseEvent secondary = mouseEvent(
			MouseEvent.MOUSE_DRAGGED,
			MouseEvent.NOBUTTON,
			InputEvent.BUTTON3_DOWN_MASK,
			20,
			20);

		assertTrue(QuestJournalManager.isPrimaryButtonEvent(primary));
		assertTrue(QuestJournalManager.isMiddleButtonEvent(middle));
		assertTrue(QuestJournalManager.isSecondaryButtonEvent(secondary));
		assertFalse(QuestJournalManager.isMiddleButtonEvent(primary));
		assertFalse(QuestJournalManager.isPrimaryButtonEvent(secondary));
	}

	@Test
	void nativeChatboxSearchLiveFiltersAndClosingRestoresTheList()
	{
		fixture.startAndOpen();
		fixture.manager.toggleQuestSearch();

		assertSame(fixture.searchInput, fixture.currentChatboxInput.get());
		assertNotNull(fixture.searchChanged.get());
		fixture.searchChanged.get().accept("dr");
		assertEquals("dr", fixture.manager.getQuestFilter().getSearchText());

		KeyEvent escape = pressed(KeyEvent.VK_ESCAPE, (char) 27);
		fixture.manager.keyPressed(escape);
		assertFalse(escape.isConsumed());
		assertTrue(fixture.manager.isJournalOpen());

		fixture.currentChatboxInput.set(null);
		fixture.searchClosed.get().run();
		assertNull(fixture.currentChatboxInput.get());
		assertEquals("", fixture.manager.getQuestFilter().getSearchText());
	}

	@Test
	void externalChatboxInputKeepsOwnershipOfJournalKeys()
	{
		fixture.startAndOpen();
		fixture.currentChatboxInput.set(mock(ChatboxTextInput.class));

		KeyEvent escape = pressed(KeyEvent.VK_ESCAPE, (char) 27);
		fixture.manager.keyPressed(escape);
		assertFalse(escape.isConsumed());
		assertTrue(fixture.manager.isJournalOpen());

		KeyEvent hotkey = pressed(KeyEvent.VK_J, 'j');
		fixture.manager.keyPressed(hotkey);
		assertFalse(hotkey.isConsumed());
		assertTrue(fixture.manager.isJournalOpen());
	}

	@Test
	void closingSearchOnlyClosesItsOwnedChatboxInput()
	{
		fixture.startAndOpen();
		fixture.manager.toggleQuestSearch();
		fixture.manager.toggleQuestSearch();

		verify(fixture.chatboxPanelManager).close();
		verify(fixture.client, times(2)).playSoundEffect(SoundEffectID.UI_BOOP);
	}

	@Test
	void replacedSearchDoesNotCloseAnotherChatboxInput()
	{
		fixture.startAndOpen();
		fixture.manager.toggleQuestSearch();
		ChatboxTextInput replacement = mock(ChatboxTextInput.class);
		fixture.currentChatboxInput.set(replacement);

		fixture.manager.toggleQuestSearch();

		verify(fixture.chatboxPanelManager, never()).close();
		assertSame(replacement, fixture.currentChatboxInput.get());
	}

	@Test
	void questSearchIconRequiresAMatchingPressAndRelease()
	{
		fixture.startAndOpen();
		fixture.renderInteractiveSurfaces();

		fixture.manager.mousePressed(mouseEvent(
			MouseEvent.MOUSE_PRESSED,
			MouseEvent.BUTTON1,
			0,
			SEARCH_BOUNDS.x + 2,
			SEARCH_BOUNDS.y + 2));
		fixture.manager.mouseReleased(mouseEvent(
			MouseEvent.MOUSE_RELEASED,
			MouseEvent.BUTTON1,
			0,
			SEARCH_BOUNDS.x + SEARCH_BOUNDS.width + 3,
			SEARCH_BOUNDS.y + 2));
		assertNull(fixture.currentChatboxInput.get());
		verify(fixture.client, never()).playSoundEffect(any(int.class));

		click(fixture.manager, new Point(SEARCH_BOUNDS.x + 2, SEARCH_BOUNDS.y + 2));

		verify(fixture.chatboxPanelManager).openTextInput("Search quest journal");
		verify(fixture.searchInput).build();
		verify(fixture.client).playSoundEffect(SoundEffectID.UI_BOOP);
		assertSame(fixture.searchInput, fixture.currentChatboxInput.get());
	}

	@Test
	void questSearchClickMarshalsSoundAndChatboxCreationToTheClientThread()
	{
		fixture.startAndOpen();
		fixture.renderInteractiveSurfaces();
		AtomicReference<Runnable> queuedClientTask = new AtomicReference<>();
		when(fixture.client.isClientThread()).thenReturn(false);
		doAnswer(invocation ->
		{
			queuedClientTask.set(invocation.getArgument(0));
			return null;
		}).when(fixture.clientThread).invokeLater(any(Runnable.class));

		click(fixture.manager, new Point(SEARCH_BOUNDS.x + 2, SEARCH_BOUNDS.y + 2));

		assertNotNull(queuedClientTask.get());
		verify(fixture.client, never()).playSoundEffect(any(int.class));
		verify(fixture.chatboxPanelManager, never()).openTextInput(any());

		when(fixture.client.isClientThread()).thenReturn(true);
		queuedClientTask.get().run();

		verify(fixture.client).playSoundEffect(SoundEffectID.UI_BOOP);
		verify(fixture.chatboxPanelManager).openTextInput("Search quest journal");
		assertSame(fixture.searchInput, fixture.currentChatboxInput.get());
	}

	@Test
	void filterVisibilityButtonRequiresAMatchingClickAndPlaysOneUiSound()
	{
		fixture.startAndOpen();
		fixture.renderInteractiveSurfaces();

		fixture.manager.mousePressed(mouseEvent(
			MouseEvent.MOUSE_PRESSED,
			MouseEvent.BUTTON1,
			InputEvent.BUTTON1_DOWN_MASK,
			FILTER_BUTTON_BOUNDS.x + 2,
			FILTER_BUTTON_BOUNDS.y + 2));
		fixture.manager.mouseReleased(mouseEvent(
			MouseEvent.MOUSE_RELEASED,
			MouseEvent.BUTTON1,
			0,
			FILTER_BUTTON_BOUNDS.x + FILTER_BUTTON_BOUNDS.width + 2,
			FILTER_BUTTON_BOUNDS.y + 2));

		verify(fixture.journalOverlay, never()).toggleFilterVisibility();
		verify(fixture.client, never()).playSoundEffect(any(int.class));

		click(fixture.manager, new Point(
			FILTER_BUTTON_BOUNDS.x + 2,
			FILTER_BUTTON_BOUNDS.y + 2));

		verify(fixture.journalOverlay).toggleFilterVisibility();
		verify(fixture.client).playSoundEffect(SoundEffectID.UI_BOOP);
	}

	@Test
	void starQuestButtonRequiresAMatchingClickAndPersistsPerProfile()
	{
		when(fixture.configManager.getRSProfileKey()).thenReturn("profile");
		fixture.startAndOpen();
		fixture.renderInteractiveSurfaces();

		fixture.manager.mousePressed(mouseEvent(
			MouseEvent.MOUSE_PRESSED,
			MouseEvent.BUTTON1,
			InputEvent.BUTTON1_DOWN_MASK,
			STAR_BUTTON_BOUNDS.x + 2,
			STAR_BUTTON_BOUNDS.y + 2));
		fixture.manager.mouseReleased(mouseEvent(
			MouseEvent.MOUSE_RELEASED,
			MouseEvent.BUTTON1,
			0,
			STAR_BUTTON_BOUNDS.x + STAR_BUTTON_BOUNDS.width + 2,
			STAR_BUTTON_BOUNDS.y + 2));

		verify(fixture.starredQuestStore, never()).save(any());
		assertFalse(fixture.manager.isQuestStarred("COOKS_ASSISTANT"));

		click(fixture.manager, STAR_BUTTON_BOUNDS);

		verify(fixture.starredQuestStore).save(Collections.singleton("COOKS_ASSISTANT"));
		assertTrue(fixture.manager.isQuestStarred("COOKS_ASSISTANT"));

		clearInvocations(fixture.starredQuestStore);
		click(fixture.manager, STAR_BUTTON_BOUNDS);

		verify(fixture.starredQuestStore).save(Collections.emptySet());
		assertFalse(fixture.manager.isQuestStarred("COOKS_ASSISTANT"));
		verify(fixture.client, times(2)).playSoundEffect(SoundEffectID.UI_BOOP);
	}

	@Test
	void resettingFilterConfigurationDeletesPersistedStars()
	{
		when(fixture.starredQuestStore.load())
			.thenReturn(Collections.singleton("COOKS_ASSISTANT"));
		fixture.filterController.reloadStarredQuests();
		assertTrue(fixture.filterController.isQuestStarred("COOKS_ASSISTANT"));
		clearInvocations(fixture.starredQuestStore);

		fixture.filterController.resetConfiguration();

		verify(fixture.starredQuestStore).clear();
		assertFalse(fixture.filterController.isQuestStarred("COOKS_ASSISTANT"));
		assertTrue(fixture.filterController.getStarredQuestIds().isEmpty());
	}

	@Test
	void unloadingAProfileDoesNotDeletePersistedStars()
	{
		when(fixture.starredQuestStore.load())
			.thenReturn(Collections.singleton("COOKS_ASSISTANT"));
		fixture.filterController.reloadStarredQuests();
		clearInvocations(fixture.starredQuestStore);

		fixture.filterController.unloadStarredQuests();

		verify(fixture.starredQuestStore, never()).clear();
		assertFalse(fixture.filterController.isQuestStarred("COOKS_ASSISTANT"));
		assertTrue(fixture.filterController.getStarredQuestIds().isEmpty());
	}

	@Test
	void staleQueuedSearchClickCannotAffectANewerJournalLifecycle()
	{
		fixture.startAndOpen();
		fixture.manager.keyReleased(new KeyEvent(
			new Canvas(),
			KeyEvent.KEY_RELEASED,
			2L,
			0,
			KeyEvent.VK_J,
			KeyEvent.CHAR_UNDEFINED));
		List<Runnable> queuedClientTasks = new ArrayList<>();
		when(fixture.client.isClientThread()).thenReturn(false);
		doAnswer(invocation ->
		{
			queuedClientTasks.add(invocation.getArgument(0));
			return null;
		}).when(fixture.clientThread).invokeLater(any(Runnable.class));

		fixture.manager.toggleQuestSearch();
		assertEquals(1, queuedClientTasks.size());
		Runnable staleSearchClick = queuedClientTasks.get(0);
		fixture.manager.keyPressed(pressed(KeyEvent.VK_J, KeyEvent.CHAR_UNDEFINED));
		fixture.manager.keyReleased(new KeyEvent(
			new Canvas(),
			KeyEvent.KEY_RELEASED,
			3L,
			0,
			KeyEvent.VK_J,
			KeyEvent.CHAR_UNDEFINED));
		fixture.manager.keyPressed(pressed(KeyEvent.VK_J, KeyEvent.CHAR_UNDEFINED));

		when(fixture.client.isClientThread()).thenReturn(true);
		staleSearchClick.run();

		verify(fixture.chatboxPanelManager, never()).openTextInput(any());
		verify(fixture.client, never()).playSoundEffect(any(int.class));
	}

	@Test
	void matchingFilterTriggerClickPlaysOneUiSound()
	{
		fixture.startAndOpen();
		fixture.renderInteractiveSurfaces();
		Point filterPoint = new Point(180, 180);
		when(fixture.journalOverlay.filterControlAt(filterPoint))
			.thenReturn(JournalOverlay.FilterControl.DIFFICULTY);

		click(fixture.manager, filterPoint);

		verify(fixture.journalOverlay).toggleFilterDropdown(
			JournalOverlay.FilterControl.DIFFICULTY);
		verify(fixture.client).playSoundEffect(SoundEffectID.UI_BOOP);
	}

	@Test
	void matchingFilterOptionClickPlaysOneUiSound()
	{
		fixture.startAndOpen();
		fixture.renderInteractiveSurfaces();
		Point optionPoint = new Point(180, 180);
		JournalOverlay.FilterSelection selection = new JournalOverlay.FilterSelection(
			JournalOverlay.FilterControl.ORDER,
			JournalSnapshot.QuestOrder.A_TO_Z,
			"Alphabetical");
		when(fixture.journalOverlay.filterSelectionAt(optionPoint)).thenReturn(selection);

		click(fixture.manager, optionPoint);

		assertEquals(JournalSnapshot.QuestOrder.A_TO_Z, fixture.manager.getQuestFilter().getOrder());
		verify(fixture.journalOverlay).closeFilterDropdown();
		verify(fixture.client).playSoundEffect(SoundEffectID.UI_BOOP);
	}

	@Test
	void starredTypeFilterIsEnabledByDefaultAndParticipatesInBulkActions()
	{
		fixture.startAndOpen();

		assertTrue(fixture.manager.getQuestFilter().isStarredSelected());
		fixture.filterController.applyFilterSelection(new JournalOverlay.FilterSelection(
			JournalOverlay.FilterControl.TYPE,
			JournalOverlay.TypeFilterOption.STARRED,
			"Starred"), fixture.options);
		assertFalse(fixture.manager.getQuestFilter().isStarredSelected());

		fixture.filterController.applyFilterSelection(
			JournalOverlay.FilterSelection.selectAll(
				JournalOverlay.FilterControl.TYPE),
			fixture.options);
		assertTrue(fixture.manager.getQuestFilter().isStarredSelected());
		assertEquals(
			new LinkedHashSet<>(fixture.options.getTypes()),
			fixture.manager.getQuestFilter().getTypes());

		fixture.filterController.applyFilterSelection(
			JournalOverlay.FilterSelection.selectNone(
				JournalOverlay.FilterControl.TYPE),
			fixture.options);
		assertFalse(fixture.manager.getQuestFilter().isStarredSelected());
		assertTrue(fixture.manager.getQuestFilter().getTypes().isEmpty());

		fixture.filterController.resetFilter(
			JournalOverlay.FilterControl.TYPE,
			fixture.options);
		assertTrue(fixture.manager.getQuestFilter().isStarredSelected());
	}

	@Test
	void statusFilterDefaultsAndResetsToAllStates()
	{
		fixture.startAndOpen();
		Set<JournalSnapshot.QuestState> allStates = new LinkedHashSet<>(
			Arrays.asList(JournalSnapshot.QuestState.values()));

		assertEquals(allStates, fixture.manager.getQuestFilter().getStates());

		fixture.filterController.setQuestStateSelections(Collections.singleton(
			JournalSnapshot.QuestState.NOT_STARTED));
		fixture.filterController.resetFilter(
			JournalOverlay.FilterControl.STATUS,
			fixture.options);

		assertEquals(allStates, fixture.manager.getQuestFilter().getStates());
	}

	@Test
	void matchingChecklistToggleExpandsOnlyOnReleaseAndPlaysOneUiSound()
	{
		fixture.startAndOpen();
		fixture.renderInteractiveSurfaces();
		Point togglePoint = new Point(180, 180);
		when(fixture.journalOverlay.checklistToggleIdAt(togglePoint))
			.thenReturn("COOKS_ASSISTANT:0");

		MouseEvent press = mouseEvent(
			MouseEvent.MOUSE_PRESSED,
			MouseEvent.BUTTON1,
			InputEvent.BUTTON1_DOWN_MASK,
			togglePoint.x,
			togglePoint.y);
		MouseEvent release = mouseEvent(
			MouseEvent.MOUSE_RELEASED,
			MouseEvent.BUTTON1,
			0,
			togglePoint.x,
			togglePoint.y);

		fixture.manager.mousePressed(press);
		verify(fixture.journalOverlay, never()).toggleChecklist(any());
		fixture.manager.mouseReleased(release);

		assertTrue(press.isConsumed());
		assertTrue(release.isConsumed());
		verify(fixture.journalOverlay).toggleChecklist("COOKS_ASSISTANT:0");
		verify(fixture.client).playSoundEffect(SoundEffectID.UI_BOOP);
	}

	@Test
	void closingTheWindowWithSearchOpenPlaysOnlyTheWindowSound()
	{
		fixture.startAndOpen();
		fixture.renderInteractiveSurfaces();
		fixture.manager.toggleQuestSearch();
		clearInvocations(fixture.client);
		Point closePoint = new Point(200, 150);
		when(fixture.journalOverlay.isCloseButton(closePoint)).thenReturn(true);

		click(fixture.manager, closePoint);

		verify(fixture.client).playSoundEffect(SoundEffectID.UI_BOOP);
		verify(fixture.chatboxPanelManager).close();
		assertFalse(fixture.manager.isJournalOpen());
	}

	@Test
	void checklistBulkActionsSelectNoneAndAllWithoutClosingTheDropdown()
	{
		fixture.startAndOpen();

		JournalOverlay.FilterSelection none = JournalOverlay.FilterSelection.selectNone(
			JournalOverlay.FilterControl.TYPE);
		JournalOverlay.FilterSelection all = JournalOverlay.FilterSelection.selectAll(
			JournalOverlay.FilterControl.TYPE);

		fixture.filterController.applyFilterSelection(none, fixture.options);
		assertTrue(fixture.manager.getQuestFilter().getTypes().isEmpty());
		assertFalse(FilterController.shouldCloseFilterDropdown(none));

		fixture.filterController.applyFilterSelection(all, fixture.options);
		assertEquals(
			new LinkedHashSet<>(fixture.options.getTypes()),
			fixture.manager.getQuestFilter().getTypes());
		assertFalse(FilterController.shouldCloseFilterDropdown(all));
	}

	@Test
	void explicitActiveQuestActionRequiresAMatchingPressAndRelease()
	{
		fixture.startAndOpen();
		when(fixture.dataSource.activateQuest("COOKS_ASSISTANT")).thenReturn(true);

		invokeMismatchedThenMatchingRelease(
			fixture.manager::activateQuestIfReleased,
			() -> verify(fixture.dataSource, never()).activateQuest(any()));

		verify(fixture.dataSource).activateQuest("COOKS_ASSISTANT");
		verify(fixture.dataSource, never()).browseQuest(any());
	}

	@Test
	void clearActiveQuestRequiresAMatchingPressAndRelease()
	{
		fixture.startAndOpen();
		when(fixture.dataSource.stopActiveQuest("COOKS_ASSISTANT")).thenReturn(true);

		invokeMismatchedThenMatchingRelease(
			fixture.manager::clearActiveQuestIfReleased,
			() -> verify(fixture.dataSource, never()).stopActiveQuest(any()));

		verify(fixture.dataSource).stopActiveQuest("COOKS_ASSISTANT");
		verify(fixture.dataSource, never()).activateQuest(any());
	}

	@Test
	void clearActiveQuestButtonRoutesItsMatchingClickToTheDataSource()
	{
		fixture.startAndOpen();
		fixture.renderInteractiveSurfaces();
		Point clearPoint = new Point(180, 180);
		when(fixture.journalOverlay.clearActiveQuestIdAt(clearPoint))
			.thenReturn("COOKS_ASSISTANT");
		when(fixture.dataSource.stopActiveQuest("COOKS_ASSISTANT")).thenReturn(true);

		click(fixture.manager, clearPoint);

		verify(fixture.dataSource).stopActiveQuest("COOKS_ASSISTANT");
	}

	@Test
	void returnToActiveQuestRequiresAMatchingPressAndRelease()
	{
		fixture.startAndOpen();
		when(fixture.dataSource.browseActiveQuest()).thenReturn(true);

		invokeMismatchedThenMatchingRelease(
			fixture.manager::returnToActiveQuestIfReleased,
			() -> verify(fixture.dataSource, never()).browseActiveQuest());

		verify(fixture.dataSource).browseActiveQuest();
		verify(fixture.dataSource, never()).activateQuest(any());
	}

	@Test
	void linkedQuestNavigationBrowsesWithoutActivating()
	{
		fixture.startAndOpen();
		when(fixture.dataSource.browseQuest("COOKS_ASSISTANT")).thenReturn(true);

		fixture.manager.selectLinkedQuestIfReleased("COOKS_ASSISTANT", "COOKS_ASSISTANT");

		verify(fixture.dataSource).browseQuest("COOKS_ASSISTANT");
		verify(fixture.dataSource, never()).activateQuest(any());
	}

	@Test
	void clickingTheFocusedListQuestClearsOnlyTheJournalView()
	{
		JournalSnapshot snapshot = snapshotWithSelection(
			"COOKS_ASSISTANT",
			"Cook's Assistant");
		when(fixture.dataSource.getSnapshot()).thenReturn(snapshot);
		when(fixture.dataSource.getSnapshot(any(JournalSnapshot.QuestFilter.class)))
			.thenReturn(snapshot);
		when(fixture.dataSource.clearBrowsedQuest("COOKS_ASSISTANT")).thenReturn(true);
		fixture.startAndOpen();

		fixture.manager.toggleBrowsedQuestIfReleased(
			"COOKS_ASSISTANT",
			"COOKS_ASSISTANT");

		verify(fixture.dataSource).clearBrowsedQuest("COOKS_ASSISTANT");
		verify(fixture.dataSource, never()).browseQuest(any());
		verify(fixture.dataSource, never()).activateQuest(any());
		verify(fixture.dataSource, never()).stopActiveQuest(any());
	}

	@Test
	void clickingADifferentListQuestBrowsesWithoutClearingTheCurrentView()
	{
		JournalSnapshot snapshot = snapshotWithSelection(
			"COOKS_ASSISTANT",
			"Cook's Assistant");
		when(fixture.dataSource.getSnapshot()).thenReturn(snapshot);
		when(fixture.dataSource.getSnapshot(any(JournalSnapshot.QuestFilter.class)))
			.thenReturn(snapshot);
		when(fixture.dataSource.browseQuest("DRAGON_SLAYER_I")).thenReturn(true);
		fixture.startAndOpen();

		fixture.manager.toggleBrowsedQuestIfReleased(
			"DRAGON_SLAYER_I",
			"DRAGON_SLAYER_I");

		verify(fixture.dataSource).browseQuest("DRAGON_SLAYER_I");
		verify(fixture.dataSource, never()).clearBrowsedQuest(any());
		verify(fixture.dataSource, never()).activateQuest(any());
	}

	@Test
	void filterPopupPaddingConsumesTheClickInsteadOfNavigatingUnderneath()
	{
		fixture.startAndOpen();
		fixture.renderInteractiveSurfaces();
		Point popupPadding = new Point(200, 200);
		when(fixture.journalOverlay.isFilterDropdownOpen()).thenReturn(true);
		when(fixture.journalOverlay.isFilterPopupSurface(popupPadding)).thenReturn(true);
		when(fixture.journalOverlay.isSearchControl(popupPadding)).thenReturn(true);
		when(fixture.journalOverlay.filterControlAt(popupPadding))
			.thenReturn(JournalOverlay.FilterControl.STATUS);
		when(fixture.journalOverlay.returnToActiveQuestIdAt(popupPadding))
			.thenReturn("COOKS_ASSISTANT");
		MouseEvent press = mouseEvent(
			MouseEvent.MOUSE_PRESSED,
			MouseEvent.BUTTON1,
			InputEvent.BUTTON1_DOWN_MASK,
			popupPadding.x,
			popupPadding.y);
		MouseEvent release = mouseEvent(
			MouseEvent.MOUSE_RELEASED,
			MouseEvent.BUTTON1,
			0,
			popupPadding.x,
			popupPadding.y);

		fixture.manager.mousePressed(press);
		fixture.manager.mouseReleased(release);

		assertTrue(press.isConsumed());
		assertTrue(release.isConsumed());
		verify(fixture.journalOverlay).closeFilterDropdown();
		verify(fixture.dataSource, never()).browseActiveQuest();
		verify(fixture.dataSource, never()).browseQuest(any());
		verify(fixture.dataSource, never()).activateQuest(any());
		verify(fixture.chatboxPanelManager, never()).openTextInput(any());
		verify(fixture.journalOverlay, never()).toggleFilterDropdown(any());
	}

	@Test
	void rightClickingAFilterRestoresItsPublishedDefault()
	{
		JournalSnapshot.QuestListOptions options = JournalSnapshot.QuestListOptions.defaults();
		fixture = new Fixture(options);
		fixture.startAndOpen();
		fixture.renderInteractiveSurfaces();
		fixture.filterController.setQuestDifficultySelections(
			Collections.singleton(JournalSnapshot.QuestDifficulty.NOVICE),
			fixture.options);
		assertEquals(
			Collections.singleton(JournalSnapshot.QuestDifficulty.NOVICE),
			fixture.manager.getQuestFilter().getDifficulties());

		Point filterPoint = new Point(180, 180);
		when(fixture.journalOverlay.filterControlAt(filterPoint))
			.thenReturn(JournalOverlay.FilterControl.DIFFICULTY);
		MouseEvent press = mouseEvent(
			MouseEvent.MOUSE_PRESSED,
			MouseEvent.BUTTON3,
			InputEvent.BUTTON3_DOWN_MASK,
			filterPoint.x,
			filterPoint.y);
		MouseEvent release = mouseEvent(
			MouseEvent.MOUSE_RELEASED,
			MouseEvent.BUTTON3,
			0,
			filterPoint.x,
			filterPoint.y);

		fixture.manager.mousePressed(press);
		fixture.manager.mouseReleased(release);

		assertTrue(press.isConsumed());
		assertTrue(release.isConsumed());
		assertEquals(
			JournalSnapshot.QuestListOptions.defaults().getConfiguredDifficulties(),
			fixture.manager.getQuestFilter().getDifficulties());
		verify(fixture.journalOverlay).closeFilterDropdown();
	}

	@Test
	void activeMainModalUsesTheMovementAncestorIntersection()
	{
		Widget activeMainModal = widget(300, new Rectangle(398, 162, 512, 334), true);
		Widget movementParent = widget(200, new Rectangle(0, 0, 1308, 700), false);
		Widget movementRoot = widget(100, new Rectangle(0, 0, 1381, 659), false);
		when(movementParent.getDynamicChildren()).thenReturn(new Widget[]{activeMainModal});
		when(movementRoot.getDynamicChildren()).thenReturn(new Widget[]{movementParent});
		when(fixture.client.getTopLevelInterfaceId())
			.thenReturn(InterfaceID.ToplevelOsm.MAINMODAL >>> 16);
		when(fixture.client.getWidget(InterfaceID.ToplevelOsm.MAINMODAL))
			.thenReturn(activeMainModal);
		when(fixture.client.getWidgetRoots()).thenReturn(new Widget[]{movementRoot});

		assertEquals(
			new Rectangle(0, 0, 1308, 659),
			fixture.manager.getJournalContentBounds(new Rectangle(0, 0, 1381, 800)));
	}

	@Test
	void safeAreaCalculationIsReusedOnlyWithinTheSameGameCycle()
	{
		Widget activeMainModal = widget(300, new Rectangle(398, 162, 512, 334), true);
		Widget movementParent = widget(200, new Rectangle(0, 0, 1308, 700), false);
		Widget movementRoot = widget(100, new Rectangle(0, 0, 1381, 659), false);
		when(movementParent.getDynamicChildren()).thenReturn(new Widget[]{activeMainModal});
		when(movementRoot.getDynamicChildren()).thenReturn(new Widget[]{movementParent});
		when(fixture.client.getTopLevelInterfaceId())
			.thenReturn(InterfaceID.ToplevelOsm.MAINMODAL >>> 16);
		when(fixture.client.getWidget(InterfaceID.ToplevelOsm.MAINMODAL))
			.thenReturn(activeMainModal);
		when(fixture.client.getWidgetRoots()).thenReturn(new Widget[]{movementRoot});
		when(fixture.client.getGameCycle()).thenReturn(5, 5, 6);
		Rectangle canvas = new Rectangle(0, 0, 1381, 800);

		Rectangle first = fixture.manager.getJournalContentBounds(canvas);
		first.x = 999;
		assertEquals(
			new Rectangle(0, 0, 1308, 659),
			fixture.manager.getJournalContentBounds(canvas));
		assertEquals(
			new Rectangle(0, 0, 1308, 659),
			fixture.manager.getJournalContentBounds(canvas));

		verify(fixture.client, times(2)).getWidget(InterfaceID.ToplevelOsm.MAINMODAL);
	}

	@Test
	void safeAreaFallsBackToTheCanvasWhenNoMainModalExists()
	{
		when(fixture.client.getTopLevelInterfaceId()).thenReturn(-1);
		when(fixture.client.getWidgetRoots()).thenReturn(new Widget[0]);

		assertEquals(
			new Rectangle(0, 0, 1000, 700),
			fixture.manager.getJournalContentBounds(new Rectangle(0, 0, 1000, 700)));
	}

	@Test
	void fullCanvasPreferenceBypassesTheSafeArea()
	{
		Widget mainModal = widget(300, new Rectangle(100, 120, 500, 300), false);
		when(fixture.config.journalKeepWithinGameArea()).thenReturn(false);
		when(fixture.client.getTopLevelInterfaceId())
			.thenReturn(InterfaceID.ToplevelOsm.MAINMODAL >>> 16);
		when(fixture.client.getWidget(InterfaceID.ToplevelOsm.MAINMODAL))
			.thenReturn(mainModal);

		assertEquals(
			new Rectangle(0, 0, 1381, 800),
			fixture.manager.getJournalContentBounds(new Rectangle(0, 0, 1381, 800)));
	}

	@Test
	void titlebarResetWhileMaximizedPreservesTheRestoreBounds()
	{
		Rectangle restored = new Rectangle(150, 90, 760, 520);
		stubPersistedPanelBounds(restored);
		when(fixture.journalOverlay.getPanelBounds()).thenReturn(restored);
		when(fixture.journalOverlay.getViewportBounds()).thenReturn(VIEWPORT);
		fixture.startAndOpen();

		assertEquals(restored, fixture.manager.getJournalRenderState(VIEWPORT).bounds());
		fixture.panelController.toggleMaximized(fixture.journalOverlay);
		assertTrue(fixture.manager.getJournalRenderState(VIEWPORT).maximized());
		assertEquals(VIEWPORT, fixture.manager.getJournalRenderState(VIEWPORT).bounds());

		fixture.manager.onJournalOverlayReset();
		assertTrue(fixture.manager.getJournalRenderState(VIEWPORT).maximized());

		fixture.panelController.toggleMaximized(fixture.journalOverlay);
		assertFalse(fixture.manager.getJournalRenderState(VIEWPORT).maximized());
		assertEquals(restored, fixture.manager.getJournalRenderState(VIEWPORT).bounds());
		verify(fixture.journalOverlay).beginPanelStateTransition(restored);
	}

	@Test
	void pluginResetClearsMaximizedPanelGeometry()
	{
		Rectangle restored = new Rectangle(150, 90, 760, 520);
		stubPersistedPanelBounds(restored);
		when(fixture.journalOverlay.getPanelBounds()).thenReturn(restored);
		when(fixture.journalOverlay.getViewportBounds()).thenReturn(VIEWPORT);
		fixture.startAndOpen();
		fixture.panelController.toggleMaximized(fixture.journalOverlay);
		assertTrue(fixture.panelController.isMaximized());
		clearInvocations(
			fixture.configManager,
			fixture.journalOverlay,
			fixture.overlayManager,
			fixture.viewStateStore);

		fixture.manager.resetConfiguration();

		assertFalse(fixture.panelController.isMaximized());
		assertEquals(
			JournalGeometry.defaultPanelBounds(VIEWPORT),
			fixture.manager.getJournalRenderState(VIEWPORT).bounds());
		verify(fixture.overlayManager).resetOverlay(fixture.journalOverlay);
		verify(fixture.journalOverlay)
			.beginPanelStateTransition(new Rectangle());
		verify(fixture.journalOverlay).clearPersistentViewState();
		verify(fixture.viewStateStore).save(ViewState.empty());
	}

	@Test
	void titlebarResetClearsNonMaximizedPanelGeometry()
	{
		Rectangle restored = new Rectangle(150, 90, 760, 520);
		stubPersistedPanelBounds(restored);
		fixture.startAndOpen();
		assertEquals(restored, fixture.manager.getJournalRenderState(VIEWPORT).bounds());

		fixture.manager.onJournalOverlayReset();

		assertEquals(
			JournalGeometry.defaultPanelBounds(VIEWPORT),
			fixture.manager.getJournalRenderState(VIEWPORT).bounds());
	}

	private void stubPersistedPanelBounds(Rectangle bounds)
	{
		when(fixture.configManager.getConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			"journalPanelInitialized",
			Boolean.class)).thenReturn(true);
		when(fixture.configManager.getConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			"journalPanelX",
			Integer.class)).thenReturn(bounds.x);
		when(fixture.configManager.getConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			"journalPanelY",
			Integer.class)).thenReturn(bounds.y);
		when(fixture.configManager.getConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			"journalPanelWidth",
			Integer.class)).thenReturn(bounds.width);
		when(fixture.configManager.getConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			"journalPanelHeight",
			Integer.class)).thenReturn(bounds.height);
	}

	private static void invokeMismatchedThenMatchingRelease(
		BiConsumer<String, String> action,
		Runnable verifyMismatchedRelease)
	{
		action.accept("COOKS_ASSISTANT", "DRAGON_SLAYER_I");
		verifyMismatchedRelease.run();
		action.accept("COOKS_ASSISTANT", "COOKS_ASSISTANT");
	}

	private static Point center(Rectangle bounds)
	{
		return new Point(
			(int) bounds.getCenterX(),
			(int) bounds.getCenterY());
	}

	private static void click(QuestJournalManager manager, Rectangle bounds)
	{
		click(manager, center(bounds));
	}

	private static void click(QuestJournalManager manager, Point point)
	{
		manager.mousePressed(mouseEvent(
			MouseEvent.MOUSE_PRESSED,
			MouseEvent.BUTTON1,
			InputEvent.BUTTON1_DOWN_MASK,
			point.x,
			point.y));
		manager.mouseReleased(mouseEvent(
			MouseEvent.MOUSE_RELEASED,
			MouseEvent.BUTTON1,
			0,
			point.x,
			point.y));
	}

	private static MouseEvent mouseEvent(
		int id,
		int button,
		int modifiers,
		int x,
		int y)
	{
		return new MouseEvent(
			new Canvas(),
			id,
			System.currentTimeMillis(),
			modifiers,
			x,
			y,
			1,
			false,
			button);
	}

	private static KeyEvent pressed(int keyCode, char character)
	{
		return new KeyEvent(
			new Canvas(),
			KeyEvent.KEY_PRESSED,
			System.currentTimeMillis(),
			0,
			keyCode,
			character);
	}

	private static Widget widget(int id, Rectangle bounds, boolean hidden)
	{
		Widget widget = mock(Widget.class);
		when(widget.getId()).thenReturn(id);
		when(widget.getBounds()).thenReturn(new Rectangle(bounds));
		when(widget.isHidden()).thenReturn(hidden);
		return widget;
	}

	private static JournalSnapshot snapshotWithSelection(String id, String title)
	{
		JournalSnapshot.SelectedQuest selected = new JournalSnapshot.SelectedQuest(
			new JournalSnapshot.QuestOverview(
				id,
				title,
				JournalSnapshot.QuestType.QUEST,
				JournalSnapshot.QuestState.NOT_STARTED,
				JournalSnapshot.QuestDifficulty.NOVICE,
				false),
			Collections.emptyList(),
			Collections.emptyList(),
			Collections.emptyList(),
			Collections.emptyList(),
			Collections.emptyList(),
			Collections.emptyList());
		return new JournalSnapshot(
			Collections.emptyList(),
			selected,
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 0, 0, 0));
	}

	private static final class Fixture
	{
		private final JournalSnapshot.QuestListOptions options;
		private final Client client = mock(Client.class);
		private final ClientThread clientThread = mock(ClientThread.class);
		private final ConfigManager configManager = mock(ConfigManager.class);
		private final StarredQuestStore starredQuestStore =
			mock(StarredQuestStore.class);
		private final ViewStateStore viewStateStore =
			mock(ViewStateStore.class);
		private final EventBus eventBus = mock(EventBus.class);
		private final QuestHelperConfig config = mock(QuestHelperConfig.class);
		private final OverlayManager overlayManager = mock(OverlayManager.class);
		private final MouseManager mouseManager = mock(MouseManager.class);
		private final KeyManager keyManager = mock(KeyManager.class);
		private final ChatboxPanelManager chatboxPanelManager = mock(ChatboxPanelManager.class);
		private final ChatboxTextInput searchInput = mock(ChatboxTextInput.class);
		private final Provider<LauncherOverlay> launcherProvider = mock(Provider.class);
		private final Provider<JournalOverlay> journalProvider = mock(Provider.class);
		private final Provider<JournalDataSource> dataProvider = mock(Provider.class);
		private final Provider<QuestHelperPlugin> ownerProvider = mock(Provider.class);
		private final LauncherOverlay launcherOverlay = mock(LauncherOverlay.class);
		private final JournalOverlay journalOverlay = mock(JournalOverlay.class);
		private final JournalDataSource dataSource = mock(JournalDataSource.class);
		private final Keybind hotkey = mock(Keybind.class);
		private final AtomicReference<ChatboxTextInput> currentChatboxInput = new AtomicReference<>();
		private final AtomicReference<Consumer<String>> searchChanged = new AtomicReference<>();
		private final AtomicReference<Runnable> searchClosed = new AtomicReference<>();
		private final FilterController filterController;
		private final PanelController panelController;
		private final QuestJournalManager manager;

		private Fixture(JournalSnapshot.QuestListOptions options)
		{
			this.options = options;
			JournalSnapshot snapshot = new JournalSnapshot(
				Collections.emptyList(),
				null,
				null,
				options,
				new JournalSnapshot.QuestProgress(0, 0, 0, 0));

			when(client.isClientThread()).thenReturn(true);
			when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
			when(client.getTickCount()).thenReturn(42);
			when(config.enableQuestJournal()).thenReturn(true);
			when(config.journalShowButton()).thenReturn(true);
			when(config.journalOpenHotkey()).thenReturn(hotkey);
			when(config.journalKeepWithinGameArea()).thenReturn(true);
			when(config.journalExpandChecklistsOnDemand()).thenReturn(true);
			when(launcherProvider.get()).thenReturn(launcherOverlay);
			when(journalProvider.get()).thenReturn(journalOverlay);
			when(dataProvider.get()).thenReturn(dataSource);
			when(dataSource.getSnapshot()).thenReturn(snapshot);
			when(dataSource.getSnapshot(any(JournalSnapshot.QuestFilter.class))).thenReturn(snapshot);
			when(starredQuestStore.load()).thenReturn(Collections.emptySet());
			when(viewStateStore.load()).thenReturn(ViewState.empty());
			when(hotkey.matches(any(KeyEvent.class))).thenAnswer(invocation ->
				invocation.<KeyEvent>getArgument(0).getKeyCode() == KeyEvent.VK_J);
			when(chatboxPanelManager.openTextInput("Search quest journal")).thenReturn(searchInput);
			when(chatboxPanelManager.getCurrentInput()).thenAnswer(invocation -> currentChatboxInput.get());
			when(searchInput.value(any(String.class))).thenReturn(searchInput);
			when(searchInput.onChanged(any(Consumer.class))).thenAnswer(invocation ->
			{
				searchChanged.set(invocation.getArgument(0));
				return searchInput;
			});
			when(searchInput.onDone(any(Predicate.class))).thenReturn(searchInput);
			when(searchInput.isBuilt()).thenReturn(true);
			when(searchInput.onClose(any(Runnable.class))).thenAnswer(invocation ->
			{
				searchClosed.set(invocation.getArgument(0));
				return searchInput;
			});
			when(searchInput.build()).thenAnswer(invocation ->
			{
				currentChatboxInput.set(searchInput);
				return searchInput;
			});

			filterController = new FilterController(configManager, starredQuestStore);
			panelController = new PanelController(client, configManager, config, overlayManager);
			manager = new QuestJournalManager(
				client,
				clientThread,
				configManager,
				panelController,
				new SearchController(client, clientThread, chatboxPanelManager),
				filterController,
				new QuestSelectionController(client, config),
				new ViewStateController(client, viewStateStore, filterController),
				eventBus,
				config,
				overlayManager,
				mouseManager,
				keyManager,
				launcherProvider,
				journalProvider,
				dataProvider,
				ownerProvider);
		}

		private void startAndOpen()
		{
			manager.startUp();
			manager.keyPressed(pressed(KeyEvent.VK_J, KeyEvent.CHAR_UNDEFINED));
			assertTrue(manager.isJournalOpen());
		}

		private void renderInteractiveSurfaces()
		{
			when(launcherOverlay.isRendered()).thenReturn(true);
			when(launcherOverlay.contains(any(Point.class))).thenAnswer(invocation ->
				LAUNCHER_BOUNDS.contains(invocation.<Point>getArgument(0)));
			when(journalOverlay.isRendered()).thenReturn(true);
			when(journalOverlay.isRenderedForMaximizedState(anyBoolean())).thenReturn(true);
			when(journalOverlay.contains(any(Point.class))).thenAnswer(invocation ->
				JOURNAL_BOUNDS.contains(invocation.<Point>getArgument(0)));
			when(journalOverlay.getPanelBounds()).thenReturn(JOURNAL_BOUNDS);
			when(journalOverlay.getViewportBounds()).thenReturn(VIEWPORT);
			when(journalOverlay.getBounds()).thenReturn(new Rectangle(JOURNAL_BOUNDS));
			when(journalOverlay.isTitleIcon(any(Point.class))).thenAnswer(invocation ->
			{
				Point point = invocation.getArgument(0);
				return TITLE_ICON_BOUNDS.contains(point)
					|| MAXIMIZED_TITLE_ICON_BOUNDS.contains(point);
			});
			when(journalOverlay.isSearchControl(any(Point.class))).thenAnswer(invocation ->
				SEARCH_BOUNDS.contains(invocation.<Point>getArgument(0)));
			when(journalOverlay.isFilterVisibilityControl(any(Point.class))).thenAnswer(invocation ->
				FILTER_BUTTON_BOUNDS.contains(invocation.<Point>getArgument(0)));
			when(journalOverlay.starControlQuestIdAt(any(Point.class))).thenAnswer(invocation ->
				STAR_BUTTON_BOUNDS.contains(invocation.<Point>getArgument(0))
					? "COOKS_ASSISTANT"
					: null);
		}
	}
}
