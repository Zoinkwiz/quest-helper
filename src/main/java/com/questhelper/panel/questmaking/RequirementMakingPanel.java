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

import com.questhelper.questimport.JsonConstants;
import com.questhelper.questimport.RequirementData;
import com.questhelper.questimport.RequirementParameterDefinitions;
import net.runelite.api.Skill;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequirementMakingPanel extends JPanel
{
	private final DataModel dataModel;
	private JList<RequirementData> requirementList;
	private JComboBox<String> requirementTypeComboBox;
	private JPanel parameterFieldsPanel;
	private final Map<String, JComponent> parameterFields;
	private JButton saveChangesButton;
	private JButton removeRequirementButton;
	private RequirementData selectedRequirement;
	private JTextField currentIdComponent;

	public RequirementMakingPanel(DataModel dataModel)
	{
		this.dataModel = dataModel;
		this.parameterFields = new HashMap<>();
		initializeUI();
	}

	private void initializeUI()
	{
		setLayout(new BorderLayout());

		currentIdComponent = new JTextField(10);

		// Left panel with the list of requirements
		JPanel leftPanel = new JPanel(new BorderLayout());
		requirementList = new JList<>(dataModel.getRequirementListModel());
		requirementList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		requirementList.addListSelectionListener(e -> onRequirementSelected());
		JScrollPane listScrollPane = new JScrollPane(requirementList);
		leftPanel.add(listScrollPane, BorderLayout.CENTER);

		// Create Requirement button
		JButton createRequirementButton = new JButton("Create Requirement");
		createRequirementButton.addActionListener(e -> createRequirement());
		leftPanel.add(createRequirementButton, BorderLayout.SOUTH);

		// Right panel with editing fields
		JPanel rightPanel = new JPanel(new BorderLayout());

		// Requirement Type ComboBox
		requirementTypeComboBox = new JComboBox<>(RequirementParameterDefinitions.getAllRequirementTypes());
		requirementTypeComboBox.addActionListener(e -> updateParameterFields());

		// Parameter fields panel
		parameterFieldsPanel = new JPanel();
		parameterFieldsPanel.setLayout(new BoxLayout(parameterFieldsPanel, BoxLayout.Y_AXIS));

		// Save Changes and Remove Requirement buttons
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		saveChangesButton = new JButton("Save Changes");
		saveChangesButton.addActionListener(e -> saveChanges());
		removeRequirementButton = new JButton("Remove Requirement");
		removeRequirementButton.addActionListener(e -> removeRequirement());
		buttonsPanel.add(saveChangesButton);
		buttonsPanel.add(removeRequirementButton);

		// Add components to right panel
		rightPanel.add(requirementTypeComboBox, BorderLayout.NORTH);
		rightPanel.add(new JScrollPane(parameterFieldsPanel), BorderLayout.CENTER);
		rightPanel.add(buttonsPanel, BorderLayout.SOUTH);


		JSplitPane sl = new JSplitPane(JSplitPane.VERTICAL_SPLIT, leftPanel, rightPanel);
		sl.setOrientation(SwingConstants.VERTICAL);

		add(sl);

		setEditingFieldsEnabled(false);
	}

	private void addIdField()
	{
		JLabel label = new JLabel("id:");

		JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		fieldPanel.add(label);
		fieldPanel.add(currentIdComponent);
		parameterFieldsPanel.add(fieldPanel);
	}

	private void updateParameterFields()
	{
		String selectedType = (String) requirementTypeComboBox.getSelectedItem();
		List<String> parameters = RequirementParameterDefinitions.getParametersForRequirementType(selectedType);

		parameterFieldsPanel.removeAll();
		parameterFields.clear();

		addIdField();
		for (String param : parameters)
		{
			JLabel label = new JLabel(param + ":");
			JComponent field = createFieldForParameter(param);
			parameterFields.put(param, field);

			JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			fieldPanel.add(label);
			fieldPanel.add(field);
			parameterFieldsPanel.add(fieldPanel);
		}

		parameterFieldsPanel.revalidate();
		parameterFieldsPanel.repaint();
	}

	private JComponent createFieldForParameter(String param)
	{
		switch (param)
		{
			case JsonConstants.PARAM_SKILL:
				return new JComboBox<>(Skill.values());
			case JsonConstants.PARAM_LEVEL:
				return new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
			case JsonConstants.PARAM_BOOSTABLE:
				return new JCheckBox("Boostable", true);
			case JsonConstants.PARAM_ITEM_ID:
				return new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
			case JsonConstants.PARAM_QUANTITY:
				return new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
			case JsonConstants.PARAM_EQUIPPED:
				return new JCheckBox("Equipped", false);
			// Add other parameters as needed
			default:
				return new JTextField(20);
		}
	}

	private void createRequirement()
	{
		// Create a new RequirementData with default values
		RequirementData newRequirement = new RequirementData();
		newRequirement.setId(generateUniqueRequirementId());
		newRequirement.setType((String) requirementTypeComboBox.getSelectedItem());
		newRequirement.setParameters(new HashMap<>());

		dataModel.getRequirementListModel().addElement(newRequirement);
		requirementList.setSelectedValue(newRequirement, true);
	}

	private String generateUniqueRequirementId()
	{
		int counter = 1;
		String baseId = "req";
		String newId = baseId + counter;
		while (dataModel.containsRequirementWithId(newId))
		{
			counter++;
			newId = baseId + counter;
		}
		return newId;
	}

	private void saveChanges()
	{
		if (selectedRequirement == null)
		{
			JOptionPane.showMessageDialog(this, "No requirement selected to save.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		selectedRequirement.setId(currentIdComponent.getText());

		// Update requirement type
		String selectedType = (String) requirementTypeComboBox.getSelectedItem();
		selectedRequirement.setType(selectedType);

		// Update parameters
		Map<String, Object> params = new HashMap<>();
		for (Map.Entry<String, JComponent> entry : parameterFields.entrySet())
		{
			String paramName = entry.getKey();
			JComponent field = entry.getValue();
			Object value = getValueFromField(field);
			params.put(paramName, value);
		}
		selectedRequirement.setParameters(params);

		// Refresh the list
		requirementList.repaint();
	}

	private Object getValueFromField(JComponent field)
	{
		if (field instanceof JTextField)
		{
			return ((JTextField) field).getText();
		}
		else if (field instanceof JSpinner)
		{
			return ((JSpinner) field).getValue();
		}
		else if (field instanceof JCheckBox)
		{
			return ((JCheckBox) field).isSelected();
		}
		else if (field instanceof JComboBox)
		{
			return ((JComboBox<?>) field).getSelectedItem();
		}
		else
		{
			return null;
		}
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
				setEditingFieldsEnabled(false);
				clearParameterFields();
			}
		}
	}

	private void onRequirementSelected()
	{
		selectedRequirement = requirementList.getSelectedValue();
		if (selectedRequirement != null)
		{
			setEditingFieldsEnabled(true);
			populateFieldsWithRequirementData(selectedRequirement);
		}
		else
		{
			setEditingFieldsEnabled(false);
			clearParameterFields();
		}
	}

	private void populateFieldsWithRequirementData(RequirementData requirementData)
	{
		// Set the requirement type
		requirementTypeComboBox.setSelectedItem(requirementData.getType());

		currentIdComponent.setText(requirementData.getId());

		// Update parameter fields
		updateParameterFields();

		// Set parameter values
		Map<String, Object> params = requirementData.getParameters();
		for (Map.Entry<String, Object> entry : params.entrySet())
		{
			String paramName = entry.getKey();
			Object value = entry.getValue();
			JComponent field = parameterFields.get(paramName);
			if (field != null)
			{
				setValueToField(field, value);
			}
		}
	}

	private void setValueToField(JComponent field, Object value)
	{
		if (field instanceof JTextField)
		{
			((JTextField) field).setText(value != null ? value.toString() : "");
		}
		else if (field instanceof JSpinner)
		{
			((JSpinner) field).setValue(value != null ? value : 0);
		}
		else if (field instanceof JCheckBox)
		{
			((JCheckBox) field).setSelected(value != null && (Boolean) value);
		}
		else if (field instanceof JComboBox)
		{
			((JComboBox<Object>) field).setSelectedItem(value);
		}
	}

	private void setEditingFieldsEnabled(boolean enabled)
	{
		requirementTypeComboBox.setEnabled(enabled);
		for (JComponent field : parameterFields.values())
		{
			field.setEnabled(enabled);
		}
		saveChangesButton.setEnabled(enabled);
		removeRequirementButton.setEnabled(enabled);
	}

	private void clearParameterFields()
	{
		requirementTypeComboBox.setSelectedIndex(0);
		parameterFieldsPanel.removeAll();
		parameterFields.clear();
		parameterFieldsPanel.revalidate();
		parameterFieldsPanel.repaint();
	}
}
