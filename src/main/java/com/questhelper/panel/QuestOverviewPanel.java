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

import com.questhelper.BankItems;
import com.questhelper.IconUtil;
import com.questhelper.QuestHelperPlugin;

import com.questhelper.StreamUtil;
import com.questhelper.panel.components.ActionsContainer;
import com.questhelper.panel.components.QuestRequirementOverviewPanel;
import com.questhelper.panel.components.QuestRequirementPanel;
import com.questhelper.panel.components.QuestRequirementSection;
import com.questhelper.panel.components.QuestStepContainer;
import com.questhelper.panel.components.QuestStepPanel;
import com.questhelper.panel.components.RequirementContainer;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.api.Client;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

/**
 * The actual quest panel that displays all the required items/skills/etc.
 * As well as the quest steps.
 */
public class QuestOverviewPanel extends JPanel implements RequirementContainer
{
	private final QuestHelperPlugin questHelperPlugin;
	public QuestHelper currentQuest;

	private final QuestStepContainer questStepsContainer;
	private final ActionsContainer actionsContainer;

	private final JPanel introPanel = new JPanel();

	/** Holder for all quest requirements so we can pass on all Container calls easily
	 *  We still need the individual sections because each gets a unique list of requirements
	 */
	private final QuestRequirementOverviewPanel questRequirementOverviewPanel = new QuestRequirementOverviewPanel();
	private final QuestRequirementSection generalRequirements = new QuestRequirementSection("General Requirements:");
	private final QuestRequirementSection generalRecommended = new QuestRequirementSection("Recommended:");
	private final QuestRequirementSection itemRequirements = new QuestRequirementSection("Item Requirements:");
	private final QuestRequirementSection recommendedItems = new QuestRequirementSection("Recommended Items:");
	private final QuestRequirementSection enemiesToDefeat = new QuestRequirementSection("Enemies to defeat:");
	private final QuestRequirementSection notes = new QuestRequirementSection("Notes:");

	private final JButton collapseBtn = new JButton();

	private final List<QuestStepPanel> questStepPanelList = new ArrayList<>();

	private final List<QuestRequirementPanel> requirementPanels = new ArrayList<>();

	public QuestOverviewPanel(QuestHelperPlugin questHelperPlugin)
	{
		super();
		this.questHelperPlugin = questHelperPlugin;
		this.questStepsContainer = new QuestStepContainer(this.questHelperPlugin);

		BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(boxLayout);
		setBorder(new EmptyBorder(6, 6, 6, 6));

		/* CONTROLS */
		actionsContainer = new ActionsContainer(ev -> closeHelper());

		/* Quest overview panel */
		introPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(5, 0, 0, 0, ColorScheme.DARK_GRAY_COLOR),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)
		));
		introPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		introPanel.setLayout(new BorderLayout());
		introPanel.setVisible(false);

		/* Panel for all overview details*/
		questRequirementOverviewPanel.add(generalRequirements);
		questRequirementOverviewPanel.add(generalRecommended);
		questRequirementOverviewPanel.add(itemRequirements);
		questRequirementOverviewPanel.add(recommendedItems);
		questRequirementOverviewPanel.add(enemiesToDefeat);
		questRequirementOverviewPanel.add(notes);

		introPanel.add(questRequirementOverviewPanel, BorderLayout.NORTH);

		/* Container for quest steps */
		questStepsContainer.setLayout(new BoxLayout(questStepsContainer, BoxLayout.Y_AXIS));

		add(actionsContainer);
		add(introPanel);
		add(questStepsContainer);
	}

	public void addQuest(QuestHelper quest, boolean isActive)
	{
		currentQuest = quest;

		QuestStep currentStep;
		if (isActive)
		{
			currentStep = quest.getCurrentStep().getSidePanelStep();
		}
		else
		{
			currentStep = new DetailedQuestStep(quest, "Fake step");
		}

		if (quest.getCurrentStep() != null)
		{
			actionsContainer.getQuestNameLabel().setText(quest.getQuest().getName());
			actionsContainer.setVisible(true);

			setupQuestRequirements(quest);
			questStepsContainer.initQuestSteps(quest, currentStep, (panel, ev) -> updateCollapseText());
			introPanel.setVisible(true);
			questRequirementOverviewPanel.setVisible(true);

			repaint();
			revalidate();
		}
	}

	public void updateSteps()
	{
		questStepsContainer.updateSteps();
	}

	public void updateHighlight(QuestStep newStep)
	{
		questStepsContainer.updateHighlight(newStep, currentQuest);
		repaint();
		revalidate();
	}

	public void updateLocks()
	{
		questStepsContainer.updateLocks();
		repaint();
		revalidate();
	}

	public void removeQuest()
	{
		actionsContainer.setVisible(false);
		introPanel.setVisible(false);
		questStepsContainer.removeAll();
		questRequirementOverviewPanel.removeAll();
		currentQuest = null;
		repaint();
		revalidate();
	}

	private void closeHelper()
	{
		questHelperPlugin.shutDownQuestFromSidebar();
	}

	void updateCollapseText()
	{
		collapseBtn.setSelected(questStepsContainer.isAllCollapsed());
	}

	public void setupQuestRequirements(QuestHelper quest)
	{
		/* Non-item requirements */
		generalRequirements.addOrUpdateRequirements(quest.getGeneralRequirements());
		generalRecommended.addOrUpdateRequirements(quest.getGeneralRecommended());

		/* Required items */
		ArrayList<ItemRequirement> itemReq = quest.getItemRequirements();
		if (itemReq != null)
		{
			this.itemRequirements.addOrUpdateRequirements(quest.getItemRequirements());
		}
		else
		{
			JLabel itemRequiredLabel = new JLabel();
			itemRequiredLabel.setForeground(Color.GRAY);
			itemRequiredLabel.setText("None");
			this.itemRequirements.add(itemRequiredLabel);
		}

		/* Recommended items */
		ArrayList<ItemRequirement> itemRecommended = quest.getItemRecommended();
		if (itemRecommended != null)
		{
			this.recommendedItems.addOrUpdateRequirements(quest.getItemRecommended());
		}
		else
		{
			JLabel itemRecommendedLabel = new JLabel();
			itemRecommendedLabel.setForeground(Color.GRAY);
			itemRecommendedLabel.setText("None");
			this.recommendedItems.add(itemRecommendedLabel);
		}

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

		this.enemiesToDefeat.add(combatLabel);

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

			this.notes.add(overviewLabel);
			this.notes.setVisible(true);
		}
		else
		{
			this.notes.setVisible(false);
		}

		this.requirementPanels.addAll(questRequirementOverviewPanel.getRequirementPanels());
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(PluginPanel.PANEL_WIDTH, super.getPreferredSize().height);
	}

	@Override
	public void updateRequirements(Client client, BankItems bankItems)
	{
		requirementPanels.forEach(panel -> panel.updateRequirements(client, bankItems));
		questStepsContainer.updateRequirements(client, bankItems);
		revalidate();
	}

	@Override
	public List<Requirement> getRequirements()
	{
		List<Requirement> panels = StreamUtil.getRequirements(requirementPanels);
		List<Requirement> steps = StreamUtil.getRequirements(questStepPanelList);
		// ensure distinct() is called so we don't return multiple of the same requirement
		return Stream.concat(panels.stream(), steps.stream()).distinct().collect(Collectors.toList());
	}
}
