/*
 * Copyright (c) 2026, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.maker;

import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.util.Operation;
import lombok.Getter;

import static com.questhelper.maker.HelperConstructModels.DraftStepAttachedRequirement;

/**
 * Shared varbit id / value / operation / display for order-slot varbits and VARBIT step attachments.
 */
@Getter
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
}
