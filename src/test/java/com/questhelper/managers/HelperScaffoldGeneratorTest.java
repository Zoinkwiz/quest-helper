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

		HelperConstructModels.DraftStep itemStep = new HelperConstructModels.DraftStep();
		itemStep.setKind(HelperConstructModels.StepKind.ITEM);
		itemStep.setRawId(526);
		itemStep.setLinkedRequirementRawId(526);
		itemStep.setInstructionText("Use Bones.");
		itemStep.setSuggestedVarName("useBones");
		draft.getSteps().add(itemStep);

		HelperConstructModels.DraftStep priorStep = new HelperConstructModels.DraftStep();
		priorStep.setKind(HelperConstructModels.StepKind.OBJECT);
		priorStep.setRawId(1000);
		priorStep.setInstructionText("First.");
		priorStep.setSuggestedVarName("firstStep");
		priorStep.setWorldPoint(new WorldPoint(3200, 3200, 0));
		draft.getSteps().add(0, priorStep);

		var source = generator.generate(draft).getSource();
		assertTrue(source.contains("useBones = new ItemStep(this, \"Use Bones.\", bones.highlighted());"));
		assertTrue(source.contains("section1Task.addStep(not(bones), useBones);"));
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
		draft.getSteps().add(step1);

		HelperConstructModels.DraftStep step2 = new HelperConstructModels.DraftStep();
		step2.setKind(HelperConstructModels.StepKind.NPC);
		step2.setRawId(2);
		step2.setSuggestedVarName("talktofarid");
		step2.setInstructionText("Talk to Farid.");
		step2.setWorldPoint(new WorldPoint(3201, 3201, 0));
		draft.getSteps().add(step2);

		HelperConstructModels.DraftStep step3 = new HelperConstructModels.DraftStep();
		step3.setKind(HelperConstructModels.StepKind.OBJECT);
		step3.setRawId(3);
		step3.setSuggestedVarName("killgoblin");
		step3.setInstructionText("Kill goblin.");
		step3.setWorldPoint(new WorldPoint(3202, 3202, 0));
		draft.getSteps().add(step3);

		HelperConstructModels.DraftStep divider = new HelperConstructModels.DraftStep();
		divider.setSectionDivider(true);
		divider.setSuggestedVarName("Second Section");
		draft.getSteps().add(divider);

		HelperConstructModels.DraftStep step4 = new HelperConstructModels.DraftStep();
		step4.setKind(HelperConstructModels.StepKind.ITEM);
		step4.setRawId(1712);
		step4.setSuggestedVarName("usedragonstoneamulet");
		step4.setInstructionText("Use dragonstone amulet.");
		draft.getSteps().add(step4);

		var source = generator.generate(draft).getSource();
		assertTrue(source.contains("allSections = new ConditionalStep(this, section2Task);"));
		assertTrue(source.contains("allSections.addStep(nor(examinebrynVarbitReq, talktofaridVarbitReq, killgoblinVarbitReq), section1Task);"));
		assertTrue(source.contains("section1Task = new ConditionalStep(this, killgoblin);"));
		assertTrue(source.contains("section1Task.addStep(not(examinebrynVarbitReq), examinebryn);"));
		assertTrue(source.contains("section1Task.addStep(not(talktofaridVarbitReq), talktofarid);"));
	}
}
