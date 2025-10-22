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
import net.runelite.api.KeyCode;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.widgets.ItemQuantityMode;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetSizeMode;
import net.runelite.api.widgets.WidgetType;
import net.runelite.api.widgets.JavaScriptCallback;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a complete draggable modal window
 * Handles background, borders, title bar, close button, and drag outline
 */
public class ModalDialog
{
	private final Widget modalWidget;
	private final Widget contentArea;
	private final Widget draggedBorderWidget;
	private final Widget dragger;

	// Drag state tracking
	private final AtomicInteger dragStartX = new AtomicInteger(0);
	private final AtomicInteger dragStartY = new AtomicInteger(0);
	private final AtomicInteger widgetStartX = new AtomicInteger(0);
	private final AtomicInteger widgetStartY = new AtomicInteger(0);
	
	// Position change callback
	private final JavaScriptCallback onPositionChange;

	public ModalDialog(Client client, Widget parentWidget, String title, int width, int height, int x, int y, JavaScriptCallback onClose)
	{
		this(client, parentWidget, title, width, height, x, y, onClose, null);
	}
	
	public ModalDialog(Client client, Widget parentWidget, String title, int width, int height, int x, int y, JavaScriptCallback onClose, JavaScriptCallback onPositionChange)
	{
		this.onPositionChange = onPositionChange;
		Widget guideFloater = WidgetFactory.createFloatLayer(parentWidget);

		// Create dragged border widget
		this.draggedBorderWidget = createDraggedOutline(guideFloater, x, y, width, height);

		// Create the main content layer first
		this.modalWidget = WidgetFactory.createLayer(guideFloater, x, y, width, height);
		modalWidget.setNoClickThrough(true);
		modalWidget.setHasListener(true);

		// Add ESC key handling to the entire modal
		// TODO: Consider having a general key listener for closing, rather than requiring widget to be focused
		modalWidget.setOnKeyListener((JavaScriptCallback) (ev) -> {
			if (client.isKeyPressed(KeyCode.KC_ESCAPE))
			{
				if (onClose != null)
				{
					onClose.run(ev);
				}
			}
		});
		modalWidget.revalidate();

		// Create the dragger widget
		dragger = createDragger(guideFloater, x, y, width);

		// Create modal background with borders
		createModalBackground(width, height);

		// Create draggable title
		WidgetFactory.createTitle(modalWidget, title);

		// Title separator border
		Widget titleSeparator = WidgetFactory.createGraphicAbsolute(modalWidget, SpriteID.SteelborderDivider._0, 0, 20, 10, 26);
		titleSeparator.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		titleSeparator.setWidthMode(WidgetSizeMode.MINUS);
		titleSeparator.revalidate();

		// Create close button
		WidgetFactory.createCloseButton(modalWidget, onClose);

		// Create content area (fills under header, above scrollbar area)
		this.contentArea = WidgetFactory.createContentContainer(modalWidget, 6, 35, 14, 42);
	}

	/**
	 * Get the content area where child widgets should be added
	 */
	public Widget getContentArea()
	{
		return contentArea;
	}

	/**
	 * Get the modal widget
	 */
	public Widget getModalWidget()
	{
		return modalWidget;
	}

	public void setPos(int x, int y)
	{
		modalWidget.setPos(x, y);
		modalWidget.revalidate();

		dragger.setPos(x, y);
		dragger.revalidate();
		draggedBorderWidget.setPos(x, y);
		draggedBorderWidget.revalidate();
	}

	/**
	 * Show the modal
	 */
	public void show()
	{
		modalWidget.setHidden(false);
		dragger.setHidden(false);
	}

	/**
	 * Hide the modal
	 */
	public void hide()
	{
		modalWidget.setHidden(true);
		dragger.setHidden(true);
	}

	/**
	 * Close and destroy the modal
	 */
	public void close()
	{
		modalWidget.getParent().deleteAllChildren();
	}

	/**
	 * Create the dragger widget for dragging the modal
	 */
	private Widget createDragger(Widget parent, int x, int y, int width)
	{
		final int TITLE_HEIGHT = 32;

		var dragger = parent.createChild(WidgetType.GRAPHIC);
		dragger.setPos(x, y);
		dragger.setOriginalWidth(width);
		dragger.setOriginalHeight(TITLE_HEIGHT);
		dragger.setSpriteId(SpriteID.TRADEBACKING_LIGHT);
		dragger.setOpacity(255);
		dragger.setItemQuantityMode(ItemQuantityMode.STACKABLE);
		dragger.setSpriteTiling(true);
		dragger.setHasListener(true);
		dragger.setDragDeadZone(1); // Minimum 1 pixel movement before drag starts
		dragger.setDragDeadTime(5); // 5 game ticks delay before drag begins
		dragger.setDragParent(parent); // Make this widget the drag handle for itself

		dragger.setOnMouseOverListener((JavaScriptCallback) (ev) -> {
			dragger.setOpacity(200);
		});

		dragger.setOnMouseLeaveListener((JavaScriptCallback) (ev) -> {
			dragger.setOpacity(255);
		});

		dragger.setOnClickListener((JavaScriptCallback) (ev) -> {
			dragger.setOpacity(0);
			dragStartX.set(ev.getMouseX());
			dragStartY.set(ev.getMouseY());
			widgetStartX.set(dragger.getOriginalX());
			widgetStartY.set(dragger.getOriginalY());
		});

		dragger.setOnHoldListener((JavaScriptCallback) (ev) -> {
			dragger.setOpacity(0);
		});

		// Set up drag listeners
		dragger.setOnDragListener((JavaScriptCallback) (ev) -> {
			// Calculate new position based on mouse movement
			int deltaX = ev.getMouseX() - dragStartX.get();
			int deltaY = ev.getMouseY() - dragStartY.get();
			int newX = widgetStartX.get() + deltaX;
			int newY = widgetStartY.get() + deltaY;

			// Ensure widget stays within bounds
			int maxX = parent.getWidth() - modalWidget.getWidth();
			int maxY = parent.getHeight() - modalWidget.getHeight();
			newX = Math.max(0, Math.min(newX, maxX));
			newY = Math.max(0, Math.min(newY, maxY));

			// Update both dragger and modalWidget positions
			dragger.setPos(newX, newY);
			dragger.revalidate();

			// Hide main widget and show border during drag
			modalWidget.setHidden(true);
			modalWidget.setPos(newX, newY);
			modalWidget.revalidate();

			draggedBorderWidget.setHidden(false);
			draggedBorderWidget.setPos(newX, newY);
			draggedBorderWidget.revalidate();
		});

		dragger.setOnDragCompleteListener((JavaScriptCallback) (ev) -> {
			// End of drag
			dragger.setOpacity(250);
			dragger.revalidate();

			modalWidget.setHidden(false);
			modalWidget.revalidate();

			draggedBorderWidget.setHidden(true);
			draggedBorderWidget.revalidate();
			
			// Notify about position change
			if (onPositionChange != null)
			{
				onPositionChange.run(ev);
			}
		});

		dragger.revalidate();

		return dragger;
	}

	/**
	 * Create dragged outline widget
	 */
	private Widget createDraggedOutline(Widget parent, int x, int y, int width, int height)
	{
		Widget borderLayer = WidgetFactory.createLayer(parent, x, y, width, height);
		borderLayer.revalidate();

		addLayerToBorder(borderLayer, 0);
		addLayerToBorder(borderLayer, 1);
		addLayerToBorder(borderLayer, 2);
		addLayerToBorder(borderLayer, 3);

		// Initially hidden
		borderLayer.setHidden(true);

		return borderLayer;
	}

	/**
	 * Add a border layer
	 */
	private void addLayerToBorder(Widget borderLayer, int depth)
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
	 * Create modal background with borders
	 */
	private void createModalBackground(int width, int height)
	{
		// Main background
		Widget background = WidgetFactory.createGraphicAbsolute(modalWidget, SpriteID.TRADEBACKING, 0, 0, 0, 0);
		background.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		background.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		background.setWidthMode(WidgetSizeMode.MINUS);
		background.setHeightMode(WidgetSizeMode.MINUS);
		background.setHasListener(true);
		background.setOnClickListener((JavaScriptCallback) (ev) -> {
		}); // Prevent clicks from passing through
		background.revalidate();

		// Corner borders
		createCornerBorders(width, height);

		// Side borders
		createSideBorders(width, height);
	}

	/**
	 * Create corner borders for a modal
	 */
	private void createCornerBorders(int width, int height)
	{
		// Top-left corner
		WidgetFactory.createGraphicAbsolute(modalWidget, SpriteID.Steelborder._0, 0, 0, 25, 30);

		// Top-right corner
		Widget topRight = WidgetFactory.createGraphicAbsolute(modalWidget, SpriteID.Steelborder._1, 0, 0, 25, 30);
		topRight.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
		topRight.revalidate();

		// Bottom-left corner
		Widget bottomLeft = WidgetFactory.createGraphicAbsolute(modalWidget, SpriteID.Steelborder._2, 0, 0, 25, 30);
		bottomLeft.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
		bottomLeft.revalidate();

		// Bottom-right corner
		Widget bottomRight = WidgetFactory.createGraphicAbsolute(modalWidget, SpriteID.Steelborder._3, 0, 0, 25, 30);
		bottomRight.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
		bottomRight.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
		bottomRight.revalidate();
	}

	/**
	 * Create side borders for a modal
	 */
	private void createSideBorders(int width, int height)
	{
		// Left border
		Widget leftBorder = WidgetFactory.createGraphicAbsolute(modalWidget, SpriteID.Miscgraphics._2, -15, 0, 36, 36);
		leftBorder.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		leftBorder.setHeightMode(WidgetSizeMode.MINUS);
		leftBorder.revalidate();

		// Right border
		Widget rightBorder = WidgetFactory.createGraphicAbsolute(modalWidget, SpriteID.Steelborder2._1, -15, 0, 36, 36);
		rightBorder.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
		rightBorder.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		rightBorder.setHeightMode(WidgetSizeMode.MINUS);
		rightBorder.revalidate();

		// Top border
		Widget topBorder = WidgetFactory.createGraphicAbsolute(modalWidget, SpriteID.Steelborder2._0, 0, -15, 36, 36);
		topBorder.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		topBorder.setWidthMode(WidgetSizeMode.MINUS);
		topBorder.revalidate();

		// Bottom border
		Widget bottomBorder = WidgetFactory.createGraphicAbsolute(modalWidget, SpriteID.Miscgraphics._3, 0, -15, 36, 36);
		bottomBorder.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		bottomBorder.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
		bottomBorder.setWidthMode(WidgetSizeMode.MINUS);
		bottomBorder.revalidate();
	}
}
