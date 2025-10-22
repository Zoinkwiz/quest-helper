/*
 * Copyright (c) 2025, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.ui;

import com.questhelper.helpers.guides.Unlock;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

/**
 * Sidebar overlay for handling conflicts when starting a quest while an active path is running
 */
public class PathConflictDialog extends JPanel
{
	public enum ConflictChoice
	{
		CONTINUE_PATH,
		PAUSE_PATH,
		STOP_PATH,
		CANCEL
	}
	
	private ConflictChoice result = ConflictChoice.CANCEL;
	private final Consumer<ConflictChoice> callback;
	private final Unlock activePath;
	private final String questName;
	
	public PathConflictDialog(Unlock activePath, String questName, Consumer<ConflictChoice> callback)
	{
		this.activePath = activePath;
		this.questName = questName;
		this.callback = callback;
		
		initComponents();
	}
	
	private void initComponents()
	{
		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setOpaque(true);
		setVisible(true);
		setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_COLOR, 1),
			BorderFactory.createEmptyBorder(10, 10, 10, 10)
		));
		
		// Main content panel
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout(5, 5));
		contentPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		contentPanel.setOpaque(true);
		contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		// Message label
		JLabel messageLabel = new JLabel();
		messageLabel.setText("<html><div style='text-align: center; width: 180px;'>" +
			"<b>Path Conflict</b><br/><br/>" +
			"Starting <b>" + questName + "</b> will interrupt your active path:<br/>" +
			"<b>" + activePath.getName() + "</b><br/><br/>" +
			"What would you like to do?</div></html>");
		messageLabel.setForeground(Color.WHITE);
		messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(messageLabel, BorderLayout.NORTH);
		
		// Button panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(2, 2, 3, 3));
		buttonPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		
		// Continue Path button
		JButton continueButton = createStyledButton("Continue Path", () -> {
			result = ConflictChoice.CONTINUE_PATH;
			hideOverlay();
		});
		buttonPanel.add(continueButton);
		
		// Pause Path button
		JButton pauseButton = createStyledButton("Pause Path", () -> {
			result = ConflictChoice.PAUSE_PATH;
			hideOverlay();
		});
		buttonPanel.add(pauseButton);
		
		// Stop Path button
		JButton stopButton = createStyledButton("Stop Path", () -> {
			result = ConflictChoice.STOP_PATH;
			hideOverlay();
		});
		buttonPanel.add(stopButton);
		
		// Cancel button
		JButton cancelButton = createStyledButton("Cancel", () -> {
			result = ConflictChoice.CANCEL;
			hideOverlay();
		});
		buttonPanel.add(cancelButton);
		
		contentPanel.add(buttonPanel, BorderLayout.SOUTH);
		add(contentPanel, BorderLayout.CENTER);
	}
	
	private JButton createStyledButton(String text, Runnable action)
	{
		JButton button = new JButton(text);
		button.setPreferredSize(new Dimension(90, 35));
		button.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setFont(button.getFont().deriveFont(Font.BOLD, 12f));
		
		button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				action.run();
			}
		});
		
		// Hover effects
		button.addMouseListener(new java.awt.event.MouseAdapter()
		{
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e)
			{
				button.setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);
			}
			
			@Override
			public void mouseExited(java.awt.event.MouseEvent e)
			{
				button.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			}
		});
		
		return button;
	}
	
	/**
	 * Show the overlay
	 */
	public void showOverlay()
	{
		setVisible(true);
	}
	
	/**
	 * Hide the overlay
	 */
	public void hideOverlay()
	{
		setVisible(false);
		if (callback != null)
		{
			callback.accept(result);
		}
	}
}
