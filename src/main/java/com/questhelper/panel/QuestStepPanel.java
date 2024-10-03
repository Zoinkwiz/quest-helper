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
import com.questhelper.managers.QuestManager;
import com.questhelper.questhelpers.QuestHelper;
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
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.SwingUtil;

public class QuestStepPanel extends JPanel
{
	private static final int TITLE_PADDING = 5;

	private final PanelDetails panelDetails;
	private final QuestHelperPlugin questHelperPlugin;

	private final JPanel headerPanel = new JPanel();
	private final JLabel headerLabel = JGenerator.makeJLabel();
	private final JPanel bodyPanel = new JPanel();
	private final JCheckBox lockStep = new JCheckBox();
	private final JPanel leftTitleContainer;
	private final JPanel viewControls;
	private final HashMap<QuestStep, JTextPane> steps = new HashMap<>();
	private final @Nullable QuestRequirementsPanel requiredItemsPanel;
	private final @Nullable QuestRequirementsPanel recommendedItemsPanel;
	private QuestStep currentlyHighlighted = null;
	private boolean stepAutoLocked;

	public QuestStepPanel(PanelDetails panelDetails, QuestStep currentStep, QuestManager questManager, QuestHelperPlugin questHelperPlugin)
	{
		this.panelDetails = panelDetails;
		this.questHelperPlugin = questHelperPlugin;

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

		if (panelDetails.getRequirements() != null)
		{
			requiredItemsPanel = new QuestRequirementsPanel("Bring the following items:", panelDetails.getRequirements(), questManager, false);
			bodyPanel.add(requiredItemsPanel, BorderLayout.NORTH);
		}
		else
		{
			requiredItemsPanel = null;
		}

		if (panelDetails.getRecommended() != null)
		{
			recommendedItemsPanel = new QuestRequirementsPanel("Bring the following items:", panelDetails.getRecommended(), questManager, false);
			bodyPanel.add(recommendedItemsPanel, BorderLayout.CENTER);
		}
		else
		{
			recommendedItemsPanel = null;
		}

		JPanel questStepsPanel = new JPanel();
		questStepsPanel.setLayout(new BoxLayout(questStepsPanel, BoxLayout.Y_AXIS));
		questStepsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		for (QuestStep step : panelDetails.getSteps())
		{
			JTextPane questStepLabel = JGenerator.makeJTextPane();
			questStepLabel.setLayout(new BorderLayout());
			questStepLabel.setAlignmentX(SwingConstants.LEFT);
			questStepLabel.setAlignmentY(SwingConstants.TOP);
			questStepLabel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			questStepLabel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR.brighter()),
				BorderFactory.createEmptyBorder(5, 5, 10, 0)
			));
			questStepLabel.setText(generateText(step));
			questStepLabel.setOpaque(true);
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

	public String generateText(QuestStep step)
	{
		StringBuilder text = new StringBuilder();

		if (step.getText() != null)
		{
			var first = true;
			for (var line : step.getText())
			{
				if (!first)
				{
					text.append("\n");
				}
				text.append(line);
				first = false;
			}
		}

		return text.toString();
	}

	public List<QuestStep> getSteps()
	{
		return new ArrayList<>(steps.keySet());
	}

	public HashMap<QuestStep, JTextPane> getStepsLabels()
	{
		return steps;
	}

	public void setLockable(boolean canLock)
	{
		lockStep.setVisible(canLock);
	}

	public void updateHighlightCheck(Client client, QuestStep newStep, QuestHelper currentQuest)
	{
		if (panelDetails.getHideCondition() == null || !panelDetails.getHideCondition().check(client))
		{
			setVisible(true);
			boolean highlighted = false;
			setLockable(panelDetails.getLockingQuestSteps() != null &&
				(panelDetails.getVars() == null || panelDetails.getVars().contains(currentQuest.getVar())));

			for (QuestStep step : getSteps())
			{
				if (step.getConditionToHide() != null && step.getConditionToHide().check(client)) continue;
				if (step == newStep || step.getSubsteps().contains(newStep))
				{
					highlighted = true;
					updateHighlight(step);
					break;
				}
			}

			if (!highlighted)
			{
				removeHighlight();
			}
		}
		else
		{
			setVisible(false);
		}
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

	public void updateRequirements(Client client, List<Item> bankItems)
	{
		if (requiredItemsPanel != null)
		{
			requiredItemsPanel.update(client, questHelperPlugin, bankItems);
		}

		if (recommendedItemsPanel != null)
		{
			recommendedItemsPanel.update(client, questHelperPlugin, bankItems);
		}

		updateStepVisibility(client);
	}

	public void updateStepVisibility(Client client)
	{
		boolean stepVisibilityChanged = false;
		for (QuestStep step : steps.keySet())
		{
			boolean oldVisibility = step.isShowInSidebar();
			boolean newVisibility = step.getConditionToHide() == null || !step.getConditionToHide().check(client);
			stepVisibilityChanged = stepVisibilityChanged || (oldVisibility != newVisibility);

			step.setShowInSidebar(newVisibility);
			steps.get(step).setVisible(newVisibility);
		}

		if (stepVisibilityChanged && currentlyHighlighted != null)
		{
			updateHighlightCheck(client, currentlyHighlighted.getQuestHelper().getCurrentStep(), currentlyHighlighted.getQuestHelper());
		}
	}
}
