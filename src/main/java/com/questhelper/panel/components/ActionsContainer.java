/*
 *
 *  * Copyright (c) 2021, Senmori
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice, this
 *  *    list of conditions and the following disclaimer.
 *  * 2. Redistributions in binary form must reproduce the above copyright notice,
 *  *    this list of conditions and the following disclaimer in the documentation
 *  *    and/or other materials provided with the distribution.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.questhelper.panel.components;

import com.questhelper.IconUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.SwingUtil;

/**
 * Contains the close button and the quest name label and the functionality
 * for when the close button is clicked
 */
public class ActionsContainer extends JPanel
{
	private static final ImageIcon CLOSE_ICON = IconUtil.CLOSE.getIcon();

	private final JPanel viewControls = new JPanel();
	private final JPanel titleContainer = new JPanel();

	@Getter
	private final JButton closeButton = new JButton();
	@Getter
	private final JLabel questNameLabel = new JLabel();
	public ActionsContainer(Consumer<ActionEvent> closeButtonConsumer)
	{
		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setPreferredSize(new Dimension(0, 30));
		setBorder(new EmptyBorder(5, 5, 5, 10));
		setVisible(false);

		viewControls.setLayout(new GridLayout(1, 3, 10, 0));
		viewControls.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		SwingUtil.removeButtonDecorations(closeButton);
		closeButton.setIcon(CLOSE_ICON);
		closeButton.setToolTipText("Close helper");
		closeButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		closeButton.setUI(new BasicButtonUI());
		closeButton.addActionListener(closeButtonConsumer::accept);

		viewControls.add(closeButton);

		add(viewControls, BorderLayout.EAST);

		titleContainer.setLayout(new BorderLayout(5, 0));
		titleContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		questNameLabel.setForeground(Color.WHITE);
		questNameLabel.setText("");

		titleContainer.add(questNameLabel, BorderLayout.CENTER);

		add(titleContainer, BorderLayout.WEST);
	}
}
