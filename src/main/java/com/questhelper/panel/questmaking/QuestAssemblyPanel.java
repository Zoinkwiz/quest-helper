/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.panel.questmaking;

import com.questhelper.panel.JGenerator;
import com.questhelper.questimport.QuestStepData;
import com.questhelper.questimport.RequirementData;
import com.questhelper.questimport.StepData;
import net.runelite.client.ui.ColorScheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class QuestAssemblyPanel extends JPanel
{
	private DataModel dataModel;
	private JList<QuestStepData> questStepList;
	private JPanel questStepDetailsPanel;
	private JComboBox<StepData> stepComboBox;
	private JPanel conditionalRequirementsListPanel;
	private JButton addRequirementButton;
	private QuestStepData selectedQuestStepData;
	private List<JComboBox<RequirementData>> requirementComboBoxes;

	public QuestAssemblyPanel(DataModel dataModel)
	{
		this.dataModel = dataModel;
		this.requirementComboBoxes = new ArrayList<>();
		initializeUI();
		addDataListeners();
	}

	private void initializeUI()
	{
		setLayout(new BorderLayout());

		// Left panel with quest steps list
		JPanel leftPanel = new JPanel(new BorderLayout());
		questStepList = new JList<>(dataModel.getQuestStepListModel());
		questStepList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		questStepList.addListSelectionListener(e -> onQuestStepSelected());
		JScrollPane questStepListScrollPane = new JScrollPane(questStepList);
		leftPanel.add(questStepListScrollPane, BorderLayout.CENTER);

		// Buttons to add/remove quest steps
		JPanel questStepButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//		questStepButtonsPanel.setPreferredSize();
		JButton addQuestStepButton = new JButton("Add Quest Step");
		addQuestStepButton.addActionListener(e -> addQuestStep());
		JButton removeQuestStepButton = new JButton("Remove Quest Step");
		removeQuestStepButton.addActionListener(e -> removeQuestStep());
		JButton moveUpButton = new JButton("Move Up");
		moveUpButton.addActionListener(e -> moveQuestStepUp());
		JButton moveDownButton = new JButton("Move Down");
		moveDownButton.addActionListener(e -> moveQuestStepDown());

		// Save changes button
		JButton saveChangesButton = new JButton("Save Changes");
		saveChangesButton.addActionListener(e -> saveChanges());

		questStepButtonsPanel.add(addQuestStepButton);
		questStepButtonsPanel.add(removeQuestStepButton);
		questStepButtonsPanel.add(moveUpButton);
		questStepButtonsPanel.add(moveDownButton);
		questStepButtonsPanel.add(saveChangesButton);

		// Right panel with quest step details
		questStepDetailsPanel = new JPanel();
		questStepDetailsPanel.setLayout(new BoxLayout(questStepDetailsPanel, BoxLayout.Y_AXIS));

		// Step selection
		JPanel stepSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel stepLabel = new JLabel("Select Step:");
		stepComboBox = new JComboBox<>(new DefaultComboBoxModel<>(getStepListData()));
		stepSelectionPanel.add(stepLabel);
		stepSelectionPanel.add(stepComboBox);
		questStepDetailsPanel.add(stepSelectionPanel, BorderLayout.NORTH);

		// Conditional requirements
		JPanel conditionalRequirementsPanel = new JPanel(new BorderLayout());
		conditionalRequirementsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JPanel requirementHeaderPanel = new JPanel(new BorderLayout());

		JTextPane conditionalRequirementsLabel = JGenerator.makeJTextPane("Requirement to continue:");
		conditionalRequirementsLabel.setForeground(Color.WHITE);
		requirementHeaderPanel.add(conditionalRequirementsLabel, BorderLayout.WEST);

		conditionalRequirementsListPanel = new JPanel();
		conditionalRequirementsListPanel.setLayout(new BoxLayout(conditionalRequirementsListPanel, BoxLayout.Y_AXIS));
		conditionalRequirementsListPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		JScrollPane requirementsScrollPane = new JScrollPane(conditionalRequirementsListPanel);

		addRequirementButton = new JButton("Add Requirement");
		addRequirementButton.addActionListener(e -> addConditionalRequirement());
		requirementHeaderPanel.add(addRequirementButton, BorderLayout.AFTER_LINE_ENDS);

//		JPanel requirementsButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		conditionalRequirementsPanel.add(requirementHeaderPanel, BorderLayout.NORTH);
//		conditionalRequirementsPanel.add(requirementsButtonsPanel, BorderLayout.CENTER);
		conditionalRequirementsPanel.add(requirementsScrollPane, BorderLayout.CENTER);

		questStepDetailsPanel.add(conditionalRequirementsPanel, BorderLayout.CENTER);
//		questStepDetailsPanel.add(saveChangesButton);

		JSplitPane sl = new JSplitPane(JSplitPane.VERTICAL_SPLIT, leftPanel, questStepDetailsPanel);
		sl.setOrientation(SwingConstants.VERTICAL);

		add(sl, BorderLayout.CENTER);
		add(questStepButtonsPanel, BorderLayout.SOUTH);

		// Disable editing fields initially
		setEditingFieldsEnabled(false);
	}

	private void addDataListeners()
	{
		dataModel.getStepListModel().addListDataListener(new ListDataListener()
		{
			@Override
			public void intervalAdded(ListDataEvent e)
			{
				updateStepComboBox();
			}

			@Override
			public void intervalRemoved(ListDataEvent e)
			{
				updateStepComboBox();
			}

			@Override
			public void contentsChanged(ListDataEvent e)
			{
				updateStepComboBox();
			}
		});

		dataModel.getRequirementListModel().addListDataListener(new ListDataListener()
		{
			@Override
			public void intervalAdded(ListDataEvent e)
			{
				updateRequirementComboBoxes();
			}

			@Override
			public void intervalRemoved(ListDataEvent e)
			{
				updateRequirementComboBoxes();
			}

			@Override
			public void contentsChanged(ListDataEvent e)
			{
				updateRequirementComboBoxes();
			}
		});
	}

	private void onQuestStepSelected()
	{
		selectedQuestStepData = questStepList.getSelectedValue();
		if (selectedQuestStepData != null)
		{
			setEditingFieldsEnabled(true);
			populateQuestStepDetails(selectedQuestStepData);
		}
		else
		{
			setEditingFieldsEnabled(false);
			clearQuestStepDetails();
		}
	}

	private void addQuestStep()
	{
		QuestStepData newQuestStepData = new QuestStepData();
		newQuestStepData.setStepData(null);
		newQuestStepData.setConditionalRequirements(new ArrayList<>());
		dataModel.getQuestStepListModel().addElement(newQuestStepData);
		questStepList.setSelectedValue(newQuestStepData, true);
	}

	private void removeQuestStep()
	{
		if (selectedQuestStepData != null)
		{
			int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove the selected quest step?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION)
			{
				dataModel.getQuestStepListModel().removeElement(selectedQuestStepData);
				selectedQuestStepData = null;
				setEditingFieldsEnabled(false);
				clearQuestStepDetails();
			}
		}
	}

	private void moveQuestStepUp()
	{
		int index = questStepList.getSelectedIndex();
		if (index > 0)
		{
			DefaultListModel<QuestStepData> model = dataModel.getQuestStepListModel();
			QuestStepData questStepData = model.getElementAt(index);
			model.removeElementAt(index);
			model.add(index - 1, questStepData);
			questStepList.setSelectedIndex(index - 1);
		}
	}

	private void moveQuestStepDown()
	{
		int index = questStepList.getSelectedIndex();
		DefaultListModel<QuestStepData> model = dataModel.getQuestStepListModel();
		if (index >= 0 && index < model.size() - 1)
		{
			QuestStepData questStepData = model.getElementAt(index);
			model.removeElementAt(index);
			model.add(index + 1, questStepData);
			questStepList.setSelectedIndex(index + 1);
		}
	}

	private void setEditingFieldsEnabled(boolean enabled)
	{
		stepComboBox.setEnabled(enabled);
		addRequirementButton.setEnabled(enabled);
		for (JComboBox<RequirementData> comboBox : requirementComboBoxes)
		{
			comboBox.setEnabled(enabled);
		}
	}

	private void populateQuestStepDetails(QuestStepData questStepData)
	{
		stepComboBox.setSelectedItem(questStepData.getStepData());

		// Clear existing requirement rows
		conditionalRequirementsListPanel.removeAll();
		requirementComboBoxes.clear();

		// Populate conditional requirements
		for (RequirementData reqData : questStepData.getConditionalRequirements())
		{
			if (reqData != null)
			{
				addRequirementRow(reqData);
			}
		}

		// Refresh the panel
		conditionalRequirementsListPanel.revalidate();
		conditionalRequirementsListPanel.repaint();
	}

	private void clearQuestStepDetails()
	{
		stepComboBox.setSelectedIndex(-1);
		conditionalRequirementsListPanel.removeAll();
		requirementComboBoxes.clear();
		conditionalRequirementsListPanel.revalidate();
		conditionalRequirementsListPanel.repaint();
	}

	private void saveChanges()
	{
		if (selectedQuestStepData != null)
		{
			// Save selected step
			StepData selectedStep = (StepData) stepComboBox.getSelectedItem();
			if (selectedStep != null)
			{
				selectedQuestStepData.setStepData(selectedStep);
			}
			else
			{
				JOptionPane.showMessageDialog(this, "Please select a step.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Save conditional requirements
			List<RequirementData> reqDatum = new ArrayList<>();
			for (JComboBox<RequirementData> comboBox : requirementComboBoxes)
			{
				RequirementData reqData = (RequirementData) comboBox.getSelectedItem();
				if (reqData != null)
				{
					reqDatum.add(reqData);
				}
			}
			selectedQuestStepData.setConditionalRequirements(reqDatum);

			// Refresh the quest step list
			questStepList.repaint();
		}
	}

	private void addConditionalRequirement()
	{
		addRequirementRow(null);
	}

	private void addRequirementRow(RequirementData selectedRequirement)
	{
		JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JComboBox<RequirementData> requirementComboBox = new JComboBox<>(new DefaultComboBoxModel<>(getRequirementListData()));
		if (selectedRequirement != null)
		{
			requirementComboBox.setSelectedItem(selectedRequirement);
		}
		JButton removeButton = new JButton("Remove");
		removeButton.addActionListener(e -> removeRequirementRow(rowPanel, requirementComboBox));

		rowPanel.add(requirementComboBox);
		rowPanel.add(removeButton);

		requirementComboBoxes.add(requirementComboBox);
		conditionalRequirementsListPanel.add(rowPanel);

		conditionalRequirementsListPanel.revalidate();
		conditionalRequirementsListPanel.repaint();
	}

	private void removeRequirementRow(JPanel rowPanel, JComboBox<RequirementData> requirementComboBox)
	{
		conditionalRequirementsListPanel.remove(rowPanel);
		requirementComboBoxes.remove(requirementComboBox);

		conditionalRequirementsListPanel.revalidate();
		conditionalRequirementsListPanel.repaint();
	}

	private void updateStepComboBox()
	{
		SwingUtilities.invokeLater(() ->
		{
			StepData selectedStep = (StepData) stepComboBox.getSelectedItem();
			stepComboBox.setModel(new DefaultComboBoxModel<>(getStepListData()));
			if (selectedStep != null)
			{
				stepComboBox.setSelectedItem(selectedStep);
			}
		});
	}

	private void updateRequirementComboBoxes()
	{
		SwingUtilities.invokeLater(() ->
		{
			RequirementData[] requirementsArray = getRequirementListData();
			for (JComboBox<RequirementData> comboBox : requirementComboBoxes)
			{
				RequirementData selectedReq = (RequirementData) comboBox.getSelectedItem();
				comboBox.setModel(new DefaultComboBoxModel<>(requirementsArray));
				if (selectedReq != null)
				{
					comboBox.setSelectedItem(selectedReq);
				}
			}
		});
	}

	private StepData[] getStepListData()
	{
		StepData[] stepsArray = new StepData[dataModel.getStepListModel().getSize()];
		for (int i = 0; i < dataModel.getStepListModel().getSize(); i++)
		{
			stepsArray[i] = dataModel.getStepListModel().getElementAt(i);
		}
		return stepsArray;
	}

	private RequirementData[] getRequirementListData()
	{
		RequirementData[] requirementsArray = new RequirementData[dataModel.getRequirementListModel().getSize()];
		for (int i = 0; i < dataModel.getRequirementListModel().getSize(); i++)
		{
			requirementsArray[i] = dataModel.getRequirementListModel().getElementAt(i);
		}
		return requirementsArray;
	}
}
