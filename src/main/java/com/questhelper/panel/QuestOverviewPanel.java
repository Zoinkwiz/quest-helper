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

import com.questhelper.managers.QuestManager;
import com.questhelper.questinfo.ExternalQuestResources;
import com.questhelper.questinfo.HelperConfig;
import com.questhelper.tools.Icon;
import com.questhelper.QuestHelperConfig;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestPointRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import java.awt.event.ItemEvent;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.questhelper.util.Fonts;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.ui.ColorScheme;
import static net.runelite.client.ui.PluginPanel.PANEL_WIDTH;
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
import org.apache.commons.lang3.StringUtils;

public class QuestOverviewPanel extends JPanel
{
	@Getter
	private final QuestHelperPlugin questHelperPlugin;
	private final QuestManager questManager;

	public QuestHelper currentQuest;

	private final QuestRequirementsPanel questGeneralRequirementsPanel;
	private final QuestRequirementsPanel questGeneralRecommendedPanel;
	private final QuestRequirementsPanel questItemRequirementsPanel;
	private final QuestRequirementsPanel questItemRecommendedPanel;
	private final JPanel questCombatRequirementsListPanel;
	private final JPanel questNotesPanel;
	private final JPanel questNotesList;
	private final QuestRewardsPanel questRewardsPanel;
	private final JPanel questExternalResourcesList;

	private final JPanel questStepsContainer = new JPanel();
	private final JPanel actionsContainer = new JPanel();
	private final JPanel configContainer = new JPanel();

	private final JPanel introPanel = new JPanel();

	private final JLabel questNameLabel = JGenerator.makeJLabel();

	private static final ImageIcon CLOSE_ICON = Icon.CLOSE.getIcon();

	private final JButton collapseBtn = new JButton();

	private final List<QuestStepPanel> questStepPanelList = new CopyOnWriteArrayList<>();

	public QuestOverviewPanel(QuestHelperPlugin questHelperPlugin, QuestManager questManager)
	{
		super();
		this.questManager = questManager;
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
		final JPanel leftTitleContainer = new JPanel(new BorderLayout(5, 0));
		leftTitleContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		leftTitleContainer.add(questNameLabel, BorderLayout.CENTER);

		actionsContainer.add(leftTitleContainer, BorderLayout.WEST);

		/* Quest config panel */
		configContainer.setLayout(new BorderLayout());
		configContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		configContainer.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(5, 0, 0, 0, ColorScheme.DARK_GRAY_COLOR),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)
		));
		BoxLayout boxLayoutOverview2 = new BoxLayout(configContainer, BoxLayout.Y_AXIS);
		configContainer.setLayout(boxLayoutOverview2);
		configContainer.setVisible(false);

		JPanel configHeaderPanel = new JPanel();
		configHeaderPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		configHeaderPanel.setLayout(new BorderLayout());
		configHeaderPanel.setBorder(new EmptyBorder(5, 5, 5, 10));
		JTextArea configHeaderText = JGenerator.makeJTextArea();
		configHeaderText.setForeground(Color.WHITE);
		configHeaderText.setText("Configuration:");
		configHeaderText.setMinimumSize(new Dimension(1, configHeaderPanel.getPreferredSize().height));
		configHeaderPanel.add(configHeaderText);
		configContainer.add(configHeaderPanel);

		/* Quest overview panel */
		introPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(5, 0, 0, 0, ColorScheme.DARK_GRAY_COLOR),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)
		));
		introPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		introPanel.setLayout(new BorderLayout());
		introPanel.setVisible(false);

		/* Panel for all overview details */
		final JPanel overviewPanel = new JPanel();
		BoxLayout boxLayoutOverview = new BoxLayout(overviewPanel, BoxLayout.Y_AXIS);
		overviewPanel.setLayout(boxLayoutOverview);

		questGeneralRequirementsPanel = new QuestRequirementsPanel("General requirements:", null, questManager, false);
		overviewPanel.add(questGeneralRequirementsPanel);

		questGeneralRecommendedPanel = new QuestRequirementsPanel("Recommended:", null, questManager, false);
		overviewPanel.add(questGeneralRecommendedPanel);

		questItemRequirementsPanel = new QuestRequirementsPanel("Item requirements:", null, questManager, true);
		overviewPanel.add(questItemRequirementsPanel);

		questItemRecommendedPanel = new QuestRequirementsPanel("Recommended items:", null, questManager, true);
		overviewPanel.add(questItemRecommendedPanel);

		var combatRequirements = QuestRequirementsPanel.createGenericGroup("Enemies to defeat:");
		questCombatRequirementsListPanel = combatRequirements.getRight();
		overviewPanel.add(combatRequirements.getLeft());

		var notes = QuestRequirementsPanel.createGenericGroup("Notes:");
		questNotesPanel = notes.getLeft();
		questNotesList = notes.getRight();
		overviewPanel.add(questNotesPanel);

		questRewardsPanel = new QuestRewardsPanel();
		overviewPanel.add(questRewardsPanel);

		var externalResources = QuestRequirementsPanel.createGenericGroup("External Resources:");
		questExternalResourcesList = externalResources.getRight();
		overviewPanel.add(externalResources.getLeft());

		introPanel.add(overviewPanel, BorderLayout.NORTH);

		/* Container for quest steps */
		questStepsContainer.setLayout(new BoxLayout(questStepsContainer, BoxLayout.Y_AXIS));

		add(actionsContainer);
		add(configContainer);
		add(introPanel);
		add(questStepsContainer);
	}

	private JComboBox<Enum> makeNewDropdown(Enum[] values, String key)
	{
		JComboBox<Enum> dropdown = new JComboBox<>(values);
		dropdown.setFocusable(false);
		dropdown.setForeground(Color.WHITE);
		dropdown.setRenderer(new DropdownRenderer());
		dropdown.addItemListener(e ->
		{
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				Enum source = (Enum) e.getItem();
				questHelperPlugin.getConfigManager().setRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, key,
					source);
			}
		});
		String currentVal = questHelperPlugin.getConfigManager().getRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, key);
		for (Enum value : values)
		{
			if (value.name().equals(currentVal))
			{
				dropdown.setSelectedItem(value);
			}
		}

		return dropdown;
	}

	private JPanel makeDropdownPanel(JComboBox dropdown, String name)
	{
		// Filters
		JTextArea filterName = JGenerator.makeJTextArea(name);
		filterName.setForeground(Color.WHITE);

		JPanel filtersPanel = new JPanel();
		filtersPanel.setLayout(new BorderLayout());
		filtersPanel.setMinimumSize(new Dimension(PANEL_WIDTH, 0));
		filtersPanel.add(filterName, BorderLayout.CENTER);
		filtersPanel.add(dropdown, BorderLayout.EAST);

		return filtersPanel;
	}

	public void addQuest(QuestHelper quest, boolean isActive)
	{
		currentQuest = quest;
		questStepPanelList.clear();

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
			questNameLabel.setText(quest.getName());
			actionsContainer.setVisible(true);

			if (quest.getConfigs() != null)
			{
				configContainer.setVisible(true);
			}

			setupQuestRequirements(quest);
			introPanel.setVisible(true);

			for (PanelDetails panelDetail : steps)
			{
				var newStep = new QuestStepPanel(panelDetail, currentStep, questManager, questHelperPlugin);
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

	public void updateStepsTexts()
	{
		questStepPanelList.forEach(panel -> {
			for (QuestStep step : panel.getSteps())
			{
				JTextPane label = panel.getStepsLabels().get(step);
				if (label != null)
				{
					var newText = panel.generateText(step);
					var oldText = label.getText();
					if (newText == null)
					{
						continue;
					}

					if (!newText.equals(oldText))
					{
						label.setText(panel.generateText(step));
					}
				}
			}
		});
	}

	public void updateHighlight(Client client, QuestStep newStep)
	{
		if (currentQuest == null) return;
		questStepPanelList.forEach(panel -> {
			panel.updateHighlightCheck(client, newStep, currentQuest);
		});

		repaint();
		revalidate();
	}

	public void updateLocks()
	{
		questStepPanelList.forEach(QuestStepPanel::updateLock);
	}

	public void removeQuest()
	{
		actionsContainer.setVisible(false);
		introPanel.setVisible(false);
		configContainer.setVisible(false);
		configContainer.removeAll();
		questStepsContainer.removeAll();
		questGeneralRequirementsPanel.setRequirements(null);
		questGeneralRecommendedPanel.setRequirements(null);
		questItemRequirementsPanel.setRequirements(null);
		questItemRecommendedPanel.setRequirements(null);
		questCombatRequirementsListPanel.removeAll();
		currentQuest = null;
		questNotesList.removeAll();
		questRewardsPanel.setRewards(null);
		repaint();
		revalidate();
	}

	/// The quest helper's X is clicked
	private void closeHelper()
	{
		questManager.shutDownQuest(false);
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
		/* Config setup */
		if (quest.getConfigs() != null)
		{
			JPanel configHeaderPanel = new JPanel();
			configHeaderPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			configHeaderPanel.setLayout(new BorderLayout());
			configHeaderPanel.setBorder(new EmptyBorder(5, 5, 5, 10));
			JTextArea configHeaderText = JGenerator.makeJTextArea();
			configHeaderText.setForeground(Color.WHITE);
			configHeaderText.setText("Configuration:");
			configHeaderText.setMinimumSize(new Dimension(1, configHeaderPanel.getPreferredSize().height));
			configHeaderPanel.add(configHeaderText);
			configContainer.add(configHeaderPanel);

			List<HelperConfig> configs = quest.getConfigs();
			for (HelperConfig config : configs)
			{
				var dropdown = makeNewDropdown(config.getEnums(), config.getKey());
				JPanel dropdownPanel = makeDropdownPanel(dropdown, config.getName());
				dropdownPanel.setPreferredSize(new Dimension(PANEL_WIDTH, QuestHelperPanel.DROPDOWN_HEIGHT));

				configContainer.add(dropdownPanel);
			}
		}

		if (questHelperPlugin.getConfig().showFullRequirements())
		{
			/* Non-item requirements */
			questGeneralRequirementsPanel.setRequirements(getAggregatedRequirements(quest));
		}
		else
		{
			questGeneralRequirementsPanel.setRequirements(quest.getGeneralRequirements());
		}

		/* Non-item recommended */
		questGeneralRecommendedPanel.setRequirements(quest.getGeneralRecommended());

		/* Required items */
		questItemRequirementsPanel.setRequirements(quest.getItemRequirements());

		/* Recommended items */
		questItemRecommendedPanel.setRequirements(quest.getItemRecommended());

		/* Combat requirements */
		updateCombatRequirementsPanels(quest.getCombatRequirements());

		/* External Resources */
		updateExternalResourcesPanel(quest);

		/* Quest overview */
		updateQuestNotes(quest.getNotes());

		/* Rewards */
		if (questHelperPlugin.getConfig().hideQuestRewards()) {
			questRewardsPanel.hideRewards();
		} else {
			questRewardsPanel.setRewards(quest.getQuestRewards());
		}
	}

	private static void collectRequirements(QuestHelper quest, List<Requirement> allRequirements, Set<String> processedQuestIds)
	{
		if (quest.getQuest().getQuestHelper().getGeneralRequirements() == null) return;

		List<Requirement> generalRequirements = quest.getQuest().getQuestHelper().getGeneralRequirements();
		for (Requirement requirement : generalRequirements)
		{
			if (requirement instanceof QuestRequirement)
			{
				QuestRequirement subQuest = ((QuestRequirement) requirement);
				String questId = subQuest.getQuest().getName();
				if (processedQuestIds.contains(questId))
				{
					continue;  // Skip processing if this quest has already been processed
				}
				processedQuestIds.add(questId);  // Mark this quest as processed

				allRequirements.add(requirement);

				collectRequirements(subQuest.getQuest().getQuestHelper(), allRequirements, processedQuestIds);
			}
			else
			{
				// Avoid adding duplicate requirements
				if (!allRequirements.contains(requirement))
				{
					allRequirements.add(requirement);
				}
			}
		}
	}

	public static List<Requirement> getAllRequirements(QuestHelper quest)
	{
		List<Requirement> allRequirements = new ArrayList<>();
		Set<String> processedQuestIds = new HashSet<>();
		collectRequirements(quest, allRequirements, processedQuestIds);
		return allRequirements;
	}

	public static List<Requirement> getAggregatedRequirements(QuestHelper quest)
	{
		List<Requirement> allRequirements = getAllRequirements(quest);
		Map<Skill, SkillRequirement> highestSkillRequirements = new HashMap<>();
		int highestQuestPointRequirement = 0;
		List<Requirement> otherRequirements = new ArrayList<>();

		for (Requirement requirement : allRequirements)
		{
			if (requirement instanceof SkillRequirement)
			{
				SkillRequirement skillRequirement = (SkillRequirement) requirement;
				Skill skill = skillRequirement.getSkill();
				SkillRequirement existingHighest = highestSkillRequirements.get(skill);
				if (existingHighest == null || skillRequirement.getRequiredLevel() > existingHighest.getRequiredLevel())
				{
					highestSkillRequirements.put(skill, skillRequirement);
				}
			}
			else if (requirement instanceof QuestPointRequirement)
			{
				QuestPointRequirement questPointRequirement = (QuestPointRequirement) requirement;
				if (highestQuestPointRequirement == 0 || questPointRequirement.getRequiredQuestPoints() > highestQuestPointRequirement)
				{
					highestQuestPointRequirement = questPointRequirement.getRequiredQuestPoints();
				}
			}
			else
			{
				otherRequirements.add(requirement);
			}
		}

		// Combine the highest SkillRequirements and other requirements into a single list
		List<Requirement> aggregatedRequirements = new ArrayList<>(otherRequirements);
		if (highestQuestPointRequirement != 0)
			aggregatedRequirements.add(new QuestPointRequirement(highestQuestPointRequirement));
		aggregatedRequirements.addAll(highestSkillRequirements.values());
		return aggregatedRequirements;
	}

	private void updateCombatRequirementsPanels(List<String> combatRequirementList)
	{
		JTextArea combatLabel = JGenerator.makeJTextArea();
		combatLabel.setForeground(Color.GRAY);
		StringBuilder textCombat = new StringBuilder();
		if (combatRequirementList == null)
		{
			textCombat.append("None");
		}
		else
		{
			var first = true;
			for (var combatRequirement : combatRequirementList)
			{
				if (!first)
				{
					textCombat.append("\n");
				}
				textCombat.append(combatRequirement);
				first = false;
			}
		}
		combatLabel.setText(textCombat.toString());

		questCombatRequirementsListPanel.add(combatLabel);
	}

	private void updateExternalResourcesPanel(QuestHelper quest)
	{
		List<String> externalResourcesList;
		try
		{
			externalResourcesList = Collections.singletonList(ExternalQuestResources.valueOf(quest.getQuest().name().toUpperCase()).getWikiURL());
		} catch (Exception e)
		{
			externalResourcesList = Collections.singletonList("https://oldschool.runescape.wiki/w/" + StringUtils.lowerCase(URLEncoder.encode(quest.getName(), StandardCharsets.UTF_8)));
		}
		JButton wikiBtn = new JButton();

		//Button constant properties
		wikiBtn.setUI(new BasicButtonUI());
		wikiBtn.setBorder(new EmptyBorder(0, 0, 0, 0));
		SwingUtil.removeButtonDecorations(wikiBtn);
		wikiBtn.setHorizontalAlignment(SwingConstants.LEFT);
		wikiBtn.setBackground(ColorScheme.DARK_GRAY_COLOR);
		wikiBtn.setToolTipText("Open the official wiki in your browser.");

		//Button variable properties
		wikiBtn.setText("Open RuneScape Wiki");

		wikiBtn.addMouseListener(new java.awt.event.MouseAdapter()
		{
			public void mouseEntered(java.awt.event.MouseEvent evt)
			{
				wikiBtn.setForeground(Color.cyan.darker());
				wikiBtn.setFont(Fonts.getUnderlinedFont());
			}

			public void mouseExited(java.awt.event.MouseEvent evt)
			{
				wikiBtn.setForeground(Color.white);
				wikiBtn.setFont(Fonts.getOriginalFont());
			}
		});

		//Access URL values from ExternalQuestResources enum class
		for (String externalResource : externalResourcesList)
		{
			wikiBtn.addActionListener((ev) -> LinkBrowser.browse(externalResource));
		}

		questExternalResourcesList.removeAll();
		questExternalResourcesList.add(wikiBtn, BorderLayout.EAST);
	}

	private void updateQuestNotes(List<String> notes)
	{
		if (notes != null)
		{
			var textNote = new StringBuilder();
			var first = true;
			for (var note : notes)
			{
				if (!first)
				{
					textNote.append("\n\n");
				}
				textNote.append(note);
				first = false;
			}
			var overviewLabel = JGenerator.makeJTextArea();
			overviewLabel.setForeground(Color.GRAY);
			overviewLabel.setText(textNote.toString());

			questNotesList.add(overviewLabel);
			questNotesPanel.setVisible(true);
		}
		else
		{
			questNotesPanel.setVisible(false);
		}
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(PANEL_WIDTH, super.getPreferredSize().height);
	}

	public void updateRequirements(Client client)
	{
		questGeneralRequirementsPanel.update(client, questHelperPlugin);
		questGeneralRecommendedPanel.update(client, questHelperPlugin);
		questItemRequirementsPanel.update(client, questHelperPlugin);
		questItemRecommendedPanel.update(client, questHelperPlugin);

		questStepPanelList.forEach((questStepPanel) -> {
			questStepPanel.updateRequirements(client);
		});
		revalidate();
	}
}
