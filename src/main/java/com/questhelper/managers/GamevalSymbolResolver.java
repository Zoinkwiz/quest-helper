package com.questhelper.managers;

import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import javax.inject.Singleton;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.questhelper.managers.HelperConstructModels.IdType;

@Singleton
public class GamevalSymbolResolver
{
	private final Map<IdType, Map<Integer, List<String>>> symbolsByType = new EnumMap<>(IdType.class);

	public GamevalSymbolResolver()
	{
		symbolsByType.put(IdType.ITEM, buildMap(ItemID.class, "ItemID"));
		symbolsByType.put(IdType.NPC, buildMap(NpcID.class, "NpcID"));
		symbolsByType.put(IdType.OBJECT, buildObjectMap());
		symbolsByType.put(IdType.VARBIT, buildMap(VarbitID.class, "VarbitID"));
	}

	public ResolutionResult resolve(IdType idType, int rawId)
	{
		Map<Integer, List<String>> map = symbolsByType.getOrDefault(idType, Collections.emptyMap());
		List<String> matches = map.getOrDefault(rawId, Collections.emptyList());
		if (matches.isEmpty())
		{
			return new ResolutionResult(rawId + " /* TODO unresolved " + idType.name().toLowerCase() + " */", false, true);
		}

		boolean ambiguous = matches.size() > 1;
		return new ResolutionResult(matches.get(0), ambiguous, false);
	}

	private Map<Integer, List<String>> buildMap(Class<?> clazz, String prefix)
	{
		Map<Integer, List<String>> map = new HashMap<>();
		for (Field field : clazz.getDeclaredFields())
		{
			if (!Modifier.isStatic(field.getModifiers()) || field.getType() != int.class)
			{
				continue;
			}

			try
			{
				// Some generated gameval split classes (eg ObjectID1) are package-private.
				// Their public static fields still need reflective access override here.
				if (!field.canAccess(null))
				{
					field.setAccessible(true);
				}
				int value = ((Number) field.get(null)).intValue();
				String symbol = prefix + "." + field.getName();
				map.computeIfAbsent(value, ignored -> new ArrayList<>()).add(symbol);
			}
			catch (ReflectiveOperationException | RuntimeException ignored)
			{
			}
		}

		map.values().forEach(Collections::sort);
		return map;
	}

	private Map<Integer, List<String>> buildObjectMap()
	{
		Map<Integer, List<String>> map = new HashMap<>();
		mergeMap(map, buildMap(ObjectID.class, "ObjectID"));
		mergeOptionalMap(map, "net.runelite.api.gameval.ObjectID1", "ObjectID");
		return map;
	}

	private void mergeOptionalMap(Map<Integer, List<String>> destination, String className, String prefix)
	{
		try
		{
			Class<?> clazz = Class.forName(className);
			mergeMap(destination, buildMap(clazz, prefix));
		}
		catch (ClassNotFoundException ignored)
		{
			// Optional split constants class not available in this version.
		}
	}

	private void mergeMap(Map<Integer, List<String>> destination, Map<Integer, List<String>> source)
	{
		for (Map.Entry<Integer, List<String>> entry : source.entrySet())
		{
			destination.computeIfAbsent(entry.getKey(), ignored -> new ArrayList<>()).addAll(entry.getValue());
		}
		destination.values().forEach(Collections::sort);
	}

	public static final class ResolutionResult
	{
		private final String symbol;
		private final boolean ambiguous;
		private final boolean fallbackLiteral;

		public ResolutionResult(String symbol, boolean ambiguous, boolean fallbackLiteral)
		{
			this.symbol = symbol;
			this.ambiguous = ambiguous;
			this.fallbackLiteral = fallbackLiteral;
		}

		public String getSymbol()
		{
			return symbol;
		}

		public boolean isAmbiguous()
		{
			return ambiguous;
		}

		public boolean isFallbackLiteral()
		{
			return fallbackLiteral;
		}
	}
}
