package com.questhelper.steps.choice;

import com.questhelper.QuestHelperConfig;

import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;

public class DialogChoiceStep extends WidgetChoiceStep
{
	@Setter
	@Getter
	protected String expectedPreviousLine;

	public DialogChoiceStep(QuestHelperConfig config, String choice)
	{
		super(config, choice, 219, 1);
		shouldNumber = true;
	}

	public DialogChoiceStep(QuestHelperConfig config, Pattern pattern)
	{
		super(config, pattern, 219, 1);
		shouldNumber = true;
	}

	public DialogChoiceStep(QuestHelperConfig config, int choiceId, String choice)
	{
		super(config, choiceId, choice, 219, 1);
		shouldNumber = true;
	}

	public DialogChoiceStep(QuestHelperConfig config, int choiceId, Pattern pattern)
	{
		super(config, choiceId, pattern, 219, 1);
		shouldNumber = true;
	}

	public DialogChoiceStep(QuestHelperConfig config, int choice)
	{
		super(config, choice, 219, 1);
		shouldNumber = true;
	}
}
