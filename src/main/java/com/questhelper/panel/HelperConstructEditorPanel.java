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
	private static final String USAGE_GUIDE = String.join("\n",
		"1. In-game: right-click NPCs, objects, or items and use the Construct: menu entries to capture definitions.",
		"2. NPC / Object / Item tabs: edit instruction text in the middle column; Remove deletes a captured row (requirements live under Item steps).",
		"3. Quest order tab: add references to definitions, section dividers, and requirement overrides. Drag rows to reorder.",
		"4. Build copies generated Java to the clipboard. Preview loads the draft in the Quest Helper sidebar.",
		"5. JSON export/import uses the same format as the plugin's saved draft — share with others or back up your work.");

	private final HelperConstructManager helperConstructManager;
	private final StepLibraryTableModel npcLibraryModel = new StepLibraryTableModel(StepLibraryKind.NPC);
	private final StepLibraryTableModel objectLibraryModel = new StepLibraryTableModel(StepLibraryKind.OBJECT);
	private final StepLibraryTableModel itemLibraryModel = new StepLibraryTableModel(StepLibraryKind.ITEM);
	private final LibraryTableModel requirementLibraryModel = new LibraryTableModel();
	private JTable orderTable;
	private final StepOrderTableModel stepOrderTableModel = new StepOrderTableModel();
	private final Timer refreshTimer;
	private JButton worldMapRouteButton;
	private String lastRenderSignature = "";

	public HelperConstructEditorPanel(HelperConstructManager helperConstructManager)
	{
		this.helperConstructManager = helperConstructManager;

		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(8, 8, 8, 8));

		JPanel header = new JPanel(new BorderLayout(0, 6));
		header.setOpaque(false);

		JPanel titleRow = new JPanel(new BorderLayout(8, 0));
		titleRow.setOpaque(false);
		var title = JGenerator.makeJTextArea("Quest Helper Maker");
		title.setForeground(Color.WHITE);
		titleRow.add(title, BorderLayout.CENTER);
		JButton helpButton = new JButton("How to use…");
		SwingUtil.removeButtonDecorations(helpButton);
		helpButton.setForeground(new Color(143, 188, 187));
		helpButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		helpButton.setToolTipText("Open the full usage guide");
		helpButton.addActionListener(e -> JOptionPane.showMessageDialog(this, USAGE_GUIDE, "Quest Helper Maker", JOptionPane.INFORMATION_MESSAGE));
		titleRow.add(helpButton, BorderLayout.LINE_END);
		header.add(titleRow, BorderLayout.NORTH);

		JLabel workflowLine = new JLabel(
			"Capture in-game → edit in tabs below → Build / Preview / JSON. Hover buttons for details.");
		workflowLine.setForeground(new Color(160, 160, 170));
		header.add(workflowLine, BorderLayout.CENTER);

		JButton buildButton = new JButton("Build");
		JButton resetButton = new JButton("Reset");
		JButton previewButton = new JButton("Preview");
		JButton mapButton = new JButton("Route map");
		worldMapRouteButton = new JButton("WorldMap");
		JButton exportJsonButton = new JButton("Export JSON");
		JButton saveJsonButton = new JButton("Save JSON…");
		JButton importJsonButton = new JButton("Import JSON…");
		applyMakerToolbarStyle(buildButton, resetButton, mapButton, previewButton, worldMapRouteButton,
			exportJsonButton, saveJsonButton, importJsonButton);
		buildButton.setToolTipText("Copy generated Java helper source to the clipboard.");
		resetButton.setToolTipText("Clear the draft and remove saved maker state from the plugin config.");
		previewButton.setToolTipText("Load this draft as a preview in the main Quest Helper sidebar.");
		mapButton.setToolTipText("Save a PNG of step world positions connected as a route (needs 2+ points with coordinates).");
		worldMapRouteButton.setToolTipText("Toggle in-game world map markers for the ordered route.");
		exportJsonButton.setToolTipText("Copy the whole draft as pretty-printed JSON (same shape as plugin save).");
		saveJsonButton.setToolTipText("Write the draft JSON to a file.");
		importJsonButton.setToolTipText("Replace the current draft from pasted JSON or a file (saved to config when successful).");
		syncWorldMapRouteButtonLabel();

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
			syncWorldMapRouteButtonLabel();
		});
		exportJsonButton.addActionListener(e -> exportDraftJsonToClipboard());
		saveJsonButton.addActionListener(e -> saveDraftJsonToFile());
		importJsonButton.addActionListener(e -> showImportDraftJsonDialog());

		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
		toolbar.setOpaque(false);
		toolbar.add(buildButton);
		toolbar.add(resetButton);
		toolbar.add(previewButton);
		toolbar.add(newToolbarSeparator());
		toolbar.add(mapButton);
		toolbar.add(worldMapRouteButton);
		toolbar.add(newToolbarSeparator());
		toolbar.add(exportJsonButton);
		toolbar.add(saveJsonButton);
		toolbar.add(importJsonButton);
		header.add(toolbar, BorderLayout.SOUTH);

		JPanel orderedView = buildOrderedView();
		JTabbedPane mainTabs = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		mainTabs.setBackground(ColorScheme.DARK_GRAY_COLOR);
		styleConstructTabbedPane(mainTabs);
		mainTabs.addTab("NPC steps", wrapStepLibraryTable(new JTable(npcLibraryModel), StepLibraryKind.NPC));
		mainTabs.addTab("Object steps", wrapStepLibraryTable(new JTable(objectLibraryModel), StepLibraryKind.OBJECT));
		mainTabs.addTab("Item steps", buildItemStepsAndRequirementsPanel());
		mainTabs.addTab("Quest order", orderedView);

		add(header, BorderLayout.NORTH);
		add(mainTabs, BorderLayout.CENTER);

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

	private void syncWorldMapRouteButtonLabel()
	{
		if (worldMapRouteButton != null)
		{
			worldMapRouteButton.setText(helperConstructManager.isWorldMapRoutePreviewEnabled()
				? "WorldMap (on)"
				: "WorldMap (off)");
		}
	}

	private static JComponent newToolbarSeparator()
	{
		JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
		Dimension d = sep.getPreferredSize();
		sep.setPreferredSize(new Dimension(Math.max(d.width, 2), 22));
		return sep;
	}

	private static void styleConstructTabbedPane(JTabbedPane tabs)
	{
		tabs.setOpaque(true);
		tabs.setBackground(ColorScheme.DARK_GRAY_COLOR);
	}

	private JPanel buildItemStepsAndRequirementsPanel()
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		JScrollPane itemScroll = wrapStepLibraryTable(new JTable(itemLibraryModel), StepLibraryKind.ITEM);
		JPanel reqBlock = new JPanel(new BorderLayout(0, 6));
		reqBlock.setBackground(ColorScheme.DARK_GRAY_COLOR);
		reqBlock.add(sectionLabel("Captured requirements (used in quest order overrides)"), BorderLayout.NORTH);
		reqBlock.add(wrapRequirementLibraryTable(new JTable(requirementLibraryModel)), BorderLayout.CENTER);
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, itemScroll, reqBlock);
		split.setResizeWeight(0.55);
		split.setBorder(null);
		split.setContinuousLayout(true);
		panel.add(split, BorderLayout.CENTER);
		return panel;
	}

	private JScrollPane wrapStepLibraryTable(JTable table, StepLibraryKind stepKind)
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
				switch (stepKind)
				{
					case NPC:
						helperConstructManager.removeNpcStepAt(row);
						break;
					case OBJECT:
						helperConstructManager.removeObjectStepAt(row);
						break;
					case ITEM:
						helperConstructManager.removeItemStepAt(row);
						break;
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
			table.getColumnModel().getColumn(0).setPreferredWidth(280);
			table.getColumnModel().getColumn(1).setPreferredWidth(240);
			table.getColumnModel().getColumn(2).setPreferredWidth(72);
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
			table.getColumnModel().getColumn(0).setPreferredWidth(400);
			table.getColumnModel().getColumn(1).setPreferredWidth(72);
		}
	}

	private enum StepLibraryKind
	{
		NPC,
		OBJECT,
		ITEM
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
		private final StepLibraryKind kind;
		private final String[] columns = {"Captured", "Instruction text", "Remove"};
		private List<String> summaries = new ArrayList<>();
		private List<String> instructions = new ArrayList<>();

		private StepLibraryTableModel(StepLibraryKind kind)
		{
			this.kind = kind;
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
			switch (kind)
			{
				case NPC:
					helperConstructManager.updateNpcStepInstructionAt(rowIndex, text);
					break;
				case OBJECT:
					helperConstructManager.updateObjectStepInstructionAt(rowIndex, text);
					break;
				case ITEM:
					helperConstructManager.updateItemStepInstructionAt(rowIndex, text);
					break;
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
		JLabel orderHint = new JLabel("Drag rows to reorder. Add Step / Add Section / Remove use the buttons below.");
		orderHint.setForeground(Color.GRAY);
		ordered.add(orderHint, BorderLayout.NORTH);

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
		orderTable.getColumnModel().getColumn(0).setPreferredWidth(160);
		orderTable.getColumnModel().getColumn(1).setPreferredWidth(220);
		orderTable.getColumnModel().getColumn(2).setPreferredWidth(260);
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
		npcLibraryModel.setRows(helperConstructManager.getNpcStepSummaries(), helperConstructManager.getNpcStepInstructionTexts());
		objectLibraryModel.setRows(helperConstructManager.getObjectStepSummaries(), helperConstructManager.getObjectStepInstructionTexts());
		itemLibraryModel.setRows(helperConstructManager.getItemStepSummaries(), helperConstructManager.getItemStepInstructionTexts());
		requirementLibraryModel.setRows(helperConstructManager.getRequirementSummaries());
		stepOrderTableModel.setRows(helperConstructManager.getCombinedStepRows());
		syncWorldMapRouteButtonLabel();
		lastRenderSignature = computeSignature();
		revalidate();
		repaint();
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
			+ "|I|" + String.join("\n", helperConstructManager.getItemStepSummaries())
			+ "|Ii|" + String.join("\n", helperConstructManager.getItemStepInstructionTexts())
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
