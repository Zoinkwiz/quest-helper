package com.questhelper.panel;

import com.questhelper.managers.HelperConstructManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HelperConstructPanel extends PluginPanel
{
	private final HelperConstructManager helperConstructManager;
	private final LibraryTableModel npcLibraryModel = new LibraryTableModel();
	private final LibraryTableModel objectLibraryModel = new LibraryTableModel();
	private final LibraryTableModel requirementLibraryModel = new LibraryTableModel();
	private JTable orderTable;
	private final CardLayout stepsViewLayout = new CardLayout();
	private final JPanel stepsViewContainer = new JPanel(stepsViewLayout);
	private final StepOrderTableModel stepOrderTableModel = new StepOrderTableModel();
	private boolean showingOrderedView = false;
	private final Timer refreshTimer;
	private final JScrollPane scrollPane;
	private String lastRenderSignature = "";

	public HelperConstructPanel(HelperConstructManager helperConstructManager)
	{
		super(false);
		this.helperConstructManager = helperConstructManager;

		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setLayout(new BorderLayout());

		FixedWidthPanel root = new FixedWidthPanel();
		root.setBackground(ColorScheme.DARK_GRAY_COLOR);
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		root.setBorder(new EmptyBorder(8, 8, 8, 8));
		root.setAlignmentX(Component.LEFT_ALIGNMENT);

		var title = JGenerator.makeJTextArea("Quest Helper Maker");
		title.setForeground(Color.WHITE);
		root.add(title);
		root.add(Box.createVerticalStrut(6));
		JPanel buttonRow = new JPanel(new GridLayout(2, 3, 6, 6));
		buttonRow.setBackground(ColorScheme.DARK_GRAY_COLOR);
		buttonRow.setMaximumSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 58));
		JButton buildButton = new JButton("Build");
		JButton resetButton = new JButton("Reset");
		JButton mapButton = new JButton("Map");
		JButton previewButton = new JButton("Preview");
		JButton worldMapRouteButton = new JButton("WorldMap Route");
		JButton viewModeButton = new JButton("Order View");
		SwingUtil.removeButtonDecorations(buildButton);
		SwingUtil.removeButtonDecorations(resetButton);
		SwingUtil.removeButtonDecorations(mapButton);
		SwingUtil.removeButtonDecorations(previewButton);
		SwingUtil.removeButtonDecorations(worldMapRouteButton);
		SwingUtil.removeButtonDecorations(viewModeButton);
		buildButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		resetButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		mapButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		previewButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		worldMapRouteButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		viewModeButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		buildButton.setForeground(Color.WHITE);
		resetButton.setForeground(Color.WHITE);
		mapButton.setForeground(Color.WHITE);
		previewButton.setForeground(Color.WHITE);
		worldMapRouteButton.setForeground(Color.WHITE);
		viewModeButton.setForeground(Color.WHITE);
		buildButton.addActionListener(e -> helperConstructManager.buildToClipboardFromUi());
		resetButton.addActionListener(e ->
		{
			helperConstructManager.resetDraftAndClearSavedStateFromUi();
			refresh();
		});
		mapButton.addActionListener(e -> helperConstructManager.buildRouteMapImageFromUi());
		previewButton.addActionListener(e -> helperConstructManager.previewInSidebarFromUi());
		worldMapRouteButton.addActionListener(e ->
		{
			helperConstructManager.toggleWorldMapRoutePreviewFromUi();
			worldMapRouteButton.setText(helperConstructManager.isWorldMapRoutePreviewEnabled() ? "WorldMap Off" : "WorldMap Route");
		});
		viewModeButton.addActionListener(e ->
		{
			showingOrderedView = !showingOrderedView;
			stepsViewLayout.show(stepsViewContainer, showingOrderedView ? "ordered" : "sectioned");
			viewModeButton.setText(showingOrderedView ? "Section View" : "Order View");
			refresh();
		});
		buttonRow.add(buildButton);
		buttonRow.add(resetButton);
		buttonRow.add(mapButton);
		buttonRow.add(previewButton);
		buttonRow.add(worldMapRouteButton);
		buttonRow.add(viewModeButton);
		root.add(buttonRow);
		root.add(Box.createVerticalStrut(10));

		JPanel sectionedView = buildSectionedView();
		JPanel orderedView = buildOrderedView();
		stepsViewContainer.setBackground(ColorScheme.DARK_GRAY_COLOR);
		stepsViewContainer.add(sectionedView, "sectioned");
		stepsViewContainer.add(orderedView, "ordered");
		stepsViewLayout.show(stepsViewContainer, "sectioned");
		root.add(stepsViewContainer);

		scrollPane = new JScrollPane(root);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		add(scrollPane, BorderLayout.CENTER);

		refresh();

		// Keep panel list in sync with right-click captures made outside this panel.
		refreshTimer = new Timer(1000, e -> refreshIfChanged());
		refreshTimer.setRepeats(true);
		refreshTimer.start();
	}

	private JPanel buildSectionedView()
	{
		JPanel sectioned = new JPanel();
		sectioned.setLayout(new BoxLayout(sectioned, BoxLayout.Y_AXIS));
		sectioned.setBackground(ColorScheme.DARK_GRAY_COLOR);

		sectioned.add(sectionLabel("NPC Steps (library)"));
		sectioned.add(wrapLibraryTable(new JTable(npcLibraryModel), LibraryRemoveKind.NPC));
		sectioned.add(Box.createVerticalStrut(10));
		sectioned.add(sectionLabel("Object Steps (library)"));
		sectioned.add(wrapLibraryTable(new JTable(objectLibraryModel), LibraryRemoveKind.OBJECT));
		sectioned.add(Box.createVerticalStrut(10));
		sectioned.add(sectionLabel("Captured Requirements"));
		sectioned.add(wrapLibraryTable(new JTable(requirementLibraryModel), LibraryRemoveKind.REQUIREMENT));
		return sectioned;
	}

	private JScrollPane wrapLibraryTable(JTable table, LibraryRemoveKind removeKind)
	{
		styleLibraryTable(table);
		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				int row = table.rowAtPoint(e.getPoint());
				int col = table.columnAtPoint(e.getPoint());
				if (row < 0 || col != 1)
				{
					return;
				}
				switch (removeKind)
				{
					case NPC:
						helperConstructManager.removeNpcStepAt(row);
						break;
					case OBJECT:
						helperConstructManager.removeObjectStepAt(row);
						break;
					case REQUIREMENT:
						helperConstructManager.removeRequirementAt(row);
						break;
				}
				refresh();
			}
		});
		JScrollPane sp = new JScrollPane(table);
		sp.setBorder(new EmptyBorder(0, 0, 0, 0));
		return sp;
	}

	private void styleLibraryTable(JTable table)
	{
		table.setFillsViewportHeight(true);
		table.setRowHeight(24);
		table.setShowGrid(false);
		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
				if (!isSelected)
				{
					c.setBackground(ColorScheme.DARK_GRAY_COLOR);
					c.setForeground(column == 1 ? new Color(100, 180, 255) : Color.WHITE);
				}
				return c;
			}
		});
		if (table.getColumnModel().getColumnCount() >= 2)
		{
			table.getColumnModel().getColumn(0).setPreferredWidth(320);
			table.getColumnModel().getColumn(1).setPreferredWidth(72);
		}
	}

	private enum LibraryRemoveKind
	{
		NPC,
		OBJECT,
		REQUIREMENT
	}

	private static final class LibraryTableModel extends AbstractTableModel
	{
		private final String[] columns = {"Captured", "Remove"};
		private List<String> rows = new ArrayList<>();

		void setRows(List<String> updatedRows)
		{
			rows = new ArrayList<>(updatedRows);
			fireTableDataChanged();
		}

		@Override
		public int getRowCount()
		{
			return rows.size();
		}

		@Override
		public int getColumnCount()
		{
			return columns.length;
		}

		@Override
		public String getColumnName(int column)
		{
			return columns[column];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if (columnIndex == 0)
			{
				return rows.get(rowIndex);
			}
			return rows.isEmpty() ? "" : "Remove";
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return false;
		}
	}

	private JPanel buildOrderedView()
	{
		JPanel ordered = new JPanel(new BorderLayout(0, 6));
		ordered.setBackground(ColorScheme.DARK_GRAY_COLOR);
		ordered.add(sectionLabel("Quest order (sections + step references)"), BorderLayout.NORTH);

		orderTable = new JTable(stepOrderTableModel)
		{
			@Override
			public TableCellEditor getCellEditor(int row, int column)
			{
				if (column == 1)
				{
					var entry = stepOrderTableModel.getRow(row);
					if (entry != null && entry.isSectionDivider())
					{
						return new DefaultCellEditor(new JTextField());
					}
					return buildRequirementComboEditor(row);
				}
				return super.getCellEditor(row, column);
			}
		};
		orderTable.setDragEnabled(true);
		orderTable.setDropMode(DropMode.INSERT_ROWS);
		orderTable.setFillsViewportHeight(true);
		orderTable.setRowHeight(24);
		orderTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
				var entry = stepOrderTableModel.getRow(row);
				if (entry != null && entry.isSectionDivider())
				{
					if (!isSelected)
					{
						c.setBackground(new Color(46, 52, 64));
						c.setForeground(new Color(143, 188, 187));
					}
					setFont(getFont().deriveFont(Font.BOLD));
				}
				else
				{
					if (!isSelected)
					{
						c.setBackground(ColorScheme.DARK_GRAY_COLOR);
						c.setForeground(Color.WHITE);
					}
					setFont(getFont().deriveFont(Font.PLAIN));
				}
				return c;
			}
		});
		orderTable.getColumnModel().getColumn(0).setPreferredWidth(150);
		orderTable.getColumnModel().getColumn(1).setPreferredWidth(240);
		orderTable.setTransferHandler(new StepReorderTransferHandler(orderTable));

		JScrollPane tableScroll = new JScrollPane(orderTable);
		tableScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		ordered.add(tableScroll, BorderLayout.CENTER);

		JButton removeSelectedButton = new JButton("Remove Selected");
		JButton addStepButton = new JButton("Add Step");
		JButton addSectionButton = new JButton("Add Section");
		SwingUtil.removeButtonDecorations(removeSelectedButton);
		SwingUtil.removeButtonDecorations(addStepButton);
		SwingUtil.removeButtonDecorations(addSectionButton);
		removeSelectedButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		addStepButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		addSectionButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		removeSelectedButton.setForeground(Color.WHITE);
		addStepButton.setForeground(Color.WHITE);
		addSectionButton.setForeground(Color.WHITE);
		removeSelectedButton.addActionListener(e ->
		{
			int selected = orderTable.getSelectedRow();
			if (selected >= 0)
			{
				helperConstructManager.removeStepAt(selected);
				refresh();
			}
		});
		addStepButton.addActionListener(e -> showAddStepDialog());
		addSectionButton.addActionListener(e ->
		{
			helperConstructManager.addSectionDivider();
			refresh();
		});
		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		actions.setBackground(ColorScheme.DARK_GRAY_COLOR);
		actions.add(addStepButton);
		actions.add(Box.createHorizontalStrut(6));
		actions.add(addSectionButton);
		actions.add(Box.createHorizontalStrut(6));
		actions.add(removeSelectedButton);
		ordered.add(actions, BorderLayout.SOUTH);
		return ordered;
	}

	private void showAddStepDialog()
	{
		var options = helperConstructManager.getStepDefinitionPickOptions();
		if (options.isEmpty())
		{
			JOptionPane.showMessageDialog(this,
				"Capture NPC, object, or item steps in Section View first, then add them to the quest order here.",
				"Add Step",
				JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		JList<HelperConstructManager.StepDefinitionPickOption> list = new JList<>(options.toArray(new HelperConstructManager.StepDefinitionPickOption[0]));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		if (!options.isEmpty())
		{
			list.setSelectedIndex(0);
		}
		JScrollPane sp = new JScrollPane(list);
		sp.setPreferredSize(new Dimension(420, 240));
		int r = JOptionPane.showConfirmDialog(this, sp, "Add Step to order", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (r == JOptionPane.OK_OPTION && list.getSelectedValue() != null)
		{
			helperConstructManager.addOrderRef(list.getSelectedValue().getStepId());
			refresh();
		}
	}

	private TableCellEditor buildRequirementComboEditor(int row)
	{
		JComboBox<ReqChoice> combo = new JComboBox<>();
		combo.addItem(new ReqChoice("Default (from step)", null));
		combo.addItem(new ReqChoice("Varbit only", HelperConstructManager.ORDER_REQUIREMENT_VARBIT_ONLY));
		List<Integer> ids = helperConstructManager.getRequirementRawIds();
		List<String> labels = helperConstructManager.getRequirementSummaries();
		for (int i = 0; i < ids.size(); i++)
		{
			String lab = i < labels.size() ? labels.get(i) : String.valueOf(ids.get(i));
			combo.addItem(new ReqChoice(lab, ids.get(i)));
		}
		var entry = stepOrderTableModel.getRow(row);
		Integer cur = entry == null ? null : entry.getOrderLinkedRequirementRawId();
		for (int i = 0; i < combo.getItemCount(); i++)
		{
			if (Objects.equals(combo.getItemAt(i).value, cur))
			{
				combo.setSelectedIndex(i);
				break;
			}
		}
		return new DefaultCellEditor(combo);
	}

	private static final class ReqChoice
	{
		private final String label;
		private final Integer value;

		private ReqChoice(String label, Integer value)
		{
			this.label = label;
			this.value = value;
		}

		@Override
		public String toString()
		{
			return label;
		}
	}

	public void refresh()
	{
		int scrollValue = scrollPane.getVerticalScrollBar().getValue();
		npcLibraryModel.setRows(helperConstructManager.getNpcStepSummaries());
		objectLibraryModel.setRows(helperConstructManager.getObjectStepSummaries());
		requirementLibraryModel.setRows(helperConstructManager.getRequirementSummaries());
		stepOrderTableModel.setRows(helperConstructManager.getCombinedStepRows());
		lastRenderSignature = computeSignature();
		revalidate();
		repaint();
		SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(scrollValue));
	}

	public void shutDown()
	{
		refreshTimer.stop();
		helperConstructManager.disableWorldMapRoutePreview();
	}

	private void refreshIfChanged()
	{
		String currentSignature = computeSignature();
		if (!currentSignature.equals(lastRenderSignature))
		{
			refresh();
		}
	}

	private String computeSignature()
	{
		return helperConstructManager.getCurrentDraftClassName()
			+ "|N|" + String.join("\n", helperConstructManager.getNpcStepSummaries())
			+ "|O|" + String.join("\n", helperConstructManager.getObjectStepSummaries())
			+ "|A|" + stepOrderTableModel.signature()
			+ "|R|" + String.join("\n", helperConstructManager.getRequirementSummaries());
	}

	private JTextArea sectionLabel(String text)
	{
		var label = JGenerator.makeJTextArea(text);
		label.setForeground(Color.GRAY);
		label.setLineWrap(true);
		label.setWrapStyleWord(true);
		return label;
	}

	private final class StepOrderTableModel extends AbstractTableModel
	{
		private final String[] columns = {"Name/Var", "Requirement / condition"};
		private List<HelperConstructManager.CombinedStepRow> rows = new ArrayList<>();

		@Override
		public int getRowCount()
		{
			return rows.size();
		}

		@Override
		public int getColumnCount()
		{
			return columns.length;
		}

		@Override
		public String getColumnName(int column)
		{
			return columns[column];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			var row = rows.get(rowIndex);
			if (columnIndex == 0)
			{
				return row.getVarName();
			}
			if (row.isSectionDivider())
			{
				return row.getSectionCondition() == null ? "" : row.getSectionCondition();
			}
			return formatRequirementChoiceLabel(row);
		}

		private String formatRequirementChoiceLabel(HelperConstructManager.CombinedStepRow row)
		{
			Integer v = row.getOrderLinkedRequirementRawId();
			if (v == null)
			{
				return "Default (from step)";
			}
			if (v == HelperConstructManager.ORDER_REQUIREMENT_VARBIT_ONLY)
			{
				return "Varbit only";
			}
			List<Integer> ids = helperConstructManager.getRequirementRawIds();
			List<String> labels = helperConstructManager.getRequirementSummaries();
			for (int i = 0; i < ids.size(); i++)
			{
				if (ids.get(i).equals(v))
				{
					return i < labels.size() ? labels.get(i) : String.valueOf(v);
				}
			}
			return "Requirement id " + v;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			var row = rows.get(rowIndex);
			if (columnIndex == 0)
			{
				return true;
			}
			return columnIndex == 1;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			var row = rows.get(rowIndex);
			if (columnIndex == 0)
			{
				helperConstructManager.updateStepVarName(rowIndex, aValue == null ? "" : String.valueOf(aValue));
				setRows(helperConstructManager.getCombinedStepRows());
				return;
			}
			if (columnIndex == 1)
			{
				if (row.isSectionDivider())
				{
					helperConstructManager.updateSectionCondition(rowIndex, aValue == null ? "" : String.valueOf(aValue));
				}
				else if (aValue instanceof ReqChoice)
				{
					helperConstructManager.updateOrderLinkedRequirement(rowIndex, ((ReqChoice) aValue).value);
				}
				setRows(helperConstructManager.getCombinedStepRows());
			}
		}

		void setRows(List<HelperConstructManager.CombinedStepRow> updatedRows)
		{
			rows = new ArrayList<>(updatedRows);
			fireTableDataChanged();
		}

		HelperConstructManager.CombinedStepRow getRow(int index)
		{
			if (index < 0 || index >= rows.size())
			{
				return null;
			}
			return rows.get(index);
		}

		String signature()
		{
			List<String> parts = new ArrayList<>();
			for (HelperConstructManager.CombinedStepRow row : rows)
			{
				parts.add(row.getVarName() + "|" + row.getSummary() + "|" + row.getSectionCondition() + "|" + row.getOrderLinkedRequirementRawId());
			}
			return String.join("\n", parts);
		}
	}

	private final class StepReorderTransferHandler extends TransferHandler
	{
		private final JTable table;
		private int fromRow = -1;

		private StepReorderTransferHandler(JTable table)
		{
			this.table = table;
		}

		@Override
		protected Transferable createTransferable(JComponent c)
		{
			fromRow = table.getSelectedRow();
			return new StringSelection(String.valueOf(fromRow));
		}

		@Override
		public int getSourceActions(JComponent c)
		{
			return MOVE;
		}

		@Override
		public boolean canImport(TransferSupport support)
		{
			return support.isDrop();
		}

		@Override
		public boolean importData(TransferSupport support)
		{
			if (!canImport(support))
			{
				return false;
			}

			JTable.DropLocation dropLocation = (JTable.DropLocation) support.getDropLocation();
			int toRow = dropLocation.getRow();
			if (fromRow < 0 || toRow < 0)
			{
				return false;
			}

			int targetIndex = toRow;
			if (targetIndex > fromRow)
			{
				targetIndex--;
			}
			if (targetIndex < 0)
			{
				targetIndex = 0;
			}
			if (targetIndex >= stepOrderTableModel.getRowCount())
			{
				targetIndex = stepOrderTableModel.getRowCount() - 1;
			}

			boolean moved = helperConstructManager.moveStep(fromRow, targetIndex);
			if (moved)
			{
				setRowsAfterReorder();
			}
			return moved;
		}

		private void setRowsAfterReorder()
		{
			stepOrderTableModel.setRows(helperConstructManager.getCombinedStepRows());
			lastRenderSignature = computeSignature();
		}
	}
}
