package com.questhelper.requirements;

import com.questhelper.steps.conditional.Operation;
import net.runelite.api.Client;
import net.runelite.api.Player;

/**
 * Checks if the player's combat level meets the required level
 */
public class CombatLevelRequirement extends Requirement
{
	private final int requiredLevel;
	private final Operation operation;

	/**
	 * Checks if the player's combat level meets the required level using the given
	 * {@link Operation}
	 *
	 * @param operation the {@link Operation} to use
	 * @param requiredLevel the required combat level
	 */
	public CombatLevelRequirement(Operation operation, int requiredLevel)
	{
		this.operation = operation;
		this.requiredLevel = requiredLevel;
	}

	/**
	 * Check if the player has the required combat level using {@link Operation#GREATER_EQUAL}
	 *
	 * @param requiredLevel the required combat level
	 */
	public CombatLevelRequirement(int requiredLevel)
	{
		this(Operation.GREATER_EQUAL, requiredLevel);
	}

	@Override
	public boolean check(Client client)
	{
		Player player = client.getLocalPlayer();
		return player != null && operation.check(player.getCombatLevel(), requiredLevel);
	}

	@Override
	public String getDisplayText()
	{
		return "Combat Level " + requiredLevel;
	}
}
