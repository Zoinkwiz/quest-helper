package com.questhelper.steps.choice;

import com.questhelper.QuestHelperConfig;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Pattern;

public class DialogChoiceStep extends WidgetChoiceStep
{
	public static final int DIALOG_WIDGET_GROUP_ID = 219;
	public static final int DIALOG_WIDGET_CHILD_ID = 1;

	@Setter
	@Getter
	protected String expectedPreviousLine;

	public DialogChoiceStep(QuestHelperConfig config, String choice)
	{
		super(config, choice, DIALOG_WIDGET_GROUP_ID, DIALOG_WIDGET_CHILD_ID);
		shouldNumber = true;
	}

	public DialogChoiceStep(QuestHelperConfig config, Pattern pattern)
	{
		super(config, pattern, DIALOG_WIDGET_GROUP_ID, DIALOG_WIDGET_CHILD_ID);
		shouldNumber = true;
	}

	public DialogChoiceStep(QuestHelperConfig config, int choiceId, String choice)
	{
		super(config, choiceId, choice, DIALOG_WIDGET_GROUP_ID, DIALOG_WIDGET_CHILD_ID);
		shouldNumber = true;
	}

	public DialogChoiceStep(QuestHelperConfig config, int choiceId, Pattern pattern)
	{
		super(config, choiceId, pattern, DIALOG_WIDGET_GROUP_ID, DIALOG_WIDGET_CHILD_ID);
		shouldNumber = true;
	}

	public DialogChoiceStep(QuestHelperConfig config, int choice)
	{
		super(config, choice, DIALOG_WIDGET_GROUP_ID, DIALOG_WIDGET_CHILD_ID);
		shouldNumber = true;
	}

	public DialogChoiceStep(QuestHelperConfig config, int varbitId, Map<Integer, String> valueToAnswer)
	{
		super(config, DIALOG_WIDGET_GROUP_ID, DIALOG_WIDGET_CHILD_ID, varbitId, valueToAnswer);
		shouldNumber = true;

	}
}
