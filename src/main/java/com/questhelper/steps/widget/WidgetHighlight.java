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
package com.questhelper.steps.widget;

import com.questhelper.QuestHelperPlugin;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;

import javax.annotation.Nullable;
import java.awt.*;

public class WidgetHighlight extends AbstractWidgetHighlight
{
	@Getter
	protected final int interfaceID;

	protected final int childChildId;

	@Getter
	protected Integer itemIdRequirement;

	@Getter

	@Setter
	protected Integer modelIdRequirement;

	@Getter
	protected String requiredText;

	@Nullable
	private String nameToCheckFor = null;


	protected final boolean checkChildren;

	public WidgetHighlight(int interfaceID)
	{
		this.interfaceID = interfaceID;
		this.childChildId = -1;
		this.checkChildren = false;
	}

	public WidgetHighlight(int interfaceID, boolean checkChildren)
	{
		this.interfaceID = interfaceID;
		this.childChildId = -1;
		this.checkChildren = checkChildren;
	}


	public WidgetHighlight(int groupId, int childId)
	{
		this.interfaceID = groupId << 16 | childId;
		this.childChildId = -1;
		this.checkChildren = false;
	}

	public WidgetHighlight(int groupId, int childId, int childChildId)
	{
		this.interfaceID = groupId << 16 | childId;
		this.childChildId = childChildId;
		this.checkChildren = false;
	}

	public WidgetHighlight(int groupId, int childId, boolean checkChildren)
	{
		this.interfaceID = groupId << 16 | childId;
		this.childChildId = -1;
		this.checkChildren = checkChildren;
	}

	public WidgetHighlight(int groupId, int childId, int itemIdRequirement, boolean checkChildren)
	{
		this.interfaceID = groupId << 16 | childId;
		this.childChildId = -1;
		this.itemIdRequirement = itemIdRequirement;
		this.checkChildren = checkChildren;
	}

	public WidgetHighlight(int groupId, int childId, String requiredText, boolean checkChildren)
	{
		this.interfaceID = groupId << 16 | childId;
		this.childChildId = -1;
		this.requiredText = requiredText;
		this.checkChildren = checkChildren;
	}

	public static WidgetHighlight createMultiskillByName(String roughName)
	{
		var w = new WidgetHighlight(InterfaceID.Skillmulti.BOTTOM, true);
		w.nameToCheckFor = roughName;
		return w;
	}

	@Override
	public void highlightChoices(Graphics2D graphics, Client client, QuestHelperPlugin questHelper)
	{
		Widget widgetToHighlight = client.getWidget(interfaceID);
		if (widgetToHighlight == null) return;
		if (widgetToHighlight.isHidden()) return;

		highlightChoices(widgetToHighlight, graphics, questHelper);
	}

	private void highlightChoices(Widget parentWidget, Graphics2D graphics, QuestHelperPlugin questHelper)
	{
		if (parentWidget == null) return;

		Widget[] widgets = parentWidget.getChildren();
		Widget[] staticWidgets = parentWidget.getStaticChildren();

		if (childChildId != -1 && widgets != null)
		{
			highlightChoices(widgets[childChildId], graphics, questHelper);
			return;
		}

		if (checkChildren && widgets != null)
		{
			for (Widget widget : widgets)
			{
				highlightChoices(widget, graphics, questHelper);
			}
			for (Widget widget : staticWidgets)
			{
				highlightChoices(widget, graphics, questHelper);
			}
		}

		highlightWidget(graphics, questHelper, parentWidget);
	}

	@Override
	protected void highlightWidget(Graphics2D graphics, QuestHelperPlugin questHelper, Widget widgetToHighlight)
	{
		if (widgetToHighlight == null) return;
		if (!itemCheckPasses(widgetToHighlight)) return;
		if (!modelCheckPasses(widgetToHighlight)) return;
		if (!requiredTextCheckPasses(widgetToHighlight)) return;
		if (!roughNameCheckPasses(widgetToHighlight)) return;

		super.highlightWidget(graphics, questHelper, widgetToHighlight);
	}


	private boolean itemCheckPasses(Widget widgetToHighlight)
	{
		return (itemIdRequirement == null || widgetToHighlight.getItemId() == itemIdRequirement);
	}

	private boolean modelCheckPasses(Widget widget)
	{
		return (modelIdRequirement == null || widget.getModelId() == modelIdRequirement);
	}

	private boolean requiredTextCheckPasses(Widget widget)
	{
		if (requiredText == null) return true;
		if (widget.getText() == null) return false;
		return widget.getText().contains(requiredText);
	}

	private boolean roughNameCheckPasses(Widget widget)
	{
		if (nameToCheckFor == null) return true;
		var widgetName = widget.getName();
		if (widgetName == null || widgetName.isEmpty()) return false;
		return widgetName.contains(nameToCheckFor);
	}
}
