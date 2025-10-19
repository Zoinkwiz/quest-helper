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
 * Generic container with vertical scrollbar functionality
 * Encapsulates scrollbar logic and provides a content layer for child widgets
 * Can be used with any type of content that implements ScrollableContent
 */
public class VerticalScrollableContainer
{
	// Scrollbar constants
	private static final int SCROLLBAR_WIDTH = 20;
	private static final int DRAGGER_HEIGHT = 20;
	private static final int SCROLLBAR_PADDING = 16;
	private static final int DRAGGER_END_HEIGHT = 5;

	private final Widget container;
	private final Widget contentLayer;
	private final Widget scrollbar;
	private final Client client;
	private ScrollableContent content;

	// Scrollbar interaction state
	private final AtomicBoolean clicked = new AtomicBoolean(false);
	private final AtomicInteger clickOffset = new AtomicInteger(0);
	private final AtomicBoolean upButtonPressed = new AtomicBoolean(false);
	private final AtomicBoolean downButtonPressed = new AtomicBoolean(false);

	public VerticalScrollableContainer(Widget parent, int x, int y, int width, int height, Client client)
	{
		this.client = client;
		
		// Create container for the entire scrollable section
		this.container = WidgetFactory.createLayer(parent, x, y, width, height);
		this.container.setWidthMode(WidgetSizeMode.MINUS);
		this.container.revalidate();
		
		// Create content layer within the container (takes up most of the space, leaving room for scrollbar)
		int contentWidth = width - SCROLLBAR_WIDTH - 5; // Leave space for scrollbar
		this.contentLayer = WidgetFactory.createButtonLayer(this.container, 0, 0, contentWidth, 0);
		this.contentLayer.setHeightMode(WidgetSizeMode.MINUS);
		this.contentLayer.revalidate();

		var border = container.createChild(-1, WidgetType.RECTANGLE);
		border.setTextColor(Integer.parseInt("111111", 16));
		border.setOpacity(100);
		border.setWidthMode(WidgetSizeMode.MINUS);
		border.setHeightMode(WidgetSizeMode.MINUS);
		border.revalidate();
		
		// Create scrollbar within the container, positioned at the right
		this.scrollbar = createVerticalScrollbar(this.container, this.contentLayer, SCROLLBAR_WIDTH);
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
		
		// Calculate if we need to show the scrollbar based on total rows needed
		int itemCount = content.getItemCount();
		int totalRows = (int) Math.ceil(itemCount / (double) content.getColumns());
		int totalContentHeight = totalRows * content.getItemHeight();
		int containerHeight = contentLayer.getHeight();
		
		// Show scrollbar if there's content that overflows, or if there's no content (for proper initialization)
		boolean shouldShow = totalContentHeight > containerHeight || totalContentHeight == 0;
		scrollbar.setHidden(!shouldShow);
		
		// Update dragger size if scrollbar is visible
		if (shouldShow && scrollbar.getDynamicChildren() != null)
		{
			// Find the scroll area and dragger widgets
			Widget scrollArea = null;
			Widget mainDragger = null;
			Widget mainDraggerTop = null;
			Widget mainDraggerBottom = null;
			
			for (Widget child : scrollbar.getDynamicChildren())
			{
				if (child.getSpriteId() == SpriteID.ScrollbarDraggerV2._3) // Scroll area
				{
					scrollArea = child;
				}
				else if (child.getSpriteId() == SpriteID.ScrollbarDraggerV2._1) // Main dragger
				{
					mainDragger = child;
				}
				else if (child.getSpriteId() == SpriteID.ScrollbarDraggerV2._2) // Top end
				{
					mainDraggerTop = child;
				}
				else if (child.getSpriteId() == SpriteID.ScrollbarDraggerV2._0) // Bottom end
				{
					mainDraggerBottom = child;
				}
			}
			
			if (scrollArea != null && mainDragger != null && mainDraggerTop != null && mainDraggerBottom != null)
			{
				initializeDraggerSize(scrollArea, contentLayer, mainDragger, mainDraggerBottom, totalRows);
			}
		}
	}

	/**
	 * Create a vertical scrollbar for content layers
	 */
	private Widget createVerticalScrollbar(Widget parent, Widget scrollableContainer, int width)
	{
		// Create scrollbar container
		Widget scrollbar = WidgetFactory.createLayer(parent, 0, 0, width, 0);
		scrollbar.setHeightMode(WidgetSizeMode.MINUS);
		scrollbar.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
		scrollbar.revalidate();

		// Create scroll area
		Widget scrollArea = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarDraggerV2._3, 0, 0, SCROLLBAR_WIDTH, SCROLLBAR_PADDING * 2);
		scrollArea.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		scrollArea.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		scrollArea.setHeightMode(WidgetSizeMode.MINUS);
		scrollArea.setNoClickThrough(true);
		scrollArea.setHasListener(true);
		scrollArea.revalidate();

		// Create main dragger
		Widget mainDragger = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarDraggerV2._1, 0, SCROLLBAR_PADDING, SCROLLBAR_WIDTH, DRAGGER_HEIGHT);
		mainDragger.setDragParent(scrollArea);
		mainDragger.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		mainDragger.revalidate();

		// Create dragger ends
		Widget mainDraggerTop = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarDraggerV2._2, 0, SCROLLBAR_PADDING, SCROLLBAR_WIDTH, DRAGGER_END_HEIGHT);
		mainDraggerTop.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		mainDraggerTop.revalidate();

		Widget mainDraggerBottom = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarDraggerV2._0, 0, DRAGGER_HEIGHT + SCROLLBAR_PADDING, SCROLLBAR_WIDTH, DRAGGER_END_HEIGHT);
		mainDraggerBottom.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		mainDraggerBottom.revalidate();

		// Create scroll buttons
		Widget upScroll = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarV2._0, 0, 0, SCROLLBAR_WIDTH, SCROLLBAR_PADDING);
		upScroll.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		upScroll.setHasListener(true);
		upScroll.revalidate();

		Widget downScroll = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarV2._1, 0, 0, SCROLLBAR_WIDTH, SCROLLBAR_PADDING);
		downScroll.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		downScroll.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
		downScroll.setHasListener(true);
		downScroll.revalidate();

		// Setup scrollbar interaction
		setupScrollbarInteraction(scrollArea, scrollableContainer, mainDragger, mainDraggerTop, mainDraggerBottom, upScroll, downScroll);

		// Initialize dragger size
		initializeDraggerSize(scrollArea, scrollableContainer, mainDragger, mainDraggerBottom, 0);

		return scrollbar;
	}

	/**
	 * Setup scrollbar interaction logic
	 */
	private void setupScrollbarInteraction(Widget scrollArea, Widget scrollableContainer, Widget mainDragger, Widget mainDraggerTop, Widget mainDraggerBottom, Widget upScroll, Widget downScroll)
	{
		// Click handling
		scrollArea.setOnClickListener((JavaScriptCallback) (ev) -> {
			clicked.set(true);
			
			// Calculate the offset from the top edge of the dragger where the mouse was clicked
			int mouseY = client.getMouseCanvasPosition().getY();
			int draggerY = mainDragger.getCanvasLocation().getY();
			int draggerHeight = mainDragger.getOriginalHeight();
			
			// If clicked outside the dragger, treat it as clicking the center of the dragger
			if (mouseY < draggerY || mouseY > draggerY + draggerHeight) {
				clickOffset.set(draggerHeight / 2); // Center of dragger
			} else {
				clickOffset.set(mouseY - draggerY); // Actual click position
			}
		});

		// Scroll wheel handling
		scrollArea.setOnScrollWheelListener((JavaScriptCallback) (ev) -> {
			if (content == null) return;
			int itemCount = content.getItemCount();
			// Get current dragger position relative to scroll area
			int currentDraggerY = mainDragger.getOriginalY() - SCROLLBAR_PADDING;
			int yPos = currentDraggerY + (10 * ev.getMouseY());
			int totalArea = scrollArea.getHeight();
			
			// Get current dragger height (don't recalculate during scroll)
			int currentDraggerHeight = mainDragger.getOriginalHeight();
			
			// Clamp to valid dragger positions within the scroll area
			if (yPos > totalArea - currentDraggerHeight) yPos = totalArea - currentDraggerHeight;
			if (yPos < 0) yPos = 0;

			scrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerTop, mainDraggerBottom, yPos, itemCount);
		});

		// Add scroll listener to scrollable container
		scrollableContainer.setOnScrollWheelListener((JavaScriptCallback) (ev) -> {
			if (content == null) return;
			int itemCount = content.getItemCount();
			// Get current dragger position relative to scroll area
			int currentDraggerY = mainDragger.getOriginalY() - SCROLLBAR_PADDING;
			int yPos = currentDraggerY + (10 * ev.getMouseY());
			int totalArea = scrollArea.getHeight();
			
			// Get current dragger height (don't recalculate during scroll)
			int currentDraggerHeight = mainDragger.getOriginalHeight();
			
			// Clamp to valid dragger positions within the scroll area
			if (yPos > totalArea - currentDraggerHeight) yPos = totalArea - currentDraggerHeight;
			if (yPos < 0) yPos = 0;

			scrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerTop, mainDraggerBottom, yPos, itemCount);
		});

		// Up scroll button handling
		upScroll.setOnClickListener((JavaScriptCallback) (ev) -> {
			upButtonPressed.set(true);
		});
		
		upScroll.setOnMouseOverListener((JavaScriptCallback) (ev) -> {
			if (client.getMouseCurrentButton() == 1) {
				upButtonPressed.set(true);
			}
		});
		
		upScroll.setOnMouseLeaveListener((JavaScriptCallback) (ev) -> {
			upButtonPressed.set(false);
		});

		// Down scroll button handling
		downScroll.setOnClickListener((JavaScriptCallback) (ev) -> {
			downButtonPressed.set(true);
		});
		
		downScroll.setOnMouseOverListener((JavaScriptCallback) (ev) -> {
			if (client.getMouseCurrentButton() == 1) {
				downButtonPressed.set(true);
			}
		});
		
		downScroll.setOnMouseLeaveListener((JavaScriptCallback) (ev) -> {
			downButtonPressed.set(false);
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

				// Get current dragger height (don't recalculate during drag)
				int currentDraggerHeight = mainDragger.getOriginalHeight();
				int totalArea = scrollArea.getHeight();

				// Calculate mouse position relative to scrollbar
				int mouseY = client.getMouseCanvasPosition().getY();
				int scrollStartY = scrollArea.getCanvasLocation().getY();
				int mouseRelativeToScrollArea = mouseY - scrollStartY;
				
				// Calculate dragger position maintaining the click offset
				int yPosClicked = mouseRelativeToScrollArea - clickOffset.get();
				
				// Clamp to valid dragger positions within the scroll area
				if (yPosClicked > totalArea - currentDraggerHeight) yPosClicked = totalArea - currentDraggerHeight;
				if (yPosClicked < 0) yPosClicked = 0;

				scrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerTop, mainDraggerBottom, yPosClicked, itemCount);
			}
			
			// Handle scroll button pressing
			if (upButtonPressed.get() && client.getMouseCurrentButton() == 1)
			{
				int itemCount = content.getItemCount();
				int currentDraggerY = mainDragger.getOriginalY() - SCROLLBAR_PADDING;
				
				// Move dragger up by a small amount
				int newYPos = Math.max(0, currentDraggerY - 5);
				
				scrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerTop, mainDraggerBottom, newYPos, itemCount);
			}
			else if (downButtonPressed.get() && client.getMouseCurrentButton() == 1)
			{
				int itemCount = content.getItemCount();
				int currentDraggerY = mainDragger.getOriginalY() - SCROLLBAR_PADDING;
				int currentDraggerHeight = mainDragger.getOriginalHeight();
				int totalArea = scrollArea.getHeight();
				int totalDraggableArea = totalArea - currentDraggerHeight;
				
				// Move dragger down by a small amount
				int newYPos = Math.min(totalDraggableArea, currentDraggerY + 5);
				
				scrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerTop, mainDraggerBottom, newYPos, itemCount);
			}
			else
			{
				// Release buttons if mouse is not pressed
				upButtonPressed.set(false);
				downButtonPressed.set(false);
			}
		});

		scrollArea.revalidate();
		scrollableContainer.revalidate();
	}

	/**
	 * Initialize dragger size based on content
	 */
	private void initializeDraggerSize(Widget scrollArea, Widget contentLayer, Widget mainDragger, Widget mainDraggerBottom, int totalRows)
	{
		if (content == null) return;
		
		int totalArea = scrollArea.getHeight();
		
		// Calculate content dimensions
		int totalContentHeight = totalRows * content.getItemHeight();
		int containerHeight = contentLayer.getHeight();
		
		// Calculate dynamic dragger size based on visible area ratio
		// If no content or content fits entirely, dragger fills the entire area
		float visibleRatio = (totalContentHeight == 0) ? 1.0f : Math.min(1.0f, (float) containerHeight / totalContentHeight);
		int dynamicDraggerHeight = Math.max(DRAGGER_HEIGHT, Math.round(totalArea * visibleRatio));

		// Update dragger size
		mainDragger.setOriginalHeight(dynamicDraggerHeight);
		mainDragger.revalidate();
		
		// Update dragger end positions
		mainDraggerBottom.setOriginalY(SCROLLBAR_PADDING + dynamicDraggerHeight);
		mainDraggerBottom.revalidate();
	}

	/**
	 * Handle scrollbar movement
	 */
	private void scrollbarMove(Widget scrollArea, Widget contentLayer, Widget mainDragger, Widget mainDraggerTop, Widget mainDraggerBottom, int yPosToMoveScrollbar, int itemCount)
	{
		if (content == null) return;
		
		int totalArea = scrollArea.getHeight();
		
		// Calculate content dimensions
		int totalRows = (int) Math.ceil(itemCount / (double) content.getColumns());
		int totalContentHeight = totalRows * content.getItemHeight();
		int containerHeight = contentLayer.getHeight();
		int overflowHeight = Math.max(0, totalContentHeight - containerHeight);
		
		// Recalculate dragger size based on current content
		float visibleRatio = Math.min(1.0f, (float) containerHeight / Math.max(1, totalContentHeight));
		int dynamicDraggerHeight = Math.max(DRAGGER_HEIGHT, Math.round(totalArea * visibleRatio));

		// Calculate the draggable area within the scroll area (accounting for current dragger height)
		int totalDraggableArea = totalArea - dynamicDraggerHeight;
		
		// Set dragger position relative to the scrollbar container
		int draggerAbsoluteY = SCROLLBAR_PADDING + yPosToMoveScrollbar;
		mainDragger.setOriginalY(draggerAbsoluteY);
		mainDragger.revalidate();
		mainDraggerTop.setOriginalY(draggerAbsoluteY);
		mainDraggerTop.revalidate();
		mainDraggerBottom.setOriginalY(draggerAbsoluteY + dynamicDraggerHeight);
		mainDraggerBottom.revalidate();
		
		// Calculate scroll ratio based on dragger position within the draggable area
		float scrollRatio = (float) yPosToMoveScrollbar / Math.max(1, totalDraggableArea);
		
		// Clamp scroll ratio to [0, 1]
		scrollRatio = Math.max(0, Math.min(1, scrollRatio));
		
		// Calculate scroll position - 0% scrollbar = 0% content, 100% scrollbar = 100% content
		int posToMoveContentLayer = Math.round(overflowHeight * scrollRatio);
		contentLayer.setScrollY(posToMoveContentLayer);
	}
}
