/*
 * Copyright (c) 2026, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.maker;

import com.questhelper.maker.construct.DraftRoutingIds;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.questhelper.maker.HelperConstructModels.DraftStep;
import static com.questhelper.maker.HelperConstructModels.DraftHelper;
import static com.questhelper.maker.HelperConstructModels.StepKind;

/**
 * Registry of per-{@link StepKind} behavior for maker preview and scaffold emission (NPC / Object / TEXT; legacy ITEM shares TEXT).
 */
public final class ConstructStepKindHandlers
{
	private static final EnumMap<StepKind, ConstructStepKindHandler> BY_KIND = new EnumMap<>(StepKind.class);

	static
	{
		NpcHandler npc = new NpcHandler();
		BY_KIND.put(StepKind.NPC, npc);
		BY_KIND.put(StepKind.OBJECT, new ObjectHandler());
		TextHandler text = new TextHandler();
		BY_KIND.put(StepKind.TEXT, text);
		BY_KIND.put(StepKind.ITEM, text);
	}

	private ConstructStepKindHandlers()
	{
	}

	private static int[] mergedNpcObjectIds(DraftStep d)
	{
		List<Integer> merged = DraftRoutingIds.mergedStepOrRequirementIds(d.getRawId(), d.getAlternateRawIds());
		int[] out = new int[merged.size()];
		for (int i = 0; i < merged.size(); i++)
		{
			out[i] = merged.get(i);
		}
		return out;
	}

	public static ConstructStepKindHandler forStepKind(StepKind kind)
	{
		return BY_KIND.get(kind);
	}

	public interface ConstructStepKindHandler
	{
		StepKind kind();

		QuestStep buildPreviewQuestStep(ConstructPreviewStepParams params);

		void appendScaffoldDefinitionSetup(ScaffoldDefinitionSetupContext ctx);

		/**
		 * Blank step row from the maker UI (Add step). Caller sets {@link DraftStep#setStepId(String)}.
		 */
		DraftStep createBlankStepForMakerUi();
	}

	public static final class ConstructPreviewStepParams
	{
		private final QuestHelper questHelper;
		private final DraftStep draftStep;
		private final String instruction;
		private final Requirement[] extrasArr;
		private final Map<Integer, ItemRequirement> requirementById;
		private final Integer itemHighlightRawId;

		ConstructPreviewStepParams(
			QuestHelper questHelper,
			DraftStep draftStep,
			String instruction,
			Requirement[] extrasArr,
			Map<Integer, ItemRequirement> requirementById,
			Integer itemHighlightRawId)
		{
			this.questHelper = questHelper;
			this.draftStep = draftStep;
			this.instruction = instruction;
			this.extrasArr = extrasArr;
			this.requirementById = requirementById;
			this.itemHighlightRawId = itemHighlightRawId;
		}

		QuestHelper questHelper()
		{
			return questHelper;
		}

		DraftStep draftStep()
		{
			return draftStep;
		}

		String instruction()
		{
			return instruction;
		}

		Requirement[] extrasArr()
		{
			return extrasArr;
		}

		Map<Integer, ItemRequirement> requirementById()
		{
			return requirementById;
		}

		Integer itemHighlightRawId()
		{
			return itemHighlightRawId;
		}
	}

	public static final class ScaffoldDefinitionSetupContext
	{
		private final HelperScaffoldGenerator generator;
		private final StringBuilder out;
		private final DraftHelper draft;
		private final DraftStep step;
		private final String varName;
		private final String instruction;
		private final Map<Integer, String> requirementVarNamesByRawId;
		private final List<String> warnings;

		public ScaffoldDefinitionSetupContext(
			HelperScaffoldGenerator generator,
			StringBuilder out,
			DraftHelper draft,
			DraftStep step,
			String varName,
			String instruction,
			Map<Integer, String> requirementVarNamesByRawId,
			List<String> warnings)
		{
			this.generator = generator;
			this.out = out;
			this.draft = draft;
			this.step = step;
			this.varName = varName;
			this.instruction = instruction;
			this.requirementVarNamesByRawId = requirementVarNamesByRawId;
			this.warnings = warnings;
		}

		HelperScaffoldGenerator generator()
		{
			return generator;
		}

		StringBuilder out()
		{
			return out;
		}

		DraftHelper draft()
		{
			return draft;
		}

		DraftStep step()
		{
			return step;
		}

		String varName()
		{
			return varName;
		}

		String instruction()
		{
			return instruction;
		}

		Map<Integer, String> requirementVarNamesByRawId()
		{
			return requirementVarNamesByRawId;
		}

		List<String> warnings()
		{
			return warnings;
		}
	}

	private static final class NpcHandler implements ConstructStepKindHandler
	{
		@Override
		public StepKind kind()
		{
			return StepKind.NPC;
		}

		@Override
		public QuestStep buildPreviewQuestStep(ConstructPreviewStepParams p)
		{
			DraftStep d = p.draftStep();
			int[] ids = mergedNpcObjectIds(d);
			WorldPoint wp = d.getWorldPoint();
			return new NpcStep(p.questHelper(), ids, wp, p.instruction(), true, p.extrasArr());
		}

		@Override
		public void appendScaffoldDefinitionSetup(ScaffoldDefinitionSetupContext c)
		{
			c.generator().appendNpcObjectDefinitionSetup(c.out(), c.step(), c.varName(), c.instruction(), c.warnings());
		}

		@Override
		public DraftStep createBlankStepForMakerUi()
		{
			DraftStep step = new DraftStep();
			step.setKind(StepKind.NPC);
			step.setRawId(0);
			step.setInstructionText("");
			step.setTargetText("");
			step.setPanelName("Captured Steps");
			step.setWorldPoint(null);
			step.setOption("");
			step.setSectionDivider(false);
			step.setSuggestedVarName(HelperScaffoldGenerator.toVarName("npc step", "step"));
			return step;
		}
	}

	private static final class ObjectHandler implements ConstructStepKindHandler
	{
		@Override
		public StepKind kind()
		{
			return StepKind.OBJECT;
		}

		@Override
		public QuestStep buildPreviewQuestStep(ConstructPreviewStepParams p)
		{
			DraftStep d = p.draftStep();
			int[] ids = mergedNpcObjectIds(d);
			if (d.getWorldPoint() != null)
			{
				ObjectStep s = new ObjectStep(p.questHelper(), ids[0], d.getWorldPoint(), p.instruction(), true, p.extrasArr());
				for (int i = 1; i < ids.length; i++)
				{
					s.addAlternateObjects(ids[i]);
				}
				return s;
			}
			ObjectStep s = new ObjectStep(p.questHelper(), ids[0], p.instruction(), true, p.extrasArr());
			for (int i = 1; i < ids.length; i++)
			{
				s.addAlternateObjects(ids[i]);
			}
			return s;
		}

		@Override
		public void appendScaffoldDefinitionSetup(ScaffoldDefinitionSetupContext c)
		{
			c.generator().appendNpcObjectDefinitionSetup(c.out(), c.step(), c.varName(), c.instruction(), c.warnings());
		}

		@Override
		public DraftStep createBlankStepForMakerUi()
		{
			DraftStep step = new DraftStep();
			step.setKind(StepKind.OBJECT);
			step.setRawId(0);
			step.setInstructionText("");
			step.setTargetText("");
			step.setPanelName("Captured Steps");
			step.setWorldPoint(null);
			step.setOption("");
			step.setSectionDivider(false);
			step.setSuggestedVarName(HelperScaffoldGenerator.toVarName("object step", "step"));
			return step;
		}
	}

	private static final class TextHandler implements ConstructStepKindHandler
	{
		@Override
		public StepKind kind()
		{
			return StepKind.TEXT;
		}

		@Override
		public QuestStep buildPreviewQuestStep(ConstructPreviewStepParams p)
		{
			DraftStep d = p.draftStep();
			List<Requirement> all = new ArrayList<>();
			Integer highlightRawId = p.itemHighlightRawId();
			if (highlightRawId != null)
			{
				ItemRequirement ir = p.requirementById().get(highlightRawId);
				all.add(Objects.requireNonNullElseGet(ir, () -> new ItemRequirement("Item " + highlightRawId, highlightRawId)).highlighted());
			}
			all.addAll(Arrays.asList(p.extrasArr()));
			Requirement[] arr = all.toArray(new Requirement[0]);
			if (d.getWorldPoint() != null)
			{
				return new DetailedQuestStep(p.questHelper(), d.getWorldPoint(), p.instruction(), arr);
			}
			if (arr.length == 0)
			{
				return new DetailedQuestStep(p.questHelper(), p.instruction());
			}
			return new DetailedQuestStep(p.questHelper(), p.instruction(), arr);
		}

		@Override
		public void appendScaffoldDefinitionSetup(ScaffoldDefinitionSetupContext c)
		{
			c.generator().appendTextGenericDefinitionSetup(c.out(), c.step(), c.varName(), c.instruction());
		}

		@Override
		public DraftStep createBlankStepForMakerUi()
		{
			DraftStep step = new DraftStep();
			step.setKind(StepKind.TEXT);
			step.setRawId(0);
			step.setInstructionText("");
			step.setTargetText("");
			step.setPanelName("Captured Steps");
			step.setWorldPoint(null);
			step.setOption("");
			step.setSectionDivider(false);
			step.setSuggestedVarName(HelperScaffoldGenerator.toVarName("generic step", "step"));
			return step;
		}
	}
}
