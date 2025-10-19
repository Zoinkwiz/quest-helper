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
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetSizeMode;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.WidgetType;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generic container with horizontal scrollbar functionality
 * Encapsulates scrollbar logic and provides a content layer for child widgets
 * Can be used with any type of content that implements ScrollableContent
 */
public class ScrollableContainer
{
	// Scrollbar constants
	private static final int SCROLLBAR_HEIGHT = 20;
	private static final int DRAGGER_WIDTH = 20;
	private static final int SCROLLBAR_PADDING = 16;
	private static final int DRAGGER_END_WIDTH = 5;

	private final Widget container;
	private final Widget contentLayer;
	private final Widget scrollbar;
	private final Client client;
	private ScrollableContent content;

	// Scrollbar interaction state
	private final AtomicBoolean clicked = new AtomicBoolean(false);
	private final AtomicInteger clickOffset = new AtomicInteger(0);
	private final AtomicBoolean leftButtonPressed = new AtomicBoolean(false);
	private final AtomicBoolean rightButtonPressed = new AtomicBoolean(false);

	public ScrollableContainer(Widget parent, int x, int y, int width, int height, Client client)
	{
		this.client = client;
		
		// Create container for the entire scrollable section
		this.container = WidgetFactory.createLayer(parent, x, y, width, height);
		this.container.setWidthMode(WidgetSizeMode.MINUS);
		this.container.revalidate();
		
		// Create content layer within the container (takes up most of the space, leaving room for scrollbar)
		int contentHeight = height - SCROLLBAR_HEIGHT - 5; // Leave space for scrollbar
		this.contentLayer = WidgetFactory.createButtonLayer(this.container, 0, 0, 0, contentHeight);

		var border = container.createChild(-1, WidgetType.RECTANGLE);
		border.setTextColor(Integer.parseInt("111111", 16));
		border.setOpacity(100);
		border.setWidthMode(WidgetSizeMode.MINUS);
		border.setHeightMode(WidgetSizeMode.MINUS);
		border.revalidate();
		
		// Create scrollbar within the container, positioned at the bottom
		this.scrollbar = createHorizontalScrollbar(this.container, this.contentLayer, SCROLLBAR_HEIGHT);
		this.scrollbar.revalidate();
		
		// Initially hide the scrollbar - it will be shown if needed when content is added
		this.scrollbar.setHidden(true);
	}

	/**
	 * Set the content that this container will scroll
	 */
	public void setContent(ScrollableContent content)
	{
		this.content = content;
		updateScrollbar();
	}

	/**
	 * Get the content layer where child widgets should be added
	 */
	public Widget getContentLayer()
	{
		return contentLayer;
	}

	/**
	 * Get the container widget
	 */
	public Widget getContainer()
	{
		return container;
	}

	/**
	 * Update scrollbar visibility and dragger size based on current content
	 */
	public void updateScrollbar()
	{
		if (content == null) return;
		
		// Calculate if we need to show the scrollbar based on total columns needed
		int itemCount = content.getItemCount();
		int totalColumns = (int) Math.ceil(itemCount / (double) content.getRows());
		int totalContentWidth = totalColumns * content.getItemWidth();
		int containerWidth = contentLayer.getWidth();
		
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
				initializeDraggerSize(scrollArea, contentLayer, mainDragger, mainDraggerRight, totalColumns);
			}
		}
	}

	/**
	 * Create a horizontal scrollbar for content layers
	 */
	private Widget createHorizontalScrollbar(Widget parent, Widget scrollableContainer, int height)
	{
		// Create scrollbar container
		Widget scrollbar = WidgetFactory.createLayer(parent, 0, 0, 0, height);
		scrollbar.setWidthMode(WidgetSizeMode.MINUS);
		scrollbar.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
		scrollbar.revalidate();

		// Create scroll area
		Widget scrollArea = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarDraggerHorizontalV2._3, 0, 0, SCROLLBAR_PADDING * 2, SCROLLBAR_HEIGHT);
		scrollArea.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		scrollArea.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		scrollArea.setWidthMode(WidgetSizeMode.MINUS);
		scrollArea.setNoClickThrough(true);
		scrollArea.setHasListener(true);
		scrollArea.revalidate();

		// Create main dragger
		Widget mainDragger = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarDraggerHorizontalV2._1, SCROLLBAR_PADDING, 0, DRAGGER_WIDTH, SCROLLBAR_HEIGHT);
		mainDragger.setDragParent(scrollArea);
		mainDragger.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		mainDragger.revalidate();

		// Create dragger ends
		Widget mainDraggerLeft = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarDraggerHorizontalV2._2, SCROLLBAR_PADDING, 0, DRAGGER_END_WIDTH, SCROLLBAR_HEIGHT);
		mainDraggerLeft.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		mainDraggerLeft.revalidate();

		Widget mainDraggerRight = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarDraggerHorizontalV2._0, DRAGGER_WIDTH + SCROLLBAR_PADDING, 0, DRAGGER_END_WIDTH, SCROLLBAR_HEIGHT);
		mainDraggerRight.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		mainDraggerRight.revalidate();

		// Create scroll buttons
		Widget leftScroll = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarV2._2, 0, 0, SCROLLBAR_PADDING, SCROLLBAR_HEIGHT);
		leftScroll.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		leftScroll.setHasListener(true);
		leftScroll.revalidate();

		Widget rightScroll = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarV2._3, 0, 0, SCROLLBAR_PADDING, SCROLLBAR_HEIGHT);
		rightScroll.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		rightScroll.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
		rightScroll.setHasListener(true);
		rightScroll.revalidate();

		// Setup scrollbar interaction
		setupScrollbarInteraction(scrollArea, scrollableContainer, mainDragger, mainDraggerLeft, mainDraggerRight, leftScroll, rightScroll);

		// Initialize dragger size
		initializeDraggerSize(scrollArea, scrollableContainer, mainDragger, mainDraggerRight, 0);

		return scrollbar;
	}

	/**
	 * Setup scrollbar interaction logic
	 */
	private void setupScrollbarInteraction(Widget scrollArea, Widget scrollableContainer, Widget mainDragger, Widget mainDraggerLeft, Widget mainDraggerRight, Widget leftScroll, Widget rightScroll)
	{
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
			if (content == null) return;
			int itemCount = content.getItemCount();
			// Get current dragger position relative to scroll area
			int currentDraggerX = mainDragger.getOriginalX() - SCROLLBAR_PADDING;
			int xPos = currentDraggerX + (10 * ev.getMouseY());
			int totalArea = scrollArea.getWidth();
			
			// Get current dragger width (don't recalculate during scroll)
			int currentDraggerWidth = mainDragger.getOriginalWidth();
			
			// Clamp to valid dragger positions within the scroll area
			if (xPos > totalArea - currentDraggerWidth) xPos = totalArea - currentDraggerWidth;
			if (xPos < 0) xPos = 0;

			scrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerLeft, mainDraggerRight, xPos, itemCount);
		});

		// Add scroll listener to scrollable container
		scrollableContainer.setOnScrollWheelListener((JavaScriptCallback) (ev) -> {
			if (content == null) return;
			int itemCount = content.getItemCount();
			// Get current dragger position relative to scroll area
			int currentDraggerX = mainDragger.getOriginalX() - SCROLLBAR_PADDING;
			int xPos = currentDraggerX + (10 * ev.getMouseY());
			int totalArea = scrollArea.getWidth();
			
			// Get current dragger width (don't recalculate during scroll)
			int currentDraggerWidth = mainDragger.getOriginalWidth();
			
			// Clamp to valid dragger positions within the scroll area
			if (xPos > totalArea - currentDraggerWidth) xPos = totalArea - currentDraggerWidth;
			if (xPos < 0) xPos = 0;

			scrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerLeft, mainDraggerRight, xPos, itemCount);
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
			if (content == null) return;
			
			// Handle dragger dragging (existing logic)
			if (clicked.get())
			{
				if (client.getMouseCurrentButton() != 1)
				{
					clicked.set(false);
					return;
				}
				int itemCount = content.getItemCount();

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

				scrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerLeft, mainDraggerRight, xPosClicked, itemCount);
			}
			
			// Handle scroll button pressing
			if (leftButtonPressed.get() && client.getMouseCurrentButton() == 1)
			{
				int itemCount = content.getItemCount();
				int currentDraggerX = mainDragger.getOriginalX() - SCROLLBAR_PADDING;
				
				// Move dragger left by a small amount
				int newXPos = Math.max(0, currentDraggerX - 5);
				
				scrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerLeft, mainDraggerRight, newXPos, itemCount);
			}
			else if (rightButtonPressed.get() && client.getMouseCurrentButton() == 1)
			{
				int itemCount = content.getItemCount();
				int currentDraggerX = mainDragger.getOriginalX() - SCROLLBAR_PADDING;
				int currentDraggerWidth = mainDragger.getOriginalWidth();
				int totalArea = scrollArea.getWidth();
				int totalDraggableArea = totalArea - currentDraggerWidth;
				
				// Move dragger right by a small amount
				int newXPos = Math.min(totalDraggableArea, currentDraggerX + 5);
				
				scrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerLeft, mainDraggerRight, newXPos, itemCount);
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
	private void initializeDraggerSize(Widget scrollArea, Widget contentLayer, Widget mainDragger, Widget mainDraggerRight, int totalColumns)
	{
		if (content == null) return;
		
		int totalArea = scrollArea.getWidth();
		
		// Calculate content dimensions
		int totalContentWidth = totalColumns * content.getItemWidth();
		int containerWidth = contentLayer.getWidth();
		
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
	private void scrollbarMove(Widget scrollArea, Widget contentLayer, Widget mainDragger, Widget mainDraggerLeft, Widget mainDraggerRight, int xPosToMoveScrollbar, int itemCount)
	{
		if (content == null) return;
		
		int totalArea = scrollArea.getWidth();
		
		// Calculate content dimensions
		int totalColumns = (int) Math.ceil(itemCount / (double) content.getRows());
		int totalContentWidth = totalColumns * content.getItemWidth();
		int containerWidth = contentLayer.getWidth();
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
		int posToMoveContentLayer = Math.round(overflowWidth * scrollRatio);
		contentLayer.setScrollX(posToMoveContentLayer);
	}
}
