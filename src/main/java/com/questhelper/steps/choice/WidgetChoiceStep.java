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

import java.awt.Color;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

public class WidgetChoiceStep
{
	@Getter
	private final String choice;

	private String excludedString;
	private int excludedGroupId;
	private int excludedChildId;

	private final int choiceById;

	@Getter
	private final int groupId;
	private final int childId;

	private final int TEXT_HIGHLIGHT_COLOR = Color.CYAN.darker().getRGB();

	public WidgetChoiceStep(String choice, int groupId, int childId)
	{
		this.choice = choice;
		this.choiceById = -1;
		this.groupId = groupId;
		this.childId = childId;
	}

	public WidgetChoiceStep(int choiceId, int groupId, int childId)
	{
		this.choice = null;
		this.choiceById = choiceId;
		this.groupId = groupId;
		this.childId = childId;
	}

	public WidgetChoiceStep(int choiceId, String choice, int groupId, int childId)
	{
		this.choice = choice;
		this.choiceById = choiceId;
		this.groupId = groupId;
		this.childId = childId;
	}

	public void addExclusion(String excludedString, int excludedGroupId, int excludedChildId)
	{
		this.excludedString = excludedString;
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
					if (currentExclusionChoice.getText().equals(excludedString))
					{
						return;
					}
				}
			}
		}

		Widget dialogChoice = client.getWidget(groupId, childId);
		if (dialogChoice != null)
		{
			Widget[] choices = dialogChoice.getChildren();
			if (choices != null)
			{
				if (choiceById != -1 && choices[choiceById] != null)
				{
					if (choice == null || choice.equals(choices[choiceById].getText()))
					{
						highlightText(choices[choiceById]);
					}
				}
				else
				{
					for (Widget currentChoice : choices)
					{
						if (currentChoice.getText().equals(choice))
						{
							highlightText(currentChoice);
							return;
						}
					}
				}
			}
		}
	}

	private void highlightText(Widget text)
	{
		text.setTextColor(TEXT_HIGHLIGHT_COLOR);
		text.setOnMouseLeaveListener((JavaScriptCallback) ev -> text.setTextColor(TEXT_HIGHLIGHT_COLOR));
	}
}