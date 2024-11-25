package com.questhelper.panel.questmaking;

import com.questhelper.questimport.RequirementData;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class RequirementMakingPanel extends JPanel
{
	private JComboBox<String> requirementTypeComboBox;
	private JPanel parameterPanel;
	private JList<RequirementData> requirementList;
	private DataModel dataModel;

	private Map<String, JTextField> parameterFields;

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
		requirementTypeComboBox = new JComboBox<>(new String[] { "SkillRequirement", "ItemRequirement" });
		requirementTypeComboBox.addActionListener(e -> updateRequirementParameterFields());

		// Panel to hold parameter fields
		parameterPanel = new JPanel();
		parameterPanel.setLayout(new BoxLayout(parameterPanel, BoxLayout.Y_AXIS));

		// Initialize parameter fields
		updateRequirementParameterFields();

		// Add components to creation panel
		JPanel topPanel = new JPanel(new FlowLayout());
		topPanel.add(new JLabel("Requirement Type:"));
		topPanel.add(requirementTypeComboBox);
		creationPanel.add(topPanel, BorderLayout.NORTH);
		creationPanel.add(parameterPanel, BorderLayout.CENTER);

		// Requirements list
		requirementList = new JList<>(dataModel.getRequirementListModel());
		requirementList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Buttons panel
		JPanel buttonsPanel = new JPanel();
		JButton addRequirementButton = new JButton("Add Requirement");
		JButton removeRequirementButton = new JButton("Remove Requirement");

		buttonsPanel.add(addRequirementButton);
		buttonsPanel.add(removeRequirementButton);

		// Add action listeners
		addRequirementButton.addActionListener(e -> addRequirement());
		removeRequirementButton.addActionListener(e -> removeRequirement());

		// Assemble the panel
		add(creationPanel, BorderLayout.NORTH);
		add(new JScrollPane(requirementList), BorderLayout.CENTER);
		add(buttonsPanel, BorderLayout.SOUTH);
	}

	private void updateRequirementParameterFields()
	{
		parameterPanel.removeAll();
		parameterFields.clear();

		// Requirement ID field
		parameterPanel.add(new JLabel("Requirement ID:"));
		JTextField requirementIdField = new JTextField(20);
		parameterPanel.add(requirementIdField);
		parameterFields.put("Requirement ID", requirementIdField);

		String selectedType = (String) requirementTypeComboBox.getSelectedItem();

		switch (selectedType)
		{
			case "SkillRequirement":
				parameterPanel.add(new JLabel("Skill Name:"));
				JTextField skillNameField = new JTextField(20);
				parameterPanel.add(skillNameField);
				parameterFields.put("Skill Name", skillNameField);

				parameterPanel.add(new JLabel("Level:"));
				JTextField levelField = new JTextField(20);
				parameterPanel.add(levelField);
				parameterFields.put("Level", levelField);

				parameterPanel.add(new JLabel("Boostable (true/false):"));
				JTextField boostableField = new JTextField(20);
				parameterPanel.add(boostableField);
				parameterFields.put("Boostable", boostableField);
				break;

			case "ItemRequirement":
				parameterPanel.add(new JLabel("Item ID:"));
				JTextField itemIdField = new JTextField(20);
				parameterPanel.add(itemIdField);
				parameterFields.put("Item ID", itemIdField);

				parameterPanel.add(new JLabel("Quantity:"));
				JTextField quantityField = new JTextField(20);
				parameterPanel.add(quantityField);
				parameterFields.put("Quantity", quantityField);

				parameterPanel.add(new JLabel("Equipped (true/false):"));
				JTextField equippedField = new JTextField(20);
				parameterPanel.add(equippedField);
				parameterFields.put("Equipped", equippedField);
				break;

			// Add other requirement types as needed
		}

		parameterPanel.revalidate();
		parameterPanel.repaint();
	}

	private void addRequirement()
	{
		// Collect requirement ID
		JTextField requirementIdField = parameterFields.get("Requirement ID");
		String requirementId = requirementIdField.getText();

		if (requirementId == null || requirementId.trim().isEmpty())
		{
			JOptionPane.showMessageDialog(this, "Requirement ID is required.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Check for duplicate IDs
		for (int i = 0; i < dataModel.getRequirementListModel().size(); i++)
		{
			RequirementData existingRequirement = dataModel.getRequirementListModel().getElementAt(i);
			if (requirementId.equals(existingRequirement.getId()))
			{
				JOptionPane.showMessageDialog(this, "Requirement ID must be unique.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		// Collect other parameters
		Map<String, Object> parameters = new HashMap<>();
		for (Map.Entry<String, JTextField> entry : parameterFields.entrySet())
		{
			String key = entry.getKey();
			if ("Requirement ID".equals(key))
				continue; // Already processed

			String value = entry.getValue().getText();
			parameters.put(key, value);
		}

		String selectedType = (String) requirementTypeComboBox.getSelectedItem();

		// Create a new RequirementData object
		RequirementData requirementData = new RequirementData();
		requirementData.setId(requirementId);
		requirementData.setType(selectedType);
		requirementData.setParameters(parameters);

		// Add the requirement to the list model
		dataModel.getRequirementListModel().addElement(requirementData);

		// Notify listeners
		dataModel.notifyRequirementChangeListeners();
	}

	private void removeRequirement()
	{
		int selectedIndex = requirementList.getSelectedIndex();
		if (selectedIndex != -1)
		{
			dataModel.getRequirementListModel().remove(selectedIndex);
		}

		dataModel.notifyRequirementChangeListeners();
	}
}
