package com.questhelper.steps.choice;

import net.runelite.api.widgets.WidgetInfo;

public class DialogChoiceStep extends WidgetChoiceStep
{
	public DialogChoiceStep(String choice)
	{
		super(choice,219, 1);
	}

	public DialogChoiceStep(int choiceId, String choice)
	{
		super(choiceId, choice,219, 1);
	}

	public DialogChoiceStep(int choice)
	{
		super(choice, 219, 1);
	}
}
