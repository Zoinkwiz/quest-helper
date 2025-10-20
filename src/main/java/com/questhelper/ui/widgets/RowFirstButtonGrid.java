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
import net.runelite.api.widgets.WidgetType;

/**
 * Grid layout for StyledButtons that fills rows first (left to right, then new row)
 * This is the opposite behavior of ButtonGrid which fills columns first
 * Non-scrollable version - just a simple grid container
 */
public class RowFirstButtonGrid
{
	// Button constants
	private static final int BUTTON_BACKGROUND_WIDTH = 95;
	private static final int BUTTON_BACKGROUND_HEIGHT = 71;

	private final Widget container;
	private final int columns;

	public RowFirstButtonGrid(Widget parent, int x, int y, int columns, Client client)
	{
		this.columns = columns;
		// Create a simple layer container instead of scrollable container
		this.container = parent.createChild(-1, WidgetType.LAYER);
		this.container.setPos(x, y);
		// Start with height for 1 row, will grow as needed
		int initialHeight = BUTTON_BACKGROUND_HEIGHT;
		this.container.setSize(columns * BUTTON_BACKGROUND_WIDTH, initialHeight);
		this.container.revalidate();
	}

	public RowFirstButtonGrid(Widget parent, int x, int y, int columns, int negativeHeight, Client client)
	{
		this.columns = columns;
		// Create a simple layer container instead of scrollable container
		this.container = parent.createChild(-1, WidgetType.LAYER);
		this.container.setPos(x, y);
		// Start with height for 1 row, will grow as needed
		int initialHeight = BUTTON_BACKGROUND_HEIGHT;
		this.container.setSize(columns * BUTTON_BACKGROUND_WIDTH, initialHeight);
		this.container.revalidate();
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
		int existingButtons = (container.getDynamicChildren() == null) ? 0 : container.getDynamicChildren().length;
		
		// Row-first logic: fill columns first, then move to next row
		int columnIndex = existingButtons % columns;
		int rowIndex = existingButtons / columns;
		
		// Position: column determines X, row determines Y
		int xShift = columnIndex * BUTTON_BACKGROUND_WIDTH;
		int yShift = rowIndex * BUTTON_BACKGROUND_HEIGHT;

		// Create the styled button
		StyledButton button = new StyledButton(container, text, icon, xShift, yShift, onClick);

		// Update container height if we've added a new row
		int newTotalButtons = existingButtons + 1;
		int newRowCount = (newTotalButtons + columns - 1) / columns; // Round up division
		int requiredHeight = newRowCount * BUTTON_BACKGROUND_HEIGHT;
		
		// Update the container height if needed
		if (container.getHeight() < requiredHeight)
		{
			container.setSize(container.getWidth(), requiredHeight);
			container.revalidate();
		}

		return button;
	}

	/**
	 * Get the container widget
	 */
	public Widget getContainer()
	{
		return container;
	}

	/**
	 * Get the current height of the grid container
	 * @return The height in pixels
	 */
	public int getHeight()
	{
		return container.getHeight();
	}
}
