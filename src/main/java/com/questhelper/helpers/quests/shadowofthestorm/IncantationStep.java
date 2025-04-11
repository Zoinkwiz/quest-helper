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
package com.questhelper.helpers.quests.shadowofthestorm;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.steps.DetailedQuestStep;
import net.runelite.api.annotations.Varbit;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

import java.util.Collections;

public class IncantationStep extends DetailedQuestStep
{
	/**
	 * The index of the first word in the incantation, as per the order of the Demonic tome (reverse of the order Denath gives you).
	 * The order of the words received by Denath is reverse.
	 * The value of this varbit maps to the {@link IncantationStep#WORDS} array.
	 */
	private static final @Varbit int INCANTATION_WORD_1 = 1373;
	/**
	 * The index of the second word in the incantation, as per the order of the Demonic tome (reverse of the order Denath gives you).
	 * The order of the words received by Denath is reverse.
	 * The value of this varbit maps to the {@link IncantationStep#WORDS} array.
	 */
	private static final @Varbit int INCANTATION_WORD_2 = 1374;
	/**
	 * The index of the third word in the incantation, as per the order of the Demonic tome (reverse of the order Denath gives you).
	 * The order of the words received by Denath is reverse.
	 * The value of this varbit maps to the {@link IncantationStep#WORDS} array.
	 */
	private static final @Varbit int INCANTATION_WORD_3 = 1375;
	/**
	 * The index of the fourth word in the incantation, as per the order of the Demonic tome (reverse of the order Denath gives you).
	 * The order of the words received by Denath is reverse.
	 * The value of this varbit maps to the {@link IncantationStep#WORDS} array.
	 */
	private static final @Varbit int INCANTATION_WORD_4 = 1376;
	/**
	 * The index of the fifth word in the incantation, as per the order of the Demonic tome (reverse of the order Denath gives you).
	 * The order of the words received by Denath is reverse.
	 * The value of this varbit maps to the {@link IncantationStep#WORDS} array.
	 */
	private static final @Varbit int INCANTATION_WORD_5 = 1377;

	/**
	 * The possible words that that can be used the incantation
	 * The index correlates to the value of the varbit
	 */
	private static final String[] WORDS = {
		"Caldar",
		"Nahudu",
		"Agrith-Naar",
		"Camerinthum",
		"Tarren",
	};

	private final boolean reverse;
	private String[] incantationOrder;
	private int incantationPosition = 0;

	public IncantationStep(QuestHelper questHelper, boolean reverse)
	{
		super(questHelper, "Click the demonic sigil and read the incantation.");
		ItemRequirement sigilHighlighted = new ItemRequirement("Demonic sigil", ItemID.AGRITH_SIGIL);
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
		if (groupId == InterfaceID.CHATMENU)
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
		var widget = event.getWidget();

		if (widget == null) {
			return;
		}

		if (widget.getId() != InterfaceID.INVENTORY)
		{
			return;
		}

		if (event.getMenuOption().equals("Chant"))
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
		Widget widget = client.getWidget(InterfaceID.CHATMENU, 1);
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
		if (incantationOrder != null)
		{
			// The order has already been calculated
			return;
		}

		if (client.getVarbitValue(INCANTATION_WORD_2) == 0 && client.getVarbitValue(INCANTATION_WORD_3) == 0)
		{
			// The word order hasn't been received yet (two incantations can't point to the same word)
			return;
		}

		if (reverse) {
			incantationOrder = new String[]{
				WORDS[client.getVarbitValue(INCANTATION_WORD_5)],
				WORDS[client.getVarbitValue(INCANTATION_WORD_4)],
				WORDS[client.getVarbitValue(INCANTATION_WORD_3)],
				WORDS[client.getVarbitValue(INCANTATION_WORD_2)],
				WORDS[client.getVarbitValue(INCANTATION_WORD_1)],
			};
		} else {
			incantationOrder = new String[]{
				WORDS[client.getVarbitValue(INCANTATION_WORD_1)],
				WORDS[client.getVarbitValue(INCANTATION_WORD_2)],
				WORDS[client.getVarbitValue(INCANTATION_WORD_3)],
				WORDS[client.getVarbitValue(INCANTATION_WORD_4)],
				WORDS[client.getVarbitValue(INCANTATION_WORD_5)],
			};
		}
		String incantString = "Say the following in order: " + String.join(", ", incantationOrder);

		setText("Click the demonic sigil and read the incantation.");
		addText(incantString);
	}
}
