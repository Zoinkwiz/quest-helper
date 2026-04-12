package com.questhelper.managers;

import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.questhelper.managers.HelperConstructModels.DraftHelper;
import static com.questhelper.managers.HelperConstructModels.DraftOrderLine;
import static com.questhelper.managers.HelperConstructModels.DraftOrderStepRequirement;
import static com.questhelper.managers.HelperConstructModels.DraftStep;
import static com.questhelper.managers.HelperConstructModels.DraftStepAttachedRequirement;
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
				migrateInlineVarbitLeavesToOrderRouting(line, line.getStepRequirement());
			}
		}
	}

	/**
	 * Upgrades legacy kinds, moves any inline {@code VARBIT} leaves onto this row's routing attachment, replaces those
	 * leaves with {@code ORDER_VARBIT}, then validates. Call before persisting a conditions tree from the editor or JSON.
	 *
	 * @return {@code null} if the tree is OK to save, otherwise a short error for the UI.
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
			String mig = migrateInlineVarbitLeavesToOrderRouting(line, tree);
			if (mig != null)
			{
				return mig;
			}
		}
		return validateTreeOrError(tree);
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
		if ("ORDER_VARBIT".equals(k) || "ROUTING_VARBIT".equals(k))
		{
			return null;
		}
		if ("INVERT".equals(k))
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
		if ("GROUP".equals(k))
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
			|| !Objects.equals(a.getVarbitId(), b.getVarbitId())
			|| !Objects.equals(a.getVarbitRequiredValue(), b.getVarbitRequiredValue())
			|| !Objects.equals(a.getVarbitOperation(), b.getVarbitOperation())
			|| !Objects.equals(a.getVarbitDisplayText(), b.getVarbitDisplayText()))
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
				return ir != null ? ir : new ItemRequirement("Item " + rid, rid);
			}
			case "ORDER_VARBIT":
			case "ROUTING_VARBIT":
				return routingVarbitDone(line, def);
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
			return parts.get(0);
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

	/**
	 * Java source for the game selector (argument to {@code addStep} when using a tree — no outer {@code not()}).
	 */
	public static String emitSelectorJava(
		DraftOrderStepRequirement node,
		DraftOrderLine line,
		DraftStep def,
		Map<Integer, String> requirementVarNamesByRawId,
		Map<String, String> varbitFieldByOrderSlotId,
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
				return emitGroupJava(node, line, def, requirementVarNamesByRawId, varbitFieldByOrderSlotId, warnings);
			case "INVERT":
			{
				String inner = node.getChildren() == null || node.getChildren().isEmpty()
					? null
					: emitSelectorJava(node.getChildren().get(0), line, def, requirementVarNamesByRawId, varbitFieldByOrderSlotId, warnings);
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
				return v;
			}
			case "ORDER_VARBIT":
			case "ROUTING_VARBIT":
				return HelperScaffoldGenerator.varbitFieldNameForOrderSlot(line, varbitFieldByOrderSlotId, warnings);
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
			default:
				warnings.add("Unknown order step requirement kind: " + node.getKind());
				return "new VarbitRequirement(0, Operation.EQUAL, 1, null)";
		}
	}

	private static String emitGroupJava(
		DraftOrderStepRequirement node,
		DraftOrderLine line,
		DraftStep def,
		Map<Integer, String> requirementVarNamesByRawId,
		Map<String, String> varbitFieldByOrderSlotId,
		List<String> warnings)
	{
		LogicType lt = parseLogicType(node.getLogic());
		List<String> parts = new ArrayList<>();
		if (node.getChildren() != null)
		{
			for (DraftOrderStepRequirement ch : node.getChildren())
			{
				parts.add(emitSelectorJava(ch, line, def, requirementVarNamesByRawId, varbitFieldByOrderSlotId, warnings));
			}
		}
		if (parts.isEmpty())
		{
			warnings.add("GROUP without children in order step requirement");
			return "new VarbitRequirement(0, Operation.EQUAL, 1, null)";
		}
		if (parts.size() == 1)
		{
			return parts.get(0);
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
				return null;
			case "ORDER_VARBIT":
			case "ROUTING_VARBIT":
				return null;
			case "VARBIT":
			case "INLINE_VARBIT":
				return "Order conditions cannot use inline VARBIT nodes. Set the varbit on the Varbit reqs tab for this row, then use only \"Order varbit (slot)\" here (or save again to auto-migrate legacy drafts).";
			default:
				return "Unknown kind: " + node.getKind();
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
