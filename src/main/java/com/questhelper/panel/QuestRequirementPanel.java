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

import com.questhelper.Icon;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import javax.annotation.Nullable;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

public class QuestRequirementPanel extends JPanel
{
	private static final ImageIcon INFO_ICON = Icon.INFO_ICON.getIcon();

	@Getter
	@Setter
	private JLabel label;


	@Getter
	private final Requirement requirement;

	@Nullable
	@Getter
	private JButton infoButton;

	public QuestRequirementPanel(Requirement requirement)
	{
		this.requirement = requirement;

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(0, 0, 0, 0));

		StringBuilder text = new StringBuilder();
		if (requirement instanceof ItemRequirement)
		{
			ItemRequirement itemRequirement = (ItemRequirement) requirement;
			if (itemRequirement.showQuantity())
			{
				text.append(itemRequirement.getQuantity()).append(" x ");
			}
		}
		text.append(requirement.getDisplayText());

		String html1 = "<html><body style='padding: 0px; margin: 0px; width: 140px'>";
		String html2 = "</body></html>";

		label = new JLabel(html1 + text + html2);
		label.setForeground(Color.GRAY);
		label.setSize(label.getPreferredSize());
		setPreferredSize(label.getSize());
		add(label, BorderLayout.WEST);

		if (requirement.getTooltip() != null)
		{
			addButtonToPanel(requirement.getTooltip());
		}
	}

	public void setInfoButtonTooltip(String text)
	{
		if (infoButton != null)
		{
			if (StringUtils.isBlank(text))
			{
				infoButton.setToolTipText("");
				infoButton.setVisible(false);
			}
			else
			{
				String html1 = "<html><body>";
				String html2 = "</body></html>";
				text = text.replaceAll("\\n", "<br>");
				infoButton.setToolTipText(html1 + text + html2);
			}
		}
	}

	private void addButtonToPanel(String tooltipText)
	{

		infoButton = new JButton(INFO_ICON);
		infoButton.setPreferredSize(new Dimension(10, 10));
		setInfoButtonTooltip(tooltipText);
		infoButton.setBorderPainted(false);
		infoButton.setFocusPainted(false);
		infoButton.setBorderPainted(false);
		infoButton.setContentAreaFilled(false);
		infoButton.setMargin(new Insets(0, 0, 0, 0));
		add(infoButton);
	}
}

