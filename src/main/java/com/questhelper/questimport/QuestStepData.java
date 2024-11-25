package com.questhelper.questimport;

import lombok.Data;
import java.util.List;

@Data
public class QuestStepData
{
	private String stepId;
	private List<String> conditionalRequirements;

	// Getters and setters

	@Override
	public String toString()
	{
		return "Quest Step: " + stepId;
	}
}
