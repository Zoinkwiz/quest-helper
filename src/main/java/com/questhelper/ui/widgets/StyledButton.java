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

import net.runelite.api.gameval.SpriteID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.JavaScriptCallback;

/**
 * Reusable styled button with icon and text
 * Encapsulates button background, border, text, icon, and hover effects
 */
public class StyledButton
{
	// Button constants
	private static final int BUTTON_BACKGROUND_WIDTH = 95;
	private static final int BUTTON_BACKGROUND_HEIGHT = 71;
	private static final int BUTTON_PADDING = 4;
	private static final int BUTTON_WIDTH = 90;
	private static final int BUTTON_HEIGHT = 60;
	private static final int BUTTON_EDGE_SIZE = 9;
	private static final int ICON_SIZE = 24;

	private final Widget buttonContainer;
	private final Widget buttonBackground;

	public StyledButton(Widget parent, String text, int icon, int x, int y, JavaScriptCallback onClick)
	{
		// Create button container
		this.buttonContainer = WidgetFactory.createLayer(parent, x, y, BUTTON_BACKGROUND_WIDTH, BUTTON_BACKGROUND_HEIGHT);

		// Create button background with hover effects
		this.buttonBackground = WidgetFactory.createGraphic(buttonContainer, SpriteID.TRADEBACKING, 0, 0, BUTTON_WIDTH, BUTTON_HEIGHT);
		buttonBackground.setOnMouseOverListener((JavaScriptCallback) (ev) -> {
			buttonBackground.setSpriteId(SpriteID.TRADEBACKING_DARK);
		});
		buttonBackground.setOnMouseLeaveListener((JavaScriptCallback) (ev) -> {
			buttonBackground.setSpriteId(SpriteID.TRADEBACKING);
		});
		buttonBackground.setHasListener(true);
		buttonBackground.setAction(0, "Open");
		buttonBackground.setOnOpListener(onClick);
		buttonBackground.revalidate();

		// Create button border pieces
		createButtonBorder();

		// Create button text
		WidgetFactory.createCenteredText(buttonContainer, text, "ff9933", true, BUTTON_PADDING, BUTTON_PADDING + 1, BUTTON_WIDTH, 12);

		// Create button icon
		WidgetFactory.createGraphic(buttonContainer, icon, (BUTTON_WIDTH - ICON_SIZE) / 2, 25, ICON_SIZE, ICON_SIZE);
	}

	/**
	 * Get the button container widget
	 */
	public Widget getButtonContainer()
	{
		return buttonContainer;
	}

	/**
	 * Get the button background widget
	 */
	public Widget getButtonBackground()
	{
		return buttonBackground;
	}

	/**
	 * Create button border pieces
	 */
	private void createButtonBorder()
	{
		// Corner pieces
		WidgetFactory.createGraphic(buttonContainer, SpriteID.V2StoneButton._0, BUTTON_PADDING, 0, BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE); // Top-left
		WidgetFactory.createGraphic(buttonContainer, SpriteID.V2StoneButton._1, BUTTON_WIDTH - BUTTON_EDGE_SIZE, 0, BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE); // Top-right
		WidgetFactory.createGraphic(buttonContainer, SpriteID.V2StoneButton._2, BUTTON_PADDING, BUTTON_HEIGHT - BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE); // Bottom-left
		WidgetFactory.createGraphic(buttonContainer, SpriteID.V2StoneButton._3, BUTTON_WIDTH - BUTTON_EDGE_SIZE, BUTTON_HEIGHT - BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE); // Bottom-right

		// Edge pieces
		WidgetFactory.createGraphic(buttonContainer, SpriteID.V2StoneButton._4, BUTTON_PADDING, BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE, BUTTON_HEIGHT - (BUTTON_EDGE_SIZE * 2)); // Left
		WidgetFactory.createGraphic(buttonContainer, SpriteID.V2StoneButton._5, BUTTON_EDGE_SIZE + BUTTON_PADDING, 0, BUTTON_WIDTH - (BUTTON_EDGE_SIZE * 2), BUTTON_EDGE_SIZE); // Top
		WidgetFactory.createGraphic(buttonContainer, SpriteID.V2StoneButton._6, BUTTON_WIDTH - BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE, BUTTON_HEIGHT - (BUTTON_EDGE_SIZE * 2)); // Right
		WidgetFactory.createGraphic(buttonContainer, SpriteID.V2StoneButton._7, BUTTON_EDGE_SIZE + BUTTON_PADDING, BUTTON_HEIGHT - BUTTON_EDGE_SIZE, BUTTON_WIDTH - (BUTTON_EDGE_SIZE * 2), BUTTON_EDGE_SIZE); // Bottom
	}
}
