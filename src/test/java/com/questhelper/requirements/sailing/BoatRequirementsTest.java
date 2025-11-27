package com.questhelper.requirements.sailing;

import com.google.gson.Gson;
import com.questhelper.statemanagement.boats.BoatSlotState;
import com.questhelper.statemanagement.boats.BoatStateStore;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.config.ConfigManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BoatRequirementsTest
{
	private static final String PROFILE = "profile";

	private BoatStateStore store;

	@BeforeEach
	void setup()
	{
		ConfigManager configManager = mock(ConfigManager.class);
		when(configManager.getRSProfileKey()).thenReturn(PROFILE);
		when(configManager.getRSProfileConfiguration(anyString(), anyString())).thenReturn(null);
		doNothing().when(configManager).setConfiguration(anyString(), anyString(), anyString(), anyString());
		store = new BoatStateStore(configManager, new Gson());
		store.initialize();
		BoatStateStore.instance = store;
	}

	@Test
	void iceRequirementPassesWhenBoatPresent()
	{
		store.updateBoat(BoatSlotState.builder().boatId(1).iceResistant(true).build());
		var requirement = new HasBoatResistanceRequirement(BoatResistanceType.ICE, false, 1);
		assertTrue(requirement.check(null));
	}

	@Test
	void rapidRequirementHonorsMinimumLevel()
	{
		store.updateBoat(BoatSlotState.builder()
			.boatId(7)
			.rapidResistanceLevel(1)
			.build());

		var levelTwoRequirement = new HasBoatResistanceRequirement(BoatResistanceType.RAPID, false, 2);
		assertFalse(levelTwoRequirement.check(null));

		var levelOneRequirement = new HasBoatResistanceRequirement(BoatResistanceType.RAPID, false, 1);
		assertTrue(levelOneRequirement.check(null));
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
		store.updateBoat(BoatSlotState.builder().boatId(1).iceResistant(false).build());
		store.updateBoat(BoatSlotState.builder().boatId(2).iceResistant(true).build());

		var requirement = new HasBoatResistanceRequirement(BoatResistanceType.ICE, false, 1);
		assertTrue(requirement.check(null));
	}

	@Test
	void currentShipRequirementOnlyChecksCurrentBoat()
	{
		Client client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		when(client.getVarbitValue(eq(VarbitID.SAILING_BOARDED_BOAT))).thenReturn(1);
		when(client.getVarbitValue(eq(VarbitID.SAILING_SIDEPANEL_BOAT_ICYSEAS_RESISTANT))).thenReturn(0);

		// Store has boats with resistance, but current boat (from varbits) doesn't
		store.updateBoat(BoatSlotState.builder().boatId(1).iceResistant(false).build());
		store.updateBoat(BoatSlotState.builder().boatId(2).iceResistant(true).build());

		var requirement = new HasBoatResistanceRequirement(BoatResistanceType.ICE, true, 1);
		assertFalse(requirement.check(client));
	}
}

