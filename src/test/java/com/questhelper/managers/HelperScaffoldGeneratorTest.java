package com.questhelper.managers;

import net.runelite.api.coords.WorldPoint;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HelperScaffoldGeneratorTest
{
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
		draft.getSteps().add(step);

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
		draft.getSteps().add(first);

		HelperConstructModels.DraftStep second = new HelperConstructModels.DraftStep();
		second.setKind(HelperConstructModels.StepKind.NPC);
		second.setRawId(2);
		second.setSuggestedVarName("first step");
		second.setInstructionText("Second.");
		second.setTargetText("Second");
		second.setWorldPoint(new WorldPoint(3201, 3201, 0));
		draft.getSteps().add(second);

		var source = generator.generate(draft).getSource();
		assertTrue(source.contains("extends ComplexStateQuestHelper"));
		assertTrue(source.contains("public QuestStep loadStep()"));
		assertTrue(source.contains("ConditionalStep route = new ConditionalStep(this, firstStep);"));
		assertTrue(source.contains("route.addStep(not(firstStep2VarbitReq), firstStep2);"));
		assertTrue(source.contains("return route;"));
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

		HelperConstructModels.DraftStep itemStep = new HelperConstructModels.DraftStep();
		itemStep.setKind(HelperConstructModels.StepKind.ITEM);
		itemStep.setRawId(526);
		itemStep.setLinkedRequirementRawId(526);
		itemStep.setInstructionText("Use Bones.");
		itemStep.setSuggestedVarName("useBones");
		draft.getSteps().add(itemStep);

		var source = generator.generate(draft).getSource();
		assertTrue(source.contains("useBones = new ItemStep(this, \"Use Bones.\", bones.highlighted());"));
	}
}
