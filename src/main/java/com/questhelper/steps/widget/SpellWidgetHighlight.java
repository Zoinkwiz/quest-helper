/*
 * Copyright (c) 2024, pajlada <https://github.com/pajlada>
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
package com.questhelper.steps.widget;

import com.questhelper.QuestHelperPlugin;
import net.runelite.api.Client;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.client.util.Text;

import java.awt.*;

/**
 * Highlight a spell in the spellbook with a given spell name
 * <p>
 * Use this over a raw group id + child id highlight combination for spells where possible since
 * spells being moved from Jagex may change the child id
 */
public class SpellWidgetHighlight extends AbstractWidgetHighlight
{
	/**
	 * The InterfaceID.SPELLBOOK child that contains the list of spell widgets
	 */
	private static final int SPELLBOOK_SPELL_LIST_CHILD_ID = 3;

	/**
	 * The name of the spell to search for without tags (e.g. "Ardougne Teleport")
	 */
	private final String spellName;

	/**
	 * Internal state for whether to perform a full search next time the spellbook is visible
	 */
	private boolean redoSearch = true;

	/**
	 * The final full component ID of the spell widget
	 */
	private Integer spellComponentId = null;

	public SpellWidgetHighlight(Spell spell)
	{
		this.spellName = spell.getSpellName();
	}

	@Override
	public void highlightChoices(Graphics2D graphics, Client client, QuestHelperPlugin questHelper)
	{
		var spellbookWidget = client.getWidget(InterfaceID.SPELLBOOK, SpellWidgetHighlight.SPELLBOOK_SPELL_LIST_CHILD_ID);
		if (spellbookWidget == null || spellbookWidget.isHidden())
		{
			redoSearch = true;
			return;
		}

		if (redoSearch)
		{
			spellComponentId = null;
			var staticChildren = spellbookWidget.getStaticChildren();
			if (staticChildren != null)
			{
				for (var widget : staticChildren)
				{
					if (Text.removeTags(widget.getName()).equals(spellName))
					{
						spellComponentId = widget.getId();
						break;
					}
				}

				redoSearch = false;
			}
		}

		if (spellComponentId != null)
		{
			var spellWidget = client.getWidget(spellComponentId);
			if (spellWidget == null || spellWidget.isHidden())
			{
				// Widget is not visible
				return;
			}

			// NOTE: This is overly cautious, we confirm that the widget we're about to highlight matches
			// the spell name we're searching for. So far, this has never fired
			if (!Text.removeTags(spellWidget.getName()).equals(spellName))
			{
				spellComponentId = null;
				redoSearch = true;
				return;
			}

			highlightWidget(graphics, questHelper, spellWidget);
		}
	}
}
