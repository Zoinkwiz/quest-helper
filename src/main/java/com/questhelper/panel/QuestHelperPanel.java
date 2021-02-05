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

import com.questhelper.BankItems;
import com.questhelper.Icon;
import com.questhelper.QuestHelperConfig;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.QuestHelperQuest;
import com.questhelper.questhelpers.Quest;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.QuestStep;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicButtonUI;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.QuestState;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;
import net.runelite.client.util.SwingUtil;
import net.runelite.client.util.Text;

@Slf4j
public class QuestHelperPanel extends PluginPanel
{
	private final QuestOverviewPanel questOverviewPanel;
	private final FixedWidthPanel questOverviewWrapper = new FixedWidthPanel();

	private final JPanel allQuestsCompletedPanel = new JPanel();

	private final JPanel allDropdownSections = new JPanel();
	private final JComboBox<Enum> filterDropdown, difficultyDropdown, orderDropdown;

	private final IconTextField searchBar = new IconTextField();
	private final FixedWidthPanel questListPanel = new FixedWidthPanel();
	private final FixedWidthPanel questListWrapper = new FixedWidthPanel();
	private final JScrollPane scrollableContainer;
	private final int DROPDOWN_HEIGHT = 20;


	private final ArrayList<QuestSelectPanel> questSelectPanels = new ArrayList<>();

	QuestHelperPlugin questHelperPlugin;

	private static final ImageIcon DISCORD_ICON;

	static
	{
		DISCORD_ICON = Icon.DISCORD.getIcon(img -> ImageUtil.resizeImage(img, 16, 16));
	}

	public QuestHelperPanel(QuestHelperPlugin questHelperPlugin)
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

		JLabel questsCompletedLabel = new JLabel();
		questsCompletedLabel.setForeground(Color.GRAY);
		questsCompletedLabel.setText("<html><body style='text-align:left'>Please log in to see available quests" +
			". Note that not all quests are available in the Quest Helper yet.</body></html>");

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

		questListPanel.setBorder(new EmptyBorder(8, 10, 0, 10));
		questListPanel.setLayout(new DynamicGridLayout(0, 1, 0, 5));
		questListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		showMatchingQuests("");

		// Filters
		filterDropdown = makeNewDropdown(QuestHelperConfig.QuestFilter.values(), "filterListBy");
		JPanel filtersPanel = makeDropdownPanel(filterDropdown, "Filters");
		filtersPanel.setPreferredSize(new Dimension(PANEL_WIDTH, DROPDOWN_HEIGHT));

		difficultyDropdown = makeNewDropdown(Quest.Difficulty.values(), "questDifficulty");
		JPanel difficultyPanel = makeDropdownPanel(difficultyDropdown, "Difficulty");
		difficultyPanel.setPreferredSize(new Dimension(PANEL_WIDTH, DROPDOWN_HEIGHT));

		orderDropdown = makeNewDropdown(QuestHelperConfig.QuestOrdering.values(), "orderListBy");
		JPanel orderPanel = makeDropdownPanel(orderDropdown, "Ordering");
		orderPanel.setPreferredSize(new Dimension(PANEL_WIDTH, DROPDOWN_HEIGHT));

		allDropdownSections.setBorder(new EmptyBorder(0, 0, 10, 0));
		allDropdownSections.setLayout(new BorderLayout(0, BORDER_OFFSET));
		allDropdownSections.add(filtersPanel, BorderLayout.NORTH);
		allDropdownSections.add(difficultyPanel, BorderLayout.CENTER);
		allDropdownSections.add(orderPanel, BorderLayout.SOUTH);

		searchQuestsPanel.add(allDropdownSections, BorderLayout.NORTH);

		// Wrapper
		questListWrapper.setLayout(new BorderLayout());
		questListWrapper.add(questListPanel, BorderLayout.NORTH);

		scrollableContainer = new JScrollPane(questListWrapper);
		scrollableContainer.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		JPanel introDetailsPanel = new JPanel();
		introDetailsPanel.setLayout(new BorderLayout());
		introDetailsPanel.add(titlePanel, BorderLayout.NORTH);
		introDetailsPanel.add(searchQuestsPanel, BorderLayout.CENTER);

		add(introDetailsPanel, BorderLayout.NORTH);
		add(scrollableContainer, BorderLayout.CENTER);

		/* Layout */
		questOverviewPanel = new QuestOverviewPanel(questHelperPlugin);

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

	private JComboBox<Enum> makeNewDropdown(Enum[] values, String key)
	{
		JComboBox<Enum> dropdown = new JComboBox<>(values);
		dropdown.setFocusable(false);
		dropdown.setForeground(Color.WHITE);
		dropdown.setRenderer(new DropdownRenderer());
		dropdown.addItemListener(e ->
		{
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				Enum source = (Enum) e.getItem();
				questHelperPlugin.getConfigManager().setConfiguration("questhelper", key,
					source);
			}
		});

		return dropdown;
	}

	private JPanel makeDropdownPanel(JComboBox dropdown, String name)
	{
		// Filters
		JLabel filterName = new JLabel(name);
		filterName.setForeground(Color.WHITE);

		JPanel filtersPanel = new JPanel();
		filtersPanel.setLayout(new BorderLayout());
		filtersPanel.setMinimumSize(new Dimension(PANEL_WIDTH, 0));
		filtersPanel.add(filterName, BorderLayout.CENTER);
		filtersPanel.add(dropdown, BorderLayout.EAST);

		return filtersPanel;
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

	public void refresh(List<QuestHelper> questHelpers, boolean loggedOut, Map<QuestHelperQuest, QuestState> completedQuests)
	{
		questSelectPanels.forEach(questListPanel::remove);
		questSelectPanels.clear();

		filterDropdown.setSelectedItem(questHelperPlugin.getConfig().filterListBy());
		difficultyDropdown.setSelectedItem(questHelperPlugin.getConfig().difficulty());
		orderDropdown.setSelectedItem(questHelperPlugin.getConfig().orderListBy());

		for (QuestHelper questHelper : questHelpers)
		{
			QuestState questState = completedQuests.getOrDefault(questHelper.getQuest(), QuestState.NOT_STARTED);
			questSelectPanels.add(new QuestSelectPanel(questHelperPlugin, this, questHelper, questState));
		}


		Set<QuestHelperQuest> quests = completedQuests.keySet();
		boolean hasMoreQuests = quests.stream().anyMatch(q -> completedQuests.get(q) != QuestState.FINISHED);
		if (questSelectPanels.isEmpty() && hasMoreQuests)
		{
			allQuestsCompletedPanel.removeAll();
			JLabel noMatch = new JLabel();
			noMatch.setForeground(Color.GRAY);
			if (loggedOut)
			{
				noMatch.setText("<html><body style='text-align:left'>Log in to see available quests</body></html>");
			}
			else
			{
				noMatch.setText("<html><body style='text-align:left'>No quests are available that match your current filters</body></html>");
			}
			allQuestsCompletedPanel.add(noMatch);
		}
		allQuestsCompletedPanel.setVisible(questSelectPanels.isEmpty());

		repaint();
		revalidate();
		showMatchingQuests(searchBar.getText() != null ? searchBar.getText() : "");
	}

	public void addQuest(QuestHelper quest, boolean isActive)
	{
		allDropdownSections.setVisible(false);
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
		allDropdownSections.setVisible(true);
		scrollableContainer.setViewportView(questListWrapper);
		questOverviewPanel.removeQuest();

		repaint();
		revalidate();
	}

	public void emptyBar()
	{
		searchBar.setText("");
	}

	public void updateItemRequirements(Client client, BankItems bankItems)
	{
		questOverviewPanel.updateRequirements(client, bankItems);
	}
}
