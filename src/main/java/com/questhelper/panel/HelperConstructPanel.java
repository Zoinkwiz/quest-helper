package com.questhelper.panel;

import com.questhelper.managers.HelperConstructManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;

public class HelperConstructPanel extends PluginPanel
{
	private final HelperConstructManager helperConstructManager;
	private final JPanel npcStepsList = new JPanel();
	private final JPanel objectStepsList = new JPanel();
	private final JPanel requirementsList = new JPanel();
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

		var title = JGenerator.makeJTextArea("Quest Helper Construct");
		title.setForeground(Color.WHITE);
		root.add(title);
		root.add(Box.createVerticalStrut(8));
		JPanel buttonRow = new JPanel(new GridLayout(2, 3, 6, 6));
		buttonRow.setBackground(ColorScheme.DARK_GRAY_COLOR);
		buttonRow.setMaximumSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 58));
		JButton buildButton = new JButton("Build");
		JButton resetButton = new JButton("Reset");
		JButton mapButton = new JButton("Map");
		JButton worldMapRouteButton = new JButton("WorldMap Route");
		JButton viewModeButton = new JButton("Order View");
		SwingUtil.removeButtonDecorations(buildButton);
		SwingUtil.removeButtonDecorations(resetButton);
		SwingUtil.removeButtonDecorations(mapButton);
		SwingUtil.removeButtonDecorations(worldMapRouteButton);
		SwingUtil.removeButtonDecorations(viewModeButton);
		buildButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		resetButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		mapButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		worldMapRouteButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		viewModeButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		buildButton.setForeground(Color.WHITE);
		resetButton.setForeground(Color.WHITE);
		mapButton.setForeground(Color.WHITE);
		worldMapRouteButton.setForeground(Color.WHITE);
		viewModeButton.setForeground(Color.WHITE);
		buildButton.addActionListener(e -> helperConstructManager.buildToClipboardFromUi());
		resetButton.addActionListener(e ->
		{
			helperConstructManager.resetDraftAndClearSavedStateFromUi();
			refresh();
		});
		mapButton.addActionListener(e -> helperConstructManager.buildRouteMapImageFromUi());
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
		buttonRow.add(worldMapRouteButton);
		buttonRow.add(viewModeButton);
		buttonRow.add(new JLabel());
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

		var borderNpcSteps = new JPanel(new BorderLayout());
		borderNpcSteps.setBackground(ColorScheme.DARK_GRAY_COLOR);
		npcStepsList.setLayout(new BoxLayout(npcStepsList, BoxLayout.Y_AXIS));
		npcStepsList.setBorder(new EmptyBorder(10, 5, 10, 5));
		borderNpcSteps.add(npcStepsList);

		var borderObjectSteps = new JPanel(new BorderLayout());
		objectStepsList.setLayout(new BoxLayout(objectStepsList, BoxLayout.Y_AXIS));
		objectStepsList.setBorder(new EmptyBorder(10, 5, 10, 5));
		borderObjectSteps.add(objectStepsList);

		var borderRequirements = new JPanel(new BorderLayout());
		borderRequirements.setBackground(ColorScheme.DARK_GRAY_COLOR);
		requirementsList.setLayout(new BoxLayout(requirementsList, BoxLayout.Y_AXIS));
		requirementsList.setBorder(new EmptyBorder(10, 5, 10, 5));
		borderRequirements.add(requirementsList);

		sectioned.add(sectionLabel("NPC Steps"));
		sectioned.add(borderNpcSteps);
		sectioned.add(Box.createVerticalStrut(10));
		sectioned.add(sectionLabel("Object Steps"));
		sectioned.add(borderObjectSteps);
		sectioned.add(Box.createVerticalStrut(10));
		sectioned.add(sectionLabel("Captured Requirements"));
		sectioned.add(borderRequirements);
		return sectioned;
	}

	private JPanel buildOrderedView()
	{
		JPanel ordered = new JPanel(new BorderLayout(0, 6));
		ordered.setBackground(ColorScheme.DARK_GRAY_COLOR);
		ordered.add(sectionLabel("Ordered Steps + Section Dividers (drag rows to reorder)"), BorderLayout.NORTH);

		JTable table = new JTable(stepOrderTableModel);
		table.setDragEnabled(true);
		table.setDropMode(DropMode.INSERT_ROWS);
		table.setFillsViewportHeight(true);
		table.setRowHeight(24);
		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
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
		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(1).setPreferredWidth(240);
		table.setTransferHandler(new StepReorderTransferHandler(table));

		JScrollPane tableScroll = new JScrollPane(table);
		tableScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		ordered.add(tableScroll, BorderLayout.CENTER);

		JButton removeSelectedButton = new JButton("Remove Selected");
		JButton addSectionButton = new JButton("Add Section");
		SwingUtil.removeButtonDecorations(removeSelectedButton);
		SwingUtil.removeButtonDecorations(addSectionButton);
		removeSelectedButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		addSectionButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		removeSelectedButton.setForeground(Color.WHITE);
		addSectionButton.setForeground(Color.WHITE);
		removeSelectedButton.addActionListener(e ->
		{
			int selected = table.getSelectedRow();
			if (selected >= 0)
			{
				helperConstructManager.removeStepAt(selected);
				refresh();
			}
		});
		addSectionButton.addActionListener(e ->
		{
			helperConstructManager.addSectionDivider();
			refresh();
		});
		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		actions.setBackground(ColorScheme.DARK_GRAY_COLOR);
		actions.add(addSectionButton);
		actions.add(Box.createHorizontalStrut(6));
		actions.add(removeSelectedButton);
		ordered.add(actions, BorderLayout.SOUTH);
		return ordered;
	}

	public void refresh()
	{
		int scrollValue = scrollPane.getVerticalScrollBar().getValue();
		rebuildNpcStepRows();
		rebuildObjectStepRows();
		rebuildRequirementRows();
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

	private void rebuildNpcStepRows()
	{
		npcStepsList.removeAll();
		var rows = helperConstructManager.getNpcStepSummaries();
		if (rows.isEmpty())
		{
			npcStepsList.add(emptyLabel("No NPC steps captured."));
			return;
		}

		for (int i = 0; i < rows.size(); i++)
		{
			JPanel row = makeStepRow(rows.get(i), i, StepCategory.NPC);
			row.setAlignmentX(Component.LEFT_ALIGNMENT);
			row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
			npcStepsList.add(row);
		}
	}

	private void rebuildObjectStepRows()
	{
		objectStepsList.removeAll();
		var rows = helperConstructManager.getObjectStepSummaries();
		if (rows.isEmpty())
		{
			objectStepsList.add(emptyLabel("No object steps captured."));
			return;
		}

		for (int i = 0; i < rows.size(); i++)
		{
			JPanel row = makeStepRow(rows.get(i), i, StepCategory.OBJECT);
			row.setAlignmentX(Component.LEFT_ALIGNMENT);
			row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
			objectStepsList.add(row);
		}
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

	private void rebuildRequirementRows()
	{
		requirementsList.removeAll();
		var rows = helperConstructManager.getRequirementSummaries();
		if (rows.isEmpty())
		{
			requirementsList.add(emptyLabel("No requirements captured."));
			return;
		}

		for (int i = 0; i < rows.size(); i++)
		{
			JPanel row = makeRequirementRow(rows.get(i), i);
			row.setAlignmentX(Component.LEFT_ALIGNMENT);
			row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
			requirementsList.add(row);
		}
	}

	private JPanel makeStepRow(String text, int index, StepCategory category)
	{
		JPanel row = new FixedWidthPanel();
		row.setLayout(new BorderLayout(0, 4));
		row.setBackground(ColorScheme.DARK_GRAY_COLOR);
		row.setBorder(new EmptyBorder(4, 0, 4, 0));
		row.setAlignmentX(Component.LEFT_ALIGNMENT);

		var textArea = JGenerator.makeJTextArea(text);
		textArea.setForeground(Color.WHITE);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		textArea.setFocusable(false);
		textArea.setBackground(ColorScheme.DARK_GRAY_COLOR);
		textArea.setBorder(new EmptyBorder(0, 0, 0, 0));
		row.add(textArea, BorderLayout.CENTER);

		JButton remove = new JButton("Remove");
		SwingUtil.removeButtonDecorations(remove);
		remove.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		remove.setForeground(Color.WHITE);
		remove.setPreferredSize(new Dimension(68, 22));
		remove.addActionListener(e ->
		{
			if (category == StepCategory.NPC)
			{
				helperConstructManager.removeNpcStepAt(index);
			}
			else
			{
				helperConstructManager.removeObjectStepAt(index);
			}
			refresh();
		});
		JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		actionRow.setBackground(ColorScheme.DARK_GRAY_COLOR);
		actionRow.add(remove);
		row.add(actionRow, BorderLayout.SOUTH);
		return row;
	}

	private JPanel makeRequirementRow(String text, int index)
	{
		JPanel row = new FixedWidthPanel();
		row.setLayout(new BorderLayout(0, 4));
		row.setBackground(ColorScheme.DARK_GRAY_COLOR);
		row.setBorder(new EmptyBorder(4, 0, 4, 0));
		row.setAlignmentX(Component.LEFT_ALIGNMENT);

		var textArea = JGenerator.makeJTextArea(text);
		textArea.setForeground(Color.WHITE);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		textArea.setFocusable(false);
		textArea.setBackground(ColorScheme.DARK_GRAY_COLOR);
		textArea.setBorder(new EmptyBorder(0, 0, 0, 0));
		row.add(textArea, BorderLayout.CENTER);

		JButton remove = new JButton("Remove");
		SwingUtil.removeButtonDecorations(remove);
		remove.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		remove.setForeground(Color.WHITE);
		remove.setPreferredSize(new Dimension(68, 22));
		remove.addActionListener(e ->
		{
			helperConstructManager.removeRequirementAt(index);
			refresh();
		});
		JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		actionRow.setBackground(ColorScheme.DARK_GRAY_COLOR);
		actionRow.add(remove);
		row.add(actionRow, BorderLayout.SOUTH);
		return row;
	}

	private JTextArea sectionLabel(String text)
	{
		var label = JGenerator.makeJTextArea(text);
		label.setForeground(Color.GRAY);
		label.setLineWrap(true);
		label.setWrapStyleWord(true);
		return label;
	}

	private JTextArea emptyLabel(String text)
	{
		var label = JGenerator.makeJTextArea(text);
		label.setForeground(Color.GRAY);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		label.setLineWrap(true);
		label.setWrapStyleWord(true);
		return label;
	}

	private enum StepCategory
	{
		NPC,
		OBJECT
	}

	private final class StepOrderTableModel extends AbstractTableModel
	{
		private final String[] columns = {"Name/Var", "Detail"};
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
			return row.isSectionDivider() ? (row.getSectionCondition() == null ? "" : row.getSectionCondition()) : row.getSummary();
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			var row = rows.get(rowIndex);
			if (columnIndex == 0)
			{
				return true;
			}
			if (columnIndex == 1)
			{
				return row.isSectionDivider();
			}
			return false;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			if (columnIndex != 0)
			{
				if (columnIndex == 1)
				{
					helperConstructManager.updateSectionCondition(rowIndex, aValue == null ? "" : String.valueOf(aValue));
					setRows(helperConstructManager.getCombinedStepRows());
				}
				return;
			}
			helperConstructManager.updateStepVarName(rowIndex, aValue == null ? "" : String.valueOf(aValue));
			setRows(helperConstructManager.getCombinedStepRows());
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
				parts.add(row.getVarName() + "|" + row.getSummary() + "|" + row.getSectionCondition());
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
