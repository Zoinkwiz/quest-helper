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

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import javax.swing.plaf.basic.BasicButtonUI;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import com.questhelper.ItemRequirement;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

@Slf4j
public
class QuestHelperPanel extends PluginPanel
{
	static
	{
		final BufferedImage collapseImg = ImageUtil.getResourceStreamFromClass(QuestHelperPlugin.class, "/collapsed.png");
		final BufferedImage expandedImg = ImageUtil.getResourceStreamFromClass(QuestHelperPlugin.class, "/expanded.png");

		COLLAPSE_ICON = new ImageIcon(collapseImg);
		EXPAND_ICON = new ImageIcon(expandedImg);
	}

	private final Client client;

	private final JLabel title = new JLabel();
	private final PluginErrorPanel noQuestView = new PluginErrorPanel();
	private QuestHelperPlugin plugin;

	private final JPanel actionsContainer = new JPanel();
	private final JPanel titlePanel = new JPanel();

	private final JPanel introPanel = new JPanel();
	private final JPanel questItemRequirementsListPanel = new JPanel();
	private final JPanel questItemRecommendedListPanel = new JPanel();
	private final JPanel questCombatRequirementsListPanel = new JPanel();
	private final JPanel questOverviewListPanel = new JPanel();

	private final JPanel questStepsContainer = new JPanel();
	private final JLabel questNameLabel = new JLabel();

	private final List<QuestStepPanel> questStepPanelList = new ArrayList<>();


	private static final ImageIcon COLLAPSE_ICON;
	private static final ImageIcon EXPAND_ICON;
	private final JButton collapseBtn = new JButton();

	public QuestHelperPanel(QuestHelperPlugin questHelperPlugin, Client client)
	{
		super();

		this.plugin = questHelperPlugin;
		this.client = client;

		setBorder(new EmptyBorder(6, 6, 6, 6));
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setLayout(new BorderLayout());

		/* CONTROLS */
		actionsContainer.setLayout(new BorderLayout());
		actionsContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		actionsContainer.setPreferredSize(new Dimension(0, 30));
		actionsContainer.setBorder(new EmptyBorder(5, 5, 5, 10));
		actionsContainer.setVisible(false);

		final JPanel viewControls = new JPanel(new GridLayout(1, 3, 10, 0));
		viewControls.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		SwingUtil.removeButtonDecorations(collapseBtn);
		collapseBtn.setIcon(EXPAND_ICON);
		collapseBtn.setSelectedIcon(COLLAPSE_ICON);
		SwingUtil.addModalTooltip(collapseBtn, "Collapse All", "Un-Collapse All");
		collapseBtn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		collapseBtn.setUI(new BasicButtonUI());
		collapseBtn.addActionListener(ev -> changeCollapse());
		viewControls.add(collapseBtn);

		actionsContainer.add(viewControls, BorderLayout.EAST);

		questNameLabel.setForeground(Color.WHITE);
		questNameLabel.setText("");
		final JPanel leftTitleContainer = new JPanel(new BorderLayout(5, 0));
		leftTitleContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		leftTitleContainer.add(questNameLabel, BorderLayout.CENTER);

		actionsContainer.add(leftTitleContainer, BorderLayout.WEST);

		/* Quest overview panel */
		introPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(5, 0, 0, 0, ColorScheme.DARK_GRAY_COLOR),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)
		));
		introPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		introPanel.setLayout(new BorderLayout());
		introPanel.setVisible(false);

		/* Item requirements */
		JPanel questItemRequirementsPanel = new JPanel();
		questItemRequirementsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		questItemRequirementsPanel.setLayout(new BorderLayout());
		questItemRequirementsPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

		JPanel questItemRequirementsHeader = new JPanel();
		questItemRequirementsHeader.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		questItemRequirementsHeader.setLayout(new BorderLayout());
		questItemRequirementsHeader.setBorder(new EmptyBorder(5, 5, 5, 10));

		JLabel questItemReqs = new JLabel();
		questItemReqs.setForeground(Color.WHITE);
		questItemReqs.setText("Item requirements:");
		questItemReqs.setMinimumSize(new Dimension(1, questItemRequirementsHeader.getPreferredSize().height));
		questItemRequirementsHeader.add(questItemReqs, BorderLayout.NORTH);

		questItemRequirementsListPanel.setLayout(new BorderLayout());
		questItemRequirementsListPanel.setBorder(new EmptyBorder(10, 5, 10, 5));

		questItemRequirementsPanel.add(questItemRequirementsHeader, BorderLayout.NORTH);
		questItemRequirementsPanel.add(questItemRequirementsListPanel, BorderLayout.CENTER);

		/* Recommended Items */
		JPanel questItemRecommendedPanel = new JPanel();
		questItemRecommendedPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		questItemRecommendedPanel.setLayout(new BorderLayout());
		questItemRecommendedPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

		JPanel questItemRecommendedHeader = new JPanel();
		questItemRecommendedHeader.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		questItemRecommendedHeader.setLayout(new BorderLayout());
		questItemRecommendedHeader.setBorder(new EmptyBorder(5, 5, 5, 10));

		JLabel questItemRecommended = new JLabel();
		questItemRecommended.setForeground(Color.WHITE);
		questItemRecommended.setText("Recommended items:");
		questItemRecommended.setMinimumSize(new Dimension(1, questItemRecommendedHeader.getPreferredSize().height));
		questItemRecommendedHeader.add(questItemRecommended, BorderLayout.NORTH);

		questItemRecommendedListPanel.setLayout(new BorderLayout());
		questItemRecommendedListPanel.setBorder(new EmptyBorder(10, 5, 10, 5));

		questItemRecommendedPanel.add(questItemRecommendedHeader, BorderLayout.NORTH);
		questItemRecommendedPanel.add(questItemRecommendedListPanel, BorderLayout.CENTER);

		/* Combat requirements */
		JPanel questCombatRequirementsPanel = new JPanel();
		questCombatRequirementsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		questCombatRequirementsPanel.setLayout(new BorderLayout());
		questCombatRequirementsPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

		JPanel questCombatRequirementsHeader = new JPanel();
		questCombatRequirementsHeader.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		questCombatRequirementsHeader.setLayout(new BorderLayout());
		questCombatRequirementsHeader.setBorder(new EmptyBorder(5, 5, 5, 10));

		JLabel questCombatReqs = new JLabel();
		questCombatReqs.setForeground(Color.WHITE);
		questCombatReqs.setText("Enemies to defeat:");
		questCombatReqs.setMinimumSize(new Dimension(1, questCombatRequirementsHeader.getPreferredSize().height));
		questCombatRequirementsHeader.add(questCombatReqs, BorderLayout.NORTH);

		questCombatRequirementsListPanel.setLayout(new BorderLayout());
		questCombatRequirementsListPanel.setBorder(new EmptyBorder(10, 5, 10, 5));

		questCombatRequirementsPanel.add(questCombatRequirementsHeader, BorderLayout.NORTH);
		questCombatRequirementsPanel.add(questCombatRequirementsListPanel, BorderLayout.CENTER);

		/* Quest notes */
		JPanel questOverviewPanel = new JPanel();
		questOverviewPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		questOverviewPanel.setLayout(new BorderLayout());
		questOverviewPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

		JPanel questOverviewHeader = new JPanel();
		questOverviewHeader.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		questOverviewHeader.setLayout(new BorderLayout());
		questOverviewHeader.setBorder(new EmptyBorder(5, 5, 5, 10));

		JLabel questOverview = new JLabel();
		questOverview.setForeground(Color.WHITE);
		questOverview.setText("Notes:");
		questOverview.setMinimumSize(new Dimension(1, questOverviewHeader.getPreferredSize().height));
		questOverviewHeader.add(questOverview, BorderLayout.NORTH);

		questOverviewListPanel.setLayout(new BorderLayout());
		questOverviewListPanel.setBorder(new EmptyBorder(10, 5, 10, 5));

		questOverviewPanel.add(questOverviewHeader, BorderLayout.NORTH);
		questOverviewPanel.add(questOverviewListPanel, BorderLayout.CENTER);

		/* Layout */
		final JPanel layoutPanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(layoutPanel, BoxLayout.Y_AXIS);
		layoutPanel.setLayout(boxLayout);
		add(layoutPanel, BorderLayout.NORTH);

		final JPanel overviewPanel = new JPanel();
		BoxLayout boxLayoutOverview = new BoxLayout(overviewPanel, BoxLayout.Y_AXIS);
		overviewPanel.setLayout(boxLayoutOverview);

		overviewPanel.add(questItemRequirementsPanel);
		overviewPanel.add(questItemRecommendedPanel);
		overviewPanel.add(questCombatRequirementsPanel);
		overviewPanel.add(questOverviewPanel);

		introPanel.add(overviewPanel, BorderLayout.NORTH);
		/* Setup overview panel */

		titlePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		titlePanel.setLayout(new BorderLayout());

		title.setText("Quest Helper");
		title.setForeground(Color.WHITE);
		titlePanel.add(title, BorderLayout.WEST);

		/* Container for quest steps */
		questStepsContainer.setLayout(new BoxLayout(questStepsContainer, BoxLayout.Y_AXIS));

		layoutPanel.add(titlePanel);
		layoutPanel.add(actionsContainer);
		layoutPanel.add(introPanel);
		layoutPanel.add(questStepsContainer);
	}

	public void addQuest(BasicQuestHelper quest)
	{
		ArrayList<PanelDetails> steps = quest.getPanels();

		if (quest.getCurrentStep() != null)
		{
			QuestStep currentStep = quest.getCurrentStep().getActiveStep();

			questNameLabel.setText(quest.getQuest().getName());
			actionsContainer.setVisible(true);

			setupQuestRequirements(quest);
			introPanel.setVisible(true);

			for (int i = 0; i < steps.size(); i++)
			{
				PanelDetails panelDetail = steps.get(i);
				QuestStepPanel newStep = new QuestStepPanel(panelDetail, currentStep);
				if (panelDetail.lockingQuest != null)
				{
					newStep.setLockable();
				}
				questStepPanelList.add(newStep);
				questStepsContainer.add(newStep);
				newStep.addMouseListener(new MouseAdapter()
				{
					@Override
					public void mouseClicked(MouseEvent e)
					{
						if (e.getButton() == MouseEvent.BUTTON1)
						{
							if (newStep.isCollapsed())
							{
								newStep.expand();
							}
							else
							{
								newStep.collapse();
							}
							updateCollapseText();
						}
					}
				});
				repaint();
				revalidate();
			}
		}
	}

	public void updateHighlight(QuestStep newStep)
	{
		questStepPanelList.forEach(panel -> {
			boolean highlighted = false;
			for (QuestStep step : panel.getSteps())
			{
				if (step == newStep || step.getSubsteps().contains(newStep))
				{
					highlighted = true;
					panel.updateHighlight(step);
					break;
				}
			}
			if (!highlighted)
			{
				panel.removeHighlight();
			}
		});

		repaint();
		revalidate();
	}

	public void updateLocks()
	{
		questStepPanelList.forEach(panel -> {
			panel.updateLock();
		});

		repaint();
		revalidate();
	}

	public void removeQuest()
	{
		actionsContainer.setVisible(false);
		introPanel.setVisible(false);
		questStepsContainer.removeAll();
		questItemRequirementsListPanel.removeAll();
		questItemRecommendedListPanel.removeAll();
		questOverviewListPanel.removeAll();
		repaint();
		revalidate();
	}

	public void setupQuestRequirements(BasicQuestHelper quest)
	{
		ArrayList<ItemRequirement> itemRequirements = quest.getItemRequirements();

		/* Required items */
		JLabel itemLabel = new JLabel();
		itemLabel.setForeground(Color.GRAY);
		StringBuilder text = new StringBuilder();
		if (itemRequirements != null)
		{
			for (ItemRequirement itemRequirement : itemRequirements)
			{
				if (!text.toString().equals(""))
				{
					text.append("<br>");
				}
				if (itemRequirement.showQuantity())
				{
					text.append(itemRequirement.getQuantity()).append(" x ");
				}
				text.append(itemRequirement.getName());
			}
		}
		else
		{
			text.append("None");
		}

		questItemRequirementsListPanel.add(itemLabel);

		itemLabel.setText("<html><body style = 'text-align:left'>" + text + "</body></html>");

		/* Recommended items */
		ArrayList<ItemRequirement> itemRecommended = quest.getItemRecommended();
		JLabel itemRecommendedLabel = new JLabel();
		itemRecommendedLabel.setForeground(Color.GRAY);
		StringBuilder textRecommended = new StringBuilder();
		if (itemRecommended == null)
		{
			textRecommended.append("None");
		}
		else
		{
			for (ItemRequirement itemRequirement : itemRecommended)
			{
				if (!textRecommended.toString().equals(""))
				{
					textRecommended.append("<br>");
				}
				if (itemRequirement.showQuantity())
				{
					textRecommended.append(itemRequirement.getQuantity()).append(" x ");
				}
				textRecommended.append(itemRequirement.getName());
			}
		}

		itemRecommendedLabel.setText("<html><body style = 'text-align:left'>" + textRecommended + "</body></html>");

		questItemRecommendedListPanel.add(itemRecommendedLabel);

		/* Combat requirements */
		JLabel combatLabel = new JLabel();
		combatLabel.setForeground(Color.GRAY);
		ArrayList<String> combatRequirementList = quest.getCombatRequirements();
		StringBuilder textCombat = new StringBuilder();
		if (combatRequirementList == null)
		{
			textCombat.append("None");
		}
		else
		{
			for (String combatRequirement : combatRequirementList)
			{
				textCombat.append(combatRequirement);
				textCombat.append("<br>");
			}
		}
		combatLabel.setText("<html><body style = 'text-align:left'>" + textCombat + "</body></html>");

		questCombatRequirementsListPanel.add(combatLabel);

		/* Quest overview */
		JLabel overviewLabel = new JLabel();
		overviewLabel.setForeground(Color.GRAY);
		ArrayList<String> notes = quest.getNotes();
		StringBuilder textNote = new StringBuilder();
		if (notes == null)
		{
			textNote.append("None");
		}
		else
		{
			for (String note : notes)
			{
				textNote.append(note);
				textNote.append("<br><br>");
			}
		}

		overviewLabel.setText("<html><body style = 'text-align:left'>" + textNote + "</body></html>");

		questOverviewListPanel.add(overviewLabel);
	}

	public void rebuild()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;

		boolean empty = constraints.gridy == 0;
		noQuestView.setVisible(empty);
		title.setVisible(!empty);

		repaint();
		revalidate();
	}

	/**
	 * Changes the collapse status of quest steps
	 */
	private void changeCollapse()
	{
		boolean isAllCollapsed = isAllCollapsed();

		for (QuestStepPanel box : questStepPanelList)
		{
			if (isAllCollapsed)
			{
				box.expand();
			}
			else if (!box.isCollapsed())
			{
				box.collapse();
			}
		}

		updateCollapseText();
	}

	void updateCollapseText()
	{
		collapseBtn.setSelected(isAllCollapsed());
	}

	private boolean isAllCollapsed()
	{
		return questStepPanelList.stream()
			.filter(QuestStepPanel::isCollapsed)
			.count() == questStepPanelList.size();
	}
}
