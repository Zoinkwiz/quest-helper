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
import com.questhelper.questimport.StepData;
import com.questhelper.questimport.StepParameterDefinitions;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.ColorScheme;
import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepMakingPanel extends JPanel
{
	private final DataModel dataModel;
	private JList<StepData> stepList;
	private JComboBox<String> stepTypeComboBox;
	private JPanel parameterFieldsPanel;
	private final Map<String, JComponent> parameterFields;
	private JButton saveChangesButton;
	private JButton removeStepButton;
	private StepData selectedStep;
	private JTextField currentIdComponent;
	private JPanel requirementsListPanel;
	private final List<JComboBox<RequirementData>> requirementComboBoxes;
	private JButton addRequirementButton;

	public StepMakingPanel(DataModel dataModel)
	{
		this.dataModel = dataModel;
		this.parameterFields = new HashMap<>();
		this.requirementComboBoxes = new ArrayList<>();
		initializeUI();
		addDataListeners();
	}

	private void initializeUI()
	{
		setLayout(new BorderLayout());

		currentIdComponent = new JTextField(10);;

		// Left panel with the list of steps
		JPanel leftPanel = new JPanel(new BorderLayout());
		stepList = new JList<>(dataModel.getStepListModel());
		stepList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		stepList.addListSelectionListener(e -> onStepSelected());
		JScrollPane listScrollPane = new JScrollPane(stepList);
		leftPanel.add(listScrollPane, BorderLayout.CENTER);

		// Create Step button
		JButton createStepButton = new JButton("Create Step");
		createStepButton.addActionListener(e -> createStep());
		leftPanel.add(createStepButton, BorderLayout.SOUTH);

		// Right panel with editing fields
		JPanel rightPanel = new JPanel(new BorderLayout());

		// Step Type ComboBox
		stepTypeComboBox = new JComboBox<>(StepParameterDefinitions.getAllStepTypes());
		stepTypeComboBox.addActionListener(e -> updateParameterFields());

		// Parameter fields panel
		parameterFieldsPanel = new JPanel();
		parameterFieldsPanel.setLayout(new BoxLayout(parameterFieldsPanel, BoxLayout.Y_AXIS));

		// Save Changes and Remove Step buttons
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		saveChangesButton = new JButton("Save Changes");
		saveChangesButton.addActionListener(e -> saveChanges());
		removeStepButton = new JButton("Remove Step");
		removeStepButton.addActionListener(e -> removeStep());
		buttonsPanel.add(saveChangesButton);
		buttonsPanel.add(removeStepButton);

		// Add components to right panel
		rightPanel.add(stepTypeComboBox, BorderLayout.NORTH);
		rightPanel.add(new JScrollPane(parameterFieldsPanel), BorderLayout.CENTER);
		rightPanel.add(buttonsPanel, BorderLayout.SOUTH);

		// Conditional requirements
		JPanel requirementsPanel = new JPanel(new BorderLayout());
		JLabel requirementsLabel = new JLabel("Requirements:");
		requirementsListPanel = new JPanel();
		requirementsListPanel.setLayout(new BoxLayout(requirementsListPanel, BoxLayout.Y_AXIS));
		requirementsListPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		JScrollPane requirementsScrollPane = new JScrollPane(requirementsListPanel);
		JPanel requirementsButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		addRequirementButton = new JButton("Add Requirement");
		addRequirementButton.addActionListener(e -> addRequirement());
		requirementsButtonsPanel.add(addRequirementButton);
		requirementsPanel.add(requirementsLabel, BorderLayout.NORTH);
		requirementsPanel.add(requirementsScrollPane, BorderLayout.CENTER);
		requirementsPanel.add(requirementsButtonsPanel, BorderLayout.SOUTH);

		JSplitPane sl = new JSplitPane(JSplitPane.VERTICAL_SPLIT, leftPanel, rightPanel);
		sl.setOrientation(SwingConstants.VERTICAL);
		JSplitPane sl2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, sl, requirementsPanel);
		sl2.setOrientation(SwingConstants.VERTICAL);

		add(sl2);

		setEditingFieldsEnabled(false);
	}

	private void updateParameterFields()
	{
		String selectedType = (String) stepTypeComboBox.getSelectedItem();
		List<String> parameters = StepParameterDefinitions.getParametersForStepType(selectedType);

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

	private void addIdField()
	{
		JLabel label = new JLabel("id:");

		JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		fieldPanel.add(label);
		fieldPanel.add(currentIdComponent);
		parameterFieldsPanel.add(fieldPanel);
	}

	private JComponent createFieldForParameter(String param)
	{
		switch (param)
		{
			case JsonConstants.PARAM_NPC_ID:
			case JsonConstants.PARAM_OBJECT_ID:
			case JsonConstants.PARAM_WORLD_POINT_X:
			case JsonConstants.PARAM_WORLD_POINT_Y:
			case JsonConstants.PARAM_WORLD_POINT_Z:
				return new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
			// Add other parameters as needed
			default:
				return new JTextField(10);
		}
	}

	private void createStep()
	{
		// Create a new StepData with default values
		StepData newStep = new StepData();
		newStep.setId(generateUniqueStepId());
		newStep.setType((String) stepTypeComboBox.getSelectedItem());
		newStep.setParameters(new HashMap<>());
		newStep.setStepRequirements(new ArrayList<>());

		dataModel.getStepListModel().addElement(newStep);
		stepList.setSelectedValue(newStep, true);
	}

	public void createNpcStep(int npcID, String name, WorldPoint worldPoint)
	{
		// Create a new StepData with default values
		StepData newStep = new StepData();
		newStep.setId("talkTo" + name);
		newStep.setType(JsonConstants.NPC_STEP);
		Map<String, Object> npcDetails = new HashMap<>();
		npcDetails.put(JsonConstants.PARAM_NPC_ID, npcID);
		npcDetails.put(JsonConstants.PARAM_WORLD_POINT_X, worldPoint.getX());
		npcDetails.put(JsonConstants.PARAM_WORLD_POINT_Y, worldPoint.getY());
		npcDetails.put(JsonConstants.PARAM_WORLD_POINT_Z, worldPoint.getPlane());
		newStep.setParameters(npcDetails);
		newStep.setStepRequirements(new ArrayList<>());

		dataModel.getStepListModel().addElement(newStep);
		stepList.setSelectedValue(newStep, true);
	}

	private String generateUniqueStepId()
	{
		int counter = 1;
		String baseId = "step";
		String newId = baseId + counter;
		while (dataModel.containsStepWithId(newId))
		{
			counter++;
			newId = baseId + counter;
		}
		return newId;
	}

	private void saveChanges()
	{
		if (selectedStep == null)
		{
			JOptionPane.showMessageDialog(this, "No step selected to save.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Update step type
		String selectedType = (String) stepTypeComboBox.getSelectedItem();
		selectedStep.setType(selectedType);
		selectedStep.setId(currentIdComponent.getText());

		// Update parameters
		Map<String, Object> params = new HashMap<>();
		for (Map.Entry<String, JComponent> entry : parameterFields.entrySet())
		{
			String paramName = entry.getKey();
			JComponent field = entry.getValue();
			Object value = getValueFromField(field);
			params.put(paramName, value);
		}
		selectedStep.setParameters(params);

		// Save requirements
		List<RequirementData> reqDatas = new ArrayList<>();
		for (JComboBox<RequirementData> comboBox : requirementComboBoxes)
		{
			RequirementData reqData = (RequirementData) comboBox.getSelectedItem();
			if (reqData != null)
			{
				reqDatas.add(reqData);
			}
		}
		selectedStep.setStepRequirements(reqDatas);

		// Refresh the list
		stepList.repaint();
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

	private void removeStep()
	{
		if (selectedStep != null)
		{
			int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove the selected step?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION)
			{
				dataModel.getStepListModel().removeElement(selectedStep);
				selectedStep = null;
				setEditingFieldsEnabled(false);
				clearParameterFields();
			}
		}
	}

	private void onStepSelected()
	{
		selectedStep = stepList.getSelectedValue();
		if (selectedStep != null)
		{
			setEditingFieldsEnabled(true);
			populateFieldsWithStepData(selectedStep);
		}
		else
		{
			setEditingFieldsEnabled(false);
			clearParameterFields();
		}
	}

	private void populateFieldsWithStepData(StepData stepData)
	{
		requirementsListPanel.removeAll();
		requirementComboBoxes.clear();

		currentIdComponent.setText(stepData.getId());

		// Populate requirements
		for (RequirementData reqData : stepData.getStepRequirements())
		{
			if (reqData != null)
			{
				addRequirementRow(reqData);
			}
		}

		// Refresh the panel
		requirementsListPanel.revalidate();
		requirementsListPanel.repaint();

		// Set the step type
		stepTypeComboBox.setSelectedItem(stepData.getType());

		// Update parameter fields
		updateParameterFields();

		// Set parameter values
		Map<String, Object> params = stepData.getParameters();
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

	private void addRequirement()
	{
		addRequirementRow(null);
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
		stepTypeComboBox.setEnabled(enabled);
		addRequirementButton.setEnabled(enabled);
		for (JComponent field : parameterFields.values())
		{
			field.setEnabled(enabled);
		}
		for (JComboBox<RequirementData> comboBox : requirementComboBoxes)
		{
			comboBox.setEnabled(enabled);
		}
		saveChangesButton.setEnabled(enabled);
		removeStepButton.setEnabled(enabled);
	}

	private void clearParameterFields()
	{
		stepTypeComboBox.setSelectedIndex(0);
		parameterFieldsPanel.removeAll();
		parameterFields.clear();
		parameterFieldsPanel.revalidate();
		parameterFieldsPanel.repaint();
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
		requirementsListPanel.add(rowPanel);

		requirementsListPanel.revalidate();
		requirementsListPanel.repaint();
	}

	private void removeRequirementRow(JPanel rowPanel, JComboBox<RequirementData> requirementComboBox)
	{
		requirementsListPanel.remove(rowPanel);
		requirementComboBoxes.remove(requirementComboBox);

		requirementsListPanel.revalidate();
		requirementsListPanel.repaint();
	}

	private void addDataListeners()
	{
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
}
