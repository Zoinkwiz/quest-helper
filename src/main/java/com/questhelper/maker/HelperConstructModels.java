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
	 * Semantics for quest-order conditions:
	 * SHOW_WHEN_TRUE -> selector true means this row should be active;
	 * CONTINUE_WHEN_TRUE -> selector true means this row is considered done and flow should move forward.
	 */
	public enum OrderConditionMode
	{
		SHOW_WHEN_TRUE,
		CONTINUE_WHEN_TRUE
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
		/** When {@code kind} is {@code SKILL}: {@link net.runelite.api.Skill#name()}. */
		private String skillName;
		private Integer skillRequiredLevel;
		private String skillOperation;
		private String skillDisplayText;
		private boolean skillCanBeBoosted;
		/** When {@code kind} is {@link StepAttachmentKind#ITEM}: show {@link com.questhelper.requirements.item.ItemRequirement#highlighted()} in preview / codegen. */
		private boolean attachmentHighlighted;
		/**
		 * When {@code kind} is {@link StepAttachmentKind#ITEM}: required stack count for preview / codegen ({@code >= 1}).
		 * Ignored for other kinds; persisted as {@code 0} for varbit rows.
		 */
		private int itemQuantity;
		/** When {@code kind} is {@link StepAttachmentKind#ITEM}: whether item must be equipped. */
		private boolean itemMustBeEquipped;
		/** When {@code kind} is {@link StepAttachmentKind#WIDGET}: interface group id. */
		private Integer widgetGroupId;
		/** When {@code kind} is {@link StepAttachmentKind#WIDGET}: interface child id. */
		private Integer widgetChildId;
		/** Optional third-level child for direct child-child targeting. */
		private Integer widgetChildChildId;
		/** Optional item id filter for widget highlight matching. */
		private Integer widgetItemId;
		/** Optional text filter for widget highlight matching. */
		private String widgetDialogText;
		/** When item/text filters are used: whether to search descendants. */
		private boolean widgetCheckChildren;
		/**
		 * When {@code kind} is {@link StepAttachmentKind#VARBIT}: optional link to a quest-order {@link DraftOrderLine#getOrderSlotId()}
		 * for step-definition attachments that mirror a slot (importer / legacy). Order conditions store varbits inline as
		 * {@code VARBIT} tree nodes; the {@link DraftHelper#getVarbitRequirements()} registry is keyed by {@code orderSlotId}.
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
			int q = Math.max(itemQuantity, 1);
			DraftStepAttachedRequirement d = new DraftStepAttachedRequirement();
			d.setKind(StepAttachmentKind.ITEM.name());
			d.setItemRawId(rawId);
			d.setAttachmentHighlighted(attachmentHighlighted);
			d.setItemQuantity(q);
			return d;
		}

		public static DraftStepAttachedRequirement item(int rawId, boolean attachmentHighlighted, int itemQuantity, boolean itemMustBeEquipped)
		{
			DraftStepAttachedRequirement d = item(rawId, attachmentHighlighted, itemQuantity);
			d.setItemMustBeEquipped(itemMustBeEquipped);
			return d;
		}

		/** Extra (non–order-slot) varbit requirement on the step. */
		public static DraftStepAttachedRequirement varbit(int varbitId, int requiredValue, String operation, String displayText)
		{
			String op = operation == null || operation.isBlank() ? "EQUAL" : operation.trim();
			DraftStepAttachedRequirement d = new DraftStepAttachedRequirement();
			d.setKind(StepAttachmentKind.VARBIT.name());
			d.setVarbitId(varbitId);
			d.setVarbitRequiredValue(requiredValue);
			d.setVarbitOperation(op);
			d.setVarbitDisplayText(displayText);
			return d;
		}

		public static DraftStepAttachedRequirement skill(String skillName, int requiredLevel, String operation, String displayText, boolean canBeBoosted)
		{
			String op = operation == null || operation.isBlank() ? "GREATER_EQUAL" : operation.trim();
			int level = Math.max(requiredLevel, 1);
			DraftStepAttachedRequirement d = new DraftStepAttachedRequirement();
			d.setKind(StepAttachmentKind.SKILL.name());
			d.setSkillName(skillName);
			d.setSkillRequiredLevel(level);
			d.setSkillOperation(op);
			d.setSkillDisplayText(displayText);
			d.setSkillCanBeBoosted(canBeBoosted);
			return d;
		}

		/** Primary varbit routing row for one quest-order slot; matches {@link DraftOrderLine#getOrderSlotId()}. */
		public static DraftStepAttachedRequirement routingVarbitForOrderSlot(String orderSlotId, int varbitId, int requiredValue, String operation, String displayText)
		{
			String op = operation == null || operation.isBlank() ? "EQUAL" : operation.trim();
			DraftStepAttachedRequirement d = new DraftStepAttachedRequirement();
			d.setKind(StepAttachmentKind.VARBIT.name());
			d.setVarbitId(varbitId);
			d.setVarbitRequiredValue(requiredValue);
			d.setVarbitOperation(op);
			d.setVarbitDisplayText(displayText);
			d.setOrderSlotId(orderSlotId);
			return d;
		}

		public static DraftStepAttachedRequirement widgetByGroupChild(int groupId, int childId, Integer childChildId)
		{
			DraftStepAttachedRequirement d = new DraftStepAttachedRequirement();
			d.setKind(StepAttachmentKind.WIDGET.name());
			d.setWidgetGroupId(groupId);
			d.setWidgetChildId(childId);
			d.setWidgetChildChildId(childChildId);
			return d;
		}

		public static DraftStepAttachedRequirement widgetByItemId(int groupId, int childId, int itemId, boolean checkChildren)
		{
			DraftStepAttachedRequirement d = new DraftStepAttachedRequirement();
			d.setKind(StepAttachmentKind.WIDGET.name());
			d.setWidgetGroupId(groupId);
			d.setWidgetChildId(childId);
			d.setWidgetItemId(itemId);
			d.setWidgetCheckChildren(checkChildren);
			return d;
		}

		public static DraftStepAttachedRequirement widgetByDialogText(int groupId, int childId, String requiredText, boolean checkChildren)
		{
			DraftStepAttachedRequirement d = new DraftStepAttachedRequirement();
			d.setKind(StepAttachmentKind.WIDGET.name());
			d.setWidgetGroupId(groupId);
			d.setWidgetChildId(childId);
			d.setWidgetDialogText(requiredText == null ? null : requiredText.trim());
			d.setWidgetCheckChildren(true);
			return d;
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

		/** First VARBIT attachment on this order row (legacy persistence only; prefer {@link DraftHelper#getVarbitRequirements()}). */
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

		public static void syncHelperZoneRequirementsFromOrder(DraftHelper draft)
		{
			if (draft == null)
			{
				return;
			}
			draft.getZoneRequirements().clear();
			if (draft.getOrder() == null)
			{
				return;
			}
			for (DraftOrderLine line : draft.getOrder())
			{
				if (line == null
					|| line.getOrderSlotId() == null
					|| line.getOrderSlotId().isBlank()
					|| line.getZoneRoutingCorner1() == null
					|| line.getZoneRoutingCorner2() == null)
				{
					continue;
				}
				DraftZoneRequirement req = new DraftZoneRequirement();
				req.setRequirementId("zone:" + line.getOrderSlotId());
				req.setOrderSlotId(line.getOrderSlotId());
				req.setCorner1(line.getZoneRoutingCorner1());
				req.setCorner2(line.getZoneRoutingCorner2());
				req.setDisplayText(line.getZoneRoutingDisplayText());
				draft.getZoneRequirements().add(req);
			}
		}

		public static void applyHelperZoneRequirementsToOrder(DraftHelper draft)
		{
			if (draft == null || draft.getOrder() == null || draft.getZoneRequirements() == null)
			{
				return;
			}
			for (DraftOrderLine line : draft.getOrder())
			{
				if (line == null || line.getOrderSlotId() == null || line.getOrderSlotId().isBlank())
				{
					continue;
				}
				for (DraftZoneRequirement req : draft.getZoneRequirements())
				{
					if (req == null || req.getOrderSlotId() == null || !line.getOrderSlotId().equals(req.getOrderSlotId()))
					{
						continue;
					}
					line.setZoneRoutingCorner1(req.getCorner1());
					line.setZoneRoutingCorner2(req.getCorner2());
					line.setZoneRoutingDisplayText(req.getDisplayText());
					break;
				}
			}
		}
	}

	public enum StepAttachmentKind
	{
		ITEM,
		VARBIT,
		SKILL,
		WIDGET
	}

	@Data
	@NoArgsConstructor
	public static class DraftSkillRequirement
	{
		private String requirementId;
		private String skillName;
		private int requiredLevel = 1;
		private boolean canBeBoosted = true;
		private String displayText;
		private String operation = "GREATER_EQUAL";
	}

	@Data
	@NoArgsConstructor
	public static class DraftRequirement
	{
		private String requirementId;
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
		private final List<DraftStepAttachedRequirement> attachedRequirements = new ArrayList<>();
		/** Additional NPC/object ids (comma-separated in UI); first id is {@link #rawId}. */
		private final List<Integer> alternateRawIds = new ArrayList<>();
	}

	/**
	 * Recursive order-line requirement tree: branch requirement for {@link com.questhelper.steps.ConditionalStep#addStep}
	 * (when {@link com.questhelper.requirements.Requirement#check} is true, that child step is active for routing).
	 * <p>
	 * {@link #kind}: {@code GROUP}, {@code INVERT}, {@code ITEM}, {@code VARBIT}, {@code ZONE} (legacy loads may still contain {@code ORDER_VARBIT} / {@code ORDER_ZONE} until migrated).
	 * For {@code GROUP}, {@link #logic} is a {@link com.questhelper.requirements.util.LogicType} name ({@code AND}, {@code OR}, {@code NOR}, {@code NAND}).
	 * {@code INVERT} uses a single entry in {@link #children}. Leaves have empty {@link #children}.
	 */
	@Data
	@NoArgsConstructor
	public static class DraftOrderStepRequirement
	{
		private String kind;
		/** Reference to a requirement entry (item/varbit/zone) by stable id. */
		private String requirementRefId;
		private String logic;
		/** Gson fills this; must remain mutable for deserialization. */
		private List<DraftOrderStepRequirement> children = new ArrayList<>();
		private Integer itemRawId;
		private Integer itemQuantity;
		private boolean itemAlsoCheckBank;
		private boolean itemMustBeEquipped;
		private Integer varbitId;
		private Integer varbitRequiredValue;
		private String varbitOperation;
		private String varbitDisplayText;
		private String skillName;
		private Integer skillRequiredLevel;
		private String skillOperation;
		private String skillDisplayText;
		private boolean skillCanBeBoosted;
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

		/** Zone from this quest-order row’s {@link DraftOrderLine#getZoneRoutingCorner1()} / {@link DraftOrderLine#getZoneRoutingCorner2()} (Zone reqs tab). */
		public static DraftOrderStepRequirement orderZoneSlot()
		{
			DraftOrderStepRequirement n = new DraftOrderStepRequirement();
			n.setKind("ORDER_ZONE");
			return n;
		}

		public static DraftOrderStepRequirement item(int rawId)
		{
			return item(rawId, false, 1);
		}

		public static DraftOrderStepRequirement item(int rawId, boolean alsoCheckBank)
		{
			return item(rawId, alsoCheckBank, 1);
		}

		public static DraftOrderStepRequirement item(int rawId, boolean alsoCheckBank, int quantity)
		{
			return item(rawId, alsoCheckBank, quantity, false);
		}

		public static DraftOrderStepRequirement item(int rawId, boolean alsoCheckBank, int quantity, boolean mustBeEquipped)
		{
			DraftOrderStepRequirement n = new DraftOrderStepRequirement();
			n.setKind("ITEM");
			n.setRequirementRefId("item:" + rawId);
			n.setItemRawId(rawId);
			n.setItemQuantity(Math.max(1, quantity));
			n.setItemAlsoCheckBank(alsoCheckBank);
			n.setItemMustBeEquipped(mustBeEquipped);
			return n;
		}

		public static DraftOrderStepRequirement varbit(int varbitId, int requiredValue, String operation, String displayText)
		{
			DraftOrderStepRequirement n = new DraftOrderStepRequirement();
			n.setKind("VARBIT");
			n.setRequirementRefId("varbit:" + varbitId);
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
			String idPart1 = corner1 == null ? "0_0_0" : corner1.getX() + "_" + corner1.getY() + "_" + corner1.getPlane();
			String idPart2 = corner2 == null ? "0_0_0" : corner2.getX() + "_" + corner2.getY() + "_" + corner2.getPlane();
			n.setRequirementRefId("zone:" + idPart1 + ":" + idPart2);
			n.setZoneCorner1(corner1);
			n.setZoneCorner2(corner2);
			n.setZoneDisplayText(displayText);
			return n;
		}

		public static DraftOrderStepRequirement skill(String skillName, int requiredLevel, String operation, String displayText, boolean canBeBoosted)
		{
			DraftOrderStepRequirement n = new DraftOrderStepRequirement();
			n.setKind("SKILL");
			String op = operation == null || operation.isBlank() ? "GREATER_EQUAL" : operation.trim();
			int lvl = Math.max(requiredLevel, 1);
			String disp = displayText == null ? "" : displayText.trim();
			n.setRequirementRefId("skill:" + (skillName == null ? "" : skillName.trim()) + ":" + lvl + ":" + op + ":" + canBeBoosted + ":" + disp);
			n.setSkillName(skillName);
			n.setSkillRequiredLevel(lvl);
			n.setSkillOperation(op);
			n.setSkillDisplayText(displayText);
			n.setSkillCanBeBoosted(canBeBoosted);
			return n;
		}
	}

	/**
	 * Order row: section divider or ref to a definition.
	 * {@code linkedRequirementRawId}: optional captured item raw id for highlight / legacy item routing when no tree is set; otherwise unused.
	 * {@link #orderSlotId}: stable id for this slot ({@code ORDER_ZONE}, {@link DraftHelper#getVarbitRequirements()}, persistence).
	 * {@link #attachedRequirements}: order-scoped extras. Varbits in conditions use inline {@code VARBIT} nodes in {@link #stepRequirement}
	 * and/or entries in {@link DraftHelper#getVarbitRequirements()}.
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
		private String refStepId;
		private Integer linkedRequirementRawId;
		private DraftOrderStepRequirement stepRequirement;
		private OrderConditionMode stepRequirementMode;
		/**
		 * When true, once this row's completion requirement passes at least once, the row is auto-marked as manually
		 * skipped (persisted) so routing does not fall back even if the requirement later becomes false.
		 */
		private boolean passOnceCompletedOnce;
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
		/** Centralized routing varbit entries keyed by orderSlotId. */
		private final List<DraftVarbitRequirement> varbitRequirements = new ArrayList<>();
		/** Centralized zone requirements keyed by requirementId. */
		private final List<DraftZoneRequirement> zoneRequirements = new ArrayList<>();
		private final List<DraftRequirement> requirements = new ArrayList<>();
		private final List<DraftSkillRequirement> skillRequirements = new ArrayList<>();
	}

	@Data
	@NoArgsConstructor
	public static class DraftVarbitRequirement
	{
		private String requirementId;
		private String orderSlotId;
		private Integer varbitId;
		private Integer requiredValue;
		private String operation;
		private String displayText;
		/** Editor / labels; when blank, derived from {@link #displayText} on load and sync. */
		private String suggestedVarName;
	}

	@Data
	@NoArgsConstructor
	public static class DraftZoneRequirement
	{
		private String requirementId;
		private String orderSlotId;
		private WorldPoint corner1;
		private WorldPoint corner2;
		private String displayText;
	}
}
