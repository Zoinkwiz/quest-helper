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

import com.questhelper.maker.HelperConstructModels.OrderConditionMode;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;

import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.questhelper.maker.HelperConstructModels.DraftHelper;
import static com.questhelper.maker.HelperConstructModels.DraftOrderLine;
import static com.questhelper.maker.HelperConstructModels.DraftOrderStepRequirement;
import static com.questhelper.maker.HelperConstructModels.DraftStep;
import static com.questhelper.maker.HelperConstructModels.DraftStepAttachedRequirement;
import static com.questhelper.requirements.util.LogicHelper.not;

/**
 * Load-time migration from legacy {@link DraftOrderLine#getLinkedRequirementRawId()} into
 * {@link DraftOrderLine#getStepRequirement()}, runtime evaluation for preview, and Java emission for codegen.
 */
public final class OrderStepRequirementSupport
{
	private OrderStepRequirementSupport()
	{
	}

	/**
	 * When {@link DraftOrderLine#getStepRequirement()} is absent, synthesize a tree only for legacy
	 * positive {@link DraftOrderLine#getLinkedRequirementRawId()} (item) rows. No default varbit routing is applied.
	 */
	public static void normalizeLoadedDraft(DraftHelper draft)
	{
		if (draft == null || draft.getOrder() == null)
		{
			return;
		}
		for (DraftOrderLine line : draft.getOrder())
		{
			if (line.isSectionDivider())
			{
				continue;
			}
			if (line.getStepRequirement() == null)
			{
				DraftStep def = findDefinition(draft, line.getRefStepId());
				DraftOrderStepRequirement syn = synthesizeLegacyTreeForOrderRow(line, def);
				if (syn != null)
				{
					line.setStepRequirement(syn);
				}
			}
			if (line.getStepRequirement() != null)
			{
				upgradeLegacyKindsInTree(line.getStepRequirement());
				migrateLegacyOrderLeavesToInline(line, line.getStepRequirement());
			}
		}
	}

	public static OrderConditionMode effectiveConditionMode(@Nullable DraftOrderLine line)
	{
		if (line == null || line.getStepRequirementMode() == null)
		{
			return OrderConditionMode.SHOW_WHEN_TRUE;
		}
		return line.getStepRequirementMode();
	}

	/**
	 * Upgrades legacy kinds and migrates legacy row-slot leaves ({@code ORDER_VARBIT}/{@code ORDER_ZONE}) to inline
	 * {@code VARBIT}/{@code ZONE} nodes, then validates.
	 */
	@Nullable
	public static String prepareOrderStepTreeForPersistence(@Nullable DraftOrderLine line, @Nullable DraftOrderStepRequirement tree)
	{
		if (tree == null)
		{
			return null;
		}
		upgradeLegacyKindsInTree(tree);
		if (line != null && !line.isSectionDivider())
		{
			String mig = migrateLegacyOrderLeavesToInline(line, tree);
			if (mig != null)
			{
				return mig;
			}
		}
		return validateTreeOrError(tree);
	}

	@Nullable
	private static String migrateLegacyOrderLeavesToInline(DraftOrderLine line, DraftOrderStepRequirement root)
	{
		if (line == null || line.isSectionDivider() || root == null)
		{
			return null;
		}
		return migrateLegacyOrderLeavesToInlineRecursive(line, root);
	}

	@Nullable
	private static String migrateLegacyOrderLeavesToInlineRecursive(DraftOrderLine line, DraftOrderStepRequirement node)
	{
		if (node == null)
		{
			return null;
		}
		String k = node.getKind() == null ? "" : node.getKind().trim().toUpperCase(Locale.ROOT);
		if ("ORDER_VARBIT".equals(k) || "ROUTING_VARBIT".equals(k))
		{
			DraftStepAttachedRequirement cfg = DraftStepAttachedRequirement.findOrderRoutingVarbit(line);
			if (cfg == null)
			{
				return "Order varbit leaf requires a varbit configured on this row.";
			}
			VarbitSpec spec = VarbitSpec.fromStepAttachment(cfg);
			node.setKind("VARBIT");
			node.setVarbitId(spec.getVarbitId());
			node.setVarbitRequiredValue(spec.getRequiredValue());
			node.setVarbitOperation(spec.getOperation().name());
			node.setVarbitDisplayText(spec.getDisplayText());
			node.setItemRawId(null);
			node.setItemQuantity(null);
			node.setItemAlsoCheckBank(false);
		}
		else if ("ORDER_ZONE".equals(k))
		{
			if (line.getZoneRoutingCorner1() == null || line.getZoneRoutingCorner2() == null)
			{
				return "Order zone leaf requires both zone corners on this row.";
			}
			node.setKind("ZONE");
			node.setZoneCorner1(line.getZoneRoutingCorner1());
			node.setZoneCorner2(line.getZoneRoutingCorner2());
			node.setZoneDisplayText(line.getZoneRoutingDisplayText());
			node.setItemRawId(null);
			node.setItemQuantity(null);
			node.setItemAlsoCheckBank(false);
		}
		if (node.getChildren() != null)
		{
			for (DraftOrderStepRequirement ch : node.getChildren())
			{
				String err = migrateLegacyOrderLeavesToInlineRecursive(line, ch);
				if (err != null)
				{
					return err;
				}
			}
		}
		return null;
	}

	/** Renames persisted {@code kind} strings from older drafts to current names ({@code ITEM}, {@code ORDER_VARBIT}, {@code VARBIT}). */
	public static void upgradeLegacyKindsInTree(@Nullable DraftOrderStepRequirement node)
	{
		if (node == null)
		{
			return;
		}
		String k = node.getKind() == null ? "" : node.getKind().trim().toUpperCase(Locale.ROOT);
		switch (k)
		{
			case "CAPTURED_ITEM":
				node.setKind("ITEM");
				break;
			case "ROUTING_VARBIT":
				node.setKind("ORDER_VARBIT");
				break;
			case "INLINE_VARBIT":
				node.setKind("VARBIT");
				break;
			default:
				break;
		}
		if (node.getChildren() != null)
		{
			for (DraftOrderStepRequirement ch : node.getChildren())
			{
				upgradeLegacyKindsInTree(ch);
			}
		}
	}

	/** True when the tree contains an {@code ORDER_VARBIT} leaf (needs a per-slot {@link VarbitRequirement} field in codegen). */
	public static boolean treeContainsOrderVarbitLeaf(@Nullable DraftOrderStepRequirement root)
	{
		if (root == null)
		{
			return false;
		}
		String k = root.getKind() == null ? "" : root.getKind().trim().toUpperCase(Locale.ROOT);
		if ("ORDER_VARBIT".equals(k) || "ROUTING_VARBIT".equals(k))
		{
			return true;
		}
		if (root.getChildren() != null)
		{
			for (DraftOrderStepRequirement ch : root.getChildren())
			{
				if (treeContainsOrderVarbitLeaf(ch))
				{
					return true;
				}
			}
		}
		return false;
	}

	/** True when generated Java needs {@link com.questhelper.requirements.zone.Zone} / {@link com.questhelper.requirements.zone.ZoneRequirement} imports. */
	public static boolean draftUsesZoneRequirement(@Nullable DraftHelper draft)
	{
		if (draft == null || draft.getOrder() == null)
		{
			return false;
		}
		for (DraftOrderLine line : draft.getOrder())
		{
			if (line == null || line.isSectionDivider())
			{
				continue;
			}
			if (treeContainsZoneLeaf(line.getStepRequirement())
				|| treeContainsOrderZoneLeaf(line.getStepRequirement())
				|| orderLineHasZoneRoutingCorners(line))
			{
				return true;
			}
		}
		return false;
	}

	/** True when this order row has zone corners persisted for the Zone reqs tab / {@code ORDER_ZONE}. */
	public static boolean orderLineHasZoneRoutingCorners(@Nullable DraftOrderLine line)
	{
		return line != null && line.getZoneRoutingCorner1() != null && line.getZoneRoutingCorner2() != null;
	}

	/** True when the Zone reqs tab should list this row (slot leaf in tree or zone corners already set on the line). */
	public static boolean orderLineUsesZoneRoutingForEditor(@Nullable DraftOrderLine line)
	{
		if (line == null || line.isSectionDivider())
		{
			return false;
		}
		return treeContainsOrderZoneLeaf(line.getStepRequirement())
			|| treeContainsZoneLeaf(line.getStepRequirement())
			|| orderLineHasZoneRoutingCorners(line);
	}

	public static boolean treeContainsAnyVarbitLeaf(@Nullable DraftOrderStepRequirement root)
	{
		if (root == null)
		{
			return false;
		}
		String k = root.getKind() == null ? "" : root.getKind().trim().toUpperCase(Locale.ROOT);
		if ("ORDER_VARBIT".equals(k) || "ROUTING_VARBIT".equals(k) || "VARBIT".equals(k) || "INLINE_VARBIT".equals(k))
		{
			return true;
		}
		if (root.getChildren() != null)
		{
			for (DraftOrderStepRequirement ch : root.getChildren())
			{
				if (treeContainsAnyVarbitLeaf(ch))
				{
					return true;
				}
			}
		}
		return false;
	}

	/** Clears orphan zone routing on order lines (e.g. before codegen). */
	public static void normalizeZoneRoutingOnDraft(@Nullable DraftHelper draft)
	{
		if (draft == null || draft.getOrder() == null)
		{
			return;
		}
		for (DraftOrderLine line : draft.getOrder())
		{
			if (line == null || line.isSectionDivider())
			{
				continue;
			}
			if (!orderLineUsesZoneRoutingForEditor(line))
			{
				line.setZoneRoutingCorner1(null);
				line.setZoneRoutingCorner2(null);
				line.setZoneRoutingDisplayText(null);
			}
		}
	}

	/** True when the tree still has legacy inline {@code ZONE} leaves (corners on the node; migrate to tab + {@code ORDER_ZONE}). */
	public static boolean treeContainsZoneLeaf(@Nullable DraftOrderStepRequirement root)
	{
		if (root == null)
		{
			return false;
		}
		String k = root.getKind() == null ? "" : root.getKind().trim().toUpperCase(Locale.ROOT);
		if ("ZONE".equals(k))
		{
			return true;
		}
		if (root.getChildren() != null)
		{
			for (DraftOrderStepRequirement ch : root.getChildren())
			{
				if (treeContainsZoneLeaf(ch))
				{
					return true;
				}
			}
		}
		return false;
	}

	public static boolean treeContainsOrderZoneLeaf(@Nullable DraftOrderStepRequirement root)
	{
		if (root == null)
		{
			return false;
		}
		String k = root.getKind() == null ? "" : root.getKind().trim().toUpperCase(Locale.ROOT);
		if ("ORDER_ZONE".equals(k))
		{
			return true;
		}
		if (root.getChildren() != null)
		{
			for (DraftOrderStepRequirement ch : root.getChildren())
			{
				if (treeContainsOrderZoneLeaf(ch))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Collects {@code VARBIT} / {@code INLINE_VARBIT} leaves, ensures a single config, syncs
	 * {@link DraftStepAttachedRequirement#setOrderLineRoutingVarbit}, and rewrites each leaf to {@code ORDER_VARBIT}.
	 *
	 * @return {@code null} when nothing to do or migration succeeded; otherwise a short error (caller should not persist).
	 */
	@Nullable
	public static String migrateInlineVarbitLeavesToOrderRouting(DraftOrderLine line, DraftOrderStepRequirement root)
	{
		if (line == null || line.isSectionDivider() || root == null)
		{
			return null;
		}
		List<DraftOrderStepRequirement> leaves = new ArrayList<>();
		collectVarbitLikeLeaves(root, leaves);
		if (leaves.isEmpty())
		{
			return null;
		}
		for (DraftOrderStepRequirement n : leaves)
		{
			if (n.getVarbitId() == null)
			{
				return "VARBIT needs varbitId.";
			}
		}
		Set<String> signatures = new LinkedHashSet<>();
		for (DraftOrderStepRequirement n : leaves)
		{
			signatures.add(persistedVarbitSignature(n));
		}
		if (signatures.size() > 1)
		{
			return "This order row mixes different varbit configs in the conditions tree. Use one varbit per row, or split across multiple quest-order rows. Set values on the Varbit reqs tab and use only \"Order varbit (slot)\" in conditions.";
		}
		DraftOrderStepRequirement sample = leaves.get(0);
		int reqVal = sample.getVarbitRequiredValue() == null ? 1 : sample.getVarbitRequiredValue();
		String op = sample.getVarbitOperation() == null || sample.getVarbitOperation().isBlank()
			? "EQUAL"
			: sample.getVarbitOperation().trim();
		VarbitSpec fromTree = VarbitSpec.fromStepAttachment(DraftStepAttachedRequirement.varbit(
			sample.getVarbitId(), reqVal, op, sample.getVarbitDisplayText()));
		DraftStepAttachedRequirement existing = DraftStepAttachedRequirement.findOrderRoutingVarbit(line);
		if (existing != null)
		{
			VarbitSpec fromLine = VarbitSpec.fromStepAttachment(existing);
			if (!varbitSpecsMatchForOrderRouting(fromTree, fromLine))
			{
				return "The Varbit reqs tab value for this row does not match varbit leaves in conditions. Edit the varbit on the Varbit tab, or remove conflicting nodes.";
			}
		}
		else
		{
			DraftStepAttachedRequirement.setOrderLineRoutingVarbit(line, DraftStepAttachedRequirement.varbit(
				fromTree.getVarbitId(), fromTree.getRequiredValue(), fromTree.getOperation().name(), fromTree.getDisplayText()));
		}
		for (DraftOrderStepRequirement n : leaves)
		{
			convertVarbitLeafToOrderSlot(n);
		}
		return null;
	}

	private static void collectVarbitLikeLeaves(DraftOrderStepRequirement node, List<DraftOrderStepRequirement> out)
	{
		if (node == null)
		{
			return;
		}
		String k = node.getKind() == null ? "" : node.getKind().trim().toUpperCase(Locale.ROOT);
		if ("VARBIT".equals(k) || "INLINE_VARBIT".equals(k))
		{
			out.add(node);
			return;
		}
		if (node.getChildren() != null)
		{
			for (DraftOrderStepRequirement ch : node.getChildren())
			{
				collectVarbitLikeLeaves(ch, out);
			}
		}
	}

	private static String persistedVarbitSignature(DraftOrderStepRequirement n)
	{
		int reqVal = n.getVarbitRequiredValue() == null ? 1 : n.getVarbitRequiredValue();
		String op = n.getVarbitOperation() == null || n.getVarbitOperation().isBlank()
			? "EQUAL"
			: n.getVarbitOperation().trim().toUpperCase(Locale.ROOT);
		String disp = n.getVarbitDisplayText();
		if (disp != null && disp.isBlank())
		{
			disp = null;
		}
		return n.getVarbitId() + "|" + reqVal + "|" + op + "|" + (disp == null ? "" : disp);
	}

	private static boolean varbitSpecsMatchForOrderRouting(VarbitSpec a, VarbitSpec b)
	{
		return a.getVarbitId() == b.getVarbitId()
			&& a.getRequiredValue() == b.getRequiredValue()
			&& a.getOperation() == b.getOperation()
			&& Objects.equals(
				a.getDisplayText() == null || a.getDisplayText().isBlank() ? null : a.getDisplayText(),
				b.getDisplayText() == null || b.getDisplayText().isBlank() ? null : b.getDisplayText());
	}

	private static void convertVarbitLeafToOrderSlot(DraftOrderStepRequirement n)
	{
		n.setKind("ORDER_VARBIT");
		n.setVarbitId(null);
		n.setVarbitRequiredValue(null);
		n.setVarbitOperation(null);
		n.setVarbitDisplayText(null);
		n.setItemRawId(null);
		if (n.getChildren() != null)
		{
			n.getChildren().clear();
		}
	}

	/**
	 * Collects inline {@code ZONE} leaves, syncs {@link DraftOrderLine} zone routing fields, replaces each leaf with {@code ORDER_ZONE}.
	 *
	 * @return {@code null} when nothing to do or migration succeeded; otherwise a short error (caller should not persist).
	 */
	@Nullable
	public static String migrateInlineZoneLeavesToOrderRouting(DraftOrderLine line, DraftOrderStepRequirement root)
	{
		if (line == null || line.isSectionDivider() || root == null)
		{
			return null;
		}
		List<DraftOrderStepRequirement> leaves = new ArrayList<>();
		collectZoneLikeLeaves(root, leaves);
		if (leaves.isEmpty())
		{
			return null;
		}
		for (DraftOrderStepRequirement n : leaves)
		{
			if (n.getZoneCorner1() == null || n.getZoneCorner2() == null)
			{
				return "ZONE needs both zone corners (world points).";
			}
		}
		Set<String> signatures = new LinkedHashSet<>();
		for (DraftOrderStepRequirement n : leaves)
		{
			signatures.add(persistedZoneSignature(n));
		}
		if (signatures.size() > 1)
		{
			return "This order row mixes different zone rectangles in the conditions tree. Use one zone per row, or split across multiple quest-order rows.";
		}
		DraftOrderStepRequirement sample = leaves.get(0);
		if (orderLineHasZoneRoutingCorners(line))
		{
			if (!zoneSampleMatchesLineRouting(sample, line))
			{
				return "The Zone reqs tab corners for this row do not match ZONE leaves in conditions. Edit the zone on the Zone tab, or remove conflicting nodes.";
			}
		}
		else
		{
			line.setZoneRoutingCorner1(sample.getZoneCorner1());
			line.setZoneRoutingCorner2(sample.getZoneCorner2());
			line.setZoneRoutingDisplayText(sample.getZoneDisplayText());
		}
		for (DraftOrderStepRequirement n : leaves)
		{
			convertZoneLeafToOrderSlot(n);
		}
		return null;
	}

	private static void collectZoneLikeLeaves(DraftOrderStepRequirement node, List<DraftOrderStepRequirement> out)
	{
		if (node == null)
		{
			return;
		}
		String k = node.getKind() == null ? "" : node.getKind().trim().toUpperCase(Locale.ROOT);
		if ("ZONE".equals(k))
		{
			out.add(node);
			return;
		}
		if (node.getChildren() != null)
		{
			for (DraftOrderStepRequirement ch : node.getChildren())
			{
				collectZoneLikeLeaves(ch, out);
			}
		}
	}

	private static String persistedZoneSignature(DraftOrderStepRequirement n)
	{
		WorldPoint a = n.getZoneCorner1();
		WorldPoint b = n.getZoneCorner2();
		String disp = n.getZoneDisplayText();
		if (disp != null && disp.isBlank())
		{
			disp = null;
		}
		return a.getX() + "," + a.getY() + "," + a.getPlane() + "|" + b.getX() + "," + b.getY() + "," + b.getPlane() + "|" + (disp == null ? "" : disp);
	}

	private static boolean zoneSampleMatchesLineRouting(DraftOrderStepRequirement sample, DraftOrderLine line)
	{
		return worldPointsEqual(sample.getZoneCorner1(), line.getZoneRoutingCorner1())
			&& worldPointsEqual(sample.getZoneCorner2(), line.getZoneRoutingCorner2())
			&& Objects.equals(
				sample.getZoneDisplayText() == null || sample.getZoneDisplayText().isBlank() ? null : sample.getZoneDisplayText().trim(),
				line.getZoneRoutingDisplayText() == null || line.getZoneRoutingDisplayText().isBlank() ? null : line.getZoneRoutingDisplayText().trim());
	}

	private static void convertZoneLeafToOrderSlot(DraftOrderStepRequirement n)
	{
		n.setKind("ORDER_ZONE");
		n.setZoneCorner1(null);
		n.setZoneCorner2(null);
		n.setZoneDisplayText(null);
		n.setItemRawId(null);
		n.setVarbitId(null);
		n.setVarbitRequiredValue(null);
		n.setVarbitOperation(null);
		n.setVarbitDisplayText(null);
		if (n.getChildren() != null)
		{
			n.getChildren().clear();
		}
	}

	/**
	 * Removes {@code ORDER_VARBIT} / {@code ROUTING_VARBIT} leaves and unwraps single-child groups. Returns
	 * {@code null} when nothing remains (caller should clear the order row's tree).
	 */
	@Nullable
	public static DraftOrderStepRequirement stripOrderVarbitLeaves(@Nullable DraftOrderStepRequirement node)
	{
		if (node == null)
		{
			return null;
		}
		String k = node.getKind() == null ? "" : node.getKind().trim().toUpperCase(Locale.ROOT);
		if (List.of("ORDER_VARBIT", "ROUTING_VARBIT").contains(k))
		{
			return null;
		}
		else if ("INVERT".equals(k))
		{
			if (node.getChildren() == null || node.getChildren().isEmpty())
			{
				return null;
			}
			DraftOrderStepRequirement inner = stripOrderVarbitLeaves(node.getChildren().get(0));
			if (inner == null)
			{
				return null;
			}
			node.getChildren().clear();
			node.getChildren().add(inner);
			return node;
		}
		else if ("GROUP".equals(k))
		{
			List<DraftOrderStepRequirement> kept = new ArrayList<>();
			List<DraftOrderStepRequirement> ch = node.getChildren() == null ? List.of() : new ArrayList<>(node.getChildren());
			for (DraftOrderStepRequirement c : ch)
			{
				DraftOrderStepRequirement p = stripOrderVarbitLeaves(c);
				if (p != null)
				{
					kept.add(p);
				}
			}
			if (kept.isEmpty())
			{
				return null;
			}
			if (kept.size() == 1)
			{
				return kept.get(0);
			}
			node.getChildren().clear();
			node.getChildren().addAll(kept);
			return node;
		}
		return node;
	}

	/**
	 * Removes {@code ORDER_ZONE} leaves and unwraps single-child groups. Returns {@code null} when nothing remains.
	 */
	@Nullable
	public static DraftOrderStepRequirement stripOrderZoneLeaves(@Nullable DraftOrderStepRequirement node)
	{
		if (node == null)
		{
			return null;
		}
		String k = node.getKind() == null ? "" : node.getKind().trim().toUpperCase(Locale.ROOT);
		if ("ORDER_ZONE".equals(k))
		{
			return null;
		}
		else if ("INVERT".equals(k))
		{
			if (node.getChildren() == null || node.getChildren().isEmpty())
			{
				return null;
			}
			DraftOrderStepRequirement inner = stripOrderZoneLeaves(node.getChildren().get(0));
			if (inner == null)
			{
				return null;
			}
			node.getChildren().clear();
			node.getChildren().add(inner);
			return node;
		}
		else if ("GROUP".equals(k))
		{
			List<DraftOrderStepRequirement> kept = new ArrayList<>();
			List<DraftOrderStepRequirement> ch = node.getChildren() == null ? List.of() : new ArrayList<>(node.getChildren());
			for (DraftOrderStepRequirement c : ch)
			{
				DraftOrderStepRequirement p = stripOrderZoneLeaves(c);
				if (p != null)
				{
					kept.add(p);
				}
			}
			if (kept.isEmpty())
			{
				return null;
			}
			if (kept.size() == 1)
			{
				return kept.get(0);
			}
			node.getChildren().clear();
			node.getChildren().addAll(kept);
			return node;
		}
		return node;
	}

	@Nullable
	public static Integer findFirstItemRawIdInTree(@Nullable DraftOrderStepRequirement root)
	{
		if (root == null)
		{
			return null;
		}
		String k = root.getKind() == null ? "" : root.getKind().trim().toUpperCase(Locale.ROOT);
		if (("ITEM".equals(k) || "CAPTURED_ITEM".equals(k)) && root.getItemRawId() != null)
		{
			return root.getItemRawId();
		}
		if (root.getChildren() != null)
		{
			for (DraftOrderStepRequirement ch : root.getChildren())
			{
				Integer v = findFirstItemRawIdInTree(ch);
				if (v != null)
				{
					return v;
				}
			}
		}
		return null;
	}

	@Nullable
	public static DraftOrderStepRequirement synthesizeLegacyTreeForOrderRow(DraftOrderLine line, @Nullable DraftStep def)
	{
		if (line == null || line.isSectionDivider())
		{
			return null;
		}
		Integer linked = line.getLinkedRequirementRawId();
		if (linked != null && linked > 0)
		{
			return DraftOrderStepRequirement.invert(DraftOrderStepRequirement.item(linked));
		}
		return null;
	}

	public static boolean orderStepTreesEqual(@Nullable DraftOrderStepRequirement a, @Nullable DraftOrderStepRequirement b)
	{
		if (a == null && b == null)
		{
			return true;
		}
		if (a == null || b == null)
		{
			return false;
		}
		String ka = a.getKind() == null ? "" : a.getKind().trim().toUpperCase(Locale.ROOT);
		String kb = b.getKind() == null ? "" : b.getKind().trim().toUpperCase(Locale.ROOT);
		if (!ka.equals(kb))
		{
			return false;
		}
		if (!Objects.equals(a.getLogic(), b.getLogic())
			|| !Objects.equals(a.getItemRawId(), b.getItemRawId())
			|| !Objects.equals(a.getItemQuantity(), b.getItemQuantity())
			|| a.isItemAlsoCheckBank() != b.isItemAlsoCheckBank()
			|| !Objects.equals(a.getVarbitId(), b.getVarbitId())
			|| !Objects.equals(a.getVarbitRequiredValue(), b.getVarbitRequiredValue())
			|| !Objects.equals(a.getVarbitOperation(), b.getVarbitOperation())
			|| !Objects.equals(a.getVarbitDisplayText(), b.getVarbitDisplayText())
			|| !Objects.equals(a.getSkillName(), b.getSkillName())
			|| !Objects.equals(a.getSkillRequiredLevel(), b.getSkillRequiredLevel())
			|| !Objects.equals(a.getSkillOperation(), b.getSkillOperation())
			|| !Objects.equals(a.getSkillDisplayText(), b.getSkillDisplayText())
			|| a.isSkillCanBeBoosted() != b.isSkillCanBeBoosted()
			|| !worldPointsEqual(a.getZoneCorner1(), b.getZoneCorner1())
			|| !worldPointsEqual(a.getZoneCorner2(), b.getZoneCorner2())
			|| !Objects.equals(a.getZoneDisplayText(), b.getZoneDisplayText()))
		{
			return false;
		}
		List<DraftOrderStepRequirement> ca = a.getChildren() == null ? List.of() : a.getChildren();
		List<DraftOrderStepRequirement> cb = b.getChildren() == null ? List.of() : b.getChildren();
		if (ca.size() != cb.size())
		{
			return false;
		}
		for (int i = 0; i < ca.size(); i++)
		{
			if (!orderStepTreesEqual(ca.get(i), cb.get(i)))
			{
				return false;
			}
		}
		return true;
	}

	private static boolean worldPointsEqual(@Nullable WorldPoint a, @Nullable WorldPoint b)
	{
		if (a == null && b == null)
		{
			return true;
		}
		if (a == null || b == null)
		{
			return false;
		}
		return a.getX() == b.getX() && a.getY() == b.getY() && a.getPlane() == b.getPlane();
	}

	private static DraftStep findDefinition(DraftHelper draft, String refStepId)
	{
		if (draft.getStepDefinitions() == null || refStepId == null || refStepId.isBlank())
		{
			return null;
		}
		for (DraftStep s : draft.getStepDefinitions())
		{
			if (refStepId.equals(s.getStepId()))
			{
				return s;
			}
		}
		return null;
	}

	/**
	 * Builds the persisted game selector {@code S} (without preview {@link com.questhelper.requirements.ManualRequirement}).
	 */
	@Nullable
	public static Requirement buildRuntimeSelector(
		@Nullable DraftOrderStepRequirement node,
		DraftOrderLine line,
		@Nullable DraftStep def,
		Map<Integer, ItemRequirement> requirementById)
	{
		if (node == null)
		{
			return null;
		}
		String k = node.getKind() == null ? "" : node.getKind().trim().toUpperCase(Locale.ROOT);
		switch (k)
		{
			case "GROUP":
				return buildGroup(node, line, def, requirementById);
			case "INVERT":
			{
				Requirement inner = firstChildSelector(node, line, def, requirementById);
				return inner == null ? null : not(inner);
			}
			case "ITEM":
			case "CAPTURED_ITEM":
			{
				Integer rid = node.getItemRawId();
				if (rid == null)
				{
					return null;
				}
				ItemRequirement ir = requirementById.get(rid);
				ItemRequirement out = ir != null ? ir : new ItemRequirement("Item " + rid, rid);
				int qty = node.getItemQuantity() == null ? 1 : Math.max(1, node.getItemQuantity());
				if (qty > 1)
				{
					out = out.quantity(qty);
				}
				return node.isItemAlsoCheckBank() ? out.alsoCheckBank() : out;
			}
			case "ORDER_VARBIT":
			case "ROUTING_VARBIT":
				return routingVarbitDone(line, def);
			case "ORDER_ZONE":
				return routingZoneDone(line);
			case "VARBIT":
			case "INLINE_VARBIT":
			{
				Integer vid = node.getVarbitId();
				if (vid == null)
				{
					return null;
				}
				int val = node.getVarbitRequiredValue() == null ? 1 : node.getVarbitRequiredValue();
				Operation op = VarbitSpec.parseOperation(node.getVarbitOperation(), Operation.EQUAL);
				String disp = node.getVarbitDisplayText();
				if (disp != null && disp.isBlank())
				{
					disp = null;
				}
				return new VarbitRequirement(vid, op, val, disp);
			}
			case "ZONE":
			{
				WorldPoint c1 = node.getZoneCorner1();
				WorldPoint c2 = node.getZoneCorner2();
				if (c1 == null || c2 == null)
				{
					return null;
				}
				String disp = node.getZoneDisplayText();
				if (disp == null || disp.isBlank())
				{
					disp = "In zone";
				}
				return new ZoneRequirement(disp, new Zone(c1, c2));
			}
			case "SKILL":
			{
				Skill skill = parseSkillOrDefault(node.getSkillName());
				int level = node.getSkillRequiredLevel() == null ? 1 : Math.max(1, node.getSkillRequiredLevel());
				Operation op = VarbitSpec.parseOperation(node.getSkillOperation(), Operation.GREATER_EQUAL);
				String disp = node.getSkillDisplayText();
				if (op == Operation.GREATER_EQUAL)
				{
					return (disp != null && !disp.isBlank())
						? new SkillRequirement(skill, level, node.isSkillCanBeBoosted(), disp)
						: new SkillRequirement(skill, level, node.isSkillCanBeBoosted());
				}
				return new SkillRequirement(skill, level, op);
			}
			default:
				return null;
		}
	}

	private static Requirement buildGroup(
		DraftOrderStepRequirement node,
		DraftOrderLine line,
		DraftStep def,
		Map<Integer, ItemRequirement> requirementById)
	{
		LogicType lt = parseLogicType(node.getLogic());
		List<Requirement> parts = new ArrayList<>();
		if (node.getChildren() != null)
		{
			for (DraftOrderStepRequirement ch : node.getChildren())
			{
				Requirement r = buildRuntimeSelector(ch, line, def, requirementById);
				if (r != null)
				{
					parts.add(r);
				}
			}
		}
		if (parts.isEmpty())
		{
			return null;
		}
		if (parts.size() == 1)
		{
			if (lt == LogicType.AND || lt == LogicType.OR)
			{
				return parts.get(0);
			}
			return new Conditions(lt, parts.toArray(new Requirement[0]));
		}
		return new Conditions(lt, parts.toArray(new Requirement[0]));
	}

	private static LogicType parseLogicType(String name)
	{
		if (name == null || name.isBlank())
		{
			return LogicType.AND;
		}
		try
		{
			return LogicType.valueOf(name.trim().toUpperCase(Locale.ROOT));
		}
		catch (IllegalArgumentException ex)
		{
			return LogicType.AND;
		}
	}

	@Nullable
	private static Requirement firstChildSelector(
		DraftOrderStepRequirement node,
		DraftOrderLine line,
		DraftStep def,
		Map<Integer, ItemRequirement> requirementById)
	{
		if (node.getChildren() == null || node.getChildren().isEmpty())
		{
			return null;
		}
		return buildRuntimeSelector(node.getChildren().get(0), line, def, requirementById);
	}

	@Nullable
	private static Requirement routingVarbitDone(DraftOrderLine line, DraftStep def)
	{
		if (line.getOrderSlotId() == null || line.getOrderSlotId().isBlank())
		{
			return null;
		}
		DraftStepAttachedRequirement cfg = DraftStepAttachedRequirement.findOrderRoutingVarbit(line);
		if (cfg == null && def != null)
		{
			cfg = DraftStepAttachedRequirement.findRoutingVarbitForSlot(def, line.getOrderSlotId());
		}
		if (cfg == null)
		{
			return null;
		}
		return VarbitSpec.fromStepAttachment(cfg).toVarbitRequirement();
	}

	@Nullable
	private static Requirement routingZoneDone(DraftOrderLine line)
	{
		WorldPoint c1 = line.getZoneRoutingCorner1();
		WorldPoint c2 = line.getZoneRoutingCorner2();
		if (c1 == null || c2 == null)
		{
			return null;
		}
		String disp = line.getZoneRoutingDisplayText();
		if (disp == null || disp.isBlank())
		{
			disp = "In zone";
		}
		return new ZoneRequirement(disp, new Zone(c1, c2));
	}

	/**
	 * Java source for the game selector (argument to {@code addStep} when using a tree — no outer {@code not()}).
	 */
	public static String emitSelectorJava(
		DraftOrderStepRequirement node,
		DraftOrderLine line,
		DraftStep def,
		Map<Integer, String> requirementVarNamesByRawId,
		Map<String, String> varbitFieldByOrderSlotId,
		Map<String, String> zoneFieldByOrderSlotId,
		List<String> warnings)
	{
		if (node == null)
		{
			warnings.add("Null order step requirement tree");
			return "new VarbitRequirement(0, Operation.EQUAL, 1, null)";
		}
		String k = node.getKind() == null ? "" : node.getKind().trim().toUpperCase(Locale.ROOT);
		switch (k)
		{
			case "GROUP":
				return emitGroupJava(node, line, def, requirementVarNamesByRawId, varbitFieldByOrderSlotId, zoneFieldByOrderSlotId, warnings);
			case "INVERT":
			{
				String inner = node.getChildren() == null || node.getChildren().isEmpty()
					? null
					: emitSelectorJava(node.getChildren().get(0), line, def, requirementVarNamesByRawId, varbitFieldByOrderSlotId, zoneFieldByOrderSlotId, warnings);
				if (inner == null || inner.isBlank())
				{
					warnings.add("INVERT without child in order step requirement");
					return "new VarbitRequirement(0, Operation.EQUAL, 1, null)";
				}
				return "not(" + inner + ")";
			}
			case "ITEM":
			case "CAPTURED_ITEM":
			{
				Integer rid = node.getItemRawId();
				if (rid == null)
				{
					warnings.add("ITEM without raw id");
					return "new VarbitRequirement(0, Operation.EQUAL, 1, null)";
				}
				String v = requirementVarNamesByRawId.get(rid);
				if (v == null)
				{
					warnings.add("Missing item requirement var for tree leaf raw id " + rid);
					return "new VarbitRequirement(0, Operation.EQUAL, 1, null)";
				}
				int qty = node.getItemQuantity() == null ? 1 : Math.max(1, node.getItemQuantity());
				String expr = qty > 1 ? v + ".quantity(" + qty + ")" : v;
				return node.isItemAlsoCheckBank() ? expr + ".alsoCheckBank()" : expr;
			}
			case "ORDER_VARBIT":
			case "ROUTING_VARBIT":
				return HelperScaffoldGenerator.varbitFieldNameForOrderSlot(line, varbitFieldByOrderSlotId, warnings);
			case "ORDER_ZONE":
				return HelperScaffoldGenerator.zoneFieldNameForOrderSlot(line, zoneFieldByOrderSlotId, warnings);
			case "VARBIT":
			case "INLINE_VARBIT":
			{
				Integer vid = node.getVarbitId();
				if (vid == null)
				{
					warnings.add("VARBIT without varbit id");
					return "new VarbitRequirement(0, Operation.EQUAL, 1, null)";
				}
				int val = node.getVarbitRequiredValue() == null ? 1 : node.getVarbitRequiredValue();
				String opName = node.getVarbitOperation() == null || node.getVarbitOperation().isBlank()
					? "EQUAL"
					: node.getVarbitOperation().trim().toUpperCase(Locale.ROOT);
				String disp = node.getVarbitDisplayText();
				String dispArg = disp == null || disp.isBlank() ? "null" : "\"" + HelperScaffoldGenerator.escapeJavaLiteral(disp) + "\"";
				return "new VarbitRequirement(" + vid + ", Operation." + opName + ", " + val + ", " + dispArg + ")";
			}
			case "ZONE":
			{
				WorldPoint c1 = node.getZoneCorner1();
				WorldPoint c2 = node.getZoneCorner2();
				if (c1 == null || c2 == null)
				{
					warnings.add("ZONE without both corners");
					return "new VarbitRequirement(0, Operation.EQUAL, 1, null)";
				}
				String disp = node.getZoneDisplayText();
				String dispLit = disp == null || disp.isBlank()
					? "In zone"
					: HelperScaffoldGenerator.escapeJavaLiteral(disp);
				String dispArg = "\"" + dispLit + "\"";
				return "new ZoneRequirement(" + dispArg + ", new Zone("
					+ worldPointJavaLiteral(c1) + ", " + worldPointJavaLiteral(c2) + "))";
			}
			case "SKILL":
			{
				String skillName = node.getSkillName() == null || node.getSkillName().isBlank()
					? "ATTACK"
					: node.getSkillName().trim().toUpperCase(Locale.ROOT);
				try
				{
					Skill.valueOf(skillName);
				}
				catch (IllegalArgumentException ex)
				{
					warnings.add("Unknown SKILL name: " + node.getSkillName());
					skillName = "ATTACK";
				}
				int level = node.getSkillRequiredLevel() == null ? 1 : Math.max(1, node.getSkillRequiredLevel());
				String opName = node.getSkillOperation() == null || node.getSkillOperation().isBlank()
					? "GREATER_EQUAL"
					: node.getSkillOperation().trim().toUpperCase(Locale.ROOT);
				Operation op;
				try
				{
					op = Operation.valueOf(opName);
				}
				catch (IllegalArgumentException ex)
				{
					warnings.add("Unknown SKILL operation: " + node.getSkillOperation());
					op = Operation.GREATER_EQUAL;
				}
				String disp = node.getSkillDisplayText();
				if (op == Operation.GREATER_EQUAL)
				{
					if (disp != null && !disp.isBlank())
					{
						return "new SkillRequirement(Skill." + skillName + ", " + level + ", "
							+ node.isSkillCanBeBoosted() + ", \"" + HelperScaffoldGenerator.escapeJavaLiteral(disp) + "\")";
					}
					return "new SkillRequirement(Skill." + skillName + ", " + level + ", " + node.isSkillCanBeBoosted() + ")";
				}
				return "new SkillRequirement(Skill." + skillName + ", " + level + ", Operation." + op.name() + ")";
			}
			default:
				warnings.add("Unknown order step requirement kind: " + node.getKind());
				return "new VarbitRequirement(0, Operation.EQUAL, 1, null)";
		}
	}

	private static String worldPointJavaLiteral(WorldPoint p)
	{
		return "new WorldPoint(" + p.getX() + ", " + p.getY() + ", " + p.getPlane() + ")";
	}

	private static String emitGroupJava(
		DraftOrderStepRequirement node,
		DraftOrderLine line,
		DraftStep def,
		Map<Integer, String> requirementVarNamesByRawId,
		Map<String, String> varbitFieldByOrderSlotId,
		Map<String, String> zoneFieldByOrderSlotId,
		List<String> warnings)
	{
		LogicType lt = parseLogicType(node.getLogic());
		List<String> parts = new ArrayList<>();
		if (node.getChildren() != null)
		{
			for (DraftOrderStepRequirement ch : node.getChildren())
			{
				parts.add(emitSelectorJava(ch, line, def, requirementVarNamesByRawId, varbitFieldByOrderSlotId, zoneFieldByOrderSlotId, warnings));
			}
		}
		if (parts.isEmpty())
		{
			warnings.add("GROUP without children in order step requirement");
			return "new VarbitRequirement(0, Operation.EQUAL, 1, null)";
		}
		if (parts.size() == 1)
		{
			if (lt == LogicType.AND || lt == LogicType.OR)
			{
				return parts.get(0);
			}
			return "new Conditions(LogicType." + lt.name() + ", " + parts.get(0) + ")";
		}
		return "new Conditions(LogicType." + lt.name() + ", " + String.join(", ", parts) + ")";
	}

	/**
	 * Validates tree shape for UI / import (kinds, GROUP logic names, child counts).
	 */
	public static String validateTreeOrError(DraftOrderStepRequirement node)
	{
		if (node == null)
		{
			return "Tree is null.";
		}
		return validateNode(node, 0);
	}

	private static String validateNode(DraftOrderStepRequirement node, int depth)
	{
		if (depth > 32)
		{
			return "Requirement tree is too deep.";
		}
		String k = node.getKind() == null ? "" : node.getKind().trim().toUpperCase(Locale.ROOT);
		if (k.isEmpty())
		{
			return "Missing kind on a node.";
		}
		switch (k)
		{
			case "GROUP":
				if (node.getLogic() == null || node.getLogic().isBlank())
				{
					return "GROUP node needs logic (AND, OR, NOR, NAND).";
				}
				try
				{
					LogicType.valueOf(node.getLogic().trim().toUpperCase(Locale.ROOT));
				}
				catch (IllegalArgumentException ex)
				{
					return "Invalid logic type: " + node.getLogic();
				}
				if (node.getChildren() == null || node.getChildren().isEmpty())
				{
					return "GROUP needs at least one child.";
				}
				for (DraftOrderStepRequirement ch : node.getChildren())
				{
					String err = validateNode(ch, depth + 1);
					if (err != null)
					{
						return err;
					}
				}
				return null;
			case "INVERT":
				if (node.getChildren() == null || node.getChildren().size() != 1)
				{
					return "INVERT needs exactly one child.";
				}
				return validateNode(node.getChildren().get(0), depth + 1);
			case "ITEM":
			case "CAPTURED_ITEM":
				if (node.getItemRawId() == null)
				{
					return "ITEM needs itemRawId.";
				}
				if (node.getItemQuantity() != null && node.getItemQuantity() < 1)
				{
					return "ITEM quantity must be >= 1.";
				}
				return null;
			case "ORDER_VARBIT":
			case "ROUTING_VARBIT":
				return null;
			case "ORDER_ZONE":
				return null;
			case "VARBIT":
			case "INLINE_VARBIT":
				return "Order conditions cannot use inline VARBIT nodes. Set the varbit on the Varbit reqs tab for this row, then use only \"Order varbit (slot)\" here (or save again to auto-migrate legacy drafts).";
			case "ZONE":
				if (node.getZoneCorner1() == null || node.getZoneCorner2() == null)
				{
					return "ZONE needs both corners.";
				}
				return null;
			case "SKILL":
				if (node.getSkillName() == null || node.getSkillName().isBlank())
				{
					return "SKILL needs skillName.";
				}
				if (node.getSkillRequiredLevel() == null || node.getSkillRequiredLevel() < 1)
				{
					return "SKILL needs required level >= 1.";
				}
				return null;
			default:
				return "Unknown kind: " + node.getKind();
		}
	}

	private static Skill parseSkillOrDefault(String name)
	{
		if (name == null || name.isBlank())
		{
			return Skill.ATTACK;
		}
		try
		{
			return Skill.valueOf(name.trim().toUpperCase(Locale.ROOT));
		}
		catch (IllegalArgumentException ex)
		{
			return Skill.ATTACK;
		}
	}

	/**
	 * When the maker references an item attachment on a step for highlight, mirror that id for persistence
	 * (write-only legacy) when the tree is a single {@code INVERT(ITEM)}.
	 */
	public static void mirrorLinkedRawIdFromSimpleTree(DraftOrderLine line)
	{
		DraftOrderStepRequirement root = line.getStepRequirement();
		if (root == null || !"INVERT".equalsIgnoreCase(root.getKind()) || root.getChildren() == null || root.getChildren().size() != 1)
		{
			return;
		}
		DraftOrderStepRequirement inner = root.getChildren().get(0);
		if (inner == null || inner.getItemRawId() == null)
		{
			return;
		}
		String ik = inner.getKind() == null ? "" : inner.getKind().trim().toUpperCase(Locale.ROOT);
		if (!"ITEM".equals(ik) && !"CAPTURED_ITEM".equals(ik))
		{
			return;
		}
		line.setLinkedRequirementRawId(inner.getItemRawId());
	}
}
