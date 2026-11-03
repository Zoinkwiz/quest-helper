package com.questhelper.helpers.mischelpers.farmruns;

import com.questhelper.MockedTest;
import com.questhelper.QuestHelperConfig;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.plugins.timetracking.farming.CompostState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import static com.questhelper.helpers.mischelpers.farmruns.TreeRunOptions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TreeRunTest extends MockedTest
{
	private TreeRun run;
	private FarmingHandler prediction;
	private FarmingWorld world;
	private final Map<String, String> values = new HashMap<>();

	@BeforeEach
	void setupRun() throws Exception
	{
		when(configManager.getRSProfileConfiguration(eq(QuestHelperConfig.QUEST_BACKGROUND_GROUP), anyString()))
			.thenAnswer(invocation -> values.get(invocation.getArgument(1)));
		when(configManager.getRSProfileKey()).thenReturn("test-profile");
		doAnswer(invocation -> { values.put(invocation.getArgument(1), invocation.getArgument(2).toString()); return null; })
			.when(configManager).setRSProfileConfiguration(eq(QuestHelperConfig.QUEST_BACKGROUND_GROUP), anyString(), any());
		values.put("payOrCutTree", "PAY");
		values.put("payOrCompostTree", "COMPOST");
		when(client.getRealSkillLevel(Skill.FARMING)).thenReturn(85);
		when(client.getBoostedSkillLevel(Skill.FARMING)).thenReturn(85);
		doNothing().when(questHelperPlugin).refreshBank();
		run = new TreeRun();
		injector.injectMembers(run);
		run.setQuestHelperPlugin(questHelperPlugin);
		run.loadStep();
		world = injector.getInstance(FarmingWorld.class);
		prediction = mock(FarmingHandler.class);
		when(prediction.predictPatch(any(FarmingPatch.class))).thenReturn(CropState.UNCHECKED);
		Field field = TreeRun.class.getDeclaredField("farmingHandler");
		field.setAccessible(true);
		field.set(run, prediction);
		run.onGameTick(new GameTick());
	}

	private FarmingPatch lumbridge()
	{
		return world.getTabs().values().stream().flatMap(java.util.Collection::stream)
			.filter(p -> p.getImplementation() == PatchImplementation.TREE && p.getRegion().getName().equals("Lumbridge"))
			.findFirst().orElseThrow();
	}

	@Test
	void disablingCategoryRemovesItsSaplingsAndCompostDemand()
	{
		int before = run.allTreeSaplings.getQuantity();
		assertTrue(before > 0);
		values.put(Category.REGULAR.key(), Inclusion.SKIP.name());
		run.onGameTick(new GameTick());
		assertEquals(0, run.allTreeSaplings.getQuantity());
		assertFalse(run.allTreeSaplings.shouldDisplayText(client));
		assertTrue(run.lumbridgeStates.getIsGrowing().check(client));
	}

	@Test
	void manuallyLockedPatchDoesNotCountASapling()
	{
		int before = run.allTreeSaplings.getQuantity();
		run.lumbridgeStep.setLockedManually(true);
		run.onGameTick(new GameTick());
		assertEquals(before - 1, run.allTreeSaplings.getQuantity());
	}

	@Test
	void individualUltracompostHasOneBucketPerPlannedPatch()
	{
		values.put(PREFIX + "CompostSource", CompostSource.INDIVIDUAL_BUCKETS.name());
		run.onGameTick(new GameTick());
		int expected = run.allTreeSaplings.getQuantity() + run.allFruitSaplings.getQuantity() + run.allHardwoodSaplings.getQuantity() + run.allCalquatSaplings.getQuantity();
		assertEquals(ItemID.BUCKET_ULTRACOMPOST, run.compost.getId());
		assertEquals(expected, run.compost.getQuantity());
	}

	@Test
	void neitherModeDoesNotAskForCompost()
	{
		values.put("payOrCompostTree", "NEITHER");
		run.onGameTick(new GameTick());
		assertFalse(run.compost.shouldDisplayText(client));
	}

	@Test
	void leprechaunModeDoesNotAskForCarriedBuckets()
	{
		values.put(PREFIX + "CompostSource", CompostSource.LEPRECHAUN_STORAGE.name());
		run.onGameTick(new GameTick());
		assertFalse(run.compost.shouldDisplayText(client));
	}

	@Test
	void freshlyPlantedTreeWaitsForCompostButDoesNotAskForAnotherSapling()
	{
		FarmingPatch patch = lumbridge();
		int before = run.allTreeSaplings.getQuantity();
		when(prediction.predictPatch(patch)).thenReturn(CropState.EMPTY);
		run.onGameTick(new GameTick());
		when(prediction.predictPatch(patch)).thenReturn(CropState.GROWING);
		run.onGameTick(new GameTick());
		assertFalse(run.lumbridgeStates.getIsGrowing().check(client));
		assertEquals(before - 1, run.allTreeSaplings.getQuantity());
		when(configManager.getRSProfileConfiguration(eq("timetracking"), eq(patch.configKey() + ".compost"), eq(CompostState.class))).thenReturn(CompostState.ULTRACOMPOST);
		run.onGameTick(new GameTick());
		assertTrue(run.lumbridgeStates.getIsGrowing().check(client));
	}

	@Test
	void existingGrowingTreesDoNotForceAnExtraCompostTrip()
	{
		when(prediction.predictPatch(lumbridge())).thenReturn(CropState.GROWING);
		run.onGameTick(new GameTick());
		assertTrue(run.lumbridgeStates.getIsGrowing().check(client));
	}

	@Test
	void growingInaccessiblePatchCannotMakeCountsNegative()
	{
		when(client.getRealSkillLevel(Skill.FARMING)).thenReturn(1);
		when(client.getBoostedSkillLevel(Skill.FARMING)).thenReturn(1);
		when(prediction.predictPatch(any(FarmingPatch.class))).thenReturn(CropState.GROWING);
		run.onGameTick(new GameTick());
		assertEquals(0, run.allTreeSaplings.getQuantity());
		assertEquals(0, run.allFruitSaplings.getQuantity());
	}
	@Test
	void pohChoiceActuallyReplacesTheVisibleBankRecommendations()
	{
		when(prediction.predictPatch(any(FarmingPatch.class))).thenReturn(CropState.GROWING);
		FarmingPatch brim = world.getTabs().values().stream().flatMap(java.util.Collection::stream)
			.filter(p -> p.getImplementation() == PatchImplementation.FRUIT_TREE && p.getRegion().getName().equals("Brimhaven"))
			.findFirst().orElseThrow();
		when(prediction.predictPatch(brim)).thenReturn(CropState.UNCHECKED);
		values.put(Destination.BRIMHAVEN.key(), Travel.POH_SPIRIT_TREE.name());
		run.onGameTick(new GameTick());
		java.util.List<com.questhelper.requirements.item.ItemRequirement> visible = run.getItemRecommended().stream()
			.filter(item -> item.shouldDisplayText(client)).collect(java.util.stream.Collectors.toList());
		assertEquals(2, visible.size());
		assertTrue(visible.stream().anyMatch(item -> item == run.compost));
		assertTrue(visible.stream().anyMatch(item -> item.getId() == ItemID.POH_TABLET_TELEPORTTOHOUSE && item.getQuantity() == 1));
	}

	@Test
	void boatFareIsAddedToRemovalCoins()
	{
		when(prediction.predictPatch(any(FarmingPatch.class))).thenReturn(CropState.GROWING);
		FarmingPatch brim = world.getTabs().values().stream().flatMap(java.util.Collection::stream)
			.filter(p -> p.getImplementation() == PatchImplementation.FRUIT_TREE && p.getRegion().getName().equals("Brimhaven"))
			.findFirst().orElseThrow();
		when(prediction.predictPatch(brim)).thenReturn(CropState.UNCHECKED);
		run.onGameTick(new GameTick());
		assertEquals(230, run.coins.getQuantity());
		assertTrue(run.coins.shouldDisplayText(client));
	}

	@Test
	void everyCategoryCanBeDisabledWithoutLeavingNegativeOrPhantomQuantities()
	{
		for (Category category : Category.values()) values.put(category.key(), Inclusion.SKIP.name());
		run.onGameTick(new GameTick());
		assertEquals(0, run.allTreeSaplings.getQuantity());
		assertEquals(0, run.allFruitSaplings.getQuantity());
		assertEquals(0, run.allHardwoodSaplings.getQuantity());
		assertEquals(0, run.allCalquatSaplings.getQuantity());
		assertFalse(run.compost.shouldDisplayText(client));
		assertFalse(run.coins.shouldDisplayText(client));
		assertTrue(run.getItemRecommended().stream().noneMatch(item -> item.shouldDisplayText(client)));
	}

	@Test
	void legacyBooleanDefaultMigratesToThePayChoiceShownInDropdown()
	{
		values.put("payOrCutTree", "false");
		run.uninitializeRequirements();
		run.loadStep();
		run.onGameTick(new GameTick());
		assertEquals("PAY", values.get("payOrCutTree"));
		assertFalse(run.axe.shouldDisplayText(client));
		assertTrue(run.coins.shouldDisplayText(client));
		assertTrue(run.coins.getQuantity() > 0);
	}

	@Test
	void rakeCanBeSkippedWithoutAutoWeed()
	{
		assertTrue(run.rake.shouldDisplayText(client));
		values.put(PREFIX + "Rake", Rake.SKIP.name());
		run.onGameTick(new GameTick());
		assertFalse(run.rake.shouldDisplayText(client));
		assertFalse(run.getItemRequirements().contains(run.rake));
	}

	@Test
	void compostOnMatureTreeDoesNotRemoveSupplyForItsReplacement()
	{
		for (FarmingPatch patch : world.getTabs().values().stream().flatMap(java.util.Collection::stream).collect(java.util.stream.Collectors.toList()))
			when(configManager.getRSProfileConfiguration(eq("timetracking"), eq(patch.configKey() + ".compost"), eq(CompostState.class)))
				.thenReturn(CompostState.ULTRACOMPOST);
		values.put(PREFIX + "CompostSource", CompostSource.INDIVIDUAL_BUCKETS.name());
		run.onGameTick(new GameTick());
		int patches = run.allTreeSaplings.getQuantity() + run.allFruitSaplings.getQuantity() + run.allHardwoodSaplings.getQuantity() + run.allCalquatSaplings.getQuantity();
		assertEquals(patches, run.compost.getQuantity());
		assertTrue(run.getItemRecommended().contains(run.compost));
	}

	@Test
	void checkedTreeSelectsTheGardenerWhenPayingForRemoval()
	{
		when(prediction.predictPatch(lumbridge())).thenReturn(CropState.HARVESTABLE);
		run.onGameTick(new GameTick());
		com.questhelper.steps.QuestStep next = run.lumbridgeStep.getStepsMap().entrySet().stream()
			.filter(e -> e.getKey() != null && e.getKey().check(client)).findFirst().orElseThrow().getValue();
		assertSame(run.lumbridgeTreePatchClear, next);
		assertTrue(next instanceof com.questhelper.steps.NpcStep);
	}

	@Test
	void realBankSectionsShowOnlySelectedSupplies()
	{
		values.put(Destination.VARROCK.key(), Travel.POH_NEXUS.name());
		values.put(Destination.NEMUS.key(), Travel.POH_FAIRY_RING.name());
		values.put(Destination.KASTORI.key(), Travel.POH_FAIRY_RING.name());
		values.put(PREFIX + "Rake", Rake.SKIP.name());
		run.onGameTick(new GameTick());
		doReturn(run).when(questHelperPlugin).getSelectedQuest();
		com.questhelper.bank.banktab.QuestHelperBankTagService bank = injector.getInstance(com.questhelper.bank.banktab.QuestHelperBankTagService.class);
		java.util.List<com.questhelper.bank.banktab.BankTabItem> items = bank.getPluginBankTagItemsForSections(false).stream()
			.flatMap(section -> section.getItems().stream()).collect(java.util.stream.Collectors.toList());
		assertTrue(items.stream().anyMatch(item -> item.getItemRequirement() == run.coins && item.getQuantity() > 0));
		assertTrue(items.stream().anyMatch(item -> item.getItemRequirement() == run.compost));
		assertFalse(items.stream().anyMatch(item -> item.getItemIDs().contains(ItemID.POH_TABLET_VARROCKTELEPORT)));
		assertFalse(items.stream().anyMatch(item -> item.getItemIDs().contains(ItemID.PENDANT_OF_ATES)));
		assertFalse(items.stream().anyMatch(item -> item.getItemRequirement() == run.axe || item.getItemRequirement() == run.rake));
	}


	@Test
	void noneSaplingSelectionDisablesRegularTreesWithoutInvalidItemIds()
	{
		values.put("treeSaplings", "NONE");
		run.onGameTick(new GameTick());
		assertEquals(0, run.allTreeSaplings.getQuantity());
		assertEquals(0, run.allProtectionItemTree.getQuantity());
		assertTrue(run.lumbridgeStates.getIsGrowing().check(client));
		assertTrue(run.allTreeSaplings.getId() > 0);
		assertTrue(run.treeSapling.getId() > 0);
	}

	@Test
	void noneCalquatSelectionDisablesCalquatSupplies()
	{
		values.put("calquatTreeSaplings", "NONE");
		run.onGameTick(new GameTick());
		assertEquals(0, run.allCalquatSaplings.getQuantity());
		assertEquals(0, run.allProtectionItemCalquat.getQuantity());
		assertTrue(run.taiBwoWannaiStates.getIsGrowing().check(client));
	}

	@Test
	void calquatFarmingLevelRequirementPreventsPlanningBelow72()
	{
		when(client.getRealSkillLevel(Skill.FARMING)).thenReturn(71);
		when(client.getBoostedSkillLevel(Skill.FARMING)).thenReturn(71);
		run.onGameTick(new GameTick());
		assertEquals(0, run.allCalquatSaplings.getQuantity());
		assertTrue(run.taiBwoWannaiStates.getIsGrowing().check(client));
	}
	private net.runelite.api.Player placePlayer(net.runelite.api.coords.WorldPoint point)
	{
		net.runelite.api.Player player = mock(net.runelite.api.Player.class);
		when(client.getLocalPlayer()).thenReturn(player);
		when(player.getWorldLocation()).thenReturn(point);
		return player;
	}

	@Test
	void bothStrongholdPatchesUseWalkingInstructionsOnceInsideStronghold()
	{
		values.put(Destination.STRONGHOLD.key(), Travel.POH_SPIRIT_TREE.name());
		placePlayer(run.gnomeStrongholdFruitTreePatchPlant.getDefinedPoint().getWorldPoint());
		run.onGameTick(new GameTick());
		assertTrue(run.gnomeStrongholdTreePatchPlant.getText().stream().anyMatch(t -> t.contains("no teleport needed")));
		assertTrue(run.gnomeStrongholdFruitTreePatchPlant.getText().stream().anyMatch(t -> t.contains("no teleport needed")));
		assertTrue(run.gnomeStrongholdTreePatchPlant.getTeleport().isEmpty());
		assertTrue(run.gnomeStrongholdFruitTreePatchPlant.getTeleport().isEmpty());
	}

	@Test
	void leavingStrongholdRestoresSelectedArrivalDirections()
	{
		values.put(Destination.STRONGHOLD.key(), Travel.POH_SPIRIT_TREE.name());
		net.runelite.api.Player player = placePlayer(run.gnomeStrongholdTreePatchPlant.getDefinedPoint().getWorldPoint());
		run.onGameTick(new GameTick());
		when(player.getWorldLocation()).thenReturn(new net.runelite.api.coords.WorldPoint(3200, 3200, 0));
		run.onGameTick(new GameTick());
		assertTrue(run.gnomeStrongholdTreePatchPlant.getText().stream().anyMatch(t -> t.contains("POH spirit tree")));
		assertFalse(run.gnomeStrongholdTreePatchPlant.getTeleport().isEmpty());
	}

	@Test
	void catherbyRocksRouteWalksFromTaverleyWithoutAnotherTeleport()
	{
		values.put(Destination.CATHERBY.key(), Travel.WALK_FROM_TAVERLEY_ROCKS.name());
		when(client.getRealSkillLevel(Skill.AGILITY)).thenReturn(68);
		placePlayer(run.taverleyTreePatchPlant.getDefinedPoint().getWorldPoint());
		run.onGameTick(new GameTick());
		assertTrue(run.catherbyFruitTreePatchPlant.getText().stream().anyMatch(t -> t.contains("rocks") && t.contains("No teleport needed")));
		assertTrue(run.catherbyFruitTreePatchPlant.getTeleport().isEmpty());
	}

	@Test
	void catherbyRocksRouteExplainsMissingAgilityInsteadOfClaimingItIsUsable()
	{
		values.put(Destination.CATHERBY.key(), Travel.WALK_FROM_TAVERLEY_ROCKS.name());
		placePlayer(run.taverleyTreePatchPlant.getDefinedPoint().getWorldPoint());
		run.onGameTick(new GameTick());
		assertTrue(run.catherbyFruitTreePatchPlant.getText().stream().anyMatch(t -> t.contains("requires 68 Agility")));
	}

	@Test
	void ckrArrivalShowsOnlyTheLocalWalkAndForbidsCompetingTeleports()
	{
		values.put(Destination.TAI_BWO_WANNAI.key(), Travel.POH_FAIRY_RING.name());
		placePlayer(new net.runelite.api.coords.WorldPoint(2801, 3003, 0));
		run.setConfig(questHelperConfig);
		when(questHelperConfig.useShortestPath()).thenReturn(true);
		run.onGameTick(new GameTick());
		assertTrue(run.taiBwoWannaiCalquatPatchCheckHealth.getTeleport().isEmpty());
		assertTrue(run.taiBwoWannaiCalquatPatchCheckHealth.getText().stream().anyMatch(t -> t.contains("no teleport needed")));
		injector.injectMembers(run.taiBwoWannaiCalquatPatchCheckHealth);
		clearInvocations(eventBus);
		run.taiBwoWannaiCalquatPatchCheckHealth.setShortestPath();
		org.mockito.ArgumentCaptor<net.runelite.client.events.PluginMessage> message = org.mockito.ArgumentCaptor.forClass(net.runelite.client.events.PluginMessage.class);
		verify(eventBus).post(message.capture());
		Map<?, ?> overrides = (Map<?, ?>) message.getValue().getData().get("config");
		assertEquals("None", overrides.get("useTeleportationItems"));
		assertEquals(false, overrides.get("usePoh"));
		assertEquals(false, overrides.get("useTeleportationSpells"));
	}

	@Test
	void remoteFairyRingStepDoesNotAskShortestPathForAnUnrelatedTeleport()
	{
		values.put(Destination.TAI_BWO_WANNAI.key(), Travel.POH_FAIRY_RING.name());
		placePlayer(new net.runelite.api.coords.WorldPoint(3200, 3200, 0));
		run.setConfig(questHelperConfig);
		when(questHelperConfig.useShortestPath()).thenReturn(true);
		run.onGameTick(new GameTick());
		assertTrue(run.taiBwoWannaiCalquatPatchCheckHealth.getText().stream().anyMatch(t -> t.contains("CKR")));
		injector.injectMembers(run.taiBwoWannaiCalquatPatchCheckHealth);
		clearInvocations(eventBus);
		run.taiBwoWannaiCalquatPatchCheckHealth.setShortestPath();
		verifyNoInteractions(eventBus);
	}


	@Test
	void routeChangesRefreshOnTickWithoutAConfigEvent()
	{
		values.put(Destination.KASTORI.key(), Travel.MOUNTAIN_GUIDE_QUETZAL.name());
		values.put(Destination.NEMUS.key(), Travel.POH_FAIRY_RING.name());
		run.onGameTick(new GameTick());
		assertTrue(run.kastoriFruitTreePatchPlant.getText().stream().anyMatch(t -> t.contains("mountain guide")));
		values.put(Destination.KASTORI.key(), Travel.DIRECT_TELEPORT.name());
		run.onGameTick(new GameTick());
		assertTrue(run.kastoriFruitTreePatchPlant.getText().stream().anyMatch(t -> t.contains("Pendant of Ates")));
		assertFalse(run.kastoriFruitTreePatchPlant.getText().stream().anyMatch(t -> t.contains("mountain guide")));
	}

	@Test
	void spiritTreeArrivalDoesNotRecommendAnotherHouseTeleport()
	{
		values.put(Destination.STRONGHOLD.key(), Travel.POH_SPIRIT_TREE.name());
		placePlayer(new net.runelite.api.coords.WorldPoint(2461, 3444, 0));
		run.onGameTick(new GameTick());
		assertTrue(run.gnomeStrongholdTreePatchPlant.getTeleport().isEmpty());
		assertTrue(run.gnomeStrongholdFruitTreePatchPlant.getTeleport().isEmpty());
	}

	@Test
	void guideRouteContinuesFromAuburnvaleWithoutReturningToHouse()
	{
		values.put(Destination.KASTORI.key(), Travel.MOUNTAIN_GUIDE_QUETZAL.name());
		values.put(Destination.NEMUS.key(), Travel.POH_FAIRY_RING.name());
		placePlayer(run.auburnvaleTreePatchPlant.getDefinedPoint().getWorldPoint());
		run.onGameTick(new GameTick());
		assertTrue(run.kastoriFruitTreePatchPlant.getTeleport().isEmpty());
		assertTrue(run.kastoriFruitTreePatchPlant.getText().stream().anyMatch(t -> t.contains("no house teleport needed")));
	}
}
