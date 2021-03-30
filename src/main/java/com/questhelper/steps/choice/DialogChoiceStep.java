package com.questhelper.steps.choice;

import com.questhelper.QuestHelperConfig;

public class DialogChoiceStep extends WidgetChoiceStep
{

	public DialogChoiceStep(QuestHelperConfig config, String choice)
	{
		super(config, choice, 219, 1);
		shouldNumber = true;
	}

	public DialogChoiceStep(QuestHelperConfig config, int choiceId, String choice)
	{
		super(config, choiceId, choice, 219, 1);
		shouldNumber = true;
	}

	public DialogChoiceStep(QuestHelperConfig config, int choice)
	{
		super(config, choice, 219, 1);
		shouldNumber = true;
	}
}
