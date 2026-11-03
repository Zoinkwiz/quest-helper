package com.questhelper.helpers.mischelpers.farmruns;

import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;

class TreeRunNexusMenuTest
{
	@Test
	void readsNativeLetterWithoutChangingTheMenu()
	{
		Client client = mock(Client.class);
		Widget root = mock(Widget.class), row = mock(Widget.class);
		when(client.getWidget(InterfaceID.TelenexusTeleport.UNIVERSE)).thenReturn(root);
		when(root.getChildren()).thenReturn(new Widget[]{row});
		when(row.getText()).thenReturn("<col=ff9040>G:</col> Varrock");
		assertEquals("G", TreeRunNexusMenu.hotkey(client, "Varrock"));
		assertNull(TreeRunNexusMenu.hotkey(client, "Falador"));
		verify(row, never()).setText(anyString());
	}

	@Test
	void doesNotInventAKeyForClosedOrHiddenMenus()
	{
		Client client = mock(Client.class);
		assertNull(TreeRunNexusMenu.hotkey(client, "Varrock"));
		Widget root = mock(Widget.class);
		when(client.getWidget(InterfaceID.TelenexusTeleport.UNIVERSE)).thenReturn(root);
		when(root.isHidden()).thenReturn(true);
		assertNull(TreeRunNexusMenu.hotkey(client, "Varrock"));
	}
	@Test
	void persistsObservedKeysAfterMenuClosesAndInvalidatesWhenReordering()
	{
		Client client = mock(Client.class);
		net.runelite.client.config.ConfigManager config = mock(net.runelite.client.config.ConfigManager.class);
		when(config.getRSProfileKey()).thenReturn("character-one");
		java.util.Map<String, String> values = new java.util.HashMap<>();
		when(config.getRSProfileConfiguration(anyString(), anyString()))
			.thenAnswer(i -> values.get(i.getArgument(1)));
		doAnswer(i -> { values.put(i.getArgument(1), i.getArgument(2)); return null; })
			.when(config).setRSProfileConfiguration(anyString(), anyString(), any());
		doAnswer(i -> { values.remove(i.getArgument(1)); return null; })
			.when(config).unsetRSProfileConfiguration(anyString(), anyString());
		Widget root = mock(Widget.class), row = mock(Widget.class);
		when(client.getWidget(InterfaceID.TelenexusTeleport.UNIVERSE)).thenReturn(root);
		when(root.getChildren()).thenReturn(new Widget[]{row});
		when(row.getText()).thenReturn("<col=ffffff>F10 : Catherby</col>");
		assertEquals("F10", TreeRunNexusMenu.rememberedHotkeys(client, config).get(TreeRunOptions.Destination.CATHERBY));
		when(client.getWidget(InterfaceID.TelenexusTeleport.UNIVERSE)).thenReturn(null);
		assertEquals("F10", TreeRunNexusMenu.rememberedHotkeys(client, config).get(TreeRunOptions.Destination.CATHERBY));
		values.clear(); // A different RS profile has its own empty config.
		assertTrue(TreeRunNexusMenu.rememberedHotkeys(client, config).isEmpty());
		values.put("treeRunNexusHotkeys", "CATHERBY=F10;");
		Widget configuration = mock(Widget.class);
		when(client.getWidget(InterfaceID.Telenexus.UNIVERSE)).thenReturn(configuration);
		assertTrue(TreeRunNexusMenu.rememberedHotkeys(client, config).isEmpty());
		assertFalse(values.containsKey("treeRunNexusHotkeys"));
	}

	@Test
	void replacesOldKeyAndDropsRemovedDestinationOnNextMenuRead()
	{
		Client client = mock(Client.class);
		net.runelite.client.config.ConfigManager config = mock(net.runelite.client.config.ConfigManager.class);
		when(config.getRSProfileKey()).thenReturn("character-one");
		when(config.getRSProfileConfiguration(anyString(), anyString())).thenReturn("VARROCK=G;CATHERBY=4;");
		Widget root = mock(Widget.class);
		when(client.getWidget(InterfaceID.TelenexusTeleport.UNIVERSE)).thenReturn(root);
		when(root.getText()).thenReturn("B: Varrock");
		java.util.Map<TreeRunOptions.Destination, String> keys = TreeRunNexusMenu.rememberedHotkeys(client, config);
		assertEquals("B", keys.get(TreeRunOptions.Destination.VARROCK));
		assertFalse(keys.containsKey(TreeRunOptions.Destination.CATHERBY));
	}


	@Test
	void readsWhitespaceSeparatedNativeShortcut()
	{
		Client client = mock(Client.class);
		Widget root = mock(Widget.class);
		when(client.getWidget(InterfaceID.TelenexusTeleport.UNIVERSE)).thenReturn(root);
		when(root.getText()).thenReturn("<col=ffffff>G</col> Varrock");
		assertEquals("G", TreeRunNexusMenu.hotkey(client, "Varrock"));
	}

	@Test
	void readsSeparateShortcutAndDestinationWidgetsOnTheSameRow()
	{
		Client client = mock(Client.class);
		Widget root = mock(Widget.class), key = mock(Widget.class), label = mock(Widget.class);
		when(client.getWidget(InterfaceID.TelenexusTeleport.UNIVERSE)).thenReturn(root);
		when(root.getDynamicChildren()).thenReturn(new Widget[]{key, label});
		when(key.getText()).thenReturn("G:");
		when(label.getText()).thenReturn("Varrock");
		when(key.getRelativeX()).thenReturn(10);
		when(label.getRelativeX()).thenReturn(30);
		assertEquals("G", TreeRunNexusMenu.hotkey(client, "Varrock"));
		when(key.getRelativeY()).thenReturn(20);
		assertNull(TreeRunNexusMenu.hotkey(client, "Varrock"));
	}
}
