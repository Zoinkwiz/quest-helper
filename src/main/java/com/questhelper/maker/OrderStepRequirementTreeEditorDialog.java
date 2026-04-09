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
import com.questhelper.maker.HelperConstructModels.DraftOrderStepRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.ColorScheme;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Visual editor for {@link DraftOrderStepRequirement} on a quest-order row: logic groups (AND/OR/NOR/NAND),
 * order varbit leaves ({@code Add varbit} reuses another slot’s routing; {@code Create new varbit} defines routing for
 * this row), captured items, order zone slots (corners on the Zone reqs tab), and NOT (invert). Varbit values are edited on the Varbit reqs tab; zone corners on the Zone reqs tab.
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

	private boolean pendingZoneRouting;
	private WorldPoint pendingZoneC1;
	private WorldPoint pendingZoneC2;
	private String pendingZoneDisplayText;

	private final DefaultMutableTreeNode visualRoot = new DefaultMutableTreeNode(RootMarker.INSTANCE);
	private final DefaultTreeModel treeModel;
	private final JTree tree;
	private final JComboBox<OrderConditionMode> modeCombo;
	private final JCheckBox passOnceCompletedCheck;
	private final boolean sectionDividerRow;
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
		this.sectionDividerRow = manager.isOrderRowSectionDivider(orderIndex);
		this.rootDto = initialClone;
		clearPendingVarbitRouting();
		OrderConditionMode initialMode = manager.getOrderStepRequirementMode(orderIndex);
		boolean initialPassOnceCompleted = manager.isOrderStepPassOnceCompletedOnce(orderIndex);

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
		reqMenu.add(newAddSkillFromExistingItem());
		reqMenu.add(newCreateSkillItem());
		reqMenu.add(newAddZoneFromExistingItem());
		reqMenu.add(newCreateZoneItem());
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

		JPanel toolbar = new JPanel(new BorderLayout());
		toolbar.setBackground(ColorScheme.DARK_GRAY_COLOR);
		JLabel semanticsLabel = new JLabel("Semantics:");
		semanticsLabel.setToolTipText("Choose how this row's condition tree is interpreted.");
		modeCombo = new JComboBox<>(OrderConditionMode.values());
		modeCombo.setRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof OrderConditionMode)
				{
					OrderConditionMode mode = (OrderConditionMode) value;
					setText(mode == OrderConditionMode.CONTINUE_WHEN_TRUE ? "Continue when true" : "Show when true");
				}
				return this;
			}
		});
		modeCombo.setSelectedItem(initialMode == null ? OrderConditionMode.CONTINUE_WHEN_TRUE : initialMode);
		updateSemanticsToolTip();
		modeCombo.addActionListener(e -> updateSemanticsToolTip());
		passOnceCompletedCheck = new JCheckBox("Pass once completed once");
		passOnceCompletedCheck.setSelected(initialPassOnceCompleted);
		passOnceCompletedCheck.setToolTipText("When this row is completed once, auto-tick its sidebar skip and keep it passed.");
		passOnceCompletedCheck.setOpaque(false);
		if (sectionDividerRow)
		{
			passOnceCompletedCheck.setSelected(false);
			passOnceCompletedCheck.setEnabled(false);
			passOnceCompletedCheck.setToolTipText("Not applicable to section rows.");
		}

		setOverflowHandler(addReq, anchor -> reqMenu.show(anchor, 0, anchor.getHeight()));
		setOverflowHandler(addLogic, anchor -> logicMenu.show(anchor, 0, anchor.getHeight()));

		List<JComponent> toolbarChain = List.of(
			semanticsLabel,
			modeCombo,
			passOnceCompletedCheck,
			addReq,
			addLogic,
			editBtn,
			removeBtn,
			clearBtn);
		toolbar.add(new OverflowingDialogToolbar(toolbarChain), BorderLayout.CENTER);

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

	private JMenuItem newAddZoneFromExistingItem()
	{
		JMenuItem it = new JMenuItem("Add zone");
		it.addActionListener(e -> addZoneFromExistingInteractive(false));
		return it;
	}

	private JMenuItem newCreateZoneItem()
	{
		JMenuItem it = new JMenuItem("Create new zone…");
		it.addActionListener(e -> addZoneCreateNewInteractive(false));
		return it;
	}

	private JMenuItem newAddSkillFromExistingItem()
	{
		JMenuItem it = new JMenuItem("Add skill");
		it.addActionListener(e -> addSkillFromExistingInteractive(false));
		return it;
	}

	private JMenuItem newCreateSkillItem()
	{
		JMenuItem it = new JMenuItem("Create new skill…");
		it.addActionListener(e -> addSkillCreateNewInteractive(false));
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
			return isStructuralContainer(rootDto) ? rootDto : null;
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
			return d.getChildren().isEmpty();
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
		JCheckBox checkBank = new JCheckBox("Also check bank");
		JCheckBox equipped = new JCheckBox("Must be equipped");
		JSpinner qty = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
		JPanel qtyRow = new JPanel(new BorderLayout(6, 0));
		qtyRow.add(new JLabel("Quantity:"), BorderLayout.WEST);
		qtyRow.add(qty, BorderLayout.CENTER);
		JPanel panel = new JPanel(new BorderLayout(0, 6));
		panel.add(combo, BorderLayout.NORTH);
		JPanel south = new JPanel(new BorderLayout(0, 6));
		south.add(qtyRow, BorderLayout.NORTH);
		JPanel checks = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		checks.add(checkBank);
		checks.add(equipped);
		south.add(checks, BorderLayout.SOUTH);
		panel.add(south, BorderLayout.SOUTH);
		int r = JOptionPane.showConfirmDialog(this, panel, "Pick item requirement", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (r != JOptionPane.OK_OPTION)
		{
			return;
		}
		int idx = combo.getSelectedIndex();
		if (idx < 0)
		{
			return;
		}
		int quantity = ((Number) qty.getValue()).intValue();
		addLeaf(DraftOrderStepRequirement.item(choices.get(idx).getRawId(), checkBank.isSelected(), quantity, equipped.isSelected()));
	}

	private void clearPendingVarbitRouting()
	{
		pendingZoneRouting = false;
		pendingZoneC1 = null;
		pendingZoneC2 = null;
		pendingZoneDisplayText = null;
	}

	private void stashPendingZoneRouting(WorldPoint c1, WorldPoint c2, String displayText)
	{
		pendingZoneRouting = true;
		pendingZoneC1 = c1;
		pendingZoneC2 = c2;
		pendingZoneDisplayText = displayText == null || displayText.isBlank() ? null : displayText.trim();
	}

	private void addZoneFromExistingInteractive(boolean forInvert)
	{
		List<HelperConstructManager.ZoneSlotRow> rows = manager.getZoneSlotsInQuestOrderForEditor();
		if (rows.isEmpty())
		{
			JOptionPane.showMessageDialog(this,
				"No zone rows yet. Use \"Create new zone…\" here, or select a quest-order step row and use Zone reqs → Add.",
				"Add zone",
				JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		JComboBox<String> combo = new JComboBox<>();
		for (HelperConstructManager.ZoneSlotRow row : rows)
		{
			combo.addItem(formatZoneSlotChoiceLabel(row));
		}
		int r = JOptionPane.showConfirmDialog(this, combo, "Pick zone row", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (r != JOptionPane.OK_OPTION)
		{
			return;
		}
		int idx = combo.getSelectedIndex();
		if (idx < 0)
		{
			return;
		}
		HelperConstructManager.ZoneSlotRow pick = rows.get(idx);
		WorldPoint c1 = parseCsvWorldPointOrNull(pick.getCorner1Text());
		WorldPoint c2 = parseCsvWorldPointOrNull(pick.getCorner2Text());
		if (c1 == null || c2 == null)
		{
			JOptionPane.showMessageDialog(this, "Could not read zone corners from the selected row.", "Add zone", JOptionPane.WARNING_MESSAGE);
			return;
		}
		DraftOrderStepRequirement zoneLeaf = DraftOrderStepRequirement.zone(c1, c2, pick.getDisplayText());
		if (forInvert)
		{
			addInvertWithInner(zoneLeaf);
		}
		else
		{
			addLeaf(zoneLeaf);
		}
	}

	private static WorldPoint parseCsvWorldPointOrNull(String text)
	{
		if (text == null)
		{
			return null;
		}
		String[] parts = text.trim().split(",");
		if (parts.length != 3)
		{
			return null;
		}
		try
		{
			int x = Integer.parseInt(parts[0].trim());
			int y = Integer.parseInt(parts[1].trim());
			int p = Integer.parseInt(parts[2].trim());
			return new WorldPoint(x, y, p);
		}
		catch (NumberFormatException ex)
		{
			return null;
		}
	}

	private static String formatZoneSlotChoiceLabel(HelperConstructManager.ZoneSlotRow row)
	{
		String disp = row.getDisplayText();
		String base = row.getVarName() + " — " + row.getCorner1Text() + " / " + row.getCorner2Text();
		return disp != null && !disp.isBlank() ? disp + " — " + base : base;
	}

	private static Skill parseSkillNameOrDefault(String name)
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

	private static Operation parseOperation(String name)
	{
		if (name == null || name.isBlank())
		{
			return Operation.GREATER_EQUAL;
		}
		try
		{
			return Operation.valueOf(name.trim().toUpperCase(Locale.ROOT));
		}
		catch (IllegalArgumentException ex)
		{
			return Operation.GREATER_EQUAL;
		}
	}

	private SkillPick showSkillFormOrNull(String skillExisting, Integer levelExisting, String opExisting, String displayExisting, Boolean canBoostExisting)
	{
		JComboBox<Skill> skill = new JComboBox<>(Skill.values());
		skill.setMaximumRowCount(Skill.values().length);
		skill.setSelectedItem(parseSkillNameOrDefault(skillExisting));
		int level = levelExisting == null ? 1 : Math.max(1, levelExisting);
		JSpinner levelSp = new JSpinner(new SpinnerNumberModel(level, 1, 255, 1));
		JComboBox<Operation> op = new JComboBox<>(Operation.values());
		op.setMaximumRowCount(Operation.values().length);
		op.setSelectedItem(parseOperation(opExisting));
		JCheckBox boost = new JCheckBox("Can be boosted", canBoostExisting == null || canBoostExisting);
		JTextField disp = new JTextField(displayExisting == null ? "" : displayExisting, 24);

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(labeled("Skill", skill));
		p.add(labeled("Required level", levelSp));
		p.add(labeled("Operation", op));
		p.add(boost);
		p.add(labeled("Display text (optional)", disp));
		int r = JOptionPane.showConfirmDialog(this, p, "Skill requirement", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (r != JOptionPane.OK_OPTION)
		{
			return null;
		}
		Skill s = (Skill) skill.getSelectedItem();
		if (s == null)
		{
			s = Skill.ATTACK;
		}
		Operation operation = (Operation) op.getSelectedItem();
		if (operation == null)
		{
			operation = Operation.GREATER_EQUAL;
		}
		String dt = disp.getText().trim();
		return new SkillPick(s, ((Number) levelSp.getValue()).intValue(), operation, dt.isEmpty() ? null : dt, boost.isSelected());
	}

	private static String formatSkillChoiceLabel(String skillName, int requiredLevel, String operation, String displayText, boolean canBoost)
	{
		Skill s = parseSkillNameOrDefault(skillName);
		String op = operation == null || operation.isBlank() ? Operation.GREATER_EQUAL.name() : operation;
		String base = requiredLevel + " " + s.getName() + " (" + op + ")";
		if (canBoost)
		{
			base += " [boost]";
		}
		return displayText != null && !displayText.isBlank() ? displayText + " — " + base : base;
	}

	private void addZoneCreateNewInteractive(boolean forInvert)
	{
		ZonePick pick = showZoneFormOrNull(null, null, null);
		if (pick == null)
		{
			return;
		}
		DraftOrderStepRequirement leaf = DraftOrderStepRequirement.zone(
			pick.corner1,
			pick.corner2,
			pick.displayText);
		if (forInvert)
		{
			addInvertWithInner(leaf);
		}
		else
		{
			addLeaf(leaf);
		}
	}

	private void addSkillFromExistingInteractive(boolean forInvert)
	{
		List<HelperConstructManager.SkillReqRow> rows = manager.getSkillRequirementsForEditor();
		if (rows.isEmpty())
		{
			JOptionPane.showMessageDialog(this,
				"No skill rows yet. Use \"Create new skill…\" or add rows on the Skill reqs tab first.",
				"Add skill",
				JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		JComboBox<String> combo = new JComboBox<>();
		for (HelperConstructManager.SkillReqRow row : rows)
		{
			combo.addItem(formatSkillChoiceLabel(row.getSkillName(), row.getRequiredLevel(), row.getOperation(), row.getDisplayText(), row.isCanBeBoosted()));
		}
		int r = JOptionPane.showConfirmDialog(this, combo, "Pick skill row", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (r != JOptionPane.OK_OPTION)
		{
			return;
		}
		int idx = combo.getSelectedIndex();
		if (idx < 0)
		{
			return;
		}
		HelperConstructManager.SkillReqRow pick = rows.get(idx);
		DraftOrderStepRequirement leaf = DraftOrderStepRequirement.skill(
			pick.getSkillName(),
			pick.getRequiredLevel(),
			pick.getOperation(),
			pick.getDisplayText(),
			pick.isCanBeBoosted());
		if (forInvert)
		{
			addInvertWithInner(leaf);
		}
		else
		{
			addLeaf(leaf);
		}
	}

	private void addSkillCreateNewInteractive(boolean forInvert)
	{
		SkillPick pick = showSkillFormOrNull(null, null, null, null, null);
		if (pick == null)
		{
			return;
		}
		DraftOrderStepRequirement leaf = DraftOrderStepRequirement.skill(
			pick.skill.name(),
			pick.requiredLevel,
			pick.operation.name(),
			pick.displayText,
			pick.canBeBoosted);
		if (forInvert)
		{
			addInvertWithInner(leaf);
		}
		else
		{
			addLeaf(leaf);
		}
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
		HelperConstructManager.VarbitSlotRow pick = showPickVarbitFromCatalogOrNull(rows);
		if (pick == null)
		{
			return;
		}
		DraftOrderStepRequirement leaf = DraftOrderStepRequirement.varbit(
			pick.getVarbitId(),
			pick.getRequiredValue(),
			pick.getOperation(),
			pick.getDisplayText());
		if (forInvert)
		{
			addInvertWithInner(leaf);
		}
		else
		{
			addLeaf(leaf);
		}
	}

	@Nullable
	private HelperConstructManager.VarbitSlotRow showPickVarbitFromCatalogOrNull(List<HelperConstructManager.VarbitSlotRow> rows)
	{
		JDialog dlg = new JDialog(this, "Pick varbit", Dialog.ModalityType.APPLICATION_MODAL);
		dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		JPanel root = new JPanel(new BorderLayout(8, 8));
		root.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

		JPanel north = new JPanel(new BorderLayout(6, 0));
		north.add(new JLabel("Search:"), BorderLayout.WEST);
		JTextField search = new JTextField();
		search.setToolTipText("Filter by var name, varbit id, display text, operation, or required value (case-insensitive).");
		north.add(search, BorderLayout.CENTER);
		root.add(north, BorderLayout.NORTH);

		DefaultListModel<HelperConstructManager.VarbitSlotRow> listModel = new DefaultListModel<>();
		for (HelperConstructManager.VarbitSlotRow row : rows)
		{
			listModel.addElement(row);
		}
		JList<HelperConstructManager.VarbitSlotRow> list = new JList<>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList<?> lst, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				super.getListCellRendererComponent(lst, value, index, isSelected, cellHasFocus);
				if (value instanceof HelperConstructManager.VarbitSlotRow)
				{
					setText(formatVarbitSlotChoiceLabel((HelperConstructManager.VarbitSlotRow) value));
				}
				return this;
			}
		});
		if (!rows.isEmpty())
		{
			list.setSelectedIndex(0);
		}
		JScrollPane sp = new JScrollPane(list);
		sp.setPreferredSize(new Dimension(560, 320));
		root.add(sp, BorderLayout.CENTER);

		final HelperConstructManager.VarbitSlotRow[] chosen = { null };
		JButton okBtn = new JButton("OK");
		JButton cancelBtn = new JButton("Cancel");

		Runnable applyFilter = () ->
		{
			String q = search.getText().toLowerCase(Locale.ROOT).trim();
			HelperConstructManager.VarbitSlotRow prev = list.getSelectedValue();
			listModel.clear();
			for (HelperConstructManager.VarbitSlotRow row : rows)
			{
				if (varbitCatalogRowMatchesSearch(row, q))
				{
					listModel.addElement(row);
				}
			}
			if (listModel.isEmpty())
			{
				okBtn.setEnabled(false);
				return;
			}
			int pickIdx = 0;
			if (prev != null)
			{
				for (int i = 0; i < listModel.getSize(); i++)
				{
					if (listModel.getElementAt(i).getVarbitId() == prev.getVarbitId())
					{
						pickIdx = i;
						break;
					}
				}
			}
			list.setSelectedIndex(pickIdx);
			okBtn.setEnabled(list.getSelectedValue() != null);
		};

		search.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				applyFilter.run();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				applyFilter.run();
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				applyFilter.run();
			}
		});

		list.addListSelectionListener(e -> okBtn.setEnabled(list.getSelectedValue() != null));
		list.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2 && list.getSelectedValue() != null)
				{
					chosen[0] = list.getSelectedValue();
					dlg.dispose();
				}
			}
		});

		okBtn.addActionListener(e ->
		{
			HelperConstructManager.VarbitSlotRow v = list.getSelectedValue();
			if (v != null)
			{
				chosen[0] = v;
			}
			dlg.dispose();
		});
		cancelBtn.addActionListener(e -> dlg.dispose());

		JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		south.add(cancelBtn);
		south.add(okBtn);
		root.add(south, BorderLayout.SOUTH);

		dlg.setContentPane(root);
		dlg.getRootPane().setDefaultButton(okBtn);
		dlg.pack();
		dlg.setLocationRelativeTo(this);
		SwingUtilities.invokeLater(() -> search.requestFocusInWindow());
		dlg.setVisible(true);
		return chosen[0];
	}

	private static boolean varbitCatalogRowMatchesSearch(HelperConstructManager.VarbitSlotRow row, String q)
	{
		if (q.isEmpty())
		{
			return true;
		}
		String hay = String.valueOf(row.getVarbitId()) + " "
			+ (row.getDisplayText() == null ? "" : row.getDisplayText()) + " "
			+ (row.getVarName() == null ? "" : row.getVarName()) + " "
			+ (row.getOperation() == null ? "" : row.getOperation()) + " "
			+ row.getRequiredValue();
		hay = hay.toLowerCase(Locale.ROOT);
		return hay.contains(q);
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
		DraftOrderStepRequirement leaf = DraftOrderStepRequirement.varbit(
			p.varbitId,
			p.requiredValue,
			p.operation,
			p.displayText);
		if (forInvert)
		{
			addInvertWithInner(leaf);
		}
		else
		{
			addLeaf(leaf);
		}
	}

	private void editVarbitLeaf(DraftOrderStepRequirement d)
	{
		if (d == null)
		{
			return;
		}
		int existingId = d.getVarbitId() == null ? 0 : d.getVarbitId();
		int existingVal = d.getVarbitRequiredValue() == null ? 1 : d.getVarbitRequiredValue();
		String existingOp = d.getVarbitOperation() == null || d.getVarbitOperation().isBlank()
			? Operation.EQUAL.name()
			: d.getVarbitOperation().trim();
		String dispExisting = d.getVarbitDisplayText() == null ? "" : d.getVarbitDisplayText();
		VarbitRoutingPick p = showVarbitFormOrNull(existingId, existingVal, existingOp, dispExisting, "Edit varbit");
		if (p == null)
		{
			return;
		}
		d.setKind("VARBIT");
		d.setVarbitId(p.varbitId);
		d.setVarbitRequiredValue(p.requiredValue);
		d.setVarbitOperation(p.operation);
		d.setVarbitDisplayText(p.displayText);
		d.setRequirementRefId("varbit:" + p.varbitId);
		if (d.getChildren() != null)
		{
			d.getChildren().clear();
		}
		rebuildTree();
		selectDto(d);
	}

	private VarbitRoutingPick showCreateVarbitFormOrNull()
	{
		return showVarbitFormOrNull(0, 0, Operation.EQUAL.name(), "", "Create new varbit");
	}

	private VarbitRoutingPick showVarbitFormOrNull(
		int varbitId,
		int requiredValue,
		String operationName,
		String displayExisting,
		String title)
	{
		JSpinner idSp = new JSpinner(new SpinnerNumberModel(varbitId, 0, Integer.MAX_VALUE, 1));
		JSpinner valSp = new JSpinner(new SpinnerNumberModel(requiredValue, 0, Integer.MAX_VALUE, 1));
		JComboBox<String> op = new JComboBox<>();
		for (Operation o : Operation.values())
		{
			op.addItem(o.name());
		}
		String opPick = operationName == null || operationName.isBlank() ? Operation.EQUAL.name() : operationName.trim();
		op.setSelectedItem(opPick);
		for (int i = 0; i < op.getItemCount(); i++)
		{
			if (opPick.equals(op.getItemAt(i)))
			{
				op.setSelectedIndex(i);
				break;
			}
		}
		JTextField disp = new JTextField(displayExisting == null ? "" : displayExisting, 24);
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(labeled("Varbit id", idSp));
		p.add(labeled("Required value", valSp));
		p.add(labeled("Operation", op));
		p.add(labeled("Display text (optional)", disp));
		int r = JOptionPane.showConfirmDialog(this, p, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (r != JOptionPane.OK_OPTION)
		{
			return null;
		}
		int vid = ((Number) idSp.getValue()).intValue();
		int vval = ((Number) valSp.getValue()).intValue();
		String opName = (String) op.getSelectedItem();
		return new VarbitRoutingPick(vid, vval, opName, disp.getText());
	}

	/**
	 * Two opposite corners of an axis-aligned rectangle (same as {@link com.questhelper.requirements.zone.Zone}).
	 */
	private ZonePick showZoneFormOrNull(WorldPoint corner1Existing, WorldPoint corner2Existing, String displayExisting)
	{
		int x1 = corner1Existing != null ? corner1Existing.getX() : 0;
		int y1 = corner1Existing != null ? corner1Existing.getY() : 0;
		int p1 = corner1Existing != null ? corner1Existing.getPlane() : 0;
		int x2 = corner2Existing != null ? corner2Existing.getX() : 1;
		int y2 = corner2Existing != null ? corner2Existing.getY() : 1;
		int p2 = corner2Existing != null ? corner2Existing.getPlane() : 0;
		JSpinner c1x = new JSpinner(new SpinnerNumberModel(x1, -60000, 60000, 1));
		JSpinner c1y = new JSpinner(new SpinnerNumberModel(y1, -60000, 60000, 1));
		JSpinner c1p = new JSpinner(new SpinnerNumberModel(p1, -3, 3, 1));
		JSpinner c2x = new JSpinner(new SpinnerNumberModel(x2, -60000, 60000, 1));
		JSpinner c2y = new JSpinner(new SpinnerNumberModel(y2, -60000, 60000, 1));
		JSpinner c2p = new JSpinner(new SpinnerNumberModel(p2, -3, 3, 1));
		JTextField disp = new JTextField(displayExisting == null ? "" : displayExisting, 24);
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(new JLabel("Corner 1 — world X, Y, plane:"));
		p.add(cornerSpinnersRow(c1x, c1y, c1p));
		p.add(new JLabel("Corner 2 — world X, Y, plane:"));
		p.add(cornerSpinnersRow(c2x, c2y, c2p));
		p.add(labeled("Display text (optional)", disp));
		int r = JOptionPane.showConfirmDialog(this, p, "Zone (rectangle from two corners)",
			JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (r != JOptionPane.OK_OPTION)
		{
			return null;
		}
		WorldPoint wp1 = new WorldPoint(
			((Number) c1x.getValue()).intValue(),
			((Number) c1y.getValue()).intValue(),
			((Number) c1p.getValue()).intValue());
		WorldPoint wp2 = new WorldPoint(
			((Number) c2x.getValue()).intValue(),
			((Number) c2y.getValue()).intValue(),
			((Number) c2p.getValue()).intValue());
		String dt = disp.getText().trim();
		return new ZonePick(wp1, wp2, dt.isEmpty() ? null : dt);
	}

	private static JPanel cornerSpinnersRow(JSpinner x, JSpinner y, JSpinner plane)
	{
		JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
		row.add(new JLabel("X"));
		row.add(x);
		row.add(new JLabel("Y"));
		row.add(y);
		row.add(new JLabel("Plane"));
		row.add(plane);
		return row;
	}

	private void addInvertInteractive()
	{
		String[] options = { "Add varbit", "Create new varbit", "Item…", "Skill…", "Zone…" };
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
		if (c == 3)
		{
			String[] sopts = { "From existing skill row", "New skill…" };
			int s = JOptionPane.showOptionDialog(this,
				"Choose how to add NOT(skill):",
				"Skill",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				sopts,
				sopts[0]);
			if (s == 0)
			{
				addSkillFromExistingInteractive(true);
			}
			else if (s == 1)
			{
				addSkillCreateNewInteractive(true);
			}
			return;
		}
		if (c == 4)
		{
			String[] zopts = { "From existing zone row", "New zone…" };
			int z = JOptionPane.showOptionDialog(this,
				"Choose how to add NOT(zone):",
				"Zone",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				zopts,
				zopts[0]);
			if (z == 0)
			{
				addZoneFromExistingInteractive(true);
			}
			else if (z == 1)
			{
				addZoneCreateNewInteractive(true);
			}
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
		JCheckBox checkBank = new JCheckBox("Also check bank");
		JCheckBox equipped = new JCheckBox("Must be equipped");
		JSpinner qty = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
		JPanel qtyRow = new JPanel(new BorderLayout(6, 0));
		qtyRow.add(new JLabel("Quantity:"), BorderLayout.WEST);
		qtyRow.add(qty, BorderLayout.CENTER);
		JPanel panel = new JPanel(new BorderLayout(0, 6));
		panel.add(combo, BorderLayout.NORTH);
		JPanel south = new JPanel(new BorderLayout(0, 6));
		south.add(qtyRow, BorderLayout.NORTH);
		JPanel checks = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		checks.add(checkBank);
		checks.add(equipped);
		south.add(checks, BorderLayout.SOUTH);
		panel.add(south, BorderLayout.SOUTH);
		if (JOptionPane.showConfirmDialog(this, panel, "Pick item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION
			|| combo.getSelectedIndex() < 0)
		{
			return;
		}
		int quantity = ((Number) qty.getValue()).intValue();
		addInvertWithInner(DraftOrderStepRequirement.item(choices.get(combo.getSelectedIndex()).getRawId(), checkBank.isSelected(), quantity, equipped.isSelected()));
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

	private static void setOverflowHandler(JButton button, Consumer<JButton> handler)
	{
		button.putClientProperty("orderConditionsOverflowHandler", handler);
	}

	private static final class OverflowingDialogToolbar extends JPanel
	{
		private final List<JComponent> chain;
		private final JPanel inlinePanel;
		private final JButton moreButton;
		private final JPopupMenu overflowMenu;

		OverflowingDialogToolbar(List<JComponent> chain)
		{
			super(new BorderLayout());
			this.chain = List.copyOf(chain);
			setOpaque(false);
			inlinePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
			inlinePanel.setOpaque(false);
			moreButton = new JButton("More...");
			moreButton.setToolTipText("Additional order condition actions.");
			overflowMenu = new JPopupMenu();
			moreButton.addActionListener(e -> overflowMenu.show(moreButton, 0, moreButton.getHeight()));
			add(inlinePanel, BorderLayout.CENTER);
			addComponentListener(new ComponentAdapter()
			{
				@Override
				public void componentResized(ComponentEvent e)
				{
					reconcile();
				}
			});
			SwingUtilities.invokeLater(this::reconcile);
		}

		private void reconcile()
		{
			final int hgap = 6;
			int avail = getWidth() - getInsets().left - getInsets().right;
			if (avail <= 0)
			{
				return;
			}
			int n = chain.size();
			int moreW = moreButton.getPreferredSize().width + hgap;
			int best = 0;
			for (int k = n; k >= 0; k--)
			{
				int prefixW = prefixWidth(chain, 0, k, hgap);
				int reserveMore = (k < n) ? moreW : 0;
				if (prefixW + reserveMore <= avail)
				{
					best = k;
					break;
				}
			}
			inlinePanel.removeAll();
			overflowMenu.removeAll();
			for (int i = 0; i < best; i++)
			{
				inlinePanel.add(chain.get(i));
			}
			if (best < n)
			{
				for (int i = best; i < n; i++)
				{
					JComponent c = chain.get(i);
					if (c instanceof JCheckBox)
					{
						JCheckBox cb = (JCheckBox) c;
						JCheckBoxMenuItem mi = new JCheckBoxMenuItem(cb.getText(), cb.isSelected());
						String tt = cb.getToolTipText();
						if (tt != null && !tt.isBlank())
						{
							mi.setToolTipText(tt);
						}
						mi.addActionListener(ev ->
						{
							cb.setSelected(mi.isSelected());
							for (java.awt.event.ActionListener l : cb.getActionListeners())
							{
								l.actionPerformed(new java.awt.event.ActionEvent(cb, java.awt.event.ActionEvent.ACTION_PERFORMED, "overflow-toggle"));
							}
						});
						overflowMenu.add(mi);
						continue;
					}
					if (c instanceof JButton)
					{
						JButton b = (JButton) c;
						JMenuItem mi = new JMenuItem(b.getText());
						String tt = b.getToolTipText();
						if (tt != null && !tt.isBlank())
						{
							mi.setToolTipText(tt);
						}
						mi.addActionListener(ev ->
						{
							Object handler = b.getClientProperty("orderConditionsOverflowHandler");
							if (handler instanceof Consumer)
							{
								@SuppressWarnings("unchecked")
								Consumer<JButton> consumer = (Consumer<JButton>) handler;
								consumer.accept(moreButton);
							}
							else
							{
								b.doClick();
							}
						});
						overflowMenu.add(mi);
					}
				}
				inlinePanel.add(moreButton);
			}
			inlinePanel.revalidate();
			inlinePanel.repaint();
		}

		private static int prefixWidth(List<JComponent> chain, int from, int toExclusive, int hgap)
		{
			int w = 0;
			for (int i = from; i < toExclusive; i++)
			{
				if (i > from)
				{
					w += hgap;
				}
				w += chain.get(i).getPreferredSize().width;
			}
			return w;
		}
	}

	private void removeSelected()
	{
		DraftOrderStepRequirement sel = selectedDto();
		DefaultMutableTreeNode vn = selectedVisualNode();
		if (sel == null)
		{
			if (isRootMarker(vn))
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
				editVarbitLeaf(d);
				break;
			case "ORDER_ZONE":
				JOptionPane.showMessageDialog(this,
					"Zone corners and display text are edited on the Zone reqs tab for this quest-order row.",
					"Order zone",
					JOptionPane.INFORMATION_MESSAGE);
				break;
			case "ZONE":
				JOptionPane.showMessageDialog(this,
					"Zone corners and display text are stored directly on this condition node.",
					"Zone",
					JOptionPane.INFORMATION_MESSAGE);
				break;
			case "SKILL":
				editSkill(d);
				break;
			default:
				JOptionPane.showMessageDialog(this, "Nothing to edit for this node type.", "Edit", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void editSkill(DraftOrderStepRequirement d)
	{
		SkillPick pick = showSkillFormOrNull(
			d.getSkillName(),
			d.getSkillRequiredLevel(),
			d.getSkillOperation(),
			d.getSkillDisplayText(),
			d.isSkillCanBeBoosted());
		if (pick == null)
		{
			return;
		}
		d.setSkillName(pick.skill.name());
		d.setSkillRequiredLevel(Math.max(1, pick.requiredLevel));
		d.setSkillOperation(pick.operation.name());
		d.setSkillDisplayText(pick.displayText);
		d.setSkillCanBeBoosted(pick.canBeBoosted);
		rebuildTree();
		selectDto(d);
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
		JCheckBox checkBank = new JCheckBox("Also check bank", d.isItemAlsoCheckBank());
		JCheckBox equipped = new JCheckBox("Must be equipped", d.isItemMustBeEquipped());
		int existingQty = d.getItemQuantity() == null ? 1 : Math.max(1, d.getItemQuantity());
		JSpinner qty = new JSpinner(new SpinnerNumberModel(existingQty, 1, 100000, 1));
		JPanel qtyRow = new JPanel(new BorderLayout(6, 0));
		qtyRow.add(new JLabel("Quantity:"), BorderLayout.WEST);
		qtyRow.add(qty, BorderLayout.CENTER);
		JPanel panel = new JPanel(new BorderLayout(0, 6));
		panel.add(combo, BorderLayout.NORTH);
		JPanel south = new JPanel(new BorderLayout(0, 6));
		south.add(qtyRow, BorderLayout.NORTH);
		JPanel checks = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		checks.add(checkBank);
		checks.add(equipped);
		south.add(checks, BorderLayout.SOUTH);
		panel.add(south, BorderLayout.SOUTH);
		int r = JOptionPane.showConfirmDialog(this, panel, "Captured item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (r == JOptionPane.OK_OPTION && combo.getSelectedIndex() >= 0)
		{
			d.setItemRawId(choices.get(combo.getSelectedIndex()).getRawId());
			d.setItemQuantity(((Number) qty.getValue()).intValue());
			d.setItemAlsoCheckBank(checkBank.isSelected());
			d.setItemMustBeEquipped(equipped.isSelected());
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
			for (DraftOrderStepRequirement leaf : collectSkillLeaves(rootDto))
			{
				manager.ensureSkillRequirementForReuse(
					leaf.getSkillName(),
					leaf.getSkillRequiredLevel() == null ? 1 : leaf.getSkillRequiredLevel(),
					leaf.isSkillCanBeBoosted(),
					leaf.getSkillDisplayText(),
					leaf.getSkillOperation());
			}
		}
		if (pendingZoneRouting && OrderStepRequirementSupport.treeContainsOrderZoneLeaf(rootDto))
		{
			if (!manager.applyZoneRoutingToOrderLineByIndex(
				orderIndex,
				pendingZoneC1,
				pendingZoneC2,
				pendingZoneDisplayText,
				false))
			{
				JOptionPane.showMessageDialog(this,
					"Could not apply zone routing to this order row.",
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
		OrderConditionMode mode = (OrderConditionMode) modeCombo.getSelectedItem();
		String modeErr = manager.applyOrderStepRequirementMode(orderIndex, mode);
		if (modeErr != null)
		{
			JOptionPane.showMessageDialog(this, modeErr, "Could not save", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!sectionDividerRow)
		{
			String passOnceErr = manager.applyOrderStepPassOnceCompletedOnce(orderIndex, passOnceCompletedCheck.isSelected());
			if (passOnceErr != null)
			{
				JOptionPane.showMessageDialog(this, passOnceErr, "Could not save", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		accepted = true;
		dispose();
	}

	private void updateSemanticsToolTip()
	{
		OrderConditionMode mode = (OrderConditionMode) modeCombo.getSelectedItem();
		if (mode == OrderConditionMode.CONTINUE_WHEN_TRUE)
		{
			modeCombo.setToolTipText("Continue when true: condition true means this step is complete; show it while false.");
		}
		else
		{
			modeCombo.setToolTipText("Show when true: condition true means this step is active.");
		}
	}

	private static List<DraftOrderStepRequirement> collectSkillLeaves(DraftOrderStepRequirement root)
	{
		List<DraftOrderStepRequirement> out = new ArrayList<>();
		collectSkillLeavesRecursive(root, out);
		return out;
	}

	private static void collectSkillLeavesRecursive(DraftOrderStepRequirement node, List<DraftOrderStepRequirement> out)
	{
		if (node == null)
		{
			return;
		}
		String kind = node.getKind() == null ? "" : node.getKind().trim().toUpperCase(Locale.ROOT);
		if ("SKILL".equals(kind))
		{
			out.add(node);
			return;
		}
		if (node.getChildren() != null)
		{
			for (DraftOrderStepRequirement child : node.getChildren())
			{
				collectSkillLeavesRecursive(child, out);
			}
		}
	}

	private static final class ZonePick
	{
		final WorldPoint corner1;
		final WorldPoint corner2;
		final String displayText;

		ZonePick(WorldPoint corner1, WorldPoint corner2, String displayText)
		{
			this.corner1 = corner1;
			this.corner2 = corner2;
			this.displayText = displayText;
		}
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

	private static final class SkillPick
	{
		final Skill skill;
		final int requiredLevel;
		final Operation operation;
		final String displayText;
		final boolean canBeBoosted;

		SkillPick(Skill skill, int requiredLevel, Operation operation, String displayText, boolean canBeBoosted)
		{
			this.skill = skill;
			this.requiredLevel = requiredLevel;
			this.operation = operation;
			this.displayText = displayText;
			this.canBeBoosted = canBeBoosted;
		}
	}

	private String formatPendingZoneSummary()
	{
		if (pendingZoneC1 == null || pendingZoneC2 == null)
		{
			return "Zone (pending)";
		}
		String pts = "(" + pendingZoneC1.getX() + "," + pendingZoneC1.getY() + "," + pendingZoneC1.getPlane() + ")–("
			+ pendingZoneC2.getX() + "," + pendingZoneC2.getY() + "," + pendingZoneC2.getPlane() + ")";
		if (pendingZoneDisplayText != null && !pendingZoneDisplayText.isBlank())
		{
			return pendingZoneDisplayText + " — " + pts;
		}
		return "Zone " + pts;
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
				int qty = d.getItemQuantity() == null ? 1 : Math.max(1, d.getItemQuantity());
				return "Item raw id " + d.getItemRawId() + " x" + qty
					+ (d.isItemAlsoCheckBank() ? " [bank]" : "")
					+ (d.isItemMustBeEquipped() ? " [equipped]" : "");
			case "ORDER_VARBIT":
			case "ROUTING_VARBIT":
				return manager.formatOrderVarbitLeafSummaryForEditor(orderIndex);
			case "ORDER_ZONE":
				if (pendingZoneRouting)
				{
					return formatPendingZoneSummary();
				}
				return manager.formatOrderZoneLeafSummaryForEditor(orderIndex);
			case "VARBIT":
			case "INLINE_VARBIT":
				return "Varbit " + d.getVarbitId() + " " + (d.getVarbitOperation() == null ? "" : d.getVarbitOperation()) + " " + d.getVarbitRequiredValue();
			case "ZONE":
			{
				WorldPoint c1 = d.getZoneCorner1();
				WorldPoint c2 = d.getZoneCorner2();
				String base;
				if (c1 == null || c2 == null)
				{
					base = "Zone";
				}
				else
				{
					base = String.format("Zone (%d, %d, %d) -> (%d, %d, %d)",
						c1.getX(), c1.getY(), c1.getPlane(),
						c2.getX(), c2.getY(), c2.getPlane());
				}
				String disp = d.getZoneDisplayText();
				return disp == null || disp.isBlank() ? base : disp + " — " + base;
			}
			case "SKILL":
				return formatSkillChoiceLabel(
					d.getSkillName(),
					d.getSkillRequiredLevel() == null ? 1 : d.getSkillRequiredLevel(),
					d.getSkillOperation(),
					d.getSkillDisplayText(),
					d.isSkillCanBeBoosted());
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
