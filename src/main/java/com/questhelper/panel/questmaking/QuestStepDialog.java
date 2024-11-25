package com.questhelper.panel.questmaking;

import com.questhelper.questimport.QuestStepData;
import com.questhelper.questimport.RequirementData;
import com.questhelper.questimport.StepData;
import lombok.Getter;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class QuestStepDialog extends JDialog
{
	private JComboBox<StepData> stepComboBox;
	private JList<RequirementData> conditionalRequirementsList;
	@Getter
	private QuestStepData questStepData;

	public QuestStepDialog(Window owner, DefaultListModel<StepData> stepListModel, DefaultListModel<RequirementData> requirementListModel)
	{
		super(owner, "Add Quest Step", ModalityType.APPLICATION_MODAL);
		setLayout(new BorderLayout());

		// Step selection
		stepComboBox = new JComboBox<>(new Vector<>(Collections.list(stepListModel.elements())));

		// Conditional requirements selection
		conditionalRequirementsList = new JList<>(requirementListModel);
		conditionalRequirementsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		// Buttons
		JButton addButton = new JButton("Add");
		addButton.addActionListener(e -> addQuestStep());

		// Layout
		JPanel topPanel = new JPanel(new GridLayout(2, 1));
		topPanel.add(new JLabel("Select Step:"));
		topPanel.add(stepComboBox);

		JPanel middlePanel = new JPanel(new BorderLayout());
		middlePanel.add(new JLabel("Conditional Requirements:"), BorderLayout.NORTH);
		middlePanel.add(new JScrollPane(conditionalRequirementsList), BorderLayout.CENTER);

		add(topPanel, BorderLayout.NORTH);
		add(middlePanel, BorderLayout.CENTER);
		add(addButton, BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(owner);
	}

	private void addQuestStep()
	{
		StepData selectedStep = (StepData) stepComboBox.getSelectedItem();
		if (selectedStep == null)
		{
			JOptionPane.showMessageDialog(this, "Please select a step.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Get selected requirement IDs
		List<String> conditionalRequirements = new ArrayList<>();
		for (RequirementData req : conditionalRequirementsList.getSelectedValuesList())
		{
			conditionalRequirements.add(req.getId());
		}

		// Create QuestStepData
		questStepData = new QuestStepData();
		questStepData.setStepId(selectedStep.getId());
		questStepData.setConditionalRequirements(conditionalRequirements);

		dispose();
	}
}
