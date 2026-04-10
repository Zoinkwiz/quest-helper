package com.questhelper.managers;

import net.runelite.api.coords.WorldPoint;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HelperScaffoldGeneratorTest
{
	private static void addDefinitionAndRef(HelperConstructModels.DraftHelper draft, HelperConstructModels.DraftStep step)
	{
		if (step.getStepId() == null || step.getStepId().isBlank())
		{
			step.setStepId(UUID.randomUUID().toString());
		}
		draft.getStepDefinitions().add(step);
		HelperConstructModels.DraftOrderLine line = new HelperConstructModels.DraftOrderLine();
		line.setSectionDivider(false);
		line.setRefStepId(step.getStepId());
		line.setLinkedRequirementRawId(null);
		draft.getOrder().add(line);
	}

	private static void addSectionDivider(HelperConstructModels.DraftHelper draft, String name)
	{
		HelperConstructModels.DraftOrderLine line = new HelperConstructModels.DraftOrderLine();
		line.setLineId(UUID.randomUUID().toString());
		line.setSectionDivider(true);
		line.setSuggestedVarName(name);
		line.setSectionCondition("");
		line.setSkipWhenConditionMet(false);
		draft.getOrder().add(line);
	}

	@Test
	void varNameSanitizationIsDeterministic()
	{
		assertEquals("cookSRange", HelperScaffoldGenerator.toVarName("Cook's range", "fallback"));
		assertEquals("fallback", HelperScaffoldGenerator.toVarName("###", "fallback"));
	}

	@Test
	void generatorAddsFallbackWarningsForUnknownIds()
	{
		HelperScaffoldGenerator generator = new HelperScaffoldGenerator(new GamevalSymbolResolver());
		HelperConstructModels.DraftHelper draft = new HelperConstructModels.DraftHelper();
		draft.setClassName("TestGenerated");
		draft.setPackagePath("com.questhelper.helpers.quests.generated");

		HelperConstructModels.DraftStep step = new HelperConstructModels.DraftStep();
		step.setKind(HelperConstructModels.StepKind.OBJECT);
		step.setRawId(-999999);
		step.setInstructionText("Inspect the thing.");
		step.setSuggestedVarName("inspectThing");
		step.setTargetText("Thing");
		step.setWorldPoint(new WorldPoint(3200, 3200, 0));
		addDefinitionAndRef(draft, step);

		HelperConstructModels.DraftRequirement req = new HelperConstructModels.DraftRequirement();
		req.setRawId(-999999);
		req.setDisplayName("Unknown Item");
		draft.getRequirements().add(req);

		var scaffold = generator.generate(draft);
		assertTrue(scaffold.getSource().contains("TODO unresolved object"));
		assertTrue(scaffold.getSource().contains("TODO unresolved item"));
		assertTrue(scaffold.getWarnings().size() >= 2);
	}

	@Test
	void generatorUsesComplexStateAndCapturedStepOrder()
	{
		HelperScaffoldGenerator generator = new HelperScaffoldGenerator(new GamevalSymbolResolver());
		HelperConstructModels.DraftHelper draft = new HelperConstructModels.DraftHelper();
		draft.setClassName("LeaguesStart");
		draft.setPackagePath("com.questhelper.helpers.quests.generated");

		HelperConstructModels.DraftStep first = new HelperConstructModels.DraftStep();
		first.setKind(HelperConstructModels.StepKind.OBJECT);
		first.setRawId(1);
		first.setSuggestedVarName("first step");
		first.setInstructionText("First.");
		first.setTargetText("First");
		first.setWorldPoint(new WorldPoint(3200, 3200, 0));
		addDefinitionAndRef(draft, first);

		HelperConstructModels.DraftStep second = new HelperConstructModels.DraftStep();
		second.setKind(HelperConstructModels.StepKind.NPC);
		second.setRawId(2);
		second.setSuggestedVarName("first step");
		second.setInstructionText("Second.");
		second.setTargetText("Second");
		second.setWorldPoint(new WorldPoint(3201, 3201, 0));
		addDefinitionAndRef(draft, second);

		var source = generator.generate(draft).getSource();
		assertTrue(source.contains("extends ComplexStateQuestHelper"));
		assertTrue(source.contains("public QuestStep loadStep()"));
		assertTrue(source.contains("section1Task = new ConditionalStep(this, firstStep2);"));
		assertTrue(source.contains("section1Task.addStep(not(firstStepVarbitReq), firstStep);"));
		assertTrue(source.contains("ConditionalStep allSections = new ConditionalStep(this, section1Task);"));
		assertTrue(source.contains("return allSections;"));
		assertTrue(source.contains("new VarbitRequirement(/* TODO varbit id */ 0, 1)"));
	}

	@Test
	void itemStepUsesHighlightedLinkedRequirement()
	{
		HelperScaffoldGenerator generator = new HelperScaffoldGenerator(new GamevalSymbolResolver());
		HelperConstructModels.DraftHelper draft = new HelperConstructModels.DraftHelper();
		draft.setClassName("ItemStepGenerated");
		draft.setPackagePath("com.questhelper.helpers.quests.generated");

		HelperConstructModels.DraftRequirement req = new HelperConstructModels.DraftRequirement();
		req.setRawId(526);
		req.setDisplayName("Bones");
		draft.getRequirements().add(req);

		HelperConstructModels.DraftStep priorStep = new HelperConstructModels.DraftStep();
		priorStep.setKind(HelperConstructModels.StepKind.OBJECT);
		priorStep.setRawId(1000);
		priorStep.setInstructionText("First.");
		priorStep.setSuggestedVarName("firstStep");
		priorStep.setWorldPoint(new WorldPoint(3200, 3200, 0));
		addDefinitionAndRef(draft, priorStep);

		HelperConstructModels.DraftStep itemStep = new HelperConstructModels.DraftStep();
		itemStep.setKind(HelperConstructModels.StepKind.ITEM);
		itemStep.setRawId(526);
		itemStep.setLinkedRequirementRawId(526);
		itemStep.setInstructionText("Use Bones.");
		itemStep.setSuggestedVarName("useBones");
		addDefinitionAndRef(draft, itemStep);

		var source = generator.generate(draft).getSource();
		assertTrue(source.contains("useBones = new ItemStep(this, \"Use Bones.\", bones.highlighted());"));
		assertTrue(source.contains("section1Task.addStep(not(bones), useBones);"));
		assertTrue(source.contains("section1Task.addStep(not(firstStepVarbitReq), firstStep);"));
		assertTrue(!source.contains("useBonesVarbitReq"));
		assertTrue(source.contains("public List<PanelDetails> getPanels()"));
		assertTrue(source.contains("PanelDetails section1Steps = new PanelDetails("));
		assertTrue(source.contains("section1Steps.setLockingStep(section1Task);"));
	}

	@Test
	void loadStepUsesAllSectionsWrapperForMultipleSections()
	{
		HelperScaffoldGenerator generator = new HelperScaffoldGenerator(new GamevalSymbolResolver());
		HelperConstructModels.DraftHelper draft = new HelperConstructModels.DraftHelper();
		draft.setClassName("SectionWrapperGenerated");
		draft.setPackagePath("com.questhelper.helpers.quests.generated");

		HelperConstructModels.DraftStep step1 = new HelperConstructModels.DraftStep();
		step1.setKind(HelperConstructModels.StepKind.OBJECT);
		step1.setRawId(1);
		step1.setSuggestedVarName("examinebryn");
		step1.setInstructionText("Examine Bryn.");
		step1.setWorldPoint(new WorldPoint(3200, 3200, 0));
		addDefinitionAndRef(draft, step1);

		HelperConstructModels.DraftStep step2 = new HelperConstructModels.DraftStep();
		step2.setKind(HelperConstructModels.StepKind.NPC);
		step2.setRawId(2);
		step2.setSuggestedVarName("talktofarid");
		step2.setInstructionText("Talk to Farid.");
		step2.setWorldPoint(new WorldPoint(3201, 3201, 0));
		addDefinitionAndRef(draft, step2);

		HelperConstructModels.DraftStep step3 = new HelperConstructModels.DraftStep();
		step3.setKind(HelperConstructModels.StepKind.OBJECT);
		step3.setRawId(3);
		step3.setSuggestedVarName("killgoblin");
		step3.setInstructionText("Kill goblin.");
		step3.setWorldPoint(new WorldPoint(3202, 3202, 0));
		addDefinitionAndRef(draft, step3);

		addSectionDivider(draft, "Second Section");

		HelperConstructModels.DraftStep step4 = new HelperConstructModels.DraftStep();
		step4.setKind(HelperConstructModels.StepKind.ITEM);
		step4.setRawId(1712);
		step4.setSuggestedVarName("usedragonstoneamulet");
		step4.setInstructionText("Use dragonstone amulet.");
		addDefinitionAndRef(draft, step4);

		var source = generator.generate(draft).getSource();
		assertTrue(source.contains("allSections = new ConditionalStep(this, section2Task);"));
		assertTrue(source.contains("allSections.addStep(nor(examinebrynVarbitReq, talktofaridVarbitReq, killgoblinVarbitReq), section1Task);"));
		assertTrue(source.contains("section1Task = new ConditionalStep(this, killgoblin);"));
		assertTrue(source.contains("section1Task.addStep(not(examinebrynVarbitReq), examinebryn);"));
		assertTrue(source.contains("section1Task.addStep(not(talktofaridVarbitReq), talktofarid);"));
	}

	@Test
	void duplicateOrderRefsShareOneStepField()
	{
		HelperScaffoldGenerator generator = new HelperScaffoldGenerator(new GamevalSymbolResolver());
		HelperConstructModels.DraftHelper draft = new HelperConstructModels.DraftHelper();
		draft.setClassName("ReuseStep");
		draft.setPackagePath("com.questhelper.helpers.quests.generated");

		HelperConstructModels.DraftStep npc = new HelperConstructModels.DraftStep();
		npc.setKind(HelperConstructModels.StepKind.NPC);
		npc.setRawId(1);
		npc.setSuggestedVarName("talkGuard");
		npc.setInstructionText("Talk.");
		npc.setWorldPoint(new WorldPoint(3200, 3200, 0));
		npc.setStepId("shared-npc");
		draft.getStepDefinitions().add(npc);

		HelperConstructModels.DraftOrderLine a = new HelperConstructModels.DraftOrderLine();
		a.setSectionDivider(false);
		a.setRefStepId("shared-npc");
		a.setLinkedRequirementRawId(null);
		draft.getOrder().add(a);
		HelperConstructModels.DraftOrderLine b = new HelperConstructModels.DraftOrderLine();
		b.setSectionDivider(false);
		b.setRefStepId("shared-npc");
		b.setLinkedRequirementRawId(HelperConstructModels.ORDER_ROUTING_VARBIT_SENTINEL);
		draft.getOrder().add(b);

		var source = generator.generate(draft).getSource();
		int idx = source.indexOf("NpcStep ");
		assertTrue(idx >= 0);
		assertTrue(source.indexOf("NpcStep ", idx + 1) < 0, "expected single NpcStep field");
		assertTrue(source.contains("section1Task.addStep(not(talkGuardVarbitReq), talkGuard);"));
		assertTrue(source.contains("List.of(talkGuard, talkGuard)"));
	}
}
