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
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.requirements.Requirement;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a single Quest {@link Requirement}.
 * These are displayed both in the general quest requirements section
 * as well as in the individual quest step sections.
 */

//TODO: Add panel to consolidate quest requirements (item,skill,recommended,etc)
//TODO: Add panel to show the quest requirements (item, skill, etc)
//TODO: Make QuestOverviewPanel *only* show the quest steps and whatever is necessary for the quest
//TODO: Rename panels to be more accurate. Names TBD
public class QuestRequirementPanel extends JPanel
{
	private static final ImageIcon INFO_ICON = IconUtil.INFO_ICON.getIcon();

	@Getter
	@Setter
	private JLabel label;

	@Getter
	private final Requirement itemRequirement;

	public QuestRequirementPanel(Requirement requirement)
	{
		this.itemRequirement = requirement;

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

		if (requirement.getTip() != null)
		{
			addButtonToPanel(requirement.getTip());
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

