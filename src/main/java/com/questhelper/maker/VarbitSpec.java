package com.questhelper.maker;

import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.util.Operation;

import static com.questhelper.maker.HelperConstructModels.DraftStepAttachedRequirement;

/**
 * Shared varbit id / value / operation / display for order-slot varbits and VARBIT step attachments.
 */
public final class VarbitSpec
{
	private final int varbitId;
	private final int requiredValue;
	private final Operation operation;
	private final String displayText;

	private VarbitSpec(int varbitId, int requiredValue, Operation operation, String displayText)
	{
		this.varbitId = varbitId;
		this.requiredValue = requiredValue;
		this.operation = operation;
		this.displayText = displayText;
	}

	public static Operation parseOperation(String operationName, Operation defaultOp)
	{
		if (operationName == null || operationName.isBlank())
		{
			return defaultOp;
		}
		try
		{
			return Operation.valueOf(operationName.trim());
		}
		catch (IllegalArgumentException ex)
		{
			return defaultOp;
		}
	}

	public static VarbitSpec fromStepAttachment(DraftStepAttachedRequirement a)
	{
		if (a == null)
		{
			return new VarbitSpec(0, 1, Operation.EQUAL, null);
		}
		int vid = a.getVarbitId() == null ? 0 : a.getVarbitId();
		int val = a.getVarbitRequiredValue() == null ? 1 : a.getVarbitRequiredValue();
		String disp = a.getVarbitDisplayText();
		if (disp != null && disp.isBlank())
		{
			disp = null;
		}
		return new VarbitSpec(vid, val, parseOperation(a.getVarbitOperation(), Operation.EQUAL), disp);
	}

	/** @return {@code null} when the operation name is not a valid {@link Operation} constant. */
	public static VarbitSpec tryFromStepAttachmentEdit(HelperConstructManager.StepAttachmentEdit edit)
	{
		if (edit == null || edit.getVarbitId() == null)
		{
			return null;
		}
		try
		{
			Operation.valueOf(edit.getVarbitOperation() == null ? "EQUAL" : edit.getVarbitOperation().trim());
		}
		catch (IllegalArgumentException ex)
		{
			return null;
		}
		int val = edit.getVarbitRequiredValue() == null ? 1 : edit.getVarbitRequiredValue();
		String disp = edit.getVarbitDisplayText();
		if (disp != null && disp.isBlank())
		{
			disp = null;
		}
		return new VarbitSpec(edit.getVarbitId(), val, parseOperation(edit.getVarbitOperation(), Operation.EQUAL), disp);
	}

	public VarbitRequirement toVarbitRequirement()
	{
		return new VarbitRequirement(varbitId, operation, requiredValue, displayText);
	}

	public int getVarbitId()
	{
		return varbitId;
	}

	public int getRequiredValue()
	{
		return requiredValue;
	}

	public Operation getOperation()
	{
		return operation;
	}

	public String getDisplayText()
	{
		return displayText;
	}
}
