package com.questhelper.requirements;

import com.questhelper.requirements.conditional.Operation;
import java.util.Locale;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Varbits;

/**
 * Checks if a player's varbit value is meets the required value as determined by the
 * {@link Operation}
 */
@Getter
public class VarbitRequirement extends AbstractRequirement
{
	private final int varbitID;
	private final int requiredValue;
	private final Operation operation;
	private final String displayText;

	/**
	 * Check if the player's varbit value meets the required level using the given
	 * {@link Operation}.
	 *
	 * @param varbitID the {@link Varbits} id to use
	 * @param operation the {@link Operation} to check with
	 * @param requiredValue the required varbit value to pass this requirement
	 * @param displayText the display text
	 */
	public VarbitRequirement(int varbitID, Operation operation, int requiredValue, String displayText) {
		this.varbitID = varbitID;
		this.operation = operation;
		this.requiredValue = requiredValue;
		this.displayText = displayText;
	}

	/**
	 * Check if the player's {@link Varbits} value meets the required level using the given
	 * {@link Operation}.
	 *
	 * @param varbit the {@link Varbits} to check
	 * @param operation the {@link Operation} to check with
	 * @param requiredValue the required varbit value to pass this requirement
	 * @param displayText the display text
	 */
	public VarbitRequirement(Varbits varbit, Operation operation, int requiredValue, String displayText) {
		this(varbit.getId(), operation, requiredValue, displayText);
	}

	@Override
	public boolean check(Client client)
	{
		return operation.check(client.getVarbitValue(varbitID), requiredValue);
	}

	@Override
	public String getDisplayText()
	{
		if (displayText != null) {
			return displayText;
		}
		return varbitID + " must be + " + operation.name().toLowerCase(Locale.ROOT) + " " + requiredValue;
	}
}
