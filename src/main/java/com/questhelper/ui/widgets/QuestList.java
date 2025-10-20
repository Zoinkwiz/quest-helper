package com.questhelper.ui.widgets;

import net.runelite.api.Client;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetSizeMode;
import net.runelite.api.widgets.WidgetTextAlignment;
import net.runelite.api.widgets.WidgetType;
import java.util.ArrayList;
import java.util.List;

public class QuestList
{
	final Widget scrollableArea;
	final Widget contentLayer;
	final VerticalScrollableContainer verticalScrollableContainer;
	final List<Widget> listSections = new ArrayList<>();

	final int BASE_HEIGHT = 49;
	final int EXPANDED_HEIGHT = 81;
	public QuestList(Client client, Widget parent)
	{
		verticalScrollableContainer = new VerticalScrollableContainer(parent, 0, 40, 0, 40, client);
		scrollableArea = verticalScrollableContainer.getContainer();
		contentLayer = verticalScrollableContainer.getContentLayer();
	}


	public Widget addItem(String title, String subtitle, String baseColorHex, JavaScriptCallback onClick)
	{
		var backgroundColour = "ffffff";
		int backgroundOpacity;
		if (listSections.size() % 2 == 0)
		{
			backgroundOpacity = 245;
		}
		else
		{
			backgroundOpacity = 230;
		}

		var yPos = listSections.size() * BASE_HEIGHT;

		var questSection = contentLayer.createChild(-1, WidgetType.LAYER);
		questSection.setOriginalHeight(BASE_HEIGHT);
		questSection.setOriginalY(yPos);
		questSection.setWidthMode(WidgetSizeMode.MINUS);
		questSection.setName("QH Quest List Element");
		questSection.setHasListener(true);
		questSection.setOnClickListener(onClick);
		questSection.revalidate();

		var questSectionBackground = questSection.createChild(-1, WidgetType.RECTANGLE);
		questSectionBackground.setTextColor(Integer.parseInt(backgroundColour, 16));
		questSectionBackground.setOpacity(backgroundOpacity);
		questSectionBackground.setWidthMode(WidgetSizeMode.MINUS);
		questSectionBackground.setHeightMode(WidgetSizeMode.MINUS);
		questSectionBackground.revalidate();

		var expandSymbol = questSection.createChild(-1, WidgetType.GRAPHIC);
		expandSymbol.setSpriteId(SpriteID.OpenButtons._4);
		expandSymbol.setPos(-1, 1);
		expandSymbol.setSize(24, 23);
		expandSymbol.revalidate();

		var questSymbol = questSection.createChild(-1, WidgetType.GRAPHIC);
		questSymbol.setSpriteId(SpriteID.AchievementDiaryIcons._0);
		questSymbol.setPos(23, 4);
		questSymbol.setSize(18, 18);
		questSymbol.revalidate();

		var questTitle = questSection.createChild(-1, WidgetType.TEXT);
		questTitle.setText(title);
		questTitle.setTextColor(Integer.parseInt(baseColorHex, 16));
		questTitle.setFontId(495);
		questTitle.setTextShadowed(true);
		questTitle.setPos(44, 5);
		questTitle.setWidthMode(WidgetSizeMode.MINUS);
		questTitle.setOriginalHeight(16);
		questTitle.setLineHeight(16);
		questTitle.setYTextAlignment(WidgetTextAlignment.CENTER);
		questTitle.revalidate();

		var questDescription = questSection.createChild(-1, WidgetType.TEXT);
		questDescription.setText(subtitle);
		questDescription.setTextColor(Integer.parseInt("ffffff", 16));
		questDescription.setFontId(494);
		questDescription.setTextShadowed(true);
		questDescription.setPos(23, 26);
		questDescription.setWidthMode(WidgetSizeMode.MINUS);
		questDescription.setOriginalHeight(13);
		questDescription.setLineHeight(13);
		questDescription.setYTextAlignment(WidgetTextAlignment.CENTER);
		questDescription.revalidate();

		// Hover effects on the whole section: make title white on hover, restore on leave
		questSection.setOnMouseOverListener((JavaScriptCallback) (ev) -> {
			questTitle.setTextColor(Integer.parseInt("ffffff", 16));
		});
		questSection.setOnMouseLeaveListener((JavaScriptCallback) (ev) -> {
			questTitle.setTextColor(Integer.parseInt(baseColorHex, 16));
		});

		listSections.add(questSection);
		verticalScrollableContainer.updateScrollbar();
		return questSection;
	}

	public void refresh()
	{
		verticalScrollableContainer.updateScrollbar();
	}

	public void clear()
	{
		for (Widget widget : listSections)
		{
			widget.setHidden(true);
		}
		listSections.clear();
		verticalScrollableContainer.updateScrollbar();
	}
}
