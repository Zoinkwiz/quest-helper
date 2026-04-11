package com.questhelper.panel;

import com.questhelper.managers.HelperConstructManager;
import com.questhelper.requirements.util.Operation;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.SwingUtil;
import org.jetbrains.annotations.NotNull;

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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

public final class HelperConstructEditorPanel extends JPanel
{
	private static final String USAGE_GUIDE = String.join("\n",
		"1. In-game: right-click NPCs, objects, or items and use the Construct: menu entries to capture definitions.",
		"2. NPC / Object / Item tabs: edit world point and instruction; click Attachments to add item or varbit requirements (and optional extra item raw IDs). Item highlight / completion for item steps is set per row in Quest order. Remove deletes a row.",
		"3. Quest order tab: add references to definitions, section dividers, and requirement overrides. Drag rows to reorder.",
		"4. Varbit reqs tab: one row per quest-order slot that uses Default or Varbit only routing — edit varbit id, value, Operation name (e.g. EQUAL), and optional display text. Choosing a concrete item override on that order row removes the varbit row.",
		"5. Build copies generated Java to the clipboard. Preview loads the draft in the Quest Helper sidebar.",
		"6. JSON export/import uses the same format as the plugin's saved draft — share with others or back up your work.");

	private final HelperConstructManager helperConstructManager;
	private final StepLibraryTableModel npcLibraryModel = new StepLibraryTableModel(StepLibraryKind.NPC);
	private final StepLibraryTableModel objectLibraryModel = new StepLibraryTableModel(StepLibraryKind.OBJECT);
	private final StepLibraryTableModel itemLibraryModel = new StepLibraryTableModel(StepLibraryKind.ITEM);
	private final LibraryTableModel requirementLibraryModel = new LibraryTableModel();
	private JTable orderTable;
	private final StepOrderTableModel stepOrderTableModel = new StepOrderTableModel();
	private final VarbitRoutingTableModel varbitRoutingTableModel = new VarbitRoutingTableModel();
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
		resetButton.setToolTipText("Clear the draft and remove saved maker state from the plugin config (asks for confirmation).");
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
			int confirm = JOptionPane.showConfirmDialog(this,
				"Clear the maker draft and remove saved maker state from the plugin config?\nExport or save first if you need a backup.",
				"Reset draft",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE);
			if (confirm != JOptionPane.OK_OPTION)
			{
				return;
			}
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

		JTable varbitTable = new JTable(varbitRoutingTableModel);
		styleVarbitRoutingTable(varbitTable);
		varbitTable.setRowHeight(24);
		varbitTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField()));
		varbitTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JTextField()));
		JComboBox<Operation> varbitOperationCombo = getOperationJComboBox();
		varbitTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				Object label = value instanceof Operation ? operationChoiceLabel((Operation) value) : value;
				return super.getTableCellRendererComponent(table, label, isSelected, hasFocus, row, column);
			}
		});
		varbitTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(varbitOperationCombo));
		varbitTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(new JTextField()));
		JScrollPane varbitScroll = new JScrollPane(varbitTable);
		varbitScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		varbitScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		mainTabs.addTab("Varbit reqs", varbitScroll);

		mainTabs.addTab("Quest order", orderedView);

		add(header, BorderLayout.NORTH);
		add(mainTabs, BorderLayout.CENTER);

		refresh();

		// Keep panel list in sync with right-click captures made outside this panel.
		refreshTimer = new Timer(1000, e -> refreshIfChanged());
		refreshTimer.setRepeats(true);
		refreshTimer.start();
	}

	private static @NotNull JComboBox<Operation> getOperationJComboBox()
	{
		JComboBox<Operation> varbitOperationCombo = new JComboBox<>(Operation.values());
		varbitOperationCombo.setMaximumRowCount(Operation.values().length);
		varbitOperationCombo.setRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				Object label = value instanceof Operation ? operationChoiceLabel((Operation) value) : value;
				return super.getListCellRendererComponent(list, label, index, isSelected, cellHasFocus);
			}
		});
		return varbitOperationCombo;
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

	/** Maps persisted operation name to enum; unknown values become {@link Operation#EQUAL}. */
	private static Operation operationFromPersistedName(String name)
	{
		if (name == null || name.isBlank())
		{
			return Operation.EQUAL;
		}
		try
		{
			return Operation.valueOf(name.trim());
		}
		catch (IllegalArgumentException ex)
		{
			return Operation.EQUAL;
		}
	}

	private static String operationChoiceLabel(Operation o)
	{
		return o.name() + " (" + o.getDisplayText() + ")";
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
		table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JTextField()));
		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				int row = table.rowAtPoint(e.getPoint());
				int col = table.columnAtPoint(e.getPoint());
				if (row < 0)
				{
					return;
				}
				if (col == 2)
				{
					openStepRequirementsEditor(stepKind, row);
					return;
				}
				if (col != 4)
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

	private void openStepRequirementsEditor(StepLibraryKind kind, int row)
	{
		List<HelperConstructManager.StepAttachmentEdit> initial = new ArrayList<>();
		switch (kind)
		{
			case NPC:
				initial.addAll(helperConstructManager.getNpcStepAttachmentsAt(row));
				break;
			case OBJECT:
				initial.addAll(helperConstructManager.getObjectStepAttachmentsAt(row));
				break;
			case ITEM:
				initial.addAll(helperConstructManager.getItemStepAttachmentsAt(row));
				break;
			default:
				return;
		}

		DefaultListModel<HelperConstructManager.StepAttachmentEdit> model = new DefaultListModel<>();
		for (HelperConstructManager.StepAttachmentEdit e : initial)
		{
			model.addElement(e);
		}

		JList<HelperConstructManager.StepAttachmentEdit> list = new JList<>(model);
		list.setVisibleRowCount(8);
		list.setCellRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList<?> jList, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				HelperConstructManager.StepAttachmentEdit e = (HelperConstructManager.StepAttachmentEdit) value;
				String text = helperConstructManager.summarizeStepAttachmentEdit(e);
				return super.getListCellRendererComponent(jList, text, index, isSelected, cellHasFocus);
			}
		});

		JButton addItemsButton = new JButton("Add items…");
		addItemsButton.addActionListener(ev -> appendCapturedItemsToAttachmentModel(model));

		JButton addVarbitButton = new JButton("Add varbit…");
		addVarbitButton.addActionListener(ev -> appendVarbitFromDialogToAttachmentModel(model));

		JButton removeButton = new JButton("Remove selected");
		removeButton.addActionListener(ev ->
		{
			int i = list.getSelectedIndex();
			if (i >= 0)
			{
				model.remove(i);
			}
		});

		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
		toolbar.setOpaque(false);
		toolbar.add(addItemsButton);
		toolbar.add(addVarbitButton);
		toolbar.add(removeButton);

		JLabel hint = new JLabel("<html>Attach extra requirements to this step (items from your captured list, or varbits).<br>Preview and generated code use <code>QuestStep.addRequirement</code> where supported.");
		hint.setForeground(Color.GRAY);

		JPanel panel = new JPanel(new BorderLayout(8, 8));
		panel.setOpaque(false);
		panel.add(toolbar, BorderLayout.NORTH);
		panel.add(new JScrollPane(list), BorderLayout.CENTER);
		panel.add(hint, BorderLayout.SOUTH);
		panel.setPreferredSize(new Dimension(500, 380));

		int r = JOptionPane.showConfirmDialog(this, panel, "Step attachments", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (r != JOptionPane.OK_OPTION)
		{
			return;
		}

		List<HelperConstructManager.StepAttachmentEdit> out = new ArrayList<>();
		for (int i = 0; i < model.size(); i++)
		{
			out.add(model.get(i));
		}

		boolean applied;
		switch (kind)
		{
			case NPC:
				applied = helperConstructManager.applyNpcStepAttachmentsAt(row, out);
				break;
			case OBJECT:
				applied = helperConstructManager.applyObjectStepAttachmentsAt(row, out);
				break;
			case ITEM:
				applied = helperConstructManager.applyItemStepAttachmentsAt(row, out);
				break;
			default:
				applied = false;
				break;
		}
		if (!applied)
		{
			JOptionPane.showMessageDialog(this,
				"Could not save attachments (check varbit id/value and Operation name, e.g. EQUAL).",
				"Step attachments",
				JOptionPane.WARNING_MESSAGE);
			return;
		}
		refresh();
	}

	private void appendCapturedItemsToAttachmentModel(DefaultListModel<HelperConstructManager.StepAttachmentEdit> model)
	{
		List<Integer> known = helperConstructManager.getRequirementRawIds();
		LinkedHashSet<Integer> knownSet = new LinkedHashSet<>(known);
		List<ReqChoice> listChoices = new ArrayList<>();
		List<String> labels = helperConstructManager.getRequirementSummaries();
		for (int i = 0; i < known.size(); i++)
		{
			int raw = known.get(i);
			String lab = i < labels.size() ? labels.get(i) : String.valueOf(raw);
			listChoices.add(new ReqChoice(lab, raw));
		}
		JList<ReqChoice> extrasList = new JList<>(listChoices.toArray(new ReqChoice[0]));
		extrasList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		extrasList.setVisibleRowCount(6);
		JTextField otherField = new JTextField(24);
		JPanel p = new JPanel(new BorderLayout(0, 6));
		p.setOpaque(false);
		p.add(new JLabel("Captured items (Ctrl/Cmd+click):", SwingConstants.LEFT), BorderLayout.NORTH);
		p.add(new JScrollPane(extrasList), BorderLayout.CENTER);
		JPanel south = new JPanel(new BorderLayout(0, 4));
		south.setOpaque(false);
		south.add(new JLabel("Other item raw IDs (comma-separated):", SwingConstants.LEFT), BorderLayout.NORTH);
		south.add(otherField, BorderLayout.CENTER);
		p.add(south, BorderLayout.SOUTH);
		p.setPreferredSize(new Dimension(420, 280));

		if (JOptionPane.showConfirmDialog(this, p, "Add item attachments", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION)
		{
			return;
		}
		for (ReqChoice c : extrasList.getSelectedValuesList())
		{
			if (c != null && c.getValue() != null && knownSet.contains(c.getValue()))
			{
				model.addElement(HelperConstructManager.StepAttachmentEdit.item(c.getValue()));
			}
		}
		for (Integer id : parseCommaSeparatedInts(otherField.getText()))
		{
			if (id != null)
			{
				model.addElement(HelperConstructManager.StepAttachmentEdit.item(id));
			}
		}
	}

	private void appendVarbitFromDialogToAttachmentModel(DefaultListModel<HelperConstructManager.StepAttachmentEdit> model)
	{
		JTextField idField = new JTextField(8);
		JTextField valueField = new JTextField("1", 6);
		JComboBox<Operation> opCombo = new JComboBox<>(Operation.values());
		opCombo.setSelectedItem(Operation.EQUAL);
		JTextField displayField = new JTextField(20);
		JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
		form.setOpaque(false);
		form.add(new JLabel("Varbit id:"));
		form.add(idField);
		form.add(new JLabel("Required value:"));
		form.add(valueField);
		form.add(new JLabel("Operation:"));
		form.add(opCombo);
		form.add(new JLabel("Display text (optional):"));
		form.add(displayField);

		if (JOptionPane.showConfirmDialog(this, form, "Add varbit attachment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION)
		{
			return;
		}
		try
		{
			int vid = Integer.parseInt(idField.getText().trim());
			int val = Integer.parseInt(valueField.getText().trim());
			Operation op = (Operation) opCombo.getSelectedItem();
			String disp = displayField.getText();
			model.addElement(HelperConstructManager.StepAttachmentEdit.varbit(vid, val, op == null ? "EQUAL" : op.name(), disp));
		}
		catch (NumberFormatException ex)
		{
			JOptionPane.showMessageDialog(this, "Enter valid integers for varbit id and required value.", "Add varbit", JOptionPane.WARNING_MESSAGE);
		}
	}

	private static List<Integer> parseCommaSeparatedInts(String raw)
	{
		List<Integer> out = new ArrayList<>();
		if (raw == null)
		{
			return out;
		}
		String t = raw.trim();
		if (t.isEmpty())
		{
			return out;
		}
		for (String part : t.split(","))
		{
			String p = part.trim();
			if (p.isEmpty())
			{
				continue;
			}
			try
			{
				out.add(Integer.parseInt(p));
			}
			catch (NumberFormatException ignored)
			{
			}
		}
		return out;
	}

	private static void appendCommaSeparated(StringBuilder sb, int id)
	{
		if (sb.length() > 0)
		{
			sb.append(", ");
		}
		sb.append(id);
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
					boolean clickable = column == 2 || column == 4;
					c.setForeground(clickable ? new Color(100, 180, 255) : Color.WHITE);
				}
				return c;
			}
		});
		if (table.getColumnModel().getColumnCount() >= 5)
		{
			table.getColumnModel().getColumn(0).setPreferredWidth(200);
			table.getColumnModel().getColumn(1).setPreferredWidth(110);
			table.getColumnModel().getColumn(2).setPreferredWidth(220);
			table.getColumnModel().getColumn(3).setPreferredWidth(200);
			table.getColumnModel().getColumn(4).setPreferredWidth(56);
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

	private void styleVarbitRoutingTable(JTable table)
	{
		table.setFillsViewportHeight(true);
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
					c.setForeground(Color.WHITE);
				}
				return c;
			}
		});
		if (table.getColumnModel().getColumnCount() >= 5)
		{
			table.getColumnModel().getColumn(0).setPreferredWidth(320);
			table.getColumnModel().getColumn(1).setPreferredWidth(88);
			table.getColumnModel().getColumn(2).setPreferredWidth(96);
			table.getColumnModel().getColumn(3).setPreferredWidth(100);
			table.getColumnModel().getColumn(4).setPreferredWidth(220);
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
		private final String[] columns = {
			"Captured",
			"World (x, y, plane)",
			"Attachments",
			"Instruction",
			"Remove"
		};
		private List<String> summaries = new ArrayList<>();
		private List<String> worldPoints = new ArrayList<>();
		private List<String> requirementDisplays = new ArrayList<>();
		private List<String> instructions = new ArrayList<>();

		private StepLibraryTableModel(StepLibraryKind kind)
		{
			this.kind = kind;
		}

		void setRows(
			List<String> updatedSummaries,
			List<String> updatedWorldPoints,
			List<String> updatedRequirementDisplays,
			List<String> updatedInstructions)
		{
			summaries = new ArrayList<>(updatedSummaries);
			worldPoints = new ArrayList<>(updatedWorldPoints);
			requirementDisplays = new ArrayList<>(updatedRequirementDisplays);
			instructions = new ArrayList<>(updatedInstructions);
			int n = summaries.size();
			padToSize(worldPoints, n);
			padToSize(requirementDisplays, n);
			padToSize(instructions, n);
			fireTableDataChanged();
		}

		private void padToSize(List<String> list, int n)
		{
			while (list.size() < n)
			{
				list.add("");
			}
			if (list.size() > n)
			{
				list.subList(n, list.size()).clear();
			}
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
			switch (columnIndex)
			{
				case 0:
					return summaries.get(rowIndex);
				case 1:
					return rowIndex < worldPoints.size() ? worldPoints.get(rowIndex) : "";
				case 2:
					return rowIndex < requirementDisplays.size() ? requirementDisplays.get(rowIndex) : "";
				case 3:
					return rowIndex < instructions.size() ? instructions.get(rowIndex) : "";
				case 4:
				default:
					return summaries.isEmpty() ? "" : "Remove";
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return columnIndex == 1 || columnIndex == 3;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			if (rowIndex < 0 || rowIndex >= summaries.size() || (columnIndex != 1 && columnIndex != 3))
			{
				return;
			}
			String text = aValue == null ? "" : String.valueOf(aValue);
			boolean ok = applyStepLibraryEdit(kind, rowIndex, columnIndex, text);
			if (!ok)
			{
				return;
			}
			if (columnIndex == 1)
			{
				worldPoints.set(rowIndex, text);
			}
			else
			{
				instructions.set(rowIndex, text);
			}
			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}

	private boolean applyStepLibraryEdit(StepLibraryKind kind, int row, int column, String text)
	{
		switch (column)
		{
			case 1:
				switch (kind)
				{
					case NPC:
						return helperConstructManager.updateNpcStepWorldPointAt(row, text);
					case OBJECT:
						return helperConstructManager.updateObjectStepWorldPointAt(row, text);
					case ITEM:
						return helperConstructManager.updateItemStepWorldPointAt(row, text);
					default:
						return false;
				}
			case 3:
				switch (kind)
				{
					case NPC:
						return helperConstructManager.updateNpcStepInstructionAt(row, text);
					case OBJECT:
						return helperConstructManager.updateObjectStepInstructionAt(row, text);
					case ITEM:
						return helperConstructManager.updateItemStepInstructionAt(row, text);
					default:
						return false;
				}
			default:
				return false;
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
		combo.addItem(new ReqChoice("Default (varbit routing)", null));
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
			return "Default (varbit routing)";
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
		npcLibraryModel.setRows(
			helperConstructManager.getNpcStepSummaries(),
			helperConstructManager.getNpcStepWorldPointTexts(),
			helperConstructManager.getNpcStepRequirementsDisplays(),
			helperConstructManager.getNpcStepInstructionTexts());
		objectLibraryModel.setRows(
			helperConstructManager.getObjectStepSummaries(),
			helperConstructManager.getObjectStepWorldPointTexts(),
			helperConstructManager.getObjectStepRequirementsDisplays(),
			helperConstructManager.getObjectStepInstructionTexts());
		itemLibraryModel.setRows(
			helperConstructManager.getItemStepSummaries(),
			helperConstructManager.getItemStepWorldPointTexts(),
			helperConstructManager.getItemStepRequirementsDisplays(),
			helperConstructManager.getItemStepInstructionTexts());
		requirementLibraryModel.setRows(helperConstructManager.getRequirementSummaries());
		stepOrderTableModel.setRows(helperConstructManager.getCombinedStepRows());
		varbitRoutingTableModel.reloadFromManager();
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
			+ "|Nw|" + String.join("\n", helperConstructManager.getNpcStepWorldPointTexts())
			+ "|Nrq|" + String.join("\n", helperConstructManager.getNpcStepRequirementsDisplays())
			+ "|Ni|" + String.join("\n", helperConstructManager.getNpcStepInstructionTexts())
			+ "|O|" + String.join("\n", helperConstructManager.getObjectStepSummaries())
			+ "|Ow|" + String.join("\n", helperConstructManager.getObjectStepWorldPointTexts())
			+ "|Orq|" + String.join("\n", helperConstructManager.getObjectStepRequirementsDisplays())
			+ "|Oi|" + String.join("\n", helperConstructManager.getObjectStepInstructionTexts())
			+ "|I|" + String.join("\n", helperConstructManager.getItemStepSummaries())
			+ "|Iw|" + String.join("\n", helperConstructManager.getItemStepWorldPointTexts())
			+ "|Irq|" + String.join("\n", helperConstructManager.getItemStepRequirementsDisplays())
			+ "|Ii|" + String.join("\n", helperConstructManager.getItemStepInstructionTexts())
			+ "|A|" + stepOrderTableModel.signature()
			+ "|R|" + String.join("\n", helperConstructManager.getRequirementSummaries())
			+ "|V|" + varbitRoutingTableModel.signature();
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

	private final class VarbitRoutingTableModel extends AbstractTableModel
	{
		private final String[] columns = {"Order slot", "Varbit ID", "Required value", "Operation", "Display text"};
		private final List<VarbitRoutingRow> rows = new ArrayList<>();

		void reloadFromManager()
		{
			rows.clear();
			for (HelperConstructManager.VarbitSlotRow slot : helperConstructManager.getVarbitSlotsInQuestOrderForEditor())
			{
				rows.add(new VarbitRoutingRow(
					slot.getOrderLineId(),
					slot.getOrderSlotSummary(),
					slot.getVarbitId(),
					slot.getRequiredValue(),
					slot.getOperation(),
					slot.getDisplayText()));
			}
			fireTableDataChanged();
		}

		String signature()
		{
			List<String> parts = new ArrayList<>();
			for (VarbitRoutingRow row : rows)
			{
				parts.add(row.orderLineId + "|" + row.summary + "|" + row.varbitId + "|" + row.requiredValue + "|" + row.operation + "|" + row.displayText);
			}
			return String.join("\n", parts);
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
			VarbitRoutingRow row = rows.get(rowIndex);
			switch (columnIndex)
			{
				case 0:
					return row.summary;
				case 1:
					return row.varbitId;
				case 2:
					return row.requiredValue;
				case 3:
					return operationFromPersistedName(row.operation);
				case 4:
					return row.displayText;
				default:
					return "";
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex)
		{
			if (columnIndex == 1 || columnIndex == 2)
			{
				return Integer.class;
			}
			if (columnIndex == 3)
			{
				return Operation.class;
			}
			return String.class;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return columnIndex >= 1;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			if (columnIndex < 1)
			{
				return;
			}
			VarbitRoutingRow row = rows.get(rowIndex);
			int newVarbit = row.varbitId;
			int newReq = row.requiredValue;
			String newOp = row.operation;
			String newDisp = row.displayText;
			switch (columnIndex)
			{
				case 1:
					try
					{
						newVarbit = Integer.parseInt(String.valueOf(aValue).trim());
					}
					catch (NumberFormatException ex)
					{
						JOptionPane.showMessageDialog(HelperConstructEditorPanel.this,
							"Enter a valid integer for varbit id.",
							"Varbit reqs",
							JOptionPane.WARNING_MESSAGE);
						reloadFromManager();
						return;
					}
					break;
				case 2:
					try
					{
						newReq = Integer.parseInt(String.valueOf(aValue).trim());
					}
					catch (NumberFormatException ex)
					{
						JOptionPane.showMessageDialog(HelperConstructEditorPanel.this,
							"Enter a valid integer for required value.",
							"Varbit reqs",
							JOptionPane.WARNING_MESSAGE);
						reloadFromManager();
						return;
					}
					break;
				case 3:
					if (aValue instanceof Operation)
					{
						newOp = ((Operation) aValue).name();
					}
					else
					{
						newOp = operationFromPersistedName(String.valueOf(aValue)).name();
					}
					break;
				case 4:
					newDisp = String.valueOf(aValue);
					break;
				default:
					return;
			}
			if (!helperConstructManager.updateVarbitSlotForOrderLine(row.orderLineId, newVarbit, newReq, newOp, newDisp))
			{
				JOptionPane.showMessageDialog(HelperConstructEditorPanel.this,
					"Could not apply varbit settings.",
					"Varbit reqs",
					JOptionPane.WARNING_MESSAGE);
			}
			reloadFromManager();
		}

		private final class VarbitRoutingRow
		{
			private final String orderLineId;
			private final String summary;
			private final int varbitId;
			private final int requiredValue;
			private final String operation;
			private final String displayText;

			private VarbitRoutingRow(String orderLineId, String summary, int varbitId, int requiredValue, String operation, String displayText)
			{
				this.orderLineId = orderLineId;
				this.summary = summary;
				this.varbitId = varbitId;
				this.requiredValue = requiredValue;
				this.operation = operation;
				this.displayText = displayText;
			}
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
