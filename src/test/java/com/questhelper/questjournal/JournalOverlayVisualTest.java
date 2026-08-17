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

package com.questhelper.questjournal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.gameval.SpriteID;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.components.TooltipComponent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JournalOverlayVisualTest
{
	private static final Rectangle STANDARD_VIEWPORT = new Rectangle(0, 0, 1_000, 700);
	private static final Rectangle STANDARD_PANEL = new Rectangle(100, 80, 800, 500);

	@Test
	public void constructionUsesTheFullNativeSkillIcon()
	{
		SkillIconManager skillIconManager = mock(SkillIconManager.class);
		JournalOverlay overlay =
			new JournalOverlay(null, null, null, null, null, skillIconManager);
		BufferedImage canvas =
			new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = canvas.createGraphics();
		try
		{
			overlay.drawIdentityIcon(
				graphics,
				JournalSnapshot.IconIdentity.skill(Skill.CONSTRUCTION.name()),
				new Rectangle(0, 0, 18, 18));
			overlay.drawIdentityIcon(
				graphics,
				JournalSnapshot.IconIdentity.skill(Skill.COOKING.name()),
				new Rectangle(0, 0, 18, 18));
		}
		finally
		{
			graphics.dispose();
		}

		verify(skillIconManager).getSkillImage(Skill.CONSTRUCTION, false);
		verify(skillIconManager).getSkillImage(Skill.COOKING, true);
	}

	@Test
	public void titleIconEasterEggStaysHiddenUntilTheTenthClick()
	{
		JournalOverlay overlay = new JournalOverlay(null, null, null, null, null, null);
		assertEquals("Quest Journal", overlay.headerTitleText());
		for (int click = 0; click < 9; click++)
		{
			overlay.recordTitleIconClick();
			assertEquals("Quest Journal", overlay.headerTitleText());
		}
		overlay.recordTitleIconClick();
		assertEquals("Quest Helper by Ruined Heir", overlay.headerTitleText());
		overlay.recordTitleIconClick();
		assertEquals("Quest Helper by Ruined Heir", overlay.headerTitleText());
		overlay.clearPersistentViewState();
		assertEquals("Quest Journal", overlay.headerTitleText());
		for (int click = 0; click < 10; click++)
		{
			overlay.recordTitleIconClick();
		}
		overlay.release();
		assertEquals("Quest Journal", overlay.headerTitleText());
	}

	@Test
	public void journalTitleAndCurrentObjectiveUseTheExactJournalOrange()
	{
		Color expected = new Color(0xFF, 0x99, 0x33);
		JournalSnapshot.Objective current = objective(
			"Current step",
			JournalSnapshot.ObjectiveState.AVAILABLE,
			true);

		assertEquals(expected, JournalOverlay.journalTitleColor());
		assertEquals(expected, JournalOverlay.objectiveTextColor(current, false));
	}

	@Test
	public void incompleteObjectiveMarkersAlignWithTheFirstTextLine()
	{
		Rectangle firstLine = JournalOverlay.objectiveGlyphBounds(
			16,
			20,
			36,
			20,
			16,
			false);

		assertEquals(new Rectangle(16, 21, 14, 14), firstLine);
		assertEquals(
			new Rectangle(21, 26, 4, 4),
			JournalOverlay.pendingObjectiveDotBounds(firstLine));
		assertEquals(
			new Rectangle(16, 31, 14, 14),
			JournalOverlay.objectiveGlyphBounds(16, 20, 36, 20, 16, true));
	}

	@Test
	public void clearActiveQuestTooltipSaysClearActiveQuest()
	{
		assertEquals("Clear active quest", ControlsRenderer.clearActiveQuestTooltipText());
	}

	@Test
	public void overviewTitleAndMetadataUseOneMetricAwareCenteredBlock()
	{
		Rectangle header = new Rectangle(100, 40, 420, 36);
		Rectangle[] rows = ControlsRenderer.selectedQuestHeaderRowBounds(
			header,
			13,
			11);
		Rectangle title = rows[0];
		Rectangle metadata = rows[1];

		assertEquals(new Rectangle(100, 45, 420, 13), title);
		assertEquals(new Rectangle(100, 60, 420, 11), metadata);
		assertEquals(2, metadata.y - title.y - title.height);
		assertEquals(title.y - header.y, header.y + header.height - metadata.y - metadata.height);
	}

	@Test
	public void overviewDifficultyAndMembershipTextUseTheSectionHeaderAccent()
	{
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		JournalSnapshot snapshot = new JournalSnapshot(
			Collections.emptyList(),
			selectedQuest(
				"COOKS_ASSISTANT",
				"Cook's Assistant",
				JournalSnapshot.QuestState.NOT_STARTED),
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 1, 0, 0));
		RenderedSnapshot rendered = renderSnapshotWithCanvas(
			snapshot, viewport, panel, new RenderOptions());
		JournalGeometry geometry = JournalGeometry.create(panel, viewport);
		Rectangle setActive = rendered.controlsRenderer.setActiveQuestControlBounds(geometry);
		Rectangle mainPane = geometry.mainPaneBounds();
		Rectangle header = new Rectangle(
			mainPane.x,
			mainPane.y,
			mainPane.width,
			setActive.y + setActive.height / 2 - mainPane.y);
		BufferedImage metricsImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D metricsGraphics = metricsImage.createGraphics();
		try
		{
			FontMetrics titleMetrics = metricsGraphics.getFontMetrics(
				JournalOverlay.overviewQuestTitleFont());
			FontMetrics metadataMetrics = metricsGraphics.getFontMetrics(
				JournalOverlay.overviewMetadataFont());
			Rectangle metadataRow = ControlsRenderer.selectedQuestHeaderRowBounds(
				header,
				titleMetrics.getHeight(),
				metadataMetrics.getHeight())[1];
			Rectangle difficultyText = new Rectangle(
				header.x + 10 + 8 + 4,
				metadataRow.y,
				metadataMetrics.stringWidth("Novice"),
				metadataRow.height);
			Rectangle membershipText = new Rectangle(
				header.x + header.width - 10 - metadataMetrics.stringWidth("Free to play"),
				metadataRow.y,
				metadataMetrics.stringWidth("Free to play"),
				metadataRow.height);
			int accent = new Color(0xE9, 0xBF, 0x6F).getRGB();

			assertTrue(countPixelsEqualTo(rendered.canvas, difficultyText, accent) > 0);
			assertTrue(countPixelsEqualTo(rendered.canvas, membershipText, accent) > 0);
		}
		finally
		{
			metricsGraphics.dispose();
		}
	}

	@Test
	public void requirementGrayIsConsistentAcrossRenderContexts()
	{
		JournalSnapshot.Requirement informational = new JournalSnapshot.Requirement(
			"Optional item",
			JournalSnapshot.RequirementState.UNKNOWN,
			Color.GRAY.getRGB(),
			Collections.emptyList(),
			"",
			"",
			JournalSnapshot.IconIdentity.none(),
			"",
			"");

		assertEquals(
			new Color(0x99, 0x99, 0x99),
			JournalOverlay.requirementColor(informational));
		assertEquals(
			JournalOverlay.requirementColor(informational),
			JournalOverlay.requirementUnderlineColor(informational));
	}

	@Test
	public void renderedTitleIconPublishesOnlyItsVisibleHitRectangle()
	{
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		JournalSnapshot snapshot = new JournalSnapshot(
			Collections.emptyList(),
			null,
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 0, 0, 0));
		JournalOverlay overlay = renderSnapshot(snapshot, viewport, panel);
		int hitPixels = 0;
		Point hit = null;
		for (int y = panel.y; y < panel.y + 30; y++)
		{
			for (int x = panel.x; x < panel.x + panel.width; x++)
			{
				Point point = new Point(x, y);
				if (overlay.isTitleIcon(point))
				{
					hitPixels++;
					hit = point;
				}
			}
		}

		assertEquals(13 * 13, hitPixels);
		assertNotNull(hit);
		overlay.release();
		assertFalse(overlay.isTitleIcon(hit));
	}

	@Test
	public void releaseAndRestoreRoundTripsJournalScrollAndDisclosureState()
	{
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		JournalOverlay overlay = renderSnapshot(
			new JournalSnapshot(
				Collections.emptyList(),
				null,
				null,
				JournalSnapshot.QuestListOptions.defaults(),
				new JournalSnapshot.QuestProgress(0, 0, 0, 0)),
			viewport,
			panel);
		JournalSnapshot.QuestFilter filter = JournalSnapshot.QuestFilter.all()
			.withSearchText("")
			.withStarredSelected(true);
		ViewState expected = ViewState.fromFilter(
			"COOKS_ASSISTANT",
			44,
			55,
			66,
			filter,
			true,
			Collections.singleton("section:start"));

		overlay.release();
		overlay.restoreViewState(expected);

		assertEquals(expected, overlay.captureViewState("COOKS_ASSISTANT", filter));
	}

	@Test
	public void rejectedRenderDoesNotPublishSelectionOrScrollState()
	{
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		Client client = mock(Client.class);
		QuestJournalManager manager = mock(QuestJournalManager.class);
		QuestJournalManager.JournalPanelRenderState panelState =
			mock(QuestJournalManager.JournalPanelRenderState.class);
		JournalSnapshot.QuestFilter filter = JournalSnapshot.QuestFilter.all();
		AtomicBoolean acceptRender = new AtomicBoolean();
		ViewState expected = ViewState.fromFilter(
			"PREVIOUS_QUEST",
			44,
			55,
			66,
			filter,
			false,
			Collections.emptySet());
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		when(client.getRealDimensions()).thenReturn(viewport.getSize());
		when(manager.isJournalOpen()).thenReturn(true);
		when(manager.getJournalContentBounds(any(Rectangle.class))).thenReturn(viewport);
		when(manager.getJournalRenderState(any(Rectangle.class))).thenReturn(panelState);
		when(panelState.bounds()).thenReturn(panel);
		when(panelState.maximized()).thenReturn(false);
		when(panelState.revision()).thenReturn(11L);
		when(manager.isJournalRenderStateCurrent(11L)).thenReturn(true);
		when(manager.getJournalSnapshot()).thenReturn(
			new JournalSnapshot(
				Collections.emptyList(),
				selectedQuest(
					"COOKS_ASSISTANT",
					"Cook's Assistant",
					JournalSnapshot.QuestState.NOT_STARTED),
				null,
				JournalSnapshot.QuestListOptions.defaults(),
				new JournalSnapshot.QuestProgress(0, 1, 0, 0)));
		when(manager.getPointerCanvasPoint()).thenReturn(new Point(-1, -1));
		when(manager.getQuestFilter()).thenReturn(filter);
		when(manager.commitJournalRender(anyLong(), any(Runnable.class))).thenAnswer(invocation ->
		{
			if (!acceptRender.get())
			{
				return false;
			}
			invocation.getArgument(1, Runnable.class).run();
			return true;
		});
		JournalOverlay overlay = new JournalOverlay(
			client,
			null,
			manager,
			mock(SpriteManager.class),
			null,
			null);
		overlay.restoreViewState(expected);
		BufferedImage canvas = new BufferedImage(
			viewport.width,
			viewport.height,
			BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = canvas.createGraphics();
		try
		{
			overlay.render(graphics);
		}
		finally
		{
			graphics.dispose();
		}

		assertEquals(
			expected,
			overlay.captureViewState("PREVIOUS_QUEST", filter));

		acceptRender.set(true);
		graphics = canvas.createGraphics();
		try
		{
			overlay.render(graphics);
		}
		finally
		{
			graphics.dispose();
		}
		assertEquals(
			ViewState.fromFilter(
				"COOKS_ASSISTANT",
				0,
				0,
				0,
				filter,
				false,
				Collections.emptySet()),
			overlay.captureViewState("COOKS_ASSISTANT", filter));
	}

	@Test
	public void acceptedRenderDoesNotOverwriteScrollInputDuringCommit()
	{
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		List<JournalSnapshot.QuestListItem> quests = new ArrayList<>();
		for (int index = 0; index < 80; index++)
		{
			quests.add(new JournalSnapshot.QuestListItem(
				"QUEST_" + index,
				"Quest " + index,
				JournalSnapshot.QuestType.QUEST,
				JournalSnapshot.QuestState.NOT_STARTED,
				JournalSnapshot.QuestDifficulty.NOVICE,
				false,
				Collections.emptyMap()));
		}
		JournalSnapshot snapshot = new JournalSnapshot(
			quests,
			null,
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, quests.size(), 0, 0));
		AtomicReference<JournalSnapshot> snapshotReference =
			new AtomicReference<>(snapshot);
		JournalSnapshot.QuestFilter filter = JournalSnapshot.QuestFilter.all();
		Client client = mock(Client.class);
		QuestJournalManager manager = mock(QuestJournalManager.class);
		QuestJournalManager.JournalPanelRenderState panelState =
			mock(QuestJournalManager.JournalPanelRenderState.class);
		AtomicBoolean injectScroll = new AtomicBoolean();
		AtomicReference<JournalOverlay> overlayReference = new AtomicReference<>();
		JournalGeometry geometry = JournalGeometry.create(panel, viewport);
		Rectangle listScrollBounds =
			QuestListRenderer.questListScrollBounds(
				geometry.questListContentBounds());
		Point listPoint = new Point(
			listScrollBounds.x + 4,
			listScrollBounds.y + 4);

		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		when(client.getRealDimensions()).thenReturn(viewport.getSize());
		when(manager.isJournalOpen()).thenReturn(true);
		when(manager.getJournalContentBounds(any(Rectangle.class))).thenReturn(viewport);
		when(manager.getJournalRenderState(any(Rectangle.class))).thenReturn(panelState);
		when(panelState.bounds()).thenReturn(panel);
		when(panelState.maximized()).thenReturn(false);
		when(panelState.revision()).thenReturn(12L);
		when(manager.isJournalRenderStateCurrent(12L)).thenReturn(true);
		when(manager.getJournalSnapshot()).thenAnswer(
			invocation -> snapshotReference.get());
		when(manager.getPointerCanvasPoint()).thenReturn(new Point(-1, -1));
		when(manager.getQuestFilter()).thenReturn(filter);
		when(manager.commitJournalRender(anyLong(), any(Runnable.class))).thenAnswer(invocation ->
		{
			if (injectScroll.getAndSet(false))
			{
				assertTrue(overlayReference.get().scrollAt(listPoint, 1));
			}
			invocation.getArgument(1, Runnable.class).run();
			return true;
		});

		JournalOverlay overlay = new JournalOverlay(
			client,
			null,
			manager,
			mock(SpriteManager.class),
			null,
			null);
		overlayReference.set(overlay);
		renderAgain(overlay, viewport.getSize());
		assertEquals(0, overlay.captureViewState(null, filter).getListScrollOffset());
		Point originalFirstRow = findHitPoint(panel, overlay::questIdAt, "QUEST_0");
		assertNotNull(originalFirstRow);

		List<JournalSnapshot.QuestListItem> replacementQuests = new ArrayList<>(quests);
		replacementQuests.set(0, new JournalSnapshot.QuestListItem(
			"REPLACEMENT_QUEST",
			"Replacement Quest",
			JournalSnapshot.QuestType.QUEST,
			JournalSnapshot.QuestState.NOT_STARTED,
			JournalSnapshot.QuestDifficulty.NOVICE,
			false,
			Collections.emptyMap()));
		snapshotReference.set(new JournalSnapshot(
			replacementQuests,
			null,
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, replacementQuests.size(), 0, 0)));

		injectScroll.set(true);
		renderAgain(overlay, viewport.getSize());

		assertTrue(overlay.captureViewState(null, filter).getListScrollOffset() > 0);
		assertEquals("QUEST_0", overlay.questIdAt(originalFirstRow));
	}

	@Test
	public void rewardEntriesUseWhiteText()
	{
		assertEquals(Color.WHITE, DetailRenderer.rewardTextColor());
	}

	@Test
	public void questTitleRasterKeepsOpaqueJournalStateColors()
	{
		List<Color> expectedColors = Arrays.asList(
			new Color(0xFF, 0x1F, 0x1F),
			new Color(0xFF, 0xFF, 0x1F),
			new Color(0x1F, 0xFF, 0x1F));
		JournalSnapshot.QuestState[] states = JournalSnapshot.QuestState.values();
		for (int stateIndex = 0; stateIndex < states.length; stateIndex++)
		{
			Color color = JournalOverlay.questStateColor(states[stateIndex]);
			assertEquals(expectedColors.get(stateIndex), color);
			assertEquals(color, ControlsRenderer.activeQuestTitleColor(states[stateIndex]));
			for (Font font : Arrays.asList(
				JournalOverlay.questListFont(),
				JournalOverlay.overviewQuestTitleFont()))
			{
				BufferedImage image = new BufferedImage(120, 30, BufferedImage.TYPE_INT_ARGB);
				Graphics2D graphics = image.createGraphics();
				try
				{
					graphics.setFont(font);
					graphics.setRenderingHint(
						RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					JournalOverlay.drawQuestTitleString(graphics, "Quest", 3, 20, color);
					assertEquals(
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON,
						graphics.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));
				}
				finally
				{
					graphics.dispose();
				}

				int coloredPixels = 0;
				for (int y = 0; y < image.getHeight(); y++)
				{
					for (int x = 0; x < image.getWidth(); x++)
					{
						int pixel = image.getRGB(x, y);
						if ((pixel >>> 24) == 0 || pixel == Color.BLACK.getRGB())
						{
							continue;
						}
						assertEquals(color.getRGB(), pixel);
						coloredPixels++;
					}
				}
				assertTrue(coloredPixels > 0);
			}
		}
	}

	@Test
	public void emptyStatesHaveConciseInstructions()
	{
		assertEquals(
			"Select a quest to view its steps",
			JournalOverlay.emptyStateMessage());
		assertEquals(
			"No quests match your filters",
			JournalOverlay.emptyQuestListMessage());
	}

	@Test
	public void requirementHoverShowsQuestHelperGuidanceBeforeTheWikiAction()
	{
		JournalSnapshot.Requirement missing = requirementWithHelp(
			JournalSnapshot.RequirementState.UNMET,
			Collections.emptyList(),
			"Take one from the stump.\nIt is south of the house.");
		assertEquals(
			Arrays.asList(
				"Take one from the stump.<br>It is south of the house.",
				"<col=ffffff>Click to open Wiki</col>"),
			JournalOverlay.requirementTooltipBlocks(missing, true));
		List<Rectangle> tooltipBlocks = TooltipRenderer.cursorTooltipBlockBounds(
			new Point(100, 100),
			Arrays.asList(new Dimension(120, 30), new Dimension(90, 20)),
			new Rectangle(0, 0, 400, 300));
		assertEquals(2, tooltipBlocks.size());
		assertEquals(
			tooltipBlocks.get(0).y + tooltipBlocks.get(0).height + 2,
			tooltipBlocks.get(1).y);

		JournalSnapshot.Requirement partial = requirementWithHelp(
			JournalSnapshot.RequirementState.PARTIAL,
			Collections.singletonList(JournalSnapshot.ItemLocation.BANK),
			"Two are available during the quest.");
		assertEquals(
			Collections.singletonList(
				"Two are available during the quest.<br>Located in: Bank"),
			JournalOverlay.requirementTooltipBlocks(partial, false));

		JournalSnapshot.Requirement banked = requirementWithHelp(
			JournalSnapshot.RequirementState.BANKED,
			Collections.singletonList(JournalSnapshot.ItemLocation.BANK),
			"Items can be found in your: Bank");
		assertEquals(
			Collections.singletonList("Items can be found in your: Bank"),
			JournalOverlay.requirementTooltipBlocks(banked, false));
	}

	@Test
	public void longRequirementTooltipsWrapWithoutMergingTheirActionBlock()
	{
		BufferedImage metricsImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = metricsImage.createGraphics();
		try
		{
			graphics.setFont(FontManager.getRunescapeFont());
			FontMetrics metrics = graphics.getFontMetrics();
			Rectangle viewport = new Rectangle(0, 0, 200, 240);
			int maximumContentWidth = TooltipRenderer.tooltipContentWidth(viewport);
			assertEquals(184, maximumContentWidth);
			assertEquals(
				280,
				TooltipRenderer.tooltipContentWidth(new Rectangle(0, 0, 1_000, 600)));

			String guidance = "Search the shelves in the north-west room of the distant manor."
				+ "<br/>Then speak to <col=ffffff>the travelling merchant</col>.";
			String wrappedGuidance = TooltipRenderer.wrapTooltipText(
				metrics,
				guidance,
				90);
			assertTrue(wrappedGuidance.contains("<br>"));
			assertFalse(wrappedGuidance.contains("<br/>"));
			assertTrue(wrappedGuidance.contains("<col=ffffff>"));
			assertTrue(wrappedGuidance.contains("</col>"));
			for (String line : wrappedGuidance.split("<br>", -1))
			{
				assertTrue(metrics.stringWidth(line.replaceAll("<[^>]*>", "")) <= 90);
			}

			String wrappedLongName = TooltipRenderer.wrapTooltipText(
				metrics,
				"AnExceptionallyLongUnbrokenRequirementNameThatCannotFit",
				90);
			assertTrue(wrappedLongName.contains("<br>"));
			for (String line : wrappedLongName.split("<br>", -1))
			{
				assertTrue(metrics.stringWidth(line) <= 90);
			}

			String wikiAction = "<col=ffffff>Click to open Wiki</col>";
			Dimension guidanceSize = TooltipRenderer.tooltipSize(metrics, wrappedGuidance);
			Dimension actionSize = TooltipRenderer.tooltipSize(metrics, wikiAction);
			assertTrue(guidanceSize.width <= 98);
			TooltipComponent renderedTooltip = new TooltipComponent();
			renderedTooltip.setText(wrappedGuidance);
			assertEquals(guidanceSize, renderedTooltip.render(graphics));
			List<Rectangle> blocks = TooltipRenderer.cursorTooltipBlockBounds(
				new Point(195, 235),
				Arrays.asList(guidanceSize, actionSize),
				viewport);
			assertEquals(2, blocks.size());
			assertEquals(blocks.get(0).y + blocks.get(0).height + 2, blocks.get(1).y);
			for (Rectangle block : blocks)
			{
				assertTrue(block.x >= 4);
				assertTrue(block.y >= 4);
				assertTrue(block.x + block.width <= viewport.width - 4);
				assertTrue(block.y + block.height <= viewport.height - 4);
			}
		}
		finally
		{
			graphics.dispose();
		}
	}

	@Test
	public void facetFiltersUseChecklistMarkersWhileOrderUsesOneSelection()
	{
		assertTrue(JournalOverlay.FilterControl.TYPE.isChecklist());
		assertTrue(JournalOverlay.FilterControl.DIFFICULTY.isChecklist());
		assertTrue(JournalOverlay.FilterControl.MEMBERSHIP.isChecklist());
		assertTrue(JournalOverlay.FilterControl.STATUS.isChecklist());
		assertFalse(JournalOverlay.FilterControl.ORDER.isChecklist());
		assertFalse(JournalOverlay.FilterControl.STATUS.isSingleSelect());
		assertTrue(JournalOverlay.FilterControl.ORDER.isSingleSelect());
		assertEquals(
			"Free to play",
			FilterRenderer.membershipFilterLabel(
				JournalSnapshot.QuestMembership.FREE_TO_PLAY));
	}

	@Test
	public void checklistSummariesDistinguishAllNoneSingleAndMultipleSelections()
	{
		List<JournalSnapshot.QuestType> available = Arrays.asList(
			JournalSnapshot.QuestType.QUEST,
			JournalSnapshot.QuestType.MINIQUEST,
			JournalSnapshot.QuestType.ACHIEVEMENT_DIARY);

		assertEquals("All", FilterRenderer.checklistSummary(
			new LinkedHashSet<>(available),
			available,
			FilterRenderer::typeLabel,
			0,
			String::length));
		assertEquals("None", FilterRenderer.checklistSummary(
			Collections.emptySet(),
			available,
			FilterRenderer::typeLabel,
			0,
			String::length));
		assertEquals("Miniquests", FilterRenderer.checklistSummary(
			Collections.singleton(JournalSnapshot.QuestType.MINIQUEST),
			available,
			FilterRenderer::typeLabel,
			0,
			String::length));
		assertEquals("2 selected", FilterRenderer.checklistSummary(
			new LinkedHashSet<>(available.subList(0, 2)),
			available,
			FilterRenderer::typeLabel,
			0,
			String::length));

		Set<JournalSnapshot.QuestType> twoSelections =
			new LinkedHashSet<>(available.subList(0, 2));
		assertEquals("Quests & Miniquests", FilterRenderer.checklistSummary(
			twoSelections,
			available,
			FilterRenderer::typeLabel,
			19,
			String::length));
		assertEquals("2 selected", FilterRenderer.checklistSummary(
			twoSelections,
			available,
			FilterRenderer::typeLabel,
			18,
			String::length));
	}

	@Test
	public void questListRowsPlaceTheStarAtTheEndUntilTheCombatIconAppears()
	{
		Rectangle row = new Rectangle(10, 20, 200, 22);
		assertEquals(14, QuestListRenderer.questListTitleTextX(row, false));
		assertEquals(29, QuestListRenderer.questListTitleTextX(row, true));
		assertEquals(
			new Rectangle(14, 25, 12, 12),
			QuestListRenderer.questListTypeMarkerBounds(row));
		assertEquals(
			new Rectangle(190, 23, 16, 16),
			QuestListRenderer.questListStarMarkerBounds(row, false));
		assertEquals(
			new Rectangle(171, 23, 16, 16),
			QuestListRenderer.questListStarMarkerBounds(row, true));
		assertEquals(
			new Rectangle(190, 23, 16, 16),
			QuestListRenderer.questListActiveMarkerBounds(row));
		assertEquals(206, QuestListRenderer.questListTitleRight(row, false, false));
		assertEquals(187, QuestListRenderer.questListTitleRight(row, true, false));
		assertEquals(187, QuestListRenderer.questListTitleRight(row, false, true));
		assertEquals(168, QuestListRenderer.questListTitleRight(row, true, true));
	}

	@Test
	public void listRowsHaveNoNormalBackground()
	{
		assertEquals(0x00000000, QuestListRenderer.questRowBackground(false, false).getRGB());
		assertNotEquals(0x00000000, QuestListRenderer.questRowBackground(true, false).getRGB());
		assertEquals(
			QuestListRenderer.questRowBackground(false, false),
			QuestListRenderer.questRowBackground(false, true));
		assertEquals(
			Color.WHITE,
			QuestListRenderer.questRowTextColor(
				JournalSnapshot.QuestState.NOT_STARTED,
				true));
		assertEquals(
			new Color(0xFF, 0x1F, 0x1F),
			QuestListRenderer.questRowTextColor(
				JournalSnapshot.QuestState.NOT_STARTED,
				false));
		assertEquals(new Color(0x0A, 0x09, 0x06), JournalOverlay.checklistToggleIconColor());
	}

	@Test
	public void emptyQuestAndDetailPaneInteriorsKeepTheUntintedPanelSurface()
	{
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		JournalSnapshot snapshot = new JournalSnapshot(
			Collections.emptyList(),
			null,
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 0, 0, 0));
		RenderedSnapshot rendered = renderSnapshotWithCanvas(
			snapshot, viewport, panel, new RenderOptions());
		JournalGeometry geometry = JournalGeometry.create(panel, viewport);

		Rectangle quest = geometry.questListContentBounds();
		Rectangle main = geometry.mainContentBounds();
		Rectangle detail = geometry.detailContentBounds();
		int questInterior = rendered.canvas.getRGB(quest.x + 2, quest.y + 2);
		int mainInterior = rendered.canvas.getRGB(main.x + 2, main.y + 2);
		int detailInterior = rendered.canvas.getRGB(detail.x + 2, detail.y + 2);

		assertNotEquals(0x00000000, mainInterior);
		assertEquals(mainInterior, questInterior);
		assertEquals(mainInterior, detailInterior);
	}

	@Test
	public void sectionGapAppearsOnlyBeforeASectionThatFollowsAnotherSection()
	{
		assertEquals(27, JournalOverlay.sectionStartCursor(27, false));
		assertEquals(35, JournalOverlay.sectionStartCursor(27, true));
	}

	@Test
	public void sectionChecklistToggleRevealsTheUnionBeforeItsSteps()
	{
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		String sectionId = "COOKS_ASSISTANT:section:0";
		String guidance = "Buy one from the general store.";
		JournalSnapshot.Requirement rope = requirementWithHelp(
			JournalSnapshot.RequirementState.UNMET,
			Collections.emptyList(),
			guidance);
		JournalSnapshot.SelectedQuest viewed = new JournalSnapshot.SelectedQuest(
			selectedQuest(
				"COOKS_ASSISTANT",
				"Cook's Assistant",
				JournalSnapshot.QuestState.NOT_STARTED).getOverview(),
			Collections.singletonList(new JournalSnapshot.Objective(
				sectionId,
				"Kitchen",
				"Gather the ingredients.",
				JournalSnapshot.ObjectiveState.AVAILABLE,
				false,
				Collections.singletonList(rope))),
			Collections.emptyList(),
			Collections.emptyList(),
			Collections.emptyList(),
			Collections.emptyList(),
			Collections.emptyList());
		JournalSnapshot snapshot = new JournalSnapshot(
			Collections.emptyList(),
			viewed,
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 1, 0, 0));
		AtomicReference<Point> pointer = new AtomicReference<>(new Point(-1, -1));
		RenderedSnapshot rendered = renderSnapshotWithCanvas(
			snapshot,
			viewport,
			panel,
			new RenderOptions().pointer(pointer));
		JournalOverlay overlay = rendered.overlay;
		String checklistId = JournalOverlay.sectionChecklistId(sectionId);
		Point toggle = findHitPoint(
			panel,
			overlay::checklistToggleIdAt,
			checklistId);
		Rectangle toggleBounds = findHitBounds(
			panel,
			overlay::checklistToggleIdAt,
			checklistId);

		assertNotNull(toggle);
		assertEquals(66, toggleBounds.width);
		assertEquals(18, toggleBounds.height);
		pointer.set(toggle);
		assertInvertedControlBevel(
			rendered.canvas,
			renderAgainWithCanvas(overlay, viewport.getSize()),
			toggleBounds);
		pointer.set(new Point(-1, -1));
		assertTrue(overlay.toggleChecklist(checklistId));
		renderAgain(overlay, viewport.getSize());
		assertNotNull(findHitPoint(panel, overlay::missingItemWikiUrlAt, rope.getWikiUrl()));
	}

	@Test
	public void alwaysExpandedSettingShowsChecklistsWithoutDisclosureButtons()
	{
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		String stepGuidance = "Carry this item for the current step.";
		String sectionGuidance = "Keep this item for the whole section.";
		JournalSnapshot.Requirement stepItem = requirementWithHelp(
			JournalSnapshot.RequirementState.UNMET,
			Collections.emptyList(),
			stepGuidance);
		JournalSnapshot.Requirement sectionItem = requirementWithHelp(
			JournalSnapshot.RequirementState.UNMET,
			Collections.emptyList(),
			sectionGuidance);
		JournalSnapshot.SelectedQuest viewed = new JournalSnapshot.SelectedQuest(
			selectedQuest(
				"COOKS_ASSISTANT",
				"Cook's Assistant",
				JournalSnapshot.QuestState.NOT_STARTED).getOverview(),
			Collections.singletonList(new JournalSnapshot.Objective(
				"COOKS_ASSISTANT:section:0",
				"Kitchen",
				"Gather the ingredients.",
				JournalSnapshot.ObjectiveState.AVAILABLE,
				false,
				Arrays.asList(stepItem, sectionItem))),
			Collections.emptyList(),
			Collections.emptyList(),
			Collections.emptyList(),
			Collections.emptyList(),
			Collections.emptyList());
		JournalSnapshot snapshot = new JournalSnapshot(
			Collections.emptyList(),
			viewed,
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 1, 0, 0));
		AtomicBoolean alwaysExpanded = new AtomicBoolean(false);
		JournalOverlay overlay = renderSnapshotWithCanvas(
			snapshot,
			viewport,
			panel,
			new RenderOptions().alwaysExpanded(alwaysExpanded)).overlay;

		assertNull(findHitPoint(panel, overlay::missingItemWikiUrlAt, stepItem.getWikiUrl()));
		alwaysExpanded.set(true);
		renderAgain(overlay, viewport.getSize());

		assertNull(findHitPoint(
			panel,
			overlay::checklistToggleIdAt,
			JournalOverlay.sectionChecklistId("COOKS_ASSISTANT:section:0")));
		assertNotNull(findHitPoint(panel, overlay::missingItemWikiUrlAt, stepItem.getWikiUrl()));

		alwaysExpanded.set(false);
		renderAgain(overlay, viewport.getSize());
		assertNull(findHitPoint(panel, overlay::missingItemWikiUrlAt, stepItem.getWikiUrl()));
	}

	@Test
	public void activeStripAndProgressFooterUseFixedSpace()
	{
		Rectangle listContent = new Rectangle(10, 20, 200, 180);
		assertEquals(
			new Rectangle(10, 20, 200, 155),
			QuestListRenderer.questListScrollBounds(listContent));
		assertEquals(
			new Rectangle(10, 176, 200, 24),
			QuestListRenderer.questProgressFooterBounds(listContent));

		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		JournalGeometry geometry = JournalGeometry.create(panel, viewport);
		JournalOverlay overlay = new JournalOverlay(null, null, null, null, null, null);
		ControlsRenderer controlsRenderer =
			testControlsRenderer(overlay);
		JournalSnapshot.QuestOverview overview = selectedQuest(
			"COOKS_ASSISTANT",
			"Cook's Assistant",
			JournalSnapshot.QuestState.IN_PROGRESS).getOverview();
		Rectangle activeControl =
			controlsRenderer.activeQuestControlBounds(geometry);
		Rectangle mainPane = geometry.mainPaneBounds();
		assertEquals(26, activeControl.height);
		Rectangle setActiveControl =
			controlsRenderer.setActiveQuestControlBounds(geometry);
		assertEquals(116, setActiveControl.width);
		assertEquals(26, setActiveControl.height);
		assertEquals((int) mainPane.getCenterX(), (int) setActiveControl.getCenterX());
		assertEquals(mainPane.y + 36, (int) setActiveControl.getCenterY());
		assertEquals(
			setActiveControl.y + setActiveControl.height + 1,
			ControlsRenderer.selectedQuestMainContentBounds(
				geometry.mainContentBounds(),
				true).y);
		assertEquals(
			setActiveControl,
			controlsRenderer.setActiveQuestBounds(geometry, overview, null));
	}

	@Test
	public void fixedFootersAreExcludedFromScrollbarInteraction()
	{
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		List<JournalSnapshot.QuestListItem> quests = new ArrayList<>();
		List<JournalSnapshot.Objective> objectives = new ArrayList<>();
		for (int index = 0; index < 80; index++)
		{
			quests.add(new JournalSnapshot.QuestListItem(
				"QUEST_" + index,
				"Quest " + index,
				JournalSnapshot.QuestType.QUEST,
				JournalSnapshot.QuestState.NOT_STARTED,
				JournalSnapshot.QuestDifficulty.NOVICE,
				false,
				Collections.emptyMap()));
			objectives.add(objective(
				"Complete objective " + index,
				JournalSnapshot.ObjectiveState.LOCKED,
				false));
		}
		JournalSnapshot.SelectedQuest viewed = new JournalSnapshot.SelectedQuest(
			selectedQuest(
				"QUEST_0",
				"Quest 0",
				JournalSnapshot.QuestState.NOT_STARTED).getOverview(),
			objectives,
			Collections.emptyList(),
			Collections.emptyList(),
			Collections.emptyList(),
			Collections.emptyList(),
			Collections.emptyList());
		JournalSnapshot snapshot = new JournalSnapshot(
			quests,
			viewed,
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, quests.size(), 0, 0));
		JournalOverlay overlay = renderSnapshot(snapshot, viewport, panel);
		JournalGeometry geometry = JournalGeometry.create(panel, viewport);
		Rectangle listScroll = QuestListRenderer.questListScrollBounds(
			geometry.questListContentBounds());
		Rectangle mainScroll = geometry.mainContentBounds();
		Point listScrollbar = findScrollbarPoint(
			overlay,
			listScroll,
			JournalOverlay.ScrollRegion.QUEST_LIST);
		Point mainScrollbar = findScrollbarPoint(
			overlay,
			mainScroll,
			JournalOverlay.ScrollRegion.MAIN);

		assertNotNull(listScrollbar);
		assertNotNull(mainScrollbar);
		assertTrue(listScrollbar.y < QuestListRenderer.questProgressFooterBounds(
			geometry.questListContentBounds()).y);
		assertTrue(geometry.mainContentBounds().contains(mainScrollbar));
		Point progressFooterPoint = new Point(
			geometry.questListContentBounds().x + geometry.questListContentBounds().width - 2,
			QuestListRenderer.questProgressFooterBounds(
				geometry.questListContentBounds()).y + 2);
		assertNull(overlay.scrollbarInteractionAt(progressFooterPoint));
		assertFalse(overlay.scrollAt(progressFooterPoint, 1));
	}

	@Test
	public void difficultyMarkersExplainTheirMeaningConcisely()
	{
		assertEquals(
			"Novice difficulty",
			QuestListRenderer.difficultyMarkerTooltip(
				JournalSnapshot.QuestDifficulty.NOVICE));
		assertEquals(
			"Easy achievement diary",
			QuestListRenderer.difficultyMarkerTooltip(
				JournalSnapshot.QuestDifficulty.EASY));
	}

	@Test
	public void questListActionRowUsesStableGeometryPaddingAndHoverBevels()
	{
		Rectangle viewport = new Rectangle(0, 0, 1_200, 800);
		Rectangle panel = new Rectangle(20, 30, 900, 600);
		JournalSnapshot snapshot = new JournalSnapshot(
			Collections.emptyList(),
			selectedQuest(
				"COOKS_ASSISTANT",
				"Cook's Assistant",
				JournalSnapshot.QuestState.NOT_STARTED),
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 1, 0, 0));
		AtomicReference<Point> pointer = new AtomicReference<>(new Point(-1, -1));
		RenderedSnapshot rendered = renderSnapshotWithCanvas(
			snapshot,
			viewport,
			panel,
			new RenderOptions().pointer(pointer));
		JournalOverlay overlay = rendered.overlay;
		JournalGeometry expandedGeometry = JournalGeometry.create(panel, viewport, true);
		Rectangle activeControl =
			rendered.controlsRenderer.activeQuestControlBounds(expandedGeometry);
		Rectangle progress = QuestListRenderer.questProgressFooterBounds(
			expandedGeometry.questListContentBounds());
		Rectangle star =
			rendered.controlsRenderer.starQuestControlBounds(expandedGeometry);
		Rectangle search =
			rendered.controlsRenderer.searchControlBounds(expandedGeometry);
		Rectangle filterButton =
			rendered.controlsRenderer.filterVisibilityControlBounds(
				expandedGeometry);
		Rectangle type =
			rendered.filterRenderer.typeControlBounds(expandedGeometry);
		Rectangle typeCaption = ControlsRenderer.filterCaptionBounds(
			expandedGeometry,
			type);

		assertEquals(26, activeControl.height);
		assertEquals(65, star.width);
		assertEquals(26, star.height);
		assertEquals(64, search.width);
		assertEquals(26, search.height);
		assertEquals(65, filterButton.width);
		assertEquals(26, filterButton.height);
		assertEquals(activeControl.height, star.height);
		assertEquals(star.x + star.width + 3, filterButton.x);
		assertEquals(search.x - 3, filterButton.x + filterButton.width);
		assertEquals(activeControl.y + activeControl.height + 4, search.y);
		int[] groupX = ControlsRenderer.progressGroupPositions(progress, 60, 44);
		int leftGap = groupX[0] - progress.x;
		int middleGap = groupX[1] - groupX[0] - 60;
		int rightGap = progress.x + progress.width - groupX[1] - 44;
		assertEquals(leftGap, middleGap);
		assertTrue(rightGap >= middleGap && rightGap <= middleGap + 2);

		BufferedImage metricsImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D metricsGraphics = metricsImage.createGraphics();
		try
		{
			FontMetrics metrics =
				metricsGraphics.getFontMetrics(JournalOverlay.progressFont());
			int centeredBaseline = (int) Math.round(
				progress.getCenterY()
					+ (metrics.getAscent() - metrics.getDescent()) / 2.0);
			assertEquals(
				centeredBaseline + 1,
				ControlsRenderer.progressTextBaseline(metrics, progress));

			Rectangle control = new Rectangle(0, 0, 73, 26);
			Rectangle expectedIcon = new Rectangle(52, 6, 14, 14);
			FontMetrics buttonMetrics =
				metricsGraphics.getFontMetrics(JournalOverlay.compactSmallFont());
			for (String label : Arrays.asList("Star", "Unstar", "Filters", "Search"))
			{
				int labelWidth = buttonMetrics.stringWidth(label);
				Rectangle icon = ControlsRenderer.listHeaderActionIconBounds(control);
				Rectangle face = JournalOverlay.headerControlFaceBounds(control);
				int labelX = control.x + 7;
				int rightSlack = control.x + control.width - icon.x - icon.width;

				assertEquals(expectedIcon, icon);
				assertEquals(7, labelX - control.x);
				assertEquals(7, rightSlack);
				assertEquals(4, icon.y - face.y);
				assertEquals(4, face.y + face.height - icon.y - icon.height);
				assertTrue(icon.x - labelX - labelWidth >= 3);
			}
		}
		finally
		{
			metricsGraphics.dispose();
		}
		assertEquals(search.y + search.height + 4, type.y);
		assertEquals(22, type.height);
		assertEquals(4, type.x - typeCaption.x - typeCaption.width);
		assertEquals(type.y, typeCaption.y);
		assertEquals(type.height, typeCaption.height);
		JournalGeometry renderedGeometry = JournalGeometry.create(panel, viewport);
		List<Rectangle> controls = Arrays.asList(
			rendered.controlsRenderer.starQuestControlBounds(renderedGeometry),
			rendered.controlsRenderer.searchControlBounds(renderedGeometry),
			rendered.controlsRenderer.filterVisibilityControlBounds(
				renderedGeometry));

		for (Rectangle control : controls)
		{
			assertFalse(control.isEmpty());
			pointer.set(new Point(
				control.x + control.width / 2,
				control.y + control.height / 2));
			assertInvertedControlBevel(
				rendered.canvas,
				renderAgainWithCanvas(overlay, viewport.getSize()),
				control);
		}
	}

	@Test
	public void filterTriggersAndPopupsUseTheGamePaletteAndGeometry()
	{
		assertEquals(new Color(0, 0, 0, 38), FilterRenderer.filterTriggerBackgroundColor());
		assertEquals(Color.BLACK, FilterRenderer.filterTriggerOuterBorderColor());
		assertEquals(new Color(0x44, 0x44, 0x42), FilterRenderer.filterTriggerInnerBorderColor());
		assertEquals(Color.WHITE, FilterRenderer.filterTriggerTextColor());
		assertEquals(new Color(0xFF, 0x99, 0x33), FilterRenderer.filterTriggerTextColor(true));
		assertEquals(Color.WHITE, FilterRenderer.filterTriggerArrowColor());
		assertEquals(Color.WHITE, FilterRenderer.filterPopupTextColor());
		assertEquals(new Color(0xFF, 0x99, 0x33), FilterRenderer.filterPopupTextColor(true));
		assertEquals(new Color(0, 0, 0, 0), FilterRenderer.filterPopupRowOverlayColor(0));
		assertEquals(new Color(0xFF, 0xFF, 0xFF, 13), FilterRenderer.filterPopupRowOverlayColor(1));
		assertEquals(new Color(0, 0, 0, 0), FilterRenderer.filterPopupRowOverlayColor(2));
		assertEquals(new Color(0xE9, 0xBF, 0x6F), FilterRenderer.filterActiveOptionMarkerColor());

		Rectangle row = new Rectangle(10, 20, 220, 22);
		Rectangle trigger = ControlsRenderer.filterTriggerBounds(row);
		Rectangle arrow = ControlsRenderer.filterArrowBounds(trigger);
		assertEquals(new Rectangle(84, 20, 146, 22), trigger);
		assertEquals(new Rectangle(215, 27, 9, 7), arrow);
		assertEquals(6, trigger.x + trigger.width - arrow.x - arrow.width);
		assertEquals(7, arrow.y - trigger.y);
		assertEquals(8, trigger.y + trigger.height - arrow.y - arrow.height);
		assertEquals(6, JournalOverlay.scrollbarTrackArc());
		assertEquals(6, JournalOverlay.scrollbarThumbArc());
		Rectangle popup = FilterRenderer.filterPopupBounds(
			new Rectangle(100, 50, 80, 19),
			new Rectangle(20, 20, 220, 300),
			JournalOverlay.FilterControl.ORDER,
			3);
		assertEquals(new Rectangle(30, 71, 150, 59), popup);
		assertEquals(
			new Rectangle(31, 72, 148, 19),
			FilterRenderer.filterPopupOptionRowBounds(
				popup,
				JournalOverlay.FilterControl.ORDER,
				0));

		Rectangle checklistPopup = FilterRenderer.filterPopupBounds(
			new Rectangle(100, 50, 80, 19),
			new Rectangle(20, 20, 220, 300),
			JournalOverlay.FilterControl.TYPE,
			3);
		assertEquals(new Rectangle(30, 71, 150, 85), checklistPopup);
		assertEquals(
			new Rectangle(31, 129, 148, 26),
			FilterRenderer.filterPopupActionRowBounds(checklistPopup));
		assertEquals(
			new Rectangle(31, 72, 148, 19),
			FilterRenderer.filterPopupOptionRowBounds(
				checklistPopup,
				JournalOverlay.FilterControl.TYPE,
				0));
	}

	@Test
	public void openFilterPopupBlocksUnderlyingQuestHitAndScroll()
	{
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		List<JournalSnapshot.QuestListItem> quests = new ArrayList<>();
		for (int index = 0; index < 40; index++)
		{
			quests.add(new JournalSnapshot.QuestListItem(
				"QUEST_" + index,
				"Quest " + index,
				JournalSnapshot.QuestType.QUEST,
				JournalSnapshot.QuestState.NOT_STARTED,
				JournalSnapshot.QuestDifficulty.NOVICE,
				false,
				Collections.emptyMap()));
		}
		JournalSnapshot snapshot = new JournalSnapshot(
			quests,
			null,
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, quests.size(), 0, 0));
		JournalOverlay overlay = renderSnapshot(snapshot, viewport, panel);
		overlay.toggleFilterVisibility();
		renderAgain(overlay, viewport.getSize());

		JournalGeometry geometry = JournalGeometry.create(panel, viewport, true);
		Rectangle popup = FilterRenderer.filterPopupBounds(
			testFilterRenderer(overlay).orderControlBounds(geometry),
			geometry.questListPaneBounds(),
			JournalOverlay.FilterControl.ORDER,
			JournalSnapshot.QuestOrder.values().length);
		Point coveredQuestPoint = null;
		for (int y = popup.y; y < popup.y + popup.height && coveredQuestPoint == null; y++)
		{
			for (int x = popup.x; x < popup.x + popup.width; x++)
			{
				Point candidate = new Point(x, y);
				if (overlay.questIdAt(candidate) != null)
				{
					coveredQuestPoint = candidate;
					break;
				}
			}
		}

		assertNotNull(coveredQuestPoint);
		assertTrue(overlay.scrollAt(coveredQuestPoint, 1));
		overlay.resetQuestListScroll();
		renderAgain(overlay, viewport.getSize());
		overlay.toggleFilterDropdown(JournalOverlay.FilterControl.ORDER);
		renderAgain(overlay, viewport.getSize());

		assertTrue(overlay.isFilterPopupSurface(coveredQuestPoint));
		assertNull(overlay.questIdAt(coveredQuestPoint));
		assertFalse(overlay.scrollAt(coveredQuestPoint, 1));
	}

	@Test
	public void checklistPopupActionsUseHeaderSkinsStripesAndTheStarredAccentIcon()
	{
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		JournalSnapshot.QuestListOptions options = JournalSnapshot.QuestListOptions.defaults();
		JournalSnapshot snapshot = new JournalSnapshot(
			Collections.emptyList(),
			null,
			null,
			options,
			new JournalSnapshot.QuestProgress(0, 0, 0, 0));
		AtomicReference<Point> pointer = new AtomicReference<>(new Point(-1, -1));
		RenderedSnapshot rendered = renderSnapshotWithCanvas(
			snapshot,
			viewport,
			panel,
			new RenderOptions().pointer(pointer));
		rendered.overlay.toggleFilterVisibility();
		rendered.overlay.toggleFilterDropdown(JournalOverlay.FilterControl.TYPE);
		BufferedImage normal = renderAgainWithCanvas(rendered.overlay, viewport.getSize());
		JournalGeometry geometry = JournalGeometry.create(panel, viewport, true);
		Rectangle popup = FilterRenderer.filterPopupBounds(
			rendered.filterRenderer.typeControlBounds(geometry),
			geometry.questListPaneBounds(),
			JournalOverlay.FilterControl.TYPE,
			options.getTypes().size() + 1);
		Rectangle actionRow = FilterRenderer.filterPopupActionRowBounds(popup);
		Rectangle all = FilterRenderer.checklistActionButtonBounds(
			actionRow,
			JournalOverlay.FilterSelectionAction.SELECT_ALL);
		Rectangle none = FilterRenderer.checklistActionButtonBounds(
			actionRow,
			JournalOverlay.FilterSelectionAction.SELECT_NONE);

		assertEquals(22, all.height);
		assertEquals(all.height, none.height);
		assertEquals(2, all.y - actionRow.y);
		assertEquals(
			2,
			actionRow.y + actionRow.height - all.y - all.height);
		assertEquals(all.y, none.y);
		Rectangle lastOption = FilterRenderer.filterPopupOptionRowBounds(
			popup,
			JournalOverlay.FilterControl.TYPE,
			options.getTypes().size());
		assertEquals(
			lastOption.y + lastOption.height,
			actionRow.y);
		assertOutsetControlBevel(normal, all);
		assertOutsetControlBevel(normal, none);
		pointer.set(new Point(all.x + all.width / 2, all.y + all.height / 2));
		assertInvertedControlBevel(
			normal,
			renderAgainWithCanvas(rendered.overlay, viewport.getSize()),
			all);

		Rectangle starredRow = FilterRenderer.filterPopupOptionRowBounds(
			popup,
			JournalOverlay.FilterControl.TYPE,
			0);
		Rectangle secondOption = FilterRenderer.filterPopupOptionRowBounds(
			popup,
			JournalOverlay.FilterControl.TYPE,
			1);
		Color popupBackground = new Color(0x3C, 0x35, 0x2B);
		int background = popupBackground.getRGB();
		int stripedBackground = compositeColor(
			popupBackground,
			FilterRenderer.filterPopupRowOverlayColor(1));
		assertEquals(
			background,
			normal.getRGB(actionRow.x + 1, actionRow.y + actionRow.height / 2));
		assertEquals(
			background,
			normal.getRGB(
				starredRow.x + starredRow.width - 2,
				starredRow.y + starredRow.height / 2));
		assertEquals(
			stripedBackground,
			normal.getRGB(
				secondOption.x + secondOption.width - 2,
				secondOption.y + secondOption.height / 2));

		Rectangle starredIcon = FilterRenderer.filterTypeIconSlotBounds(
			starredRow,
			starredRow.x + 20);
		assertEquals(12, starredIcon.width);
		assertEquals(
			starredRow.x + 36,
			FilterRenderer.filterTypeLabelX(starredIcon));
		assertEquals(
			new Color(0xE9, 0xBF, 0x6F).getRGB(),
			normal.getRGB(
				starredIcon.x + starredIcon.width / 2,
				starredIcon.y + starredIcon.height / 2));
	}

	@Test
	public void membershipFilterRowsUseTheOverviewMembershipEmblems()
	{
		assertEquals(
			SpriteID.WorldswitcherStars.FREE,
			JournalOverlay.membershipEmblemSpriteId(false));
		assertEquals(
			SpriteID.WorldswitcherStars.MEMBERS,
			JournalOverlay.membershipEmblemSpriteId(true));
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		Color freeLight = new Color(0xD8, 0xD8, 0xD8);
		Color freeDark = new Color(0x68, 0x68, 0x68);
		Color memberGold = new Color(0xD8, 0xA0, 0x24);
		BufferedImage freeIcon = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);
		freeIcon.setRGB(0, 1, freeDark.getRGB());
		freeIcon.setRGB(1, 1, freeLight.getRGB());
		BufferedImage memberIcon = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);
		memberIcon.setRGB(1, 1, memberGold.getRGB());
		SpriteManager spriteManager = mock(SpriteManager.class);
		when(spriteManager.getSprite(SpriteID.WorldswitcherStars.FREE, 0))
			.thenReturn(freeIcon);
		when(spriteManager.getSprite(SpriteID.WorldswitcherStars.MEMBERS, 0))
			.thenReturn(memberIcon);
		JournalSnapshot snapshot = new JournalSnapshot(
			Collections.emptyList(),
			null,
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 0, 0, 0));
		RenderedSnapshot rendered = renderSnapshotWithCanvas(
			snapshot,
			viewport,
			panel,
			new RenderOptions().sprites(spriteManager));
		JournalOverlay overlay = rendered.overlay;
		overlay.toggleFilterVisibility();
		overlay.toggleFilterDropdown(JournalOverlay.FilterControl.MEMBERSHIP);
		BufferedImage canvas = renderAgainWithCanvas(overlay, viewport.getSize());
		JournalGeometry geometry = JournalGeometry.create(panel, viewport, true);
		Rectangle popup = FilterRenderer.filterPopupBounds(
			rendered.filterRenderer.membershipControlBounds(geometry),
			geometry.questListPaneBounds(),
			JournalOverlay.FilterControl.MEMBERSHIP,
			JournalSnapshot.QuestMembership.values().length);

		for (int index = 0; index < JournalSnapshot.QuestMembership.values().length; index++)
		{
			Rectangle row = FilterRenderer.filterPopupOptionRowBounds(
				popup,
				JournalOverlay.FilterControl.MEMBERSHIP,
				index);
			Rectangle icon = new Rectangle(row.x + 20, row.y + (row.height - 12) / 2, 12, 12);
			int rowBackground = canvas.getRGB(
				row.x + row.width - 2,
				row.y + row.height / 2);
			assertTrue(countPixelsDifferentFrom(
				canvas,
				icon,
				rowBackground) > 0);
			if (JournalSnapshot.QuestMembership.values()[index]
				== JournalSnapshot.QuestMembership.FREE_TO_PLAY)
			{
				assertTrue(countPixelsEqualTo(canvas, icon, freeLight.getRGB()) > 0);
				assertTrue(countPixelsEqualTo(canvas, icon, freeDark.getRGB()) > 0);
			}
			else
			{
				assertTrue(countPixelsEqualTo(canvas, icon, memberGold.getRGB()) > 0);
			}
		}
	}

	@Test
	public void singleSelectPopupUsesAnAlignedAccentArrowWithoutAnActivePerimeter()
	{
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		JournalSnapshot.QuestListOptions options = JournalSnapshot.QuestListOptions.defaults();
		JournalSnapshot snapshot = new JournalSnapshot(
			Collections.emptyList(),
			null,
			null,
			options,
			new JournalSnapshot.QuestProgress(0, 0, 0, 0));
		RenderedSnapshot rendered = renderSnapshotWithCanvas(
			snapshot, viewport, panel, new RenderOptions());
		rendered.overlay.toggleFilterVisibility();
		rendered.overlay.toggleFilterDropdown(JournalOverlay.FilterControl.ORDER);
		BufferedImage canvas = renderAgainWithCanvas(rendered.overlay, viewport.getSize());
		JournalGeometry geometry = JournalGeometry.create(panel, viewport, true);
		Rectangle popup = FilterRenderer.filterPopupBounds(
			rendered.filterRenderer.orderControlBounds(geometry),
			geometry.questListPaneBounds(),
			JournalOverlay.FilterControl.ORDER,
			options.getOrders().size());
		Rectangle selectedRow = FilterRenderer.filterPopupOptionRowBounds(
			popup,
			JournalOverlay.FilterControl.ORDER,
			0);
		Rectangle selectedMarker = FilterRenderer.filterOptionMarkerBounds(selectedRow);
		int accent = FilterRenderer.filterActiveOptionMarkerColor().getRGB();
		int labelX = FilterRenderer.filterOptionLabelX(selectedMarker);

		assertTrue(countPixelsEqualTo(canvas, selectedMarker, accent) > 0);
		assertEquals(
			selectedMarker.y - selectedRow.y,
			selectedRow.y + selectedRow.height - selectedMarker.y - selectedMarker.height);
		for (int index = 0; index < options.getOrders().size(); index++)
		{
			Rectangle row = FilterRenderer.filterPopupOptionRowBounds(
				popup,
				JournalOverlay.FilterControl.ORDER,
				index);
			Rectangle marker = FilterRenderer.filterOptionMarkerBounds(row);
			assertEquals(selectedMarker.x, marker.x);
			assertEquals(labelX, FilterRenderer.filterOptionLabelX(marker));
			if (index > 0)
			{
				assertEquals(0, countPixelsEqualTo(canvas, marker, accent));
			}
		}
		assertRectanglePerimeterDoesNotContain(canvas, selectedRow, accent);
	}

	@Test
	public void statusChecklistKeepsSingleAndMultipleSelectionCheckmarksAndEmptyGutters()
	{
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		JournalSnapshot.QuestListOptions options = JournalSnapshot.QuestListOptions.defaults();
		JournalSnapshot snapshot = new JournalSnapshot(
			Collections.emptyList(),
			null,
			null,
			options,
			new JournalSnapshot.QuestProgress(0, 0, 0, 0));
		Set<JournalSnapshot.QuestState> multipleSelections = new LinkedHashSet<>(Arrays.asList(
			JournalSnapshot.QuestState.NOT_STARTED,
			JournalSnapshot.QuestState.IN_PROGRESS));
		List<Set<JournalSnapshot.QuestState>> selections = Arrays.asList(
			Collections.singleton(JournalSnapshot.QuestState.IN_PROGRESS),
			multipleSelections);
		for (Set<JournalSnapshot.QuestState> selectedStates : selections)
		{
			JournalSnapshot.QuestFilter filter = JournalSnapshot.QuestFilter.all()
				.withStateSelections(selectedStates);
			RenderedSnapshot rendered = renderSnapshotWithCanvas(
				snapshot,
				viewport,
				panel,
				new RenderOptions().filter(filter));
			rendered.overlay.toggleFilterVisibility();
			rendered.overlay.toggleFilterDropdown(JournalOverlay.FilterControl.STATUS);
			BufferedImage canvas = renderAgainWithCanvas(rendered.overlay, viewport.getSize());
			JournalGeometry geometry = JournalGeometry.create(panel, viewport, true);
			Rectangle popup = FilterRenderer.filterPopupBounds(
				rendered.filterRenderer.statusControlBounds(geometry),
				geometry.questListPaneBounds(),
				JournalOverlay.FilterControl.STATUS,
				JournalSnapshot.QuestState.values().length);
			assertStatusChecklistMarkers(canvas, popup, selectedStates);
		}
	}

	@Test
	public void popupOptionHoverChangesOnlyItsTextToOrange()
	{
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		JournalSnapshot.QuestListOptions options = JournalSnapshot.QuestListOptions.defaults();
		JournalSnapshot snapshot = new JournalSnapshot(
			Collections.emptyList(),
			null,
			null,
			options,
			new JournalSnapshot.QuestProgress(0, 0, 0, 0));
		AtomicReference<Point> pointer = new AtomicReference<>(new Point(-1, -1));
		RenderedSnapshot rendered = renderSnapshotWithCanvas(
			snapshot,
			viewport,
			panel,
			new RenderOptions().pointer(pointer));
		rendered.overlay.toggleFilterVisibility();
		rendered.overlay.toggleFilterDropdown(JournalOverlay.FilterControl.ORDER);
		BufferedImage normal = renderAgainWithCanvas(rendered.overlay, viewport.getSize());
		JournalGeometry geometry = JournalGeometry.create(panel, viewport, true);
		Rectangle popup = FilterRenderer.filterPopupBounds(
			rendered.filterRenderer.orderControlBounds(geometry),
			geometry.questListPaneBounds(),
			JournalOverlay.FilterControl.ORDER,
			options.getOrders().size());
		Rectangle hoveredRow = FilterRenderer.filterPopupOptionRowBounds(
			popup,
			JournalOverlay.FilterControl.ORDER,
			1);
		pointer.set(new Point(hoveredRow.x + hoveredRow.width / 2, hoveredRow.y + hoveredRow.height / 2));
		assertNotNull(rendered.overlay.filterSelectionAt(pointer.get()));
		BufferedImage hovered = renderAgainWithCanvas(rendered.overlay, viewport.getSize());
		Rectangle marker = FilterRenderer.filterOptionMarkerBounds(hoveredRow);
		int labelX = FilterRenderer.filterOptionLabelX(marker);
		boolean textShiftedTowardOrange = false;
		for (int y = hoveredRow.y; y < hoveredRow.y + hoveredRow.height; y++)
		{
			for (int x = labelX; x < hoveredRow.x + hoveredRow.width - 6; x++)
			{
				Color normalPixel = new Color(normal.getRGB(x, y), true);
				Color hoveredPixel = new Color(hovered.getRGB(x, y), true);
				textShiftedTowardOrange |= hoveredPixel.getRed() >= normalPixel.getRed()
					&& hoveredPixel.getGreen() < normalPixel.getGreen()
					&& hoveredPixel.getBlue() < normalPixel.getBlue();
			}
		}
		assertTrue(textShiftedTowardOrange);
		for (int y = hoveredRow.y; y < hoveredRow.y + hoveredRow.height; y++)
		{
			for (int x = hoveredRow.x + hoveredRow.width - 5;
				x < hoveredRow.x + hoveredRow.width;
				x++)
			{
				assertEquals(normal.getRGB(x, y), hovered.getRGB(x, y));
			}
		}
	}

	@Test
	public void filterRowsPlaceStatusBeforeOrder()
	{
		JournalGeometry geometry = JournalGeometry.create(
			new Rectangle(20, 30, 900, 600),
			new Rectangle(0, 0, 1_200, 800),
			true);
		JournalOverlay overlay =
			new JournalOverlay(null, null, null, null, null, null);
		FilterRenderer renderer = testFilterRenderer(overlay);

		Rectangle membership = renderer.membershipControlBounds(geometry);
		Rectangle status = renderer.statusControlBounds(geometry);
		Rectangle order = renderer.orderControlBounds(geometry);

		assertTrue(status.y > membership.y);
		assertTrue(order.y > status.y);
	}

	@Test
	public void filterHoverChangesOnlyTheTextAndLeavesTheFlatTriggerAndArrowAlone()
	{
		BufferedImage normal = renderFilterControl(false, false);
		BufferedImage hovered = renderFilterControl(true, false);
		Rectangle trigger = new Rectangle(0, 0, normal.getWidth(), normal.getHeight());
		Rectangle arrow = ControlsRenderer.filterArrowBounds(trigger);

		assertEquals(
			FilterRenderer.filterTriggerOuterBorderColor().getRGB(),
			hovered.getRGB(0, trigger.height / 2));
		assertEquals(normal.getRGB(0, 0), hovered.getRGB(0, 0));
		assertEquals(normal.getRGB(1, 1), hovered.getRGB(1, 1));
		assertEquals(normal.getRGB(trigger.width / 2, 2),
			hovered.getRGB(trigger.width / 2, 2));
		assertEquals(0, countPixelsEqualTo(
			normal,
			trigger,
			FilterRenderer.filterTriggerTextColor(true).getRGB()));
		assertTrue(countPixelsEqualTo(
			hovered,
			trigger,
			FilterRenderer.filterTriggerTextColor(true).getRGB()) > 0);
		for (int y = arrow.y; y < arrow.y + arrow.height; y++)
		{
			for (int x = arrow.x; x < arrow.x + arrow.width; x++)
			{
				assertEquals(normal.getRGB(x, y), hovered.getRGB(x, y));
			}
		}
	}

	@Test
	public void filterCaptionsFitAndArrowGlyphsAreSolidSymmetricAndChangeDirection()
	{
		BufferedImage closed = renderFilterControl(false, false);
		BufferedImage open = renderFilterControl(false, true);
		Rectangle arrow = ControlsRenderer.filterArrowBounds(
			new Rectangle(0, 0, closed.getWidth(), closed.getHeight()));
		int centerX = arrow.x + arrow.width / 2;
		int centerY = arrow.y + arrow.height / 2;
		int arrowColor = FilterRenderer.filterTriggerArrowColor().getRGB();

		boolean orientationChanges = false;
		for (int y = centerY - 4; y <= centerY + 4; y++)
		{
			for (int x = centerX - 3; x <= centerX + 3; x++)
			{
				orientationChanges |= closed.getRGB(x, y) != open.getRGB(x, y);
			}
			for (int distance = 1; distance <= 3; distance++)
			{
				boolean leftChanges = closed.getRGB(centerX - distance, y)
					!= open.getRGB(centerX - distance, y);
				boolean rightChanges = closed.getRGB(centerX + distance, y)
					!= open.getRGB(centerX + distance, y);
				assertEquals(leftChanges, rightChanges,
					"Arrow change mask at y=" + y + ", distance=" + distance);
			}
		}
		assertTrue(orientationChanges);
		assertEquals(arrowColor, closed.getRGB(centerX - 2, centerY - 1));
		assertNotEquals(arrowColor, open.getRGB(centerX - 2, centerY - 1));
		assertEquals(arrowColor, open.getRGB(centerX - 2, centerY + 1));
		assertNotEquals(arrowColor, closed.getRGB(centerX - 2, centerY + 1));

		BufferedImage metricsImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D metricsGraphics = metricsImage.createGraphics();
		try
		{
			metricsGraphics.setFont(JournalOverlay.compactSmallFont());
			int captionWidth = 69;
			for (String caption : Arrays.asList(
				"Type:",
				"Difficulty:",
				"Membership:",
				"Status:",
				"Order:"))
			{
				assertTrue(metricsGraphics.getFontMetrics().stringWidth(caption) <= captionWidth);
			}
		}
		finally
		{
			metricsGraphics.dispose();
		}
	}

	@Test
	public void questTypeMarkersUseQuestDiaryAndSkillingSprites()
	{
		BufferedImage quest = solidImage(9, 9, Color.RED);
		BufferedImage diary = solidImage(9, 9, Color.GREEN);
		BufferedImage skill = solidImage(9, 9, Color.BLUE);
		SpriteManager spriteManager = mock(SpriteManager.class);
		when(spriteManager.getSprite(SpriteID.SideIcons.QUEST, 0)).thenReturn(quest);
		when(spriteManager.getSprite(
			SpriteID.AchievementDiaryIcons.GREEN_ACHIEVEMENT_DIARIES,
			0)).thenReturn(diary);
		when(spriteManager.getSprite(SpriteID.SideIcons.STATS, 0)).thenReturn(skill);
		JournalPanelAssets assets = JournalPanelAssets.load(spriteManager);

		assertSame(quest, FilterRenderer.questTypeMarkerImage(
			JournalSnapshot.QuestType.QUEST, assets));
		assertSame(quest, FilterRenderer.questTypeMarkerImage(
			JournalSnapshot.QuestType.MINIQUEST, assets));
		assertSame(quest, FilterRenderer.questTypeMarkerImage(
			JournalSnapshot.QuestType.GENERIC, assets));
		assertSame(quest, FilterRenderer.questTypeMarkerImage(
			JournalSnapshot.QuestType.PLAYER_QUEST, assets));
		assertSame(diary, FilterRenderer.questTypeMarkerImage(
			JournalSnapshot.QuestType.ACHIEVEMENT_DIARY, assets));
		assertSame(skill, FilterRenderer.questTypeMarkerImage(
			JournalSnapshot.QuestType.SKILL, assets));

		for (JournalSnapshot.QuestType type : JournalSnapshot.QuestType.values())
		{
			JournalSnapshot.QuestListItem item = new JournalSnapshot.QuestListItem(
				type.name(),
				type.name(),
				type,
				JournalSnapshot.QuestState.NOT_STARTED,
				JournalSnapshot.QuestDifficulty.NOVICE,
				false,
				Collections.emptyMap());
			assertEquals(
				type != JournalSnapshot.QuestType.QUEST,
				QuestListRenderer.questListMarkerAvailable(item, assets));
		}
	}

	@Test
	public void clearActiveQuestUsesTheExactWindowCloseControl()
	{
		BufferedImage normal = solidImage(13, 13, Color.RED);
		BufferedImage hovered = solidImage(13, 13, Color.BLUE);
		JournalPanelAssets assets = headerControlAssets(normal, hovered);
		JournalOverlay overlay = new JournalOverlay(null, null, null, null, null, null);
		Rectangle titlebarIcon = JournalOverlay.closeIconBounds(
			new Rectangle(0, 0, 21, 21));
		Rectangle activeIcon = JournalOverlay.closeIconBounds(
			new Rectangle(0, 0, 26, 26));
		assertEquals(titlebarIcon.getSize(), activeIcon.getSize());
		for (Point pointer : Arrays.asList(new Point(-1, -1), new Point(10, 10)))
		{
			BufferedImage expected = renderHeaderControl(
				overlay,
				JournalOverlay.HeaderControlIcon.CLOSE,
				pointer,
				assets);
			BufferedImage actual = renderClearActiveQuestControl(
				overlay,
				pointer,
				assets);
			assertTrue(Arrays.equals(
				expected.getRGB(
					titlebarIcon.x,
					titlebarIcon.y,
					titlebarIcon.width,
					titlebarIcon.height,
					null,
					0,
					titlebarIcon.width),
				actual.getRGB(
					activeIcon.x,
					activeIcon.y,
					activeIcon.width,
					activeIcon.height,
					null,
					0,
					activeIcon.width)));
		}
	}

	@Test
	public void journalContentIconsReceiveABlackPixelOutline()
	{
		BufferedImage source = new BufferedImage(5, 5, BufferedImage.TYPE_INT_ARGB);
		source.setRGB(2, 2, Color.ORANGE.getRGB());
		BufferedImage outlined = JournalOverlay.createBlackOutlinedIcon(source);

		assertEquals(7, outlined.getWidth());
		assertEquals(7, outlined.getHeight());
		assertEquals(Color.ORANGE.getRGB(), outlined.getRGB(3, 3));
		assertEquals(Color.BLACK.getRGB(), outlined.getRGB(3, 2));
		assertEquals(Color.BLACK.getRGB(), outlined.getRGB(2, 3));
		assertEquals(Color.BLACK.getRGB(), outlined.getRGB(4, 3));
		assertEquals(Color.BLACK.getRGB(), outlined.getRGB(3, 4));

		BufferedImage edgeSource = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
		edgeSource.setRGB(0, 0, Color.CYAN.getRGB());
		BufferedImage edgeOutlined = JournalOverlay.createBlackOutlinedIcon(edgeSource);
		assertEquals(Color.CYAN.getRGB(), edgeOutlined.getRGB(1, 1));
		assertEquals(Color.BLACK.getRGB(), edgeOutlined.getRGB(0, 1));
		assertEquals(Color.BLACK.getRGB(), edgeOutlined.getRGB(1, 0));
	}

	@Test
	public void activeQuestListUsesTheNativeCombatIconWithoutAnOrangeBorder()
	{
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		String questId = "COOKS_ASSISTANT";
		JournalSnapshot snapshot = new JournalSnapshot(
			Collections.singletonList(new JournalSnapshot.QuestListItem(
				questId,
				"Cook's Assistant",
				JournalSnapshot.QuestType.QUEST,
				JournalSnapshot.QuestState.IN_PROGRESS,
				JournalSnapshot.QuestDifficulty.NOVICE,
				false,
				Collections.emptyMap())),
			null,
			new JournalSnapshot.ActiveQuest(
				questId,
				"Cook's Assistant",
				JournalSnapshot.QuestState.IN_PROGRESS),
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 1, 0, 0));
		BufferedImage combatIcon = solidImage(15, 15, Color.CYAN);
		SpriteManager spriteManager = mock(SpriteManager.class);
		when(spriteManager.getSprite(SpriteID.AccountIcons._0, 0)).thenReturn(combatIcon);

		RenderedSnapshot rendered = renderSnapshotWithCanvas(
			snapshot,
			viewport,
			panel,
			new RenderOptions().sprites(spriteManager));
		Rectangle list = QuestListRenderer.questListScrollBounds(
			JournalGeometry.create(panel, viewport).questListContentBounds());
		Rectangle row = new Rectangle(
			list.x,
			list.y,
			list.width,
			22);
		Rectangle marker = QuestListRenderer.questListActiveMarkerBounds(row);

		assertTrue(countPixelsEqualTo(rendered.canvas, marker, Color.CYAN.getRGB()) > 0);
		assertNotEquals(
			Color.CYAN.getRGB(),
			rendered.canvas.getRGB(marker.x, marker.y + marker.height / 2));
		assertEquals(
			Color.CYAN.getRGB(),
			rendered.canvas.getRGB(
				marker.x + marker.width - 1,
				marker.y + marker.height / 2));
		assertEquals(
			new Rectangle(marker.x + 1, marker.y, 15, 15),
			JournalOverlay.pixelArtRightAlignedDestinationBounds(
				new Rectangle(0, 0, 15, 15),
				marker));
		assertEquals(
			0,
			countPixelsEqualTo(
				rendered.canvas,
				marker,
				new Color(0xFF, 0x99, 0x33).getRGB()));
	}

	@Test
	public void settingsControlKeepsItsTooltipAndSpacing()
	{
		assertEquals(
			"Open RuneLite Quest Helper settings",
			TooltipRenderer.settingsTooltipText());
		assertEquals(
			new Rectangle(559, 37, 21, 21),
			JournalOverlay.settingsButtonBounds(new Rectangle(584, 37, 21, 21)));
	}

	@Test
	public void titleSeparatorOverlapsOnePixelIntoWindowSideBorders()
	{
		assertEquals(
			new Rectangle(5, 30, 610, 6),
			JournalOverlay.titleSeparatorBounds(620, 480, 30));
		assertEquals(
			new Rectangle(4, 4, 1, 6),
			JournalOverlay.titleSeparatorBounds(9, 20, 4));
		assertEquals(
			new Rectangle(3, 5, 0, 0),
			JournalOverlay.titleSeparatorBounds(6, 5, 10));
	}

	@Test
	public void allHeaderControlsUseTheSameSkinAndNativeCloseGlyphStates()
	{
		BufferedImage normal = solidImage(13, 13, Color.RED);
		BufferedImage hovered = solidImage(13, 13, Color.BLUE);
		JournalPanelAssets assets = headerControlAssets(normal, hovered);
		JournalOverlay overlay = new JournalOverlay(null, null, null, null, null, null);

		BufferedImage normalControl = renderHeaderControl(
			overlay,
			JournalOverlay.HeaderControlIcon.CLOSE,
			new Point(-1, -1),
			assets);
		BufferedImage hoveredControl = renderHeaderControl(
			overlay,
			JournalOverlay.HeaderControlIcon.CLOSE,
			new Point(10, 10),
			assets);

		BufferedImage maximizeControl = renderHeaderControl(
			overlay,
			JournalOverlay.HeaderControlIcon.MAXIMIZE,
			new Point(-1, -1),
			assets);

		assertEquals(Color.RED.getRGB(), normalControl.getRGB(10, 10));
		assertNotEquals(Color.RED.getRGB(), normalControl.getRGB(0, 0));
		assertEquals(Color.BLUE.getRGB(), hoveredControl.getRGB(10, 10));
		assertNotEquals(Color.BLUE.getRGB(), hoveredControl.getRGB(20, 20));
		assertEquals(normalControl.getRGB(0, 0), maximizeControl.getRGB(0, 0));
		assertEquals(normalControl.getRGB(1, 1), maximizeControl.getRGB(1, 1));
		assertEquals(normalControl.getRGB(2, 2), maximizeControl.getRGB(2, 2));
	}

	@Test
	public void fallbackCloseGlyphIsThickAndPointed()
	{
		JournalOverlay overlay = new JournalOverlay(null, null, null, null, null, null);
		BufferedImage close = renderHeaderControl(
			overlay,
			JournalOverlay.HeaderControlIcon.CLOSE,
			new Point(-1, -1),
			null);
		assertNotEquals(close.getRGB(10, 3), close.getRGB(10, 10));
		assertNotEquals(close.getRGB(3, 3), close.getRGB(4, 6));
		assertNotEquals(close.getRGB(17, 17), close.getRGB(16, 14));
	}

	@Test
	public void maximizeIsThickAndCenteredAndSettingsUsesTheNativeBankButtonFrames()
	{
		JournalOverlay overlay = new JournalOverlay(null, null, null, null, null, null);
		BufferedImage maximize = renderHeaderControl(
			overlay,
			JournalOverlay.HeaderControlIcon.MAXIMIZE,
			new Point(-1, -1),
			null);
		assertNotEquals(maximize.getRGB(3, 10), maximize.getRGB(4, 10));
		assertNotEquals(maximize.getRGB(4, 10), maximize.getRGB(10, 10));
		assertNotEquals(maximize.getRGB(10, 10), maximize.getRGB(16, 10));

		BufferedImage settingsButton = solidImage(21, 21, Color.MAGENTA);
		BufferedImage settingsButtonHovered = solidImage(21, 21, Color.CYAN);
		JournalPanelAssets assets = headerControlAssets(
			null,
			null,
			settingsButton,
			settingsButtonHovered);
		BufferedImage settings = renderHeaderControl(
			overlay,
			JournalOverlay.HeaderControlIcon.SETTINGS,
			new Point(-1, -1),
			assets);
		BufferedImage settingsHovered = renderHeaderControl(
			overlay,
			JournalOverlay.HeaderControlIcon.SETTINGS,
			new Point(10, 10),
			assets);
		assertEquals(Color.MAGENTA.getRGB(), settings.getRGB(0, 0));
		assertEquals(Color.MAGENTA.getRGB(), settings.getRGB(20, 20));
		assertEquals(Color.CYAN.getRGB(), settingsHovered.getRGB(0, 0));
		assertEquals(Color.CYAN.getRGB(), settingsHovered.getRGB(20, 20));
	}

	@Test
	public void onlyNonExemptJournalTextLosesAnotherPoint()
	{
		Font regular = FontManager.getRunescapeFont();
		Font small = FontManager.getRunescapeSmallFont();

		assertEquals(regular.getSize2D() - 2f,
			JournalOverlay.contentFont().getSize2D(), 0.01f);
		assertEquals(regular.getSize2D() - 1f,
			JournalOverlay.overviewQuestTitleFont().getSize2D(), 0.01f);
		assertEquals(regular.getStyle(), JournalOverlay.overviewQuestTitleFont().getStyle());
		assertEquals(regular.getSize2D() - 1f,
			JournalOverlay.questListFont().getSize2D(), 0.01f);
		assertEquals(regular.getStyle(), JournalOverlay.questListFont().getStyle());
		assertEquals(regular.getFontName(), JournalOverlay.questListFont().getFontName());
		assertEquals(small.getSize2D() - 1f,
			JournalOverlay.sectionHeadingFont().getSize2D(), 0.01f);
		assertEquals(small.getSize2D() - 1f,
			JournalOverlay.overviewMetadataFont().getSize2D(), 0.01f);
		assertEquals(small.getSize2D() - 1f,
			JournalOverlay.progressFont().getSize2D(), 0.01f);
		assertEquals(small.getSize2D() - 2f,
			JournalOverlay.compactSmallFont().getSize2D(), 0.01f);
		assertEquals(JournalOverlay.compactSmallFont().getSize2D() + 2f,
			JournalOverlay.setActiveQuestFont().getSize2D(), 0.01f);
		assertEquals(new Color(0xE9, 0xBF, 0x6F),
			ControlsRenderer.noActiveQuestTextColor());
	}

	@Test
	public void selectedContentRasterNeverExceedsItsViewport()
	{
		assertEquals(
			new Dimension(320, 240),
			QuestViewRenderer.boundedViewportSurfaceSize(320, 240, 100_000, 50_000));
		assertEquals(
			new Dimension(320, 40),
			QuestViewRenderer.boundedViewportSurfaceSize(320, 240, 100, 60));
		assertEquals(
			new Dimension(320, 100),
			QuestViewRenderer.boundedViewportSurfaceSize(320, 240, 100, 0));
		assertEquals(
			new Dimension(0, 0),
			QuestViewRenderer.boundedViewportSurfaceSize(-1, -1, 100, 0));
	}

	@Test
	public void replacingAResizeBackgroundFlushesOnlyTheSupersededImage()
	{
		TrackingBufferedImage first = new TrackingBufferedImage();
		TrackingBufferedImage second = new TrackingBufferedImage();

		ChromeRenderer.flushSupersededBackground(first, first);
		assertFalse(first.flushed);

		ChromeRenderer.flushSupersededBackground(first, second);
		assertTrue(first.flushed);
		assertFalse(second.flushed);
	}

	@Test
	public void scrollbarGutterIsReservedOnlyWhenScrollingIsNeeded()
	{
		Rectangle content = new Rectangle(10, 20, 200, 300);
		assertEquals(content, JournalOverlay.scrollBodyBounds(content, false));
		assertEquals(
			new Rectangle(10, 20, 181, 300),
			JournalOverlay.scrollBodyBounds(content, true));
	}

	@Test
	public void fallbackScrollbarUsesAOnePixelBlackBorder()
	{
		Rectangle content = new Rectangle(20, 20, 100, 100);
		ScrollbarGeometry scrollbar =
			ScrollbarGeometry.create(content, 400, 150, 300);
		BufferedImage image =
			new BufferedImage(140, 140, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		try
		{
			ChromeRenderer.applyQualityHints(graphics);
			JournalOverlay overlay =
				new JournalOverlay(null, null, null, null, null, null);
			new ChromeRenderer(overlay, null).drawScrollBar(
				graphics,
				content,
				400,
				150,
				300);
		}
		finally
		{
			graphics.dispose();
		}

		Rectangle track = scrollbar.visualTrackBounds();
		assertOnePixelBlackBorder(image, track, track.y + 5);
		Rectangle thumb = scrollbar.visualThumbBounds();
		assertOnePixelBlackBorder(image, thumb, thumb.y + thumb.height / 2);
	}

	@Test
	public void nativeScrollbarTrackDoesNotDoubleItsBuiltInSideBorder()
	{
		BufferedImage nativeTrack = solidImage(
			14,
			3,
			JournalOverlay.SCROLL_TRACK);
		for (int y = 0; y < nativeTrack.getHeight(); y++)
		{
			nativeTrack.setRGB(0, y, Color.BLACK.getRGB());
			nativeTrack.setRGB(
				nativeTrack.getWidth() - 1,
				y,
				Color.BLACK.getRGB());
		}
		BufferedImage nativeThumb =
			solidImage(14, 3, JournalOverlay.SCROLL_THUMB_LEFT);
		SpriteManager spriteManager = mock(SpriteManager.class);
		when(spriteManager.getSprite(SpriteID.ScrollbarDraggerV2.TOP, 0))
			.thenReturn(nativeThumb);
		when(spriteManager.getSprite(SpriteID.ScrollbarDraggerV2.MIDDLE, 0))
			.thenReturn(nativeThumb);
		when(spriteManager.getSprite(SpriteID.ScrollbarDraggerV2.BOTTOM, 0))
			.thenReturn(nativeThumb);
		when(spriteManager.getSprite(SpriteID.ScrollbarDraggerV2.TRACK, 0))
			.thenReturn(nativeTrack);

		Rectangle content = new Rectangle(20, 20, 100, 100);
		ScrollbarGeometry scrollbar =
			ScrollbarGeometry.create(content, 400, 150, 300);
		BufferedImage image =
			new BufferedImage(140, 140, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		try
		{
			ChromeRenderer.applyQualityHints(graphics);
			JournalOverlay overlay =
				new JournalOverlay(null, null, null, null, null, null);
			ChromeRenderer renderer =
				new ChromeRenderer(overlay, spriteManager);
			renderer.currentPanelAssets();
			renderer.drawScrollBar(graphics, content, 400, 150, 300);
		}
		finally
		{
			graphics.dispose();
		}

		Rectangle track = scrollbar.visualTrackBounds();
		assertOnePixelBlackBorder(image, track, track.y + 5);
	}

	@Test
	public void releaseClearsTransientOverlayControls()
	{
		JournalOverlay overlay = new JournalOverlay(null, null, null, null, null, null);
		assertFalse(overlay.areFiltersVisible());
		overlay.toggleFilterVisibility();
		assertTrue(overlay.areFiltersVisible());
		overlay.toggleFilterDropdown(JournalOverlay.FilterControl.STATUS);

		overlay.release();

		assertFalse(overlay.areFiltersVisible());
		assertFalse(overlay.isFilterDropdownOpen());
		assertFalse(overlay.isRendered());
	}

	@Test
	public void directSnapshotRendersTheNormalJournalChromeWithoutASelection()
	{
		Client client = mock(Client.class);
		QuestJournalManager manager = mock(QuestJournalManager.class);
		QuestJournalManager.JournalPanelRenderState panelState =
			mock(QuestJournalManager.JournalPanelRenderState.class);
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		when(client.getRealDimensions()).thenReturn(viewport.getSize());
		when(manager.isJournalOpen()).thenReturn(true);
		when(manager.getJournalContentBounds(any(Rectangle.class))).thenReturn(viewport);
		when(manager.getJournalRenderState(any(Rectangle.class))).thenReturn(panelState);
		when(panelState.bounds()).thenReturn(panel);
		when(panelState.maximized()).thenReturn(false);
		when(panelState.revision()).thenReturn(7L);
		when(manager.isJournalRenderStateCurrent(7L)).thenReturn(true);
		when(manager.getJournalSnapshot()).thenReturn(
			new JournalSnapshot(
				Collections.emptyList(),
				null,
				null,
				JournalSnapshot.QuestListOptions.defaults(),
				new JournalSnapshot.QuestProgress(0, 0, 0, 0)));
		when(manager.getPointerCanvasPoint()).thenReturn(new Point(-1, -1));
		when(manager.getQuestFilter()).thenReturn(JournalSnapshot.QuestFilter.all());
		when(manager.commitJournalRender(anyLong(), any(Runnable.class))).thenAnswer(invocation ->
		{
			invocation.getArgument(1, Runnable.class).run();
			return true;
		});

		JournalOverlay overlay = new JournalOverlay(
			client,
			null,
			manager,
			mock(SpriteManager.class),
			null,
			null);
		BufferedImage canvas = new BufferedImage(1_000, 700, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = canvas.createGraphics();
		Dimension rendered;
		try
		{
			rendered = overlay.render(graphics);
		}
		finally
		{
			graphics.dispose();
		}

		assertNotNull(rendered);
		assertEquals(panel.getSize(), rendered);
		assertTrue(overlay.isRendered());
		assertEquals(panel, overlay.getPanelBounds());
		assertTrue((canvas.getRGB(panel.x + 20, panel.y + 20) >>> 24) != 0);
		overlay.release();
		assertFalse(overlay.isRendered());
	}

	@Test
	public void browsingAwayFromTheActiveQuestPublishesExplicitActionAndReturnHits()
	{
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		JournalSnapshot.SelectedQuest viewed = selectedQuest(
			"COOKS_ASSISTANT",
			"Cook's Assistant",
			JournalSnapshot.QuestState.IN_PROGRESS);
		JournalSnapshot.ActiveQuest active = new JournalSnapshot.ActiveQuest(
			"DRAGON_SLAYER_I",
			"Dragon Slayer I",
			JournalSnapshot.QuestState.IN_PROGRESS);
		JournalSnapshot snapshot = new JournalSnapshot(
			Collections.singletonList(new JournalSnapshot.QuestListItem(
				"COOKS_ASSISTANT",
				"Cook's Assistant",
				JournalSnapshot.QuestType.QUEST,
				JournalSnapshot.QuestState.IN_PROGRESS,
				JournalSnapshot.QuestDifficulty.NOVICE,
				false,
				Collections.emptyMap())),
			viewed,
			active,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 1, 0, 0));

		JournalOverlay overlay = renderSnapshot(snapshot, viewport, panel);
		Point setActive = findHitPoint(
			panel,
			overlay::setActiveQuestIdAt,
			"COOKS_ASSISTANT");
		Point returnToActiveQuest = findHitPoint(
			panel,
			overlay::returnToActiveQuestIdAt,
			"DRAGON_SLAYER_I");
		Point clearActive = findHitPoint(
			panel,
			overlay::clearActiveQuestIdAt,
			"DRAGON_SLAYER_I");

		assertNotNull(setActive);
		assertNotNull(returnToActiveQuest);
		assertNotNull(clearActive);
		assertNull(overlay.clearActiveQuestIdAt(setActive));
		assertNull(overlay.questIdAt(returnToActiveQuest));
		JournalGeometry geometry = JournalGeometry.create(panel, viewport);
		ControlsRenderer controlsRenderer =
			testControlsRenderer(overlay);
		Rectangle activeControl =
			controlsRenderer.activeQuestControlBounds(geometry);
		Rectangle activeStatus = ControlsRenderer.activeQuestStatusBounds(
			activeControl,
			true);
		Rectangle clearControl = ControlsRenderer.clearActiveQuestBounds(
			activeControl,
			active);
		assertTrue(
			controlsRenderer.setActiveQuestControlBounds(geometry)
				.contains(setActive));
		assertTrue(activeStatus.contains(returnToActiveQuest));
		assertTrue(clearControl.contains(clearActive));
		assertEquals(activeControl.y, clearControl.y);
		assertEquals(activeControl.height, clearControl.height);
		assertFalse(activeStatus.intersects(clearControl));
		assertNull(overlay.returnToActiveQuestIdAt(clearActive));
	}

	@Test
	public void anActiveQuestRemainsAButtonWhenTheJournalViewIsEmpty()
	{
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		JournalSnapshot.ActiveQuest active = new JournalSnapshot.ActiveQuest(
			"COOKS_ASSISTANT",
			"Cook's Assistant",
			JournalSnapshot.QuestState.IN_PROGRESS);
		JournalSnapshot activeSnapshot = new JournalSnapshot(
			Collections.emptyList(),
			null,
			active,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 1, 0, 0));
		JournalSnapshot inactiveSnapshot = new JournalSnapshot(
			Collections.emptyList(),
			null,
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 1, 0, 0));

		RenderedSnapshot rendered = renderSnapshotWithCanvas(
			activeSnapshot, viewport, panel, new RenderOptions());
		RenderedSnapshot inactive = renderSnapshotWithCanvas(
			inactiveSnapshot, viewport, panel, new RenderOptions());
		JournalGeometry geometry = JournalGeometry.create(panel, viewport);
		Rectangle status = ControlsRenderer.activeQuestStatusBounds(
			rendered.controlsRenderer.activeQuestControlBounds(geometry),
			true);
		Point face = new Point(status.x + 3, status.y + 3);

		assertEquals(
			"COOKS_ASSISTANT",
			rendered.overlay.returnToActiveQuestIdAt(face));
		assertNotEquals(
			inactive.canvas.getRGB(face.x, face.y),
			rendered.canvas.getRGB(face.x, face.y));
	}

	@Test
	public void settingAnActiveQuestDoesNotMoveQuestListRows()
	{
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		JournalSnapshot.SelectedQuest viewed = selectedQuest(
			"COOKS_ASSISTANT",
			"Cook's Assistant",
			JournalSnapshot.QuestState.IN_PROGRESS);
		List<JournalSnapshot.QuestListItem> quests = Collections.singletonList(
			new JournalSnapshot.QuestListItem(
				"COOKS_ASSISTANT",
				"Cook's Assistant",
				JournalSnapshot.QuestType.QUEST,
				JournalSnapshot.QuestState.IN_PROGRESS,
				JournalSnapshot.QuestDifficulty.NOVICE,
				false,
				Collections.emptyMap()));
		JournalSnapshot withoutActiveQuest = new JournalSnapshot(
			quests,
			viewed,
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 1, 0, 0));
		JournalSnapshot withActiveQuest = new JournalSnapshot(
			quests,
			viewed,
			new JournalSnapshot.ActiveQuest(
				"DRAGON_SLAYER_I",
				"Dragon Slayer I",
				JournalSnapshot.QuestState.IN_PROGRESS),
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 1, 0, 0));

		Point before = findHitPoint(
			panel,
			renderSnapshot(withoutActiveQuest, viewport, panel)::questIdAt,
			"COOKS_ASSISTANT");
		Point after = findHitPoint(
			panel,
			renderSnapshot(withActiveQuest, viewport, panel)::questIdAt,
			"COOKS_ASSISTANT");

		assertNotNull(before);
		assertNotNull(after);
		assertEquals(before.y, after.y);
	}

	@Test
	public void starQuestControlPublishesOnlyForTheBrowsedQuest()
	{
		assertEquals("Star", ControlsRenderer.starQuestButtonLabel(false));
		assertEquals("Unstar", ControlsRenderer.starQuestButtonLabel(true));
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		JournalSnapshot.SelectedQuest viewed = selectedQuest(
			"COOKS_ASSISTANT",
			"Cook's Assistant",
			JournalSnapshot.QuestState.NOT_STARTED);
		JournalSnapshot withSelection = new JournalSnapshot(
			Collections.emptyList(),
			viewed,
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 1, 0, 0));
		JournalOverlay selectedOverlay = renderSnapshot(withSelection, viewport, panel);

		Point starControl = findHitPoint(
			panel,
			selectedOverlay::starControlQuestIdAt,
			"COOKS_ASSISTANT");

		assertNotNull(starControl);
		assertTrue(testControlsRenderer(selectedOverlay).starQuestControlBounds(
			JournalGeometry.create(panel, viewport)).contains(starControl));

		JournalSnapshot withoutSelection = new JournalSnapshot(
			Collections.emptyList(),
			null,
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 1, 0, 0));
		JournalOverlay emptyOverlay = renderSnapshot(withoutSelection, viewport, panel);
		assertNull(findHitPoint(panel, emptyOverlay::starControlQuestIdAt, "COOKS_ASSISTANT"));
	}

	@Test
	public void activeCombatIconPushesTheStarMarkerOneSlotLeft()
	{
		Rectangle row = new Rectangle(10, 20, 200, 42);
		Rectangle starOnly = QuestListRenderer.questListStarMarkerBounds(row, false);
		Rectangle starWithActive = QuestListRenderer.questListStarMarkerBounds(row, true);
		Rectangle active = QuestListRenderer.questListActiveMarkerBounds(row);

		assertEquals(new Rectangle(190, 33, 16, 16), starOnly);
		assertEquals(new Rectangle(171, 33, 16, 16), starWithActive);
		assertEquals(new Rectangle(190, 33, 16, 16), active);
		assertEquals(starOnly.x + starOnly.width, active.x + active.width);
		assertEquals(3, active.x - starWithActive.x - starWithActive.width);
		assertEquals(3, starWithActive.x
			- QuestListRenderer.questListTitleRight(row, true, true));
	}

	@Test
	public void viewingTheActiveQuestUsesPlainStatusTextAndKeepsTheClearControl()
	{
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		JournalSnapshot.SelectedQuest viewed = selectedQuest(
			"COOKS_ASSISTANT",
			"Cook's Assistant",
			JournalSnapshot.QuestState.IN_PROGRESS);
		JournalSnapshot snapshot = new JournalSnapshot(
			Collections.emptyList(),
			viewed,
			new JournalSnapshot.ActiveQuest(
				"COOKS_ASSISTANT",
				"Cook's Assistant",
				JournalSnapshot.QuestState.IN_PROGRESS),
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 1, 0, 0));

		RenderedSnapshot rendered = renderSnapshotWithCanvas(
			snapshot,
			viewport,
			panel,
			new RenderOptions());
		JournalSnapshot withoutActiveQuest = new JournalSnapshot(
			Collections.emptyList(),
			viewed,
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 1, 0, 0));
		RenderedSnapshot inactive = renderSnapshotWithCanvas(
			withoutActiveQuest,
			viewport,
			panel,
			new RenderOptions());
		JournalOverlay overlay = rendered.overlay;

		assertNull(findHitPoint(panel, overlay::setActiveQuestIdAt, "COOKS_ASSISTANT"));
		assertNull(findHitPoint(
			panel,
			overlay::returnToActiveQuestIdAt,
			"COOKS_ASSISTANT"));
		JournalGeometry geometry = JournalGeometry.create(panel, viewport);
		ControlsRenderer controlsRenderer =
			rendered.controlsRenderer;
		Rectangle setActiveControl =
			controlsRenderer.setActiveQuestControlBounds(geometry);
		Rectangle activeControl =
			controlsRenderer.activeQuestControlBounds(geometry);
		Rectangle clearControl = ControlsRenderer.clearActiveQuestBounds(
			activeControl,
			snapshot.getActiveQuest());
		Point clearPoint = new Point(
			setActiveControl.x + setActiveControl.width / 2,
			setActiveControl.y + setActiveControl.height / 2);
		Point clearPointOnControl = new Point(
			clearControl.x + clearControl.width / 2,
			clearControl.y + clearControl.height / 2);
		assertNull(overlay.clearActiveQuestIdAt(clearPoint));
		assertEquals("COOKS_ASSISTANT", overlay.clearActiveQuestIdAt(clearPointOnControl));
		Rectangle activeStatus = ControlsRenderer.activeQuestStatusBounds(
			activeControl,
			true);
		assertEquals(
			inactive.canvas.getRGB(activeStatus.x, activeStatus.y),
			rendered.canvas.getRGB(activeStatus.x, activeStatus.y));
		assertFalse(setActiveControl.intersects(clearControl));
	}

	@Test
	public void automaticModeRemovesManualControlsAndReclaimsTheirLayoutSpace()
	{
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		String questId = "COOKS_ASSISTANT";
		JournalSnapshot snapshot = new JournalSnapshot(
			Collections.emptyList(),
			selectedQuest(
				questId,
				"Cook's Assistant",
				JournalSnapshot.QuestState.NOT_STARTED),
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 1, 0, 0));
		AtomicBoolean manualSelection = new AtomicBoolean(true);
		RenderedSnapshot rendered = renderSnapshotWithCanvas(
			snapshot,
			viewport,
			panel,
			new RenderOptions().manualSelection(manualSelection));
		JournalGeometry manualGeometry = JournalGeometry.create(panel, viewport, false, true);
		Rectangle manualActionRow = rendered.controlsRenderer.starQuestControlBounds(manualGeometry);
		Rectangle setControl = rendered.controlsRenderer.setActiveQuestControlBounds(manualGeometry);
		Point setPoint = new Point(
			setControl.x + setControl.width / 2,
			setControl.y + setControl.height / 2);
		assertEquals(questId, rendered.overlay.setActiveQuestIdAt(setPoint));

		manualSelection.set(false);
		assertNull(rendered.overlay.setActiveQuestIdAt(setPoint));
		renderAgain(rendered.overlay, viewport.getSize());

		JournalGeometry automaticGeometry = JournalGeometry.create(
			panel,
			viewport,
			false,
			false);
		Rectangle automaticActionRow =
			rendered.controlsRenderer.starQuestControlBounds(automaticGeometry);
		assertEquals(manualActionRow.y - 30, automaticActionRow.y);
		assertTrue(rendered.controlsRenderer.activeQuestControlBounds(automaticGeometry).isEmpty());
		Rectangle content = new Rectangle(300, 200, 250, 180);
		assertEquals(
			content,
			ControlsRenderer.selectedQuestMainContentBounds(content, false));
		assertEquals(
			content.y + 9,
			ControlsRenderer.selectedQuestMainContentBounds(content, true).y);
	}

	@Test
	public void longQuestNamesWrapIntoTallerRowsInsteadOfEllipsizing()
	{
		Rectangle viewport = new Rectangle(STANDARD_VIEWPORT), panel = new Rectangle(STANDARD_PANEL);
		String id = "LONG_QUEST";
		String title = "The Surprisingly Long and Entirely Unabridged Quest Name of the Forgotten Kingdom";
		JournalSnapshot snapshot = new JournalSnapshot(
			Collections.singletonList(new JournalSnapshot.QuestListItem(
				id,
				title,
				JournalSnapshot.QuestType.QUEST,
				JournalSnapshot.QuestState.NOT_STARTED,
				JournalSnapshot.QuestDifficulty.NOVICE,
				false,
				Collections.emptyMap())),
			null,
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 1, 0, 0));

		JournalOverlay overlay = renderSnapshot(snapshot, viewport, panel);
		Rectangle list = QuestListRenderer.questListScrollBounds(
			JournalGeometry.create(panel, viewport).questListContentBounds());
		int x = list.x + 10;
		int hitHeight = 0;
		for (int y = list.y; y < list.y + list.height; y++)
		{
			if (id.equals(overlay.questIdAt(new Point(x, y))))
			{
				hitHeight++;
			}
		}

		assertTrue(hitHeight > 22);
	}

	private static JournalSnapshot.SelectedQuest selectedQuest(
		String id,
		String title,
		JournalSnapshot.QuestState state)
	{
		return new JournalSnapshot.SelectedQuest(
			new JournalSnapshot.QuestOverview(
				id,
				title,
				JournalSnapshot.QuestType.QUEST,
				state,
				JournalSnapshot.QuestDifficulty.NOVICE,
				false),
			Collections.emptyList(),
			Collections.emptyList(),
			Collections.emptyList(),
			Collections.emptyList(),
			Collections.emptyList(),
			Collections.emptyList());
	}

	private static JournalSnapshot.Objective objective(
		String text,
		JournalSnapshot.ObjectiveState state,
		boolean current)
	{
		return new JournalSnapshot.Objective(
			"", "", text, state, current, Collections.emptyList());
	}

	private static JournalOverlay renderSnapshot(
		JournalSnapshot snapshot,
		Rectangle viewport,
		Rectangle panel)
	{
		return renderSnapshotWithCanvas(
			snapshot, viewport, panel, new RenderOptions()).overlay;
	}

	private static void renderAgain(JournalOverlay overlay, Dimension viewport)
	{
		renderAgainWithCanvas(overlay, viewport);
	}

	private static BufferedImage renderAgainWithCanvas(
		JournalOverlay overlay,
		Dimension viewport)
	{
		BufferedImage canvas = new BufferedImage(
			viewport.width,
			viewport.height,
			BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = canvas.createGraphics();
		try
		{
			Rectangle host = overlay.getBounds();
			graphics.translate(host.x, host.y);
			overlay.render(graphics);
		}
		finally
		{
			graphics.dispose();
		}
		return canvas;
	}

	private static RenderedSnapshot renderSnapshotWithCanvas(
		JournalSnapshot snapshot,
		Rectangle viewport,
		Rectangle panel,
		RenderOptions options)
	{
		SpriteManager spriteManager = options.spriteManager == null
			? mock(SpriteManager.class)
			: options.spriteManager;
		Client client = mock(Client.class);
		QuestJournalManager manager = mock(QuestJournalManager.class);
		QuestJournalManager.JournalPanelRenderState panelState =
			mock(QuestJournalManager.JournalPanelRenderState.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		when(client.getRealDimensions()).thenReturn(viewport.getSize());
		when(manager.isJournalOpen()).thenReturn(true);
		when(manager.getJournalContentBounds(any(Rectangle.class))).thenReturn(viewport);
		when(manager.getJournalRenderState(any(Rectangle.class))).thenReturn(panelState);
		when(panelState.bounds()).thenReturn(panel);
		when(panelState.maximized()).thenReturn(false);
		when(panelState.revision()).thenReturn(9L);
		when(manager.isJournalRenderStateCurrent(9L)).thenReturn(true);
		when(manager.getJournalSnapshot()).thenReturn(snapshot);
		when(manager.getPointerCanvasPoint()).thenAnswer(invocation -> options.pointer.get());
		when(manager.getQuestFilter()).thenReturn(options.filter);
		when(manager.shouldOpenMissingItemWikiLinks()).thenReturn(true);
		when(manager.isManualActiveQuestSelection()).thenAnswer(
			invocation -> options.manualSelection.get());
		when(manager.shouldAlwaysExpandChecklists()).thenAnswer(
			invocation -> options.alwaysExpanded.get());
		when(manager.commitJournalRender(anyLong(), any(Runnable.class))).thenAnswer(invocation ->
		{
			invocation.getArgument(1, Runnable.class).run();
			return true;
		});

		JournalOverlay overlay = new JournalOverlay(
			client,
			null,
			manager,
			spriteManager,
			null,
			null);
		BufferedImage canvas = new BufferedImage(
			viewport.width,
			viewport.height,
			BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = canvas.createGraphics();
		try
		{
			overlay.render(graphics);
		}
		finally
		{
			graphics.dispose();
		}
		ChromeRenderer chromeRenderer =
			new ChromeRenderer(overlay, spriteManager);
		FilterRenderer filterRenderer =
			new FilterRenderer(
				overlay,
				manager,
				chromeRenderer);
		ControlsRenderer controlsRenderer =
			new ControlsRenderer(
				overlay,
				manager,
				filterRenderer,
				chromeRenderer);
		return new RenderedSnapshot(
			overlay,
			canvas,
			controlsRenderer,
			filterRenderer);
	}

	private static void assertStatusChecklistMarkers(
		BufferedImage canvas,
		Rectangle popup,
		Set<JournalSnapshot.QuestState> selectedStates)
	{
		int accent = FilterRenderer.filterActiveOptionMarkerColor().getRGB();
		int expectedLabelX = -1;
		JournalSnapshot.QuestState[] states = JournalSnapshot.QuestState.values();
		for (int index = 0; index < states.length; index++)
		{
			Rectangle row = FilterRenderer.filterPopupOptionRowBounds(
				popup,
				JournalOverlay.FilterControl.STATUS,
				index);
			Rectangle marker = FilterRenderer.filterOptionMarkerBounds(row);
			int labelX = FilterRenderer.filterOptionLabelX(marker);
			if (expectedLabelX < 0)
			{
				expectedLabelX = labelX;
			}
			assertEquals(expectedLabelX, labelX);

			int rowBackground = canvas.getRGB(
				row.x + row.width - 2,
				row.y + row.height / 2);
			if (selectedStates.contains(states[index]))
			{
				assertTrue(countPixelsDifferentFrom(canvas, marker, rowBackground) > 0);
				assertTrue(countPixelsEqualTo(canvas, marker, accent) > 0);
				assertRectanglePerimeterDoesNotContain(canvas, row, accent);
				if (states[index] == JournalSnapshot.QuestState.IN_PROGRESS)
				{
					assertEquals(accent, canvas.getRGB(marker.x, marker.y + 5));
					assertEquals(accent, canvas.getRGB(marker.x + 8, marker.y + 1));
				}
			}
			else
			{
				assertEquals(0, countPixelsDifferentFrom(canvas, marker, rowBackground));
				assertEquals(0, countPixelsEqualTo(canvas, marker, accent));
			}
		}
	}

	private static int countPixelsDifferentFrom(
		BufferedImage image,
		Rectangle bounds,
		int color)
	{
		int count = 0;
		for (int y = bounds.y; y < bounds.y + bounds.height; y++)
		{
			for (int x = bounds.x; x < bounds.x + bounds.width; x++)
			{
				if (image.getRGB(x, y) != color)
				{
					count++;
				}
			}
		}
		return count;
	}

	private static int countPixelsEqualTo(
		BufferedImage image,
		Rectangle bounds,
		int color)
	{
		int count = 0;
		for (int y = bounds.y; y < bounds.y + bounds.height; y++)
		{
			for (int x = bounds.x; x < bounds.x + bounds.width; x++)
			{
				if (image.getRGB(x, y) == color)
				{
					count++;
				}
			}
		}
		return count;
	}

	private static int compositeColor(Color background, Color overlay)
	{
		BufferedImage pixel = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = pixel.createGraphics();
		try
		{
			graphics.setColor(background);
			graphics.fillRect(0, 0, 1, 1);
			graphics.setColor(overlay);
			graphics.fillRect(0, 0, 1, 1);
		}
		finally
		{
			graphics.dispose();
		}
		return pixel.getRGB(0, 0);
	}

	private static void assertRectanglePerimeterDoesNotContain(
		BufferedImage image,
		Rectangle bounds,
		int color)
	{
		for (int x = bounds.x; x < bounds.x + bounds.width; x++)
		{
			assertNotEquals(color, image.getRGB(x, bounds.y));
			assertNotEquals(color, image.getRGB(x, bounds.y + bounds.height - 1));
		}
		for (int y = bounds.y + 1; y < bounds.y + bounds.height - 1; y++)
		{
			assertNotEquals(color, image.getRGB(bounds.x, y));
			assertNotEquals(color, image.getRGB(bounds.x + bounds.width - 1, y));
		}
	}

	private static Point findHitPoint(
		Rectangle bounds,
		Function<Point, String> hitLookup,
		String expectedId)
	{
		for (int y = bounds.y; y < bounds.y + bounds.height; y++)
		{
			for (int x = bounds.x; x < bounds.x + bounds.width; x++)
			{
				Point point = new Point(x, y);
				if (expectedId.equals(hitLookup.apply(point)))
				{
					return point;
				}
			}
		}
		return null;
	}

	private static Rectangle findHitBounds(
		Rectangle bounds,
		Function<Point, String> hitLookup,
		String expectedId)
	{
		int minimumX = bounds.x + bounds.width;
		int minimumY = bounds.y + bounds.height;
		int maximumX = -1;
		int maximumY = -1;
		for (int y = bounds.y; y < bounds.y + bounds.height; y++)
		{
			for (int x = bounds.x; x < bounds.x + bounds.width; x++)
			{
				if (expectedId.equals(hitLookup.apply(new Point(x, y))))
				{
					minimumX = Math.min(minimumX, x);
					minimumY = Math.min(minimumY, y);
					maximumX = Math.max(maximumX, x);
					maximumY = Math.max(maximumY, y);
				}
			}
		}
		return maximumX < minimumX || maximumY < minimumY
			? new Rectangle()
			: new Rectangle(
				minimumX,
				minimumY,
				maximumX - minimumX + 1,
				maximumY - minimumY + 1);
	}

	private static Point findScrollbarPoint(
		JournalOverlay overlay,
		Rectangle bounds,
		JournalOverlay.ScrollRegion expectedRegion)
	{
		for (int y = bounds.y; y < bounds.y + bounds.height; y++)
		{
			for (int x = bounds.x; x < bounds.x + bounds.width; x++)
			{
				Point point = new Point(x, y);
				JournalOverlay.ScrollbarInteraction interaction =
					overlay.scrollbarInteractionAt(point);
				if (interaction != null && interaction.region() == expectedRegion)
				{
					return point;
				}
			}
		}
		return null;
	}

	private static JournalPanelAssets headerControlAssets(
		BufferedImage closeIcon,
		BufferedImage closeIconHovered)
	{
		return headerControlAssets(closeIcon, closeIconHovered, null, null);
	}

	private static JournalPanelAssets headerControlAssets(
		BufferedImage closeIcon,
		BufferedImage closeIconHovered,
		BufferedImage settingsButton,
		BufferedImage settingsButtonHovered)
	{
		SpriteManager spriteManager = mock(SpriteManager.class);
		when(spriteManager.getSprite(SpriteID.CloseButtons._14, 0)).thenReturn(closeIcon);
		when(spriteManager.getSprite(SpriteID.CloseButtons._15, 0)).thenReturn(closeIconHovered);
		when(spriteManager.getSprite(SpriteID.MenuButtons._10, 0)).thenReturn(settingsButton);
		when(spriteManager.getSprite(SpriteID.MenuButtons._11, 0)).thenReturn(settingsButtonHovered);
		JournalPanelAssets assets = JournalPanelAssets.load(spriteManager);
		assertSame(closeIcon, assets.closeIcon);
		assertSame(closeIconHovered, assets.closeIconHovered);
		assertSame(settingsButton, assets.settingsButton);
		assertSame(settingsButtonHovered, assets.settingsButtonHovered);
		return assets;
	}

	private static FilterRenderer testFilterRenderer(
		JournalOverlay overlay)
	{
		ChromeRenderer chromeRenderer =
			new ChromeRenderer(overlay, null);
		return new FilterRenderer(
			overlay,
			null,
			chromeRenderer);
	}

	private static ControlsRenderer testControlsRenderer(
		JournalOverlay overlay)
	{
		ChromeRenderer chromeRenderer =
			new ChromeRenderer(overlay, null);
		FilterRenderer filterRenderer =
			new FilterRenderer(
				overlay,
				null,
				chromeRenderer);
		return new ControlsRenderer(
			overlay,
			null,
			filterRenderer,
			chromeRenderer);
	}

	private static BufferedImage renderHeaderControl(
		JournalOverlay overlay,
		JournalOverlay.HeaderControlIcon icon,
		Point pointer,
		JournalPanelAssets assets)
	{
		BufferedImage image = new BufferedImage(21, 21, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		try
		{
			ChromeRenderer chromeRenderer =
				new ChromeRenderer(overlay, null);
			chromeRenderer.drawHeaderControl(
				graphics,
				new Rectangle(0, 0, 21, 21),
				pointer,
				icon,
				assets);
		}
		finally
		{
			graphics.dispose();
		}
		return image;
	}

	private static BufferedImage renderClearActiveQuestControl(
		JournalOverlay overlay,
		Point pointer,
		JournalPanelAssets assets)
	{
		BufferedImage image = new BufferedImage(26, 26, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		try
		{
			ChromeRenderer chromeRenderer =
				new ChromeRenderer(overlay, null);
			FilterRenderer filterRenderer =
				new FilterRenderer(
					overlay,
					null,
					chromeRenderer);
			ControlsRenderer controlsRenderer =
				new ControlsRenderer(
					overlay,
					null,
					filterRenderer,
					chromeRenderer);
			controlsRenderer.drawClearActiveQuestControl(
				graphics,
				new Rectangle(0, 0, 26, 26),
				pointer,
				assets);
		}
		finally
		{
			graphics.dispose();
		}
		return image;
	}

	private static BufferedImage renderFilterControl(boolean hovered, boolean open)
	{
		BufferedImage image = new BufferedImage(180, 22, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		try
		{
			JournalOverlay overlay = new JournalOverlay(null, null, null, null, null, null);
			testFilterRenderer(overlay).drawFilterControl(
				graphics,
				new Rectangle(),
				new Rectangle(0, 0, image.getWidth(), image.getHeight()),
				"",
				"All",
				hovered,
				open);
		}
		finally
		{
			graphics.dispose();
		}
		return image;
	}

	private static void assertOnePixelBlackBorder(
		BufferedImage image,
		Rectangle bounds,
		int sideSampleY)
	{
		int black = JournalOverlay.SCROLL_BEZEL_DARK.getRGB();
		int centerX = bounds.x + bounds.width / 2;
		assertEquals(black, image.getRGB(bounds.x, sideSampleY));
		assertNotEquals(black, image.getRGB(bounds.x + 1, sideSampleY));
		assertEquals(black, image.getRGB(bounds.x + bounds.width - 1, sideSampleY));
		assertNotEquals(black, image.getRGB(bounds.x + bounds.width - 2, sideSampleY));
		assertEquals(black, image.getRGB(centerX, bounds.y));
		assertNotEquals(black, image.getRGB(centerX, bounds.y + 1));
		assertEquals(black, image.getRGB(centerX, bounds.y + bounds.height - 1));
		assertNotEquals(black, image.getRGB(centerX, bounds.y + bounds.height - 2));
	}

	private static void assertInvertedControlBevel(
		BufferedImage normal,
		BufferedImage hovered,
		Rectangle bounds)
	{
		Rectangle shadow = JournalOverlay.headerControlShadowBounds(bounds);
		Rectangle face = JournalOverlay.headerControlFaceBounds(bounds);
		int shadowX = shadow.x + shadow.width / 2;
		int shadowY = shadow.y + shadow.height / 2;
		int faceX = face.x + face.width / 2;
		int faceY = face.y + face.height / 2;

		assertNotEquals(normal.getRGB(shadowX, shadow.y), hovered.getRGB(shadowX, shadow.y));
		assertEquals(
			normal.getRGB(shadowX, shadow.y),
			hovered.getRGB(shadowX, shadow.y + shadow.height - 1));
		assertEquals(
			normal.getRGB(shadow.x + shadow.width - 1, shadowY),
			hovered.getRGB(shadow.x, shadowY));
		assertNotEquals(normal.getRGB(faceX, face.y), hovered.getRGB(faceX, face.y));
		assertEquals(
			normal.getRGB(faceX, face.y),
			hovered.getRGB(faceX, face.y + face.height - 1));
		assertEquals(
			normal.getRGB(face.x + face.width - 1, faceY),
			hovered.getRGB(face.x, faceY));
	}

	private static void assertOutsetControlBevel(BufferedImage image, Rectangle bounds)
	{
		Rectangle shadow = JournalOverlay.headerControlShadowBounds(bounds);
		Rectangle face = JournalOverlay.headerControlFaceBounds(bounds);
		assertEquals(new Color(0x00, 0x00, 0x01).getRGB(), image.getRGB(bounds.x, bounds.y));
		assertEquals(
			new Color(0xAA, 0x93, 0x68).getRGB(),
			image.getRGB(shadow.x + shadow.width / 2, shadow.y));
		assertEquals(
			new Color(0x2B, 0x2B, 0x29).getRGB(),
			image.getRGB(
				shadow.x + shadow.width / 2,
				shadow.y + shadow.height - 1));
		assertEquals(
			new Color(0x8A, 0x78, 0x5B).getRGB(),
			image.getRGB(face.x + face.width / 2, face.y));
		assertEquals(
			new Color(0x44, 0x3B, 0x2C).getRGB(),
			image.getRGB(
				face.x + face.width / 2,
				face.y + face.height - 1));
	}

	private static JournalSnapshot.Requirement requirementWithHelp(
		JournalSnapshot.RequirementState state,
		List<JournalSnapshot.ItemLocation> locations,
		String helpText)
	{
		return new JournalSnapshot.Requirement(
			"Bronze axe",
			state,
			null,
			locations,
			"",
			"",
			JournalSnapshot.IconIdentity.item(1351, 1),
			"https://oldschool.runescape.wiki/w/Special:Lookup?type=item&id=1351#Item_sources",
			helpText);
	}

	private static BufferedImage solidImage(int width, int height, Color color)
	{
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		try
		{
			graphics.setColor(color);
			graphics.fillRect(0, 0, width, height);
		}
		finally
		{
			graphics.dispose();
		}
		return image;
	}

	private static final class TrackingBufferedImage extends BufferedImage
	{
		private boolean flushed;

		private TrackingBufferedImage()
		{
			super(1, 1, BufferedImage.TYPE_INT_ARGB);
		}

		@Override
		public void flush()
		{
			flushed = true;
			super.flush();
		}
	}

	private static final class RenderOptions
	{
		private AtomicBoolean alwaysExpanded = new AtomicBoolean();
		private AtomicReference<Point> pointer =
			new AtomicReference<>(new Point(-1, -1));
		private SpriteManager spriteManager;
		private AtomicBoolean manualSelection = new AtomicBoolean(true);
		private JournalSnapshot.QuestFilter filter = JournalSnapshot.QuestFilter.all();

		private RenderOptions alwaysExpanded(AtomicBoolean alwaysExpanded)
		{
			this.alwaysExpanded = alwaysExpanded;
			return this;
		}

		private RenderOptions pointer(AtomicReference<Point> pointer)
		{
			this.pointer = pointer;
			return this;
		}

		private RenderOptions sprites(SpriteManager spriteManager)
		{
			this.spriteManager = spriteManager;
			return this;
		}

		private RenderOptions manualSelection(AtomicBoolean manualSelection)
		{
			this.manualSelection = manualSelection;
			return this;
		}

		private RenderOptions filter(JournalSnapshot.QuestFilter filter)
		{
			this.filter = filter;
			return this;
		}
	}

	private static final class RenderedSnapshot
	{
		private final JournalOverlay overlay;
		private final BufferedImage canvas;
		private final ControlsRenderer controlsRenderer;
		private final FilterRenderer filterRenderer;

		private RenderedSnapshot(
			JournalOverlay overlay,
			BufferedImage canvas,
			ControlsRenderer controlsRenderer,
			FilterRenderer filterRenderer)
		{
			this.overlay = overlay;
			this.canvas = canvas;
			this.controlsRenderer = controlsRenderer;
			this.filterRenderer = filterRenderer;
		}
	}
}
