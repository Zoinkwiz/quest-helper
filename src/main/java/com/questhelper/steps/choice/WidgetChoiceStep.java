/*
 * Copyright (c) 2020, Zoinkwiz
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
package com.questhelper.steps.choice;

import com.questhelper.QuestHelperConfig;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class WidgetChoiceStep
{
	protected final QuestHelperConfig config;

	@Getter
	private final String choice;

	@Setter
	String expectedTextInWidget;

	private Pattern pattern;

	protected List<String> excludedStrings;
	protected int excludedGroupId;
	protected int excludedChildId;

	private final int choiceById;

	@Getter
	protected final int groupId;
	protected final int childId;

	protected final int varbitId;
	protected int varbitValue = -1;
	protected final Map<Integer, String> varbitValueToAnswer;

	protected boolean shouldNumber = false;

	@Setter
	@Getter
	private int groupIdForChecking;

	public WidgetChoiceStep(QuestHelperConfig config, String choice, int groupId, int childId)
	{
		this.config = config;
		this.choice = choice;
		this.choiceById = -1;
		this.groupId = groupId;
		this.groupIdForChecking = groupId;
		this.childId = childId;
		this.pattern = null;
		this.varbitId = -1;
		this.varbitValueToAnswer = null;
	}

	public WidgetChoiceStep(QuestHelperConfig config, int groupId, int childId)
	{
		this.config = config;
		this.choice = null;
		this.choiceById = -1;
		this.groupId = groupId;
		this.groupIdForChecking = groupId;
		this.childId = childId;
		this.pattern = null;
		this.varbitId = -1;
		this.varbitValueToAnswer = null;
	}

	public WidgetChoiceStep(QuestHelperConfig config, Pattern pattern, int groupId, int childId)
	{
		this.config = config;
		this.choice = null;
		this.choiceById = -1;
		this.groupId = groupId;
		this.groupIdForChecking = groupId;
		this.childId = childId;
		this.pattern = pattern;
		this.varbitId = -1;
		this.varbitValueToAnswer = null;
	}

	public WidgetChoiceStep(QuestHelperConfig config, int choiceId, int groupId, int childId)
	{
		this.config = config;
		this.choice = null;
		this.choiceById = choiceId;
		this.groupId = groupId;
		this.groupIdForChecking = groupId;
		this.childId = childId;
		this.varbitId = -1;
		this.varbitValueToAnswer = null;
	}

	public WidgetChoiceStep(QuestHelperConfig config, int choiceId, String choice, int groupId, int childId)
	{
		this.config = config;
		this.choice = choice;
		this.choiceById = choiceId;
		this.groupId = groupId;
		this.groupIdForChecking = groupId;
		this.childId = childId;
		this.varbitId = -1;
		this.varbitValueToAnswer = null;
	}

	public WidgetChoiceStep(QuestHelperConfig config, int choiceId, Pattern pattern, int groupId, int childId)
	{
		this.config = config;
		this.choice = null;
		this.choiceById = choiceId;
		this.groupId = groupId;
		this.groupIdForChecking = groupId;
		this.childId = childId;
		this.pattern = pattern;
		this.varbitId = -1;
		this.varbitValueToAnswer = null;
	}

	public WidgetChoiceStep(QuestHelperConfig config, int groupId, int childId, int varbitId, Map<Integer, String> varbitValueToAnswer)
	{
		this.config = config;
		this.choice = null;
		this.choiceById = -1;
		this.groupId = groupId;
		this.groupIdForChecking = groupId;
		this.childId = childId;
		this.pattern = null;
		this.varbitId = varbitId;
		this.varbitValueToAnswer = varbitValueToAnswer;
	}

	public void addExclusion(int excludedGroupId, int excludedChildId, String excludedString)
	{
		this.excludedStrings = Collections.singletonList(excludedString);
		this.excludedGroupId = excludedGroupId;
		this.excludedChildId = excludedChildId;
	}

	public void addExclusions(int excludedGroupId, int excludedChildId, String... excludedStrings)
	{
		this.excludedStrings = Arrays.asList(excludedStrings);
		this.excludedGroupId = excludedGroupId;
		this.excludedChildId = excludedChildId;
	}

	public void highlightChoice(Client client)
	{
		Widget exclusionDialogChoice = client.getWidget(excludedGroupId, excludedChildId);
		if (exclusionDialogChoice != null)
		{
			Widget[] exclusionChoices = exclusionDialogChoice.getChildren();
			if (exclusionChoices != null)
			{
				for (Widget currentExclusionChoice : exclusionChoices)
				{
					for (String excludedString : excludedStrings)
					{
						if (currentExclusionChoice.getText().contains(excludedString))
						{
							return;
						}
					}
				}
			}
		}
		Widget dialogChoice = client.getWidget(groupId, childId);

		if (dialogChoice == null)
		{
			return;
		}
		if(varbitId != -1){
			varbitValue = client.getVarbitValue(varbitId);
		}

		Widget[] choices = dialogChoice.getChildren();
		checkWidgets(choices);
		Widget[] nestedChildren = dialogChoice.getNestedChildren();
		checkWidgets(nestedChildren);
	}

	protected void checkWidgets(Widget[] choices)
	{
		if (choices != null && choices.length > 0)
		{
			if (choiceById != -1 && choices[choiceById] != null)
			{
				if ((choice != null && choice.equals(choices[choiceById].getText())) ||
					(pattern != null && pattern.matcher(choices[choiceById].getText()).find()) ||
					(choice == null && pattern == null))
				{
					highlightText(choices[choiceById], choiceById);
				}
			}
			else if (varbitId != -1 && varbitValue != -1 && varbitValueToAnswer != null)
			{
				String choice = varbitValueToAnswer.get(varbitValue);
				for (int i = 0; i < choices.length; i++)
				{
					if (choices[i].getText().equals(choice))
					{
						highlightText(choices[i], i);
						return;
					}
				}
			}
			else
			{
				for (int i = 0; i < choices.length; i++)
				{
					if (choices[i].getText().equals(choice) ||
						(pattern != null && pattern.matcher(choices[i].getText()).find()))
					{
						highlightText(choices[i], i);
						return;
					}
				}
			}
		}
	}

	protected void highlightText(Widget text, int option)
	{
		if (!config.showTextHighlight())
		{
			return;
		}

		if (shouldNumber)
		{
			text.setText("[" + option + "] " + text.getText());
		}

		text.setTextColor(config.textHighlightColor().getRGB());
		text.setOnMouseLeaveListener((JavaScriptCallback) ev -> text.setTextColor(config.textHighlightColor().getRGB()));
	}
}
