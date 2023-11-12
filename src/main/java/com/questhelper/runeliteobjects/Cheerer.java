/*
 * Copyright (c) 2022, Zoinkwiz <https://github.com/Zoinkwiz>
 * Copyright (c) 2021, Trevor <https://github.com/Trevor159>
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
package com.questhelper.runeliteobjects;

import com.questhelper.runeliteobjects.extendedruneliteobjects.FakeNpc;
import com.questhelper.runeliteobjects.extendedruneliteobjects.RuneliteObjectManager;
import net.runelite.api.Client;
import net.runelite.api.JagexColor;
import net.runelite.api.Model;
import net.runelite.api.ModelData;
import net.runelite.api.coords.WorldPoint;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;

public class Cheerer
{
	private static final List<FakeNpc> cheerers = new ArrayList<>();

	public static void createCheerers(RuneliteObjectManager runeliteObjectManager, Client client, ConfigManager configManager)
	{
		createWOM(runeliteObjectManager, client);
		createZoinkwiz(runeliteObjectManager, client);
	}

	private static void createWOM(RuneliteObjectManager runeliteObjectManager, Client client)
	{
		WorldPoint playerPos = client.getLocalPlayer().getWorldLocation();
		WorldPoint pointAbovePlayer = new WorldPoint(playerPos.getX(), playerPos.getY() + 1, playerPos.getPlane());
		FakeNpc wiseOldMan = runeliteObjectManager.createFakeNpc("global", wiseOldManOutfit(client), pointAbovePlayer, 862);
		wiseOldMan.setName("Wise Old Man");
		wiseOldMan.setExamine("Loves questing.");
		wiseOldMan.addExamineAction(runeliteObjectManager);
		wiseOldMan.disable();

		cheerers.add(wiseOldMan);
	}

	private static Model wiseOldManOutfit(Client client)
	{
		short clothingColor = JagexColor.rgbToHSL(new Color(102, 93, 44).getRGB(), 0.6d);
		short blue = JagexColor.rgbToHSL(Color.BLUE.getRGB(), 1.0d);
		ModelData skirt = client.loadModelData(265).cloneColors()
			.recolor((short)25238, clothingColor);

		ModelData shirt = client.loadModelData(292).cloneColors()
			.recolor((short)8741, clothingColor);

		ModelData arms = client.loadModelData(170).cloneColors()
			.recolor((short)8741, clothingColor);

		// 8741
		ModelData cape = client.loadModelData(323).cloneColors()
			.recolor((short) 926, blue) // Inside
			.recolor((short) 7700, blue) // Mail cape
			.recolor((short) 11200, (short) 8741); // Trim

		ModelData partyhat = client.loadModelData(187).cloneColors()
			.recolor((short)926, blue);
		ModelData mdf = createModel(client,
			9103, // face
			4925, // beard
			176, // hands
			181); // feet?

		mdf = createModel(client, mdf, skirt, shirt, arms, cape, partyhat);

		return mdf.cloneColors()
			.light();
	}

	private static void createZoinkwiz(RuneliteObjectManager runeliteObjectManager, Client client)
	{
		WorldPoint playerPos = client.getLocalPlayer().getWorldLocation();
		WorldPoint pointAbovePlayer = new WorldPoint(playerPos.getX(), playerPos.getY() + 1, playerPos.getPlane());
		FakeNpc zoinkwiz = runeliteObjectManager.createFakeNpc("global", zoinkwizOutfit(client), pointAbovePlayer, 862);
		zoinkwiz.setName("Zoinkwiz");
		zoinkwiz.setExamine("Loves questing.");
		zoinkwiz.addExamineAction(runeliteObjectManager);
		zoinkwiz.disable();

		cheerers.add(zoinkwiz);
	}

	private static Model zoinkwizOutfit(Client client)
	{
		short qpcDark = (short) -22440;
		final int QUEST_HOOD_MALE = 18914;
		final int QUEST_CAPE_MALE = 18946;

		ModelData hood = client.loadModelData(QUEST_HOOD_MALE).cloneColors()
			.recolor((short) -21568, qpcDark) // Inside
			.recolor((short) 22464, qpcDark)
			.recolor((short) 960, qpcDark);

		ModelData cape = client.loadModelData(QUEST_CAPE_MALE).cloneColors()
			.recolor((short) -8256, qpcDark) // Outside trim
			.recolor((short) -11353, JagexColor.rgbToHSL(Color.WHITE.getRGB(), 1.0d)); // Inside trim

		ModelData mdf = createModel(client,
			11359, 38101, 38079, 4925, 10706, 358);
		mdf = createModel(client, mdf, hood, cape);

		return mdf.cloneColors()
			.light(10 + ModelData.DEFAULT_AMBIENT, 1875 + ModelData.DEFAULT_CONTRAST,
				ModelData.DEFAULT_X, ModelData.DEFAULT_Y, 20);
	}

	private static ModelData createModel(Client client, ModelData... data)
	{
		return client.mergeModels(data);
	}

	private static ModelData createModel(Client client, int... data)
	{
		ModelData[] modelData = new ModelData[data.length];
		for (int i = 0; i < data.length; i++)
		{
			modelData[i] = client.loadModelData(data[i]);
		}
		return client.mergeModels(modelData);
	}

	public static void activateCheerer(Client client, ChatMessageManager chatMessageManager)
	{
		Random generator = new Random();
		FakeNpc cheerer = cheerers.get(generator.nextInt(cheerers.size()));
		WorldPoint playerPos = client.getLocalPlayer().getWorldLocation();

		int tickToRemoveOn = client.getTickCount() + 20;

		WorldPoint pointAbovePlayer = new WorldPoint(playerPos.getX(), playerPos.getY() + 1, playerPos.getPlane());
		cheerer.setWorldPoint(pointAbovePlayer);
		cheerer.activate();
		cheerer.addOverheadText("Congratz on completing the quest!", tickToRemoveOn, chatMessageManager);
		cheerer.setDisableAfterTick(tickToRemoveOn);
	}
}
