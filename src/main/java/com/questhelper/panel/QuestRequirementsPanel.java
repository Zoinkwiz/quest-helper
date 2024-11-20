/*
 * Copyright (c) 2021, Zoinkwiz
 * Copyright (c) 2024, pajlada <https://github.com/pajlada>
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
import com.questhelper.managers.QuestManager;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.NoItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.tools.Icon;
import com.questhelper.util.Fonts;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.LinkBrowser;
import org.apache.commons.lang3.tuple.Pair;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

public class QuestRequirementsPanel extends JPanel
{
	private static final ImageIcon INFO_ICON = Icon.INFO_ICON.getIcon();

	/**
	 * List of Requirements & their respective text areas
	 * This can be iterated over to update their display text, or their color
	 */
	private final List<InlineRequirement> requirementList = new ArrayList<>();
	private final QuestManager questManager;
	private final boolean showEvenIfEmpty;
	private final JPanel requirementsPanel = new JPanel();

	public QuestRequirementsPanel(@NonNull String header, Collection<Requirement> requirements, QuestHelperPlugin questHelperPlugin, @NonNull QuestManager questManager,
								  boolean showEvenIfEmpty)
	{
		this.questManager = questManager;
		this.showEvenIfEmpty = showEvenIfEmpty;

		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(0, 0, 10, 0));

		var headerPanel = createHeader(header);

		add(headerPanel, BorderLayout.NORTH);

		requirementsPanel.setLayout(new DynamicPaddedGridLayout(0, 1, 0, 1));
		requirementsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		add(requirementsPanel, BorderLayout.CENTER);

		setRequirements(questHelperPlugin, requirements);
	}

	public static JPanel createHeader(@NonNull String header)
	{
		var headerPanel = new JPanel();
		headerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		headerPanel.setLayout(new BorderLayout());
		headerPanel.setBorder(new EmptyBorder(5, 5, 5, 10));

		var headerLabel = JGenerator.makeJTextArea(header);
		headerLabel.setForeground(Color.WHITE);
		headerLabel.setMinimumSize(new Dimension(1, headerPanel.getPreferredSize().height));
		headerPanel.add(headerLabel, BorderLayout.NORTH);

		return headerPanel;
	}

	public static Pair<JPanel, JPanel> createGenericGroup(@NonNull String header)
	{
		var group = new JPanel();
		group.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		group.setLayout(new BorderLayout());
		group.setBorder(new EmptyBorder(0, 0, 0, 0));

		var headerPanel = createHeader(header);

		var listPanel = new JPanel();
		listPanel.setLayout(new DynamicPaddedGridLayout(0, 1, 0, 1));
		listPanel.setBorder(new EmptyBorder(10, 5, 10, 5));

		group.add(headerPanel, BorderLayout.NORTH);
		group.add(listPanel, BorderLayout.CENTER);

		return Pair.of(group, listPanel);
	}

	public void setRequirements(QuestHelperPlugin questHelperPlugin, @Nullable Collection<? extends Requirement> requirements)
	{
		requirementList.forEach((req) -> questHelperPlugin.getActiveRequirementsManager().removeRequirement(req.requirement));
		requirementList.clear();
		requirementsPanel.removeAll();

		if (requirements != null && !requirements.isEmpty())
		{
			questHelperPlugin.getClientThread().invokeLater(() -> requirements.forEach((req) -> questHelperPlugin.getActiveRequirementsManager().addSidebarRequirement(req)));
			for (var requirement : requirements)
			{
				var panel = new JPanel();
				panel.setLayout(new BorderLayout());
				panel.setBorder(new EmptyBorder(0, 0, 0, 0));

				var label = JGenerator.makeJTextArea(requirement.getDisplayText());
				label.setFont(Fonts.getOriginalFont());
				var menu = new JPopupMenu("Menu");
				QuestHelper quest = null;

				JButton tooltipButton = null;

				panel.add(label, BorderLayout.CENTER);

				if (requirement.getTooltip() != null)
				{
					tooltipButton = addButtonToPanel(panel, requirement.getTooltip());
				}

				var wikiUrl = requirement.getWikiUrl();
				if (wikiUrl != null)
				{
					var wikiLink = new JMenuItem(new AbstractAction("Go to wiki..")
					{
						@Override
						public void actionPerformed(ActionEvent e)
						{
							LinkBrowser.browse(wikiUrl);
						}
					});

					menu.add(wikiLink);
				}

				if (requirement instanceof QuestRequirement)
				{
					quest = ((QuestRequirement) requirement).getQuest().getQuestHelper();

					QuestHelper finalQuest1 = quest;
					var questLink = new JMenuItem(new AbstractAction("Open quest helper...")
					{
						@Override
						public void actionPerformed(ActionEvent e)
						{
							questManager.setSidebarSelectedQuest(finalQuest1);
						}
					});

					menu.add(questLink);
				}

				var menuHasEntries = menu.getSubElements().length > 0;

				if (menuHasEntries || quest != null)
				{
					QuestHelper finalQuest = quest;
					label.addMouseListener(new MouseAdapter()
					{
						@Override
						public void mouseClicked(MouseEvent e)
						{
							if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1)
							{
								menu.show(label, e.getX(), e.getY());
							}

							if (finalQuest != null)
							{
								if (SwingUtilities.isLeftMouseButton(e))
								{
									questManager.setSidebarSelectedQuest(finalQuest);
								}
							}
						}

						public void mouseEntered(MouseEvent evt)
						{
							if (finalQuest != null)
							{
								label.setFont(Fonts.getUnderlinedFont());
							}
						}

						public void mouseExited(MouseEvent evt)
						{
							if (finalQuest != null)
							{
								label.setFont(Fonts.getOriginalFont());
							}
						}
					});
				}

				requirementsPanel.add(panel);
				requirementList.add(new InlineRequirement(requirement, label, tooltipButton));
			}
		}
		else if (showEvenIfEmpty)
		{
			var noneLabel = JGenerator.makeJTextArea("None");
			noneLabel.setForeground(Color.GRAY);
			requirementsPanel.add(noneLabel);
		}

		revalidate();
	}

	public void updateRequirement(Client client, QuestHelperPlugin questHelperPlugin, Requirement requirement)
	{
		requirementList.forEach((inlineReq) ->
		{
			if (inlineReq.requirement == requirement)
			{
				changeRequirement(client, questHelperPlugin, inlineReq);
			}
		});
	}

	public void update(Client client, QuestHelperPlugin questHelperPlugin, List<Item> bankItems)
	{
		int numActive = 0;

		for (var v : requirementList)
		{
			numActive += changeRequirement(client, questHelperPlugin, v);
		}

		this.setVisible(numActive > 0);
	}

	private int changeRequirement(Client client, QuestHelperPlugin questHelperPlugin, InlineRequirement inlineRequirement)
	{
		var req = inlineRequirement.requirement;
		var label = inlineRequirement.textArea;
		var tooltipButton = inlineRequirement.tooltipButton;

		if (!req.shouldDisplayText(client))
		{
			label.setVisible(false);
			if (tooltipButton != null)
			{
				tooltipButton.setVisible(false);
			}
			return 0;
		}

		label.setVisible(true);
		if (tooltipButton != null)
		{
			tooltipButton.setVisible(true);
		}

		var newText = req.getDisplayText();
		if (!label.getText().equals(newText))
		{
			label.setText(req.getDisplayText());
		}

		Color newColor;

		if (req instanceof ItemRequirement)
		{
			var itemRequirement = (ItemRequirement) req;

			if (itemRequirement instanceof NoItemRequirement)
			{
				newColor = itemRequirement.getColor(client, questHelperPlugin.getConfig()); // explicitly call this because
				// NoItemRequirement overrides it
			}
			else
			{
				newColor = itemRequirement.getColorConsideringBank(questHelperPlugin.getConfig());
			}
		}
		else
		{
			newColor = req.getColor(client, questHelperPlugin.getConfig());
		}

		if (newColor == Color.WHITE)
		{
			label.setToolTipText("In bank");
		}
		else if (newColor == Color.ORANGE)
		{
			label.setToolTipText("On steel key ring");
		}
		else
		{
			label.setToolTipText("");
		}

		label.setForeground(newColor);

		return 1;
	}

	private JButton addButtonToPanel(JPanel panel, String tooltipText)
	{
		JButton b = new JButton(INFO_ICON);
		b.setPreferredSize(new Dimension(10, 10));
		b.setToolTipText(tooltipText);
		b.setBorderPainted(false);
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		b.setContentAreaFilled(false);
		b.setMargin(new Insets(0, 0, 0, 0));
		panel.add(b, BorderLayout.EAST);

		return b;
	}

	@RequiredArgsConstructor
	private static class InlineRequirement
	{
		private final Requirement requirement;
		private final JTextArea textArea;
		private final @Nullable JButton tooltipButton;
	}
}
