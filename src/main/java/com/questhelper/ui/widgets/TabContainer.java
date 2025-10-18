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
import java.util.function.Consumer;

/**
 * Manages tabbed interface with headers and content panels
 * Handles tab selection and visibility toggling
 */
public class TabContainer
{
	private final Widget[] tabContents;
	private final Widget[] tabHeaders;
	private final Widget contentContainer;
	private int selectedTabIndex = 0;
	private Consumer<Integer> onTabChangeCallback;

	public TabContainer(Widget parent, String[] tabTitles)
	{
		// Create tab headers
		tabHeaders = new Widget[tabTitles.length];
		int headerX = 12;
		for (int i = 0; i < tabTitles.length; i++)
		{
			final int idx = i;
			tabHeaders[i] = WidgetFactory.createTabHeader(
				parent,
				tabTitles[i],
				headerX,
				2,
				i == 0,
				(ev) -> selectTab(idx)
			);
			headerX += tabTitles[i].length() * 6 + 18; // rough width spacing
		}

		// Content container (fills under header, above scrollbar area)
		contentContainer = WidgetFactory.createContentContainer(parent, 8, 35, 16, 40);

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
		if (onTabChangeCallback != null) {
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
			if (tabHeaders != null && tabHeaders[i] != null)
			{
				tabHeaders[i].setTextColor(Integer.parseInt(visible ? "ff981f" : "c8aa6e", 16));
			}
		}
	}
}
