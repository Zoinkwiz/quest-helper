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
	private JComboBox<StepType> stepTypeComboBox;
	private JPanel parameterPanel;
	private JList<StepData> stepList;
	private DataModel dataModel;
	private StepData selectedStep;
	private Map<String, JComponent> parameterFields;
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
		stepTypeComboBox = new JComboBox<>(StepType.values());
		stepTypeComboBox.addActionListener(e -> updateParameterFields());

		// Panel to hold parameter fields
		parameterPanel = new JPanel(new GridBagLayout());

		// Step Requirements
		JPanel stepRequirementsPanel = new JPanel(new GridBagLayout());
		stepRequirementsPanel.setBorder(BorderFactory.createTitledBorder("Step Requirements"));

		// Initialize the requirement selectors
		int requirementSlots = 5;
		requirementSelectors = new JComboBox[requirementSlots];
		GridBagConstraints gbcReq = new GridBagConstraints();
		gbcReq.insets = new Insets(4, 4, 4, 4);
		gbcReq.anchor = GridBagConstraints.WEST;
		gbcReq.fill = GridBagConstraints.HORIZONTAL;
		gbcReq.gridx = 0;
		gbcReq.gridy = 0;

		for (int i = 0; i < requirementSlots; i++)
		{
			stepRequirementsPanel.add(new JLabel("Requirement " + (i + 1) + ":"), gbcReq);
			gbcReq.gridx++;
			requirementSelectors[i] = new JComboBox<>();
			stepRequirementsPanel.add(requirementSelectors[i], gbcReq);
			gbcReq.gridx = 0;
			gbcReq.gridy++;
		}

		// Initialize parameter fields
		updateParameterFields();

		// Add components to creation panel
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
		for (JComponent component : parameterFields.values())
		{
			component.setEnabled(enabled);
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
		Map<String, Object> existingValues = new HashMap<>();
		for (Map.Entry<String, JComponent> entry : parameterFields.entrySet())
		{
			String key = entry.getKey();
			JComponent component = entry.getValue();
			Object value = getValueFromComponent(component);
			existingValues.put(key, value);
		}

		parameterPanel.removeAll();
		parameterFields.clear();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;

		// Step ID field
		parameterPanel.add(new JLabel("Step ID:"), gbc);
		gbc.gridx++;
		JTextField stepIdField = new JTextField(20);
		stepIdField.setEnabled(false); // Step ID should not be editable after creation
		parameterPanel.add(stepIdField, gbc);
		parameterFields.put("Step ID", stepIdField);

		StepType selectedType = (StepType) stepTypeComboBox.getSelectedItem();

		gbc.gridx = 0;
		gbc.gridy++;

		// Add fields based on the selected step type
		switch (selectedType)
		{
			case NPC_STEP:
				parameterPanel.add(new JLabel("NPC ID:"), gbc);
				gbc.gridx++;
				JSpinner npcIdSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
				parameterPanel.add(npcIdSpinner, gbc);
				parameterFields.put("NPC ID", npcIdSpinner);

				gbc.gridx = 0;
				gbc.gridy++;
				parameterPanel.add(new JLabel("Text:"), gbc);
				gbc.gridx++;
				JTextField textField = new JTextField(20);
				parameterPanel.add(textField, gbc);
				parameterFields.put("text", textField);

				break;

			case OBJECT_STEP:
				parameterPanel.add(new JLabel("Object ID:"), gbc);
				gbc.gridx++;
				JSpinner objectIdSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
				parameterPanel.add(objectIdSpinner, gbc);
				parameterFields.put("Object ID", objectIdSpinner);

				gbc.gridx = 0;
				gbc.gridy++;
				parameterPanel.add(new JLabel("Text:"), gbc);
				gbc.gridx++;
				JTextField objectActionField = new JTextField(20);
				parameterPanel.add(objectActionField, gbc);
				parameterFields.put("text", objectActionField);
				break;

		}

		// Restore existing values where keys match
		for (Map.Entry<String, JComponent> entry : parameterFields.entrySet())
		{
			String key = entry.getKey();
			Object value = existingValues.get(key);
			if (value != null)
			{
				setValueToComponent(entry.getValue(), value);
			}
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
		newStep.setType(stepTypeComboBox.getSelectedItem().toString());
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

		dataModel.notifyStepChangeListeners();
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
		((JTextField) parameterFields.get("Step ID")).setText(step.getId());

		// Set the step type
		stepTypeComboBox.setSelectedItem(StepType.fromString(step.getType()));

		// Update parameter fields for the selected type
		updateParameterFields();

		// Set the parameters
		for (Map.Entry<String, Object> entry : step.getParameters().entrySet())
		{
			JComponent component = parameterFields.get(entry.getKey());
			if (component != null)
			{
				setValueToComponent(component, entry.getValue());
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
		for (JComponent component : parameterFields.values())
		{
			if (component instanceof JTextField)
			{
				((JTextField) component).setText("");
			}
			else if (component instanceof JComboBox)
			{
				((JComboBox<?>) component).setSelectedIndex(0);
			}
			else if (component instanceof JSpinner)
			{
				((JSpinner) component).setValue(1);
			}
			else if (component instanceof JCheckBox)
			{
				((JCheckBox) component).setSelected(false);
			}
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
		String newStepId = ((JTextField) parameterFields.get("Step ID")).getText();
		if (newStepId == null || newStepId.trim().isEmpty())
		{
			JOptionPane.showMessageDialog(this, "Step ID is required.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Check for duplicate IDs
		for (int i = 0; i < dataModel.getStepListModel().size(); i++)
		{
			StepData existingStep = dataModel.getStepListModel().getElementAt(i);
			if (existingStep != selectedStep && newStepId.equals(existingStep.getId()))
			{
				JOptionPane.showMessageDialog(this, "Step ID must be unique.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		selectedStep.setId(newStepId);

		// Update step type
		StepType newStepType = (StepType) stepTypeComboBox.getSelectedItem();
		selectedStep.setType(newStepType.toString());

		// Update parameters
		Map<String, Object> newParameters = new HashMap<>();
		for (Map.Entry<String, JComponent> entry : parameterFields.entrySet())
		{
			String key = entry.getKey();
			if ("Step ID".equals(key))
				continue; // Already handled
			Object value = getValueFromComponent(entry.getValue());
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
		if (selectedStep != null)
		{
			int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove the selected step?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION)
			{
				dataModel.getStepListModel().removeElement(selectedStep);
				selectedStep = null;

				// Disable editing fields and buttons
				setEditingFieldsEnabled(false);
				saveChangesButton.setEnabled(false);
				removeStepButton.setEnabled(false);

				// Clear the fields
				clearEditingFields();
			}
		}

		dataModel.notifyStepChangeListeners();
	}

	private Object getValueFromComponent(JComponent component)
	{
		if (component instanceof JTextField)
		{
			return ((JTextField) component).getText();
		}
		else if (component instanceof JComboBox)
		{
			return ((JComboBox<?>) component).getSelectedItem();
		}
		else if (component instanceof JSpinner)
		{
			return ((JSpinner) component).getValue();
		}
		else if (component instanceof JCheckBox)
		{
			return ((JCheckBox) component).isSelected();
		}
		return null;
	}

	private void setValueToComponent(JComponent component, Object value)
	{
		if (component instanceof JTextField)
		{
			((JTextField) component).setText(value.toString());
		}
		else if (component instanceof JComboBox)
		{
			((JComboBox) component).setSelectedItem(value);
		}
		else if (component instanceof JSpinner)
		{
			((JSpinner) component).setValue(value);
		}
		else if (component instanceof JCheckBox)
		{
			((JCheckBox) component).setSelected(Boolean.parseBoolean(value.toString()));
		}
	}
}
