package com.questhelper.panel.questmaking;

import com.questhelper.questimport.QuestStepData;
import com.questhelper.questimport.RequirementData;
import com.questhelper.questimport.StepData;
import javax.swing.DefaultListModel;

public class DataModel
{
	private DefaultListModel<StepData> stepListModel;
	private DefaultListModel<RequirementData> requirementListModel;
	private DefaultListModel<QuestStepData> questStepListModel;

	public DataModel()
	{
		stepListModel = new DefaultListModel<>();
		requirementListModel = new DefaultListModel<>();
		questStepListModel = new DefaultListModel<>();
	}

	public DefaultListModel<StepData> getStepListModel()
	{
		return stepListModel;
	}

	public DefaultListModel<RequirementData> getRequirementListModel()
	{
		return requirementListModel;
	}

	public DefaultListModel<QuestStepData> getQuestStepListModel()
	{
		return questStepListModel;
	}
}
