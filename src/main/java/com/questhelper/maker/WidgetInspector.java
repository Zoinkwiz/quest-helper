/*
 * Copyright (c) 2018 Abex
 * Copyright (c) 2017, Kronos <https://github.com/KronosDesign>
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
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
package com.questhelper.maker;


import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import java.awt.Color;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetConfig;
import net.runelite.api.widgets.WidgetType;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;

@Slf4j
@Singleton
public class WidgetInspector
{
	@Getter
	static final class PickedWidget
	{
		private final int groupId;
		private final int childId;
		private final String label;
		private final String text;

		private PickedWidget(int groupId, int childId, String label, String text)
		{
			this.groupId = groupId;
			this.childId = childId;
			this.label = label;
			this.text = text;
		}
	}

	static final Color SELECTED_WIDGET_COLOR = Color.CYAN;
	private static final float SELECTED_WIDGET_HUE;

	static
	{
		float[] hsb = new float[3];
		Color.RGBtoHSB(SELECTED_WIDGET_COLOR.getRed(), SELECTED_WIDGET_COLOR.getGreen(), SELECTED_WIDGET_COLOR.getBlue(), hsb);
		SELECTED_WIDGET_HUE = hsb[0];
	}

	private final Client client;
	private final ClientThread clientThread;
	private final Provider<WidgetInspectorOverlay> overlay;
	private final OverlayManager overlayManager;

	@Getter
	private Widget selectedWidget;
	@Getter
	private PickedWidget selectedWidgetPick;
	private Consumer<PickedWidget> pickListener;
	private boolean keepPickerOpen;
	private Widget picker;

	@Getter
	private boolean pickerSelected = false;

	@Inject
	private WidgetInspector(
		Client client,
		ClientThread clientThread,
		EventBus eventBus,
		Provider<WidgetInspectorOverlay> overlay,
		OverlayManager overlayManager)
	{
		this.client = client;
		this.clientThread = clientThread;
		this.overlay = overlay;
		this.overlayManager = overlayManager;

		eventBus.register(this);
	}

	private void setSelectedWidget(Widget widget, int packedComponentId)
	{
		selectedWidget = widget;
		int groupId = WidgetUtil.componentToInterface(packedComponentId);
		int childId = WidgetUtil.componentToId(packedComponentId);
		String label = groupId + "," + childId;
		String rawText = widget == null ? null : Text.removeTags(widget.getText());
		String cleanedText = rawText == null ? null : rawText.trim();
		PickedWidget picked = new PickedWidget(groupId, childId, label, cleanedText == null || cleanedText.isBlank() ? null : cleanedText);
		selectedWidgetPick = picked;
		Consumer<PickedWidget> listener = pickListener;
		if (!keepPickerOpen)
		{
			pickListener = null;
		}
		if (listener != null)
		{
			SwingUtilities.invokeLater(() -> listener.accept(picked));
		}
	}

	private void clearSelectedWidget()
	{
		selectedWidget = null;
		selectedWidgetPick = null;
	}

	public void openForPick(Consumer<PickedWidget> listener)
	{
		openForPick(listener, false);
	}

	public void openForPick(Consumer<PickedWidget> listener, boolean keepOpen)
	{
		this.pickListener = listener;
		this.keepPickerOpen = keepOpen;
		open();
	}

	public void open()
	{
		overlayManager.add(this.overlay.get());
		clientThread.invokeLater(this::addPickerWidget);
	}

	public void close()
	{
		overlayManager.remove(this.overlay.get());
		clientThread.invokeLater(this::removePickerWidget);
		keepPickerOpen = false;
		pickListener = null;
		clearSelectedWidget();
	}

	private void removePickerWidget()
	{
		onPickerDeselect();
		if (picker == null)
		{
			return;
		}
		Widget parent = picker.getParent();
		if (parent == null)
		{
			picker = null;
			return;
		}
		Widget[] children = parent.getChildren();
		if (children != null && children.length > picker.getIndex() && children[picker.getIndex()] == picker)
		{
			children[picker.getIndex()] = null;
		}
		picker = null;
	}

	private void addPickerWidget()
	{
		removePickerWidget();
		int x = 10;
		int y = 2;
		Widget parent = client.getWidget(InterfaceID.Orbs.UNIVERSE);
		if (parent == null || parent.isHidden())
		{
			parent = client.getWidget(InterfaceID.OrbsNomap.UNIVERSE);
			x = 32;
			y = 0;
		}
		if (parent == null || parent.isHidden())
		{
			Widget[] roots = client.getWidgetRoots();
			if (roots == null || roots.length == 0)
			{
				return;
			}
			parent = Stream.of(roots)
				.filter(w -> w != null && w.getType() == WidgetType.LAYER && w.getContentType() == 0 && !w.isSelfHidden())
				.max(Comparator.comparingInt((Widget w) -> w.getRelativeX() + w.getRelativeY()).reversed()
					.thenComparingInt(Widget::getId))
				.orElse(null);
			x = 4;
			y = 4;
		}
		if (parent == null)
		{
			return;
		}
		picker = parent.createChild(-1, WidgetType.GRAPHIC);
		picker.setSpriteId(SpriteID.OptionsIcons.MOBILE_FINGER_ON_INTERFACE);
		picker.setOriginalWidth(15);
		picker.setOriginalHeight(17);
		picker.setOriginalX(x);
		picker.setOriginalY(y);
		picker.revalidate();
		picker.setTargetVerb("Select");
		picker.setName("Pick");
		picker.setClickMask(WidgetConfig.USE_WIDGET);
		picker.setNoClickThrough(true);
		picker.setOpacity(0);
		picker.setOnTargetEnterListener((JavaScriptCallback) ev ->
		{
			pickerSelected = true;
			picker.setOpacity(30);
			client.setAllWidgetsAreOpTargetable(true);
		});
		picker.setOnTargetLeaveListener((JavaScriptCallback) ev -> onPickerDeselect());
	}

	private void onPickerDeselect()
	{
		client.setAllWidgetsAreOpTargetable(false);
		pickerSelected = false;
	}

	@Subscribe
	private void onMenuOptionClicked(MenuOptionClicked ev)
	{
		if (!pickerSelected)
		{
			return;
		}

		onPickerDeselect();
		client.setWidgetSelected(false);
		ev.consume();

		Widget target = getWidgetForMenuOption(ev.getMenuAction(), ev.getParam0(), ev.getParam1());
		if (target == null)
		{
			return;
		}

		setSelectedWidget(target, ev.getParam1());
		if (!keepPickerOpen)
		{
			close();
		}
	}

	@Subscribe
	private void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (!pickerSelected)
		{
			return;
		}

		MenuEntry[] menuEntries = client.getMenuEntries();

		for (int i = 0; i < menuEntries.length; i++)
		{
			MenuEntry entry = menuEntries[i];
			if (entry.getType() != MenuAction.WIDGET_TARGET_ON_WIDGET)
			{
				continue;
			}
			String name = WidgetUtil.componentToInterface(entry.getParam1()) + "." + WidgetUtil.componentToId(entry.getParam1());

			if (entry.getParam0() != -1)
			{
				name += " [" + entry.getParam0() + "]";
			}
			Widget hoveredWidget = getWidgetForMenuOption(entry.getType(), entry.getParam0(), entry.getParam1());
			String hoveredText = hoveredWidget == null ? null : Text.removeTags(hoveredWidget.getText());
			if (hoveredText != null)
			{
				hoveredText = hoveredText.trim();
				if (!hoveredText.isBlank())
				{
					name += " - \"" + hoveredText + "\"";
				}
			}

			Color color = colorForWidget(i, menuEntries.length);

			entry.setTarget(ColorUtil.wrapWithColorTag(name, color));
		}
	}

	Color colorForWidget(int index, int length)
	{
		float h = SELECTED_WIDGET_HUE + .1f + (.8f / length) * index;

		return Color.getHSBColor(h, 1, 1);
	}

	Widget getWidgetForMenuOption(MenuAction type, int param0, int param1)
	{
		if (type == MenuAction.WIDGET_TARGET_ON_WIDGET)
		{
			Widget w = client.getWidget(param1);
			if (param0 != -1)
			{
				w = w.getChild(param0);
			}

			return w;
		}

		return null;
	}
}
