/*
 * Copyright (c) 2024, Zoinkwiz
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
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetSizeMode;
import net.runelite.api.widgets.WidgetTextAlignment;
import net.runelite.api.widgets.WidgetType;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.FontID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple widget builder that provides common helper methods for widget creation
 * without complex inheritance or method chaining that can cause API issues
 */
public class SimpleWidgetBuilder
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

	public static Widget createDraggedOutline(Widget parent, int x, int y, int width, int height)
	{
		Widget borderLayer = parent.createChild(-1, WidgetType.LAYER);
		borderLayer.setPos(x, y);
		borderLayer.setSize(width, height);
		borderLayer.revalidate();

		addLayerToBorder(borderLayer, 0);
		addLayerToBorder(borderLayer, 1);
		addLayerToBorder(borderLayer, 2);
		addLayerToBorder(borderLayer, 3);

		return borderLayer;
	}

	private static void addLayerToBorder(Widget borderLayer, int depth)
	{
		final String BORDER_COLOUR = "9f9f9f";

		var border = borderLayer.createChild(-1, WidgetType.RECTANGLE);
		border.setTextColor(Integer.parseInt(BORDER_COLOUR, 16));
		border.setOpacity(100 + (10 * depth));
		border.setWidthMode(WidgetSizeMode.MINUS);
		border.setHeightMode(WidgetSizeMode.MINUS);
		border.setSize(depth * 2, depth * 2);
		border.setPos(depth, depth);
		border.revalidate();
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
		widget.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		widget.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		widget.setWidthMode(WidgetSizeMode.MINUS);
		widget.setHeightMode(WidgetSizeMode.MINUS);
		widget.setHasListener(true);
		// Modal mode 1??
		// client.openInterface a consideration
//		widget.modal
		// Note: Size modes are set separately in the calling code to avoid API compatibility issues
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
		button.setHasListener(true);
		button.setOnClickListener(onClick);
		
		// Button text
		createCenteredText(parent, text, "c8aa6e", false, x + 2, y + 1, width - 4, height - 2);
		
		return button;
	}
	
	/**
	 * Create a modal background with borders
	 */
	public static Widget createModalBackground(Widget parent, int width, int height)
	{
		// Main background
		Widget background = createGraphicAbsolute(parent, SpriteID.TRADEBACKING, 0, 0, 0, 0);
		background.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		background.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		background.setWidthMode(WidgetSizeMode.MINUS);
		background.setHeightMode(WidgetSizeMode.MINUS);
		background.setHasListener(true);
		background.setOnClickListener((JavaScriptCallback) (ev) -> {}); // Prevent clicks from passing through
		background.revalidate();
		
		// Corner borders
		createCornerBorders(parent, width, height);
		
		// Side borders
		createSideBorders(parent, width, height);
		
		return background;
	}
	
	/**
	 * Create corner borders for a modal
	 */
	private static void createCornerBorders(Widget parent, int width, int height)
	{
		// Top-left corner
		createGraphicAbsolute(parent, SpriteID.Steelborder._0, 0, 0, 25, 30);
		
		// Top-right corner
		Widget topRight = createGraphicAbsolute(parent, SpriteID.Steelborder._1, 0, 0, 25, 30);
		topRight.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
		topRight.revalidate();

		// Bottom-left corner
		Widget bottomLeft = createGraphicAbsolute(parent, SpriteID.Steelborder._2, 0, 0, 25, 30);
		bottomLeft.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
		bottomLeft.revalidate();

		// Bottom-right corner
		Widget bottomRight = createGraphicAbsolute(parent, SpriteID.Steelborder._3, 0, 0, 25, 30);
		bottomRight.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
		bottomRight.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
		bottomRight.revalidate();
	}
	
	/**
	 * Create side borders for a modal
	 */
	private static void createSideBorders(Widget parent, int width, int height)
	{
		// Left border
		Widget leftBorder = createGraphicAbsolute(parent, SpriteID.Miscgraphics._2, -15, 0, 36, 36);
		leftBorder.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		leftBorder.setHeightMode(WidgetSizeMode.MINUS);
		leftBorder.revalidate();
		
		// Right border
		Widget rightBorder = createGraphicAbsolute(parent, SpriteID.Steelborder2._1, -15, 0, 36, 36);
		rightBorder.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
		rightBorder.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		rightBorder.setHeightMode(WidgetSizeMode.MINUS);
		rightBorder.revalidate();
		// Top border
		Widget topBorder = createGraphicAbsolute(parent, SpriteID.Steelborder2._0, 0, -15, 36, 36);
		topBorder.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		topBorder.setWidthMode(WidgetSizeMode.MINUS);
		topBorder.revalidate();
		// Bottom border
		Widget bottomBorder = createGraphicAbsolute(parent, SpriteID.Miscgraphics._3, 0, -15, 36, 36);
		bottomBorder.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		bottomBorder.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
		bottomBorder.setWidthMode(WidgetSizeMode.MINUS);
		bottomBorder.revalidate();
	}
	
	/**
	 * Create a title widget
	 */
	public static Widget createTitle(Widget parent, String title)
	{
		Widget widget = createText(parent, title, "ff981f", true, 0, 6, 12, 24);
		widget.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		widget.setWidthMode(WidgetSizeMode.MINUS);
		widget.setFontId(496); // Title font
		widget.setXTextAlignment(WidgetTextAlignment.CENTER);
		widget.setYTextAlignment(WidgetTextAlignment.CENTER);
		widget.revalidate();
		return widget;
	}
	
	/**
	 * Create a draggable title widget
	 */
	public static Widget createDraggableTitle(Widget parent, String title)
	{
		Widget widget = createText(parent, title, "ff981f", true, 0, 6, 12, 24);
		widget.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		widget.setWidthMode(WidgetSizeMode.MINUS);
		widget.setFontId(496); // Title font
		widget.setXTextAlignment(WidgetTextAlignment.CENTER);
		widget.setYTextAlignment(WidgetTextAlignment.CENTER);
		
		// Enable drag functionality
//		widget.setHasListener(true);
//		widget.setDragDeadZone(1); // Minimum 1 pixel movement before drag starts
//		widget.setDragDeadTime(5); // 5 game ticks delay before drag begins
//		widget.setDragParent(parent); // Make this widget the drag handle for itself
//
		widget.revalidate();
		return widget;
	}
	
	/**
	 * Create a close button
	 */
	public static Widget createCloseButton(Widget parent, JavaScriptCallback onClose)
	{
		Widget closeButton = createGraphicAbsolute(parent, SpriteID.CloseButtons._0, 3, 6, 26, 23);
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
	 * Create a tab header
	 */
	public static Widget createTabHeader(Widget parent, String text, int x, int y, boolean selected, JavaScriptCallback onClick)
	{
		Widget widget = createText(parent, text, selected ? "ff981f" : "c8aa6e", true, x, y, 12, 16);
		widget.setHasListener(true);
		widget.setOnClickListener(onClick);
		widget.revalidate();
		return widget;
	}
	
	/**
	 * Create a content container
	 */
	public static Widget createContentContainer(Widget parent, int x, int y, int width, int height)
	{
		Widget widget = createLayer(parent, x, y, width, height);
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
		widget.setWidthMode(WidgetSizeMode.MINUS);
		widget.setHeightMode(WidgetSizeMode.MINUS);
		widget.revalidate();
		return widget;
	}

	// Button and Scrollbar constants
	private static final int BUTTON_BACKGROUND_WIDTH = 95;
	private static final int BUTTON_BACKGROUND_HEIGHT = 71;
	private static final int BUTTON_PADDING = 4;
	private static final int BUTTON_WIDTH = 90;
	private static final int BUTTON_HEIGHT = 60;
	private static final int BUTTON_EDGE_SIZE = 9;
	private static final int ICON_SIZE = 24;
	
	// Scrollbar constants
	private static final int SCROLLBAR_HEIGHT = 20;
	private static final int DRAGGER_WIDTH = 20;
	private static final int SCROLLBAR_PADDING = 16;
	private static final int DRAGGER_END_WIDTH = 5;

	/**
	 * Create a button layer for horizontal scrolling buttons
	 */
	public static Widget createButtonLayer(Widget parent, int x, int y, int width, int height)
	{
		Widget buttonLayer = createLayer(parent, x, y, width, height);
		buttonLayer.setWidthMode(WidgetSizeMode.MINUS);
		buttonLayer.setHasListener(true);
		buttonLayer.revalidate();
		return buttonLayer;
	}

	/**
	 * Create a horizontal scrollbar for button layers
	 */
	public static Widget createHorizontalScrollbar(net.runelite.api.Client client, Widget parent, Widget scrollableContainer, int height, int rows)
	{
		// Create scrollbar container
		Widget scrollbar = createLayer(parent, 0, 0, 0, height);
		scrollbar.setWidthMode(WidgetSizeMode.MINUS);
		scrollbar.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
		scrollbar.revalidate();

		// Create scroll area
		Widget scrollArea = createGraphicAbsolute(scrollbar, SpriteID.ScrollbarDraggerHorizontalV2._3, 0, 0, SCROLLBAR_PADDING * 2, SCROLLBAR_HEIGHT);
		scrollArea.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		scrollArea.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		scrollArea.setWidthMode(WidgetSizeMode.MINUS);
		scrollArea.setNoClickThrough(true);
		scrollArea.setHasListener(true);
		scrollArea.revalidate();

		// Create main dragger
		Widget mainDragger = createGraphicAbsolute(scrollbar, SpriteID.ScrollbarDraggerHorizontalV2._1, SCROLLBAR_PADDING, 0, DRAGGER_WIDTH, SCROLLBAR_HEIGHT);
		mainDragger.setDragParent(scrollArea);
		mainDragger.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		mainDragger.revalidate();

		// Create dragger ends
		Widget mainDraggerLeft = createGraphicAbsolute(scrollbar, SpriteID.ScrollbarDraggerHorizontalV2._2, SCROLLBAR_PADDING, 0, DRAGGER_END_WIDTH, SCROLLBAR_HEIGHT);
		mainDraggerLeft.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		mainDraggerLeft.revalidate();

		Widget mainDraggerRight = createGraphicAbsolute(scrollbar, SpriteID.ScrollbarDraggerHorizontalV2._0, DRAGGER_WIDTH + SCROLLBAR_PADDING, 0, DRAGGER_END_WIDTH, SCROLLBAR_HEIGHT);
		mainDraggerRight.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		mainDraggerRight.revalidate();

		// Create scroll buttons
		Widget leftScroll = createGraphicAbsolute(scrollbar, SpriteID.ScrollbarV2._2, 0, 0, SCROLLBAR_PADDING, SCROLLBAR_HEIGHT);
		leftScroll.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		leftScroll.setHasListener(true);
		leftScroll.revalidate();

		Widget rightScroll = createGraphicAbsolute(scrollbar, SpriteID.ScrollbarV2._3, 0, 0, SCROLLBAR_PADDING, SCROLLBAR_HEIGHT);
		rightScroll.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		rightScroll.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
		rightScroll.setHasListener(true);
		rightScroll.revalidate();

		// Setup scrollbar interaction
		setupScrollbarInteraction(client, scrollArea, scrollableContainer, mainDragger, mainDraggerLeft, mainDraggerRight, leftScroll, rightScroll, rows);

		// Initialize dragger size
		initializeDraggerSize(scrollArea, scrollableContainer, mainDragger, mainDraggerRight, 0);

		return scrollbar;
	}

	/**
	 * Create a styled button with text and icon in a ButtonSection
	 */
	public static Widget createStyledButton(ButtonSection buttonSection, String text, int icon, JavaScriptCallback onClick)
	{
		Widget buttonLayer = buttonSection.getButtonLayer();
		int existingButtons = (buttonLayer.getDynamicChildren() == null) ? 0 : buttonLayer.getDynamicChildren().length;
		
		// Get the number of rows from the button section
		int rows = buttonSection.getRows();
		
		// Simple logic: fill rows first, then move to next column
		int rowIndex = existingButtons % rows;
		int columnIndex = existingButtons / rows;
		
		// Position: column determines X, row determines Y
		int xShift = columnIndex * BUTTON_BACKGROUND_WIDTH;
		int yShift = rowIndex * BUTTON_BACKGROUND_HEIGHT;

		// Create button container
		Widget buttonContainer = createLayer(buttonLayer, xShift, yShift, BUTTON_BACKGROUND_WIDTH, BUTTON_BACKGROUND_HEIGHT);

		// Create button background with hover effects
		Widget buttonBackground = createGraphic(buttonContainer, SpriteID.TRADEBACKING, 0, 0, BUTTON_WIDTH, BUTTON_HEIGHT);
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
		createButtonBorder(buttonContainer);

		// Create button text
		createCenteredText(buttonContainer, text, "ff9933", true, BUTTON_PADDING, BUTTON_PADDING + 1, BUTTON_WIDTH, 12);

		// Create button icon
		createGraphic(buttonContainer, icon, (BUTTON_WIDTH - ICON_SIZE) / 2, 25, ICON_SIZE, ICON_SIZE);

		// Update scrollbar visibility after adding button
		updateButtonSectionScrollbar(buttonSection);

		return buttonContainer;
	}

	/**
	 * Create button border pieces
	 */
	private static void createButtonBorder(Widget buttonContainer)
	{
		// Corner pieces
		createGraphic(buttonContainer, SpriteID.V2StoneButton._0, BUTTON_PADDING, 0, BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE); // Top-left
		createGraphic(buttonContainer, SpriteID.V2StoneButton._1, BUTTON_WIDTH - BUTTON_EDGE_SIZE, 0, BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE); // Top-right
		createGraphic(buttonContainer, SpriteID.V2StoneButton._2, BUTTON_PADDING, BUTTON_HEIGHT - BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE); // Bottom-left
		createGraphic(buttonContainer, SpriteID.V2StoneButton._3, BUTTON_WIDTH - BUTTON_EDGE_SIZE, BUTTON_HEIGHT - BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE); // Bottom-right

		// Edge pieces
		createGraphic(buttonContainer, SpriteID.V2StoneButton._4, BUTTON_PADDING, BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE, BUTTON_HEIGHT - (BUTTON_EDGE_SIZE * 2)); // Left
		createGraphic(buttonContainer, SpriteID.V2StoneButton._5, BUTTON_EDGE_SIZE + BUTTON_PADDING, 0, BUTTON_WIDTH - (BUTTON_EDGE_SIZE * 2), BUTTON_EDGE_SIZE); // Top
		createGraphic(buttonContainer, SpriteID.V2StoneButton._6, BUTTON_WIDTH - BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE, BUTTON_HEIGHT - (BUTTON_EDGE_SIZE * 2)); // Right
		createGraphic(buttonContainer, SpriteID.V2StoneButton._7, BUTTON_EDGE_SIZE + BUTTON_PADDING, BUTTON_HEIGHT - BUTTON_EDGE_SIZE, BUTTON_WIDTH - (BUTTON_EDGE_SIZE * 2), BUTTON_EDGE_SIZE); // Bottom
	}

	/**
	 * Setup scrollbar interaction logic
	 */
	private static void setupScrollbarInteraction(net.runelite.api.Client client, Widget scrollArea, Widget scrollableContainer, Widget mainDragger, Widget mainDraggerLeft, Widget mainDraggerRight, Widget leftScroll, Widget rightScroll, int rows)
	{
		AtomicBoolean clicked = new AtomicBoolean(false);
		AtomicInteger clickOffset = new AtomicInteger(0); // Offset from left edge of dragger where mouse was clicked
		AtomicBoolean leftButtonPressed = new AtomicBoolean(false);
		AtomicBoolean rightButtonPressed = new AtomicBoolean(false);

		// Click handling
		scrollArea.setOnClickListener((JavaScriptCallback) (ev) -> {
			clicked.set(true);
			
			// Calculate the offset from the left edge of the dragger where the mouse was clicked
			int mouseX = client.getMouseCanvasPosition().getX();
			int draggerX = mainDragger.getCanvasLocation().getX();
			int draggerWidth = mainDragger.getOriginalWidth();
			
			// If clicked outside the dragger, treat it as clicking the center of the dragger
			if (mouseX < draggerX || mouseX > draggerX + draggerWidth) {
				clickOffset.set(draggerWidth / 2); // Center of dragger
			} else {
				clickOffset.set(mouseX - draggerX); // Actual click position
			}
		});


		// Scroll wheel handling
		scrollArea.setOnScrollWheelListener((JavaScriptCallback) (ev) -> {
			int existingButtons = (scrollableContainer.getDynamicChildren() == null) ? 0 : scrollableContainer.getDynamicChildren().length;
			// Get current dragger position relative to scroll area
			int currentDraggerX = mainDragger.getOriginalX() - SCROLLBAR_PADDING;
			int xPos = currentDraggerX + (10 * ev.getMouseY());
			int totalArea = scrollArea.getWidth();
			
			// Get current dragger width (don't recalculate during scroll)
			int currentDraggerWidth = mainDragger.getOriginalWidth();
			
			// Clamp to valid dragger positions within the scroll area
			if (xPos > totalArea - currentDraggerWidth) xPos = totalArea - currentDraggerWidth;
			if (xPos < 0) xPos = 0;

			scrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerLeft, mainDraggerRight, xPos, existingButtons, rows);
		});

		// Add scroll listener to scrollable container
		scrollableContainer.setOnScrollWheelListener((JavaScriptCallback) (ev) -> {
			int existingButtons = (scrollableContainer.getDynamicChildren() == null) ? 0 : scrollableContainer.getDynamicChildren().length;
			// Get current dragger position relative to scroll area
			int currentDraggerX = mainDragger.getOriginalX() - SCROLLBAR_PADDING;
			int xPos = currentDraggerX + (10 * ev.getMouseY());
			int totalArea = scrollArea.getWidth();
			
			// Get current dragger width (don't recalculate during scroll)
			int currentDraggerWidth = mainDragger.getOriginalWidth();
			
			// Clamp to valid dragger positions within the scroll area
			if (xPos > totalArea - currentDraggerWidth) xPos = totalArea - currentDraggerWidth;
			if (xPos < 0) xPos = 0;

			scrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerLeft, mainDraggerRight, xPos, existingButtons, rows);
		});

		// Left scroll button handling
		leftScroll.setOnClickListener((JavaScriptCallback) (ev) -> {
			leftButtonPressed.set(true);
		});
		
		leftScroll.setOnMouseOverListener((JavaScriptCallback) (ev) -> {
			if (client.getMouseCurrentButton() == 1) {
				leftButtonPressed.set(true);
			}
		});
		
		leftScroll.setOnMouseLeaveListener((JavaScriptCallback) (ev) -> {
			leftButtonPressed.set(false);
		});

		// Right scroll button handling
		rightScroll.setOnClickListener((JavaScriptCallback) (ev) -> {
			rightButtonPressed.set(true);
		});
		
		rightScroll.setOnMouseOverListener((JavaScriptCallback) (ev) -> {
			if (client.getMouseCurrentButton() == 1) {
				rightButtonPressed.set(true);
			}
		});
		
		rightScroll.setOnMouseLeaveListener((JavaScriptCallback) (ev) -> {
			rightButtonPressed.set(false);
		});

		// Timer handling for scroll buttons
		scrollArea.setOnTimerListener((JavaScriptCallback) (ev) -> {
			// Handle dragger dragging (existing logic)
			if (clicked.get())
			{
				if (client.getMouseCurrentButton() != 1)
				{
					clicked.set(false);
					return;
				}
				int existingButtons = (scrollableContainer.getDynamicChildren() == null) ? 0 : scrollableContainer.getDynamicChildren().length;

				// Get current dragger width (don't recalculate during drag)
				int currentDraggerWidth = mainDragger.getOriginalWidth();
				int totalArea = scrollArea.getWidth();

				// Calculate mouse position relative to scrollbar
				int mouseX = client.getMouseCanvasPosition().getX();
				int scrollStartX = scrollArea.getCanvasLocation().getX();
				int mouseRelativeToScrollArea = mouseX - scrollStartX;
				
				// Calculate dragger position maintaining the click offset
				int xPosClicked = mouseRelativeToScrollArea - clickOffset.get();
				
				// Clamp to valid dragger positions within the scroll area
				if (xPosClicked > totalArea - currentDraggerWidth) xPosClicked = totalArea - currentDraggerWidth;
				if (xPosClicked < 0) xPosClicked = 0;

				scrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerLeft, mainDraggerRight, xPosClicked, existingButtons, rows);
			}
			
			// Handle scroll button pressing
			if (leftButtonPressed.get() && client.getMouseCurrentButton() == 1)
			{
				int existingButtons = (scrollableContainer.getDynamicChildren() == null) ? 0 : scrollableContainer.getDynamicChildren().length;
				int currentDraggerX = mainDragger.getOriginalX() - SCROLLBAR_PADDING;
				
				// Move dragger left by a small amount
				int newXPos = Math.max(0, currentDraggerX - 5);
				
				scrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerLeft, mainDraggerRight, newXPos, existingButtons, rows);
			}
			else if (rightButtonPressed.get() && client.getMouseCurrentButton() == 1)
			{
				int existingButtons = (scrollableContainer.getDynamicChildren() == null) ? 0 : scrollableContainer.getDynamicChildren().length;
				int currentDraggerX = mainDragger.getOriginalX() - SCROLLBAR_PADDING;
				int currentDraggerWidth = mainDragger.getOriginalWidth();
				int totalArea = scrollArea.getWidth();
				int totalDraggableArea = totalArea - currentDraggerWidth;
				
				// Move dragger right by a small amount
				int newXPos = Math.min(totalDraggableArea, currentDraggerX + 5);
				
				scrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerLeft, mainDraggerRight, newXPos, existingButtons, rows);
			}
			else
			{
				// Release buttons if mouse is not pressed
				leftButtonPressed.set(false);
				rightButtonPressed.set(false);
			}
		});

		scrollArea.revalidate();
		scrollableContainer.revalidate();
	}

	/**
	 * Initialize dragger size based on content
	 */
	private static void initializeDraggerSize(Widget scrollArea, Widget buttonLayer, Widget mainDragger, Widget mainDraggerRight, int totalColumns)
	{
		int totalArea = scrollArea.getWidth();
		
		// Calculate content dimensions
		int totalContentWidth = totalColumns * BUTTON_BACKGROUND_WIDTH;
		int containerWidth = buttonLayer.getWidth();
		
		// Calculate dynamic dragger size based on visible area ratio
		// If no content or content fits entirely, dragger fills the entire area
		float visibleRatio = (totalContentWidth == 0) ? 1.0f : Math.min(1.0f, (float) containerWidth / totalContentWidth);
		int dynamicDraggerWidth = Math.max(DRAGGER_WIDTH, Math.round(totalArea * visibleRatio));

		// Update dragger size
		mainDragger.setOriginalWidth(dynamicDraggerWidth);
		mainDragger.revalidate();
		
		// Update dragger end positions
		mainDraggerRight.setOriginalX(SCROLLBAR_PADDING + dynamicDraggerWidth);
		mainDraggerRight.revalidate();
	}

	/**
	 * Handle scrollbar movement
	 */
	private static void scrollbarMove(Widget scrollArea, Widget buttonLayer, Widget mainDragger, Widget mainDraggerLeft, Widget mainDraggerRight, int xPosToMoveScrollbar, int numButtons, int rows)
	{
		int totalArea = scrollArea.getWidth();
		
		// Calculate content dimensions
		int totalColumns = (int) Math.ceil(numButtons / (double) rows);
		int totalContentWidth = totalColumns * BUTTON_BACKGROUND_WIDTH;
		int containerWidth = buttonLayer.getWidth();
		int overflowWidth = Math.max(0, totalContentWidth - containerWidth);
		
		// Recalculate dragger size based on current content
		float visibleRatio = Math.min(1.0f, (float) containerWidth / Math.max(1, totalContentWidth));
		int dynamicDraggerWidth = Math.max(DRAGGER_WIDTH, Math.round(totalArea * visibleRatio));

		// Calculate the draggable area within the scroll area (accounting for current dragger width)
		int totalDraggableArea = totalArea - dynamicDraggerWidth;
		
		// Set dragger position relative to the scrollbar container
		int draggerAbsoluteX = SCROLLBAR_PADDING + xPosToMoveScrollbar;
		mainDragger.setOriginalX(draggerAbsoluteX);
		mainDragger.revalidate();
		mainDraggerLeft.setOriginalX(draggerAbsoluteX);
		mainDraggerLeft.revalidate();
		mainDraggerRight.setOriginalX(draggerAbsoluteX + dynamicDraggerWidth);
		mainDraggerRight.revalidate();
		
		// Calculate scroll ratio based on dragger position within the draggable area
		float scrollRatio = (float) xPosToMoveScrollbar / Math.max(1, totalDraggableArea);
		
		// Clamp scroll ratio to [0, 1]
		scrollRatio = Math.max(0, Math.min(1, scrollRatio));
		
		// Calculate scroll position - 0% scrollbar = 0% content, 100% scrollbar = 100% content
		int posToMoveButtonLayer = Math.round(overflowWidth * scrollRatio);
		buttonLayer.setScrollX(posToMoveButtonLayer);
	}

	public static ButtonSection createButtonSection(Widget parent, int x, int y, int rows, Client client)
	{
		// Calculate height based on number of rows and button dimensions
		int buttonAreaHeight = rows * BUTTON_BACKGROUND_HEIGHT;
		int totalHeight = buttonAreaHeight + 15; // 5px padding between buttons and scrollbar
		
		// Create container for the entire button section
		Widget sectionContainer = createLayer(parent, x, y, x, totalHeight);
		sectionContainer.setWidthMode(WidgetSizeMode.MINUS);
		sectionContainer.revalidate();
		
		// Create button layer within the container
		Widget buttonLayer = createButtonLayer(sectionContainer, 0, 0, 0, buttonAreaHeight);
		
		// Create scrollbar within the container, positioned at the bottom
		Widget scrollbar = createHorizontalScrollbar(client, sectionContainer, buttonLayer, SCROLLBAR_HEIGHT, rows);
		scrollbar.revalidate();
		
		// Initially hide the scrollbar - it will be shown if needed when buttons are added
		scrollbar.setHidden(true);
		
		return new ButtonSection(sectionContainer, buttonLayer, scrollbar, rows);
	}

	/**
	 * Update scrollbar visibility and dragger size for a ButtonSection
	 */
	public static void updateButtonSectionScrollbar(ButtonSection buttonSection)
	{
		Widget buttonLayer = buttonSection.getButtonLayer();
		Widget scrollbar = buttonSection.getScrollbar();
		int rows = buttonSection.getRows();
		
		// Calculate if we need to show the scrollbar based on total columns needed
		int numButtons = (buttonLayer.getDynamicChildren() == null) ? 0 : buttonLayer.getDynamicChildren().length;
		int totalColumns = (int) Math.ceil(numButtons / (double) rows);
		int totalContentWidth = totalColumns * BUTTON_BACKGROUND_WIDTH;
		int containerWidth = buttonLayer.getWidth();
		
		// Show scrollbar if there's content that overflows, or if there's no content (for proper initialization)
		boolean shouldShow = totalContentWidth > containerWidth || totalContentWidth == 0;
		scrollbar.setHidden(!shouldShow);
		
		// Update dragger size if scrollbar is visible
		if (shouldShow && scrollbar.getDynamicChildren() != null)
		{
			// Find the scroll area and dragger widgets
			Widget scrollArea = null;
			Widget mainDragger = null;
			Widget mainDraggerLeft = null;
			Widget mainDraggerRight = null;
			
			for (Widget child : scrollbar.getDynamicChildren())
			{
				if (child.getSpriteId() == SpriteID.ScrollbarDraggerHorizontalV2._3) // Scroll area
				{
					scrollArea = child;
				}
				else if (child.getSpriteId() == SpriteID.ScrollbarDraggerHorizontalV2._1) // Main dragger
				{
					mainDragger = child;
				}
				else if (child.getSpriteId() == SpriteID.ScrollbarDraggerHorizontalV2._2) // Left end
				{
					mainDraggerLeft = child;
				}
				else if (child.getSpriteId() == SpriteID.ScrollbarDraggerHorizontalV2._0) // Right end
				{
					mainDraggerRight = child;
				}
			}
			
			if (scrollArea != null && mainDragger != null && mainDraggerLeft != null && mainDraggerRight != null)
			{
				initializeDraggerSize(scrollArea, buttonLayer, mainDragger, mainDraggerRight, totalColumns);
			}
		}
		
		// Debug output to help troubleshoot
		System.out.println("Scrollbar visibility check: " + numButtons + " buttons, " + rows + " rows, " + 
			totalColumns + " columns, " + totalContentWidth + " total width, " + containerWidth + " container width, show=" + shouldShow);
	}
}
