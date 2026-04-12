package com.questhelper.panel;

import com.questhelper.managers.HelperConstructManager;
import com.questhelper.managers.HelperConstructModels.DraftOrderStepRequirement;
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
 * leaves (routing varbit, captured item, inline varbit), and NOT (invert).
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
		reqMenu.add(newRoutingVarbitItem());
		reqMenu.add(newCapturedItemItem());
		reqMenu.add(newInlineVarbitItem());
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

	private JMenuItem newRoutingVarbitItem()
	{
		JMenuItem it = new JMenuItem("Order varbit (slot attachment)");
		it.addActionListener(e -> addLeaf(DraftOrderStepRequirement.orderVarbitSlot()));
		return it;
	}

	private JMenuItem newCapturedItemItem()
	{
		JMenuItem it = new JMenuItem("Captured item…");
		it.addActionListener(e -> addCapturedItemInteractive());
		return it;
	}

	private JMenuItem newInlineVarbitItem()
	{
		JMenuItem it = new JMenuItem("Inline varbit…");
		it.addActionListener(e -> addInlineVarbitInteractive());
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

	private void addInlineVarbitInteractive()
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
		int r = JOptionPane.showConfirmDialog(this, p, "Inline varbit", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (r != JOptionPane.OK_OPTION)
		{
			return;
		}
		int vid = ((Number) idSp.getValue()).intValue();
		int vval = ((Number) valSp.getValue()).intValue();
		String opName = (String) op.getSelectedItem();
		addLeaf(DraftOrderStepRequirement.varbit(vid, vval, opName, disp.getText()));
	}

	private void addInvertInteractive()
	{
		String[] options = { "Order varbit (slot)", "Item…", "Varbit…" };
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
		if (c == 2)
		{
			addInvertWithInlineInner();
			return;
		}
		DraftOrderStepRequirement inner;
		if (c == 0)
		{
			inner = DraftOrderStepRequirement.orderVarbitSlot();
		}
		else
		{
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
			inner = DraftOrderStepRequirement.item(choices.get(combo.getSelectedIndex()).getRawId());
		}
		addInvertWithInner(inner);
	}

	private void addInvertWithInlineInner()
	{
		JSpinner idSp = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
		JSpinner valSp = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
		JComboBox<String> op = new JComboBox<>();
		for (Operation o : Operation.values())
		{
			op.addItem(o.name());
		}
		op.setSelectedItem(Operation.EQUAL.name());
		JTextField disp = new JTextField(20);
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(labeled("Varbit id", idSp));
		p.add(labeled("Required value", valSp));
		p.add(labeled("Operation", op));
		p.add(labeled("Display text", disp));
		int r = JOptionPane.showConfirmDialog(this, p, "Inline varbit (inner)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (r != JOptionPane.OK_OPTION)
		{
			return;
		}
		DraftOrderStepRequirement inner = DraftOrderStepRequirement.varbit(
			((Number) idSp.getValue()).intValue(),
			((Number) valSp.getValue()).intValue(),
			(String) op.getSelectedItem(),
			disp.getText());
		addInvertWithInner(inner);
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
				editInline(d);
				break;
			case "ORDER_VARBIT":
			case "ROUTING_VARBIT":
				JOptionPane.showMessageDialog(this,
					"This node uses the Varbit attachment on the step for this order slot (Varbits tab).",
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

	private void editInline(DraftOrderStepRequirement d)
	{
		JSpinner idSp = new JSpinner(new SpinnerNumberModel(d.getVarbitId() == null ? 0 : d.getVarbitId(), 0, Integer.MAX_VALUE, 1));
		JSpinner valSp = new JSpinner(new SpinnerNumberModel(d.getVarbitRequiredValue() == null ? 0 : d.getVarbitRequiredValue(), 0, Integer.MAX_VALUE, 1));
		JComboBox<String> op = new JComboBox<>();
		for (Operation o : Operation.values())
		{
			op.addItem(o.name());
		}
		String curOp = d.getVarbitOperation() == null ? "EQUAL" : d.getVarbitOperation().trim();
		op.setSelectedItem(curOp.toUpperCase(Locale.ROOT));
		JTextField disp = new JTextField(d.getVarbitDisplayText() == null ? "" : d.getVarbitDisplayText(), 24);
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(labeled("Varbit id", idSp));
		p.add(labeled("Required value", valSp));
		p.add(labeled("Operation", op));
		p.add(labeled("Display text", disp));
		int r = JOptionPane.showConfirmDialog(this, p, "Inline varbit", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (r == JOptionPane.OK_OPTION)
		{
			d.setVarbitId(((Number) idSp.getValue()).intValue());
			d.setVarbitRequiredValue(((Number) valSp.getValue()).intValue());
			d.setVarbitOperation((String) op.getSelectedItem());
			d.setVarbitDisplayText(disp.getText());
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
		if (rootDto != null)
		{
			String err = com.questhelper.managers.OrderStepRequirementSupport.validateTreeOrError(rootDto);
			if (err != null)
			{
				JOptionPane.showMessageDialog(this, err, "Invalid requirement tree", JOptionPane.ERROR_MESSAGE);
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

	private static String describe(DraftOrderStepRequirement d)
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
				return "Order varbit (slot)";
			case "VARBIT":
			case "INLINE_VARBIT":
				return "Varbit " + d.getVarbitId() + " " + (d.getVarbitOperation() == null ? "" : d.getVarbitOperation()) + " " + d.getVarbitRequiredValue();
			default:
				return d.getKind();
		}
	}

	private static final class ReqTreeCellRenderer extends DefaultTreeCellRenderer
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
				setText(describe((DraftOrderStepRequirement) u));
			}
			return this;
		}
	}
}
