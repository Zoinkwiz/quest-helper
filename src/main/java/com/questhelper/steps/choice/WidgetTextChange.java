/*
 * Copyright (c) 2021, Zoinkwiz
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
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;

public class WidgetTextChange extends WidgetChoiceStep
{
	private final String textChange;

	public WidgetTextChange(QuestHelperConfig config, String choice, int groupId, int childId, String textChange)
	{
		super(config, choice, groupId, childId);
		this.textChange = textChange;
	}

	@Override
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
					if (excludedStrings.contains(currentExclusionChoice.getText()))
					{
						return;
					}
				}
			}
		}
		Widget dialogChoice = client.getWidget(groupId, childId);
		if (dialogChoice != null)
		{
			highlightText(dialogChoice, -1);
		}
	}

	@Override
	protected void highlightText(Widget text, int option)
	{
		if (!config.showTextHighlight())
		{
			return;
		}

		text.setText(textChange);
	}
}
