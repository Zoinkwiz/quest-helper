package com.questhelper.managers;

/**
 * Public façade for NPC / Object / Generic (text) step rows in the Quest Helper Maker step library.
 * Maps to the internal {@link HelperConstructModels.StepKind} used in persisted drafts.
 */
public enum ConstructStepKind
{
	NPC(HelperConstructModels.StepKind.NPC),
	OBJECT(HelperConstructModels.StepKind.OBJECT),
	/** Generic step: optional world point and attachments; not tied to a specific NPC/object id. */
	TEXT(HelperConstructModels.StepKind.TEXT);

	private final HelperConstructModels.StepKind stepKind;

	ConstructStepKind(HelperConstructModels.StepKind stepKind)
	{
		this.stepKind = stepKind;
	}

	HelperConstructModels.StepKind stepKind()
	{
		return stepKind;
	}
}
