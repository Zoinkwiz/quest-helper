/*
 * Copyright (c) 2021, Zoinkwiz
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

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.ItemRequirement;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.text.WordUtils;

public class QuestRequirementPanel extends JPanel
{
	private static final ImageIcon INFO_ICON;

	@Getter
	@Setter
	private JLabel label;


	@Getter
	private final ItemRequirement itemRequirement;

	static
	{
		final BufferedImage infoImg = ImageUtil.getResourceStreamFromClass(QuestHelperPlugin.class, "/info_icon.png");
		INFO_ICON = new ImageIcon(infoImg);
	}

	public QuestRequirementPanel(ItemRequirement itemRequirement)
	{
		this.itemRequirement = itemRequirement;

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(0, 0, 0, 0));

		StringBuilder text = new StringBuilder();
		if (itemRequirement.showQuantity())
		{
			text.append(itemRequirement.getQuantity()).append(" x ");
		}
		text.append(itemRequirement.getDisplayText());

		String html1 = "<html><body style='padding: 0px; margin: 0px; width: 140px'>";
		String html2 = "</body></html>";

		label = new JLabel(html1 + text + html2);
		label.setForeground(Color.GRAY);
		label.setSize(label.getPreferredSize());
		setPreferredSize(label.getSize());
		add(label, BorderLayout.WEST);

		if (itemRequirement.getTip() != null)
		{
			addButtonToPanel(itemRequirement.getTip());
		}
	}

	private void addButtonToPanel(String tooltipText)
	{
		JButton b = new JButton(INFO_ICON);
		b.setPreferredSize(new Dimension(10, 10));
		b.setToolTipText(tooltipText);
		b.setBorderPainted(false);
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		b.setContentAreaFilled(false);
		b.setMargin(new Insets(0, 0, 0, 0));
		add(b);
	}
}

