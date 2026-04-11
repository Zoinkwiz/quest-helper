package com.questhelper.panel;

import com.questhelper.managers.ConstructStepKind;
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
import java.util.List;
import java.util.Objects;

public final class HelperConstructEditorPanel extends JPanel
{
	private static final String USAGE_GUIDE = String.join("\n",
		"1. In-game: right-click NPCs, objects, items, or Walk here on a tile and use the Construct: menu entries to capture definitions.",
		"2. NPC / Object / Generic tabs: edit Name/Var, id (NPC/object), world point, and instruction; click Attachments to pick requirements, toggle Highlight for item rows, and save. Remove deletes a row. Use Add step to insert an empty row of that tab's type.",
		"3. Item reqs tab lists captured items used for quest order overrides and attachments.",
		"4. Quest order tab: add references to definitions, section dividers, and requirement overrides. Drag rows to reorder.",
		"5. Varbit reqs tab: one row per quest-order slot that uses Default or Varbit only routing — edit varbit id, value, Operation name (e.g. EQUAL), and optional display text. Choosing a concrete item override on that order row removes the varbit row.",
		"6. Build copies generated Java to the clipboard. Preview loads the draft in the Quest Helper sidebar.",
		"7. JSON export/import uses the same format as the plugin's saved draft — share with others or back up your work.");

	private final HelperConstructManager helperConstructManager;
	private final StepLibraryTableModel npcLibraryModel = new StepLibraryTableModel(ConstructStepKind.NPC);
	private final StepLibraryTableModel objectLibraryModel = new StepLibraryTableModel(ConstructStepKind.OBJECT);
	private final StepLibraryTableModel genericLibraryModel = new StepLibraryTableModel(ConstructStepKind.TEXT);
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
		mainTabs.addTab("NPC steps", wrapStepLibraryTableWithToolbar(new JTable(npcLibraryModel), ConstructStepKind.NPC));
		mainTabs.addTab("Object steps", wrapStepLibraryTableWithToolbar(new JTable(objectLibraryModel), ConstructStepKind.OBJECT));
		mainTabs.addTab("Generic steps", wrapStepLibraryTableWithToolbar(new JTable(genericLibraryModel), ConstructStepKind.TEXT));
		mainTabs.addTab("Item reqs", buildItemRequirementsPanel());

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

	private JPanel buildItemRequirementsPanel()
	{
		JPanel panel = new JPanel(new BorderLayout(0, 6));
		panel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		panel.add(sectionLabel("Captured item requirements (quest order overrides and step attachments)"), BorderLayout.NORTH);
		panel.add(wrapRequirementLibraryTable(new JTable(requirementLibraryModel)), BorderLayout.CENTER);
		return panel;
	}

	private JPanel wrapStepLibraryTableWithToolbar(JTable table, ConstructStepKind stepKind)
	{
		JPanel wrap = new JPanel(new BorderLayout(0, 6));
		wrap.setOpaque(false);
		JButton addStep = new JButton("Add step");
		addStep.setForeground(Color.WHITE);
		addStep.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		addStep.addActionListener(e ->
		{
			helperConstructManager.addEmptyStepFromUi(stepKind);
			refresh();
		});
		JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		bar.setOpaque(false);
		bar.add(addStep);
		wrap.add(bar, BorderLayout.NORTH);
		wrap.add(wrapStepLibraryTable(table, stepKind), BorderLayout.CENTER);
		return wrap;
	}

	private static int stepLibIdColumn(ConstructStepKind k)
	{
		return k == ConstructStepKind.TEXT ? -1 : 1;
	}

	private static int stepLibWorldColumn(ConstructStepKind k)
	{
		return k == ConstructStepKind.TEXT ? 1 : 2;
	}

	private static int stepLibAttachmentsColumn(ConstructStepKind k)
	{
		return k == ConstructStepKind.TEXT ? 2 : 3;
	}

	private static int stepLibInstructionColumn(ConstructStepKind k)
	{
		return k == ConstructStepKind.TEXT ? 3 : 4;
	}

	private static int stepLibRemoveColumn(ConstructStepKind k)
	{
		return k == ConstructStepKind.TEXT ? 4 : 5;
	}

	private JScrollPane wrapStepLibraryTable(JTable table, ConstructStepKind stepKind)
	{
		styleStepLibraryTable(table, stepKind);
		int worldCol = stepLibWorldColumn(stepKind);
		int instrCol = stepLibInstructionColumn(stepKind);
		table.getColumnModel().getColumn(worldCol).setCellEditor(new DefaultCellEditor(new JTextField()));
		table.getColumnModel().getColumn(instrCol).setCellEditor(new DefaultCellEditor(new JTextField()));
		int idCol = stepLibIdColumn(stepKind);
		if (idCol >= 0)
		{
			table.getColumnModel().getColumn(idCol).setCellEditor(new DefaultCellEditor(new JTextField()));
		}
		int attachCol = stepLibAttachmentsColumn(stepKind);
		int removeCol = stepLibRemoveColumn(stepKind);
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
				if (col == attachCol)
				{
					openStepRequirementsEditor(stepKind, row);
					return;
				}
				if (col != removeCol)
				{
					return;
				}
				helperConstructManager.removeStepAt(stepKind, row);
				refresh();
			}
		});
		JScrollPane sp = new JScrollPane(table);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setBorder(new EmptyBorder(0, 0, 0, 0));
		return sp;
	}

	private void openStepRequirementsEditor(ConstructStepKind kind, int row)
	{
		List<HelperConstructManager.StepAttachmentEdit> initial = new ArrayList<>();
		initial.addAll(helperConstructManager.getStepAttachmentsAt(kind, row));

		DefaultListModel<HelperConstructManager.StepAttachmentEdit> model = new DefaultListModel<>();
		for (HelperConstructManager.StepAttachmentEdit e : initial)
		{
			HelperConstructManager.StepAttachmentEdit c = HelperConstructManager.StepAttachmentEdit.copyOf(e);
			if (c != null)
			{
				model.addElement(c);
			}
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

		JCheckBox highlightCb = new JCheckBox("Highlight (item attachments only)");
		highlightCb.setOpaque(false);
		highlightCb.setForeground(Color.WHITE);
		Runnable syncHighlightFromSelection = () ->
		{
			HelperConstructManager.StepAttachmentEdit sel = list.getSelectedValue();
			boolean item = sel != null && "ITEM".equalsIgnoreCase(sel.getKind());
			highlightCb.setEnabled(item);
			if (item)
			{
				highlightCb.setSelected(sel.isItemHighlighted());
			}
			else
			{
				highlightCb.setSelected(false);
			}
		};
		list.addListSelectionListener(e ->
		{
			if (!e.getValueIsAdjusting())
			{
				syncHighlightFromSelection.run();
			}
		});
		highlightCb.addActionListener(ev ->
		{
			HelperConstructManager.StepAttachmentEdit sel = list.getSelectedValue();
			if (sel != null && "ITEM".equalsIgnoreCase(sel.getKind()))
			{
				sel.setItemHighlighted(highlightCb.isSelected());
				list.repaint();
			}
		});

		JButton addPickButton = new JButton("Add…");
		addPickButton.addActionListener(ev -> appendRequirementPickToAttachmentModel(model));

		JButton removeButton = new JButton("Remove selected");
		removeButton.addActionListener(ev ->
		{
			int i = list.getSelectedIndex();
			if (i >= 0)
			{
				model.remove(i);
				syncHighlightFromSelection.run();
			}
		});

		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
		toolbar.setOpaque(false);
		toolbar.add(addPickButton);
		toolbar.add(removeButton);

		JLabel hint = new JLabel("<html>Choose from captured item requirements and varbit routing rows (same list as the Varbit reqs tab).<br>Preview and generated code use <code>QuestStep.addRequirement</code> where supported.");
		hint.setForeground(Color.GRAY);

		JPanel south = new JPanel(new BorderLayout(0, 6));
		south.setOpaque(false);
		south.add(highlightCb, BorderLayout.NORTH);
		south.add(hint, BorderLayout.CENTER);

		JPanel panel = new JPanel(new BorderLayout(8, 8));
		panel.setOpaque(false);
		panel.add(toolbar, BorderLayout.NORTH);
		panel.add(new JScrollPane(list), BorderLayout.CENTER);
		panel.add(south, BorderLayout.SOUTH);
		panel.setPreferredSize(new Dimension(500, 420));

		if (model.size() > 0)
		{
			list.setSelectedIndex(0);
			syncHighlightFromSelection.run();
		}

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

		boolean applied = helperConstructManager.applyStepAttachmentsAt(kind, row, out);
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

	private void appendRequirementPickToAttachmentModel(DefaultListModel<HelperConstructManager.StepAttachmentEdit> model)
	{
		List<HelperConstructManager.StepAttachmentPickOption> picks = helperConstructManager.getStepAttachmentPickOptions();
		if (picks.isEmpty())
		{
			JOptionPane.showMessageDialog(this,
				"Add item requirements (Item reqs tab) or quest-order rows that use varbit routing first.",
				"Add attachment",
				JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		JList<HelperConstructManager.StepAttachmentPickOption> list = new JList<>(picks.toArray(new HelperConstructManager.StepAttachmentPickOption[0]));
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setVisibleRowCount(10);
		list.setCellRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList<?> jList, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				HelperConstructManager.StepAttachmentPickOption o = (HelperConstructManager.StepAttachmentPickOption) value;
				return super.getListCellRendererComponent(jList, o.getLabel(), index, isSelected, cellHasFocus);
			}
		});
		JPanel p = new JPanel(new BorderLayout(0, 6));
		p.setOpaque(false);
		p.add(new JLabel("Select one or more (Ctrl/Cmd+click):", SwingConstants.LEFT), BorderLayout.NORTH);
		p.add(new JScrollPane(list), BorderLayout.CENTER);
		p.setPreferredSize(new Dimension(520, 320));
		if (JOptionPane.showConfirmDialog(this, p, "Add attachment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION)
		{
			return;
		}
		for (HelperConstructManager.StepAttachmentPickOption o : list.getSelectedValuesList())
		{
			if (o == null || o.getEdit() == null)
			{
				continue;
			}
			HelperConstructManager.StepAttachmentEdit add = HelperConstructManager.StepAttachmentEdit.copyOf(o.getEdit());
			if (add != null)
			{
				model.addElement(add);
			}
		}
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

	private void styleStepLibraryTable(JTable table, ConstructStepKind stepKind)
	{
		int attachCol = stepLibAttachmentsColumn(stepKind);
		int removeCol = stepLibRemoveColumn(stepKind);
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
					boolean clickable = column == attachCol || column == removeCol;
					c.setForeground(clickable ? new Color(100, 180, 255) : Color.WHITE);
				}
				return c;
			}
		});
		int n = table.getColumnModel().getColumnCount();
		if (n >= 5)
		{
			table.getColumnModel().getColumn(0).setPreferredWidth(200);
			if (n >= 6)
			{
				table.getColumnModel().getColumn(1).setPreferredWidth(72);
				table.getColumnModel().getColumn(2).setPreferredWidth(110);
				table.getColumnModel().getColumn(3).setPreferredWidth(200);
				table.getColumnModel().getColumn(4).setPreferredWidth(200);
				table.getColumnModel().getColumn(5).setPreferredWidth(56);
			}
			else
			{
				table.getColumnModel().getColumn(1).setPreferredWidth(110);
				table.getColumnModel().getColumn(2).setPreferredWidth(220);
				table.getColumnModel().getColumn(3).setPreferredWidth(200);
				table.getColumnModel().getColumn(4).setPreferredWidth(56);
			}
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
		private final ConstructStepKind kind;
		private final String[] columns;
		private List<String> summaries = new ArrayList<>();
		private List<String> rawIdTexts = new ArrayList<>();
		private List<String> worldPoints = new ArrayList<>();
		private List<String> requirementDisplays = new ArrayList<>();
		private List<String> instructions = new ArrayList<>();

		private StepLibraryTableModel(ConstructStepKind kind)
		{
			this.kind = kind;
			this.columns = kind == ConstructStepKind.TEXT
				? new String[]{"Name/Var", "World (x, y, plane)", "Attachments", "Instruction", "Remove"}
				: new String[]{"Name/Var", "Id to use", "World (x, y, plane)", "Attachments", "Instruction", "Remove"};
		}

		void setRows(
			List<String> updatedSummaries,
			List<String> updatedRawIdTexts,
			List<String> updatedWorldPoints,
			List<String> updatedRequirementDisplays,
			List<String> updatedInstructions)
		{
			summaries = new ArrayList<>(updatedSummaries);
			rawIdTexts = new ArrayList<>(updatedRawIdTexts);
			worldPoints = new ArrayList<>(updatedWorldPoints);
			requirementDisplays = new ArrayList<>(updatedRequirementDisplays);
			instructions = new ArrayList<>(updatedInstructions);
			int n = summaries.size();
			padToSize(rawIdTexts, n);
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
			if (kind == ConstructStepKind.TEXT)
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
			switch (columnIndex)
			{
				case 0:
					return summaries.get(rowIndex);
				case 1:
					return rowIndex < rawIdTexts.size() ? rawIdTexts.get(rowIndex) : "";
				case 2:
					return rowIndex < worldPoints.size() ? worldPoints.get(rowIndex) : "";
				case 3:
					return rowIndex < requirementDisplays.size() ? requirementDisplays.get(rowIndex) : "";
				case 4:
					return rowIndex < instructions.size() ? instructions.get(rowIndex) : "";
				case 5:
				default:
					return summaries.isEmpty() ? "" : "Remove";
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			if (kind == ConstructStepKind.TEXT)
			{
				return columnIndex == 1 || columnIndex == 3;
			}
			return columnIndex == 1 || columnIndex == 2 || columnIndex == 4;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			if (rowIndex < 0 || rowIndex >= summaries.size() || !isCellEditable(rowIndex, columnIndex))
			{
				return;
			}
			String text = aValue == null ? "" : String.valueOf(aValue);
			boolean ok = applyStepLibraryEdit(kind, rowIndex, columnIndex, text);
			if (!ok)
			{
				return;
			}
			if (kind == ConstructStepKind.TEXT)
			{
				if (columnIndex == 1)
				{
					worldPoints.set(rowIndex, text);
				}
				else
				{
					instructions.set(rowIndex, text);
				}
			}
			else if (columnIndex == 1)
			{
				rawIdTexts.set(rowIndex, text);
			}
			else if (columnIndex == 2)
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

	private boolean applyStepLibraryEdit(ConstructStepKind kind, int row, int column, String text)
	{
		if (kind == ConstructStepKind.TEXT)
		{
			switch (column)
			{
				case 1:
					return helperConstructManager.updateStepWorldPointAt(kind, row, text);
				case 3:
					return helperConstructManager.updateStepInstructionAt(kind, row, text);
				default:
					return false;
			}
		}
		switch (column)
		{
			case 1:
				return helperConstructManager.updateStepRawIdAt(kind, row, text);
			case 2:
				return helperConstructManager.updateStepWorldPointAt(kind, row, text);
			case 4:
				return helperConstructManager.updateStepInstructionAt(kind, row, text);
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
				"Capture NPC, object, or generic steps in the maker tabs first, then add them to the quest order here.",
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
		refreshStepLibraryTable(npcLibraryModel, ConstructStepKind.NPC);
		refreshStepLibraryTable(objectLibraryModel, ConstructStepKind.OBJECT);
		refreshStepLibraryTable(genericLibraryModel, ConstructStepKind.TEXT);
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
		StringBuilder sb = new StringBuilder(helperConstructManager.getCurrentDraftClassName());
		for (ConstructStepKind k : ConstructStepKind.values())
		{
			String p;
			switch (k)
			{
				case NPC:
					p = "N";
					break;
				case OBJECT:
					p = "O";
					break;
				case TEXT:
					p = "T";
					break;
				default:
					p = "N";
					break;
			}
			sb.append('|').append(p).append('|').append(String.join("\n", helperConstructManager.getStepVarNames(k)));
			sb.append('|').append(p).append('r').append('|').append(String.join("\n", helperConstructManager.getStepRawIdTexts(k)));
			sb.append('|').append(p).append('w').append('|').append(String.join("\n", helperConstructManager.getStepWorldPointTexts(k)));
			sb.append('|').append(p).append("rq|").append(String.join("\n", helperConstructManager.getStepRequirementsDisplays(k)));
			sb.append('|').append(p).append('i').append('|').append(String.join("\n", helperConstructManager.getStepInstructionTexts(k)));
		}
		sb.append("|A|").append(stepOrderTableModel.signature());
		sb.append("|R|").append(String.join("\n", helperConstructManager.getRequirementSummaries()));
		sb.append("|V|").append(varbitRoutingTableModel.signature());
		return sb.toString();
	}

	private void refreshStepLibraryTable(StepLibraryTableModel model, ConstructStepKind kind)
	{
		model.setRows(
			helperConstructManager.getStepVarNames(kind),
			helperConstructManager.getStepRawIdTexts(kind),
			helperConstructManager.getStepWorldPointTexts(kind),
			helperConstructManager.getStepRequirementsDisplays(kind),
			helperConstructManager.getStepInstructionTexts(kind));
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
