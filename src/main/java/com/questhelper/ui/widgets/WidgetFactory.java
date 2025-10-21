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
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetSizeMode;
import net.runelite.api.widgets.WidgetTextAlignment;
import net.runelite.api.widgets.WidgetType;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.FontID;

/**
 * Factory for creating primitive widgets (non-composite)
 * Provides low-level widget creation methods for basic UI elements
 */
public class WidgetFactory
{
	/**
	 * Create a text widget with common styling
	 */
	public static Widget createText(Widget parent, String text, String color, boolean shadow, int x, int y, int width, int height)
	{
		Widget widget = parent.createChild(-1, WidgetType.TEXT);
		widget.setText(text);
		widget.setFontId(FontID.PLAIN_12);
		widget.setTextColor(Integer.parseInt(color, 16));
		widget.setTextShadowed(shadow);
		widget.setPos(x, y);
		widget.setSize(width, height);
		widget.revalidate();
		return widget;
	}
	
	/**
	 * Create a centered text widget
	 */
	public static Widget createCenteredText(Widget parent, String text, String color, boolean shadow, int x, int y, int width, int height)
	{
		Widget widget = createText(parent, text, color, shadow, x, y, width, height);
		widget.setXTextAlignment(WidgetTextAlignment.CENTER);
		widget.setYTextAlignment(WidgetTextAlignment.CENTER);
		widget.revalidate();
		return widget;
	}
	
	/**
	 * Create a graphic widget (sprite)
	 */
	public static Widget createGraphic(Widget parent, int spriteId, int x, int y, int width, int height)
	{
		Widget widget = parent.createChild(-1, WidgetType.GRAPHIC);
		widget.setSpriteId(spriteId);
		widget.setPos(x, y);
		widget.setSize(width, height);
		widget.revalidate();
		return widget;
	}
	
	/**
	 * Create a graphic widget with original dimensions (for absolute positioning)
	 */
	public static Widget createGraphicAbsolute(Widget parent, int spriteId, int originalX, int originalY, int originalWidth, int originalHeight)
	{
		Widget widget = parent.createChild(-1, WidgetType.GRAPHIC);
		widget.setSpriteId(spriteId);
		widget.setOriginalX(originalX);
		widget.setOriginalY(originalY);
		widget.setOriginalWidth(originalWidth);
		widget.setOriginalHeight(originalHeight);
		widget.revalidate();
		return widget;
	}

	/**
	 * Create a layer widget (container)
	 */
	public static Widget createLayer(Widget parent, int x, int y, int width, int height)
	{
		Widget widget = parent.createChild(-1, WidgetType.LAYER);
		widget.setPos(x, y);
		widget.setSize(width, height);
		widget.revalidate();
		return widget;
	}
	
	/**
	 * Create a layer widget with size modes
	 */
	public static Widget createFloatLayer(Widget parent)
	{
		Widget widget = parent.createChild(-1, WidgetType.LAYER);
		widget.setName("QH Float Layer");
		widget.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		widget.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		widget.setWidthMode(WidgetSizeMode.MINUS);
		widget.setHeightMode(WidgetSizeMode.MINUS);
		widget.setHasListener(true);
		widget.revalidate();
		return widget;
	}
	
	/**
	 * Create a clickable button with text
	 */
	public static Widget createTextButton(Widget parent, String text, int x, int y, int width, int height, JavaScriptCallback onClick)
	{
		// Button background
		Widget button = createGraphic(parent, SpriteID.TRADEBACKING, x, y, width, height);
		button.setName("QH Text Button");
		button.setHasListener(true);
		button.setOnClickListener(onClick);
		
		// Button text
		createCenteredText(parent, text, "c8aa6e", false, x + 2, y + 1, width - 4, height - 2);
		
		return button;
	}

	/**
	 * Create a clickable button with text
	 */
	public static Widget createLeftAlignedTextButton(Widget parent, String text, int x, int y, int width, int height, JavaScriptCallback onClick)
	{
		// Button background
		Widget button = createGraphic(parent, SpriteID.TRADEBACKING, x, y, width, height);
		button.setName("QH Text Button");
		button.setHasListener(true);
		button.setOnClickListener(onClick);

		// Button text
		createText(parent, text, "c8aa6e", false, x + 2, y + 1, width - 4, height - 2);

		return button;
	}

	public static Widget createParagraphSection(Widget parent, String text, int x, int y, int width, int height)
	{
		var widget = parent.createChild(-1, WidgetType.TEXT);
		widget.setText(text);
		widget.setTextColor(Integer.parseInt("ff981f", 16));
		widget.setPos(x, y);
		widget.setSize(width, height);
		widget.setFontId(495);
		widget.revalidate();

		return widget;
	}

	/**
	 * Create a left-aligned text button with color and hover-to-white behavior
	 */
	public static Widget createLeftAlignedTextButtonColored(
		Widget parent,
		String text,
		String baseColor,
		int x,
		int y,
		int width,
		int height,
		JavaScriptCallback onClick
	)
	{
		// Button background
		Widget button = createGraphic(parent, SpriteID.TRADEBACKING, x, y, width, height);
		button.setName("QH Text Button Colored");
		button.setHasListener(true);
		button.setOnClickListener(onClick);

		// Button text
		Widget textWidget = createText(parent, text, baseColor, false, x + 2, y + 1, width - 4, height - 2);

		// Hover effects: to white on hover, restore base color on leave
		button.setOnMouseOverListener((JavaScriptCallback) (ev) -> {
			textWidget.setTextColor(Integer.parseInt("ffffff", 16));
		});
		button.setOnMouseLeaveListener((JavaScriptCallback) (ev) -> {
			textWidget.setTextColor(Integer.parseInt(baseColor, 16));
		});

		return button;
	}

	/**
	 * Create a title widget
	 */
	public static Widget createTitle(Widget parent, String title)
	{
		Widget widget = createText(parent, title, "ff981f", true, 0, 6, 12, 24);
		widget.setName("QH Title");
		widget.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		widget.setWidthMode(WidgetSizeMode.MINUS);
		widget.setFontId(496); // Title font
		widget.setXTextAlignment(WidgetTextAlignment.CENTER);
		widget.setYTextAlignment(WidgetTextAlignment.CENTER);
		widget.revalidate();
		return widget;
	}
	
	/**
	 * Create a close button
	 */
	public static Widget createCloseButton(Widget parent, JavaScriptCallback onClose)
	{
		Widget closeButton = createGraphicAbsolute(parent, SpriteID.CloseButtons._0, 3, 6, 26, 23);
		closeButton.setName("QH Close Button");
		closeButton.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
		closeButton.setHasListener(true);
		closeButton.setAction(0, "Close");
		closeButton.setOnOpListener(onClose);
		closeButton.setNoClickThrough(true);
		closeButton.revalidate();
		
		// Add hover effects
		closeButton.setOnMouseOverListener((JavaScriptCallback) (ev) -> closeButton.setSpriteId(SpriteID.CloseButtons._1));
		closeButton.setOnMouseLeaveListener((JavaScriptCallback) (ev) -> closeButton.setSpriteId(SpriteID.CloseButtons._0));
		
		return closeButton;
	}
	
	/**
	 * Create a tab header (deprecated - use createTabButton instead)
	 */
	@Deprecated
	public static Widget createTabHeader(Widget parent, String text, int x, int y, boolean selected, JavaScriptCallback onClick)
	{
		Widget widget = createText(parent, text, selected ? "ff981f" : "c8aa6e", true, x, y, 12, 16);
		widget.setName("QH Tab Header");
		widget.setHasListener(true);
		widget.setOnClickListener(onClick);
		widget.revalidate();
		return widget;
	}

	/**
	 * Create a horizontal divider line
	 * @param parent the parent widget
	 * @param x the x position
	 * @param y the y position
	 * @param width the width of the divider
	 * @return the divider widget
	 */
	public static Widget createDivider(Widget parent, int x, int y, int width)
	{
		Widget widget = createGraphicAbsolute(parent, SpriteID.TRADEBACKING, x, y, width, 1);
		widget.setName("QH Divider");
		widget.setOpacity(100);
		widget.revalidate();
		return widget;
	}

	/**
	 * Create a tab button using UIButton
	 * @param parent the parent widget
	 * @param x the x position
	 * @param y the y position
	 * @param width the width
	 * @param height the height
	 * @param action the action name
	 * @param callback the click callback
	 * @return the UIButton instance
	 */
	public static UIButton createTabButton(Widget parent, String text, int x, int y, int width, int height, String action, JavaScriptCallback callback)
	{
		UIButton button = new UIButton(parent, text);
		button.setSize(width, height);
		button.setPosition(x, y);
		button.addAction(action, callback);
		button.revalidate();
		return button;
	}
	
	/**
	 * Create a content container
	 */
	public static Widget createContentContainer(Widget parent, int x, int y, int width, int height)
	{
		Widget widget = createLayer(parent, x, y, width, height);
		widget.setName("QH Content Container");
		widget.setWidthMode(WidgetSizeMode.MINUS);
		widget.setHeightMode(WidgetSizeMode.MINUS);
		widget.revalidate();
		return widget;
	}
	
	/**
	 * Create a scrollable content area
	 */
	public static Widget createScrollableContent(Widget parent, int width, int height)
	{
		Widget widget = createLayer(parent, 0, 0, width, height);
		widget.setName("QH Scrollable Content");
		widget.setWidthMode(WidgetSizeMode.MINUS);
		widget.setHeightMode(WidgetSizeMode.MINUS);
		widget.revalidate();
		return widget;
	}

	/**
	 * Create a button layer for horizontal scrolling buttons
	 */
	public static Widget createButtonLayer(Widget parent, int x, int y, int width, int height)
	{
		Widget buttonLayer = createLayer(parent, x, y, width, height);
		buttonLayer.setName("QH Button Layer");
		buttonLayer.setWidthMode(WidgetSizeMode.MINUS);
		buttonLayer.setHeightMode(WidgetSizeMode.MINUS);
		buttonLayer.setHasListener(true);
		buttonLayer.revalidate();
		return buttonLayer;
	}
}
