package com.questhelper.panel.questmaking;

import com.questhelper.questimport.QuestStepData;
import com.questhelper.questimport.RequirementData;
import com.questhelper.questimport.StepData;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class QuestAssemblyPanel extends JPanel
{
	private final DataModel dataModel;
	private JList<QuestStepData> questStepList;
	private JComboBox<StepData> stepComboBox;
	private JComboBox<RequirementData>[] conditionalRequirementSelectors;

	private JButton createQuestStepButton;
	private JButton saveChangesButton;
	private JButton removeQuestStepButton;
	private JButton moveUpButton;
	private JButton moveDownButton;

	private QuestStepData selectedQuestStep;

	public QuestAssemblyPanel(DataModel dataModel)
	{
		this.dataModel = dataModel;
		setLayout(new BorderLayout());
		initializeUI();
	}

	private void initializeUI()
	{
		// Split the panel into two: list on the left, editing on the right
		setLayout(new BorderLayout());

		// Left panel with the quest steps list and move buttons
		JPanel leftPanel = new JPanel(new BorderLayout());
		questStepList = new JList<>(dataModel.getQuestStepListModel());
		questStepList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		questStepList.addListSelectionListener(e -> onQuestStepSelected());

		// Move buttons
		JPanel moveButtonsPanel = new JPanel(new GridLayout(1, 2));
		moveUpButton = new JButton("Move Up");
		moveDownButton = new JButton("Move Down");
		moveButtonsPanel.add(moveUpButton);
		moveButtonsPanel.add(moveDownButton);

		moveUpButton.addActionListener(e -> moveQuestStepUp());
		moveDownButton.addActionListener(e -> moveQuestStepDown());

		leftPanel.add(new JScrollPane(questStepList), BorderLayout.CENTER);
		leftPanel.add(moveButtonsPanel, BorderLayout.SOUTH);

		// Right panel with editing fields
		JPanel rightPanel = new JPanel(new BorderLayout());

		// Editing fields panel
		JPanel editingPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;

		// Step selection
		editingPanel.add(new JLabel("Select Step:"), gbc);
		gbc.gridx++;
		stepComboBox = new JComboBox<>();
		updateStepComboBox();
		editingPanel.add(stepComboBox, gbc);

		// Conditional Requirements
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		editingPanel.add(new JLabel("Conditional Requirements:"), gbc);

		int requirementSlots = 5;
		conditionalRequirementSelectors = new JComboBox[requirementSlots];
		for (int i = 0; i < requirementSlots; i++)
		{
			gbc.gridx = 0;
			gbc.gridy++;
			gbc.gridwidth = 1;
			editingPanel.add(new JLabel("Requirement " + (i + 1) + ":"), gbc);

			gbc.gridx++;
			conditionalRequirementSelectors[i] = new JComboBox<>();
			editingPanel.add(conditionalRequirementSelectors[i], gbc);
		}

		updateConditionalRequirementSelectors();

		// Buttons panel
		JPanel buttonsPanel = new JPanel();
		createQuestStepButton = new JButton("Create Quest Step");
		saveChangesButton = new JButton("Save Changes");
		removeQuestStepButton = new JButton("Remove Quest Step");

		buttonsPanel.add(createQuestStepButton);
		buttonsPanel.add(saveChangesButton);
		buttonsPanel.add(removeQuestStepButton);

		createQuestStepButton.addActionListener(e -> createQuestStep());
		saveChangesButton.addActionListener(e -> saveChanges());
		removeQuestStepButton.addActionListener(e -> removeQuestStep());

		// Initially disable editing fields and buttons
		setEditingFieldsEnabled(false);
		saveChangesButton.setEnabled(false);
		removeQuestStepButton.setEnabled(false);

		rightPanel.add(editingPanel, BorderLayout.CENTER);
		rightPanel.add(buttonsPanel, BorderLayout.SOUTH);

		// Add left and right panels to the main panel
		add(leftPanel, BorderLayout.WEST);
		add(rightPanel, BorderLayout.CENTER);

		// Register as a listener for data model changes
		dataModel.addStepChangeListener(this::updateStepComboBox);
		dataModel.addRequirementChangeListener(this::updateConditionalRequirementSelectors);
	}

	private void createQuestStep()
	{
		// Create a new QuestStepData object
		QuestStepData newQuestStep = new QuestStepData();
		newQuestStep.setConditionalRequirements(new ArrayList<>());

		// Add the new quest step to the list model
		dataModel.getQuestStepListModel().addElement(newQuestStep);

		// Select the new quest step in the list
		questStepList.setSelectedValue(newQuestStep, true);
	}

	private void onQuestStepSelected()
	{
		selectedQuestStep = questStepList.getSelectedValue();
		if (selectedQuestStep != null)
		{
			// Enable editing fields
			setEditingFieldsEnabled(true);

			// Enable Save Changes and Remove Quest Step buttons
			saveChangesButton.setEnabled(true);
			removeQuestStepButton.setEnabled(true);

			// Populate fields with selected quest step's data
			populateFieldsWithQuestStepData(selectedQuestStep);
		}
		else
		{
			// Disable editing fields
			setEditingFieldsEnabled(false);

			// Disable Save Changes and Remove Quest Step buttons
			saveChangesButton.setEnabled(false);
			removeQuestStepButton.setEnabled(false);

			// Clear the fields
			clearEditingFields();
		}
	}

	private void saveChanges()
	{
		if (selectedQuestStep == null)
		{
			JOptionPane.showMessageDialog(this, "No quest step selected to save.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Update associated step
		StepData selectedStep = (StepData) stepComboBox.getSelectedItem();
		if (selectedStep == null)
		{
			JOptionPane.showMessageDialog(this, "Please select a step.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		selectedQuestStep.setStepId(selectedStep.getId());

		// Update conditional requirements
		List<String> conditionalRequirements = new ArrayList<>();
		for (JComboBox<RequirementData> selector : conditionalRequirementSelectors)
		{
			RequirementData selectedRequirement = (RequirementData) selector.getSelectedItem();
			if (selectedRequirement != null)
			{
				String reqId = selectedRequirement.getId();
				if (conditionalRequirements.contains(reqId))
				{
					JOptionPane.showMessageDialog(this, "Duplicate conditional requirements selected.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				conditionalRequirements.add(reqId);
			}
		}
		selectedQuestStep.setConditionalRequirements(conditionalRequirements);

		// Update the quest step in the list model
		questStepList.repaint();
	}

	private void removeQuestStep()
	{
		if (selectedQuestStep != null)
		{
			int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove the selected quest step?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION)
			{
				dataModel.getQuestStepListModel().removeElement(selectedQuestStep);
				selectedQuestStep = null;

				// Disable editing fields and buttons
				setEditingFieldsEnabled(false);
				saveChangesButton.setEnabled(false);
				removeQuestStepButton.setEnabled(false);

				// Clear the fields
				clearEditingFields();
			}
		}
	}

	private void moveQuestStepUp()
	{
		int selectedIndex = questStepList.getSelectedIndex();
		if (selectedIndex > 0)
		{
			QuestStepData questStep = dataModel.getQuestStepListModel().getElementAt(selectedIndex);
			dataModel.getQuestStepListModel().remove(selectedIndex);
			dataModel.getQuestStepListModel().add(selectedIndex - 1, questStep);
			questStepList.setSelectedIndex(selectedIndex - 1);
		}
	}

	private void moveQuestStepDown()
	{
		int selectedIndex = questStepList.getSelectedIndex();
		if (selectedIndex < dataModel.getQuestStepListModel().getSize() - 1)
		{
			QuestStepData questStep = dataModel.getQuestStepListModel().getElementAt(selectedIndex);
			dataModel.getQuestStepListModel().remove(selectedIndex);
			dataModel.getQuestStepListModel().add(selectedIndex + 1, questStep);
			questStepList.setSelectedIndex(selectedIndex + 1);
		}
	}

	private void setEditingFieldsEnabled(boolean enabled)
	{
		stepComboBox.setEnabled(enabled);
		for (JComboBox<RequirementData> selector : conditionalRequirementSelectors)
		{
			selector.setEnabled(enabled);
		}
	}

	private void populateFieldsWithQuestStepData(QuestStepData questStep)
	{
		// Set the selected step
		StepData associatedStep = null;
		String stepId = questStep.getStepId();
		if (stepId != null)
		{
			for (int i = 0; i < dataModel.getStepListModel().size(); i++)
			{
				StepData stepData = dataModel.getStepListModel().getElementAt(i);
				if (stepData.getId().equals(stepId))
				{
					associatedStep = stepData;
					break;
				}
			}
		}
		stepComboBox.setSelectedItem(associatedStep);

		// Set the conditional requirements
		List<String> condReqIds = questStep.getConditionalRequirements();
		for (int i = 0; i < conditionalRequirementSelectors.length; i++)
		{
			JComboBox<RequirementData> selector = conditionalRequirementSelectors[i];
			RequirementData selectedRequirement = null;
			if (condReqIds != null && i < condReqIds.size())
			{
				String reqId = condReqIds.get(i);
				for (int j = 0; j < dataModel.getRequirementListModel().size(); j++)
				{
					RequirementData reqData = dataModel.getRequirementListModel().getElementAt(j);
					if (reqData.getId().equals(reqId))
					{
						selectedRequirement = reqData;
						break;
					}
				}
			}
			selector.setSelectedItem(selectedRequirement);
		}
	}

	private void clearEditingFields()
	{
		stepComboBox.setSelectedIndex(0);
		for (JComboBox<RequirementData> selector : conditionalRequirementSelectors)
		{
			selector.setSelectedIndex(0);
		}
	}

	private void updateStepComboBox()
	{
		StepData selected = (StepData) stepComboBox.getSelectedItem();
		StepData[] stepsArray = new StepData[dataModel.getStepListModel().size() + 1];
		stepsArray[0] = null; // Representing "None"
		for (int i = 0; i < dataModel.getStepListModel().size(); i++)
		{
			stepsArray[i + 1] = dataModel.getStepListModel().getElementAt(i);
		}
		stepComboBox.setModel(new DefaultComboBoxModel<>(stepsArray));
		stepComboBox.setSelectedItem(selected);
	}

	private void updateConditionalRequirementSelectors()
	{
		RequirementData[] requirementsArray = new RequirementData[dataModel.getRequirementListModel().size() + 1];
		requirementsArray[0] = null; // Representing "None"
		for (int i = 0; i < dataModel.getRequirementListModel().size(); i++)
		{
			requirementsArray[i + 1] = dataModel.getRequirementListModel().getElementAt(i);
		}

		for (JComboBox<RequirementData> selector : conditionalRequirementSelectors)
		{
			RequirementData selected = (RequirementData) selector.getSelectedItem();
			selector.setModel(new DefaultComboBoxModel<>(requirementsArray));
			selector.setSelectedItem(selected);
		}
	}
}
