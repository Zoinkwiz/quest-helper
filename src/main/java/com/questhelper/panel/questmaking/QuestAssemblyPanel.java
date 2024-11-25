package com.questhelper.panel.questmaking;

import com.questhelper.questimport.QuestStepData;
import javax.swing.*;
import java.awt.*;

public class QuestAssemblyPanel extends JPanel
{
	private DataModel dataModel;
	private JList<QuestStepData> questStepList;

	public QuestAssemblyPanel(DataModel dataModel)
	{
		this.dataModel = dataModel;
		setLayout(new BorderLayout());
		initializeUI();
	}

	private void initializeUI()
	{
		// Quest steps list
		questStepList = new JList<>(dataModel.getQuestStepListModel());
		questStepList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Buttons panel
		JPanel buttonsPanel = new JPanel();
		JButton addQuestStepButton = new JButton("Add Quest Step");
		JButton removeQuestStepButton = new JButton("Remove Quest Step");
		JButton moveUpButton = new JButton("Move Up");
		JButton moveDownButton = new JButton("Move Down");

		buttonsPanel.add(addQuestStepButton);
		buttonsPanel.add(removeQuestStepButton);
		buttonsPanel.add(moveUpButton);
		buttonsPanel.add(moveDownButton);

		// Add action listeners
		addQuestStepButton.addActionListener(e -> addQuestStep());
		removeQuestStepButton.addActionListener(e -> removeQuestStep());
		moveUpButton.addActionListener(e -> moveQuestStepUp());
		moveDownButton.addActionListener(e -> moveQuestStepDown());

		// Assemble the panel
		add(new JScrollPane(questStepList), BorderLayout.CENTER);
		add(buttonsPanel, BorderLayout.SOUTH);
	}

	private void addQuestStep()
	{
		QuestStepDialog questStepDialog = new QuestStepDialog(SwingUtilities.getWindowAncestor(this), dataModel.getStepListModel(), dataModel.getRequirementListModel());
		questStepDialog.setVisible(true);

		QuestStepData questStepData = questStepDialog.getQuestStepData();
		if (questStepData != null)
		{
			dataModel.getQuestStepListModel().addElement(questStepData);
		}
	}

	private void removeQuestStep()
	{
		int selectedIndex = questStepList.getSelectedIndex();
		if (selectedIndex != -1)
		{
			dataModel.getQuestStepListModel().remove(selectedIndex);
		}
	}

	private void moveQuestStepUp()
	{
		int selectedIndex = questStepList.getSelectedIndex();
		if (selectedIndex > 0)
		{
			QuestStepData questStep = dataModel.getQuestStepListModel().getElementAt(selectedIndex);
			dataModel.getQuestStepListModel().remove(selectedIndex);
			dataModel.getQuestStepListModel().add(selectedIndex - 1, questStep);
			questStepList.setSelectedIndex(selectedIndex - 1);
		}
	}

	private void moveQuestStepDown()
	{
		int selectedIndex = questStepList.getSelectedIndex();
		if (selectedIndex < dataModel.getQuestStepListModel().getSize() - 1)
		{
			QuestStepData questStep = dataModel.getQuestStepListModel().getElementAt(selectedIndex);
			dataModel.getQuestStepListModel().remove(selectedIndex);
			dataModel.getQuestStepListModel().add(selectedIndex + 1, questStep);
			questStepList.setSelectedIndex(selectedIndex + 1);
		}
	}
}
