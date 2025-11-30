package com.questhelper.requirements.sailing;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.gameval.VarbitID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BoatRequirementsTest
{
	@Test
	void iceRequirementPassesWhenBoatPresent()
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_1_BRAZIER))).thenReturn(1);

		var requirement = new HasBoatResistanceRequirement(BoatResistanceType.ICE, false, 1);
		assertTrue(requirement.check(client));
	}

	@Test
	void rapidRequirementHonorsMinimumLevel()
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		// Boat 1 has rapid resistance level 1 (SAIL value 1-2 = level 1)
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_1_SAIL))).thenReturn(1);

		var levelTwoRequirement = new HasBoatResistanceRequirement(BoatResistanceType.RAPID, false, 2);
		assertFalse(levelTwoRequirement.check(client));

		var levelOneRequirement = new HasBoatResistanceRequirement(BoatResistanceType.RAPID, false, 1);
		assertTrue(levelOneRequirement.check(client));
	}

	@Test
	void currentBoatRequirementPassesWhenCurrentBoatHasResistance()
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOARDED_BOAT))).thenReturn(1);
		when(client.getVarbitValue(eq(VarbitID.SAILING_SIDEPANEL_BOAT_ICYSEAS_RESISTANT))).thenReturn(1);

		var requirement = new HasBoatResistanceRequirement(BoatResistanceType.ICE, true, 1);
		assertTrue(requirement.check(client));
	}

	@Test
	void currentBoatRequirementFailsWhenCurrentBoatLacksResistance()
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOARDED_BOAT))).thenReturn(1);
		when(client.getVarbitValue(eq(VarbitID.SAILING_SIDEPANEL_BOAT_ICYSEAS_RESISTANT))).thenReturn(0);

		var requirement = new HasBoatResistanceRequirement(BoatResistanceType.ICE, true, 1);
		assertFalse(requirement.check(client));
	}

	@Test
	void currentBoatRequirementFailsWhenNotOnBoat()
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOARDED_BOAT))).thenReturn(0);

		var requirement = new HasBoatResistanceRequirement(BoatResistanceType.ICE, true, 1);
		assertFalse(requirement.check(client));
	}

	@Test
	void currentBoatRequirementFailsWhenNotLoggedIn()
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGIN_SCREEN);

		var requirement = new HasBoatResistanceRequirement(BoatResistanceType.ICE, true, 1);
		assertFalse(requirement.check(client));
	}

	@Test
	void currentBoatRapidRequirementHonorsMinimumLevel()
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOARDED_BOAT))).thenReturn(1);
		when(client.getVarbitValue(eq(VarbitID.SAILING_SIDEPANEL_BOAT_RAPIDRESISTANCE))).thenReturn(1);

		var levelTwoRequirement = new HasBoatResistanceRequirement(BoatResistanceType.RAPID, true, 2);
		assertFalse(levelTwoRequirement.check(client));

		var levelOneRequirement = new HasBoatResistanceRequirement(BoatResistanceType.RAPID, true, 1);
		assertTrue(levelOneRequirement.check(client));
	}

	@Test
	void anyShipRequirementChecksAllBoats()
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		// Boat 1 has no ice resistance
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_1_BRAZIER))).thenReturn(0);
		// Boat 2 has ice resistance
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_2_BRAZIER))).thenReturn(1);

		var requirement = new HasBoatResistanceRequirement(BoatResistanceType.ICE, false, 1);
		assertTrue(requirement.check(client));
	}

	@Test
	void currentShipRequirementOnlyChecksCurrentBoat()
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOARDED_BOAT))).thenReturn(1);
		// Current boat has no ice resistance
		when(client.getVarbitValue(eq(VarbitID.SAILING_SIDEPANEL_BOAT_ICYSEAS_RESISTANT))).thenReturn(0);
		// But boat 2 (stored) has ice resistance
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_2_BRAZIER))).thenReturn(1);

		var requirement = new HasBoatResistanceRequirement(BoatResistanceType.ICE, true, 1);
		assertFalse(requirement.check(client));
	}

	@Test
	void anyShipRequirementFailsWhenNoBoatHasResistance()
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		// All boats have no ice resistance
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_1_BRAZIER))).thenReturn(0);
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_2_BRAZIER))).thenReturn(0);
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_3_BRAZIER))).thenReturn(0);
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_4_BRAZIER))).thenReturn(0);
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_5_BRAZIER))).thenReturn(0);

		var requirement = new HasBoatResistanceRequirement(BoatResistanceType.ICE, false, 1);
		assertFalse(requirement.check(client));
	}

	@Test
	void rapidRequirementLevel2()
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		// Boat 1 has rapid resistance level 2 (SAIL value 3-6 = level 2)
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_1_SAIL))).thenReturn(3);

		var levelTwoRequirement = new HasBoatResistanceRequirement(BoatResistanceType.RAPID, false, 2);
		assertTrue(levelTwoRequirement.check(client));

		var levelOneRequirement = new HasBoatResistanceRequirement(BoatResistanceType.RAPID, false, 1);
		assertTrue(levelOneRequirement.check(client));
	}

	@Test
	void stormRequirementHonorsMinimumLevel()
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		// Boat 1 has storm resistance level 1 (HULL value 1-3 = level 1)
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_1_HULL))).thenReturn(2);

		var levelTwoRequirement = new HasBoatResistanceRequirement(BoatResistanceType.STORM, false, 2);
		assertFalse(levelTwoRequirement.check(client));

		var levelOneRequirement = new HasBoatResistanceRequirement(BoatResistanceType.STORM, false, 1);
		assertTrue(levelOneRequirement.check(client));
	}

	@Test
	void stormRequirementLevel2()
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		// Boat 1 has storm resistance level 2 (HULL value 4-6 = level 2)
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_1_HULL))).thenReturn(4);

		var levelTwoRequirement = new HasBoatResistanceRequirement(BoatResistanceType.STORM, false, 2);
		assertTrue(levelTwoRequirement.check(client));

		var levelOneRequirement = new HasBoatResistanceRequirement(BoatResistanceType.STORM, false, 1);
		assertTrue(levelOneRequirement.check(client));
	}

	@Test
	void kelpResistance()
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		// Boat 1 has kelp resistance (STEERING >= 4)
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_1_STEERING))).thenReturn(4);

		var requirement = new HasBoatResistanceRequirement(BoatResistanceType.KELP, false, 1);
		assertTrue(requirement.check(client));
	}

	@Test
	void kelpResistanceFailsWhenSteeringTooLow()
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		// Boat 1 does not have kelp resistance (STEERING < 4)
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_1_STEERING))).thenReturn(3);

		var requirement = new HasBoatResistanceRequirement(BoatResistanceType.KELP, false, 1);
		assertFalse(requirement.check(client));
	}

	@Test
	void crystalResistance()
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		// Boat 1 has crystal resistance (KEEL >= 4)
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_1_KEEL))).thenReturn(4);

		var requirement = new HasBoatResistanceRequirement(BoatResistanceType.CRYSTAL, false, 1);
		assertTrue(requirement.check(client));
	}

	@Test
	void crystalResistanceFailsWhenKeelTooLow()
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		// Boat 1 does not have crystal resistance (KEEL < 4)
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_1_KEEL))).thenReturn(3);

		var requirement = new HasBoatResistanceRequirement(BoatResistanceType.CRYSTAL, false, 1);
		assertFalse(requirement.check(client));
	}

	@Test
	void fetidWaterResistanceType0()
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		// Type 0 boat cannot have fetid water resistance
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_1_TYPE))).thenReturn(0);

		var requirement = new HasBoatResistanceRequirement(BoatResistanceType.FETID_WATER, false, 1);
		assertFalse(requirement.check(client));
	}

	@Test
	void fetidWaterResistanceType1()
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		// Type 1 boat with HOTSPOT_5 = 1 has fetid water resistance
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_1_TYPE))).thenReturn(1);
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_1_HOTSPOT_5))).thenReturn(1);

		var requirement = new HasBoatResistanceRequirement(BoatResistanceType.FETID_WATER, false, 1);
		assertTrue(requirement.check(client));
	}

	@Test
	void fetidWaterResistanceType1FailsWhenHotspot5NotSet()
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		// Type 1 boat but HOTSPOT_5 = 0 (no resistance)
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_1_TYPE))).thenReturn(1);
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_1_HOTSPOT_5))).thenReturn(0);

		var requirement = new HasBoatResistanceRequirement(BoatResistanceType.FETID_WATER, false, 1);
		assertFalse(requirement.check(client));
	}

	@Test
	void fetidWaterResistanceType2()
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		// Type 2 boat with HOTSPOT_9 = 1 has fetid water resistance
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_1_TYPE))).thenReturn(2);
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_1_HOTSPOT_9))).thenReturn(1);

		var requirement = new HasBoatResistanceRequirement(BoatResistanceType.FETID_WATER, false, 1);
		assertTrue(requirement.check(client));
	}

	@Test
	void fetidWaterResistanceType2FailsWhenHotspot9NotSet()
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		// Type 2 boat but HOTSPOT_9 = 0 (no resistance)
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_1_TYPE))).thenReturn(2);
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOAT_1_HOTSPOT_9))).thenReturn(0);

		var requirement = new HasBoatResistanceRequirement(BoatResistanceType.FETID_WATER, false, 1);
		assertFalse(requirement.check(client));
	}
}

