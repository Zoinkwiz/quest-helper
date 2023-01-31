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
package com.questhelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Animation;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.JagexColor;
import net.runelite.api.Model;
import net.runelite.api.ModelData;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import java.awt.Color;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;

public class Cheerer
{
	public final RuneLiteObject runeLiteObject;

	public final WorldPoint worldPoint;
	private final Client client;
	private final ClientThread clientThread;
	@Getter
	private final Style style;

	@Getter
	private final String message;
	private static final int QUEST_HOOD_MALE = 18914;
	private static final int QUEST_CAPE_MALE = 18946;

	@RequiredArgsConstructor
	public enum Style
	{
		FAN(l ->
		{
			short qpcDark = (short) -22440;

			ModelData hood = l.client.loadModelData(QUEST_HOOD_MALE).cloneColors()
				.recolor((short) -21568, qpcDark) // Inside
				.recolor((short) 22464, qpcDark)
				.recolor((short) 960, qpcDark);

			ModelData cape = l.client.loadModelData(QUEST_CAPE_MALE).cloneColors()
				.recolor((short) -8256, qpcDark) // Outside trim
				.recolor((short) -11353, JagexColor.rgbToHSL(Color.WHITE.getRGB(), 1.0d)); // Inside trim

			ModelData mdf = createModel(l.client,
				11359, 38101, 38079, 4925, 10706, 358);
			mdf = createModel(l.client, mdf, hood, cape);

			return mdf.cloneColors()
				.light(10 + ModelData.DEFAULT_AMBIENT, 1875 + ModelData.DEFAULT_CONTRAST,
					ModelData.DEFAULT_X, ModelData.DEFAULT_Y, 20);
		}, anim(862), "Zoinkwiz", "Loves questing."),
		WISE_OLD_MAN(l ->
		{
			short clothingColor = JagexColor.rgbToHSL(new Color(102, 93, 44).getRGB(), 0.6d);
			short blue = JagexColor.rgbToHSL(Color.BLUE.getRGB(), 1.0d);
			ModelData skirt = l.client.loadModelData(265).cloneColors()
				.recolor((short)25238, clothingColor);

			ModelData shirt = l.client.loadModelData(292).cloneColors()
				.recolor((short)8741, clothingColor);

			ModelData arms = l.client.loadModelData(170).cloneColors()
				.recolor((short)8741, clothingColor);

			// 8741
			ModelData cape = l.client.loadModelData(323).cloneColors()
				.recolor((short) 926, blue) // Inside
				.recolor((short) 7700, blue) // Mail cape
				.recolor((short) 11200, (short)8741); // Trim

			ModelData partyhat = l.client.loadModelData(187).cloneColors()
				.recolor((short)926, blue);
			ModelData mdf = createModel(l.client,
				9103, // face
				4925, // beard
				176, // hands
				181); // feet?

			mdf = createModel(l.client, mdf, skirt, shirt, arms, cape, partyhat);

			return mdf.cloneColors()
				.light();
		}, anim(862), "Wise Old Man", "Loves questing."),
		;

		private final Function<Cheerer, Model> modelSupplier;
		private final Function<Cheerer, Animation> animationSupplier;

		@Getter
		private final String displayName;

		@Getter
		private final String examine;

		private static final List<Style> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
		private static final int SIZE = VALUES.size();
		private static final Random RANDOM = new Random();

		public static Style randomCheerer()
		{
			return VALUES.get(RANDOM.nextInt(SIZE));
		}
	}

	private static Function<Cheerer, Animation> anim(int id)
	{
		return b -> b.client.loadAnimation(id);
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

	public Cheerer(Client client, ClientThread clientThread, WorldPoint worldPoint, Style style, ChatMessageManager chatMessageManager, String message)
	{
		this.client = client;
		this.clientThread = clientThread;
		this.style = style;
		this.runeLiteObject = client.createRuneLiteObject();
		this.worldPoint = worldPoint;
		this.message = message;

		update();

		runeLiteObject.setShouldLoop(true);

		LocalPoint lp = LocalPoint.fromWorld(client, this.worldPoint);

		String chatMessage = new ChatMessageBuilder()
			.append(ChatColorType.NORMAL)
			.append(this.message)
			.build();

		chatMessageManager.queue(QueuedMessage.builder()
			.type(ChatMessageType.PUBLICCHAT)
			.name(style.getDisplayName())
			.runeLiteFormattedMessage(chatMessage)
			.timestamp((int) (System.currentTimeMillis() / 1000))
			.build());
		runeLiteObject.setLocation(lp, client.getPlane());

		runeLiteObject.setActive(true);
	}

	private void update()
	{
		clientThread.invoke(() ->
		{
			Model model = style.modelSupplier.apply(this);
			if (model == null)
			{
				return false;
			}

			Animation anim = style.animationSupplier.apply(this);

			runeLiteObject.setAnimation(anim);
			runeLiteObject.setModel(model);
			return true;
		});
	}

	public void remove()
	{
		runeLiteObject.setActive(false);
	}

}