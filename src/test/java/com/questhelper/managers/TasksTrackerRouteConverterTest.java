package com.questhelper.managers;

import com.questhelper.managers.taskstroute.TasksTrackerRouteDto;
import com.questhelper.managers.taskstroute.TasksTrackerRouteDto.RouteCustomItemDto;
import com.questhelper.managers.taskstroute.TasksTrackerRouteDto.RouteInteractDto;
import com.questhelper.managers.taskstroute.TasksTrackerRouteDto.RouteItemDto;
import com.questhelper.managers.taskstroute.TasksTrackerRouteDto.RouteLocationDto;
import com.questhelper.managers.taskstroute.TasksTrackerRouteDto.RouteSectionDto;
import com.questhelper.managers.taskstroute.TasksTrackerRouteExporter;
import com.questhelper.managers.taskstroute.TasksTrackerRouteImporter;
import com.questhelper.managers.taskstroute.TasksTrackerRouteValidation;
import net.runelite.api.coords.WorldPoint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.questhelper.managers.HelperConstructModels.DraftHelper;
import static com.questhelper.managers.HelperConstructModels.DraftOrderLine;
import static com.questhelper.managers.HelperConstructModels.DraftStep;
import static com.questhelper.managers.HelperConstructModels.StepKind;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TasksTrackerRouteConverterTest
{
	private static final GamevalSymbolResolver SYMBOLS = new GamevalSymbolResolver();

	private static String hubRow(int structId, int sortId, String name, String description, int x, int y, int plane)
	{
		return "{\"structId\":" + structId + ",\"sortId\":" + sortId + ",\"name\":\"" + name + "\",\"description\":\""
			+ description + "\",\"location\":{\"x\":" + x + ",\"y\":" + y + ",\"plane\":" + plane + "}}";
	}

	@Test
	void validateRoute_rejectsBlankName()
	{
		TasksTrackerRouteDto route = new TasksTrackerRouteDto();
		route.setName(" ");
		route.setTaskType("LEAGUE_5");
		RouteSectionDto sec = new RouteSectionDto();
		sec.setName("S");
		sec.setItems(List.of());
		route.setSections(List.of(sec));
		assertEquals("Route name is required", TasksTrackerRouteValidation.validateRoute(route));
	}

	@Test
	void validateRoute_rejectsBothTaskIdAndCustomItem()
	{
		TasksTrackerRouteDto route = minimalRoute("R");
		RouteItemDto bad = new RouteItemDto();
		bad.setTaskId(1);
		RouteCustomItemDto c = new RouteCustomItemDto();
		c.setId("x");
		c.setLabel("L");
		bad.setCustomItem(c);
		route.getSections().get(0).setItems(List.of(bad));
		assertTrue(TasksTrackerRouteValidation.validateRoute(route).contains("only one of taskId or customItem"));
	}

	@Test
	void importRoute_withoutHub_succeedsUsingTaskIdAndNote()
	{
		TasksTrackerRouteDto route = minimalRoute("R");
		RouteItemDto item = new RouteItemDto();
		item.setTaskId(2645);
		item.setNote("Do the league step.");
		route.getSections().get(0).setItems(List.of(item));
		DraftHelper draft = TasksTrackerRouteImporter.importRoute(route, null, SYMBOLS);
		DraftStep step = firstStepDefinition(draft);
		assertEquals(2645, (int) step.getStructId());
		assertTrue(step.getInstructionText().contains("Do the league step"));
		assertTrue(step.getSuggestedVarName().endsWith("2645"));
	}

	@Test
	void importRoute_prefersNpcWhenBothInteractListsPresent()
	{
		int sid = 424242;
		TasksTrackerRouteDto route = minimalRoute("NpcPref");
		RouteItemDto item = new RouteItemDto();
		item.setTaskId(sid);
		RouteInteractDto inter = new RouteInteractDto();
		inter.setNpc(List.of(100));
		inter.setObject(List.of(200));
		item.setInteract(inter);
		item.setNote("npc step");
		route.getSections().get(0).setItems(List.of(item));

		DraftHelper draft = TasksTrackerRouteImporter.importRoute(route, null, SYMBOLS);

		DraftStep step = firstStepDefinition(draft);
		assertEquals(StepKind.NPC, step.getKind());
		assertEquals(100, step.getRawId());
	}

	@Test
	void importRoute_storesExtraNpcIdsAsAlternates()
	{
		int sid = 424250;
		TasksTrackerRouteDto route = minimalRoute("MultiNpc");
		RouteItemDto item = new RouteItemDto();
		item.setTaskId(sid);
		RouteInteractDto inter = new RouteInteractDto();
		inter.setNpc(List.of(100, 101, 102));
		item.setInteract(inter);
		item.setNote("multi");
		route.getSections().get(0).setItems(List.of(item));

		DraftHelper draft = TasksTrackerRouteImporter.importRoute(route, null, SYMBOLS);
		DraftStep step = firstStepDefinition(draft);
		assertEquals(StepKind.NPC, step.getKind());
		assertEquals(100, step.getRawId());
		assertEquals(List.of(101, 102), step.getAlternateRawIds());

		TasksTrackerRouteDto out = TasksTrackerRouteExporter.export(draft);
		RouteItemDto outItem = out.getSections().get(0).getItems().get(0);
		assertEquals(List.of(100, 101, 102), outItem.getInteract().getNpc());
	}

	@Test
	void importRoute_mapsObjectInteract()
	{
		int sid = 424243;
		TasksTrackerRouteDto route = minimalRoute("Obj");
		RouteItemDto item = new RouteItemDto();
		item.setTaskId(sid);
		RouteInteractDto inter = new RouteInteractDto();
		inter.setObject(List.of(12345));
		item.setInteract(inter);
		item.setNote("obj step");
		route.getSections().get(0).setItems(List.of(item));

		DraftHelper draft = TasksTrackerRouteImporter.importRoute(route, null, SYMBOLS);

		DraftStep step = firstStepDefinition(draft);
		assertEquals(StepKind.OBJECT, step.getKind());
		assertEquals(12345, step.getRawId());
	}

	@Test
	void importThenExport_roundTripLeagueTaskFields()
	{
		int sid = 55501;
		TasksTrackerRouteDto route = minimalRoute("Round");
		RouteItemDto item = new RouteItemDto();
		item.setTaskId(sid);
		item.setNote("Do the thing");
		RouteLocationDto loc = new RouteLocationDto();
		loc.setX(3200);
		loc.setY(3201);
		loc.setPlane(0);
		item.setLocation(loc);
		RouteInteractDto inter = new RouteInteractDto();
		inter.setNpc(List.of(100));
		item.setInteract(inter);
		route.getSections().get(0).setItems(List.of(item));

		DraftHelper draft = TasksTrackerRouteImporter.importRoute(route, null, SYMBOLS);

		TasksTrackerRouteDto out = TasksTrackerRouteExporter.export(draft);
		assertEquals("Round", out.getName());
		assertEquals("LEAGUE_5", out.getTaskType());
		assertEquals(1, out.getSections().size());
		RouteItemDto outItem = out.getSections().get(0).getItems().get(0);
		assertEquals(sid, outItem.getTaskId());
		assertEquals("Do the thing", outItem.getNote());
		assertNotNull(outItem.getInteract());
		assertEquals(List.of(100), outItem.getInteract().getNpc());
		assertEquals(3200, outItem.getLocation().getX());
		assertEquals(3201, outItem.getLocation().getY());
	}

	@Test
	void importRoute_customItemBecomesTextStep()
	{
		TasksTrackerRouteDto route = minimalRoute("Custom");
		RouteItemDto item = new RouteItemDto();
		RouteCustomItemDto c = new RouteCustomItemDto();
		c.setId("my_custom_1");
		c.setLabel("Bank");
		c.setDescription("Use the chest.");
		item.setCustomItem(c);
		route.getSections().get(0).setItems(List.of(item));

		DraftHelper draft = TasksTrackerRouteImporter.importRoute(route, null, SYMBOLS);

		DraftStep step = firstStepDefinition(draft);
		assertNull(step.getStructId());
		assertEquals(StepKind.TEXT, step.getKind());
		assertTrue(step.getInstructionText().contains("Bank"));
		assertTrue(step.getInstructionText().contains("Use the chest."));
	}

	@Test
	void importRoute_customItem_appendsRouteNote()
	{
		TasksTrackerRouteDto route = minimalRoute("CustomNote");
		RouteItemDto item = new RouteItemDto();
		RouteCustomItemDto c = new RouteCustomItemDto();
		c.setId("id1");
		c.setLabel("Bank");
		c.setDescription("");
		item.setCustomItem(c);
		item.setNote("Deposit all.");
		route.getSections().get(0).setItems(List.of(item));

		DraftHelper draft = TasksTrackerRouteImporter.importRoute(route, null, SYMBOLS);
		DraftStep step = firstStepDefinition(draft);
		assertTrue(step.getInstructionText().contains("Bank"));
		assertTrue(step.getInstructionText().contains("Deposit all"));
	}

	@Test
	void importRoute_optionalHub_usesHubNameWhenNoteMissing()
	{
		int sid = 9001;
		TasksTrackerRouteDto route = minimalRoute("Hubbed");
		RouteItemDto item = new RouteItemDto();
		item.setTaskId(sid);
		route.getSections().get(0).setItems(List.of(item));
		String hubJson = "[" + hubRow(sid, 5, "Official Name", "Extra desc", 10, 20, 0) + "]";
		DraftHelper draft = TasksTrackerRouteImporter.importRoute(route,
			TasksTrackerRouteImporter.parseLeagueFullHub(hubJson), SYMBOLS);
		DraftStep step = firstStepDefinition(draft);
		assertTrue(step.getInstructionText().contains("Official Name"));
		assertTrue(step.getSuggestedVarName().endsWith("5"));
	}

	@Test
	void exportDraft_withLeagueAndCustom_producesExpectedItems()
	{
		DraftHelper draft = new DraftHelper();
		draft.setQuestName("Q");

		DraftStep league = new DraftStep();
		league.setStepId("777");
		league.setStructId(777);
		league.setKind(StepKind.NPC);
		league.setRawId(50);
		league.setInstructionText("Talk here.");
		league.setWorldPoint(new WorldPoint(100, 200, 0));
		draft.getStepDefinitions().add(league);
		DraftOrderLine ol = new DraftOrderLine();
		ol.setSectionDivider(false);
		ol.setRefStepId("777");
		draft.getOrder().add(ol);

		DraftStep custom = new DraftStep();
		custom.setStepId("uuid-step-1");
		custom.setStructId(null);
		custom.setKind(StepKind.TEXT);
		custom.setRawId(0);
		custom.setSuggestedVarName("Prep");
		custom.setInstructionText("Prep text.");
		draft.getStepDefinitions().add(custom);
		DraftOrderLine ol2 = new DraftOrderLine();
		ol2.setSectionDivider(false);
		ol2.setRefStepId("uuid-step-1");
		draft.getOrder().add(ol2);

		TasksTrackerRouteDto out = TasksTrackerRouteExporter.export(draft);
		assertEquals(1, out.getSections().size());
		List<RouteItemDto> items = out.getSections().get(0).getItems();
		assertEquals(2, items.size());
		assertEquals(777, items.get(0).getTaskId());
		assertNotNull(items.get(0).getInteract());
		assertEquals(List.of(50), items.get(0).getInteract().getNpc());
		assertNotNull(items.get(1).getCustomItem());
		assertEquals("Prep", items.get(1).getCustomItem().getLabel());
		assertTrue(items.get(1).getCustomItem().getId().startsWith("qh_"));
	}

	private static TasksTrackerRouteDto minimalRoute(String name)
	{
		TasksTrackerRouteDto route = new TasksTrackerRouteDto();
		route.setName(name);
		route.setTaskType("LEAGUE_5");
		RouteSectionDto sec = new RouteSectionDto();
		sec.setName("Main");
		sec.setItems(List.of());
		route.setSections(List.of(sec));
		return route;
	}

	private static DraftStep firstStepDefinition(DraftHelper draft)
	{
		for (DraftOrderLine line : draft.getOrder())
		{
			if (!line.isSectionDivider())
			{
				String id = line.getRefStepId();
				for (DraftStep s : draft.getStepDefinitions())
				{
					if (id.equals(s.getStepId()))
					{
						return s;
					}
				}
			}
		}
		throw new AssertionError("no step ref in order");
	}
}
