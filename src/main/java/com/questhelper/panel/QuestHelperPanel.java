/*
 * Copyright (c) 2020, Zoinkwiz
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
package com.questhelper.panel;

import com.questhelper.questhelpers.QuestHelper;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicButtonUI;
import lombok.extern.slf4j.Slf4j;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.steps.QuestStep;
import net.runelite.client.Notifier;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;
import net.runelite.client.util.SwingUtil;
import net.runelite.client.util.Text;

@Slf4j
public
class QuestHelperPanel extends PluginPanel
{
	private final QuestOverviewPanel questOverviewPanel;
	private final FixedWidthPanel questOverviewWrapper = new FixedWidthPanel();

	private final JPanel questPromptPanel = new JPanel();
	private final JPanel allQuestsCompletedPanel = new JPanel();

	private final IconTextField searchBar = new IconTextField();
	private final FixedWidthPanel questListPanel = new FixedWidthPanel();
	private final FixedWidthPanel questListWrapper = new FixedWidthPanel();
	private final JScrollPane scrollableContainer;

	private final ArrayList<QuestSelectPanel> questSelectPanels = new ArrayList<>();

	QuestHelperPlugin questHelperPlugin;

	private static final ImageIcon DISCORD_ICON;

	static
	{
		final BufferedImage discordImage = ImageUtil.getResourceStreamFromClass(QuestHelperPlugin.class, "/discord.png");
		final BufferedImage scaledImage = ImageUtil.resizeImage(discordImage, 16, 16);
		DISCORD_ICON = new ImageIcon(scaledImage);
	}

	public QuestHelperPanel(QuestHelperPlugin questHelperPlugin, Notifier notifier)
	{
		super(false);

		this.questHelperPlugin = questHelperPlugin;

		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setLayout(new BorderLayout());

		/* Setup overview panel */
		JPanel titlePanel = new JPanel();
		titlePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		titlePanel.setLayout(new BorderLayout());

		JLabel title = new JLabel();
		title.setText("Quest Helper");
		title.setForeground(Color.WHITE);
		titlePanel.add(title, BorderLayout.WEST);

		JButton discordBtn = new JButton();
		SwingUtil.removeButtonDecorations(discordBtn);
		discordBtn.setIcon(DISCORD_ICON);
		discordBtn.setToolTipText("Get help with the Quest Helper or make suggestions on Discord");
		discordBtn.setBackground(ColorScheme.DARK_GRAY_COLOR);
		discordBtn.setUI(new BasicButtonUI());
		discordBtn.addActionListener((ev) -> LinkBrowser.browse("https://discord.gg/XCfwNnz6RB"));
		discordBtn.addMouseListener(new java.awt.event.MouseAdapter()
		{
			public void mouseEntered(java.awt.event.MouseEvent evt)
			{
				discordBtn.setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);
			}

			public void mouseExited(java.awt.event.MouseEvent evt)
			{
				discordBtn.setBackground(ColorScheme.DARK_GRAY_COLOR);
			}
		});
		titlePanel.add(discordBtn, BorderLayout.EAST);

		JLabel questPromptLabel = new JLabel();
		questPromptLabel.setForeground(Color.GRAY);
		questPromptLabel.setText("<html><body style = 'text-align:left'>Select a quest to begin. You'll need to be logged in to see the quest list. Note that not all quests are currently supported.</body></html>");

		questPromptPanel.setLayout(new BorderLayout());
		questPromptPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		questPromptPanel.add(questPromptLabel);

		JLabel questsCompletedLabel = new JLabel();
		questsCompletedLabel.setForeground(Color.GRAY);
		questsCompletedLabel.setText("<html><body style = 'text-align:left'>You've completed all the quests that are currently supported!</body></html>");

		allQuestsCompletedPanel.setLayout(new BorderLayout());
		allQuestsCompletedPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		allQuestsCompletedPanel.add(questsCompletedLabel);
		allQuestsCompletedPanel.setVisible(false);

		/* Search bar */
		searchBar.setIcon(IconTextField.Icon.SEARCH);
		searchBar.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
		searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
		searchBar.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				onSearchBarChanged();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				onSearchBarChanged();
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				onSearchBarChanged();
			}
		});

		JPanel searchQuestsPanel = new JPanel();
		searchQuestsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		searchQuestsPanel.setLayout(new BorderLayout(0, BORDER_OFFSET));
		searchQuestsPanel.add(searchBar, BorderLayout.CENTER);
		searchQuestsPanel.add(allQuestsCompletedPanel, BorderLayout.SOUTH);

		questListPanel.setBorder(new EmptyBorder(8, 10, 10, 10));
		questListPanel.setLayout(new DynamicGridLayout(0, 1, 0, 5));
		questListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		showMatchingQuests("");

		questListWrapper.setLayout(new BorderLayout());
		questListWrapper.add(questListPanel, BorderLayout.NORTH);

		scrollableContainer = new JScrollPane(questListWrapper);
		scrollableContainer.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		JPanel introDetailsPanel = new JPanel();
		introDetailsPanel.setLayout(new BorderLayout());
		introDetailsPanel.add(titlePanel, BorderLayout.NORTH);
		introDetailsPanel.add(questPromptPanel, BorderLayout.CENTER);
		introDetailsPanel.add(searchQuestsPanel, BorderLayout.SOUTH);

		add(introDetailsPanel, BorderLayout.NORTH);
		add(scrollableContainer, BorderLayout.CENTER);

		/* Layout */
		questOverviewPanel = new QuestOverviewPanel(questHelperPlugin, notifier);

		questOverviewWrapper.setLayout(new BorderLayout());
		questOverviewWrapper.add(questOverviewPanel, BorderLayout.NORTH);
	}

	private void onSearchBarChanged()
	{
		final String text = searchBar.getText();

		if ((questOverviewPanel.currentQuest == null || !text.isEmpty()))
		{
			scrollableContainer.setViewportView(questListWrapper);
			questSelectPanels.forEach(questListPanel::remove);
			showMatchingQuests(text);
		}
		else
		{
			scrollableContainer.setViewportView(questOverviewWrapper);
		}
		revalidate();
	}

	private void showMatchingQuests(String text)
	{
		if (text.isEmpty())
		{
			questSelectPanels.forEach(questListPanel::add);
			return;
		}

		final String[] searchTerms = text.toLowerCase().split(" ");
		questSelectPanels.forEach(listItem ->
		{
			if (Text.matchesSearchTerms(Arrays.asList(searchTerms), listItem.getKeywords()))
			{
				questListPanel.add(listItem);
			}
		});
	}

	public void refresh(List<QuestHelper> helpers, boolean loggedOut)
	{
		questSelectPanels.forEach(questListPanel::remove);
		questSelectPanels.clear();
		for (QuestHelper questHelper : helpers)
		{
			questSelectPanels.add(new QuestSelectPanel(questHelperPlugin, this, questHelper));
		}
		questSelectPanels.sort(Comparator.comparing(p -> p.getQuestHelper().getQuest().getName()));

		if (loggedOut)
		{
			allQuestsCompletedPanel.setVisible(false);
		}
		else
		{
			allQuestsCompletedPanel.setVisible(questSelectPanels.isEmpty());
		}

		repaint();
		revalidate();
		showMatchingQuests("");
	}

	public void addQuest(QuestHelper quest, boolean isActive)
	{
		questPromptPanel.setVisible(false);
		scrollableContainer.setViewportView(questOverviewWrapper);

		questOverviewPanel.addQuest(quest, isActive);

		repaint();
		revalidate();
	}

	public void updateSteps()
	{
		questOverviewPanel.updateSteps();
	}

	public void updateHighlight(QuestStep newStep)
	{
		questOverviewPanel.updateHighlight(newStep);

		repaint();
		revalidate();
	}

	public void updateLocks()
	{
		questOverviewPanel.updateLocks();

		repaint();
		revalidate();
	}

	public void removeQuest()
	{
		questPromptPanel.setVisible(true);
		scrollableContainer.setViewportView(questListWrapper);
		questOverviewPanel.removeQuest();

		repaint();
		revalidate();
	}

	public void emptyBar()
	{
		searchBar.setText("");
	}
}
