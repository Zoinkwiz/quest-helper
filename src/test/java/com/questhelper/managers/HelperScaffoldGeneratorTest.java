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
}
