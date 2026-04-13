package com.questhelper.maker;

import javax.annotation.Nullable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.List;

public final class HelperConstructModels
{
	private HelperConstructModels()
	{
	}

	public enum StepKind
	{
		NPC,
		OBJECT,
		ITEM,
		TEXT
	}

	public enum IdType
	{
		NPC,
		OBJECT,
		ITEM,
		VARBIT
	}

	/**
	 * Extra requirements on a step definition (shown in preview / emitted on the step).
	 * {@code kind} is extensible for future types (unknown kinds are skipped at runtime with a warning in codegen).
	 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DraftStepAttachedRequirement
	{
		/** {@link StepAttachmentKind#name()} or future discriminator. */
		private String kind;
		/** When {@code kind} is {@code ITEM}: captured / item raw id. */
		private Integer itemRawId;
		/** When {@code kind} is {@code VARBIT}: varbit id. */
		private Integer varbitId;
		private Integer varbitRequiredValue;
		/** {@link com.questhelper.requirements.util.Operation} name. */
		private String varbitOperation;
		private String varbitDisplayText;
		/** When {@code kind} is {@link StepAttachmentKind#ITEM}: show {@link com.questhelper.requirements.item.ItemRequirement#highlighted()} in preview / codegen. */
		private boolean attachmentHighlighted;
		/**
		 * When {@code kind} is {@link StepAttachmentKind#ITEM}: required stack count for preview / codegen ({@code >= 1}).
		 * Ignored for other kinds; persisted as {@code 0} for varbit rows.
		 */
		private int itemQuantity;
		/**
		 * Legacy only: when {@code kind} is {@link StepAttachmentKind#VARBIT} and set, this was the routing varbit for the
		 * quest-order slot with the same id; new drafts store routing varbits on {@link DraftOrderLine#getAttachedRequirements()}
		 * instead. Load migrates these onto the matching order line. Inline (non-routing) varbits on a step definition use
		 * {@code null} here.
		 */
		private String orderSlotId;

		public static DraftStepAttachedRequirement item(int rawId)
		{
			return item(rawId, false);
		}

		public static DraftStepAttachedRequirement item(int rawId, boolean attachmentHighlighted)
		{
			return item(rawId, attachmentHighlighted, 1);
		}

		public static DraftStepAttachedRequirement item(int rawId, boolean attachmentHighlighted, int itemQuantity)
		{
			int q = itemQuantity < 1 ? 1 : itemQuantity;
			return new DraftStepAttachedRequirement(StepAttachmentKind.ITEM.name(), rawId, null, null, null, null, attachmentHighlighted, q, null);
		}

		/** Extra (non–order-slot) varbit requirement on the step. */
		public static DraftStepAttachedRequirement varbit(int varbitId, int requiredValue, String operation, String displayText)
		{
			String op = operation == null || operation.isBlank() ? "EQUAL" : operation.trim();
			return new DraftStepAttachedRequirement(StepAttachmentKind.VARBIT.name(), null, varbitId, requiredValue, op, displayText, false, 0, null);
		}

		/** Primary varbit routing row for one quest-order slot; matches {@link DraftOrderLine#getOrderSlotId()}. */
		public static DraftStepAttachedRequirement routingVarbitForOrderSlot(String orderSlotId, int varbitId, int requiredValue, String operation, String displayText)
		{
			String op = operation == null || operation.isBlank() ? "EQUAL" : operation.trim();
			return new DraftStepAttachedRequirement(StepAttachmentKind.VARBIT.name(), null, varbitId, requiredValue, op, displayText, false, 0, orderSlotId);
		}

		public static DraftStepAttachedRequirement findRoutingVarbitForSlot(DraftStep step, String orderSlotId)
		{
			if (step == null || step.getAttachedRequirements() == null || orderSlotId == null || orderSlotId.isBlank())
			{
				return null;
			}
			for (DraftStepAttachedRequirement a : step.getAttachedRequirements())
			{
				if (!StepAttachmentKind.VARBIT.name().equalsIgnoreCase(a.getKind()))
				{
					continue;
				}
				if (orderSlotId.equals(a.getOrderSlotId()))
				{
					return a;
				}
			}
			return null;
		}

		public static void removeRoutingVarbitForSlot(DraftStep step, String orderSlotId)
		{
			if (step == null || step.getAttachedRequirements() == null || orderSlotId == null || orderSlotId.isBlank())
			{
				return;
			}
			step.getAttachedRequirements().removeIf(a ->
				StepAttachmentKind.VARBIT.name().equalsIgnoreCase(a.getKind()) && orderSlotId.equals(a.getOrderSlotId()));
		}

		/** First VARBIT attachment on this order row (routing config for {@code ORDER_VARBIT} in the conditions tree). */
		@Nullable
		public static DraftStepAttachedRequirement findOrderRoutingVarbit(DraftOrderLine line)
		{
			if (line == null || line.getAttachedRequirements() == null)
			{
				return null;
			}
			for (DraftStepAttachedRequirement a : line.getAttachedRequirements())
			{
				if (StepAttachmentKind.VARBIT.name().equalsIgnoreCase(a.getKind()))
				{
					return a;
				}
			}
			return null;
		}

		public static void clearVarbitAttachmentsOnOrderLine(DraftOrderLine line)
		{
			if (line == null || line.getAttachedRequirements() == null)
			{
				return;
			}
			line.getAttachedRequirements().removeIf(a -> StepAttachmentKind.VARBIT.name().equalsIgnoreCase(a.getKind()));
		}

		/** Replaces any VARBIT rows on {@code line} with {@code routing} (clears {@code orderSlotId} on the stored copy). */
		public static void setOrderLineRoutingVarbit(DraftOrderLine line, DraftStepAttachedRequirement routing)
		{
			if (line == null || routing == null)
			{
				return;
			}
			clearVarbitAttachmentsOnOrderLine(line);
			routing.setOrderSlotId(null);
			line.getAttachedRequirements().add(0, routing);
		}
	}

	public enum StepAttachmentKind
	{
		ITEM,
		VARBIT
	}

	@Data
	@NoArgsConstructor
	public static class DraftRequirement
	{
		private int rawId;
		private String displayName;
		/** Additional item ids (comma-separated in UI); first id is {@link #rawId}. */
		private final List<Integer> alternateRawIds = new ArrayList<>();
	}

	@Data
	@NoArgsConstructor
	public static class DraftStep
	{
		private String stepId;
		private StepKind kind;
		private boolean sectionDivider;
		/**
		 * Optional league / external struct id persisted in draft JSON only; not read by codegen or preview logic.
		 */
		private Integer structId;
		private int rawId;
		private WorldPoint worldPoint;
		private String option;
		private String targetText;
		private String suggestedVarName;
		private String instructionText;
		private String panelName;
		private String sectionCondition;
		private boolean skipWhenConditionMet;
		private final List<DraftStepAttachedRequirement> attachedRequirements = new ArrayList<>();
		/** Additional NPC/object ids (comma-separated in UI); first id is {@link #rawId}. */
		private final List<Integer> alternateRawIds = new ArrayList<>();
	}

	/**
	 * Recursive order-line requirement tree: branch requirement for {@link com.questhelper.steps.ConditionalStep#addStep}
	 * (when {@link com.questhelper.requirements.Requirement#check} is true, that child step is active for routing).
	 * <p>
	 * {@link #kind}: {@code GROUP}, {@code INVERT}, {@code ITEM}, {@code ORDER_VARBIT}, {@code VARBIT}, {@code ZONE}.
	 * For {@code GROUP}, {@link #logic} is a {@link com.questhelper.requirements.util.LogicType} name ({@code AND}, {@code OR}, {@code NOR}, {@code NAND}).
	 * {@code INVERT} uses a single entry in {@link #children}. Leaves have empty {@link #children}.
	 */
	@Data
	@NoArgsConstructor
	public static class DraftOrderStepRequirement
	{
		private String kind;
		private String logic;
		/** Gson fills this; must remain mutable for deserialization. */
		private List<DraftOrderStepRequirement> children = new ArrayList<>();
		private Integer itemRawId;
		private Integer varbitId;
		private Integer varbitRequiredValue;
		private String varbitOperation;
		private String varbitDisplayText;
		/** When {@code kind} is {@code ZONE}: inclusive axis-aligned box (two opposite corners). */
		private WorldPoint zoneCorner1;
		private WorldPoint zoneCorner2;
		/** Optional label for {@link com.questhelper.requirements.zone.ZoneRequirement} in preview/codegen. */
		private String zoneDisplayText;

		public static DraftOrderStepRequirement group(String logicTypeName, DraftOrderStepRequirement... ch)
		{
			DraftOrderStepRequirement n = new DraftOrderStepRequirement();
			n.setKind("GROUP");
			n.setLogic(logicTypeName);
			for (DraftOrderStepRequirement c : ch)
			{
				if (c != null)
				{
					n.getChildren().add(c);
				}
			}
			return n;
		}

		public static DraftOrderStepRequirement invert(DraftOrderStepRequirement inner)
		{
			DraftOrderStepRequirement n = new DraftOrderStepRequirement();
			n.setKind("INVERT");
			if (inner != null)
			{
				n.getChildren().add(inner);
			}
			return n;
		}

		/** Varbit from the referenced step’s routing attachment for this row’s {@link DraftOrderLine#getOrderSlotId()}. */
		public static DraftOrderStepRequirement orderVarbitSlot()
		{
			DraftOrderStepRequirement n = new DraftOrderStepRequirement();
			n.setKind("ORDER_VARBIT");
			return n;
		}

		/** Zone from this quest-order row’s {@link DraftOrderLine#getZoneRoutingCorner1()} / {@link DraftOrderLine#getZoneRoutingCorner2()} (Zone reqs tab). */
		public static DraftOrderStepRequirement orderZoneSlot()
		{
			DraftOrderStepRequirement n = new DraftOrderStepRequirement();
			n.setKind("ORDER_ZONE");
			return n;
		}

		public static DraftOrderStepRequirement item(int rawId)
		{
			DraftOrderStepRequirement n = new DraftOrderStepRequirement();
			n.setKind("ITEM");
			n.setItemRawId(rawId);
			return n;
		}

		public static DraftOrderStepRequirement varbit(int varbitId, int requiredValue, String operation, String displayText)
		{
			DraftOrderStepRequirement n = new DraftOrderStepRequirement();
			n.setKind("VARBIT");
			n.setVarbitId(varbitId);
			n.setVarbitRequiredValue(requiredValue);
			n.setVarbitOperation(operation == null || operation.isBlank() ? "EQUAL" : operation.trim());
			n.setVarbitDisplayText(displayText);
			return n;
		}

		/** Player must be inside the rectangle defined by the two corners (same semantics as {@link com.questhelper.requirements.zone.Zone}). */
		public static DraftOrderStepRequirement zone(WorldPoint corner1, WorldPoint corner2, String displayText)
		{
			DraftOrderStepRequirement n = new DraftOrderStepRequirement();
			n.setKind("ZONE");
			n.setZoneCorner1(corner1);
			n.setZoneCorner2(corner2);
			n.setZoneDisplayText(displayText);
			return n;
		}
	}

	/**
	 * Order row: section divider or ref to a definition.
	 * {@code linkedRequirementRawId}: optional captured item raw id for highlight / legacy item routing when no tree is set; otherwise unused.
	 * {@link #orderSlotId}: stable id for this slot (conditions tree {@code ORDER_VARBIT}, {@code ORDER_ZONE}, and persistence).
	 * {@link #attachedRequirements}: order-scoped extras (e.g. VARBIT routing for this slot). Step definitions keep only
	 * inline varbits and item attachments that apply to every use of the step.
	 * Zone corners for {@code ORDER_ZONE} are stored on the line ({@link #zoneRoutingCorner1} / {@link #zoneRoutingCorner2}), edited on the Zone reqs tab.
	 * When {@link #stepRequirement} is set, preview and codegen use it as the game branch selector (see {@link DraftOrderStepRequirement}).
	 */
	@Data
	@NoArgsConstructor
	public static class DraftOrderLine
	{
		private boolean sectionDivider;
		private String orderSlotId;
		private String suggestedVarName;
		private String sectionCondition;
		private boolean skipWhenConditionMet;
		private String refStepId;
		private Integer linkedRequirementRawId;
		private DraftOrderStepRequirement stepRequirement;
		private final List<DraftStepAttachedRequirement> attachedRequirements = new ArrayList<>();
		/** When conditions use {@code ORDER_ZONE}: rectangle corners (Zone reqs tab). */
		private WorldPoint zoneRoutingCorner1;
		private WorldPoint zoneRoutingCorner2;
		private String zoneRoutingDisplayText;
	}

	@Data
	@NoArgsConstructor
	public static class DraftHelper
	{
		private String questName = "Generated Quest";
		private String className = "GeneratedQuestHelper";
		private String packagePath = "com.questhelper.helpers.quests.generated";
		private String helperType = "BasicQuestHelper";
		private final List<DraftStep> stepDefinitions = new ArrayList<>();
		private final List<DraftOrderLine> order = new ArrayList<>();
		private final List<DraftRequirement> requirements = new ArrayList<>();
	}

	/** Sentinel for order line: use varbit (not item requirement) for routing on this slot. */
	public static final int ORDER_ROUTING_VARBIT_SENTINEL = -1;
}
