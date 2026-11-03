package com.questhelper.helpers.mischelpers.farmruns;

import com.questhelper.QuestHelperConfig;
import com.questhelper.requirements.item.ItemRequirement;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.config.ConfigManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.questhelper.helpers.mischelpers.farmruns.TreeRunOptions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TreeRunOptionsTest
{
	private TreeRunOptions options;
	private final Map<String, String> values = new HashMap<>();

	@BeforeEach
	void setup()
	{
		ConfigManager config = mock(ConfigManager.class);
		when(config.getRSProfileConfiguration(eq(QuestHelperConfig.QUEST_BACKGROUND_GROUP), anyString()))
			.thenAnswer(invocation -> values.get(invocation.getArgument(1)));
		options = new TreeRunOptions(config);
	}

	private int count(List<ItemRequirement> items, int id)
	{
		return items.stream().filter(i -> i.getId() == id).mapToInt(ItemRequirement::getQuantity).sum();
	}

	@Test
	void brimhavenDefaultHasTransportAndBoatFare()
	{
		List<ItemRequirement> items = options.travelItems(Arrays.asList(Destination.BRIMHAVEN));
		assertEquals(1, count(items, ItemID.POH_TABLET_ARDOUGNETELEPORT));
		assertEquals(30, count(items, ItemID.COINS));
		assertTrue(options.instruction(Destination.BRIMHAVEN).contains("Captain Barnaby"));
	}

	@Test
	void taiBwoWannaiDirectScrollIsInBankList()
	{
		values.put(Destination.TAI_BWO_WANNAI.key(), Travel.DIRECT_TELEPORT.name());
		List<ItemRequirement> items = options.travelItems(Arrays.asList(Destination.TAI_BWO_WANNAI));
		assertEquals(1, count(items, ItemID.TELEPORTSCROLL_TAIBWO));
		assertEquals(0, count(items, ItemID.POH_TABLET_ARDOUGNETELEPORT));
	}

	@Test
	void sharedHouseTravelReplacesSeparateTeleportsAndCountsVisits()
	{
		values.put(Destination.GUILD.key(), Travel.POH_JEWELLERY_BOX.name());
		values.put(Destination.CATHERBY.key(), Travel.POH_NEXUS.name());
		values.put(Destination.BRIMHAVEN.key(), Travel.POH_SPIRIT_TREE.name());
		List<ItemRequirement> items = options.travelItems(Arrays.asList(Destination.GUILD, Destination.CATHERBY, Destination.BRIMHAVEN));
		assertEquals(1, items.size());
		assertEquals(3, count(items, ItemID.POH_TABLET_TELEPORTTOHOUSE));
	}

	@Test
	void walkingToCalquatStillIncludesBrimhavenJourneyWhenFruitPatchIsSkipped()
	{
		values.put(Destination.BRIMHAVEN.key(), Travel.POH_SPIRIT_TREE.name());
		assertEquals(1, count(options.travelItems(Arrays.asList(Destination.TAI_BWO_WANNAI)), ItemID.POH_TABLET_TELEPORTTOHOUSE));
		assertEquals(1, count(options.travelItems(Arrays.asList(Destination.BRIMHAVEN, Destination.TAI_BWO_WANNAI)), ItemID.POH_TABLET_TELEPORTTOHOUSE));
	}

	@Test
	void walkingToTaverleyRespectsFaladorTravelChoice()
	{
		values.put(Destination.FALADOR.key(), Travel.POH_NEXUS.name());
		List<ItemRequirement> items = options.travelItems(Arrays.asList(Destination.TAVERLEY));
		assertEquals(1, items.size());
		assertEquals(1, count(items, ItemID.POH_TABLET_TELEPORTTOHOUSE));
	}

	@Test
	void houseSpellCountsRunesForEveryVisit()
	{
		values.put(PREFIX + "HouseEntry", HouseEntry.HOUSE_SPELL.name());
		values.put(Destination.CATHERBY.key(), Travel.POH_NEXUS.name());
		List<ItemRequirement> items = options.travelItems(Arrays.asList(Destination.CATHERBY));
		assertEquals(1, count(items, ItemID.LAWRUNE));
		assertEquals(1, count(items, ItemID.AIRRUNE));
		assertEquals(1, count(items, ItemID.EARTHRUNE));
		assertEquals(0, count(items, ItemID.POH_TABLET_TELEPORTTOHOUSE));
	}

	@Test
	void atesSharedBetweenBothDestinations()
	{
		assertEquals(1, count(options.travelItems(Arrays.asList(Destination.NEMUS, Destination.KASTORI)), ItemID.PENDANT_OF_ATES));
	}

	@Test
	void everyOfferedTravelOptionHasInstructionsAndSupplies()
	{
		for (Destination destination : Destination.values())
		{
			for (Travel travel : destination.choices)
			{
				values.put(destination.key(), travel.name());
				assertFalse(options.instruction(destination).isEmpty());
				assertFalse(options.travelItems(Arrays.asList(destination)).isEmpty(), destination + ": " + travel);
			}
		}
	}

	@Test
	void unsupportedOrCorruptSavedChoiceFallsBackToSupportedDefault()
	{
		values.put(Destination.BRIMHAVEN.key(), Travel.POH_NEXUS.name());
		assertEquals(Travel.ARDOUGNE_BOAT, options.travel(Destination.BRIMHAVEN));
		values.put(Destination.BRIMHAVEN.key(), "REMOVED_SETTING");
		assertEquals(Travel.ARDOUGNE_BOAT, options.travel(Destination.BRIMHAVEN));
	}

	@Test
	void houseLocatedInBrimhavenUsesOrdinaryHouseEntry()
	{
		values.put(Destination.BRIMHAVEN.key(), Travel.POH_OUTSIDE.name());
		List<ItemRequirement> items = options.travelItems(Arrays.asList(Destination.BRIMHAVEN));
		assertEquals(1, count(items, ItemID.POH_TABLET_TELEPORTTOHOUSE));
		assertEquals(0, count(items, ItemID.NZONE_TELETAB_BRIMHAVEN));
		assertEquals(0, count(items, ItemID.COINS));
		assertTrue(options.instruction(Destination.BRIMHAVEN).contains("Requires your house to be located in Brimhaven"));
	}

	@Test
	void geSpiritTreeJourneyHonoursVarrockNexusSelection()
	{
		values.put(Destination.VARROCK.key(), Travel.POH_NEXUS.name());
		List<ItemRequirement> items = options.travelItems(Arrays.asList(Destination.STRONGHOLD, Destination.VILLAGE));
		assertEquals(0, count(items, ItemID.POH_TABLET_VARROCKTELEPORT));
		assertEquals(1, count(items, ItemID.POH_TABLET_TELEPORTTOHOUSE));
		assertTrue(options.instruction(Destination.STRONGHOLD).contains("POH nexus"));
	}

	@Test
	void fairyRingRoutesRemoveAtesForBothVarlamoreDestinations()
	{
		values.put(Destination.KASTORI.key(), Travel.POH_FAIRY_RING.name());
		values.put(Destination.NEMUS.key(), Travel.POH_FAIRY_RING.name());
		List<ItemRequirement> items = options.travelItems(Arrays.asList(Destination.KASTORI, Destination.NEMUS));
		assertEquals(0, count(items, ItemID.PENDANT_OF_ATES));
		assertEquals(2, count(items, ItemID.POH_TABLET_TELEPORTTOHOUSE));
		assertTrue(options.instruction(Destination.KASTORI).contains("AIS"));
		assertTrue(options.instruction(Destination.KASTORI).contains("Achilka"));
	}
	@Test
	void catherbyRocksCountsTheSelectedTaverleyArrivalOnlyOnce()
	{
		values.put(Destination.CATHERBY.key(), Travel.WALK_FROM_TAVERLEY_ROCKS.name());
		values.put(Destination.TAVERLEY.key(), Travel.REDIRECTED_HOUSE_TABLET.name());
		List<ItemRequirement> items = options.travelItems(Arrays.asList(Destination.TAVERLEY, Destination.CATHERBY));
		assertEquals(1, count(items, ItemID.NZONE_TELETAB_TAVERLEY));
		assertEquals(0, count(items, ItemID.LUNAR_TABLET_CATHERBY_TELEPORT));
		assertEquals(0, count(items, ItemID.POH_TABLET_TELEPORTTOHOUSE));
		assertEquals(1, count(options.travelItems(Arrays.asList(Destination.CATHERBY)), ItemID.NZONE_TELETAB_TAVERLEY));
	}

	@Test
	void catherbyRocksResolvesWalkingTaverleyDependencyThroughFalador()
	{
		values.put(Destination.CATHERBY.key(), Travel.WALK_FROM_TAVERLEY_ROCKS.name());
		values.put(Destination.TAVERLEY.key(), Travel.WALK_FROM_FALADOR.name());
		values.put(Destination.FALADOR.key(), Travel.POH_NEXUS.name());
		assertEquals(1, count(options.travelItems(Arrays.asList(Destination.CATHERBY)), ItemID.POH_TABLET_TELEPORTTOHOUSE));
	}

}
