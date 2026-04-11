package com.questhelper.panel;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Minimal RuneLite nav panel that opens the {@link HelperConstructFrame} maker window.
 */
public class HelperConstructPanel extends PluginPanel
{
	private final Runnable openMakerWindow;

	public HelperConstructPanel(Runnable openMakerWindow)
	{
		super(false);
		this.openMakerWindow = openMakerWindow;

		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(12, 8, 12, 8));

		JTextArea intro = JGenerator.makeJTextArea(
			"The maker opens in a separate window (NPC / object / item step tabs plus quest order, compact toolbar and “How to use…” for the full guide).\n\n"
				+ "Use the button below. You can return to this tab anytime to open or focus the window.");
		intro.setForeground(Color.LIGHT_GRAY);
		intro.setLineWrap(true);
		intro.setWrapStyleWord(true);
		intro.setOpaque(false);

		JButton open = new JButton("Open Quest Helper Maker");
		SwingUtil.removeButtonDecorations(open);
		open.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		open.setForeground(Color.WHITE);
		open.addActionListener(e -> openMakerWindow.run());

		JPanel center = new JPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
		center.setBackground(ColorScheme.DARK_GRAY_COLOR);
		center.add(intro);
		center.add(Box.createVerticalStrut(12));
		open.setAlignmentX(Component.LEFT_ALIGNMENT);
		center.add(open);

		add(center, BorderLayout.NORTH);
	}

	public void shutDown()
	{
	}
}
