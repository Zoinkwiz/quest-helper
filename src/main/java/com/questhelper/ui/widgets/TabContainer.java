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

import net.runelite.api.widgets.Widget;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.widgets.WidgetSizeMode;
import net.runelite.api.widgets.WidgetType;
import java.util.function.Consumer;

/**
 * Manages tabbed interface with sprite-based headers and content panels
 * Handles tab selection and visibility toggling with hover states
 */
public class TabContainer
{
	private final Widget[] tabContents;
	private final UIButton[] tabButtons;
	private final Widget contentContainer;
	private final Widget divider;
	private int selectedTabIndex = 0;
	private Consumer<Integer> onTabChangeCallback;

	// Tab dimensions
	private static final int TAB_WIDTH = 90;
	private static final int TAB_HEIGHT = 21;
	private static final int TAB_SPACING = 2;

	// Sprite IDs for tabs (using existing RuneLite sprites)
	private static final int TAB_STANDARD_SPRITE = SpriteID.TRADEBACKING;
	private static final int TAB_HOVER_SPRITE = SpriteID.CloseButtons._1;

	public TabContainer(Widget parent, String[] tabTitles)
	{
		// Create tab buttons
		tabButtons = new UIButton[tabTitles.length];
		int tabX = TAB_SPACING;
		for (int i = 0; i < tabTitles.length; i++)
		{
			// 2284 middle, Tiling on
			// 2283 Right, Left
			final int idx = i;
			tabButtons[i] = WidgetFactory.createTabButton(
				parent,
				tabTitles[i],
				tabX,
				2,
				TAB_WIDTH,
				TAB_HEIGHT,
				"View " + tabTitles[i],
				(ev) -> selectTab(idx)
			);
			tabX += TAB_WIDTH + TAB_SPACING;
		}

		// Create horizontal divider line below tabs
		divider = WidgetFactory.createDivider(parent, 0, TAB_HEIGHT + 4, parent.getWidth());

		// Content container (fills under header, above scrollbar area)
		contentContainer = WidgetFactory.createContentContainer(parent, 0, TAB_HEIGHT, 0, 20);
		var contentRectangleWidget = contentContainer.createChild(-1, WidgetType.RECTANGLE);
		contentRectangleWidget.setName("QH Content Rectangle");
		contentRectangleWidget.setTextColor(Integer.parseInt("585040", 16));
		contentRectangleWidget.setWidthMode(WidgetSizeMode.MINUS);
		contentRectangleWidget.setHeightMode(WidgetSizeMode.MINUS);
		contentRectangleWidget.revalidate();

		// Create per-tab content layers
		tabContents = new Widget[tabTitles.length];
		for (int i = 0; i < tabTitles.length; i++)
		{
			tabContents[i] = WidgetFactory.createScrollableContent(contentContainer, 6, 0);
		}

		// Initial tab selection
		applyTabVisibility(0);
	}

	/**
	 * Get the content widget for a specific tab
	 * @param index Tab index
	 * @return Widget for the tab content
	 */
	public Widget getTabContent(int index)
	{
		if (index < 0 || index >= tabContents.length) {
			return null;
		}
		return tabContents[index];
	}

	/**
	 * Get the content container widget
	 */
	public Widget getContentContainer()
	{
		return contentContainer;
	}

	/**
	 * Get the currently selected tab index
	 */
	public int getSelectedTabIndex()
	{
		return selectedTabIndex;
	}

	/**
	 * Set a callback for when tabs change
	 */
	public void setOnTabChange(Consumer<Integer> callback)
	{
		this.onTabChangeCallback = callback;
	}

	/**
	 * Select a tab by index
	 */
	public void selectTab(int index)
	{
		if (index < 0 || index >= tabContents.length) return;
		if (selectedTabIndex == index) return;
		selectedTabIndex = index;
		applyTabVisibility(index);

		// Call the callback if set
		if (onTabChangeCallback != null)
		{
			onTabChangeCallback.accept(index);
		}
	}

	/**
	 * Apply tab visibility based on selection
	 */
	private void applyTabVisibility(int index)
	{
		for (int i = 0; i < tabContents.length; i++)
		{
			boolean visible = (i == index);
			if (tabContents[i] != null)
			{
				tabContents[i].setHidden(!visible);
			}
			if (tabButtons != null && tabButtons[i] != null)
			{
				// Active tab shows hover sprite permanently, inactive tabs show standard sprite
				if (visible)
				{
					tabButtons[i].setSelected(true);
				}
				else
				{
					tabButtons[i].setSelected(false);
				}
			}
		}
	}
}
