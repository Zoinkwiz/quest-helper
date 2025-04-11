/*
 * Copyright (c) 2023, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.runeliteobjects.extendedruneliteobjects;

import net.runelite.api.Client;
import net.runelite.api.ScriptID;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;

import java.util.List;

public class QuestCompletedWidget
{
	private static boolean activeWidget = false;

	public void createWidget(Client client, String questName, List<String> rewards, int itemID, int rotationX, int rotationY, int rotationZ, int zoom)
	{
		// Close an existing widget
		close(client);

		// Fixed
		Widget fixedContainer = client.getWidget(InterfaceID.Toplevel.MAIN);
		if (fixedContainer != null)
		{
			createWidgets(client, fixedContainer, questName, rewards, itemID, rotationX, rotationY, rotationZ, zoom);
		}
		// Resizable classic
		Widget classicContainer = client.getWidget(InterfaceID.ToplevelOsrsStretch.HUD_CONTAINER_BACK);
		if (classicContainer != null)
		{
			createWidgets(client, classicContainer, questName, rewards, itemID, rotationX, rotationY, rotationZ, zoom);
		}
		// Resizable modern
		Widget modernContainer = client.getWidget(InterfaceID.ToplevelPreEoc.HUD_CONTAINER_BACK);
		if (modernContainer != null)
		{
			createWidgets(client, modernContainer, questName, rewards, itemID, rotationX, rotationY, rotationZ, zoom);
		}

		activeWidget = true;
	}

	private void createWidgets(Client client, Widget container, String questName, List<String> rewards, int itemID, int rotationX, int rotationY, int rotationZ, int zoom)
	{
		Widget backgroundWidget = container.createChild(-1, WidgetType.MODEL);
		backgroundWidget.setModelId(3032);
		backgroundWidget.setXPositionMode(1);
		backgroundWidget.setYPositionMode(1);
		backgroundWidget.setRotationX(502);
		backgroundWidget.setRotationZ(2047);
		backgroundWidget.setModelZoom(554);
		backgroundWidget.setOriginalY(-19);
		backgroundWidget.setOriginalWidth(500);
		backgroundWidget.setOriginalHeight(300);
		backgroundWidget.setOnOpListener(ScriptID.NULL);
		backgroundWidget.setHasListener(true);
		backgroundWidget.setNoClickThrough(true);
		backgroundWidget.setOnClickListener((JavaScriptCallback) (ev) -> {});
		backgroundWidget.revalidate();

		Widget congratulationWidget = container.createChild(-1, WidgetType.TEXT);
		congratulationWidget.setText("Congratulations!");
		congratulationWidget.setFontId(497);
		congratulationWidget.setTextColor(Integer.parseInt("37242b", 16)); //CHANGE
		congratulationWidget.setOriginalY(-92);
		congratulationWidget.setOriginalWidth(162);
		congratulationWidget.setOriginalHeight(18);
		congratulationWidget.setXPositionMode(1);
		congratulationWidget.setYPositionMode(1);
		congratulationWidget.setXTextAlignment(1);
		congratulationWidget.revalidate();

		Widget questCompleteWidget = container.createChild(-1, WidgetType.TEXT);
		questCompleteWidget.setText("You have completed " + questName + "!");
		questCompleteWidget.setFontId(495);
		questCompleteWidget.setTextColor(Integer.parseInt("321e25", 16)); // CHANGE
		questCompleteWidget.setOriginalY(-70);
		questCompleteWidget.setOriginalWidth(210);
		questCompleteWidget.setOriginalHeight(14);
		questCompleteWidget.setXPositionMode(1);
		questCompleteWidget.setYPositionMode(1);
		questCompleteWidget.setXTextAlignment(1);
		questCompleteWidget.revalidate();

		Widget iconWidget = container.createChild(-1, WidgetType.MODEL);
		iconWidget.setItemId(itemID);
		iconWidget.setRotationX(rotationX);
		iconWidget.setRotationY(rotationY);
		iconWidget.setRotationZ(rotationZ);
		iconWidget.setModelZoom(zoom);
		iconWidget.setOriginalX(-140);
		iconWidget.setOriginalY(20);
		iconWidget.setOriginalWidth(80);
		iconWidget.setOriginalHeight(80);
		iconWidget.setXPositionMode(1);
		iconWidget.setYPositionMode(1);
		iconWidget.revalidate();

		Widget awardedWidget = container.createChild(-1, WidgetType.TEXT);
		awardedWidget.setText("You are awarded:");
		awardedWidget.setFontId(495);
		awardedWidget.setTextColor(Integer.parseInt("321e25", 16)); // CHANGE
		awardedWidget.setOriginalY(-35);
		awardedWidget.setOriginalWidth(180);
		awardedWidget.setOriginalHeight(14);
		awardedWidget.setXPositionMode(1);
		awardedWidget.setYPositionMode(1);
		awardedWidget.setXTextAlignment(1);
		awardedWidget.revalidate();

		for (int i = 0; i < rewards.size(); i++)
		{
			Widget rewardWidget = container.createChild(-1, WidgetType.TEXT);
			rewardWidget.setText(rewards.get(i));
			rewardWidget.setFontId(495);
			rewardWidget.setTextColor(Integer.parseInt("321e25", 16)); // CHANGE
			rewardWidget.setOriginalY(-14 + (i * 15));
			rewardWidget.setOriginalWidth(180);
			rewardWidget.setOriginalHeight(14);
			rewardWidget.setXPositionMode(1);
			rewardWidget.setYPositionMode(1);
			rewardWidget.setXTextAlignment(1);
			rewardWidget.revalidate();
		}

		Widget ribbonWidget = container.createChild(-1, WidgetType.MODEL);
		ribbonWidget.setModelId(3037);
		ribbonWidget.setRotationX(500);
		ribbonWidget.setModelZoom(800);
		ribbonWidget.setOriginalX(150);
		ribbonWidget.setOriginalY(60);
		ribbonWidget.setOriginalWidth(32);
		ribbonWidget.setOriginalHeight(32);
		ribbonWidget.setXPositionMode(1);
		ribbonWidget.setYPositionMode(1);
		ribbonWidget.revalidate();

		Widget closeWidget = container.createChild(-1, WidgetType.GRAPHIC);
		closeWidget.setSpriteId(537);
		closeWidget.setXPositionMode(1);
		closeWidget.setYPositionMode(1);
		closeWidget.setOriginalX(170);
		closeWidget.setOriginalY(-70);
		closeWidget.setOriginalWidth(26);
		closeWidget.setOriginalHeight(23);
		closeWidget.setOnOpListener(ScriptID.NULL);
		closeWidget.setHasListener(true);
		closeWidget.setOnClickListener((JavaScriptCallback) (ev) -> {
			close(client);
		});
		closeWidget.setNoClickThrough(true);
		closeWidget.revalidate();
	}

	public void createWidget(Client client, String questName, List<String> rewards, int itemID)
	{
		createWidget(client, questName, rewards, itemID, 0, 0, 0, 0);
	}

	public void close(Client client)
	{
		if (!activeWidget) return;

		// Fixed
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
		Widget modernContainer = client.getWidget(InterfaceID.ToplevelPreEoc.HUD_CONTAINER_BACK);
		if (modernContainer != null)
		{
			modernContainer.deleteAllChildren();
		}
	}
}
