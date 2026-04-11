package com.questhelper.panel;

import com.questhelper.managers.HelperConstructManager;
import net.runelite.client.ui.ColorScheme;
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
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class HelperConstructEditorPanel extends JPanel
{
	private final HelperConstructManager helperConstructManager;
	private final StepLibraryTableModel npcLibraryModel = new StepLibraryTableModel(true);
	private final StepLibraryTableModel objectLibraryModel = new StepLibraryTableModel(false);
	private final LibraryTableModel requirementLibraryModel = new LibraryTableModel();
	private JTable orderTable;
	private final CardLayout stepsViewLayout = new CardLayout();
	private final JPanel stepsViewContainer = new JPanel(stepsViewLayout);
	private final StepOrderTableModel stepOrderTableModel = new StepOrderTableModel();
	private boolean showingOrderedView = false;
	private final Timer refreshTimer;
	private final JScrollPane scrollPane;
	private String lastRenderSignature = "";

	public HelperConstructEditorPanel(HelperConstructManager helperConstructManager)
	{
		this.helperConstructManager = helperConstructManager;

		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setLayout(new BorderLayout());

		JPanel root = new JPanel();
		root.setBackground(ColorScheme.DARK_GRAY_COLOR);
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		root.setBorder(new EmptyBorder(8, 8, 8, 8));
		root.setAlignmentX(Component.LEFT_ALIGNMENT);

		var title = JGenerator.makeJTextArea("Quest Helper Maker");
		title.setForeground(Color.WHITE);
		root.add(title);
		root.add(Box.createVerticalStrut(6));
		JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
		buttonRow.setBackground(ColorScheme.DARK_GRAY_COLOR);
		JButton buildButton = new JButton("Build");
		JButton resetButton = new JButton("Reset");
		JButton mapButton = new JButton("Map");
		JButton previewButton = new JButton("Preview");
		JButton worldMapRouteButton = new JButton("WorldMap Route");
		JButton viewModeButton = new JButton("Order View");
		JButton exportJsonButton = new JButton("Export JSON");
		JButton saveJsonButton = new JButton("Save JSON…");
		JButton importJsonButton = new JButton("Import JSON…");
		applyMakerToolbarStyle(buildButton, resetButton, mapButton, previewButton, worldMapRouteButton, viewModeButton,
			exportJsonButton, saveJsonButton, importJsonButton);
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
		exportJsonButton.addActionListener(e -> exportDraftJsonToClipboard());
		saveJsonButton.addActionListener(e -> saveDraftJsonToFile());
		importJsonButton.addActionListener(e -> showImportDraftJsonDialog());
		buttonRow.add(buildButton);
		buttonRow.add(resetButton);
		buttonRow.add(mapButton);
		buttonRow.add(previewButton);
		buttonRow.add(worldMapRouteButton);
		buttonRow.add(viewModeButton);
		buttonRow.add(exportJsonButton);
		buttonRow.add(saveJsonButton);
		buttonRow.add(importJsonButton);
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
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		add(scrollPane, BorderLayout.CENTER);

		refresh();

		// Keep panel list in sync with right-click captures made outside this panel.
		refreshTimer = new Timer(1000, e -> refreshIfChanged());
		refreshTimer.setRepeats(true);
		refreshTimer.start();
	}

	private void exportDraftJsonToClipboard()
	{
		String json = helperConstructManager.exportDraftJson();
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(json), null);
		JOptionPane.showMessageDialog(this,
			"Draft JSON copied to clipboard (pretty-printed).",
			"Export JSON",
			JOptionPane.INFORMATION_MESSAGE);
	}

	private void saveDraftJsonToFile()
	{
		JFileChooser fc = new JFileChooser();
		String base = helperConstructManager.getCurrentDraftClassName();
		if (base == null || base.isBlank())
		{
			base = "construct-draft";
		}
		fc.setSelectedFile(new File(base.replaceAll("[^a-zA-Z0-9_-]+", "_") + "-draft.json"));
		if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
		{
			return;
		}
		File f = fc.getSelectedFile();
		try
		{
			Files.write(f.toPath(), helperConstructManager.exportDraftJson().getBytes(StandardCharsets.UTF_8));
			JOptionPane.showMessageDialog(this, "Saved to:\n" + f.getAbsolutePath(), "Save JSON", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Save failed", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void showImportDraftJsonDialog()
	{
		int confirm = JOptionPane.showConfirmDialog(this,
			"Replace the current maker draft with imported JSON?\nExport or save first if you need a backup.",
			"Import draft",
			JOptionPane.OK_CANCEL_OPTION,
			JOptionPane.WARNING_MESSAGE);
		if (confirm != JOptionPane.OK_OPTION)
		{
			return;
		}

		JTextArea textArea = new JTextArea(14, 44);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		JButton browse = new JButton("Load from file…");
		browse.addActionListener(ev ->
		{
			JFileChooser fc = new JFileChooser();
			if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
			{
				try
				{
					textArea.setText(new String(Files.readAllBytes(fc.getSelectedFile().toPath()), StandardCharsets.UTF_8));
				}
				catch (IOException ex)
				{
					JOptionPane.showMessageDialog(this, ex.getMessage(), "Read failed", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		JPanel panel = new JPanel(new BorderLayout(0, 6));
		panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
		panel.add(browse, BorderLayout.SOUTH);

		int r = JOptionPane.showConfirmDialog(this, panel, "Paste construct draft JSON", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (r != JOptionPane.OK_OPTION)
		{
			return;
		}

		HelperConstructManager.ImportDraftResult result = helperConstructManager.importDraftFromJson(textArea.getText());
		if (result.isSuccess())
		{
			refresh();
			JOptionPane.showMessageDialog(this, "Draft imported and saved to plugin config.", "Import", JOptionPane.INFORMATION_MESSAGE);
		}
		else
		{
			JOptionPane.showMessageDialog(this, result.getErrorMessage(), "Import failed", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void applyMakerToolbarStyle(JButton... buttons)
	{
		for (JButton b : buttons)
		{
			SwingUtil.removeButtonDecorations(b);
			b.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			b.setForeground(Color.WHITE);
		}
	}

	private JPanel buildSectionedView()
	{
		JPanel sectioned = new JPanel();
		sectioned.setLayout(new BoxLayout(sectioned, BoxLayout.Y_AXIS));
		sectioned.setBackground(ColorScheme.DARK_GRAY_COLOR);

		sectioned.add(sectionLabel("NPC Steps (library)"));
		sectioned.add(wrapStepLibraryTable(new JTable(npcLibraryModel), LibraryRemoveKind.NPC));
		sectioned.add(Box.createVerticalStrut(10));
		sectioned.add(sectionLabel("Object Steps (library)"));
		sectioned.add(wrapStepLibraryTable(new JTable(objectLibraryModel), LibraryRemoveKind.OBJECT));
		sectioned.add(Box.createVerticalStrut(10));
		sectioned.add(sectionLabel("Captured Requirements"));
		sectioned.add(wrapRequirementLibraryTable(new JTable(requirementLibraryModel)));
		return sectioned;
	}

	private JScrollPane wrapStepLibraryTable(JTable table, LibraryRemoveKind removeKind)
	{
		styleStepLibraryTable(table);
		table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField()));
		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				int row = table.rowAtPoint(e.getPoint());
				int col = table.columnAtPoint(e.getPoint());
				if (row < 0 || col != 2)
				{
					return;
				}
				if (removeKind == LibraryRemoveKind.NPC)
				{
					helperConstructManager.removeNpcStepAt(row);
				}
				else
				{
					helperConstructManager.removeObjectStepAt(row);
				}
				refresh();
			}
		});
		JScrollPane sp = new JScrollPane(table);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setBorder(new EmptyBorder(0, 0, 0, 0));
		return sp;
	}

	private JScrollPane wrapRequirementLibraryTable(JTable table)
	{
		styleRequirementLibraryTable(table);
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
				helperConstructManager.removeRequirementAt(row);
				refresh();
			}
		});
		JScrollPane sp = new JScrollPane(table);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setBorder(new EmptyBorder(0, 0, 0, 0));
		return sp;
	}

	private void styleStepLibraryTable(JTable table)
	{
		table.setFillsViewportHeight(true);
		table.setRowHeight(24);
		table.setShowGrid(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
				if (!isSelected)
				{
					c.setBackground(ColorScheme.DARK_GRAY_COLOR);
					c.setForeground(column == 2 ? new Color(100, 180, 255) : Color.WHITE);
				}
				return c;
			}
		});
		if (table.getColumnModel().getColumnCount() >= 3)
		{
			table.getColumnModel().getColumn(0).setPreferredWidth(420);
			table.getColumnModel().getColumn(1).setPreferredWidth(360);
			table.getColumnModel().getColumn(2).setPreferredWidth(80);
		}
	}

	private void styleRequirementLibraryTable(JTable table)
	{
		table.setFillsViewportHeight(true);
		table.setRowHeight(24);
		table.setShowGrid(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
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
			table.getColumnModel().getColumn(0).setPreferredWidth(520);
			table.getColumnModel().getColumn(1).setPreferredWidth(80);
		}
	}

	private enum LibraryRemoveKind
	{
		NPC,
		OBJECT
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

	private final class StepLibraryTableModel extends AbstractTableModel
	{
		private final boolean npcSteps;
		private final String[] columns = {"Captured", "Instruction text", "Remove"};
		private List<String> summaries = new ArrayList<>();
		private List<String> instructions = new ArrayList<>();

		private StepLibraryTableModel(boolean npcSteps)
		{
			this.npcSteps = npcSteps;
		}

		void setRows(List<String> updatedSummaries, List<String> updatedInstructions)
		{
			summaries = new ArrayList<>(updatedSummaries);
			instructions = new ArrayList<>(updatedInstructions);
			while (instructions.size() < summaries.size())
			{
				instructions.add("");
			}
			if (instructions.size() > summaries.size())
			{
				instructions = new ArrayList<>(instructions.subList(0, summaries.size()));
			}
			fireTableDataChanged();
		}

		@Override
		public int getRowCount()
		{
			return summaries.size();
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
				return summaries.get(rowIndex);
			}
			if (columnIndex == 1)
			{
				return rowIndex < instructions.size() ? instructions.get(rowIndex) : "";
			}
			return summaries.isEmpty() ? "" : "Remove";
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return columnIndex == 1;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			if (columnIndex != 1 || rowIndex < 0 || rowIndex >= summaries.size())
			{
				return;
			}
			String text = aValue == null ? "" : String.valueOf(aValue);
			if (npcSteps)
			{
				helperConstructManager.updateNpcStepInstructionAt(rowIndex, text);
			}
			else
			{
				helperConstructManager.updateObjectStepInstructionAt(rowIndex, text);
			}
			while (instructions.size() <= rowIndex)
			{
				instructions.add("");
			}
			instructions.set(rowIndex, text);
			fireTableCellUpdated(rowIndex, 1);
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
				if (column == 2)
				{
					var entry = stepOrderTableModel.getRow(row);
					if (entry != null && !entry.isSectionDivider())
					{
						return new DefaultCellEditor(new JTextField());
					}
				}
				return super.getCellEditor(row, column);
			}
		};
		orderTable.setDragEnabled(true);
		orderTable.setDropMode(DropMode.INSERT_ROWS);
		orderTable.setFillsViewportHeight(true);
		orderTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
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
		orderTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		orderTable.getColumnModel().getColumn(1).setPreferredWidth(280);
		orderTable.getColumnModel().getColumn(2).setPreferredWidth(400);
		orderTable.setTransferHandler(new StepReorderTransferHandler(orderTable));

		JScrollPane tableScroll = new JScrollPane(orderTable);
		tableScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tableScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		ordered.add(tableScroll, BorderLayout.CENTER);

		JButton removeSelectedButton = new JButton("Remove Selected");
		JButton addStepButton = new JButton("Add Step");
		JButton addSectionButton = new JButton("Add Section");
		applyMakerToolbarStyle(removeSelectedButton, addStepButton, addSectionButton);
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
		list.setSelectedIndex(0);
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
		var entry = stepOrderTableModel.getRow(row);
		Integer cur = entry == null ? null : entry.getOrderLinkedRequirementRawId();
		populateRequirementCombo(combo, cur);
		return new DefaultCellEditor(combo);
	}

	private void populateRequirementCombo(JComboBox<ReqChoice> combo, Integer selectedRawId)
	{
		combo.removeAllItems();
		combo.addItem(new ReqChoice("Default (from step)", null));
		combo.addItem(new ReqChoice("Varbit only", HelperConstructManager.ORDER_REQUIREMENT_VARBIT_ONLY));
		List<Integer> ids = helperConstructManager.getRequirementRawIds();
		List<String> labels = helperConstructManager.getRequirementSummaries();
		for (int i = 0; i < ids.size(); i++)
		{
			String lab = i < labels.size() ? labels.get(i) : String.valueOf(ids.get(i));
			combo.addItem(new ReqChoice(lab, ids.get(i)));
		}
		selectRequirementComboValue(combo, selectedRawId);
	}

	private static void selectRequirementComboValue(JComboBox<ReqChoice> combo, Integer rawId)
	{
		for (int i = 0; i < combo.getItemCount(); i++)
		{
			if (Objects.equals(combo.getItemAt(i).getValue(), rawId))
			{
				combo.setSelectedIndex(i);
				return;
			}
		}
	}

	private String labelForOrderRequirementOverride(Integer v)
	{
		if (v == null)
		{
			return "Default (from step)";
		}
		if (v.equals(HelperConstructManager.ORDER_REQUIREMENT_VARBIT_ONLY))
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

	private static final class ReqChoice
	{
		private final String label;
		private final Integer value;

		private ReqChoice(String label, Integer value)
		{
			this.label = label;
			this.value = value;
		}

		Integer getValue()
		{
			return value;
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
		npcLibraryModel.setRows(helperConstructManager.getNpcStepSummaries(), helperConstructManager.getNpcStepInstructionTexts());
		objectLibraryModel.setRows(helperConstructManager.getObjectStepSummaries(), helperConstructManager.getObjectStepInstructionTexts());
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
			+ "|Ni|" + String.join("\n", helperConstructManager.getNpcStepInstructionTexts())
			+ "|O|" + String.join("\n", helperConstructManager.getObjectStepSummaries())
			+ "|Oi|" + String.join("\n", helperConstructManager.getObjectStepInstructionTexts())
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
		private final String[] columns = {"Name/Var", "Requirement / condition", "Instruction text"};
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
			if (columnIndex == 2)
			{
				return row.isSectionDivider() ? "" : row.getInstructionText();
			}
			if (row.isSectionDivider())
			{
				return row.getSectionCondition() == null ? "" : row.getSectionCondition();
			}
			return labelForOrderRequirementOverride(row.getOrderLinkedRequirementRawId());
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			var row = rows.get(rowIndex);
			if (columnIndex == 0)
			{
				return true;
			}
			if (columnIndex == 2)
			{
				return !row.isSectionDivider();
			}
			return columnIndex == 1;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			var row = rows.get(rowIndex);
			if (columnIndex == 0)
			{
				helperConstructManager.updateStepVarName(row.getIndex(), aValue == null ? "" : String.valueOf(aValue));
				setRows(helperConstructManager.getCombinedStepRows());
				return;
			}
			if (columnIndex == 2 && !row.isSectionDivider())
			{
				helperConstructManager.updateOrderReferencedStepInstructionText(row.getIndex(), aValue == null ? "" : String.valueOf(aValue));
				setRows(helperConstructManager.getCombinedStepRows());
				return;
			}
			if (columnIndex == 1)
			{
				if (row.isSectionDivider())
				{
					helperConstructManager.updateSectionCondition(row.getIndex(), aValue == null ? "" : String.valueOf(aValue));
				}
				else if (aValue instanceof ReqChoice)
				{
					helperConstructManager.updateOrderLinkedRequirement(row.getIndex(), ((ReqChoice) aValue).getValue());
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
				parts.add(row.getVarName() + "|" + row.getSummary() + "|" + row.getSectionCondition() + "|" + row.getOrderLinkedRequirementRawId()
					+ "|" + row.getInstructionText());
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
