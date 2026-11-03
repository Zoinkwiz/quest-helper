/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz/>
 * Copyright (c) 2024, Kerpackie <https://github.com/Kerpackie/>
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
package com.questhelper.helpers.mischelpers.farmruns;

import com.questhelper.QuestHelperConfig;
import com.questhelper.questinfo.HelperConfig;
import com.questhelper.requirements.item.ItemRequirement;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.timetracking.farming.CompostState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Player-selected transport, shared by the route instructions and the bank list. */
final class TreeRunOptions
{
	static final String PREFIX = "treeRun";

	enum Inclusion { INCLUDE, SKIP }
	enum Rake { BRING, SKIP }
	enum CompostType
	{
		ULTRACOMPOST(ItemID.BUCKET_ULTRACOMPOST, CompostState.ULTRACOMPOST),
		SUPERCOMPOST(ItemID.BUCKET_SUPERCOMPOST, CompostState.SUPERCOMPOST),
		COMPOST(ItemID.BUCKET_COMPOST, CompostState.COMPOST),
		NONE(-1, null);

		final int itemId;
		final CompostState state;
		CompostType(int itemId, CompostState state) { this.itemId = itemId; this.state = state; }
	}
	enum CompostSource { BOTTOMLESS_BUCKET, INDIVIDUAL_BUCKETS, LEPRECHAUN_STORAGE }
	enum HouseEntry { HOUSE_TABLETS, HOUSE_SPELL }
	enum Travel
	{
		DIRECT_TELEPORT, POH_NEXUS, POH_SPIRIT_TREE, POH_JEWELLERY_BOX,
		POH_MOUNTED_DIGSITE, POH_FAIRY_RING, POH_OUTSIDE, REDIRECTED_HOUSE_TABLET,
		SPIRIT_TREE_VIA_GE, ARDOUGNE_BOAT, WALK_FROM_FALADOR, WALK_FROM_BRIMHAVEN,
		ROWBOAT_FROM_CORSAIR_COVE, WALK_FROM_TAVERLEY_ROCKS, MOUNTAIN_GUIDE_QUETZAL
	}
	enum Category
	{
		REGULAR("Regular trees"), FRUIT("Fruit trees"), HARDWOOD("Hardwood trees"), CALQUAT("Calquat trees");
		final String label;
		Category(String label) { this.label = label; }
		String key() { return PREFIX + "Include" + name(); }
	}
	enum Destination
	{
		GUILD("Farming Guild", Travel.DIRECT_TELEPORT, Travel.POH_JEWELLERY_BOX, Travel.POH_SPIRIT_TREE, Travel.POH_FAIRY_RING),
		LUMBRIDGE("Lumbridge", Travel.DIRECT_TELEPORT, Travel.POH_NEXUS),
		FALADOR("Falador", Travel.DIRECT_TELEPORT, Travel.POH_NEXUS, Travel.POH_JEWELLERY_BOX),
		TAVERLEY("Taverley", Travel.WALK_FROM_FALADOR, Travel.REDIRECTED_HOUSE_TABLET, Travel.POH_JEWELLERY_BOX, Travel.POH_OUTSIDE),
		VARROCK("Varrock", Travel.DIRECT_TELEPORT, Travel.POH_NEXUS),
		STRONGHOLD("Gnome Stronghold", Travel.SPIRIT_TREE_VIA_GE, Travel.POH_SPIRIT_TREE),
		VILLAGE("Tree Gnome Village", Travel.SPIRIT_TREE_VIA_GE, Travel.POH_SPIRIT_TREE, Travel.POH_FAIRY_RING),
		CATHERBY("Catherby", Travel.DIRECT_TELEPORT, Travel.POH_NEXUS, Travel.WALK_FROM_TAVERLEY_ROCKS),
		BRIMHAVEN("Brimhaven", Travel.ARDOUGNE_BOAT, Travel.REDIRECTED_HOUSE_TABLET, Travel.POH_SPIRIT_TREE, Travel.POH_OUTSIDE),
		TAI_BWO_WANNAI("Tai Bwo Wannai", Travel.WALK_FROM_BRIMHAVEN, Travel.DIRECT_TELEPORT, Travel.POH_FAIRY_RING),
		LLETYA("Lletya", Travel.DIRECT_TELEPORT),
		FOSSIL_ISLAND("Fossil Island", Travel.DIRECT_TELEPORT, Travel.POH_MOUNTED_DIGSITE),
		SAVANNAH("Avium Savannah", Travel.POH_FAIRY_RING),
		NEMUS("Nemus Retreat", Travel.DIRECT_TELEPORT, Travel.POH_FAIRY_RING),
		KASTORI("Kastori", Travel.DIRECT_TELEPORT, Travel.POH_FAIRY_RING, Travel.MOUNTAIN_GUIDE_QUETZAL),
		ANGLERS("Anglers' Retreat", Travel.ROWBOAT_FROM_CORSAIR_COVE),
		GREAT_CONCH("Great Conch", Travel.POH_FAIRY_RING);

		final String label;
		final Travel[] choices;
		Destination(String label, Travel... choices) { this.label = label; this.choices = choices; }
		String key() { return PREFIX + "Travel" + name(); }
	}

	private final ConfigManager configManager;
	TreeRunOptions(ConfigManager configManager) { this.configManager = configManager; }

	<T extends Enum<T>> T read(String key, Class<T> type, T fallback)
	{
		String value = configManager.getRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, key);
		if (value != null)
		{
			try { return Enum.valueOf(type, value); }
			catch (IllegalArgumentException ignored) { /* Old or invalid setting: use a supported default. */ }
		}
		return fallback;
	}

	boolean includes(Category category) { return read(category.key(), Inclusion.class, Inclusion.INCLUDE) == Inclusion.INCLUDE; }
	boolean bringRake() { return read(PREFIX + "Rake", Rake.class, Rake.BRING) == Rake.BRING; }
	CompostType compostType() { return read(PREFIX + "CompostType", CompostType.class, CompostType.ULTRACOMPOST); }
	CompostSource compostSource() { return read(PREFIX + "CompostSource", CompostSource.class, CompostSource.BOTTOMLESS_BUCKET); }
	HouseEntry houseEntry() { return read(PREFIX + "HouseEntry", HouseEntry.class, HouseEntry.HOUSE_TABLETS); }
	Travel travel(Destination destination)
	{
		Travel value = read(destination.key(), Travel.class, destination.choices[0]);
		return Arrays.asList(destination.choices).contains(value) ? value : destination.choices[0];
	}

	String routeSignature()
	{
		StringBuilder value = new StringBuilder();
		for (Destination destination : Destination.values()) value.append(travel(destination)).append(';');
		return value.append(houseEntry()).append(';').append(compostType()).append(';').append(compostSource()).toString();
	}

	List<HelperConfig> configs()
	{
		List<HelperConfig> configs = new ArrayList<>();
		for (Category category : Category.values()) configs.add(new HelperConfig("Include " + category.label.toLowerCase(), category.key(), Inclusion.values()));
		configs.add(new HelperConfig("Compost type", PREFIX + "CompostType", CompostType.values()));
		configs.add(new HelperConfig("Compost source", PREFIX + "CompostSource", CompostSource.values()));
		configs.add(new HelperConfig("House entry", PREFIX + "HouseEntry", HouseEntry.values()));
		configs.add(new HelperConfig("Rake", PREFIX + "Rake", Rake.values()));
		for (Destination destination : Destination.values())
		{
			if (destination.choices.length > 1) configs.add(new HelperConfig(destination.label + " travel", destination.key(), destination.choices));
		}
		return configs;
	}

	String instruction(Destination destination)
	{
		switch (travel(destination))
		{
			case POH_NEXUS: return "POH nexus/portal -> " + destination.label + ". Open the teleport menu for your configured shortcut; keys depend on your nexus order.";
			case POH_SPIRIT_TREE: return "POH spirit tree -> " + destination.label + "." +
				((destination == Destination.BRIMHAVEN || destination == Destination.GUILD) ? " Plant a spirit tree there first." : "") +
				(destination == Destination.VILLAGE ? " Follow Elkoy out of the maze." : "");
			case POH_JEWELLERY_BOX: return "POH jewellery box -> " +
				(destination == Destination.TAVERLEY ? "Burthorpe; run south to Taverley." : destination == Destination.FALADOR ? "Falador Park." : "Farming Guild.");
			case POH_MOUNTED_DIGSITE: return "POH mounted digsite pendant -> Fossil Island; mushroom -> Mushroom Meadow. Unlock both destinations first.";
			case POH_FAIRY_RING: return "POH fairy ring -> " + fairyCode(destination) + "; " + fairyDirections(destination);
			case POH_OUTSIDE: return "Teleport to House -> outside your " + destination.label + " house portal. Requires your house to be located in " + destination.label + ". Use Outside on the tablet, or leave through the house portal.";
			case REDIRECTED_HOUSE_TABLET: return destination.label + " redirected house tablet (teleports outside the house portal).";
			case SPIRIT_TREE_VIA_GE: return instruction(Destination.VARROCK) + " Walk to the Grand Exchange spirit tree -> " + destination.label +
				(destination == Destination.VILLAGE ? "; follow Elkoy out of the maze." : ".");
			case ARDOUGNE_BOAT: return "Ardougne teleport -> Captain Barnaby at the docks -> Brimhaven (30 coins).";
			case MOUNTAIN_GUIDE_QUETZAL: return "From the Auburnvale tree patch, follow the mountain guide, then use the nearby quetzal to Kastori and walk to the farming patches. Unlock the quetzal destination first. To reach Auburnvale: " + instruction(Destination.NEMUS);
			case WALK_FROM_FALADOR: return "Walk north-west from Falador to Taverley. To reach Falador: " + instruction(Destination.FALADOR);
			case WALK_FROM_BRIMHAVEN: return "Walk south from the Brimhaven fruit tree to Tai Bwo Wannai. To reach Brimhaven: " + instruction(Destination.BRIMHAVEN);
			case WALK_FROM_TAVERLEY_ROCKS: return "From Taverley, run south-west to the rocks on White Wolf Mountain; climb down west to the Catherby fruit tree (68 Agility, no grapple). To reach Taverley: " + instruction(Destination.TAVERLEY);
			case ROWBOAT_FROM_CORSAIR_COVE: return "POH nexus/portal -> Watchtower; run to Corsair Cove -> your built rowboat to Anglers' Retreat. Requires the rowboat and 51 Sailing.";
			default:
				switch (destination)
				{
					case GUILD: return "Skills necklace -> Farming Guild.";
					case FALADOR: return "Ring of wealth -> Falador Park, or Falador teleport tablet.";
					case CATHERBY: return "Catherby teleport tablet, or Camelot tablet then run south-east.";
					case TAI_BWO_WANNAI: return "Tai bwo wannai teleport scroll -> calquat patch.";
					case LLETYA: return "Teleport crystal -> Lletya.";
					case FOSSIL_ISLAND: return "Digsite pendant -> Fossil Island; mushroom -> Mushroom Meadow. Unlock both destinations first.";
					case NEMUS: return "Pendant of Ates -> Nemus Retreat; run north. Activate the statue first.";
					case KASTORI: return "Pendant of Ates -> Kastori; run south-west. Activate the statue first.";
					default: return destination.label + " teleport tablet -> tree patch.";
				}
		}
	}

	private String fairyCode(Destination destination)
	{
		switch (destination)
		{
			case GUILD: return "CIR";
			case VILLAGE: return "CIQ";
			case TAI_BWO_WANNAI: return "CKR";
			case SAVANNAH: return "AJP";
			case NEMUS: return "AIS";
			case KASTORI: return "AIS";
			case GREAT_CONCH: return "CJQ";
			default: throw new IllegalArgumentException("No fairy ring for " + destination);
		}
	}

	private String fairyDirections(Destination destination)
	{
		switch (destination)
		{
			case GUILD: return "run south-west to the Farming Guild.";
			case VILLAGE: return "run north-west to the fruit tree outside the maze.";
			case TAI_BWO_WANNAI: return "run north to the calquat patch.";
			case SAVANNAH: return "run north-west to the hardwood patch.";
			case NEMUS: return "run south-west to Nemus Retreat.";
			case KASTORI: return "run south-west to Achilka at Gloomthorn Trail; take her free boat to Kastori, then run west to the farming patches.";
			case GREAT_CONCH: return "run south-west to the calquat patch (requires island access).";
			default: throw new IllegalArgumentException("No fairy ring for " + destination);
		}
	}

	/** Count consumables once per destination, and shared reusable items only once. */
	List<ItemRequirement> travelItems(Iterable<Destination> destinations)
	{
		return travelItems(destinations, java.util.Collections.emptySet());
	}

	List<ItemRequirement> travelItems(Iterable<Destination> destinations, java.util.Set<Destination> arrived)
	{
		Map<Integer, ItemRequirement> items = new LinkedHashMap<>();
		int houseVisits = 0;
		java.util.Set<Destination> journeys = new java.util.LinkedHashSet<>();
		destinations.forEach(journeys::add);
		if (journeys.contains(Destination.KASTORI) && travel(Destination.KASTORI) == Travel.MOUNTAIN_GUIDE_QUETZAL) journeys.add(Destination.NEMUS);
		if (journeys.contains(Destination.CATHERBY) && travel(Destination.CATHERBY) == Travel.WALK_FROM_TAVERLEY_ROCKS) journeys.add(Destination.TAVERLEY);
		if (journeys.contains(Destination.TAI_BWO_WANNAI) && travel(Destination.TAI_BWO_WANNAI) == Travel.WALK_FROM_BRIMHAVEN) journeys.add(Destination.BRIMHAVEN);
		if (journeys.contains(Destination.TAVERLEY) && travel(Destination.TAVERLEY) == Travel.WALK_FROM_FALADOR) journeys.add(Destination.FALADOR);
		if (journeys.stream().anyMatch(d -> travel(d) == Travel.SPIRIT_TREE_VIA_GE)) journeys.add(Destination.VARROCK);
		journeys.removeAll(arrived);
		for (Destination destination : journeys)
		{
			Travel travel = travel(destination);
			switch (travel)
			{
				case POH_NEXUS: case POH_SPIRIT_TREE: case POH_JEWELLERY_BOX:
				case POH_MOUNTED_DIGSITE: case POH_FAIRY_RING: case POH_OUTSIDE: case ROWBOAT_FROM_CORSAIR_COVE:
					houseVisits++;
					break;
				case MOUNTAIN_GUIDE_QUETZAL: case WALK_FROM_FALADOR: case WALK_FROM_BRIMHAVEN: case WALK_FROM_TAVERLEY_ROCKS:
					// Their prerequisite destination was added above, even if its patch is skipped/growing.
					break;
				case REDIRECTED_HOUSE_TABLET:
					add(items, new ItemRequirement(destination.label + " house tablet", destination == Destination.BRIMHAVEN ? ItemID.NZONE_TELETAB_BRIMHAVEN : ItemID.NZONE_TELETAB_TAVERLEY), true);
					break;
				case SPIRIT_TREE_VIA_GE:
					// Use the selected Varrock route, including POH nexus, for the GE connection.
					break;
				case ARDOUGNE_BOAT:
					add(items, new ItemRequirement("Ardougne teleport (boat to Brimhaven)", ItemID.POH_TABLET_ARDOUGNETELEPORT), true);
					add(items, new ItemRequirement("Brimhaven boat fare", ItemID.COINS, 30), true);
					break;
				case DIRECT_TELEPORT:
					ItemRequirement item = directItem(destination);
					add(items, item, destination != Destination.GUILD && destination != Destination.FALADOR && destination != Destination.LLETYA && destination != Destination.FOSSIL_ISLAND && destination != Destination.NEMUS && destination != Destination.KASTORI);
					break;
				default: throw new IllegalArgumentException("Unsupported travel " + travel);
			}
		}
		if (houseVisits > 0)
		{
			if (houseEntry() == HouseEntry.HOUSE_SPELL)
			{
				add(items, new ItemRequirement("Law runes for Teleport to House (standard spellbook)", ItemID.LAWRUNE, houseVisits), true);
				add(items, new ItemRequirement("Air runes for Teleport to House", ItemID.AIRRUNE, houseVisits), true);
				add(items, new ItemRequirement("Earth runes for Teleport to House", ItemID.EARTHRUNE, houseVisits), true);
			}
			else add(items, new ItemRequirement("Teleport to house tablets", ItemID.POH_TABLET_TELEPORTTOHOUSE, houseVisits), true);
		}
		return new ArrayList<>(items.values());
	}

	/** Stable requirement objects allow the sidebar to update without rebuilding the helper. */
	List<ItemRequirement> itemCatalog()
	{
		Map<Integer, ItemRequirement> items = new LinkedHashMap<>();
		for (Destination destination : Destination.values())
			if (Arrays.asList(destination.choices).contains(Travel.DIRECT_TELEPORT)) add(items, directItem(destination), false);
		add(items, new ItemRequirement("Teleport to house tablets", ItemID.POH_TABLET_TELEPORTTOHOUSE), false);
		add(items, new ItemRequirement("Law runes for Teleport to House", ItemID.LAWRUNE), false);
		add(items, new ItemRequirement("Air runes for Teleport to House", ItemID.AIRRUNE), false);
		add(items, new ItemRequirement("Earth runes for Teleport to House", ItemID.EARTHRUNE), false);
		add(items, new ItemRequirement("Brimhaven house tablet", ItemID.NZONE_TELETAB_BRIMHAVEN), false);
		add(items, new ItemRequirement("Taverley house tablet", ItemID.NZONE_TELETAB_TAVERLEY), false);
		add(items, new ItemRequirement("Ardougne teleport", ItemID.POH_TABLET_ARDOUGNETELEPORT), false);
		return new ArrayList<>(items.values());
	}

	private ItemRequirement directItem(Destination destination)
	{
		switch (destination)
		{
			case GUILD: return new ItemRequirement("Skills necklace", com.questhelper.collections.ItemCollections.SKILLS_NECKLACES);
			case LUMBRIDGE: return new ItemRequirement("Lumbridge teleport", ItemID.POH_TABLET_LUMBRIDGETELEPORT);
			case VARROCK: return new ItemRequirement("Varrock teleport", ItemID.POH_TABLET_VARROCKTELEPORT);
			case FALADOR:
				ItemRequirement falador = new ItemRequirement("Falador Park / Falador teleport", com.questhelper.collections.ItemCollections.RING_OF_WEALTHS);
				falador.addAlternates(ItemID.POH_TABLET_FALADORTELEPORT);
				return falador;
			case CATHERBY:
				ItemRequirement catherby = new ItemRequirement("Catherby / Camelot teleport", ItemID.LUNAR_TABLET_CATHERBY_TELEPORT);
				catherby.addAlternates(ItemID.POH_TABLET_CAMELOTTELEPORT);
				return catherby;
			case TAI_BWO_WANNAI: return new ItemRequirement("Tai bwo wannai teleport", ItemID.TELEPORTSCROLL_TAIBWO);
			case LLETYA: return new ItemRequirement("Lletya teleport crystal", com.questhelper.collections.ItemCollections.TELEPORT_CRYSTAL);
			case FOSSIL_ISLAND: return new ItemRequirement("Digsite pendant", com.questhelper.collections.ItemCollections.DIGSITE_PENDANTS);
			case NEMUS: case KASTORI: return new ItemRequirement("Pendant of Ates", ItemID.PENDANT_OF_ATES);
			default: throw new IllegalArgumentException("No direct item for " + destination);
		}
	}

	private static void add(Map<Integer, ItemRequirement> items, ItemRequirement item, boolean consumed)
	{
		ItemRequirement previous = items.get(item.getId());
		if (previous == null) items.put(item.getId(), item);
		else previous.setQuantity(consumed ? previous.getQuantity() + item.getQuantity() : Math.max(previous.getQuantity(), item.getQuantity()));
	}
}
