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

import com.questhelper.Icon;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lombok.Getter;
import net.runelite.api.QuestState;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

public class QuestSelectPanel extends JPanel
{
	@Getter
	private final List<String> keywords = new ArrayList<>();

	@Getter
	private final QuestHelper questHelper;

	private static final ImageIcon START_ICON = Icon.START.getIcon();

	public QuestSelectPanel(QuestHelperPlugin questHelperPlugin, QuestHelperPanel questHelperPanel, QuestHelper questHelper, QuestState questState)
	{
		this.questHelper = questHelper;

		keywords.addAll(questHelper.getQuest().getKeywords());

		setLayout(new BorderLayout(3, 0));
		setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH, 20));

		JLabel nameLabel = new JLabel(questHelper.getQuest().getName());
		Color color = questState == QuestState.FINISHED ? questHelperPlugin.getConfig().passColour() : (questState == QuestState.IN_PROGRESS ?
			new Color(240,207, 123) : Color.WHITE);
		nameLabel.setForeground(color);
		add(nameLabel, BorderLayout.CENTER);

		if (questState != QuestState.FINISHED)
		{
			JButton startButton = new JButton();
			startButton.setIcon(START_ICON);
			startButton.addActionListener(e ->
			{
				questHelperPlugin.setSidebarSelectedQuest(questHelper);
				questHelperPanel.emptyBar();
			});
			add(startButton, BorderLayout.LINE_END);
		}
	}

	public QuestSelectPanel(String text)
	{
		this.questHelper = null;

		setLayout(new BorderLayout(3, 3));
		setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH, 30));
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JLabel nameLabel = new JLabel(text);
		Color color = Color.WHITE;
		nameLabel.setForeground(color);
		add(nameLabel, BorderLayout.CENTER);
	}
}
