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

import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.JavaScriptCallback;

/**
 * Grid layout for StyledButtons with automatic scrollbar
 * Replaces ButtonSection with clearer name and better encapsulation
 */
public class ButtonGrid
{
	// Button constants
	private static final int BUTTON_BACKGROUND_WIDTH = 95;
	private static final int BUTTON_BACKGROUND_HEIGHT = 71;

	private final ScrollableContainer scrollableContainer;
	private final int rows;

	public ButtonGrid(Widget parent, int x, int y, int rows, Client client)
	{
		this.rows = rows;
		this.scrollableContainer = new ScrollableContainer(parent, x, y, 0, 0, rows, client);
	}

	public ButtonGrid(Widget parent, int x, int y, int negativeWidth, int negativeHeight, int rows, Client client)
	{
		this.rows = rows;
		this.scrollableContainer = new ScrollableContainer(parent, x, y, negativeWidth, negativeHeight, rows, client);
	}

	/**
	 * Add a button to the grid
	 * @param text Button text
	 * @param icon Button icon sprite ID
	 * @param onClick Click callback
	 * @return The created StyledButton
	 */
	public StyledButton addButton(String text, int icon, JavaScriptCallback onClick)
	{
		Widget contentLayer = scrollableContainer.getContentLayer();
		int existingButtons = (contentLayer.getDynamicChildren() == null) ? 0 : contentLayer.getDynamicChildren().length;
		
		// Simple logic: fill rows first, then move to next column
		int rowIndex = existingButtons % rows;
		int columnIndex = existingButtons / rows;
		
		// Position: column determines X, row determines Y
		int xShift = columnIndex * BUTTON_BACKGROUND_WIDTH;
		int yShift = rowIndex * BUTTON_BACKGROUND_HEIGHT;

		// Create the styled button
		StyledButton button = new StyledButton(contentLayer, text, icon, xShift, yShift, onClick);

		// Update scrollbar visibility after adding button
		scrollableContainer.updateScrollbar();

		return button;
	}

	/**
	 * Get the container widget
	 */
	public Widget getContainer()
	{
		return scrollableContainer.getContainer();
	}

	/**
	 * Get the content layer where buttons are added
	 */
	public Widget getContentLayer()
	{
		return scrollableContainer.getContentLayer();
	}

	/**
	 * Get the number of rows in the grid
	 */
	public int getRows()
	{
		return rows;
	}

	/**
	 * Update the scrollbar based on current content
	 */
	public void updateScrollbar()
	{
		scrollableContainer.updateScrollbar();
	}
}
