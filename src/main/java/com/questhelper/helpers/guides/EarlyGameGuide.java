package com.questhelper.helpers.guides;

import net.runelite.api.Client;
import net.runelite.api.FontID;
import net.runelite.api.KeyCode;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetSizeMode;
import net.runelite.api.widgets.WidgetTextAlignment;
import net.runelite.api.widgets.WidgetType;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

@Singleton
public class EarlyGameGuide
{
	Widget babyWidget;

	final int BUTTONS_PER_COLUMN = 3;
	final int BUTTON_LAYER_WIDTH = 460;
	final int BUTTON_AREA_HEIGHT = 90;

	final int BUTTON_BACKGROUND_WIDTH = 95;
	final int BUTTON_BACKGROUND_HEIGHT = 71;
	final int BUTTON_PADDING = 4;
	final int BUTTON_WIDTH = 90;
	final int BUTTON_HEIGHT = 60;
	final int BUTTON_EDGE_SIZE = 9;
	final int ICON_SIZE = 24;

	// Scrollbar
	final int SCROLLBAR_HEIGHT = 20;
	final int DRAGGER_WIDTH = 20;
	final int PADDING = 16;
	final int DRAGGER_END_WIDTH = 5;

	public void setup(Client client)
	{
		close(client);
		Widget parentWidget = getTopLevelWidget(client);
		if (parentWidget == null) return;

		Widget topLevelWidget = parentWidget.createChild(-1, WidgetType.LAYER);
		topLevelWidget.setSize(480, 326);
		topLevelWidget.setPos(0, 0, WidgetPositionMode.ABSOLUTE_CENTER, WidgetPositionMode.ABSOLUTE_CENTER);
		topLevelWidget.revalidate();

		// Background
		Widget backgroundWidget = topLevelWidget.createChild(-1, WidgetType.GRAPHIC);
		backgroundWidget.setNoClickThrough(true);
		backgroundWidget.setHasListener(true);
		backgroundWidget.setOnClickListener((JavaScriptCallback) (ev) -> {});
		backgroundWidget.setSpriteId(SpriteID.TRADEBACKING);
		backgroundWidget.setOriginalWidth(512);
		backgroundWidget.setOriginalHeight(334);
		backgroundWidget.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		backgroundWidget.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		backgroundWidget.revalidate();

		// Title
		Widget titleWidget = topLevelWidget.createChild(-1, WidgetType.TEXT);
		titleWidget.setText("Early game helper");
		titleWidget.setFontId(496);
		titleWidget.setTextColor(Integer.parseInt("ff981f", 16));
		titleWidget.setTextShadowed(true);
		titleWidget.setXTextAlignment(WidgetTextAlignment.CENTER);
		titleWidget.setYTextAlignment(WidgetTextAlignment.CENTER);
		titleWidget.setOriginalY(6);
		titleWidget.setOriginalWidth(12);
		titleWidget.setOriginalHeight(24);
		titleWidget.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		titleWidget.setWidthMode(WidgetSizeMode.MINUS);
		titleWidget.revalidate();

		// Top-left corner border
		Widget topLeftCorner = topLevelWidget.createChild(-1, WidgetType.GRAPHIC);
		topLeftCorner.setSpriteId(SpriteID.Steelborder._0);
		topLeftCorner.setOriginalWidth(25);
		topLeftCorner.setOriginalHeight(30);
		topLeftCorner.revalidate();

		// Top-right corner border
		Widget topRightCorner = topLevelWidget.createChild(-1, WidgetType.GRAPHIC);
		topRightCorner.setSpriteId(SpriteID.Steelborder._1);
		topRightCorner.setOriginalWidth(25);
		topRightCorner.setOriginalHeight(30);
		topRightCorner.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
		topRightCorner.revalidate();

		// Bottom-left corner border
		Widget bottomLeftCorner = topLevelWidget.createChild(-1, WidgetType.GRAPHIC);
		bottomLeftCorner.setSpriteId(SpriteID.Steelborder._2);
		bottomLeftCorner.setOriginalWidth(25);
		bottomLeftCorner.setOriginalHeight(30);
		bottomLeftCorner.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
		bottomLeftCorner.revalidate();

		// Bottom-left corner border
		Widget bottomRightCorner = topLevelWidget.createChild(-1, WidgetType.GRAPHIC);
		bottomRightCorner.setSpriteId(SpriteID.Steelborder._3);
		bottomRightCorner.setOriginalWidth(25);
		bottomRightCorner.setOriginalHeight(30);
		bottomRightCorner.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
		bottomRightCorner.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
		bottomRightCorner.revalidate();

		// Left border
		Widget leftBorder = topLevelWidget.createChild(-1, WidgetType.GRAPHIC);
		leftBorder.setSpriteId(SpriteID.Miscgraphics._2);
		leftBorder.setOriginalWidth(36);
		leftBorder.setOriginalHeight(60);
		leftBorder.setOriginalX(-15);
		leftBorder.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		leftBorder.setHeightMode(WidgetSizeMode.MINUS);
		leftBorder.revalidate();

		// Right border
		Widget rightBorder = topLevelWidget.createChild(-1, WidgetType.GRAPHIC);
		rightBorder.setSpriteId(SpriteID.Steelborder2._1);
		rightBorder.setOriginalWidth(36);
		rightBorder.setOriginalHeight(60);
		rightBorder.setOriginalX(-15);
		rightBorder.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
		rightBorder.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		rightBorder.setHeightMode(WidgetSizeMode.MINUS);
		rightBorder.revalidate();

		// Top border
		Widget topBorder = topLevelWidget.createChild(-1, WidgetType.GRAPHIC);
		topBorder.setSpriteId(SpriteID.Steelborder2._0);
		topBorder.setOriginalWidth(50);
		topBorder.setOriginalHeight(36);
		topBorder.setOriginalY(-15);
		topBorder.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		topBorder.setWidthMode(WidgetSizeMode.MINUS);
		topBorder.revalidate();

		// Bottom border
		Widget bottomBorder = topLevelWidget.createChild(-1, WidgetType.GRAPHIC);
		bottomBorder.setSpriteId(SpriteID.Miscgraphics._3);
		bottomBorder.setOriginalWidth(50);
		bottomBorder.setOriginalHeight(36);
		bottomBorder.setOriginalY(-15);
		bottomBorder.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		bottomBorder.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
		bottomBorder.setWidthMode(WidgetSizeMode.MINUS);
		bottomBorder.revalidate();

		// Title separator border
		Widget titleSeparatorBorder = topLevelWidget.createChild(-1, WidgetType.GRAPHIC);
		titleSeparatorBorder.setSpriteId(SpriteID.SteelborderDivider._0);
		titleSeparatorBorder.setOriginalWidth(10);
		titleSeparatorBorder.setOriginalHeight(26);
		titleSeparatorBorder.setOriginalY(20);
		titleSeparatorBorder.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		titleSeparatorBorder.setWidthMode(WidgetSizeMode.MINUS);
		titleSeparatorBorder.revalidate();

		Widget closeButton = topLevelWidget.createChild(-1, WidgetType.GRAPHIC);
		closeButton.setSpriteId(SpriteID.CloseButtons._0);
		closeButton.setOriginalX(3);
		closeButton.setOriginalY(6);
		closeButton.setOriginalWidth(26);
		closeButton.setOriginalHeight(23);
		closeButton.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
		closeButton.setHasListener(true);
		closeButton.setAction(0, "Close");
		closeButton.setOnOpListener((JavaScriptCallback) (ev) -> {
			close(client);
		});
		closeButton.setOnKeyListener((JavaScriptCallback) (ev) -> {
			if (client.isKeyPressed(KeyCode.KC_ESCAPE))
			{
				close(client);
			}
		});
		closeButton.setOnMouseOverListener((JavaScriptCallback) (ev) -> {
			closeButton.setSpriteId(SpriteID.CloseButtons._1);
		});
		closeButton.setOnMouseLeaveListener((JavaScriptCallback) (ev) -> {
			closeButton.setSpriteId(SpriteID.CloseButtons._0);
		});
		closeButton.setNoClickThrough(true);
		closeButton.revalidate();

		for (int i = 0; i <= 30; i++)
		{
			makeButton(client, topLevelWidget, "Quest Guides" + i, SpriteID.AchievementDiaryIcons._0);
		}

		babyWidget = backgroundWidget;
	}

	Widget buttonLayer;

	Widget scrollbar;

	private void makeButton(Client client, Widget topWidget, String text, int icon)
	{
		if (buttonLayer == null)
		{
			buttonLayer = topWidget.createChild(-1, WidgetType.LAYER);
			buttonLayer.setSize(BUTTON_LAYER_WIDTH, BUTTON_AREA_HEIGHT);
			buttonLayer.setHeightMode(WidgetSizeMode.MINUS);
			buttonLayer.setPos(6, 80);
			buttonLayer.setHasListener(true);
			buttonLayer.revalidate();

			scrollbar = topWidget.createChild(-1, WidgetType.LAYER);
			scrollbar.setSize(BUTTON_LAYER_WIDTH, SCROLLBAR_HEIGHT);
			scrollbar.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
			scrollbar.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
			scrollbar.setOriginalY(SCROLLBAR_HEIGHT / 2);
			scrollbar.revalidate();

			Widget scrollArea = scrollbar.createChild(-1, WidgetType.GRAPHIC);
			scrollArea.setSpriteId(SpriteID.ScrollbarDraggerHorizontalV2._3);
			scrollArea.setOriginalHeight(SCROLLBAR_HEIGHT);
			scrollArea.setOriginalWidth(PADDING * 2);
			scrollArea.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
			scrollArea.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
			scrollArea.setWidthMode(WidgetSizeMode.MINUS);
			scrollArea.setNoClickThrough(true);
			scrollArea.setHasListener(true);

			Widget mainDragger = scrollbar.createChild(-1, WidgetType.GRAPHIC);
			mainDragger.setSpriteId(SpriteID.ScrollbarDraggerHorizontalV2._1);
			mainDragger.setDragParent(scrollArea);
			mainDragger.setOriginalX(PADDING);
			mainDragger.setOriginalWidth(DRAGGER_WIDTH);
			mainDragger.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
			mainDragger.setHeightMode(WidgetSizeMode.MINUS);
			mainDragger.revalidate();

			Widget mainDraggerLeft = scrollbar.createChild(-1, WidgetType.GRAPHIC);
			mainDraggerLeft.setSpriteId(SpriteID.ScrollbarDraggerHorizontalV2._2);
			mainDraggerLeft.setOriginalX(PADDING);
			mainDraggerLeft.setOriginalWidth(DRAGGER_END_WIDTH);
			mainDraggerLeft.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
			mainDraggerLeft.setHeightMode(WidgetSizeMode.MINUS);
			mainDraggerLeft.revalidate();

			Widget mainDraggerRight = scrollbar.createChild(-1, WidgetType.GRAPHIC);
			mainDraggerRight.setSpriteId(SpriteID.ScrollbarDraggerHorizontalV2._0);
			mainDraggerRight.setOriginalWidth(DRAGGER_END_WIDTH);
			mainDraggerRight.setOriginalX(DRAGGER_WIDTH + PADDING);
			mainDraggerRight.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
			mainDraggerRight.setHeightMode(WidgetSizeMode.MINUS);
			mainDraggerRight.revalidate();

			// Left scroll
			Widget leftScroll = scrollbar.createChild(-1, WidgetType.GRAPHIC);
			leftScroll.setSpriteId(SpriteID.ScrollbarV2._2);
			leftScroll.setOriginalWidth(PADDING);
			leftScroll.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
			leftScroll.setHeightMode(WidgetSizeMode.MINUS);
			leftScroll.revalidate();

			Widget rightScroll = scrollbar.createChild(-1, WidgetType.GRAPHIC);
			rightScroll.setSpriteId(SpriteID.ScrollbarV2._3);
			rightScroll.setOriginalWidth(PADDING);
			rightScroll.setYPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
			rightScroll.setHeightMode(WidgetSizeMode.MINUS);
			rightScroll.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
			rightScroll.revalidate();

			buttonLayer.setOnScrollWheelListener((JavaScriptCallback) (ev) -> {
				var existingButtons = (buttonLayer.getDynamicChildren() == null) ? 0 : buttonLayer.getDynamicChildren().length;

				// Work out pos of mouse relative to scrollbar
				var xPos = mainDragger.getOriginalX() + (10 * ev.getMouseY());
				var totalArea = scrollArea.getWidth();
				if (xPos > totalArea - (DRAGGER_WIDTH / 2)) xPos = totalArea - (DRAGGER_WIDTH / 2);
				if (xPos < PADDING) xPos = PADDING;

				scrollbarMove(scrollArea, buttonLayer, mainDragger, mainDraggerLeft, mainDraggerRight, xPos, existingButtons);
			});
			buttonLayer.revalidate();

			var clicked = new AtomicBoolean(false);
			// Setup position logic for scrolled
			scrollArea.setOnClickListener((JavaScriptCallback) (ev) -> {
				clicked.set(true);
			});
			scrollArea.setOnTimerListener((JavaScriptCallback) (ev) -> {
				if (clicked.get())
				{
					if (client.getMouseCurrentButton() != 1)
					{
						clicked.set(false);
						return;
					}
					var existingButtons = (buttonLayer.getDynamicChildren() == null) ? 0 : buttonLayer.getDynamicChildren().length;

					// Work out pos of mouse relative to scrollbar
					var mouseX = client.getMouseCanvasPosition().getX();
					var scrollStartX = scrollArea.getRelativeX();
					var xPosClicked = mouseX - scrollStartX - (BUTTON_LAYER_WIDTH / 2);
					var totalArea = scrollArea.getWidth();
					if (xPosClicked > totalArea - (DRAGGER_WIDTH / 2)) xPosClicked = totalArea - (DRAGGER_WIDTH / 2);
					if (xPosClicked < PADDING) xPosClicked = PADDING;

					scrollbarMove(scrollArea, buttonLayer, mainDragger, mainDraggerLeft, mainDraggerRight, xPosClicked, existingButtons);
				}
			});
			scrollArea.setOnScrollWheelListener((JavaScriptCallback) (ev) -> {
				var existingButtons = (buttonLayer.getDynamicChildren() == null) ? 0 : buttonLayer.getDynamicChildren().length;

				// Work out pos of mouse relative to scrollbar
				var xPos = mainDragger.getOriginalX() + (10 * ev.getMouseY());
				var totalArea = scrollArea.getWidth();
				if (xPos > totalArea - (DRAGGER_WIDTH / 2)) xPos = totalArea - (DRAGGER_WIDTH / 2);
				if (xPos < PADDING) xPos = PADDING;

				scrollbarMove(scrollArea, buttonLayer, mainDragger, mainDraggerLeft, mainDraggerRight, xPos, existingButtons);
			});
			scrollArea.revalidate();
		}

		int existingButtons = (buttonLayer.getDynamicChildren() == null) ? 0 : buttonLayer.getDynamicChildren().length;
		int xShift = ((existingButtons) / 3) * BUTTON_BACKGROUND_WIDTH;
		int yShift = (existingButtons % 3) * BUTTON_BACKGROUND_HEIGHT;

		Widget buttonContainer = buttonLayer.createChild(-1, WidgetType.LAYER);
		buttonContainer.setSize(BUTTON_BACKGROUND_WIDTH, BUTTON_BACKGROUND_HEIGHT);
		buttonContainer.setPos(xShift, yShift);
		buttonContainer.revalidate();

		Widget buttonBackground = buttonContainer.createChild(-1, WidgetType.GRAPHIC);
		buttonBackground.setSpriteId(SpriteID.TRADEBACKING);
		buttonBackground.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		buttonBackground.setOnMouseOverListener((JavaScriptCallback) (ev) -> {
			buttonBackground.setSpriteId(SpriteID.TRADEBACKING_DARK);
		});
		buttonBackground.setOnMouseLeaveListener((JavaScriptCallback) (ev) -> {
			buttonBackground.setSpriteId(SpriteID.TRADEBACKING);
		});
		buttonBackground.setHasListener(true);
		buttonBackground.setAction(0, "Open");
		buttonBackground.revalidate();

		Widget buttonTopLeft = buttonContainer.createChild(-1, WidgetType.GRAPHIC);
		buttonTopLeft.setSpriteId(SpriteID.V2StoneButton._0);
		buttonTopLeft.setPos(BUTTON_PADDING, 0);
		buttonTopLeft.setSize(BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE);
		buttonTopLeft.revalidate();

		Widget buttonTopRight = buttonContainer.createChild(-1, WidgetType.GRAPHIC);
		buttonTopRight.setSpriteId(SpriteID.V2StoneButton._1);
		buttonTopRight.setPos(BUTTON_WIDTH - BUTTON_EDGE_SIZE, 0); // 85
		buttonTopRight.setSize(BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE);
		buttonTopRight.revalidate();

		Widget buttonBottomLeft = buttonContainer.createChild(-1, WidgetType.GRAPHIC);
		buttonBottomLeft.setSpriteId(SpriteID.V2StoneButton._2);
		buttonBottomLeft.setPos(BUTTON_PADDING, BUTTON_HEIGHT - BUTTON_EDGE_SIZE);
		buttonBottomLeft.setSize(BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE);
		buttonBottomLeft.revalidate();

		Widget buttonBottomRight = buttonContainer.createChild(-1, WidgetType.GRAPHIC);
		buttonBottomRight.setSpriteId(SpriteID.V2StoneButton._3);
		buttonBottomRight.setPos(BUTTON_WIDTH - BUTTON_EDGE_SIZE, BUTTON_HEIGHT - BUTTON_EDGE_SIZE);
		buttonBottomRight.setSize(BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE);
		buttonBottomRight.revalidate();

		Widget buttonLeft = buttonContainer.createChild(-1, WidgetType.GRAPHIC);
		buttonLeft.setSpriteId(SpriteID.V2StoneButton._4);
		buttonLeft.setPos(BUTTON_PADDING, BUTTON_EDGE_SIZE);
		buttonLeft.setSize(BUTTON_EDGE_SIZE, BUTTON_HEIGHT - (BUTTON_EDGE_SIZE * 2));
		buttonLeft.revalidate();

		Widget buttonTop = buttonContainer.createChild(-1, WidgetType.GRAPHIC);
		buttonTop.setSpriteId(SpriteID.V2StoneButton._5);
		buttonTop.setPos(BUTTON_EDGE_SIZE + BUTTON_PADDING, 0);
		buttonTop.setSize(BUTTON_WIDTH - (BUTTON_EDGE_SIZE * 2), BUTTON_EDGE_SIZE);
		buttonTop.revalidate();

		Widget buttonRight = buttonContainer.createChild(-1, WidgetType.GRAPHIC);
		buttonRight.setSpriteId(SpriteID.V2StoneButton._6);
		buttonRight.setPos(BUTTON_WIDTH - BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE);
		buttonRight.setSize(BUTTON_EDGE_SIZE, BUTTON_HEIGHT - (BUTTON_EDGE_SIZE * 2));
		buttonRight.revalidate();

		Widget buttonBottom = buttonContainer.createChild(-1, WidgetType.GRAPHIC);
		buttonBottom.setSpriteId(SpriteID.V2StoneButton._7);
		buttonBottom.setPos(BUTTON_EDGE_SIZE + BUTTON_PADDING, BUTTON_HEIGHT - BUTTON_EDGE_SIZE);
		buttonBottom.setSize(BUTTON_WIDTH - (BUTTON_EDGE_SIZE * 2), BUTTON_EDGE_SIZE);
		buttonBottom.revalidate();

		Widget buttonText = buttonContainer.createChild(-1, WidgetType.TEXT);
		buttonText.setText(text);
		buttonText.setFontId(FontID.PLAIN_12);
		buttonText.setTextShadowed(true);
		buttonText.setTextColor(Integer.parseInt("ff9933", 16));
		buttonText.setPos(BUTTON_PADDING, BUTTON_PADDING + 1);
		buttonText.setSize(BUTTON_WIDTH, 12);
		buttonText.setXTextAlignment(WidgetTextAlignment.CENTER);
		buttonText.setYTextAlignment(WidgetTextAlignment.CENTER);
		buttonText.revalidate();

		Widget buttonIcon = buttonContainer.createChild(-1, WidgetType.GRAPHIC);
		buttonIcon.setSpriteId(icon);
		buttonIcon.setPos((BUTTON_WIDTH - ICON_SIZE) / 2, 19);
		buttonIcon.setSize(ICON_SIZE, ICON_SIZE);
		buttonIcon.revalidate();

		buttonLayer.setScrollWidth(BUTTON_LAYER_WIDTH - xShift);
	}

	private void scrollbarMove(Widget scrollArea, Widget buttonLayer, Widget mainDragger, Widget mainDraggerLeft, Widget mainDraggerRight, int xPosToMoveScrollbar, int numButtons)
	{
		var totalArea = scrollArea.getWidth();
		mainDragger.setOriginalX(xPosToMoveScrollbar);
		mainDragger.revalidate();
		mainDraggerLeft.setOriginalX(xPosToMoveScrollbar);
		mainDraggerLeft.revalidate();
		mainDraggerRight.setOriginalX(xPosToMoveScrollbar + DRAGGER_WIDTH);
		mainDraggerRight.revalidate();

		// Update drag of panels
		var totalDraggableArea = totalArea - (PADDING + (DRAGGER_WIDTH / 2));
		var scrollRatio = (float) (xPosToMoveScrollbar - PADDING) / totalDraggableArea;
		var xShift = (numButtons / BUTTONS_PER_COLUMN) * BUTTON_BACKGROUND_WIDTH;
		var posToMoveButtonLayer = Math.max(0, Math.round((xShift + BUTTON_BACKGROUND_WIDTH - BUTTON_LAYER_WIDTH) * scrollRatio));
		buttonLayer.setScrollX(posToMoveButtonLayer);
	}

	private Widget getTopLevelWidget(Client client)
	{
		Widget fixedContainer = client.getWidget(InterfaceID.Toplevel.MAIN);
		if (fixedContainer != null)
		{
			return fixedContainer;
		}
		// Resizable classic
		Widget classicContainer = client.getWidget(InterfaceID.ToplevelOsrsStretch.HUD_CONTAINER_BACK);
		if (classicContainer != null)
		{
			return classicContainer;
		}
		// Resizable modern
		Widget modernContainer = client.getWidget(InterfaceID.ToplevelPreEoc.MAINMODAL);
		if (modernContainer != null)
		{
			return modernContainer;
		}

		return null;
	}

	public void close(Client client)
	{
		if (babyWidget == null) return;

		Widget fixedContainer = client.getWidget(InterfaceID.Toplevel.MAIN);
		if (fixedContainer != null)
		{
			fixedContainer.deleteAllChildren();
		}
		// Resizable classic
		Widget classicContainer = client.getWidget(InterfaceID.ToplevelOsrsStretch.HUD_CONTAINER_BACK);
		if (classicContainer != null)
		{
			classicContainer.deleteAllChildren();
		}
		// Resizable modern
		Widget modernContainer = client.getWidget(InterfaceID.ToplevelPreEoc.MAINMODAL);
		if (modernContainer != null)
		{
			modernContainer.deleteAllChildren();
		}

		babyWidget = null;
		buttonLayer = null;
		scrollbar = null;
	}
}
