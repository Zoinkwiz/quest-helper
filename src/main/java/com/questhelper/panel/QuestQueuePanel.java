/*
 * Copyright (c) 2026, phillocity <https://github.com/phillocity>
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

import com.questhelper.managers.QuestQueueManager;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.tools.Icon;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;

public class QuestQueuePanel extends JPanel
{
	private static final ImageIcon START_ICON = Icon.START.getIcon();
	private static final ImageIcon REMOVE_ICON = Icon.CLOSE.getIcon();
	private static final ImageIcon DOWN_ICON = Icon.EXPANDED.getIcon();
	private static final ImageIcon UP_ICON = new ImageIcon(ImageUtil.flipImage(Icon.EXPANDED.getImage(), false, true));

	private final QuestQueueManager questQueueManager;
	private final Consumer<QuestHelper> startQuest;

	private final JLabel titleLabel = JGenerator.makeJLabel();
	private final JButton clearButton = new JButton("Clear");
	private final JPanel rowsPanel = new JPanel();
	private final JLabel hintLabel = JGenerator.makeJLabel();

	public QuestQueuePanel(QuestQueueManager questQueueManager, Consumer<QuestHelper> startQuest)
	{
		this.questQueueManager = questQueueManager;
		this.startQuest = startQuest;

		setLayout(new BorderLayout(0, 4));
		setBorder(new EmptyBorder(8, 10, 0, 10));
		setOpaque(false);

		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		titleLabel.setFont(FontManager.getRunescapeBoldFont());
		titleLabel.setForeground(Color.WHITE);
		header.add(titleLabel, BorderLayout.CENTER);

		clearButton.setToolTipText("Remove every quest from the queue");
		clearButton.setMargin(new Insets(0, 4, 0, 4));
		clearButton.setFocusPainted(false);
		clearButton.addActionListener(e -> questQueueManager.clear());
		header.add(clearButton, BorderLayout.EAST);

		rowsPanel.setLayout(new BoxLayout(rowsPanel, BoxLayout.Y_AXIS));
		rowsPanel.setOpaque(false);

		hintLabel.setFont(FontManager.getRunescapeSmallFont());
		hintLabel.setForeground(Color.GRAY);

		add(header, BorderLayout.NORTH);
		add(rowsPanel, BorderLayout.CENTER);
		add(hintLabel, BorderLayout.SOUTH);

		update(List.of(), false);
	}

	public void update(List<QuestHelperQuest> queue, boolean loggedIn)
	{
		// The queue is saved per account, so there's nothing to show until we're logged in
		setVisible(loggedIn);

		titleLabel.setText("Quest queue" + (queue.isEmpty() ? "" : " (" + queue.size() + ")"));
		clearButton.setVisible(!queue.isEmpty());
		hintLabel.setText(queue.isEmpty()
			? "Press + on a quest to queue it"
			: "Items for all of these are in the bank tab");

		rowsPanel.removeAll();
		for (int i = 0; i < queue.size(); i++)
		{
			rowsPanel.add(makeRow(queue.get(i), i, queue.size()));
			rowsPanel.add(Box.createVerticalStrut(2));
		}

		revalidate();
		repaint();
	}

	private JPanel makeRow(QuestHelperQuest quest, int index, int queueSize)
	{
		JPanel row = new JPanel(new BorderLayout(3, 0));
		row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		row.setBorder(new EmptyBorder(2, 4, 2, 2));

		JLabel name = JGenerator.makeJLabel((index + 1) + ". " + quest.getName());
		name.setForeground(Color.WHITE);
		name.setToolTipText(quest.getName());
		row.add(name, BorderLayout.CENTER);

		JButton startButton = makeIconButton(START_ICON, "Start this quest's helper");
		startButton.addActionListener(e -> startQuest.accept(quest.getQuestHelper()));

		JButton upButton = makeIconButton(UP_ICON, "Move up (do sooner)");
		upButton.setEnabled(index > 0);
		upButton.addActionListener(e -> questQueueManager.moveUp(quest));

		JButton downButton = makeIconButton(DOWN_ICON, "Move down (do later)");
		downButton.setEnabled(index < queueSize - 1);
		downButton.addActionListener(e -> questQueueManager.moveDown(quest));

		JButton removeButton = makeIconButton(REMOVE_ICON, "Remove from queue");
		removeButton.addActionListener(e -> questQueueManager.remove(quest));

		JPanel buttons = new JPanel(new GridLayout(1, 4, 1, 0));
		buttons.setOpaque(false);
		buttons.add(startButton);
		buttons.add(upButton);
		buttons.add(downButton);
		buttons.add(removeButton);
		row.add(buttons, BorderLayout.EAST);

		return row;
	}

	private static JButton makeIconButton(ImageIcon icon, String tooltip)
	{
		JButton button = new JButton(icon);
		button.setToolTipText(tooltip);
		button.setPreferredSize(new Dimension(20, 18));
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setFocusPainted(false);
		return button;
	}
}
