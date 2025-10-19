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
 * Generic container with configurable scrollbar functionality (horizontal, vertical, or both)
 * Encapsulates scrollbar logic and provides a content layer for child widgets
 * Can be used with any type of content that implements ScrollableContent
 */
public class UnifiedScrollableContainer
{
	public enum ScrollDirection
	{
		HORIZONTAL,
		VERTICAL,
		BOTH
	}

	// Scrollbar constants
	private static final int SCROLLBAR_SIZE = 20;
	private static final int DRAGGER_SIZE = 20;
	private static final int SCROLLBAR_PADDING = 16;
	private static final int DRAGGER_END_SIZE = 5;

	private final Widget container;
	private final Widget contentLayer;
	private final Widget horizontalScrollbar;
	private final Widget verticalScrollbar;
	private final Client client;
	private ScrollableContent content;

	// Horizontal scrollbar interaction state
	private final AtomicBoolean hClicked = new AtomicBoolean(false);
	private final AtomicInteger hClickOffset = new AtomicInteger(0);
	private final AtomicBoolean leftButtonPressed = new AtomicBoolean(false);
	private final AtomicBoolean rightButtonPressed = new AtomicBoolean(false);

	// Vertical scrollbar interaction state
	private final AtomicBoolean vClicked = new AtomicBoolean(false);
	private final AtomicInteger vClickOffset = new AtomicInteger(0);
	private final AtomicBoolean upButtonPressed = new AtomicBoolean(false);
	private final AtomicBoolean downButtonPressed = new AtomicBoolean(false);

	public UnifiedScrollableContainer(Widget parent, int x, int y, int width, int height, Client client, ScrollDirection scrollDirection)
	{
		this.client = client;
		
		// Create container for the entire scrollable section
		this.container = WidgetFactory.createLayer(parent, x, y, width, height);
		this.container.setWidthMode(WidgetSizeMode.MINUS);
		this.container.revalidate();
		
		// Calculate content layer dimensions based on scroll direction
		int contentWidth = width;
		int contentHeight = height;
		
		if (scrollDirection == ScrollDirection.HORIZONTAL || scrollDirection == ScrollDirection.BOTH)
		{
			contentHeight -= SCROLLBAR_SIZE + 5; // Leave space for horizontal scrollbar
		}
		
		if (scrollDirection == ScrollDirection.VERTICAL || scrollDirection == ScrollDirection.BOTH)
		{
			contentWidth -= SCROLLBAR_SIZE + 5; // Leave space for vertical scrollbar
		}
		
		// Create content layer within the container
		this.contentLayer = WidgetFactory.createButtonLayer(this.container, 0, 0, contentWidth, contentHeight);
		if (scrollDirection == ScrollDirection.HORIZONTAL || scrollDirection == ScrollDirection.BOTH)
		{
			this.contentLayer.setHeightMode(WidgetSizeMode.MINUS);
		}
		if (scrollDirection == ScrollDirection.VERTICAL || scrollDirection == ScrollDirection.BOTH)
		{
			this.contentLayer.setWidthMode(WidgetSizeMode.MINUS);
		}
		this.contentLayer.revalidate();

		// Create border
		var border = container.createChild(-1, WidgetType.RECTANGLE);
		border.setTextColor(Integer.parseInt("111111", 16));
		border.setOpacity(100);
		border.setWidthMode(WidgetSizeMode.MINUS);
		border.setHeightMode(WidgetSizeMode.MINUS);
		border.revalidate();
		
		// Create scrollbars based on direction
		if (scrollDirection == ScrollDirection.HORIZONTAL || scrollDirection == ScrollDirection.BOTH)
		{
			this.horizontalScrollbar = createHorizontalScrollbar(this.container, this.contentLayer, SCROLLBAR_SIZE);
			this.horizontalScrollbar.revalidate();
			this.horizontalScrollbar.setHidden(true);
		}
		else
		{
			this.horizontalScrollbar = null;
		}
		
		if (scrollDirection == ScrollDirection.VERTICAL || scrollDirection == ScrollDirection.BOTH)
		{
			this.verticalScrollbar = createVerticalScrollbar(this.container, this.contentLayer, SCROLLBAR_SIZE);
			this.verticalScrollbar.revalidate();
			this.verticalScrollbar.setHidden(true);
		}
		else
		{
			this.verticalScrollbar = null;
		}
	}

	/**
	 * Set the content that this container will scroll
	 */
	public void setContent(ScrollableContent content)
	{
		this.content = content;
		updateScrollbars();
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
	public void updateScrollbars()
	{
		if (content == null) return;
		
		// Update horizontal scrollbar if present
		if (horizontalScrollbar != null)
		{
			updateHorizontalScrollbar();
		}
		
		// Update vertical scrollbar if present
		if (verticalScrollbar != null)
		{
			updateVerticalScrollbar();
		}
	}

	private void updateHorizontalScrollbar()
	{
		// Calculate if we need to show the horizontal scrollbar
		int itemCount = content.getItemCount();
		int totalColumns = (int) Math.ceil(itemCount / (double) content.getRows());
		int totalContentWidth = totalColumns * content.getItemWidth();
		int containerWidth = contentLayer.getWidth();
		
		boolean shouldShow = totalContentWidth > containerWidth || totalContentWidth == 0;
		horizontalScrollbar.setHidden(!shouldShow);
		
		if (shouldShow && horizontalScrollbar.getDynamicChildren() != null)
		{
			Widget scrollArea = null;
			Widget mainDragger = null;
			Widget mainDraggerLeft = null;
			Widget mainDraggerRight = null;
			
			for (Widget child : horizontalScrollbar.getDynamicChildren())
			{
				if (child.getSpriteId() == SpriteID.ScrollbarDraggerHorizontalV2._3)
				{
					scrollArea = child;
				}
				else if (child.getSpriteId() == SpriteID.ScrollbarDraggerHorizontalV2._1)
				{
					mainDragger = child;
				}
				else if (child.getSpriteId() == SpriteID.ScrollbarDraggerHorizontalV2._2)
				{
					mainDraggerLeft = child;
				}
				else if (child.getSpriteId() == SpriteID.ScrollbarDraggerHorizontalV2._0)
				{
					mainDraggerRight = child;
				}
			}
			
			if (scrollArea != null && mainDragger != null && mainDraggerLeft != null && mainDraggerRight != null)
			{
				initializeHorizontalDraggerSize(scrollArea, contentLayer, mainDragger, mainDraggerRight, totalColumns);
			}
		}
	}

	private void updateVerticalScrollbar()
	{
		// Calculate if we need to show the vertical scrollbar
		int itemCount = content.getItemCount();
		int totalRows = (int) Math.ceil(itemCount / (double) content.getColumns());
		int totalContentHeight = totalRows * content.getItemHeight();
		int containerHeight = contentLayer.getHeight();
		
		boolean shouldShow = totalContentHeight > containerHeight || totalContentHeight == 0;
		verticalScrollbar.setHidden(!shouldShow);
		
		if (shouldShow && verticalScrollbar.getDynamicChildren() != null)
		{
			Widget scrollArea = null;
			Widget mainDragger = null;
			Widget mainDraggerTop = null;
			Widget mainDraggerBottom = null;
			
			for (Widget child : verticalScrollbar.getDynamicChildren())
			{
				if (child.getSpriteId() == SpriteID.ScrollbarDraggerV2._3)
				{
					scrollArea = child;
				}
				else if (child.getSpriteId() == SpriteID.ScrollbarDraggerV2._1)
				{
					mainDragger = child;
				}
				else if (child.getSpriteId() == SpriteID.ScrollbarDraggerV2._2)
				{
					mainDraggerTop = child;
				}
				else if (child.getSpriteId() == SpriteID.ScrollbarDraggerV2._0)
				{
					mainDraggerBottom = child;
				}
			}
			
			if (scrollArea != null && mainDragger != null && mainDraggerTop != null && mainDraggerBottom != null)
			{
				initializeVerticalDraggerSize(scrollArea, contentLayer, mainDragger, mainDraggerBottom, totalRows);
			}
		}
	}

	/**
	 * Create a horizontal scrollbar for content layers
	 */
	private Widget createHorizontalScrollbar(Widget parent, Widget scrollableContainer, int height)
	{
		Widget scrollbar = WidgetFactory.createLayer(parent, 0, 0, 0, height);
		scrollbar.setWidthMode(WidgetSizeMode.MINUS);
		scrollbar.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
		scrollbar.revalidate();

		Widget scrollArea = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarDraggerHorizontalV2._3, 0, 0, SCROLLBAR_PADDING * 2, SCROLLBAR_SIZE);
		scrollArea.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		scrollArea.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		scrollArea.setWidthMode(WidgetSizeMode.MINUS);
		scrollArea.setNoClickThrough(true);
		scrollArea.setHasListener(true);
		scrollArea.revalidate();

		Widget mainDragger = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarDraggerHorizontalV2._1, SCROLLBAR_PADDING, 0, DRAGGER_SIZE, SCROLLBAR_SIZE);
		mainDragger.setDragParent(scrollArea);
		mainDragger.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		mainDragger.revalidate();

		Widget mainDraggerLeft = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarDraggerHorizontalV2._2, SCROLLBAR_PADDING, 0, DRAGGER_END_SIZE, SCROLLBAR_SIZE);
		mainDraggerLeft.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		mainDraggerLeft.revalidate();

		Widget mainDraggerRight = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarDraggerHorizontalV2._0, DRAGGER_SIZE + SCROLLBAR_PADDING, 0, DRAGGER_END_SIZE, SCROLLBAR_SIZE);
		mainDraggerRight.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		mainDraggerRight.revalidate();

		Widget leftScroll = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarV2._2, 0, 0, SCROLLBAR_PADDING, SCROLLBAR_SIZE);
		leftScroll.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		leftScroll.setHasListener(true);
		leftScroll.revalidate();

		Widget rightScroll = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarV2._3, 0, 0, SCROLLBAR_PADDING, SCROLLBAR_SIZE);
		rightScroll.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		rightScroll.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
		rightScroll.setHasListener(true);
		rightScroll.revalidate();

		setupHorizontalScrollbarInteraction(scrollArea, scrollableContainer, mainDragger, mainDraggerLeft, mainDraggerRight, leftScroll, rightScroll);
		initializeHorizontalDraggerSize(scrollArea, scrollableContainer, mainDragger, mainDraggerRight, 0);

		return scrollbar;
	}

	/**
	 * Create a vertical scrollbar for content layers
	 */
	private Widget createVerticalScrollbar(Widget parent, Widget scrollableContainer, int width)
	{
		Widget scrollbar = WidgetFactory.createLayer(parent, 0, 0, width, 0);
		scrollbar.setHeightMode(WidgetSizeMode.MINUS);
		scrollbar.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
		scrollbar.revalidate();

		Widget scrollArea = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarDraggerV2._3, 0, 0, SCROLLBAR_SIZE, SCROLLBAR_PADDING * 2);
		scrollArea.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		scrollArea.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		scrollArea.setHeightMode(WidgetSizeMode.MINUS);
		scrollArea.setNoClickThrough(true);
		scrollArea.setHasListener(true);
		scrollArea.revalidate();

		Widget mainDragger = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarDraggerV2._1, 0, SCROLLBAR_PADDING, SCROLLBAR_SIZE, DRAGGER_SIZE);
		mainDragger.setDragParent(scrollArea);
		mainDragger.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		mainDragger.revalidate();

		Widget mainDraggerTop = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarDraggerV2._2, 0, SCROLLBAR_PADDING, SCROLLBAR_SIZE, DRAGGER_END_SIZE);
		mainDraggerTop.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		mainDraggerTop.revalidate();

		Widget mainDraggerBottom = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarDraggerV2._0, 0, DRAGGER_SIZE + SCROLLBAR_PADDING, SCROLLBAR_SIZE, DRAGGER_END_SIZE);
		mainDraggerBottom.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		mainDraggerBottom.revalidate();

		Widget upScroll = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarV2._0, 0, 0, SCROLLBAR_SIZE, SCROLLBAR_PADDING);
		upScroll.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		upScroll.setHasListener(true);
		upScroll.revalidate();

		Widget downScroll = WidgetFactory.createGraphicAbsolute(scrollbar, SpriteID.ScrollbarV2._1, 0, 0, SCROLLBAR_SIZE, SCROLLBAR_PADDING);
		downScroll.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		downScroll.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
		downScroll.setHasListener(true);
		downScroll.revalidate();

		setupVerticalScrollbarInteraction(scrollArea, scrollableContainer, mainDragger, mainDraggerTop, mainDraggerBottom, upScroll, downScroll);
		initializeVerticalDraggerSize(scrollArea, scrollableContainer, mainDragger, mainDraggerBottom, 0);

		return scrollbar;
	}

	/**
	 * Setup horizontal scrollbar interaction logic
	 */
	private void setupHorizontalScrollbarInteraction(Widget scrollArea, Widget scrollableContainer, Widget mainDragger, Widget mainDraggerLeft, Widget mainDraggerRight, Widget leftScroll, Widget rightScroll)
	{
		// Click handling
		scrollArea.setOnClickListener((JavaScriptCallback) (ev) -> {
			hClicked.set(true);
			
			int mouseX = client.getMouseCanvasPosition().getX();
			int draggerX = mainDragger.getCanvasLocation().getX();
			int draggerWidth = mainDragger.getOriginalWidth();
			
			if (mouseX < draggerX || mouseX > draggerX + draggerWidth) {
				hClickOffset.set(draggerWidth / 2);
			} else {
				hClickOffset.set(mouseX - draggerX);
			}
		});

		// Scroll wheel handling
		scrollArea.setOnScrollWheelListener((JavaScriptCallback) (ev) -> {
			if (content == null) return;
			int itemCount = content.getItemCount();
			int currentDraggerX = mainDragger.getOriginalX() - SCROLLBAR_PADDING;
			int xPos = currentDraggerX + (10 * ev.getMouseY());
			int totalArea = scrollArea.getWidth();
			int currentDraggerWidth = mainDragger.getOriginalWidth();
			
			if (xPos > totalArea - currentDraggerWidth) xPos = totalArea - currentDraggerWidth;
			if (xPos < 0) xPos = 0;

			horizontalScrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerLeft, mainDraggerRight, xPos, itemCount);
		});

		// Add scroll listener to scrollable container
		scrollableContainer.setOnScrollWheelListener((JavaScriptCallback) (ev) -> {
			if (content == null) return;
			int itemCount = content.getItemCount();
			int currentDraggerX = mainDragger.getOriginalX() - SCROLLBAR_PADDING;
			int xPos = currentDraggerX + (10 * ev.getMouseY());
			int totalArea = scrollArea.getWidth();
			int currentDraggerWidth = mainDragger.getOriginalWidth();
			
			if (xPos > totalArea - currentDraggerWidth) xPos = totalArea - currentDraggerWidth;
			if (xPos < 0) xPos = 0;

			horizontalScrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerLeft, mainDraggerRight, xPos, itemCount);
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
			
			// Handle dragger dragging
			if (hClicked.get())
			{
				if (client.getMouseCurrentButton() != 1)
				{
					hClicked.set(false);
					return;
				}
				int itemCount = content.getItemCount();
				int currentDraggerWidth = mainDragger.getOriginalWidth();
				int totalArea = scrollArea.getWidth();

				int mouseX = client.getMouseCanvasPosition().getX();
				int scrollStartX = scrollArea.getCanvasLocation().getX();
				int mouseRelativeToScrollArea = mouseX - scrollStartX;
				
				int xPosClicked = mouseRelativeToScrollArea - hClickOffset.get();
				
				if (xPosClicked > totalArea - currentDraggerWidth) xPosClicked = totalArea - currentDraggerWidth;
				if (xPosClicked < 0) xPosClicked = 0;

				horizontalScrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerLeft, mainDraggerRight, xPosClicked, itemCount);
			}
			
			// Handle scroll button pressing
			if (leftButtonPressed.get() && client.getMouseCurrentButton() == 1)
			{
				int itemCount = content.getItemCount();
				int currentDraggerX = mainDragger.getOriginalX() - SCROLLBAR_PADDING;
				int newXPos = Math.max(0, currentDraggerX - 5);
				horizontalScrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerLeft, mainDraggerRight, newXPos, itemCount);
			}
			else if (rightButtonPressed.get() && client.getMouseCurrentButton() == 1)
			{
				int itemCount = content.getItemCount();
				int currentDraggerX = mainDragger.getOriginalX() - SCROLLBAR_PADDING;
				int currentDraggerWidth = mainDragger.getOriginalWidth();
				int totalArea = scrollArea.getWidth();
				int totalDraggableArea = totalArea - currentDraggerWidth;
				int newXPos = Math.min(totalDraggableArea, currentDraggerX + 5);
				horizontalScrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerLeft, mainDraggerRight, newXPos, itemCount);
			}
			else
			{
				leftButtonPressed.set(false);
				rightButtonPressed.set(false);
			}
		});

		scrollArea.revalidate();
		scrollableContainer.revalidate();
	}

	/**
	 * Setup vertical scrollbar interaction logic
	 */
	private void setupVerticalScrollbarInteraction(Widget scrollArea, Widget scrollableContainer, Widget mainDragger, Widget mainDraggerTop, Widget mainDraggerBottom, Widget upScroll, Widget downScroll)
	{
		// Click handling
		scrollArea.setOnClickListener((JavaScriptCallback) (ev) -> {
			vClicked.set(true);
			
			int mouseY = client.getMouseCanvasPosition().getY();
			int draggerY = mainDragger.getCanvasLocation().getY();
			int draggerHeight = mainDragger.getOriginalHeight();
			
			if (mouseY < draggerY || mouseY > draggerY + draggerHeight) {
				vClickOffset.set(draggerHeight / 2);
			} else {
				vClickOffset.set(mouseY - draggerY);
			}
		});

		// Scroll wheel handling
		scrollArea.setOnScrollWheelListener((JavaScriptCallback) (ev) -> {
			if (content == null) return;
			int itemCount = content.getItemCount();
			int currentDraggerY = mainDragger.getOriginalY() - SCROLLBAR_PADDING;
			int yPos = currentDraggerY + (10 * ev.getMouseY());
			int totalArea = scrollArea.getHeight();
			int currentDraggerHeight = mainDragger.getOriginalHeight();
			
			if (yPos > totalArea - currentDraggerHeight) yPos = totalArea - currentDraggerHeight;
			if (yPos < 0) yPos = 0;

			verticalScrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerTop, mainDraggerBottom, yPos, itemCount);
		});

		// Add scroll listener to scrollable container
		scrollableContainer.setOnScrollWheelListener((JavaScriptCallback) (ev) -> {
			if (content == null) return;
			int itemCount = content.getItemCount();
			int currentDraggerY = mainDragger.getOriginalY() - SCROLLBAR_PADDING;
			int yPos = currentDraggerY + (10 * ev.getMouseY());
			int totalArea = scrollArea.getHeight();
			int currentDraggerHeight = mainDragger.getOriginalHeight();
			
			if (yPos > totalArea - currentDraggerHeight) yPos = totalArea - currentDraggerHeight;
			if (yPos < 0) yPos = 0;

			verticalScrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerTop, mainDraggerBottom, yPos, itemCount);
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
			
			// Handle dragger dragging
			if (vClicked.get())
			{
				if (client.getMouseCurrentButton() != 1)
				{
					vClicked.set(false);
					return;
				}
				int itemCount = content.getItemCount();
				int currentDraggerHeight = mainDragger.getOriginalHeight();
				int totalArea = scrollArea.getHeight();

				int mouseY = client.getMouseCanvasPosition().getY();
				int scrollStartY = scrollArea.getCanvasLocation().getY();
				int mouseRelativeToScrollArea = mouseY - scrollStartY;
				
				int yPosClicked = mouseRelativeToScrollArea - vClickOffset.get();
				
				if (yPosClicked > totalArea - currentDraggerHeight) yPosClicked = totalArea - currentDraggerHeight;
				if (yPosClicked < 0) yPosClicked = 0;

				verticalScrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerTop, mainDraggerBottom, yPosClicked, itemCount);
			}
			
			// Handle scroll button pressing
			if (upButtonPressed.get() && client.getMouseCurrentButton() == 1)
			{
				int itemCount = content.getItemCount();
				int currentDraggerY = mainDragger.getOriginalY() - SCROLLBAR_PADDING;
				int newYPos = Math.max(0, currentDraggerY - 5);
				verticalScrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerTop, mainDraggerBottom, newYPos, itemCount);
			}
			else if (downButtonPressed.get() && client.getMouseCurrentButton() == 1)
			{
				int itemCount = content.getItemCount();
				int currentDraggerY = mainDragger.getOriginalY() - SCROLLBAR_PADDING;
				int currentDraggerHeight = mainDragger.getOriginalHeight();
				int totalArea = scrollArea.getHeight();
				int totalDraggableArea = totalArea - currentDraggerHeight;
				int newYPos = Math.min(totalDraggableArea, currentDraggerY + 5);
				verticalScrollbarMove(scrollArea, scrollableContainer, mainDragger, mainDraggerTop, mainDraggerBottom, newYPos, itemCount);
			}
			else
			{
				upButtonPressed.set(false);
				downButtonPressed.set(false);
			}
		});

		scrollArea.revalidate();
		scrollableContainer.revalidate();
	}

	/**
	 * Initialize horizontal dragger size based on content
	 */
	private void initializeHorizontalDraggerSize(Widget scrollArea, Widget contentLayer, Widget mainDragger, Widget mainDraggerRight, int totalColumns)
	{
		if (content == null) return;
		
		int totalArea = scrollArea.getWidth();
		int totalContentWidth = totalColumns * content.getItemWidth();
		int containerWidth = contentLayer.getWidth();
		
		float visibleRatio = (totalContentWidth == 0) ? 1.0f : Math.min(1.0f, (float) containerWidth / totalContentWidth);
		int dynamicDraggerWidth = Math.max(DRAGGER_SIZE, Math.round(totalArea * visibleRatio));

		mainDragger.setOriginalWidth(dynamicDraggerWidth);
		mainDragger.revalidate();
		
		mainDraggerRight.setOriginalX(SCROLLBAR_PADDING + dynamicDraggerWidth);
		mainDraggerRight.revalidate();
	}

	/**
	 * Initialize vertical dragger size based on content
	 */
	private void initializeVerticalDraggerSize(Widget scrollArea, Widget contentLayer, Widget mainDragger, Widget mainDraggerBottom, int totalRows)
	{
		if (content == null) return;
		
		int totalArea = scrollArea.getHeight();
		int totalContentHeight = totalRows * content.getItemHeight();
		int containerHeight = contentLayer.getHeight();
		
		float visibleRatio = (totalContentHeight == 0) ? 1.0f : Math.min(1.0f, (float) containerHeight / totalContentHeight);
		int dynamicDraggerHeight = Math.max(DRAGGER_SIZE, Math.round(totalArea * visibleRatio));

		mainDragger.setOriginalHeight(dynamicDraggerHeight);
		mainDragger.revalidate();
		
		mainDraggerBottom.setOriginalY(SCROLLBAR_PADDING + dynamicDraggerHeight);
		mainDraggerBottom.revalidate();
	}

	/**
	 * Handle horizontal scrollbar movement
	 */
	private void horizontalScrollbarMove(Widget scrollArea, Widget contentLayer, Widget mainDragger, Widget mainDraggerLeft, Widget mainDraggerRight, int xPosToMoveScrollbar, int itemCount)
	{
		if (content == null) return;
		
		int totalArea = scrollArea.getWidth();
		int totalColumns = (int) Math.ceil(itemCount / (double) content.getRows());
		int totalContentWidth = totalColumns * content.getItemWidth();
		int containerWidth = contentLayer.getWidth();
		int overflowWidth = Math.max(0, totalContentWidth - containerWidth);
		
		float visibleRatio = Math.min(1.0f, (float) containerWidth / Math.max(1, totalContentWidth));
		int dynamicDraggerWidth = Math.max(DRAGGER_SIZE, Math.round(totalArea * visibleRatio));

		int totalDraggableArea = totalArea - dynamicDraggerWidth;
		
		int draggerAbsoluteX = SCROLLBAR_PADDING + xPosToMoveScrollbar;
		mainDragger.setOriginalX(draggerAbsoluteX);
		mainDragger.revalidate();
		mainDraggerLeft.setOriginalX(draggerAbsoluteX);
		mainDraggerLeft.revalidate();
		mainDraggerRight.setOriginalX(draggerAbsoluteX + dynamicDraggerWidth);
		mainDraggerRight.revalidate();
		
		float scrollRatio = (float) xPosToMoveScrollbar / Math.max(1, totalDraggableArea);
		scrollRatio = Math.max(0, Math.min(1, scrollRatio));
		
		int posToMoveContentLayer = Math.round(overflowWidth * scrollRatio);
		contentLayer.setScrollX(posToMoveContentLayer);
	}

	/**
	 * Handle vertical scrollbar movement
	 */
	private void verticalScrollbarMove(Widget scrollArea, Widget contentLayer, Widget mainDragger, Widget mainDraggerTop, Widget mainDraggerBottom, int yPosToMoveScrollbar, int itemCount)
	{
		if (content == null) return;
		
		int totalArea = scrollArea.getHeight();
		int totalRows = (int) Math.ceil(itemCount / (double) content.getColumns());
		int totalContentHeight = totalRows * content.getItemHeight();
		int containerHeight = contentLayer.getHeight();
		int overflowHeight = Math.max(0, totalContentHeight - containerHeight);
		
		float visibleRatio = Math.min(1.0f, (float) containerHeight / Math.max(1, totalContentHeight));
		int dynamicDraggerHeight = Math.max(DRAGGER_SIZE, Math.round(totalArea * visibleRatio));

		int totalDraggableArea = totalArea - dynamicDraggerHeight;
		
		int draggerAbsoluteY = SCROLLBAR_PADDING + yPosToMoveScrollbar;
		mainDragger.setOriginalY(draggerAbsoluteY);
		mainDragger.revalidate();
		mainDraggerTop.setOriginalY(draggerAbsoluteY);
		mainDraggerTop.revalidate();
		mainDraggerBottom.setOriginalY(draggerAbsoluteY + dynamicDraggerHeight);
		mainDraggerBottom.revalidate();
		
		float scrollRatio = (float) yPosToMoveScrollbar / Math.max(1, totalDraggableArea);
		scrollRatio = Math.max(0, Math.min(1, scrollRatio));
		
		int posToMoveContentLayer = Math.round(overflowHeight * scrollRatio);
		contentLayer.setScrollY(posToMoveContentLayer);
	}
}
