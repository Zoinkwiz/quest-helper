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

import com.questhelper.requirements.Requirement;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import com.questhelper.steps.QuestStep;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.client.ui.ColorScheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.SwingUtil;

public class QuestStepPanel extends JPanel
{
	private static final int TITLE_PADDING = 5;

	PanelDetails panelDetails;

	private final JPanel headerPanel = new JPanel();
	private final JLabel headerLabel = new JLabel();
	private final JPanel bodyPanel = new JPanel();
	private final JCheckBox lockStep = new JCheckBox();
	private final JPanel leftTitleContainer;
	private final JPanel viewControls;

	private QuestStep currentlyHighlighted = null;
	private boolean stepAutoLocked;

	private final HashMap<QuestStep, JLabel> steps = new HashMap<>();

	private final ArrayList<QuestRequirementPanel> requirementPanels = new ArrayList<>();

	public QuestStepPanel(PanelDetails panelDetails, QuestStep currentStep)
	{
		this.panelDetails = panelDetails;

		setLayout(new BorderLayout(0, 1));
		setBorder(new EmptyBorder(5, 0, 0, 0));

		leftTitleContainer = new JPanel(new BorderLayout(5, 0));

		headerLabel.setText(panelDetails.getHeader());
		headerLabel.setFont(FontManager.getRunescapeBoldFont());

		headerLabel.setMinimumSize(new Dimension(1, headerLabel.getPreferredSize().height));

		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
		headerPanel.setBorder(new EmptyBorder(7, 7, 7, 7));

		headerPanel.add(Box.createRigidArea(new Dimension(TITLE_PADDING, 0)));
		leftTitleContainer.add(headerLabel, BorderLayout.CENTER);
		headerPanel.add(leftTitleContainer, BorderLayout.WEST);

		viewControls = new JPanel(new GridLayout(1, 3, 10, 0));

		SwingUtil.addModalTooltip(lockStep, "Mark section as incomplete", "Mark section as complete");
		lockStep.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		lockStep.addActionListener(ev -> lockSection(lockStep.isSelected()));
		lockStep.setVisible(false);
		headerPanel.add(lockStep, BorderLayout.EAST);

		viewControls.add(lockStep);

		headerPanel.add(viewControls, BorderLayout.EAST);

		if (panelDetails.contains(currentStep))
		{
			headerLabel.setForeground(Color.BLACK);
			headerPanel.setBackground(ColorScheme.BRAND_ORANGE);
			viewControls.setBackground(ColorScheme.BRAND_ORANGE);
			leftTitleContainer.setBackground(ColorScheme.BRAND_ORANGE);
		}
		else
		{
			headerLabel.setForeground(Color.WHITE);
			headerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
			viewControls.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
			leftTitleContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
		}

		/* Body */
		bodyPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		bodyPanel.setLayout(new BorderLayout());
		bodyPanel.setBorder(new EmptyBorder(10, 5, 10, 5));

		if (!panelDetails.getRequirements().isEmpty())
		{
			addRequirements("Bring the following items:", panelDetails.getRequirements(), BorderLayout.NORTH);
		}

		if (panelDetails.getRecommended() != null && !panelDetails.getRecommended().isEmpty())
		{
			addRequirements("Optionally bring the following:", panelDetails.getRecommended(), BorderLayout.CENTER);
		}

		JPanel questStepsPanel = new JPanel();
		questStepsPanel.setLayout(new BoxLayout(questStepsPanel, BoxLayout.Y_AXIS));
		questStepsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		for (QuestStep step : panelDetails.getSteps())
		{
			JLabel questStepLabel = new JLabel();
			questStepLabel.setLayout(new BorderLayout());
			questStepLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
			questStepLabel.setHorizontalAlignment(SwingConstants.LEFT);
			questStepLabel.setVerticalAlignment(SwingConstants.TOP);
			questStepLabel.setText(generateText(step));
			questStepLabel.setVisible(step.isShowInSidebar());

			steps.put(step, questStepLabel);
			questStepsPanel.add(questStepLabel);

		}

		bodyPanel.add(questStepsPanel, BorderLayout.SOUTH);

		add(headerPanel, BorderLayout.NORTH);
		add(bodyPanel, BorderLayout.CENTER);

		if (!panelDetails.getSteps().contains(currentStep))
		{
			collapse();
		}
	}

	public void addRequirements(String text, List<Requirement> reqs, String borderLayout)
	{
		JPanel questRequirementsPanel = new JPanel();
		questRequirementsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		questRequirementsPanel.setLayout(new BorderLayout());
		questRequirementsPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

		JPanel questRequirementsHeader = new JPanel();
		questRequirementsHeader.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		questRequirementsHeader.setLayout(new BorderLayout());
		questRequirementsHeader.setBorder(new EmptyBorder(5, 5, 5, 10));

		JLabel questReqsTitle = new JLabel();
		questReqsTitle.setForeground(Color.WHITE);
		questReqsTitle.setText(text);
		questReqsTitle.setMinimumSize(new Dimension(1, questRequirementsHeader.getPreferredSize().height));
		questRequirementsHeader.add(questReqsTitle, BorderLayout.NORTH);

		JPanel questRequirementsListPanel = new JPanel();
		questRequirementsListPanel.setLayout(new DynamicGridLayout(0, 1, 0, 1));
		questRequirementsListPanel.setBorder(new EmptyBorder(5, 5, 5, 5));


		for (Requirement req : reqs)
		{
			QuestRequirementPanel reqPanel = new QuestRequirementPanel(req);
			requirementPanels.add(reqPanel);
			questRequirementsListPanel.add(new QuestRequirementWrapperPanel(reqPanel));
		}

		questRequirementsPanel.add(questRequirementsHeader, BorderLayout.NORTH);
		questRequirementsPanel.add(questRequirementsListPanel, BorderLayout.CENTER);

		bodyPanel.add(questRequirementsPanel, borderLayout);
	}

	public String generateText(QuestStep step)
	{
		StringBuilder text = new StringBuilder();

		step.getText().forEach(line -> text.append(line).append(" "));

		return "<html><body style = 'text-align:left'>" + text + "</body></html>";
	}

	public List<QuestStep> getSteps()
	{
		return new ArrayList<>(steps.keySet());
	}

	public HashMap<QuestStep, JLabel> getStepsLabels()
	{
		return steps;
	}

	public void setLockable(boolean canLock)
	{
		lockStep.setVisible(canLock);
	}

	public void updateHighlight(QuestStep currentStep)
	{
		expand();

		if (currentlyHighlighted != null && steps.get(currentlyHighlighted) != null)
		{
			steps.get(currentlyHighlighted).setForeground(Color.LIGHT_GRAY);
		}
		else
		{
			headerLabel.setForeground(Color.BLACK);
			headerPanel.setBackground(ColorScheme.BRAND_ORANGE);
			viewControls.setBackground(ColorScheme.BRAND_ORANGE);
			leftTitleContainer.setBackground(ColorScheme.BRAND_ORANGE);
		}

		if (steps.get(currentStep) != null)
		{
			steps.get(currentStep).setForeground(ColorScheme.BRAND_ORANGE);
		}
		currentlyHighlighted = currentStep;
	}

	public void removeHighlight()
	{
		if (currentlyHighlighted != null)
		{
			headerLabel.setForeground(Color.WHITE);
			if (isCollapsed())
			{
				applyDimmer(false, headerPanel);
			}
			headerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
			viewControls.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
			leftTitleContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
			if (steps.get(currentlyHighlighted) != null)
			{
				steps.get(currentlyHighlighted).setForeground(Color.LIGHT_GRAY);
			}
			currentlyHighlighted = null;
		}
		collapse();
	}

	public void updateLock()
	{
		if (panelDetails.getLockingQuestSteps() == null)
		{
			return;
		}

		if (panelDetails.getLockingQuestSteps().isUnlockable())
		{
			stepAutoLocked = false;
			lockStep.setEnabled(true);
		}
		else
		{
			if (!stepAutoLocked)
			{
				collapse();
			}
			stepAutoLocked = true;
			lockStep.setEnabled(false);
		}

		if (panelDetails.getLockingQuestSteps().isLocked())
		{
			lockStep.setSelected(true);
		}
	}

	private void lockSection(boolean locked)
	{
		if (locked)
		{
			panelDetails.getLockingQuestSteps().setLockedManually(true);
			if (!isCollapsed())
			{
				collapse();
			}
		}
		else
		{
			panelDetails.getLockingQuestSteps().setLockedManually(false);
			if (isCollapsed())
			{
				expand();
			}
		}
	}

	void collapse()
	{
		if (!isCollapsed())
		{
			bodyPanel.setVisible(false);
			applyDimmer(false, headerPanel);
		}
	}

	void expand()
	{
		if (isCollapsed())
		{
			bodyPanel.setVisible(true);
			applyDimmer(true, headerPanel);
		}
	}

	boolean isCollapsed()
	{
		return !bodyPanel.isVisible();
	}

	private void applyDimmer(boolean brighten, JPanel panel)
	{
		for (Component component : panel.getComponents())
		{
			Color color = component.getForeground();

			component.setForeground(brighten ? color.brighter() : color.darker());
		}
	}

	public void updateRequirements(Client client, List<Item> bankItems, QuestOverviewPanel questOverviewPanel)
	{
		questOverviewPanel.updateRequirementPanels(client, requirementPanels, bankItems);
		updateStepVisibility(client);
	}

	public void updateStepVisibility(Client client)
	{
		for (QuestStep step : steps.keySet())
		{
			step.setShowInSidebar(step.getConditionToHide() == null || !step.getConditionToHide().check(client));
			steps.get(step).setVisible(step.isShowInSidebar());
		}
	}
}
