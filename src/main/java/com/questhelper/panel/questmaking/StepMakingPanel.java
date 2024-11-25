package com.questhelper.panel.questmaking;

import com.questhelper.questimport.RequirementData;
import com.questhelper.questimport.StepData;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepMakingPanel extends JPanel
{
	private JComboBox<String> stepTypeComboBox;
	private JPanel parameterPanel;
	private JList<StepData> stepList;
	private DataModel dataModel;
	private StepData selectedStep;
	private Map<String, JTextField> parameterFields;
	private JComboBox<RequirementData>[] requirementSelectors;


	private JButton createStepButton;
	private JButton saveChangesButton;
	private JButton removeStepButton;

	public StepMakingPanel(DataModel dataModel)
	{
		this.dataModel = dataModel;
		this.parameterFields = new HashMap<>();
		setLayout(new BorderLayout());
		initializeUI();

		// Register as a listener for requirement changes
		dataModel.addRequirementChangeListener(this::updateRequirementSelectors);
	}

	private void initializeUI()
	{
		// Top panel for step creation
		JPanel creationPanel = new JPanel(new BorderLayout());

		// Step type selection
		stepTypeComboBox = new JComboBox<>(new String[] { "NpcStep", "ObjectStep" });
		stepTypeComboBox.addActionListener(e -> updateParameterFields());

		// Panel to hold parameter fields
		parameterPanel = new JPanel();
		parameterPanel.setLayout(new BoxLayout(parameterPanel, BoxLayout.Y_AXIS));

		// Step Requirements
		JPanel stepRequirementsPanel = new JPanel();
		stepRequirementsPanel.setLayout(new GridLayout(5, 2));
		stepRequirementsPanel.setBorder(BorderFactory.createTitledBorder("Step Requirements"));

		// Initialize the requirement selectors
		int requirementSlots = 5;
		requirementSelectors = new JComboBox[requirementSlots];
		for (int i = 0; i < requirementSlots; i++)
		{
			stepRequirementsPanel.add(new JLabel("Requirement " + (i + 1) + ":"));
			requirementSelectors[i] = new JComboBox<>();
			stepRequirementsPanel.add(requirementSelectors[i]);
		}

		// Initialize parameter fields
		updateParameterFields();

		// Add components to creation panel
		JPanel topPanel = new JPanel(new FlowLayout());
		topPanel.add(new JLabel("Step Type:"));
		topPanel.add(stepTypeComboBox);
		creationPanel.add(topPanel, BorderLayout.NORTH);
		creationPanel.add(parameterPanel, BorderLayout.CENTER);
		creationPanel.add(stepRequirementsPanel, BorderLayout.EAST);

		// Steps list
		stepList = new JList<>(dataModel.getStepListModel());
		stepList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Add list selection listener
		stepList.addListSelectionListener(e -> onStepSelected());

		// Buttons panel
		JPanel buttonsPanel = new JPanel();
		createStepButton = new JButton("Create Step");
		saveChangesButton = new JButton("Save Changes");
		removeStepButton = new JButton("Remove Step");

		buttonsPanel.add(createStepButton);
		buttonsPanel.add(saveChangesButton);
		buttonsPanel.add(removeStepButton);

		// Add action listeners
		createStepButton.addActionListener(e -> createStep());
		saveChangesButton.addActionListener(e -> saveChanges());
		removeStepButton.addActionListener(e -> removeStep());

		// Initially disable editing fields and buttons
		setEditingFieldsEnabled(false);
		saveChangesButton.setEnabled(false);
		removeStepButton.setEnabled(false);

		updateRequirementSelectors();

		// Assemble the panel
		add(creationPanel, BorderLayout.NORTH);
		add(new JScrollPane(stepList), BorderLayout.CENTER);
		add(buttonsPanel, BorderLayout.SOUTH);
	}

	private void updateRequirementSelectors()
	{
		// Create a new array including "None" option and all requirements
		RequirementData[] requirementsArray = new RequirementData[dataModel.getRequirementListModel().size() + 1];
		requirementsArray[0] = null; // Representing "None"
		for (int i = 0; i < dataModel.getRequirementListModel().size(); i++)
		{
			requirementsArray[i + 1] = dataModel.getRequirementListModel().getElementAt(i);
		}

		// Update each selector
		for (JComboBox<RequirementData> selector : requirementSelectors)
		{
			RequirementData selected = (RequirementData) selector.getSelectedItem();
			selector.setModel(new DefaultComboBoxModel<>(requirementsArray));
			selector.setSelectedItem(selected);
		}
	}

	private void setEditingFieldsEnabled(boolean enabled)
	{
		for (JTextField field : parameterFields.values())
		{
			field.setEnabled(enabled);
		}
		// Enable or disable requirement selectors
		for (JComboBox<RequirementData> selector : requirementSelectors)
		{
			selector.setEnabled(enabled);
		}
	}

	private void updateParameterFields()
	{
		// Preserve the existing parameter values
		Map<String, String> existingValues = new HashMap<>();
		for (Map.Entry<String, JTextField> entry : parameterFields.entrySet())
		{
			existingValues.put(entry.getKey(), entry.getValue().getText());
		}

		parameterPanel.removeAll();
		parameterFields.clear();

		// Step ID field
		parameterPanel.add(new JLabel("Step ID:"));
		JTextField stepIdField = new JTextField(20);
		stepIdField.setEnabled(false); // Step ID should not be editable after creation
		parameterPanel.add(stepIdField);
		parameterFields.put("Step ID", stepIdField);

		String selectedType = (String) stepTypeComboBox.getSelectedItem();

		// Add fields based on the selected step type
		switch (selectedType)
		{
			case "NpcStep":
				parameterPanel.add(new JLabel("NPC ID:"));
				JTextField npcIdField = new JTextField(20);
				parameterPanel.add(npcIdField);
				parameterFields.put("NPC ID", npcIdField);

				parameterPanel.add(new JLabel("Action:"));
				JTextField actionField = new JTextField(20);
				parameterPanel.add(actionField);
				parameterFields.put("Action", actionField);

				parameterPanel.add(new JLabel("Dialog Text (comma-separated):"));
				JTextField dialogTextField = new JTextField(20);
				parameterPanel.add(dialogTextField);
				parameterFields.put("Dialog Text", dialogTextField);
				break;

			case "ObjectStep":
				parameterPanel.add(new JLabel("Object ID:"));
				JTextField objectIdField = new JTextField(20);
				parameterPanel.add(objectIdField);
				parameterFields.put("Object ID", objectIdField);

				parameterPanel.add(new JLabel("Action:"));
				JTextField objectActionField = new JTextField(20);
				parameterPanel.add(objectActionField);
				parameterFields.put("Action", objectActionField);
				break;

			// Add other step types as needed
		}

		// Restore existing values where keys match
		for (Map.Entry<String, JTextField> entry : parameterFields.entrySet())
		{
			String key = entry.getKey();
			String value = existingValues.getOrDefault(key, "");
			entry.getValue().setText(value);
		}

		// Enable or disable editing fields based on whether a step is selected
		boolean enabled = selectedStep != null;
		setEditingFieldsEnabled(enabled);

		parameterPanel.revalidate();
		parameterPanel.repaint();
	}

	private void createStep()
	{
		// Create a new StepData object with a default or unique ID
		String defaultStepId = "step" + (dataModel.getStepListModel().getSize() + 1);
		StepData newStep = new StepData();
		newStep.setId(defaultStepId);
		newStep.setType((String) stepTypeComboBox.getSelectedItem());
		newStep.setParameters(new HashMap<>());
		newStep.setStepRequirements(new ArrayList<>());

		// Add the new step to the list model
		dataModel.getStepListModel().addElement(newStep);

		// Select the new step in the list
		stepList.setSelectedValue(newStep, true);

		for (JComboBox<RequirementData> selector : requirementSelectors)
		{
			selector.setSelectedIndex(0); // Set to "None"
		}
	}

	private void onStepSelected()
	{
		selectedStep = stepList.getSelectedValue();
		if (selectedStep != null)
		{
			// Enable editing fields
			setEditingFieldsEnabled(true);

			// Enable Save Changes and Remove Step buttons
			saveChangesButton.setEnabled(true);
			removeStepButton.setEnabled(true);

			// Populate fields with selected step's data
			populateFieldsWithStepData(selectedStep);
		}
		else
		{
			// Disable editing fields
			setEditingFieldsEnabled(false);

			// Disable Save Changes and Remove Step buttons
			saveChangesButton.setEnabled(false);
			removeStepButton.setEnabled(false);

			// Clear the fields
			clearEditingFields();
		}
	}

	private void populateFieldsWithStepData(StepData step)
	{
		// Set the step ID
		parameterFields.get("Step ID").setText(step.getId());

		// Set the step type
		stepTypeComboBox.setSelectedItem(step.getType());

		// Update parameter fields for the selected type
		updateParameterFields();

		// Set the parameters
		for (Map.Entry<String, Object> entry : step.getParameters().entrySet())
		{
			JTextField field = parameterFields.get(entry.getKey());
			if (field != null)
			{
				field.setText(entry.getValue().toString());
			}
		}

		List<String> stepReqIds = step.getStepRequirements();
		for (int i = 0; i < requirementSelectors.length; i++)
		{
			JComboBox<RequirementData> selector = requirementSelectors[i];
			RequirementData selectedRequirement = null;
			if (stepReqIds != null && i < stepReqIds.size())
			{
				String reqId = stepReqIds.get(i);
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
		for (JTextField field : parameterFields.values())
		{
			field.setText("");
		}
		for (JComboBox<RequirementData> selector : requirementSelectors)
		{
			selector.setSelectedIndex(0); // Set to "None"
		}
	}

	private void saveChanges()
	{
		if (selectedStep == null)
		{
			JOptionPane.showMessageDialog(this, "No step selected to save.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Update step ID
		String newStepId = parameterFields.get("Step ID").getText();
		if (newStepId == null || newStepId.trim().isEmpty())
		{
			JOptionPane.showMessageDialog(this, "Step ID is required.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		selectedStep.setId(newStepId);

		// Update step type
		String newStepType = (String) stepTypeComboBox.getSelectedItem();
		selectedStep.setType(newStepType);

		// Update parameters
		Map<String, Object> newParameters = new HashMap<>();
		for (Map.Entry<String, JTextField> entry : parameterFields.entrySet())
		{
			String key = entry.getKey();
			if ("Step ID".equals(key))
				continue; // Already handled
			String value = entry.getValue().getText();
			newParameters.put(key, value);
		}
		selectedStep.setParameters(newParameters);

		// Update step requirements
		List<String> stepRequirements = new ArrayList<>();
		for (JComboBox<RequirementData> selector : requirementSelectors)
		{
			RequirementData selectedRequirement = (RequirementData) selector.getSelectedItem();
			if (selectedRequirement != null)
			{
				stepRequirements.add(selectedRequirement.getId());
			}
		}
		selectedStep.setStepRequirements(stepRequirements);

		// Update the step in the list model
		stepList.repaint();
	}

	private void removeStep()
	{
		int selectedIndex = stepList.getSelectedIndex();
		if (selectedIndex != -1)
		{
			dataModel.getStepListModel().remove(selectedIndex);
		}
	}
}
