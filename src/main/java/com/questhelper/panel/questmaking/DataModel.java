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
import java.nio.file.Files;
import java.util.Collections;
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

	public void importData(File file, Gson gson) throws IOException
	{
		String json = Files.readString(file.toPath());

		QuestData questData = gson.fromJson(json, QuestData.class);

		setQuestData(questData);
	}

	public void setQuestData(QuestData questData)
	{
		getStepListModel().clear();
		getRequirementListModel().clear();
		getQuestStepListModel().clear();

		for (StepData step : questData.getSteps())
		{
			getStepListModel().addElement(step);
		}

		for (RequirementData requirement : questData.getRequirements())
		{
			getRequirementListModel().addElement(requirement);
		}

		for (QuestStepData questStep : questData.getQuestSteps())
		{
			getQuestStepListModel().addElement(questStep);
		}
	}

	public void exportData(File file, Gson gson) throws IOException
	{
		List<StepData> steps = Collections.list(getStepListModel().elements());
		List<RequirementData> requirements = Collections.list(getRequirementListModel().elements());
		List<QuestStepData> questSteps = Collections.list(getQuestStepListModel().elements());

		QuestData questData = new QuestData();
		questData.setSteps(steps);
		questData.setRequirements(requirements);
		questData.setQuestSteps(questSteps);

		String json = gson.toJson(questData);

		try (FileWriter writer = new FileWriter(file))
		{
			writer.write(json);
		}
	}

	public boolean containsStepWithId(String id)
	{
		for (int i = 0; i < stepListModel.size(); i++)
		{
			if (stepListModel.getElementAt(i).getId().equals(id))
			{
				return true;
			}
		}
		return false;
	}

	public boolean containsRequirementWithId(String id)
	{
		for (int i = 0; i < requirementListModel.size(); i++)
		{
			if (requirementListModel.getElementAt(i).getId().equals(id))
			{
				return true;
			}
		}
		return false;
	}
}
