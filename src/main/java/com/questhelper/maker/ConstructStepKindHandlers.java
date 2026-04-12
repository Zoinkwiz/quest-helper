package com.questhelper.maker;

import com.questhelper.maker.construct.DraftRoutingIds;
import com.questhelper.managers.HelperScaffoldGenerator;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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

	static final class ConstructPreviewStepParams
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
				if (ir != null)
				{
					all.add(ir.highlighted());
				}
				else
				{
					all.add(new ItemRequirement("Item " + highlightRawId, highlightRawId).highlighted());
				}
			}
			for (Requirement r : p.extrasArr())
			{
				all.add(r);
			}
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
