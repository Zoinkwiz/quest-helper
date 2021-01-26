package com.questhelper.requirements;

import com.questhelper.requirements.conditional.Operation;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.VarPlayer;

/**
 * Requirement that checks if a player has a required number of quest points.
 */
public class QuestPointRequirement extends AbstractRequirement
{
	@Getter
	private int requiredQuestPoints;
	private Operation operation;

	/**
	 * Checks if a player has a required number of quest points.
	 * By default, it uses {@link Operation#GREATER_EQUAL}.
	 *
	 * @param requiredQuestPoints the required number of quest points
	 */
	public QuestPointRequirement(int requiredQuestPoints) {
		this.requiredQuestPoints = requiredQuestPoints;
		this.operation = Operation.GREATER_EQUAL;
	}

	/**
	 * Checks if a player has a required number of quest points.
	 *
	 * @param requiredQuestPoints the required number of quest points
	 * @param operation the {@link Operation} to use.
	 */
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
