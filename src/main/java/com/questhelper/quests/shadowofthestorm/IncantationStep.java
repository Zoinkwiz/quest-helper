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
package com.questhelper.quests.shadowofthestorm;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.steps.DetailedQuestStep;
import java.util.Collections;
import net.runelite.api.ItemID;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;

import java.util.HashMap;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import org.apache.commons.lang3.ArrayUtils;

public class IncantationStep extends DetailedQuestStep
{
	private final HashMap<Integer, String> words = new HashMap<Integer, String>()
	{{
		put(0, "Caldar");
		put(1, "Nahudu");
		put(2, "Agrith-Naar");
		put(3, "Camerinthum");
		put(4, "Tarren");
	}};

	private final boolean reverse;
	private String[] incantationOrder;
	private int incantationPosition = 0;

	public IncantationStep(QuestHelper questHelper, boolean reverse)
	{
		super(questHelper, "Click the demonic sigil and read the incantation.");
		ItemRequirement sigilHighlighted = new ItemRequirement("Demonic sigil", ItemID.DEMONIC_SIGIL);
		sigilHighlighted.setHighlightInInventory(true);
		this.addItemRequirements(Collections.singletonList(sigilHighlighted));
		this.reverse = reverse;
	}

	@Override
	public void startUp()
	{
		super.startUp();
		updateHints();
	}

	@Override
	public void onWidgetLoaded(WidgetLoaded event)
	{
		int groupId = event.getGroupId();
		if (groupId == WidgetID.DIALOG_OPTION_GROUP_ID)
		{
			clientThread.invokeLater(this::updateChoiceIfRequired);
		}

		super.onWidgetLoaded(event);
	}

	/**
	 * This checks for the demonic sigil being clicked, indicating the start of the incantation.
	 */
	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if(event.getWidgetId() == WidgetInfo.INVENTORY.getId() && event.getMenuOption().equals("Chant"))
		{
			incantationPosition = 0;
		}
	}

	/**
	 * Updates the choices highlighted as the incantation progresses.
	 */
	private void updateChoiceIfRequired()
	{
		if (!shouldUpdateChoice())
		{
			return;
		}

		// As the incantations have all the same dialogs we want to reset the choices after each dialog
		// as we want only the correct one to be highlighted
		choices.resetChoices();
		addDialogStep(incantationOrder[incantationPosition]);
		incantationPosition++;
	}

	private boolean shouldUpdateChoice()
	{
		Widget widget = client.getWidget(WidgetID.DIALOG_OPTION_GROUP_ID, 1);
		if (widget == null)
		{
			return false;
		}

		Widget[] children = widget.getChildren();
		if (children == null || children.length < 3)
		{
			return false;
		}

		Widget childWidget = widget.getChild(2);
		return childWidget != null && "Nahudu".equals(childWidget.getText());
	}

	/**
	 * {@inheritDoc}
	 */
	protected void updateHints()
	{
		if (incantationOrder != null || (client.getVarbitValue(1374) == 0 && client.getVarbitValue(1375) == 0))
		{
			return;
		}
		incantationOrder = new String[]{
			words.get(client.getVarbitValue(1373)),
			words.get(client.getVarbitValue(1374)),
			words.get(client.getVarbitValue(1375)),
			words.get(client.getVarbitValue(1376)),
			words.get(client.getVarbitValue(1377))
		};
		if (reverse)
		{
			ArrayUtils.reverse(incantationOrder);
		}
		String incantString = "Say the following in order: " + String.join(", ", incantationOrder);

		setText("Click the demonic sigil and read the incantation.");
		addText(incantString);
	}
}
