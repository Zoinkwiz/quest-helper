/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
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

import com.questhelper.QuestHelperConfig;
import com.questhelper.config.AssistanceLevel;
import com.questhelper.questhelpers.QuestHelper;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import static com.questhelper.config.AssistanceLevel.ASSISTANCE_LEVEL_KEY;

public class AssistLevelPanel extends JPanel
{
	private final FixedWidthPanel mainPanel;
	private ConfigManager configManager;
	private QuestHelperPanel questHelperPanel;

	private final Color ACTIVE_COLOUR = ColorScheme.BRAND_ORANGE;
	private final Color INACTIVE_COLOUR = ColorScheme.DARKER_GRAY_COLOR;

	private JButton[] buttons = new JButton[3];
	
	public AssistLevelPanel()
	{
		super(false);

		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		// Title
		JPanel titlePanel = new FixedWidthPanel();
		titlePanel.setLayout(new BorderLayout());

		JTextArea title = JGenerator.makeJTextArea("Choose Assist Level");
		title.setForeground(Color.WHITE);
		title.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));

		titlePanel.add(title, BorderLayout.CENTER);

		// Main content
		mainPanel = new FixedWidthPanel();
		mainPanel.setBorder(new EmptyBorder(8, 10, 10, 10));
		mainPanel.setLayout(new DynamicGridLayout(0, 1, 0, 5));
		mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.add(titlePanel, BorderLayout.NORTH);

		JPanel northPanel = new FixedWidthPanel();
		northPanel.setLayout(new BorderLayout());
		northPanel.add(titlePanel, BorderLayout.NORTH);
		northPanel.add(mainPanel, BorderLayout.CENTER);

		JScrollPane scrollPane = new JScrollPane(northPanel);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);
	}

	public void rebuild(QuestHelper questHelper, ConfigManager configManager, QuestHelperPanel questHelperPanel)
	{
		this.configManager = configManager;
		this.questHelperPanel = questHelperPanel;

		mainPanel.removeAll();

		mainPanel.add(createFullAssistPanel(questHelper), BorderLayout.NORTH);
		mainPanel.add(createMediumAssistPanel(questHelper), BorderLayout.CENTER);
		mainPanel.add(createMinimumAssistPanel(questHelper), BorderLayout.SOUTH);
	}


	private JPanel createMinimumAssistPanel(QuestHelper questHelper)
	{
		JPanel minAssistPanel = new JPanel();
		minAssistPanel.setLayout(new BorderLayout());

		JTextArea title = JGenerator.makeJTextArea();
		title.setText("Minimum Assistance");
		title.setForeground(Color.WHITE);
		title.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));

		JTextArea description = JGenerator.makeJTextArea();
		description.setText("This disables most overlay-based help, and hides puzzle solutions. This is a good option if you just want the sidebar with helpful information, " +
				"as it will still track where you are in the quest, and show items needed.");
		description.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JButton select = new JButton();
		select.setText("Use minimum assistance");
		select.setPreferredSize(new Dimension(200, 50));
		select.addActionListener(ev -> {
			AssistanceLevel.setToMinimumAssistance(configManager);
			configManager.setConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, "selected-assist-level", "true");
			questHelperPanel.setSelectedQuest(questHelper);
			highlightPanel(select);
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(select);

		minAssistPanel.add(title, BorderLayout.NORTH);
		minAssistPanel.add(description, BorderLayout.CENTER);
		minAssistPanel.add(buttonPanel, BorderLayout.SOUTH);

		select.setBackground(getButtonColor("minimum"));

		buttons[0] = select;

		return minAssistPanel;
	}

	private JPanel createMediumAssistPanel(QuestHelper questHelper)
	{
		JPanel medAssistPanel = new JPanel();
		medAssistPanel.setLayout(new BorderLayout());

		JTextArea title = JGenerator.makeJTextArea("Medium Assistance");
		title.setForeground(Color.WHITE);
		title.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));

		JTextArea description = JGenerator.makeJTextArea("This enables most overlay-based help, but hides puzzle solutions. " +
				"This is a good option if you want to still have guidance, but still solve certain parts for yourself.");
		description.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JButton select = new JButton();
		select.setText("Use medium assistance");
		select.setPreferredSize(new Dimension(200, 50));
		select.addActionListener(ev -> {
			AssistanceLevel.setToMediumAssistance(configManager);
			configManager.setConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, "selected-assist-level", "true");
			questHelperPanel.setSelectedQuest(questHelper);
			highlightPanel(select);
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(select);

		medAssistPanel.add(title, BorderLayout.NORTH);
		medAssistPanel.add(description, BorderLayout.CENTER);
		medAssistPanel.add(buttonPanel, BorderLayout.SOUTH);

		select.setBackground(getButtonColor("medium"));

		buttons[1] = select;

		return medAssistPanel;
	}

	private JPanel createFullAssistPanel(QuestHelper questHelper)
	{
		JPanel fullAssistPanel = new JPanel();
		fullAssistPanel.setLayout(new BorderLayout());

		JTextArea title = JGenerator.makeJTextArea("Full Assistance");
		title.setForeground(Color.WHITE);
		title.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));

		JTextArea description = JGenerator.makeJTextArea("Gives you the full helper experience, highlighting objects & dialogue options, puzzler solvers, and " +
				"with arrows so you know where to go.");
		description.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JButton select = new JButton();
		select.setText("Use full assistance");
		select.setPreferredSize(new Dimension(200, 50));
		select.addActionListener(ev -> {
			AssistanceLevel.setToFullAssistance(configManager);
			configManager.setConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, "selected-assist-level", "true");
			questHelperPanel.setSelectedQuest(questHelper);

			highlightPanel(select);
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(select);

		fullAssistPanel.add(title, BorderLayout.NORTH);
		fullAssistPanel.add(description, BorderLayout.CENTER);
		fullAssistPanel.add(buttonPanel, BorderLayout.SOUTH);

		select.setBackground(getButtonColor("full"));

		buttons[2] = select;

		return fullAssistPanel;
	}

	private Color getButtonColor(String assistLevel)
	{
		String assistLevelActive = configManager.getConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, ASSISTANCE_LEVEL_KEY);

		if (assistLevel.equals(assistLevelActive))
		{
			return ACTIVE_COLOUR;
		}

		return INACTIVE_COLOUR;
	}


	private void highlightPanel(JButton highlightButton)
	{
		for (JButton jButton : buttons)
		{
			if (jButton == highlightButton)
			{
				jButton.setBackground(ACTIVE_COLOUR);
			}
			else
			{
				jButton.setBackground(INACTIVE_COLOUR);
			}
		}
	}
}
