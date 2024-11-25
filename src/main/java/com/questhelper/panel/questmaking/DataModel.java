package com.questhelper.panel.questmaking;

import com.questhelper.questimport.QuestStepData;
import com.questhelper.questimport.RequirementData;
import com.questhelper.questimport.StepData;
import lombok.Getter;
import javax.swing.DefaultListModel;
import java.util.ArrayList;
import java.util.List;

@Getter
public class DataModel
{
	private final DefaultListModel<StepData> stepListModel;
	private final DefaultListModel<RequirementData> requirementListModel;
	private final DefaultListModel<QuestStepData> questStepListModel;

	public DataModel()
	{
		stepListModel = new DefaultListModel<>();
		requirementListModel = new DefaultListModel<>();
		questStepListModel = new DefaultListModel<>();
	}

	private List<Runnable> requirementChangeListeners = new ArrayList<>();

	private List<Runnable> stepChangeListeners = new ArrayList<>();

	public void addRequirementChangeListener(Runnable listener)
	{
		requirementChangeListeners.add(listener);
	}

	public void notifyRequirementChangeListeners()
	{
		for (Runnable listener : requirementChangeListeners)
		{
			listener.run();
		}
	}

	public void addStepChangeListener(Runnable listener)
	{
		stepChangeListeners.add(listener);
	}

	public void notifyStepChangeListeners()
	{
		for (Runnable listener : stepChangeListeners)
		{
			listener.run();
		}
	}
}

