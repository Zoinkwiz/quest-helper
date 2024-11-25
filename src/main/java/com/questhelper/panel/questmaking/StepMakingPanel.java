package com.questhelper.panel.questmaking;

import com.questhelper.panel.questmaking.DataModel;
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
	private final DataModel dataModel;

	private final Map<String, JTextField> parameterFields;

	public StepMakingPanel(DataModel dataModel)
	{
		this.dataModel = dataModel;
		this.parameterFields = new HashMap<>();
		setLayout(new BorderLayout());
		initializeUI();
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

		// Initialize parameter fields
		updateParameterFields();

		// Requirements selection panels
		JPanel requirementsSelectionPanel = new JPanel(new GridLayout(1, 2));

		// Step Requirements
		JPanel stepRequirementsPanel = new JPanel(new BorderLayout());
		stepRequirementsPanel.add(new JLabel("Step Requirements:"), BorderLayout.NORTH);
		JList<RequirementData> stepRequirementsList = new JList<>(dataModel.getRequirementListModel());
		stepRequirementsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		stepRequirementsPanel.add(new JScrollPane(stepRequirementsList), BorderLayout.CENTER);

		// Conditional Requirements
		JPanel conditionalRequirementsPanel = new JPanel(new BorderLayout());
		conditionalRequirementsPanel.add(new JLabel("Conditional Requirements:"), BorderLayout.NORTH);
		JList<RequirementData> conditionalRequirementsList = new JList<>(dataModel.getRequirementListModel());
		conditionalRequirementsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		conditionalRequirementsPanel.add(new JScrollPane(conditionalRequirementsList), BorderLayout.CENTER);

		requirementsSelectionPanel.add(stepRequirementsPanel);
		requirementsSelectionPanel.add(conditionalRequirementsPanel);

		// Add components to creation panel
		JPanel topPanel = new JPanel(new FlowLayout());
		topPanel.add(new JLabel("Step Type:"));
		topPanel.add(stepTypeComboBox);
		creationPanel.add(topPanel, BorderLayout.NORTH);
		creationPanel.add(parameterPanel, BorderLayout.CENTER);
		creationPanel.add(requirementsSelectionPanel, BorderLayout.EAST);

		// Steps list
		stepList = new JList<>(dataModel.getStepListModel());
		stepList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Buttons panel
		JPanel buttonsPanel = new JPanel();
		JButton addStepButton = new JButton("Add Step");
		JButton removeStepButton = new JButton("Remove Step");

		buttonsPanel.add(addStepButton);
		buttonsPanel.add(removeStepButton);

		// Add action listeners
		addStepButton.addActionListener(e -> addStep(stepRequirementsList, conditionalRequirementsList));
		removeStepButton.addActionListener(e -> removeStep());

		// Assemble the panel
		add(creationPanel, BorderLayout.NORTH);
		add(new JScrollPane(stepList), BorderLayout.CENTER);
		add(buttonsPanel, BorderLayout.SOUTH);
	}

	private void updateParameterFields()
	{
		parameterPanel.removeAll();
		parameterFields.clear();

		// Step ID field
		parameterPanel.add(new JLabel("Step ID:"));
		JTextField stepIdField = new JTextField(20);
		parameterPanel.add(stepIdField);
		parameterFields.put("Step ID", stepIdField);

		String selectedType = (String) stepTypeComboBox.getSelectedItem();

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

		parameterPanel.revalidate();
		parameterPanel.repaint();
	}

	private void addStep(JList<RequirementData> stepRequirementsList, JList<RequirementData> conditionalRequirementsList)
	{
		// Collect step ID
		JTextField stepIdField = parameterFields.get("Step ID");
		String stepId = stepIdField.getText();

		if (stepId == null || stepId.trim().isEmpty())
		{
			JOptionPane.showMessageDialog(this, "Step ID is required.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Collect other parameters
		Map<String, Object> parameters = new HashMap<>();
		for (Map.Entry<String, JTextField> entry : parameterFields.entrySet())
		{
			String key = entry.getKey();
			if ("Step ID".equals(key))
				continue; // Already processed

			String value = entry.getValue().getText();
			parameters.put(key, value);
		}

		String selectedType = (String) stepTypeComboBox.getSelectedItem();

		// Create a new StepData object
		StepData stepData = new StepData();
		stepData.setId(stepId);
		stepData.setType(selectedType);
		stepData.setParameters(parameters);

		// Get selected requirement IDs
		List<String> stepRequirements = new ArrayList<>();
		for (RequirementData req : stepRequirementsList.getSelectedValuesList())
		{
			stepRequirements.add(req.getId());
		}

		java.util.List<String> conditionalRequirements = new ArrayList<>();
		for (RequirementData req : conditionalRequirementsList.getSelectedValuesList())
		{
			conditionalRequirements.add(req.getId());
		}

		stepData.setStepRequirements(stepRequirements);
		stepData.setConditionalRequirements(conditionalRequirements);

		// Add the step to the list model
		dataModel.getStepListModel().addElement(stepData);
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
