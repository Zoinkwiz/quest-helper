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

import com.questhelper.ExternalQuestResources;
import com.questhelper.Icon;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.NoItemRequirement;
import com.questhelper.rewards.Reward;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.LinkBrowser;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestOverviewPanel extends JPanel
{
	private final QuestHelperPlugin questHelperPlugin;
	public QuestHelper currentQuest;

	private final JPanel questStepsContainer = new JPanel();
	private final JPanel actionsContainer = new JPanel();

	private final JPanel introPanel = new JPanel();
	private final JLabel questOverviewNotes = new JLabel();

	private final JPanel questGeneralRequirementsListPanel = new JPanel();
	private final JPanel questGeneralRecommendedListPanel = new JPanel();

	private final JPanel questItemRequirementsListPanel = new JPanel();
	private final JPanel questItemRecommendedListPanel = new JPanel();
	private final JPanel questCombatRequirementsListPanel = new JPanel();
	private final JPanel questOverviewNotesPanel = new JPanel();

	private final JPanel questRewardPanel = new JPanel();

	private final JPanel externalQuestResourcesPanel = new JPanel();

	private final JPanel questGeneralRequirementsHeader = new JPanel();
	private final JPanel questGeneralRecommendedHeader = new JPanel();
	private final JPanel questItemRequirementsHeader = new JPanel();
	private final JPanel questCombatRequirementHeader = new JPanel();
	private final JPanel questItemRecommendedHeader = new JPanel();
	private final JPanel questNoteHeader = new JPanel();

	private final JPanel questRewardHeader = new JPanel();

	private final JPanel externalQuestResourcesHeader = new JPanel();

	private final JLabel questNameLabel = new JLabel();

	private static final ImageIcon CLOSE_ICON = Icon.CLOSE.getIcon();
	private static final ImageIcon INFO_ICON = Icon.INFO_ICON.getIcon();

	private final JButton collapseBtn = new JButton();

	private final List<QuestStepPanel> questStepPanelList = new ArrayList<>();

	private final List<QuestRequirementPanel> requirementPanels = new ArrayList<>();

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

		/* Panel for all overview details*/
		final JPanel overviewPanel = new JPanel();
		BoxLayout boxLayoutOverview = new BoxLayout(overviewPanel, BoxLayout.Y_AXIS);
		overviewPanel.setLayout(boxLayoutOverview);

		overviewPanel.add(generateRequirementPanel(questGeneralRequirementsListPanel,
			questGeneralRequirementsHeader, "General requirements:"));
		overviewPanel.add(generateRequirementPanel(questGeneralRecommendedListPanel,
			questGeneralRecommendedHeader, "Recommended:"));
		overviewPanel.add(generateRequirementPanel(questItemRequirementsListPanel,
			questItemRequirementsHeader, "Item requirements:"));
		overviewPanel.add(generateRequirementPanel(questItemRecommendedListPanel, questItemRecommendedHeader,
			"Recommended items:"));
		overviewPanel.add(generateRequirementPanel(questCombatRequirementsListPanel, questCombatRequirementHeader,
			"Enemies to defeat:"));
		overviewPanel.add(generateRequirementPanel(questOverviewNotesPanel, questNoteHeader, "Notes:"));
		overviewPanel.add(generateRequirementPanel(questRewardPanel, questRewardHeader,
			"Rewards:"));
		overviewPanel.add(generateRequirementPanel(externalQuestResourcesPanel, externalQuestResourcesHeader , "External Resources:"));

		introPanel.add(overviewPanel, BorderLayout.NORTH);

		/* Container for quest steps */
		questStepsContainer.setLayout(new BoxLayout(questStepsContainer, BoxLayout.Y_AXIS));

		add(actionsContainer);
		add(introPanel);
		add(questStepsContainer);
	}

	private JPanel generateRequirementPanel(JPanel listPanel, JPanel headerPanel, String header)
	{
		JPanel requirementPanel = new JPanel();
		requirementPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		requirementPanel.setLayout(new BorderLayout());
		requirementPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

		headerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		headerPanel.setLayout(new BorderLayout());
		headerPanel.setBorder(new EmptyBorder(5, 5, 5, 10));

		JLabel questItemReqs = new JLabel();
		questItemReqs.setForeground(Color.WHITE);
		questItemReqs.setText(header);
		questItemReqs.setMinimumSize(new Dimension(1, headerPanel.getPreferredSize().height));
		headerPanel.add(questItemReqs, BorderLayout.NORTH);

		listPanel.setLayout(new DynamicGridLayout(0, 1, 0, 1));
		listPanel.setBorder(new EmptyBorder(10, 5, 10, 5));

		requirementPanel.add(headerPanel, BorderLayout.NORTH);
		requirementPanel.add(listPanel, BorderLayout.CENTER);

		return requirementPanel;
	}

	public void addQuest(QuestHelper quest, boolean isActive)
	{
		currentQuest = quest;

		List<PanelDetails> steps = quest.getPanels();
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
		if (questStepPanelList == null)
		{
			return;
		}
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

	public void updateHighlight(Client client, QuestStep newStep)
	{
		questStepPanelList.forEach(panel -> {
			if (panel.panelDetails.getHideCondition() == null || !panel.panelDetails.getHideCondition().check(client))
			{
				panel.setVisible(true);
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
			}
			else
			{
				panel.setVisible(false);
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
		questGeneralRequirementsListPanel.removeAll();
		questGeneralRecommendedListPanel.removeAll();
		questItemRequirementsListPanel.removeAll();
		questItemRecommendedListPanel.removeAll();
		questCombatRequirementsListPanel.removeAll();
		currentQuest = null;
		questOverviewNotesPanel.removeAll();
		questRewardPanel.removeAll();
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
		/* Non-item requirements */
		updateRequirementsPanels(questGeneralRequirementsHeader, questGeneralRequirementsListPanel, requirementPanels, quest.getGeneralRequirements());

		/* Non-item recommended */
		updateRequirementsPanels(questGeneralRecommendedHeader, questGeneralRecommendedListPanel, requirementPanels, quest.getGeneralRecommended());

		/* Required items */
		updateItemRequirementsPanels(questItemRequirementsListPanel, requirementPanels, quest.getItemRequirements());

		/* Recommended items */
		updateItemRequirementsPanels(questItemRecommendedListPanel, requirementPanels, quest.getItemRecommended());

		/* Combat requirements */
		updateCombatRequirementsPanels(quest.getCombatRequirements());

		/* External Resources */
		updateExternalResourcesPanel(quest);

		/* Quest overview */
		updateQuestOverview(quest.getNotes());

		/* Rewards */
		List<Reward> rewards = new ArrayList<>();
		if (quest.getQuestPointReward() != null) rewards.add(quest.getQuestPointReward());
		if (quest.getExperienceRewards() != null) rewards.addAll(quest.getExperienceRewards());
		if (quest.getItemRewards() != null) rewards.addAll(quest.getItemRewards());
		if (quest.getUnlockRewards() != null) rewards.addAll(quest.getUnlockRewards());

		updateRewardsPanels(rewards);
	}

	private void updateRequirementsPanels(JPanel header, JPanel listPanel, List<QuestRequirementPanel> panels,
										  List<Requirement> requirements)
	{
		if (requirements != null)
		{
			for (Requirement generalRecommend : requirements)
			{
				QuestRequirementPanel reqPanel = new QuestRequirementPanel(generalRecommend);
				panels.add(reqPanel);
				listPanel.add(new QuestRequirementWrapperPanel(reqPanel));

				listPanel.setVisible(true);
				header.setVisible(true);
			}
		}
		else
		{
			listPanel.setVisible(false);
			header.setVisible(false);
		}
	}

	private void updateItemRequirementsPanels(JPanel listPanel, List<QuestRequirementPanel> panels,
										  List<ItemRequirement> requirements)
	{
		if (requirements != null)
		{
			for (Requirement generalRecommend : requirements)
			{
				QuestRequirementPanel reqPanel = new QuestRequirementPanel(generalRecommend);
				panels.add(reqPanel);
				listPanel.add(new QuestRequirementWrapperPanel(reqPanel));

				listPanel.setVisible(true);
			}
		}
		else
		{
			JLabel itemRequiredLabel = new JLabel();
			itemRequiredLabel.setForeground(Color.GRAY);
			itemRequiredLabel.setText("None");
			listPanel.add(itemRequiredLabel);
		}
	}

	private void updateRewardsPanels(List<Reward> rewards)
	{
		Reward lastReward = null;
		if (rewards != null)
		{
			for (Reward reward : rewards)
			{
				if (lastReward != null && lastReward.rewardType() != reward.rewardType())
				{
					questRewardPanel.add(new JLabel(" "));
				}
				lastReward = reward;

				QuestRewardPanel rewardPanel = new QuestRewardPanel(reward);
				questRewardPanel.add(new QuestRewardWrapperPanel(rewardPanel));

				questRewardPanel.setVisible(true);
			}
		}
		else
		{
			JLabel itemRequiredLabel = new JLabel();
			itemRequiredLabel.setForeground(Color.GRAY);
			itemRequiredLabel.setText("None");
			questRewardPanel.add(itemRequiredLabel);
		}
	}

	private void updateCombatRequirementsPanels(List<String> combatRequirementList)
	{
		JLabel combatLabel = new JLabel();
		combatLabel.setForeground(Color.GRAY);
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
	}

	private void updateExternalResourcesPanel(QuestHelper quest)
	{
		List<ExternalQuestResources> externalResourcesList = Collections.singletonList(ExternalQuestResources.valueOf(quest.getQuest().name().toUpperCase()));
		JLabel externalResources = new JLabel();
		externalResources.setForeground(Color.GRAY);
		JButton wikiBtn = new JButton();

		//Button constant properties
		wikiBtn.setUI(new BasicButtonUI());
		SwingUtil.removeButtonDecorations(wikiBtn);
		wikiBtn.setHorizontalAlignment(SwingConstants.LEFT);
		wikiBtn.setBackground(ColorScheme.DARK_GRAY_COLOR);
		wikiBtn.setToolTipText("Open the official wiki in your browser.");

		//Button variable properties
		wikiBtn.setText("<html><body>" + quest.getQuest().getName() + " Wiki </body></html>");

		wikiBtn.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				wikiBtn.setForeground(Color.blue.brighter().brighter().brighter());
				wikiBtn.setText("<html><body style = 'text-decoration:underline'>" + quest.getQuest().getName() + " Wiki </body></html>");
			}

			public void mouseExited(java.awt.event.MouseEvent evt) {
				wikiBtn.setForeground(Color.white);
				wikiBtn.setText("<html><body>" + quest.getQuest().getName() + " Wiki </body></html>");
			}
		});

		//Access URL values from ExternalQuestResources enum class
		for (ExternalQuestResources externalResource : externalResourcesList) {
			if (externalResource.getWikiURL().length() > 0) {
				wikiBtn.addActionListener((ev) -> LinkBrowser.browse(externalResource.getWikiURL()));
			}
		}

		externalQuestResourcesPanel.removeAll();
		externalQuestResourcesPanel.add(externalResources);
		externalQuestResourcesPanel.add(wikiBtn, BorderLayout.EAST);
	}

	private void updateQuestOverview(List<String> notes)
	{
		JLabel overviewLabel = new JLabel();
		overviewLabel.setForeground(Color.GRAY);
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
			questNoteHeader.setVisible(true);
			questOverviewNotes.setVisible(true);
		}
		else
		{
			questOverviewNotes.setVisible(false);
			questNoteHeader.setVisible(false);
			questOverviewNotesPanel.setVisible(false);
		}
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(PluginPanel.PANEL_WIDTH, super.getPreferredSize().height);
	}

	public void updateRequirements(Client client, List<Item> bankItems)
	{
		updateRequirementPanels(client, requirementPanels, bankItems);

		if (questStepPanelList != null)
		{
			for (QuestStepPanel questStepPanel : questStepPanelList)
			{
				questStepPanel.updateRequirements(client, bankItems, this);
			}
		}
		revalidate();
	}

	public void updateRequirementPanels(Client client, List<QuestRequirementPanel> reqPanels, List<Item> bankItems)
	{
		if (reqPanels == null)
		{
			return;
		}
		for (QuestRequirementPanel requirementPanel : reqPanels)
		{
			Color newColor;

			if (requirementPanel.getRequirement() instanceof ItemRequirement)
			{
				ItemRequirement itemRequirement = (ItemRequirement) requirementPanel.getRequirement();

				requirementPanel.setVisible(itemRequirement.getConditionToHide() == null || !itemRequirement.getConditionToHide().check(client));

				if (itemRequirement instanceof NoItemRequirement)
				{
					newColor = itemRequirement.getColor(client, questHelperPlugin.getConfig()); // explicitly call this because
					// NoItemRequirement overrides it
				}
				else
				{
					newColor = itemRequirement.getColorConsideringBank(client, false, bankItems, questHelperPlugin.getConfig());
				}
			}
			else
			{
				newColor = requirementPanel.getRequirement().getColor(client, questHelperPlugin.getConfig());
			}

			if (newColor == Color.WHITE)
			{
				requirementPanel.getLabel().setToolTipText("In bank");
			}
			else
			{
				requirementPanel.getLabel().setToolTipText("");
			}

			requirementPanel.getLabel().setForeground(newColor);
		}
	}
}
