package com.questhelper.requirements;

import com.questhelper.steps.conditional.Operation;
import java.util.Locale;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Varbits;

@Getter
public class VarbitRequirement extends Requirement
{
	private int varbitID;
	private int requiredValue;
	private Operation operation;
	private String displayText;

	public VarbitRequirement(int varbitID, Operation operation, int requiredValue, String displayText) {
		this.varbitID = varbitID;
		this.operation = operation;
		this.requiredValue = requiredValue;
		this.displayText = displayText;
	}

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
