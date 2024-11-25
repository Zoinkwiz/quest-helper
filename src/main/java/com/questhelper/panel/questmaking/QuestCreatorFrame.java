package com.questhelper.panel.questmaking;

import com.google.gson.Gson;
import com.questhelper.questimport.QuestData;
import com.questhelper.questimport.QuestStepData;
import com.questhelper.questimport.RequirementData;
import com.questhelper.questimport.StepData;
import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class QuestCreatorFrame extends JFrame
{
	private final Gson gson;
	// Panels
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

		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem saveMenuItem = new JMenuItem("Save Quest");
		saveMenuItem.addActionListener(e -> saveQuest(dataModel));
		fileMenu.add(saveMenuItem);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
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

