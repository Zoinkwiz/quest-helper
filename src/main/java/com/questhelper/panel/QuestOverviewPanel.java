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

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.BasicQuestHelper;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

public class QuestOverviewPanel extends JPanel
{
	private final QuestHelperPlugin questHelperPlugin;
	public QuestHelper currentQuest;

	private final JPanel questStepsContainer = new JPanel();
	private final JPanel actionsContainer = new JPanel();

	private final JPanel introPanel = new JPanel();
	private final JLabel questOverviewNotes = new JLabel();

	private final JPanel questItemRequirementsListPanel = new JPanel();
	private final JPanel questItemRecommendedListPanel = new JPanel();
	private final JPanel questCombatRequirementsListPanel = new JPanel();
	private final JPanel questOverviewNotesPanel = new JPanel();

	private final JLabel questNameLabel = new JLabel();

	private static final ImageIcon CLOSE_ICON;

	private final JButton collapseBtn = new JButton();

	private final List<QuestStepPanel> questStepPanelList = new ArrayList<>();

	static
	{
		final BufferedImage closeImg = ImageUtil.getResourceStreamFromClass(QuestHelperPlugin.class, "/close.png");

		CLOSE_ICON = new ImageIcon(closeImg);
	}

	public QuestOverviewPanel(QuestHelperPlugin questHelperPlugin)
	{
		super();
		this.questHelperPlugin = questHelperPlugin;

		BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(boxLayout);
		setBorder(new EmptyBorder(6, 6, 6, 6));

		/* CONTROLS */
		actionsContainer.setLayout(new BorderLayout());
		actionsContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		actionsContainer.setPreferredSize(new Dimension(0, 30));
		actionsContainer.setBorder(new EmptyBorder(5, 5, 5, 10));
		actionsContainer.setVisible(false);

		final JPanel viewControls = new JPanel(new GridLayout(1, 3, 10, 0));
		viewControls.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JButton closeBtn = new JButton();
		SwingUtil.removeButtonDecorations(closeBtn);
		closeBtn.setIcon(CLOSE_ICON);
		closeBtn.setToolTipText("Close helper");
		closeBtn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		closeBtn.setUI(new BasicButtonUI());
		closeBtn.addActionListener(ev -> closeHelper());
		viewControls.add(closeBtn);

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

		questOverviewNotes.setForeground(Color.WHITE);
		questOverviewNotes.setText("Notes:");
		questOverviewNotes.setMinimumSize(new Dimension(1, questOverviewHeader.getPreferredSize().height));
		questOverviewHeader.add(questOverviewNotes, BorderLayout.NORTH);

		questOverviewNotesPanel.setLayout(new BorderLayout());
		questOverviewNotesPanel.setBorder(new EmptyBorder(10, 5, 10, 5));

		questOverviewPanel.add(questOverviewHeader, BorderLayout.NORTH);
		questOverviewPanel.add(questOverviewNotesPanel, BorderLayout.CENTER);

		/* Panel for all overview details*/
		final JPanel overviewPanel = new JPanel();
		BoxLayout boxLayoutOverview = new BoxLayout(overviewPanel, BoxLayout.Y_AXIS);
		overviewPanel.setLayout(boxLayoutOverview);

		overviewPanel.add(questItemRequirementsPanel);
		overviewPanel.add(questItemRecommendedPanel);
		overviewPanel.add(questCombatRequirementsPanel);
		overviewPanel.add(questOverviewPanel);

		introPanel.add(overviewPanel, BorderLayout.NORTH);

		/* Container for quest steps */
		questStepsContainer.setLayout(new BoxLayout(questStepsContainer, BoxLayout.Y_AXIS));

		add(actionsContainer);
		add(introPanel);
		add(questStepsContainer);
	}

	public void addQuest(QuestHelper quest, boolean isActive)
	{
		currentQuest = quest;

		ArrayList<PanelDetails> steps = quest.getPanels();
		QuestStep currentStep;
		if (isActive)
		{
			currentStep = quest.getCurrentStep().getActiveStep();
		}
		else
		{
			currentStep = new DetailedQuestStep(quest, "Fake step");
		}

		if (quest.getCurrentStep() != null)
		{
			questNameLabel.setText(quest.getQuest().getName());
			actionsContainer.setVisible(true);

			setupQuestRequirements(quest);
			introPanel.setVisible(true);

			for (PanelDetails panelDetail : steps)
			{
				QuestStepPanel newStep = new QuestStepPanel(panelDetail, currentStep);
				if (panelDetail.getLockingQuestSteps() != null &&
					(panelDetail.getVars() == null
						|| panelDetail.getVars().contains(currentQuest.getVar())))
				{
					newStep.setLockable(true);
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

	public void updateSteps()
	{
		questStepPanelList.forEach(panel -> {
			for (QuestStep step : panel.getSteps())
			{
				JLabel label = panel.getStepsLabels().get(step);
				if (label != null)
				{
					label.setText(panel.generateText(step));
				}
			}
		});
	}

	public void updateHighlight(QuestStep newStep)
	{
		questStepPanelList.forEach(panel -> {
			boolean highlighted = false;
			panel.setLockable(panel.panelDetails.getLockingQuestSteps() != null &&
				(panel.panelDetails.getVars() == null || panel.panelDetails.getVars().contains(currentQuest.getVar())));
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
		questStepPanelList.forEach(QuestStepPanel::updateLock);

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
		questCombatRequirementsListPanel.removeAll();
		currentQuest = null;
		questOverviewNotesPanel.removeAll();
		repaint();
		revalidate();
	}

	private void closeHelper()
	{
		questHelperPlugin.shutDownQuestFromSidebar();
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

	public void setupQuestRequirements(QuestHelper quest)
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
		if (notes != null)
		{
			for (String note : notes)
			{
				textNote.append(note);
				textNote.append("<br><br>");
			}
			overviewLabel.setText("<html><body style = 'text-align:left'>" + textNote + "</body></html>");

			questOverviewNotesPanel.add(overviewLabel);
			questOverviewNotesPanel.setVisible(true);
			questOverviewNotes.setVisible(true);
		}
		else
		{
			questOverviewNotes.setVisible(false);
			questOverviewNotesPanel.setVisible(false);
		}
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(PluginPanel.PANEL_WIDTH, super.getPreferredSize().height);
	}
}
