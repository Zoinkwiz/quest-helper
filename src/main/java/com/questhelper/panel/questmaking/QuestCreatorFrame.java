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

import com.google.gson.Gson;
import com.questhelper.questimport.QuestData;
import com.questhelper.questimport.QuestStepData;
import com.questhelper.questimport.RequirementData;
import com.questhelper.questimport.StepData;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class QuestCreatorFrame extends JFrame
{
	private final Gson gson;
	// Panels

	@Getter
	private StepMakingPanel stepMakingPanel;
	private RequirementMakingPanel requirementMakingPanel;
	private QuestAssemblyPanel questAssemblyPanel;

	public QuestCreatorFrame(Gson gson)
	{
		this.gson = gson;

		setTitle("Quest Creator");
		setSize(1000, 600);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null); // Center on screen

		// Initialize UI components
		initializeUI();
	}

	private void initializeUI()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		// Initialize data models
		DataModel dataModel = new DataModel();

		// Create panels
		stepMakingPanel = new StepMakingPanel(dataModel);
		requirementMakingPanel = new RequirementMakingPanel(dataModel);
		questAssemblyPanel = new QuestAssemblyPanel(dataModel);

		tabbedPane.addTab("Step Making", stepMakingPanel);
		tabbedPane.addTab("Requirement Making", requirementMakingPanel);
		tabbedPane.addTab("Quest Assembly", questAssemblyPanel);

		add(tabbedPane);

		JMenuBar menuBar = getMenuBar(dataModel);
		setJMenuBar(menuBar);
	}

	private void importProject(DataModel dataModel)
	{
		JFileChooser fileChooser = new JFileChooser();
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChooser.getSelectedFile();
			try
			{
				dataModel.importData(file, gson);
				JOptionPane.showMessageDialog(this, "Project imported successfully.", "Import Project", JOptionPane.INFORMATION_MESSAGE);
			}
			catch (IOException ex)
			{
				JOptionPane.showMessageDialog(this, "Failed to import project: " + ex.getMessage(), "Import Project", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void exportProject(DataModel dataModel)
	{
		JFileChooser fileChooser = new JFileChooser();
		int result = fileChooser.showSaveDialog(this);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChooser.getSelectedFile();
			try
			{
				dataModel.exportData(file, gson);
				JOptionPane.showMessageDialog(this, "Project exported successfully.", "Export Project", JOptionPane.INFORMATION_MESSAGE);
			}
			catch (IOException ex)
			{
				JOptionPane.showMessageDialog(this, "Failed to export project: " + ex.getMessage(), "Export Project", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@NotNull
	private JMenuBar getMenuBar(DataModel dataModel)
	{
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");

		JMenuItem saveMenuItem = new JMenuItem("Get JSON");
		JMenuItem importProjectMenuItem = new JMenuItem("Import Project");
		JMenuItem exportProjectMenuItem = new JMenuItem("Export Project");

		importProjectMenuItem.addActionListener(e -> importProject(dataModel));
		exportProjectMenuItem.addActionListener(e -> exportProject(dataModel));

		saveMenuItem.addActionListener(e -> saveQuest(dataModel));

		fileMenu.add(saveMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(importProjectMenuItem);
		fileMenu.add(exportProjectMenuItem);
		menuBar.add(fileMenu);
		return menuBar;
	}

	private void saveQuest(DataModel dataModel)
	{
		// Collect all steps and requirements
		List<StepData> steps = Collections.list(dataModel.getStepListModel().elements());
		List<RequirementData> requirements = Collections.list(dataModel.getRequirementListModel().elements());
		List<QuestStepData> questSteps = Collections.list(dataModel.getQuestStepListModel().elements());

		// Create a QuestData object
		QuestData questData = new QuestData();
		questData.setSteps(steps);
		questData.setRequirements(requirements);
		questData.setQuestSteps(questSteps);

		// Serialize the quest data
		String json = gson.toJson(questData);

		// Show the JSON in a dialog or save to a file
		JTextArea textArea = new JTextArea(json);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(600, 400));

		JOptionPane.showMessageDialog(this, scrollPane, "Quest JSON", JOptionPane.INFORMATION_MESSAGE);
	}
}
