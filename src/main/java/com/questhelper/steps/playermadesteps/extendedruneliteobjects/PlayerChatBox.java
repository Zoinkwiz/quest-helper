/*
 * Copyright (c) 2023, Zoinkwiz <https://github.com/Zoinkwiz>
 * Copyright (c) 2018 Abex
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
package com.questhelper.steps.playermadesteps.extendedruneliteobjects;


import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetModelType;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.game.chatbox.ChatboxPanelManager;

public class PlayerChatBox extends ChatBox
{
	protected PlayerChatBox(Client client, ChatboxPanelManager chatboxPanelManager)
	{
		super(client, chatboxPanelManager);
	}

	@Override
	protected void open()
	{
		Widget container = chatboxPanelManager.getContainerWidget();

		Widget npcFaceWidget = container.createChild(0, WidgetType.RECTANGLE);
		npcFaceWidget.setType(WidgetType.MODEL);
		npcFaceWidget.setModelId(dialog.getFaceID());
		npcFaceWidget.setAnimationId(dialog.getAnimation());
		npcFaceWidget.setRotationX(40);
		npcFaceWidget.setRotationZ(166);
		npcFaceWidget.setModelZoom(796);
		npcFaceWidget.setOriginalX(420);
		npcFaceWidget.setOriginalY(50);
		npcFaceWidget.setOriginalWidth(32);
		npcFaceWidget.setOriginalHeight(32);
		npcFaceWidget.setXPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
		npcFaceWidget.setYPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
		npcFaceWidget.setModelType(WidgetModelType.LOCAL_PLAYER_CHATHEAD);
		npcFaceWidget.revalidate();

		setupDialog(container, 5);
	}
}

