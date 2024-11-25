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
