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
import javax.swing.DefaultListModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class DataModel
{
	// Listeners and notification methods
	private transient List<Runnable> stepChangeListeners = new ArrayList<>();
	private transient List<Runnable> requirementChangeListeners = new ArrayList<>();
	private transient List<Runnable> questStepChangeListeners = new ArrayList<>();

	private final DefaultListModel<StepData> stepListModel;
	private final DefaultListModel<RequirementData> requirementListModel;
	private final DefaultListModel<QuestStepData> questStepListModel;

	public DataModel()
	{
		stepListModel = new DefaultListModel<>();
		requirementListModel = new DefaultListModel<>();
		questStepListModel = new DefaultListModel<>();
	}

	public void importData(File file, Gson gson) throws IOException
	{
		// Read JSON from the specified file
		String json = Files.readString(file.toPath());

		// Deserialize the quest data
		QuestData questData = gson.fromJson(json, QuestData.class);

		// Replace current data with imported data
		setQuestData(questData);

		// Notify listeners to update UI components
		notifyStepChangeListeners();
		notifyRequirementChangeListeners();
		notifyQuestStepChangeListeners();
	}

	// Method to set the data from QuestData
	public void setQuestData(QuestData questData)
	{
		// Clear existing data
		getStepListModel().clear();
		getRequirementListModel().clear();
		getQuestStepListModel().clear();

		// Add steps
		for (StepData step : questData.getSteps())
		{
			getStepListModel().addElement(step);
		}

		// Add requirements
		for (RequirementData requirement : questData.getRequirements())
		{
			getRequirementListModel().addElement(requirement);
		}

		// Add quest steps
		for (QuestStepData questStep : questData.getQuestSteps())
		{
			getQuestStepListModel().addElement(questStep);
		}
	}

	// Export the entire data model to a file
	public void exportData(File file, Gson gson) throws IOException
	{
		// Collect all steps and requirements
		List<StepData> steps = Collections.list(getStepListModel().elements());
		List<RequirementData> requirements = Collections.list(getRequirementListModel().elements());
		List<QuestStepData> questSteps = Collections.list(getQuestStepListModel().elements());

		// Create a QuestData object
		QuestData questData = new QuestData();
		questData.setSteps(steps);
		questData.setRequirements(requirements);
		questData.setQuestSteps(questSteps);

		String json = gson.toJson(questData);

		// Save JSON to the specified file
		try (FileWriter writer = new FileWriter(file))
		{
			writer.write(json);
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

	public void addQuestStepChangeListener(Runnable listener)
	{
		questStepChangeListeners.add(listener);
	}

	public void notifyQuestStepChangeListeners()
	{
		for (Runnable listener : questStepChangeListeners)
		{
			listener.run();
		}
	}

	// Ensure that transient lists are initialized after deserialization
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
	{
		ois.defaultReadObject();
		stepChangeListeners = new ArrayList<>();
		requirementChangeListeners = new ArrayList<>();
		questStepChangeListeners = new ArrayList<>();
	}
}
