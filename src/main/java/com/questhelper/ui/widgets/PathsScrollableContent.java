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

/**
 * ScrollableContent implementation for the Paths tab
 * Manages a list of category items that can be scrolled vertically
 */
public class PathsScrollableContent implements ScrollableContent
{
	private final Widget contentLayer;
	private int itemCount = 0;
	private int totalHeight = 0;
	
	// Constants for item dimensions
	private static final int ITEM_WIDTH = 400; // Full width of container
	private static final int ITEM_HEIGHT = 20; // Height per item (category header or unlock row)
	private static final int CATEGORY_SPACING = 12; // Space between categories
	private static final int UNLOCK_ROW_HEIGHT = 90; // Height for unlock button rows
	
	public PathsScrollableContent(Widget contentLayer)
	{
		this.contentLayer = contentLayer;
	}
	
	/**
	 * Add a category header item
	 */
	public void addCategoryHeader()
	{
		itemCount++;
		totalHeight += ITEM_HEIGHT;
	}
	
	/**
	 * Add an unlock row (horizontal scrollable container with buttons)
	 */
	public void addUnlockRow()
	{
		itemCount++;
		totalHeight += UNLOCK_ROW_HEIGHT;
	}
	
	/**
	 * Add spacing between categories
	 */
	public void addCategorySpacing()
	{
		totalHeight += CATEGORY_SPACING;
	}
	
	/**
	 * Reset the content for rebuilding
	 */
	public void reset()
	{
		itemCount = 0;
		totalHeight = 0;
	}
	
	@Override
	public int getItemWidth()
	{
		return ITEM_WIDTH;
	}
	
	@Override
	public int getItemHeight()
	{
		return ITEM_HEIGHT;
	}
	
	@Override
	public int getRows()
	{
		// For vertical scrolling, we want 1 column (items stacked vertically)
		return itemCount;
	}
	
	@Override
	public int getColumns()
	{
		// For vertical scrolling, we want 1 column
		return 1;
	}
	
	@Override
	public int getItemCount()
	{
		return itemCount;
	}
	
	@Override
	public Widget getContentLayer()
	{
		return contentLayer;
	}
	
	/**
	 * Get the total height of all content
	 */
	public int getTotalHeight()
	{
		return totalHeight;
	}
}
