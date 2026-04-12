package com.questhelper.maker;

import com.google.gson.Gson;
import com.questhelper.managers.GamevalSymbolResolver;
import com.questhelper.managers.HelperScaffoldGenerator;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.zone.ZoneRequirement;
import net.runelite.api.coords.WorldPoint;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.questhelper.maker.HelperConstructModels.DraftHelper;
import static com.questhelper.maker.HelperConstructModels.DraftOrderLine;
import static com.questhelper.maker.HelperConstructModels.DraftOrderStepRequirement;
import static com.questhelper.maker.HelperConstructModels.DraftRequirement;
import static com.questhelper.maker.HelperConstructModels.DraftStep;
import static com.questhelper.maker.HelperConstructModels.DraftStepAttachedRequirement;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderStepRequirementSupportTest
{
	@Test
	void gsonRoundTripsNestedTree()
	{
		DraftOrderStepRequirement root = DraftOrderStepRequirement.group("OR",
			DraftOrderStepRequirement.invert(DraftOrderStepRequirement.orderVarbitSlot()),
			DraftOrderStepRequirement.item(42));
		Gson gson = new Gson();
		String json = gson.toJson(root);
		DraftOrderStepRequirement back = gson.fromJson(json, DraftOrderStepRequirement.class);
		assertTrue(OrderStepRequirementSupport.orderStepTreesEqual(root, back));
		assertEquals("OR", back.getLogic());
		assertEquals(2, back.getChildren().size());
	}

	@Test
	void gsonRoundTripsTreeWithZoneLeaf()
	{
		DraftOrderStepRequirement root = DraftOrderStepRequirement.group("AND",
			DraftOrderStepRequirement.zone(new WorldPoint(10, 20, 0), new WorldPoint(30, 40, 0), "Near bank"),
			DraftOrderStepRequirement.item(99));
		Gson gson = new Gson();
		String json = gson.toJson(root);
		DraftOrderStepRequirement back = gson.fromJson(json, DraftOrderStepRequirement.class);
		assertTrue(OrderStepRequirementSupport.orderStepTreesEqual(root, back), json);
	}

	@Test
	void validateRejectsInlineZoneNodes()
	{
		DraftOrderStepRequirement z = DraftOrderStepRequirement.zone(
			new WorldPoint(0, 0, 0), new WorldPoint(1, 1, 0), null);
		assertNotNull(OrderStepRequirementSupport.validateTreeOrError(z));
	}

	@Test
	void validateAcceptsOrderZoneLeaf()
	{
		assertNull(OrderStepRequirementSupport.validateTreeOrError(DraftOrderStepRequirement.orderZoneSlot()));
	}

	@Test
	void emitSelectorJavaEmitsInlineZoneLiteral()
	{
		DraftOrderStepRequirement z = DraftOrderStepRequirement.zone(
			new WorldPoint(10, 20, 0), new WorldPoint(30, 40, 1), "Area");
		DraftOrderLine line = new DraftOrderLine();
		DraftStep def = new DraftStep();
		List<String> warnings = new ArrayList<>();
		String out = OrderStepRequirementSupport.emitSelectorJava(z, line, def, Map.of(), Map.of(), Map.of(), warnings);
		assertTrue(out.contains("ZoneRequirement"), out);
		assertTrue(out.contains("new Zone("), out);
		assertTrue(out.contains("new WorldPoint(10, 20, 0)"), out);
		assertTrue(out.contains("new WorldPoint(30, 40, 1)"), out);
		assertTrue(out.contains("Area"), out);
	}

	@Test
	void emitSelectorJavaEmitsOrderZoneFieldReference()
	{
		DraftOrderLine line = new DraftOrderLine();
		line.setOrderSlotId("slot-z");
		line.setZoneRoutingCorner1(new WorldPoint(1, 2, 0));
		line.setZoneRoutingCorner2(new WorldPoint(3, 4, 0));
		DraftOrderStepRequirement z = DraftOrderStepRequirement.orderZoneSlot();
		List<String> warnings = new ArrayList<>();
		String out = OrderStepRequirementSupport.emitSelectorJava(
			z, line, new DraftStep(), Map.of(), Map.of(), Map.of("slot-z", "zoneReqField"), warnings);
		assertEquals("zoneReqField", out);
	}

	@Test
	void draftUsesZoneRequirementScansOrder()
	{
		DraftHelper d = new DraftHelper();
		assertFalse(OrderStepRequirementSupport.draftUsesZoneRequirement(d));
		DraftOrderLine line = new DraftOrderLine();
		line.setSectionDivider(false);
		line.setStepRequirement(DraftOrderStepRequirement.orderZoneSlot());
		line.setZoneRoutingCorner1(new WorldPoint(0, 0, 0));
		line.setZoneRoutingCorner2(new WorldPoint(1, 0, 0));
		d.getOrder().add(line);
		assertTrue(OrderStepRequirementSupport.draftUsesZoneRequirement(d));
	}

	@Test
	void buildRuntimeSelectorOrderZoneUsesLineRouting()
	{
		DraftOrderLine line = new DraftOrderLine();
		line.setZoneRoutingCorner1(new WorldPoint(5, 5, 0));
		line.setZoneRoutingCorner2(new WorldPoint(6, 7, 0));
		line.setZoneRoutingDisplayText("L");
		DraftOrderStepRequirement z = DraftOrderStepRequirement.orderZoneSlot();
		Requirement r = OrderStepRequirementSupport.buildRuntimeSelector(z, line, null, Map.of());
		assertNotNull(r);
		assertTrue(r instanceof ZoneRequirement);
	}

	@Test
	void migrateInlineZoneLeavesWritesRoutingAndRewritesLeaf()
	{
		DraftOrderLine line = new DraftOrderLine();
		line.setSectionDivider(false);
		line.setOrderSlotId("slot-z");
		DraftOrderStepRequirement tree = DraftOrderStepRequirement.group("AND",
			DraftOrderStepRequirement.zone(new WorldPoint(1, 2, 0), new WorldPoint(3, 4, 0), "z"),
			DraftOrderStepRequirement.item(10));
		assertNull(OrderStepRequirementSupport.migrateInlineZoneLeavesToOrderRouting(line, tree));
		assertEquals("ORDER_ZONE", tree.getChildren().get(0).getKind());
		assertNotNull(line.getZoneRoutingCorner1());
		assertEquals(1, line.getZoneRoutingCorner1().getX());
		assertEquals(3, line.getZoneRoutingCorner2().getX());
	}

	@Test
	void stripOrderZoneLeavesRemovesSlotLeaf()
	{
		DraftOrderStepRequirement root = DraftOrderStepRequirement.group("AND",
			DraftOrderStepRequirement.orderZoneSlot(),
			DraftOrderStepRequirement.item(10));
		DraftOrderStepRequirement out = OrderStepRequirementSupport.stripOrderZoneLeaves(root);
		assertNotNull(out);
		assertEquals("ITEM", out.getKind());
		assertEquals(10, out.getItemRawId().intValue());
	}

	@Test
	void migrationFromScalarLinkedItemAddsInvertCapturedItem()
	{
		ConstructDraftPersistence.DraftState state = new ConstructDraftPersistence.DraftState();
		state.formatVersion = ConstructDraftPersistence.DRAFT_FORMAT_VERSION;
		ConstructDraftPersistence.DraftStepState def = new ConstructDraftPersistence.DraftStepState();
		def.stepId = "s1";
		def.kind = HelperConstructModels.StepKind.OBJECT;
		def.rawId = 1;
		def.note = "x";
		state.definitions.add(def);
		ConstructDraftPersistence.DraftOrderLineState ord = new ConstructDraftPersistence.DraftOrderLineState();
		ord.orderSlotId = "slot-a";
		ord.refStepId = "s1";
		ord.linkedRequirementRawId = 526;
		state.order.add(ord);

		DraftHelper loaded = ConstructDraftPersistence.draftHelperFromState(state);
		DraftOrderLine line = loaded.getOrder().get(0);
		assertNotNull(line.getStepRequirement());
		assertTrue(OrderStepRequirementSupport.orderStepTreesEqual(
			DraftOrderStepRequirement.invert(DraftOrderStepRequirement.item(526)),
			line.getStepRequirement()));
	}

	@Test
	void migrationDoesNotAddVarbitTreeWhenOnlyRoutingAttachmentExists()
	{
		ConstructDraftPersistence.DraftState state = new ConstructDraftPersistence.DraftState();
		state.formatVersion = ConstructDraftPersistence.DRAFT_FORMAT_VERSION;
		ConstructDraftPersistence.DraftStepState def = new ConstructDraftPersistence.DraftStepState();
		def.stepId = "s1";
		def.kind = HelperConstructModels.StepKind.OBJECT;
		def.rawId = 1;
		def.note = "x";
		ConstructDraftPersistence.DraftStepAttachedRequirementState vb = new ConstructDraftPersistence.DraftStepAttachedRequirementState();
		vb.kind = HelperConstructModels.StepAttachmentKind.VARBIT.name();
		vb.orderSlotId = "slot-a";
		vb.varbitId = 100;
		vb.varbitRequiredValue = 1;
		vb.varbitOperation = "EQUAL";
		def.attachedRequirements.add(vb);
		state.definitions.add(def);
		ConstructDraftPersistence.DraftOrderLineState ord = new ConstructDraftPersistence.DraftOrderLineState();
		ord.orderSlotId = "slot-a";
		ord.refStepId = "s1";
		ord.linkedRequirementRawId = null;
		state.order.add(ord);

		DraftHelper loaded = ConstructDraftPersistence.draftHelperFromState(state);
		DraftOrderLine line = loaded.getOrder().get(0);
		assertNull(line.getStepRequirement());
	}

	@Test
	void scaffoldGeneratorEmitsConditionsForExplicitGroupTree()
	{
		HelperScaffoldGenerator generator = new HelperScaffoldGenerator(new GamevalSymbolResolver());
		DraftHelper draft = new DraftHelper();
		draft.setClassName("TreeOrderTest");
		draft.setPackagePath("com.questhelper.helpers.quests.generated");

		DraftRequirement req = new DraftRequirement();
		req.setRawId(10);
		req.setDisplayName("A");
		draft.getRequirements().add(req);

		DraftStep step1 = new DraftStep();
		step1.setKind(HelperConstructModels.StepKind.OBJECT);
		step1.setRawId(1);
		step1.setStepId("st1");
		step1.setInstructionText("First.");
		step1.setSuggestedVarName("firstStep");
		step1.setWorldPoint(new WorldPoint(3200, 3200, 0));
		draft.getStepDefinitions().add(step1);

		DraftStep step2 = new DraftStep();
		step2.setKind(HelperConstructModels.StepKind.OBJECT);
		step2.setRawId(2);
		step2.setStepId("st2");
		step2.setInstructionText("Second.");
		step2.setSuggestedVarName("secondStep");
		step2.setWorldPoint(new WorldPoint(3201, 3201, 0));
		draft.getStepDefinitions().add(step2);

		DraftOrderLine line1 = new DraftOrderLine();
		line1.setSectionDivider(false);
		line1.setRefStepId("st1");
		line1.setOrderSlotId("os1");
		line1.setLinkedRequirementRawId(null);
		line1.setStepRequirement(DraftOrderStepRequirement.group("AND",
			DraftOrderStepRequirement.invert(DraftOrderStepRequirement.orderVarbitSlot()),
			DraftOrderStepRequirement.item(10)));
		line1.getAttachedRequirements().add(DraftStepAttachedRequirement.varbit(5, 1, "EQUAL", ""));

		DraftOrderLine line2 = new DraftOrderLine();
		line2.setSectionDivider(false);
		line2.setRefStepId("st2");
		line2.setOrderSlotId("os2");
		line2.setLinkedRequirementRawId(null);

		draft.getOrder().add(line1);
		draft.getOrder().add(line2);

		line2.getAttachedRequirements().add(DraftStepAttachedRequirement.varbit(6, 1, "EQUAL", ""));

		String source = generator.generate(draft).getSource();
		assertTrue(source.contains("new Conditions(LogicType.AND,"), source);
		assertTrue(source.contains(".addStep(new Conditions(LogicType.AND,"), source);
	}

	@Test
	void scaffoldGeneratorAddsZoneImportsWhenOrderUsesZone()
	{
		HelperScaffoldGenerator generator = new HelperScaffoldGenerator(new GamevalSymbolResolver());
		DraftHelper draft = new DraftHelper();
		draft.setClassName("ZoneOrderTest");
		draft.setPackagePath("com.questhelper.helpers.quests.generated");

		DraftRequirement req = new DraftRequirement();
		req.setRawId(10);
		req.setDisplayName("A");
		draft.getRequirements().add(req);

		DraftStep step1 = new DraftStep();
		step1.setKind(HelperConstructModels.StepKind.OBJECT);
		step1.setRawId(1);
		step1.setStepId("st1");
		step1.setInstructionText("First.");
		step1.setSuggestedVarName("firstStep");
		step1.setWorldPoint(new WorldPoint(3200, 3200, 0));
		draft.getStepDefinitions().add(step1);

		DraftOrderLine line1 = new DraftOrderLine();
		line1.setSectionDivider(false);
		line1.setRefStepId("st1");
		line1.setOrderSlotId("os1");
		line1.setLinkedRequirementRawId(null);
		line1.setZoneRoutingCorner1(new WorldPoint(1000, 2000, 0));
		line1.setZoneRoutingCorner2(new WorldPoint(1005, 2005, 0));
		line1.setZoneRoutingDisplayText("Cell");
		line1.setStepRequirement(DraftOrderStepRequirement.group("AND",
			DraftOrderStepRequirement.invert(DraftOrderStepRequirement.orderVarbitSlot()),
			DraftOrderStepRequirement.item(10),
			DraftOrderStepRequirement.orderZoneSlot()));
		line1.getAttachedRequirements().add(DraftStepAttachedRequirement.varbit(5, 1, "EQUAL", ""));
		draft.getOrder().add(line1);

		String source = generator.generate(draft).getSource();
		assertTrue(source.contains("import com.questhelper.requirements.zone.Zone;"), source);
		assertTrue(source.contains("import com.questhelper.requirements.zone.ZoneRequirement;"), source);
		assertTrue(source.contains("new ZoneRequirement("), source);
	}

	@Test
	void buildRuntimeSelectorForGroupAndInvert()
	{
		DraftOrderLine line = new DraftOrderLine();
		line.setOrderSlotId("os1");
		line.getAttachedRequirements().add(DraftStepAttachedRequirement.varbit(9, 2, "EQUAL", "x"));
		DraftStep step = new DraftStep();
		ItemRequirement cap = new ItemRequirement("A", 7);
		Map<Integer, ItemRequirement> byId = new HashMap<>();
		byId.put(7, cap);
		DraftOrderStepRequirement tree = DraftOrderStepRequirement.group("OR",
			DraftOrderStepRequirement.invert(DraftOrderStepRequirement.orderVarbitSlot()),
			DraftOrderStepRequirement.item(7));
		Requirement r = OrderStepRequirementSupport.buildRuntimeSelector(tree, line, step, byId);
		assertNotNull(r);
	}

	@Test
	void migrateConvertsInlineVarbitLeavesToOrderSlotAndWritesRouting()
	{
		DraftOrderLine line = new DraftOrderLine();
		line.setSectionDivider(false);
		line.setOrderSlotId("slot-x");
		DraftOrderStepRequirement tree = DraftOrderStepRequirement.group("AND",
			DraftOrderStepRequirement.varbit(7, 2, "GREATER_EQUAL", "d"),
			DraftOrderStepRequirement.item(10));
		assertNull(OrderStepRequirementSupport.migrateInlineVarbitLeavesToOrderRouting(line, tree));
		assertEquals("ORDER_VARBIT", tree.getChildren().get(0).getKind());
		DraftStepAttachedRequirement cfg = DraftStepAttachedRequirement.findOrderRoutingVarbit(line);
		assertNotNull(cfg);
		assertEquals(7, cfg.getVarbitId().intValue());
		assertEquals(2, cfg.getVarbitRequiredValue().intValue());
	}

	@Test
	void prepareRejectsConflictingInlineVarbitsOnSameRow()
	{
		DraftOrderLine line = new DraftOrderLine();
		line.setSectionDivider(false);
		DraftOrderStepRequirement tree = DraftOrderStepRequirement.group("AND",
			DraftOrderStepRequirement.varbit(1, 1, "EQUAL", null),
			DraftOrderStepRequirement.varbit(2, 1, "EQUAL", null));
		assertNotNull(OrderStepRequirementSupport.prepareOrderStepTreeForPersistence(line, tree));
	}

	@Test
	void migrateRejectsWhenVarbitTabValueDisagreesWithInlineTree()
	{
		DraftOrderLine line = new DraftOrderLine();
		line.setSectionDivider(false);
		line.getAttachedRequirements().add(DraftStepAttachedRequirement.varbit(99, 1, "EQUAL", null));
		DraftOrderStepRequirement tree = DraftOrderStepRequirement.varbit(1, 1, "EQUAL", null);
		assertNotNull(OrderStepRequirementSupport.migrateInlineVarbitLeavesToOrderRouting(line, tree));
	}

	@Test
	void stripOrderVarbitLeavesRemovesSlotAndUnwrapsSingleChildGroup()
	{
		DraftOrderStepRequirement root = DraftOrderStepRequirement.group("AND",
			DraftOrderStepRequirement.orderVarbitSlot(),
			DraftOrderStepRequirement.item(10));
		DraftOrderStepRequirement out = OrderStepRequirementSupport.stripOrderVarbitLeaves(root);
		assertNotNull(out);
		assertEquals("ITEM", out.getKind());
		assertEquals(10, out.getItemRawId().intValue());
	}

	@Test
	void stripOrderVarbitLeavesReturnsNullWhenOnlyOrderVarbit()
	{
		assertNull(OrderStepRequirementSupport.stripOrderVarbitLeaves(DraftOrderStepRequirement.orderVarbitSlot()));
	}
}
