package com.questhelper.panel.questmaking;

import com.questhelper.questimport.RequirementData;
import net.runelite.api.Skill;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class RequirementMakingPanel extends JPanel
{
	private JComboBox<RequirementType> requirementTypeComboBox;
	private JPanel parameterPanel;
	private JList<RequirementData> requirementList;
	private DataModel dataModel;

	private Map<String, JComponent> parameterFields;

	private JButton createRequirementButton;
	private JButton saveChangesButton;
	private JButton removeRequirementButton;

	private RequirementData selectedRequirement;

	public RequirementMakingPanel(DataModel dataModel)
	{
		this.dataModel = dataModel;
		this.parameterFields = new HashMap<>();
		setLayout(new BorderLayout());
		initializeUI();
	}

	private void initializeUI()
	{
		// Top panel for requirement creation
		JPanel creationPanel = new JPanel(new BorderLayout());

		// Requirement type selection
		requirementTypeComboBox = new JComboBox<>(RequirementType.values());
		requirementTypeComboBox.addActionListener(e -> updateRequirementParameterFields());

		// Panel to hold parameter fields
		parameterPanel = new JPanel(new GridBagLayout());

		// Initialize parameter fields
		updateRequirementParameterFields();

		// Add components to creation panel
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(new JLabel("Requirement Type:"));
		topPanel.add(requirementTypeComboBox);
		creationPanel.add(topPanel, BorderLayout.NORTH);
		creationPanel.add(parameterPanel, BorderLayout.CENTER);

		// Requirements list
		requirementList = new JList<>(dataModel.getRequirementListModel());
		requirementList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		requirementList.addListSelectionListener(e -> onRequirementSelected());

		// Buttons panel
		JPanel buttonsPanel = new JPanel();
		createRequirementButton = new JButton("Create Requirement");
		saveChangesButton = new JButton("Save Changes");
		removeRequirementButton = new JButton("Remove Requirement");

		buttonsPanel.add(createRequirementButton);
		buttonsPanel.add(saveChangesButton);
		buttonsPanel.add(removeRequirementButton);

		// Add action listeners
		createRequirementButton.addActionListener(e -> createRequirement());
		saveChangesButton.addActionListener(e -> saveChanges());
		removeRequirementButton.addActionListener(e -> removeRequirement());

		// Initially disable editing fields and buttons
		setEditingFieldsEnabled(false);
		saveChangesButton.setEnabled(false);
		removeRequirementButton.setEnabled(false);

		// Assemble the panel
		add(creationPanel, BorderLayout.NORTH);
		add(new JScrollPane(requirementList), BorderLayout.CENTER);
		add(buttonsPanel, BorderLayout.SOUTH);
	}

	private void onRequirementSelected()
	{
		selectedRequirement = requirementList.getSelectedValue();
		if (selectedRequirement != null)
		{
			// Enable editing fields
			setEditingFieldsEnabled(true);

			// Enable Save Changes and Remove Requirement buttons
			saveChangesButton.setEnabled(true);
			removeRequirementButton.setEnabled(true);

			// Populate fields with selected requirement's data
			populateFieldsWithRequirementData(selectedRequirement);
		}
		else
		{
			// Disable editing fields
			setEditingFieldsEnabled(false);

			// Disable Save Changes and Remove Requirement buttons
			saveChangesButton.setEnabled(false);
			removeRequirementButton.setEnabled(false);

			// Clear the fields
			clearEditingFields();
		}
	}

	private void updateRequirementParameterFields()
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

		// Requirement ID field
		parameterPanel.add(new JLabel("Requirement ID:"), gbc);
		gbc.gridx++;
		JTextField requirementIdField = new JTextField(20);
		requirementIdField.setEnabled(selectedRequirement == null); // Disable editing if requirement is already created
		parameterPanel.add(requirementIdField, gbc);
		parameterFields.put("Requirement ID", requirementIdField);

		RequirementType selectedType = (RequirementType) requirementTypeComboBox.getSelectedItem();

		switch (selectedType)
		{
			case SKILL:
				gbc.gridx = 0;
				gbc.gridy++;

				parameterPanel.add(new JLabel("Skill Name:"), gbc);
				gbc.gridx++;
				JComboBox<Skill> skillNameComboBox = new JComboBox<>(Skill.values());
				parameterPanel.add(skillNameComboBox, gbc);
				parameterFields.put("Skill Name", skillNameComboBox);

				gbc.gridx = 0;
				gbc.gridy++;
				parameterPanel.add(new JLabel("Level:"), gbc);
				gbc.gridx++;
				JSpinner levelSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
				parameterPanel.add(levelSpinner, gbc);
				parameterFields.put("Level", levelSpinner);

				gbc.gridx = 0;
				gbc.gridy++;
				parameterPanel.add(new JLabel("Boostable:"), gbc);
				gbc.gridx++;
				JCheckBox boostableCheckBox = new JCheckBox();
				parameterPanel.add(boostableCheckBox, gbc);
				parameterFields.put("Boostable", boostableCheckBox);
				break;

			case ITEM:
				gbc.gridx = 0;
				gbc.gridy++;

				parameterPanel.add(new JLabel("Item ID:"), gbc);
				gbc.gridx++;
				JTextField itemIdField = new JTextField(20);
				parameterPanel.add(itemIdField, gbc);
				parameterFields.put("Item ID", itemIdField);

				gbc.gridx = 0;
				gbc.gridy++;
				parameterPanel.add(new JLabel("Quantity:"), gbc);
				gbc.gridx++;
				JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
				parameterPanel.add(quantitySpinner, gbc);
				parameterFields.put("Quantity", quantitySpinner);

				gbc.gridx = 0;
				gbc.gridy++;
				parameterPanel.add(new JLabel("Equipped:"), gbc);
				gbc.gridx++;
				JCheckBox equippedCheckBox = new JCheckBox();
				parameterPanel.add(equippedCheckBox, gbc);
				parameterFields.put("Equipped", equippedCheckBox);
				break;

			// Add other requirement types as needed
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

		// Enable or disable editing fields based on whether a requirement is selected
		boolean enabled = selectedRequirement != null;
		setEditingFieldsEnabled(enabled);

		parameterPanel.revalidate();
		parameterPanel.repaint();
	}

	private void createRequirement()
	{
		// Create a new RequirementData object with a default or unique ID
		String defaultRequirementId = "req" + (dataModel.getRequirementListModel().getSize() + 1);
		RequirementData newRequirement = new RequirementData();
		newRequirement.setId(defaultRequirementId);
		newRequirement.setType(requirementTypeComboBox.getSelectedItem().toString());
		newRequirement.setParameters(new HashMap<>());

		// Add the new requirement to the list model
		dataModel.getRequirementListModel().addElement(newRequirement);

		// Select the new requirement in the list
		requirementList.setSelectedValue(newRequirement, true);
	}

	private void removeRequirement()
	{
		if (selectedRequirement != null)
		{
			int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove the selected requirement?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION)
			{
				dataModel.getRequirementListModel().removeElement(selectedRequirement);
				selectedRequirement = null;

				// Notify listeners
				dataModel.notifyRequirementChangeListeners();
			}
		}
	}

	private void saveChanges()
	{
		if (selectedRequirement == null)
		{
			JOptionPane.showMessageDialog(this, "No requirement selected to save.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Update the selectedRequirement object with the values from the fields

		// Update requirement ID
		String newRequirementId = ((JTextField) parameterFields.get("Requirement ID")).getText();
		if (newRequirementId == null || newRequirementId.trim().isEmpty())
		{
			JOptionPane.showMessageDialog(this, "Requirement ID is required.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Check for duplicate IDs
		for (int i = 0; i < dataModel.getRequirementListModel().size(); i++)
		{
			RequirementData existingRequirement = dataModel.getRequirementListModel().getElementAt(i);
			if (existingRequirement != selectedRequirement && newRequirementId.equals(existingRequirement.getId()))
			{
				JOptionPane.showMessageDialog(this, "Requirement ID must be unique.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		selectedRequirement.setId(newRequirementId);

		// Update requirement type
		RequirementType newRequirementType = (RequirementType) requirementTypeComboBox.getSelectedItem();
		selectedRequirement.setType(newRequirementType.toString());

		// Update parameters
		Map<String, Object> newParameters = new HashMap<>();
		for (Map.Entry<String, JComponent> entry : parameterFields.entrySet())
		{
			String key = entry.getKey();
			if ("Requirement ID".equals(key))
				continue; // Already handled

			Object value = getValueFromComponent(entry.getValue());
			newParameters.put(key, value);
		}
		selectedRequirement.setParameters(newParameters);

		// Update the requirement in the list model
		requirementList.repaint();

		// Notify listeners (e.g., StepMakingPanel to update requirement selectors)
		dataModel.notifyRequirementChangeListeners();
	}

	private void populateFieldsWithRequirementData(RequirementData requirement)
	{
		// Set the requirement ID
		((JTextField) parameterFields.get("Requirement ID")).setText(requirement.getId());

		// Set the requirement type
		requirementTypeComboBox.setSelectedItem(RequirementType.fromString(requirement.getType()));

		// Update parameter fields for the selected type
		updateRequirementParameterFields();

		// Set the parameters
		for (Map.Entry<String, Object> entry : requirement.getParameters().entrySet())
		{
			JComponent component = parameterFields.get(entry.getKey());
			if (component != null)
			{
				setValueToComponent(component, entry.getValue());
			}
		}
	}

	private void setEditingFieldsEnabled(boolean enabled)
	{
		for (JComponent component : parameterFields.values())
		{
			component.setEnabled(enabled);
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
