package com.questhelper.maker;

import com.questhelper.maker.HelperConstructManager;
import com.questhelper.maker.HelperConstructModels.DraftOrderStepRequirement;
import com.questhelper.maker.OrderStepRequirementSupport;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Visual editor for {@link DraftOrderStepRequirement} on a quest-order row: logic groups (AND/OR/NOR/NAND),
 * order varbit leaves ({@code Add varbit} reuses another slot’s routing; {@code Create new varbit} defines routing for
 * this row), captured items, and NOT (invert). Varbit values are edited on the Varbit reqs tab.
 */
public final class OrderStepRequirementTreeEditorDialog extends JDialog
{
	private static final class RootMarker
	{
		static final RootMarker INSTANCE = new RootMarker();

		private RootMarker()
		{
		}
	}

	private final HelperConstructManager manager;
	private final int orderIndex;
	private DraftOrderStepRequirement rootDto;

	/** Routing to apply to this order row when OK is pressed (if the tree still has an order varbit leaf). */
	private boolean pendingVarbitRouting;
	private int pendingVarbitId;
	private int pendingVarbitRequiredValue;
	private String pendingVarbitOperation;
	private String pendingVarbitDisplayText;

	private final DefaultMutableTreeNode visualRoot = new DefaultMutableTreeNode(RootMarker.INSTANCE);
	private final DefaultTreeModel treeModel;
	private final JTree tree;
	private boolean accepted;

	private OrderStepRequirementTreeEditorDialog(
		Window owner,
		HelperConstructManager manager,
		int orderIndex,
		String rowLabel,
		DraftOrderStepRequirement initialClone)
	{
		super(owner, "Order requirement — " + rowLabel, ModalityType.APPLICATION_MODAL);
		this.manager = manager;
		this.orderIndex = orderIndex;
		this.rootDto = initialClone;
		clearPendingVarbitRouting();

		treeModel = new DefaultTreeModel(visualRoot);
		tree = new JTree(treeModel);
		tree.setRootVisible(true);
		tree.setShowsRootHandles(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new ReqTreeCellRenderer());
		tree.addMouseListener(new java.awt.event.MouseAdapter()
		{
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e)
			{
				if (e.getClickCount() == 2)
				{
					editSelectedNode();
				}
			}
		});

		rebuildTree();

		JButton addReq = new JButton("Add requirement ▾");
		JPopupMenu reqMenu = new JPopupMenu();
		reqMenu.add(newAddVarbitFromExistingItem());
		reqMenu.add(newCreateVarbitItem());
		reqMenu.add(newCapturedItemItem());
		reqMenu.add(newInvertItem());
		addReq.addActionListener(e -> reqMenu.show(addReq, 0, addReq.getHeight()));

		JButton addLogic = new JButton("Add logic ▾");
		JPopupMenu logicMenu = new JPopupMenu();
		for (LogicType lt : new LogicType[] { LogicType.AND, LogicType.OR, LogicType.NOR, LogicType.NAND })
		{
			logicMenu.add(new JMenuItem(lt.name())).addActionListener(ev -> addLogicGroup(lt));
		}
		addLogic.addActionListener(e -> logicMenu.show(addLogic, 0, addLogic.getHeight()));

		JButton removeBtn = new JButton("Remove");
		removeBtn.addActionListener(e -> removeSelected());

		JButton editBtn = new JButton("Edit…");
		editBtn.addActionListener(e -> editSelectedNode());

		JButton clearBtn = new JButton("Clear all");
		clearBtn.addActionListener(e ->
		{
			rootDto = null;
			rebuildTree();
		});

		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
		toolbar.setBackground(ColorScheme.DARK_GRAY_COLOR);
		toolbar.add(addReq);
		toolbar.add(addLogic);
		toolbar.add(editBtn);
		toolbar.add(removeBtn);
		toolbar.add(clearBtn);

		JScrollPane scroll = new JScrollPane(tree);
		scroll.setPreferredSize(new Dimension(520, 360));

		JButton ok = new JButton("OK");
		ok.addActionListener(e -> onOk());
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(e -> dispose());
		JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
		south.setBackground(ColorScheme.DARK_GRAY_COLOR);
		south.add(cancel);
		south.add(ok);

		JPanel content = new JPanel(new BorderLayout(8, 8));
		content.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
		content.setBackground(ColorScheme.DARK_GRAY_COLOR);
		content.add(toolbar, BorderLayout.NORTH);
		content.add(scroll, BorderLayout.CENTER);
		content.add(south, BorderLayout.SOUTH);

		setContentPane(content);
		pack();
		setLocationRelativeTo(owner);
		getRootPane().setDefaultButton(ok);
	}

	private JMenuItem newAddVarbitFromExistingItem()
	{
		JMenuItem it = new JMenuItem("Add varbit");
		it.addActionListener(e -> addVarbitFromExistingInteractive(false));
		return it;
	}

	private JMenuItem newCreateVarbitItem()
	{
		JMenuItem it = new JMenuItem("Create new varbit");
		it.addActionListener(e -> addVarbitCreateNewInteractive(false));
		return it;
	}

	private JMenuItem newCapturedItemItem()
	{
		JMenuItem it = new JMenuItem("Captured item…");
		it.addActionListener(e -> addCapturedItemInteractive());
		return it;
	}

	private JMenuItem newInvertItem()
	{
		JMenuItem it = new JMenuItem("NOT (invert)…");
		it.addActionListener(e -> addInvertInteractive());
		return it;
	}

	public static boolean showEditor(Window owner, HelperConstructManager manager, int orderIndex, String rowLabel)
	{
		DraftOrderStepRequirement clone = manager.cloneOrderStepRequirementForEditor(orderIndex);
		OrderStepRequirementTreeEditorDialog d = new OrderStepRequirementTreeEditorDialog(owner, manager, orderIndex, rowLabel, clone);
		d.setVisible(true);
		return d.accepted;
	}

	private void rebuildTree()
	{
		visualRoot.removeAllChildren();
		if (rootDto != null)
		{
			visualRoot.add(buildVisual(rootDto));
		}
		treeModel.reload();
		for (int i = 0; i < tree.getRowCount(); i++)
		{
			tree.expandRow(i);
		}
	}

	private static DefaultMutableTreeNode buildVisual(DraftOrderStepRequirement dto)
	{
		DefaultMutableTreeNode n = new DefaultMutableTreeNode(dto);
		ensureChildren(dto);
		for (DraftOrderStepRequirement ch : dto.getChildren())
		{
			n.add(buildVisual(ch));
		}
		return n;
	}

	private static void ensureChildren(DraftOrderStepRequirement d)
	{
		if (d.getChildren() == null)
		{
			d.setChildren(new ArrayList<>());
		}
	}

	private DefaultMutableTreeNode selectedVisualNode()
	{
		TreePath path = tree.getSelectionPath();
		if (path == null)
		{
			return null;
		}
		Object last = path.getLastPathComponent();
		return last instanceof DefaultMutableTreeNode ? (DefaultMutableTreeNode) last : null;
	}

	private DraftOrderStepRequirement selectedDto()
	{
		DefaultMutableTreeNode n = selectedVisualNode();
		if (n == null)
		{
			return null;
		}
		Object o = n.getUserObject();
		return o instanceof DraftOrderStepRequirement ? (DraftOrderStepRequirement) o : null;
	}

	private boolean isRootMarker(DefaultMutableTreeNode n)
	{
		return n != null && n.getUserObject() instanceof RootMarker;
	}

	/**
	 * Container to attach new nodes: selected GROUP/empty INVERT, else parent of selected leaf, else root slot.
	 */
	private DraftOrderStepRequirement resolveAddParent()
	{
		DefaultMutableTreeNode n = selectedVisualNode();
		if (n == null || isRootMarker(n))
		{
			if (rootDto == null)
			{
				return null;
			}
			if (isStructuralContainer(rootDto))
			{
				return rootDto;
			}
			return null;
		}
		Object o = n.getUserObject();
		if (o instanceof DraftOrderStepRequirement)
		{
			DraftOrderStepRequirement d = (DraftOrderStepRequirement) o;
			if (isStructuralContainer(d))
			{
				return d;
			}
			DefaultMutableTreeNode p = (DefaultMutableTreeNode) n.getParent();
			if (p != null && !isRootMarker(p) && p.getUserObject() instanceof DraftOrderStepRequirement)
			{
				DraftOrderStepRequirement pd = (DraftOrderStepRequirement) p.getUserObject();
				if (isStructuralContainer(pd))
				{
					return pd;
				}
			}
			return rootDto != null && isStructuralContainer(rootDto) ? rootDto : null;
		}
		return null;
	}

	private static boolean isStructuralContainer(DraftOrderStepRequirement d)
	{
		if (d == null || d.getKind() == null)
		{
			return false;
		}
		String k = d.getKind().trim().toUpperCase(Locale.ROOT);
		if ("GROUP".equals(k))
		{
			return true;
		}
		if ("INVERT".equals(k))
		{
			ensureChildren(d);
			return d.getChildren().size() < 1;
		}
		return false;
	}

	private void addLeaf(DraftOrderStepRequirement leaf)
	{
		DraftOrderStepRequirement parent = resolveAddParent();
		if (parent == null && rootDto == null)
		{
			rootDto = leaf;
			rebuildTree();
			selectDto(leaf);
			return;
		}
		if (parent == null)
		{
			JOptionPane.showMessageDialog(this,
				"Select a logic group (AND/OR/…) or NOT that still needs an inner requirement, or clear the tree and start again.",
				"Order requirement",
				JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		ensureChildren(parent);
		String k = parent.getKind() == null ? "" : parent.getKind().trim().toUpperCase(Locale.ROOT);
		if ("INVERT".equals(k) && !parent.getChildren().isEmpty())
		{
			JOptionPane.showMessageDialog(this, "NOT already has an inner requirement. Remove it first or edit it.", "Order requirement", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		parent.getChildren().add(leaf);
		rebuildTree();
		selectDto(leaf);
	}

	private void addLogicGroup(LogicType lt)
	{
		DraftOrderStepRequirement group = DraftOrderStepRequirement.group(lt.name());
		DraftOrderStepRequirement parent = resolveAddParent();
		if (parent == null && rootDto == null)
		{
			rootDto = group;
			rebuildTree();
			selectDto(group);
			return;
		}
		if (parent == null)
		{
			JOptionPane.showMessageDialog(this,
				"Select a logic group to nest under, or clear the tree.",
				"Order requirement",
				JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		ensureChildren(parent);
		String pk = parent.getKind() == null ? "" : parent.getKind().trim().toUpperCase(Locale.ROOT);
		if ("INVERT".equals(pk) && !parent.getChildren().isEmpty())
		{
			JOptionPane.showMessageDialog(this, "NOT accepts only one inner node.", "Order requirement", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		parent.getChildren().add(group);
		rebuildTree();
		selectDto(group);
	}

	private void addCapturedItemInteractive()
	{
		List<HelperConstructManager.RequirementRoutingChoice> choices = manager.getRequirementRoutingChoices();
		if (choices.isEmpty())
		{
			JOptionPane.showMessageDialog(this, "Add captured items on the Item reqs tab first.", "Captured item", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		JComboBox<String> combo = new JComboBox<>();
		for (HelperConstructManager.RequirementRoutingChoice c : choices)
		{
			combo.addItem(c.getLabel() + " (" + c.getRawId() + ")");
		}
		int r = JOptionPane.showConfirmDialog(this, combo, "Pick item requirement", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (r != JOptionPane.OK_OPTION)
		{
			return;
		}
		int idx = combo.getSelectedIndex();
		if (idx < 0)
		{
			return;
		}
		addLeaf(DraftOrderStepRequirement.item(choices.get(idx).getRawId()));
	}

	private void clearPendingVarbitRouting()
	{
		pendingVarbitRouting = false;
	}

	private void stashPendingVarbitRouting(int varbitId, int requiredValue, String operationName, String displayText)
	{
		pendingVarbitRouting = true;
		pendingVarbitId = varbitId;
		pendingVarbitRequiredValue = requiredValue;
		pendingVarbitOperation = operationName == null || operationName.isBlank() ? "EQUAL" : operationName.trim();
		pendingVarbitDisplayText = displayText == null ? "" : displayText;
	}

	private void addVarbitFromExistingInteractive(boolean forInvert)
	{
		List<HelperConstructManager.VarbitSlotRow> rows = manager.getVarbitSlotsInQuestOrderForEditor();
		if (rows.isEmpty())
		{
			JOptionPane.showMessageDialog(this,
				"No varbit slots yet. Use \"Create new varbit\" or add rows on the Varbit reqs tab first.",
				"Add varbit",
				JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		JComboBox<String> combo = new JComboBox<>();
		for (HelperConstructManager.VarbitSlotRow row : rows)
		{
			combo.addItem(formatVarbitSlotChoiceLabel(row));
		}
		int r = JOptionPane.showConfirmDialog(this, combo, "Pick varbit", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (r != JOptionPane.OK_OPTION)
		{
			return;
		}
		int idx = combo.getSelectedIndex();
		if (idx < 0)
		{
			return;
		}
		HelperConstructManager.VarbitSlotRow pick = rows.get(idx);
		stashPendingVarbitRouting(pick.getVarbitId(), pick.getRequiredValue(), pick.getOperation(), pick.getDisplayText());
		if (forInvert)
		{
			addInvertWithInner(DraftOrderStepRequirement.orderVarbitSlot());
		}
		else
		{
			addLeaf(DraftOrderStepRequirement.orderVarbitSlot());
		}
	}

	private static String formatVarbitSlotChoiceLabel(HelperConstructManager.VarbitSlotRow row)
	{
		String disp = row.getDisplayText();
		String base = row.getVarName() + " — " + row.getVarbitId() + " " + row.getOperation() + " " + row.getRequiredValue();
		return disp != null && !disp.isBlank() ? disp + " — " + base : base;
	}

	private void addVarbitCreateNewInteractive(boolean forInvert)
	{
		VarbitRoutingPick p = showCreateVarbitFormOrNull();
		if (p == null)
		{
			return;
		}
		stashPendingVarbitRouting(p.varbitId, p.requiredValue, p.operation, p.displayText);
		if (forInvert)
		{
			addInvertWithInner(DraftOrderStepRequirement.orderVarbitSlot());
		}
		else
		{
			addLeaf(DraftOrderStepRequirement.orderVarbitSlot());
		}
	}

	private VarbitRoutingPick showCreateVarbitFormOrNull()
	{
		JSpinner idSp = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
		JSpinner valSp = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
		JComboBox<String> op = new JComboBox<>();
		for (Operation o : Operation.values())
		{
			op.addItem(o.name());
		}
		op.setSelectedItem(Operation.EQUAL.name());
		JTextField disp = new JTextField(24);
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(labeled("Varbit id", idSp));
		p.add(labeled("Required value", valSp));
		p.add(labeled("Operation", op));
		p.add(labeled("Display text (optional)", disp));
		int r = JOptionPane.showConfirmDialog(this, p, "Create new varbit", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (r != JOptionPane.OK_OPTION)
		{
			return null;
		}
		int vid = ((Number) idSp.getValue()).intValue();
		int vval = ((Number) valSp.getValue()).intValue();
		String opName = (String) op.getSelectedItem();
		return new VarbitRoutingPick(vid, vval, opName, disp.getText());
	}

	private void addInvertInteractive()
	{
		String[] options = { "Add varbit", "Create new varbit", "Item…" };
		int c = JOptionPane.showOptionDialog(this,
			"Choose the inner requirement for NOT:",
			"NOT (invert)",
			JOptionPane.DEFAULT_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[0]);
		if (c < 0)
		{
			return;
		}
		if (c == 0)
		{
			addVarbitFromExistingInteractive(true);
			return;
		}
		if (c == 1)
		{
			addVarbitCreateNewInteractive(true);
			return;
		}
		List<HelperConstructManager.RequirementRoutingChoice> choices = manager.getRequirementRoutingChoices();
		if (choices.isEmpty())
		{
			JOptionPane.showMessageDialog(this, "Add captured items on the Item reqs tab first.", "Captured item", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		JComboBox<String> combo = new JComboBox<>();
		for (HelperConstructManager.RequirementRoutingChoice ch : choices)
		{
			combo.addItem(ch.getLabel() + " (" + ch.getRawId() + ")");
		}
		int r = JOptionPane.showConfirmDialog(this, combo, "Pick item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (r != JOptionPane.OK_OPTION || combo.getSelectedIndex() < 0)
		{
			return;
		}
		addInvertWithInner(DraftOrderStepRequirement.item(choices.get(combo.getSelectedIndex()).getRawId()));
	}

	private void addInvertWithInner(DraftOrderStepRequirement inner)
	{
		DraftOrderStepRequirement inv = DraftOrderStepRequirement.invert(inner);
		DraftOrderStepRequirement parent = resolveAddParent();
		if (parent == null && rootDto == null)
		{
			rootDto = inv;
			rebuildTree();
			selectDto(inv);
			return;
		}
		if (parent == null)
		{
			JOptionPane.showMessageDialog(this,
				"Select a logic group to add under, or clear the tree.",
				"Order requirement",
				JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		ensureChildren(parent);
		String pk = parent.getKind() == null ? "" : parent.getKind().trim().toUpperCase(Locale.ROOT);
		if ("INVERT".equals(pk) && !parent.getChildren().isEmpty())
		{
			JOptionPane.showMessageDialog(this, "NOT accepts only one inner node.", "Order requirement", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		parent.getChildren().add(inv);
		rebuildTree();
		selectDto(inv);
	}

	private static JPanel labeled(String title, JComponent field)
	{
		JPanel row = new JPanel(new BorderLayout(6, 0));
		row.add(new JLabel(title + ":"), BorderLayout.WEST);
		row.add(field, BorderLayout.CENTER);
		return row;
	}

	private void removeSelected()
	{
		DraftOrderStepRequirement sel = selectedDto();
		DefaultMutableTreeNode vn = selectedVisualNode();
		if (sel == null)
		{
			if (vn != null && isRootMarker(vn))
			{
				rootDto = null;
				rebuildTree();
			}
			return;
		}
		if (sel == rootDto)
		{
			rootDto = null;
			rebuildTree();
			return;
		}
		if (!removeChildRecursive(rootDto, sel))
		{
			JOptionPane.showMessageDialog(this, "Could not remove node from tree.", "Order requirement", JOptionPane.WARNING_MESSAGE);
		}
		else
		{
			rebuildTree();
		}
	}

	private static boolean removeChildRecursive(DraftOrderStepRequirement parent, DraftOrderStepRequirement target)
	{
		if (parent == null || parent.getChildren() == null)
		{
			return false;
		}
		List<DraftOrderStepRequirement> ch = parent.getChildren();
		for (int i = 0; i < ch.size(); i++)
		{
			if (ch.get(i) == target)
			{
				ch.remove(i);
				return true;
			}
			if (removeChildRecursive(ch.get(i), target))
			{
				return true;
			}
		}
		return false;
	}

	private void editSelectedNode()
	{
		DraftOrderStepRequirement d = selectedDto();
		if (d == null)
		{
			return;
		}
		String k = d.getKind() == null ? "" : d.getKind().trim().toUpperCase(Locale.ROOT);
		switch (k)
		{
			case "GROUP":
				editGroup(d);
				break;
			case "ITEM":
			case "CAPTURED_ITEM":
				editCapturedItem(d);
				break;
			case "VARBIT":
			case "INLINE_VARBIT":
			case "ORDER_VARBIT":
			case "ROUTING_VARBIT":
				JOptionPane.showMessageDialog(this,
					"Varbit id, value, and operation are edited on the Varbit reqs tab for this quest-order row.",
					"Order varbit",
					JOptionPane.INFORMATION_MESSAGE);
				break;
			default:
				JOptionPane.showMessageDialog(this, "Nothing to edit for this node type.", "Edit", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void editGroup(DraftOrderStepRequirement d)
	{
		JComboBox<String> combo = new JComboBox<>(new String[] { "AND", "OR", "NOR", "NAND" });
		combo.setSelectedItem(d.getLogic() == null ? "AND" : d.getLogic().trim().toUpperCase(Locale.ROOT));
		int r = JOptionPane.showConfirmDialog(this, combo, "Logic type", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (r == JOptionPane.OK_OPTION && combo.getSelectedItem() != null)
		{
			d.setLogic((String) combo.getSelectedItem());
			rebuildTree();
			selectDto(d);
		}
	}

	private void editCapturedItem(DraftOrderStepRequirement d)
	{
		List<HelperConstructManager.RequirementRoutingChoice> choices = manager.getRequirementRoutingChoices();
		if (choices.isEmpty())
		{
			return;
		}
		JComboBox<String> combo = new JComboBox<>();
		int sel = 0;
		for (int i = 0; i < choices.size(); i++)
		{
			HelperConstructManager.RequirementRoutingChoice c = choices.get(i);
			if (d.getItemRawId() != null && d.getItemRawId() == c.getRawId())
			{
				sel = i;
			}
			combo.addItem(c.getLabel() + " (" + c.getRawId() + ")");
		}
		combo.setSelectedIndex(sel);
		int r = JOptionPane.showConfirmDialog(this, combo, "Captured item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (r == JOptionPane.OK_OPTION && combo.getSelectedIndex() >= 0)
		{
			d.setItemRawId(choices.get(combo.getSelectedIndex()).getRawId());
			rebuildTree();
			selectDto(d);
		}
	}

	private void selectDto(DraftOrderStepRequirement dto)
	{
		DefaultMutableTreeNode node = findVisual(visualRoot, dto);
		if (node != null)
		{
			tree.setSelectionPath(new TreePath(node.getPath()));
		}
	}

	private static DefaultMutableTreeNode findVisual(DefaultMutableTreeNode n, DraftOrderStepRequirement dto)
	{
		if (n.getUserObject() == dto)
		{
			return n;
		}
		for (int i = 0; i < n.getChildCount(); i++)
		{
			DefaultMutableTreeNode f = findVisual((DefaultMutableTreeNode) n.getChildAt(i), dto);
			if (f != null)
			{
				return f;
			}
		}
		return null;
	}

	private void onOk()
	{
		if (rootDto != null && pendingVarbitRouting && OrderStepRequirementSupport.treeContainsOrderVarbitLeaf(rootDto))
		{
			if (!manager.applyVarbitRoutingToOrderLineByIndex(
				orderIndex,
				pendingVarbitId,
				pendingVarbitRequiredValue,
				pendingVarbitOperation,
				pendingVarbitDisplayText,
				false))
			{
				JOptionPane.showMessageDialog(this,
					"Invalid operation or could not apply varbit routing to this order row.",
					"Could not save",
					JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		String err = manager.applyOrderStepRequirementTree(orderIndex, rootDto);
		if (err != null)
		{
			JOptionPane.showMessageDialog(this, err, "Could not save", JOptionPane.ERROR_MESSAGE);
			return;
		}
		accepted = true;
		dispose();
	}

	private static final class VarbitRoutingPick
	{
		final int varbitId;
		final int requiredValue;
		final String operation;
		final String displayText;

		VarbitRoutingPick(int varbitId, int requiredValue, String operation, String displayText)
		{
			this.varbitId = varbitId;
			this.requiredValue = requiredValue;
			this.operation = operation;
			this.displayText = displayText;
		}
	}

	private String formatPendingVarbitSummary()
	{
		String disp = pendingVarbitDisplayText == null || pendingVarbitDisplayText.isBlank()
			? null
			: pendingVarbitDisplayText;
		String op = pendingVarbitOperation == null || pendingVarbitOperation.isBlank() ? "EQUAL" : pendingVarbitOperation;
		if (disp != null)
		{
			return disp + " — vb " + pendingVarbitId + " " + op + " " + pendingVarbitRequiredValue;
		}
		return "Varbit " + pendingVarbitId + " " + op + " " + pendingVarbitRequiredValue;
	}

	private String describeNode(DraftOrderStepRequirement d)
	{
		if (d == null || d.getKind() == null)
		{
			return "(invalid)";
		}
		switch (d.getKind().trim().toUpperCase(Locale.ROOT))
		{
			case "GROUP":
				return "Logic: " + (d.getLogic() == null ? "?" : d.getLogic().toUpperCase(Locale.ROOT));
			case "INVERT":
				return "NOT (invert)";
			case "ITEM":
			case "CAPTURED_ITEM":
				return "Item raw id " + d.getItemRawId();
			case "ORDER_VARBIT":
			case "ROUTING_VARBIT":
				if (pendingVarbitRouting)
				{
					return formatPendingVarbitSummary();
				}
				return manager.formatOrderVarbitLeafSummaryForEditor(orderIndex);
			case "VARBIT":
			case "INLINE_VARBIT":
				return "Varbit " + d.getVarbitId() + " " + (d.getVarbitOperation() == null ? "" : d.getVarbitOperation()) + " " + d.getVarbitRequiredValue();
			default:
				return d.getKind();
		}
	}

	private final class ReqTreeCellRenderer extends DefaultTreeCellRenderer
	{
		@Override
		public Component getTreeCellRendererComponent(
			JTree tree,
			Object value,
			boolean sel,
			boolean expanded,
			boolean leaf,
			int row,
			boolean hasFocus)
		{
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			if (!(value instanceof DefaultMutableTreeNode))
			{
				return this;
			}
			Object u = ((DefaultMutableTreeNode) value).getUserObject();
			if (u instanceof RootMarker)
			{
				setText("Quest order (add logic or requirement here)");
			}
			else if (u instanceof DraftOrderStepRequirement)
			{
				setText(describeNode((DraftOrderStepRequirement) u));
			}
			return this;
		}
	}
}
