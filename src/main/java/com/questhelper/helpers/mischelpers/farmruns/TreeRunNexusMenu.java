package com.questhelper.helpers.mischelpers.farmruns;

import com.questhelper.QuestHelperConfig;
import net.runelite.client.config.ConfigManager;
import java.util.Map;
import java.util.LinkedHashMap;
import com.questhelper.helpers.mischelpers.farmruns.TreeRunOptions.Destination;
import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.util.Text;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Read the player's displayed shortcut; never rewrite native menu labels or keybinds. */
final class TreeRunNexusMenu
{
	private static final Pattern KEY = Pattern.compile("^([a-z0-9]|F(?:[1-9]|1[0-2]))(?:\\s*[:.)]\\s*|\\s+)(.+)$", Pattern.CASE_INSENSITIVE);

	private static final String CACHE_KEY = "treeRunNexusHotkeys";

	/** Keep the last observed menu per character; opening the menu replaces the snapshot. */
	static Map<Destination, String> rememberedHotkeys(Client client, ConfigManager config)
	{
		Map<Destination, String> keys = new LinkedHashMap<>();
		if (config.getRSProfileKey() == null) return keys;
		Widget configuration = client.getWidget(InterfaceID.Telenexus.UNIVERSE);
		if (configuration != null && !configuration.isHidden())
		{
			// Reordering can invalidate every remembered key. Relearn from the teleport menu.
			config.unsetRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, CACHE_KEY);
			return keys;
		}
		Widget root = client.getWidget(InterfaceID.TelenexusTeleport.UNIVERSE);
		if (root != null && !root.isHidden())
		{
			for (Destination destination : Destination.values())
			{
				String key = hotkey(client, destination.label);
				if (key != null) keys.put(destination, key);
			}
			StringBuilder saved = new StringBuilder();
			keys.forEach((destination, key) -> saved.append(destination.name()).append('=').append(key).append(';'));
			String value = saved.toString();
			if (!value.equals(config.getRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, CACHE_KEY)))
				config.setRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, CACHE_KEY, value);
			return keys;
		}
		String saved = config.getRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, CACHE_KEY);
		if (saved == null) return keys;
		for (String entry : saved.split(";"))
		{
			String[] parts = entry.split("=", -1);
			if (parts.length != 2 || !parts[1].matches("[A-Z0-9]|F(?:[1-9]|1[0-2])")) continue;
			try { keys.put(Destination.valueOf(parts[0]), parts[1]); }
			catch (IllegalArgumentException ignored) { /* Ignore obsolete/corrupt entries. */ }
		}
		return keys;
	}

	static String hotkey(Client client, String destination)
	{
		Widget root = client.getWidget(InterfaceID.TelenexusTeleport.UNIVERSE);
		if (root == null || root.isHidden()) return null;
		return find(root, destination, Collections.newSetFromMap(new IdentityHashMap<>()));
	}

	private static String find(Widget widget, String destination, Set<Widget> visited)
	{
		if (widget == null || widget.isHidden() || !visited.add(widget)) return null;
		String text = widget.getText();
		if (text != null)
		{
			Matcher match = KEY.matcher(Text.removeTags(text).replace('\u00a0', ' ').trim());
			if (match.matches() && match.group(2).trim().equalsIgnoreCase(destination))
				return match.group(1).toUpperCase(java.util.Locale.ROOT);
		}
		Widget[][] children = {widget.getChildren(), widget.getStaticChildren(), widget.getDynamicChildren()};
		for (Widget[] group : children)
		{
			if (group == null) continue;
			// Shortcut and destination may be separate widgets on the same row.
			for (Widget label : group)
			{
				if (label == null || label.isHidden() || label.getText() == null
					|| !Text.removeTags(label.getText()).trim().equalsIgnoreCase(destination)) continue;
				String candidate = null;
				for (Widget keyWidget : group)
				{
					if (keyWidget == null || keyWidget == label || keyWidget.isHidden()
						|| keyWidget.getText() == null || keyWidget.getRelativeY() != label.getRelativeY()
						|| keyWidget.getRelativeX() >= label.getRelativeX()) continue;
					String value = Text.removeTags(keyWidget.getText()).trim().replaceFirst("[:.)]$", "").trim();
					if (!value.matches("(?i)[a-z0-9]|F(?:[1-9]|1[0-2])")) continue;
					if (candidate != null) { candidate = null; break; }
					candidate = value.toUpperCase(java.util.Locale.ROOT);
				}
				if (candidate != null) return candidate;
			}
			for (Widget child : group)
			{
				String key = find(child, destination, visited);
				if (key != null) return key;
			}
		}
		return null;
	}
}
