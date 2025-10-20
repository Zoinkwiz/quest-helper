/*
 * Copyright (c) 2025, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.ui.widgets;

import net.runelite.api.ScriptEvent;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetSizeMode;
import net.runelite.api.widgets.WidgetTextAlignment;
import net.runelite.api.widgets.WidgetType;

import java.util.ArrayList;
import java.util.List;

/**
 * UI Button component that wraps a Widget with button functionality
 * Supports standard and hover sprite states that switch on mouse enter/leave
 */
public class UIButton
{
	private final Widget widget;
	private final Widget leftWidget;
	private final Widget middleWidget;
	private final Widget rightWidget;
	private final Widget textWidget;
	private final List<JavaScriptCallback> actions = new ArrayList<>();

	private boolean isSelected;

	/**
	 * Constructs a new button component
	 * @param parent the parent widget
	 */
	public UIButton(Widget parent, String text)
	{
		this.widget = parent.createChild(-1, WidgetType.LAYER);
		this.widget.setHasListener(true);
		this.widget.setOnMouseOverListener((JavaScriptCallback) this::onMouseHover);
		this.widget.setOnMouseLeaveListener((JavaScriptCallback) this::onMouseLeave);
		this.widget.setOnOpListener((JavaScriptCallback) this::onActionSelected);
		this.widget.revalidate();

		leftWidget = this.widget.createChild(-1, WidgetType.GRAPHIC);
		leftWidget.setSpriteId(SpriteID.TabsTall._0);
		leftWidget.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		leftWidget.setSize(20, 20);
		leftWidget.revalidate();

		middleWidget = this.widget.createChild(-1, WidgetType.GRAPHIC);
		middleWidget.setSpriteId(SpriteID.TabsTall._1);
		middleWidget.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		middleWidget.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		middleWidget.setWidthMode(WidgetSizeMode.MINUS);
		middleWidget.setSize(40, 20);
		middleWidget.revalidate();

		rightWidget = this.widget.createChild(-1, WidgetType.GRAPHIC);
		rightWidget.setSpriteId(SpriteID.TabsTall._0);
		rightWidget.setFlippedHorizontally(true);
		rightWidget.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
		rightWidget.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		rightWidget.setSize(24, 20);
		rightWidget.revalidate();

		// TODO: Calculate width based on text
		textWidget = this.widget.createChild(-1, WidgetType.TEXT);
		textWidget.setText(text);
		textWidget.setTextColor(Integer.parseInt("ffa82f", 16));
		textWidget.setFontId(495);
		textWidget.setTextShadowed(true);
		textWidget.setXTextAlignment(WidgetTextAlignment.CENTER);
		textWidget.setYTextAlignment(WidgetTextAlignment.CENTER);
		textWidget.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		textWidget.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		textWidget.setWidthMode(WidgetSizeMode.MINUS);
		textWidget.setHeightMode(WidgetSizeMode.MINUS);
		textWidget.revalidate();
	}

	/**
	 * Sets the size of the button
	 * @param width the width
	 * @param height the height
	 */
	public void setSize(int width, int height)
	{
		widget.setOriginalWidth(width);
		widget.setOriginalHeight(height);
		widget.revalidate();

		leftWidget.revalidate();
		middleWidget.revalidate();
		rightWidget.revalidate();
		textWidget.revalidate();
	}

	/**
	 * Sets the position of the button
	 * @param x the x position
	 * @param y the y position
	 */
	public void setPosition(int x, int y)
	{
		this.widget.setOriginalX(x);
		this.widget.setOriginalY(y);
		this.widget.revalidate();

		leftWidget.revalidate();
		middleWidget.revalidate();
		rightWidget.revalidate();
		textWidget.revalidate();
	}

	/**
	 * Sets the visibility of the button
	 * @param visible true for visible, false for hidden
	 */
	public void setVisibility(boolean visible)
	{
		this.widget.setHidden(!visible);
	}

	/**
	 * Adds an action to the button
	 * @param action the action name
	 * @param callback the callback to execute
	 */
	public void addAction(String action, JavaScriptCallback callback)
	{
		this.widget.setAction(actions.size(), action);
		this.actions.add(callback);
	}

	/**
	 * Clears all actions from the button
	 */
	public void clearActions()
	{
		this.actions.clear();
		this.widget.clearActions();
	}

	/**
	 * Gets the underlying widget
	 * @return the widget
	 */
	public Widget getWidget()
	{
		return this.widget;
	}

	/**
	 * Revalidates the widget
	 */
	public void revalidate()
	{
		this.widget.revalidate();
	}

	/**
	 * Triggered when mouse hovers over the button
	 */
	private void onMouseHover(ScriptEvent e)
	{
		if (isSelected)
		{
			return;
		}

		setIsHovered();
	}

	public void setSelected(boolean isSelected)
	{
		if (isSelected)
		{
			setIsHovered();
		}
		else
		{
			setIsNotHovered();
		}

		this.isSelected = isSelected;
	}

	private void setIsHovered()
	{
		leftWidget.setSpriteId(SpriteID.TabsTall._2);
		leftWidget.revalidate();
		middleWidget.setSpriteId(SpriteID.TabsTall._3);
		middleWidget.revalidate();
		rightWidget.setSpriteId(SpriteID.TabsTall._2);
		rightWidget.revalidate();
	}

	private void setIsNotHovered()
	{
		leftWidget.setSpriteId(SpriteID.TabsTall._0);
		leftWidget.revalidate();
		middleWidget.setSpriteId(SpriteID.TabsTall._1);
		middleWidget.revalidate();
		rightWidget.setSpriteId(SpriteID.TabsTall._0);
		rightWidget.revalidate();
	}

	/**
	 * Triggered when mouse leaves the button
	 */
	private void onMouseLeave(ScriptEvent e)
	{
		if (isSelected)
		{
			return;
		}

		setIsNotHovered();
	}

	/**
	 * Triggered when an action is selected
	 */
	private void onActionSelected(ScriptEvent e)
	{
		if (this.actions.isEmpty())
			return;

		int actionIndex = e.getOp() - 1;
		if (actionIndex >= 0 && actionIndex < this.actions.size())
		{
			this.actions.get(actionIndex).run(e);
		}
	}
}
