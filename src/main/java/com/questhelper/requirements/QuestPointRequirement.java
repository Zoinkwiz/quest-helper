package com.questhelper.requirements;

import com.questhelper.steps.conditional.Operation;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.VarPlayer;

public class QuestPointRequirement extends Requirement
{
	@Getter
	private int requiredQuestPoints;
	private Operation operation;

	public QuestPointRequirement(int requiredQuestPoints) {
		this.requiredQuestPoints = requiredQuestPoints;
		this.operation = Operation.GREATER_EQUAL;
	}

	public QuestPointRequirement(int requiredQuestPoints, Operation operation) {
		this.requiredQuestPoints = requiredQuestPoints;
		this.operation = operation;
	}
	@Override
	public boolean check(Client client)
	{
		return operation.check(client.getVar(VarPlayer.QUEST_POINTS), requiredQuestPoints);
	}

	@Override
	public String getDisplayText()
	{
		return getRequiredQuestPoints() + " Quest Points";
	}
}
