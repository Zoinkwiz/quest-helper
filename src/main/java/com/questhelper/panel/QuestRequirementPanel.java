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

import com.questhelper.managers.QuestManager;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.tools.Icon;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.Setter;

public class QuestRequirementPanel extends JPanel
{
	private static final ImageIcon INFO_ICON = Icon.INFO_ICON.getIcon();

	@Getter
	@Setter
	private JLabel label;

	@Getter
	private final Requirement requirement;

	public QuestRequirementPanel(Requirement requirement, QuestManager questManager)
	{
		this.requirement = requirement;

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(0, 0, 0, 0));

		// TODO: Create generic getSidebarText which handles generating sidebar text for all requirement types
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
		String html1Underline = "<html><body style='padding: 0px; margin: 0px; width: 140px; text-decoration:underline'>";

		label = new JLabel(html1 + text + html2);
		label.setForeground(Color.GRAY);
		label.setSize(label.getPreferredSize());
		setPreferredSize(label.getSize());
		add(label, BorderLayout.WEST);

		if (requirement instanceof ItemRequirement)
		{
			JPopupMenu menu = new JPopupMenu("Menu");
			int id = ((ItemRequirement) requirement).getId();
			JMenuItem wikiLink = new JMenuItem(new AbstractAction("Go to wiki..")
			{
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						if (requirement.getUrlSuffix() == null)
						{
							Desktop.getDesktop().browse(new URI("https://oldschool.runescape.wiki/w/Special:Lookup?type=item&id=" + id));
						}
						else
						{
							Desktop.getDesktop().browse(new URI("https://oldschool.runescape.wiki/w/" + requirement.getUrlSuffix()));
						}
					}
					catch (IOException | URISyntaxException e1)
					{
						e1.printStackTrace();
					}
				}
			});

			menu.add(wikiLink);

			label.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent e)
				{
					ItemRequirement iReq = ((ItemRequirement) requirement);
					//right mouse click event
					if (iReq.getId() != -1 || (iReq.getId() != -1 && iReq.getUrlSuffix() != null)
						|| (iReq.getId() == -1 && iReq.getUrlSuffix() != null))
					{
						if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1)
						{
							menu.show(label, e.getX(), e.getY());
						}
					}
				}
			});
		}
		else if (questManager != null && requirement instanceof QuestRequirement)
		{
			QuestHelper quest = ((QuestRequirement) requirement).getQuest().getQuestHelper();
			label.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent event)
				{
					questManager.setSidebarSelectedQuest(quest);
				}

				public void mouseEntered(MouseEvent evt)
				{
					label.setText(html1Underline + text + html2);
				}

				public void mouseExited(MouseEvent evt)
				{
					label.setText(html1 + text + html2);
				}
			});
		}

		if (requirement.getTooltip() != null)
		{
			addButtonToPanel(requirement.getTooltip());
		}
	}

	private void addButtonToPanel(String tooltipText)
	{
		String html1 = "<html><body>";
		String html2 = "</body></html>";
		tooltipText = tooltipText.replaceAll("\\n", "<br>");
		JButton b = new JButton(INFO_ICON);
		b.setPreferredSize(new Dimension(10, 10));
		b.setToolTipText(html1 + tooltipText + html2);
		b.setBorderPainted(false);
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		b.setContentAreaFilled(false);
		b.setMargin(new Insets(0, 0, 0, 0));
		add(b);
	}
}

