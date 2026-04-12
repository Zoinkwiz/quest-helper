package com.questhelper.managers;

import net.runelite.client.config.ConfigManager;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.runelite.api.coords.WorldPoint;

import static com.questhelper.managers.ConstructDraftTestUtil.addDefinitionAndRef;
import static com.questhelper.managers.HelperConstructModels.IdType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HelperConstructManagerTest
{
	@Test
	void removeStepAtBoundsChecked()
	{
		HelperConstructManager manager = new HelperConstructManager();
		HelperConstructModels.DraftStep step = new HelperConstructModels.DraftStep();
		step.setStepId("a");
		addDefinitionAndRef(manager, step);

		assertFalse(manager.removeStepAt(-1));
		assertFalse(manager.removeStepAt(1));
		assertTrue(manager.removeStepAt(0));
		assertFalse(manager.removeStepAt(0));
	}

	@Test
	void removeRequirementAtBoundsChecked()
	{
		HelperConstructManager manager = new HelperConstructManager();
		manager.getCurrentDraft().getRequirements().add(new HelperConstructModels.DraftRequirement());

		assertFalse(manager.removeRequirementAt(-1));
		assertFalse(manager.removeRequirementAt(1));
		assertTrue(manager.removeRequirementAt(0));
		assertFalse(manager.removeRequirementAt(0));
	}

	@Test
	void moveStepReordersCombinedRows()
	{
		HelperConstructManager manager = new HelperConstructManager();
		HelperConstructModels.DraftStep step1 = new HelperConstructModels.DraftStep();
		step1.setStepId("a");
		step1.setSuggestedVarName("firstStep");
		step1.setTargetText("First");
		addDefinitionAndRef(manager, step1);
		HelperConstructModels.DraftStep step2 = new HelperConstructModels.DraftStep();
		step2.setStepId("b");
		step2.setSuggestedVarName("secondStep");
		step2.setTargetText("Second");
		addDefinitionAndRef(manager, step2);

		assertTrue(manager.moveStep(1, 0));
		List<HelperConstructManager.CombinedStepRow> rows = manager.getCombinedStepRows();
		assertEquals("secondStep", rows.get(0).getVarName());
		assertEquals("firstStep", rows.get(1).getVarName());
	}

	@Test
	void updateStepVarNameUpdatesCombinedRows()
	{
		HelperConstructManager manager = new HelperConstructManager();
		HelperConstructModels.DraftStep step = new HelperConstructModels.DraftStep();
		step.setStepId("a");
		step.setSuggestedVarName("oldName");
		addDefinitionAndRef(manager, step);

		assertTrue(manager.updateStepVarName(0, "newName"));
		assertEquals("newName", manager.getCombinedStepRows().get(0).getVarName());
		assertFalse(manager.updateStepVarName(1, "x"));
	}

	@Test
	void addItemStepReusesExistingRequirement()
		throws Exception
	{
		HelperConstructManager manager = new HelperConstructManager();
		Method addItemStep = HelperConstructManager.class.getDeclaredMethod("addItemStep", int.class, String.class);
		addItemStep.setAccessible(true);

		addItemStep.invoke(manager, 100, "Bucket");
		addItemStep.invoke(manager, 100, "Bucket");

		assertEquals(1, manager.getCurrentDraft().getRequirements().size());
		assertEquals(1, manager.getCurrentDraft().getStepDefinitions().size());
		assertEquals(100, manager.getCurrentDraft().getRequirements().get(0).getRawId());
		assertEquals(100, manager.getCurrentDraft().getStepDefinitions().get(0).getRawId());
	}

	@Test
	void removingNpcDefinitionRemovesOrderRefs()
	{
		HelperConstructManager manager = new HelperConstructManager();
		HelperConstructModels.DraftStep npc = new HelperConstructModels.DraftStep();
		npc.setStepId("npc1");
		npc.setKind(HelperConstructModels.StepKind.NPC);
		manager.getCurrentDraft().getStepDefinitions().add(npc);
		HelperConstructModels.DraftOrderLine line = new HelperConstructModels.DraftOrderLine();
		line.setSectionDivider(false);
		line.setRefStepId("npc1");
		manager.getCurrentDraft().getOrder().add(line);

		assertTrue(manager.removeStepAt(ConstructStepKind.NPC, 0));
		assertTrue(manager.getCurrentDraft().getStepDefinitions().isEmpty());
		assertTrue(manager.getCurrentDraft().getOrder().isEmpty());
	}

	@Test
	void exportImportJsonRoundTripPreservesDraft()
	{
		HelperConstructManager manager = new HelperConstructManager();
		manager.getCurrentDraft().setQuestName("RoundTripQuest");
		manager.getCurrentDraft().setClassName("RoundTripHelper");
		HelperConstructModels.DraftStep step = new HelperConstructModels.DraftStep();
		step.setStepId("s1");
		step.setKind(HelperConstructModels.StepKind.NPC);
		step.setInstructionText("Do the thing");
		step.getAttachedRequirements().add(HelperConstructModels.DraftStepAttachedRequirement.item(995));
		step.setWorldPoint(new WorldPoint(3200, 3200, 0));
		addDefinitionAndRef(manager, step);

		String json = manager.exportDraftJson();
		HelperConstructManager receiver = new HelperConstructManager();
		HelperConstructManager.ImportDraftResult result = receiver.importDraftFromJson(json);
		assertTrue(result.isSuccess(), result.getErrorMessage());
		assertEquals("RoundTripQuest", receiver.getCurrentDraft().getQuestName());
		assertEquals("RoundTripHelper", receiver.getCurrentDraft().getClassName());
		assertEquals(1, receiver.getCurrentDraft().getStepDefinitions().size());
		HelperConstructModels.DraftStep loaded = receiver.getCurrentDraft().getStepDefinitions().get(0);
		assertEquals("Do the thing", loaded.getInstructionText());
		assertEquals(1, loaded.getAttachedRequirements().size());
		assertEquals(995, loaded.getAttachedRequirements().get(0).getItemRawId().intValue());
		assertEquals(3200, loaded.getWorldPoint().getX());
	}

	@Test
	void stepWorldPointAndRequiredItemsCanBeUpdatedByIndex()
	{
		HelperConstructManager manager = new HelperConstructManager();
		HelperConstructModels.DraftStep npc = new HelperConstructModels.DraftStep();
		npc.setStepId("n1");
		npc.setKind(HelperConstructModels.StepKind.NPC);
		npc.setRawId(1);
		manager.getCurrentDraft().getStepDefinitions().add(npc);

		assertTrue(manager.updateStepWorldPointAt(ConstructStepKind.NPC, 0, "3000, 3100, 1"));
		assertEquals(3000, manager.getCurrentDraft().getStepDefinitions().get(0).getWorldPoint().getX());
		assertEquals(1, manager.getCurrentDraft().getStepDefinitions().get(0).getWorldPoint().getPlane());

		assertTrue(manager.updateStepRequiredItemsAt(ConstructStepKind.NPC, 0, "995, 2349"));
		List<Integer> ids = new ArrayList<>();
		for (HelperConstructModels.DraftStepAttachedRequirement a : manager.getCurrentDraft().getStepDefinitions().get(0).getAttachedRequirements())
		{
			ids.add(a.getItemRawId());
		}
		assertEquals(List.of(995, 2349), ids);

		assertFalse(manager.updateStepWorldPointAt(ConstructStepKind.NPC, 0, "not-a-point"));
	}

	@Test
	void applyStepAttachmentsAtStoresItemAttachments()
	{
		HelperConstructManager manager = new HelperConstructManager();
		HelperConstructModels.DraftRequirement r1 = new HelperConstructModels.DraftRequirement();
		r1.setRawId(100);
		r1.setDisplayName("A");
		manager.getCurrentDraft().getRequirements().add(r1);
		HelperConstructModels.DraftRequirement r2 = new HelperConstructModels.DraftRequirement();
		r2.setRawId(200);
		r2.setDisplayName("B");
		manager.getCurrentDraft().getRequirements().add(r2);

		HelperConstructModels.DraftStep npc = new HelperConstructModels.DraftStep();
		npc.setStepId("n1");
		npc.setKind(HelperConstructModels.StepKind.NPC);
		npc.setRawId(1);
		manager.getCurrentDraft().getStepDefinitions().add(npc);

		assertTrue(manager.applyStepAttachmentsAt(ConstructStepKind.NPC, 0, Arrays.asList(
			HelperConstructManager.StepAttachmentEdit.item(100),
			HelperConstructManager.StepAttachmentEdit.item(200))));
		List<Integer> ids = new ArrayList<>();
		for (HelperConstructModels.DraftStepAttachedRequirement a : manager.getCurrentDraft().getStepDefinitions().get(0).getAttachedRequirements())
		{
			ids.add(a.getItemRawId());
		}
		assertEquals(List.of(100, 200), ids);
	}

	@Test
	void importJsonWithoutRequirementsFieldDoesNotThrow()
	{
		HelperConstructManager manager = new HelperConstructManager();
		String json = "{\"formatVersion\":1,\"questName\":\"X\",\"className\":\"YHelper\",\"definitions\":[],\"order\":[]}";
		assertTrue(manager.importDraftFromJson(json).isSuccess());
		assertEquals("X", manager.getCurrentDraft().getQuestName());
		assertTrue(manager.getCurrentDraft().getRequirements().isEmpty());
	}

	@Test
	void convertStepDefinitionKindPreservesStepIdAndOrderRef() throws Exception
	{
		HelperConstructManager manager = new HelperConstructManager();
		GamevalSymbolResolver resolver = mock(GamevalSymbolResolver.class);
		when(resolver.resolve(any(IdType.class), eq(0)))
			.thenReturn(new GamevalSymbolResolver.ResolutionResult("SYM", false, false));
		Field fr = HelperConstructManager.class.getDeclaredField("symbolResolver");
		fr.setAccessible(true);
		fr.set(manager, resolver);
		ConfigManager cm = mock(ConfigManager.class);
		Field fc = HelperConstructManager.class.getDeclaredField("configManager");
		fc.setAccessible(true);
		fc.set(manager, cm);

		HelperConstructModels.DraftStep step = new HelperConstructModels.DraftStep();
		step.setStepId("step-keep");
		step.setKind(HelperConstructModels.StepKind.TEXT);
		step.setRawId(0);
		step.setResolvedSymbol("");
		step.setInstructionText("do thing");
		manager.getCurrentDraft().getStepDefinitions().add(step);
		HelperConstructModels.DraftOrderLine line = new HelperConstructModels.DraftOrderLine();
		line.setLineId("line-1");
		line.setRefStepId("step-keep");
		line.setSectionDivider(false);
		manager.getCurrentDraft().getOrder().add(line);

		Field loaded = HelperConstructManager.class.getDeclaredField("loadedFromConfig");
		loaded.setAccessible(true);
		loaded.set(manager, true);

		assertTrue(manager.convertStepDefinitionKind(ConstructStepKind.TEXT, 0, ConstructStepKind.NPC));
		assertEquals(HelperConstructModels.StepKind.NPC, manager.getCurrentDraft().getStepDefinitions().get(0).getKind());
		assertEquals("step-keep", manager.getCurrentDraft().getStepDefinitions().get(0).getStepId());
		assertEquals("step-keep", manager.getCurrentDraft().getOrder().get(0).getRefStepId());
		assertEquals(0, manager.getCurrentDraft().getStepDefinitions().get(0).getRawId());
		assertEquals("SYM", manager.getCurrentDraft().getStepDefinitions().get(0).getResolvedSymbol());
	}
}
