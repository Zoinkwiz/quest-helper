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
	private final Style style;

	@Getter
	private final String name;
	@Getter
	private final String message;

	@Getter
	private int textShift = 0;

	@RequiredArgsConstructor
	public enum Style
	{
		FAN(l ->
		{
			short qpcDark = (short) -22440;

			ModelData hood = l.client.loadModelData(18914).cloneColors()
				.recolor((short) -21568, qpcDark) // Inside
				.recolor((short) 22464, qpcDark)
				.recolor((short) 960, qpcDark);

			ModelData cape = l.client.loadModelData(18946).cloneColors()
				.recolor((short) -8256, qpcDark) // Outside trim
				.recolor((short) -11353, JagexColor.rgbToHSL(Color.WHITE.getRGB(), 1.0d)); // Inside trim!!!

			ModelData[] mda = new ModelData[]{ hood, // QP Hood
				cape, // QP Cape
				l.client.loadModelData(11359),
				l.client.loadModelData(38101), // crystal body
				l.client.loadModelData(38079), // crystal legs
				l.client.loadModelData(4925), // Beard
				l.client.loadModelData(10706), // Hands
				l.client.loadModelData(358) // Feet
			};

			ModelData mdf = l.client.mergeModels(mda);

			return mdf.cloneColors()
				.light(10 + ModelData.DEFAULT_AMBIENT, 1875 + ModelData.DEFAULT_CONTRAST,
					ModelData.DEFAULT_X, ModelData.DEFAULT_Y, 20);
		}, anim(862)),
		;

		private final Function<Cheerer, Model> modelSupplier;
		private final Function<Cheerer, Animation> animationSupplier;
	}

	private static Function<Cheerer, Animation> anim(int id)
	{
		return b -> b.client.loadAnimation(id);
	}

	public Cheerer(Client client, ClientThread clientThread, WorldPoint worldPoint, Style style, ChatMessageManager chatMessageManager, String name, String message)
	{
		this.client = client;
		this.clientThread = clientThread;
		this.style = style;
		this.runeLiteObject = client.createRuneLiteObject();
		this.worldPoint = worldPoint;
		this.name = name;
		this.message = message;

		update();

		runeLiteObject.setShouldLoop(true);

		LocalPoint lp = LocalPoint.fromWorld(client, this.worldPoint);

		String chatMessage = new ChatMessageBuilder()
			.append(ChatColorType.NORMAL)
			.append(this.message)
			.build();

		this.textShift = this.message.length() * 2 + 20;

		chatMessageManager.queue(QueuedMessage.builder()
			.type(ChatMessageType.PUBLICCHAT)
			.name(name)
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