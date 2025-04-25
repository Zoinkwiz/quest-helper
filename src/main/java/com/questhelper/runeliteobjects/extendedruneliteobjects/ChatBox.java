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
package com.questhelper.runeliteobjects.extendedruneliteobjects;

import com.questhelper.runeliteobjects.dialog.RuneliteDialogStep;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.FontID;
import net.runelite.api.widgets.*;
import net.runelite.client.game.chatbox.ChatboxInput;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.input.KeyListener;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class ChatBox extends ChatboxInput implements KeyListener
{
	@Data
	@AllArgsConstructor
	private static final class Entry
	{
		private String text;
		private Runnable callback;
	}

	protected final Client client;

	protected final ChatboxPanelManager chatboxPanelManager;

	@Getter
	protected RuneliteDialogStep dialog;

	@Getter
	private final List<Entry> options = new ArrayList<>();

	@Getter
	private Runnable onClose;

	RuneliteDialogStep nextDialog;
	ChatBox nextChatBox;

	boolean chatProgressed = false;

	@Inject
	protected ChatBox(Client client, ChatboxPanelManager chatboxPanelManager)
	{
		this.client = client;
		this.chatboxPanelManager = chatboxPanelManager;
	}

	public ChatBox dialog(RuneliteDialogStep dialog)
	{
		this.dialog = dialog;
		return this;
	}


	public ChatBox option(String text, Runnable callback)
	{
		options.add(new Entry(text, callback));
		return this;
	}

	public ChatBox onClose(Runnable onClose)
	{
		this.onClose = onClose;
		return this;
	}

	public ChatBox build()
	{
		if (dialog == null)
		{
			throw new IllegalStateException("Dialog must be set");
		}

		chatboxPanelManager.openInput(this);
		return this;
	}

	Widget continueW;

	@Override
	protected void open()
	{
		Widget container = chatboxPanelManager.getContainerWidget();

		Widget npcFaceWidget = container.createChild(0, WidgetType.RECTANGLE);
		npcFaceWidget.setType(WidgetType.MODEL);
		npcFaceWidget.setModelId(dialog.getFaceID());
		npcFaceWidget.setModelType(WidgetModelType.NPC_CHATHEAD);
		npcFaceWidget.setAnimationId(dialog.getAnimation());
		npcFaceWidget.setRotationX(40);
		npcFaceWidget.setRotationZ(1882);
		npcFaceWidget.setModelZoom(796);
		npcFaceWidget.setOriginalX(46);
		npcFaceWidget.setOriginalY(53);
		npcFaceWidget.setOriginalWidth(32);
		npcFaceWidget.setOriginalHeight(32);
		npcFaceWidget.setXPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
		npcFaceWidget.setYPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
		npcFaceWidget.revalidate();

		setupDialog(container, 96);
	}

	public void setupDialog(Widget container, int x)
	{
		int WRAPPER_HEIGHT_ADJUSTMENT = 17;
		int WRAPPER_WIDTH_ADJUSTMENT = 12;

		Widget nameWidget = container.createChild(-1, WidgetType.TEXT);
		nameWidget.setText(dialog.getName());
		nameWidget.setTextColor(0x800000);
		nameWidget.setFontId(FontID.QUILL_8);
		nameWidget.setXPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
		nameWidget.setOriginalX(WRAPPER_WIDTH_ADJUSTMENT + x);
		nameWidget.setYPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
		nameWidget.setOriginalY(WRAPPER_HEIGHT_ADJUSTMENT);
		nameWidget.setOriginalWidth(380);
		nameWidget.setOriginalHeight(17);
		nameWidget.setXTextAlignment(WidgetTextAlignment.CENTER);
		nameWidget.setYTextAlignment(WidgetTextAlignment.CENTER);
		nameWidget.setWidthMode(WidgetSizeMode.ABSOLUTE);
		nameWidget.revalidate();

		Widget continueWidget = container.createChild(-1, WidgetType.TEXT);
		continueWidget.setText("Click here to continue");
		continueWidget.setTextColor(0xff);
		continueWidget.setFontId(FontID.QUILL_8);
		continueWidget.setXPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
		continueWidget.setOriginalX(WRAPPER_WIDTH_ADJUSTMENT + x);
		continueWidget.setYPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
		continueWidget.setOriginalY(WRAPPER_HEIGHT_ADJUSTMENT + 80);
		continueWidget.setOriginalWidth(380);
		continueWidget.setOriginalHeight(17);
		continueWidget.setXTextAlignment(WidgetTextAlignment.CENTER);
		continueWidget.setYTextAlignment(WidgetTextAlignment.CENTER);
		continueWidget.setWidthMode(WidgetSizeMode.ABSOLUTE);
		continueWidget.setAction(0, "Continue");
		continueWidget.setOnOpListener((JavaScriptCallback) ev -> {
			continueWidget.setText("Please wait...");
			continueChat();
		});
		continueWidget.setOnMouseOverListener((JavaScriptCallback) ev -> continueWidget.setTextColor(0xFFFFFF));
		continueWidget.setOnMouseLeaveListener((JavaScriptCallback) ev -> continueWidget.setTextColor(0xff));
		continueWidget.setHasListener(true);
		continueWidget.revalidate();

		continueW = continueWidget;

		Widget dialogWidget = container.createChild(-1, WidgetType.TEXT);
		dialogWidget.setText(dialog.getText());
		dialogWidget.setTextColor(0x0);
		dialogWidget.setFontId(FontID.QUILL_8);
		dialogWidget.setXPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
		dialogWidget.setOriginalX(WRAPPER_WIDTH_ADJUSTMENT + x);
		dialogWidget.setYPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
		dialogWidget.setOriginalY(WRAPPER_HEIGHT_ADJUSTMENT + 16);
		dialogWidget.setOriginalWidth(380);
		dialogWidget.setOriginalHeight(67);
		dialogWidget.setXTextAlignment(WidgetTextAlignment.CENTER);
		dialogWidget.setYTextAlignment(WidgetTextAlignment.CENTER);
		dialogWidget.setWidthMode(WidgetSizeMode.ABSOLUTE);
		dialogWidget.revalidate();
	}

	public void progressDialog()
	{
		if (nextDialog == null) return;

		// If already progressed, try to progress next step
		if (nextChatBox != null)
		{
			nextChatBox.progressDialog();
			return;
		}

		if (nextDialog.isPlayer())
		{
			nextChatBox = new PlayerChatBox(client, chatboxPanelManager)
				.dialog(nextDialog)
				.build();
			return;
		}
		nextChatBox = new NpcChatBox(client, chatboxPanelManager)
			.dialog(nextDialog)
			.build();
	}

	protected void continueChat()
	{
		dialog.progressState();

		// Already clicked an option
		if (nextDialog != null) return;

		// TODO: Possible race condition once we add multiple choices, need to make sure they lock choices once one is clicked
		nextDialog = dialog.getContinueDialog();
		if (nextDialog == null)
		{
			chatboxPanelManager.close();
		}
	}

	private void callback(Entry entry)
	{
		Widget container = chatboxPanelManager.getContainerWidget();
		container.setOnKeyListener((Object[]) null);

		chatboxPanelManager.close();

		entry.callback.run();
	}

	@Override
	protected void close()
	{
		if (onClose != null)
		{
			onClose.run();
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		if (!chatboxPanelManager.shouldTakeInput())
		{
			return;
		}

		char c = e.getKeyChar();

		if (c == '\033')
		{
			chatboxPanelManager.close();
			e.consume();
			return;
		}

		if (c == KeyEvent.VK_SPACE)
		{
			if (continueW != null) continueW.setText("Please wait...");
			continueChat();
		}

		int n = c - '1';
		if (n >= 0 && n < options.size())
		{
			callback(options.get(n));
			e.consume();
		}
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (!chatboxPanelManager.shouldTakeInput())
		{
			return;
		}

		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			e.consume();
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
	}
}
