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
package com.questhelper.panel.queststepsection;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.managers.QuestManager;
import com.questhelper.panel.JGenerator;
import com.questhelper.panel.PanelDetails;
import com.questhelper.panel.QuestRequirementsPanel;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.BoardShipStep;
import com.questhelper.steps.PortTaskStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.Client;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.SwingUtil;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class QuestStepPanel extends AbstractQuestSection implements MouseListener
{
	private static final int TITLE_PADDING = 5;

	private final QuestHelperPlugin questHelperPlugin;
	private final HashMap<QuestStep, JTextPane> steps = new HashMap<>();
	private final @Nullable QuestRequirementsPanel requiredItemsPanel;
	private final @Nullable QuestRequirementsPanel recommendedItemsPanel;
	private boolean stepAutoLocked;
	private final QuestHelper questHelper;
	private QuestStep lastHighlightedStep = null;

	public QuestStepPanel(PanelDetails panelDetails, QuestStep currentStep, QuestManager questManager, QuestHelperPlugin questHelperPlugin)
	{
		this.panelDetails = panelDetails;
		this.questHelperPlugin = questHelperPlugin;
		this.questHelper = questManager.getSelectedQuest();

		setLayout(new BorderLayout(0, 1));
		setBorder(new EmptyBorder(5, 0, 0, 0));

		leftTitleContainer = new JPanel(new BorderLayout(5, 0));

		headerLabel.addMouseListener(this);
		addMouseListener(this);

		headerLabel.setText(panelDetails.getHeader());
		headerLabel.setFont(FontManager.getRunescapeBoldFont());

		headerLabel.setMinimumSize(new Dimension(1, headerLabel.getPreferredSize().height));

		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
		headerPanel.setBorder(new EmptyBorder(7, 7, 3, 7));

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
			recommendedItemsPanel = new QuestRequirementsPanel("Optionally bring the following items:", panelDetails.getRecommended(), questManager,
					false);
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
			if(step instanceof PortTaskStep)
			{
				for (QuestStep step2 : ((PortTaskStep) step).getStepsList())
				{
					JTextPane questStepLabel = createQuestStepLabel(step2);
					steps.put(step2, questStepLabel);
					questStepsPanel.add(questStepLabel);
				}
			}else{
				JTextPane questStepLabel = createQuestStepLabel(step);
				steps.put(step, questStepLabel);
				questStepsPanel.add(questStepLabel);
			}
		}

		bodyPanel.add(questStepsPanel, BorderLayout.SOUTH);

		add(headerPanel, BorderLayout.NORTH);
		add(bodyPanel, BorderLayout.CENTER);

		if (!panelDetails.getSteps().contains(currentStep))
		{
			collapse();
		}
	}

	private JTextPane createQuestStepLabel(QuestStep step){
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
		return questStepLabel;
	}

	public void updateAllText()
	{
		steps.forEach((questStep, textPane) -> {
			if (textPane != null)
			{
				String newText = generateText(questStep);
				String oldText = textPane.getText();

				if (!newText.equals(oldText))
				{
					textPane.setText(newText);
				}
			}
		});
	}

	public String generateText(QuestStep step)
	{
		StringBuilder text = new StringBuilder();
		QuestStep textStep = step;
		if(step instanceof BoardShipStep){
			textStep = step.getActiveStep();
		}
		if (textStep.getText() != null)
		{
			var first = true;
			for (var line : textStep.getText())
			{
				if (!first)
				{
					text.append("\n\n");
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

	public void setLockable(boolean canLock)
	{
		lockStep.setVisible(canLock);
	}

	public boolean updateHighlightCheck(Client client, QuestStep newStep, QuestHelper currentQuest)
	{
		if (panelDetails.getHideCondition() == null || !panelDetails.getHideCondition().check(client))
		{
			setVisible(true);
			boolean highlighted = false;
			setLockable(panelDetails.getLockingQuestSteps() != null &&
				(panelDetails.getVars() == null || panelDetails.getVars().contains(currentQuest.getVar())));

			for (QuestStep sidebarStep : getSteps())
			{
				if (sidebarStep.getConditionToHide() != null && sidebarStep.getConditionToHide().check(client)) continue;

				if (sidebarStep.getFadeCondition() != null)
				{
					if (sidebarStep.getFadeCondition().check(client))
					{
						updateTextToFaded(sidebarStep);
					}
					else
					{
						updateTextToUnfaded(sidebarStep);
					}
				}

				if (!highlighted && sidebarStep.containsSteps(newStep, new HashSet<>()))
				{
					highlighted = true;
					updateHighlight(sidebarStep);
				}
			}

			if (!highlighted)
			{
				removeHighlight();
			}

			return highlighted;
		}
		else
		{
			setVisible(false);
			return false;
		}
	}

	public void updateTextToFaded(QuestStep questStep)
	{
		if (steps.get(questStep) != null)
		{
			steps.get(questStep).setForeground(Color.DARK_GRAY);
			steps.get(questStep).setToolTipText(questStep.getFadeCondition().getDisplayText());
		}
	}

	public void updateTextToUnfaded(QuestStep questStep)
	{
		if (steps.get(questStep) != null)
		{
			steps.get(questStep).setForeground(Color.LIGHT_GRAY);
			steps.get(questStep).setToolTipText(null);
		}
	}

	public void updateHighlight(QuestStep currentStep)
	{
		expand();

		if (steps.get(lastHighlightedStep) != null)
		{
			steps.get(lastHighlightedStep).setForeground(Color.LIGHT_GRAY);
		}

		if (steps.get(currentStep) != null)
		{
			steps.get(currentStep).setForeground(ColorScheme.BRAND_ORANGE);
			headerLabel.setForeground(Color.BLACK);
			headerPanel.setBackground(ColorScheme.BRAND_ORANGE);
			viewControls.setBackground(ColorScheme.BRAND_ORANGE);
			leftTitleContainer.setBackground(ColorScheme.BRAND_ORANGE);
		}

		lastHighlightedStep = currentStep;
	}

	public void removeHighlight()
	{
		headerLabel.setForeground(Color.WHITE);
		if (isCollapsed())
		{
			applyDimmer(false, headerPanel);
		}
		headerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
		viewControls.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
		leftTitleContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
		if (steps.get(currentlyActiveQuestSidebarStep()) != null)
		{
			steps.get(currentlyActiveQuestSidebarStep()).setForeground(Color.LIGHT_GRAY);
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

	protected void lockSection(boolean locked)
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

	protected void collapse()
	{
		if (!isCollapsed())
		{
			bodyPanel.setVisible(false);
			applyDimmer(false, headerPanel);
		}
	}

	protected void expand()
	{
		if (isCollapsed())
		{
			bodyPanel.setVisible(true);
			applyDimmer(true, headerPanel);
		}
	}

	public boolean isCollapsed()
	{
		return !bodyPanel.isVisible();
	}

	protected void applyDimmer(boolean brighten, JPanel panel)
	{
		for (Component component : panel.getComponents())
		{
			Color color = component.getForeground();
			component.setForeground(brighten ? color.brighter() : color.darker());
		}
	}

	public void updateRequirements(Client client)
	{
		if (requiredItemsPanel != null)
		{
			requiredItemsPanel.update(client, questHelperPlugin);
		}

		if (recommendedItemsPanel != null)
		{
			recommendedItemsPanel.update(client, questHelperPlugin);
		}

		updateStepVisibility(client);
	}

	public boolean updateStepVisibility(Client client)
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

		if (stepVisibilityChanged)
		{
			updateHighlightCheck(client, currentlyActiveQuestSidebarStep(), questHelper);
		}

		return stepVisibilityChanged;
	}

	protected QuestStep currentlyActiveQuestSidebarStep()
	{
		var selectedQuest = questHelperPlugin.getSelectedQuest();
		if (selectedQuest == null) return null;
		var currentStep = selectedQuest.getCurrentStep();
		return currentStep.getActiveStep();
	}

	public List<Integer> getIds()
	{
		if (panelDetails.getId() == -1) return List.of();
		return List.of(panelDetails.getId());
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			if (isCollapsed())
			{
				expand();
			}
			else
			{
				collapse();
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent mouseEvent)
	{
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent)
	{
	}

	@Override
	public void mouseEntered(MouseEvent mouseEvent)
	{
	}

	@Override
	public void mouseExited(MouseEvent mouseEvent)
	{
	}
}
